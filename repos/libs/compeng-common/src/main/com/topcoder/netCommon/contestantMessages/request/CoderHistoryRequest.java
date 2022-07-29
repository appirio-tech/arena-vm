package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a request to get the submission/challenge/system testing history of a coder in a room.<br>
 * Use: When the current user wants to see the history of a coder, such request is sent.<br>
 * Note: The current user does not have to be in the contest room or registered to the round.
 * 
 * @author Walter Mundt
 * @version $Id: CoderHistoryRequest.java 72163 2008-08-07 07:51:04Z qliu $
 */
public class CoderHistoryRequest extends BaseRequest {
    /** Represents all kinds of history information should be retrieved. */
    public static final int TYPE_ALL = 0xFFFFFFFF;

    /** Represents only example submission history should be retrieved. Only meanful in marathon rounds. */
    public static final int TYPE_SUBMISSIONS_EXAMPLE = 1;

    /** Represents only full submission history should be retrieved. Only meanful in marathon rounds. */
    public static final int TYPE_SUBMISSIONS_NON_EXAMPLE = 2;

    /** Represents the handle of the user whose history should be retrieved. */
    protected String handle;

    /** Represents the ID of the room where the user is assigned to. */
    protected int roomID;

    /** Represents the type of the user whose history should be retrieved. */
    protected int userType;

    /** Represents the history information requested to be retrieved. */
    private int historyType;

    /**
     * Creates a new instance of <code>CoderHistoryRequest</code>. It is required by custom serialization.
     */
    public CoderHistoryRequest() {
    }

    /**
     * Creates a new instance of <code>CoderHistoryRequest</code>. All history information will be requested.
     * 
     * @param handle the handle of the user whose history should be retrieved.
     * @param roomID the ID of the room where the user is assigned to.
     * @param userType the type of the user whose history should be retrieved.
     */
    public CoderHistoryRequest(String handle, int roomID, int userType) {
        this(handle, roomID, userType, TYPE_ALL);
    }

    /**
     * Creates a new instance of <code>CoderHistoryRequest</code>.
     * 
     * @param handle the handle of the user whose history should be retrieved.
     * @param roomID the ID of the room where the user is assigned to.
     * @param userType the type of the user whose history should be retrieved.
     * @param historyType the history information requested to be retrieved.
     * @see #getHistoryType()
     */
    public CoderHistoryRequest(String handle, int roomID, int userType, int historyType) {
        this.handle = handle;
        this.roomID = roomID;
        this.userType = userType;
        this.historyType = historyType;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeString(handle);
        writer.writeInt(roomID);
        writer.writeInt(userType);
        writer.writeInt(historyType);
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        handle = reader.readString();
        roomID = reader.readInt();
        userType = reader.readInt();
        historyType = reader.readInt();
    }

    /**
     * Gets the type of the user whose history should be retrieved.
     * 
     * @return the type of the user.
     */
    public int getUserType() {
        return userType;
    }

    public int getRequestType() {
        return ContestConstants.CODER_HISTORY;
    }

    /**
     * Gets the handle of the user whose history should be retrieved.
     * 
     * @return the handle of the user.
     */
    public String getHandle() {
        return handle;
    }

    /**
     * Gets the ID of the room where the user is assigned to.
     * 
     * @return the ID of the room.
     */
    public int getRoomID() {
        return roomID;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.CoderHistoryRequest) [");
        ret.append("handle = ");
        if (handle == null) {
            ret.append("null");
        } else {
            ret.append(handle.toString());
        }
        ret.append(", ");
        ret.append("roomID = ");
        ret.append(roomID);
        ret.append(", userType=");
        ret.append(userType);
        ret.append("]");
        return ret.toString();
    }

    /**
     * Gets the history information which is requested.
     * 
     * @return the type of history information.
     * @see #TYPE_ALL
     * @see #TYPE_SUBMISSIONS_EXAMPLE
     * @see #TYPE_SUBMISSIONS_NON_EXAMPLE
     */
    public int getHistoryType() {
        return historyType;
    }
}
