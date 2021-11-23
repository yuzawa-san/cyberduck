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

import ch.cyberduck.core.AbstractProtocol;
import ch.cyberduck.core.PathContainerService;
import ch.cyberduck.core.Scheme;
import ch.cyberduck.core.text.DefaultLexicographicOrderComparator;

import java.util.Comparator;

public class B2Protocol extends AbstractProtocol {

    @Override
    public String getName() {
        return "B2";
    }

    @Override
    public String getIdentifier() {
        return "b2";
    }

    @Override
    public String getDescription() {
        return "Backblaze B2 Cloud Storage";
    }

    @Override
    public Scheme getScheme() {
        return Scheme.https;
    }

    @Override
    public String getUsernamePlaceholder() {
        return "Account ID";
    }

    @Override
    public String getPasswordPlaceholder() {
        return "Application Key";
    }

    @Override
    public boolean isHostnameConfigurable() {
        return false;
    }

    @Override
    public boolean isPortConfigurable() {
        return false;
    }

    @Override
    public String disk() {
        return String.format("%s.tiff", "ftp");
    }

    @Override
    public Comparator<String> getListComparator() {
        return new DefaultLexicographicOrderComparator();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getFeature(final Class<T> type) {
        if(type == PathContainerService.class) {
            return (T) new B2PathContainerService();
        }
        return super.getFeature(type);
    }
}
