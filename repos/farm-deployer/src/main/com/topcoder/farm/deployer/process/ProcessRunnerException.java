/*
 * ProcessRunnerException
 * 
 * Created 01/12/2007
 */
package com.topcoder.farm.deployer.process;

/**
 * Exception thrown when a problem occurred while trying or executing a process<p>
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ProcessRunnerException extends Exception {
    private String stdOut;
    private String stdErr;
    
    public ProcessRunnerException(Throwable e, String stdOut, String stdErr) {
        this(e.getMessage(), e, stdOut, stdErr);
    }

    public ProcessRunnerException(String message, Throwable e, String stdOut, String stdErr) {
        super(message, e);
        this.stdOut = stdOut;
        this.stdErr = stdErr;
    }

    /**
     * @return The stdErr collected from the process, if any could be collected
     */
    public String getStdErr() {
        return stdErr;
    }

    /**
     * @return The stdOut collected from the process, if any could be collected
     */
    public String getStdOut() {
        return stdOut;
    }
}
