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

import com.marklogic.xqrunner.XQuery;
import com.marklogic.xqrunner.XQAsyncRunner;
import com.marklogic.xqrunner.XQProgressListener;
import com.marklogic.xqrunner.XQResult;
import com.marklogic.xqrunner.XQRunner;
import com.marklogic.xqrunner.XQException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Ron Hitchens
 */
public class AsyncRunner implements XQAsyncRunner
{
	private Map listeners = Collections.synchronizedMap (new HashMap());
	private XQRunner syncRunner;
	private volatile XQRunner activeRunner = null;

	public AsyncRunner (XQRunner syncRunner)
	{
		this.syncRunner = syncRunner;
	}

	// ---------------------------------------------------------
	// Implementation of XQRunner interface

	public XQResult runQuery (XQuery query) throws XQException
	{
		setActiveRunner (syncRunner);

		XQResult result = syncRunner.runQuery (query);

		clearActiveRunner();

		return (result);
	}

	public synchronized void abortQuery () throws XQException
	{
		if (activeRunner != null) {
			activeRunner.abortQuery();

			clearActiveRunner ();
		} else {
			throw new IllegalStateException ("No active query");
		}
	}

	// ---------------------------------------------------------
	// Implementation of XQAsyncRunner interface

	public synchronized void startQuery (XQuery query)
	{
		setActiveRunner (new BgRunner (this, syncRunner, query));

		try {
			activeRunner.runQuery (query);
		} catch (XQException e) {
			// nothing, won't happen with BgRunner
		}
	}

	public void startQuery (XQuery query, XQProgressListener listener, Object attachment)
	{
		registerListener (listener, attachment);
		startQuery (query);
	}

	public synchronized void awaitQueryCompletion()
	{
		while (activeRunner != null) {
			try {
				wait();
			} catch (InterruptedException e) {
				// nothing
			}
		}
	}

	public void registerListener (XQProgressListener listener, Object attachment)
	{
		listeners.put (listener, attachment);
	}

	public void unregisterListener (XQProgressListener listener)
	{
		listeners.remove (listener);
	}

	public void clearListeners()
	{
		listeners.clear();
	}

	// -----------------------------------------------------------

	private synchronized void setActiveRunner (XQRunner runner)
	{
		if (activeRunner != null) {
			throw new IllegalStateException ("Query is already running");
		}

		activeRunner = runner;
	}

	private synchronized void clearActiveRunner()
	{
		activeRunner = null;

		notifyAll();
	}

	// -----------------------------------------------------------

	public void notifyStart (XQRunner context)
	{
		notifyListeners (NotifyType.QSTART, context, null, null);
	}

	public void notifyFinished (XQRunner context, XQResult result)
	{
		notifyListeners (NotifyType.QDONE, context, result, null);
	}

	public void notifyAborted (XQRunner context)
	{
		notifyListeners (NotifyType.QABORT, context, null, null);
	}

	public void notifyFailed (XQRunner context, Throwable throwable)
	{
		notifyListeners (NotifyType.QFAIL, context, null, throwable);
	}

	private void notifyListeners (NotifyType which, XQRunner context, XQResult result, Throwable throwable)
	{
		synchronized (listeners) {
			for (Iterator it = listeners.keySet().iterator (); it.hasNext ();) {
				XQProgressListener listener = (XQProgressListener) it.next ();
				Object attachment = listeners.get (listener);

				if (which == NotifyType.QSTART) {
					listener.queryStarted (context, attachment);
				}

				if (which == NotifyType.QDONE) {
					listener.queryFinished (context, result, attachment);
				}

				if (which == NotifyType.QABORT) {
					listener.queryAborted (context, attachment);
				}

				if (which == NotifyType.QFAIL) {
					listener.queryFailed (context, throwable, attachment);
				}
			}
		}
	}

	private static class NotifyType
	{
		public static final NotifyType QSTART = new NotifyType ("Start");
		public static final NotifyType QDONE = new NotifyType ("Finished");
		public static final NotifyType QABORT = new NotifyType ("Aorted");
		public static final NotifyType QFAIL = new NotifyType ("Failed");

		private String name;

		private NotifyType (String name) {
			this.name = name;
		}

		public String toString ()
		{
			return (name);
		}
	}

	// -----------------------------------------------------------

	private class BgRunner implements Runnable, XQRunner
	{
		private XQRunner context;
		private XQuery query;
		private XQRunner runner;
		private volatile boolean queryRunning;

		public BgRunner (XQRunner context, XQRunner runner, XQuery query)
		{
			this.context = context;
			this.query = query;

			this.runner = runner;
		}

		public XQResult runQuery (XQuery query)
		{
			new Thread (this).start();

			return (null);
		}

		public void abortQuery () throws XQException
		{
			synchronized (this) {
				if (queryRunning) {
					runner.abortQuery();
				}
			}

			notifyAborted (context);
		}

		public void run ()
		{
			XQResult result = null;

			notifyStart (context);

			try {
				synchronized (this) {
					queryRunning = true;
				}

				result = runner.runQuery (query);

				synchronized (this) {
					queryRunning = false;
				}

				notifyFinished (context, result);
			} catch (XQException e) {
				queryRunning = false;

				notifyFailed (context, e);
			}

			clearActiveRunner();
		}
	}
}
