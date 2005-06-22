
	ReadMe for XQRunner

Documentation is pretty light still.  Take a look at
the src/junit tree for usage examples.  The source bundle
comes with a top-level Ant build.xml control file.  Just
type "ant all" to build everything.  The results of the
build are placed in the "deliverable" directory.  The
directory named "buildtmp" is temporary stuff that can
be deleted after the build.

   The binary bundle contains pre-built jar files (the
content of the deliverable directory) which you can use
without doing a build.

   The Javadoc API documentation is included in the
binary distribution zip file as xqrunner-api.zip.

Version 0.7.1
	Update bundled xdbc jars to 2.2-8, which
	will talk to 3.0 servers.
	Tweak provider not found exception text

Version 0.7.0
	Added external variables (parameters) on queries
	Streaming mode for large results
	Document insert
	More junit tests
	Misc fixes and robustitude

Version 0.6.0
	Initial release
	Javadoc comments not finished yet.
	Need some good examples

Rh 11/12/2004
