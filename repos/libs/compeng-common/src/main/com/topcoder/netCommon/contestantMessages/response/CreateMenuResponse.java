/**
 * CreateMenuResponse.java Description: Specifies a create menu response for both spectator and contest applets
 * 
 * @author Lars Backstrom
 */

package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.util.ArrayList;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a response to send the client the current moderated chat rooms and lobby chat rooms. The names, status, and
 * IDs of the chat rooms are sent as well.<br>
 * Use: After logging in or refreshing the list of chat rooms, this response is sent to the client to update the list.
 * Any previous lists should be replaced by this one.
 * 
 * @author Lars Backstrom
 * @version $Id: CreateMenuResponse.java 72300 2008-08-13 08:33:29Z qliu $
 */
public class CreateMenuResponse extends BaseResponse {
    /** Represents the type of the chat rooms in the response. */
    private int type;

    /** Represents the names of the chat rooms. */
    private ArrayList names;

    /** Represents the status of each chat room. */
    private ArrayList statii;

    /** Represents the IDs of the chat rooms. */
    private ArrayList IDs;

    /**
     * Creates a new instance of <code>CreateMenuResponse</code>. It is required by custom serialization.
     */
    public CreateMenuResponse() {
    }

    /**
     * Creates a new instance of <code>CreateMenuResponse</code>. There is no copy. The list of names is a list of
     * strings. The list of status is a list of strings of 'A' (available) or 'F' (not available). The list of IDs is a
     * list of <code>Integer</code> instances. The type can only be lobby chat rooms or moderated chat rooms.
     * 
     * @param type the type of the list in the response.
     * @param names the names of the chat rooms.
     * @param statii the status of each chat room.
     * @param IDs the IDs of the chat rooms.
     * @see ContestConstants#LOBBY_MENU
     * @see ContestConstants#ACTIVE_CHAT_MENU
     */
    public CreateMenuResponse(int type, ArrayList names, ArrayList statii, ArrayList IDs) {
        this.type = type;
        this.names = names;
        this.statii = statii;
        this.IDs = IDs;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(type);
        writer.writeArrayList(names);
        writer.writeArrayList(statii);
        writer.writeArrayList(IDs);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        type = reader.readInt();
        names = reader.readArrayList();
        statii = reader.readArrayList();
        IDs = reader.readArrayList();
    }

    /**
     * Gets the type of the chat rooms in the response. It will only be lobby or moderated.
     * 
     * @return the type of the chat rooms.
     * @see ContestConstants#LOBBY_MENU
     * @see ContestConstants#ACTIVE_CHAT_MENU
     */
    public int getType() {
        return type;
    }

    /**
     * Gets the names of the chat rooms. There is no copy. It is a list of strings.
     * 
     * @return the names of the chat rooms.
     */
    public ArrayList getNames() {
        return names;
    }

    /**
     * Gets the status of each chat room. There is no copy. It is a list of strings of 'A' (available) or 'F' (not
     * available).
     * 
     * @return the status of each chat room.
     */
    public ArrayList getStatii() {
        return statii;
    }

    /**
     * Gets the IDs of the chat rooms. There is no copy. It is a list of <code>Integer</code> instances.
     * 
     * @return the IDs of the chat rooms.
     */
    public ArrayList getIDs() {
        return IDs;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.response.CreateMenuResponse) [");
        ret.append("type = ");
        ret.append(type);
        ret.append(", ");
        ret.append("names = ");
        if (names == null) {
            ret.append("null");
        } else {
            ret.append(names.toString());
        }
        ret.append(", ");
        ret.append("statii = ");
        if (statii == null) {
            ret.append("null");
        } else {
            ret.append(statii.toString());
        }
        ret.append(", ");
        ret.append("IDs = ");
        if (IDs == null) {
            ret.append("null");
        } else {
            ret.append(IDs.toString());
        }
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }

}
