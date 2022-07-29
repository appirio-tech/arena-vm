
/*------------------------------------------------------------------------
				  COPYRIGHT
--------------------------------------------------------------------------
	  Copyright (C) 1995-2000
	  Jeff Kotula
	  All Rights Reserved.
--------------------------------------------------------------------------
				   FILE LOG
--------------------------------------------------------------------------

    Member functions for the Handler classes.

------------------------------------------------------------------------*/

#include    "cobweb.h"
#include    "sys.h"

extern  int debug;
extern  int linenumber;


// #define  TABLESTRING "table border"
#define TABLESTRING "table"

static const int    GarbageTolerance = 200;

/*------------------------------------------------------------------------
    Returns TRUE if the given character is a vowel.
	Case independent.
------------------------------------------------------------------------*/
static int  isVowel( char ch )
{
    char    letter = (char) toupper( ch );

    if ( letter == 'A'  ||  letter == 'E'  ||  letter == 'I' ||
	 letter == 'O'  ||  letter == 'U' )
	return TRUE;
    return FALSE;
}


/*------------------------------------------------------------------------
    Determine whether or not the given string is terminating a
    comment header block.
------------------------------------------------------------------------*/
static int  CommentTerminator( char* buff )
{
    int	    len = strlen( buff );

    if ( len >= 2  &&  strncmp( &buff[len-2], "*/", 2 ) == 0 )
	return TRUE;
    return FALSE;
} // CommentTerminator



/*------------------------------------------------------------------------
--------------------------------------------------------------------------
				ContextTracker
--------------------------------------------------------------------------
------------------------------------------------------------------------*/
void	ContextTracker::CheckLine( StringBuff& buff )
{
    int	    i = 0;

    _skipnext = FALSE;
    while ( buff[i]  &&  buff[i+1] )
    {
	if ( ! _incomment )
	{
	    for ( ; buff[i]  &&  buff[i+1]; i++ )
		if ( buff[i] == '/'  &&  buff[i+1] == '*' )
		{
		    _incomment = TRUE;
		    _lastcomment.Clear();
		    _lastcomment.Append( "/*" );
		    i += 2;
		    break;
		}
		else if ( buff[i] == '/'  &&  buff[i+1] == '/' )
		{
		    if ( ! _cppstyle )
			_lastcomment.Clear();
		    _cppstyle = TRUE;
		    _lastcomment.Append( &buff[i] );
		    _lastcomment.Append( "\n" );
		    i = strlen( buff );
		    break;
		}
		else if ( buff[i] == '\\'  ||  buff[i+1] == '\\' )
		    _skipnext = TRUE;
		else if ( ! isspace( buff[i] ) )
		{
		    _skipnext = FALSE;
		    _lastcomment.Clear();
		}
	}
	if ( _incomment )
	{
	    for ( ; buff[i]; i++ )
	    {
		if ( buff[i] == '*'  &&  buff[i+1]  &&  buff[i+1]== '/' )
		{
		    _lastcomment.Append( "*/\n" );
		    _incomment = FALSE;
		    i += 2;
		    break;
		}
		else
		{
		    char    tmp[2];

		    tmp[0] = buff[i];
		    tmp[1] = '\0';
		    _lastcomment.Append( tmp );
		}
	    }
	}

    } // while

    if ( _incomment )
	_lastcomment.Append( "\n" );

} // CheckLine


/*------------------------------------------------------------------------
--------------------------------------------------------------------------
				Census
--------------------------------------------------------------------------
------------------------------------------------------------------------*/
Census::Census( const int isglobal ) :
    _isglobal( isglobal ), _numabstract( 0 ), _enumcount( 1 ),
    _allinline( TRUE )
{
} // constructor


void	Census::Function( const int isinline, const int isabstract )
{
    if ( ! isinline )
	_allinline = FALSE;
    if ( isabstract )
	_numabstract++;
} // Function


void	Census::Data( const int isunnamedenum )
{
    if ( isunnamedenum )
	_enumcount++;
} // Data


/*------------------------------------------------------------------------
--------------------------------------------------------------------------
				Handler
--------------------------------------------------------------------------
------------------------------------------------------------------------*/
Handler::~Handler()
{
    if ( _subhandler )
    {
	if ( debug )
	    cout << "\tLine " << linenumber << ": Preempting handler "
		 << _subhandler->SectionName() << endl;
	delete _subhandler;
    }
    _subhandler = NULL;

} // ~Handler


void	Handler::Control( StringBuff& buff, int& surrendercontrol )
{
    Handler*	newhandler   = NULL;
    int		subsurrender = FALSE;

				// see if we should release control
    surrendercontrol = FALSE;
    if ( TerminatorFound( buff ) )
    {
	if ( debug )
	    cout << "Line " << linenumber << ": Terminator for "
		 << SectionName() << " found." << endl;
	surrendercontrol = TRUE;
	return;
    }
				// see if we need a new subhandler
    newhandler = NewHandler( buff );
    if ( newhandler )
    {
	if ( _subhandler )
	{
	    if ( debug )
		cout << "\tLine " << linenumber << ": Preempting old handler "
		     << _subhandler->SectionName() << endl;
	    delete _subhandler;
	}
	_subhandler = newhandler;
	if ( debug )
	    cout << "Line " << linenumber << ": New handler: "
		 << _subhandler->SectionName() << endl;
	if ( _subhandler->SurrenderImmediately() )
	{
	    if ( debug )
		cout << "\tLine " << linenumber << ": Immediate surrender "
		     << "from handler " << _subhandler->SectionName()
		     << endl;
	    delete _subhandler;
	    _subhandler = NULL;
	}
    }
				// otherwise pass control on to current
				// subhandler
    else if ( _subhandler )
    {
	_subhandler->Control( buff, subsurrender );
	if ( subsurrender )
	{
	    if ( debug )
		cout << "Line " << linenumber << ": "
		     << _subhandler->SectionName() << " surrendered control "
		     << "back to " << SectionName() << endl;
	    delete _subhandler;
	    _subhandler = NULL;
	}
    }
				    // otherwise process the line
    else
    {
	/*  lots of output if this one is uncommented
	if ( debug )
	    cout << "\t\tProcessing line " << linenumber " for "
		 << SectionName() << endl;
	*/
	ProcessLine( buff, surrendercontrol );
    }

} // Control


void	Handler::Preempt()
{
    if ( _subhandler )
    {
	if ( debug )
	    cout << "\tLine " << linenumber << ": Preempting handler "
		 << _subhandler->SectionName() << endl;
	delete _subhandler;
    }
    _subhandler = NULL;
}


/*------------------------------------------------------------------------
--------------------------------------------------------------------------
				HeaderFile
--------------------------------------------------------------------------
------------------------------------------------------------------------*/
HeaderFile::HeaderFile( Xref& xref, Customize& custom, char* libname ) :
    Handler( xref, custom ), _membernum( 0 ), _classdeclactive( FALSE ),
    _census( TRUE )
{
    StringBuff  tmpname;
    int		doinit;

    _libname.Append( libname );
    _census.SetName( libname );
				// load up the current set of census data
				// for the library
    // JJK!

				// open the files for the library data
				// link file
    sprintf( tmpname, "%s.lnk", libname );
    _liblink.open( tmpname, ios::app );
    if ( ! _liblink )
	THROW( "Couldn't open library link file for appending." );

				// function file
    sprintf( tmpname, "%s.fnc", libname );
    _libfunc.open( tmpname, ios::app );
    if ( ! _libfunc )
	THROW( "Couldn't open library function file for appending." );

				// class list file
    sprintf( tmpname, "%s.cls", libname );
    _libclass.open( tmpname, ios::app );
    if ( ! _libclass )
	THROW( "Couldn't open library function file for appending." );

				// data type/element file
    sprintf( tmpname, "%s.dta", libname );
    _libdata.open( tmpname, ios::app );
    if ( ! _libdata )
	THROW( "Couldn't open library data file for appending." );

				// quick index file
    sprintf( tmpname, "%s.ind", libname );
    _libdocindex.open( tmpname, ios::app );
    if ( ! _libdocindex )
	THROW( "Couldn't open library quick index file for appending." );

    if ( Custom().UseFrames() )
    {
	sprintf(tmpname, "%s.fin", libname);
	_indexframefile.open(tmpname, ios::app);
	if ( ! _indexframefile )
	    THROW( "Couldn't open library frame index file for appending." );
	sprintf(tmpname, "%s~mbr.lbs", libname);
	_memberframefile.open(tmpname, ios::app);
	if ( ! _memberframefile )
	    THROW( "Couldn't open library frame index file for appending." );
    }

				// create the skeleton for the library page
					// first, make sure it doesn't
					// already exist
    if ( Custom().UseFrames() )
	sprintf( tmpname, "%s~full.lbs", libname );
    else
	sprintf( tmpname, "%s.lbs", libname );
    doinit = ! OS().Exists( tmpname );
    _libmain.open( tmpname, ios::app );
    if ( ! _libmain )
	THROW( "Couldn't open main library file for appending." );

					// write the stuff
    if ( doinit )
    {
	ofstream    logofile;
	ofstream    sigfile;

	CrossRef().Keywords() << "LIBRARIES  " << libname << " "
			      << Custom().WebPath() << endl;
	_libmain << "<html><head><title>" << libname
		 << " Library</title></head>" << endl
		 << "<body" << Custom().BodyOptions() << "><h"
		 << custom.BaseHeading() << ">" << endl
		 << "cocooninclude(" << libname << ".lgo)" << endl
		 << "<a name=\"topofdoc\">Library " << libname
		 << "</a></h" << custom.BaseHeading() << ">" << endl
		 << custom.Rule() << "</a>" << endl;

	Custom().HTMLAtTop( _libmain );

	_libmain << "<p><strong>[" << endl;
	if ( Custom().UsePerl() )
	    _libmain << "<a href=\"" << Custom().Protocol()
		     << CrossRef().XrefURL() << "srchform.htm\">Search</a> | "
		     << endl;
	_libmain << "<a href=\"" << Custom().Protocol() << CrossRef().XrefURL()
		 << "keyweb" << Custom().Extension()
		 << "\">Keywords</a> | " << endl
		 << "<a href=\"#classes\">Classes</a> | " << endl
		 << "<a href=\"#data\">Data</a> | " << endl
		 << "<a href=\"#functions\">Functions</a>" << endl
		 << " ]</strong><p>" << endl;

	_libmain << "<h" << custom.BaseHeading()+1 << ">"
		 << custom.QuickRefImage();
	if ( ! custom.ImageOnly() || ! strlen( custom.QuickRefImage() ) )
	    _libmain << "Quick Index";
	_libmain << "</h" << custom.BaseHeading()+1 << ">" << endl
		 << "<dl>" << endl << "cocooninclude(" << libname
		 << ".ind)" << endl << "</dl>" << endl
		 << "<p><br><p>"

		 << "<a name=\"classes\">" << custom.Rule()
		 << "</a><h" << custom.BaseHeading()+1 << ">Classes</h"
		 << custom.BaseHeading()+1 << ">" << endl
		 << "<dl><dl>" << endl
		 << "cocooninclude(" << libname << ".cls)" << endl
		 << "</dl></dl>" << endl;
	if ( custom.BackToTop() )
	    _libmain << custom.BackToTop() << "<i>" << libname << "</i><p>"
		     << endl;

	_libmain << "<a name=\"data\">" << custom.Rule()
		 << "</a><h" << custom.BaseHeading()+1 << ">Data</h"
		 << custom.BaseHeading()+1 << ">" << endl;
	if ( Custom().UseTable() )
	    _libmain << "<" << TABLESTRING << ">" << endl;
	else
	    _libmain << "<dl>" << endl;
	_libmain << "cocooninclude(" << libname << ".dta)" << endl;
	if ( Custom().UseTable() )
	    _libmain << "</table>" << endl;
	else
	    _libmain << "</dl>" << endl;
	if ( custom.BackToTop() )
	    _libmain << custom.BackToTop() << "<i>" << libname << "</i><p>"
		     << endl;

	_libmain << "<a name=\"functions\">" << custom.Rule()
		 << "</a><h" << custom.BaseHeading()+1
		 << ">Global Functions</h" << custom.BaseHeading()+1
		 << ">" << endl;
	if ( Custom().UseTable() )
	    _libmain << "<" << TABLESTRING << ">" << endl;
	else
	    _libmain << "<dl>" << endl;
	_libmain << "cocooninclude(" << libname << ".fnc)" << endl;
	if ( Custom().UseTable() )
	    _libmain << "</table>" << endl;
	else
	    _libmain << "</dl>" << endl;
	if ( custom.BackToTop() )
	    _libmain << custom.BackToTop() << "<i>" << libname << "</i><p>"
		     << endl;

					// create default logo file
	sprintf( tmpname, "%s.lgo", libname );
	logofile.open( tmpname, ios::out );
	if ( ! logofile )
	    cout << "Error: could not open " << tmpname
		 << " for writing." << endl;
	logofile << Custom().Logo() << endl;

					// create signature file
	sprintf( tmpname, "%s.sig", libname );
	sigfile.open( tmpname, ios::out );
	if ( ! sigfile )
	    cout << "Error: could not open " << tmpname
		 << " for writing." << endl;
	Signature( sigfile, Custom() );

				// if using frames, set up the files
	if ( Custom().UseFrames() )
	{
	    ofstream	mainframe;
	    ofstream	declframe;
	    ofstream	indexframe;
	    ofstream	emptyframe;

	    sprintf(tmpname, "%s%s", (char*)libname, Custom().Extension());
	    mainframe.open(tmpname, ios::out);
	    sprintf(tmpname, "%s~decl%s", (char*)libname,Custom().Extension());
	    declframe.open(tmpname, ios::out);
	    sprintf(tmpname, "%s~sid.lbs", (char*)libname);
	    indexframe.open(tmpname, ios::out);
	    emptyframe.open("empty.htm", ios::out);

	    				// main layout
	    mainframe << "<html><head><title>Library " << libname
	    	      << " Documentation</title></head>\n";
	    mainframe << "<frameset cols=\"25%,*\">\n";
	    mainframe << "<frame src=" << libname << "~sid"
	    	      << Custom().Extension() << " name=index>\n";
	    mainframe << "<frameset rows=\"60,*,*\">\n";
	    mainframe << "<frame src=" << libname
	    	      << "~decl" << Custom().Extension()
		      << " name=decl noresize>\n";
	    mainframe << "<frame src=" << libname << "~mbr"
	    	      << Custom().Extension() << " name=member>\n";
	    mainframe << "<frame src=empty.htm name=doc>\n";
	    mainframe << "</frameset></frameset></html>\n";
	    mainframe.close();

					// declaration frame
	    declframe << "<html><head></head><body>\n<h"
		      << Custom().BaseHeading() << ">\n";
	    declframe << "Library " << libname << "\n";
	    declframe << "</h" << Custom().BaseHeading() << "></body></html>\n";
	    declframe.close();

	    				// side index frame
	    indexframe << "<html><head>\n";
	    indexframe << "<base target=doc></head><body "
	    	       << "link=darkred alink=darkred vlink=darkgreen>\n";
	    indexframe << "cocooninclude(" << libname << ".lgo)\n";
	    indexframe << "cocooninclude(" << libname << ".fin)\n";
	    indexframe << "<p><hr><p><small><i>\n";
	    indexframe << "<a href=\"" << Custom().Protocol()
	    	       << CrossRef().XrefURL() << "keyweb"
		       << Custom().Extension()
		       << "\" target=_top>Keywords</a><p>\n";
	    indexframe << "<a href=" << libname
	    	       << "~mbr" << Custom().Extension()
		       << "#class target=member>Classes</a><p>\n";
	    indexframe << "<a href=" << libname
	    	       << "~mbr" << Custom().Extension()
		       << "#func target=member>Functions</a><p>\n";
	    indexframe << "<a href=" << libname
	    	       << "~mbr" << Custom().Extension()
		       << "#data target=member>Data</a><p>\n";
	    indexframe << "<a href=" << libname
	    	       << "~full" << Custom().Extension()
		       << " target=_top>One-sheet</a><p>\n";
	    indexframe << "</small></i></body></html>\n";

	    				// member summary frame
	    _memberframefile << "<html><head>\n";
	    _memberframefile << "<base target=doc></head><body>\n";
	    _memberframefile << "<a name=class><p><big><b><i><center>Classes"
			     << "</center></i></b></big></a>\n";
	    _memberframefile << "cocooninclude(" << libname << ".cls)\n";
	    _memberframefile << "<a name=func><p>" << Custom().Rule()
	    		     << "<big><b><i><center>Functions</center>"
			     << "</i></b></big></a>\n";
	    if ( Custom().UseTable() )
		_memberframefile << "<" << TABLESTRING << ">" << endl;
	    else
		_memberframefile << "<dl>" << endl;
	    _memberframefile << "cocooninclude(" << libname << ".fnc)\n";
	    if ( Custom().UseTable() )
		_memberframefile << "</table>" << endl;
	    else
		_memberframefile << "</dl>" << endl;
	    _memberframefile << "<a name=data><p>" << Custom().Rule()
			     << "<big><b><i><center>Data</center>"
			     << "</i></b></big></a>\n";
	    if ( Custom().UseTable() )
		_memberframefile << "<" << TABLESTRING << ">" << endl;
	    else
		_memberframefile << "<dl>" << endl;
	    _memberframefile << "cocooninclude(" << libname << ".dta)\n";
	    if ( Custom().UseTable() )
		_memberframefile << "</table>" << endl;
	    else
		_memberframefile << "</dl>" << endl;
	    _memberframefile << "</body></html>\n";

					// initial (empty) doc frame
	    emptyframe << "<html><head></head><body></body></html>\n";
	}
    }
} // HeaderFile


HeaderFile::~HeaderFile()
{
    Preempt();
} // ~HeaderFile


Handler*    HeaderFile::NewHandler( StringBuff &buff )
{
    char    memberkey[1024];
    int	    i;		// looping index

    if ( ! haveSubhandler() )
	_classdeclactive = FALSE;

    if ( _classdeclactive )	// don't do anything until ClassDecl
	return NULL;		    // releases control


					    // look for new handlers
    if ( _tracker.SkipNext() )
    {
	    // do nothing!
    }

    else if ( ClassDecl::FindSentinel( buff, Custom() ) )
    {
	StringBuff  prevcomment;

	_classdeclactive = TRUE;
	_tracker.LastComment( prevcomment );
	_tracker.Reset();
	return new ClassDecl( CrossRef(), Custom(), _libclass, buff,
			      prevcomment );
    }

    else if ( !_tracker.inComment()  &&
		  ( ! Custom().NoSentinels()  ||  ! haveSubhandler() ) &&
	      Member::FindSentinel( buff, Custom() ) )
    {
	int i;
	StringBuff  prevcomment;

				// build the key string used to identify
				// members -- remove any non-alpha-
				// numeric characters that may appear in
				// the file name, otherwise the string
				// substitution won't work
	_membernum++;
	sprintf( memberkey, "%s_member_%d", CrossRef().FileName(),
		 _membernum );
	for ( i = 0; memberkey[i]; i++ )
	    if ( ! isalnum( memberkey[i] ) )
		memberkey[i] = 'X';

	_tracker.LastComment( prevcomment );
	_tracker.Reset();
	return new Member( CrossRef(), Custom(), _libmain, _libfunc, _libdata,
			   _libfunc, _liblink, memberkey, _census, buff,
			   prevcomment );
    }

    else if ( LibDoc::FindSentinel( buff, Custom() ) )
	return new LibDoc( CrossRef(), Custom(), _libname, _libmain,
			   _libdocindex, _indexframefile );

    else if ( strncmp( &buff[buff.NonWhite()], "// FUNCTION GROUP:", 18 ) == 0 )
    {
	if ( Custom().UseTable() )
	    _libfunc << "<tr><td></td></tr><tr><td></td><td>";
	else
	    _libfunc << "<dl><dl><dl><dl><dl><dl>" << endl << "<dt>";
	_libfunc << "<strong>// " << &buff[buff.NonWhite()+18]
		 << "</strong>";
	if ( Custom().UseTable() )
	    _libfunc << "</td></tr>" << endl;
	else
	    _libfunc << "</dl></dl></dl></dl></dl></dl>" << endl;
    }

    else if ( strncmp( &buff[buff.NonWhite()], "// DATA GROUP:", 14 ) == 0 )
    {
	if ( Custom().UseTable() )
	    _libdata << "<tr><td></td></tr><tr><td></td><td>";
	else
	    _libdata << "<dl><dl><dl><dl><dl><dl>" << endl << "<dt>";
	_libdata << "<strong>// " << &buff[buff.NonWhite()+14]
		 << "</strong>";
	if ( Custom().UseTable() )
	    _libdata << "</td></tr>" << endl;
	else
	    _libdata << "</dl></dl></dl></dl></dl></dl>" << endl;
    }


    else if ( strncmp( &buff[buff.NonWhite()], "// CLASS GROUP:", 15 ) == 0 )
    {
	_libclass << "<dl><dl><dl><dl><dl>" << endl << "<dt>";
	_libclass << "<strong>// " << &buff[buff.NonWhite()+15]
		  << "</strong>";
	_libclass << "</dl></dl></dl></dl></dl>" << endl;
    }

    _tracker.CheckLine( buff );

    return NULL;
} // NewHandler


/*------------------------------------------------------------------------
--------------------------------------------------------------------------
				ClassDecl
--------------------------------------------------------------------------
------------------------------------------------------------------------*/
int ClassDecl::FindSentinel( StringBuff& buff, Customize& custom )
{
    if ( strncmp( &buff[0], custom.ClassSentinel(),
		  custom.ClassSentinelLength() ) == 0  &&
		  ! isalnum( buff[custom.ClassSentinelLength()] ) )
    {
	if ( custom.ForUnimax()  &&
	     strcmp( custom.ClassSentinel(), "CLASS") == 0 )
	{
	    int	    i;

	    for ( i = 5; buff[i]; i++ )
		    if ( ! isspace( buff[i] ) )
		    break;
	    if ( ! buff[i] )
		return TRUE;
	}
	else
	    return TRUE;
    }

    if ( custom.NoSentinels()  &&  isBodySentinel( buff, custom ) )
	    return TRUE;

    return FALSE;

} // FindSentinel


ClassDecl::ClassDecl( Xref& xref, Customize& custom, ofstream& libclass,
		      StringBuff& sentinelline, StringBuff& prevcomment,
		      char* nestedin ) :
    Handler( xref, custom ), _libclass( libclass ), _inbody( FALSE ),
    _inpublic( FALSE ), _inprotected( FALSE ), _inprivate( TRUE ),
    _startdd( TRUE ), _getinh( FALSE ), _membercount( 0 ),
    _libdocdone( FALSE ), _indenting( FALSE ), _hasparents( FALSE ),
    _census( FALSE ), _hasdoc( FALSE )
{
    int	    class_dec = FALSE;  // TRUE if its a class declaration
    int	    process   = FALSE;  // TRUE if we need to call ProcessLine()
    int	    i;			// loop control index
    int	    length;		// length of most recently found identifier

    if ( nestedin )
	_nestedin.Append( nestedin );

				// see if we can extract the class name--
				// if so, the sentinel was a class declaration
				// rather than the comment sentinel, so call
				// ProcessLine
    for ( i = sentinelline.NonWhite();
	      sentinelline.FoundIdentifier( i, length, TRUE );
	      i += length )
    {

	if ( length != 5 ) continue;

	if ( strncmp( &sentinelline[i], custom.ClassString(),
		      custom.ClassStringLength() ) == 0 )
	    class_dec = TRUE;
	else if ( Custom().ForUnimax()  &&
		  strncmp( &sentinelline[i], "CLASS", 5 ) == 0 )
	    class_dec = TRUE;

	if ( class_dec )
	{
	    StringBuff	    str;

				// capture the class name, if it's there
	    i += length;
	    do
	    {
		str.Clear();
		if ( sentinelline.FoundIdentifier(i, length, TRUE) )
		{
		    str.Append(&sentinelline[i], length);
		    i += length;
		}
	    }
	    while ( length > 0  &&  Custom().IgnoreDirective(str) );
	    if ( length < 1 )
		break;
	    _classname.Append(str);

				    // set up the internals for the call
				    // to ProcessLine()
	    InitFiles();
	    process = TRUE;
	    break;
	}
    }

    if ( process )
    {
	int dummy;  // the surrendercontrol flag isn't important here...

	if ( strlen( prevcomment ) > 0 )
	{
	    _indfile  << "<a href=#DESCRIPTION>DESCRIPTION</a>" << endl;
	    _mainfile << "<a name=\"DESCRIPTION\">" << Custom().Rule()
		      << "</a><h" << Custom().BaseHeading()+2
		      << ">DESCRIPTION</h" << Custom().BaseHeading()+2
		      << ">" << endl;
	    prevcomment.MakeIntoDoc( Custom() );
	    _mainfile << prevcomment;
	    if ( Custom().BackToTop() )
		_mainfile << Custom().BackToTop() << "<i>" << _classname
			  << "</i><p>" << endl;
	}
	else
	    _indfile << "<dd><big>No documentation available.</big></dd>"
		     << endl;
	ProcessLine( sentinelline, dummy );
    }

} // constructor


ClassDecl::~ClassDecl()
{
    Preempt();
    if ( _census.AllInline() )
	_summaryfile << "<p><em><strong>All</strong> class functions are "
		     << "defined inline.</em></p>" << endl;
    if ( _census.isAbstract() )
	_summaryfile << "<p><em>This is an <strong>abstract</strong> class "
		     << "and cannot be directly instanced.</em></p>" << endl;
    if ( _census.AllInline()  &&  _census.isAbstract() )
	_indexframefile << "<hr><p><small><i>Abstract, inline.</i></small>\n";
    else if ( _census.AllInline() )
	_indexframefile << "<hr><p><small><i>Fully inline.</i></small>\n";
    else if ( _census.isAbstract() )
	_indexframefile << "<hr><p><small><i>Abstract base class</i></small>\n";

    _mainfile << "cocooninclude(" << _classname << ".flt)" << endl;
    Signature( _mainfile, Custom() );
    _mainfile << "</body></html>" << endl;
    if ( _indenting )
	_indfile << "</dl>" << endl;
    if ( Custom().ForUnimax() )
	CrossRef().LispDump() << "))" << endl;
    if ( Custom().UseFrames() )
    {
	StringBuff	tmp;
	StringBuff	dirpath;

	tmp.Append(Custom().WebPath());
	if ( strncmp(tmp, "WEBROOT/", 8) == 0 )
	    strcpy(dirpath, &tmp[8]);
	else
	    dirpath.Append(tmp);
	_indexframefile << "<p><hr><p><small>"
			<< "<a href=\"" << Custom().WebPath()
			    << CrossRef().LibName() << Custom().Extension()
			    << "\" target=_top>" << CrossRef().LibName()
			    << "</a><p>\n"
			<< "<a href=" << _classname
			    << ".h target=_top>Source</a>  ";
	if ( Custom().ForStratasys() )
	{
	    _indexframefile << "<a href=http://rabbit.stratasys.com:5080/"
	    		    << "cgi-bin/cvsweb.cgi/puc/" << dirpath
			    << _classname << ".h target=_top>"
			    << "<small>(CVS)</small></a>";
	}
	_indexframefile << "<p>\n";
	if ( _implName[0] )
	{
	    _indexframefile << "<a href=" << _implName << " target=_top>"
	    		    << "Implementation</a>  ";
	    if ( Custom().ForStratasys() )
	    {
		_indexframefile << "<a href=http://rabbit.stratasys.com:5080/"
				<< "cgi-bin/cvsweb.cgi/puc/" << dirpath
				<< _implName << " target=_top>"
				<< "<small>(CVS)</small></a>";
	    }
	    _indexframefile << "<p>\n";
	}
	_indexframefile << "<a href=\"" << _classname
				<< "~full" << Custom().Extension()
				<< "#parents\">Ancestors</a><p>\n"
			<< "<a href=\"" << _classname
				<< "~full" << Custom().Extension()
				<< "#children\">Descendants</a><p>\n"
			<< "<a href=../keyweb" << Custom().Extension()
			    << " target=_top>Keywords</a><p>\n"
			<< "<a href=" << _classname
			    << "~full" << Custom().Extension()
			    << " target=_top>One-sheet</a><p>\n";
	if ( Custom().UsePerl() )
	    _indexframefile << "<a href=\"" << Custom().Protocol()
	    	            << CrossRef().XrefURL()
		            << "srchform.htm\">Search</a><p>\n" << endl;

	_indexframefile  << "</small></body></html>\n";
	_memberframefile << "</body></html>\n";
    }
}


int ClassDecl::TerminatorFound( StringBuff& buff )
{
    if ( haveSubhandler()  &&  strcmp( SubhandlerName(), "ClassDecl" ) == 0 )
	return FALSE;
    return strncmp( buff, "};", 2 ) == 0;
}


Handler*    ClassDecl::NewHandler( StringBuff& buff )
{
    char    memberkey[1024];

    if ( _tracker.SkipNext() )
    {
	    // do nothing!
    }
    else if ( ! _inbody )
    {
	_libdocdone = TRUE;
	if ( IgnoreHeading::FindSentinel( buff, Custom() ) )
	{
	    _hasdoc = TRUE;
	    return new IgnoreHeading( CrossRef(), Custom() );
	}

	else if ( Keyword::FindSentinel( buff, Custom() ) )
	{
	    _hasdoc = TRUE;
	    return new Keyword( CrossRef(), Custom(), _classname );
	}

	else if ( DocHeader::FindSentinel( buff, Custom() ) )
	{
	    StringBuff  newheading;
	    int	    len;
	    int	    i = 0;

	    _hasdoc = TRUE;
	    newheading.Append( buff );
	    newheading.TrimWhite();
	    len = strlen( _prevheading );
	    if ( len > 0  &&  ! _indenting  &&
		 strncmp( _prevheading, newheading, len ) == 0  &&
		 newheading[len] == ':' )
	    {
		_indfile << "<dl>" << endl;
		_indenting = TRUE;
		i	   = len+2;
	    }
	    else if ( _indenting )
	    {
		if ( strncmp( _prevheading, newheading, len ) != 0 )
		{
		    _indfile << "</dl>" << endl;
		    _indenting = FALSE;
		}
		else
		    i = len+2;
	    }
	    if ( i == 0 )
	    {
		_prevheading.Clear();
		_prevheading.Append( newheading );
	    }

	    return new DocHeader( CrossRef(), Custom(), _classname,
				  &newheading[i], _mainfile, _indfile,
				  _indexframefile );
	}
	_libdocdone = FALSE;
    }

    else if ( _inbody  &&  ! _tracker.inComment()  &&
	      (! _inprivate || Custom().ShowPrivates())  &&
	      ! haveSubhandler()  &&
	      Member::FindSentinel( buff, Custom() ) )
    {
	StringBuff  prevcomment;

	_tracker.LastComment( prevcomment );
	_tracker.Reset();
	_membercount++;
	sprintf( memberkey, "%s_member_%d", (char *)_classname, _membercount );

	if ( _inpublic )
	    return new Member( CrossRef(), Custom(), _mainfile, _pubfile,
			       _pubfile, _indfile, _linkfile, memberkey,
			       _census, buff, prevcomment );
	else if ( _inprivate )
	    return new Member( CrossRef(), Custom(), _mainfile, _privfile,
			       _privfile, _indfile, _linkfile, memberkey,
			       _census, buff, prevcomment );
	else
	    return new Member( CrossRef(), Custom(), _mainfile, _protfile,
			       _protfile, _indfile, _linkfile, memberkey,
			       _census, buff, prevcomment );
    }
    else if ( _inbody  &&  ! haveSubhandler()  &&
	      (! _inprivate || Custom().ShowPrivates())  &&
	      ClassDecl::FindSentinel( buff, Custom() ) )
    {
	StringBuff  prevcomment;

	_tracker.LastComment( prevcomment );
	_tracker.Reset();
	if ( _inpublic )
	    return new ClassDecl( CrossRef(), Custom(), _pubfile, buff,
			      prevcomment, _classname );
	else if ( _inprivate )
	    return new ClassDecl( CrossRef(), Custom(), _privfile, buff,
			      prevcomment, _classname );
	else
	    return new ClassDecl( CrossRef(), Custom(), _protfile, buff,
			      prevcomment, _classname );
    }

    if ( _inbody )
    {
	_tracker.CheckLine( buff );
	_census.SetName( _classname );
    }

    return NULL;
}


void	ClassDecl::ProcessLine( StringBuff& buff, int& surrendercontrol )
{
    surrendercontrol = FALSE;
					// looking for class name
    if ( strlen( _classname ) == 0 )
    {
					// parse out the class name
	int i;	    // looping index
	int length;	// length of most recently found identifier

	for (i = buff.NonWhite(); buff.FoundIdentifier(i, length); i += length)
	{
	    StringBuff  tmpstr;
					// use first identifier as class,
					// but skip "class" if it appears
	    if ( length == Custom().ClassStringLength()  &&
		 strncmp( &buff[i], Custom().ClassString(),
			  Custom().ClassStringLength() ) == 0 )
		continue;
	    tmpstr.Append(&buff[i], length);
	    if ( Custom().IgnoreDirective(tmpstr) )
		continue;

	    _classname.Append( &buff[i], length );

	    InitFiles();
	    break;
	}

					// see if a logo image is specified
	for ( i = buff.NonWhite(); buff[i]; i++ )
	    if ( buff[i] == ',' )
		break;
	if ( buff[i] )
	{
	    cout << "Sorry, logo images can no longer be specified for "
		 << "individual classes." << endl
		 << "Use the 'logo' option in the customization section "
		 << "for each library." << endl;
	}

    }
					// looking for body sentinel
    else if ( ! _inbody )
    {
	if ( isBodySentinel( buff, Custom() ) )
	    _inbody = TRUE;

	if ( _inbody )
	{
	    _getinh = TRUE;	// get the inheritance info
	    GetInheritance( buff );
	}

	if ( CommentTerminator( buff ) )
	    _libdocdone = TRUE;
	if ( ! _inbody  &&  ! _libdocdone  &&  strlen(_nestedin) < 1  &&
	     ! _hasdoc )
	{		// other text goes to the library
					// class listing and the summary
					// description section.
	    if ( _startdd )
	    {
		_libclass    << "<dd>";
		_summaryfile << "<dd>";
		_startdd = FALSE;
	    }
	    _libclass	    << buff << endl;
	    _summaryfile    << buff << endl;
	    _indexframefile << "<font color=darkgreen><i><small>" << buff
	    		    << "<small></i></font>" << endl;
	}
    }
					// looking for visibility sentinels
    else
    {
	if ( _getinh )
	    GetInheritance( buff );
	else if ( strncmp( &buff[buff.NonWhite()], "public:", 7 ) == 0 )
	{
	    _inpublic	 = TRUE;
	    _inprotected = FALSE;
	    _inprivate   = FALSE;
	}
	else if ( strncmp( &buff[buff.NonWhite()], "protected:", 10 ) == 0 )
	{
	    _inpublic	 = FALSE;
	    _inprotected = TRUE;
	    _inprivate   = FALSE;
	}
	else if ( strncmp( &buff[buff.NonWhite()], "private:", 8 ) == 0 )
	{
	    _inpublic	 = FALSE;
	    _inprotected = FALSE;
	    _inprivate   = TRUE;
	}
	else if ( strncmp( &buff[buff.NonWhite()], "// GROUP:", 9 ) == 0 )
	{
	    ofstream*   outfile = NULL;

	    if ( _inpublic )
		outfile = &_pubfile;
	    else if ( _inprotected )
		outfile = &_protfile;
	    else if ( _inprivate && Custom().ShowPrivates() )
		outfile = &_privfile;
	    if ( outfile )
	    {
		if ( Custom().UseTable() )
		    *outfile << "<tr><td></td></tr><tr><td></td><td>";
		else
		    *outfile << "<dl><dl><dl><dl><dl><dl>" << endl << "<dt>";
		*outfile << "<strong>// " << &buff[buff.NonWhite()+9]
			 << "</strong>";
		if ( Custom().UseTable() )
		    *outfile << "</td></tr>" << endl;
		else
		    *outfile << "</dl></dl></dl></dl></dl></dl>" << endl;
	    }
	}
    }
}


void	ClassDecl::InitFiles()
{
    StringBuff  filename;
    ofstream	mainframe;
    ofstream	declframe;
    ofstream	emptyframe;


    if ( strcmp( _classname, CrossRef().LibName() ) == 0 )
	THROW( "Class name cannot be the same as the library name!" );

    if ( strlen(_nestedin) > 0 )
    {
	if ( Custom().UseTable() )
	    _libclass << "<tr><td align=right valign=top>class</td><td>"
		      << _classname << ";</td></tr>" << endl;
	else
	    _libclass << "<dd>class " << _classname << ";</dd>" << endl;
    }

    if ( Custom().UseFrames() )
	sprintf( filename, "%s~full.bas", (char *)_classname );
    else
	sprintf( filename, "%s.bas", (char *)_classname );
    _mainfile.open( filename, ios::out );
    sprintf( filename, "%s.pub", (char *)_classname );
    _pubfile.open( filename, ios::out );
    sprintf( filename, "%s.prt", (char *)_classname );
    _protfile.open( filename, ios::out );
    if ( Custom().ShowPrivates() )
    {
	sprintf( filename, "%s.prv", (char *)_classname );
	_privfile.open( filename, ios::out );
    }
    sprintf( filename, "%s.lnk", (char *)_classname );
    _linkfile.open( filename, ios::out );
    sprintf( filename, "%s.ind", (char *)_classname );
    _indfile.open( filename, ios::out );
    sprintf( filename, "%s.inh", (char *)_classname );
    _inhfile.open( filename, ios::out );
    sprintf( filename, "%s.sum", (char *)_classname );
    _summaryfile.open( filename, ios::out );
    if ( Custom().UseFrames() )
    {
	sprintf( filename, "%s~decl.bas", (char *)_classname );
	declframe.open( filename, ios::out );
	sprintf( filename, "%s~sid%s", (char *)_classname,Custom().Extension());
	_indexframefile.open( filename, ios::out );
	sprintf( filename, "%s~mbr.bas", (char *)_classname );
	_memberframefile.open( filename, ios::out );
	sprintf( filename, "%s%s", (char *)_classname, Custom().Extension());
	mainframe.open( filename, ios::out );
	emptyframe.open("empty.htm", ios::out);
	if ( ! declframe  ||  ! _indexframefile  ||  ! _memberframefile ||
	     ! mainframe )
	{
	    declframe.close();
	    _indexframefile.close();
	    _memberframefile.close();
	    mainframe.close();
	    emptyframe.close();
	    _mainfile.close();		// trigger error message and exit
	}
    }
    if ( ! _mainfile  ||  ! _summaryfile  ||  ! _pubfile  ||  ! _protfile  ||
	 ! _linkfile  ||  ! _indfile      ||  ! _inhfile  ||
	 (Custom().ShowPrivates() &&  ! _privfile) )
    {
	cout << "Couldn't open temporary files for writing.\n";
	_mainfile.close();
	_pubfile.close();
	_protfile.close();
	_linkfile.close();
	_indfile.close();
	_inhfile.close();
	_privfile.close();
	return;
    }

    cout << "\t\t" << _classname << "..." << endl;

    if ( Custom().MaxNameLength()  &&
	 strlen( _classname ) > Custom().MaxNameLength() )
	cout << "WARNING: Class name '" << _classname
	     << "' is longer than specified name length limit!"
	     << endl << endl;


				// write out preliminary stuff to the files
					// update class reference file
    CrossRef().Index() << "%" << _classname << "%<a href=\""
		       << Custom().WebPath() << _classname
		       << Custom().Extension() << "\" target=_top>"
		       << _classname << "</a>%" << endl;

					// update library class list
    if ( strlen(_nestedin) < 1 )
	_libclass << "<dt><a href=\"" << Custom().WebPath() << _classname
		  << Custom().Extension() << "\" target=_top>" << _classname
		  << "</a>\n";

					// add keyword cross-reference entry
    CrossRef().Keywords() << "CLASSES  " << _classname << " "
			  << Custom().WebPath() << endl;

					// main html file
    _mainfile << "<html><head><title>" << _classname
	      << " Documentation</title></head>" << endl;
    _mainfile << "<body" << Custom().BodyOptions() << "><h"
	      << Custom().BaseHeading() << ">" << endl
	      << "<a name=\"topofdoc\">";
    if ( strlen( _logoimage ) > 0 )
	_mainfile << "<img src=\"" << _logoimage << "\">";
    else
	_mainfile << Custom().Logo();
    _mainfile << _classname << "</a>";
    if ( strlen(_nestedin) > 0 )
	_mainfile << " (from " << _nestedin << ")";
    _mainfile << "</h" << Custom().BaseHeading() << ">"
	      << endl << "<dl>" << endl
	      << "cocooninclude(" << _classname << ".sum)" << endl
	      << "</dl>" << endl
	      << Custom().Rule() << endl;
    Custom().HTMLAtTop( _mainfile );

    _mainfile << "<p><strong>[ " << endl
	      << "<a href=\"" << Custom().WebPath() << CrossRef().LibName()
	      << Custom().Extension() << "\">" << CrossRef().LibName()
	      << "</a> | " << endl
	      << "<a href=\"" << Custom().FilePath() << CrossRef().FileName()
	      << "\">Source</a> | " << endl;
    if ( Custom().AccessImplementation() )
    {
	StringBuff  extn;
	StringBuff  basename;
	int	    i;

	extn.Append(SRCFILEEXT);
	basename.Append(Custom().FilePath());
	basename.Append(CrossRef().FileName());
	for ( i = 0; basename[i]; i++ )
	{
	    if ( basename[i] == '.' )
	    {
		basename[i] = '\0';
		break;
	    }
	}
	i = 0;
	while ( extn[i] )
	{
	    StringBuff  name;

	    name.Append(basename);
	    name.Append(&extn[i]);
	    for ( int j = 0; name[j]; j++ )
	    {
		if ( name[j] == '|' )
		{
		    name[j] = '\0';
		    break;
		}
	    }
	    if ( OS().Exists(name) )
	    {
		_implName.Append(name);
		_mainfile << "<a href=\"" << name << "\">Implementation</a> | "
			  << endl;
		break;
	    }
	    while ( extn[i]  &&  extn[i] != '|' )
		i++;
	    if ( extn[i] )
		i++;
	}
    }
    if ( Custom().UsePerl() )
	_mainfile << "<a href=\"" << Custom().Protocol() << CrossRef().XrefURL()		  << "srchform.htm\">Search</a> | " << endl;
    _mainfile << "<a href=\"" << Custom().Protocol() << CrossRef().XrefURL()
	      << "keyweb" << Custom().Extension()
	      << "\">Keywords</a> | " << endl
	      // << "<a href=\"#quickind\">Quick Index</a> | "
	      << "<a href=\"#aag\">Summary</a> | "
	      << "<a href=\"#parents\">Ancestors</a> | "
	      << "<a href=\"#flat\">All Members</a> | "
	      << "<a href=\"#children\">Descendants</a> "
	      << " ]</strong><p>" << endl;

    _mainfile << "<h" << Custom().BaseHeading()+1 << ">"
	      << "<a name=\"quickind\">" << Custom().QuickRefImage();
    if ( ! Custom().ImageOnly() || ! strlen( Custom().QuickRefImage() ) )
	_mainfile << "Quick Index";
    _mainfile << "</a></h" << Custom().BaseHeading()+1 << ">" << endl
	      << "<dl>" << endl
	      << "cocooninclude(" << _classname << ".ind)" << endl
	      << "</dl>" << endl;

    _mainfile << "<a name=\"aag\">" << Custom().Rule() << "</a><h"
	      << Custom().BaseHeading()+1 << ">"
	      << Custom().AagImage();
    if ( ! Custom().ImageOnly() || ! strlen( Custom().AagImage() ) )
	_mainfile << "Class Summary";
    _mainfile << "</h" << Custom().BaseHeading()+1 << ">" << endl
	      << "cocooninclude(" << _classname << ".inh)" << endl
	      << "<br>{" << endl;

    if ( Custom().UseTable() )
	_mainfile << "<" << TABLESTRING ">" << endl
	      << "<tr><td align=left><font size=+1><i><b>public:</b></i></font>"
	      << endl << "</td></tr>" << endl;
    else
	_mainfile << "<br><i><b>public</b></i>:" << endl << "<dl>" << endl;
    _mainfile << "cocooninclude(" << _classname << ".pub)" << endl;
    if ( ! Custom().UseTable() )
	_mainfile << "</dl>" << endl;

    if ( Custom().UseTable() )
	_mainfile << "<tr><td align=left><font size=+1><i><b>protected:</b>"
		  << "</i></font>" << endl << "</td></tr>" << endl;
    else
	_mainfile << "<i><b>protected</b></i>:" << endl << "<dl>" << endl;
    _mainfile << "cocooninclude(" << _classname << ".prt)" << endl;
    if ( ! Custom().UseTable() )
	_mainfile << "</dl>" << endl;

    if ( Custom().ShowPrivates() )
    {
	if ( Custom().UseTable() )
	    _mainfile << "<tr><td align=left><font size=+1><i><b>private:</b>"
		      << "</i></font>" << endl << "</td></tr>" << endl;
	else
	    _mainfile << "<i><b>private</b></i>:" << endl << "<dl>" << endl;
	_mainfile << "cocooninclude(" << _classname << ".prv)" << endl;
	if ( ! Custom().UseTable() )
	    _mainfile << "</dl>" << endl;
    }
    if ( Custom().UseTable() )
	_mainfile << "</table>" << endl;


    _mainfile << "}; // " << _classname << " <p>" << endl;
    if ( Custom().BackToTop() )
	_mainfile << Custom().BackToTop() << "<i>" << _classname << "</i><p>"
		  << endl;

				// make sure the full path to reach these
				// files is given in the family inheritance
				// information file
    StringBuff  workingdir;

    OS().WorkingDir( workingdir );
    CrossRef().Family() << "#" << _classname << "#" << workingdir << endl;


				// generate keywords automatically by parsing
				// words marked by case-changes or underscores
    if ( Custom().doAutoKeywords() )
    {
	int	i;	    // looping index
	int	hasvowel = FALSE;
	int	keywordstart = 0;
	StringBuff  tmp;

	for ( i = 0; _classname[i]; i++ )
	    if ( isupper( _classname[i] )  ||  _classname[i] == '_' )
	    {
		if ( i - keywordstart > 3   &&  hasvowel )
		{
						// got a keyword
		    tmp.Clear();
		    tmp.Append( &_classname[keywordstart] );
		    tmp[i-keywordstart] = '\0';
		    for ( int j = 0; tmp[j]; j++ )
			tmp[j] = (char) toupper( tmp[j] );
		    CrossRef().Keywords() << tmp << "  " << _classname << "  "
					  << Custom().WebPath() << endl;
		}
		keywordstart = i;
		if ( _classname[i] == '_' )
		    keywordstart++;
		hasvowel = isVowel( _classname[i] );
	    }
	    else if ( isVowel( _classname[i] ) )
		hasvowel = TRUE;
	if ( i - keywordstart > 3   &&  hasvowel )
	{
					// got a keyword
	    tmp.Clear();
	    tmp.Append( &_classname[keywordstart] );
	    tmp[i-keywordstart] = '\0';
	    for ( int j = 0; tmp[j]; j++ )
		tmp[j] = (char) toupper( tmp[j] );
	    CrossRef().Keywords() << tmp << "  " << _classname << "  "
				  << Custom().WebPath() << endl;
	}
    }
    				// initialize frame files if needed
    if ( Custom().UseFrames() )
    {
					// main layout
	mainframe << "<html><head><title>" << _classname
		  << " Documentation</title></head>\n";
	mainframe << "<frameset cols=\"25%,*\">\n";
	mainframe << "<frame src=" << _classname << "~sid"
		  << Custom().Extension() << " name=index>\n";
	mainframe << "<frameset rows=\"60,*,*\">\n";
	mainframe << "<frame src=" << _classname
		  << "~decl" << Custom().Extension()
		  << " name=decl noresize>\n";
	mainframe << "<frame src=" << _classname << "~mbr"
		  << Custom().Extension() << " name=member>\n";
	mainframe << "<frame src=empty.htm name=doc>\n";
	mainframe << "</frameset></frameset></html>\n";
	mainframe.close();

					// declaration frame
	declframe << "<html><head></head><body>\n<h"
		  << Custom().BaseHeading() << ">\n";
	declframe << "cocooninclude(" << _classname << ".inh)\n";
	declframe << "</h" << Custom().BaseHeading() << "></body></html>\n";
	declframe.close();

					// side index frame
	_indexframefile << "<html><head>\n";
	_indexframefile << "<base target=doc></head><body "
			<< "link=darkred alink=darkred vlink=darkgreen>\n";
	if ( strlen( _logoimage ) > 0 )
	    _indexframefile << "<img src=\"" << _logoimage << "\">";
	else
	    _indexframefile << Custom().Logo();

					// member summary frame
	_memberframefile << "<html><head>\n";
	_memberframefile << "<base target=doc></head><body>\n";
	if ( Custom().UseTable() )
	    _memberframefile << "<" << TABLESTRING ">" << endl
	      << "<tr><td align=left><font size=+1><i><b>public:</b></i></font>"
	      << endl << "</td></tr>" << endl;
	else
	    _memberframefile << "<br><i><b>public</b></i>:" << endl
	      << "<dl>" << endl;
	_memberframefile << "cocooninclude(" << _classname << ".pub)" << endl;
	if ( ! Custom().UseTable() )
	    _memberframefile << "</dl>" << endl;

	if ( Custom().UseTable() )
	    _memberframefile << "<tr><td align=left><font size=+1><i>"
	    	<< "<b>protected:</b></i></font>" << endl << "</td></tr>\n";
	else
	    _memberframefile << "<i><b>protected</b></i>:\n<dl>" << endl;
	_memberframefile << "cocooninclude(" << _classname << ".prt)" << endl;
	if ( ! Custom().UseTable() )
	    _memberframefile << "</dl>" << endl;
	if ( Custom().ShowPrivates() )
	{
	    if ( Custom().UseTable() )
		_memberframefile << "<tr><td align=left><font size=+1><i>"
			<< "<b>private:</b></i></font>\n</td></tr>" << endl;
	    else
		_memberframefile << "<i><b>private</b></i>:\n<dl>" << endl;
	    _memberframefile << "cocooninclude(" << _classname << ".prv)\n";
	    if ( ! Custom().UseTable() )
		_memberframefile << "</dl>" << endl;
	}
	if ( Custom().UseTable() )
	    _memberframefile << "</table>" << endl;

					// initial (empty) doc frame
	emptyframe << "<html><head></head><body></body></html>\n";
    }

} // InitFiles


void	ClassDecl::GetInheritance( StringBuff& buff )
{
    int		i = 0;
    StringBuff  tmpbuff;
    int		numbase = 0;

    buff.TrimWhite();
    tmpbuff.Append( buff );
    tmpbuff.HTMLSafe();
    if ( Custom().ForUnimax() &&  tmpbuff[i] != '{' )
	CrossRef().LispDump() << endl << "(make-instance 'c-class" << endl
	    << "\t:class-name \"" << _classname << "\"" << endl;
				// look for any public or protected inheritance
				// clauses
    while ( tmpbuff[i] )
    {
				// skip past private inheritance stuff
	if ( strncmp( &tmpbuff[i], "private", 7 ) == 0  &&
	     isspace( tmpbuff[i+7] ) )
	{
				// ignore up until next ',' or '{'
	    while ( tmpbuff[i]  &&  tmpbuff[i] != ','  &&  tmpbuff[i] != '{' )
		i++;
	}
	else if ( ( strncmp( &tmpbuff[i], "public", 6 ) == 0  &&
		    isspace( tmpbuff[i+6] ) ) ||
		  ( strncmp( &tmpbuff[i], "protected", 9 ) == 0  &&
		    isspace( tmpbuff[i+9] ) ) )
	{
	    int	    j	     = i + 9;
	    int	    ispublic = FALSE;

	    if ( tmpbuff[i+1] == 'u' )
	    {
		j	 = i + 6;
		ispublic = TRUE;
	    }
				// find the name of the parent class and
				// write it out to the cross-referencing
				// data
					// find first non-blank
	    for ( ; tmpbuff[j]; j++ )
		if ( isalnum( tmpbuff[j] )  ||  tmpbuff[j] == '_' )
		    break;

					// skip past 'virtual' keyword
	    if ( strncmp( &tmpbuff[j], "virtual", 7 ) == 0  &&
		 isspace( tmpbuff[j+7] ) )
	    {
		for ( j += 7; tmpbuff[j]; j++ )
		    if ( ! isspace( tmpbuff[j] ) )
			break;
	    }
					// find first non-name character
	    if ( tmpbuff[j] )
	    {
		CrossRef().Family() << "%" << _classname << "%";
		if ( Custom().ForUnimax() )
		{
		    if ( numbase == 0 )
			CrossRef().LispDump() << "\t:base-class '(" << endl;
		    CrossRef().LispDump() << "\t\t\"";
		}
		numbase++;
		for ( ; tmpbuff[j]; j++ )
		    if ( isalnum( tmpbuff[j] ) || tmpbuff[j] == '_' )
		    {
			CrossRef().Family() << tmpbuff[j];
			if ( Custom().ForUnimax() )
			    CrossRef().LispDump() << tmpbuff[j];
		    }
		    else
			break;
		if ( Custom().ForUnimax() )
		    CrossRef().LispDump() << "\"" << endl;
		if ( ispublic )
		    CrossRef().Family() << "%public" << endl;
		else
		    CrossRef().Family() << "%protected" << endl;
		_hasparents = TRUE;
	    }
	}
	if ( tmpbuff[i] == '{' )
	{
	    _getinh = FALSE;
	    break;
	}
	else
	    _inhfile << tmpbuff[i];
	i++;
    }
    _inhfile << endl;
    if ( Custom().ForUnimax()  &&  tmpbuff[i] != '{' )
    {
	if ( numbase > 0 )
	    CrossRef().LispDump() << "\t)" << endl;
	CrossRef().LispDump() << "\t:instance-variables '(" << endl;
    }
} // GetInheritance


int ClassDecl::isBodySentinel( StringBuff& buff, Customize& custom )
{
    int foundbody    = FALSE;
    int sentinelsize = 5;
    int istemplate   = FALSE;

    if ( strncmp( &buff[buff.NonWhite()], "template", 8 ) == 0 )
    {
	foundbody    = TRUE;
	sentinelsize = 8;
	istemplate   = TRUE;
    }

    else if ( strncmp( &buff[buff.NonWhite()], custom.ClassString(),
		       custom.ClassStringLength() ) == 0 )
	foundbody = TRUE;

    else if ( custom.ForUnimax()  &&
	      strncmp( &buff[buff.NonWhite()], "CLASS", 5 ) == 0 )
	foundbody = TRUE;

    if ( isalnum( buff[buff.NonWhite()+sentinelsize] ) )
	foundbody = FALSE;

    if ( foundbody )
    {
	int	i;	// loop control

				    // make sure it isn't just a forward
				    // declaration
	for ( i = buff.NonWhite()+sentinelsize; buff[i]; i++ )
	    if ( buff[i] == ';' )
	    {
		foundbody = FALSE;
		break;
	    }
				    // check to see if the first non-white
				    // character following the name is a
				    // '<', '{' or a ':'
	if ( foundbody  &&  ! istemplate )
	{
	    int	    length;

	    i = buff.NonWhite()+sentinelsize;
	    while ( buff.FoundIdentifier( i, length, TRUE ) )
	    {
		i += length;
	    }
	    for ( ; buff[i]; i++ )
		if ( ! isspace( buff[i] ) )
		    break;
	    if ( buff[i] != ':'  &&  buff[i] != '<'  &&  buff[i] != '{'  &&
		 buff[i] != '\0' )
	    {
		if ( debug )
		    cout << "\t\tRejecting '" << buff << "'" << endl;
		foundbody = FALSE;
	    }
	}
    }

    return foundbody;
} // isBodySentinel



/*------------------------------------------------------------------------
--------------------------------------------------------------------------
				DocHeader
--------------------------------------------------------------------------
------------------------------------------------------------------------*/
int DocHeader::FindSentinel( StringBuff& buff, Customize& custom )
{
    int	    i;

    UNUSED( custom );
    if ( ! isupper( buff[0] ) )
	return FALSE;
    for ( i = 1; buff[i]; i++ )
	if ( islower( buff[i] ) )
	    return FALSE;
    return TRUE;
}


DocHeader::DocHeader( Xref& xref, Customize& custom, char* classname,
		      char* buff, ofstream& maintext, ofstream& quickindex,
		      ofstream& frameindex ) :
    Handler( xref, custom ), _maintext( maintext ), _quickindex( quickindex ),
    _inpreformatted( FALSE ), _initialized( FALSE ), _frameindex(frameindex)
{
    _headername.Append( buff );
    _classname.Append( classname );
}


DocHeader::~DocHeader( )
{
    Preempt();
    if ( Custom().UseTable() )
	_maintext << "</table></dl></dl>" << endl;
    if ( Custom().Preformatted()  ||  _inpreformatted )
	_maintext << "</pre>" << endl;
				// output link back to top
    if ( Custom().BackToTop() )
	_maintext << Custom().BackToTop() << "<i>" << _classname << "</i><p>"
		  << endl;
}

Handler*    DocHeader::NewHandler( StringBuff &buff )
{
    if ( CodeExample::FindSentinel( buff, Custom() ) )
	return new CodeExample( CrossRef(), Custom(), _maintext, _classname );

    return NULL;
} // NewHandler



void	DocHeader::ProcessLine( StringBuff& buff, int& surrendercontrol )
{
    if ( ! _initialized )
    {
	StringBuff  headerurl( _headername );

	headerurl.URLSafe();
					    // output header
	_maintext << "<a name=\"" << headerurl << "\">" << Custom().Rule()
		  << "</a><h" << Custom().BaseHeading()+2 << ">"
		  << _headername << "</h" << Custom().BaseHeading()+2
		  << ">" << endl;
	if ( Custom().Preformatted() )
	    _maintext << "<pre>" << endl;
	if ( Custom().UseTable() )
	    _maintext << "<dl><dl><table width=90%><td>" << endl;
					    // output index entry
	_quickindex << "<dd><a href=\"#" << headerurl << "\">"
		    << _headername << "</a>" << endl;
	if ( Custom().UseFrames() )
	{
	    _frameindex << "<p><a href=\"" << _classname << "~full"
	    	        << Custom().Extension() << "#" << headerurl << "\">"
			<< _headername << "</a><p>\n" << endl;
	}
	_initialized = TRUE;
    }
			// blank lines become paragraph delimiters
			// lines that end the header comment get skipped
			// other lines go verbatim
				// look for common variants of comment
				// separater lines
    if ( CommentTerminator( buff ) )
    {
	surrendercontrol = TRUE;
	return;
    }

				// see if we are in a preformatted section
    if ( strncmp( &buff[buff.NonWhite()], "<pre>", 5 ) == 0 )
	_inpreformatted = TRUE;
    else if ( strncmp( &buff[buff.NonWhite()], "</pre>", 6 ) == 0 )
	_inpreformatted = FALSE;

				// line goes out, see if it goes as a
				// paragraph separator or verbatim
    if ( ! _inpreformatted  &&  ! Custom().Preformatted()  &&
	 ! buff[buff.NonWhite()] )
	_maintext << "<p>" << endl;
    else
	_maintext << buff << endl;
}


/*------------------------------------------------------------------------
--------------------------------------------------------------------------
				Member
--------------------------------------------------------------------------
------------------------------------------------------------------------*/
int Member::FindSentinel( StringBuff& buff, Customize& custom )
{
    if ( strncmp( &buff[buff.NonWhite()], custom.MemberSentinel(),
		    custom.MemSentinelLength() ) == 0 )
	return TRUE;

    if ( custom.NoSentinels() )
    {
	int foundmember = TRUE;
	int nw = buff.NonWhite();

					// rule out common junk
	if ( ! isalnum( buff[nw] )  && buff[nw] != '_'  &&  buff[nw] != '~' )
	    foundmember = FALSE;
	else if ( strncmp( &buff[nw], "extern", 6 ) == 0 )
	    foundmember = FALSE;
	else if ( strncmp( &buff[nw], custom.ClassString(),
		  custom.ClassStringLength() ) == 0 )
	    foundmember = FALSE;
	else if ( custom.ForUnimax() && strncmp( &buff[nw], "CLASS", 5 ) == 0 )
	    foundmember = FALSE;
	else if ( custom.ForUnimax() && strncmp( &buff[nw], "strcat", 6 ) == 0 )
	    foundmember = FALSE;
	else if ( custom.ForUnimax() && strncmp( &buff[nw], "assert", 6 ) == 0 )
	    foundmember = FALSE;
	else if ( custom.ForUnimax() &&
		  strncmp( &buff[nw], "CONTAIN_INTERFACE", 17 ) == 0 )
	    foundmember = FALSE;
	else if ( custom.ForUnimax() &&
		  strncmp( &buff[nw], "CV_IMPORT", 9 ) == 0 )
	    foundmember = FALSE;
	else if ( strncmp( &buff[nw], "template", 8 ) == 0 )
	    foundmember = FALSE;
	else if ( strncmp( buff, "public:", 7 ) == 0 )
	    foundmember = FALSE;
	else if ( strncmp( buff, "protected:", 10 ) == 0 )
	    foundmember = FALSE;
	else if ( strncmp( buff, "private:", 8 ) == 0 )
	    foundmember = FALSE;
	else
	{
	    int	    i;		// loop index
	    int	    parennest = 0;
	    int	    blocknest = 0;

					// try to distinguish from comment
					// text by looking for special
					// syntactic markers
	    foundmember = FALSE;
	    for ( i = nw; buff[i]; i++ )
	    {
		if ( buff[i] == ';'  ||  buff[i] == '('  ||
		     buff[i] == '[' )
		    foundmember = TRUE;
		if ( buff[i] == '(' )	    // make sure parenthesis and
		    parennest++;	// block nesting stay above -1
		else if ( buff[i] == '{' )
		    blocknest++;
		else if ( buff[i] == ')' )
		    parennest--;
		else if ( buff[i] == '}' )
		    blocknest--;
		if ( parennest < 0  ||  blocknest < 0 )
		{
		    foundmember = FALSE;
		    break;
		}
	    }
					// see if it is a simple ADT
	    if ( strncmp( &buff[nw], "typedef", 7 ) == 0 )
		foundmember = TRUE;
	    else if ( strncmp( &buff[nw], "struct", 6 ) == 0  ||
		      strncmp( &buff[nw], "enum", 4 )   == 0 )
	    {
		int	i;

				    // make sure it isn't a forward
				    // declaration
		foundmember = TRUE;
		for ( i = nw; buff[i]; i++ )
		    if ( buff[i] == '{' )
			break;
		    else if ( buff[i] == ';' )
		    {
			foundmember = FALSE;
			break;
		    }
	    }
					// rule out line continuations from
					// # directives
	    for ( i = strlen( buff )-1; i >= 0; i-- )
		if ( ! isspace( buff[i] ) )
		{
		    if ( buff[i] == '\\' )
		    {
			foundmember = FALSE;
			break;
		    }
		}
	}
	if ( foundmember == TRUE )
	    return TRUE;
    }

    return FALSE;

} // FindSentinel


Member::Member( Xref& xref, Customize& custom, ofstream& body,
		ofstream& funcaag, ofstream& dataaag, ofstream& index,
		ofstream& link, char* memberkey, Census& census,
		StringBuff& sentinelline, StringBuff& prevcomment ) :
    Handler( xref, custom ), _doingsig( FALSE ), _doingargs( FALSE ),
    _outasdd( FALSE ), _body( body ), _funcaag( funcaag ), _dataaag(dataaag),
    _ind( index ), _link( link ), _trimdoubleslash( TRUE ),
    _inlinedef( FALSE ), _isdata( FALSE ), _isfunction( FALSE ),
    _ispurevirtual( FALSE ), _bodynest( 0 ), _census( &census ),
    _inpreformatted( FALSE ), _forcesurrender( FALSE ), _hadsentinel( TRUE ),
    _unnamedenum( FALSE )
{
    StringBuff  key2;

    _memberkey.Append( memberkey );
    key2.Append( memberkey );
    key2.Append( "_2" );
				// open up the temporary file used to store
				// the body text until we are sure this member
				// is valid.
    _tmpbody.open( "member.tmp", ios::out );
    if ( ! _tmpbody )
    {
	cout << "Couldn't open member.tmp for writing!" << endl;
	return;
    }
				// output symbolic anchor
    _tmpbody << "<a name=\"" << _memberkey << "\">" << Custom().Rule()
	     << "<h" << Custom().BaseHeading()+2 << ">" << key2
	     << "</h" << Custom().BaseHeading()+2 << "></a>" << endl;
				// put in include-file reference if its
				// at global scope
    if ( _census->isGlobal() )
	_tmpbody << "<strong>#include \"<a href=\"" << Custom().FilePath()
		 << CrossRef().FileName() << "\">" << Custom().FilePath()
		 << CrossRef().FileName() << "</a>\"</strong><p>" << endl;
    if ( Custom().Preformatted() )
	_tmpbody << "<pre>" << endl;
    if ( Custom().DocWithSentinel() )
    {
	StringBuff  linetmp;

	linetmp.Append( "// " );
	linetmp.Append( &sentinelline[sentinelline.NonWhite() +
					Custom().MemSentinelLength()] );
	ProcessLine( linetmp, _forcesurrender );
    }
    if ( strncmp( Custom().MemberSentinel(), "/*", 2 ) == 0 )
	_trimdoubleslash = FALSE;

				// see if this was a member with no sentinel
				// marker
    if ( Custom().NoSentinels() )
    {
	if ( strncmp( &sentinelline[sentinelline.NonWhite()], "//", 2) != 0 &&
	     strncmp( &sentinelline[sentinelline.NonWhite()], "/*", 2) != 0)
	{
				    // treat short preceding comments as group
				// markers
	    int	    len = strlen( prevcomment );
	    if ( len > 0  &&  len < 30 )
	    {
		_groupcomment.Append( prevcomment );
		_groupcomment.MakeIntoDoc( Custom() );
		for ( int i = 0; _groupcomment[i]; i++ )
		    if ( _groupcomment[i] == '\n' )
			_groupcomment[i] = ' ';
		_tmpbody << "<big>No documentation available.</big><p>" << endl;
	    }
	    else if ( len > 0 )
	    {
		prevcomment.MakeIntoDoc( Custom() );
		_tmpbody << prevcomment;
	    }
	    else
		_tmpbody << "<big>No documentation available.</big><p>" << endl;
	    _trimdoubleslash = TRUE;
	    _hadsentinel     = FALSE;
	    ProcessLine( sentinelline, _forcesurrender );
	}
    }

} // constructor


int Member::FindMemberName( StringBuff& buff, int& startname, int& endname,
				int& nameclause, int& argclause )
{
    int	    len = strlen( buff );
    int	    i;
    int	    semicolon;
    int	    paren;

    startname  = 0;
    endname    = len-1;
    nameclause = 0;
    argclause  = 0;

				    // find the semi-colon
    for ( semicolon = len-1; semicolon >= 0; semicolon-- )
	if ( buff[semicolon] == ';' )
	    break;
    if ( semicolon < 0 )
	semicolon = -1;

				// find the first paren of the last nested
				// pairing of parenthesis
    paren = 0;
    for ( i = len-1; i >= 0; i-- )
	if ( buff[i] == ')' )
	    paren++;
	else if ( buff[i] == '(' )
	{
	    paren--;
	    if ( paren == 0 )
		break;
	}
    if ( i < 0 )	    // if no parenthesis found, it must
    {			// be a data item, so make sure the
	_isdata	    = TRUE;	// flags are set appropriately
	_isfunction = FALSE;
	paren	    = -1;
					// look for [] or :
	for ( i = len-1; i >= 0; i-- )
	    if ( buff[i] == '['  ||  buff[i] == ':' )
	    {
		paren = i;
		break;
	    }
    }
    else
	paren = i;

				// special case handling for operator
				// functions...
    for ( int j = paren; buff[j]; j-- )
    {
	if ( strncmp(&buff[j], "operator", 8) == 0 )
	{
	    startname  = j;
	    nameclause = startname;
	    argclause  = paren;
	    endname    = paren-1;
	    while ( endname >= 0 && ( buff[endname] == ' ' ||
				      buff[endname] == '\t' ) )
		endname--;
	    return TRUE;
	}
    }


				// find the end of the member name by
				// starting at the found paren or the semi-
				// colon and tracing back to find the first
				// alphanumeric in the name -- also check for
				// the characters that may mark the end of
				// an operator function declaration (some
				// may already have been replaced by HTML
				// metacharacters)
    if ( paren != -1 )
	endname = paren-1;
    else
    {
	if ( semicolon != -1 )
	    endname = semicolon-1;
	else
	    endname = len-1;
					// search back and see if there is
					// a '=' because this is a constant
	for ( i = endname; i >= 0; i-- )
	    if ( buff[i] == '=' )
		break;
	if ( i > 0 )
	    endname = i-1;
    }
    for ( ; endname >= 0; endname-- )
	if ( isalnum( buff[endname] )  ||  buff[endname] == '_'  ||
	     buff[endname] == '='  || buff[endname] == '&'  ||
	     buff[endname] == '*'  || buff[endname] == ']' ||
	     buff[endname] == '+'  || buff[endname] == '-' ||
	     buff[endname] == '!'  || buff[endname] == ';' ||
	     ( buff[endname] == ')'  &&  buff[endname-1] == '(' ) )
	    break;
    if ( endname < 0 )		// something's screwy -- give up
	return FALSE;

				// find the start of the member name by
				// going backward to find the first character
				// that *isn't* part of an identifier -- keep
				// in mind that there may be HTML metacharacters
				// embedded in the name and that it may be
				// an operator function
    int	    allowpunct = TRUE;
    int	    inmetachar = FALSE;
    for ( startname = endname-1; startname >= 0; startname-- )
    {
	if ( buff[startname] == ';'  &&  ! inmetachar )
	{
	    inmetachar = TRUE;
	}
	else if ( isalnum( buff[startname] )  ||  buff[startname] == '_' )
	{
	    if ( ! inmetachar )
		allowpunct = FALSE;
	}
	else if ( buff[startname] == '&' )
	{
	    if ( inmetachar )
		inmetachar = FALSE;
	    else
		allowpunct = FALSE;
	}
	else if ( ! allowpunct )
	    break;
	else if ( buff[startname] != '('  &&  buff[startname] != '['  &&
		  buff[startname] != '-'  &&  buff[startname] != '='  &&
		  buff[startname] != '~' )
	    break;
    }
    startname++;
				// compensate for HTML escaped characters
    int	    watchout = FALSE;
    if ( startname > 0  &&  buff[startname] == '&' )
    {
	watchout = TRUE;
	if ( strncmp( &buff[startname+1], "amp;", 4 ) == 0 )
	    startname += 5;
	else if ( strncmp( &buff[startname+1], "amp", 3 ) == 0 )
	    startname += 4;
	else if ( strncmp( &buff[startname+1], "gt;", 3 ) == 0 )
	    startname += 4;
	else if ( strncmp( &buff[startname+1], "gt", 2 ) == 0 )
	    startname += 3;
	else if ( strncmp( &buff[startname+1], "lt;", 3 ) == 0 )
	    startname += 4;
	else if ( strncmp( &buff[startname+1], "lt", 2 ) == 0 )
	    startname += 3;
	else
	    watchout = FALSE;
    }

    if ( startname > endname )	    // something's screwy -- give up
	return FALSE;

				// find the start of the name clause by going
				// backward from the start of the name until
				// hitting whitespace -- the identification of
				// the name and arg clauses is done for
				// function signature typedefs
    if ( watchout )
	nameclause = startname;
    else
    {
	for ( nameclause = startname; nameclause >= 0; nameclause-- )
	    if ( ! isalnum( buff[nameclause] )  &&  buff[nameclause] != '_'  &&
		 buff[nameclause] != '~'  &&  buff[nameclause] != '&' )
		break;
	nameclause++;
    }

				// the argument clause is either just past
				// the semicolon, or at the first paren
    if ( paren != -1 )
	argclause = paren;
    else if ( semicolon != -1 )
	argclause = semicolon+1;
    else
	argclause = len;

				// check to make sure it is a function by
				// checking for more than one token for an
				// argument -- make sure to ignore type-
				// casting clauses
    if ( _isfunction )
    {
    }

				// see if it is an unnamed enum
    if ( strncmp( &buff[startname], "enum", 4 ) == 0 )
    {
	char	num[80];

	buff.Clear();
	buff.Append( "Unnamed_Enum UEnum_" );
	buff.Append( CrossRef().FileName() );
	for ( int ii = 0; buff[ii]; ii++ )
	    if ( buff[ii] == '.' )
	    {
		buff[ii] = '_';
		break;
	    }
	buff.Append( "_" );
	sprintf( num, "%d", _census->NextUnnamedEnum() );
	buff.Append( num );
	buff.Append( ";" );
	startname    = 13;
	nameclause   = 13;
	endname	     = strlen( buff ) - 2;
	argclause    = endname+1;
	_unnamedenum = TRUE;
    }

/*
    if ( debug )
	cout << "Debug data : '" << buff << endl << "	 start " << startname
	     << "\tend " << endname << "\tnameclause " << nameclause
	     << "\targclause " << argclause << endl;
*/

    return TRUE;

} // FindMemberName



void	Member::TrimThrowList( StringBuff& buff )
{
    int	    i;		// looping index
    int	    inthrowlist = FALSE;
    int	    parencount  = 0;

    for ( i = 1; buff[i]; i++ )
    {
	if ( inthrowlist )
	{
	    if ( buff[i] == '(' )
		parencount++;
	    else if ( buff[i] == ')' )
	    {
		parencount--;
		if ( parencount == 0 )
		    inthrowlist = FALSE;
	    }
	    buff[i] = ' ';
	}
	else if ( ! isalnum( buff[i-1] )  &&  buff[i-1] != '_' )
	{
	    if ( strncmp( &buff[i], "throw", 5 ) == 0 )
	    {
		int j = i + 5;

		while ( isspace( buff[j] ) )
		    j++;
		if ( buff[j] == '(' )
		{
		    inthrowlist = TRUE;
		    for ( ; i < j; i++ )
			buff[i] = ' ';
		    buff[i] = ' ';
		    parencount = 1;
		}
	    }
	}
    }
} // TrimThrowList

			    // remove the body of the definition (if given)
			    // from the signature
void	Member::TrimBody( StringBuff& buff, int isfunction )
{
    int	    indefinition = 0;   // '{' nesting count
    int	    j;			// looping index
    int	    inComment    = 0;	// 1 - C-style, 2 - C++-style

    for ( j = 0; buff[j]; j++ )
    {
	if ( inComment == 1 )
	{
	    if ( buff[j] == '*'  &&  buff[j+1] == '/' )
		inComment = 0;
	}
	else if ( inComment == 2 )
	{
	    if ( buff[j] == '\n' )
		inComment = 0;
	}
	else if ( buff[j] == '/'  &&  buff[j+1] == '*' )
	{
	    inComment = 1;
	}
	else if ( buff[j] == '/'  &&  buff[j+1] == '/' )
	{
	    inComment = 2;
	}
	else if ( buff[j] == ':'  &&  buff[j+1] != ':'   &&  j > 0  &&
	     buff[j-1] != ':' )		// trim away subclass constructors
	{
	    buff[j] = ';';
	    buff[j+1] = '\0';
	    break;
	}
	else if ( buff[j] == '{' )
	    indefinition++;
	else if ( buff[j] == '}' )
	{
	    buff[j] = ' ';
	    indefinition--;
	    if ( indefinition == 0 )
	    {
		if ( isfunction )
		{
		    int k = j+1;

		    while ( buff[j] && (buff[j] == ' ' || buff[j] == '\t') )
			j++;
		    if ( buff[j] != ';' )
			buff.Append( ";" );
		}
		break;
	    }
	}
	if ( indefinition )
	    buff[j] = ' ';
    }
} // TrimBody



Member::~Member()
{
    int	    i;
    int	    j;
    int	    isvirtual;
    int	    isinline;
    int	    isstatic;
    int	    startname;
    int	    endname;
    int	    nameclause;
    int	    argclause;
    int	    firstparen;
    StringBuff  tmpbuff;
    ofstream*   aag = &_funcaag;

    Preempt();

    if ( Custom().Preformatted() )
	_tmpbody << "</pre>" << endl;

    if ( strlen( _signature ) < 1 )
    {
	cout << "WARNING!  Member identified but no signature found, line "
	     << linenumber << "." << endl;
	return;
    }

    _signature.TrimOldComments();   // get rid of old-style comments
    _signature.TrimComments();	    // get rid of new-style comments
    TrimThrowList( _signature );    // trim out any throw-list
    _signature.TrimWhite();	// clean up the string
    isvirtual  = FALSE;
    isstatic   = FALSE;
    isinline   = _inlinedef;
					// look for 'extern' keyword
    if ( strncmp( _signature, "extern ", 7 ) == 0 )
	for ( j = 0; j < 6; j++ )
	    _signature[j] = ' ';

					// look for '#'
    if ( _signature[0] == '#' )
	_signature[0] = ' ';

					// look for 'virtual', 'inline', and
					// 'static' keywords
    for ( i = 0; _signature[i]; i++ )
    {
	if ( _signature[i] == '('  ||  _signature[i] == '{' )
	    break;
	if ( i == 0  ||  _signature[i-1] == ' ' )
	{
	    if ( strncmp( &_signature[i], "virtual ", 8 ) == 0 )
	    {
		for ( j = 0; j < 7; j++ )
		    _signature[i+j] = ' ';
		isvirtual	= TRUE;
	    }
	    else if ( strncmp( &_signature[i], "static ", 7 ) == 0 )
	    {
		for ( j = 0; j < 6; j++ )
		    _signature[i+j] = ' ';
		isstatic = TRUE;
	    }
	    else if ( strncmp( &_signature[i], "inline ", 7 ) == 0 )
	    {
		for ( j = 0; j < 6; j++ )
		    _signature[i+j] = ' ';
		isinline = TRUE;
	    }
	}
    }

			    // remove the body of the definition (if given)
			    // from the signature
    TrimBody( _signature, _isfunction );
    _signature.TrimWhite();	// clean up the string

			    // trim out the default argument values, if
			    // any -- first find the opening paren of the
			    // declaration...
    j = 0;  // count nested parens
    for ( i = strlen( _signature ) - 1; i >= 0; i-- )
    {
	if ( _signature[i] == ')' )
	    j++;
	if ( _signature[i] == '(' )
	{
	    j--;
	    if ( j == 0 )
		break;
	}
    }
				// move forward, trimming away everything
				// between a '=' and a ',', ')', or ';'
    firstparen = i;
    if ( i > 0 )
    {
	int parencount = 0;
	j = FALSE;
	for ( i++; _signature[i]; i++ )
	{
	    if ( _signature[i] == ',' )
	    {
		j = FALSE;
		parencount = 0;
	    }
	    else if ( _signature[i] == ')' )
	    {
		parencount--;
		if ( parencount == -1 )
		{
		    j = FALSE;
		}
	    }
	    else if ( _signature[i] == '(' )
		parencount++;
	    else if ( _signature[i] == ';' )
		j = FALSE;
	    else if ( _signature[i] == '=' )
		j = TRUE;
	    if ( j )
		_signature[i] = ' ';
	}
    }
			    // trim away excess whitespace to simplify
			    // further processing
    _signature.TrimWhite();


			    // check for characters that might indicate
			    // garbagy input -- abort if any are found
    for ( i = 0; _signature[i]; i++ )
    {
	if ( _signature[i] == '\\'  ||  _signature[i] == '#'  ||
	   ( _signature[i] == '='  &&  firstparen != -1 && i > firstparen ) ||
	   ( i < firstparen  &&  _signature[i] == ':' ) )
	{
	    if ( debug )
		cout << "\t\tAbandoning '" << _signature << "'" << endl;
	    return;
	}
	else if ( strncmp( &_signature[i], "->", 2 ) == 0  &&  i < firstparen )
	{
	    if ( i < 8  ||  strncmp( &_signature[i-8], "operator->", 2 ) != 0)
	    {
		if ( debug )
		    cout << "\t\tAbandoning '" << _signature << "'" << endl;
		return;
	    }
	}
    }

			    // move contents of temporary file to regular
			    // body file
    ifstream	tmpread;
    StringBuff  filebuff;

    _tmpbody.close();
    tmpread.open( "member.tmp", ios::in );
    if ( ! tmpread )
    {
	cout << "Couldn't open member.tmp for reading!" << endl;
	return;
    }
    while ( filebuff.GetLine( tmpread ) )
	_body << filebuff << endl;



			    // sanity check
    if ( strlen( _signature ) > GarbageTolerance )
	cout << "\t\tWARNING: Possible garbage: '" << _signature << "'" << endl;


			    // substitute HTML meta-characters
    _signature.HTMLSafe();


			    // update statistics
    if ( _isdata )
	_census->Data( _unnamedenum );
    else
	_census->Function( isinline, _ispurevirtual );


			    // update aag files
    if ( FindMemberName( _signature, startname, endname, nameclause,
			 argclause) )
    {
	if ( _isdata )
	{
	    aag = &_dataaag;
			// make sure the typedef name isn't duplicated --
			// this can happen, for instance, in the definition
			// of a linked-list node
	    for ( i = 0; i < startname - (endname-startname); i++ )
		if ( strncmp( &_signature[i], &_signature[startname],
			      (endname-startname+1) ) == 0 )
		{
		    char    following;

		    if ( i > 0  &&  ( isalpha(_signature[i-1]) ||
			 _signature[i-1] == '_' ) )
			continue;
		    following = _signature[i+(endname-startname+1)];
		    if ( ! isalnum( following )  &&  following != '_' )
		    {
					// found duplicate -- blank it out
			for ( j = i+endname-startname+1; _signature[j]; j++)
			    _signature[j-(endname-startname+1)] =
							_signature[j];
			_signature[j-(endname-startname+1)] = '\0';
			j = endname-startname+1;
			startname  -= j;
			endname	   -= j;
			nameclause -= j;
			argclause  -= j;
			break;
		    }
		}

	}

	_groupcomment.TrimWhite();
	if ( strlen( _groupcomment ) > 0 )
	{
	    if ( Custom().UseTable() )
		*aag << "<tr><td></td></tr><tr><td></td><td>";
	    else
		*aag << "<dl><dl><dl><dl><dl><dl>" << endl << "<dt>";
	    *aag << "<strong>// " << _groupcomment << "</strong>";
	    if ( Custom().UseTable() )
		*aag << "</td></tr>" << endl;
	    else
		*aag << "</dl></dl></dl></dl></dl></dl>" << endl;
	}

	if ( _isdata  &&  ! _census->isGlobal()  &&  Custom().ForUnimax() )
	    CrossRef().LispDump() << "\t\t(\"";

				// write out up to beginning of name clause
	if ( Custom().UseTable() )
	    *aag << "<tr><td align=right valign=top>";
	else
	    *aag << "<dd>";
	if ( isstatic )
	    *aag << "<strong>static</strong> ";

	if ( nameclause > 0 )
	{
	    strncpy(tmpbuff, _signature, nameclause);
	    tmpbuff[nameclause] = '\0';
	    *aag << tmpbuff;
	    if ( _isdata  &&  ! _census->isGlobal()  &&  Custom().ForUnimax() )
	    {
		StringBuff  tmpstr;

		tmpstr.Append( tmpbuff );
		tmpstr.TrimWhite();
		CrossRef().LispDump() << tmpstr;
	    }
	}


				// write out up to beginning of anchored name
	if ( Custom().UseTable() )
	    *aag << "</td><td>";
	if ( startname > nameclause )
	{
	    strncpy(tmpbuff, &_signature[nameclause], startname-nameclause);
	    tmpbuff[startname-nameclause] = '\0';
	    *aag << tmpbuff;
	    if ( _isdata  &&  ! _census->isGlobal()  &&  Custom().ForUnimax() )
	    {
		StringBuff  tmpstr;

		tmpstr.Append( tmpbuff );
		tmpstr.TrimWhite();
		CrossRef().LispDump() << tmpstr;
	    }
	}
	if ( _isdata  &&  ! _census->isGlobal()  &&  Custom().ForUnimax() )
	    CrossRef().LispDump() << "\" \"";

				// write out anchor and name
	if ( isvirtual )
	    *aag << "<i>";
	*aag << "<a href=\"";
	*aag << _census->Name();
	if ( Custom().UseFrames() )
	    *aag << "~full";
	*aag << Custom().Extension();

	StringBuff  sigurl( _signature );
	sigurl.URLSafe();
	*aag << "#" << sigurl << "\">";

	*aag << "<font color=green>";
	strncpy(tmpbuff, &_signature[startname], endname-startname+1);
	tmpbuff[endname-startname+1] = '\0';
	*aag << tmpbuff << "</font></a>";
	if ( _isdata  &&  ! _census->isGlobal()  &&  Custom().ForUnimax() )
	    CrossRef().LispDump() << tmpbuff << "\")" << endl;

				// update class reference file if necessary
	if ( _isdata  &&  _census->isGlobal() )
	{
	    CrossRef().Index() << "%";
	    SubEscape( CrossRef().Index(), tmpbuff );
	    CrossRef().Index() << "%<a href=\""
			       << Custom().WebPath() << CrossRef().LibName();
	    if ( Custom().UseFrames() )
		CrossRef().Index() << "~full";
	    CrossRef().Index() << Custom().Extension() << "\\#";
	    SubEscape( CrossRef().Index(), sigurl );
	    CrossRef().Index() << "\">";
	    SubEscape( CrossRef().Index(), tmpbuff );
	    CrossRef().Index() << "</a>%" << endl;
	}
	if ( isvirtual )
	    *aag << "</i>";

				// write out up to beginning of argument clause
	if ( argclause > endname+1 )
	{
	    strncpy(tmpbuff, &_signature[endname+1], argclause-endname-1);
	    tmpbuff[argclause-endname-1] = '\0';
	    *aag << tmpbuff;
	}
				// write out the rest
	if ( _signature[argclause] )
	    *aag << &_signature[argclause];

	if ( _ispurevirtual )
	{
	    *aag << "   <font size=-1 color=lightgrey>// pure virtual</font>";
	}

				// finish off the line
	if ( Custom().UseTable() )
	    *aag << "</td></tr>";
	*aag << endl;

				// get the inter-class links set up
	tmpbuff.Clear();
	tmpbuff.Append( &_signature[startname] );
	tmpbuff[endname-startname+1] = '\0';
	InsertLinks( tmpbuff, _signature );

    }
    else
    {
				// if we couldn't identify the member name,
				// just anchor the whole string
	if ( Custom().UseTable() )
	    _dataaag << "<tr><td></td>" << endl << "<td colspan=2>";
	else
	    _dataaag << "<dd>";

	StringBuff  sigurl( _signature );
	sigurl.URLSafe();
	_dataaag << "<a href=\"#" << sigurl << "\">" << _signature
		 << "</a>" << endl;
	if ( Custom().UseTable() )
	    _dataaag << "</td></tr>";
	InsertLinks( _signature, _signature );
    }

				// output declaration body
    if ( ! _isfunction  ||  ! Custom().SkipFullDeclaration() )
    {
	if ( _isfunction )
	    TrimBody( _fulldeclaration, TRUE );
	_body << "<p><pre>" << endl;
	_fulldeclaration.HTMLSafe();
	_body << _fulldeclaration;
	_body << "</pre>" << endl;
    }
    if ( isinline )
	_body << "<p><em>Function is currently defined <strong>inline"
	      << "</strong>.</em></p>" << endl;

				// output link to top of document
    if ( Custom().BackToTop() )
    {
	_body << "<br>" << Custom().BackToTop() << "<i> " << _census->Name()
	      << " </i> <p>" << endl;
    }

} // ~Member


int Member::TerminatorFound( StringBuff& buff )
{
				// is it an empty line?
    if ( _hadsentinel  &&  _trimdoubleslash  &&  ! buff[buff.NonWhite()] )
	return TRUE;
    if ( _forcesurrender )
	return TRUE;
    return FALSE;
}


void	Member::ProcessLine( StringBuff& buff, int& surrendercontrol )
{
    int		i;
    int		j;
    int		gotasterick;
    int		nextissig = FALSE;
    StringBuff  tmpbuff;

    surrendercontrol = FALSE;
    if ( _trimdoubleslash  &&  strncmp( &buff[buff.NonWhite()], "//", 2 ) != 0 )
	_doingsig = TRUE;

				// are we doing a C++-style comment?
    if ( ! _doingsig  &&  strncmp( &buff[buff.NonWhite()], "//", 2 ) == 0 )
    {
				// trim off leading comment characters
	buff[buff.NonWhite()+1] = ' ';
	buff[buff.NonWhite()]   = ' ';
    }

					// look for the end of a C-style comment
    if ( ! _doingsig  &&  ! _trimdoubleslash )
    {
	gotasterick = FALSE;
	for ( i = 0; buff[i]; i++ )
	    if ( buff[i] == '*' )
		gotasterick = TRUE;
	    else if ( gotasterick )
	    {
		gotasterick = FALSE;
		if ( buff[i] == '/' )
		{
		    buff[i-1] = '\0';
		    nextissig = TRUE;
		    break;
		}
	    }
	    else
		gotasterick = FALSE;
    }


    if ( ! _doingsig )
    {
					// blank line
	if ( ! buff[buff.NonWhite()] )
	{
	    if ( ! Custom().Preformatted()  &&  ! _inpreformatted )
		_tmpbody << "<p>" << endl;
	    else
		_tmpbody << endl;
	    if ( _doingargs )
		_tmpbody << "</dl></dl>" << endl;
	    _doingargs = FALSE;
	    _outasdd   = FALSE;
	}

					// argument line
	else if ( ! _inpreformatted  &&  buff[buff.NonWhite()] == '['  &&
		  ! Custom().Preformatted() )
	{
	    StringBuff  argkeyword;
	    int	    nextchar = 0;
	    char*   argimage;

	    if ( ! _doingargs )
	    {
		_tmpbody << "<dl><dl>" << endl;
		_doingargs = TRUE;
	    }
					// find the argument keyword
	    for ( i = buff.NonWhite()+1; buff[i]  &&  buff[i] != ']'; i++ )
		argkeyword[nextchar++] = buff[i];
	    argkeyword[nextchar] = '\0';
	    argkeyword.TrimWhite();
					// see if there is an image for it
	    argimage = Custom().BulletByName( argkeyword );

	    if ( ! argimage )
		_tmpbody << "<dt><i>" << argkeyword << "</i>"
			 << Custom().Bullet();
	    else
		_tmpbody << "<dt>" << argimage;

					// output the rest
	    _outasdd = TRUE;
	    if ( buff[i] )
	    {
		//
		// Assume the first word is the argument name -- output
		// other text as the start of the documentation
		//
					// find first non-space
		for ( j = i+1; buff[j]; j++ )
		    if ( ! isspace( buff[j] ) )
			break;
					// find space on other side of
					// argument name
		for ( ; buff[j]; j++ )
		    if ( isspace( buff[j] ) )
			break;
					// output argument name
		_tmpbody << "<strong>";
		for ( i = i+1; i < j; i++ )
		    _tmpbody << buff[i];
		_tmpbody << "</strong>" << endl;
					// output documentation, if any
		for ( ; buff[j]; j++ )
		    if ( ! isspace( buff[j] ) )
			break;
		if ( buff[j] )
		{
		    _tmpbody << "<dd>" << &buff[j] << endl;
		    _outasdd = FALSE;
		}
	    }
	    else
		_tmpbody << endl;
	}


					// documentation or argument
					// description line
	else
	{
	    if ( _outasdd )
	    {
		_tmpbody << "<dd>";
		_outasdd = FALSE;
	    }
	    _tmpbody << buff << endl;
	    if ( strncmp( &buff[buff.NonWhite()], "<pre>", 5 ) == 0 )
		_inpreformatted = TRUE;
	    else if ( strncmp( &buff[buff.NonWhite()], "</pre>", 6 ) == 0 )
		_inpreformatted = FALSE;
	}
    }

			// doing a signature line
    if ( _doingsig )
    {
	if ( _doingargs )
	    _tmpbody << "</dl></dl>" << endl;
	_doingargs = FALSE;
	_outasdd   = FALSE;

					// if it's the first line, look for
					// 'typedef' or 'enum'
	if ( ! _isdata  &&  ! _isfunction )
	{
	    if ( strncmp( &buff[buff.NonWhite()], "typedef ", 8 ) == 0  ||
		 strncmp( &buff[buff.NonWhite()], "typedef\t", 8 ) == 0  ||
		 strncmp( &buff[buff.NonWhite()], "enum ", 5 ) == 0  ||
		 strncmp( &buff[buff.NonWhite()], "enum\t", 5 ) == 0  ||
		 strncmp( &buff[buff.NonWhite()], "const ", 6 ) == 0  ||
		 strncmp( &buff[buff.NonWhite()], "const\t", 6 ) == 0  ||
		 strncmp( &buff[buff.NonWhite()], "struct ", 7 ) == 0  ||
		 strncmp( &buff[buff.NonWhite()], "struct\t", 7 ) == 0 )
		_isdata = TRUE;
	    else
		_isfunction = TRUE; // might actually be a variable
					// declaration, but we'll deal with
					// that later...
	}

	tmpbuff.Append( buff );

				// look for the termination symbols
	for ( i = 0; tmpbuff[i]; i++ )
	{
	    int	    foundterminator = FALSE;

					// look for terminator
	    if ( tmpbuff[i] == '{' )
		_bodynest++;
	    else if ( tmpbuff[i] == '}' )
	    {
		_bodynest--;
		if ( _bodynest == 0  &&  _isfunction )
		    foundterminator = TRUE;
	    }
	    else if ( _bodynest == 0  &&  tmpbuff[i] == ';' )
		foundterminator = TRUE;


	    if ( foundterminator )
	    {
		if ( _isfunction )
		{
		    int	    ispurevirtual = 0;
		    int	    j;

		    if ( tmpbuff[i] != ';' )
			_inlinedef = TRUE;
					// is the member a pure virtual
					// function?  (Use the ispurevirtual
					// flag as a state indicator...)
		    for ( j = i - 1; j >= 0; j-- )
		    {
			if ( isspace( tmpbuff[j] ) )
			    continue;
			else if ( ispurevirtual == 0  &&  tmpbuff[j] == '0' )
			    ispurevirtual = 1;
			else if ( ispurevirtual == 1  &&  tmpbuff[j] == '=' )
			{
			    int	    k;

			    k = j - 1;
			    while ( k >= 0  &&  tmpbuff[k] == ' ' )
				k--;
			    k++;
			    tmpbuff[k] = ';';
			    for ( k++; k <= i; k++ )
				tmpbuff[k] = ' ';
			    break;
			}
			else
			{
			    ispurevirtual = 0;
			    break;
			}
		    }
		    if ( ispurevirtual )
			_ispurevirtual = TRUE;

		}
		surrendercontrol = TRUE;
		break;
	    }
	    else if ( tmpbuff[i] == '/'  &&  tmpbuff[i+1] == '/' )
		break;
	}

				// buffer up for later output
					// if last character is the terminator,
					// move it back to the end of the
					// previous line
	if ( tmpbuff[tmpbuff.NonWhite()] == ';' &&
	     strlen( _fulldeclaration ) >= 2 )
	{
					    // find beginning of previous line
	    i = strlen( _fulldeclaration ) - 2;
	    while ( i >= 0  &&  _fulldeclaration[i] != '\n' )
		i--;

					    // search forward for end or beginning
					// of comment
	    i++;
	    while ( _fulldeclaration[i]  &&  _fulldeclaration[i] != '\n' &&
		    ( _fulldeclaration[i] != '/'  &&
		      _fulldeclaration[i+1] != '/' ) )
		i++;
	    if ( ! _fulldeclaration[i] )
	    {
		int	k = i-1;

		while ( _fulldeclaration[k] && (_fulldeclaration[k] == ' '  ||
			_fulldeclaration[k] == '\t') )
		    k--;
		if ( _fulldeclaration[k] != ';' )
		    _fulldeclaration.Append(";");
	    }
	    else
	    {
		int j;

					// make room in buffer for additional
					// character
		_fulldeclaration.Append(" ");
					// search back for non-whitespace
		i--;
		while ( _fulldeclaration[i] != '\n'  &&
			isspace( _fulldeclaration[i] ) )
		    i--;
		i++;
					// move everything at the end
		for ( j = strlen( _fulldeclaration ) - 2; j >= i; j-- )
		    _fulldeclaration[j+1] = _fulldeclaration[j];
					// add the terminator
		_fulldeclaration[i] = ';';
	    }
	}
	else
	    _fulldeclaration.Append( tmpbuff );
					// separate lines with return
	_fulldeclaration.Append( "\n" );

				// append to signature buffer
	tmpbuff.TrimWhite();
	tmpbuff.TrimComments();
	_signature.Append( " " );
	_signature.Append( tmpbuff );

    }

    if ( nextissig )
	_doingsig = TRUE;

} // ProcessLine


void	Member::InsertLinks( char* funcname, char* fullsig )
{
    StringBuff  sigurl;
    StringBuff  key2;

    sigurl.Append( fullsig );
    sigurl.URLSafe();
    key2.Append( _memberkey );
    key2.Append( "_2" );
    if ( debug )
	cout <<  "\tLine " << linenumber << ": Member " << funcname
	     << " found." << endl;
    _ind  << "cocoondefine(" << _memberkey << "," << sigurl  << ")" << endl;
    _ind  << "cocoondefine(" << key2	   << "," << fullsig << ")" << endl;
    _link << "%";
    SubEscape( _link, funcname );
    _link << "%<a href=\"\\#";
    SubEscape( _link, sigurl );
    _link << "\">";
    SubEscape( _link, funcname );
    _link << "</a>%" << endl;
} // InsertLinks


/*------------------------------------------------------------------------
--------------------------------------------------------------------------
				LibDoc
--------------------------------------------------------------------------
------------------------------------------------------------------------*/
int LibDoc::FindSentinel( StringBuff& buff, Customize& custom )
{
    return strncmp( &buff[0], custom.LibrarySentinel(),
			custom.LibrarySentinelLength() ) == 0;
}


LibDoc::LibDoc( Xref& xref, Customize& custom, char* libname,
		ofstream& maintext, ofstream& quickindex,
		ofstream& frameindex ) :
    Handler( xref, custom ), _maintext( maintext ), _indenting( FALSE ),
    _quickindex( quickindex ), _namechecked( FALSE ), _frameindex(frameindex)
{
    _libname.Append( libname );
    if ( Custom().MaxNameLength()  &&
	 strlen( libname ) > Custom().MaxNameLength() )
	cout << "WARNING: Library name '" << libname
	     << "' is longer than specified name length limit!"
	     << endl << endl;
}


LibDoc::~LibDoc()
{
    Preempt();
    if ( _indenting )
	_quickindex << "</dl>" << endl;
}


int LibDoc::TerminatorFound( StringBuff& buff )
{
    if ( CommentTerminator( buff ) )
	return TRUE;
    return FALSE;
}


Handler*    LibDoc::NewHandler( StringBuff& buff )
{
    if ( DocHeader::FindSentinel( buff, Custom() ) )
    {
	StringBuff  newheading;
	int	len;
	int	i = 0;

	newheading.Append( buff );
	newheading.TrimWhite();
	len = strlen( _prevheading );
	if ( len > 0  &&  ! _indenting  &&
	     strncmp( _prevheading, newheading, len ) == 0  &&
	     newheading[len] == ':' )
	{
	    _quickindex << "<dl>" << endl;
	    _indenting = TRUE;
	    i	       = len+2;
	}
	else if ( _indenting )
	{
	    if ( strncmp( _prevheading, newheading, len ) != 0 )
	    {
		_quickindex << "</dl>" << endl;
		_indenting = FALSE;
	    }
	    else
		i = len+2;
	}
	if ( i == 0 )
	{
	    _prevheading.Clear();
	    _prevheading.Append( newheading );
	}

	return new DocHeader( CrossRef(), Custom(), _libname,
			      &newheading[i], _maintext, _quickindex,
			      _frameindex );
    }
    return NULL;
}


void	LibDoc::ProcessLine( StringBuff& buff, int& surrendercontrol )
{
    int	    i;
    StringBuff  name;

    UNUSED( surrendercontrol );
					// verify library name
    if ( ! _namechecked )
    {
	_namechecked = TRUE;
					// parse out the library name
	name.Append( buff );
	name.TrimWhite();
	for ( i = 0; name[i]; i++ )
	    if ( ! isalnum( name[i] )  &&  name[i] != '_' )
	    {
		name[i] = '\0';
		break;
	    }
	if ( strcmp( name, _libname ) != 0 )
	{
	    cerr << "Library name mismatch between documentation (" << name
		     << ") and command line (" << _libname << ")!";
	    THROW( "Fatal error!" );
	}

					// see if a logo image is specified
	for ( i = 0; buff[i]; i++ )
	    if ( buff[i] == ',' )
		break;
	if ( buff[i] )
	{
	    ofstream	logofile;
	    char    filename[1000];

	    name.Clear();
	    name.Append( &buff[i+1] );
	    name.TrimWhite();
					// overwrite the logo image file
	    sprintf( filename, "%s.lgo", (char *)_libname );
	    logofile.open( filename, ios::out );
	    logofile << "<img src=\"" << name << "\">" << endl;
	}
    }
}



/*------------------------------------------------------------------------
--------------------------------------------------------------------------
				IgnoreHeading
--------------------------------------------------------------------------
------------------------------------------------------------------------*/
int IgnoreHeading::FindSentinel( StringBuff& buff, Customize& custom )
{
    int	    i;
    StringList* ignorelist = custom.IgnoreList();

    if ( ! isupper( buff[0] ) )
	return FALSE;
    for ( i = 1; buff[i]; i++ )
	if ( islower( buff[i] ) )
	    return FALSE;
    while ( ignorelist )
    {
	if ( strncmp( &buff[0], ignorelist->String(),
		      strlen( ignorelist->String() ) ) == 0  &&
		      ! isalnum( buff[3] ) )
	    return TRUE;
	ignorelist = ignorelist->Next();
    }
    return FALSE;
}


int IgnoreHeading::TerminatorFound( StringBuff& buff )
{
    if ( CommentTerminator( buff ) )
	return TRUE;
    return FALSE;
}



/*------------------------------------------------------------------------
--------------------------------------------------------------------------
				Keyword
--------------------------------------------------------------------------
------------------------------------------------------------------------*/
int Keyword::FindSentinel( StringBuff& buff, Customize& custom )
{
    if ( strncmp( &buff[0], custom.KeywordSentinel(),
		  custom.KeywordSentinelLength() ) == 0  &&
		  ! isalnum( buff[custom.KeywordSentinelLength()] ) )
	return TRUE;
    return FALSE;
}


void	Keyword::ProcessLine( StringBuff& buff, int& surrendercontrol )
{
    int	    i;
    int	    j;
    int	    nextchar = 0;
    StringBuff  tmpbuff;

    surrendercontrol = FALSE;
    if ( CommentTerminator( buff ) )
    {
	surrendercontrol = TRUE;
	return;
    }
				// stick in a terminating keyword separator
    j	      = strlen( buff );
    buff[j]   = ',';
    buff[j+1] = '\0';
				// suck out all the keywords and update
				// the keyword cross-reference file
    for ( i = 0; buff[i]; i++ )
    {
	if ( isalnum( buff[i] )  ||  buff[i] == '_' )
	    tmpbuff[nextchar++] = buff[i];
	else if ( nextchar > 0 )
	{
	    for ( j = 0; j < nextchar; j++ )
		tmpbuff[j] = (char) toupper( tmpbuff[j] );
	    tmpbuff[nextchar] = '\0';
	    CrossRef().Keywords() << tmpbuff << "  " << _classname << "  "
				  << Custom().WebPath() << endl;
	    tmpbuff[0] = '\0';
	    nextchar   = 0;
	}
    }

} // ProcessLine


int Keyword::TerminatorFound( StringBuff& buff )
{
    if ( CommentTerminator( buff ) )
	return TRUE;
    return FALSE;
}


/*------------------------------------------------------------------------
--------------------------------------------------------------------------
				CodeExample
--------------------------------------------------------------------------
------------------------------------------------------------------------*/
int CodeExample::FindSentinel( StringBuff& buff, Customize& custom )
{
    if ( strncmp( &buff[buff.NonWhite()], custom.ExampleSentinel(),
		custom.ExampleSentinelLength() ) == 0  &&
	 ! isalnum( buff[buff.NonWhite()+custom.ExampleSentinelLength()] ) )
	return TRUE;
    return FALSE;
}


CodeExample::CodeExample( Xref& xref, Customize& custom, ofstream& maintext,
			  StringBuff& classname ) :
    Handler( xref, custom ), _maintext( maintext )
{
    _classname.Append( classname );
    _maintext << "<p>" << Custom().ExampleImage() << "<p>" << endl
	      << "<pre>" << endl;
}


CodeExample::~CodeExample()
{
    Preempt();
    _maintext << "</pre>" << endl;
}


void	CodeExample::ProcessLine( StringBuff& buff, int& surrendercontrol )
{
    UNUSED( surrendercontrol );
    buff.HTMLSafe();
    _maintext << buff << endl;
}


int CodeExample::TerminatorFound( StringBuff& buff )
{
    if ( strncmp( &buff[buff.NonWhite()], Custom().EndSentinel(),
		Custom().EndSentinelLength() ) == 0  &&
		! isalnum( buff[buff.NonWhite()+Custom().EndSentinelLength()]) )
	return TRUE;
    return FALSE;
}

