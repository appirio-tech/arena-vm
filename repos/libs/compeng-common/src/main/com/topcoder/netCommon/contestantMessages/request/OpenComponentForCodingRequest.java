package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a request to get the problem statement of the problem component, and start coding.<br>
 * Use: When the current user wants to start/resume coding for a problem component during an active round or practice
 * room, this request should be sent.<br>
 * Note: When sending this request, the phase must be coding/intermission/challenging/system testing/end of contest
 * phase. The current user must be in the contest room. This request <b>does not</b> automatically changes the state of
 * other coding/viewing/challenging window to close. If there is other opening coding/viewing/challenging window, this
 * request will fail. The client has to change the coding/viewing/challenging window to close by sending
 * <code>CloseProblemRequest</code>. The handle of the current user will be ignored.
 * 
 * @author Walter Mundt
 * @version $Id: OpenComponentForCodingRequest.java 72292 2008-08-12 09:10:29Z qliu $
 * @see CloseProblemRequest
 */
public class OpenComponentForCodingRequest extends BaseRequest {
    /** Represents the ID of the problem component. */
    protected int componentID;

    /** Represents the handle of the current user. It will be ignored. */
    protected String handle;

    /**
     * Creates a new instance of <code>OpenComponentForCodingRequest</code>. It is required by custom serialization.
     */
    public OpenComponentForCodingRequest() {
    }

    /**
     * Creates a new instance of <code>OpenComponentForCodingRequest</code>. The handle is not set, since it is
     * ignored.
     * 
     * @param componentID the ID of the problem component.
     */
    public OpenComponentForCodingRequest(int componentID) {
        this.componentID = componentID;
    }

    /**
     * Creates a new instance of <code>OpenComponentForCodingRequest</code>.
     * 
     * @param handle the handle of the current user.
     * @param componentID the ID of the problem component.
     */
    public OpenComponentForCodingRequest(String handle, int componentID) {
        this(componentID);
        this.handle = handle;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(componentID);
        writer.writeString(handle);
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        componentID = reader.readInt();
        handle = reader.readString();
    }

    public int getRequestType() {
        return ContestConstants.GET_COMPONENT;
    }

    /**
     * Gets the ID of the problem component.
     * 
     * @return the ID of the problem component.
     */
    public int getComponentID() {
        return componentID;
    }

    /**
     * Gets the handle of the current user. It is ignored.
     * 
     * @return the handle of the current user.
     */
    public String getHandle() {
        return handle;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.OpenComponentForCodingRequest) [");
        ret.append("componentID = ");
        ret.append(componentID);
        ret.append(", ");
        ret.append("handle = ");
        ret.append(handle);
        ret.append("]");
        return ret.toString();
    }
}
