package com.marklogic.xqrunner.apps;

import com.marklogic.xqrunner.XQDataSource;
import com.marklogic.xqrunner.XQException;
import com.marklogic.xqrunner.XQResult;
import com.marklogic.xqrunner.XQVariable;
import com.marklogic.xqrunner.XQVariableType;
import com.marklogic.xqrunner.XQuery;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Run a query on a remote CIS server and return the result.
 * Usage:<br>
 *   (serveruri | -u filenamecontaininguri)<br>
 *   [-o outputfilename]<br>
 *   (-q query | -f filenamecontianingquery | -i moduletoinvoke)<br>
 *   any number of variable declarations:<br>
 *     (xs:schematype)namespace:localname=value<br>
 *     Type is optional, default is xs:untypedAtomic<br>
 *     namespace is optional, default is ""<br>
 *     Entire variable declaration must be one argument, quote if needed.
 * @author Ron Hitchens, Mark Logic
 */
public class XQRun
{
	private XQDataSource dataSource;
	private XQuery query;
	private Writer output;

	public XQRun (Args args)
		throws Exception
	{
		dataSource = AppHelper.createDataSource (args);
		output = setupOutput (args);
		query = buildQuery (dataSource, args);
	}

	public void run() throws XQException, IOException
	{
		XQResult result = dataSource.newSyncRunner().runQuery (query);

		result.writeTo (output);
		output.flush();
	}

	// ----------------------------------------------------------------

	private XQuery buildQuery (XQDataSource dataSource, Args args)
		throws Exception
	{
		String queryText = args.findAndConsumeNamedArg ("-q");
		String moduleName = args.findAndConsumeNamedArg ("-i");

		if (queryText == null) {
			String fileName = args.findAndConsumeNamedArg ("-f");
			queryText = AppHelper.loadStringFromFile (fileName, "\n");

			if (queryText == null) {
				throw new Exception ("Cannot load query from file: " + fileName);
			}
		}

		if ((queryText == null) && (moduleName != null)) {
			queryText = moduleName;

			// TODO: Not finished
			throw new Exception ("Module invoke not yet supported");
		}

		if ((queryText == null) && (moduleName == null)) {
			throw new Exception ("No query or module name provided");
		}

		args.rewind();

		List variables = getVariables (dataSource, args);
		// TODO: handle module invocation vs query execute
		XQuery query = dataSource.newQuery (queryText);

		for (Iterator it = variables.iterator (); it.hasNext ();) {
			query.setVariable ((XQVariable) it.next());
		}

		return (query);
	}

	private Writer setupOutput (Args args) throws IOException
	{
		String filename = args.findAndConsumeNamedArg ("-o");

		if (filename == null) {
			return (new PrintWriter (System.out));
		} else {
			return (new BufferedWriter (new FileWriter (filename)));
		}
	}

	// -----------------------------------------------------------------

	protected static final Pattern varPattern = Pattern.compile (
		"(\\((\\p{Lower}+:\\w+)\\))?\\s*" +	// (xs:some-type), optional
		"((.+):)?\\s*" +			// namespace (not prefix), optional
		"([\\w-]+)\\s*" +			// localname, required
		"\\=\\s*" +				// = sign
		"(.*)\\s*");				// value, required

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
			System.err.println (e.getMessage ());
			System.err.println ("usage: (serverURI | -s URIfilename) [(type)varname=value]* (-f filename | -q query | -i moduleuri) [-o outfilename]");
			System.err.println ("  Form of server URI is: xdbc://user:password@host:port");
			System.err.println ("  Any number of variables may be provided");
			System.err.println ("  With -f, filename contains XQuery code to run");
			System.err.println ("  With -q, query must be quoted as one argument");
			System.err.println ("  With -i, module uri to invoke is given");
			System.err.println ("  Filename of '-' means stdin");

			return;
		}

		try {
			xqRun.run();
		} catch (XQException e) {
			System.err.println ("Could not execute query: " + e);
		} catch (IOException e) {
			System.err.println ("Could not write query output: " + e);
		}
	}
}
