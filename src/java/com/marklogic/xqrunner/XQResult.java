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

import java.io.Writer;
import java.io.IOException;

/**
 * Contains a (possibly empty) result sequence.
 * @author Ron Hitchens, Mark Logic
 */
public interface XQResult
{
	/**
	 * Indicates whether this result object was produced by the
	 * XQRunnger.runQueryStreaming() method.  Individual ResultItem
	 * instances may or may not be streaming.  Atomic values, such
	 * as booleans, numeric values and strings, never stream.  It's
	 * therefore possible that a straming result would not have any
	 * streaming items.  A streaming result mus still be accessed
	 * sequentially, even if none of it's items are streaming.
	 * @return True if this result is streaming, false if buffered.
	 */
	boolean isStreaming();

	/**
	 * Release any resources held by this result object.  Call this
	 * method on streaming results to release connection state.
	 * @see #isStreaming()
	 */
	void release();

	/**
	 * @see #isStreaming()
	 * @return The number of ResultItem elements the sequence, -1 if streaming.
	 */
	int getSize();

	/**
	 * @return The full sequence of results as an array.  If the result
	 * is streaming, the size of the returned array will be either 1 or 0.
	 * If there is a current result item, this array will contain that
	 * ResultItem object.  If the result sequence was empty, or you've
	 * advanced off the end, this array will be zero length.
	 */
	XQResultItem [] getItems();

	/**
	 * @param index The index of the ResultItem object to return, the first
	 * is zero.
	 * @return The ResultItem object at the requested index.
	 * @throws XQException If there is a problem marshalling the
	 *  ResultItem.  This will only happen for streaming ResultItems.
	 */
	XQResultItem getItem (int index) throws XQException;

	/**
	 * @return The next item in the sequence.  An internal index is
	 *  maintained.  For streaming results, this index may only move
	 *  forward.  Fo buffered results, the index may be rewound with
	 *  the rewindItems() method.
	 * @see #rewindItems()
	 * @throws XQException If there is a problem getting the next
	 *  ResultItem.  This will only happen for streaming ResultItems.
	 * @throws IllegalStateException
	 */
	XQResultItem nextItem() throws XQException;

	/**
	 * Resets the internal index so that the next nextItem() call
	 * returns the first item in the sequence.  Streaming results may
	 * not be rewound.
	 * @throws IllegalStateException If called on a streaming result.
	 */
	void rewindItems();

	/**
	 * @return A String representation of the result sequence by
	 * concatenating the string representations of each item in
	 * the sequence.
	 * @throws XQException If there is a problem marshalling the
	 *  value as a string.
	 */
	String asString() throws XQException;

	/**
	 * @param separator A String value to be inserted between
	 * each item in the sequence.
	 * @return A String representation of the result sequence by
	 * concatenating the string representations of each item in
	 * the sequence, with the separator String between each.
	 * @throws XQException If there is a problem marshalling the
	 *  value as a string.
	 */
	String asString (String separator) throws XQException;

	/**
	 * Write the String representation (as would be returned by
	 * asString()) to the provided Writer.
	 * @param writer The Writer object to which the String representation
	 *  of this result should be written.
	 * @exception IOException Thrown is there is a problem writing
	 *  to the given Writer.
	 */
	void writeTo (Writer writer) throws IOException, XQException;

	/**
	 * Write the String representation (as would be returned by
	 * asString()) to the provided Writer, inserting the given
	 * separator String between each item in the result sequence.
	 * @param writer The Writer object to which the String representation
	 *  of this result should be written.
	 * @param separator A String value to be inserted between
	 *  each item in the sequence.
	 * @exception IOException Thrown is there is a problem writing
	 *  to the given Writer.
	 */
	void writeTo (Writer writer, String separator) throws IOException, XQException;
}
