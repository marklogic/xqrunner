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

import com.marklogic.xdbc.XDBCConnection;
import com.marklogic.xdbc.XDBCException;
import com.marklogic.xdbc.XDBCResultSequence;
import com.marklogic.xdbc.XDBCSchemaTypes;
import com.marklogic.xdbc.XDBCStatement;
import com.marklogic.xdbc.XDBCXName;
import com.marklogic.xqrunner.XQException;
import com.marklogic.xqrunner.XQResult;
import com.marklogic.xqrunner.XQResultItem;
import com.marklogic.xqrunner.XQRunner;
import com.marklogic.xqrunner.XQuery;
import com.marklogic.xqrunner.XQVariable;
import com.marklogic.xqrunner.XQVariableType;

import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.GregorianCalendar;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * XDBC implementation of a synchronous XQRunner.
 * @author Ron Hitchens
 */
public class XdbcSyncRunner implements XQRunner
{
	private XdbcDataSource datasource;
	private volatile XDBCStatement statement = null;

	public XdbcSyncRunner (XdbcDataSource datasource)
	{
		this.datasource = datasource;
	}

	public XQResult runQuery (XQuery query)
		throws XQException
	{
		XDBCConnection connection = null;
		XDBCResultSequence resultSequence = null;

		try {
			connection = datasource.getConnection();
			statement = connection.createStatement();

			if (query.getTimeout() != -1) {
				statement.setQueryTimeout (query.getTimeout());
			}

			setParameters (statement, query);

			resultSequence = statement.executeQuery (query.getBody());

			return (new ResultImpl (resultSequence));
		} catch (XDBCException e) {
			throw new XQException (e.getMessage(), e);
		} finally {
			if (resultSequence != null) {
				try {
					resultSequence.close();
				} catch (XDBCException e) {
					// nothing
				}
			}
			if (statement != null) {
				try {
					statement.close();
				} catch (XDBCException e) {
					// nothing
				} finally {
					statement = null;
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
	}

	private void setParameters (XDBCStatement statement, XQuery query)
		throws XDBCException
	{
		XQVariable [] params = query.getVariables();

		for (int i = 0; i < params.length; i++) {
			XQVariable param = params[i];
			XQVariableType type = param.getType();
			Object value = param.getValue();
			String namespace = param.getNamespace();
			String localname = param.getLocalname();
//			XDBCXName xName = (namespace == null) ? new XDBCXName (localname) : new XDBCXName (namespace, localname);
			XDBCXName xName = new XDBCXName ((namespace == null) ? "" : namespace, localname);

			if (type == XQVariableType.XS_STRING) {
				statement.setString (xName, (String) value);

			} else if (type == XQVariableType.XS_UNTYPED_ATOMIC) {
				statement.setUntypedAtomic (xName, (String) value);

			} else if (type == XQVariableType.XS_DATE_TIME) {
				if (value instanceof Date) {
					statement.setDateTime (xName, (Date) value);
				} else {
					statement.setDateTime (xName, (String) value);
				}

			} else if (type == XQVariableType.XS_DATE) {
				if (value instanceof Date) {
					statement.setDate (xName, (Date) value);
				} else {
					statement.setDate (xName, (String) value);
				}

			} else if (type == XQVariableType.XS_TIME) {
				if (value instanceof Date) {
					statement.setTime (xName, (Date) value);
				} else {
					statement.setTime (xName, (String) value);
				}

			} else if (type == XQVariableType.XS_DURATION) {
				statement.setDuration (xName, ((XdbcDataSource.DurationAdapter) value).getDuration());

			} else if (type == XQVariableType.XS_DAYTIMEDURATION) {
				statement.setDayTimeDuration (xName, ((XdbcDataSource.DurationAdapter) value).getDuration());

			} else if (type == XQVariableType.XS_YEARMONTHDURATION) {
				statement.setYearMonthDuration (xName, ((XdbcDataSource.DurationAdapter) value).getDuration());

			} else if (type == XQVariableType.XS_DECIMAL) {
				statement.setDecimal (xName, (BigDecimal) value);

			} else if (type == XQVariableType.XS_INTEGER) {
				statement.setInteger (xName, (BigInteger) value);

			} else if (type == XQVariableType.XS_BOOLEAN) {
				statement.setBoolean (xName, ((Boolean) value).booleanValue());

			} else if (type == XQVariableType.XS_DOUBLE) {
				statement.setDouble (xName, ((BigDecimal) value).doubleValue());

			} else if (type == XQVariableType.XS_FLOAT) {
				statement.setFloat (xName, ((BigDecimal) value).floatValue());

			} else if (type == XQVariableType.XS_ANY_URI) {
				statement.setAnyURI (xName, (String) value);

			} else if (type == XQVariableType.NULL) {
				statement.setNull (xName);

			} else if (type == XQVariableType.XS_QNAME) {
				statement.setQName (xName, (String) value);

			} else if (type == XQVariableType.XS_GDAY) {
				statement.setGDay (xName, (GregorianCalendar) value);

			} else if (type == XQVariableType.XS_GMONTH) {
				statement.setGMonth (xName, (GregorianCalendar) value);

			} else if (type == XQVariableType.XS_GMONTHDAY) {
				statement.setGMonthDay (xName, (GregorianCalendar) value);

			} else if (type == XQVariableType.XS_GYEAR) {
				statement.setGYear (xName, (GregorianCalendar) value);

			} else if (type == XQVariableType.XS_GYEARMONTH) {
				statement.setGYearMonth (xName, (GregorianCalendar) value);

			} else if (type == XQVariableType.XS_HEXBINARY) {
				statement.setHexBinary (xName, (String) value);

			} else if (type == XQVariableType.XS_BASE64BINARY) {
				statement.setBase64Binary (xName, (String) value);

			} else {
				throw new UnsupportedOperationException ("FIXME: implement '" + type + "'");
			}
		}
	}

	public synchronized void abortQuery()
		throws XQException
	{
		if (statement == null) {
			throw new IllegalStateException ("No active query");
		}

		try {
			statement.cancel ();
		} catch (XDBCException e) {
			throw new XQException (e.getMessage(), e);
		}
	}

	// ------------------------------------------------------------
	// FIXME: break this out to a top-level class?

	private class ResultImpl implements XQResult
	{
		private XQResultItem [] sequence;

		public ResultImpl (XDBCResultSequence xdbcResultSequence)
			throws XDBCException
		{
			List list = new ArrayList();
			int index = 0;

			while (xdbcResultSequence.hasNext()) {
				xdbcResultSequence.next();

				list.add (new ResultItemImpl (xdbcResultSequence, index));

				index++;
			}

			sequence = new ResultItemImpl [list.size()];

			list.toArray (sequence);
		}

		public int getSize ()
		{
			return (sequence.length);
		}

		public XQResultItem [] getItems ()
		{
			return (sequence);
		}

		public XQResultItem getItem (int index)
		{
			return (sequence [index]);
		}

		public String asString ()
		{
			return (asString (""));
		}

		public String asString (String separator)
		{
			StringBuffer sb = new StringBuffer();

			for (int i = 0; i < sequence.length; i++) {
				XQResultItem item = sequence[i];

				if ((i != 0) && (separator != null)) {
					sb.append (separator);
				}

				sb.append (item.asString());
			}

			return (sb.toString());
		}
	}

	private class ResultItemImpl implements XQResultItem
	{
		private XDBCSchemaTypes.Node node;
		private Object object;
		private String string = null;
		private org.jdom.Document jdom = null;
		private org.w3c.dom.Document w3cDom = null;
		private int index;

		// ------------------------------------------------------------

		/**
		 * Reads and stores the current item from the given ResultSequence.
		 * @param xdbcResultSequence The source of the data.
		 * @param index The logical index of this item, relative to others in the sequence.
		 * @throws XDBCException If there is a problem obtaining the data.
		 */
		public ResultItemImpl (XDBCResultSequence xdbcResultSequence, int index)
			throws XDBCException
		{
			this.index = index;

			if (xdbcResultSequence.getItemType() == XDBCResultSequence.XDBC_Node) {
				node = xdbcResultSequence.getNode();
				object = node;
				string = node.asString();
			} else {
				object = resultAsObject (xdbcResultSequence);
			}
		}

		// ------------------------------------------------------------

		/**
		 * @return This item's index.
		 */
		public int getIndex()
		{
			return (index);
		}

		/**
		 * @return True if this result item is a Node, false otherwise.
		 */
		public boolean isNode()
		{
			return (node != null);
		}

		/**
		 * @return The value of this result item as a generic Object.
		 */
		public Object asObject()
		{
			return (object);
		}

		/**
		 * @return The value of this result item as a String.
		 */
		public String asString()
		{
			return ((string != null) ? string : asObject().toString());
		}

		/**
		 * @return This result item as a W3C DOM tree.
		 * @throws XQException If there is a problem converting this
		 *  item to a DOM, or if this item is not a Node.
		 */
		public org.w3c.dom.Document asW3cDom() throws XQException
		{
			if (w3cDom == null) {
				w3cDom = toW3cDom (asString());
			}

			return (w3cDom);
		}

		/**
		 * @return This result item as a JDom tree.
		 * @throws XQException If there is a problem converting this
		 *  item to a DOM, or if this item is not a Node.
		 */
		public org.jdom.Document asJDom()
			throws XQException
		{
			if (jdom == null) {
				jdom = toJDom (asString());
			}

			return (jdom);
		}

		/**
		 * @return This item as a Reader.  This implementation uses
		 *  a StringReader with the getString() value as the source.
		 */
		public Reader asReader()
		{
			return (new StringReader (asString()));
		}

		// -------------------------------------------------------

		private org.jdom.Document toJDom (String string)
			throws XQException
		{
			if (isNode() == false) {
				throw new XQException ("Result is not a node");
			}

			try {
				return (new SAXBuilder().build (new StringReader (string)));
			} catch (JDOMException e) {
				throw new XQException ("Problem during JDOM build", e);
			} catch (IOException e) {
				throw new XQException ("IO problem during JDOM build", e);
			}
		}

		private org.w3c.dom.Document toW3cDom (String string)
			throws XQException
		{
			if (isNode() == false) {
				throw new XQException ("Result is not a node");
			}

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance ();

			try {
				return (factory.newDocumentBuilder().parse (new InputSource (new StringReader (string))));
			} catch (SAXException e) {
				throw new XQException ("SAX Exception parsing result", e);
			} catch (Exception e) {
				throw new XQException ("Exception building W3C DOM", e);
			}
		}

		/**
		 * Encapsulate the current element of an XDBC ResultSequence as an object.
		 * @param xdbcResultSequence An active XDBCResultSequence object.
		 * @return An Object representing the current item.
		 * @throws XDBCException If an XDBC error ocurrs while getting the value.
		 */
		private Object resultAsObject (XDBCResultSequence xdbcResultSequence)
			throws XDBCException
		{
			switch (xdbcResultSequence.getItemType ()) {
			case XDBCResultSequence.XDBC_Boolean:
				return (xdbcResultSequence.getBoolean().asBoolean());

			// FIXME: Handle Gregorian dates
			case XDBCResultSequence.XDBC_Date:
			case XDBCResultSequence.XDBC_DateTime:
			case XDBCResultSequence.XDBC_Time:
				return (xdbcResultSequence.getDate().asDate());

			case XDBCResultSequence.XDBC_Double:
			case XDBCResultSequence.XDBC_Float:
				return (xdbcResultSequence.getDouble().asDouble());

			case XDBCResultSequence.XDBC_Decimal:
				return (xdbcResultSequence.getDecimal().asBigDecimal());

			case XDBCResultSequence.XDBC_Integer:
				return (xdbcResultSequence.getInteger().asInteger());

			case XDBCResultSequence.XDBC_Node:
				return (xdbcResultSequence.getNode());

			case XDBCResultSequence.XDBC_String:
				return (xdbcResultSequence.get_String());

			// FIXME: Handle BLOBs and other sundry types

			default:
				throw new XDBCException ("Unexpected result type: " + xdbcResultSequence.getItemType());
			}
		}
	}
}
