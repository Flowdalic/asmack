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

import java.util.Map;

import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;

abstract public class SharedStateManager {

    private boolean useFirstPass = false;

    private boolean tryFirstPass = false;

    private boolean storePass = false;

    private boolean clearPass = false;

    protected DebugUtil debugUtil;

    protected Map<String, Object> sharedState;

    @SuppressWarnings("unchecked")
    protected void prepareSharedState(Map<String, ?> sharedState,
            final Map<String, ?> options) {
        this.sharedState = (Map<String, Object>) sharedState;

        useFirstPass = false;
        tryFirstPass = false;
        storePass = false;
        clearPass = false;
        Object optionValue = null;

        optionValue = options.get("useFirstPass");
        if (optionValue != null && optionValue.equals("true")) {
            useFirstPass = true;
        }

        optionValue = options.get("tryFirstPass");
        if (optionValue != null && optionValue.equals("true")) {
            tryFirstPass = true;
            useFirstPass = false;
        }

        optionValue = options.get("storePass");
        if (optionValue != null && optionValue.equals("true")) {
            storePass = true;
        }

        optionValue = options.get("clearPass");
        if (optionValue != null && optionValue.equals("true")) {
            clearPass = true;
            storePass = false;
        }
    }

    protected void loginWithSharedState() throws LoginException {
        if (useFirstPass || tryFirstPass) {
            getUserIdentityFromSharedStatus();
        } else {
            getUserIdentityFromCallbackHandler();
        }
        boolean passAuth = false;
        passAuth = mainAuthenticationProcess();
        if (!passAuth) {
            if (tryFirstPass) {
                debugUtil.recordDebugInfo("["
                        + getModuleName()
                        + "] tryFirstPass failed with:"
                        + new FailedLoginException("Login incorrect")
                                .toString() + "\n");
                getUserIdentityFromCallbackHandler();
                passAuth = mainAuthenticationProcess();
                if (!passAuth) {
                    debugUtil.recordDebugInfo("[" + getModuleName()
                            + "] regular authentication failed\n");
                    debugUtil.printAndClearDebugInfo();
                    throw new FailedLoginException("Login incorrect");
                } else {
                    debugUtil.recordDebugInfo("[" + getModuleName()
                            + "] regular authentication succeeded\n");
                }
            } else {
                if (useFirstPass) {
                    debugUtil.recordDebugInfo("["
                            + getModuleName()
                            + "] useFirstPass failed with:"
                            + new FailedLoginException("Login incorrect")
                                    .toString() + "\n");
                } else {
                    debugUtil.recordDebugInfo("[" + getModuleName()
                            + "] regular authentication failed\n");
                }
                debugUtil.printAndClearDebugInfo();
                throw new FailedLoginException("Login incorrect");
            }
        } else {
            if (tryFirstPass) {
                debugUtil.recordDebugInfo("[" + getModuleName()
                        + "] tryFirstPass ");
            } else if (useFirstPass) {
                debugUtil.recordDebugInfo("[" + getModuleName()
                        + "] useFirstPass ");
            } else {
                debugUtil.recordDebugInfo("[" + getModuleName()
                        + "] regular authentication ");
            }
            debugUtil.recordDebugInfo("succeeded\n");
        }
        storePass();
    }

    private void getUserIdentityFromSharedStatus() throws LoginException {
        if (sharedState == null)
            throw new LoginException("No shared status");
        String userName = (String) sharedState
                .get("javax.security.auth.login.name");
        char[] userPassword = (char[]) sharedState
                .get("javax.security.auth.login.password");
        if (userName == null || userPassword == null) {
            throw new LoginException(
                    "Cannot get user ID or user password from shared state");
        }
        setUserName(userName);
        setUserPassword(userPassword);
    }

    protected void storePass() throws LoginException {
        if (storePass) {
            if (sharedState == null) {
                throw new LoginException("No Shared State");
            }
            if (sharedState.get("javax.security.auth.login.name") == null) {
                sharedState
                        .put("javax.security.auth.login.name", getUserName());
            }
            if (sharedState.get("javax.security.auth.login.password") == null) {
                sharedState.put("javax.security.auth.login.password",
                        getUserPassword());
            }
        }
    }

    protected void clearPass() throws LoginException {
        if (clearPass) {
            if (sharedState == null) {
                throw new LoginException("No Shared State");
            }
            sharedState.remove("javax.security.auth.login.name");
            sharedState.remove("javax.security.auth.login.password");
        }
    }

    abstract protected boolean mainAuthenticationProcess()
            throws LoginException;

    abstract protected void getUserIdentityFromCallbackHandler()
            throws LoginException;

    abstract protected void setUserName(String userName);

    abstract protected String getUserName();

    abstract protected void setUserPassword(char[] userPassword);

    abstract protected char[] getUserPassword();

    abstract protected String getModuleName();
}
