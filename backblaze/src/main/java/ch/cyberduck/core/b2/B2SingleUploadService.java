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

import ch.cyberduck.core.LocaleFactory;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.exception.BackgroundException;
import ch.cyberduck.core.exception.ChecksumException;
import ch.cyberduck.core.features.Upload;
import ch.cyberduck.core.features.Write;
import ch.cyberduck.core.http.HttpUploadFeature;
import ch.cyberduck.core.io.Checksum;
import ch.cyberduck.core.preferences.HostPreferences;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;

import synapticloop.b2.response.B2FileResponse;
import synapticloop.b2.response.BaseB2Response;

public class B2SingleUploadService extends HttpUploadFeature<BaseB2Response, MessageDigest> {
    private static final Logger log = Logger.getLogger(B2SingleUploadService.class);

    private final B2Session session;
    private Write<BaseB2Response> writer;

    public B2SingleUploadService(final B2Session session, final Write<BaseB2Response> writer) {
        super(writer);
        this.session = session;
        this.writer = writer;
    }

    @Override
    protected InputStream decorate(final InputStream in, final MessageDigest digest) throws IOException {
        if(null == digest) {
            return super.decorate(in, null);
        }
        else {
            return new DigestInputStream(super.decorate(in, digest), digest);
        }
    }

    @Override
    protected MessageDigest digest() throws IOException {
        MessageDigest digest = null;
        if(new HostPreferences(session.getHost()).getBoolean("b2.upload.checksum.verify")) {
            try {
                digest = MessageDigest.getInstance("SHA1");
            }
            catch(NoSuchAlgorithmException e) {
                throw new IOException(e.getMessage(), e);
            }
        }
        return digest;
    }

    @Override
    protected void post(final Path file, final MessageDigest digest, final BaseB2Response response) throws BackgroundException {
        this.verify(file, digest, Checksum.parse(StringUtils.removeStart(((B2FileResponse) response).getContentSha1(), "unverified:")));
    }

    @Override
    protected void verify(final Path file, final MessageDigest digest, final Checksum checksum) throws ChecksumException {
        if(null == digest) {
            log.debug(String.format("Digest verification disabled for file %s", file));
            return;
        }
        if(file.getType().contains(Path.Type.encrypted)) {
            log.warn(String.format("Skip checksum verification for %s with client side encryption enabled", file));
            return;
        }
        final String expected = Hex.encodeHexString(digest.digest());
        if(!checksum.equals(Checksum.parse(expected))) {
            throw new ChecksumException(MessageFormat.format(LocaleFactory.localizedString("Upload {0} failed", "Error"), file.getName()),
                    MessageFormat.format("Mismatch between {0} hash {1} of uploaded data and ETag {2} returned by the server",
                            checksum.algorithm.toString(), expected, checksum.hash));
        }
    }

    @Override
    public Upload<BaseB2Response> withWriter(final Write<BaseB2Response> writer) {
        this.writer = writer;
        return super.withWriter(writer);
    }
}
