/*
* Copyright (C) - 2013 TopCoder Inc., All Rights Reserved.
*/
#include "SecurityChecker.h"

#include <errno.h>
#include <sys/ptrace.h>
#include <sstream>
#include <string.h>
using namespace std;

#if __WORDSIZE == 64
    #define NR_invalid 219
#else
    #define NR_invalid 0
#endif

#define SYS_FAIL(e) do { ps->scno = NR_invalid; config->setInt("iret", -(e)); return true; } while(0);

/**
 *
 * <p>
 * Changes in version 1.1 (TC Competition Engine - C++ and Python Customization Support for SRM v1.0):
 * <ol>
 *    <li>Update {@link #SyscallCheck(struct pstate_t *ps, pid_t childpid)} method to meet python2.7.5.</li>
 *    <li>Add {NR_set_tid_address}, {NR_getdents} to support python2.7.5 blist. </li>
 *    <li>Update {NR_open} to allow customization python install. </li>
 * </ol>
 * </p>
 * @author TCSASSEMBLER
 * @version 1.1
 */
class PythonSecurityChecker : public SecurityChecker {
    private:
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
            if(!(r&0xff) || !(r&0xff00) || !(r&0xff0000) || !(r&0xff000000)) {
                break;
            }
          }
          if(i==64)
            return 0;
          return b;
        }
        
        static int starts_with_dir(const char *f, const char *dir) {
          int len = strlen(dir);
          return (!strncmp(f, dir, len)) && (f[len] == '/' || f[len] == 0);
        }
        
    public:
        
        virtual ~PythonSecurityChecker() {
            config->log("Destroy\n");
        }
        
        virtual void Setup(Configuration* c) {
            config = c;
        }
        
        bool SyscallCheck(struct pstate_t *ps, pid_t childpid) {          
            //return true;
            switch(ps->scno) {
                /* futexs are fast userspace mutexs.  should be safe, needed for handling lots of locks*/
                case NR_futex:

                  /* pass unmodified */
                  return true;
                
                /* add to support python2.7.5 blist */
                case NR_set_tid_address: case NR_getdents:
                  return true;

                case NR_readlink:
                    //safe, just gets link pointer
                    return true;
                    
                case NR_ioctl:
                    {
                    #if __WORDSIZE == 64
                        if(ps->rsv0[3] == 0 || ps->rsv0[3] == 1) {
                            if(ps->rsv0[2] = 21505) {
                    #else
                        if(ps->arg[0] == 0 || ps->arg[0] == 1) {
                            if(ps->arg[1] = 21505) {
                    #endif                        
                                //TCGETS
                                return true;
                            }
                                
                        }
                    return false;
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
                    }

                    if((strstr(filename, "../Wrapper.pyc") == filename)) {
                        return true;
                    }

                    /**
                     * allow customization python install to visit it's own site_packages and libs
                     */
                    vector<string> paths = config->getVector(APPROVED_PATH, ",");
                    for(int i = 0; i < paths.size(); i++) {
                        if(starts_with_dir(filename, paths[i].c_str())) {
                            stringstream s;
                            s << "Allowing " << filename << endl;
                            config->log(s.str());
                            return true;
                        }
                    }
                    string base = config->get(BASE_DIR);
                    if(starts_with_dir(filename, base.c_str())) {
                        string f = filename;
                        f = f.substr(f.size()-4, f.size());
                        
                        if(f == ".pyc") {
                            stringstream s;
                            s << "Allowing " << filename << endl;
                            config->log(s.str());
                            
                            return true;
                        }
                    }

                    //DPRINT("permitted open of %s\n", filename);
                  }

                  /* arguments were ok, or returning from kernel */
                  return false;
                    
                default:
                    break;
            }
            
            return false;
        }
};



extern "C" SecurityChecker* create() {
    return new PythonSecurityChecker;
}

extern "C" void destroy(SecurityChecker* i) {
    delete i;
}
