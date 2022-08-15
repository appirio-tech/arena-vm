#include "common.h"
#include <signal.h>
#include "../long_io.cc"

char *progname = "sandbox";
char logstr[80];

/* did we tell the parent what happened? */
int printed_status = 0;

/* settings determined at runtime
   note the defaults here */
int cpu_msec_limit = 2*1000;
int wall_msec_limit = 20*1000;
int memory_byte_limit = 64*1024*1024;
int output_byte_limit = 20000;
int proc_memory_tracking = 0;
int show_crash_stackdump = 0;
int max_threads = 1;
int port = 0;
int sock = 0;

int child_two_pipes[2];

/* results are stored in a unique directory */
char resultdir[80];
//@@@ accept dir as an argument?  LENGTH LIMIT BAD

char *childpath;
char **childargs;

/* some stuff used during tracing */
pid_t child;
pid_t child_two;
pid_t parent;
struct rusage ru;
int procfd;

/* some results */
int nsyscalls, nfiltered, child_exit, child_cored, maxrss, nprocscan, usedcpu;

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


/* we were invoked with arguments that didn't make sense */
void usage() {
  fprintf(stderr,
                  "USAGE: %s [options] executable [child args]\n"
                  "\n"
                  "options:\n"
                  "  --maxcpu <msecs>\n"
                  "  --maxmem <bytes>\n"
                  "  --maxwall <msecs>\n"
                  "  --maxwrite <bytes>\n"
                  "  --memtrack <0|1>\n"
                  "  --stackdump <0|1>\n"
                  "  --maxthreads <threads>\n"
                  "  --port <port>\n"
                  "\n"
                  "<executable> is treated as a file name, and not subject to path searching\n"
                  "any options after <executable> are passed to it\n"
                  "\n"
                , progname);
  print_status_stdout(1);
  exit(EXIT_FAILURE);
}

/* parse command line arguments
   returns 1 on success,
           0 if there is a problem */
int parse_cmdline_args(int argc, char **argv) {
  int i, v;

  /* used by usage */
  if(argc>=1 && argv && argv[0])
    progname = argv[0];

  /* looking for options */
  for(i=1; i<argc; i++) {

    /* not a switch, we must have reached the subcommand to sandbox */
    if(argv[i][0] != '-') break;

    /* is there a numeric argument coming? */
    if(i+1 >= argc || !isdigit(argv[i+1][0]))
      return 0;

    v = atoi(argv[i+1]);

    if(!strcmp(argv[i], "--maxcpu"))
      cpu_msec_limit = v;
    else if(!strcmp(argv[i], "--maxmem"))
      memory_byte_limit = v;
    else if(!strcmp(argv[i], "--maxwall"))
      wall_msec_limit = v;
    else if(!strcmp(argv[i], "--maxwrite"))
      output_byte_limit = v;
    else if(!strcmp(argv[i], "--memtrack"))
      proc_memory_tracking = v;
    else if(!strcmp(argv[i], "--stackdump"))
      show_crash_stackdump = v;
    else if(!strcmp(argv[i], "--maxthreads"))
      max_threads = v;
    else if(!strcmp(argv[i], "--port"))
      port = v;
    else
      return 0;

    /* eat the numeric argument too */
    i++;
  }

  if(i >= argc)
    return 0;

  sprintf(resultdir, "results.%d", getpid());

  /* this always gets used from within resultdir, so prepend "../" unless it is an absolute path */
  childpath = (char*)malloc(strlen(argv[i])+10);
  childpath[0] = 0;
  if(argv[i][0] != '/')
    strcpy(childpath, "../");
  strcat(childpath, argv[i]);

  childargs = argv+i;

  return 1;
}

void start_timer(int cputimeoutval, int walltimeoutval) {
  {
    struct itimerval walltimeout, cputimeout;

    /* we only want them to fire once */
    walltimeout.it_interval.tv_sec = 0;
    walltimeout.it_interval.tv_usec = 0;
    cputimeout.it_interval.tv_sec = 0;
    cputimeout.it_interval.tv_usec = 0;

    /* converting from milliseconds */
    walltimeout.it_value.tv_sec = walltimeoutval/1000;
    walltimeout.it_value.tv_usec = 1000*(walltimeoutval%1000);
    cputimeout.it_value.tv_sec = cputimeoutval/1000;
    cputimeout.it_value.tv_usec = 1000*(cputimeoutval%1000);

    /* exit after this much cpu time has been used; the SIGVTALRM is guaranteed to
       arrive one tick late rather than early, so we can use our exact cpu max here */
    setitimer(ITIMER_VIRTUAL, &cputimeout, 0);

    /* exit after this much wall time, just additional paranoia in case a
       syscall somehow blocks and the parent doesn't take the child with it */
    setitimer(ITIMER_REAL, &walltimeout, 0);

    /* nothing else need be done; the default handler for both signals
       delivered by the timers (SIGVTALRM and SIGALRM) is to exit */
  }
}

/* make the execution environment appropriate for the child process */
void child_env_setup() {

  mkdir(resultdir, 0700);
  chdir(resultdir);
  /* use interval timers to receive a signal and exit after some time has passed */
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
    struct rlimit rl;

   /* address space; apparently the only reliable way to limit memory use in linux,
      as the other rlimit fields (except stack) don't seem to have any effect in 2.4 */
    rl.rlim_cur = rl.rlim_max = memory_byte_limit;
    setrlimit(RLIMIT_AS, &rl);

    /* core dump size; a core is basically one page of status info plus whatever the
       process had mapped, so making this slightly larger than the memory limit seems
       appropriate.  (smaller might work too, i think linux doesn't dump code pages.
       it really doesn't matter much because of the limit above.)  we have to set it
       to something if we want cores, the default is frequently 0 meaning no cores. */
    if(show_crash_stackdump)
      rl.rlim_cur = rl.rlim_max += 1024*1024;
    else
      rl.rlim_cur = rl.rlim_max = 0;
    setrlimit(RLIMIT_CORE, &rl);
  }

  /* doing setuid(nobody) here might be cool, as would chroot, but we probably don't start as root...
     should the wrapper be suid root? */
}

/* get a file descriptor to some information in /proc for our child */
void setup_proc_tracking() {
  char procfile[30];
  sprintf(procfile, "/proc/%d/status", child);
  procfd = open(procfile, O_RDONLY, 0);
  maxrss = nprocscan = 0;
}

/* fetch and compare child memory use from proc filesystem */
void rescan_proc_rss() {
  char buf[4096], *p;
  int len, rss;
  if(procfd < 0)
    return;
  nprocscan++;
  lseek(procfd, 0, SEEK_SET);
  len = read(procfd, buf, sizeof buf);
  if(len < 0 || len >= (int)sizeof buf)
    FATAL("incompatible proc filesystem");
  p = strstr(buf, "VmRSS:");
  if(!p || 1!=sscanf(p, "VmRSS: %d", &rss))
    FATAL("incompatible proc filesystem");
  if(rss > maxrss)
    maxrss = rss;
}

int child_sig_remap(int sig) {

  switch(sig) {
    /* this can't get here */
    case SIGTRAP:
      return 0;

    /* we should be attached to the tty too, but whatever...
       i think this interacts strangely with ptrace */
    case SIGTSTP:
      return 0;

    /* these should core anyway */
    case SIGQUIT: case SIGILL: case SIGABRT: case SIGFPE: case SIGSEGV:
    case SIGBUS: case SIGSYS: case SIGXCPU: case SIGXFSZ:
      return sig;

    /* these generally shouldn't happen, but it seems safest to ignore them.
       arguably SIGPIPE should get the child killed as usual... */
    case SIGCONT: case SIGCHLD: case SIGWINCH: case SIGURG:
    case SIGPIPE: case SIGIO: case SIGTTIN: case SIGTTOU:
    case SIGSTOP:
      return sig;

    /* probably somebody is trying to kill the child, help them */
    case SIGHUP: case SIGINT: case SIGTERM: case SIGPWR:
      return SIGKILL;

    /* these mean expiration of our timer... change them to
       something similar in meaning which will generate a core */
    case SIGALRM: case SIGVTALRM: //case SIGPROF:
      return SIGXCPU;

    case SIGPROF:
      return 0;

    /* doesn't core by default for some reason?  note that this means the FPU stack */
    case SIGSTKFLT:
      return SIGSEGV;

    //what are you sig_32?

    /* random stuff that shouldn't happen, dump core so we get a clue why */
    default:
      if(sig >= MAX_NORMAL_SIGNALS) {
        return sig;
      }
      return SIGQUIT;
  }
}

/* the dirty work... intercept what the child process is doing and modify system calls and signals appropriately */
void do_trace_loop() {

  pid_t retpid;
  map<int, int> incall;
  map<int, int> last;

  int status = 0;

  //int status = 0, incall = 0, last = 0;

  struct pstate_t ps;

  nsyscalls = nfiltered = child_cored = 0;

  for(;;) {

    //-1 == all children
    retpid = wait4(-1, &status, __WALL, &ru);

    if(retpid == -1) {
      FATAL("wait");

    } else if(retpid == 0) {
      FATAL("wait was not passed WNOHANG but returned 0");

    } else {

      if(WIFEXITED(status)) {
        if(retpid == child) {
            DPRINT("child %i exited with status %d\n", retpid, WEXITSTATUS(status));
            break;
        } else {
            DPRINT("child thread %i exited with status %d\n", retpid, WEXITSTATUS(status));
            continue;
        }
      }

      //this was below
      if(WIFSIGNALED(status)) {
        DPRINT("child %i exited due to signal %s\n", retpid, signame(WTERMSIG(status)));

        string s = "Process exited due to signal ";
        s += signame(WTERMSIG(status));

        if(WTERMSIG(status) == SIGXCPU) //timeout
            s = "Process exceeded the time limit";

        writeException(stdout, &s);

        if(child == retpid) {
            child_cored = WCOREDUMP(status);
            if(child_cored)
                DPRINT("CORED FROM CHILD %i", retpid);
            //this is a weird one

            break;
        }
        continue;
      }

      if(WIFSTOPPED(status)) {
        int sig = WSTOPSIG(status);

        /* stopped due to entering or leaving a system call? */
        if(sig == SIGTRAP) {

          //DPRINT("Looking at pid %i\n", retpid);

          /* note that my structure has extra fields and this doesn't fill them */
          if(ptrace(PTRACE_GETREGS, retpid, 0, &ps)) {
            DPRINT("Can't trace %i, errno %i\n", retpid, errno);
            //FATAL("ptrace(PTRACE_GETREGS, ...)");
            //you really shouldn't do this, but what the hell

            //ptrace(PTRACE_SYSCALL, retpid, 0, sig);
            continue;
          }

          if(incall[retpid] && ps.scno != last[retpid]) {
            /* this can potentially happen if we get confused by a signal or if we see nested system calls...
               it also happens after a successful exec */
            DPRINT("not in syscall %s as expected\n", scname(last[retpid]));
            incall[retpid] = 0;
          }

          /* becomes true if entering, false if leaving */
          incall[retpid] = !incall[retpid];

          if(incall[retpid])
            nsyscalls++;

          /* these may or may not be changed by editing */
          ps.in = incall[retpid];
          ps.sig = sig = 0;
          last[retpid] = ps.scno;

          DPRINT("%d (pid: %d) %s %s\n", nsyscalls, retpid, ps.in?"entering":"leaving", scname(ps.scno));

          if(syscall_modify(&ps, retpid)) {
            /* don't write regs back or pick up junk from the struct if nothing changed */

            if(ptrace(PTRACE_SETREGS, retpid, 0, &ps)) {
              DPRINT("Can't setreg trace %i, errno %i\n", retpid, errno);
              //FATAL("ptrace(PTRACE_SETREGS, ...)");
              continue;
            }

            last[retpid] = ps.scno;
            sig = ps.sig;

            if(incall[retpid])
              nfiltered++;
          }

        /* it's a real signal */
        } else {

          DPRINT("child (pid %d) received signal %s\n", retpid, signame(sig));

          //so the timer stops
          if(sig == SIGPROF) {
            kill(child_two, SIGPROF);
          }

          /* possibly modify the signal to exit now, leave a core, be ignored, etc */
          sig = child_sig_remap(sig);
        }

        ptrace(PTRACE_SYSCALL, retpid, 0, sig);
        continue;
      }



      FATAL("cannot make sense of wait status");
    }
  }

  if(!exec_done)
    FATAL("child exited early");

  child_exit = WIFSIGNALED(status) ? -WTERMSIG(status) : WEXITSTATUS(status);
}

/* invoke gdb on a core file to show what happened */
//@@@ this entire function should have been a shell script
void do_gdb_backtrace() {
  int status;
  char buf[80];
  static const char *cmd =
              "set height 0\n"
              "set language c++\n"
              "set pagination off\n"
              "set print array on\n"
              "set print elements 60\n"
              "set print null-stop on\n"
              "set print pretty on\n"
              "set print sevenbit-strings on\n"
              "set print symbol-filename on\n"
              "set verbose on\n"
              "backtrace full 10\n"
              "backtrace full -10\n"
            ;

  if(!fork()) {
    int fd;

    chdir(resultdir);

    fd = open("gdb.cmd", O_WRONLY|O_CREAT|O_TRUNC, 0600);
    if(fd<0)
      FATAL("creating temporary file for gdb commands");
    write(fd, cmd, strlen(cmd));
    close(fd);

    close(1);
    if(1 != open("backtrace", O_WRONLY|O_CREAT|O_TRUNC|O_APPEND, 0600))
      FATAL("creating output file for gdb");
    close(2);
    /* try to catch stderr too */
    dup2(1, 2);
    close(0);

    execlp("gdb", "gdb", childpath, "core", "--batch", "--nx", "--nw", "-x", "gdb.cmd", 0);
  }

  if( ! (wait(&status)>0 && WIFEXITED(status) && 0==WEXITSTATUS(status)) )
    WARN("problem running gdb");

  strcpy(buf, resultdir);
  strcat(buf, "/gdb.cmd");
  unlink(buf);
}

/* results are conveyed to the java side in several ways...
 * stdout should have the first few lines in a standard format
 *   <zero if success, nonzero if internal error>
 *   resultdir
 *   childexit btavail usedcpu maxmem
 *   nsyscalls nfiltered nprocscan
 * anything on stderr is just logging spew, could explain an internal error
 * stdin is left connected to the child on fd 10
 * resultdir/stdout is stdout from the child
 * resultdir/stderr is stderr from the child
 * resultdir/result is the formatted result from the method
 * resultdir/backtrace is the mess from gdb
 */
void print_status_stdout(int early) {

  if(printed_status) {
    /* this would be odd, and harmless, but still... */
    return;
  }

  DPRINT("Child exit is %i\n", child_exit);

  printed_status = 1;

  if(child_exit < 0) { //segfault, etc.
    early = 1;
  }
  if(child_exit == -SIGXCPU) { //timeout
    early = 0;
    fprintf(stdout, "0\n0\n");
  }

  if(early) {
    /* early exit due to internal error, stats are not meaningful */
    fprintf(stdout, "0\n0\n0\n");
    return;
  }
}
/*make sure that the child process ends if the parent does*/
void stop(int signal){
    kill(child,SIGKILL);
    exit(0);
}

void catch_alarm(int sig) {
    //this means we're out of time so we have to do some output
    writeTime(stderr, cpu_msec_limit);
    fprintf(stderr, "%d\n", cpu_msec_limit);
    fflush(stderr);

    //propegate signal to child, I love this function name
    kill(child, sig);

    signal(sig, catch_alarm);
}

int started = 0;

void catch_prof(int sig) {
    //reset timers
    DPRINT("Prof %i", sig);
    if(started == 1) {
        start_timer(0,0);
        signal(sig, catch_prof);
        started = 0;
    } else {
        started = 1;
        start_timer(cpu_msec_limit, cpu_msec_limit);
        signal(sig, catch_prof);
    }
}

int main(int argc, char **argv) {

  /* set output streams from this process to be completely unbuffered,
     to minimize confusing output from parent and child when debugging,
     and let me muck with file descriptors behind libc's back.
     this must be done before any writes are performed on the streams. */
  setvbuf(stdout, 0, _IONBF, 0);
  setvbuf(stderr, 0, _IONBF, 0);

  //signal(SIGTERM, stop);

  /* identify ourselves if something goes wrong */
  sprintf(logstr, "sandbox parent (pid=%d)", getpid());

  /* setup options and get name and args of child process to trace */
  if(!parse_cmdline_args(argc, argv))
    usage();

  /* just in case all the other timeout mechanisms fail :) */
  {
    struct sigaction sa;
    struct itimerval walltimeout;

    sa.sa_handler = death_by_signal;
    sigemptyset(&sa.sa_mask);
    sa.sa_flags = 0;

    sigaction(SIGALRM, &sa, 0);

    walltimeout.it_interval.tv_sec = 0;
    walltimeout.it_interval.tv_usec = 0;
    walltimeout.it_value.tv_sec = wall_msec_limit/1000 + 2;
    walltimeout.it_value.tv_usec = 1000*(wall_msec_limit%1000);

    setitimer(ITIMER_REAL, &walltimeout, 0);
  }

  parent = getpid();

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

  struct sockaddr_in servername;
  init_sockaddr(&servername, "localhost", port);

  if(0 > connect(sock, (struct sockaddr *) &servername, sizeof (servername))) {
    FATAL("Couldn't connect to server");
  }

  if( ! (child = fork()) ) {
    /* if we are child or fork fails */

    if(child == -1)
      FATAL("fork");

    /* not our problem anymore */
    printed_status = 1;

    /* we actually made it into the child, note that for any future errors */
    sprintf(logstr, "sandbox child (pid=%d)", getpid());

    child_env_setup();

    //the child reads from the 0 end of the pipe
    close(child_two_pipes[1]);

    int fd;

    //move socket to somewhere we can get at it later (12)
    fd = dup2(sock, 12);
    if(fd != 12)
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
        //move pipe to somewhere we can get at it (11)
    fd = dup2(child_two_pipes[0], 11);
    if(fd != 11) {
        FATAL("Couldn't redirect stdin");
    }
    close(child_two_pipes[0]);

    //stdin = fdopen(fd, "r");

    /* try to make the target executable...
       if this fails and it matters, we find out soon enough */
    chmod(childpath, 0755);

    /* attempt to attach for tracing */
    if(ptrace(PTRACE_TRACEME, 0, 0, 0))
      FATAL("ptrace(PTRACE_TRACEME, ...)");

    /* run the child */
    {
      /* setting LD_LIBRARY_PATH might be useful... otherwise empty seems best */
      char *emptyenv[1] = { 0 };
      execve(childpath, childargs, emptyenv);
    }

    /* the parent will not see the failed exec, but it can tell something went wrong precisely for that reason */
    FATAL("exec");
  }

  //now we need to spawn our man in the middle child.  it will need to spin and read data forever
  if( ! (child_two = fork()) ) {

    if(child_two == -1)
        FATAL("fork");

    sprintf(logstr, "sandbox reader (pid=%d)", getpid());

    /* not our problem anymore */
    printed_status = 1;

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

    //time to spin like crazy
    while(true) {
        string s = getStringRaw(stdin);

        //check for timeout here, then do some stuff
        if( s == string("@@@@Timeout@@@@")) {
            cpu_msec_limit = getInt(stdin);
        } else {
            //check for method here, start the timer
            if( s == string("@@@@Method@@@@")) {
                //start_timer(cpu_msec_limit, cpu_msec_limit);
                writeArg(stdout,&s);
                flush(stdout);
            } else {
                //write out the line
                fprintf(stdout, "%s\n", s.c_str());
                fflush(stdout);
            }
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


  /* only parent reaches here, and only on success */

  /* open the /proc entry for tracking if enabled */
  procfd = -1;
  maxrss = nprocscan = 0;  /* these get printed regardless, so lets not have trash in them */
  if(proc_memory_tracking)
    setup_proc_tracking();

  /* the heart of the matter */
  do_trace_loop();

  usedcpu = 1000*ru.ru_utime.tv_sec + ru.ru_utime.tv_usec/1000;

  print_status_stdout(0);
  kill(child_two, SIGKILL);
  //close our pipes
  //WARN("Closing Pipes");

  /* if we left a core, perhaps get a stack trace out of it */
  if(child_cored && show_crash_stackdump)
    do_gdb_backtrace();

  close(sock);

  return 0;
}

