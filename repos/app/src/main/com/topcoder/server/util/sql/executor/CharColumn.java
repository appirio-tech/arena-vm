package com.topcoder.server.util.sql.executor;

import java.sql.ResultSet;
import java.sql.SQLException;

final class CharColumn extends Column {

    CharColumn(int column) {
        super(column);
    }

    Item getItem(ResultSet resultSet) throws SQLException {
        String s = resultSet.getString(getColumn());
        if (s.length() != 1) {
            throw new RuntimeException("s.length() = " + s.length());
        }
        char ch = s.charAt(0);
        return new CharItem(ch);
    }

}
