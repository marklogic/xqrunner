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

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: Sep 30, 2004
 * Time: 4:13:01 PM
 */
public class XQParameterType
{
	private String displayName;

	private XQParameterType (String displayName)
	{
		this.displayName = displayName;
	}

	public String toString ()
	{
		return (displayName);
	}

	public static final XQParameterType XS_UNTYPED_ATOMIC = new XQParameterType ("xs:untypedAtomic");
	public static final XQParameterType XS_STRING = new XQParameterType ("xs:string");
	public static final XQParameterType XS_BOOLEAN = new XQParameterType ("xs:boolean");
	public static final XQParameterType XS_INTEGER = new XQParameterType ("xs:integer");
	public static final XQParameterType XS_DECIMAL = new XQParameterType ("xs:decimal");
	public static final XQParameterType XS_FLOAT = new XQParameterType ("xs:float");
	public static final XQParameterType XS_DOUBLE = new XQParameterType ("xs:double");
	public static final XQParameterType XS_DURATION = new XQParameterType ("xs:duration");
	public static final XQParameterType XS_DATE_TIME = new XQParameterType ("xs:dateTime");
	public static final XQParameterType XS_TIME = new XQParameterType ("xs:time");
	public static final XQParameterType XS_DATE = new XQParameterType ("xs:date");
	public static final XQParameterType XS_ANY_URI = new XQParameterType ("xs:anyURI");
	public static final XQParameterType XS_QNAME = new XQParameterType ("xs:QName");
	public static final XQParameterType NULL = new XQParameterType ("--null--");
	// TODO: complete this list
}
