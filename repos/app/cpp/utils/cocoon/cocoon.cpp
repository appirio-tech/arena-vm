
/*------------------------------------------------------------------------
				  COPYRIGHT
--------------------------------------------------------------------------
	  Copyright (C) 1995-2000
	  Jeff Kotula
	  All Rights Reserved.
--------------------------------------------------------------------------
				   FILE LOG
--------------------------------------------------------------------------

    Source code for the main cocoon utility.

------------------------------------------------------------------------*/

#include    "cocoon.h"
#include    "sys.h"


#define NUMTICKS    50  // Spoon!

				// global debugging variables
static int  debug	 = FALSE;
static int  savepartials = FALSE;
static int  savexref	 = FALSE;

const char* execpath = NULL;

static StringBuff	cocoonDir;


/*------------------------------------------------------------------------
--------------------------------------------------------------------------
			    Convenience Functions
--------------------------------------------------------------------------
------------------------------------------------------------------------*/
static void Message( char *message )
{
    char    dummybuff[160];

    if ( message )
	cout << message << endl;
    if ( ! debug )
	return;
    cout << "Hit return to continue...";
    cin.getline( dummybuff, 159 );
    cout << endl;
} // Message

/*------------------------------------------------------------------------
------------------------------------------------------------------------*/
void	ReplaceExt( StringBuff& filename, char* newext )
{
    int	    i;

    for ( i = 0; filename[i]; i++ )
	if ( filename[i] == '.' )
	{
	    filename[i] = '\0';
	    break;
	}
    if ( newext )
	filename.Append( newext );
} // ReplaceExt

/*------------------------------------------------------------------------
------------------------------------------------------------------------*/
static void UpdateTick( int reset )
{
    static int  tickcount = 0;

    if ( reset )
    {
	tickcount = 0;
	cout << "\t";
	return;
    }
    cout << ".";
    tickcount++;
    if ( tickcount % NUMTICKS == 0 )
	cout << endl << "\t";
    cout.flush();
} // UpdateTick


/*------------------------------------------------------------------------
--------------------------------------------------------------------------
			    LibraryIter
--------------------------------------------------------------------------
------------------------------------------------------------------------*/
void	LibraryIter::First()
{
    _currlib = _liblist;
    _webdir.Clear();
    if ( _currlib )
    {
	_webdir.Append( _webroot );
	if ( strlen( _webdir ) )
	    _webdir.Append( OS().PathDivider() );
	_webdir.Append( _currlib->WebSubDir() );
    }
} // First

void	LibraryIter::Next()
{
    if ( ! _currlib )
	return;
    _currlib = _currlib->Next();
    _webdir.Clear();
    if ( _currlib )
    {
	_webdir.Append( _webroot );
	if ( strlen( _webdir ) )
	    _webdir.Append( OS().PathDivider() );
	_webdir.Append( _currlib->WebSubDir() );
    }
} // Next


/*------------------------------------------------------------------------
--------------------------------------------------------------------------
			    FileIter
--------------------------------------------------------------------------
------------------------------------------------------------------------*/
void	FileIter::First()
{
    if ( _filelist )
    {
	delete _filelist;
	_filelist = NULL;
	_currfile = NULL;
    }
    LibraryIter::First();
    while ( ! _currfile  &&  ! LibraryIter::isDone() )
    {
	if ( _sourcedir )
	    _filelist = OS().DirList( CurrLibrary()->SourceDir(), _ext );
	else
	    _filelist = OS().DirList( WebDir(), _ext );
	if ( ! _filelist )
	    LibraryIter::Next();
	_currfile = _filelist;
    }
} // First

void	FileIter::Next()
{
    if ( ! _currfile )
	return;
    _currfile = _currfile->Next();
    if ( _currfile == NULL )
    {
	delete _filelist;
	_filelist = NULL;
	while ( ! _currfile  &&  ! LibraryIter::isDone() )
	{
	    LibraryIter::Next();
	    if ( LibraryIter::isDone() )
		break;
	    if ( _sourcedir )
		_filelist = OS().DirList( CurrLibrary()->SourceDir(), _ext );
	    else
		_filelist = OS().DirList( WebDir(), _ext );
	    _currfile = _filelist;
	}
    }
} // Next

int FileIter::isDone()
{
    return _currfile == NULL  ||  LibraryIter::isDone();
} // Next

char*   FileIter::FileName()
{
    if ( ! _currfile )
	return NULL;
    return _currfile->String();
} // FileName


/*------------------------------------------------------------------------
--------------------------------------------------------------------------
			    CustFile
--------------------------------------------------------------------------
------------------------------------------------------------------------*/

/*------------------------------------------------------------------------
------------------------------------------------------------------------*/
CustFile::~CustFile()
{
			// recursively delete any other nodes following
			// this one
    if ( _next )
	delete _next;
    _next = NULL;
}


/*------------------------------------------------------------------------
------------------------------------------------------------------------*/
void	CustFile::AddLine( char* nextline )
{
    CustFile*   line;
    CustFile*   newnode;

    if ( ! nextline )
	return;
			// if the end of the list is empty, put the
			// line there, otherwise allocate a new node
    line = this;
    while ( line->_next )
	line = line->_next;
    if ( strlen( line->_line ) == 0 )
    {
	line->_line.Append( nextline );
	return;
    }

    newnode = new CustFile( nextline );
    if ( ! newnode )
	THROW( "Memory allocation failure!" );
    line->_next = newnode;

} // AddLine


/*------------------------------------------------------------------------
------------------------------------------------------------------------*/
void	CustFile::Output( ostream& str )
{
    CustFile*   line;

    line = this;
    while ( line )
    {
	str << line->_line << endl;
	line = line->_next;
    }
} // Output


/*------------------------------------------------------------------------
--------------------------------------------------------------------------
			    OtherData
--------------------------------------------------------------------------
------------------------------------------------------------------------*/

/*------------------------------------------------------------------------
------------------------------------------------------------------------*/
OtherData::OtherData( char* extension, char* srcdir, char* websubdir,
		      int isbinary ) :
    _next( NULL ), _isbinary( isbinary )
{
    assert( extension != NULL  &&  strlen( extension ) > 0 );
    assert( srcdir != NULL     &&  strlen( srcdir ) > 0 );
    assert( srcdir[0] == '/'   ||  strncmp(":/", &srcdir[1], 2) == 0);
    assert( ! websubdir  ||  websubdir[0] != '/' );

    if ( extension[0] != '.' )
	_extension.Append( "." );
    _extension.Append( extension );
    _srcdir.Append( srcdir );
    _websubdir.Append( websubdir );
} // OtherData

/*------------------------------------------------------------------------
------------------------------------------------------------------------*/
OtherData::~OtherData()
{
			// recursive delete...
    if ( _next )
	delete _next;
    _next = NULL;
} // destructor


/*------------------------------------------------------------------------
--------------------------------------------------------------------------
			    LibraryData
--------------------------------------------------------------------------
------------------------------------------------------------------------*/

/*------------------------------------------------------------------------
------------------------------------------------------------------------*/
LibraryData::LibraryData( char* name, char* srcdir, char* websubdir )
{
    assert( name != NULL       &&  strlen( name ) > 0 );

    _name.Append( name );
    _srcdir.Append( srcdir );
    _websubdir.Append( websubdir );
    _next = NULL;
}


/*------------------------------------------------------------------------
------------------------------------------------------------------------*/
LibraryData::~LibraryData()
{
			// recursive delete...
    if ( _next )
	delete _next;
    _next = NULL;
}


/*------------------------------------------------------------------------
--------------------------------------------------------------------------
			    DocSetConfig
--------------------------------------------------------------------------
------------------------------------------------------------------------*/

/*------------------------------------------------------------------------
------------------------------------------------------------------------*/
DocSetConfig::DocSetConfig( char* filename, int localonly ) :
    _liblist( NULL ), _otherlist( NULL ), _localonly( localonly ),
    _useperl( FALSE ), _useframes(FALSE), _accessImp( FALSE )
{
    if ( strlen( filename ) < 1 )
	THROW( "No filename specified." );
    _filename.Append( filename );
    _ext.Append( ".html" );
    _hyperlinks = NULL;
    if ( ! localonly )
	LoadConfigFile();
    else
    {
	_liblist = new LibraryData( filename, NULL, NULL );
	if ( ! _liblist )
	    THROW( "Memory allocation error!" );
    }
} // constructor


/*------------------------------------------------------------------------
------------------------------------------------------------------------*/
DocSetConfig::~DocSetConfig()
{
    if ( _liblist )
	delete _liblist;
    _liblist = NULL;
    if ( _hyperlinks )
	delete _hyperlinks;
    _hyperlinks = NULL;
} // destructor


/*------------------------------------------------------------------------
------------------------------------------------------------------------*/
void DocSetConfig::Generate()
{
TRY
{
    StringBuff	    cmd;    // command string for shell

    if ( ! _liblist )
	return;
    cout << endl << "Starting generation of document set." << endl;

    //
    //  Create the output web directories--one per library.
    //  Do not remove or alter any existing directories.
    //
				// iterates through all the libraries
    LibraryIter	    libiter( *_liblist, _webroot );

    if ( ! _localonly )
    {
	libiter.First();
	cout << endl << "Making web directories..." << endl;
	while ( ! libiter.isDone() )
	{
	    if ( OS().Exists( libiter.WebDir() ) )
		cout << "\tOverwriting files in "
		     << libiter.CurrLibrary()->WebSubDir() << endl;
	    else
		OS().CreateDir( libiter.WebDir() );
	    if ( strcmp( libiter.WebDir(),
			 libiter.CurrLibrary()->SourceDir()) != 0 )
	    {
		OS().RemoveAll( libiter.WebDir(), INCFILEEXT );
		OS().RemoveAll( libiter.WebDir(), SRCFILEEXT );
	    }
	    OS().RemoveAll( libiter.WebDir(), Extension() );
	    libiter.Next();
	}
	Message( "Done." );
    }


    //
    //  Copy the source header files from the source directory
    //  to the web directories.
    //
    if ( ! _localonly )
    {
			// iterates through all the header files in the
			// source directories
	StringBuff  extns;
	extns.Append(INCFILEEXT);
	if ( _accessImp )
	{
	    extns.Append("|");
	    extns.Append(SRCFILEEXT);
	}

	FileIter    srciter( *_liblist, _webroot, extns, TRUE );

	srciter.First();
	cout << endl << "Copying source files..." << endl;
	UpdateTick( TRUE );
	while ( ! srciter.isDone() )
	{
	    StringBuff  src;	// source file path
	    StringBuff  dest;   // destination path

	    UpdateTick( FALSE );
					// construct the path strings
	    src.Append( srciter.CurrLibrary()->SourceDir() );
	    if ( strlen( src ) )
		src.Append( OS().PathDivider() );
	    src.Append( srciter.FileName() );
	    dest.Append( srciter.WebDir() );
	    if ( strlen( dest ) )
		dest.Append( OS().PathDivider() );
	    dest.Append( srciter.FileName() );
					// copy the file
	    OS().CopyFile( src, dest, FALSE );

	    srciter.Next();
	}
	cout << endl;
	Message( "Done." );
    }


    //
    //  Write out the customization files for each library
    //  to the directories.
    //
    libiter.First();
    cout << endl << "Writing out customization data..." << endl;
    while ( ! libiter.isDone() )
    {
	StringBuff  filename;
					// build the file name
	filename.Append( libiter.WebDir() );
	if ( strlen( filename ) )
	    filename.Append( OS().PathDivider() );
	filename.Append( "cocoon.ini" );
					// open the file
	ofstream    custfile( filename, ios::out );

	if ( ! custfile )
	    cout << "Couldn't open file " << filename << " for writing."
		 << endl;
	else
	{
					// write out the customization file
	    custfile << "#defaults" << endl;
	    _defaultcust.Output( custfile );
	    custfile << "#end defaults" << endl << endl
		     << "#customizations" << endl;
	    libiter.CurrLibrary()->Overrides().Output( custfile );
	    custfile << "#end customizations" << endl;
	}
	custfile.close();
	libiter.Next();
    }
				// change to the web root and write out
				// a customization file
    OS().ChangeDir( _webroot );
    Cleanup( ".xrf" );		    // get rid of old data

    ofstream	webcust( "cocoon.ini", ios::out );
    if ( ! webcust )
	cout << "Couldn't open file " << _webroot << OS().PathDivider()
	     << "cocoon.ini for writing." << endl;
    _defaultcust.Output( webcust );
    webcust.close();
    Message( "Done." );

    //
    //  Copy search form stuff if needed.
    //
    StringBuff	fileName;
    if ( _useperl )
    {
	OS().Remove("srchkey.pl");
	fileName.Append(cocoonDir);
	fileName.Append("srchkey.pl");
	OS().CopyFile(fileName, "srchkey.pl", FALSE);
	OS().Remove("srchform.htm");
	fileName.Clear();
	fileName.Append(cocoonDir);
	fileName.Append("srchform.htm");
	OS().CopyFile(fileName, "srchform.htm", FALSE);
    }
    OS().Remove("coclogo.gif");
    fileName.Clear();
    fileName.Append(cocoonDir);
    fileName.Append("coclogo.gif");
    OS().CopyFile(fileName, "coclogo.gif", TRUE);


    //
    //  For each source file in each web directory, generate
    //  the basic and partial files using cocoon.
    //
    StringBuff  linkfile;   // master cross-class link file
    StringBuff  keyfile;    // master keyword file
    StringBuff  familyfile; // master inheritance data file
    StringBuff  xrefurl;    // URL to cross-reference file

				// iterates through all the header files in the
				// source directories
    FileIter	websrciter( *_liblist, _webroot, INCFILEEXT, FALSE );
    StringBuff  lastlibname;

					// build up the file locations
    linkfile.Append( _webroot );	// base paths
    if ( strlen( linkfile ) )
	linkfile.Append( OS().PathDivider() );
    keyfile.Append( linkfile );
    familyfile.Append( linkfile );
    linkfile.Append( "classes.xrf" );	    // file names
    keyfile.Append( "keyword.xrf" );
    familyfile.Append( "family.xrf" );

    xrefurl.Append( _urlroot );
    if ( strcmp(&xrefurl[(int)strlen(xrefurl)-1], OS().PathDivider()) != 0 )
	xrefurl.Append( OS().PathDivider() );

    websrciter.First();
    cout << endl << "Generating basic and partial files..." << endl;
    while ( ! websrciter.isDone() )
    {
					// make that directory the working
					// directory
	OS().ChangeDir( websrciter.WebDir() );

					// build up command string
	cmd.Clear();
	if ( execpath )
	    cmd.Append( execpath );
	if ( debug )
	    cmd.Append( "cobweb -d -l " );
	else
	    cmd.Append( "cobweb -l " );
	cmd.Append( linkfile );
	cmd.Append( " -k " );
	cmd.Append( keyfile );
	cmd.Append( " -f " );
	cmd.Append( familyfile );
	if ( strlen( _urlroot ) )
	{
	    cmd.Append( " -w " );
	    cmd.Append( _urlroot );
	    if ( strcmp(&_urlroot[(int)strlen(_urlroot)-1],OS().PathDivider()) != 0 )
		cmd.Append( OS().PathDivider() );
	    cmd.Append( websrciter.CurrLibrary()->WebSubDir() );
	}
	if ( strlen( xrefurl ) > 0 )
	{
	    cmd.Append( " -x " );
	    cmd.Append( xrefurl );
	}
	cmd.Append( " " );
	cmd.Append( websrciter.CurrLibrary()->Name() );
	cmd.Append( " " );
	cmd.Append( websrciter.FileName() );

					// ship it to the shell
	if ( strcmp( lastlibname, websrciter.CurrLibrary()->Name() ) != 0 )
	{
	    cout << "    Library " << websrciter.CurrLibrary()->Name() << endl;
	    lastlibname.Clear();
	    lastlibname.Append( websrciter.CurrLibrary()->Name() );
	}
	if ( debug )
	    cout << cmd << endl;
	OS().System( cmd );

	websrciter.Next();
    }
			// make sure the signature file gets concatenated
			// onto the end of each library file
    libiter.First();
    while ( ! libiter.isDone() )
    {
	LibraryData*	currlib = libiter.CurrLibrary();
	StringBuff  libfile;
	StringBuff  sigfile;

	libfile.Append( libiter.WebDir() );
	if ( strlen( libfile ) )
	    libfile.Append( OS().PathDivider() );
	libfile.Append( currlib->Name() );
	libfile.Append( ".lbs" );

	if ( OS().Exists( libfile ) )
	{
	    sigfile.Append( libiter.WebDir() );
	    if ( strlen( sigfile ) )
		sigfile.Append( OS().PathDivider() );
	    sigfile.Append( currlib->Name() );
	    sigfile.Append( ".sig" );

	    OS().Concatenate( libfile, sigfile );
	}
	libiter.Next();
    }
    Cleanup( ".sig" );
    Message( "Done." );


    //
    //  For each class in each library, generate the lineage-
    //  based partial files.  (lineage just uses the data in the
    //  combined lineage files of all the libraries...)
    //
					// change to the web root directory
    OS().ChangeDir( _webroot );
					// build the command string
    cmd.Clear();
    if ( execpath )
	cmd.Append( execpath );
    cmd.Append( "lineage -f " );
    cmd.Append( familyfile );
					// ship it to the shell
    cout << endl << "Generating lineage partial files..." << endl;
    if ( debug )
	cout << cmd << endl;
    OS().System( cmd );
    Message( "Done." );


    //
    //  Combine the fragments for each class of each library
    //  into the basic HTML page.
    //
			// iterates through all the basic class files
			// found in each web directory
    FileIter	basiciter( *_liblist, _webroot, ".bas", FALSE );

    basiciter.First();
    cout << endl << "Combining class basic and partial files..." << endl;
    UpdateTick( TRUE );
    while ( ! basiciter.isDone() )
    {
	StringBuff  outname;
	StringBuff  exclude;
	int	i;

	UpdateTick( FALSE );
					// make that directory the working
					// directory
	OS().ChangeDir( basiciter.WebDir() );

					// construct the output file name
	outname.Append( basiciter.FileName() );
	for ( i = 0; outname[i]; i++ )
	    if ( outname[i] == '.' )
	    {
		outname[i] = '\0';
		break;
	    }
	exclude.Append( outname );
	outname.Append( ".cmb" );

					// build up command string
	cmd.Clear();
	if ( execpath )
	    cmd.Append( execpath );
	cmd.Append( "recomb -x " );
	cmd.Append( exclude );
	cmd.Append( " " );
	cmd.Append( basiciter.FileName() );
	cmd.Append( " " );
	cmd.Append( outname );
					// ship it to the shell
	if ( debug )
	    cout << cmd << endl;
	OS().System( cmd );

	basiciter.Next();
    }
    cout << endl;
    Message( "Done." );


    //
    //  Combine the fragments of each library page into its
    //  basic HTML page.
    //
			// iterates through all the basic library files
			// found in each web directory
    FileIter	baslibiter( *_liblist, _webroot, ".lbs", FALSE );

    baslibiter.First();
    cout << endl << "Combining library basic and partial files..." << endl;
    UpdateTick( TRUE );
    while ( ! baslibiter.isDone() )
    {
	StringBuff  outname;

	UpdateTick( FALSE );
					// make that directory the working
					// directory
	OS().ChangeDir( baslibiter.WebDir() );

					// construct the output file name
	outname.Append( baslibiter.FileName() );
	ReplaceExt( outname, ".cmb" );
					// build up command string
	cmd.Clear();
	if ( execpath )
	    cmd.Append( execpath );
	cmd.Append( "recomb " );
	cmd.Append( baslibiter.FileName() );
	cmd.Append( " " );
	cmd.Append( outname );
					// ship it to the shell
	if ( debug )
	    cout << cmd << endl;
	OS().System( cmd );

	baslibiter.Next();
    }
    cout << endl;
    Cleanup( ".bas" );
    Cleanup( ".pub" );
    Cleanup( ".prt" );
    Cleanup( ".prv" );
    Cleanup( ".inh" );
    Cleanup( ".cls" );
    Cleanup( ".fnc" );
    Cleanup( ".lgo" );
    Cleanup( ".dta" );
    Cleanup( ".flt" );
    Cleanup( ".ind" );
    Cleanup( ".sum" );
    Cleanup( ".lbs" );
    Cleanup( ".fin" );
    Message( "Done." );


    //
    //  Insert within-page hypertext links to member functions.
    //
			// iterates through all the combined HTML
			// files in all libraries
    FileIter	combiter( *_liblist, _webroot, ".cmb", FALSE );

    combiter.First();
    cout << endl << "Inserting within-page hypertext links..." << endl;
    UpdateTick( TRUE );
    while ( ! combiter.isDone() )
    {
	StringBuff  subfilename;
	StringBuff  outfilename;

	UpdateTick( FALSE );
					// make that directory the working
					// directory
	OS().ChangeDir( combiter.WebDir() );

					// construct other file names
	subfilename.Append( combiter.FileName() );
					    // trim off frame-based name munging
	for ( int i = strlen(subfilename)-1; i >= 0; i-- )
	    if ( subfilename[i] == '~' )
	    {
		subfilename[i] = '\0';
		break;
	    }
	ReplaceExt( subfilename, ".lnk" );
	outfilename.Append( combiter.FileName() );
	ReplaceExt( outfilename, ".cal" );

					// construct command string
	cmd.Clear();
	if ( execpath )
	    cmd.Append( execpath );
	cmd.Append( "anchor -m " );
	cmd.Append( subfilename );
	cmd.Append( " " );
	cmd.Append( combiter.FileName() );
	cmd.Append( " " );
	cmd.Append( outfilename );
					// ship it to the shell
	if ( debug )
	    cout << cmd << endl;
	OS().System( cmd );

	combiter.Next();
    }
    cout << endl;
    Cleanup( ".cmb" );
    Message( "Done." );


    //
    //  Construct the global keyword cross-reference page.
    //  Put it in the web root directory.
    //
    OS().ChangeDir( _webroot );
    cmd.Clear();
    if ( execpath )
	cmd.Append( execpath );
    cmd.Append( "keyweb " );
    if ( execpath )
    {
	cmd.Append( " -p " );
	cmd.Append( execpath );
    }
    cmd.Append( " keyword.xrf" );
    cout << endl << "Constructing the keyword cross-reference page..." << endl;
    if ( debug )
	cout << cmd << endl;
    OS().System( cmd );
    Cleanup( ".mnu" );
    Cleanup( ".bas" );
    Message( "Done." );


    //
    //  Add the global substitution lists to the master cross-
    //  linking file.
    //
			// copy the class xrf data to a new temp file...
    OS().ChangeDir( _webroot );
    OS().CopyFile( "classes.xrf", "links.tmp", FALSE );

    if ( _hyperlinks )
    {
	cout << endl << "Adding custom links..." << endl;

	ofstream    masterfile( "links.tmp", ios::app );
	if ( ! masterfile )
	    cout << "Could not open " << _webroot << "/links.tmp "
		 << "for appending." << endl;
	else
	{
	    StringList*	    nextlink = _hyperlinks;

	    while ( nextlink )
	    {
		int	i;
		StringBuff  target;
		StringBuff  repl;

		target.Append( nextlink->String() );
		repl.Append( GetSecondWord( nextlink->String() ) );
		for ( i = 0; target[i]; i++ )
		    if ( isspace( target[i] ) )
		    {
			target[i] = '\0';
			break;
		    }

		masterfile << "%";
		SubEscape( masterfile, target );
		masterfile << "%";
		SubEscape( masterfile, repl );
		masterfile << "%" << endl;
		nextlink = nextlink->Next();
	    }
	    masterfile.close();
	}
	Message( "Done." );
    }


    //
    //  Insert cross-page hypertext links in each HTML page
    //  in every library.
    //
			// iterates through all the combined and
			// linked HTML files in all libraries
    FileIter	caliter( *_liblist, _webroot, ".cal", FALSE );

    caliter.First();
    cout << endl << "Inserting cross-page links..." << endl;
    UpdateTick( TRUE );
    while ( ! caliter.isDone() )
    {
	StringBuff  outfilename;
	StringBuff  classname;

	UpdateTick( FALSE );
					// make that directory the working
					// directory
	OS().ChangeDir( caliter.WebDir() );

					// construct other file names
	outfilename.Append( caliter.FileName() );
	ReplaceExt( outfilename, Extension() );

					// get the basic class name for
					// substitution exclusion
	classname.Append( caliter.FileName() );
	ReplaceExt( classname, NULL );
					    // trim off frame-based name munging
	for ( int i = strlen(classname)-1; i >= 0; i-- )
	    if ( classname[i] == '~' )
	    {
		classname[i] = '\0';
		break;
	    }

					// construct command string
	cmd.Clear();
	if ( execpath )
	    cmd.Append( execpath );
	cmd.Append( "anchor -x " );
	cmd.Append( classname );
	cmd.Append( " " );
	cmd.Append( _webroot );
	if ( strlen( _webroot ) )
	    cmd.Append( OS().PathDivider() );
	cmd.Append( "links.tmp " );
	cmd.Append( caliter.FileName() );
	cmd.Append( " " );
	cmd.Append( outfilename );
					// ship it to the shell
	if ( debug )
	    cout << cmd << endl;
	OS().System( cmd );

	caliter.Next();
    }
    cout << endl;
    Cleanup( ".cal" );
    Cleanup( ".lnk" );
    Message( "Done." );

    //
    //  Convert absolute URL references to relative.
    //
			// iterates through all the HTML files in all libraries
    FileIter	htmiter( *_liblist, _webroot, Extension(), FALSE );

    htmiter.First();
    cout << endl << "Relativizing links..." << endl;
    UpdateTick( TRUE );
    while ( ! htmiter.isDone() )
    {
	UpdateTick( FALSE );
					// make that directory the working
					// directory
	OS().ChangeDir( htmiter.WebDir() );

					// construct command string
	cmd.Clear();
	if ( execpath )
	    cmd.Append( execpath );
	cmd.Append( "relativize WEBROOT/" );
	cmd.Append( htmiter.CurrLibrary()->WebSubDir() );
	cmd.Append( " " );
	cmd.Append( htmiter.FileName() );
	cmd.Append( " " );
					// ship it to the shell
	if ( debug )
	    cout << cmd << endl;
	OS().System( cmd );

	htmiter.Next();
    }
    				// don't forget keyweb.htm
    UpdateTick( FALSE );
    OS().ChangeDir( _webroot );
    cmd.Clear();
    if ( execpath )
	cmd.Append( execpath );
    cmd.Append( "relativize WEBROOT/ keyweb" );
    if ( _useframes )
	cmd.Append("~main");
    cmd.Append( Extension() );
    if ( debug )
	cout << cmd << endl;
    OS().System( cmd );
    cout << endl;
    Message( "Done." );



    //
    //  Copy all the auxiliary data to the documentation directories.
    //
    if ( _otherlist )
    {
	OtherData*  dat = _otherlist;

	cout << endl << "Copy auxiliary data files..." << endl;
	UpdateTick( TRUE );
	while ( dat )
	{
	    StringList*	    auxfile;
	    StringList*	    fil;
	    StringBuff	    destdir;

				// build up the destination directory and
				// make sure it exists
	    destdir.Append( _webroot );
	    if ( strlen( dat->WebSubDir() ) > 0 )
	    {
		destdir.Append( OS().PathDivider() );
		destdir.Append( dat->WebSubDir() );
	    }
	    if ( ! OS().Exists( destdir ) )
		OS().CreateDir( destdir );
	    destdir.Append( OS().PathDivider() );

				// copy all the files
	    auxfile = OS().DirList( dat->SourceDir(), dat->Extension() );
	    fil = auxfile;
	    while ( fil )
	    {
		StringBuff  src;
		StringBuff  dest;

		UpdateTick( FALSE );
		src.Append( dat->SourceDir() );
		src.Append( OS().PathDivider() );
		src.Append( fil->String() );
		dest.Append( destdir );
		dest.Append( fil->String() );
		OS().CopyFile( src, dest, dat->isBinary() );

		fil = fil->Next();
	    }
	    if ( auxfile )
		delete auxfile;
	    dat = dat->Next();
	}
	cout << endl;
	Message( "Done." );
    }

    //
    //  Remove all the temporary files from the web directories.
    //
    Cleanup( ".ini" );
    Cleanup( ".tmp" );
    if ( ! savexref  &&  ! _useperl )
	Cleanup( ".xrf" );


    cout << endl << "COMPLETELY DONE!" << endl << endl;
}
CATCH( char* str )
{
#ifdef  NOEXCEPTIONS
	const char* str="Unknown";
#endif
    Cleanup( ".bas" );
    Cleanup( ".pub" );
    Cleanup( ".prt" );
    Cleanup( ".prv" );
    Cleanup( ".lnk" );
    Cleanup( ".ind" );
    Cleanup( ".inh" );
    Cleanup( ".flt" );
    Cleanup( ".cls" );
    Cleanup( ".fnc" );
    Cleanup( ".lbs" );
    Cleanup( ".lgo" );
    Cleanup( ".dta" );
    Cleanup( ".sum" );
    Cleanup( ".sig" );
    Cleanup( ".ini" );
    Cleanup( ".cal" );
    Cleanup( ".cmb" );
    Cleanup( ".mnu" );
    Cleanup( ".tmp" );
    THROW( str );
}

} // Generate


/*------------------------------------------------------------------------
------------------------------------------------------------------------*/
void	DocSetConfig::Cleanup( char* extension )
{
    LibraryIter	    libiter( *_liblist, _webroot );

    if ( savepartials )
	return;
    if ( strcmp( extension, ".xrf" ) == 0  &&  ! savexref )
	OS().RemoveAll( _webroot, ".xrf" );

    libiter.First();
    while ( ! libiter.isDone() )
    {
	OS().RemoveAll( libiter.WebDir(), extension );
	libiter.Next();
    }
    OS().RemoveAll( _webroot, extension );
} // Cleanup


/*------------------------------------------------------------------------
------------------------------------------------------------------------*/
void DocSetConfig::PrintLibData()
{
    LibraryData*    lib = _liblist;

    while ( lib )
    {
	cout << "-------------- " << lib->Name()
	     << " Customization File --------------" << endl;
	_defaultcust.Output( cout );
	cout << "#<end defaults>" << endl;
	lib->Overrides().Output( cout );
	lib = lib->Next();
    }
} // PrintLibData


/*------------------------------------------------------------------------
------------------------------------------------------------------------*/
void DocSetConfig::LoadConfigFile()
{
    StringBuff	    inbuff;	// input line buffer
    ifstream	    infile;	// input file
    CustFile*	    custfile;   // current customization file
    int		    linenumber; // line number in file
    LibraryData*    lastlib = NULL; // last library processed

				// initialize
    custfile   = NULL;
    linenumber = 0;

    _urlroot.Clear();
    _urlroot.Append("WEBROOT");
    //
    //  Open up the documentation description file.
    //
    infile.open( _filename, ios::in );
    if ( ! infile )
    {
	cout << "Couldn't open input file " << _filename
	     << " for reading." << endl;
	return;
    }


    //
    //  Go through the file, line by line.
    //
    linenumber = 0;
    while ( inbuff.GetLine( infile, TRUE ) )
    {
	linenumber++;
	inbuff.TrimWhite();

	//
	//  Look for keywords...
	//

	if ( strncmp( inbuff, "end_customize", 13 ) == 0 )
	    custfile = NULL;

	else if ( custfile )
	{
	    if ( strcmp( inbuff, "useperl" ) == 0 )
		_useperl = TRUE;
	    else if ( strcmp( inbuff, "useframes" ) == 0 )
		_useframes = TRUE;
	    if ( strcmp( inbuff, "accessimplementation" ) == 0 )
		_accessImp = TRUE;
	    if ( _liblist )
	    {
		if ( OnceOnly( inbuff ) )
		    cout << "Customization option on line " << linenumber
			 << " may only be given in the default" << endl
			 << "\tcustomization section." << endl;
		else
		    custfile->AddLine( inbuff );
	    }
	    else
	    {
		custfile->AddLine( inbuff );
		if ( strncmp( &inbuff[ inbuff.NonWhite() ],
			      "extension", 9 ) == 0 )
		{
		    _ext.Clear();
		    _ext.Append( GetSecondWord( inbuff ) );
		}
	    }
	}

	else if ( strncmp( inbuff, "webroot", 7 ) == 0 )
	{
	    int lastchar;

	    if ( strlen( _webroot ) > 0 )
		cout << "Multiple webroots, line " << linenumber << "." << endl;
	    _webroot.Append( GetSecondWord( inbuff ) );
				// make sure there is no slash at the end
	    lastchar = strlen( _webroot ) - 1;
	    if ( _webroot[lastchar] == '/'  ||  _webroot[lastchar] == '\\' )
		_webroot[lastchar] = '\0';
	}

	else if ( strncmp( inbuff, "urlroot", 7 ) == 0 )
	{
	    cout << "The 'urlroot' directive is no longer used.\n";
	}

	else if ( strncmp( inbuff, "library", 7 ) == 0 )
	{
	    int		i;
	    char*	name	  = NULL;
	    char*	srcdir	  = NULL;
	    char*	websubdir = NULL;
	    LibraryData*    newlib    = NULL;

					// find pointers to the required
					// strings
	    name = GetSecondWord( inbuff );
	    if ( ! name  ||  strlen(name) == 0 )
	    {
		cout << "Missing library name, line " << linenumber
		     << "." << endl;
		name = NULL;
	    }
	    srcdir = GetSecondWord( name );
	    if ( ! srcdir  ||  strlen(srcdir) == 0 )
	    {
		cout << "Missing library source directory, line " << linenumber
		     << "." << endl;
		srcdir = NULL;
	    }
	    websubdir = GetSecondWord( srcdir );
	    if ( ! websubdir  ||  strlen(websubdir) == 0 )
	    {
		cout << "Missing library Web sub-directory, line " << linenumber
		     << "." << endl;
		websubdir = NULL;
	    }
	    if ( srcdir  &&  srcdir[0] != '/'  &&  srcdir[1] != ':' )
	    {
		cout << "Source directory not given a full path, line "
		     << linenumber << "." << endl;
		srcdir = NULL;
	    }
	    if ( websubdir  &&  websubdir[0] == '/' )
	    {
		cout << "Sub-directory must be a relative path, line "
		     << linenumber << "." << endl;
		websubdir = NULL;
	    }

	    if ( name  &&  srcdir  &&  websubdir )
	    {
					// terminate each string
		i = 0;
		while ( name[i]  &&  name[i] != ' ' ) i++;
		name[i] = '\0';
		i = 0;
		while ( srcdir[i]  &&  srcdir[i] != ' ' ) i++;
		srcdir[i] = '\0';
		i = 0;
		while ( websubdir[i]  &&  websubdir[i] != ' ' ) i++;
		websubdir[i] = '\0';

					// create a new library
		newlib = new LibraryData( name, srcdir, websubdir );
		if ( ! newlib )
		    THROW( "Memory allocation error!" );
		if ( ! _liblist )
		    _liblist = newlib;
		else
		    lastlib->SetNext( *newlib );
		lastlib = newlib;
	    }
	}


	else if ( strncmp( inbuff, "otherdata", 9 ) == 0 )
	{
	    char*	ext	  = NULL;
	    char*	srcdir	  = NULL;
	    char*	websubdir = NULL;
	    char*	typekey   = NULL;
	    int		isbinary  = FALSE;

					// find pointers to the required
					// strings
	    ext = GetSecondWord( inbuff );
	    if ( ! ext )
		cout << "Missing file extension, line " << linenumber
		     << "." << endl;
	    srcdir = GetSecondWord( ext );
	    if ( ! srcdir )
		cout << "Missing source directory, line " << linenumber
		     << "." << endl;
	    websubdir = GetSecondWord( srcdir );
	    if ( ! websubdir )
		cout << "Missing Web sub-directory, line " << linenumber
		     << "." << endl;
	    typekey = GetSecondWord( websubdir );
	    if ( typekey )
	    {
		if ( strncmp( typekey, "binary", 6 ) == 0 )
		    isbinary = TRUE;
	    }

	    if ( srcdir[0] != '/'  &&  strncmp(":/", &srcdir[1], 2) != 0 )
	    {
		cout << "Source directory not given a full path, line "
		     << linenumber << "." << endl;
		srcdir = NULL;
	    }
	    if ( websubdir[0] == '/' )
	    {
		cout << "Sub-directory must be a relative path, line "
		     << linenumber << "." << endl;
		websubdir = NULL;
	    }

	    if ( ext  &&  srcdir  &&  websubdir )
	    {
		int	    i;
		OtherData*  newobj = NULL;

					// terminate each string
		i = 0;
		while ( ext[i]  &&  ext[i] != ' ' ) i++;
		ext[i] = '\0';
		i = 0;
		while ( srcdir[i]  &&  srcdir[i] != ' ' ) i++;
		srcdir[i] = '\0';
		i = 0;
		while ( websubdir[i]  &&  websubdir[i] != ' ' ) i++;
		websubdir[i] = '\0';
		if ( websubdir[0] == '.' )
		    websubdir = NULL;   // in webroot itself

					// create a new otherdata object
		newobj = new OtherData( ext, srcdir, websubdir, isbinary );
		if ( ! newobj )
		    THROW( "Memory allocation error!" );
		if ( _otherlist )
		    newobj->SetNext( *_otherlist );
		_otherlist = newobj;
	    }
	}

	else if ( strncmp( inbuff, "link", 4 ) == 0 )
	{
	    StringList*	    newlink;

	    newlink = new StringList( GetSecondWord( inbuff ) );
	    if ( ! newlink )
		THROW( "Memory allocation error!" );
	    if ( _hyperlinks )
		newlink->SetNext( *_hyperlinks );
	    _hyperlinks = newlink;
	}

	else if ( strncmp( inbuff, "customize", 9 ) == 0 )
	{
	    if ( _liblist )
		custfile = &(lastlib->Overrides());
	    else
		custfile = &_defaultcust;
	}

	else if ( strlen( inbuff ) > 0 )
	    cout << "Unrecognized word, line " << linenumber << "." << endl;

    } // while

    infile.close();

} // LoadConfigFile


/*------------------------------------------------------------------------
------------------------------------------------------------------------*/
int DocSetConfig::OnceOnly( StringBuff& line )
{
    char*   word;

    word = &line[ line.NonWhite() ];
    if ( strncmp( word, "usetable", 8 ) == 0 )
	return TRUE;
    else if ( strncmp( word, "useperl", 7 ) == 0 )
	return TRUE;
    else if ( strncmp( word, "namelength", 10 ) == 0 )
	return TRUE;
    else if ( strncmp( word, "server", 6 ) == 0 )
	return TRUE;
    else if ( strncmp( word, "extension", 9 ) == 0 )
	return TRUE;
    else if ( strncmp( word, "keyword", 7 ) == 0 )
	return TRUE;
    else if ( strncmp( word, "accessimplementation", 20 ) == 0 )
	return TRUE;
    else if ( strncmp( word, "showprivates", 12 ) == 0 )
	return TRUE;
    else if ( strncmp( word, "useframes", 9 ) == 0 )
	return TRUE;
    return FALSE;
} // OnceOnly




/*------------------------------------------------------------------------
--------------------------------------------------------------------------
				    ROUTINES
--------------------------------------------------------------------------
------------------------------------------------------------------------*/

/*------------------------------------------------------------------------
    Prints a usage error message.
------------------------------------------------------------------------*/
static void
UsageError()
{
cout << "Usage  : cocoon  [options]  configuration_file" << endl;
cout << "Version: " << VERSION << endl;
cout << "Options:" << endl;
cout << "   -l libname     don't use a configuration file, just do all" <<endl;
cout << "                  the files on the current directory--" << endl;
cout << "                  must provide the name of the library but" << endl;
cout << "                  can skip the configuration_file--if given," << endl;
cout << "                  this must be given last on the command line.\n";
cout << "   -d             debug mode" << endl;
cout << "   -s             save partial files" << endl;
cout << "   -x             save cross-reference data files" << endl;
cout << "   -p pathname    use the given path name to find the other" << endl;
cout << "                  Cocoon executables - be sure to include" << endl;
cout << "                  final separator!" << endl;
cout << "   -h             print this listing" << endl;
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
    int	    i;		// looping index
    int	    localonly = FALSE;  // flag for -l command line option

    cocoonDir.Append(OS().getVariable("COCOON"));
    if ( argc < 2 )
	UsageError();

    if ( strcmp(argv[argc-1], "-h") == 0 )
	UsageError();
    for ( i = 1; i < argc-1; i++ )
    {
	if ( strcmp( argv[i], "-d" ) == 0 )
	    debug = TRUE;
	else if ( strcmp( argv[i], "-s" ) == 0 )
	    savepartials = TRUE;
	else if ( strcmp( argv[i], "-x" ) == 0 )
	    savexref = TRUE;
	else if ( strcmp( argv[i], "-l" ) == 0 )
	    localonly = TRUE;
	else if ( strcmp( argv[i], "-h" ) == 0 )
	    UsageError();
	else if ( strcmp( argv[i], "-p" ) == 0 )
	{
	    if ( i >= argc-2 )
		UsageError();
	    if ( argv[i+1][strlen( argv[i+1] ) - 1] != '/'  &&
		 argv[i+1][strlen( argv[i+1] ) - 1] != '\\' )
		UsageError();
	    execpath = argv[i+1];
	    cocoonDir.Append(execpath);
	    i++;
	}
	else
	    UsageError();
    }
    if ( argc == 2  &&  argv[1][0] == '-' )
    {
	UsageError();
    }

    cout << endl << VERSION << endl;

    if ( ! (const char*)cocoonDir  ||  strlen(cocoonDir) == 0 )
    {
	cout << "\nError: The environment variable COCOON must be set to the\n";
	cout << "directory where Cocoon was installed.\n\n";
	cout.flush();
	return -1;
    }
    if ( cocoonDir[(int)strlen(cocoonDir)-1] != '/' &&
	 cocoonDir[(int)strlen(cocoonDir)-1] != '\\' )
    {
	cocoonDir.Append(OS().PathDivider());
    }

    //
    //  Load up the documentation configuration.
    //
    DocSetConfig    config( argv[argc-1], localonly );

    //
    //  Generate the documentation set.
    //
    config.Generate();

}
CATCH( char* str )
{
    cout << "EXCEPTION: " << str << endl << endl;
    return -1;
}

return 0;

} // main

