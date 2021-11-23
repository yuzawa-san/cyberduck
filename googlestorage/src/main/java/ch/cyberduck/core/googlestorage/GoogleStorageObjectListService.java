package ch.cyberduck.core.googlestorage;

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

import ch.cyberduck.core.AttributedList;
import ch.cyberduck.core.ListProgressListener;
import ch.cyberduck.core.ListService;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.PathAttributes;
import ch.cyberduck.core.PathContainerService;
import ch.cyberduck.core.PathNormalizer;
import ch.cyberduck.core.SimplePathPredicate;
import ch.cyberduck.core.URIEncoder;
import ch.cyberduck.core.VersioningConfiguration;
import ch.cyberduck.core.exception.BackgroundException;
import ch.cyberduck.core.exception.ConnectionCanceledException;
import ch.cyberduck.core.exception.NotfoundException;
import ch.cyberduck.core.features.Versioning;
import ch.cyberduck.core.preferences.HostPreferences;
import ch.cyberduck.core.threading.BackgroundExceptionCallable;
import ch.cyberduck.core.threading.ThreadPool;
import ch.cyberduck.core.threading.ThreadPoolFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.ConcurrentUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.google.api.services.storage.model.Objects;
import com.google.api.services.storage.model.StorageObject;
import com.google.common.collect.ImmutableMap;

public class GoogleStorageObjectListService implements ListService {
    private static final Logger log = Logger.getLogger(GoogleStorageObjectListService.class);

    private final GoogleStorageSession session;
    private final GoogleStorageAttributesFinderFeature attributes;
    private final PathContainerService containerService;
    private final boolean references;
    private final Integer concurrency;

    public GoogleStorageObjectListService(final GoogleStorageSession session) {
        this(session, new HostPreferences(session.getHost()).getInteger("googlestorage.listing.concurrency"),
                new HostPreferences(session.getHost()).getBoolean("googlestorage.versioning.references.enable"));
    }

    public GoogleStorageObjectListService(final GoogleStorageSession session, final boolean references) {
        this(session, new HostPreferences(session.getHost()).getInteger("googlestorage.listing.concurrency"), references);
    }

    public GoogleStorageObjectListService(final GoogleStorageSession session, final Integer concurrency, final boolean references) {
        this.session = session;
        this.attributes = new GoogleStorageAttributesFinderFeature(session);
        this.containerService = session.getFeature(PathContainerService.class);
        this.concurrency = concurrency;
        this.references = references;
    }

    @Override
    public AttributedList<Path> list(final Path directory, final ListProgressListener listener) throws BackgroundException {
        return this.list(directory, listener, String.valueOf(Path.DELIMITER),
                new HostPreferences(session.getHost()).getInteger("googlestorage.listing.chunksize"));
    }

    public AttributedList<Path> list(final Path directory, final ListProgressListener listener, final String delimiter, final int chunksize) throws BackgroundException {
        final ThreadPool pool = ThreadPoolFactory.get("list", concurrency);
        try {
            final Path bucket = containerService.getContainer(directory);
            final VersioningConfiguration versioning = null != session.getFeature(Versioning.class) ? session.getFeature(Versioning.class).getConfiguration(
                    containerService.getContainer(directory)
            ) : VersioningConfiguration.empty();
            final AttributedList<Path> objects = new AttributedList<>();
            final List<Future<Path>> folders = new ArrayList<>();
            Objects response;
            long revision = 0L;
            String lastKey = null;
            String page = null;
            boolean hasDirectoryPlaceholder = containerService.isContainer(directory);
            do {
                response = session.getClient().objects().list(bucket.getName())
                        .setPageToken(page)
                        // lists all versions of an object as distinct results. The default is false
                        .setVersions(versioning.isEnabled())
                        .setMaxResults((long) chunksize)
                        .setDelimiter(delimiter)
                        .setPrefix(this.createPrefix(directory))
                        .execute();
                if(response.getItems() != null) {
                    for(StorageObject object : response.getItems()) {
                        final String key = PathNormalizer.normalize(object.getName());
                        if(String.valueOf(Path.DELIMITER).equals(key)) {
                            log.warn(String.format("Skipping prefix %s", key));
                            continue;
                        }
                        if(new SimplePathPredicate(new Path(bucket, key, EnumSet.of(Path.Type.directory))).test(directory)) {
                            // Placeholder object, skip
                            hasDirectoryPlaceholder = true;
                            continue;
                        }
                        if(!StringUtils.equals(lastKey, key)) {
                            // Reset revision for next file
                            revision = 0L;
                        }
                        final EnumSet<Path.Type> types = object.getName().endsWith(String.valueOf(Path.DELIMITER))
                                ? EnumSet.of(Path.Type.directory) : EnumSet.of(Path.Type.file);
                        final Path file;
                        final PathAttributes attr = attributes.toAttributes(object, versioning);
                        attr.setRevision(++revision);
                        // Copy bucket location
                        attr.setRegion(bucket.attributes().getRegion());
                        if(null == delimiter) {
                            // When searching for files recursively
                            file = new Path(String.format("%s%s", bucket.getAbsolute(), key), types, attr);
                        }
                        else {
                            file = new Path(directory.isDirectory() ? directory : directory.getParent(), PathNormalizer.name(key), types, attr);
                        }
                        objects.add(file);
                        lastKey = key;
                    }
                    if(versioning.isEnabled()) {
                        if(references) {
                            for(Path f : objects) {
                                if(f.attributes().isDuplicate()) {
                                    final Path latest = objects.find(new LatestVersionPathPredicate(f));
                                    if(latest != null) {
                                        // Reference version
                                        final AttributedList<Path> versions = new AttributedList<>(latest.attributes().getVersions());
                                        versions.add(f);
                                        latest.attributes().setVersions(versions);
                                    }
                                    else {
                                        log.warn(String.format("No current version found for %s", f));
                                    }
                                }
                            }
                        }
                    }
                }
                if(response.getPrefixes() != null) {
                    for(String prefix : response.getPrefixes()) {
                        if(String.valueOf(Path.DELIMITER).equals(prefix)) {
                            log.warn(String.format("Skipping prefix %s", prefix));
                            continue;
                        }
                        final String key = PathNormalizer.normalize(prefix);
                        if(new SimplePathPredicate(new Path(bucket, key, EnumSet.of(Path.Type.directory))).test(directory)) {
                            continue;
                        }
                        final Path file;
                        final PathAttributes attributes = new PathAttributes();
                        attributes.setRegion(bucket.attributes().getRegion());
                        if(null == delimiter) {
                            // When searching for files recursively
                            file = new Path(String.format("%s%s", bucket.getAbsolute(), key), EnumSet.of(Path.Type.directory, Path.Type.placeholder), attributes);
                        }
                        else {
                            file = new Path(directory, PathNormalizer.name(key), EnumSet.of(Path.Type.directory, Path.Type.placeholder), attributes);
                        }
                        if(versioning.isEnabled()) {
                            folders.add(this.submit(pool, bucket, directory, URIEncoder.decode(prefix)));
                        }
                        else {
                            folders.add(ConcurrentUtils.constantFuture(file));
                        }
                    }
                }
                page = response.getNextPageToken();
                listener.chunk(directory, objects);
            }
            while(page != null);
            for(Future<Path> future : folders) {
                try {
                    objects.add(future.get());
                }
                catch(InterruptedException e) {
                    log.error("Listing versioned objects failed with interrupt failure");
                    throw new ConnectionCanceledException(e);
                }
                catch(ExecutionException e) {
                    log.warn(String.format("Listing versioned objects failed with execution failure %s", e.getMessage()));
                    if(e.getCause() instanceof BackgroundException) {
                        throw (BackgroundException) e.getCause();
                    }
                    throw new BackgroundException(e.getCause());
                }
            }
            listener.chunk(directory, objects);
            if(!hasDirectoryPlaceholder && objects.isEmpty()) {
                throw new NotfoundException(directory.getAbsolute());
            }
            return objects;
        }
        catch(IOException e) {
            throw new GoogleStorageExceptionMappingService().map("Listing directory {0} failed", e, directory);
        }
    }

    private Future<Path> submit(final ThreadPool pool, final Path bucket, final Path directory, final String common) {
        return pool.execute(new BackgroundExceptionCallable<Path>() {
            @Override
            public Path call() throws BackgroundException {
                final PathAttributes attr = new PathAttributes();
                attr.setRegion(bucket.attributes().getRegion());
                final Path prefix = new Path(directory, PathNormalizer.name(common),
                        EnumSet.of(Path.Type.directory, Path.Type.placeholder), attr);
                try {
                    final Objects versions = session.getClient().objects().list(bucket.getName())
                            .setVersions(true)
                            .setMaxResults(1L)
                            .setPrefix(common)
                            .execute();
                    if(null != versions.getItems() && versions.getItems().size() == 1) {
                        final StorageObject version = versions.getItems().get(0);
                        if(URIEncoder.decode(version.getName()).equals(common)) {
                            attr.setVersionId(String.valueOf(version.getGeneration()));
                        }
                        // Check if all of them are deleted
                        final Objects unversioned = session.getClient().objects().list(bucket.getName())
                                .setVersions(false)
                                .setMaxResults(1L)
                                .setPrefix(common)
                                .execute();
                        if(null == unversioned.getItems() || unversioned.getItems().size() == 0) {
                            attr.setDuplicate(true);
                        }
                    }
                    return prefix;
                }
                catch(IOException e) {
                    throw new GoogleStorageExceptionMappingService().map("Listing directory {0} failed", e, prefix);
                }
            }
        });
    }

    protected String createPrefix(final Path directory) {
        // Keys can be listed by prefix. By choosing a common prefix
        // for the names of related keys and marking these keys with
        // a special character that delimits hierarchy, you can use the list
        // operation to select and browse keys hierarchically
        String prefix = StringUtils.EMPTY;
        if(!containerService.isContainer(directory)) {
            // Restricts the response to only contain results that begin with the
            // specified prefix. If you omit this optional argument, the value
            // of Prefix for your query will be the empty string.
            // In other words, the results will be not be restricted by prefix.
            prefix = containerService.getKey(directory);
            if(StringUtils.isBlank(prefix)) {
                return StringUtils.EMPTY;
            }
            if(directory.isDirectory()) {
                if(!prefix.endsWith(String.valueOf(Path.DELIMITER))) {
                    prefix += Path.DELIMITER;
                }
            }
        }
        return prefix;
    }

    private static final class LatestVersionPathPredicate extends SimplePathPredicate {
        public LatestVersionPathPredicate(final Path f) {
            super(f);
        }

        @Override
        public boolean test(final Path test) {
            if(super.test(test)) {
                return !test.attributes().isDuplicate();
            }
            return false;
        }
    }
}
