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

import junit.framework.TestCase;
import com.marklogic.xqrunner.XQVariable;
import com.marklogic.xqrunner.XQVariableType;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: Oct 14, 2004
 * Time: 2:32:42 PM
 */
public class TestVariable extends TestCase
{
	public void testEqualsSimple()
	{
		XQVariable v1 = new GenericVariable ("ns", "local", true);
		XQVariable v2 = new GenericVariable ("ns", "local", true);

		assertNotSame (v1, v2);

		assertEquals (v1, v2);
	}

	public void testEqualsSimpleNoNS()
	{
		XQVariable v1 = new GenericVariable ("local", true);
		XQVariable v2 = new GenericVariable ("local", true);

		assertNotSame (v1, v2);

		assertEquals (v1, v2);
	}

	public void testEqualsSimpleEmptyNS()
	{
		XQVariable v1 = new GenericVariable ("", "local", true);
		XQVariable v2 = new GenericVariable ("", "local", true);

		assertNotSame (v1, v2);

		assertEquals (v1, v2);
	}

	public void testEqualsSimpleMixedNS()
	{
		XQVariable v1 = new GenericVariable ("", "local", true);
		XQVariable v2 = new GenericVariable ("local", true);

		assertNotSame (v1, v2);

		assertEquals (v1, v2);
	}

	public void testEqualsDifferentValues()
	{
		XQVariable v1 = new GenericVariable ("ns", "local", true);
		XQVariable v2 = new GenericVariable ("ns", "local", false);

		assertNotSame (v1, v2);

		assertFalse (v1.equals (v2));
	}

	public void testEqualsDifferentTypes()
	{
		XQVariable v1 = new GenericVariable ("ns", "local", XQVariableType.XS_STRING, "foobar");
		XQVariable v2 = new GenericVariable ("ns", "local", XQVariableType.XS_UNTYPED_ATOMIC, "foobar");

		assertNotSame (v1, v2);

		assertFalse (v1.equals (v2));
	}

	public void testHashSimple()
	{
		XQVariable v1 = new GenericVariable ("ns", "local", XQVariableType.XS_STRING, "foobar");
		XQVariable v2 = new GenericVariable ("ns", "local", XQVariableType.XS_UNTYPED_ATOMIC, "foobar");

		assertFalse (v1.equals (v2));

		assertEquals (v1.hashCode(), v2.hashCode());
	}

	public void testHashNoNS()
	{
		XQVariable v1 = new GenericVariable ("local", XQVariableType.XS_STRING, "foobar");
		XQVariable v2 = new GenericVariable ("local", XQVariableType.XS_UNTYPED_ATOMIC, "foobar");

		assertFalse (v1.equals (v2));

		assertEquals (v1.hashCode(), v2.hashCode());
	}

	public void testHashMixedNS()
	{
		XQVariable v1 = new GenericVariable ("local", XQVariableType.XS_STRING, "foobar");
		XQVariable v2 = new GenericVariable ("", "local", XQVariableType.XS_UNTYPED_ATOMIC, "foobar");

		assertFalse (v1.equals (v2));

		assertEquals (v1.hashCode(), v2.hashCode());
	}
}
