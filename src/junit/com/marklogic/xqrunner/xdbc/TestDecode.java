/*
 * Copyright (c)2004 Mark Logic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * The use of the Apache License does not indicate that this project is
 * affiliated with the Apache Software Foundation.
 */
package com.marklogic.xqrunner.xdbc;

import junit.framework.TestCase;
import com.marklogic.xqrunner.generic.Base64;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: Nov 9, 2004
 * Time: 6:06:03 PM
 */
public class TestDecode extends TestCase
{
	private byte [] bytes1 = new byte [0];
	private byte [] bytes2 = { (byte)0xAB, (byte)0xCD, 0x12, 0x34 };

	public void testHexDecode()
	{
		assertTrue (sameBytes (bytes1, XdbcResultItem.decodeHexString ("   ")));
		assertTrue (sameBytes (bytes2, XdbcResultItem.decodeHexString ("ABCD1234")));
	}

	public void testByteValue()
	{
		assertEquals (0x12, XdbcResultItem.byteValue ('1', '2'));
		assertEquals (0xAB, XdbcResultItem.byteValue ('a', 'B'));
	}

	public void testHexValue()
	{
		assertEquals (0, XdbcResultItem.hexValue ('0'));
		assertEquals (5, XdbcResultItem.hexValue ('5'));
		assertEquals (9, XdbcResultItem.hexValue ('9'));
		assertEquals (0xa, XdbcResultItem.hexValue ('a'));
		assertEquals (0xa, XdbcResultItem.hexValue ('A'));
		assertEquals (0xc, XdbcResultItem.hexValue ('c'));
		assertEquals (0xc, XdbcResultItem.hexValue ('C'));
		assertEquals (0xf, XdbcResultItem.hexValue ('f'));
		assertEquals (0xf, XdbcResultItem.hexValue ('F'));
	}

	private String clearText =
		"The quick brown fox jumped over the lazy dog\n" +
		"The rain in Spain falls mainly on the plain\n" +
		"This text is meaningless";

	private String base64String =
		"VGhlIHF1aWNrIGJyb3duIGZveCBqdW1wZWQgb3ZlciB0aGUgbGF6eSBkb2cKVGhlIHJhaW4gaW4g\n" +
		"U3BhaW4gZmFsbHMgbWFpbmx5IG9uIHRoZSBwbGFpbgpUaGlzIHRleHQgaXMgbWVhbmluZ2xlc3M=";


	public void testBase64Decode()
	{
		assertEquals (clearText, new String (Base64.decode (base64String)));
	}

	public void testBase64Encode()
	{
		assertEquals (base64String, Base64.encodeBytes (clearText.getBytes()));
	}

	// ---------------------------------------------------------

	private static boolean sameBytes (byte[] b1, byte[] b2)
	{
		if (b1.length != b2.length) {
			throw new RuntimeException ("b1.length: " + b1.length + ", b2.length: " + b2.length);
		}

		for (int i = 0; i < b1.length; i++) {
			if (b1 [i] != b2[i]) {
				throw new RuntimeException ("[" + i + "] b1=" + hex (b1 [i]) + ", b2=" + hex (b2 [i]));
			}
		}

		return (true);
	}

	private static String hex (byte b)
	{
		int value = b & 0xff;

		return (hexDigit (value >> 4) + hexDigit (value & 0xf));
	}

	private static String hexDigit (int value)
	{
		if ((value >= 10)) {
			return ("" + (char)(value - 10 + 'a'));
		}

		return ("" + (char)(value + '0'));
	}
}
