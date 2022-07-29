/*
 * Copyright (C) 2006 - 2014 TopCoder Inc., All Rights Reserved.
 */

/**
 * <p>The initializer for SRM type of contests.</p>
 *
 * <p>
 * Changes in version 1.1 (PoC Assembly - Return Peak Memory Usage for Executing SRM Solution):
 * <ol>
 *     <li> Update {@link #SRMInitializer::exit(bool) to print maximum memory used.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.2 (TopCoder Competition Engine - Support Large Memory Limit Settings):
 * <ol>
 *      <li>Update {@link #childInit()} method to support large memory.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.3 (TopCoder Competition Engine - Stack Size Configuration For SRM Problems v1.0):
 * <ol>
 *      <li>Updated {@link #childInit()} method to support stack limit.</li>
 *      <li>Added setrlimit() error handling to {@link #childInit()} method.</li>
 * </ol>
 * </p>
 *
 * @author rfairfax, dexy, savon_cn, Selena
 * @version 1.3
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
#include <errno.h>
#include <stdlib.h>
using namespace std;

class SRMInitializer: public Initializer {
private:
    Configuration* config;
    void FATAL(const char* c) {
        config->log(c);
        exit (EXIT_FAILURE);
    }

    int movefd(int oldfd, int newfd) {
        int ret = -1;
        int tries = 0;
        while (ret == -1 && tries < 10) {
            tries++;
            ret = dup2(oldfd, newfd);
            if (ret != newfd) {
                if (errno != EBUSY) {
                    config->log("FD IS: ");
                    config->log(ret);
                    config->log("\n");
                    config->log("ERRNO IS: ");
                    config->log(errno);
                    config->log("\n");
                } else {
                    //spin here, multithreaded bug
                    config->log("Spinning on EBUSY\n");
                    struct timespec t = { 0 };
                    t.tv_nsec = 100000000;
                    while (nanosleep(&t, &t) == -1) {
                        continue;
                    }
                    ret = -1;
                }
            }
        }
        return ret;
    }

public:

    virtual ~SRMInitializer() {
        config->log("Destroy\n");
    }

    virtual void Setup(Configuration* c) {
        config = c;
    }

    virtual void init() {
        config->log("SRM Init\n");
    }

    /*
     * Initializes child process.
     */
    virtual void childInit() {
        config->log("SRM Child Init\n");

        string resultdir = config->get(RESULTS_DIR);

        /* arrange for getting data in and out, clean up stray fds */
        {
            int fd;

            /* try to move stderr for ourseful until exec */
            fd = movefd(2, 12);
            if (fd != 12) {
                FATAL("dup2 from stderr");
            }

            config->log("dup2 on 12\n");

            /* this way main doesn't have to clean it up */
            fcntl(fd, F_SETFD, FD_CLOEXEC);
            fclose (stderr);
            stderr = fdopen(fd, "w");
            setvbuf(stderr, 0, _IONBF, 0);

            config->log("flcose(stderr)\n");

            /* every time i use fclose, i am relying on libc to actually close the file descriptor too */
            fclose (stdout);

            close(3); //what is this - logger

            if (1 != open("stdout", O_WRONLY | O_CREAT | O_TRUNC | O_APPEND, 0600))
                FATAL("open stdout for child");

            if (2 != open("stderr", O_WRONLY | O_CREAT | O_TRUNC | O_APPEND, 0600))
                FATAL("open stderr for child");

            if (3 != open("result", O_WRONLY | O_CREAT | O_TRUNC | O_APPEND, 0600))
                FATAL("open result for child");

            config->log("opens\n");

            /* try to move stdin for child... we keep it here so long to occupy fd 0 */
            fd = movefd(0, 10);
            if (fd != 10)
                FATAL("dup2 from stdin");
            fclose (stdin);

            config->log("fclose(stdin\n");

            //if(0 != open("input", O_RDONLY|O_CREAT|O_TRUNC|O_APPEND, 0600))
            //  FATAL("open /dev/null for child");

            //stdin = fdopen(0,"r");
        }

        /* use interval timers to receive a signal and exit after some time has passed */
        {
            struct itimerval walltimeout, cputimeout;

            int wall_msec_limit = config->getInt(WALL_TIMEOUT);
            int cpu_msec_limit = config->getInt(CPU_TIMEOUT);

            /* we only want them to fire once */
            walltimeout.it_interval.tv_sec = 0;
            walltimeout.it_interval.tv_usec = 0;
            cputimeout.it_interval.tv_sec = 0;
            cputimeout.it_interval.tv_usec = 0;

            /* converting from milliseconds */
            walltimeout.it_value.tv_sec = wall_msec_limit / 1000;
            walltimeout.it_value.tv_usec = 1000 * (wall_msec_limit % 1000);
            cputimeout.it_value.tv_sec = cpu_msec_limit / 1000;
            cputimeout.it_value.tv_usec = 1000 * (cpu_msec_limit % 1000);

            /* exit after this much cpu time has been used; the SIGVTALRM is guaranteed to
             arrive one tick late rather than early, so we can use our exact cpu max here */
            setitimer(ITIMER_VIRTUAL, &cputimeout, 0);

            /* exit after this much wall time, just additional paranoia in case a
             syscall somehow blocks and the parent doesn't take the child with it */
            setitimer(ITIMER_REAL, &walltimeout, 0);

            /* nothing else need be done; the default handler for both signals
             delivered by the timers (SIGVTALRM and SIGALRM) is to exit */
        }

        config->log("timers set\n");

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

            //TODO: Change me to 0 when you're done debugging random crash
            rl.rlim_cur = rl.rlim_max = 0; //memory_byte_limit + 1024;
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

        config->log("rlimit set\n");
    }

    virtual void postChildInit() {
        config->log("SRM Post Child Init\n");
    }

    virtual void exit(bool early) {
        string resultsdir = config->get(RESULTS_DIR);
        printf("%d\n", early ? 1 : 0);
        printf("%s\n", resultsdir.c_str());

        if (early) {
            /* early exit due to internal error, stats are not meaningful */
            return;
        }

        int child_exit = config->getInt(CHILD_EXIT);
        int usedcpu = config->getInt(USED_CPU);
        // retrieves maximum memory used in KB
        int maxMemUsed = config->getInt(MAXMEMUSED);
        stringstream s;
        s << "CHILD EXIT: " << child_exit << ":" << usedcpu;
        config->log(s.str());

        // prints the results in fixed order as specified
        printf("%d %d %d %d\n", child_exit, 0, usedcpu, maxMemUsed);
        //printf("%d %d %d\n", nsyscalls, nfiltered, nprocscan);
        printf("%d %d %d\n", 0, 0, 0);
    }

};

extern "C" Initializer* create() {
    return new SRMInitializer;
}

extern "C" void destroy(Initializer* i) {
    delete i;
}

