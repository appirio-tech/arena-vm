
/*------------------------------------------------------------------------
				  COPYRIGHT
--------------------------------------------------------------------------
	  Copyright (C) 1995-2000
	  Jeff Kotula
	  All Rights Reserved.
--------------------------------------------------------------------------
				   FILE LOG
--------------------------------------------------------------------------

    Utilities for the cocoon programs.

------------------------------------------------------------------------*/

#include    "cobweb.h"
#include    "sys.h"


/*------------------------------------------------------------------------
    Outputs a signature string for each html file produced.
------------------------------------------------------------------------*/
void	Signature( ofstream& str, Customize& custom )
{
    StringBuff  date;

    OS().DateAndTime( date );
    custom.HTMLAtBottom( str );
    str << custom.Rule() << endl
	<< "Generated from source by the <i><a href=\"" << custom.Server()
	<< "cocoon/index.htm\">Cocoon</a></i> utilities on " << date
	<< ".<p>" << endl <<
	"<address>Report <a href=\"mailto:jkotula@vitalimages.com"
	<< "\">problems</a> to jkotula@vitalimages.com</address>" << endl;
} // Signature


/*------------------------------------------------------------------------
    Helper function.  Returns a pointer to the second word in
    the given line.
------------------------------------------------------------------------*/
char*
GetSecondWord( char* line )
{
    int	    i = 0;

    if ( ! line )
	return NULL;
    while ( line[i]  &&  (line[i] == ' ' || line[i] == '\t') )
	i++;
    while ( line[i]  &&  line[i] != ' ' && line[i] != '\t' )
	i++;
    while ( line[i]  &&  (line[i] == ' ' || line[i] == '\t') )
	i++;
    return &line[i];
} // GetSecondWord


/*------------------------------------------------------------------------
    Escapes the appropriate characters for output to a substitution file.
------------------------------------------------------------------------*/
void	SubEscape( ofstream& str, char* buff )
{
    int	    i;  // looping index

    for ( i = 0; buff[i]; i++ )
    {
	if ( buff[i] == '\\'  ||  buff[i] == '%'  ||  buff[i] == '#' )
	    str << '\\';
	str << buff[i];
    }
} // SubEscape


/*------------------------------------------------------------------------
    Member functions for StringBuff.
------------------------------------------------------------------------*/
StringBuff::StringBuff()
{
    _str = new char[GrowthFactor];
    if ( ! _str )
	THROW( "Memory allocation error!" );
    _str[0]	   = '\0';
    _capacity	   = GrowthFactor;
    _firstnonblank = -1;
}


StringBuff::StringBuff( const StringBuff& value ) :
    _str( NULL ), _capacity( 0 ), _firstnonblank( -1 )
{
    _str = new char[GrowthFactor];
    if ( ! _str )
	THROW( "Memory allocation error!" );
    _str[0]	   = '\0';
    _capacity	   = GrowthFactor;
    Append( (char*)value._str );
}


StringBuff::~StringBuff()
{
    _str[0]	   = '\0';
    delete [] _str;
    _str	   = NULL;
    _capacity	   = -999;
    _firstnonblank = -999;
}


int StringBuff::NonWhite()
{
    int	    i;

    if ( _firstnonblank < 0 )
    {
	for ( i = 0; _str[i]; i++ )
	    if ( ! isspace( _str[i] ) )
		break;
	_firstnonblank = i;
    }
    return _firstnonblank;
}


void	StringBuff::Append( char* otherstring, int maxlength )
{
    if ( ! otherstring )
	return;
    if ( strlen( otherstring ) > maxlength )
    {
	char	ch = otherstring[maxlength];
	otherstring[maxlength] = '\0';
	Append( otherstring );
	otherstring[maxlength] = ch;
    }
    else
	Append( otherstring );
}


void	StringBuff::Append( const char* otherstring )
{
    int	    targetlen;
    int	    neednewbuff = FALSE;
    char*   newbuff	= NULL;

    if ( ! otherstring )
	return;
    if ( _str )
	targetlen = strlen( _str ) + strlen( otherstring ) + 1;
    else
    {
	neednewbuff = TRUE;
	targetlen   = strlen( otherstring ) + 1;
    }
    while ( _capacity <= targetlen )
    {
	_capacity += GrowthFactor;
	neednewbuff = TRUE;
    }
    if ( neednewbuff )
    {
	newbuff = new char[_capacity];
	if ( ! newbuff )
	    THROW( "Memory allocation error!" );
	if ( _str )
	{
	    strcpy( newbuff, _str );
	    delete [] _str;
	}
	else
	    newbuff[0] = '\0';
	_str = newbuff;
    }
    strcat( _str, otherstring );
    _firstnonblank = -1;
}

int StringBuff::GetLine( ifstream& infile, int allowcontinue,
			     int eightbitclean )
{
    int	    i;		// looping index

    if ( ! allowcontinue )	// trivial case
	return FullLine( infile, eightbitclean );

			// check for line continuation syntax
    int	    done = FALSE;	// loop control

    Clear();
    while ( ! done )
    {
	StringBuff  extrabuffer;	// input buffer

	extrabuffer.FullLine( infile, eightbitclean );
	done = TRUE;
	for ( i = strlen( extrabuffer )-1; i >= 0; i-- )
	    if ( ! isspace( extrabuffer[i] ) )
	    {
		if ( extrabuffer[i] == '\\'  &&  i > 0  &&
		     extrabuffer[i-1] != '\\' )
		{
		    done	 = FALSE;
		    extrabuffer[i] = '\0';
		}
		break;
	    }
			// trim out comments
	for ( i = 0; extrabuffer[i]; i++ )
	    if ( extrabuffer[i] == '#'  &&
		 ( i == 0  ||  extrabuffer[i-1] != '\\') )
	    {
		extrabuffer[i] = '\0';
		break;
	    }
	Append( extrabuffer );
    }

					// now go through and unescape any
					// escaped characters
    int	    ind = 0;
    for ( i = 0; _str[i]; i++ )
    {
	if ( _str[i] != '\\' )
	    _str[ind++] = _str[i];
	else if ( _str[i+1] != '#'  &&  _str[i+1] != '\\' )
	    _str[ind++] = _str[i];
    }
    _str[ind] = '\0';

    if ( infile.fail()  || infile.bad() )
	return FALSE;
    return ! infile.eof();
}


int StringBuff::FullLine( ifstream& infile, int eightbitclean )
{
    int	    i;
    int	    nextchar;
    StringBuff  extrabuffer;

    Clear();
    infile.clear();	// not sure why the fail() flag is set on Linux
			// but it works fine to clear it out...
    (void) infile.getline( _str, _capacity-1 );
    if ( ! _str[0] )
	return ! infile.eof();
    if ( infile.gcount()  ==  _capacity-2 )
    {
	do
	{
	    extrabuffer.Clear();
	    infile.clear();	// not sure why the fail() flag is set on Linux
	    			// but it works fine to clear it out...
	    (void) infile.getline( extrabuffer._str, extrabuffer._capacity-1 );
	    Append( extrabuffer );
	}
	while ( ! infile.eof() && infile.gcount() == extrabuffer._capacity-2 );
    }

			// remove non-printing characters, in particular
			// the annoying ^M that gets stuck in when a file
			// comes from a DOS system but is parsed on a
			// UNIX system...
    if ( ! eightbitclean )
    {
	nextchar = 0;
	for ( i = 0; _str[i]; i++ )
	    if ( ! iscntrl( _str[i] )  ||  _str[i] == '\t' )
		_str[nextchar++] = _str[i];
	_str[nextchar] = '\0';
    }

    _firstnonblank = -1;

    return ! infile.eof();
}


int
StringBuff::FoundIdentifier(int& start, int& length, int ignoreAngleBracketed)
{
    length = 0;

    if ( start >= strlen(_str)  ||  start < 0 )
	return FALSE;

    int temArgNesting = 0; // > 0 if looking at arguments to a template
    int inComment = FALSE;
    int i;

    for ( i = start; _str[i]; i++ )
    {
	if ( inComment )
	{
	    if ( _str[i] == '*'  &&  _str[i+1] == '/' )
	    {
		i++;
		inComment = FALSE;
	    }
	}
	else if ( _str[i] == '/' )
	{
	    if ( _str[i+1] == '/' )
		return FALSE;
	    if ( _str[i+1] == '*' )
	    {
		i++;
		inComment = TRUE;
	    }
	}
	else if ( ignoreAngleBracketed  &&  _str[i] == '<' )
	{
	    temArgNesting++;
	}
	else if ( temArgNesting )
	{
	    if ( _str[i] == '>' )
		temArgNesting--;
	}
	else if ( isalpha(_str[i])  ||  _str[i] == '_' )
	{
	    start = i;
	    for ( ++i; _str[i]; i++ )
		if ( (! isalnum(_str[i]))  &&  _str[i] != '_' )
		    break;
	    length = i - start;
	    return TRUE;
	}
	else if ( ! isspace(_str[i]) )
	{
	    return FALSE;
	}
    }
    return FALSE;
}


void	StringBuff::TrimComments()
{
    int	    i;

    for ( i = 0; _str[i]; i++ )
	if ( _str[i] == '/'  &&  _str[i+1] == '/' )
	{
	    _str[i] = '\0';
	    break;
	}
    _firstnonblank = -1;
}


void	StringBuff::TrimOldComments()
{
    int	    i;
    int	    incomment = FALSE;

    for ( i = 0; _str[i]; i++ )
    {
	if ( _str[i] == '/'  &&  _str[i+1] == '*' )
	{
	    _str[i]   = ' ';
	    incomment = TRUE;
	}
	else if ( incomment  &&  _str[i] == '*'  && _str[i+1] == '/' )
	{
	    incomment = FALSE;
	    _str[i]   = ' ';
	    _str[i+1] = ' ';
	    i++;
	}
	else if ( incomment )
	    _str[i] = ' ';
    }
    _firstnonblank = -1;
}


void	StringBuff::TrimWhite()
{
    int	    nextchar  = 0;
    int	    lastwhite = TRUE;
    int	    i;

    for ( i = 0; _str[i]; i++ )
    {
	if ( isspace( _str[i] ) )
	{
	    if ( ! lastwhite )
		_str[nextchar++] = ' ';
	    lastwhite = TRUE;
	}
	else
	{
	    _str[nextchar++] = _str[i];
	    lastwhite	     = FALSE;
	}
    }
    if ( nextchar > 0  &&  (_str[i-1] == ' '   ||  _str[i-1] == '\t') )
	_str[nextchar-1] = '\0';
    _str[nextchar] = '\0';
    _firstnonblank = 0;
}


void
StringBuff::HTMLSafe()
{
    int	    i;
    int	    len;
    int	    nextchar;
    int	    numnew = 0;
    char*   buff   = NULL;

    len = strlen( _str );
    for ( i = 0; i < len; i++ )
	if ( _str[i] == '<'  ||  _str[i] == '>' )
	    numnew += 3;
	else if ( _str[i] == '&' )
	    numnew += 4;

				// make sure the buffer is big enough
    if ( len + numnew  <  _capacity )
    {
	while ( _capacity <= len + numnew )
	    _capacity += GrowthFactor;
	buff = _str;
	_str = new char[_capacity];
	if ( ! _str )
	{
	    _str = buff;
	    THROW( "Memory allocation error!" );
	}
	_str[0] = '\0';
    }

				// replace the meta-characters...
    nextchar = 0;
    for ( i = 0; buff[i]; i++ )
    {
	if ( buff[i] == '<' )
	{
	    _str[nextchar++] = '&';
	    _str[nextchar++] = 'l';
	    _str[nextchar++] = 't';
	    _str[nextchar++] = ';';
	}
	else if ( buff[i] == '>' )
	{
	    _str[nextchar++] = '&';
	    _str[nextchar++] = 'g';
	    _str[nextchar++] = 't';
	    _str[nextchar++] = ';';
	}
	else if ( buff[i] == '&' )
	{
	    _str[nextchar++] = '&';
	    _str[nextchar++] = 'a';
	    _str[nextchar++] = 'm';
	    _str[nextchar++] = 'p';
	    _str[nextchar++] = ';';
	}
	else
	    _str[nextchar++] = buff[i];
    }
    _str[nextchar] = '\0';

    delete [] buff;

}



void
StringBuff::URLSafe()
{
    int	    i;  // looping index

    if ( ! _str )
	return;
			// replace all reserved characters
    for ( i = 0; _str[i]; i++ )
    {
	if ( isspace( _str[i] ) )
	    _str[i] = '_';
	else if ( _str[i] == '='  ||  _str[i] == ';'  ||  _str[i] == '/'  ||
		  _str[i] == '#'  ||  _str[i] == '?'  ||  _str[i] == ':' )
	    _str[i] = '$';
    }
} // URLSafe



void
StringBuff::StripHTML()
{
    int	    i;
    int	    nextchar	  = 0;
    int	    inelement	  = FALSE;
    int	    suppresswhite = TRUE;

    for ( i = 0; _str[i]; i++ )
	if ( _str[i] == '<' )
	    inelement = TRUE;
	else if ( _str[i] == '>' )
	    inelement = FALSE;
	else if ( ! inelement )
	{
	    if ( _str[i] == ' '  ||  _str[i] == '\t' )
	    {
		if ( ! suppresswhite )
		    _str[nextchar++] = ' ';
	    }
	    else
	    {
		suppresswhite = TRUE;
		_str[nextchar++] = _str[i];
		if ( isalnum( _str[i] )  ||  _str[i] == '_' )
		    suppresswhite = FALSE;
	    }
	}

    _str[nextchar] = '\0';
}

void
StringBuff::ToUpper()
{
    int	    i;

    for ( i = 0; _str[i]; i++ )
	if ( islower( _str[i] ) )
	    _str[i] = (char) toupper( _str[i] );
}


void
StringBuff::MakeIntoDoc( Customize& custom )
{
    int	    startingnew = TRUE;

    for ( int i = 0; _str[i]; i++ )
    {
	if ( custom.ForUnimax() )
	{
	    int	    len = 0;

	    if ( strncmp( &_str[i], "/*@Introduction", 15 ) == 0 )
		len = 15;
	    else if ( strncmp( &_str[i], "/*@Variable", 11 ) == 0 )
		len = 11;
	    else if ( strncmp( &_str[i], "/*@Method", 9 ) == 0 )
		len = 9;
	    for ( int j = 0; j < len; j++ )
		_str[i+j] = ' ';
	}
	if ( strncmp( &_str[i], "//", 2 ) == 0  ||
	     strncmp( &_str[i], "/*", 2 ) == 0  ||
	     strncmp( &_str[i], "*/", 2 ) == 0 )
	{
	    _str[i]   = ' ';
	    _str[i+1] = ' ';
	}
	if ( _str[i] == '\n' )
	{
	    if ( startingnew )
	    {
		StringBuff  tmp;

		tmp.Append( &_str[i] );
		_str[i] = '\0';
		Append( "<p>" );
		Append( tmp );
	    }
	    startingnew = TRUE;
	}
	else if ( ! isspace( _str[i] ) )
	    startingnew = FALSE;
    }
} // MakeIntoDoc


ofstream& operator<<( ofstream& str, const StringBuff& buff )
{
	str << (const char*)buff;
	return str;
}


/*------------------------------------------------------------------------
------------------------------------------------------------------------*/
StringList* StringList::AddInOrder( char* newstr )
{
    StringList* newnode;
    StringList* node = this;
    int	    compvalue;

    newnode = new StringList( newstr );
    if ( ! newnode )
	THROW( "Memory allocation error!" );

				// see if it goes at the head of the list
    compvalue = strcmp( newstr, this->_string );
    if ( compvalue == 0 )
	return this;
    if ( compvalue < 0 )
    {
	newnode->_next = this;
	return newnode;
    }
				// see if it goes between existing nodes
    while ( node->_next )
    {
	compvalue = strcmp( newstr, node->_next->_string );
	if ( compvalue == 0 )
	    return this;
	if ( compvalue < 0 )
	{
	    newnode->_next = node->_next;
	    node->_next	   = newnode;
	    return this;
	}
	node = node->_next;
    }
				// put it at end, if it isn't in yet
    node->_next = newnode;

    return this;
} // AddInOrder

