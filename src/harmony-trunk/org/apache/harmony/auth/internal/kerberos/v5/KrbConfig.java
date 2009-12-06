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

package org.apache.harmony.auth.internal.kerberos.v5;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.util.Hashtable;

/**
 * Kerberos configuration.
 * 
 * Parses Kerberos configuration file according to the following syntax:<br>
 * CONFIG = (SECTION)*<br>
 * SECTION = '[' name ']' (EXPR)*<br>
 * EXPR = S | C<br>
 * S = tag '=' value<br>
 * C = tag '=' '{' (EXPR)* '}'<br>
 * 
 * @see http://web.mit.edu/kerberos/www/krb5-1.5/krb5-1.5.1/doc/krb5-admin/krb5.conf.html#krb5.conf
 */
public class KrbConfig {

    private static final int TT_START_SECTION = '[';

    private static final int TT_END_SECTION = ']';

    private static final int TT_EQAUL = '=';

    private Hashtable<String, Hashtable<String, String>> values = new Hashtable<String, Hashtable<String, String>>();

    /**
     * Creates configuration.
     * 
     * @param f -
     *            configuration file
     * @throws IOException -
     *             in case of I/O errors
     */
    public KrbConfig(File f) throws IOException {
        StreamTokenizer t = new StreamTokenizer(new InputStreamReader(
                new FileInputStream(f)));

        t.commentChar('#');

        t.ordinaryChar(TT_START_SECTION);
        t.ordinaryChar(TT_END_SECTION);
        t.ordinaryChar(TT_EQAUL);

        t.wordChars('_', '_');
        t.wordChars(':', ':');
        t.wordChars('/', '/');

        t.nextToken(); // init stream tokenizer

        String currentSection = nextSection(t);
        while (currentSection != null) {

            // allow duplicate section
            Hashtable<String, String> sectionValues = values
                    .get(currentSection);
            if (sectionValues == null) {
                sectionValues = new Hashtable<String, String>();
                values.put(currentSection, sectionValues);
            }

            // parse section content
            parseSection(t, sectionValues);

            currentSection = nextSection(t);
        }
    }

    private String nextSection(StreamTokenizer t) throws IOException {
        while (true) {
            if (t.ttype == StreamTokenizer.TT_EOF) {
                return null;
            }
            if (t.ttype != TT_START_SECTION) {
                t.nextToken();
                continue;
            }

            t.nextToken();
            if (t.ttype != StreamTokenizer.TT_WORD) {
                t.nextToken();
                continue;
            }
            String section = t.sval;

            t.nextToken();
            if (t.ttype != TT_END_SECTION) {
                t.nextToken();
                continue;
            }
            t.nextToken();
            return section;
        }
    }

    // TODO: implement parsing values in curly braces and decide 
    // how to represent them
    private void parseSection(StreamTokenizer t,
            Hashtable<String, String> sectionValues) throws IOException {

        while (true) {
            // end of file or new section
            if (t.ttype == StreamTokenizer.TT_EOF
                    || t.ttype == TT_START_SECTION) {
                return;
            }

            // tag
            if (t.ttype != StreamTokenizer.TT_WORD) {
                t.nextToken();
                continue;
            }
            String tag = t.sval;
            t.nextToken();

            // equals char
            if (t.ttype != TT_EQAUL) {
                t.nextToken();
                continue;
            }
            t.nextToken();

            // value
            if (t.ttype != StreamTokenizer.TT_WORD) {
                t.nextToken();
                continue;
            }
            String value = t.sval;
            t.nextToken();

            sectionValues.put(tag, value);
        }
    }

    public String getValue(String section, String tag) {
        Hashtable<String, String> h = values.get(section);
        if (h != null) {
            return h.get(tag);
        }
        return null;
    }

    /**
     * Search Kerberos configuration
     * 
     * @return - configuration file or null if there is no one
     * @throws IOException -
     *             in case of I/O errors
     */
    public static KrbConfig getSystemConfig() throws IOException {

        String fName = System.getProperty("java.security.krb5.conf"); //$NON-NLS-1$
        if (fName == null) {

            fName = System.getProperty("java.home") + "/lib/security/krb5.conf"; //$NON-NLS-1$ //$NON-NLS-2$
            File f = new File(fName);
            if (f.exists()) {
                return new KrbConfig(f);
            }

            String OSName = System.getProperty("os.name"); //$NON-NLS-1$
            if (OSName.indexOf("Windows") != -1) { //$NON-NLS-1$
                fName = "c:\\winnt\\krb5.ini"; //$NON-NLS-1$
            } else if (OSName.indexOf("Linux") != -1) { //$NON-NLS-1$
                fName = "/etc/krb5.conf"; //$NON-NLS-1$
            } else {
                throw new UnsupportedOperationException(OSName);
            }
        }

        File f = new File(fName);
        if (f.exists() && f.isFile()) {
            return new KrbConfig(f);
        }
        return null;
    }
}
