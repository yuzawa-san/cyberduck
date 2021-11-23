package ch.cyberduck.core.s3;

/*
 * Copyright (c) 2002-2018 iterate GmbH. All rights reserved.
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

import ch.cyberduck.core.Host;
import ch.cyberduck.core.LoginCallback;
import ch.cyberduck.core.exception.ExpiredTokenException;
import ch.cyberduck.core.exception.LoginCanceledException;
import ch.cyberduck.core.exception.LoginFailureException;
import ch.cyberduck.core.http.DisabledServiceUnavailableRetryStrategy;
import ch.cyberduck.core.ssl.X509KeyManager;
import ch.cyberduck.core.ssl.X509TrustManager;
import ch.cyberduck.core.sts.STSCredentialsConfigurator;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.jets3t.service.S3ServiceException;

import java.io.IOException;

public class S3TokenExpiredResponseInterceptor extends DisabledServiceUnavailableRetryStrategy {
    private static final Logger log = Logger.getLogger(S3TokenExpiredResponseInterceptor.class);

    private static final int MAX_RETRIES = 1;

    private final X509TrustManager trust;
    private final X509KeyManager key;
    private final LoginCallback prompt;
    private final Host host;

    public S3TokenExpiredResponseInterceptor(final S3Session session, final X509TrustManager trust, final X509KeyManager key, final LoginCallback prompt) {
        this.host = session.getHost();
        this.trust = trust;
        this.key = key;
        this.prompt = prompt;
    }

    @Override
    public boolean retryRequest(final HttpResponse response, final int executionCount, final HttpContext context) {
        if(executionCount <= MAX_RETRIES) {
            switch(response.getStatusLine().getStatusCode()) {
                case HttpStatus.SC_BAD_REQUEST:
                    final S3ServiceException failure;
                    try {
                        if(null != response.getEntity()) {
                            EntityUtils.updateEntity(response, new BufferedHttpEntity(response.getEntity()));
                            failure = new S3ServiceException(response.getStatusLine().getReasonPhrase(),
                                    EntityUtils.toString(response.getEntity()));
                            if(new S3ExceptionMappingService().map(failure) instanceof ExpiredTokenException) {
                                try {
                                    host.setCredentials(new STSCredentialsConfigurator(trust, key, prompt).configure(host));
                                    return true;
                                }
                                catch(LoginFailureException | LoginCanceledException e) {
                                    log.warn(String.format("Attempt to renew expired token failed. %s", e.getMessage()));
                                }
                            }
                        }
                    }
                    catch(IOException e) {
                        log.warn(String.format("Failure parsing response entity from %s", response));
                    }
            }
        }
        return false;
    }
}
