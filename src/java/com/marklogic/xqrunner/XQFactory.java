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

import com.marklogic.xqrunner.spi.XQProvider;

import java.util.Map;
import java.util.HashMap;

/**
 * <p>Use this class to obtain instances of XQDataSource and XQRunner.
 * Each instance of this class is bound to a specific provider (XDBC, XQJ, etc).</p>
 *
 * <p>The no-arg constructor creates an instance that uses the default provider.
 * If the system property "com.marklogic.xqrunner.spi.XQProvider" is set
 * its value is the fully qualified name of an XQProvider implementation to
 * use.  If the property is not set, a compile-time default is used.</p>
 *
 * <p>The constructor that takes a String argument names a provider to use.
 * If a system property named "xqrunner.provider.<i>name</i>" is set its value
 * is the fully qualified name of an XQProvider implementation to use.  If no
 * property is set, the provider name is looked up in a list of compile-time
 * default providers.  If the name is not found in either lookup, an
 * exception is thrown.
 * As of this writing, the only default provider is XDBC.</p>
 *
 * @author Ron Hitchens
 * @see com.marklogic.xqrunner.spi.XQProvider
 */
public class XQFactory
{
	public static final String PROPERTY_PREFIX = "xqrunner.provider.";

	public static final String XDBC_PROVIDER_NAME = "xdbc";
	public static final String XDBC_PROVIDER_CLASS =
		"com.marklogic.xqrunner.xdbc.XdbcProvider";

	public static final String DEFAULT_PROVIDER_NAME = XDBC_PROVIDER_NAME;
	public static final String DEFAULT_PROVIDER_CLASS = XDBC_PROVIDER_CLASS;

	private static Map defaultProviders = null;

	private XQProvider provider = null;

	// -----------------------------------------------------------------

	/**
	 * Construct a factory using the default provider.
	 *
	 * @throws XQException Thrown if the default XQProvider implementation
	 *  cannot be instantiated.
	 */
	public XQFactory()
		throws XQException
	{
		String className = System.getProperty (XQProvider.class.getName());

		if (className == null) {
			className = DEFAULT_PROVIDER_CLASS;
		}

		provider = loadProvider (className);
	}

	/**
	 * Construct a factory for the named provider.
	 *
	 * @param providerName The name of the provider to use for this factory.
	 * @throws XQException Thrown if the default XQProvider implementation
	 *  cannot be instantiated.
	 */
	public XQFactory (String providerName)
		throws XQException
	{
		String className = System.getProperty (PROPERTY_PREFIX + providerName);

		if (className == null) {
			className = (String) defaultProviders.get (providerName);
		}

		if (className == null) {
			throw new UnsupportedOperationException (
				"No provider named '" + providerName + "' configured");
		}

		provider = loadProvider (className);

		provider.setName (providerName);
	}

	// -----------------------------------------------------------------

	public String providerName()
	{
		return (provider.getName());
	}

	public XQDataSource newDataSource (String host, int port,
		String user, String password)
		throws XQException
	{
		return (provider.newDataSource (host, port, user, password));
	}

	public XQDataSource newDataSource (String key, String user, String password)
		throws XQException
	{
		return (provider.newDataSource (key, user, password));
	}

	// -----------------------------------------------------------------

	private XQProvider loadProvider (String className)
		throws XQException
	{
		try {
			Class clazz = Class.forName (className);

			return ((XQProvider) clazz.newInstance());
		} catch (Exception e) {
			throw new XQException (e.getMessage(), e);
		}
	}

	static {
		defaultProviders = new HashMap();

		defaultProviders.put (XDBC_PROVIDER_NAME, XDBC_PROVIDER_CLASS);
	}
}
