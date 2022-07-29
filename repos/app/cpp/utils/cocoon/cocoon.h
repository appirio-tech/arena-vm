
/*------------------------------------------------------------------------
				  COPYRIGHT
--------------------------------------------------------------------------
	  Copyright (C) 1995-2000
	  Jeff Kotula
	  All Rights Reserved.
--------------------------------------------------------------------------
				   FILE LOG
--------------------------------------------------------------------------

    Header file for the cocoon utility.

------------------------------------------------------------------------*/
#ifndef docweb_h
#define docweb_h

#include    "util.h"


/*------------------------------------------------------------------------
CLASS
    CustFile

    This is a linked list making up a set of lines in a customization file.

DESCRIPTION

    This is a linked list making up a set of lines in a customization file.
    The implementation isn't important.  The client merely declares an
    instance of CustFile.  The list nodes are built as needed, and
    automatically destructed when the root object goes out of scope.

------------------------------------------------------------------------*/
class CustFile
{
public:
	/////////////////////////////
	//
	//  Constructor.
	//
    CustFile()
	{ _next = NULL; }

	/////////////////////////////
	//
	//  Construct <i>with</i> a particular first line of the
	//  customizations...
	//
	//  [in] line   First line of customization file.
	//
    CustFile( char* line )
	{ _line.Append( line ); _next = NULL; }

	/////////////////////////////
	//
	//  Destructor.
	//
    ~CustFile();

	/////////////////////////////
	//
	//  Add another line to the customization file.
	//
	//  [in] nextline   Line to add.
	//
    void    AddLine( char* nextline );

	/////////////////////////////
	//
	//  Output all lines to the given stream.
	//
	//  [in] str	Stream to output to.
	//
    void    Output( ostream& str );

private:
    StringBuff  _line;
    CustFile*   _next;

}; // Custfile

/*------------------------------------------------------------------------
CLASS
    OtherData

    This holds a description of auxiliary data files needing to be
    copied into the documentation tree.

DESCRIPTION
    This holds a description of auxiliary data files needing to be
    copied into the documentation tree.  Allows the user to get gif
    images, implementation files, or other web pages into the
    documentation tree easily.

    Some list management functions are built-in for convenience.

------------------------------------------------------------------------*/
class OtherData
{
public:
	/////////////////////////////
	//
	//  Constructor.
	//
	//  [in] extension  Extension for the files to copy.
	//  [in] srcdir	    Directory where files can be found.
	//  [in] websubdir  Directory in documentation tree where
	//		    files should be copied to.
	//  [in] isbinary   TRUE if the files are binary
	//
    OtherData( char* extension, char* srcdir, char* websubdir, int isbinary );


	/////////////////////////////
	//
	//  Destructor.  Deletes any and all objects forward
	//  in list.
	//
    ~OtherData();

				//-----------------------------------
				// GROUP:   Query
				//-----------------------------------

	/////////////////////////////
	//
	//  Returns the file extension of the target files.
	//
    char*
    Extension()
	{ return _extension; }

	/////////////////////////////
	//
	//  Returns the directory where source can be found.
	//
    char*
    SourceDir()
	{ return _srcdir; }

	/////////////////////////////
	//
	//  Returns the directory where the files should be put.
	//
    char*
    WebSubDir()
	{ return _websubdir; }

	/////////////////////////////
	//
	//  Returns TRUE if the file to be copied is binary
	//
    int
    isBinary()
	{ return _isbinary; }

				//-----------------------------------
				// GROUP:   List Management
				//-----------------------------------

	/////////////////////////////
	//
	//  Returns the next object in the list.  May be NULL.
	//
    OtherData*
    Next()
	{ return _next; }

	/////////////////////////////
	//
	//  Sets the next object in the list.
	//
	//  [in] other	    Next object in list.
	//
    void
    SetNext( OtherData& other )
	{ _next = &other; }

private:
    StringBuff  _extension;
    StringBuff  _srcdir;
    StringBuff  _websubdir;
    int		_isbinary;
    OtherData*  _next;

}; // OtherData



/*------------------------------------------------------------------------
CLASS
    LibraryData

    This holds the data for a library.

DESCRIPTION
    This class encapsulates all the data that is necessary to generate
    the documentation for a single library.  Some library list
    management functions are built-in for convenience.

------------------------------------------------------------------------*/
class LibraryData
{
public:
	/////////////////////////////
	//
	//  Constructor.
	//
	//  [in] name	    The name of the library.
	//  [in] srcdir	    Directory where source can be found.
	//  [in] websubdir  Subdirectory where web files should go.
	//
    LibraryData( char* name, char* srcdir, char* websubdir );

	/////////////////////////////
	//
	//  Destructor.  Deletes any and all libraries forward
	//  in list.
	//
    ~LibraryData();

				//-----------------------------------
				// GROUP:   Query
				//-----------------------------------

	/////////////////////////////
	//
	//  Returns the object encapsulating the customization
	//  overrides.
	//
    CustFile&
    Overrides()
	{ return _overrides; }

	/////////////////////////////
	//
	//  Returns the name of the library
	//
    char*
    Name()
	{ return _name; }

	/////////////////////////////
	//
	//  Returns the directory where source can be found.
	//
    char*
    SourceDir()
	{ return _srcdir; }

	/////////////////////////////
	//
	//  Returns the directory where web pages should be put.
	//
    char*
    WebSubDir()
	{ return _websubdir; }

				//-----------------------------------
				// GROUP:   List Management
				//-----------------------------------

	/////////////////////////////
	//
	//  Returns the next library in the list.  May be NULL.
	//
    LibraryData*
    Next()
	{ return _next; }

	/////////////////////////////
	//
	//  Sets the next library in the list.
	//
	//  [in] library    Next library in list.
	//
    void
    SetNext( LibraryData& library )
	{ _next = &library; }

private:
    StringBuff	    _name;
    StringBuff	    _srcdir;
    StringBuff	    _websubdir;
    CustFile	    _overrides;
    LibraryData*    _next;

    friend class    LibraryIter;

}; // LibraryData


/*------------------------------------------------------------------------
CLASS
    LibraryIter

    Iterates through all the libraries in the list.

------------------------------------------------------------------------*/
class LibraryIter
{
public:
	/////////////////////////////
	//
	//  Constructor.  Initially, isDone() is TRUE.  Call
	//  First() at top of iteration.
	//
	//  [in] liblist    Library list to iterate through.
	//  [in] webroot    Root directory for the web files
	//
    LibraryIter( LibraryData& liblist, char* webroot ) :
	_liblist( &liblist ), _currlib( NULL )
	{ _webroot.Append( webroot ); }

	/////////////////////////////
	//
	//  Destructor.
	//
    virtual
    ~LibraryIter()
	{ _liblist = NULL; _currlib = NULL;
	  _webdir.Clear(); _webroot.Clear(); }

	/////////////////////////////
	//
	//  Resets to the first element in the library list.
	//
    virtual void
    First();

	/////////////////////////////
	//
	//  Steps to the next library in the list.
	//
    virtual void
    Next();

	/////////////////////////////
	//
	//  Returns TRUE if there are no more libraries in the list.
    virtual int
    isDone()
	{ return _currlib == NULL; }

	/////////////////////////////
	//
	//  Returns the data for the current library.  Returns NULL
	//  if isDone() equals TRUE.
	//
    LibraryData*
    CurrLibrary()
	{ return _currlib; }

	/////////////////////////////
	//
	//  Returns a string representing the webroot plus the
	//  web subdirectory for the library.
	//
    char*
    WebDir()
	{ return _webdir; }

private:
    LibraryData*    _liblist;
    LibraryData*    _currlib;
    StringBuff	    _webroot;
    StringBuff	    _webdir;

}; // LibraryIter


/*------------------------------------------------------------------------
CLASS
    FileIter

    Iterates through all the files with a given extension in the
    libraries.

------------------------------------------------------------------------*/
class FileIter : public LibraryIter
{
public:
	/////////////////////////////
	//
	//  Constructor.  Initially, isDone() is TRUE.  Call
	//  First() at top of iteration.
	//
	//  [in] liblist    Library list to iterate through.
	//  [in] webroot    Root directory for the web files
	//  [in] ext	    File extension to look for. NULL ok.
	//  [in] sourcedir  If TRUE, goes through source directories
	//	    of libraries, otherwise through the web
	//	    directories
	//
    FileIter( LibraryData& liblist, char* webroot, char* ext, int sourcedir ) :
	LibraryIter( liblist, webroot ), _filelist( NULL ), _currfile( NULL ),
	_sourcedir( sourcedir )
	{ _ext.Append( ext ); }

	/////////////////////////////
	//
	//  Destructor.
	//
    virtual
    ~FileIter()
	{ _filelist  = NULL; _currfile = NULL; _ext.Clear();
	  _sourcedir = FALSE; }

	/////////////////////////////
	//
	//  Resets the iteration to the first file in the first
	//  library.
	//
    virtual void
    First();

	/////////////////////////////
	//
	//  Moves to the next file in the library.  If at the end
	//  of the files, moves on to the next library.
	//
    virtual void
    Next();

	/////////////////////////////
	//
	//  Returns TRUE if all done.
	//
    virtual int
    isDone();

	/////////////////////////////
	//
	//  Returns the name of the file.  Returns NULL if isDone()
	//  equals TRUE;
	//
    char*
    FileName();

private:
    StringBuff  _ext;
    StringList* _filelist;
    StringList* _currfile;
    BitField	_sourcedir : 1;

}; // FileIter


/*------------------------------------------------------------------------
CLASS
    DocSetConfig

    The configuration for the extraction of a documentation set.

DESCRIPTION
    Encapsulates all the configuration data to enable the extraction
    of a documentation set from source files.

------------------------------------------------------------------------*/
class DocSetConfig
{
public:
	/////////////////////////////
	//
	//  Constructor.  Loads up the documentation set configuration
	//  file.
	//
	//  [in] filename   Name of the configuration file (or the
	//	    library name if localonly is TRUE).
	//  [in] localonly  If TRUE, indicates that just the source files
	//	    on the current working directory should
	//	    be done.
	//
    DocSetConfig( char* filename, int localonly );

	/////////////////////////////
	//
	//  Destructor.
	//
    ~DocSetConfig();

	/////////////////////////////
	//
	//  Generate the documentation set by using the other
	//  <i>cocoon</i> utilities.
	//
    void
    Generate();

	/////////////////////////////
	//
	//  Return the ultimate web page extension to use.
	//
    char*
    Extension()
	{ return _ext; }

				//-----------------------------------
				// GROUP:   Debugging
				//-----------------------------------
	/////////////////////////////
	//
	//  Prints out the customization file for each library.
	//
    void
    PrintLibData();


private:
    StringBuff	    _filename;  // configuration file
    StringBuff	    _webroot;   // root directory for web files
    StringBuff	    _urlroot;   // root path for URLs -- need this for
				// the relinking of cross-class
				// references--because two classes may
				// at different depths in the doc tree,
				// can't just use relative path names
    StringBuff	    _ext;	// html filename extension
    CustFile	    _defaultcust;// default customization for all
    LibraryData*    _liblist;   // library data
    OtherData*	    _otherlist; // other data to copy over
    StringList*	    _hyperlinks;// extra hypertext links
    int		    _useperl;
    int		    _useframes;
    int		    _accessImp;
    int		    _localonly; // doing only the files on the
				// current directory -- special
				// handling of source overwriting, etc.


	/////////////////////////////
	//
	//  Returns TRUE if the given customization option can only
	//  be part of the default customizations.
	//
	//  [in] line   Line with customization option.
	//
    int
    OnceOnly( StringBuff& line );

	/////////////////////////////
	//
	//  Loads the configuration file data.
	//
    void
    LoadConfigFile();

	/////////////////////////////
	//
	//  Cleans up any temporary files left behind during processing.
	//
    void
    Cleanup( char* extension );


}; // DocSetConfig


#endif  // docweb_h

