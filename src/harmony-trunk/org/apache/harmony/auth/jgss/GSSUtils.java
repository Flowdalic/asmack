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

import java.io.UnsupportedEncodingException;

import org.ietf.jgss.GSSException;

public class GSSUtils {

	public static final String DEFAULT_CHARSET_NAME = "UTF-8";

	public static final int DEFAULT_GSSEXCEPTION_MAJOR_CODE = 3;

	public static final int DEFAULT_GSSEXCEPTION_MINOR_CODE = 0;
	
	public static String toString(byte[] bytes) throws GSSException {
		try {
			return new String(bytes, DEFAULT_CHARSET_NAME);
		} catch (UnsupportedEncodingException e) {
			throw new GSSException(DEFAULT_GSSEXCEPTION_MAJOR_CODE,
					DEFAULT_GSSEXCEPTION_MINOR_CODE, e.getMessage());
		}
	}

	public static byte[] getBytes(String s) throws GSSException {
		try {
			return s.getBytes(DEFAULT_CHARSET_NAME);
		} catch (UnsupportedEncodingException e) {
			throw new GSSException(DEFAULT_GSSEXCEPTION_MAJOR_CODE,
					DEFAULT_GSSEXCEPTION_MINOR_CODE, e.getMessage());
		}
	}
	
	public static byte[] getBytes(int source, int length) {
		if (source < 0) {
			throw new Error(
					"org.apache.harmony.auth.jgss.GSSUtils.getBytes(int i, int length) does not support negative integer");
		}
		if (length <= 0 || length > 4) {
			throw new Error(
					"org.apache.harmony.auth.jgss.GSSUtils.getBytes(int i, int length) must have 0<length<=4");
		}
		byte[] target = new byte[length];
		int shift = (length - 1) * 8;
		for (int j = 0; j < length; j++) {
			target[j] = (byte) (source >>> shift);			
			shift -=8;
		}
		return target;
	}
	
	public static int toInt(byte[] source, int offset, int length) {
		if (length == 0 || length > 4) {
			throw new Error(
					"org.apache.harmony.auth.jgss.GSSUtils.toInt(byte[] source) must have 0<source.length<=4");
		}
		if (source[0] < 0) {
			throw new Error(
					"org.apache.harmony.auth.jgss.GSSUtils.toInt(byte[] source) does not support negative integer.");
		}
		int target = 0;
		for (int index = offset; index <offset +length; index++) {
			byte b = source[index];
			target <<= 8;			
			target += (b & 0xFF);
		}
		return target;
	}
}
