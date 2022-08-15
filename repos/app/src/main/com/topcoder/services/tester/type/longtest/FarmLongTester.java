/*
 * Copyright (C) - 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.services.tester.type.longtest;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Iterator;
import java.util.List;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.server.farm.longtester.FarmLongTestRequest;
import com.topcoder.server.tester.ComponentFiles;
import com.topcoder.server.tester.Solution;
import com.topcoder.server.util.FileUtil;
import com.topcoder.server.util.Java13Utils;
import com.topcoder.services.tester.common.LongTestResults;
import com.topcoder.services.tester.invoke.FarmSolutionInvokator;
import com.topcoder.services.tester.invoke.SolutionInvocator;
import com.topcoder.services.tester.java.SocketWrapper;
import com.topcoder.services.tester.java.StreamHelperThread;
import com.topcoder.services.util.LongTesterIO;
import com.topcoder.services.util.datatype.BoundaryChecker;
import com.topcoder.shared.common.LongRoundScores;
import com.topcoder.shared.common.ServicesConstants;
import com.topcoder.shared.problem.DataType;
import com.topcoder.shared.problem.ProblemConstants;
import com.topcoder.shared.util.concurrent.Waiter;
import com.topcoder.shared.util.logging.Logger;

/**
 * Process a test for a long component.  Executes a process containing the user's
 * code and communicates with it through STDIO.
 *
 * This class is a refactored version of com.topcoder.services.tester.type.longtest.LongTest
 * It allows testing of the solutions and submission.
 *
 * <p>
 *  Version 1.1 (TC Competition Engine Code Execution Time Issue) change notes:
 *  <ul>
 *      <li>Update {@link #doProcessLongTest(FarmLongTestRequest, boolean)} method to max the execution
 *      time limit configurable for every problem.</li>
 *  </ul>
 * </p>
 *
 * <p>
 * Changes in version 1.2 (TC Competition Engine - CPP Language Upgrade And Customization Support v1.0):
 * <ol>
 *      <li>Update {@link #doProcessLongTest(FarmLongTestRequest, boolean)} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.3 (TC Competition Engine - Python Language Upgrade And Customization Support v1.0):
 * <ol>
 *      <li>Added {@link #<li>DEFAULT_PYTHON_TEST_COMMAND} field.</li>
 *      <li>Added {@link #getApprovedPath(String approvedPath)} method.</li>
 *      <li>Added {@link #getPythonTestCommand(String pythonCommand)} method.</li>
 *      <li>Update {@link #doProcessLongTest(FarmLongTestRequest, boolean)} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.4 (TC Competition Engine - R Language Test Support v1.0):
 * <ol>
 *      <li>Update {@link #doProcessLongTest(FarmLongTestRequest, boolean)} method.</li>
 *      <li>Update {@link #getSubPath(ComponentFiles problemFiles)} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.5 (Release Assembly - TopCoder Competition Engine Improvement Series 2 v1.0):
 * <ol>
 *      <li>Added {@link #PYTHON_TEST_COMMAND_PROPERTY_NAME} constant.</li>
 *      <li>Updated {@link #getPythonTestCommand(String)} method to use configurable value.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.6 (TopCoder Competition Engine Improvement Series 3 v1.0):
 * <ol>
 *      <li>Update {@link #doProcessLongTest(FarmLongTestRequest, boolean)} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.7 (TopCoder Competition Engine - Support Large Memory Limit Settings):
 * <ol>
 *      <li>Update {@link #doProcessLongTest(FarmLongTestRequest, boolean)} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.8 (Return Peak Memory Usage for Marathon Match Cpp v1.0):
 * <ol>
 *      <li>Update {@link #doProcessLongTest(FarmLongTestRequest testRequest, boolean canFail)} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.9 (Return Peak Memory Usage for Marathon Match Java v1.0):
 * <ol>
 *      <li>Update {@link #doProcessLongTest(FarmLongTestRequest testRequest, boolean canFail)} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.10 (Module Assembly - Return Peak Memory Usage for Marathon Match - DotNet v1.0):
 * <ol>
 *      <li>Update {@link #doProcessLongTest(FarmLongTestRequest testRequest, boolean canFail)} method
 *      to read the peak memory used (in KB) from the buffer also for DotNet testing.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 2.0 (TopCoder Competition Engine - Stack Size Configuration For MM Problems v1.0):
 * <ol>
 *      <li>Update {@link #doProcessLongTest(FarmLongTestRequest testRequest, boolean canFail)} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 2.1 (Python3 Support):
 * <ol>
 *      <li>Added {@link #PYTHON3_TEST_COMMAND_PROPERTY_NAME} and {@link #DEFAULT_PYTHON3_TEST_COMMAND} fields.</li>
 *      <li>Update {@link #doProcessLongTest(FarmLongTestRequest, boolean)} method.</li>
 *      <li>Update {@link #getPythonTestCommand(String, boolean)} method.</li>
 * </ol>
 * </p>
 *
 * @author Diego Belfer (Mural), savon_cn, Selena, liuliquan
 * @version 2.1
 */
public class FarmLongTester {
    /**
     * This constant defines name for Python test command property. 
     */
    private static final String PYTHON_TEST_COMMAND_PROPERTY_NAME =
            "com.topcoder.services.tester.type.longtest.FarmLongTester.pythonTestCommand";
    private static final String PYTHON3_TEST_COMMAND_PROPERTY_NAME =
            "com.topcoder.services.tester.type.longtest.FarmLongTester.python3TestCommand";

    private static final int MAX_LOG_SIZE_TO_ANALYZE = 150*1024*1024;
    private static final int TIME_TO_WAIT_FOR_PROCESS = 2000;
    private static final Logger logger = Logger.getLogger(FarmLongTester.class);
    private static final boolean ANALYZE_LOG = getAnalyzeLog(); //False
    private static final int MAX_TRIES = 3;
    private static final String SANDBOX_FAILURE = "SANDOX_FAILED";
    private File baseFolder;
    
    /**
     * <p>
     * the default python test command.
     * </p>
     * @since 1.3
     */
    private static final String DEFAULT_PYTHON_TEST_COMMAND = "/usr/bin/python";
    private static final String DEFAULT_PYTHON3_TEST_COMMAND = "/usr/bin/python3";

    //private File workFolder;
    /**
     * Creates a new FarmLongTester
     */
    public FarmLongTester(File baseFolder, File workFolder) {
        this.baseFolder = baseFolder;
        //this.workFolder = workFolder;
    }


    public LongTestResults processLongTest(FarmLongTestRequest testRequest) {
        int tries = 0;
        LongTestResults results = null;
        while (tries < MAX_TRIES && results == null) {
            tries ++;
            try {
                results = doProcessLongTest(testRequest, tries < MAX_TRIES);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }
        return results;
    }
    
    /**
     * <p>
     * get the approved path, used in cpp and python
     * </p>
     * @param approvedPath the approved path.
     * @return approved path.
     */
    private String getApprovedPath(String approvedPath) {
        if (approvedPath != null && approvedPath.trim().length() > 0) {
            return " --approvedpath " + approvedPath;
        }
        return "";
    }

    
    /**
     * <p>
     * do the test of long problem.
     * </p>
     * @param testRequest the long test request.
     * @param canFail true=can be failure.
     * @return the test result.
     * @throws InterruptedException
     *       if any error occur or execution time exceed.
     */
    public LongTestResults doProcessLongTest(FarmLongTestRequest testRequest, boolean canFail) throws InterruptedException {
        boolean mustRetry = false;
        try {
            FarmSolutionInvokator solutionInvokator = new FarmSolutionInvokator(testRequest.getSolution());
            ComponentFiles problemFiles = testRequest.getComponentFiles();
            Object[] args = testRequest.getArguments();
            String className = testRequest.getClassName();
            int languageID = problemFiles.getLanguageId();
            logger.info("LANGUAGE IS: " + languageID);
            double score = 0;
            String message = null;
            boolean success = false;
            Object resultObject = null;

            BufferedInputStream br;
            BufferedReader stdoutReader;
            BufferedReader stderrReader;
            BufferedOutputStream bw;

            BufferedInputStream readBr;
            BufferedOutputStream readBw;

            String stdout = null;
            String stderr = null;

            String abortCmd = null;
            String abortCmd2 = null;

            long time = 0;
            /**
             * the initial memory used should be -1.
             */
            long peakMemoryUsed = -1;
            LongTestResults results = new LongTestResults();
            try {
                BoundaryChecker.validate(new DataType[]{new DataType("String")}, args, className, solutionInvokator);
                File pathPrefix = resolvePathPrefix(testRequest);
                String subPath = getSubPath(problemFiles);
                File fullPath = new File(pathPrefix, subPath);

                /* write out the executable */
                problemFiles.storeClasses(pathPrefix.getAbsolutePath());


                String testCase = (String) args[0];
                String cmd = "";
                String sh = ServicesConstants.RUN;
                /**
                 * support large memory usage
                 */
                int memory = testRequest.getProblemCustomSettings().getMemLimit();
                long memory_with_bytes =  ((long)memory) * 1024 * 1024;
                int stackLimitMB = testRequest.getProblemCustomSettings().getStackLimit();
                long stackLimitBytes = (stackLimitMB > 0) ? (((long)stackLimitMB) * 1024 * 1024) : 0;
                int executionTimeLimit = testRequest.getProblemCustomSettings().getExecutionTimeLimit();
                logger.info("Execution Time Is: " + executionTimeLimit);

                int maxThreads = 1; //change me if needed
                if(testRequest.getMaxThreadCount() > 1) {
                    maxThreads = testRequest.getMaxThreadCount();
                }
                String packageName = subPath.replace('/','.');
                if(languageID == ContestConstants.CPP){
                    cmd = ServicesConstants.SANDBOX2+" --maxcpu " + executionTimeLimit + " --maxwall "
                          + executionTimeLimit + " --port "+ ServicesConstants.MARATHON_PORT_NUMBER
                          + " --maxthreads "+maxThreads+" --maxmem "+ memory_with_bytes
                          + ((stackLimitBytes > 0) ? (" --maxstack " + stackLimitBytes) : "")
                          + " --config " + (maxThreads > 1 ?
                              ServicesConstants.SANDBOX2_THREADED_LONG_CONFIG: ServicesConstants.SANDBOX2_LONG_CONFIG);
                    //add the customization approved path from user input.
                    cmd = cmd + getApprovedPath(testRequest.getProblemCustomSettings().getCppApprovedPath()) + " " + fullPath;

                    abortCmd = buildAbortCommand(fullPath.toString());
                    abortCmd2 = "kill -9 ";
                }else if(languageID == ContestConstants.PYTHON || languageID == ContestConstants.PYTHON3){
                    boolean python3 = languageID == ContestConstants.PYTHON3;
                    String pythonCommandLine = getPythonTestCommand(testRequest.getProblemCustomSettings().getPythonCommand(), python3)+" " + fullPath + "/Wrapper.pyc";
                    
                    cmd = ServicesConstants.SANDBOX2+" --maxcpu " + executionTimeLimit + " --maxwall "
                        + executionTimeLimit + " --port "
                        + ServicesConstants.MARATHON_PORT_NUMBER + " --maxthreads "
                        + maxThreads + " --maxmem " + memory_with_bytes
                        + ((stackLimitBytes > 0) ? (" --maxstack " + stackLimitBytes) : "")
                        + " --config " + (python3 ? ServicesConstants.SANDBOX2_LONG_PYTON3_CONFIG : ServicesConstants.SANDBOX2_LONG_PYTON_CONFIG)
                        + " " + getApprovedPath(testRequest.getProblemCustomSettings().getPythonApprovedPath())
                        + " "+ pythonCommandLine;
                    abortCmd = buildAbortCommand(fullPath.toString());
                    abortCmd2 = "kill -9 ";
                } else if(languageID == ContestConstants.R) {
                    String rCommandLine = "/opt/R-2.15.3/bin/Rscript " + ServicesConstants.R_RUNNER + " " + fullPath + "/Wrapper.rlc";
                    cmd = ServicesConstants.SANDBOX2+" --maxcpu " + executionTimeLimit + " --maxwall " + executionTimeLimit + " --port "+ ServicesConstants.MARATHON_PORT_NUMBER+" --maxthreads "+maxThreads+" --maxmem "+memory_with_bytes+" --config " + ServicesConstants.SANDBOX2_LONG_R_CONFIG + " "+rCommandLine;
                    abortCmd = buildAbortCommand(fullPath.toString());
                    abortCmd2 = "kill -9 ";
                } else if(languageID == ContestConstants.JAVA){
                    sh = ServicesConstants.RUN_JAVA;
                    cmd = "-client -Xms" + memory + "m -Xmx" + memory + "m "
                            + ((stackLimitMB > 0) ? ("-Xss" + stackLimitMB + "m ") : "")
                            + "-Djava.security.policy="+ServicesConstants.TESTER_POLICY
                            + " -cp " + ServicesConstants.RESOURCES_FOLDER
                            + File.pathSeparator + ServicesConstants.APPS_CLASSES
                            + File.pathSeparator + pathPrefix
                            + " -DALLOW_THREADING=" + (maxThreads > 1 ? "true" : "false")
                            + " -DMAX_THREADS=" + maxThreads
                            + " -DMARATHON_PORT_NUMBER=" + ServicesConstants.MARATHON_PORT_NUMBER
                            + " " + packageName
                            + "." + ProblemConstants.WRAPPER_CLASS;
                    abortCmd = buildAbortCommand(packageName);
                    abortCmd2 = "kill -9 ";
                }else if(languageID == ContestConstants.CSHARP || languageID == ContestConstants.VB){
                    sh = "";
                    cmd = ServicesConstants.DOTNET_SANDBOX + " \"" + fullPath + "\" " + memory + " "
                        + stackLimitBytes + " " + maxThreads + " " + ServicesConstants.MARATHON_PORT_NUMBER;
                    abortCmd = "taskkill /t /F /PID ";
                    abortCmd2 = "taskkill /F /IM LongWrapper.exe";
                }else{
                    throw new RuntimeException("Invalid language: "+languageID);
                }
                logger.debug("Current dir: "+ System.getProperty("user.dir"));
                logger.debug((sh+" "+cmd).replace('/',File.separatorChar));

                //here we want to start our server socket.  We use the port number in ServicesConstants
                LongTesterIO.initialize(ServicesConstants.MARATHON_PORT_NUMBER);

                logger.debug("POST INIT");


                final Process p = Runtime.getRuntime().exec((sh+" "+cmd).replace('/',File.separatorChar), null, new File(System.getProperty("user.dir")));

                logger.debug("POST SPAWN");

                stdoutReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                stderrReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));

                String pid = stdoutReader.readLine().trim();
                if(languageID != ContestConstants.CSHARP && languageID != ContestConstants.VB) {
                    abortCmd2 = abortCmd2 + pid;
                } else {
                    abortCmd = abortCmd + pid;
                }

                logger.debug("POST PID: "+pid);

                Socket client = null;
                Socket readClient = null;
                try {
                    client = LongTesterIO.getSocket();
                    readClient = LongTesterIO.getReadSocket();
                } catch (IOException ioe) {
                    //timeout usally
                    LongTesterIO.shutdownSocket();
                    throw ioe;
                }

                logger.debug("POST SOCKET");

                SocketWrapper sw = new SocketWrapper(client);
                br = sw.getInputStream();
                bw = sw.getOutputStream();

                SocketWrapper readSw = new SocketWrapper(readClient);
                readBr = readSw.getInputStream();
                readBw = readSw.getOutputStream();

                final AbortThread at = new AbortThread(executionTimeLimit, abortCmd, abortCmd2);
                at.start();

                logger.debug("Calling solution with arg:");
                logger.debug(testCase);
                Double result = (Double)solutionInvokator.callSolutionMethod(SolutionInvocator.RUN_TEST,new Object[]{br,bw,readBr,readBw,subPath,testCase});

                try {
                    LongTesterIO.terminate(bw);
                    /**
                     * get the peak memory used for java,dotNet,cpp and python.
                     */
                    if (languageID == ContestConstants.JAVA || languageID == ContestConstants.CPP ||
                            languageID == ContestConstants.PYTHON || languageID == ContestConstants.CSHARP ||
                            languageID == ContestConstants.VB) {
                        long tmp = 0;
                        do {
                           tmp =  LongTesterIO.getLong(br);
                        } while (tmp == 0 && br.available() > 0);
                        peakMemoryUsed = (tmp == 0) ? -1 : tmp;
                    }
                    
                    br.close();
                    bw.close();
                    client.close();

                    readBr.close();
                    readBw.close();
                    readClient.close();


                } catch (IOException ioe) {
                    logger.error("Exception terminating long testerIO", ioe);
                }

                logger.debug("POST TERMINATE");

                success = true;

                List exceptions = LongTesterIO.getExceptions();
                if(exceptions.size() > 0) {
                    success = false;
                    message = "";
                    for(int i = 0; i < exceptions.size(); i++) {
                        message += (String)exceptions.get(i) + " ";
                    }
                    message = message.trim();
                    //clean up stack traces
                    message = message.replace(packageName + ".","");
                }
                Thread cancelThread = new Thread() {
                    public void run() {
                        try {
                            p.waitFor();
                            logger.debug("waitFor SUCCEEDED");
                            at.done();
                        } catch (InterruptedException e) {
                        } finally {
                            p.destroy();
                        }
                    }
                };

                cancelThread.start();
                cancelThread.join(TIME_TO_WAIT_FOR_PROCESS);
                at.cancel();
                cancelThread.join();
                at.join();

                if(p.exitValue() == 1000)
                    message = "Program execution exceeded the thread limit";

                //its important to kill the process first and then close the streams, it seems
                stdoutReader.close();
                stderrReader.close();
                p.getInputStream().close();

                //cleanup stdout / err files
                //directory is results.PID
                File resultdir = new File(baseFolder, "results." + pid);;

                stdout = slurpTextFile(new File(resultdir,"stdout"));
                stderr = slurpTextFile(new File(resultdir, "stderr"));

                //We need to catch invalid system calls
                File logFile = new File(resultdir,"log");
                if (languageID == ContestConstants.CPP && shouldAnalyzeLog()) {
                    String additionaError = analizeLog(logFile);
                    if (additionaError == SANDBOX_FAILURE) {
                        if (canFail) {
                            mustRetry = true;
                            logger.error("SANDBOX: weird timing forcing retest");
                        } else {
                            logger.error("SANDBOX: weird timing max tries reached, can't retest. Flagging message.");
                            if (message == null) {
                                message = "{SWT}\n";
                            } else {
                                message = "{SWT}\n"+message;
                            }
                        }
                    } else {
                        if (additionaError != null) {
                            logger.error("SANDBOX: "+additionaError);
                            stderr = additionaError + stderr;
                        }
                    }
                }

                logger.debug("MESSAGE: " + message);
                logger.debug("STDOUT : " + stdout);
                logger.debug("STDERR : " + stderr);
                
                if (!getKeepResultFolder() && !mustRetry) {
                    new File(resultdir, "done").delete();
                    new File(resultdir, "core").delete();
                    new File(resultdir,"stdout").delete();
                    new File(resultdir,"stderr").delete();
                    new File(resultdir,"result").delete();
                    new File(resultdir,"backtrace").delete();
                    logFile.delete();
                    new File(resultdir,"temp").delete();
                    resultdir.delete();
                }
                score = result.doubleValue();
                logger.info("score = "+score);
                
                resultObject = LongTesterIO.getResultObject();
                
                time = LongTesterIO.getTime();
                logger.info("time = "+time);
            } catch (Exception e) {
                logger.error("Exception while running test.", e);
            } finally {
                try {
                    LongTesterIO.shutdownSocket();
                } catch (Exception e) {

                }
            }
            if (!mustRetry) {
                results.setSuccess(success);
                results.setMessage(message);
                results.setScore(score);
                /**
                 * the R language will generate the stderr that not seem like really stderr
                 * we must ignore it when the score is large than 0
                 */
                if(languageID != ContestConstants.R || score == 0) {
                    results.setStderr(stderr);
                }
                results.setStdout(stdout);
                results.setTime(time);
                results.setResultObject(resultObject);
                results.setPeakMemoryUsed(peakMemoryUsed);
                return results;
            } else  {
                Thread.sleep(1000);
                return null;
            }
        } catch (Error e) {
            logger.error("ERROR main method!", e);
            System.exit(0);
            return null;
        }
    }


    private boolean shouldAnalyzeLog() {
        return ANALYZE_LOG;
    }


    private String analizeLog(File logFile) {
        try {
            if (logFile.exists()) {
                if (logFile.length() > MAX_LOG_SIZE_TO_ANALYZE) {
                    logger.warn("Avoiding log analysis, file is too long");
                    return null;
                }
                String contents = FileUtil.getStringContents(logFile);
                //Catch weird timings in AMD boxes
                if (contents.indexOf("ptrace took") > -1 || contents.indexOf("SYSCALL TIME") > -1) {
                    return SANDBOX_FAILURE;
                }
                //Catch invalid system calls
                String text = "Unrecognized system call ";
                int pos = contents.indexOf(text);
                if (pos > -1) {
                    int endPos = pos + text.length() + 20;
                    if (endPos > contents.length()) {
                        endPos = contents.length();
                    }
                    String callToReport = contents.substring(pos, endPos);
                    if (callToReport.indexOf(" 252") == -1) { 
                        return "Your submission tried to execute a forbidden system call: '" +callToReport+"'"+".\nPlease contact Admins.\n";
                    }
                }

            }
        } catch (Exception e) {
            logger.error("Could not analize log file",e);
        }
        return null;
    }


    private String buildAbortCommand(String textToSearch) {
        return ServicesConstants.PKILL+" "+getUserName()+" "+textToSearch.substring(0,1)+" "+ textToSearch.substring(1);
    }


    private String getUserName() {
        return System.getProperty("user.name", "farm");
    }

    /**
     * Resolve the path prefix that has to be used depending of the
     * the type test of request
     *
     * @param testRequest Test request
     *
     * @return PathPrefix that has to be used
     */
    private File resolvePathPrefix(FarmLongTestRequest testRequest) {
        return new File(testRequest.getCodeType() == FarmLongTestRequest.CODE_TYPE_SOLUTION ?
                ServicesConstants.SOLUTIONS :
                ServicesConstants.LONG_SUBMISSIONS);
    }

    /**
     * Builds the fullpath  (prefix + package + Class/Exe) used as argument
     * of the command line
     *
     * @param problemFiles Files of the solution/submission to test
     * @param pathPrefix PrefixPath where files are stored
     *
     * @return The fullpath to the mainclass or exe for the solution/submission
     *          Null if not expected file could not be found.
     */
    private String getSubPath(ComponentFiles problemFiles) {
        int languageID =  problemFiles.getLanguageId();
        for (Iterator it = problemFiles.getClassMap().keySet().iterator(); it.hasNext();) {
            String path = (String) it.next();
            if (path.endsWith(problemFiles.getComponentName()) || path.endsWith(problemFiles.getComponentName()+".exe")) {
                if(languageID == ContestConstants.JAVA){
                    return path.substring(0, path.lastIndexOf('.')).replace('.','/');
                }else if(languageID == ContestConstants.CPP){
                    return path;
                }else if(languageID == ContestConstants.CSHARP || languageID == ContestConstants.VB){
                    return path;
                } else if (languageID == ContestConstants.PYTHON || languageID == ContestConstants.PYTHON3) {
                    return path.substring(0, path.lastIndexOf('.')).replace('.','/');
                }  else if (languageID == ContestConstants.R) {
                    return path.substring(0, path.lastIndexOf('.')).replace('.','/');
                }
                return null;
            }
        }
        return null;
    }

    private static String slurpTextFile(File f) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            int amount = Math.min(20000, (int)f.length());
            char[] buf = new char[amount];
            br.read(buf);
            br.close();
            String str = new String(buf);
            if (str.trim().length() == 0) {
                return "";
            }
            return str;
        } catch (Exception e) {
            return null;
        }
    }

    public LongRoundScores recalculateFinalScores(Solution solution, LongRoundScores lrr) {
        try {
            List s = lrr.getScores();
            double[][] d = new double[s.size()][];
            for(int i = 0; i<d.length; i++){
                List al = (List)s.get(i);
                d[i] = new double[al.size()];
                for(int j = 0; j<d[i].length; j++){
                    d[i][j] = ((Double)al.get(j)).doubleValue();
                }
            }
            double[] fin = (double[]) new FarmSolutionInvokator(solution).callSolutionMethod(SolutionInvocator.SCORE, new Object[]{d});
            lrr.setFinalScores(fin);
            return lrr;
        } catch (Exception e) {
            logger.error("Error calculating scores for componentId=" + lrr.getComponentID() + " roundId=" + lrr.getRoundID(),e);
            e.printStackTrace();
            return null;
        }
    }
    
    private static boolean getAnalyzeLog() {
        try {
            return Boolean.parseBoolean(System.getProperty("com.topcoder.services.tester.type.longtest.FarmLongTester.analyzeLog"));
        } catch (Exception e) {
            return false;
        }
    }
    
    private static boolean getKeepResultFolder() {
        try {
            return Boolean.parseBoolean(System.getProperty("com.topcoder.services.tester.type.longtest.FarmLongTester.keepResultFolder"));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * <p>
     * Gets the Python test command.
     * </p>
     *
     * @param customCommand The custom Python test command.
     *      If null/empty, then pre-configured/default value will be used.
     * @param python3 Whether for python3
     * @return The Python command to be used.
     */
    private static String getPythonTestCommand(String customCommand, boolean python3) {
        if(customCommand == null || customCommand.trim().length() == 0) {
        	if (python3) {
                return System.getProperty(PYTHON3_TEST_COMMAND_PROPERTY_NAME, DEFAULT_PYTHON3_TEST_COMMAND);
        	}
            return System.getProperty(PYTHON_TEST_COMMAND_PROPERTY_NAME, DEFAULT_PYTHON_TEST_COMMAND);
        }
        return customCommand;
    }

    private class AbortThread extends Thread {
        private long timeout;
        private String killString;
        private String killString2;
        private boolean done;
        private final Object mutex = new Object();
        private boolean canceled;

        public AbortThread(long t, String s) {
            this(t,s,null);
        }

        public AbortThread(long t, String s, String s2) {
            timeout = t;
            killString = s;
            killString2 = s2;
            logger.debug("Kill String 1: "+killString);
            logger.debug("Kill String 2: "+killString2);
            done = false;
            canceled = false;
        }

        public void run() {
            try {
                synchronized (mutex) {
                    Waiter waiter = new Waiter(timeout, mutex);
                    while (!waiter.elapsed() && !done & !canceled) {
                        waiter.await();
                    }
                }
//                if(!done) {
                    if (done) {
                        Thread.sleep(1000);
                    } else  {
                        logger.info("ABNORMAL ABORT, FIX ME");
                    }
                    executeTask(killString);
                    executeTask(killString2);
//                }
            } catch (InterruptedException e) {
                logger.error("InterruptedException in AbortThread", e);
            }
        }


        private void executeTask(String cmd) {
            logger.info("executing: "+cmd);
            try {
                Process p = Runtime.getRuntime().exec(cmd);
                StreamHelperThread out = new StreamHelperThread(p.getInputStream());
                StreamHelperThread err = new StreamHelperThread(p.getErrorStream());
                boolean exit = false;
                try {
                    int r = p.exitValue();
                    exit = true;
                } catch (Exception e) {
                    try {
                        p.waitFor();
                        exit = true;
                    } catch (InterruptedException e1) {
                        logger.error("InterruptedException waiting for abort task "+cmd, e1);
                        Thread.currentThread().interrupt();
                    }
                } finally {
                    out.quit();
                    err.quit();
                    try {
                        if (!Thread.currentThread().isInterrupted()) {
                            out.join();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    try {
                        if (!Thread.currentThread().isInterrupted()) {
                            err.join();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    if (!exit) {
                        try {
                            p.destroy();
                        } catch (Exception e) {
                            logger.error("Exception in destroy for task "+ cmd, e);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("Exception for task "+ cmd, e);
            }
        }

        public void done() {
            synchronized (mutex) {
                done = true;
                mutex.notify();
            }
        }

        public void cancel() {
            synchronized (mutex) {
                canceled = true;
                mutex.notify();
            }
        }

    }
}

