package com.topcoder.server.ejb.TestServices;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

final class ComponentStateTable {

    private static final String POINT_TOTAL_SQL = "SELECT SUM(points) FROM component_state WHERE coder_id=? AND round_id=?";

    static int getPointTotal(Connection connection, int coderId, int roundId) throws SQLException {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(POINT_TOTAL_SQL);
            preparedStatement.setInt(1, coderId);
            preparedStatement.setInt(2, roundId);
            ResultSet resultSet = null;
            try {
                resultSet = preparedStatement.executeQuery();
                boolean next = resultSet.next();
                if (!next) {
                    throw new RuntimeException("no rows");
                }
                int pointTotal = (int) Math.round(resultSet.getDouble(1));
                next = resultSet.next();
                if (next) {
                    throw new RuntimeException("2 or more rows");
                }
                return pointTotal;
            } finally {
                if (resultSet != null) {
                    resultSet.close();
                }
            }
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
    }

}
