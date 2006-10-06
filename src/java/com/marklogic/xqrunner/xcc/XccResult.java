package com.marklogic.xqrunner.xcc;

import com.marklogic.xcc.ResultItem;
import com.marklogic.xcc.ResultSequence;
import com.marklogic.xqrunner.XQException;
import com.marklogic.xqrunner.XQResult;
import com.marklogic.xqrunner.XQResultItem;

import java.io.IOException;
import java.io.Writer;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: Oct 4, 2006
 * Time: 5:07:15 PM
 */
public class XccResult implements XQResult
{
	private final boolean streaming;
	private final ResultSequence resultSequence;
	private final XQResultItem [] sequence;
	private XQResultItem currItem = null;
	private int index = -1;

	public XccResult (ResultSequence resultSequence)
	{
		this.resultSequence = resultSequence;
		this.streaming = ! resultSequence.isCached();

		if (streaming) {
			sequence = new XQResultItem [0];
		} else {
			ResultItem [] resultItems = resultSequence.toResultItemArray();

			sequence = new XQResultItem [resultItems.length];

			for (int i = 0; i < resultItems.length; i++) {
				sequence [i] = new XccResultItem (resultItems [i], i);
			}
		}
	}

	public boolean isStreaming()
	{
		return streaming;
	}

	public void release()
	{
		resultSequence.close();
	}

	public int getSize()
	{
		if (streaming) {
			return -1;
		} else {
			return sequence.length;
		}
	}

	public XQResultItem [] getItems()
	{
		if (streaming) {
			XQResultItem [] seq;

			if (currItem == null) {
				seq = new XQResultItem [0];
			} else {
				seq = new XQResultItem [] { currItem };
			}

			return seq;
		}

		return (XQResultItem[]) sequence.clone();
	}

	public XQResultItem getItem (int index) throws XQException
	{
		if ( ! streaming) {
			if (index < sequence.length) {
				return sequence [index];
			}
			return null;
		}

		if (index == (this.index + 1)) {
			currItem = nextItem();	// this.index incremented as side-effect

			if (currItem == null) return (null);
		}

		if (index == this.index) {
			return currItem;
		}

		throw new IllegalStateException ("Streaming result must be accessed sequentially: requested=" +
			index + ", cursor=" + this.index);
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
			if (resultSequence.hasNext()) {
				ResultItem resItem = resultSequence.next();

				index++;

				return (new XccResultItem (resItem, index));
			} else {
				release();

				return (null);
			}
		} catch (Exception e) {
			// streaming RSs can throw socket-related exceptions
			throw new XQException ("Fetching next: " + e, e);
		}
	}

	public void rewindItems()
	{
		if (streaming && (index != -1)) {
			throw new IllegalStateException ("Cannot rewind a streaming result");
		}

		index = -1;
	}

	public String asString() throws XQException
	{
		return resultSequence.asString();
	}

	public String asString (String separator) throws XQException
	{
		return resultSequence.asString (separator);
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
	}
}
