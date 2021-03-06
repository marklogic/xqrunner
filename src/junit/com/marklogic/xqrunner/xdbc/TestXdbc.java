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
import com.marklogic.xqrunner.TestServerConfig;

import java.io.Reader;
import java.io.StringWriter;
import java.io.IOException;

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

		String host = TestServerConfig.getHost();
		int port = TestServerConfig.getPort();
		String user = TestServerConfig.getUser();
		String password = TestServerConfig.getPassword();

		XDMPDataSource datasource = new XDMPDataSource (host, port);
		connection = datasource.getConnection (user, password);
	}

	public void testExtvarNoNamespace() throws XDBCException
	{
		XDBCStatement statement = connection.createStatement();

		statement.setString (new XDBCXName ("extvar"), "thevalue");

		XDBCResultSequence resultSequence = null;

		resultSequence = statement.executeQuery (
			"define variable $extvar external\n" +
			"concat (\"var: \", $extvar)");

		resultSequence.next ();

		assertEquals ("var: thevalue", resultSequence.get_String ());
	}

	public void testExtvarNullStringNamespace() throws XDBCException
	{
		XDBCStatement statement = connection.createStatement();

		statement.setString (new XDBCXName ("", "extvar"), "thevalue");

		XDBCResultSequence resultSequence = null;

		resultSequence = statement.executeQuery (
			"define variable $extvar external\n" +
			"concat (\"var: \", $extvar)");

		resultSequence.next ();

		assertEquals ("var: thevalue", resultSequence.get_String ());
	}

	public void testExtvarWithNamespace() throws XDBCException
	{
		XDBCStatement statement = connection.createStatement();

		statement.setString (new XDBCXName ("foobar", "extvar"), "thevalue");

		XDBCResultSequence resultSequence = null;

		resultSequence = statement.executeQuery (
			"declare namespace foo=\"foobar\"\n" +
			"define variable $foo:extvar external\n" +
			"concat (\"var: \", $foo:extvar)");

		resultSequence.next ();

		assertEquals ("var: thevalue", resultSequence.get_String ());
	}

	public void testStringGetReader() throws XDBCException, IOException
	{
		XDBCStatement statement = connection.createStatement();
		XDBCResultSequence resultSequence = statement.executeQuery ("\"this is a string\"");

		resultSequence.next();

		Reader reader = resultSequence.getReader();

		assertNull (reader);

//		assertNotNull (reader);
//
//		StringWriter stringWriter = new StringWriter();
//		char [] buffer = new char [100];
//		int rc;
//
//		while ((rc = reader.read (buffer)) >= 0) {
//			stringWriter.write (buffer, 0, rc);
//		}
//
//		reader.close();
//
//		assertEquals ("this is a string", stringWriter.toString());
	}

	private static final String nodeString = "<blech>this is a string</blech>";

	public void testNodeGetReader() throws XDBCException, IOException
	{
		XDBCStatement statement = connection.createStatement();
		XDBCResultSequence resultSequence = statement.executeQuery (nodeString);

		resultSequence.next();

		Reader reader = resultSequence.getReader();

		assertNotNull (reader);

		StringWriter stringWriter = new StringWriter();
		char [] buffer = new char [100];
		int rc;

		while ((rc = reader.read (buffer)) >= 0) {
			stringWriter.write (buffer, 0, rc);
		}

		reader.close();

		assertEquals (nodeString, stringWriter.toString());
	}
}
