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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;

import org.apache.harmony.security.asn1.ASN1Any;
import org.apache.harmony.security.asn1.ASN1Constants;
import org.apache.harmony.security.asn1.ASN1Explicit;
import org.apache.harmony.security.asn1.ASN1Integer;
import org.apache.harmony.security.asn1.ASN1Sequence;
import org.apache.harmony.security.asn1.ASN1SequenceOf;
import org.apache.harmony.security.asn1.ASN1StringType;
import org.apache.harmony.security.asn1.ASN1Type;

/**
 * @see http://www.ietf.org/rfc/rfc3961.txt
 * @see http://www.ietf.org/rfc/rfc4120.txt
 */
public class KDCRequest {

    /**
     * Authentication Service request message type
     */
    public static final int AS_REQ = 10;

    /**
     * Ticket-Granting Service request message type
     */
    public static final int TGS_REQ = 12;

    // type of a protocol message: AS_REQ or TGS_REQ
    private final int msgType;

    private final PrincipalName cname;

    private final String realm;

    private final PrincipalName sname;

    KDCRequest(int msgType, PrincipalName cname, String realm, PrincipalName sname) {
        super();
        this.msgType = msgType;
        this.cname = cname;
        this.realm = realm;
        this.sname = sname;
    }

    public DatagramSocket send(InetAddress address, int port) throws IOException {

        if (msgType != AS_REQ) {
            throw new RuntimeException("Not implemented");
        }

        byte[] enc = AS_REQ_ASN1.encode(this);

        DatagramPacket req = new DatagramPacket(enc, enc.length, address, port);
        DatagramSocket socket = new DatagramSocket();

        socket.send(req);

        return socket;
    }

    /**
     * 
     * <pre>
     * KDC-REQ-BODY    ::= SEQUENCE {
     *      kdc-options             [0] KDCOptions,
     *      cname                   [1] PrincipalName OPTIONAL
     *      -- Used only in AS-REQ --,
     *      realm                   [2] Realm
     *      -- Server's realm
     *      -- Also client's in AS-REQ --,
     *      sname                   [3] PrincipalName OPTIONAL,
     *      from                    [4] KerberosTime OPTIONAL,
     *      till                    [5] KerberosTime,
     *      rtime                   [6] KerberosTime OPTIONAL,
     *      nonce                   [7] UInt32,
     *      etype                   [8] SEQUENCE OF Int32 -- EncryptionType
     *      -- in preference order --,
     *      addresses               [9] HostAddresses OPTIONAL,
     *      enc-authorization-data  [10] EncryptedData OPTIONAL
     *      -- AuthorizationData --,
     *      additional-tickets      [11] SEQUENCE OF Ticket OPTIONAL
     *      -- NOTE: not empty
     *      }
     * </pre>
     */
    private static final ASN1Sequence KDC_REQ_BODY = new ASN1Sequence(new ASN1Type[] {
            new ASN1Explicit(0, ASN1Any.getInstance()), // TODO: ignored
            new ASN1Explicit(1, PrincipalName.ASN1),
            // TODO should we define Realm type?
            new ASN1Explicit(2, ASN1StringType.GENERALSTRING),
            new ASN1Explicit(3, PrincipalName.ASN1),
            new ASN1Explicit(4, ASN1Any.getInstance()), //TODO: ignored
            new ASN1Explicit(5, KerberosTime.getASN1()),
            new ASN1Explicit(6, ASN1Any.getInstance()), //TODO: ignored
            new ASN1Explicit(7, ASN1Integer.getInstance()),
            new ASN1Explicit(8, new ASN1SequenceOf(ASN1Integer.getInstance())),
            new ASN1Explicit(9, ASN1Any.getInstance()), //TODO: ignored
            new ASN1Explicit(10, ASN1Any.getInstance()), //TODO: ignored
            new ASN1Explicit(11, ASN1Any.getInstance()), //TODO: ignored

    }) {
        {
            setOptional(1); // cname
            setOptional(3); // sname
            setOptional(4); // from
            setOptional(6); // rtime
            setOptional(9); // addresses
            setOptional(10); // enc-authorization-data
            setOptional(11); // additional-tickets
        }

        @Override
        protected void getValues(Object object, Object[] values) {
            KDCRequest request = (KDCRequest) object;

            // FIXME: hardcoded - no KDCOptions are set
            // note: number of bits should be >= 32
            // (see RFC 4120, 5.2.8. KerberosFlags)
            values[0] = new byte[] { (byte) 0x03, (byte) 0x05, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, };

            values[1] = request.cname;
            values[2] = request.realm;
            values[3] = request.sname;

            // value[4] = from; //TODO

            // till: requested: "19700101000000Z" FIXME
            values[5] = new Date(0);

            // values[6] = rtime //TODO

            // nonce FIXME: hardcoded
            values[7] = BigInteger.valueOf(0).toByteArray();

            // etype FIXME
            ArrayList<byte[]> list = new ArrayList<byte[]>();

            // see RFC 3961 (Section 8)
            list.add(BigInteger.valueOf(1).toByteArray());// des-cbc-crc
            list.add(BigInteger.valueOf(2).toByteArray());// des-cbc-md4
            list.add(BigInteger.valueOf(3).toByteArray());// des-cbc-md5
            values[8] = list;

            // value[9] = FIXME
            // value[10] = FIXME
            // value[11] = FIXME
        }
    };

    /**
     * <pre>
     * KDC-REQ         ::= SEQUENCE {
     *      -- NOTE: first tag is [1], not [0]
     *      pvno            [1] INTEGER (5) ,
     *      msg-type        [2] INTEGER (10 -- AS -- | 12 -- TGS --),
     *      padata          [3] SEQUENCE OF PA-DATA OPTIONAL
     *      -- NOTE: not empty --,
     *      req-body        [4] KDC-REQ-BODY
     *      }
     * </pre>
     */
    static final ASN1Sequence KDC_REQ_ASN1 = new ASN1Sequence(new ASN1Type[] {
    // pvno [1] INTEGER (5)
            new ASN1Explicit(1, ASN1Integer.getInstance()),
            // msg-type [2] INTEGER
            new ASN1Explicit(2, ASN1Integer.getInstance()),
            // padata [3] SEQUENCE OF PA-DATA OPTIONAL
            new ASN1Explicit(3, new ASN1SequenceOf(ASN1Any.getInstance())),
            // req-body [4] KDC-REQ-BODY
            new ASN1Explicit(4, KDC_REQ_BODY), }) {
        {
            setOptional(2); // padata
        }

        @Override
        protected void getValues(Object object, Object[] values) {
            KDCRequest request = (KDCRequest) object;

            values[0] = BigInteger.valueOf(5).toByteArray();
            values[1] = BigInteger.valueOf(request.msgType).toByteArray();
            // values[2] = //FIXME
            values[3] = request; // pass for further use
        }
    };

    static final ASN1Explicit AS_REQ_ASN1 = new ASN1Explicit(ASN1Constants.CLASS_APPLICATION,
            AS_REQ, KDC_REQ_ASN1);
}
