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
import com.marklogic.xqrunner.generic.GenericQuery;
import com.marklogic.xqrunner.generic.GenericVariable;
import com.marklogic.xdmp.XDMPDataSource;
import com.marklogic.xdbc.XDBCException;
import com.marklogic.xdbc.XDBCDuration;

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
		return (new DurationAdapter (positive, years, months, days, hours, minutes, seconds, subseconds));
	}

	public XQDuration newDuration (String value)
	{
		return (new DurationAdapter (value));
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

	static class DurationAdapter implements XQDuration
	{
		private final XDBCDuration duration;

		// FIXME: Check XDBC API, what about Days?
		private DurationAdapter (boolean positive, int years, int months, int days,
			int hours, int minutes, int seconds, double subseconds)
		{
			duration = new XDBCDuration (
				positive ? XDBCDuration.XDBC_DURATION_POSITIVE : XDBCDuration.XDBC_DURATION_NEGATIVE,
				years, months, /*days,*/ hours, minutes, seconds, subseconds);
		}

		private DurationAdapter (String value)
		{
			duration = new XDBCDuration (value);
		}

		XDBCDuration getDuration()
		{
			return (new XDBCDuration (duration.getSign(), duration.getYears(),
				duration.getMonths(), duration.getHours(), duration.getMinutes(),
				duration.getSeconds(), duration.getSubSeconds()));
		}

		public boolean isNegative ()
		{
			return (duration.getSign() == XDBCDuration.XDBC_DURATION_NEGATIVE);
		}

		public int getYears ()
		{
			return duration.getYears ();
		}

		public int getMonths ()
		{
			return duration.getMonths ();
		}

		public int getDays ()
		{
			return duration.getDays ();
		}

		public int getHours ()
		{
			return duration.getHours ();
		}

		public int getMinutes ()
		{
			return duration.getMinutes ();
		}

		public int getSeconds ()
		{
			return duration.getSeconds ();
		}

		public double getSubSeconds ()
		{
			return duration.getSubSeconds ();
		}

		public String toString ()
		{
			return duration.toString ();
		}
	}
}
