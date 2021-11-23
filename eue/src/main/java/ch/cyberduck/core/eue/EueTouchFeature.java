package ch.cyberduck.core.eue;

/*
 * Copyright (c) 2002-2021 iterate GmbH. All rights reserved.
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
import ch.cyberduck.core.shared.DefaultTouchFeature;

import org.apache.commons.lang3.StringUtils;

public class EueTouchFeature extends DefaultTouchFeature<EueUploadHelper.UploadResponse> {

    public EueTouchFeature(final EueSession session, final EueResourceIdProvider fileid) {
        super(new EueWriteFeature(session, fileid), new EueAttributesFinderFeature(session, fileid));
    }

    @Override
    public boolean isSupported(final Path workdir, final String filename) {
        // The path may contain all characters except the following: /, ", >, <, ?, *, :, \, |.
        // Also prohibited is a trailing space or a trailing dot (" ", ".").
        if(StringUtils.containsAny(filename, '\\', '<', '>', ':', '"', '|', '?', '*', '/')) {
            return false;
        }
        if(StringUtils.endsWith(filename, StringUtils.SPACE)) {
            return false;
        }
        if(StringUtils.endsWith(filename, ".")) {
            return false;
        }
        return super.isSupported(workdir, filename);
    }
}
