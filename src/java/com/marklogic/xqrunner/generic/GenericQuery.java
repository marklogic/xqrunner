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

import com.marklogic.xqrunner.XQVariable;
import com.marklogic.xqrunner.XQuery;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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
	private Set variables = Collections.synchronizedSet (new HashSet());

	public GenericQuery (String text)
	{
		this.text = text;
	}

	public String getBody()
	{
		return (text);
	}

	public void clearVariables()
	{
		variables.clear();
	}

	public void addVariable (XQVariable variable)
	{
		variables.add (variable);
	}

	public XQVariable [] getVariables()
	{
		XQVariable [] variableArray;

		synchronized (variables) {
			variableArray = new XQVariable [variables.size()];

			variables.toArray (variableArray);
		}

		return (variableArray);
	}

	public void setTimeout (int seconds)
	{
		timeout = seconds;
	}

	public int getTimeout()
	{
		return (timeout);
	}

	public String toString ()
	{
		StringBuffer sb = new StringBuffer();

		synchronized (variables) {
			if (variables.size() > 0) {
				sb.append ("(: Variables :)\n");

				for (Iterator it = variables.iterator (); it.hasNext ();) {
					XQVariable variable = (XQVariable) it.next ();

					sb.append (variable.toString ()).append ("\n");
				}

				sb.append ("(: Body :)\n");
			}
		}

		sb.append (text);

		return (sb.toString());
	}
}
