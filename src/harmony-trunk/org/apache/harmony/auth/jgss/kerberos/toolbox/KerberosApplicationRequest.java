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

package org.apache.harmony.auth.jgss.kerberos.toolbox;

import javax.crypto.SecretKey;
import javax.security.auth.kerberos.KerberosTicket;

// TODO The Request for encoding includes TGS while decoding includes
// SessionKey. Maybe more information, for example, peer Principal is required
// in decoding.
public class KerberosApplicationRequest {
    private long seqNum;

    private boolean[] options;

    private KerberosTicket tgs;

    private SecretKey sessionKey;

    public KerberosApplicationRequest(long seqNum, boolean[] options,
            KerberosTicket tgs, SecretKey sessionKey) {
        this.seqNum = seqNum;
        this.options = options;
        this.tgs = tgs;
        this.sessionKey = sessionKey;
    }

    public long getSeqNum() {
        return seqNum;
    }

    public boolean[] getOptions() {
        return options;
    }

    public KerberosTicket getTGS() {
        return tgs;
    }

    public SecretKey getSessionKey() {
        return sessionKey;
    }
}
