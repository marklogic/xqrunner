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
import com.marklogic.xqrunner.generic.GenericDocumentWrapper;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: Sep 29, 2004
 * Time: 2:56:38 PM
 */
public class TestSync extends TestCase
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

	public void testSyncRun() throws XQException
	{
		try {
			runner.abortQuery ();
			fail ("abort should fail if no query active");
		} catch (IllegalStateException e) {
			// good result
		}

		XQResult result = runner.runQuery (dataSource.newQuery ("\"Hello World\""));

		assertEquals ("Hello World", result.asString ());

		try {
			runner.abortQuery ();
			fail ("abort should fail if no query active");
		} catch (IllegalStateException e) {
			// good result
		}

		result = runner.runQuery (dataSource.newQuery ("concat (\"foo \", \"bar\")"));

		assertEquals ("foo bar", result.asString ());

		result = runner.runQuery (dataSource.newQuery ("<frobule>{attribute {\"foo\"}{\"bar\"}}</frobule>"));

		assertEquals ("<frobule foo=\"bar\"/>", result.asString ());

		try {
			runner.abortQuery ();
			fail ("abort should fail if no query active");
		} catch (IllegalStateException e) {
			// good result
		}
	}

	public void testSyncRunStreaming() throws XQException
	{
		try {
			runner.abortQuery();
			fail ("abort should fail if no query active");
		} catch (IllegalStateException e) {
			// good result
		}

		XQResult result = runner.runQueryStreaming (dataSource.newQuery ("\"Hello World\""));

		assertEquals ("Hello World", result.asString());

		try {
			runner.abortQuery();
			fail ("abort should fail if no query active");
		} catch (IllegalStateException e) {
			// good result
		}

		result = runner.runQueryStreaming (dataSource.newQuery ("concat (\"foo \", \"bar\")"));

		assertEquals ("foo bar", result.asString());

		result = runner.runQueryStreaming (dataSource.newQuery ("<frobule>{attribute {\"foo\"}{\"bar\"}}</frobule>"));

		assertEquals ("<frobule foo=\"bar\"/>", result.asString());

		try {
			runner.abortQuery ();
			fail ("abort should fail if no query active");
		} catch (IllegalStateException e) {
			// good result
		}
	}

	public void testStreamingAccess() throws XQException
	{
		XQResult result = runner.runQueryStreaming (dataSource.newQuery ("(\"Item0\", \"Item1\", \"Item2\")"));

		XQResultItem item = result.getItem (0);

		assertEquals ("Item0", item.asString());

		try {
			item = result.getItem (2);
			fail ("Expected IllegalStateException on out-of-order access");
		} catch (IllegalStateException e) {
			// good result
		}

		item = result.getItem (0);
		assertEquals ("Item0", item.asString());
		item = result.getItem (0);
		assertEquals ("Item0", item.asString());

		item = result.getItem (1);
		item = result.getItem (2);
		assertEquals ("Item2", item.asString());
	}

	public void testStreamingItemArray() throws XQException
	{
		XQResult result = runner.runQueryStreaming (dataSource.newQuery ("(\"Item0\", \"Item1\", \"Item2\")"));

		for (int i = 0; i < 3; i++) {
			XQResultItem item = result.getItem (i);
			XQResultItem [] items = result.getItems();

			assertEquals (1, items.length);
			assertEquals ("Item" + i, item.asString());
		}

		assertEquals (null, result.getItem (3));
		assertEquals (0, result.getItems().length);

		result = runner.runQueryStreaming (dataSource.newQuery ("()"));

		assertEquals (0, result.getItems().length);
		assertEquals (null, result.getItem (0));
		assertEquals (0, result.getItems().length);
	}

	public void testItemStreamingState() throws XQException
	{
		XQResult result = runner.runQueryStreaming (dataSource.newQuery ("(\"Item0\", <Item1></Item1>, \"Item2\")"));

		XQResultItem item = result.getItem (0);
		assertEquals ("Item0", item.asString());
		assertFalse (item.isNode());
		assertFalse (item.isStreaming());

		item = result.getItem (1);
		assertTrue (item.isStreaming());
		assertEquals ("<Item1/>", item.asString());
		assertTrue (item.isNode());
		assertFalse (item.isStreaming());

		item = result.getItem (2);
		assertEquals ("Item2", item.asString());
		assertFalse (item.isNode());
		assertFalse (item.isStreaming());

		item = result.getItem (3);
		assertNull (item);
	}

	public void testNextItem() throws XQException
	{
		XQResult result = runner.runQuery (dataSource.newQuery ("(\"Item0\", <Item1></Item1>, \"Item2\")"));

		XQResultItem item = result.nextItem();
		assertEquals ("Item0", item.asString());
		assertFalse (item.isNode());
		assertFalse (item.isStreaming());

		item = result.nextItem();
		assertFalse (item.isStreaming());
		assertEquals ("<Item1/>", item.asString());
		assertTrue (item.isNode());
		assertFalse (item.isStreaming());

		item = result.nextItem();
		assertEquals ("Item2", item.asString());
		assertFalse (item.isNode());
		assertFalse (item.isStreaming());

		item = result.nextItem();
		assertNull (item);

		result.rewindItems();
		item = result.nextItem();
		assertEquals ("Item0", item.asString());
		assertFalse (item.isNode());
		assertFalse (item.isStreaming());

		result = runner.runQuery (dataSource.newQuery ("()"));

		item = result.nextItem();
		assertNull (item);
	}

	public void testNextItemStreaming() throws XQException
	{
		XQResult result = runner.runQueryStreaming (dataSource.newQuery ("(\"Item0\", <Item1></Item1>, \"Item2\")"));

		XQResultItem item = result.nextItem();
		assertEquals ("Item0", item.asString());
		assertFalse (item.isNode());
		assertFalse (item.isStreaming());

		item = result.nextItem();
		assertTrue (item.isStreaming());
		assertEquals ("<Item1/>", item.asString());
		assertTrue (item.isNode());
		assertFalse (item.isStreaming());

		item = result.nextItem();
		assertEquals ("Item2", item.asString());
		assertFalse (item.isNode());
		assertFalse (item.isStreaming());

		item = result.nextItem();
		assertNull (item);

		try {
			result.rewindItems();
			fail ("expected IllegalStateException");
		} catch (IllegalStateException e) {
			// expected result
		}

		result = runner.runQueryStreaming (dataSource.newQuery ("()"));

		item = result.nextItem();
		assertNull (item);
	}

	public void testDocInsert() throws XQException, IOException
	{
		String docValue = "<test>" + System.currentTimeMillis() + "</test>";
		XQDocumentWrapper doc = GenericDocumentWrapper.newXml (docValue);

		runner.insertDocument ("/testdocs/test.xml", doc, null);

		XQResult result = runner.runQuery (dataSource.newQuery ("doc(\"/testdocs/test.xml\")"));

		assertEquals (docValue + "\n", result.asString ());
	}
}
