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

import java.io.IOException;

import org.apache.qpid.management.common.mbeans.annotations.MBeanAttribute;

/**
 * Interface for the ServerInformation MBean
 * @since Qpid JMX API 1.3
 */
public interface ServerInformation
{
    String TYPE = "ServerInformation";
    int VERSION = 1;
    
    /* API version info for the brokers JMX management interface
     * 
     *  As the ServerInformation MBean was only introduced in Qpid JMX API 1.3 it is not possible to use it
     *  for identifying earlier broker JMX APIs for compatibility purposes. However, this can be accomplished 
     *  by inspecting the 'version' property key of the UserManagement MBean JMX ObjectName. This was first 
     *  introduced in Qpid JMX API 1.2 and so if present in the absence of the ServerInformation MBean 
     *  indicates that version. If it is not present then a null value will be returned upon inspection and 
     *  Qpid JMX API 1.1 can be assumed.
     */
    int QPID_JMX_API_MAJOR_VERSION = 1;
    int QPID_JMX_API_MINOR_VERSION = 5;
    
    
    /**
     * Attribute to represent the major version number for the management API.
     * @return The major management version number.
     * @since Qpid JMX API 1.3
     */
    @MBeanAttribute(name="ManagementApiMajorVersion", 
                    description = "The major version number for the broker management API")
    Integer getManagementApiMajorVersion() throws IOException;
    
    /**
     * Attribute to represent the minor version number for the management API.
     * @return The minor management version number.
     * @since Qpid JMX API 1.3
     */
    @MBeanAttribute(name="ManagementApiMinorVersion", 
                    description = "The minor version number for the broker management API")
    Integer getManagementApiMinorVersion() throws IOException;
    
    /**
     * Attribute to represent the build version string.
     * @return The build version string
     * @since Qpid JMX API 1.3
     */
    @MBeanAttribute(name="BuildVersion", 
                    description = "The repository build version string")
    String getBuildVersion() throws IOException;
    
    /**
     * Attribute to represent the product version string.
     * @return The product version string
     * @since Qpid JMX API 1.3
     */
    @MBeanAttribute(name="ProductVersion", 
                    description = "The product version string")
    String getProductVersion() throws IOException;
}
