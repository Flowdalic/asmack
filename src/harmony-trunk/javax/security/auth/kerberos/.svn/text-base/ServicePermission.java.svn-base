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
import java.io.Serializable;
import java.security.Permission;
import java.security.PermissionCollection;

import org.apache.harmony.auth.internal.nls.Messages;

public final class ServicePermission extends Permission implements Serializable {

    private static final long serialVersionUID = -1227585031618624935L;

    private static final String INITIATE = "initiate"; //$NON-NLS-1$
    private static final String ACCEPT = "accept"; //$NON-NLS-1$
    private static final String INITIATE_ACCEPT = "initiate,accept"; //$NON-NLS-1$
    private static final String[] ACTIONS_TABLE = {"", ACCEPT, INITIATE, INITIATE_ACCEPT}; //$NON-NLS-1$

    private final static char ACCEPT_MASK = 1;
    private final static char INITIATE_MASK = 2;

    private static final int INITIATE_LEN = INITIATE.length();
    private static final int ACCEPT_LEN = ACCEPT.length();
    private static final int MIN_LEN = Math.min(INITIATE_LEN,ACCEPT_LEN); 

    /** 
     * ACCEPT_MASK, INITIATE_ACCEPT or (INITIATE_ACCEPT | ACCEPT_MASK)
     */
    private String actions;

    // initialization of actions
    private void initActions(String actions) {
        if (actions == null || actions.length() < MIN_LEN) {
            throw new IllegalArgumentException(Messages.getString("auth.2E")); //$NON-NLS-1$
        }

        char[] c_acts = actions.toCharArray();

        int result = 0;
        int ptr = 0;

        int len6 = c_acts.length - ACCEPT_LEN;
        int len8 = c_acts.length - INITIATE_LEN;

        do {
            //skipping whitespaces
            while (ptr <= len6
                    && (c_acts[ptr] == ' ' || c_acts[ptr] == '\t'
                            || c_acts[ptr] == '\n' || c_acts[ptr] == 0x0B
                            || c_acts[ptr] == '\f' || c_acts[ptr] == '\r')) {
                ++ptr;
            }

            if (ptr > len6) {
                // expect string "accept" or "initiate", not just white
                // spaces
                throw new IllegalArgumentException(Messages.getString("auth.2E")); //$NON-NLS-1$
            }

            //parsing string
            if ((c_acts[ptr] == 'a' || c_acts[ptr] == 'A')
                    && (c_acts[ptr + 1] == 'c' || c_acts[ptr + 1] == 'C')
                    && (c_acts[ptr + 2] == 'c' || c_acts[ptr + 2] == 'C')
                    && (c_acts[ptr + 3] == 'e' || c_acts[ptr + 3] == 'E')
                    && (c_acts[ptr + 4] == 'p' || c_acts[ptr + 4] == 'P')
                    && (c_acts[ptr + 5] == 't' || c_acts[ptr + 5] == 'T')) {
                result |= ACCEPT_MASK;
                ptr += ACCEPT_LEN;
            } else if (ptr <= len8
                    && (c_acts[ptr] == 'i' || c_acts[ptr] == 'I')
                    && (c_acts[ptr + 1] == 'n' || c_acts[ptr + 1] == 'N')
                    && (c_acts[ptr + 2] == 'i' || c_acts[ptr + 2] == 'I')
                    && (c_acts[ptr + 3] == 't' || c_acts[ptr + 3] == 'T')
                    && (c_acts[ptr + 4] == 'i' || c_acts[ptr + 4] == 'I')
                    && (c_acts[ptr + 5] == 'a' || c_acts[ptr + 5] == 'A')
                    && (c_acts[ptr + 6] == 't' || c_acts[ptr + 6] == 'T')
                    && (c_acts[ptr + 7] == 'e' || c_acts[ptr + 7] == 'E')) {
                result |= INITIATE_MASK;
                ptr += INITIATE_LEN;
            } else {
                throw new IllegalArgumentException(Messages.getString("auth.2E")); //$NON-NLS-1$
            }

            //skipping trailing whitespaces
            while (ptr < c_acts.length
                    && (c_acts[ptr] == ' ' || c_acts[ptr] == '\t'
                            || c_acts[ptr] == '\n' || c_acts[ptr] == 0x0B
                            || c_acts[ptr] == '\f' || c_acts[ptr] == '\r')) {
                ptr++;
            }

            if (ptr == c_acts.length) {
                this.actions = ACTIONS_TABLE[result];
                return;
            }
        } while (c_acts[ptr++] == ',');

        // unknown trailing symbol
        throw new IllegalArgumentException(Messages.getString("auth.2E")); //$NON-NLS-1$
    }

    public ServicePermission(String name, String actions) {
        super(name);

        initActions(actions);

        if (name == null) {
            throw new NullPointerException(Messages.getString("auth.2F")); //$NON-NLS-1$
        }
        if (name.trim().length() == 0) {
            throw new IllegalArgumentException(Messages.getString("auth.30")); //$NON-NLS-1$
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || ServicePermission.class != obj.getClass()) {
            return false;
        }
        ServicePermission sp = (ServicePermission) obj;

        return actions == sp.actions && getName().equals(sp.getName());
    }

    @Override
    public int hashCode() {
        return getName().hashCode() * actions.length();
    }

    @Override
    public String getActions() {
        return actions;
    }

    @Override
    public boolean implies(Permission permission) {
        if (this == permission) {
            return true;
        }

        if (permission == null || ServicePermission.class != permission.getClass()) {
            return false;
        }

        ServicePermission sp = (ServicePermission) permission;
        String name = getName();

        return (actions == INITIATE_ACCEPT || actions == sp.actions)
				&& (name.length() == 1 && name.charAt(0) == '*' || name.equals(permission.getName()));
    }

    @Override
    public PermissionCollection newPermissionCollection() {
        return new KrbServicePermissionCollection();
    }

    private synchronized void writeObject(java.io.ObjectOutputStream s)
            throws IOException {
        s.defaultWriteObject();
    }

    private synchronized void readObject(java.io.ObjectInputStream s)
            throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        initActions(getActions());
    }
}
