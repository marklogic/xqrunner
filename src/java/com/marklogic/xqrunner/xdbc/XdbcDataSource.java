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
import com.marklogic.xqrunner.generic.GenericQuery;
import com.marklogic.xdmp.XDMPDataSource;
import com.marklogic.xdbc.XDBCException;

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

	public Object getConnection()
		throws XQException
	{
		try {
			return (dataSource.getConnection (user, password));
		} catch (XDBCException e) {
			throw new XQException (e.getMessage(), e);
		}
	}

	public Object getConnection (String user, String password)
		throws XQException
	{
		try {
			return (dataSource.getConnection (user, password));
		} catch (XDBCException e) {
			throw new XQException (e.getMessage(), e);
		}
	}

	public XQuery newQuery (String body)
	{
		return (new GenericQuery (body));
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
}
