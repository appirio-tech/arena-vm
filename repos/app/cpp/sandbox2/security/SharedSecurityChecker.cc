/*
 * Copyright (C) - 2022 TopCoder Inc., All Rights Reserved.
 */
#include "SecurityChecker.h"

#include <errno.h>
#include <sys/ptrace.h>
#include <fcntl.h>
#include <sys/mman.h>
#include <signal.h>
#include <iostream>
#include <sstream>
#include <stdlib.h>
#include <string.h>
using namespace std;

//TODO: iret?
#define SYS_FAIL(e) do { ps->scno = NR_invalid; config->setInt("iret", -(e)); return true; } while(0);
/* a system call which has no effect */
#if __WORDSIZE == 64
    #define NR_invalid 219
#else
    #define NR_invalid 0
#endif

/* arbitrary limit on fd numbers which can be written to */
#define WR_FD_MAX 32

/**
 * Shared security checker.
 *
 * <p>
 * Changes in version 1.1 (Python3 Support):
 * <ol>
 *     <li> Updated {@link #SyscallCheck(struct pstate_t *ps, pid_t childpid)} method to enhance security for NR_open.</li>
 * </ol>
 * </p>
 *
 * @author liuliquan
 * @version 1.1
 */
class SharedSecurityChecker : public SecurityChecker {
    private:
        bool exec_done;
        int total_written[WR_FD_MAX];
    
        void FATAL(const char* c) {
            config->log(c);
            exit(EXIT_FAILURE);
        }
        
        char *child_str(long addr, pid_t childpid) {
          int i;
          long r;
          static char b[256];  /* PATH_MAX is whatever i want it to be :) */
          int *x = (int *)b;

          for(i=0; i<64; i++) {
            r = ptrace(PTRACE_PEEKDATA, childpid, addr+4*i, 0);
            if(r==-1 && errno) {
              return 0;
            }
            x[i] = r;
            
            if(!(r&0xff) || !(r&0xff00) || !(r&0xff0000) || !(r&0xff000000))
              break;
          }
          if(i==64)
            return 0;
          return b;
        }

        static int starts_with_dir(const char *f, const char *dir) {
          int len = strlen(dir);
          return (!strncmp(f, dir, len)) && (f[len] == '/' || f[len] == 0);
        }

        /* predicate for whether a file may be opened by the child */
        int approved_path(const char *f) {
          if(strstr(f, ".."))
            return 0;
          //TODO: load from file
          vector<string> paths = config->getVector(APPROVED_PATH, ",");
          for(int i = 0; i < paths.size(); i++) {
              if(starts_with_dir(f, paths[i].c_str()))
                  return true;
          }
          
          vector<string> files = config->getVector(APPROVED_FILES, ",");
          for(int i = 0; i < files.size(); i++) {
              const char* file = files[i].c_str();
              if(strncmp(f, file, strlen(file) ) == 0) {
                  return true;
              }
          }
          return false;
          /* /etc/mtab and /proc/meminfo are not here because the library recovers fine from not finding them */
        }

        
        bool write(struct pstate_t *ps, pid_t childpid) {
              bool mod = false;
              if(ps->in) {
                long lim;
                
                #if __WORDSIZE == 64
                    if(((ps->rsv0[3] == 11) || (ps->rsv0[3] == 12) || (ps->rsv0[3] == 13))) {
                #else
                    if(((ps->arg[0] == 11) || (ps->arg[0] == 12) || (ps->arg[0] == 13))) {
                #endif   
                    //long io return fds, always ok
                    return 0;
                }

                //DPRINT("write to fd %d of length %d\n", ps->arg[0], ps->arg[2]);
                /* there is no need to check the file descriptor, since the child cannot open files for writing.
                   if we don't want it writing somewhere, we don't pass it an open, writable fd.... */
                #if __WORDSIZE == 64
                    if(ps->rsv0[3] < 0 || ps->rsv0[3] >= WR_FD_MAX) {
                #else
                    if(ps->arg[0] < 0 || ps->arg[0] >= WR_FD_MAX) {
                #endif
                
                  /* pretend it's not open for writing, since we aren't tracking the total on it */
                  SYS_FAIL(EBADF);
                }
                #if __WORDSIZE == 64
                    if(ps->rsv0[1] == 0) {
                #else
                    if(ps->arg[2] == 0) {
                #endif
                
                  /* writing nothing is always ok */
                  return false;
                }
                
                int output_byte_limit = config->getInt(OUTPUT_BYTE_LIMIT);
                #if __WORDSIZE == 64
                    lim = output_byte_limit - total_written[ps->rsv0[3]];
                #else
                    lim = output_byte_limit - total_written[ps->arg[0]];
                #endif
                

                if(lim <= 0) {

                  /* pretend disk is full */
                  SYS_FAIL(ENOSPC);

                } else {

                  /* the count argument is actually a size_t, meaning unsigned, so some care is taken... */
                  #if __WORDSIZE == 64
                    if((size_t)(ps->rsv0[1]) <= (size_t)(lim)) {
                  #else
                    if((size_t)(ps->arg[2]) <= (size_t)(lim)) {
                  #endif

                    /* the entire write is ok */
                    mod = false;

                  } else {

                    /* truncate the request... most likely they will try again with the tail, get ENOSPC, and give up */
                    //DPRINT("truncating write to %d\n", lim);
                    #if __WORDSIZE == 64
                        ps->rsv0[1] = lim;
                    #else
                        ps->arg[2] = lim;
                    #endif
                    
                    mod = true;
                  }
                    #if __WORDSIZE == 64
                        total_written[ps->rsv0[3]] += ps->rsv0[1];
                    #else
                        total_written[ps->arg[0]] += ps->arg[2];
                    #endif
                  
                  return mod;

                }
              } else {
                return false;
              }
        }

        bool kill(struct pstate_t *ps, pid_t fromPid) {
            int child = config->getInt(CHILD_PID);
            #if __WORDSIZE == 64
            long signalNum, toPid, *signalNumPtr;
            if (ps->scno == NR_tgkill) {
                signalNum = ps->rsv0[1];
                toPid = ps->rsv0[2];
                signalNumPtr = &ps->rsv0[1];
                //arg[0] contains tgid
            } else {
                //kill, tgkill
                signalNum = ps->rsv0[2];
                toPid = ps->rsv0[3];
                signalNumPtr = &ps->rsv0[2];
            }
            #else
            int signalNum, toPid, *signalNumPtr;
            if (ps->scno == NR_tgkill) {
                signalNum = ps->arg[2];
                toPid = ps->arg[1];
                signalNumPtr = &ps->arg[2];
                //arg[0] contains tgid
            } else {
                //kill, tgkill
                signalNum = ps->arg[1];
                toPid = ps->arg[0];
                signalNumPtr = &ps->arg[1];
            }
            #endif
            

#ifdef DEBUG            
            if(ps->in) {
                stringstream s;
                s << "Trying to send (" << scname(ps->scno) << ") " << signalNum << " from " << fromPid << " to pid " << toPid  << ", child is " << child << endl;
                config->log(s.str());
                dumpRegs("Trying to send sinal", ps);
            }
#endif
              if(ps->in && (toPid != child || signalNum == SIGTRAP)) {
                /* we can only send signals to ourself, and SIGTRAP might confuse the tracing code...
                   using zero as the signal makes kill a nop which still does error checking */
                if(signalNum >= MAX_NORMAL_SIGNALS) {
                    //stupid, let these through
                    return 0;
              } else {
                    stringstream s;
                    s << "Blocking signal (" << scname(ps->scno) << ") signal:" << signalNum << " from " << fromPid << " to pid " << toPid  << ", child is " << child << endl;
                    config->log(s.str());
                    dumpRegs("Trying to send sinal", ps);
                    *signalNumPtr = 0;
                }
                return true;
              } else {
                /* fine otherwise */
                return false;
              }
        }
    
        bool mmap(struct pstate_t *ps, pid_t childpid) {
            int mod = 0;
            if(ps->in) {
            #if __WORDSIZE == 64
                long prot, flags;
            #else
                int prot, flags;
            #endif
                                
                #if __WORDSIZE == 64
                if(ps->scno == NR_mmap) {
                  prot = ps->rsv0[1];
                  flags = ps->arg[7];
                #else
                if(ps->scno == NR_mmap2) {
                  prot = ps->arg[2];
                  flags = ps->arg[3];
                #endif

                } else {
                  errno = 0;
                #if __WORDSIZE == 64
                prot = ptrace(PTRACE_PEEKDATA, childpid, ps->rsv0[3]+16, 0);
                #else
                prot = ptrace(PTRACE_PEEKDATA, childpid, ps->arg[0]+8, 0);
                #endif
                  
                  if(prot==-1 && errno) {
                    /* it was going to fault anyway */
                    SYS_FAIL(EFAULT);
                  }
                  #if __WORDSIZE == 64
                  flags = ptrace(PTRACE_PEEKDATA, childpid, ps->rsv0[3]+24, 0);
                  #else
                  flags = ptrace(PTRACE_PEEKDATA, childpid, ps->arg[0]+12, 0);
                  #endif
                  if(flags==-1 && errno) {
                    /* it was going to fault anyway */
                    SYS_FAIL(EFAULT);
                  }
                }

                /* now we have the real values for prot and flags, so inspect them */
                if(flags & MAP_LOCKED) {
                  /* they won't notice */
                  flags &= ~MAP_LOCKED;
                  mod = 1;
                }
                

                if((prot & PROT_WRITE) && !(flags & MAP_PRIVATE)) {
                   stringstream s;
                   #if __WORDSIZE == 64
                    s << "Writing, " << ps->rsv0[3] << "," << ps->rsv0[2] << "," << ps->rsv0[1] << endl;
                   #else
                    s << "Writing, " << ps->arg[0] << "," << ps->arg[1] << "," << ps->arg[2] << endl;
                   #endif
                   
                   config->log(s.str());
                  //DPRINT("Writing, %i, %i, %i\n", ps->arg[0], ps->arg[1], mod);
                  /* odds are the process will immediately get a segfault, so return an error instead of masking out PROT_WRITE */
                  SYS_FAIL(EACCES);
                }

                if(mod) {
                  /* something had to be changed, stuff it back into the arguments */
                  
                  
                  #if __WORDSIZE == 64
                  if(ps -> scno == NR_mmap) {
                    ps->rsv0[1] = prot;
                    ps->arg[7] = flags;
                  #else
                  if(ps -> scno == NR_mmap2) {
                    ps->arg[2] = prot;
                    ps->arg[3] = flags;
                  #endif
                  } else {
                  #if __WORDSIZE == 64
                      if(ptrace(PTRACE_POKEUSER, childpid, ps->rsv0[3]+16, &prot) ||
                       ptrace(PTRACE_POKEUSER, childpid, ps->rsv0[3]+24, &flags))
                      FATAL("ptrace(PTRACE_POKEUSER, ...)");
                  #else
                      if(ptrace(PTRACE_POKEUSER, childpid, ps->arg[0]+8, &prot) ||
                       ptrace(PTRACE_POKEUSER, childpid, ps->arg[0]+12, &flags))
                      FATAL("ptrace(PTRACE_POKEUSER, ...)");
                  #endif

                  }
                }
            }
            return mod?true:false;
        }
    
    public:
        
        virtual ~SharedSecurityChecker() {
            config->log("Destroy\n");
        }
        
        virtual void Setup(Configuration* c) {
            exec_done = false;
            config = c;
        }
        
        bool SyscallCheck(struct pstate_t *ps, pid_t childpid) {
            int mod = 0;
            
            switch(ps->scno) {
                /* we are most likely here because a system call was replaced via SYS_FAIL */
                case NR_invalid:
                  if(!ps->in) {
                    /* if returning, use the substitute return value set before the call */
                    int iret = config->getInt("iret");
                    
                    ps->ret = iret;
                    return true;
                  } else {
                    return false;
                  }
                                
                #if __WORDSIZE == 64
                case NR_mmap:
                #else
                case NR_old_mmap:
                case NR_mmap2:
                #endif
                
                    return mmap(ps, childpid);
                    
                case NR_brk: case NR_munmap: case NR_mremap:
                    return false;
                    
                case NR_exit:
                    return false;
                    
                /* only the first exec is ok, and what we are really seeing in that case is the new process returning from it */
                case NR_execve:
                  if(!exec_done) {
                    exec_done = true;
                    return false;
                  } else {
                    SYS_FAIL(EPERM);
                  }
                  
                case NR_kill: case NR_tkill: case NR_tgkill:
                    return kill(ps, childpid);
                    
                case NR_write:
                    return write(ps, childpid);
                    
                /* harmless, and the library sometimes calls getrlimit */
                case NR_nanosleep: case NR_time: case NR_times:
                case NR_gettimeofday: case NR_getitimer:
                case NR_getrusage: 
                #if __WORDSIZE != 64
                    case NR_old_getrlimit: 
                #endif
                case NR_getrlimit:

                /* NR_access -> checks for file permissions, should be harmless */
                case NR_access: case NR_fdatasync: case NR_fsync:
                    return false;
                    
                /* necessary */
                case NR_read: case NR_readv: 
                #if __WORDSIZE != 64
                    case NR_pread:
                #endif
                {
#ifdef DEBUG
                    stringstream s;
                    #if __WORDSIZE != 64
                        s << "read to fd " << ps->rsv0[3] << " length " << ps->rsv0[1] << endl;
                    #else
                        s << "read to fd " << ps->arg[0] << " length " << ps->arg[2] << endl;
                    #endif
                    config->log(s.str());
#endif
                    return false;
                }
                    
                /* used by the linker */
                #if __WORDSIZE == 64
                    case NR_arch_prctl:
                    case NR_stat:
                    case NR_lstat:
                    case NR_writev:
                    case NR_exit_group:
                #endif
                case NR_close: case NR_fstat: 
                #if __WORDSIZE != 64
                    case NR_fstat64: 
                #endif
                case NR_mprotect:

                /* the library might use select to sleep */
                #if __WORDSIZE != 64
                    case NR_old_select: 
                #endif
                case NR_select: case NR_poll:

                /* potentially leaks interesting information, but harmless */
                #if __WORDSIZE != 64
                    case NR_olduname:
                    case NR_newuname:
                #endif
                case NR_uname: case NR_getcwd:

                /* required by raise, which is used by abort */
                case NR_getpid:

                /* added - rfairfax, more abort stuff */
                case NR_gettid:

                /* all harmless things the library might call */         
                #if __WORDSIZE != 64                
                    case NR_getuid16: case NR_getgid16: case NR_geteuid16: case NR_getegid16:
                    case NR_getgroups16: case NR_getresuid16: case NR_getresgid16:
                #endif
                case NR_getppid: case NR_getpgrp:  case NR_getpgid:
                case NR_getsid:
                case NR_getuid: case NR_getgid: case NR_geteuid: case NR_getegid:
                case NR_getgroups: case NR_getresuid: case NR_getresgid:
                /* Thread-local storage calls; if these fail, then the dynamic linker 
                   segfaults when linking a recent glibc.  Relatively harmless. -- Jed Davis */
                case NR_set_thread_area: case NR_get_thread_area:

                /* these must be approved for web services to work */
                #if __WORDSIZE == 64
                    case NR_connect:
                    case NR_sendto:
                #else
                    case NR_socketcall:
                #endif
                
                case NR_rt_sigprocmask:

                case NR_lseek: 
                #if __WORDSIZE != 64    
                    case NR_llseek:
                    case NR_sigreturn:
                #endif
                /* i'm fairly certain the child will never be *returning* from a signal handler, but just in case... */
                 case NR_rt_sigreturn: case TC_leaving_sigreturn:
                /* used in new kernel by libraries */
                case NR_fadvise64: 
                    return false;
                case NR_madvise: 
                    return false;
			    case NR_futex: 
					return false;

                /* here we need to allow some signal stuff for linux 2.6 to work */
                case NR_rt_sigaction:
                    if(ps->in) {
                        //allow these, needed for IPC
                        stringstream s;
                        #if __WORDSIZE == 64
                            s << "rt_sigaction: " << signame(ps->rsv0[3]) << ", " << signame(ps->rsv0[2]) << endl;
                        #else
                            s << "rt_sigaction: " << signame(ps->arg[0]) << ", " << signame(ps->arg[1]) << endl;
                        #endif
                        config->log(s.str());
                        
                        #if __WORDSIZE == 64
                            if(ps->rsv0[3] >= MAX_NORMAL_SIGNALS || ps->rsv0[3] == SIGABRT) {
                        #else
                            if(ps->arg[0] >= MAX_NORMAL_SIGNALS || ps->arg[0] == SIGABRT) {
                        #endif                        
                            config->log("ALLOWING\n");
                            return false;
                        } else {
                            SYS_FAIL(EINTR);
                        }   
                    }

                    return false;
                    
                /* the library uses these, but when just fetching flags it seems happy enough
                   always getting zeros back, and actually validating the arguments is a pain */
                #if __WORDSIZE == 64
                    case NR_fcntl: 
                #else
                    case NR_fcntl64:
                #endif
                    //here we let people open our magic fds, 11 and 12
                    #if __WORDSIZE == 64
                        if(((ps->rsv0[3] == 11) || (ps->rsv0[3] == 12) || (ps->rsv0[3] == 13))) {
                    #else
                        if(((ps->arg[0] == 11) || (ps->arg[0] == 12) || (ps->arg[0] == 13))) {
                    #endif                    
                        return false;
                    } else {
                        SYS_FAIL(0);
                    }
                    
                /* unfortunately we have to put up with this from the dynamic linker */
                case NR_open:
                    
                  if(ps->in) {
                    #if __WORDSIZE == 64
                        char *filename = child_str(ps->rsv0[3],childpid);                   
                    #else
                        char *filename = child_str(ps->arg[0],childpid);
                    #endif

                    if(!filename) {
                        /* it was going to fault anyway */
                        SYS_FAIL(EFAULT);
                    } else if(!openForRead(ps, filename)) {
                        stringstream s;
                        s << "SharedSecurityChecker disallowed open of " << filename << " for writing" << endl;
                        config->log(s.str());
                        SYS_FAIL(EACCES);
                    } else if(!approved_path(filename)) {
                        /* not a file they should be opening */
                        stringstream s;
                        s << "SharedSecurityChecker disallowed open of " << filename << endl;
                        config->log(s.str());
                        SYS_FAIL(ENOENT);
                    } else {
                        stringstream s;
                        s << "SharedSecurityChecker Allowing " << filename << endl;
                        config->log(s.str());
                    }
                  }

                  /* arguments were ok, or returning from kernel */
                  return false;


                /* C++ tests would fail until I added this - Steven Fuller */  
                
                #if __WORDSIZE != 64
                 case NR_stat64:                
                  if(ps->in) {
                    char *filename = child_str(ps->arg[0],childpid); 
                    if(!filename) {
                      SYS_FAIL(EFAULT);
                    }

                    if(!approved_path(filename)) {
                        stringstream s;
                        s << "disallowed fstat64 of " << filename << endl;
                        config->log(s.str());
                        SYS_FAIL(ENOENT);
                    }

                    //DPRINT("permitted stat64 of %s\n", filename);
                  }

                  /* arguments were ok, or returning from kernel */
                  return false;
                #endif
                case NR_ioctl:
                    {
                    stringstream s;
                    #if __WORDSIZE == 64
                        s << "IOCTL: " << ps->rsv0[3] << "," << ps->rsv0[2] << endl;
                    #else
                        s << "IOCTL: " << ps->arg[0] << "," << ps->arg[1] << endl;
                    #endif
                    config->log(s.str());
                    SYS_FAIL(0);
                    return false;
                    }
                
                default:
                    //unhandled. We padded so we can extract 20 more chars
                    stringstream s;
                    s << "Unrecognized system call " << ps->scno << "                     " <<  endl;
                    config->log(s.str());

                    if(ps->in) {
                        /* pretend success... returning ENOSYS or killing with SIGSYS are also reasonable ideas */
                        SYS_FAIL(0);
                    } else {
                        FATAL("child returned from unauthorized system call!");
                    }
                    break;
            }
            
            return false;
        }
};


extern "C" SecurityChecker* create() {
    return new SharedSecurityChecker;
}

extern "C" void destroy(SecurityChecker* i) {
    delete i;
}
