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
import com.marklogic.xdbc.XDBCStatement;
import com.marklogic.xdbc.XDBCXName;
import com.marklogic.xdmp.XDMPDocInsertStream;
import com.marklogic.xqrunner.XQDocumentMetaData;
import com.marklogic.xqrunner.XQDocumentWrapper;
import com.marklogic.xqrunner.XQException;
import com.marklogic.xqrunner.XQResult;
import com.marklogic.xqrunner.XQRunner;
import com.marklogic.xqrunner.XQVariable;
import com.marklogic.xqrunner.XQVariableType;
import com.marklogic.xqrunner.XQuery;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.GregorianCalendar;

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

	public synchronized void abortQuery()
		throws XQException
	{
		if (statement == null) {
			throw new IllegalStateException ("No active query");
		}

		try {
			statement.cancel();
		} catch (Exception e) {
			throw new XQException (e.getMessage(), e);
		}
	}

	private XQResult runTheQuery (XQuery query, boolean streaming)
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

			return (new XdbcResult (resultSequence, streaming));
		} catch (XDBCException e) {
			throw new XQException (e.getMessage(), e);
		} finally {
			if (streaming) {
				statement = null;
			} else {
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
	}

	public XQResult runQuery (XQuery query) throws XQException
	{
		return runTheQuery (query, false);
	}

	public XQResult runQueryStreaming (XQuery query) throws XQException
	{
		return runTheQuery (query, true);
	}

	public void insertDocument (String uri, XQDocumentWrapper documentWrapper, XQDocumentMetaData metaData)
		throws XQException, IOException
	{
		XDMPDocInsertStream dis = datasource.getDocInsertStream (uri);
		InputStream inputStream = documentWrapper.asStream();
		byte [] buffer = new byte [100000];
		int rc;

		while ((rc = inputStream.read (buffer)) >= 0) {
			dis.write (buffer, 0, rc);
		}

		dis.flush();

		try {
			dis.commit();
		} catch (XDBCException e) {
			throw new XQException (e.getMessage(), e);
		}
	}

	// --------------------------------------------------------------

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
			XDBCXName xName = (namespace == null) ? new XDBCXName (localname) : new XDBCXName (namespace, localname);
//			XDBCXName xName = new XDBCXName ((namespace == null) ? "" : namespace, localname);

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
				statement.setDuration (xName, ((XdbcDurationAdapter) value).getDuration());

			} else if (type == XQVariableType.XDT_DAY_TIME_DURATION) {
				statement.setDayTimeDuration (xName, ((XdbcDurationAdapter) value).getDuration());

			} else if (type == XQVariableType.XDT_YEAR_MONTH_DURATION) {
				statement.setYearMonthDuration (xName, ((XdbcDurationAdapter) value).getDuration());

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
}
