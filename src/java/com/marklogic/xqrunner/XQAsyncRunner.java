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
 * The API for an asynchronous XQuery runner.
 * @author ROn Hitchens, Mark Logic Corporation
 */
public interface XQAsyncRunner extends XQRunner
{
	/**
	 * Start the given query running and return.  The query
	 * will be processed asynchronously and registered listeners
	 * will be notified of query progress.
	 * @param query The XQuery instance to be executed.
	 */
	void startQuery (XQuery query);

	/**
	 * Start the given query running and return.  The query
	 * will be processed asynchronously and registered listeners
	 * will be notified of query progress.  The Result will be
	 * streaming.
	 * @param query The XQuery instance to be executed.
	 */
	void startQueryStreaming (XQuery query);

	/**
	 * Register the given listener (with the provided attachement),
	 * start the query and return.
	 * @param query The query to be processed.
	 * @param listener The XQProgressListener instance to register.
	 * @param attachment The object to be attached to the listener.
	 */
	void startQuery (XQuery query, XQProgressListener listener, Object attachment);

	/**
	 * Put the calling thread to sleep until the currently running
	 * query completes, either normally or abnormally.  If no query
	 * is currently running, return is immediate.
	 */
	void awaitQueryCompletion();

	/**
	 * Register the provided XQProgressListener object with this async
	 * runner.  Methods on the listener will be invoked as the query
	 * proceeds.
	 * @param listener The XQProgressListener instance to register with
	 *  this runner.
	 * @param attachment An object to attach to the listener which
	 *  can be used in any way the user sees fit.
	 */
	void registerListener (XQProgressListener listener, Object attachment);

	/**
	 * Unregister the given listener from this runner.
	 * @param listener The XQProgressListener instance to remove from
	 *  the list of listener.
	 */
	void unregisterListener (XQProgressListener listener);

	/**
	 * Clear the listener list for this runner so that no listeners
	 * are registered.
	 */
	void clearListeners();
}
