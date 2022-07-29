package com.topcoder.server.common;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

public class ReplayAdminEvent extends TCEvent implements CustomSerializable {

    String m_cmd;

    public String getCmd() {
        return m_cmd;
    }

    public ReplayAdminEvent() {
    }

    public ReplayAdminEvent(int userId, String cmd) {
        super(REPLAY_ADMIN_TYPE, ROOM_TARGET, userId);
        m_cmd = cmd;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeString(m_cmd);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        m_cmd = reader.readString();
    }

}
