package ch.cyberduck.core.profiles;

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

import ch.cyberduck.core.HostParser;
import ch.cyberduck.core.ProtocolFactory;
import ch.cyberduck.core.dav.DAVSSLProtocol;

import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertFalse;

public class RemoteProfilesFinderTest {

    @Test
    public void find() throws Exception {
        final RemoteProfilesFinder finder = new RemoteProfilesFinder(
            new HostParser(new ProtocolFactory(Collections.singleton(new DAVSSLProtocol() {
                @Override
                public boolean isEnabled() {
                    return true;
                }
            }))).get("https://svn.cyberduck.io/trunk/profiles"));
        assertFalse(finder.find().isEmpty());
    }
}
