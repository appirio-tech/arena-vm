package com.topcoder.server.AdminListener.request;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import java.io.IOException;

public class ObjectUpdateRequest extends ContestMonitorRequest implements ProcessedAtBackEndRequest {

    private String tableName, columnName, whereClause;
    private Object updateObject;
    private boolean requireUniqueRow;
    
    public ObjectUpdateRequest() {
        
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeString(tableName);
        writer.writeString(columnName);
        writer.writeString(whereClause);
        writer.writeObject(updateObject);
        writer.writeBoolean(requireUniqueRow);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        tableName = reader.readString();
        columnName = reader.readString();
        whereClause = reader.readString();
        updateObject = reader.readObject();
        requireUniqueRow = reader.readBoolean();
    }

    public ObjectUpdateRequest(String tableName, String columnName, String whereClause, Object updateObject, boolean requireUniqueRow) {
        this.tableName = tableName;
        this.columnName = columnName;
        this.whereClause = whereClause;
        this.updateObject = updateObject;
        this.requireUniqueRow = requireUniqueRow;
    }

    public String getTableName() {
        return tableName;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getWhereClause() {
        return whereClause;
    }

    public Object getUpdateObject() {
        return updateObject;
    }

    public boolean getRequireUniqueRow() {
        return requireUniqueRow;
    }
}

