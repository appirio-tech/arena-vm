package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a response to notify the client that there is a new chat text.<br>
 * Use: This response is triggered by <code>ChatRequest</code> or by the server automatically. The response will be
 * received by the necessary clients only.<br>
 * Note: Special functions, such as whisper or reply, is specified in the chat text itself.
 * 
 * @author Lars Backstrom
 * @version $Id: UpdateChatResponse.java 72385 2008-08-19 07:00:36Z qliu $
 */
public class UpdateChatResponse extends WatchableResponse {
    /** Represents the type of the chat. */
    private int type;

    /** Represents the chat text. */
    private String data;

    /** Represents the handle of the user sending the text. */
    private String prefix;

    /** Represents the rating of the user sending the text. */
    private int rating;

    /** Represents the scope of the chat. */
    private int scope;

    /**
     * Creates a new instance of <code>UpdateChatResponse</code>. It is required by custom serialization.
     */
    public UpdateChatResponse() {
        super(-1, -1);
    }

    /**
     * Creates a new instance of <code>UpdateChatResponse</code>. The handle and the rating of the user sending this
     * chat is uninitialized. This should only be used when the type of the chat is
     * <code>ContestConstants.USER_CHAT</code>, <code>ContestConstants.MODERATED_CHAT_SPEAKER_CHAT</code>, or
     * <code>ContestConstants.MODERATED_CHAT_QUESTION_CHAT</code>.
     * 
     * @param type the type of the chat
     * @param data the chat text.
     * @param roomType the type of the room.
     * @param roomID the ID of the room.
     * @param scope the scope of the chat.
     * @see #getScope()
     * @see #getType()
     */
    public UpdateChatResponse(int type, String data, int roomType, int roomID, int scope) {
        super(roomType, roomID);
        this.type = type;
        this.data = data;
        this.scope = scope;
    }

    /**
     * Creates a new instance of <code>UpdateChatResponse</code>.
     * 
     * @param type the type of the chat.
     * @param data the chat text.
     * @param prefix the handle of the user sending the chat.
     * @param rating the rating of the user sending the chat.
     * @param roomType the type of the room.
     * @param roomID the ID of the room.
     * @param scope the scope of the chat.
     * @see #getScope()
     * @see #getType()
     */
    public UpdateChatResponse(int type, String data, String prefix, int rating, int roomType, int roomID, int scope) {
        this(type, data, roomType, roomID, scope);
        this.prefix = prefix;
        this.rating = rating;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(type);
        writer.writeString(data);
        writer.writeInt(scope);
        if (type == ContestConstants.USER_CHAT || type == ContestConstants.MODERATED_CHAT_SPEAKER_CHAT
            || type == ContestConstants.MODERATED_CHAT_QUESTION_CHAT) {
            writer.writeInt(rating);
            writer.writeString(prefix);
        }
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        type = reader.readInt();
        data = reader.readString();
        scope = reader.readInt();
        if (type == ContestConstants.USER_CHAT || type == ContestConstants.MODERATED_CHAT_SPEAKER_CHAT
            || type == ContestConstants.MODERATED_CHAT_QUESTION_CHAT) {
            rating = reader.readInt();
            prefix = reader.readString();
        }
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

    /**
     * Gets the rating of the user sending the chat.
     * 
     * @return the rating of the user.
     */
    public int getRating() {
        return rating;
    }

    /**
     * Gets the type of the chat.
     * 
     * @return the type of the chat.
     * @see ContestConstants#USER_CHAT
     * @see ContestConstants#MODERATED_CHAT_SPEAKER_CHAT
     * @see ContestConstants#MODERATED_CHAT_QUESTION_CHAT
     * @see ContestConstants#SYSTEM_CHAT
     * @see ContestConstants#EMPH_SYSTEM_CHAT
     * @see ContestConstants#IRC_CHAT
     * @see ContestConstants#WHISPER_TO_YOU_CHAT
     */
    public int getType() {
        return type;
    }

    /**
     * Gets the chat text.
     * 
     * @return the chat text.
     */
    public String getData() {
        return data;
    }

    /**
     * Gets the handle of the user sending the chat.
     * 
     * @return the handle of the user.
     */
    public String getPrefix() {
        return prefix;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.response.UpdateChatResponse) [");
        ret.append("type = ");
        ret.append(type);
        ret.append(", ");
        ret.append("data = ");
        if (data == null) {
            ret.append("null");
        } else {
            ret.append(data.toString());
        }
        ret.append(", ");
        ret.append("prefix = ");
        if (prefix == null) {
            ret.append("null");
        } else {
            ret.append(prefix.toString());
        }
        ret.append(", ");
        ret.append("rating = ");
        ret.append(rating);
        ret.append(", ");
        ret.append(super.toString());
        ret.append("]");
        return ret.toString();
    }

}
