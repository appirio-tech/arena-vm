package com.topcoder.server.common;

//import com.topcoder.netCommon.*;
import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

public class ChatEvent extends TCEvent {

    private String m_message;
    private int m_chatStyle = ContestConstants.IRC_CHAT;
    private int m_userRating;
    private String m_prefix;
    private boolean m_userMessage;
    private int coderID = -1;

    public int getCoderID() {
        return coderID;
    }

    public void setCoderID(int id) {
        coderID = id;
    }

    private int roundID = -1;

    public int getRoundID() {
        return roundID;
    }

    public void setRoundID(int id) {
        roundID = id;
    }

    private int roomID = -1;

    public int getRoomID() {
        return roomID;
    }

    public void setRoomID(int id) {
        roomID = id;
    }

    private int scope;

    public int getScope() {
        return scope;
    }

    private int teamID;

    public int getTeamID() {
        return teamID;
    }

    public void setTeamID(int id) {
        teamID = id;
    }

    public ChatEvent() {
    }

    public ChatEvent(int targetType, int target, int chatStyle, String message, int scope) {
        super(CHAT_TYPE, targetType, target);
        m_message = message;
        m_chatStyle = chatStyle;
        this.scope = scope;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeString(m_message);
        writer.writeInt(m_chatStyle);
        writer.writeInt(m_userRating);
        writer.writeString(m_prefix);
        writer.writeBoolean(m_userMessage);
        writer.writeInt(coderID);
        writer.writeInt(roundID);
        writer.writeInt(roomID);
        writer.writeInt(scope);
        writer.writeInt(teamID);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        m_message = reader.readString();
        m_chatStyle = reader.readInt();
        m_userRating = reader.readInt();
        m_prefix = reader.readString();
        m_userMessage = reader.readBoolean();
        coderID = reader.readInt();
        roundID = reader.readInt();
        roomID = reader.readInt();
        scope = reader.readInt();
        teamID = reader.readInt();
    }


    public String getMessage() {
        return m_message;
    }

    public int getChatStyle() {
        return m_chatStyle;
    }

    public boolean getUserMessage() {
        return m_userMessage;
    }

    public void setUserMessage(boolean value) {
        m_userMessage = value;
    }

    public String getPrefix() {
        return m_prefix;
    }

    public void setPrefix(String prefix) {
        m_prefix = prefix;
    }

    public int getUserRating() {
        return m_userRating;
    }

    public void setUserRating(int rating) {
        m_userRating = rating;
    }
}
