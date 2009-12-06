/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.    
 *
 * 
 */
package org.apache.qpid.management.common.mbeans;

import org.apache.qpid.management.common.mbeans.annotations.MBeanOperation;
import org.apache.qpid.management.common.mbeans.annotations.MBeanOperationParameter;

import javax.management.openmbean.TabularData;
import javax.management.MBeanOperationInfo;

public interface UserManagement
{

    String TYPE = "UserManagement";
    int VERSION = 2;
    
    //TabularType and contained CompositeType key/description information.
    //For compatibility reasons, DONT MODIFY the existing key values if expanding the set.
    String[] COMPOSITE_ITEM_NAMES = {"Username", "read", "write", "admin"};
    String[] COMPOSITE_ITEM_DESCRIPTIONS = {"Broker Login username", 
                                            "Management Console Read Permission", 
                                            "Management Console Write Permission", 
                                            "Management Console Admin Permission"};
    String[] TABULAR_UNIQUE_INDEX = {COMPOSITE_ITEM_NAMES[0]};

    //********** Operations *****************//
    /**
     * set password for user.
     * 
     * Since Qpid JMX API 1.2 this operation expects plain text passwords to be provided. Prior to this, MD5 hashed passwords were supplied.
     *
     * @param username The username to create
     * @param password The password for the user
     *
     * @return The result of the operation
     */
    @MBeanOperation(name = "setPassword", description = "Set password for user.",
                    impact = MBeanOperationInfo.ACTION)
    boolean setPassword(@MBeanOperationParameter(name = "username", description = "Username")String username,
                        @MBeanOperationParameter(name = "password", description = "Password")char[] password);

    /**
     * set rights for users with given details
     *
     * @param username The username to create
     * @param read     The set of permission to give the new user
     * @param write    The set of permission to give the new user
     * @param admin    The set of permission to give the new user
     *
     * @return The result of the operation
     */
    @MBeanOperation(name = "setRights", description = "Set access rights for user.",
                    impact = MBeanOperationInfo.ACTION)
    boolean setRights(@MBeanOperationParameter(name = "username", description = "Username")String username,
                      @MBeanOperationParameter(name = "read", description = "Administration read")boolean read,
                      @MBeanOperationParameter(name = "readAndWrite", description = "Administration write")boolean write,
                      @MBeanOperationParameter(name = "admin", description = "Administration rights")boolean admin);

    /**
     * Create users with given details
     *
     * Since Qpid JMX API 1.2 this operation expects plain text passwords to be provided. Prior to this, MD5 hashed passwords were supplied.
     * 
     * @param username The username to create
     * @param password The password for the user
     * @param read     The set of permission to give the new user
     * @param write    The set of permission to give the new user
     * @param admin    The set of permission to give the new user
     *
     * @return The result of the operation
     */
    @MBeanOperation(name = "createUser", description = "Create new user from system.",
                    impact = MBeanOperationInfo.ACTION)
    boolean createUser(@MBeanOperationParameter(name = "username", description = "Username")String username,
                       @MBeanOperationParameter(name = "password", description = "Password")char[] password,
                       @MBeanOperationParameter(name = "read", description = "Administration read")boolean read,
                       @MBeanOperationParameter(name = "readAndWrite", description = "Administration write")boolean write,
                       @MBeanOperationParameter(name = "admin", description = "Administration rights")boolean admin);

    /**
     * View users returns all the users that are currently available to the system.
     *
     * @param username The user to delete
     *
     * @return The result of the operation
     */
    @MBeanOperation(name = "deleteUser", description = "Delete user from system.",
                    impact = MBeanOperationInfo.ACTION)
    boolean deleteUser(@MBeanOperationParameter(name = "username", description = "Username")String username);


    /**
     * Reload the date from disk
     * 
     * Since Qpid JMX API 1.2 this operation reloads the password and authorisation files. Prior to this, only the authorisation file was reloaded.
     *
     * @return The result of the operation
     */
    @MBeanOperation(name = "reloadData", description = "Reload the authentication and authorisation files from disk.",
                    impact = MBeanOperationInfo.ACTION)
    boolean reloadData();

    /**
     * View users returns all the users that are currently available to the system.
     *
     * @return a table of users data (Username, read, write, admin)
     */
    @MBeanOperation(name = "viewUsers", description = "All users with access rights to the system.",
                    impact = MBeanOperationInfo.INFO)
    TabularData viewUsers();


}
