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
import java.io.Serializable;
import java.security.BasicPermission;
import java.security.Permission;
import java.security.PermissionCollection;

import org.apache.harmony.auth.internal.nls.Messages;

public final class DelegationPermission extends BasicPermission implements Serializable {

    private static final long serialVersionUID = 883133252142523922L;

    // initialization of a target name
    private static String init(String name) {

        String trName = name.trim();

        int length = trName.length();
        // length MUST be at least 7 characters
        if (length < 7) {
            throw new IllegalArgumentException(Messages.getString("auth.20")); //$NON-NLS-1$

        }

        int index = name.indexOf('"', 2);

        if (trName.charAt(0) != '"' || index == -1 || (index + 6) > trName.length()
                || trName.charAt(index + 1) != ' ' || trName.charAt(index + 2) != '"'
                || trName.charAt(trName.length() - 1) != '"') {
            throw new IllegalArgumentException(Messages.getString("auth.20")); //$NON-NLS-1$
        }
        return trName;
    }

    public DelegationPermission(String principals) {
        super(init(principals));
    }

    public DelegationPermission(String principals, String action) {
        super(init(principals), action);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        return this.getName().equals(((DelegationPermission) obj).getName());
    }

    @Override
    public boolean implies(Permission permission) {
        return equals(permission);
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    @Override
    public PermissionCollection newPermissionCollection() {
        return new KrbDelegationPermissionCollection();
    }

    private void writeObject(ObjectOutputStream s) throws IOException, ClassNotFoundException {
        s.defaultWriteObject();
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        init(getName());
    }
}
