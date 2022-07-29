/*
 * RecalcPlaced.java
 *
 * Created on February 17, 2006, 10:43 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.topcoder.utilities;

import org.apache.log4j.Logger;
import com.topcoder.shared.util.DBMS;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import com.topcoder.shared.dataAccess.resultSet.ResultSetContainer;

/**
 *
 * @author rfairfax
 */
public class RecalcOverallRank {
    public static final Logger log = Logger.getLogger(RecalcOverallRank.class);
    
    /** Creates a new instance of RecalcOverallRank */
    public RecalcOverallRank() {
    }
    
    public static void main(String[] args) {
        int roundId = Integer.parseInt(args[0]);
        int ratingType = Integer.parseInt(args[1]);
        try {
            go(DBMS.getDirectConnection(), roundId, ratingType);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static ResultSetContainer runSelectQuery(PreparedStatement ps) throws SQLException {
        ResultSet rs = null;
        try {
            rs = ps.executeQuery();
            return new ResultSetContainer(rs);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }
    
    public static void go(Connection c, int roundId, int ratingType) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            int i;
            ResultSetContainer rsc = null;
            // update ROOM_RESULT.room_placed
            // Set overall_rank
            StringBuilder sqlStr = new StringBuilder();
            sqlStr.append("SELECT rr.coder_id, r.room_id, rr.point_total ");
            sqlStr.append("FROM room_result rr, room r ");
            sqlStr.append("WHERE r.room_id = rr.room_id ");
            sqlStr.append("AND rr.round_id = ? ");
            sqlStr.append("AND rr.attended = 'Y' ");
            sqlStr.append("ORDER BY r.room_id, rr.point_total DESC");
            ps = c.prepareStatement(sqlStr.toString());
            ps.setInt(1, roundId);
            ResultSetContainer contestantList = runSelectQuery(ps);
            ps.close();
            ps = null;

            sqlStr = new StringBuilder();
            sqlStr.append("SELECT u.user_id, cr.rating ");
            sqlStr.append("FROM user u, algo_rating cr, coder c ");
            sqlStr.append("WHERE u.user_id = c.coder_id ");
            sqlStr.append("AND c.coder_id = cr.coder_id ");
            sqlStr.append("AND cr.rating > 0 ");
            sqlStr.append("AND cr.algo_rating_type_id = ? ");
            sqlStr.append("ORDER BY cr.rating DESC");
            ps = c.prepareStatement(sqlStr.toString());
            ps.setInt(1, ratingType);
    
            // Make a ranklist.
            rs = ps.executeQuery();
            rsc = new ResultSetContainer(rs, 1, Integer.MAX_VALUE, 2);
            rs.close();
            rs = null;
            ps.close();
            ps = null;
    
            // Build a hash map of the users to rating rank, for fast access
            HashMap rankMap = new HashMap();
            for (i = 0; i < rsc.getRowCount(); i++) {
                int userId = Integer.parseInt(rsc.getItem(i, 0).getResultData().toString());
                int rank = Integer.parseInt(rsc.getItem(i, "rank").
                                            getResultData().toString());
                rankMap.put(Integer.valueOf(userId), Integer.valueOf(rank));
            }
    
            // Instead of searching for each user in the ranklist to see whether he is in the contest
            // (as done in the old code - thousands of queries - very inefficient) we go through the list of
            // users in the contest and just look them up in the rank map.  The list of users was
            // generated above when updating room_placed.
            ps = c.prepareStatement("UPDATE room_result SET overall_rank = ? WHERE coder_id = ? AND round_id = ?");
            ps.setInt(3, roundId);
            for (i = 0; i < contestantList.getRowCount(); i++) {
                int coderId = Integer.parseInt(contestantList.getItem(i, "coder_id").toString());
                Integer rank = (Integer) rankMap.get(Integer.valueOf(coderId));
                if (rank == null) {
                    // User not in ranklist, probably because he is unrated
                    continue;
                }
                ps.setInt(1, rank.intValue());
                ps.setInt(2, coderId);
                int rowsUpdated = ps.executeUpdate();
                if (rowsUpdated != 1) {
                    throw new Exception("WRONG NUMBER OF ROWS UPDATED FOR overall_rank: " + rowsUpdated +
                                        " where coder_id = " + coderId);
                } else {
                    log.debug("Set coder #" + coderId + " overall rank = " + rank);
                }
            }
        } finally {
            DBMS.close(ps,rs);
        }
    }
    
}
