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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;

import javax.crypto.SecretKey;

import org.apache.harmony.security.asn1.ASN1Any;
import org.apache.harmony.security.asn1.ASN1BitString;
import org.apache.harmony.security.asn1.ASN1Constants;
import org.apache.harmony.security.asn1.ASN1Explicit;
import org.apache.harmony.security.asn1.ASN1Integer;
import org.apache.harmony.security.asn1.ASN1OctetString;
import org.apache.harmony.security.asn1.ASN1Sequence;
import org.apache.harmony.security.asn1.ASN1SequenceOf;
import org.apache.harmony.security.asn1.ASN1StringType;
import org.apache.harmony.security.asn1.ASN1Type;
import org.apache.harmony.security.asn1.BerInputStream;
import org.apache.harmony.security.asn1.BitString;
import org.apache.harmony.security.asn1.DerInputStream;

/**
 * @see http://www.ietf.org/rfc/rfc4120.txt
 */
public class KDCReply {

    /**
     * Authentication Service request message type
     */
    public static final int AS_REP = 11;

    /**
     * Ticket-Granting Service request message type
     */
    public static final int TGS_REP = 13;

    // type of a protocol message: AS_REP or TGS_REP
    private final int msgType;

    private final PrincipalName cname;

    private final String crealm;

    private final Ticket ticket;

    private final EncryptedData encPart;

    //
    //
    //
    private Date authtime;

    private Date starttime;

    private Date endtime;

    private Date renewtill;

    private String srealm;

    private PrincipalName sname;

    // session key
    private EncryptionKey key;

    private BitString flags;

    private KDCReply(int msgType, String crealm, PrincipalName cname,
            Ticket ticket, EncryptedData encPart) {
        this.msgType = msgType;
        this.cname = cname;
        this.crealm = crealm;
        this.ticket = ticket;
        this.encPart = encPart;
    }

    public void decrypt(SecretKey key) throws IOException {
        DerInputStream in = new DerInputStream(new ByteArrayInputStream(encPart
                .decrypt(key)));

        Object[] values = (Object[]) ENC_AS_REP_PART.decode(in);

        this.key = (EncryptionKey) values[0];
        flags = (BitString) values[4];
        authtime = (Date) values[5];
        starttime = (Date) values[6];
        endtime = (Date) values[7];
        renewtill = (Date) values[8];
        srealm = (String) values[9];
        sname = (PrincipalName) values[10];
    }

    public int getMsgtype() {
        return msgType;
    }

    public String getCrealm() {
        return crealm;
    }

    public PrincipalName getCname() {
        return cname;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public EncryptedData getEncPart() {
        return encPart;
    }

    //
    //
    //

    public Date getAuthtime() {
        return authtime;
    }

    public Date getStarttime() {
        return starttime;
    }

    public Date getEndtime() {
        return endtime;
    }

    public Date getRenewtill() {
        return renewtill;
    }

    public String getSrealm() {
        return srealm;
    }

    public PrincipalName getSname() {
        return sname;
    }

    public EncryptionKey getKey() {
        return key;
    }

    public BitString getFlags() {
        return flags;
    }

    /**
     * <pre>
     *  KDC-REP         ::= SEQUENCE {
     *       pvno            [0] INTEGER (5),
     *       msg-type        [1] INTEGER (11 -- AS -- | 13 -- TGS --),
     *       padata          [2] SEQUENCE OF PA-DATA OPTIONAL
     *       -- NOTE: not empty --,
     *       crealm          [3] Realm,
     *       cname           [4] PrincipalName,
     *       ticket          [5] Ticket,
     *       enc-part        [6] EncryptedData
     *       -- EncASRepPart or EncTGSRepPart,
     *       -- as appropriate
     *       }
     * </pre>
     */
    static final ASN1Sequence KDC_REP_ASN1 = new ASN1Sequence(new ASN1Type[] {
            new ASN1Explicit(0, ASN1Integer.getInstance()), // pvno
            new ASN1Explicit(1, ASN1Integer.getInstance()), // msg-type
            new ASN1Explicit(2, new ASN1SequenceOf(ASN1Any.getInstance())),
            // TODO should we define Realm type?
            new ASN1Explicit(3, ASN1StringType.GENERALSTRING), // crealm
            new ASN1Explicit(4, PrincipalName.ASN1), // cname
            new ASN1Explicit(5, Ticket.TICKET_ASN1), // ticket
            new ASN1Explicit(6, EncryptedData.ASN1), // enc-part
    }) {
        {
            setOptional(2); // padata
        }

        @Override
        protected Object getDecodedObject(BerInputStream in) throws IOException {

            Object[] values = (Object[]) in.content;

            return new KDCReply(ASN1Integer.toIntValue(values[1]),
                    (String) values[3], (PrincipalName) values[4],
                    (Ticket) values[5], (EncryptedData) values[6]);
        }

        @Override
        protected void getValues(Object object, Object[] values) {
            throw new RuntimeException(); // FIXME message
        }
    };

    public static final ASN1Explicit AS_REP_ASN1 = new ASN1Explicit(
            ASN1Constants.CLASS_APPLICATION, AS_REP, KDC_REP_ASN1);

    private static final ASN1SequenceOf LAST_REQ = new ASN1SequenceOf(
            new ASN1Sequence(new ASN1Type[] {
            // TODO should we define Int32 type?
                    new ASN1Explicit(0, ASN1Integer.getInstance()), // lr-type
                    new ASN1Explicit(1, KerberosTime.getASN1()), // lr-value
            }));

    private static final ASN1Sequence HOST_ADDRESS = new ASN1Sequence(
            new ASN1Type[] {
            // TODO should we define Int32 type?
                    new ASN1Explicit(0, ASN1Integer.getInstance()), // addr-type
                    new ASN1Explicit(1, ASN1OctetString.getInstance()), // address
            });

    private static final ASN1Sequence ENC_KDC_REP_PART = new ASN1Sequence(
            new ASN1Type[] { new ASN1Explicit(0, EncryptionKey.ASN1), // key
                    new ASN1Explicit(1, LAST_REQ), // last-req
                    // TODO should we define UInt32 type?
                    new ASN1Explicit(2, ASN1Integer.getInstance()), // nonce
                    new ASN1Explicit(3, KerberosTime.getASN1()), // key-expiration
                    // TODO TicketFlags type?
                    new ASN1Explicit(4, ASN1BitString.getInstance()), // flags
                    new ASN1Explicit(5, KerberosTime.getASN1()), // authtime
                    new ASN1Explicit(6, KerberosTime.getASN1()), // starttime
                    new ASN1Explicit(7, KerberosTime.getASN1()), // endtime
                    new ASN1Explicit(8, KerberosTime.getASN1()), // renew-till
                    // TODO should we define Realm type?
                    new ASN1Explicit(9, ASN1StringType.GENERALSTRING), // srealm
                    new ASN1Explicit(10, PrincipalName.ASN1), // sname
                    new ASN1Explicit(11, HOST_ADDRESS), // caddr
            }) {
        {
            setOptional(3); // key-expiration
            setOptional(6); // starttime
            setOptional(8); // renew-till
            setOptional(11); // caddr
        }
    };

    //TODO: create const ENC_AS_REP_PART=25
    private static final ASN1Explicit ENC_AS_REP_PART = new ASN1Explicit(
            ASN1Constants.CLASS_APPLICATION, 25, ENC_KDC_REP_PART);

}
