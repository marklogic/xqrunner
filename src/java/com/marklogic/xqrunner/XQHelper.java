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

import org.w3c.dom.Document;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: Oct 18, 2004
 * Time: 11:07:30 PM
 */
public class XQHelper
{
	private XQHelper()
	{
		// no no no
	}

	public static String executeString (XQRunner runner, XQuery query, String separator)
		throws XQException
	{
		XQResult result = runner.runQuery (query);

		return (result.asString (separator));
	}

	public static String executeString (XQRunner runner, XQuery query)
		throws XQException
	{
		return (executeString (runner, query, ""));
	}

	public static String [] executeStrings (XQRunner runner, XQuery query)
		throws XQException
	{
		XQResult result = runner.runQuery (query);
		String [] strings = new String [result.getSize()];

		for (int i = 0; i < strings.length; i++) {
			strings [i] = result.getItem (i).asString();
		}

		return (strings);
	}

	public static Object executeAtomic (XQRunner runner, XQuery query)
		throws XQException
	{
		XQResultItem item = getAtomic0 (runner, query);

		return (item.asObject());
	}

	public static Object executeObject (XQRunner runner, XQuery query)
		throws XQException
	{
		XQResultItem item = getItem0 (runner, query);

		return (item.asObject());
	}

	public static Object [] executeObjects (XQRunner runner, XQuery query)
		throws XQException
	{
		XQResult result = runner.runQuery (query);
		Object [] objects = new Object [result.getSize()];

		for (int i = 0; i < objects.length; i++) {
			objects [i] = result.getItem (i).asObject();
		}

		return (objects);
	}

	public static boolean executeBoolean (XQRunner runner, XQuery query)
		throws XQException
	{
		XQResultItem item = getTypedItem0 (runner, query, XQVariableType.XS_BOOLEAN);

		return (((Boolean) item.asObject()).booleanValue());
	}

	public static int executeInteger (XQRunner runner, XQuery query)
		throws XQException
	{
		XQResultItem item = getTypedItem0 (runner, query, XQVariableType.XS_INTEGER);

		return (((BigInteger) item.asObject()).intValue());
	}

	public static float executeFloat (XQRunner runner, XQuery query)
		throws XQException
	{
		XQResultItem item = getTypedItem0 (runner, query, XQVariableType.XS_FLOAT);

		return (((Float) item.asObject()).floatValue());
	}

	public static double executeDouble (XQRunner runner, XQuery query)
		throws XQException
	{
		XQResultItem item = getTypedItem0 (runner, query, XQVariableType.XS_DOUBLE);

		return (((Double) item.asObject()).doubleValue());
	}

	public static double executeDecimal (XQRunner runner, XQuery query)
		throws XQException
	{
		XQResultItem item = getTypedItem0 (runner, query, XQVariableType.XS_DECIMAL);

		return (((BigDecimal) item.asObject()).doubleValue());
	}

	public static org.jdom.Document executeJDom (XQRunner runner, XQuery query)
		throws XQException
	{
		XQResultItem item = getNode0 (runner, query);

		return (item.asJDom());
	}

	public static Document executeW3CDom (XQRunner runner, XQuery query)
		throws XQException
	{
		XQResultItem item = getNode0 (runner, query);

		return (item.asW3cDom());
	}

	// ----------------------------------------------------------------

	private static XQResultItem getNode0 (XQRunner runner, XQuery query)
		throws XQException
	{
		XQResultItem item = getItem0 (runner, query);

		if ( ! item.isNode()) {
			throw new XQException ("Result is not a node (" + item.getType() + ")");
		}

		return item;
	}

	private static XQResultItem getAtomic0 (XQRunner runner, XQuery query)
		throws XQException
	{
		XQResultItem item = getItem0 (runner, query);

		if (item.isNode()) {
			throw new XQException ("Result is not atomic (" + item.getType() + ")");
		}

		return item;
	}

	private static XQResultItem getItem0 (XQRunner runner, XQuery query)
		throws XQException
	{
		XQResult result = runner.runQuery (query);

		if (result.getSize() < 1) {
			throw new XQException ("Empty result sequence");
		}

		return (result.getItem (0));
	}

	private static XQResultItem getTypedItem0 (XQRunner runner, XQuery query,
		XQVariableType expected)
		throws XQException
	{
		XQResultItem item = getItem0 (runner, query);
		XQVariableType actual = item.getType();

		if (actual != expected) {
			throw new XQException ("Item is not of expected type: expected=" +
				expected + ", actual=" + actual);
		}

		return (item);
	}
}
