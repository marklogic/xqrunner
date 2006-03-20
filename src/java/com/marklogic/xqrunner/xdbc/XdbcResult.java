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

import com.marklogic.xqrunner.XQResult;
import com.marklogic.xqrunner.XQResultItem;
import com.marklogic.xqrunner.XQException;
import com.marklogic.xdbc.XDBCResultSequence;
import com.marklogic.xdbc.XDBCException;
import com.marklogic.xdbc.XDBCStatement;
import com.marklogic.xdbc.XDBCConnection;

import java.util.List;
import java.util.ArrayList;
import java.io.Writer;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: Oct 18, 2004
 * Time: 11:31:10 AM
 */
class XdbcResult implements XQResult
{
	private boolean streaming;
	private XQResultItem [] sequence = null;
	private XDBCResultSequence xdbcResultSequence = null;
	private int index = -1;

	XdbcResult (XDBCResultSequence xdbcResultSequence, boolean streaming)
		throws XDBCException
	{
		this.streaming = streaming;

		if (streaming) {
			this.xdbcResultSequence = xdbcResultSequence;
			sequence = new XQResultItem [0];
		} else {
			List list = new ArrayList();
			int index = 0;

			while (xdbcResultSequence.hasNext()) {
				xdbcResultSequence.next();

				list.add (new XdbcResultItem (xdbcResultSequence, index, false));

				index++;
			}

			sequence = new XdbcResultItem [list.size()];

			list.toArray (sequence);
		}
	}

	// -----------------------------------------------------------

	public boolean isStreaming()
	{
		return (streaming);
	}

	public void release()
	{
		XDBCStatement statement = null;
		XDBCConnection connection = null;

		if (xdbcResultSequence != null) {
			try {
				statement = xdbcResultSequence.getStatement();
				xdbcResultSequence.close();
			} catch (XDBCException e) {
				// nothing
			} finally {
				xdbcResultSequence = null;
			}
		}

		if (statement != null) {
			try {
				connection = statement.getConnection();
				statement.close ();
			} catch (XDBCException e) {
				// nothing
			}
		}

		if (connection != null) {
			try {
				connection.close();
			} catch (XDBCException e1) {
				// nothing
			}
		}
	}

	public int getSize ()
	{
		if (streaming) {
			return (-1);
		} else {
			return (sequence.length);
		}
	}

	public void rewindItems()
	{
		if (streaming && (index != -1)) {
			throw new IllegalStateException ("Cannot rewind a streaming result");
		}

		index = -1;
	}

	public XQResultItem [] getItems ()
	{
		return (sequence);
	}

	public XQResultItem getItem (int index) throws XQException
	{
		if ( ! streaming) {
			if (index < sequence.length) {
				return (sequence [index]);
			} else {
				return (null);
			}
		}

		if (index == (this.index + 1)) {
			XQResultItem item = nextItem();	// index incremented as side-effect

			if (item == null) {
				if ((sequence != null) && (sequence.length != 0)) {
					sequence = new XQResultItem [0];
				}

				return (null);
			} else {
				if ((sequence != null) && (sequence.length != 1)) {
					sequence = new XQResultItem [1];
				}

				sequence [0] = item;
			}
		}

		if (index == this.index) {
			return (sequence [0]);
		}

		throw new IllegalStateException ("Streaming result must be accessed sequentially");
	}

	public XQResultItem nextItem() throws XQException
	{
		if ( ! streaming) {
			index++;

			if (index >= getSize()) {
				return (null);
			}

			return (sequence [index]);
		}

		try {
			if (xdbcResultSequence.hasNext()) {
				xdbcResultSequence.next();

				index++;

				return (new XdbcResultItem (xdbcResultSequence, index, true));
			} else {
				release();

				return (null);
			}
		} catch (XDBCException e) {
			throw new XQException ("Fetching next: " + e, e);
		}
	}

	public String asString () throws XQException
	{
		return (asString (""));
	}

	public String asString (String separator) throws XQException
	{
		StringBuffer sb = new StringBuffer();
		XQResultItem item = null;

		for (int i = 0; ((item = getItem (i)) != null); i++) {
			if ((i != 0) && (separator != null)) {
				sb.append (separator);
			}

			sb.append (item.asString());
		}

		return (sb.toString());
	}

	public String toString()
	{
		try {
			return (asString());
		} catch (XQException e) {
			return ("Caught exception in toString(): " + e);
		}
	}

	public void writeTo (Writer writer) throws IOException, XQException
	{
		writeTo (writer, "");
	}

	public void writeTo (Writer writer, String separator) throws IOException, XQException
	{
		XQResultItem item = null;
		boolean notFirst = false;

		while ((item = nextItem()) != null) {
			if (notFirst) {
				if (separator != null) {
					writer.write (separator);
				}
			} else {
				notFirst = true;
			}

			item.writeTo (writer);
		}

//		for (int i = 0; ((item = getItem (i)) != null); i++) {
//			if ((i != 0) && (separator != null)) {
//				writer.write (separator);
//			}
//
//			item.writeTo (writer);
//		}
	}
}
