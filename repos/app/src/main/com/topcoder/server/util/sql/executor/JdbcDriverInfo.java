package com.topcoder.server.util.sql.executor;

import java.sql.Connection;
import java.sql.SQLException;

final class JdbcDriverInfo {

    private final Connection connection;

    private ScaleAdjuster scaleAdjuster;

    JdbcDriverInfo(Connection connection) {
        this.connection = connection;
    }

    int getScale(int scale) throws SQLException {
        if (scaleAdjuster == null) {
            scaleAdjuster = getScaleAdjuster();
        }
        return scaleAdjuster.getScale(scale);
    }

    private static String getDriverName(Connection connection) throws SQLException {
        return connection.getMetaData().getDriverName();
    }

    private ScaleAdjuster getScaleAdjuster() throws SQLException {
        String driverName = getDriverName(connection);
        ScaleAdjuster scaleAdjuster;
        if ("Informix JDBC Driver for Informix Dynamic Server".equals(driverName)) {
            scaleAdjuster = new InformixScaleAdjuster();
        } else if ("Mark Matthews' MySQL Driver".equals(driverName)) {
            scaleAdjuster = new MysqlScaleAdjuster();
        } else {
            throw new UnsupportedOperationException("driverName = '" + driverName + "'");
        }
        return scaleAdjuster;
    }

}
