package com.topcoder.server.util.sql.executor;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

final class ColumnNameMap {

    private final Map map = new HashMap();

    ColumnNameMap(ResultSetMetaData metaData, int columnCount) throws SQLException {
        for (int column = 1; column <= columnCount; column++) {
            String columnName = metaData.getColumnName(column);
            map.put(columnName, new Integer(column));
        }
    }

    int getColumnIndex(String columnName) {
        Object value = map.get(columnName);
        return ((Integer) value).intValue();
    }

}
