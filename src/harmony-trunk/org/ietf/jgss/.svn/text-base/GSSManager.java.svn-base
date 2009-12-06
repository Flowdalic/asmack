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

package org.ietf.jgss;

import java.security.AccessController;
import java.security.Provider;

import org.apache.harmony.security.fortress.PolicyUtils;

public abstract class GSSManager {

    static final String MANAGER = "jgss.spi.manager"; //$NON-NLS-1$

    /**
     * The implementation class name should be specified via
     * &quot;jgss.spi.manager&quot; security property.
     * 
     * @throws SecurityException if manager instance cannot be created
     */
    public static GSSManager getInstance() {
        return AccessController.doPrivileged(new PolicyUtils.ProviderLoader<GSSManager>(
                MANAGER, GSSManager.class));
    }

    public abstract Oid[] getMechs();

    public abstract Oid[] getNamesForMech(Oid mech) throws GSSException;

    public abstract Oid[] getMechsForName(Oid nameType);

    public abstract GSSName createName(String nameStr, Oid nameType) throws GSSException;

    public abstract GSSName createName(byte[] name, Oid nameType) throws GSSException;

    public abstract GSSName createName(String nameStr, Oid nameType, Oid mech)
            throws GSSException;

    public abstract GSSName createName(byte[] name, Oid nameType, Oid mech) throws GSSException;

    public abstract GSSCredential createCredential(int usage) throws GSSException;

    public abstract GSSCredential createCredential(GSSName name, int lifetime, Oid mech,
            int usage) throws GSSException;

    public abstract GSSCredential createCredential(GSSName name, int lifetime, Oid[] mechs,
            int usage) throws GSSException;

    public abstract GSSContext createContext(GSSName peer, Oid mech, GSSCredential myCred,
            int lifetime) throws GSSException;

    public abstract GSSContext createContext(GSSCredential myCred) throws GSSException;

    public abstract GSSContext createContext(byte[] interProcessToken) throws GSSException;

    public abstract void addProviderAtFront(Provider p, Oid mech) throws GSSException;

    public abstract void addProviderAtEnd(Provider p, Oid mech) throws GSSException;
}
