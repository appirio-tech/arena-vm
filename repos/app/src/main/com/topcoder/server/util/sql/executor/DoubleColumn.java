package com.topcoder.server.util.sql.executor;

import java.sql.ResultSet;
import java.sql.SQLException;

final class DoubleColumn extends Column {

    DoubleColumn(int column) {
        super(column);
    }

    Item getItem(ResultSet resultSet) throws SQLException {
        double v = resultSet.getDouble(getColumn());
        return new DoubleItem(v);
    }

}
