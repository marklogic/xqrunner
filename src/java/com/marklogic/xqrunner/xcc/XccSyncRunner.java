package com.marklogic.xqrunner.xcc;

import com.marklogic.xcc.Content;
import com.marklogic.xcc.ContentCreateOptions;
import com.marklogic.xcc.ContentFactory;
import com.marklogic.xcc.Request;
import com.marklogic.xcc.RequestOptions;
import com.marklogic.xcc.Session;
import com.marklogic.xcc.exceptions.RequestException;
import com.marklogic.xcc.types.ItemType;
import com.marklogic.xcc.types.ValueType;
import com.marklogic.xqrunner.XQDocumentMetaData;
import com.marklogic.xqrunner.XQDocumentWrapper;
import com.marklogic.xqrunner.XQException;
import com.marklogic.xqrunner.XQResult;
import com.marklogic.xqrunner.XQRunner;
import com.marklogic.xqrunner.XQVariable;
import com.marklogic.xqrunner.XQVariableType;
import com.marklogic.xqrunner.XQuery;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.GregorianCalendar;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: Oct 4, 2006
 * Time: 4:28:49 PM
 */
public class XccSyncRunner implements XQRunner
{
	private final Session session;
	private volatile boolean active = false;

	public XccSyncRunner (Session session)
	{
		this.session = session;
	}

	public XQResult runQuery (XQuery query) throws XQException
	{
		return runTheQuery (query, false);
	}

	public XQResult runQueryStreaming (XQuery query) throws XQException
	{
		return runTheQuery (query, true);
	}

	public void insertDocument (String uri, XQDocumentWrapper documentWrapper,
		XQDocumentMetaData metaData) throws XQException, IOException
	{
		ContentCreateOptions options = new ContentCreateOptions();

		if (documentWrapper.isXml()) options.setFormatXml();
		if (documentWrapper.isText()) options.setFormatText();
		if (documentWrapper.isBinary()) options.setFormatBinary();

		Content content = ContentFactory.newContent (uri, documentWrapper.asStream(), options);

		try {
			active = true;
			session.insertContent (content);
		} catch (RequestException e) {
			throw new XQException (e.getMessage(), e);
		} finally {
			active = false;
		}
	}

	public void abortQuery() throws XQException
	{
		if ( ! active) {
			throw new IllegalStateException ("No active query");
		}

		throw new XQException ("Query abort is not supported by XCC");
	}

	// -----------------------------------------------------

	private XQResult runTheQuery (XQuery query, boolean streaming) throws XQException
	{
		Request request = session.newAdhocQuery (query.getBody());

		if (streaming) {
			RequestOptions options = new RequestOptions();

			options.setCacheResult (false);
			request.setOptions (options);
		}

		setParameters (request, query);

		try {
			active = true;

			return new XccResult (session.submitRequest (request));
		} catch (RequestException e) {
			throw new XQException (e.getMessage(), e);
		} finally {
			active = false;
		}
	}

	private void setParameters (Request request, XQuery query)
	{
		XQVariable [] params = query.getVariables();

		for (int i = 0; i < params.length; i++) {
			XQVariable param = params [i];
			XQVariableType type = param.getType();
			Object value = param.getValue();
			String namespace = param.getNamespace();
			String localname = param.getLocalname();

			// XCC doesn't currently only has factory methods that take String args
			if (type == XQVariableType.XS_DATE) value = dateToString ((Date) value, DATE_FMT_STRING);
			if (type == XQVariableType.XS_DATE_TIME) value = dateToString ((Date) value, DATETIME_FMT_STRING);
			if (type == XQVariableType.XS_TIME) value = dateToString ((Date) value, TIME_FMT_STRING);

			if (type == XQVariableType.XS_GDAY) value = gcalToString ((GregorianCalendar) value, GDAY_FMT_STRING);
			if (type == XQVariableType.XS_GMONTH) value = gcalToString ((GregorianCalendar) value, GMONTH_FMT_STRING);
			if (type == XQVariableType.XS_GMONTHDAY) value = gcalToString ((GregorianCalendar) value, GMONTHDAY_FMT_STRING);
			if (type == XQVariableType.XS_GYEAR) value = gcalToString ((GregorianCalendar) value, GYEAR_FMT_STRING);
			if (type == XQVariableType.XS_GYEARMONTH) value = gcalToString ((GregorianCalendar) value, GYEARMONTH_FMT_STRING);

			// Constrain precision to suppress floating point precision errors
			if (type == XQVariableType.XS_FLOAT) value = new Float (((BigDecimal) value).floatValue());
			if (type == XQVariableType.XS_DOUBLE) value = new Double (((BigDecimal) value).doubleValue());

			request.setNewVariable (namespace, localname, xccTypeFromXqType (type), value);
		}
	}

	private static final String  DATETIME_FMT_STRING = "yyyy-MM-dd'T'HH:mm:ssZ";
	private static final String  DATE_FMT_STRING = "yyyy-MM-ddZ";
	private static final String  TIME_FMT_STRING = "HH:mm:ssZ";
	private static final String  GDAY_FMT_STRING = "---dd";
	private static final String  GMONTH_FMT_STRING = "--MM";
	private static final String  GMONTHDAY_FMT_STRING = "--MM-dd";
	private static final String  GYEAR_FMT_STRING = "yyyy";
	private static final String  GYEARMONTH_FMT_STRING = "yyyy-MM";
	private static final FieldPosition START_POS = new FieldPosition (0);


	private String dateToString (Date date, String fmt)
	{
		StringBuffer sb = new StringBuffer();
		SimpleDateFormat sdf = new SimpleDateFormat (fmt);

		sdf.format (date, sb, START_POS);
		sb.insert (sb.length() - 2, ":");

		return sb.toString();
	}

	private String gcalToString (GregorianCalendar gcal, String fmt)
	{
		Date date = gcal.getTime();
		StringBuffer sb = new StringBuffer();
		SimpleDateFormat sdf = new SimpleDateFormat (fmt);

		sdf.format (date, sb, START_POS);

		return sb.toString();
	}

	// ------------------------------------------------------------------

	private static final Map xqToXccTypeMap;
	private static final Map xccToXqTypeMap;

	static {
		xqToXccTypeMap = new HashMap();

		xqToXccTypeMap.put (XQVariableType.NODE, ValueType.ELEMENT);
		xqToXccTypeMap.put (XQVariableType.BINARY, ValueType.BINARY);
		xqToXccTypeMap.put (XQVariableType.TEXT, ValueType.TEXT);
		xqToXccTypeMap.put (XQVariableType.XS_ANY_URI, ValueType.XS_ANY_URI);
		xqToXccTypeMap.put (XQVariableType.XS_BASE64BINARY, ValueType.XS_BASE64_BINARY);
		xqToXccTypeMap.put (XQVariableType.XS_BOOLEAN, ValueType.XS_BOOLEAN);
		xqToXccTypeMap.put (XQVariableType.XS_DATE, ValueType.XS_DATE);
		xqToXccTypeMap.put (XQVariableType.XS_DATE_TIME, ValueType.XS_DATE_TIME);
		xqToXccTypeMap.put (XQVariableType.XS_DECIMAL, ValueType.XS_DECIMAL);
		xqToXccTypeMap.put (XQVariableType.XS_DOUBLE, ValueType.XS_DOUBLE);
		xqToXccTypeMap.put (XQVariableType.XS_DURATION, ValueType.XS_DURATION);
		xqToXccTypeMap.put (XQVariableType.XS_FLOAT, ValueType.XS_FLOAT);
		xqToXccTypeMap.put (XQVariableType.XS_GDAY, ValueType.XS_GDAY);
		xqToXccTypeMap.put (XQVariableType.XS_GMONTH, ValueType.XS_GMONTH);
		xqToXccTypeMap.put (XQVariableType.XS_GMONTHDAY, ValueType.XS_GMONTH_DAY);
		xqToXccTypeMap.put (XQVariableType.XS_GYEAR, ValueType.XS_GYEAR);
		xqToXccTypeMap.put (XQVariableType.XS_GYEARMONTH, ValueType.XS_GYEAR_MONTH);
		xqToXccTypeMap.put (XQVariableType.XS_HEXBINARY, ValueType.XS_HEX_BINARY);
		xqToXccTypeMap.put (XQVariableType.XS_INTEGER, ValueType.XS_INTEGER);
		xqToXccTypeMap.put (XQVariableType.XS_QNAME, ValueType.XS_QNAME);
		xqToXccTypeMap.put (XQVariableType.XS_STRING, ValueType.XS_STRING);
		xqToXccTypeMap.put (XQVariableType.XS_TIME, ValueType.XS_TIME);
		xqToXccTypeMap.put (XQVariableType.XS_UNTYPED_ATOMIC, ValueType.XDT_UNTYPED_ATOMIC);
		xqToXccTypeMap.put (XQVariableType.XDT_DAY_TIME_DURATION, ValueType.XDT_DAY_TIME_DURATION);
		xqToXccTypeMap.put (XQVariableType.XDT_YEAR_MONTH_DURATION, ValueType.XDT_YEAR_MONTH_DURATION);

		xccToXqTypeMap = new HashMap();

		for (Iterator it = xqToXccTypeMap.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();

			xccToXqTypeMap.put (entry.getValue(), entry.getKey());
		}
	}

	static ItemType xccTypeFromXqType (XQVariableType xqType)
	{
		ItemType xccType = (ItemType) xqToXccTypeMap.get (xqType);

		if (xccType == null) {
			return ValueType.XDT_UNTYPED_ATOMIC;
		}

		return xccType;
	}

	static XQVariableType xqTypeFromXccType (ItemType xccType)
	{
		XQVariableType xqType = (XQVariableType) xccToXqTypeMap.get (xccType);

		if (xqType == null) {
			return XQVariableType.XS_UNTYPED_ATOMIC;
		}

		return xqType;
	}
}
