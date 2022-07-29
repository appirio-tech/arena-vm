/*
 * LongTestResultsResponse Created 30/07/2007
 */
package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;

import com.topcoder.netCommon.contestantMessages.response.data.LongTestResultData;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a response to send the test results for a solution of a marathon problem component in a round.<br>
 * Use: This response is specific to <code>LongTestResultsRequest</code>. The client should show the test results
 * to the current user.
 * 
 * @author Diego Belfer (mural)
 * @version $Id: LongTestResultsResponse.java 72313 2008-08-14 07:16:48Z qliu $
 */
public class LongTestResultsResponse extends BaseResponse {
    /** Represents the type of example test results. */
    public static final int RESULT_EXAMPLE = 0;

    /** Rperesents the type of non-final system test results. */
    public static final int RESULT_NONFINAL = 1;

    /** Represents the type of final system test results. */
    public static final int RESULT_FINAL = 2;

    /** Represents the ID of the round. */
    private int roundId;

    /** Represents the ID of the marathon problem component in the round. */
    private int componentId;

    /** Represents the handle of the user whose solution's test results are in the response. */
    private String handle;

    /** Represents the type of the test results. */
    private int resultType;

    /** Represents the argument values of test cases. */
    private String[] args;

    /** Represents the results of test cases. */
    private LongTestResultData[] resultData;

    /**
     * Creates a new instance of <code>LongTestResultsResponse</code>. It is required by custom serialization.
     */
    public LongTestResultsResponse() {
    }

    /**
     * Creates a new instance of <code>LongTestResultsResponse</code>. All arrays are not copied.
     * 
     * @param roundId the ID of the round.
     * @param componentId the ID of the marathon problem component in the round.
     * @param handle the handle of the user whose solution's test results are in the response.
     * @param resultType the type of the test results.
     * @param args the argument values of test cases.
     * @param resultData the results of test cases.
     */
    public LongTestResultsResponse(int roundId, int componentId, String handle, int resultType, String[] args,
        LongTestResultData[] resultData) {
        this.roundId = roundId;
        this.componentId = componentId;
        this.handle = handle;
        this.resultType = resultType;
        this.args = args;
        this.resultData = resultData;
    }

    /**
     * Gets the results of all test cases. There is no copy.
     * 
     * @return the results of all test cases.
     */
    public LongTestResultData[] getResultData() {
        return resultData;
    }

    /**
     * Gets the results of all test cases. There is no copy.
     * 
     * @param resultData the results of all test cases.
     */
    public void setResultData(LongTestResultData[] resultData) {
        this.resultData = resultData;
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        this.roundId = reader.readInt();
        this.componentId = reader.readInt();
        this.handle = reader.readString();
        this.resultType = reader.readInt();
        this.args = (String[]) reader.readObjectArray(String.class);
        this.resultData = (LongTestResultData[]) reader.readObjectArray(LongTestResultData.class);
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(roundId);
        writer.writeInt(componentId);
        writer.writeString(handle);
        writer.writeInt(resultType);
        writer.writeObjectArray(args);
        writer.writeObjectArray(resultData);
    }

    /**
     * Gets the argument values of all test cases. There is no copy.
     * 
     * @return the argument values of all test cases.
     */
    public String[] getArgs() {
        return args;
    }

    /**
     * Sets the argument values of all test cases. There is no copy.
     * 
     * @param args the argument values of all test cases.
     */
    public void setArgs(String[] args) {
        this.args = args;
    }

    /**
     * Gets the handle of the user whose solution's test results are in the response.
     * 
     * @return the handle of the user.
     */
    public String getHandle() {
        return handle;
    }

    /**
     * Sets the handle of the user whose solution's test results are in the response.
     * 
     * @param handle the handle of the user.
     */
    public void setCoderId(String handle) {
        this.handle = handle;
    }

    /**
     * Gets the ID of the problem component in the round.
     * 
     * @return the problem component ID.
     */
    public int getComponentId() {
        return componentId;
    }

    /**
     * Sets the ID of the problem component in the round.
     * 
     * @param componentId the problem component ID.
     */
    public void setComponentId(int componentId) {
        this.componentId = componentId;
    }

    /**
     * Gets the type of the test results.
     * 
     * @return the type of the test results.
     * @see #RESULT_EXAMPLE
     * @see #RESULT_NONFINAL
     * @see #RESULT_FINAL
     */
    public int getResultType() {
        return resultType;
    }

    /**
     * Sets the type of the test results.
     * 
     * @param resultType the type of the test results.
     */
    public void setResultType(int resultType) {
        this.resultType = resultType;
    }

    /**
     * Gets the ID of the round.
     * 
     * @return the round ID.
     */
    public int getRoundId() {
        return roundId;
    }

    /**
     * Sets the ID of the round.
     * 
     * @param roundId the round ID.
     */
    public void setRoundId(int roundId) {
        this.roundId = roundId;
    }

    public String toString() {
        return "LongTestResultsResponse[roundId=" + roundId + ", handle=" + handle + ", data=" + resultData + "]";
    }

    /**
     * Gets a description of the type of the test results.
     * 
     * @return a description of the result type.
     */
    public String getResultTypeString() {
        switch (resultType) {
        case RESULT_EXAMPLE:
            return "Example results";
        case RESULT_NONFINAL:
            return "Provisional results";
        case RESULT_FINAL:
            return "Final results";
        }
        return "Invalid result type";
    }
}
