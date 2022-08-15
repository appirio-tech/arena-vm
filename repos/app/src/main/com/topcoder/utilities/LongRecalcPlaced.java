/*
 * RecalcPlaced.java
 *
 * Created on February 17, 2006, 10:43 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.topcoder.utilities;

import com.topcoder.shared.util.DBMS;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author rfairfax
 */
public class LongRecalcPlaced {
    
    /** Creates a new instance of RecalcPlaced */
    public LongRecalcPlaced() {
    }
    
    public static void main(String[] args) {
        int roundId = Integer.parseInt(args[0]);
        Connection cnn = null;
        try {
            cnn = DBMS.getDirectConnection();
            go(cnn, roundId);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBMS.close(cnn);
        }
    }
    
    public static void go(Connection directConnection, int roundId) throws SQLException {
        calculatePlaces(directConnection, roundId, "point_total", "placed");
    }
    
    /**
     * Calculates the place obtained by each attendee to the round.
     * 
     * @param cnn Connection used to access database
     * @param roundId Id of the round
     * 
     * @throws SQLException If an SQLException is thrown during the process
     */
    public static void recalcPlacesWithSystemTestPoints(Connection cnn, int roundId) throws SQLException {
        calculatePlaces(cnn, roundId, "system_point_total", "placed");
    }

    private static void calculatePlaces(Connection c, int roundId, String pointField, String placeField) throws SQLException {
        PreparedStatement ps = null;
        PreparedStatement psUpdate = null;
        ResultSet rs = null;
        try {
            StringBuffer sqlStr = new StringBuffer();

            // Set division_placed - where the coder placed in their division
            // get all the users in order for this division by points.
            sqlStr = new StringBuffer();
            sqlStr.append("SELECT rr.coder_id, rr.").append(pointField);
            sqlStr.append("  FROM long_comp_result rr ");
            sqlStr.append("  WHERE rr.round_id = ? ");
            sqlStr.append("        AND rr.attended = 'Y' ");
            sqlStr.append("  ORDER BY rr.").append(pointField).append(" DESC");
            ps = c.prepareStatement(sqlStr.toString());
            ps.setInt(1, roundId);
            rs = ps.executeQuery();
            
            int divisionPlace = 0;

            sqlStr = new StringBuffer();
            sqlStr.append("UPDATE long_comp_result SET ").append(placeField).append(" = ?");
            sqlStr.append(" WHERE coder_id = ? AND round_id = ?");
            psUpdate = c.prepareStatement(sqlStr.toString());

            double lastPoints = 0;
            int numInPlace = 0;
            boolean first = true;
            while(rs.next()) {
                int coderId = rs.getInt(1);
                double points = rs.getDouble(2);

                // Are we starting a new division?
                if (first) {
                    first = false;
                    divisionPlace = 1;
                    numInPlace = 0;
                    lastPoints = points;
                }

                if (points == lastPoints) {
                    numInPlace++;
                } else {
                    divisionPlace += numInPlace;
                    numInPlace = 1;
                }
                lastPoints = points;

                // set their division placed index
                psUpdate.setInt(1, divisionPlace);
                psUpdate.setInt(2, coderId);
                psUpdate.setInt(3, roundId);
                psUpdate.executeUpdate();

            }
            psUpdate.close();
            psUpdate = null;
            ps.close();
            ps = null;
        } finally {
            DBMS.close(null, psUpdate, rs);
            DBMS.close(ps);
        }
    }
}
