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

import org.apache.harmony.security.asn1.ASN1Any;
import org.apache.harmony.security.asn1.ASN1Constants;
import org.apache.harmony.security.asn1.ASN1Explicit;
import org.apache.harmony.security.asn1.ASN1Integer;
import org.apache.harmony.security.asn1.ASN1Sequence;
import org.apache.harmony.security.asn1.ASN1StringType;
import org.apache.harmony.security.asn1.ASN1Type;
import org.apache.harmony.security.asn1.BerInputStream;

/**
 * @see http://www.ietf.org/rfc/rfc4120.txt
 */
public class Ticket {

    private final PrincipalName sname;

    private final String realm;

    // ASN.1 encoding of this ticket
    private byte[] encoded;
    
    private Ticket(String realm, PrincipalName sname) {
        this.sname = sname;
        this.realm = realm;
    }

    public String getRealm() {
        return realm;
    }

    public PrincipalName getSname() {
        return sname;
    }

    public byte[] getEncoded() {
        return encoded;
    }

    /**
     <pre>Ticket          ::= [APPLICATION 1] SEQUENCE {
     tkt-vno         [0] INTEGER (5),
     realm           [1] Realm,
     sname           [2] PrincipalName,
     enc-part        [3] EncryptedData -- EncTicketPart
     }</pre>
     */
    static final ASN1Sequence ASN1 = new ASN1Sequence(new ASN1Type[] {
            new ASN1Explicit(0, ASN1Integer.getInstance()), // tkt-vno
            // TODO should we define Realm type?
            new ASN1Explicit(1, ASN1StringType.GENERALSTRING), // realm
            new ASN1Explicit(2, PrincipalName.ASN1), // sname
            // FIXME ignored
            new ASN1Explicit(3, ASN1Any.getInstance()), // ticket 
    }) {

        @Override
        protected Object getDecodedObject(BerInputStream in) throws IOException {

            Object[] values = (Object[]) in.content;

            Ticket ticket = new Ticket((String) values[1],
                    (PrincipalName) values[2]);
            ticket.encoded = in.getEncoded();

            return ticket;
        }

        @Override
        protected void getValues(Object object, Object[] values) {
            throw new RuntimeException(); //FIXME message
        }
    };

    public static final ASN1Explicit TICKET_ASN1 = new ASN1Explicit(
            ASN1Constants.CLASS_APPLICATION, 1, ASN1);
}
