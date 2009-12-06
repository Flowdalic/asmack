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

public class GSSException extends Exception {

    private static final long serialVersionUID = -2706218945227726672L;

    public static final int BAD_BINDINGS = 1;

    public static final int BAD_MECH = 2;

    public static final int BAD_MIC = 6;

    public static final int BAD_NAME = 3;

    public static final int BAD_NAMETYPE = 4;

    public static final int BAD_QOP = 14;

    public static final int BAD_STATUS = 5;

    public static final int CONTEXT_EXPIRED = 7;

    public static final int CREDENTIALS_EXPIRED = 8;

    public static final int DEFECTIVE_CREDENTIAL = 9;

    public static final int DEFECTIVE_TOKEN = 10;

    public static final int DUPLICATE_ELEMENT = 17;

    public static final int DUPLICATE_TOKEN = 19;

    public static final int FAILURE = 11;

    public static final int GAP_TOKEN = 22;

    public static final int NAME_NOT_MN = 18;

    public static final int NO_CONTEXT = 12;

    public static final int NO_CRED = 13;

    public static final int OLD_TOKEN = 20;

    public static final int UNAUTHORIZED = 15;

    public static final int UNAVAILABLE = 16;

    public static final int UNSEQ_TOKEN = 21;

    // error messages
    private static final String[] errorMessages = { "BAD BINDINGS", "BAD MECH", //$NON-NLS-1$ //$NON-NLS-2$
            "BAD NAME", "BAD NAMETYPE", "BAD STATUS", "BAD MIC", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            "CONTEXT EXPIRED", "CREDENTIALS EXPIRED", "DEFECTIVE CREDENTIAL", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            "DEFECTIVE TOKEN", "FAILURE", "NO CONTEXT", "NO CRED", "BAD QOP", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
            "UNAUTHORIZED", "UNAVAILABLE", "DUPLICATE ELEMENT", "NAME NOT MN", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            "DUPLICATE TOKEN", "OLD TOKEN", "UNSEQ TOKEN", "GAP TOKEN" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

    // major code
    private int major = FAILURE;

    // minor code
    private int minor;

    // minor string
    private String minorMessage;

    // major string
    private String majorString;

    public GSSException(int majorCode) {
        super();
        if (majorCode > 0 && majorCode <= 22) {
            this.major = majorCode;
        }
        this.majorString = errorMessages[major - 1];
    }

    public GSSException(int majorCode, int minorCode, String minorString) {
        this(majorCode);
        this.minor = minorCode;
        this.minorMessage = minorString;
    }

    public int getMajor() {
        return major;
    }

    public String getMajorString() {
        return majorString;
    }

    public int getMinor() {
        return minor;
    }

    public String getMinorString() {
        if (minor == 0) {
            return null;
        }
        return minorMessage;
    }

    @Override
    public String getMessage() {
        String tmp = getMinorString();
        String tmp2 = getMajorString();
        if (tmp == null) {
            return tmp2;
        }
        return tmp2 + " (" + tmp + ')'; //$NON-NLS-1$
    }

    public void setMinor(int minorCode, String minorString) {
        this.minor = minorCode;
        this.minorMessage = minorString;
    }

    @Override
    public String toString() {
        return "GSSException: " + getMessage(); //$NON-NLS-1$
    }
}
