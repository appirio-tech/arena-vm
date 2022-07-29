package com.topcoder.services.compiler.invoke;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

/**
 * This class parses the users code for prohibited and/or malicious
 * statements.
 *
 * @author Jason Stanek
 * @author Sean Stanek
 * @version 1.0
 */
public class SecurityParser {

    private static final int STATE_CODE = 1;
    private static final int STATE_STRING = 2;
    private static final int STATE_STRING_ESCAPED = 3;
    private static final int STATE_COMMENTED = 4;

    /** the list of prohibited C++ methods */

    private static final ArrayList badSymbols = new ArrayList(Arrays.asList(new String[]{

        // -- method name --             -- return message

        "asm", "Inline assembly is disallowed",
        "_asm", "Inline assembly is disallowed",
        "__asm", "Inline assembly is disallowed",
        "main", "Calling/defining main is disallowed",

        "open", "open is disallowed",
        "read", "read is disallowed",
        "close", "fclose is disallowed",
        "fopen", "fopen is disallowed",
        "fread", "fread is disallowed",
        "fclose", "fclose is disallowed",
        "kill", "kill is disallowed",
        "setuid", "setuid is disallowed",
        "setgid", "setgid is disallowed",

        "setup", "setup is disallowed",
        "fork", "forking is disallowed",
        "waitpid", "waitpid is disallowed",
        "creat", "creat is disallowed",
        "link", "link is disallowed",
        "unlink", "unlink is disallowed",
        "execve", "execve is disallowed",
        "chdir", "chdir is disallowed",
        "mknod", "mknod is disallowed",
        "chmod", "chmod is disallowed",
        "lchown", "lchown is disallowed",
        "oldstat", "oldstat is disallowed",
        "lseek", "lseek is disallowed",
        "getpid", "getpid is disallowed",
        "mount", "mount is disallowed",
        "oldumount", "oldumount is disallowed",
        "setuid", "setuid is disallowed",
        "getuid", "getuid is disallowed",
        "stime", "stime is disallowed",
        "ptrace", "ptrace is disallowed",
        "alarm", "alarm is disallowed",
        "oldfstat", "oldfstat is disallowed",
        "pause", "pause is disallowed",
        "utime", "utime is disallowed",
        "stty", "stty is disallowed",
        "gtty", "gtty is disallowed",
        "access", "access is disallowed",
        "nice", "nice is disallowed",
        "ftime", "ftime is disallowed",
        "sync", "sync is disallowed",
        "rename", "rename is disallowed",
        "mkdir", "mkdir is disallowed",
        "rmdir", "rmdir is disallowed",
        "dup", "dup is disallowed",
        "pipe", "pipe is disallowed",
        "times", "times is disallowed",
        "prof", "prof is disallowed",
        "setgid", "setgid is disallowed",
        "getgid", "getgid is disallowed",
        "signal", "signal is disallowed",
        "geteuid", "geteuid is disallowed",
        "getegid", "getegid is disallowed",
        "acct", "acct is disallowed",
        "umount", "umount is disallowed",
        "lock", "lock is disallowed",
        "ioctl", "ioctl is disallowed",
        "fcntl", "fcntl is disallowed",
        "mpx", "mpx is disallowed",
        "setpgid", "setpgid is disallowed",
        "ulimit", "ulimit is disallowed",
        "oldolduname", "oldolduname is disallowed",
        "uname", "uname is disallowed",
        "chroot", "chroot is disallowed",
        "ustat", "ustat is disallowed",
        "dup2", "dup2 is disallowed",
        "getppid", "getppid is disallowed",
        "getpgrp", "getpgrp is disallowed",

        "setsid", "setsid is disallowed",
        "sigaction", "sigaction is disallowed",
        "siggetmask", "siggetmask is disallowed",
        "sigsetmask", "sigsetmask is disallowed",
        "setreuid", "setreuid is disallowed",
        "setregid", "setregid is disallowed",
        "sigsuspend", "sigsuspend is disallowed",
        "sigpending", "sigpending is disallowed",
        "sethostname", "sethostname is disallowed",
        "setrlimit", "setrlimit is disallowed",
        "getrlimit", "getrlimit is disallowed",
        "getrusage", "getrusage is disallowed",
        "settimeofday", "settimeofday is disallowed",
        "getgroups", "getgroups is disallowed",
        "setgroups", "setgroups is disallowed",
        "oldselect", "oldselect is disallowed",
        "symlink", "symlink is disallowed",
        "oldlstat", "oldlstat is disallowed",
        "readlink", "readlink is disallowed",
        "uselib", "uselib is disallowed",
        "swapon", "swapon is disallowed",
        "reboot", "reboot is disallowed",
        "readdir", "readdir is disallowed",
        "old_mmap", "old_mmap is disallowed",
        "munmap", "munmap is disallowed",
        "truncate", "truncate is disallowed",
        "ftruncate", "ftruncate is disallowed",
        "fchmod", "fchmod is disallowed",
        "fchown", "fchown is disallowed",
        "getpriority", "getpriority is disallowed",
        "setpriority", "setpriority is disallowed",
        "profil", "profil is disallowed",
        "statfs", "statfs is disallowed",
        "fstatfs", "fstatfs is disallowed",
        "ioperm", "ioperm is disallowed",
        "socketcall", "socketcall is disallowed",
        "syslog", "syslog is disallowed",
        "setitimer", "setitimer is disallowed",
        "getitimer", "getitimer is disallowed",
        "stat", "stat is disallowed",
        "lstat", "lstat is disallowed",
        "fstat", "fstat is disallowed",
        "olduname", "olduname is disallowed",
        "iopl", "iopl is disallowed",
        "vhangup", "vhangup is disallowed",
        "idle", "idle is disallowed",
        "vm86old", "vm86old is disallowed",
        "wait4", "wait4 is disallowed",
        "swapoff", "swapoff is disallowed",
        "sysinfo", "sysinfo is disallowed",
        "ipc", "ipc is disallowed",
        "fsync", "fsync is disallowed",
        "sigreturn", "sigreturn is disallowed",
        "__clone", "__clone is disallowed",
        "setdomainname", "setdomainname is disallowed",
        "uname", "uname is disallowed",
        "modify_ldt", "modify_ldt is disallowed",
        "adjtimex", "adjtimex is disallowed",
        "mprotect", "mprotect is disallowed",
        "sigprocmask", "sigprocmask is disallowed",
        "create_module", "create_module is disallowed",
        "init_module", "init_module is disallowed",
        "delete_module", "delete_module is disallowed",
        "get_kernel_syms", "get_kernel_syms is disallowed",
        "quotactl", "quotactl is disallowed",
        "getpgid", "getpgid is disallowed",
        "fchdir", "fchdir is disallowed",
        "bdflush", "bdflush is disallowed",
        "sysfs", "sysfs is disallowed",
        "personality", "personality is disallowed",
        "afs_syscall", "afs_syscall is disallowed",
        "setfsuid", "setfsuid is disallowed",
        "setfsgid", "setfsgid is disallowed",
        "_llseek", "_llseek is disallowed",
        "getdents", "getdents is disallowed",
        "select", "select is disallowed",
        "flock", "flock is disallowed",
        "msync", "msync is disallowed",
        "readv", "readv is disallowed",
        "writev", "writev is disallowed",
        "getsid", "getsid is disallowed",
        "fdatasync", "fdatasync is disallowed",
        "_sysctl", "_sysctl is disallowed",
        "mlock", "mlock is disallowed",
        "munlock", "munlock is disallowed",
        "mlockall", "mlockall is disallowed",
        "munlockall", "munlockall is disallowed",
        "sched_setparam", "sched_setparam is disallowed",
        "sched_getparam", "sched_getparam is disallowed",
        "sched_setscheduler", "sched_setscheduler is disallowed",
        "sched_getscheduler", "sched_getscheduler is disallowed",
        "sched_yield", "sched_yield is disallowed",
        "sched_get_priority_max", "sched_get_priority_max is disallowed",
        "sched_get_priority_min", "sched_get_priority_min is disallowed",
        "sched_rr_get_interval", "sched_rr_get_interval is disallowed",
        "nanosleep", "nanosleep is disallowed",
        "mremap", "mremap is disallowed",
        "setresuid", "setresuid is disallowed",
        "getresuid", "getresuid is disallowed",
        "vm86", "vm86 is disallowed",
        "query_module", "query_module is disallowed",
        "poll", "poll is disallowed",
        "nfsservctl", "nfsservctl is disallowed",
        "setresgid", "setresgid is disallowed",
        "getresgid", "getresgid is disallowed",
        "prctl", "prctl is disallowed",
        "rt_sigreturn", "rt_sigreturn is disallowed",
        "rt_sigaction", "rt_sigaction is disallowed",
        "rt_sigprocmask", "rt_sigprocmask is disallowed",
        "rt_sigpending", "rt_sigpending is disallowed",
        "rt_sigtimedwait", "rt_sigtimedwait is disallowed",
        "rt_sigqueueinfo", "rt_sigqueueinfo is disallowed",
        "rt_sigsuspend", "rt_sigsuspend is disallowed",
        "pread", "pread is disallowed",
        "pwrite", "pwrite is disallowed",
        "chown", "chown is disallowed",
        "getcwd", "getcwd is disallowed",
        "capget", "capget is disallowed",
        "capset", "capset is disallowed",
        "sigaltstack", "sigaltstack is disallowed",
        "sendfile", "sendfile is disallowed",
        "getpmsg", "getpmsg is disallowed",
        "putpmsg", "putpmsg is disallowed",
        "vfork", "vfork is disallowed",
        "getrlimit", "getrlimit is disallowed",
        "mmap2", "mmap2 is disallowed",
        "truncate64", "truncate64 is disallowed",
        "ftruncate64", "ftruncate64 is disallowed",
        "stat64", "stat64 is disallowed",
        "lstat64", "lstat64 is disallowed",
        "fstat64", "fstat64 is disallowed",
        "lchown32", "lchown32 is disallowed",
        "getuid32", "getuid32 is disallowed",
        "getgid32", "getgid32 is disallowed",
        "geteuid32", "geteuid32 is disallowed",
        "getegid32", "getegid32 is disallowed",
        "setreuid32", "setreuid32 is disallowed",
        "setregid32", "setregid32 is disallowed",
        "getgroups32", "getgroups32 is disallowed",
        "setgroups32", "setgroups32 is disallowed",
        "fchown32", "fchown32 is disallowed",
        "setresuid32", "setresuid32 is disallowed",
        "getresuid32", "getresuid32 is disallowed",
        "setresgid32", "setresgid32 is disallowed",
        "getsetgid32", "getsetgid32 is disallowed",
        "chown32", "chown32 is disallowed",
        "setuid32", "setuid32 is disallowed",
        "setgid32", "setgid32 is disallowed",
        "setfsuid32", "setfsuid32 is disallowed",
        "setfsgid32", "setfsgid32 is disallowed",
        "pivot_root", "pivot_root is disallowed",
        "mincore", "mincore is disallowed",
        "madvise", "madvise is disallowed",
        "getdents64", "getdents64 is disallowed",
        "fcntl64", "fcntl64 is disallowed"

    }));

    private String sourceCode;
    private boolean status;
    private String securityError;
    private String firstBadSymbol;

    private boolean ministate_poundsign = false;
    private boolean ministate_include = false;

    /**
     * constructor initializes prepares the source for parsing
     *
     * @param source       String containing the users source
     */
    ////////////////////////////////////////////////////////////////////////////////
    public SecurityParser(String source)
            ////////////////////////////////////////////////////////////////////////////////
    {
        sourceCode = new String(source);
        status = true;
        securityError = new String("You haven't parsed the code yet!");
        firstBadSymbol = new String();
    }

    /**
     * parse the users code for malicious content
     */
    ////////////////////////////////////////////////////////////////////////////////
    public void parse()
            ////////////////////////////////////////////////////////////////////////////////
    {
        status = false;
        int state = STATE_CODE;
        int pos = 0;

        StringTokenizer tokens = new StringTokenizer(sourceCode, "", true);

        String nextToken = new String();
        String curToken = new String();
        String postToken = new String();
        //String preToken = new String();
        String quoteChars = new String();
        String commentEnd = new String();

        while (tokens.hasMoreTokens()) {
            int newtoken = 0;
            if (state == STATE_CODE || state == STATE_COMMENTED) {
                newtoken = 1;
                nextToken = tokens.nextToken("\n\r\t ;()[]{}\"#<>-=+~!%^&|*/?,");
                pos += nextToken.length();

                /**
                 * Check for multicharacter operations (i.e. ++, &&, |=, //, etc.)
                 * This is important to disambiguate things like "a++//evilcodehere!!\n;"
                 */

                if (pos < sourceCode.length()) {

                    if (nextToken.equals("+") && (sourceCode.charAt(pos) == '+')) {
                        nextToken += tokens.nextToken("+");
                        pos++;
                    }
                    if (nextToken.equals("-") && (sourceCode.charAt(pos) == '-')) {
                        nextToken += tokens.nextToken("-");
                        pos++;
                    }
                    if (nextToken.equals("|") && (sourceCode.charAt(pos) == '|')) {
                        nextToken += tokens.nextToken("|");
                        pos++;
                    }
                    if (nextToken.equals("&") && (sourceCode.charAt(pos) == '&')) {
                        nextToken += tokens.nextToken("&");
                        pos++;
                    }
                    if (nextToken.equals("<") && (sourceCode.charAt(pos) == '<')) {
                        nextToken += tokens.nextToken("<");
                        pos++;
                    }
                    if (nextToken.equals(">") && (sourceCode.charAt(pos) == '>')) {
                        nextToken += tokens.nextToken(">");
                        pos++;
                    }

                    if (nextToken.equals("+") && (sourceCode.charAt(pos) == '=')) {
                        nextToken += tokens.nextToken("=");
                        pos++;
                    }
                    if (nextToken.equals("-") && (sourceCode.charAt(pos) == '=')) {
                        nextToken += tokens.nextToken("=");
                        pos++;
                    }
                    if (nextToken.equals("*") && (sourceCode.charAt(pos) == '=')) {
                        nextToken += tokens.nextToken("=");
                        pos++;
                    }
                    if (nextToken.equals("/") && (sourceCode.charAt(pos) == '=')) {
                        nextToken += tokens.nextToken("=");
                        pos++;
                    }
                    if (nextToken.equals("%") && (sourceCode.charAt(pos) == '=')) {
                        nextToken += tokens.nextToken("=");
                        pos++;
                    }
                    if (nextToken.equals("&") && (sourceCode.charAt(pos) == '=')) {
                        nextToken += tokens.nextToken("=");
                        pos++;
                    }
                    if (nextToken.equals("|") && (sourceCode.charAt(pos) == '=')) {
                        nextToken += tokens.nextToken("=");
                        pos++;
                    }
                    if (nextToken.equals("^") && (sourceCode.charAt(pos) == '=')) {
                        nextToken += tokens.nextToken("=");
                        pos++;
                    }
                    if (nextToken.equals("~") && (sourceCode.charAt(pos) == '=')) {
                        nextToken += tokens.nextToken("=");
                        pos++;
                    }

                    if (nextToken.equals("=") && (sourceCode.charAt(pos) == '=')) {
                        nextToken += tokens.nextToken("=");
                        pos++;
                    }
                    if (nextToken.equals("!") && (sourceCode.charAt(pos) == '=')) {
                        nextToken += tokens.nextToken("=");
                        pos++;
                    }
                    if (nextToken.equals(">") && (sourceCode.charAt(pos) == '=')) {
                        nextToken += tokens.nextToken("=");
                        pos++;
                    }
                    if (nextToken.equals("<") && (sourceCode.charAt(pos) == '=')) {
                        nextToken += tokens.nextToken("=");
                        pos++;
                    }


                    // #include'd files inside "<...>" treated as comment because there are multiple symbols inside
                    // (including the "<" and ">") so we have to concatenate them all together.. easy to do as a comment.
                    // in fact should be done as a comment simply because even commenting characters inside are treated
                    // as literals.

                    if (nextToken.equals("<") && ministate_include) {
                        if (state != STATE_COMMENTED) {
                            state = STATE_COMMENTED;
                            commentEnd = new String(">");
                        }
                    }


                    // also - make sure we can't overwrite the type of comment ("//" vs. "/* ... */") if we are already inside a comment

                    if (nextToken.equals("/") && (sourceCode.charAt(pos) == '/')) {
                        nextToken += tokens.nextToken("/");
                        pos++;
                        if (state != STATE_COMMENTED) {
                            state = STATE_COMMENTED;
                            commentEnd = new String("\n");
                        }
                    }
                    if (nextToken.equals("/") && (sourceCode.charAt(pos) == '*')) {
                        nextToken += tokens.nextToken("*");
                        pos++;
                        if (state != STATE_COMMENTED) {
                            state = STATE_COMMENTED;
                            commentEnd = new String("*/");
                        }
                    }
                    if (nextToken.equals("*") && (sourceCode.charAt(pos) == '/')) {
                        nextToken += tokens.nextToken("/");
                        pos++;
                    }

                }

                if (state == STATE_COMMENTED) {
                    newtoken = 0;
                    if (nextToken.equals("\n") == false) {     // append a space instead of a newline if inside comment
                        postToken += nextToken;
                    } else {
                        postToken += " ";
                    }
                    if (nextToken.equals(commentEnd)) {
                        nextToken = new String();
                        state = STATE_CODE;
                        newtoken = 1;
                    }

                } else {

                    if (nextToken.equals("\"") == true) {
                        state = STATE_STRING;
                        newtoken = 0;
                        quoteChars = new String("\"");
                    }
                    if (nextToken.equals("'") == true) {
                        state = STATE_STRING;
                        newtoken = 0;
                        quoteChars = new String("'");
                    }
                    if (nextToken.equals("\n") == true) {
                        newtoken = 0;
                    }
                    if (nextToken.equals("\r") == true) {
                        newtoken = 0;
                    }
                    if (nextToken.equals("\t") == true) {
                        newtoken = 0;
                    }
                    if (nextToken.equals(" ") == true) {
                        newtoken = 0;
                    }

                }

            } else if (state == STATE_STRING || state == STATE_STRING_ESCAPED) {
                newtoken = 1;
                curToken = tokens.nextToken("\\\"" + quoteChars);
                pos += curToken.length();
                if ((curToken.equals(quoteChars) == true) && (state != STATE_STRING_ESCAPED)) {
                    state = STATE_CODE;
                    postToken = new String(quoteChars);
                } else if (curToken.equals("\\") == true) {
                    nextToken += curToken;
                    if (state == STATE_STRING_ESCAPED) {
                        state = STATE_STRING;
                    } else {
                        state = STATE_STRING_ESCAPED;
                    }
                    newtoken = 0;
                } else {
                    state = STATE_STRING;
                    nextToken += curToken;
                    newtoken = 0;
                }
            } else {
                System.out.println("SecurityParser error: unexpected state: " + state + ".");
            }

            if (newtoken == 1) {
                if (postToken.equals("") == false) {
                    nextToken += postToken;
                    postToken = new String();
                }
                //System.out.println(nextToken);

                for (int i = 0; i < badSymbols.size(); i += 2) {
                    if (((String) badSymbols.get(i)).equals(nextToken) == true) {
                        System.out.println("*** Notice: possible malicious code detected - " + ((String) badSymbols.get(i + 1)));
                        if (firstBadSymbol.equals("")) {
                            firstBadSymbol = new String((String) badSymbols.get(i + 1));
                        }
                        status = true;
                    }
                }


                /* custom malicious code checks need to go here */

                if (ministate_include) {
                    if (nextToken.charAt(0) == '\"' && nextToken.charAt(nextToken.length() - 1) == '\"') {
                        status = true;
                        firstBadSymbol = new String("You are not allowed to #include stray files");
                    }
                    if (nextToken.charAt(0) == '<' && nextToken.charAt(nextToken.length() - 1) == '>') {
                        if (nextToken.indexOf("..") != -1) {
                            status = true;
                            firstBadSymbol = new String("You are not allowed to #include stray files");
                        } else {
                            int i = 0;
                            while (nextToken.charAt(i) == '<' || nextToken.charAt(i) == ' ') i++;
                            if (nextToken.charAt(i) == '/') {
                                status = true;
                                firstBadSymbol = new String("You are not allowed to #include stray files");
                            }
                        }
                    }
                }

                if (nextToken.equals("include") && ministate_poundsign)
                    ministate_include = true;
                else
                    ministate_include = false;

                if (nextToken.equals("#"))
                    ministate_poundsign = true;
                else
                    ministate_poundsign = false;


                /* end custom malicious code checks */

                nextToken = new String();
            }
        }

        if (status == true) {
            securityError = new String(firstBadSymbol);
        } else {
            securityError = new String("No malicious code was found");
        }
    }

    /**
     * return the status of the code after parsing has taken place.
     *
     * @return boolean         status of the parse (malicious or not)
     */
    ////////////////////////////////////////////////////////////////////////////////
    public boolean isMalicious()
            ////////////////////////////////////////////////////////////////////////////////
    {
        return status;
    }

    /**
     * return the parse error after parsing has taken place.
     *
     * @return string         parse error
     */
    ////////////////////////////////////////////////////////////////////////////////
    public String getParserError()
            ////////////////////////////////////////////////////////////////////////////////
    {
        return new String(securityError);
    }

}
