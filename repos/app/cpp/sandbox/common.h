/* pick features */
#ifndef _GNU_SOURCE
#define _GNU_SOURCE
#endif
#define _ISOC99_SOURCE

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>

#include <unistd.h>
#include <errno.h>
#include <fcntl.h>
#include <signal.h>
#include <sys/types.h>
#include <sys/time.h>
#include <sys/stat.h>
#include <sys/wait.h>
#include <sys/resource.h>
#include <sys/ptrace.h>

#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>

//adding some STL for the sake of maintainability
#include <map>
#include <string>
#include <set>
using namespace std;

/* something unexpected but recoverable has occurred... print a warning */
#define WARN(msg) \
  do { \
    fprintf(stderr, "WARN at %s:%d -- %s: %s (errno=\"%s\")\n", __FILE__, __LINE__, logstr, (msg), strerror(errno)); \
  } while(0);

/* something has gone horribly wrong... perror and terminate,
   making sure the java side sees enough to know what happened */
#define FATAL(msg) \
  do { \
    fprintf(stderr, "FATAL at %s:%d -- %s: %s (errno=\"%s\")\n", __FILE__, __LINE__, logstr, (msg), strerror(errno)); \
    ptrace(PTRACE_KILL, child, 0, 0); \
    print_status_stdout(1); \
    exit(EXIT_FAILURE); \
  } while(0);

#ifdef DEBUG
#define DPRINT(fmt, args...)    do { fprintf(stderr, fmt, args); } while(0);
#else
#define DPRINT(fmt, args...)    do { } while(0);
#endif

#define MAX_NORMAL_SIGNALS 32

/* an extension to user_regs_struct from <sys/user.h>
   linux specific, naturally */
struct pstate_t {
  int arg[6];     /* system call arguments, if entering system */
  int ret;        /* return value of the system call, if leaving system */
  int rsv0[4];
  int scno;       /* system call number, if entering system */
  int pc;         /* program counter (eip) */
  int rsv1[4];
  int in;         /* true if entering system */
  int sig;        /* if nonzero, signal to deliver to child */
};

/* from main.c */
extern char *progname;
extern char logstr[];
extern pid_t child;
extern int output_byte_limit;
extern void rescan_proc_rss();
extern void print_status_stdout(int);
extern int max_threads;

/* from util.c */
extern const char *signame(int);

extern const char *scname(int);
extern void death_by_signal(int);

/* from system.c */
extern int exec_done;
extern int syscall_modify(struct pstate_t *, pid_t childpid);
