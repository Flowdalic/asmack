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

package org.ietf.jgss;

import java.io.InputStream;
import java.io.OutputStream;

public interface GSSContext {

    static final int DEFAULT_LIFETIME = 0;

    static final int INDEFINITE_LIFETIME = Integer.MAX_VALUE;//2147483647;

    byte[] initSecContext(byte[] inputBuf, int offset, int len) throws GSSException;

    int initSecContext(InputStream inStream, OutputStream outStream) throws GSSException;

    byte[] acceptSecContext(byte[] inToken, int offset, int len) throws GSSException;

    void acceptSecContext(InputStream inStream, OutputStream outStream) throws GSSException;

    boolean isEstablished();

    void dispose() throws GSSException;

    int getWrapSizeLimit(int qop, boolean confReq, int maxTokenSize) throws GSSException;

    byte[] wrap(byte[] inBuf, int offset, int len, MessageProp msgProp) throws GSSException;

    void wrap(InputStream inStream, OutputStream outStream, MessageProp msgProp)
            throws GSSException;

    byte[] unwrap(byte[] inBuf, int offset, int len, MessageProp msgProp) throws GSSException;

    void unwrap(InputStream inStream, OutputStream outStream, MessageProp msgProp)
            throws GSSException;

    byte[] getMIC(byte[] inMsg, int offset, int len, MessageProp msgProp) throws GSSException;

    void getMIC(InputStream inStream, OutputStream outStream, MessageProp msgProp)
            throws GSSException;

    void verifyMIC(byte[] inToken, int tokOffset, int tokLen, byte[] inMsg, int msgOffset,
            int msgLen, MessageProp msgProp) throws GSSException;

    void verifyMIC(InputStream tokStream, InputStream msgStream, MessageProp msgProp)
            throws GSSException;

    byte[] export() throws GSSException;

    void requestMutualAuth(boolean state) throws GSSException;

    void requestReplayDet(boolean state) throws GSSException;

    void requestSequenceDet(boolean state) throws GSSException;

    void requestCredDeleg(boolean state) throws GSSException;

    void requestAnonymity(boolean state) throws GSSException;

    void requestConf(boolean state) throws GSSException;

    void requestInteg(boolean state) throws GSSException;

    void requestLifetime(int lifetime) throws GSSException;

    void setChannelBinding(ChannelBinding cb) throws GSSException;

    boolean getCredDelegState();

    boolean getMutualAuthState();

    boolean getReplayDetState();

    boolean getSequenceDetState();

    boolean getAnonymityState();

    boolean isTransferable() throws GSSException;

    boolean isProtReady();

    boolean getConfState();

    boolean getIntegState();

    int getLifetime();

    GSSName getSrcName() throws GSSException;

    GSSName getTargName() throws GSSException;

    Oid getMech() throws GSSException;

    GSSCredential getDelegCred() throws GSSException;

    boolean isInitiator() throws GSSException;
}
