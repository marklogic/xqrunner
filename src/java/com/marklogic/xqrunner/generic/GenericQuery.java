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

import com.marklogic.xqrunner.XQuery;
import com.marklogic.xqrunner.XQParameter;
import com.marklogic.xqrunner.XQParameterType;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: Aug 1, 2004
 * Time: 4:41:44 PM
 */
public class GenericQuery implements XQuery
{
	private String text;
	private int timeout = -1;
	private List params = Collections.synchronizedList (new ArrayList());

	public GenericQuery (String text)
	{
		this.text = text;
	}

	public String asString()
	{
		return (text);
	}

	public String getBody()
	{
		return (text);
	}

	public void clearParameters()
	{
		params.clear();
	}

	public void addParameter (XQParameter parameter)
	{
		params.add (parameter);
	}

	public void addParameter (String nameSpace, String localName, XQParameterType type, Object value)
	{
		addParameter (new GenericParameter (nameSpace, localName, type, value));
	}

	public void addParameter (String localName, XQParameterType type, Object value)
	{
		addParameter (null, localName, type, value);
	}

	public XQParameter [] getParameters()
	{
		XQParameter [] paramArray;

		synchronized (params) {
			paramArray = new XQParameter [params.size()];

			params.toArray (paramArray);
		}

		return (paramArray);
	}

	public void setTimeout (int seconds)
	{
		timeout = seconds;
	}

	public int getTimeout()
	{
		return (timeout);
	}
}
