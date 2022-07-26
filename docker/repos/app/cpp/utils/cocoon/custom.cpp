/*------------------------------------------------------------------------
				  COPYRIGHT
--------------------------------------------------------------------------
	  Copyright (C) 1995-2000
	  Jeff Kotula
	  All Rights Reserved.
--------------------------------------------------------------------------
				   FILE LOG
--------------------------------------------------------------------------

    Source code for the customization facilities of the cocoon
    utilities.

------------------------------------------------------------------------*/

#include    "cobweb.h"

typedef struct
{
    char*   color;
    char*   rgb;
} ColorMapCell;

			// The following colormap was found at
			// http://www.infi.net/wwwimages/colorindex.html
static ColorMapCell colormap[] = {
    { "WHITE", "FFFFFF" }, { "RED", "FF0000" }, { "GREEN", "00FF00" },
    { "BLUE", "0000FF" }, { "MAGENTA", "FF00FF" }, { "YELLOW", "FFFF00" },
    { "BLACK", "000000" }, { "AQUAMARINE", "70DB93" },
    { "BAKER'S CHOCOLATE", "5C3317" }, { "BLUE VIOLET", "9F5F9F" },
    { "BRASS", "B5A642" }, { "BRIGHT GOLD", "D9D919" }, { "BROWN", "A62A2A" },
    { "BRONZE", "8C7853" }, { "BRONZE II", "A67D3D" },
    { "CADET BLUE", "5F9F9F" }, { "COOL COPPER", "D98719" },
    { "COPPER", "B87333" }, { "CORAL", "FF7F00" },
    { "CORN FLOWER BLUE", "42426F" }, { "DARK BROWN", "5C4033" },
    { "DARK GREEN", "2F4F2F" }, { "DARK GREEN COPPER", "4A766E" },
    { "DARK OLIVE GREEN", "4F4F2F" }, { "DARK ORCHID", "9932CD" },
    { "DARK PURPLE", "871F78" }, { "DARK SLATE BLUE", "6B238E" },
    { "DARK SLATE GREY", "2F4F4F" }, { "DARK TAN", "97694F" },
    { "DARK TURQUOISE", "7093DB" }, { "DARK WOOD", "855E42" },
    { "DIM GREY", "545454" }, { "DUSTY ROSE", "856363" },
    { "FELDSPAR", "D19275" }, { "FIREBRICK", "8E2323" },
    { "FOREST GREEN", "238E23" }, { "GOLD", "CD7F32" },
    { "GOLDENROD", "DBDB70" }, { "GREY", "C0C0C0" },
    { "GREEN COPPER", "527F76" }, { "GREEN YELLOW", "93DB70" },
    { "HUNTER GREEN", "215E21" },
    { "KHAKI", "9F9F5F" }, { "LIGHT BLUE", "C0D9D9" },
    { "LIGHT GREY", "A8A8A8" }, { "LIGHT STEEL BLUE", "8F8FBD" },
    { "LIGHT WOOD", "E9C2A6" }, { "LIME GREEN", "32CD32" },
    { "MANDARIN ORANGE", "E47833" }, { "MAROON", "8E236B" },
    { "MEDIUM AQUAMARINE", "32CD99" }, { "MEDIUM BLUE", "3232CD" },
    { "MEDIUM FOREST GREEN", "6B8E23" }, { "MEDIUM GOLDENROD", "EAEAAE" },
    { "MEDIUM ORCHID", "9370DB" }, { "MEDIUM SEA GREEN", "426F42" },
    { "MEDIUM SLATE BLUE", "7F00FF" }, { "MEDIUM SPRING GREEN", "7FFF00" },
    { "MEDIUM TURQUOISE", "70DBDB" }, { "MEDIUM VIOLET RED", "DB7093" },
    { "MEDIUM WOOD", "A68064" }, { "MIDNIGHT BLUE", "2F2F4F" },
    { "NAVY BLUE", "23238E" }, { "NEON BLUE", "4D4DFF" },
    { "NEON PINK", "FF6EC7" }, { "NEW MIDNIGHT BLUE", "00009C" },
    { "NEW TAN", "EBC79E" }, { "OLD GOLD", "CFB53B" },
    { "ORANGE", "FF7F00" }, { "ORANGE RED", "FF2400" }, { "ORCHID", "DB70DB" },
    { "PALE GREEN", "8FBC8F" }, { "PINK", "BC8F8F" }, { "PLUM", "EAADEA" },
    { "QUARTZ", "D9D9F3" }, { "RICH BLUE", "5959AB" }, { "SALMON", "6F4242" },
    { "SCARLET", "8C1717" }, { "SEA GREEN", "238E68" },
    { "SEMI-SWEET CHOCOLATE", "6B4226" }, { "SIENNA", "8E6B23" },
    { "SILVER", "E6E8FA" }, { "SKY BLUE", "3299CC" },
    { "SLATE BLUE", "007FFF" }, { "SPICY PINK", "FF1CAE" },
    { "SPRING GREEN", "00FF7F" }, { "STEEL BLUE", "236B8E" },
    { "SUMMER SKY", "38B0DE" }, { "TAN", "DB9370" }, { "THISTLE", "D8BFD8" },
    { "TURQUOISE", "ADEAEA" }, { "VERY DARK BROWN", "5C4033" },
    { "VERY LIGHT GREY", "CDCDCD" }, { "VIOLET", "4F2F4F" },
    { "VIOLET RED", "CC3299" }, { "WHEAT", "D8D8BF" },
    { "YELLOW GREEN", "99CC32" }
};


/*------------------------------------------------------------------------

   Constructor for the object holding all the custom data.

------------------------------------------------------------------------*/
Customize::Customize( int preformatted, char* customfile, char* headerpath,
		      char* webpath, char* extension ) :
    _preformatted( preformatted ), _usetable( FALSE ), _useperl( FALSE ),
    _bulletmap( NULL ), _maxnamelen( 0 ), _baseheading( 1 ),
    _imageonly( FALSE ), _numkeycolumns( 5 ), _docwithsentinel( FALSE ),
    _ignorelist( NULL ), _forunimax( FALSE ), _nosentinels( FALSE ),
    _skipfulldecl( FALSE ), _autokeywords( FALSE ), _eightbitclean( FALSE ),
    _accessImp( FALSE ), _showPrivates( FALSE ), _ignoreDirectiveList( NULL ),
    _useframes(FALSE), _forstratasys(FALSE)
{

				// initialize basic paths and http stuff
    _server.Append("http://www.stratasys.com/software/");
    if ( headerpath )
    {
	_filepath.Append( headerpath );
	if ( headerpath[strlen( headerpath ) - 1] != '/' )
	    _filepath.Append("/");
    }
    if ( webpath )
    {
	int i;

	_webpath.Append( webpath );
	for ( i = 0; _webpath[i]; i++ )
	    if ( _webpath[i] == '\\' )
		_webpath[i] = '/';
	if ( webpath[i-1] != '/' )
	    _webpath.Append("/");
    }
    if ( extension )
    {
	if ( extension[0] != '.' )
	    _extension.Append( "." );
	_extension.Append( extension );
    }
    else
	_extension.Append( ".html" );


				// initialize the defaults for images, etc.
    _logo.Append("<img src=WEBROOT/coclogo.gif>");

				// include an empty paragraph in the rule--
				// some browsers may not be able to handle
				// <hr> in an anchor all alone
    _rule.Append( "<hr><p></p>" );

    _backtotop.Append("<p>Back to the <a href=\"#topofdoc\">top</a> of  ");

    _membersentinel.Append( "//////////" );
    _memsentinellength = strlen( _membersentinel );
    _classsentinel.Append( "CLASS" );
    _classsentinellength = strlen( _classsentinel );
    _librarysentinel.Append( "LIBRARY" );
    _librarysentinellength = strlen( _librarysentinel );
    _keywordsentinel.Append( "KEYWORDS" );
    _keywordsentinellength = strlen( _keywordsentinel );
    _examplesentinel.Append( "EXAMPLE" );
    _examplesentinellength = strlen( _examplesentinel );
    _endsentinel.Append( "END" );
    _endsentinellength = strlen( _endsentinel );
    _classString.Append( "class" );
    _classStringLength = strlen( _classString );
    _ignorelist = new StringList( "LOG" );
    if ( ! _ignorelist )
	THROW( "Memory allocation error." );

				// load up the user customizations
    GetCustomizations( customfile );

} // Customize constructor


/*------------------------------------------------------------------------

    Maps the given color specification to the actual string to use
    in the HTML file.

------------------------------------------------------------------------*/
static char*
MapColor( char* color )
{
    int	    i;
    int	    size;
    StringBuff  buff;

    size = sizeof( colormap ) / sizeof( ColorMapCell );
    assert( size > 1 );

    buff.Append( color );
    buff.TrimWhite();
    buff.ToUpper();

    for ( i = 0; i < size; i++ )
	if ( strcmp( colormap[i].color, buff ) == 0 )
	    return( colormap[i].rgb );
    return color;
}


/*------------------------------------------------------------------------

    Processes the user customization file.

    These are the lines recognized in the customization file:

	preformatted
	usetable
	useframes
	numkeycolumns   number
	baseheading	number
	namelength	maxlength
	useperl

	membersentinel	    sentinelstring
	docwithsentinel
	nosentinels
	autokeywords
	classsentinel	    sentinelstring
	librarysentinel	    sentinelstring
	keywordsentinel	    sentinelstring
	examplesentinel	    sentinelstring
	endsentinel	    sentinelstring
	classstring	    string
	ignoreheading	    headingstring
	ignoredirective	    directivestring

	server		serverstring
	extension	file_extension
	eightbitclean
	accessimplementation
	showprivates

	background    imagename
	backcolor     color
	textcolor     color
	linkcolor     color
	visitedcolor  color
	activecolor   color

	rule	      rulestring
	aag	      imagename
	quickref      imagename
	keyword	      imagename
	example	      imagename
	bullet	      imagename
	backtotop     imagename
	logo	      imagename
	mapbullet     keyword   imagemap
	imageonly
	nobacktotop

	attop	    htmlfile
	atbottom    htmlfile

	skipfulldeclaration

	forunimax
	forstratasys


    Comments start with '#' and proceed to the end of the line.

    Any 'color' argument can be given via the ugly numbers used
    by Netscape, in which case it is used verbatim.  Otherwise, one of
    the common colors specified in the colormap at the top of this file
    can be given.

------------------------------------------------------------------------*/
void Customize::GetCustomizations( char* customfile )
{
    int	    givenname  = TRUE;
    int	    linenumber = 0;
    char*   line;
    ifstream	custfile;
    StringBuff  inbuff;

    if ( ! customfile )
    {
	givenname = FALSE;
	customfile = "cocoon.ini";
    }

    custfile.open( customfile, ios::in );
    if ( ! custfile  &&  givenname )
    {
	cout << "Can't open " << customfile << " for reading." << endl;
	THROW("File handling error.");
    }
    if ( ! custfile )
	return;


    while ( inbuff.GetLine( custfile, TRUE ) )
    {
	linenumber++;
	line = &inbuff[inbuff.NonWhite()];
	if ( strlen( line ) == 0 )
	    continue;


	if ( strcmp( line, "preformatted" ) == 0 )
	    _preformatted = TRUE;

	else if ( strcmp( line, "imageonly" ) == 0 )
	    _imageonly = TRUE;

	else if ( strcmp( line, "docwithsentinel" ) == 0 )
	    _docwithsentinel = TRUE;

	else if ( strcmp( line, "accessimplementation" ) == 0 )
	    _accessImp = TRUE;

	else if ( strcmp( line, "showprivates" ) == 0 )
	    _showPrivates = TRUE;

	else if ( strcmp( line, "nosentinels" ) == 0 )
	    _nosentinels = TRUE;

	else if ( strcmp( line, "autokeywords" ) == 0 )
	    _autokeywords = TRUE;

	else if ( strcmp( line, "forunimax" ) == 0 )
	{
	    _forunimax = TRUE;
	    if ( ! _ignoreDirectiveList )
	    {
		_ignoreDirectiveList = new StringList( "UM_DLL_INTERFACE" );
		if ( ! _ignoreDirectiveList )
		    THROW( "Memory allocation error." );
	    }
	    else
		_ignoreDirectiveList = _ignoreDirectiveList->AddInOrder(
							"UM_DLL_INTERFACE" );
	}

	else if ( strcmp( line, "forstratasys" ) == 0 )
	    _forstratasys = TRUE;

	else if ( strcmp( line, "eightbitclean" ) == 0 )
	    _eightbitclean = TRUE;

	else if ( strncmp( line, "nowebserver", 11 ) == 0 )
	{
	    cout << "The 'nowebserver' directive is no longer needed.\n";
	}

	else if ( strcmp( line, "skipfulldeclaration" ) == 0 )
	    _skipfulldecl = TRUE;

	else if ( strcmp( line, "usetable" ) == 0 )
	    _usetable = TRUE;

	else if ( strcmp( line, "useframes" ) == 0 )
	    _useframes = TRUE;

	else if ( strcmp( line, "useperl" ) == 0 )
	    _useperl = TRUE;

	else if ( strncmp( line, "membersentinel", 14 ) == 0 )
	{
	    _membersentinel.Clear();
	    _membersentinel.Append( GetSecondWord( line ) );
	    _memsentinellength = strlen( _membersentinel );
	    if ( strncmp( _membersentinel, "//", 2 ) != 0  &&
		 strncmp( _membersentinel, "/*", 2 ) != 0 )
		THROW( "Member sentinel must begin with \"//\" or \"/*\"." );
	}

	else if ( strncmp( line, "classsentinel", 13 ) == 0 )
	{
	    _classsentinel.Clear();
	    _classsentinel.Append( GetSecondWord( line ) );
	    _classsentinel.TrimWhite();
	    _classsentinellength = strlen( _classsentinel );
	}

	else if ( strncmp( line, "librarysentinel", 15 ) == 0 )
	{
	    _librarysentinel.Clear();
	    _librarysentinel.Append( GetSecondWord( line ) );
	    _librarysentinel.TrimWhite();
	    _librarysentinellength = strlen( _librarysentinel );
	}

	else if ( strncmp( line, "keywordsentinel", 15 ) == 0 )
	{
	    _keywordsentinel.Clear();
	    _keywordsentinel.Append( GetSecondWord( line ) );
	    _keywordsentinel.TrimWhite();
	    _keywordsentinellength = strlen( _keywordsentinel );
	}

	else if ( strncmp( line, "examplesentinel", 15 ) == 0 )
	{
	    _examplesentinel.Clear();
	    _examplesentinel.Append( GetSecondWord( line ) );
	    _examplesentinel.TrimWhite();
	    _examplesentinellength = strlen( _examplesentinel );
	}

	else if ( strncmp( line, "endsentinel", 11 ) == 0 )
	{
	    _endsentinel.Clear();
	    _endsentinel.Append( GetSecondWord( line ) );
	    _endsentinel.TrimWhite();
	    _endsentinellength = strlen( _endsentinel );
	}

	else if ( strncmp( line, "classstring", 11 ) == 0 )
	{
	    _classString.Clear();
	    _classString.Append( GetSecondWord( line ) );
	    _classString.TrimWhite();
	    _classStringLength = strlen( _classString );
	}

	else if ( strncmp( line, "ignoreheading", 13 ) == 0 )
	{
	    StringBuff  tmp;

	    tmp.Append( GetSecondWord( line ) );
	    tmp.TrimWhite();
	    tmp.ToUpper();
	    _ignorelist = _ignorelist->AddInOrder( tmp );
	}

	else if ( strncmp( line, "ignoredirective", 15 ) == 0 )
	{
	    StringBuff  tmp;

	    tmp.Append( GetSecondWord( line ) );
	    tmp.TrimWhite();
	    if ( ! _ignoreDirectiveList )
	    {
		_ignoreDirectiveList = new StringList( tmp );
		if ( ! _ignoreDirectiveList )
		    THROW( "Memory allocation error." );
	    }
	    else
		_ignoreDirectiveList = _ignoreDirectiveList->AddInOrder( tmp );
	}

	else if ( strncmp( line, "server", 6 ) == 0 )
	{
	    sscanf( line, " server %s ", (char *)_server );
	    _logo.Clear();
	    _logo.Append( "<img src=\"" );
	    _logo.Append( _server );
	    _logo.Append( "cocoon/coclogo.gif\">" );
	}

	else if ( strncmp( line, "rule", 4 ) == 0 )
	{
	    _rule.Clear();
	    _rule.Append( GetSecondWord( line ) );
	    _rule.Append( " <p>" );
	}

	else if ( strncmp( line, "baseheading", 11 ) == 0 )
	{
	    sscanf( GetSecondWord( line ), "%d", &_baseheading );
	    if ( _baseheading < 1  ||  _baseheading > 4 )
	    {
		_baseheading = 1;
		cout << "Illegal base heading number at line " << linenumber
		     << " of customization" << " file " << customfile
		     << ".  Ignoring." << endl;
	    }
	}


	else if ( strncmp( line, "numkeycolumns", 13 ) == 0 )
	{
	    sscanf( GetSecondWord( line ), "%d", &_numkeycolumns );
	    if ( _numkeycolumns < 1 )
	    {
		_numkeycolumns = 5;
		cout << "Illegal number of columns at line " << linenumber
		     << " of customization" << " file " << customfile
		     << ".  Ignoring." << endl;
	    }
	}

	else if ( strncmp( line, "namelength", 10 ) == 0 )
	{
	    sscanf( GetSecondWord( line ), "%d", &_maxnamelen );
	    if ( _maxnamelen < 0 )
		_maxnamelen = 0;
	}

	else if ( strncmp( line, "background", 10 ) == 0 )
	{
	    _background.Clear();
	    _background.Append( " BACKGROUND = \"" );
	    _background.Append( GetSecondWord( line ) );
	    _background.Append( "\"" );
	}

	else if ( strncmp( line, "backcolor", 9 ) == 0 )
	{
	    _backcolor.Clear();
	    _backcolor.Append( " BGCOLOR=#" );
	    _backcolor.Append( MapColor( GetSecondWord( line ) ) );
	    _backcolor.Append( " " );
	}

	else if ( strncmp( line, "textcolor", 9 ) == 0 )
	{
	    _textcolor.Clear();
	    _textcolor.Append( " TEXT=#" );
	    _textcolor.Append( MapColor( GetSecondWord( line ) ) );
	    _textcolor.Append( " " );
	}

	else if ( strncmp( line, "linkcolor", 9 ) == 0 )
	{
	    _linkcolor.Clear();
	    _linkcolor.Append( " LINK=#" );
	    _linkcolor.Append( MapColor( GetSecondWord( line ) ) );
	    _linkcolor.Append( " " );
	}

	else if ( strncmp( line, "visitedcolor", 12 ) == 0 )
	{
	    _visitedcolor.Clear();
	    _visitedcolor.Append( " VLINK=#" );
	    _visitedcolor.Append( MapColor( GetSecondWord( line ) ) );
	    _visitedcolor.Append( " " );
	}

	else if ( strncmp( line, "activecolor", 11 ) == 0 )
	{
	    _activecolor.Clear();
	    _activecolor.Append( " ALINK=#" );
	    _activecolor.Append( MapColor( GetSecondWord( line ) ) );
	    _activecolor.Append( " " );
	}


	else if ( strncmp( line, "attop", 5 ) == 0 )
	{
	    _attopfile.Clear();
	    _attopfile.Append( GetSecondWord( line ) );
	}

	else if ( strncmp( line, "atbottom", 5 ) == 0 )
	{
	    _atbottomfile.Clear();
	    _atbottomfile.Append( GetSecondWord( line ) );
	}

	else if ( strncmp( line, "aag", 3 ) == 0 )
	{
	    _aagimage.Clear();
	    _aagimage.Append( "<img src=\"" );
	    _aagimage.Append( GetSecondWord( line ) );
	    _aagimage.Append( "\">" );
	}

	else if ( strncmp( line, "quickref", 8 ) == 0 )
	{
	    _quickrefimage.Clear();
	    _quickrefimage.Append( "<img src=\"" );
	    _quickrefimage.Append( GetSecondWord( line ) );
	    _quickrefimage.Append( "\">" );
	}

	else if ( strncmp( line, "keyword", 7 ) == 0 )
	{
	    _keywordimage.Clear();
	    _keywordimage.Append( "<img src=\"" );
	    _keywordimage.Append( GetSecondWord( line ) );
	    _keywordimage.Append( "\">" );
	}

	else if ( strncmp( line, "example", 7 ) == 0 )
	{
	    _exampleimage.Clear();
	    _exampleimage.Append( "<img src=\"" );
	    _exampleimage.Append( GetSecondWord( line ) );
	    _exampleimage.Append( "\">" );
	}

	else if ( strncmp( line, "bullet", 6 ) == 0 )
	{
	    _bullet.Clear();
	    _bullet.Append( "<img src=\"" );
	    _bullet.Append( GetSecondWord( line ) );
	    _bullet.Append( "\">" );
	}

	else if ( strncmp( line, "backtotop", 9 ) == 0 )
	{
	    _backtotop.Clear();
	    _backtotop.Append( "<p><a href=\"#topofdoc\">" );
	    _backtotop.Append( "<img src=\"" );
	    _backtotop.Append( GetSecondWord( line ) );
	    _backtotop.Append( "\" border=0 align=middle></a>Back to the top of  " );
	}

	else if ( strncmp( line, "nobacktotop", 11 ) == 0 )
	    _backtotop.Clear();

	else if ( strncmp( line, "logo", 4 ) == 0 )
	{
	    _logo.Clear();
	    _logo.Append( "<img src=\"" );
	    _logo.Append( GetSecondWord( line ) );
	    _logo.Append( "\">" );
	}

	else if ( strncmp( line, "extension", 9 ) == 0 )
	{
	    char*   tmpptr;

	    tmpptr = GetSecondWord( line );
	    if ( tmpptr )
	    {
		_extension.Clear();
		if ( tmpptr[0] != '.' )
		    _extension.Append( "." );
		_extension.Append( tmpptr );
	    }
	}

	else if ( strncmp( line, "mapbullet", 9 ) == 0 )
	{
	    BulletMap*  newmap = new BulletMap;
	    char*   secondword;
	    int	    i;
	    int	    foundbreak = FALSE;

	    if ( ! newmap )
		THROW( "Memory allocation error!" );
	    newmap->next = NULL;
	    secondword = GetSecondWord( line );
	    newmap->keyword.Append( secondword );
	    for ( i = 0; newmap->keyword[i]; i++ )
		if ( isspace( newmap->keyword[i] ) )
		{
		    newmap->keyword[i] = '\0';
		    foundbreak = TRUE;
		    break;
		}
	    if ( foundbreak )
	    {
		newmap->image.Append( "<img src=\"" );
		newmap->image.Append( GetSecondWord( secondword ) );
		newmap->image.Append( "\">" );
		newmap->keyword.TrimWhite();
		newmap->image.TrimWhite();
		newmap->next = _bulletmap;
		_bulletmap   = newmap;
	    }
	    else
		cout << "Error in line " << linenumber << " of customization"
		     << " file " << customfile << ".  Ignoring." << endl;
	}

	else
	    cout << "Error in line " << linenumber << " of customization"
		 << " file " << customfile << ".  Ignoring." << endl << "\t"
		 << line << endl;;
    } // while

    _bodyoptions.Append( _background );
    _bodyoptions.Append( _backcolor );
    _bodyoptions.Append( _textcolor );
    _bodyoptions.Append( _linkcolor );
    _bodyoptions.Append( _visitedcolor );
    _bodyoptions.Append( _activecolor );

    custfile.close();

} // GetCustomizations


void	Customize::DumpFile( ofstream& outfile, char* name, char* notfoundmsg)
{
    if ( strlen( name ) < 1 )
	return;

    StringBuff  buffer;
    ifstream	infile( name, ios::in );

    if ( ! infile )
	THROW( notfoundmsg );

    while ( buffer.GetLine( infile ) )
	outfile << buffer << endl;

} // DumpFile


void	Customize::HTMLAtBottom( ofstream& outfile )
{
    if ( strlen( _atbottomfile ) < 1 )
	return;
    outfile << Rule() << endl;
    DumpFile( outfile, _atbottomfile, "Specified at-bottom file not found.");
} // HTMLAtBottom


char*   Customize::BulletByName( char* keyword )
{
    BulletMap*  map;

    map = _bulletmap;
    while ( map )
    {
	if ( strcmp( keyword, map->keyword ) == 0 )
	    return map->image;
	map = map->next;
    }
    return NULL;
} // BulletByName


char*   Customize::BackToTop()
{
    if ( _backtotop[0] )
	return _backtotop;
    return NULL;
} // BackToTop


int	Customize::IgnoreDirective(char* str)
{
    StringList*	    list = _ignoreDirectiveList;

    if ( ! list )
	return FALSE;

    while ( list )
    {
	if ( strcmp(str, list->String()) == 0 )
	    return TRUE;
	list = list->Next();
    }

    return FALSE;

} // IgnoreDirective

