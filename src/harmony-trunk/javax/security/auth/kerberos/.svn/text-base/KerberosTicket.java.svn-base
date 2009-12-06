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

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.security.auth.DestroyFailedException;
import javax.security.auth.Destroyable;
import javax.security.auth.RefreshFailedException;
import javax.security.auth.Refreshable;

import org.apache.harmony.auth.internal.kerberos.v5.KerberosException;
import org.apache.harmony.auth.internal.kerberos.v5.KrbClient;
import org.apache.harmony.auth.internal.nls.Messages;
import org.apache.harmony.security.utils.Array;

public class KerberosTicket implements Destroyable, Refreshable, Serializable {

    private static final long serialVersionUID = 7395334370157380539L;

    // The description of these flags defines in the Kerberos Protocol Specification (RFC 1510).

    // FORWARDABLE flag 
    private static final int FORWARDABLE = 1;

    // FORWARDED flag
    private static final int FORWARDED = 2;

    // PROXIABLE flag
    private static final int PROXIABLE = 3;

    // PROXY flag
    private static final int PROXY = 4;

    // POSTDATED flag
    private static final int POSTDATED = 6;

    // RENEWABLE flag
    private static final int RENEWABLE = 8;

    // INITIAL flag
    private static final int INITIAL = 9;

    // number of flags used by Kerberos protocol
    private static final int FLAGS_NUM = 32;

    // line feed 
    private static final String LF = "\n"; //$NON-NLS-1$

    //ASN.1 encoding of the ticket
    private byte[] asn1Encoding;

    //raw bytes for the session key
    private KeyImpl sessionKey;

    //ticket flags
    private boolean[] flags;

    //time of initial authentication for the client
    private Date authTime;

    //time after which the ticket will be valid
    private Date startTime;

    // time after which the ticket will be invalid
    private Date endTime;

    // expiration time for the ticket
    private Date renewTill;

    // client that owns this ticket
    private KerberosPrincipal client;

    //service that owns this ticket
    private KerberosPrincipal server;

    //addresses from where the ticket may be used by the client
    private InetAddress[] clientAddresses;

    // indicates the ticket state
    private transient boolean destroyed;

    public KerberosTicket(byte[] asn1Encoding, KerberosPrincipal client,
            KerberosPrincipal server, byte[] keyBytes, int keyType, boolean[] flags,
            Date authTime, Date startTime, Date endTime, Date renewTill,
            InetAddress[] clientAddresses) {

        if (asn1Encoding == null) {
            throw new IllegalArgumentException(Messages.getString("auth.3B")); //$NON-NLS-1$
        }
        if (client == null) {
            throw new IllegalArgumentException(Messages.getString("auth.3C")); //$NON-NLS-1$
        }

        if (server == null) {
            throw new IllegalArgumentException(Messages.getString("auth.3D")); //$NON-NLS-1$
        }

        if (keyBytes == null) {
            throw new IllegalArgumentException(Messages.getString("auth.3E")); //$NON-NLS-1$
        }

        if (authTime == null) {
            throw new IllegalArgumentException(Messages.getString("auth.3F")); //$NON-NLS-1$
        }

        if (endTime == null) {
            throw new IllegalArgumentException(Messages.getString("auth.40")); //$NON-NLS-1$
        }

        this.asn1Encoding = new byte[asn1Encoding.length];
        System.arraycopy(asn1Encoding, 0, this.asn1Encoding, 0, this.asn1Encoding.length);

        this.client = client;
        this.server = server;
        this.sessionKey = new KeyImpl(keyBytes, keyType);

        if (flags == null) {
            this.flags = new boolean[FLAGS_NUM];
        } else if (flags.length > FLAGS_NUM) {
            this.flags = new boolean[flags.length];
            System.arraycopy(flags, 0, this.flags, 0, this.flags.length);
        } else {
            this.flags = new boolean[FLAGS_NUM];
            System.arraycopy(flags, 0, this.flags, 0, flags.length);
        }

        if (this.flags[RENEWABLE] && renewTill == null) {
            throw new IllegalArgumentException(Messages.getString("auth.41")); //$NON-NLS-1$
        }

        this.renewTill = renewTill;

        if (startTime != null) {
            this.startTime = startTime;
        } else {
            this.startTime = authTime;
        }

        if (this.startTime.getTime() > endTime.getTime()) {
            // TODO: make correct description of the exception  
            throw new IllegalArgumentException(Messages.getString("auth.42")); //$NON-NLS-1$
        }

        this.authTime = authTime;
        this.endTime = endTime;

        if (clientAddresses != null) {
            this.clientAddresses = new InetAddress[clientAddresses.length];
            System.arraycopy(clientAddresses, 0, this.clientAddresses, 0,
                    this.clientAddresses.length);
        }

    }

    public final KerberosPrincipal getClient() {
        return client;
    }

    public final KerberosPrincipal getServer() {
        return server;
    }

    public final SecretKey getSessionKey() {
        checkState();
        return sessionKey;
    }

    public final int getSessionKeyType() {
        checkState();
        return sessionKey.getKeyType();
    }

    public final byte[] getEncoded() {
        checkState();
        byte[] tmp = new byte[this.asn1Encoding.length];
        System.arraycopy(this.asn1Encoding, 0, tmp, 0, tmp.length);
        return tmp;
    }

    public final boolean isForwardable() {
        checkState();
        return flags[FORWARDABLE];
    }

    public final boolean isForwarded() {
        checkState();
        //TODO: was based on authentication involving a forwarded TGT ?
        return flags[FORWARDED];
    }

    public final boolean isProxiable() {
        checkState();
        return flags[PROXIABLE];
    }

    public final boolean isProxy() {
        checkState();
        return flags[PROXY];
    }

    public final boolean isPostdated() {
        checkState();
        return flags[POSTDATED];
    }

    public final boolean isRenewable() {
        checkState();
        return flags[RENEWABLE];
    }

    public final boolean isInitial() {
        checkState();
        return flags[INITIAL];
    }

    public final boolean[] getFlags() {
        if (destroyed) {
            return null;
        }
        boolean[] tmp = new boolean[flags.length];
        System.arraycopy(flags, 0, tmp, 0, tmp.length);
        return tmp;

    }

    public final Date getAuthTime() {
        if (destroyed) {
            return null;
        }
        return new Date(authTime.getTime());
    }

    public final Date getStartTime() {
        checkState();
        return new Date(startTime.getTime());
    }

    public final Date getEndTime() {
        if (destroyed) {
            return null;
        }
        return new Date(endTime.getTime());
    }

    public final Date getRenewTill() {
        if (destroyed) {
            return null;
        }
        return renewTill;
    }

    public final InetAddress[] getClientAddresses() {
        if (this.clientAddresses != null) {
            InetAddress[] tmp = new InetAddress[this.clientAddresses.length];
            System.arraycopy(clientAddresses, 0, tmp, 0, tmp.length);
            return tmp;
        }
        return null;
    }

    public void destroy() throws DestroyFailedException {
        if (destroyed) {
            return;
        }
        Arrays.fill(this.asn1Encoding, (byte) 0);
        this.client = null;
        this.server = null;
        this.sessionKey.destroy();
        this.flags = null;
        this.authTime = null;
        this.startTime = null;
        this.endTime = null;
        this.renewTill = null;
        this.clientAddresses = null;
        destroyed = true;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void refresh() throws RefreshFailedException {

        checkState();

        if (!flags[RENEWABLE]) {
            throw new RefreshFailedException(Messages.getString("auth.44")); //$NON-NLS-1$
        }

        if (System.currentTimeMillis() > this.renewTill.getTime()) {
            throw new RefreshFailedException(Messages.getString("auth.45")); //$NON-NLS-1$
        }

        try {
            KrbClient.doTGS();
        } catch (KerberosException e) {
            throw new RefreshFailedException(e.getMessage());
        }
    }

    public boolean isCurrent() {
        checkState();
        if (this.getStartTime().getTime() <= System.currentTimeMillis()
                && System.currentTimeMillis() <= this.getEndTime().getTime()) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        checkState();
        StringBuilder sb = new StringBuilder();
        sb.append("Ticket = ").append(Array.toString(asn1Encoding, "(hex) ") + LF); //$NON-NLS-1$ //$NON-NLS-2$
        sb.append("Client Principal = ").append(client.getName() + LF); //$NON-NLS-1$
        sb.append("Server Principal = ").append(server.getName() + LF); //$NON-NLS-1$
        //TODO: append session key
        sb.append("Session Key = ").append(sessionKey.toString() + LF); //$NON-NLS-1$
        sb.append("Forwardable Ticket = ").append(flags[FORWARDABLE] + LF); //$NON-NLS-1$
        sb.append("Forwarded Ticket = ").append(flags[FORWARDED] + LF); //$NON-NLS-1$
        sb.append("Proxiable Ticket = ").append(flags[PROXIABLE] + LF); //$NON-NLS-1$
        sb.append("Proxy Ticket = ").append(flags[PROXY] + LF); //$NON-NLS-1$
        sb.append("Postdated Ticket = ").append(flags[POSTDATED] + LF); //$NON-NLS-1$
        sb.append("Renewable Ticket = ").append(flags[RENEWABLE] + LF); //$NON-NLS-1$
        sb.append("Initial Ticket = ").append(flags[INITIAL] + LF); //$NON-NLS-1$
        sb.append("Auth Time = ").append(this.authTime.toString() + LF); //$NON-NLS-1$
        sb.append("Start Time = ").append(this.startTime.toString() + LF); //$NON-NLS-1$
        sb.append("End Time = ").append(this.endTime.toString() + LF); //$NON-NLS-1$
        sb.append("Renew Till = ").append(this.renewTill.toString() + LF); //$NON-NLS-1$
        sb.append("Client Addresses "); //$NON-NLS-1$
        if (clientAddresses != null) {
            for (int i = 0; i < clientAddresses.length; i++) {
                if (clientAddresses[i] == null) {
                    throw new NullPointerException(Messages.getString("auth.46")); //$NON-NLS-1$
                }
                sb
                        .append("clientAddresses[" + i + "] = ").append(clientAddresses[i].toString() + LF + "\t\t"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            }
        } else {
            sb.append("null"); //$NON-NLS-1$
        }

        return sb.toString();
    }

    /** 
     * if a key is destroyed then IllegalStateException must be thrown 
     */
    private void checkState() {
        if (destroyed) {
            throw new IllegalStateException(Messages.getString("auth.43")); //$NON-NLS-1$
        }
    }
}
