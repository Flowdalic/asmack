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

package org.apache.harmony.auth.jgss;

import org.apache.harmony.auth.jgss.GSSUtils;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;

public abstract class GSSNameImpl implements GSSName {
private static final byte EXPORTED_TOKEN_FIRST_BYTE = 0x04;
	
	private static final byte EXPORTED_TOKEN_SECOND_BYTE = 0x01;
	
	private static final int EXPORTED_TOKEN_LENGTH = 2;
	
	private static final int OID_LENGTH_ENCODED_LENGTH = 2;
	
	private static final int NAME_LENGTH_ENCODED_LENGTH = 4;
	
	private static final int FIX_CONTENT_LENGTH = EXPORTED_TOKEN_LENGTH + OID_LENGTH_ENCODED_LENGTH + NAME_LENGTH_ENCODED_LENGTH;

		
	
	static GSSName importFromString(byte[] encodedGSSName,
			GSSManagerImpl gssManagerImpl) throws GSSException {
		byte[] encoded = encodedGSSName;
		int index = 0;

		if (encoded[index++] != EXPORTED_TOKEN_FIRST_BYTE
				|| encoded[index++] != EXPORTED_TOKEN_SECOND_BYTE) {
			throw new GSSException(GSSUtils.DEFAULT_GSSEXCEPTION_MAJOR_CODE,
					GSSUtils.DEFAULT_GSSEXCEPTION_MINOR_CODE,
					"Illegal token in importing string to GSSName");
		}

		int oidLength = GSSUtils.toInt(encoded, index,
				OID_LENGTH_ENCODED_LENGTH);
		index += OID_LENGTH_ENCODED_LENGTH;

		byte[] encodedMech = new byte[oidLength];
		System.arraycopy(encoded, index, encodedMech, 0, oidLength);
		index += oidLength;
		Oid mech = new Oid(encodedMech);
		GSSMechSpi gssApi = gssManagerImpl.getSpi(mech);

		int nameLength = GSSUtils.toInt(encoded, index,
				NAME_LENGTH_ENCODED_LENGTH);
		index += NAME_LENGTH_ENCODED_LENGTH;

		byte[] encodedName = new byte[nameLength];
		System.arraycopy(encoded, index, encodedName, 0, nameLength);
		String name = GSSUtils.toString(encodedName);
		return gssApi.createName(name);
	}
	
	public boolean equals(Object o){
		if( o instanceof GSSName){
			try {
				return equals((GSSName) o);
			} catch (GSSException e) {				
			}
		}
		return false;
	}
	
	public byte[] export() throws GSSException {
		byte[] name = exportMechDependent();
		byte[] oid = getMech().getDER();
		
		byte[] encoded = new byte[FIX_CONTENT_LENGTH + oid.length + name.length]; 
		int index = 0;
		encoded[index++] = EXPORTED_TOKEN_FIRST_BYTE;
		encoded[index++] = EXPORTED_TOKEN_SECOND_BYTE;		
		
		byte[] oid_length = GSSUtils.getBytes(oid.length, OID_LENGTH_ENCODED_LENGTH);
		System.arraycopy(oid_length, 0, encoded, index, OID_LENGTH_ENCODED_LENGTH);
		index += OID_LENGTH_ENCODED_LENGTH;
		System.arraycopy(oid, 0, encoded, index, oid.length);
		index += oid.length;
		
		
		byte[] name_length = GSSUtils.getBytes(name.length, NAME_LENGTH_ENCODED_LENGTH);
		System.arraycopy(name_length, 0, encoded, index, NAME_LENGTH_ENCODED_LENGTH);
		index += NAME_LENGTH_ENCODED_LENGTH;
		System.arraycopy(name, 0, encoded, index, name.length);
		return encoded;		
	}
	
	protected abstract byte[] exportMechDependent() throws GSSException;
	protected abstract Oid getMech();
}
