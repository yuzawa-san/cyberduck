package ch.cyberduck.core.dropbox;

/*
 * Copyright (c) 2002-2016 iterate GmbH. All rights reserved.
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

import ch.cyberduck.core.ListProgressListener;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.PathAttributes;
import ch.cyberduck.core.PathContainerService;
import ch.cyberduck.core.exception.BackgroundException;
import ch.cyberduck.core.features.AttributesFinder;

import org.apache.log4j.Logger;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.DbxUserFilesRequests;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.users.DbxUserUsersRequests;
import com.dropbox.core.v2.users.FullAccount;

public class DropboxAttributesFinderFeature implements AttributesFinder {
    private static final Logger log = Logger.getLogger(DropboxAttributesFinderFeature.class);

    private final DropboxSession session;
    private final PathContainerService containerService;

    public DropboxAttributesFinderFeature(final DropboxSession session) {
        this.session = session;
        this.containerService = new DropboxPathContainerService(session);
    }

    @Override
    public PathAttributes find(final Path file, final ListProgressListener listener) throws BackgroundException {
        try {
            // Metadata for the root folder is unsupported
            if(file.isRoot()) {
                // Retrieve he namespace ID for a users home folder and team root folder
                final FullAccount account = new DbxUserUsersRequests(session.getClient()).getCurrentAccount();
                if(log.isDebugEnabled()) {
                    log.debug(String.format("Set root namespace %s", account.getRootInfo().getRootNamespaceId()));
                }
                return new PathAttributes().withFileId(account.getRootInfo().getRootNamespaceId());
            }
            final Metadata metadata = new DbxUserFilesRequests(session.getClient(file)).getMetadata(containerService.getKey(file));
            return this.toAttributes(metadata);
        }
        catch(DbxException e) {
            throw new DropboxExceptionMappingService().map("Failure to read attributes of {0}", e, file);
        }
    }

    protected PathAttributes toAttributes(final Metadata metadata) {
        final PathAttributes attributes = new PathAttributes();
        if(metadata instanceof FileMetadata) {
            final FileMetadata file = (FileMetadata) metadata;
            attributes.setSize(file.getSize());
            attributes.setModificationDate(file.getClientModified().getTime());
            if(file.getFileLockInfo() != null) {
                attributes.setLockId(String.valueOf(file.getFileLockInfo().getIsLockholder()));
            }
        }
        if(metadata instanceof FolderMetadata) {
            final FolderMetadata folder = (FolderMetadata) metadata;
            // All shared folders have a shared_folder_id. This value is identical to the namespace ID for that shared folder
            attributes.setFileId(folder.getSharedFolderId());
        }
        return attributes;
    }
}
