/*
 * Copyright (C) - 2022 TopCoder Inc., All Rights Reserved.
 */

/**
 * <p>The syscall header for 32 bits system.</p>
 *
 * <p>
 * Changes in version 1.1 (Python3 Support):
 * <ol>
 *     <li>Add 'NR_getrandom' which is used by Python3.</li>
 * </ol>
 * </p>
 *
 * @author liuliquan
 * @version 1.1
 */

#ifndef _Syscall_H
#define	_Syscall_H

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

//from util
const char *signame(int);
const char *scname(int);

/* derived from linux/arch/i386/kernel/entry.S */
/* similar but possibly stale info available in <asm/unistd.h> */

//This doesn't make much sense, but we see it anyway
#define  TC_leaving_sigreturn   -1

#define  NR_setup		0
#define  NR_exit		1
#define  NR_fork		2
#define  NR_read		3
#define  NR_write		4
#define  NR_open		5
#define  NR_close		6
#define  NR_waitpid		7
#define  NR_creat		8
#define  NR_link		9
#define  NR_unlink		10
#define  NR_execve		11
#define  NR_chdir		12
#define  NR_time		13
#define  NR_mknod		14
#define  NR_chmod		15
#define  NR_lchown16		16
#define  NR_break		17
#define  NR_stat		18
#define  NR_lseek		19
#define  NR_getpid		20
#define  NR_mount		21
#define  NR_oldumount		22
#define  NR_setuid16		23
#define  NR_getuid16		24
#define  NR_stime		25
#define  NR_ptrace		26
#define  NR_alarm		27
#define  NR_fstat		28
#define  NR_pause		29
#define  NR_utime		30
#define  NR_stty		31
#define  NR_gtty		32
#define  NR_access		33
#define  NR_nice		34
#define  NR_ftime		35
#define  NR_sync		36
#define  NR_kill		37
#define  NR_rename		38
#define  NR_mkdir		39
#define  NR_rmdir		40
#define  NR_dup		41
#define  NR_pipe		42
#define  NR_times		43
#define  NR_prof		44
#define  NR_brk		45
#define  NR_setgid16		46
#define  NR_getgid16		47
#define  NR_signal		48
#define  NR_geteuid16		49
#define  NR_getegid16		50
#define  NR_acct		51
#define  NR_umount		52
#define  NR_lock		53
#define  NR_ioctl		54
#define  NR_fcntl		55
#define  NR_mpx		56
#define  NR_setpgid		57
#define  NR_ulimit		58
#define  NR_olduname		59
#define  NR_umask		60
#define  NR_chroot		61
#define  NR_ustat		62
#define  NR_dup2		63
#define  NR_getppid		64
#define  NR_getpgrp		65
#define  NR_setsid		66
#define  NR_sigaction		67
#define  NR_sgetmask		68
#define  NR_ssetmask		69
#define  NR_setreuid16		70
#define  NR_setregid16		71
#define  NR_sigsuspend		72
#define  NR_sigpending		73
#define  NR_sethostname		74
#define  NR_setrlimit		75
#define  NR_old_getrlimit		76
#define  NR_getrusage		77
#define  NR_gettimeofday		78
#define  NR_settimeofday		79
#define  NR_getgroups16		80
#define  NR_setgroups16		81
#define  NR_old_select		82
#define  NR_symlink		83
#define  NR_lstat		84
#define  NR_readlink		85
#define  NR_uselib		86
#define  NR_swapon		87
#define  NR_reboot		88
#define  NR_old_readdir		89
#define  NR_old_mmap		90
#define  NR_munmap		91
#define  NR_truncate		92
#define  NR_ftruncate		93
#define  NR_fchmod		94
#define  NR_fchown16		95
#define  NR_getpriority		96
#define  NR_setpriority		97
#define  NR_profil		98
#define  NR_statfs		99
#define  NR_fstatfs		100
#define  NR_ioperm		101
#define  NR_socketcall		102
#define  NR_syslog		103
#define  NR_setitimer		104
#define  NR_getitimer		105
#define  NR_newstat		106
#define  NR_newlstat		107
#define  NR_newfstat		108
#define  NR_uname		109
#define  NR_iopl		110
#define  NR_vhangup		111
#define  NR_idle		112
#define  NR_vm86old		113
#define  NR_wait4		114
#define  NR_swapoff		115
#define  NR_sysinfo		116
#define  NR_ipc		117
#define  NR_fsync		118
#define  NR_sigreturn		119
#define  NR_clone		120
#define  NR_setdomainname		121
#define  NR_newuname		122
#define  NR_modify_ldt		123
#define  NR_adjtimex		124
#define  NR_mprotect		125
#define  NR_sigprocmask		126
#define  NR_create_module		127
#define  NR_init_module		128
#define  NR_delete_module		129
#define  NR_get_kernel_syms		130
#define  NR_quotactl		131
#define  NR_getpgid		132
#define  NR_fchdir		133
#define  NR_bdflush		134
#define  NR_sysfs		135
#define  NR_personality		136
#define  NR_afs_syscall		137
#define  NR_setfsuid16		138
#define  NR_setfsgid16		139
#define  NR_llseek		140
#define  NR_getdents		141
#define  NR_select		142
#define  NR_flock		143
#define  NR_msync		144
#define  NR_readv		145
#define  NR_writev		146
#define  NR_getsid		147
#define  NR_fdatasync		148
#define  NR_sysctl		149
#define  NR_mlock		150
#define  NR_munlock		151
#define  NR_mlockall		152
#define  NR_munlockall		153
#define  NR_sched_setparam		154
#define  NR_sched_getparam		155
#define  NR_sched_setscheduler		156
#define  NR_sched_getscheduler		157
#define  NR_sched_yield		158
#define  NR_sched_get_priority_max		159
#define  NR_sched_get_priority_min		160
#define  NR_sched_rr_get_interval		161
#define  NR_nanosleep		162
#define  NR_mremap		163
#define  NR_setresuid16		164
#define  NR_getresuid16		165
#define  NR_vm86		166
#define  NR_query_module		167
#define  NR_poll		168
#define  NR_nfsservctl		169
#define  NR_setresgid16		170
#define  NR_getresgid16		171
#define  NR_prctl		172
#define  NR_rt_sigreturn		173
#define  NR_rt_sigaction		174
#define  NR_rt_sigprocmask		175
#define  NR_rt_sigpending		176
#define  NR_rt_sigtimedwait		177
#define  NR_rt_sigqueueinfo		178
#define  NR_rt_sigsuspend		179
#define  NR_pread		180
#define  NR_pwrite		181
#define  NR_chown16		182
#define  NR_getcwd		183
#define  NR_capget		184
#define  NR_capset		185
#define  NR_sigaltstack		186
#define  NR_sendfile		187
#define  NR_getpmsg		188
#define  NR_putpmsg		189
#define  NR_vfork		190
#define  NR_getrlimit		191
#define  NR_mmap2		192
#define  NR_truncate64		193
#define  NR_ftruncate64		194
#define  NR_stat64		195
#define  NR_lstat64		196
#define  NR_fstat64		197
#define  NR_lchown		198
#define  NR_getuid		199
#define  NR_getgid		200
#define  NR_geteuid		201
#define  NR_getegid		202
#define  NR_setreuid		203
#define  NR_setregid		204
#define  NR_getgroups		205
#define  NR_setgroups		206
#define  NR_fchown		207
#define  NR_setresuid		208
#define  NR_getresuid		209
#define  NR_setresgid		210
#define  NR_getresgid		211
#define  NR_chown		212
#define  NR_setuid		213
#define  NR_setgid		214
#define  NR_setfsuid		215
#define  NR_setfsgid		216
#define  NR_pivot_root		217
#define  NR_mincore		218
#define  NR_madvise		219
#define  NR_getdents64		220
#define  NR_fcntl64		221
#define  NR_tux		222
#define  NR_security		223
#define  NR_gettid		224
#define  NR_readahead		225
#define  NR_setxattr		226
#define  NR_lsetxattr		227
#define  NR_fsetxattr		228
#define  NR_getxattr		229
#define  NR_lgetxattr		230
#define  NR_fgetxattr		231
#define  NR_listxattr		232
#define  NR_llistxattr		233
#define  NR_flistxattr		234
#define  NR_removexattr		235
#define  NR_lremovexattr		236
#define  NR_fremovexattr		237
#define  NR_tkill		238
#define  NR_sys_239		239
#define  NR_futex		240
#define  NR_sched_setaffinity	241
#define  NR_sched_getaffinity	242
#define  NR_set_thread_area	243
#define  NR_get_thread_area	244
#define  NR_sys_245		245
#define  NR_sys_246		246
#define  NR_sys_247		247
#define  NR_sys_248		248
#define  NR_sys_249		249
#define  NR_fadvise64		250
#define  NR_sys_251		251
#define  NR_exit_group		252
#define  NR_sys_253		253
#define  NR_sys_254		254
#define  NR_sys_255		255
#define  NR_epoll_wait		256
#define  NR_remap_file_pages	257
#define  NR_set_tid_address	258
#define  NR_timer_create	259
#define  NR_timer_settime	260
#define  NR_timer_gettime	261
#define  NR_timer_getoverrun	262
#define  NR_timer_delete	263
#define  NR_clock_settime	264
#define  NR_clock_gettime	265
#define  NR_clock_getres	266
#define  NR_clock_nanosleep	267
#define  NR_statfs64		268
#define  NR_fstatfs64		269
#define  NR_tgkill		270
//new 2.6 calls
#define NR_utimes             271
#define NR_fadvise64_64       272
#define NR_vserver            273
#define NR_mbind              274
#define NR_get_mempolicy      275
#define NR_set_mempolicy      276
#define NR_mq_open            277
#define NR_mq_unlink          (NR_mq_open+1)
#define NR_mq_timedsend       (NR_mq_open+2)
#define NR_mq_timedreceive    (NR_mq_open+3)
#define NR_mq_notify          (NR_mq_open+4)
#define NR_mq_getsetattr      (NR_mq_open+5)
#define NR_kexec_load         283
#define NR_waitid             284
/* #define NR_sys_setaltroot  285 */
#define NR_add_key            286
#define NR_request_key        287
#define NR_keyctl             288
#define NR_ioprio_set         289
#define NR_ioprio_get         290
#define NR_inotify_init       291
#define NR_inotify_add_watch  292
#define NR_inotify_rm_watch   293
#define NR_migrate_pages      294
#define NR_openat             295
#define NR_mkdirat            296
#define NR_mknodat            297
#define NR_fchownat           298
#define NR_futimesat          299
#define NR_fstatat64          300
#define NR_unlinkat           301
#define NR_renameat           302
#define NR_linkat             303
#define NR_symlinkat          304
#define NR_readlinkat         305
#define NR_fchmodat           306
#define NR_faccessat          307
#define NR_pselect6           308
#define NR_ppoll              309
#define NR_unshare            310
#define NR_set_robust_list    311
#define NR_get_robust_list    312
#define NR_splice             313
#define NR_sync_file_range    314
#define NR_tee                315
#define NR_vmsplice           316
#define NR_move_pages         317
#define NR_getcpu             318
#define NR_epoll_pwait        319
#define NR_utimensat          320
#define NR_signalfd           321
#define NR_timerfd            322
#define NR_eventfd            323
#define NR_getrandom          355
#endif
