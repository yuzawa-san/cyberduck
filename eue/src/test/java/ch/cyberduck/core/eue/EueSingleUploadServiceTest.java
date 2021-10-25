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

import ch.cyberduck.core.AbstractPath;
import ch.cyberduck.core.AlphanumericRandomStringService;
import ch.cyberduck.core.BytecountStreamListener;
import ch.cyberduck.core.DisabledConnectionCallback;
import ch.cyberduck.core.DisabledLoginCallback;
import ch.cyberduck.core.DisabledPasswordCallback;
import ch.cyberduck.core.DisabledPasswordStore;
import ch.cyberduck.core.Local;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.cryptomator.CryptoVault;
import ch.cyberduck.core.cryptomator.features.CryptoAttributesFeature;
import ch.cyberduck.core.cryptomator.features.CryptoFindFeature;
import ch.cyberduck.core.cryptomator.features.CryptoReadFeature;
import ch.cyberduck.core.cryptomator.features.CryptoUploadFeature;
import ch.cyberduck.core.features.Delete;
import ch.cyberduck.core.io.BandwidthThrottle;
import ch.cyberduck.core.io.StreamCopier;
import ch.cyberduck.core.transfer.TransferStatus;
import ch.cyberduck.core.vault.DefaultVaultRegistry;
import ch.cyberduck.core.vault.VaultCredentials;
import ch.cyberduck.test.IntegrationTest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomUtils;
import org.cryptomator.cryptolib.api.FileHeader;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.UUID;

import static org.junit.Assert.*;

@Category(IntegrationTest.class)
public class EueSingleUploadServiceTest extends AbstractEueSessionTest {

    @Test
    public void testUploadSimpleFile() throws Exception {
        final EueResourceIdProvider fileid = new EueResourceIdProvider(session);
        final EueSingleUploadService service = new EueSingleUploadService(session, fileid, new EueWriteFeature(session, fileid));
        final Path container = new EueDirectoryFeature(session, fileid).mkdir(new Path(new AlphanumericRandomStringService().random(), EnumSet.of(AbstractPath.Type.directory)), new TransferStatus().withLength(0L));
        final String name = new AlphanumericRandomStringService().random();
        final Path file = new Path(container, name, EnumSet.of(Path.Type.file));
        final Local local = new Local(System.getProperty("java.io.tmpdir"), name);
        {
            final byte[] content = RandomUtils.nextBytes(2881);
            IOUtils.write(content, local.getOutputStream(false));
            final TransferStatus status = new TransferStatus().withLength(content.length);
            final BytecountStreamListener count = new BytecountStreamListener();
            service.upload(file, local, new BandwidthThrottle(BandwidthThrottle.UNLIMITED), count, status, new DisabledConnectionCallback());
            assertEquals(content.length, count.getSent());
            assertTrue(status.isComplete());
            assertTrue(new EueFindFeature(session, fileid).find(file));
            assertEquals(content.length, new EueAttributesFinderFeature(session, fileid).find(file).getSize());
            final byte[] compare = new byte[content.length];
            IOUtils.readFully(new EueReadFeature(session, fileid).read(file, new TransferStatus().withLength(content.length), new DisabledConnectionCallback()), compare);
            assertArrayEquals(content, compare);
        }
        {
            final byte[] content = RandomUtils.nextBytes(526);
            IOUtils.write(content, local.getOutputStream(false));
            final TransferStatus status = new TransferStatus().withLength(content.length).exists(true);
            final BytecountStreamListener count = new BytecountStreamListener();
            service.upload(file, local, new BandwidthThrottle(BandwidthThrottle.UNLIMITED), count, status, new DisabledConnectionCallback());
            assertEquals(content.length, count.getSent());
            assertTrue(status.isComplete());
            assertTrue(new EueFindFeature(session, fileid).find(file));
            assertEquals(content.length, new EueAttributesFinderFeature(session, fileid).find(file).getSize());
            final byte[] compare = new byte[content.length];
            IOUtils.readFully(new EueReadFeature(session, fileid).read(file, new TransferStatus().withLength(content.length), new DisabledConnectionCallback()), compare);
            assertArrayEquals(content, compare);
        }
        // Override
        new EueDeleteFeature(session, fileid).delete(Collections.singletonList(container), new DisabledLoginCallback(), new Delete.DisabledCallback());
        local.delete();
    }

    @Test
    public void testUploadVault() throws Exception {
        final EueResourceIdProvider fileid = new EueResourceIdProvider(session);
        final Path container = new EueDirectoryFeature(session, fileid).mkdir(new Path(new AlphanumericRandomStringService().random(), EnumSet.of(AbstractPath.Type.directory)), new TransferStatus().withLength(0L));
        final Path vault = new Path(container, new AlphanumericRandomStringService().random(), EnumSet.of(Path.Type.directory));
        final Path test = new Path(vault, new AlphanumericRandomStringService().random(), EnumSet.of(Path.Type.file));
        final CryptoVault cryptomator = new CryptoVault(vault);
        cryptomator.create(session, null, new VaultCredentials("test"), new DisabledPasswordStore(), CryptoVault.VAULT_VERSION_DEPRECATED);
        session.withRegistry(new DefaultVaultRegistry(new DisabledPasswordStore(), new DisabledPasswordCallback(), cryptomator));
        final Local local = new Local(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString());
        final byte[] content = RandomUtils.nextBytes(502400);
        IOUtils.write(content, local.getOutputStream(false));
        final TransferStatus writeStatus = new TransferStatus();
        final FileHeader header = cryptomator.getFileHeaderCryptor().create();
        writeStatus.setHeader(cryptomator.getFileHeaderCryptor().encryptHeader(header));
        writeStatus.setLength(content.length);
        final BytecountStreamListener count = new BytecountStreamListener();
        final CryptoUploadFeature feature = new CryptoUploadFeature<>(session,
                new EueSingleUploadService(session, fileid, new EueWriteFeature(session, fileid)),
                new EueWriteFeature(session, fileid), cryptomator);
        feature.upload(test, local, new BandwidthThrottle(BandwidthThrottle.UNLIMITED), count, writeStatus, null);
        assertEquals(content.length, count.getSent());
        assertTrue(writeStatus.isComplete());
        assertTrue(new CryptoFindFeature(session, new EueFindFeature(session, fileid), cryptomator).find(test));
        assertEquals(content.length, new CryptoAttributesFeature(session, new EueAttributesFinderFeature(session, fileid), cryptomator).find(test).getSize());
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream(content.length);
        final TransferStatus readStatus = new TransferStatus().withLength(content.length);
        final InputStream in = new CryptoReadFeature(session, new EueReadFeature(session, fileid), cryptomator).read(test, readStatus, new DisabledConnectionCallback());
        new StreamCopier(readStatus, readStatus).transfer(in, buffer);
        assertArrayEquals(content, buffer.toByteArray());
        cryptomator.getFeature(session, Delete.class, new EueDeleteFeature(session, fileid)).delete(Arrays.asList(test, vault), new DisabledLoginCallback(), new Delete.DisabledCallback());
        local.delete();
    }
}
