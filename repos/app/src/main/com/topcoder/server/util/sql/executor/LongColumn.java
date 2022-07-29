package com.topcoder.server.util.sql.executor;

import java.sql.ResultSet;
import java.sql.SQLException;

final class LongColumn extends Column {

    LongColumn(int column) {
        super(column);
    }

    Item getItem(ResultSet resultSet) throws SQLException {
        long k = resultSet.getLong(getColumn());
        return new LongItem(k);
    }

}
