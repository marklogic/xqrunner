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
public class XQVariableType
{
	private String displayName;

	private XQVariableType (String displayName)
	{
		this.displayName = displayName;
	}

	public String toString ()
	{
		return (displayName);
	}

	public static final XQVariableType NULL = new XQVariableType ("--null--");
	public static final XQVariableType XS_UNTYPED_ATOMIC = new XQVariableType ("xs:untypedAtomic");
	public static final XQVariableType XS_STRING = new XQVariableType ("xs:string");
	public static final XQVariableType XS_BOOLEAN = new XQVariableType ("xs:boolean");
	public static final XQVariableType XS_INTEGER = new XQVariableType ("xs:integer");
	public static final XQVariableType XS_DECIMAL = new XQVariableType ("xs:decimal");
	public static final XQVariableType XS_FLOAT = new XQVariableType ("xs:float");
	public static final XQVariableType XS_DOUBLE = new XQVariableType ("xs:double");
	public static final XQVariableType XS_DURATION = new XQVariableType ("xs:duration");
	public static final XQVariableType XS_DAYTIMEDURATION = new XQVariableType ("xdt:dayTimeDuration");
	public static final XQVariableType XS_YEARMONTHDURATION = new XQVariableType ("xdt:yearMonthDuration");
	public static final XQVariableType XS_DATE_TIME = new XQVariableType ("xs:dateTime");
	public static final XQVariableType XS_TIME = new XQVariableType ("xs:time");
	public static final XQVariableType XS_DATE = new XQVariableType ("xs:date");
	public static final XQVariableType XS_ANY_URI = new XQVariableType ("xs:anyURI");
	public static final XQVariableType XS_QNAME = new XQVariableType ("xs:QName");
	public static final XQVariableType XS_GDAY = new XQVariableType ("xs:gDay");
	public static final XQVariableType XS_GMONTH = new XQVariableType ("xs:gMonth");
	public static final XQVariableType XS_GMONTHDAY = new XQVariableType ("xs:gMonthDay");
	public static final XQVariableType XS_GYEAR = new XQVariableType ("xs:gYear");
	public static final XQVariableType XS_GYEARMONTH = new XQVariableType ("xs:gYearMonth");
	public static final XQVariableType XS_HEXBINARY = new XQVariableType ("xs:hexBinary");
	public static final XQVariableType XS_BASE64BINARY = new XQVariableType ("xs:base64Binary");
}
