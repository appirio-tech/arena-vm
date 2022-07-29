package com.topcoder.server.util.sql.executor;

import java.sql.PreparedStatement;
import java.sql.SQLException;

final class StringItem extends Item {

    private final String s;

    StringItem(String s) {
        this.s = s;
    }

    String getString() {
        return s;
    }

    void set(PreparedStatement preparedStatement, int parameterIndex) throws SQLException {
        preparedStatement.setString(parameterIndex, s);
    }

}
