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
 * This class represents a unix user by its name. 
 */
public class UnixPrincipal implements Serializable, Principal {

    /**
     * @serial
     */
    private static final long serialVersionUID = -4145806951495625642L;

    // User name
    private String name;

    /**
     * Sole constructor.
     * @param name user name
     * @throws NullPointerException if name is null
     */
    public UnixPrincipal(String name) {
        if (name == null) {
            throw new NullPointerException(Messages.getString("auth.00")); //$NON-NLS-1$
        }
        this.name = name;
    }

    /**
     * @return user name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns string representation of this object
     */
    @Override
    public String toString() {
        return "UnixPrincipal, name=" + name; //$NON-NLS-1$
    }

    /**
     * Compares two UnixPrincipal objects.<br>
     * Two principal objects are considered equal if they are both of type 
     * UnixPrincipal and their names are equal.
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof UnixPrincipal) {
            return name.equals(((UnixPrincipal) o).name);
        }
        return false;
    }

    /**
     * Return hash code of this object.
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }
}