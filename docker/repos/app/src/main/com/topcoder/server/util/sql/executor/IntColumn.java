package com.topcoder.server.util.sql.executor;

import java.sql.ResultSet;
import java.sql.SQLException;

final class IntColumn extends Column {

    IntColumn(int column) {
        super(column);
    }

    Item getItem(ResultSet resultSet) throws SQLException {
        int k = resultSet.getInt(getColumn());
        return new IntItem(k);
    }

}
