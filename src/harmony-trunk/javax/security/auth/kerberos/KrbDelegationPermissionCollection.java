/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package javax.security.auth.kerberos;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Vector;

import org.apache.harmony.auth.internal.nls.Messages;

/**
 * Specific PermissionCollection for storing DelegationPermissions
 * 
 */
class KrbDelegationPermissionCollection extends PermissionCollection implements Serializable {

    private static final long serialVersionUID = -3383936936589966948L;
    
    private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField(
            "permissions", Vector.class) }; //$NON-NLS-1$

    private transient DelegationPermission[] items = new DelegationPermission[10];

    private transient int offset;

    //initialization of a collection
    KrbDelegationPermissionCollection() {
        super();
    }

    /**
     * Adds a ServicePermission to the collection.
     */
    @Override
    public void add(Permission permission) {

        if (isReadOnly()) {
            throw new SecurityException(Messages.getString("auth.21")); //$NON-NLS-1$
        }

        if (permission == null || !(permission instanceof DelegationPermission)) {
            throw new IllegalArgumentException(Messages.getString("auth.22", permission)); //$NON-NLS-1$
        }
        synchronized (this) {
            if (offset == items.length) {
                DelegationPermission[] dp = new DelegationPermission[items.length * 2];
                System.arraycopy(items, 0, dp, 0, offset);
                items = dp;
            }
            items[offset++] = (DelegationPermission) permission;
        }
    }

    /**
     * Returns enumeration of the collection.
     */
    @Override
    public Enumeration<Permission> elements() {
        return new Enumeration<Permission>() {
            private int index;

            public boolean hasMoreElements() {
                return index < offset;
            }

            public DelegationPermission nextElement() {
                if (index == offset) {
                    throw new NoSuchElementException();
                }
                return items[index++];
            }
        };
    }

    /**
     * Returns true if this collection implies the specified permission. 
     */
    @Override
    public boolean implies(Permission permission) {
        if (permission == null || !(permission instanceof DelegationPermission)) {
            return false;
        }

        synchronized (this) {
            for (int i = 0; i < offset; i++) {
                if (items[i].implies(permission)) {
                    return true;
                }
            }
        }
        return false;
    }

    // white a collection to stream
    private void writeObject(ObjectOutputStream out) throws IOException {
        Vector<DelegationPermission> permissions;
        permissions = new Vector<DelegationPermission>(offset);
        for (int i = 0; i < offset; permissions.add(items[i++])) {
        }
        ObjectOutputStream.PutField fields = out.putFields();
        fields.put("permissions", permissions); //$NON-NLS-1$
        out.writeFields();
    }

    // read a collection from stream
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        ObjectInputStream.GetField fields = in.readFields();
        Vector<?> permissions = (Vector<?>) fields.get("permissions", null); //$NON-NLS-1$
        items = new DelegationPermission[permissions.size() * 2];
        for (offset = 0; offset < items.length / 2;) {
            Object obj = permissions.get(offset);
            if (obj == null || !(obj instanceof DelegationPermission)) {
                throw new IllegalArgumentException(Messages.getString("auth.22", obj)); //$NON-NLS-1$
            }
            items[offset++] = (DelegationPermission) obj;
        }
    }
}
