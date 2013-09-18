package ch.cyberduck.core.identity;

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

import ch.cyberduck.core.*;
import ch.cyberduck.core.exception.BackgroundException;
import ch.cyberduck.core.exception.LoginFailureException;

import org.apache.log4j.Logger;

import java.util.concurrent.Callable;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClient;
import com.amazonaws.services.identitymanagement.model.*;

/**
 * @version $Id$
 */
public class AWSIdentityConfiguration implements IdentityConfiguration {
    private static final Logger log = Logger.getLogger(AWSIdentityConfiguration.class);

    private Host host;

    private UseragentProvider ua = new PreferencesUseragentProvider();

    private AmazonIdentityManagementClient client;

    /**
     * Prefix in preferences
     */
    private static final String prefix = "iam.";

    public AWSIdentityConfiguration(final Host host) {
        this.host = host;
        final int timeout = Preferences.instance().getInteger("connection.timeout.seconds") * 1000;
        final ClientConfiguration configuration = new ClientConfiguration();
        configuration.setConnectionTimeout(timeout);
        configuration.setSocketTimeout(timeout);
        configuration.setUserAgent(ua.get());
        configuration.setMaxErrorRetry(0);
        configuration.setMaxConnections(1);
        configuration.setProxyHost(ProxyFactory.get().getHTTPSProxyHost(host));
        configuration.setProxyPort(ProxyFactory.get().getHTTPSProxyPort(host));
        // Create new IAM credentials
        client = new AmazonIdentityManagementClient(
                new com.amazonaws.auth.AWSCredentials() {
                    @Override
                    public String getAWSAccessKeyId() {
                        return host.getCredentials().getUsername();
                    }

                    @Override
                    public String getAWSSecretKey() {
                        return host.getCredentials().getPassword();
                    }
                }, configuration
        );
    }

    private static interface Authenticated<T> extends Callable<T> {
        T call() throws BackgroundException;
    }

    private <T> T authenticated(final Authenticated<T> run, final LoginController prompt) throws BackgroundException {
        final LoginOptions options = new LoginOptions();
        options.anonymous = false;
        options.publickey = false;
        try {
            final KeychainLoginService login = new KeychainLoginService(prompt, PasswordStoreFactory.get());
            login.validate(host, LocaleFactory.localizedString("AWS Identity and Access Management", "S3"), options);
            return run.call();
        }
        catch(LoginFailureException failure) {
            prompt.prompt(host.getProtocol(), host.getCredentials(),
                    LocaleFactory.localizedString("Login failed", "Credentials"), failure.getMessage(), options);
            return this.authenticated(run, prompt);
        }
    }

    @Override
    public void delete(final String username, final LoginController prompt) throws BackgroundException {
        this.authenticated(new Authenticated<Void>() {
            @Override
            public Void call() throws BackgroundException {
                Preferences.instance().deleteProperty(String.format("%s%s", prefix, username));
                try {
                    final ListAccessKeysResult keys
                            = client.listAccessKeys(new ListAccessKeysRequest().withUserName(username));

                    for(AccessKeyMetadata key : keys.getAccessKeyMetadata()) {
                        client.deleteAccessKey(new DeleteAccessKeyRequest(key.getAccessKeyId()).withUserName(username));
                    }

                    final ListUserPoliciesResult policies = client.listUserPolicies(new ListUserPoliciesRequest(username));
                    for(String policy : policies.getPolicyNames()) {
                        client.deleteUserPolicy(new DeleteUserPolicyRequest(username, policy));
                    }
                    client.deleteUser(new DeleteUserRequest(username));
                }
                catch(NoSuchEntityException e) {
                    log.warn(String.format("User %s already removed", username));
                }
                catch(AmazonServiceException e) {
                    throw new AmazonServiceExceptionMappingService().map("Cannot write user configuration", e);
                }
                return null;
            }
        }, prompt);
    }

    @Override
    public Credentials getCredentials(final String username) {
        // Resolve access key id
        final String key = Preferences.instance().getProperty(String.format("%s%s", prefix, username));
        if(null == key) {
            log.warn(String.format("No access key found for user %s", username));
            return null;
        }
        return new Credentials(key, PasswordStoreFactory.get().getPassword(host.getProtocol().getScheme(), host.getPort(),
                host.getHostname(), key));
    }

    @Override
    public void create(final String username, final String policy, final LoginController prompt) throws BackgroundException {
        this.authenticated(new Authenticated<Void>() {
            @Override
            public Void call() throws BackgroundException {
                try {
                    // Create new IAM credentials
                    final User user = client.createUser(new CreateUserRequest().withUserName(username)).getUser();
                    final CreateAccessKeyResult key = client.createAccessKey(
                            new CreateAccessKeyRequest().withUserName(user.getUserName()));

                    // Write policy document to get read access
                    client.putUserPolicy(new PutUserPolicyRequest(user.getUserName(), "Policy", policy));
                    // Map virtual user name to IAM access key
                    final String id = key.getAccessKey().getAccessKeyId();
                    Preferences.instance().setProperty(String.format("%s%s", prefix, username), id);
                    // Save secret
                    PasswordStoreFactory.get().addPassword(
                            host.getProtocol().getScheme(), host.getPort(), host.getHostname(),
                            id, key.getAccessKey().getSecretAccessKey());
                }
                catch(AmazonServiceException e) {
                    throw new AmazonServiceExceptionMappingService().map("Cannot write user configuration", e);
                }
                return null;
            }
        }, prompt);
    }
}
