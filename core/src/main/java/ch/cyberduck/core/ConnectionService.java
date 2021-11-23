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

import ch.cyberduck.core.exception.BackgroundException;
import ch.cyberduck.core.threading.CancelCallback;

public interface ConnectionService {

    /**
     * Assert that the connection to the remote host is still alive and opens connection if needed. Prompts for
     * credentials and connect to server if not already connected.
     *
     * @param session  Session
     * @param callback Cancel
     * @return True if new connection was opened. False if connection is reused.
     * @throws BackgroundException If opening connection fails
     */
    boolean check(Session<?> session, CancelCallback callback) throws BackgroundException;

    /**
     * Open connection
     *
     * @param session  Connection
     * @param callback Cancel
     */
    void connect(Session<?> session, CancelCallback callback) throws BackgroundException;

    /**
     * Disconnect
     *
     * @param session Connection
     */
    void close(Session<?> session) throws BackgroundException;
}
