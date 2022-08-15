/*
 * Copyright (C) 2006 - 2014 TopCoder Inc., All Rights Reserved.
 */

/*
 * <p>
 * Changes in version 1.1 (TopCoder Competition Engine - Support Large Memory Limit Settings):
 * <ol>
 *      <li>Update {@link #childInit()} method to support large memory.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.2 (Return Peak Memory Usage for Marathon Match Cpp v1.0):
 * <ol>
 *      <li>Update {@link #exit(bool early)} method to write peak memory used.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.3 (Module Assembly - Return Peak Memory Usage for Marathon Match - DotNet):
 * <ol>
 *      <li>Update {@link #exit(bool early)} method to write long value of peak memory used.</li>
  * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.4 (TopCoder Competition Engine - Stack Size Configuration For MM Problems v1.0):
 * <ol>
 *      <li>Updated {@link #childInit()} method to support stack limit.</li>
 *      <li>Added setrlimit() error handling to {@link #childInit()} method.</li>
 * </ol>
 * </p>
 *
 * @author rfairfax, dexy, savon_cn, Selena
 * @version 1.4
 */
#include "initializer.h"

#include <cstdio>
#include <sys/time.h>
#include <signal.h>
#include <fcntl.h>
#include <unistd.h>
#include <sys/resource.h>
#include <iostream>
#include <sstream>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <netinet/tcp.h>
#include <stdlib.h>

using namespace std;

class BaseLongInitializer;
void catch_prof(int sig);
void catch_winch(int sig);
void catch_alarm(int sig);

BaseLongInitializer* obj;

class BaseLongInitializer : public Initializer {
    public:
        Configuration* config;
        void FATAL(const char* c) {
            config->log(c);
            exit(EXIT_FAILURE);
        }
        
        int sock;
        int sock2;
        int child_two_pipes[2];
        pid_t child_two;
        int cpu_msec_limit;
        
        /* for socket creation */
        void
        init_sockaddr (struct sockaddr_in *name,
                       const char *hostname,
                       uint16_t port)
        {
          struct hostent *hostinfo;

          name->sin_family = AF_INET;
          name->sin_port = htons (port);
          hostinfo = gethostbyname (hostname);
          if (hostinfo == NULL)
            {
              fprintf (stderr, "Unknown host %s.\n", hostname);
              exit (EXIT_FAILURE);
            }
          name->sin_addr = *(struct in_addr *) hostinfo->h_addr;
        }

        
        virtual void start_timer(int cputimeoutval, int walltimeoutval) {
            //Subclasses must implement it
        }
        
        virtual ~BaseLongInitializer() {
            config->log("Destroy\n");
        }
        
        int started;
        int delayed;
        int timeLeft;

        virtual void Setup(Configuration* c) {
            config = c;
            obj = this;
            started = 0;
        }
        
        virtual void init() {
              config->log("Long Init\n");
              
              //figure out base dir for python
              string c = config->get(CHILD_ARGS);
              if(c != "") {
                  // we only need to handle the last child args as base dir prototype
                  size_t pos1 = c.rfind(";");
                  if(pos1 != string::npos) {
                      c = c.substr(pos1+1,c.length()-pos1-1); 
                  }

                  if(c.rfind("/") != string::npos) {
                    c = c.substr(0, c.rfind("/"));
                    config->set(BASE_DIR, c);
                  }
              }
              
              //now for some fun.  We need to spawn yet another child process to act as the
              //middleman so that it can intercept timeout messages and set timers which then
              //cause SIGALRM to raise in child2, which should then signal child1, which will
              //cause a bubble signal to the parent.  First we'll need to setup some pipes.
              if(pipe(child_two_pipes)) {
                FATAL("Piping Failed");
              }
              //TODO: We need to establish a socket connection here
              sock = socket(PF_INET, SOCK_STREAM, 0);
              if(sock < 0) {
                FATAL("Creating Socket");
              }
              
              sock2 = socket(PF_INET, SOCK_STREAM, 0);
              if(sock2 < 0) {
                FATAL("Creating Socket");
              }
              
              int flag = 1;
              int result = setsockopt(sock,            /* socket affected */
                                     IPPROTO_TCP,     /* set option at TCP level */
                                     TCP_NODELAY,     /* name of option */
                                     (char *) &flag,  /* the cast is historical
                                                             cruft */
                                     sizeof(int));    /* length of option value */ 
              result = setsockopt(sock2,            /* socket affected */
                                     IPPROTO_TCP,     /* set option at TCP level */
                                     TCP_NODELAY,     /* name of option */
                                     (char *) &flag,  /* the cast is historical
                                                             cruft */
                                     sizeof(int));    /* length of option value */

              
              int port = config->getInt(PORT);

              struct sockaddr_in servername;
              init_sockaddr(&servername, "localhost", port);

              if(0 > connect(sock, (struct sockaddr *) &servername, sizeof (servername))) {
                FATAL("Couldn't connect to server");
              }
              
              init_sockaddr(&servername, "localhost", port+1);

              if(0 > connect(sock2, (struct sockaddr *) &servername, sizeof (servername))) {
                FATAL("Couldn't connect to server");
              }
        }

        /*
         * Initializes child process.
         */
        virtual void childInit() {
              config->log("Long Child Init\n");
              
              string resultdir = config->get(RESULTS_DIR);
              
              int fd;

                //move socket to somewhere we can get at it later (12)
                fd = dup2(sock, 12);
                if(fd != 12)
                  FATAL("dup2 from sock");
                
                fd = dup2(sock2, 13);
                if(fd != 13)
                  FATAL("dup2 from sock");

                /* this way main doesn't have to clean it up */
                //fcntl(fd, F_SETFD, FD_CLOEXEC);

                fclose(stdout);

            //    stdout = fdopen(12, "w");
            //    setvbuf(stdout, 0, _IONBF, 0);

                //close stderr
                fclose(stderr);

                //open up redirects
                if(1 != open("stdout", O_WRONLY|O_CREAT|O_TRUNC|O_APPEND, 0600))
                  FATAL("open stdout for child");

                if(2 != open("stderr", O_WRONLY|O_CREAT|O_TRUNC|O_APPEND, 0600))
                  FATAL("open stderr for child");

                stdout = fdopen(1, "w");
                setvbuf(stdout, 0, _IONBF, 0);

                stderr = fdopen(2, "w");
                setvbuf(stderr, 0, _IONBF, 0);

                //shutdown the existing stdin, so we can move stdin
                fclose(stdin);

                //move pipe to somewhere we can get at it (11)
                fd = dup2(child_two_pipes[0], 11);
                if(fd != 11) {
                    FATAL("Couldn't redirect stdin");
                }
                close(child_two_pipes[0]);

              /* use interval timers to receive a signal and exit after some time has passed */
              int wall_msec_limit = config->getInt(WALL_TIMEOUT);
              cpu_msec_limit = config->getInt(CPU_TIMEOUT);

              start_timer(cpu_msec_limit, wall_msec_limit);

            /*
            rlimit means resource limit.  these are values you can set per process
            that represent kernel-imposed limitations.  linux defines these:

            RLIMIT_CPU      // CPU time in ms
            RLIMIT_FSIZE    // Maximum filesize
            RLIMIT_DATA     // max data size
            RLIMIT_STACK    // max stack size
            RLIMIT_CORE     // max core file size
            RLIMIT_RSS      // max resident set size
            RLIMIT_NPROC    // max number of processes
            RLIMIT_NOFILE   // max number of open files
            RLIMIT_MEMLOCK  // max locked-in-memory address space
            RLIMIT_AS       // address space limit
            RLIMIT_LOCKS    // maximum file locks held

            the comments above are from the kernel headers.  some of these comments
            are wrong... figuring out the real units for some of the numbers above
            is not easy.  most of the ones you would expect to be in pages are
            actually in bytes; cpu is actually in seconds, and limits combined user
            and system time.

            stack is not terribly useful for our purposes.  data and rss don't seem
            to do anything useful in 2.4.  locks, memlock, nofile and nproc should
            all be unnecessary, since the tracing should prohibit any use of those.
            cpu doesn't have exactly the behavior we want, or enough precison to be
            more than a failsafe.

            this leaves us with fsize, as, and core.  as seems to work correctly as
            a way of limiting memory use, if you fudge about 2 meg to account for
            mapping libs.  fsize does exactly what we want for limiting output, but
            only on regular files, not pipes or sockets.  and fsize also constrains
            core, even if core is set higher.

            so what i do here is set as and core, and replicate fsize by hand later.
            */

            /* change resource limits for the child */
            {
                int setrlimit_ret;

                #if __WORDSIZE == 64
                    struct rlimit64 rl;
                    int64 memory_byte_limit = config->getInt64(MEMORY_LIMIT);
                    int64 stack_byte_limit = config->getInt64(STACK_LIMIT);
                #else
                    struct rlimit rl;
                    int memory_byte_limit = config->getInt(MEMORY_LIMIT);
                    int stack_byte_limit = config->getInt(STACK_LIMIT);
                #endif

                /* address space; apparently the only reliable way to limit memory use in linux,
                  as the other rlimit fields (except stack) don't seem to have any effect in 2.4 */
                rl.rlim_cur = rl.rlim_max = memory_byte_limit;
                #if __WORDSIZE == 64
                    setrlimit_ret = setrlimit64(RLIMIT_AS, &rl);
                #else
                    setrlimit_ret = setrlimit(RLIMIT_AS, &rl);
                #endif
                if (setrlimit_ret)
                    FATAL("setrlimit for RLIMIT_AS");

                /* Stack size. If <= 0, stack size limit will not be set, so default will be used. */
                if (stack_byte_limit > 0)
                {
                    rl.rlim_cur = rl.rlim_max = stack_byte_limit;
                    #if __WORDSIZE == 64
                        setrlimit_ret = setrlimit64(RLIMIT_STACK, &rl);
                    #else
                        setrlimit_ret = setrlimit(RLIMIT_STACK, &rl);
                    #endif
                    if (setrlimit_ret)
                        FATAL("setrlimit for RLIMIT_STACK");
                }

                /* core dump size; a core is basically one page of status info plus whatever the
                   process had mapped, so making this slightly larger than the memory limit seems
                   appropriate.  (smaller might work too, i think linux doesn't dump code pages.
                   it really doesn't matter much because of the limit above.)  we have to set it
                   to something if we want cores, the default is frequently 0 meaning no cores. */

                rl.rlim_cur = rl.rlim_max = 0;
                #if __WORDSIZE == 64
                    setrlimit_ret = setrlimit64(RLIMIT_CORE, &rl);
                #else
                    setrlimit_ret = setrlimit(RLIMIT_CORE, &rl);
                #endif
                if (setrlimit_ret)
                    FATAL("setrlimit for RLIMIT_CORE");
            }

            /* doing setuid(nobody) here might be cool, as would chroot, but we probably don't start as root...
               should the wrapper be suid root? */
        }
        
        virtual void postChildInit() {
            config->log("Long Post Child Init\n");
            
            if( ! (child_two = fork()) ) {

                if(child_two == -1)
                    FATAL("fork");

                //child tmaybewo writes to the 1 end of the pipe
                close(child_two_pipes[0]);

                //we'll need close stdout first
                fclose(stdout);

                int fd;

                fd = dup2(child_two_pipes[1], STDOUT_FILENO);
                if(fd != STDOUT_FILENO) {
                    FATAL("Couldn't redirect stdout");
                }
                close(child_two_pipes[1]);

                stdout = fdopen(fd, "w");

                //redirect stdin from the socket
                fclose(stdin);
                fd = dup2(sock, STDIN_FILENO);
                if(fd != STDIN_FILENO) {
                    FATAL("Couldn't redirect stdin");
                }

                stdin = fdopen(fd, "r");

                fclose(stderr);
                fd = dup2(sock, STDERR_FILENO);
                if(fd != STDERR_FILENO) {
                    FATAL("Couldn't redirect stdin");
                }

                stderr = fdopen(fd, "w");


                //setup a signal handler to trap SIGALRM
                signal(SIGALRM, catch_alarm);
                signal(SIGPROF, catch_prof);
                signal(SIGXFSZ, catch_winch);

                //time to spin like crazy
                while(true) {
                    int command = getc(stdin);
                    {
                    stringstream s;
                    s << "command#BaseLong: " << ABORT<<endl;      
                    s << "command#BaseLong: " << TIMEOUT<<endl;   
                    s << "command#BaseLong: " << command<<endl;   
                    config->log(s.str());
                    }
                    if( command == ABORT) {
                        kill(config->getInt(CHILD_PID), SIGABRT);
                        break;
                    } else if ( command == TIMEOUT) {
                        cpu_msec_limit = getInt2(stdin);
#ifdef DEBUG
                        stringstream s;
                        s << "Got new timeout: " << cpu_msec_limit << endl;
                        config->log(s.str());
#endif
                    } else {
                        ungetc(command,stdin);
                        forward(stdin,stdout);
                        fflush(stdout);
                    }
                }

                FATAL("exec");
              }

              close(child_two_pipes[0]);
              close(child_two_pipes[1]);

              fclose(stdout);

              int fd;

              fd = dup2(sock, STDOUT_FILENO);
              if(fd != STDOUT_FILENO) {
                  FATAL("Couldn't redirect stdout");
              }

              stdout = fdopen(fd, "w");
        }
        /**
         * Exit of the main process.
         * @param early if it is exited before child process.
         */
        virtual void exit(bool early) {
            string resultsdir = config->get(RESULTS_DIR);

             if(early) {
               /* early exit due to internal error, stats are not meaningful */
               return;
             }
            
             int child_exit = config->getInt(CHILD_EXIT);
             int usedcpu = config->getInt(USED_CPU);
             
             long long peakMemoryUsed = (long long) config->getInt(MAXMEMUSED);
             writePeakMemoryUsed(stdout, peakMemoryUsed);
             fflush(stdout);

             stringstream s;
             s << "CHILD EXIT: " << child_exit << ":" << usedcpu;
             config->log(s.str());

             if(child_exit < 0) { //segfault, etc.
                early = true;
             }
             if(child_exit == -SIGXCPU) { //timeout
                early = 0;
                fprintf(stdout, "0\n0\n");
             }


             kill(child_two, SIGKILL);
             
             if(early) {
                /* early exit due to internal error, stats are not meaningful */
                fprintf(stdout, "0\n0\n0\n");
                return;
             }
        }
        
        virtual void signaled(int signal, string name) {
            string s = "Process exited due to signal ";
            s += name;

            if(signal == SIGXCPU) //timeout
                s = "Process exceeded the time limit";

            writeException(stdout, &s);
        }
        
        virtual bool receiveSignal(int signal, int pid) {
            if(signal == SIGPROF || signal == SIGXFSZ) {
               kill(child_two, signal);
               return true;
            }
            return false;
        }
        
};

void catch_alarm(int sig) {
    //this means we're out of time so we have to do some output
    writeTime(stderr, obj->cpu_msec_limit);
    fprintf(stderr, "%d\n", obj->cpu_msec_limit);
    fflush(stderr);

    int child = obj->config->getInt(CHILD_PID);
    //propegate signal to child, I love this function name
    kill(child, sig);

    signal(sig, catch_alarm);
}

void catch_prof(int sig) {
    //reset timers
    if(obj->started == 1) {
#ifdef DEBUG
        obj->config->log("pausing timer User\n");
#endif
        obj->start_timer(0,0);
        signal(sig, catch_prof);
        obj->started = 0;
    } else {
#ifdef DEBUG
        obj->config->log("enabling timer User\n");
#endif
        obj->started = 1;
        obj->start_timer(obj->cpu_msec_limit, obj->cpu_msec_limit);
        signal(sig, catch_prof);
    }
}

void catch_winch(int sig) {
    //temporarily stop timers
    
    if(obj->delayed == 1) {
#ifdef DEBUG
        obj->config->log("enabling timer Exposed\n");
#endif
        obj->start_timer(obj->timeLeft,obj->timeLeft);
        signal(sig, catch_winch);
        obj->delayed = 0;
    } else {
#ifdef DEBUG
       obj->config->log("pausing timer Exposed\n");
#endif
        obj->delayed = 1;
        //get timer info
        struct itimerval timeout;
        getitimer(ITIMER_REAL, &timeout);
        obj->start_timer(0,0);

        int t = 0;
        t += (timeout.it_value.tv_sec * 1000);
        t += (timeout.it_value.tv_usec / 1000);
        
        obj->timeLeft = t;
        signal(sig, catch_winch);
    }
}

