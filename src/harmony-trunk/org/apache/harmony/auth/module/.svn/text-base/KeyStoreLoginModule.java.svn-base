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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.AuthProvider;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.cert.CertPath;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import javax.security.auth.x500.X500Principal;
import javax.security.auth.x500.X500PrivateCredential;

public class KeyStoreLoginModule implements LoginModule {
    
    private static final String DEFAULT_KEYSTORE_TYPE = KeyStore.getDefaultType();
    
    private LoginModuleUtils.LoginModuleStatus status = new LoginModuleUtils.LoginModuleStatus();
    
    private Subject subject;
    
    private CallbackHandler callbackHandler;
    
    //private Map<String,?> sharedState;
    
    private Map<String,?> options;   
        
    private String keyStoreURL;
    
    private String keyStoreType;
    
    private Provider keyStoreProvider;
    
    private String keyStoreAlias;      
      
    private CertPath certPath;
    
    private X500Principal principal;
    
    private X500PrivateCredential privateCredential;

    private char[] keyStorePassword;
    
    private char[] privateKeyPassword;    
      
    private boolean needKeyStorePassword = true;
    
    private boolean needPrivateKeyPassword = true;
        
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
            subject.getPrincipals().add(principal);
            subject.getPublicCredentials().add(certPath);
            subject.getPrivateCredentials().add(privateCredential);
            status.committed();
            return true;
        }
    }

    public void initialize(Subject subject, CallbackHandler callbackHandler,
            Map<String, ?> sharedState, Map<String, ?> options) {
        if (null == options) {
            throw new NullPointerException();
        }
        this.subject = subject;
        this.callbackHandler = callbackHandler;
        // this.sharedState = sharedState;
        this.options = options;
        
        //clear state
        this.keyStoreAlias = null;
        this.keyStorePassword = null;
        this.privateKeyPassword = null;
        status.initialized();
    }

    public boolean login() throws LoginException {
        LoginModuleUtils.ACTION action = status.checkLogin();
        if (action.equals(LoginModuleUtils.ACTION.no_action)) {
            return true;
        }
        getKeyStoreParameters();
        getPrincipalsFromKeyStore();
        status.logined();
        return true;                
    }

    public boolean logout() throws LoginException {
        LoginModuleUtils.ACTION action = status.checkLogout();
        if (action.equals(LoginModuleUtils.ACTION.no_action)) {
            return true;
        }
        clear();
        return true;
    }
      

    private void getKeyStoreParameters() throws LoginException {
        // Get parameters from options.
        keyStoreURL = (String) options.get("keyStoreURL");
        keyStoreType = (String) options.get("keyStoreType");
        if (null == keyStoreType) {
            keyStoreType = DEFAULT_KEYSTORE_TYPE;
        }
        String keyStoreProvider = (String)options.get("keyStoreProvider");
        if(keyStoreProvider != null){
            this.keyStoreProvider = Security.getProvider(keyStoreProvider);
        }
        keyStoreAlias = (String) options.get("keyStoreAlias");
        String keyStorePasswordURL = (String) options.get("keyStorePasswordURL");
        String privateKeyPasswordURL = (String) options.get("privateKeyPasswordURL");
        boolean has_protected_authentication_path = "true"
                .equalsIgnoreCase((String) options.get("protected"));
        
        if (keyStoreType != null && keyStoreType.equals("PKCS11")) {
            if (!keyStoreURL.equals("NONE")
                    || privateKeyPasswordURL != null) {
                throw new LoginException(
                        "PKCS11 must have NONE as keyStoreURL and privateKeyPasswordURL unset");
            }            
            needPrivateKeyPassword = false;            
        }
        
        if (has_protected_authentication_path) {
            if (keyStorePasswordURL != null && privateKeyPasswordURL != null) {
                throw new LoginException(
                        "Protected authentication path must have keyStorePasswordURL and privateKeyPasswordURL unset");
            }
            needKeyStorePassword = false;
            needPrivateKeyPassword = false;
        }
        
        if (this.callbackHandler != null) {
            this.getParametersWithCallbackHandler();
        } else {
            this.getParametersWithoutCallbackHandler(keyStorePasswordURL,
                    privateKeyPasswordURL);
        }
        
        // privateKeyPassword is empty, use keystorepassword instead.
        if (needPrivateKeyPassword
                && (privateKeyPassword == null || privateKeyPassword.length == 0)) {
            privateKeyPassword = keyStorePassword;
        }
    }
    
    
    private void getParametersWithCallbackHandler() throws LoginException {
        ArrayList<Callback> callbacks = new ArrayList<Callback>();
        NameCallback keyStoreAliasNameCallback = new NameCallback(
                "KeyStore Alias");
        callbacks.add(keyStoreAliasNameCallback);
        PasswordCallback keyStorePasswordCallback = null;
        if (needKeyStorePassword) {
            keyStorePasswordCallback = new PasswordCallback(
                    "KeyStore password", false);
            callbacks.add(keyStorePasswordCallback);
        }
        PasswordCallback privateKeyPasswordCallback = null;
        if (needPrivateKeyPassword) {
            privateKeyPasswordCallback = new PasswordCallback(
                    "PrivateKey password", false);
            callbacks.add(privateKeyPasswordCallback);
        }

        try {
            callbackHandler.handle(callbacks.toArray(new Callback[callbacks
                    .size()]));
        } catch (Exception e) {
            throw new LoginException(e.toString());
        }
        keyStoreAlias = keyStoreAliasNameCallback.getName();
        if (needKeyStorePassword) {
            keyStorePassword = keyStorePasswordCallback.getPassword();
        }
        if (needPrivateKeyPassword) {
            privateKeyPassword = privateKeyPasswordCallback.getPassword();
        }
    }
    
    private void getParametersWithoutCallbackHandler(
            String keyStorePasswordURL, String privateKeyPasswordURL)
            throws LoginException {
        InputStream keyStorePasswordInputStream = null;
        InputStream privateKeyPasswordInputStream = null;
        try {
            if (keyStorePasswordURL != null) {
                keyStorePasswordInputStream = new URL(keyStorePasswordURL)
                        .openStream();
                this.keyStorePassword = LoginModuleUtils
                        .getPassword(keyStorePasswordInputStream);
            }

            if (privateKeyPasswordURL != null) {
                privateKeyPasswordInputStream = new URL(privateKeyPasswordURL)
                        .openStream();
                privateKeyPassword = LoginModuleUtils
                        .getPassword(privateKeyPasswordInputStream);
            }
        } catch (Exception e) {

        } finally {
            if (keyStorePasswordInputStream != null) {
                try {
                    keyStorePasswordInputStream.close();
                } catch (IOException e1) {
                }
            }
            if (privateKeyPasswordInputStream != null) {
                try {
                    privateKeyPasswordInputStream.close();
                } catch (IOException e1) {
                }
            }
        }

        if (null == keyStoreURL || (needKeyStorePassword && null == keyStorePassword)) {
            throw new LoginException(
                    "Failure to get KeyStore or KeyStore Password");
        }
    }

    private void getPrincipalsFromKeyStore() throws LoginException {

        InputStream keyStoreInputStream;
        try {

            KeyStore keyStore = keyStoreProvider == null ? KeyStore
                    .getInstance(keyStoreType) : KeyStore.getInstance(
                    keyStoreType, keyStoreProvider);
            keyStoreInputStream = keyStoreURL.equals("NONE") ? null : new URL(
                    keyStoreURL).openStream();

            keyStore.load(keyStoreInputStream, keyStorePassword);
            Certificate[] certificates = keyStore
                    .getCertificateChain(keyStoreAlias);
            if (null == certificates || certificates.length == 0) {
                throw new FailedLoginException(
                        "Cannot find certificate path for " + keyStoreAlias);
            }
            List<Certificate> list = new ArrayList<Certificate>(
                    certificates.length);
            for (int i = 0; i < certificates.length; i++) {
                list.add(certificates[i]);
            }
            CertificateFactory certificateFactory = CertificateFactory
                    .getInstance("X.509");
            certPath = certificateFactory.generateCertPath(list);

            X509Certificate firstCertificate = (X509Certificate) certificates[0];
            principal = new X500Principal(firstCertificate.getSubjectDN()
                    .getName());

            Key privateKey = keyStore.getKey(keyStoreAlias, privateKeyPassword);
            if (null == privateKey || !(privateKey instanceof PrivateKey)) {
                throw new FailedLoginException("Cannot find private key for "
                        + keyStoreAlias);
            }
            privateCredential = new X500PrivateCredential(firstCertificate,
                    (PrivateKey) privateKey, keyStoreAlias);

        } catch (Exception e) {
            if (e instanceof LoginException) {
                throw (LoginException) e;
            }
            throw new LoginException(e.toString());
        }
    }  
    
    private void clear() throws LoginException {
        LoginModuleUtils.clearPassword(keyStorePassword);
        keyStorePassword = null;
        LoginModuleUtils.clearPassword(privateKeyPassword);
        privateKeyPassword = null;
        
        if (keyStoreProvider instanceof AuthProvider) {
            ((AuthProvider) (keyStoreProvider)).logout();
        }
        
        if (principal != null) {
            subject.getPrincipals().remove(principal);
            principal = null;
        }
        if (certPath != null) {
            subject.getPublicCredentials().remove(certPath);
            certPath = null;
        }
        if (privateCredential != null) {
            subject.getPrivateCredentials().remove(privateCredential);
            privateCredential.destroy();
            privateCredential = null;
        }
        status.logouted();
    } 
  
}
