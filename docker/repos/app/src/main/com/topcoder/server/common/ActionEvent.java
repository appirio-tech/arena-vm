package com.topcoder.server.common;

//import com.topcoder.netCommon.*;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

public class ActionEvent extends TCEvent {

    public static final int KICK_USER = 0;
    public static final int ASSIGNED_ROOMS = 1;
    public static final int END_CONTEST = 2;
    public static final int ENABLE_CONTEST = 3;
    public static final int DISABLE_CONTEST = 4;
    public static final int LOGGED_IN_ELSEWHERE = 5;
    public static final int QUAL_UPDATE_ROOMS = 6;
    public static final int FORWARDED_ROUND_UPDATE = 7;

    private int m_action;
    private int m_roundID;

    public ActionEvent() {
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(m_action);
        writer.writeInt(m_roundID);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        m_action = reader.readInt();
        m_roundID = reader.readInt();
    }

    public ActionEvent(int targetType, int target, int action) {
        super(ACTION_TYPE, targetType, target);
        m_action = action;
    }

    public ActionEvent(int userID, int action) {
        this(USER_TARGET, userID, action);
    }

    public int getAction() {
        return m_action;
    }

    public void setRoundID(int roundID) {
        m_roundID = roundID;
    }

    public int getRoundID() {
        return m_roundID;
    }
}
