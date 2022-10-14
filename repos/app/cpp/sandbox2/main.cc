/*
 * Copyright (C) 2006 - 2014 TopCoder Inc., All Rights Reserved.
 */

/**
 * <p>The entry point for the sandbox.</p>
 *
 * <p>
 * Changes in version 1.1 (PoC Assembly - Return Peak Memory Usage for Executing SRM Solution):
 * <ol>
 *     <li> Add {@link #get_memory_usage(pid_t)} to measure memory usage of the process.</li>
 *     <li> Update {@link #do_trace_loop} </li>
 *     <li> Update {@link #main(int, char**)} </li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.2 (TopCoder Competition Engine - Stack Size Configuration For SRM Problems v1.0):
 * <ol>
 *     <li> Updated {@link #usage()} method to support stack limit parameter.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.3 (Python3 Support):
 * <ol>
 *     <li> Updated {@link #main(int, char**)} method to add PYTHONUSERBASE env and thus avoid /etc/passwd access.</li>
 * </ol>
 * </p>
 *
 * @author rfairfax, dexy, Selena, liuliquan
 * @version 1.3
 */

#include <stdlib.h>
#include <string.h>
#include <vector>
#include <utility>
#include <iostream>
#include <fstream>
#include <sstream>

//getpid
#include <unistd.h>
//mkdir
#include <sys/stat.h>
//setvbuf
#include <cstdio>
//ptrace
#include <sys/ptrace.h>
//rusage
#include <sys/resource.h>

//wait4
#include <sys/types.h>
#include <sys/time.h>
#include <sys/resource.h>
#include <sys/wait.h>
#include <sys/user.h>

#include <dlfcn.h>
#include <errno.h>

#include "config/configuration.h"
#include "initializer/initializer.h"
#if __WORDSIZE == 64
#include "syscall64.h"
#else
#include "syscall.h"
#endif
#include "security/SecurityChecker.h"
#include "mutex.h"
#include "logger.h"
#include "cond.h"

using namespace std;

Logger* logger;
Configuration* config;
pid_t child = -1;
struct rusage ru;
struct timeval initTime;
/**
 * maximum memory usage in KB.
 * @since 1.1
 */
int maxMemUsed = -1;
#ifdef TIMED
vector<struct timeval> initCallTime(33000);
#endif
//
// TODO:
//

vector<void*> initModules;
vector<create_init_t*> initModuleCreates;
vector<destroy_init_t*> initModuleDestroys;
vector<Initializer*> initModulePlugins;

vector<void*> securityModules;
vector<create_security_t*> securityModuleCreates;
vector<destroy_security_t*> securityModuleDestroys;
vector<SecurityChecker*> securityModulePlugins;

void cleanup();

#ifdef DEBUG
#define debug(x) do { logger->log(x); logger->flush(); } while(0);
#else
#define debug(x) do { } while(0);
#endif

void FATAL(string s) {
    FATAL(s.c_str());
}

void FATAL(const char* s) {
    logger->log(s);

    for (int i = 0; i < initModulePlugins.size(); i++) {
        initModulePlugins[i]->exit(true);
    }
    //kill child if necessary
    kill(config->getInt(CHILD_PID), SIGKILL);
    cleanup();
    //exit_group(EXIT_FAILURE);
}

void cleanup() {
    debug("Cleaning up");
    for (int i = 0; i < initModulePlugins.size(); i++) {
        initModuleDestroys[i](initModulePlugins[i]);
    }

    for (int i = 0; i < initModules.size(); i++) {
        dlclose(initModules[i]);
    }

    for (int i = 0; i < securityModulePlugins.size(); i++) {
        securityModuleDestroys[i](securityModulePlugins[i]);
    }

    for (int i = 0; i < securityModules.size(); i++) {
        dlclose(securityModules[i]);
    }

    if (config) {
        delete config;
        config = NULL;
    }

    if (logger) {
        delete logger;
        logger = NULL;
    }
}

/*
 * Prints command line usage to user and terminates application with failure.
 */
void usage() {
    fprintf(stderr, "USAGE: sandbox2 [options] executable [child args]\n"
            "\n"
            "options:\n"
            "  --maxcpu <msecs>\n"
            "  --maxmem <bytes>\n"
            "  --maxstack <bytes>\n"
            "  --maxwall <msecs>\n"
            "  --maxwrite <bytes>\n"
            "  --memtrack <0|1>\n"
            "  --stackdump <0|1>\n"
            "\n"
            "<executable> is treated as a file name, and not subject to path searching\n"
            "any options after <executable> are passed to it\n"
            "\n");
    //TODO: FIXME, REDOCUMENT
    //print_status_stdout(1);
    cleanup();
    exit (EXIT_FAILURE);
}

void setup_output() {
    //creates results dir, adds logs
    stringstream s;
    s << "results." << getpid();

    string resultsdir = s.str();
    string cwd = getcwd(NULL, 0);

#ifdef DEBUG
    //resultsdir = "temp";
#endif

    string tempfile = "temp";
    string outputFile = "log";

    mkdir(resultsdir.c_str(), 0700);
    chdir(resultsdir.c_str());

    ofstream temp;
    temp.open(tempfile.c_str());

    logger = new Logger(outputFile);

    temp.close();
    logger->log("Log started");

    config = new Configuration(logger);
    config->set(RESULTS_DIR, resultsdir);
    config->set(BASE_DIR, cwd);
}

/* parse command line arguments
 returns 1 on success,
 0 if there is a problem */
int parse_cmdline_args(int argc, char **argv) {
    int i;

    vector < pair<string, string> > override;

    /* looking for options */
    for (i = 1; i < argc; i++) {

        /* not a switch, we must have reached the subcommand to sandbox */
        if (argv[i][0] != '-')
            break;

        /* is there another argument coming? */
        if (i + 1 >= argc)
            return 0;

        string a = argv[i];
        string b = argv[i + 1];

        a = a.substr(2, a.size());

        override.push_back(make_pair(a, b));

        /* eat the argument too */
        i++;
    }

    if (i >= argc)
        return 0;

    string childpath = "";

    if (argv[i][0] != '/')
        childpath += "../";
    childpath += argv[i];

    string childargs = "";
    for (i = i + 1; i < argc; i++) {
        childargs += argv[i];
        childargs += ";";
    }

    if (childargs != "") {
        childargs = childargs.substr(0, childargs.size() - 1);
    }

    if (!config->Setup(override)) {
        return 0;
    }

    config->set(CHILD_PATH, childpath);
    config->set(CHILD_ARGS, childargs);

    return 1;
}

int child_sig_remap(int sig) {

    switch (sig) {
    /* this can't get here */
    case SIGTRAP:
        return 0;

        /* we should be attached to the tty too, but whatever...
         i think this interacts strangely with ptrace */
    case SIGTSTP:
    case SIGSTOP:
        return 0;

        /* these should core anyway */
    case SIGQUIT:
    case SIGILL:
    case SIGABRT:
    case SIGFPE:
    case SIGSEGV:
    case SIGBUS:
    case SIGSYS:
    case SIGXCPU:
    case SIGXFSZ:
        return sig;

        /* these generally shouldn't happen, but it seems safest to ignore them.
         arguably SIGPIPE should get the child killed as usual... */
    case SIGCONT:
    case SIGCHLD:
    case SIGWINCH:
    case SIGURG:
    case SIGPIPE:
    case SIGIO:
    case SIGTTIN:
    case SIGTTOU:
        return sig;

        /* probably somebody is trying to kill the child, help them */
    case SIGHUP:
    case SIGINT:
    case SIGTERM:
    case SIGPWR:
        return SIGKILL;

        /* these mean expiration of our timer... change them to
         something similar in meaning which will generate a core */
    case SIGALRM:
    case SIGVTALRM:
    case SIGPROF:
        return SIGXCPU;

        /* doesn't core by default for some reason?  note that this means the FPU stack */
    case SIGSTKFLT:
        return SIGSEGV;

        /* random stuff that shouldn't happen, dump core so we get a clue why */
    default:
        return SIGQUIT;
    }
}

long long calcTime(timeval &from, timeval &to) {
    return (to.tv_sec - from.tv_sec) * 1000000 + to.tv_usec - from.tv_usec;
}

void getelapsed(timeval &init, long long maxTime, const string &text) {
    struct timeval t;
    gettimeofday(&t, 0);

    if (calcTime(init, t) > maxTime) {
        stringstream s;
        s << text << " took: " << calcTime(init, t);
        logger->log(s.str());
    }
    init = t;
}

/**
 * Returns the current peak memory usage (in KB) of the process with given
 * process id.
 * It obtains the result by reading the field VmPeak from `/proc/[processPid]/status` file.
 *
 * @param processPid  id of the process for which we measure the memory usage.
 * @return  current memory usage in KB if process is still alive, -1 otherwise.
 * @since 1.1
 */
int get_memory_usage(pid_t processPid) {
    char buffer[64];
    sprintf(buffer, "/proc/%d/status", processPid);
    FILE* fproc = fopen(buffer, "r");
    if (fproc == NULL) {
        return -1;
    }
    int vmPeak = 0;
    while (fgets(buffer, 32, fproc)) {
        if (!strncmp(buffer, "VmPeak:", 7)) {
            sscanf(buffer + 7, "%d", &vmPeak);
        }
    }
    fclose(fproc);
    return vmPeak;
}

void do_trace_loop() {
    pid_t retpid;
    map<int, long> incall;
    map<int, long> last;
    int status = 0;

    struct pstate_t ps;

    int nsyscalls = 0, nfiltered = 0, child_cored = 0, child_exit = 0;
    // current memory usage
    int curMemUsed = -1;
    gettimeofday(&initTime, 0);
    for (;;) {
#ifdef TIMED
        timeval init;
        gettimeofday(&init,0);
#endif
        retpid = wait4(-1, &status, __WALL, &ru);
        // gets current memory usage and updates maximum memory usage statistics.
        curMemUsed = get_memory_usage(child);
        maxMemUsed = max(maxMemUsed, curMemUsed);
#ifdef TIMED
        getelapsed(init, 1000000, "wait");
#endif

        if (retpid == -1) {
            FATAL("wait");

        } else if (retpid == 0) {
            FATAL("wait was not passed WNOHANG but returned 0");

        } else {
#ifdef DEBUG
            {
                stringstream s;
                s << "PID: " << retpid <<"-----------";
                logger->log(s.str());
            }
#endif
            if (WIFEXITED(status)) {
                if (retpid == child) {
                    stringstream s;
                    s << "child exited with status " << WEXITSTATUS(status);
                    logger->log(s.str());
                    break;
                } else {
                    stringstream s;
                    s << "child thread " << retpid << " exited with status " << WEXITSTATUS(status);
                    debug(s.str());
                    continue;
                }
            }

            //this was below
            if (WIFSIGNALED(status)) {
                for (int i = 0; i < initModulePlugins.size(); i++) {
                    Initializer* c = initModulePlugins[i];
                    c->signaled(WTERMSIG(status), signame(WTERMSIG(status)));
                }

                if (child == retpid) {

                    break;
                }
                continue;
            }

            if (WIFSTOPPED(status)) {

                int sig = WSTOPSIG(status);

                /* stopped due to entering or leaving a system call? */
                if (sig == SIGTRAP) {
                    /* note that my structure has extra fields and this doesn't fill them */
                    if (ptrace(PTRACE_GETREGS, retpid, 0, &ps)) {
                        //FATAL("ptrace(PTRACE_GETREGS, ...)");

                        stringstream s;
                        s << "PTRACE_GETREGS  " << retpid << " failed";
                        logger->log(s.str());

                        continue;
                    }

                    if (incall[retpid] && ps.scno != last[retpid]) {
                        /* this can potentially happen if we get confused by a signal or if we see nested system calls...
                         it also happens after a successful exec */
                        stringstream s;
                        s << "not in syscall " << scname(last[retpid]) << " as expected";
                        logger->log(s.str());
                        incall[retpid] = 0;
                    }

                    /* becomes true if entering, false if leaving */
                    incall[retpid] = !incall[retpid];

                    if (incall[retpid]) {
                        nsyscalls++;
                    }

                    /* these may or may not be changed by editing */
                    ps.in = incall[retpid];
                    ps.sig = sig = 0;
                    last[retpid] = ps.scno;

#ifdef TIMED
                    {
                        if (ps.in) {
                            initCallTime[retpid] = init;
                        } else {
                            long long elapsed = calcTime(initCallTime[retpid], init);
                            if (elapsed >= 500000 && ps.scno != NR_read && ps.scno != NR_write && ps.scno != NR_futex && ps.scno != NR_sched_yield) {
                                stringstream s;
                                s << retpid << " started: " << calcTime(initTime, initCallTime[retpid]) << " finished: " << calcTime(initTime, init) << " : SYSCALL TIME: " << " #" << ps.scno << " : " << scname(ps.scno) << " took: " << elapsed << "us";
                                logger->log(s.str());
                            }
                        }
                    }
#endif
#ifdef DEBUG
                    {
                        stringstream s;
                        s << nsyscalls << " " << (ps.in?"entering":"leaving") << " " << scname(ps.scno);
#ifdef TIMED
                        if (!ps.in) {
                            s << " took: " << calcTime(initCallTime[retpid], init) << "us";
                        }
#endif
                        logger->log(s.str());
                    }
#endif

                    for (int i = 0; i < securityModulePlugins.size(); i++) {
                        SecurityChecker* c = securityModulePlugins[i];
                        if (c->SyscallCheck(&ps, retpid)) {
                            // don't write regs back or pick up junk from the struct if nothing changed

                            if (ptrace(PTRACE_SETREGS, retpid, 0, &ps))
                                FATAL("ptrace(PTRACE_SETREGS, ...)");

                            last[retpid] = ps.scno;
                            sig = ps.sig;

                            if (incall[retpid])
                                nfiltered++;

                            break;
                        }
                    }
#ifdef PTRACE_PATCH
                    {
                        stringstream s;
                        s << "ifdef PTRACE_PATCH: " <<endl;
                        logger->log(s.str());
                    }

                    if (ps.scno != 0) {
                        //We will not receive the exit
                        incall[retpid] = 0;
                    }
#endif

                    /* it's a real signal */
                } else {
#ifndef DEBUG
                    if (SIGXFSZ != sig && SIGPROF != sig) {
#endif
                        stringstream s;
#ifdef TIMED
                        s << calcTime(initTime, init) << " ";
#endif
                        s << "child (" << retpid << ") received signal: " << sig << " " << signame(sig);
                        logger->log(s.str());
#ifndef DEBUG
                    }
#endif

                    bool processed = false;
                    for (int i = 0; i < initModulePlugins.size(); i++) {
                        Initializer* c = initModulePlugins[i];
                        if (c->receiveSignal(sig, retpid)) {
                            processed = true;
                            sig = 0;
                            break;
                        }
                    }
                    if (!processed) {
                        /* possibly modify the signal to exit now, leave a core, be ignored, etc */
                        sig = child_sig_remap(sig);
                    }
                }
                if (sig != 0) {
                    stringstream s;
                    s << "tracing with signal " << sig;
                    logger->log(s.str());
                }
#ifdef TIMED
                getelapsed(init,100000,"processing ");
#endif

                if (ptrace(PTRACE_SYSCALL, retpid, 0, sig) < 0) {
                    logger->log("PTRACE_SYSCALL  failed for pid");
                }

#ifdef TIMED
                getelapsed(init, 300000, "ptrace");
#endif
                continue;
            }

            FATAL("cannot make sense of wait status");
        }
    }

    //TODO: fix me to use configurator
    //if(!exec_done)
    //FATAL("child exited early");

    child_exit = WIFSIGNALED(status) ? -WTERMSIG(status) : WEXITSTATUS(status);
    config->setInt(CHILD_EXIT, child_exit);
}

void load_init_module(string s) {
    // load the library
    void* module = dlopen(s.c_str(), RTLD_LAZY);
    if (!module) {
        (*logger) << "Cannot load library " << s << "\n";
        FATAL("library");
    }

    initModules.push_back(module);

    // reset errors
    dlerror();

    // load the symbols
    create_init_t* create_module = (create_init_t*) dlsym(module, "create");
    const char* dlsym_error = dlerror();
    if (dlsym_error) {
        (*logger) << "Cannot load symbol create: " << dlsym_error << "\n";
        FATAL("symbol");
    }

    initModuleCreates.push_back(create_module);

    destroy_init_t* destroy_module = (destroy_init_t*) dlsym(module, "destroy");
    dlsym_error = dlerror();
    if (dlsym_error) {
        (*logger) << "Cannot load symbol destroy: " << dlsym_error << "\n";
        FATAL("library");
    }

    initModuleDestroys.push_back(destroy_module);

    // create an instance of the class
    Initializer* mod = create_module();
    mod->Setup(config);

    initModulePlugins.push_back(mod);
}

void load_security_module(string s) {
    debug("Loading " + s);

    // load the library
    void* module = dlopen(s.c_str(), RTLD_LAZY);
    if (!module) {
        (*logger) << "Cannot load library " << s << "\n";
        FATAL("library");
    }

    securityModules.push_back(module);

    // reset errors
    dlerror();

    // load the symbols
    create_security_t* create_module = (create_security_t*) dlsym(module, "create");
    const char* dlsym_error = dlerror();
    if (dlsym_error) {
        (*logger) << "Cannot load symbol create: " << dlsym_error << "\n";
        FATAL("library");
    }

    securityModuleCreates.push_back(create_module);

    destroy_security_t* destroy_module = (destroy_security_t*) dlsym(module, "destroy");
    dlsym_error = dlerror();
    if (dlsym_error) {
        (*logger) << "Cannot load symbol destroy: " << dlsym_error << "\n";
        FATAL("library");
    }

    securityModuleDestroys.push_back(destroy_module);

    // create an instance of the class
    SecurityChecker* mod = create_module();
    mod->Setup(config);

    securityModulePlugins.push_back(mod);
}

int main(int argc, char** argv) {
    setvbuf(stdout, 0, _IONBF, 0);
    setvbuf(stderr, 0, _IONBF, 0);

    setup_output();

    debug("MAIN STARTED");

    if (!parse_cmdline_args(argc, argv))
        usage();

    //load init modules
    vector < string > s = config->getVector(INIT_MODULES, ",");
    debug("PAST GET");

    for (int i = 0; i < (int) s.size(); i++) {
        load_init_module (s[i]);

    }

    debug("PAST MODULES");

    //load security modules
    s = config->getVector(SECURITY_MODULES, ",");
    for (int i = 0; i < s.size(); i++) {
        load_security_module (s[i]);
    }

    //init calls
    for (int i = 0; i < initModulePlugins.size(); i++) {
        initModulePlugins[i]->init();
    }

    logger->flush();

    debug("MUTEX SETUP");

    stringstream ss;
    ss << "mutex." << getpid();
    debug(ss.str());

    //We set the priority to see if it helps
    setpriority(PRIO_PROCESS, getpid(), -1);

    //setup semaphore
    //Mutex* m = new Mutex(ss.str().c_str(), logger);
    //Cond* c = new Cond(m, logger);

    debug("CONDITION CREATED");

    //fork child
    if (!(child = fork())) {
        //lock
        //m->lock();

        debug("CHILD START");
        /* if we are child or fork fails */

        if (child == -1)
            FATAL("fork");

        debug("BEFORE CHILD INIT");

        //childInit calls
        for (int i = 0; i < initModulePlugins.size(); i++) {
            initModulePlugins[i]->childInit();
            logger->flush();
        }

        debug("AFTER CHILD INIT");

        string childpath = config->get(CHILD_PATH);
        vector < string > childargs = config->getVector(CHILD_ARGS, ";");

        char* childargsptr[childargs.size() + 2];
        childargsptr[0] = (char*) childpath.c_str();
        for (int i = 0; i < childargs.size(); i++) {
            childargsptr[i + 1] = (char*) childargs[i].c_str();
            (*logger) << childargsptr[i] << "\n";
        }
        childargsptr[childargs.size() + 1] = (char*) 0;

        /* try to make the target executable...
         if this fails and it matters, we find out soon enough */
        chmod(childpath.c_str(), 0755);

        /* attempt to attach for tracing */
        if (ptrace(PTRACE_TRACEME, 0, 0, 0))
            FATAL("ptrace");

        //mark that the work has been done by dropping a file "done"
        ofstream temp;
        temp.open("done");
        temp.close();

        //signal completion
        //c->signal();

        //Unlock semaphore.  Because the child hasn't send SIGTRAP yet, our
        //syscall will go through unharassed
        //m->unlock();

        debug("UNLOCKED MUTEX");

        /* run the child */
        {
            /* setting LD_LIBRARY_PATH might be useful... */
            // For python set PYTHONUSERBASE env to BASE_DIR (which is working dir and safe to access),
            // so that python won't need to access /etc/passwd to get user home
            string pythonUserBase = "PYTHONUSERBASE=" + config->get(BASE_DIR);
            char *envp[] = { &pythonUserBase[0], 0 };
            execve(childpath.c_str(), childargsptr, envp);
        }

        (*logger) << "execve failure: " << errno << "\n";
        logger->flush();
        /* the parent will not see the failed exec, but it can tell something went wrong precisely for that reason */
        FATAL("execve");
    }

    debug("POST FORK LOCK");

    //lock semaphore
    //m->lock();
    //debug("DONE LOCKING AFTER FORK");
    struct stat st;
    while ((stat("done", &st)) == -1) {
        //c->wait();
    }
    //m->unlock();
    debug("AFTER FORK LOCK");

    //set child pid
    config->setInt(CHILD_PID, child);

    //postChildInit calls
    for (int i = 0; i < initModulePlugins.size(); i++) {
        initModulePlugins[i]->postChildInit();
    }

    logger->flush();

    //delete c;
    //delete m;

    do_trace_loop();

    //USED_CPU
    int usedcpu = 1000*ru.ru_utime.tv_sec + ru.ru_utime.tv_usec/1000;
    config->setInt(USED_CPU, usedcpu);

    //sets maximum memory usage statistics
    config->setInt(MAXMEMUSED, maxMemUsed);

    for (int i = 0; i < initModulePlugins.size(); i++) {
        initModulePlugins[i]->exit(false);
    }

    cleanup();

    return (EXIT_SUCCESS);
}
