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
import org.apache.qpid.management.common.mbeans.annotations.MBeanOperation;
import org.apache.qpid.management.common.mbeans.annotations.MBeanOperationParameter;

import javax.management.MBeanOperationInfo;
import javax.management.openmbean.TabularData;

/**
 * Interface for the LoggingManagement MBean
 * @since Qpid JMX API 1.2
 */
public interface LoggingManagement
{
    String TYPE = "LoggingManagement";
    int VERSION = 2;
    
    //TabularType and contained CompositeType key/description information
    //For compatibility reasons, DONT MODIFY the existing key values if expanding the set. 
    String[] COMPOSITE_ITEM_NAMES = {"LoggerName", "Level"};
    String[] COMPOSITE_ITEM_DESCRIPTIONS = {"Name of the logger", "Level of the logger"};
    String[] TABULAR_UNIQUE_INDEX = {COMPOSITE_ITEM_NAMES[0]};
    
    /**
     * Attribute to represent the log4j xml configuration file's LogWatch interval.
     * @return The logwatch interval in seconds.
     * @since Qpid JMX API 1.2
     */
    @MBeanAttribute(name="Log4jLogWatchInterval", 
                    description = "The log4j xml configuration file LogWatch interval (in seconds). 0 indicates not being checked.")
    Integer getLog4jLogWatchInterval();
    
    /**
     * Attribute to represent the available log4j logger output levels.
     * @return The logging level names.
     * @since Qpid JMX API 1.2
     */
    @MBeanAttribute(name="AvailableLoggerLevels", description = "The values to which log output level can be set.")
    String[] getAvailableLoggerLevels();
    
    
    //****** log4j runtime operations ****** //

    /**
     * Sets the level of an active Log4J logger
     * @param logger The name of the logger
     * @param level The level to set the logger to
     * @return True if successful, false if unsuccessful (eg if an invalid level is specified) 
     * @since Qpid JMX API 1.2
     */
    @MBeanOperation(name = "setRuntimeLoggerLevel", description = "Set the runtime logging level for an active log4j logger.",
                    impact = MBeanOperationInfo.ACTION)
    boolean setRuntimeLoggerLevel(@MBeanOperationParameter(name = "logger", description = "Logger name")String logger,
                                @MBeanOperationParameter(name = "level", description = "Logger level")String level);

    /**
     * Retrieves a TabularData set of the active log4j loggers and their levels
     * @return TabularData set of CompositeData rows with logger name and level, or null if there is a problem with the TabularData type 
     * @since Qpid JMX API 1.2
     */
    @MBeanOperation(name = "viewEffectiveRuntimeLoggerLevels", description = "View the effective runtime logging level " +
    		        "for active log4j logger's.", impact = MBeanOperationInfo.INFO)
    TabularData viewEffectiveRuntimeLoggerLevels();
    
    /**
     * Sets the level of the active Log4J RootLogger
     * @param level The level to set the RootLogger to
     * @return True if successful, false if unsuccessful (eg if an invalid level is specified) 
     * @since Qpid JMX API 1.2
     */
    @MBeanOperation(name = "setRuntimeRootLoggerLevel", description = "Set the runtime logging level for the active log4j Root Logger.",
                    impact = MBeanOperationInfo.ACTION)
    boolean setRuntimeRootLoggerLevel(@MBeanOperationParameter(name = "level", description = "Logger level")String level);

    /**
     * Attribute to represent the level of the active Log4J RootLogger
     * @return The level of the RootLogger.
     * @since Qpid JMX API 1.2
     */
    @MBeanAttribute(name = "getRuntimeRootLoggerLevel", description = "Get the runtime logging level for the active log4j Root Logger.")
    String getRuntimeRootLoggerLevel();
    
    
    //****** log4j XML configuration file operations ****** //
    
    /**
     * Reloads the log4j configuration file, applying any changes made. 
     * 
     * @throws IOException
     * @since Qpid JMX API 1.4
     */
    @MBeanOperation(name = "reloadConfigFile", description = "Reload the log4j xml configuration file", impact = MBeanOperationInfo.ACTION)
    void reloadConfigFile() throws IOException;

    /**
     * Updates the level of an existing Log4J logger within the xml configuration file
     * @param logger The name of the logger
     * @param level The level to set the logger to
     * @return True if successful, false if unsuccessful (eg if an invalid logger or level is specified) 
     * @throws IOException if there is an error parsing the configuration file.
     * @since Qpid JMX API 1.2
     */
    @MBeanOperation(name = "setConfigFileLoggerLevel", description = "Set the logging level for an existing logger " +
    		         "in the log4j xml configuration file", impact = MBeanOperationInfo.ACTION)
    boolean setConfigFileLoggerLevel(@MBeanOperationParameter(name = "logger", description = "logger name")String logger,
                                    @MBeanOperationParameter(name = "level", description = "Logger level")String level) throws IOException;
    
    /**
     * Retrieves a TabularData set of the existing Log4J loggers within the xml configuration file
     * @return TabularData set of CompositeData rows with logger name and level, or null if there is a problem with the TabularData type 
     * @throws IOException if there is an error parsing the configuration file.
     * @since Qpid JMX API 1.2
     */
    @MBeanOperation(name = "viewConfigFileLoggerLevels", description = "Get the logging level defined for the logger's " +
    		        "in the log4j xml configuration file.", impact = MBeanOperationInfo.INFO)
    TabularData viewConfigFileLoggerLevels() throws IOException;
 
    /**
     * Updates the level of the Log4J RootLogger within the xml configuration file if it is present
     * @param level The level to set the logger to
     * @return True if successful, false if not (eg an invalid level is specified, or root logger level isnt already defined) 
     * @throws IOException if there is an error parsing the configuration file.
     * @since Qpid JMX API 1.2
     */
    @MBeanOperation(name = "setConfigFileRootLoggerLevel", description = "Set the logging level for the Root Logger " +
    		        "in the log4j xml configuration file.", impact = MBeanOperationInfo.ACTION)
    boolean setConfigFileRootLoggerLevel(@MBeanOperationParameter(name = "level", description = "Logger level")String level) throws IOException;

    /**
     * Attribute to represent the level of the Log4J RootLogger within the xml configuration file
     * @return The level of the RootLogger, or null if it is not present
     * @since Qpid JMX API 1.2
     */
    @MBeanAttribute(name = "getConfigFileRootLoggerLevel", description = "Get the logging level for the Root Logger " +
                    "in the log4j xml configuration file.")
    String getConfigFileRootLoggerLevel() throws IOException;
}
