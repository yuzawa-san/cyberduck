package ch.cyberduck.core.freenet;/*
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

import ch.cyberduck.core.CertificateStoreFactory;
import ch.cyberduck.core.DescriptiveUrl;
import ch.cyberduck.core.DescriptiveUrlBag;
import ch.cyberduck.core.DisabledCertificateIdentityCallback;
import ch.cyberduck.core.DisabledCertificateTrustCallback;
import ch.cyberduck.core.DisabledLoginCallback;
import ch.cyberduck.core.DisabledTranscriptListener;
import ch.cyberduck.core.Host;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.PreferencesUseragentProvider;
import ch.cyberduck.core.UrlProvider;
import ch.cyberduck.core.dav.DAVSSLProtocol;
import ch.cyberduck.core.exception.BackgroundException;
import ch.cyberduck.core.http.HttpConnectionPoolBuilder;
import ch.cyberduck.core.http.HttpExceptionMappingService;
import ch.cyberduck.core.http.UserAgentHttpRequestInitializer;
import ch.cyberduck.core.proxy.Proxy;
import ch.cyberduck.core.proxy.ProxyFactory;
import ch.cyberduck.core.ssl.DefaultTrustManagerHostnameCallback;
import ch.cyberduck.core.ssl.KeychainX509KeyManager;
import ch.cyberduck.core.ssl.KeychainX509TrustManager;
import ch.cyberduck.core.ssl.ThreadLocalHostnameDelegatingTrustManager;
import ch.cyberduck.core.ssl.X509KeyManager;
import ch.cyberduck.core.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.AbstractResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.PasswordTokenRequest;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.http.BasicAuthentication;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.apache.v2.ApacheHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

public class FreenetAuthenticatedUrlProvider implements UrlProvider {
    private static final Logger log = Logger.getLogger(FreenetAuthenticatedUrlProvider.class);

    private final Host bookmark;

    public FreenetAuthenticatedUrlProvider(final Host bookmark) {
        this.bookmark = bookmark;
    }

    @Override
    public DescriptiveUrlBag toUrl(final Path file) {
        try {
            // Run password flow
            final TokenResponse response;
            try {
                final Host target = new Host(new DAVSSLProtocol(), "oauth.freenet.de");
                final X509TrustManager trust = new KeychainX509TrustManager(new DisabledCertificateTrustCallback(),
                    new DefaultTrustManagerHostnameCallback(target), CertificateStoreFactory.get());
                final X509KeyManager key = new KeychainX509KeyManager(new DisabledCertificateIdentityCallback(), target,
                    CertificateStoreFactory.get());
                final CloseableHttpClient client = new HttpConnectionPoolBuilder(
                    target, new ThreadLocalHostnameDelegatingTrustManager(trust, target.getHostname()), key, ProxyFactory.get()
                )
                    .build(Proxy.DIRECT, new DisabledTranscriptListener(), new DisabledLoginCallback()).build();
                response = new PasswordTokenRequest(new ApacheHttpTransport(client),
                    new GsonFactory(), new GenericUrl("https://oauth.freenet.de/oauth/token"),
                    bookmark.getCredentials().getUsername(), bookmark.getCredentials().getPassword()
                )
                    .setClientAuthentication(new BasicAuthentication("desktop_client", "6LIGIHuOSkznLomu5xw0EPPBJOXb2jLp"))
                    .setRequestInitializer(new UserAgentHttpRequestInitializer(new PreferencesUseragentProvider()))
                    .execute();
                final FreenetTemporaryLoginResponse login = this.getLoginSession(client, response.getAccessToken());
                return new DescriptiveUrlBag(Collections.singletonList(
                    new DescriptiveUrl(URI.create(login.urls.login), DescriptiveUrl.Type.authenticated)));
            }
            catch(IOException e) {
                throw new HttpExceptionMappingService().map(e);
            }
        }
        catch(BackgroundException e) {
            log.warn(String.format("Failure %s retrieving authenticated URL for %s", e, file));
            return DescriptiveUrlBag.empty();
        }
    }

    private FreenetTemporaryLoginResponse getLoginSession(final HttpClient client, final String token) throws BackgroundException {
        final HttpGet request = new HttpGet("https://api.mail.freenet.de/v2.0/hash/create");
        request.addHeader("Token", token);
        try {
            return client.execute(request, new AbstractResponseHandler<FreenetTemporaryLoginResponse>() {
                @Override
                public FreenetTemporaryLoginResponse handleEntity(final HttpEntity entity) throws IOException {
                    final ObjectMapper mapper = new ObjectMapper();
                    return mapper.readValue(entity.getContent(), FreenetTemporaryLoginResponse.class);
                }
            });
        }
        catch(IOException e) {
            throw new HttpExceptionMappingService().map(e);
        }
    }
}
