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

import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: Dec 17, 2004
 * Time: 12:30:33 PM
 */
public class TestArgs extends TestCase
{
	public void testGeneral()
	{
		Args args = new Args (new String [] { "foo", "bar" });

		assertEquals (2, args.size());
		assertEquals (2, args.remaining());
		assertTrue (args.hasNext ());
		assertEquals ("foo", args.nextArg());
		assertEquals (1, args.remaining());
		assertEquals ("bar", args.peekNextArg());
		assertEquals ("bar", args.nextArg());
		assertFalse (args.hasNext());
		assertEquals (0, args.remaining());
		assertNull (args.nextArg());
	}

	public void testRewind()
	{
		Args args = new Args (new String [] { "foo", "bar", "bop" });

		assertEquals ("foo", args.nextArg());
		assertEquals ("bar", args.nextArg());
		assertEquals ("bop", args.nextArg());
		assertNull (args.nextArg());

		args.rewind();
		assertEquals ("foo", args.nextArg());
		assertEquals ("bar", args.nextArg());
		assertEquals ("bop", args.nextArg());
		assertNull (args.nextArg());
	}

	public void testGetArg()
	{
		Args args = new Args (new String [] { "foo", "bar", "bop" });

		assertEquals ("bar", args.getArg (1));
		assertEquals ("foo", args.getArg (0));
		assertEquals ("bop", args.getArg (2));

		assertEquals (3, args.remaining());
		assertEquals ("foo", args.nextArg());

		assertEquals ("bar", args.getArg (1));
		assertEquals ("foo", args.getArg (0));
		assertEquals ("bop", args.getArg (2));
		assertEquals (2, args.remaining());

		try {
			args.getArg (3);
			fail ("Expected IllegalArgumentException");
		} catch (Exception e) {
			// good result
		}
		try {
			args.getArg (10000);
			fail ("Expected IllegalArgumentException");
		} catch (Exception e) {
			// good result
		}
		try {
			args.getArg (10000);
			fail ("Expected IllegalArgumentException");
		} catch (Exception e) {
			// good result
		}
	}

	public void testPeek()
	{
		Args args = new Args (new String [] { "foo", "bar", "bop" });

		assertEquals ("foo", args.peekNextArg());
		assertEquals ("foo", args.nextArg());

		assertEquals ("bar", args.peekNextArg());
		assertEquals ("bar", args.nextArg());

		assertEquals ("bop", args.peekNextArg());
		assertEquals ("bop", args.nextArg());

		assertNull (args.peekNextArg());
		assertNull (args.nextArg());
	}

	public void testHasArg()
	{
		Args args = new Args (new String [] { "foo", "bar", "bop" });

		assertTrue (args.hasArg (0));
		assertTrue (args.hasArg (1));
		assertTrue (args.hasArg (2));

		assertFalse (args.hasArg (4));
		assertFalse (args.hasArg (10000));
		assertFalse (args.hasArg (-1));
	}

	public void testEmpty()
	{
		Args args = new Args (new String [0]);

		assertEquals (0, args.size());
		assertFalse (args.hasNext());
		assertFalse (args.hasArg (0));
		assertFalse (args.hasArg (1));
		assertFalse (args.hasArg (-1));
		assertNull (args.peekNextArg());
		assertNull (args.nextArg());
	}

	public void testStepBack()
	{
		Args args = new Args (new String [] { "foo", "bar", "bop" });

		assertEquals (3, args.size());

		args.stepBack();
		assertEquals (3, args.remaining());
		assertEquals ("foo", args.peekNextArg());

		assertEquals ("foo", args.nextArg());
		assertEquals (2, args.remaining());
		assertEquals ("bar", args.peekNextArg());

		assertEquals ("bar", args.nextArg());
		assertEquals (1, args.remaining());
		assertEquals ("bop", args.peekNextArg());

		args.stepBack();
		assertEquals ("bar", args.nextArg());

		args.stepBack();
		args.stepBack();
		assertEquals ("foo", args.peekNextArg());
		assertEquals (3, args.remaining());
	}

	public void testConsume()
	{
		Args args = new Args (new String [] { "foo", "bar", "bop" });

		assertEquals (3, args.size());
		assertEquals ("foo", args.consumeArg());
		assertEquals (2, args.size());
		assertEquals ("bar", args.nextArg());
		assertEquals ("bop", args.consumeArg());
		assertEquals ("bar", args.peekNextArg());
		assertEquals (1, args.size());
		assertEquals ("bar", args.consumeArg());
		assertNull (args.consumeArg());
	}

	public void testConsumeIndex()
	{
		Args args = new Args (new String [] { "foo", "bar", "bop" });

		assertEquals (3, args.size());
		assertEquals ("bar", args.consumeArg (1));
		assertEquals (2, args.size());
		assertEquals (2, args.remaining());
		assertEquals ("foo", args.peekNextArg());
		assertEquals ("foo", args.consumeArg (0));
		assertEquals (1, args.size());
		assertEquals ("bop", args.nextArg());
		assertEquals (0, args.remaining());
		assertNull (args.nextArg());
	}

	public void testPushBack()
	{
		Args args = new Args (new String [] { "foo", "bar", "bop" });

		assertEquals (3, args.size());
		String arg = args.consumeArg();
		assertEquals ("foo", arg);
		assertEquals (2, args.size());
		assertEquals (2, args.remaining());
		args.pushBack (arg);
		assertEquals ("foo", args.nextArg());
		assertEquals ("bar", args.nextArg());
		assertEquals ("bop", args.nextArg());
		assertNull (args.nextArg());
	}

	public void testPushBackIndex()
	{
		Args args = new Args (new String [] { "foo", "bar", "bop" });

		assertEquals (3, args.size());
		assertEquals ("bar", args.consumeArg (1));
		assertEquals (2, args.size());
		args.pushBack ("bar", 1);
		assertEquals ("foo", args.nextArg());
		assertEquals ("bar", args.nextArg());
		assertEquals ("bop", args.nextArg());
		assertNull (args.nextArg());
	}

	public void testExtractArg()
	{
		Args args = new Args (new String [] { "foo", "-phat", "bar", "-f", "filename", "-phat", "bop", "-x" });

		assertEquals (8, args.size());
		assertNull (args.findAndConsumeNamedArg ("-q"));
		assertEquals (8, args.size());

		assertEquals ("hat", args.findAndConsumeNamedArg ("-p"));
		assertEquals (7, args.size());
		assertEquals ("foo", args.getArg (0));
		assertEquals ("bar", args.getArg (1));

		assertEquals ("-f", args.getArg (2));
		assertEquals ("filename", args.getArg (3));
		assertEquals ("filename", args.findAndConsumeNamedArg ("-f"));
		assertEquals (5, args.size());
		assertEquals ("bar", args.getArg (1));
		assertEquals ("-phat", args.getArg (2));

		assertNull (args.findAndConsumeNamedArg ("-x"));
		assertEquals (4, args.size());
	}
}
