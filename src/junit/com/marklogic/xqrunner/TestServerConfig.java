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

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: Feb 17, 2005
 * Time: 11:39:18 AM
 */
public class TestServerConfig
{
	private static boolean configed = false;
	private static XQDataSource dataSource = null;
	private static XQRunner runner = null;

	private TestServerConfig ()
	{
		// no no no
	}

	private static void setUp() throws XQException
	{
		if (configed) {
			return;
		}

		XQFactory factory = new XQFactory();

		String host = getHost ();
		int port = getPort ();
		String user = getUser ();
		String password = getPassword ();

		dataSource = factory.newDataSource (host, port, user, password);
		runner = dataSource.newSyncRunner();

		configed = true;
	}

	public static String getHost ()
	{
		return System.getProperty ("xqhost");
	}

	public static int getPort ()
	{
		return Integer.parseInt (System.getProperty ("xqport"));
	}

	public static String getPassword ()
	{
		return System.getProperty ("xqpw");
	}

	public static String getUser ()
	{
		return System.getProperty ("xquser");
	}

	public static XQDataSource getDataSource() throws XQException
	{
		setUp();

		return (dataSource);
	}

	public static XQRunner getRunner() throws XQException
	{
		setUp();

		return (runner);
	}
}
