
/*------------------------------------------------------------------------
				  COPYRIGHT
--------------------------------------------------------------------------
	  Copyright (C) 1995-2000
	  Jeff Kotula
	  All Rights Reserved.
--------------------------------------------------------------------------
				   FILE LOG
--------------------------------------------------------------------------

    Source code for the cocoon utility.  First phase processing for
    producing html pages from C++ header files.

------------------------------------------------------------------------*/

#include    "cobweb.h"


int debug      = FALSE; // global debug flag

int linenumber = 0;	// line number in file



/*------------------------------------------------------------------------
    Member functions for cross-reference tracker.
------------------------------------------------------------------------*/
Xref::Xref( char* indexname, char* keyname, char* familyname,
	    char* filename, char* libname, char* xrefurl, Customize& custom ) :
    _enumcount( 0 )
{
	int	i;	// looping index

    _index.open( indexname, ios::app );
    if ( ! _index )
	cout << "Can't open class index file, " << indexname
	     << ",  for appending." << endl;
    _keywords.open( keyname, ios::app );
    if ( ! _keywords )
	cout << "Can't open keyword index file, " << keyname
	     << ", for appending." << endl;
    _family.open( familyname, ios::app );
    if ( ! _family )
	cout << "Can't open inheritance-family data file, " << familyname
	     << ", for appending." << endl;
    if ( custom.ForUnimax() )
    {
	_lispdump.open( "lispdump.lsp", ios::app );
	if ( ! _lispdump )
	    cout << "Can't open lispdump.lsp for appending." << endl;
    }
    _filename.Append( filename );
    _libname.Append( libname );
    _xrefurl.Append( xrefurl );
    for ( i = 0; _xrefurl[i]; i++ )
	if ( _xrefurl[i] == '\\' )
	    _xrefurl[i] = '/';
} // constructor


/*------------------------------------------------------------------------
    Prints a usage error message.
------------------------------------------------------------------------*/
static void
UsageError()
{
cout << "Usage  : cobweb  [options]  libraryname  headerfile" << endl;
cout << "Version: " << VERSION << endl;
cout << "Options:" << endl;
cout << "    -d		    Run in debug mode\n";
cout << "    -p		    Output documentation as pre-formatted text\n";
cout << "    -l linkfile    Specifies the cross-class link file to add to\n";
cout << "    -k keyfile	    Specifies the master keyword file to add to\n";
cout << "    -f familyfile  Specifies the inheritance-family data file to\n";
cout << "		    add to\n";
cout << "    -w webpath	    Specifies the path for links to the pages\n";
cout << "		    produced from the current file\n";
cout << "    -s sourcepath  Specifies the path for links to this source code\n";
cout << "		    file\n";
cout << "    -c customfile  Specifies the customization data file\n";
cout << "    -x xrefurl	    Specifies the URL for the keyword cross-\n";
cout << "		    reference page\n";
cout << endl;
THROW("Terminated.");
} // UsageError


/*------------------------------------------------------------------------

    MAIN

------------------------------------------------------------------------*/
int
main( int argc, char* argv[] )
{
TRY
{
    StringBuff  inbuff;		// input line buffer
    ifstream	infile;		// input file
    char*   customfile = NULL;  // customization file
    char*   headerpath = NULL;  // header file path
    char*   webpath    = NULL;  // web path prefix
					// cross-class link file
    char*   linkfile   = "classes.xrf";
					// keyword index data file
    char*   keyfile    = "keyword.xrf";
					// inheritance family data file
    char*   familyfile = "family.xrf";
					// URL for cross-reference page
    char*   xrefurl    = "keyweb.html";
    int	    i;		// looping index
    int	    forcedexit;	    // section forcing an exit from itself?
    int	    docpreformatted;	// is documentation preformatted?


    //
    //  Get command line garbage.
    //
    if ( argc < 3 )
	UsageError();
    docpreformatted = FALSE;
    i = 1;
    while ( i < argc-2 )
    {
	if ( strcmp( argv[i], "-d" ) == 0 )
	    debug = TRUE;
	else if ( strcmp( argv[i], "-p" ) == 0 )
	    docpreformatted = TRUE;
	else if ( strcmp( argv[i], "-c" ) == 0 )
	{
	    if ( i == argc-2 )
		UsageError();
	    i++;
	    customfile = argv[i];
	}
	else if ( strcmp( argv[i], "-s" ) == 0 )
	{
	    if ( i == argc-2 )
		UsageError();
	    i++;
	    headerpath = argv[i];
	}
	else if ( strcmp( argv[i], "-w" ) == 0 )
	{
	    if ( i == argc-2 )
		UsageError();
	    i++;
	    webpath = argv[i];
	}
	else if ( strcmp( argv[i], "-l" ) == 0 )
	{
	    if ( i == argc-2 )
		UsageError();
	    i++;
	    linkfile = argv[i];
	}
	else if ( strcmp( argv[i], "-k" ) == 0 )
	{
	    if ( i == argc-2 )
		UsageError();
	    i++;
	    keyfile = argv[i];
	}
	else if ( strcmp( argv[i], "-f" ) == 0 )
	{
	    if ( i == argc-2 )
		UsageError();
	    i++;
	    familyfile = argv[i];
	}
	else if ( strcmp( argv[i], "-x" ) == 0 )
	{
	    if ( i == argc-2 )
		UsageError();
	    i++;
	    xrefurl = argv[i];
	}
	else
	    UsageError();
	i++;
    }

    cout << "\t" << argv[argc-1] << "..." << endl;

    //
    //  Create stuff that depends on command line.
    //
				    // user customization doo-dads
    Customize   custom( docpreformatted, customfile, headerpath,
			webpath, NULL );

				    // cross-reference environment
    Xref    xref( linkfile, keyfile, familyfile, argv[argc-1],
		  argv[argc-2], xrefurl, custom );


    //
    //  Create the outermost file handler.
    //
    HeaderFile  handler( xref, custom, argv[argc-2] );


    //
    //  Go through the file.
    //
    infile.open( argv[argc-1], ios::in );
    if ( ! infile )
    {
	cout << "Couldn't open input file " << argv[argc-1]
	     << " for reading." << endl;
	return -1;
    }
    linenumber = 0;
    while ( inbuff.GetLine( infile, FALSE, FALSE ) )
    {
	linenumber++;

	handler.Control( inbuff, forcedexit );
	if ( forcedexit )
	    break;

    } // while

}
CATCH( char* str )
{
#ifdef  NOEXCEPTIONS
	const char* str="Unknown";
#endif
    cout << "EXCEPTION: " << str << endl;
    return -1;
}

return 0;

} // main

