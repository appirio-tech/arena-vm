package com.topcoder.server.util.sql.executor;

import java.sql.PreparedStatement;
import java.sql.SQLException;

final class DoubleItem extends Item {

    private final double v;

    DoubleItem(double v) {
        this.v = v;
    }

    void set(PreparedStatement preparedStatement, int parameterIndex) throws SQLException {
        preparedStatement.setDouble(parameterIndex, v);
    }

}
