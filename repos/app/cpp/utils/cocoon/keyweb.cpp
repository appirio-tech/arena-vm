
/*------------------------------------------------------------------------
				  COPYRIGHT
--------------------------------------------------------------------------
	  Copyright (C) 1995-2000
	  Jeff Kotula
	  All Rights Reserved.
--------------------------------------------------------------------------
				   FILE LOG
--------------------------------------------------------------------------

    Source code for the keyweb utility.  Produces web pages for
    representing the keyword cross-indexing produced by cocoon.

------------------------------------------------------------------------*/

#include    "cobweb.h"
#include    "sys.h"


#define MenuPerRow  15

const char* execpath = NULL;


/*------------------------------------------------------------------------
------------------------------------------------------------------------*/
void	UsageError()
{
cout << "Usage: keyweb  [options]  keyfilename" << endl;
cout << "Options:" << endl;
cout << "    -c customfile  Specifies a customization file to use" << endl;
cout << "		    instead of cocoon.ini" << endl;
cout << "    -p pathname    use the given path name to find the other" << endl;
cout << "		    Cocoon executables - be sure to include" << endl;
cout << "		    final separator!" << endl;
cout << "    -h		    print this listing" << endl;
cout << endl;
THROW( "Terminated." );
} // UsageError

/*------------------------------------------------------------------------
    MAIN PROGRAM

    Assumes that the input file is sorted by keyword and that each line
    is unique.

------------------------------------------------------------------------*/
main( int argc, char* argv[] )
{
TRY
{
    StringBuff  inbuff;
    StringBuff  keyword;
    StringBuff  currentkey;
    StringBuff  classname;
    StringBuff  basename;
    StringList* keylist	   = NULL;
    StringList* nextkey;
    int		linenumber = 0;
    int		i;
    int		stripping;
    int		numout;
    int		nummenuout;
    char	lastmenu  = '\0';
    ifstream	xref;
    ofstream	mainpage;
    ofstream	menupage;
    ofstream	sideframe;
    char*	custfile       = NULL;
    char*	orgelement     = "dir";
    char*	itemelement    = "<li>";
    char*	itemelementend = "</li>";

    //
    //  Process the command line arguments
    //
    if ( argc < 2 )
	UsageError();
    for ( i = 1; i < argc-1; i++ )
    {
	if ( strcmp( argv[i], "-h" ) == 0 )
	    UsageError();
	else if ( strcmp( argv[i], "-p" ) == 0 )
	{
	    if ( i >= argc-2 )
		UsageError();
	    if ( argv[i+1][strlen( argv[i+1] ) - 1] != '/'  &&
		 argv[i+1][strlen( argv[i+1] ) - 1] != '\\' )
		UsageError();
	    execpath = argv[i+1];
	    i++;
	}
	else if ( strcmp( argv[1], "-c" ) == 0 )
	{
	    if ( i >= argc-2 )
		UsageError();
	    custfile = argv[i+1];
	    i++;
	}
	else
	    UsageError();
    }

    //
    // Create the customization object.
    //
    Customize   custom( FALSE, custfile, NULL, NULL, NULL );

    //
    //  Open the input cross-reference.
    //
    xref.open( argv[argc-1], ios::in );
    if ( ! xref )
    {
	cout << "Couldn't open " << argv[argc-1] << " for reading." << endl;
	return -1;
    }


    //
    //  Load up and sort the input file.
    //
    while ( inbuff.GetLine( xref ) )
    {
	if ( ! keylist )
	    keylist = new StringList( inbuff );
	else
	    keylist = keylist->AddInOrder( inbuff );
	if ( ! keylist )
	{
	    cout << "Error reading keyword file." << endl;
	    return -1;
	}
    }


    //
    //  Open the output pages.
    //
    if ( custom.UseFrames() )
	mainpage.open( "keyweb~main.bas", ios::out );
    else
	mainpage.open( "keyweb.bas", ios::out );
    if ( ! mainpage )
    {
	cout << "Couldn't open keyword file for writing." << endl;
	return -1;
    }
    if ( custom.UseFrames() )
    {
	char	buff[500];

	sprintf(buff, "keyweb~sid%s", custom.Extension());
	sideframe.open(buff, ios::out);
	if ( ! sideframe )
	{
	    cout << "Couldn't open keyword frame files for writing." << endl;
	    return -1;
	}
    }

    menupage.open( "keyweb.mnu", ios::out );
    if ( ! menupage )
    {
	cout << "Couldn't open keyweb menu file for writing." << endl;
	return -1;
    }


    //
    //  Write out intro stuff
    //
    mainpage << "<html><head><title>Keyword Cross-Reference</title>" << endl
	     << "<base target=_top></head><body " << custom.BodyOptions()
	     << ">\n<a name=\"topofdoc\"><h" << custom.BaseHeading() << ">";
    if ( custom.KeywordImage() )
	mainpage << custom.KeywordImage();
    else
	mainpage << custom.Logo();
    if ( ! custom.ImageOnly()  ||  ! strlen( custom.KeywordImage() ) )
	mainpage << "Keyword Cross-Reference";
    mainpage << "</h" << custom.BaseHeading() << "></a>" << endl
	     << custom.Rule() << endl;

    custom.HTMLAtTop( mainpage );

    if ( custom.UseTable() )
    {
	orgelement     = "table cellspacing=4 cellpadding=6";
	itemelement    = "<td align=left>";
	itemelementend = "</td>";
    }

			// put in the menu-bar
    mainpage << "<center><" << orgelement << ">" << endl;
    if ( custom.UseTable() )
	mainpage << "<tr>" << endl;
    mainpage << "cocooninclude(keyweb.mnu)" << endl;
    if ( custom.UseTable() )
	mainpage << "</tr>" << endl;
    mainpage << "</" << orgelement << ">" << endl;
    mainpage << "<font size=+2>" << endl
	     << "<a href=#library><b><i>Libraries</i></b></a><p>" << endl
	     << "<a href=#class><b><i>Classes</i></b></a><p>" << endl;
    if ( custom.UsePerl() )
	mainpage << "<a href=srchform.htm><b><i>Search</i></b></a><p>" << endl;
    mainpage << "</center></font>" << endl << custom.Rule() << endl;

    			// write frames-based pages
    if ( custom.UseFrames() )
    {
	ofstream	mainframe;
	ofstream	emptyframe;
	ofstream	headerframe;
	char		buff[500];

				// main layout
	sprintf(buff, "keyweb%s", custom.Extension());
	mainframe.open(buff, ios::out);
	mainframe << "<html><head><title>Keyword Cross-Reference</title>"
		  << "</head>\n";
	mainframe << "<frameset cols=\"25%,*\">\n";
	mainframe << "<frame src=keyweb~sid" << custom.Extension()
		  << " name=index>\n";
	mainframe << "<frameset rows=\"60,*\">\n";
	mainframe << "<frame src=keyweb~hdr" << custom.Extension()
		  << " name=header noresize>\n";
	mainframe << "<frame src=empty.htm name=doc>\n";
	mainframe << "</frameset></frameset></html>\n";
	mainframe.close();

				// header frame
	sprintf(buff, "keyweb~hdr%s", custom.Extension());
	headerframe.open(buff, ios::out);
	headerframe << "<html><head></head><body>\n<h"
		    << custom.BaseHeading() << ">\n";
	headerframe << "Keyword Cross-Reference\n";
	headerframe << "</h" << custom.BaseHeading() << "></body></html>\n";
	headerframe.close();

				// side index frame
	sideframe << "<html><head><base target=doc></head><body "
		  << "link=darkred alink=darkred vlink=darkgreen>\n";
	sideframe << "<b><i><center><a href=keyweb~main" << custom.Extension()
		  << "#library>Libraries"
		  << "</a>\t<a href=keyweb~main" << custom.Extension()
		  << "#class>"
		  << "Classes</a><p>" << endl;
	if ( custom.UsePerl() )
	    sideframe << "<a href=srchform.htm target=_top>Search</a><p>\n";
	sideframe << "</i><small>\n";

				// initial (empty) doc frame
	emptyframe.open("empty.htm", ios::out);
	emptyframe << "<html><head></head><body></body></html>\n";
	emptyframe.close();
    }

    //
    //  Go through each keyword in the cross-reference file and update
    //  the web page.
    //
    numout     = 0;
    nummenuout = 0;
    nextkey    = keylist;
    while ( nextkey )
    {
	inbuff.Clear();
	inbuff.Append( nextkey->String() );
	linenumber++;

	//
	//  Update for the keyword
	//
	keyword.Clear();
	classname.Clear();
	basename.Clear();
	sscanf( inbuff, " %s %s %s ", (char *)keyword, (char *)basename,
		(char*) classname );
	if ( strlen( keyword ) < 1  ||  strlen( basename ) < 1 )
	{
	    cout << "Bogus line (" << linenumber << ").  Ignoring it." << endl;
	    nextkey = nextkey->Next();
	    continue;
	}
	classname.Append( basename );

	//
	//  Strip out any underscores--replace them with spaces.
	//
	for ( i = 0; keyword[i]; i++ )
	    if ( keyword[i] == '_' )
		keyword[i] = ' ';

	//
	//  See if we're switching keywords...
	//
	if ( strcmp( currentkey, keyword ) != 0 )
	{
	    if ( strlen( currentkey ) != 0 )
	    {
		if ( custom.UseTable() )
		    mainpage << "</tr>" << endl;
		mainpage << "</" << orgelement << "></ul>" << endl;
		if ( custom.BackToTop() )
		    mainpage << custom.BackToTop() << "keywords<p>" << endl;
		mainpage << custom.Rule() << endl;
	    }
	    mainpage << "<h" << custom.BaseHeading()+1 << "><em>";
	    if ( keyword[0] != lastmenu )
		mainpage << "<a name=" << keyword[0] << ">";
	    if ( strcmp( keyword, "CLASSES" ) == 0 )
		mainpage << "<a name=class>";
	    else if ( strcmp( keyword, "LIBRARIES" ) == 0 )
		mainpage << "<a name=library>";
	    mainpage << keyword;
	    if ( strcmp( keyword, "CLASSES" ) == 0  ||
		 strcmp( keyword, "LIBRARIES" ) == 0 )
		mainpage << "</a>";
	    if ( keyword[0] != lastmenu )
		mainpage << "</a>";
	    mainpage << "</em></h" << custom.BaseHeading()+1 << ">" << endl;
	    mainpage << "<ul><" << orgelement << ">" << endl;
	    if ( custom.UseTable() )
		mainpage << "<tr>" << endl;
	    currentkey.Clear();
	    currentkey.Append( keyword );
	    numout = 0;
			// add an entry to the menu page
	    if ( currentkey[0] != lastmenu )
	    {
		menupage << itemelement << "<a href=#" << currentkey[0]
			 << "><font size=+2><b>" << currentkey[0]
			 << "</b></a>" << itemelementend << endl;
		nummenuout++;
		if ( nummenuout >= MenuPerRow )
		{
		    if ( custom.UseTable() )
			menupage << endl << "</tr><tr>" << endl;
		    menupage << endl;
		    nummenuout = 0;
		}
		if ( custom.UseFrames() )
		{
		    sideframe << "<a href=keyweb~main" << custom.Extension()
		    	      << "#" << currentkey[0] << ">" << currentkey[0]
			      << "</a><p>\n";
		}
		lastmenu = currentkey[0];
	    }
	}

	mainpage << itemelement << "<a href=\"" << classname
		 << custom.Extension() << "\">";

				// strip out any preceding path info
	stripping = FALSE;
	for ( i = strlen( classname ) - 1; i >= 0; i-- )
	{
	    if ( stripping )
		classname[i] = ' ';
	    else if ( classname[i] == '/' )
	    {
		classname[i] = ' ';
		stripping = TRUE;
	    }
	}
	classname.TrimWhite();

	mainpage << classname << "</a>" << itemelementend;

	numout++;
	if ( numout == custom.NumKeyColumns()  &&  custom.UseTable() )
	{
	    mainpage << endl << "</tr><tr>" << endl;
	    numout = 0;
	}

	mainpage << endl;
	nextkey = nextkey->Next();
    } // while

    if ( custom.UseTable() )
	mainpage << "</tr>" << endl;

    mainpage << "</" << orgelement << "></ul>" << endl;
    Signature( mainpage, custom );
    mainpage << "</body></html>" << endl;

				// clean up
    if ( keylist )
	delete keylist;
    mainpage.close();
    menupage.close();
    if ( custom.UseFrames() )
    {
	sideframe << "</small></b></center></body></html>\n";
	sideframe.close();
    }
    xref.close();

				// run keyweb.bas through recombine to
				// get the menu-bar added in
    StringBuff  cmd;

    cmd.Append( execpath );
    if ( custom.UseFrames() )
	cmd.Append( "recomb keyweb~main.bas keyweb~main" );
    else
	cmd.Append( "recomb keyweb.bas keyweb" );
    cmd.Append( custom.Extension() );
    OS().System( cmd );
}

CATCH( char* str )
{
#ifdef  NOEXCEPTIONS
	const char* str="Unknown";
#endif
    cout << "Exception : " << str << endl;
    return -1;
}

    return 0;
} // main



