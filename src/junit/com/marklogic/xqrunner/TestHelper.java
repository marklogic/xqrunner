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

import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: Oct 18, 2004
 * Time: 11:15:55 PM
 */
public class TestHelper extends TestCase
{
	XQDataSource dataSource = null;
	XQRunner runner = null;

	protected void setUp () throws Exception
	{
		super.setUp ();

		dataSource = TestServerConfig.getDataSource();
		runner = TestServerConfig.getRunner();
	}

	public void testStrings() throws XQException
	{
		String [] expected = { "12", "foo", "<foo/>" };
		String [] result = XQHelper.executeStrings (runner, dataSource.newQuery ("(12, \"foo\", <foo></foo>)"));

		for (int i = 0; i < expected.length; i++) {
			String e = expected [i];
			String r = result [i];

			assertEquals ("Strings[" + i + "]", e, r);
		}
	}

	public void testString() throws XQException
	{
		String result = XQHelper.executeString (runner, dataSource.newQuery ("(12, \"foo\", <foo></foo>)"));

		assertEquals ("12foo<foo/>", result);
	}

	public void testString2() throws XQException
	{
		String result = XQHelper.executeString (runner, dataSource.newQuery ("(12, \"foo\", <foo></foo>)"), "*");

		assertEquals ("12*foo*<foo/>", result);
	}

	public void testJDom() throws XQException
	{
		try {
			XQHelper.executeJDom (runner, dataSource.newQuery ("()"));
			fail ("Expected empty sequence exception");
		} catch (XQException e) {
			assertEquals ("Empty result sequence", e.getMessage());
		}

		try {
			XQHelper.executeJDom (runner, dataSource.newQuery ("\"foobar\""));
			fail ("Expected type clash exception");
		} catch (XQException e) {
			assertEquals ("Result is not a node (xs:string)", e.getMessage());
		}

		org.jdom.Document result = XQHelper.executeJDom (runner, dataSource.newQuery ("<foo>bar</foo>"));

		assertEquals ("bar", result.getRootElement().getText());
	}

	public void testW3CDom() throws XQException
	{
		try {
			XQHelper.executeJDom (runner, dataSource.newQuery ("()"));
			fail ("Expected empty sequence exception");
		} catch (XQException e) {
			assertEquals ("Empty result sequence", e.getMessage());
		}

		try {
			XQHelper.executeJDom (runner, dataSource.newQuery ("\"foobar\""));
			fail ("Expected type clash exception");
		} catch (XQException e) {
			assertEquals ("Result is not a node (xs:string)", e.getMessage());
		}

		org.w3c.dom.Document result = XQHelper.executeW3CDom (runner, dataSource.newQuery ("<foo>bar</foo>"));

		assertEquals ("bar", result.getDocumentElement().getFirstChild().getNodeValue());
	}

	public void testBoolean() throws XQException
	{
		try {
			XQHelper.executeBoolean (runner, dataSource.newQuery ("()"));
			fail ("Expected empty sequence exception");
		} catch (XQException e) {
			assertEquals ("Empty result sequence", e.getMessage());
		}

		try {
			XQHelper.executeBoolean (runner, dataSource.newQuery ("\"foobar\""));
			fail ("Expected type clash exception");
		} catch (XQException e) {
			assertEquals ("Item is not of expected type: expected=xs:boolean, actual=xs:string", e.getMessage());
		}

		assertTrue (XQHelper.executeBoolean (runner, dataSource.newQuery ("true()")));
		assertFalse (XQHelper.executeBoolean (runner, dataSource.newQuery ("false()")));
		assertTrue (XQHelper.executeBoolean (runner, dataSource.newQuery ("(true(), \"blah\", <foo>bar</foo>)")));
	}

	public void testInteger() throws XQException
	{
		try {
			XQHelper.executeInteger (runner, dataSource.newQuery ("()"));
			fail ("Expected empty sequence exception");
		} catch (XQException e) {
			assertEquals ("Empty result sequence", e.getMessage());
		}

		try {
			XQHelper.executeInteger (runner, dataSource.newQuery ("\"foobar\""));
			fail ("Expected type clash exception");
		} catch (XQException e) {
			assertEquals ("Item is not of expected type: expected=xs:integer, actual=xs:string", e.getMessage());
		}

		assertEquals (12, XQHelper.executeInteger (runner, dataSource.newQuery ("12")));
		assertEquals (2323234, XQHelper.executeInteger (runner, dataSource.newQuery ("(2323234, <floob>xx</floob>)")));
	}

	public void testFloat() throws XQException
	{
		try {
			XQHelper.executeFloat (runner, dataSource.newQuery ("()"));
			fail ("Expected empty sequence exception");
		} catch (XQException e) {
			assertEquals ("Empty result sequence", e.getMessage());
		}

		try {
			XQHelper.executeFloat (runner, dataSource.newQuery ("\"foobar\""));
			fail ("Expected type clash exception");
		} catch (XQException e) {
			assertEquals ("Item is not of expected type: expected=xs:float, actual=xs:string", e.getMessage());
		}

		assertEquals (12.75, XQHelper.executeFloat (runner, dataSource.newQuery ("xs:float(12.75)")), 0.0);
		assertEquals (23232.25, XQHelper.executeFloat (runner, dataSource.newQuery ("(xs:float(23232.25), <floob>xx</floob>)")), 0.0);
	}

	public void testDouble() throws XQException
	{
		try {
			XQHelper.executeDouble (runner, dataSource.newQuery ("()"));
			fail ("Expected empty sequence exception");
		} catch (XQException e) {
			assertEquals ("Empty result sequence", e.getMessage());
		}

		try {
			XQHelper.executeDouble (runner, dataSource.newQuery ("\"foobar\""));
			fail ("Expected type clash exception");
		} catch (XQException e) {
			assertEquals ("Item is not of expected type: expected=xs:double, actual=xs:string", e.getMessage());
		}

		assertEquals (13468532622.75, XQHelper.executeDouble (runner, dataSource.newQuery ("xs:double(13468532622.75)")), 0.0);
		assertEquals (237767898232.25, XQHelper.executeDouble (runner, dataSource.newQuery ("(xs:double(237767898232.25), <floob>xx</floob>)")), 0.0);
	}

	public void testDecimal() throws XQException
	{
		try {
			XQHelper.executeDecimal (runner, dataSource.newQuery ("()"));
			fail ("Expected empty sequence exception");
		} catch (XQException e) {
			assertEquals ("Empty result sequence", e.getMessage());
		}

		try {
			XQHelper.executeDecimal (runner, dataSource.newQuery ("\"foobar\""));
			fail ("Expected type clash exception");
		} catch (XQException e) {
			assertEquals ("Item is not of expected type: expected=xs:decimal, actual=xs:string", e.getMessage());
		}

		assertEquals (13468532622.75, XQHelper.executeDecimal (runner, dataSource.newQuery ("13468532622.75")), 0.0);
		assertEquals (237767898232.25, XQHelper.executeDecimal (runner, dataSource.newQuery ("237767898232.25, <floob>xx</floob>)")), 0.0);
	}

	public void testAtomic() throws XQException
	{
		try {
			XQHelper.executeAtomic (runner, dataSource.newQuery ("()"));
			fail ("Expected empty sequence exception");
		} catch (XQException e) {
			assertEquals ("Empty result sequence", e.getMessage());
		}

		try {
			XQHelper.executeAtomic (runner, dataSource.newQuery ("<foo>bar</foo>"));
			fail ("Expected type clash exception");
		} catch (XQException e) {
			assertEquals ("Result is not atomic (node())", e.getMessage());
		}

		Object object = XQHelper.executeAtomic (runner, dataSource.newQuery ("\"foobar\""));
		assertTrue (object instanceof String);
		assertEquals ("foobar", object);

		object = XQHelper.executeAtomic (runner, dataSource.newQuery ("123.56"));
		assertTrue (object instanceof BigDecimal);
		assertEquals (new BigDecimal ("123.56"), object);

		object = XQHelper.executeAtomic (runner, dataSource.newQuery ("xs:double(123.56)"));
		assertTrue (object instanceof Double);
		assertEquals (new Double (123.56), object);

		object = XQHelper.executeAtomic (runner, dataSource.newQuery ("xs:anyURI(\"blah://snurt/floob/\")"));
		assertTrue (object instanceof String);
		assertEquals ("blah://snurt/floob/", object);

		object = XQHelper.executeAtomic (runner, dataSource.newQuery ("(\"foobar\", <foo>bar</foo>)"));
		assertTrue (object instanceof String);
		assertEquals ("foobar", object);
	}

	public void testObject() throws XQException
	{
		try {
			XQHelper.executeObject (runner, dataSource.newQuery ("()"));
			fail ("Expected empty sequence exception");
		} catch (XQException e) {
			assertEquals ("Empty result sequence", e.getMessage());
		}

		Object object = XQHelper.executeObject (runner, dataSource.newQuery ("\"foobar\""));
		assertTrue (object instanceof String);
		assertEquals ("foobar", object);

		object = XQHelper.executeObject (runner, dataSource.newQuery ("123.56"));
		assertTrue (object instanceof BigDecimal);
		assertEquals (new BigDecimal ("123.56"), object);

		object = XQHelper.executeObject (runner, dataSource.newQuery ("xs:double(123.56)"));
		assertTrue (object instanceof Double);
		assertEquals (new Double (123.56), object);

		object = XQHelper.executeObject (runner, dataSource.newQuery ("xs:anyURI(\"blah://snurt/floob/\")"));
		assertTrue (object instanceof String);
		assertEquals ("blah://snurt/floob/", object);

		object = XQHelper.executeObject (runner, dataSource.newQuery ("(\"foobar\", <foo>bar</foo>)"));
		assertTrue (object instanceof String);
		assertEquals ("foobar", object);

		object = XQHelper.executeObject (runner, dataSource.newQuery ("<foo></foo>"));
		// no exception expected, object is opaque
	}

	public void testObjects() throws XQException
	{
		Object [] expected = { new Integer(12), new BigDecimal ("1234.56"), "foo", null, "blah://foo/blech" };
		Object [] result = XQHelper.executeObjects (runner, dataSource.newQuery ("(12, 1234.56, \"foo\", <foo></foo>, xs:anyURI(\"blah://foo/blech\"))"));

		for (int i = 0; i < expected.length; i++) {
			Object e = expected [i];
			Object r = result [i];

			if (expected [i] != null) {
				assertEquals ("Objects[" + i + "]", e, r);
			}
		}
	}
}
