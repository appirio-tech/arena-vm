package com.topcoder.shared.util.dwload;

/**
 * This program is intendeed to run once to set the value of the elapsed_time field in challenge table in topcoder_dw
 */

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

import com.topcoder.shared.util.DBMS;
import com.topcoder.shared.util.logging.Logger;

public class TCFixChallengeTime extends TCLoad {
    private static Logger log = Logger.getLogger(TCFixChallengeTime.class);

    /**
     * Constructor. Set our usage message here.
     */
    public TCFixChallengeTime() {
        DEBUG = false;

        USAGE_MESSAGE = "";
    }

    /**
     * This method is passed any parameters passed to this load
     */
    public boolean setParameters(Hashtable params) {
    	   return true;
    }

    /**
     * This method performs the load for the round information tables
     */
    public void performLoad() throws Exception {
        try {
            loadChallenge();

            log.info("SUCCESS");
        } catch (Exception ex) {
            setReasonFailed(ex.getMessage());
            throw ex;
        }
    }


    /**
     * This populates the 'challenge' table
     */
    private void loadChallenge() throws Exception {
        PreparedStatement psUpd = null;
        PreparedStatement psSel = null;
        ResultSet rs = null;
        StringBuffer query = null;
        
        try {
        	query = new StringBuffer(100);
            query.append("UPDATE challenge set time_elapsed = submit_time - ? where round_id = ?");
            psUpd = prepareStatement(query.toString(), TARGET_DB);
            
        	query = new StringBuffer(100);
            query.append("select round_id, start_time from round_segment where segment_id= 4");
            psSel = prepareStatement(query.toString(), SOURCE_DB);
            rs = psSel.executeQuery();
            
            while (rs.next()) {
            	long roundId = rs.getLong(1);
            	long startTime = rs.getTimestamp(2).getTime();
            	
            	psUpd.setLong(1, startTime);
            	psUpd.setLong(2, roundId);
            	int count = psUpd.executeUpdate();
            	
            	log.info(count + " records updated for round " + roundId);
            }
            
        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Load of 'challenge' table failed.\n" +
                    sqle.getMessage());
        } finally {
            close(rs);
            close(psSel);
            close(psUpd);
        }
    }

}
