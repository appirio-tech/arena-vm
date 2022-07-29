package com.topcoder.server.util.sql.executor;

import java.sql.PreparedStatement;
import java.sql.SQLException;

final class ByteItem extends Item {

    private final byte b;

    ByteItem(byte b) {
        this.b = b;
    }

    public String toString() {
        return "Byte";
    }

    void set(PreparedStatement preparedStatement, int parameterIndex) throws SQLException {
        preparedStatement.setByte(parameterIndex, b);
    }

    int getInt() {
        return b;
    }

}
