/*
 * Copyright (c) 2002-2016 iterate GmbH. All rights reserved.
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

package ch.cyberduck.core.openstack;

import ch.cyberduck.core.ConnectionCallback;
import ch.cyberduck.core.DefaultIOExceptionMappingService;
import ch.cyberduck.core.DefaultPathContainerService;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.PathContainerService;
import ch.cyberduck.core.exception.BackgroundException;
import ch.cyberduck.core.features.MultipartWrite;
import ch.cyberduck.core.http.HttpResponseOutputStream;
import ch.cyberduck.core.io.HashAlgorithm;
import ch.cyberduck.core.io.MemorySegementingOutputStream;
import ch.cyberduck.core.preferences.HostPreferences;
import ch.cyberduck.core.threading.BackgroundExceptionCallable;
import ch.cyberduck.core.threading.DefaultRetryCallable;
import ch.cyberduck.core.transfer.TransferStatus;

import org.apache.http.entity.ByteArrayEntity;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import ch.iterate.openstack.swift.exception.GenericException;
import ch.iterate.openstack.swift.model.StorageObject;

public class SwiftLargeUploadWriteFeature implements MultipartWrite<List<StorageObject>> {
    private static final Logger log = Logger.getLogger(SwiftLargeUploadWriteFeature.class);

    private final PathContainerService containerService = new DefaultPathContainerService();
    private final SwiftSession session;
    private final SwiftSegmentService segmentService;
    private final SwiftRegionService regionService;

    public SwiftLargeUploadWriteFeature(final SwiftSession session) {
        this(session, new SwiftRegionService(session));
    }

    public SwiftLargeUploadWriteFeature(final SwiftSession session, final SwiftRegionService regionService) {
        this(session, regionService, new SwiftSegmentService(session, regionService));
    }

    public SwiftLargeUploadWriteFeature(final SwiftSession session, final SwiftRegionService regionService, final SwiftSegmentService segmentService) {
        this.session = session;
        this.regionService = regionService;
        this.segmentService = segmentService;
    }

    @Override
    public HttpResponseOutputStream<List<StorageObject>> write(final Path file, final TransferStatus status, final ConnectionCallback callback) {
        final LargeUploadOutputStream proxy = new LargeUploadOutputStream(file, status);
        return new HttpResponseOutputStream<List<StorageObject>>(new MemorySegementingOutputStream(proxy,
            new HostPreferences(session.getHost()).getInteger("openstack.upload.largeobject.size.minimum"))) {
            @Override
            public List<StorageObject> getStatus() {
                return proxy.getCompleted();
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

    private final class LargeUploadOutputStream extends OutputStream {
        private final List<StorageObject> completed = new ArrayList<>();
        private final Path file;
        private final TransferStatus overall;
        private final AtomicBoolean close = new AtomicBoolean();
        private int segmentNumber;

        public LargeUploadOutputStream(final Path file, final TransferStatus status) {
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
                completed.add(new DefaultRetryCallable<>(session.getHost(), new BackgroundExceptionCallable<StorageObject>() {
                    @Override
                    public StorageObject call() throws BackgroundException {
                        final TransferStatus status = new TransferStatus().withLength(len);
                        status.setChecksum(SwiftLargeUploadWriteFeature.this.checksum(file, status)
                            .compute(new ByteArrayInputStream(content, off, len), status)
                        );
                        // Segment name with left padded segment number
                        final Path segment = segmentService.getSegment(file, ++segmentNumber);
                        final ByteArrayEntity entity = new ByteArrayEntity(content, off, len);
                        final HashMap<String, String> headers = new HashMap<>();
                        final String checksum;
                        try {
                            checksum = session.getClient().storeObject(
                                regionService.lookup(file),
                                containerService.getContainer(segment).getName(), containerService.getKey(segment),
                                entity, headers,
                                status.getChecksum().algorithm == HashAlgorithm.md5 ? status.getChecksum().hash : null);
                        }
                        catch(GenericException e) {
                            throw new SwiftExceptionMappingService().map("Upload {0} failed", e, file);
                        }
                        catch(IOException e) {
                            throw new DefaultIOExceptionMappingService().map("Upload {0} failed", e, file);
                        }
                        if(log.isDebugEnabled()) {
                            log.debug(String.format("Saved segment %s with checksum %s", segment, checksum));
                        }
                        final StorageObject stored = new StorageObject(containerService.getKey(segment));
                        stored.setMd5sum(checksum);
                        stored.setSize(status.getLength());
                        return stored;
                    }
                }, overall).call());
            }
            catch(BackgroundException e) {
                throw new IOException(e.getMessage(), e);
            }
        }

        @Override
        public void close() throws IOException {
            // Create and upload the large object manifest. It is best to upload all the segments first and
            // then create or update the manifest.
            try {
                if(close.get()) {
                    log.warn(String.format("Skip double close of stream %s", this));
                    return;
                }
                if(completed.isEmpty()) {
                    new SwiftTouchFeature(session, regionService).touch(file, new TransferStatus());
                }
                else {
                    // Static Large Object
                    final String manifest = segmentService.manifest(containerService.getContainer(file).getName(), completed);
                    if(log.isDebugEnabled()) {
                        log.debug(String.format("Creating SLO manifest %s for %s", manifest, file));
                    }
                    session.getClient().createSLOManifestObject(regionService.lookup(
                        containerService.getContainer(file)),
                        containerService.getContainer(file).getName(),
                        overall.getMime(),
                        containerService.getKey(file), manifest, Collections.emptyMap());
                }
            }
            catch(BackgroundException e) {
                throw new IOException(e.getMessage(), e);
            }
            finally {
                close.set(true);
            }
        }

        public List<StorageObject> getCompleted() {
            return completed;
        }
    }
}
