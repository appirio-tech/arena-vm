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

import com.topcoder.shared.dataAccess.resultSet.ResultSetContainer;
import com.topcoder.shared.util.DBMS;
import com.topcoder.shared.util.logging.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TreeSet;

public class TCLoadAggregate extends TCLoad {
    private static Logger log = Logger.getLogger(TCLoadAggregate.class);
    // The following set of variables are all configureable from the command
    // line by specifying -variable (where the variable is after the //)
    // followed by the new value
    private int fRoundId = -1;                 // roundid
    private int STATUS_FAILED = 0;    // failed
    private int STATUS_SUCCEEDED = 1;    // succeeded
    private int SINGLE_ROUND_MATCH = 1;    // singrndmatch
    private int TOURNAMENT_ROUND = 2;
    private int LONG_ROUND = 10;
    private int RATING_INCREASE_SRM_ONLY = 3;
    private int RATING_INCREASE = 4;
    private int RATING_DECREASE_SRM_ONLY = 5;
    private int RATING_DECREASE = 6;
    private int RATING_SRM_APPEARANCES = 7;
    private int CONSEC_WINS_DIV1 = 1;    // conswinsdiv1
    private int CONSEC_WINS_DIV2 = 2;    // conswinsdiv2
    private int STATUS_OPENED = 120;  // opened
    private int STATUS_SUBMITTED = 130;  // submitted
    private int STATUS_CHLNG_SUCCEEDED = 140;  // chlngsucceeded
    private int STATUS_PASSED_SYS_TEST = 150;  // passsystest
    private int STATUS_FAILED_SYS_TEST = 160;  // failsystest

    private boolean FULL_LOAD = false;//fullload
    private boolean ONLY_TEAM_ROUND = false;
    private int algoType = 0; // 1 for regular, 2 for hs, 3 for marathon

    /**
     * Constructor. Set our usage message here.
     */
    public TCLoadAggregate() {
        USAGE_MESSAGE = new String(
                "TCLoadAggregate parameters - defaults in ():\n" +
                        "  -roundid number       : Round ID to load\n" +
                        "  [-failed number]         : Failed status for succeeded column    (0)\n" +
                        "  [-succeeded number]      : Succeeded status for succeeded column (1)\n" +
                        "  [-singrndmatch number]   : Round_type_id for single round matches   (1)\n" +
                        "  [-conswinsdiv1 number]   : Streak_type_id for consecutive div1 wins (1)\n" +
                        "  [-conswinsdiv2 number]   : Streak_type_id for consecutive div2 wins (2)\n" +
                        "  [-opened number]         : Problem_status of opened              (120)\n" +
                        "  [-submitted number]      : Problem_status of submitted           (130)\n" +
                        "  [-chlngsucceeded number] : Problem_status of challenge succeeded (140)\n" +
                        "  [-passsystest number]    : Problem_status of passed system test  (150)\n" +
                        "  [-failsystest number]    : Problem_status of failed system test  (160)\n" +
                        "  [-fullload boolean] : true-clean round load, false-selective  (false)\n" +
                        "  [-lnlyteamround boolean] : true: just loads the team_round table  (false)\n");
    }

    /**
     * This method is passed any parameters passed to this load
     */
    public boolean setParameters(Hashtable params) {
        try {
            Integer tmp;
            Boolean tmpBool;

            fRoundId = retrieveIntParam("roundid", params, false, true).intValue();

            tmp = retrieveIntParam("failed", params, true, true);
            if (tmp != null) {
                STATUS_FAILED = tmp.intValue();
                log.info("New failed is " + STATUS_FAILED);
            }

            tmp = retrieveIntParam("succeeded", params, true, true);
            if (tmp != null) {
                STATUS_SUCCEEDED = tmp.intValue();
                log.info("New succeeded is " + STATUS_SUCCEEDED);
            }

            tmp = retrieveIntParam("opened", params, true, true);
            if (tmp != null) {
                STATUS_OPENED = tmp.intValue();
                log.info("New opened is " + STATUS_OPENED);
            }

            tmp = retrieveIntParam("submitted", params, true, true);
            if (tmp != null) {
                STATUS_SUBMITTED = tmp.intValue();
                log.info("New submitted is " + STATUS_SUBMITTED);
            }

            tmp = retrieveIntParam("chlngsucceeded", params, true, true);
            if (tmp != null) {
                STATUS_CHLNG_SUCCEEDED = tmp.intValue();
                log.info("New chlngsucceeded is " + STATUS_CHLNG_SUCCEEDED);
            }

            tmp = retrieveIntParam("passsystest", params, true, true);
            if (tmp != null) {
                STATUS_PASSED_SYS_TEST = tmp.intValue();
                log.info("New passsystest is " + STATUS_PASSED_SYS_TEST);
            }

            tmp = retrieveIntParam("failsystest", params, true, true);
            if (tmp != null) {
                STATUS_FAILED_SYS_TEST = tmp.intValue();
                log.info("New failsystest  is " + STATUS_FAILED_SYS_TEST);
            }

            tmp = retrieveIntParam("conswinsdiv1", params, true, true);
            if (tmp != null) {
                CONSEC_WINS_DIV1 = tmp.intValue();
                log.info("New conswinsdiv1  is " + CONSEC_WINS_DIV1);
            }

            tmp = retrieveIntParam("conswinsdiv2", params, true, true);
            if (tmp != null) {
                CONSEC_WINS_DIV2 = tmp.intValue();
                log.info("New conswinsdiv2  is " + CONSEC_WINS_DIV2);
            }

            tmp = retrieveIntParam("singrndmatch", params, true, true);
            if (tmp != null) {
                SINGLE_ROUND_MATCH = tmp.intValue();
                log.info("New singrndmatch is " + SINGLE_ROUND_MATCH);
            }

            tmpBool = retrieveBooleanParam("fullload", params, true);
            if (tmpBool != null) {
                FULL_LOAD = tmpBool.booleanValue();
                log.info("New fullload flag is " + FULL_LOAD);
            }

            tmpBool = retrieveBooleanParam("onlyteamround", params, true);
            if (tmpBool != null) {
                ONLY_TEAM_ROUND = tmpBool.booleanValue();
                log.info("New fullload onlyteamround is " + ONLY_TEAM_ROUND);
            }

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
            if (ONLY_TEAM_ROUND) {
                loadTeamRound();
                log.info("SUCCESS: team_round load ran successfully.");
                return;
            }
            algoType = getRoundType(fRoundId);
            loadRoomResult2();
            loadCoderDivision();

            loadRoundDivision();

            loadRoomResult3();

            loadCoderProblemSummary();

            loadCoderLevel();

            if (algoType == TC_RATING_TYPE_ID) {
                loadStreak();

                loadRatingIncreaseStreak(true);

                loadRatingIncreaseStreak(false);

                loadRatingDecreaseStreak(true);

                loadRatingDecreaseStreak(false);

                loadConsecutiveSRMAppearances();
            }

            loadRoundProblem();

            loadProblemLanguage();

            loadCoderProblem();

            if (algoType == HS_RATING_TYPE_ID) {
                loadTeamRound();
                loadSeasonRatingHistory();
            }

            //if running for an old round, the rating history load can not be run.
            //if historic ratings have changed, then
            if (isMostRecentRound()) {
                loadRatingHistory();
            } else {
                log.info("\n\n\n\n\n\nIMPORTANT MESSAGE");
                log.info("You're running this load for a round that is not the most recent.  If the historic " +
                        "rating information has changed, then you'll need to update the algo_rating_history table " +
                        "by hand *BEFORE* you run the rank load.");
            }
            log.info("SUCCESS: Aggregate load ran successfully.");
        } catch (Exception ex) {
            setReasonFailed(ex.getMessage());
            throw ex;
        }
    }


    private final static String MOST_RECENT = "select calendar_id, time_id, round_id\n" +
            "from round r " +
            ", round_type_lu rt " +
            "where r.round_type_id = rt.round_type_id " +
            "and rt.algo_rating_type_id = ?" +
            "order by calendar_id desc, time_id desc, round_id desc";

    private boolean isMostRecentRound() throws Exception {
        PreparedStatement psSel = null;
        ResultSet rs = null;
        StringBuffer query = null;

        try {
            // Get all the coders that participated in this round
            psSel = prepareStatement(MOST_RECENT, SOURCE_DB);
            psSel.setLong(1, algoType);

            rs = psSel.executeQuery();

            if (rs.next()) {
                return rs.getInt("round_id")==fRoundId;
            } else {
                throw new RuntimeException("no rounds in system");
            }
        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Load of 'algo_rating_history' table failed.\n" +
                    sqle.getMessage());
        } finally {
            close(rs);
            close(psSel);
        }

    }

    /**
     * This method CANNOT be run after the fact; meaning, if the
     * rating table in the transactional database has the rating
     * for a match that is not the one we're running the load for
     * then rating history will get hosed up.
     *
     * @throws Exception
     */
    private void loadRatingHistory() throws Exception {
        int count = 0;
        int retVal = 0;
        PreparedStatement psSel = null;
        PreparedStatement psIns = null;
        PreparedStatement psDel = null;
        ResultSet rs = null;
        StringBuffer query = null;

        try {
            // Get all the coders that participated in this round
            query = new StringBuffer(100);
            query.append(" select coder_id, rating, vol, num_ratings, algo_rating_type_id");
            query.append(" from algo_rating");
            query.append(" where num_ratings > 0");
            query.append(" and algo_rating_type_id = " + algoType);

            psSel = prepareStatement(query.toString(), SOURCE_DB);

            query = new StringBuffer(100);
            query.append("insert into algo_rating_history (coder_id, round_id, rating, vol, num_ratings, algo_rating_type_id)");
            query.append("values (?,?,?,?,?,?)");
            psIns = prepareStatement(query.toString(), TARGET_DB);

            query = new StringBuffer(100);
            query.append("delete from algo_rating_history where round_id = ?");
            psDel = prepareStatement(query.toString(), TARGET_DB);
            psDel.setLong(1, fRoundId);

            psDel.executeUpdate();

            rs = psSel.executeQuery();

            while (rs.next()) {
                psIns.clearParameters();
                psIns.setLong(1, rs.getLong("coder_id"));
                psIns.setLong(2, fRoundId);
                psIns.setInt(3, rs.getInt("rating"));
                psIns.setInt(4, rs.getInt("vol"));
                psIns.setInt(5, rs.getInt("num_ratings"));
                psIns.setInt(6, rs.getInt("algo_rating_type_id"));

                retVal = psIns.executeUpdate();
                count = count + retVal;
                if (retVal != 1) {
                    throw new SQLException("TCLoadCoders: Insert for coderId " +
                            rs.getLong("coder_id") +
                            " modified " + retVal + " rows, not one.");
                }

                printLoadProgress(count, "algo_rating_history");
            }

            log.info("Rating History records updated = " + count);
        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Load of 'algo_rating_history' table failed.\n" +
                    sqle.getMessage());
        } finally {
            close(rs);
            close(psSel);
            close(psIns);
            close(psDel);
        }
    }

    /**
     * This method CANNOT be run after the fact; meaning, if the
     * rating table in the transactional database has the rating
     * for a match that is not the one we're running the load for
     * then rating history will get hosed up.
     *
     * @throws Exception
     */
    private void loadSeasonRatingHistory() throws Exception {
        int count = 0;
        int retVal = 0;
        PreparedStatement psSel = null;
        PreparedStatement psIns = null;
        PreparedStatement psDel = null;
        ResultSet rs = null;
        StringBuffer query = null;

        try {
            int seasonId = getSeasonId(fRoundId);
            // Get all the coders that participated in this round
            query = new StringBuffer(100);
            query.append(" select coder_id, rating, vol, num_ratings");
            query.append(" from season_algo_rating");
            query.append(" where num_ratings > 0");
            query.append(" and season_id = ").append(seasonId);

            psSel = prepareStatement(query.toString(), SOURCE_DB);

            query = new StringBuffer(100);
            query.append("insert into season_algo_rating_history (coder_id, round_id, rating, vol, num_ratings, season_id)");
            query.append("values (?,?,?,?,?,?)");
            psIns = prepareStatement(query.toString(), TARGET_DB);

            query = new StringBuffer(100);
            query.append("delete from season_algo_rating_history where round_id = ?");
            psDel = prepareStatement(query.toString(), TARGET_DB);
            psDel.setLong(1, fRoundId);

            psDel.executeUpdate();

            rs = psSel.executeQuery();

            while (rs.next()) {
                psIns.clearParameters();
                psIns.setLong(1, rs.getLong("coder_id"));
                psIns.setLong(2, fRoundId);
                psIns.setInt(3, rs.getInt("rating"));
                psIns.setInt(4, rs.getInt("vol"));
                psIns.setInt(5, rs.getInt("num_ratings"));
                psIns.setInt(6, seasonId);

                retVal = psIns.executeUpdate();
                count = count + retVal;
                if (retVal != 1) {
                    throw new SQLException("TCLoadCoders: Insert for coderId " +
                            rs.getLong("coder_id") +
                            " modified " + retVal + " rows, not one.");
                }

                printLoadProgress(count, "season_algo_rating_history");
            }

            log.info("Season Rating History records updated = " + count);
        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Load of 'season_algo_rating_history' table failed.\n" +
                    sqle.getMessage());
        } finally {
            close(rs);
            close(psSel);
            close(psIns);
            close(psDel);
        }
    }


    /**
     * This method loads the 'coder_division' table
     */
    private void loadCoderDivision() throws Exception {
        int retVal = 0;
        int count = 0;
        PreparedStatement psSel = null;
        PreparedStatement psIns = null;
        PreparedStatement psDel = null;
        ResultSet rs = null;
        StringBuffer query = null;

        try {
            query = new StringBuffer(100);
            query.append("SELECT rr.coder_id ");                              // 1
            query.append("       ,rr.division_id ");                          // 2
            query.append("       ,SUM(problems_presented) ");              // 3
            query.append("       ,SUM(problems_opened) ");                 // 4
            query.append("       ,SUM(problems_submitted) ");              // 5
            query.append("       ,SUM(problems_correct) ");                // 6
            query.append("       ,SUM(problems_failed_by_challenge) ");    // 7
            query.append("       ,SUM(problems_failed_by_system_test) ");  // 8
            query.append("       ,SUM(problems_left_open) ");              // 9
            query.append("       ,SUM(challenge_attempts_made) ");         // 10
            query.append("       ,SUM(challenges_made_successful) ");      // 11
            query.append("       ,SUM(challenges_made_failed) ");          // 12
            query.append("       ,SUM(challenge_attempts_received) ");     // 13
            query.append("       ,SUM(challenges_received_successful) ");  // 14
            query.append("       ,SUM(challenges_received_failed) ");      // 15
            query.append("       ,SUM(submission_points) ");               // 16
            query.append("       ,SUM(challenge_points) ");                // 17
            query.append("       ,SUM(system_test_points) ");              // 18
            query.append("       ,SUM(final_points) ");                    // 19
            query.append("       ,SUM(defense_points) ");                  // 20
            query.append("       ,rt.algo_rating_type_id ");               // 21
            query.append("  FROM room_result rr, round r, round_type_lu rt ");
            query.append("  WHERE rr.round_id = r.round_id ");
            query.append("  AND r.round_type_id = rt.round_type_id ");

            if (!FULL_LOAD) {   //if it's not a full load, just load up the people that competed in the round we're loading
                query.append(" AND rr.coder_id IN");
                query.append(" (SELECT coder_id");
                query.append(" FROM room_result");
                query.append(" WHERE attended = 'Y'");
                query.append(" AND round_id = " + fRoundId + ")");
                query.append(" AND rt.algo_rating_type_id = " + algoType);

            }
            query.append(" GROUP BY rr.coder_id ");
            query.append("          ,rr.division_id");
            query.append("          ,rt.algo_rating_type_id");
            psSel = prepareStatement(query.toString(), SOURCE_DB);

            query = new StringBuffer(100);
            query.append("INSERT INTO coder_division ");
            query.append("      (coder_id ");                         // 1
            query.append("       ,division_id ");                     // 2
            query.append("       ,problems_presented ");              // 3
            query.append("       ,problems_opened ");                 // 4
            query.append("       ,problems_submitted ");              // 5
            query.append("       ,problems_correct ");                // 6
            query.append("       ,problems_failed_by_challenge ");    // 7
            query.append("       ,problems_failed_by_system_test ");  // 8
            query.append("       ,problems_left_open ");              // 9
            query.append("       ,challenge_attempts_made ");         // 10
            query.append("       ,challenges_made_successful ");      // 11
            query.append("       ,challenges_made_failed ");          // 12
            query.append("       ,challenge_attempts_received ");     // 13
            query.append("       ,challenges_received_successful ");  // 14
            query.append("       ,challenges_received_failed ");      // 15
            query.append("       ,submission_points ");               // 16
            query.append("       ,challenge_points ");                // 17
            query.append("       ,system_test_points ");              // 18
            query.append("       ,final_points ");                    // 19
            query.append("       ,defense_points  ");                 // 20
            query.append("       ,algo_rating_type_id) ");            // 21
            query.append("VALUES (");
            query.append("?,?,?,?,?,?,?,?,?,?,");  // 10 values
            query.append("?,?,?,?,?,?,?,?,?,?,?)");  // 21 total values
            psIns = prepareStatement(query.toString(), TARGET_DB);

            query = new StringBuffer(100);
            query.append("DELETE FROM coder_division ");
            query.append(" WHERE coder_id = ? ");
            query.append("   AND division_id = ?");
            query.append("   AND algo_rating_type_id = ?");
            psDel = prepareStatement(query.toString(), TARGET_DB);

            // On to the load
            rs = psSel.executeQuery();

            while (rs.next()) {
                int coder_id = rs.getInt(1);
                int division_id = rs.getInt(2);
                int algo_rating_type_id = rs.getInt(21);

                psDel.clearParameters();
                psDel.setInt(1, coder_id);
                psDel.setInt(2, division_id);
                psDel.setInt(3, algo_rating_type_id);
                psDel.executeUpdate();

                psIns.clearParameters();
                psIns.setInt(1, rs.getInt(1));  // coder_id
                psIns.setInt(2, rs.getInt(2));  // division_id
                psIns.setInt(3, rs.getInt(3));  // problems_presented
                psIns.setInt(4, rs.getInt(4));  // problems_opened
                psIns.setInt(5, rs.getInt(5));  // problems_submitted
                psIns.setInt(6, rs.getInt(6));  // problems_correct
                psIns.setInt(7, rs.getInt(7));  // prblms_failed_by_systest
                psIns.setInt(8, rs.getInt(8));  // prblms_failed_by_chlnge
                psIns.setInt(9, rs.getInt(9));  // problems_left_open
                psIns.setInt(10, rs.getInt(10));  // challenge_attempts_made
                psIns.setInt(11, rs.getInt(11));  // chlnges_made_successful
                psIns.setInt(12, rs.getInt(12));  // chlnges_made_failed
                psIns.setInt(13, rs.getInt(13));  // chlnge_attempts_received
                psIns.setInt(14, rs.getInt(14));  // chlnge_recvd_successfl
                psIns.setInt(15, rs.getInt(15));  // chlnge_recvd_failed
                psIns.setFloat(16, rs.getFloat(16));  // submission_points
                psIns.setFloat(17, rs.getFloat(17));  // challenge_points
                psIns.setFloat(18, rs.getFloat(18));  // system_test_points
                psIns.setFloat(19, rs.getFloat(19));  // final_points
                psIns.setFloat(20, rs.getFloat(20));  // defense_points
                psIns.setInt(21, rs.getInt(21));  // algo_rating_type_id

                retVal = psIns.executeUpdate();
                count += retVal;
                if (retVal != 1) {
                    throw new SQLException("TCLoadAggregate: Insert for " +
                            "coder_id " + coder_id +
                            ", division_id " + division_id +
                            " modified " + retVal + " rows, not one.");
                }

                printLoadProgress(count, "coder_division");
            }

            log.info("Records loaded for coder_division: " + count);
        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Load of 'coder_division' table failed.\n" +
                    sqle.getMessage());
        } finally {
            close(rs);
            close(psSel);
            close(psIns);
            close(psDel);
        }
    }

    /**
     * This method loads the 'round_division' table
     */
    private void loadRoundDivision() throws Exception {
        int retVal = 0;
        int count = 0;
        PreparedStatement psSel = null;
        PreparedStatement psIns = null;
        PreparedStatement psDel = null;
        ResultSet rs = null;
        StringBuffer query = null;

        try {
            query = new StringBuffer(100);
            query.append("SELECT round_id ");                              // 1
            query.append("       ,division_id ");                          // 2
            query.append("       ,SUM(problems_presented) ");              // 3
            query.append("       ,SUM(problems_opened) ");                 // 4
            query.append("       ,SUM(problems_submitted) ");              // 5
            query.append("       ,SUM(problems_correct) ");                // 6
            query.append("       ,SUM(problems_failed_by_system_test) ");  // 7
            query.append("       ,SUM(problems_failed_by_challenge) ");    // 8
            query.append("       ,SUM(problems_left_open) ");              // 9
            query.append("       ,SUM(challenge_attempts_made) ");         // 10
            query.append("       ,SUM(challenges_made_successful) ");      // 11
            query.append("       ,SUM(challenges_made_failed) ");          // 12
            query.append("       ,SUM(challenge_attempts_received) ");     // 13
            query.append("       ,AVG(final_points) ");                    // 14
            query.append("       ,STDEV(final_points) ");                  // 15
            query.append("       ,SUM(defense_points) ");                  // 16
            query.append("       ,COUNT(*) ");                             // 17
            query.append("  FROM room_result ");
            if (!FULL_LOAD) {   //if it's not a full load, just load up the problems from this round
                query.append(" WHERE round_id =" + fRoundId);
            }
            query.append(" GROUP BY round_id ");
            query.append("          ,division_id");
            psSel = prepareStatement(query.toString(), SOURCE_DB);

            query = new StringBuffer(100);
            query.append("INSERT INTO round_division ");
            query.append("      (round_id ");                         // 1
            query.append("       ,division_id ");                     // 2
            query.append("       ,problems_presented ");              // 3
            query.append("       ,problems_opened ");                 // 4
            query.append("       ,problems_submitted ");              // 5
            query.append("       ,problems_correct ");                // 6
            query.append("       ,problems_failed_by_system_test ");  // 7
            query.append("       ,problems_failed_by_challenge ");    // 8
            query.append("       ,problems_left_open ");              // 9
            query.append("       ,challenge_attempts_made ");         // 10
            query.append("       ,challenges_made_successful ");      // 11
            query.append("       ,challenges_made_failed ");          // 12
            query.append("       ,challenge_attempts_received ");     // 13
            query.append("       ,average_points ");                  // 14
            query.append("       ,point_standard_deviation ");        // 15
            query.append("       ,defense_points  ");                 // 16
            query.append("       ,num_coders)  ");                    // 17
            query.append("VALUES (");
            query.append("?,?,?,?,?,?,?,?,?,?,");  // 10 values
            query.append("?,?,?,?,?,?,?)");        // 17 total values
            psIns = prepareStatement(query.toString(), TARGET_DB);

            query = new StringBuffer(100);
            query.append("DELETE FROM round_division ");
            query.append(" WHERE round_id = ? ");
            query.append("   AND division_id = ?");
            psDel = prepareStatement(query.toString(), TARGET_DB);

            // On to the load
            rs = psSel.executeQuery();

            while (rs.next()) {
                int round_id = rs.getInt(1);
                int division_id = rs.getInt(2);

                psDel.clearParameters();
                psDel.setInt(1, round_id);
                psDel.setInt(2, division_id);
                psDel.executeUpdate();

                psIns.clearParameters();
                psIns.setInt(1, rs.getInt(1));  // round_id
                psIns.setInt(2, rs.getInt(2));  // division_id
                psIns.setInt(3, rs.getInt(3));  // problems_presented
                psIns.setInt(4, rs.getInt(4));  // problems_opened
                psIns.setInt(5, rs.getInt(5));  // problems_submitted
                psIns.setInt(6, rs.getInt(6));  // problems_correct
                psIns.setInt(7, rs.getInt(7));  // prblms_failed_by_systest
                psIns.setInt(8, rs.getInt(8));  // prblms_failed_by_chlnge
                psIns.setInt(9, rs.getInt(9));  // problems_left_open
                psIns.setInt(10, rs.getInt(10));  // challenge_attempts_made
                psIns.setInt(11, rs.getInt(11));  // chlnges_made_successful
                psIns.setInt(12, rs.getInt(12));  // chlnges_made_failed
                psIns.setInt(13, rs.getInt(13));  // chlnge_attempts_received
                psIns.setFloat(14, rs.getFloat(14));  // average_points
                psIns.setFloat(15, rs.getFloat(15));  // point_standard_deviation
                psIns.setFloat(16, rs.getFloat(16));  // defense_points
                psIns.setInt(17, rs.getInt(17));  // num_coders

                retVal = psIns.executeUpdate();
                count += retVal;
                if (retVal != 1) {
                    throw new SQLException("TCLoadAggregate: Insert for " +
                            "round_id " + round_id +
                            ", division_id " + division_id +
                            " modified " + retVal + " rows, not one.");
                }

                printLoadProgress(count, "round_division");
            }

            log.info("Records loaded for round_division: " + count);
        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Load of 'round_division' table failed.\n" +
                    sqle.getMessage());
        } finally {
            close(rs);
            close(psSel);
            close(psIns);
            close(psDel);
        }
    }

    /**
     * This method loads the 'coder_level' table
     */
    private void loadCoderLevel() throws Exception {
        int retVal = 0;
        int count = 0;
        PreparedStatement psSel = null;
        PreparedStatement psIns = null;
        PreparedStatement psUpd = null;
        PreparedStatement psDel = null;
        ResultSet rs = null;
        StringBuffer query = null;

        // Need the point_standard_deviation for this table
        try {
            query = new StringBuffer(100);

            //  TODO replace hard codes with constants
            query.append(" SELECT cp.coder_id ");                       // 1
            query.append(" ,cp.division_id ");                          // 2
            query.append(" ,cp.level_id ");                             // 3
            query.append(" ,SUM(CASE WHEN cp.end_status_id >= 120 THEN 1 ELSE 0 END) AS problems_opened");     // 4
            query.append(" ,SUM(CASE WHEN cp.end_status_id >= 130 THEN 1 ELSE 0 END) AS problems_submitted");   //5
            query.append(" ,SUM(CASE WHEN cp.end_status_id = 150 THEN 1 ELSE 0 END) AS problems_correct");   //6
            query.append(" ,SUM(CASE WHEN cp.end_status_id = 140 THEN 1 ELSE 0 END) AS problems_failed_by_challenge ");   //7
            query.append(" ,SUM(CASE WHEN cp.end_status_id = 160 THEN 1 ELSE 0 END) AS problems_failed_by_system_test ");   //8
            query.append(" ,SUM(CASE WHEN cp.end_status_id between 120 and 121 THEN 1 ELSE 0 END) AS problems_left_open");   //9
            query.append(" ,AVG(cp.final_points)");   //10
            query.append(" ,SUM(cp.submission_points) ");   //11
            query.append(" ,SUM(cp.challenge_points)");   //12
            query.append(" ,SUM(cp.system_test_points)");   //13
            query.append(" ,SUM(cp.final_points)");   //14
            query.append(" ,STDEV(cp.final_points)");   //15
            query.append(" ,SUM(cp.defense_points)");   //16
            query.append(" ,(SELECT AVG(cp1.time_elapsed) ");
            query.append(" FROM coder_problem cp1, round r1, round_type_lu rt1 ");
            query.append(" WHERE cp1.level_id = cp.level_id ");
            query.append(" and r1.round_id = cp1.round_id ");
            query.append(" and r1.round_type_id = rt1.round_type_id ");
            query.append(" and rt1.algo_rating_type_id = ").append(algoType);
            query.append(" and cp1.coder_id = cp.coder_id ");
            query.append(" and cp1.division_id = cp.division_id ");
            query.append(" and cp1.end_status_id = ").append(STATUS_PASSED_SYS_TEST).append(")");     //17
            query.append(" ,rt.algo_rating_type_id ");  //18
            query.append(" FROM coder_problem cp");
            query.append("    , round r ");
            query.append("    , round_type_lu rt ");
            query.append("     WHERE cp.round_id = r.round_id ");
            query.append("     AND r.round_type_id = rt.round_type_id ");
            query.append("     AND rt.algo_rating_type_id = " + algoType);

            if (!FULL_LOAD) {   //if it's not a full load, just load up the people that competed in the round we're loading
                query.append(" AND cp.coder_id IN");
                query.append(" (SELECT coder_id");
                query.append(" FROM room_result");
                query.append(" WHERE attended = 'Y'");
                query.append(" AND round_id = " + fRoundId + ")");
            }
            query.append(" GROUP BY 1,2,3, 18");
            psSel = prepareStatement(query.toString(), SOURCE_DB);


            query = new StringBuffer(100);
            query.append(" INSERT ");
            query.append(" INTO coder_level ");
            query.append(" (coder_id ");                         // 1
            query.append(" ,division_id ");                     // 2
            query.append(" ,level_id ");                        // 3
            query.append(" ,problems_opened ");                 // 4
            query.append(" ,problems_submitted ");              // 5
            query.append(" ,problems_correct ");                // 6
            query.append(" ,problems_failed_by_challenge ");    // 7
            query.append(" ,problems_failed_by_system_test ");  // 8
            query.append(" ,problems_left_open ");              // 9
            query.append(" ,average_points ");                  // 10
            query.append(" ,submission_points ");               // 11
            query.append(" ,challenge_points ");                // 12
            query.append(" ,system_test_points ");              // 13
            query.append(" ,final_points ");                    // 14
            query.append(" ,point_standard_deviation ");        // 15
            query.append(" ,defense_points ");                  // 16
            query.append(" ,avg_time_elapsed ");                // 17
            query.append(" ,algo_rating_type_id) ");            // 18
            query.append(" VALUES (");
            query.append("?,?,?,?,?,?,?,?,?,?,");  // 10 values
            query.append("?,?,?,?,?,?,?,?)");       // 18 total values
            psIns = prepareStatement(query.toString(), TARGET_DB);

            query = new StringBuffer(100);
            query.append(" UPDATE coder_level");
            query.append(" SET problems_presented = ");
            query.append(" (SELECT count(*)");
            query.append(" FROM problem p ");
            query.append(" ,room_result rr ");
            query.append(" ,round r ");
            query.append(" ,round_type_lu rt ");
            query.append(" WHERE p.round_id = rr.round_id ");
            query.append(" AND r.round_id = rr.round_id ");
            query.append(" AND r.round_type_id = rt.round_type_id ");
            query.append(" AND rt.algo_rating_type_id=coder_level.algo_rating_type_id");
            query.append(" AND rr.coder_id = coder_level.coder_id");
            query.append(" AND rr.division_id = p.division_id");
            query.append(" AND p.level_id = coder_level.level_id");
            query.append(" AND p.division_id = coder_level.division_id)");

            query.append(" ,challenge_attempts_made = ");
            query.append(" (SELECT count(*) ");
            query.append(" FROM challenge c ");
            query.append(" ,problem p ");
            query.append(" ,room_result rr ");
            query.append(" ,round r ");
            query.append(" ,round_type_lu rt ");
            query.append(" WHERE c.challenger_id = rr.coder_id");
            query.append(" AND r.round_id = rr.round_id ");
            query.append(" AND r.round_type_id = rt.round_type_id ");
            query.append(" AND rt.algo_rating_type_id=coder_level.algo_rating_type_id");
            query.append(" AND rr.coder_id = coder_level.coder_id");
            query.append(" AND c.round_id = rr.round_id");
            query.append(" AND rr.round_id = p.round_id");
            query.append(" AND rr.division_id = p.division_id");
            query.append(" AND p.problem_id = c.problem_id");
            query.append(" AND p.division_id = coder_level.division_id");
            query.append(" AND p.level_id = coder_level.level_id)");

            query.append(" ,challenges_made_successful = ");
            query.append(" (SELECT count(*) ");
            query.append(" FROM challenge c ");
            query.append(" ,problem p ");
            query.append(" ,room_result rr ");
            query.append(" ,round r ");
            query.append(" ,round_type_lu rt ");
            query.append(" WHERE c.challenger_id = rr.coder_id");
            query.append(" AND r.round_id = rr.round_id ");
            query.append(" AND r.round_type_id = rt.round_type_id ");
            query.append(" AND rt.algo_rating_type_id=coder_level.algo_rating_type_id");
            query.append(" AND rr.coder_id = coder_level.coder_id");
            query.append(" AND c.round_id = rr.round_id");
            query.append(" AND rr.round_id = p.round_id");
            query.append(" AND rr.division_id = p.division_id");
            query.append(" AND p.problem_id = c.problem_id");
            query.append(" AND p.division_id = coder_level.division_id");
            query.append(" AND p.level_id = coder_level.level_id ");
            query.append(" AND c.succeeded = " + STATUS_SUCCEEDED + ")");

            query.append(" ,challenges_made_failed = ");
            query.append(" (SELECT count(*) ");
            query.append(" FROM challenge c ");
            query.append(" ,problem p ");
            query.append(" ,room_result rr ");
            query.append(" ,round r ");
            query.append(" ,round_type_lu rt ");
            query.append(" WHERE c.challenger_id = rr.coder_id");
            query.append(" AND r.round_id = rr.round_id ");
            query.append(" AND r.round_type_id = rt.round_type_id ");
            query.append(" AND rt.algo_rating_type_id=coder_level.algo_rating_type_id");
            query.append(" AND rr.coder_id = coder_level.coder_id");
            query.append(" AND c.round_id = rr.round_id");
            query.append(" AND rr.round_id = p.round_id");
            query.append(" AND rr.division_id = p.division_id");
            query.append(" AND p.problem_id = c.problem_id");
            query.append(" AND p.division_id = coder_level.division_id");
            query.append(" AND p.level_id = coder_level.level_id ");
            query.append(" AND c.succeeded = " + STATUS_FAILED + ")");

            query.append(" ,challenge_attempts_received = ");
            query.append(" (SELECT count(*) ");
            query.append(" FROM challenge c ");
            query.append(" ,problem p ");
            query.append(" ,room_result rr ");
            query.append(" ,round r ");
            query.append(" ,round_type_lu rt ");
            query.append(" WHERE c.defendant_id = rr.coder_id");
            query.append(" AND r.round_id = rr.round_id ");
            query.append(" AND r.round_type_id = rt.round_type_id ");
            query.append(" AND rt.algo_rating_type_id=coder_level.algo_rating_type_id");
            query.append(" AND rr.coder_id = coder_level.coder_id");
            query.append(" AND c.round_id = rr.round_id");
            query.append(" AND rr.round_id = p.round_id");
            query.append(" AND rr.division_id = p.division_id");
            query.append(" AND p.problem_id = c.problem_id");
            query.append(" AND p.division_id = coder_level.division_id");
            query.append(" AND p.level_id = coder_level.level_id) ");

            query.append(" ,challenges_received_successful = ");
            query.append(" (SELECT count(*) ");
            query.append(" FROM challenge c ");
            query.append(" ,problem p ");
            query.append(" ,room_result rr ");
            query.append(" ,round r ");
            query.append(" ,round_type_lu rt ");
            query.append(" WHERE c.defendant_id = rr.coder_id");
            query.append(" AND r.round_id = rr.round_id ");
            query.append(" AND r.round_type_id = rt.round_type_id ");
            query.append(" AND rt.algo_rating_type_id=coder_level.algo_rating_type_id");
            query.append(" AND rr.coder_id = coder_level.coder_id");
            query.append(" AND c.round_id = rr.round_id");
            query.append(" AND rr.round_id = p.round_id");
            query.append(" AND rr.division_id = p.division_id");
            query.append(" AND p.problem_id = c.problem_id");
            query.append(" AND p.division_id = coder_level.division_id");
            query.append(" AND p.level_id = coder_level.level_id ");
            query.append(" AND c.succeeded = " + STATUS_SUCCEEDED + ")");

            query.append(" ,challenges_received_failed = ");
            query.append(" (SELECT count(*) ");
            query.append(" FROM challenge c ");
            query.append(" ,problem p ");
            query.append(" ,room_result rr ");
            query.append(" ,round r ");
            query.append(" ,round_type_lu rt ");
            query.append(" WHERE c.defendant_id = rr.coder_id");
            query.append(" AND r.round_id = rr.round_id ");
            query.append(" AND r.round_type_id = rt.round_type_id ");
            query.append(" AND rt.algo_rating_type_id=coder_level.algo_rating_type_id");
            query.append(" AND rr.coder_id = coder_level.coder_id");
            query.append(" AND c.round_id = rr.round_id");
            query.append(" AND rr.round_id = p.round_id");
            query.append(" AND rr.division_id = p.division_id");
            query.append(" AND p.problem_id = c.problem_id");
            query.append(" AND p.division_id = coder_level.division_id");
            query.append(" AND p.level_id = coder_level.level_id ");
            query.append(" AND c.succeeded = " + STATUS_FAILED + ")");

            query.append(" WHERE coder_id = ?");
            query.append(" AND division_id = ?");
            query.append(" AND level_id = ?");
            query.append(" AND algo_rating_type_id = ?");

            psUpd = prepareStatement(query.toString(), TARGET_DB);

            query = new StringBuffer(100);
            query.append("DELETE FROM coder_level ");
            query.append(" WHERE coder_id = ? ");
            query.append("   AND division_id = ?");
            query.append("   AND level_id = ?");
            query.append("   AND algo_rating_type_id = ?");
            psDel = prepareStatement(query.toString(), TARGET_DB);

            // On to the load
            rs = psSel.executeQuery();

            while (rs.next()) {
                int coder_id = rs.getInt(1);
                int division_id = rs.getInt(2);
                int level_id = rs.getInt(3);
                int algoType = rs.getInt(18);

                psDel.clearParameters();
                psDel.setInt(1, coder_id);
                psDel.setInt(2, division_id);
                psDel.setInt(3, level_id);
                psDel.setInt(4, algoType);
                psDel.executeUpdate();

                psIns.clearParameters();
                psIns.setInt(1, rs.getInt(1));  // coder_id
                psIns.setInt(2, rs.getInt(2));  // division_id
                psIns.setInt(3, rs.getInt(3));  // level_id
                psIns.setInt(4, rs.getInt(4));  // problems_opened
                psIns.setInt(5, rs.getInt(5));  // problems_submitted
                psIns.setInt(6, rs.getInt(6));  // problems_correct
                psIns.setInt(7, rs.getInt(7));  // prblms_failed_by_chlnge
                psIns.setInt(8, rs.getInt(8));  // prblms_failed_by_systest
                psIns.setInt(9, rs.getInt(9));  // problems_left_open
                psIns.setFloat(10, rs.getFloat(10));  // average_points
                psIns.setFloat(11, rs.getFloat(11));  // submission_points
                psIns.setFloat(12, rs.getFloat(12));  // challenge_points
                psIns.setFloat(13, rs.getFloat(13));  // system_test_points
                psIns.setFloat(14, rs.getFloat(14));  // final_points
                psIns.setFloat(15, rs.getFloat(15));  // point_standard_deviation
                psIns.setFloat(16, rs.getFloat(16));  // defense_points
                psIns.setFloat(17, rs.getFloat(17));  // avg_time_elapsed
                psIns.setInt(18, rs.getInt(18));      // algo_rating_type_id

                retVal = psIns.executeUpdate();
                count += retVal;
                if (retVal != 1) {
                    throw new SQLException("TCLoadAggregate: Insert for " +
                            "coder_id " + coder_id +
                            ", division_id " + division_id +
                            "level_id " + level_id +
                            " modified " + retVal + " rows, not one.");
                }

                psUpd.setInt(1, coder_id);
                psUpd.setInt(2, division_id);
                psUpd.setInt(3, level_id);
                psUpd.setInt(4, algoType);

                retVal = psUpd.executeUpdate();
                if (retVal != 1) {
                    throw new SQLException("TCLoadAggregate: Update for " +
                            "coder_id " + coder_id +
                            ", division_id " + division_id +
                            "level_id " + level_id +
                            " modified " + retVal + " rows, not one.");
                }


                printLoadProgress(count, "coder_level");
            }

            log.info("Records loaded for coder_level: " + count);
        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Load of 'coder_level' table failed.\n" +
                    sqle.getMessage());
        } finally {
            close(rs);
            close(psSel);
            close(psIns);
            close(psUpd);
            close(psDel);
        }
    }

    /**
     * This method loads the 'coder_problem_summary' table
     */
    private void loadCoderProblemSummary() throws Exception {
        int retVal = 0;
        int count = 0;
        PreparedStatement psSel = null;
        PreparedStatement psIns = null;
        PreparedStatement psDel = null;
        ResultSet rs = null;
        StringBuffer query = null;

        try {
            query = new StringBuffer(100);
            query.append("SELECT rr.coder_id ");                           // 1
            query.append("       ,SUM(problems_presented) ");              // 2
            query.append("       ,SUM(problems_opened) ");                 // 3
            query.append("       ,SUM(problems_submitted) ");              // 4
            query.append("       ,SUM(problems_correct) ");                // 5
            query.append("       ,SUM(problems_failed_by_challenge) ");    // 6
            query.append("       ,SUM(problems_failed_by_system_test) ");  // 7
            query.append("       ,SUM(problems_left_open) ");              // 8
            query.append("       ,SUM(challenge_attempts_made) ");         // 9
            query.append("       ,SUM(challenges_made_successful) ");      // 10
            query.append("       ,SUM(challenges_made_failed) ");          // 11
            query.append("       ,SUM(challenge_attempts_received) ");     // 12
            query.append("       ,SUM(challenges_received_successful) ");  // 13
            query.append("       ,SUM(challenges_received_failed) ");      // 14
            query.append("       ,SUM(submission_points) ");               // 15
            query.append("       ,SUM(challenge_points) ");                // 16
            query.append("       ,SUM(system_test_points) ");              // 17
            query.append("       ,SUM(final_points) ");                    // 18
            query.append("       ,SUM(defense_points) ");                  // 19
            query.append("       ,rt.algo_rating_type_id ");               // 20
            query.append("  FROM room_result rr");
            query.append("      , round r ");
            query.append("      , round_type_lu rt ");
            query.append("      WHERE rr.round_id = r.round_id ");
            query.append("      AND r.round_type_id = rt.round_type_id ");
            query.append("      AND rt.algo_rating_type_id = " + algoType);

            if (!FULL_LOAD) {   //if it's not a full load, just load up the people that competed in the round we're loading
                query.append(" AND coder_id IN");
                query.append(" (SELECT coder_id");
                query.append(" FROM room_result");
                query.append(" WHERE attended = 'Y'");
                query.append(" AND round_id = " + fRoundId + ")");
            }
            query.append(" GROUP BY coder_id, algo_rating_type_id ");
            psSel = prepareStatement(query.toString(), SOURCE_DB);

            query = new StringBuffer(100);
            query.append("INSERT INTO coder_problem_summary ");
            query.append("      (coder_id ");                         // 1
            query.append("       ,problems_presented ");              // 2
            query.append("       ,problems_opened ");                 // 3
            query.append("       ,problems_submitted ");              // 4
            query.append("       ,problems_correct ");                // 5
            query.append("       ,problems_failed_by_challenge ");    // 6
            query.append("       ,problems_failed_by_system_test ");  // 7
            query.append("       ,problems_left_open ");              // 8
            query.append("       ,challenge_attempts_made ");         // 9
            query.append("       ,challenges_made_successful ");      // 10
            query.append("       ,challenges_made_failed ");          // 11
            query.append("       ,challenge_attempts_received ");     // 12
            query.append("       ,challenges_received_successful ");  // 13
            query.append("       ,challenges_received_failed ");      // 14
            query.append("       ,submission_points ");               // 15
            query.append("       ,challenge_points ");                // 16
            query.append("       ,system_test_points ");              // 17
            query.append("       ,final_points ");                    // 18
            query.append("       ,defense_points ");                  // 19
            query.append("       ,algo_rating_type_id) ");            // 20
            query.append("VALUES (");
            query.append("?,?,?,?,?,?,?,?,?,?,");  // 10 values
            query.append("?,?,?,?,?,?,?,?,?,?)");    // 20 total values
            psIns = prepareStatement(query.toString(), TARGET_DB);

            query = new StringBuffer(100);
            query.append("DELETE FROM coder_problem_summary ");
            query.append(" WHERE coder_id = ? AND algo_rating_type_id = ? ");
            psDel = prepareStatement(query.toString(), TARGET_DB);

            // On to the load
            rs = psSel.executeQuery();

            while (rs.next()) {
                int coder_id = rs.getInt(1);
                int algoType = rs.getInt(20);

                psDel.clearParameters();
                psDel.setInt(1, coder_id);
                psDel.setInt(2, algoType);
                psDel.executeUpdate();

                psIns.clearParameters();
                psIns.setInt(1, rs.getInt(1));  // coder_id
                psIns.setInt(2, rs.getInt(2));  // problems_presented
                psIns.setInt(3, rs.getInt(3));  // problems_opened
                psIns.setInt(4, rs.getInt(4));  // problems_submitted
                psIns.setInt(5, rs.getInt(5));  // problems_correct
                psIns.setInt(6, rs.getInt(6));  // prblms_failed_by_chlnge
                psIns.setInt(7, rs.getInt(7));  // prblms_failed_by_systest
                psIns.setInt(8, rs.getInt(8));  // problems_left_open
                psIns.setInt(9, rs.getInt(9));  // challenge_attempts_made
                psIns.setInt(10, rs.getInt(10));  // chlnges_made_successful
                psIns.setInt(11, rs.getInt(11));  // chlnges_made_failed
                psIns.setInt(12, rs.getInt(12));  // chlnge_attempts_received
                psIns.setInt(13, rs.getInt(13));  // chlnge_recvd_successfl
                psIns.setInt(14, rs.getInt(14));  // chlnge_recvd_failed
                psIns.setFloat(15, rs.getFloat(15));  // submission_points
                psIns.setFloat(16, rs.getFloat(16));  // challenge_points
                psIns.setFloat(17, rs.getFloat(17));  // system_test_points
                psIns.setFloat(18, rs.getFloat(18));  // final_points
                psIns.setFloat(19, rs.getFloat(19));  // defense_points
                psIns.setInt(20, rs.getInt(20));  // algo_rating_type_id

                retVal = psIns.executeUpdate();
                count += retVal;
                if (retVal != 1) {
                    throw new SQLException("TCLoadAggregate: Insert for " +
                            "coder_id " + coder_id +
                            " modified " + retVal + " rows, not one.");
                }

                printLoadProgress(count, "coder_problem_summary");
            }

            log.info("Records loaded for coder_problem_summary: " + count);
        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Load of 'coder_problem_summary' table failed.\n" +
                    sqle.getMessage());
        } finally {
            close(rs);
            close(psSel);
            close(psIns);
            close(psDel);
        }
    }

    /**
     * This method loads the 'streak' table
     */
    private void loadStreak() throws Exception {
        int retVal = 0;
        int count = 0;
        PreparedStatement psSel = null;
        PreparedStatement psSel2 = null;
        PreparedStatement psDel = null;
        PreparedStatement psIns = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        StringBuffer query = null;

        try {
            query = new StringBuffer(100);
            query.append("SELECT rr.coder_id ");      // 1
            query.append("       ,rr.round_id ");     // 2
            query.append("       ,rm.division_id ");  // 3
            query.append("       ,rr.room_placed ");  // 4
            query.append("       ,r.calendar_id");
            query.append("       ,r.round_id");
            query.append("  FROM room_result rr ");
            query.append("       ,room rm ");
            query.append("       ,round r ");
            query.append(" WHERE rr.room_id = rm.room_id ");
            query.append("   AND r.round_type_id = " + SINGLE_ROUND_MATCH);
            query.append("   AND r.round_id = rr.round_id ");
            query.append(" ORDER BY rr.coder_id ");
            query.append("          ,r.calendar_id asc ");
            query.append("          ,r.round_id asc ");
            psSel = prepareStatement(query.toString(), SOURCE_DB);

            query = new StringBuffer(100);
            query.append("INSERT INTO streak ");
            query.append("      (coder_id ");         // 1
            query.append("       ,streak_type_id ");  // 2
            query.append("       ,start_round_id ");  // 3
            query.append("       ,end_round_id ");    // 4
            query.append("       ,length ");          // 5
            query.append("       ,is_current) ");     // 6
            query.append("VALUES (?,?,?,?,?,?)");  // 6 total values
            psIns = prepareStatement(query.toString(), TARGET_DB);

            query = new StringBuffer(100);
            query.append("SELECT round_id FROM round ");
            query.append("WHERE calendar_id=(SELECT max(r.calendar_id) ");
            query.append("       FROM room_result rr ,round r ");
            query.append("       WHERE rr.round_id = r.round_id ");
            query.append(" AND r.round_type_id =" + SINGLE_ROUND_MATCH);
            query.append("       AND rr.coder_id = ?)        ");
            psSel2 = prepareStatement(query.toString(), TARGET_DB);

            query = new StringBuffer(100);
            query.append("DELETE FROM streak WHERE streak_type_id in (" + CONSEC_WINS_DIV1 + "," + CONSEC_WINS_DIV2 + ")");


            psDel = prepareStatement(query.toString(), TARGET_DB);

            // On to the load. First, we want to delete the whole table so
            // we can reload it.
            // Win streaks will include unrated rounds.
            psDel.executeUpdate();

            rs = psSel.executeQuery();

            int cur_division_id = -1, cur_coder_id = -1;
            int start_round_id = -1, end_round_id = -1;
            int numWins = 0;

            while (rs.next()) {
                int coder_id = rs.getInt(1);
                int round_id = rs.getInt(2);
                int division_id = rs.getInt(3);
                int room_placed = rs.getInt(4);

                // The first thing we do is check to see if we are still with
                // the same coder and in the same division. Only with those
                // constraints can a win streak continue. And then, iff room
                // placed is 1 do we continue the streak.
                if ((cur_coder_id == -1 || coder_id == cur_coder_id) &&
                        (cur_division_id == -1 || division_id == cur_division_id) &&
                        room_placed == 1) {
                    cur_coder_id = coder_id;
                    cur_division_id = division_id;
                    numWins++;
                    if (start_round_id == -1)
                        start_round_id = round_id;
                    end_round_id = round_id;
                } else if (numWins > 1) {  //we have a streak, load it up
                    int streak_type_id = -1;
                    if (cur_division_id == 1)
                        streak_type_id = CONSEC_WINS_DIV1;
                    else if (cur_division_id == 2)
                        streak_type_id = CONSEC_WINS_DIV2;
                    else
                        throw new SQLException("Unknown division_id " + cur_division_id +
                                ". Code for streak table needs to be " +
                                "modified to accomodate new division.");

                    // Get the most recent round_id information for the coder.
                    // We compare this against the ending round id for a
                    // streak. If they match, the streak is considered current.
                    psSel2.setInt(1, coder_id);
                    rs2 = psSel2.executeQuery();
                    int latest_round_id = -1;
                    if (rs2.next()) {
                        latest_round_id = rs2.getInt(1);
                    }
                    close(rs2);


                    psIns.clearParameters();
                    psIns.setInt(1, cur_coder_id);
                    psIns.setInt(2, streak_type_id);
                    psIns.setInt(3, start_round_id);
                    psIns.setInt(4, end_round_id);
                    psIns.setInt(5, numWins);
                    psIns.setInt(6, (end_round_id == latest_round_id ? 1 : 0));

                    retVal = psIns.executeUpdate();
                    count += retVal;
                    if (retVal != 1) {
                        throw new SQLException("TCLoadAggregate: Insert for " +
                                "coder_id " + coder_id +
                                ", streak_type_id " + streak_type_id +
                                " modified " + retVal + " rows, not one.");
                    }

                    printLoadProgress(count, "streak");

                    // if this record is a first place, start a new streak
                    if (room_placed == 1) {
                        cur_coder_id = coder_id;
                        cur_division_id = division_id;
                        start_round_id = round_id;
                        end_round_id = round_id;
                        numWins = 1;
                    } else {
                        cur_coder_id = -1;
                        cur_division_id = -1;
                        start_round_id = -1;
                        end_round_id = -1;
                        numWins = 0;
                    }
                } else {
                    cur_coder_id = -1;
                    cur_division_id = -1;
                    start_round_id = -1;
                    end_round_id = -1;
                    numWins = 0;
                }
            }
            log.info("Records loaded for streak: " + count);
        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Load of 'streak' table failed.\n" +
                    sqle.getMessage());
        } finally {
            close(rs);
            close(psSel);
            close(psSel2);
            close(psIns);
        }
    }


    /**
     * This method loads the 'streak' table
     * Unrated rounds are not considered.  --csj
     */
    private void loadRatingIncreaseStreak(boolean srmOnly) throws Exception {
        int retVal = 0;
        int count = 0;
        PreparedStatement psSel = null;
        PreparedStatement psSel2 = null;
        PreparedStatement psDel = null;
        PreparedStatement psIns = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        StringBuffer query = null;

        try {
            query = new StringBuffer(100);
            query.append("SELECT rr.coder_id ");      // 1
            query.append("       ,rr.round_id ");     // 2
            query.append("       ,rr.old_rating");    // 3
            query.append("       ,rr.new_rating");    // 4
            query.append("       ,r.calendar_id");    // 5
            query.append("       ,r.round_id");       // 6
            query.append("  FROM room_result rr ");
            query.append("       ,round r ");
            if (srmOnly)
                query.append(" WHERE r.round_type_id in (" + SINGLE_ROUND_MATCH + ")");
            else
                query.append(" WHERE r.round_type_id in (" + SINGLE_ROUND_MATCH + ", " + TOURNAMENT_ROUND + ", " + LONG_ROUND + ")");
            query.append("   AND r.round_id = rr.round_id ");
            query.append("   AND rr.rated_flag = 1 ");   // --csj
            query.append("   AND rr.old_rating > 0 ");   // avoid counting the first round as a rating increase.
            query.append(" ORDER BY rr.coder_id ");
            query.append("          ,r.calendar_id asc");
            query.append("          ,r.round_id asc");
            psSel = prepareStatement(query.toString(), SOURCE_DB);

            query = new StringBuffer(100);
            query.append("INSERT INTO streak ");
            query.append("      (coder_id ");         // 1
            query.append("       ,streak_type_id ");  // 2
            query.append("       ,start_round_id ");  // 3
            query.append("       ,end_round_id ");    // 4
            query.append("       ,length ");          // 5
            query.append("       ,is_current) ");     // 6
            query.append("VALUES (?,?,?,?,?,?)");  // 6 total values
            psIns = prepareStatement(query.toString(), TARGET_DB);


            query = new StringBuffer(100);
            query.append("SELECT round_id FROM round ");
            query.append("WHERE calendar_id=(SELECT max(r.calendar_id) ");
            query.append("       FROM room_result rr ,round r ");
            query.append("       WHERE rr.round_id = r.round_id ");
            if (srmOnly)
                query.append(" AND r.round_type_id in (" + SINGLE_ROUND_MATCH + ")");
            else
                query.append(" AND r.round_type_id in (" + SINGLE_ROUND_MATCH + ", " + TOURNAMENT_ROUND + ", " + LONG_ROUND + ")");

            query.append("       AND rr.coder_id = ?)        ");

            psSel2 = prepareStatement(query.toString(), TARGET_DB);

            query = new StringBuffer(100);
            if (srmOnly)
                query.append("DELETE FROM streak WHERE streak_type_id in (" + RATING_INCREASE_SRM_ONLY + ")");
            else
                query.append("DELETE FROM streak WHERE streak_type_id in (" + RATING_INCREASE + ")");

            psDel = prepareStatement(query.toString(), TARGET_DB);

            // On to the load. First, we want to delete the whole table so
            // we can reload it.
            psDel.executeUpdate();

            rs = psSel.executeQuery();

            int cur_coder_id = -1;
            int start_round_id = -1, end_round_id = -1;
            int numConsecutive = 0;

            while (rs.next()) {
                int coder_id = rs.getInt("coder_id");
                int round_id = rs.getInt("round_id");
                int oldRating = rs.getInt("old_rating");
                int newRating = rs.getInt("new_rating");

                // The first thing we do is check to see if we are still with
                // the same coder and in the same division. Only with those
                // constraints can a win streak continue. And then, iff new rating
                // is greater than old rating do we continue the streak.
                if ((cur_coder_id == -1 || coder_id == cur_coder_id) &&
                        newRating > oldRating) {
                    cur_coder_id = coder_id;
                    numConsecutive++;
                    if (start_round_id == -1)
                        start_round_id = round_id;
                    end_round_id = round_id;
                } else if (numConsecutive > 1) {  //we have a streak, load it up
                    int streak_type_id = -1;
                    streak_type_id = srmOnly ? RATING_INCREASE_SRM_ONLY : RATING_INCREASE;

                    // Get the most recent round_id information for the coder.
                    // We compare this against the ending round id for a
                    // streak. If they match, the streak is considered current.
                    psSel2.setInt(1, coder_id);
                    rs2 = psSel2.executeQuery();
                    int latest_round_id = -1;
                    if (rs2.next()) {
                        latest_round_id = rs2.getInt(1);
                    }
                    close(rs2);

                    psIns.clearParameters();
                    psIns.setInt(1, cur_coder_id);
                    psIns.setInt(2, streak_type_id);
                    psIns.setInt(3, start_round_id);
                    psIns.setInt(4, end_round_id);
                    psIns.setInt(5, numConsecutive);
                    psIns.setInt(6, (end_round_id == latest_round_id ? 1 : 0));

                    retVal = psIns.executeUpdate();
                    count += retVal;
                    if (retVal != 1) {
                        throw new SQLException("TCLoadAggregate: Insert for " +
                                "coder_id " + coder_id +
                                ", streak_type_id " + streak_type_id +
                                " modified " + retVal + " rows, not one.");
                    }

                    printLoadProgress(count, "rating increase " + (srmOnly ? "(srm only)" : "") + " streak");

                    // if this record is an increase, start a new streak
                    if (newRating > oldRating) {
                        cur_coder_id = coder_id;
                        start_round_id = round_id;
                        end_round_id = round_id;
                        numConsecutive = 1;
                    } else {
                        cur_coder_id = -1;
                        start_round_id = -1;
                        end_round_id = -1;
                        numConsecutive = 0;
                    }
                } else {
                    cur_coder_id = -1;
                    start_round_id = -1;
                    end_round_id = -1;
                    numConsecutive = 0;
                }
            }
            log.info("Records loaded for rating increase " + (srmOnly ? "(srm only)" : "") + " streak: " + count);
        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Load of 'streak' table failed.\n" +
                    sqle.getMessage());
        } finally {
            close(rs);
            close(psSel);
            close(psSel2);
            close(psIns);
        }
    }


    /**
     * This method loads the 'streak' table
     * Unrated rounds are not considered.  --csj
     */
    private void loadRatingDecreaseStreak(boolean srmOnly) throws Exception {
        int retVal = 0;
        int count = 0;
        PreparedStatement psSel = null;
        PreparedStatement psSel2 = null;
        PreparedStatement psDel = null;
        PreparedStatement psIns = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        StringBuffer query = null;

        try {
            query = new StringBuffer(100);
            query.append("SELECT rr.coder_id ");      // 1
            query.append("       ,rr.round_id ");     // 2
            query.append("       ,rr.old_rating");    // 3
            query.append("       ,rr.new_rating");    // 4
            query.append("       ,r.calendar_id");    // 5
            query.append("       ,r.round_id");       // 6
            query.append("  FROM room_result rr ");
            query.append("       ,round r ");
            if (srmOnly)
                query.append(" WHERE r.round_type_id in (" + SINGLE_ROUND_MATCH + ")");
            else
                query.append(" WHERE r.round_type_id in (" + SINGLE_ROUND_MATCH + ", " + TOURNAMENT_ROUND + ", " + LONG_ROUND + ")");
            query.append("   AND r.round_id = rr.round_id ");
            query.append("   AND rr.rated_flag = 1 ");  // --csj
            query.append(" ORDER BY rr.coder_id ");
            query.append("          ,r.calendar_id asc ");
            query.append("          ,r.round_id asc ");
            psSel = prepareStatement(query.toString(), SOURCE_DB);

            query = new StringBuffer(100);
            query.append("INSERT INTO streak ");
            query.append("      (coder_id ");         // 1
            query.append("       ,streak_type_id ");  // 2
            query.append("       ,start_round_id ");  // 3
            query.append("       ,end_round_id ");    // 4
            query.append("       ,length ");          // 5
            query.append("       ,is_current) ");     // 6
            query.append("VALUES (?,?,?,?,?,?)");  // 6 total values
            psIns = prepareStatement(query.toString(), TARGET_DB);

            query = new StringBuffer(100);
            query.append("SELECT round_id FROM round ");
            query.append("WHERE calendar_id=(SELECT max(r.calendar_id) ");
            query.append("       FROM room_result rr ,round r ");
            query.append("       WHERE rr.round_id = r.round_id ");
            if (srmOnly)
                query.append(" AND r.round_type_id in (" + SINGLE_ROUND_MATCH + ")");
            else
                query.append(" AND r.round_type_id in (" + SINGLE_ROUND_MATCH + ", " + TOURNAMENT_ROUND + ", " + LONG_ROUND + ")");
            query.append("       AND rr.coder_id = ?)        ");
            psSel2 = prepareStatement(query.toString(), TARGET_DB);

            query = new StringBuffer(100);
            if (srmOnly)
                query.append("DELETE FROM streak WHERE streak_type_id in (" + RATING_DECREASE_SRM_ONLY + ")");
            else
                query.append("DELETE FROM streak WHERE streak_type_id in (" + RATING_DECREASE + ")");

            psDel = prepareStatement(query.toString(), TARGET_DB);

            // On to the load. First, we want to delete the whole table so
            // we can reload it.
            psDel.executeUpdate();

            rs = psSel.executeQuery();

            int cur_coder_id = -1;
            int start_round_id = -1, end_round_id = -1;
            int numConsecutive = 0;

            while (rs.next()) {
                int coder_id = rs.getInt("coder_id");
                int round_id = rs.getInt("round_id");
                int oldRating = rs.getInt("old_rating");
                int newRating = rs.getInt("new_rating");

                // The first thing we do is check to see if we are still with
                // the same coder and in the same division. Only with those
                // constraints can a win streak continue. And then, iff room
                // placed is 1 do we continue the streak.
                if ((cur_coder_id == -1 || coder_id == cur_coder_id) &&
                        newRating < oldRating) {
                    cur_coder_id = coder_id;
                    numConsecutive++;
                    if (start_round_id == -1)
                        start_round_id = round_id;
                    end_round_id = round_id;
                } else if (numConsecutive > 1) {  //we have a streak, load it up
                    int streak_type_id = -1;
                    streak_type_id = srmOnly ? RATING_DECREASE_SRM_ONLY : RATING_DECREASE;

                    // Get the most recent round_id information for the coder.
                    // We compare this against the ending round id for a
                    // streak. If they match, the streak is considered current.
                    psSel2.setInt(1, coder_id);
                    rs2 = psSel2.executeQuery();
                    int latest_round_id = -1;
                    if (rs2.next()) {
                        latest_round_id = rs2.getInt(1);
                    }
                    close(rs2);

                    psIns.clearParameters();
                    psIns.setInt(1, cur_coder_id);
                    psIns.setInt(2, streak_type_id);
                    psIns.setInt(3, start_round_id);
                    psIns.setInt(4, end_round_id);
                    psIns.setInt(5, numConsecutive);
                    psIns.setInt(6, (end_round_id == latest_round_id ? 1 : 0));

                    retVal = psIns.executeUpdate();
                    count += retVal;
                    if (retVal != 1) {
                        throw new SQLException("TCLoadAggregate: Insert for " +
                                "coder_id " + coder_id +
                                ", streak_type_id " + streak_type_id +
                                " modified " + retVal + " rows, not one.");
                    }

                    printLoadProgress(count, "rating decrease " + (srmOnly ? "(srm only)" : "") + " streak");

                    // if this record is an decrease, start a new streak
                    if (newRating < oldRating) {
                        cur_coder_id = coder_id;
                        start_round_id = round_id;
                        end_round_id = round_id;
                        numConsecutive = 1;
                    } else {
                        cur_coder_id = -1;
                        start_round_id = -1;
                        end_round_id = -1;
                        numConsecutive = 0;
                    }
                } else {
                    cur_coder_id = -1;
                    start_round_id = -1;
                    end_round_id = -1;
                    numConsecutive = 0;
                }
            }
            log.info("Records loaded for rating decrease " + (srmOnly ? "(srm only)" : "") + " streak: " + count);
        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Load of 'streak' table failed.\n" +
                    sqle.getMessage());
        } finally {
            close(rs);
            close(psSel);
            close(psSel2);
            close(psIns);
        }
    }

    /**
     * This method loads the 'streak' table for consecutive appeareance in srms
     * Unrated rounds are not considered.
     */

    //todo consider changing this so that all matches that didn't "fail" count 
    private void loadConsecutiveSRMAppearances() throws Exception {
        int retVal = 0;
        int count = 0;
        PreparedStatement psSel = null;
        PreparedStatement psSel2 = null;
        PreparedStatement psDel = null;
        PreparedStatement psIns = null;
        ResultSet rs = null;
        StringBuffer query = null;

        try {
            query = new StringBuffer(100);
            query.append("SELECT rr.coder_id ");      // 1
            query.append("       ,rr.round_id ");     // 2
            query.append("       ,r.calendar_id");    // 3
            query.append("       ,rr.division_id");
            query.append("       ,rr.rated_flag");
            query.append("  FROM room_result rr ");
            query.append("       ,round r ");
            query.append(" WHERE r.round_type_id in (" + SINGLE_ROUND_MATCH + ")");
            query.append("   AND r.round_id = rr.round_id ");
            query.append("   AND rr.attended = 'Y' ");
            query.append("   AND r.rated_ind = 1 ");
            query.append(" ORDER BY rr.coder_id ");
            query.append("          ,r.calendar_id asc");
            query.append("          ,r.round_id asc");
            psSel = prepareStatement(query.toString(), SOURCE_DB);

            query = new StringBuffer(100);
            query.append("INSERT INTO streak ");
            query.append("      (coder_id ");         // 1
            query.append("       ,streak_type_id ");  // 2
            query.append("       ,start_round_id ");  // 3
            query.append("       ,end_round_id ");    // 4
            query.append("       ,length ");          // 5
            query.append("       ,is_current) ");     // 6
            query.append("VALUES (?,?,?,?,?,?)");  // 6 total values
            psIns = prepareStatement(query.toString(), TARGET_DB);


            query = new StringBuffer(100);
            query.append(" SELECT calendar_id, round_id FROM round r where round_type_id = " + SINGLE_ROUND_MATCH + " and rated_ind = 1");
            query.append("  and exists (select 1 from room_result rr where rr.round_id=r.round_id and rr.rated_flag = 1 and rr.division_id = ?) ");
            query.append("  ORDER BY calendar_id, time_id, round_id ");
            psSel2 = prepareStatement(query.toString(), SOURCE_DB);

            long div1LastRoundId = -1;
            ArrayList<Integer> div1Rounds = new ArrayList<Integer>();
            psSel2.setInt(1, 1);
            rs = psSel2.executeQuery();
            while (rs.next()) {
                div1Rounds.add(rs.getInt("calendar_id"));
                div1LastRoundId = rs.getInt("round_id"); //  the last round will be stored...nasty trick ;)
            }

            long div2LastRoundId = -1;
            ArrayList<Integer> div2Rounds = new ArrayList<Integer>();
            psSel2.clearParameters();
            psSel2.setInt(1, 2);
            close(rs);
            rs = psSel2.executeQuery();
            while (rs.next()) {
                div2Rounds.add(rs.getInt("calendar_id"));
                div2LastRoundId = rs.getInt("round_id"); //  the last round will be stored...nasty trick ;)
            }

            TreeSet<Integer> tempAllRounds = new TreeSet<Integer>();
            tempAllRounds.addAll(div1Rounds);
            tempAllRounds.addAll(div2Rounds);

            ArrayList<Integer> allRounds = new ArrayList<Integer>(tempAllRounds.size());
            allRounds.addAll(tempAllRounds);
            if (log.isDebugEnabled()) {
/*
                for (Integer i : allRounds) {
                    log.debug("i " + i);
                }
*/
                log.debug("div 2");
                for (Integer i : div2Rounds) {
                    log.debug("i " + i);
                }

            }

            query = new StringBuffer(100);
            query.append("DELETE FROM streak WHERE streak_type_id in (" + RATING_SRM_APPEARANCES + ")");

            psDel = prepareStatement(query.toString(), TARGET_DB);
            psDel.executeUpdate();

            rs = psSel.executeQuery();

            int curCoderId = -1;
            int curDivisionId = -1;
            int startRoundId = -1;
            int endRoundId = -1;
            int numConsecutive = 0;
            int roundIdx = -1;
            boolean hasNext = true;

            while (hasNext) {
                hasNext = rs.next();
                int coderId = -2;
                int roundId = -2;
                int calendarId = -2;
                int divisionId = -2;
                int ratedFlag = -2;

                if (hasNext) {
                    coderId = rs.getInt("coder_id");
                    roundId = rs.getInt("round_id");
                    calendarId = rs.getInt("calendar_id");
                    divisionId = rs.getInt("division_id");
                    ratedFlag = rs.getInt("rated_flag");
                }

                if (coderId == curCoderId && roundIdx >= 0 && allRounds.get(roundIdx) == calendarId) {
                    if (ratedFlag == 1) {
                        // if it's the same coder and he participated in the next round he is expected to, and he was rated, it's consecutive
                        numConsecutive++;
                        roundIdx++;
                        endRoundId = roundId;
                        curDivisionId = divisionId;
                    } else {
                        //if he wasn't rated, then it doesn't break his streak
                        roundIdx++;
                    }

                } else {
                    // it was not consecutive, so save the streak if needed and start a new one
                    if (numConsecutive > 1) {
                        psIns.clearParameters();
                        psIns.setInt(1, curCoderId);
                        psIns.setInt(2, RATING_SRM_APPEARANCES);
                        psIns.setInt(3, startRoundId);
                        psIns.setInt(4, endRoundId);
                        psIns.setInt(5, numConsecutive);
                        if (curDivisionId == 1) {
                            psIns.setInt(6, endRoundId == div1LastRoundId ? 1 : 0);
                        } else {
                            psIns.setInt(6, endRoundId == div2LastRoundId ? 1 : 0);
                        }

                        retVal = psIns.executeUpdate();
                        count += retVal;
                        if (retVal != 1) {
                            throw new SQLException("TCLoadAggregate: Insert for coder_id " + coderId + ", streak_type_id " + RATING_SRM_APPEARANCES +
                                    " modified " + retVal + " rows, not one.");
                        }
                        printLoadProgress(count, "Consecutive SRM appeareance streak");
                    }

                    if (hasNext) {
                        roundIdx = Collections.binarySearch(allRounds, calendarId);
                        if (roundIdx < 0) {
                            throw new Exception("Round with calendar_id=" + calendarId + " not found!");
                        }
                        roundIdx++;
                        curCoderId = coderId;
                        curDivisionId = divisionId;
                        startRoundId = roundId;
                        endRoundId = roundId;
                        numConsecutive = 1;
                    }
                }

            }
            log.info("Records loaded for Consecutive SRM appeareance streak: " + count);
        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Load of 'streak' table failed.\n" +
                    sqle.getMessage());
        } finally {
            close(rs);
            close(psSel);
            close(psSel2);
            close(psIns);
        }
    }


    /**
     * This method loads the 'room_result' table with information we
     * didn't get from the round load...namely submission_points,
     * problems_submitted
     */
    private void loadRoomResult2() throws Exception {
        int retVal = 0;
        int count = 0;
        PreparedStatement psSel = null;
        PreparedStatement psUpd = null;
        PreparedStatement psDel = null;
        ResultSet rs = null;
        StringBuffer query = null;

        try {
            // First, lets get the submission_points
            query = new StringBuffer(100);
            query.append(" SELECT cp.round_id");
            query.append(" ,cp.coder_id ");
            query.append(" ,SUM(cp.submission_points) ");
            query.append(" ,SUM(CASE WHEN cp.end_status_id >= " + STATUS_SUBMITTED + " THEN 1 ELSE 0 END)");
            query.append(" FROM coder_problem cp");
            if (!FULL_LOAD) {   //if it's not a full load, just load up the people that competed in the round we're loading
                query.append(" WHERE cp.coder_id IN");
                query.append(" (SELECT coder_id");
                query.append(" FROM room_result");
                query.append(" WHERE attended = 'Y'");
                query.append(" AND round_id = " + fRoundId + ")");
                query.append(" AND cp.round_id = " + fRoundId + " ");
            }
            query.append(" GROUP BY cp.round_id ");
            query.append(" ,cp.coder_id ");
            psSel = prepareStatement(query.toString(), SOURCE_DB);

            query = new StringBuffer(100);
            query.append("UPDATE room_result ");
            query.append("   SET submission_points = ? ");    // 1
            query.append("       ,problems_submitted = ? ");  // 2
            query.append(" WHERE round_id = ? ");
            query.append("   AND coder_id = ? ");
            psUpd = prepareStatement(query.toString(), TARGET_DB);

            // On to the load
            rs = psSel.executeQuery();

            while (rs.next()) {
                int round_id = rs.getInt(1);
                int coder_id = rs.getInt(2);

                psUpd.clearParameters();
                psUpd.setFloat(1, rs.getFloat(3));  // sum submission_points
                psUpd.setInt(2, rs.getInt(4));  // problems_submitted
                psUpd.setInt(3, round_id);
                psUpd.setInt(4, coder_id);

                retVal = psUpd.executeUpdate();
                count += retVal;
                if (retVal > 1) {
                    throw new SQLException("TCLoadAggregate: Update for " +
                            "round_id " + round_id +
                            " coder_id " + coder_id +
                            " modified more than one row.");
                }

                printLoadProgress(count, "room_result");
            }

            log.info("Records loaded for room_result " +
                    "(submission_points): " + count);
        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Load of aggregate 'room_result' table failed.\n" +
                    sqle.getMessage());
        } finally {
            close(rs);
            close(psSel);
            close(psUpd);
            close(psDel);
        }
    }

    /**
     * This method loads the 'room_result' table with information about
     * point_standard_deviation
     */
    private void loadRoomResult3() throws Exception {
        int retVal = 0;
        int count = 0;
        PreparedStatement psSel = null;
        PreparedStatement psSel2 = null;
        PreparedStatement psUpd = null;
        PreparedStatement psDel = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        StringBuffer query = null;

        try {
            query = new StringBuffer(100);
            query.append("SELECT rd.round_id ");                   // 1
            query.append("       ,rd.division_id ");               // 2
            query.append("       ,rd.point_standard_deviation ");  // 3
            query.append("       ,rd.average_points ");            // 4
            query.append("  FROM round_division rd ");
            if (!FULL_LOAD) {   //if it's not a full load, just load up the problems from this round
                query.append(" WHERE rd.round_id =" + fRoundId);
            }
            psSel = prepareStatement(query.toString(), SOURCE_DB);

            query = new StringBuffer(100);
            query.append("SELECT rr.coder_id ");                   // 1
            query.append("       ,rr.final_points ");              // 2
            query.append("  FROM room_result rr ");
            query.append(" WHERE rr.round_id = ? ");
            query.append("   AND rr.division_id = ? ");
            psSel2 = prepareStatement(query.toString(), SOURCE_DB);

            query = new StringBuffer(100);
            query.append("UPDATE room_result ");
            query.append("   SET point_standard_deviation = ? ");  // 1
            query.append(" WHERE round_id = ? ");
            query.append("   AND coder_id = ? ");
            query.append("   AND division_id = ? ");
            psUpd = prepareStatement(query.toString(), TARGET_DB);

            // On to the load
            rs = psSel.executeQuery();

            while (rs.next()) {
                int round_id = rs.getInt(1);
                int division_id = rs.getInt(2);
                float pstddev = rs.getFloat(3);
                float avgpts = rs.getFloat(4);

                psSel2.clearParameters();
                psSel2.setInt(1, round_id);
                psSel2.setInt(2, division_id);
                rs2 = psSel2.executeQuery();

                while (rs2.next()) {
                    int coder_id = rs2.getInt(1);
                    float final_points = rs2.getFloat(2);
                    float coder_pstddev = ((final_points - avgpts) / pstddev);

                    if (pstddev < 0.00001) {
                        // this should happen only if all the coders got the same points.
                        coder_pstddev = 0;
                        log.warn("pstddev too small in round " + round_id + " for coder " + coder_id);
                    }
                    psUpd.clearParameters();
                    psUpd.setFloat(1, coder_pstddev); // point_standard_deviation
                    psUpd.setInt(2, round_id);
                    psUpd.setInt(3, coder_id);
                    psUpd.setInt(4, division_id);

                    retVal = psUpd.executeUpdate();
                    count += retVal;
                    if (retVal > 1) {
                        throw new SQLException("TCLoadAggregate: Update for " +
                                "round_id " + round_id +
                                " coder_id " + coder_id +
                                " division_id " + division_id +
                                " modified more than one row.");
                    }

                    printLoadProgress(count, "room_result_pstdev");
                }

                close(rs2);
            }

            log.info("Records loaded for room_result " +
                    "(point_standard_deviation): " + count);
        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Load of aggregate 'room_result' for " +
                    "point_standard_deviation failed.\n" +
                    sqle.getMessage());
        } finally {
            close(rs);
            close(rs2);
            close(psSel);
            close(psSel2);
            close(psUpd);
            close(psDel);
        }
    }


    /**
     * This populates the 'round_problem' table
     */
    private void loadRoundProblem() throws Exception {
        int retVal = 0;
        int count = 0;
        PreparedStatement psSel = null;
        PreparedStatement psIns = null;
        PreparedStatement psDel = null;
        PreparedStatement psUpd = null;
        ResultSet rs = null;
        StringBuffer query = null;

        try {
            query = new StringBuffer(100);
            query.append("SELECT cp.round_id ");                              // 1
            query.append("       ,cp.problem_id ");                           // 2
            query.append("       ,cp.division_id ");                          // 3
            // 4: problems_opened
            query.append(" ,SUM(CASE WHEN cp.end_status_id >= " + STATUS_OPENED +
                    " THEN 1 ELSE 0 END)");
            // 5: problems_submitted
            query.append(" ,SUM(CASE WHEN cp.end_status_id >= " + STATUS_SUBMITTED +
                    " THEN 1 ELSE 0 END)");
            // 6: problems_correct
            query.append(" ,SUM(CASE WHEN cp.end_status_id = " +
                    STATUS_PASSED_SYS_TEST + " THEN 1 ELSE 0 END)  ");
            // 7: problems_failed_by_challenge
            query.append(" ,SUM(CASE WHEN cp.end_status_id = " +
                    STATUS_CHLNG_SUCCEEDED + " THEN 1 ELSE 0 END)  ");
            // 8: problems_failed_by_system_test
            query.append(" ,SUM(CASE WHEN cp.end_status_id = " +
                    STATUS_FAILED_SYS_TEST + " THEN 1 ELSE 0 END)  ");
            // 9: problems_left_open
            query.append(" ,SUM(CASE WHEN cp.end_status_id between " +
                    STATUS_OPENED + " and 121 THEN 1 ELSE 0 END)");
            // 10: submission_points
            query.append("  ,SUM(cp.submission_points) ");
            query.append(" ,SUM(cp.challenge_points)"); // 11: challenge_points
            query.append(" ,SUM(cp.system_test_points)"); // 12: system_test_points
            query.append(" ,SUM(cp.defense_points)"); // 13: defense_points
            query.append(" ,AVG(cp.final_points)"); // 14: average_points
            query.append("       ,STDEV(final_points) "); // 15: point_standard_deviation
            query.append("       ,SUM(final_points) "); // 16: final_points
            query.append(" ,(SELECT AVG(time_elapsed) ");
            query.append(" FROM coder_problem");
            query.append(" WHERE round_id = cp.round_id ");
            query.append(" and problem_id = cp.problem_id ");
            query.append(" and division_id = cp.division_id ");
            query.append(" and end_status_id = ").append(STATUS_PASSED_SYS_TEST).append(")");     //17
            query.append("  FROM coder_problem cp ");
            if (!FULL_LOAD) {   //if it's not a full load, just load up the problems from this round
                query.append(" WHERE cp.round_id =" + fRoundId);
            }
            query.append(" GROUP BY 1,2,3 ");
            psSel = prepareStatement(query.toString(), SOURCE_DB);

            query = new StringBuffer(100);
            query.append("INSERT INTO round_problem ");
            query.append("      (round_id ");                         // 1
            query.append("       ,problem_id ");                      // 2
            query.append("       ,division_id ");                     // 3
            query.append("       ,problems_opened ");                 // 4
            query.append("       ,problems_submitted ");              // 5
            query.append("       ,problems_correct ");                // 6
            query.append("       ,problems_failed_by_challenge ");    // 7
            query.append("       ,problems_failed_by_system_test ");  // 8
            query.append("       ,problems_left_open ");              // 9
            query.append("       ,submission_points ");               // 10
            query.append("       ,challenge_points ");                // 11
            query.append("       ,system_test_points ");              // 12
            query.append("       ,defense_points ");                  // 13
            query.append("       ,average_points ");                  // 14
            query.append("       ,point_standard_deviation ");        // 15
            query.append("       ,final_points ");                    // 16
            query.append("       ,avg_time_elapsed) ");               // 17
            query.append("VALUES (");
            query.append("?,?,?,?,?,?,?,?,?,?,");  // 10 values
            query.append("?,?,?,?,?,?,?)");        // 17 total values
            psIns = prepareStatement(query.toString(), TARGET_DB);

            query = new StringBuffer(100);
            query.append("DELETE FROM round_problem ");
            query.append(" WHERE round_id = ? ");
            query.append("   AND problem_id = ? ");
            query.append("   AND division_id = ? ");
            psDel = prepareStatement(query.toString(), TARGET_DB);

            query = new StringBuffer(100);
            query.append(" UPDATE round_problem ");
            query.append(" SET problems_presented = ");
            query.append(" (SELECT count(*)");
            query.append(" FROM problem p ");
            query.append(" ,room_result rr ");
            query.append(" WHERE p.round_id = rr.round_id ");
            query.append(" AND p.round_id = round_problem.round_id");
            query.append(" AND p.problem_id = round_problem.problem_id");
            query.append(" AND p.division_id = round_problem.division_id");
            query.append(" AND p.division_id = rr.division_id)");
            query.append(" ,challenge_attempts_made = ");
            query.append(" (SELECT count(*) ");
            query.append(" FROM challenge c ");
            query.append(" ,problem p ");
            query.append(" ,room_result rr ");
            query.append(" WHERE c.challenger_id = rr.coder_id");
            query.append(" AND c.round_id = rr.round_id");
            query.append(" AND rr.round_id = p.round_id");
            query.append(" AND rr.division_id = p.division_id");
            query.append(" AND p.problem_id = c.problem_id");
            query.append(" AND p.round_id = round_problem.round_id");
            query.append(" AND p.problem_id = round_problem.problem_id");
            query.append(" AND p.division_id = round_problem.division_id)");
            query.append(" ,challenges_made_failed = ");
            query.append(" (SELECT count(*) ");
            query.append(" FROM challenge c ");
            query.append(" ,problem p ");
            query.append(" ,room_result rr ");
            query.append(" WHERE c.challenger_id = rr.coder_id");
            query.append(" AND c.round_id = rr.round_id");
            query.append(" AND rr.round_id = p.round_id");
            query.append(" AND rr.division_id = p.division_id");
            query.append(" AND p.problem_id = c.problem_id");
            query.append(" AND p.division_id = round_problem.division_id");
            query.append(" AND p.problem_id = round_problem.problem_id");
            query.append(" AND p.round_id = round_problem.round_id");
            query.append(" AND c.succeeded = " + STATUS_FAILED + ")");
            query.append(" ,open_order = ");
            query.append(" (SELECT distinct open_order ");
            query.append(" FROM coder_problem cp ");
            query.append(" WHERE cp.problem_id = round_problem.problem_id ");
            query.append("   AND cp.round_id = round_problem.round_id ");
            query.append("   AND cp.division_id = round_problem.division_id) ");
            query.append(" ,level_id = ");
            query.append(" (SELECT distinct level_id ");
            query.append(" FROM coder_problem cp ");
            query.append(" WHERE cp.problem_id = round_problem.problem_id ");
            query.append("   AND cp.round_id = round_problem.round_id ");
            query.append("   AND cp.division_id = round_problem.division_id) ");
            query.append(" ,level_desc = ");
            query.append(" (SELECT distinct level_desc ");
            query.append(" FROM coder_problem cp ");
            query.append(" WHERE cp.problem_id = round_problem.problem_id ");
            query.append("   AND cp.round_id = round_problem.round_id ");
            query.append("   AND cp.division_id = round_problem.division_id) ");
            query.append(" WHERE problem_id = ? ");
            query.append("   AND round_id = ? ");
            query.append("   AND division_id = ?");
            psUpd = prepareStatement(query.toString(), TARGET_DB);

            // On to the load
            rs = psSel.executeQuery();

            while (rs.next()) {
                int round_id = rs.getInt(1);
                int problem_id = rs.getInt(2);
                int division_id = rs.getInt(3);

                psDel.clearParameters();
                psDel.setInt(1, round_id);
                psDel.setInt(2, problem_id);
                psDel.setInt(3, division_id);
                psDel.executeUpdate();

                psIns.clearParameters();
                psIns.setInt(1, round_id);  // round_id
                psIns.setInt(2, problem_id);  // problem_id
                psIns.setInt(3, division_id);  // division_id
                psIns.setInt(4, rs.getInt(4));  // problems_opened
                psIns.setInt(5, rs.getInt(5));  // problems_submitted
                psIns.setInt(6, rs.getInt(6));  // problems_correct
                psIns.setInt(7, rs.getInt(7));  // prb_failed_by_challenge
                psIns.setInt(8, rs.getInt(8));  // prb_failed_by_systemtest
                psIns.setInt(9, rs.getInt(9));  // problems_left_open
                psIns.setFloat(10, rs.getFloat(10));  // submission_points
                psIns.setFloat(11, rs.getFloat(11));  // challenge_points
                psIns.setFloat(12, rs.getFloat(12));  // system_test_points
                psIns.setFloat(13, rs.getFloat(13));  // defense_points
                psIns.setFloat(14, rs.getFloat(14));  // average_points
                psIns.setFloat(15, rs.getFloat(15));  // point_standard_dev
                psIns.setFloat(16, rs.getFloat(16));  // final_points
                psIns.setFloat(17, rs.getFloat(17));  // avg_time_elapsed

                retVal = psIns.executeUpdate();
                count += retVal;
                if (retVal != 1) {
                    throw new SQLException("TCLoadRound: Insert for round_id " +
                            round_id +
                            ", problem_id " + problem_id +
                            ", division_id " + division_id +
                            " modified " + retVal + " rows, not one.");
                }

                psUpd.clearParameters();
                psUpd.setInt(1, problem_id);      // problem_id
                psUpd.setInt(2, round_id);        // round_id
                psUpd.setInt(3, division_id);     // division_id
                psUpd.executeUpdate();

                printLoadProgress(count, "round_problem");
            }

            log.info("Round_problem records copied = " + count);
        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Load of 'round_problem' table failed.\n" +
                    sqle.getMessage());
        } finally {
            close(rs);
            close(psSel);
            close(psIns);
            close(psDel);
            close(psUpd);
        }
    }


    /**
     * This populates the 'problem_language' table
     */
    private void loadProblemLanguage() throws Exception {
        int retVal = 0;
        int count = 0;
        PreparedStatement psSel = null;
        PreparedStatement psIns = null;
        PreparedStatement psDel = null;
        PreparedStatement psUpd = null;
        ResultSet rs = null;
        StringBuffer query = null;

        try {
            query = new StringBuffer(100);
            query.append("SELECT cp.round_id ");    // 1
            query.append(" , cp.problem_id ");  // 2
            query.append(" , cp.division_id "); // 3
            query.append(" , cp.language_id "); // 4
            // 5: problems_submitted
            query.append(" ,SUM(CASE WHEN cp.end_status_id >= " + STATUS_SUBMITTED +
                    " THEN 1 ELSE 0 END)");
            // 6: problems_correct
            query.append(" ,SUM(CASE WHEN cp.end_status_id = " +
                    STATUS_PASSED_SYS_TEST + " THEN 1 ELSE 0 END)  ");
            // 7: problems_failed_by_challenge
            query.append(" ,SUM(CASE WHEN cp.end_status_id = " +
                    STATUS_CHLNG_SUCCEEDED + " THEN 1 ELSE 0 END)  ");
            // 8: problems_failed_by_system_test
            query.append(" ,SUM(CASE WHEN cp.end_status_id = " +
                    STATUS_FAILED_SYS_TEST + " THEN 1 ELSE 0 END)  ");
            query.append("  ,SUM(cp.submission_points) ");// 9: submission_points
            query.append(" ,SUM(cp.challenge_points)"); // 10: challenge_points
            query.append(" ,SUM(cp.system_test_points)"); // 11: system_test_points
            query.append(" ,SUM(cp.defense_points)"); // 12: defense_points
            query.append(" ,AVG(cp.final_points)"); // 13: average_points
            query.append(" ,STDEV(final_points) "); // 14: point_standard_deviation
            query.append(" ,SUM(final_points) "); // 15: final_points
            query.append(" ,(SELECT AVG(time_elapsed) ");
            query.append(" FROM coder_problem ");
            query.append(" WHERE round_id = cp.round_id ");
            query.append(" and problem_id = cp.problem_id ");
            query.append(" and division_id = cp.division_id ");
            query.append("and language_id = cp.language_id ");
            query.append(" and end_status_id = ").append(STATUS_PASSED_SYS_TEST).append(")");     //16"
            query.append("  FROM coder_problem cp ");
            query.append(" WHERE cp.end_status_id >= " + STATUS_SUBMITTED);
            if (!FULL_LOAD) {   //if it's not a full load, just load up the problems from this round
                query.append(" AND cp.round_id =" + fRoundId);
            }
            query.append(" GROUP BY 1,2,3,4 ");
            psSel = prepareStatement(query.toString(), SOURCE_DB);

            query = new StringBuffer(100);
            query.append("INSERT INTO problem_language ");
            query.append("      (round_id ");                         // 1
            query.append("       ,problem_id ");                      // 2
            query.append("       ,division_id ");                     // 3
            query.append("       ,language_id ");                     // 4
            query.append("       ,problems_submitted ");              // 5
            query.append("       ,problems_correct ");                // 6
            query.append("       ,problems_failed_by_challenge ");    // 7
            query.append("       ,problems_failed_by_system_test ");  // 8
            query.append("       ,submission_points ");               // 9
            query.append("       ,challenge_points ");                // 10
            query.append("       ,system_test_points ");              // 11
            query.append("       ,defense_points ");                  // 12
            query.append("       ,average_points ");                  // 13
            query.append("       ,point_standard_deviation ");        // 14
            query.append("       ,final_points ");                    // 15
            query.append("       ,avg_time_elapsed) ");               // 16
            query.append("VALUES (");
            query.append("?,?,?,?,?,?,?,?,?,?,");  // 10 values
            query.append("?,?,?,?,?,?)");        // 16 total values
            psIns = prepareStatement(query.toString(), TARGET_DB);

            query = new StringBuffer(100);
            query.append("DELETE FROM problem_language ");
            query.append(" WHERE round_id = ? ");
            query.append("   AND problem_id = ? ");
            query.append("   AND division_id = ? ");
            query.append("   AND language_id = ?");
            psDel = prepareStatement(query.toString(), TARGET_DB);

            query = new StringBuffer(100);
            query.append(" UPDATE problem_language ");
            query.append(" SET challenge_attempts_made = ");
            query.append(" (SELECT count(*)");
            query.append(" FROM challenge c");
            query.append(" ,problem p");
            query.append(" ,coder_problem cp");
            query.append(" WHERE p.problem_id = c.problem_id");
            query.append(" AND p.problem_id = cp.problem_id");
            query.append(" and p.division_id = cp.division_id");
            query.append(" and c.defendant_id = cp.coder_id");
            query.append(" and c.round_id = cp.round_id");
            query.append(" and cp.round_id = p.round_id");
            query.append(" AND cp.language_id = problem_language.language_id");
            query.append(" AND p.division_id = problem_language.division_id");
            query.append(" AND p.problem_id = problem_language.problem_id");
            query.append(" and p.round_id = problem_language.round_id)");
            query.append(" ,challenges_made_failed = ");
            query.append(" (SELECT count(*) ");
            query.append(" FROM challenge c");
            query.append(" ,problem p");
            query.append(" ,coder_problem cp");
            query.append(" WHERE p.problem_id = c.problem_id");
            query.append(" AND p.problem_id = cp.problem_id");
            query.append(" and p.division_id = cp.division_id");
            query.append(" and c.defendant_id = cp.coder_id");
            query.append(" AND c.round_id = cp.round_id");
            query.append(" and cp.round_id = p.round_id");
            query.append(" AND cp.language_id = problem_language.language_id");
            query.append(" AND p.division_id = problem_language.division_id");
            query.append(" AND p.problem_id = problem_language.problem_id");
            query.append(" and p.round_id = problem_language.round_id");
            query.append(" AND c.succeeded = " + STATUS_FAILED + ")");
            query.append(" ,open_order = ");
            query.append(" (SELECT distinct open_order ");
            query.append(" FROM coder_problem cp ");
            query.append(" WHERE cp.problem_id = problem_language.problem_id ");
            query.append("   AND cp.round_id = problem_language.round_id ");
            query.append("   AND cp.division_id = problem_language.division_id) ");
            query.append(" ,level_id = ");
            query.append(" (SELECT distinct level_id ");
            query.append(" FROM coder_problem cp ");
            query.append(" WHERE cp.problem_id = problem_language.problem_id ");
            query.append("   AND cp.round_id = problem_language.round_id ");
            query.append("   AND cp.division_id = problem_language.division_id) ");
            query.append(" ,level_desc = ");
            query.append(" (SELECT distinct level_desc ");
            query.append(" FROM coder_problem cp ");
            query.append(" WHERE cp.problem_id = problem_language.problem_id ");
            query.append("   AND cp.round_id = problem_language.round_id ");
            query.append("   AND cp.division_id = problem_language.division_id) ");
            query.append(" WHERE problem_id = ? ");
            query.append("   AND round_id = ? ");
            query.append("   AND division_id = ?");
            query.append("   AND language_id = ?");
            psUpd = prepareStatement(query.toString(), TARGET_DB);

            // On to the load
            rs = psSel.executeQuery();

            while (rs.next()) {
                int round_id = rs.getInt(1);
                int problem_id = rs.getInt(2);
                int division_id = rs.getInt(3);
                int language_id = rs.getInt(4);

                psDel.clearParameters();
                psDel.setInt(1, round_id);
                psDel.setInt(2, problem_id);
                psDel.setInt(3, division_id);
                psDel.setInt(4, language_id);
                psDel.executeUpdate();

                psIns.clearParameters();
                psIns.setInt(1, round_id);  // round_id
                psIns.setInt(2, problem_id);  // problem_id
                psIns.setInt(3, division_id);  // division_id
                psIns.setInt(4, language_id);  // language_id
                psIns.setInt(5, rs.getInt(5));  // problems_submitted
                psIns.setInt(6, rs.getInt(6));  // problems_correct
                psIns.setInt(7, rs.getInt(7));  // prb_failed_by_challenge
                psIns.setInt(8, rs.getInt(8));  // prb_failed_by_systemtest
                psIns.setFloat(9, rs.getFloat(9));   // submission_points
                psIns.setFloat(10, rs.getFloat(10)); // challenge_points
                psIns.setFloat(11, rs.getFloat(11)); // system_test_points
                psIns.setFloat(12, rs.getFloat(12)); // defense_points
                psIns.setFloat(13, rs.getFloat(13)); // average_points
                psIns.setFloat(14, rs.getFloat(14)); // point_standard_dev
                psIns.setFloat(15, rs.getFloat(15)); // final_points
                psIns.setFloat(16, rs.getFloat(16)); // avg_time_elapsed

                retVal = psIns.executeUpdate();
                count += retVal;
                if (retVal != 1) {
                    throw new SQLException("TCLoadRound: Insert for round_id " +
                            round_id +
                            ", problem_id " + problem_id +
                            ", division_id " + division_id +
                            " modified " + retVal + " rows, not one.");
                }

                psUpd.clearParameters();
                psUpd.setInt(1, problem_id);      // problem_id
                psUpd.setInt(2, round_id);        // round_id
                psUpd.setInt(3, division_id);     // division_id
                psUpd.setInt(4, language_id);
                psUpd.executeUpdate();

                printLoadProgress(count, "problem_language");
            }

            log.info("problem_language records copied = " + count);
        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Load of 'problem_language' table failed.\n" +
                    sqle.getMessage());
        } finally {
            close(rs);
            close(psSel);
            close(psIns);
            close(psDel);
            close(psUpd);
        }
    }


    private static final String PROBLEM_QUERY =
            " select problem_id, division_id" +
                    " from problem" +
                    " where round_id = ?";


    private static final String POINT_QUERY =
            " select cp.coder_id, cp.round_id, cp.division_id, cp.problem_id, cp.time_elapsed" +
                    " from coder_problem cp, coder c" +
                    " where cp.round_id = ?" +
                    " and cp.problem_id = ?" +
                    " and cp.division_id = ?" +
                    " and cp.end_status_id = 150 " +
                    " and cp.coder_id = c.coder_id " +
                    " and c.status = 'A' " +
                    " order by cp.time_elapsed asc";

    private static final String LANGUAGE_POINT_QUERY =
            " select cp.coder_id, cp.round_id, cp.division_id, cp.problem_id, cp.time_elapsed" +
                    " from coder_problem cp, coder c " +
                    " where cp.round_id = ?" +
                    " and cp.problem_id = ?" +
                    " and cp.division_id = ?" +
                    " and cp.language_id = ?" +
                    " and cp.coder_id = c.coder_id " +
                    " and c.status = 'A' " +
                    " and cp.end_status_id = 150 " +
                    " order by cp.time_elapsed asc";

    private static final String LANGUAGE_QUERY = "select distinct language_id " +
            "from coder_problem " +
            "where round_id = ? and language_id >0";
    private static final String PROBLEM_RANK_UPDATE =
            "update coder_problem set placed = ? " +
                    " where round_id = ? and coder_id = ? and division_id = ? and problem_id = ?";

    private static final String LANGUAGE_PROBLEM_RANK_UPDATE =
            "update coder_problem set language_placed = ? " +
                    " where round_id = ? and coder_id = ? and division_id = ? and problem_id = ?";

    private static final String ROUNDS = "select round_id from round r, round_type_lu rt " +
            "where r.round_type_id = rt.round_type_id " +
            "and rt.algo_rating_type_id in ( " + TC_RATING_TYPE_ID + ", " + HS_RATING_TYPE_ID + ")";

    private static final String DELETE = "update coder_problem set placed = null, language_placed = null where round_id = ? ";

    /**
     * This populates the 'coder_problem' table
     */
    private void loadCoderProblem() throws Exception {
        int retVal;
        int count = 0;
        PreparedStatement psSelProblem = null;
        PreparedStatement psSelPoint = null;
        PreparedStatement psUpd = null;
        PreparedStatement psSelLang = null;
        PreparedStatement psSelLangPoint = null;
        PreparedStatement delete = null;
        ResultSet rs = null;
        ResultSet rsLang = null;
        ResultSet rsLangPoint = null;
        PreparedStatement psUpdLang = null;
        PreparedStatement psSelRounds = null;
        ResultSet roundRs = null;
        ResultSet problemRs = null;
        ResultSet pointRs = null;

        try {

            ArrayList rounds = new ArrayList();
            if (FULL_LOAD) {
                //get rounds
                psSelRounds = prepareStatement(ROUNDS, TARGET_DB);
                roundRs = psSelRounds.executeQuery();
                while (roundRs.next()) {
                    rounds.add(new Long(roundRs.getLong("round_id")));
                }
            } else {
                rounds.add(new Long(fRoundId));
            }

            long roundId;

            psSelProblem = prepareStatement(PROBLEM_QUERY, TARGET_DB);
            psSelPoint = prepareStatement(POINT_QUERY, TARGET_DB);
            psUpd = prepareStatement(PROBLEM_RANK_UPDATE, TARGET_DB);
            psSelLang = prepareStatement(LANGUAGE_QUERY, TARGET_DB);
            psSelLangPoint = prepareStatement(LANGUAGE_POINT_QUERY, TARGET_DB);
            psUpdLang = prepareStatement(LANGUAGE_PROBLEM_RANK_UPDATE, TARGET_DB);
            delete = prepareStatement(DELETE, TARGET_DB);


            for (Iterator roundIterator = rounds.iterator(); roundIterator.hasNext();) {
                roundId = ((Long) roundIterator.next()).longValue();

                delete.clearParameters();
                delete.setLong(1, roundId);
                delete.executeUpdate();

                psSelProblem.clearParameters();
                psSelProblem.setLong(1, roundId);

                problemRs = psSelProblem.executeQuery();

                ResultSetContainer problems = new ResultSetContainer(problemRs);

                problemRs.close();

                ResultSetContainer rankList;
                ResultSetContainer.ResultSetRow row;
                ResultSetContainer.ResultSetRow innerRow;
                for (Iterator it = problems.iterator(); it.hasNext();) {
                    row = (ResultSetContainer.ResultSetRow) it.next();
                    psSelPoint.clearParameters();
                    psSelPoint.setLong(1, roundId);
                    psSelPoint.setLong(2, row.getLongItem("problem_id"));
                    psSelPoint.setInt(3, row.getIntItem("division_id"));
                    pointRs = psSelPoint.executeQuery();
                    rankList = new ResultSetContainer(pointRs, 0, Integer.MAX_VALUE, 5);
                    pointRs.close();

                    for (Iterator it1 = rankList.iterator(); it1.hasNext();) {
                        innerRow = (ResultSetContainer.ResultSetRow) it1.next();
                        psUpd.clearParameters();
                        psUpd.setInt(1, innerRow.getIntItem("rank"));
                        psUpd.setLong(2, roundId);
                        psUpd.setLong(3, innerRow.getLongItem("coder_id"));
                        psUpd.setInt(4, innerRow.getIntItem("division_id"));
                        psUpd.setLong(5, innerRow.getLongItem("problem_id"));
                        retVal = psUpd.executeUpdate();
                        count += retVal;
                        if (retVal != 1) {
                            throw new SQLException("TCLoadAggregate: Update for round_id " +
                                    roundId +
                                    ", problem_id " + innerRow.getLongItem("round_id") +
                                    ", division_id " + innerRow.getLongItem("division_id") +
                                    ", coder" + innerRow.getLongItem("coder_id") +
                                    " modified " + retVal + " rows, not one.");
                        }
                        printLoadProgress(count, "coder_problem");
                    }
                }

                //for each lanuage
                psSelLang.clearParameters();
                psSelLang.setLong(1, roundId);

                rsLang = psSelLang.executeQuery();

                ResultSetContainer languages = new ResultSetContainer(rsLang);
                rsLang.close();

                ResultSetContainer langProblemRankList;
                ResultSetContainer.ResultSetRow probLangRow;
                ResultSetContainer.ResultSetRow langProblemInnerRow;
                ResultSetContainer.ResultSetRow langRow;

                //for each language and problem, rank the scores
                for (Iterator it = languages.iterator(); it.hasNext();) {
                    langRow = (ResultSetContainer.ResultSetRow) it.next();
                    for (Iterator it1 = problems.iterator(); it1.hasNext();) {
                        probLangRow = (ResultSetContainer.ResultSetRow) it1.next();
                        psSelLangPoint.clearParameters();
                        psSelLangPoint.setLong(1, roundId);
                        psSelLangPoint.setLong(2, probLangRow.getLongItem("problem_id"));
                        psSelLangPoint.setInt(3, probLangRow.getIntItem("division_id"));
                        psSelLangPoint.setInt(4, langRow.getIntItem("language_id"));
                        rsLangPoint = psSelLangPoint.executeQuery();
                        langProblemRankList = new ResultSetContainer(rsLangPoint, 0, Integer.MAX_VALUE, 5);

                        for (Iterator it2 = langProblemRankList.iterator(); it2.hasNext();) {
                            langProblemInnerRow = (ResultSetContainer.ResultSetRow) it2.next();
                            psUpdLang.clearParameters();
                            psUpdLang.setInt(1, langProblemInnerRow.getIntItem("rank"));
                            psUpdLang.setLong(2, roundId);
                            psUpdLang.setLong(3, langProblemInnerRow.getLongItem("coder_id"));
                            psUpdLang.setInt(4, langProblemInnerRow.getIntItem("division_id"));
                            psUpdLang.setLong(5, langProblemInnerRow.getLongItem("problem_id"));
                            retVal = psUpdLang.executeUpdate();
                            count += retVal;
                            if (retVal != 1) {
                                throw new SQLException("TCLoadAggregate: Update for round_id " +
                                        roundId +
                                        ", problem_id " + langProblemInnerRow.getLongItem("round_id") +
                                        ", division_id " + langProblemInnerRow.getLongItem("division_id") +
                                        ", coder" + langProblemInnerRow.getLongItem("coder_id") +
                                        " modified " + retVal + " rows, not one.");
                            }
                            printLoadProgress(count, "coder_problem");
                        }
                    }
                }
            }
            log.info("coder_problem records copied = " + count);
        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Load of 'coder_problem' table failed.\n" +
                    sqle.getMessage());
        } finally {
            close(psSelProblem);
            close(psSelPoint);
            close(psUpd);
            close(psSelLang);
            close(psSelLangPoint);
            close(rs);
            close(rsLang);
            close(rsLangPoint);
            close(psUpdLang);
            close(psSelRounds);
            close(roundRs);
            close(problemRs);
            close(pointRs);
        }
    }


    /**
     * This method loads the 'team_round' table
     */
    private void loadTeamRound() throws Exception {
        int retVal = 0;
        int count = 0;
        PreparedStatement psSel = null;
        PreparedStatement psIns = null;
        PreparedStatement psDel = null;
        ResultSet rs = null;
        StringBuffer query = null;

        try {
            query = new StringBuffer(100);
            query.append("SELECT round_id ");                              // 1
            query.append("       ,team_id ");                              // 2
            query.append("       ,SUM(problems_presented) ");              // 3
            query.append("       ,SUM(problems_opened) ");                 // 4
            query.append("       ,SUM(problems_submitted) ");              // 5
            query.append("       ,SUM(problems_correct) ");                // 6
            query.append("       ,SUM(problems_failed_by_challenge) ");    // 7
            query.append("       ,SUM(problems_failed_by_system_test) ");  // 8
            query.append("       ,SUM(problems_left_open) ");              // 9
            query.append("       ,SUM(challenge_attempts_made) ");         // 10
            query.append("       ,SUM(challenges_made_successful) ");      // 11
            query.append("       ,SUM(challenges_made_failed) ");          // 12
            query.append("       ,SUM(challenge_attempts_received) ");     // 13
            query.append("       ,SUM(challenges_received_successful) ");  // 14
            query.append("       ,SUM(challenges_received_failed) ");      // 15
            query.append("       ,SUM(submission_points) ");               // 16
            query.append("       ,SUM(challenge_points) ");                // 17
            query.append("       ,SUM(system_test_points) ");              // 18
            query.append("       ,SUM(final_points) ");                    // 19
            query.append("       ,SUM(defense_points) ");                  // 20
            query.append("       ,AVG(final_points) ");                    // 21
            query.append("       ,STDEV(final_points) ");                  // 22
            query.append("       ,COUNT(coder_id) ");                      // 23
            query.append("       ,SUM(team_points) team_points");          // 24
            query.append("  FROM room_result ");
            query.append("  WHERE team_id is not null ");
            if (!FULL_LOAD) {   //if it's not a full load, just load up the people that competed in the round we're loading
                query.append(" AND round_id = " + fRoundId);
            }
            query.append(" GROUP BY round_id ");
            query.append("          ,team_id");
            query.append(" ORDER BY round_id, team_points ");
            psSel = prepareStatement(query.toString(), SOURCE_DB);


            query = new StringBuffer(100);
            query.append("INSERT INTO team_round ");
            query.append("      (round_id ");                         // 1
            query.append("       ,team_id ");                         // 2
            query.append("       ,problems_presented ");              // 3
            query.append("       ,problems_opened ");                 // 4
            query.append("       ,problems_submitted ");              // 5
            query.append("       ,problems_correct ");                // 6
            query.append("       ,problems_failed_by_challenge ");    // 7
            query.append("       ,problems_failed_by_system_test ");  // 8
            query.append("       ,problems_left_open ");              // 9
            query.append("       ,challenge_attempts_made ");         // 10
            query.append("       ,challenges_made_successful ");      // 11
            query.append("       ,challenges_made_failed ");          // 12
            query.append("       ,challenge_attempts_received ");     // 13
            query.append("       ,challenges_received_successful ");  // 14
            query.append("       ,challenges_received_failed ");      // 15
            query.append("       ,submission_points ");               // 16
            query.append("       ,challenge_points ");                // 17
            query.append("       ,system_test_points ");              // 18
            query.append("       ,final_points ");                    // 19
            query.append("       ,defense_points  ");                 // 20
            query.append("       ,average_points ");                  // 21
            query.append("       ,point_standard_deviation ");        // 22
            query.append("       ,num_coders ");                      // 23
            query.append("       ,team_points ");                     // 24
            query.append("       ,team_placed) ");                     // 25
            query.append("VALUES (");
            query.append("?,?,?,?,?,?,?,?,?,?,");  // 10 values
            query.append("?,?,?,?,?,?,?,?,?,?,");  // 20 values
            query.append("?,?,?,?,?)");            // 25 total values
            psIns = prepareStatement(query.toString(), TARGET_DB);

            query = new StringBuffer(100);
            query.append("DELETE FROM team_round ");
            if (!FULL_LOAD) {
                query.append(" WHERE round_id = ?");
            }
            psDel = prepareStatement(query.toString(), TARGET_DB);
            if (!FULL_LOAD) {
                psDel.setInt(1, fRoundId);
            }
            psDel.executeUpdate();

            // On to the load
            rs = psSel.executeQuery();

            int placed = 1;
            int placedNoTie = 1;
            int previousRound = -1;
            int previousPoints = -1;

            while (rs.next()) {
                int round_id = rs.getInt(1);
                int team_id = rs.getInt(2);
                int points = rs.getInt(24);
                boolean nullPoints = rs.getString(24) == null;

                // when starting with another round, start again with placed
                if (round_id != previousRound) {
                    placedNoTie = 1;
                    previousPoints = -1;
                }

                if (!nullPoints && (previousPoints != points)) {
                    placed = placedNoTie;
                }

                previousRound = round_id;

                if (!nullPoints) {
                    previousPoints = points;
                    placedNoTie++;
                }

                psIns.clearParameters();
                psIns.setInt(1, rs.getInt(1));  // coder_id
                psIns.setInt(2, rs.getInt(2));  // division_id
                psIns.setInt(3, rs.getInt(3));  // problems_presented
                psIns.setInt(4, rs.getInt(4));  // problems_opened
                psIns.setInt(5, rs.getInt(5));  // problems_submitted
                psIns.setInt(6, rs.getInt(6));  // problems_correct
                psIns.setInt(7, rs.getInt(7));  // prblms_failed_by_systest
                psIns.setInt(8, rs.getInt(8));  // prblms_failed_by_chlnge
                psIns.setInt(9, rs.getInt(9));  // problems_left_open
                psIns.setInt(10, rs.getInt(10));  // challenge_attempts_made
                psIns.setInt(11, rs.getInt(11));  // chlnges_made_successful
                psIns.setInt(12, rs.getInt(12));  // chlnges_made_failed
                psIns.setInt(13, rs.getInt(13));  // chlnge_attempts_received
                psIns.setInt(14, rs.getInt(14));  // chlnge_recvd_successfl
                psIns.setInt(15, rs.getInt(15));  // chlnge_recvd_failed
                psIns.setFloat(16, rs.getFloat(16));  // submission_points
                psIns.setFloat(17, rs.getFloat(17));  // challenge_points
                psIns.setFloat(18, rs.getFloat(18));  // system_test_points
                psIns.setFloat(19, rs.getFloat(19));  // final_points
                psIns.setFloat(20, rs.getFloat(20));  // defense_points
                psIns.setFloat(21, rs.getFloat(21));  // average_points
                psIns.setFloat(22, rs.getFloat(22));  // point_standard_deviation
                psIns.setInt(23, rs.getInt(23));  // num_coders
                if (nullPoints) {
                    psIns.setNull(24, java.sql.Types.DECIMAL);  // team_points
                    psIns.setNull(25, java.sql.Types.DECIMAL);  // placed
                } else {
                    psIns.setInt(24, rs.getInt(24));  // team_points
                    psIns.setInt(25, placed);  // placed
                }


                retVal = psIns.executeUpdate();
                count += retVal;
                if (retVal != 1) {
                    throw new SQLException("TCLoadAggregate: Insert for " +
                            "round " + round_id +
                            ", team_id " + team_id +
                            " modified " + retVal + " rows, not one.");
                }

                printLoadProgress(count, "team_round");
            }

            log.info("Records loaded for team_round: " + count);
        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Load of 'team_round' table failed.\n" +
                    sqle.getMessage());
        } finally {
            close(rs);
            close(psSel);
            close(psIns);
            close(psDel);
        }
    }


}
