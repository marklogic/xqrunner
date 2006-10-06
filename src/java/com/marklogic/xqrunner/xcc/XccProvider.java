package com.marklogic.xqrunner.xcc;

import com.marklogic.xqrunner.spi.XQProvider;
import com.marklogic.xqrunner.XQDataSource;
import com.marklogic.xqrunner.XQException;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: Jul 26, 2006
 * Time: 4:26:36 PM
 */
public class XccProvider  implements XQProvider
{
	public static final String NAME = "xcc";

	private String name = NAME;

	public void setName (String name)
	{
		this.name = name;
	}

	public String getName ()
	{
		return (name);
	}

	public XQDataSource newDataSource (String host, int port, String user, String password) throws XQException
	{
		return (new XccDataSource (host, port, user, password));
	}

	public XQDataSource newDataSource (String key, String user, String password) throws XQException
	{
		return (new XccDataSource (key, user, password));
	}
}
