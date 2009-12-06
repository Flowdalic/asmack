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

package javax.security.auth.x500;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.Principal;

import org.apache.harmony.auth.internal.nls.Messages;
import org.apache.harmony.security.x501.Name;

/**
 * Represents an X.500 principal, which holds the distinguished name of some
 * network entity. An example of a distinguished name is {@code "O=SomeOrg,
 * OU=SomeOrgUnit, C=US"}. The class can be instantiated from a byte representation
 * of an object identifier (OID), an ASN.1 DER-encoded version, or a simple
 * string holding the distinguished name. The representations must follow either
 * RFC 2253, RFC 1779, or RFC2459.
 */
public final class X500Principal implements Serializable, Principal {

    private static final long serialVersionUID = -500463348111345721L;

    /**
     * Defines a constant for the canonical string format of distinguished
     * names.
     */
    public static final String CANONICAL = "CANONICAL"; //$NON-NLS-1$

    /**
     * Defines a constant for the RFC 1779 string format of distinguished
     * names.
     */
    public static final String RFC1779 = "RFC1779"; //$NON-NLS-1$

    /**
     * Defines a constant for the RFC 2253 string format of distinguished
     * names.
     */
    public static final String RFC2253 = "RFC2253"; //$NON-NLS-1$

    //Distinguished Name
    private transient Name dn;

    /**
     * Creates a new X500Principal from a given ASN.1 DER encoding of a
     * distinguished name.
     *
     * @param name
     *            the ASN.1 DER-encoded distinguished name
     *
     * @throws IllegalArgumentException
     *             if the ASN.1 DER-encoded distinguished name is incorrect
     */
    public X500Principal(byte[] name) {
        super();
        if (name == null) {
            throw new IllegalArgumentException(Messages.getString("auth.00")); //$NON-NLS-1$
        }
        try {
            // FIXME dn = new Name(name);
            dn = (Name) Name.ASN1.decode(name);
        } catch (IOException e) {
            IllegalArgumentException iae = new IllegalArgumentException(Messages
                    .getString("auth.2B")); //$NON-NLS-1$
            iae.initCause(e);
            throw iae;
        }
    }

    /**
     * Creates a new X500Principal from a given ASN.1 DER encoding of a
     * distinguished name.
     *
     * @param in
     *            an {@code InputStream} holding the ASN.1 DER-encoded
     *            distinguished name
     *
     * @throws IllegalArgumentException
     *             if the ASN.1 DER-encoded distinguished name is incorrect
     */
    public X500Principal(InputStream in) {
        super();
        if (in == null) {
            throw new NullPointerException(Messages.getString("auth.2C")); //$NON-NLS-1$
        }
        try {
            // FIXME dn = new Name(is);
            dn = (Name) Name.ASN1.decode(in);
        } catch (IOException e) {
            IllegalArgumentException iae = new IllegalArgumentException(Messages
                    .getString("auth.2B")); //$NON-NLS-1$
            iae.initCause(e);
            throw iae;
        }
    }

    /**
     * Creates a new X500Principal from a string representation of a
     * distinguished name.
     *
     * @param name
     *            the string representation of the distinguished name
     *
     * @throws IllegalArgumentException
     *             if the string representation of the distinguished name is
     *             incorrect
     */
    public X500Principal(String name) {
        super();
        if (name == null) {
            throw new NullPointerException(Messages.getString("auth.00")); //$NON-NLS-1$
        }
        try {
            dn = new Name(name);
        } catch (IOException e) {
            IllegalArgumentException iae = new IllegalArgumentException(Messages
                    .getString("auth.2D")); //$NON-NLS-1$
            iae.initCause(e);
            throw iae;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        X500Principal principal = (X500Principal) o;
        return dn.getName(CANONICAL).equals(principal.dn.getName(CANONICAL));
    }

    /**
     * Returns an ASN.1 DER-encoded representation of the distinguished name
     * contained in this X.500 principal.
     *
     * @return the ASN.1 DER-encoded representation
     */
    public byte[] getEncoded() {
        byte[] src = dn.getEncoded();
        byte[] dst = new byte[src.length];
        System.arraycopy(src, 0, dst, 0, dst.length);
        return dst;
    }

    /**
     * Returns a human-readable string representation of the distinguished name
     * contained in this X.500 principal.
     *
     * @return the string representation
     */
    public String getName() {
        return dn.getName(RFC2253);
    }

    /**
     * Returns a string representation of the distinguished name contained in
     * this X.500 principal. The format of the representation can be chosen.
     * Valid arguments are {@link #RFC1779}, {@link #RFC2253}, and
     * {@link #CANONICAL}. The representations are specified in RFC 1779 and RFC
     * 2253, respectively. The canonical form is based on RFC 2253, but adds
     * some canonicalizing operations like removing leading and trailing
     * whitespace, lower-casing the whole name, and bringing it into a
     * normalized Unicode representation.
     *
     * @param format
     *            the name of the format to use for the representation
     *
     * @return the string representation
     *
     * @throws IllegalArgumentException
     *             if the {@code format} argument is not one of the three
     *             mentioned above
     */
    public String getName(String format) {
        return dn.getName(format);
    }

    @Override
    public int hashCode() {
        return dn.getName(CANONICAL).hashCode();
    }

    @Override
    public String toString() {
        return dn.getName(RFC1779);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(dn.getEncoded());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {

        dn = (Name) Name.ASN1.decode((byte[]) in.readObject());
    }
}
