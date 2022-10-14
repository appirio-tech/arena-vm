package com.topcoder.server.util.logging.net;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;

public class TCBatchLoggingEvent implements CustomSerializable, Serializable {
    
    private List items;
    
    public TCBatchLoggingEvent() {
        this.items = new ArrayList();
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(items.size());
        for(int i = 0; i < items.size();i++) {
            TCLoggingEvent e = (TCLoggingEvent)items.get(i);
            e.customWriteObject(writer);
        }
    }

    public void customReadObject(CSReader reader) throws IOException {
        int sz = reader.readInt();
        items = new ArrayList();
        for(int i = 0; i < sz; i++) {
            TCLoggingEvent e = new TCLoggingEvent();
            e.customReadObject(reader);
            items.add(e);
        }
    }    
    
    public void addItem(TCLoggingEvent e) {
        items.add(e);
    }
    
    public List getItems() {
        return items;
    }
}
