package com.topcoder.server.util.sql.executor;

import java.sql.ResultSet;
import java.sql.SQLException;

final class ByteColumn extends Column {

    ByteColumn(int column) {
        super(column);
    }

    Item getItem(ResultSet resultSet) throws SQLException {
        byte b = resultSet.getByte(getColumn());
        return new ByteItem(b);
    }

}
