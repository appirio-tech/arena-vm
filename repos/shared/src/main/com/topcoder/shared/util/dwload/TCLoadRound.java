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

public class TCLoadRound extends TCLoad {
    private static Logger log = Logger.getLogger(TCLoadRound.class);
    protected java.sql.Timestamp fStartTime = null;
    protected java.sql.Timestamp fLastLogTime = null;

    // The following set of variables are all configureable from the command
    // line by specifying -variable (where the variable is after the //)
    // followed by the new value
    private int fRoundId = -1;                 // roundid
    private int STATUS_FAILED = 0;    // failed
    private int STATUS_SUCCEEDED = 1;    // succeeded
    private int CODING_SEGMENT_ID = 2;    // codingseg
    private int STATUS_OPENED = 120;  // opened
    private int STATUS_PASSED_SYS_TEST = 150;  // passsystest
    private int STATUS_FAILED_SYS_TEST = 160;  // failsystest
    private int CONTEST_ROOM = 2;    // contestroom
    private int ROUND_LOG_TYPE = 1;    // roundlogtype
    private int CHALLENGE_NULLIFIED = 92;   // challengenullified
    private boolean FULL_LOAD = false;//fullload

    private static int PROBLEM_WRITER_USER_TYPE_ID = 5;
    private static int PROBLEM_TESTER_USER_TYPE_ID = 6;
    /**
     * This Hashtable stores the start date of a particular round so
     * that we don't have to look it up each time.
     */
    private Hashtable fRoundStartHT = new Hashtable();

    /**
     * Constructor. Set our usage message here.
     */
    public TCLoadRound() {
        DEBUG = false;

        USAGE_MESSAGE = "TCLoadRound parameters - defaults in ():\n" +
                "  -roundid number       : Round ID to load\n" +
                "  [-failed number]      : Failed status for succeeded column    (0)\n" +
                "  [-succeeded number]   : Succeeded status for succeeded column (1)\n" +
                "  [-codingseg number]   : ID for beginning of coding segment    (2)\n" +
                "  [-opened number]      : Problem_status of opened              (120)\n" +
                "  [-passsystest number] : Problem_status of passed system test  (150)\n" +
                "  [-failsystest number] : Problem_status of failed system test  (160)\n" +
                "  [-contestroom number] : Type id for contest rooms             (2)\n" +
                "  [-roundlogtype number] : Log type id for this load            (1)\n" +
                "  [-challengenullified number] : id for nullified challenges    (2)\n" +
                "  [-fullload boolean] : true-clean round load, false-selective  (false)\n";
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

            tmp = retrieveIntParam("codingseg", params, true, true);
            if (tmp != null) {
                CODING_SEGMENT_ID = tmp.intValue();
                log.info("New coding segment id is " + CODING_SEGMENT_ID);
            }

            tmp = retrieveIntParam("opened", params, true, true);
            if (tmp != null) {
                STATUS_OPENED = tmp.intValue();
                log.info("New opened is " + STATUS_OPENED);
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

            tmp = retrieveIntParam("contestroom", params, true, true);
            if (tmp != null) {
                CONTEST_ROOM = tmp.intValue();
                log.info("New contestroom id is " + CONTEST_ROOM);
            }

            tmp = retrieveIntParam("roundlogtype", params, true, true);
            if (tmp != null) {
                ROUND_LOG_TYPE = tmp.intValue();
                log.info("New roundlogtype is " + ROUND_LOG_TYPE);
            }

            tmp = retrieveIntParam("challengenullified", params, true, true);
            if (tmp != null) {
                CHALLENGE_NULLIFIED = tmp.intValue();
                log.info("New challengenullified id is " + CHALLENGE_NULLIFIED);
            }

            tmpBool = retrieveBooleanParam("fullload", params, true);
            if (tmpBool != null) {
                FULL_LOAD = tmpBool.booleanValue();
                log.info("New fullload flag is " + FULL_LOAD);
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
            log.info("Loading round: " + fRoundId);

            fStartTime = new java.sql.Timestamp(System.currentTimeMillis());

            getLastUpdateTime();

            clearRound();

            loadSeasons();

            loadContest();

            loadRound();

            loadProblem();

            loadProblemCategory();

            loadProblemSubmission();

            loadSystemTestCase();

            loadSystemTestResult();

            loadRoom();

            loadRoomResult();

            loadRating();

            loadSeasonRating();

            loadCoderProblem();

            loadChallenge();

            loadProblemAuthors();

            setLastUpdateTime();

            log.info("SUCCESS: Round " + fRoundId +
                    " load ran successfully.");
        } catch (Exception ex) {
            setReasonFailed(ex.getMessage());
            throw ex;
        }
    }

    private void clearRound() throws Exception {
        PreparedStatement ps = null;
        ArrayList a = null;

        try {
            a = new ArrayList();

            int algoType = getRoundType(fRoundId);

            if (FULL_LOAD) {
                String divs = algoType == 1 ? "1,2" : "-1";
                a.add("DELETE FROM coder_level where algo_rating_type_id=" + algoType);
                a.add("DELETE FROM coder_division where division_id in (" + divs + ")");
                a.add("DELETE FROM room_result WHERE round_id = ?");
                a.add("DELETE FROM round_division where round_id=?");
                a.add("DELETE FROM coder_problem_summary where algo_rating_type_id=" + algoType);
                a.add("DELETE FROM system_test_case WHERE problem_id in (SELECT problem_id FROM round_problem WHERE round_id = ?)");
                a.add("DELETE FROM round_problem");
                a.add("DELETE FROM problem_language");
                a.add("DELETE FROM challenge WHERE round_id = ?");
                a.add("DELETE FROM coder_problem WHERE round_id = ?");
                a.add("DELETE FROM room WHERE round_id = ?");
                a.add("DELETE FROM system_test_result WHERE round_id = ?");
                a.add("DELETE FROM problem_submission WHERE round_id = ?");
                a.add("DELETE FROM problem_category_xref where problem_id in (select problem_id from problem where round_id = ?)");
                a.add("DELETE FROM problem WHERE round_id = ?");
                a.add("UPDATE algo_rating SET first_rated_round_id = null WHERE first_rated_round_id = ?");
                a.add("UPDATE algo_rating SET last_rated_round_id = null WHERE last_rated_round_id = ?");
            } else {
                a.add("DELETE FROM coder_level WHERE coder_id IN (SELECT coder_id FROM room_result WHERE attended = 'Y' AND round_id = ?) AND algo_rating_type_id=" + algoType);
                a.add("DELETE FROM coder_division WHERE coder_id IN (SELECT coder_id FROM room_result WHERE attended = 'Y' AND round_id = ?)");
                a.add("DELETE FROM coder_problem_summary WHERE coder_id IN (SELECT coder_id FROM room_result WHERE attended = 'Y' AND round_id = ?) AND algo_rating_type_id=" + algoType);
                a.add("DELETE FROM room_result WHERE round_id = ?");
                a.add("DELETE FROM round_division WHERE round_id = ?");
                a.add("DELETE FROM system_test_case WHERE problem_id in (SELECT problem_id FROM round_problem WHERE round_id = ?)");
                a.add("DELETE FROM round_problem WHERE round_id = ?");
                a.add("DELETE FROM problem_language WHERE round_id = ?");
                a.add("DELETE FROM challenge WHERE round_id = ?");
                a.add("DELETE FROM coder_problem WHERE round_id = ?");
                a.add("DELETE FROM system_test_result WHERE round_id = ?");
                a.add("DELETE FROM problem_submission WHERE round_id = ?");
                a.add("DELETE FROM problem_category_xref where problem_id in (select problem_id from problem where round_id = ?)");
                a.add("DELETE FROM problem WHERE round_id = ?");
                a.add("UPDATE algo_rating SET first_rated_round_id = null WHERE first_rated_round_id = ?");
                a.add("UPDATE algo_rating SET last_rated_round_id = null WHERE last_rated_round_id = ?");
            }

            int count = 0;
            for (int i = 0; i < a.size(); i++) {
                ps = prepareStatement((String) a.get(i), TARGET_DB);
                if (((String) a.get(i)).indexOf('?') > -1)
                    ps.setInt(1, fRoundId);
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


    private void getLastUpdateTime() throws Exception {
        Statement stmt = null;
        ResultSet rs = null;
        StringBuffer query = null;

        try {
            query = new StringBuffer(100);
            query.append("select timestamp from update_log where log_id = ");
            query.append("(select max(log_id) from update_log where log_type_id = " + ROUND_LOG_TYPE + ")");
            stmt = createStatement(TARGET_DB);
            rs = stmt.executeQuery(query.toString());
            if (rs.next()) {
                fLastLogTime = rs.getTimestamp(1);
            } else {
                throw new SQLException("Last log time not found in update_log table");
            }
        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Failed to retrieve last log time.\n" +
                    sqle.getMessage());
        } finally {
            close(rs);
            close(stmt);
        }
    }

    /**
     * Here we want to load any new rating information into the rating table.
     */
    private void loadRating() throws Exception {
        int count = 0;
        int retVal = 0;
        int algoType = 0;
        PreparedStatement psSel = null;
        PreparedStatement psRatedRound = null;
        PreparedStatement psSelNumCompetitions = null;
        PreparedStatement psSelRatedRounds = null;
        PreparedStatement psSelMinMaxRatings = null;
        PreparedStatement psUpd = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        StringBuffer query = null;

        try {
            algoType = getRoundType(fRoundId);

            query = new StringBuffer(100);
            query.append(" SELECT 1 FROM round");
            query.append(" WHERE round_id = " + fRoundId);
            query.append(" AND rated_ind = 1");
            psRatedRound = prepareStatement(query.toString(), SOURCE_DB);
            rs = psRatedRound.executeQuery();

            if (!rs.next()) {
            	log.info("Not loading rating, since the round is not rated");
            	return;
            }

            // Get all the coders that participated in this round
            query = new StringBuffer(100);
            query.append("SELECT rr.coder_id ");    // 1
            query.append("  FROM room_result rr ");
            query.append(" WHERE rr.round_id = ? ");
            query.append("   AND rr.attended = 'Y' ");
            query.append("   AND NOT EXISTS ");
            query.append("       (SELECT 'pops' ");
            query.append("          FROM user_group_xref ugx ");
            query.append("         WHERE ugx.login_id= rr.coder_id ");
            query.append("           AND ugx.group_id = 2000115)");
            query.append("   AND NOT EXISTS ");
            query.append("       (SELECT 'pops' ");
            query.append("          FROM group_user gu ");
            query.append("         WHERE gu.user_id = rr.coder_id ");
            query.append("           AND gu.group_id = 13)");
            query.append("           AND rr.rated_flag = 1");

            psSel = prepareStatement(query.toString(), SOURCE_DB);

            query = new StringBuffer(100);
            query.append("SELECT min(rr.new_rating) ");  // 1
            query.append("       ,max(rr.new_rating) "); // 2
            query.append("  FROM room_result rr ");
            query.append("       ,round r ");
            query.append("       ,round_type_lu rt ");
            query.append(" WHERE r.round_id = rr.round_id ");
            query.append("   AND r.round_type_id = rt.round_type_id ");
            query.append("   AND rr.coder_id = ? ");
            query.append("   AND rr.attended = 'Y' ");
            query.append("   AND rr.new_rating > 0 ");
            query.append("   AND rt.algo_rating_type_id = ? ");

            //use the target db (warehouse) for this historical data
            psSelMinMaxRatings = prepareStatement(query.toString(), TARGET_DB);

            // No need to filter admins here as they have already been filtered from
            // the DW rating table
            query = new StringBuffer(100);
            query.append("SELECT first_rated_round_id "); // 1
            query.append("       ,last_rated_round_id "); // 2
            query.append("  FROM algo_rating ");
            query.append(" WHERE coder_id = ? ");
            query.append(" AND algo_rating_type_id = ? ");
            psSelRatedRounds = prepareStatement(query.toString(), TARGET_DB);

            // No need to filter admins here as they have already been filtered from
            // the DW rating table
            query = new StringBuffer(100);
            query.append("SELECT count(*) ");     // 1
            query.append("  FROM room_result rr ");
            query.append("       ,round r ");
            query.append("       ,round_type_lu rt ");
            query.append(" WHERE r.round_id = rr.round_id ");
            query.append("   AND r.round_type_id = rt.round_type_id ");
            query.append("   AND rr.attended = 'Y' ");
            query.append("   AND rr.coder_id = ? ");
            query.append("   AND rt.algo_rating_type_id = ? ");
            psSelNumCompetitions = prepareStatement(query.toString(), TARGET_DB);

            query = new StringBuffer(100);
            query.append("UPDATE algo_rating ");
            query.append("   SET first_rated_round_id = ? ");  // 1
            query.append("       ,last_rated_round_id = ? ");  // 2
            query.append("       ,lowest_rating = ? ");        // 3
            query.append("       ,highest_rating = ? ");       // 4
            query.append("       ,num_competitions = ? ");     // 5
            query.append(" WHERE coder_id = ?");               // 6
            query.append("   AND algo_rating_type_id = ? ");
            psUpd = prepareStatement(query.toString(), TARGET_DB);

            psSel.setInt(1, fRoundId);
            rs = psSel.executeQuery();

            while (rs.next()) {
                int coder_id = rs.getInt(1);

                int num_competitions = -1;
                int first_rated_round_id = -1;
                int last_rated_round_id = -1;
                int lowest_rating = -1;
                int highest_rating = -1;

                // Get the existing first and last rated round ids in case they are
                // already there.
                psSelRatedRounds.clearParameters();
                psSelRatedRounds.setInt(1, coder_id);
                psSelRatedRounds.setInt(2, algoType);
                rs2 = psSelRatedRounds.executeQuery();
                if (rs2.next()) {
                    if (rs2.getString(1) != null)
                        first_rated_round_id = rs2.getInt(1);
                    if (rs2.getString(2) != null)
                        last_rated_round_id = rs2.getInt(2);
                }

                close(rs2);

                // Get the number of competitions
                psSelNumCompetitions.clearParameters();
                psSelNumCompetitions.setInt(1, coder_id);
                psSelNumCompetitions.setInt(2, algoType);
                rs2 = psSelNumCompetitions.executeQuery();
                if (rs2.next()) {
                    num_competitions = rs2.getInt(1);
                }

                close(rs2);

                // Get the new min/max ratings to see if we
                psSelMinMaxRatings.clearParameters();
                psSelMinMaxRatings.setInt(1, coder_id);
                psSelMinMaxRatings.setInt(2, algoType);
                rs2 = psSelMinMaxRatings.executeQuery();
                if (rs2.next()) {
                    lowest_rating = rs2.getInt(1);
                    highest_rating = rs2.getInt(2);
                }

                close(rs2);

                // Check to see if any of the round ids need to be updated to be this
                // round id. If the round we are loading is prior to the first rated
                // round (or it isn't set) we set this round as the first rated round.
                // If the round we are loading is greater than the last rated round
                // (or it isn't set), we set this round as the last rated round
                if (first_rated_round_id == -1 ||
                        getRoundStart(fRoundId).compareTo(getRoundStart(first_rated_round_id)) < 0)
                    first_rated_round_id = fRoundId;

                if (last_rated_round_id == -1 ||
                        getRoundStart(fRoundId).compareTo(getRoundStart(last_rated_round_id)) > 0)
                    last_rated_round_id = fRoundId;

                // Finally, do update
                psUpd.clearParameters();
                psUpd.setInt(1, first_rated_round_id);  // first_rated_round_id
                psUpd.setInt(2, last_rated_round_id);   // last_rated_round_id
                psUpd.setInt(3, lowest_rating);         // lowest_rating
                psUpd.setInt(4, highest_rating);        // highest_rating
                psUpd.setInt(5, num_competitions);      // num_competitions
                psUpd.setInt(6, coder_id);              // coder_id
                psUpd.setInt(7, algoType);              // algo_rating_type_id

                retVal = psUpd.executeUpdate();
                count = count + retVal;
                if (retVal != 1) {
                    throw new SQLException("TCLoadCoders: Insert for coder_id " +
                            coder_id +
                            " modified " + retVal + " rows, not one.");
                }

                printLoadProgress(count, "rating");
            }

            log.info("Rating records updated = " + count);
        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Load of 'rating' table failed.\n" +
                    sqle.getMessage());
        } finally {
            close(rs);
            close(rs2);
            close(psRatedRound);
            close(psSel);
            close(psSelNumCompetitions);
            close(psSelRatedRounds);
            close(psSelMinMaxRatings);
            close(psUpd);
        }
    }

    /**
     * This method loads the 'problem_submission' table which holds
     * information for a given round and given coder, the results of a
     * particular problem
     */
    private void loadProblemSubmission() throws Exception {
        int retVal = 0;
        int count = 0;
        PreparedStatement psSel = null;
        PreparedStatement psIns = null;
        PreparedStatement psDel = null;
        ResultSet rs = null;
        StringBuffer query = null;

        try {
            query = new StringBuffer(100);
            query.append(" SELECT cs.round_id");              //1
            query.append(" ,cs.coder_id ");            //2
            query.append(" , (SELECT cm.problem_id FROM component cm WHERE cm.component_id = cs.component_id)");          //3
            query.append(" ,cs.points ");              //4
            query.append(" ,cs.status_id ");           //5
            query.append(" ,CASE WHEN s.language_id is null THEN c.language_id ELSE s.language_id END as language_id");         //6
            query.append(" ,s.open_time ");            //7
            query.append(" ,cs.submission_number ");   //8
            query.append(" ,s.submission_text ");      //9
            query.append(" ,s.submit_time ");          //10
            query.append(" ,s.submission_points ");    //11
            query.append("  ,(SELECT status_desc ");   //12
            query.append(" FROM problem_status_lu ");
            query.append(" WHERE problem_status_id = cs.status_id) ");
            query.append(" ,c.compilation_text");      //13
            query.append(" ,s.submission_number");     //14
            query.append(" FROM component_state cs ");
            query.append(" LEFT OUTER JOIN submission s ");
            query.append(" ON cs.component_state_id = s.component_state_id");
            query.append(" LEFT OUTER JOIN compilation c ");
            query.append(" ON cs.component_state_id = c.component_state_id");
            query.append(" WHERE cs.round_id = ?");
            query.append("   AND NOT EXISTS ");
            query.append("       (SELECT 'pops' ");
            query.append("          FROM user_group_xref ugx ");
            query.append("         WHERE ugx.login_id= cs.coder_id ");
            query.append("           AND ugx.group_id = 2000115)");
            query.append("   AND NOT EXISTS ");
            query.append("       (SELECT 'pops' ");
            query.append("          FROM group_user gu ");
            query.append("         WHERE gu.user_id = cs.coder_id ");
            query.append("           AND gu.group_id = 13)");


            psSel = prepareStatement(query.toString(), SOURCE_DB);

            query = new StringBuffer(100);
            query.append("INSERT INTO problem_submission ");
            query.append("      (round_id ");            // 1
            query.append("       ,coder_id ");           // 2
            query.append("       ,problem_id ");         // 3
            query.append("       ,final_points ");       // 4
            query.append("       ,status_id ");          // 5
            query.append("       ,language_id ");        // 6
            query.append("       ,open_time ");          // 7
            query.append("       ,submission_number ");  // 8
            query.append("       ,submission_text ");    // 9
            query.append("       ,submit_time ");        // 10
            query.append("       ,submission_points ");  // 11
            query.append("       ,status_desc ");        // 12
            query.append("       ,last_submission) ");   // 13
            query.append("VALUES (");
            query.append("?,?,?,?,?,?,?,?,?,?,");  // 10
            query.append("?,?,?)");                // 12 total values
            psIns = prepareStatement(query.toString(), TARGET_DB);

            query = new StringBuffer(100);
            query.append("DELETE FROM problem_submission ");
            query.append(" WHERE round_id = ? ");
            query.append("   AND coder_id = ? ");
            query.append("   AND problem_id = ?");
            query.append("   AND submission_number = ?");
            psDel = prepareStatement(query.toString(), TARGET_DB);

            // On to the load
            psSel.setInt(1, fRoundId);
            rs = psSel.executeQuery();

            while (rs.next()) {
                int round_id = rs.getInt(1);
                int coder_id = rs.getInt(2);
                int problem_id = rs.getInt(3);
                int submission_number = rs.getInt(14);
                int last_submission = 0;
                if (rs.getInt(8) > 0) {  //they submitted at least once
                    last_submission = rs.getInt(8) == submission_number ? 1 : 0;
                }

                psDel.clearParameters();
                psDel.setInt(1, round_id);
                psDel.setInt(2, coder_id);
                psDel.setInt(3, problem_id);
                psDel.setInt(4, submission_number);
                psDel.executeUpdate();

                psIns.clearParameters();
                psIns.setInt(1, rs.getInt(1));  // round_id
                psIns.setInt(2, rs.getInt(2));  // coder_id
                psIns.setInt(3, rs.getInt(3));  // problem_id
                psIns.setFloat(4, rs.getFloat(4));  // final_points
                psIns.setInt(5, rs.getInt(5));  // status_id
                psIns.setInt(6, rs.getInt(6));  // language_id
                psIns.setLong(7, rs.getLong(7));  // open_time
                psIns.setInt(8, rs.getInt(14));  // submission_number
                if (Arrays.equals(getBytes(rs, 9), "".getBytes()))
                    setBytes(psIns, 9, getBytes(rs, 13));       // use compilation_text
                else
                    setBytes(psIns, 9, getBytes(rs, 9));       // use submission_text
                psIns.setLong(10, rs.getLong(10));  // submit_time
                psIns.setFloat(11, rs.getFloat(11));  // submission_points
                psIns.setString(12, rs.getString(12));  // status_desc
                psIns.setInt(13, last_submission);  // last_submission

                retVal = psIns.executeUpdate();
                count += retVal;
                if (retVal != 1) {
                    throw new SQLException("TCLoadRound: Insert for coder_id " +
                            coder_id + ", round_id " + round_id +
                            ", problem_id " + problem_id +
                            " modified " + retVal + " rows, not one.");
                }

                printLoadProgress(count, "problem_submission");
            }

            log.info("Problem_submission records copied = " + count);
        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Load of 'problem_submission' table failed.\n" +
                    sqle.getMessage());
        } finally {
            close(rs);
            close(psSel);
            close(psIns);
            close(psDel);
        }
    }

    /**
     * This load the 'system_test_case' table
     */
    private void loadSystemTestCase() throws Exception {
        int retVal = 0;
        int count = 0;
        PreparedStatement psSel = null;
        PreparedStatement psIns = null;
        PreparedStatement psDel = null;
        ResultSet rs = null;
        StringBuffer query = null;

        try {
            query = new StringBuffer(100);
            query.append("SELECT stc.test_case_id ");      // 1
            query.append("       ,comp.problem_id ");       // 2
            query.append("       ,stc.args ");             // 3
            query.append("       ,stc.expected_result ");  // 4
            query.append("       ,CURRENT ");              // 5
            query.append("  FROM system_test_case stc, component comp ");
            query.append(" WHERE comp.component_id in (SELECT component_id FROM round_component WHERE round_id = ?)");
            query.append(" AND comp.component_id = stc.component_id");
            psSel = prepareStatement(query.toString(), SOURCE_DB);

            query = new StringBuffer(100);
            query.append("INSERT INTO system_test_case ");
            query.append("      (test_case_id ");      // 1
            query.append("       ,problem_id ");       // 2
            query.append("       ,args ");             // 3
            query.append("       ,expected_result ");  // 4
            query.append("       ,modify_date) ");     // 5
            query.append("VALUES ( ");
            query.append("?,?,?,?,?)");  // 5 total values
            psIns = prepareStatement(query.toString(), TARGET_DB);

            query = new StringBuffer(100);
            query.append("DELETE FROM system_test_case ");
            query.append(" WHERE test_case_id = ? ");
            query.append("   AND problem_id = ?");
            psDel = prepareStatement(query.toString(), TARGET_DB);

            // On to the load
            psSel.setInt(1, fRoundId);
            rs = psSel.executeQuery();

            while (rs.next()) {
                int test_case_id = rs.getInt(1);
                int problem_id = rs.getInt(2);

                psDel.clearParameters();
                psDel.setInt(1, test_case_id);
                psDel.setInt(2, problem_id);
                psDel.executeUpdate();

                psIns.clearParameters();
                psIns.setInt(1, rs.getInt(1));  // test_case_id
                psIns.setInt(2, rs.getInt(2));  // problem_id
                setBytes(psIns, 3, getBlobObject(rs, 3));  // args
                setBytes(psIns, 4, getBlobObject(rs, 4));  // expected_result
                psIns.setTimestamp(5, rs.getTimestamp(5));  // modify_date

                retVal = psIns.executeUpdate();
                count += retVal;
                if (retVal != 1) {
                    throw new SQLException("TCLoadRound: Insert for test_case_id " +
                            test_case_id + ", problem_id " + problem_id +
                            " modified more than one row.");
                }

                printLoadProgress(count, "system_test_case");
            }

            log.info("System_test_case records copied = " + count);
        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Load of 'system_test_case' table failed.\n" +
                    sqle.getMessage());
        } finally {
            close(rs);
            close(psSel);
            close(psIns);
            close(psDel);
        }
    }

    /**
     * This load the 'system_test_result' table which holds the results
     * of the system tests for a give round, coder and problem.
     */
    private void loadSystemTestResult() throws Exception {
        int retVal = 0;
        int count = 0;
        PreparedStatement psSel = null;
        PreparedStatement psIns = null;
        PreparedStatement psDel = null;
        ResultSet rs = null;
        StringBuffer query = null;

        try {
            query = new StringBuffer(100);
            query.append("SELECT str.coder_id ");           // 1
            query.append("       ,str.round_id ");          // 2
            query.append("       ,comp.problem_id ");        // 3
            query.append("       ,str.test_case_id ");      // 4
            query.append("       ,str.num_iterations ");    // 5
            query.append("       ,str.processing_time ");   // 6
            query.append("       ,str.deduction_amount ");  // 7
            query.append("       ,str.timestamp ");         // 8
            query.append("       ,str.viewable ");          // 9
            query.append("       ,str.received ");          // 10
            query.append("       ,str.succeeded ");         // 11
            query.append("       ,str.message ");           // 12
            query.append("  FROM system_test_result str, component comp ");
            query.append(" WHERE str.round_id = ?");
            query.append(" AND comp.component_id = str.component_id");
            query.append(" AND str.coder_id NOT IN  ");
            query.append("       (SELECT ugx.login_id ");
            query.append("          FROM user_group_xref ugx ");
            query.append("         WHERE ugx.group_id = 2000115)");
            query.append(" AND str.coder_id NOT IN ");
            query.append("       (SELECT user_id ");
            query.append("          FROM group_user");
            query.append("         WHERE group_id = 13)");


            psSel = prepareStatement(query.toString(), SOURCE_DB);

            query = new StringBuffer(100);
            query.append("INSERT INTO system_test_result ");
            query.append("      (coder_id ");           // 1
            query.append("       ,round_id ");          // 2
            query.append("       ,problem_id ");        // 3
            query.append("       ,test_case_id ");      // 4
            query.append("       ,num_iterations ");    // 5
            query.append("       ,processing_time ");   // 6
            query.append("       ,deduction_amount ");  // 7
            query.append("       ,timestamp ");         // 8
            query.append("       ,viewable ");          // 9
            query.append("       ,received ");          // 10
            query.append("       ,succeeded ");         // 11
            query.append("       ,message) ");          // 12
            query.append("VALUES (");
            query.append("?,?,?,?,?,?,?,?,?,?,");  // 10 values
            query.append("?,?)");                   // 12 total values
            psIns = prepareStatement(query.toString(), TARGET_DB);

            query = new StringBuffer(100);
            query.append("DELETE FROM system_test_result ");
            query.append(" WHERE coder_id = ? ");
            query.append("   AND round_id = ? ");
            query.append("   AND problem_id = ? ");
            query.append("   AND test_case_id = ?");
            psDel = prepareStatement(query.toString(), TARGET_DB);

            // On to the load
            psSel.setInt(1, fRoundId);
            rs = psSel.executeQuery();

            while (rs.next()) {
                int coder_id = rs.getInt(1);
                int round_id = rs.getInt(2);
                int problem_id = rs.getInt(3);
                int test_case_id = rs.getInt(4);

                psDel.clearParameters();
                psDel.setInt(1, coder_id);
                psDel.setInt(2, round_id);
                psDel.setInt(3, problem_id);
                psDel.setInt(4, test_case_id);
                psDel.executeUpdate();

                psIns.clearParameters();
                psIns.setInt(1, rs.getInt(1));  // coder_id
                psIns.setInt(2, rs.getInt(2));  // round_id
                psIns.setInt(3, rs.getInt(3));  // problem_id
                psIns.setInt(4, rs.getInt(4));  // test_case_id
                psIns.setInt(5, rs.getInt(5));  // num_iterations
                psIns.setLong(6, rs.getLong(6));  // processing_time
                psIns.setFloat(7, rs.getFloat(7));  // deduction_amount
                psIns.setTimestamp(8, rs.getTimestamp(8));  // timestamp
                psIns.setString(9, rs.getString(9));  // viewable
                setBytes(psIns, 10, getBlobObject(rs, 10));  // received
                psIns.setInt(11, rs.getInt(11));  // succeeded
                psIns.setString(12, rs.getString(12));  // message

                retVal = psIns.executeUpdate();
                count += retVal;
                if (retVal != 1) {
                    throw new SQLException("TCLoadRound: Insert for coder_id " +
                            coder_id + ", round_id " + round_id +
                            ", problem_id " + problem_id +
                            ", test_case_id " + test_case_id +
                            " modified " + retVal + " rows, not one.");
                }

                printLoadProgress(count, "system_test_result");
            }

            log.info("System_test_result records copied = " + count);
        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Load of 'system_test_result' table failed.\n" +
                    sqle.getMessage());
        } finally {
            close(rs);
            close(psSel);
            close(psIns);
            close(psDel);
        }
    }

    /**
     * This loads the 'contest' table
     */
    private void loadContest() throws Exception {
        int retVal = 0;
        int count = 0;
        PreparedStatement psSel = null;
        PreparedStatement psIns = null;
        PreparedStatement psSel2 = null;
        PreparedStatement psUpd = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        StringBuffer query = null;

        try {
            query = new StringBuffer(100);
            query.append("SELECT c.contest_id ");    // 1
            query.append("       ,c.name ");         // 2
            query.append("       ,c.start_date ");   // 3
            query.append("       ,c.end_date ");     // 4
            query.append("       ,c.status ");       // 5
            query.append("       ,c.group_id ");     // 6
            query.append("       ,c.ad_text ");      // 7
            query.append("       ,c.ad_start ");     // 8
            query.append("       ,c.ad_end ");       // 9
            query.append("       ,c.ad_task ");      // 10
            query.append("       ,c.ad_command ");   // 11
            query.append("       ,c.season_id ");    // 12
            query.append("  FROM contest c ");
            query.append("       ,round r ");
            query.append(" WHERE r.round_id = ? ");
            query.append("   AND r.contest_id = c.contest_id");
            psSel = prepareStatement(query.toString(), SOURCE_DB);

            query = new StringBuffer(100);
            query.append("INSERT INTO contest ");
            query.append("      (contest_id ");    // 1
            query.append("       ,name ");         // 2
            query.append("       ,start_date ");   // 3
            query.append("       ,end_date ");     // 4
            query.append("       ,status ");       // 5
            query.append("       ,group_id ");     // 6
            query.append("       ,ad_text ");      // 7
            query.append("       ,ad_start ");     // 8
            query.append("       ,ad_end ");       // 9
            query.append("       ,ad_task ");      // 10
            query.append("       ,ad_command ");  // 11
            query.append("       ,season_id) ");    // 12
            query.append("VALUES (");
            query.append("?,?,?,?,?,?,?,?,?,?,");  // 10 values
            query.append("?,?)");                // 12 total values
            psIns = prepareStatement(query.toString(), TARGET_DB);

            query = new StringBuffer(100);
            query.append("UPDATE contest ");
            query.append("   SET name = ? ");          // 1
            query.append("       ,start_date = ? ");   // 2
            query.append("       ,end_date = ? ");     // 3
            query.append("       ,status = ? ");       // 4
            query.append("       ,group_id = ? ");     // 5
            query.append("       ,ad_text = ? ");      // 6
            query.append("       ,ad_start = ? ");     // 7
            query.append("       ,ad_end = ? ");       // 8
            query.append("       ,ad_task = ? ");      // 9
            query.append("       ,ad_command = ? ");   // 10
            query.append("       ,season_id = ? ");    // 11
            query.append(" WHERE contest_id = ? ");    // 12
            psUpd = prepareStatement(query.toString(), TARGET_DB);

            query = new StringBuffer(100);
            query.append("SELECT 'pops' ");
            query.append("  FROM contest ");
            query.append(" WHERE contest_id = ?");
            psSel2 = prepareStatement(query.toString(), TARGET_DB);

            // On to the load
            psSel.setInt(1, fRoundId);
            rs = psSel.executeQuery();

            while (rs.next()) {
                int contest_id = rs.getInt(1);
                psSel2.clearParameters();
                psSel2.setInt(1, contest_id);
                rs2 = psSel2.executeQuery();

                // If next() returns true that means this row exists. If so,
                // we update. Otherwise, we insert.
                if (rs2.next()) {
                    psUpd.clearParameters();
                    psUpd.setString(1, rs.getString(2));  // name
                    psUpd.setTimestamp(2, rs.getTimestamp(3));  // start_date
                    psUpd.setTimestamp(3, rs.getTimestamp(4));  // end_date
                    psUpd.setString(4, rs.getString(5));  // status
                    psUpd.setInt(5, rs.getInt(6));  // group_id
                    psUpd.setString(6, rs.getString(7));  // ad_text
                    psUpd.setTimestamp(7, rs.getTimestamp(8));  // ad_start
                    psUpd.setTimestamp(8, rs.getTimestamp(9));  // ad_end
                    psUpd.setString(9, rs.getString(10));  // ad_task
                    psUpd.setString(10, rs.getString(11));  // ad_command
                    psUpd.setString(11, rs.getString(12));  // season_id
                    psUpd.setInt(12, rs.getInt(1));  // contest_id

                    retVal = psUpd.executeUpdate();
                    count += retVal;
                    if (retVal != 1) {
                        throw new SQLException("TCLoadRound: Insert for contest_id " +
                                contest_id +
                                " modified " + retVal + " rows, not one.");
                    }
                } else {
                    psIns.clearParameters();
                    psIns.setInt(1, rs.getInt(1));  // contest_id
                    psIns.setString(2, rs.getString(2));  // name
                    psIns.setTimestamp(3, rs.getTimestamp(3));  // start_date
                    psIns.setTimestamp(4, rs.getTimestamp(4));  // end_date
                    psIns.setString(5, rs.getString(5));  // status
                    psIns.setInt(6, rs.getInt(6));  // group_id
                    psIns.setString(7, rs.getString(7));  // ad_text
                    psIns.setTimestamp(8, rs.getTimestamp(8));  // ad_start
                    psIns.setTimestamp(9, rs.getTimestamp(9));  // ad_end
                    psIns.setString(10, rs.getString(10));  // ad_task
                    psIns.setString(11, rs.getString(11));  // ad_command
                    psIns.setString(12, rs.getString(12));  // season_id

                    retVal = psIns.executeUpdate();
                    count += retVal;
                    if (retVal != 1) {
                        throw new SQLException("TCLoadRound: Insert for contest_id " +
                                contest_id +
                                " modified " + retVal + " rows, not one.");
                    }
                }

                close(rs2);
                printLoadProgress(count, "contest");
            }

            log.info("Contest records copied = " + count);
        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Load of 'contest' table failed.\n" +
                    sqle.getMessage());
        } finally {
            close(rs);
            close(rs2);
            close(psSel);
            close(psSel2);
            close(psIns);
            close(psUpd);
        }
    }

    /**
     * This loads the 'problem' table
     */
    private void loadProblem() throws Exception {
        int retVal = 0;
        int count = 0;
        PreparedStatement psSel = null;
        PreparedStatement psSel2 = null;
        PreparedStatement psIns = null;
        PreparedStatement psUpd = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        StringBuffer query = null;

        try {
            query = new StringBuffer(100);
            query.append("SELECT p.problem_id ");                             // 1
            query.append("       ,rp.round_id ");                             // 2
            query.append("       ,c.result_type_id ");                        // 3
            query.append("       ,c.method_name ");                           // 4
            query.append("       ,c.class_name ");                            // 5
            query.append("       ,p.status_id ");                                // 6
            query.append("       ,c.default_solution ");                      // 7
            query.append("       ,c.component_text ");                          // 8
            query.append("       ,CURRENT ");                                 // 9
            query.append("       ,(SELECT data_type_desc ");                  // 10
            query.append("           FROM data_type ");
            query.append("          WHERE data_type_id = c.result_type_id) ");
            query.append("       ,d.difficulty_id ");                         // 11
            query.append("       ,d.difficulty_desc ");                       // 12
            query.append("       ,rp.division_id ");                          // 13
            query.append("       ,rp.points ");                               // 14
            query.append("  FROM problem p ");
            query.append("       ,round_component rp ");
            query.append("       ,difficulty d ");
            query.append("       ,component c ");
            query.append(" WHERE rp.round_id = ? ");
            query.append("   AND p.problem_id = c.problem_id");
            query.append("   AND c.component_id = rp.component_id ");
            query.append("   AND rp.difficulty_id = d.difficulty_id");
            psSel = prepareStatement(query.toString(), SOURCE_DB);

            query = new StringBuffer(100);
            query.append("INSERT INTO problem ");
            query.append("      (problem_id ");         // 1
            query.append("       ,round_id ");          // 2
            query.append("       ,result_type_id ");    // 3
            query.append("       ,method_name ");       // 4
            query.append("       ,class_name ");        // 5
            query.append("       ,status ");            // 6
            query.append("       ,default_solution ");  // 7
            query.append("       ,problem_text ");      // 8
            query.append("       ,modify_date ");       // 9
            query.append("       ,result_type_desc ");  // 10
            query.append("       ,level_id ");          // 11
            query.append("       ,level_desc ");        // 12
            query.append("       ,division_id ");       // 13
            query.append("       ,points ");            // 14
            query.append("       ,viewable) ");         // 15
            query.append("VALUES (");
            query.append("?,?,?,?,?,?,?,?,?,?,");
            query.append("?,?,?,?,?)");
            psIns = prepareStatement(query.toString(), TARGET_DB);

            query = new StringBuffer(100);
            query.append("UPDATE problem ");
            query.append("   SET result_type_id = ? ");     // 1
            query.append("       ,method_name = ? ");       // 2
            query.append("       ,class_name = ? ");        // 3
            query.append("       ,status = ? ");            // 4
            query.append("       ,default_solution = ? ");  // 5
            query.append("       ,problem_text = ? ");      // 6
            query.append("       ,modify_date = ? ");       // 7
            query.append("       ,result_type_desc = ? ");  // 8
            query.append("       ,level_id = ? ");          // 9
            query.append("       ,level_desc = ? ");        // 10
            query.append("       ,points = ? ");            // 11
            query.append("       ,viewable = ?");           // 12
            query.append(" WHERE problem_id = ? ");         // 13
            query.append("   AND round_id = ? ");           // 14
            query.append("   AND division_id = ? ");        // 15
            psUpd = prepareStatement(query.toString(), TARGET_DB);

            query = new StringBuffer(100);
            query.append("SELECT 'pops' FROM problem ");
            query.append(" WHERE problem_id = ? ");
            query.append("   AND round_id = ?");
            query.append("   AND division_id = ?");
            psSel2 = prepareStatement(query.toString(), TARGET_DB);

            // On to the load
            psSel.setInt(1, fRoundId);
            rs = psSel.executeQuery();

            while (rs.next()) {
                int problem_id = rs.getInt(1);
                int round_id = rs.getInt(2);
                int division_id = rs.getInt(13);

                psSel2.clearParameters();
                psSel2.setInt(1, problem_id);
                psSel2.setInt(2, round_id);
                psSel2.setInt(3, division_id);
                rs2 = psSel2.executeQuery();

                // If next() returns true that means this row exists. If so,
                // we update. Otherwise, we insert.
                if (rs2.next()) {
                    psUpd.clearParameters();
                    psUpd.setInt(1, rs.getInt(3));  // result_type_id
                    psUpd.setString(2, rs.getString(4));  // method_name
                    psUpd.setString(3, rs.getString(5));  // class_name
                    psUpd.setInt(4, rs.getInt(6));  // status
                    setBytes(psUpd, 5, getBytes(rs, 7));  // default_solution
                    setBytes(psUpd, 6, getBytes(rs, 8));  // problem_text
                    psUpd.setTimestamp(7, rs.getTimestamp(9));  // modify_date
                    psUpd.setString(8, rs.getString(10));  // result_type_desc
                    psUpd.setInt(9, rs.getInt(11));  // level_id
                    psUpd.setString(10, rs.getString(12));  // level_desc
                    psUpd.setFloat(11, rs.getFloat(14)); // points
                    psUpd.setInt(12, 1); //viewable
                    psUpd.setInt(13, rs.getInt(1));  // problem_id
                    psUpd.setInt(14, rs.getInt(2));  // round_id
                    psUpd.setInt(15, rs.getInt(13));  // division_id

                    retVal = psUpd.executeUpdate();
                    count += retVal;
                    if (retVal != 1) {
                        throw new SQLException("TCLoadRound: Update for problem_id " +
                                problem_id + ", round_id " + round_id +
                                " modified " + retVal + " rows, not one.");
                    }
                } else {
                    psIns.clearParameters();
                    psIns.setInt(1, rs.getInt(1));  // problem_id
                    psIns.setInt(2, rs.getInt(2));  // round_id
                    psIns.setInt(3, rs.getInt(3));  // result_type_id
                    psIns.setString(4, rs.getString(4));  // method_name
                    psIns.setString(5, rs.getString(5));  // class_name
                    psIns.setInt(6, rs.getInt(6));  // status
                    setBytes(psIns, 7, getBytes(rs, 7));  // default_solution
                    setBytes(psIns, 8, getBytes(rs, 8));  // problem_text
                    psIns.setTimestamp(9, rs.getTimestamp(9));  // modify_date
                    psIns.setString(10, rs.getString(10));  // result_type_desc
                    psIns.setInt(11, rs.getInt(11));  // level_id
                    psIns.setString(12, rs.getString(12));  // level_desc
                    psIns.setInt(13, rs.getInt(13));  // division_id
                    psIns.setFloat(14, rs.getFloat(14)); //points
                    psIns.setInt(15, 1); //viewable

                    retVal = psIns.executeUpdate();
                    count += retVal;
                    if (retVal != 1) {
                        throw new SQLException("TCLoadRound: Insert for problem_id " +
                                problem_id + ", round_id " + round_id +
                                " modified " + retVal + " rows, not one.");
                    }
                }

                close(rs2);
                printLoadProgress(count, "problem");
            }

            log.info("Problem records copied = " + count);
        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Load of 'problem' table failed.\n" +
                    sqle.getMessage());
        } finally {
            close(rs);
            close(rs2);
            close(psSel);
            close(psSel2);
            close(psIns);
            close(psUpd);
        }
    }

    /**
     * This loads the 'round' table
     */
    private void loadRound() throws Exception {
        int retVal = 0;
        int count = 0;
        PreparedStatement psSel = null;
        PreparedStatement psSel2 = null;
        PreparedStatement psSelRatingOrder = null;
        PreparedStatement psIns = null;
        PreparedStatement psUpd = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        StringBuffer query = null;

        try {
            query = new StringBuffer(100);
            query.append("SELECT r.round_id ");                          // 1
            query.append("       ,r.contest_id ");                       // 2
            query.append("       ,r.name ");                             // 3
            query.append("       ,r.status ");                           // 4
            query.append("       ,(SELECT sum(paid) ");                  // 5
            query.append("           FROM room_result rr ");
            query.append("          WHERE rr.round_id = r.round_id) ");
            query.append("       ,rs.start_time ");                      // 6
            query.append("       ,r.round_type_id ");                    // 7
            query.append("       ,r.invitational ");                     // 8
            query.append("       ,r.notes ");                            // 9
            query.append("       ,rtlu.round_type_desc ");               // 10
            query.append("       ,r.short_name ");                       // 11
            query.append("       ,r.forum_id");                          // 12
            query.append("       ,r.rated_ind");                         // 13
            query.append("       ,r.region_id");                         // 14
            query.append("       ,rtlu.algo_rating_type_id");            // 15
            query.append("  FROM round r ");
            query.append("       ,round_segment rs ");
            query.append("       ,round_type_lu rtlu ");
            query.append(" WHERE r.round_id = ? ");
            query.append("   AND rs.round_id = r.round_id ");
            query.append("   AND rs.segment_id = " + CODING_SEGMENT_ID);
            query.append("   AND rtlu.round_type_id = r.round_type_id");
            psSel = prepareStatement(query.toString(), SOURCE_DB);

            query = new StringBuffer(100);
            query.append(" SELECT max(r1.rating_order)  ");
            query.append("         from round r1");
            query.append("         ,round_type_lu rt1 ");
            query.append("         where r1.rated_ind = 1 ");
            query.append("         AND r1.round_type_id = rt1.round_type_id ");
            query.append("         AND rt1.algo_rating_type_id = ? ");    //1
            query.append("         AND ((r1.calendar_id < ?) ");          //2 
            query.append("         OR (r1.calendar_id = ? AND r1.time_id < ?) "); //3 & 4
            query.append("         OR (r1.calendar_id = ? AND r1.time_id = ? AND r1.round_id < ?)) "); // 5, 6 & 7
            psSelRatingOrder = prepareStatement(query.toString(), TARGET_DB);

            // We have 8 values in the insert as opposed to 7 in the select
            // because we want to provide a default value for failed. We
            // don't have a place to select failed from in the transactional
            // DB
            query = new StringBuffer(100);
            query.append("INSERT INTO round ");
            query.append("      (round_id ");          // 1
            query.append("       ,contest_id ");       // 2
            query.append("       ,name ");             // 3
            query.append("       ,status ");           // 4
            query.append("       ,money_paid ");       // 5
            query.append("       ,calendar_id ");      // 6
            query.append("       ,failed ");           // 7
            query.append("       ,round_type_id ");    // 8
            query.append("       ,invitational  ");    // 9
            query.append("       ,notes         ");    // 10
            query.append("       ,round_type_desc ");  // 11
            query.append("       ,short_name ");       // 12
            query.append("       ,forum_id ");         // 13
            query.append("       ,rated_ind ");        // 14
            query.append("       ,region_id ");           // 15
            query.append("       ,time_id ");         // 16
            query.append("       ,rating_order) ");         // 17
            query.append("VALUES (");
            query.append("?,?,?,?,?,?,?,?,?,?,");
            query.append("?,?,?,?,?,?,?)");

            psIns = prepareStatement(query.toString(), TARGET_DB);

            query = new StringBuffer(100);
            query.append("UPDATE round ");
            query.append("   SET contest_id = ? ");       // 1
            query.append("       ,name = ? ");            // 2
            query.append("       ,status = ? ");          // 3
            query.append("       ,money_paid = ? ");      // 4
            query.append("       ,calendar_id = ? ");     // 5
            query.append("       ,failed = ? ");          // 6
            query.append("       ,round_type_id = ? ");   // 7
            query.append("       ,invitational  = ? ");   // 8
            query.append("       ,notes = ?         ");   // 9
            query.append("       ,round_type_desc = ? "); // 10
            query.append("       ,short_name = ? ");      // 11
            query.append("       ,forum_id = ? ");        // 12
            query.append("       ,rated_ind = ? ");       // 13
            query.append("       ,region_id = ? ");       // 14
            query.append("       ,time_id = ? ");         // 15
            query.append("       ,rating_order = ? ");         // 16
            query.append(" WHERE round_id = ? ");         // 17
            psUpd = prepareStatement(query.toString(), TARGET_DB);

            query = new StringBuffer(100);
            query.append("SELECT 'pops' FROM round where round_id = ?");
            psSel2 = prepareStatement(query.toString(), TARGET_DB);

            // On to the load
            psSel.setInt(1, fRoundId);
            rs = psSel.executeQuery();
            while (rs.next()) {
                int round_id = rs.getInt(1);
                int rated_ind = rs.getInt(13);
                int ratingType = rs.getInt(15);
                psSel2.clearParameters();
                psSel2.setInt(1, round_id);
                rs2 = psSel2.executeQuery();
                boolean roundExists = rs2.next();
                close(rs2);

                // Retrieve the calendar_id for the start_time of this round
                java.sql.Timestamp stamp = rs.getTimestamp(6);
                int calendar_id = lookupCalendarId(stamp, TARGET_DB);
                int time_id = lookupTimeId(stamp, TARGET_DB);
                
                int ratingOrder = 0;
                if (rated_ind==1) {
                    psSelRatingOrder.clearParameters();
                    psSelRatingOrder.setInt(1, ratingType);
                    psSelRatingOrder.setInt(2, calendar_id);
                    psSelRatingOrder.setInt(3, calendar_id);
                    psSelRatingOrder.setInt(4, time_id);
                    psSelRatingOrder.setInt(5, calendar_id);
                    psSelRatingOrder.setInt(6, time_id);
                    psSelRatingOrder.setInt(7, round_id);
                    rs2 = psSelRatingOrder.executeQuery();
                    ratingOrder = rs2.next() ? rs2.getInt(1) : 0;
                    close(rs2);
                    ratingOrder++;
                }

                // If next() returns true that means this row exists. If so,
                // we update. Otherwise, we insert.
                if (roundExists) {
                    psUpd.clearParameters();
                    psUpd.setInt(1, rs.getInt(2));  // contest_id
                    psUpd.setString(2, rs.getString(3));  // name
                    psUpd.setString(3, rs.getString(4));  // status
                    psUpd.setFloat(4, rs.getFloat(5));  // money_paid
                    psUpd.setInt(5, calendar_id);         // cal_id of start_time
                    psUpd.setInt(6, 0);                   // failed (default is 0)
                    psUpd.setInt(7, rs.getInt(7));        // round_type_id
                    psUpd.setInt(8, rs.getInt(8));        // invitational
                    psUpd.setString(9, rs.getString(9));     // notes
                    psUpd.setString(10, rs.getString(10));    // round_type_desc
                    psUpd.setString(11, rs.getString(11));   // shortname
                    psUpd.setInt(12, rs.getInt(12));   // forum_id
                    psUpd.setInt(13, rated_ind);   // rated_ind
                    if (rs.getString(14) != null) {
                        psUpd.setInt(14, rs.getInt(14));  // region_id
                    } else {
                        psUpd.setNull(14, java.sql.Types.DECIMAL);  // region_id
                    }

                    psUpd.setInt(15, time_id);
                    psUpd.setInt(16, ratingOrder);
                    psUpd.setInt(17, rs.getInt(1));  // round_id

                    retVal = psUpd.executeUpdate();
                    count += retVal;
                    if (retVal != 1) {
                        throw new SQLException("TCLoadRound: Update for round_id " +
                                round_id +
                                " modified " + retVal + " rows, not one.");
                    }
                } else {
                    psIns.clearParameters();
                    psIns.setInt(1, rs.getInt(1));  // round_id
                    psIns.setInt(2, rs.getInt(2));  // contest_id
                    psIns.setString(3, rs.getString(3));  // name
                    psIns.setString(4, rs.getString(4));  // status
                    psIns.setFloat(5, rs.getFloat(5));  // money_paid
                    psIns.setInt(6, calendar_id);  // cal_id of start_time
                    psIns.setInt(7, 0);                   // failed (default is 0)
                    psIns.setInt(8, rs.getInt(7));        // round_type_id
                    psIns.setInt(9, rs.getInt(8));        // invitational
                    psIns.setString(10, rs.getString(9));     // notes
                    psIns.setString(11, rs.getString(10));    // round_type_desc
                    psIns.setString(12, rs.getString(11));  // short name
                    psIns.setString(13, rs.getString(12));  // forum_id
                    psIns.setInt(14, rs.getInt(13));  // rated_ind
                    if (rs.getString(14) != null) {
                        psIns.setInt(15, rs.getInt(14));  // region_id
                    } else {
                        psIns.setNull(15, java.sql.Types.DECIMAL);  // region_id
                    }
                    psIns.setInt(16, time_id);
                    psIns.setInt(17, ratingOrder);

                    retVal = psIns.executeUpdate();
                    count += retVal;
                    if (retVal != 1) {
                        throw new SQLException("TCLoadRound: Insert for round_id " +
                                round_id +
                                " modified " + retVal + " rows, not one.");
                    }
                }
                printLoadProgress(count, "round");
            }

            log.info("Round records copied = " + count);
        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Load of 'round' table failed.\n" +
                    sqle.getMessage());
        } finally {
            close(rs2);
            close(psSel);
            close(psSel2);
            close(psSelRatingOrder);
            close(psIns);
            close(psUpd);
        }
    }

    /**
     * This loads the 'room' table
     */
    private void loadRoom() throws Exception {
        int retVal = 0;
        int count = 0;
        PreparedStatement psSel = null;
        PreparedStatement psSel2 = null;
        PreparedStatement psIns = null;
        PreparedStatement psUpd = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        StringBuffer query = null;

        try {
            query = new StringBuffer(100);
            query.append("SELECT r.room_id");                                    // 1
            query.append("       ,r.round_id ");                                 // 2
            query.append("       ,r.name ");                                     // 3
            query.append("       ,r.state_code ");                               // 4
            query.append("       ,(SELECT s.state_name ");                       // 5
            query.append("           FROM state s ");
            query.append("          WHERE s.state_code = r.state_code) ");
            query.append("       ,r.country_code ");                             // 6
            query.append("       ,(SELECT c.country_name ");                     // 7
            query.append("           FROM country c ");
            query.append("          WHERE c.country_code = r.country_code) ");
            query.append("       ,r.division_id ");                              // 8
            query.append("       ,(SELECT d.division_desc ");                    // 9
            query.append("           FROM division d ");
            query.append("          WHERE d.division_id = r.division_id) ");
            query.append("       ,r.room_type_id ");                             // 10
            query.append("       ,(SELECT rt.room_type_desc ");                  // 11
            query.append("           FROM room_type rt ");
            query.append("          WHERE rt.room_type_id = r.room_type_id) ");
            query.append("       ,r.eligible ");                                 // 12
            query.append("       ,r.unrated ");                                  // 13
            query.append("   FROM room r ");
            query.append("  WHERE round_id = ?");
            query.append("    AND r.room_type_id = " + CONTEST_ROOM);
            psSel = prepareStatement(query.toString(), SOURCE_DB);

            query = new StringBuffer(100);
            query.append("INSERT INTO room ");
            query.append("      (room_id ");           // 1
            query.append("       ,round_id ");         // 2
            query.append("       ,name ");             // 3
            query.append("       ,state_code ");       // 4
            query.append("       ,state_name ");       // 5
            query.append("       ,country_code ");     // 6
            query.append("       ,country_name ");     // 7
            query.append("       ,division_id ");      // 8
            query.append("       ,division_desc ");    // 9
            query.append("       ,room_type_id ");     // 10
            query.append("       ,room_type_desc ");   // 11
            query.append("       ,eligible ");         // 12
            query.append("       ,unrated) ");         // 13
            query.append("VALUES (");
            query.append("?,?,?,?,?,?,?,?,?,?,");  // 10 values
            query.append("?,?,?)");                // 13 total values
            psIns = prepareStatement(query.toString(), TARGET_DB);

            query = new StringBuffer(100);
            query.append("UPDATE room ");
            query.append("   SET round_id = ? ");          // 1
            query.append("       ,name = ? ");             // 2
            query.append("       ,state_code = ? ");       // 3
            query.append("       ,state_name = ? ");       // 4
            query.append("       ,country_code = ? ");     // 5
            query.append("       ,country_name = ? ");     // 6
            query.append("       ,division_id = ? ");      // 7
            query.append("       ,division_desc = ? ");    // 8
            query.append("       ,room_type_id = ? ");     // 9
            query.append("       ,room_type_desc = ? ");   // 10
            query.append("       ,eligible = ? ");         // 11
            query.append("       ,unrated = ? ");          // 12
            query.append(" WHERE room_id = ? ");           // 13
            psUpd = prepareStatement(query.toString(), TARGET_DB);

            query = new StringBuffer(100);
            query.append("SELECT 'pops' FROM room WHERE room_id = ?");
            psSel2 = prepareStatement(query.toString(), TARGET_DB);

            // On to the load
            psSel.setInt(1, fRoundId);
            rs = psSel.executeQuery();

            while (rs.next()) {
                int room_id = rs.getInt(1);
                psSel2.clearParameters();
                psSel2.setInt(1, room_id);
                rs2 = psSel2.executeQuery();

                // If next() returns true that means this row exists. If so,
                // we update. Otherwise, we insert.
                if (rs2.next()) {
                    psUpd.clearParameters();
                    psUpd.setInt(1, rs.getInt(2));  // round_id
                    psUpd.setString(2, rs.getString(3));  // name
                    psUpd.setString(3, rs.getString(4));  // state_code
                    psUpd.setString(4, rs.getString(5));  // state_name
                    psUpd.setString(5, rs.getString(6));  // country_code
                    psUpd.setString(6, rs.getString(7));  // country_name
                    psUpd.setInt(7, rs.getInt(8));  // division_id
                    psUpd.setString(8, rs.getString(9));  // division_desc
                    psUpd.setInt(9, rs.getInt(10));  // room_type_id
                    psUpd.setString(10, rs.getString(11));  // room_type_desc
                    psUpd.setInt(11, rs.getInt(12));  // eligible
                    psUpd.setInt(12, rs.getInt(13));  // unrated
                    psUpd.setInt(13, rs.getInt(1));  // room_id

                    retVal = psUpd.executeUpdate();
                    count += retVal;
                    if (retVal != 1) {
                        throw new SQLException("TCLoadRound: Update for room_id " +
                                room_id +
                                " modified " + retVal + " rows, not one.");
                    }
                } else {
                    psIns.clearParameters();
                    psIns.setInt(1, rs.getInt(1));  // room_id
                    psIns.setInt(2, rs.getInt(2));  // round_id
                    psIns.setString(3, rs.getString(3));  // name
                    psIns.setString(4, rs.getString(4));  // state_code
                    psIns.setString(5, rs.getString(5));  // state_name
                    psIns.setString(6, rs.getString(6));  // country_code
                    psIns.setString(7, rs.getString(7));  // country_name
                    psIns.setInt(8, rs.getInt(8));  // division_id
                    psIns.setString(9, rs.getString(9));  // division_desc
                    psIns.setInt(10, rs.getInt(10));  // room_type_id
                    psIns.setString(11, rs.getString(11));  // room_type_desc
                    psIns.setInt(12, rs.getInt(12));  // eligible
                    psIns.setInt(13, rs.getInt(13));  // unrated

                    retVal = psIns.executeUpdate();
                    count += retVal;
                    if (retVal != 1) {
                        throw new SQLException("TCLoadRound: Insert for room_id " +
                                room_id +
                                " modified " + retVal + " rows, not one.");
                    }
                }

                close(rs2);
                printLoadProgress(count, "room");
            }

            log.info("Room records copied = " + count);
        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Load of 'room' table failed.\n" +
                    sqle.getMessage());
        } finally {
            close(rs);
            close(rs2);
            close(psSel);
            close(psSel2);
            close(psIns);
            close(psUpd);
        }
    }

    /**
     * This loads the 'room_result'. This is actually a partial load of
     * the 'room_result' table as three columns are not populated:
     * submission_points, problems_submitted and
     * point_standard_deviation. We get these later on in the aggregate
     * load.
     */
    private void loadRoomResult() throws Exception {
        int retVal = 0;
        int count = 0;
        PreparedStatement psSel = null;
        PreparedStatement psSel2 = null;
        PreparedStatement psIns = null;
        PreparedStatement psDel = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        StringBuffer query = null;
        int round_id = 0;
        int room_id = 0;
        int coder_id = 0;


        try {
            query = new StringBuffer(100);
            query.append("SELECT rr.round_id ");                              // 1
            query.append("       ,rr.room_id ");                              // 2
            query.append("       ,rr.coder_id ");                             // 3
            query.append("       ,rr.point_total ");                          // 4
            query.append("       ,rr.room_seed ");                            // 5
            query.append("       ,rp.paid ");                                 // 6
            query.append("       ,rr.old_rating ");                           // 7
            query.append("       ,rr.new_rating ");                           // 8
            query.append("       ,rr.room_placed ");                          // 9
            query.append("       ,rr.attended ");                             // 10
            query.append("       ,rr.advanced ");                             // 11
            query.append("       ,(SELECT sum(c.challenger_points) ");        // 12
            query.append("           FROM challenge c ");
            query.append("          WHERE c.round_id = rr.round_id ");
            query.append("          AND c.status_id <> " + CHALLENGE_NULLIFIED);
            query.append("          AND c.challenger_id = rr.coder_id) ");
            query.append("       ,(SELECT sum(deduction_amount) ");           // 13
            query.append("           FROM system_test_result str ");
            query.append("          WHERE str.round_id = rr.round_id ");
            query.append("            AND str.coder_id = rr.coder_id) ");
            query.append("       ,(SELECT division_id FROM room ");           // 14
            query.append("          WHERE room.room_id = rr.room_id) ");
            query.append("       ,(SELECT count(*) ");                        // 15
            query.append("           FROM round_component rp ");
            query.append("                ,room r ");
            query.append("          WHERE rp.round_id = rr.round_id ");
            query.append("            AND rp.division_id = r.division_id ");
            query.append("            AND rr.room_id = r.room_id) ");
            query.append("       ,(SELECT count(*) FROM component_state cs ");  // 16
            query.append("          WHERE cs.round_id = rr.round_id ");
            query.append("            AND cs.coder_id = rr.coder_id ");
            query.append("            AND cs.status_id = " + STATUS_PASSED_SYS_TEST + ") ");
            query.append("       ,(SELECT count(*) FROM component_state cs ");  // 17
            query.append("          WHERE cs.round_id = rr.round_id ");
            query.append("            AND cs.coder_id = rr.coder_id ");
            query.append("            AND cs.status_id = " + STATUS_FAILED_SYS_TEST + ") ");
            query.append("       ,(SELECT count(*) FROM challenge c ");       // 18
            query.append("          WHERE c.round_id = rr.round_id ");
            query.append("          AND c.status_id <> " + CHALLENGE_NULLIFIED);
            query.append("            AND c.defendant_id = rr.coder_id ");
            query.append("            AND c.succeeded = " + STATUS_SUCCEEDED + ") ");
            query.append("       ,(SELECT count(*) FROM component_state cs ");  // 19
            query.append("          WHERE cs.round_id = rr.round_id ");
            query.append("            AND cs.coder_id = rr.coder_id) ");
            query.append("       ,(SELECT count(*) FROM component_state cs ");  // 20
            query.append("          WHERE cs.round_id = rr.round_id ");
            query.append("            AND cs.coder_id = rr.coder_id ");
            query.append("            AND cs.status_id = " + STATUS_OPENED + ") ");
            query.append("       ,(SELECT count(*) FROM challenge c ");       // 21
            query.append("          WHERE c.challenger_id = rr.coder_id ");
            query.append("          AND c.status_id <> " + CHALLENGE_NULLIFIED);
            query.append("            AND c.round_id = rr.round_id) ");
            query.append("       ,(SELECT count(*) FROM challenge c ");       // 22
            query.append("          WHERE c.challenger_id = rr.coder_id ");
            query.append("          AND c.status_id <> " + CHALLENGE_NULLIFIED);
            query.append("            AND c.round_id = rr.round_id ");
            query.append("            AND c.succeeded = " + STATUS_SUCCEEDED + ") ");
            query.append("       ,(SELECT count(*) FROM challenge c ");       // 23
            query.append("          WHERE c.challenger_id = rr.coder_id ");
            query.append("          AND c.status_id <> " + CHALLENGE_NULLIFIED);
            query.append("            AND c.round_id = rr.round_id ");
            query.append("            AND c.succeeded = " + STATUS_FAILED + ") ");
            query.append("       ,(SELECT count(*) FROM challenge c ");       // 24
            query.append("          WHERE c.defendant_id = rr.coder_id ");
            query.append("          AND c.status_id <> " + CHALLENGE_NULLIFIED);
            query.append("            AND c.round_id = rr.round_id) ");
            query.append("       ,(SELECT count(*) FROM challenge c ");       // 25
            query.append("          WHERE c.defendant_id = rr.coder_id ");
            query.append("          AND c.status_id <> " + CHALLENGE_NULLIFIED);
            query.append("            AND c.round_id = rr.round_id ");
            query.append("            AND c.succeeded = " + STATUS_SUCCEEDED + ") ");
            query.append("       ,(SELECT count(*) FROM challenge c ");       // 26
            query.append("          WHERE c.defendant_id = rr.coder_id ");
            query.append("          AND c.status_id <> " + CHALLENGE_NULLIFIED);
            query.append("            AND c.round_id = rr.round_id ");
            query.append("            AND c.succeeded = " + STATUS_FAILED + ") ");
            query.append("       ,(SELECT sum(c.defendant_points) ");           // 27
            query.append("           FROM challenge c ");
            query.append("          WHERE c.round_id = rr.round_id ");
            query.append("          AND c.status_id <> " + CHALLENGE_NULLIFIED);
            query.append("            AND c.defendant_id = rr.coder_id) ");
            query.append("       ,rr.overall_rank ");                        // 28
            query.append("       ,rr.division_placed ");                     // 29
            query.append("       ,rr.division_seed ");                       // 30
            query.append("       ,pt.payment_type_id");                      // 31
            query.append("       ,pt.payment_type_desc");                    // 32
            query.append("       ,rr.rated_flag");                           //33
            query.append("       ,rr.team_points");                          //34
            query.append("       ,rreg.team_id ");        // 35
            query.append("       ,rr.region_placed ");                       // 36
            query.append("  FROM room_result rr ");
            query.append("  JOIN room r ON rr.round_id = r.round_id ");
            query.append("   AND rr.room_id = r.room_id ");
            query.append("  LEFT OUTER JOIN round_payment rp ON rr.round_id = rp.round_id");
            query.append("              AND rp.coder_id = rr.coder_id");
            query.append("  LEFT OUTER JOIN payment_type_lu pt ON rp.payment_type_id = pt.payment_type_id");
            query.append("  JOIN round_registration rreg ON rreg.round_id = r.round_id and rreg.coder_id = rr.coder_id ");
            query.append(" WHERE r.room_type_id = " + CONTEST_ROOM);
            query.append("   AND rr.round_id = ?");
            query.append("   AND rr.attended = 'Y'");
            query.append("   AND NOT EXISTS ");
            query.append("       (SELECT 'pops' ");
            query.append("          FROM user_group_xref ugx ");
            query.append("         WHERE ugx.login_id= rr.coder_id ");
            query.append("           AND ugx.group_id = 2000115)");
            query.append("   AND NOT EXISTS ");
            query.append("       (SELECT 'pops' ");
            query.append("          FROM group_user gu ");
            query.append("         WHERE gu.user_id = rr.coder_id ");
            query.append("           AND gu.group_id = 13)");


            psSel = prepareStatement(query.toString(), SOURCE_DB);

            int algoType = getRoundType(fRoundId);

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
            query.append(" and (c2.date < c1.date or (c2.date = c1.date and r2.round_id < r1.round_id))");
            query.append(" group by rr2.coder_id");

            psSel2 = prepareStatement(query.toString(), TARGET_DB);

            query = new StringBuffer(100);
            query.append("INSERT INTO room_result ");
            query.append("      (round_id ");                         // 1
            query.append("       ,room_id ");                         // 2
            query.append("       ,coder_id ");                        // 3
            query.append("       ,final_points ");                    // 4
            query.append("       ,room_seed ");                       // 5
            query.append("       ,paid ");                            // 6
            query.append("       ,old_rating ");                      // 7
            query.append("       ,new_rating ");                      // 8
            query.append("       ,room_placed ");                     // 9
            query.append("       ,attended ");                        // 10
            query.append("       ,advanced ");                        // 11
            query.append("       ,challenge_points ");                // 12
            query.append("       ,system_test_points ");              // 13
            query.append("       ,division_id ");                     // 14
            query.append("       ,problems_presented ");              // 15
            query.append("       ,problems_correct ");                // 16
            query.append("       ,problems_failed_by_system_test ");  // 17
            query.append("       ,problems_failed_by_challenge ");    // 18
            query.append("       ,problems_opened ");                 // 19
            query.append("       ,problems_left_open ");              // 20
            query.append("       ,challenge_attempts_made ");         // 21
            query.append("       ,challenges_made_successful ");      // 22
            query.append("       ,challenges_made_failed ");          // 23
            query.append("       ,challenge_attempts_received ");     // 24
            query.append("       ,challenges_received_successful ");  // 25
            query.append("       ,challenges_received_failed ");      // 26
            query.append("       ,defense_points ");                  // 27
            query.append("       ,overall_rank ");                    // 28
            query.append("       ,division_placed ");                 // 29
            query.append("       ,division_seed ");                   // 30
            query.append("       ,payment_type_id ");                 // 31
            query.append("       ,payment_type_desc ");               // 32
            query.append("       ,num_ratings ");                     // 33
            query.append("       ,rated_flag ");                      // 34
            query.append("       ,team_points ");                     // 35
            query.append("       ,team_id  ");                        // 36
            query.append("       ,region_placed ");                   // 37
            query.append("       ,old_rating_id ");                  // 38
            query.append("       ,new_rating_id) ");                  //39
            query.append("VALUES (?,?,?,?,?,?,?,?,?,?,");  // 10 values
            query.append("        ?,?,?,?,?,?,?,?,?,?,");  // 20 values
            query.append("        ?,?,?,?,?,?,?,?,?,?,");  // 30 values
            query.append("        ?,?,?,?,?,?,?,?,?)");       // 39 total values
            psIns = prepareStatement(query.toString(), TARGET_DB);

            query = new StringBuffer(100);
            query.append("DELETE FROM room_result ");
            query.append(" WHERE round_id = ? ");
            query.append("   AND room_id = ? ");
            query.append("   AND coder_id = ? ");
            psDel = prepareStatement(query.toString(), TARGET_DB);

            // On to the load
            psSel.setInt(1, fRoundId);
            rs = psSel.executeQuery();

            psSel2.setInt(1, fRoundId);
            rs2 = psSel2.executeQuery();

            HashMap ratingsMap = new HashMap();

            while (rs2.next()) {
                ratingsMap.put(new Long(rs2.getLong("coder_id")), new Integer(rs2.getInt("count")));
            }

            while (rs.next()) {
                round_id = rs.getInt(1);
                room_id = rs.getInt(2);
                coder_id = rs.getInt(3);

                psDel.clearParameters();
                psDel.setInt(1, round_id);
                psDel.setInt(2, room_id);
                psDel.setInt(3, coder_id);
                psDel.executeUpdate();

                psIns.clearParameters();
                psIns.setInt(1, rs.getInt(1));  // round_id
                psIns.setInt(2, rs.getInt(2));  // room_id
                psIns.setInt(3, rs.getInt(3));  // coder_id
                psIns.setFloat(4, rs.getFloat(4));  // point_total
                psIns.setInt(5, rs.getInt(5));  // room_seed
                psIns.setFloat(6, rs.getFloat(6));  // paid
                psIns.setInt(7, rs.getInt(7));  // old_rating
                psIns.setInt(8, rs.getInt(8));  // new_rating
                psIns.setInt(9, rs.getInt(9));  // room_placed
                psIns.setString(10, rs.getString(10));  // attended
                psIns.setString(11, rs.getString(11));  // advanced
                psIns.setFloat(12, rs.getFloat(12));  // challenge_points
                psIns.setFloat(13, rs.getFloat(13));  // system_test_points
                psIns.setInt(14, rs.getInt(14));  // division_id
                psIns.setInt(15, rs.getInt(15));  // problems_presented
                psIns.setInt(16, rs.getInt(16));  // problems_correct
                psIns.setInt(17, rs.getInt(17));  // problems_failed_by_system_test
                psIns.setInt(18, rs.getInt(18));  // problems_failed_by_challenge
                psIns.setInt(19, rs.getInt(19));  // problems_opened
                psIns.setInt(20, rs.getInt(20));  // problems_left_open
                psIns.setInt(21, rs.getInt(21));  // challenge_attempts_made
                psIns.setInt(22, rs.getInt(22));  // challenges_made_successful
                psIns.setInt(23, rs.getInt(23));  // challenges_made_failed
                psIns.setInt(24, rs.getInt(24));  // challenge_attempts_received
                psIns.setInt(25, rs.getInt(25));  // challenges_received_successful
                psIns.setInt(26, rs.getInt(26));  // challenges_received_failed
                psIns.setFloat(27, rs.getFloat(27));  // defense_points
                psIns.setInt(28, rs.getInt(28));  // overall_rank
                psIns.setInt(29, rs.getInt(29));  // division_placed
                psIns.setInt(30, rs.getInt(30));  // division_seed
                if (rs.getInt("payment_type_id") == 0) {  //it's null
                    psIns.setNull(31, java.sql.Types.DECIMAL);
                    psIns.setNull(32, java.sql.Types.VARCHAR);
                } else {
                    psIns.setInt(31, rs.getInt("payment_type_id"));
                    psIns.setString(32, rs.getString("payment_type_desc"));
                }
                int numRatings = 0;
                Long tempCoderId = new Long(coder_id);
                if (ratingsMap.containsKey(tempCoderId))
                    numRatings = ((Integer) ratingsMap.get(tempCoderId)).intValue();
                psIns.setInt(33, rs.getInt("rated_flag") == 1 ? numRatings + 1 : numRatings);
                psIns.setInt(34, rs.getInt("rated_flag"));

                if (rs.getString("team_points") == null) {
                    psIns.setNull(35, java.sql.Types.DECIMAL);
                } else {
                    psIns.setInt(35, rs.getInt("team_points"));
                }

                if (rs.getString("team_id") == null) {
                    psIns.setNull(36, java.sql.Types.DECIMAL);
                } else {
                    psIns.setInt(36, rs.getInt("team_id"));
                }

                if (rs.getString("region_placed") == null) {
                    psIns.setNull(37, java.sql.Types.DECIMAL);
                } else {
                    psIns.setInt(37, rs.getInt("region_placed"));
                }
                //we can just use the rating because the id's and ratings match up.  may not be the case one day.
                psIns.setInt(38, rs.getInt("old_rating") == 0 ? -2 : rs.getInt("old_rating"));
                psIns.setInt(39, rs.getInt("new_rating") == 0 ? -2 : rs.getInt("new_rating"));


                retVal = psIns.executeUpdate();
                count += retVal;
                if (retVal != 1) {
                    throw new SQLException("TCLoadRound: Insert for coder_id " +
                            coder_id + ", round_id " + round_id +
                            ", room_id " + room_id +
                            " modified " + retVal + " rows, not one.");
                }

                printLoadProgress(count, "room_result");
            }

            log.info("Room_result records copied = " + count);
        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Load of 'room_result' table failed for coder_id " +
                    coder_id + ", round_id " + round_id +
                    ", room_id " + room_id + "\n" +
                    sqle.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            close(rs);
            close(rs2);
            close(psSel);
            close(psSel2);
            close(psIns);
            close(psDel);
        }
    }

    /**
     * This loads the 'coder_problem' table. This is a new table
     * which reports the results of each problem by coder.
     */
    private void loadCoderProblem() throws Exception {
        int retVal = 0;
        int count = 0;
        PreparedStatement psSel = null;
        PreparedStatement psSelOpenSubmitOrder = null;
        PreparedStatement psSel2 = null;
        PreparedStatement psIns = null;
        PreparedStatement psDel = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        StringBuffer query = null;

        int coder_id = 0;
        int round_id = 0;
        int division_id = 0;
        int problem_id = 0;
        int component_id = 0;

        try {
            query = new StringBuffer(100);
            query.append("SELECT cs.coder_id ");                                 // 1
            query.append("       ,cs.round_id ");                                // 2
            // 3: division_id
            query.append("       ,(SELECT r.division_id ");                      // 3
            query.append("           FROM room r ");
            query.append("                ,room_result rr ");
            query.append("          WHERE rr.coder_id = cs.coder_id ");
            query.append("            AND rr.round_id = cs.round_id ");
            query.append("            AND r.room_type_id = " + CONTEST_ROOM);
            query.append("            AND r.room_id = rr.room_id) ");
            query.append("       ,(SELECT comp.problem_id FROM component comp WHERE cs.component_id = comp.component_id)");                              // 4
            query.append("       ,s.submission_points ");                        // 5
            query.append("       ,cs.points ");                                  // 6
            query.append("       ,cs.status_id ");                               // 7
            // 8: end_status_text
            query.append("       ,(SELECT status_desc ");                        // 8
            query.append("           FROM component_status_lu ");
            query.append("          WHERE component_status_id = cs.status_id) ");
            query.append("       ,c.open_time ");                                // 9
            query.append("       ,s.submit_time ");                              // 10
            query.append("       ,s.submit_time - c.open_time ");                // 11
            query.append(" ,CASE WHEN s.language_id is null THEN c.language_id ELSE s.language_id END as language_id");         //12
//            query.append("       ,cs.language_id ");                             // 12
            // 13: challenge_points
            query.append("       ,(SELECT sum(c.challenger_points) ");           // 13
            query.append("           FROM challenge c ");
            query.append("          WHERE c.round_id = cs.round_id ");
            query.append("          AND c.status_id <> " + CHALLENGE_NULLIFIED);
            query.append("            AND c.challenger_id = cs.coder_id ");
            query.append("            AND c.component_id = cs.component_id) ");
            // 14: system_test_points
            query.append("       ,(SELECT sum(deduction_amount) ");              // 14
            query.append("           FROM system_test_result str ");
            query.append("          WHERE str.round_id = cs.round_id ");
            query.append("            AND str.coder_id = cs.coder_id ");
            query.append("            AND str.component_id = cs.component_id) ");
            // 15: defense_points
            query.append("       ,(SELECT sum(defendant_points) ");              // 15
            query.append("           FROM challenge c ");
            query.append("          WHERE c.round_id = cs.round_id ");
            query.append("          AND c.status_id <> " + CHALLENGE_NULLIFIED);
            query.append("            AND c.defendant_id = cs.coder_id ");
            query.append("            AND c.component_id = cs.component_id) ");
            query.append("       ,(SELECT rs.end_time");                         // 16
            query.append("           FROM round_segment rs");
            query.append("          WHERE rs.round_id = cs.round_id");
            query.append("            AND rs.segment_id = 2)");                  // coding segment...need constant
            query.append("       ,cs.component_id ");
            query.append("       ,(select sum(processing_time) ");
            query.append("           from system_test_result str ");
            query.append("          where str.coder_id = cs.coder_id");
            query.append("            and str.round_id = cs.round_id");
            query.append("            and str.component_id = cs.component_id)");
            query.append(" FROM component_state cs");
            query.append(" LEFT OUTER JOIN submission s ");
            query.append(" ON cs.component_state_id = s.component_state_id");
            query.append(" AND s.submission_number = cs.submission_number");
            query.append(" LEFT OUTER JOIN compilation c ");
            query.append(" ON cs.component_state_id = c.component_state_id");
            query.append(" JOIN room_result rr ");
            query.append(" ON rr.round_id = cs.round_id");
            query.append(" AND rr.coder_id = cs.coder_id");
            query.append(" WHERE cs.round_id = ?");
            query.append("   AND rr.attended = 'Y'");
            query.append("   AND NOT EXISTS ");
            query.append("       (SELECT 'pops' ");
            query.append("          FROM user_group_xref ugx ");
            query.append("         WHERE ugx.login_id= cs.coder_id ");
            query.append("           AND ugx.group_id = 2000115)");
            query.append("   AND NOT EXISTS ");
            query.append("       (SELECT 'pops' ");
            query.append("          FROM group_user gu ");
            query.append("         WHERE gu.user_id = cs.coder_id ");
            query.append("           AND gu.group_id = 13)");


            psSel = prepareStatement(query.toString(), SOURCE_DB);

            query = new StringBuffer(100);
            query.append("SELECT rp.open_order ");     // 1
            query.append("       ,rp.submit_order ");  // 2
            query.append("  FROM round_component rp ");
            query.append(" WHERE rp.component_id = ? ");
            query.append("   AND rp.round_id = ? ");
            query.append("   AND rp.division_id = ? ");
            psSelOpenSubmitOrder = prepareStatement(query.toString(), SOURCE_DB);

            query = new StringBuffer(100);
            query.append("SELECT rp.difficulty_id ");    // 1
            query.append("       ,d.difficulty_desc ");  // 2
            query.append("  FROM round_component rp ");
            query.append("       ,difficulty d ");
            query.append(" WHERE rp.component_id = ? ");
            query.append("   AND rp.division_id = ? ");
            query.append("   AND rp.round_id = ? ");
            query.append("   AND rp.difficulty_id = d.difficulty_id ");
            psSel2 = prepareStatement(query.toString(), SOURCE_DB);

            // Need to add these in later if we determine we need them
            query = new StringBuffer(100);
            query.append("INSERT INTO coder_problem ");
            query.append("      (coder_id ");             // 1
            query.append("       ,round_id ");            // 2
            query.append("       ,division_id ");         // 3
            query.append("       ,problem_id ");          // 4
            query.append("       ,submission_points ");   // 5
            query.append("       ,final_points ");        // 6
            query.append("       ,end_status_id ");       // 7
            query.append("       ,end_status_text ");     // 8
            query.append("       ,open_time ");           // 9
            query.append("       ,submit_time ");         // 10
            query.append("       ,time_elapsed ");        // 11
            query.append("       ,language_id ");         // 12
            query.append("       ,challenge_points ");    // 13
            query.append("       ,system_test_points ");  // 14
            query.append("       ,defense_points ");      // 15
            query.append("       ,open_order ");          // 16
            query.append("       ,submit_order ");        // 17
            query.append("       ,level_id ");            // 18
            query.append("       ,level_desc ");          // 19
            query.append("       ,total_execution_time) ");//20
            query.append("VALUES (");
            query.append("?,?,?,?,?,?,?,?,?,?,");  // 10 values
            query.append("?,?,?,?,?,?,?,?,?,?)");    // 20 total values
            psIns = prepareStatement(query.toString(), TARGET_DB);

            query = new StringBuffer(100);
            query.append("DELETE FROM coder_problem ");
            query.append(" WHERE coder_id = ? ");
            query.append("   AND round_id = ? ");
            query.append("   AND division_id = ? ");
            query.append("   AND problem_id = ?");
            psDel = prepareStatement(query.toString(), TARGET_DB);

            // On to the load
            psSel.setInt(1, fRoundId);
            rs = psSel.executeQuery();


            while (rs.next()) {
                coder_id = rs.getInt(1);
                round_id = rs.getInt(2);
                division_id = rs.getInt(3);
                problem_id = rs.getInt(4);
                component_id = rs.getInt("component_id");
                // if they didn't submit, use the difference between open time and the end of the coding phase
                // otherwise use the difference between open time and submit time
                if (coder_id == 20525058) {
                    log.info("submit time: " + rs.getLong(10));
                    log.info("round end time: " + rs.getTimestamp(16));
                    log.info("round end time: " + rs.getTimestamp(16).getTime());
                    log.info("open time: " + rs.getLong(9));
                    log.info("elapsed: " + rs.getLong(11));
                }
                long elapsed_time = rs.getLong(10) == 0 ? rs.getTimestamp(16).getTime() - rs.getLong(9) : rs.getLong(11);
                if (elapsed_time < 0) {
                    elapsed_time = 0;
                }

                psSel2.clearParameters();
                psSel2.setInt(1, component_id);
                psSel2.setInt(2, division_id);
                psSel2.setInt(3, fRoundId);
                int level_id = -1;
                String level_desc = null;
                rs2 = psSel2.executeQuery();

                // Get level_id and level_desc
                if (rs2.next()) {
                    level_id = rs2.getInt(1);
                    level_desc = rs2.getString(2);
                } else {
                    throw new SQLException("Unable to find level_id and level_desc " +
                            "for problem_id " + problem_id +
                            " and division_id " + division_id);
                }

                close(rs2);

                // Get open_order and submit_order
                psSelOpenSubmitOrder.clearParameters();
                psSelOpenSubmitOrder.setInt(1, component_id);
                psSelOpenSubmitOrder.setInt(2, fRoundId);
                psSelOpenSubmitOrder.setInt(3, division_id);
                rs2 = psSelOpenSubmitOrder.executeQuery();

                int open_order = 0, submit_order = 0;
                if (rs2.next()) {
                    open_order = rs2.getInt(1);
                    submit_order = rs2.getInt(2);
                } else {
                    throw new SQLException("Unable to find open_order and submit_order " +
                            "for problem_id " + problem_id +
                            " and division_id " + division_id);
                }

                close(rs2);

                psDel.clearParameters();
                psDel.setInt(1, coder_id);
                psDel.setInt(2, round_id);
                psDel.setInt(3, division_id);
                psDel.setInt(4, problem_id);
                psDel.executeUpdate();

                psIns.clearParameters();
                psIns.setInt(1, rs.getInt(1));  // coder_id
                psIns.setInt(2, rs.getInt(2));  // round_id
                psIns.setInt(3, rs.getInt(3));  // division_id
                psIns.setInt(4, rs.getInt(4));  // problem_id
                psIns.setFloat(5, rs.getFloat(5));  // submission_points
                psIns.setFloat(6, rs.getFloat(6));  // final_points
                psIns.setInt(7, rs.getInt(7));  // end_status_id
                psIns.setString(8, rs.getString(8));  // end_status_text
                psIns.setLong(9, rs.getLong(9));  // open_time
                psIns.setLong(10, rs.getLong(10));  // submit_time
                psIns.setLong(11, elapsed_time);  // time_elapsed
                psIns.setInt(12, rs.getInt(12));  // language_id
                psIns.setFloat(13, rs.getFloat(13));  // challenge_points
                psIns.setFloat(14, rs.getFloat(14));  // system_test_points
                psIns.setFloat(15, rs.getFloat(15));  // defense_points
                psIns.setInt(16, open_order);  // open_order
                psIns.setInt(17, submit_order);  // submit_order
                psIns.setInt(18, level_id);
                psIns.setString(19, level_desc);
                psIns.setInt(20, rs.getInt(17)); //processing time

                retVal = psIns.executeUpdate();
                count += retVal;
                if (retVal != 1) {
                    throw new SQLException("TCLoadRound: Insert for coder_id " +
                            coder_id + ", round_id " + round_id +
                            ", division_id " + division_id +
                            ", problem_id " + problem_id +
                            " modified " + retVal + " rows, not one.");
                }

                printLoadProgress(count, "coder_problem");
            }

            log.info("Coder_problem records copied = " + count);
        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Load of 'coder_problem' table failed.\n" +
                    "coder: " + coder_id + ", round_id " + round_id +
                    ", division_id " + division_id +
                    ", problem_id " + problem_id +
                    sqle.getMessage());
        } finally {
            close(rs);
            close(rs2);
            close(psSel);
            close(psSel2);
            close(psIns);
            close(psDel);
        }
    }

    /**
     * This populates the 'challenge' table
     */
    private void loadChallenge() throws Exception {
        int retVal = 0;
        int count = 0;
        PreparedStatement psSel = null;
        PreparedStatement psIns = null;
        PreparedStatement psDel = null;
        ResultSet rs = null;
        StringBuffer query = null;

        try {
            query = new StringBuffer(100);
            query.append("SELECT chal.challenge_id ");        // 1
            query.append("       ,chal.defendant_id ");       // 2
            query.append("       ,comp.problem_id ");         // 3
            query.append("       ,chal.round_id ");           // 4
            query.append("       ,chal.succeeded ");          // 5
            query.append("       ,chal.submit_time ");        // 6
            query.append("       ,chal.challenger_id ");      // 7
            query.append("       ,chal.args ");               // 8
            query.append("       ,chal.message ");            // 9
            query.append("       ,chal.challenger_points ");  // 10
            query.append("       ,chal.defendant_points ");   // 11
            query.append("       ,chal.expected ");           // 12
            query.append("       ,chal.received ");           // 13
            query.append("       , (select start_time from round_segment where round_id = chal.round_id and segment_id = 4) as start_time"); //14
            query.append("  FROM challenge chal, component comp");
            query.append(" WHERE chal.round_id = ? ");
            query.append("   AND chal.component_id = comp.component_id");
            query.append("   AND chal.status_id <> " + CHALLENGE_NULLIFIED);
            query.append("   AND NOT EXISTS ");
            query.append("       (SELECT 'pops' ");
            query.append("          FROM user_group_xref ugx ");
            query.append("         WHERE ugx.login_id= chal.challenger_id ");
            query.append("           AND ugx.group_id = 2000115)");
            query.append("   AND NOT EXISTS ");
            query.append("       (SELECT 'pops' ");
            query.append("          FROM user_group_xref ugx ");
            query.append("         WHERE ugx.login_id= chal.defendant_id ");
            query.append("           AND ugx.group_id = 2000115)");


            psSel = prepareStatement(query.toString(), SOURCE_DB);

            query = new StringBuffer(100);
            query.append("INSERT INTO challenge ");
            query.append("      (challenge_id ");        // 1
            query.append("       ,defendant_id ");       // 2
            query.append("       ,problem_id ");         // 3
            query.append("       ,round_id ");           // 4
            query.append("       ,succeeded ");          // 5
            query.append("       ,submit_time ");        // 6
            query.append("       ,challenger_id ");      // 7
            query.append("       ,args ");               // 8
            query.append("       ,message ");            // 9
            query.append("       ,challenger_points ");  // 10
            query.append("       ,defendant_points ");   // 11
            query.append("       ,expected ");           // 12
            query.append("       ,received ");           // 13
            query.append("       ,time_elapsed)");      // 14
            query.append("VALUES (");
            query.append("?,?,?,?,?,?,?,?,?,?,");  // 10 values
            query.append("?,?,?,?)");                // 14 total values
            psIns = prepareStatement(query.toString(), TARGET_DB);

            query = new StringBuffer(100);
            query.append("DELETE FROM challenge ");
            query.append(" WHERE round_id = ? ");
            psDel = prepareStatement(query.toString(), TARGET_DB);

            // On to the load
            psSel.setInt(1, fRoundId);
            rs = psSel.executeQuery();

            // First thing we do is delete all the challenge entries for this round
            psDel.setInt(1, fRoundId);
            psDel.executeUpdate();

            while (rs.next()) {
                psIns.clearParameters();
                psIns.setInt(1, rs.getInt(1));  // challenge_id
                psIns.setInt(2, rs.getInt(2));  // defendant_id
                psIns.setInt(3, rs.getInt(3));  // problem_id
                psIns.setInt(4, rs.getInt(4));  // round_id
                psIns.setInt(5, rs.getInt(5));  // succeeded
                psIns.setLong(6, rs.getLong(6));  // submit_time
                psIns.setInt(7, rs.getInt(7));  // challenger_id
                setBytes(psIns, 8, getBlobObject(rs, 8));  // args
                psIns.setString(9, rs.getString(9));  // message
                psIns.setFloat(10, rs.getFloat(10));  // challenger_points
                psIns.setFloat(11, rs.getFloat(11));  // defendant_points
                setBytes(psIns, 12, getBlobObject(rs, 12));  // expected
                setBytes(psIns, 13, getBlobObject(rs, 13));  // received
                psIns.setLong(14, rs.getLong(6)-rs.getTimestamp("start_time").getTime());

                retVal = psIns.executeUpdate();
                count += retVal;
                if (retVal != 1) {
                    throw new SQLException("TCLoadRound: Insert for challenge_id " +
                            rs.getInt(1) +
                            " modified " + retVal + " rows, not one.");
                }

                printLoadProgress(count, "challenge");
            }

            log.info("Challenge records copied = " + count);
        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Load of 'challenge' table failed.\n" +
                    sqle.getMessage());
        } finally {
            close(rs);
            close(psSel);
            close(psIns);
            close(psDel);
        }
    }


    /**
     * This populates the 'challenge' table
     */
    private void loadProblemCategory() throws Exception {
        int retVal = 0;
        int count = 0;
        PreparedStatement psSel = null;
        PreparedStatement psIns = null;
        PreparedStatement psDel = null;
        ResultSet rs = null;
        StringBuffer query = null;

        try {
            query = new StringBuffer(100);
            query.append(" select distinct p.problem_id");
            query.append("      , cc.component_category_id");
            query.append(" from problem p");
            query.append(" , component c");
            query.append(" , component_category_xref cc");
            query.append(" , round_component rc");
            query.append(" where cc.component_id = c.component_id");
            query.append(" and c.problem_id = p.problem_id");
            query.append(" and c.component_id = rc.component_id");
            query.append(" and rc.round_id = ?");

            psSel = prepareStatement(query.toString(), SOURCE_DB);

            query = new StringBuffer(100);
            query.append("INSERT INTO problem_category_xref");
            query.append("      (problem_id ");        // 1
            query.append("       ,problem_category_id) ");       // 2
            query.append("VALUES (");
            query.append("?,?)");  // 2 values
            psIns = prepareStatement(query.toString(), TARGET_DB);

            query = new StringBuffer(100);
            query.append("DELETE FROM problem_category_xref");
            query.append(" WHERE problem_id in ( ");
            query.append(" select problem_id from problem where round_id = ?)");
            psDel = prepareStatement(query.toString(), TARGET_DB);

            // On to the load
            psSel.setInt(1, fRoundId);
            rs = psSel.executeQuery();

            // First thing we do is delete all the challenge entries for this round
            psDel.setInt(1, fRoundId);
            psDel.executeUpdate();

            while (rs.next()) {
                psIns.clearParameters();
                psIns.setLong(1, rs.getLong(1));  // problem_id
                psIns.setLong(2, rs.getLong(2));  // problem_category_id

                retVal = psIns.executeUpdate();
                count += retVal;
                if (retVal != 1) {
                    throw new SQLException("TCLoadRound: Insert for prbolem_id " +
                            rs.getLong(1) + " problem_category_id " + rs.getLong(2) +
                            " modified " + retVal + " rows, not one.");
                }

                printLoadProgress(count, "problem_category_xref");
            }

            log.info("Problem Category records copied = " + count);
        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Load of 'problem_category_xref' table failed.\n" +
                    sqle.getMessage());
        } finally {
            close(rs);
            close(psSel);
            close(psIns);
            close(psDel);
        }
    }


    /**
     * Load all the seasons from transactional.
     */
    private void loadSeasons() throws Exception {
        int retVal = 0;
        int count = 0;
        PreparedStatement psSel = null;
        PreparedStatement psSel2 = null;
        PreparedStatement psIns = null;
        PreparedStatement psUpd = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        StringBuffer query = null;

        try {
            query = new StringBuffer(100);
            query.append("SELECT s.season_id ");            // 1
            query.append("      ,s.start_date ");           // 2
            query.append("      ,s.end_date ");             // 3
            query.append("      ,s.name ");                 // 4
            query.append("      ,s.season_type_id ");       // 5
            query.append("      ,st.season_type_desc ");    // 6
            query.append("      FROM season s ");
            query.append("      ,season_type_lu st ");
            query.append("      WHERE s.season_type_id = st.season_type_id ");
            psSel = prepareStatement(query.toString(), SOURCE_DB);

            query = new StringBuffer(100);
            query.append("INSERT INTO season ");
            query.append("       (start_calendar_id ");   // 1
            query.append("       ,end_calendar_id ");     // 2
            query.append("       ,name ");                // 3
            query.append("       ,season_type_id ");      // 4
            query.append("       ,season_type_desc ");    // 5
            query.append("       ,season_id) ");          // 6
            query.append("VALUES (");
            query.append("?,?,?,?,?,?)");  // 6 values
            psIns = prepareStatement(query.toString(), TARGET_DB);

            query = new StringBuffer(100);
            query.append("UPDATE season ");
            query.append("   SET start_calendar_id = ?");   // 1
            query.append("      ,end_calendar_id = ?");     // 2
            query.append("      ,name = ?");                // 3
            query.append("      ,season_type_id = ?");      // 4
            query.append("      ,season_type_desc = ?");    // 5
            query.append(" WHERE season_id = ?");           // 6
            psUpd = prepareStatement(query.toString(), TARGET_DB);

            query = new StringBuffer(100);
            query.append("SELECT 1 ");
            query.append("  FROM season ");
            query.append(" WHERE season_id = ?");
            psSel2 = prepareStatement(query.toString(), TARGET_DB);

            // On to the load
            rs = psSel.executeQuery();

            while (rs.next()) {
                int season_id = rs.getInt(1);
                int start_calendar_id = lookupCalendarId(rs.getTimestamp(2), TARGET_DB);
                int end_calendar_id = lookupCalendarId(rs.getTimestamp(3), TARGET_DB);
                String name = rs.getString(4);
                int season_type_id = rs.getInt(5);
                String season_type_desc = rs.getString(6);

                psSel2.clearParameters();
                psSel2.setInt(1, season_id);
                rs2 = psSel2.executeQuery();

                // If next() returns true that means this row exists. If so,
                // we update. Otherwise, we insert.
                PreparedStatement psInsUpd = rs2.next() ? psUpd : psIns;

                psInsUpd.clearParameters();
                psInsUpd.setInt(1, start_calendar_id);
                psInsUpd.setInt(2, end_calendar_id);
                psInsUpd.setString(3, name);
                psInsUpd.setInt(4, season_type_id);
                psInsUpd.setString(5, season_type_desc);
                psInsUpd.setInt(6, season_id);

                retVal = psInsUpd.executeUpdate();
                count += retVal;
                if (retVal != 1) {
                    throw new SQLException("TCLoadRound: Insert or Update for season_id " + season_id +
                            " modified " + retVal + " rows, not one.");
                }


                printLoadProgress(count, "season");
            }

            log.info("season records copied = " + count);
        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Load of 'seasons' table failed.\n" +
                    sqle.getMessage());
        } finally {
            close(rs);
            close(psSel);
            close(psIns);
            close(psUpd);
        }
    }

    /**
     * Load the season_algo_rating table in DW.
     * This is based in the table of the same name in transactional, but has some additional information for
     * highest and lowest rating, first and last round rated and number of competitions.
     * The number of competitions is counted from room_results.
     * The other fields are the result of checking whether the previous values in the table in DW are smaller/bigger
     * than the values being inserted.
     * For example, if there is already a row for the coder in the season with highest rating = 2000 but the current
     * rating is 2100, the highest rating will be replaced.
     */
    private void loadSeasonRating() throws Exception {
        int count = 0;
        int retVal = 0;
        PreparedStatement psSel = null;
        PreparedStatement psSel2 = null;
        PreparedStatement psSelNumCompetitions = null;
        PreparedStatement psIns = null;
        PreparedStatement psDel = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        StringBuffer query = null;

        try {
            query = new StringBuffer(100);
            query.append("SELECT r.coder_id ");           // 1
            query.append("       ,r.rating ");            // 2
            query.append("       ,r.num_ratings ");       // 3
            query.append("       ,r.vol ");               // 4
            query.append("       ,r.season_id ");         // 5
            query.append("       ,r.round_id ");          // 6
            query.append("  FROM season_algo_rating r ");
            query.append("  WHERE r.modify_date > ? ");
            query.append("   AND NOT EXISTS ");
            query.append("       (SELECT 'pops' ");
            query.append("          FROM user_group_xref ugx ");
            query.append("         WHERE ugx.login_id= r.coder_id ");
            query.append("           AND ugx.group_id = 2000115)");
            query.append("   AND NOT EXISTS ");
            query.append("       (SELECT 'pops' ");
            query.append("          FROM group_user gu ");
            query.append("         WHERE gu.user_id = r.coder_id ");
            query.append("           AND gu.group_id = 13)");

            psSel = prepareStatement(query.toString(), SOURCE_DB);

            query = new StringBuffer(100);
            query.append("SELECT first_rated_round_id ");  // 1
            query.append("       ,last_rated_round_id ");  // 2
            query.append("       ,lowest_rating ");        // 3
            query.append("       ,highest_rating ");       // 4
            query.append("  FROM season_algo_rating ");
            query.append(" WHERE coder_id = ?");
            query.append(" AND season_id  = ?");
            psSel2 = prepareStatement(query.toString(), TARGET_DB);

            query = new StringBuffer(100);
            query.append("INSERT INTO season_algo_rating ");
            query.append("      (coder_id ");               // 1
            query.append("       ,season_id ");             // 2
            query.append("       ,rating ");                // 3
            query.append("       ,vol ");                   // 4
            query.append("       ,num_ratings ");           // 5
            query.append("       ,num_competitions ");      // 6
            query.append("       ,highest_rating ");        // 7
            query.append("       ,lowest_rating ");         // 8
            query.append("       ,first_rated_round_id ");  // 9
            query.append("       ,last_rated_round_id) ");   // 10
            query.append("VALUES (");
            query.append("?,?,?,?,?,?,?,?,?,?)");  // 10 values
            psIns = prepareStatement(query.toString(), TARGET_DB);

            query = new StringBuffer(100);
            query.append("SELECT count(*) ");     // 1
            query.append("  FROM room_result rr ");
            query.append("       ,round r ");
            query.append("       ,contest c ");
            query.append(" WHERE r.round_id = rr.round_id ");
            query.append("   AND r.contest_id = c.contest_id ");
            query.append("   AND rr.attended = 'Y' ");
            query.append("   AND rr.coder_id = ? ");
            query.append("   AND c.season_id = ? ");
            psSelNumCompetitions = prepareStatement(query.toString(), SOURCE_DB);


            query = new StringBuffer(100);
            query.append("DELETE FROM season_algo_rating where coder_id = ? ");
            query.append(" AND season_id = ?");
            psDel = prepareStatement(query.toString(), TARGET_DB);

            psSel.setTimestamp(1, fLastLogTime);
            rs = executeQuery(psSel, "loadRating");

            while (rs.next()) {
                int coder_id = rs.getInt(1);
                int rating = rs.getInt(2);
                int num_ratings = rs.getInt(3);
                int vol = rs.getInt(4);
                int season_id = rs.getInt(5);
                int round_id = rs.getInt(6);

                int num_competitions = 0;

                //use by default the loaded round id and rating, so that if there aren't other rounds, that round is the first and last
                // and the rating is the lowest and highest.
                int first_rated_round_id = round_id;
                int last_rated_round_id = round_id;
                int lowest_rating = rating;
                int highest_rating = rating;

                psSel2.clearParameters();
                psSel2.setInt(1, coder_id);
                psSel2.setInt(2, season_id);
                rs2 = psSel2.executeQuery();

                // if there was already a row for that coder in the season, check the min and max for round date and rating
                if (rs2.next()) {
                    if (rs2.getString(1) != null) {
                        first_rated_round_id = rs2.getInt(1);
                    }

                    if (rs2.getString(2) != null) {
                        last_rated_round_id = rs2.getInt(2);
                    }
                    lowest_rating = Math.min(rs2.getInt(3), rating);
                    highest_rating = Math.max(rs2.getInt(4), rating);

                    if (getRoundStart(round_id).compareTo(getRoundStart(first_rated_round_id)) < 0)
                        first_rated_round_id = round_id;

                    if (getRoundStart(round_id).compareTo(getRoundStart(last_rated_round_id)) > 0)
                        last_rated_round_id = round_id;

                    // clear the row
                    psDel.clearParameters();
                    psDel.setInt(1, coder_id);
                    psDel.setInt(2, season_id);
                    psDel.executeUpdate();
                }

                close(rs2);

                // Get the number of competitions
                psSelNumCompetitions.clearParameters();
                psSelNumCompetitions.setInt(1, coder_id);
                psSelNumCompetitions.setInt(2, season_id);
                rs2 = psSelNumCompetitions.executeQuery();
                if (rs2.next()) {
                    num_competitions = rs2.getInt(1);
                }

                close(rs2);

                psIns.clearParameters();
                psIns.setInt(1, coder_id);
                psIns.setInt(2, season_id);
                psIns.setInt(3, rating);
                psIns.setInt(4, vol);
                psIns.setInt(5, num_ratings);
                psIns.setInt(6, num_competitions);
                psIns.setInt(7, highest_rating);
                psIns.setInt(8, lowest_rating);
                psIns.setInt(9, first_rated_round_id);
                psIns.setInt(10, last_rated_round_id);

                retVal = psIns.executeUpdate();
                count = count + retVal;
                if (retVal != 1) {
                    throw new SQLException("TCLoadCoders: Insert for coder_id " +
                            coder_id +
                            " modified " + retVal + " rows, not one.");
                }

                printLoadProgress(count, "season_algo_rating");
            }

            log.info("Season Rating records copied = " + count);
        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Load of 'season_algo_rating' table failed.\n" +
                    sqle.getMessage());
        } finally {
            close(rs);
            close(psSel);
            close(psSel2);
            close(psSelNumCompetitions);
            close(psIns);
            close(psDel);
        }
    }



    /**
     * This loads the 'problem_tester' and problem_writer tables
     */
    private void loadProblemAuthors() throws Exception {
        int retVal = 0;
        int count = 0;
        PreparedStatement psSel = null;
        PreparedStatement psCheckWriter = null;
        PreparedStatement psCheckTester = null;
        PreparedStatement psInsWriter = null;
        PreparedStatement psInsTester = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        StringBuffer query = null;

        try {
            query = new StringBuffer(100);
            query.append(" SELECT x.user_id, x.user_type_id, c.problem_id  ");
            query.append(" FROM component_user_xref x, component c ");
            query.append(" WHERE c.component_id = x.component_id ");
            query.append(" AND c.component_id in (SELECT component_id FROM round_component WHERE round_id = ?) ");
            query.append(" AND x.user_type_id in (" + PROBLEM_WRITER_USER_TYPE_ID +", " + PROBLEM_TESTER_USER_TYPE_ID + ") ");
            
            psSel = prepareStatement(query.toString(), SOURCE_DB);

            query = new StringBuffer(100);
            query.append(" INSERT INTO problem_writer ");
            query.append(" (writer_id, problem_id) ");
            query.append(" VALUES (?,?)");
            psInsWriter = prepareStatement(query.toString(), TARGET_DB);

            query = new StringBuffer(100);
            query.append(" INSERT INTO problem_tester ");
            query.append(" (tester_id, problem_id) ");
            query.append(" VALUES (?,?)");
            psInsTester = prepareStatement(query.toString(), TARGET_DB);

            query = new StringBuffer(100);
            query.append("SELECT 1 FROM problem_writer WHERE writer_id =? AND problem_id=?");
            psCheckWriter = prepareStatement(query.toString(), TARGET_DB);

            query = new StringBuffer(100);
            query.append("SELECT 1 FROM problem_tester WHERE tester_id =? AND problem_id=?");
            psCheckTester = prepareStatement(query.toString(), TARGET_DB);

            psSel.setInt(1, fRoundId);
            rs = psSel.executeQuery();
            while (rs.next()) {
                long problem_id = rs.getLong("problem_id");
                long user_id = rs.getLong("user_id");
                int type = rs.getInt("user_type_id");
                
                if (type == PROBLEM_WRITER_USER_TYPE_ID) {
                	psCheckWriter.setLong(1, user_id);
                	psCheckWriter.setLong(2, problem_id);
                	rs2 = psCheckWriter.executeQuery();
                	if (rs2.next()) {
                		log.info("writer_id=" + user_id + ", problem_id=" + problem_id + " already loaded in DW");
                	} else {
                		psInsWriter.setLong(1, user_id);
                		psInsWriter.setLong(2, problem_id);
                    	retVal = psInsWriter.executeUpdate();
                        if (retVal != 1) {
                            throw new SQLException("loadProblemAuthors updated " + retVal +
                                    " rows, not just one.");
                        }                		
                    	count++;
                    	printLoadProgress(count, "problem writer/tester");
                	}                	
                } else { // Problem Tester
                	psCheckTester.setLong(1, user_id);
                	psCheckTester.setLong(2, problem_id);
                	rs2 = psCheckTester.executeQuery();
                	if (rs2.next()) {
                		log.info("tester_id=" + user_id + ", problem_id=" + problem_id + " already loaded in DW");
                	} else {
                		psInsTester.setLong(1, user_id);
                		psInsTester.setLong(2, problem_id);
                    	retVal = psInsTester.executeUpdate();
                        if (retVal != 1) {
                            throw new SQLException("loadProblemAuthors updated " + retVal +
                                    " rows, not just one.");
                        }                		
                    	count++;
                    	printLoadProgress(count, "problem writer/tester");
                	}                	                	
                }
                
            }

            log.info("problem writer/tester records copied = " + count);
        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Load of 'problem writer/tester' table failed.\n" +
                    sqle.getMessage());
        } finally {
            close(rs);
            close(rs2);
            close(psSel);
            close(psInsWriter);
            close(psInsTester);
            close(psCheckWriter);
            close(psCheckTester);
        }
    }


    /**
     * This method places the start time of the load into the update_log table
     */
    private void setLastUpdateTime() throws Exception {
        PreparedStatement psUpd = null;
        StringBuffer query = null;

        try {
            int retVal = 0;
            query = new StringBuffer(100);
            query.append("INSERT INTO update_log ");
            query.append("      (log_id ");        // 1
            query.append("       ,calendar_id ");  // 2
            query.append("       ,timestamp  ");   // 3
            query.append("       ,log_type_id) ");   // 4
            query.append("VALUES (0, ?, ?, " + ROUND_LOG_TYPE + ")");
            psUpd = prepareStatement(query.toString(), TARGET_DB);

            int calendar_id = lookupCalendarId(fStartTime, TARGET_DB);
            psUpd.setInt(1, calendar_id);
            psUpd.setTimestamp(2, fStartTime);

            retVal = psUpd.executeUpdate();
            if (retVal != 1) {
                throw new SQLException("SetLastUpdateTime updated " + retVal +
                        " rows, not just one.");
            }
        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Failed to set last log time.\n" +
                    sqle.getMessage());
        } finally {
            close(psUpd);
        }
    }

    private java.sql.Date getRoundStart(int roundId)
            throws SQLException {
        Integer iRoundId = new Integer(roundId);
        StringBuffer query = null;
        if (fRoundStartHT.get(iRoundId) != null)
            return (java.sql.Date) fRoundStartHT.get(iRoundId);

        query = new StringBuffer(100);
        query.append("SELECT rs.start_time ");
        query.append("  FROM round_segment rs ");
        query.append(" WHERE rs.round_id = ? ");
        query.append("   AND rs.segment_id = " + CODING_SEGMENT_ID);
        PreparedStatement pSel = prepareStatement(query.toString(), SOURCE_DB);

        pSel.setInt(1, roundId);
        ResultSet rs = pSel.executeQuery();

        if (rs.next()) {
            java.sql.Date date = rs.getDate(1);
            fRoundStartHT.put(new Integer(roundId), date);
            return date;
        } else {
            throw new SQLException("Unable to determine start for " + roundId);
        }
    }

}
