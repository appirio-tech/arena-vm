/*
 * Copyright (C) 2007 - 2014 TopCoder Inc., All Rights Reserved.
 */

package com.topcoder.netCommon.contestantMessages.response.data;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * Defines the test result of an individual test case for the user submission in a practice room.<br>
 * Instances of this class are received in response to a practice system test request. When the actual
 * returned value is not available, <code>null</code> is set.
 *
 * <p>
 * Changes in version 1.1 (PoC Assembly - TopCoder Competition Engine - Support Custom Output Checker):
 * <ol>
 *     <li>Added {@link checkAnswerResponse} property and added/updated corresponding methods.
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.2 (PoC Assembly - Return Peak Memory Usage for Executing SRM Solution):
 * <ol>
 *      <li>Add {@link #maxMemoryUsed} field to include maximum memory used (in KB) and getter/setter added.</li>
 *      <li>Update {@link #PracticeTestResultData(int, int, int, int, boolean, String,
 *                          Object[], Object, Object, long, long, String) } </li>
 * </ol>
 * </p>
 *
 * @author Diego Belfer (mural), gevak, dexy
 * @version 1.2
 */
public class PracticeTestResultData implements Serializable, CustomSerializable {
    /**
     * Represents the ID of the round the tested submission belongs to.
     */
    private int roundId;

    /**
     * Represents the ID of the room the tested submission belongs to.
     */
    private int roomId;

    /**
     * Represents the ID of the problem component the tested submission belongs to.
     */
    private int componentId;

    /**
     * Represents the index of the test case for which this result was generated.
     */
    private int testCaseIndex;

    /**
     * Represents a flag indicating if the test case succeeded.
     */
    private boolean succeeded;

    /**
     * Represents the argument values for the test case.
     */
    private Object[] args;

    /**
     * Represents the expected value of the test case.
     */
    private Object expectedValue;

    /**
     * Represents the value return by the submission, if any.
     */
    private Object returnValue;

    /**
     * Represents a description message in case of failure.
     */
    private String message;

    /**
     * Represents the execution time of the test in milliseconds.
     */
    private long execTime;


    /**
     * Represents the memory used (in KB).
     *
     * @since 1.2
     */
    private long maxMemoryUsed;

    /**
     * Check answer response.
     *
     * @since 1.1
     */
    private String checkAnswerResponse;

    /**
     * Creates a new instance of <code>PracticeTestResultData</code>. It is required by custom serialization.
     */
    public PracticeTestResultData() {
    }

    /**
     * Creates a new instance of <code>PracticeTestResultData</code>. There is no copy.
     *
     * @param roundId the ID of the round the tested submission belongs to.
     * @param roomId the ID of the room the tested submission belongs to.
     * @param componentId the ID of the problem component the tested submission belongs to.
     * @param testCaseIndex the index of the test case for which this result was generated.
     * @param succeeded <code>true</code> if the test case succeeded; <code>false</code> otherwise.
     * @param message a description message in case of failure.
     * @param args the argument values for the test case.
     * @param expectedValue the expected value of the test case.
     * @param returnValue the value return by the submission.
     * @param execTime the execution time of the test in milliseconds.
     * @param maxMemoryUsed the memory used in KB
	 * @param checkAnswerResponse check answer response.
     */
    public PracticeTestResultData(int roundId, int roomId, int componentId, int testCaseIndex, boolean succeeded,
        String message, Object[] args, Object expectedValue, Object returnValue, long execTime, long maxMemoryUsed,
        String checkAnswerResponse) {
        this.roundId = roundId;
        this.roomId = roomId;
        this.componentId = componentId;
        this.testCaseIndex = testCaseIndex;
        this.succeeded = succeeded;
        this.expectedValue = expectedValue;
        this.returnValue = returnValue;
        this.message = message;
        this.args = args;
        this.execTime = execTime;
        this.maxMemoryUsed = maxMemoryUsed;
        this.checkAnswerResponse = checkAnswerResponse;
    }

    /**
     * Performs de-serialization.
     *
     * @param reader Reader.
     *
     * @throws IOException If any I/O error occurs.
     * @throws ObjectStreamException If any stream error occurs.
     **/
    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        roundId = reader.readInt();
        roomId = reader.readInt();
        componentId = reader.readInt();
        testCaseIndex = reader.readInt();
        succeeded = reader.readBoolean();
        message = reader.readString();
        expectedValue = reader.readObject();
        returnValue = reader.readObject();
        args = reader.readObjectArray();
        execTime = reader.readLong();
        maxMemoryUsed = reader.readLong();
        checkAnswerResponse = reader.readString();
    }

    /**
     * Performs serialization.
     *
     * @param writer Writer.
     *
     * @throws IOException If any I/O error occurs.
     **/
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(roundId);
        writer.writeInt(roomId);
        writer.writeInt(componentId);
        writer.writeInt(testCaseIndex);
        writer.writeBoolean(succeeded);
        writer.writeString(message);
        writer.writeObject(expectedValue);
        writer.writeObject(returnValue);
        writer.writeObjectArray(args);
        writer.writeLong(execTime);
        writer.writeLong(maxMemoryUsed);
        writer.writeString(checkAnswerResponse);
    }

    /**
     * Gets the execution time of the test in milliseconds.
     *
     * @return the execution time of the test in milliseconds.
     */
    public long getExecTime() {
        return execTime;
    }

    /**
     * Gets the memory used (in KB).
     *
     * @return the memory used in KB
     * @since 1.2
     */
    public long getMaxMemoryUsed() {
        return maxMemoryUsed;
    }

    /**
     * Sets the max memory used (in KB).
     *
     * @param maxMemoryUsed the new max memory used in KB
     * @since 1.2
     */
    public void setMaxMemoryUsed(long maxMemoryUsed) {
        this.maxMemoryUsed = maxMemoryUsed;
    }

    /**
     * Gets the ID of the problem component the tested submission belongs to.
     *
     * @return the problem component ID.
     */
    public int getComponentId() {
        return componentId;
    }

    /**
     * Sets the ID of the problem component the tested submission belongs to.
     *
     * @param componentId the problem component ID.
     */
    public void setComponentId(int componentId) {
        this.componentId = componentId;
    }

    /**
     * Gets a description message in case of failure.
     *
     * @return a description message in case of failure.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets a description message in case of failure.
     *
     * @param message a description message in case of failure.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the ID of the round the tested submission belongs to.
     *
     * @return the round ID.
     */
    public int getRoundId() {
        return roundId;
    }

    /**
     * Sets the ID of the round the tested submission belongs to.
     *
     * @param roundId the round ID.
     */
    public void setRoundId(int roundId) {
        this.roundId = roundId;
    }

    /**
     * Gets a flag indicating if the test case succeeded.
     *
     * @return <code>true</code> if the test case succeeded; <code>false</code> otherwise.
     */
    public boolean isSucceeded() {
        return succeeded;
    }

    /**
     * Sets a flag indicating if the test case succeeded.
     *
     * @param succeeded <code>true</code> if the test case succeeded; <code>false</code> otherwise.
     */
    public void setSucceeded(boolean succeeded) {
        this.succeeded = succeeded;
    }

    /**
     * Gets the index of the test case for which this result was generated.
     *
     * @return the index of the test case.
     */
    public int getTestCaseIndex() {
        return testCaseIndex;
    }

    /**
     * Sets the index of the test case for which this result was generated.
     *
     * @param testCaseIndex the index of the test case.
     */
    public void setTestCaseIndex(int testCaseIndex) {
        this.testCaseIndex = testCaseIndex;
    }

    /**
     * Gets the ID of the room the tested submission belongs to.
     *
     * @return the room ID.
     */
    public int getRoomId() {
        return roomId;
    }

    /**
     * Sets the ID of the room the tested submission belongs to.
     *
     * @param roomId the room ID.
     */
    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String toString() {
        return "PracticeTestResultData[" + "roundId=" + roundId + ", " + "roomId=" + roomId + ", " + "componentId="
            + componentId + ", " + "testCase=" + testCaseIndex + ", " + "success=" + succeeded + "]";
    }

    /**
     * Gets the expected value of the test case.
     *
     * @return the expected value of the test case.
     */
    public Object getExpectedValue() {
        return expectedValue;
    }

    /**
     * Sets the expected value of the test case.
     *
     * @param expectedValue the expected value of the test case.
     */
    public void setExpectedValue(Object expectedValue) {
        this.expectedValue = expectedValue;
    }

    /**
     * Gets the value return by the submission. When the submission does not return due to exception, <code>null</code>
     * is used.
     *
     * @return the value return by the submission.
     */
    public Object getReturnValue() {
        return returnValue;
    }

    /**
     * Sets the value return by the submission. When the submission does not return due to exception, <code>null</code>
     * is set.
     *
     * @param returnValue the value return by the submission.
     */
    public void setReturnValue(Object returnValue) {
        this.returnValue = returnValue;
    }

    /**
     * Gets the argument values for the test case. There is no copy.
     *
     * @return the argument values for the test case.
     */
    public Object[] getArgs() {
        return args;
    }

    /**
     * Sets the argument values for the test case.
     *
     * @param args the argument values for the test case.
     */
    public void setArgs(Object[] args) {
        this.args = args;
    }

    /**
     * Gets check answer response.
     *
     * @return Check answer response.
     * @since 1.1
     */
    public String getCheckAnswerResponse() {
        return checkAnswerResponse;
    }

    /**
     * Sets check answer response.
     *
     * @param checkAnswerResponse Check answer response.
     * @since 1.1
     */
    public void setCheckAnswerResponse(String checkAnswerResponse) {
        this.checkAnswerResponse = checkAnswerResponse;
    }
}
