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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.security.auth.kerberos.KerberosKey;
import javax.security.auth.kerberos.KerberosPrincipal;

import org.apache.harmony.auth.internal.nls.Messages;
import org.apache.harmony.security.asn1.DerInputStream;

/**
 * @see http://www.ietf.org/rfc/rfc4120.txt
 */
public class KrbClient {

    // default kdc server
    private static final String DEFAULT_KDC = "java.security.krb5.kdc"; //$NON-NLS-1$

    // default realm
    private static final String DEFAULT_REALM = "java.security.krb5.realm"; //$NON-NLS-1$

    private static String kdc;

    private static String realm;

    private static int port = 88;//default

    private static final int BUF_SIZE = 1024;

    private static boolean isInitialized = false;
    
    private KrbClient() {
        // no objects
    }

    public static synchronized String getRealm() throws KerberosException {
        if (!isInitialized) {
            setEnv();
        }
        return realm;
    }

    private static void setEnv() throws KerberosException {
        if (isInitialized) {
            return;
        }

        //TODO put in doPrivileged
        kdc = System.getProperty(DEFAULT_KDC);
        realm = System.getProperty(DEFAULT_REALM);
        if (kdc == null && realm != null || kdc != null && realm == null) {
            // both properties should be set or unset together
            throw new KerberosException();//FIXME message
        } else if (kdc == null && realm == null) {

            // reading config from configuration file 'krb5.conf'
            KrbConfig config = null;
            try {
                config = KrbConfig.getSystemConfig();
            } catch (IOException e) {
                throw new KerberosException(e.getMessage());
            }

            if (config == null) {
                // no config file was found
                throw new KerberosException();//FIXME err msg
            }
            realm = config.getValue("libdefaults", //$NON-NLS-1$
                    "default_realm"); //$NON-NLS-1$
            
            //TODO set KDC
            return;
        }

        int pos = kdc.indexOf(':');
        if (pos != -1) {
            port = Integer.parseInt(kdc.substring(pos + 1));
            kdc = kdc.substring(0, pos);
        }
        
        isInitialized = true;
    }

    /**
     * Get credentials from Authentication Service.
     * 
     * @param address - service host
     * @param port - service port
     * @param cname - client's principal identifier
     * @param realm - client's realm
     * @return - ticket
     */
    public static KDCReply doAS(PrincipalName cname, char[] password)
            throws KerberosException {

        setEnv();

        PrincipalName sname = new PrincipalName(PrincipalName.NT_SRV_XHST,
                new String[] { "krbtgt", realm }); //$NON-NLS-1$

        KDCRequest request = new KDCRequest(KDCRequest.AS_REQ, cname, realm,
                sname);

        try {
            DatagramSocket socket = request.send(InetAddress.getByName(kdc),
                    port);

            ByteArrayOutputStream out = new ByteArrayOutputStream(BUF_SIZE);

            byte[] buf = new byte[BUF_SIZE];

            DatagramPacket resp = new DatagramPacket(buf, buf.length);

            int bytesRead = BUF_SIZE;
            while (bytesRead == BUF_SIZE) {
                socket.receive(resp);

                bytesRead = resp.getLength();
                out.write(buf, resp.getOffset(), bytesRead);
            }
            DerInputStream in = new DerInputStream(out.toByteArray());

            if (in.tag == KDCReply.AS_REP_ASN1.constrId) { //TODO AS reply
                KDCReply reply = (KDCReply) KDCReply.AS_REP_ASN1.decode(in);

                KerberosKey key = new KerberosKey(new KerberosPrincipal(cname
                        .getName()[0]
                        + '@' + realm, cname.getType()), password, "DES");

                reply.decrypt(key);

                return reply;
            } else if (in.tag == KerberosErrorMessage.ASN1.constrId) {
                KerberosErrorMessage errMsg = KerberosErrorMessage.decode(in);
                // auth.52=Error code: {0}
                throw new KerberosException(Messages.getString(
                        "auth.52", errMsg.getErrorCode())); //$NON-NLS-1$
            } else {
                throw new KerberosException(); //FIXME
            }

        } catch (IOException e) {
            throw new KerberosException(e.getMessage()); //FIXME 
        }
    }

    public static KDCReply doTGS() throws KerberosException {
        setEnv();

        return null;
    }
}
