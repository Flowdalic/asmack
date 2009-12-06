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

package org.apache.harmony.auth.module;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import javax.security.auth.login.LoginException;

public class LoginModuleUtils {
    
    public static enum ACTION {
        no_action, login, commit, logout
    };
    
    public final static class LoginModuleStatus {        

        private static enum PHASE {
            uninitialized, initialized, logined, committed
        };

        private PHASE phase;
        
        public LoginModuleStatus(){
            phase = PHASE.uninitialized;
        }
        
        public void initialized(){
            phase = PHASE.initialized;
        }
        
        public void logined(){
            phase = PHASE.logined;
        }
        
        public void committed(){
            phase = PHASE.committed;
        }
        
        public void logouted(){
            phase = PHASE.logined;
        }
        
        public boolean isLoggined(){
            return phase.equals(PHASE.logined) || phase.equals(PHASE.committed);
        }
        
        public boolean isCommitted(){
            return phase.equals(PHASE.committed);
        }

        
        public ACTION checkAbout() {
            switch (phase) {
            case uninitialized:
            case initialized:
                return ACTION.no_action;
            default:
                return ACTION.logout;
            }
        }
        
        public ACTION checkLogin() throws LoginException {
            switch (phase) {
            case uninitialized:
                throw new LoginException("Login Module is not initialized.");
            case initialized:
                return ACTION.login;
            default:
                return ACTION.no_action;
            }
        }

        public ACTION checkCommit() throws LoginException {
            switch (phase) {
            case uninitialized:
                throw new LoginException("Login Module is not initialized.");
            case initialized:
                return ACTION.logout;
            case logined:
                return ACTION.commit;
            default:
                return ACTION.no_action;
            }
        }

        public ACTION checkLogout() throws LoginException {
            switch (phase) {
            case uninitialized:
                throw new LoginException("Login Module is not initialized.");
            case initialized:
            case logined:
                return ACTION.no_action;
            default:
                return ACTION.logout;
            }
        }
    }
    
    
    /**
     * Reads the password stored in an inputstream to a char array.
     * 
     * @param in
     *            an inputstream which stores the password.
     * @return a char array which contains the password.
     * @throws IOException
     */
    public static char[] getPassword(InputStream in) throws IOException {
        // just read one line.
        char[] passwdBuffer = new char[512];
        int passwdLength = 0;
        boolean hasCarriage = false;

        boolean isFinished = false;
        BufferedInputStream bis = new BufferedInputStream(in);
        byte[] byteBuffer = new byte[512];
        byte[] utf8Bytes;
        int readLength;
        while (!isFinished && (readLength = bis.read(byteBuffer)) != -1) {
            utf8Bytes = new String(byteBuffer, 0, readLength).getBytes("UTF-8"); //$NON-NLS-1$
            for (int index = 0; index < readLength; index++) {
                if (utf8Bytes[index] == '\n') {
                    isFinished = true;
                    break;
                }

                if (hasCarriage) {
                    passwdBuffer = appendChars(passwdBuffer, '\r',
                            passwdLength++);
                    hasCarriage = false;
                }

                if (utf8Bytes[index] == '\r') {
                    hasCarriage = true;
                } else {
                    passwdBuffer = appendChars(passwdBuffer,
                            (char) utf8Bytes[index], passwdLength++);
                }
            }
        }

        if (passwdLength == 0) {
            return null;
        }

        char[] passwd = new char[passwdLength];
        System.arraycopy(passwdBuffer, 0, passwd, 0, passwdLength);
        return passwd;
    }

    private static char[] appendChars(char[] src, char c, int position) {
        char[] dest = src;
        if (position == src.length) {
            dest = new char[src.length * 2];
            System.arraycopy(src, 0, dest, 0, src.length);
        }
        dest[position] = c;
        return dest;
    }
    
    /**
     * Cleans the password stored in one char array.
     * 
     * @param password
     *            the char array which contains password.
     */
    public static void clearPassword(char[] password) {
        if (password == null || password.length == 0) {
            return;
        }
        Arrays.fill(password, '\0');
    }
}
