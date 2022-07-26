/*
* Copyright (C) 2007-2014 TopCoder Inc., All Rights Reserved.
*/

/*
 * LongTestResult
 * 
 * Created 06/14/2007
 */
package com.topcoder.server.ejb.TestServices;

import java.io.Serializable;

/**
 *
 * <p>
 * Changes in version 1.1 (Return Peak Memory Usage for Marathon Match Cpp v1.0):
 * <ol>
 *      <li>Add {@link #peakMemoryUsed} field.</li>
 *      <li>Add {@link #getPeakMemoryUsed()} method.</li>
 *      <li>Update {@link #LongTestResult(String arg, double score, int peakMemoryUsed, long processingTime,
 *               String stdOut, String stdErr, String fatalErrors, Object result)} method.</li>
 * </ol>
 * </p>
 * @autor Diego Belfer (Mural), TCSASSEMBLER
 * @version 1.1
 */
public class LongTestResult implements Serializable {
    private String arg;
    private double score;
    private long processingTime;
    private String stdOut;
    private String stdErr;
    private String fatalErrors;
    private Object resultObject;
    /**
     * the peak memory used in KB.
     * @since 1.1
     */
    private long peakMemoryUsed;
    
    public LongTestResult() {
    }
    
    /**
     * The long test result constructor.
     * @param arg the problem arguments.
     * @param score the problem score.
     * @param peakMemoryUsed the peak memory used.
     * @param processingTime the execution time.
     * @param stdOut the standard output of problem.
     * @param stdErr the error output of problem.
     * @param fatalErrors the fatal error output of problem.
     * @param result the result object.
     */
    public LongTestResult(String arg, double score, long peakMemoryUsed, long processingTime, String stdOut, String stdErr, String fatalErrors, Object result) {
        this.arg = arg;
        this.score = score;
        this.processingTime = processingTime;
        this.stdOut = stdOut;
        this.stdErr = stdErr;
        this.fatalErrors = fatalErrors;
        this.resultObject = result;
        this.peakMemoryUsed = peakMemoryUsed;
    }
    /**
     * Getter the peak memory used.
     * @return the peak memory used.
     * @since 1.1
     */
    public long getPeakMemoryUsed() {
        return peakMemoryUsed;
    }
    
    public String getArg() {
        return arg;
    }

    public String getFatalErrors() {
        return fatalErrors;
    }

    public long getProcessingTime() {
        return processingTime;
    }

    public double getScore() {
        return score;
    }

    public String getStdErr() {
        return stdErr;
    }

    public String getStdOut() {
        return stdOut;
    }
    
    public Object getResultObject() {
        return resultObject;
    }

    public void setResultObject(Object resultObject) {
        this.resultObject = resultObject;
    }
}