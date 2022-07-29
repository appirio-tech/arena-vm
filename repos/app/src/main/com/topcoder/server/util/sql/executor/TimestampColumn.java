package com.topcoder.server.util.sql.executor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

final class TimestampColumn extends Column {

    TimestampColumn(int column) {
        super(column);
    }

    Item getItem(ResultSet resultSet) throws SQLException {
        Timestamp timestamp = resultSet.getTimestamp(getColumn());
        return new TimestampItem(timestamp);
    }

}
