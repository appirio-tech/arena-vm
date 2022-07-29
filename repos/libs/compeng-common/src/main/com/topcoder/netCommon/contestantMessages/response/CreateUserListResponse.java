package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.util.ArrayList;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contestantMessages.response.data.UserListItem;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a response to send the list of users in a room. The list can be The information includes proper rating of the
 * user (e.g. for a marathon contest, it contains marathon rating).<br>
 * Use: This response is used to establish the initial list of users in the room. Any previous list of users in the room
 * should be replaced by the data in this response.<br>
 * Note: This response is usually the first response to room-related subscription request. Subsequent update responses
 * are modifications to the list provided by this response. At present, it is used to track all logged in users, users
 * in a room, current team members in a team contest room, and available members in a team contest room.
 * 
 * @author Lars Backstrom
 * @version $Id: CreateUserListResponse.java 72424 2008-08-20 08:06:01Z qliu $
 */
public class CreateUserListResponse extends WatchableResponse {
    /** Represents the type of the user list. */
    private int type;

    /** Represents the list of users. */
    private UserListItem[] items = null;

    /**
     * Creates a new instance of <code>CreateUserListResponse</code>. It is required by custom serialization.
     */
    public CreateUserListResponse() {
        super(-1, -1);
    }

    /**
     * Creates a new instance of <code>CreateUserListResponse</code>. There is no copy.
     * 
     * @param type the type of the user list.
     * @param items the list of users.
     * @param roomType the type of the room.
     * @param roomID the ID of the room.
     * @see #getType()
     */
    public CreateUserListResponse(int type, UserListItem[] items, int roomType, int roomID) {
        super(roomType, roomID);
        this.type = type;
        this.items = items;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(type);
        writer.writeObjectArray(items);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        type = reader.readInt();
        items = (UserListItem[]) reader.readObjectArray(UserListItem.class);
    }

    /**
     * Gets the type of the user list.
     * <ul>
     * <li><code>ContestConstants.ACTIVE_USERS</code> means all logged in users regardless of the room information.</li>
     * <li><code>ContestConstants.ROOM_USERS</code> means users in the room (not assigned users).</li>
     * <li><code>ContestConstants.TEAM_MEMBER_USERS</code> means the current team members in the room.</li>
     * <li><code>ContestConstants.TEAM_AVAILABLE_USERS</code> means the available team members in the room.</li>
     * </ul>
     * 
     * @return the type of the user list.
     * @see ContestConstants#ACTIVE_USERS
     * @see ContestConstants#ROOM_USERS
     * @see ContestConstants#TEAM_MEMBER_USERS
     * @see ContestConstants#TEAM_AVAILABLE_USERS
     */
    public int getType() {
        return type;
    }

    /**
     * Gets the list of users.
     * 
     * @return the list of users.
     */
    public UserListItem[] getUserListItems() {
        return items;
    }

    /**
     * Gets the handles of users in the list. The returned list contains strings.
     * 
     * @return the handles of users in the list.
     * @deprecated Use {@link #getUserListItems()}
     */
    public ArrayList getNames() {
        ArrayList names = new ArrayList(items.length);
        for (int i = 0; i < items.length; i++)
            names.add(items[i].getUserName());
        return names;
    }

    /**
     * Gets the ratings of users in the list. The returned list contains <code>Integer</code> instances.
     * 
     * @return the ratings of users in the list.
     * @deprecated Use {@link #getUserListItems()}
     */
    public ArrayList getRatings() {
        ArrayList ratings = new ArrayList(items.length);
        for (int i = 0; i < items.length; i++)
            ratings.add(new Integer(items[i].getUserRating()));
        return ratings;
    }

    /**
     * Gets the team names of users in the list. The returned list contains strings.
     * 
     * @return the team names of users in the list.
     * @deprecated Use {@link #getUserListItems()}
     */
    public ArrayList getTeams() {
        ArrayList teams = new ArrayList(items.length);
        for (int i = 0; i < items.length; i++)
            teams.add(items[i].getTeamName());
        return teams;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.response.CreateUserListResponse) [");
        ret.append("type = ");
        ret.append(type);
        ret.append(", ");
        ret.append("items = ");
        if (items == null) {
            ret.append("null");
        } else {
            ret.append("{");
            for (int i = 0; i < items.length; i++) {
                ret.append(items[i].toString() + ",");
            }
            ret.append("}");
        }
        ret.append(", ");
        ret.append(super.toString());
        ret.append("]");
        return ret.toString();
    }
}
