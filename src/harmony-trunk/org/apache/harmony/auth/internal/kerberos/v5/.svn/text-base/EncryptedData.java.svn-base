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

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.apache.harmony.security.asn1.ASN1Explicit;
import org.apache.harmony.security.asn1.ASN1Integer;
import org.apache.harmony.security.asn1.ASN1OctetString;
import org.apache.harmony.security.asn1.ASN1Sequence;
import org.apache.harmony.security.asn1.ASN1Type;
import org.apache.harmony.security.asn1.BerInputStream;

/**
 * @see http://www.ietf.org/rfc/rfc4120.txt
 */
public class EncryptedData {

    /**
     * DES in CBC mode with CRC-based checksum
     */
    public static final int DES_CBC_CRC = 1;

    /**
     * DES in CBC mode with CRC-based checksum
     */
    public static final int DES_CBC_MD4 = 2;

    /**
     * DES in CBC mode with CRC-based checksum
     */
    public static final int DES_CBC_MD5 = 3;

    private final int etype;

    private final int kvno;

    private final byte[] cipher;

    public EncryptedData(int etype, int kvno, byte[] cipher) {
        this.etype = etype;
        this.kvno = kvno;
        this.cipher = cipher;
    }

    public int getEtype() {
        return etype;
    }

    public int getKvno() {
        return kvno;
    }

    public byte[] getCipher() {
        return cipher;
    }

    public byte[] decrypt(SecretKey key) {

        int offset;

        IvParameterSpec initCipherState;
        switch (etype) {
            case DES_CBC_CRC:
                offset = 12;// confounder(8)+CRC-32 checksum(4)
                // copy of original key
                initCipherState = new IvParameterSpec(key.getEncoded());
                break;
            case DES_CBC_MD4:
            case DES_CBC_MD5:
                offset = 24;// confounder(8)+ MD4/5 checksum(16)
                // all-zero
                initCipherState = new IvParameterSpec(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, });
                break;
            default:
                throw new RuntimeException();//FIXME not implemented yet
        }

        try {
            Cipher dcipher = Cipher.getInstance("DES/CBC/NoPadding");

            dcipher.init(Cipher.DECRYPT_MODE, key, initCipherState);

            byte[] tmp = dcipher.doFinal(cipher);

            // TODO: verify checksum

            // cat out: confounder and checksum bytes
            // TODO: how to do the same for padding bytes?
            byte[] result = new byte[tmp.length - offset];
            System.arraycopy(tmp, offset, result, 0, result.length);

            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <pre>
     * EncryptedData   ::= SEQUENCE {
     *      etype   [0] Int32 -- EncryptionType --,
     *      kvno    [1] UInt32 OPTIONAL,
     *      cipher  [2] OCTET STRING -- ciphertext
     *      }
     * </pre>
     */
    static final ASN1Sequence ASN1 = new ASN1Sequence(new ASN1Type[] {
    // TODO should we define Int32 type?
            new ASN1Explicit(0, ASN1Integer.getInstance()), // etype
            // TODO should we define UInt32 type?
            new ASN1Explicit(1, ASN1Integer.getInstance()), // kvno
            // cipher
            new ASN1Explicit(2, ASN1OctetString.getInstance()), }) {
        {
            setOptional(1); // kvno
        }

        @Override
        protected Object getDecodedObject(BerInputStream in) throws IOException {
            Object[] values = (Object[]) in.content;
            return new EncryptedData(ASN1Integer.toIntValue(values[0]), ASN1Integer
                    .toIntValue(values[1]), (byte[]) values[2]);
        }

        @Override
        protected void getValues(Object object, Object[] values) {
            throw new RuntimeException(); //FIXME message
        }
    };
}
