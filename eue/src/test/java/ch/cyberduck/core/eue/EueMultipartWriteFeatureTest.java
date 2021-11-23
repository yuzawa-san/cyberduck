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

import ch.cyberduck.core.AlphanumericRandomStringService;
import ch.cyberduck.core.BytecountStreamListener;
import ch.cyberduck.core.DisabledConnectionCallback;
import ch.cyberduck.core.DisabledLoginCallback;
import ch.cyberduck.core.DisabledPasswordCallback;
import ch.cyberduck.core.DisabledPasswordStore;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.cryptomator.CryptoVault;
import ch.cyberduck.core.cryptomator.features.CryptoFindFeature;
import ch.cyberduck.core.cryptomator.features.CryptoReadFeature;
import ch.cyberduck.core.cryptomator.features.CryptoWriteFeature;
import ch.cyberduck.core.cryptomator.random.RandomNonceGenerator;
import ch.cyberduck.core.features.Delete;
import ch.cyberduck.core.http.HttpResponseOutputStream;
import ch.cyberduck.core.io.Checksum;
import ch.cyberduck.core.io.StatusOutputStream;
import ch.cyberduck.core.io.StreamCopier;
import ch.cyberduck.core.shared.DefaultFindFeature;
import ch.cyberduck.core.transfer.TransferStatus;
import ch.cyberduck.core.vault.DefaultVaultRegistry;
import ch.cyberduck.core.vault.VaultCredentials;
import ch.cyberduck.test.IntegrationTest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomUtils;
import org.cryptomator.cryptolib.api.FileHeader;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;

import static org.junit.Assert.*;

@Category(IntegrationTest.class)
public class EueMultipartWriteFeatureTest extends AbstractEueSessionTest {

    @Test
    public void testSmallChunk() throws Exception {
        // Uploading a file via the Upload Resource, using the chunked upload method, is only allowed for documents bigger than the chunksize (4MiB)
        final EueResourceIdProvider fileid = new EueResourceIdProvider(session);
        final EueMultipartWriteFeature feature = new EueMultipartWriteFeature(session, fileid);
        final Path container = new EueDirectoryFeature(session, fileid).mkdir(new Path(new AlphanumericRandomStringService().random(), EnumSet.of(Path.Type.directory)), new TransferStatus());
        final Path file = new Path(container, new AlphanumericRandomStringService().random(), EnumSet.of(Path.Type.file));
        final byte[] content = RandomUtils.nextBytes(512000);
        final TransferStatus status = new TransferStatus().withLength(512000L);
        final Checksum checksum = feature.checksum(file, status).compute(new ByteArrayInputStream(content), new TransferStatus().withLength(content.length));
        final HttpResponseOutputStream<EueWriteFeature.Chunk> out = feature.write(file, status, new DisabledConnectionCallback());
        assertNotNull(out);
        new StreamCopier(status, status).transfer(new ByteArrayInputStream(content), out);
        assertNotNull(out.getStatus());
        assertNotNull(out.getStatus().getCdash64());
        assertEquals(checksum, out.getStatus().getChecksum());
        assertTrue(new DefaultFindFeature(session).find(file));
        final byte[] compare = new byte[content.length];
        final InputStream stream = new EueReadFeature(session, fileid).read(file, new TransferStatus().withLength(content.length), new DisabledConnectionCallback());
        IOUtils.readFully(stream, compare);
        stream.close();
        assertArrayEquals(content, compare);
        new EueDeleteFeature(session, fileid).delete(Collections.singletonList(container), new DisabledLoginCallback(), new Delete.DisabledCallback());
    }

    @Test
    public void testMultipartWrite() throws Exception {
        final EueResourceIdProvider fileid = new EueResourceIdProvider(session);
        final EueMultipartWriteFeature feature = new EueMultipartWriteFeature(session, fileid);
        final Path container = new EueDirectoryFeature(session, fileid).mkdir(new Path(new AlphanumericRandomStringService().random(), EnumSet.of(Path.Type.directory)), new TransferStatus());
        final Path file = new Path(container, new AlphanumericRandomStringService().random(), EnumSet.of(Path.Type.file));
        {
            final byte[] content = RandomUtils.nextBytes(8943045);
            final TransferStatus status = new TransferStatus().withLength(content.length);
            final Checksum checksum = feature.checksum(file, status).compute(new ByteArrayInputStream(content), new TransferStatus().withLength(content.length));
            final HttpResponseOutputStream<EueWriteFeature.Chunk> out = feature.write(file, status, new DisabledConnectionCallback());
            assertNotNull(out);
            new StreamCopier(status, status).transfer(new ByteArrayInputStream(content), out);
            assertNotNull(out.getStatus());
            assertNotNull(out.getStatus().getCdash64());
            // Different due to chunking
            assertNotEquals(checksum.hash, out.getStatus().getCdash64());
            assertTrue(new DefaultFindFeature(session).find(file));
            final byte[] compare = new byte[content.length];
            final InputStream stream = new EueReadFeature(session, fileid).read(file, new TransferStatus().withLength(content.length), new DisabledConnectionCallback());
            IOUtils.readFully(stream, compare);
            stream.close();
            assertArrayEquals(content, compare);
        }
        // Override
        {
            final byte[] content = RandomUtils.nextBytes(4943045);
            final TransferStatus status = new TransferStatus().withLength(content.length).exists(true);
            final Checksum checksum = feature.checksum(file, status).compute(new ByteArrayInputStream(content), new TransferStatus().withLength(content.length));
            final HttpResponseOutputStream<EueWriteFeature.Chunk> out = feature.write(file, status, new DisabledConnectionCallback());
            assertNotNull(out);
            new StreamCopier(status, status).transfer(new ByteArrayInputStream(content), out);
            assertNotNull(out.getStatus());
            assertNotNull(out.getStatus().getCdash64());
            // Different due to chunking
            assertNotEquals(checksum.hash, out.getStatus().getCdash64());
            assertTrue(new DefaultFindFeature(session).find(file));
            final byte[] compare = new byte[content.length];
            final InputStream stream = new EueReadFeature(session, fileid).read(file, new TransferStatus().withLength(content.length), new DisabledConnectionCallback());
            IOUtils.readFully(stream, compare);
            stream.close();
            assertArrayEquals(content, compare);
        }
        new EueDeleteFeature(session, fileid).delete(Collections.singletonList(container), new DisabledLoginCallback(), new Delete.DisabledCallback());
    }

    @Test
    public void testWriteVault() throws Exception {
        final EueResourceIdProvider fileid = new EueResourceIdProvider(session);
        final Path container = new EueDirectoryFeature(session, fileid).mkdir(new Path(new AlphanumericRandomStringService().random(), EnumSet.of(Path.Type.directory)), new TransferStatus());
        final Path vault = new Path(container, new AlphanumericRandomStringService().random(), EnumSet.of(Path.Type.directory));
        final Path test = new Path(vault, new AlphanumericRandomStringService().random(), EnumSet.of(Path.Type.file));
        final CryptoVault cryptomator = new CryptoVault(vault);
        cryptomator.create(session, null, new VaultCredentials("test"), new DisabledPasswordStore(), CryptoVault.VAULT_VERSION_DEPRECATED);
        session.withRegistry(new DefaultVaultRegistry(new DisabledPasswordStore(), new DisabledPasswordCallback(), cryptomator));
        final CryptoWriteFeature feature = new CryptoWriteFeature<>(session, new EueWriteFeature(session, fileid), cryptomator);
        final byte[] content = RandomUtils.nextBytes(6 * 1024 * 1024);
        final TransferStatus writeStatus = new TransferStatus();
        final FileHeader header = cryptomator.getFileHeaderCryptor().create();
        writeStatus.setHeader(cryptomator.getFileHeaderCryptor().encryptHeader(header));
        writeStatus.setNonces(new RandomNonceGenerator());
        writeStatus.setLength(-1L);
        final StatusOutputStream out = feature.write(test, writeStatus, new DisabledConnectionCallback());
        final ByteArrayInputStream in = new ByteArrayInputStream(content);
        final TransferStatus progress = new TransferStatus();
        final BytecountStreamListener count = new BytecountStreamListener();
        new StreamCopier(new TransferStatus(), progress).withListener(count).transfer(in, out);
        assertEquals(content.length, count.getSent());
        assertEquals(content.length, count.getRecv());
        assertNotNull(out.getStatus());
        assertTrue(new CryptoFindFeature(session, new EueFindFeature(session, fileid), cryptomator).find(test));
        final byte[] compare = new byte[content.length];
        final InputStream stream = new CryptoReadFeature(session, new EueReadFeature(session, fileid), cryptomator).read(test, new TransferStatus().withLength(content.length), new DisabledConnectionCallback());
        IOUtils.readFully(stream, compare);
        stream.close();
        assertArrayEquals(content, compare);
        cryptomator.getFeature(session, Delete.class, new EueDeleteFeature(session, fileid)).delete(Arrays.asList(test, vault), new DisabledLoginCallback(), new Delete.DisabledCallback());
    }

}
