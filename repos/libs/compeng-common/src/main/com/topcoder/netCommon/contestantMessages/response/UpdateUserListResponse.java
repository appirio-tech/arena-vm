package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contestantMessages.response.data.UserListItem;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a response to update the list of users in a room. The update is either adding or removing a user.<br>
 * Use: This response is used to update the list of users in a room initially sent by
 * <code>CreateUserListResponse</code>. <br>
 * Note: At present, it is used to track users in a room, current team members in a team contest room, and available
 * members in a team contest room.
 * 
 * @author Lars Backstrom
 * @version $Id: UpdateUserListResponse.java 72385 2008-08-19 07:00:36Z qliu $
 * @see CreateUserListResponse
 */
public class UpdateUserListResponse extends WatchableResponse {
    /** Represents the type of the user list. */
    private int type;

    /** Represents the action of adding or removing a user. */
    private int action;

    /** Represents the user to be added or removed. */
    private UserListItem info;

    /**
     * Creates a new instance of <code>UpdateUserListResponse</code>. It is required by custom serialization.
     */
    public UpdateUserListResponse() {
        super(-1, -1);
    }

    /**
     * Creates a new instance of <code>UpdateUserListResponse</code>.
     * 
     * @param type the type of the user list.
     * @param action the action of adding or removing a user.
     * @param info the user to be added or removed.
     * @param roomType the type of the room.
     * @param roomID the ID of the room.
     * @see #getType()
     * @see #getAction()
     */
    public UpdateUserListResponse(int type, int action, UserListItem info, int roomType, int roomID) {
        super(roomType, roomID);
        this.type = type;
        this.action = action;
        this.info = info;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(type);
        writer.writeInt(action);
        writer.writeObject(info);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        type = reader.readInt();
        action = reader.readInt();
        info = (UserListItem) reader.readObject();
    }

    /**
     * Gets the type of the user list.
     * <ul>
     * <li><code>ContestConstants.ROOM_USERS</code> means users in the room (not assigned users).</li>
     * <li><code>ContestConstants.TEAM_MEMBER_USERS</code> means the current team members in the room.</li>
     * <li><code>ContestConstants.TEAM_AVAILABLE_USERS</code> means the available team members in the room.</li>
     * </ul>
     * 
     * @return the type of the user list.
     * @see ContestConstants#ROOM_USERS
     * @see ContestConstants#TEAM_MEMBER_USERS
     * @see ContestConstants#TEAM_AVAILABLE_USERS
     */
    public int getType() {
        return type;
    }

    /**
     * Gets the action of adding or removing a user.
     * 
     * @return the action.
     * @see ContestConstants#ADD
     * @see ContestConstants#REMOVE
     */
    public int getAction() {
        return action;
    }

    /**
     * Gets the user to be added or removed.
     * 
     * @return the information of the user.
     */
    public UserListItem getUserListItem() {
        return info;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.response.UpdateUserListResponse) [");
        ret.append("type = ");
        ret.append(type);
        ret.append(", ");
        ret.append("action = ");
        ret.append(action);
        ret.append(", ");
        ret.append("info = ");
        if (info == null) {
            ret.append("null");
        } else {
            ret.append(info.toString());
        }
        ret.append(", ");
        ret.append(super.toString());
        ret.append("]");
        return ret.toString();
    }

}