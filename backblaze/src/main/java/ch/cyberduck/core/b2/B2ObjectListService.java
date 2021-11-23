package ch.cyberduck.core.b2;

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

import ch.cyberduck.core.AttributedList;
import ch.cyberduck.core.DefaultIOExceptionMappingService;
import ch.cyberduck.core.DefaultPathContainerService;
import ch.cyberduck.core.DisabledListProgressListener;
import ch.cyberduck.core.ListProgressListener;
import ch.cyberduck.core.ListService;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.PathAttributes;
import ch.cyberduck.core.PathContainerService;
import ch.cyberduck.core.PathNormalizer;
import ch.cyberduck.core.exception.BackgroundException;
import ch.cyberduck.core.exception.NotfoundException;
import ch.cyberduck.core.io.Checksum;
import ch.cyberduck.core.preferences.HostPreferences;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import synapticloop.b2.Action;
import synapticloop.b2.exception.B2ApiException;
import synapticloop.b2.response.B2FileInfoResponse;
import synapticloop.b2.response.B2ListFilesResponse;

import static ch.cyberduck.core.b2.B2MetadataFeature.X_BZ_INFO_SRC_LAST_MODIFIED_MILLIS;

public class B2ObjectListService implements ListService {
    private static final Logger log = Logger.getLogger(B2ObjectListService.class);

    private final PathContainerService containerService
        = new DefaultPathContainerService();

    private final B2Session session;

    private final int chunksize;
    private final B2VersionIdProvider fileid;

    public B2ObjectListService(final B2Session session, final B2VersionIdProvider fileid) {
        this(session, fileid, new HostPreferences(session.getHost()).getInteger("b2.listing.chunksize"));
    }

    public B2ObjectListService(final B2Session session, final B2VersionIdProvider fileid, final int chunksize) {
        this.session = session;
        this.fileid = fileid;
        this.chunksize = chunksize;
    }

    @Override
    public AttributedList<Path> list(final Path directory, final ListProgressListener listener) throws BackgroundException {
        try {
            final AttributedList<Path> objects = new AttributedList<>();
            Marker marker;
            if(containerService.isContainer(directory)) {
                marker = new Marker(null, null);
            }
            else {
                marker = new Marker(String.format("%s%s", containerService.getKey(directory), Path.DELIMITER), null);
            }
            final String containerId = fileid.getVersionId(containerService.getContainer(directory), new DisabledListProgressListener());
            // Seen placeholders
            final Map<String, Long> revisions = new HashMap<>();
            boolean hasDirectoryPlaceholder = containerService.isContainer(directory);
            do {
                if(log.isDebugEnabled()) {
                    log.debug(String.format("List directory %s with marker %s", directory, marker));
                }
                // In alphabetical order by file name, and by reverse of date/time uploaded for
                // versions of files with the same name.
                final B2ListFilesResponse response = session.getClient().listFileVersions(
                    containerId,
                    marker.nextFilename, marker.nextFileId, chunksize,
                    containerService.isContainer(directory) ? null : String.format("%s%s", containerService.getKey(directory), Path.DELIMITER),
                    String.valueOf(Path.DELIMITER));
                marker = this.parse(directory, objects, response, revisions);
                if(null == marker.nextFileId) {
                    if(!response.getFiles().isEmpty()) {
                        hasDirectoryPlaceholder = true;
                    }
                }
                listener.chunk(directory, objects);
            }
            while(marker.hasNext());
            if(!hasDirectoryPlaceholder && objects.isEmpty()) {
                throw new NotfoundException(directory.getAbsolute());
            }
            return objects;
        }
        catch(B2ApiException e) {
            throw new B2ExceptionMappingService(fileid).map("Listing directory {0} failed", e, directory);
        }
        catch(IOException e) {
            throw new DefaultIOExceptionMappingService().map(e);
        }
    }

    protected Marker parse(final Path directory, final AttributedList<Path> objects,
                           final B2ListFilesResponse response, final Map<String, Long> revisions) {
        for(B2FileInfoResponse info : response.getFiles()) {
            if(StringUtils.equals(PathNormalizer.name(info.getFileName()), B2PathContainerService.PLACEHOLDER)) {
                continue;
            }
            if(StringUtils.isBlank(info.getFileId())) {
                // Common prefix
                final Path placeholder = new Path(directory, PathNormalizer.name(info.getFileName()), EnumSet.of(Path.Type.directory, Path.Type.placeholder));
                objects.add(placeholder);
                continue;
            }
            final PathAttributes attributes = this.parse(info);
            final long revision;
            if(revisions.containsKey(info.getFileName())) {
                // Later version already found
                attributes.setDuplicate(true);
                revision = revisions.get(info.getFileName()) + 1L;
            }
            else {
                revision = 1L;
            }
            revisions.put(info.getFileName(), revision);
            attributes.setRevision(revision);
            objects.add(new Path(directory, PathNormalizer.name(info.getFileName()),
                info.getAction() == Action.start ? EnumSet.of(Path.Type.file, Path.Type.upload) : EnumSet.of(Path.Type.file), attributes));
        }
        if(null == response.getNextFileName()) {
            return new Marker(response.getNextFileName(), response.getNextFileId());
        }
        return new Marker(response.getNextFileName(), response.getNextFileId());
    }

    /**
     * @param response List filenames response from server
     * @return Null when respone filename is not child of working directory directory
     */
    protected PathAttributes parse(final B2FileInfoResponse response) {
        final PathAttributes attributes = new PathAttributes();
        attributes.setChecksum(
            Checksum.parse(StringUtils.removeStart(StringUtils.lowerCase(response.getContentSha1(), Locale.ROOT), "unverified:"))
        );
        final long timestamp = response.getUploadTimestamp();
        if(response.getFileInfo().containsKey(X_BZ_INFO_SRC_LAST_MODIFIED_MILLIS)) {
            final String value = response.getFileInfo().get(X_BZ_INFO_SRC_LAST_MODIFIED_MILLIS);
            try {
                attributes.setModificationDate(Long.parseLong(value));
            }
            catch(NumberFormatException e) {
                log.warn(String.format("Failure parsing src_last_modified_millis with value %s", value));
            }
        }
        else {
            attributes.setModificationDate(timestamp);
        }
        attributes.setVersionId(response.getFileId());
        switch(response.getAction()) {
            case hide:
                // File version marking the file as hidden, so that it will not show up in b2_list_file_names
            case start:
                // Large file has been started, but not finished or canceled
                attributes.setDuplicate(true);
                break;
            default:
                attributes.setSize(response.getContentLength());
                break;
        }
        return attributes;
    }

    private static final class Marker {
        public final String nextFilename;
        public final String nextFileId;

        public Marker(final String nextFilename, final String nextFileId) {
            this.nextFilename = nextFilename;
            this.nextFileId = nextFileId;
        }

        public boolean hasNext() {
            return nextFilename != null;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Marker{");
            sb.append("nextFilename='").append(nextFilename).append('\'');
            sb.append(", nextFileId='").append(nextFileId).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }

}
