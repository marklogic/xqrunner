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

import java.net.URI;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p>Use this class to obtain instances of XQDataSource.
 * Each instance of this class is bound to a specific provider (XDBC, XQJ, etc).</p>
 *
 * <p>The no-arg constructor creates an instance that uses the default provider.
 * If the system property "com.marklogic.xqrunner.spi.XQProvider" is set
 * its value is the fully qualified name of an XQProvider implementation class to
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

	public static final String XCC_PROVIDER_NAME = "xcc";
	public static final String XCC_PROVIDER_CLASS =
		"com.marklogic.xqrunner.xcc.XccProvider";

	public static final String XDBC_PROVIDER_NAME = "xdbc";
	public static final String XDBC_PROVIDER_CLASS =
		"com.marklogic.xqrunner.xdbc.XdbcProvider";

	public static final String DEFAULT_PROVIDER_NAME = XCC_PROVIDER_NAME;
	public static final String DEFAULT_PROVIDER_CLASS = XCC_PROVIDER_CLASS;

	private static final Map knownProvidersMap;

	private XQProvider provider = null;

	// -----------------------------------------------------------------

	/**
	 * <p>
	 * Construct a factory by searching the list of default providers.
	 * This method first attempts to load the class the class named by
	 * the system propery "com.marklogic.xqrunner.spi.XQProvider".  If
	 * that class if not found, then the list of known providers is tried
	 * until one is successfully loaded.
	 * </p>
	 * <p>
	 * In practice, this means that you can place either the XCC jar or
	 * the XDBC jars in the classpath and XQRunner will use whichever
	 * on it finds first.  Currently, XCC will take precedence over XDBC.
	 * </p>
	 *
	 * @throws XQException Thrown if the an XQProvider implementation
	 *  cannot be instantiated.
	 */
	public XQFactory() throws XQException
	{
		String className = System.getProperty (XQProvider.class.getName());

		if (className != null) {
			try {
				provider = loadProvider (className);
				return;
			} catch (XQException e) {
				// didn't find it, carry on
			}
		}

		XQException ex = null;

		// Go with the first loadable provider class we find
		for (Iterator it = knownProvidersMap.keySet().iterator(); it.hasNext();) {
			String providerName = (String) it.next();

			// undocumented means to change class names for defined providers
			className = System.getProperty (PROPERTY_PREFIX + providerName);

			if (className == null) {
				className = (String) knownProvidersMap.get (providerName);
			}

			try {
				provider = loadProvider (className);

				return;
			} catch (XQException e) {
				ex = e;
			}
		}

		throw ex;
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
			className = (String) knownProvidersMap.get (providerName);
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

	// -----------------------------------------------------------------

	/**
	 * Return an XQDataSource instance as described by the provided URI.
	 * The form of the URI is provider://user:password@serverhost:port.  There
	 * is no default provider, one must be named explicitly.  The XDBC
	 * provider ("xdbc") is guaranteed to always be configured.<br>
	 * Examples:<br>
	 *    xdbc://admin:secret@localhost:8003<br>
	 *    xdbc://joe:hush@somehost.somewhere.com:9032
	 * @param serverUri A URI which identifies the XQProvider, user,
	 *  password, host and port to which the connection should be made.
	 * @return An XQDataSource instance
	 * @throws XQException If the named provider is not configured or
	 *  there is some other problem with the URI.
	 */
	public static XQDataSource newDataSource (URI serverUri)
		throws XQException
	{
		String scheme = serverUri.getScheme();
		XQFactory factory = null;

		try {
			factory = new XQFactory (scheme);
		} catch (XQException e) {
			throw e;

		} catch (Exception e) {
			throw new XQException ("Cannot create XQFactory: " + e);
		}

		String userInfoStr = serverUri.getUserInfo();
		String [] userInfo = (userInfoStr == null) ? (new String [0]) : userInfoStr.split (":");

		if ((userInfo.length != 2) || (userInfo [0].length() == 0) || (userInfo [1].length() == 0)) {
			throw new XQException ("Expected user:password, found: " + userInfoStr);
		}

		return (factory.newDataSource (serverUri.getHost(), serverUri.getPort(),
			userInfo [0], userInfo [1]));
	}

	// -----------------------------------------------------------------

	private XQProvider loadProvider (String className)
		throws XQException
	{
		try {
			Class clazz = Class.forName (className);

			return ((XQProvider) clazz.newInstance());
		} catch (Exception e) {
			throw new XQException ("Could not load provider '" + className + "': " + e, e);
		}
	}

	static {
		knownProvidersMap = new LinkedHashMap ();

		// provider classes will be tried in this order
		knownProvidersMap.put (XCC_PROVIDER_NAME, XCC_PROVIDER_CLASS);
		knownProvidersMap.put (XDBC_PROVIDER_NAME, XDBC_PROVIDER_CLASS);
	}
}
