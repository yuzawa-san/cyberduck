package ch.cyberduck.core.transfer.upload;

/*
 * Copyright (c) 2002-2013 David Kocher. All rights reserved.
 * http://cyberduck.ch/
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
 *
 * Bug fixes, suggestions and comments should be sent to feedback@cyberduck.ch
 */

import ch.cyberduck.core.Local;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.ProgressListener;
import ch.cyberduck.core.Session;
import ch.cyberduck.core.exception.BackgroundException;
import ch.cyberduck.core.transfer.TransferStatus;
import ch.cyberduck.core.transfer.symlink.SymlinkResolver;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class RenameFilter extends AbstractUploadFilter {
    private static final Logger log = Logger.getLogger(RenameFilter.class);

    public RenameFilter(final SymlinkResolver<Local> symlinkResolver, final Session<?> session) {
        this(symlinkResolver, session, new UploadFilterOptions(session.getHost()));
    }

    public RenameFilter(final SymlinkResolver<Local> symlinkResolver, final Session<?> session,
                        final UploadFilterOptions options) {
        super(symlinkResolver, session, options);
    }

    @Override
    public TransferStatus prepare(final Path file, final Local local, final TransferStatus parent, final ProgressListener progress) throws BackgroundException {
        final TransferStatus status = super.prepare(file, local, parent, progress);
        if(status.isExists()) {
            final String filename = file.getName();
            int no = 0;
            do {
                String proposal = String.format("%s-%d", FilenameUtils.getBaseName(filename), ++no);
                if(StringUtils.isNotBlank(Path.getExtension(filename))) {
                    proposal += String.format(".%s", Path.getExtension(filename));
                }
                if(parent.getRename().remote != null) {
                    status.rename(new Path(parent.getRename().remote, proposal, file.getType()));
                }
                else {
                    status.rename(new Path(file.getParent(), proposal, file.getType()));
                }
            }
            while(find.find(status.getRename().remote));
            if(log.isInfoEnabled()) {
                log.info(String.format("Changed upload target from %s to %s", file, status.getRename().remote));
            }
            if(log.isDebugEnabled()) {
                log.debug(String.format("Clear exist flag for file %s", file));
            }
            status.setExists(false);
        }
        else {
            if(parent.getRename().remote != null) {
                status.rename(new Path(parent.getRename().remote, file.getName(), file.getType()));
            }
            if(log.isInfoEnabled()) {
                log.info(String.format("Changed upload target from %s to %s", file, status.getRename().remote));
            }
        }
        return status;
    }
}
