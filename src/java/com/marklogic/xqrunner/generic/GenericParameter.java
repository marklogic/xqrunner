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

import com.marklogic.xqrunner.XQParameter;
import com.marklogic.xqrunner.XQParameterType;

import java.math.BigInteger;
import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: Sep 30, 2004
 * Time: 4:32:41 PM
 */
public class GenericParameter implements XQParameter
{
	private final String namespace;
	private final String localname;
	private final XQParameterType type;
	private final Object value;

	public GenericParameter (String namespace, String localname, XQParameterType type, Object value)
	{
		this.namespace = namespace;
		this.localname = localname;
		this.type = type;
		this.value = value;
	}

	public GenericParameter (String namespace, String localname, XQParameterType type, long value)
	{
		this (namespace, localname, type, new BigInteger ("" + value));
	}

	public GenericParameter (String namespace, String localname, XQParameterType type, double value)
	{
		this (namespace, localname, type, new BigDecimal (value));
	}

	public String getNamespace ()
	{
		return (namespace);
	}

	public String getLocalname ()
	{
		return (localname);
	}

	public XQParameterType getType ()
	{
		return (type);
	}

	public Object getValue ()
	{
		return (value);
	}

	public String toString ()
	{
		return (((namespace == null) ? "" : (namespace + ":")) + localname
			+ ", type=" + type + ", value=" + value + "("
			+ value.getClass().getName() + ")");
	}
}
