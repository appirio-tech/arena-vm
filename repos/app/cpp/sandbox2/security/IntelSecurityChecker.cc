#include "SecurityChecker.h"

#include <errno.h>
#include <sys/ptrace.h>
#include <set>
#include <signal.h>
#include <sstream>

using namespace std;

#define SYS_FAIL(e) do { ps->scno = NR_invalid; config->setInt("iret", -(e)); return true; } while(0);

#define NR_invalid 0

class IntelSecurityChecker : public SecurityChecker {
    private:
        set<pid_t> threads;
        
        bool write(struct pstate_t *ps, pid_t childpid) {
              bool mod = false;
              if(ps->in) {
                int lim;

                //might be wrong
                if(ps->arg[0] == 11 || ps->arg[0] == 12) {
                    //long io return fds, always ok
                    return true;
                }
              } else {
                return false;
              }
        }

        bool kill(struct pstate_t *ps, pid_t childpid) {
              int child = config->getInt(CHILD_PID);
              if(ps->in && (ps->arg[0] != child || ps->arg[1] == SIGTRAP)) {
                if(ps->arg[1] == SIGPROF) {
                    ps->arg[0] = child;
                    return true;
                }
              }
              return false;
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

        
    public:
        
        virtual ~IntelSecurityChecker() {
            config->log("Destroy\n");
        }
        
        virtual void Setup(Configuration* c) {
            config = c;
        }
        
        bool SyscallCheck(struct pstate_t *ps, pid_t childpid) {
            int child = config->getInt(CHILD_PID);
            
            switch(ps->scno) {
                case NR_exit:
                    threads.erase(childpid);
                    return false;

                /* openmp wants to do stuff with affinity.  I don't want to let it, but looking won't hurt */
                case NR_sched_getaffinity:
                    return true;

                case NR_sched_setaffinity:
                    if (ps->arg[0] == 0 || ps->arg[0] == child || threads.count(ps->arg[0])>0) {
                       return true;
                    }
                    SYS_FAIL(EPERM);

                //harmless openmp stuff
                case NR_clock_gettime:  case NR_set_tid_address:
                    return true;
                    
                case NR_kill: case NR_tkill: case NR_tgkill:
                    return kill(ps, childpid);
                    
                case NR_write:
                    return write(ps, childpid);
                    
                /* futexs are fast userspace mutexs.  should be safe, needed for handling lots of threads */
                case NR_futex: case NR_set_robust_list: case NR_get_robust_list:

                  /* pass unmodified */
                  return true;
                
                /*I don't like this but... It is necessary. We must try mutexes */
                //case NR_ipc: 
                //     return true;

                /* I hate to do this, but it's needed for IPC */
                case NR_pipe:
                    return true;

                case NR_rt_sigsuspend: /* suspends until a signal is received, should be needed */
                    return true;
                    
                #if __WORDSIZE == 64
                    case NR_waitid:
                #else
                    case NR_waitpid: /* wait for pid to terminate */
                #endif
                    return true;

                /* this quits all theads for a process */
                case NR_exit_group:
                    if(childpid != child) {
                        //DPRINT("removing thread %i\n", childpid);
                        threads.erase(childpid);
                    }

                    return true;
                    
                case NR_rt_sigaction:
                    if(ps->in) {
                        //allow these, needed for IPC
                        //crap for OpenMP
                        if(ps->arg[0] >= MAX_NORMAL_SIGNALS || validOpenMPSignal(ps->arg[0])) {
                            //DPRINT("Allowing rt_sigaction: %s, %s\n", signame(ps->arg[0]), signame(ps->arg[1]));
                            return true;
                        } else {
        	                stringstream s;
                	        s << "Invalid rt_sigaction: " <<  signame(ps->arg[0]) << "," << signame(ps->arg[1]);
                            config->log(s.str());
                            dumpRegs("regs: ", ps);
                            SYS_FAIL(EINTR);
                        }   
                    }

                    return false;
                case NR_sched_yield:
                    return true;    

                 /* allowed for threading.  this can be dangerous, but is a necessary evil at this point. */
                case NR_clone:
                    if(!ps->in) {
                        /*
                        stringstream s;
                        s << "Attached to pid: " << ps->ret << endl;
                        config->log(s.str());
                        */
                        //add the pid to list of known threads
                        threads.insert(ps->ret);
                    } else {
                        //check against the max number of threads, +1 for the parent
                        int max_threads = config->getInt(MAX_THREADS);
                        if(threads.size() + 1 >= max_threads) {
                            config->log("Thread limit exceeded");
                            SYS_FAIL(0);
                        } else {
                            ps->arg[0] |= CLONE_PTRACE;
                        } 
                    }
                    return true;

                default:
                    break;
            }
            
            return false;
        }
};



extern "C" SecurityChecker* create() {
    return new IntelSecurityChecker;
}

extern "C" void destroy(SecurityChecker* i) {
    delete i;
}
