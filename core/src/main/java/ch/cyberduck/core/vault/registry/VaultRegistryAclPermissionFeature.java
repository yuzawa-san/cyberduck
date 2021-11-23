package ch.cyberduck.core.vault.registry;

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
import ch.cyberduck.core.Local;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.Session;
import ch.cyberduck.core.exception.BackgroundException;
import ch.cyberduck.core.features.AclPermission;
import ch.cyberduck.core.vault.VaultRegistry;

import java.util.EnumSet;
import java.util.List;

public class VaultRegistryAclPermissionFeature implements AclPermission {

    private final Session<?> session;
    private final AclPermission proxy;
    private final VaultRegistry registry;

    public VaultRegistryAclPermissionFeature(final Session<?> session, final AclPermission proxy, final VaultRegistry registry) {
        this.session = session;
        this.proxy = proxy;
        this.registry = registry;
    }

    @Override
    public Acl getPermission(final Path file) throws BackgroundException {
        return registry.find(session, file).getFeature(session, AclPermission.class, proxy).getPermission(file);
    }

    @Override
    public void setPermission(final Path file, final Acl acl) throws BackgroundException {
        registry.find(session, file).getFeature(session, AclPermission.class, proxy).setPermission(file, acl);
    }

    @Override
    public List<Acl.User> getAvailableAclUsers() {
        return proxy.getAvailableAclUsers();
    }

    @Override
    public List<Acl.Role> getAvailableAclRoles(final List<Path> files) {
        return proxy.getAvailableAclRoles(files);
    }

    @Override
    public Acl getDefault(final Path file, final Local local) {
        return proxy.getDefault(file, local);
    }

    @Override
    public Acl getDefault(final EnumSet<Path.Type> type) {
        return proxy.getDefault(type);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("VaultRegistryAclPermissionFeature{");
        sb.append("proxy=").append(proxy);
        sb.append('}');
        return sb.toString();
    }
}
