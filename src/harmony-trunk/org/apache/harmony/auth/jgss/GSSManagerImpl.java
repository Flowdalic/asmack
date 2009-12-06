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

import java.security.Provider;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.harmony.auth.jgss.kerberos.KerberosSpiImpl;
import org.apache.harmony.auth.jgss.kerberos.KerberosProvider;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;

public class GSSManagerImpl extends GSSManager {

	private static Oid DEFAULT_MECH;
	
	private static Provider DEFAULT_PROVIDER = new KerberosProvider("kerberos provider", 0, "");
	
	private static GSSMechSpi DEFAULT_API = new KerberosSpiImpl();		

	
	static {
		try {
			DEFAULT_MECH = new Oid("1.2.840.113554.1.2.2");			
		} catch (GSSException e) {

		}		
	}
	
	public GSSManagerImpl() throws GSSException{		
		addProviderAtFront(DEFAULT_PROVIDER, null);
	}

	private Hashtable<Oid, GSSMechSpi> spis = new Hashtable<Oid, GSSMechSpi>();

	private static final String JGSSAPI = "GssApiMechanism.";

	private void enumApisFromProvider(Provider p, Oid mech, boolean override) {
		for (Entry entry : p.entrySet()) {
			String key = (String) entry.getKey();

			String value = (String) entry.getValue();

			if (!key.startsWith(JGSSAPI)) {
				continue;
			}

			String currentMechName = key.substring(JGSSAPI.length()).trim();
			Oid currentMech;
			try {
				currentMech = new Oid(currentMechName);
			} catch (GSSException e) {
				continue;
			}

			if (mech != null && !mech.equals(currentMech)) {
				continue;
			}

			if (!override && spis.get(currentMech) != null) {
				continue;
			}

			GSSMechSpi gssApi;
			try {
				gssApi = (GSSMechSpi) Class.forName(value).newInstance();
			} catch (Exception e) {
				continue;
			}
			spis.put(currentMech, gssApi);
		}
	}

	@Override
	public void addProviderAtEnd(Provider p, Oid mech) throws GSSException {
		enumApisFromProvider(p, mech, false);
	}

	@Override
	public void addProviderAtFront(Provider p, Oid mech) throws GSSException {
		enumApisFromProvider(p, mech, true);
	}

	@Override
	public GSSContext createContext(GSSName peer, Oid mech,
			GSSCredential myCred, int lifetime) throws GSSException {
		
		return null;
	}

	@Override
	public GSSContext createContext(GSSCredential myCred) throws GSSException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GSSContext createContext(byte[] interProcessToken)
			throws GSSException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GSSCredential createCredential(int usage) throws GSSException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GSSCredential createCredential(GSSName name, int lifetime, Oid mech,
			int usage) throws GSSException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GSSCredential createCredential(GSSName name, int lifetime,
			Oid[] mechs, int usage) throws GSSException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GSSName createName(String nameStr, Oid nameType) throws GSSException {
		if(nameType != null && nameType.equals(GSSName.NT_EXPORT_NAME)){
			return GSSNameImpl.importFromString(GSSUtils.getBytes(nameStr), this);
		}		
		return DEFAULT_API.createName(nameStr, nameType);	
	}

	@Override
	public GSSName createName(byte[] name, Oid nameType) throws GSSException {
		if (nameType != null && nameType.equals(GSSName.NT_EXPORT_NAME)) {
			return GSSNameImpl.importFromString(name, this);
		}
		return DEFAULT_API.createName(GSSUtils.toString(name), nameType);
	}

	@Override
	public GSSName createName(String nameStr, Oid nameType, Oid mech)
			throws GSSException {		
		return createName(nameStr, nameType).canonicalize(mech);
	}

	@Override
	public GSSName createName(byte[] name, Oid nameType, Oid mech)
			throws GSSException {
		return createName(GSSUtils.toString(name), nameType, mech);
	}

	@Override
	public Oid[] getMechs() {
		Set<Oid> oids = spis.keySet();
		Oid[] mechs = new Oid[oids.size()];
		int i = 0;
		for (Oid oid : oids) {
			mechs[i++] = oid;
		}
		return mechs;
	}

	@Override
	public Oid[] getMechsForName(Oid nameType) {
		ArrayList<Oid> mechs = new ArrayList<Oid>();
		Oid[] oids = getMechs();
		for (Oid oid : oids) {
			GSSMechSpi api = spis.get(oid);
			Oid[] mechNames = api.getNameMechs();
			boolean support = false;
			for (Oid mechName : mechNames) {
				if (mechName.equals(nameType)) {
					support = true;
					break;
				}
			}
			if (support) {
				mechs.add(oid);
			}
		}
		return mechs.toArray(new Oid[mechs.size()]);
	}

	@Override
	public Oid[] getNamesForMech(Oid mech) throws GSSException {
		GSSMechSpi api = getSpi(mech);
		return api.getNameMechs();
	}
	
	GSSMechSpi getSpi(Oid mech){
		return spis.get(mech);
	}
	
	Oid getDefaultMech(){
		return DEFAULT_MECH;
	}
	
	

	GSSCredentialElement createCredentialElement(GSSName name, int initLifetime, int acceptLifetime, Oid mech, int usage) {
		// TODO Auto-generated method stub
		return null;
	}
}
