#include "print.h"

/**
 * This file contains functions that print out specific
 * syscalls and their arguments in the case that malicious
 * activity was detected.
 *
 * @author Jason Stanek
 * @version 1.0
 */

/********PRINT FUNCTIONS************/

/**
* This is a function to print out the open system call.
* 
* @param pid             Process id of child process being traced
* @param scno            The system call number of open
* @param args            The memory locations of the arguments in the child process
*
* @return                 0
*/
////////////////////////////////////////////////////////////////////////////////
int print_open(int pid, long scno, long *args){
////////////////////////////////////////////////////////////////////////////////
  char filename[MAXSTRLEN];
  long flags = 0;
  long mode = 0;

  flags = args[1];
  mode = args[2];

  if(get_string(pid, filename, args[0])==-1){
    fprintf(stderr,"Error reading filename\n");
  }

  get_number(pid, &flags, args[1]);
  get_number(pid, &mode, args[2]);
 
  fprintf(stderr,"%s,%d,%x", filename, flags, mode);
  return 0;
}

/**
* This is a function to print out the read system call.
* 
* @param pid             Process id of child process being traced
* @param scno            The system call number of read
* @param args            The memory locations of the arguments in the child process
*
* @return                 0
*/
////////////////////////////////////////////////////////////////////////////////
int print_read(int pid, long scno, long *args){
////////////////////////////////////////////////////////////////////////////////
  long fd = 0;
  long count = 0;

  fd = args[0];
  count = args[2];

  get_number(pid, &fd, args[0]);
  get_number(pid, &count, args[2]);
 
  fprintf(stderr,"%d,buffer,%d", fd, count);
  return 0;
}

/**
* This is a function to print out the write system call.
* 
* @param pid             Process id of child process being traced
* @param scno            The system call number of write
* @param args            The memory locations of the arguments in the child process
*
* @return                 0
*/
////////////////////////////////////////////////////////////////////////////////
int print_write(int pid, long scno, long *args){
////////////////////////////////////////////////////////////////////////////////
  long fd = 0;
  long count = 0;

  fd = args[0];
  count = args[2];

  get_number(pid, &fd, args[0]);
  get_number(pid, &count, args[2]);
 
  fprintf(stderr,"%d,buffer,%d", fd, count);
  return 0;
}

/**
* This is a function to print out the close system call.
* 
* @param pid             Process id of child process being traced
* @param scno            The system call number of close
* @param args            The memory locations of the arguments in the child process
*
* @return                 0
*/
////////////////////////////////////////////////////////////////////////////////
int print_close(int pid, long scno, long *args){
////////////////////////////////////////////////////////////////////////////////
  long fd = 0;

  fd = args[0];

  get_number(pid, &fd, args[0]);
 
  fprintf(stderr,"%d", fd);
  return 0;
}

/**
* This is a function to print out the exec system call.
* 
* @param pid             Process id of child process being traced
* @param scno            The system call number of exec
* @param args            The memory locations of the arguments in the child process
*
* @return                 0
*/
////////////////////////////////////////////////////////////////////////////////
int print_execve(int pid, long scno, long *args){
////////////////////////////////////////////////////////////////////////////////
  char filename[MAXSTRLEN];

  if(get_string(pid, filename, args[0])==-1){
    fprintf(stderr,"Error reading filename\n");
  }

  fprintf(stderr,"%s,...", filename);

  return 0;
}

/**
* This is a function to print out the brk system call.
* 
* @param pid             Process id of child process being traced
* @param scno            The system call number of brk
* @param args            The memory locations of the arguments in the child process
*
* @return                 0
*/
////////////////////////////////////////////////////////////////////////////////
int print_brk(int pid, long scno, long *args){
////////////////////////////////////////////////////////////////////////////////
  long end_data_seg = 0;

  end_data_seg = args[0];

  get_number(pid, &end_data_seg, args[0]);
 
  fprintf(stderr,"%d", end_data_seg);
  return 0;
}

/**
* This is a function to print out the ioctl system call.
* 
* @param pid             Process id of child process being traced
* @param scno            The system call number of ioctl
* @param args            The memory locations of the arguments in the child process
*
* @return                 0
*/
////////////////////////////////////////////////////////////////////////////////
int print_ioctl(int pid, long scno, long *args){
////////////////////////////////////////////////////////////////////////////////
  long device = 0;
  long request = 0;

  device = args[0];
  request = args[1];

  get_number(pid, &device, args[0]);
  get_number(pid, &request, args[1]);
 
  fprintf(stderr,"%d,%d,...", device, request);
  return 0;
}

/**
* This is a function to print out the old_mmap system call.
* 
* @param pid             Process id of child process being traced
* @param scno            The system call number of old_mmap
* @param args            The memory locations of the arguments in the child process
*
* @return                 0
*/
////////////////////////////////////////////////////////////////////////////////
int print_old_mmap(int pid, long scno, long *args){
////////////////////////////////////////////////////////////////////////////////
  long address = 0;
  long length = 0;
  long prot = 0;
  long flags = 0;
  long fd = 0;
  long offset = 0;

  address = args[0];
  length = args[1];
  prot = args[2];
  flags = args[3];
  fd = args[4];
  offset = args[5];

  get_number(pid, &address, args[0]);
  get_number(pid, &length, args[1]);
  get_number(pid, &prot, args[2]);
  get_number(pid, &flags, args[3]);
  get_number(pid, &fd, args[4]);
  get_number(pid, &offset, args[5]);

  fprintf(stderr,"0x%x,%d,%d,%d,%d,%x", address, length, prot, flags, fd, offset);
  return 0;
}

/**
* This is a function to print out the munmap system call.
* 
* @param pid             Process id of child process being traced
* @param scno            The system call number of munmap
* @param args            The memory locations of the arguments in the child process
*
* @return                 0
*/
////////////////////////////////////////////////////////////////////////////////
int print_munmap(int pid, long scno, long *args){
////////////////////////////////////////////////////////////////////////////////
  long address = 0;
  long length = 0;

  address = args[0];
  length = args[1];

  get_number(pid, &length, args[1]);
 
  fprintf(stderr,"0x%x,%d", address,length);
  return 0;
}

/**
* This is a function to print out the mprotect system call.
* 
* @param pid             Process id of child process being traced
* @param scno            The system call number of mprotect
* @param args            The memory locations of the arguments in the child process
*
* @return                 0
*/
////////////////////////////////////////////////////////////////////////////////
int print_mprotect(int pid, long scno, long *args){
////////////////////////////////////////////////////////////////////////////////
  long address = 0;
  long length = 0;
  long prot = 0;
  char protbuf[256];
  protbuf[0] = 0;
  char * t = protbuf;

  address = args[0];
  length = args[1];
  prot = args[2];

  get_number(pid, &length, args[1]);
  get_number(pid, &prot, args[2]);
 
  if(PROT_EXEC & prot){
    sprintf(t, "PROT_EXEC ");
    t += strlen(t);
  }
  if(PROT_READ & prot){
    sprintf(t, "PROT_READ ");
    t += strlen(t);
  }
  if(PROT_WRITE & prot){
    sprintf(t, "PROT_WRITE ");
    t += strlen(t);
  }
  if(!prot){
    sprintf(t, "PROT_NONE");
  }

  fprintf(stderr,"0x%x,%d,%s", address,length, protbuf);
  return 0;
}

/**
* This is a function to print out the personality system call.
* 
* @param pid             Process id of child process being traced
* @param scno            The system call number of personality
* @param args            The memory locations of the arguments in the child process
*
* @return                 0
*/
////////////////////////////////////////////////////////////////////////////////
int print_personality(int pid, long scno, long *args){
////////////////////////////////////////////////////////////////////////////////
  long persona = 0;

  persona = args[0];

  get_number(pid, &persona, args[0]);
 
  fprintf(stderr,"%d", persona);
  return 0;
}

/**
* This is a function to print out the sysctl system call.
* 
* @param pid             Process id of child process being traced
* @param scno            The system call number of sysctl
* @param args            The memory locations of the arguments in the child process
*
* @return                 0
*/
////////////////////////////////////////////////////////////////////////////////
int print_sysctl(int pid, long scno, long *args){
////////////////////////////////////////////////////////////////////////////////
  struct __sysctl_args thestruct;
  
  get_memory(pid, (char *)&thestruct, args[0], sizeof(struct __sysctl_args));
  
  fprintf(stderr, "{%s,%d,%x,%x,%x,%d}", thestruct.name, thestruct.nlen, thestruct.oldval, thestruct.oldlenp, thestruct.newval, thestruct.newlen);

  return 0;
}

/**
* This is a function to print out a default system call.
* 
* @param pid             Process id of child process being traced
* @param scno            The system call number of any syscall
* @param args            The memory locations of the arguments in the child process
*
* @return                 0
*/
////////////////////////////////////////////////////////////////////////////////
int print_default(int pid, long scno, long *args){
////////////////////////////////////////////////////////////////////////////////
  fprintf(stderr,"...");
  return 0;
}

