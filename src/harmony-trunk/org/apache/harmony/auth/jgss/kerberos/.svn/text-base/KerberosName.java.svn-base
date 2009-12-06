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

package org.apache.harmony.auth.jgss.kerberos;

import org.apache.harmony.auth.jgss.GSSNameImpl;
import org.apache.harmony.auth.jgss.GSSUtils;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;

/**
 * Current the org.apache.harmony.auth.jgss.GSSNameImpl just supports kerberos
 * related GSSName.
 */
public class KerberosName extends GSSNameImpl {

	private String name;

	private Oid nameType;
	
	public KerberosName(String name, Oid nameType) throws GSSException {
		if (null == name) {
			throw new GSSException(KerberosUtils.DEFAULT_GSSEXCEPTION_MAJOR_CODE,
					KerberosUtils.DEFAULT_GSSEXCEPTION_MINOR_CODE,
					"Cannot import null GSSName");
		}

		if (null == nameType) {
			nameType = KerberosUtils.KRB5_PRINCIPAL_NAMETYPE;
		}
		
		if(nameType.equals(GSSName.NT_HOSTBASED_SERVICE)){
			name = name.replaceAll("@", "/");
		}
		
		if (!(nameType.equals(GSSName.NT_HOSTBASED_SERVICE)
				|| nameType.equals(GSSName.NT_USER_NAME) || nameType
				.equals(KerberosUtils.KRB5_PRINCIPAL_NAMETYPE))) {
			throw new GSSException(
					KerberosUtils.DEFAULT_GSSEXCEPTION_MAJOR_CODE,
					KerberosUtils.DEFAULT_GSSEXCEPTION_MINOR_CODE,
					"Unsupported OID");
		}
		this.name = name;
		this.nameType = nameType;
	}

	String getName() {
		return name;
	}

	public GSSName canonicalize(Oid mech) throws GSSException {
		return new KerberosName(getName(), mech);
	}

	public boolean equals(GSSName another) throws GSSException {		
		if (isAnonymous() && another.isAnonymous()) {
			return true;
		}

		if (!(another instanceof KerberosName)) {		
			
			return false;
		}

		KerberosName anotherNameImpl = (KerberosName) another;
		String thisName = getName();
		String anotherName = anotherNameImpl.getName();

		if (!thisName.equals(anotherName)) {
			return false;
		}

		Oid thisOid = getStringNameType();
		Oid anotherOid = anotherNameImpl.getStringNameType();

		if (thisOid.equals(KerberosUtils.KRB5_PRINCIPAL_NAMETYPE)
				|| anotherOid.equals(KerberosUtils.KRB5_PRINCIPAL_NAMETYPE)) {
			return true;
		}
		return thisOid.equals(anotherOid);
	}

		
	
	public Oid getStringNameType() throws GSSException {
		return nameType;
	}

	// org.apache.harmony.auth.jgss.GSSNameImpl actually does not support
	// GSSNAME.NT_ANONYMOUS, so it always returns false.
	public boolean isAnonymous() {
		return nameType.equals(GSSName.NT_ANONYMOUS);
	}

	// org.apache.harmony.auth.jgss.GSSNameImpl only supports MN GSSNAME.
	public boolean isMN() {
		return true;
	}
	
	public String toString(){
		return name;
	}

	@Override
	protected byte[] exportMechDependent() throws GSSException {		
		return GSSUtils.getBytes(name);
	}

	@Override
	protected Oid getMech() {
		return KerberosUtils.KRB5_MECH;
	}
}
