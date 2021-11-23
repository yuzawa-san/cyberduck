package ch.cyberduck.core.cryptomator.features;

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

import ch.cyberduck.core.ConnectionCallback;
import ch.cyberduck.core.DefaultIOExceptionMappingService;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.Session;
import ch.cyberduck.core.cryptomator.CryptoOutputStream;
import ch.cyberduck.core.cryptomator.CryptoVault;
import ch.cyberduck.core.cryptomator.random.RotatingNonceGenerator;
import ch.cyberduck.core.exception.BackgroundException;
import ch.cyberduck.core.features.Write;
import ch.cyberduck.core.io.ChecksumCompute;
import ch.cyberduck.core.io.StatusOutputStream;
import ch.cyberduck.core.transfer.TransferStatus;

import java.io.IOException;

public class CryptoWriteFeature<Reply> implements Write<Reply> {

    private final Session<?> session;
    private final Write<Reply> proxy;
    private final CryptoVault vault;

    public CryptoWriteFeature(final Session<?> session, final Write<Reply> proxy, final CryptoVault vault) {
        this.session = session;
        this.proxy = proxy;
        this.vault = vault;
    }

    @Override
    public StatusOutputStream<Reply> write(final Path file, final TransferStatus status, final ConnectionCallback callback) throws BackgroundException {
        try {
            if(null == status.getNonces()) {
                // If not previously set in bulk feature
                status.setNonces(new RotatingNonceGenerator(vault.numberOfChunks(status.getLength())));
            }
            final StatusOutputStream<Reply> out = proxy.write(vault.encrypt(session, file),
                    new TransferStatus(status)
                            .withLength(vault.toCiphertextSize(status.getOffset(), status.getLength()))
                            // Assume single chunk upload
                            .withOffset(vault.toCiphertextSize(0L, status.getOffset()))
                            .withMime(null), callback);
            if(status.getOffset() == 0L) {
                out.write(status.getHeader().array());
            }
            return new CryptoOutputStream<>(out, vault.getFileContentCryptor(), vault.getFileHeaderCryptor().decryptHeader(status.getHeader()),
                    status.getNonces(), vault.numberOfChunks(status.getOffset()));
        }
        catch(IOException e) {
            throw new DefaultIOExceptionMappingService().map(e);
        }
    }

    @Override
    public Append append(final Path file, final TransferStatus status) throws BackgroundException {
        return new Append(false).withStatus(status);
    }

    @Override
    public boolean temporary() {
        return proxy.temporary();
    }

    @Override
    public boolean random() {
        return proxy.random();
    }

    @Override
    public boolean timestamp() {
        return proxy.timestamp();
    }

    @Override
    public ChecksumCompute checksum(final Path file, final TransferStatus status) {
        return new CryptoChecksumCompute(proxy.checksum(file, status), vault);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CryptoWriteFeature{");
        sb.append("proxy=").append(proxy);
        sb.append('}');
        return sb.toString();
    }
}
