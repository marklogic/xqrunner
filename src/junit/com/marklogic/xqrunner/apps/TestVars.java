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
package com.marklogic.xqrunner.apps;

import junit.framework.TestCase;
import com.marklogic.xqrunner.XQDataSource;
import com.marklogic.xqrunner.XQFactory;
import com.marklogic.xqrunner.XQVariable;
import com.marklogic.xqrunner.XQVariableType;

import java.util.regex.Matcher;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: Dec 16, 2004
 * Time: 5:04:20 PM
 */
public class TestVars extends TestCase
{
	XQDataSource dataSource = null;
//	XQRunner runner = null;

	protected void setUp () throws Exception
	{
		super.setUp ();

		XQFactory factory = new XQFactory();

		String host = System.getProperty ("xqhost");
		int port = Integer.parseInt (System.getProperty ("xqport"));
		String user = System.getProperty ("xquser");
		String password = System.getProperty ("xqpw");

		dataSource = factory.newDataSource (host, port, user, password);
//		runner = dataSource.newSyncRunner();
	}

	public void testVars() throws Exception
	{
		checkRegex ("foo=123", XQVariableType.XS_UNTYPED_ATOMIC, null, "foo", "123");
		checkRegex ("blah:foo=123", XQVariableType.XS_UNTYPED_ATOMIC, "blah", "foo", "123");
		checkRegex ("(xs:string)blah:foo=123", XQVariableType.XS_STRING, "blah", "foo", "123");
		checkRegex ("blah:foo=123", XQVariableType.XS_UNTYPED_ATOMIC, "blah", "foo", "123");
		checkRegex ("(xs:anyURI)blah:foo=http://foo.com/blah", XQVariableType.XS_ANY_URI, "blah",
			"foo", "http://foo.com/blah");
	}

	public void checkRegex (String arg, XQVariableType type, String ns, String ln, String val)
	{
		Matcher matcher = XQRun.varPattern.matcher (arg);

		assertTrue (matcher.matches());
		if (matcher.group (2) != null) {
			assertSame ("var type", type, XQVariableType.forType (matcher.group (2)));
		}
		assertEquals ("var namespace", ns, matcher.group (4));
		assertEquals ("var localname", ln, matcher.group (5));
		assertEquals ("value", val, matcher.group (6));

		XQVariable var = XQRun.parseVariable (dataSource, arg);

		assertNotNull (var);
		assertSame ("var parsed type", type, var.getType());
		assertEquals ("var parsed namespace", (ns == null) ? "" : ns, var.getNamespace());
		assertEquals ("var parsed localname", ln, var.getLocalname());
		assertEquals ("var parsed value", val, var.getValue());
	}
}
