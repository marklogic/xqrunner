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
package com.marklogic.xqrunner.xdbc;

import junit.framework.TestCase;
import com.marklogic.xqrunner.XQDataSource;
import com.marklogic.xqrunner.XQRunner;
import com.marklogic.xqrunner.XQFactory;
import com.marklogic.xqrunner.XQResult;
import com.marklogic.xqrunner.XQException;
import com.marklogic.xqrunner.XQuery;
import com.marklogic.xqrunner.XQParameterType;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.math.BigInteger;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: Sep 30, 2004
 * Time: 4:48:54 PM
 */
public class TestParam extends TestCase
{
	XQDataSource dataSource = null;
	XQRunner runner = null;

	protected void setUp () throws Exception
	{
		super.setUp ();

		XQFactory factory = new XQFactory();

		String host = System.getProperty ("xqhost");
		int port = Integer.parseInt (System.getProperty ("xqport"));
		String user = System.getProperty ("xquser");
		String password = System.getProperty ("xqpw");

		dataSource = factory.newDataSource (host, port, user, password);
		runner = factory.newSyncRunner (dataSource);
	}

	public void testStringNoNamespaceParam() throws XQException
	{
		XQuery query = dataSource.newQuery (
			"define variable $extvar external\n" +
			"concat (\"var: \", $extvar)");

		query.addParameter ("extvar", XQParameterType.XS_STRING, "thevalue");

		XQResult result = runner.runQuery (query);

		assertEquals ("var: thevalue", result.asString ());
	}

	public void testStringNullStringNamespaceParam() throws XQException
	{
		XQuery query = dataSource.newQuery (
			"define variable $extvar external\n" +
			"concat (\"var: \", $extvar)");

		query.addParameter ("", "extvar", XQParameterType.XS_STRING, "thevalue");

		XQResult result = runner.runQuery (query);

		assertEquals ("var: thevalue", result.asString ());
	}

	public void testStringWithNamesapceParam() throws XQException
	{
		XQuery query = dataSource.newQuery (
			"declare namespace foo=\"foobar\"\n" +
			"define variable $foo:extvar external\n" +
			"concat (\"var: \", $foo:extvar)");

		query.addParameter ("foobar", "extvar", XQParameterType.XS_STRING, "thevalue");

		XQResult result = runner.runQuery (query);
		assertEquals ("var: thevalue", result.asString ());
	}

	public void testInteger() throws XQException
	{
		XQuery query = dataSource.newQuery (
			"define variable $extvar external\n" +
			"concat (\"var: \", xs:string($extvar))");

		query.addParameter ("extvar", XQParameterType.XS_INTEGER, new BigInteger ("" + 945128));

		XQResult result = runner.runQuery (query);
		assertEquals ("var: 945128", result.asString ());
	}

	public void testDate() throws XQException
	{
		Date date = new Date (1234567890);
		DateFormat format = new SimpleDateFormat ("MMM d, yyyy KK:mma");
		String expected = format.format (date);

		XQuery query = dataSource.newQuery (
			"define variable $datevar external\n" +
			"xdmp:strftime (\"%b %d, %Y %l:%M%p\", $datevar)");

		query.addParameter ("datevar", XQParameterType.XS_DATE_TIME, date);

		XQResult result = runner.runQuery (query);
		assertEquals (expected, result.asString());
	}
}
