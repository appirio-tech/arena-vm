
/*------------------------------------------------------------------------
				  COPYRIGHT
--------------------------------------------------------------------------
	  Copyright (C) 1995-2000
	  Jeff Kotula
	  All Rights Reserved.
--------------------------------------------------------------------------
				   FILE LOG
--------------------------------------------------------------------------

    Include file for the cocoon programs.

LIBRARY
    cocoon

OVERVIEW
    The main idea behind cocoon is to break the include file down
    into sections, based on the occurrence of certain sentinel
    strings.  For instance, the body of a class is recognized by
    searching for a string "class" starting in column 1.  Documentation
    headings are recognized as all-upper-case strings starting in
    column 1.

    The rules for sentinel recognition will vary with the section
    that they herald.  In general, constraints will be as loose as
    reasonable.

    It is assumed throughout that the header file provided is
    done in accord with the formatting assumptions documented elsewhere.
    In other words, cocoon is lousy for diagnostics.  But since
    the assumptions and constraints are fairly flexible, this hopefully
    won't be a major weakness.


DATA FLOW
    Here is a rough diagram of the data flow for the cocoon utilities.
    Data stores are enclosed in boxes, executables are bold script, and
    input files coming from the using are in the thick boxes:

    <img src=dataflow.gif>

    As you can see, the include files are broken down into a set of partial
    files.  Each class and library will have a set of ten or so partial
    files that contain different pieces of the documentation.  These are
    later recombined, but in a different order than they appear in the
    source file.  In the end, all the temporary files are folded back into
    one may HTML file.  The within-file and inter-file links are then
    inserted.

    I have some thoughts regarding how to simplify this processing by using
    a two-pass algorithm rather than the one-pass algorithm currently used.
    Depending on how things go, I may rewrite Cocoon to use these other
    algorithms.

INTERNALS
    The individual include files are processed by a set of nesting handler
    objects.  A handler knows how to handle source text that has some well-
    defined beginning sentinel.  Handlers continue operating until either
    they find an ending sentinel, or some other handler preempts them.
    When a handler releases control, control passes back to the handler that
    created it.

    Most of the rest of the code supports the main processing of the
    handlers.  There are utility classes and functions for easier string
    handling and OS support.  There are main programs that handle some of the
    other discrete bits of processing, such as generating the keyword cross-
    reference page, or generating the flat class summaries based on the
    inheritance information gathered by the handlers.

    The main <b>cocoon</b> program controls all the processing, sequencing
    the calling of the other utilities.  It also does all this in an
    operating system independent way, making things much more portable.  It
    is also responsible for the processing of the configuration file.  The
    config file is broken down and fed to the utilities through a special,
    shortened version of the config file that only recognizes
    customizations.  For each library, one of these customization files is
    created with the customizations that are specific to that library.

    At this time, the handlers are specific to ANSI C, C++ source code.
    This isn't a necessity, but its just the way it is right now.
    Similarily, the output is geared specifically to HTML, although other
    formats (Microsoft Help for instance) might be possible in the future.
    Rest assured I've got these little tidbits in mind for future work...

------------------------------------------------------------------------*/

#ifndef cocoon_cobweb_h
#define cocoon_cobweb_h

#include    "util.h"



//=========================================================================
//
//  Classes
//
//=========================================================================


/*------------------------------------------------------------------------
CLASS
    Xref

    Manages class and file cross-reference data.

KEYWORDS
    cross_reference, file

DESCRIPTION
    Class for handling cross-linking and cross-referencing data files.
    Keeps pointers to many of the global, temporary files, and other data
    relating to connecting everything up into a web.

------------------------------------------------------------------------*/
class Xref {
public:
	///////////////////////
	//
	//  Constructor
	//
	//  [in] indexname  Name of the cross-class index file
	//  [in] keyname    Name of the keyword cross-reference data file
	//  [in] familyname Name of inheritance family data file
	//  [in] filename   Name of the file being processed.
	//  [in] libname    Name of the library the file is in.
	//  [in] xrefurl    URL to the cross-reference page.  This should
	//		    <strong>not</strong> include the filename
	//		    extension!
	//  [in] custom	    Customization data.
	//
	//  If the input files already exist, they are appended to.
	//
    Xref( char* indexname, char* keyname, char* familyname, char* filename,
	  char* libname, char* xrefurl, Customize& custom );


	///////////////////////
	//
	//  Returns the stream use for the cross-class index file.
	//
    ofstream&
    Index()
	{ return _index; }


	///////////////////////
	//
	//  Returns the stream for the keyword cross-reference data file.
	//
    ofstream&
    Keywords()
	{ return _keywords; }


	///////////////////////
	//
	//  Returns the stream for the inheritance-family data file.
	//
    ofstream&
    Family()
	{ return _family; }


	///////////////////////
	//
	//  Returns the stream for the Lisp data dump.
	//
    ofstream&
    LispDump()
	{ return _lispdump; }


	///////////////////////
	//
	//  Returns the name of the file being processed.
	//
    char*
    FileName()
	{ return _filename; }


	///////////////////////
	//
	//  Returns the name of the library being processed.
	//
    char*
    LibName()
	{ return _libname; }


	///////////////////////
	//
	//  Returns the URL path to the xref directory (where the keyweb
	//  file is).
	//
    char*
    XrefURL()
	{ return _xrefurl; }


	///////////////////////
	//
	//  Returns an internal pointer to the count of unnamed enums
	//  found in the current library.
	//
    int*
    EnumCount()
	{ return &_enumcount; }


private:
    ofstream	_index;
    ofstream	_keywords;
    ofstream	_family;
    ofstream	_lispdump;
    StringBuff  _filename;
    StringBuff  _libname;
    StringBuff  _xrefurl;
    int		_enumcount;

}; // Xref



/*------------------------------------------------------------------------
CLASS
	ContextTracker

	Tracks the surrounding code context of #defines and comments.

KEYWORDS
	Comments

DESCRIPTION

	This helper class is used by meta-contexts such as HeaderFile and
	ClassDecl to help them track the surrounding context of the code.
	The class will process each line of source code and track whether,
	at the end of it, there is an open <b>/*</b>-style comment. It will
	also say whether or not the next line should be done based on
	whether or not it is the last line of a #define.

APOLOGIA

	This is a kludge. An obvious, painful kludge. I apologize. The correct
	solution will be designed into the next major rewrite of Cocoon.

------------------------------------------------------------------------*/
class   ContextTracker
{
public:
	////////////////////////
	//
	//  Constructor.  Initializes everything.
	//
    ContextTracker() :
	_incomment( FALSE ), _skipnext( FALSE ), _cppstyle( FALSE )
	{ }


	////////////////////////
	//
	//  Destructor.
	//
    ~ContextTracker()
	{ _lastcomment.Clear(); }


	////////////////////////
	//
	//  Processes the given line to track the occurrence of
	//  comments and #defines.  Additionally, it will track
	//  the contents of the last commenting block.
	//
    void
    CheckLine( StringBuff& buff );


	////////////////////////
	//
	//  Resets the state of the checker. Clients should call
	//  this function after some event occurs which assures that
	//  any open comments will be closed.
	//
    void
    Reset()
	{ _incomment = FALSE; _skipnext = FALSE; _cppstyle = FALSE;
	  _lastcomment.Clear(); }


	////////////////////////
	//
	//  Returns TRUE if, at the end of the last line sent for
	//  processing, we are in the midst of an open <b>/*</b>-style
	//  comment.
	//
    int
    inComment() const
	{ return _incomment; }


	////////////////////////
	//
	//  Returns TRUE if the next line of source code should be
	//  skipped because it is the last line of a #define.
	//
    int
    SkipNext() const
	{ return _skipnext; }


	////////////////////////
	//
	//  Returns the last comment block that was read in, including
	//  all comment markers.
	//
    void
    LastComment( StringBuff& comment )
	{ comment.Clear(); comment.Append( _lastcomment ); }


private:
    BitField	_incomment : 1; // at the end of the line, are we in
				// a comment?
    BitField	_skipnext  : 1; // should we skip the next source line?
    BitField	_cppstyle  : 1; // TRUE if the comment block is new-style
    StringBuff  _lastcomment;   // the last comment encountered

}; // ContextTracker


/*------------------------------------------------------------------------
CLASS
    Handler

    Base class for all file handling, sentinel recognition, etc.

KEYWORDS
    Handler

DESCRIPTION
    Base class definitions for section handling.  The processing of a
    section is broken into 4 main steps.
	<ul>
	<li>  Search for some sentinel string that indicates
	      the beginning or end of the section.  This will vary for
	      each section.  Each subclass must provide an instance
	      of the static function FindSentinel().
	<li>  Perform some processing upon entry into the section.
	      This is done in the constructor for the base class.
	<li>  Process a line of text from the source code.  During this
	      processing, the section can also look for some exit
	      criteria.  For instance, when processing a member function
	      definition, the ';' or '{' character that signals the
	      end of the function signature can be looked for, and an
	      exit from the section can be forced.  Child classes must
	      override the function ProcessLine() for this purpose.
	<li>  Perform some processing when the section is exitted.
	      The normal reason for exitting a section is that some other
	      section's sentinel has been found and it is being activated.
	      This is done in the child class' destructor.
	</ul>

    Handler's can be nested.  The set of possible handlers at any given
    time is dependent on what handler is active.  For instance, the
    handler for member function definitions can only be made active
    by the handler for processing a class declaration.

------------------------------------------------------------------------*/
class Handler
{
public:
	///////////////////////
	//
	//  Search the input string for the sentinel for the section.
	//
	//  [in] buff
	//	Buffer of source code.
	//
	//  Returns TRUE if the sentinel for the section is found,
	//  FALSE otherwise.
	//
	//  One of these static functions <i>must</i> be defined for
	//  each child class of Handler.  Child classes can add
	//  other input arguments.  (This function is shown here purely
	//  as an example.)
	//
    static int
    FindSentinel( StringBuff& buff, Customize& custom )
	{ UNUSED( buff ); UNUSED( custom ); return FALSE; }


	///////////////////////
	//
	//  The ctor should perform whatever processing is needed when
	//  entering the section.  This may include opening files,
	//  setting up state information or whatever.  Child classes
	//  should use whatever input arguments it wants any client
	//  handler to provide.
	//
	//  [in] xref
	//	Cross-reference file data.
	//  [in] custom
	//	Data for user customization of output.
	//
	//  For convenience, the Handler base class will track the
	//  cross-reference and customization data.
	//
    Handler( Xref& xref, Customize& custom ) :
	_xref( xref ), _custom( custom ), _subhandler( NULL )
	{ }


	///////////////////////
	//
	//  Perform whatever processing is needed when the section
	//  is exitted.  Should close any files that were open,
	//  or write any bracketing information such as list termination
	//  tags.
	//
    virtual
    ~Handler();


	///////////////////////
	//
	//  This function is the main control algorithm for coordinating
	//  the nesting and creation of handlers.  This function should
	//  be called for each line of source text that is read while
	//  the handler is active.
	//
	//  It first calls TerminatorFound() to see if the handler should
	//  exit.  It then calls NewHandler() to see if a new subhandler
	//  should be created.  If there is a subhandler, the Control()
	//  function for it is called, otherwise the ProcessLine()
	//  function for the handler is called.
	//
	//  [in] buff
	//	Buffer of source code.
	//  [out] surrendercontrol
	//	Set to TRUE if the controlling handler should
	//	immediately exit this section.
	//
    void
    Control( StringBuff& buff, int& surrendercontrol );


	///////////////////////
	//
	//  Search for any string that indicates the end of this handler.
	//  Not all child classes will need this function.  The
	//  ProcessLine() function also provides a way for surrending
	//  control.
	//
	//  [in] buff
	//	Buffer of source code.
	//
    virtual int
    TerminatorFound( StringBuff& buff )
	{ UNUSED( buff ); return FALSE; }


	///////////////////////
	//
	//  Search for the sentinel strings of any other handlers
	//  that make sense within the current context.  If found,
	//  create an instance of the handler and pass it back.
	//
	//  [in] buff
	//	Buffer of source code.
	//
    virtual Handler*
    NewHandler( StringBuff& buff )
	{ UNUSED( buff ); return NULL; }


	///////////////////////
	//
	//  This is checked immediately after a new handler is created
	//  and made active. Sections which may consist of a single
	//  line may need to return TRUE for this to immediately
	//  complete their processing at the same line where it started.
	//
    virtual int
    SurrenderImmediately()
	{ return FALSE; }


	///////////////////////
	//
	//  Perform whatever processing is needed for source code lines
	//  within this section.
	//
	//  [in] buff
	//	Buffer of source code.
	//  [out] surrendercontrol
	//	Set to TRUE if the controlling handler should
	//	immediately exit this section.
	//
    virtual void
    ProcessLine( StringBuff& buff, int& surrendercontrol )
	{ UNUSED( buff ); surrendercontrol = FALSE; }


	///////////////////////
	//
	//  Returns the name of the section.  This is used for
	//  outputting diagnostic messages when in debug mode.
	//
    virtual char*
    SectionName()
	{ return "unknown"; }


protected:

	///////////////////////
	//
	//  Preempts any currently active subhandler, letting it go
	//  through its cleanup work.  Should be the first thing
	//  called in the destructor of every class.
	//
    void
    Preempt();


	///////////////////////
	//
	//  Returns TRUE if there is currently a subhandler for the
	//  object.
	//
    int
    haveSubhandler()
	{ return _subhandler != NULL; }


	///////////////////////
	//
	//  Returns the name of the current subhandler. Do not
	//  call this function unless haveSubhandler() returns
	//  TRUE.
	//
    char*
    SubhandlerName()
	{ return _subhandler->SectionName(); }


	///////////////////////
	//
	//  Returns the cross-referencing data file information.
	//
    Xref&
    CrossRef()
	{ return _xref; }


	///////////////////////
	//
	//  Returns the customization data.
	//
    Customize&
    Custom()
	{ return _custom; }


private:
				// current handler for sub-context
    Handler*	_subhandler;

				    // Cross-reference and customization data.
    Xref&   _xref;
    Customize&  _custom;

}; // Handler


/*------------------------------------------------------------------------
CLASS
    Census

    Accumulates summary statistics regarding members defined in
    libraries or classes.

KEYWORDS
    Statistics, member, summary, metrics

DESCRIPTION
    This class is used to accumulate statistical data about the members
    defined in a class or library. These statistical data are used
    in generating the pages for the classes and libraries themselves,
    but also in producing the metrics file for each class and library.
    This file is added to during the processing of inheritance
    information. Finally it is processed into a partial file that
    is recombined with the class and library page bodies.

------------------------------------------------------------------------*/
class   Census
{
public:
	///////////////////////
	//
	//  Constructor.
	//
	//  [in] isglobal   If TRUE, members are defined at global
	//		    scope as part of a library, rather than as
	//		    part of a class.
	//
    Census( const int isglobal );


	///////////////////////
	//
	//  Destructor.
	//
    ~Census()
	{ /* nothing special */ };


	///////////////////////
	//
	//  Sets the name of the class or library.
	//
	//  [in] classname  The name of the class or library being measured.
	//
    void
    SetName( char* name )
	{ _name.Clear(); _name.Append( name ); }


					// GROUP: Query functions
	///////////////////////
	//
	//  Returns the name of the class or library being measured.
	//
    const char*
    Name()
	{ return &_name[0]; }


	///////////////////////
	//
	//  Returns TRUE if the artifact being measured is a library
	//  rather than a class.
	//
    int
    isGlobal() const
	{ return _isglobal; }


	///////////////////////
	//
	//  Returns TRUE if all the member functions within the class
	//  have been defined as inline.
	//
    int
    AllInline() const
	{ return _allinline; }


	///////////////////////
	//
	//  Returns TRUE if any of the member functions within the class
	//  are pure virtual, indicating that the class is abstract.
	//
    int
    isAbstract() const
	{ return _numabstract > 0; }


	///////////////////////
	//
	//  Returns the index number for the next unnamed enumeration
	//  type encountered.
	//
    int
    NextUnnamedEnum() const
	{ return _enumcount; }

				    // GROUP: Calls for member datums

	///////////////////////
	//
	//  Clients should call this function when a member function
	//  declaration is successfully recognized. The statistics
	//  will be accumulated into the census data.
	//
    void
    Function( const int isinline, const int isabstract );


	///////////////////////
	//
	//  Clients should call this function when a type
	//  definition or data member declaration is successfully recognized.
	//  The statistics will be accumulated into the census data.
	//
    void
    Data( const int isunnamedenum );


private:

    StringBuff  _name;		// the name of the class or library
    int		_numabstract;   // number of abstract member functions
				// (applies only to classes)
    int		_enumcount;	// identifier to use for next unnamed enum.
    BitField	_isglobal  : 1; // if TRUE, members are defined at global scope
				// (part of a library rather than a class)
    BitField	_allinline : 1; // TRUE if all members are defined inline


	///////////////////////
	//
	//  Disabled.
    Census( const Census& copy );
    Census&
    operator=( const Census& copy );

}; // Census



/*------------------------------------------------------------------------
CLASS
    HeaderFile

    Handler for C++ header files.

KEYWORDS
    Handler, header, file

DESCRIPTION
    Handler for a C++ header file.  The outermost context for processing
    a header file is a library.
    Since this is a highest-level handler, there is no FindSentinel
    function.

ALLOWED SUBHANDLERS
    ClassDecl, Member, LibDoc

------------------------------------------------------------------------*/
class HeaderFile : public Handler
{
public:
	///////////////////////
	//
	//  Constructor.
	//
	//  [in] xref
	//	Cross-reference file data.
	//  [in] custom
	//	Data for user customization of output.
	//  [in] libname
	//	Name of the library that the source code header
	//	file is part of.
	//
    HeaderFile( Xref& xref, Customize& custom, char* libname );


	///////////////////////
	//
	//  Destructor.
	//
    virtual
    ~HeaderFile();


	///////////////////////
	//
	//  <i>REDEFINED FROM PARENT</i>.
	//
    virtual Handler*
    NewHandler( StringBuff& buff );


	///////////////////////
	//
	//  Returns the name of the section.  This is used for
	//  outputting diagnostic messages when in debug mode.
	//
    virtual char*
    SectionName()
	{ return "HeaderFile"; }


	///////////////////////
	//
	//  Returns the name of the library.
	//
    char*
    LibName()
	{ return _libname; }


private:
    StringBuff	    _libname;
    ofstream	    _libmain;
    ofstream	    _liblink;
    ofstream	    _libclass;
    ofstream	    _libfunc;
    ofstream	    _libdata;
    ofstream	    _libdocindex;
    ofstream	    _indexframefile;
    ofstream	    _memberframefile;
    int		    _membernum;
    int		    _classdeclactive;
    ContextTracker  _tracker;
    Census	    _census;

}; // HeaderFile


/*------------------------------------------------------------------------
CLASS
    ClassDecl

    Handler for a class declaration.

KEYWORDS
    Handler, class, scope

DESCRIPTION
    Process class declarations, creating a corresponding html page and
    updating the global link files.  Immediately creates a DocHeader
    subhandler to process the documentation header.

ALLOWED SUBHANDLERS
    DocHeader, IgnoreHeading, Keyword, Member, ClassDecl

------------------------------------------------------------------------*/
class ClassDecl: public Handler
{
public:
	///////////////////////
	//
	//  <i>REDEFINED FROM PARENT</i>.
	//
    static int
    FindSentinel( StringBuff& buff, Customize& custom );


	///////////////////////
	//
	//  Constructor.
	//
	//  [in] xref
	//	Cross-reference file data.
	//  [in] custom
	//	Data for user customization of output.
	//  [in] libclass
	//	Class summary listing for library.
	//  [in] sentinelline
	//	The full line of text where the sentinel string was
	//	found.
	//  [in] prevcomment
	//	Any previously occurring comment string. Used with
	//	non-sentinel declarations.
	//  [in] nestedin
	//	If the class is nested within another, provides
	//	the name of the containing class, otherwise NULL.
	//
    ClassDecl( Xref& xref, Customize& custom, ofstream& libclass,
	       StringBuff& sentinelline, StringBuff& prevcomment,
	       char* nestedin = NULL);


	///////////////////////
	//
	//  Destructor.
	//
    virtual
    ~ClassDecl();


	///////////////////////
	//
	//  <i>REDEFINED FROM PARENT</i>.
	//
    virtual int
    TerminatorFound( StringBuff& buff );


	///////////////////////
	//
	//  <i>REDEFINED FROM PARENT</i>.
	//
    virtual Handler*
    NewHandler( StringBuff& buff );


	///////////////////////
	//
	//  <i>REDEFINED FROM PARENT</i>.
	//
    virtual void
    ProcessLine( StringBuff& buff, int& surrendercontrol );


	///////////////////////
	//
	//  Returns the name of the section.  This is used for
	//  outputting diagnostic messages when in debug mode.
	//
    virtual char*
    SectionName()
	{ return "ClassDecl"; }


	///////////////////////
	//
	//  Returns the name of the class being processed.
	//
    char*
    Name()
	{ return _classname; }


private:
    StringBuff	    _classname;
    StringBuff	    _nestedin;
    StringBuff	    _logoimage;
    StringBuff	    _prevheading;
    StringBuff	    _implName;
    BitField	    _inbody	  : 1;
    BitField	    _inpublic	  : 1;
    BitField	    _inprotected  : 1;
    BitField	    _inprivate	  : 1;
    BitField	    _getinh	  : 1;
    BitField	    _startdd	  : 1;
    BitField	    _libdocdone   : 1;
    BitField	    _indenting	  : 1;
    BitField	    _hasparents   : 1;
    BitField	    _hasdoc	  : 1;
    ContextTracker  _tracker;
    ofstream	    _mainfile;
    ofstream	    _summaryfile;
    ofstream	    _pubfile;
    ofstream	    _protfile;
    ofstream	    _privfile;
    ofstream	    _linkfile;
    ofstream	    _indfile;
    ofstream	    _inhfile;
    ofstream&	    _libclass;
    ofstream        _indexframefile;
    ofstream	    _memberframefile;
    ofstream	    _framefile;
    int		    _membercount;
    Census	    _census;

    void    InitFiles();
    void    GetInheritance( StringBuff& buff );
    static
    int	    isBodySentinel( StringBuff& buff, Customize& custom );

}; // ClassDecl



/*------------------------------------------------------------------------
CLASS
    DocHeader

    Handler for a documentation heading section.

KEYWORDS
    Handler, header, documentation

DESCRIPTION
    Processes a documentation header.  The header text is output to
    a main web page, and a quick index file is updated.  Empty lines
    are interpreted as paragraph breaks and extra &ltp&gt directives
    output.

    Text within the 'preformatted' HTML tags ( &ltpre&gt ) is
    output as is.

ALLOWED SUBHANDLERS

    CodeExample

------------------------------------------------------------------------*/
class DocHeader : public Handler
{
public:
	///////////////////////
	//
	//  <i>REDEFINED FROM PARENT</i>.
	//
    static int
    FindSentinel( StringBuff& buff, Customize& custom );


	///////////////////////
	//
	//  Constructor
	//
	//  [in] xref
	//	Cross-reference file data.
	//  [in] custom
	//	Data for user customization of output.
	//  [in] classname
	//	Name of class being processed.
	//  [in] buff
	//	Line from file.
	//  [in] maintext
	//	File for main html text of documentation.
	//  [in] quickindex
	//	File for quick index entries on main page.
	//  [in] frameindex
	//	File for quick index entries in the frame-based layout.
	//
	//
    DocHeader( Xref& xref, Customize& custom, char* classname, char* buff,
	       ofstream& maintext, ofstream& quickindex, ofstream& frameindex );


	///////////////////////
	//
	//  Destructor.
	//
    virtual
    ~DocHeader();


	///////////////////////
	//
	//  <i>REDEFINED FROM PARENT</i>.
	//
    virtual Handler*
    NewHandler( StringBuff& buff );


	///////////////////////
	//
	//  <i>REDEFINED FROM PARENT</i>.
	//
    virtual void
    ProcessLine( StringBuff& buff, int& surrendercontrol );


	///////////////////////
	//
	//  Returns the name of the section.  This is used for
	//  outputting diagnostic messages when in debug mode.
	//
    virtual char*
    SectionName()
	{ return "DocHeader"; }


private:
    StringBuff  _headername;
    StringBuff  _classname;
    BitField	_inpreformatted : 1;
    BitField	_initialized	: 1;
    ofstream&   _maintext;
    ofstream&   _quickindex;
    ofstream&   _frameindex;

}; // DocHeader


/*------------------------------------------------------------------------
CLASS
    CodeExample

    Handler for an example provided in the documentation.

KEYWORDS
    Handler, documentation, example

DESCRIPTION
    When examples are given in the documentation, it is assumed that
    they are preformatted code samples.  A special image to tag the
    sample is displayed and the text is output preformatted.  The
    example is begun when the string "EXAMPLE" is found as the first string
    of the line, and terminated when the first string on the line is "END".

    Here is an example of how the example section looks:

    EXAMPLE
	//
	//  Here's an example...
	//
	int i;  // looping index

	Foo();
	for ( i = 0; i < 20; i++ )
	    cout << Bar( i ) << endl;
	cout << "Done with example." << endl;
    END

    (Remember that the "EXAMPLE" keyword can't start in the first column
    though, or it will be mistaken for a documentation heading.)


------------------------------------------------------------------------*/
class CodeExample : public Handler
{
public:
	///////////////////////
	//
	//  <i>REDEFINED FROM PARENT</i>.
	//
    static int
    FindSentinel( StringBuff& buff, Customize& custom );


	///////////////////////
	//
	//  Constructor.
	//
	//  [in] xref
	//	Cross-reference file data.
	//  [in] custom
	//	Data for user customization of output.
	//  [in] maintext
	//	File for the text of the example.
	//  [in] classname
	//	Name of the class being processed.
	//
    CodeExample( Xref& xref, Customize& custom, ofstream& maintext,
		 StringBuff& classname );


	///////////////////////
	//
	//  Destructor
	//
    ~CodeExample();


	///////////////////////
	//
	//  <i>REDEFINED FROM PARENT</i>.
	//
    virtual void
    ProcessLine( StringBuff& buff, int& surrendercontrol );


	///////////////////////
	//
	//  <i>REDEFINED FROM PARENT</i>.
	//
    virtual int
    TerminatorFound( StringBuff& buff );


	///////////////////////
	//
	//  Returns the name of the section.  This is used for
	//  outputting diagnostic messages when in debug mode.
	//
    virtual char*
    SectionName()
	{ return "CodeExample"; }

private:
    ofstream&   _maintext;
    StringBuff  _classname;


}; // CodeExample


/*------------------------------------------------------------------------
CLASS
    Member

    Handler for data members, member functions, global functions,
    typedefs, enums, etc.

KEYWORDS
    Handler, member, function, variable, typedef, enum, class, library

DESCRIPTION
    Handler for members of libraries and classes.  This should handle
    functions, typedefs, variables, and enums.  This includes handling of
    member arguments or sub-fields, establishing the cross-linking data for
    the members, etc.

    This handler is too complicated right now, and needs to be
    redesigned...

------------------------------------------------------------------------*/
class Member: public Handler
{
public:
	///////////////////////
	//
	//  <i>REDEFINED FROM PARENT</i>.
	//
    static int
    FindSentinel( StringBuff& buff, Customize& custom );


	///////////////////////
	//
	//  Constructor.
	//
	//  [in] xref
	//	Cross-reference file data.
	//  [in] custom
	//	Data for user customization of output.
	//  [in] body
	//	File for body of member documentation.
	//  [in] funcaag
	//	File for at-a-glance listing of functions.
	//  [in] dataaag
	//	File for at-a-glance listing of data.
	//  [in] index
	//	Index file for reanchoring the aag index.
	//  [in] link
	//	File with substitutions for automatic linking within
	//	the page.
	//  [in] memberkey
	//	Key string used to uniquely identify this member
	//	within a page.
	//  [i/o] census
	//	Statistical data summarizing the class or library
	//	containing the member.
	//  [in] sentinelline
	//	The full line of text where the sentinel string was
	//	found.
	//  [in] prevcomment
	//	Any previously occurring comment string. Used with
	//	non-sentinel declarations.
	//
    Member( Xref& xref, Customize& custom, ofstream& body, ofstream& funcaag,
	    ofstream& dataaag, ofstream& index, ofstream& link,
	    char* memberkey, Census& census, StringBuff& sentinelline,
	    StringBuff& prevcomment );


	///////////////////////
	//
	//  Destructor.
	//
    virtual
    ~Member();


	///////////////////////
	//
	//  <i>REDEFINED FROM PARENT</i>.
	//
    virtual int
    TerminatorFound( StringBuff& buff );


	///////////////////////
	//
	//  <i>REDEFINED FROM PARENT</i>.
	//
    virtual void
    ProcessLine( StringBuff& buff, int& surrendercontrol );


	///////////////////////
	//
	//  <i>REDEFINED FROM PARENT</i>.
	//
    virtual int
    SurrenderImmediately()
	{ return _forcesurrender; }


	///////////////////////
	//
	//  <i>REDEFINED FROM PARENT</i>.
	//
    virtual char*
    SectionName()
	{ return "Member"; }


private:

    StringBuff  _signature;
    StringBuff  _fulldeclaration;
    BitField	_trimdoubleslash : 1;
    BitField	_doingsig	 : 1;
    BitField	_doingargs	 : 1;
    BitField	_outasdd	 : 1;
    BitField	_inlinedef	 : 1;
    BitField	_isdata		 : 1;
    BitField	_isfunction	 : 1;
    BitField	_ispurevirtual   : 1;
    BitField	_inpreformatted  : 1;
    BitField	_hadsentinel	 : 1;
    BitField	_unnamedenum	 : 1;
    int		_bodynest;
    int		_forcesurrender;
    ofstream	_tmpbody;
    ofstream&   _body;
    ofstream&   _funcaag;
    ofstream&   _dataaag;
    ofstream&   _ind;
    ofstream&   _link;
    StringBuff  _memberkey;
    StringBuff  _groupcomment;
    Census*	_census;

    void
    TrimThrowList( StringBuff& buff );

    void
    InsertLinks( char* funcname, char* fullsig );

    int
    FindMemberName( StringBuff& buff, int& startname, int& endname,
		    int& nameclause, int& argclause );

    void
    TrimBody( StringBuff& buff, int isfunction );

}; // Member


/*------------------------------------------------------------------------
CLASS
    LibDoc

    Handler for a library documentation header block.

KEYWORDS
    Handler, library

DESCRIPTION
    Handler for library documentation header.  Works very similarily to
    the ClassDecl, except that there is no class body to process.

ALLOWED SUBHANDLERS
    Member, DocHeader

------------------------------------------------------------------------*/
class LibDoc : public Handler
{
public:
	///////////////////////
	//
	//  <i>REDEFINED FROM PARENT</i>.
	//
    static int
    FindSentinel( StringBuff& buff, Customize& custom );


	///////////////////////
	//
	//  Constructor.
	//
	//  [in] xref
	//	Cross-reference file data.
	//  [in] custom
	//	Data for user customization of output.
	//  [in] libname
	//	Name of the library -- verify against name in
	//	comment block.
	//  [in] maintext
	//	File for main documentation.
	//  [in] quickindex
	//	File with the documentation quick index list.
	//  [in] frameindex
	//	File with the documentation quick index list for frames
	//	layout.
	//
    LibDoc( Xref& xref, Customize& custom, char* libname,
	    ofstream& maintext, ofstream& quickindex, ofstream& frameindex );


	///////////////////////
	//
	//  Destructor.
	//
    virtual
    ~LibDoc();


	///////////////////////
	//
	//  <i>REDEFINED FROM PARENT</i>.
	//
    virtual int
    TerminatorFound( StringBuff& buff );


	///////////////////////
	//
	//  <i>REDEFINED FROM PARENT</i>.
	//
    virtual Handler*
    NewHandler( StringBuff& buff );


	///////////////////////
	//
	//  <i>REDEFINED FROM PARENT</i>.
	//
    virtual void
    ProcessLine( StringBuff& buff, int& surrendercontrol );


	///////////////////////
	//
	//  Returns the name of the section.  This is used for
	//  outputting diagnostic messages when in debug mode.
	//
    virtual char*
    SectionName()
	{ return "LibDoc"; }


private:
    StringBuff  _libname;
    StringBuff  _prevheading;
    BitField	_namechecked : 1;
    BitField	_indenting   : 1;
    ofstream&   _maintext;
    ofstream&   _quickindex;
    ofstream&   _frameindex;

}; // LibDoc


/*------------------------------------------------------------------------
CLASS
    IgnoreHeading

    Handler for a documentation headers to ignore.

KEYWORDS
    Handler, documentation, header, log

DESCRIPTION
    Dump the contents of the section to the bit-bucket.  By default,
    all sections named LOG are ignored.  Others can be added via
    customization options.  This can be useful in weeding out proprietary
    or otherwise sensitive data.

------------------------------------------------------------------------*/
class IgnoreHeading : public Handler
{
public:
	///////////////////////
	//
	//  <i>REDEFINED FROM PARENT</i>.
	//
    static int
    FindSentinel( StringBuff& buff, Customize& custom );


	///////////////////////
	//
	//  Constructor.
	//
	//  [in] xref
	//	Cross-reference file data.
	//  [in] custom
	//	Data for user customization of output.
	//
    IgnoreHeading( Xref& xref, Customize& custom ) :
	Handler( xref, custom )
	{ }


	///////////////////////
	//
	//  <i>REDEFINED FROM PARENT</i>.
	//
    virtual int
    TerminatorFound( StringBuff& buff );


	///////////////////////
	//
	//  Returns the name of the section.  This is used for
	//  outputting diagnostic messages when in debug mode.
	//
    virtual char*
    SectionName()
	{ return "IgnoreHeading"; }


}; // IgnoreHeading



/*------------------------------------------------------------------------
CLASS
    Keyword

    Handler for the KEYWORD documentation header.

KEYWORDS
    Handler, documentation, header, keyword

DESCRIPTION
    Handles a KEYWORD documentation header.  Parses out individual
    keywords and updates the global cross-reference file.  This file
    is later processed to create the keyword cross-reference page.

------------------------------------------------------------------------*/
class Keyword: public Handler
{
public:
	///////////////////////
	//
	//  <i>REDEFINED FROM PARENT</i>.
	//
    static int
    FindSentinel( StringBuff& buff, Customize& custom );


	///////////////////////
	//
	//  Constructor.
	//
	//  [in] xref
	//	Cross-reference file data.
	//  [in] custom
	//	Data for user customization of output.
	//
    Keyword( Xref& xref, Customize& custom, char* classname ) :
	Handler( xref, custom )
	{ _classname.Append( classname ); }


	///////////////////////
	//
	//  <i>REDEFINED FROM PARENT</i>.
	//
    virtual void
    ProcessLine( StringBuff& buff, int& surrendercontrol );


	///////////////////////
	//
	//  <i>REDEFINED FROM PARENT</i>.
	//
    virtual int
    TerminatorFound( StringBuff& buff );


	///////////////////////
	//
	//  <i>REDEFINED FROM PARENT</i>.
	//
    virtual char*
    SectionName()
	{ return "Keyword"; }


private:
    StringBuff  _classname;

}; // Keyword



#endif

