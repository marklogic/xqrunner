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

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: Nov 4, 2004
 * Time: 12:57:47 AM
 */
public class TestVarType extends TestCase
{
	public void testForType()
	{
		assertSame (XQVariableType.XS_STRING, XQVariableType.forType ("xs:string"));
		assertSame (XQVariableType.XS_INTEGER, XQVariableType.forType ("xs:integer"));
		assertSame (XQVariableType.BINARY, XQVariableType.forType ("binary()"));
		assertSame (XQVariableType.NODE, XQVariableType.forType ("node()"));
		assertSame (XQVariableType.TEXT, XQVariableType.forType ("text()"));
		assertSame (XQVariableType.NULL, XQVariableType.forType ("--null--"));

		assertNotSame (XQVariableType.XS_STRING, XQVariableType.forType ("xs:integer"));

		assertNull (XQVariableType.forType ("gobbledegook"));
	}
}
