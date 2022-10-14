package com.topcoder.server.util.sql.executor;

import java.sql.ResultSet;
import java.sql.SQLException;

final class StringColumn extends Column {

    StringColumn(int column) {
        super(column);
    }

    Item getItem(ResultSet resultSet) throws SQLException {
        String s = resultSet.getString(getColumn());
        return new StringItem(s);
    }

}
