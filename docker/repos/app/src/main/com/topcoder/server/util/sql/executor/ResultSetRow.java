package com.topcoder.server.util.sql.executor;

import java.sql.Timestamp;

public final class ResultSetRow {

    private final Item[] items;
    private final ColumnNameMap columnNameMap;

    ResultSetRow(Item[] items, ColumnNameMap columnNameMap) {
        this.items = items;
        this.columnNameMap = columnNameMap;
    }

    public String toString() {
        String s = "[";
        for (int i = 0; i < items.length; i++) {
            Item item = items[i];
            s += (i + 1) + " " + item;
            if (i < items.length - 1) {
                s += ", ";
            }
        }
        s += "]";
        return s;
    }

    public Item getItem(int columnIndex) {
        return items[columnIndex - 1];
    }

    public Item getItem(String columnName) {
        int columnIndex = columnNameMap.getColumnIndex(columnName);
        return getItem(columnIndex);
    }

    public long getLong(int columnIndex) {
        return getItem(columnIndex).getLong();
    }

    public Timestamp getTimestamp(int columnIndex) {
        return getItem(columnIndex).getTimestamp();
    }

    public String getString(int columnIndex) {
        return getItem(columnIndex).getString();
    }

    public String getString(String columnName) {
        return getItem(columnName).getString();
    }

    public int getInt(int columnIndex) {
        return getItem(columnIndex).getInt();
    }

    public int getInt(String columnName) {
        return getItem(columnName).getInt();
    }

}
