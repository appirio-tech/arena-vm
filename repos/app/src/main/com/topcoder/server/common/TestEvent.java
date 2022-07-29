package com.topcoder.server.common;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

public final class TestEvent extends TCEvent implements CustomSerializable {

    private int m_testType;
    private int m_userID;
    private Object m_data;
    private long m_submitTime;

    public TestEvent() {
    }

    public TestEvent(int testType, int userID, Object data, long submitTime) {
        super(TEST_TYPE, USER_TARGET, userID);
        m_testType = testType;
        m_userID = userID;
        m_data = data;
        m_submitTime = submitTime;
    }

    public int getTestType() {
        return m_testType;
    }

    public int getUserID() {
        return m_userID;
    }

    public Object getData() {
        return m_data;
    }

    public long getSubmitTime() {
        return m_submitTime;
    }

    // TODO: Do we really need to impl?

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
    }

}
