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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;

public class GSSCredentialImpl implements GSSCredential {

	private GSSCredentialElement defaultCredentialElement;

	private HashMap<GSSCredentialType, GSSCredentialElement> credentials = new HashMap<GSSCredentialType, GSSCredentialElement>();

	private boolean disposed;

	private final GSSManagerImpl managerImpl;

	public GSSCredentialImpl(GSSManagerImpl managerImpl) {
		this.managerImpl = managerImpl;
	}

	public void add(GSSName name, int initLifetime, int acceptLifetime,
			Oid mech, int usage) throws GSSException {
		checkDisposed();

		if (mech == null) {
			mech = managerImpl.getDefaultMech();
		}

		GSSCredentialType credentialType = new GSSCredentialType(mech, usage);
		if (credentials.containsKey(credentialType)) {
			throw new GSSException(GSSException.DUPLICATE_ELEMENT,
					GSSUtils.DEFAULT_GSSEXCEPTION_MINOR_CODE, mech + " "
							+ usage);
		}

		GSSCredentialElement credentialElement = managerImpl
				.createCredentialElement(name, initLifetime, acceptLifetime,
						mech, usage);
		defaultCredentialElement = credentialElement;
		credentials.put(credentialType, credentialElement);
	}

	public void dispose() throws GSSException {
		if (disposed) {
			return;
		}

		for (GSSCredentialElement credential : credentials.values()) {
			credential.dispose();
		}
		disposed = true;
	}

	public Oid[] getMechs() throws GSSException {
		checkDisposed();
		ArrayList<Oid> mechs = new ArrayList<Oid>();
		for (GSSCredentialType credentialType : credentials.keySet()) {
			Oid mech = credentialType.mech;
			if (!mechs.contains(mech)) {
				mechs.add(mech);
			}
		}
		return mechs.toArray(new Oid[mechs.size()]);
	}

	public GSSName getName() throws GSSException {
		checkDisposed();
		return defaultCredentialElement.getName();
	}

	public GSSName getName(Oid mech) throws GSSException {
		checkDisposed();
		GSSCredentialElement credential = null;
		for (Entry<GSSCredentialType, GSSCredentialElement> entry : credentials
				.entrySet()) {
			if (entry.getKey().mech.equals(mech)) {
				credential = entry.getValue();
				break;
			}
		}
		if (null == credential) {
			throw new GSSException(GSSException.BAD_MECH,
					GSSUtils.DEFAULT_GSSEXCEPTION_MINOR_CODE,
					"fail to get name for " + mech);
		}
		return credential.getName();
	}

	public int getRemainingAcceptLifetime(Oid mech) throws GSSException {
		checkDisposed();
		GSSCredentialElement credential = null;
		int remainingAcceptLifetime = Integer.MIN_VALUE;
		credential = credentials.get(new GSSCredentialType(mech,
				GSSCredential.INITIATE_ONLY));
		if (credential != null) {
			remainingAcceptLifetime = credential.getRemainingAcceptLifetime();
		}
		GSSCredentialElement tempCredential = credentials
				.get(new GSSCredentialType(mech,
						GSSCredential.INITIATE_AND_ACCEPT));
		if (tempCredential != null) {
			credential = tempCredential;
			remainingAcceptLifetime = Math.max(remainingAcceptLifetime,
					credential.getRemainingAcceptLifetime());
		}

		if (credential == null) {
			throw new GSSException(GSSException.BAD_MECH,
					GSSUtils.DEFAULT_GSSEXCEPTION_MINOR_CODE,
					"no credential for mech " + mech);
		}
		return remainingAcceptLifetime;
	}

	public int getRemainingInitLifetime(Oid mech) throws GSSException {
		checkDisposed();
		GSSCredentialElement credential = null;
		int remainingInitLifetime = Integer.MIN_VALUE;
		credential = credentials.get(new GSSCredentialType(mech,
				GSSCredential.INITIATE_ONLY));
		if (credential != null) {
			remainingInitLifetime = credential.getRemainingInitLifetime();
		}
		GSSCredentialElement tempCredential = credentials
				.get(new GSSCredentialType(mech,
						GSSCredential.INITIATE_AND_ACCEPT));
		if (tempCredential != null) {
			credential = tempCredential;
			remainingInitLifetime = Math.max(remainingInitLifetime, credential
					.getRemainingInitLifetime());
		}

		if (credential == null) {
			throw new GSSException(GSSException.BAD_MECH,
					GSSUtils.DEFAULT_GSSEXCEPTION_MINOR_CODE,
					"no credential for mech " + mech);
		}
		return remainingInitLifetime;
	}

	public int getRemainingLifetime() throws GSSException {
		checkDisposed();
		int remainingLifeTime = GSSCredential.INDEFINITE_LIFETIME;
		for (Entry<GSSCredentialType, GSSCredentialElement> credential : credentials
				.entrySet()) {
			GSSCredentialType credentialType = credential.getKey();
			GSSCredentialElement credentialElement = credential.getValue();
			int credentialRemainingLifeTime;
			switch (credentialType.usage) {
			case GSSCredential.INITIATE_ONLY:
				credentialRemainingLifeTime = credentialElement
						.getRemainingInitLifetime();
				break;
			case GSSCredential.ACCEPT_ONLY:
				credentialRemainingLifeTime = credentialElement
						.getRemainingAcceptLifetime();
				break;
			default: // INITIATE_AND_ACCEPT
				credentialRemainingLifeTime = Math.min(credentialElement
						.getRemainingInitLifetime(), credentialElement
						.getRemainingAcceptLifetime());
				break;
			}
			remainingLifeTime = Math.min(remainingLifeTime,
					credentialRemainingLifeTime);

		}
		return remainingLifeTime;
	}

	public int getUsage() throws GSSException {
		checkDisposed();
		boolean isInitiate = false;
		boolean isAccept = false;
		for (GSSCredentialType credentialType : credentials.keySet()) {
			switch (credentialType.usage) {
			case GSSCredential.INITIATE_ONLY:
				isInitiate = true;
				break;
			case GSSCredential.ACCEPT_ONLY:
				isAccept = true;
				break;
			case GSSCredential.INITIATE_AND_ACCEPT:
				isInitiate = isAccept = true;
			}
		}

		if (isInitiate) {
			if (isAccept) {
				return GSSCredential.INITIATE_AND_ACCEPT;
			}
			return GSSCredential.INITIATE_ONLY;
		}
		if (isAccept) {
			return GSSCredential.ACCEPT_ONLY;
		}
		throw new GSSException(GSSException.FAILURE,
				GSSUtils.DEFAULT_GSSEXCEPTION_MINOR_CODE,
				"no credential element in this credential");
	}

	public int getUsage(Oid mech) throws GSSException {
		checkDisposed();
		boolean isInitiate = false;
		boolean isAccept = false;
		for (GSSCredentialType credentialType : credentials.keySet()) {
			if (credentialType.mech.equals(mech)) {
				switch (credentialType.usage) {
				case GSSCredential.INITIATE_ONLY:
					isInitiate = true;
					break;
				case GSSCredential.ACCEPT_ONLY:
					isAccept = true;
					break;
				case GSSCredential.INITIATE_AND_ACCEPT:
					isInitiate = isAccept = true;
				}
			}
		}

		if (isInitiate) {
			if (isAccept) {
				return GSSCredential.INITIATE_AND_ACCEPT;
			}
			return GSSCredential.INITIATE_ONLY;
		}
		if (isAccept) {
			return GSSCredential.ACCEPT_ONLY;
		}
		throw new GSSException(GSSException.BAD_MECH,
				GSSUtils.DEFAULT_GSSEXCEPTION_MINOR_CODE,
				"no credential for mech " + mech);
	}

	private void checkDisposed() throws GSSException {
		if (disposed) {
			throw new GSSException(GSSUtils.DEFAULT_GSSEXCEPTION_MAJOR_CODE,
					GSSUtils.DEFAULT_GSSEXCEPTION_MINOR_CODE,
					"credential disposed");
		}
	}

	private static class GSSCredentialType {
		public final Oid mech;

		public final int usage;

		public GSSCredentialType(Oid mech, int usage) {
			this.mech = mech;
			this.usage = usage;
		}

		@Override
		public boolean equals(Object other) {
			if (!(other instanceof GSSCredentialType)) {
				return false;
			}
			GSSCredentialType otherType = (GSSCredentialType) other;
			return mech.equals(otherType.mech) && usage == otherType.usage;
		}

		@Override
		public int hashCode() {
			return mech.hashCode() + usage;
		}
	}
}
