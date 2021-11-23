package ch.cyberduck.core.azure;

/*
 * Copyright (c) 2002-2014 David Kocher. All rights reserved.
 * http://cyberduck.io/
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
 * Bug fixes, suggestions and comments should be sent to:
 * feedback@cyberduck.io
 */

import ch.cyberduck.core.CancellingListProgressListener;
import ch.cyberduck.core.DisabledHostKeyCallback;
import ch.cyberduck.core.Host;
import ch.cyberduck.core.HostKeyCallback;
import ch.cyberduck.core.ListService;
import ch.cyberduck.core.LoginCallback;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.PreferencesUseragentProvider;
import ch.cyberduck.core.Scheme;
import ch.cyberduck.core.exception.BackgroundException;
import ch.cyberduck.core.exception.ListCanceledException;
import ch.cyberduck.core.exception.LoginFailureException;
import ch.cyberduck.core.features.AclPermission;
import ch.cyberduck.core.features.AttributesFinder;
import ch.cyberduck.core.features.Copy;
import ch.cyberduck.core.features.Delete;
import ch.cyberduck.core.features.Directory;
import ch.cyberduck.core.features.Find;
import ch.cyberduck.core.features.Headers;
import ch.cyberduck.core.features.Logging;
import ch.cyberduck.core.features.Metadata;
import ch.cyberduck.core.features.Move;
import ch.cyberduck.core.features.PromptUrlProvider;
import ch.cyberduck.core.features.Read;
import ch.cyberduck.core.features.Touch;
import ch.cyberduck.core.features.Write;
import ch.cyberduck.core.http.DisabledX509HostnameVerifier;
import ch.cyberduck.core.proxy.Proxy;
import ch.cyberduck.core.shared.DefaultHomeFinderService;
import ch.cyberduck.core.ssl.CustomTrustSSLProtocolSocketFactory;
import ch.cyberduck.core.ssl.DefaultX509KeyManager;
import ch.cyberduck.core.ssl.DisabledX509TrustManager;
import ch.cyberduck.core.ssl.SSLSession;
import ch.cyberduck.core.ssl.X509KeyManager;
import ch.cyberduck.core.ssl.X509TrustManager;
import ch.cyberduck.core.threading.CancelCallback;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;

import com.microsoft.azure.storage.OperationContext;
import com.microsoft.azure.storage.RetryNoRetry;
import com.microsoft.azure.storage.SendingRequestEvent;
import com.microsoft.azure.storage.StorageCredentials;
import com.microsoft.azure.storage.StorageCredentialsAccountAndKey;
import com.microsoft.azure.storage.StorageCredentialsSharedAccessSignature;
import com.microsoft.azure.storage.StorageEvent;
import com.microsoft.azure.storage.blob.BlobRequestOptions;
import com.microsoft.azure.storage.blob.CloudBlobClient;

public class AzureSession extends SSLSession<CloudBlobClient> {
    private static final Logger log = Logger.getLogger(AzureSession.class);

    private final OperationContext context
        = new OperationContext();

    private StorageEvent<SendingRequestEvent> listener;

    public AzureSession(final Host h) {
        super(h, new DisabledX509TrustManager(), new DefaultX509KeyManager());
    }

    public AzureSession(final Host h, final X509TrustManager trust, final X509KeyManager key) {
        super(h, trust, key);
    }

    static {
        HttpsURLConnection.setDefaultSSLSocketFactory(new CustomTrustSSLProtocolSocketFactory(new DisabledX509TrustManager(), new DefaultX509KeyManager()));
        HttpsURLConnection.setDefaultHostnameVerifier(new DisabledX509HostnameVerifier());
        HttpsURLConnection.setFollowRedirects(true);
    }

    @Override
    protected CloudBlobClient connect(final Proxy proxy, final HostKeyCallback callback, final LoginCallback prompt, final CancelCallback cancel) throws BackgroundException {
        try {
            final StorageCredentials credentials;
            if(host.getCredentials().isTokenAuthentication()) {
                credentials = new StorageCredentialsSharedAccessSignature(host.getCredentials().getToken());
            }
            else {
                credentials = new StorageCredentialsAccountAndKey(host.getCredentials().getUsername(), "null");
            }
            // Client configured with no credentials
            final URI uri = new URI(String.format("%s://%s", Scheme.https, host.getHostname()));
            final CloudBlobClient client = new CloudBlobClient(uri, credentials);
            client.setDirectoryDelimiter(String.valueOf(Path.DELIMITER));
            final BlobRequestOptions options = new BlobRequestOptions();
            options.setRetryPolicyFactory(new RetryNoRetry());
            context.setLoggingEnabled(true);
            context.setLogger(LoggerFactory.getLogger(log.getName()));
            context.setUserHeaders(new HashMap<>(Collections.singletonMap(
                HttpHeaders.USER_AGENT, new PreferencesUseragentProvider().get()))
            );
            context.getSendingRequestEventHandler().addListener(listener = new StorageEvent<SendingRequestEvent>() {
                @Override
                public void eventOccurred(final SendingRequestEvent event) {
                    if(event.getConnectionObject() instanceof HttpsURLConnection) {
                        final HttpsURLConnection connection = (HttpsURLConnection) event.getConnectionObject();
                        connection.setSSLSocketFactory(new CustomTrustSSLProtocolSocketFactory(trust, key));
                        connection.setHostnameVerifier(new DisabledX509HostnameVerifier());
                    }
                }
            });
            switch(proxy.getType()) {
                case SOCKS: {
                    if(log.isInfoEnabled()) {
                        log.info(String.format("Configured to use SOCKS proxy %s", proxy));
                    }
                    final java.net.Proxy socksProxy = new java.net.Proxy(
                        java.net.Proxy.Type.SOCKS, new InetSocketAddress(proxy.getHostname(), proxy.getPort()));
                    context.setProxy(socksProxy);
                    break;
                }
                case HTTP:
                case HTTPS: {
                    if(log.isInfoEnabled()) {
                        log.info(String.format("Configured to use HTTP proxy %s", proxy));
                    }
                    final java.net.Proxy httpProxy = new java.net.Proxy(
                        java.net.Proxy.Type.HTTP, new InetSocketAddress(proxy.getHostname(), proxy.getPort()));
                    context.setProxy(httpProxy);
                    break;
                }
            }
            return client;
        }
        catch(URISyntaxException e) {
            throw new LoginFailureException(e.getMessage(), e);
        }
    }

    @Override
    public void login(final Proxy proxy, final LoginCallback prompt, final CancelCallback cancel) throws BackgroundException {
        final StorageCredentials credentials = client.getCredentials();
        if(host.getCredentials().isPasswordAuthentication()) {
            // Update credentials
            ((StorageCredentialsAccountAndKey) credentials).updateKey(host.getCredentials().getPassword());
        }
        else if(host.getCredentials().isTokenAuthentication()) {
            if(!StringUtils.equals(host.getCredentials().getToken(), ((StorageCredentialsSharedAccessSignature) credentials).getToken())) {
                this.interrupt();
                this.open(proxy, new DisabledHostKeyCallback(), prompt, cancel);
            }
        }
        // Fetch reference for directory to check login credentials
        try {
            this.getFeature(ListService.class).list(new DefaultHomeFinderService(this).find(), new CancellingListProgressListener());
        }
        catch(ListCanceledException e) {
            // Success
        }
    }

    @Override
    protected void logout() {
        context.getSendingRequestEventHandler().removeListener(listener);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T _getFeature(final Class<T> type) {
        if(type == ListService.class) {
            return (T) new AzureListService(this, context);
        }
        if(type == Read.class) {
            return (T) new AzureReadFeature(this, context);
        }
        if(type == Write.class) {
            return (T) new AzureWriteFeature(this, context);
        }
        if(type == Directory.class) {
            return (T) new AzureDirectoryFeature(this, context);
        }
        if(type == Delete.class) {
            return (T) new AzureDeleteFeature(this, context);
        }
        if(type == Headers.class) {
            return (T) new AzureMetadataFeature(this, context);
        }
        if(type == Metadata.class) {
            return (T) new AzureMetadataFeature(this, context);
        }
        if(type == Find.class) {
            return (T) new AzureFindFeature(this, context);
        }
        if(type == AttributesFinder.class) {
            return (T) new AzureAttributesFinderFeature(this, context);
        }
        if(type == Logging.class) {
            return (T) new AzureLoggingFeature(this, context);
        }
        if(type == Move.class) {
            return (T) new AzureMoveFeature(this, context);
        }
        if(type == Copy.class) {
            return (T) new AzureCopyFeature(this, context);
        }
        if(type == Touch.class) {
            return (T) new AzureTouchFeature(this, context);
        }
        if(type == PromptUrlProvider.class) {
            return (T) new AzureUrlProvider(this);
        }
        if(type == AclPermission.class) {
            return (T) new AzureAclPermissionFeature(this, context);
        }
        return super._getFeature(type);
    }
}
