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

import java.util.List;
import java.util.ArrayList;


/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: Dec 16, 2004
 * Time: 3:06:12 PM
 */
public class Args
{
	private List argv;
	private int index = 0;

	public Args (String [] argvArray)
	{
		argv = new ArrayList (argvArray.length);

		for (int i = 0; i < argvArray.length; i++) {
			argv.add (argvArray [i]);
		}
	}

	public String nextArg()
	{
		String arg = peekNextArg();

		if (arg != null) {
			index++;
		}

		return (arg);
	}

	public String peekNextArg()
	{
		if ( ! hasNext()) {
			return (null);
		}

		return (String) argv.get (index);
	}

	public String getArg (int index)
	{
		if (hasArg (index)) {
			return (String) argv.get (index);
		}

		throw new IllegalArgumentException ("No argument at index=" + index
			+ ", arg count=" + argv.size());
	}

	public void rewind()
	{
		index = 0;
	}

	public boolean hasArg (int index)
	{
		return ((index >= 0) && (index < argv.size()));
	}

	public boolean hasNext()
	{
		return ((this.index >= 0) && (this.index < argv.size()));
	}

	public int size ()
	{
		return (argv.size());
	}

	public int remaining ()
	{
		return (size() - index);
	}

	public void stepBack ()
	{
		if (index != 0) {
			index--;
		}
	}

	public String consumeArg (int index)
	{
		String arg = getArg (index);

		argv.remove (index);

		this.index = Math.min (this.index, argv.size() - 1);

		return (arg);
	}

	public String consumeArg()
	{
		try {
			return (consumeArg (this.index));
		} catch (IllegalArgumentException e) {
			return (null);
		}
	}

	public void pushBack (String arg)
	{
		pushBack (arg, this.index);
	}

	public void pushBack (String arg, int index)
	{
		argv.add (index, arg);
	}
}
