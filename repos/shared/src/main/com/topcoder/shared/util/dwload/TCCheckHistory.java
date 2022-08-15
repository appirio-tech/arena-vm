package com.topcoder.shared.util.dwload;

/**
 * TCLoadAggregate.java
 *
 * TCLoadAggregate loads the various aggregate tables in the data warehouse.
 *
 * The tables that are built by this load procedure are:
 *
 * <ul>
 * <li>room_result</li>
 * <li>coder_division</li>
 * <li>round_division</li>
 * <li>coder_problem_summary</li>
 * <li>coder_level</li>
 * <li>streak</li>
 * <li>round_problem</li>
 * <li>team_round</li>
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
import java.util.Hashtable;

public class TCCheckHistory extends TCLoad {
    private static Logger log = Logger.getLogger(TCCheckHistory.class);

    private int fRoundId = -1;                 // roundid

    /**
     * This method is passed any parameters passed to this load
     */
    public boolean setParameters(Hashtable params) {
        try {

            fRoundId = retrieveIntParam("roundid", params, false, true).intValue();


        } catch (Exception ex) {
            setReasonFailed(ex.getMessage());
            return false;
        }

        return true;
    }

    /**
     * This method performs the load for the round information tables
     */
    public void performLoad() throws Exception {
        try {
            int algoType = getRoundType(fRoundId);

            checkRatingHistory();
            checkRankHistory();

            if (algoType == 2) {
                checkSeasonRatingHistory();
                checkSeasonRankHistory();
            }
            log.info("SUCCESS: Check Finished.");
        } catch (Exception ex) {
            setReasonFailed(ex.getMessage());
            throw ex;
        }
    }


    private void checkRatingHistory() throws Exception {
        PreparedStatement psSel = null;
        ResultSet rs = null;
        StringBuffer query = null;

        try {
            // Get all the coders that participated in this round
            query = new StringBuffer(100);
            query.append(" select coder_id from room_result where rated_flag = 1 and attended = 'Y' ");
            query.append("and round_id = ? ");
            query.append("and coder_id not in (select coder_id from algo_rating_history where round_id = ?) ");
            query.append("and coder_id in (select coder_id from coder where status = 'A') ");
            psSel = prepareStatement(query.toString(), SOURCE_DB);

            psSel.setInt(1, fRoundId);
            psSel.setInt(2, fRoundId);

            rs = psSel.executeQuery();

            boolean found = false;
            while (rs.next()) {
                if (!found) {
                    log.info("The following coders are not present in algo_rating_history for round " + fRoundId);
                }
                found = true;
                log.info("    " + rs.getInt("coder_id"));
            }
            if (!found) {
                log.info("algo_rating_history isn't missing any record for round " + fRoundId);
            }

        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Check of 'algo_rating_history' table failed.\n" +
                    sqle.getMessage());
        } finally {
            close(rs);
            close(psSel);
        }
    }

    private void checkSeasonRatingHistory() throws Exception {
        PreparedStatement psSel = null;
        ResultSet rs = null;
        StringBuffer query = null;

        try {
            // Get all the coders that participated in this round
            query = new StringBuffer(100);
            query.append(" select coder_id from room_result where rated_flag = 1 and attended = 'Y' ");
            query.append("and round_id = ? ");
            query.append("and coder_id not in (select coder_id from season_algo_rating_history where round_id = ?) ");
            query.append("and coder_id in (select coder_id from coder where status = 'A') ");
            psSel = prepareStatement(query.toString(), SOURCE_DB);

            psSel.setInt(1, fRoundId);
            psSel.setInt(2, fRoundId);

            rs = psSel.executeQuery();

            boolean found = false;
            while (rs.next()) {
                if (!found) {
                    log.info("The following coders are not present in season_algo_rating_history for round " + fRoundId);
                }
                found = true;
                log.info("    " + rs.getInt("coder_id"));
            }
            if (!found) {
                log.info("season_algo_rating_history isn't missing any record for round " + fRoundId);
            }

        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Check of 'season_algo_rating_history' table failed.\n" +
                    sqle.getMessage());
        } finally {
            close(rs);
            close(psSel);
        }
    }

    private void checkSeasonRankHistory() throws Exception {
        PreparedStatement psSel = null;
        ResultSet rs = null;
        StringBuffer query = null;

        try {
            // Get all the coders that participated in this round
            query = new StringBuffer(100);
            query.append(" select coder_id from room_result where rated_flag = 1 and attended = 'Y' ");
            query.append("and round_id = ? ");
            query.append("and coder_id not in (select coder_id from season_rank_history where round_id = ?) ");
            query.append("and coder_id in (select coder_id from coder where status = 'A') ");
            psSel = prepareStatement(query.toString(), SOURCE_DB);

            psSel.setInt(1, fRoundId);
            psSel.setInt(2, fRoundId);

            rs = psSel.executeQuery();

            boolean found = false;
            while (rs.next()) {
                if (!found) {
                    log.info("The following coders are not present in season_rank_history for round " + fRoundId);
                }
                found = true;
                log.info("    " + rs.getInt("coder_id"));
            }
            if (!found) {
                log.info("season_rank_history isn't missing any record for round " + fRoundId);
            }

        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Check of 'season_rank_history' table failed.\n" +
                    sqle.getMessage());
        } finally {
            close(rs);
            close(psSel);
        }
    }

    private void checkRankHistory() throws Exception {
        PreparedStatement psSel = null;
        ResultSet rs = null;
        StringBuffer query = null;

        try {
            // Get all the coders that participated in this round
            query = new StringBuffer(100);
            query.append(" select coder_id from room_result where rated_flag = 1 and attended = 'Y' ");
            query.append("and round_id = ? ");
            query.append("and coder_id not in (select coder_id from coder_rank_history where coder_rank_type_id = 2 and round_id = ?) ");
            query.append("and coder_id in (select coder_id from coder where status = 'A') ");
            psSel = prepareStatement(query.toString(), SOURCE_DB);

            psSel.setInt(1, fRoundId);
            psSel.setInt(2, fRoundId);

            rs = psSel.executeQuery();

            boolean found = false;
            while (rs.next()) {
                if (!found) {
                    log.info("The following coders are not present in coder_rank_history for round " + fRoundId);
                }
                found = true;
                log.info("    " + rs.getInt("coder_id"));
            }
            if (!found) {
                log.info("coder_rank_history isn't missing any record for round " + fRoundId);
            }

        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Check of 'coder_rank_history' table failed.\n" +
                    sqle.getMessage());
        } finally {
            close(rs);
            close(psSel);
        }
    }


}
