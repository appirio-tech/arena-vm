#include "safe.h"

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

/********SAFE FUNCTIONS************/

/**
* This is a function to determine if open is safe or not.
* Open is only allowed to open "*.so* (shared object) files.
* The first argument of open is the path name of the file to be opened.
* if the filename contains ".so" then we are allowed to open it for read only
* 
* @param pid             Process id of child process being traced
* @param scno            The system call number of open
* @param args            The memory locations of the arguments in the child process
*
* @return                 1 if is safe, 0 if not safe 
*/
////////////////////////////////////////////////////////////////////////////////
int safe_open(int pid, long scno, long *args){
////////////////////////////////////////////////////////////////////////////////
  char filename[MAXSTRLEN];
  long flags = 0;
  long mode = 0;

  flags = args[1];
  mode = args[2];

  if(get_string(pid, filename, args[0])==-1){
    printf("Error reading filename\n");fflush(0);
  }

  get_number(pid, &flags, args[1]);
  get_number(pid, &mode, args[2]);

  if(strstr(filename,".so")!=NULL){
//    print_syscall(pid, scno, args);
    if(flags==O_RDONLY){
      return 1;
    }
  } else if(strcmp(filename,"/etc/mtab")==0){
    if(flags==O_RDONLY){
      return 1;
    }
  } else if(strcmp(filename,"/proc/meminfo")==0){
    if(flags==O_RDONLY){
      return 1;
    }
  }
 
  return 0;
}


/**
* This is a function to determine if read is safe or not.
* Shared object files are opened one at a time.  So we only allow one file descriptor
* to be opened by the user process and we allow the user to read from that descriptor
* only.  The file descriptor is #4.
* 
* @param pid             Process id of child process being traced
* @param scno            The system call number of open
* @param args            The memory locations of the arguments in the child process
*
* @return                 1 if safe, 0 if not safe 
*/
////////////////////////////////////////////////////////////////////////////////
int safe_read(int pid, long scno, long *args){
////////////////////////////////////////////////////////////////////////////////
  long fd = 0;
  long count = 0;

  fd = args[0];
  count = args[2];

  get_number(pid, &fd, args[0]);
  get_number(pid, &count, args[2]);
 
  if(fd==4){
    return 1;
  }

  return 0;
}

/**
* This is a function to determine if write is safe or not.
* We really only want the user to be able to write to stdout, stderr, and because
* we need a way to return the user's return value, we have a pipe open in fd 3.
* Each of these pipes have a limit on the amount of data that can be passed
* through them.  Per TopCoder's request, this limit has been set at 5000 bytes.
* If more than 5000 bytes are written to any of the pipes, the data sent to be 
* written is truncated to abide by the 5000 byte limit.  To ensure that the user
* cannot write to a file, only fd 1-3 are enabled.
* 
* @param pid             Process id of child process being traced
* @param scno            The system call number of open
* @param args            The memory locations of the arguments in the child process
*
* @return                 1 if safe, 0 if not safe 
*/
////////////////////////////////////////////////////////////////////////////////
int safe_write(int pid, long scno, long *args){
////////////////////////////////////////////////////////////////////////////////
  long fd = 0;
  long count = 0;
  long temp = 0;
  static long out = 0;
  static long err = 0;
  static long result = 0;
  static long last = 0;
  static long odd = 0;
  static long changed = 0;

  fd = args[0];
  count = args[2];

  get_number(pid, &fd, args[0]);
  get_number(pid, &count, args[2]);
   
  if(odd==0){
    last = count;
  } else {
    if(changed){
      if(ptrace(PTRACE_PEEKUSER, pid, EAX*4, 0)!=-1){
        ptrace(PTRACE_POKEUSER, pid, EAX*4, last);
      }
    }
    changed = 0;
  }
  odd = !odd;

  if(fd==1 || fd==2 || fd==3){
    if(fd==1){
      out += count;
      if(out>RETLIMIT){
        changed = !changed;
        count = RETLIMIT-(out-count);
        out = RETLIMIT;
        if(set_number(pid, count, args[2])==-1){
          ptrace(PTRACE_POKEUSER, pid, (2*4), count);
        }
//        fprintf(stderr,"%d ",temp);
      }
    } else if(fd==2){
      err += count;
      if(err>RETLIMIT){
        changed = !changed;
        count = RETLIMIT-(err-count);
        err = RETLIMIT;
        if(set_number(pid, count, args[2])==-1){
          ptrace(PTRACE_POKEUSER, pid, (2*4), count);
        }
      }
    } else if(fd==3){
      result += count;
      if(result>RETLIMIT){
        changed = !changed;
        count = RETLIMIT-(result-count);
        result = RETLIMIT;
        if(set_number(pid, count, args[2])==-1){
          ptrace(PTRACE_POKEUSER, pid, (2*4), count);
        }
        fprintf(stderr,"The result you have returned has exceeded the 5000 character limit.\n");
        return 0;
      }
    }
    
    return 1;
  }

  return 0;
}

/**
* This is a function to determine if close is safe or not.
* It would be really bad if the user were allowed to close stdin, stdout, stderr, or
* the resultpipe.  If the user were allowed to do that, then he could open shared
* object files and write to them.  Because the user has no need to close any fd except
* for the shared object files opened by java and library function, we disallow closing
* any fd except 4 (shared objects).
* 
* @param pid             Process id of child process being traced
* @param scno            The system call number of open
* @param args            The memory locations of the arguments in the child process
*
* @return                 1 if safe, 0 if not safe 
*/
////////////////////////////////////////////////////////////////////////////////
int safe_close(int pid, long scno, long *args){
////////////////////////////////////////////////////////////////////////////////
  long fd = 0;

  fd = args[0];

  get_number(pid, &fd, args[0]);
 
  if(fd==4){
    return 1;
  }

  return 0;
}

/**
* This is a function to determine if exec is safe or not.
* Exec must be called to separate the user code from the sandbox code.  By calling
* exec we overwrite the current process memory and keep the user program from reading
* any of the local or global variables.
* Of course we do not want the user to be able to call exec and call a new
* program.  We also have to remember that we have to have the user process
* ask to be traced.  So, we want to allow exec to be called once.  This is the
* execution of the user program.  If exec is called a second time, the user process
* is killed before the new program has a chance to begin.
* 
* @param pid             Process id of child process being traced
* @param scno            The system call number of open
* @param args            The memory locations of the arguments in the child process
*
* @return                 1 if safe, 0 if not safe 
*/
////////////////////////////////////////////////////////////////////////////////
int safe_execve(int pid, long scno, long *args){
////////////////////////////////////////////////////////////////////////////////
  static int execcount = 0;
  
  if(!execcount){
    execcount++;
    return 1;
    }

  return 0;
}

/**
* This is a function to determine if brk is safe or not.
* brk is a system call used to allocate memory for a process.  Under normal
* circumstances most users will call malloc or new which in turn call brk.
* Allocating memory is not a big deal, unless we want to limit the amount of 
* memory that can be used by the user process, just in case one process decides
* to hog it all.  Per TopCoder's request, the memory limit has been set at 8MB.
* 
* @param pid             Process id of child process being traced
* @param scno            The system call number of open
* @param args            The memory locations of the arguments in the child process
*
* @return                 1 if safe, 0 if not safe 
*/
////////////////////////////////////////////////////////////////////////////////
int safe_brk(int pid, long scno, long *args){
////////////////////////////////////////////////////////////////////////////////
  static long begin_data_seg = 0;
  long end_data_seg = 0;
  long result = 0;

  end_data_seg = args[0];

  get_number(pid, &end_data_seg, args[0]);

  if(end_data_seg == 0){
    result = ptrace(PTRACE_PEEKUSER, pid, EAX*4, 0);
    if(result != -38){
      begin_data_seg = result;
    }
  }else{
    if((end_data_seg - begin_data_seg) > MEMLIMIT){
      fprintf(stderr,"Memory limit of %d bytes exceeded\n",MEMLIMIT);
      return 0;
    }
  }
    
  return 1;
}

/**
* This is a function to determine if ioctl is safe or not.
* ioctl is called by the user program.  We allow it to be called only on stdout
* and stderr.
* 
* @param pid             Process id of child process being traced
* @param scno            The system call number of open
* @param args            The memory locations of the arguments in the child process
*
* @return                 1 if safe, 0 if not safe 
*/
////////////////////////////////////////////////////////////////////////////////
int safe_ioctl(int pid, long scno, long *args){
////////////////////////////////////////////////////////////////////////////////
  long device = 0;
  long request = 0;

  device = args[0];
  request = args[1];

  get_number(pid, &device, args[0]);
  get_number(pid, &request, args[1]);
 
  if(device==1 || device==2){
    return 1;
  }

  return 0;
}

/**
* old_mmap is a safe function.  For debugging purposes it has been added.
* 
* @param pid             Process id of child process being traced
* @param scno            The system call number of open
* @param args            The memory locations of the arguments in the child process
*
* @return                 This function is always safe and always returns 1
*/
////////////////////////////////////////////////////////////////////////////////
int safe_old_mmap(int pid, long scno, long *args){
////////////////////////////////////////////////////////////////////////////////
  long length = 0;
  long prot = 0;
  long flags = 0;
  long fd = 0;
  long offset = 0;

  length = args[1];
  prot = args[2];
  flags = args[3];
  fd = args[4];
  offset = args[5];

  get_number(pid, &length, args[1]);
  get_number(pid, &prot, args[2]);
  get_number(pid, &flags, args[3]);
  get_number(pid, &fd, args[4]);
  get_number(pid, &offset, args[5]);
 

  return 1;
}

/**
* munmap is a safe function.  For debugging purposes it has been added.
* 
* @param pid             Process id of child process being traced
* @param scno            The system call number of open
* @param args            The memory locations of the arguments in the child process
*
* @return                 This function is always safe and always returns 1 
*/
////////////////////////////////////////////////////////////////////////////////
int safe_munmap(int pid, long scno, long *args){
////////////////////////////////////////////////////////////////////////////////
  long length = 0;

  length = args[1];

  get_number(pid, &length, args[1]);
 
  return 1;
}

/**
* mprotect is a safe function.  For debugging purposes it has been added.
* 
* @param pid             Process id of child process being traced
* @param scno            The system call number of open
* @param args            The memory locations of the arguments in the child process
*
* @return                 This function is always safe and always returns 1 
*/
////////////////////////////////////////////////////////////////////////////////
int safe_mprotect(int pid, long scno, long *args){
////////////////////////////////////////////////////////////////////////////////
  long length = 0;
  long prot = 0;

  length = args[1];
  prot = args[2];

  get_number(pid, &length, args[1]);
  get_number(pid, &prot, args[2]);
 
  return 1;
}

/**
* This is a function to determine if personality is safe or not.
* We only want to allow the user to specify 0 as the persona.
* 
* @param pid             Process id of child process being traced
* @param scno            The system call number of open
* @param args            The memory locations of the arguments in the child process
*
* @return                 1 if safe, 0 if not safe 
*/
////////////////////////////////////////////////////////////////////////////////
int safe_personality(int pid, long scno, long *args){
////////////////////////////////////////////////////////////////////////////////
  long persona = 0;

  persona = args[0];

  get_number(pid, &persona, args[0]);
 
  if(persona==0){
    return 1;
  }

  return 0;
}

/**
* This is a function to determine if sysctl is safe or not.
* This function is called by Redhat at the start of a program.  It is not called
* by Slackware and if possible, we really don't want the user to call this.
* Because Redhat Linux insists on calling this function, we make sure that the
* function does not attempt to write to any of the system parameters.
* 
* @param pid             Process id of child process being traced
* @param scno            The system call number of open
* @param args            The memory locations of the arguments in the child process
*
* @return                 1 if safe, 0 if not safe 
*/
////////////////////////////////////////////////////////////////////////////////
int safe_sysctl(int pid, long scno, long *args){
////////////////////////////////////////////////////////////////////////////////
  struct __sysctl_args thestruct;

  get_memory(pid, (char *)&thestruct, args[0], sizeof(struct __sysctl_args));

  if(thestruct.newval == NULL && thestruct.newlen == 0){
    return 1;
  }

  return 0;
}


/**
* This is a default safe function.  The default function will never be called.
* 
* @param pid             Process id of child process being traced
* @param scno            The system call number of open
* @param args            The memory locations of the arguments in the child process
*
* @return                 0 is always returned because default system calls are
*                         not safe.
*/
////////////////////////////////////////////////////////////////////////////////
int safe_default(int pid, long scno, long *args){
////////////////////////////////////////////////////////////////////////////////
  return 0;
}

