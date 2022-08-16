#include "common.h"

/* get system call numbers */
#include "syscall.h"

/* get some random flag bits we need */
#include <sys/mman.h>
#include <fcntl.h>

/* a system call which has no effect */
#define NR_invalid 0

#define ALLOW_THREADING 1

/* arbitrary limit on fd numbers which can be written to */
#define WR_FD_MAX 32

/* whether the child has called exec (transferring control to the submission) yet */
int exec_done = 0;

/* how much output the child has generated on each file descriptor */
int total_written[WR_FD_MAX] = { 0 };

set<pid_t> threads;

bool validOpenMPSignal(int sig);

char *child_str(int addr, pid_t childpid) {
  int i, r;
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
  return strstr(f, "/etc/ld") == f ||
         starts_with_dir(f, "/lib") ||
         starts_with_dir(f, "/usr/lib") || strstr(f, "/dev/null") == f;
  /* /etc/mtab and /proc/meminfo are not here because the library recovers fine from not finding them */
}


/* figure out what to do with a system call.  return nonzero if we modified anything.
 *
 * pstate_t has the following relevant fields:
 *
 *   in          --  true if we are entering the system call, false if returning from system
 *   scno        --  which system call
 *   args[0..5]  --  the arguments (only valid when calling)
 *   scret       --  return value (only valid when returning)
 *   sig         --  if nonzero, a signal to be delivered to the child
 */
int syscall_modify(struct pstate_t *ps, pid_t childpid) {

  static int iret;
  int mod = 0;

#define SYS_FAIL(e) do { ps->scno = NR_invalid; iret = -(e); return 1; } while(0);

//@@@ i hate giant case statements, rewrite this with a dispatch table?
  switch(ps->scno) {


    /* we are most likely here because a system call was replaced via SYS_FAIL */
    case NR_invalid:

      if(!ps->in) {
        /* if returning, use the substitute return value set before the call */
        ps->ret = iret;
        return 1;
      } else {
        return 0;
      }


    /* do some checking on these to make sure they are not making writable maps to files
       (which could circumvent our limits on output size?) or marking pages unswappable */
    case NR_old_mmap:
    case NR_mmap2:

      if(ps->in) {
        int prot, flags;

        if(ps->scno == NR_mmap2) {
          prot = ps->arg[2];
          flags = ps->arg[3];
        } else {
          errno = 0;
          prot = ptrace(PTRACE_PEEKDATA, childpid, ps->arg[0]+8, 0);
          if(prot==-1 && errno) {
            /* it was going to fault anyway */
            SYS_FAIL(EFAULT);
          }
          flags = ptrace(PTRACE_PEEKDATA, childpid, ps->arg[0]+12, 0);
          if(flags==-1 && errno) {
            /* it was going to fault anyway */
            SYS_FAIL(EFAULT);
          }
        }

        /* now we have the real values for prot and flags, so inspect them */
        if(flags & MAP_LOCKED) {

          DPRINT("Prot: %i, Flags %i, fd %i\n", prot, flags, ps->arg[4]);

          /* they won't notice */
          flags &= ~MAP_LOCKED;
          mod = 1;
        }
        

        if((prot & PROT_WRITE) && !(flags & MAP_PRIVATE)) {
          DPRINT("Writing, %i, %i, %i\n", ps->arg[0], ps->arg[1], mod);
          /* odds are the process will immediately get a segfault, so return an error instead of masking out PROT_WRITE */
          SYS_FAIL(EACCES);
        }

        if(mod) {
          /* something had to be changed, stuff it back into the arguments */
          if(ps -> scno == NR_mmap2) {
            ps->arg[2] = prot;
            ps->arg[3] = flags;
          } else {
            if(ptrace(PTRACE_POKEUSER, childpid, ps->arg[0]+8, &prot) ||
               ptrace(PTRACE_POKEUSER, childpid, ps->arg[0]+12, &flags))
              FATAL("ptrace(PTRACE_POKEUSER, ...)");
          }
        }
      }

  /* FALL-THROUGH */
    /* system calls around which we check memory usage... exit is here to get one final value at exit */
    case NR_brk: case NR_munmap: case NR_mremap:

      rescan_proc_rss();

      /* pass unmodified (unless an mmap was edited above) */
      return mod;

    case NR_exit:
        DPRINT("removing thread %i\n", childpid);
        threads.erase(childpid);

        rescan_proc_rss();
        return 0;

    /* only the first exec is ok, and what we are really seeing in that case is the new process returning from it */
    case NR_execve:

      if(!exec_done) {
        exec_done = 1;
        return 0;
      } else {
        SYS_FAIL(EPERM);
      }

    /* openmp wants to do stuff with affinity.  I don't want to let it, but looking won't hurt */
    case NR_sched_getaffinity:
        return 0;

    //harmless openmp stuff
    case NR_clock_gettime:  case NR_set_tid_address:
        return 0;


    /* some silliness is permitted here so that abort and unhandled exceptions behave normally */
    case NR_kill: case NR_tkill: case NR_tgkill:

      if(ps->in && (ps->arg[0] != child || ps->arg[1] == SIGTRAP)) {
        /* we can only send signals to ourself, and SIGTRAP might confuse the tracing code...
           using zero as the signal makes kill a nop which still does error checking */
        if(ps->arg[1] >= MAX_NORMAL_SIGNALS) {
            //stupid, let these through
            return 0;
        } else if(ps->arg[1] == SIGPROF) {
            ps->arg[0] = child;
        } else {
            DPRINT("Trying to send signal %i to pid %i, child is %i\n", ps->arg[1], ps->arg[0], child);
            ps->arg[1] = 0;
        }
        return 1;
      } else {
        /* fine otherwise */
        return 0;
      }

    /* limit the amount written */
    case NR_write:

      if(ps->in) {
        int lim;

        if(ps->arg[0] == 11 || ps->arg[0] == 12) {
            //long io return fds, always ok
            return 0;
        }

        DPRINT("write to fd %d of length %d\n", ps->arg[0], ps->arg[2]);
        /* there is no need to check the file descriptor, since the child cannot open files for writing.
           if we don't want it writing somewhere, we don't pass it an open, writable fd.... */

        if(ps->arg[0] < 0 || ps->arg[0] >= WR_FD_MAX) {
          /* pretend it's not open for writing, since we aren't tracking the total on it */
          SYS_FAIL(EBADF);
        }

        if(ps->arg[2] == 0) {
          /* writing nothing is always ok */
          return 0;
        }

        lim = output_byte_limit - total_written[ps->arg[0]];

        if(lim <= 0) {

          /* pretend disk is full */
          SYS_FAIL(ENOSPC);

        } else {

          /* the count argument is actually a size_t, meaning unsigned, so some care is taken... */
          if((size_t)(ps->arg[2]) <= (size_t)(lim)) {

            /* the entire write is ok */
            mod = 0;

          } else {

            /* truncate the request... most likely they will try again with the tail, get ENOSPC, and give up */
            DPRINT("truncating write to %d\n", lim);
            ps->arg[2] = lim;
            mod = 1;
          }

          total_written[ps->arg[0]] += ps->arg[2];
          return mod;

        }
      } else {
        return 0;
      }


    /* unfortunately we have to put up with this from the dynamic linker */
    case NR_open:
      if(ps->in) {
        char *filename = child_str(ps->arg[0],childpid);

        if(!filename) {
          /* it was going to fault anyway */
          SYS_FAIL(EFAULT);
        }

        if(ps->arg[1] & ~(O_RDONLY|O_LARGEFILE)) {
          /* no other modes allowed */
          if(! (strstr(filename, "/dev/null") == filename)) {
            DPRINT("disallowed open of %s for writing\n", filename);
            SYS_FAIL(EACCES);
          }
        }

        if(!approved_path(filename)) {
          /* not a file they should be opening */
          DPRINT("disallowed open of %s\n", filename);
          SYS_FAIL(ENOENT);
        }

        DPRINT("permitted open of %s\n", filename);
      }

      /* arguments were ok, or returning from kernel */
      return 0;


    /* harmless, and the library sometimes calls getrlimit */
    case NR_nanosleep: case NR_time: case NR_times:
    case NR_gettimeofday: case NR_getitimer:
    case NR_getrusage: case NR_old_getrlimit: case NR_getrlimit:

    /* NR_access -> checks for file permissions, should be harmless */
    case NR_access:

        return 0;

    /* necessary */
    case NR_read: case NR_readv: case NR_pread:
        DPRINT("read to fd %d of length %d\n", ps->arg[0], ps->arg[2]);
        return 0;

    /* used by the linker */
    case NR_close: case NR_fstat: case NR_fstat64: case NR_mprotect:

    /* the library might use select to sleep */
    case NR_old_select: case NR_select: case NR_poll:

    /* potentially leaks interesting information, but harmless */
    case NR_olduname: case NR_uname: case NR_newuname: case NR_getcwd:

    /* required by raise, which is used by abort */
    case NR_getpid:

    /* added - rfairfax, more abort stuff */
    case NR_gettid:

    /* all harmless things the library might call */
    case NR_getuid16: case NR_getgid16: case NR_geteuid16: case NR_getegid16:
    case NR_getppid: case NR_getpgrp: case NR_getgroups16: case NR_getpgid:
    case NR_getsid: case NR_getresuid16: case NR_getresgid16:
    case NR_getuid: case NR_getgid: case NR_geteuid: case NR_getegid:
    case NR_getgroups: case NR_getresuid: case NR_getresgid:
    /* Thread-local storage calls; if these fail, then the dynamic linker 
       segfaults when linking a recent glibc.  Relatively harmless. -- Jed Davis */
    case NR_set_thread_area: case NR_get_thread_area:

    /* i'm fairly certain the child will never be *returning* from a signal handler, but just in case... */
    case NR_sigreturn: case NR_rt_sigreturn: case TC_leaving_sigreturn:

    /* futexs are fast userspace mutexs.  should be safe, needed for handling lots of threads */
    case NR_futex:

      /* pass unmodified */
      return 0;

    /* I hate to do this, but it's needed for IPC */
    case NR_pipe:
        return 0;

    case NR_rt_sigsuspend: /* suspends until a signal is received, should be needed */
        return 0;

    case NR_waitpid: /* wait for pid to terminate */
        return 0;

    /* this quits all theads for a process */
    case NR_exit_group:
        if(childpid != child) {
            DPRINT("removing thread %i\n", childpid);
            threads.erase(childpid);
        }

        return 0;

    /* here we need to allow some signal stuff for linux 2.6 to work */
    case NR_rt_sigaction:
        if(ps->in) {
            DPRINT("rt_sigaction: %s, %s, %i\n", signame(ps->arg[0]), signame(ps->arg[1]), MAX_NORMAL_SIGNALS);
            //allow these, needed for IPC
            //crap for OpenMP
            if(ps->arg[0] >= MAX_NORMAL_SIGNALS || validOpenMPSignal(ps->arg[0])) {
                DPRINT("Allowing rt_sigaction: %s, %s\n", signame(ps->arg[0]), signame(ps->arg[1]));
                return 0;
            } else {
                SYS_FAIL(EINTR);
            }   
        }
        
        return 0;

    /* C++ tests would fail until I added this - Steven Fuller */
    case NR_stat64:
      if(ps->in) {
        char *filename = child_str(ps->arg[0],childpid); 

        if(!filename) {
          SYS_FAIL(EFAULT);
        }

        if(!approved_path(filename)) {
          DPRINT("disallowed stat64 of %s\n", filename);
          SYS_FAIL(ENOENT);
        }

        DPRINT("permitted stat64 of %s\n", filename);
      }

      /* arguments were ok, or returning from kernel */
      return 0;

    /* these must be approved for web services to work */
    case NR_socketcall: case NR_rt_sigprocmask:
        return 0;

    /* allowed for threading.  this can be dangerous, but is a necessary evil at this point. */
    case NR_clone:
        if(ALLOW_THREADING) {
            if(!ps->in) {
                DPRINT("Creating new thread, pid is %i\n", ps->ret);
                //we need to attach a ptrace here so that we can monitor security in the new thread
                int pret = ptrace(PTRACE_ATTACH, ps->ret, NULL, NULL);
                DPRINT("Attached to pid %i, ret is %i\n", ps->ret, pret);

                //add the pid to list of known threads
                threads.insert(ps->ret);
            } else {
                //check against the max number of threads, +1 for the parent
                if(threads.size() + 1 >= max_threads) {
                    DPRINT("New thread creation rejected, current threads are %i\n", (threads.size()+1));
                    SYS_FAIL(0);
                }
            }
            
            return 0;
        } else {
            FATAL("Attempting to get around threading, not good");
        }

    /* these are probably safe since all writable fds the child gets have O_APPEND set */
    case NR_lseek: case NR_llseek:
        //return 0;

    /* the library uses these, but when just fetching flags it seems happy enough
       always getting zeros back, and actually validating the arguments is a pain */
    case NR_fcntl: case NR_fcntl64:

    if(ps->in)
      DPRINT("fcntl(%d %d %d)\n", ps->arg[0], ps->arg[1], ps->arg[2]);

    //here we let people open our magic fds, 11 and 12
   
    if(((ps->arg[0] == 11) || (ps->arg[0] == 12))) {
        if(ps->in)
            DPRINT("Allowing fcntl on %d\n", ps->arg[0]);

        return 0;
    }

    /* the linker uses this to report errors; enable it only for debugging.
       it is conceivable that libraries would use this, but not observed */
    case NR_writev:

    /* everything which is not explicitly approved */
    default:
      DPRINT("Unrecognized system call %d\n", ps->scno);

      if(ps->in) {

        /* pretend success... returning ENOSYS or killing with SIGSYS are also reasonable ideas */
        SYS_FAIL(0);

      } else {

        /* we shouldn't be here, panic... the FATAL macro kills the child immediately */
        FATAL("child returned from unauthorized system call!");
      }
  }
}

bool validOpenMPSignal(int sig) {
    switch(sig) {
        case SIGABRT: case SIGHUP: case SIGINT: case SIGQUIT: case SIGILL:
        case SIGBUS: case SIGSEGV: case SIGSYS: case SIGTERM: case SIGPIPE:
        case SIGFPE:
            return true;

        default:
            return false;
    }
}
