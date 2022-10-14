package com.topcoder.shared.util.dwload;

import com.topcoder.shared.util.DBMS;
import com.topcoder.shared.util.logging.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Fixes round data for marathon
 * 
 * @author Cucu
 *
 */
public class TCFixMarathonRounds extends TCLoadLong {
    private static Logger log = Logger.getLogger(TCFixMarathonRounds.class);

    /**
     * This method performs the load for the round information tables
     */
    public void performLoad() throws Exception {
        try {                       

            List<Integer> rounds = getRounds();
            
            for (Integer rId : rounds) {
                log.info("Loading round: " + roundId);
                
                roundId = rId;
                
                clearRound();
                
                loadContest();
                
                loadRound();
                         
                loadResult();
            }

            
            loadStreaks();

            // rating order is new for algo
            loadRatingOrder(1);
            loadRatingOrder(2);
            loadRatingOrder(3);
            
            log.info("SUCCESS!");
        } catch (Exception ex) {
            setReasonFailed(ex.getMessage());
            throw ex;
        }
    }
    
    
    private void clearRound() throws Exception {
        PreparedStatement ps = null;
        ArrayList<String> a = null;

        try {
            a = new ArrayList<String>();


            a.add("DELETE FROM long_comp_result WHERE round_id = ?");

            int count = 0;
            for (int i = 0; i < a.size(); i++) {
                ps = prepareStatement((String) a.get(i), TARGET_DB);
                if (((String) a.get(i)).indexOf('?') > -1)
                    ps.setInt(1, roundId);
                count = ps.executeUpdate();
                log.info("" + count + " rows: " + (String) a.get(i));
            }
        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("clearing data failed.\n" +
                    sqle.getMessage());
        } finally {
            close(ps);
        }
    }

    /*
    private List<Integer> getRounds() throws SQLException {
        List<Integer> rounds = new ArrayList<Integer>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        StringBuffer query = new StringBuffer(100);
        query.append("SELECT r.round_id ");
        query.append("FROM round r, round_type_lu rt, round_segment rs "); 
        query.append("WHERE r.round_type_id = rt.round_type_id "); 
        query.append("AND rs.round_id = r.round_id ");
        query.append("AND rs.segment_id = 2 ");
        query.append("AND (algo_rating_type_id = 3 OR r.round_type_id=15) "); // accept intel as well
        //query.append("AND rated_ind = 1 ");
        query.append("ORDER BY rs.start_time "); 
        
        try {
            ps = prepareStatement(query.toString(), SOURCE_DB);
            rs = ps.executeQuery();
            while(rs.next()) {
                rounds.add(rs.getInt("round_id"));
            }
        } finally {
            close(rs);
            close (ps);
        }
        return rounds;
    }*/

    private List<Integer> getRounds() throws SQLException {
        List<Integer> rounds = new ArrayList<Integer>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        StringBuffer query = new StringBuffer(100);
        query.append("select round_id from round ");
        query.append("where round_type_id in (13,15,19) ");
        query.append("order by calendar_id, time_id, round_id ");
        
        try {
            ps = prepareStatement(query.toString(), TARGET_DB);
            rs = ps.executeQuery();
            while(rs.next()) {
                rounds.add(rs.getInt("round_id"));
            }
        } finally {
            close(rs);
            close (ps);
        }
        return rounds;
    }


    private void loadRatingOrder(int algoType) throws SQLException {
        log.debug("Loading rating_order for algo type " + algoType);
        PreparedStatement ps = null;
        PreparedStatement psUpd = null;
        ResultSet rs = null;

        
        
        try {
            StringBuffer query = new StringBuffer(100);

            query.append("select calendar_id, time_id, round_id ");
            query.append(" from round r ");
            query.append(" , round_type_lu rt ");
            query.append(" where r.round_type_id = rt.round_type_id ");
            query.append(" and rt.algo_rating_type_id = " + algoType);
            query.append(" and r.rated_ind = 1 ");
            query.append(" order by calendar_id, time_id, round_id ");
            ps = prepareStatement(query.toString(), TARGET_DB);
            
            psUpd = prepareStatement("UPDATE round SET rating_order=? WHERE round_id=?", TARGET_DB);
            
            rs = ps.executeQuery();
            int ratingOrder = 1;
            while(rs.next()) {
                psUpd.clearParameters();
                psUpd.setInt(1, ratingOrder);
                psUpd.setInt(2, rs.getInt("round_id"));
                int retVal = psUpd.executeUpdate();
                if (retVal != 1) {
                    log.debug("Not exaclty 1 row updated: " + retVal);
                }
                ratingOrder++;
            }
            log.debug("loaded " + (ratingOrder - 1) + " rows ");
        } finally {
            close(rs);
            close (ps);
            close (psUpd);
        }

    }

    @Override
    public boolean setParameters(Hashtable params) {
        return true;
    }

}
