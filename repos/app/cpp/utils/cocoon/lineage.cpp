
/*------------------------------------------------------------------------
				  COPYRIGHT
--------------------------------------------------------------------------
	  Copyright (C) 1995-2000
	  Jeff Kotula
	  All Rights Reserved.
--------------------------------------------------------------------------
				   FILE LOG
--------------------------------------------------------------------------

    Source code for the flataag utility.  Produces web pages for
    the short-flat form of each class.

    Note that memory management is punted for local classes -- we count
    on exitting the program to free up all dynamically allocated memory.

------------------------------------------------------------------------*/

#include    "cobweb.h"
#include    <stdlib.h>  // malloc


const int   MaxChain = 100;


/*------------------------------------------------------------------------
------------------------------------------------------------------------*/


/*------------------------------------------------------------------------

  Class for holding the inheritance data.

------------------------------------------------------------------------*/
class ClassNode
{
public:
    ClassNode( char* name, char* path );

    char*   Name()	{ return _name; }

    char*   Path()	{ return _path; }

    void
    AddParent( ClassNode& parent, int ispublic );

    int
    NumParent()
	{ return _numparent; }

    ClassNode*
    GetParent( int i )
	{ assert( i >= 0  &&  i < _numparent ); return _parent[i]; }

    int
    ParentPublic( int i )
	{ assert( i >= 0  &&  i < _numparent ); return _parentpublic[i]; }

private:
    enum
    {
	MaxParents = 10
    };

    StringBuff  _name;
    StringBuff  _path;
    int	    _numparent;
    ClassNode*  _parent[MaxParents];
    int	    _parentpublic[MaxParents];

}; // ClassNode


ClassNode::ClassNode( char* name, char* path ) :
    _numparent( 0 )
{
    int	    i;

    _name.Append( name );
    _path.Append( path );
    for ( i = 0; i < MaxParents; i++ )
    {
	_parent[i]	 = NULL;
	_parentpublic[i] = FALSE;
    }
}

void
ClassNode::AddParent( ClassNode& parent, int ispublic )
{
    if ( _numparent == MaxParents )
    {
	cout << "Exceeded maximimum number of parents for " << _name
	     << "." << endl
	     << "Parent " << parent.Name() << " ignored." << endl;
	return;
    }
    _parent[_numparent]	      = &parent;
    _parentpublic[_numparent] = ispublic;
    _numparent++;
}



/*------------------------------------------------------------------------

  Class for managing the inheritance data.

------------------------------------------------------------------------*/
class   FamilyTree
{
public:
    FamilyTree();

    ClassNode*
    FindClass( char* name );

    void	// assumes class is not already in tree
    AddClass( ClassNode& node );

    int
    NumClass()
	{ return _numclass; }

    ClassNode*
    GetClass( int i )
	{ assert( i >= 0  &&  i < _numclass ); return _class[i]; }

    void
    Debug( Customize& custom );

private:
    int	    _numclass;
    int	    _capacity;
    ClassNode** _class;

    enum
    {
	BlockFactor = 100
    };
};


FamilyTree::FamilyTree() :
    _numclass( 0 ), _capacity( 0 ), _class( NULL )
{
    _class = (ClassNode**) malloc( BlockFactor * sizeof( ClassNode* ) );
    if ( ! _class )
	THROW( "Memory allocation error!" );
    _capacity = BlockFactor;
}

ClassNode*
FamilyTree::FindClass( char* name )
{
    int	    i;
    int	    compvalue;

    for ( i = 0; i < _numclass; i++ )
    {
	compvalue = strcmp( name, _class[i]->Name() );
	if ( compvalue == 0 )
	    return _class[i];
	else if ( compvalue < 0 )
	    break;
    }
    return NULL;
}

void
FamilyTree::AddClass( ClassNode& node )
{
    if ( _numclass == _capacity )
    {
	ClassNode** newbuff;

	newbuff = (ClassNode**) realloc( (void *)_class,
			(_capacity+BlockFactor) * sizeof( ClassNode*) );
	if ( ! newbuff )
	    THROW( "Memory allocation error!" );
	_class	   = newbuff;
	_capacity += BlockFactor;
    }
    _class[_numclass] = &node;
    _numclass++;
}


void	FamilyTree::Debug( Customize& custom )
{
    int	    i;
    int	    j;
    int	    num;

    cout << "Number of classes: " << _numclass << endl;
    for ( i = 0; i < _numclass; i++ )
    {
	cout << i+1 << ": " << _class[i]->Path() << "/" << _class[i]->Name()
	     << custom.Extension() << endl;
	num = _class[i]->NumParent();
	if ( num )
	{
	    cout << "\t";
	    for ( j = 0; j < num; j++ )
	    {
		ClassNode   *parent;

		if ( j != 0 )
		    cout << ", ";
		parent = _class[i]->GetParent( j );
		cout << parent->Name();
		if ( ! _class[i]->ParentPublic( j ) )
		    cout << "*";
	    }
	    cout << endl;
	}
    }
}


/*------------------------------------------------------------------------
  Class for a parent/visiblity pair.
------------------------------------------------------------------------*/
class ParentLink
{
public:
    ParentLink() :
	_parent( NULL ), _ispublic( FALSE )
	{ }

    void
    SetParent( ClassNode& parent )
	{ _parent = &parent; }

    void
    SetPublic()
	{ _ispublic = TRUE; }

    ClassNode*  Parent()    { return _parent; }
    int	    isPublic()  { return _ispublic; }

private:
    ClassNode*  _parent;
    int	    _ispublic;
};


/*------------------------------------------------------------------------
  Puts together an inheritance chain.
------------------------------------------------------------------------*/
void AddLinks( ClassNode* node, int& numlink, ParentLink* chain )
{
    int	    i;
    int	    j;
    int	    numparent;
    ClassNode*  parent;

    numparent = node->NumParent();
				// base case
    if ( numparent <= 0 )
	return;

				// for each parent, add its parents to
				// the chain
    for ( i = 0; i < numparent; i++ )
    {
	parent = node->GetParent( i );
	if ( parent != node )
	    AddLinks( parent, numlink, chain );
    }

				// add each parent to the chain
    for ( i = 0; i < numparent; i++ )
    {
				// make sure the list has room
	parent = node->GetParent( i );
	if ( numlink == MaxChain )
	{
	    cout << "Sorry, exceeded maximum inheritance depth..." << endl;
	    break;
	}
				// make sure parent isn't already in list
	for ( j = 0; j < numlink; j++ )
	    if ( strcmp( chain[j].Parent()->Name(), parent->Name() ) == 0 )
		break;
	if ( j < numlink )
	    continue;
				// add it to list
	chain[numlink].SetParent( *parent );
	if ( node->ParentPublic( i ) )
	    chain[numlink].SetPublic();
	numlink++;
    }
}


/*------------------------------------------------------------------------
  Class for handling the internal buffering of lines that have been
  output.
------------------------------------------------------------------------*/
class FlatBuffer
{
public:
    FlatBuffer();

    void
    AddLine( StringBuff& stripped );

    int
    FindLine( StringBuff& stripped );

    void
    Reset();

private:

    enum
    {
	BlockFactor = 20,
	MaxLineSize = 1000
    };

    typedef char    LineBuff[MaxLineSize];
    int		_numlines;
    int		_capacity;
    LineBuff*	    _lines;

};

FlatBuffer::FlatBuffer() :
    _numlines( 0 ), _capacity( 0 ), _lines( NULL )
{
    int	    i;

    _lines = (LineBuff*) malloc( BlockFactor * sizeof( LineBuff ) );
    if ( ! _lines )
	THROW( "Memory allocation error!" );
    for ( i = 0; i < BlockFactor; i++ )
	_lines[i][0] = '\0';
    _capacity = BlockFactor;
}

void	FlatBuffer::AddLine( StringBuff& stripped )
{
    if ( strlen( stripped ) > MaxLineSize )
	THROW( "Sorry, exceeded maximum signature size." );
    if ( _numlines == _capacity )
    {
	LineBuff*   tmp;
	int	i;

	tmp = (LineBuff*) realloc( (void *)_lines,
			(_capacity+BlockFactor) * sizeof( LineBuff ));
	if ( ! tmp )
	    THROW( "Memory allocation error!" );
	_lines = tmp;
	for ( i = 0; i < BlockFactor; i++ )
	    _lines[_capacity+i][0] = '\0';
	_capacity += BlockFactor;
    }
    strcpy( _lines[_numlines], stripped );
    _numlines++;
}

int FlatBuffer::FindLine( StringBuff& stripped )
{
    int	    i;
    StringBuff  nocomments;

    nocomments.Append( stripped );
    nocomments.TrimComments();
    for ( i = 0; i < _numlines; i++ )
	if ( strcmp( _lines[i], nocomments ) == 0 )
	    return TRUE;
    return FALSE;
}

void	FlatBuffer::Reset()
{
    int	    i;

    for ( i = 0; i < _numlines; i++ )
	_lines[i][0] = '\0';
    _numlines = 0;
}




/*------------------------------------------------------------------------
  Appends a file to the flat listing.
------------------------------------------------------------------------*/
void	AppendFile( ifstream& infile, FlatBuffer& flatbuff, char* name,
		    ofstream& flat )
{
    StringBuff  buff;
    StringBuff  stripped;
    StringBuff  comment;
    int		namelen = strlen( name );
    int		nameind;
    int		strippedlen;
    int		i;

    while ( buff.GetLine( infile ) )
    {
	//
	//  Copy the input buffer and strip out all html.
	//
	stripped.Clear();
	stripped.Append( buff );
	stripped.StripHTML();
	strippedlen = strlen( stripped );

	//
	//  Don't output constructors, or the destructor.
	//
	if ( strippedlen > 0 )
	{
	    nameind = 0;
	    for ( i = 0; stripped[i]; i++ )
		if ( stripped[i] == '~' )
		{
		    nameind = i + 1;
		    break;
		}
	    if ( strncmp( &stripped[nameind], name, namelen ) == 0 )
	    {
		for ( i = nameind+namelen; stripped[i]; i++ )
		    if ( stripped[i] != ' ' )
			break;
		if ( stripped[i] == '(' )
		    continue;
	    }
	}

	//
	//  Don't output duplications of the member functions already
	//  found in a parent class.
	//
	if ( strippedlen > 0  &&  flatbuff.FindLine( stripped ) )
	    continue;

	//
	//  Buffer up comments until we have an actual line to output.
	//
	for ( i = 0; stripped[i]; i++ )
	    if ( ! isspace( stripped[i] ) )
		break;
	if ( stripped[i] == '/'  &&  stripped[i+1] == '/' )
	{
	    comment.Clear();
	    comment.Append( buff );
	    continue;
	}
	else if ( strlen( comment ) > 0  &&  strlen( stripped ) > 0 )
	{
	    flat << comment << endl;
	    comment.Clear();
	}


	//
	//  Put it out.
	//
	flat << buff << endl;
	if ( strncmp( stripped, "static ", 7 ) != 0  &&  strippedlen > 0 )
	{
	    stripped.TrimComments();
	    flatbuff.AddLine( stripped );
	}
    }
}


/*------------------------------------------------------------------------
  Process a file -- open it and call AppendFile.
------------------------------------------------------------------------*/
void	ProcessFile( char* path, char* name, ofstream& flat,
		     FlatBuffer& flatbuff, char* extension )
{
    char    filename[1000];
    ifstream	infile;

    sprintf( filename, "%s/%s.%s", path, name, extension );
    infile.open( filename, ios::in );
    if ( ! infile )
    {
	cout << "Couldn't open " << filename << " for reading." << endl;
	return;
    }

    AppendFile( infile, flatbuff, name, flat );
}


/*------------------------------------------------------------------------
    Recursively print out the child-tree.
------------------------------------------------------------------------*/
void	ListChildren( ClassNode* node, ofstream& outfile,
		      FamilyTree& inhdata, int& childfound )
{
    int		i;	    // looping index
    int		j;	    // looping index
    int		liststart = FALSE;
    ClassNode*  temp;
    ClassNode*  parent;

    for ( i = 0; i < inhdata.NumClass(); i++ )
    {
	temp = inhdata.GetClass( i );
	for ( j = 0; j < temp->NumParent(); j++ )
	{
	    parent = temp->GetParent( j );
	    if ( parent != temp  &&
		 strcmp( node->Name(), parent->Name() ) == 0 )
	    {
		if ( ! liststart )
		{
		    outfile << "<ul>" << endl;
		    liststart = TRUE;
		}
		outfile << "<li>" << temp->Name() << endl;
		ListChildren( temp, outfile, inhdata, childfound );
		childfound = TRUE;
		break;
	    }

	} // for j
    } // for i

    if ( liststart )
	outfile << "</ul>" << endl;

} // ListChildren


/*------------------------------------------------------------------------
------------------------------------------------------------------------*/
void	UsageError()
{
cout << "Usage: flataag  [options]" << endl;
cout << "Options:" << endl;
cout << "    -f familyfile  Specifies the file containing the inheritance\n";
cout << "		    information. Must be sorted..." << endl;
cout << "    -c customfile  Specifies a customization file to use" << endl;
cout << "		    instead of cocoon.ini" << endl;
cout << endl;
THROW( "Terminated." );
} // UsageError

/*------------------------------------------------------------------------

    MAIN PROGRAM

    Assumes that the family inheritance data file is sorted.

------------------------------------------------------------------------*/
main( int argc, char* argv[] )
{
TRY
{
    int	    i;	    // looping index
				// customization file
    char*   custfile   = NULL;
				// file with inheritance family information
    char*   familyfile = "family.xrf";
    ifstream	familydata; // inheritance data file
    FamilyTree  inhdata;    // inheritance data structure
    StringBuff  buff;	    // input buffer
    int	    linenumber; // line number in file
    ClassNode*  node;	    // class description node
    FlatBuffer  flatbuff;   // buffer for flat-form output
    StringList* famlist = NULL; // family data list
    StringList* nextfam;    // next element in list

    //
    //  Process the command line arguments
    //
    if ( argc <= 1 )
	UsageError();
    for ( i = 1; i < argc; i += 2 )
    {
	if ( strcmp( argv[i], "-f" ) == 0 )
	    familyfile = argv[i+1];
	else if ( strcmp( argv[i], "-c" ) == 0 )
	    custfile = argv[i+1];
	else
	    break;
    }


    //
    //  Create the customization object.
    //
    Customize   custom( FALSE, custfile, NULL, NULL, NULL );


    //
    //  Load in and sort the inheritance data.
    //
    familydata.open( familyfile, ios::in );
    if ( ! familydata )
    {
	cout << "\tInheritance data file not found." << endl;
	return 0;
    }
    while ( buff.GetLine( familydata ) )
    {
	if ( ! famlist )
	    famlist = new StringList( buff );
	else
	    famlist = famlist->AddInOrder( buff );
	if ( ! famlist )
	    THROW( "Memory allocation error." );
    }
    if ( ! famlist )
    {
	cout << "\tNo class or inheritance data found." << endl;
	return 0;
    }


    //
    //  Loop through all the data.
    //
    linenumber = 0;
    nextfam    = famlist;
    while ( nextfam )
    {
	buff.Clear();
	buff.Append( nextfam->String() );
	linenumber++;

				// class location definition line
	if ( buff[0] == '#' )
	{
	    int	    path;

				// find path string
	    for ( path = 2; buff[path]; path++ )
		if ( buff[path] == '#' )
		    break;
	    if ( ! buff[path] )
		cout << "Bogus line (" << linenumber << ") in inheritance"
		     << "data file." << endl;
	    else
	    {
		buff[path] = '\0';
		path++;
				// create node and add it to the table
		node = inhdata.FindClass( &buff[1] );
		if ( ! node )
		{
		    node = new ClassNode( &buff[1], &buff[path] );
		    if ( ! node )
			THROW( "Memory allocation error" );
		    inhdata.AddClass( *node ) ;
		}
		else
		    cout << "Duplicate line for class " << &buff[1]
			 << " found and ignored." << endl;
	    }

	}

				// class inheritance line
	else if ( buff[0] == '%' )
	{
	    int	    parent;
	    int	    visibility;
	    int	    ispublic;
	    ClassNode*  parentnode;

	    for ( parent = 2; buff[parent]; parent++ )
		if ( buff[parent] == '%' )
		    break;
	    if ( ! buff[parent] )
		cout << "Bad line (" << linenumber << ") in data file." << endl;
	    else
	    {
		buff[parent] = '\0';
		parent++;
		for ( visibility = parent+1; buff[visibility]; visibility++)
		    if ( buff[visibility] == '%' )
			break;
		if ( ! buff[visibility] )
		    cout << "Bad line (" << linenumber << ") in data file."
			 << endl;
		else
		{
		    buff[visibility] = '\0';
		    visibility++;
					// create class if it doesn't exist
		    node = inhdata.FindClass( &buff[1] );
		    if ( ! node )
		    {
			node = new ClassNode( &buff[1], NULL );
			if ( ! node )
			    THROW( "Memory allocation error" );
			inhdata.AddClass( *node ) ;
		    }
					// see if the parent exists
		    parentnode = inhdata.FindClass( &buff[parent] );
		    if ( ! parentnode )
		    {
			parentnode = new ClassNode( &buff[parent], NULL );
			if ( ! parentnode )
			    THROW( "Memory allocation error" );
			inhdata.AddClass( *parentnode ) ;
		    }
					// add parent to node class
		    ispublic = FALSE;
		    if ( strcmp( &buff[visibility], "public" ) == 0 )
			ispublic = TRUE;
		    node->AddParent( *parentnode, ispublic );
		}
	    }
	}

	else
	    cout << "Bogus line (" << linenumber << ") in inheritance"
		 << "data file." << endl;

	nextfam = nextfam->Next();
    }
    if ( famlist )
	delete famlist;
    famlist = NULL;

    // inhdata.Debug( custom );


    //
    //  Create the base flat-form for each class.
    //  Go through the inheritance data -- for each class for which
    //  we have a location, generate the flat-form.
    //
    for ( i = 0; i < inhdata.NumClass(); i++ )
    {
	ofstream    flat;
	char	    filename[1000];
	ParentLink  chain[MaxChain];
	int	numlink = 0;
	int	parentind;
	int	ind;		// looping index
	int	childfound;
	int	needmessage;

	node = inhdata.GetClass( i );
	if ( strlen( node->Path() ) <= 0 )
	    continue;

	cout << "\t" << node->Name() << "..." << endl;
	flatbuff.Reset();

	//
	//  Get the chain of parents for the class.
	//
	AddLinks( node, numlink, chain );


	//
	//  Open the .flt file for the class.
	//
	sprintf( filename, "%s/%s.flt", (char*)node->Path(),
		 (char*)node->Name() );
	flat.open( filename, ios::out );
	if ( ! flat )
	{
	    cout << "Unable to open flat file for " << node->Name()
		 << "." << endl;
	    continue;
	}


	//
	//  For each parent, starting at the top, write out one or both
	//  of the .pub and .prt files.  For parents whose location we
	//  don't know, skip 'em...
	//

	flat << "<a name=\"flat\">" << custom.Rule() << "</a>" << endl
	     << "<h" << custom.BaseHeading()+1 << ">All Members</h"
	     << custom.BaseHeading()+1 << ">" << endl;

	if ( custom.UseTable() )
	    flat << "<table>" << endl;
	else
	    flat << "<dl>" << endl;

				// public
	if ( custom.UseTable() )
	    flat << "<tr><th align=\"left\" colspan=3>public:</th></tr>"
		 << endl;
	else
	    flat << "<dt><strong>public:</strong>" << endl;
	for ( parentind = 0; parentind < numlink; parentind++ )
	{
	    ClassNode*  parent = chain[parentind].Parent();

	    if ( strlen( parent->Path() ) <= 0 )
		continue;
	    if ( ! chain[parentind].isPublic() )
		continue;

	    ProcessFile( parent->Path(), parent->Name(), flat,
			 flatbuff, "pub" );
	}
	ProcessFile( node->Path(), node->Name(), flat, flatbuff, "pub" );

				// protected
	if ( custom.UseTable() )
	    flat << "<tr><th align=\"left\" colspan=3>protected:</th></tr>"
		 << endl;
	else
	    flat << "<dt><strong>protected:</strong>" << endl;
	for ( parentind = 0; parentind < numlink; parentind++ )
	{
	    ClassNode*  parent = chain[parentind].Parent();

	    if ( strlen( parent->Path() ) <= 0 )
		continue;

	    if ( ! chain[parentind].isPublic() )
		ProcessFile( node->Path(), node->Name(), flat, flatbuff,
			     "pub" );

	    ProcessFile( parent->Path(), parent->Name(), flat,
			 flatbuff, "prt" );
	}
	ProcessFile( node->Path(), node->Name(), flat, flatbuff, "prt" );

	if ( custom.UseTable() )
	    flat << "</table>" << endl;
	else
	    flat << "</dl>" << endl;

	if ( custom.BackToTop() )
	    flat << custom.BackToTop() << node->Name() << "<p>" << endl;


	//
	//  Output parent list
	//
	flat << "<a name=\"parents\">" << custom.Rule() << "</a>" << endl
	     << "<h" << custom.BaseHeading()+1 << ">Ancestors</h"
	     << custom.BaseHeading()+1 << ">" << endl;

	needmessage = FALSE;
	if ( node->NumParent() > 0 )
	{
	    flat << "Inheritance chain for " << node->Name() << ":<p>" << endl
		 << "<ul>" << endl;
	    for ( ind = 0; ind < numlink; ind++ )
	    {
		flat << "<li>";
		if ( ! chain[ind].isPublic() )
		{
		    flat << "<i>";
		    needmessage = TRUE;
		}
		flat << chain[ind].Parent()->Name();
		if ( ! chain[ind].isPublic() )
		    flat << "</i>";
		flat << endl;
	    }
	    flat << "</ul>" << endl;
	    if ( needmessage )
		 flat << "<i>Italicized classes use protected inheritance.</i>"
		      << endl << "<p>" << endl;
	}
	else
	    flat << "Class does not inherit from any other class.<p>" << endl;

	if ( custom.BackToTop() )
	    flat << custom.BackToTop() << node->Name() << "<p>" << endl;


	//
	//  Output child hierarchy
	//
	flat << "<a name=\"children\">" << custom.Rule() << "</a>" << endl
	     << "<h" << custom.BaseHeading()+1 << ">Descendants</h"
	     << custom.BaseHeading()+1 << ">" << endl;
	childfound = FALSE;
	ListChildren( node, flat, inhdata, childfound );

	if ( ! childfound )
	    flat << "Class is not inherited by any others.<p>" << endl;

	if ( custom.BackToTop() )
	    flat << custom.BackToTop() << node->Name() << "<p>" << endl;


    }  // for each class
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


