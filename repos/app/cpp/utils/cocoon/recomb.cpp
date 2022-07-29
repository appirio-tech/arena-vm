
/*------------------------------------------------------------------------
				  COPYRIGHT
--------------------------------------------------------------------------
	  Copyright (C) 1995-2000
	  Jeff Kotula, Mark Penning
	  All Rights Reserved.
--------------------------------------------------------------------------
				   FILE LOG
--------------------------------------------------------------------------

    Source code for the recombine utility.  An m4 clone, but
    only supports the include and define statements.

    Recombine is not as general as m4.  Directives must appear
    alone on a line, and start in the first column.  Punctuation for
    the directives must be tightly packed.  The keywords used are
    cocooninclude, and cocoondefine to help avoid accidentally
    identifying text in an html page as directives.  The strings
    that can be substituted must be all alphanumeric, and have *no*
    whitespace.  The replacement strings may have whitespace.

------------------------------------------------------------------------*/

#include    "cobweb.h"
#include    "sys.h"


			// string to exclude from substitution
StringBuff  exclude;



/*------------------------------------------------------------------------
    Class to assist in the management of the substitution list.
------------------------------------------------------------------------*/
class Substitution
{
public:
    Substitution( char* target, char* replacement );
    ~Substitution();
    void Print();

			// finds sorted (by target) position within
			// this list and inserts the new substitution --
			// returns the new head of the list
    Substitution* Add( Substitution& newsub );

			// finds the given target, returns the replacement
			// string, if any
    char*   Find( char* target );

private:
    StringBuff	    _target;
    StringBuff	    _replacement;
    Substitution*   _next;

}; // Substitution

Substitution::Substitution( char* target, char* replacement )
{
    _target.Append( target );
    _replacement.Append( replacement );
    _next = NULL;
} // constructor

Substitution::~Substitution()
{
    if ( _next )
	delete _next;
    _next = NULL;
} // destructor

void Substitution::Print()
{
    cout << "target: " << _target << "\treplacement: " << _replacement
	 << endl;
    if ( _next )
	_next->Print();
}

Substitution* Substitution::Add( Substitution& newsub )
{
    Substitution*   node     = this;

				// see if it goes at the head of the list
    if ( strcmp( newsub._target, this->_target ) < 0 )
    {
	newsub._next = this;
	return &newsub;
    }

				// see if it goes between existing nodes
    while ( node->_next )
    {
	int compvalue;

	compvalue = strcmp( newsub._target, node->_next->_target );
	if ( compvalue == 0 )
	    return this;
	if ( compvalue < 0 )
	{
	    newsub._next = node->_next;
	    node->_next  = &newsub;
	    return this;
	}
	node = node->_next;
    }

				// put it at end, if it isn't in yet
    node->_next = &newsub;

    return this;
} // Add

char* Substitution::Find( char* target )
{
    Substitution*   node = this;

    while ( node )
    {
	int compvalue;

	compvalue = strcmp( target, node->_target );
	if ( compvalue == 0 )
	    return node->_replacement;
	if ( compvalue < 0 )
	    break;
	node = node->_next;
    }
    return NULL;
} // Find


/*------------------------------------------------------------------------
    Processes a single line of the input.  Recursively calls itself
    on the text of included files.
------------------------------------------------------------------------*/
void	ProcessLine( ofstream& outfile, StringBuff& line, char* filename,
		     int& linenumber )
{
			// the substitution list
    static Substitution*    sublist = NULL;

    //
    //  Check for the include directive
    //
    if ( strncmp( &line[0], "cocooninclude", 13 ) == 0 )
    {
	ifstream    includefile;    // file to include
	char*	    incfilename;    // its name
	char*	    end;	// used in searching
	StringBuff  includeline;    // line from include file
	int	inclinenumber;  // line in include file

	//
	//  Isolate the filename.
	//
	if ( line[13] != '(' )
	{		// error -- dump the line and continue
	    cout << "Recombine error: " << filename << ", line "
		 << linenumber << endl;
	    cout << "Missing left paren in include." << endl;
	    outfile << line << endl;
	    return;
	}
	incfilename = &line[14];
	end = strchr( incfilename, ')' );
	if ( ! end )
	{		// error -- dump the line and continue
	    cout << "Recombine error: " << filename << ", line "
		 << linenumber << endl;
	    cout << "Missing right paren in include." << endl;
	    outfile << line << endl;
	    return;
	}
	*end = '\0';

	//
	//  Sort the class name file before recombining it. This
	//  is a special, ugly hack to get the class listing in the
	//  library pages to come out in alphabetical order.
	//
	if ( strstr( incfilename, ".cls") != 0 )
	{
	    OS().SortClassFile(incfilename);
	}

	//
	//  Open the include file and dump its contents out recursively.
	//
	includefile.open( incfilename, ios::in );
	if ( ! includefile )
	{
	    cout << "Couldn't open include file " << incfilename
		 << " for reading.  Ignored." << endl;
	    return;
	}
	inclinenumber = 0;
	while ( includeline.GetLine( includefile ) )
	{
	    inclinenumber++;
	    ProcessLine( outfile, includeline, incfilename, inclinenumber );
	}
	includefile.close();
    } // cocooninclude

    //
    //  Check for the define directive
    //
    else if ( strncmp( &line[0], "cocoondefine", 12 ) == 0 )
    {
	char*	    target; // substitution target string
	char*	    rep;    // substitution replacement string
	int	i;  // looping index
	char*	    find;   // used in searching
	Substitution*   newsub; // new substitution object

	//
	//  Isolate the target and replacement strings
	//
	if ( line[12] != '(' )
	{		// error -- dump the line and continue
	    cout << "Recombine error: " << filename << ", line "
		 << linenumber << endl;
	    cout << "Missing left paren for cocoondefine." << endl;
	    outfile << line << endl;
	    return;
	}
	target = &line[13];
	find   = strchr( target, ',' );
	if ( find == NULL )
	{		// error -- dump the line and continue
	    cout << "Recombine error: " << filename << ", line "
		 << linenumber << endl;
	    cout << "Missing comma for cocoondefine." << endl;
	    outfile << line << endl;
	    return;
	}
	rep  = &find[1];
	for ( i = strlen( rep ) - 1; i >= 0; i-- )
	    if ( rep[i] == ')' )
		break;
	if ( i < 0 )
	{		// error -- dump the line and continue
	    cout << "Recombine error: " << filename << ", line "
		 << linenumber << endl;
	    cout << "Missing right paren for cocoondefine." << endl;
	    outfile << line << endl;
	    return;
	}
	*find  = '\0';
	rep[i] = '\0';

	//
	//  Construct a new substitution object and add it to
	//  the list.
	//
	newsub = new Substitution( target, rep );
	if ( ! newsub )
	    THROW( "Memory allocation error." );
	if ( ! sublist )
	    sublist = newsub;
	else
	    sublist = sublist->Add( *newsub );

    } // cocoondefine

    //
    //  Otherwise, perform text substitutions and dump it out
    //
    else
    {
	int start = 0;
	int end;

	if ( ! sublist )
	{
	    outfile << line << endl;
	    return;
	}

	while ( line[start] )
	{
					// find the beginning of a string
	    while ( line[start]  &&  ! isalnum( line[start] ) )
	    {
		outfile << line[start];
		start++;
	    }
					// find the end of a string
	    if ( line[start] )
	    {
		end = start + 1;
		while ( line[end]  &&
			( isalnum( line[end] ) || line[end] == '_' ) )
		    end++;
	    }
	    else
		end = start;
					// see if there is a replacement
					// string
	    char*   rep;
	    char    savedend;

	    savedend  = line[end];
	    line[end] = '\0';
	    if ( line[start] )
	    {
		rep = NULL;
		if ( strcmp( &line[start], exclude ) != 0 )
		    rep = sublist->Find( &line[start] );
		if ( rep )
		    outfile << rep;
		else
		    outfile << &line[start];
	    }

					// reset for next iteration
	    line[end] = savedend;
	    start     = end;
	}
	outfile << endl;
    }

} // ProcessLine


/*------------------------------------------------------------------------
    Prints a usage error message.
------------------------------------------------------------------------*/
static void UsageError()
{
    cout << "usage  : recombine  [options] infile  outfile";
    cout << "version: " << VERSION << endl;
    cout << "options:" << endl;
    cout << "	 -x string     Do not perform substitutions on this" << endl;
    cout << "		       string" << endl;
    THROW( "Terminated!" );
} // UsageError


/*------------------------------------------------------------------------

				MAIN

------------------------------------------------------------------------*/
main( int argc, char* argv[] )
{
TRY
{
    ifstream	infile;	    // input file
    ofstream	outfile;    // output file
    StringBuff  line;	    // input line from file
    int		linenumber; // line number in file
    int		i;	    // looping index

    if ( argc < 3 )
	UsageError();

    //
    //  Check out the command line arguments.
    //
    for ( i = 1; i < argc-2; i++ )
    {
	if ( strcmp( argv[i], "-x" ) == 0 )
	{
	    i++;
	    if ( i >= argc-2 )
		UsageError();
	    exclude.Append( argv[i] );
	}
	else
	    UsageError();
    }


    //
    //  Make sure the input and output files ain't the same...
    //
    if ( strcmp( argv[argc-2], argv[argc-1] ) == 0 )
    {
	cout << "Recombine won't overwrite the input file "
	     << argv[argc-2] << endl;
	return -1;
    }

    //
    //  Open the input and output files.
    //
    infile.open( argv[argc-2], ios::in );
    if ( ! infile )
    {
	cout << "Couldn't open input file " << argv[argc-2]
	     << " for reading." << endl;
	return -1;
    }
    outfile.open( argv[argc-1], ios::out );
    if ( ! outfile )
    {
	cout << "Couldn't open output file " << argv[argc-1]
	     << " for writing." << endl;
	return -1;
    }

    //
    //  Process the file
    //
    linenumber = 0;
    while ( line.GetLine( infile ) )
    {
	linenumber++;
	ProcessLine( outfile, line, argv[argc-1], linenumber );
    }
    infile.close();
    outfile.close();

    return 0;
}
CATCH( char* str )
{
#ifdef  NOEXCEPTIONS
	const char* str="Unknown";
#endif
    cout << "EXCEPTION: " << str << endl;
}
} // main

