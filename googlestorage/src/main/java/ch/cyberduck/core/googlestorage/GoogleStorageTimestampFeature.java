package ch.cyberduck.core.googlestorage;

/*
 * Copyright (c) 2002-2019 iterate GmbH. All rights reserved.
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

import ch.cyberduck.core.Path;
import ch.cyberduck.core.PathContainerService;
import ch.cyberduck.core.exception.BackgroundException;
import ch.cyberduck.core.exception.NotfoundException;
import ch.cyberduck.core.shared.DefaultTimestampFeature;
import ch.cyberduck.core.transfer.TransferStatus;

import org.apache.log4j.Logger;

import java.io.IOException;

import com.google.api.client.util.DateTime;
import com.google.api.services.storage.model.StorageObject;

public class GoogleStorageTimestampFeature extends DefaultTimestampFeature {
    private static final Logger log = Logger.getLogger(GoogleStorageTimestampFeature.class);

    private final GoogleStorageSession session;
    private final PathContainerService containerService;

    public GoogleStorageTimestampFeature(final GoogleStorageSession session) {
        this.session = session;
        this.containerService = session.getFeature(PathContainerService.class);
    }

    @Override
    public void setTimestamp(final Path file, final TransferStatus status) throws BackgroundException {
        if(file.isFile() || file.isPlaceholder()) {
            try {
                // The Custom-Time metadata is a user-specified date and time represented in the RFC 3339
                // format YYYY-MM-DD'T'HH:MM:SS.SS'Z' or YYYY-MM-DD'T'HH:MM:SS'Z' when milliseconds are zero.
                session.getClient().objects().patch(containerService.getContainer(file).getName(), containerService.getKey(file),
                    new StorageObject().setCustomTime(new DateTime(status.getTimestamp()))).execute();
            }
            catch(IOException e) {
                final BackgroundException failure = new GoogleStorageExceptionMappingService().map("Failure to write attributes of {0}", e, file);
                if(file.isPlaceholder()) {
                    if(failure instanceof NotfoundException) {
                        // No placeholder file may exist but we just have a common prefix
                        return;
                    }
                }
                throw failure;
            }
        }
    }
}
