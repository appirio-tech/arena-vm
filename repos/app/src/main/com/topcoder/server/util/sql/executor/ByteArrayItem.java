package com.topcoder.server.util.sql.executor;

import java.sql.PreparedStatement;
import java.sql.SQLException;

final class ByteArrayItem extends Item {

    private final byte[] bytes;

    ByteArrayItem(byte[] bytes) {
        this.bytes = bytes;
    }

    public String toString() {
        return "ByteArray";
    }

    void set(PreparedStatement preparedStatement, int parameterIndex) throws SQLException {
        preparedStatement.setBytes(parameterIndex, bytes);
    }

}
