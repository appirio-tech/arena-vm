/*
* Copyright (C) 2007 - 2014 TopCoder Inc., All Rights Reserved.
*/
package com.topcoder.server.ejb.TestServices.to;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;
import com.topcoder.shared.netCommon.ExternalizableHelper;

/**
 * System test result DTO.
 *
 * <p>
 * Changes in version 1.1 (PoC Assembly - TopCoder Competition Engine - Support Custom Output Checker):
 * <ol>
 *     <li>Added {@link #message} property and added/updated all corresponding methods.</li>
 * </ol>
 * </p>
 *
 * @author Diego Belfer (mural), gevak
 * @version 1.1
 */
public class SystemTestResult implements Externalizable, CustomSerializable {
    int contestId;
    int coderId;
    int roundId;
    int componentId;
    int testCaseId;
    Object resultObj;
    boolean succeeded;
    double execTime;
    int failure_reason;
    int systemTestVersion;

    /**
     * Message.
     *
     * @since 1.1
     */
    String message;

    public SystemTestResult() {
    }

    /**
     * Creates instance.
     *
     * @param contestId Contest ID.
     * @param roundId Round ID.
     * @param coderId Coder ID.
     * @param componentId Component ID.
     * @param testCaseId Test case ID.
     * @param resultObj Result object.
     * @param succeeded Succeeded flag.
     * @param execTime Execution time.
     * @param failure_reason Failure reason code.
     * @param systemTestVersion System test version.
     * @param message Failure message.
     */
    public SystemTestResult(int contestId, int roundId, int coderId, int componentId, int testCaseId,
        Object resultObj, boolean succeeded, double execTime, int failure_reason, int systemTestVersion,
        String message) {
        this.contestId = contestId;
        this.roundId = roundId;
        this.coderId = coderId;
        this.componentId = componentId;
        this.testCaseId = testCaseId;
        this.resultObj = resultObj;
        this.succeeded = succeeded;
        this.execTime = execTime;
        this.failure_reason = failure_reason;
        this.systemTestVersion = systemTestVersion;
        this.message = message;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("(").
        append(", contestId=").append(contestId).
        append(", coderId=").append(coderId).
        append(", roundId=").append(roundId).
        append(", componentId=").append(componentId).
        append(", testCaseId=").append(testCaseId).
        append(", resultObj=").append(resultObj).
        append(", succeeded=").append(succeeded).
        append(", execTime=").append(execTime).
        append(", failure_reason=").append(failure_reason).
        append(", systemTestVersion=").append(systemTestVersion).append(")");
        return buffer.toString();
    }

    public void setCoderId(int coderId) {
        this.coderId = coderId;
    }

    public int getCoderId() {
        return coderId;
    }

    public void setRoundId(int roundId) {
        this.roundId = roundId;
    }

    public int getRoundId() {
        return roundId;
    }

    public void setComponentId(int componentId) {
        this.componentId = componentId;
    }

    public int getComponentId() {
        return componentId;
    }

    public void setTestCaseId(int testCaseId) {
        this.testCaseId = testCaseId;
    }

    public int getTestCaseId() {
        return testCaseId;
    }

    public void setResultObj(Object resultObj) {
        this.resultObj = resultObj;
    }

    public Object getResultObj() {
        return resultObj;
    }

    public void setSucceeded(boolean succeeded) {
        this.succeeded = succeeded;
    }

    public boolean isSucceeded() {
        return succeeded;
    }

    public void setExecTime(double execTime) {
        this.execTime = execTime;
    }

    public double getExecTime() {
        return execTime;
    }

    public void setFailure_reason(int failure_reason) {
        this.failure_reason = failure_reason;
    }

    public int getFailure_reason() {
        return failure_reason;
    }

    public void setSystemTestVersion(int systemTestVersion) {
        this.systemTestVersion = systemTestVersion;
    }

    public int getSystemTestVersion() {
        return systemTestVersion;
    }

    /**
     * Gets message.
     *
     * @param message Message.
     *
     * @since 1.1
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Sets message.
     *
     * @return Message.
     *
     * @since 1.1
     */
    public String getMessage() {
        return message;
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
        contestId = reader.readInt();
        coderId = reader.readInt();
        roundId = reader.readInt();
        componentId = reader.readInt();
        testCaseId = reader.readInt();
        resultObj = reader.readObject();
        succeeded = reader.readBoolean();
        execTime = reader.readDouble();
        failure_reason = reader.readInt();
        systemTestVersion = reader.readInt();
        message = reader.readString();
    }

    /**
     * Performs serialization.
     *
     * @param writer Writer.
     *
     * @throws IOException If any I/O error occurs.
     **/
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(contestId);
        writer.writeInt(coderId);
        writer.writeInt(roundId);
        writer.writeInt(componentId);
        writer.writeInt(testCaseId);
        writer.writeObject(resultObj);
        writer.writeBoolean(succeeded);
        writer.writeDouble(execTime);
        writer.writeInt(failure_reason);
        writer.writeInt(systemTestVersion);
        writer.writeString(message);
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        ExternalizableHelper.writeExternal(out, this);
    }

    public void readExternal(ObjectInput in) throws IOException {
        ExternalizableHelper.readExternal(in, this);
    }
}