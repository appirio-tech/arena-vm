/*
 * Copyright (C) - 2022 TopCoder Inc., All Rights Reserved.
 */

/**
 * <p>The syscall header for 64 bits system.</p>
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
  long arg[10];     /* system call arguments, if entering system */
  long ret;        /* return value of the system call, if leaving system */
  long rsv0[4];
  long scno;       /* system call number, if entering system */
  long pc;         /* program counter (eip) */
  long rsv1[10];
  long in;         /* true if entering system */
  long sig;        /* if nonzero, signal to deliver to child */
};

//from util
const char *signame(int);
const char *scname(int);

/* derived from linux/arch/i386/kernel/entry.S */
/* similar but possibly stale info available in <asm/unistd.h> */

//This doesn't make much sense, but we see it anyway
#define  TC_leaving_sigreturn   -1

#define NR_accept                 43
#define NR_accept4                288
#define NR_access                 21
#define NR_acct                   163
#define NR_add_key                248
#define NR_adjtimex               159
#define NR_afs_syscall            183
#define NR_alarm                  37
#define NR_arch_prctl             158
#define NR_bind                   49
#define NR_brk                    12
#define NR_capget                 125
#define NR_capset                 126
#define NR_chdir                  80
#define NR_chmod                  90
#define NR_chown                  92
#define NR_chroot                 161
#define NR_clock_getres           229
#define NR_clock_gettime          228
#define NR_clock_nanosleep        230
#define NR_clock_settime          227
#define NR_clone                  56
#define NR_close                  3
#define NR_connect                42
#define NR_creat                  85
#define NR_create_module          174
#define NR_delete_module          176
#define NR_dup                    32
#define NR_dup2                   33
#define NR_dup3                   292
#define NR_epoll_create           213
#define NR_epoll_create1          291
#define NR_epoll_ctl              233
#define NR_epoll_ctl_old          214
#define NR_epoll_pwait            281
#define NR_epoll_wait             232
#define NR_epoll_wait_old         215
#define NR_eventfd                284
#define NR_eventfd2               290
#define NR_execve                 59
#define NR_exit                   60
#define NR_exit_group             231
#define NR_faccessat              269
#define NR_fadvise64              221
#define NR_fallocate              285
#define NR_fanotify_init          300
#define NR_fanotify_mark          301
#define NR_fchdir                 81
#define NR_fchmod                 91
#define NR_fchmodat               268
#define NR_fchown                 93
#define NR_fchownat               260
#define NR_fcntl                  72
#define NR_fdatasync              75
#define NR_fgetxattr              193
#define NR_flistxattr             196
#define NR_flock                  73
#define NR_fork                   57
#define NR_fremovexattr           199
#define NR_fsetxattr              190
#define NR_fstat                  5
#define NR_fstatfs                138
#define NR_fsync                  74
#define NR_ftruncate              77
#define NR_futex                  202
#define NR_futimesat              261
#define NR_get_kernel_syms        177
#define NR_get_mempolicy          239
#define NR_get_robust_list        274
#define NR_get_thread_area        211
#define NR_getcwd                 79
#define NR_getdents               78
#define NR_getdents64             217
#define NR_getegid                108
#define NR_geteuid                107
#define NR_getgid                 104
#define NR_getgroups              115
#define NR_getitimer              36
#define NR_getpeername            52
#define NR_getpgid                121
#define NR_getpgrp                111
#define NR_getpid                 39
#define NR_getpmsg                181
#define NR_getppid                110
#define NR_getpriority            140
#define NR_getresgid              120
#define NR_getresuid              118
#define NR_getrlimit              97
#define NR_getrusage              98
#define NR_getsid                 124
#define NR_getsockname            51
#define NR_getsockopt             55
#define NR_gettid                 186
#define NR_gettimeofday           96
#define NR_getuid                 102
#define NR_getxattr               191
#define NR_init_module            175
#define NR_inotify_add_watch      254
#define NR_inotify_init           253
#define NR_inotify_init1          294
#define NR_inotify_rm_watch       255
#define NR_io_cancel              210
#define NR_io_destroy             207
#define NR_io_getevents           208
#define NR_io_setup               206
#define NR_io_submit              209
#define NR_ioctl                  16
#define NR_ioperm                 173
#define NR_iopl                   172
#define NR_ioprio_get             252
#define NR_ioprio_set             251
#define NR_kexec_load             246
#define NR_keyctl                 250
#define NR_kill                   62
#define NR_lchown                 94
#define NR_lgetxattr              192
#define NR_link                   86
#define NR_linkat                 265
#define NR_listen                 50
#define NR_listxattr              194
#define NR_llistxattr             195
#define NR_lookup_dcookie         212
#define NR_lremovexattr           198
#define NR_lseek                  8
#define NR_lsetxattr              189
#define NR_lstat                  6
#define NR_madvise                28
#define NR_mbind                  237
#define NR_migrate_pages          256
#define NR_mincore                27
#define NR_mkdir                  83
#define NR_mkdirat                258
#define NR_mknod                  133
#define NR_mknodat                259
#define NR_mlock                  149
#define NR_mlockall               151
#define NR_mmap                   9
#define NR_modify_ldt             154
#define NR_mount                  165
#define NR_move_pages             279
#define NR_mprotect               10
#define NR_mq_getsetattr          245
#define NR_mq_notify              244
#define NR_mq_open                240
#define NR_mq_timedreceive        243
#define NR_mq_timedsend           242
#define NR_mq_unlink              241
#define NR_mremap                 25
#define NR_msgctl                 71
#define NR_msgget                 68
#define NR_msgrcv                 70
#define NR_msgsnd                 69
#define NR_msync                  26
#define NR_munlock                150
#define NR_munlockall             152
#define NR_munmap                 11
#define NR_nanosleep              35
#define NR_newfstatat             262
#define NR_nfsservctl             180
#define NR_open                   2
#define NR_openat                 257
#define NR_pause                  34
#define NR_perf_event_open        298
#define NR_personality            135
#define NR_pipe                   22
#define NR_pipe2                  293
#define NR_pivot_root             155
#define NR_poll                   7
#define NR_ppoll                  271
#define NR_prctl                  157
#define NR_pread64                17
#define NR_preadv                 295
#define NR_prlimit64              302
#define NR_pselect6               270
#define NR_ptrace                 101
#define NR_putpmsg                182
#define NR_pwrite64               18
#define NR_pwritev                296
#define NR_query_module           178
#define NR_quotactl               179
#define NR_read                   0
#define NR_readahead              187
#define NR_readlink               89
#define NR_readlinkat             267
#define NR_readv                  19
#define NR_reboot                 169
#define NR_recvfrom               45
#define NR_recvmmsg               299
#define NR_recvmsg                47
#define NR_remap_file_pages       216
#define NR_removexattr            197
#define NR_rename                 82
#define NR_renameat               264
#define NR_request_key            249
#define NR_restart_syscall        219
#define NR_rmdir                  84
#define NR_rt_sigaction           13
#define NR_rt_sigpending          127
#define NR_rt_sigprocmask         14
#define NR_rt_sigqueueinfo        129
#define NR_rt_sigreturn           15
#define NR_rt_sigsuspend          130
#define NR_rt_sigtimedwait        128
#define NR_rt_tgsigqueueinfo      297
#define NR_sched_get_priority_max 146
#define NR_sched_get_priority_min 147
#define NR_sched_getaffinity      204
#define NR_sched_getparam         143
#define NR_sched_getscheduler     145
#define NR_sched_rr_get_interval  148
#define NR_sched_setaffinity      203
#define NR_sched_setparam         142
#define NR_sched_setscheduler     144
#define NR_sched_yield            24
#define NR_security               185
#define NR_select                 23
#define NR_semctl                 66
#define NR_semget                 64
#define NR_semop                  65
#define NR_semtimedop             220
#define NR_sendfile               40
#define NR_sendmsg                46
#define NR_sendto                 44
#define NR_set_mempolicy          238
#define NR_set_robust_list        273
#define NR_set_thread_area        205
#define NR_set_tid_address        218
#define NR_setdomainname          171
#define NR_setfsgid               123
#define NR_setfsuid               122
#define NR_setgid                 106
#define NR_setgroups              116
#define NR_sethostname            170
#define NR_setitimer              38
#define NR_setpgid                109
#define NR_setpriority            141
#define NR_setregid               114
#define NR_setresgid              119
#define NR_setresuid              117
#define NR_setreuid               113
#define NR_setrlimit              160
#define NR_setsid                 112
#define NR_setsockopt             54
#define NR_settimeofday           164
#define NR_setuid                 105
#define NR_setxattr               188
#define NR_shmat                  30
#define NR_shmctl                 31
#define NR_shmdt                  67
#define NR_shmget                 29
#define NR_shutdown               48
#define NR_sigaltstack            131
#define NR_signalfd               282
#define NR_signalfd4              289
#define NR_socket                 41
#define NR_socketpair             53
#define NR_splice                 275
#define NR_stat                   4
#define NR_statfs                 137
#define NR_swapoff                168
#define NR_swapon                 167
#define NR_symlink                88
#define NR_symlinkat              266
#define NR_sync                   162
#define NR_sync_file_range        277
#define NR__sysctl                156
#define NR_sysfs                  139
#define NR_sysinfo                99
#define NR_syslog                 103
#define NR_tee                    276
#define NR_tgkill                 234
#define NR_time                   201
#define NR_timer_create           222
#define NR_timer_delete           226
#define NR_timer_getoverrun       225
#define NR_timer_gettime          224
#define NR_timer_settime          223
#define NR_timerfd_create         283
#define NR_timerfd_gettime        287
#define NR_timerfd_settime        286
#define NR_times                  100
#define NR_tkill                  200
#define NR_truncate               76
#define NR_tuxcall                184
#define NR_umask                  95
#define NR_umount2                166
#define NR_uname                  63
#define NR_unlink                 87
#define NR_unlinkat               263
#define NR_unshare                272
#define NR_uselib                 134
#define NR_ustat                  136
#define NR_utime                  132
#define NR_utimensat              280
#define NR_utimes                 235
#define NR_vfork                  58
#define NR_vhangup                153
#define NR_vmsplice               278
#define NR_vserver                236
#define NR_wait4                  61
#define NR_waitid                 247
#define NR_write                  1
#define NR_writev                 20
#define NR_getrandom              318
#endif
