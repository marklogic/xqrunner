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
package com.marklogic.xqrunner.generic;

import junit.framework.TestCase;
import com.marklogic.xqrunner.XQVariableType;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: Oct 18, 2004
 * Time: 11:11:21 AM
 */
public class TestQuery extends TestCase
{
	public void testToStringSimple()
	{
		GenericQuery query = new GenericQuery ("this is the query");

		assertEquals ("this is the query", query.toString ());
	}

	public void testToStringVars()
	{
		GenericQuery query = new GenericQuery ("this is the query");

		query.setVariable (new GenericVariable ("foo", false));
		query.setVariable (new GenericVariable ("bar", true));

		assertEquals ("(: Variables :)\n" +
			"foo, type=xs:boolean, value=false(java.lang.Boolean)\n" +
			"bar, type=xs:boolean, value=true(java.lang.Boolean)\n" +
			"(: Body :)\n" +
			"this is the query", query.toString ());
	}

	public void testToStringVars2()
	{
		GenericQuery query = new GenericQuery ("this is the query");

		query.setVariable (new GenericVariable ("ns1", "foo", false));
		query.setVariable (new GenericVariable ("ns2", "bar", true));

		assertEquals ("(: Variables :)\n" +
			"ns1:foo, type=xs:boolean, value=false(java.lang.Boolean)\n" +
			"ns2:bar, type=xs:boolean, value=true(java.lang.Boolean)\n" +
			"(: Body :)\n" +
			"this is the query", query.toString ());
	}

	public void testToStringVars3()
	{
		GenericQuery query = new GenericQuery ("this is the query");

		query.setVariable (new GenericVariable ("bar", XQVariableType.XS_ANY_URI, "adsfasdfasdf"));
		query.setVariable (new GenericVariable ("foo", XQVariableType.XS_STRING, "blah"));
		query.setVariable (new GenericVariable ("foo", false));
		query.setVariable (new GenericVariable ("bar", true));

		assertEquals ("(: Variables :)\n" +
			"foo, type=xs:boolean, value=false(java.lang.Boolean)\n" +
			"bar, type=xs:boolean, value=true(java.lang.Boolean)\n" +
			"(: Body :)\n" +
			"this is the query", query.toString ());
	}
}
