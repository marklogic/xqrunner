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

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: Sep 29, 2004
 * Time: 5:40:09 PM
 */
public class XQRunnerFactory
{
	private static final String PROPERTY_PREFIX = "xqrunner.provider.";
	private static final String DEFAULT_PROVIDER_NAME = "xdbc";
	private static final String DEFAULT_PROVIDER_CLASS =
		"com.marklogic.xqrunner.xdbc.XdbcProvider";

	private static Map providers = null;

	private XQProvider provider;

	public XQRunnerFactory (String providerName)
	{
		initProvidersIfNeeded();

		provider = (XQProvider) providers.get (providerName);
	}

	private static synchronized void initProvidersIfNeeded()
	{
		if (providers == null) {
			return;
		}

		providers = new HashMap();

		Properties properties = System.getProperties();

		for (Iterator it = properties.keySet().iterator(); it.hasNext();) {
			String key = (String) it.next ();
			String value = properties.getProperty (key);

			if (key.startsWith (PROPERTY_PREFIX)) {
				addProvider (providers, key.substring (PROPERTY_PREFIX.length()), value);
			}
		}

		if (providers.size() == 0) {
			addProvider (providers, DEFAULT_PROVIDER_NAME, DEFAULT_PROVIDER_CLASS);
		}
	}

	private static void addProvider (Map map, String key, String className)
	{
		try {
			Class clazz = Class.forName (className);
			XQProvider provider = (XQProvider) clazz.newInstance();

			map.put (key.substring (PROPERTY_PREFIX.length()), provider);
		} catch (Exception e) {
			// problem, do nothing
		}
	}

	public XQDataSource newDataSource (String host, int port, String user, String password)
		throws XQException
	{
		return (provider.newDataSource (host, port, user, password));
	}

	public XQDataSource newDataSource (String key, String user, String password)
		throws XQException
	{
		return (provider.newDataSource (key, user, password));
	}

	public XQRunner newSyncRunner (XQDataSource dataSource)
	{
		return (provider.newSyncRunner (dataSource));
	}
}
