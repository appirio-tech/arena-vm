/*
 * Copyright (C)  - 2014 TopCoder Inc., All Rights Reserved.
 */

package com.topcoder.server.common;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.topcoder.server.tester.ComponentFiles;
import com.topcoder.server.tester.Solution;
import com.topcoder.services.tester.common.TestRequest;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.problem.SimpleComponent;

/**
 * <p>
 * DTO for system test attributes.
 * </p>
 *
 * <p>
 * Changes in version 1.1 (PoC Assembly - TopCoder Competition Engine - Support Custom Output Checker):
 * <ol>
 *      <li>Added {@link #checkAnswerResponse} field and added/updated all corresponding methods.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.2 (PoC Assembly - Return Peak Memory Usage for Executing
 * SRM Solution):
 * <ol>
 *      <li>Add {@link #maxMemoryUsed} field with getter/setter.</li>
 *      <li>Update {@link #customReadObject(CSReader)}, {@link #customWriteObject(CSWriter)}.</li>
 * </ol>
 * </p>
 *
 * @author gevak, dexy
 * @version 1.2
 */
public final class SystemTestAttributes implements Serializable, TestRequest {
    // unintuitively, this also represents a checkData failure (invalid args)
    public static final byte RESULT_SYSTEM_FAILURE = -1;
    public static final byte RESULT_CORRECT = 0;
    public static final byte RESULT_INCORRECT = 1;
    public static final byte RESULT_EXCEPTION = 2;
    public static final byte RESULT_TIMEOUT = 3;

    private boolean practice;
    private Submission submission;
    private Solution solution;
    private int testCaseId;
    private int testCaseIndex;
    private Object[] args;
    private Object resultValue;
    private Object expectedResult;
    private int resultCode;
    private String message;
    private List dependencyComponentFiles;
    private Map compiledWebServiceClientFiles;
    private boolean exclusiveExecution;
    private ComponentFiles componentFiles;
    private long execTime;

    /**
     * The maximum memory used in KB.
     *
     * @since 1.2
     */
    private long maxMemoryUsed;
    private int systemTestVersion;

    /**
     * Check answer response.
     *
     * @since 1.1
     */
    private String checkAnswerResponse;

    @JsonTypeInfo(use = Id.CLASS, include = As.PROPERTY, property = "@class")
    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    @JsonTypeInfo(use = Id.CLASS, include = As.PROPERTY, property = "@class")
    public Object getExpectedResult() {
        return expectedResult;
    }

    public void setExpectedResult(Object expectedResult) {
        this.expectedResult = expectedResult;
    }

    @JsonTypeInfo(use = Id.CLASS, include = As.PROPERTY, property = "@class")
    public Object getResultValue() {
        return resultValue;
    }

    public void setResultValue(Object resultValue) {
        this.resultValue = resultValue;
    }

    public Submission getSubmission() {
        return submission;
    }

    public void setSubmission(Submission submission) {
        this.submission = submission;
    }

    public Solution getSolution() {
        return solution;
    }

    public ComponentFiles getComponentFiles() {
        return componentFiles;
    }

    @JsonTypeInfo(use = Id.CLASS, include = As.PROPERTY, property = "@class")
    public List getDependencyComponentFiles() {
        return dependencyComponentFiles;
    }

    @JsonTypeInfo(use = Id.CLASS, include = As.PROPERTY, property = "@class")
    public Map getCompiledWebServiceClientFiles() {
        return compiledWebServiceClientFiles;
    }

    public void setComponentFiles(ComponentFiles componentFiles) {
        this.componentFiles = componentFiles;
    }

    public void setCompiledWebServiceClientFiles(Map compiledWebServiceClientFiles) {
        this.compiledWebServiceClientFiles = compiledWebServiceClientFiles;
    }

    public void setDependencyComponentFiles(List dependencyComponentFiles) {
        this.dependencyComponentFiles = dependencyComponentFiles;
    }

    public void setSolution(Solution solution) {
        this.solution = solution;
    }

    @JsonIgnore
    public SimpleComponent getComponent() {
        return submission.getComponent();
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setExecTime(long execTime) {
        this.execTime = execTime;
    }

    public long getExecTime() {
        return execTime;
    }

    /**
     * Sets the maximum memory used in KB.
     *
     * @param maxMemoryUsed the new max memory used.
     * @since 1.2
     */
    public void setMaxMemoryUsed(long maxMemoryUsed) {
        this.maxMemoryUsed = maxMemoryUsed;
    }

    /**
     * Gets the maximum memory used in KB.
     *
     * @return the max memory used
     * @since 1.2
     */
    public long getMaxMemoryUsed() {
        return maxMemoryUsed;
    }

    public int getTestCaseId() {
        return testCaseId;
    }

    public void setTestCaseId(int testCaseId) {
        this.testCaseId = testCaseId;
    }

    @JsonIgnore
    public boolean isTimeOut() {
        return resultCode == RESULT_TIMEOUT;
    }

    @JsonIgnore
    public boolean isSystemFailure() {
        return resultCode == RESULT_SYSTEM_FAILURE;
    }

    @JsonIgnore
    public boolean isException() {
        return resultCode == RESULT_EXCEPTION;
    }

    @JsonIgnore
    public boolean isCorrect() {
        return resultCode == RESULT_CORRECT;
    }

    @JsonIgnore
    public boolean isIncorrect() {
        return resultCode == RESULT_INCORRECT;
    }

    @JsonIgnore
    public boolean isPassed() {
        return isCorrect();
    }

    public void clearResult() {
        setResultCode(0);
        setMessage(null);
        setExecTime(0);
        setResultValue(null);
    }

    public boolean isExclusiveExecution() {
        return exclusiveExecution;
    }

    public void setExclusiveExecution(boolean exclusiveExecution) {
        this.exclusiveExecution = exclusiveExecution;
    }

    public int getTestCaseIndex() {
        return testCaseIndex;
    }

    public void setTestCaseIndex(int testCaseIndex) {
        this.testCaseIndex = testCaseIndex;
    }

    public boolean isPractice() {
        return practice;
    }

    public void setPractice(boolean practice) {
        this.practice = practice;
    }

    public boolean mustValidateArgs() {
        return false;
    }

    public int getSystemTestVersion() {
        return systemTestVersion;
    }

    public void setSystemTestVersion(int systemTestVersion) {
        this.systemTestVersion = systemTestVersion;
    }

    /**
     * Sets check answer response.
     *
     * @param   checkAnswerResponse Check answer response.
     *
     * @since 1.1
     */
    public void setCheckAnswerResponse(String checkAnswerResponse) {
        this.checkAnswerResponse = checkAnswerResponse;
    }

    /**
     * Gets check answer response.
     *
     * @return Check answer response.
     *
     * @since 1.1
     */
    public String getCheckAnswerResponse() {
        return checkAnswerResponse;
    }

    /**
     * Performs de-serialization.
     *
     * @param   reader  Reader.
     *
     * @throws  IOException If any I/O error occurs.
     * @throws  ObjectStreamException   If any stream error occurs.
     **/
    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        this.practice = reader.readBoolean();
        this.submission = (Submission) reader.readObject();
        this.solution = (Solution) reader.readObject();
        this.testCaseId = reader.readInt();
        this.testCaseIndex = reader.readInt();
        this.args = reader.readObjectArray();
        this.resultValue = reader.readObject();
        this.expectedResult = reader.readObject();
        this.resultCode = reader.readInt();
        this.message = reader.readString();
        this.dependencyComponentFiles = reader.readArrayList();
        this.compiledWebServiceClientFiles = reader.readHashMap();
        this.exclusiveExecution = reader.readBoolean();
        this.componentFiles = (ComponentFiles) reader.readObject();
        this.execTime = reader.readLong();
        this.maxMemoryUsed = reader.readLong();
        this.systemTestVersion = reader.readInt();
        this.checkAnswerResponse = reader.readString();
    }
    /**
     * Performs serialization.
     *
     * @param   writer  Writer.
     *
     * @throws  IOException If any I/O error occurs.
     **/
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeBoolean(this.practice);
        writer.writeObject(this.submission);
        writer.writeObject(this.solution);
        writer.writeInt(this.testCaseId);
        writer.writeInt(this.testCaseIndex);
        writer.writeObjectArray(this.args);
        writer.writeObject(this.resultValue);
        writer.writeObject(this.expectedResult);
        writer.writeInt(this.resultCode);
        writer.writeString(this.message);
        writer.writeList(this.dependencyComponentFiles);
        writer.writeMap(this.compiledWebServiceClientFiles);
        writer.writeBoolean(this.exclusiveExecution);
        writer.writeObject(componentFiles);
        writer.writeLong(this.execTime);
        writer.writeLong(this.maxMemoryUsed);
        writer.writeInt(this.systemTestVersion);
        writer.writeString(this.checkAnswerResponse);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        return sb.append(this.getClass().getName()).append("[")
        .append("pratice=").append(isPractice())
        .append(", coderId=").append(submission.getCoderID())
        .append(", location=").append(submission.getLocation())
        .append(", componentId=").append(submission.getComponentID())
        .append(", testCaseId=").append(getTestCaseId())
        .append(", resultCode=").append(getResultCode())
        .append(", resultValue=").append(getResultValue())
        .append(", message=").append(getMessage())
        .append("]")
        .toString();
    }
    
    
}
