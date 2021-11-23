package ch.cyberduck.core.cloudfront;

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
 * Bug fixes, suggestions and comments should be sent to:
 * dkocher@cyberduck.ch
 */

import ch.cyberduck.core.DefaultWebUrlProvider;
import ch.cyberduck.core.DisabledCancelCallback;
import ch.cyberduck.core.DisabledHostKeyCallback;
import ch.cyberduck.core.DisabledProgressListener;
import ch.cyberduck.core.Host;
import ch.cyberduck.core.LoginCallback;
import ch.cyberduck.core.LoginConnectionService;
import ch.cyberduck.core.PasswordStoreFactory;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.PathNormalizer;
import ch.cyberduck.core.cdn.Distribution;
import ch.cyberduck.core.exception.BackgroundException;
import ch.cyberduck.core.features.Location;
import ch.cyberduck.core.s3.S3Protocol;
import ch.cyberduck.core.s3.S3Session;
import ch.cyberduck.core.ssl.X509KeyManager;
import ch.cyberduck.core.ssl.X509TrustManager;

import org.apache.log4j.Logger;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

public class CustomOriginCloudFrontDistributionConfiguration extends CloudFrontDistributionConfiguration {
    private static final Logger log = Logger.getLogger(CustomOriginCloudFrontDistributionConfiguration.class);

    private final Host origin;

    public CustomOriginCloudFrontDistributionConfiguration(final Host origin,
                                                           final X509TrustManager trust,
                                                           final X509KeyManager key) {
        // Configure with the same host as S3 to get the same credentials from the keychain.
        super(new S3Session(new Host(new S3Protocol(),
            new S3Protocol().getDefaultHostname(), origin.getCdnCredentials()), trust, key), trust, key);
        this.origin = origin;
    }

    private interface Connected<T> extends Callable<T> {
        T call() throws BackgroundException;
    }

    private <T> T connected(final Connected<T> run, final LoginCallback prompt) throws BackgroundException {
        new LoginConnectionService(prompt, new DisabledHostKeyCallback(), PasswordStoreFactory.get(), new DisabledProgressListener())
            .check(session, new DisabledCancelCallback());
        return run.call();
    }

    @Override
    public Distribution read(final Path file, final Distribution.Method method, final LoginCallback prompt) throws BackgroundException {
        return this.connected(new Connected<Distribution>() {
            @Override
            public Distribution call() throws BackgroundException {
                return CustomOriginCloudFrontDistributionConfiguration.super.read(file, method, prompt);
            }
        }, prompt);
    }

    @Override
    public void write(final Path file, final Distribution distribution, final LoginCallback prompt) throws BackgroundException {
        this.connected(new Connected<Void>() {
            @Override
            public Void call() throws BackgroundException {
                CustomOriginCloudFrontDistributionConfiguration.super.write(file, distribution, prompt);
                return null;
            }
        }, prompt);
    }

    @Override
    public List<Distribution.Method> getMethods(final Path container) {
        return Collections.singletonList(Distribution.CUSTOM);
    }

    @Override
    protected URI getOrigin(final Path container, final Distribution.Method method) {
        final URI url = URI.create(String.format("%s%s", new DefaultWebUrlProvider().toUrl(origin).getUrl(), PathNormalizer.normalize(origin.getDefaultPath(), true)));
        if(log.isDebugEnabled()) {
            log.debug(String.format("Use origin %s for distribution %s", url, method));
        }
        return url;
    }

    @Override
    protected Location.Name getRegion(final Path container) {
        return Location.unknown;
    }
}
