/*
* Copyright (C) 2007 - 2014 TopCoder Inc., All Rights Reserved.
*/

package com.topcoder.netCommon.contestantMessages.response.data;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;

import com.topcoder.netCommon.contestantMessages.response.LongTestResultsResponse;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * Defines the result of an indiviudual test case for a marathon problem.<br>
 * Instances of this class are received in response to long example results request, or detailed long test result
 * request.
 *
 * <p>
 * Changes in version 1.1 (Return Peak Memory Usage for Marathon Match Cpp v1.0):
 * <ol>
 *      <li>Add {@link #peakMemoryUsed} field.</li>
 *      <li>Update {@link #LongTestResultData(int testCaseIndex, double score, int peakMemoryUsed,
 *           String message, long execTime)} field.</li>
 *      <li>Update {@link #LongTestResultData(int testCaseIndex, double score, String message, long execTime, String stdOut,
 *           String stdErr, int peakMemoryUsed)} method.</li>
 *      <li>Add {@link #getPeakMemoryUsed()} method.<li>
 *      <li>Add {@link #setPeakMemoryUsed(long peakMemoryUsed)} method.<li>
 *      <li>Update {@link #toString()} method.<li>
 * </ol>
 * </p>
 * @author Diego Belfer (mural), TCSASSEMBLER
 * @version 1.1
 * @see LongTestResultsResponse
 */
public class LongTestResultData implements Serializable, CustomSerializable {
    /**
     * Represents the index of the test case for which this result was generated.
     */
    private int testCaseIndex;

    /**
     * Represents the score for the given test.
     */
    private double score;
    /**
     * The peak memory used.
     * @since 1.1
     */
    private long peakMemoryUsed;
    
    /**
     * Represents the description message in case of failure.
     */
    private String message;

    /**
     * Represents the execution time of the test in milliseconds.
     */
    private long execTime;

    /**
     * Represents the content of the standard output if any.
     */
    private String stdOut;

    /**
     * Represents the content of the standard error if any.
     */
    private String stdErr;

    /**
     * Creates a new instance of <code>LongTestResultData</code>. It is required by custom serialization.
     */
    public LongTestResultData() {
    }

    /**
     * Creates a new instance of <code>LongTestResultData</code>.
     * 
     * @param testCaseIndex the index of the test case for which this result was generated.
     * @param score the score for the given test.
     * @param message the description message in case of failure.
     * @param execTime the execution time of the test in milliseconds.
     * @param stdOut the content of the standard output.
     * @param stdErr the content of the standard error.
     * @param peakMemoryUsed the peak memory used.
     */
    public LongTestResultData(int testCaseIndex, double score, String message, long execTime, String stdOut,
        String stdErr, long peakMemoryUsed) {
        this.testCaseIndex = testCaseIndex;
        this.score = score;
        this.message = message;
        this.execTime = execTime;
        this.stdOut = stdOut;
        this.stdErr = stdErr;
        this.peakMemoryUsed = peakMemoryUsed;
    }

    /**
     * Creates a new instance of <code>LongTestResultData</code>. The standard output and standard error contents are
     * not available.
     * 
     * @param testCaseIndex the index of the test case for which this result was generated.
     * @param score the score for the given test.
     * @param message the description message in case of failure.
     * @param execTime the execution time of the test in milliseconds.
     * @param peakMemoryUsed the peak memory used.
     */
    public LongTestResultData(int testCaseIndex, double score, String message, long execTime, long peakMemoryUsed) {
        this(testCaseIndex, score, message, execTime, null, null, peakMemoryUsed);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        testCaseIndex = reader.readInt();
        score = reader.readDouble();
        message = reader.readString();
        execTime = reader.readLong();
        stdOut = reader.readString();
        stdErr = reader.readString();
        peakMemoryUsed = reader.readLong();
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(testCaseIndex);
        writer.writeDouble(score);
        writer.writeString(message);
        writer.writeLong(execTime);
        writer.writeString(stdOut);
        writer.writeString(stdErr);
        writer.writeLong(peakMemoryUsed);
    }

    /**
     * Gets the score for the given test.
     * 
     * @return the score for the given test.
     */
    public double getScore() {
        return score;
    }

    /**
     * Sets the score for the given test.
     * 
     * @param score the score for the given test.
     */
    public void setScore(double score) {
        this.score = score;
    }

    /**
     * Gets the content of the standard error. When the standard error is not available, <code>null</code> is
     * returned.
     * 
     * @return the content of the standard error.
     */
    public String getStdErr() {
        return stdErr;
    }
    /**
     * Getter the peak memory used.
     * @return the peak memory used.
     * @since 1.1
     */
    public long getPeakMemoryUsed() {
        return peakMemoryUsed;
    }
    /**
     * Setter the peak memory used.
     * @param peakMemoryUsed the peak memory used.
     * @since 1.1
     */
    public void setPeakMemoryUsed(long peakMemoryUsed) {
        this.peakMemoryUsed = peakMemoryUsed;
    }

    /**
     * Sets the content of the standard error. When the standard error is not available, <code>null</code> is set.
     * 
     * @param stdErr the content of the standard error.
     */
    public void setStdErr(String stdErr) {
        this.stdErr = stdErr;
    }

    /**
     * Gets the content of the standard output. When the standard output is not available, <code>null</code> is
     * returned.
     * 
     * @return the content of the standard output.
     */
    public String getStdOut() {
        return stdOut;
    }

    /**
     * Sets the content of the standard output. When the standard output is not available, <code>null</code> is set.
     * 
     * @param stdOut the content of the standard output.
     */
    public void setStdOut(String stdOut) {
        this.stdOut = stdOut;
    }

    /**
     * Gets the index of the test case for which this result was generated.
     * 
     * @return the index of the test case for which this result was generated.
     */
    public int getTestCaseIndex() {
        return testCaseIndex;
    }

    /**
     * Sets the index of the test case for which this result was generated.
     * 
     * @param testCaseIndex the index of the test case for which this result was generated.
     */
    public void setTestCaseIndex(int testCaseIndex) {
        this.testCaseIndex = testCaseIndex;
    }

    /**
     * Sets the execution time of the test in milliseconds.
     * 
     * @param execTime the execution time of the test.
     */
    public void setExecTime(long execTime) {
        this.execTime = execTime;
    }

    /**
     * Gets the execution time of the test in milliseconds.
     * 
     * @return the execution time of the test.
     */
    public long getExecTime() {
        return execTime;
    }

    /**
     * Gets the description message in case of failure.
     * 
     * @return the description message in case of failure.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the description message in case of failure.
     * 
     * @param message the description message in case of failure.
     */
    public void setMessage(String message) {
        this.message = message;
    }
    /**
     * The toString method.
     * @return the object digest.
     */
    public String toString() {
        return "LongTestResultData[" + "testCase=" + testCaseIndex + ", " + "score=" + score +
                ", " + "peakMemoryUsed=" + peakMemoryUsed + ", " + "message=" + message + "]";
    }

}
