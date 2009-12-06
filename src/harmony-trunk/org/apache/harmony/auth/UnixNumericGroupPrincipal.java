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

package org.apache.harmony.auth;

import java.io.Serializable;
import java.security.Principal;

import org.apache.harmony.auth.internal.nls.Messages;

/**
 * This class represents a unix groups by its group id. 
 */
public class UnixNumericGroupPrincipal implements Serializable, Principal {

    private static final long serialVersionUID = -535408497353506159L;

    // Group id
    private long gid;
    // Group name
    private String gname;
    /**
     * Creates the object using a String representation of gid.
     * @param gid string representation of gid
     * @param primary shows whether the group is primary
     * throws NullPointerException if gid is null
     */
    public UnixNumericGroupPrincipal(String gid, boolean primary) {
        if (gid == null) {
            throw new NullPointerException(Messages.getString("auth.07")); //$NON-NLS-1$
        }
        this.gid = Long.parseLong(gid);
    }

    /**
     * Creates the object using gid passed.
     * @param gid gid
     * @param primary shows whether the group is primary
     */
    public UnixNumericGroupPrincipal(long gid, boolean primary) {
        this.gid = gid;
    }

    /**
     * Creates the object using gid and group's name passed.
     * @param gid gid
     * @param gname group name
     * @param primary shows whether the group is primary
     */
    public UnixNumericGroupPrincipal(long gid, String gname, boolean primary) {
        this.gid = gid;
        this.gname = gname;
    }
    
    /**
     * Returns String representation of the stored GID.
     */
    public String getName() {
        return Long.toString(gid);
    }

    /**
     * Returns group name.
     */
    public String getObjectName() {
        return gname;
    }
    
    /**
     * Returns numeric representation of the stored gid.
     */
    public long longValue() {
        return gid;
    }

    /**
     * Returns String representation of this object.
     */
    @Override
    public String toString() {
        if( gname == null ) {
            return "UnixNumericGroupPrincipal, gid=" + gid; //$NON-NLS-1$
        }
        return "UnixNumericGroupPrincipal, gid=" + gid+"; name="+gname; //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Tests objects for equality.<br>
     * The objects are considered equals if they both are of type 
     * UnixNumericGroupPrincipal and have the same gid.
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof UnixNumericGroupPrincipal) {
            return ((UnixNumericGroupPrincipal) o).gid == gid;
        }
        return false;
    }

    /**
     * Returns hash code of this object.
     */
    @Override
    public int hashCode() {
        return (int) gid;
    }
}
