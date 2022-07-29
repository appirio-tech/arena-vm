package com.topcoder.server.common;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.util.ArrayList;

import com.topcoder.netCommon.contestantMessages.response.BaseResponse;
import com.topcoder.netCommon.contestantMessages.response.WatchableResponse;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

public class ResponseEvent extends TCEvent {

    private ArrayList m_allResponses;

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeArrayList(m_allResponses);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        m_allResponses = reader.readArrayList();
    }


    public ResponseEvent() {
        super(TCEvent.RESPONSE_TYPE, TCEvent.USER_TARGET, -1);
    }

    public ResponseEvent(int targetType, int target, BaseResponse response) {
        super(TCEvent.RESPONSE_TYPE, targetType, target);
        m_allResponses = new ArrayList();
        m_allResponses.add(response);
    }

    public ResponseEvent(int targetType, int target, ArrayList allResponses) {
        super(TCEvent.RESPONSE_TYPE, targetType, target);
        m_allResponses = allResponses;
    }

    public ArrayList getAllResponses() {
        return m_allResponses;
    }

    public ResponseEvent cloneForRoom(int roomIndex, int roomType) {
        ResponseEvent event = new ResponseEvent();
        event.m_allResponses = new ArrayList(m_allResponses.size());
        for (int i = 0; i < m_allResponses.size(); i++) {
            BaseResponse r = (BaseResponse) m_allResponses.get(i);
            try {
                r = (BaseResponse) r.clone();
                if (r instanceof WatchableResponse) {
                    ((WatchableResponse) r).setRoomID(roomIndex);
                    ((WatchableResponse) r).setRoomType(roomType);
                }
                event.m_allResponses.add(r);
            } catch (CloneNotSupportedException cnse) {
            }
        }
        return event;
    }
}
