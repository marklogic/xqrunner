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
import com.marklogic.xqrunner.XQDocumentWrapper;
import com.marklogic.xqrunner.XQDocumentMetaData;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Generic asynchronous query runner framework.
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
		return (runTheQuery (query, false));
	}

	public XQResult runQueryStreaming (XQuery query) throws XQException
	{
		return (runTheQuery (query, true));
	}

	public void insertDocument (String uri, XQDocumentWrapper documentWrapper, XQDocumentMetaData metaData)
	{
		throw new UnsupportedOperationException ("not yet implemented");
	}

	public synchronized void abortQuery () throws XQException
	{
		if (activeRunner != null) {
			activeRunner.abortQuery();

			awaitQueryCompletion();
		} else {
			throw new IllegalStateException ("No active query");
		}
	}

	private XQResult runTheQuery (XQuery query, boolean streaming) throws XQException
	{
		setActiveRunner (syncRunner);

		XQResult result = (streaming) ? syncRunner.runQueryStreaming (query) :  syncRunner.runQuery (query);

		clearActiveRunner();

		return (result);
	}

	// ---------------------------------------------------------
	// Implementation of XQAsyncRunner interface

	public void startQuery (XQuery query)
	{
		startTheQuery (query, false);
	}

	public void startQueryStreaming (XQuery query)
	{
		startTheQuery (query, true);
	}

	private synchronized void startTheQuery (XQuery query, boolean streaming)
	{
		setActiveRunner (new BgRunner (this, syncRunner, query));

		try {
			if (streaming) {
				activeRunner.runQueryStreaming (query);
			} else {
				activeRunner.runQuery (query);
			}
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

	public void notifyStart (XQAsyncRunner context)
	{
		notifyListeners (NotifyType.QSTART, context, null, null);
	}

	public void notifyFinished (XQAsyncRunner context, XQResult result)
	{
		notifyListeners (NotifyType.QDONE, context, result, null);
	}

	public void notifyAborted (XQAsyncRunner context)
	{
		notifyListeners (NotifyType.QABORT, context, null, null);
	}

	public void notifyFailed (XQAsyncRunner context, Throwable throwable)
	{
		notifyListeners (NotifyType.QFAIL, context, null, throwable);
	}

	private void notifyListeners (NotifyType which, XQAsyncRunner context, XQResult result, Throwable throwable)
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
		public static final NotifyType QABORT = new NotifyType ("Aborted");
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
		private XQAsyncRunner context;
		private XQuery query;
		private XQRunner runner;
		private volatile boolean streaming;
		private volatile boolean queryRunning;
		private volatile boolean aborted = false;

		public BgRunner (XQAsyncRunner context, XQRunner runner, XQuery query)
		{
			this.context = context;
			this.query = query;
			this.runner = runner;
		}

		public XQResult runQuery (XQuery query)
		{
			streaming = false;

			new Thread (this).start();

			return (null);
		}

		public XQResult runQueryStreaming (XQuery query) throws XQException
		{
			streaming = true;

			return (runQuery (query));
		}

		public void insertDocument (String uri, XQDocumentWrapper documentWrapper, XQDocumentMetaData metaData)
		{
			throw new UnsupportedOperationException ("not yet implemented");
		}

		public void abortQuery () throws XQException
		{
			synchronized (this) {
				if (queryRunning) {
					runner.abortQuery();
					aborted = true;
				}
			}
		}

		public void run ()
		{
			XQResult result = null;

			notifyStart (context);

			try {
				synchronized (this) {
					queryRunning = true;
				}

				result = (streaming) ? runner.runQueryStreaming (query) : runner.runQuery (query);

				synchronized (this) {
					queryRunning = false;
				}

				if (aborted) {
					notifyAborted (context);
				} else {
					notifyFinished (context, result);
				}

			} catch (XQException e) {
				queryRunning = false;

				notifyFailed (context, e);
			}

			clearActiveRunner();
		}
	}
}
