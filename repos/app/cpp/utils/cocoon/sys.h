
/*------------------------------------------------------------------------
				  COPYRIGHT
--------------------------------------------------------------------------
	  Copyright (C) 1995-2000
	  Jeff Kotula
	  All Rights Reserved.
--------------------------------------------------------------------------
				   FILE LOG
--------------------------------------------------------------------------

    Include file for system-function class.

------------------------------------------------------------------------*/

#ifndef cocoon_sys_h
#define cocoon_sys_h

#include    "util.h"


//=========================================================================
//
//  Define some useful macros and constants
//
//=========================================================================


//=========================================================================
//
//  Declarations of global functions.
//
//=========================================================================

class OSFunctions;

	///////////////////////
	//
	//  Returns a reference to the global operating-system
	//  object.  The first call to this function will initialize
	//  this subsystem.
	//
OSFunctions&	OS();


//=========================================================================
//
//  Classes
//
//=========================================================================

/*-------------------------------------------------------------------------
CLASS
    OSFunctions

    Base class for operating-system functions that may vary between
    Unix and Windows, or are just convenient to put into a uniform
    calling sequence.

DESCRIPTION

    This class defines the interface for OS-dependent functions.
    Initialization of this subsystem is handled automatically: the
    user needs only to call the global function OS to get access
    to it.

PATHS

    All directory paths can be either NULL, full or relative, but must be
    accurate from whatever the current working directory is.  Paths
    should use the Unix and URL standard '/' character as a path divider.

SELECTING THE OS

    By default, sys.cxx will be compilable under Unix.  In order to
    run under UNIX, sys.cxx must be compiled with the WINDOWS
    symbol defined.

-------------------------------------------------------------------------*/
class OSFunctions
{
public:

	///////////////////////
	//
	//  Default constructor.  Doesn't really do anything...
	//
    OSFunctions() { }

	///////////////////////
	//
	//  Destructor.
	//
    virtual
    ~OSFunctions() { }

	///////////////////////
	//
	//  This function returns TRUE if the indicated file or
	//  directory exists.
	//
	//  [in] path   Path to the file or directory
	//
    virtual int
    Exists( char* path ) = 0;

	///////////////////////
	//
	//  Creates a directory with the given path.  Will overwrite
	//  any existing directory.  Will also create any parent
	//  directories in the path which don't yet exist.
	//
    virtual void
    CreateDir( char* path ) = 0;

	///////////////////////
	//
	//  Returns the path to the current working directory.
	//
    virtual void
    WorkingDir( StringBuff& path ) = 0;

	///////////////////////
	//
	//  Change the current working directory to the given one.
	//
	//  [in] path	    New working directory
	//
    virtual void
    ChangeDir( char* path ) = 0;

	///////////////////////
	//
	//  Copies the given source file to the given destination.
	//  If the destination file already exists an exception is
	//  thrown.
	//
	//  [in] source		File to copy
	//  [in] destination	File to create as the copy
	//  [in] isbinary	TRUE if the file is binary
	//
    void
    CopyFile( char* source, char* destination, int isbinary );


	///////////////////////
	//
	//  Sorts the contents of the file into alphabetical order via
	//  a simple insertion sort.  Not particularily efficient, but
	//  adequate for smaller files.
	//
	//  [in] file	    The file to sort
	//
    void
    SortFile( char* file );

	///////////////////////
	//
	//  Performs a special sort for the library class summary
	//  partial file.
	//
	//  [in] file	    The file to sort
	//
    void
    SortClassFile( char* file );


	///////////////////////
	//
	//  Returns a list of the paths to every file in the
	//  given directory with the given extension.  The list is
	//  returned in alphabetical order.
	//
	//  [in] path	    Directory to look at.
	//  [in] extension  Filename extension to match.  Must
	//	    start with a '.' character if not NULL.
	//	    If it is NULL, all files in the directory
	//	    will be returned. Multiple extensions may
	//	    be provided, separated by a '|' character.
	//
	//  If the directory is empty or none of the file name extensions
	//  match, NULL is returned.  The returned string list must be
	//  <strong>delete</strong>d by the caller when they are done
	//  with it.
	//
    StringList*
    DirList( char* path, char* extension );

	///////////////////////
	//
	//  Issue a command to an operating system shell.  The
	//  format of the command is Unix standard.
	//
    virtual void
    System( char* commandline ) = 0;

	///////////////////////
	//
	//  Concatenates the contents of one file to another.
	//  If the result file does not exist, it is created.
	//
	//  [in] result	    The file to concatenate to.
	//  [in] tocat	    The file whose contents should be
	//	    concatenated.
	//
    void
    Concatenate( char* result, char* tocat );

	///////////////////////
	//
	//  Remove the given file or directory.
	//
	//  [in] path	    File or directory to remove.
	//
    virtual void
    Remove( char* path ) = 0;

	///////////////////////
	//
	//  Remove all files at the given location that have the
	//  given extension.
	//
	//  [in] path	    Directory where the files currently
	//	    exist.
	//  [in] extension  Filename extension to match.  Must
	//	    start with a '.' character if not NULL.
	//	    If it is NULL, all files in the directory
	//	    will be returned.
	//
    void
    RemoveAll( char* path, char* extension );


	///////////////////////
	//
	//  Renames the given file to the new name. If the a file
	//  with the new name already exists, it will be deleted.
	//  
	//  [in] currentName	The file's current name.
	//  [in] newName	It's desired name.
	//
    void
    Rename( char* currentName, char* newName );


	///////////////////////
	//
	//  Returns the character that the OS uses to separate
	//  elements in a directory path.  You may use the default
	//  divider of '/' if you wish and the OS functions will
	//  automatically map to the proper divider, but you can also
	//  just use it upfront...
	//
    virtual char*
    PathDivider();


	///////////////////////
	//
	//  Fill in the current date and time into the given string.
	//
	//  [out] str   Filled with the current date and time.
	//
    void
    DateAndTime( StringBuff& str );


	///////////////////////
	//
	//  Return a pointer to a string with the value of the
	//  given environment variable. NULL is returned if no
	//  such environment variable is found.
	//  
	//  [in] envVariable	The name of the environment variable
	//
    const char*
    getVariable(const char* envVariable);


//---------------------------------------------------------------------
protected:
	///////////////////////
	//
	//  Replaces all occurrences of the standard divider
	//  character with the real divider.
	//
	//  [in] path   Path to edit.
	//
    void
    ReplaceDivider( char* path );


	///////////////////////
	//
	//  Adds to a list of the paths to every file in the
	//  given directory with the given extension.  The list is
	//  returned in alphabetical order.
	//
	//  [in] path	    Directory to look at.
	//  [in] extension  Filename extension to match.  Must
	//	    start with a '.' character if not NULL.
	//  [in] list	    List of files to add to.
	//
	//  If the directory is empty or none of the file name extensions
	//  match, NULL is returned.  The returned string list must be
	//  <strong>delete</strong>d by the caller when they are done
	//  with it.
	//
    virtual void
    AddFilesOfType( char* path, char* extension, StringList*& list ) = 0;


//---------------------------------------------------------------------
private:

			// pointer to the global OS -- the first time
			// OS() is called, the appropriate os object
			// is created and this pointer set
    static OSFunctions* _os;

    friend OSFunctions& OS();   // needs access to _os

}; // OSFunctions

#endif  // cocoon_sys_h

