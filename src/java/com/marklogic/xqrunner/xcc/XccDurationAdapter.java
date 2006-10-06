package com.marklogic.xqrunner.xcc;

import com.marklogic.xcc.types.Duration;
import com.marklogic.xcc.types.XdmDuration;
import com.marklogic.xqrunner.XQDuration;

import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: Jul 26, 2006
 * Time: 5:10:09 PM
 */
public class XccDurationAdapter implements XQDuration
{
	XdmDuration duration;

	public XccDurationAdapter (XdmDuration duration)
	{
		this.duration = duration;
	}

	public XccDurationAdapter (String duration)
	{
		this (new Duration (duration));
	}

	public XccDurationAdapter (boolean positive, int years, int months,
		int days, int hours, int minutes, int seconds, double subseconds)
	{
		this (new Duration ( ! positive, years, months, days, hours, minutes,
			new BigDecimal ("" + seconds).add (new BigDecimal (subseconds))));
	}

	// ------------------------------------------------------------------
	// Implementation of XQDuration interface

	public boolean isNegative ()
	{
		return duration.isNegative();
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
		return (int) duration.getWholeSeconds();
	}

	public double getSubSeconds()
	{
		BigDecimal secs = duration.getSeconds();
		BigDecimal sub = secs.subtract (new BigDecimal ("" + getSeconds()));

		return sub.doubleValue();
	}

	public String toString ()
	{
		return duration.toString();
	}
}
