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
import com.marklogic.xqrunner.XQRunner;
import com.marklogic.xqrunner.XQDataSource;
import com.marklogic.xqrunner.XQException;
import com.marklogic.xqrunner.XQResult;
import com.marklogic.xqrunner.XQFactory;

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
		runner = factory.newSyncRunner (dataSource);
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

}
