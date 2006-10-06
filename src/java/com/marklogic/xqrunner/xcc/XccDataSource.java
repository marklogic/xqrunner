package com.marklogic.xqrunner.xcc;

import com.marklogic.xcc.ContentSource;
import com.marklogic.xcc.ContentSourceFactory;
import com.marklogic.xqrunner.XQAsyncRunner;
import com.marklogic.xqrunner.XQDataSource;
import com.marklogic.xqrunner.XQDuration;
import com.marklogic.xqrunner.XQException;
import com.marklogic.xqrunner.XQRunner;
import com.marklogic.xqrunner.XQVariable;
import com.marklogic.xqrunner.XQVariableType;
import com.marklogic.xqrunner.XQuery;
import com.marklogic.xqrunner.generic.AsyncRunner;
import com.marklogic.xqrunner.generic.GenericQuery;
import com.marklogic.xqrunner.generic.GenericVariable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: Jul 26, 2006
 * Time: 4:28:23 PM
 */
public class XccDataSource implements XQDataSource
{
	private final ContentSource contentSource;
	private final String user;
	private final String password;

	public XccDataSource (String host, int port, String user, String password)
	{
		contentSource = ContentSourceFactory.newContentSource (host, port, user, password);
		this.user = user;
		this.password = password;
	}

	public XccDataSource (String jndiName, String user, String password) throws XQException
	{
		this.user = user;
		this.password = password;

		try {
			Context initCtx = new InitialContext ();
			Context envCtx = (Context) initCtx.lookup ("java:comp/env");

			contentSource = (ContentSource) envCtx.lookup (jndiName);
		} catch (NamingException e) {
			throw new XQException ("Cannot locate JNDI DataSource: " + jndiName, e);
		} catch (ClassCastException e) {
			throw new XQException ("JNDI object '" + jndiName + "' is not of type ContentSource: " + e.getMessage(), e);
		}
	}

	public XQRunner newSyncRunner()
	{
		return (new XccSyncRunner (contentSource.newSession (user, password)));
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
		return (new XccDurationAdapter (positive, years, months, days, hours, minutes, seconds, subseconds));
	}

	public XQDuration newDuration (String value)
	{
		return (new XccDurationAdapter (value));
	}
}
