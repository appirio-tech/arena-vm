/*
* Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
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
 * this class is used to R language security checker.
 * it will check if the specific syscall is legal.
 * @author TCSASSEMBLER
 * @version 1.0
 */
class RLanguageSecurityChecker : public SecurityChecker {
    private:
        /**
         * the max clone count = 4
         * Rscript Run.R code.R will fork a thread to execute the user code
         * so we must at least allow NR_clone to execute 4 times
         * if more than 4 time detect, we can determine that user code have 
         * the fork program. so we must forbiden it.
         */
        int clone_count;
        /**
         * get the visited filename from NR_open syscall.
         * @param addr the syscall address.
         * @param childpid the forked child pid.
         * @return the filename
         */
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
        /**
         * to check whether the filename path is end with the specific part.
         * @param filename the filename.
         * @param endsPart the end part name.
         * @return true = the filename is end with the part.
         */
        bool isFileEndswithMatch(const char* filename, const char* endsPart) {
          int filename_len = strlen(filename);
          int r_len = strlen(endsPart);
          if (filename_len >= r_len && strcmp(filename + filename_len - r_len, endsPart) == 0) {
            return true;
          }
          return false;
        }
        /**
         * to check whether the filename path is start with the base dir.
         * @param filename the filename.
         * @param endsPart the end part name.
         * @return 0 = not start with
         */
        static int starts_with_dir(const char *f, const char *dir) {
          int len = strlen(dir);
          return (!strncmp(f, dir, len)) && (f[len] == '/' || f[len] == 0);
        }
        
    public:
        /**
         * the deconstructor method.
         */
        virtual ~RLanguageSecurityChecker() {
            config->log("Destroy\n");
        }
        /**
         * set up the configuration.
         * @param c the configuration entity.
         */
        virtual void Setup(Configuration* c) {
            clone_count = 0;
            config = c;
        }
        /**
         * check the syscall is legal.
         * @param ps the pstate_t struct.
         * @param childpid the forked child pid.
         * @return true = the syscall is legal.
         */
        bool SyscallCheck(struct pstate_t *ps, pid_t childpid) {    
            //return true;
            switch(ps->scno) {
                //R language need to execute more than one time
                case NR_execve:
                    return true;
                
                /* this syscall is used to R language */
                #if __WORDSIZE == 64     
                    case NR_wait4: case NR_getdents:
                    case NR_clock_gettime:
                #else
                    case NR_waitpid: case NR_lstat64:
                    case NR_getdents64: 
                #endif
                case NR_pipe: case NR_set_robust_list:
                case NR_set_tid_address:

                return true;

                case NR_clone:
                      clone_count++;
                    if(clone_count<5)
                        return true;
                    else
                        return false;
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
                    
                    if((strstr(filename, "/dev/tty") == filename)) {
                        stringstream s;
                        s << "Allowing " << filename << endl;
                        config->log(s.str());
                        return true;
                    }
                    
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
                        
                        if(f == ".rlc") {
                            stringstream s;
                            s << "Allowing " << filename << endl;
                            config->log(s.str());
                            
                            return true;
                        }
                    }

                    //these two files required by R language, they are unique
                    const char* cc[] = {"/Run.R","/chelper.so"};
                    int len = sizeof(cc)/sizeof(cc[0]);
                    for(int i=0;i<len;i++) {
                      if(isFileEndswithMatch(filename,cc[i])) {
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


/**
 * the extern method to be created by other module.
 */
extern "C" SecurityChecker* create() {
    return new RLanguageSecurityChecker;
}
/**
 * the extern method to be destroyed by other module.
 */
extern "C" void destroy(SecurityChecker* i) {
    delete i;
}
