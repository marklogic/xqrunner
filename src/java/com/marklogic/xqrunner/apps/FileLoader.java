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
package com.marklogic.xqrunner.apps;

import com.marklogic.xqrunner.XQDataSource;
import com.marklogic.xqrunner.XQDocumentWrapper;
import com.marklogic.xqrunner.XQRunner;
import com.marklogic.xqrunner.XQException;
import com.marklogic.xqrunner.generic.GenericDocumentWrapper;

import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Load a file into a CIS database with a given URI.
 * Examples:
 *    xdbc://user:secret@localhost:8003 -f filename -u someURI -t text
 *    xdbc://user:secret@localhost:8003 -f- -u someURI -t xml
 *    xdbc://user:secret@localhost:8003 -ffilename -u someURI -t binary
 */
public class FileLoader
{
	private XQDataSource dataSource;
	private String uri;
	private GenericDocumentWrapper.DocumentType docType;
	private InputStream inputStream;

	public FileLoader (Args args) throws Exception
	{
		dataSource = AppHelper.createDataSource (args);
		uri = getUri (args);
		docType = getDocumentType (args);
		inputStream = setupInput (args);
	}

	// -----------------------------------------------------------------

	private GenericDocumentWrapper.DocumentType getDocumentType (Args args)
	{
		GenericDocumentWrapper.DocumentType type =
			GenericDocumentWrapper.typeFor (args.findAndConsumeNamedArg ("-t"));

		if (type == null) {
			return (GenericDocumentWrapper.XML);
		}

		return (type);
	}

	private String getUri (Args args)
	{
		uri = args.findAndConsumeNamedArg ("-u");

		if (uri == null) {
			throw new IllegalArgumentException ("No document URI provided");
		}

		return (uri);
	}

	private InputStream setupInput (Args args) throws FileNotFoundException
	{
		String filename = args.findAndConsumeNamedArg ("-f");

		if (filename == null) {
			throw new IllegalArgumentException ("No input filename provided");
		}

		if (filename.equals ("-")) {
			inputStream = System.in;
		} else {
			inputStream = new BufferedInputStream (new FileInputStream (filename));
		}

		return (inputStream);
	}

	private void run() throws IOException, XQException
	{
		XQDocumentWrapper doc = GenericDocumentWrapper.newDoc (inputStream, docType);
		XQRunner runner = dataSource.newSyncRunner();

		runner.insertDocument (uri, doc, null);
	}

	// -----------------------------------------------------------------

	public static void main (String[] args)
	{
		FileLoader fileLoader = null;
		try {
			fileLoader = new FileLoader (new Args (args));
		} catch (Exception e) {
			System.err.println (e.toString());
			System.err.println ("usage: (serveruri | -s serverurifile) -f inputfilename -u docuri [-t xml|text|binary]");
			return;
		}

		try {
			fileLoader.run();
		} catch (Exception e) {
			System.err.println ("Uploading Document: " + e);
		}
	}
}
