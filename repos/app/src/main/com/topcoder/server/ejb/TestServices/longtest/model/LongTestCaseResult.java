/*
* Copyright (C) 2006 - 2014 TopCoder Inc., All Rights Reserved.
*/

package com.topcoder.server.ejb.TestServices.longtest.model;

import java.io.Serializable;

/**
 *
 * <p>
 * Changes in version 1.1 (Return Peak Memory Usage for Marathon Match Cpp v1.0):
 * <ol>
 *      <li>Add {@link #peakMemoryUsed} field.</li>
 *      <li>Add {@link #getPeakMemoryUsed()} method.</li>
 *      <li>Add {@link #setPeakMemoryUsed(Integer peakMemoryUsed)} method.</li>
 *      <li>Update {@link #LongTestCaseResult(boolean success, Double score, String message, Long processingTime,
 *                      String stdout, String stderr, Integer peakMemoryUsed)} method.</li>
 * </ol>
 * </p>
 * @author Diego Belfer (Mural), savon_cn
 * @version 1.1
 */
public class LongTestCaseResult implements Serializable {
    private boolean success;
    private Double score;
    private String message;
    private Long processingTime;
    private String stdout;
    private String stderr;
    /**
     * The peak memory uesd in KB.
     * @since 1.1
     */
    private Long peakMemoryUsed;
    
    public LongTestCaseResult() {
    }
    
    public LongTestCaseResult(boolean success, Double score, String message, Long processingTime) {
        this.success = success;
        this.score = score;
        this.message = message;
        this.processingTime = processingTime;
    }
    
    /**
     * The constructor of long test case result.
     * @param success the flag of test case result.
     * @param score the test case score.
     * @param message the message of test case.
     * @param processingTime the execution time of test case.
     * @param stdout the standard output of test case.
     * @param stderr the error output of test case.
     * @param peakMemoryUsed the peak memory used.
     */
    public LongTestCaseResult(boolean success, Double score, String message, Long processingTime, String stdout, String stderr, Long peakMemoryUsed) {
        this.success = success;
        this.score = score;
        this.message = message;
        this.processingTime = processingTime;
        this.stdout = stdout;
        this.stderr = stderr;
        this.peakMemoryUsed = peakMemoryUsed;
    }
    /**
     * Getter the peak memory used.
     * @return the peak memory used.
     * @since 1.1
     */
    public Long getPeakMemoryUsed() {
        return peakMemoryUsed;
    }
    /**
     * Setter the peak memory used.
     * @param peakMemoryUsed the peak memory uesd.
     * @since 1.1
     */
    public void setPeakMemoryUsed(Long peakMemoryUsed) {
        this.peakMemoryUsed = peakMemoryUsed;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public Long getProcessingTime() {
        return processingTime;
    }
    public void setProcessingTime(Long processingTime) {
        this.processingTime = processingTime;
    }
    public Double getScore() {
        return score;
    }
    public void setScore(Double score) {
        this.score = score;
    }
    public String getStderr() {
        return stderr;
    }
    public void setStderr(String stderr) {
        this.stderr = stderr;
    }
    public String getStdout() {
        return stdout;
    }
    public void setStdout(String stdout) {
        this.stdout = stdout;
    }
    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }
}