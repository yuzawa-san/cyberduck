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
import ch.cyberduck.core.Local;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.features.Delete;
import ch.cyberduck.core.io.BandwidthThrottle;
import ch.cyberduck.core.transfer.TransferStatus;
import ch.cyberduck.test.IntegrationTest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Collections;
import java.util.EnumSet;

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
}
