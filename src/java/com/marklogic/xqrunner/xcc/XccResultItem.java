package com.marklogic.xqrunner.xcc;

import com.marklogic.xcc.ResultItem;
import com.marklogic.xcc.types.*;
import com.marklogic.xqrunner.XQException;
import com.marklogic.xqrunner.XQResultItem;
import com.marklogic.xqrunner.XQVariableType;

import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: Oct 4, 2006
 * Time: 6:15:13 PM
 */
public class XccResultItem implements XQResultItem
{
	private final ResultItem resultItem;
	private final ItemType type;
	private final int index;
	private final boolean streaming;

	public XccResultItem (ResultItem resultItem, int index)
	{
		this.resultItem = resultItem;
		this.type = resultItem.getItemType();
		this.index = index;
		this.streaming = ! resultItem.isCached();
	}

	public int getIndex()
	{
		return index;
	}

	public boolean isStreaming()
	{
		return streaming;
	}

	public boolean isNode()
	{
		return type.isNode();
	}

	public XQVariableType getType()
	{
		return XccSyncRunner.xqTypeFromXccType (type);
	}

	public Object asObject()
	{
		resultItem.cache();

		XdmItem item = resultItem.getItem();

		if (type.isNode()) return item.asString();

		if (item instanceof XSString) return item.asString();
		if (item instanceof XSAnyURI) return item.asString();
		if (item instanceof XSQName) return item.asString();
		if (item instanceof XdmText) return item.asString();

		if (item instanceof XSInteger) return ((XSInteger) item).asBigInteger();
		if (item instanceof XSDecimal) return ((XSDecimal) item).asBigDecimal();
		if (item instanceof XSDouble) return ((XSDouble) item).asDouble();
		if (item instanceof XSFloat) return ((XSFloat) item).asFloat();
		if (item instanceof XSBoolean) return ((XSBoolean) item).asBoolean();

		if (item instanceof XSDate) return ((XSDate) item).asDate();
		if (item instanceof XSDateTime) return ((XSDateTime) item).asDate();
		if (item instanceof XSTime) return ((XSTime) item).asDate();

		if (item instanceof XSGDay) return ((XSGDay) item).asGregorianCalendar();
		if (item instanceof XSGMonth) return ((XSGMonth) item).asGregorianCalendar();
		if (item instanceof XSGMonthDay) return ((XSGMonthDay) item).asGregorianCalendar();
		if (item instanceof XSGYear) return ((XSGYear) item).asGregorianCalendar();
		if (item instanceof XSGYearMonth) return ((XSGYearMonth) item).asGregorianCalendar();

		if (item instanceof XSDuration) return new XccDurationAdapter (((XSDuration) item).asDuration());
		if (item instanceof XDTDayTimeDuration) return new XccDurationAdapter (((XDTDayTimeDuration) item).asDuration());
		if (item instanceof XDTYearMonthDuration) return new XccDurationAdapter (((XDTYearMonthDuration) item).asDuration());

		if (item instanceof XdmBinary) return ((XdmBinary) item).asBinaryData();
		if (item instanceof XSHexBinary) return ((XSHexBinary) item).asBinaryData();
		if (item instanceof XSBase64Binary) return ((XSBase64Binary) item).asBinaryData();


		throw new IllegalStateException ("Unexpected result type: " + type);

	}

	public String asString()
	{
		return resultItem.asString();
	}

	public Document asW3cDom() throws XQException
	{
		if ( ! isNode()) {
			throw new XQException ("Result item is not a node");
		}

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance ();

		try {
			return (factory.newDocumentBuilder().parse (new InputSource (resultItem.asReader())));
		} catch (SAXException e) {
			throw new XQException ("SAX Exception parsing result", e);
		} catch (Exception e) {
			throw new XQException ("Exception building W3C DOM", e);
		}
	}

	public org.jdom.Document asJDom() throws XQException
	{
		if ( ! isNode()) {
			throw new XQException ("Result item is not a node");
		}

		try {
			return (new SAXBuilder().build (resultItem.asReader()));
		} catch (JDOMException e) {
			throw new XQException ("Problem during JDOM build", e);
		} catch (IOException e) {
			throw new XQException ("IO problem during JDOM build", e);
		}
	}

	public Reader asReader() throws XQException
	{
		return resultItem.asReader();
	}

	public InputStream asStream() throws XQException
	{
		return resultItem.asInputStream();
	}

	public byte [] asBytes() throws IOException, XQException
	{
		if (resultItem.getItem() instanceof XdmBinary) {
			return ((XdmBinary) resultItem.getItem()).asBinaryData();
		}

		if (resultItem.getItem() instanceof XSHexBinary) {
			return ((XSHexBinary) resultItem.getItem()).asBinaryData();
		}

		if (resultItem.getItem() instanceof XSBase64Binary) {
			return ((XSBase64Binary) resultItem.getItem()).asBinaryData();
		}

		InputStream is = resultItem.asInputStream();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte [] buffer = new byte [10240];
		int rc;

		while ((rc = is.read (buffer, 0, buffer.length)) > 0) {
			bos.write (buffer, 0, rc);
		}

		bos.flush();

		return bos.toByteArray();
	}

	public void writeTo (Writer writer) throws IOException, XQException
	{
		resultItem.writeTo (writer);
	}

	public void streamTo (OutputStream stream) throws IOException, XQException
	{
		resultItem.writeTo (stream);
	}
}
