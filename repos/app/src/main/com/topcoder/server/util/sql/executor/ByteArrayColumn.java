package com.topcoder.server.util.sql.executor;

import java.sql.ResultSet;
import java.sql.SQLException;

final class ByteArrayColumn extends Column {

    ByteArrayColumn(int column) {
        super(column);
    }

    Item getItem(ResultSet resultSet) throws SQLException {
        byte[] bytes = resultSet.getBytes(getColumn());
        return new ByteArrayItem(bytes);
    }

}
