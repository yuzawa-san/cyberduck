package ch.cyberduck.core.s3;

import ch.cyberduck.core.ConnectionCallback;
import ch.cyberduck.core.LocaleFactory;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.PathContainerService;
import ch.cyberduck.core.exception.BackgroundException;
import ch.cyberduck.core.exception.ChecksumException;
import ch.cyberduck.core.features.MultipartWrite;
import ch.cyberduck.core.http.HttpResponseOutputStream;
import ch.cyberduck.core.io.ChecksumComputeFactory;
import ch.cyberduck.core.io.HashAlgorithm;
import ch.cyberduck.core.io.MemorySegementingOutputStream;
import ch.cyberduck.core.preferences.HostPreferences;
import ch.cyberduck.core.threading.BackgroundExceptionCallable;
import ch.cyberduck.core.threading.DefaultRetryCallable;
import ch.cyberduck.core.transfer.TransferStatus;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.log4j.Logger;
import org.jets3t.service.ServiceException;
import org.jets3t.service.model.MultipartCompleted;
import org.jets3t.service.model.MultipartPart;
import org.jets3t.service.model.MultipartUpload;
import org.jets3t.service.model.S3Object;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class S3MultipartWriteFeature implements MultipartWrite<MultipartUpload> {
    private static final Logger log = Logger.getLogger(S3MultipartWriteFeature.class);

    private final PathContainerService containerService;
    private final S3Session session;

    public S3MultipartWriteFeature(final S3Session session) {
        this.session = session;
        this.containerService = session.getFeature(PathContainerService.class);
    }

    @Override
    public HttpResponseOutputStream<MultipartUpload> write(final Path file, final TransferStatus status, final ConnectionCallback callback) throws BackgroundException {
        final S3Object object = new S3WriteFeature(session)
            .getDetails(file, status);
        // ID for the initiated multipart upload.
        final MultipartUpload multipart;
        try {
            multipart = session.getClient().multipartStartUpload(
                containerService.getContainer(file).getName(), object);
            if(log.isDebugEnabled()) {
                log.debug(String.format("Multipart upload started for %s with ID %s",
                    multipart.getObjectKey(), multipart.getUploadId()));
            }
        }
        catch(ServiceException e) {
            throw new S3ExceptionMappingService().map("Upload {0} failed", e, file);
        }
        final MultipartOutputStream proxy = new MultipartOutputStream(multipart, file, status);
        return new HttpResponseOutputStream<MultipartUpload>(new MemorySegementingOutputStream(proxy,
            new HostPreferences(session.getHost()).getInteger("s3.upload.multipart.size"))) {
            @Override
            public MultipartUpload getStatus() {
                return multipart;
            }
        };
    }

    @Override
    public Append append(final Path file, final TransferStatus status) throws BackgroundException {
        return new Append(false).withStatus(status);
    }

    @Override
    public boolean temporary() {
        return false;
    }

    private final class MultipartOutputStream extends OutputStream {
        /**
         * Completed parts
         */
        private final List<MultipartPart> completed
            = new ArrayList<>();

        private final MultipartUpload multipart;
        private final Path file;
        private final TransferStatus overall;
        private final AtomicBoolean close = new AtomicBoolean();
        private int partNumber;

        public MultipartOutputStream(final MultipartUpload multipart, final Path file, final TransferStatus status) {
            this.multipart = multipart;
            this.file = file;
            this.overall = status;
        }

        @Override
        public void write(final int value) throws IOException {
            throw new IOException(new UnsupportedOperationException());
        }

        @Override
        public void write(final byte[] content, final int off, final int len) throws IOException {
            try {
                completed.add(new DefaultRetryCallable<>(session.getHost(), new BackgroundExceptionCallable<MultipartPart>() {
                    @Override
                    public MultipartPart call() throws BackgroundException {
                        final Map<String, String> parameters = new HashMap<>();
                        parameters.put("uploadId", multipart.getUploadId());
                        parameters.put("partNumber", String.valueOf(++partNumber));
                        final TransferStatus status = new TransferStatus().withParameters(parameters).withLength(len);
                        switch(session.getSignatureVersion()) {
                            case AWS4HMACSHA256:
                                status.setChecksum(ChecksumComputeFactory.get(HashAlgorithm.sha256)
                                    .compute(new ByteArrayInputStream(content, off, len), status)
                                );
                                break;
                        }
                        status.setSegment(true);
                        final S3Object part = new S3WriteFeature(session).getDetails(file, status);
                        try {
                            session.getClient().putObjectWithRequestEntityImpl(
                                containerService.getContainer(file).getName(), part,
                                new ByteArrayEntity(content, off, len), parameters);
                        }
                        catch(ServiceException e) {
                            throw new S3ExceptionMappingService().map("Upload {0} failed", e, file);
                        }
                        if(log.isDebugEnabled()) {
                            log.debug(String.format("Saved object %s with checksum %s", file, part.getETag()));
                        }
                        return new MultipartPart(partNumber,
                            null == part.getLastModifiedDate() ? new Date(System.currentTimeMillis()) : part.getLastModifiedDate(),
                            null == part.getETag() ? StringUtils.EMPTY : part.getETag(),
                            part.getContentLength());
                    }
                }, overall).call());
            }
            catch(BackgroundException e) {
                throw new IOException(e.getMessage(), e);
            }
        }

        @Override
        public void close() throws IOException {
            try {
                if(close.get()) {
                    log.warn(String.format("Skip double close of stream %s", this));
                    return;
                }
                if(completed.isEmpty()) {
                    log.warn(String.format("Abort multipart upload %s with no completed parts", multipart));
                    session.getClient().multipartAbortUpload(multipart);
                    new S3TouchFeature(session).touch(file, new TransferStatus());
                }
                else {
                    final MultipartCompleted complete = session.getClient().multipartCompleteUpload(multipart, completed);
                    if(log.isDebugEnabled()) {
                        log.debug(String.format("Completed multipart upload for %s with checksum %s",
                            complete.getObjectKey(), complete.getEtag()));
                    }
                    file.attributes().setVersionId(complete.getVersionId());
                    if(file.getType().contains(Path.Type.encrypted)) {
                        log.warn(String.format("Skip checksum verification for %s with client side encryption enabled", file));
                    }
                    else {
                        if(S3Session.isAwsHostname(session.getHost().getHostname())) {
                            final StringBuilder concat = new StringBuilder();
                            for(MultipartPart part : completed) {
                                concat.append(part.getEtag());
                            }
                            final String expected = String.format("%s-%d",
                                    ChecksumComputeFactory.get(HashAlgorithm.md5).compute(concat.toString(), new TransferStatus()), completed.size());
                            final String reference = StringUtils.removeEnd(StringUtils.removeStart(complete.getEtag(), "\""), "\"");
                            if(!StringUtils.equalsIgnoreCase(expected, reference)) {
                                throw new ChecksumException(MessageFormat.format(LocaleFactory.localizedString("Upload {0} failed", "Error"), file.getName()),
                                    MessageFormat.format("Mismatch between MD5 hash {0} of uploaded data and ETag {1} returned by the server",
                                        expected, reference));
                            }
                        }
                    }
                }
            }
            catch(BackgroundException e) {
                throw new IOException(e);
            }
            catch(ServiceException e) {
                throw new IOException(e.getErrorMessage(), new S3ExceptionMappingService().map(e));
            }
            finally {
                close.set(true);
            }
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("MultipartOutputStream{");
            sb.append("multipart=").append(multipart);
            sb.append(", file=").append(file);
            sb.append('}');
            return sb.toString();
        }
    }

    @Override
    public boolean timestamp() {
        return true;
    }
}
