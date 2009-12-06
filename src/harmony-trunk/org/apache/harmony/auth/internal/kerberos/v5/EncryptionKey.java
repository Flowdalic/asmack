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

import org.apache.harmony.security.asn1.ASN1Explicit;
import org.apache.harmony.security.asn1.ASN1Integer;
import org.apache.harmony.security.asn1.ASN1OctetString;
import org.apache.harmony.security.asn1.ASN1Sequence;
import org.apache.harmony.security.asn1.ASN1Type;
import org.apache.harmony.security.asn1.BerInputStream;

/**
 * Kerberos EncryptionKey type.
 * 
 * @see http://www.ietf.org/rfc/rfc4120.txt
 */
public class EncryptionKey {

    private final int type;

    private final byte[] value;

    /**
     * Creates EncryptionKey
     * 
     * @param type -
     *            encryption type
     * @param value -
     *            key value
     */
    public EncryptionKey(int type, byte[] value) {
        this.type = type;
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public byte[] getValue() {
        return value;
    }

    /**
     * ASN.1 decoder for EncryptionKey
     * 
     * EncryptionKey   ::= SEQUENCE {
     *     keytype         [0] Int32 -- actually encryption type --,
     *     keyvalue        [1] OCTET STRING
     * }
     */
    public static final ASN1Sequence ASN1 = new ASN1Sequence(new ASN1Type[] {
    // TODO should we define Int32 type?
            new ASN1Explicit(0, ASN1Integer.getInstance()), // keytype
            new ASN1Explicit(1, ASN1OctetString.getInstance()), // keyvalue
    }) {

        @Override
        protected Object getDecodedObject(BerInputStream in) throws IOException {

            Object[] values = (Object[]) in.content;

            return new EncryptionKey(ASN1Integer.toIntValue(values[0]),
                    (byte[]) values[1]);
        }

        @Override
        protected void getValues(Object object, Object[] values) {

            EncryptionKey ekey = (EncryptionKey) object;

            values[0] = ASN1Integer.fromIntValue(ekey.type);
            values[1] = ekey.value;
        }
    };
}
