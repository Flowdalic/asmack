/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package org.apache.qpid.management.common.mbeans;

import java.io.IOException;
import java.util.List;

import javax.management.JMException;
import javax.management.MBeanOperationInfo;

import org.apache.qpid.management.common.mbeans.annotations.MBeanAttribute;
import org.apache.qpid.management.common.mbeans.annotations.MBeanOperation;
import org.apache.qpid.management.common.mbeans.annotations.MBeanOperationParameter;

/**
 * The ManagedBroker is the management interface to expose management
 * features of the Broker.
 *
 * @author   Bhupendra Bhardwaj
 * @version  0.1
 */
public interface ManagedBroker
{
    static final String TYPE = "VirtualHostManager";

    static final int VERSION = 1 ;

    /**
     * Returns an array of the exchange types available for creation.
     * @since Qpid JMX API 1.3
     * @throws IOException 
     */
    @MBeanAttribute(name="ExchangeTypes", description = "The types of Exchange available for creation.")
    String[] getExchangeTypes() throws IOException;
    
    /**
     * Returns a list containing the names of the attributes available for the Queue mbeans.
     * @since Qpid JMX API 1.3
     * @throws IOException
     */
    @MBeanOperation(name = "retrieveQueueAttributeNames", description = "Retrieve the attribute names for queues in this virtualhost", 
            impact = MBeanOperationInfo.INFO)
    List<String> retrieveQueueAttributeNames() throws IOException;
    
    /**
     * Returns a List of Object Lists containing the requested attribute values (in the same sequence requested) for each queue in the virtualhost.
     * If a particular attribute cant be found or raises an mbean/reflection exception whilst being gathered its value is substituted with the String "-".
     * @since Qpid JMX API 1.3
     * @throws IOException
     */
    @MBeanOperation(name = "retrieveQueueAttributeValues", description = "Retrieve the indicated attributes for queues in this virtualhost", 
            impact = MBeanOperationInfo.INFO)
    List<List<Object>> retrieveQueueAttributeValues(@MBeanOperationParameter(name="attributes", description="Attributes to retrieve") String[] attributes) throws IOException;
    
    /**
     * Creates a new Exchange.
     * @param name
     * @param type
     * @param durable
     * @throws IOException
     * @throws JMException
     */
    @MBeanOperation(name="createNewExchange", description="Creates a new Exchange", impact= MBeanOperationInfo.ACTION)
    void createNewExchange(@MBeanOperationParameter(name="name", description="Name of the new exchange")String name,
                           @MBeanOperationParameter(name="ExchangeType", description="Type of the exchange")String type,
                           @MBeanOperationParameter(name="durable", description="true if the Exchang should be durable")boolean durable)
        throws IOException, JMException;

    /**
     * unregisters all the channels, queuebindings etc and unregisters
     * this exchange from managed objects.
     * @param exchange
     * @throws IOException
     * @throws JMException
     */
    @MBeanOperation(name="unregisterExchange",
                    description="Unregisters all the related channels and queuebindings of this exchange",
                    impact= MBeanOperationInfo.ACTION)
    void unregisterExchange(@MBeanOperationParameter(name= ManagedExchange.TYPE, description="Exchange Name")String exchange)
        throws IOException, JMException;

    /**
     * Create a new Queue on the Broker server
     * @param queueName
     * @param durable
     * @param owner
     * @throws IOException
     * @throws JMException
     */
    @MBeanOperation(name="createNewQueue", description="Create a new Queue on the Broker server", impact= MBeanOperationInfo.ACTION)
    void createNewQueue(@MBeanOperationParameter(name="queue name", description="Name of the new queue")String queueName,
                        @MBeanOperationParameter(name="owner", description="Owner name")String owner,
                        @MBeanOperationParameter(name="durable", description="true if the queue should be durable")boolean durable)
        throws IOException, JMException;

    /**
     * Unregisters the Queue bindings, removes the subscriptions and unregisters
     * from the managed objects.
     * @param queueName
     * @throws IOException
     * @throws JMException
     */
    @MBeanOperation(name="deleteQueue",
                         description="Unregisters the Queue bindings, removes the subscriptions and deletes the queue",
                         impact= MBeanOperationInfo.ACTION)
    void deleteQueue(@MBeanOperationParameter(name= ManagedQueue.TYPE, description="Queue Name")String queueName)
        throws IOException, JMException;
}
