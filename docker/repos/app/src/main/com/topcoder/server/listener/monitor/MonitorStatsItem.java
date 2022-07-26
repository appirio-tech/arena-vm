package com.topcoder.server.listener.monitor;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.topcoder.shared.netCommon.CustomSerializable;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CSReader;

public final class MonitorStatsItem extends ContestMonitorResponse implements Serializable, CustomSerializable {

    private ArrayList actionList;
    private HashMap bytesMap;

    MonitorStatsItem() {
    }

    MonitorStatsItem(ArrayList addList, HashMap bytesMap) {
        this.actionList = addList;
        this.bytesMap = bytesMap;
    }

    public Collection getActionList() {
        return actionList;
    }

    public Map getBytesMap() {
        return bytesMap;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeArrayList(actionList);
        writer.writeHashMap(bytesMap);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        actionList = reader.readArrayList();
        bytesMap = reader.readHashMap();
    }

    public String toString() {
        return "action=" + actionList + ", bytesMap=" + bytesMap;
    }

}
