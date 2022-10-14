
/*------------------------------------------------------------------------
				  COPYRIGHT
--------------------------------------------------------------------------
	  Copyright (C) 1995-2000
	  Jeff Kotula
	  All Rights Reserved.
--------------------------------------------------------------------------
				   FILE LOG
--------------------------------------------------------------------------

    Implementations for system-function classes.

------------------------------------------------------------------------*/

#include    "sys.h"

#ifdef  WINDOWS
#include    <io.h>
#ifdef  __BORLANDC__
#include    <fcntl.h>
#define _stat stat
#define _mkdir mkdir
#else
#include    <sys/types.h>
#include    <direct.h>
#include    <process.h>
#include    <errno.h>
#endif
#else
#include    <unistd.h>
#include    <dirent.h>
#endif

			// common includes
#include    <sys/stat.h>
#include    <stdlib.h>
#include    <time.h>


#if !defined(WINDOWS) && !defined(OS2)

//=========================================================================
//
//  Unix-Specific Class and functions
//
//=========================================================================

class UnixFunctions : public OSFunctions
{
public:

		    UnixFunctions()  { }
    virtual	    ~UnixFunctions() { }

    virtual int	    Exists( char* path );
    virtual void    Remove( char* path );

    virtual void    CreateDir( char* path );
    virtual void    WorkingDir( StringBuff& path );
    virtual void    ChangeDir( char* path );
    virtual void    AddFilesOfType( char* path, char* extension,
					StringList*& list );

    virtual void    System( char* commandline );

}; // UnixFunctions

/*-------------------------------------------------------------------------
-------------------------------------------------------------------------*/
int UnixFunctions::Exists( char* path )
{
    struct stat statbuff;

    if ( stat( path, &statbuff ) != 0 )
	return FALSE;
    return TRUE;
} // Exists


/*-------------------------------------------------------------------------
-------------------------------------------------------------------------*/
void	UnixFunctions::Remove( char* path )
{
    if ( ! path )
	return;
    if ( unlink( path ) != 0 )
	cout << "Could not remove " << path << endl;
} // Remove

/*-------------------------------------------------------------------------
-------------------------------------------------------------------------*/
void	UnixFunctions::CreateDir( char* path )
{
    if ( ! path  ||  ! strlen( path ) )
	return;
				// see if the parent exists yet
    int	    i;  // looping index
    StringBuff  parent; // path to parent

    parent.Append( path );
    for ( i = strlen( parent )-1; i >= 0; i-- )
	if ( parent[i] == '/' )
	{
	    parent[i] = '\0';
	    break;
	}
    if ( ! Exists( parent ) )
	CreateDir( parent );

				// create the directory
    mode_t  dirmode = S_IRUSR | S_IWUSR | S_IXUSR |
		      S_IRGRP | S_IWGRP | S_IXGRP |
		      S_IROTH | S_IWOTH | S_IXOTH;
    if ( mkdir( path, dirmode ) != 0 )
	cout << "Could not create directory " << path << endl;
} // CreateDir

/*-------------------------------------------------------------------------
-------------------------------------------------------------------------*/
void	UnixFunctions::WorkingDir( StringBuff& path )
{
    path.Clear();
    if ( ! getcwd( path, path.Capacity() ) )
	cout << "Could not get current working directory " << path << endl;
} // WorkingDir

/*-------------------------------------------------------------------------
-------------------------------------------------------------------------*/
void	UnixFunctions::ChangeDir( char* path )
{
    if ( ! path  ||  ! strlen( path ) )
	return;
    if ( chdir( path ) != 0 )
	cout << "Could not change to directory " << path << endl;
} // ChangeDir

/*-------------------------------------------------------------------------
-------------------------------------------------------------------------*/
void UnixFunctions::AddFilesOfType( char* path, char* extension,
				    StringList*& list )
{
    DIR*	dir	= NULL; // directory data
    struct dirent*  dent    = NULL; // directory entry
    char*	p	= ".";  // path to directory

    if ( path  &&  strlen( path ) )
	p = path;
    assert( ! extension || extension[0] == '.' );
    dir = opendir( p );
    if ( ! dir )
    {
	cout << "Could not open directory " << path << endl;
	return;
    }
    rewinddir( dir );

    dent = readdir( dir );
    while ( dent )
    {
	int period;

	for ( period = 0; dent->d_name[period]; period++ )
	    if ( dent->d_name[period] == '.' )
		break;

	if ( ! extension || strcmp( extension, &(dent->d_name[period])) == 0 )
	{
	    if ( ! list )
	    {
		list = new StringList( dent->d_name );
		if ( ! list )
		    THROW( "Memory allocation error!" );
	    }
	    else
		list = list->AddInOrder( dent->d_name );
	}

	dent = readdir( dir );
    }

    if ( closedir( dir ) != 0 )
	cout << "Could not close directory " << path << endl;
} // AddFilesOfType

/*-------------------------------------------------------------------------
-------------------------------------------------------------------------*/
void	UnixFunctions::System( char* commandline )
{
    assert( commandline != NULL );
    if ( system( commandline ) != 0 )
    {
	cout << "Could not perform: " << commandline << endl;
	THROW( "Could not execute." );
    }
} // System



#elif defined(WINDOWS)

//=========================================================================
//
//  WindowsNT-Specific Class and functions
//
//=========================================================================

class NTFunctions : public OSFunctions
{
public:

			    NTFunctions() { }
    virtual	~NTFunctions() { }

    virtual int	    Exists( char* path );
    virtual void    Remove( char* path );

    virtual void    CreateDir( char* path );
    virtual void    WorkingDir( StringBuff& path );
    virtual void    ChangeDir( char* path );
    virtual void    AddFilesOfType( char* path, char* extension,
					StringList*& list );

    virtual void    System( char* commandline );

    virtual char*   PathDivider();

}; // NTFunctions

/*-------------------------------------------------------------------------
-------------------------------------------------------------------------*/
int NTFunctions::Exists( char* path )
{
    struct _stat    statbuff;

    ReplaceDivider( path );
    if ( path[strlen( path ) - 1] == ':' )
	return TRUE;
    if ( _stat( path, &statbuff ) != 0 )
	return FALSE;
    return TRUE;
} // Exists

/*-------------------------------------------------------------------------
-------------------------------------------------------------------------*/
void	NTFunctions::Remove( char* path )
{
    if ( ! path )
	return;
    ReplaceDivider( path );
    if ( remove( path ) != 0 )
	cout << "Could not remove " << path << endl;
} // Remove

/*-------------------------------------------------------------------------
-------------------------------------------------------------------------*/
void	NTFunctions::CreateDir( char* path )
{
    if ( ! path  ||  ! strlen( path ) )
	return;
    ReplaceDivider( path );
				// see if the parent exists yet
    int	    i;  // looping index
    StringBuff  parent; // path to parent

    parent.Append( path );
    for ( i = strlen( parent )-1; i >= 0; i-- )
	if ( parent[i] == '\\' )
	{
	    parent[i] = '\0';
	    break;
	}
    if ( ! Exists( parent ) )
	CreateDir( parent );

    if ( _mkdir( path ) != 0 )
	cout << "Could not create directory " << path << endl;
} // CreateDir

/*-------------------------------------------------------------------------
-------------------------------------------------------------------------*/
void	NTFunctions::WorkingDir( StringBuff& path )
{
    int	    i;

#ifdef __BORLANDC__
#define GETCWD  getcwd
#else
#define GETCWD  _getcwd
#endif
    path.Clear();
    if ( ! GETCWD( path, path.Capacity() ) )
	cout << "Could not get current working directory " << path << endl;
    for ( i = 0; path[i]; i++ )
	if ( path[i] == '\\' )
	    path[i] = '/';
} // WorkingDir

/*-------------------------------------------------------------------------
-------------------------------------------------------------------------*/
void	NTFunctions::ChangeDir( char* path )
{
    if ( ! path  ||  ! strlen( path ) )
	return;
    ReplaceDivider( path );
    if ( chdir( path ) != 0 )
	cout << "Could not change to directory " << path << endl;
} // ChangeDir

/*-------------------------------------------------------------------------
-------------------------------------------------------------------------*/
void NTFunctions::AddFilesOfType( char* path, char* extension,
				  StringList*& list )
{
#ifdef  __BORLANDC__
#define FINDDATAT	    ffblk
#define FILENAMEX	    ff_name
#define FINDFIRSTFILE(p, b) findfirst(p, b, 0)
#define FINDNEXTFILE(h, b)  findnext(b)
#else
#define FINDDATAT	_finddata_t
#define FILENAMEX	name
#define FINDFIRSTFILE   _findfirst
#define FINDNEXTFILE	_findnext
#endif
    char*		p	= ".";  // path to directory
    long		handle;	    // search handle
    struct FINDDATAT	info;	    // information on the file
    StringBuff		filespec;   // file specification
    int			stat;	    // return status

    if ( path  &&  strlen( path ) )
	p = path;
    assert( ! extension || extension[0] == '.' );

    filespec.Append( p );
    filespec.Append( PathDivider() );
    filespec.Append( "*" );
    if ( extension )
	filespec.Append( extension );

    handle = FINDFIRSTFILE( filespec, &info );
    if ( handle == -1  &&  errno != ENOENT )
    {
	cout << "Could not open directory " << path << endl;
	return;
    }

    if ( handle == -1 )
	stat = -1;
    else
	stat = 0;
    while ( stat == 0 )
    {
	if ( ! list )
	{
	    list = new StringList( info.FILENAMEX );
	    if ( ! list )
		THROW( "Memory allocation error!" );
	}
	else
	    list = list->AddInOrder( info.FILENAMEX );

	stat = FINDNEXTFILE( handle, &info );
    }

#ifndef __BORLANDC__
    (void)_findclose( handle );
#endif

} // AddFilesOfType

/*-------------------------------------------------------------------------
-------------------------------------------------------------------------*/
void	NTFunctions::System( char* commandline )
{
    assert( commandline );
    ReplaceDivider( commandline );
    if ( system( commandline ) != 0 )
    {
	cout << "Could not perform: " << commandline << endl;
	THROW( "Could not execute." );
    }
} // System


/*-------------------------------------------------------------------------
-------------------------------------------------------------------------*/
char* NTFunctions::PathDivider()
{
    return "\\";
} // PathDivider



#elif defined(OS2)


//=========================================================================
//
//  OS/2 -Specific Class and functions
//
//=========================================================================

class OS2Functions : public OSFunctions
{
public:

		OS2Functions() { }
    virtual	~OS2Functions() { }

    virtual int	    Exists( char* path );
    virtual void    Remove( char* path );

    virtual void    CreateDir( char* path );
    virtual void    WorkingDir( StringBuff& path );
    virtual void    ChangeDir( char* path );
    virtual void    AddFilesOfType( char* path, char* extension,
		    StringList*& list );

    virtual void    System( char* commandline );

    virtual char*   PathDivider();
    virtual void	DateAndTime( StringBuff& str );
}; // OS2Functions

/*-------------------------------------------------------------------------
-------------------------------------------------------------------------*/
int OS2Functions::Exists( char* path )
{
    struct _stat    statbuff;

    ReplaceDivider( path );
    if ( _stat( path, &statbuff ) != 0 )
    return FALSE;
    return TRUE;
} // Exists

/*-------------------------------------------------------------------------
-------------------------------------------------------------------------*/
void	OS2Functions::Remove( char* path )
{
    if ( ! path )
    return;
    ReplaceDivider( path );
    if ( remove( path ) != 0 )
    cerr << "Could not remove " << path << endl;
} // Remove

/*-------------------------------------------------------------------------
-------------------------------------------------------------------------*/
void	OS2Functions::CreateDir( char* path )
{
    if ( ! path  ||  ! strlen( path ) )
    return;
    ReplaceDivider( path );
		// see if the parent exists yet
    int	    i;  // looping index
    StringBuff  parent; // path to parent

    parent.Append( path );
    for ( i = strlen( parent )-1; i >= 0; i-- )
    if ( parent[i] == '\\' )
    {
	parent[i] = '\0';
	break;
    }
    if ( ! Exists( parent ) )
    CreateDir( parent );

    if ( mkdir( path ) != 0 )
    cerr << "Could not create directory " << path << endl;
} // CreateDir

/*-------------------------------------------------------------------------
-------------------------------------------------------------------------*/
void	OS2Functions::WorkingDir( StringBuff& path )
{
    int	    i;

    path.Clear();
    if ( ! getcwd( path, path.Capacity() ) )
    cerr << "Could not get current working directory " << path << endl;
    for ( i = 0; path[i]; i++ )
    if ( path[i] == '\\' )
	path[i] = '/';
} // WorkingDir

/*-------------------------------------------------------------------------
-------------------------------------------------------------------------*/
void	OS2Functions::ChangeDir( char* path )
{
    if ( ! path  ||  ! strlen( path ) )
    return;
    ReplaceDivider( path );
    if ( chdir( path ) != 0 )
    cerr << "Could not change to directory " << path << endl;
} // ChangeDir

/*-------------------------------------------------------------------------
-------------------------------------------------------------------------*/
void OS2Functions::AddFilesOfType( char* path, char* extension,
		  StringList*& list )
{
    DIR*	dir	= NULL; // directory data
    struct dirent*  dent    = NULL; // directory entry
    char*	p	= ".";  // path to directory

    if ( path  &&  strlen( path ) )
    p = path;
    assert( ! extension || extension[0] == '.' );
    dir = opendir( p );
    if ( ! dir )
    {
    cerr << "Could not open directory " << path << endl;
    return;
    }

    dent = readdir( dir );
    while ( dent )
    {
    int period;

    for ( period = 0; dent->d_name[period]; period++ )
	if ( dent->d_name[period] == '.' )
	break;

    if ( ! extension || strcmp( extension, &(dent->d_name[period])) == 0 )
    {
	if ( ! list )
	{
	list = new StringList( dent->d_name );
	if ( ! list )
	    THROW( "Memory allocation error!" );
	}
	else
	list = list->AddInOrder( dent->d_name );
    }

    dent = readdir( dir );
    }

    if ( closedir( dir ) != 0 )
    cerr << "Could not close directory " << path << endl;
} // AddFilesOfType

/*-------------------------------------------------------------------------
-------------------------------------------------------------------------*/
void	OS2Functions::System( char* commandline )
{
    assert( commandline );
    ReplaceDivider( commandline );
    if ( system( commandline ) != 0 )
    {
    cerr << "Could not perform: " << commandline << endl;
    THROW( "Could not execute." );
    }
} // System


/*-------------------------------------------------------------------------
-------------------------------------------------------------------------*/
char* OS2Functions::PathDivider()
{
    return "\\";
} // PathDivider

void OS2Functions::DateAndTime( StringBuff& str )
{
    time_t  timer;

    str.Clear();
    timer = time( NULL );
    str.Append( ctime( &timer ) );
} // DateAnd Time



#endif

//=========================================================================
//
//  OSFunction member implementations
//
//=========================================================================
OSFunctions*	OSFunctions::_os = NULL;


/*-------------------------------------------------------------------------
-------------------------------------------------------------------------*/
OSFunctions& OS()
{
    if ( ! OSFunctions::_os )
    {
#ifdef  WINDOWS
	OSFunctions::_os = new NTFunctions();
#else
	OSFunctions::_os = new UnixFunctions();
#endif
	if ( ! OSFunctions::_os )
	    THROW( "Memory allocation error." );
    }

    return *OSFunctions::_os;
} // OS

/*-------------------------------------------------------------------------
-------------------------------------------------------------------------*/
void OSFunctions::CopyFile( char* source, char* destination, int isbinary )
{
    ReplaceDivider( source );
    ReplaceDivider( destination );

#ifdef WINDOWS
    ifstream	infile( source, isbinary ? (ios::in | ios::binary) : ios::in );
    ofstream	outfile( destination, isbinary ? (ios::out | ios::binary) : ios::out );
#else
    ifstream	infile( source, ios::in );
    ofstream	outfile( destination, ios::out );
#endif
    char	c;

    if ( ! infile )
    {
	cout << "Couldn't open " << source << " for reading." << endl;
	THROW( "File error." );
    }
    if ( ! outfile )
    {
	cout << "Couldn't open " << destination << " for writing." << endl;
	THROW( "File error." );
    }

    while ( infile.get( c ) )
	outfile.put( c );

    infile.close();
    outfile.close();

} // CopyFile


/*-------------------------------------------------------------------------
-------------------------------------------------------------------------*/
void OSFunctions::SortFile( char* file )
{
    StringBuff  inbuff;	    // input buffer
    StringList* substrings; // ordered list of substitution strings
    StringList* nextsubstring;  // used to iterate through the list

				// open the file for reading
    ReplaceDivider( file );
    ifstream	infile( file, ios::in );
    if ( ! infile )
    {
	cout << "Couldn't open " << file << " for reading." << endl;
	THROW( "File error." );
    }

				    // load in the substitution list and sort
				// it out...
    substrings = NULL;
    while ( inbuff.GetLine( infile ) )
    {
	if ( ! substrings )
	    substrings = new StringList( inbuff );
	else
	    substrings = substrings->AddInOrder( inbuff );
	if ( ! substrings )
	    THROW( "Memory allocation error." );
    }

				// close, and reopen the file for writing
    infile.close();

    ofstream	outfile( file, ios::out );
    if ( ! outfile )
    {
	cout << "Couldn't open " << file << " for writing." << endl;
	THROW( "File error." );
    }

				// go through all the substitution strings
    nextsubstring = substrings;
    while ( nextsubstring )
    {
	outfile << nextsubstring->String() << endl;
	nextsubstring = nextsubstring->Next();
    } // while
				// clean up
    outfile.close();
    if ( substrings )
	delete substrings;
    substrings = NULL;
} // Sort


/*-------------------------------------------------------------------------
-------------------------------------------------------------------------*/
void OSFunctions::Concatenate( char* result, char* tocat )
{
    StringBuff  buff;

    ReplaceDivider( result );
    ReplaceDivider( tocat );

    ifstream	infile( tocat, ios::in );
    ofstream	outfile( result, ios::app );

    if ( ! infile )
    {
	cout << "Couldn't open " << tocat << " for reading." << endl;
	THROW( "File error." );
    }
    if ( ! outfile )
    {
	cout << "Couldn't open " << result << " for appending." << endl;
	THROW( "File error." );
    }

    while ( buff.GetLine( infile ) )
	outfile << buff << endl;

} // Concatenate

/*-------------------------------------------------------------------------
-------------------------------------------------------------------------*/
void OSFunctions::RemoveAll( char* path, char* extension )
{
    StringList*	    dirlist = NULL; // list of files to remove
    StringList*	    dirent;	// next entry;

    dirlist = DirList( path, extension );
    if ( ! dirlist )
	return;

    dirent  = dirlist;
    while ( dirent )
    {
	StringBuff  fullpath;

	fullpath.Append( path );
	if ( path  &&  strlen( path ) )
	    fullpath.Append( PathDivider() );
	fullpath.Append( dirent->String() );
	Remove( fullpath );
	dirent = dirent->Next();
    }

    delete dirlist;
} // RemoveAll


/*-------------------------------------------------------------------------
-------------------------------------------------------------------------*/
void OSFunctions::Rename( char* currentName, char* newName )
{
    if ( rename(currentName, newName) != 0 )
    {
	cout << "Couldn't rename " << currentName << " to " << newName << endl;
	THROW( "File error." );
    }
} // Rename


/*-------------------------------------------------------------------------
-------------------------------------------------------------------------*/
char* OSFunctions::PathDivider()
{
    return "/";
} // PathDivider

/*-------------------------------------------------------------------------
-------------------------------------------------------------------------*/
void OSFunctions::ReplaceDivider( char* path )
{
    char    div = PathDivider()[0];
    int	    i;

    if ( ! path )
	return;
    if ( div == '/' )
	return;

    for ( i = 0; path[i]; i++ )
	if ( path[i] == '/' )
	    path[i] = div;

} // ReplaceDivider


/*-------------------------------------------------------------------------
-------------------------------------------------------------------------*/
StringList* OSFunctions::DirList( char* path, char* extension )
{
    StringList* dirlist = NULL;	    // list of files
    StringBuff  ext;		// extension for this iteration
    int	    i;		// index into extension list
    int	    j;		// temp

    if ( ! extension )
    {
	AddFilesOfType( path, extension, dirlist );
	return dirlist;
    }

					// go through all the extensions in
					// the list
    i = 0;
    while ( extension[i] )
    {
	ext.Clear();
	ext.Append( &extension[i] );
	for ( j = 0; ext[j]; j++ )
	    if ( ext[j] == '|' )
	    {
		ext[j] = '\0';
		break;
	    }
	AddFilesOfType( path, ext, dirlist );

					// get next one
	i += j;
	if ( extension[i] != '|' )
	    break;
	i++;
    } // while

    return dirlist;
} // DirList


/*-------------------------------------------------------------------------
-------------------------------------------------------------------------*/
void	OSFunctions::DateAndTime( StringBuff& str )
{
    time_t  timer;

    str.Clear();
    timer = time( NULL );
    str.Append( ctime( &timer ) );
} // DateAndTime


/*-------------------------------------------------------------------------
-------------------------------------------------------------------------*/
const char*
OSFunctions::getVariable(const char* envVariable)
{
    return getenv(envVariable);
} // getVariable


/*-------------------------------------------------------------------------
    Donated by Skarpi Hedinsson.
-------------------------------------------------------------------------*/
void OSFunctions::SortClassFile( char* file )
{
    StringBuff	  inbuff;	    // input buffer
    StringList*   substrings;	    // ordered list of substitution strings
    StringList*   nextsubstring;    // used to iterate through the list
    int		  found;	    // flag used in output loop

    //
    // Open the file for reading
    //
    ReplaceDivider( file );
    ifstream	infile( file, ios::in );

    //
    // Open a temp file to store the sorted class names.
    //
    ofstream	tempfile( "temp.cls", ios::out );

    if ( ! infile )
    {
	cout << "Couldn't open " << file << " for reading." << endl;
	THROW( "File error." );
    }

    //
    // Load in the class names and sort them out...
    //
    substrings = NULL;
    while ( inbuff.GetLine( infile ) )
    {
	//
	// If the line has a link to a class name we want it.
	//
	if ( strstr(inbuff, "<a href=") != NULL )
	{
	    if ( ! substrings )
		substrings = new StringList( inbuff );
	    else
		substrings = substrings->AddInOrder( inbuff );

	    if ( ! substrings )
		THROW( "Memory allocation error." );
	}

	//
	// Add the line to the temp file.
	//
	tempfile << inbuff << endl;
    }

    //
    // Close, and reopen the files for writing and reading.
    //
    infile.close();
    tempfile.close();
    ifstream	tempout( "temp.cls", ios::in );
    ofstream	outfile( file, ios::out );

    if ( ! outfile )
    {
	cout << "Couldn't open " << file << " for writing." << endl;
	THROW( "File error." );
    }

    if ( ! tempfile )
    {
	cout << "Couldn't open temp.cls for reading." << endl;
	THROW( "File error." );
    }

    //
    // Go through all the substitution strings
    //
    nextsubstring = substrings;
    while ( nextsubstring )
    {
	outfile << nextsubstring->String() << endl;

	//
	// Hack to reset the file position to the begining of the
	// file.
	//
	tempout.close();
	tempout.open( "temp.cls", ios::in);
	found = FALSE;

	//
	// Find the class name in the temp file and add the description.
	//
	while ( (inbuff.GetLine( tempout ))  &&  (found == FALSE) )
	{
	    if ( strcmp(inbuff, nextsubstring->String()) == 0 )
	    {
		//
		// Found the class name.  Now copy the description to
		// the outfile.
		//
		while ((inbuff.GetLine( tempout )) && (found == FALSE))
		{
		    if (strstr(inbuff, "<dt><a") == NULL)
		    {
			outfile << inbuff << endl;
		    }
		    else
		    {
			found = TRUE;
		    }
		}
	    }
	}

	nextsubstring = nextsubstring->Next();

    } // while

    //
    // Clean up.
    //
    outfile.close();
    tempfile.close();

    if ( substrings )
	delete substrings;

    substrings = NULL;

} // SortClassFile



