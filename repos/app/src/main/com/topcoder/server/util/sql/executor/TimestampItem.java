package com.topcoder.server.util.sql.executor;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

final class TimestampItem extends Item {

    private final Timestamp timestamp;

    TimestampItem(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    Timestamp getTimestamp() {
        return timestamp;
    }

    void set(PreparedStatement preparedStatement, int parameterIndex) throws SQLException {
        preparedStatement.setTimestamp(parameterIndex, timestamp);
    }

    public String toString() {
        return "Timestamp";
    }

}
