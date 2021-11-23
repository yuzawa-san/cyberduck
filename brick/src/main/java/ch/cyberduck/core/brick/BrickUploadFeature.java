package ch.cyberduck.core.brick;

/*
 * Copyright (c) 2002-2020 iterate GmbH. All rights reserved.
 * https://cyberduck.io/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

import ch.cyberduck.core.BytecountStreamListener;
import ch.cyberduck.core.ConnectionCallback;
import ch.cyberduck.core.Local;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.brick.io.swagger.client.ApiException;
import ch.cyberduck.core.brick.io.swagger.client.api.FileActionsApi;
import ch.cyberduck.core.brick.io.swagger.client.api.FilesApi;
import ch.cyberduck.core.brick.io.swagger.client.model.BeginUploadPathBody;
import ch.cyberduck.core.brick.io.swagger.client.model.FileEntity;
import ch.cyberduck.core.brick.io.swagger.client.model.FileUploadPartEntity;
import ch.cyberduck.core.brick.io.swagger.client.model.FilesPathBody;
import ch.cyberduck.core.exception.BackgroundException;
import ch.cyberduck.core.exception.ConnectionCanceledException;
import ch.cyberduck.core.exception.NotfoundException;
import ch.cyberduck.core.http.HttpUploadFeature;
import ch.cyberduck.core.io.BandwidthThrottle;
import ch.cyberduck.core.io.StreamListener;
import ch.cyberduck.core.preferences.PreferencesFactory;
import ch.cyberduck.core.threading.BackgroundExceptionCallable;
import ch.cyberduck.core.threading.ThreadPool;
import ch.cyberduck.core.threading.ThreadPoolFactory;
import ch.cyberduck.core.transfer.SegmentRetryCallable;
import ch.cyberduck.core.transfer.TransferStatus;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class BrickUploadFeature extends HttpUploadFeature<Void, MessageDigest> {
    private static final Logger log = Logger.getLogger(BrickUploadFeature.class);

    /**
     * The maximum allowed parts in a multipart upload.
     */
    public static final int MAXIMUM_UPLOAD_PARTS = 10000;

    private final BrickSession session;

    private final BrickWriteFeature writer;
    private final Long partsize;
    private final Integer concurrency;

    public BrickUploadFeature(final BrickSession session, final BrickWriteFeature writer) {
        this(session, writer, PreferencesFactory.get().getLong("brick.upload.multipart.size"),
            PreferencesFactory.get().getInteger("brick.upload.multipart.concurrency"));
    }

    public BrickUploadFeature(final BrickSession session, final BrickWriteFeature writer, final Long partsize, final Integer concurrency) {
        super(writer);
        this.session = session;
        this.writer = writer;
        this.partsize = partsize;
        this.concurrency = concurrency;
    }

    @Override
    public Void upload(final Path file, final Local local, final BandwidthThrottle throttle, final StreamListener listener,
                       final TransferStatus status, final ConnectionCallback callback) throws BackgroundException {
        final ThreadPool pool = ThreadPoolFactory.get("multipart", concurrency);
        try {
            // Full size of file
            final long size = status.getLength() + status.getOffset();
            final List<Future<TransferStatus>> parts = new ArrayList<>();
            final List<TransferStatus> checksums = new ArrayList<>();
            long offset = 0;
            long remaining = status.getLength();
            String ref = null;
            for(int partNumber = 1; remaining > 0; partNumber++) {
                final FileUploadPartEntity uploadPartEntity = this.continueUpload(file, ref, partNumber);
                final long length;
                if(uploadPartEntity.isParallelParts()) {
                    length = Math.min(Math.max(size / (MAXIMUM_UPLOAD_PARTS - 1), partsize), remaining);
                }
                else {
                    length = remaining;
                }
                parts.add(this.submit(pool, file, local, throttle, listener, status,
                    uploadPartEntity.getUploadUri(), partNumber, offset, length, callback));
                remaining -= length;
                offset += length;
                ref = uploadPartEntity.getRef();
            }
            for(Future<TransferStatus> future : parts) {
                try {
                    checksums.add(future.get());
                }
                catch(InterruptedException e) {
                    log.error("Part upload failed with interrupt failure");
                    status.setCanceled();
                    throw new ConnectionCanceledException(e);
                }
                catch(ExecutionException e) {
                    log.warn(String.format("Part upload failed with execution failure %s", e.getMessage()));
                    if(e.getCause() instanceof BackgroundException) {
                        throw (BackgroundException) e.getCause();
                    }
                    throw new BackgroundException(e.getCause());
                }
            }
            this.completeUpload(file, ref, status, checksums);
            // Mark parent status as complete
            status.setComplete();
            return null;
        }
        finally {
            // Cancel future tasks
            pool.shutdown(false);
        }
    }

    protected FileUploadPartEntity startUpload(final Path file) throws BackgroundException {
        return this.continueUpload(file, null, 1);
    }

    protected FileUploadPartEntity continueUpload(final Path file, final String ref, final int partNumber) throws BackgroundException {
        final List<FileUploadPartEntity> uploadPartEntities;
        try {
            uploadPartEntities = new FileActionsApi(new BrickApiClient(session))
                .beginUpload(StringUtils.removeStart(file.getAbsolute(), String.valueOf(Path.DELIMITER)), new BeginUploadPathBody().ref(ref).part(partNumber));
        }
        catch(ApiException e) {
            throw new BrickExceptionMappingService().map("Upload {0} failed", e, file);
        }
        final Optional<FileUploadPartEntity> entity = uploadPartEntities.stream().findFirst();
        if(!entity.isPresent()) {
            throw new NotfoundException(file.getAbsolute());
        }
        return entity.get();
    }

    protected FileEntity completeUpload(final Path file, final String ref, final TransferStatus status, final List<TransferStatus> checksums) throws BackgroundException {
        try {
            return new FilesApi(new BrickApiClient(session)).postFilesPath(new FilesPathBody()
                .etagsEtag(checksums.stream().map(s -> s.getChecksum().hash).collect(Collectors.toList()))
                .etagsPart(checksums.stream().map(TransferStatus::getPart).collect(Collectors.toList()))
                .providedMtime(null != status.getTimestamp() ? new DateTime(status.getTimestamp()) : null)
                .ref(ref)
                .action("end"), StringUtils.removeStart(file.getAbsolute(), String.valueOf(Path.DELIMITER)));
        }
        catch(ApiException e) {
            throw new BrickExceptionMappingService().map("Upload {0} failed", e, file);
        }
    }

    private Future<TransferStatus> submit(final ThreadPool pool, final Path file, final Local local,
                                          final BandwidthThrottle throttle, final StreamListener listener,
                                          final TransferStatus overall, final String url, final Integer partNumber,
                                          final long offset, final long length, final ConnectionCallback callback) {
        if(log.isInfoEnabled()) {
            log.info(String.format("Submit part %d of %s to queue with offset %d and length %d", partNumber, file, offset, length));
        }
        final BytecountStreamListener counter = new BytecountStreamListener(listener);
        return pool.execute(new SegmentRetryCallable<>(session.getHost(), new BackgroundExceptionCallable<TransferStatus>() {
            @Override
            public TransferStatus call() throws BackgroundException {
                overall.validate();
                final TransferStatus status = new TransferStatus()
                    .segment(true)
                    .withLength(length)
                    .withOffset(offset);
                status.setChecksum(writer.checksum(file, status).compute(local.getInputStream(), status));
                status.setUrl(url);
                status.setPart(partNumber);
                status.setHeader(overall.getHeader());
                BrickUploadFeature.super.upload(
                    file, local, throttle, listener, status, overall, status, callback);
                if(log.isInfoEnabled()) {
                    log.info(String.format("Received response for part number %d", partNumber));
                }
                return status;
            }
        }, overall, counter));
    }
}
