#include "common.h"

/* this is specific to linux 2.4 i386 */
const char *signame(int n) {
  static const char *sn[32] = {
    0, "SIGHUP", "SIGINT", "SIGQUIT", "SIGILL", "SIGTRAP", "SIGABRT",
    "SIGBUS", "SIGFPE", "SIGKILL", "SIGUSR1", "SIGSEGV", "SIGUSR2",
    "SIGPIPE", "SIGALRM", "SIGTERM", "SIGSTKFLT", "SIGCHLD", "SIGCONT",
    "SIGSTOP", "SIGTSTP", "SIGTTIN", "SIGTTOU", "SIGURG", "SIGXCPU",
    "SIGXFSZ", "SIGVTALRM", "SIGPROF", "SIGWINCH", "SIGIO", "SIGPWR",
    "SIGSYS" };
  static char buf[16];
  if(n>0 && n<32) return sn[n];
  sprintf(buf, "SIG_%d", n);
  return buf;
}

/* this is specific to linux 2.4 i386 */
const char *scname(int n) {
  static const char *cn[271] = {
    "setup", "exit", "fork", "read", "write", "open", "close", "waitpid", "creat", "link",
    "unlink", "execve", "chdir", "time", "mknod", "chmod", "lchown16", "break", "stat", "lseek",
    "getpid", "mount", "oldumount", "setuid16", "getuid16", "stime", "ptrace", "alarm", "fstat", "pause",
    "utime", "stty", "gtty", "access", "nice", "ftime", "sync", "kill", "rename", "mkdir",
    "rmdir", "dup", "pipe", "times", "prof", "brk", "setgid16", "getgid16", "signal", "geteuid16",
    "getegid16", "acct", "umount", "lock", "ioctl", "fcntl", "mpx", "setpgid", "ulimit", "olduname",
    "umask", "chroot", "ustat", "dup2", "getppid", "getpgrp", "setsid", "sigaction", "sgetmask", "ssetmask",
    "setreuid16", "setregid16", "sigsuspend", "sigpending", "sethostname", "setrlimit", "old_getrlimit", "getrusage", "gettimeofday", "settimeofday",
    "getgroups16", "setgroups16", "old_select", "symlink", "lstat", "readlink", "uselib", "swapon", "reboot", "old_readdir",
    "old_mmap", "munmap", "truncate", "ftruncate", "fchmod", "fchown16", "getpriority", "setpriority", "profil", "statfs",
    "fstatfs", "ioperm", "socketcall", "syslog", "setitimer", "getitimer", "newstat", "newlstat", "newfstat", "uname",
    "iopl", "vhangup", "idle", "vm86old", "wait4", "swapoff", "sysinfo", "ipc", "fsync", "sigreturn",
    "clone", "setdomainname", "newuname", "modify_ldt", "adjtimex", "mprotect", "sigprocmask", "create_module", "init_module", "delete_module",
    "get_kernel_syms", "quotactl", "getpgid", "fchdir", "bdflush", "sysfs", "personality", "afs_syscall", "setfsuid16", "setfsgid16",
    "llseek", "getdents", "select", "flock", "msync", "readv", "writev", "getsid", "fdatasync", "sysctl",
    "mlock", "munlock", "mlockall", "munlockall", "sched_setparam", "sched_getparam", "sched_setscheduler", "sched_getscheduler", "sched_yield", "sched_get_priority_max",
    "sched_get_priority_min", "sched_rr_get_interval", "nanosleep", "mremap", "setresuid16", "getresuid16", "vm86", "query_module", "poll", "nfsservctl",
    "setresgid16", "getresgid16", "prctl", "rt_sigreturn", "rt_sigaction", "rt_sigprocmask", "rt_sigpending", "rt_sigtimedwait", "rt_sigqueueinfo", "rt_sigsuspend",
    "pread", "pwrite", "chown16", "getcwd", "capget", "capset", "sigaltstack", "sendfile", "getpmsg", "putpmsg",
    "vfork", "getrlimit", "mmap2", "truncate64", "ftruncate64", "stat64", "lstat64", "fstat64", "lchown", "getuid",
    "getgid", "geteuid", "getegid", "setreuid", "setregid", "getgroups", "setgroups", "fchown", "setresuid", "getresuid",
    "setresgid", "getresgid", "chown", "setuid", "setgid", "setfsuid", "setfsgid", "pivot_root", "mincore", "madvise",
    "getdents64", "fcntl64", "tux", "security", "gettid", "readahead", "setxattr", "lsetxattr", "fsetxattr", "getxattr",
    "lgetxattr", "fgetxattr", "listxattr", "llistxattr", "flistxattr", "removexattr", "lremovexattr", "fremovexattr", "tkill",
    "sys_239", "futex", "sched_setaffinity", "sched_getaffinity", "set_thread_area", "get_thread_area", "sys_245", "sys_246", "sys_247", "sys_248",
    "sys_249", "sys_250", "sys_251", "exit_group", "sys_253", "sys_254", "sys_255", "epoll_wait", "remap_file_pages", "set_tid_address", "timer_create",
    "timer_settime", "timer_gettime", "timer_getoverrun", "timer_delete", "clock_settime", "clock_gettime", "clock_getres", "clock_nanosleep",
    "statfs64", "fstatfs64", "tgkill"};
  static char buf[16];
  if(n>=0 && n<=270) return cn[n];
  sprintf(buf, "sys_%d", n);
  return buf;
}

void death_by_signal(int sig) {
  char msg[80];
  sprintf(msg, "caught signal %s", signame(sig));
  FATAL(msg);
}
