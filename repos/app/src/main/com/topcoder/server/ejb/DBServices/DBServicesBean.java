/*
* Copyright (C) - 2014 TopCoder Inc., All Rights Reserved.
*/
package com.topcoder.server.ejb.DBServices;

import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.ejb.EJBException;

import org.apache.commons.lang3.StringUtils;

import com.topcoder.netCommon.contest.ComponentAssignmentData;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contest.Question;
import com.topcoder.netCommon.contest.ResultDisplayType;
import com.topcoder.netCommon.contest.SurveyAnswerData;
import com.topcoder.netCommon.contest.SurveyChoiceData;
import com.topcoder.netCommon.contest.TeamConstants;
import com.topcoder.netCommon.contest.round.RoundCustomPropertiesImpl;
import com.topcoder.netCommon.contest.round.RoundType;
import com.topcoder.netCommon.contestantMessages.AdminBroadcast;
import com.topcoder.netCommon.contestantMessages.ComponentBroadcast;
import com.topcoder.netCommon.contestantMessages.RoundBroadcast;
import com.topcoder.netCommon.contestantMessages.response.data.CategoryData;
import com.topcoder.netCommon.contestantMessages.response.data.UserListItem;
import com.topcoder.security.Util;
import com.topcoder.server.AdminListener.response.CommandFailedResponse;
import com.topcoder.server.AdminListener.response.CommandResponse;
import com.topcoder.server.AdminListener.response.CommandSucceededResponse;
import com.topcoder.server.common.BaseCodingRoom;
import com.topcoder.server.common.BaseRound;
import com.topcoder.server.common.ChatEvent;
import com.topcoder.server.common.Coder;
import com.topcoder.server.common.CoderComponent;
import com.topcoder.server.common.CoderFactory;
import com.topcoder.server.common.CoderHistory;
import com.topcoder.server.common.CoderHistory.ChallengeCoder;
import com.topcoder.server.common.CoderHistory.ChallengeData;
import com.topcoder.server.common.ContestRoom;
import com.topcoder.server.common.ContestRound;
import com.topcoder.server.common.EventRegistration;
import com.topcoder.server.common.IndividualCoder;
import com.topcoder.server.common.LongCoderComponent;
import com.topcoder.server.common.LongContestCoder;
import com.topcoder.server.common.LongContestRoom;
import com.topcoder.server.common.Rating;
import com.topcoder.server.common.Registration;
import com.topcoder.server.common.RegistrationResult;
import com.topcoder.server.common.Results;
import com.topcoder.server.common.Room;
import com.topcoder.server.common.Round;
import com.topcoder.server.common.RoundEvent;
import com.topcoder.server.common.RoundFactory;
import com.topcoder.server.common.ServerContestConstants;
import com.topcoder.server.common.SurveyAnswer;
import com.topcoder.server.common.SurveyQuestion;
import com.topcoder.server.common.TCEvent;
import com.topcoder.server.common.Team;
import com.topcoder.server.common.TeamCoder;
import com.topcoder.server.common.TeamCoderHistory;
import com.topcoder.server.common.TeamCoderHistory.TeamChallengeData;
import com.topcoder.server.common.TeamContestRoom;
import com.topcoder.server.common.User;
import com.topcoder.server.common.WeakestLinkCoder;
import com.topcoder.server.common.WeakestLinkData;
import com.topcoder.server.common.WeakestLinkTeam;
import com.topcoder.server.contest.AnswerData;
import com.topcoder.server.contest.AssignedRoom;
import com.topcoder.server.contest.AssignedTeamRoom;
import com.topcoder.server.contest.DartboardRoomAssigner;
import com.topcoder.server.contest.EmptyRoomAssigner;
import com.topcoder.server.contest.ImportantMessageData;
import com.topcoder.server.contest.IronmanRoomAssigner;
import com.topcoder.server.contest.PrizeAllocator;
import com.topcoder.server.contest.PrizeRoom;
import com.topcoder.server.contest.PrizeWinner;
import com.topcoder.server.contest.RandomRoomAssigner;
import com.topcoder.server.contest.RoomAssigner;
import com.topcoder.server.contest.RoundRoomAssignment;
import com.topcoder.server.contest.TCHSRoomAssigner;
import com.topcoder.server.contest.TCO05RoomAssigner;
import com.topcoder.server.contest.TeamPrizeAllocator;
import com.topcoder.server.contest.TeamRoomAssigner;
import com.topcoder.server.contest.UltraRandomDiv2RoomAssigner;
import com.topcoder.server.contest.UltraRandomRoomAssigner;
import com.topcoder.server.contest.WeakestLinkRoomAssigner;
import com.topcoder.server.ejb.BaseEJB;
import com.topcoder.server.ejb.dao.RoundDao;
import com.topcoder.server.services.CoreServices;
import com.topcoder.server.util.DBUtils;
import com.topcoder.shared.dataAccess.resultSet.ResultSetContainer;
import com.topcoder.shared.language.BaseLanguage;
import com.topcoder.shared.language.Language;
import com.topcoder.shared.util.ApplicationServer;
import com.topcoder.shared.util.DBMS;
import com.topcoder.shared.util.IdGeneratorClient;
import com.topcoder.shared.util.logging.Logger;
import com.topcoder.util.idgenerator.IDGenerationException;
import com.topcoder.util.idgenerator.IDGenerator;
import com.topcoder.util.idgenerator.IDGeneratorFactory;

/**
 * <p>
 * Changes in version 1.0 (TopCoder Competition Engine - Event Support For Registration v1.0):
 * <ol>
 * <li>Added {@link #getEventRegistrationData(int,int)} to get event registration data. </li>
 * <li>Added {@link #getEventRegistrationData(Connection,int,int)} to get event registration data. </li>
 * <li>Updated {@link #getUser(Connection,int,boolean)} to retrieve {@link User#m_userStatus} from User table</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.1 (TopCoder Competition Engine Arena Login Logic Update v1.0):
 * <ol>
 *      <li>Add {@link #GET_SPECIFIC_GROUP_COUNTS} field.</li>
 *      <li>Add {@link #SQL_ADD_USER_TO_GROUPS} field.</li>
 *      <li>Add {@link #addUserGroup(int userId, int groupId)} method to add user group.</li>
 *      <li>Update {@link #registerCoder(int, int, List, Connection)} method to handle user algo rating data.</li>
 *      <li>Add {@link #handleCoderAlgoRatingRecord(int, int)} method to handle user algo rating data.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.2 (TopCoder Competition Engine - Revise Authentication Logic for SSO v1.0):
 * <ol>
 *      <li>Removed {@link #authenticateUserSSO(String)} method.</li>
 *      <li>Added {@link #validateSSOToken(String)} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in Version 1.3 (TopCoder Competition Engine - Eligibility Questions Validation):
 * <ol>
 *     <li>Updated {@link #registerCoder(int, int, java.util.List, java.sql.Connection)} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.4 (Web Arena UI Member Photo Display v1.0):
 * <ol>
 *      <li>Add {@link #QUERY_MEMBER_PHOTO_PATH} field.</li>
 *      <li>Add {@link #getMemberPhotoPath(int)} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.5 (Module Assembly - Web Socket Listener - Track User Actions From Web Arena):
 * <ol>
 *     <li>Added {@link #recordUserAction(String, String, String, java.util.Date)} method.</li>
 * </ol>
 * </p>
 *
 * @author savon_cn, freegod
 * @version 1.5
 */
public class DBServicesBean extends BaseEJB {
    /**
     * Category for logging.
     */
    private final static Logger s_trace = Logger.getLogger(DBServicesBean.class);
    private static final RoundDao roundDao = new RoundDao();

    /**
     * <p>
     * get event registration query text.
     * </p>
     */
    private static final String GET_EVENT_REGISTRATION_QUERY = "SELECT user_id,event_id,eligible_ind"
        + " FROM event_registration re WHERE user_id=? AND event_id=?";
    private static final int MAX_RESULTS_LENGTH = 200;
    private static final DateFormat DATE_FORMAT_HH_MM = new SimpleDateFormat("HH:mm");

    /**
     * A String holding the following query to save in database the fact of
     * acceptance by registered coder the terms for specififed round: <p>
     * <pre>
     *    INSERT INTO round_terms_acceptance
     *    (user_id, round_id, timestamp)
     *    VALUES (?, ?, ?)
     * </pre>
     * This String should be used by <code>DBServices.recordRegistration()
     * </code> method to save the fact of acceptance of round's terms by
     * registered coder.
     *
     * @since Admin Tool 2.0
     */
    private final static String INSERT_TERMS_ACCEPTANCE_QUERY = "INSERT INTO round_terms_acceptance "
        + "(user_id, round_id, timestamp) " + "VALUES (?, ?, ?)";
    private static final String GET_LONG_CODER_COMPONENT_QUERY = "SELECT cs.component_id, cs.status_id, cr.point_total, "
        + "       c.compilation_text, c.language_id, " + "       s.submission_text, s.language_id, "
        + "       ex.submission_text, ex.language_id, " + "       s.submit_time, ex.submit_time,"
        + "       cs.submission_number, cs.example_submission_number," + "       c.open_time"
        + "  FROM long_component_state cs, long_compilation c, long_comp_result cr, OUTER long_submission s,  OUTER long_submission ex"
        + "  WHERE cs.round_id = ? " + "    AND cs.coder_id = ? " + "    AND cs.component_id = ? "
        + "    AND s.long_component_state_id = cs.long_component_state_id "
        + "    AND s.submission_number = cs.submission_number " + "    AND s.example=0"
        + "    AND c.long_component_state_id = cs.long_component_state_id"
        + "    AND ex.long_component_state_id = cs.long_component_state_id "
        + "    AND ex.submission_number = cs.example_submission_number " + "    AND ex.example=1"
        + "    AND cr.round_id = cs.round_id " + "    AND cr.coder_id = cs.coder_id";
    
    private static final String GET_ROUND_EVENT_QUERY = "SELECT re.event_id,re.event_name,re.registration_url"
        + " FROM round_event re WHERE re.round_id=?";

    /**
     * this query was modified for AdminTool 2.0 to get the round room
     * assignment data for the round.
     */
    private static final String GET_COMPETITION_ROUND_QUERY = "SELECT" + "  c.contest_id," + "  c.name,"
        + "  r.name," + "  c.status," + "  c.activate_menu," + "  r.round_type_id," + "  r.round_id,"
        + "  ra.coders_per_room," + "  ra.algorithm," + "  ra.by_division," + "  ra.final," + "  ra.by_region,"
        + "  ra.p, " + "  r.region_id, " + "  c.season_id " + " FROM contest c, round r "
        + "      LEFT OUTER JOIN round_type_lu rt " + "        ON r.round_type_id=rt.round_type_id"
        + "      LEFT OUTER JOIN round_room_assignment ra" + "        ON r.round_id=ra.round_id"
        + " WHERE r.round_id = ? AND r.contest_id = c.contest_id";
    private static final String GET_CONTEST_ROOM_QUERY = "SELECT " + "room.name, " + // "round.contest_id, " +
        "round.round_id, " + "room.room_type_id, " + "room.division_id, " + "room.eligible, " + "room.unrated, "
        + "room.room_limit " + "FROM room, round WHERE round.round_id = room.round_id AND room.room_id = ?";
    private static final String GET_ROOM_RESULT_QUERY = "select coder_id from room_result where round_id = ?";
    private static final String BACKUP_PRACTICE_ROOM_RESULT_QUERY = "insert into practice_room_result select rr.round_id, rr.room_id, rr.coder_id, rr.point_total "
        + "from room_result rr " + "where rr.round_id = ?";
    private static final String BACKUP_PRACTICE_COMPONENT_STATE_QUERY = "insert into practice_component_state select cs.component_state_id, cs.round_id, cs.coder_id, cs.component_id, cs.points, cs.status_id, s.language_id, cs.submission_number "
        + "from component_state cs, OUTER(submission s) " + "where cs.round_id = ? "
        + "and s.component_state_id = cs.component_state_id " + "and s.submission_number = cs.submission_number";

    // TODO: we might not be getting the right ratings, adding the rating will lose coders
    // create the coder objects
    private static final String GET_ROOM_CODERS_QUERY = "SELECT DISTINCT rs.coder_id, u.handle, cr.rating, c.language_id, r.division_id, rs.attended, rs.old_rating, rr.eligible "
        + "FROM OUTER(algo_rating cr), OUTER(round_registration rr), room_result rs, room r, round rd, user u, coder c WHERE r.room_id = ? AND "
        + "rs.room_id = r.room_id AND rd.round_id = r.round_id AND rr.round_id = r.round_id AND rr.coder_id = c.coder_id "
        + "AND u.user_id = rs.coder_id AND u.user_id = cr.coder_id AND u.user_id = c.coder_id AND cr.algo_rating_type_id = ?";
    private static final String GET_ROOM_CODERS_LONG_QUERY = "SELECT rs.coder_id, u.handle, cr.rating, c.language_id, r.division_id, rs.attended, rs.old_rating, rr.eligible, rs.system_point_total "
        + "FROM OUTER(algo_rating cr), OUTER(round_registration rr), long_comp_result rs, room r, round rd, user u, coder c"
        + " WHERE r.room_id = ? AND rs.round_id = r.round_id AND rd.round_id = r.round_id AND rr.round_id = r.round_id AND rr.coder_id = c.coder_id "
        + "  AND u.user_id = rs.coder_id AND cr.coder_id = u.user_id AND c.coder_id = u.user_id AND cr.algo_rating_type_id = ? "
        + "  AND (" + "       rd.round_type_id IN (" + ContestConstants.LONG_PROBLEM_PRACTICE_ROUND_TYPE_ID + ", "
        + ContestConstants.AMD_LONG_PROBLEM_PRACTICE_ROUND_TYPE_ID + ") OR "
        + "       EXISTS (SELECT rr.round_id FROM round_registration rr WHERE rr.round_id = r.round_id AND rr.coder_id = rs.coder_id))";
    private static final String GET_ADMIN_ROOM_CODERS_LONG_QUERY = "SELECT rs.coder_id, u.handle, cr.rating, c.language_id, r.division_id, rs.attended, rs.old_rating, rr.eligible, rs.system_point_total "
        + "FROM OUTER(algo_rating cr), OUTER(round_registration rr), long_comp_result rs, room r, round rd, user u, coder c "
        + " WHERE r.room_id = ? AND rs.round_id = r.round_id AND rd.round_id = r.round_id AND rr.round_id = r.round_id AND rr.coder_id = c.coder_id "
        + "  AND u.user_id = rs.coder_id AND cr.coder_id = u.user_id AND c.coder_id = u.user_id AND cr.algo_rating_type_id = ? "
        + "  AND NOT EXISTS (SELECT rr.round_id FROM round_registration rr WHERE rr.round_id = r.round_id AND rr.coder_id = rs.coder_id)";
    private static final String GET_ROOM_TEAMS_QUERY = "SELECT DISTINCT t.team_id, r.division_id, rr.coder_id, u.handle, cr.rating, c.language_id "
        + "FROM team t, team_coder_xref tc, room_result rr, room r, rating cr, user u, coder c "
        + "WHERE rr.coder_id = tc.coder_id AND rr.coder_id = c.coder_id AND rr.coder_id = u.user_id "
        + "AND rr.coder_id = cr.coder_id AND t.team_id = tc.team_id AND r.room_id = rr.room_id "
        + "AND rr.room_id = ? ORDER BY t.team_id";
    private static final String GET_TEAM_NAME_QUERY = "SELECT t.team_name FROM team t WHERE t.team_id = ?";
    private static final String GET_TEAM_TYPE_QUERY = "SELECT team_type " + "FROM team WHERE team_id = ? ";
    private static final String GET_TEAM_CAPTAIN_QUERY = "SELECT tc.coder_id " + "FROM team_coder_xref tc WHERE "
        + "tc.captain = " + TeamConstants.TEAM_CAPTAIN + " AND " + "tc.team_id = ?";
    private static final String GET_TEAM_MEMBERS_QUERY = "SELECT tc.coder_id " + "FROM team_coder_xref tc WHERE "
        + "tc.captain = " + TeamConstants.TEAM_MEMBER + " AND " + "tc.team_id = ?";
    private static final String GET_TEAM_MEMBERS_FOR_ROUND_QUERY = "SELECT rr.coder_id "
        + "FROM team_coder_xref tc, round_registration rr WHERE " + "rr.coder_id = tc.coder_id AND "
        + "tc.captain  = " + TeamConstants.TEAM_MEMBER + " AND " + "tc.team_id = ? AND " + "rr.round_id = ?";

    /**
     * SQL to query the specific group IDs the given user already joined.
     * @since 1.1
     */
    private static final String GET_SPECIFIC_GROUP_COUNTS = "SELECT count(group_id) FROM user_group_xref "
            + "WHERE login_id = ? and group_id=?";

    /**
     * SQL to add user to groups.
     * @since 1.1
     */
    private static final String INSERT_USER_TO_GROUPS = "INSERT INTO user_group_xref (user_group_id, login_id, "
            + "group_id, create_user_id, security_status_id) VALUES (?, ?, ?, 1, 1)";
    /**
     * SQL to query the coder record in table algo_rating.
     * @since 1.1
     */
    private static final String GET_CODE_ALGO_RATING_COUNT = "SELECT count(coder_id) FROM algo_rating "
            + "WHERE coder_id = ? and algo_rating_type_id = ?";
    /**
     * SQL to add record in table algo_rating.
     */
    private static final String INSERT_CODER_ALOG_RATING = "INSERT INTO algo_rating (coder_id, rating, vol, num_ratings, "
            + "algo_rating_type_id) VALUES (?, 0, 0, 0, ?)";
    /**
     * A String holding the following query to get the terms for specified
     * round : <p>
     * <pre>
     *    SELECT terms_content
     *    FROM round_terms
     *    WHERE roundId = ?
     * </pre>
     * This String should be used by <code>DBServices.getRegistration(int)
     * </code> method to get the content of terms agreement for specified
     * round.
     *
     * @since Admin Tool 2.0
     */
    private final static String GET_ROUND_TERMS_QUERY = "SELECT terms_content FROM round_terms WHERE round_id = ?";

    /**
     * The column name which holds the full text of terms for a given round
     */
    private static final String TERMS_COLUMN = "terms_content";
    private final static String GET_USER_SQL_BATCH = "SELECT u.user_id,u.status, u.handle, cr.rating, c.language_id, cr.num_ratings, c.member_since, u.last_login, "
        + "c.quote, co.country_name, a.state_code, e.status_id, ct.coder_type_desc, rs.start_time as last_rated_event, "
        + "s.name, cs.viewable, cr_hs.rating as hs_rating, cr_hs.num_ratings as hs_num_ratings, rs_hs.start_time as hs_last_rated_event, "
        + "co2.country_name as origin_country_name, cr_mm.rating as mm_rating, cr_mm.num_ratings as mm_num_ratings, rs_mm.start_time as mm_last_rated_event "
        + "FROM user u, coder c, OUTER(algo_rating cr, OUTER(round_segment rs)), OUTER(user_address_xref uax, address a, country co2), OUTER(country co), OUTER(email e),  "
        + "coder_type ct, OUTER(current_school cs, school s) , OUTER(algo_rating cr_hs, OUTER(round_segment rs_hs)), "
        + "OUTER(algo_rating cr_mm, OUTER(round_segment rs_mm)), round_registration r_reg "
        + "WHERE r_reg.round_id = ? AND u.user_id = r_reg.coder_id AND u.user_id = c.coder_id AND c.coder_id = cr.coder_id  "
        + "and cr.algo_rating_type_id = 1 " + "and uax.user_id = u.user_id  "
        + "and a.address_id = uax.address_id AND a.address_type_id = 2  " + "and co2.country_code = a.country_code "
        + "and co.country_code = c.comp_country_code  " + "and ct.coder_type_id = c.coder_type_id  "
        + "and e.user_id = u.user_id  " + "and c.coder_id = cs.coder_id  " + "and cs.school_id = s.school_id  "
        + "and rs.round_id = cr.round_id  " + "and rs.segment_id = 2  " + "and e.primary_ind = 1  "
        + "and cr_hs.coder_id = c.coder_id " + "and cr_hs.algo_rating_type_id = 2 "
        + "and rs_hs.round_id = cr_hs.round_id " + "and rs_hs.segment_id = 2 " + "and cr_mm.coder_id = c.coder_id "
        + "and cr_mm.algo_rating_type_id = 3 " + "and rs_mm.round_id = cr_mm.round_id " + "and rs_mm.segment_id = 2";
    private final static String GET_NEW_RATING_BATCH = "SELECT coder_id, highest_rating FROM algo_rating WHERE algo_rating_type_id=1 AND coder_id in (?)";
    private final static String GET_NEW_HS_RATING_BATCH = "SELECT coder_id, highest_rating FROM algo_rating WHERE algo_rating_type_id=2 AND coder_id in (?)";
    private final static String GET_NEW_MM_RATING_BATCH = "SELECT coder_id, highest_rating FROM algo_rating WHERE algo_rating_type_id=3 AND coder_id in (?)";
    private final static String GET_IS_LEVEL_ONE_ADMIN_BATCH = "SELECT r_reg.coder_id, count(gu.group_id) as is_level_one_admin "
        + "FROM group_user gu, round_registration r_reg "
        + "WHERE r_reg.round_id = ? AND gu.user_id = r_reg.coder_id AND gu.group_id = 13 "
        + "group by r_reg.coder_id";
    private final static String GET_IS_LEVEL_TWO_ADMIN_BATCH = "SELECT r_reg.coder_id, count(gu.group_id) as is_level_two_admin "
        + "FROM group_user gu, round_registration r_reg "
        + "WHERE r_reg.round_id = ? AND gu.user_id = r_reg.coder_id AND gu.group_id = 14 "
        + "group by r_reg.coder_id";
    private final static String TEAM_INFO_BATCH = "SELECT tc.team_id, tc.captain, t.team_name, crx.region_id, tc.coder_id  "
        + "FROM team_coder_xref tc, team t, school s, address a, country_region_xref crx "
        + "WHERE tc.coder_id IN (BATCH_PARAMETERS) AND t.team_id = tc.team_id AND s.school_id = t.schooL_id "
        + "AND a.address_id = s.address_id AND crx.country_code = a.country_code";
    private final static String SEASON_INFO_BATCH = "SELECT s.season_id, er.user_id "
        + "FROM season s, event_registration er " + "WHERE er.event_id = s.event_id "
        + "AND er.user_id IN (BATCH_PARAMETERS) " + "AND er.eligible_ind = 1";
    private final static String GET_USER_SQL = "SELECT u.user_id, u.handle, cr.rating, c.language_id, cr.num_ratings, c.member_since, u.last_login,  "
        + "c.quote, co.country_name, a.state_code, e.status_id, ct.coder_type_desc, rs.start_time as last_rated_event,  "
        + "s.name, cs.viewable, (select max(new_rating) from room_result rr, round r where coder_id = u.user_id and r.round_id = rr.round_id and r.round_type_id in (1,2,10)) as new_rating, "
        + "cr_hs.rating as hs_rating, cr_hs.num_ratings as hs_num_ratings, rs_hs.start_time as hs_last_rated_event, "
        + "(select max(new_rating) from room_result rr, round r where coder_id = u.user_id and r.round_id = rr.round_id and r.round_type_id in (17,18)) as hs_new_rating, co2.country_name as origin_country_name, "
        + "cr_mm.rating as mm_rating, cr_mm.num_ratings as mm_num_ratings, rs_mm.start_time as mm_last_rated_event, "
        + "(select max(new_rating) from long_comp_result rr, round r where coder_id = u.user_id and r.round_id = rr.round_id and r.round_type_id in (13,19)) as mm_new_rating, "
        + "(select count(gu.group_id) from group_user gu where gu.user_id = u.user_id and gu.group_id = ?) as is_level_one_admin, "
        + "(select count(gu1.group_id) from group_user gu1 where gu1.user_id = u.user_id and gu1.group_id = ?) as is_level_two_admin, "
        + "u.status "
        + "FROM user u, coder c, OUTER(algo_rating cr, OUTER(round_segment rs)), OUTER(user_address_xref uax, address a, country co2), OUTER(country co), OUTER(email e),  "
        + "coder_type ct, OUTER(current_school cs, school s) , OUTER(algo_rating cr_hs, OUTER(round_segment rs_hs)), "
        + "OUTER(algo_rating cr_mm, OUTER(round_segment rs_mm)) "
        + "WHERE u.user_id = ? AND u.user_id = c.coder_id AND c.coder_id = cr.coder_id  "
        + "and cr.algo_rating_type_id = 1 " + "and uax.user_id = u.user_id  "
        + "and a.address_id = uax.address_id AND a.address_type_id = 2  " + "and co2.country_code = a.country_code "
        + "and co.country_code = c.comp_country_code  " + "and ct.coder_type_id = c.coder_type_id  "
        + "and e.user_id = u.user_id  " + "and c.coder_id = cs.coder_id  " + "and cs.school_id = s.school_id  "
        + "and rs.round_id = cr.round_id  " + "and rs.segment_id = 2  " + "and e.primary_ind = 1  "
        + "and cr_hs.coder_id = c.coder_id " + "and cr_hs.algo_rating_type_id = 2 "
        + "and rs_hs.round_id = cr_hs.round_id " + "and rs_hs.segment_id = 2 " + "and cr_mm.coder_id = c.coder_id "
        + "and cr_mm.algo_rating_type_id = 3 " + "and rs_mm.round_id = cr_mm.round_id " + "and rs_mm.segment_id = 2";
    private final static String TEAM_INFO = "SELECT tc.team_id, tc.captain, t.team_name, crx.region_id  "
        + "FROM team_coder_xref tc, team t, school s, address a, country_region_xref crx " + "WHERE tc.coder_id = ? "
        + "AND t.team_id = tc.team_id " + "AND s.school_id = t.schooL_id " + "AND a.address_id = s.address_id "
        + "AND crx.country_code = a.country_code";
    private final static String SEASON_INFO = "SELECT s.season_id " + "FROM season s, event_registration er "
        + "WHERE er.event_id = s.event_id " + "AND er.user_id = ? " + "AND er.eligible_ind = 1";
    private final static String INSERT_SECURE_OBJECT = "INSERT INTO secure_object"
        + " (secure_object_id, secure_object_type)" + " VALUES (?, 'U')";
    private final static String INSERT_USER = "INSERT INTO user"
        + " (user_id, handle, password, last_login, status, logged_in, terms, user_type_id)"
        + " VALUES (?, ?, 'fake', current, 'A', 'Y', 'Y', 1)";
    private final static String INSERT_CODER = "INSERT INTO coder"
        + " (coder_id,editor_id, language_id, member_since, quote)" + " VALUES (?, 0, 1, current, '')";
    private final static String INSERT_RATING = "INSERT INTO rating"
        + " (coder_id, round_id, rating, num_ratings, modify_date, vol, rating_no_vol)"
        + " VALUES (?, 0, 0, 0, current, 0, 0)";
    private final static String INSERT_GROUP = "INSERT INTO group_user" + " (group_id,user_id)" + " VALUES (?, ?)";
    private final static String INSERT_COMPANY_USER_XREF = "INSERT INTO company_user_xref"
        + " (company_id, user_id, company_user_code)" + " VALUES (?, ?, ?)";
    private final static String SELECT_USER_BY_REMOTE_ID = "SELECT user_id FROM company_user_xref WHERE company_id = ? AND company_user_code = ?";
    private static final String CLEAR_REGISTRATION_FOR_TEAM_QUERY = "DELETE FROM round_registration rr, team_coder_xref tc WHERE rr.coder_id = tc.coder_id AND tc.team_id = ? AND rr.round_id = ?";
    private static final String REGISTER_TEAM_MEMBER_QUERY = "INSERT INTO round_registration (round_id,coder_id,timestamp,eligible) VALUES (?,?,?,'N')";
    private static final String SELECT_SCHOOL_IF_FROM_USER = "select school_id from user_school_xref where user_id = ? and current_ind = 1";
    private static final String SELECT_COACH_ID_FROM_SCHOOL = "select " + " usx.user_id, "
        + " sg.description as type " + "from " + "  user_school_xref usx, " + "  user_group_xref ugx,  "
        + "  security_groups sg " + "where " + "  usx.current_ind = 1 and " + "  usx.school_id = ? and "
        + "  ugx.login_id = usx.user_id and " + "  ugx.group_id = sg.group_id and " + "  sg.description = 'Coach' ";
    private static final String SELECT_SCHOOL_ID_FROM_COACH = "select " + "   usx.school_id, " + "   usx.user_id, "
        + "   sg.description as type " + "from " + "   user_school_xref usx, " + "   user_group_xref ugx, "
        + "   security_groups sg " + "where " + "   usx.current_ind = 1 " + "   and usx.user_id = ? "
        + "   and ugx.login_id = usx.user_id " + "   and ugx.group_id = sg.group_id "
        + "   and sg.description = 'Coach' ";
    private static final String CLEAR_MEMBERS_QUERY = "DELETE FROM team_coder_xref WHERE team_id = ?";
    private static final String INSERT_MEMBER_QUERY = "INSERT INTO team_coder_xref (team_id,coder_id) VALUES (?,?)";

    /**
     * SQL statements to get answers of round question.
     *
     * @since 1.3
     */
    private static final String SELECT_ROUND_QUESTION_ANSWERS =
            " SELECT " +
            "    a.answer_id AS id " +
            "    , a.answer_text AS text " +
            "    , a.sort_order AS sort_order " +
            "    , a.correct AS correct " +
            " FROM " +
            "    answer a " +
            " WHERE " +
            "    a.question_id = ?";

    private static final String QUERY_MEMBER_PHOTO_PATH = "SELECT" +
            "    i.file_name as file_name, i.link, " +
            "   (select path from path p where p.path_id = i.path_id) as image_path" +
            " FROM coder_image_xref mi " +
            " INNER JOIN image i on mi.image_id=i.image_id" + 
            " WHERE mi.coder_id=? AND mi.display_flag=1";
    /**
     * SQL statement to insert user action audit item.
     *
     * @since 1.4
     */
    private static final String INSERT_USER_ACTION_AUDIT =
            "INSERT INTO user_action_audit(user_action_audit_id,handle,action_name,client,create_date) VALUES(?,?,?,?,?)";

    /**
     * Called during wrapup to calculate payouts for the given round
     */
    public Collection allocatePrizes(int roundID, boolean isFinal)
        throws DBServicesException {
        Connection conn = null;

        try {
            conn = DBMS.getConnection();
            conn.setAutoCommit(false);

            Round contest = getContestRound(conn, roundID, false);
            Collection r = null;

            if (contest.isTeamRound()) {
                r = allocatePrizes(conn, contest, new TeamPrizeAllocator(), isFinal);
            } else {
                r = allocatePrizes(conn, contest, new PrizeAllocator(), isFinal);
            }

            conn.commit();

            return r;
        } catch (Exception e) {
            rollback(conn);
            DBMS.printException(e);
            throw new DBServicesException("" + e);
        } finally {
            close(conn, null, null);
        }
    }

    private Collection allocatePrizes(Connection conn, Round contest, PrizeAllocator allocator, boolean isFinal)
        throws SQLException, DBServicesException, IDGenerationException {
        ArrayList contestRooms = new ArrayList();

        for (Iterator allRooms = contest.getAllRoomIDs(); allRooms.hasNext();) {
            ContestRoom nextRoom = (ContestRoom) getRoom(conn, ((Integer) allRooms.next()).intValue());

            if (!nextRoom.isAdminRoom()) {
                contestRooms.add(nextRoom);
            }
        }

        Collection prizeRooms = allocator.allocatePrizes(contestRooms);

        if (!isFinal) {
            return prizeRooms;
        }

        PreparedStatement ps = null;

        try {
            for (Iterator allPrizeRooms = prizeRooms.iterator(); allPrizeRooms.hasNext();) {
                PrizeRoom prizeRoom = (PrizeRoom) allPrizeRooms.next();

                for (Iterator allWinners = prizeRoom.getPrizeWinners().iterator(); allWinners.hasNext();) {
                    PrizeWinner winner = (PrizeWinner) allWinners.next();
                    s_trace.debug("Updating tables for winner: " + winner.getName() + " prize = " + winner.getPrize());

                    String sqlStr = "INSERT INTO round_payment (round_payment_id, round_id, coder_id, paid, payment_type_id) "
                        + "VALUES (?,?,?,?,?)";
                    ps = conn.prepareStatement(sqlStr);
                    ps.setInt(1, IdGeneratorClient.getSeqIdAsInt(DBMS.PAYMENT_SEQ));
                    ps.setInt(2, contest.getRoundID());
                    ps.setInt(3, winner.getUserID());
                    ps.setFloat(4, (float) winner.getPrize());

                    if (winner.isEligible()) {
                        ps.setInt(5, ContestConstants.CONTEST_PAYMENT);
                    } else {
                        ps.setInt(5, ContestConstants.CHARITY_PAYMENT);
                    }

                    int updateResult = ps.executeUpdate();

                    if (updateResult != 1) {
                        s_trace.error("WRONG NUMBER OF ROWS UPDATE IN wrapUpContest: " + updateResult);
                    }

                    ps.close();
                    ps = null;
                }
            }

            return prizeRooms;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e1) {
                printException(e1);
            }
        }
    }

    //private static final Comparator OBJECT_ARRAY_KEY_COMPARATOR = new Comparator() {
    //        public int compare(Object o1, Object o2) {
    //            return ((Comparable) ((Object[]) o1)[0]).compareTo(((Object[]) o2)[0]);
    //        }
    //    };
    private void buildBatchParameters(StringBuilder sb, int count) {
        for (int i = 0; i < count; ++i) {
            if (i == 0) {
                sb.append("?");
            } else {
                sb.append(",?");
            }
        }
    }

    private void loadCoderHistoryBatch(Connection conn, Collection coders, int contestId, int roundId, int roomId)
        throws SQLException {
        if (coders.size() == 0) {
            // No coder need to be loaded.
            return;
        }

        if (s_trace.isDebugEnabled()) {
            debug("DBServices.loadCoderHistoryBatch()....num of coders:" + coders.size() + "      contestId:"
                + contestId + "           roundId:" + roundId);
        }

        Map historyMap = new HashMap();
        int divisionId = ((Coder) coders.iterator().next()).getDivisionID();

        for (Iterator iter = coders.iterator(); iter.hasNext();) {
            Coder coder = (Coder) iter.next();

            if (coder.getDivisionID() != divisionId) {
                throw new SQLException("Different division cannot be loaded in a batch in loadCoderHistoryBatch().");
            }

            historyMap.put(Integer.valueOf(coder.getID()), new CoderHistory());
        }

        StringBuilder sqlStr = new StringBuilder();
        loadSubmissionHistoryBatch(conn, sqlStr, roundId, divisionId, roomId, historyMap);
        loadTotalSubmissionPointsBatch(conn, sqlStr, roundId, roomId, historyMap);
        loadChallengeHistoryBatch(conn, sqlStr, roundId, roomId, historyMap);
        loadSystemTestHistoryBatch(conn, sqlStr, roundId, divisionId, roomId, historyMap);

        // Set the coder history
        for (Iterator iter = coders.iterator(); iter.hasNext();) {
            Coder coder = (Coder) iter.next();
            coder.setHistory((CoderHistory) historyMap.get(Integer.valueOf(coder.getID())));
        }
    }

    /**
     * Used to get the CoderHistory for a given contest for a Coder
     */
    private CoderHistory getCoderHistory(Connection conn, int coderId, int divId, Round contestRound)
        throws SQLException {
        int contestId = contestRound.getContestID();
        int roundId = contestRound.getRoundID();
        if (s_trace.isDebugEnabled()) {
            debug("DBServices.getCoderHistory()....coderId:" + coderId + "      contestId:" + contestId
                + "           roundId:" + roundId);
        }

        StringBuilder sqlStr = new StringBuilder(300);
        CoderHistory ch = new CoderHistory();
        loadSubmissionHistory(conn, sqlStr, roundId, coderId, divId, ch);

        int totalSubmissionPoints = getTotalSubmissionPoints(conn, sqlStr, roundId, coderId);

        if (s_trace.isDebugEnabled()) {
            debug("Calculated " + totalSubmissionPoints + " total submission points for coder #" + coderId
                + " in round #" + roundId);
        }

        ch.setTotalSubmissionPoints(totalSubmissionPoints);
        loadChallengeHistory(conn, sqlStr, contestRound, coderId, ch);
        loadSystemTestHistory(conn, sqlStr, coderId, roundId, divId, ch);

        return ch;
    }

    private void loadSystemTestHistoryBatch(Connection conn, StringBuilder sqlStr, int roundId, int divId,
        int roomId, Map historyMap) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        // Lastly, get the system test history
        sqlStr.replace(0, sqlStr.length(), "SELECT str.timestamp, str.deduction_amount, ");
        sqlStr.append(
            "rc.points, stc.args, str.timestamp, str.received, stc.expected_result, rc.component_id, str.coder_id, str.succeeded ");
        sqlStr.append("FROM system_test_result str, round_component rc, system_test_case stc ");
        sqlStr.append(", room_result rs ");
        sqlStr.append(
            "WHERE rs.round_id = ? AND rs.room_id = ? AND str.round_id = rs.round_id AND str.coder_id = rs.coder_id ");
        sqlStr.append(
            "AND str.viewable = 'Y' AND rc.round_id = str.round_id AND rc.component_id = str.component_id AND ");
        sqlStr.append("rc.division_id = ? AND stc.test_case_id = str.test_case_id ORDER BY str.timestamp");

        s_trace.info("start loadSystemTestHistoryBatch, roundId=" + roundId + ", roomId=" + roomId);
        //sqlStr.append("str.coder_id IN (");
        //buildBatchParameters(sqlStr, historyMap.size());
        //sqlStr.append(")");
        try {
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, roundId);
            ps.setInt(2, roomId);
            ps.setInt(3, divId);
            //int index = 3;
            //for (Iterator iter = historyMap.keySet().iterator(); iter.hasNext(); ++index) {
            //    ps.setInt(index, ((Integer) iter.next()).intValue());
            //}
            rs = ps.executeQuery();

            while (rs.next()) {
                Integer coderId = Integer.valueOf(rs.getInt(9));
                boolean succeeded = rs.getBoolean(10);
                Timestamp timestamp = rs.getTimestamp(1);

                //String timestampStr = DATE_FORMAT_HH_MM.format(timestamp);
                int problemVal = rs.getInt(3);
                int deductAmt = (int) Math.round(rs.getDouble(2) * 100);
                String args = ContestConstants.makePretty(DBMS.getBlobObject(rs, 4));
                int problemId = rs.getInt(8);
                String results = ContestConstants.makePretty(DBMS.getBlobObject(rs, 6));

                if ((results != null) && (results.length() > MAX_RESULTS_LENGTH)) {
                    results = results.substring(0, MAX_RESULTS_LENGTH);
                }

                s_trace.debug("Adding system test for coder " + coderId + ": " + results);
                ((CoderHistory) historyMap.get(coderId)).addTest(problemId, timestamp, deductAmt, problemVal + "",
                    args, results, succeeded);
            }

            s_trace.info("finished loadSystemTestHistoryBatch, roundId=" + roundId + ", roomId=" + roomId);
        } finally {
            DBMS.close(ps, rs);
        }
    }

    private void loadSystemTestHistory(Connection conn, StringBuilder sqlStr, int coderId, int roundId, int divId,
        CoderHistory ch) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        // Lastly, get the system test history
        sqlStr.replace(0, sqlStr.length(), "SELECT str.timestamp, str.deduction_amount, ");
        sqlStr.append(
            "rc.points, stc.args, str.timestamp, str.received, stc.expected_result, c.component_id, str.succeeded ");
        sqlStr.append(
            "FROM system_test_result str, round_component rc, system_test_case stc, component c WHERE str.coder_id = ? ");
        sqlStr.append(
            "AND str.round_id = ? AND str.test_case_id = stc.test_case_id AND str.component_id = c.component_id ");
        sqlStr.append(
            "AND str.viewable = 'Y' AND rc.round_id = str.round_id AND rc.component_id = c.component_id AND rc.division_id = ? ORDER BY str.timestamp");

        s_trace.info("start loadSystemTestHistory, roundId=" + roundId + ", coderId=" + coderId);
        try {
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, coderId);
            ps.setInt(2, roundId);
            ps.setInt(3, divId);
            rs = ps.executeQuery();

            while (rs.next()) {
                Timestamp timestamp = rs.getTimestamp(1);
                String timestampStr = DATE_FORMAT_HH_MM.format(timestamp);
                int problemVal = rs.getInt(3);
                int deductAmt = (int) Math.round(rs.getDouble(2) * 100);
                String args = ContestConstants.makePretty(DBMS.getBlobObject(rs, 4));
                int problemId = rs.getInt(8);
                String results = ContestConstants.makePretty(DBMS.getBlobObject(rs, 6));
                boolean succeeded = rs.getBoolean(9);

                if ((results != null) && (results.length() > MAX_RESULTS_LENGTH)) {
                    results = results.substring(0, MAX_RESULTS_LENGTH);
                }

                s_trace.debug("Adding system test: " + results);
                ch.addTest(problemId, timestamp, deductAmt, problemVal + "", args, results, succeeded);
            }

            s_trace.info("finished loadSystemTestHistory, roundId=" + roundId + ", coderId=" + coderId);
        } finally {
            DBMS.close(rs);
            DBMS.close(ps);
        }
    }

    private void loadChallengeHistoryBatch(Connection conn, StringBuilder sqlStr, int roundId, int roomId,
        Map historyMap) throws SQLException {
        // Next get the challenge history
        PreparedStatement ps = null;
        ResultSet rs = null;
        sqlStr.replace(0, sqlStr.length(),
            "SELECT submit_time, message, challenger_points, defendant_points, challenger_id, defendant_id, component_id, args ");
        sqlStr.append("FROM challenge WHERE challenge.round_id = ? AND status_id != ? AND ");
        sqlStr.append("(challenger_id IN (SELECT rs.coder_id FROM room_result rs WHERE rs.room_id = ?) OR ");
        sqlStr.append("defendant_id IN (SELECT rs1.coder_id FROM room_result rs1 WHERE rs1.room_id = ?)) ");
        sqlStr.append("ORDER BY submit_time");

        s_trace.info("start loadChallengeHistoryBatch, roundId=" + roundId + ", roomId=" + roomId);
        //sqlStr.append("(challenger_id IN (");
        //buildBatchParameters(sqlStr, historyMap.size());
        //sqlStr.append(") OR defendant_id IN (");
        //buildBatchParameters(sqlStr, historyMap.size());
        //sqlStr.append("))");
        try {
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, roundId);
            ps.setInt(2, ContestConstants.NULLIFIED_CHALLENGE);
            ps.setInt(3, roomId);
            ps.setInt(4, roomId);
            //int index = 3;
            // Set challenger IDs
            //for (Iterator iter = historyMap.keySet().iterator(); iter.hasNext(); ++index) {
            //    ps.setInt(index, ((Integer) iter.next()).intValue());
            //}
            // Set defendant IDs
            //for (Iterator iter = historyMap.keySet().iterator(); iter.hasNext(); ++index) {
            //    ps.setInt(index, ((Integer) iter.next()).intValue());
            //}
            rs = ps.executeQuery();

            // next, get challenge history
            while (rs.next()) {
                String msg = rs.getString(2);
                Integer challengerId = Integer.valueOf(rs.getInt(5));
                Integer defendantId = Integer.valueOf(rs.getInt(6));
                int challengerPoints = (int) Math.round(rs.getDouble(3) * 100);
                int defendantPoints = (int) Math.round(rs.getDouble(4) * 100);
                int componentID = rs.getInt(7);
                Object obj = DBMS.getBlobObject(rs, 8);
                Object[] args;
                Date submitTime = new Date(rs.getLong(1));

                if (obj instanceof List) {
                    args = ((List) obj).toArray();
                } else {
                    s_trace.error("Challenge args not instanceof List");
                    args = null;
                }

                if (historyMap.containsKey(challengerId)) {
                    ((CoderHistory) historyMap.get(challengerId)).addChallenge(msg, submitTime, challengerPoints,
                        componentID, new ChallengeCoder(defendantId.intValue()), true, args);
                }

                if (historyMap.containsKey(defendantId)) {
                    ((CoderHistory) historyMap.get(defendantId)).addChallenge(msg, submitTime, defendantPoints,
                        componentID, new ChallengeCoder(challengerId.intValue()), false, args);
                }
            }
            s_trace.info("finished loadChallengeHistoryBatch, roundId=" + roundId + ", roomId=" + roomId);
        } finally {
            DBMS.close(rs);
            DBMS.close(ps);
        }
    }

    private void loadChallengeHistory(Connection conn, StringBuilder sqlStr, Round contestRound, int coderId, CoderHistory ch)
        throws SQLException {
        // Next get the challenge history
    	int roundId = contestRound.getRoundID();
        PreparedStatement ps = null;
        ResultSet rs = null;

        s_trace.info("start loadChallengeHistory, roundId=" + roundId + ", coderId=" + coderId);
        try {
            List<ChallengeData> challenges = new ArrayList();

            ps = conn.prepareStatement("SELECT c.submit_time, c.message, c.component_id, c.args, c.challenger_points, c.defendant_id, u.handle, ar.rating "
            		+ "FROM challenge c, user u, OUTER(algo_rating ar) WHERE c.round_id = ? AND c.status_id != ? AND "
            		+ "c.challenger_id = ? AND u.user_id = c.defendant_id AND ar.coder_id = u.user_id AND ar.algo_rating_type_id =? ");
            ps.setInt(1, roundId);
            ps.setInt(2, ContestConstants.NULLIFIED_CHALLENGE);
            ps.setInt(3, coderId);
            ps.setInt(4, contestRound.getRoundType().getRatingType());
            rs = ps.executeQuery();

            // next, get challenge history
            while (rs.next()) {
            	Date date = new Date(rs.getLong(1));
                String msg = rs.getString(2);
                int componentID = rs.getInt(3);
                Object args = DBMS.getBlobObject(rs, 4);

                int points = (int) Math.round(rs.getDouble(5) * 100);

                ChallengeCoder otherUser = new ChallengeCoder(rs.getInt(6), rs.getString(7), rs.getInt(8));

                challenges.add(new ChallengeData(msg, date, points, true, otherUser, componentID, 
                		args instanceof List ? ((List) args).toArray() : null));
            }
            DBMS.close(ps, rs);

            // next, get defendant history
            ps = conn.prepareStatement("SELECT c.submit_time, c.message, c.component_id, c.args, c.defendant_points, c.challenger_id, u.handle, ar.rating "
            		+ "FROM challenge c, user u, OUTER(algo_rating ar) WHERE c.round_id = ? AND c.status_id != ? AND "
            		+ "c.defendant_id = ? AND u.user_id = c.challenger_id AND ar.coder_id = u.user_id AND ar.algo_rating_type_id =? ");
            ps.setInt(1, roundId);
            ps.setInt(2, ContestConstants.NULLIFIED_CHALLENGE);
            ps.setInt(3, coderId);
            ps.setInt(4, contestRound.getRoundType().getRatingType());
            rs = ps.executeQuery();
            while (rs.next()) {
            	Date date = new Date(rs.getLong(1));
                String msg = rs.getString(2);
                int componentID = rs.getInt(3);
                Object args = DBMS.getBlobObject(rs, 4);

                int points = (int) Math.round(rs.getDouble(5) * 100);

                ChallengeCoder otherUser = new ChallengeCoder(rs.getInt(6), rs.getString(7), rs.getInt(8));

                challenges.add(new ChallengeData(msg, date, points, false, otherUser, componentID, 
                		args instanceof List ? ((List) args).toArray() : null));
            }

            ch.addChallenges(challenges);
            s_trace.info("finished loadChallengeHistory, roundId=" + roundId + ", coderId=" + coderId);
        } finally {
            DBMS.close(rs);
            DBMS.close(ps);
        }
    }

    private void loadSubmissionHistoryBatch(Connection conn, StringBuilder sqlStr, int roundId, int divId,
        int roomId, Map historyMap) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;

        // First get the submission history
        sqlStr.setLength(0);
        sqlStr.append("SELECT s.submit_time, rc.points, s.submission_points, cs.coder_id ");
        sqlStr.append("FROM component_state cs, component c, round_component rc, submission s ");
        sqlStr.append(", room_result rs ");
        sqlStr.append("WHERE cs.round_id = ? AND s.submission_points > 0 ");
        sqlStr.append("  AND cs.component_id = c.component_id AND cs.component_state_id = s.component_state_id ");
        sqlStr.append("AND rc.round_id = cs.round_id AND rc.component_id = c.component_id AND rc.division_id = ? ");
        sqlStr.append("AND cs.coder_id = rs.coder_id AND rs.room_id = ? ORDER BY s.submit_time");

        s_trace.info("start loadSubmissionHistoryBatch, roundId=" + roundId + ", roomId=" + roomId);
        //sqlStr.append("AND cs.coder_id IN (");
        //buildBatchParameters(sqlStr, historyMap.size());
        //sqlStr.append(")");
        try {
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, roundId);
            ps.setInt(2, divId);
            //int index = 3;
            //for (Iterator iter = historyMap.keySet().iterator(); iter.hasNext(); ++index) {
            //    ps.setInt(index, ((Integer) iter.next()).intValue());
            //}
            ps.setInt(3, roomId);
            rs = ps.executeQuery();

            // first get submission history
            while (rs.next()) {
                Date submitTime = new Date(rs.getLong(1));
                int points = (int) Math.round(rs.getDouble(2));
                Integer coderId = Integer.valueOf(rs.getInt(4));
                int submitPoints = (int) Math.round(rs.getDouble(3) * 100);
                ((CoderHistory) historyMap.get(coderId)).addSubmission("" + points, submitTime, submitPoints);
            }
            s_trace.info("finished loadSubmissionHistoryBatch, roundId=" + roundId + ", roomId=" + roomId);
        } finally {
            close(null, ps, rs);
        }
    }

    private void loadSubmissionHistory(Connection conn, StringBuilder sqlStr, int roundId, int coderId, int divId,
        CoderHistory ch) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;

        // First get the submission history
        sqlStr.setLength(0);
        sqlStr.append("SELECT s.submit_time, rc.points, s.submission_points ");
        sqlStr.append("FROM component_state cs, component c, round_component rc, submission s ");
        sqlStr.append("WHERE cs.round_id = ? AND cs.coder_id = ? AND s.submission_points > 0 ");
        sqlStr.append("  AND cs.component_id = c.component_id AND cs.component_state_id = s.component_state_id ");
        sqlStr.append(
            "AND rc.round_id = cs.round_id AND rc.component_id = c.component_id AND rc.division_id = ? ORDER BY s.submit_time");

        s_trace.info("start loadSubmissionHistory, roundId=" + roundId + ", coderId=" + coderId);
        try {
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, roundId);
            ps.setInt(2, coderId);
            ps.setInt(3, divId);
            rs = ps.executeQuery();

            // first get submission history
            while (rs.next()) {
                int points = (int) Math.round(rs.getDouble(2));
                ch.addSubmission("" + points, new Date(rs.getLong(1)), (int) Math.round(rs.getDouble(3) * 100));
            }
            s_trace.info("finished loadSubmissionHistory, roundId=" + roundId + ", coderId=" + coderId);
        } finally {
            close(null, ps, rs);
        }
    }

    private void loadTotalSubmissionPointsBatch(Connection conn, StringBuilder sqlStr, int roundId, int roomId,
        Map historyMap) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;

        // Begin GT Added bc of Multiple Submit Mode
        sqlStr.replace(0, sqlStr.length(), "SELECT SUM(s.submission_points), cs.coder_id ");
        sqlStr.append("FROM component_state cs, submission s ");
        sqlStr.append(", room_result rs ");
        sqlStr.append("WHERE cs.round_id = ? ");
        sqlStr.append("AND s.submission_points > 0 ");
        sqlStr.append("AND cs.component_state_id = s.component_state_id ");
        sqlStr.append("AND s.submission_number = cs.submission_number ");
        sqlStr.append("AND cs.coder_id = rs.coder_id AND rs.room_id = ? GROUP BY cs.coder_id");

        s_trace.info("start loadTotalSubmissionPointsBatch, roundId=" + roundId + ", roomId=" + roomId);
        //sqlStr.append("AND cs.coder_id IN (");
        //buildBatchParameters(sqlStr, historyMap.size());
        //sqlStr.append(")");
        try {
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, roundId);
            //int index = 2;
            //for (Iterator iter = historyMap.keySet().iterator(); iter.hasNext(); ++index) {
            //    ps.setInt(index, ((Integer) iter.next()).intValue());
            //}
            ps.setInt(2, roomId);
            rs = ps.executeQuery();

            while (rs.next()) {
                Integer coderId = Integer.valueOf(rs.getInt(2));
                ((CoderHistory) historyMap.get(coderId)).setTotalSubmissionPoints((int) Math.round(
                        rs.getDouble(1) * 100));
            }
            s_trace.info("finished loadTotalSubmissionPointsBatch, roundId=" + roundId + ", roomId=" + roomId);
        } finally {
            close(null, ps, rs);
        }
    }

    private int getTotalSubmissionPoints(Connection conn, StringBuilder sqlStr, int roundId, int coderId)
        throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;

        // Begin GT Added bc of Multiple Submit Mode
        sqlStr.replace(0, sqlStr.length(), "SELECT sum(s.submission_points) ");
        sqlStr.append("FROM component_state cs, submission s ");
        sqlStr.append("WHERE cs.round_id = ? ");
        sqlStr.append("AND cs.coder_id = ? ");
        sqlStr.append("AND s.submission_points > 0 ");
        sqlStr.append("AND cs.component_state_id = s.component_state_id ");
        sqlStr.append("AND s.submission_number = cs.submission_number ");

        s_trace.info("start getTotalSubmissionPoints, roundId=" + roundId + ", coderId=" + coderId);
        try {
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, roundId);
            ps.setInt(2, coderId);
            rs = ps.executeQuery();
            rs.next();

            s_trace.info("finished getTotalSubmissionPoints, roundId=" + roundId + ", coderId=" + coderId);
            return (int) Math.round(rs.getDouble(1) * 100);
        } finally {
            close(null, ps, rs);
        }
    }

    /**
     * Used to get the CoderHistory for a given contest for a Coder
     */
    private CoderHistory getTeamHistory(Connection conn, TeamCoder teamCoder, int divId, int contestId, Round round)
        throws SQLException {
    	int roundId = round.getRoundID();
        int teamId = teamCoder.getID();

        if (s_trace.isDebugEnabled()) {
            s_trace.debug("DBServices.getTeamHistory()....teamId:" + teamId + "      contestId:" + contestId
                + "           roundId:" + roundId);
        }

        StringBuilder sqlStr = new StringBuilder(300);

        TeamCoderHistory ch = new TeamCoderHistory();
        loadTeamSubmissionHistory(conn, sqlStr, roundId, teamId, divId, ch);

        int totalSubmissionPoints = getTotalTeamSubmissionPoints(conn, sqlStr, roundId, teamId);

        if (s_trace.isDebugEnabled()) {
            debug("Calculated " + totalSubmissionPoints + " total submission points for team #" + teamId
                + " in round #" + roundId);
        }

        ch.setTotalSubmissionPoints(totalSubmissionPoints);
        loadTeamChallengeHistory(conn, sqlStr, round, teamCoder, ch);
        loadTeamSystemTestHistory(conn, sqlStr, teamId, roundId, divId, ch);

        return ch;
    }

    private void loadTeamSystemTestHistory(Connection conn, StringBuilder sqlStr, int teamId, int roundId, int divId,
        TeamCoderHistory ch) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;

        // Lastly, get the system test history
        sqlStr.replace(0, sqlStr.length(), "SELECT str.timestamp, str.deduction_amount, ");
        sqlStr.append(
            "rc.points, stc.args, str.timestamp, str.received, stc.expected_result, c.component_id, str.succeeded ");
        sqlStr.append(
            "FROM system_test_result str, round_component rc, system_test_case stc, component c, team_coder_component_xref tcc ");
        sqlStr.append(
            "WHERE tcc.team_id = ? AND str.coder_id = tcc.coder_id AND str.component_id = tcc.component_id ");
        sqlStr.append(
            "AND str.round_id = ? AND str.test_case_id = stc.test_case_id AND str.component_id = c.component_id ");
        sqlStr.append(
            "AND str.viewable = 'Y' AND rc.round_id = str.round_id AND rc.component_id = c.component_id AND rc.division_id = ? ");
        sqlStr.append("AND tcc.round_id = rc.round_id ORDER BY str.timestamp");

        try {
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, teamId);
            ps.setInt(2, roundId);
            ps.setInt(3, divId);
            rs = ps.executeQuery();
            s_trace.debug("Loading system test history for team: " + teamId);

            while (rs.next()) {
                Timestamp timestamp = rs.getTimestamp(1);

                //String timestampStr = DATE_FORMAT_HH_MM.format(timestamp);
                int problemVal = (int) Math.round(rs.getDouble(3) * 100);
                int deductAmt = (int) Math.round(rs.getDouble(2) * 100);
                String args = ContestConstants.makePretty(DBMS.getBlobObject(rs, 4));
                int problemId = rs.getInt(8);
                boolean succeeded = rs.getBoolean(9);
                String results;
                // if (DBMS.DB==DBMS.INFORMIX) {
                results = ContestConstants.makePretty(DBMS.getBlobObject(rs, 6));

                // } else
                // results = rs.getString(6);
                if ((results != null) && (results.length() > MAX_RESULTS_LENGTH)) {
                    results = results.substring(0, MAX_RESULTS_LENGTH);
                }

                s_trace.debug("Adding system test: " + results);
                ch.addTest(problemId, timestamp, deductAmt, problemVal + "", args, results, succeeded);
            }
        } finally {
            DBMS.close(rs);
            DBMS.close(ps);
        }
    }

    private void loadTeamChallengeHistory(Connection conn, StringBuilder sqlStr, Round contestRound, TeamCoder teamCoder,
        TeamCoderHistory ch) throws SQLException {
        // Next get the challenge history
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            List<ChallengeData> challenges = new ArrayList();
            ps = conn.prepareStatement("SELECT ch.submit_time, ch.message, ch.component_id, ch.args, ch.challenger_points, ch.defendant_id, u.handle, ar.rating "
            		+ "FROM challenge ch, user u, OUTER(algo_rating ar) "
            		+ "WHERE ch.round_id = ? AND ch.status_id != ? "
            		+ "AND ch.challenger_id IN (SELECT tc.coder_id FROM team_coder_xref tc WHERE tc.team_id=?) "
            		+ "AND u.user_id = ch.defendant_id AND ar.coder_id = u.user_id AND ar.algo_rating_type_id =?");
            ps.setInt(1, contestRound.getRoundID());
            ps.setInt(2, ContestConstants.NULLIFIED_CHALLENGE);
            ps.setInt(3, teamCoder.getID());
            ps.setInt(4, contestRound.getRoundType().getRatingType());
            rs = ps.executeQuery();

            while (rs.next()) {
            	Date date = new Date(rs.getLong(1));
                String msg = rs.getString(2);
                int componentID = rs.getInt(3);
                Object args = DBMS.getBlobObject(rs, 4);

                int points = (int) Math.round(rs.getDouble(5) * 100);

                ChallengeCoder otherUser = new ChallengeCoder(rs.getInt(6), rs.getString(7), rs.getInt(8));

                challenges.add(new TeamChallengeData(msg, date, points, true, otherUser, componentID, 
                		args instanceof List ? ((List) args).toArray() : null));
            }
            DBMS.close(ps, rs);

            ps = conn.prepareStatement("SELECT ch.submit_time, ch.message, ch.component_id, ch.args, ch.defendant_points, ch.challenger_id, u.handle, ar.rating "
            		+ "FROM challenge ch, user u, OUTER(algo_rating ar) "
            		+ "WHERE ch.round_id = ? AND ch.status_id != ? "
            		+ "AND ch.defendant_id IN (SELECT tc.coder_id FROM team_coder_xref tc WHERE tc.team_id=?) "
            		+ "AND u.user_id = ch.challenger_id AND ar.coder_id = u.user_id AND ar.algo_rating_type_id =?");
            ps.setInt(1, contestRound.getRoundID());
            ps.setInt(2, ContestConstants.NULLIFIED_CHALLENGE);
            ps.setInt(3, teamCoder.getID());
            ps.setInt(4, contestRound.getRoundType().getRatingType());
            rs = ps.executeQuery();
            while (rs.next()) {
            	Date date = new Date(rs.getLong(1));
                String msg = rs.getString(2);
                int componentID = rs.getInt(3);
                Object args = DBMS.getBlobObject(rs, 4);

                int points = (int) Math.round(rs.getDouble(5) * 100);

                ChallengeCoder otherUser = new ChallengeCoder(rs.getInt(6), rs.getString(7), rs.getInt(8));

                challenges.add(new TeamChallengeData(msg, date, points, false, otherUser, componentID, 
                		args instanceof List ? ((List) args).toArray() : null));
            }

            ch.addChallenges(challenges);
        } finally {
            DBMS.close(rs);
            DBMS.close(ps);
        }
    }

    private void loadTeamSubmissionHistory(Connection conn, StringBuilder sqlStr, int roundId, int teamId, int divId,
        TeamCoderHistory ch) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        // First get the submission history
        sqlStr.append("SELECT s.submit_time, rc.points, s.submission_points, s.submit_time, u.handle ");
        sqlStr.append(
            "FROM component_state cs, component c, round_component rc, submission s, team_coder_component_xref tcc, user u ");
        sqlStr.append(
            "WHERE u.user_id = tcc.coder_id AND cs.round_id = ? AND tcc.team_id = ? AND rc.division_id = ? ");
        sqlStr.append("AND cs.coder_id = tcc.coder_id AND s.submission_points > 0 ");
        sqlStr.append("  AND cs.component_id = c.component_id AND cs.component_state_id = s.component_state_id ");
        sqlStr.append(
            "AND rc.round_id = cs.round_id AND rc.component_id = c.component_id AND cs.component_id = tcc.component_id AND tcc.round_id = rc.round_id ");
        sqlStr.append("ORDER BY s.submit_time");

        try {
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, roundId);
            ps.setInt(2, teamId);
            ps.setInt(3, divId);
            rs = ps.executeQuery();

            // first get submission history
            while (rs.next()) {
                int points = (int) Math.round(rs.getDouble(2) * 100);
                ch.addSubmission("" + points, new java.sql.Date(rs.getLong(1)),
                    (int) Math.round(rs.getDouble(3) * 100), rs.getString(5));
            }
        } finally {
            close(null, ps, rs);
        }
    }

    private int getTotalTeamSubmissionPoints(Connection conn, StringBuilder sqlStr, int roundId, int teamId)
        throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        // Begin GT Added bc of Multiple Submit Mode
        sqlStr.replace(0, sqlStr.length(), "SELECT sum(s.submission_points) ");
        sqlStr.append("FROM component_state cs, submission s,team_coder_component_xref tcc ");
        sqlStr.append("WHERE cs.round_id = ? AND tcc.team_id = ?");
        sqlStr.append("AND cs.coder_id = tcc.coder_id ");
        sqlStr.append("AND tcc.active = 1 ");
        sqlStr.append("AND tcc.component_id = cs.component_id ");
        sqlStr.append("AND s.submission_points > 0 ");
        sqlStr.append("AND cs.component_state_id = s.component_state_id ");
        sqlStr.append("AND s.submission_number = cs.submission_number ");
        sqlStr.append("AND tcc.round_id = cs.round_id ");

        try {
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, roundId);
            ps.setInt(2, teamId);
            rs = ps.executeQuery();
            rs.next();

            return (int) Math.round(rs.getDouble(1) * 100);
        } finally {
            close(null, ps, rs);
        }
    }

    /**
     * This method registers a coder by handle, without survey information.
     * Requests to call this method are submitted by an admin from the admin monitor.
     */
    public CommandResponse registerCoderByHandle(String handle, int roundId, boolean atLeast18) {
        Connection c = null;
        PreparedStatement ps = null;

        try {
            c = DBMS.getConnection();

            // Lookup the userid.
            ps = c.prepareStatement("SELECT user_id FROM user WHERE handle=? and status = 'A' ");
            ps.setString(1, handle);

            ResultSetContainer rsc = runSelectQuery(ps);

            if (rsc.getRowCount() == 0) {
                return new CommandFailedResponse("User " + handle + " was not found in the database.");
            }

            int userId = Integer.parseInt(rsc.getItem(0, 0).toString());
            ps.close();
            ps = null;

            // Verify user is not already registered.
            if (roundDao.isCoderRegistered(userId, roundId, c)) {
                return new CommandFailedResponse("User " + handle + " is already registered for this round.");
            }

            // Check prize eligibility
            boolean eligible = atLeast18;
            String ineligReason = "you specified that the coder was not at least 18";
            StringBuilder query = new StringBuilder();
            query.append("SELECT ct.participating ");
            query.append("FROM country ct, user_address_xref c, address a  ");
            query.append("WHERE c.user_id = " + userId
                + " AND a.country_code = ct.country_code AND a.address_id = c.address_id");
            ps = c.prepareStatement(query.toString());
            rsc = runSelectQuery(ps);

            // If no country code, ineligible
            if (rsc.getRowCount() == 0) {
                eligible = false;
                ineligReason = "there is no country associated with the coder";
            } else {
                int participating = Integer.parseInt(rsc.getItem(0, 0).toString());

                // If country is not a participating country, ineligible
                if (participating != 1) {
                    eligible = false;
                    ineligReason = "the coder is from a non-participating country";
                }
            }

            ps.close();
            ps = null;

            // Add the registration
            /*
               String currentTime;
               if (DBMS.DB == DBMS.INFORMIX) {
                   currentTime = "CURRENT";
               } else {
                   currentTime = "CURRENT_TIME";
               }*/

            // determine if it's a high school round
            if (isHighSchoolRound(c, roundId)) {
                ps = c.prepareStatement("SELECT team_id FROM team_coder_xref WHERE coder_id = " + userId);

                ResultSet rs = ps.executeQuery();
                rs.next();
                ps = c.prepareStatement(
                        "INSERT INTO round_registration (coder_id, round_id, timestamp, eligible, team_id) VALUES (?,?,?,?,"
                        + rs.getInt(1) + ")");
            } else {
                ps = c.prepareStatement(
                        "INSERT INTO round_registration (coder_id, round_id, timestamp, eligible) VALUES (?,?,?,?)");
            }

            ps.setInt(1, userId);
            ps.setInt(2, roundId);

            Timestamp now = new Timestamp(System.currentTimeMillis());
            ps.setTimestamp(3, now);
            ps.setInt(4, eligible ? 1 : 0);
            ps.executeUpdate();
            ps.close();
            ps = null;

            String eligibleString;

            if (eligible) {
                eligibleString = "as eligible for prizes.";
            } else {
                eligibleString = "as ineligible for prizes.\nCoder is ineligible because\n" + ineligReason + ".";
            }

            return new CommandSucceededResponse("Coder " + handle + " successfully registered " + eligibleString);
        } catch (Exception e) {
            printException(e);

            return new CommandFailedResponse("Received exception registering coder; check log.");
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e1) {
                printException(e1);
            }

            try {
                if (c != null) {
                    c.close();
                }
            } catch (Exception e1) {
                printException(e1);
            }
        }
    }

    /**
     * This method registers a coder by handle, without survey information.
     * Requests to call this method are submitted by an admin from the admin monitor.
     */
    public CommandResponse unregisterCoderByHandle(String handle, int roundId) {
        Connection c = null;
        PreparedStatement ps = null;

        try {
            c = DBMS.getConnection();
            c.setAutoCommit(false);

            // Lookup the userid.
            ps = c.prepareStatement("SELECT user_id FROM user WHERE handle=?");
            ps.setString(1, handle);

            ResultSetContainer rsc = runSelectQuery(ps);

            if (rsc.getRowCount() == 0) {
                return new CommandFailedResponse("User " + handle + " was not found in the database.");
            }

            int userId = Integer.parseInt(rsc.getItem(0, 0).toString());
            ps.close();
            ps = null;

            // Verify user is already registered.
            if (!roundDao.isCoderRegistered(userId, roundId, c)) {
                return new CommandFailedResponse("User " + handle + " is not registered for this round.");
            }

            //remove the registration
            roundDao.deleteRoundRegistration(userId, roundId, c);

            ps = c.prepareStatement("DELETE FROM response WHERE user_id = ? AND question_id IN ("
                    + "SELECT question_id FROM round_question WHERE round_id = ? " + ")");
            ps.setInt(1, userId);
            ps.setInt(2, roundId);
            ps.executeUpdate();
            ps.close();
            c.commit();

            return new CommandSucceededResponse("Coder " + handle + " successfully unregistered");
        } catch (Exception e) {
            rollback(c);
            printException(e);

            return new CommandFailedResponse("Received exception unregistering coder; check log.");
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e1) {
                printException(e1);
            }

            try {
                if (c != null) {
                    c.close();
                }
            } catch (Exception e1) {
                printException(e1);
            }
        }
    }

    public Results registerCoderWithChecks(int coderId, int roundId, List surveyData)
        throws DBServicesException {
        s_trace.info("registerCoderWithChecks(" + coderId + "," + roundId + ")");

        Connection cnn = null;

        try {
            cnn = DBMS.getConnection();

            if (roundDao.isCoderRegistered(coderId, roundId, cnn)) {
                return new Results(false, "ALREADY_REGISTERED");
            }

            if (roundDao.getSegmentPhaseStatus(roundId, ContestConstants.REGISTRATION_SEGMENT_ID, cnn) != 0) {
                return new Results(false, "REGISTRATION_NOT_OPENED");
            }

            if (roundDao.isInvitational(roundId, cnn) && !roundDao.isInvited(roundId, coderId, cnn)) {
                return new Results(false, "NOT_INVITED");
            }

            if (roundDao.getRegistrationLimit(roundId, cnn) <= roundDao.getRegistrationCount(roundId, cnn)) {
                return new Results(false, "REGISTRATION_LIMIT");
            }

            Results results = registerCoder(coderId, roundId, surveyData, cnn);

            if (results.isSuccess()) {
                return new Results(true, "REGISTRATION_SUCCEEDED");
            } else {
                return new Results(false, "INTERNAL_SERVER_ERROR");
            }
        } catch (SQLException e) {
            s_trace.error("Exception while registering user", e);

            return new Results(false, "INTERNAL_SERVER_ERROR");
        } finally {
            DBMS.closeAndResetAC(cnn);
        }
    }

    /**
     * This method takes care of storing all the survey information and
     * inserting the proper record into the round_registration table
     */
    public Results registerCoder(int coderId, int roundId, List surveyData)
        throws DBServicesException {
        debug("registerCoder: " + coderId);

        Connection conn = null;

        try {
            conn = DBMS.getConnection();

            return registerCoder(coderId, roundId, surveyData, conn);
        } catch (SQLException e) {
            s_trace.error("Could not get connection", e);

            return new Results(false, "Internal Server Error");
        } finally {
            DBMS.closeAndResetAC(conn);
        }
    }
    /**
     * <p>
     * register the coder.
     * </p>
     * @param coderId the coder id.
     * @param roundId the contest round id.
     * @param surveyData the user survey data.
     * @param conn the db connection.
     * @return the register coder action result.
     */
    private Results registerCoder(int coderId, int roundId, List surveyData, Connection conn) {
        boolean recordSurvey = false;
        StringBuilder output = new StringBuilder();

        try {
            int questionsCnt = getRoundSurveyQuestionsCount(conn, roundId);

            // still need to validate answeres somehow
            if (surveyData.size() != questionsCnt) {
                s_trace.error("SurveyData.size(): " + surveyData.size() + " != numQuestions: " + questionsCnt);

                return new Results(false, "The answers to the survey were invalid. Please try again.");
            } else {
                debug("Survey data complete (" + surveyData.size() + "). Recording results");
                recordSurvey = true;
            }

            conn.setAutoCommit(false);

            boolean eligible = true;

            for (int i = 0; i < surveyData.size(); i++) {
                SurveyAnswerData ans = (SurveyAnswerData) surveyData.get(i);
                s_trace.debug("SurveyAnswer Eligible = " + ans.isEligible() + " Correct = " + ans.isCorrect());

                if (!ans.isEligible()) {
                    //ignore other question types except Eligible
                    continue;
                }
                ArrayList allAnswers = getRoundQuestionAnswers(ans.getQuestionID());
                if (ans.getType() == Question.MULTIPLECHOICE || ans.getType() == Question.SINGLECHOICE) {
                    if (null == ans.getChoices() || ans.getChoices().size() <= 0) {
                        eligible = false;
                    } else {
                        for (int j = 0; j < ans.getAnswers().size(); j++) {
                            String s = (String) ans.getAnswers().get(j);
                            for (int k = 0; k < allAnswers.size(); k++) {
                                AnswerData data = (AnswerData) allAnswers.get(k);
                                if (data.getText().equals(s)) {
                                    if (!data.isCorrect()) {
                                        eligible = false;
                                        break;
                                    }
                                }
                            }
                            if(!eligible) {
                                break;
                            }
                        }
                    }
                } else {
                    if (null == ans.getAnswers() || ans.getAnswers().size() <= 0) {
                        eligible = false;
                    } else {
                        String answerText = (String)ans.getAnswers().get(0);
                        boolean flag = false;
                        for (int j = 0; j < allAnswers.size(); j++) {
                            AnswerData data = (AnswerData)allAnswers.get(j);
                            if (answerText.equals(data.getText()) && data.isCorrect()) {
                                flag = true;
                                break;
                            }
                        }
                        if (!flag) {
                            eligible = false;
                        }
                    }
                }
                if (!eligible) {
                    break;
                }
            }

            if (recordSurvey) {
                recordSurvey(surveyData, conn, coderId, output);
                debug("Survey recorded succesfully");
            }

            s_trace.debug("Eligible = " + eligible);
            if (!eligible) {
                return new Results(false, "Based on your answers you are not eligible to participate in this match. " +
                        "If you believe this to be an error please try again or contact your administrator.");
            }
            
            // Check if coder is from an eligible country.
            if (eligible) {
                eligible = isCoderFromEligibleCountry(conn, coderId);
            }

            recordRegistration(conn, coderId, roundId, eligible);
            
            //handle record in algo_rating table
            handleCoderAlgoRatingRecord(coderId, Rating.ALGO);

            conn.commit();

            return new Results(true, "You have successfully registered for the match.");
        } catch (Exception e) {
            rollback(conn);
            s_trace.error("DBServices:registerCoder:insert: Error:", e);

            if (output.length() == 0) {
                output.append(
                    "Error recording your survey responses.  Please report this error to a TopCoder representative.");
            }

            return new Results(false, output.toString());
        }
    }

    /**
     * <p>
     * Check whether this option is correct or not.
     * </p>
     *
     * @param choiceData
     *          the choice data
     * @param allAnswers
     *          all answers
     * @return
     *          true if this option is correct
     * @since 1.3
     */
    private boolean verifyChoice(SurveyChoiceData choiceData, ArrayList allAnswers) {
        for (int i=0;i<allAnswers.size();i++) {
            AnswerData data = (AnswerData)allAnswers.get(i);
            if (data.getText().equals(choiceData.getText()) && data.isCorrect()) {
                return true;
            }
        }
        return false;
    }

    private void recordRegistration(Connection conn, int coderId, int eventId, boolean eligible)
        throws SQLException, DBServicesException {
        PreparedStatement ps = null;

        try {
            if (isHighSchoolRound(conn, eventId)) {
                ps = conn.prepareStatement("SELECT team_id FROM team_coder_xref WHERE coder_id = " + coderId);

                ResultSet rs = ps.executeQuery();
                rs.next();
                ps = conn.prepareStatement(
                        "INSERT INTO round_registration (coder_id, round_id, timestamp, eligible, team_id) VALUES (?,?,?,?,"
                        + rs.getInt(1) + ")");
            } else {
                ps = conn.prepareStatement(
                        "INSERT INTO round_registration (coder_id, round_id, timestamp, eligible) VALUES (?,?,?,?)");
            }

            ps.setInt(1, coderId);
            ps.setInt(2, eventId); // most likely round id here

            Timestamp now = new Timestamp(System.currentTimeMillis());
            ps.setTimestamp(3, now);

            if (eligible) {
                ps.setInt(4, 1);
            } else {
                ps.setInt(4, 0);
            }

            // execute query
            int rows = ps.executeUpdate();

            if (rows != 1) {
                throw new DBServicesException("Unexpected number of rows updated: " + rows);
            }

            // Update the terms acceptance table too
            ps = conn.prepareStatement(INSERT_TERMS_ACCEPTANCE_QUERY);
            ps.setInt(1, coderId);
            ps.setInt(2, eventId);
            ps.setTimestamp(3, now);
            rows = ps.executeUpdate();

            if (rows != 1) {
                throw new DBServicesException("Unexpected number of rows updated: " + rows);
            }
        } catch (DBServicesException e) {
            throw e;
        } catch (Exception e) {
            s_trace.error("Error recording round registration: ", e);
            throw new DBServicesException("Registration failed: " + e.toString());
        } finally {
            close(null, ps, null);
        }
    }

    public boolean isHighSchoolRound(long roundID) {
        Connection conn = null;

        try {
            conn = DBMS.getConnection();

            return isHighSchoolRound(conn, roundID);
        } catch (SQLException e) {
            s_trace.error("Error creating connection: isHighSchoolRound(): ", e);

            return false;
        } finally {
            close(conn, null, null);
        }
    }

    private boolean isHighSchoolRound(Connection conn, long roundID) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement(
                    "SELECT rt.algo_rating_type_id FROM round_type_lu rt INNER JOIN round r ON r.round_type_id=rt.round_type_id WHERE r.round_id = "
                    + roundID);
            rs = ps.executeQuery();

            if (rs.next() && (rs.getInt(1) == 2)) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            s_trace.error("Could not determine if high school round. ", e);

            return false;
        } finally {
            close(null, ps, rs);
        }
    }

    private boolean isCoderFromEligibleCountry(Connection conn, int coderId)
        throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement(
                    "SELECT ct.participating FROM country ct, user_address_xref c, address a WHERE c.user_id = ? AND a.country_code = ct.country_code AND a.address_id = c.address_id");
            ps.setInt(1, coderId);
            rs = ps.executeQuery();

            if (rs.next()) {
                int participatingCode = rs.getInt(1);
                s_trace.debug("Participating code of country = " + participatingCode);

                return participatingCode != 0;
            } else {
                s_trace.error("Failed to find a country for user: " + coderId);

                return false;
            }
        } finally {
            close(null, ps, rs);
        }
    }

    private void recordSurvey(List surveyData, Connection conn, int coderId, StringBuilder output)
        throws DBServicesException {
        try {
            for (int i = 0; i < surveyData.size(); i++) {
                try {
                    SurveyAnswerData ans = (SurveyAnswerData) surveyData.get(i);
                    int questionID = ans.getQuestionID();
                    int questionType = ans.getType();
                    List<String> answers = ans.getAnswers();
                    List<AnswerData> allAnswers = getRoundQuestionAnswers(ans.getQuestionID());
                    int answerID = -1;
                    
                    switch (questionType) {
                    case ContestConstants.RADIO_BUTTON: // It should just be an integer
                        for(AnswerData answer: allAnswers) {
                            if(answer.getText().equals(answers.get(0))) {
                                answerID = answer.getId();
                            }
                        }
                        recordResponse(conn, coderId, questionID, answerID, null);

                        break;

                    case ContestConstants.CHECK_BOX: // ArrayList of many integers

                        if (answers.size() == 0) {
                            output.append("You must select at least one choice for a check box question.");
                            throw new DBServicesException();
                        }

                        for (AnswerData answer: allAnswers) {
                            if(answers.contains(answer.getText())) {
                                recordResponse(conn, coderId, questionID, answer.getId(), null);
                            }
                            
                        }

                        break;

                    case ContestConstants.SHORT_ANSWER:
                    case ContestConstants.LONG_ANSWER:
                        // Assume one choice with the desired id
                        recordResponse(conn, coderId, questionID, allAnswers.get(0).getId(), (String) answers.get(0));

                        break;

                    default:
                        throw new IllegalArgumentException("Unknown question type: " + questionType);
                    }
                } catch (Exception e) {
                    if (output.length() == 0) {
                        output.append("Error recording your response for question #" + (i + 1)
                            + ".  Please try again.");
                    }

                    throw e;
                }
            }
        } catch (Exception e) {
            if (output.length() == 0) {
                output.append(
                    "There was an error recording your survey responses. Please contact us if you have additional problems.");
            }

            s_trace.error("ERROR: Recording survey responses.", e);
            throw new DBServicesException("Error recording survey");
        }    
    }

    private void recordResponse(Connection conn, int coderId, int questionID, int answerID, String response)
        throws SQLException {
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement(
                    "INSERT INTO response (user_id, question_id, answer_id, response, response_id) VALUES(?,?,?,?,?)");
            ps.setInt(1, coderId);
            ps.setInt(2, questionID); // question id

            if (answerID != 0) {
                ps.setInt(3, answerID); // answer id
            } else {
                ps.setNull(3, Types.INTEGER);
            }

            if (response == null) {
                ps.setNull(4, Types.BLOB);
            } else {
                ps.setString(4, response);
            }

            long id = -1;

            try {
                id = IdGeneratorClient.getSeqId(DBMS.RESPONSE_SEQ);
            } catch (IDGenerationException e) {
                e.printStackTrace();
            }

            ps.setLong(5, id);

            ps.executeUpdate();
        } finally {
            close(null, ps, null);
        }
    }

    public void archiveChat(ArrayList chatQueue) throws DBServicesException {
        debug("LoadBal: archiveChat called [!]");

        StringBuilder sqlStr = new StringBuilder(128);
        PreparedStatement ps = null;
        java.sql.Connection conn = null;

        try {
            debug("DOING A GENERIC ARCHIVE CHAT AT " + new Timestamp(System.currentTimeMillis()));
            conn = DBMS.getConnection();
            conn.setAutoCommit(false);
            sqlStr.replace(0, sqlStr.length(), "INSERT INTO chat_history ");
            sqlStr.append("  (coder_id,round_id, room_id, message, timestamp) ");
            sqlStr.append("VALUES (?,?,?,?,?)");
            ps = conn.prepareStatement(sqlStr.toString());

            String handle = null;

            for (int i = 0; i < chatQueue.size(); i++) {
                ps.clearParameters();

                ChatEvent event = (ChatEvent) chatQueue.get(i);
                String message = event.getMessage();

                if (event.getTargetType() == TCEvent.USER_TARGET) {
                    int index = message.indexOf("whispers to you");

                    if (index != -1) {
                        int target = event.getTarget();
                        handle = getHandle(target);

                        if (handle != null) {
                            StringBuilder msg = new StringBuilder(message);
                            msg.replace(index + 12, index + 15, handle);
                            message = msg.toString();
                        }
                    }
                }

                ps.setInt(1, event.getCoderID());
                ps.setInt(2, event.getRoundID());
                ps.setInt(3, event.getRoomID());
                ps.setString(4, message);
                ps.setTimestamp(5, new Timestamp(event.getCreateTime()));
                ps.executeUpdate();
            }

            conn.commit();
            debug("FINISHED ARCHIVING CHAT AT " + new Timestamp(System.currentTimeMillis()));
        } catch (Exception e) {
            rollback(conn);
            DBMS.printException(e);
            throw new DBServicesException("ContestServices: archiveChat: Error " + e);
        } finally {
            close(conn, ps, null);
        }
    }

    // added 2-20 rfairfax
    public boolean clearPracticeProblem(int coderId, int roundId, Long componentID, boolean teamClear) {
        if (s_trace.isDebugEnabled()) {
            debug("Clearing practice data for coderID = " + coderId + " roundID = " + roundId + " componentID = "
                + componentID);
        }

        Connection conn = null;

        try {
            conn = DBMS.getConnection();
            conn.setAutoCommit(false);

            StringBuilder inClause = new StringBuilder(10);
            List stateIds = getPracticeComponentStateIDsByComponentID(conn, coderId, roundId, componentID, teamClear);

            if (s_trace.isDebugEnabled()) {
                s_trace.debug("stateIds: " + stateIds);
            }

            if (stateIds.size() > 0) {
                inClause.append("(");

                for (int i = 0; i < stateIds.size(); i++) {
                    inClause.append("?");

                    if (i != (stateIds.size() - 1)) {
                        inClause.append(",");
                    }
                }

                inClause.append(")");

                clearPracticeData(conn, stateIds, "submission_class_file", inClause);
                clearPracticeData(conn, stateIds, "submission", inClause);
                clearPracticeData(conn, stateIds, "compilation_class_file", inClause);
                clearPracticeData(conn, stateIds, "compilation", inClause);
                clearPracticeData(conn, stateIds, "component_state", inClause);
            }

            clearPracticeSystemTestDataByComponentID(conn, coderId, roundId, componentID);
            conn.commit();

            return true;
        } catch (Exception e) {
            rollback(conn);
            s_trace.error("Exception in clearPracticeProblem", e);

            return false;
        } finally {
            close(conn, null, null);
        }
    }

    /**
     * This method clears a coder's practice room data
     */
    public boolean clearPracticer(int coderId, int roundId, boolean teamClear) {
        if (s_trace.isDebugEnabled()) {
            debug("Clearing practice data for coderID = " + coderId + " roundID = " + roundId);
        }

        Connection conn = null;

        try {
            conn = DBMS.getConnection();
            conn.setAutoCommit(false);

            StringBuilder inClause = new StringBuilder(10);
            List stateIds = getPracticeComponentStateIDs(conn, coderId, roundId, teamClear);

            if (s_trace.isDebugEnabled()) {
                s_trace.debug("stateIds: " + stateIds);
            }

            if (stateIds.size() > 0) {
                inClause.append("(");

                for (int i = 0; i < stateIds.size(); i++) {
                    inClause.append("?");

                    if (i != (stateIds.size() - 1)) {
                        inClause.append(",");
                    }
                }

                inClause.append(")");

                clearPracticeData(conn, stateIds, "submission_class_file", inClause);
                clearPracticeData(conn, stateIds, "submission", inClause);
                clearPracticeData(conn, stateIds, "compilation_class_file", inClause);
                clearPracticeData(conn, stateIds, "compilation", inClause);
                clearPracticeData(conn, stateIds, "component_state", inClause);
            }

            clearPracticeChallengeData(conn, coderId, roundId);
            clearPracticeSystemTestData(conn, coderId, roundId);
            conn.commit();

            return true;
        } catch (Exception e) {
            rollback(conn);
            s_trace.error("Exception in clearPracticer", e);

            return false;
        } finally {
            close(conn, null, null);
        }
    }

    private List getPracticeComponentStateIDs(Connection conn, int coderId, int roundId, boolean teamClear)
        throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            if (teamClear) {
                ps = conn.prepareStatement("SELECT component_state_id FROM component_state WHERE coder_id IN "
                        + "(SELECT coder_id FROM team_coder_xref WHERE team_id = ?) AND round_id = ? ");
            } else {
                ps = conn.prepareStatement(
                        "SELECT component_state_id FROM component_state WHERE coder_id = ? AND round_id = ?");
            }

            ps.setInt(1, coderId);
            ps.setInt(2, roundId);
            rs = ps.executeQuery();

            List stateIds = new Vector();

            while (rs.next())
                stateIds.add(Integer.valueOf(rs.getInt(1)));

            return stateIds;
        } finally {
            close(null, ps, rs);
        }
    }

    // added 2-20 rfairfax
    private List getPracticeComponentStateIDsByComponentID(Connection conn, int coderId, int roundId,
        Long componentID, boolean teamClear) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            if (teamClear) {
                ps = conn.prepareStatement("SELECT component_state_id FROM component_state WHERE coder_id IN "
                        + "(SELECT coder_id FROM team_coder_xref WHERE team_id = ?) AND round_id = ? AND component_id = ?");
            } else {
                ps = conn.prepareStatement(
                        "SELECT component_state_id FROM component_state WHERE coder_id = ? AND round_id = ? AND component_id = ?");
            }

            ps.setInt(1, coderId);
            ps.setInt(2, roundId);
            ps.setLong(3, componentID.longValue());
            rs = ps.executeQuery();

            List stateIds = new Vector();

            while (rs.next())
                stateIds.add(Integer.valueOf(rs.getInt(1)));

            return stateIds;
        } finally {
            close(null, ps, rs);
        }
    }

    // added 2-20 rfairfax
    private void clearPracticeSystemTestDataByComponentID(Connection conn, int coderId, int roundId, Long componentID)
        throws SQLException {
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement(
                    "DELETE FROM system_test_result WHERE coder_id = ? AND round_id = ? AND component_id = ?");
            ps.setInt(1, coderId);
            ps.setInt(2, roundId);
            ps.setLong(3, componentID.longValue());
            ps.executeUpdate();
        } finally {
            close(null, ps, null);
        }
    }

    private void clearPracticeSystemTestData(Connection conn, int coderId, int roundId)
        throws SQLException {
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement("DELETE FROM system_test_result WHERE coder_id = ? AND round_id = ?");
            ps.setInt(1, coderId);
            ps.setInt(2, roundId);
            ps.executeUpdate();
        } finally {
            close(null, ps, null);
        }
    }

    private void clearPracticeChallengeData(Connection conn, int coderId, int roundId)
        throws SQLException {
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement(
                    "DELETE FROM challenge WHERE (challenger_id = ? OR defendant_id = ?) AND round_id = ?");
            ps.setInt(1, coderId);
            ps.setInt(2, coderId);
            ps.setInt(3, roundId);
            ps.executeUpdate();
        } finally {
            close(null, ps, null);
        }
    }

    private void clearPracticeData(Connection conn, List stateIds, String query, StringBuilder inClause)
        throws SQLException {
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement("DELETE FROM " + query + " WHERE component_state_id IN " + inClause);

            for (int i = 1; i < (stateIds.size() + 1); i++)
                ps.setInt(i, ((Integer) stateIds.get(i - 1)).intValue());

            ps.executeUpdate();
        } finally {
            close(null, ps, null);
        }
    }

    /**
     * Closes out all the open connections in the connection history
     * @throws DBServicesException
     */
    public void deleteConnections() throws DBServicesException {
        StringBuilder sqlStr = new StringBuilder(128);
        PreparedStatement ps = null;
        java.sql.Connection conn = null;

        try {
            conn = DBMS.getConnection();
            sqlStr.append("UPDATE connection_history SET end_time = ? WHERE end_time is null");

            ps = conn.prepareStatement(sqlStr.toString());
            ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            ps.executeUpdate();

            debug("Closed connections");
        } catch (Exception e) {
            s_trace.error("error in deleteConnections", e);
            throw new DBServicesException("deleteConnections: Error " + e);
        } finally {
            close(conn, ps, null);
        }
    }

    /**
     * Returns the next server id from the sequence in the db
     */
    public int getNextServerID() throws DBServicesException {
        try {
            return IdGeneratorClient.getSeqIdAsInt(DBMS.SERVER_SEQ);
        } catch (Exception e) {
            s_trace.error("Exception in getNextServerID()", e);
            throw new EJBException(e.toString());
        }
    }

    /**
     * Adds a new entry into the connection history/ or updates the existing one
     */
    public void addConnection(String ip, String serverType, int servID, int connID, int coderID, String userName,
        Timestamp timestamp) throws DBServicesException {
        debug("addConnection");

        StringBuilder sqlStr = new StringBuilder(128);
        PreparedStatement ps = null;
        java.sql.Connection conn = null;

        try {
            conn = DBMS.getConnection();
            conn.setAutoCommit(false);

            sqlStr.append("INSERT INTO connection_history (server_id, connection_id, coder_id, ");
            sqlStr.append("start_time, client_ip) VALUES (?,?,?,?,?)");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, servID);
            ps.setInt(2, connID);
            ps.setInt(3, coderID);
            ps.setTimestamp(4, timestamp);
            ps.setString(5, ip);
            ps.executeUpdate();

            if (coderID > 0) {
                updateLastLogin(conn, coderID);
            }

            conn.commit();
        } catch (Exception e) {
            rollback(conn);
            s_trace.error("addConnection Error:", e);
            throw new DBServicesException("ContestServicesBean: addConnectionInfo: Error " + e);
        } finally {
            close(conn, ps, null);
        }
    }

    private void updateLastLogin(Connection conn, int coderID)
        throws SQLException, DBServicesException {
        PreparedStatement ps = null;

        try {
            if (DBMS.DB == DBMS.INFORMIX) {
                ps = conn.prepareStatement("UPDATE user SET last_login = CURRENT WHERE user_id = ?");
            } else {
                ps = conn.prepareStatement("UPDATE user SET last_login = CURRENT_TIMESTAMP WHERE user_id = ?");
            }

            ps.setInt(1, coderID);

            int rc = ps.executeUpdate();

            if (rc != 1) {
                throw new DBServicesException("ERROR: Setting USERS.LOGGED_IN for coderId " + coderID);
            }
        } finally {
            close(null, ps, null);
        }
    }

    /**
     * Closes out all a connection in the connection history
     */
    public void removeConnection(String serverType, int servId, int connId, Timestamp timestamp)
        throws DBServicesException {
        if (s_trace.isDebugEnabled()) {
            debug("DBServicesBean.removeConnection() called... serverType = " + serverType + " servId = " + servId
                + " connId = " + connId + " time = " + timestamp);
        }

        StringBuilder sqlStr = new StringBuilder(128);
        PreparedStatement ps = null;
        java.sql.Connection conn = null;

        try {
            conn = DBMS.getConnection();
            sqlStr.append("UPDATE connection_history SET end_time = ? WHERE server_id = ? AND connection_id = ?");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setTimestamp(1, timestamp);
            ps.setInt(2, servId);
            ps.setInt(3, connId);

            int rc = ps.executeUpdate();

            if (rc == 0) {
                throw new DBServicesException("Update was unsuccessful. (no records to update)");
            }
        } catch (Exception e) {
            DBMS.printException(e);
            throw new DBServicesException("ContestServices: removeConnection: Error " + e);
        } finally {
            close(conn, ps, null);
        }
    }

    /**
     * This method loads a collection of coders history/runtime stuff in a batch from DB
     */
    private void loadCoderInfoBatch(Connection conn, Round contestRound, Collection coders, int roomId,
        boolean mustLoadPassedTests) throws SQLException, DBServicesException {
        if (coders.size() == 0) {
            // No coder need to be loaded.
            return;
        }

        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder sqlStr = new StringBuilder();

        try {
            int contestId = contestRound.getContestID();
            int roundId = contestRound.getRoundID();
            loadCoderHistoryBatch(conn, coders, contestId, roundId, roomId);

            if (DBMS.DB == DBMS.INFORMIX) {
                sqlStr.append("SELECT cs.component_id, cs.status_id, cs.points, ");
                sqlStr.append(
                    "c.compilation_text, c.language_id, s.submission_text, s.language_id, cs.coder_id, c.open_time  ");
                sqlStr.append("FROM component_state cs, OUTER submission s, compilation c ");
                sqlStr.append(", room_result rs ");
                sqlStr.append("WHERE cs.round_id = ? ");
                sqlStr.append("AND s.component_state_id = cs.component_state_id ");
                sqlStr.append("AND cs.submission_number = s.submission_number ");
                sqlStr.append("AND cs.component_state_id = c.component_state_id ");
                sqlStr.append("AND s.component_state_id = c.component_state_id ");
                sqlStr.append("AND cs.coder_id = rs.coder_id AND rs.room_id = ?");

                //sqlStr.append("AND cs.coder_id IN (");
                //buildBatchParameters(sqlStr, coders.size());
                //sqlStr.append(")");
            } else {
                sqlStr.append("SELECT cs.component_id, cs.status_id, cs.points, ");
                sqlStr.append(
                    "c.compilation_text, cs.language_id, s.submission_text, s.language_id, cs.coder_id, c.open_time ");
                sqlStr.append("FROM component_state cs, submission s, compilation c ");
                sqlStr.append(", room_result rs ");
                sqlStr.append("WHERE cs.round_id = ? ");
                sqlStr.append("AND s.component_state_id = cs.component_state_id ");
                sqlStr.append("AND cs.submission_number = s.submission_number ");
                sqlStr.append("AND cs.component_state_id = c.component_state_id ");
                sqlStr.append("AND s.component_state_id = c.component_state_id ");
                sqlStr.append("AND cs.coder_id = rs.coder_id AND rs.room_id = ?");

                //sqlStr.append("AND cs.coder_id IN (");
                //buildBatchParameters(sqlStr, coders.size());
                //sqlStr.append(")");
            }

            loadCoderComponentsBatch(conn, sqlStr, roundId, coders, roomId, mustLoadPassedTests);
        } finally {
            close(null, ps, rs);
        }
    }

    /**
     * this puppy loads in the coder history/runtime stuff that's needed from the DB
     */
    private void loadCoderInfo(Connection conn, Round contestRound, Coder coder, boolean mustLoadPassedTests)
        throws SQLException, DBServicesException {
        debug("Loading coder info for: " + coder.getName());

        PreparedStatement ps = null;
        ResultSet rs = null;

        StringBuilder sqlStr = new StringBuilder(200);

        try {
            int contestId = contestRound.getContestID();
            int roundId = contestRound.getRoundID();

            CoderHistory hist = getCoderHistory(conn, coder.getID(), coder.getDivisionID(), contestRound);
            coder.setHistory(hist);

            if (DBMS.DB == DBMS.INFORMIX) {
                sqlStr.replace(0, sqlStr.length(), "SELECT cs.component_id, cs.status_id, cs.points, ");
                sqlStr.append("c.compilation_text, c.language_id, s.submission_text, s.language_id, c.open_time ");
                sqlStr.append("FROM component_state cs, OUTER submission s, compilation c ");
                sqlStr.append("WHERE cs.round_id = ? ");
                sqlStr.append("AND cs.coder_id = ? ");
                sqlStr.append("AND s.component_state_id = cs.component_state_id ");
                sqlStr.append("AND cs.submission_number = s.submission_number ");
                sqlStr.append("AND cs.component_state_id = c.component_state_id ");
                sqlStr.append("AND s.component_state_id = c.component_state_id ");
            } else {
                sqlStr.replace(0, sqlStr.length(), "SELECT cs.component_id, cs.status_id, cs.points, ");
                sqlStr.append("c.compilation_text, cs.language_id, s.submission_text, s.language_id, c.open_time ");
                sqlStr.append("FROM component_state cs, submission s, compilation c ");
                sqlStr.append("WHERE cs.round_id = ? ");
                sqlStr.append("AND cs.coder_id = ? ");
                sqlStr.append("AND s.component_state_id = cs.component_state_id ");
                sqlStr.append("AND cs.submission_number = s.submission_number ");
                sqlStr.append("AND cs.component_state_id = c.component_state_id ");
                sqlStr.append("AND s.component_state_id = c.component_state_id ");
            }

            loadCoderComponents(conn, sqlStr, roundId, coder, mustLoadPassedTests);
        } finally {
            close(null, ps, rs);
        }
    }

    private boolean isCoderEligible(Connection conn, int roundID, int coderID)
        throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement("SELECT eligible FROM round_registration WHERE round_id = ? AND coder_id = ?");
            ps.setInt(1, roundID);
            ps.setInt(2, coderID);
            rs = ps.executeQuery();

            return rs.next() && (rs.getInt(1) == 1);
        } finally {
            close(null, ps, rs);
        }
    }

    private static void loadCoderComponentsBatch(Connection conn, StringBuilder sqlStr, int roundId,
        Collection coders, int roomId, boolean mustLoadPassedTests)
        throws SQLException, DBServicesException {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, roundId);

            //int index = 2;
            Map coderMap = new HashMap(coders.size());

            //for (Iterator iter = coders.iterator(); iter.hasNext(); ++index) {
            for (Iterator iter = coders.iterator(); iter.hasNext();) {
                Coder coder = (Coder) iter.next();
                //ps.setInt(index, coder.getID());
                coderMap.put(Integer.valueOf(coder.getID()), coder);
            }

            ps.setInt(2, roomId);
            rs = ps.executeQuery();

            String progTxt = null;
            List challenged = new ArrayList();

            while (rs.next()) {
                int componentID = rs.getInt(1);
                int status = rs.getInt(2);
                int points = (int) Math.round(rs.getDouble(3) * 100);

                if ((status == ContestConstants.COMPILED_UNSUBMITTED) || (status == ContestConstants.PASSED)) {
                    progTxt = rs.getString(4);
                } else {
                    progTxt = rs.getString(6);
                }

                int langID = rs.getInt(5);
                int subLangID = rs.getInt(7);
                int coderID = rs.getInt(8);
                long openedTime = rs.getLong(9);
                Coder coder = (Coder) coderMap.get(Integer.valueOf(coderID));

                CoderComponent coderComponent = (CoderComponent) coder.getComponent(componentID);
                coderComponent.setStatus(status);
                coderComponent.setProgramText(progTxt);
                coderComponent.setLanguage(langID);
                coderComponent.setOpenedTime(openedTime);

                switch (status) {
                case ContestConstants.NOT_OPENED:

                    // weird shouldn't happen
                    break;

                case ContestConstants.LOOKED_AT:
                case ContestConstants.COMPILED_UNSUBMITTED:
                case ContestConstants.PASSED:
                    break;

                case ContestConstants.CHALLENGE_SUCCEEDED:
                    // set challenger
                    challenged.add(coderComponent);

                case ContestConstants.NOT_CHALLENGED: // submitted
                case ContestConstants.CHALLENGE_FAILED:
                case ContestConstants.SYSTEM_TEST_SUCCEEDED:
                case ContestConstants.SYSTEM_TEST_FAILED:
                    coderComponent.setSubmittedValue(points);
                    coderComponent.setSubmittedProgramText(progTxt);
                    coderComponent.setSubmittedLanguage(subLangID);

                    if (mustLoadPassedTests
                            && ((status == ContestConstants.SYSTEM_TEST_SUCCEEDED)
                            || (status == ContestConstants.SYSTEM_TEST_FAILED))) {
                        coderComponent.setPassedSystemTests(new Integer(getPassedSystemTests(conn,
                                    coder.getRoundID(), coder.getID(), componentID)));
                    }

                    break;

                default:
                    throw new DBServicesException("Invalid status type: " + status);
                }

                if (s_trace.isDebugEnabled()) {
                    s_trace.debug("filling out component: " + coderComponent + " to " + coder);
                }
            }

            loadChallengerBatch(conn, challenged);
        } finally {
            close(null, ps, rs);
        }
    }

    private static void loadCoderComponents(Connection conn, StringBuilder sqlStr, int roundId, Coder coder,
        boolean mustLoadPassedTests) throws SQLException, DBServicesException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        s_trace.info("start loadCoderComponents, roundId=" + roundId + ", coderId=" + coder.getID());

        try {
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, roundId);
            ps.setInt(2, coder.getID());
            rs = ps.executeQuery();

            String progTxt = null;

            while (rs.next()) {
                int componentID = rs.getInt(1);
                int status = rs.getInt(2);
                int points = (int) Math.round(rs.getDouble(3) * 100);

                if ((status == ContestConstants.COMPILED_UNSUBMITTED) || (status == ContestConstants.PASSED)) {
                    progTxt = rs.getString(4);
                } else {
                    progTxt = rs.getString(6);
                }

                int langID = rs.getInt(5);
                int subLangID = rs.getInt(7);
                long openedTime = rs.getLong(8);

                CoderComponent coderComponent = (CoderComponent) coder.getComponent(componentID);
                coderComponent.setStatus(status);
                coderComponent.setProgramText(progTxt);
                coderComponent.setLanguage(langID);
                coderComponent.setOpenedTime(openedTime);

                switch (status) {
                case ContestConstants.NOT_OPENED:

                    // weird shouldn't happen
                    break;

                case ContestConstants.LOOKED_AT:
                case ContestConstants.COMPILED_UNSUBMITTED:
                case ContestConstants.PASSED:
                    break;

                case ContestConstants.CHALLENGE_SUCCEEDED:
                    // set challenger
                    coderComponent.setChallenger(loadChallenger(conn, coderComponent));

                case ContestConstants.NOT_CHALLENGED: // submitted
                case ContestConstants.CHALLENGE_FAILED:
                case ContestConstants.SYSTEM_TEST_SUCCEEDED:
                case ContestConstants.SYSTEM_TEST_FAILED:
                    coderComponent.setSubmittedValue(points);
                    coderComponent.setSubmittedProgramText(progTxt);
                    coderComponent.setSubmittedLanguage(subLangID);

                    if (mustLoadPassedTests) {
                        coderComponent.setPassedSystemTests(new Integer(getPassedSystemTests(conn,
                                    coder.getRoundID(), coder.getID(), componentID)));
                    }

                    break;

                default:
                    throw new DBServicesException("Invalid status type: " + status);
                }

                if (s_trace.isDebugEnabled()) {
                    s_trace.debug("filling out component: " + coderComponent + " to " + coder);
                }
            }
            s_trace.info("finished loadCoderComponents, roundId=" + roundId + ", coderId=" + coder.getID());
        } finally {
            close(null, ps, rs);
        }
    }

    private static int getPassedSystemTests(Connection conn, int roundID, int coderID, int componentID)
        throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement(
                    "SELECT count(*) FROM system_test_result WHERE round_id = ? AND coder_id = ? AND component_id = ? AND succeeded = ?");
            ps.setInt(1, roundID);
            ps.setInt(2, coderID);
            ps.setInt(3, componentID);
            ps.setInt(4, 1);
            rs = ps.executeQuery();
            rs.next();

            return rs.getInt(1);
        } finally {
            DBMS.close(ps, rs);
        }
    }

    public LongCoderComponent getLongCoderComponent(int roundId, int coderId, int componentId)
        throws DBServicesException {
        Connection cnn = null;

        try {
            cnn = DBMS.getConnection();

            LongCoderComponent longCoderComponent = new LongCoderComponent(coderId, componentId);
            loadLongCoderComponent(cnn, roundId, coderId, componentId, longCoderComponent);

            return longCoderComponent;
        } catch (SQLException e) {
            s_trace.error("Could not refresh coder component", e);
            printException(e);
            throw new DBServicesException("Could not refresh coder component: " + e.getMessage());
        } finally {
            DBMS.close(cnn);
        }
    }

    private static void loadLongCoderComponents(Connection conn, int roundId, Coder coder)
        throws SQLException {
        long[] componentIDs = coder.getComponentIDs();

        for (int i = 0; i < componentIDs.length; i++) {
            loadLongCoderComponent(conn, roundId, coder.getID(), (int) componentIDs[i],
                (LongCoderComponent) coder.getComponent(componentIDs[i]));
        }
    }

    private static void loadLongCoderComponent(Connection conn, int roundId, int coderId, int componentId,
        LongCoderComponent coderComponent) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement(GET_LONG_CODER_COMPONENT_QUERY);
            ps.setInt(1, roundId);
            ps.setInt(2, coderId);
            ps.setInt(3, componentId);
            rs = ps.executeQuery();

            while (rs.next()) {
                int componentID = rs.getInt(1);
                int status = rs.getInt(2);
                int points = (int) Math.round(rs.getDouble(3) * 100);
                String lastSavedOrCompiledText = rs.getString(4);
                int langID = rs.getInt(5);
                String lastSubmittedCode = rs.getString(6);
                int lastSubmittedLang = rs.getInt(7);
                String lastExampleCode = rs.getString(8);
                int lastExampleLang = rs.getInt(9);
                long lastSubmittedTime = rs.getLong(10);
                long lastExampleTime = rs.getLong(11);
                long openTime = rs.getLong(12);

                debug("Setting long component status: " + status + " componentID = " + componentID + " coderId = "
                    + coderId);
                coderComponent.setStatus(status);
                coderComponent.setProgramText(lastSavedOrCompiledText);
                coderComponent.setLanguage(langID);
                coderComponent.setSubmissionCount(rs.getInt(12));
                coderComponent.setExampleSubmissionCount(rs.getInt(13));
                coderComponent.setOpenedTime(openTime);

                if (lastSubmittedCode != null) {
                    coderComponent.setSubmittedValue(points);
                    coderComponent.setSubmittedProgramText(lastSubmittedCode);
                    coderComponent.setSubmittedLanguage(lastSubmittedLang);
                    coderComponent.setSubmittedTime(lastSubmittedTime);
                }

                if (lastExampleCode != null) {
                    coderComponent.setExampleSubmittedProgramText(lastExampleCode);
                    coderComponent.setExampleSubmittedLanguage(lastExampleLang);
                    coderComponent.setExampleSubmittedTime(lastExampleTime);
                }

                debug("filling out long component: " + coderComponent + " to " + componentId);
            }
        } finally {
            close(null, ps, rs);
        }
    }

    private static void loadChallengerBatch(Connection conn, Collection components)
        throws SQLException {
        if (components.size() == 0) {
            return;
        }

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            StringBuilder sqlStr = new StringBuilder();

            sqlStr.append("SELECT handle, c.component_id, c.defendant_id  FROM user u, challenge c ");
            sqlStr.append("WHERE u.user_id = c.challenger_id AND ");
            sqlStr.append("c.succeeded = 1 AND (");

            for (int i = 0; i < components.size(); ++i) {
                if (i != 0) {
                    sqlStr.append(" OR ");
                }

                sqlStr.append("(c.component_id = ? AND c.defendant_id = ?)");
            }

            sqlStr.append(")");

            ps = conn.prepareStatement(sqlStr.toString());

            int index = 1;
            Map coderMap = new HashMap();
            ;

            for (Iterator iter = components.iterator(); iter.hasNext();) {
                CoderComponent component = (CoderComponent) iter.next();
                ps.setInt(index++, component.getComponentID());
                ps.setInt(index++, component.getCoderID());

                Integer coderId = Integer.valueOf(component.getCoderID());
                Integer componentId = Integer.valueOf(component.getComponentID());

                if (!coderMap.containsKey(coderId)) {
                    coderMap.put(coderId, new HashMap());
                }

                ((Map) coderMap.get(coderId)).put(componentId, component);
            }

            rs = ps.executeQuery();

            while (rs.next()) {
                Integer coderId = Integer.valueOf(rs.getInt(3));
                Integer componentId = Integer.valueOf(rs.getInt(2));
                CoderComponent component = (CoderComponent) ((Map) coderMap.get(coderId)).get(componentId);
                component.setChallenger(rs.getString(1));
            }
        } finally {
            close(null, ps, rs);
        }
    }

    private static String loadChallenger(Connection conn, CoderComponent component)
        throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            StringBuilder sqlStr = new StringBuilder();

            sqlStr.append("SELECT FIRST 1 handle FROM user u, challenge c ");
            sqlStr.append("WHERE u.user_id = c.challenger_id AND ");
            sqlStr.append("c.component_id = ? AND c.defendant_id = ? ");
            sqlStr.append("AND c.succeeded = 1 ORDER BY c.challenge_id desc");

            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, component.getComponentID());
            ps.setInt(2, component.getCoderID());

            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("handle");
            }

            rs.close();
            ps.close();
        } finally {
            close(null, ps, rs);
        }

        return null;
    }

    /**
     * this puppy loads in the coder history/runtime stuff that's needed from the DB
     */
    private void loadTeamInfo(Round contestRound, TeamCoder teamCoder, Connection conn)
        throws SQLException, DBServicesException {
        debug("Loading team coder info for: " + teamCoder.getName());

        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder sqlStr = new StringBuilder(200);

        try {
            int contestId = contestRound.getContestID();
            int roundId = contestRound.getRoundID();

            CoderHistory hist = getTeamHistory(conn, teamCoder, teamCoder.getDivisionID(), contestId, contestRound);
            teamCoder.setHistory(hist);
            sqlStr.replace(0, sqlStr.length(), "SELECT cs.component_id, cs.status_id, cs.points, ");
            sqlStr.append("c.compilation_text, c.language_id, s.submission_text, s.language_id, c.open_time ");
            sqlStr.append(
                "FROM component_state cs, team_coder_component_xref tcc, outer submission s, compilation c  ");
            sqlStr.append("WHERE cs.round_id = ? ");
            sqlStr.append("AND tcc.team_id = ? ");
            sqlStr.append("AND tcc.active = 1 ");
            sqlStr.append("AND tcc.component_id = cs.component_id ");
            sqlStr.append("AND cs.coder_id = tcc.coder_id ");
            sqlStr.append("AND s.component_state_id = cs.component_state_id ");
            sqlStr.append("AND cs.submission_number = s.submission_number ");
            sqlStr.append("AND cs.component_state_id = c.component_state_id ");
            sqlStr.append("AND s.component_state_id = c.component_state_id ");
            sqlStr.append("AND tcc.round_id = cs.round_id ");
            loadCoderComponents(conn, sqlStr, roundId, teamCoder,
                contestRound.getRoundProperties().allowsScoreType(ResultDisplayType.PASSED_TESTS));

            sqlStr.replace(0, sqlStr.length(), "SELECT rs.attended, rs.old_rating ");
            sqlStr.append(" FROM room_result rs JOIN team_coder_xref tc ON rs.coder_id = tc.coder_id ");
            sqlStr.append(" WHERE rs.round_id = ? AND rs.coder_id = ? AND tc.captain = " + TeamConstants.TEAM_CAPTAIN);

            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, roundId);
            ps.setInt(2, teamCoder.getID());
            rs = ps.executeQuery();

            if (rs.next()) {
                String attended = rs.getString(1);

                if ((attended != null) && attended.equals("Y")) {
                    teamCoder.setAttended(true);
                }

                int oldRating = rs.getInt(2);
                teamCoder.setOldRating(oldRating);
            } else {
                debug("No attended or eligibility data for team " + teamCoder.getName() + " round #" + roundId);
            }

            rs.close();
            rs = null;
            ps.close();
            ps = null;

            ps = conn.prepareStatement(
                    "SELECT COUNT(*) FROM round_registration rr JOIN team_coder_xref tc ON tc.coder_id = rr.coder_id "
                    + " WHERE tc.team_id = ? AND rr.eligible = 1");
            ps.setInt(1, teamCoder.getID());
            rs = ps.executeQuery();
            rs.next();
            teamCoder.setEligible(rs.getInt(1) > 0);

            // Figure out which users on the team have opened which problems
            sqlStr.replace(0, sqlStr.length(), "SELECT cs.component_id, cs.coder_id ");
            sqlStr.append("FROM component_state cs ");
            sqlStr.append("WHERE cs.round_id = ? ");
            sqlStr.append("AND cs.coder_id IN (SELECT coder_id FROM team_coder_xref WHERE team_id = ? ) ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, roundId);
            ps.setInt(2, teamCoder.getID());
            rs = ps.executeQuery();

            HashMap openedComponents = new HashMap();

            while (rs.next()) {
                if (openedComponents.get(new Long(rs.getInt(1))) == null) {
                    openedComponents.put(new Long(rs.getInt(1)), new HashSet());
                }

                ((HashSet) openedComponents.get(new Long(rs.getInt(1)))).add(Integer.valueOf(rs.getInt(2)));
            }

            teamCoder.setOpenedComponents(openedComponents);
        } finally {
            close(null, ps, rs);
        }
    }

    /**
     * Use this guy to get a map of RoundID => AdminRoomID
     */
    public HashMap getAdminRoomMap() throws DBServicesException {
        java.sql.Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder sqlStr = new StringBuilder(256);
        HashMap map = new HashMap(100);

        try {
            conn = DBMS.getConnection();
            sqlStr.replace(0, sqlStr.length(), "SELECT r.room_id, r.round_id FROM room r WHERE r.room_type_id IN (");
            sqlStr.append(ContestConstants.ADMIN_ROOM_TYPE_ID);
            sqlStr.append(",");
            sqlStr.append(ContestConstants.TEAM_ADMIN_ROOM_TYPE_ID);
            sqlStr.append(")");
            ps = conn.prepareStatement(sqlStr.toString());
            rs = ps.executeQuery();

            while (rs.next()) {
                int roomId = rs.getInt(1);
                int roundId = rs.getInt(2);
                map.put(Integer.valueOf(roundId), Integer.valueOf(roomId));
            }

            return map;
        } catch (Exception e) {
            s_trace.error("Error in getAdminRoomMap", e);
            throw new DBServicesException(e.toString());
        } finally {
            close(conn, ps, rs);
        }
    }

    /**
     * This method adds a new broadcast to the broadcast table
     * Author: EtherMage
     */
    public void addNewBroadcast(long timeSent, String message, int round_id, int componentID, int sent_by_user_id,
        int broadcast_type_id, int status_id) throws DBServicesException {
        String sqlStr = null;
        PreparedStatement ps = null;
        java.sql.Connection conn = null;

        try {
            conn = DBMS.getConnection();

            sqlStr = "INSERT INTO broadcast (broadcast_id, date_sent, message, round_id, component_id, sent_by_user_id, ";
            sqlStr += "broadcast_type_id, status_id) ";
            sqlStr += "VALUES (?,?,?,?,?,?,?,?)";
            ps = conn.prepareStatement(sqlStr);

            ps.setInt(1, IdGeneratorClient.getSeqIdAsInt(DBMS.BROADCAST_SEQ));
            ps.setTimestamp(2, new Timestamp(timeSent));
            ps.setBytes(3, DBMS.serializeBlobObject(message));

            if (round_id != -1) {
                ps.setInt(4, round_id);
            } else {
                ps.setNull(4, java.sql.Types.INTEGER);
            }

            if (componentID != -1) {
                ps.setInt(5, componentID);
            } else {
                ps.setNull(5, java.sql.Types.INTEGER);
            }

            if (sent_by_user_id != -1) {
                ps.setInt(6, sent_by_user_id);
            } else {
                ps.setNull(6, java.sql.Types.INTEGER);
            }

            ps.setInt(7, broadcast_type_id);

            if (status_id != -1) {
                ps.setInt(8, status_id);
            } else {
                ps.setNull(8, java.sql.Types.INTEGER);
            }

            ps.executeUpdate();
        } catch (Exception e) {
            printException(e);
            throw new DBServicesException(e.toString());
        } finally {
            close(conn, ps, null);
        }
    }

    /**
     * Returns a map of specific data for a given round.
     * Works with topcoder_dw for speed reasons.
     */
    private Map<Integer, Integer> getMapForRoundFromDW(String sql, List<Integer> userIDs, Connection dwConn)
        throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;

        StringBuilder sb = new StringBuilder();
        for (int ID : userIDs) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(ID);
        }

        sql = sql.replace("?", sb.toString());

        try {
            ps = dwConn.prepareStatement(sql);
            rs = ps.executeQuery();

            Map<Integer, Integer> res = new HashMap<Integer, Integer>();

            while (rs.next()) {
                res.put(rs.getInt(1), rs.getInt(2));
            }

            return res;
        } catch (Exception e) {
            e.printStackTrace();
            s_trace.error("getMapForRoundFromDW()", e);
        } finally {
            ps.close();
            rs.close();
        }

        return null;
    }

    /**
     * Returns a map of specific data for a given round
     */
    private Map<Integer, Integer> getMapForRound(String sql, int roundId, Connection conn)
        throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, roundId);
            rs = ps.executeQuery();

            Map<Integer, Integer> res = new HashMap<Integer, Integer>();

            while (rs.next()) {
                res.put(rs.getInt(1), rs.getInt(2));
            }

            return res;
        } catch (Exception e) {
            e.printStackTrace();
            s_trace.error("Error in getMapForRound()", e);
        } finally {
            ps.close();
            rs.close();
        }

        return null;
    }

    /**
     * Returns a collection of registered users for a given round
     */
    private Collection getAllRegisteredUsersForRound(int roundId, Connection conn, Connection dwConn)
        throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder sqlStr = new StringBuilder(256);

        try {
            s_trace.info("using DW version");
            s_trace.info("getAllRegisteredUsersForRound: checkpoint 1");

            sqlStr.replace(0, sqlStr.length(),
                "SELECT rr.coder_id, rr.eligible, NVL(il.seed,1) as seed , NVL(il.tournament_rating,0) as tournament_rating "
                + "FROM round_registration rr, " + "OUTER(invite_list il) " + "WHERE rr.round_id = ? "
                + "and il.round_id = rr.round_id " + "and il.coder_id = rr.coder_id");

            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, roundId);
            s_trace.info("getAllRegisteredUsersForRound: checkpoint 2");
            rs = ps.executeQuery();
            s_trace.info("getAllRegisteredUsersForRound: checkpoint 3");

            List userIDs = new ArrayList();
            Map params = new HashMap();

            while (rs.next()) {
                Integer userId = Integer.valueOf(rs.getInt(1));
                userIDs.add(userId);

                int[] param = new int[3];
                param[0] = rs.getInt(2);
                param[1] = rs.getInt(3);
                param[2] = rs.getInt(4);
                params.put(userId, param);
            }

            s_trace.info("start getting maps");

            Map<Integer, Integer> newRatingMap = getMapForRoundFromDW(GET_NEW_RATING_BATCH, userIDs, dwConn);
            s_trace.info("new rating map (DW)");

            Map<Integer, Integer> newHSRatingMap = getMapForRoundFromDW(GET_NEW_HS_RATING_BATCH, userIDs, dwConn);
            s_trace.info("new hs rating map (DW)");

            Map<Integer, Integer> newMMRatingMap = getMapForRoundFromDW(GET_NEW_MM_RATING_BATCH, userIDs, dwConn);
            s_trace.info("new mm rating map (DW)");

            // Map<Integer, Integer> isLevelOneAdminMap = getMapForRound(GET_IS_LEVEL_ONE_ADMIN_BATCH, roundId, conn);
            Map<Integer, Integer> isLevelOneAdminMap = new HashMap<Integer, Integer>();
            s_trace.info("is level one admin map (empty)");

            // Map<Integer, Integer> isLevelTwoAdminMap = getMapForRound(GET_IS_LEVEL_TWO_ADMIN_BATCH, roundId, conn);
            Map<Integer, Integer> isLevelTwoAdminMap = new HashMap<Integer, Integer>();
            s_trace.info("is level two admin map (empty");
            s_trace.info("end getting maps");

            s_trace.info("getAllRegisteredUsersForRound: checkpoint 4");

            List regUsers = getUserBatch(conn, roundId, userIDs, newRatingMap, newHSRatingMap, newMMRatingMap,
                    isLevelOneAdminMap, isLevelTwoAdminMap);
            s_trace.info("getAllRegisteredUsersForRound: checkpoint 5");

            for (Iterator iter = regUsers.iterator(); iter.hasNext();) {
                User user = (User) iter.next();
                int[] param = (int[]) params.get(Integer.valueOf(user.getID()));
                user.setEligible(param[0] != 0);
                user.setSeed(param[1]);
                user.setTournamentRating(param[2]);
            }

            s_trace.info("getAllRegisteredUsersForRound: checkpoint 6");

            return regUsers;
        } catch (Exception e) {
            s_trace.error("Error in getAllRegisteredUsersForRound()", e);
        } finally {
            //close(null, ps, rs);
            ps.close();
            rs.close();
        }

        return null;
    }

    /**
     * Returns a collection of registered users for a given round
     */
    private Collection getAllRegisteredTeamsForRound(int roundId, Connection conn)
        throws DBServicesException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder sqlStr = new StringBuilder(256);

        try {
            ArrayList regTeams = new ArrayList(400);
            sqlStr.replace(0, sqlStr.length(),
                "SELECT DISTINCT tc.team_id FROM round_registration rr, team_coder_xref tc ");
            sqlStr.append("WHERE tc.coder_id=rr.coder_id AND rr.round_id = ?");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, roundId);
            rs = ps.executeQuery();

            while (rs.next()) {
                Team team = getTeam(conn, rs.getInt(1), roundId);
                regTeams.add(team);
            }

            s_trace.info("using DW version");
            return regTeams;
        } catch (Exception e) {
            printException(e);
            throw new DBServicesException(e.toString());
        } finally {
            close(null, ps, rs);
        }
    }

    /**
     * Takes a AssignedRoom from the RoomAssigner and creates the appropriate entries in the DB
     */
    private int createAssignedRoom(int roomId, int roundId, int seed, AssignedRoom room, Connection conn,
        int ratingType) throws SQLException {
        // insert room table into the db first
        insertRoom(conn, roomId, roundId, room);

        // insert the assigned coders
        ArrayList users = room.getUsers();
        seed = assignCodersToRoom(conn, users, roundId, roomId, seed, room, ratingType);

        return seed;
    }

    private int assignCodersToRoom(Connection conn, ArrayList users, int roundId, int roomId, int seed,
        AssignedRoom room, int ratingType) throws SQLException {
        StringBuilder sqlStr = new StringBuilder();
        PreparedStatement ps = null;
        sqlStr.replace(0, sqlStr.length(), "INSERT INTO room_result (round_id,room_id,coder_id,room_seed,");
        sqlStr.append(
            "paid,room_placed,division_placed,attended, advanced, overall_rank, point_total, division_seed) ");
        sqlStr.append("VALUES (?,?,?,?, 0, 0, 0, 'N', 'N', 0, 0,?)");

        try {
            ps = conn.prepareStatement(sqlStr.toString());

            for (int i = 0; i < users.size(); i++) {
                User user = (User) users.get(i);
                ps.clearParameters();
                ps.setInt(1, roundId);
                ps.setInt(2, roomId);
                ps.setInt(3, user.getID());
                ps.setInt(4, i + 1); // TODO: assuming they are in seeded order
                ps.setInt(5, seed);
                seed++;
                ps.executeUpdate();
                debug("Adding coder with  rating:" + user.getRating(ratingType).getRating() + " to room "
                    + room.getName());
            }

            return seed;
        } finally {
            DBMS.close(ps);
        }
    }

    public int createNewQualRoom(int roundId) throws DBServicesException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            int roomId = IdGeneratorClient.getSeqIdAsInt(DBMS.ROOM_SEQ);

            conn = DBMS.getConnection();

            int maxRoomNumber;

            // find the next room number
            StringBuilder sqlStr = new StringBuilder();
            sqlStr.append("SELECT max(SUBSTR(name, 6)::decimal) as max_room_number ");
            sqlStr.append("FROM room WHERE round_id = ? ");
            sqlStr.append("AND room_type_id = 2 ORDER BY 1");

            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, roundId);

            rs = ps.executeQuery();

            if (rs.next()) {
                maxRoomNumber = rs.getInt("max_room_number");
            } else {
                throw new DBServicesException("No room number found");
            }

            rs.close();
            ps.close();

            rs = null;
            ps = null;

            AssignedRoom room = new AssignedRoom("Room " + (maxRoomNumber + 1), 1, true, false);
            insertRoom(conn, roomId, roundId, room);

            return roomId;
        } catch (Exception e) {
            printException(e);
            throw new DBServicesException(e.toString());
        } finally {
            DBMS.close(conn, ps, rs);
        }
    }

    private void insertRoom(Connection conn, int roomId, int roundId, AssignedRoom room)
        throws SQLException {
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement(
                    "INSERT INTO room (room_id,round_id,name,division_id,room_type_id,eligible,unrated) VALUES (?,?,?,?,?,?,?)");
            ps.setInt(1, roomId);
            ps.setInt(2, roundId);
            ps.setString(3, room.getName());
            ps.setInt(4, room.getDivisionID());
            ps.setInt(5, ContestConstants.CONTEST_ROOM_TYPE_ID);

            int eligible = 0;

            if (room.isEligible()) {
                eligible = 1;
            }

            ps.setInt(6, eligible);

            int unrated = 0;

            if (room.isUnrated()) {
                unrated = 1;
            }

            ps.setInt(7, unrated);
            debug("Adding room id " + roomId + " " + room.getName() + ")");
            ps.executeUpdate();
        } finally {
            DBMS.close(ps);
        }
    }

    /**
     * Takes a AssignedRoom from the RoomAssigner and creates the appropriate entries in the DB
     */
    private int createAssignedTeamRoom(int roomId, int roundId, int seed, AssignedTeamRoom room, Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder sqlStr = new StringBuilder(256);
        int rows;

        try {
            // insert room table into the db first
            sqlStr.replace(0, sqlStr.length(),
                "INSERT INTO room (room_id,round_id,name,division_id,room_type_id,eligible,unrated) VALUES (?,?,?,?,?,?,?)");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, roomId);
            ps.setInt(2, roundId);
            ps.setString(3, room.getName());
            ps.setInt(4, room.getDivisionID());
            ps.setInt(5, ContestConstants.TEAM_CONTEST_ROOM_TYPE_ID);

            int eligible = 0;

            if (room.isEligible()) {
                eligible = 1;
            }

            ps.setInt(6, eligible);

            int unrated = 0;

            if (room.isUnrated()) {
                unrated = 1;
            }

            ps.setInt(7, unrated);
            debug("Adding room id " + roomId + " " + room.getName() + ")");
            rows = ps.executeUpdate();

            if (rows == 0) {
                throw new EJBException("ERROR: Creating room from room assignments: " + room);
            }

            // insert the assigned coders
            ArrayList teams = room.getTeams();
            sqlStr.replace(0, sqlStr.length(),
                "INSERT INTO room_result (round_id,room_id,coder_id,room_seed,old_rating,new_rating,");
            sqlStr.append(
                "paid,room_placed,division_placed,attended, advanced, overall_rank, point_total, division_seed) ");
            sqlStr.append("VALUES (?,?,?,?,?,?, 0, 0, 0, 'N', 'N', 0, 0,?)");
            ps = conn.prepareStatement(sqlStr.toString());

            for (int j = 0; j < teams.size(); j++) {
                Team team = ((Team) teams.get(j));
                Collection users = team.getMembers();

                for (Iterator it = users.iterator(); it.hasNext();) {
                    int userID = ((Integer) it.next()).intValue();
                    ps.clearParameters();
                    ps.setInt(1, roundId);
                    ps.setInt(2, roomId);
                    ps.setInt(3, userID);
                    ps.setInt(4, j + 1); // TODO: assuming they are in seeded order
                    ps.setInt(5, team.getRating());
                    ps.setInt(6, team.getRating());
                    ps.setInt(7, seed);
                    rows = ps.executeUpdate();

                    if (rows != 1) {
                        throw new RuntimeException("ERROR: Inserting coder " + userID + " into room "
                            + room.getName());
                    } else {
                        debug("Adding coder with team rating:" + team.getRating() + " to room " + room.getName());
                    }
                }

                seed++;
            }

            return seed;
        } catch (Exception e) {
            s_trace.error("Error in createAssignedTeamRoom()", e);

            return seed;
        } finally {
            close(null, ps, rs);
        }
    }

    private void classicAssignRooms(int roundId, boolean byDivision, int codersPerRoom) {
        java.sql.Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        // ResultSet rs2 = null;
        StringBuilder sqlStr = new StringBuilder(256);
        int rows;
        s_trace.debug("classicAssignRooms");

        try {
            conn = DBMS.getConnection();

            // Get the registered coders:
            sqlStr.replace(0, sqlStr.length(),
                "SELECT er.coder_id, cr.rating, cr.rating_no_vol FROM round_registration er, rating cr ");
            sqlStr.append("WHERE er.round_id = ? AND er.coder_id = cr.coder_id ");
            sqlStr.append("ORDER BY rating_no_vol desc");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, roundId);
            rs = ps.executeQuery();

            // make 2 ArrayLists of coder IDs, one ArrayList per division
            ArrayList aCoderIds = new ArrayList(); //list of good coders (Division A)
            ArrayList bCoderIds = new ArrayList(); //list of crappy coders (Division B)
            ArrayList aRatings = new ArrayList();
            ArrayList bRatings = new ArrayList();
            int rating = 0;
            int rating_no_vol = 0;

            while (rs.next()) {
                rating = (Integer.valueOf(rs.getInt(2))).intValue();
                rating_no_vol = (Integer.valueOf(rs.getInt(3))).intValue();

                // If it's not by division, assume everyone is a good coder
                if (!byDivision || (rating >= ContestConstants.DIVISION_SPLIT)) {
                    aCoderIds.add(Integer.valueOf(rs.getInt(1)));
                    aRatings.add(Integer.valueOf(rating_no_vol));
                } else {
                    bCoderIds.add(Integer.valueOf(rs.getInt(1)));
                    bRatings.add(Integer.valueOf(rating_no_vol));
                }
            }

            int aCoders = aCoderIds.size();
            int bCoders = bCoderIds.size();

            // insert number of rooms into the database:
            int numARooms = ((aCoders + codersPerRoom) - 1) / codersPerRoom;
            int numBRooms = ((bCoders + codersPerRoom) - 1) / codersPerRoom;

            int[] aRoomIds = new int[numARooms];
            int[] bRoomIds = new int[numBRooms];
            int[] roomIds = new int[numARooms + numBRooms];
            int i;

            // dpecora - get the rooms ID's from a sequence
            for (i = 0; i < numARooms; i++) {
                aRoomIds[i] = IdGeneratorClient.getSeqIdAsInt(DBMS.ROOM_SEQ);
                roomIds[i] = aRoomIds[i];
            }

            for (i = 0; i < numBRooms; i++) {
                bRoomIds[i] = IdGeneratorClient.getSeqIdAsInt(DBMS.ROOM_SEQ);
                roomIds[i + numARooms] = bRoomIds[i];
            }

            for (i = 0; i < (numARooms + numBRooms); i++) {
                sqlStr.replace(0, sqlStr.length(),
                    "INSERT INTO room (room_id,round_id,name,division_id,room_type_id) VALUES (?, ?, ?, ?,?)");
                ps = conn.prepareStatement(sqlStr.toString());
                ps.setInt(1, roomIds[i]);
                ps.setInt(2, roundId);
                ps.setString(3, "Room " + (i + 1));
                ps.setInt(4, (i < numARooms) ? 1 : 2);
                ps.setInt(5, ServerContestConstants.CONTEST_ROOM_TYPE_ID);

                debug("Adding room id " + roomIds[i] + "(Room " + (i + 1) + ")");
                rows = ps.executeUpdate();

                if (rows == 0) {
                    throw new Exception("ERROR: Creating rooms for room assignments");
                }
            }

            // Insert into ROOM_STATUS using NCAA style ordering among two divisions:

            // int nextCoder = 0;
            // int room=0;
            // int max=0;
            // int ac = 0;
            int seed = 0;

            if (aCoders > 0) {
                // max = (aCoders/8) * 8;
                do {
                    if (aCoderIds.size() >= numARooms) {
                        seed++;

                        for (i = 0; i < numARooms; i++) {
                            sqlStr.replace(0, sqlStr.length(),
                                "INSERT INTO room_result (round_id,room_id,coder_id,room_seed,point_total,attended,advanced) VALUES (?,?,?,?,0,'N','N')");
                            ps = conn.prepareStatement(sqlStr.toString());
                            ps.setInt(1, roundId);
                            ps.setInt(2, aRoomIds[i]);
                            ps.setInt(3, ((Integer) aCoderIds.remove(0)).intValue());
                            ps.setInt(4, seed);
                            rows = ps.executeUpdate();
                        }
                    }

                    seed++;

                    for (i = numARooms - 1; (i >= 0) && (aCoderIds.size() > 0); i--) {
                        sqlStr.replace(0, sqlStr.length(),
                            "INSERT INTO room_result (round_id,room_id,coder_id,room_seed,point_total,attended,advanced) VALUES (?,?,?,?,0,'N','N')");
                        ps = conn.prepareStatement(sqlStr.toString());
                        ps.setInt(1, roundId);
                        ps.setInt(2, aRoomIds[i]);
                        ps.setInt(3, ((Integer) aCoderIds.remove(0)).intValue());
                        ps.setInt(4, seed);
                        rows = ps.executeUpdate();
                    }
                } while (aCoderIds.size() > 0);
            }

            // B rooms:
            seed = 0;

            if (bCoders > 0) {
                // max = (bCoders/8) * 8;
                do {
                    if (bCoderIds.size() >= numBRooms) {
                        seed++;

                        for (i = 0; i < numBRooms; i++) {
                            sqlStr.replace(0, sqlStr.length(),
                                "INSERT INTO room_result (round_id,room_id,coder_id,room_seed,point_total,attended,advanced) VALUES (?,?,?,?,0,'N','N')");
                            ps = conn.prepareStatement(sqlStr.toString());
                            ps.setInt(1, roundId);
                            ps.setInt(2, bRoomIds[i]);
                            ps.setInt(3, ((Integer) bCoderIds.remove(0)).intValue());
                            ps.setInt(4, seed);
                            rows = ps.executeUpdate();
                        }
                    }

                    seed++;

                    for (i = numBRooms - 1; (i >= 0) && (bCoderIds.size() > 0); i--) {
                        sqlStr.replace(0, sqlStr.length(),
                            "INSERT INTO room_result (round_id,room_id,coder_id,room_seed,point_total,attended,advanced) VALUES (?,?,?,?,0,'N','N')");
                        ps = conn.prepareStatement(sqlStr.toString());
                        ps.setInt(1, roundId);
                        ps.setInt(2, bRoomIds[i]);
                        ps.setInt(3, ((Integer) bCoderIds.remove(0)).intValue());
                        ps.setInt(4, seed);
                        rows = ps.executeUpdate();
                    }
                } while (bCoderIds.size() > 0);
            }

            // Lastly, update ROOM_STATUS.old_rating
            sqlStr.replace(0, sqlStr.length(), "UPDATE room_result SET old_rating = (SELECT rating FROM rating ");
            sqlStr.append("WHERE coder_id = room_result.coder_id) WHERE round_id = ?");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, roundId);
            rows = ps.executeUpdate();
            debug(rows + " rows updated in ROOM_RESULT");

            sqlStr.replace(0, sqlStr.length(), "UPDATE room_result SET new_rating = old_rating WHERE round_id = ?");
            // sqlStr.append("AND round_id = ?");
            ps = conn.prepareStatement(sqlStr.toString());
            // ps.setInt(1, contestId);
            ps.setInt(1, roundId);
            rows = ps.executeUpdate();
            debug(rows + " rows updated in ROOM_RESULT");

            // Update the round status to let people move to the room early
            sqlStr.replace(0, sqlStr.length(), "UPDATE round SET status = 'A' WHERE round_id = ?");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, roundId);
            rows = ps.executeUpdate();

            if (rows != 1) {
                s_trace.error("ERROR: Updating ROUND_SEGMENTS on NEW_LEADERBOARD response.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            s_trace.error("Exception in assigning rooms", e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ignore) {
                s_trace.error("Close E: " + ignore);
            }

            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception ignore) {
                s_trace.error("Close E: " + ignore);
            }

            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception ignore) {
                s_trace.error("Close E: " + ignore);
            }
        }
    }

    private void assignTeamRooms(int roundId, int teamsPerRoom, boolean isFinal) {
        java.sql.Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder sqlStr = new StringBuilder(256);
        int rows;

        try {
            conn = DBMS.getConnection();
            conn.setAutoCommit(false);

            TeamRoomAssigner assigner = new TeamRoomAssigner();
            Collection teams = getAllRegisteredTeamsForRound(roundId, conn);

            assigner.initialize(teamsPerRoom, false, false, Rating.ALGO);

            Collection assignedRooms = assigner.assignRooms(teams);

            if (isFinal) {
                Iterator rooms = assignedRooms.iterator();
                int seed = 1;
                AssignedTeamRoom previousRoom = null;

                while (rooms.hasNext()) {
                    int roomID = IdGeneratorClient.getSeqIdAsInt(DBMS.ROOM_SEQ);
                    AssignedTeamRoom room = (AssignedTeamRoom) rooms.next();

                    if ((previousRoom == null) || (room.getDivisionID() != previousRoom.getDivisionID())) {
                        seed = 1;
                    }

                    seed = createAssignedTeamRoom(roomID, roundId, seed, room, conn);
                    previousRoom = room;
                }

                // Update the round status to let people move to the room early
                sqlStr.replace(0, sqlStr.length(), "UPDATE round SET status = 'A' WHERE round_id = ?");
                ps = conn.prepareStatement(sqlStr.toString());
                ps.setInt(1, roundId);
                rows = ps.executeUpdate();

                if (rows != 1) {
                    s_trace.error("ERROR: Updating ROUND_SEGMENTS on NEW_LEADERBOARD response.");
                }
            }

            try {
                conn.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();

            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (Exception eee) {
                eee.printStackTrace();
            }

            s_trace.error("Exception in assigning rooms", e);
        } finally {
            close(conn, ps, rs);
        }
    }

    public ImportantMessageData[] getMessages(int user_id)
        throws DBServicesException {
        s_trace.debug("In getMessages(" + user_id + ")");

        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder sqlStr = new StringBuilder(200);
        java.sql.Connection conn = null;

        try {
            conn = DBMS.getConnection();
            sqlStr.append("select m.message_id, m.message, um.create_date ");
            sqlStr.append("from user_message um, message m ");
            sqlStr.append("where um.message_id = m.message_id ");
            sqlStr.append("and um.user_id = ? ");
            sqlStr.append("and m.status_id = 1 ");
            sqlStr.append("and um.status_id = 1 ");

            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, user_id);
            rs = ps.executeQuery();

            ArrayList results = new ArrayList();

            while (rs.next()) {
                int messageID = rs.getInt(1);
                String message = rs.getString(2);
                long time = rs.getTimestamp(3).getTime();
                results.add(new ImportantMessageData(messageID, message, time));
            }

            ImportantMessageData[] r = (ImportantMessageData[]) results.toArray(new ImportantMessageData[0]);

            return r;
        } catch (Exception e) {
            printException(e);
            throw new DBServicesException(e.toString());
        } finally {
            close(conn, ps, rs);
        }
    }

    public ArrayList getUserImportantMessages(int user_id)
        throws DBServicesException {
        s_trace.debug("In getUserImportantMessages(" + user_id + ")");

        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder sqlStr = new StringBuilder(200);
        java.sql.Connection conn = null;

        try {
            conn = DBMS.getConnection();
            sqlStr.append("select m.message_id, m.message ");
            sqlStr.append("from message m ");
            sqlStr.append("where CURRENT BETWEEN m.begin_date and m.end_date ");
            sqlStr.append("and message_id not in (select message_id from user_message where user_id = ?) ");
            sqlStr.append("and m.status_id = 1");

            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, user_id);
            rs = ps.executeQuery();

            ArrayList results = new ArrayList();

            while (rs.next()) {
                int messageID = rs.getInt(1);
                String message = rs.getString(2);
                results.add(new ImportantMessageData(messageID, message));
            }

            return results;
        } catch (Exception e) {
            printException(e);
            throw new DBServicesException(e.toString());
        } finally {
            close(conn, ps, rs);
        }
    }

    /** SYHAAS 2002-05-13 created
     * Returns the coderIDs that are allowed to speak, the users that
     * are allowed to respond to a question
     */
    public ArrayList getAllowedSpeakers(int round_id) throws DBServicesException {
        s_trace.debug("In getAllowedSpeakers(" + round_id + ")");

        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder sqlStr = new StringBuilder(200);
        java.sql.Connection conn = null;

        try {
            conn = DBMS.getConnection();
            sqlStr.append("SELECT coder_id FROM invite_list WHERE round_id = ?");

            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, round_id);
            rs = ps.executeQuery();

            ArrayList results = new ArrayList();

            while (rs.next()) {
                int userID = rs.getInt(1);
                results.add(Integer.valueOf(userID));
            }

            return results;
        } catch (Exception e) {
            printException(e);
            throw new DBServicesException(e.toString());
        } finally {
            close(conn, ps, rs);
        }
    }

    /**
     * SYHAAS 2002-05-13 created
     * returns all currently active moderated chat sessions
     * returns ContestRound Id's
     */
    public ArrayList getAllActiveModeratedChatSessions()
        throws DBServicesException {
        s_trace.debug("In getAllActiveModeratedChatSessions()");

        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder sqlStr = new StringBuilder(200);
        java.sql.Connection conn = null;
        Round contestRound = null;

        // ContestRoom contestRoom = null;
        Timestamp now = new Timestamp(System.currentTimeMillis());

        try {
            conn = DBMS.getConnection();

            sqlStr.append("SELECT rnd.round_id");
            sqlStr.append(" FROM room rm, round rnd, contest c");
            sqlStr.append(" WHERE rm.round_id = rnd.round_id AND rnd.contest_id = c.contest_id");
            sqlStr.append(" AND c.start_date < ? AND c.end_date > ? AND rm.room_type_id = ?");
            sqlStr.append(" ORDER BY c.start_date");

            ps = conn.prepareStatement(sqlStr.toString());
            ps.setTimestamp(1, now);
            ps.setTimestamp(2, now);
            ps.setInt(3, ServerContestConstants.MODERATED_CHAT_ROOM_TYPE_ID);
            rs = ps.executeQuery();

            ArrayList results = new ArrayList();

            while (rs.next()) {
                results.add(new Integer(rs.getInt(1)));
                debug("Got ContestRound: " + contestRound);
            }

            return results;
        } catch (Exception e) {
            s_trace.error("Error in getAllActiveModeratedChatSessions()", e);
            throw new DBServicesException(e.getMessage());
        } finally {
            close(conn, ps, rs);
        }
    }

    public List getRoundIDsToLoadOnStartUp(long minCodingTimeEnd)
        throws DBServicesException {
        s_trace.info("In getRoundIDsToLoadOnStartUp()");

        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;

        Timestamp now = new Timestamp(minCodingTimeEnd);

        try {
            conn = DBMS.getConnection();

            String sql = "SELECT r.round_id" + " FROM round r, round_segment rs"
                + "  WHERE r.round_id = rs.round_id AND r.round_type_id = "
                + ContestConstants.EDUCATION_ALGO_ROUND_TYPE_ID + "        AND rs.segment_id = "
                + ContestConstants.CODING_SEGMENT_ID + " AND rs.end_time > ? " + " ORDER BY rs.start_time";

            ps = conn.prepareStatement(sql);
            ps.setTimestamp(1, now);
            rs = ps.executeQuery();

            ArrayList results = new ArrayList();

            while (rs.next()) {
                Long roundId = new Long(rs.getInt(1));
                results.add(roundId);
                s_trace.info("Got round to load: id=" + roundId);
            }

            return results;
        } catch (Exception e) {
            s_trace.error("Error in getRoundIDsToLoadOnStartUp()", e);
            throw new DBServicesException(e.getMessage());
        } finally {
            DBMS.close(conn, ps, rs);
        }
    }

    public CategoryData[] getCategories() throws DBServicesException {
        s_trace.debug("In getCategories()");

        PreparedStatement ps = null;
        ResultSet rs = null;
        java.sql.Connection conn = null;

        try {
            conn = DBMS.getConnection();

            List<CategoryData> categories = new ArrayList();

            ps = conn.prepareStatement("SELECT group_id, group_name FROM practice_group");
            rs = ps.executeQuery();

            while (rs.next()) {
                int categoryID = rs.getInt("group_id");
                String categoryName = rs.getString("group_name");
                categories.add(new CategoryData(categoryID, categoryName));
            }

            return categories.toArray(new CategoryData[categories.size()]);
        } catch (Exception e) {
            s_trace.error("Error in getCategories()", e);
            throw new DBServicesException(e.getMessage());
        } finally {
            close(conn, ps, rs);
        }
    }

    /**
     * This method returns contest rounds the the user has previously visited
     */
    public int[] getVisitedPracticeRounds(int coderID)
        throws DBServicesException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        java.sql.Connection conn = null;
        StringBuilder sqlStr = new StringBuilder(200);

        try {
            conn = DBMS.getConnection();
            sqlStr.append("SELECT r.round_id FROM component_state cs, round r ");
            sqlStr.append("   WHERE cs.coder_id = ?  AND ");
            sqlStr.append("   cs.round_id = r.round_id AND r.status = 'A' AND r.round_type_id = 3 ");
            sqlStr.append(" UNION");
            sqlStr.append(" SELECT r1.round_id FROM long_component_state lcs, round r1 ");
            sqlStr.append("  WHERE lcs.coder_id = ?  AND ");
            sqlStr.append("  lcs.round_id = r1.round_id AND r1.status = 'A' AND r1.round_type_id = 14");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, coderID);
            ps.setInt(2, coderID);
            rs = ps.executeQuery();

            List list = new LinkedList();

            while (rs.next()) {
                list.add(new Integer(rs.getInt(1)));
            }

            int i = 0;
            int[] roundIDs = new int[list.size()];

            for (Iterator it = list.iterator(); it.hasNext(); i++) {
                Integer id = (Integer) it.next();
                roundIDs[i] = id.intValue();
            }

            return roundIDs;
        } catch (Exception e) {
            s_trace.error("Error in getVisitedPracticeRounds()", e);
            throw new DBServicesException(e.getMessage());
        } finally {
            DBMS.close(conn, ps, rs);
        }
    }

    /**
     * This method returns the ContestRound from the database
     * note: this does not initalize the contest rooms for the round
     */
    public Round getContestRound(int roundID) throws DBServicesException {
        java.sql.Connection conn = null;

        try {
            conn = DBMS.getConnection();

            return getContestRound(conn, roundID, true);
        } catch (Exception e) {
            printException(e);
            throw new DBServicesException("" + e);
        } finally {
            close(conn, null, null);
        }
    }

    /**
     * This method returns the ContestRound from the database
     * note: this does not initalize the contest rooms for the round
     */
    private Round getContestRound(Connection conn, int roundID, boolean loadAssignedRooms)
        throws DBServicesException, SQLException {
        if (s_trace.isDebugEnabled()) {
            s_trace.debug("Getting contest round " + roundID);
        }

        BaseRound contest = null;

        if (roundID == CoreServices.LOBBY_ROUND_ID) {
            contest = (BaseRound) RoundFactory.newRound(roundID, roundID, ContestConstants.LOBBY_ROUND_TYPE_ID,
                    "Lobby", "");
        } else if (ContestConstants.isPracticeRoundType(getRoundTypeId(conn, roundID))) {
            contest = (BaseRound) getPracticeRound(conn, roundID);
        } else {
            contest = (BaseRound) getCompetitionRound(conn, roundID);
        }

        loadRoundProperties(conn, contest);
        loadRoomMenu(conn, roundID, contest);
        loadRoundComponents(conn, roundID, contest);
        loadRoundEventData(conn, roundID, contest);

        // We don't really need assigned map for practice round
        if (loadAssignedRooms && !contest.getRoundType().isPracticeRound() && !contest.isLongContestRound()) {
            ((ContestRound) contest).setAssignedRoomMap(getAssignedRoomMap(conn, contest));
        }

        return contest;
    }

    /**
     * <p>
     * get the round event data.
     * </p>
     * @param conn
     *        the db connection.
     * @param roundID
     *        the round id.
     * @return the round event data.
     * @throws DBServicesException
     *          if any error occurs during ejb call.
     * @throws SQLException
     *          if any db related error occurs.
     */
    private void loadRoundEventData(Connection conn, int roundID, Round contest)
        throws DBServicesException, SQLException {
        ResultSet result = null;
        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement(GET_ROUND_EVENT_QUERY);
            stmt.setInt(1, roundID);
            result = stmt.executeQuery();

            RoundEvent re = new RoundEvent(roundID);

            if (result.next()) {
                re.setEventId(result.getInt(1));
                re.setEventName(result.getString(2));
                re.setRegistrationUrl(result.getString(3));
            }

            contest.setRoundEvent(re);
        } catch (SQLException e) {
            printException(e);
            throw e;
        } finally {
            close(null, stmt, result);
        }
    }

    /**
     * <p>
     * get the event registration data.
     * </p>
     * @param userId
     *         the user id.
     * @param eventId
     *         the event id.
     * @return the event registration data.
     * @throws RemoteException
     *          if any error occurs during ejb call.
     * @throws DBServicesException
     *          if any db related error occurs.
     */
    public EventRegistration getEventRegistrationData(int userId, int eventId)
        throws RemoteException, DBServicesException {
        java.sql.Connection conn = null;

        try {
            conn = DBMS.getConnection();

            return getEventRegistrationData(conn, userId, eventId);
        } catch (Exception e) {
            printException(e);
            throw new DBServicesException("" + e);
        } finally {
            close(conn, null, null);
        }
    }

    /**
     * <p>
     * get the event registration data.
     * </p>
     * @param conn
     *         the db connection.
     * @param userId
     *         the user id.
     * @param eventId
     *         the event id.
     * @return the event registration data.
     * @throws DBServicesException
     *          if any error occurs during ejb call.
     * @throws SQLException
     *          if any db related error occurs.
     */
    public EventRegistration getEventRegistrationData(Connection conn, int userId, int eventId)
        throws DBServicesException, SQLException {
        ResultSet result = null;
        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement(GET_EVENT_REGISTRATION_QUERY);
            stmt.setInt(1, userId);
            stmt.setInt(2, eventId);
            result = stmt.executeQuery();

            if (result.next()) {
                EventRegistration re = new EventRegistration(userId, eventId);
                re.setEligibleInd(result.getInt(3));

                return re;
            }

            return null;
        } catch (SQLException e) {
            printException(e);
            throw e;
        } finally {
            close(null, stmt, result);
        }
    }

    private void loadRoundProperties(Connection conn, BaseRound contest)
        throws SQLException, DBServicesException {
        if (s_trace.isDebugEnabled()) {
            s_trace.debug("getContestRound getting round properties on contest " + contest.getRoundID());
        }

        RoundCustomPropertiesImpl props = roundDao.getRoundDynamicProperties(contest.getRoundID(), conn);
        Language[] allowedLanguages = getCustomAllowedLanguagesForRound(contest.getRoundID(), conn);

        if (allowedLanguages != null) {
            props = ensureProps(props);
            props.setAllowedLanguages(allowedLanguages);
        }

        if (props != null) {
            contest.setCustomProperties(props);
        }
    }

    private RoundCustomPropertiesImpl ensureProps(RoundCustomPropertiesImpl props) {
        if (props == null) {
            props = new RoundCustomPropertiesImpl();
        }

        return props;
    }

    private void loadRoundComponents(Connection conn, int roundID, Round contest)
        throws SQLException {
        ArrayList d1Points = new ArrayList();
        ArrayList d2Points = new ArrayList();
        ArrayList d1Components = new ArrayList();
        ArrayList d2Components = new ArrayList();

        List<RoundComponent> rcomponents = getRoundComponents(conn, roundID);

        for (RoundComponent rc : rcomponents) {
        	if (rc.divisionId == ContestConstants.DIVISION_ONE) {
        		d1Points.add(rc.points);
        		d1Components.add(rc.componentId);
        	} else if (rc.divisionId == ContestConstants.DIVISION_TWO) {
        		d2Points.add(rc.points);
        		d2Components.add(rc.componentId);
        	}
        }

        ArrayList adminPoints = new ArrayList();
        ArrayList adminComponents = new ArrayList();
        adminPoints.addAll(d1Points);
        adminPoints.addAll(d2Points);
        adminComponents.addAll(d1Components);
        adminComponents.addAll(d2Components);

        contest.setDivisionComponents(ContestConstants.DIVISION_ONE, d1Components, d1Points);
        contest.setDivisionComponents(ContestConstants.DIVISION_TWO, d2Components, d2Points);
        contest.setDivisionComponents(ContestConstants.DIVISION_ADMIN, adminComponents, adminPoints);

    }

    private void loadRoomMenu(Connection conn, int roundID, Round contest)
        throws SQLException {
        if (s_trace.isDebugEnabled()) {
            s_trace.debug("getContestRound getting rooms on contest " + contest);
        }

        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder sqlStr = new StringBuilder();
        sqlStr.replace(0, sqlStr.length(), "SELECT room_id, name FROM room ");
        sqlStr.append("WHERE round_id = ? ORDER BY room_id");

        try {
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, roundID);
            rs = ps.executeQuery();

            while (rs.next()) {
                int roomId = rs.getInt(1);
                contest.addRoomID(roomId);
            }
        } finally {
            close(null, ps, rs);
        }
    }

    /**
     * This method has been modified to allow for the room assignment data that
     * is now part of the ContestRound object. The method ensures backwards
     * compatability for rounds that may have been setup prior to AdminTool 2.0
     * If no room assignment data is found, the default data is created.
     *
     * @see RoundRoomAssignment
     * @see ContestRound#setRoomAssignment()
     */
    private Round getCompetitionRound(Connection conn, int roundID)
        throws SQLException, DBServicesException {
        PreparedStatement ps = null;
        ResultSet rs = null;

        // First get the starting info of the next contest's coding phase
        StringBuilder sqlStr = new StringBuilder(GET_COMPETITION_ROUND_QUERY);

        try {
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, roundID);
            rs = ps.executeQuery();

            if (rs.next()) {
                debug("Got a round!");

                int idx = 1;
                long contestID = rs.getLong(idx++);
                String contestName = rs.getString(idx++);
                String roundDesc = rs.getString(idx++);
                // String status = rs.getString(idx);
                idx++;

                boolean activate = (rs.getInt(idx++) != 0);
                int roundTypeID = rs.getInt(idx++);
                int roundId = rs.getInt(idx++);
                int coders = rs.getInt(idx++);
                int type = rs.getInt(idx++);
                boolean isDivision = rs.getBoolean(idx++);
                boolean isFinal = rs.getBoolean(idx++);
                boolean isRegion = rs.getBoolean(idx++);
                double p = rs.getDouble(idx++);
                int region = Round.NO_REGION;

                if (rs.getString(idx) != null) {
                    region = rs.getInt(idx);
                }

                idx++;

                int season = ContestRound.NO_SEASON;

                if (rs.getString(idx) != null) {
                    season = rs.getInt(idx);
                }

                // this takes care of rounds that don't have assignment data
                // type '0' is invalid so if we find thisvalue, we know that this
                // round must be a round with no room assignment data
                // a new default set of values is created instead
                RoundRoomAssignment rra = null;

                if (type == 0) {
                    rra = new RoundRoomAssignment(roundId);
                } else {
                    rra = new RoundRoomAssignment(roundId, coders, type, isDivision, isFinal, isRegion, p);
                }

                Round contest = RoundFactory.newRound((int) contestID, roundID, roundTypeID, contestName, roundDesc);
                contest.setActiveMenu(activate);
                contest.setRoomAssignment(rra);
                contest.setRegion(region);
                contest.setSeason(season);
                loadRoundSegments(conn, roundID, contest);

                return contest;
            } else {
                throw new DBServicesException("Couldn't find round: " + roundID);
            }
        } finally {
            close(null, ps, rs);
        }
    }

    private void loadRoundSegments(Connection conn, int roundID, Round contest)
        throws SQLException, DBServicesException {
        // load the round segment times
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement(
                    "SELECT segment_id, start_time, end_time FROM round_segment WHERE round_id = ? order by segment_id");
            ps.setInt(1, roundID);
            rs = ps.executeQuery();

            while (rs.next()) {
                int segId = rs.getInt(1);
                Timestamp start = rs.getTimestamp(2);
                Timestamp end = rs.getTimestamp(3);

                switch (segId) {
                case ServerContestConstants.REGISTRATION_SEGMENT_ID:
                    contest.setRegistrationStart(start);
                    contest.setRegistrationEnd(end);

                    if ((System.currentTimeMillis() >= start.getTime())
                            && (System.currentTimeMillis() <= end.getTime())) {
                        contest.beginRegistrationPhase();
                    }

                    break;

                case ServerContestConstants.ROOM_ASSIGNMENT_SEGMENT_ID:
                    contest.setRoomAssignmentStart(start);
                    contest.setRoomAssignmentEnd(end);

                    if ((System.currentTimeMillis() >= start.getTime())
                            && (System.currentTimeMillis() <= end.getTime())) {
                        contest.endRegistrationPhase();
                    }

                    break;

                case ServerContestConstants.CODING_SEGMENT_ID:
                    contest.setCodingStart(start);
                    contest.setCodingEnd(end);

                    if ((System.currentTimeMillis() >= start.getTime())
                            && (System.currentTimeMillis() <= end.getTime())) {
                        contest.beginCodingPhase();
                    }

                    break;

                case ServerContestConstants.INTERMISSION_SEGMENT_ID:
                    contest.setIntermissionStart(start);
                    contest.setIntermissionEnd(end);

                    if ((System.currentTimeMillis() >= start.getTime())
                            && (System.currentTimeMillis() <= end.getTime())) {
                        contest.endCodingPhase();
                    }

                    break;

                case ServerContestConstants.CHALLENGE_SEGMENT_ID:
                    contest.setChallengeStart(start);
                    contest.setChallengeEnd(end);

                    if ((System.currentTimeMillis() >= start.getTime())
                            && (System.currentTimeMillis() <= end.getTime())) {
                        contest.beginChallengePhase();
                    }

                    break;

                case ServerContestConstants.SYSTEM_TEST_SEGMENT_ID:
                    contest.setSystemTestStart(start);
                    contest.setSystemTestEnd(end);

                    if ((System.currentTimeMillis() >= start.getTime())
                            && (System.currentTimeMillis() <= end.getTime())) {
                        contest.endChallengePhase();
                    }

                    break;

                case ServerContestConstants.MODERATED_CHAT_SEGMENT_ID: /*added by SYHAAS 2002-05-18*/
                    contest.setModeratedChatStart(start);
                    contest.setModeratedChatEnd(end);

                    break;

                default:
                    throw new DBServicesException("Unknown segment ID: " + segId);
                }
            }
        } finally {
            close(null, ps, rs);
        }
    }

    private Round getPracticeRound(Connection conn, int roundID)
        throws SQLException, DBServicesException {
        Round contest = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT c.name, c.status, r.division_id, ro.round_type_id, c.contest_id, r.name, rgx.group_id FROM contest c, round ro, room r, OUTER(round_group_xref rgx) "
                + "WHERE ro.round_id = ? AND ro.round_id=r.round_id AND ro.round_id=rgx.round_id AND ro.contest_id = c.contest_id ";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, roundID); // contestID=roundID for practice always it seems
            rs = ps.executeQuery();

            if (rs.next()) {
                String contestName = rs.getString(1);

                // String status = rs.getString(2);
                int division = rs.getInt(3);
                int roundType = rs.getInt(4);
                int contestID = rs.getInt(5);
                String roundName = rs.getString(6);
                int groupId = rs.getInt(7);

                if (!ContestConstants.isLongRoundType(Integer.valueOf(roundType)) || (roundName == null)) {
                    roundName = "";
                }

                contest = RoundFactory.newRound(contestID, roundID, roundType, contestName, roundName);
                // TODO is this the right place to update the PracticeRound phase?
                contest.beginCodingPhase();
                contest.setPracticeDivisionID(division);

                contest.setCategory(groupId > 0 ? groupId : -1);
            } else {
                throw new DBServicesException("Couldn't find practice round " + roundID);
            }

        } finally {
            close(null, ps, rs);
        }

        return contest;
    }

    private Language[] getCustomAllowedLanguagesForRound(int roundID, Connection cnn)
        throws DBServicesException {
        try {
            List languageIdsOfRound = roundDao.getLanguageIdsOfRound(roundID, cnn);

            if ((languageIdsOfRound == null) || (languageIdsOfRound.size() == 0)) {
                return null;
            }

            Language[] langs = new Language[languageIdsOfRound.size()];
            int i = 0;

            for (Iterator iter = languageIdsOfRound.iterator(); iter.hasNext(); i++) {
                Integer id = (Integer) iter.next();
                langs[i] = BaseLanguage.getLanguage(id.intValue());
            }

            return langs;
        } catch (Exception e) {
            s_trace.error("Could not obtain allowed languages for round: " + roundID, e);
            throw new DBServicesException("Could not obtain languages for rounds");
        }
    }

    public Language[] getAllowedLanguagesForRound(int roundID)
        throws DBServicesException {
        try {
            Connection conn = DBUtils.initDBBlock();
            Language[] languages = getCustomAllowedLanguagesForRound(roundID, conn);

            if (languages == null) {
                RoundType roundType = RoundType.get(roundDao.getRoundTypeId(roundID, conn));

                return roundType.getDefaultRoundProperties().getAllowedLanguages();
            }

            return languages;
        } catch (SQLException e) {
            s_trace.error("Could not obtain allowed languages for round: " + roundID, e);
            throw new DBServicesException("Could not obtain languages for rounds");
        } finally {
            DBUtils.endDBBlock();
        }
    }

    private void cleanPracticeRoom(int roundID)
        throws DBServicesException {
        java.sql.Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBMS.getConnection();

            s_trace.info("Clean practice room (" + roundID + ")");

            String strSQL = "delete from submission_class_file where component_state_id in (select component_state_id from component_state where round_id = ?);\n";

            ps = conn.prepareStatement(strSQL);
            ps.setInt(1, roundID);

            ps.executeUpdate();
            ps.close();

            strSQL = "delete from submission where component_state_id in (select component_state_id from component_state where round_id = ?); \n";

            ps = conn.prepareStatement(strSQL);
            ps.setInt(1, roundID);

            ps.executeUpdate();
            ps.close();

            strSQL = "delete from compilation_class_file where component_state_id in (select component_state_id from component_state where round_id = ?);\n";

            ps = conn.prepareStatement(strSQL);
            ps.setInt(1, roundID);

            ps.executeUpdate();
            ps.close();

            strSQL = "delete from compilation where component_state_id in (select component_state_id from component_state where round_id = ?); \n";

            ps = conn.prepareStatement(strSQL);
            ps.setInt(1, roundID);

            ps.executeUpdate();
            ps.close();

            strSQL = "delete from challenge where round_id = ?;\n";

            ps = conn.prepareStatement(strSQL);
            ps.setInt(1, roundID);

            ps.executeUpdate();
            ps.close();

            strSQL = "delete from component_state where round_id = ?;\n";

            ps = conn.prepareStatement(strSQL);
            ps.setInt(1, roundID);

            ps.executeUpdate();
            ps.close();

            strSQL = "delete from room_result where round_id = ?;";

            ps = conn.prepareStatement(strSQL);
            ps.setInt(1, roundID);

            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            printException(e);
            throw new DBServicesException(e.toString());
        } finally {
            close(conn, null, null);
        }
    }

    public void deleteCoderFromPracticeRoom(int roundID, int coderID)
        throws DBServicesException {
        java.sql.Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBMS.getConnection();

            s_trace.info("DELETING (" + roundID + "," + coderID + ")");

            String strSQL = "delete from submission_class_file where component_state_id in (select component_state_id from component_state where round_id = ? and coder_id = ?);\n";

            ps = conn.prepareStatement(strSQL);
            ps.setInt(1, roundID);
            ps.setInt(2, coderID);

            ps.executeUpdate();
            ps.close();

            strSQL = "delete from submission where component_state_id in (select component_state_id from component_state where round_id = ? and coder_id = ?); \n";

            ps = conn.prepareStatement(strSQL);
            ps.setInt(1, roundID);
            ps.setInt(2, coderID);

            ps.executeUpdate();
            ps.close();

            strSQL = "delete from compilation_class_file where component_state_id in (select component_state_id from component_state where round_id = ? and coder_id = ?);\n";

            ps = conn.prepareStatement(strSQL);
            ps.setInt(1, roundID);
            ps.setInt(2, coderID);

            ps.executeUpdate();
            ps.close();

            strSQL = "delete from compilation where component_state_id in (select component_state_id from component_state where round_id = ? and coder_id = ?); \n";

            ps = conn.prepareStatement(strSQL);
            ps.setInt(1, roundID);
            ps.setInt(2, coderID);

            ps.executeUpdate();
            ps.close();

            strSQL = "delete from challenge where round_id = ? and challenger_id = ?;\n";

            ps = conn.prepareStatement(strSQL);
            ps.setInt(1, roundID);
            ps.setInt(2, coderID);

            ps.executeUpdate();
            ps.close();

            strSQL = "delete from component_state where round_id = ? and coder_id = ?;\n";

            ps = conn.prepareStatement(strSQL);
            ps.setInt(1, roundID);
            ps.setInt(2, coderID);

            ps.executeUpdate();
            ps.close();

            strSQL = "delete from room_result where round_id = ? and coder_id = ?;";

            ps = conn.prepareStatement(strSQL);
            ps.setInt(1, roundID);
            ps.setInt(2, coderID);

            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            printException(e);
            throw new DBServicesException(e.toString());
        } finally {
            close(conn, null, null);
        }
    }

    public boolean isDeleteCoderFromPracticeRoom(int roundID, int coderID, int type)
        throws DBServicesException {
        // If all coders need to be cleared, no query to database
        if (type == ContestConstants.CLEAR_PRACTICE_ALL) {
            return true;
        }

        java.sql.Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBMS.getConnection();

            ps = conn.prepareStatement(
                    "SELECT MAX(submit_time) AS last_submit_time, COUNT(*) AS open_count FROM submission WHERE component_state_id IN (SELECT component_state_id FROM component_state WHERE round_id=? AND coder_id=?)");
            ps.setInt(1, roundID);
            ps.setInt(2, coderID);
            rs = ps.executeQuery();
            rs.next();

            int count = rs.getInt(2);
            long lastSubmission = rs.getLong(1);

            switch (type) {
            case ContestConstants.CLEAR_PRACTICE_NOT_OPEN:
                return count == 0;

            case ContestConstants.CLEAR_PRACTICE_NOT_SUBMIT:
                return lastSubmission == 0;

            case ContestConstants.CLEAR_PRACTICE_NOT_SUBMIT_RECENT:

                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MONTH, -6);

                return calendar.getTimeInMillis() > lastSubmission;

            default:
                throw new DBServicesException("Unknown clear practice room type.");
            }
        } catch (Exception e) {
            printException(e);
            throw new DBServicesException(e.toString());
        } finally {
            close(conn, ps, rs);
        }
    }

    public void clearPracticeRoom(int roundID, int type)
        throws DBServicesException {
        java.sql.Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBMS.getConnection();

            if (type == ContestConstants.CLEAR_PRACTICE_ALL) {
            	cleanPracticeRoom(roundID);
            } else {
                ps = conn.prepareStatement(GET_ROOM_RESULT_QUERY);
                ps.setInt(1, roundID);
                rs = ps.executeQuery();

                while (rs.next()) {
                    int coderID = rs.getInt("coder_id");

                    if (isDeleteCoderFromPracticeRoom(roundID, coderID, type)) {
                        deleteCoderFromPracticeRoom(roundID, coderID);
                    }
                }
            }
        } catch (Exception e) {
            printException(e);
            throw new DBServicesException(e.toString());
        } finally {
            close(conn, ps, rs);
        }
    }

    public void backupPracticeRoom(int roundID) throws DBServicesException {
        java.sql.Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBMS.getConnection();

            // insert backup values first
            ps = conn.prepareStatement(BACKUP_PRACTICE_ROOM_RESULT_QUERY);
            ps.setInt(1, roundID);
            ps.executeUpdate();
            ps.close();

            ps = conn.prepareStatement(BACKUP_PRACTICE_COMPONENT_STATE_QUERY);
            ps.setInt(1, roundID);
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            printException(e);
            throw new DBServicesException(e.toString());
        } finally {
            close(conn, null, null);
        }
    }

    /**
     * This method returns a given contest round from the database
     */
    public Room getRoom(int roomID) throws DBServicesException {
        java.sql.Connection conn = null;

        try {
            conn = DBMS.getConnection();

            return getRoom(conn, roomID);
        } catch (Exception e) {
            printException(e);
            throw new DBServicesException(e.toString());
        } finally {
            close(conn, null, null);
        }
    }

    private Room getRoom(Connection conn, int roomId) throws DBServicesException, SQLException {
        s_trace.info("Loading room=" + roomId);

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement(GET_CONTEST_ROOM_QUERY);
            ps.setInt(1, roomId);
            rs = ps.executeQuery();

            String name = null;
            int roundId = -1;
            int type = -1;
            int divId = -1;
            int capacity = -1;
            Room room = null;
            BaseCodingRoom contestRoom = null;

            if (rs.next()) {
                int idx = 1;
                name = rs.getString(idx++);
                roundId = rs.getInt(idx++);
                type = rs.getInt(idx++);
                divId = rs.getInt(idx++);

                boolean eligible = (rs.getInt(idx++) != 0);
                boolean unrated = (rs.getInt(idx++) != 0);
                boolean isAdminRoom = false;
                boolean isTeamRoom = false;
                capacity = rs.getInt(idx++);

                DBMS.close(ps, rs);

                int roomType = ContestConstants.INVALID_ROOM;

                switch (type) {
                case ContestConstants.ADMIN_ROOM_TYPE_ID:
                    s_trace.debug("Loading admin roomID = " + roomId + " for contest: " + roundId);
                    isAdminRoom = true;
                    divId = ContestConstants.DIVISION_ADMIN;
                    roomType = ContestConstants.ADMIN_ROOM;

                    break;

                case ContestConstants.TEAM_ADMIN_ROOM_TYPE_ID:
                    s_trace.debug("Loading admin roomID = " + roomId + " for contest: " + roundId);
                    isTeamRoom = true;
                    isAdminRoom = true;
                    divId = ContestConstants.DIVISION_ADMIN;
                    roomType = ContestConstants.TEAM_ADMIN_ROOM;

                    break;

                case ContestConstants.PRACTICE_ROOM_TYPE_ID:
                    roomType = ContestConstants.PRACTICE_CODER_ROOM;

                    break;

                case ContestConstants.CONTEST_ROOM_TYPE_ID:
                    roomType = ContestConstants.CODER_ROOM;

                    break;

                case ContestConstants.MODERATED_CHAT_ROOM_TYPE_ID:
                    roomType = ContestConstants.MODERATED_CHAT_ROOM;

                    break;

                case ContestConstants.TEAM_CONTEST_ROOM_TYPE_ID:
                    isTeamRoom = true;
                    roomType = ContestConstants.TEAM_CODER_ROOM;

                    break;

                case ContestConstants.TEAM_PRACTICE_ROOM_TYPE_ID:
                    isTeamRoom = true;
                    roomType = ContestConstants.TEAM_PRACTICE_CODER_ROOM;

                    break;

                case ContestConstants.LOBBY_ROOM_TYPE_ID:
                    roomType = ContestConstants.LOBBY_ROOM;

                    break;

                default:
                    throw new IllegalArgumentException("unrecognized db room type=" + type);
                }

                if (roomType == ContestConstants.LOBBY_ROOM) {
                    room = new Room(name, roomId, roomType, Rating.ALGO);
                    room.setCapacity(capacity);
                    room.setAdminRoom(isAdminRoom);
                } else {
                    Round contest = getContestRound(conn, roundId, false);
                    int ratingType = contest.getRoundType().getRatingType();

                    if (s_trace.isDebugEnabled()) {
                        s_trace.debug("Using RatingId=" + ratingType);
                    }

                    if ((roomType == ContestConstants.TEAM_CODER_ROOM)
                            || (roomType == ContestConstants.TEAM_PRACTICE_CODER_ROOM)
                            || (roomType == ContestConstants.TEAM_ADMIN_ROOM)) {
                        room = contestRoom = new TeamContestRoom(roomId, name, contest, divId, roomType, ratingType);
                    } else if (contest.isLongContestRound()) {
                        room = contestRoom = new LongContestRoom(roomId, name, contest, divId, roomType, ratingType);
                    } else {
                        room = contestRoom = new ContestRoom(roomId, name, contest, divId, roomType, ratingType);
                    }

                    contestRoom.setEligible(eligible);
                    contestRoom.setUnrated(unrated);
                    room.setCapacity(capacity);
                    room.setAdminRoom(isAdminRoom);

                    // Don't load coders for practice round since there could be many coders
                    // For practice round, the coder will be lazily loaded after user enter room
                    if (!contest.getRoundType().isPracticeRound()) {
                    	List<Coder> coders;
                        if (isTeamRoom) {
                        	coders = populateTeamContestRoom(conn, roomId, contest, null);
                        } else {
                            coders = populateContestRoom(conn, roomId, contest, isAdminRoom, null);
                        }

                        for (Iterator iter = coders.iterator(); iter.hasNext();) {
                        	contestRoom.addCoder((Coder) iter.next());
                        }

                        contestRoom.updateLeader();
                    }
                }

                s_trace.info("Loaded room for ID = " + roomId);

                return room;
            } else {
                throw new IllegalArgumentException("No room for ID = " + roomId);
            }
        } finally {
            close(null, ps, rs);
        }
    }

    private List<Coder> populateContestRoom(Connection conn, int roomId, Round contest, boolean adminRoom, Integer coderToLoad)
        throws SQLException, DBServicesException {
        s_trace.info("Populating coders for room " + roomId + " coder: " + coderToLoad);

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql;

            if (contest.isLongContestRound()) {
                if (s_trace.isDebugEnabled()) {
                    s_trace.debug("Loading long contest room =" + roomId + " admin=" + adminRoom);
                }

                if (adminRoom) {
                    sql = GET_ADMIN_ROOM_CODERS_LONG_QUERY;
                } else {
                    sql = GET_ROOM_CODERS_LONG_QUERY;
                }
            } else {
                sql = GET_ROOM_CODERS_QUERY;
            }

            if (coderToLoad != null) {
                sql = sql + " AND rs.coder_id=" + coderToLoad;	
            }

            ps = conn.prepareStatement(sql);
            ps.setInt(1, roomId);

            int ratingType = contest.getRoundType().getRatingType();
            ps.setInt(2, ratingType);

            rs = ps.executeQuery();

            List<Coder> coders = new ArrayList();

            while (rs.next()) {
                int idx = 1;
                int coderId = rs.getInt(idx++);
                String handle = rs.getString(idx++);
                int rating = rs.getInt(idx++);
                int language = rs.getInt(idx++);
                int div = rs.getInt(idx++); // should equal divId from room
                String attended = rs.getString(idx++);
                int oldRating = rs.getInt(idx++);
                Coder coder = CoderFactory.createCoder(coderId, handle, div, contest, roomId, rating, language);
                coder.setOldRating(oldRating);

                if ((attended != null) && attended.equals("Y")) {
                    coder.setAttended(true);
                }

                coder.setEligible(rs.getInt(idx++) == 1);
                coders.add(coder);

                if (contest.isLongContestRound()) {
                    ((LongContestCoder) coder).setFinalPoints((int) Math.round(rs.getDouble(idx++) * 100));
                    loadLongCoderComponents(conn, contest.getRoundID(), coder);
                }
            }

            // Load in a batch instead of individual ones
            if (!contest.isLongContestRound()) {
                // load the coders info from the DB
            	boolean loadPassedTests = contest.getRoundProperties().allowsScoreType(ResultDisplayType.PASSED_TESTS);
            	if (coders.size() > 1) {
                    loadCoderInfoBatch(conn, contest, coders, roomId, loadPassedTests);	
            	} else if (coders.size() == 1) {
                    loadCoderInfo(conn, contest, coders.get(0), loadPassedTests);            		
            	}
            }

            s_trace.info("Populated coders for room " + roomId + " coder: " + coderToLoad);
            return coders;
        } finally {
            close(null, ps, rs);
        }
    }

    public Coder getRoomCoder(Round contest, int roomId, int coderId, int teamId) throws DBServicesException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBMS.getConnection();

            ps = conn.prepareStatement("select room.room_type_id from room where room.room_id=?");
            ps.setInt(1, roomId);
            rs = ps.executeQuery();

            List<Coder> coders = null;
            if (rs.next()) {
            	int roomTypeId = rs.getInt(1);
            	boolean isTeamRoom = roomTypeId == ContestConstants.TEAM_ADMIN_ROOM_TYPE_ID
            			|| roomTypeId == ContestConstants.TEAM_CONTEST_ROOM_TYPE_ID
            			|| roomTypeId == ContestConstants.TEAM_PRACTICE_ROOM_TYPE_ID;
            	boolean isAdminRoom = roomTypeId == ContestConstants.ADMIN_ROOM_TYPE_ID
            			|| roomTypeId == ContestConstants.TEAM_ADMIN_ROOM_TYPE_ID;

                if (isTeamRoom) {
                	coders = populateTeamContestRoom(conn, roomId, contest, teamId);
                } else {
                	coders = populateContestRoom(conn, roomId, contest, isAdminRoom, coderId);
                }
            }

            return coders == null || coders.isEmpty() ? null : coders.get(0);
        } catch (Exception e) {
            printException(e);
            throw new DBServicesException(e.toString());
        } finally {
            close(conn, ps, rs);
        }
    }

    /**
     * Gets the user image info.
     * @param coderId the coder id.
     * @return the user image info entity.
     */
    public String getMemberPhotoPath(int coderId) throws DBServicesException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            conn = DBMS.getConnection();
            ps = conn.prepareStatement(QUERY_MEMBER_PHOTO_PATH);
            ps.setInt(1, coderId);
            rs = ps.executeQuery();
            if (rs.next()) {
            	String link = rs.getString("link");
            	String path = rs.getString("image_path");
            	String file = rs.getString("file_name");
            	if (StringUtils.isNotBlank(link)) {
            		return StringUtils.trimToEmpty(link);
            	}
                return StringUtils.trimToEmpty(path) + StringUtils.trimToEmpty(file);
            }
        } catch (Exception e) {
            DBMS.printException(e);
            throw new DBServicesException(e.toString());
        } finally {
            close(conn, ps, rs);
        }
        return "";
    }
    
    private List<Coder> populateTeamContestRoom(Connection conn, int roomId, Round contestRound, Integer teamToLoad)
        throws SQLException, DBServicesException {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
        	String sql = GET_ROOM_TEAMS_QUERY;
        	if (teamToLoad != null) {
        		sql = sql.replace("ORDER BY t.team_id", " AND t.team_id=" + teamToLoad);
        	}
            ps = conn.prepareStatement(sql);
            ps.setInt(1, roomId);
            rs = ps.executeQuery();

            int lastTeamID = -1;
            TeamCoder teamCoder = null;
            List<Coder> teamCoders = new ArrayList();

            while (rs.next()) {
                int idx = 1;
                int teamID = rs.getInt(idx++);
                int divId = rs.getInt(idx++);

                if (teamID != lastTeamID) {
                    Team team = getTeam(conn, teamID, contestRound.getRoundID());
                    teamCoder = new TeamCoder(team, contestRound, divId, roomId, team.getRating(), team.getLanguage());
                    teamCoder.setComponentAssignmentData(getComponentAssignmentData(conn, teamID,
                            contestRound.getRoundID()));
                    teamCoders.add(teamCoder);
                    lastTeamID = teamID;
                }

                int coderId = rs.getInt(idx++);
                String handle = rs.getString(idx++);
                int rating = rs.getInt(idx++);
                int language = rs.getInt(idx++);
                Coder coder = CoderFactory.createCoder(coderId, handle, divId, contestRound, roomId, rating, language);
                teamCoder.addMemberCoder(coder);
            }

            for (Iterator it = teamCoders.iterator(); it.hasNext();) {
                teamCoder = (TeamCoder) it.next();
                // must do this after all members are loaded
                loadTeamInfo(contestRound, teamCoder, conn);
            }
            return teamCoders;
        } finally {
            close(null, ps, rs);
        }
    }

    private String getTeamName(Connection conn, int teamID)
        throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement(GET_TEAM_NAME_QUERY);
            ps.setInt(1, teamID);
            rs = ps.executeQuery();
            rs.next();

            return rs.getString(1);
        } finally {
            close(null, ps, rs);
        }
    }

    public Team getTeam(int teamID) throws DBServicesException {
        Connection conn = null;

        try {
            conn = DBMS.getConnection();

            return getTeam(conn, teamID, -1);
        } catch (Exception e) {
            DBMS.printException(e);
            throw new DBServicesException(e.toString());
        } finally {
            close(conn, null, null);
        }
    }

    private int getRoundTypeId(Connection conn, long rid)
        throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql;
        sql = "SELECT round_type_id FROM round WHERE round_id = ?";

        try {
            ps = conn.prepareStatement(sql);
            ps.setLong(1, rid);
            rs = ps.executeQuery();
            rs.next();

            return rs.getInt(1);
        } finally {
            close(null, ps, rs);
        }
    }

    private Team getTeam(Connection conn, int teamID, int roundID)
        throws SQLException, DBServicesException {
        PreparedStatement ps = null;
        PreparedStatement ps2 = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        Team team = null;

        try {
            String teamName = getTeamName(conn, teamID);
            ps = conn.prepareStatement(GET_TEAM_CAPTAIN_QUERY);
            ps.setInt(1, teamID);
            rs = ps.executeQuery();

            if (rs.next()) {
                int userID = rs.getInt(1);
                User captain = getUser(conn, userID);
                team = new Team(teamID, teamName, captain);
            } else {
                s_trace.error("Error: no captain found for team #" + teamID + "," + teamName);

                return null;
            }

            ps = conn.prepareStatement(GET_TEAM_TYPE_QUERY);
            ps.setInt(1, teamID);
            rs = ps.executeQuery();
            rs.next();

            int teamTypeID = rs.getInt(1);
            team.setTeamTypeID(teamTypeID);

            if ((roundID == -1) || ContestConstants.isPracticeRoundType(getRoundTypeId(conn, roundID))) { // roomID=roundID
                ps2 = conn.prepareStatement(GET_TEAM_MEMBERS_QUERY);
                ps2.setInt(1, teamID);
                rs2 = ps2.executeQuery();

                while (rs2.next()) {
                    User coder = getUser(conn, rs2.getInt(1));
                    team.addMember(coder);
                }
            } else {
                ps2 = conn.prepareStatement(GET_TEAM_MEMBERS_FOR_ROUND_QUERY);
                ps2.setInt(1, teamID);
                ps2.setInt(2, roundID);
                rs2 = ps2.executeQuery();

                while (rs2.next()) {
                    User coder = getUser(conn, rs2.getInt(1));
                    team.addMember(coder);
                }
            }

            return team;
        } finally {
            close(null, ps, rs);
        }
    }

    /**
     * This method takes care of updating all the appropriate tables in the db to add a practice coder
     */
    public void addPracticeCoder(Coder coder) throws DBServicesException {
        int roomId = coder.getRoomID();
        int roundId = coder.getRoundID();
        int coderId = coder.getID();
        boolean isTeamCoder = coder instanceof TeamCoder;
        ArrayList coderIdsToAdd = new ArrayList();

        PreparedStatement ps = null;
        StringBuilder sqlStr = new StringBuilder(200);
        java.sql.Connection conn = null;

        try {
            conn = DBMS.getConnection();

            if (isTeamCoder) {
                sqlStr.replace(0, sqlStr.length(), "SELECT coder_id FROM team_coder_xref WHERE team_id = ? ");
                ps = conn.prepareStatement(sqlStr.toString());
                ps.setInt(1, coderId);

                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    coderIdsToAdd.add(Integer.valueOf(rs.getInt(1)));
                }
            } else {
                coderIdsToAdd.add(Integer.valueOf(coderId));
            }

            sqlStr.replace(0, sqlStr.length(),
                "INSERT INTO room_result (round_id, room_id, coder_id, point_total, advanced) ");
            sqlStr.append("VALUES (?, ?, ?, 0, 'N')");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, roundId);
            ps.setInt(2, roomId);

            for (int i = 0; i < coderIdsToAdd.size(); i++) {
                ps.setInt(3, ((Integer) coderIdsToAdd.get(i)).intValue());
                ps.executeUpdate();
            }
        } catch (Exception e) {
            printException(e);
            throw new DBServicesException(e.toString());
        } finally {
            close(conn, ps, null);
        }
    }

    /**
     * Deletes a contest, debugging use only!
     */
    private void deleteContest(int contestID) throws DBServicesException {
        StringBuilder sqlStr = new StringBuilder(128);
        PreparedStatement ps = null;
        java.sql.Connection conn = null;

        try {
            conn = DBMS.getConnection();

            sqlStr.replace(0, sqlStr.length(), "delete from compilation where component_state_id IN ");
            sqlStr.append("(SELECT c.component_state_id from component_state c where c.round_id = ?)");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, contestID);
            ps.executeUpdate();

            sqlStr.replace(0, sqlStr.length(), "delete from submission where component_state_id IN ");
            sqlStr.append("(SELECT c.component_state_id from component_state c where c.round_id = ?)");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, contestID);
            ps.executeUpdate();
            sqlStr.replace(0, sqlStr.length(), "delete from component_state where round_id = ?");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, contestID);
            ps.executeUpdate();

            sqlStr.replace(0, sqlStr.length(), "delete from round_component where round_id = ?");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, contestID);
            ps.executeUpdate();

            sqlStr.replace(0, sqlStr.length(), "delete from room_result where round_id = ?");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, contestID);
            ps.executeUpdate();

            sqlStr.replace(0, sqlStr.length(), "delete from round_segment where round_id = ?");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, contestID);
            ps.executeUpdate();

            sqlStr.replace(0, sqlStr.length(), "delete from room where round_id = ?");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, contestID);
            ps.executeUpdate();

            sqlStr.replace(0, sqlStr.length(), "delete from event_registration where event_id = ?");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, contestID);
            ps.executeUpdate();

            sqlStr.replace(0, sqlStr.length(), "delete from event where event_id = ?");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, contestID);
            ps.executeUpdate();

            sqlStr.replace(0, sqlStr.length(), "delete from round where round_id = ? ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, contestID);
            ps.executeUpdate();

            sqlStr.replace(0, sqlStr.length(), "delete from contest where contest_id = ? ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, contestID);
            ps.executeUpdate();

            debug("Deleted: Contest #" + contestID);
        } catch (Exception e) {
            printException(e);
            throw new DBServicesException("deleteContest: Error " + e);
        } finally {
            close(conn, ps, null);
        }
    }

    /**
     * Creates a contest, debugging use only!
     */
    public void createContest(Round cr) throws DBServicesException {
        StringBuilder sqlStr = new StringBuilder(128);
        PreparedStatement ps = null;
        java.sql.Connection conn = null;
        int contestID = cr.getContestID();

        deleteContest(contestID);

        String contestName = cr.getContestName();

        try {
            conn = DBMS.getConnection();

            // stuff contest
            sqlStr.append("insert into contest select " + contestID + " , '" + contestName
                + "', start_date, end_date, 'A', ");
            sqlStr.append("language_id, group_id, region_code, ");
            sqlStr.append("ad_text, ad_start, ad_end, ad_task, ad_command from ");
            sqlStr.append("contest where contest_id = 4005 ");
            ps = conn.prepareStatement(sqlStr.toString());

            // ps.setInt(1, contestID);
            // ps.setString(2, contestName);
            int rc = ps.executeUpdate();

            if (rc == 0) {
                throw new EJBException("createContest was unsuccessful.");
            }

            // stuff round
            sqlStr.replace(0, sqlStr.length(), "insert into round values (?, ?, 'Services Test', 'F')");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, contestID);
            ps.setInt(2, contestID);
            rc = ps.executeUpdate();

            if (rc == 0) {
                throw new EJBException("createContest was unsuccessful.");
            }

            debug("Pre-probelms");

            // stuff problems
            sqlStr.replace(0, sqlStr.length(), "insert into round_component values (?, 1, 0)");

            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, contestID);

            rc = ps.executeUpdate();

            if (rc == 0) {
                throw new EJBException("createContest was unsuccessful.");
            }

            sqlStr.replace(0, sqlStr.length(), "insert into round_component values (?, 2, 0);");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, contestID);
            rc = ps.executeUpdate();

            if (rc == 0) {
                throw new EJBException("createContest was unsuccessful.");
            }

            sqlStr.replace(0, sqlStr.length(), "insert into round_component values (?, 3, 0);");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, contestID);
            // if (DBMS.DB != DBMS.INFORMIX) ps.setInt(2, contestID);
            rc = ps.executeUpdate();

            if (rc == 0) {
                throw new EJBException("createContest was unsuccessful.");
            }

            // admin room
            sqlStr.replace(0, sqlStr.length(),
                "INSERT INTO room (room_id,round_id,name,division_id,room_type_id) VALUES (?, ?, ?, ?,?)");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, contestID); // admin room id =contestid
            ps.setInt(2, contestID);
            ps.setString(3, "Admin Room " + contestID);
            ps.setInt(4, ContestConstants.DIVISION_ADMIN);
            ps.setInt(5, ContestConstants.ADMIN_ROOM_TYPE_ID); // TODO: or TEAM_ADMIN_ROOM_TYPE_ID??
            rc = ps.executeUpdate();

            if (rc == 0) {
                throw new EJBException("createContest was unsuccessful.");
            }

            debug("Pre-round");

            // coding times
            sqlStr.replace(0, sqlStr.length(), "insert into round_segment values (?, 1, ?, ?, 'F', ?);");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, contestID);
            ps.setTimestamp(2, cr.getCodingStart());
            ps.setTimestamp(3, cr.getCodingEnd());
            ps.setTimestamp(4, cr.getIntermissionEnd());
            rc = ps.executeUpdate();

            if (rc == 0) {
                throw new EJBException("createContest was unsuccessful.");
            }

            debug("Pre-chal");

            // challenge times
            sqlStr.replace(0, sqlStr.length(), "insert into round_segment values (?, 2, ?, ?, 'F', ?);");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, contestID);
            ps.setTimestamp(2, cr.getIntermissionEnd());
            ps.setTimestamp(3, cr.getChallengeEnd());
            ps.setTimestamp(4, cr.getChallengeEnd());
            rc = ps.executeUpdate();

            if (rc == 0) {
                throw new EJBException("createContest was unsuccessful.");
            }

            // registration/event stuff
            sqlStr.replace(0, sqlStr.length(), "insert into event values (?, 3, null, 'F', ?, ?, 512)");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, contestID);

            Timestamp regStart = new Timestamp(cr.getCodingStart().getTime() - 60000);
            Timestamp regEnd = new Timestamp(cr.getCodingStart().getTime() - 30000);
            ps.setTimestamp(2, regStart);
            ps.setTimestamp(3, regEnd);
            rc = ps.executeUpdate();

            if (rc == 0) {
                throw new EJBException("createContest was unsuccessful.");
            }

            debug("created a contest: " + contestID);
        } catch (Exception e) {
            s_trace.error("error in createContest", e);
            s_trace.error("GT DEBUG  :" + sqlStr);
            s_trace.error("GT DEBUG2  :" + contestID);
            s_trace.error("GT DEBUG3  :" + contestName);
            throw new EJBException("CreateContest: Error " + e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception ignore) {
                s_trace.error("Close E: " + ignore);
            }

            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception ignore) {
                s_trace.error("Close E: " + ignore);
            }
        }
    }

    /**
     * Does room assignments.
     * @throws DBServicesException
     */
    public void assignRooms(int contestId, int roundId, int codersPerRoom, int type, boolean byDivision,
        boolean isFinal, boolean isByRegion, double p)
        throws DBServicesException {
        s_trace.debug("Start assignRooms: contestId = " + contestId + ", roundId = " + roundId);

        s_trace.debug("Start getContestRound: roundId = " + roundId);

        Round round = getContestRound(roundId);
        s_trace.debug("End getContestRound: roundId = " + roundId);

        if (round.getNumRooms() > 1) {
            s_trace.error("ERROR: Rooms have all ready been run for round " + roundId);

            return;
        }

        if (round.isTeamRound()) {
            assignTeamRooms(roundId, codersPerRoom, isFinal);

            return;
        }

        if (round.isLongContestRound()) {
            return;
        }

        if (isByRegion && (type != ContestConstants.IRON_MAN_SEEDING)) {
            AssignRoomsByRegion.assignRoomsByRegion(roundId, isFinal, codersPerRoom);

            return;
        }

        if (type == ContestConstants.NCAA_STYLE) {
            classicAssignRooms(roundId, byDivision, codersPerRoom);

            return;
        }

        java.sql.Connection conn = null;
        java.sql.Connection dwConn = null;

        PreparedStatement ps = null;
        StringBuilder sqlStr = new StringBuilder(256);
        int rows;

        try {
            conn = DBMS.getConnection();
            dwConn = DBMS.getDWConnection();

            // Set lock mode to wait for at most 1 minute
            ps = conn.prepareStatement("SET LOCK MODE TO WAIT 60");
            ps.executeUpdate();
            ps.close();
            ps = null;
            conn.setAutoCommit(false);

            RoomAssigner assigner;
            WeakestLinkData weakestLinkData = null;

            switch (type) {
            case ContestConstants.IRON_MAN_SEEDING:
                assigner = new IronmanRoomAssigner();

                break;

            case ContestConstants.RANDOM_SEEDING:
                assigner = new RandomRoomAssigner(p);

                break;

            case ContestConstants.EMPTY_ROOM_SEEDING:
                assigner = new EmptyRoomAssigner();

                break;

            case ContestConstants.WEEKEST_LINK_SEEDING:
                weakestLinkData = loadWeakestLinkData(roundId);
                assigner = new WeakestLinkRoomAssigner(weakestLinkData);

                break;

            case ContestConstants.ULTRA_RANDOM_SEEDING:
                assigner = new UltraRandomRoomAssigner();

                break;

            case ContestConstants.TCO05_SEEDING:
                assigner = new TCO05RoomAssigner();

                break;

            case ContestConstants.DARTBOARD_SEEDING:
                assigner = new DartboardRoomAssigner();

                break;

            case ContestConstants.TCHS_SEEDING:
                assigner = new TCHSRoomAssigner();

                break;

            case ContestConstants.ULTRA_RANDOM_DIV2_SEEDING:
                assigner = new UltraRandomDiv2RoomAssigner();

                break;

            default:
                throw new RuntimeException("unknown type: " + type);
            }

            int ratingType = round.getRoundType().getRatingType();
            assigner.initialize(codersPerRoom, byDivision, isByRegion, ratingType);

            Collection users;

            if (type == ContestConstants.WEEKEST_LINK_SEEDING) {
                users = getAllWeakestLinkUsers(weakestLinkData);
                s_trace.debug("Assign rooms: Found " + weakestLinkData.getTeams().length + " teams and "
                    + users.size() + " users");
            } else {
                s_trace.debug("Start getAllRegisteredUsersForRound roundId = " + roundId);
                users = getAllRegisteredUsersForRound(roundId, conn, dwConn);
                s_trace.debug("End getAllRegisteredUsersForRound roundId = " + roundId);
            }

            s_trace.debug("Start running room assignment algorithm");

            Collection assignedRooms = assigner.assignRooms(users);
            s_trace.debug("End running room assignment algorithm");

            int[] roomIds = new int[assignedRooms.size()];

            for (int i = 0; i < roomIds.length; i++) {
                roomIds[i] = IdGeneratorClient.getSeqIdAsInt(DBMS.ROOM_SEQ);
            }

            if (isFinal) {
                s_trace.debug("Start saving assignment data in DB");

                Iterator rooms = assignedRooms.iterator();
                int seed = 1;
                AssignedRoom previousRoom = null;
                int i = 0;

                while (rooms.hasNext()) {
                    AssignedRoom room = (AssignedRoom) rooms.next();

                    if ((previousRoom == null) || (room.getDivisionID() != previousRoom.getDivisionID())) {
                        seed = 1;
                    }

                    seed = createAssignedRoom(roomIds[i], roundId, seed, room, conn, ratingType);
                    previousRoom = room;
                    i++;
                }

                // Update the round status to let people move to the room early
                sqlStr.replace(0, sqlStr.length(), "UPDATE round SET status = 'A' WHERE round_id = ?");
                ps = conn.prepareStatement(sqlStr.toString());
                ps.setInt(1, roundId);
                rows = ps.executeUpdate();

                if (rows != 1) {
                    s_trace.error("ERROR: Updating ROUND_SEGMENTS on NEW_LEADERBOARD response.");
                }

                conn.commit();
                s_trace.debug("End saving assignment data in DB");
            }
        } catch (Exception e) {
            rollback(conn);
            printException(e);
            throw new EJBException(e.toString());
        } finally {
            close(conn, ps, null);
            close(dwConn, null, null);
        }

        s_trace.debug("End assignRooms: contestId = " + contestId + ", roundId = " + roundId);
    }

    /**
     * Builds the assigned room map for the contest (coderId to roomId)
     */
    private HashMap getAssignedRoomMap(Connection conn, Round contest)
        throws SQLException {
        int roundId = contest.getRoundID();
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder sqlStr = new StringBuilder(200);

        try {
            // Do the room assignments for this round
            sqlStr.replace(0, sqlStr.length(), "SELECT rs.coder_id, rs.room_id FROM room_result rs ");
            sqlStr.append("WHERE rs.round_id = ?");

            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, roundId);
            rs = ps.executeQuery();

            HashMap roomAssignmentMap = new HashMap(300);

            while (rs.next()) {
                int coderId = rs.getInt(1);
                int assignedRoomId = rs.getInt(2);
                roomAssignmentMap.put(Integer.valueOf(coderId), Integer.valueOf(assignedRoomId));
            }

            return roomAssignmentMap;
        } finally {
            close(null, ps, rs);
        }
    }

    /**
     * Loads the registration object from the DB
     */
    public Registration getRegistration(int roundId) throws DBServicesException {
        debug("getRegistration: " + roundId);

        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;

        try {
            conn = DBMS.getConnection();

            int ratingType = RoundType.get(getRoundTypeId(conn, roundId)).getRatingType();

            // int contestID = roundId;
            Registration reg = new Registration(roundId, ratingType);
            int invitationalType = roundDao.getInvitationType(roundId, conn);

            if (invitationalType != ContestConstants.NOT_INVITATIONAL) { // invitational only
                reg.setInvitationType(invitationalType);
                debug("Invite only!!");
                DBMS.close(ps, rs);
                ps = conn.prepareStatement("SELECT coder_id, seed FROM invite_list WHERE round_id = ? ORDER BY seed");
                ps.setInt(1, roundId);
                rs = ps.executeQuery();

                while (rs.next()) {
                    reg.addInviteList(rs.getInt(1));
                }
            }

            DBMS.close(ps, rs);

            int regLimit = roundDao.getRegistrationLimit(roundId, conn);

            if (regLimit >= 0) {
                reg.setRegLimit(regLimit); // Store the reg limit regardless

                ps = conn.prepareStatement("SELECT coder_id FROM round_registration WHERE round_id = ?");
                ps.setInt(1, roundId);
                rs = ps.executeQuery();

                while (rs.next()) {
                    reg.register(getUser(conn, rs.getInt(1)));
                }

                DBMS.close(ps, rs);

                try {
                    ps = conn.prepareStatement(GET_ROUND_TERMS_QUERY);
                    ps.setInt(1, roundId);
                    rs = ps.executeQuery();

                    if (rs.next()) {
                        String terms = rs.getString(TERMS_COLUMN);
                        reg.setIAgreeString(terms);
                    } else {
                        s_trace.warn("Agreement not found for round: " + roundId);
                    }
                } catch (Exception e) {
                    s_trace.error("Exception reading agreement message", e);
                }

                List roundSurveyQuestions = getRoundSurveyQuestions(conn, roundId);
                reg.addSurveyQuestions(roundSurveyQuestions);
            } // End if registration result set

            return reg;
        } catch (Exception e) {
            DBMS.printException(e);
            throw new DBServicesException(e.getMessage());
        } finally {
            DBMS.close(conn, ps, rs);
        }
    }

    /**
     * <p>
     * Changes in version 1.3 (TopCoder Competition Engine - Eligibility Questions Validation):
     * Retrieve only ACTIVE survey questions.
     * </p>
     * @param conn
     *          connection instance
     * @param roundId
     *          the id of round
     * @return
     *          a list containing all ACTIVE survey questions.
     * @throws SQLException
     *          if any related error occurs
     */
    private List getRoundSurveyQuestions(Connection conn, int roundId)
        throws SQLException {
        ResultSet rs = null;
        ResultSet rs2 = null;
        PreparedStatement ps = null;
        PreparedStatement ps2 = null;
        List questions = new ArrayList(8);

        try {
            String sqlCmd = "SELECT q.question_id, q.question_text, q.question_style_id, q.question_type_id FROM question q, round_question rq "
                + "  WHERE rq.round_id = ? AND q.question_id = rq.question_id AND status_id = 83";
            ps = conn.prepareStatement(sqlCmd);
            ps.setInt(1, roundId);
            rs = ps.executeQuery();
            ps2 = conn.prepareStatement(
                    "SELECT answer_id, answer_text, correct, sort_order FROM answer WHERE question_id = ? ORDER BY sort_order");

            while (rs.next()) {
                ArrayList answerChoices = new ArrayList(5);
                ps2.setInt(1, rs.getInt(1));
                rs2 = ps2.executeQuery();

                while (rs2.next()) {
                    SurveyAnswer answer = new SurveyAnswer(rs2.getInt(1), rs2.getString(2), rs2.getInt(3) != 0);

                    if (s_trace.isDebugEnabled()) {
                        s_trace.debug("Created answer = " + answer.getAnswerID() + " String = " + answer.getAnswer()
                            + " Correct " + answer.isCorrectEligible());
                    }

                    answerChoices.add(answer);
                }

                questions.add(new SurveyQuestion(rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getInt(4),
                        answerChoices));
                DBMS.close(rs2);
            }
        } finally {
            DBMS.close(ps2, rs2);
            DBMS.close(ps, rs);
        }

        return questions;
    }

    private int getRoundSurveyQuestionsCount(Connection conn, int roundId)
        throws SQLException {
        ResultSet rs = null;
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement("SELECT count(*) FROM round_question rq, question q WHERE rq.round_id = ? and q.question_id = rq.question_id and q.status_id = 83");
            ps.setInt(1, roundId);
            rs = ps.executeQuery();
            rs.next();

            return rs.getInt(1);
        } finally {
            DBMS.close(ps, rs);
        }
    }

    private static class RoundComponent {
    	private int componentId;
    	private int divisionId;
    	private int points;
    }

    private List<RoundComponent> getRoundComponents(Connection conn, int roundId)
        throws SQLException {
        if (s_trace.isDebugEnabled()) {
            s_trace.debug("DBServices:getRoundComponents called with args: round-" + roundId);
        }

        List<RoundComponent> retVal = new ArrayList();
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder sqlStr = new StringBuilder();

        try {
            sqlStr.append("SELECT DISTINCT rc.component_id, rc.division_id, rc.points ");
            sqlStr.append("FROM round_component rc ");
            sqlStr.append("WHERE rc.round_id=?");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, roundId);
            rs = ps.executeQuery();

            while (rs.next()) {
                RoundComponent rc = new RoundComponent();

                rc.componentId = rs.getInt(1);
                rc.divisionId = rs.getInt(2);
                rc.points = Integer.valueOf((int) rs.getDouble(3));

                if (s_trace.isDebugEnabled()) {
                    s_trace.debug("Loaded ComponentID = " + rc.componentId + " RoundID " + roundId + " DivisionID "
                        + rc.divisionId + " Points " + rc.points);
                }

                retVal.add(rc);
            }

            return retVal;
        } finally {
            close(null, ps, rs);
        }
    }

    /**
     * This method is called when a coder selects a problem from the problem list and there
     * is not yet a record of it in the database.
     */
    public long coderOpenComponent(int coderId, int contestId, int roundId, int roomId, int componentId) {
        java.sql.Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DBMS.getConnection();
            conn.setAutoCommit(false);

            ps = conn.prepareStatement("select c.open_time from compilation c, component_state cs "
            		+ "where c.component_state_id = cs.component_state_id and cs.round_id = ? and cs.coder_id = ? and cs.component_id = ?");
            ps.setInt(1, roundId);
            ps.setInt(2, coderId);
            ps.setInt(3, componentId);
            rs = ps.executeQuery();
            if (rs.next()) {
            	long openTime = rs.getLong(1);
            	s_trace.info("Coder " + coderId + " has already opened component " + componentId + " at " + openTime);
            	return openTime;
            }

            long seqId = IdGeneratorClient.getSeqIdAsInt(DBMS.COMPONENT_STATE_SEQ);

            if (s_trace.isDebugEnabled()) {
                s_trace.debug("SeqID: " + seqId);
            }

            long currentTime = System.currentTimeMillis();

            insertComponentState(conn, seqId, roundId, coderId, componentId);
            insertCompilation(conn, seqId, currentTime);
            conn.commit();

            return currentTime;
        } catch (Exception e) {
            rollback(conn);
            printException(e);
            throw new EJBException("" + e);
        } finally {
            DBMS.close(conn, ps, rs);
        }
    }

    public long coderOpenLongComponent(int coderId, int contestId, int roundId, int componentId) {
        java.sql.Connection conn = null;
        long currentTime = System.currentTimeMillis();

        try {
            long seqId = IdGeneratorClient.getSeqIdAsInt(DBMS.COMPONENT_STATE_SEQ);
            conn = DBMS.getConnection();
            conn.setAutoCommit(false);

            if (s_trace.isDebugEnabled()) {
                s_trace.debug("SeqID: " + seqId);
            }

            insertLongComponentState(conn, seqId, roundId, coderId, componentId);
            insertLongCompilation(conn, seqId, currentTime);
            conn.commit();

            return currentTime;
        } catch (Exception e) {
            rollback(conn);
            printException(e);
            throw new EJBException("" + e);
        } finally {
            DBMS.close(conn);
        }
    }

    /**
     * This method makes the newCoderId's version of this component the same in the database as the oldCoderId's version by
     * making the component_state, submission, submission_class_file, compilation, entries
     * the same.
     */
    public void synchTeamMembersComponents(int contestId, int roundId, int roomId, int componentId, int oldCoderId,
        int newCoderId) throws DBServicesException {
        debug("synchTeamMembersComponents(" + contestId + ", " + roundId + ", " + roomId + ", " + componentId + ", "
            + oldCoderId + ", " + newCoderId + ")");

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder sqlStr = null;
        boolean rollback = false;

        try {
            conn = DBMS.getConnection();
            sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT component_state_id ");
            sqlStr.append("FROM component_state ");
            sqlStr.append("WHERE round_id = ? ");
            sqlStr.append("  AND component_id = ? ");
            sqlStr.append("  AND coder_id = ? ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, roundId);
            ps.setInt(2, componentId);

            ps.setInt(3, oldCoderId);
            rs = ps.executeQuery();

            if (!rs.next()) { //the old coder hasn't opened it yet, do nothing
                debug("old team member hasn't opened component, nothing needs to be done.");

                return;
            }

            int oldComponentStateId = rs.getInt(1);

            ps.setInt(3, newCoderId);
            rs = ps.executeQuery();

            if (!rs.next()) { // the new coder hasn't opened it yet, open it
                debug("new team member hasn't opened component, calling coderOpenComponent.");
                coderOpenComponent(newCoderId, contestId, roundId, roomId, componentId);
                rs = ps.executeQuery();
                rs.next();
            }

            int newComponentStateId = rs.getInt(1);

            conn.setAutoCommit(false);
            rollback = true;

            // update component state------------------
            sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT points, status_id, submission_number ");
            sqlStr.append("FROM component_state WHERE component_state_id = ? ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, oldComponentStateId);
            rs = ps.executeQuery();
            rs.next();

            double points = rs.getDouble("points");
            boolean pointsWasNull = rs.wasNull();
            int status_id = rs.getInt("status_id");
            boolean status_idWasNull = rs.wasNull();

            // int language_id = rs.getInt("language_id");
            // boolean language_idWasNull = rs.wasNull();
            int submission_number = rs.getInt("submission_number");
            boolean submission_numberWasNull = rs.wasNull();

            // set status of old guy to reassigned
            sqlStr = new StringBuilder(256);
            sqlStr.append("UPDATE component_state ");
            sqlStr.append("SET status_id = ? ");
            sqlStr.append("WHERE component_state_id = ? ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, ContestConstants.REASSIGNED);
            ps.setInt(2, oldComponentStateId);
            ps.executeUpdate();

            sqlStr = new StringBuilder(256);
            sqlStr.append("UPDATE component_state ");
            sqlStr.append("SET points = ? ");
            sqlStr.append("   ,status_id = ? ");
            // sqlStr.append(" ,language_id = ? ");
            sqlStr.append("   ,submission_number = ? ");
            sqlStr.append("WHERE component_state_id = ? ");
            ps = conn.prepareStatement(sqlStr.toString());

            if (pointsWasNull) {
                ps.setNull(1, Types.DOUBLE);
            } else {
                ps.setDouble(1, points);
            }

            if (status_idWasNull) {
                ps.setNull(2, Types.DECIMAL);
            } else {
                ps.setDouble(2, status_id);
            }

            // if (language_idWasNull) ps.setNull(3, Types.DECIMAL); else
            // ps.setDouble(3, language_id);
            if (submission_numberWasNull) {
                ps.setNull(4, Types.DECIMAL);
            } else {
                ps.setDouble(3, submission_number);
            }

            ps.setInt(4, newComponentStateId);
            ps.executeUpdate();

            // update compilation----------------------------
            sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT open_time, compilation_text FROM compilation WHERE component_state_id = ? ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, oldComponentStateId);
            rs = ps.executeQuery();
            rs.next();

            long open_time = rs.getLong("open_time");
            boolean open_timeWasNull = rs.wasNull();
            byte[] compilation_text = rs.getBytes("compilation_text");
            boolean compilation_textWasNull = rs.wasNull();

            sqlStr = new StringBuilder(256);
            sqlStr.append("UPDATE compilation SET open_time = ?, compilation_text = ? WHERE component_state_id = ? ");
            ps = conn.prepareStatement(sqlStr.toString());

            if (open_timeWasNull) {
                ps.setNull(1, Types.DECIMAL);
            } else {
                ps.setLong(1, open_time);
            }

            if (compilation_textWasNull) {
                ps.setNull(2, Types.BINARY);
            } else {
                ps.setBytes(2, compilation_text);
            }

            ps.setInt(3, newComponentStateId);
            ps.executeUpdate();

            //update submission stuff - delete old submission data and copy new stuff over ----------------------------
            sqlStr = new StringBuilder(256);
            sqlStr.append("DELETE FROM submission_class_file WHERE component_state_id = ? ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, newComponentStateId);
            ps.executeUpdate();

            sqlStr = new StringBuilder(256);
            sqlStr.append("DELETE FROM submission WHERE component_state_id = ? ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, newComponentStateId);
            ps.executeUpdate();

            sqlStr = new StringBuilder(256);
            sqlStr.append(
                "INSERT INTO submission (component_state_id, submission_number, submission_text, open_time, ");
            sqlStr.append("submit_time, submission_points) ");
            sqlStr.append("SELECT ");
            sqlStr.append(newComponentStateId);
            sqlStr.append(", submission_number, submission_text, open_time, submit_time, submission_points ");
            sqlStr.append("FROM submission WHERE component_state_id = ? ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, oldComponentStateId);
            ps.executeUpdate();

            sqlStr = new StringBuilder(256);
            sqlStr.append(
                "INSERT INTO submission_class_file (component_state_id, submission_number, sort_order, path, class_file) ");
            sqlStr.append("SELECT ");
            sqlStr.append(newComponentStateId);
            sqlStr.append(", submission_number, sort_order, path, class_file ");
            sqlStr.append("FROM submission_class_file WHERE component_state_id = ? ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, oldComponentStateId);
            ps.executeUpdate();

            conn.commit();
            rollback = false;
        } catch (Exception e) {
            s_trace.error("error syncing components, ", e);
            throw new DBServicesException(e.toString());
        } finally {
            if (rollback) {
                try {
                    conn.rollback();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            DBMS.close(conn, ps, rs);
        }
    }

    private void insertLongCompilation(Connection conn, long seqId, long currentTime)
        throws SQLException {
        StringBuilder sqlStr = new StringBuilder();
        PreparedStatement ps = null;
        sqlStr.replace(0, sqlStr.length(), "INSERT INTO long_compilation");
        sqlStr.append(" (long_component_state_id, open_time)");
        sqlStr.append("VALUES (?,?)");

        try {
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setLong(1, seqId);
            ps.setLong(2, currentTime);
            ps.executeUpdate();
        } finally {
            DBMS.close(ps);
        }
    }

    private void insertCompilation(Connection conn, long seqId, long currentTime)
        throws SQLException {
        StringBuilder sqlStr = new StringBuilder();
        PreparedStatement ps = null;
        sqlStr.replace(0, sqlStr.length(), "INSERT INTO compilation");
        sqlStr.append(" (component_state_id, open_time)");
        sqlStr.append("VALUES (?,?)");

        try {
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setLong(1, seqId);
            ps.setLong(2, currentTime);
            ps.executeUpdate();
        } finally {
            DBMS.close(ps);
        }
    }

    private void insertLongComponentState(Connection conn, long seqId, int roundId, int coderId, int componentId)
        throws SQLException {
        StringBuilder sqlStr = new StringBuilder();
        PreparedStatement ps = null;
        sqlStr.replace(0, sqlStr.length(),
            "INSERT INTO long_component_state (long_component_state_id, round_id, coder_id, component_id,");
        sqlStr.append("points, status_id, submission_number, example_submission_number) VALUES (?,?,?,?,?,?,?,?)");

        try {
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setLong(1, seqId);
            ps.setInt(2, roundId);
            ps.setInt(3, coderId);
            ps.setInt(4, componentId);
            ps.setInt(5, 0);
            ps.setInt(6, ContestConstants.LOOKED_AT);
            // ps.setNull(7, Types.INTEGER);
            ps.setInt(7, 0);
            ps.setInt(8, 0);
            ps.executeUpdate();
        } finally {
            DBMS.close(ps);
        }
    }

    private void insertComponentState(Connection conn, long seqId, int roundId, int coderId, int componentId)
        throws SQLException {
        StringBuilder sqlStr = new StringBuilder();
        PreparedStatement ps = null;
        sqlStr.replace(0, sqlStr.length(),
            "INSERT INTO component_state (component_state_id, round_id, coder_id, component_id,");
        sqlStr.append("points, status_id, submission_number) VALUES (?,?,?,?,?,?,?)");

        try {
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setLong(1, seqId);
            ps.setInt(2, roundId);
            ps.setInt(3, coderId);
            ps.setInt(4, componentId);
            ps.setInt(5, 0);
            ps.setInt(6, ContestConstants.LOOKED_AT);
            // ps.setNull(7, Types.INTEGER);
            ps.setInt(7, 0);
            ps.executeUpdate();
        } finally {
            DBMS.close(ps);
        }
    }

    /**
     * Returns a Map of teamname -> teamid
     */
    public HashMap getTeamNameToTeamIDMap() {
        java.sql.Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null; // , rs2 = null;
        StringBuilder sqlStr = new StringBuilder(200);

        try {
            conn = DBMS.getConnection();

            sqlStr.append("SELECT team_name, team_id FROM team");

            ps = conn.prepareStatement(sqlStr.toString());
            rs = ps.executeQuery();

            HashMap retVal = new HashMap(7500);

            while (rs.next()) {
                String userName = rs.getString(1);
                int id = rs.getInt(2);
                retVal.put(userName.toLowerCase(), Integer.valueOf(id));
            }

            return retVal;
        } catch (Exception e) {
            s_trace.error("Error in getTeamNameToTeamIDMap()", e);
            throw new EJBException(e.getMessage());
        } finally {
            close(conn, ps, rs);
        }
    }

    /**
     * Returns a Map of username -> userid
     */
    public HashMap getHandleToUserIDMap() {
        java.sql.Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null; // , rs2 = null;
        StringBuilder sqlStr = new StringBuilder(200);

        try {
            conn = DBMS.getConnection();

            sqlStr.append("SELECT u.handle, u.user_id FROM user u WHERE u.status = 'A'");

            ps = conn.prepareStatement(sqlStr.toString());
            rs = ps.executeQuery();

            HashMap retVal = new HashMap(7500);

            while (rs.next()) {
                String userName = rs.getString(1);
                int id = rs.getInt(2);
                retVal.put(userName.toLowerCase(), Integer.valueOf(id));
            }

            return retVal;
        } catch (Exception e) {
            s_trace.error("Error in getHandleToUserIDMap()", e);
            throw new EJBException(e.getMessage());
        } finally {
            close(conn, ps, rs);
        }
    }

    /**
     * This function takes care of updating the round_segments for the contest
     * @throws DBServicesException
     */
    public void processRoundEvent(Round contest) throws DBServicesException {
        debug("DBServices:processRoundEvent() called...");

        java.sql.Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;

        try {
            conn = DBMS.getConnection();
            conn.setAutoCommit(false);

            // Take care of updating the database
            int phase = contest.getPhase();

            switch (phase) {
            case ContestConstants.REGISTRATION_PHASE:
                updateSegmentStatus(conn, contest.getRoundID(), ServerContestConstants.REGISTRATION_SEGMENT_ID, "A");

                break;

            case ContestConstants.ALMOST_CONTEST_PHASE:
                updateSegmentStatus(conn, contest.getRoundID(), ServerContestConstants.REGISTRATION_SEGMENT_ID, "P");

                break;

            case ContestConstants.CODING_PHASE:
                updateSegmentStatus(conn, contest.getRoundID(), ServerContestConstants.CODING_SEGMENT_ID, "A");

                break;

            case ContestConstants.INTERMISSION_PHASE:
                updateSegmentStatus(conn, contest.getRoundID(), ServerContestConstants.CODING_SEGMENT_ID, "P");
                updateSegmentStatus(conn, contest.getRoundID(), ServerContestConstants.INTERMISSION_SEGMENT_ID, "A");

                break;

            case ContestConstants.CHALLENGE_PHASE:
                updateSegmentStatus(conn, contest.getRoundID(), ServerContestConstants.INTERMISSION_SEGMENT_ID, "P");
                updateSegmentStatus(conn, contest.getRoundID(), ServerContestConstants.CHALLENGE_SEGMENT_ID, "A");

                break;

            case ContestConstants.PENDING_SYSTESTS_PHASE:
                updateSegmentStatus(conn, contest.getRoundID(), ServerContestConstants.CHALLENGE_SEGMENT_ID, "P");
                updateSegmentStatus(conn, contest.getRoundID(), ServerContestConstants.SYSTEM_TEST_SEGMENT_ID, "A");

                break;

            case ContestConstants.STARTS_IN_PHASE:
            case ContestConstants.SYSTEM_TESTING_PHASE:

                /* Do nothing */
                break;

            case ContestConstants.CONTEST_COMPLETE_PHASE:

                /* Do nothing */
                break;

            case ContestConstants.MODERATED_CHATTING_PHASE: //added by SYHAAS 2002-05-20
                updateSegmentStatus(conn, contest.getRoundID(), ServerContestConstants.MODERATED_CHAT_SEGMENT_ID, "A");

                break;

            case ContestConstants.INACTIVE_PHASE: //added by SYHAAS 2002-05-20

                if (contest.isModeratedChat()) {
                    updateSegmentStatus(conn, contest.getRoundID(), ServerContestConstants.MODERATED_CHAT_SEGMENT_ID,
                        "P");
                }

                break;

            default:
                throw new DBServicesException("Unknown phase (" + phase + ").");
            }

            conn.commit();
        } catch (Exception e) {
            rollback(conn);
            printException(e);
            throw new DBServicesException("" + e);
        } finally {
            close(conn, ps, rs);
        }
    }

    private void updateSegmentStatus(Connection conn, int roundID, int segmentID, String status)
        throws SQLException, DBServicesException {
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement("UPDATE round_segment SET status = ? WHERE round_id = ? AND segment_id = ?");
            ps.setString(1, status);
            ps.setInt(2, roundID);
            ps.setInt(3, segmentID);

            int rows = ps.executeUpdate();

            if (rows != 1) {
                throw new DBServicesException("Unexpectd update count: " + rows);
            }
        } finally {
            DBMS.close(ps);
        }
    }

    /**
     *  This function takes care of setting all the flags in the database on endContest
     */

    // dpecora - move updates to room_placed, division_placed, and overall_rank here
    // from the allocate prizes routine, and rewrite to fix bugs.
    public void endContest(int contestId, int roundId)
        throws DBServicesException {
        s_trace.info("DBServices.endContest called - contestId = " + contestId + ", roundId = " + roundId);

        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            c = DBMS.getConnection();

            ps = c.prepareStatement("UPDATE contest SET status = 'P' WHERE contest_id = ?");
            ps.setInt(1, contestId);
            ps.executeUpdate();
            ps.close();

            debug("Updated contest status");

            ps = c.prepareStatement("UPDATE round SET status = 'P' WHERE round_id = ?");
            ps.setInt(1, roundId);
            ps.executeUpdate();
            ps.close();

            debug("Updated round status");

            ps = c.prepareStatement("UPDATE round_segment SET status = 'P' WHERE round_id = ?");
            ps.setInt(1, roundId);
            ps.executeUpdate();
            ps.close();

            debug("Updated round segments");

            s_trace.info("End Contest - Contest and round statuses updated");

            RoundType roundType = RoundType.get(getRoundTypeId(c, roundId));
            if (!roundType.isLongRound()) {
                endAlgoRound(c, roundId, roundType);
            } else {
                endLongRound(c, roundId);
            }

            s_trace.info("END CONTEST - FINISHED");
        } catch (Exception e) {
            printException(e);
            throw new DBServicesException("" + e);
        } finally {
            DBMS.close(c, ps, rs);
        }
    }

    private void endLongRound(Connection c, int roundId) {
        //Nothing to do right now
    }

    private void endAlgoRound(Connection c, int roundId, RoundType roundType) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            //      make system test viewable
            StringBuilder sqlStr = new StringBuilder();
            sqlStr.append("UPDATE system_test_result SET viewable = 'Y' ");
            sqlStr.append("WHERE round_id = ?");
            ps = c.prepareStatement(sqlStr.toString());
            ps.setInt(1, roundId);
            ps.executeUpdate();
            ps.close();
            ps = null;

            debug("Set systests viewable");

            updateAttendedCoders(c, roundId);
            updateAlgoPlaceImpl(c, roundId, roundType);
        } finally {
            DBMS.close(ps, rs);
        }
    }

    public void updateAlgoPlace(int roundId) throws DBServicesException {
        if (s_trace.isDebugEnabled()) {
            debug("DBServices.updateAlgoPlace called - roundId = " + roundId);
        }

        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            c = DBMS.getConnection();
            c.setAutoCommit(false);

            RoundType roundType = RoundType.get(getRoundTypeId(c, roundId));
            updateAlgoPlaceImpl(c, roundId, roundType);

            c.commit();
            debug("Coder place updated.");
        } catch (Exception e) {
            rollback(c);
            printException(e);
            throw new DBServicesException("" + e);
        } finally {
            DBMS.closeAndResetAC(c, ps, rs);
        }
    }

    private void updateAlgoPlaceImpl(Connection c, int roundId, RoundType roundType)
        throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int ratingType = roundType.getRatingType();

        try {

            // update ROOM_RESULT.room_placed
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

            // Loop over the rows updating room result
            int lastRoom = -1;

            // Loop over the rows updating room result
            int place = 1;

            // Loop over the rows updating room result
            int numInPlace = 0;

            // Loop over the rows updating room result
            int i;
            double lastPoints = 0;
            ps = c.prepareStatement("UPDATE room_result SET room_placed = ? WHERE round_id = ? AND coder_id = ?");
            ps.setInt(2, roundId);

            for (i = 0; i < contestantList.getRowCount(); i++) {
                int coderId = Integer.parseInt(contestantList.getItem(i, "coder_id").toString());
                int room = Integer.parseInt(contestantList.getItem(i, "room_id").toString());
                double points = Double.parseDouble(contestantList.getItem(i, "point_total").toString());

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

                ps.setInt(1, place);
                ps.setInt(3, coderId);

                int rowsUpdated = ps.executeUpdate();

                if (rowsUpdated != 1) {
                    throw new Exception("WRONG NUMBER OF ROWS UPDATE IN endContest: " + rowsUpdated
                        + " where coder_id = " + coderId);
                } else {
                    debug("Set coder #" + coderId + " room placed " + place);
                }
            }

            ps.close();

            // Set division_placed - where the coder placed in their division
            // get all the users in order for this division by points.
            sqlStr = new StringBuilder();
            sqlStr.append("SELECT rr.coder_id, r.division_id, rr.point_total ");
            sqlStr.append("FROM room_result rr, room r ");
            sqlStr.append("WHERE r.room_id = rr.room_id ");
            sqlStr.append("AND rr.round_id = ? ");
            sqlStr.append("AND rr.attended = 'Y' ");
            sqlStr.append("AND r.division_id <> -1 ");
            sqlStr.append("ORDER BY r.division_id, rr.point_total DESC");
            ps = c.prepareStatement(sqlStr.toString());
            ps.setInt(1, roundId);

            ResultSetContainer rsc = runSelectQuery(ps);
            ps.close();
            ps = null;

            int previousDivision = -1;
            int divisionPlace = 0;

            ps = c.prepareStatement("UPDATE room_result SET division_placed = ? WHERE coder_id = ? AND round_id = ?");
            ps.setInt(3, roundId);

            for (i = 0; i < rsc.getRowCount(); i++) {
                int coderId = Integer.parseInt(rsc.getItem(i, "coder_id").toString());
                int divisionId = Integer.parseInt(rsc.getItem(i, "division_id").toString());
                double points = Double.parseDouble(rsc.getItem(i, "point_total").toString());

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
                ps.setInt(1, divisionPlace);
                ps.setInt(2, coderId);

                int rowsUpdated = ps.executeUpdate();

                if (rowsUpdated != 1) {
                    throw new Exception("WRONG NUMBER OF ROWS UPDATE FOR division_place: " + rowsUpdated
                        + " where coder_id = " + coderId);
                } else {
                    debug("Set coder #" + coderId + " division placed " + place);
                }
            }

            ps.close();

            // Set overall_rank
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
                int rank = Integer.parseInt(rsc.getItem(i, "rank").getResultData().toString());
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
                    throw new Exception("WRONG NUMBER OF ROWS UPDATED FOR overall_rank: " + rowsUpdated
                        + " where coder_id = " + coderId);
                } else {
                    debug("Set coder #" + coderId + " overall rank = " + rank);
                }
            }
        } finally {
            DBMS.close(ps, rs);
        }
    }

    public void endTCHSContest(int contestId, int roundId)
        throws DBServicesException {
        debug("DBServices.endTCHSContest called - contestId = " + contestId + ", roundId = " + roundId);

        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            c = DBMS.getConnection();
            c.setAutoCommit(false);

            StringBuilder sqlStr = new StringBuilder();

            // clear region placed
            sqlStr = new StringBuilder();
            sqlStr.append("UPDATE room_result set region_placed = null where round_id = ? and attended = 'Y'");
            ps = c.prepareStatement(sqlStr.toString());
            ps.setInt(1, roundId);
            ps.executeUpdate();
            ps.close();
            ps = null;

            sqlStr = new StringBuilder();
            // update ROOM_RESULT.region_placed
            sqlStr.append("SELECT rr.coder_id, rr.point_total ");
            sqlStr.append("FROM room_result rr, round r, round_registration reg, ");
            sqlStr.append("team t, school s, address a, country_region_xref crx ");
            sqlStr.append("WHERE rr.round_id = ? ");
            sqlStr.append("AND rr.attended = 'Y'  ");
            sqlStr.append("AND r.round_id = rr.round_id ");
            sqlStr.append("AND reg.coder_id = rr.coder_id ");
            sqlStr.append("AND reg.round_id = rr.round_id ");
            sqlStr.append("AND t.team_id = reg.team_id ");
            sqlStr.append("AND s.school_id = t.school_id ");
            sqlStr.append("AND a.address_id = s.address_id ");
            sqlStr.append("AND crx.country_code = a.country_code ");
            sqlStr.append("AND crx.region_id = r.region_id ");
            sqlStr.append("ORDER BY rr.point_total DESC");
            ps = c.prepareStatement(sqlStr.toString());
            ps.setInt(1, roundId);

            ResultSetContainer contestantList = runSelectQuery(ps);
            ps.close();
            ps = null;

            sqlStr = new StringBuilder();
            sqlStr.append("UPDATE room_result SET region_placed = ? WHERE round_id = ? AND coder_id = ?");

            // Loop over the rows updating room result
            int place = 1;

            // Loop over the rows updating room result
            int numInPlace = 0;

            // Loop over the rows updating room result
            int i;
            double lastPoints = 0;
            ps = c.prepareStatement(sqlStr.toString());

            for (i = 0; i < contestantList.getRowCount(); i++) {
                ps.clearParameters();

                int coderId = contestantList.getIntItem(i, "coder_id");
                double points = contestantList.getDoubleItem(i, "point_total");

                if (points == lastPoints) {
                    numInPlace++;
                } else {
                    place += numInPlace;
                    numInPlace = 1;
                }

                lastPoints = points;

                ps.setInt(1, place);
                ps.setInt(2, roundId);
                ps.setInt(3, coderId);

                int rowsUpdated = ps.executeUpdate();

                if (rowsUpdated != 1) {
                    throw new Exception("WRONG NUMBER OF ROWS UPDATE IN endContest: " + rowsUpdated
                        + " where coder_id = " + coderId);
                } else {
                    debug("Set coder #" + coderId + " room placed " + place);
                }
            }

            ps.close();
            ps = null;

            // clear team points
            sqlStr = new StringBuilder();
            sqlStr.append("UPDATE room_result set team_points = null where round_id = ? and attended = 'Y'");
            ps = c.prepareStatement(sqlStr.toString());
            ps.setInt(1, roundId);
            ps.executeUpdate();
            ps.close();
            ps = null;

            // Set team_points, get the list of all teams that competed in this region
            sqlStr = new StringBuilder();
            sqlStr.append("SELECT t.team_id, count(*) ");
            sqlStr.append("FROM room_result rr, round r, round_registration reg, ");
            sqlStr.append("team t, school s, address a, country_region_xref crx ");
            sqlStr.append("WHERE rr.round_id = ? ");
            sqlStr.append("AND rr.attended = 'Y'  ");
            sqlStr.append("AND r.round_id = rr.round_id ");
            sqlStr.append("AND reg.coder_id = rr.coder_id ");
            sqlStr.append("AND reg.round_id = rr.round_id ");
            sqlStr.append("AND t.team_id = reg.team_id ");
            sqlStr.append("AND s.school_id = t.school_id ");
            sqlStr.append("AND a.address_id = s.address_id ");
            sqlStr.append("AND crx.country_code = a.country_code ");
            sqlStr.append("AND crx.region_id = r.region_id ");
            sqlStr.append("GROUP BY t.team_id ");
            sqlStr.append("HAVING count(*) >= 3");

            ps = c.prepareStatement(sqlStr.toString());
            ps.setInt(1, roundId);

            ResultSetContainer rsc = runSelectQuery(ps);
            ps.close();
            ps = null;

            // only top 3 people contribute points
            for (i = 0; i < rsc.getRowCount(); i++) {
                // get the participants in the team
                int team = rsc.getIntItem(i, "team_id");
                sqlStr = new StringBuilder();

                sqlStr.append("SELECT rr.coder_id, rr.region_placed ");
                sqlStr.append("FROM room_result rr, round r, round_registration reg, ");
                sqlStr.append("team t, school s, address a, country_region_xref crx ");
                sqlStr.append("WHERE rr.round_id = ? ");
                sqlStr.append("AND rr.attended = 'Y'  ");
                sqlStr.append("AND r.round_id = rr.round_id ");
                sqlStr.append("AND reg.coder_id = rr.coder_id ");
                sqlStr.append("AND reg.round_id = rr.round_id ");
                sqlStr.append("AND t.team_id = reg.team_id ");
                sqlStr.append("AND s.school_id = t.school_id ");
                sqlStr.append("AND a.address_id = s.address_id ");
                sqlStr.append("AND crx.country_code = a.country_code ");
                sqlStr.append("AND crx.region_id = r.region_id ");
                sqlStr.append("AND t.team_id = ? ");
                sqlStr.append("ORDER BY rr.region_placed");

                ps = c.prepareStatement(sqlStr.toString());
                ps.setInt(1, roundId);
                ps.setInt(2, team);

                ResultSetContainer teamMembers = runSelectQuery(ps);
                ps.close();
                ps = null;

                sqlStr = new StringBuilder();
                sqlStr.append("UPDATE room_result set team_points = ? where round_id = ? and coder_id = ?");
                ps = c.prepareStatement(sqlStr.toString());

                for (int j = 0; j < 3; j++) {
                    ps.clearParameters();
                    ps.setInt(1, teamMembers.getIntItem(j, "region_placed"));
                    ps.setInt(2, roundId);
                    ps.setInt(3, teamMembers.getIntItem(j, "coder_id"));
                    ps.executeUpdate();
                }

                ps.close();
                ps = null;
            }

            // We're finally done!
            c.commit();
            debug("END HS CONTEST COMMITTED");
        } catch (Exception e) {
            rollback(c);
            printException(e);
            throw new DBServicesException("" + e);
        } finally {
            DBMS.closeAndResetAC(c, ps, rs);
        }
    }

    private static void updateAttendedCoders(Connection c, int roundId)
        throws SQLException {
        // update ROOM_STATUS.attended for those who attended
        if (DBMS.DB == DBMS.INFORMIX) {
            StringBuilder sqlStr = new StringBuilder();
            sqlStr.append("UPDATE room_result SET attended = 'Y' ");
            sqlStr.append("WHERE round_id = ? AND coder_id in (SELECT distinct(coder_id) ");
            sqlStr.append("FROM component_state WHERE round_id = ?)");

            PreparedStatement ps = null;

            try {
                ps = c.prepareStatement(sqlStr.toString());
                ps.setInt(1, roundId);
                ps.setInt(2, roundId);
                ps.executeUpdate();
            } finally {
                DBMS.close(ps);
            }
        } else {
            updateAttendedCodersMysql(c, roundId);
        }

        debug("Set attended coders");
    }

    private static void updateAttendedCodersMysql(Connection connection, int roundId)
        throws SQLException {
        String coders = getRoundCodersMysql(connection, roundId);
        String sql = "UPDATE room_result SET attended = 'Y' WHERE round_id = ? AND coder_id IN (" + coders + ")";
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, roundId);
            preparedStatement.executeUpdate();
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
    }

    private static String getRoundCodersMysql(Connection connection, int roundId)
        throws SQLException {
        String sql = "SELECT DISTINCT(coder_id) FROM component_state WHERE round_id = ?";
        String result = "";
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, roundId);

            ResultSet resultSet = null;

            try {
                resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    int k = resultSet.getInt(1);

                    if (result.length() > 0) {
                        result += ", ";
                    }

                    result += k;
                }

                return result;
            } finally {
                if (resultSet != null) {
                    resultSet.close();
                }
            }
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
    }

    /**
     *  Returns a list of contest ids, only between 1000-1999 if practice flag is set
     */
    public ArrayList getPracticeRoundIDs() throws DBServicesException {
        // First do a lookup to see if any practice contests need to be added
        java.sql.Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        ArrayList contestIds = new ArrayList(100);

        try {
            conn = DBMS.getConnection();

            String sqlCmd = "SELECT r.round_id FROM round r WHERE r.status = 'A' AND r.round_type_id IN ("
                + ContestConstants.PRACTICE_ROUND_TYPE_ID + "," + ContestConstants.TEAM_PRACTICE_ROUND_TYPE_ID + ","
                + ContestConstants.LONG_PROBLEM_PRACTICE_ROUND_TYPE_ID + ","
                + ContestConstants.AMD_LONG_PROBLEM_PRACTICE_ROUND_TYPE_ID + ") ORDER BY r.contest_id, r.round_id";
            ps = conn.prepareStatement(sqlCmd);
            rs = ps.executeQuery();

            while (rs.next()) {
                contestIds.add(Integer.valueOf(rs.getInt(1)));
            }

            return contestIds;
        } catch (Exception e) {
            DBMS.printException(e);
            throw new DBServicesException("" + e);
        } finally {
            close(conn, ps, rs);
        }
    }

    /**
     * This method is adds a certain number of minutes and seconds to the contest.
     * Authors: ads09, ademich.
     *
     * @param minutes   the number of minutes to add to the phase(s)
     * @param seconds   the number of seconds to add to the phase(s)
     * @param phase     segment id, refer to DBMS.java
     */
    public void addTime(Round contest, int minutes, int seconds, int phase, boolean addToStart)
        throws DBServicesException {
        debug("DBServices.addTime() called...");

        long toAdd = (minutes * 60000L) + (seconds * 1000L);

        StringBuilder sqlStr = new StringBuilder(128);
        PreparedStatement ps = null;
        java.sql.Connection conn = null;

        try {
            conn = DBMS.getConnection();
            conn.setAutoCommit(false);

            int startPhase = phase;

            if (!addToStart) {
                // kinda lame just increment all phases >= cur phase
                sqlStr.append(" UPDATE round_segment SET end_time = ? ");
                sqlStr.append(" WHERE round_id = ? AND segment_id = ? ");
                ps = conn.prepareStatement(sqlStr.toString());

                switch (phase) {
                case ServerContestConstants.CODING_SEGMENT_ID:
                    ps.setTimestamp(1, new Timestamp(contest.getCodingEnd().getTime() + toAdd));

                    break;

                case ServerContestConstants.CHALLENGE_SEGMENT_ID:
                    ps.setTimestamp(1, new Timestamp(contest.getChallengeEnd().getTime() + toAdd));

                    break;

                case ServerContestConstants.SYSTEM_TEST_SEGMENT_ID:
                    ps.setTimestamp(1, new Timestamp(contest.getSystemTestEnd().getTime() + toAdd));

                    break;

                case ServerContestConstants.REGISTRATION_SEGMENT_ID:
                    ps.setTimestamp(1, new Timestamp(contest.getRegistrationEnd().getTime() + toAdd));

                    break;

                case ServerContestConstants.INTERMISSION_SEGMENT_ID:
                    ps.setTimestamp(1, new Timestamp(contest.getIntermissionEnd().getTime() + toAdd));

                    break;

                case ServerContestConstants.MODERATED_CHAT_SEGMENT_ID: //added by SYHAAS 2002-05-20
                    ps.setTimestamp(1, new Timestamp(contest.getModeratedChatEnd().getTime() + toAdd));

                    break;

                default:
                    throw new IllegalArgumentException("Unknown segment id (" + phase + ").");
                }

                ps.setInt(2, contest.getRoundID());
                ps.setInt(3, phase);
                ps.executeUpdate();
                ps.close();
                ps = null;
                ++startPhase;

                if (phase == ServerContestConstants.CODING_SEGMENT_ID) {
                    //Me must keep the old coding length, and store if it was not stored yet.
                    long codingLength = contest.getCodingEnd().getTime() - contest.getCodingStart().getTime();

                    if (contest.getRoundProperties().getCodingLengthOverride() == null) {
                        roundDao.insertRoundPropertyCodingLenghtOverride(contest.getRoundID(), codingLength, conn);
                    }
                }
            }

            sqlStr.replace(0, sqlStr.length(), " UPDATE round_segment SET start_time=?, end_time = ? ");
            sqlStr.append(" WHERE round_id = ? AND segment_id = ? ");
            ps = conn.prepareStatement(sqlStr.toString());

            //            for (int seg_id = startPhase; seg_id <= ServerContestConstants.MODERATED_CHAT_SEGMENT_ID; seg_id++) {
            for (int seg_id = startPhase; seg_id < ServerContestConstants.MODERATED_CHAT_SEGMENT_ID; seg_id++) {
                ps.clearParameters();

                switch (seg_id) {
                case ServerContestConstants.CODING_SEGMENT_ID:
                    ps.setTimestamp(1, new Timestamp(contest.getCodingStart().getTime() + toAdd));
                    ps.setTimestamp(2, new Timestamp(contest.getCodingEnd().getTime() + toAdd));

                    break;

                case ServerContestConstants.CHALLENGE_SEGMENT_ID:
                    ps.setTimestamp(1, new Timestamp(contest.getChallengeStart().getTime() + toAdd));
                    ps.setTimestamp(2, new Timestamp(contest.getChallengeEnd().getTime() + toAdd));

                    break;

                case ServerContestConstants.SYSTEM_TEST_SEGMENT_ID:
                    ps.setTimestamp(1, new Timestamp(contest.getSystemTestStart().getTime() + toAdd));
                    ps.setTimestamp(2, new Timestamp(contest.getSystemTestEnd().getTime() + toAdd));

                    break;

                case ServerContestConstants.REGISTRATION_SEGMENT_ID:
                    ps.setTimestamp(1, new Timestamp(contest.getRegistrationStart().getTime() + toAdd));
                    ps.setTimestamp(2, new Timestamp(contest.getRegistrationEnd().getTime() + toAdd));

                    break;

                case ServerContestConstants.INTERMISSION_SEGMENT_ID:
                    ps.setTimestamp(1, new Timestamp(contest.getIntermissionStart().getTime() + toAdd));
                    ps.setTimestamp(2, new Timestamp(contest.getIntermissionEnd().getTime() + toAdd));

                    break;

                //                    case ServerContestConstants.MODERATED_CHAT_SEGMENT_ID://added by SYHAAS 2002-05-20
                //                        ps.setTimestamp(1, new Timestamp(contest.getModeratedChatStart().getTime() + toAdd));
                //                        ps.setTimestamp(2, new Timestamp(contest.getModeratedChatEnd().getTime() + toAdd));
                // break;
                default:
                    s_trace.error("Unkown segment id (" + seg_id + ").");

                    break;
                }

                ps.setInt(3, contest.getRoundID());
                ps.setInt(4, seg_id);
                ps.executeUpdate();
            }

            if (phase == ServerContestConstants.REGISTRATION_SEGMENT_ID) {
                //If the registration end time changed, we need to reschedule the room assignment phase. If it exists.
                Timestamp roomAssignmentStart = contest.getRoomAssignmentStart();
                Timestamp roomAssignmentEnd = contest.getRoomAssignmentEnd();

                if ((roomAssignmentStart != null) && (roomAssignmentEnd != null)) {
                    ps.clearParameters();
                    ps.setTimestamp(1, new Timestamp(roomAssignmentStart.getTime() + toAdd));
                    ps.setTimestamp(2, new Timestamp(roomAssignmentEnd.getTime() + toAdd));
                    ps.setInt(3, contest.getRoundID());
                    ps.setInt(4, ServerContestConstants.ROOM_ASSIGNMENT_SEGMENT_ID);
                    ps.executeUpdate();
                }
            }

            conn.commit();
            debug("Successfully updated time!");
        } catch (Exception e) {
            rollback(conn);
            DBMS.printException(e);
            throw new DBServicesException(e.getMessage());
        } finally {
            close(conn, ps, null);
        }
    }

    public void ejbCreate() {
    }

    public void setCoderLanguage(int coderID, int languageID)
        throws DBServicesException {
        s_trace.debug("setCoderLanguage, coderID=" + coderID + ", languageID=" + languageID);

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBMS.getConnection();

            String sqlStr = "UPDATE coder SET language_id = ? WHERE coder_id = ?";
            ps = conn.prepareStatement(sqlStr);
            ps.setInt(1, languageID);
            ps.setInt(2, coderID);

            int rows = ps.executeUpdate();
            s_trace.debug(rows + " rows updated in setUserStatus");
        } catch (Exception e) {
            DBMS.printException(e);
            throw new DBServicesException(e.toString());
        } finally {
            close(conn, ps, null);
        }
    }

    public void setUserStatus(String handle, boolean isActiveStatus)
        throws DBServicesException {
        s_trace.debug("setUserStatus, handle=" + handle + ", isActiveStatus=" + isActiveStatus);

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBMS.getConnection();

            String sqlStr;

            if (isActiveStatus) {
                sqlStr = "UPDATE user SET status = 'A' WHERE handle_lower = lower(?)";
            } else {
                sqlStr = "UPDATE user SET status = 'I' WHERE handle_lower = lower(?)";
            }

            ps = conn.prepareStatement(sqlStr);
            ps.setString(1, handle);

            int rows = ps.executeUpdate();
            s_trace.debug(rows + " rows updated in setUserStatus");
        } catch (Exception e) {
            DBMS.printException(e);
            throw new DBServicesException(e.toString());
        } finally {
            close(conn, ps, null);
        }
    }

    private int getDataFromMap(Map<Integer, Integer> data, int userID) {
        if (data.containsKey(userID)) {
            return data.get(userID);
        } else {
            return 0;
        }
    }

    /*
     *  If some map is null, then corresponding data must be present in ResultSet
     *  and will be retrieved from there.
     */
    private User resultSetToUser(ResultSet rs, Map<Integer, Integer> newRatingMap,
        Map<Integer, Integer> newHSRatingMap, Map<Integer, Integer> newMMRatingMap,
        Map<Integer, Integer> isLevelOneAdminMap, Map<Integer, Integer> isLevelTwoAdminMap)
        throws SQLException {
        if (rs.next()) {
            int userID = rs.getInt("user_id");
            String username = rs.getString("handle");
            int languageID = rs.getInt("language_id");
            Timestamp regDate = rs.getTimestamp("member_since");
            Timestamp lastLogin = rs.getTimestamp("last_login");
            String quote = rs.getString("quote");
            boolean isLevelOneAdmin = (((isLevelOneAdminMap != null) ? getDataFromMap(isLevelOneAdminMap, userID)
                                                                     : rs.getInt("is_level_one_admin")) != 0);
            boolean isLevelTwoAdmin = (((isLevelTwoAdminMap != null) ? getDataFromMap(isLevelTwoAdminMap, userID)
                                                                     : rs.getInt("is_level_two_admin")) != 0);
            String countryName = rs.getString("country_name");
            String stateCode = rs.getString("state_code");
            int emailStatus = rs.getInt("status_id");
            String coderType = rs.getString("coder_type_desc");
            String school = rs.getString("name");
            String userStatus = rs.getString("status");
            int schoolViewable = rs.getInt("viewable");

            // no comp_country_code
            if (countryName == null) {
                countryName = rs.getString("origin_country_name");
            }

            if ((countryName != null) && !countryName.equals(rs.getString("origin_country_name"))) {
                stateCode = null;
            }

            User u = new User(userID, username, languageID, regDate, lastLogin, quote, isLevelTwoAdmin,
                    isLevelOneAdmin, false, countryName, stateCode, emailStatus, coderType, school, schoolViewable,
                    userStatus);

            // add ratings
            int rating = rs.getInt("rating");

            if (rs.getString("rating") != null) {
                System.out.println("ADDING RATING " + rs.getString("rating"));

                Rating r = new Rating(Rating.ALGO, rating,
                        ((newRatingMap != null) ? getDataFromMap(newRatingMap, userID) : rs.getInt("new_rating")),
                        rs.getInt("num_ratings"), rs.getTimestamp("last_rated_event"), "");
                u.addRating(r);
            }

            rating = rs.getInt("hs_rating");

            if (rs.getString("hs_rating") != null) {
                //System.out.println("ADDING HS RATING " + rs.getString("hs_rating"));
                Rating r = new Rating(Rating.HS, rating,
                        ((newHSRatingMap != null) ? getDataFromMap(newHSRatingMap, userID) : rs.getInt("hs_rating")),
                        rs.getInt("hs_num_ratings"), rs.getTimestamp("hs_last_rated_event"), "TCHS");
                u.addRating(r);
            }

            rating = rs.getInt("mm_rating");

            if (rs.getString("mm_rating") != null) {
                //System.out.println("ADDING MM RATING " + rs.getString("mm_rating"));
                Rating r = new Rating(Rating.MM, rating,
                        ((newMMRatingMap != null) ? getDataFromMap(newMMRatingMap, userID) : rs.getInt("mm_rating")),
                        rs.getInt("mm_num_ratings"), rs.getTimestamp("mm_last_rated_event"), "MM");
                u.addRating(r);
            }

            return u;
        }

        return null;
    }

    private boolean isLevelOneAdmin(int user_id) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sqlStr = "SELECT gu.group_id " + "FROM group_user gu " + "WHERE gu.user_id=? AND gu.group_id = "
            + ServerContestConstants.ADMIN_LEVEL_ONE_GROUP_ID;

        try {
            conn = DBMS.getConnection();
            ps = conn.prepareStatement(sqlStr);
            ps.setInt(1, user_id);
            rs = ps.executeQuery();

            return rs.next();
        } finally {
            close(conn, ps, rs);
        }
    }

    private boolean isLevelTwoAdmin(int user_id) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sqlStr = "SELECT gu.group_id " + "FROM group_user gu " + "WHERE gu.user_id=? AND gu.group_id = "
            + ServerContestConstants.ADMIN_LEVEL_TWO_GROUP_ID;

        try {
            conn = DBMS.getConnection();
            ps = conn.prepareStatement(sqlStr);
            ps.setInt(1, user_id);
            rs = ps.executeQuery();

            return rs.next();
        } finally {
            close(conn, ps, rs);
        }
    }

    /**
     * This function takes as input a username and password, and outputs
     * the corresponding user object, null if failed to authenticate
     * @throws DBServicesException
     */

    // Deprecated
    public User authenticateUser(String username, String password)
        throws DBServicesException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sqlStr = "SELECT u.user_id, u.handle, cr.rating, 0, c.language_id, cr.num_ratings, c.member_since, "
            + "u.last_login, c.quote " + "FROM user u, rating cr, coder c "
            + "WHERE u.status = 'A' AND u.handle = ? AND u.password = ? AND u.user_id = c.coder_id "
            + "AND c.coder_id = cr.coder_id";

        try {
            conn = DBMS.getConnection();
            ps = conn.prepareStatement(sqlStr);
            ps.setString(1, username);
            ps.setString(2, password);
            rs = ps.executeQuery();

            if (rs.next()) {
                return getUser(conn, rs.getInt(1));
            } else {
                return null;
            }
        } catch (Exception e) {
            DBMS.printException(e);
            throw new DBServicesException("" + e);
        } finally {
            close(conn, ps, rs);
        }
    }

    /**
     * validate sso and return { handle, password } values.
     *
     * @param sso the sso to validate.
     * @return the { handle, password } array, or null if sso is invalid.
     *
     * @throws RemoteException if there are remote exceptions.
     * @throws DBServicesException if error occurs in db operations.
     * @since 1.2
     */
    public String[] validateSSOToken(String sso) throws RemoteException, DBServicesException {
        String SSO_HASH_SECRET = ApplicationServer.SSO_HASH_SECRET;
        String value = sso;

        if (value == null) {
            return null;
        }

        // Parse the sso value
        String[] parts = value.split("\\|");

        if (parts.length != 2) {
            return null;
        }

        // Parse user ID from the cookie value
        long userId;

        try {
            userId = Long.parseLong(parts[0]);
        } catch (NumberFormatException e) {
            // return null if parsing error occurred
            return null;
        }

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sqlStr = "SELECT u.user_id, u.handle, u.status,su.password "+
                "FROM security_user su, user AS u WHERE su.login_id = ? and u.user_id = su.login_id";

        try {
            conn = DBMS.getConnection();
            ps = conn.prepareStatement(sqlStr);
            ps.setLong(1, userId);
            rs = ps.executeQuery();

            if (rs.next()){
                String password = rs.getString("password");
                String status = rs.getString("status");
                String handle = rs.getString("handle");
                //cleanup
                rs.close();

                String hashedValue = parts[1];
                String realHashedValue = hashForUser(SSO_HASH_SECRET, userId, password, status);
                
                if (realHashedValue.equals(hashedValue)) {
                    password = Util.decodePassword(password, "users");
                    return new String[] { handle, password };
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            DBMS.printException(e);
            throw new DBServicesException("" + e);
        } finally {
            close(conn, ps, rs);
        }
    }

    /**
     * Compute a one-way hash of a userid and the corresponding crypted
     * password, plus a magic string thrown in for good measure.  Salting
     * this might be nice, but it doesn't seem to buy us anything as long
     * as the magic string remains a secret.
     * <p/>
     * The intent here is that
     * 1) login cookies cannot be guessed
     * 2) changing your password should invalidate any login cookies which may exist
     * 3) login cookies cannot be used to gain any information about the password
     * 4) if user status changes, it invalidates login cookies
     * <p/>
     * I would just tack on the crypted password itself, but they are
     * reversibly encrypted with a secret key using Blowfish, and I don't
     * know how well Blowfish holds up to a chosen-plaintext attack.
     * <p/>
     *
     * @param uid the user id
     * @return the hash
     * @throws Exception if there is a problem getting data from the data base or if the SHA-256 algorithm doesn't exist
     */
    private String hashForUser(String SSO_HASH_SECRET,long id, String password, String status) throws Exception {

        String plainString = SSO_HASH_SECRET + id + password + status;
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] plain = (plainString).getBytes();
        byte[] raw = md.digest(plain);
        StringBuffer hex = new StringBuffer();
        for (byte aRaw : raw) hex.append(Integer.toHexString(aRaw & 0xff));
        String hashString = hex.toString();

        return hashString;
    }

    /**
     * This method loads a user from the database using his handle.
     * @throws DBServicesException
     */
    public User getUser(String username) throws DBServicesException {
        return getUser(username, false);
    }

    /**
     * This method loads a user from the database using his handle
     * with a flag to perform search case insentively or not.
     * @throws DBServicesException
     */
    public User getUser(String username, boolean ignoreCase)
        throws DBServicesException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBMS.getConnection();

            if (ignoreCase) {
                ps = conn.prepareStatement(
                        "SELECT u.user_id FROM user u WHERE u.handle_lower = LOWER(?) and u.status='A' ");
            } else {
                ps = conn.prepareStatement("SELECT u.user_id FROM user u WHERE u.handle = ? and u.status='A' ");
            }

            ps.setString(1, username);
            rs = ps.executeQuery();

            if (rs.next()) {
                return getUser(conn, rs.getInt(1));
            }
        } catch (Exception e) {
            DBMS.printException(e);
            throw new DBServicesException("" + e);
        } finally {
            close(conn, ps, rs);
        }

        return null;
    }

    public int getComponentSystemTestStatus(int coderID, int componentID, int roundID)
        throws DBServicesException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int retval = DBServices.SYSTEM_TESTS_PENDING;

        try {
            conn = DBMS.getConnection();
            ps = conn.prepareStatement(
                    "select status_id, system_test_version from component_state where component_id = ? and round_id = ? and coder_id = ? ");
            ps.setInt(1, componentID);
            ps.setInt(2, roundID);
            ps.setInt(3, coderID);
            rs = ps.executeQuery();

            if (rs.next()) {
                int status = rs.getInt(1);
                int version = rs.getInt(2);

                if (status >= ContestConstants.SYSTEM_TEST_SUCCEEDED) {
                    retval = (status == ContestConstants.SYSTEM_TEST_SUCCEEDED) ? DBServices.SYSTEM_TESTS_PASSED
                                                                                : DBServices.SYSTEM_TESTS_FAILED;
                } else {
                    if (version < 0) {
                        retval = DBServices.SYSTEM_TESTS_FAILED;
                    } else {
                        int expected = getSystemTestCasesCount(componentID, conn);
                        int count = getPassedSystemTestResultsCount(coderID, componentID, roundID, conn);
                        retval = (count == expected) ? DBServices.SYSTEM_TESTS_PASSED : DBServices.SYSTEM_TESTS_PENDING;
                    }
                }
            }
        } catch (Exception e) {
            DBMS.printException(e);
            throw new DBServicesException("" + e);
        } finally {
            close(conn, ps, rs);
        }

        return retval;
    }

    public int getSystemTestCasesCount(int componentID, Connection cnn)
        throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = cnn.prepareStatement("SELECT count(*) FROM system_test_case WHERE component_id=? AND status = 1");
            ps.setInt(1, componentID);
            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

            return 0;
        } finally {
            DBMS.close(ps, rs);
        }
    }

    public int getPassedSystemTestResultsCount(int coderID, int componentID, int roundID, Connection cnn)
        throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = cnn.prepareStatement(
                    "SELECT count(*) FROM system_test_result WHERE component_id = ? and round_id = ? and coder_id = ? and succeeded = 1");
            ps.setInt(1, componentID);
            ps.setInt(2, roundID);
            ps.setInt(3, coderID);
            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

            return 0;
        } finally {
            DBMS.close(ps, rs);
        }
    }

    public String getFailureMessage(int coderID, int componentID, int roundID)
        throws DBServicesException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String retval = "";

        try {
            conn = DBMS.getConnection();
            ps = conn.prepareStatement(
                    "select st.received, c.args, c.expected_result from system_test_result st, system_test_case c where st.succeeded = 0 and c.test_case_id = st.test_case_id and st.round_id = ? and st.coder_id = ? and st.component_id = ?");
            ps.setInt(1, roundID);
            ps.setInt(2, coderID);
            ps.setInt(3, componentID);
            rs = ps.executeQuery();

            if (rs.next()) {
                retval = ContestConstants.makePretty(DBMS.getBlobObject(rs, 2));
                retval += ("\n    EXPECTED: " + ContestConstants.makePretty(DBMS.getBlobObject(rs, 3)));
                retval += ("\n    RECEIVED: " + ContestConstants.makePretty(DBMS.getBlobObject(rs, 1)) + "\n");
            }
        } catch (Exception e) {
            DBMS.printException(e);
            throw new DBServicesException("" + e);
        } finally {
            close(conn, ps, rs);
        }

        return retval;
    }

    public User getUser(int userID, boolean activeOnly)
        throws DBServicesException {
        Connection conn = null;

        try {
            conn = DBMS.getConnection();

            return getUser(conn, userID, activeOnly);
        } catch (DisabledUserDBException e) {
            throw e;
        } catch (Exception e) {
            DBMS.printException(e);
            throw new DBServicesException("" + e);
        } finally {
            close(conn, null, null);
        }
    }

    /**
     * This method loads a User from the database.
     * @throws DBServicesException
     */
    public User getUser(int userID) throws DBServicesException {
        return getUser(userID, false);
    }

    private String getHandle(int userID) throws DBServicesException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sqlStr = "SELECT u.handle FROM user u WHERE u.user_id = ?";

        try {
            conn = DBMS.getConnection();
            ps = conn.prepareStatement(sqlStr);
            ps.setInt(1, userID);
            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString(1);
            } else {
                throw new DBServicesException("No user found for ID " + userID);
            }
        } catch (Exception e) {
            DBMS.printException(e);
            throw new DBServicesException("" + e);
        } finally {
            close(conn, ps, rs);
        }
    }

    private List getUserBatch(Connection conn, int roundId, Collection userIDs, Map<Integer, Integer> newRatingMap,
        Map<Integer, Integer> newHSRatingMap, Map<Integer, Integer> newMMRatingMap,
        Map<Integer, Integer> isLevelOneAdminMap, Map<Integer, Integer> isLevelTwoAdminMap)
        throws SQLException, DisabledUserDBException {
        s_trace.info("getUserBatch: checkpoint 1");

        List users = new ArrayList(userIDs.size());
        Set remain = new HashSet(userIDs);
        Map userMap = new HashMap(userIDs.size());

        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder sb = new StringBuilder((userIDs.size() * 2) - 1);
        buildBatchParameters(sb, userIDs.size());

        try {
            s_trace.info("getUserBatch: checkpoint 2");
            ps = conn.prepareStatement(GET_USER_SQL_BATCH);
            ps.setInt(1, roundId);
            s_trace.info("getUserBatch: checkpoint 3");
            rs = ps.executeQuery();
            s_trace.info("getUserBatch: checkpoint 4");

            while (true) {
                User u = resultSetToUser(rs, newRatingMap, newHSRatingMap, newMMRatingMap, isLevelOneAdminMap,
                        isLevelTwoAdminMap);

                if (u == null) {
                    break;
                }

                remain.remove(Integer.valueOf(u.getID()));
                users.add(u);
                userMap.put(Integer.valueOf(u.getID()), u);
            }

            if (remain.size() != 0) {
                int userID = ((Integer) remain.iterator().next()).intValue();
                s_trace.warn("Disabled user trying to access: " + userID);
                throw new DisabledUserDBException("UserId=" + userID + " is disabled");
            }

            rs.close();
            ps.close();

            s_trace.info("getUserBatch: checkpoint 5");

            // Team information
            remain.addAll(userIDs);
            ps = conn.prepareStatement(TEAM_INFO_BATCH.replaceFirst("BATCH_PARAMETERS", sb.toString()));

            int index = 1;

            for (Iterator iter = userIDs.iterator(); iter.hasNext(); ++index) {
                ps.setInt(index, ((Integer) iter.next()).intValue());
            }

            s_trace.info("getUserBatch: checkpoint 6");
            rs = ps.executeQuery();
            s_trace.info("getUserBatch: checkpoint 7");

            while (rs.next()) {
                Integer coderId = Integer.valueOf(rs.getInt("coder_id"));
                User u = (User) userMap.get(coderId);
                remain.remove(coderId);
                u.setTeamID(rs.getInt("team_id"));
                //u.setCaptain(rs.getInt("captain") == TeamConstants.TEAM_CAPTAIN);
                u.setTeamName(rs.getString("team_name"));
                u.setHSRegion(rs.getInt("region_id"));
            }

            for (Iterator iter = remain.iterator(); iter.hasNext();) {
                User u = (User) userMap.get(iter.next());
                u.setTeamID(User.NO_TEAM);
                u.setCaptain(false);
            }

            rs.close();
            ps.close();

            s_trace.info("getUserBatch: checkpoint 8");

            ps = conn.prepareStatement(SEASON_INFO_BATCH.replaceFirst("BATCH_PARAMETERS", sb.toString()));
            index = 1;

            for (Iterator iter = userIDs.iterator(); iter.hasNext(); ++index) {
                ps.setInt(index, ((Integer) iter.next()).intValue());
            }

            s_trace.info("getUserBatch: checkpoint 9");
            rs = ps.executeQuery();
            s_trace.info("getUserBatch: checkpoint 10");

            while (rs.next()) {
                User u = (User) userMap.get(Integer.valueOf(rs.getInt("user_id")));
                //s_trace.debug("ADDING: " + rs.getInt("season_id"));
                u.addSeason(rs.getInt("season_id"));
            }

            s_trace.info("getUserBatch: checkpoint 11");

            return users;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        } finally {
            //close(null, ps, rs);
            ps.close();
            rs.close();
        }
    }

    private User getUser(Connection conn, int userID) throws SQLException, DBServicesException {
        return getUser(conn, userID, false);
    }

    private User getUser(Connection conn, int userID, boolean activeOnly)
        throws SQLException, DBServicesException {
        PreparedStatement ps = null;
        ResultSet rs = null;

        User u = null;

        try {
            ps = conn.prepareStatement(GET_USER_SQL);
            ps.setInt(1, ServerContestConstants.ADMIN_LEVEL_ONE_GROUP_ID);
            ps.setInt(2, ServerContestConstants.ADMIN_LEVEL_TWO_GROUP_ID);
            ps.setInt(3, userID);
            rs = ps.executeQuery();
            u = resultSetToUser(rs, null, null, null, null, null);

            if ((u == null) || (activeOnly && !rs.getString("status").equals("A"))) {
                s_trace.warn("Disabled user trying to access: " + userID);
                throw new DisabledUserDBException("UserId=" + userID + " is disabled");
            }

            rs.close();
            ps.close();

            // Team information
            ps = conn.prepareStatement(TEAM_INFO);
            ps.setInt(1, userID);
            rs = ps.executeQuery();

            if (rs.next()) {
                u.setTeamID(rs.getInt("team_id"));
                //u.setCaptain(rs.getInt("captain") == TeamConstants.TEAM_CAPTAIN);
                u.setTeamName(rs.getString("team_name"));
                u.setHSRegion(rs.getInt("region_id"));
            } else {
                u.setTeamID(User.NO_TEAM);
                u.setCaptain(false);
            }

            rs.close();
            ps.close();

            ps = conn.prepareStatement(SEASON_INFO);
            ps.setInt(1, userID);
            rs = ps.executeQuery();

            while (rs.next()) {
                //s_trace.debug("ADDING: " + rs.getInt("season_id"));
                u.addSeason(rs.getInt("season_id"));
            }

            debug("user id: " + userID + " team id: " + u.getTeamID());

            return u;
        } finally {
            close(null, ps, rs);
        }
    }

    // deprecated
    public User getHighSchoolUser(int userID) throws DBServicesException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        /*
           String sqlStr =
                   "SELECT u.user_id, u.handle, cr.rating, c.editor_id, c.language_id, cr.num_ratings, c.member_since, " +
                   "u.last_login, c.quote " +
                   "FROM user u, rating cr, coder c " +
                   "WHERE u.status = 'A' AND u.user_id = ? AND u.user_id = c.coder_id " +
                   "AND c.coder_id = cr.coder_id";
         */
        User u = null;

        try {
            conn = DBMS.getConnection();
            ps = conn.prepareStatement(GET_USER_SQL);
            ps.setInt(1, ServerContestConstants.ADMIN_LEVEL_ONE_GROUP_ID);
            ps.setInt(2, ServerContestConstants.ADMIN_LEVEL_TWO_GROUP_ID);
            ps.setInt(3, userID);
            rs = ps.executeQuery();
            u = resultSetToUser(rs, null, null, null, null, null);

            return u;
        } catch (Exception e) {
            DBMS.printException(e);
            throw new DBServicesException("" + e);
        } finally {
            close(conn, ps, rs);
        }
    }

    /**
     * Populates a Collection with adminBroadcasts from the given resultset.  Does not populate any of the "name"
     * fields, or other extra data for round/problem broadcasts.
     */
    private Collection resultSetToBroadcastList(ResultSet rs)
        throws SQLException {
        ArrayList list = new ArrayList();

        try {
            while (rs.next()) {
                int type = rs.getInt(5);

                switch (type) {
                case ContestConstants.BROADCAST_TYPE_ADMIN_GENERIC: {
                    AdminBroadcast b = new AdminBroadcast();
                    b.setTime(rs.getTimestamp(1).getTime());
                    b.setMessage((String) DBMS.getBlobObject(rs, 2));
                    list.add(b);

                    break;
                }

                case ContestConstants.BROADCAST_TYPE_ADMIN_ROUND: {
                    RoundBroadcast b = new RoundBroadcast();
                    b.setTime(rs.getTimestamp(1).getTime());
                    b.setMessage((String) DBMS.getBlobObject(rs, 2));
                    b.setRoundID(rs.getInt(3));
                    list.add(b);

                    break;
                }

                case ContestConstants.BROADCAST_TYPE_ADMIN_COMPONENT: {
                    ComponentBroadcast b = new ComponentBroadcast();
                    b.setTime(rs.getTimestamp(1).getTime());
                    b.setMessage((String) DBMS.getBlobObject(rs, 2));
                    b.setRoundID(rs.getInt(3));
                    b.setComponentID(rs.getInt(4));
                    list.add(b);

                    break;
                }
                }
            }
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            SQLException ex = new SQLException(e.getMessage());
            throw ex;
        }

        s_trace.debug("Broadcast refresh: returning " + list.size() + " broadcasts.");

        return list;
    }

    public Collection getRecentBroadcasts(long minTimeSent, int round_id)
        throws DBServicesException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sqlStr = "SELECT b.date_sent, b.message, b.round_id, b.component_id, b.broadcast_type_id, b.status_id ";
        sqlStr += "FROM broadcast b WHERE ((b.date_sent > ?) OR (b.round_id = ?))";

        try {
            conn = DBMS.getConnection();
            ps = conn.prepareStatement(sqlStr);
            ps.setTimestamp(1, new Timestamp(minTimeSent));

            if (round_id == -1) {
                ps.setNull(2, java.sql.Types.INTEGER);
            } else {
                ps.setInt(2, round_id);
            }

            rs = ps.executeQuery();

            return resultSetToBroadcastList(rs);
        } catch (Exception e) {
            DBMS.printException(e);
            throw new DBServicesException("" + e);
        } finally {
            close(conn, ps, rs);
        }
    }

    public Collection getRoundBroadcasts(int round_id)
        throws DBServicesException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sqlStr = "SELECT b.date_sent, b.message, b.round_id, b.component_id, b.broadcast_type_id, b.status_id ";
        sqlStr += "FROM broadcast b WHERE b.round_id = ?";

        try {
            conn = DBMS.getConnection();
            ps = conn.prepareStatement(sqlStr);
            ps.setInt(1, round_id);
            rs = ps.executeQuery();

            return resultSetToBroadcastList(rs);
        } catch (Exception e) {
            DBMS.printException(e);
            throw new DBServicesException("" + e);
        } finally {
            close(conn, ps, rs);
        }
    }

    private void rollback(Connection c) {
        if (c != null) {
            try {
                c.rollback();
            } catch (Exception e) {
                printException(e);
            }
        }
    }

    //    private final static String UPDATE_USERNAME = "UPDATE user SET handle = ? WHERE user_id = ?";
    public User createUser(String handle, String id, int companyID) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int numUpdated = 0;

        try {
            int userID = IdGeneratorClient.getSeqIdAsInt(DBMS.JMA_SEQ);
            conn = DBMS.getConnection();

            ps = conn.prepareStatement(INSERT_SECURE_OBJECT);
            ps.setInt(1, userID);
            numUpdated = ps.executeUpdate();

            if (numUpdated != 1) {
                throw new SQLException("Trying to update handle in createUser updated " + numUpdated + " rows.");
            }

            ps.close();

            ps = conn.prepareStatement(INSERT_USER);
            ps.setInt(1, userID);
            ps.setString(2, handle);
            numUpdated = ps.executeUpdate();

            if (numUpdated != 1) {
                throw new SQLException("Trying to update handle in createUser updated " + numUpdated + " rows.");
            }

            ps.close();

            ps = conn.prepareStatement(INSERT_CODER);
            ps.setInt(1, userID);
            numUpdated = ps.executeUpdate();

            if (numUpdated != 1) {
                throw new SQLException("Trying to update handle in createUser updated " + numUpdated + " rows.");
            }

            ps.close();

            ps = conn.prepareStatement(INSERT_RATING);
            ps.setInt(1, userID);
            numUpdated = ps.executeUpdate();

            if (numUpdated != 1) {
                throw new SQLException("Trying to update handle in createUser updated " + numUpdated + " rows.");
            }

            ps.close();

            ps = conn.prepareStatement(INSERT_GROUP);
            ps.setInt(1, ServerContestConstants.CODER_GROUP_ID);
            ps.setInt(2, userID);
            numUpdated = ps.executeUpdate();

            if (numUpdated != 1) {
                throw new SQLException("Trying to update handle in createUser updated " + numUpdated + " rows.");
            }

            ps.close();

            ps = conn.prepareStatement(INSERT_COMPANY_USER_XREF);
            ps.setInt(1, companyID);
            ps.setInt(2, userID);
            ps.setString(3, id);
            numUpdated = ps.executeUpdate();

            if (numUpdated != 1) {
                throw new SQLException("Trying to update handle in createUser updated " + numUpdated + " rows.");
            }

            ps.close();
        } catch (Exception e) {
            s_trace.error("Exception in createUser: ", e);
        } finally {
            close(conn, ps, rs);
        }

        try {
            return getUser(handle);
        } catch (DBServicesException e) {
            s_trace.error(e);
            throw new EJBException(e.getMessage());
        }
    }

    public boolean checkTaken(String handle) {
        if (handle == null) {
            return false;
        }

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean ret = false;

        try {
            conn = DBMS.getConnection();
            ps = conn.prepareStatement("SELECT user_id FROM user WHERE handle_lower = ?");
            ps.setString(1, handle.toLowerCase());
            rs = ps.executeQuery();
            ret = rs.next();
        } catch (SQLException e) {
            s_trace.error("SQLException in checkTaken: " + e);
            e.printStackTrace();
        } finally {
            close(conn, ps, rs);
        }

        return ret;
    }

    public User getCompanyUser(int companyID, String companyUserID) {
        if (companyUserID == null) {
            return null;
        }

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int userID = -1;

        try {
            conn = DBMS.getConnection();
            ps = conn.prepareStatement(SELECT_USER_BY_REMOTE_ID);
            ps.setInt(1, companyID);
            ps.setString(2, companyUserID);
            rs = ps.executeQuery();

            // If the ID is already in the DB
            if (rs.next()) {
                userID = rs.getInt(1);
            }

            return getUser(userID);
        } catch (Exception e) {
            printException(e);
            e.printStackTrace();
        } finally {
            close(conn, ps, rs);
        }

        return null;
    }

    public void registerTeam(int teamID, int roundID, Collection coders)
        throws DBServicesException {
        debug("Registering team #" + teamID + " for round " + roundID);

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBMS.getConnection();
            conn.setAutoCommit(false);
            clearRegistrationForTeam(conn, teamID, roundID);
            ps = conn.prepareStatement(REGISTER_TEAM_MEMBER_QUERY);

            Timestamp now = new Timestamp(System.currentTimeMillis());

            for (Iterator it = coders.iterator(); it.hasNext();) {
                Integer userID = (Integer) it.next();
                ps.clearParameters();
                ps.setInt(1, roundID);
                ps.setObject(2, userID);
                ps.setTimestamp(3, now);
                ps.executeUpdate();
            }

            conn.commit();
        } catch (Exception e) {
            rollback(conn);
            DBMS.printException(e);
            throw new DBServicesException("" + e);
        } finally {
            close(conn, ps, null);
        }
    }

    public int getCoderSchoolID(int coderID) {
        int schoolID = 0;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBMS.getConnection();
            ps = conn.prepareStatement(SELECT_SCHOOL_IF_FROM_USER);
            ps.setInt(1, coderID);
            rs = ps.executeQuery();

            if (rs.next()) {
                schoolID = rs.getInt(1);
            }

            return schoolID;
        } catch (Exception e) {
            printException(e);
            e.printStackTrace();
        } finally {
            close(conn, ps, rs);
        }

        return schoolID;
    }

    public int getCoachIDFromSchoolID(int schoolID) {
        int coachCoderID = 0;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBMS.getConnection();
            ps = conn.prepareStatement(SELECT_COACH_ID_FROM_SCHOOL);
            ps.setInt(1, schoolID);
            rs = ps.executeQuery();

            if (rs.next()) {
                coachCoderID = rs.getInt(1);
            }

            return coachCoderID;
        } catch (Exception e) {
            printException(e);
            e.printStackTrace();
        } finally {
            close(conn, ps, rs);
        }

        return coachCoderID;
    }

    public int getSchoolIDFromCoach(int coderID) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int schoolID = 0;

        try {
            conn = DBMS.getConnection();
            ps = conn.prepareStatement(SELECT_SCHOOL_ID_FROM_COACH);
            ps.setInt(1, coderID);
            rs = ps.executeQuery();

            if (rs.next()) {
                schoolID = rs.getInt(1);
            }

            return schoolID;
        } catch (Exception e) {
            printException(e);
            e.printStackTrace();
        } finally {
            close(conn, ps, rs);
        }

        return schoolID;
    }

    private void clearRegistrationForTeam(Connection conn, int teamID, int roundID)
        throws SQLException {
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement(CLEAR_REGISTRATION_FOR_TEAM_QUERY);
            ps.setInt(1, teamID);
            ps.setInt(2, roundID);
            ps.executeUpdate();
        } finally {
            close(null, ps, null);
        }
    }

    public void commitTeam(Team team) throws DBServicesException {
        s_trace.debug("Committing team: " + team.getName() + " with members " + team.getMembers());

        Connection conn = null;

        try {
            conn = DBMS.getConnection();
            conn.setAutoCommit(false);
            clearTeamMembers(conn, team);

            for (Iterator iterator = team.getMembers().iterator(); iterator.hasNext();) {
                User user = (User) iterator.next();

                if (team.getCaptainID() == user.getID()) {
                    continue;
                }

                insertMember(conn, team, user);
            }

            conn.commit();
        } catch (SQLException e) {
            rollback(conn);
            DBMS.printException(e);
            throw new DBServicesException("" + e);
        } finally {
            close(conn, null, null);
        }
    }

    private void insertMember(Connection conn, Team team, User user)
        throws SQLException {
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement(INSERT_MEMBER_QUERY);
            ps.setInt(1, team.getID());
            ps.setInt(2, user.getID());
            ps.executeUpdate();
        } finally {
            close(null, ps, null);
        }
    }

    private void clearTeamMembers(Connection conn, Team team)
        throws SQLException {
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement(CLEAR_MEMBERS_QUERY);
            ps.setInt(1, team.getID());
            ps.executeUpdate();
        } finally {
            close(null, ps, null);
        }
    }

    public Team createTeam(String teamName, int captainID, int teamType)
        throws DBServicesException {
        s_trace.debug("Creating team: " + teamName + " with captain " + captainID);

        Connection conn = null;
        Team ret = null;

        try {
            conn = DBMS.getConnection();
            conn.setAutoCommit(false);

            int teamID = insertTeam(conn, teamName, teamType);
            setCaptain(conn, teamID, captainID);
            ret = new Team(teamID, teamName, getUser(conn, captainID));
            conn.commit();

            return ret;
        } catch (Exception e) {
            rollback(conn);
            DBMS.printException(e);
            throw new DBServicesException("" + e);
        } finally {
            close(conn, null, null);
        }
    }

    private void setCaptain(Connection conn, int teamID, int captainID)
        throws SQLException {
        String setCaptainQuery = "INSERT INTO team_coder_xref (team_id,coder_id,captain) VALUES ( ?, ?, 1 )";
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement(setCaptainQuery);
            ps.setInt(1, teamID);
            ps.setInt(2, captainID);
            ps.executeUpdate();
        } finally {
            close(null, ps, null);
        }
    }

    private int insertTeam(Connection conn, String teamName, int teamType)
        throws SQLException {
        String createTeamQuery = "INSERT INTO team (team_id,team_name,team_type) VALUES ( ?, ?, ? )";
        PreparedStatement ps = null;
        int teamID = 0;

        try {
            teamID = IdGeneratorClient.getSeqIdAsInt(DBMS.JMA_SEQ);
            ps = conn.prepareStatement(createTeamQuery);
            ps.setInt(1, teamID);
            ps.setString(2, teamName);
            ps.setInt(3, teamType);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            close(null, ps, null);
        }

        return teamID;
    }

    private static void close(Connection conn, PreparedStatement ps, ResultSet rs) {
        DBMS.close(conn, ps, rs);
    }

    // Exception writer
    private static void printException(Exception e) {
        DBMS.printException(e);
    }

    // Query runner
    private ResultSetContainer runSelectQuery(PreparedStatement ps)
        throws SQLException {
        ResultSet rs = null;

        try {
            rs = ps.executeQuery();

            return new ResultSetContainer(rs);
        } catch (Exception e) {
            printException(e);
            throw new SQLException(e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e1) {
                printException(e1);
            }
        }
    }

    public void insertRoomResult(int roundId, int lastRoomId, int coderId, int roomSeed, int rating, int divisionSeed)
        throws DBServicesException {
        Connection connection = null;

        try {
            connection = DBMS.getConnection();
            RoomResultTable.insert(connection, roundId, lastRoomId, coderId, roomSeed, rating, divisionSeed);
        } catch (SQLException e) {
            printException(e);
            throw new DBServicesException("Insertion failed: " + e.getMessage());
        } finally {
            DBMS.close(connection);
        }
    }

    public Round[] getPracticeRounds(int limit) throws DBServicesException {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = DBMS.getConnection();
            ps = connection.prepareStatement("select group_id from practice_group");
            rs = ps.executeQuery();
            List<Integer> groupIds = new ArrayList();
            while (rs.next()) {
            	groupIds.add(rs.getInt(1));
            }
            DBMS.close(ps, rs);

            List<Round> rounds = new ArrayList();
            for (Integer groupId : groupIds) {
                String sqlCmd = "SELECT FIRST " + limit + " r.round_id FROM round r, round_group_xref rgx WHERE r.status = 'A' "
                		+ "AND r.round_id = rgx.round_id AND rgx.group_id = ? "
                		+ "AND r.round_type_id IN ("
                        + ContestConstants.PRACTICE_ROUND_TYPE_ID + "," + ContestConstants.TEAM_PRACTICE_ROUND_TYPE_ID + ","
                        + ContestConstants.LONG_PROBLEM_PRACTICE_ROUND_TYPE_ID + ","
                        + ContestConstants.AMD_LONG_PROBLEM_PRACTICE_ROUND_TYPE_ID + ") "
                        + "ORDER BY r.contest_id, r.round_id";
                ps = connection.prepareStatement(sqlCmd);
                ps.setInt(1, groupId);
                rs = ps.executeQuery();
                List<Integer> roundIds = new ArrayList();
                while (rs.next()) {
                	roundIds.add(rs.getInt(1));
                }
                DBMS.close(ps, rs);

                for (Integer roundId : roundIds) {
                	rounds.add(getContestRound(connection, roundId, false));
                }
            }
            return rounds.toArray(new Round[rounds.size()]);
        } catch (SQLException e) {
            printException(e);
            throw new DBServicesException(e.getMessage());
        } finally {
            DBMS.close(connection);
        }
    }

    private static Connection getConnection() throws SQLException {
        return DBMS.getConnection();
    }

    public WeakestLinkData loadWeakestLinkData(int roundId)
        throws DBServicesException {
        Connection connection = null;

        try {
            connection = getConnection();

            return WeakestLinkCoderTable.loadWeakestLinkData(connection, roundId);
        } catch (SQLException e) {
            printException(e);
            throw new DBServicesException("Operation failed: " + e.getMessage());
        } finally {
            DBMS.close(connection);
        }
    }

    public WeakestLinkTeam getWeakestLinkTeam(int teamId, int roundId)
        throws DBServicesException {
        Connection connection = null;

        try {
            connection = getConnection();

            return WeakestLinkCoderTable.getWeakestLinkTeam(connection, teamId, roundId);
        } catch (SQLException e) {
            printException(e);
            throw new DBServicesException("Operation failed: " + e.getMessage());
        } finally {
            DBMS.close(connection);
        }
    }

    public void storeWeakestLinkData(WeakestLinkData weakestLinkData, int targetRoundId)
        throws DBServicesException {
        Connection connection = null;

        try {
            connection = getConnection();
            WeakestLinkCoderTable.storeWeakestLinkData(connection, weakestLinkData, targetRoundId);
        } catch (SQLException e) {
            printException(e);
            throw new DBServicesException("Operation failed: " + e.getMessage());
        } finally {
            DBMS.close(connection);
        }
    }

    private Collection getAllWeakestLinkUsers(WeakestLinkData weakestLinkData)
        throws DBServicesException {
        Collection result = new ArrayList();
        WeakestLinkTeam[] teams = weakestLinkData.getTeams();

        for (int i = 0; i < teams.length; i++) {
            WeakestLinkCoder[] coders = teams[i].getCoders();
            s_trace.debug("Found " + coders.length + " coders in team " + i);

            for (int j = 0; j < coders.length; j++) {
                WeakestLinkCoder coder = coders[j];
                int coderId = coder.getCoderId();
                User user = getUser(coderId);
                result.add(user);
            }
        }

        return result;
    }

    public void storeBadgeId(int roundId, int coderId, String badgeId)
        throws DBServicesException {
        Connection connection = null;

        try {
            connection = getConnection();
            WeakestLinkCoderTable.storeBadgeId(connection, roundId, coderId, badgeId);
        } catch (SQLException e) {
            printException(e);
            throw new DBServicesException("Operation failed: " + e.getMessage());
        } finally {
            DBMS.close(connection);
        }
    }

    public void saveComponentAssignmentData(ComponentAssignmentData data)
        throws DBServicesException {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean commit = false;
        boolean rollback = false;

        try {
            conn = DBMS.getConnection();
            conn.setAutoCommit(false);
            rollback = true;

            String removeOldAssignments = "DELETE FROM team_coder_component_xref WHERE team_id = ? AND round_id = ? ";
            ps = conn.prepareStatement(removeOldAssignments);
            ps.setInt(1, data.getTeamID());
            ps.setInt(2, data.getRoundID());
            ps.executeUpdate();

            int[] components = data.getAssignedComponents();
            String assignComponent = "INSERT INTO team_coder_component_xref (team_id, coder_id, component_id, round_id, active) VALUES (?, ?, ?, ?, ?) ";
            ps = conn.prepareStatement(assignComponent);
            ps.setInt(1, data.getTeamID());
            ps.setInt(4, data.getRoundID());
            ps.setInt(5, 1);

            for (int i = 0; i < components.length; i++) {
                ps.setInt(2, data.getAssignedUserForComponent(components[i]));
                ps.setInt(3, components[i]);
                ps.executeUpdate();
            }

            rollback = false;
            commit = true;
        } catch (Exception e) {
            s_trace.error("Error saving component assignment data.", e);
            throw new DBServicesException(e.toString());
        } finally {
            if (rollback) {
                try {
                    conn.rollback();
                } catch (Exception e) {
                    s_trace.error("", e);
                }
            } else if (commit) {
                try {
                    conn.commit();
                } catch (Exception e) {
                    s_trace.error("", e);
                }
            }

            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception e) {
                    s_trace.error("", e);
                }
            }

            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {
                    s_trace.error("", e);
                }
            }
        }
    }

    public ComponentAssignmentData getComponentAssignmentData(int teamID, int roundID)
        throws DBServicesException {
        Connection conn = null;

        try {
            conn = DBMS.getConnection();

            return getComponentAssignmentData(conn, teamID, roundID);
        } catch (Exception e) {
            s_trace.error("Error getting component assignments data.", e);
            throw new DBServicesException(e.toString());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {
                    s_trace.error("", e);
                }
            }
        }
    }

    private ComponentAssignmentData getComponentAssignmentData(Connection conn, int teamID, int roundID)
        throws DBServicesException {
        ComponentAssignmentData data = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            HashMap assignments = new HashMap();
            String getAssignments = "SELECT coder_id, component_id FROM team_coder_component_xref WHERE team_id = ? AND round_id = ? AND active = 1";
            ps = conn.prepareStatement(getAssignments);
            ps.setInt(1, teamID);
            ps.setInt(2, roundID);
            rs = ps.executeQuery();

            while (rs.next()) {
                assignments.put(Integer.valueOf(rs.getInt("component_id")), Integer.valueOf(rs.getInt("coder_id")));
            }

            ArrayList teamMembers = new ArrayList();
            Team team = getTeam(conn, teamID, roundID);
            System.out.println("team = " + team);

            Iterator j = team.getMemberNames().iterator();

            for (Iterator i = team.getMembers().iterator(); i.hasNext();) {
                teamMembers.add(new UserListItem((String) j.next(), -1, ((Integer) i.next()).intValue(),
                        ContestConstants.SINGLE_USER));
                debug("now teamMembers=" + teamMembers);
            }

            data = new ComponentAssignmentData(teamID, roundID);
            data.setAssignments(assignments);
            data.setTeamMembers(teamMembers);
        } catch (Exception e) {
            s_trace.error("Error getting component assignments data.", e);
            throw new DBServicesException(e.toString());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                    s_trace.error("", e);
                }
            }

            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception e) {
                    s_trace.error("", e);
                }
            }
        }

        return data;
    }

    private static void debug(Object message) {
        s_trace.debug(message);
    }

    // deprecated
    public RegistrationResult registerUser(String userHandle, String password, String firstName, String lastName,
        String email, String phoneNumber) throws DBServicesException {
        try {
            RegistrationResult registrationResult;

            // normally this would work, however, we'll need to check against
            // common_oltp as well
            User user = authenticateUser(userHandle, password);

            if (user == null) {
                if (verifyUserCommonOltp(userHandle)) {
                    try {
                        insertNewUser(userHandle, password, firstName, lastName, email, phoneNumber);
                    } catch (SQLException e) {
                        throw new RuntimeException("" + e);
                    }

                    registrationResult = RegistrationResult.getSuccessfulRegistration();
                } else {
                    registrationResult = RegistrationResult.getUnsuccessfulRegistration(
                            "The handle is already taken.");
                }
            } else {
                registrationResult = RegistrationResult.getUnsuccessfulRegistration("The handle is already taken.");
            }

            return registrationResult;
        } catch (IDGenerationException e) {
            s_trace.error("The user could not be registered", e);
            throw new DBServicesException("User registration failed");
        }
    }

    public boolean verifyUserCommonOltp(String username)
        throws DBServicesException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sqlStr = "SELECT u.user_id " + "FROM common_oltp:user u " + "WHERE lower(u.handle) = ? ";

        try {
            conn = DBMS.getConnection();
            ps = conn.prepareStatement(sqlStr);
            ps.setString(1, username.toLowerCase());
            rs = ps.executeQuery();

            if (rs.next()) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            DBMS.printException(e);
            throw new DBServicesException("" + e);
        } finally {
            close(conn, ps, rs);
        }
    }

    private static int getNewUserId() throws IDGenerationException {
        return IdGeneratorClient.getSeqIdAsInt(DBMS.MAIN_SEQ);
    }

    private static int getNewEmailId() throws IDGenerationException {
        return IdGeneratorClient.getSeqIdAsInt(DBMS.MAIN_SEQ);
    }

    private static int getNewPhoneId() throws IDGenerationException {
        return IdGeneratorClient.getSeqIdAsInt(DBMS.MAIN_SEQ);
    }

    private static void insertNewUser(String userHandle, String password, String firstName, String lastName,
        String email, String phoneNumber) throws SQLException, IDGenerationException {
        int userId = getNewUserId();
        // insertNewUserIntoSecureObjectTable(userId);
        insertNewUserIntoUserTable(userId, userHandle, password, firstName, lastName);
        insertNewUserIntoEmailTable(userId, email);
        insertNewUserIntoCoderTable(userId);
        insertNewUserIntoPhoneTable(userId, phoneNumber);
        insertNewUserIntoRatingTable(userId);
    }

    private static void insertNewUserIntoPhoneTable(int userId, String phoneNumber)
        throws SQLException, IDGenerationException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;

        try {
            String sql = "INSERT INTO phone (user_id, phone_id, phone_type_id, phone_number, primary_ind) "
                + "VALUES (?, ?, 1, ?, 1)";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, getNewPhoneId());
            preparedStatement.setString(3, phoneNumber);
            preparedStatement.executeUpdate();
        } finally {
            close(connection, preparedStatement, null);
        }
    }

    private static void insertNewUserIntoEmailTable(int userId, String email)
        throws SQLException, IDGenerationException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;

        try {
            String sql = "INSERT INTO email (user_id, email_id, email_type_id, address, primary_ind, status_id) "
                + "VALUES (?, ?, 1, ?, 1, 1)";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, getNewEmailId());
            preparedStatement.setString(3, email);
            preparedStatement.executeUpdate();
        } finally {
            close(connection, preparedStatement, null);
        }
    }

    private static void insertNewUserIntoUserTable(int userId, String userHandle, String password, String firstName,
        String lastName) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;

        try {
            String sql = "INSERT INTO user (user_id, handle, password, first_name, last_name, status) "
                + "VALUES (?, ?, ?, ?, ?, 'A')";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, userHandle);
            preparedStatement.setString(3, password);
            preparedStatement.setString(4, firstName);
            preparedStatement.setString(5, lastName);
            preparedStatement.executeUpdate();
        } finally {
            close(connection, preparedStatement, null);
        }
    }

    private static void insertNewUserIntoCoderTable(int userId)
        throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;

        try {
            String sql = "INSERT INTO coder (coder_id, language_id, coder_type_id) VALUES (?, ?, 2)";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, ContestConstants.JAVA);
            preparedStatement.executeUpdate();
        } finally {
            close(connection, preparedStatement, null);
        }
    }

    private static void insertNewUserIntoRatingTable(int userId)
        throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;

        try {
            String sql = "INSERT INTO rating (coder_id) VALUES (?)";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, userId);
            preparedStatement.executeUpdate();
        } finally {
            close(connection, preparedStatement, null);
        }
    }

    public void readMessage(int userID, int messageID)
        throws DBServicesException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder sqlStr = new StringBuilder(200);
        java.sql.Connection conn = null;

        try {
            conn = DBMS.getConnection();
            sqlStr.append("insert into user_message ");
            sqlStr.append("(user_id, message_id, status_id) ");
            sqlStr.append("values (?,?,1) ");

            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, userID);
            ps.setInt(2, messageID);
            ps.execute();
        } catch (Exception e) {
            printException(e);
            throw new DBServicesException(e.toString());
        } finally {
            close(conn, ps, rs);
        }
    }

    public boolean isComponentOpened(int coderId, int roundId, int componentId)
        throws DBServicesException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder sqlStr = new StringBuilder(200);
        java.sql.Connection conn = null;

        try {
            conn = DBMS.getConnection();
            sqlStr.append("select count(*) from component_state ");
            sqlStr.append("where coder_id = ? ");
            sqlStr.append("and round_id = ? ");
            sqlStr.append("and component_id = ? ");

            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, coderId);
            ps.setInt(2, roundId);
            ps.setInt(3, componentId);
            rs = ps.executeQuery();
            rs.next();

            return rs.getInt(1) > 0;
        } catch (Exception e) {
            printException(e);
            throw new DBServicesException(e.toString());
        } finally {
            close(conn, ps, rs);
        }
    }

    public boolean isLongComponentOpened(int coderId, int roundId, int componentId)
        throws DBServicesException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder sqlStr = new StringBuilder(200);
        java.sql.Connection conn = null;

        try {
            conn = DBMS.getConnection();
            sqlStr.append("select count(*) from long_component_state ");
            sqlStr.append("where coder_id = ? ");
            sqlStr.append("and round_id = ? ");
            sqlStr.append("and component_id = ? ");

            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, coderId);
            ps.setInt(2, roundId);
            ps.setInt(3, componentId);
            rs = ps.executeQuery();
            rs.next();

            return rs.getInt(1) > 0;
        } catch (Exception e) {
            printException(e);
            throw new DBServicesException(e.toString());
        } finally {
            close(conn, ps, rs);
        }
    }
    /**
     * add to table user_group_xref with the group id and user id.
     * @param userId the user id that to be added.
     * @param groupId the group id that to be added.
     * @throws RemoteException if the ejb remote call error occurs.
     * @throws DBServicesException if any related error occurs.
     * @return true=add the user group, false=not add maybe the record is already exist.
     */
    public boolean addUserGroup(int userId, int groupId) throws DBServicesException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            conn = DBMS.getConnection();

            ps = conn.prepareStatement(GET_SPECIFIC_GROUP_COUNTS);
            ps.setInt(1, userId);
            ps.setInt(2, groupId);
            rs = ps.executeQuery();
            rs.next();

            if (rs.getInt(1) == 0) {
                ps = conn.prepareStatement(INSERT_USER_TO_GROUPS);
                IDGenerator userGroupIDGenerator = IDGeneratorFactory.getIDGenerator("USER_GROUP_SEQ");
                long userGroupId = userGroupIDGenerator.getNextID();
                ps.setLong(1, userGroupId);
                ps.setInt(2, userId);
                ps.setInt(3, groupId);
                int rows = ps.executeUpdate();

                if (rows != 1) {
                    throw new DBServicesException("Unexpected number of rows updated: " + rows);
                }
                return true;
            }
            return false;
        } catch (Exception e) {
            printException(e);
            throw new DBServicesException(e.toString());
        } finally {
            close(conn, ps, rs);
        }
    }
    /**
     * <p>
     * to check whether the record is exist in table algo_rating
     * </p>
     * @param coderId the coder id.
     * @param algo_rating_type the algo rating type from table algo_rating_type_lu.
     * @throws DBServicesException if any related error occurs.
     */
    private void handleCoderAlgoRatingRecord(int coderId, int algo_rating_type) throws DBServicesException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            conn = DBMS.getConnection();

            ps = conn.prepareStatement(GET_CODE_ALGO_RATING_COUNT);
            ps.setInt(1, coderId);
            ps.setInt(2, algo_rating_type);
            rs = ps.executeQuery();
            rs.next();

            if (rs.getInt(1) == 0) {
                ps = conn.prepareStatement(INSERT_CODER_ALOG_RATING);
                ps.setLong(1, coderId);
                ps.setInt(2, algo_rating_type);
                int rows = ps.executeUpdate();

                if (rows != 1) {
                    throw new DBServicesException("Unexpected number of rows updated: " + rows);
                }
            }
        } catch (Exception e) {
            printException(e);
            throw new DBServicesException(e.toString());
        } finally {
            close(conn, ps, rs);
        }
    }

    /**
     * <p>
     *     This method retrieves question answers of specific round question.
     * </p>
     * @param questionId
     *          the question_id
     * @return
     *          an ArrayList containing question answers.
     * @throws DBServicesException
     *          if any error occurs
     * @since 1.3
     */
    private ArrayList getRoundQuestionAnswers(int questionId) throws DBServicesException {
        ArrayList answers = new ArrayList();

        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            conn = DBMS.getConnection();

            ps = conn.prepareStatement(SELECT_ROUND_QUESTION_ANSWERS);
            ps.setInt(1, questionId);
            rs = ps.executeQuery();
            while (rs.next()) {
                AnswerData data = new AnswerData();
                data.setId(rs.getInt(1));
                data.setText(rs.getString(2));
                data.setSortOrder(rs.getInt(3));
                data.setCorrect(rs.getInt(4) > 0);

                answers.add(data);
            }
            return answers;
        } catch (Exception e) {
            printException(e);
            throw new DBServicesException(e.toString());
        } finally {
            close(conn, ps, rs);
        }
    }

    /**
     * <p>
     * Track web arena user actions.
     * </p>
     *
     * @param userHandle
     *          the user handle
     * @param actionName
     *          the action name
     * @param client
     *          the client used. Currently only 'web arena'
     * @param date
     *          the created date
     * @throws DBServicesException
     *          if related error occurs
     * @since 1.4
     */
    public void recordUserAction(String userHandle, String actionName, String client, java.util.Date date)
            throws DBServicesException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            conn = DBMS.getConnection();
            ps = conn.prepareStatement(INSERT_USER_ACTION_AUDIT);
            IDGenerator generator = IDGeneratorFactory.getIDGenerator("USER_ACTION_AUDIT_SEQ");
            long userActionAuditId = generator.getNextID();
            ps.setLong(1, userActionAuditId);
            ps.setString(2, userHandle);
            ps.setString(3, actionName);
            ps.setString(4, client);
            ps.setDate(5, new Date(date.getTime()));
            ps.executeUpdate();
        } catch (Exception e) {
            printException(e);
            throw new DBServicesException(e.toString());
        } finally {
            close(conn, ps, rs);
        }
    }
}
