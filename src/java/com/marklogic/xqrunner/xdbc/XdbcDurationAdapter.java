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

import com.marklogic.xqrunner.XQDuration;
import com.marklogic.xdbc.XDBCDuration;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: Oct 18, 2004
 * Time: 11:57:14 AM
 */
class XdbcDurationAdapter implements XQDuration
{
	private final XDBCDuration duration;

	public XdbcDurationAdapter (XDBCDuration duration)
	{
		this.duration = duration;
	}

	XdbcDurationAdapter (String value)
	{
		this (new XDBCDuration (value));
	}

	XdbcDurationAdapter (boolean positive, int years, int months, int days,
		int hours, int minutes, int seconds, double subseconds)
	{
		this (new XDBCDuration (
			positive ? XDBCDuration.XDBC_DURATION_POSITIVE : XDBCDuration.XDBC_DURATION_NEGATIVE,
			years, months, days, hours, minutes, seconds, subseconds));
	}

	XDBCDuration getDuration()
	{
		return (new XDBCDuration (duration.getSign(), duration.getYears(),
			duration.getMonths(), duration.getDays(), duration.getHours(),
			duration.getMinutes(), duration.getSeconds(), duration.getSubSeconds()));
	}

	public boolean isNegative ()
	{
		return (duration.getSign() == XDBCDuration.XDBC_DURATION_NEGATIVE);
	}

	public int getYears ()
	{
		return duration.getYears ();
	}

	public int getMonths ()
	{
		return duration.getMonths ();
	}

	public int getDays ()
	{
		return duration.getDays ();
	}

	public int getHours ()
	{
		return duration.getHours ();
	}

	public int getMinutes ()
	{
		return duration.getMinutes ();
	}

	public int getSeconds ()
	{
		return duration.getSeconds ();
	}

	public double getSubSeconds ()
	{
		return duration.getSubSeconds ();
	}

	public String toString ()
	{
		return duration.toString ();
	}
}
