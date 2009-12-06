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

package org.apache.harmony.auth.login;

import java.io.File;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.security.auth.AuthPermission;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;

import org.apache.harmony.auth.internal.nls.Messages;
import org.apache.harmony.security.fortress.PolicyUtils;

/**
 * Default Configuration implementation based on login configuration files.
 * This implementation recognizes text files, consisting of clauses with the
 * following syntax:
 * 
 * <pre>
 * Application {
 *          LoginModuleClass Flag [Options ...];
 *          LoginModuleClass Flag [Options ...];
 * };
 * </pre>
 */
public class DefaultConfiguration extends Configuration {

    // system property for dynamically added login configuration file location.
    private static final String JAVA_SECURITY_LOGIN_CONFIG = "java.security.auth.login.config"; //$NON-NLS-1$

    // location of login configuration file
    private static final String LOGIN_CONFIG_URL_PREFIX = "login.config.url."; //$NON-NLS-1$

    // creates a AuthPermission object 
    private static final AuthPermission REFRESH_LOGIN_CONFIGURATION = new AuthPermission(
            "refreshLoginConfiguration"); //$NON-NLS-1$

    // set of application entry
    private Map<String, List<AppConfigurationEntry>> configurations = Collections
            .synchronizedMap(new HashMap<String, List<AppConfigurationEntry>>());

    /**
     * Default a constructor
     */
    public DefaultConfiguration() {
        super();

        init();
    }

    @Override
    public AppConfigurationEntry[] getAppConfigurationEntry(String applicationName) {

        List<AppConfigurationEntry> list = configurations.get(applicationName);

        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.toArray(new AppConfigurationEntry[list.size()]);
    }

    @Override
    public synchronized void refresh() {

        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(REFRESH_LOGIN_CONFIGURATION);
        }
        configurations.clear();

        init();
    }

    /**
     * Initialize a login configuration file
     */
    private void init() {

        Map<String, List<AppConfigurationEntry>> fresh = new HashMap<String, List<AppConfigurationEntry>>();
        Properties system = new Properties(AccessController
                .doPrivileged(new PolicyUtils.SystemKit()));
        system.setProperty("/", File.separator); //$NON-NLS-1$
        URL[] policyLocations = PolicyUtils.getPolicyURLs(system, JAVA_SECURITY_LOGIN_CONFIG,
                LOGIN_CONFIG_URL_PREFIX);

        for (URL url : policyLocations) {
            try {
                fresh.putAll(DefaultConfigurationParser.configParser(url, system, fresh));
            } catch (Exception e) {
                //TODO: log warning
                //new SecurityException("Unable to load a login configuration file");
            }
        }
        
        // if location is not define then get a config file from user's directory 
        if (policyLocations.length == 0) {
            
            // check presence of ${user.home}/.java.login.config
            File userLoginConfig = AccessController
                    .doPrivileged(new PrivilegedAction<File>() {
                        public File run() {
                            File f = new File(System.getProperty("user.home") + //$NON-NLS-1$
                                    File.separatorChar + ".java.login.config"); //$NON-NLS-1$
                            if (f.exists()) {
                                return f;
                            }
                            return null;
                        }
                    });
            if (userLoginConfig == null) {
                // auth.53: Unable to locate a login configuration
                throw new SecurityException(Messages.getString("auth.53")); //$NON-NLS-1$
            }
            
            try {
                fresh.putAll(DefaultConfigurationParser.configParser(
                        userLoginConfig.toURL(), system, fresh));
            } catch (Exception e) {
                //TODO: log warning
                //throw new SecurityException ("Unable to load a login configuration file");
            }
        }
        configurations = fresh;
    }
}
