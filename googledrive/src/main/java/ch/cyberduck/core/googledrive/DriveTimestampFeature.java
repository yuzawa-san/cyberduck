package ch.cyberduck.core.googledrive;

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

import ch.cyberduck.core.DisabledListProgressListener;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.exception.BackgroundException;
import ch.cyberduck.core.preferences.HostPreferences;
import ch.cyberduck.core.shared.DefaultTimestampFeature;
import ch.cyberduck.core.transfer.TransferStatus;

import java.io.IOException;

import com.google.api.client.util.DateTime;
import com.google.api.services.drive.model.File;

public class DriveTimestampFeature extends DefaultTimestampFeature {

    private final DriveSession session;
    private final DriveFileIdProvider fileid;

    public DriveTimestampFeature(final DriveSession session, final DriveFileIdProvider fileid) {
        this.session = session;
        this.fileid = fileid;
    }

    @Override
    public void setTimestamp(final Path file, final TransferStatus status) throws BackgroundException {
        try {
            final String fileid = this.fileid.getFileId(file, new DisabledListProgressListener());
            final File properties = new File();
            properties.setModifiedTime(new DateTime(status.getTimestamp()));
            session.getClient().files().update(fileid, properties).setFields("modifiedTime").
                setSupportsAllDrives(new HostPreferences(session.getHost()).getBoolean("googledrive.teamdrive.enable")).execute();
        }
        catch(IOException e) {
            throw new DriveExceptionMappingService(fileid).map("Failure to write attributes of {0}", e, file);
        }
    }
}
