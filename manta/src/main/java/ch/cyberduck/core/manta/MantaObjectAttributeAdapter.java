package ch.cyberduck.core.manta;

/*
 * Copyright (c) 2002-2017 iterate GmbH. All rights reserved.
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

import ch.cyberduck.core.DescriptiveUrl;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.PathAttributes;
import ch.cyberduck.core.Permission;
import ch.cyberduck.core.URIEncoder;
import ch.cyberduck.core.DefaultWebUrlProvider;
import ch.cyberduck.core.io.Checksum;
import ch.cyberduck.core.io.HashAlgorithm;

import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.EnumSet;

import com.joyent.manta.client.MantaObject;

public final class MantaObjectAttributeAdapter {
    private static final Logger log = Logger.getLogger(MantaObjectAttributeAdapter.class);

    private final MantaSession session;

    private static final String HEADER_KEY_STORAGE_CLASS = "Durability-Level";

    public MantaObjectAttributeAdapter(MantaSession session) {
        this.session = session;
    }

    public PathAttributes convert(final MantaObject object) {
        final PathAttributes attributes = new PathAttributes();
        attributes.setPermission(new Permission(
            session.isUserWritable(object) ? Permission.Action.all : Permission.Action.read,
            Permission.Action.none,
            session.isWorldReadable(object) ? Permission.Action.read : Permission.Action.none));
        if(object.getLastModifiedTime() != null) {
            attributes.setModificationDate(object.getLastModifiedTime().getTime());
        }
        if(object.isDirectory()) {
            return attributes;
        }
        if(session.isWorldReadable(object)) {
            // mantaObject.getPath() starts with /
            final String joinedPath = new DefaultWebUrlProvider(session.getHost()).toUrl() + URIEncoder.encode(object.getPath());
            try {
                final URI link = new URI(joinedPath);
                attributes.setLink(new DescriptiveUrl(link, DescriptiveUrl.Type.http));
            }
            catch(URISyntaxException e) {
                log.warn(String.format("Cannot set link. Web URL returned %s", joinedPath), e);
            }
        }
        attributes.setSize(object.getContentLength());
        attributes.setETag(object.getEtag());
        final byte[] md5Bytes = object.getMd5Bytes();
        if(md5Bytes != null) {
            attributes.setChecksum(new Checksum(HashAlgorithm.md5, Hex.encodeHexString(md5Bytes)));
        }
        final String storageClass = object.getHeaderAsString(HEADER_KEY_STORAGE_CLASS);
        if(storageClass != null) {
            attributes.setStorageClass(storageClass);
        }
        return attributes;
    }

    public Path toPath(final MantaObject object) {
        return new Path(object.getPath(), object.isDirectory() ? EnumSet.of(Path.Type.directory) : EnumSet.of(Path.Type.file));
    }
}
