package ch.cyberduck.core.brick;

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

import ch.cyberduck.core.AlphanumericRandomStringService;
import ch.cyberduck.core.DisabledLoginCallback;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.PathAttributes;
import ch.cyberduck.core.exception.NotfoundException;
import ch.cyberduck.core.features.Delete;
import ch.cyberduck.core.transfer.TransferStatus;
import ch.cyberduck.test.IntegrationTest;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Collections;
import java.util.EnumSet;

import static org.junit.Assert.*;

@Category(IntegrationTest.class)
public class BrickAttributesFinderFeatureTest extends AbstractBrickTest {

    @Test(expected = NotfoundException.class)
    public void testFindNotFound() throws Exception {
        final Path folder = new BrickDirectoryFeature(session).mkdir(
            new Path(new AlphanumericRandomStringService().random(), EnumSet.of(Path.Type.directory)), new TransferStatus());
        final Path test = new Path(folder, new AlphanumericRandomStringService().random(), EnumSet.of(Path.Type.file));
        final BrickAttributesFinderFeature f = new BrickAttributesFinderFeature(session);
        try {
            f.find(test);
        }
        finally {
            new BrickDeleteFeature(session).delete(Collections.singletonList(folder), new DisabledLoginCallback(), new Delete.DisabledCallback());
        }
    }

    @Test
    public void testFindRoot() throws Exception {
        final BrickAttributesFinderFeature f = new BrickAttributesFinderFeature(session);
        final PathAttributes attributes = f.find(new Path("/", EnumSet.of(Path.Type.volume, Path.Type.directory)));
        assertEquals(PathAttributes.EMPTY, attributes);
    }

    @Test
    public void testFindFile() throws Exception {
        final Path folder = new BrickDirectoryFeature(session).mkdir(
            new Path(new AlphanumericRandomStringService().random(), EnumSet.of(Path.Type.directory)), new TransferStatus());
        final Path test = new BrickTouchFeature(session).touch(new Path(folder, new AlphanumericRandomStringService().random(), EnumSet.of(Path.Type.file)), new TransferStatus());
        final BrickAttributesFinderFeature f = new BrickAttributesFinderFeature(session);
        final PathAttributes attributes = f.find(test);
        assertEquals(0L, attributes.getSize());
        assertNotEquals(-1L, attributes.getModificationDate());
        assertNull(attributes.getChecksum().algorithm);
        assertTrue(attributes.getPermission().isReadable());
        assertTrue(attributes.getPermission().isWritable());
        // Test wrong type
        try {
            f.find(new Path(test.getAbsolute(), EnumSet.of(Path.Type.directory)));
            fail();
        }
        catch(NotfoundException e) {
            // Expected
        }
        new BrickDeleteFeature(session).delete(Collections.singletonList(folder), new DisabledLoginCallback(), new Delete.DisabledCallback());
    }

    @Test
    public void testFindDirectory() throws Exception {
        final Path folder = new BrickDirectoryFeature(session).mkdir(
            new Path(new AlphanumericRandomStringService().random(), EnumSet.of(Path.Type.directory)), new TransferStatus());
        final Path test = new BrickDirectoryFeature(session).mkdir(new Path(folder, new AlphanumericRandomStringService().random(), EnumSet.of(Path.Type.directory)), new TransferStatus());
        final BrickAttributesFinderFeature f = new BrickAttributesFinderFeature(session);
        final PathAttributes attributes = f.find(test);
        assertEquals(-1L, attributes.getSize());
        assertEquals(-1L, attributes.getModificationDate());
        assertNull(attributes.getChecksum().algorithm);
        assertTrue(attributes.getPermission().isReadable());
        assertTrue(attributes.getPermission().isWritable());
        assertTrue(attributes.getPermission().isExecutable());
        // Test wrong type
        try {
            f.find(new Path(test.getAbsolute(), EnumSet.of(Path.Type.file)));
            fail();
        }
        catch(NotfoundException e) {
            // Expected
        }
        new BrickDeleteFeature(session).delete(Collections.singletonList(folder), new DisabledLoginCallback(), new Delete.DisabledCallback());
    }
}
