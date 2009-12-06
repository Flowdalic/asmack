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

public class MessageProp {

    // privacy state
    private boolean privState;

    // quality-of-protection
    private int qop /* = 0 */;

    // duplicate token
    private boolean duplicate;

    // old token
    private boolean old;

    // unseq
    private boolean unseq;

    // gap
    private boolean gap;

    // minor status
    private int minorStatus /* = 0 */;

    // minor status string
    private String minorString;

    public MessageProp(boolean privState) {
        super();
        this.privState = privState;
    }

    public MessageProp(int qop, boolean privState) {
        this(privState);
        this.qop = qop;
    }

    public int getQOP() {
        return qop;
    }

    public boolean getPrivacy() {
        return privState;
    }

    public boolean isDuplicateToken() {
        return duplicate;
    }

    public boolean isOldToken() {
        return old;
    }

    public boolean isUnseqToken() {
        return unseq;
    }

    public boolean isGapToken() {
        return gap;
    }

    public int getMinorStatus() {
        return minorStatus;
    }

    public String getMinorString() {
        return minorString;
    }

    public void setQOP(int qop) {
        this.qop = qop;
    }

    public void setPrivacy(boolean privState) {
        this.privState = privState;
    }

    public void setSupplementaryStates(boolean duplicate, boolean old, boolean unseq,
            boolean gap, int minorStatus, String minorString) {
        this.duplicate = duplicate;
        this.old = old;
        this.unseq = unseq;
        this.gap = gap;
        this.minorStatus = minorStatus;
        this.minorString = minorString;
    }
}
