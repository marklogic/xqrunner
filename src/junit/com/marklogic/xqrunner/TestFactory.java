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

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: Sep 29, 2004
 * Time: 11:28:37 PM
 */
public class TestFactory extends TestCase
{
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

		new XQFactory ("xyzzy");

		try {
			new XQFactory ("dgfhdghdfgh");
			fail ("found provider for bogus provider");
		} catch (UnsupportedOperationException e) {
			// expected result
		}


		System.getProperties().remove (sysProp);

		new XQFactory (XQFactory.XDBC_PROVIDER_NAME);

		try {
			new XQFactory ("xyzzy");
			fail ("found provider for bogus provider");
		} catch (UnsupportedOperationException e) {
			// expected result
		}
	}
}
