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

import junit.framework.TestCase;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: Sep 29, 2004
 * Time: 11:28:37 PM
 */
public class TestFactory extends TestCase
{
	String host = null;
	int port = 0;
	String user = null;
	String password = null;

	protected void setUp () throws Exception
	{
		super.setUp ();

		host = System.getProperty ("xqhost");
		port = Integer.parseInt (System.getProperty ("xqport"));
		user = System.getProperty ("xquser");
		password = System.getProperty ("xqpw");
	}

	public void testProvider() throws XQException
	{
		new XQFactory (XQFactory.XDBC_PROVIDER_NAME);

		try {
			new XQFactory ("dfsghr");
			fail ("found provider for bogus provider");
		} catch (UnsupportedOperationException e) {
			// expected result
		}

		String sysProp = XQFactory.PROPERTY_PREFIX + "xyzzy";
		System.setProperty (sysProp, XQFactory.XDBC_PROVIDER_CLASS);

		XQFactory factory = new XQFactory ("xyzzy");

		assertEquals ("xyzzy", factory.providerName());

		try {
			new XQFactory ("dgfhdghdfgh");
			fail ("found provider for bogus provider");
		} catch (UnsupportedOperationException e) {
			// expected result
		}


		System.getProperties().remove (sysProp);

		factory = new XQFactory (XQFactory.XDBC_PROVIDER_NAME);

		assertEquals (XQFactory.XDBC_PROVIDER_NAME, factory.providerName());

		try {
			new XQFactory ("xyzzy");
			fail ("found provider for bogus provider");
		} catch (UnsupportedOperationException e) {
			// expected result
		}

		factory = new XQFactory();

		assertEquals (XQFactory.DEFAULT_PROVIDER_NAME, factory.providerName());
	}

	private void checkBadUri (String rawUri) throws URISyntaxException
	{
		URI uri = new URI (rawUri);

		try {
			XQFactory.newDataSource (uri);
			fail ("Expected execption here");
		} catch (XQException e) {
			// expected result
		}

	}

	public void testURI() throws XQException, URISyntaxException
	{
		String scheme = XQFactory.DEFAULT_PROVIDER_NAME;

		checkBadUri (host + ":" + port);

		checkBadUri (scheme + "://" + user + "@" + host + ":" + port);

		checkBadUri (scheme + "://" + ":" + password + "@" + host + ":" + port);

		checkBadUri ("xyzzy://" + ":" + password + "@" + host + ":" + port);

		XQFactory.newDataSource (new URI (scheme + "://" + user + ":" + password + "@" + host + ":" + port));

		String sysProp = XQFactory.PROPERTY_PREFIX + "xyzzy";
		System.setProperty (sysProp, XQFactory.XDBC_PROVIDER_CLASS);

		XQFactory.newDataSource (new URI ("xyzzy://" + user + ":" + password + "@" + host + ":" + port));
	}
}
