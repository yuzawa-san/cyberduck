package ch.cyberduck.core.brick;

/*
 * Copyright (c) 2002-2019 iterate GmbH. All rights reserved.
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

import ch.cyberduck.core.Credentials;
import ch.cyberduck.core.DefaultIOExceptionMappingService;
import ch.cyberduck.core.Host;
import ch.cyberduck.core.HostUrlProvider;
import ch.cyberduck.core.PasswordCallback;
import ch.cyberduck.core.exception.BackgroundException;
import ch.cyberduck.core.exception.ConnectionCanceledException;
import ch.cyberduck.core.exception.LoginCanceledException;
import ch.cyberduck.core.exception.LoginFailureException;
import ch.cyberduck.core.http.DefaultHttpResponseExceptionMappingService;
import ch.cyberduck.core.preferences.Preferences;
import ch.cyberduck.core.preferences.PreferencesFactory;
import ch.cyberduck.core.threading.CancelCallback;
import ch.cyberduck.core.threading.ScheduledThreadPool;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.AbstractResponseHandler;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.concurrent.TimeUnit;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class BrickPairingSchedulerFeature {
    private static final Logger log = Logger.getLogger(BrickPairingSchedulerFeature.class);

    private final BrickSession session;
    private final String token;
    private final Host host;
    private final CancelCallback cancel;
    private final ScheduledThreadPool scheduler = new ScheduledThreadPool();

    private final Preferences preferences = PreferencesFactory.get();

    public BrickPairingSchedulerFeature(final BrickSession session, final String token, final Host host, final CancelCallback cancel) {
        this.session = session;
        this.token = token;
        this.host = host;
        this.cancel = cancel;
    }

    public Credentials repeat(final PasswordCallback callback) {
        final long timeout = preferences.getLong("brick.pairing.interrupt.ms");
        final long start = System.currentTimeMillis();
        scheduler.repeat(() -> {
            try {
                if(System.currentTimeMillis() - start > timeout) {
                    throw new ConnectionCanceledException(String.format("Interrupt polling for pairing key after %d", timeout));
                }
                this.operate(callback);
            }
            catch(ConnectionCanceledException e) {
                log.warn("Cancel processing scheduled task. %s", e);
                callback.close(null);
                this.shutdown();
            }
            catch(BackgroundException e) {
                log.warn(String.format("Failure processing scheduled task. %s", e.getMessage()), e);
                callback.close(null);
                this.shutdown();
            }
        }, preferences.getLong("brick.pairing.interval.ms"), TimeUnit.MILLISECONDS);
        return null;
    }

    /**
     * Pool for pairing key from service
     *
     * @param callback Callback when service returns 200
     */
    private void operate(final PasswordCallback callback) throws BackgroundException {
        try {
            final HttpPost resource = new HttpPost(String.format("%s/api/rest/v1/sessions/pairing_key/%s",
                new HostUrlProvider().withUsername(false).withPath(false).get(session.getHost()), token));
            resource.setHeader(HttpHeaders.ACCEPT, "application/json");
            resource.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            if(log.isInfoEnabled()) {
                log.info(String.format("Fetch credentials for paring key %s from %s", token, resource));
            }
            final JsonObject json = session.getClient().execute(resource, new AbstractResponseHandler<JsonObject>() {
                @Override
                public JsonObject handleEntity(final HttpEntity entity) throws IOException {
                    final ByteArrayOutputStream out = new ByteArrayOutputStream();
                    IOUtils.copy(entity.getContent(), out);
                    return JsonParser.parseReader(new InputStreamReader(new ByteArrayInputStream(out.toByteArray()))).getAsJsonObject();
                }
            });
            if(json.has("nickname")) {
                if(preferences.getBoolean("brick.pairing.nickname.configure")) {
                    final JsonPrimitive nickname = json.getAsJsonPrimitive("nickname");
                    if(StringUtils.isNotBlank(host.getNickname())) {
                        if(!StringUtils.equals(host.getNickname(), nickname.getAsString())) {
                            log.warn(String.format("Mismatch of nickname. Previously authorized as %s and now paired as %s",
                                host.getNickname(), nickname.getAsString()));
                            callback.close(null);
                            throw new LoginCanceledException();
                        }
                    }
                    host.setNickname(nickname.getAsString());
                }
            }
            final Credentials credentials = host.getCredentials();
            if(json.has("username")) {
                credentials.setUsername(json.getAsJsonPrimitive("username").getAsString());
            }
            else {
                throw new LoginFailureException(String.format("Invalid response for pairing key %s", token));
            }
            if(json.has("password")) {
                credentials.setPassword(json.getAsJsonPrimitive("password").getAsString());
            }
            else {
                throw new LoginFailureException(String.format("Invalid response for pairing key %s", token));
            }
            if(json.has("server")) {
                if(preferences.getBoolean("brick.pairing.hostname.configure")) {
                    host.setHostname(URI.create(json.getAsJsonPrimitive("server").getAsString()).getHost());
                }
            }
            callback.close(credentials.getUsername());
        }
        catch(JsonParseException e) {
            throw new DefaultIOExceptionMappingService().map(new IOException(e.getMessage(), e));
        }
        catch(HttpResponseException e) {
            switch(e.getStatusCode()) {
                case HttpStatus.SC_NOT_FOUND:
                    log.warn(String.format("Missing login for pairing key %s", token));
                    cancel.verify();
                    break;
                default:
                    throw new DefaultHttpResponseExceptionMappingService().map(e);
            }
        }
        catch(IOException e) {
            throw new DefaultIOExceptionMappingService().map(e);
        }
    }

    public void shutdown() {
        scheduler.shutdown();
    }
}
