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
public class RecalcPlaced {
    
    /** Creates a new instance of RecalcPlaced */
    public RecalcPlaced() {
    }
    
    public static void main(String[] args) {
        int roundId = Integer.parseInt(args[0]);
        try {
            go(DBMS.getDirectConnection(), roundId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static void go(Connection c, int roundId) {
        PreparedStatement ps = null;
        PreparedStatement psUpdate = null;
        ResultSet rs = null;
        try {
            // update ROOM_RESULT.room_placed
            StringBuffer sqlStr = new StringBuffer();
            sqlStr.append("SELECT rr.coder_id, r.room_id, rr.point_total ");
            sqlStr.append("FROM room_result rr, room r ");
            sqlStr.append("WHERE r.room_id = rr.room_id ");
            sqlStr.append("AND rr.round_id = ? ");
            sqlStr.append("AND rr.attended = 'Y' ");
            sqlStr.append("ORDER BY r.room_id, rr.point_total DESC");
            ps = c.prepareStatement(sqlStr.toString());
            ps.setInt(1, roundId);
            rs = ps.executeQuery();
            

            sqlStr = new StringBuffer();
            sqlStr.append("UPDATE room_result SET room_placed = ? WHERE round_id = ? AND coder_id = ?");

            // Loop over the rows updating room result
            int lastRoom = -1, place = 1, numInPlace = 0, i;
            double lastPoints = 0;
            while(rs.next()) {
                psUpdate = c.prepareStatement(sqlStr.toString());

                int coderId = rs.getInt("coder_id");
                int room = rs.getInt("room_id");
                double points = rs.getDouble("point_total");

                // Are we starting a new room?
                if (room != lastRoom) {
                    place = 1;
                    numInPlace = 0;
                    lastRoom = room;
                    lastPoints = points;
                }

                if (points == lastPoints) {
                    numInPlace++;
                } else {
                    place += numInPlace;
                    numInPlace = 1;
                }
                lastPoints = points;

                psUpdate.setInt(1, place);
                psUpdate.setInt(2, roundId);
                psUpdate.setInt(3, coderId);
                int rowsUpdated = psUpdate.executeUpdate();

                psUpdate.close();
                psUpdate = null;
            }
            
            rs.close();
            rs = null;
            
            ps.close();
            ps = null;


            // Set division_placed - where the coder placed in their division
            // get all the users in order for this division by points.
            sqlStr = new StringBuffer();
            sqlStr.append("SELECT rr.coder_id, r.division_id, rr.point_total ");
            sqlStr.append("FROM room_result rr, room r ");
            sqlStr.append("WHERE r.room_id = rr.room_id ");
            sqlStr.append("AND rr.round_id = ? ");
            sqlStr.append("AND rr.attended = 'Y' ");
            sqlStr.append("AND r.division_id <> -1 ");
            sqlStr.append("ORDER BY r.division_id, rr.point_total DESC");
            ps = c.prepareStatement(sqlStr.toString());
            ps.setInt(1, roundId);
            rs = ps.executeQuery();
            
            int previousDivision = -1;
            int divisionPlace = 0;

            sqlStr = new StringBuffer();
            sqlStr.append("UPDATE room_result SET division_placed = ? WHERE coder_id = ? AND round_id = ?");


            while(rs.next()) {
                psUpdate = c.prepareStatement(sqlStr.toString());
                psUpdate.setInt(3, roundId);

                int coderId = rs.getInt("coder_id");
                int divisionId = rs.getInt("division_id");
                double points = rs.getDouble("point_total");

                // Are we starting a new division?
                if (divisionId != previousDivision) {
                    divisionPlace = 1;
                    numInPlace = 0;
                    previousDivision = divisionId;
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
                int rowsUpdated = psUpdate.executeUpdate();

                psUpdate.close();
                psUpdate = null;
            }
            ps.close();
            ps = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBMS.close(null, psUpdate, null);
            DBMS.close(c, ps, rs);
        }
    }
    
}
