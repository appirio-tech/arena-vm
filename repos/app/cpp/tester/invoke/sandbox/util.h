/**
 * This file contains a number of prototypes for utility functions 
 * used by the sandbox.
 *
 * @author Jason Stanek
 * @version 1.0
 */


	/////////////////////////////////////////
	//
	//  The length that filenames are truncated to.
	//
#define MAXSTRLEN 1024


	/////////////////////////////////////////
	//
	//  This is used to kill a process.  Either the grandchild or
	// child processes are killed.
	//
	//  [in] pid
	//	The process id of the process to be killed
	//
void killchild(int pid);


	/////////////////////////////////////////
	//
	//  get_args grabs the system call arguments from a child
	//  process that is being ptraced.
	//
	//  [in] pid
	//	The process id of the process that is being traced
	//  [in] scno
	//	The system call number that is being called by the traced
	//	process
	//  [in/out] args
	//	A place to write the arguments of the system call
	//
int get_args(int pid, long scno, long *args);


	/////////////////////////////////////////
	//
	//  get_memory attempts to read a protion of a ptraced
	//  process' memory.
	//
	//  [in] pid
	//	The process id of the process that is being traced
	//  [in/out] dest
	//	A pointer in the current process' memory where the 
	//	child's memory will be copied to.
	//  [in] source
	//	The memory location in the child process to read from
	//  [in] size
	//	The number of bytes to read
	//
int get_memory(int pid, char * dest, long source, long size);


	/////////////////////////////////////////
	//
	//  get_string attempts to read a string from a ptraced process'
	//  memory.  The memory is read until it has been null terminated.
	//
	//  [in] pid
	//	The process id of the process that is being traced
	//  [in/out] dest
	//	A pointer in the current process' memory where the 
	//	string from the child's memory will be copied to.
	//  [in] source
	//	The memory location in the child process to read from
	//
int get_string(int pid, char * dest, long source);


	/////////////////////////////////////////
	//
	//  get_number attempts to read a 32-bit number from a ptraced
	//  process' memory.
	//
	//  [in] pid
	//	The process id of the process that is being traced
	//  [in/out] dest
	//	A pointer in the current process' memory where the 
	//	32-bit number from the child's memory will be copied to.
	//  [in] source
	//	The memory location in the child process to read from
	//
int get_number(int pid, long * dest, long source);


	/////////////////////////////////////////
	//
	//  set_number attempts to write a 32-bit number to a ptraced
	//  process' memory.
	//
	//  [in] pid
	//	The process id of the process that is being traced
	//  [in] data
	//	The 32 bit number to write to the child's memory
	//	at the location specified by dest.
	//  [in] dest
	//	The memory location in the child process to write to
	//
int set_number(int pid, long data, long dest);


	/////////////////////////////////////////
	//
	//  This is a function that calls the appropriate function
	//  to determine if this system call is safe depending on the 
	//  system call number.
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
int safe_syscall(int pid, long scno, long *args);


	/////////////////////////////////////////
	//
	//  This function calls the appropriate function to print out
	//  the system call, its arguments, and its return value.
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
int print_syscall(int pid, long scno, long *args);
