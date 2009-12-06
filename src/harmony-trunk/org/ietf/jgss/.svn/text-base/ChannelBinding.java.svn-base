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

package org.ietf.jgss;

import java.net.InetAddress;
import java.util.Arrays;

public class ChannelBinding {
    //initiator address
    private InetAddress initAddr;

    //acceptor address
    private InetAddress acceptAddr;

    //application data
    private byte[] appData;

    public ChannelBinding(InetAddress initAddr, InetAddress acceptAddr, byte[] appData) {
        this(appData);
        this.initAddr = initAddr;
        this.acceptAddr = acceptAddr;
    }

    public ChannelBinding(byte[] appData) {
        super();
        if (appData != null) {
            this.appData = new byte[appData.length];
            System.arraycopy(appData, 0, this.appData, 0, appData.length);
        }
    }

    public InetAddress getInitiatorAddress() {
        return initAddr;
    }

    public InetAddress getAcceptorAddress() {
        return acceptAddr;
    }

    public byte[] getApplicationData() {
        byte[] bytes = null;
        if (appData != null) {
            bytes = new byte[appData.length];
            System.arraycopy(appData, 0, bytes, 0, appData.length);
        }
        return bytes;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ChannelBinding)) {
            return false;
        }
        ChannelBinding another = (ChannelBinding) obj;
        if (initAddr != another.initAddr
                && (initAddr == null || !initAddr.equals(another.initAddr))) {
            return false;
        }

        if (acceptAddr != another.acceptAddr
                && (acceptAddr == null || !acceptAddr.equals(another.acceptAddr))) {
            return false;
        }

        return Arrays.equals(appData, another.appData);
    }

    @Override
    public int hashCode() {
        if (initAddr != null) {
            return initAddr.hashCode();
        }
        if (acceptAddr != null) {
            return acceptAddr.hashCode();
        }
        if (appData != null) {
            int hashCode = 0;
            for (byte element : appData) {
                hashCode = 31 * hashCode + element;
            }
            return hashCode;
        }
        return 1;
    }
}
