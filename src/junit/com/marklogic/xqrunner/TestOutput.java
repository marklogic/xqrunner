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

import java.io.StringWriter;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: Nov 4, 2004
 * Time: 1:50:25 PM
 */
public class TestOutput extends TestCase
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

	public void testWrite() throws XQException, IOException
	{
		XQResult result = runner.runQuery (dataSource.newQuery ("\"Hello World\""));
		StringWriter writer = new StringWriter();

		result.writeTo (writer);
		assertEquals ("Hello World", writer.toString());

		writer = new StringWriter();
		result = runner.runQuery (dataSource.newQuery ("(\"Hello World\", \"and\", \"Goodbye\")"));
		result.writeTo (writer, "_");
		assertEquals ("Hello World_and_Goodbye", writer.toString());
	}

	public void testAsString() throws XQException, IOException
	{
		XQResult result = runner.runQuery (dataSource.newQuery ("\"Hello World\""));

		assertEquals ("Hello World", result.asString());

		result = runner.runQuery (dataSource.newQuery ("(\"Hello World\", \"and\", \"Goodbye\")"));
		assertEquals ("Hello World_and_Goodbye", result.asString ("_"));
	}
}
