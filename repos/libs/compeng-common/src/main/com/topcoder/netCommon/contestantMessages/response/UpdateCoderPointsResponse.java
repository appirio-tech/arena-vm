package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a response to update the total score of a user in a room.<br>
 * Use: This response may be sent as part of responses of many different kinds of requests, whenever there is an update
 * to the total score of a user. For example, a user in a room submits/challenges a problem component or fails the
 * system test. The updates will be sent to all clients in the same room and disivion/room summary-subscribed clients.<br>
 * Note: All previous total score of the user should be replaced by the total score in this response.
 * 
 * @author Lars Backstrom
 * @version $Id: UpdateCoderPointsResponse.java 72385 2008-08-19 07:00:36Z qliu $
 */
public class UpdateCoderPointsResponse extends WatchableResponse {
    /** Represents the handle of the user. */
    private String coderHandle;

    /** Represents the total score of the user. */
    private double points;

    /**
     * Creates a new instance of <code>UpdateCoderPointsResponse</code>. It is required by custom serialization.
     */
    public UpdateCoderPointsResponse() {
        super(-1, -1);
    }

    /**
     * Creates a new instance of <code>UpdateCoderPointsResponse</code>.
     * 
     * @param coderHandle the handle of the user.
     * @param points the total score of the user.
     * @param roomType the type of the room.
     * @param roomID the ID of the room.
     */
    public UpdateCoderPointsResponse(String coderHandle, double points, int roomType, int roomID) {
        super(roomType, roomID);
        this.coderHandle = coderHandle;
        this.points = points;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeString(coderHandle);
        writer.writeDouble(points);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        coderHandle = reader.readString();
        points = reader.readDouble();
    }

    /**
     * Gets the handle of the user.
     * 
     * @return the handle of the user.
     */
    public String getCoderHandle() {
        return coderHandle;
    }

    /**
     * Gets the total score of the user.
     * 
     * @return the total score of the user.
     */
    public double getPoints() {
        return points;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.response.UpdateCoderPointsResponse) [");
        ret.append("coder = " + coderHandle + ", points = " + points);
        ret.append("]");
        return ret.toString();
    }
}