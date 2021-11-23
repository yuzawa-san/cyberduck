/*
 * Copyright (c) 2015-2016 Spectra Logic Corporation. All rights reserved.
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

package ch.cyberduck.core.spectra;

import ch.cyberduck.core.AbstractProtocol;
import ch.cyberduck.core.DirectoryDelimiterPathContainerService;
import ch.cyberduck.core.LocaleFactory;
import ch.cyberduck.core.PathContainerService;
import ch.cyberduck.core.Scheme;
import ch.cyberduck.core.s3.S3Protocol;

public class SpectraProtocol extends AbstractProtocol {
    @Override
    public String getName() {
        return "Spectra S3";
    }

    @Override
    public String getDescription() {
        return LocaleFactory.localizedString("Spectra BlackPearl Deep Storage Gateway (HTTPS)", "S3");
    }

    @Override
    public String getIdentifier() {
        return "spectra";
    }

    @Override
    public boolean isPortConfigurable() {
        return true;
    }

    @Override
    public Scheme getScheme() {
        return Scheme.https;
    }

    @Override
    public Type getType() {
        return Type.s3;
    }

    @Override
    public String getPrefix() {
        return String.format("%s.%s", SpectraProtocol.class.getPackage().getName(), "Spectra");
    }

    @Override
    public boolean isHostnameConfigurable() {
        return true;
    }

    @Override
    public String getUsernamePlaceholder() {
        return LocaleFactory.localizedString("Access Key ID", "S3");
    }

    @Override
    public String getPasswordPlaceholder() {
        return LocaleFactory.localizedString("Secret Access Key", "S3");
    }

    @Override
    public String disk() {
        return String.format("%s.tiff", "ftp");
    }

    @Override
    public String getAuthorization() {
        return S3Protocol.AuthenticationHeaderSignatureVersion.AWS2.name();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getFeature(final Class<T> type) {
        if(type == PathContainerService.class) {
            return (T) new DirectoryDelimiterPathContainerService();
        }
        return super.getFeature(type);
    }
}
