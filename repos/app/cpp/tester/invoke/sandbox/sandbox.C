/**
 * This file contains the main sandbox code to protect the system
 * from malicious user code.  The sandbox implementation is done
 * using the ptrace system call.  There are 3 processes involved
 * with this protection.
 *
 * Grandchild: The grandchild process is the wrapped user code.
 * because it is a different process and exec is called to overwrite
 * the process memory, there is no way for the grandchild to look at
 * its parent's or grandparent's memory.  The child sets up a
 * result pipe in file descriptor slot 3 for the wrapper to send
 * back its result.
 *
 * Child: The child process is the main process that ptraces the 
 * user code.  This process looks up whether certain system calls
 * should be allowed through a table of system calls found in
 * syscalls.h.  If the syscall is allowed for all cases, deny is 0.
 * if the syscall is not allowed, deny is -1.  If the syscall is 
 * allowed sometimes, deny is 1 and then a function is called to
 * determine if the arguments are safe.  If the grandchild process
 * is deemed to be malicious, then it is killed and the reason is
 * written back through stderr.  The Child also sends the
 * grandchild's pid through a pipe to the parent process.
 *
 * Parent: The parent process is the timer process.  Because there
 * is no way for the wait function to timeout, we can't time the
 * length of execution from the ptracer.  Instead we have to add a 
 * third process.  This process waits for 5 seconds or until the
 * grandchild process has finished executing.  If the grandchild
 * executes for more than 5 seconds, the parent first kills the
 * grandchild, then the child, and then returns.  All error messages
 * returned through stderr.  In the case that the grandchild does
 * finish w/in the time period, it should have written back data
 * through the resultpipe.  If there is data, the parent grabs the
 * data and appends it to the stderr pipe.  There can be no more error
 * messages or appends to stderr after the user result is written to
 * stderr.
 *
 *
 * @author Jason Stanek
 * @version 1.0
 */

#include <stdio.h>
#include <stdlib.h>
#include <sys/signal.h>
#include <sys/mman.h>
#include <sys/wait.h>
#include <sys/types.h>
#include <sys/time.h>
#include <sys/poll.h>
#include <sys/ptrace.h>
#include <sys/reg.h>
#include <linux/sysctl.h>
#include <fcntl.h>
#include <unistd.h>
#include <time.h>
#include <errno.h>
#include <string.h>
#include "util.h"
#include "print.C"
#include "safe.C"
#include "syscalls.h"


#define TIMELIMIT 7.5

/**
* This is the sandbox to run the user program
* 
* @param argv[0]         sandbox executable
* @param argv[1]         user c++ program
* @param argv[2]         a randomly generated numerical key used to locate the user result
* @param argv[3]         the return type of the user program
* @param argv[4]+        the commandline arguments to be sent to the user c++ program
*
* @return                 0 if the user program ran safely
*/
////////////////////////////////////////////////////////////////////////////////
int main(int argc, char * argv[]){
////////////////////////////////////////////////////////////////////////////////
  int resultpipe[2];
  int childpidpipe[2];
  int userpid = 0;
  if(pipe(resultpipe)==-1){
    fprintf(stderr,"Unable to create result pipe\n");fflush(0);
    exit(-1);
  }
  if(pipe(childpidpipe)==-1){
    fprintf(stderr,"Unable to create child pid pipe\n");fflush(0);
    exit(-1);
  }
  int guard = fork();
  if(guard == -1){
    fprintf(stderr,"Could not fork guard process\n");fflush(0);
  }
  else if(guard == 0){

    userpid = fork();
    if(userpid == -1){
      fprintf(stderr,"Could not fork tester process\n");fflush(0);
    }
    /**
    * This is to run the wrapped user code.  This is the GrandChild process.
    * This process closes its open file descriptors that will not be needed.
    * The file descriptors that are left open are stdin, stdout, stderr, and
    * fd 3 for the user result to eventually be sent back to the java program
    * for comparison.
    */
    else if(userpid == 0){  

      if(close(resultpipe[0])==-1){
        fprintf(stderr,"Unable to close read end of result pipe\n");
        fflush(0);
        exit(-1);
      }
      if(resultpipe[1]=dup(resultpipe[1])!=3){
        fprintf(stderr,"Unable to move result pipe to fd 3\n");
        fflush(0);
        exit(-1);
      }
      if(close(4)==-1){
        fprintf(stderr,"Unable to close extra result pipe\n");
        fflush(0);
        exit(-1);
      }
      if(close(childpidpipe[0])==-1){
        fprintf(stderr,"Unable to close read end of child pid pipe\n");
        fflush(0);
        exit(-1);
      }
      if(close(childpidpipe[1])==-1){
        fprintf(stderr,"Unable to close write end of child pid pipe\n");
        fflush(0);
        exit(-1);
      }

      if(ptrace(PTRACE_TRACEME,0,0,0)==-1){
        fprintf(stderr,"Tracing not enabled, bailing out!\n");
        fflush(0);
        exit(-1);
      }

      char** args = new char*[argc];
      int i=0;
      for(i=0;i<argc-1;i++){
        args[i] = argv[i+1];
      }
      args[i] = NULL;
      execv(args[0], args);

      fprintf(stderr,"Exec failed : ERRNO %d\n",errno);fflush(0);
      exit(-1);
    }
    /**
    * This is the sandbox code.  This is the GrandChild process.
    * This process closes its unused pipes and sends the child pid to the parent
    * process so that the parent may time the process.  This process is responsible
    * for catching any potentially hostile system calls before they can be
    * executed.  Each system call is caught prior to execution and after execution.
    * If the system call is deemed hostile, then the child process is killed and
    * -1 is returned.  
    */
    else{
      int result=0;
      int address = 0;
      int data = 0;
      int status = 0;
      long scno=3;
      long args[32];

      waitpid(userpid, &status, 0);
      close(resultpipe[1]);
      close(childpidpipe[0]);

      write(childpidpipe[1],&userpid,sizeof(int));

      while(!WIFEXITED(status) && !WIFSIGNALED(status)){

        scno = ptrace(PTRACE_PEEKUSER, userpid, ORIG_EAX*4, 0);

        if(scno < 0 || scno > 229){
          fprintf(stderr,"Segment Fault\n");
          killchild(userpid);
          exit(-1);
          break;
        }else if(!syscalls[scno].deny){
          result = ptrace(PTRACE_SYSCALL, userpid, 1, 0);
        }else if(syscalls[scno].deny>0){
          if(get_args(userpid, scno, args)==-1){
            fprintf(stderr,"Error getting syscall args\n");
            killchild(userpid);
            exit(-1);
            break;
          }
          if(safe_syscall(userpid, scno,args)){
            result = ptrace(PTRACE_SYSCALL, userpid, 1, 0);
          }else{
            fprintf(stderr,"Possible malicious content\n");
            fprintf(stderr,"Illegal system call : %s\n", syscalls[scno].name);
            fflush(0);
            killchild(userpid);
            exit(-1);
            break;
          }  
        }else{
          fprintf(stderr,"Possible malicious content\n");
          fprintf(stderr,"Illegal system call : %s\n", syscalls[scno].name);
          fflush(0);
          killchild(userpid);
          exit(-1);
          break;
        }
           
        waitpid(userpid, &status, 0);
      }
      return 0;
    }
  }
  /**
  * This is the timer process.  It is the parent process.
  * The parent process closes its unneeded pipe and receives the pid of the grandchild
  * program from the child.  If the user program terminates within the timelimit,
  * then the result is relayed back to the CodeTester.  On the otherhand, if the 
  * user program runs longer than the allowed time limit, then it is killed and
  * an exit value of -1 is returned.
  */
  else{
    int status = 0;
    int done = 0;
    struct timespec current;
    current.tv_sec = 0;
    current.tv_nsec = 0;
    struct timespec remaining;
    remaining.tv_sec = 0;
    remaining.tv_nsec = 0;
    int sec = 0;
    int nsec = 0;
   
    int count = 0;

    close(childpidpipe[1]);
 
    read(childpidpipe[0],&userpid,sizeof(int));

    while(!done){
      current.tv_sec = 0;
      current.tv_nsec = 100000000;
      
      if(guard==waitpid(guard, &status, WNOHANG)){
        if(errno){
          fprintf(stderr, "Wait : ");
          switch(errno){
            case ECHILD:
              fprintf(stderr, "Child does not exist\n");
              done = 1;
              break;
            case EINVAL:
              fprintf(stderr, "Invalid options\n");
              done = 1;
              break;
            default:
              fprintf(stderr, "Unknown error, ERRNO %d\n", errno);
              done = 1;
              break;
          }
        }
        if(WIFEXITED(status)){
//          fprintf(stderr, "Children exited sucessfully\n");
          done = 1;
          if(WEXITSTATUS(status)!=0){
            killchild(userpid);
            killchild(guard);
            exit(-1);
          }
        }
        if(WIFSIGNALED(status)){
          fprintf(stderr, "Extrememly bad: please report to service@topcoder.com\n");
          killchild(userpid);
          killchild(guard);
          exit(-1);
        }
      }
      while(nanosleep(&current,&remaining)==-1){
//        fprintf(stderr, "Sleep interupted\n");
        sec = remaining.tv_sec;
        nsec = remaining.tv_nsec;
        current.tv_sec = sec;
        current.tv_nsec = nsec;
      }
      count++;
      if(count >= TIMELIMIT*10){
        fprintf(stderr, "The code execution time exceeded the 8 second time limit.\n");
        done = 1;
        killchild(userpid);
        killchild(guard);
        exit(-1);
        break;
      }
    }
//    printf("done");fflush(0);
    killchild(userpid);
    killchild(guard);

    int BUFFERSIZE = 5001;
    char * buffer = new char[BUFFERSIZE];
    char * correctbuffer = buffer;
    fd_set childresult;
    struct timeval quickdelay;
    quickdelay.tv_sec = 0;
    quickdelay.tv_usec = 100000;

    FD_ZERO(&childresult);
    FD_SET(resultpipe[0], &childresult);

    if(select(1024,&childresult, NULL, NULL, &quickdelay)<=0){
      fprintf(stderr,"No value was returned by the user process\n");
      fflush(0);
      exit(-1);
    }
    else{
      count = read(resultpipe[0], buffer, BUFFERSIZE);
      fprintf(stderr, "%s", buffer);
      fflush(0);
      exit(0);
    }
  }
}

/**
* This is a used to kill a process.  Either the grandchild or
* child processes are killed.
* 
* @param pid             This process id to be killed
*/
////////////////////////////////////////////////////////////////////////////////
void killchild(int pid){
////////////////////////////////////////////////////////////////////////////////
//  fprintf(stderr, "Killing %d from process %d\n",pid,getpid());
  if(kill(pid, SIGKILL)!=0){
    if(errno){
      switch(errno){
        case EINVAL:
          fprintf(stderr,"Invalid kill signal\n");
          break;
        case ESRCH:
//          fprintf(stderr,"Child already exited\n");
          break;
        case EPERM:
          fprintf(stderr,"Child has gained permissions!\n");
          break;
        default:
          fprintf(stderr,"Unknown kill error :%d\n", errno);
          break;
      }
    }
  } else{
//    fprintf(stderr,"Child successfully killed\n");
  }
  usleep(100000);
  fflush(0);
}

/**
* Get_args grabs the system call arguments from a child process that is
* being ptraced.
* 
* @param pid             This process id being traced
* @param scno            The system call number
* @param args            A place to write the arguments of the system call
*
* @return                 0 if the arguments were successfully read
*/
////////////////////////////////////////////////////////////////////////////////
int get_args(int pid, long scno, long *args){
////////////////////////////////////////////////////////////////////////////////
  int error = 0;
  for (int i = 0; i < syscalls[scno].nargs; i++) {
    args[i] = ptrace(PTRACE_PEEKUSER, pid, i*4, 0);
    if(args[i] == -1 && errno){
      error = -1;
fprintf(stderr,"error reading %d argument\n",i);
    }
  }
  return error;
}  

/*
int set_args(int pid, long scno, long *args){
////////////////////////////////////////////////////////////////////////////////
  int error = 0;
  for (int i = 0; i < syscalls[scno].nargs; i++) {
    ptrace(PTRACE_POKEUSER, pid, i*4, args[i]);
    if(args[i] == -1 && errno){
      error = -1;
fprintf(stderr,"error reading %d argument\n",i);
    }
  }
  return error;
}  
*/

/**
* Get_memory attempts to read a portion of a ptraced process' memory.
* 
* @param pid             This process id being traced
* @param dest            The place to write the memory to
* @param source          The memory location in the child process to read from
* @param size            The number of bytes to read
*
* @return                 0 if the memory was successfully read
*/
////////////////////////////////////////////////////////////////////////////////
int get_memory(int pid, char * dest, long source, long size){
////////////////////////////////////////////////////////////////////////////////
  int error = 0;
  int count = 0;
  int offset = 0;
  long result = 0;
  char fourchars[sizeof(long)];

  while(1) {
    result = ptrace(PTRACE_PEEKDATA, pid, (char *)source, 0);
    if(result == -1 && errno){
      fprintf(stderr,"Error reading child's memory\n");
      dest[0] = (char)NULL;
      return -1;
    }
    *((long*)fourchars) = result;
    if((size-count)<4){
      memcpy((void *)dest, (void *)fourchars, size-count);
      break;
    }else{
      memcpy((void *)dest, (void *)fourchars, sizeof(long));
    }
    dest += sizeof(long);
    source += sizeof(long);
    count += 4;
  }
  return 0;
}    

/**
* Get_string attempts to read a string from a ptraced process' memory.
* The memory is read until it has been null terminated.
* 
* @param pid             This process id being traced
* @param dest            The place to write the string to
* @param source          The memory location in the child process to read from
*
* @return                 0 if the string was successfully read
*/
////////////////////////////////////////////////////////////////////////////////
int get_string(int pid, char * dest, long source){
////////////////////////////////////////////////////////////////////////////////
  int error = 0;
  int done = 0;
  int count = 0;
  int total = 0;
  int offset = 0;
  long result = 0;
  char fourchars[sizeof(long)];

  while(!done) {
    result = ptrace(PTRACE_PEEKDATA, pid, (char *)source, 0);
    if(result == -1 && errno){
      fprintf(stderr,"Error reading child's string\n");
      dest[0] = (char)NULL;
      return -1;
      }
    *((long*)fourchars) = result;
    if(total + sizeof(long)<MAXSTRLEN){
      memcpy((void *)dest, (void *)fourchars, sizeof(long));
    } else {
      fourchars[MAXSTRLEN-total-1] = 0;
      memcpy((void *)dest, (void *)fourchars, MAXSTRLEN-total);
    }
    total += sizeof(long);
    dest += sizeof(long);
    source += sizeof(long);
    for(count=0;count<sizeof(long);count++){
      if(fourchars[count]==(char)NULL){
        done = 1;
      }
    }
  }
  return 0;
}    

/**
* Get_number attempts to read a 32-bit number from a ptraced process' memory.
* 
* @param pid             This process id being traced
* @param dest            The place to write the number to
* @param source          The memory location in the child process to read from
*
* @return                 0 if the number was successfully read
*/
////////////////////////////////////////////////////////////////////////////////
int get_number(int pid, long * dest, long source){
////////////////////////////////////////////////////////////////////////////////
  int result = 0;

  result = ptrace(PTRACE_PEEKDATA, pid, (char *)source, 0); 

  if(result == -1 && errno){
    *dest = source;
  }else{
    *dest = result;
  }

  return result;
}    

/**
* Set_number attempts to write a 32-bit number to a ptraced process' memory.
* 
* @param pid             This process id being traced
* @param data            The number to be written to the child's memory
* @param dest            The memory location in the child process to write to
*
* @return                 0 if the number was successfully written
*/
////////////////////////////////////////////////////////////////////////////////
int set_number(int pid, long data, long dest){
////////////////////////////////////////////////////////////////////////////////
  int result = 0;

  result = ptrace(PTRACE_POKEDATA, pid, (char *)dest, data); 

  return result;
}    

/**
* This function prints out a system call.
* 
* @param pid             This process id being traced
* @param scno            The number of the system call
* @param args            The arguments of the system call
*
* @return                 0
*/
////////////////////////////////////////////////////////////////////////////////
int print_syscall(int pid, long scno, long *args){
////////////////////////////////////////////////////////////////////////////////
  int i = 0;
  int result = 0;
  result = ptrace(PTRACE_PEEKUSER, pid, EAX*4, 0);
  if(result == -38){
//    return 1;
  }
  fprintf(stderr,"%s(",syscalls[scno].name);
  (syscalls[scno].print_func)(pid, scno, args);
  fprintf(stderr,");");
  fprintf(stderr, " = %d\n", result);
  fflush(0);
  return(0);
}  

/**
* This function determines if a system call is safe
* 
* @param pid             This process id being traced
* @param scno            The number of the system call
* @param args            The arguments of the system call
*
* @return                 0 if the system call is unsafe
*/
////////////////////////////////////////////////////////////////////////////////
int safe_syscall(int pid, long scno, long *args){
////////////////////////////////////////////////////////////////////////////////
  int result = 0;

  result = (syscalls[scno].safe_func)(pid, scno, args);
  if(!result){
    print_syscall(pid, scno, args);
  }
//  print_syscall(pid, scno, args);

  return result;
}  

