package com.topcoder.server.util.sql.executor;

import java.sql.PreparedStatement;
import java.sql.SQLException;

final class CharItem extends Item {

    private final char ch;

    CharItem(char ch) {
        this.ch = ch;
    }

    void set(PreparedStatement preparedStatement, int parameterIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

}
