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
import java.util.Date;

import org.apache.harmony.security.asn1.ASN1Any;
import org.apache.harmony.security.asn1.ASN1Constants;
import org.apache.harmony.security.asn1.ASN1Explicit;
import org.apache.harmony.security.asn1.ASN1Integer;
import org.apache.harmony.security.asn1.ASN1OctetString;
import org.apache.harmony.security.asn1.ASN1Sequence;
import org.apache.harmony.security.asn1.ASN1StringType;
import org.apache.harmony.security.asn1.ASN1Type;
import org.apache.harmony.security.asn1.BerInputStream;
import org.apache.harmony.security.asn1.DerInputStream;

/**
 * Kerberos Error Message type.
 * 
 * @see http://www.ietf.org/rfc/rfc4120.txt
 */
public class KerberosErrorMessage {

    private Date ctime;

    private int cusec;

    private Date stime;

    private int susec;

    private int errorCode;

    private String crealm;

    private PrincipalName cname;

    private String realm;

    private PrincipalName sname;

    private String etext;

    public KerberosErrorMessage() {
    }

    public static KerberosErrorMessage decode(DerInputStream in) throws IOException {

        return (KerberosErrorMessage) ASN1.decode(in);
    }

    public Date getCtime() {
        return ctime;
    }

    public int getCusec() {
        return cusec;
    }

    public Date getStime() {
        return stime;
    }

    public int getSusec() {
        return susec;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getCrealm() {
        return crealm;
    }

    public PrincipalName getCname() {
        return cname;
    }

    public String getRealm() {
        return realm;
    }

    public PrincipalName getSname() {
        return sname;
    }

    public String getEtext() {
        return etext;
    }

    private static final ASN1Sequence KRB_ERROR = new ASN1Sequence(new ASN1Type[] {
            new ASN1Explicit(0, ASN1Any.getInstance()), // TODO:ignored
            new ASN1Explicit(1, ASN1Any.getInstance()), // TODO: ignored
            new ASN1Explicit(2, KerberosTime.getASN1()),// ctime
            // TODO should we define Microseconds type?
            new ASN1Explicit(3, ASN1Integer.getInstance()), // cusec
            new ASN1Explicit(4, KerberosTime.getASN1()),// stime
            // TODO should we define Microseconds type?
            new ASN1Explicit(5, ASN1Integer.getInstance()), // susec
            // TODO should we define Int32 type?
            new ASN1Explicit(6, ASN1Integer.getInstance()),// error-code
            // TODO should we define Realm type?
            new ASN1Explicit(7, ASN1StringType.GENERALSTRING),// crealm
            new ASN1Explicit(8, PrincipalName.ASN1),// cname
            // TODO should we define Realm type?
            new ASN1Explicit(9, ASN1StringType.GENERALSTRING),// realm
            new ASN1Explicit(10, PrincipalName.ASN1),// sname
            // TODO should we define KerberosString type?
            new ASN1Explicit(11, ASN1StringType.GENERALSTRING),// e-text
            // TODO: ignored
            new ASN1Explicit(12, ASN1OctetString.getInstance()),// e-data
    }) {
        {
            setOptional(2);// ctime
            setOptional(3);// cusec
            setOptional(7);// crealm
            setOptional(8);// cname
            setOptional(11);// e-text
            setOptional(12);// e-data
        }

        @Override
        protected Object getDecodedObject(BerInputStream in) throws IOException {

            Object[] values = (Object[]) in.content;

            KerberosErrorMessage message = new KerberosErrorMessage();

            message.ctime = (Date) values[2];
            if (values[3] != null) {
                message.cusec = ASN1Integer.toIntValue(values[3]);
            }
            message.stime = (Date) values[4];
            message.susec = ASN1Integer.toIntValue(values[5]);
            message.errorCode = ASN1Integer.toIntValue(values[6]);
            message.crealm = (String) values[7];
            message.cname = (PrincipalName) values[8];
            message.realm = (String) values[9];
            message.sname = (PrincipalName) values[10];
            message.etext = (String) values[11];

            return message;
        }

        @Override
        protected void getValues(Object object, Object[] values) {
            throw new RuntimeException("KerberosErrorMessage encoder is not implemented");
        }
    };

    public static final ASN1Explicit ASN1 = new ASN1Explicit(ASN1Constants.CLASS_APPLICATION,
            30, KRB_ERROR);
}
