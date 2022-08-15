package com.topcoder.server.AdminListener.request;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import java.io.IOException;

public class ObjectSearchRequest extends ContestMonitorRequest implements ProcessedAtBackEndRequest {

    private String tableName, columnName, searchText, whereClause;
    
    public ObjectSearchRequest() {
        
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeString(tableName);
        writer.writeString(columnName);
        writer.writeString(searchText);
        writer.writeString(whereClause);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        tableName = reader.readString();
        columnName = reader.readString();
        searchText = reader.readString();
        whereClause = reader.readString();
    }

    public ObjectSearchRequest(String tableName, String columnName, String searchText, String whereClause) {
        this.tableName = tableName;
        this.columnName = columnName;
        this.searchText = searchText;
        this.whereClause = whereClause;
    }

    public String getTableName() {
        return tableName;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getSearchText() {
        return searchText;
    }

    public String getWhereClause() {
        return whereClause;
    }
}

