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

package org.apache.qpid.management.common;

import java.io.IOException;
import java.security.Security;
import java.util.HashMap;
import java.util.Map;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.net.ssl.SSLException;
import javax.security.auth.callback.CallbackHandler;
import javax.security.sasl.SaslClientFactory;

import org.apache.qpid.management.common.sasl.CRAMMD5HashedSaslClientFactory;
import org.apache.qpid.management.common.sasl.Constants;
import org.apache.qpid.management.common.sasl.JCAProvider;
import org.apache.qpid.management.common.sasl.SaslProvider;
import org.apache.qpid.management.common.sasl.UserPasswordCallbackHandler;
import org.apache.qpid.management.common.sasl.UsernameHashedPasswordCallbackHandler;

public class JMXConnnectionFactory {
	
	private static final String NON_JRMP_SERVER = "non-JRMP server at remote endpoint";
	private static final String SERVER_SUPPORTED_PROFILES = "The server supported profiles";
	private static final String CLIENT_REQUIRED_PROFILES = "do not match the client required profiles";
	
	public static JMXConnector getJMXConnection(long timeout, String host, int port, String username, String password) 
	                                                                        throws SSLException, IOException, Exception
	{
		//auto-negotiate an RMI or JMXMP (SASL/CRAM-MD5 or SASL/PLAIN) JMX connection to broker
        try
        {
            return createJMXconnector("RMI", timeout, host, port, username, password);
        }
        catch (IOException rmiIOE)
        {
            // check if the ioe was raised because we tried connecting to a non RMI-JRMP based JMX server
            boolean jrmpServer = !rmiIOE.getMessage().contains(NON_JRMP_SERVER);

            if (jrmpServer)
            {
                //it was an RMI-JRMP based JMX server, so something else went wrong. Check for SSL issues.
                Throwable rmiIOECause = rmiIOE.getCause();
                boolean isSSLException = false;
                if (rmiIOECause != null)
                {
                    isSSLException = rmiIOECause instanceof SSLException;
                }
                
                //if it was an SSLException based cause, throw it
                if (isSSLException)
                {
                    throw (SSLException) rmiIOECause;
                }
                else
                {
                    //can't determine cause, throw new IOE citing the original as cause
                    IOException nioe = new IOException();
                    nioe.initCause(rmiIOE);
                    throw nioe;
                }
            }
            else
            {
                try
                {
                    //It wasnt an RMI ConnectorServer at the broker end. Try to establish a JMXMP SASL/CRAM-MD5 connection instead.
                    return createJMXconnector("JMXMP_CRAM-MD5", timeout, host, port, username, password);
                }
                catch (IOException cramIOE)
                {
                    // check if the IOE was raised because we tried connecting to a SASL/PLAIN server using SASL/CRAM-MD5
                    boolean plainProfileServer = cramIOE.getMessage().contains(SERVER_SUPPORTED_PROFILES + 
                    		" [" + Constants.SASL_PLAIN + "] " + CLIENT_REQUIRED_PROFILES + " [" + Constants.SASL_CRAMMD5 + "]");

                    if (!plainProfileServer)
                    {
                        IOException nioe = new IOException();
                        nioe.initCause(cramIOE);
                        throw nioe;
                    }
                    else
                    {                    	
                        try
                        {
                            // Try to establish a JMXMP SASL/PLAIN connection instead.
                            return createJMXconnector("JMXMP_PLAIN", timeout, host, port, username, password);
                        }
                        catch (IOException plainIOE)
                        {
                            /* Out of options now. Check that the IOE was raised because we tried connecting to a server
                             * which didnt support SASL/PLAIN. If so, signal an unknown profile type. If not, raise the exception. */
                            boolean unknownProfile = plainIOE.getMessage().contains(CLIENT_REQUIRED_PROFILES + " [" + Constants.SASL_PLAIN + "]");

                            if (unknownProfile)
                            {
                                throw new Exception("Unknown JMXMP authentication mechanism, unable to connect.");
                            }
                            else
                            {
                                IOException nioe = new IOException();
                                nioe.initCause(plainIOE);
                                throw nioe;
                            }
                        }
                    }
                }
            }
        }
	}
	
	private static JMXConnector createJMXconnector(String connectionType, long timeout, String host, int port, 
	                                                String userName, String password) throws IOException, Exception
    {
	    Map<String, Object> env = new HashMap<String, Object>();
	    JMXServiceURL jmxUrl = null;
	    
        if (connectionType == "RMI")
        {
            jmxUrl = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + host + ":" + port + "/jmxrmi");

            //Add user credential's to environment map for RMIConnector startup. 
            //These will be used for authentication by the remote RMIConnectorServer if supported, or ignored otherwise.
            env.put(JMXConnector.CREDENTIALS, new String[] {userName,password});
        }
        else if (connectionType.contains("JMXMP"))
        {
            // Check that the JMXMPConnector is available to provide SASL support
            final String jmxmpcClass = "javax.management.remote.jmxmp.JMXMPConnector";

            try
            {
                Class.forName(jmxmpcClass);
            }
            catch (ClassNotFoundException cnfe)
            {
                throw new Exception("JMXMPConnector class not found, unable to connect to specified server.\n\n"
                                + "Please add the jmxremote_optional.jar to the jmxremote.sasl plugin folder, or the classpath.");
            }

            jmxUrl = new JMXServiceURL("jmxmp", host, port);
            env = new HashMap<String, Object>();

            /* set the package in which to find the JMXMP ClientProvider.class file loaded by the 
             * jmxremote.sasl plugin (from the jmxremote_optional.jar) */
            env.put("jmx.remote.protocol.provider.pkgs", "com.sun.jmx.remote.protocol");

            if (connectionType == "JMXMP_CRAM-MD5")
            {
                Map<String, Class<? extends SaslClientFactory>> map = new HashMap<String, Class<? extends SaslClientFactory>>();
                map.put("CRAM-MD5-HASHED", CRAMMD5HashedSaslClientFactory.class);
                Security.addProvider(new JCAProvider(map));

                CallbackHandler handler = new UsernameHashedPasswordCallbackHandler(
                                                userName, password);
                env.put("jmx.remote.profiles", Constants.SASL_CRAMMD5);
                env.put("jmx.remote.sasl.callback.handler", handler);
            }
            else if (connectionType == "JMXMP_PLAIN")
            {
                Security.addProvider(new SaslProvider());
                CallbackHandler handler = new UserPasswordCallbackHandler(userName, password);
                env.put("jmx.remote.profiles", Constants.SASL_PLAIN);
                env.put("jmx.remote.sasl.callback.handler", handler);
            }
            else
            {
                throw new Exception("Unknown JMXMP authentication mechanism");
            }
        }
        else
        {
            throw new Exception("Unknown connection type");
        }

        ConnectWaiter connector = new ConnectWaiter(jmxUrl, env);
        Thread connectorThread = new Thread(connector);
        connectorThread.start();
        connectorThread.join(timeout);
        
        if(connector.getJmxc() == null)
        {
            if (connector.getConnectionException() != null)
            {
                throw connector.getConnectionException();
            }
            else
            {
                throw new IOException("Timed out connecting to " + host + ":" + port);
            }
        }
        
        return connector.getJmxc();
    }
	
    private static class ConnectWaiter implements Runnable
    {
        private Exception _connectionException;
        private JMXConnector _jmxc;
        private JMXServiceURL _jmxUrl;
        private Map<String, ?> _env;
        private boolean _connectionRetrieved;
        
        public ConnectWaiter(JMXServiceURL url, Map<String, ?> env)
        {
            _jmxUrl = url;
            _env = env;
        }

        public void run()
        {
            try
            {
                _jmxc = null;
                _connectionRetrieved = false;
                _connectionException = null;
                
                JMXConnector conn = JMXConnectorFactory.connect(_jmxUrl, _env);
                
                synchronized (this)
                {
                    if(_connectionRetrieved)
                    {
                        //The app thread already timed out the attempt and retrieved the 
                        //null connection, so just close this orphaned connection
                        try
                        {
                            conn.close();
                        }
                        catch (IOException e)
                        {
                            //ignore
                        }
                    }
                    else
                    {
                        _jmxc = conn;
                    }
                }
            }
            catch (Exception ex)
            {
                _connectionException = ex;
            }
        }

        public Exception getConnectionException()
        {
            return _connectionException;
        }

        public JMXConnector getJmxc()
        {
            synchronized (this)
            {
                _connectionRetrieved = true;
                
                return _jmxc;
            }
        }
    }
}
