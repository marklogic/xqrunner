package com.marklogic.xqrunner.apps;

import com.marklogic.xqrunner.XQDataSource;
import com.marklogic.xqrunner.XQuery;
import com.marklogic.xqrunner.XQFactory;
import com.marklogic.xqrunner.XQVariable;
import com.marklogic.xqrunner.XQVariableType;
import com.marklogic.xqrunner.XQResult;
import com.marklogic.xqrunner.XQException;

import java.net.URI;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

// TODO: Handle output specification (-o file)
// TODO: Handle input from stdin
// TODO: allow options in more flexible order

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: Dec 16, 2004
 * Time: 2:59:04 PM
 */
public class XQRun
{
	private XQDataSource dataSource;
	private XQuery query;

	public XQRun (Args args)
		throws Exception
	{
		dataSource = buildDataSource (args);
		query = buildQuery (dataSource, args);
	}

	public void run() throws XQException
	{
		XQResult result = dataSource.newSyncRunner().runQuery (query);

		System.out.println (result.asString());
	}

	// ----------------------------------------------------------------

	private XQDataSource buildDataSource (Args args)
		throws Exception
	{
		String uri = args.consumeArg();

		if (uri == null) {
			throw new Exception ("No server URI provided");
		}

		if (uri.equals ("-u")) {
			uri = loadFromFile (args.consumeArg(), "");
		}

		URI serverUri = new URI (uri);

		return new XQFactory().newDataSource (serverUri);
	}

	private XQuery buildQuery (XQDataSource dataSource, Args args)
		throws Exception
	{
		List variables = getVariables (dataSource, args);
		String qswitch = args.nextArg();

		if (qswitch == null) {
			throw new Exception ("Missing query switch (-f or -q)");
		}

		String queryText = null;

		if (qswitch.equals ("-f")) {
			queryText = loadFromFile (args.nextArg(), "\n");

		} else if (qswitch.equals ("-i")) {
			throw new Exception ("Invoke not yet supported");

		} else if (qswitch.equals ("-q")) {
			queryText = args.nextArg();

			if (queryText == null) {
				throw new Exception ("No query body provided");
			}
		}

		XQuery query = dataSource.newQuery (queryText);

		for (Iterator it = variables.iterator (); it.hasNext ();) {
			query.setVariable ((XQVariable) it.next());
		}

		return (query);
	}

	private List getVariables (XQDataSource dataSource, Args args)
	{
		List vars = new ArrayList();

		while (args.hasNext()) {
			XQVariable var = parseVariable (dataSource, args.nextArg());

			if (var == null) {
				args.stepBack();
				break;
			}

			vars.add (var);
		}

		return (vars);
	}

	private String loadFromFile (String path, String sep)
		throws IOException
	{
		if (path == null) {
			return (null);
		}

		BufferedReader reader = new BufferedReader (new FileReader (path));
		StringBuffer sb = new StringBuffer();
		String line = null;

		while ((line = reader.readLine()) != null) {
			sb.append (line).append (sep) ;
		}

		reader.close();

		return (sb.toString());
	}

	protected static final Pattern varPattern = Pattern.compile (
		"(\\((\\p{Lower}+:\\w+)\\))?\\s*" +		// (xs:some-type), optional
		"((.+):)?\\s*" +			// namespace (not prefix), optional
		"([\\w-]+)\\s*" +			// localname, required
		"\\=\\s*" +
		"(.*)\\s*");				// value

	protected static XQVariable parseVariable (XQDataSource dataSource, String arg)
	{
		Matcher matcher = varPattern.matcher (arg);

		if (matcher.matches() == false) {
			return (null);
		}

		XQVariableType type = XQVariableType.forType (matcher.group (2));
		String namespace = matcher.group (4);
		String localname = matcher.group (5);
		String value = matcher.group (6);

		return (dataSource.newVariable (namespace, localname,
			(type == null) ? XQVariableType.XS_UNTYPED_ATOMIC : type, value));
	}

	// -----------------------------------------------------------------

	public static void main (String[] args)
	{
		XQRun xqRun = null;

		try {
			xqRun = new XQRun (new Args (args));

		} catch (IOException e) {
			System.err.println (e.toString());

			return;

		} catch (Exception e) {
			System.err.println ("usage: serverURI [(type)varname=value]* [[-f filename] | [-q query]] [-o outfilename]");
			System.err.println ("  Form of server URI is: xdbc://user:password@host:port");
			System.err.println ("  Any number of variables may be provided");
			System.err.println ("  With -f, filename contains XQuery code to run");
			System.err.println ("  With -q, query must be quoted as one argument");

			return;
		}

		try {
			xqRun.run();
		} catch (XQException e) {
			System.err.println ("Could not execute query: " + e);
		}
	}
}
