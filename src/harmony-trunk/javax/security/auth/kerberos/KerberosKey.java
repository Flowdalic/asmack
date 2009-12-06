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

import javax.crypto.SecretKey;
import javax.security.auth.DestroyFailedException;
import javax.security.auth.Destroyable;

import org.apache.harmony.auth.internal.nls.Messages;

/**
 * See <a href="http://www.ietf.org/rfc/rfc3961.txt">RFC3961</a>
 */
public class KerberosKey implements SecretKey, Destroyable {

    private static final long serialVersionUID = -4625402278148246993L;

    //principal    
    private KerberosPrincipal principal;

    //key version number
    private int versionNum;

    //raw bytes for the secret key
    private KeyImpl key;

    // indicates the ticket state
    private transient boolean destroyed;

    public KerberosKey(KerberosPrincipal principal, byte[] keyBytes, int keyType,
            int versionNumber) {

        if (keyBytes == null) {
            throw new NullPointerException(Messages.getString("auth.47")); //$NON-NLS-1$
        }

        this.principal = principal;
        this.versionNum = versionNumber;

        this.key = new KeyImpl(keyBytes, keyType);

    }

    public KerberosKey(KerberosPrincipal principal, char[] password, String algorithm) {

        this.principal = principal;

        this.key = new KeyImpl(principal, password, algorithm);
    }

    public final KerberosPrincipal getPrincipal() {
        checkState();
        return principal;
    }

    public final String getAlgorithm() {
        return key.getAlgorithm();
    }

    public final String getFormat() {
        return key.getFormat();
    }

    public final int getKeyType() {
        return key.getKeyType();
    }

    public final byte[] getEncoded() {
        return key.getEncoded();
    }

    public final int getVersionNumber() {
        checkState();
        return versionNum;
    }

    public void destroy() throws DestroyFailedException {
        if (!destroyed) {
            this.principal = null;
            key.destroy();
            this.destroyed = true;
        }
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    @Override
    public String toString() {
        checkState();
        StringBuilder sb = new StringBuilder();
        sb.append("KerberosPrincipal ").append(principal.getName()).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$
        sb.append("KeyVersion ").append(versionNum).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$
        sb.append(key.toString());
        return sb.toString();
    }

    // if a key is destroyed then IllegalStateException must be thrown 
    private void checkState() {
        if (destroyed) {
            throw new IllegalStateException(Messages.getString("auth.48")); //$NON-NLS-1$
        }
    }
}
