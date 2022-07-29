
/*------------------------------------------------------------------------
				  COPYRIGHT
--------------------------------------------------------------------------
	  Copyright (C) 1995-2000
	  Jeff Kotula
	  All Rights Reserved.
--------------------------------------------------------------------------
				   FILE LOG
--------------------------------------------------------------------------

    Source code for the anchorword utility.  Substitutes class names and
    member function names with html anchors.

------------------------------------------------------------------------*/

#include    "util.h"
#include    "sys.h"


//=========================================================================
//
//
//=========================================================================



	/////////////////////////
	//
	//  enum for the states of an AnchorMachine.
	//
typedef enum {
    StartState = 0,
    InWord     = 1,
    InHtml     = 2,
    InAnchor   = 3,
    GotSlash   = 4,
    EndAnchor  = 5,
    EndHtml    = 6	    // keep as last
} States;

	/////////////////////////
	//
	//  enum for the lexical elements of an AnchorMachine.
	//
typedef enum {
    WordChar   = 0,
    OpenParen  = 1,
    OpenAngle  = 2,
    LetterA    = 3,
    Slash      = 4,
    CloseAngle = 5,
    OtherChar  = 6	    // keep as last
} Inputs;

class AnchorMachine;

	/////////////////////////
	//
	//  State change callback for an AnchorMachine.
	//
typedef void	(*StateChange)( AnchorMachine& machine, ofstream& str);


	/////////////////////////
	//
	//  A cell of the AnchorMachine's state-change table.
	//
	//  [data] nextstate	Next state for the automata.
	//  [data] callback Callback to trigger upon state change.
	//
typedef struct
{
    States	nextstate;
    StateChange	    callback;
} TableEntry;


	/////////////////////////
	//
	//  Substitution table entry.  Used to develop a list of
	//  string substitutions for an AnchorMachine.
	//
	//  [data] target   The string to replace.
	//  [data] replacement  It's replacement.
	//  [data] next	    The next item in the table.
	//
typedef struct SubEntry
{
    StringBuff  target;
    StringBuff  replacement;
    SubEntry*   next;
} SubEntry;



/*------------------------------------------------------------------------
CLASS
    AnchorMachine

    Automata class for implementing HTML-smart text substitution.

KEYWORDS
    Automata, finite_state_machine, html, parsing

DESCRIPTION
    Simple finite automata for parsing text, performing anchoring on
    selected strings.  Key contribution of this class is that it
    will not perform the substitutions within an html directive, nor
    within an anchored phrase.

------------------------------------------------------------------------*/
class AnchorMachine {
public:
	///////////////////////////
	//
	//  Constructor.  Loads up the file of anchor
	//  substitution strings.
	//
	//  [in] subfile
	//	File of anchor substitution strings.  It is
	//	assumed that this file is sorted on the string
	//	being substituted for.
	//  [in] anchorclass
	//	TRUE if we are going to be doing anchoring of
	//	class names, otherwise we assume we are looking
	//	for instances of member usage.
	//  [in] exclude
	//	Do <strong>not</strong> perform any substitutions
	//	on this string even if it shows up in the substitution
	//	file.
	//
    AnchorMachine( ifstream& subfile, int anchorclass, char* exclude );


	///////////////////////////
	//
	//  Parse the next character from the input.
	//
	//  [in] next	    Next character from the input stream.
	//  [in] outstr	    Stream to spit output to.
	//
    void
    NextChar( char next, ofstream& outstr );


	///////////////////////////
	//
	//  Resets the machine to initial state, flushing any
	//  buffered text.
	//
	//  [in] outstr	    Stream to spit output to.
	//
    void
    Reset( ofstream& outstr );


private:

				// callback functions
    static void	    BufferChar( AnchorMachine& machine, ofstream& str );
    static void	    OutputChar( AnchorMachine& machine, ofstream& str );
    static void	    SubClass( AnchorMachine& machine, ofstream& str );
    static void	    SubMember( AnchorMachine& machine, ofstream& str );


				//  Checks to see if the current value of
				// _outputbuff is in the substitution table.
				// If it is, the replacement string is output,
				// otherwise the buffered string is output.
				// In either case, the buffered string is
				// cleared.
    void    Substitute( ofstream& outstr );


				// state table
    TableEntry	    _table[EndHtml+1][OtherChar+1];
    StringBuff	    _outputbuff;
    States	_currentstate;
    char	_currentchar;
    int		_numsub;
    SubEntry*	    _sublist;
    SubEntry**	    _subtable;
    BitField	    _anchorclass : 1;

};


/*------------------------------------------------------------------------
    Member functions for the AnchorMachine class.  The state transition
    table for the automata (with callbacks) is as follows:


						 |  C  |
			 |  O  |  O  |		 |  l  |  O  |
		   |  W  |  p  |  p  |		 |  o  |  t  |
		   |  o  |  e  |  e  |  L  |	 |  s  |  h  |
		   |  r  |  n  |  n  |  e  |	 |  e  |  e  |
		   |  d  |  P  |  A  |  t  |  S  |  A  |  r  |
		   |  C  |  a  |  n  |  t  |  l  |  n  |  C  |
		   |  h  |  r  |  g  |  e  |  a  |  g  |  h  |
	    Tokens |  a  |  e  |  l  |  r  |  s  |  l  |  a  |
    States	   |  r  |  n  |  e  |  A  |  h  |  e  |  r  |
    ---------------+-----+-----+-----+-----+-----+-----+-----+
    (0) StartState | 1/b |  0  |  2  | 1/b |  0  |  0  |  0  |
    (1) InWord	   | 1/b | 0/M | 0/C | 1/b | 0/C | 0/C | 0/C |
    (2) InHtml	   |  6  |  6  |  6  |  3  |  6  |  0  |  6  |
    (3) InAnchor   |  3  |  3  |  3  |  3  |  4  |  3  |  3  |
    (4) GotSlash   |  3  |  3  |  3  |  5  |  3  |  3  |  3  |
    (5) EndAnchor  |  3  |  3  |  3  |  3  |  3  |  0  |  3  |
    (6) EndHtml	   |  6  |  6  |  6  |  6  |  6  |  0  |  6  |


    Callbacks :
	Empty   OutputChar
	  b	BufferChar
	  C SubClass
	  M	SubMember

------------------------------------------------------------------------*/
AnchorMachine::AnchorMachine( ifstream& subfile, int anchorclass,
			      char* exclude ) :
    _anchorclass( anchorclass ), _currentchar( '\0' ),
    _currentstate( StartState ), _subtable( NULL ),
    _sublist( NULL ), _numsub( 0 )
{
    int	    i, j;
    StringBuff  inbuff;
    SubEntry	*lastent   = NULL;
    StringList* substrings = NULL;
    StringList* nextsubstring;

				// initialize the state change table
    for ( i = 0; i <= EndHtml; i++ )
	for ( j = 0; j <= OtherChar; j++ )
	{
	    _table[i][j].nextstate = (States)i;
	    _table[i][j].callback  = OutputChar;
	}

				// fill in the parts of the table that can't
				// use the defaults
    _table[StartState][WordChar].nextstate  = InWord;
    _table[StartState][WordChar].callback   = BufferChar;
    _table[StartState][OpenAngle].nextstate = InHtml;
    _table[StartState][LetterA].nextstate   = InWord;
    _table[StartState][LetterA].callback    = BufferChar;

    _table[InWord][OpenParen].nextstate  = StartState;
    _table[InWord][OpenAngle].nextstate  = StartState;
    _table[InWord][Slash].nextstate	 = StartState;
    _table[InWord][CloseAngle].nextstate = StartState;
    _table[InWord][OtherChar].nextstate  = StartState;

    _table[InWord][OpenParen].callback   = SubMember;
    _table[InWord][OpenAngle].callback   = SubClass;
    _table[InWord][Slash].callback	 = SubClass;
    _table[InWord][CloseAngle].callback  = SubClass;
    _table[InWord][OtherChar].callback   = SubClass;
    _table[InWord][WordChar].callback	 = BufferChar;
    _table[InWord][LetterA].callback	 = BufferChar;

    _table[InHtml][WordChar].nextstate   = EndHtml;
    _table[InHtml][OpenParen].nextstate  = EndHtml;
    _table[InHtml][OpenAngle].nextstate  = EndHtml;
    _table[InHtml][LetterA].nextstate	 = InAnchor;
    _table[InHtml][Slash].nextstate	 = EndHtml;
    _table[InHtml][CloseAngle].nextstate = StartState;
    _table[InHtml][OtherChar].nextstate  = EndHtml;

    _table[InAnchor][Slash].nextstate	 = GotSlash;

    for ( i = GotSlash; i <= EndAnchor; i++ )
	for ( j = 0; j <= OtherChar; j++ )
	    _table[i][j].nextstate = InAnchor;
    _table[GotSlash][LetterA].nextstate	    = EndAnchor;
    _table[EndAnchor][CloseAngle].nextstate = StartState;

    _table[EndHtml][CloseAngle].nextstate   = StartState;

				    // load in the substitution list and sort
				// it out...
    while ( inbuff.GetLine( subfile, TRUE ) )
    {
	if ( ! inbuff[inbuff.NonWhite()] )
	    continue;
	if ( ! substrings )
	    substrings = new StringList( inbuff );
	else
	    substrings = substrings->AddInOrder( inbuff );
	if ( ! substrings )
	    THROW( "Memory allocation error." );
    }

				// go through all the substitution strings
    nextsubstring = substrings;
    while ( nextsubstring )
    {
	int	i;
	int	nextchar;
	SubEntry    *newentry = new SubEntry;

	if ( ! newentry )
	    THROW( "Memory allocation error!" );
	newentry->next = NULL;
	inbuff.Clear();
	inbuff.Append( nextsubstring->String() );

	nextchar = 0;
	if ( inbuff[0] !=  '%' )
	{
	    cout << "First '%' is missing: " << inbuff << endl;
	    THROW( "Poorly formatted substitution strings." );
	}
	for ( i = 1; inbuff[i]; i++ )
	{
	    if ( inbuff[i] == '%' )
		break;
	    newentry->target[nextchar++] = inbuff[i];
	}
	newentry->target[nextchar] = '\0';
	if ( ! inbuff[i] )
	{
	    cout << "Couldn't find second '%': " << inbuff << endl;
	    THROW( "Poorly formatted substitution strings." );
	}
	nextchar = 0;
	for ( i++; inbuff[i]; i++ )
	{
	    if ( inbuff[i] == '%' )
		break;
	    newentry->replacement[nextchar++] = inbuff[i];
	}
	newentry->replacement[nextchar] = '\0';
	if ( ! inbuff[i] )
	{
	    cout << "Final '%' missing: " << inbuff << endl;
	    THROW( "Poorly formatted substitution strings." );
	}

	if ( ! exclude  ||  strcmp( newentry->target, exclude ) != 0 )
	{
	    if ( ! lastent )
		_sublist = newentry;
	    else
		lastent->next = newentry;
	    lastent = newentry;
	    _numsub++;
	}
	else
	    delete newentry;

	nextsubstring = nextsubstring->Next();
    }
    if ( substrings )
	delete substrings;
    substrings = NULL;

				// make an indexable table, allowing for
				// binary searching
    if ( ! _numsub )
	return;

    typedef SubEntry* SubEntryPtr;	// need this to make the syntax
    _subtable = new SubEntryPtr[_numsub];   // for 'new' work out...
    if ( ! _subtable )
	THROW( "Memory allocation error!" );

    lastent = _sublist;
    for ( i = 0; i < _numsub; i++ )
    {
	_subtable[i] = lastent;
	lastent	     = lastent->next;
    }
    assert( lastent == NULL );

}


void	AnchorMachine::NextChar( char next, ofstream& outstr )
{
    Inputs  nexttok = OtherChar;

				// find the type of the token
    _currentchar = next;
    if ( _currentchar == 'a'  ||  _currentchar == 'A' )
	nexttok = LetterA;
    else if ( isalnum( _currentchar ) || _currentchar == '_'  )
	nexttok = WordChar;
    else if ( _currentchar == '~'  &&  ! _outputbuff[0] )
	nexttok = WordChar;
    else if ( _currentchar == '(' )
	nexttok = OpenParen;
    else if ( _currentchar == '<' )
	nexttok = OpenAngle;
    else if ( _currentchar == '/' )
	nexttok = Slash;
    else if ( _currentchar == '>' )
	nexttok = CloseAngle;

				// call transition function, get the next state

    /* machine debugging stuff
    outstr << "curstate : " << _currentstate << " in : " << _currentchar
	   << " next : " << _table[_currentstate][nexttok].nextstate;
    if ( ! _table[_currentstate][nexttok].callback )
	outstr << " no callback" << endl;
    else if ( _table[_currentstate][nexttok].callback == BufferChar )
	outstr << " BufferChar" << endl;
    else if ( _table[_currentstate][nexttok].callback == OutputChar )
	outstr << " OutputChar" << endl;
    else if ( _table[_currentstate][nexttok].callback == SubClass )
	outstr << " SubClass" << endl;
    else if ( _table[_currentstate][nexttok].callback == SubMember )
	outstr << " SubMember" << endl;
    */

    if ( _table[_currentstate][nexttok].callback )
	_table[_currentstate][nexttok].callback( *this, outstr );
    _currentstate = _table[_currentstate][nexttok].nextstate;

}


void	AnchorMachine::Reset( ofstream& outstr )
{
    if ( strlen( _outputbuff ) )
    {
	outstr << _outputbuff;
	_outputbuff.Clear();
    }
    _currentstate = StartState;
    _currentchar  = '\0';
}


void	AnchorMachine::BufferChar( AnchorMachine& machine, ofstream& str )
{
    char    tmp[2];

    UNUSED( str );
    tmp[0] = machine._currentchar;
    tmp[1] = '\0';
    machine._outputbuff.Append( tmp );
}


void	AnchorMachine::OutputChar( AnchorMachine& machine, ofstream& str )
{
    if ( machine._currentchar )
	str << machine._currentchar;
}


void	AnchorMachine::Substitute( ofstream& outstr)
{
    int	    i;
    int	    compvalue;

    for ( i = 0; i < _numsub; i++ )
    {
	compvalue = strcmp( _subtable[i]->target, _outputbuff );
	if ( compvalue == 0 )
	{
	    outstr << _subtable[i]->replacement;
	    break;
	}
	if ( compvalue > 0 )
	    break;
    }

    if ( i == _numsub  ||  compvalue != 0 )
	outstr << _outputbuff;

    _outputbuff.Clear();
}


void	AnchorMachine::SubClass( AnchorMachine& machine, ofstream& str )
{
    if ( ! machine._anchorclass )
    {
	str << machine._outputbuff;
	machine._outputbuff.Clear();
    }
    else
	machine.Substitute( str );
    OutputChar( machine, str );
}


void	AnchorMachine::SubMember( AnchorMachine& machine, ofstream& str )
{
    if ( machine._anchorclass )
    {
	SubClass( machine, str );
	return;
    }
    else
	machine.Substitute( str );
    OutputChar( machine, str );
}


/*------------------------------------------------------------------------
------------------------------------------------------------------------*/
static void UsageError()
{
    cout << "usage  : anchorword  [-options] subsitution_file  infile  outfile"
	 << endl;
    cout << "version: " << VERSION << endl;
    cout << "options:" << endl;
    cout << "	 -m	      Substitute for member calls" << endl;
    cout << "	 -x string    Do not perform substitutions on this string"
	 << endl;
    THROW( "Terminated!" );
} // UsageError


/*------------------------------------------------------------------------
    MAIN
------------------------------------------------------------------------*/
main(int argc, char* argv[] )
{
TRY
{
    char	nextchar;
    char*	exclude	    = NULL;
    int		anchorclass = TRUE;
    ifstream	subfile;
    ifstream	infile;
    ofstream	outfile;
    int		i;

    //
    //  Check the input arguments
    //
    if ( argc < 4 )
	UsageError();
    for ( i = 1; i < argc-3; i++ )
    {
	if ( strcmp( argv[i], "-m" ) == 0 )
	    anchorclass = FALSE;
	else if ( strcmp( argv[i], "-x" ) == 0 )
	{
	    i++;
	    if ( i >= argc-3 )
		UsageError();
	    exclude = argv[i];
	}
	else
	    UsageError();
    }
    if ( strcmp( argv[argc-1], argv[argc-2] ) == 0 )
    {
	cout << "Error: anchorword will not overwrite the input file!" << endl;
	return -1;
    }


    //
    //  Open substitution string file. If there isn't one, just copy the
    //  input file to the output file.
    //
    if ( ! OS().Exists(argv[argc-3]) )
    {
	OS().CopyFile(argv[argc-2], argv[argc-1], FALSE);
	return 0;
    }
    subfile.open( argv[argc-3], ios::in );
    if ( ! subfile )
    {
	cout << "Couldn't open " << argv[argc-3] << " for reading." << endl;
	return -1;
    }


    //
    //  Make the state machine
    //
    AnchorMachine   machine( subfile, anchorclass, exclude );


    //
    //  Open input file
    //
    infile.open( argv[argc-2], ios::in );
    if ( ! infile )
    {
	cout << "Couldn't open " << argv[argc-2] << " for reading." << endl;
	return -1;
    }


    //
    //  Open output file
    //
    outfile.open( argv[argc-1], ios::out );
    if ( ! outfile )
    {
	cout << "Couldn't open " << argv[argc-1] << " for writing." << endl;
	return -1;
    }


    //
    //  Perform the substitutions.
    //
    while ( infile.get( nextchar ) )
	machine.NextChar( nextchar, outfile );

    machine.Reset( outfile );
}

CATCH( char *str )
{
#ifdef  NOEXCEPTIONS
	const char* str="Unknown";
#endif
    cout << "EXCEPTION: " << str << endl;
    return -1;
}

    return 0;

}

