package com.topcoder.server.util.sql.executor;

import java.sql.PreparedStatement;
import java.sql.SQLException;

final class IntItem extends Item {

    private final int k;

    IntItem(int k) {
        this.k = k;
    }

    void set(PreparedStatement preparedStatement, int parameterIndex) throws SQLException {
        preparedStatement.setInt(parameterIndex, k);
    }

    int getInt() {
        return k;
    }

}
