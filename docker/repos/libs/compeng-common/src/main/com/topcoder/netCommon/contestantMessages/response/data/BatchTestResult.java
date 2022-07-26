/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */

package com.topcoder.netCommon.contestantMessages.response.data;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * The result of one testing in batch testing.<br>
 * {@link BatchTestResponse} contains the ordered list of {@link BatchTestResult}.
 *
 * Thread safety: The class is not thread-safe but is used in a thread-safe way.
 *
 * @author dexy
 * @version 1.0
 * @see BatchTestResponse
 */
public class BatchTestResult implements Serializable, CustomSerializable, Cloneable {
//    /** The Constant SUCCESS_FIELD_INDEX. */
//    private static final int SUCCESS_FIELD_INDEX = 0;
//
//    /** The Constant ERRORMESSAGE_FIELD_INDEX. */
//    private static final int ERRORMESSAGE_FIELD_INDEX = 1;
//
//    /** The Constant STATUS_FIELD_INDEX. */
//    private static final int STATUS_FIELD_INDEX = 2;
//
//    /** The Constant MESSAGE_FIELD_INDEX. */
//    private static final int MESSAGE_FIELD_INDEX = 3;
//
//    /** The Constant EXECUTIONTIME_FIELD_INDEX. */
//    private static final int EXECUTIONTIME_FIELD_INDEX = 4;
//
//    /** The Constant PEAKMEMORYUSED_FIELD_INDEX. */
//    private static final int PEAKMEMORYUSED_FIELD_INDEX = 5;
//
//    /** The Constant RETURNVALUE_FIELD_INDEX. */
//    private static final int RETURNVALUE_FIELD_INDEX = 6;
//
//    /** The Constant CORRECTEXAMPLE_FIELD_INDEX. */
//    private static final int CORRECTEXAMPLE_FIELD_INDEX = 7;
//
//    /** The Constant STDOUT_FIELD_INDEX. */
//    private static final int STDOUT_FIELD_INDEX = 8;
//
//    /** The Constant STDERR_FIELD_INDEX. */
//    private static final int STDERR_FIELD_INDEX = 9;
//
//    /** The Constant STACKTRACE_FIELD_INDEX. */
//    private static final int STACKTRACE_FIELD_INDEX = 10;

    /**
     * The success indicator.
     * If the test is completed successfully (even if the test is wrong, has memory overflow, etc)
     * this flag is true, otherwise it is false.
     */
    private boolean success;

    /** The error message. */
    private String errorMessage;

    /**
     * The status.
     * @see STATUS_OK, STATUS_TIMEOUT, STATUS_INVALID_ARGS, STATUS_FAIL, STATUS_TESTER_FAILURE
     * @see com.topcoder.services.tester.common.TestResult
     */
    private int status;

    /** The message. */
    private String message;

    /** The execution time (in ms). */
    private long executionTime;

    /** The peak memory used (in KB). */
    private long peakMemoryUsed;

    /** The return value. */
    private Object returnValue;

    /**
     * The correct example flag.
     * It is empty if it is the custom test, otherwise (we have given test case)
     * it is equal to 'true' if the returnValue of the testing is equal
     * to the expected return value for that test case, otherwise it is 'false'
     * (the returned value for the test is different from the expected return
     * value for that test case).
     */
    private String correctExample;

    /** The standard output content (truncated if it is too long). */
    private String stdOut;

    /** The standard error content (truncated if it is too long). */
    private String stdErr;

    /** The stack trace content (truncated if it is too long). */
    private String stacktrace;

    /**
     * Creates a new instance of <code>BatchTestResult</code>. It is required by custom serialization.
     */
    public BatchTestResult() {
    }

    /**
     * Checks if it is success.
     *
     * @return true, if it is successful batch test (even if with wrong test answer)
     */
    public boolean isSuccess() {
        return success;
    }


    /**
     * Sets the success.
     *
     * @param success the success of the batch test
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }


    /**
     * Gets the error message.
     *
     * @return the error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }


    /**
     * Sets the error message.
     *
     * @param errorMessage the new error message
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }


    /**
     * Gets the execution time (in ms).
     *
     * @return the execution time
     */
    public long getExecutionTime() {
        return executionTime;
    }


    /**
     * Sets the execution time (in ms).
     *
     * @param executionTime the new execution time
     */
    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }


    /**
     * Gets the peak memory used (in KB).
     *
     * @return the peak memory used
     */
    public long getPeakMemoryUsed() {
        return peakMemoryUsed;
    }


    /**
     * Sets the peak memory used (in KB).
     *
     * @param peakMemoryUsed the new peak memory used
     */
    public void setPeakMemoryUsed(long peakMemoryUsed) {
        this.peakMemoryUsed = peakMemoryUsed;
    }


    /**
     * Gets the return value.
     *
     * @return the return value
     */
    @JsonTypeInfo(use = Id.CLASS, include = As.PROPERTY, property = "@class")
    public Object getReturnValue() {
        return returnValue;
    }


    /**
     * Sets the return value.
     *
     * @param returnValue the new return value
     */
    public void setReturnValue(Object returnValue) {
        this.returnValue = returnValue;
    }


    /**
     * Gets the correct example.
     *
     * @return the correct example
     */
    public String getCorrectExample() {
        return correctExample;
    }


    /**
     * Sets the correct example.
     *
     * @param correctExample the new correct example
     */
    public void setCorrectExample(String correctExample) {
        this.correctExample = correctExample;
    }


    /**
     * Gets the standard output content.
     *
     * @return the standard output content
     */
    public String getStdOut() {
        return stdOut;
    }


    /**
     * Sets the standard output content.
     *
     * @param stdOut the standard output content
     */
    public void setStdOut(String stdOut) {
        this.stdOut = stdOut;
    }


    /**
     * Gets the standard error content.
     *
     * @return the standard error content
     */
    public String getStdErr() {
        return stdErr;
    }


    /**
     * Sets the standard error content.
     *
     * @param stdErr the standard error content
     */
    public void setStdErr(String stdErr) {
        this.stdErr = stdErr;
    }


    /**
     * Sets the stack trace content.
     *
     * @return the stack trace content
     */
    public String getStacktrace() {
        return stacktrace;
    }


    /**
     * Sets the stack trace content.
     *
     * @param stacktrace the stack trace content
     */
    public void setStacktrace(String stacktrace) {
        this.stacktrace = stacktrace;
    }

    /**
     * Custom serialization writing of the object.
     *
     * @param reader custom serialization writer.
     */
    public void customWriteObject(CSWriter csWriter) throws IOException {
        csWriter.writeBoolean(isSuccess());
        csWriter.writeString(getErrorMessage());
        csWriter.writeInt(getStatus());
        csWriter.writeString(getMessage());
        csWriter.writeLong(getExecutionTime());
        csWriter.writeLong(getPeakMemoryUsed());
        csWriter.writeObject(getReturnValue());
        csWriter.writeString(getCorrectExample());
        csWriter.writeString(getStdOut());
        csWriter.writeString(getStdErr());
        csWriter.writeString(getStacktrace());
    }

    /**
     * Custom serialization reading of the object.
     *
     * @param reader custom serialization reader.
     */
    public void customReadObject(CSReader csReader) throws IOException, ObjectStreamException {
        setSuccess(csReader.readBoolean());
        setErrorMessage(csReader.readString());
        setStatus(csReader.readInt());
        setMessage(csReader.readString());
        setExecutionTime(csReader.readLong());
        setPeakMemoryUsed(csReader.readLong());
        setReturnValue(csReader.readObject());
        setCorrectExample(csReader.readString());
        setStdOut(csReader.readString());
        setStdErr(csReader.readString());
        setStacktrace(csReader.readString());
    }

    /**
     * Returns the string representation of the object.
     *
     * @return string representation of the object
     */
    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.response.data.BatchTestResult) [");
        ret.append("success = ");
        ret.append(success);
        if (errorMessage != null) {
            ret.append(", errorMessage = ");
            ret.append(errorMessage);
        }
        ret.append(", status = ");
        ret.append(status);
        if (message != null) {
            ret.append(", message = ");
            ret.append(message);
        }
        ret.append(", executionTime = ");
        ret.append(executionTime);
        ret.append(", peakMemoryUsed = ");
        ret.append(peakMemoryUsed);
        if (returnValue != null) {
            ret.append(", returnValue = ");
            ret.append(returnValue.toString());
        }
        if (correctExample != null) {
            ret.append(", correctExample = ");
            ret.append(correctExample);
        }
        if (stdOut != null) {
            ret.append(", stdOut = ");
            ret.append(stdOut);
        }
        if (stdErr != null) {
            ret.append(", stdErr = ");
            ret.append(stdErr);
        }
        if (stacktrace != null) {
            ret.append(", stacktrace = ");
            ret.append(stacktrace);
        }
        ret.append("]");
        return ret.toString();
    }

    /**
     * Gets the status.
     *
     * @return the status
     */
    public int getStatus() {
        return status;
    }

    /**
     * Sets the status.
     *
     * @param status the new status
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * Gets the message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message.
     *
     * @param message the new message
     */
    public void setMessage(String message) {
        this.message = message;
    }

}