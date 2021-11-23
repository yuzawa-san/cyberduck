package ch.cyberduck.core.sds;

/*
 * Copyright (c) 2002-2017 iterate GmbH. All rights reserved.
 * https://cyberduck.io/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

import ch.cyberduck.core.ConnectionCallback;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.exception.BackgroundException;
import ch.cyberduck.core.exception.ConflictException;
import ch.cyberduck.core.features.MultipartWrite;
import ch.cyberduck.core.http.HttpResponseOutputStream;
import ch.cyberduck.core.io.MemorySegementingOutputStream;
import ch.cyberduck.core.preferences.PreferencesFactory;
import ch.cyberduck.core.sds.io.swagger.client.model.CreateFileUploadResponse;
import ch.cyberduck.core.sds.io.swagger.client.model.Node;
import ch.cyberduck.core.transfer.TransferStatus;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class SDSMultipartWriteFeature implements MultipartWrite<Node> {
    private static final Logger log = Logger.getLogger(SDSMultipartWriteFeature.class);

    private final SDSSession session;
    private final SDSNodeIdProvider nodeid;
    private final SDSUploadService upload;

    public SDSMultipartWriteFeature(final SDSSession session, final SDSNodeIdProvider nodeid) {
        this.session = session;
        this.nodeid = nodeid;
        this.upload = new SDSUploadService(session, nodeid);
    }

    @Override
    public HttpResponseOutputStream<Node> write(final Path file, final TransferStatus status, final ConnectionCallback callback) throws BackgroundException {
        final CreateFileUploadResponse uploadResponse = upload.start(file, status);
        final String uploadUrl = uploadResponse.getUploadUrl();
        final String uploadToken;
        if(StringUtils.isBlank(uploadResponse.getToken())) {
            uploadToken = StringUtils.substringAfterLast(uploadUrl, Path.DELIMITER);
            log.warn(String.format("Parsed upload token %s from URL %s", uploadToken, uploadUrl));
        }
        else {
            uploadToken = uploadResponse.getToken();
        }
        final MultipartUploadTokenOutputStream proxy = new MultipartUploadTokenOutputStream(session, nodeid, file, status, uploadUrl);
        return new HttpResponseOutputStream<Node>(new MemorySegementingOutputStream(proxy, PreferencesFactory.get().getInteger("sds.upload.multipart.chunksize"))) {
            private final AtomicBoolean close = new AtomicBoolean();
            private final AtomicReference<Node> node = new AtomicReference<>();

            @Override
            public Node getStatus() {
                return node.get();
            }

            @Override
            public void close() throws IOException {
                try {
                    if(close.get()) {
                        log.warn(String.format("Skip double close of stream %s", this));
                        return;
                    }
                    super.close();
                    try {
                        node.set(upload.complete(file, uploadToken, status));
                    }
                    catch(ConflictException e) {
                        node.set(upload.complete(file, uploadToken, new TransferStatus(status).exists(true)));
                    }
                }
                catch(BackgroundException e) {
                    throw new IOException(e);
                }
                finally {
                    close.set(true);
                }
            }


            @Override
            protected void handleIOException(final IOException e) throws IOException {
                // Cancel upload on error reply
                try {
                    upload.cancel(file, uploadToken);
                }
                catch(BackgroundException f) {
                    log.warn(String.format("Failure %s cancelling upload for file %s with upload token %s after failure %s", f, file, uploadToken, e));
                }
                throw e;
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

    @Override
    public boolean timestamp() {
        return true;
    }
}
