
/*------------------------------------------------------------------------
				  COPYRIGHT
--------------------------------------------------------------------------
	  Copyright (C) 1995-2000
	  Jeff Kotula
	  All Rights Reserved.
--------------------------------------------------------------------------
				   FILE LOG
--------------------------------------------------------------------------

    Include file for the cocoon utility classes.

------------------------------------------------------------------------*/

#ifndef cocoon_util_h
#define cocoon_util_h

#include    <iostream.h>
#include    <fstream.h>
#include    <string.h>
#include    <stdio.h>
#include    <ctype.h>
#include    <assert.h>


//=========================================================================
//
//  Define some useful macros and constants
//
//=========================================================================

#ifdef  NOEXCEPTIONS
#include    <stdlib.h>
#define THROW(str)  exit(-1);
#define CATCH(str)  str="Exiting"; if ( FALSE )
#define TRY
#else
#define THROW   throw
#define CATCH   catch
#define TRY try
#endif

#define TRUE	1
#define FALSE   0

#define UNUSED(arg) ((void)&arg)


#define VERSION	     "Cocoon, Version 4.1"


typedef unsigned int	BitField;


				// include file extensions
#define INCFILEEXT  ".h|.H|.hxx|.hpp|.hh"

				// source file extensions
#define SRCFILEEXT  ".cxx|.cc|.C|.CC|.cpp|.c"


//=========================================================================
//
//  Declarations of global functions.
//
//=========================================================================

class   Customize;


	/////////////////////////////////////////
	//
	//  Output the signature line for an html page.
	//
	//  [in] str
	//	Stream to write signature to.
	//  [in] custom
	//	Customization data.
	//
extern void Signature( ofstream& str, Customize& custom );


	/////////////////////////////////////////
	//
	//  Returns a pointer to the second word in the given string.
	//
	//  [in] line   String to search in
	//
extern char*	GetSecondWord( char* line );


	/////////////////////////////////////////
	//
	//  Escapes the appropriate characters for output to a
	//  substitution file.
	//
	//  [in] str	Stream to output escaped string to
	//  [in] buff   String to check for escape-able characters
	//
extern void SubEscape( ofstream& str, char* buff );




//=========================================================================
//
//  Classes
//
//=========================================================================

/*------------------------------------------------------------------------
CLASS
    StringBuff

    Simple utility class for string handling.

KEYWORDS
    String, buffer

DESCRIPTION
    This is a simple little class used for the string handling needs
    of the cocoon utilities.  It is a little bit of a hodge-podge,
    but also contains some nice convenience functions.

------------------------------------------------------------------------*/
class StringBuff {
public:
	///////////////////////
	//
	//  Constructor.
	//
    StringBuff();

	///////////////////////
	//
	//  Copy constructor.
	//
    StringBuff( const StringBuff& value );


	///////////////////////
	//
	//  Destructor.
	//
    ~StringBuff();

			//-----------------------------------
			// GROUP:   Access Functions

	///////////////////////
	//
	//  Returns the index of the first non-whitespace
	//  character.  If the whole thing is whitspace, will
	//  return an index to the terminating NULL character.
	//
    int
    NonWhite();


	///////////////////////
	//
	//  Indexing operator.  Basically unprotected.  Bad
	//  indices will cause crashes.  Client should not
	//  try to do <strong>any</strong> memory management
	//  with it.
	//
	//  [in] index	    The index into the string.
	//
    char&
    operator[]( int index )
	{ _firstnonblank = -1; return _str[index]; }


	///////////////////////
	//
	//  Conversion operator into a standard (char *).  Gives
	//  easy access to standard char* functions.
	//
    operator char*()
	{ return _str; }


	///////////////////////
	//
	//  Conversion operator into a standard (const char *).  Gives
	//  easy access to standard char* functions.
	//
    operator const char*() const
	{ return _str; }


	///////////////////////
	//
	//  Returns the current capacity (number of characters)
	//  of the string.
	//
    int
    Capacity()
	{ return _capacity-1; }

			//-----------------------------------
			// GROUP:   Basic Manipulation

	///////////////////////
	//
	//  Clear out the string.
	//
    void
    Clear()
	{ _str[0] = '\0'; _firstnonblank = -1; }


	///////////////////////
	//
	//  Append a character to this string.
	//
	//  [in] ch	Character to append.
	//
    void
    Append( char ch )
	{ char s[2]; s[0] = ch; s[1] = '\0'; Append(s); }


	///////////////////////
	//
	//  Append another string to this one.
	//
	//  [in] otherstring	Other string to append -- duh!
	//
    void
    Append( const char* otherstring );


	///////////////////////
	//
	//  Append another string to this one up to maxlength.
	//
	//  [in] otherstring	Other string to append -- duh!
	//
	//  [in] maxlength  Maximum number of characters to append.
	//
    void
    Append( char* otherstring, int maxlength );


	///////////////////////
	//
	//  Gets a complete line from the given file, growing
	//  the string as necessary.  Returns TRUE if line was
	//  retrieved successfully.  FALSE if end-of-file is
	//  encountered.
	//
	//  [in] infile	    The file to read the string from.
	//  [in] allowcontinue  If TRUE, the line reading will use the
	//	    line continuation syntax (trailing
	//	    backslash) used in configuration and
	//	    customization files.  Also causes '#'
	//	    comments to be trimmed out...
	//  [in] eightbitclean  If TRUE, keep it eight-bit clean
	//
    int
    GetLine( ifstream& infile, int allowcontinue = FALSE,
		     int eightbitclean = TRUE );

			//-----------------------------------
			// GROUP:   Convenience Functions

	///////////////////////
	//
	//  If an identifier was found at or after position "start", returns TRUE
	//  with arguments start and length set appropriately.  Else returns FALSE.
	//  The contents of comments are ignored but not changed.
	//
	//  If a non-zero third argument is present, it will also ignore identifiers
	//  enclosed by (potentially nested) angle brackets, e.g. < ... < ... > ... >
	//  but will not modify those characters.  This is useful for ignoring the
	//  arguments to a template, e.g. template<class T> class foo ...
	//
	//  AUTHOR
	//	Eric B. Anderson
	//
    int
    FoundIdentifier( int& start, int& length, int ignoreAngleBracketed = FALSE);


	///////////////////////
	//
	//  Trim out any <code>//</code> style comments from the buffer.
	//
    void
    TrimComments();


	///////////////////////
	//
	//  Trim out any old style <code>/* */</code> comments from the
	//  buffer.  Just replaces them with whitespace.  Use TrimWhite()
	//  to get rid of that.
	//
    void
    TrimOldComments();


	///////////////////////
	//
	//  Trim out any leading, trailing, or redundant
	//  white-space.
	//
    void
    TrimWhite();


	///////////////////////
	//
	//  Removes all comment markers and replaces empty
	//  lines with the &ltp&gt tag.
	//
    void
    MakeIntoDoc( Customize& custom );


	///////////////////////
	//
	//  Make the string HTML-safe, replacing meta-characters where
	//  needed.
	//
    void
    HTMLSafe();


	///////////////////////
	//
	//  Make the string URL-safe, replacing illegal characters, etc.
	//
    void
    URLSafe();


	///////////////////////
	//
	//  Strip out all HTML elements.  Also normalizes whitespace.
	//
    void
    StripHTML();


	///////////////////////
	//
	//  Convert the string to all upper-case letters.
	//
    void
    ToUpper();


private:
    char*   _str;
    int	    _capacity;
    int	    _firstnonblank;

				// grow by 1000 bytes at a time -- what
				// the hell!
    enum {
	GrowthFactor = 1000
    };


		// gets a full line of input from the stream -- handles
		// multiple reads when the line is longer than the
		// buffer capacity -- retuns EOF status
    int
    FullLine( ifstream& infile, int eightbitclean );

}; // StringBuff


    ////////////////////////////
    //
    //  Enable the output of a StringBuff to an ofstream
    //
ofstream& operator<<( ofstream& str, const StringBuff& buff );


/*------------------------------------------------------------------------
CLASS
    StringList

    Simple utility class for handling a list of strings.

KEYWORDS
    String, list

DESCRIPTION
    This utility can be used to build and read a linked list of
    strings.  Yes, it should be a parameterized type.  Yes, it
    should be structured better.  Fortunately, I'm under no
    obligation to make it better.  You didn't pay for it...what
    do you care :)

HOW TO USE
    The list is a simple linked list, that is constructed and
    queried in an obvious way given the member functions available.
    Only the root of the list needs to be deleted: it will take care
    of deleting all the other strings.

------------------------------------------------------------------------*/
class StringList
{
public:
	///////////////////////
	//
	//  Constructor.
	//
	//  [in] string	    The string for the list
	//
    StringList( char* string ) :
	_next( NULL ) { _string.Append( string ); }

	///////////////////////
	//
	//  Destructor.  Deletes everything which follows
	//  in the list.
	//
    ~StringList()
	{ if ( _next ) delete _next; _next = NULL; }

	///////////////////////
	//
	//  Add the given string into the list rooted at
	//  at <i>this</i>.  Makes a new node and returns
	//  a pointer to the new head of the list.
	//
    StringList*
    AddInOrder( char* newstr );

	///////////////////////
	//
	//  Set the next string in the list from this
	//  item.
	//
	//  [in] nextstring The next one in the list.
    void
    SetNext( StringList& nextstring )
	{ _next = &nextstring; }

	///////////////////////
	//
	//  Returns a pointer to the next item in the
	//  list.  Returns NULL when at the end of the
	//  list.
	//
    StringList*
    Next()
	{ return _next; }

	///////////////////////
	//
	//  Returns the string for this item in the list.
	//
    char*
    String()
	{ return _string; }

private:
    StringBuff  _string;
    StringList* _next;

}; // StringList


	/////////////////////////////////////////
	//
	//  Output streaming operator for the StringBuff
	//  class.  Some compilers (gnu) don't correctly do
	//  the typecasting to char* which StringBuff supports.
	//
inline ostream& operator<<( ostream& str, const StringBuff& buff )
		    { str << (char*)&buff[0]; return str; }



	/////////////////////////
	//
	//  Table entry used for mapping argument keywords to images.
	//
	//  [data] keyword  Argument keyword
	//  [data] image    Image to use for it
	//  [data] next	    Next item in the table
	//
typedef struct BulletMap
{
    StringBuff  keyword;
    StringBuff  image;
    BulletMap*  next;
} BulletMap;


/*------------------------------------------------------------------------
CLASS
    Customize

    Manages user customization data.

KEYWORDS
    Customize, graphics, logo, options

DESCRIPTION
    Class for holding the fun customization information such as graphics,
    paths, etc.

------------------------------------------------------------------------*/
class Customize
{
public:

	///////////////////////
	//
	//  Constructor.
	//
	//  [in] preformatted
	//	If TRUE, indicates that all header documentation
	//	should be output between &ltpre&gt tags.
	//  [in] customfile
	//	Name of the file containing customization data.
	//  [in] headerpath
	//	Directory path that should precede references to
	//	the current source file.
	//  [in] webpath
	//	Directory path for references to the web pages
	//	produced from this header file.
	//
    Customize( int preformatted, char* customfile, char* headerpath,
	       char* webpath, char* extension );


	///////////////////////
	//
	//  Returns a string for the cocoon logo image.
	//
    char*
    Logo()
	{ return _logo; }


	///////////////////////
	//
	//  Returns an html string to output for the horizontal rulings
	//  in the output pages.
	//
    char*
    Rule()
	{ return _rule; }


	///////////////////////
	//
	//  Returns a string to be used as the extension for all HTML
	//  files output by the utilities.
	//
    char*
    Extension()
	{ return _extension; }


	///////////////////////
	//
	//  Returns Netscape options for the &ltbody&gt tag, controlling
	//  background bitmap and colors.
	//
    char*
    BodyOptions()
	{ return _bodyoptions; }


	///////////////////////
	//
	//  Returns the image to put with the At-a-Glance heading.
	//
    char*
    AagImage()
	{ return _aagimage; }


	///////////////////////
	//
	//  Returns the image to put with the Quick Index heading.
	//
    char*
    QuickRefImage()
	{ return _quickrefimage; }


	///////////////////////
	//
	//  Returns the image to put with the Keyword heading.
	//
    char*
    KeywordImage()
	{ return _keywordimage; }


	///////////////////////
	//
	//  Returns the image to put with code examples.
	//
    char*
    ExampleImage()
	{ return _exampleimage; }


	///////////////////////
	//
	//  Returns the html text for the "Back to Top..." line.  If
	//  a NULL is returned, that means that no such line should
	//  be output.
	//
    char*
    BackToTop();


	///////////////////////
	//
	//  Returns the image to use for bullet-lists of member
	//  function arguments.
	//
    char*
    Bullet()
	{ return _bullet; }


	///////////////////////
	//
	//  If TRUE, the documentation sections should be considered
	//  pre-formatted.
	//
    int
    Preformatted()
	{ return _preformatted; }


	///////////////////////
	//
	//  Returns TRUE if the user desires to suppress text
	//  headings in cases where images are provided -- assumes
	//  that the images the user provides are clear enough,
	//  probably containing some text.
	//
    int
    ImageOnly()
	{ return _imageonly; }


	///////////////////////
	//
	//  Returns FALSE if control characters should be stripped
	//  out. This remedies the annoying ^M's that come from
	//  DOS when you are processing on Unix.
	//
    int
    EightBitClean()
	{ return _eightbitclean; }


	///////////////////////
	//
	//  Overrides default behavior of outputting the full member
	//  declaration for functions.  Used to hide inline function
	//  definitions.  Note that the full declarations of data
	//  items will still be output.
	//
    int
    SkipFullDeclaration()
	{ return _skipfulldecl; }


	///////////////////////
	//
	//  Returns the lowest heading level. The lowest heading
	//  level corresponds to the <i>biggest</i> heading text.
	//
    int
    BaseHeading()
	{ return _baseheading; }

	///////////////////////
	//
	//  If TRUE, indicates that the TABLE feature can be used
	//  for formatting.
	//
    int
    UseTable()
	{ return _usetable; }


	///////////////////////
	//
	//  If TRUE, indicates that frames should be used for formatting
	//  access to the main class documentation. The one-sheet version
	//  of the documentation is still created, but not as the class'
	//  main page.
	//
    int
    UseFrames()
	{ return _useframes; }

	///////////////////////
	//
	//  If TRUE, indicates that the local server for the produced
	//  pages can understand the ".pl" file extension and run the
	//  perl script.  Used for the keyword search page.
	//
    int
    UsePerl()
	{ return _useperl; }


	///////////////////////
	//
	//  When UseTable() is TRUE, this function returns the number
	//  of columns to use in the keyword cross-reference tables.
	//
    int
    NumKeyColumns()
	{ return _numkeycolumns; }


	///////////////////////
	//
	//  Returns an http string for the cocoon server.
	//
    char*
    Server()
	{ return _server; }


	///////////////////////
	//
    int
    doAutoKeywords()
	{ return _autokeywords; }


	///////////////////////
	//
	//  Returns a string for the path to precede references to the
	//  file being processed.
	//
    char*
    FilePath()
	{ return _filepath; }


	///////////////////////
	//
	//  Returns a string for the path to precede web-page
	//  references.
	//
    char*
    WebPath()
	{ return _webpath; }


	///////////////////////
	//
	//  Returns the maximum number of characters that a library
	//  or class name can be.  Warnings should be issued for names
	//  that are too long.  If a zero is returned, there is no
	//  limit.
	//
    int
    MaxNameLength()
	{ return _maxnamelen; }


	///////////////////////
	//
	//  Returns the sentinel string to use for members.
	//
    char*
    MemberSentinel()
	{ return _membersentinel; }


	///////////////////////
	//
	//  Returns TRUE if Cocoon should look for documentation on
	//  the same line as the member sentinel string.  Normally,
	//  the sentinel string is matched and the remainder of the
	//  line is thrown away.  However, if this function returns
	//  TRUE, that remainder is interpreted as documentation text.
	//
    int
    DocWithSentinel()
	{ return _docwithsentinel; }


	///////////////////////
	//
	//  Returns TRUE if sentinel strings for classes and members
	//  may not be present. Sentinels will still be looked for and
	//  recognized, but a lazier search for these components will
	//  also be used.  It is possible that this will lead to
	//  some bogus documentation.  We'll see...
	//
    int
    NoSentinels()
	{ return _nosentinels; }


	///////////////////////
	//
	//  Returns TRUE if the special customizations for Unimax
	//  should be done.
	//
    int
    ForUnimax()
	    { return _forunimax; }


	///////////////////////
	//
	//  Returns TRUE if the special customizations for Stratasys
	//  should be done.
	//
    int
    ForStratasys()
	    { return _forstratasys; }


	///////////////////////
	//
	//  Returns the precalculated length of the member sentinel
	//  string.
	//
    int
    MemSentinelLength()
	{ return _memsentinellength; }


	///////////////////////
	//
	//  Returns the sentinel string marking a class declaration.
	//  Default is "CLASS".
	//
    char*
    ClassSentinel()
	{ return _classsentinel; }


	///////////////////////
	//
	//  Returns the length of the class sentinel.
	//
    int
    ClassSentinelLength()
	{ return _classsentinellength; }


	///////////////////////
	//
	//  Returns the sentinel string marking library documentation.
	//  Default is "LIBRARY".
	//
    char*
    LibrarySentinel()
	{ return _librarysentinel; }

	///////////////////////
	//
	//  Returns the length of the library sentinel.
	//
    int
    LibrarySentinelLength()
	{ return _librarysentinellength; }


	///////////////////////
	//
	//  Returns the sentinel string marking class keywords.
	//  Default is "KEYWORDS".
	//
    char*
    KeywordSentinel()
	{ return _keywordsentinel; }

	///////////////////////
	//
	//  Returns the length of the keyword sentinel.
	//
    int
    KeywordSentinelLength()
	{ return _keywordsentinellength; }

	///////////////////////
	//
	//  Returns the sentinel string marking a code example.
	//  Default is "EXAMPLE".
	//
    char*
    ExampleSentinel()
	{ return _examplesentinel; }


	///////////////////////
	//
	//  Returns the length of the example sentinel.
	//
    int
    ExampleSentinelLength()
	{ return _examplesentinellength; }


	///////////////////////
	//
	//  Returns the sentinel string marking the end of a code
	//  example.  Default is "END".
	//
    char*
    EndSentinel()
	{ return _endsentinel; }


	///////////////////////
	//
	//  Returns the length of the end sentinel.
	//
    int
    EndSentinelLength()
	{ return _endsentinellength; }


	///////////////////////
	//
	//  Returns the string which should be used in place of
	//  the 'class' C++ keyword. This keyword is often hidden
	//  behind a #define in order to include other compiler
	//  directives, RTTI mechanisms, etc.
	//
    char*
    ClassString()
	{ return _classString; }


	///////////////////////
	//
	//  Returns the length of the class string.
	//
    int
    ClassStringLength()
	{ return _classStringLength; }



	///////////////////////
	//
	//  Returns a list of documentation headings to ignore.
	//
    StringList*
    IgnoreList()
	{ return _ignorelist; }


	///////////////////////
	//
	//  Will insert the contents of a user-specified HTML
	//  file that goes at the bottom of every page.
	//
    void
    HTMLAtBottom( ofstream& outfile );


	///////////////////////
	//
	//  Will insert the contents of a user-specified HTML
	//  file that goes at the top of every page.
	//
    void
    HTMLAtTop( ofstream& outfile )
	{ DumpFile( outfile, _attopfile,
		    "Specified at-top file not found."); }


	///////////////////////
	//
	//  Returns the bullet image that is mapped to the given
	//  keyword.
	//
    char*
    BulletByName( char* keyword );


	///////////////////////
	//
	//  Returns the protocol string to prepend to web paths.
	//
    char*
    Protocol()
	    { return _protocol; }


	///////////////////////
	//
	//  Returns TRUE if the implementation source code files
	//  should be accessible from the documenation.
	//
    int
    AccessImplementation()
	{ return _accessImp; }


	///////////////////////
	//
	//  Returns TRUE if Cocoon should output data for the
	//  private portions of class declarations, as well as
	//  the public and protected.
	//
    int
    ShowPrivates()
	{ return _showPrivates; }


	///////////////////////
	//
	//  Returns TRUE if the given string is one of the user-
	//  specified "ignore directives"--non-standard compiler
	//  directives that might be mixed up in a class declaration.
	//
    int
    IgnoreDirective(char* str);


private:

				// strings for the image namges -- includes
				// full path and html directive
    StringBuff  _logo;
    StringBuff  _rule;
    StringBuff  _background;
    StringBuff  _backcolor;
    StringBuff  _textcolor;
    StringBuff  _linkcolor;
    StringBuff  _visitedcolor;
    StringBuff  _activecolor;
    StringBuff  _bodyoptions;
    StringBuff  _aagimage;
    StringBuff  _keywordimage;
    StringBuff  _exampleimage;
    StringBuff  _quickrefimage;
    StringBuff  _backtotop;
    StringBuff  _bullet;
				// http or directory info
    StringBuff  _server;
    StringBuff  _filepath;
    StringBuff  _webpath;
    StringBuff  _protocol;
    StringBuff  _extension;
    StringBuff  _attopfile;
    StringBuff  _atbottomfile;
				// sentinel string overrides
    StringBuff  _membersentinel;
    int		_memsentinellength;
    StringBuff  _classsentinel;
    int		_classsentinellength;
    StringBuff  _keywordsentinel;
    int		_keywordsentinellength;
    StringBuff  _librarysentinel;
    int		_librarysentinellength;
    StringBuff  _examplesentinel;
    int		_examplesentinellength;
    StringBuff  _endsentinel;
    int		_endsentinellength;
    StringBuff  _classString;
    int		_classStringLength;
    StringList* _ignorelist;
    StringList* _ignoreDirectiveList;
				// other customization info
    BitField	_preformatted	 : 1;
    BitField	_usetable	 : 1;
    BitField	_useframes	 : 1;
    BitField	_useperl	 : 1;
    BitField	_imageonly	 : 1;
    BitField	_docwithsentinel : 1;
    BitField	_nosentinels	 : 1;
    BitField	_forunimax	 : 1;
    BitField	_forstratasys	 : 1;
    BitField	_skipfulldecl	 : 1;
    BitField	_autokeywords	 : 1;
    BitField	_eightbitclean   : 1;
    BitField	_accessImp	 : 1;
    BitField	_showPrivates	 : 1;
    int		_numkeycolumns;
    int		_maxnamelen;
    int		_baseheading;
    BulletMap*  _bulletmap;


	///////////////////////
	//
	//  Processes the customization file.
	//
    void
    GetCustomizations( char* customfile );


	///////////////////////
	//
	//  Dump the given HTML file to the given stream.
	//
    void
    DumpFile( ofstream& str, char* name, char* notfoundmsg );


}; // Customize

#endif

