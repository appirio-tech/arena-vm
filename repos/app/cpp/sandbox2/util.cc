#include<cstdio>

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

/* this is specific to linux 2.6 i386 */
const char *scname(int n) {
#if __WORDSIZE == 64
    static const char *cn[324] = {
    "read","write","open","close","stat","fstat","lstat","poll","lseek","mmap",
    "mprotect","munmap","brk","rt_sigaction","rt_sigprocmask","rt_sigreturn","ioctl","pread64","pwrite64","readv",
    "writev","access","pipe","select","sched_yield","mremap","msync","mincore","madvise","shmget",
    "shmat","shmctl","dup","dup2","pause","nanosleep","getitimer","alarm","setitimer","getpid",
    "sendfile","socket","connect","accept","sendto","recvfrom","sendmsg","recvmsg","shutdown","bind",
    "listen","getsockname","getpeername","socketpair","setsockopt","getsockopt","clone","fork","vfork","execve",
    "exit","wait4","kill","uname","semget","semop","semctl","shmdt","msgget","msgsnd",
    "msgrcv","msgctl","fcntl","flock","fsync","fdatasync","truncate","ftruncate","getdents","getcwd",
    "chdir","fchdir","rename","mkdir","rmdir","creat","link","unlink","symlink","readlink",
    "chmod","fchmod","chown","fchown","lchown","umask","gettimeofday","getrlimit","getrusage","sysinfo",
    "times","ptrace","getuid","syslog","getgid","setuid","setgid","geteuid","getegid","setpgid",
    "getppid","getpgrp","setsid","setreuid","setregid","getgroups","setgroups","setresuid","getresuid","setresgid",
    "getresgid","getpgid","setfsuid","setfsgid","getsid","capget","capset","rt_sigpending","rt_sigtimedwait","rt_sigqueueinfo",
    "rt_sigsuspend","sigaltstack","utime","mknod","uselib","personality","ustat","statfs","fstatfs","sysfs",
    "getpriority","setpriority","sched_setparam","sched_getparam","sched_setscheduler","sched_getscheduler","sched_get_priority_max","sched_get_priority_min","sched_rr_get_interval","mlock",
    "munlock","mlockall","munlockall","vhangup","modify_ldt","pivot_root","_sysctl","prctl","arch_prctl","adjtimex",
    "setrlimit","chroot","sync","acct","settimeofday","mount","umount2","swapon","swapoff","reboot",
    "sethostname","setdomainname","iopl","ioperm","create_module","init_module","delete_module","get_kernel_syms","query_module","quotactl",
    "nfsservctl","getpmsg","putpmsg","afs_syscall","tuxcall","security","gettid","readahead","setxattr","lsetxattr",
    "fsetxattr","getxattr","lgetxattr","fgetxattr","listxattr","llistxattr","flistxattr","removexattr","lremovexattr","fremovexattr",
    "tkill","time","futex","sched_setaffinity","sched_getaffinity","set_thread_area","io_setup","io_destroy","io_getevents","io_submit",
    "io_cancel","get_thread_area","lookup_dcookie","epoll_create","epoll_ctl_old","epoll_wait_old","remap_file_pages","getdents64","set_tid_address","restart_syscall",
    "semtimedop","fadvise64","timer_create","timer_settime","timer_gettime","timer_getoverrun","timer_delete","clock_settime","clock_gettime","clock_getres",
    "clock_nanosleep","exit_group","epoll_wait","epoll_ctl","tgkill","utimes","vserver","mbind","set_mempolicy","get_mempolicy",
    "mq_open","mq_unlink","mq_timedsend","mq_timedreceive","mq_notify","mq_getsetattr","kexec_load","waitid","add_key","request_key",
    "keyctl","ioprio_set","ioprio_get","inotify_init","inotify_add_watch","inotify_rm_watch","migrate_pages","openat","mkdirat","mknodat",
    "fchownat","futimesat","newfstatat","unlinkat","renameat","linkat","symlinkat","readlinkat","fchmodat","faccessat",
    "pselect6","ppoll","unshare","set_robust_list","get_robust_list","splice","tee","sync_file_range","vmsplice","move_pages",
    "utimensat","epoll_pwait","signalfd","timerfd_create","eventfd","fallocate","timerfd_settime","timerfd_gettime","accept4","signalfd4",
    "eventfd2","epoll_create1","dup3","pipe2","inotify_init1","preadv","pwritev","rt_tgsigqueueinfo","perf_event_open","recvmmsg",
    "fanotify_init","fanotify_mark","prlimit64"};	
#else
    static const char *cn[324] = {
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
    "sys_249", "fadvise64", "sys_251", "exit_group", "sys_253", "sys_254", "sys_255", "epoll_wait", "remap_file_pages", "set_tid_address", "timer_create",
    "timer_settime", "timer_gettime", "timer_getoverrun", "timer_delete", "clock_settime", "clock_gettime", "clock_getres", "clock_nanosleep",
    "statfs64", "fstatfs64", "tgkill",
    "utimes","fadvise64_64","vserver","mbind","get_mempolicy","set_mempolicy","mq_open","mq_unlink","mq_timedsend",
    "mq_timedreceive","mq_notify","mq_getsetattr","kexec_load","waitid","add_key","request_key","keyctl","ioprio_set",
    "ioprio_get","inotify_init","inotify_add_watch","inotify_rm_watch","migrate_pages","openat","mkdirat","mknodat",
    "fchownat","futimesat","fstatat64","unlinkat","renameat","linkat","symlinkat","readlinkat","fchmodat","faccessat",
    "pselect6","ppoll","unshare","set_robust_list","get_robust_list","splice","sync_file_range","tee","vmsplice","move_pages",
    "getcpu","epoll_pwait","utimensat","signalfd","timerfd"};
#endif
  static char buf[16];
  if(n>=0 && n<=323) return cn[n];
  sprintf(buf, "sys_%d", n);
  return buf;
}

