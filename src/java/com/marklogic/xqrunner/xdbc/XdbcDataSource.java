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

import com.marklogic.xqrunner.XQDataSource;
import com.marklogic.xqrunner.XQException;
import com.marklogic.xqrunner.XQuery;
import com.marklogic.xqrunner.XQVariable;
import com.marklogic.xqrunner.XQVariableType;
import com.marklogic.xqrunner.XQDuration;
import com.marklogic.xqrunner.XQRunner;
import com.marklogic.xqrunner.XQAsyncRunner;
import com.marklogic.xqrunner.generic.GenericQuery;
import com.marklogic.xqrunner.generic.GenericVariable;
import com.marklogic.xqrunner.generic.AsyncRunner;
import com.marklogic.xdmp.XDMPDataSource;
import com.marklogic.xdmp.XDMPDocInsertStream;
import com.marklogic.xdmp.XDMPConnection;
import com.marklogic.xdbc.XDBCException;
import com.marklogic.xdbc.XDBCConnection;

import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: Aug 25, 2004
 * Time: 6:08:04 PM
 */
public class XdbcDataSource implements XQDataSource
{
	private XDMPDataSource dataSource;
	private String user;
	private String password;

	public XdbcDataSource (XDMPDataSource dataSource, String user, String password)
	{
		this.dataSource = dataSource;
		this.user = user;
		this.password = password;
	}

	public XdbcDataSource (String host, int port, String user, String password)
		throws XQException
	{
		this.dataSource = newDataSource (host, port);

		this.user = user;
		this.password = password;
	}

	public XdbcDataSource (String jndiName, String user, String password)
		throws XQException
	{
		this.dataSource = findJndiDataSource (jndiName);
		this.user = user;
		this.password = password;
	}

	// ---------------------------------------------------------------

	public XDBCConnection getConnection()
		throws XQException
	{
		try {
			return (dataSource.getConnection (user, password));
		} catch (XDBCException e) {
			throw new XQException (e.getMessage(), e);
		}
	}

	public XDBCConnection getConnection (String user, String password)
		throws XQException
	{
		try {
			return (dataSource.getConnection (user, password));
		} catch (XDBCException e) {
			throw new XQException (e.getMessage(), e);
		}
	}

	public XDMPDocInsertStream getDocInsertStream (String uri) throws XQException
	{
		XDMPConnection connection = null;
		XDBCConnection conn = null;

		try {
			conn = dataSource.getConnection();
			connection = new XDMPConnection (conn.getHost(), conn.getPort(), user, password);

			// FIXME: handle doc attributes
			return (connection.openDocInsertStream (uri));
		} catch (XDBCException e) {
			throw new XQException (e.getMessage(), e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (XDBCException e) {
					// nothing
				}
			}
		}
	}

	// ---------------------------------------------------------------
	// Implementation of the XQDataSource interface

	public XQRunner newSyncRunner()
	{
		return (new XdbcSyncRunner (this));
	}

	public XQAsyncRunner newAsyncRunner()
	{
		return (new AsyncRunner (newSyncRunner()));
	}

	public XQAsyncRunner newAsyncRunner (XQRunner runner)
	{
		return (new AsyncRunner (runner));
	}

	public XQuery newQuery (String body)
	{
		return (new GenericQuery (body));
	}

	public XQVariable newVariable (String namespace, String localname, XQVariableType type, Object value)
	{
		return (new GenericVariable (namespace, localname, type, value));
	}

	public XQVariable newVariable (String localname, XQVariableType type, Object value)
	{
		return (new GenericVariable (localname, type, value));
	}

	public XQVariable newVariable (String namespace, String localname, XQVariableType type, long value)
	{
		return (new GenericVariable (namespace, localname, type, value));
	}

	public XQVariable newVariable (String localname, XQVariableType type, long value)
	{
		return (new GenericVariable (localname, type, value));
	}

	public XQVariable newVariable (String namespace, String localname, XQVariableType type, double value)
	{
		return (new GenericVariable (namespace, localname, type, value));
	}

	public XQVariable newVariable (String localname, XQVariableType type, double value)
	{
		return (new GenericVariable (localname, type, value));
	}

	public XQVariable newVariable (String namespace, String localname, boolean value)
	{
		return (new GenericVariable (namespace, localname, value));
	}

	public XQVariable newVariable (String localname, boolean value)
	{
		return (new GenericVariable (localname, value));
	}

	public XQDuration newDuration (int years, int months, int days, int hours, int minutes, int seconds, double subseconds)
	{
		return newDuration (true, years, months, days, hours, minutes, seconds, subseconds);
	}

	public XQDuration newDuration (boolean positive, int years, int months, int days, int hours, int minutes, int seconds, double subseconds)
	{
		return (new XdbcDurationAdapter (positive, years, months, days, hours, minutes, seconds, subseconds));
	}

	public XQDuration newDuration (String value)
	{
		return (new XdbcDurationAdapter (value));
	}

	// ---------------------------------------------------------------

	private XDMPDataSource newDataSource (String host, int port)
		throws XQException
	{
		try {
			return (new XDMPDataSource (host, port));
		} catch (XDBCException e) {
			throw new XQException (e.getMessage(), e);
		}
	}

	private XDMPDataSource findJndiDataSource (String jndiName)
		throws XQException
	{
		InitialContext jndiContext = null;

		try {
			jndiContext = new InitialContext();
		} catch (NamingException e) {
			throw new XQException ("JNDI service unavaliable", e);
		}

		try {
			return ((XDMPDataSource) jndiContext.lookup (jndiName));
		} catch (NamingException e) {
			// nothing
		}

		try {
			return ((XDMPDataSource) jndiContext.lookup ("java:comp/env/" + jndiName));
		} catch (NamingException ex) {
			// nothing
		}

		throw new XQException ("Cannot locate JNDI DataSource: " + jndiName);
	}

	// ---------------------------------------------------------

}
