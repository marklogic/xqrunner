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

import java.io.Reader;
import java.io.Writer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * One item in a sequence of results.
 * @author Ron Hitchens
 */
public interface XQResultItem
{
	/**
	 * @return Index (zero-based) of this item in the result sequence.
	 */
	int getIndex();

	/**
	 * @return True if this item is streaming.  This will always be false
	 * for atomic values, even if the owning XQResult is streaming.
	 */
	boolean isStreaming();

	/**
	 * @return True if this result is an XML DOM node, otherwise false.
	 */
	boolean isNode();

	/**
	 * @return The type of the item, as defined in the XQVariableType class.
	 */
	XQVariableType getType();

	/**
	 * @return This result as an object.  The actual object type
	 * of object instance is determined by the type of the result.
	 * If this is a node (isNode() == true), the type will be
	 * an opaque object.  Call asW3CDom() or as JDom() to obtain
	 * a DOM you can to work with.
	 * Objects returned for atomic types will be appropriate types:
	 * Date types will be java.util.Date, booleans will be Boolean
	 * and numeric types will be the appropriate java.lang numeric
	 * object type.  Strings will be java.lang.String.
	 */
	Object asObject();

	/**
	 * @return This result as String.  This is equivalent to
	 * getObject().toString() but is more efficient.
	 */
	String asString();

	/**
	 * @return This result as a W3C DOM (org.w3c.dom.Document) object.
	 * @throws XQException If there is a problem converting this
	 * result to a DOM.  If this result is not a node (isNode() == false)
	 * then this exception will always be thrown.
	 */
	org.w3c.dom.Document asW3cDom() throws XQException;

	/**
	 * @return This result as a JDOM DOM (org.jdom.Document) object.
	 * @throws XQException If there is a problem converting this
	 * result to a DOM.  If this result is not a node (isNode() == false)
	 * then this exception will always be thrown.
	 */
	org.jdom.Document asJDom() throws XQException;

	/**
	 * @return A Reader from which the String representation of this
	 *  result item can be read.  If this item is buffered, this method
	 *  may be called multiple times.  If streaming, it may only be called
	 *  once.
	 * @throws XQException If there is a problem converting this
	 * result item to a Reader.
	 * @throws IllegalStateException If called more than once while streaming.
	 */
	Reader asReader() throws XQException;

	/**
 	 * @return An InputStream from which the byte representation of
	 *  this item can be read.  If this item is buffered, this method
	 *  may be called multiple times.  If streaming, it may only be
	 *  called once.  For non-binary types, the bytes read from this
	 *  stream will be the default encoding of the String representation.
	 * @throws XQException If there is a problem converting this
	 * result item to a stream.
	 * @throws IllegalStateException If called more than once while streaming.
	 */
	InputStream asStream() throws XQException;

	/**
	 * @return The byte representation of this item as a byte array.
	 *  For non-binary types, the bytes read from this stream will be
	 *  the default encoding of the String representation.
	 * @throws XQException If there is a problem converting this
	 * result item to a byte array.
	 */
	byte [] asBytes() throws IOException, XQException;

	/**
	 * Write the String representation of this result item to the
	 * given Writer.
	 * @param writer The writer to send the String representaiton to.
	 */
	void writeTo (Writer writer) throws IOException, XQException;

	/**
	 * Send the byte-array representation of this item to the given
	 * OutputStream.
	 * @param stream The OutputStream to send the bytes to.
	 * @throws IOException If there is a problem writing to the stream.
	 * @throws XQException If there is a problem marshaling the bytes
	 *  for this item.  This will only happen if the item is streaming.
	 */
	void streamTo (OutputStream stream) throws IOException, XQException;
}
