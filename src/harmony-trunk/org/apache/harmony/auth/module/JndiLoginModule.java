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

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import javax.naming.Context;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.apache.harmony.auth.UnixNumericGroupPrincipal;
import org.apache.harmony.auth.UnixNumericUserPrincipal;
import org.apache.harmony.auth.UnixPrincipal;

public class JndiLoginModule extends SharedStateManager implements LoginModule {

    public final String USER_PROVIDER = "group.provider.url";

    public final String GROUP_PROVIDER = "user.provider.url";

    //harmony lacks jndi provider
    private final String JNDI_FACTORY = "";

    private LoginModuleUtils.LoginModuleStatus status = new LoginModuleUtils.LoginModuleStatus();

    private Subject subject;

    private CallbackHandler callbackHandler;

    private Map<String, ?> options;

    private String jndiUserProvider;

    private String jndiGroupProvider;

    private String userID;

    private char[] userPassword;

    private Long uidNumber;

    private Long gidNumber;

    private UnixPrincipal unixPrincipal;

    private UnixNumericUserPrincipal unixNumericUserPrincipal;

    private Set<UnixNumericGroupPrincipal> unixNumericGroupPrincipals;

    public boolean abort() throws LoginException {
        LoginModuleUtils.ACTION action = status.checkAbout();
        if (action.equals(LoginModuleUtils.ACTION.no_action)) {
            if (status.isLoggined()) {
                return true;
            } else {
                return false;
            }
        }
        clear();
        debugUtil.recordDebugInfo("[JndiLoginModule] aborted authentication failed\n");
        if(status.isCommitted()){
        	debugUtil.recordDebugInfo("[JndiLoginModule]: logged out Subject\n");
        }
        debugUtil.printAndClearDebugInfo();
        status.logouted();
        return true;
    }

    public boolean commit() throws LoginException {
        LoginModuleUtils.ACTION action = status.checkCommit();
        switch (action) {
        case no_action:
            return true;
        case logout:
            clear();
            throw new LoginException("Fail to login");
        default:
            if (subject.isReadOnly()) {
                clear();
                throw new LoginException("Subject is readonly.");
            }
            subject.getPrincipals().add(unixPrincipal);
            debugUtil.recordDebugInfo("[JndiLoginModule] added UnixPrincipal to Subject\n");
            subject.getPrincipals().add(unixNumericUserPrincipal);
            debugUtil.recordDebugInfo("[JndiLoginModule] added UnixNumericUserPrincipal to Subject\n");
            for (Principal principal : unixNumericGroupPrincipals) {
                subject.getPrincipals().add(principal);
            }
            debugUtil.recordDebugInfo("[JndiLoginModule] added UnixNumericGroupPrincipal(s) to Subject\n");
            debugUtil.printAndClearDebugInfo();
            status.committed();
            clearPass();
            return true;
        }
    }

    @SuppressWarnings("unchecked")
    public void initialize(Subject subject, CallbackHandler callbackHandler,
            Map<String, ?> sharedState, Map<String, ?> options) {
        this.subject = subject;
        this.callbackHandler = callbackHandler;
        if (null == options) {
            throw new NullPointerException();
        }
        this.options = options;
        debugUtil = new DebugUtil(options);
        prepareSharedState(sharedState, options);
        status.initialized();
    }

    public boolean login() throws LoginException {
        LoginModuleUtils.ACTION action = status.checkLogin();
        if (action.equals(LoginModuleUtils.ACTION.no_action)) {
            return true;
        }
        getJndiParameters();
        loginWithSharedState();
        debugUtil.recordDebugInfo("[JndiLoginModule] user: '"+ userID + "' has UID: " + uidNumber + "\n");
        debugUtil.recordDebugInfo("[JndiLoginModule] user: '"+ userID + "' has GID: " + gidNumber + "\n");
        getPrinclpalsFromJndi();
        debugUtil.printAndClearDebugInfo();
        status.logined();
        return true;
    }

    public boolean logout() throws LoginException {
        LoginModuleUtils.ACTION action = status.checkLogout();
        if (action.equals(LoginModuleUtils.ACTION.no_action)) {
            return true;
        }
        clear();
        debugUtil.recordDebugInfo("[JndiLoginModule] logged out Subject\n");
        debugUtil.printAndClearDebugInfo();
        status.logouted();
        return true;
    }

    private void getJndiParameters() throws LoginException {
        jndiUserProvider = (String) options.get("user.provider.url");
        jndiGroupProvider = (String) options.get("group.provider.url");
        if (jndiUserProvider == null) {
            throw new LoginException("Unable to locate JNDI user provider");
        }
        if (jndiGroupProvider == null) {
            throw new LoginException("Unable to locate JNDI group provider");
        }
        debugUtil.recordDebugInfo("[JndiLoginModule] user provider: " + jndiUserProvider + "\n"
                +"[JndiLoginModule] group provider: " + jndiGroupProvider
                + "\n");
    }

    //not accomplished yet
    protected boolean mainAuthenticationProcess() throws LoginException {

        //check group provider
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, JNDI_FACTORY);
        env.put(Context.PROVIDER_URL, jndiGroupProvider);
        try {
            DirContext context = new InitialDirContext(env);
            context.close();
        } catch (NamingException e) {
            throw new LoginException(e.toString());
        }
        //check user
        env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, JNDI_FACTORY);
        env.put(Context.PROVIDER_URL, jndiUserProvider);
        Attribute passwordAttr;
        Attribute uidNumberAttr;
        Attribute gidNumberAttr;
        String jndiUserPassword = "";
        try {
            DirContext context = new InitialDirContext(env);
            SearchControls constraints = new SearchControls();
            constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
            Attributes attrs = new BasicAttributes("uid", userID);
            NamingEnumeration ne = context.search("", attrs);
            String[] attrIds = new String[] { "userPassword", "uidNumber",
                    "gidNumber" };
            if (ne.hasMore()) {
                NameClassPair item = (NameClassPair) ne.next();
                Attributes userAttrs = context.getAttributes(item.getName(),
                        attrIds);
                passwordAttr = userAttrs.get("userPassword");
                if (passwordAttr == null) {
                    throw new LoginException("Cannot get user password");
                }
                jndiUserPassword = new String((byte[])passwordAttr.get());
                if (!jndiUserPassword.equals(crypto(new String(userPassword)))) {
                    return false;
                }

                uidNumberAttr = userAttrs.get("uidNumber");
                if (uidNumberAttr == null) {
                    throw new LoginException("Cannot get uidNumber information");
                }
                uidNumber = Long.valueOf((String) uidNumberAttr.get());

                gidNumberAttr = userAttrs.get("gidNumber");
                if (gidNumberAttr == null) {
                    throw new LoginException("Cannot get gidNumber information");
                }
                gidNumber = Long.valueOf((String) gidNumberAttr.get());
            }
        } catch (NamingException e) {
            throw new LoginException(e.toString());
        }

        return true;
    }

    private void getPrinclpalsFromJndi() {
        unixPrincipal = new UnixPrincipal(userID);
        unixNumericUserPrincipal = new UnixNumericUserPrincipal(uidNumber);
        unixNumericGroupPrincipals = new HashSet<UnixNumericGroupPrincipal>();
        unixNumericGroupPrincipals.add(new UnixNumericGroupPrincipal(gidNumber,
                true));
    }

    protected void getUserIdentityFromCallbackHandler() throws LoginException {
        
        if (callbackHandler == null) {
            throw new LoginException("no CallbackHandler available");
        }
        ArrayList<Callback> callbacks = new ArrayList<Callback>();
        NameCallback jndiNameCallback = new NameCallback("User ID");
        callbacks.add(jndiNameCallback);
        PasswordCallback jndiPasswordCallback = new PasswordCallback(
                "User Password", false);
        callbacks.add(jndiPasswordCallback);
        try {
            callbackHandler.handle(callbacks.toArray(new Callback[callbacks
                    .size()]));
        } catch (Exception e) {
            throw new LoginException(e.toString());
        }
        userID = jndiNameCallback.getName();
        userPassword = jndiPasswordCallback.getPassword();
    }
    
    private void clear() throws LoginException {
        LoginModuleUtils.clearPassword(userPassword);
        userPassword = null;
        if (unixPrincipal != null) {
            subject.getPrincipals().remove(unixPrincipal);
            unixPrincipal = null;
        }

        if (unixNumericUserPrincipal != null) {
            subject.getPrincipals().remove(unixNumericUserPrincipal);
            unixNumericUserPrincipal = null;
        }

        if (unixNumericGroupPrincipals != null) {
            for (UnixNumericGroupPrincipal ungp : unixNumericGroupPrincipals)
                subject.getPrincipals().remove(ungp);
            unixNumericGroupPrincipals.clear();
            unixNumericGroupPrincipals = null;
        }
        status.logouted();
    }

    private String crypto(String userPassword) {
        //need to implement a crypto algorithm
        return userPassword;
    }
    
    protected void setUserName(String userName){
    	this.userID = userName;
    }
    
    protected void setUserPassword(char[] userPassword){
    	this.userPassword = userPassword;
    }
    
    protected String getUserName(){
    	return userID;
    }
    
    protected char[] getUserPassword(){
    	return userPassword;
    }
    
    protected String getModuleName(){
    	return "JndiLoginModule"; 
    }
}
