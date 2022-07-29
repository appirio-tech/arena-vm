/**
 * This file contains qualification functions for system
 * calls that are sometimes malicious and sometimes not.
 * The functions look at the arguments of the syscalls to
 * determine if it is safe to execute or not.  Per request of
 * TopCoder, an 8MB limit on memory usage was created.
 *
 * @author Jason Stanek
 * @version 1.0
 */


	/////////////////////////////////////////
	//
	//  The size of a megabyte.
	//
#define MB 1024*1024


	/////////////////////////////////////////
	//
	//  The limit on the amount of memory that the traced process
	//  may use.
	//
#define MEMLIMIT 8*MB


	/////////////////////////////////////////
	//
	//  This limit on the amount of bytes that the user can
	//  return through stdout, stderr, and the resultpipe.
	//
#define RETLIMIT 20000


	/////////////////////////////////////////
	//
	//  This is a function to determine if open is safe or not.
	//  Open is only allowed to open "*.so* (shared object) files.
	//  The first argument of open is the path name of the file to
	//  be opened. if the filename contains ".so" then we are
	//  allowed to open it for read only
	//  Returns 1 if it safe and 0 if it is not safe.
	//
	//  [in] pid
	//	The process id of the process that is being traced
	//  [in] scno
	//	The system call number that is being called by the traced
	//	process
	//  [in] args
	//	A pointer to a list of memory locations of the
	//	system call arguments int the traced process
	//
int safe_open(int pid, long scno, long *args);


	/////////////////////////////////////////
	//
	//  This is a function to determine if read is safe or not.
	//  Shared object files are opened one at a time.  So we
	//  only allow one file descriptor to be opened by the user
	//  process and we allow the user to read from that
	//  descriptor only.  The file descriptor is #4.  
	//
	//  [in] pid
	//	The process id of the process that is being traced
	//  [in] scno
	//	The system call number that is being called by the traced
	//	process
	//  [in] args
	//	A pointer to a list of memory locations of the
	//	system call arguments int the traced process
	//
int safe_read(int pid, long scno, long *args);


	/////////////////////////////////////////
	//
	//  This is a function to determine if write is safe or not.
	//  We really only want the user to be able to write to stdout,
	//  stderr, and because we need a way to return the user's return
	//  value, we have a pipe open in fd 3.  Each of these pipes have
	//  a limit on the amount of data that can be passed through
	//  them.  Per TopCoder's request, this limit has been set at 5000
	//  bytes.  If more than 5000 bytes are written to any of the
	//  pipes, the data sent to be written is truncated to abide by
	//  the 5000 byte limit.  To ensure that the user cannot write to
	//  a file, only fd 1-3 are enabled.
	//
	//  [in] pid
	//	The process id of the process that is being traced
	//  [in] scno
	//	The system call number that is being called by the traced
	//	process
	//  [in] args
	//	A pointer to a list of memory locations of the
	//	system call arguments int the traced process
	//
int safe_write(int pid, long scno, long *args);


	/////////////////////////////////////////
	//
	//  This is a function to determine if close is safe or not. It
	//  would be really bad if the user were allowed to close stdin,
	//  stdout, stderr, or the resultpipe.  If the user were allowed
	//  to do that, then he could open shared object files and write
	//  to them.  Because the user has no need to close any fd except
	//  for the shared object files opened by java and library
	//  function, we disallow closing any fd except 4 (shared
	//  objects).
	//
	//  [in] pid
	//	The process id of the process that is being traced
	//  [in] scno
	//	The system call number that is being called by the traced
	//	process
	//  [in] args
	//	A pointer to a list of memory locations of the
	//	system call arguments int the traced process
	//
int safe_close(int pid, long scno, long *args);


	/////////////////////////////////////////
	//
	//  This is a function to determine if exec is safe or not.Exec
	//  must be called to separate the user code from the sandbox
	//  code.  By calling exec we overwrite the current process
	//  memory and keep the user program from reading any of the local
	//  or global variables. Of course we do not want the user to be
	//  able to call exec and call a new program.  We also have to
	//  remember that we have to have the user process ask to be
	//  traced.  So, we want to allow exec to be called once.  This is
	//  the execution of the user program.  If exec is called a second
	//  time, the user process is killed before the new program has a
	//  chance to begin.
	//
	//  [in] pid
	//	The process id of the process that is being traced
	//  [in] scno
	//	The system call number that is being called by the traced
	//	process
	//  [in] args
	//	A pointer to a list of memory locations of the
	//	system call arguments int the traced process
	//
int safe_execve(int pid, long scno, long *args);


	/////////////////////////////////////////
	//
	//  This is a function to determine if brk is safe or not.
	//  brk is a system call used to allocate memory for a
	//  process.  Under normal circumstances most users will call
	//  malloc or new which in turn call brk.  Allocating memory is
	//  not a big deal, unless we want to limit the amount
	//  of memory that can be used by the user process, just in case
	//  one process decides to hog it all.  Per TopCoder's request,
	//  the memory limit has been set at 8MB.
	//
	//  [in] pid
	//	The process id of the process that is being traced
	//  [in] scno
	//	The system call number that is being called by the traced
	//	process
	//  [in] args
	//	A pointer to a list of memory locations of the
	//	system call arguments int the traced process
	//
int safe_brk(int pid, long scno, long *args);


	/////////////////////////////////////////
	//
	//  This is a function to determine if ioctl is safe or not.
	//  ioctl is called by the user program.  We allow it to be called
	//  only on stdout and stderr.
	//
	//  [in] pid
	//	The process id of the process that is being traced
	//  [in] scno
	//	The system call number that is being called by the traced
	//	process
	//  [in] args
	//	A pointer to a list of memory locations of the
	//	system call arguments int the traced process
	//
int safe_ioctl(int pid, long scno, long *args);


	/////////////////////////////////////////
	//
	//  old_mmap is a safe function.  For debugging purposes it has
	//  been added.
	//
	//  [in] pid
	//	The process id of the process that is being traced
	//  [in] scno
	//	The system call number that is being called by the traced
	//	process
	//  [in] args
	//	A pointer to a list of memory locations of the
	//	system call arguments int the traced process
	//
int safe_old_mmap(int pid, long scno, long *args);


	/////////////////////////////////////////
	//
	//  munmap is a safe function.  For debugging purposes it has
	//  been added.
	//
	//  [in] pid
	//	The process id of the process that is being traced
	//  [in] scno
	//	The system call number that is being called by the traced
	//	process
	//  [in] args
	//	A pointer to a list of memory locations of the
	//	system call arguments int the traced process
	//
int safe_munmap(int pid, long scno, long *args);


	/////////////////////////////////////////
	//
	//  mprotect is a safe function.  For debugging purposes it has
	//  been added.
	//
	//  [in] pid
	//	The process id of the process that is being traced
	//  [in] scno
	//	The system call number that is being called by the traced
	//	process
	//  [in] args
	//	A pointer to a list of memory locations of the
	//	system call arguments int the traced process
	//
int safe_mprotect(int pid, long scno, long *args);


	/////////////////////////////////////////
	//
	//  This is a function to determine if personality is safe or not.
	//  We only want to allow the user to specify 0 as the persona.
	//
	//  [in] pid
	//	The process id of the process that is being traced
	//  [in] scno
	//	The system call number that is being called by the traced
	//	process
	//  [in] args
	//	A pointer to a list of memory locations of the
	//	system call arguments int the traced process
	//
int safe_personality(int pid, long scno, long *args);


	/////////////////////////////////////////
	//
	//  This is a function to determine if sysctl is safe or not.
	//  This function is called by Redhat at the start of a
	//  program.  It is not called by Slackware and if possible, we
	//  really don't want the user to call this.  Because Redhat
	//  Linux insists on calling this function, we make sure that
	//  the function does not attempt to write to any of the system
	//  parameters.
	//
	//  [in] pid
	//	The process id of the process that is being traced
	//  [in] scno
	//	The system call number that is being called by the traced
	//	process
	//  [in] args
	//	A pointer to a list of memory locations of the
	//	system call arguments int the traced process
	//
int safe_sysctl(int pid, long scno, long *args);



	/////////////////////////////////////////
	//
	//  This is a default safe function.  The default function will
	//  never be called.
	//
	//  [in] pid
	//	The process id of the process that is being traced
	//  [in] scno
	//	The system call number that is being called by the traced
	//	process
	//  [in] args
	//	A pointer to a list of memory locations of the
	//	system call arguments int the traced process
	//
int safe_default(int pid, long scno, long *args);
