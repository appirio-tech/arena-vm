/**
 * This file contains functions that print out specific
 * syscalls and their arguments in the case that malicious
 * activity was detected.
 *
 * @author Jason Stanek
 * @version 1.0
 */


	/////////////////////////////////////////
	//
	//  This is a function to print out the open system call.
	//  It returns 0.
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
int print_open(int pid, long scno, long *args);



	/////////////////////////////////////////
	//
	//  This is a function to print out the read system call.
	//  It returns 0.
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
int print_read(int pid, long scno, long *args);


	/////////////////////////////////////////
	//
	//  This is a function to print out the write system call.
	//  It returns 0.
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
int print_write(int pid, long scno, long *args);


	/////////////////////////////////////////
	//
	//  This is a function to print out the close system call.
	//  It returns 0.
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
int print_close(int pid, long scno, long *args);


	/////////////////////////////////////////
	//
	//  This is a function to print out the execve system call.
	//  It returns 0.
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
int print_execve(int pid, long scno, long *args);


	/////////////////////////////////////////
	//
	//  This is a function to print out the brk system call.
	//  It returns 0.
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
int print_brk(int pid, long scno, long *args);


	/////////////////////////////////////////
	//
	//  This is a function to print out the ioctl system call.
	//  It returns 0.
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
int print_ioctl(int pid, long scno, long *args);


	/////////////////////////////////////////
	//
	//  This is a function to print out the old_mmap system call.
	//  It returns 0.
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
int print_old_mmap(int pid, long scno, long *args);


	/////////////////////////////////////////
	//
	//  This is a function to print out the munmap system call.
	//  It returns 0.
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
int print_munmap(int pid, long scno, long *args);


	/////////////////////////////////////////
	//
	//  This is a function to print out the mprotect system call.
	//  It returns 0.
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
int print_mprotect(int pid, long scno, long *args);


	/////////////////////////////////////////
	//
	//  This is a function to print out the personality system call.
	//  It returns 0.
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
int print_personality(int pid, long scno, long *args);


	/////////////////////////////////////////
	//
	//  This is a function to print out the sysctl system call
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
int print_sysctl(int pid, long scno, long *args);


	/////////////////////////////////////////
	//
	//  This is a function to print out any other system call.
	//  It returns 0.
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
int print_default(int pid, long scno, long *args);

