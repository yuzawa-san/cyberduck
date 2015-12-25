package ch.cyberduck.binding.foundation;

/*
 * Copyright (c) 2002-2015 David Kocher. All rights reserved.
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

import org.junit.Ignore;
import org.junit.Test;
import org.rococoa.cocoa.foundation.NSPoint;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * @version $Id$
 */
public class NSPointTest {

    @Test
    @Ignore
    public void testEquals() {
        assertEquals(new NSPoint(0, 0), new NSPoint(0, 0));
    }

    @Test
    public void testNotEquals() {
        assertNotEquals(new NSPoint(1, 1), new NSPoint(0, 0));
    }
}