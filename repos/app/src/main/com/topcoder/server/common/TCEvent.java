package com.topcoder.server.common;

//import com.topcoder.netCommon.*;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

public class TCEvent implements Serializable, CustomSerializable {

    static final long serialVersionUID = -762017638618427924L;

    public static final String TYPE = "EVENT_TYPE";
    public static final int LIST_TYPE = -1;
    public static final int UNKNOWN_TYPE = 0;
    public static final int CHAT_TYPE = 1;
    public static final int RESPONSE_TYPE = 2;
    public static final int COMPILE_TYPE = 3;
    public static final int ACTION_TYPE = 4;
    public static final int CONTEST_TYPE = 5;
    public static final int TEST_TYPE = 6;
    public static final int LEADER_TYPE = 7;
    public static final int PHASE_TYPE = 8;
    public static final int SUBMIT_TYPE = 9;
    public static final int MOVE_TYPE = 10;
    public static final int REPLAY_SUBMIT_TYPE = 11;
    public static final int REPLAY_CHALLENGE_TYPE = 12;
    public static final int REPLAY_COMPILE_TYPE = 13;
    public static final int REPLAY_ADMIN_TYPE = 14;
    public static final int LOBBY_FULL_TYPE = 15;
    
    public static final int ROOM_TARGET = 1;
    public static final int ALL_TARGET = 2;
    public static final int USER_TARGET = 3;
    public static final int ADMIN_TARGET = 4;
    public static final int ROUND_TARGET = 5;
    public static final int TEAM_TARGET = 6;

    private int m_targetType;
    private int m_target;
    private int m_type;
    private boolean m_replayEvent; // true if this is a replay event

    private long m_createTime = System.currentTimeMillis();

    public long getCreateTime() {
        return m_createTime;
    }

    public TCEvent() {
    }

    public TCEvent(int type, int targetType, int target) {
        m_type = type;
        m_targetType = targetType;
        m_target = target;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(m_targetType);
        writer.writeInt(m_target);
        writer.writeInt(m_type);
        writer.writeBoolean(m_replayEvent);
        writer.writeLong(m_createTime);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        m_targetType = reader.readInt();
        m_target = reader.readInt();
        m_type = reader.readInt();
        m_replayEvent = reader.readBoolean();
        m_createTime = reader.readLong();
    }


    public int getEventType() {
        return m_type;
    }

    public int getTargetType() {
        return m_targetType;
    }

    public int getTarget() {
        return m_target;
    }

    public boolean isReplayEvent() {
        return m_replayEvent;
    }

    public void setReplayEvent(boolean flag) {
        m_replayEvent = flag;
    }

}
