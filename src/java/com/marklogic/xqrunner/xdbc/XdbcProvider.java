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

import com.marklogic.xqrunner.XQDataSource;
import com.marklogic.xqrunner.XQException;
import com.marklogic.xqrunner.XQRunner;
import com.marklogic.xqrunner.spi.XQProvider;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: Sep 29, 2004
 * Time: 6:40:12 PM
 */
public class XdbcProvider implements XQProvider
{
	public XQDataSource newDataSource (String host, int port, String user, String password)
		throws XQException
	{
		return (new XdbcDataSource (host, port, user, password));
	}

	public XQDataSource newDataSource (String key, String user, String password)
		throws XQException
	{
		return (new XdbcDataSource (key, user, password));
	}

	public XQRunner newSyncRunner (XQDataSource dataSource)
	{
		return (new XdbcSyncRunner (dataSource));
	}
}
