package ch.cyberduck.core.dropbox;

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

import ch.cyberduck.core.LocaleFactory;
import ch.cyberduck.core.exception.ChecksumException;
import ch.cyberduck.core.io.AbstractChecksumCompute;
import ch.cyberduck.core.io.Checksum;
import ch.cyberduck.core.io.HashAlgorithm;
import ch.cyberduck.core.transfer.TransferStatus;

import org.apache.commons.codec.binary.Hex;

import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DropboxChecksumCompute extends AbstractChecksumCompute {

    @Override
    public Checksum compute(final InputStream in, final TransferStatus status) throws ChecksumException {
        try {
            return new Checksum(HashAlgorithm.sha256, Hex.encodeHexString(this.digest(
                    this.normalize(in, status), new DropboxContentHasher(
                            MessageDigest.getInstance("SHA-256"), MessageDigest.getInstance("SHA-256"), 0))));
        }
        catch(NoSuchAlgorithmException e) {
            throw new ChecksumException(LocaleFactory.localizedString("Checksum failure", "Error"), e.getMessage(), e);
        }
    }
}
