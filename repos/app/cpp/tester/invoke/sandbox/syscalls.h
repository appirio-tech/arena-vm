/**
 * This file contains a table of all system calls for x86 
 * architecture linux.  The idea is that each system call is
 * indexed by its particular system call number.  The table
 * is an array of sysentry's.  The sysentry contains the 
 * name of the system call for printing purposes, whether the
 * system call should be denied (0==allowed,-1==denied.
 * 1==determined safe or not by safe_func), the number of arguments,
 * a function for printing out the system call, and a function to
 * determine if the system call is safe (assuming deny is set to 1).
 *
 * @author Jason Stanek
 * @version 1.0
 */

struct sysentry{
  char * name;
  int deny;
  int nargs;
  int (*print_func)(int, long, long *);
  int (*safe_func)(int, long, long *);
  };

struct sysentry syscalls[] = {
{"setup",	-1,	0,	print_default,	safe_default},
{"_exit",	0,	1,	print_default,	safe_default},
{"fork",	-1,	0,	print_default,	safe_default},
{"read",	1,	3,	print_read,	safe_read},
{"write",	1,	3,	print_write,	safe_write},
{"open",	1,	3,	print_open,	safe_open},
{"close",	1,	1,	print_close,	safe_close},
{"waitpid",	-1,	3,	print_default,	safe_default},
{"creat",	-1,	2,	print_default,	safe_default},
{"link",	-1,	2,	print_default,	safe_default},
{"unlink",	-1,	1,	print_default,	safe_default},
{"execve",	1,	3,	print_execve,	safe_execve},
{"chdir",	-1,	1,	print_default,	safe_default},
{"time",	0,	1,	print_default,	safe_default},
{"mknod",	-1,	3,	print_default,	safe_default},
{"chmod",	-1,	2,	print_default,	safe_default},
{"lchown",	-1,	3,	print_default,	safe_default},
{"break",	-1,	0,	print_default,	safe_default},
{"oldstat",	-1,	2,	print_default,	safe_default},
{"lseek",	-1,	3,	print_default,	safe_default},
{"getpid",	0,	0,	print_default,	safe_default},
{"mount",	-1,	5,	print_default,	safe_default},
{"oldumount",	-1,	1,	print_default,	safe_default},
{"setuid",	-1,	1,	print_default,	safe_default},
{"getuid",	-1,	0,	print_default,	safe_default},
{"stime",	-1,	1,	print_default,	safe_default},
{"ptrace",	-1,	4,	print_default,	safe_default},
{"alarm",	-1,	1,	print_default,	safe_default},
{"oldfstat",	-1,	2,	print_default,	safe_default},
{"pause",	-1,	0,	print_default,	safe_default},
{"utime",	-1,	2,	print_default,	safe_default},
{"stty",	-1,	2,	print_default,	safe_default},
{"gtty",	-1,	2,	print_default,	safe_default},
{"access",	-1,	2,	print_default,	safe_default},
{"nice",	-1,	1,	print_default,	safe_default},
{"ftime",	0,	0,	print_default,	safe_default},
{"sync",	-1,	0,	print_default,	safe_default},
{"kill",	-1,	2,	print_default,	safe_default},
{"rename",	-1,	2,	print_default,	safe_default},
{"mkdir",	-1,	2,	print_default,	safe_default},
{"rmdir",	-1,	1,	print_default,	safe_default},
{"dup",		-1,	1,	print_default,	safe_default},
{"pipe",	-1,	1,	print_default,	safe_default},
{"times",	-1,	1,	print_default,	safe_default},
{"prof",	-1,	0,	print_default,	safe_default},
{"brk",		1,	1,	print_brk,	safe_brk},
{"setgid",	-1,	1,	print_default,	safe_default},
{"getgid",	-1,	0,	print_default,	safe_default},
{"signal",	-1,	3,	print_default,	safe_default},
{"geteuid",	-1,	0,	print_default,	safe_default},
{"getegid",	-1,	0,	print_default,	safe_default},
{"acct",	-1,	1,	print_default,	safe_default},
{"umount",	-1,	2,	print_default,	safe_default},
{"lock",	-1,	0,	print_default,	safe_default},
{"ioctl",	1,	3,	print_ioctl,	safe_ioctl},
{"fcntl",	-1,	3,	print_default,	safe_default},
{"mpx",		-1,	0,	print_default,	safe_default},
{"setpgid",	-1,	2,	print_default,	safe_default},
{"ulimit",	-1,	2,	print_default,	safe_default},
{"oldolduname",	-1,	1,	print_default,	safe_default},
{"umask",	-1,	1,	print_default,	safe_default},
{"chroot",	-1,	1,	print_default,	safe_default},
{"ustat",	-1,	2,	print_default,	safe_default},
{"dup2",	-1,	2,	print_default,	safe_default},
{"getppid",	-1,	0,	print_default,	safe_default},
{"getpgrp",	-1,	0,	print_default,	safe_default},
{"setsid",	-1,	0,	print_default,	safe_default},
{"sigaction",	-1,	3,	print_default,	safe_default},
{"siggetmask",	-1,	0,	print_default,	safe_default},
{"sigsetmask",	-1,	1,	print_default,	safe_default},
{"setreuid",	-1,	2,	print_default,	safe_default},
{"setregid",	-1,	2,	print_default,	safe_default},
{"sigsuspend",	-1,	3,	print_default,	safe_default},
{"sigpending",	-1,	1,	print_default,	safe_default},
{"sethostname",	-1,	2,	print_default,	safe_default},
{"setrlimit",	-1,	2,	print_default,	safe_default},
{"getrlimit",	-1,	2,	print_default,	safe_default},
{"getrusage",	-1,	2,	print_default,	safe_default},
{"gettimeofday",0,	2,	print_default,	safe_default},
{"settimeofday",-1,	2,	print_default,	safe_default},
{"getgroups",	-1,	2,	print_default,	safe_default},
{"setgroups",	-1,	2,	print_default,	safe_default},
{"oldselect",	-1,	1,	print_default,	safe_default},
{"symlink",	-1,	2,	print_default,	safe_default},
{"oldlstat",	-1,	2,	print_default,	safe_default},
{"readlink",	-1,	3,	print_default,	safe_default},
{"uselib",	-1,	1,	print_default,	safe_default},
{"swapon",	-1,	1,	print_default,	safe_default},
{"reboot",	-1,	3,	print_default,	safe_default},
{"readdir",	-1,	3,	print_default,	safe_default},
{"old_mmap",	1,	6,	print_old_mmap,	safe_old_mmap},
{"munmap",	1,	2,	print_munmap,	safe_munmap},
{"truncate",	-1,	2,	print_default,	safe_default},
{"ftruncate",	-1,	2,	print_default,	safe_default},
{"fchmod",	-1,	2,	print_default,	safe_default},
{"fchown",	-1,	3,	print_default,	safe_default},
{"getpriority",	-1,	2,	print_default,	safe_default},
{"setpriority",	-1,	3,	print_default,	safe_default},
{"profil",	-1,	4,	print_default,	safe_default},
{"statfs",	-1,	2,	print_default,	safe_default},
{"fstatfs",	-1,	2,	print_default,	safe_default},
{"ioperm",	-1,	3,	print_default,	safe_default},
{"socketcall",	-1,	2,	print_default,	safe_default},
{"syslog",	-1,	3,	print_default,	safe_default},
{"setitimer",	-1,	3,	print_default,	safe_default},
{"getitimer",	-1,	2,	print_default,	safe_default},
{"stat",	0,	2,	print_default,	safe_default},
{"lstat",	0,	2,	print_default,	safe_default},
{"fstat",	0,	2,	print_default,	safe_default},
{"olduname",	-1,	1,	print_default,	safe_default},
{"iopl",	-1,	1,	print_default,	safe_default},
{"vhangup",	-1,	0,	print_default,	safe_default},
{"idle",	-1,	0,	print_default,	safe_default},
{"vm86old",	-1,	1,	print_default,	safe_default},
{"wait4",	-1,	4,	print_default,	safe_default},
{"swapoff",	-1,	1,	print_default,	safe_default},
{"sysinfo",	-1,	1,	print_default,	safe_default},
{"ipc",		-1,	5,	print_default,	safe_default},
{"fsync",	-1,	1,	print_default,	safe_default},
{"sigreturn",	-1,	1,	print_default,	safe_default},
{"clone",	-1,	2,	print_default,	safe_default},
{"setdomainname",-1,	2,	print_default,	safe_default},
{"uname",	0,	1,	print_default,	safe_default},
{"modify_ldt",	-1,	3,	print_default,	safe_default},
{"adjtimex",	-1,	1,	print_default,	safe_default},
{"mprotect",	1,	3,	print_mprotect,	safe_mprotect},
{"sigprocmask",	-1,	3,	print_default,	safe_default},
{"create_module",-1,	2,	print_default,	safe_default},
{"init_module",	-1,	2,	print_default,	safe_default},
{"delete_module",-1,	1,	print_default,	safe_default},
{"get_kernel_syms",-1,	1,	print_default,	safe_default},
{"quotactl",	-1,	4,	print_default,	safe_default},
{"getpgid",	-1,	1,	print_default,	safe_default},
{"fchdir",	-1,	1,	print_default,	safe_default},
{"bdflush",	-1,	0,	print_default,	safe_default},
{"sysfs",	-1,	3,	print_default,	safe_default},
{"personality",	 1,	1,	print_personality,	safe_personality},
{"afs_syscall",	-1,	5,	print_default,	safe_default},
{"setfsuid",	-1,	1,	print_default,	safe_default},
{"setfsgid",	-1,	1,	print_default,	safe_default},
{"_llseek",	-1,	5,	print_default,	safe_default},
{"getdents",	-1,	3,	print_default,	safe_default},
{"select",	-1,	5,	print_default,	safe_default},
{"flock",	-1,	2,	print_default,	safe_default},
{"msync",	-1,	3,	print_default,	safe_default},
{"readv",	-1,	3,	print_default,	safe_default},
{"writev",	-1,	3,	print_default,	safe_default},
{"getsid",	-1,	1,	print_default,	safe_default},
{"fdatasync",	-1,	1,	print_default,	safe_default},
{"_sysctl",	1,	1,	print_sysctl,	safe_sysctl},
{"mlock",	-1,	1,	print_default,	safe_default},
{"munlock",	-1,	2,	print_default,	safe_default},
{"mlockall",	-1,	2,	print_default,	safe_default},
{"munlockall",	-1,	0,	print_default,	safe_default},
{"sched_setparam",-1,	0,	print_default,	safe_default},
{"sched_getparam",-1,	2,	print_default,	safe_default},
{"sched_setscheduler",-1,	3,	print_default,	safe_default},
{"sched_getscheduler",-1,	1,	print_default,	safe_default},
{"sched_yield",	-1,	0,	print_default,	safe_default},
{"sched_get_priority_max",-1,	1,	print_default,	safe_default},
{"sched_get_priority_min",-1,	1,	print_default,	safe_default},
{"sched_rr_get_interval",-1,	2,	print_default,	safe_default},
{"nanosleep",	 0,	2,	print_default,	safe_default},
{"mremap",	-1,	4,	print_default,	safe_default},
{"setresuid",	-1,	3,	print_default,	safe_default},
{"getresuid",	-1,	3,	print_default,	safe_default},
{"vm86",	-1,	5,	print_default,	safe_default},
{"query_module",-1,	5,	print_default,	safe_default},
{"poll",	-1,	3,	print_default,	safe_default},
{"nfsservctl",	-1,	3,	print_default,	safe_default},
{"setresgid",	-1,	3,	print_default,	safe_default},
{"getresgid",	-1,	3,	print_default,	safe_default},
{"prctl",	-1,	5,	print_default,	safe_default},
{"rt_sigreturn",-1,	1,	print_default,	safe_default},	
{"rt_sigaction",-1,	4,	print_default,	safe_default},
{"rt_sigprocmask",-1,	4,	print_default,	safe_default},
{"rt_sigpending",-1,	2,	print_default,	safe_default},
{"rt_sigtimedwait",-1,	4,	print_default,	safe_default},
{"rt_sigqueueinfo",-1,	3,	print_default,	safe_default},
{"rt_sigsuspend",-1,	2,	print_default,	safe_default},
{"pread",	-1,	5,	print_default,	safe_default},
{"pwrite",	-1,	5,	print_default,	safe_default},
{"chown",	-1,	3,	print_default,	safe_default},
{"getcwd",	-1,	2,	print_default,	safe_default},
{"capget",	-1,	2,	print_default,	safe_default},
{"capset",	-1,	2,	print_default,	safe_default},
{"sigaltstack",	-1,	2,	print_default,	safe_default},
{"sendfile",	-1,	4,	print_default,	safe_default},
{"getpmsg",	-1,	5,	print_default,	safe_default},
{"putpmsg",	-1,	5,	print_default,	safe_default},
{"vfork",	-1,	0,	print_default,	safe_default},
/****NEW****/
{"getrlimit",	-1,	0,	print_default,	safe_default},
{"mmap2",	0,	0,	print_default,	safe_default},
{"truncate64",	-1,	0,	print_default,	safe_default},
{"ftruncate64",	-1,	0,	print_default,	safe_default},
{"stat64",	0,	0,	print_default,	safe_default},
{"lstat64",	0,	0,	print_default,	safe_default},
{"fstat64",	0,	0,	print_default,	safe_default},
{"lchown32",	-1,	0,	print_default,	safe_default},
{"getuid32",	-1,	0,	print_default,	safe_default},
{"getgid32",	-1,	0,	print_default,	safe_default},
{"geteuid32",	-1,	0,	print_default,	safe_default},
{"getegid32",	-1,	0,	print_default,	safe_default},
{"setreuid32",	-1,	0,	print_default,	safe_default},
{"setregid32",	-1,	0,	print_default,	safe_default},
{"getgroups32",	-1,	0,	print_default,	safe_default},
{"setgroups32",	-1,	0,	print_default,	safe_default},
{"fchown32",	-1,	0,	print_default,	safe_default},
{"setresuid32",	-1,	0,	print_default,	safe_default},
{"getresuid32",	-1,	0,	print_default,	safe_default},
{"setresgid32",	-1,	0,	print_default,	safe_default},
{"getsetgid32",	-1,	0,	print_default,	safe_default},
{"chown32",	-1,	0,	print_default,	safe_default},
{"setuid32",	-1,	0,	print_default,	safe_default},
{"setgid32",	-1,	0,	print_default,	safe_default},
{"setfsuid32",	-1,	0,	print_default,	safe_default},
{"setfsgid32",	-1,	0,	print_default,	safe_default},
{"pivot_root",	-1,	0,	print_default,	safe_default},
{"mincore",	-1,	0,	print_default,	safe_default},
{"madvise",	-1,	0,	print_default,	safe_default},
{"getdents64",	-1,	0,	print_default,	safe_default},
{"fcntl64",	-1,	0,	print_default,	safe_default},
{"SYS_222",	-1,	0,	print_default,	safe_default},
{"SYS_223",	-1,	0,	print_default,	safe_default},
{"SYS_224",	-1,	0,	print_default,	safe_default},
{"SYS_225",	-1,	0,	print_default,	safe_default},
{"SYS_226",	-1,	0,	print_default,	safe_default},
{"SYS_227",	-1,	0,	print_default,	safe_default},
{"SYS_228",	-1,	0,	print_default,	safe_default},
{"SYS_229",	-1,	0,	print_default,	safe_default}
};
