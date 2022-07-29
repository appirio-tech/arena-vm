
/*------------------------------------------------------------------------
				  COPYRIGHT
--------------------------------------------------------------------------
	  Copyright (C) 1999-2000
	  Jeff Kotula
	  All Rights Reserved.
--------------------------------------------------------------------------
				   FILE LOG
--------------------------------------------------------------------------

    Source for the utility that modifies all the hrefs so that they
    are relative to the root directory. Assumes that a single href doesn't
    span multiple lines.

------------------------------------------------------------------------*/

#include    "sys.h"
#include    "util.h"


int debug      = FALSE; // global debug flag


/*------------------------------------------------------------------------
    Prints a usage error message.
------------------------------------------------------------------------*/
static void
UsageError()
{
cout << "Usage  : relativize  [options]  fileurl  filename" << endl;
cout << "Version: " << VERSION << endl;
cout << "Options:" << endl;
cout << "    -d		    Run in debug mode\n";
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
    if ( argc < 3 )
	UsageError();

    int	    	i        = 1;			// looping index
    char*	fileurl  = argv[argc-2];	// url to this file
    char*	filename = argv[argc-1];	// name of file to relativize
    FILE*	outfile  = NULL;
    StringBuff	pathAdjust;
    StringBuff	tempfile;
    StringBuff	line;


    //
    //  Get command line garbage.
    //
    while ( i < argc-2 )
    {
	if ( strcmp( argv[i], "-d" ) == 0 )
	    debug = TRUE;
	else
	    UsageError();
	i++;
    }

    				// use url-standard separator
    for ( i = 0; fileurl[i]; i++ )
	if ( fileurl[i] == '\\' )
	    fileurl[i] = '/';

				// make sure that the fileurl starts
				// with WEBROOT
    assert(strncmp("WEBROOT/", fileurl, 8) == 0);

				// figure out the relative-path adjustment up
				// the directories to get to the root
    if ( strcmp(fileurl, "WEBROOT/") == 0 )
    {
	// empty, no adjustment
    }
    else
    {
	pathAdjust.Append("../");
	for ( i = 9; fileurl[i]; i++ )
	{
	    if ( fileurl[i] == '/'  ||  fileurl[i] == '\\' )
	    {
		pathAdjust.Append("../");
	    }
	}
    }

				// open the input file
    ifstream	infile(filename, ios::in);
    if ( ! infile )
    {
	cout << "\tCould not read " << filename << endl;
	return -1;
    }
				// open the temporary output file
    tempfile.Append(filename);
    tempfile.Append(".rel");
    outfile = fopen(tempfile, "w");
    if ( ! outfile )
    {
	cout << "\tCould not write " << tempfile << endl;
	infile.close();
	return -1;
    }

    				// process each line of the file -- replace 
				// "WEBROOT/" with pathAdjust throughout
    while ( line.GetLine(infile) )
    {
	StringBuff	outline;

	i = 0;
	while ( line[i] )
	{
	    if ( line[i]   == 'W'  &&  line[i+1] == 'E'  &&  line[i+2] == 'B' &&
		 line[i+3] == 'R'  &&  line[i+4] == 'O'  &&  line[i+5] == 'O' &&
		 line[i+6] == 'T'  &&  line[i+7] == '/' )
	    {
		outline.Append(pathAdjust);
		i += 7;
	    }
	    else
	    {
		outline.Append(line[i]);
	    }
	    i++;
	}
	fputs(outline, outfile);
	fputs("\n", outfile);
    }

				// final cleanup
    infile.close();
    fclose(outfile);
    OS().Remove(filename);
    OS().Rename(tempfile, filename);
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

