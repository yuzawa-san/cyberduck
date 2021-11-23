package ch.cyberduck.core.transfer.download;

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
import ch.cyberduck.core.features.AttributesFinder;
import ch.cyberduck.core.features.Find;
import ch.cyberduck.core.synchronization.Comparison;
import ch.cyberduck.core.synchronization.DefaultComparePathFilter;
import ch.cyberduck.core.transfer.TransferStatus;
import ch.cyberduck.core.transfer.symlink.SymlinkResolver;

import org.apache.log4j.Logger;

public class CompareFilter extends AbstractDownloadFilter {
    private static final Logger log = Logger.getLogger(CompareFilter.class);

    private final ProgressListener listener;
    private final DefaultComparePathFilter comparisonService;

    public CompareFilter(final SymlinkResolver<Path> symlinkResolver, final Session<?> session, final ProgressListener listener) {
        this(symlinkResolver, session, new DownloadFilterOptions(session.getHost()), listener);
    }

    public CompareFilter(final SymlinkResolver<Path> symlinkResolver, final Session<?> session,
                         final DownloadFilterOptions options,
                         final ProgressListener listener) {
        this(symlinkResolver, session, options, listener, new DefaultComparePathFilter(session, session.getHost().getTimezone()));
    }

    public CompareFilter(final SymlinkResolver<Path> symlinkResolver, final Session<?> session,
                         final DownloadFilterOptions options, final ProgressListener listener,
                         final DefaultComparePathFilter comparisonService) {
        super(symlinkResolver, session, options);
        this.listener = listener;
        this.comparisonService = comparisonService;
    }

    @Override
    public AbstractDownloadFilter withFinder(final Find finder) {
        comparisonService.withFinder(finder);
        return super.withFinder(finder);
    }

    @Override
    public AbstractDownloadFilter withAttributes(final AttributesFinder attributes) {
        comparisonService.withAttributes(attributes);
        return super.withAttributes(attributes);
    }

    @Override
    public boolean accept(final Path file, final Local local, final TransferStatus parent) throws BackgroundException {
        if(super.accept(file, local, parent)) {
            final Comparison comparison = comparisonService.compare(file, local, listener);
            switch(comparison) {
                case local:
                    if(log.isInfoEnabled()) {
                        log.info(String.format("Skip file %s with comparison %s", file, comparison));
                    }
                    return false;
                case equal:
                    if(file.isDirectory()) {
                        return true;
                    }
                    if(log.isInfoEnabled()) {
                        log.info(String.format("Skip file %s with comparison %s", file, comparison));
                    }
                    return false;
                case remote:
                    return true;
            }
            log.warn(String.format("Invalid comparison result %s", comparison));
        }
        return false;
    }
}
