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
package com.marklogic.xqrunner;

import junit.framework.TestCase;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: Oct 18, 2004
 * Time: 12:50:07 PM
 */
public class TestResultTypes extends TestCase
{
	XQDataSource dataSource = null;
	XQRunner runner = null;

	protected void setUp () throws Exception
	{
		super.setUp ();

		XQFactory factory = new XQFactory();

		String host = System.getProperty ("xqhost");
		int port = Integer.parseInt (System.getProperty ("xqport"));
		String user = System.getProperty ("xquser");
		String password = System.getProperty ("xqpw");

		dataSource = factory.newDataSource (host, port, user, password);
		runner = dataSource.newSyncRunner();
	}


	public void testNode() throws XQException
	{
		XQResult result = runner.runQuery (dataSource.newQuery ("<foo>bar</foo>"));
		XQResultItem item = result.getItem (0);

		assertTrue (item.isNode());
		assertSame (XQVariableType.NODE, item.getType());
	}

	public void testText() throws XQException
	{
		XQResult result = runner.runQuery (dataSource.newQuery ("" +
			"define function getText ($arg) { $arg/text() }" +
			"getText(<foo>bar</foo>)"));
		XQResultItem item = result.getItem (0);

		assertFalse (item.isNode());
		assertSame (XQVariableType.TEXT, item.getType());
		assertEquals ("bar", item.asString());
	}

	public void testBoolean() throws XQException
	{
		XQResult result = runner.runQuery (dataSource.newQuery ("true()"));
		XQResultItem item = result.getItem (0);

		assertFalse (item.isNode());
		assertSame (XQVariableType.XS_BOOLEAN, item.getType());
	}

	public void testInteger() throws XQException
	{
		XQResult result = runner.runQuery (dataSource.newQuery ("xs:integer(12)"));
		XQResultItem item = result.getItem (0);

		assertFalse (item.isNode());
		assertSame (XQVariableType.XS_INTEGER, item.getType());
	}

	public void testFloat() throws XQException
	{
		XQResult result = runner.runQuery (dataSource.newQuery ("xs:float(47.345)"));
		XQResultItem item = result.getItem (0);

		assertFalse (item.isNode());
		assertSame (XQVariableType.XS_FLOAT, item.getType());
	}

	public void testDouble() throws XQException
	{
		XQResult result = runner.runQuery (dataSource.newQuery ("xs:double(467.23)"));
		XQResultItem item = result.getItem (0);

		assertFalse (item.isNode());
		assertSame (XQVariableType.XS_DOUBLE, item.getType());
	}

	public void testDecimal() throws XQException
	{
		XQResult result = runner.runQuery (dataSource.newQuery ("xs:decimal(47954.35687)"));
		XQResultItem item = result.getItem (0);

		assertFalse (item.isNode());
		assertSame (XQVariableType.XS_DECIMAL, item.getType());
	}

	public void testDate() throws XQException
	{
		XQResult result = runner.runQuery (dataSource.newQuery ("xs:date(\"2004-10-18\")"));
		XQResultItem item = result.getItem (0);

		assertFalse (item.isNode());
		assertSame (XQVariableType.XS_DATE, item.getType());
	}

	public void testDateTime() throws XQException
	{
		XQResult result = runner.runQuery (dataSource.newQuery ("xs:dateTime(\"2004-10-18T14:12:00-07:00\")"));
		XQResultItem item = result.getItem (0);

		assertFalse (item.isNode());
		assertSame (XQVariableType.XS_DATE_TIME, item.getType());
	}

	public void testTime() throws XQException
	{
		XQResult result = runner.runQuery (dataSource.newQuery ("xs:time(\"10:23:34.894\")"));
		XQResultItem item = result.getItem (0);

		assertFalse (item.isNode());
		assertSame (XQVariableType.XS_TIME, item.getType());
	}

	public void testGDay() throws XQException
	{
		XQResult result = runner.runQuery (dataSource.newQuery ("xs:gDay(xs:date(\"2004-10-18\"))"));
		XQResultItem item = result.getItem (0);

		assertFalse (item.isNode());
		assertSame (XQVariableType.XS_GDAY, item.getType());
	}

	public void testGMonth() throws XQException
	{
		XQResult result = runner.runQuery (dataSource.newQuery ("xs:gMonth(xs:date(\"2004-10-18\"))"));
		XQResultItem item = result.getItem (0);

		assertFalse (item.isNode());
		assertSame (XQVariableType.XS_GMONTH, item.getType());
	}

	public void testGMonthDay() throws XQException
	{
		XQResult result = runner.runQuery (dataSource.newQuery ("xs:gMonthDay(xs:date(\"2004-10-18\"))"));
		XQResultItem item = result.getItem (0);

		assertFalse (item.isNode());
		assertSame (XQVariableType.XS_GMONTHDAY, item.getType());
	}

	public void testGYear() throws XQException
	{
		XQResult result = runner.runQuery (dataSource.newQuery ("xs:gYear(xs:date(\"2004-10-18\"))"));
		XQResultItem item = result.getItem (0);

		assertFalse (item.isNode());
		assertSame (XQVariableType.XS_GYEAR, item.getType());
	}

	public void testGYearMonth() throws XQException
	{
		XQResult result = runner.runQuery (dataSource.newQuery ("xs:gYearMonth(xs:date(\"2004-10-18\"))"));
		XQResultItem item = result.getItem (0);

		assertFalse (item.isNode());
		assertSame (XQVariableType.XS_GYEARMONTH, item.getType());
	}

	public void testDuration() throws XQException
	{
		XQResult result = runner.runQuery (dataSource.newQuery ("xs:duration (\"P02Y04M3DT12H47M\")"));
		XQResultItem item = result.getItem (0);

		assertFalse (item.isNode());
		assertSame (XQVariableType.XS_DURATION, item.getType());
	}

	public void testYearMonthDuration() throws XQException
	{
		XQResult result = runner.runQuery (dataSource.newQuery ("xdt:yearMonthDuration (\"P07Y05M\")"));
		XQResultItem item = result.getItem (0);

		assertFalse (item.isNode());
		assertSame (XQVariableType.XDT_YEAR_MONTH_DURATION, item.getType());
	}

	public void testDayTimeDuration() throws XQException
	{
		XQResult result = runner.runQuery (dataSource.newQuery ("xdt:dayTimeDuration (\"P09DT4H17M12.3S\")"));
		XQResultItem item = result.getItem (0);

		assertFalse (item.isNode());
		assertSame (XQVariableType.XDT_DAY_TIME_DURATION, item.getType());
	}

	public void testString() throws XQException
	{
		XQResult result = runner.runQuery (dataSource.newQuery ("xs:string(\"foobar\")"));
		XQResultItem item = result.getItem (0);

		assertFalse (item.isNode());
		assertFalse (item.isStreaming());
		assertSame (XQVariableType.XS_STRING, item.getType());
		assertEquals ("foobar", item.asString());
	}

	public void testStringStreaming() throws XQException
	{
		XQResult result = runner.runQueryStreaming (dataSource.newQuery ("xs:string(\"foobar\")"));
		XQResultItem item = result.getItem (0);

		assertFalse (item.isNode());
		assertFalse (item.isStreaming());
		assertSame (XQVariableType.XS_STRING, item.getType());
		assertEquals ("foobar", item.asString());
	}

	public void testAnyURI() throws XQException
	{
		XQResult result = runner.runQuery (dataSource.newQuery ("xs:anyURI(\"blah://snicker/doodle/\")"));
		XQResultItem item = result.getItem (0);

		assertFalse (item.isNode());
		assertSame (XQVariableType.XS_ANY_URI, item.getType());
		assertEquals ("blah://snicker/doodle/", item.asString());
	}

	public void testQName() throws XQException
	{
		XQResult result = runner.runQuery (dataSource.newQuery ("declare namespace foo=\"blah\" xs:QName(\"foo:foolocal\")"));
		XQResultItem item = result.getItem (0);

		assertFalse (item.isNode());
		assertSame (XQVariableType.XS_QNAME, item.getType());
		assertEquals ("foo:foolocal", item.asString());
	}

	public void testHexBinary() throws XQException, IOException
	{
		XQResult result = runner.runQuery (dataSource.newQuery ("xs:hexBinary(\"ABCDEF012345\")"));
		XQResultItem item = result.getItem (0);

		assertFalse (item.isNode());
		assertSame (XQVariableType.XS_HEXBINARY, item.getType());
		assertEquals ("ABCDEF012345", item.asString());

		byte [] bytes = { (byte) 0xAB, (byte) 0xCD, (byte) 0xEF, 0x01, 0x23, 0x45 };
		assertTrue (sameBytes (bytes, item.asBytes()));
	}

	public void testHexBinaryStreaming() throws XQException
	{
		XQResult result = runner.runQueryStreaming (dataSource.newQuery ("xs:hexBinary(\"ABCDEF012345\")"));
		XQResultItem item = result.getItem (0);

		assertFalse (item.isNode());
		assertSame (XQVariableType.XS_HEXBINARY, item.getType());
		assertEquals ("ABCDEF012345", item.asString());
	}

	private String clearText =
		"The quick brown fox jumped over the lazy dog\n" +
		"The rain in Spain falls mainly on the plain\n" +
		"This text is meaningless";

	private String base64String =
		"VGhlIHF1aWNrIGJyb3duIGZveCBqdW1wZWQgb3ZlciB0aGUgbGF6eSBkb2cKVGhlIHJhaW4gaW4g" +
		"U3BhaW4gZmFsbHMgbWFpbmx5IG9uIHRoZSBwbGFpbgpUaGlzIHRleHQgaXMgbWVhbmluZ2xlc3M=";

	public void testBase64Binary() throws XQException, IOException
	{
		XQResult result = runner.runQuery (dataSource.newQuery (
			"xs:base64Binary(\"" + base64String + "\")"));
		XQResultItem item = result.getItem (0);

		assertFalse (item.isNode());
		assertSame (XQVariableType.XS_BASE64BINARY, item.getType());
		assertEquals (base64String, item.asString());
		assertEquals (clearText, new String (item.asBytes()));
	}

	public void testBase64BinaryStreaming() throws XQException, IOException
	{
		XQResult result = runner.runQueryStreaming (dataSource.newQuery (
			"xs:base64Binary(\"" + base64String + "\")"));
		XQResultItem item = result.getItem (0);

		assertFalse (item.isNode());
		assertSame (XQVariableType.XS_BASE64BINARY, item.getType());
		assertEquals (base64String, item.asString());
		assertEquals (clearText, new String (item.asBytes()));
	}

	public void testBinary() throws XQException, IOException
	{
		XQResult result = runner.runQuery (dataSource.newQuery ("xdmp:unquote(\"foo\", \"\", \"format-binary\")/binary()"));
		XQResultItem item = result.getItem (0);

		assertFalse (item.isStreaming());
		assertFalse (item.isNode());
		assertSame (XQVariableType.BINARY, item.getType());
		assertEquals ("foo", new String (item.asBytes()));
		assertFalse (item.isStreaming());
		assertEquals ("foo", item.asString());
	}

	public void testBinaryStreaming() throws XQException, IOException
	{
		XQResult result = runner.runQueryStreaming (dataSource.newQuery ("xdmp:unquote(\"foo\", \"\", \"format-binary\")/binary()"));
		XQResultItem item = result.getItem (0);

		assertTrue (item.isStreaming());
		assertFalse (item.isNode());
		assertSame (XQVariableType.BINARY, item.getType());
		assertEquals ("foo", new String (item.asBytes()));
		assertFalse (item.isStreaming());
		assertEquals ("foo", item.asString());
	}

	// --------------------------------------------------------------

	private boolean sameBytes (byte[] b1, byte[] b2)
	{
		if (b1.length != b2.length) {
			throw new RuntimeException ("b1.length: " + b1.length + ", b2.length: " + b2.length);
		}

		for (int i = 0; i < b1.length; i++) {
			if (b1 [i] != b2[i]) {
				throw new RuntimeException ("[" + i + "] b1=" + b1 [i] + ", b2=" + b2 [i]);
			}
		}

		return (true);
	}
}
