package com.topcoder.server.util.sql.executor;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public abstract class Item {

    abstract void set(PreparedStatement preparedStatement, int parameterIndex) throws SQLException;

    private String getClassName() {
        return getClass().getName();
    }

    String getString() {
        throw new UnsupportedOperationException(getClassName());
    }

    Timestamp getTimestamp() {
        throw new UnsupportedOperationException(getClassName());
    }

    int getInt() {
        throw new UnsupportedOperationException(getClassName());
    }

    long getLong() {
        throw new UnsupportedOperationException(getClassName());
    }

    public static Item getIntItem(int k) {
        return new IntItem(k);
    }

    public static Item getLongItem(long k) {
        return new LongItem(k);
    }

    public static Item getStringItem(String s) {
        return new StringItem(s);
    }

    public static Item getTimestampItem(Timestamp timestamp) {
        return new TimestampItem(timestamp);
    }

}
