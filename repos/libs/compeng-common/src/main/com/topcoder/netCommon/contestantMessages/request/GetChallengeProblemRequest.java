package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a request to get another coder's solution of an algorithm problem component.<br>
 * Use: When the current user wants to view another coder's solution of an algorithm problem component, this request is
 * sent.<br>
 * Note: When viewing another coder's solution, the phase must be in challenge/system testing/end of contest phase. If
 * the current phase is challenge phase, the current coder is in the assigned room, and the other coder shares the same
 * assigned room as the current coder, the code may be challenged after viewing. This request <b>does not</b>
 * automatically changes the state of other coding/viewing/challenging window to close. If there is other opening
 * coding/viewing/challenging window, this request will fail. The client has to change the coding/viewing/challenging
 * window to close by sending <code>CloseProblemRequest</code>.
 * 
 * @author Walter Mundt
 * @version $Id: GetChallengeProblemRequest.java 72163 2008-08-07 07:51:04Z qliu $
 * @see CloseProblemRequest
 */
public class GetChallengeProblemRequest extends BaseRequest {
    /** Represents the handle of the other coder whose solution is requested. */
    protected String defendant;

    /** Represents the ID of the problem component. */
    protected int componentID;

    /** Represents a flag indicating if the solution should be reformatted on the server. */
    protected boolean pretty;

    /** Represents the ID of the room where the other coder is assigned. */
    protected int roomID;

    /**
     * Creates a new instance of <code>GetChallengeProblemRequest</code>. it is required by custom serialization.
     */
    public GetChallengeProblemRequest() {
    }

    /**
     * Creates a new instance of <code>GetChallengeProblemRequest</code>.
     * 
     * @param defendant the handle of the other coder whose solution is requested.
     * @param componentID the ID of the problem component.
     * @param pretty <code>true</code> if the solution needs to be reformatted on the server; <code>false</code>
     *            otherwise.
     * @param roomID the ID of the room where the other coder is assigned.
     */
    public GetChallengeProblemRequest(String defendant, int componentID, boolean pretty, int roomID) {
        this.defendant = defendant;
        this.componentID = componentID;
        this.pretty = pretty;
        this.roomID = roomID;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeString(defendant);
        writer.writeInt(componentID);
        writer.writeBoolean(pretty);
        writer.writeInt(roomID);
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        defendant = reader.readString();
        componentID = reader.readInt();
        pretty = reader.readBoolean();
        roomID = reader.readInt();
    }

    /**
     * Gets the handle of the other coder whose solution is requested.
     * 
     * @return the handle of the other coder.
     */
    public String getDefendant() {
        return defendant;
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
     * Gets a flag indicating if the solution should be reformatted on the server.
     * 
     * @return <code>true</code> if the solution needs to be reformatted on the server; <code>false</code>
     *         otherwise.
     */
    public boolean isPretty() {
        return pretty;
    }

    /**
     * Gets the ID of the room where the other coder is assigned.
     * 
     * @return the ID of the room.
     */
    public int getRoomID() {
        return roomID;
    }

    public int getRequestType() {
        return ContestConstants.GET_CHALLENGE_PROBLEM;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.GetChallengeProblemRequest) [");
        ret.append("defendant = ");
        if (defendant == null) {
            ret.append("null");
        } else {
            ret.append(defendant.toString());
        }
        ret.append(", ");
        ret.append("componentID = ");
        ret.append(componentID);
        ret.append(", ");
        ret.append("pretty = ");
        ret.append(pretty);
        ret.append(", ");
        ret.append("roomID = ");
        ret.append(roomID);
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}
