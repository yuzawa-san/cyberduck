package ch.cyberduck.core.sts;

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

import ch.cyberduck.core.AsciiRandomStringService;
import ch.cyberduck.core.Credentials;
import ch.cyberduck.core.Host;
import ch.cyberduck.core.Local;
import ch.cyberduck.core.LocalFactory;
import ch.cyberduck.core.LocaleFactory;
import ch.cyberduck.core.LoginOptions;
import ch.cyberduck.core.PasswordCallback;
import ch.cyberduck.core.aws.CustomClientConfiguration;
import ch.cyberduck.core.exception.AccessDeniedException;
import ch.cyberduck.core.exception.LoginCanceledException;
import ch.cyberduck.core.exception.LoginFailureException;
import ch.cyberduck.core.ssl.ThreadLocalHostnameDelegatingTrustManager;
import ch.cyberduck.core.ssl.X509KeyManager;
import ch.cyberduck.core.ssl.X509TrustManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Predicate;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSSessionCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.internal.AbstractProfilesConfigFileScanner;
import com.amazonaws.auth.profile.internal.AllProfiles;
import com.amazonaws.auth.profile.internal.BasicProfile;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import com.amazonaws.services.securitytoken.model.AWSSecurityTokenServiceException;
import com.amazonaws.services.securitytoken.model.AssumeRoleRequest;
import com.amazonaws.services.securitytoken.model.AssumeRoleResult;
import com.amazonaws.services.securitytoken.model.GetSessionTokenRequest;
import com.amazonaws.services.securitytoken.model.GetSessionTokenResult;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;


public class STSCredentialsConfigurator {
    private static final Logger log = Logger.getLogger(STSCredentialsConfigurator.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();
    static {
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private final X509TrustManager trust;
    private final X509KeyManager key;
    private final PasswordCallback prompt;

    public STSCredentialsConfigurator(final X509TrustManager trust, final X509KeyManager key, final PasswordCallback prompt) {
        this.trust = trust;
        this.key = key;
        this.prompt = prompt;
    }

    public Credentials configure(final Host host) throws LoginFailureException, LoginCanceledException {
        final Credentials credentials = new Credentials(host.getCredentials());
        // Find matching profile name or AWS access key in ~/.aws/credentials
        final Local file = LocalFactory.get(LocalFactory.get(LocalFactory.get(), ".aws"), "credentials");
        // Profile can be null. The default profile from the configuration will be loaded
        final String profile = host.getCredentials().getUsername();
        if(log.isDebugEnabled()) {
            log.debug(String.format("Look for profile name %s in %s and %s", profile, configFile, credentialsFile));
        }
        // Iterating all profiles on our own because AWSProfileCredentialsConfigurator does not support MFA tokens
        final Map<String, Map<String, String>> allProfileProperties = new HashMap<>();
        try {
            final Map<String, Map<String, String>> credentialsFileProfileProperties = new ProfilesConfigFileLoaderHelper()
                .parseProfileProperties(credentialsFile);
            allProfileProperties.putAll(credentialsFileProfileProperties);
            final Map<String, Map<String, String>> configFileProfileProperties = new ProfilesConfigFileLoaderHelper()
                .parseProfileProperties(configFile);
            for(Map.Entry<String, Map<String, String>> entry : configFileProfileProperties.entrySet()) {
                final String profileName = entry.getKey();
                final Map<String, String> configFileProperties = entry.getValue();
                Map<String, String> credentialsFileProperties = allProfileProperties.get(profileName);
                // If the credentials file had properties, then merge them in
                if(credentialsFileProperties != null){ 
                    configFileProperties.putAll(credentialsFileProperties);
                }
                allProfileProperties.put(profileName, configFileProperties);
            }
        }
        catch(AccessDeniedException | IllegalArgumentException | IOException e) {
            log.warn(String.format("Failure reading %s and ", configFile, credentialsFile), e);
            return credentials;
        }
        if(allProfileProperties.isEmpty()) {
            log.warn("Missing configuration file ~/.aws/credentials or ~/.aws/config. Skip auto configuration");
            return host.getCredentials();
        }
        // Convert the loaded property map to credential objects
        final Map<String, BasicProfile> profilesByName = new LinkedHashMap<>();
        for(Map.Entry<String, Map<String, String>> entry : allProfileProperties.entrySet()) {
            String profileName = entry.getKey();
            Map<String, String> properties = entry.getValue();
            profilesByName.put(profileName, new BasicProfile(profileName, properties));
        }
        final Map<String, BasicProfile> profiles = new AllProfiles(profilesByName).getProfiles();
        final Optional<Map.Entry<String, BasicProfile>> optional = profiles.entrySet().stream().filter(new Predicate<Map.Entry<String, BasicProfile>>() {
            @Override
            public boolean test(final Map.Entry<String, BasicProfile> entry) {
                final String profileName = entry.getKey();
                final BasicProfile basicProfile = entry.getValue();
                final String awsAccessIdKey = basicProfile.getAwsAccessIdKey();
                // Matching access key or profile name
                if(StringUtils.equals(profileName, profile) || StringUtils.equals(awsAccessIdKey, profile)) {
                    if(log.isDebugEnabled()) {
                        log.debug(String.format("Found matching profile %s", profile));
                    }
                    return true;
                }
                return false;
            }
        }).findFirst();
        if(optional.isPresent()) {
            final Map.Entry<String, BasicProfile> entry = optional.get();
            final BasicProfile basicProfile = entry.getValue();
            if(basicProfile.isRoleBasedProfile()) {
                if(log.isDebugEnabled()) {
                    log.debug(String.format("Configure credentials from role based profile %s", basicProfile.getProfileName()));
                }
                if(StringUtils.isBlank(basicProfile.getRoleSourceProfile())) {
                    throw new LoginFailureException(String.format("Missing source profile reference in profile %s", basicProfile.getProfileName()));
                }
                else if(!profiles.containsKey(basicProfile.getRoleSourceProfile())) {
                    throw new LoginFailureException(String.format("Missing source profile with name %s", basicProfile.getRoleSourceProfile()));
                }
                else {
                    final BasicProfile sourceProfile = profiles.get(basicProfile.getRoleSourceProfile());
                    // If a profile defines the role_arn property then the profile is treated as an assume role profile
                    final AWSSecurityTokenService service = this.getTokenService(host,
                        host.getRegion(),
                        sourceProfile.getAwsAccessIdKey(), sourceProfile.getAwsSecretAccessKey(), sourceProfile.getAwsSessionToken());
                    final String tokenCode;
                    if(basicProfile.getProperties().containsKey("mfa_serial")) {
                        tokenCode = prompt.prompt(
                            host, LocaleFactory.localizedString("Provide additional login credentials", "Credentials"),
                            String.format("%s %s", LocaleFactory.localizedString("Multi-Factor Authentication", "S3"),
                                basicProfile.getPropertyValue("mfa_serial")),
                            new LoginOptions(host.getProtocol())
                                .password(true)
                                .passwordPlaceholder(LocaleFactory.localizedString("MFA Authentication Code", "S3"))
                                .keychain(false)
                        ).getPassword();
                    }
                    else {
                        tokenCode = null;
                    }
                    final Integer durationSeconds;
                    if(basicProfile.getProperties().containsKey("duration_seconds")) {
                        durationSeconds = Integer.valueOf(basicProfile.getPropertyValue("duration_seconds"));
                    }
                    else {
                        durationSeconds = null;
                    }
                    // Starts a new session by sending a request to the AWS Security Token Service (STS) to assume a
                    // Role using the long lived AWS credentials
                    final AssumeRoleRequest assumeRoleRequest = new AssumeRoleRequest()
                        .withExternalId(basicProfile.getRoleExternalId())
                        .withRoleArn(basicProfile.getRoleArn())
                        // Specify this value if the IAM user has a policy that requires MFA authentication
                        .withSerialNumber(basicProfile.getPropertyValue("mfa_serial"))
                        // The value provided by the MFA device, if MFA is required
                        .withTokenCode(tokenCode
                            // mfa_serial - The identification number of the MFA device to use when assuming a role. This is an optional parameter.
                            // Specify this value if the trust policy of the role being assumed includes a condition that requires MFA authentication.
                            // The value is either the serial number for a hardware device (such as GAHT12345678) or an Amazon Resource Name (ARN) for
                            // a virtual device (such as arn:aws:iam::123456789012:mfa/user).
                        )
                        .withRoleSessionName(new AsciiRandomStringService().random())
                        .withDurationSeconds(durationSeconds
                            // duration_seconds - Specifies the maximum duration of the role session, in seconds. The value can range from 900 seconds
                            // (15 minutes) up to the maximum session duration setting for the role (which can be a maximum of 43200). This is an
                            // optional parameter and by default, the value is set to 3600 seconds.
                        );
                    if(log.isDebugEnabled()) {
                        log.debug(String.format("Request %s from %s", assumeRoleRequest, service));
                    }
                    try {
                        final AssumeRoleResult assumeRoleResult = service.assumeRole(assumeRoleRequest);
                        if(log.isDebugEnabled()) {
                            log.debug(String.format("Set credentials from %s", assumeRoleResult));
                        }
                        credentials.setUsername(assumeRoleResult.getCredentials().getAccessKeyId());
                        credentials.setPassword(assumeRoleResult.getCredentials().getSecretAccessKey());
                        credentials.setToken(assumeRoleResult.getCredentials().getSessionToken());
                    }
                    catch(AWSSecurityTokenServiceException e) {
                        throw new LoginFailureException(e.getErrorMessage(), e);
                    }
                }
            }
            else {
                if(log.isDebugEnabled()) {
                    log.debug(String.format("Configure credentials from basic profile %s", basicProfile.getProfileName()));
                }
                final Map<String, String> profileProperties = basicProfile.getProperties();
                if(profileProperties.containsKey("sso_start_url")) {
                    // Read cached SSO credentials
                    fetchSsoCredentials(credentials, profileProperties, awsDirectory);
                }
                else if(StringUtils.isNotBlank(basicProfile.getAwsSessionToken())) {
                    // No need to obtain session token if preconfigured in profile
                    if(log.isDebugEnabled()) {
                        log.debug(String.format("Set session token credentials from profile %s", profile));
                    }
                    credentials.setUsername(basicProfile.getAwsAccessIdKey());
                    credentials.setPassword(basicProfile.getAwsSecretAccessKey());
                    credentials.setToken(basicProfile.getAwsSessionToken());
                }
                else {
                    if(host.getProtocol().isTokenConfigurable()) {
                        // Obtain session token
                        if(log.isDebugEnabled()) {
                            log.debug(String.format("Get session token from credentials in profile %s", basicProfile.getProfileName()));
                        }
                        final AWSSecurityTokenService service = this.getTokenService(host,
                            host.getRegion(),
                            basicProfile.getAwsAccessIdKey(), basicProfile.getAwsSecretAccessKey(), basicProfile.getAwsSessionToken());
                        final GetSessionTokenRequest sessionTokenRequest = new GetSessionTokenRequest();
                        if(log.isDebugEnabled()) {
                            log.debug(String.format("Request %s from %s", sessionTokenRequest, service));
                        }
                        try {
                            final GetSessionTokenResult sessionTokenResult = service.getSessionToken(sessionTokenRequest);
                            if(log.isDebugEnabled()) {
                                log.debug(String.format("Set credentials from %s", sessionTokenResult));
                            }
                            credentials.setUsername(sessionTokenResult.getCredentials().getAccessKeyId());
                            credentials.setPassword(sessionTokenResult.getCredentials().getSecretAccessKey());
                            credentials.setToken(sessionTokenResult.getCredentials().getSessionToken());
                        }
                        catch(AWSSecurityTokenServiceException e) {
                            throw new LoginFailureException(e.getErrorMessage(), e);
                        }
                    }
                    else {
                        if(log.isDebugEnabled()) {
                            log.debug(String.format("Set static credentials from profile %s", basicProfile.getProfileName()));
                        }
                        credentials.setUsername(basicProfile.getAwsAccessIdKey());
                        credentials.setPassword(basicProfile.getAwsSecretAccessKey());
                    }
                }
            }
        }
        return credentials;
    }

    private void fetchSsoCredentials(Credentials credentials, Map<String,String> properties, Local awsDirectory)
        throws LoginFailureException {
        // See https://github.com/boto/botocore/blob/23ee17f5446c78167ff442302471f9928c3b4b7c/botocore/credentials.py#L2004
        try {
            final String ssoStartUrl = properties.get("sso_start_url");
            final String ssoAccountId = properties.get("sso_account_id");
            final String ssoRoleName = properties.get("sso_role_name");
            final String cacheKey = String.format("{\"accountId\":\"%s\",\"roleName\":\"%s\",\"startUrl\":\"%s\"}",
                ssoAccountId, ssoRoleName, ssoStartUrl);
            final HashCode hashCode = Hashing.sha1().newHasher().putString(cacheKey, Charsets.UTF_8).hash();
            final String hash = BaseEncoding.base16().lowerCase().encode(hashCode.asBytes());
            final String cachedCredentialsJson = String.format("%s.json", hash);
            final Local cachedCredentialsFile = 
                LocalFactory.get(LocalFactory.get(LocalFactory.get(awsDirectory, "cli"), "cache"), cachedCredentialsJson);
            if(log.isDebugEnabled()) {
                log.debug(String.format("Attempting to read SSO credentials %s", cachedCredentialsFile));
            }
            if(!cachedCredentialsFile.exists()) {
                throw new LoginFailureException("SSO credentials are missing.");
            }
            try(InputStream inputStream = cachedCredentialsFile.getInputStream()) {
                CachedCredentials cachedCredentials = MAPPER.readValue(inputStream, CachedCredentials.class);
                CachedCredential cachedCredential = cachedCredentials.credentials;
                if(cachedCredential == null){
                    throw new LoginFailureException("SSO credentials missing keys.");
                }
                Instant expiration = Instant.parse(cachedCredential.expiration);
                if(expiration.isBefore(Instant.now())) {
                    throw new LoginFailureException("SSO credentials are expired.");
                }
                credentials.setUsername(cachedCredential.accessKey);
                credentials.setPassword(cachedCredential.secretKey);
                credentials.setToken(cachedCredential.sessionToken);
            }
        }
        catch(IOException | AccessDeniedException e) {
            throw new LoginFailureException("SSO credentials could not be fetched.", e);
        }
    }

    private static class CachedCredentials {
        @JsonProperty("Credentials")
        private CachedCredential credentials;
    }

    private static class CachedCredential {
        @JsonProperty("AccessKeyId")
        private String accessKey;
        @JsonProperty("SecretAccessKey")
        private String secretKey;
        @JsonProperty("SessionToken")
        private String sessionToken;
        @JsonProperty("Expiration")
        private String expiration;
    }

    protected AWSSecurityTokenService getTokenService(final Host host, final String region, final String accessKey, final String secretKey, final String sessionToken) {
        final ClientConfiguration configuration = new CustomClientConfiguration(host,
            new ThreadLocalHostnameDelegatingTrustManager(trust, host.getHostname()), key);
        return AWSSecurityTokenServiceClientBuilder.standard()
            .withCredentials(new AWSStaticCredentialsProvider(StringUtils.isBlank(sessionToken) ? new AWSCredentials() {
                @Override
                public String getAWSAccessKeyId() {
                    return accessKey;
                }

                @Override
                public String getAWSSecretKey() {
                    return secretKey;
                }
            } : new AWSSessionCredentials() {
                @Override
                public String getAWSAccessKeyId() {
                    return accessKey;
                }

                @Override
                public String getAWSSecretKey() {
                    return secretKey;
                }

                @Override
                public String getSessionToken() {
                    return sessionToken;
                }
            }))
            .withClientConfiguration(configuration)
            .withRegion(StringUtils.isNotBlank(region) ? Regions.fromName(region) : Regions.DEFAULT_REGION).build();
    }

    /**
     * Implementation of AbstractProfilesConfigFileScanner that groups profile properties into a map while scanning
     * through the credentials profile.
     */
    private static final class ProfilesConfigFileLoaderHelper extends AbstractProfilesConfigFileScanner {

        /**
         * Map from the parsed profile name to the map of all the property values included the specific profile
         */
        protected final Map<String, Map<String, String>> allProfileProperties = new LinkedHashMap<>();

        /**
         * Parses the input and returns a map of all the profile properties.
         */
        public Map<String, Map<String, String>> parseProfileProperties(Local file) throws AccessDeniedException, IOException {
            if(!file.exists()) {
                return new LinkedHashMap<>();
            }
            if(log.isDebugEnabled()) {
                log.debug(String.format("Reading AWS file %s", file));
            }
            try(InputStream inputStream = file.getInputStream();
                Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())){
                run(scanner);
                return new LinkedHashMap<>(allProfileProperties);
            }
        }

        private String sanitizeProfile(String profileName) {
            // The config file has sections that start with `profile `
            return profileName.replaceAll("^profile ", "");
        }

        @Override
        protected void onEmptyOrCommentLine(String profileName, String line) {
            // Ignore empty or comment line
        }

        @Override
        protected void onProfileStartingLine(String newProfileName, String line) {
            // If the same profile name has already been declared, clobber the
            // previous one
            allProfileProperties.put(sanitizeProfile(newProfileName), new HashMap<>());
        }

        @Override
        protected void onProfileEndingLine(String prevProfileName) {
            // No-op
        }

        @Override
        protected void onProfileProperty(String profileName, String propertyKey,
                                         String propertyValue, boolean isSupportedProperty,
                                         String line) {
            profileName = sanitizeProfile(profileName);
            Map<String, String> properties = allProfileProperties.get(profileName);

            if(properties.containsKey(propertyKey)) {
                log.warn("Duplicate property values for [" + propertyKey + "].");
            }

            properties.put(propertyKey, propertyValue);
        }

        @Override
        protected void onEndOfFile() {
            // No-op
        }
    }
}
