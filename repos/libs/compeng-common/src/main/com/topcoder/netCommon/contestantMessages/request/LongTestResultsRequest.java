/*
 * LongTestResultsRequest Created 06/10/2007
 */
package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contestantMessages.response.LongTestResultsResponse;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a request to get the test results for the last submission of a marathon problem component.<br>
 * Use: When the user wants to view his or other's test results for the last submission, this request should be sent.<br>
 * Note: All example test results can be viewed since the ending of the coding phase, except for the current user's own
 * example test results, which can be viewed during the coding phase. For all system test results, they are available
 * after the end of the round. The current user does not have to be in the contest room to send this request.
 * 
 * @autor Diego Belfer (Mural)
 * @version $Id: LongTestResultsRequest.java 72313 2008-08-14 07:16:48Z qliu $
 */
public class LongTestResultsRequest extends BaseRequest {
    /** Represents the handle of the user whose test results are requested. */
    private String handle;

    /** Represents the ID of the room where the problem component is assigned. */
    private int roomID;

    /** Represents the ID of the problem component. */
    private int componentID;

    /** Represents the type of the test results requested. */
    private int resultType;

    /**
     * Creates a new instance of <code>LongTestResultsRequest</code>. It is required by custom serialization.
     */
    public LongTestResultsRequest() {
    }

    /**
     * Creates a new instance of <code>LongTestResultsRequest</code>.
     * 
     * @param componentID the ID of the problem component.
     * @param roomID the ID of the room where the problem component is assigned.
     * @param handle the handle of the user whose test results are requested.
     * @param resultType the type of the marathon test results.
     * @see #getResultType()
     */
    public LongTestResultsRequest(int componentID, int roomID, String handle, int resultType) {
        this.handle = handle;
        this.roomID = roomID;
        this.componentID = componentID;
        this.resultType = resultType;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeString(handle);
        writer.writeInt(roomID);
        writer.writeInt(componentID);
        writer.writeInt(resultType);
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        handle = reader.readString();
        roomID = reader.readInt();
        componentID = reader.readInt();
        resultType = reader.readInt();
    }

    /**
     * Gets the ID of the problem component.
     * 
     * @return the problem component ID.
     */
    public int getComponentID() {
        return componentID;
    }

    public int getRequestType() {
        return ContestConstants.LONG_TEST_RESULTS_REQUEST;
    }

    /**
     * Gets the handle of the user whose test results are requested.
     * 
     * @return the handle of the user.
     */
    public String getHandle() {
        return handle;
    }

    /**
     * Gets the ID of the room where the problem component is assigned.
     * 
     * @return the room ID.
     */
    public int getRoomID() {
        return roomID;
    }

    /**
     * Gets the type of the test results requested.
     * 
     * @return the type of the test results.
     * @see LongTestResultsResponse#RESULT_EXAMPLE
     * @see LongTestResultsResponse#RESULT_NONFINAL
     * @see LongTestResultsResponse#RESULT_FINAL
     */
    public int getResultType() {
        return resultType;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.LongTestResultsRequest) [");
        ret.append("handle = ");
        if (handle == null) {
            ret.append("null");
        } else {
            ret.append(handle.toString());
        }
        ret.append(", ");
        ret.append("roomID = ");
        ret.append(roomID);
        ret.append(", componentID=");
        ret.append(componentID);
        ret.append(", resultType=");
        ret.append(resultType);
        ret.append("]");
        return ret.toString();
    }
}
