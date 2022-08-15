package com.topcoder.shared.util.dwload;

/**
 * TCLoadRound.java
 *
 * TCLoadRound loads round information from the transactional database and
 * populates tables in the data warehouse.
 *
 * The tables that are built by this load procedure are:
 *
 * <ul>
 * <li>algo_rating (additional information is populated in the algo_rating table)</li>
 * <li>problem_submission</li>
 * <li>system_test_case</li>
 * <li>system_test_result</li>
 * <li>contest</li>
 * <li>problem</li>
 * <li>round</li>
 * <li>room</li>
 * <li>room_result</li>
 * <li>coder_problem</li>
 * </ul>
 *
 * @author Christopher Hopkins [TCid: darkstalker] (chrism_hopkins@yahoo.com)
 * @version $Revision$
 */

import com.topcoder.shared.util.DBMS;
import com.topcoder.shared.util.logging.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;

public class TCFixNumRatings extends TCLoad {
	private static Logger log = Logger.getLogger(TCLoadRound.class);
	
	// first round to start fixing data.
	
	private static int START_ROUND = 10022; 
    /**
     * This method is passed any parameters passed to this load
     */
    public boolean setParameters(Hashtable params) {
        return true;
    }

    public void performLoad() throws Exception {
        try {
        	StringBuffer query;
        	PreparedStatement psRound;
        	PreparedStatement psCod;
        	PreparedStatement psNumRat;
        	PreparedStatement psUpd;
        	ResultSet rsRound;
        	ResultSet rs2;
        	ResultSet rsCod;
        	
            query = new StringBuffer(100);
            query.append(" update room_result set num_ratings=? where coder_id =? and round_id =?");
            psUpd = prepareStatement(query.toString(), TARGET_DB);;

        	query = new StringBuffer(100);
        	query.append(" select round_id from round r, calendar c ");
        	query.append(" where r.calendar_id = c.calendar_id");
        	query.append(" and c.calendar_id >= (select calendar_id from round where round_id = " + START_ROUND + ")");
        	query.append(" and round_type_id in (1,2,17,18)");
        	
        	psRound = prepareStatement(query.toString(), TARGET_DB);
        	rsRound = psRound.executeQuery();
        	
        	
        	while (rsRound.next()) {
        		int roundId = rsRound.getInt(1);
        		int algoType = getRoundType(roundId);
        		log.info("Fixing round " + roundId + " - type: " + algoType);

                query = new StringBuffer(100);
                query.append(" select count(*) as count");
                query.append(" , rr2.coder_id");
                query.append(" from room_result rr2 ");
                query.append(" , calendar c1");
                query.append(" , calendar c2");
                query.append(" , round r1");
                query.append(" , round r2");
                query.append(" , round_type_lu rt ");
                query.append(" where rr2.round_id = r2.round_id");
                query.append(" and r1.calendar_id = c1.calendar_id");
                query.append(" and r2.calendar_id = c2.calendar_id");
                query.append(" and r2.round_type_id = rt.round_type_id");
                query.append(" and rt.algo_rating_type_id = " + algoType);
                query.append(" and r1.round_id = ?");
                query.append(" and rr2.rated_flag = 1");
                query.append(" and c2.date < c1.date");
                query.append(" group by rr2.coder_id");
                psNumRat = prepareStatement(query.toString(), TARGET_DB);

                HashMap ratingsMap = new HashMap();
                psNumRat.setInt(1, roundId);
                rs2 = psNumRat.executeQuery();

                while (rs2.next()) {
                    ratingsMap.put(new Long(rs2.getLong("coder_id")), new Integer(rs2.getInt("count")));
                }
                
                query = new StringBuffer(100);
                query.append(" select coder_id from room_result where round_id=?");
                psCod = prepareStatement(query.toString(), TARGET_DB);
                psCod.setInt(1, roundId);
                rsCod = psCod.executeQuery();
                
                while (rsCod.next()) {
                	int cr = rsCod.getInt(1);

                	int numRatings = 0;
                    Long tempCoderId = new Long(cr);
                    if (ratingsMap.containsKey(tempCoderId))
                        numRatings = ((Integer) ratingsMap.get(tempCoderId)).intValue();
                	
                	psUpd.setInt(1, numRatings + 1);
                	psUpd.setInt(2, cr);
                	psUpd.setInt(3, roundId);
                	int r = psUpd.executeUpdate();
                	if (r!= 1) {
                		throw new Exception("Expected 1 record to be updated for coder " + cr);
                	}
                }
        	}
          

          } catch (Exception ex) {
            setReasonFailed(ex.getMessage());
            throw ex;
         }
     }

}
