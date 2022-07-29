package com.topcoder.server.listener.monitor;

import java.io.*;

import com.topcoder.shared.netCommon.CustomSerializable;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CSReader;

public final class FirstResponse extends ContestMonitorResponse {

    private long time;
    private MonitorStatsItem statsItem;

    FirstResponse() {
    }

    FirstResponse(long time, MonitorStatsItem statsItem) {
        this.time = time;
        this.statsItem = statsItem;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeLong(time);
        statsItem.customWriteObject(writer);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        time = reader.readLong();
        statsItem = new MonitorStatsItem();
        statsItem.customReadObject(reader);
    }

    public long getTime() {
        return time;
    }

    public MonitorStatsItem getStatsItem() {
        return statsItem;
    }

}
