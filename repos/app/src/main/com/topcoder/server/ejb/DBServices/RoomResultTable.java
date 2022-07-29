package com.topcoder.server.ejb.DBServices;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

final class RoomResultTable {

    private static final String ROOM_RESULT_TABLE = "room_result";

    private static final String INSERT_SQL = "INSERT INTO " + ROOM_RESULT_TABLE + " " +
            "(round_id, room_id, coder_id, room_seed, old_rating, new_rating, paid, room_placed, division_placed, " +
            "attended, advanced, overall_rank, point_total, division_seed) " +
            "VALUES (       ?,       ?,        ?,         ?,          ?,          ?,    0,           0,               0, " +
            "     'N',      'N',            0,           0,             ?)";

    static void insert(Connection connection, int roundId, int roomId, int coderId, int roomSeed, int rating, int divisionSeed)
            throws SQLException {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(INSERT_SQL);
            preparedStatement.setInt(1, roundId);
            preparedStatement.setInt(2, roomId);
            preparedStatement.setInt(3, coderId);
            preparedStatement.setInt(4, roomSeed);
            preparedStatement.setInt(5, rating);
            preparedStatement.setInt(6, rating);
            preparedStatement.setInt(7, divisionSeed);
            int rowCount = preparedStatement.executeUpdate();
            if (rowCount != 1) {
                throw new RuntimeException("rowCount==1 after update");
            }
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
    }

}
