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

import ch.cyberduck.core.LocaleFactory;
import ch.cyberduck.core.cryptomator.CryptoOutputStream;
import ch.cyberduck.core.cryptomator.CryptoVault;
import ch.cyberduck.core.cryptomator.random.RotatingNonceGenerator;
import ch.cyberduck.core.exception.BackgroundException;
import ch.cyberduck.core.exception.ChecksumException;
import ch.cyberduck.core.io.AbstractChecksumCompute;
import ch.cyberduck.core.io.Checksum;
import ch.cyberduck.core.io.ChecksumCompute;
import ch.cyberduck.core.io.StreamCopier;
import ch.cyberduck.core.io.VoidStatusOutputStream;
import ch.cyberduck.core.preferences.PreferencesFactory;
import ch.cyberduck.core.random.NonceGenerator;
import ch.cyberduck.core.threading.ThreadPool;
import ch.cyberduck.core.threading.ThreadPoolFactory;
import ch.cyberduck.core.transfer.TransferStatus;
import ch.cyberduck.core.worker.DefaultExceptionMappingService;

import org.apache.commons.io.input.NullInputStream;
import org.apache.log4j.Logger;
import org.cryptomator.cryptolib.api.FileHeader;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class CryptoChecksumCompute extends AbstractChecksumCompute {
    private static final Logger log = Logger.getLogger(CryptoChecksumCompute.class);

    private final CryptoVault cryptomator;
    private final ChecksumCompute delegate;

    public CryptoChecksumCompute(final ChecksumCompute delegate, final CryptoVault vault) {
        this.cryptomator = vault;
        this.delegate = delegate;
    }

    @Override
    public Checksum compute(final InputStream in, final TransferStatus status) throws ChecksumException {
        if(Checksum.NONE == delegate.compute(new NullInputStream(0L), new TransferStatus())) {
            return Checksum.NONE;
        }
        if(null == status.getHeader()) {
            // Write header to be reused in writer
            final FileHeader header = cryptomator.getFileHeaderCryptor().create();
            status.setHeader(cryptomator.getFileHeaderCryptor().encryptHeader(header));
        }
        // Make nonces reusable in case we need to compute a checksum
        status.setNonces(new RotatingNonceGenerator(cryptomator.numberOfChunks(status.getLength())));
        return this.compute(this.normalize(in, status), status.getOffset(), status.getLength(), status.getHeader(), status.getNonces());
    }

    protected Checksum compute(final InputStream in, final long offset, final long length, final ByteBuffer header, final NonceGenerator nonces) throws ChecksumException {
        if(log.isDebugEnabled()) {
            log.debug(String.format("Calculate checksum with header %s", header));
        }
        try {
            final PipedOutputStream source = new PipedOutputStream();
            final CryptoOutputStream<Void> out = new CryptoOutputStream<>(new VoidStatusOutputStream(source), cryptomator.getFileContentCryptor(),
                    cryptomator.getFileHeaderCryptor().decryptHeader(header), nonces, cryptomator.numberOfChunks(offset));
            final PipedInputStream sink = new PipedInputStream(source, PreferencesFactory.get().getInteger("connection.chunksize"));
            final ThreadPool pool = ThreadPoolFactory.get("checksum", 1);
            try {
                final Future<TransferStatus> execute = pool.execute(new Callable<TransferStatus>() {
                    @Override
                    public TransferStatus call() throws Exception {
                        if(offset == 0) {
                            source.write(header.array());
                        }
                        final TransferStatus status = new TransferStatus();
                        new StreamCopier(status, status).transfer(in, out);
                        return status;
                    }
                });
                try {
                    return delegate.compute(sink, new TransferStatus().withLength(cryptomator.toCiphertextSize(offset, length)));
                }
                finally {
                    try {
                        execute.get();
                    }
                    catch(InterruptedException e) {
                        throw new ChecksumException(LocaleFactory.localizedString("Checksum failure", "Error"), e.getMessage(), e);
                    }
                    catch(ExecutionException e) {
                        if(e.getCause() instanceof BackgroundException) {
                            throw (BackgroundException) e.getCause();
                        }
                        throw new DefaultExceptionMappingService().map(e.getCause());
                    }
                }
            }
            finally {
                pool.shutdown(true);
            }
        }
        catch(ChecksumException e) {
            throw e;
        }
        catch(IOException | BackgroundException e) {
            throw new ChecksumException(LocaleFactory.localizedString("Checksum failure", "Error"), e.getMessage(), e);
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CryptoChecksumCompute{");
        sb.append("delegate=").append(delegate);
        sb.append('}');
        return sb.toString();
    }
}
