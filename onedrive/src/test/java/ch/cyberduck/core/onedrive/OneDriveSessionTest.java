package ch.cyberduck.core.onedrive;

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


import ch.cyberduck.core.Host;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.TestProtocol;
import ch.cyberduck.core.ssl.DefaultX509KeyManager;
import ch.cyberduck.core.ssl.DisabledX509TrustManager;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class OneDriveSessionTest {
    private OneDriveSession session;

    @Before
    public void setup() {
        session = new OneDriveSession(new Host(new TestProtocol()), new DisabledX509TrustManager(), new DefaultX509KeyManager());
    }

    @Test
    public void testIsAccessible() {
        // Tests for valid accessibility for regular access and container access.
        // Determines whether Touch/Directory/Move/Copy can perform actions
        // Touch/Directory require valid container.
        // Move/Copy require valid container for target and valid source.

        final List<TestCase> test = new ArrayList<>();
        test.add(new TestCase("/", false, false));
        test.add(new TestCase("/Invalid", false, false));
        test.add(new TestCase("/My Files", false, true));
        test.add(new TestCase("/My Files/Test", true, true));
        test.add(new TestCase("/Shared", false, false));
        test.add(new TestCase("/Shared/Folder", false, true));
        test.add(new TestCase("/Shared/Folder/Test", true, true));

        for(TestCase testCase : test) {
            final Path path = new Path(testCase.target, EnumSet.of(Path.Type.directory));
            assertEquals(String.format("Non-Container access: for %s", path), testCase.isValid, session.isAccessible(path, false));
            assertEquals(String.format("Container access: for %s", path), testCase.isValidContainer, session.isAccessible(path, true));
        }
    }

    static class TestCase {
        private final String target;
        private final boolean isValid;
        private final boolean isValidContainer;

        TestCase(final String target, final boolean isValid, final boolean isValidContainer) {
            this.target = target;
            this.isValid = isValid;
            this.isValidContainer = isValidContainer;
        }
    }
}
