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

import com.marklogic.xqrunner.XQDataSource;
import com.marklogic.xqrunner.XQFactory;
import com.marklogic.xqrunner.generic.GenericDocumentWrapper;

import java.net.URI;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileReader;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: Dec 20, 2004
 * Time: 5:07:30 PM
 */
public class AppHelper
{
	static XQDataSource createDataSource (Args args)
		throws Exception
	{
		// -s filename-containing-server-uri
		String uri = args.findAndConsumeNamedArg ("-s");

		if (uri == null) {
			uri = args.consumeArg();
		} else {
			uri = loadStringFromFile (uri, "");
		}

		if (uri == null) {
			throw new Exception ("No server URI provided");
		}

		URI serverUri = new URI (uri);

		return XQFactory.newDataSource (serverUri);
	}

	static String loadStringFromFile (String path, String sep)
		throws IOException
	{
		if (path == null) {
			return (null);
		}

		BufferedReader reader;

		if (path.equals ("-")) {
			reader = new BufferedReader (new InputStreamReader (System.in));
		} else{
			reader = new BufferedReader (new FileReader (path));
		}

		StringBuffer sb = new StringBuffer();
		String line = null;

		while ((line = reader.readLine()) != null) {
			sb.append (line).append (sep) ;
		}

		reader.close();

		return (sb.toString());
	}
}
