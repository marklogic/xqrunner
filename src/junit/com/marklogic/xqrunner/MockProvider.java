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

import com.marklogic.xqrunner.generic.AsyncRunner;
import com.marklogic.xqrunner.generic.GenericQuery;
import com.marklogic.xqrunner.spi.XQProvider;


/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: Oct 4, 2004
 * Time: 3:52:41 PM
 */
public class MockProvider implements XQProvider
{
	private String name = "mock";

	public void setName (String name)
	{
		this.name = name;
	}

	public String getName ()
	{
		return (name);
	}

	public XQDataSource newDataSource (String host, int port, String user,
		String password) throws XQException
	{
		return (new MockDataSource());
	}

	public XQDataSource newDataSource (String key, String user, String password)
		throws XQException
	{
		return (new MockDataSource());
	}

	public XQRunner newSyncRunner (XQDataSource dataSource)
	{
		return (new MockRunner());
	}

	// ---------------------------------------------------------

	private class MockDataSource implements XQDataSource
	{
		public Object getConnection () throws XQException
		{
			return null;
		}

		public Object getConnection (String user, String password) throws XQException
		{
			return null;
		}

		public XQRunner newSyncRunner ()
		{
			return (new MockRunner());
		}

		public XQAsyncRunner newAsyncRunner()
		{
			return (new AsyncRunner (newSyncRunner()));
		}

		public XQAsyncRunner newAsyncRunner (XQRunner runner)
		{
			return (new AsyncRunner (runner));
		}

		public XQuery newQuery (String body)
		{
			return (new GenericQuery (body));
		}

		public XQVariable newVariable (String namespace, String localname, XQVariableType type, Object value)
		{
			return null;
		}

		public XQVariable newVariable (String localname, XQVariableType type, Object value)
		{
			return null;
		}

		public XQVariable newVariable (String namespace, String localname, XQVariableType type, long value)
		{
			return null;
		}

		public XQVariable newVariable (String localname, XQVariableType type, long value)
		{
			return null;
		}

		public XQVariable newVariable (String namespace, String localname, XQVariableType type, double value)
		{
			return null;
		}

		public XQVariable newVariable (String localname, XQVariableType type, double value)
		{
			return null;
		}

		public XQVariable newVariable (String namespace, String localname, boolean value)
		{
			return null;
		}

		public XQVariable newVariable (String localname, boolean value)
		{
			return null;
		}

		public XQDuration newDuration (int years, int months, int days, int hours, int minutes, int seconds, double subseconds)
		{
			return null;
		}

		public XQDuration newDuration (boolean positive, int years, int months, int days, int hours, int minutes, int seconds, double subseconds)
		{
			return null;
		}

		public XQDuration newDuration (String value)
		{
			return null;
		}
	}

	// ----------------------------------------------------------

	protected static class MockRunner implements XQRunner
	{
		private volatile long delay = 5000;
		private volatile boolean aborted = false;
		private volatile boolean throwException = false;
		private volatile boolean run = true;
		private volatile Thread sleeper = null;

		public XQResult runQuery (XQuery query) throws XQException
		{
			long target = System.currentTimeMillis() + delay;

			aborted = false;
			throwException = false;
			run = true;
			sleeper = Thread.currentThread ();

			while (run && (System.currentTimeMillis() < target)) {
				try {
					Thread.sleep (target - System.currentTimeMillis());
				} catch (InterruptedException e) {
					// nothing
				}
			}

			sleeper = null;

			if (throwException) {
				throw new XQException ("Sorry, Dude");
			}

			return (null);
		}

		public XQResult runQueryStreaming (XQuery query) throws XQException
		{
			return (runQuery (query));
		}

		public void abortQuery()
		{
			if (sleeper == null) {
				throw new IllegalStateException ("Query is not in progress");
			}

			run = false;
			aborted = true;
			sleeper.interrupt();
		}

		public void insertDocument (String uri, XQDocumentWrapper documentWrapper, XQDocumentMetaData metaData)
		{
			throw new UnsupportedOperationException ("not yet implemented");
		}

		// ---------------------------------------

		public void failQuery()
		{
			throwException = true;

			abortQuery();

			aborted = false;
		}

		public void setDelay (long delay)
		{
			this.delay = delay;
		}

		public boolean isAborted()
		{
			return aborted;
		}

		public boolean isRunning()
		{
			return (sleeper != null);
		}
	}
}
