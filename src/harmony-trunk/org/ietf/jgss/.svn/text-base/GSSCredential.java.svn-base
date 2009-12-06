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

public interface GSSCredential extends Cloneable {

    static final int ACCEPT_ONLY = 2;

    static final int DEFAULT_LIFETIME = 0;

    static final int INDEFINITE_LIFETIME = Integer.MAX_VALUE; // 2147483647;

    static final int INITIATE_AND_ACCEPT = 0;

    static final int INITIATE_ONLY = 1;

    void add(GSSName name, int initLifetime, int acceptLifetime, Oid mech, int usage)
            throws GSSException;

    void dispose() throws GSSException;

    boolean equals(Object another);

    Oid[] getMechs() throws GSSException;

    GSSName getName() throws GSSException;

    GSSName getName(Oid mech) throws GSSException;

    int getRemainingAcceptLifetime(Oid mech) throws GSSException;

    int getRemainingInitLifetime(Oid mech) throws GSSException;

    int getRemainingLifetime() throws GSSException;

    int getUsage() throws GSSException;

    int getUsage(Oid mech) throws GSSException;

    int hashCode();
}
