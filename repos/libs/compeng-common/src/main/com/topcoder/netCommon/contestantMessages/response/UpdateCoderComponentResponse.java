package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.netCommon.contestantMessages.response.data.CoderComponentItem;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a response to update the status of a problem component of a user in a room.<br>
 * Use: This response may be sent as part of responses of many different kinds of requests, whenever there is an update
 * to the problem component status of a user. For example, a user in a room opens/compiles/submits/challenges a problem
 * component. The updates will be sent to all clients in the same room and disivion/room summary-subscribed clients.<br>
 * Note: All previous status of the problem component of the user should be replaced by the status in this response.
 * 
 * @author Lars Backstrom
 * @version $Id: UpdateCoderComponentResponse.java 72385 2008-08-19 07:00:36Z qliu $
 */
public class UpdateCoderComponentResponse extends WatchableResponse {
    /** Represents the handle of the user. */
    private String coderHandle;

    /** Represents the status of the problem component. */
    private CoderComponentItem component;

    /**
     * Creates a new instance of <code>UpdateCoderComponentResponse</code>. It is required by custom serialization.
     */
    public UpdateCoderComponentResponse() {
        super(-1, -1);
    }

    /**
     * Creates a new instance of <code>UpdateCoderComponentResponse</code>.
     * 
     * @param coderHandle the handle of the user.
     * @param component the status of the problem component.
     * @param roomType the type of the room.
     * @param roomID the ID of the room.
     */
    public UpdateCoderComponentResponse(String coderHandle, CoderComponentItem component, int roomType, int roomID) {
        super(roomType, roomID);
        this.coderHandle = coderHandle;
        this.component = component;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeString(coderHandle);
        writer.writeObject(component);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        coderHandle = reader.readString();
        component = (CoderComponentItem) reader.readObject();
    }

    /**
     * Gets the handle of the user whose problem component status is updated.
     * 
     * @return the handle of the user.
     */
    public String getCoderHandle() {
        return coderHandle;
    }

    /**
     * Gets the updated status of the problem component.
     * 
     * @return the status of the problem component.
     */
    public CoderComponentItem getComponent() {
        return component;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.response.UpdateCoderComponentResponse) [");
        ret.append("coder = " + coderHandle + ", component = " + component);
        ret.append("]");
        return ret.toString();
    }
}