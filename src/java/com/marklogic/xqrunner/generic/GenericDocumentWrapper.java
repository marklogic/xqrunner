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

import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: Oct 20, 2004
 * Time: 4:31:45 PM
 */
public class GenericDocumentWrapper implements XQDocumentWrapper
{
	// TODO: Refactor this to a common API class?
	public static class DocumentType
	{
		private String name;
		private DocumentType (String name) { this.name = name; }
		public String toString() { return name; };
	}

	public static final DocumentType XML = new DocumentType ("xml");
	public static final DocumentType TEXT = new DocumentType ("text");
	public static final DocumentType BINARY = new DocumentType ("binary");

	public static DocumentType typeFor (String typeName)
	{
		if ("xml".equals (typeName)) return (XML);
		if ("text".equals (typeName)) return (TEXT);
		if ("binary".equals (typeName)) return (BINARY);
		return (null);
	}

	// ---------------------------------------------------------------

	private String body = null;
	private InputStream inputStream = null;
	private Reader reader = null;

	private boolean xml = false;
	private boolean text = false;
	private boolean binary = false;

	// ---------------------------------------------------------------

	private GenericDocumentWrapper (String body, boolean isXml)
	{
		this.body = body;

		this.xml = isXml;
		this.text = ! isXml;
	}

	private GenericDocumentWrapper (Reader reader, boolean isXml)
	{
		this.reader = reader;

		this.xml = isXml;
		this.text = ! isXml;
	}

	private GenericDocumentWrapper (InputStream inputStream, boolean isXml)
	{
		this.inputStream = inputStream;

		this.xml = isXml;
		this.text = ! isXml;
	}

	private GenericDocumentWrapper (InputStream inputStream)
	{
		this.inputStream = inputStream;

		this.binary = true;
	}

	public static XQDocumentWrapper newXml (String xml)
	{
		return new GenericDocumentWrapper (xml, true);
	}

	public static XQDocumentWrapper newText (String text)
	{
		return new GenericDocumentWrapper (text, false);
	}

	public static XQDocumentWrapper newDoc (String text, DocumentType docType)
	{
		return new GenericDocumentWrapper (text, docType == XML);
	}

	// ---------------------------------------------------------------

	public static XQDocumentWrapper newXml (Reader reader)
	{
		return new GenericDocumentWrapper (reader, true);
	}

	public static XQDocumentWrapper newText (Reader reader)
	{
		return new GenericDocumentWrapper (reader, false);
	}

	public static XQDocumentWrapper newDoc (Reader reader, DocumentType docType)
	{
		return new GenericDocumentWrapper (reader, docType == XML);
	}

	public static XQDocumentWrapper newXml (InputStream inputStream)
	{
		return new GenericDocumentWrapper (inputStream, true);
	}

	public static XQDocumentWrapper newText (InputStream inputStream)
	{
		return new GenericDocumentWrapper (inputStream, false);
	}

	public static XQDocumentWrapper newBinary (InputStream inputStream)
	{
		return new GenericDocumentWrapper (inputStream);
	}

	public static XQDocumentWrapper newDoc (InputStream inputStream, DocumentType docType)
	{
		if (docType == BINARY) {
			return new GenericDocumentWrapper (inputStream);

		}

		return new GenericDocumentWrapper (inputStream, docType == XML);
	}

	// ---------------------------------------------------------------

	public boolean isXml ()
	{
		return (xml);
	}

	public boolean isText ()
	{
		return (text);
	}

	public boolean isBinary ()
	{
		return (binary);
	}

	public InputStream asStream ()
	{
		if (body != null) {
			try {
				return (new ByteArrayInputStream (body.getBytes ("UTF-8")));
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException ("UTF-8 encoding not available - *should never happen*", e);
			}
		}

		if (inputStream != null) {
			InputStream tmp = inputStream;

			inputStream = null;

			return (tmp);
		}

		throw new IllegalStateException ("No ready InputStream available");
	}

	public synchronized Reader asReader ()
	{
		if (body != null) {
			return (new StringReader (body));
		}

		if (reader != null) {
			Reader tmp = reader;

			reader = null;

			return (tmp);
		}

		throw new IllegalStateException ("No ready Reader available");
	}
}
