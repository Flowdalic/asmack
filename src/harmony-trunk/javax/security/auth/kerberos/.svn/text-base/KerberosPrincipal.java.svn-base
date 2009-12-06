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
import java.security.Principal;

import org.apache.harmony.auth.internal.kerberos.v5.KerberosException;
import org.apache.harmony.auth.internal.kerberos.v5.KrbClient;
import org.apache.harmony.auth.internal.kerberos.v5.PrincipalName;
import org.apache.harmony.auth.internal.nls.Messages;
import org.apache.harmony.security.asn1.ASN1StringType;

public final class KerberosPrincipal implements Principal, Serializable {

    private static final long serialVersionUID = -7374788026156829911L;

    public static final int KRB_NT_UNKNOWN = 0;

    public static final int KRB_NT_PRINCIPAL = 1;

    public static final int KRB_NT_SRV_INST = 2;

    public static final int KRB_NT_SRV_HST = 3;

    public static final int KRB_NT_SRV_XHST = 4;

    public static final int KRB_NT_UID = 5;

    // the full name of principal
    private transient PrincipalName name;

    // the realm
    private transient String realm;

    // "principal" @ "realm"
    private transient String strName;

    private void init(int type, String name) {

        // FIXME: correctly implement parsing name according to RFC 1964
        // http://www.ietf.org/rfc/rfc1964.txt
        if (name == null || name.trim().length() == 0) {
            throw new IllegalArgumentException(Messages.getString("auth.23")); //$NON-NLS-1$
        }

        int pos = name.indexOf('@');
        if (pos != -1) {
            realm = name.substring(pos + 1, name.length());

            // verify realm name according to RFC 1964(2.1.1 (2))
            // check invalid chars '/', ':' and null
            if (realm.indexOf('/') != -1 || realm.indexOf(':') != -1
                    || realm.indexOf(0) != -1) {
                throw new IllegalArgumentException(Messages
                        .getString("auth.24")); //$NON-NLS-1$
            }

            name = name.substring(0, pos);
        } else {
            // look for default realm name
            try {
                realm = KrbClient.getRealm();
            } catch (KerberosException e) {
                throw new IllegalArgumentException(e);
            }
        }
        this.name = new PrincipalName(type, name);
    }

    public KerberosPrincipal(String name) {
        init(KRB_NT_PRINCIPAL, name);
    }

    public KerberosPrincipal(String name, int type) {
        init(type, name);
        if (type < 0 || type > KRB_NT_UID) {
            throw new IllegalArgumentException(Messages.getString("auth.25")); //$NON-NLS-1$
        }
    }

    public String getName() {
        if (strName == null) {
            if (realm == null) {
                strName = name.getCanonicalName();
            } else {
                strName = name.getCanonicalName() + '@' + realm;
            }
        }
        return strName;
    }

    public String getRealm() {
        return realm;
    }

    public int getNameType() {
        return name.getType();
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof KerberosPrincipal)) {
            return false;
        }

        KerberosPrincipal that = (KerberosPrincipal) obj;

        if (realm == null) {
            return that.realm == null;
        } else if (!realm.equals(that.realm)) {
            return false;
        }
        return name.equals(that.name);
    }

    @Override
    public String toString() {
        return getName();
    }

    private void readObject(ObjectInputStream s) throws IOException,
            ClassNotFoundException {

        s.defaultReadObject();

        name = PrincipalName.instanceOf((byte[]) s.readObject());
        realm = (String) ASN1StringType.GENERALSTRING.decode((byte[]) s
                .readObject());

        //FIXME: verify serialized values
    }

    private void writeObject(ObjectOutputStream s) throws IOException {

        s.defaultWriteObject();

        s.writeObject(name.getEncoded());
        s.writeObject(ASN1StringType.GENERALSTRING.encode(realm));
    }
}
