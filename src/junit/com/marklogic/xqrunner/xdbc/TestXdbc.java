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
import com.marklogic.xdbc.XDBCConnection;
import com.marklogic.xdbc.XDBCStatement;
import com.marklogic.xdbc.XDBCException;
import com.marklogic.xdbc.XDBCXName;
import com.marklogic.xdbc.XDBCResultSequence;
import com.marklogic.xdmp.XDMPDataSource;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: Oct 1, 2004
 * Time: 1:55:11 PM
 */
public class TestXdbc extends TestCase
{
	XDBCConnection connection;

	protected void setUp () throws Exception
	{
		super.setUp ();

		String host = System.getProperty ("xqhost");
		int port = Integer.parseInt (System.getProperty ("xqport"));
		String user = System.getProperty ("xquser");
		String password = System.getProperty ("xqpw");

		XDMPDataSource datasource = new XDMPDataSource (host, port);
		connection = datasource.getConnection (user, password);
	}

	public void testExtvarNoNamespace() throws XDBCException
	{
		XDBCStatement statement = connection.createStatement();

		statement.setString (new XDBCXName ("extvar"), "thevalue");

		XDBCResultSequence resultSequence = null;

		try {
			resultSequence = statement.executeQuery (
				"define variable $extvar external\n" +
				"concat (\"var: \", $extvar)");
		} catch (XDBCException e) {
			e.getCause().printStackTrace();
		}

		resultSequence.next ();

		assertEquals ("var: thevalue", resultSequence.get_String ());
	}

	public void testExtvarNullStringNamespace() throws XDBCException
	{
		XDBCStatement statement = connection.createStatement();

		statement.setString (new XDBCXName ("", "extvar"), "thevalue");

		XDBCResultSequence resultSequence = null;

		try {
			resultSequence = statement.executeQuery (
				"define variable $extvar external\n" +
				"concat (\"var: \", $extvar)");
		} catch (XDBCException e) {
			e.getCause().printStackTrace();
		}

		resultSequence.next ();

		assertEquals ("var: thevalue", resultSequence.get_String ());
	}

	public void testExtvarWithNamespace() throws XDBCException
	{
		XDBCStatement statement = connection.createStatement();

		statement.setString (new XDBCXName ("foobar", "extvar"), "thevalue");

		XDBCResultSequence resultSequence = null;

		try {
			resultSequence = statement.executeQuery (
				"declare namespace foo=\"foobar\"\n" +
				"define variable $foo:extvar external\n" +
				"concat (\"var: \", $foo:extvar)");
		} catch (XDBCException e) {
			e.getCause().printStackTrace();
		}

		resultSequence.next ();

		assertEquals ("var: thevalue", resultSequence.get_String ());
	}
}
