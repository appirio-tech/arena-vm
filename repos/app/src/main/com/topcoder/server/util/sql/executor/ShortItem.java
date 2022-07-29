package com.topcoder.server.util.sql.executor;

import java.sql.PreparedStatement;
import java.sql.SQLException;

final class ShortItem extends Item {

    private final short k;

    ShortItem(short k) {
        this.k = k;
    }

    int getInt() {
        return k;
    }

    public String toString() {
        return "Short";
    }

    void set(PreparedStatement preparedStatement, int parameterIndex) throws SQLException {
        preparedStatement.setShort(parameterIndex, k);
    }

}
