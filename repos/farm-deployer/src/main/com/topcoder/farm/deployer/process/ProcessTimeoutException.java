/*
 * ProcessTimeoutException
 * 
 * Created 09/07/2006
 */
package com.topcoder.farm.deployer.process;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ProcessTimeoutException extends Exception {
    private String stdOut;
    private String stdErr;
    
    public ProcessTimeoutException() {
    }

    public ProcessTimeoutException(String message, String stdOut, String stdErr) {
        super(message);
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
