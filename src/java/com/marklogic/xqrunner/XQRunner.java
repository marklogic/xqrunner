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

import java.io.IOException;

/**
 * The API interface for a synchronous XQuery Runner.
 * @author Ron Hitchens, Mark Logic Corporation
 */
public interface XQRunner
{
	/**
	 * Run the given query synchronously and return the result.
	 * @param query An XQuery instance that will be executed on the server.
	 * @return An XQResult object containing the buffered result.
	 * @throws XQException If a problem occurs while processing the query.
	 */
	XQResult runQuery (XQuery query) throws XQException;

	/**
	 * Run the given query synchronously and return a streaming result.
	 * @param query An XQuery instance that will be executed on the server.
	 * @return An XQResult object containing the buffered result.
	 * @throws XQException If a problem occurs while processing the query.
	 */
	XQResult runQueryStreaming (XQuery query) throws XQException;

	void insertDocument (String uri, XQDocumentWrapper documentWrapper, XQDocumentMetaData metaData)
		throws XQException, IOException;

	/**
	 * Abort a running query, if possible.
	 * @throws XQException If there is a problem aborting the query.
	 */
	void abortQuery() throws XQException;
}
