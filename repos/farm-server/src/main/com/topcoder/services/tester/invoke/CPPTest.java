/*
 * Copyright (C) - 2014 TopCoder Inc., All Rights Reserved.
 */

package com.topcoder.services.tester.invoke;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.StringTokenizer;

import com.topcoder.server.tester.ComponentFiles;
import com.topcoder.services.tester.BaseTester;
import com.topcoder.services.util.ExecWrapper;
import com.topcoder.services.util.Formatter;
import com.topcoder.shared.common.ServicesConstants;
import com.topcoder.shared.problem.ProblemCustomSettings;
import com.topcoder.shared.util.encoding.Base64Encoding;
import com.topcoder.shared.util.logging.Logger;

/**
 * <p> the CPP SRM Test</p>
 *
 * <p>
 * Changes in version 1.0 (TC Competition Engine - C++ and Python Customization Support for SRM v1.0):
 * <ol>
 *      <li>Update {@link #CPPTest((String, Object[], Object,String,String, boolean, String)} method.</li>
 *      <li>Update {@link #CPPTest((String, Object[],,String,String, boolean, String)} method.</li>
 *      <li>Update {@link #CPPTest(String, Object[], ComponentFiles, boolean,String)} method.</li>
 *      <li>Add {@link #wrapperApprovedPath(String approvedPath,ArrayList argv)} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.1 (TopCoder Competition Engine - Customize Execution Time and Memory Limits for SRM v1.0):
 * <ol>
 *      <li>Add {@link #executionTimeLimit} field.</li>
 *      <li>Update {@link #CPPTest((String, Object[], Object,String,String, boolean, String, int, int)} method.</li>
 *      <li>Update {@link #CPPTest((String, Object[],,String,String, boolean, String, int, int)} method.</li>
 *      <li>Update {@link #CPPTest(String, Object[], ComponentFiles, boolean,String, int, int)} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.2 (TopCoder Competition Engine Improvement Series 3 v1.0):
 * <ol>
 *      <li>Update {@link #CPPTest((String, Object[], Object,String,String, boolean, ProblemCustomSettings)} method.</li>
 *      <li>Update {@link #CPPTest((String, Object[],,String,String, boolean, ProblemCustomSettings)} method.</li>
 *      <li>Update {@link #CPPTest(String, Object[], ComponentFiles, boolean, ProblemCustomSettings)} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.3 (PoC Assembly - TopCoder Competition Engine - Support Custom Output Checker):
 * <ol>
 *     <li>Removed <code>formatUserTest()</code> method.</li>.
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.4 (PoC Assembly - Return Peak Memory Usage for Executing SRM Solution):
 * <ol>
 *      <li>Add {@link #submission_maxmemused} field for maximum memory used (in KB).</li>
 *      <li>Update {@link #CPPTest(String, Object[], String, String, boolean, ProblemCustomSettings)} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.5 (TopCoder Competition Engine - Support Large Memory Limit Settings):
 * <ol>
 *      <li>Update {@link #CPPTest(String, Object[], String, String, boolean, ProblemCustomSettings)} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.6 (Large Input Data Time Execution Fix):
 * <ol>
 *      <li>Update {@link #isTimeout()} method.</li>
 *      <li>Update {@link #CPPTest(String, Object[], String, String, boolean, ProblemCustomSettings)} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.7 (TopCoder Competition Engine - Stack Size Configuration For SRM Problems v1.0):
 * <ol>
 *      <li>Updated {@link #CPPTest((String, Object[], String,String, boolean, ProblemCustomSettings)}
 *          method to pass stack limit to tester.</li>
 * </ol>
 * </p>
 *
 * @author savon_cn, gevak, dexy, Selena
 * @version 1.7
 */
public final class CPPTest {

    private static Logger logger = Logger.getLogger(CPPTest.class);
    //private static final int MAX_TEST_OUTPUT = 15000;

    //public String restype;
    //public Object[] args;
    //public ComponentFiles probfiles;
    //public boolean allow_debug;

    public boolean tester_success;      /* if false, next field is errors from the tester process and others are junk */
    public String tester_errlog;
    public int submission_exitval;      /* zero if it exited normally */
    public int submission_usedcpu;      /* milliseconds */

    /**
     * The submission_maxmemused (in KB).
     * @since 1.4
     */
    public int submission_maxmemused;

    public Object submission_result;    /* already in java format, null if there was a problem parsing */
    public Object expected_result;
    public String submission_stdout;
    public String submission_stderr;
    public String crash_backtrace;      /* empty if not available or not needed */
//    private boolean supplied_testcase;
    /**
     * the execution time limit
     * @since 1.1
     */
    private int executionTimeLimit;

    /**
     *
     * <p>the CPP Test constructor to build the srm test.</p>
     * @param restype  the resource type.
     * @param expected_result the expected result.
     * @param args the arguments.
     * @param classname the user submission class name.
     * @param pathstr the full path of the submission.
     * @param allow_debug if it is allowed debug.
     * @param custom problem customization.
     */
    public CPPTest(String restype, Object[] args, Object expected_result,
            String classname,String pathstr, boolean allow_debug, ProblemCustomSettings custom) {
        this(restype,args,classname,pathstr,allow_debug, custom);
        this.expected_result = expected_result;
//        supplied_testcase = true;
    }

    /**
     * <p>
     * wrapper the srm approved path.
     * </p>
     * @param approvedPath the srm approved path.
     * @param argv the arguments.
     * @return approved path.
     */
    private void wrapperApprovedPath(String approvedPath,ArrayList argv) {
        if (approvedPath != null && approvedPath.trim().length() > 0) {
            argv.add("--approvedpath");
            argv.add(approvedPath);
        }
    }
    /**
     * <p>the CPP Test constructor to build the srm test.</p>
     * @param restype  the resource type.
     * @param args the arguments.
     * @param classname the user submission class name.
     * @param pathstr the full path of the submission.
     * @param allow_debug if it is allowed debug.
     * @param custom problem customization.
     */
    public CPPTest(String restype, Object[] args, String classname,
            String pathstr, boolean allow_debug, ProblemCustomSettings custom) {
//        supplied_testcase = false;
        this.executionTimeLimit = custom.getExecutionTimeLimit();
        // save these in case a formatter finds them useful or something
        //this.restype = restype;
        //this.args = args;
        //this.probfiles = probfiles;
        //this.allow_debug = allow_debug;
        logger.info("In CPPTest");
        StringBuffer packed = new StringBuffer();
        int i;
        for (i = 0; i < args.length; i++)
            packed.append(print(args[i]));

        File path = new File(pathstr);
        logger.info("After Path");
        /**
         * support large memory limit
         */
        long memLimit = ((long)custom.getMemLimit()) * 1024 * 1024;
        long stackLimit = ((long)custom.getStackLimit()) * 1024 * 1024;
        String cppApprovedPath = custom.getCppApprovedPath();
        ArrayList<String> argv = new ArrayList<String>(18);
        argv.add(ServicesConstants.SANDBOX2);
        argv.add("--stackdump");
        argv.add(allow_debug ? "1" : "0");
        argv.add("--config");
        argv.add(ServicesConstants.SANDBOX2_SRM_CONFIG);
        //add execution time limit
        argv.add("--maxcpu");
        /**
         * This is we want to let execution time more than the setting value
         * Because we need to give time for handling the large input data
         */
        argv.add(String.valueOf(executionTimeLimit + BaseTester.DEFAULT_EXTRA_EXECUTION_TIME));
        argv.add("--maxwall");
        argv.add(String.valueOf(executionTimeLimit + BaseTester.DEFAULT_EXTRA_EXECUTION_TIME));
        //add memory limit.
        argv.add("--maxmem");
        argv.add(String.valueOf(memLimit));
        //add stack size limit.
        argv.add("--maxstack");
        argv.add(String.valueOf(stackLimit));
        wrapperApprovedPath(cppApprovedPath,argv);
        // set sandbox options here
        argv.add(classname);
        // Add child arguments
        argv.add(Integer.toString(ServicesConstants.MAX_RESULT_LENGTH));
        logger.info("Before ExecWrapper");
        /**
         * exec wrapper should not end before execution time limit
         */
        ExecWrapper ew = new ExecWrapper(argv.toArray(new String[0]), null, path, packed.toString(), executionTimeLimit + 25000, Formatter.MAX_USER_STRING);
        logger.info("After ExecWrapper");
        logger.info(argv.toString() + " " + path + " " + Formatter.MAX_USER_STRING);
        tester_success = false;
        if (ew.error) {
            tester_errlog = "could not exec sandbox";
            return;
        }
        if (!ew.finished) {
            tester_errlog = "sandbox did not finish on time:\n" + ew.stderr;
            return;
        }
        if (ew.exitval != 0) {
            tester_errlog = "sandbox returned nonzero (" + ew.exitval + "):\n" + ew.stderr;
            return;
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

        String resultdir;
        int btavail;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new StringReader(ew.stdout));
            String firstLine = br.readLine();
            int ts = Integer.parseInt(firstLine);
            if (ts != 0) {
                tester_errlog = "sandbox internal error: " + ew.stderr + " stdout (1):"+firstLine;
                return;
            }
            resultdir = br.readLine();
            if (resultdir == null || resultdir.length() == 0) {
                // extra paranoia, because we don't want the rm below to damage things
                tester_errlog = "no results from sandbox: " + ew.stderr + " stdout (1):"+firstLine;
                return;
            }
            // make it absolute so we can use it from java
            resultdir = path + File.separator + resultdir + File.separator;
            StringTokenizer st = new StringTokenizer(br.readLine());
            submission_exitval = Integer.parseInt(st.nextToken());
            crash_backtrace = null;
            btavail = Integer.parseInt(st.nextToken());
            submission_usedcpu = Integer.parseInt(st.nextToken());
            /* fourth number in the sandbox output is the maximum memory used */
            submission_maxmemused = Integer.parseInt(st.nextToken());
        } catch (Exception e) {
            tester_errlog = "parsing sandbox report: " + e;
            return;
        } finally {
            try {
                if(br!=null)
                    br.close();
            } catch(IOException e) {
                tester_errlog = "error while closing the buffer reader: " + e;
            }
        }
        tester_success = true;

        // collect various things from the result files
        submission_stdout = slurpTextFile(resultdir + "stdout");
        submission_stderr = slurpTextFile(resultdir + "stderr");
        crash_backtrace = null;
        if (btavail != 0)
            crash_backtrace = slurpTextFile(resultdir + "backtrace");

        submission_result = null;
        if (submission_exitval == 0) {
            try {
                // convert packed output to java form
                if (submission_usedcpu > executionTimeLimit) {
                    submission_result = parse(restype, "");
                } else {
                    submission_result = parse(restype, slurpTextFile(resultdir + "result"));
                }
            } catch (Exception e) {
                logger.error("Exception while parsing result", e);
            }
        }

        // too much for java threads, apparently
        // try { Runtime.getRuntime().exec("/bin/rm -rf "+resultdir); }
        // catch(Exception e) { e.printStackTrace(); }

        // clean up our mess
        new File(resultdir + File.separator + "core").delete();
        new File(resultdir + File.separator + "stdout").delete();
        new File(resultdir + File.separator + "stderr").delete();
        new File(resultdir + File.separator + "result").delete();
        new File(resultdir + File.separator + "backtrace").delete();
        new File(resultdir + File.separator + "log").delete();
        new File(resultdir + File.separator + "temp").delete();
        new File(resultdir).delete();
    }

    /**
     * <p>
     * the CPP Test constructor.
     * </p>
     * @param restype the resource type
     * @param args the arguments.
     * @param probfiles the component files.
     * @param allow_debug if it is allowed debug.
     * @param custom problem customization.
     */
    public CPPTest(String restype, Object[] args, ComponentFiles probfiles, boolean allow_debug,
            ProblemCustomSettings custom) {
        /* need to change a few things in order to use this */
        //this.probfiles = probfiles;

        // this is just an alternate interface to CPPTest

        this(restype, args, probfiles.getComponentName(),
                probfiles.getFullComponentPath(), allow_debug, custom);
    }



    public String getStandardError() {
        if (submission_stderr != null && submission_stderr.length() > 0) {
            return submission_stderr;
        }
        return "";
    }
    /**
     * Timeout should be checked not only by submission_exitval
     * But also need <code>submission_usedcpu</code> value
     * @return the flag of timeout.
     */
    public boolean isTimeout() {
        return tester_success && (submission_exitval == -24 || submission_usedcpu > executionTimeLimit);
    }

    /* make sense of exitval */

    public String epitaph() {
        /* this table is specific to linux 2.4 i386 */
        final String[] sigstr = new String[]{
            "SIG0", "SIGHUP", "SIGINT", "SIGQUIT", "SIGILL", "SIGTRAP", "SIGABRT",
            "SIGBUS", "SIGFPE", "SIGKILL", "SIGUSR1", "SIGSEGV", "SIGUSR2",
            "SIGPIPE", "SIGALRM", "SIGTERM", "SIGSTKFLT", "SIGCHLD", "SIGCONT",
            "SIGSTOP", "SIGTSTP", "SIGTTIN", "SIGTTOU", "SIGURG", "SIGXCPU",
            "SIGXFSZ", "SIGVTALRM", "SIGPROF", "SIGWINCH", "SIGIO", "SIGPWR",
            "SIGSYS"};
        if (!tester_success)
            return "C++ TESTER FAILED";
        if (submission_exitval == 0 && submission_usedcpu <= executionTimeLimit)
            return "SUCCESS";
        if (submission_exitval == 200) // LENGTH TOO LONG
            return "returned array or string exceeded limit";
        else if (submission_exitval > 0)
            return "abnormal termination (exit " + submission_exitval + ")";
        int sig = -submission_exitval;
        if (sig == 24 || submission_usedcpu > executionTimeLimit)  // SIGXCPU or more than execution time limit
            return "The code execution time exceeded the "+
                Formatter.getExecutionTimeLimitPresent(executionTimeLimit)+" second time limit.";
        if (sig == 6)  // SIGABRT
            return "uncaught exception";
        if (sig == 11)  // SIGSEGV
            return "segmentation fault";
        if (sig == 8)  // SIGFPE
            return "arithmetic exception";
        if (sig < sigstr.length)
            return "caught signal " + sigstr[sig];
        return "caught signal " + sig;
    }

    private static String slurpTextFile(String fn) {
        try {
            File f = new File(fn);
            BufferedReader br = new BufferedReader(new FileReader(f));
            char[] buf = new char[(int) (f.length())];
            br.read(buf);
            br.close();
            return new String(buf);
        } catch (Exception e) {
            return null;
        }
    }

/* the cpp tester reads its arguments as strings on separate lines.
   array arguments have a count as the first line.
   these functions convert to and from that format. */

    private static String print(Object x) {
        if (x instanceof String && 0 <= ((String) x).indexOf("\n"))
            throw new IllegalArgumentException("C++ tester cannot handle newlines in strings!");
        if (x instanceof Integer || x instanceof Character || x instanceof Long ||
                x instanceof Double || x instanceof String)
            return x + "\n";
        if (x instanceof Boolean)
            return ((Boolean) x).booleanValue() ? "1\n" : "0\n";
        if (x.getClass().isArray()) {
            int i, n = Array.getLength(x);
            String r = n + "\n";
            for (i = 0; i < n; i++)
                r += print(Array.get(x, i));
            return r;
        }
        throw new IllegalArgumentException("cannot format object of type " + x.getClass().getName());
    }

    private static Object parse(String type, String str) {
        return parse(type, new BufferedReader(new StringReader(str)));
    }

/* this only handles 1-d arrays */

    // TODO: this should be updated to parse C++ types, and not Java
    // once that is done, the four testers must be updated
    private static Object parse(String type, BufferedReader in) {
        String line;
        try {
            line = in.readLine();
        } catch (IOException e) {
            return null;
        }
        if (type.endsWith("[]")) {
            type = type.substring(0, type.length() - 2);
            int i, cnt = Integer.parseInt(line);
            Class c = mapToJavaClass(type);
            if (c == null)
                throw new IllegalArgumentException("cannot parse array with subtype " + type);
            Object r = Array.newInstance(mapToJavaClass(type), cnt);
            for (i = 0; i < cnt; i++)
                Array.set(r, i, parse(type, in));
            return r;
        }
        if (type.equals("char")) return new Character(line.charAt(0));
        if (type.equals("boolean")) return new Boolean(line.equals("1"));
        if (type.equals("int")) return new Integer(line);
        if (type.equals("long")) return new Long(line);
        if (type.equals("double")) return new Double(parseDouble(line));
        if (type.equals("String")) return new String(Base64Encoding.decode64(line));
        throw new IllegalArgumentException("cannot parse object of type " + type);
    }

    private static Class mapToJavaClass(String jt) {
        if (jt.equals("char")) return char.class;
        if (jt.equals("boolean")) return boolean.class;
        if (jt.equals("int")) return int.class;
        if (jt.equals("long")) return long.class;
        if (jt.equals("double")) return double.class;
        if (jt.equals("String")) return String.class;
        return null;
    }

    private static double parseDouble(String line) {
        String upperLine = line.toUpperCase();
        if ("NAN".equals(upperLine)) {
            return Double.NaN;
        } else if (upperLine.startsWith("INF") || upperLine.startsWith("+INF")) {
            return Double.POSITIVE_INFINITY;
        } else if (upperLine.startsWith("-INF")) {
            return Double.NEGATIVE_INFINITY;
        } else {
            return Double.parseDouble(line);
        }
    }
}
