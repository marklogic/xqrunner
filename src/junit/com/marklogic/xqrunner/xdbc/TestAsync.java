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
package com.marklogic.xqrunner.xdbc;

import junit.framework.TestCase;
import com.marklogic.xqrunner.XQDataSource;
import com.marklogic.xqrunner.XQRunner;
import com.marklogic.xqrunner.XQFactory;
import com.marklogic.xqrunner.XQException;
import com.marklogic.xqrunner.XQAsyncRunner;
import com.marklogic.xqrunner.XQProgressListener;
import com.marklogic.xqrunner.XQResult;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: Oct 4, 2004
 * Time: 3:43:27 PM
 */
public class TestAsync extends TestCase
{
	XQDataSource dataSource = null;
	XQRunner runner = null;
	XQAsyncRunner asyncRunner = null;

	protected void setUp () throws Exception
	{
		System.setProperty (XQFactory.PROPERTY_PREFIX + "mock", MockProvider.class.getName());

		XQFactory factory = new XQFactory("mock");

		dataSource = factory.newDataSource (null, null, null);
		runner = dataSource.newSyncRunner();
		asyncRunner = dataSource.newAsyncRunner (runner);
	}

	public void testAsyncRunner() throws XQException, InterruptedException
	{
		MockProvider.MockRunner mockRunner = (MockProvider.MockRunner) runner;

		assertFalse (mockRunner.isAborted());

		mockRunner.setDelay (5000);

		try {
			asyncRunner.abortQuery();
			fail ("abort should fail if no query active");
		} catch (IllegalStateException e) {
			// good result
		}

		asyncRunner.startQuery (dataSource.newQuery ("\"Hello World\""));

		Thread.sleep (2000);

		asyncRunner.abortQuery();

		assertTrue (mockRunner.isAborted());

		try {
			asyncRunner.abortQuery();
			fail ("abort should fail if no query active");
		} catch (IllegalStateException e) {
			// good result
		}
	}

	public void testAsyncElapsed() throws InterruptedException, XQException
	{
		MockProvider.MockRunner mockRunner = (MockProvider.MockRunner) runner;

		assertFalse (mockRunner.isAborted());

		mockRunner.setDelay (1000);

		asyncRunner.startQuery (dataSource.newQuery ("\"Hello World\""));

		asyncRunner.awaitQueryCompletion();

		assertFalse (mockRunner.isAborted());

		try {
			asyncRunner.abortQuery();
			fail ("abort should fail if no query active");
		} catch (IllegalStateException e) {
			// good result
		}
	}

	public void testAsyncCallbackNoError() throws InterruptedException
	{
		MockProvider.MockRunner mockRunner = (MockProvider.MockRunner) runner;

		mockRunner.setDelay (200);

		MockListener listener = new MockListener();

		asyncRunner.registerListener (listener, null);
		asyncRunner.startQuery (dataSource.newQuery ("\"Hello World\""));

		asyncRunner.awaitQueryCompletion();

		assertFalse (mockRunner.isAborted());

		assertTrue ("started", listener.isStarted());
		assertTrue ("finished", listener.isFinished());
		assertFalse ("aborted", listener.isAborted());
		assertFalse ("failed", listener.isFailed());
	}

	public void testAsyncCallbackAbort() throws InterruptedException, XQException
	{
		MockProvider.MockRunner mockRunner = (MockProvider.MockRunner) runner;

		mockRunner.setDelay (10000);

		MockListener listener = new MockListener();

		asyncRunner.registerListener (listener, null);
		asyncRunner.startQuery (dataSource.newQuery ("\"Hello World\""));

		while ( ! mockRunner.isRunning()) {
			Thread.sleep (100);
		}

		Thread.sleep (1000);

		asyncRunner.abortQuery();

		asyncRunner.awaitQueryCompletion();

		assertTrue (mockRunner.isAborted());

		assertTrue ("started", listener.isStarted());
		assertFalse ("finished", listener.isFinished());
		assertTrue ("aborted", listener.isAborted());
		assertFalse ("failed", listener.isFailed());
	}

	public void testAsyncCallbackException() throws InterruptedException, XQException
	{
		MockProvider.MockRunner mockRunner = (MockProvider.MockRunner) runner;

		mockRunner.setDelay (5000);

		MockListener listener = new MockListener();

		asyncRunner.registerListener (listener, null);
		asyncRunner.startQuery (dataSource.newQuery ("\"Hello World\""));

		while ( ! mockRunner.isRunning()) {
			Thread.sleep (100);
		}
		Thread.sleep (100);

		mockRunner.failQuery();

		asyncRunner.awaitQueryCompletion();

		assertFalse (mockRunner.isAborted());

		assertTrue ("started", listener.isStarted());
		assertFalse ("finished", listener.isFinished());
		assertFalse ("aborted", listener.isAborted());
		assertTrue ("failed", listener.isFailed());
	}

	public void testAsyncCallbackTwice() throws InterruptedException, XQException
	{
		MockProvider.MockRunner mockRunner = (MockProvider.MockRunner) runner;

		mockRunner.setDelay (10000);

		MockListener listener = new MockListener();

		asyncRunner.registerListener (listener, null);
		asyncRunner.startQuery (dataSource.newQuery ("\"Hello World\""));

		while ( ! mockRunner.isRunning()) {
			Thread.sleep (100);
		}

		Thread.sleep (1000);

		asyncRunner.abortQuery();

		asyncRunner.awaitQueryCompletion();

		assertTrue (mockRunner.isAborted());

		assertTrue ("started", listener.isStarted());
		assertFalse ("finished", listener.isFinished());
		assertTrue ("aborted", listener.isAborted());
		assertFalse ("failed", listener.isFailed());


		mockRunner.setDelay (200);

		listener = new MockListener();

		asyncRunner.registerListener (listener, null);
		asyncRunner.startQuery (dataSource.newQuery ("\"Hello World\""));

		asyncRunner.awaitQueryCompletion();

		assertFalse (mockRunner.isAborted());

		assertTrue ("started", listener.isStarted());
		assertTrue ("finished", listener.isFinished());
		assertFalse ("aborted", listener.isAborted());
		assertFalse ("failed", listener.isFailed());
	}

	public void testAsyncCallbackTwoListeners() throws InterruptedException
	{
		MockProvider.MockRunner mockRunner = (MockProvider.MockRunner) runner;

		mockRunner.setDelay (200);

		MockListener listener = new MockListener();
		MockListener listener2 = new MockListener();

		asyncRunner.registerListener (listener, null);
		asyncRunner.registerListener (listener2, null);
		asyncRunner.startQuery (dataSource.newQuery ("\"Hello World\""));

		asyncRunner.awaitQueryCompletion();

		assertFalse (mockRunner.isAborted());

		assertTrue ("started", listener.isStarted());
		assertTrue ("finished", listener.isFinished());
		assertFalse ("aborted", listener.isAborted());
		assertFalse ("failed", listener.isFailed());

		assertTrue ("started", listener2.isStarted());
		assertTrue ("finished", listener2.isFinished());
		assertFalse ("aborted", listener2.isAborted());
		assertFalse ("failed", listener2.isFailed());
	}

	public void testAsyncCallbackAndClear() throws InterruptedException
	{
		MockProvider.MockRunner mockRunner = (MockProvider.MockRunner) runner;

		mockRunner.setDelay (200);

		MockListener listener = new MockListener();

		asyncRunner.registerListener (listener, null);
		asyncRunner.startQuery (dataSource.newQuery ("\"Hello World\""));

		asyncRunner.awaitQueryCompletion();

		assertFalse (mockRunner.isAborted());

		assertTrue ("started", listener.isStarted());
		assertTrue ("finished", listener.isFinished());
		assertFalse ("aborted", listener.isAborted());
		assertFalse ("failed", listener.isFailed());

		listener.reset();

		asyncRunner.clearListeners ();

		asyncRunner.startQuery (dataSource.newQuery ("\"Hello World\""));

		asyncRunner.awaitQueryCompletion();

		assertFalse (mockRunner.isAborted());

		assertFalse ("started", listener.isStarted());
		assertFalse ("finished", listener.isFinished());
		assertFalse ("aborted", listener.isAborted());
		assertFalse ("failed", listener.isFailed());
	}

	public void testAsyncCallbackAndUnregister() throws InterruptedException
	{
		MockProvider.MockRunner mockRunner = (MockProvider.MockRunner) runner;

		mockRunner.setDelay (200);

		MockListener listener = new MockListener();
		MockListener listener2 = new MockListener();

		asyncRunner.registerListener (listener, null);
		asyncRunner.registerListener (listener2, null);
		asyncRunner.startQuery (dataSource.newQuery ("\"Hello World\""));

		asyncRunner.awaitQueryCompletion();

		assertFalse (mockRunner.isAborted());

		assertTrue ("started", listener.isStarted());
		assertTrue ("finished", listener.isFinished());
		assertFalse ("aborted", listener.isAborted());
		assertFalse ("failed", listener.isFailed());

		assertTrue ("started", listener2.isStarted());
		assertTrue ("finished", listener2.isFinished());
		assertFalse ("aborted", listener2.isAborted());
		assertFalse ("failed", listener2.isFailed());

		asyncRunner.unregisterListener (listener);

		listener.reset();
		listener2.reset();
		asyncRunner.startQuery (dataSource.newQuery ("\"Hello World\""));

		asyncRunner.awaitQueryCompletion();

		assertFalse (mockRunner.isAborted());

		assertFalse ("started", listener.isStarted());
		assertFalse ("finished", listener.isFinished());
		assertFalse ("aborted", listener.isAborted());
		assertFalse ("failed", listener.isFailed());

		assertTrue ("started", listener2.isStarted());
		assertTrue ("finished", listener2.isFinished());
		assertFalse ("aborted", listener2.isAborted());
		assertFalse ("failed", listener2.isFailed());
	}

	// ----------------------------------------------------------------

	private class MockListener implements XQProgressListener
	{
		private volatile boolean started = false;
		private volatile boolean finished = false;
		private volatile boolean aborted = false;
		private volatile boolean failed = false;

		public void queryStarted (XQRunner runner, Object attachment)
		{
			started = true;
		}

		public void queryFinished (XQRunner runner, XQResult result, Object attachment)
		{
			finished = true;
		}

		public void queryAborted (XQRunner runner, Object attachment)
		{
			aborted = true;
		}

		public void queryFailed (XQRunner runner, Throwable throwable, Object attachment)
		{
			failed = true;
		}

		public boolean isStarted ()
		{
			return started;
		}

		public boolean isFinished ()
		{
			return finished;
		}

		public boolean isAborted ()
		{
			return aborted;
		}

		public boolean isFailed ()
		{
			return failed;
		}

		public void reset ()
		{
			started = finished = aborted = failed = false;
		}
	}
}
