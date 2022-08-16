package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.util.Arrays;

import com.topcoder.netCommon.contestantMessages.response.data.UserListItem;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a response to send the the list of all registered users for a round.<br>
 * Use: This response is specific to <code>RegisterUsersRequest</code>. When the client received this response, the
 * list of all registered users should be shown to the current user.<br>
 * Note: The round information should be available to the current user, since the client can only request registered
 * user list for an active round. The rating of the user will be proper one according to the type of the round.
 * 
 * @author Lars Backstrom
 * @version $Id: RegisteredUsersResponse.java 72343 2008-08-15 06:09:22Z qliu $
 */
public class RegisteredUsersResponse extends BaseResponse {
    /** Represents the ID of the round. */
    private long roundID;

    /** Represents the registered users for the round. */
    private UserListItem[] items = null;

    /**
     * Creates a new instance of <code>RegisteredUsersResponse</code>. It is required by custom serialization.
     */
    public RegisteredUsersResponse() {
    }

    /**
     * Creates a new instance of <code>RegisteredUsersResponse</code>. There is no copy.
     * 
     * @param roundID the ID of the round.
     * @param items the registered users for the round.
     */
    public RegisteredUsersResponse(long roundID, UserListItem[] items) {
        this.roundID = roundID;
        this.items = items;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeLong(roundID);
        writer.writeObjectArray(items);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        roundID = reader.readLong();
        this.items = (UserListItem[]) reader.readObjectArray(UserListItem.class);
    }

    /**
     * Gets the ID of the round.
     * 
     * @return the round ID.
     */
    public long getRoundID() {
        return roundID;
    }

    /**
     * Gets the registered users for the round. There is no copy.
     * 
     * @return the registered users.
     */
    public UserListItem[] getUserListItems() {
        return items;
    }

    /**
     * Gets the string representation of this object
     * 
     * @return the string representation of this object
     */
    public String toString() {
        return "(RegisteredUsersResponse)[roundID = " + roundID + " list = " + Arrays.asList(items) + "]";
    }
}