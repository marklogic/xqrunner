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

import com.marklogic.xqrunner.XQDocumentWrapper;
import junit.framework.TestCase;

import java.io.InputStream;
import java.io.Reader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.ByteArrayInputStream;

// FIXME: Refactor this to eliminate duplicate comparisons

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: Oct 20, 2004
 * Time: 4:59:58 PM
 */
public class TestDocument extends TestCase
{
	public void testXml() throws IOException
	{
		XQDocumentWrapper doc = GenericDocumentWrapper.newXml ("<foobar>blah</foobar>");

		assertTrue (doc.isXml());
		assertFalse (doc.isText());
		assertFalse (doc.isBinary());

		String value = stringFromReader (doc.asReader());
		assertEquals ("<foobar>blah</foobar>", value);

		value = stringFromReader (doc.asReader());
		assertEquals ("<foobar>blah</foobar>", value);

		value = stringFromStream (doc.asStream());
		assertEquals ("<foobar>blah</foobar>", value);

		value = stringFromStream (doc.asStream());
		assertEquals ("<foobar>blah</foobar>", value);
	}

	public void testText() throws IOException
	{
		XQDocumentWrapper doc = GenericDocumentWrapper.newText ("<foobar>blah</foobar>");

		assertFalse (doc.isXml());
		assertTrue (doc.isText());
		assertFalse (doc.isBinary());

		String value = stringFromReader (doc.asReader());
		assertEquals ("<foobar>blah</foobar>", value);

		value = stringFromReader (doc.asReader());
		assertEquals ("<foobar>blah</foobar>", value);

		value = stringFromStream (doc.asStream());
		assertEquals ("<foobar>blah</foobar>", value);

		value = stringFromStream (doc.asStream());
		assertEquals ("<foobar>blah</foobar>", value);
	}

	public void testReaderXml() throws IOException
	{
		Reader reader = new StringReader ("<foobar>blah</foobar>");
		XQDocumentWrapper doc = GenericDocumentWrapper.newXml (reader);

		assertTrue (doc.isXml());
		assertFalse (doc.isText());
		assertFalse (doc.isBinary());

		String value = stringFromReader (doc.asReader());
		assertEquals ("<foobar>blah</foobar>", value);

		try {
			value = stringFromReader (doc.asReader());
			fail ("Expect IllegalStateException");
		} catch (IllegalStateException e) {
			// ok
		}

		try {
			value = stringFromStream (doc.asStream());
			fail ("Expect IllegalStateException");
		} catch (IllegalStateException e) {
			// ok
		}
	}

	public void testReaderText() throws IOException
	{
		Reader reader = new StringReader ("<foobar>blah</foobar>");
		XQDocumentWrapper doc = GenericDocumentWrapper.newText (reader);

		assertFalse (doc.isXml());
		assertTrue (doc.isText());
		assertFalse (doc.isBinary());

		String value = stringFromReader (doc.asReader());
		assertEquals ("<foobar>blah</foobar>", value);

		try {
			value = stringFromReader (doc.asReader());
			fail ("Expect IllegalStateException");
		} catch (IllegalStateException e) {
			// ok
		}

		try {
			value = stringFromStream (doc.asStream());
			fail ("Expect IllegalStateException");
		} catch (IllegalStateException e) {
			// ok
		}
	}

	public void testStreamXml() throws IOException
	{
		InputStream inputStream = new ByteArrayInputStream ("<foobar>blah</foobar>".getBytes ("UTF-8"));
		XQDocumentWrapper doc = GenericDocumentWrapper.newXml (inputStream);

		assertTrue (doc.isXml());
		assertFalse (doc.isText());
		assertFalse (doc.isBinary());

		String value = stringFromStream (doc.asStream());
		assertEquals ("<foobar>blah</foobar>", value);

		try {
			value = stringFromStream (doc.asStream());
			fail ("Expect IllegalStateException");
		} catch (IllegalStateException e) {
			// ok
		}

		try {
			value = stringFromReader (doc.asReader());
			fail ("Expect IllegalStateException");
		} catch (IllegalStateException e) {
			// ok
		}
	}

	public void testStreamText() throws IOException
	{
		InputStream inputStream = new ByteArrayInputStream ("<foobar>blah</foobar>".getBytes ("UTF-8"));
		XQDocumentWrapper doc = GenericDocumentWrapper.newText (inputStream);

		assertFalse (doc.isXml());
		assertTrue (doc.isText());
		assertFalse (doc.isBinary());

		String value = stringFromStream (doc.asStream());
		assertEquals ("<foobar>blah</foobar>", value);

		try {
			value = stringFromStream (doc.asStream());
			fail ("Expect IllegalStateException");
		} catch (IllegalStateException e) {
			// ok
		}

		try {
			value = stringFromReader (doc.asReader());
			fail ("Expect IllegalStateException");
		} catch (IllegalStateException e) {
			// ok
		}
	}

	public void testBinaryStream() throws IOException
	{
		byte [] bytes = { 12, 32, (byte) 0xfd, 17, 127, 46, (byte) 0xb7 };
		ByteArrayInputStream bis = new ByteArrayInputStream (bytes);
		XQDocumentWrapper doc = GenericDocumentWrapper.newBinary (bis);

		assertFalse (doc.isXml());
		assertFalse (doc.isText());
		assertTrue (doc.isBinary());

		byte [] value = bytesFromStream (doc.asStream());

		for (int i = 0; i < value.length; i++) {
			assertEquals ("byte " + i + ": ", value [i], bytes [i]);
		}

		try {
			value = bytesFromStream (doc.asStream());
			fail ("Expect IllegalStateException");
		} catch (IllegalStateException e) {
			// ok
		}

		try {
			doc.asReader();
			fail ("Expect IllegalStateException");
		} catch (IllegalStateException e) {
			// ok
		}
	}

	public void testStringTypedConstructor() throws IOException
	{
		XQDocumentWrapper doc = GenericDocumentWrapper.newDoc ("<foobar>blah</foobar>", GenericDocumentWrapper.XML);

		assertTrue (doc.isXml());
		assertFalse (doc.isText());
		assertFalse (doc.isBinary());

		String value = stringFromReader (doc.asReader());
		assertEquals ("<foobar>blah</foobar>", value);

		value = stringFromReader (doc.asReader());
		assertEquals ("<foobar>blah</foobar>", value);

		value = stringFromStream (doc.asStream());
		assertEquals ("<foobar>blah</foobar>", value);

		value = stringFromStream (doc.asStream());
		assertEquals ("<foobar>blah</foobar>", value);

	}

	public void testTextTypedConstructor() throws IOException
	{
		XQDocumentWrapper doc = GenericDocumentWrapper.newDoc ("<foobar>blah</foobar>", GenericDocumentWrapper.TEXT);

		assertFalse (doc.isXml());
		assertTrue (doc.isText());
		assertFalse (doc.isBinary());

		String value = stringFromReader (doc.asReader());
		assertEquals ("<foobar>blah</foobar>", value);

		value = stringFromReader (doc.asReader());
		assertEquals ("<foobar>blah</foobar>", value);

		value = stringFromStream (doc.asStream());
		assertEquals ("<foobar>blah</foobar>", value);

		value = stringFromStream (doc.asStream());
		assertEquals ("<foobar>blah</foobar>", value);

	}

	public void testReaderTypedConstructor() throws IOException
	{
		Reader reader = new StringReader ("<foobar>blah</foobar>");
		XQDocumentWrapper doc = GenericDocumentWrapper.newDoc (reader, GenericDocumentWrapper.TEXT);

		assertFalse (doc.isXml());
		assertTrue (doc.isText());
		assertFalse (doc.isBinary());

		String value = stringFromReader (doc.asReader());
		assertEquals ("<foobar>blah</foobar>", value);

		try {
			value = stringFromReader (doc.asReader());
			fail ("Expect IllegalStateException");
		} catch (IllegalStateException e) {
			// ok
		}

		try {
			value = stringFromStream (doc.asStream());
			fail ("Expect IllegalStateException");
		} catch (IllegalStateException e) {
			// ok
		}
	}

	public void testStreamBinaryTypedConstructor() throws IOException
	{
		byte [] bytes = { 12, 32, (byte) 0xfd, 17, 127, 46, (byte) 0xb7 };
		ByteArrayInputStream bis = new ByteArrayInputStream (bytes);
		XQDocumentWrapper doc = GenericDocumentWrapper.newDoc (bis, GenericDocumentWrapper.BINARY);

		assertFalse (doc.isXml());
		assertFalse (doc.isText());
		assertTrue (doc.isBinary());

		byte [] value = bytesFromStream (doc.asStream());

		for (int i = 0; i < value.length; i++) {
			assertEquals ("byte " + i + ": ", value [i], bytes [i]);
		}

		try {
			value = bytesFromStream (doc.asStream());
			fail ("Expect IllegalStateException");
		} catch (IllegalStateException e) {
			// ok
		}

		try {
			doc.asReader();
			fail ("Expect IllegalStateException");
		} catch (IllegalStateException e) {
			// ok
		}
	}

	public void testDocType()
	{
		assertNull (GenericDocumentWrapper.typeFor (null));
		assertNull (GenericDocumentWrapper.typeFor ("foo"));
		assertSame (GenericDocumentWrapper.XML, GenericDocumentWrapper.typeFor ("xml"));
		assertSame (GenericDocumentWrapper.TEXT, GenericDocumentWrapper.typeFor ("text"));
		assertSame (GenericDocumentWrapper.BINARY, GenericDocumentWrapper.typeFor ("binary"));
	}

	// --------------------------------------------------------

	private byte [] bytesFromStream (InputStream inputStream) throws IOException
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte [] buffer = new byte [1024];
		int rc;

		while ((rc = inputStream.read (buffer)) >= 0) {
			bos.write (buffer, 0, rc);
		}

		bos.flush();

		return (bos.toByteArray ());
	}

	private String stringFromStream (InputStream inputStream) throws IOException
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte [] buffer = new byte [1024];
		int rc;

		while ((rc = inputStream.read (buffer)) >= 0) {
			bos.write (buffer, 0, rc);
		}

		bos.flush();

		return (bos.toString ("UTF-8"));
	}

	private String stringFromReader (Reader reader) throws IOException
	{
		StringBuffer sb = new StringBuffer();
		char [] buffer = new char [1024];
		int rc;

		while ((rc = reader.read (buffer)) >= 0) {
			sb.append (buffer, 0, rc);
		}

		return (sb.toString());
	}
}