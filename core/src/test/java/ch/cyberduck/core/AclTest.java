package ch.cyberduck.core;

/*
 * Copyright (c) 2002-2013 David Kocher. All rights reserved.
 * http://cyberduck.ch/
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
 *
 * Bug fixes, suggestions and comments should be sent to feedback@cyberduck.ch
 */

import org.junit.Test;

import static org.junit.Assert.*;

public class AclTest {

    @Test
    public void testEquals() {
        assertEquals(
                new Acl(new Acl.UserAndRole(new Acl.CanonicalUser("i-1"), new Acl.Role("r-1"))),
                new Acl(new Acl.UserAndRole(new Acl.CanonicalUser("i-1"), new Acl.Role("r-1"))));
        assertNotEquals(
            new Acl(new Acl.UserAndRole(new Acl.CanonicalUser("i-1"), new Acl.Role("r-1"))),
            new Acl(new Acl.UserAndRole(new Acl.CanonicalUser("i-2"), new Acl.Role("r-1"))));
        assertNotEquals(
            new Acl(new Acl.UserAndRole(new Acl.CanonicalUser("i-1"), new Acl.Role("r-1"))),
            new Acl(new Acl.UserAndRole(new Acl.CanonicalUser("i-1"), new Acl.Role("r-2"))));
        assertNotEquals(
            new Acl(new Acl.UserAndRole(new Acl.CanonicalUser("i-1"), new Acl.Role("r-1")), new Acl.UserAndRole(new Acl.CanonicalUser("i-2"), new Acl.Role("r-1"))),
            new Acl(new Acl.UserAndRole(new Acl.CanonicalUser("i-1"), new Acl.Role("r-2"))));
    }

    @Test
    public void testCanned() {
        assertSame(Acl.EMPTY, Acl.toAcl(""));
        assertSame(Acl.EMPTY, Acl.toAcl("none"));
        assertSame(Acl.CANNED_PRIVATE, Acl.toAcl("private"));
    }
}
