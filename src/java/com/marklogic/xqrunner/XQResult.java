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
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: Aug 19, 2004
 * Time: 4:34:43 PM
 */
public interface XQResult
{
	/**
	 * @return The number of ResultItem elements the sequence.
	 */
	int getSize();

	/**
	 * @return The full sequence of results as an array.
	 */
	XQResultItem [] getItems();

	/**
	 * @param index The index of the ResultItem object to return, the first
	 * is zero.
	 * @return The ResultItem object at the requested index.
	 */
	XQResultItem getItem (int index);

	String asString();

	String asString (String separator);
}
