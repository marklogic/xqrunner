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

import com.marklogic.xdbc.XDBCException;
import com.marklogic.xdbc.XDBCResultSequence;
import com.marklogic.xdbc.XDBCSchemaTypes;
import com.marklogic.xqrunner.XQException;
import com.marklogic.xqrunner.XQResultItem;
import com.marklogic.xqrunner.XQVariableType;
import com.marklogic.xqrunner.generic.Base64;

import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: Oct 18, 2004
 * Time: 11:36:02 AM
 */
class XdbcResultItem implements XQResultItem
{
	private XQVariableType type;
	private XDBCResultSequence xdbcResultSequence = null;
	private Object object = null;
	private String string = null;
	private org.jdom.Document jdom = null;
	private org.w3c.dom.Document w3cDom = null;
	private int index;
	private boolean streaming = false;

	// ------------------------------------------------------------

	/**
	 * Reads and stores the current item from the given ResultSequence.
	 * @param xdbcResultSequence The source of the data.
	 * @param index The logical index of this item, relative to others in the sequence.
	 * @throws com.marklogic.xdbc.XDBCException If there is a problem obtaining the data.
	 */
	XdbcResultItem (XDBCResultSequence xdbcResultSequence, int index, boolean streaming)
		throws XDBCException
	{
		this.index = index;

		type = resultXQType (xdbcResultSequence);

		if (streaming && (isStreamable (type))) {
			this.streaming = true;
			this.xdbcResultSequence = xdbcResultSequence;
		} else {
			bufferValue (xdbcResultSequence);
		}
	}

	private boolean isStreamable (XQVariableType type)
	{
		if ((type == XQVariableType.NODE) ||
			// XDBC won't stream strings
//			(type == XQVariableType.XS_STRING) ||
			(type == XQVariableType.BINARY) ||
			(type == XQVariableType.TEXT) ||
			(type == XQVariableType.XS_BASE64BINARY) ||
			(type == XQVariableType.XS_HEXBINARY))
		{
			return (true);
		}

		return (false);
	}

	// ------------------------------------------------------------

	public boolean isStreaming ()
	{
		return (streaming);
	}

	public XQVariableType getType()
	{
		return (type);
	}

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
		return (type == XQVariableType.NODE);
	}

	/**
	 * @return The value of this result item as a generic Object.
	 */
	public Object asObject()
	{
		if (object == null) {
			bufferValue (xdbcResultSequence);
		}

		return (object);
	}

	/**
	 * @return The value of this result item as a String.
	 */
	public String asString()
	{
		if (string == null) {
			bufferValue (xdbcResultSequence);
		}

		return (string);
	}

	/**
	 * @return This result item as a W3C DOM tree.
	 * @throws com.marklogic.xqrunner.XQException If there is a problem converting this
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
	 * @throws com.marklogic.xqrunner.XQException If there is a problem converting this
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
	 * @throws XQException If there is a problem converting the
	 *  value to a Reader.
	 */
	public Reader asReader() throws XQException
	{
		if (streaming) {
			try {
				return (xdbcResultSequence.getReader());
			} catch (XDBCException e) {
				throw new XQException ("Fetching XDBC Reader: " + e, e);
			}
		}

		return (new StringReader (asString()));
	}

	/**
	 * Write the String representation of this item to the
	 * provided Writer
	 * @param writer The Writer to send the value to
	 * @throws IOException If there is a problem writing.
	 * @throws XQException If there is a problem converting the
	 *  value to a Reader.
	 */
	public void writeTo (Writer writer)
		throws IOException, XQException
	{
		Reader reader = asReader();
		char buffer [] = new char [100000];
		int rc;

		while ((rc = reader.read (buffer)) > 0) {
			writer.write (buffer, 0, rc);
		}

		reader.close();
	}

	public InputStream asStream()
		throws XQException
	{
		if (streaming) {
			try {
				if (type == XQVariableType.BINARY) {
					return (xdbcResultSequence.getBinary().asInputStream());
				} else {
					return (xdbcResultSequence.getInputStream());
				}
			} catch (XDBCException e) {
				throw new XQException ("Fetching XDBC InputStream: " + e, e);
			}
		}

		return (new ByteArrayInputStream (asBytes()));
	}

	public byte [] asBytes() throws XQException
	{
		if (streaming) {
			bufferValue (xdbcResultSequence);
		}

		if (object instanceof byte[]) {
			return (byte[]) object;
		}

		return (asString().getBytes());
	}

	public void streamTo (OutputStream outStream)
		throws IOException, XQException
	{
		InputStream inStream = asStream();
		byte buffer [] = new byte [100000];
		int rc;

		while ((rc = inStream.read (buffer)) > 0) {
			outStream.write (buffer, 0, rc);
		}

		inStream.close();
	}

	// -------------------------------------------------------

	private void bufferValue (XDBCResultSequence xdbcResultSequence)
	{
		streaming = false;

		if (type == XQVariableType.NODE) {
			XDBCSchemaTypes.Node node = xdbcResultSequence.getNode();

			string = node.asString();
			object = node;

			return;
		}

		if (type == XQVariableType.XS_STRING) {
			object = string = xdbcResultSequence.getString().asString();

			return;
		}

		if (type == XQVariableType.TEXT) {
			object = string = xdbcResultSequence.getText().asString();

			return;
		}

		if (type == XQVariableType.BINARY) {
			object = xdbcResultSequence.getBinary().as_byte_array();
			string = new String ((byte []) object);

			return;
		}

		if (type == XQVariableType.XS_BASE64BINARY) {
			string = new String (xdbcResultSequence.getBase64Binary().as_byte_array());
			// TODO: Below is the preferred API, coming in 2.2-3+
//			string = xdbcResultSequence.getBase64Binary().asString();
			object = Base64.decode (string);

			return;
		}

		if (type == XQVariableType.XS_HEXBINARY) {
			string = xdbcResultSequence.getHexBinary().asString();
			object = decodeHexString (string);

			return;
		}

		object = resultAsObject (xdbcResultSequence);
		string = object.toString();
	}

	private org.jdom.Document toJDom (String string)
		throws XQException
	{
		if (isNode() == false) {
			throw new XQException ("Result item is not a node");
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
			throw new XQException ("Result item is not a node");
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

	private XQVariableType resultXQType (XDBCResultSequence xdbcResultSequence) throws XDBCException
	{
		switch (xdbcResultSequence.getItemType ()) {
		case XDBCResultSequence.XDBC_Node:
			return (XQVariableType.NODE);

		case XDBCResultSequence.XDBC_Text:
			return (XQVariableType.TEXT);

		// Dates
		case XDBCResultSequence.XDBC_Date:
			return (XQVariableType.XS_DATE);

		case XDBCResultSequence.XDBC_DateTime:
			return (XQVariableType.XS_DATE_TIME);

		case XDBCResultSequence.XDBC_Time:
			return (XQVariableType.XS_TIME);

		// Gregorian Dates
		case XDBCResultSequence.XDBC_GDay:
			return (XQVariableType.XS_GDAY);

		case XDBCResultSequence.XDBC_GMonth:
			return (XQVariableType.XS_GMONTH);

		case XDBCResultSequence.XDBC_GMonthDay:
			return (XQVariableType.XS_GMONTHDAY);

		case XDBCResultSequence.XDBC_GYear:
			return (XQVariableType.XS_GYEAR);

		case XDBCResultSequence.XDBC_GYearMonth:
			return (XQVariableType.XS_GYEARMONTH);

		// Durations
		case XDBCResultSequence.XDBC_Duration:
			return (XQVariableType.XS_DURATION);

		case XDBCResultSequence.XDBC_DayTimeDuration:
			return (XQVariableType.XDT_DAY_TIME_DURATION);

		case XDBCResultSequence.XDBC_YearMonthDuration:
			return (XQVariableType.XDT_YEAR_MONTH_DURATION);


		// Basic types
		case XDBCResultSequence.XDBC_Boolean:
			return (XQVariableType.XS_BOOLEAN);

		case XDBCResultSequence.XDBC_Double:
			return (XQVariableType.XS_DOUBLE);

		case XDBCResultSequence.XDBC_Float:
			return (XQVariableType.XS_FLOAT);

		case XDBCResultSequence.XDBC_Decimal:
			return (XQVariableType.XS_DECIMAL);

		case XDBCResultSequence.XDBC_Integer:
			return (XQVariableType.XS_INTEGER);

		// String-ish types
		case XDBCResultSequence.XDBC_String:
			return (XQVariableType.XS_STRING);

		case XDBCResultSequence.XDBC_AnyURI:
			return (XQVariableType.XS_ANY_URI);

		case XDBCResultSequence.XDBC_QName:
			return (XQVariableType.XS_QNAME);

		// Binary types
		case XDBCResultSequence.XDBC_Base64Binary:
			return (XQVariableType.XS_BASE64BINARY);

		case XDBCResultSequence.XDBC_HexBinary:
			return (XQVariableType.XS_HEXBINARY);

		case XDBCResultSequence.XDBC_Binary:
			return (XQVariableType.BINARY);

		default:
			throw new XDBCException ("Unexpected result type: " + xdbcResultSequence.getItemType());
		}
	}

	/**
	 * Encapsulate the current element of an XDBC ResultSequence as an object.
	 * @param xdbcResultSequence An active XDBCResultSequence object.
	 * @return An Object representing the current item.
	 * @throws IllegalStateException If an unrecognized XDBC type appears while getting the value.
	 */
	private Object resultAsObject (XDBCResultSequence xdbcResultSequence)
	{
		switch (xdbcResultSequence.getItemType ()) {
//		case XDBCResultSequence.XDBC_Node:
//			return (xdbcResultSequence.getNode());
//
//		case XDBCResultSequence.XDBC_Text:
//			return (xdbcResultSequence.getText().asString());

		// Dates
		case XDBCResultSequence.XDBC_Date:
			return (xdbcResultSequence.getDate().asDate());

		case XDBCResultSequence.XDBC_DateTime:
			return (xdbcResultSequence.getDateTime().asDate());

		case XDBCResultSequence.XDBC_Time:
			return (xdbcResultSequence.getTime().asDate());

		// Gregorian Dates
		case XDBCResultSequence.XDBC_GDay:
			return (xdbcResultSequence.getGDay().asGregorianCalendar());

		case XDBCResultSequence.XDBC_GMonth:
			return (xdbcResultSequence.getGMonth().asGregorianCalendar());

		case XDBCResultSequence.XDBC_GMonthDay:
			return (xdbcResultSequence.getGMonthDay().asGregorianCalendar());

		case XDBCResultSequence.XDBC_GYear:
			return (xdbcResultSequence.getGYear().asGregorianCalendar());

		case XDBCResultSequence.XDBC_GYearMonth:
			return (xdbcResultSequence.getGYearMonth().asGregorianCalendar());

		// Durations
		case XDBCResultSequence.XDBC_Duration:
			return (new XdbcDurationAdapter (xdbcResultSequence.getDuration().asDuration()));

		case XDBCResultSequence.XDBC_DayTimeDuration:
			return (new XdbcDurationAdapter (xdbcResultSequence.getDayTimeDuration().asDuration()));

		case XDBCResultSequence.XDBC_YearMonthDuration:
			return (new XdbcDurationAdapter (xdbcResultSequence.getYearMonthDuration().asDuration()));


		// Basic types
		case XDBCResultSequence.XDBC_Boolean:
			return (xdbcResultSequence.getBoolean().asBoolean());

		case XDBCResultSequence.XDBC_Double:
			return (xdbcResultSequence.getDouble().asDouble());

		case XDBCResultSequence.XDBC_Float:
			return (xdbcResultSequence.getFloat().asFloat());

		case XDBCResultSequence.XDBC_Decimal:
			return (xdbcResultSequence.getDecimal().asBigDecimal());

		case XDBCResultSequence.XDBC_Integer:
			return (xdbcResultSequence.getInteger().asBigInteger());

		// String-ish types
		case XDBCResultSequence.XDBC_String:
			return (xdbcResultSequence.getString().asString());

		case XDBCResultSequence.XDBC_AnyURI:
			return (xdbcResultSequence.getAnyURI().asString());

		case XDBCResultSequence.XDBC_QName:
			return (xdbcResultSequence.getQName().asString());

		// Binary types
//		case XDBCResultSequence.XDBC_Base64Binary:
//			return (xdbcResultSequence.getBase64Binary().as_byte_array());
//
//		case XDBCResultSequence.XDBC_HexBinary:
//			return (xdbcResultSequence.getHexBinary().asString());
//
//		case XDBCResultSequence.XDBC_Binary:
//			return (xdbcResultSequence.getBinary().as_byte_array());

		default:
			throw new IllegalStateException ("Unexpected result type: " + xdbcResultSequence.getItemType());
		}
	}

	// -----------------------------------------------------

	protected static byte [] decodeHexString (String hexDigits)
	{
		ByteArrayOutputStream bytes = new ByteArrayOutputStream (hexDigits.length() / 2);
		char [] chars = hexDigits.toCharArray();
		char highDigit = 0;

		for (int i = 0; i < chars.length; i++) {
			char c = chars [i];

			if (Character.isWhitespace (c)) {
				continue;
			}

			if ( ! isHexDigit (c)) {
				continue;
			}

			if (highDigit == 0) {
				highDigit = c;
				continue;
			}

			bytes.write (byteValue (highDigit, c));

			highDigit = 0;
		}

		return (bytes.toByteArray());
	}

	protected static int byteValue (char highDigit, char lowDigit)
	{
		return ((hexValue (highDigit) << 4) + hexValue (lowDigit));
	}

	private static boolean isHexDigit (char c)
	{
		return (hexValue (c) != -1);
	}

	protected static int hexValue (char ch)
	{
		char c = Character.toLowerCase (ch);

		if ((c >= 'a') && (c <= 'f')) {
			return (c - 'a' + 10);
		}

		if ((c >= '0') && (c <= '9')) {
			return (c - '0');
		}

		return (-1);
	}
}
