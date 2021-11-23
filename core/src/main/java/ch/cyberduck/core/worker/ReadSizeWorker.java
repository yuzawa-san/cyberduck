package ch.cyberduck.core.worker;

/*
 * Copyright (c) 2002-2010 David Kocher. All rights reserved.
 *
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
 * Bug fixes, suggestions and comments should be sent to:
 * dkocher@cyberduck.ch
 */

import ch.cyberduck.core.LocaleFactory;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.Session;
import ch.cyberduck.core.exception.BackgroundException;
import ch.cyberduck.core.exception.ConnectionCanceledException;
import ch.cyberduck.core.transfer.TransferStatus;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;

public class ReadSizeWorker extends Worker<Long> {

    /**
     * Selected files.
     */
    private final List<Path> files;

    private Long total = 0L;

    public ReadSizeWorker(final List<Path> files) {
        this.files = files;
    }

    @Override
    public Long run(final Session<?> session) throws BackgroundException {
        for(Path next : files) {
            if(this.isCanceled()) {
                throw new ConnectionCanceledException();
            }
            if(TransferStatus.UNKNOWN_LENGTH == next.attributes().getSize()) {
                continue;
            }
            total += next.attributes().getSize();
        }
        return total;
    }

    @Override
    public String getActivity() {
        return MessageFormat.format(LocaleFactory.localizedString("Getting size of {0}", "Status"),
                this.toString(files));
    }

    @Override
    public Long initialize() {
        return total;
    }

    @Override
    public boolean equals(final Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }
        final ReadSizeWorker that = (ReadSizeWorker) o;
        if(!Objects.equals(files, that.files)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return files != null ? files.hashCode() : 0;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ReadSizeWorker{");
        sb.append("files=").append(files);
        sb.append('}');
        return sb.toString();
    }
}
