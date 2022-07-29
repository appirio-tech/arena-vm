package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a request to send a chat text in a room. The chat will be redirected to proper receptors only (e.g.
 * whipsers).<br>
 * Use: When the current user types a chat text in any room, such request is sent.<br>
 * Note: Special functions, such as whisper or reply, is specified in the chat text itself.
 * 
 * @author Walter Mundt
 * @version $Id: ChatRequest.java 72143 2008-08-06 05:54:59Z qliu $
 */
public class ChatRequest extends BaseRequest {
    /** Represents the chat text. */
    String msg;

    /** Represents the ID of the room where the speaker sends the chat. */
    int roomID;

    /**
     * Represents the chat text scope, either within the team or globally.
     * 
     * @see ContestConstants#TEAM_CHAT_SCOPE
     * @see ContestConstants#GLOBAL_CHAT_SCOPE
     */
    int scope;

    /**
     * Creates a new instance of <code>ChatRequest</code>. It is required by custom serialization.
     */
    public ChatRequest() {
    }

    /**
     * Creates a new instance of <code>ChatRequest</code>.
     * 
     * @param msg the chat text.
     * @param roomID the ID of the room where the speaker sends the chat.
     * @param scope the scope of the chat.
     * @see #getScope()
     */
    public ChatRequest(String msg, int roomID, int scope) {
        this.msg = msg;
        this.roomID = roomID;
        this.scope = scope;
    }

    /**
     * Gets the chat text.
     * 
     * @return the chat text.
     */
    public String getMsg() {
        return msg;
    }

    /**
     * Sets the chat text.
     * 
     * @param msg the chat text.
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * Gets the ID of the room where the chat happens.
     * 
     * @return the room ID.
     */
    public int getRoomID() {
        return roomID;
    }

    /**
     * Gets the scope of the chat. The scope is either within the team or globally.
     * 
     * @return the scope of the chat.
     * @see ContestConstants#TEAM_CHAT_SCOPE
     * @see ContestConstants#GLOBAL_CHAT_SCOPE
     */
    public int getScope() {
        return scope;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeString(msg);
        writer.writeInt(roomID);
        writer.writeInt(scope);
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        msg = reader.readString();
        roomID = reader.readInt();
        scope = reader.readInt();
    }

    public int getRequestType() {
        return ContestConstants.CHAT;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.ChatRequest) [");
        ret.append("msg = ");
        if (msg == null) {
            ret.append("null");
        } else {
            ret.append(msg.toString());
        }
        ret.append(", ");
        ret.append("roomID = ");
        ret.append(roomID);
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}
