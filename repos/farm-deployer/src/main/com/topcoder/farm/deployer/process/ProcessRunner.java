/*
 * ProcessRunner
 * 
 * Created 09/07/2006
 */
package com.topcoder.farm.deployer.process;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Runs an external process and waits for completion.
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ProcessRunner {
    private Log log = LogFactory.getLog(ProcessRunner.class);
    private long timeToWaitFor = 30000;
    private int maxStdOutSize = Integer.MAX_VALUE;
    private int maxStdErrSize = Integer.MAX_VALUE;
    private String[] cmd;
    private File folder;
 //FIXME DOC
    
    
    /**
     * Creates a new ProcessRunner that will run the given processBuilder using
     * a wait time of 30 seconds and the current folder as the working folder for
     * of the subprocess
     *  
     * @param cmd String array containing command string and its args
     */
    public ProcessRunner(String[]cmd) {
        this.cmd = cmd;
    }

    
    /**
     * Creates a new ProcessRunner that will run the given processBuilder using
     * a wait time of  30 seconds
     *  
     * @param cmd String array containing command string and its args
     * @param folder the working directory of the subprocess, or
     *                    <tt>null</tt> if the subprocess should inherit
     *                    the working directory of the current process. 
     */
    public ProcessRunner(String[]cmd, File folder) {
        this.cmd = cmd;
        this.folder = folder;
    }
    
    /**
     * Creates a new ProcessRunner that will run the given command using
     * a wait time of <code>timeToWaitFor</code>ms.
     *  
     * @param cmd String array containing command string and its args
     * @param folder the working directory of the subprocess, or
     *                    <tt>null</tt> if the subprocess should inherit
     *                    the working directory of the current process. 
     * @param timeToWaitFor The max time in ms to wait for process completation
     */
    public ProcessRunner(String[]cmd, File folder, int timeToWaitFor) {
        this.cmd = cmd;
        this.folder = folder;
        this.timeToWaitFor = timeToWaitFor;
    }
    /**
     * Runs the process and wait for its completion.<p>
     * 
     * If a time out occurs the process is destroyed.
     * 
     * @return the exit code returned by the process
     * 
     * @throws ProcessRunnerException  If the exception is raise during process creation
     * @throws ProcessTimeoutException If the max time to wait was reached.
     */
    public int run() throws ProcessTimeoutException, ProcessRunnerException {
        ProcessRunResult result = run(null);
        if (result.getExitCode() != 0) {
            log.info("ExitCode="+result.getExitCode());
            log.info("StdOut=\n"+result.getStdOut());
            log.info("StdErr=\n"+result.getStdErr());
        }
        return result.getExitCode();
    }
    
    
    /**
     * Runs the process and wait for its completion.<p>
     * 
     * If a time out occurs the process is destroyed.
     * 
     * @param  stdIn An stream containing data to be written to the process stdin
     * @return the ProcessRunResult containing information about the process run
     * 
     * @throws ProcessRunnerException If the exception is raise during process execution
     * @throws ProcessTimeoutException If the max time to wait was reached.
     */
    public ProcessRunResult run(InputStream stdIn) throws ProcessRunnerException, ProcessTimeoutException {
        String stdOut = null;
        String stdErr = null;
        boolean exit = false;
        StreamHelperThread stdOutThread = null;
        StreamHelperThread stdErrThread = null;
        try {
            int exitCode = 0;
            log.info("running "+Arrays.asList(cmd));
            Process process = Runtime.getRuntime().exec(cmd, null, folder);
            stdOutThread = new StreamHelperThread(process.getInputStream(), getMaxStdOutSize());
            stdErrThread = new StreamHelperThread(process.getErrorStream(), getMaxStdErrSize());
            try {
                writeStdInAndClose(process.getOutputStream(), stdIn);
                long maxTime = System.currentTimeMillis() + timeToWaitFor;
                while (maxTime > System.currentTimeMillis()) {
                    try {
                        exitCode = process.exitValue();
                        exit = true;
                        break;
                    } catch (Exception e) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e1) {
                        }
                    }
                }
            } finally {
                // Kill the process if not terminated
                if (!exit) {
                    process.destroy();
                }

                stdOutThread.quit();
                stdErrThread.quit();
                try {
                    stdOutThread.join();
                    stdErrThread.join();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                destroyProcess(process);
                stdOut = stdOutThread.getString();
                stdErr = stdErrThread.getString();
            }
            
            if (!exit) {
                throw new ProcessTimeoutException("Timeout waiting for process to finish. " + Arrays.asList(cmd), stdOut, stdErr);
            }
            return new ProcessRunResult(exitCode, stdOut, stdErr);
        } catch (IOException e) {
            throw new ProcessRunnerException(e, stdOut, stdErr);
        } finally {
            ensureStopped(stdOutThread);
            ensureStopped(stdErrThread);
        }
    }

        public int getMaxStdErrSize() {
        return maxStdErrSize;
    }

    public void setMaxStdErrSize(int maxStdErrSize) {
        this.maxStdErrSize = maxStdErrSize;
    }

    public int getMaxStdOutSize() {
        return maxStdOutSize;
    }

    public void setMaxStdOutSize(int maxStdOutSize) {
        this.maxStdOutSize = maxStdOutSize;
    }
    
    private void ensureStopped(StreamHelperThread readerThread) {
        if (readerThread != null && !readerThread.isDone()) {
            readerThread.interrupt();
        }
    }

    private void writeStdInAndClose(OutputStream outputStream, InputStream stdIn) throws IOException {
        if (stdIn != null) {
            byte[] b = new byte[4000];
            int size = 0;
            while (stdIn.available() > 0 && size != -1) {
                size = stdIn.read(b);
                if (size > 0) {
                    outputStream.write(b, 0, size);
                }
            }
        }
        outputStream.close();
    }

    protected void destroyProcess(Process process) {
        process.destroy();
        try { process.getErrorStream().close(); } catch (Exception e) {   }
        try { process.getOutputStream().close(); } catch (Exception e) {   }
        try { process.getInputStream().close(); } catch (Exception e) {   }
    }
    
    public static class ProcessRunResult {
        private int exitCode;
        private String stdOut;
        private String stdErr;
        
        public ProcessRunResult(int exitCode, String stdOut, String stdErr) {
            this.exitCode = exitCode;
            this.stdOut = stdOut;
            this.stdErr = stdErr;
        }
        
        public int getExitCode() {
            return exitCode;
        }
        public void setExitCode(int exitCode) {
            this.exitCode = exitCode;
        }
        public String getStdErr() {
            return stdErr;
        }
        public void setStdErr(String stdErr) {
            this.stdErr = stdErr;
        }
        public String getStdOut() {
            return stdOut;
        }
        public void setStdOut(String stdOut) {
            this.stdOut = stdOut;
        }
    }
}
