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

package org.apache.harmony.auth.internal.kerberos.v5;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.harmony.security.asn1.ASN1Explicit;
import org.apache.harmony.security.asn1.ASN1Integer;
import org.apache.harmony.security.asn1.ASN1Sequence;
import org.apache.harmony.security.asn1.ASN1SequenceOf;
import org.apache.harmony.security.asn1.ASN1StringType;
import org.apache.harmony.security.asn1.ASN1Type;
import org.apache.harmony.security.asn1.BerInputStream;

/**
 * Kerberos PrincipalName type.
 * 
 * @see http://www.ietf.org/rfc/rfc4120.txt
 */
public class PrincipalName {

    public static final int NT_UNKNOWN = 0;

    public static final int NT_PRINCIPAL = 1;

    public static final int NT_SRV_INST = 2;

    public static final int NT_SRV_HST = 3;

    public static final int NT_SRV_XHST = 4;

    public static final int NT_UID = 5;

    public static final int NT_X500_PRINCIPAL = 6;

    public static final int NT_SMTP_NAME = 7;

    public static final int NT_ENTERPRISE = 10;

    private final int type;

    private final String name[];

    public PrincipalName(int type, String[] name) {
        this.type = type;
        this.name = name;
    }

    public PrincipalName(int type, String str) {
        this.type = type;
        
        // FIXME: ignores escaped '/','@' chars
        if (str.indexOf('/') == -1) {
            //there is only one component in principal name
            name = new String[] { str };
        } else {
            StringTokenizer strTknzr = new StringTokenizer(str, "/"); //$NON-NLS-1$
            name = new String[strTknzr.countTokens()];
            for (int i = 0; i < name.length; i++) {
                name[i] = strTknzr.nextToken();
            }
        }
    }

    public int getType() {
        return type;
    }

    public String[] getName() {
        return name;
    }

    public String getCanonicalName() {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < (name.length - 1); i++) {
            buf.append(name[i]);
            buf.append('/');
        }
        // append last name element
        buf.append(name[name.length - 1]);

        return buf.toString();
    }

    public byte[] getEncoded() {
        return ASN1.encode(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof PrincipalName)) {
            return false;
        }

        PrincipalName that = (PrincipalName) obj;

        return type == that.type && Arrays.equals(that.name, name);
    }

    public static PrincipalName instanceOf(byte[] enc) throws IOException {
        return (PrincipalName) ASN1.decode(enc);
    }
    
    @Override
    public int hashCode() {
        return type + Arrays.hashCode(name);
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder("Name: "); //$NON-NLS-1$

        for (int i = 0; i < (name.length - 1); i++) {
            buf.append(name[i]);
            buf.append('/');
        }
        buf.append(name[name.length - 1]);
        buf.append(", type: "); //$NON-NLS-1$
        buf.append(type);

        return buf.toString();
    }

    // PrincipalName ::= SEQUENCE {
    //     name-type   [0] Int32,
    //     name-string [1] SEQUENCE OF KerberosString
    // }
    static final ASN1Sequence ASN1 = new ASN1Sequence(new ASN1Type[] {
            new ASN1Explicit(0, ASN1Integer.getInstance()),
            new ASN1Explicit(1, new ASN1SequenceOf(ASN1StringType.GENERALSTRING)), }) {

        @Override
        protected Object getDecodedObject(BerInputStream in) throws IOException {

            Object[] values = (Object[]) in.content;

            int type = ASN1Integer.toIntValue(values[0]);

            // TODO: list to array conversion should be done by framework
            List<?> list = (List<?>) values[1];
            String[] name = list.toArray(new String[list.size()]);
            return new PrincipalName(type, name);
        }

        @Override
        protected void getValues(Object object, Object[] values) {

            PrincipalName name = (PrincipalName) object;

            values[0] = BigInteger.valueOf(name.getType()).toByteArray();

            values[1] = Arrays.asList(name.getName());
        }
    };
}
