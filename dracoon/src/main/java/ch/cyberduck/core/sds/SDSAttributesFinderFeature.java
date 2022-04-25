package ch.cyberduck.core.sds;

/*
 * Copyright (c) 2002-2017 iterate GmbH. All rights reserved.
 * https://cyberduck.io/
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
 */

import ch.cyberduck.core.Acl;
import ch.cyberduck.core.ListProgressListener;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.PathAttributes;
import ch.cyberduck.core.exception.AccessDeniedException;
import ch.cyberduck.core.exception.BackgroundException;
import ch.cyberduck.core.exception.NotfoundException;
import ch.cyberduck.core.features.AttributesFinder;
import ch.cyberduck.core.preferences.HostPreferences;
import ch.cyberduck.core.sds.io.swagger.client.ApiException;
import ch.cyberduck.core.sds.io.swagger.client.api.NodesApi;
import ch.cyberduck.core.sds.io.swagger.client.model.DeletedNode;
import ch.cyberduck.core.sds.io.swagger.client.model.Node;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SDSAttributesFinderFeature implements AttributesFinder {
    private static final Logger log = LogManager.getLogger(SDSAttributesFinderFeature.class);

    public static final String KEY_CNT_DOWNLOADSHARES = "count_downloadshares";
    public static final String KEY_CNT_UPLOADSHARES = "count_uploadshares";
    public static final String KEY_ENCRYPTED = "encrypted";
    public static final String KEY_CLASSIFICATION = "classification";

    /**
     * Lookup previous versions
     */
    private final boolean references;

    private final SDSSession session;
    private final SDSNodeIdProvider nodeid;
    private final SDSAttributesAdapter adapter;

    public SDSAttributesFinderFeature(final SDSSession session, final SDSNodeIdProvider nodeid) {
        this(session, nodeid, new HostPreferences(session.getHost()).getBoolean("sds.versioning.references.enable"));
    }

    public SDSAttributesFinderFeature(final SDSSession session, final SDSNodeIdProvider nodeid, final boolean references) {
        this.session = session;
        this.nodeid = nodeid;
        this.references = references;
        this.adapter = new SDSAttributesAdapter(session);
    }

    @Override
    public PathAttributes find(final Path file, final ListProgressListener listener) throws BackgroundException {
        if(file.isRoot()) {
            // {"code":400,"message":"Bad Request","debugInfo":"Node ID must be positive.","errorCode":-80001}
            final PathAttributes attributes = new PathAttributes();
            if(session.userAccount().isUserInRole(SDSPermissionsFeature.ROOM_MANAGER_ROLE)) {
                // We need to map user roles to ACLs in order to decide if creating a top-level room is allowed
                final Acl acl = new Acl();
                acl.addAll(new Acl.CanonicalUser(), SDSPermissionsFeature.CREATE_ROLE);
                attributes.setAcl(acl);
            }
            return attributes;
        }
        // Throw failure if looking up file fails
        final String id = nodeid.getVersionId(file, listener);
        try {
            return this.findNode(file, id, listener);
        }
        catch(NotfoundException e) {
            // Try with reset cache after failure finding node id
            return this.findNode(file, nodeid.getVersionId(file, listener), listener);
        }
    }

    private PathAttributes findNode(final Path file, final String nodeId, final ListProgressListener listener) throws BackgroundException {
        try {
            if(file.attributes().isDuplicate()) {
                final DeletedNode node = new NodesApi(session.getClient()).requestDeletedNode(Long.parseLong(nodeId),
                        StringUtils.EMPTY, null);
                return adapter.toAttributes(node);
            }
            else {
                final Node node = new NodesApi(session.getClient()).requestNode(
                        Long.parseLong(nodeId), StringUtils.EMPTY, null);
                final PathAttributes attr = adapter.toAttributes(node);
                if(adapter.toType(node).contains(Path.Type.file)) {
                    if(references) {
                        try {
                            attr.setVersions(new SDSVersioningFeature(session, nodeid).list(file, listener));
                        }
                        catch(AccessDeniedException e) {
                            log.warn(String.format("Ignore failure %s fetching versions for %s", e, file));
                        }
                    }
                }
                return attr;
            }
        }
        catch(ApiException e) {
            throw new SDSExceptionMappingService(nodeid).map("Failure to read attributes of {0}", e, file);
        }
    }
}
