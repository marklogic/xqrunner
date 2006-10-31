
	ReadMe for XQRunner

Documentation is pretty light still.  Take a look at
the src/junit tree for usage examples.  The source bundle
comes with a top-level Ant build.xml control file.  Just
type "ant all" to build everything.  The results of the
build are placed in the "deliverable" directory.  The
directory named "buildtmp" is temporary stuff that can
be deleted after the build.

   The binary bundle contains a pre-built XQRunner jar
file which you can use without doing a build.  It also
includes jdom.jar.  The jar(s) for XCC or XDBC are not
included in the binary bundle.  Please download the latest
version of XCC (preferred) or XDBC (if you must) from
http://developer.marklogic.com/download/

   The Javadoc API documentation is included in the
binary distribution zip file as xqrunner-api.zip.

Version 0.8.1 (10/30/2006)
	Explicitly build for JDK 1.4 target

Version 0.8.0 (10/29/2006)
	Add an XCC back-end provider (new default)
	Auto-detect XDBC or XCC if only is in the classpath
	As-of this version, XDBC/XCC jars are no longer
	bundled with XQRunner.

Version 0.7.2 (7/15/2005)
	Return BigInteger instance rather than Integer for xs:integer
	Updated junit tests

Version 0.7.1 (6/22/2005)
	Update bundled xdbc jars to 2.2-8, which
	will talk to 3.0 servers.
	Tweak provider not found exception text

Version 0.7.0 (11/12/2004)
	Added external variables (parameters) on queries
	Streaming mode for large results
	Document insert
	More junit tests
	Misc fixes and robustitude

Version 0.6.0
	Initial release
	Javadoc comments not finished yet.
	Need some good examples

