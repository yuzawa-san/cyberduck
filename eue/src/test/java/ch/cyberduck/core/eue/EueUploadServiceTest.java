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
import ch.cyberduck.core.cryptomator.features.CryptoBulkFeature;
import ch.cyberduck.core.cryptomator.features.CryptoFindFeature;
import ch.cyberduck.core.cryptomator.features.CryptoReadFeature;
import ch.cyberduck.core.cryptomator.features.CryptoUploadFeature;
import ch.cyberduck.core.features.Delete;
import ch.cyberduck.core.io.BandwidthThrottle;
import ch.cyberduck.core.io.StreamCopier;
import ch.cyberduck.core.shared.DisabledBulkFeature;
import ch.cyberduck.core.transfer.Transfer;
import ch.cyberduck.core.transfer.TransferItem;
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
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.*;

@Category(IntegrationTest.class)
public class EueUploadServiceTest extends AbstractEueSessionTest {

    @Test
    public void testUploadLargeFileInChunks() throws Exception {
        final EueResourceIdProvider fileid = new EueResourceIdProvider(session);
        final EueUploadService s = new EueUploadService(session, fileid, new EueMultipartWriteFeature(session, fileid));
        final Path container = new EueDirectoryFeature(session, fileid).mkdir(new Path(
                new AlphanumericRandomStringService().random(), EnumSet.of(AbstractPath.Type.directory)), new TransferStatus());
        final String name = new AlphanumericRandomStringService().random();
        final Path file = new Path(container, name, EnumSet.of(Path.Type.file));
        final Local local = new Local(System.getProperty("java.io.tmpdir"), name);
        final byte[] content = RandomUtils.nextBytes(5242881);
        IOUtils.write(content, local.getOutputStream(false));
        final TransferStatus status = new TransferStatus();
        status.setLength(content.length);
        final BytecountStreamListener count = new BytecountStreamListener();
        final EueWriteFeature.Chunk uploadResponse = s.upload(file, local, new BandwidthThrottle(BandwidthThrottle.UNLIMITED), count, status, new DisabledConnectionCallback());
        assertNotNull(uploadResponse.getCdash64());
        assertEquals(content.length, count.getSent());
        assertTrue(status.isComplete());
        assertTrue(new EueFindFeature(session, fileid).find(file));
        assertEquals(content.length, new EueAttributesFinderFeature(session, fileid).find(file).getSize());
        final byte[] compare = new byte[content.length];
        IOUtils.readFully(new EueReadFeature(session, fileid).read(file, new TransferStatus().withLength(content.length), new DisabledConnectionCallback()), compare);
        assertArrayEquals(content, compare);
        new EueDeleteFeature(session, fileid).delete(Collections.singletonList(container), new DisabledLoginCallback(), new Delete.DisabledCallback());
        local.delete();
    }
}
