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

import java.io.StringWriter;
import java.io.IOException;
import java.io.ByteArrayOutputStream;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: Nov 11, 2004
 * Time: 2:54:40 PM
 */
public class TestStreams extends TestCase
{
	XQDataSource dataSource = null;
	XQRunner runner = null;

	protected void setUp () throws Exception
	{
		super.setUp ();

		dataSource = TestServerConfig.getDataSource();
		runner = TestServerConfig.getRunner();
	}

	private static final String simpleString = "Simple String";
	private static final String quotedSimpleString = "\"" + simpleString + "\"";

	private static final String testInput = "<b>This is the result of a remote xquery</b>,\n" +
			"\t\t<br/>, \"Congratulations, your connection works\"";
	private static final String testOutput = "<b>This is the result of a remote xquery</b>" +
		"<br/>Congratulations, your connection works";

	public void testStringToWriter() throws XQException, IOException
	{
		XQResult result = runner.runQuery (dataSource.newQuery (quotedSimpleString));
		StringWriter stringWriter = new StringWriter();

		result.writeTo (stringWriter);

		assertEquals (simpleString, stringWriter.toString());
	}

	public void testStringToWriterStreaming() throws XQException, IOException
	{
		XQResult result = runner.runQueryStreaming (dataSource.newQuery (quotedSimpleString));
		StringWriter stringWriter = new StringWriter();

		result.writeTo (stringWriter);

		assertEquals (simpleString, stringWriter.toString());
	}

	public void testBinaryToStream() throws XQException, IOException
	{
		XQResult result = runner.runQueryStreaming (dataSource.newQuery ("xdmp:unquote(\"foo\", \"\", \"format-binary\")/binary()"));
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		result.nextItem().streamTo (outputStream);

		assertEquals ("foo", new String (outputStream.toByteArray()));
	}

	public void testWriteResult() throws XQException, IOException
	{
		XQResult result = runner.runQuery (dataSource.newQuery (testInput));
		StringWriter stringWriter = new StringWriter();

		result.writeTo (stringWriter, null);

		assertEquals (testOutput, stringWriter.toString());
	}

	public void testWriteResultStreaming() throws XQException, IOException
	{
		XQResult result = runner.runQueryStreaming (dataSource.newQuery (testInput));
		StringWriter stringWriter = new StringWriter();

		result.writeTo (stringWriter, null);

		assertEquals (testOutput, stringWriter.toString());
	}
}
