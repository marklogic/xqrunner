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
import java.util.Vector;
import java.util.List;
import java.util.Arrays;

import com.marklogic.xqrunner.generic.GenericDocumentWrapper;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: Dec 6, 2004
 * Time: 11:14:45 PM
 */
public class TestResultNullBug extends TestCase
{
	private XQDataSource dataSource = null;
	private XQRunner runner = null;
	private String uri = "/testdocs/test.txt";
	private String docValue = "This is a plain old text document";

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

		XQDocumentWrapper doc = GenericDocumentWrapper.newXml (docValue);

		runner.insertDocument (uri, doc, null);
	}

	public void testNullBug() throws IOException, XQException
	{
// uri is for a 'format-text' file ... a library module
		String uriQuery = "doc('" + uri + "')";

		XQResult result = runner.runQuery(dataSource.newQuery(uriQuery));
		XQResultItem [] items = result.getItems();
		String tmp = null;
// There will only be 1 item and it will already have the preceding 'null'
		for (int i = 0; i < items.length; i++) {
			tmp += items[i].asString();
		}

// Split into lines and insert into vector
		String[] split = tmp.split("\n");
		Vector lines = new Vector();

		for (int i = 0; i < split.length; i++) {
			if (i == 0) {
//				Hack for Mark Logic bug (?)
				lines.add(split[i].substring(4));
			} else {
				lines.add (split[i]);
			}
		}

		assertEquals (1, lines.size ());
		assertEquals (docValue, (String) lines.get (0));
	}

	public void testDarinFix() throws IOException, XQException
	{
		String uriQuery = "doc('" + uri + "')";

		XQResult result = runner.runQuery (dataSource.newQuery (uriQuery));
		String[] split = result.asString().split ("\n");
		List lines = Arrays.asList (split);

		assertEquals (1, lines.size());
		assertEquals (docValue, (String) lines.get (0));
	}
}
