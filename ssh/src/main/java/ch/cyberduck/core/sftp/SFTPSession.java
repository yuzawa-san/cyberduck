package ch.cyberduck.core.sftp;

/*
 *  Copyright (c) 2007 David Kocher. All rights reserved.
 *  http://cyberduck.ch/
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  Bug fixes, suggestions and comments should be sent to:
 *  dkocher@cyberduck.ch
 */

import ch.cyberduck.core.*;
import ch.cyberduck.core.cdn.DistributionConfiguration;
import ch.cyberduck.core.cloudfront.CustomOriginCloudFrontDistributionConfiguration;
import ch.cyberduck.core.exception.BackgroundException;
import ch.cyberduck.core.exception.ChecksumException;
import ch.cyberduck.core.exception.ConnectionCanceledException;
import ch.cyberduck.core.exception.ConnectionRefusedException;
import ch.cyberduck.core.exception.InteroperabilityException;
import ch.cyberduck.core.exception.LocalAccessDeniedException;
import ch.cyberduck.core.exception.LoginCanceledException;
import ch.cyberduck.core.exception.LoginFailureException;
import ch.cyberduck.core.features.*;
import ch.cyberduck.core.preferences.Preferences;
import ch.cyberduck.core.preferences.PreferencesFactory;
import ch.cyberduck.core.proxy.Proxy;
import ch.cyberduck.core.proxy.ProxySocketFactory;
import ch.cyberduck.core.sftp.auth.SFTPAgentAuthentication;
import ch.cyberduck.core.sftp.auth.SFTPChallengeResponseAuthentication;
import ch.cyberduck.core.sftp.auth.SFTPNoneAuthentication;
import ch.cyberduck.core.sftp.auth.SFTPPasswordAuthentication;
import ch.cyberduck.core.sftp.auth.SFTPPublicKeyAuthentication;
import ch.cyberduck.core.sftp.openssh.OpenSSHAgentAuthenticator;
import ch.cyberduck.core.sftp.openssh.OpenSSHCredentialsConfigurator;
import ch.cyberduck.core.sftp.openssh.OpenSSHHostnameConfigurator;
import ch.cyberduck.core.sftp.openssh.OpenSSHIdentitiesOnlyConfigurator;
import ch.cyberduck.core.sftp.openssh.OpenSSHJumpHostConfigurator;
import ch.cyberduck.core.sftp.openssh.OpenSSHPreferredAuthenticationsConfigurator;
import ch.cyberduck.core.sftp.putty.PageantAuthenticator;
import ch.cyberduck.core.ssl.X509KeyManager;
import ch.cyberduck.core.ssl.X509TrustManager;
import ch.cyberduck.core.threading.CancelCallback;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.PublicKey;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import net.schmizz.concurrent.Promise;
import net.schmizz.keepalive.KeepAlive;
import net.schmizz.keepalive.KeepAliveProvider;
import net.schmizz.sshj.Config;
import net.schmizz.sshj.DefaultConfig;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.DisconnectReason;
import net.schmizz.sshj.common.SSHException;
import net.schmizz.sshj.connection.channel.direct.DirectConnection;
import net.schmizz.sshj.sftp.Request;
import net.schmizz.sshj.sftp.Response;
import net.schmizz.sshj.sftp.SFTPEngine;
import net.schmizz.sshj.sftp.SFTPException;
import net.schmizz.sshj.transport.DisconnectListener;
import net.schmizz.sshj.transport.NegotiatedAlgorithms;
import net.schmizz.sshj.transport.Transport;
import net.schmizz.sshj.transport.compression.DelayedZlibCompression;
import net.schmizz.sshj.transport.compression.NoneCompression;
import net.schmizz.sshj.transport.compression.ZlibCompression;
import net.schmizz.sshj.transport.verification.AlgorithmsVerifier;
import net.schmizz.sshj.transport.verification.HostKeyVerifier;

public class SFTPSession extends Session<SSHClient> {
    private static final Logger log = Logger.getLogger(SFTPSession.class);

    private final Preferences preferences
        = PreferencesFactory.get();

    private SFTPEngine sftp;
    private StateDisconnectListener disconnectListener;
    private NegotiatedAlgorithms algorithms;

    private final X509TrustManager trust;
    private final X509KeyManager key;

    public SFTPSession(final Host h, final X509TrustManager trust, final X509KeyManager key) {
        super(h);
        this.trust = trust;
        this.key = key;
    }

    @Override
    public boolean isConnected() {
        if(super.isConnected()) {
            return client.isConnected();
        }
        return false;
    }

    @Override
    protected SSHClient connect(final Proxy proxy, final HostKeyCallback key, final LoginCallback prompt, final CancelCallback cancel) throws BackgroundException {
        final DefaultConfig configuration = new DefaultConfig();
        if("zlib".equals(preferences.getProperty("ssh.compression"))) {
            configuration.setCompressionFactories(Arrays.asList(
                new DelayedZlibCompression.Factory(),
                new ZlibCompression.Factory(),
                new NoneCompression.Factory()));
        }
        else {
            configuration.setCompressionFactories(Collections.singletonList(new NoneCompression.Factory()));
        }
        configuration.setVersion(new PreferencesUseragentProvider().get());
        final KeepAliveProvider heartbeat;
        if(preferences.getProperty("ssh.heartbeat.provider").equals("keep-alive")) {
            heartbeat = KeepAliveProvider.KEEP_ALIVE;
        }
        else {
            heartbeat = KeepAliveProvider.HEARTBEAT;
        }
        configuration.setKeepAliveProvider(heartbeat);
        return this.connect(key, prompt, configuration);
    }

    protected SSHClient connect(final HostKeyCallback key, final LoginCallback prompt, final Config configuration) throws BackgroundException {
        final SSHClient connection = this.toClient(key, configuration);
        try {
            // Look for jump host configuration
            final Host proxy = new OpenSSHJumpHostConfigurator().getJumphost(host.getHostname());
            if(null != proxy) {
                log.info(String.format("Connect using jump host configuration %s", proxy));
                final SSHClient hop = this.toClient(key, configuration);
                hop.connect(proxy.getHostname(), proxy.getPort());
                proxy.setCredentials(new OpenSSHCredentialsConfigurator().configure(proxy));
                // Authenticate with jump host
                this.authenticate(hop, proxy, prompt, new DisabledCancelCallback());
                if(log.isDebugEnabled()) {
                    log.debug(String.format("Authenticated with jump host %s", proxy));
                }
                if(proxy.getCredentials().isSaved()) {
                    // Write credentials to keychain
                    try {
                        PasswordStoreFactory.get().save(proxy);
                    }
                    catch(LocalAccessDeniedException e) {
                        log.error(String.format("Failure saving credentials for %s in keychain. %s", proxy, e));
                    }
                }
                final DirectConnection tunnel = hop.newDirectConnection(
                    new OpenSSHHostnameConfigurator().getHostname(host.getHostname()), host.getPort());
                // Connect to internal host
                connection.connectVia(tunnel);
            }
            else {
                connection.connect(new OpenSSHHostnameConfigurator().getHostname(host.getHostname()), host.getPort());
            }
            final KeepAlive keepalive = connection.getConnection().getKeepAlive();
            keepalive.setKeepAliveInterval(preferences.getInteger("ssh.heartbeat.seconds"));
            return connection;
        }
        catch(IOException e) {
            throw new SFTPExceptionMappingService().map(e);
        }
    }

    private SSHClient toClient(final HostKeyCallback key, final Config configuration) {
        final SSHClient connection = new SSHClient(configuration);
        final int timeout = preferences.getInteger("connection.timeout.seconds") * 1000;
        connection.setTimeout(timeout);
        connection.setSocketFactory(new ProxySocketFactory(host));
        connection.addHostKeyVerifier(new HostKeyVerifier() {
            @Override
            public boolean verify(String hostname, int port, PublicKey publicKey) {
                try {
                    return key.verify(host, publicKey);
                }
                catch(ConnectionCanceledException | ChecksumException e) {
                    return false;
                }
            }
        });
        connection.addAlgorithmsVerifier(new AlgorithmsVerifier() {
            @Override
            public boolean verify(final NegotiatedAlgorithms negotiatedAlgorithms) {
                log.info(String.format("Negotiated algorithms %s", negotiatedAlgorithms));
                algorithms = negotiatedAlgorithms;
                return true;
            }
        });
        connection.setRemoteCharset(Charset.forName(host.getEncoding()));
        disconnectListener = new StateDisconnectListener();
        final Transport transport = connection.getTransport();
        transport.setDisconnectListener(disconnectListener);
        return connection;
    }

    @Override
    public boolean alert(final ConnectionCallback prompt) throws BackgroundException {
        if(null == algorithms) {
            return super.alert(prompt);
        }
        if(!preferences.getBoolean(String.format("ssh.algorithm.whitelist.%s", host.getHostname()))) {
            if(preferences.getList("ssh.algorithm.cipher.blacklist").contains(algorithms.getClient2ServerCipherAlgorithm())) {
                alert(prompt, algorithms.getClient2ServerCipherAlgorithm());
            }
            if(preferences.getList("ssh.algorithm.cipher.blacklist").contains(algorithms.getServer2ClientCipherAlgorithm())) {
                alert(prompt, algorithms.getServer2ClientCipherAlgorithm());
            }
            if(preferences.getList("ssh.algorithm.mac.blacklist").contains(algorithms.getClient2ServerMACAlgorithm())) {
                alert(prompt, algorithms.getClient2ServerMACAlgorithm());
            }
            if(preferences.getList("ssh.algorithm.mac.blacklist").contains(algorithms.getServer2ClientMACAlgorithm())) {
                alert(prompt, algorithms.getServer2ClientMACAlgorithm());
            }
            if(preferences.getList("ssh.algorithm.kex.blacklist").contains(algorithms.getKeyExchangeAlgorithm())) {
                alert(prompt, algorithms.getKeyExchangeAlgorithm());
            }
            if(preferences.getList("ssh.algorithm.signature.blacklist").contains(algorithms.getSignatureAlgorithm())) {
                alert(prompt, algorithms.getSignatureAlgorithm());
            }
        }
        return super.alert(prompt);
    }

    private void alert(final ConnectionCallback prompt, final String algorithm) throws ConnectionCanceledException {
        prompt.warn(host, MessageFormat.format(LocaleFactory.localizedString("Insecure algorithm {0} negotiated with server", "Credentials"),
            algorithm),
            new StringAppender()
                .append(LocaleFactory.localizedString("The algorithm is possibly too weak to meet current cryptography standards", "Credentials"))
                .append(LocaleFactory.localizedString("Please contact your web hosting service provider for assistance", "Support")).toString(),
            LocaleFactory.localizedString("Continue", "Credentials"),
            LocaleFactory.localizedString("Disconnect", "Credentials"),
            String.format("ssh.algorithm.whitelist.%s", host.getHostname()));
    }

    @Override
    public void login(final Proxy proxy, final LoginCallback prompt, final CancelCallback cancel) throws BackgroundException {
        this.authenticate(client, host, prompt, cancel);
        try {
            sftp = new LoggingSFTPEngine(client, this).init();
            sftp.setTimeoutMs(preferences.getInteger("connection.timeout.seconds") * 1000);
        }
        catch(IOException e) {
            throw new SFTPExceptionMappingService().map(e);
        }
    }

    private void authenticate(final SSHClient client, final Host host, final LoginCallback prompt, final CancelCallback cancel) throws BackgroundException {
        final Credentials credentials = host.getCredentials();
        try {
            if(new SFTPNoneAuthentication(client).authenticate(host, prompt, cancel)) {
                return;
            }
        }
        catch(LoginFailureException e) {
            // Expected. The main purpose of sending this request is to get the list of supported methods from the server
        }
        // Ordered list of preferred authentication methods
        final List<AuthenticationProvider<Boolean>> defaultMethods = new ArrayList<>();
        if(preferences.getBoolean("ssh.authentication.agent.enable")) {
            if(new OpenSSHIdentitiesOnlyConfigurator().isIdentitiesOnly(host.getHostname())) {
                log.warn("Skip reading keys from SSH agent with IdentitiesOnly configuration");
            }
            else {
                switch(Factory.Platform.getDefault()) {
                    case windows:
                        defaultMethods.add(new SFTPAgentAuthentication(client, new PageantAuthenticator()));
                        break;
                    default:
                        defaultMethods.add(new SFTPAgentAuthentication(client, new OpenSSHAgentAuthenticator()));
                        break;
                }
            }
        }
        defaultMethods.add(new SFTPPublicKeyAuthentication(client));
        defaultMethods.add(new SFTPChallengeResponseAuthentication(client));
        defaultMethods.add(new SFTPPasswordAuthentication(client));
        final LinkedHashMap<String, List<AuthenticationProvider<Boolean>>> methodsMap = new LinkedHashMap<>();
        defaultMethods.forEach(m -> methodsMap.computeIfAbsent(m.getMethod(), k -> new ArrayList<>()).add(m));
        final List<AuthenticationProvider<Boolean>> methods = new ArrayList<>();
        final String[] preferred = new OpenSSHPreferredAuthenticationsConfigurator().getPreferred(host.getHostname());
        if(preferred != null) {
            if(log.isDebugEnabled()) {
                log.debug(String.format("Filter authentication methods with %s", Arrays.toString(preferred)));
            }
            for(String p : preferred) {
                final List<AuthenticationProvider<Boolean>> providers = methodsMap.remove(p);
                if(providers != null) {
                    methods.addAll(providers);
                }
            }
        }
        methodsMap.values().forEach(methods::addAll);
        if(log.isDebugEnabled()) {
            log.debug(String.format("Attempt login with %d authentication methods %s", methods.size(), Arrays.toString(methods.toArray())));
        }
        BackgroundException lastFailure = null;
        for(AuthenticationProvider<Boolean> auth : methods) {
            cancel.verify();
            try {
                // Obtain latest list of allowed methods
                final Collection<String> allowed = client.getUserAuth().getAllowedMethods();
                if(log.isDebugEnabled()) {
                    log.debug(String.format("Remaining authentication methods %s", Arrays.toString(allowed.toArray())));
                }
                if(!allowed.contains(auth.getMethod())) {
                    log.warn(String.format("Skip authentication method %s not allowed", auth));
                    continue;
                }
                if(log.isInfoEnabled()) {
                    log.info(String.format("Attempt authentication with credentials %s and authentication method %s", credentials, auth));
                }
                try {
                    if(auth.authenticate(host, prompt, cancel)) {
                        if(log.isInfoEnabled()) {
                            log.info(String.format("Authentication succeeded with credentials %s and authentication method %s", credentials, auth));
                        }
                        // Success
                        break;
                    }
                    else {
                        // Check if authentication is partial
                        if(client.getUserAuth().hadPartialSuccess()) {
                            log.info(String.format("Partial login success with credentials %s and authentication method %s", credentials, auth));
                        }
                        else {
                            log.warn(String.format("Login refused with credentials %s and authentication method %s", credentials, auth));
                        }
                        // Continue trying next authentication method
                    }
                }
                catch(LoginFailureException e) {
                    log.warn(String.format("Login failed with credentials %s and authentication method %s", credentials, auth));
                    if(!client.isConnected()) {
                        log.warn(String.format("Server disconnected after failed authentication attempt with method %s", auth));
                        // No more connected. When changing the username the connection is closed by the server.
                        throw e;
                    }
                    lastFailure = e;
                }
            }
            catch(IllegalStateException ignored) {
                log.warn(String.format("Server disconnected with %s while trying authentication method %s",
                    disconnectListener.getFailure(), auth));
                try {
                    if(null == disconnectListener.getFailure()) {
                        throw new ConnectionRefusedException(LocaleFactory.localizedString("Login failed", "Credentials"), ignored);
                    }
                    throw new SFTPExceptionMappingService().map(LocaleFactory.localizedString("Login failed", "Credentials"),
                        disconnectListener.getFailure());
                }
                catch(InteroperabilityException e) {
                    throw new LoginFailureException(e.getDetail(false), e);
                }
            }
            catch(InteroperabilityException e) {
                throw new LoginFailureException(e.getDetail(false), e);
            }
        }
        if(!client.isAuthenticated()) {
            if(null == lastFailure) {
                throw new LoginFailureException(MessageFormat.format(LocaleFactory.localizedString(
                    "Login {0} with username and password", "Credentials"), BookmarkNameProvider.toString(host)));
            }
            throw lastFailure;
        }
        final String banner = client.getUserAuth().getBanner();
        if(StringUtils.isNotBlank(banner)) {
            this.log(Type.response, banner);
        }
    }

    public SFTPEngine sftp() throws LoginCanceledException {
        if(null == sftp) {
            throw new LoginCanceledException();
        }
        return sftp;
    }

    @Override
    protected void logout() throws BackgroundException {
        try {
            if(null == sftp) {
                return;
            }
            sftp.close();
        }
        catch(IOException e) {
            throw new SFTPExceptionMappingService().map(e);
        }
    }

    @Override
    public void disconnect() {
        try {
            client.close();
        }
        catch(IOException e) {
            log.warn(String.format("Ignore disconnect failure %s", e.getMessage()));
        }
        super.disconnect();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T _getFeature(final Class<T> type) {
        if(type == ListService.class) {
            return (T) new SFTPListService(this);
        }
        if(type == Find.class) {
            return (T) new SFTPFindFeature(this);
        }
        if(type == AttributesFinder.class) {
            return (T) new SFTPAttributesFinderFeature(this);
        }
        if(type == Read.class) {
            return (T) new SFTPReadFeature(this);
        }
        if(type == Write.class) {
            return (T) new SFTPWriteFeature(this);
        }
        if(type == Directory.class) {
            return (T) new SFTPDirectoryFeature(this);
        }
        if(type == Delete.class) {
            return (T) new SFTPDeleteFeature(this);
        }
        if(type == Move.class) {
            return (T) new SFTPMoveFeature(this);
        }
        if(type == UnixPermission.class) {
            return (T) new SFTPUnixPermissionFeature(this);
        }
        if(type == Timestamp.class) {
            return (T) new SFTPTimestampFeature(this);
        }
        if(type == Touch.class) {
            return (T) new SFTPTouchFeature(this);
        }
        if(type == Symlink.class) {
            return (T) new SFTPSymlinkFeature(this);
        }
        if(type == Command.class) {
            return (T) new SFTPCommandFeature(this);
        }
        if(type == Compress.class) {
            return (T) new SFTPCompressFeature(this);
        }
        if(type == DistributionConfiguration.class) {
            return (T) new CustomOriginCloudFrontDistributionConfiguration(host, trust, key);
        }
        if(type == Home.class) {
            return (T) new SFTPHomeDirectoryService(this);
        }
        if(type == Quota.class) {
            return (T) new SFTPQuotaFeature(this);
        }
        return super._getFeature(type);
    }

    private static final class StateDisconnectListener implements DisconnectListener {
        private SSHException failure;

        @Override
        public void notifyDisconnect(final DisconnectReason reason, final String message) {
            log.warn(String.format("Disconnected %s", reason));
            failure = new SSHException(reason, message);
        }

        /**
         * @return Last disconnect reason
         */
        public SSHException getFailure() {
            return failure;
        }
    }

    private static final class LoggingSFTPEngine extends SFTPEngine {
        private final TranscriptListener transcript;

        public LoggingSFTPEngine(final SSHClient client, final TranscriptListener transcript) throws SSHException {
            super(client, String.valueOf(Path.DELIMITER));
            this.transcript = transcript;
        }

        @Override
        public Promise<Response, SFTPException> request(final Request req) throws IOException {
            transcript.log(Type.request, String.format("%d %s", req.getRequestID(), req.getType()));
            return super.request(req);
        }
    }
}
