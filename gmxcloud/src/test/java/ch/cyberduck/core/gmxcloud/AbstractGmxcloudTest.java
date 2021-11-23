package ch.cyberduck.core.gmxcloud;

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

import ch.cyberduck.core.*;
import ch.cyberduck.core.http.HttpResponseOutputStream;
import ch.cyberduck.core.io.StreamCopier;
import ch.cyberduck.core.serializer.impl.dd.ProfilePlistReader;
import ch.cyberduck.core.ssl.DefaultX509KeyManager;
import ch.cyberduck.core.ssl.DisabledX509TrustManager;
import ch.cyberduck.core.transfer.TransferStatus;

import org.junit.After;
import org.junit.Before;

import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class AbstractGmxcloudTest {
    protected GmxcloudSession session;

    @Before
    public void setup() throws Exception {
        final ProtocolFactory factory = new ProtocolFactory(new HashSet<>(Collections.singleton(new GmxcloudProtocol())));
        final Profile profile = new ProfilePlistReader(factory).read(
                this.getClass().getResourceAsStream("/GMX Cloud.cyberduckprofile"));
        final Host host = new Host(profile, profile.getDefaultHostname(), new Credentials(
                System.getProperties().getProperty("gmxcloud.user"), System.getProperties().getProperty("gmxcloud.password")
        ));
        session = new GmxcloudSession(host, new DisabledX509TrustManager(), new DefaultX509KeyManager());
        final LoginConnectionService login = new LoginConnectionService(new DisabledLoginCallback() {
            @Override
            public Credentials prompt(final Host bookmark, final String title, final String reason, final LoginOptions options) {
                fail(reason);
                return null;
            }
        }, new DisabledHostKeyCallback(), new TestPasswordStore(), new DisabledProgressListener());
        login.check(session, new DisabledCancelCallback());
    }

    @After
    public void disconnect() throws Exception {
        session.close();
    }

    public static class TestPasswordStore extends DisabledPasswordStore {
        @Override
        public String getPassword(Scheme scheme, int port, String hostname, String user) {
            if(user.equals("GMX Cloud (iterate@gmx.de) OAuth2 Access Token")) {
                return System.getProperties().getProperty("gmxcloud.accesstoken");
            }
            if(user.equals("GMX Cloud (iterate@gmx.de) OAuth2 Refresh Token")) {
                return System.getProperties().getProperty("gmxcloud.refreshtoken");
            }
            return null;
        }

        @Override
        public void addPassword(final Scheme scheme, final int port, final String hostname, final String user, final String password) {
            if(user.equals("GMX Cloud (iterate@gmx.de) OAuth2 Access Token")) {
                System.getProperties().setProperty("gmxcloud.accesstoken", password);
            }
            if(user.equals("GMX Cloud (iterate@gmx.de) OAuth2 Refresh Token")) {
                System.getProperties().setProperty("gmxcloud.refreshtoken", password);
            }
        }
    }


    protected Path createFile(Path file, final byte[] content) throws Exception {
        final GmxcloudResourceIdProvider fileid = new GmxcloudResourceIdProvider(session);
        final GmxcloudWriteFeature feature = new GmxcloudWriteFeature(session, fileid);
        final TransferStatus status = new TransferStatus()
                .withChecksum(new GmxcloudCdash64Compute().compute(new ByteArrayInputStream(content), new TransferStatus().withLength(content.length)))
                .withLength(content.length);
        final HttpResponseOutputStream<GmxcloudUploadHelper.GmxcloudUploadResponse> out = feature.write(file, status, new DisabledConnectionCallback());
        final ByteArrayInputStream in = new ByteArrayInputStream(content);
        final TransferStatus progress = new TransferStatus();
        final BytecountStreamListener count = new BytecountStreamListener();
        new StreamCopier(new TransferStatus(), progress).withListener(count).transfer(in, out);
        assertEquals(content.length, count.getSent());
        in.close();
        out.close();
        return file;
    }
}
