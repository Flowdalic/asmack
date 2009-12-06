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

package org.apache.harmony.auth.jgss.kerberos;

import java.lang.reflect.Constructor;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Set;

import javax.security.auth.DestroyFailedException;
import javax.security.auth.RefreshFailedException;
import javax.security.auth.Subject;
import javax.security.auth.kerberos.KerberosKey;
import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.kerberos.KerberosTicket;
import javax.security.auth.kerberos.ServicePermission;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.apache.harmony.auth.jgss.kerberos.toolbox.KerberosToolboxSpi;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;

public class KerberosUtils {
    public static final String DEFAULT_CHARSET_NAME = "UTF-8";

    public static final int DEFAULT_GSSEXCEPTION_MAJOR_CODE = 3;

    public static final int DEFAULT_GSSEXCEPTION_MINOR_CODE = 0;

    public static final Oid KRB5_MECH;

    public static final Oid KRB5_PRINCIPAL_NAMETYPE;

    public static final Oid[] SUPPORTED_NAME_MECHS;

    public static final String KERBEROS_TOOLBOX_PROVIDER = "org.apache.harmony.auth.jgss.kerberos.toolbox.KerberosToolboxImpl";

    public static final String KERBEROS_CONTEXT_INIT = "org.apache.harmony.auth.jgss.initiate";

    public static final String KERBEROS_CONTEXT_ACCEPT = "org.apache.harmony.auth.jgss.accept";
    
    public static final String SERVICE_PERMISSION_ACTION_INITIATE = "initiate";
    
    public static final String SERVICE_PERMISSION_ACTION_ACCEPT = "accept";
    
    public static final String[] SUPPORTED_KEY_ENCRYPT= {"DES"};

    static {
        try {
            KRB5_MECH = new Oid("1.2.840.113554.1.2.2");
            KRB5_PRINCIPAL_NAMETYPE = new Oid("1.2.840.113554.1.2.2.1");

        } catch (GSSException e) {
            throw new Error();
        }
        SUPPORTED_NAME_MECHS = new Oid[] { GSSName.NT_USER_NAME,
                GSSName.NT_HOSTBASED_SERVICE, GSSName.NT_EXPORT_NAME,
                KRB5_PRINCIPAL_NAMETYPE };
    }

    public static KerberosToolboxSpi getKerberosToolbox(String kdcName)
            throws Exception {
        Class cls = Class.forName(KERBEROS_TOOLBOX_PROVIDER);
        Constructor constructor = cls.getConstructor(String.class);
        return (KerberosToolboxSpi) constructor.newInstance(kdcName);
    }

    private static KerberosTicket getKerberosTicketFromContext(
            final KerberosPrincipal clientPrincipal,
            final KerberosPrincipal serverPrincipal) {
        AccessControlContext acc = AccessController.getContext();
        KerberosTicket tgt = AccessController.doPrivileged(
                new PrivilegedAction<KerberosTicket>() {

                    public KerberosTicket run() {
                        AccessControlContext acc = AccessController
                                .getContext();
                        Subject subject = Subject.getSubject(acc);
                        return getTicketFromSubject(subject, clientPrincipal,
                                serverPrincipal);
                    }
                }, acc);
        if (tgt != null) {
            checkTGTServicePermission(tgt);
        }
        return tgt;
    }
    
    private static KerberosKey[] getKeysFromContext(final KerberosPrincipal serverPrincipal){
        AccessControlContext acc = AccessController.getContext();
        KerberosKey[] kerberosKeys = AccessController.doPrivileged(new PrivilegedAction<KerberosKey[]>(){
            public KerberosKey[] run() {
                AccessControlContext acc = AccessController
                .getContext();
                Subject subject = Subject.getSubject(acc);
                return getKeysFromSubject(subject, serverPrincipal);
            }}, acc);
        if(kerberosKeys.length > 0){
            //All keys in kerberosKeys have the same principal. Check the permission for the first key is enough.
            checkServerKeyServicePermission(kerberosKeys[0]);
            return kerberosKeys;
        }
        return null;
    }
    
    private static KerberosPrincipal getPrincipalFromContext(){
        AccessControlContext acc = AccessController.getContext();
        return AccessController.doPrivileged(new PrivilegedAction<KerberosPrincipal>(){
            public KerberosPrincipal run() {
                AccessControlContext acc = AccessController
                .getContext();
                Subject subject = Subject.getSubject(acc);
                Set<KerberosPrincipal> kerberosPrincipals = subject.getPrincipals(KerberosPrincipal.class);
                if(kerberosPrincipals.size() == 0){
                    return null;
                }
                return kerberosPrincipals.iterator().next();
            }}, acc);
    }
    
    private static KerberosKey[] getKeysFromSubject(Subject subject, final KerberosPrincipal serverPrincipal){
        Set<KerberosKey> kerberosKeys = subject.getPrivateCredentials(KerberosKey.class);
        ArrayList<KerberosKey> serverKeys = new ArrayList<KerberosKey>();
        for(KerberosKey kerberosKey : kerberosKeys){
            if(serverPrincipal.equals(kerberosKey.getPrincipal())){
                serverKeys.add(kerberosKey);
            }
        }
        return serverKeys.toArray(new KerberosKey[serverKeys.size()]);
    }

    private static KerberosTicket getTicketFromSubject(Subject subject,
            final KerberosPrincipal clientPrincipal,
            final KerberosPrincipal serverPrincipal) {
        if (null == subject) {
            return null;
        }
        Set<KerberosTicket> kerberosTickets = subject
                .getPrivateCredentials(KerberosTicket.class);
        for (KerberosTicket tgt : kerberosTickets) {
            if (clientPrincipal.equals(tgt.getClient())
                    && serverPrincipal.equals(tgt.getServer())) {
                if (isCurrent(tgt)) {
                    return tgt;
                }

                // the kerberosTicket cannot be renewed. Just
                // discard it
                // from the subject's private credentials.
                kerberosTickets.remove(tgt);
                return null;
            }
        }
        return null;
    }

    private static boolean isCurrent(KerberosTicket ticket) {
        if (!ticket.isCurrent()) {
            try {
                ticket.refresh();
            } catch (RefreshFailedException e) {
                try {
                    ticket.destroy();
                } catch (DestroyFailedException e1) {
                    e1.printStackTrace();
                }
                return false;
            }
        }
        return true;
    }

    private static KerberosTicket getTGTFromLoginModule(
            KerberosPrincipal clientPrincipal) {
        LoginContext loginContext = null;
        try {
            loginContext = new LoginContext(KERBEROS_CONTEXT_INIT);
            loginContext.login();

        } catch (LoginException e) {
            e.printStackTrace();
            return null;
        }
        Subject subject = loginContext.getSubject();
        if(clientPrincipal == null){
            clientPrincipal = getPrincipalFromContext();
        }
        return getTicketFromSubject(subject, clientPrincipal,
                getTGTServerPrincipal(clientPrincipal));
    }
    
    private static KerberosKey[] getKeysFromLoginModule(KerberosPrincipal serverPrincipal){
        LoginContext loginContext = null;
        try {
            loginContext = new LoginContext(KERBEROS_CONTEXT_ACCEPT);
            loginContext.login();

        } catch (LoginException e) {
            e.printStackTrace();
            return null;
        }
        Subject subject = loginContext.getSubject();
        if(serverPrincipal == null){
            serverPrincipal = getPrincipalFromContext();
        }
        return getKeysFromSubject(subject, serverPrincipal);
    }

    private static KerberosPrincipal getTGTServerPrincipal(
            KerberosPrincipal clientPrincipal) {
        String realm = clientPrincipal.getRealm();
        return new KerberosPrincipal("krbtgt/" + realm + "@" + realm);
    }

    public static KerberosTicket getTGT(KerberosPrincipal clientPrincipal) {
        KerberosTicket tgt = null;
        if (clientPrincipal != null) {
            tgt = getKerberosTicketFromContext(clientPrincipal,
                    getTGTServerPrincipal(clientPrincipal));
        }
        if (null != tgt) {
            return tgt;            
        }
        tgt = getTGTFromLoginModule(clientPrincipal);
        if (null != tgt) {            
            // TODO CACHE : Whether should attach this tgt to the subject for
            // current AccessControlContext?
        }
        return tgt;
    }
    
    public static KerberosKey[] getKeys(KerberosPrincipal serverPrincipal) {
        KerberosKey[] keys = null;
        if (serverPrincipal != null) {
            keys = getKeysFromContext(serverPrincipal);
        }
        if (null != keys) {
            return keys;
        }
        keys = getKeysFromLoginModule(serverPrincipal);
        if (null != keys) {
            // TODO CACHE : Whether should attach these keys to the subject for
            // current AccessControlContext?
        }
        return keys;
    }
    
        
    public static void checkTGTServicePermission(KerberosTicket tgt){
        checkServicePermission(tgt.getServer(), SERVICE_PERMISSION_ACTION_INITIATE);
    }
    
    public static void checkServerKeyServicePermission(KerberosKey key){
        checkServicePermission(key.getPrincipal(), SERVICE_PERMISSION_ACTION_ACCEPT);
    }

    public static void checkServicePermission(KerberosPrincipal principal,
            String action) {
        SecurityManager sm = System.getSecurityManager();
        if (sm == null) {
            return;
        }
        ServicePermission servicePermission = new ServicePermission(principal
                .getName(), action);
        sm.checkPermission(servicePermission);
    }
}
