package com.topcoder.server.AdminListener.response;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class BlobColumnResponse implements CustomSerializable, Serializable {

    private boolean succeeded;
    private Map tableColumns;
    
    public BlobColumnResponse() {
        
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeBoolean(succeeded);
        writer.writeHashMap(new HashMap(tableColumns));
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        succeeded = reader.readBoolean();
        tableColumns = reader.readHashMap();
    }

    public BlobColumnResponse(boolean succeeded, TreeMap tableColumns) {
        this.succeeded = succeeded;
        this.tableColumns = tableColumns;
    }

    public boolean getSucceeded() {
        return succeeded;
    }

    public Map getTableColumns() {
        return tableColumns;
    }
}
