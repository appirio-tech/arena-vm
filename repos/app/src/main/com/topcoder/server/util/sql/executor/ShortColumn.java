package com.topcoder.server.util.sql.executor;

import java.sql.ResultSet;
import java.sql.SQLException;

final class ShortColumn extends Column {

    ShortColumn(int column) {
        super(column);
    }

    Item getItem(ResultSet resultSet) throws SQLException {
        short k = resultSet.getShort(getColumn());
        return new ShortItem(k);
    }

}
