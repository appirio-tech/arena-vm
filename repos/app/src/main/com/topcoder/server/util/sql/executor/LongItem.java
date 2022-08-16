package com.topcoder.server.util.sql.executor;

import java.sql.PreparedStatement;
import java.sql.SQLException;

final class LongItem extends Item {

    private final long k;

    LongItem(long k) {
        this.k = k;
    }

    void set(PreparedStatement preparedStatement, int parameterIndex) throws SQLException {
        preparedStatement.setLong(parameterIndex, k);
    }

    public String toString() {
        return "Long";
    }

    long getLong() {
        return k;
    }

    String getString() {
        return Long.toString(k);
    }

}
