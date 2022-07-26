/*
 * Copyright (C) 2006-2015 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.server.ejb.AdminServices;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJBException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contest.Question;
import com.topcoder.security.GeneralSecurityException;
import com.topcoder.security.RolePrincipal;
import com.topcoder.security.TCSubject;
import com.topcoder.security.policy.PermissionCollection;
import com.topcoder.security.policy.TCPermission;
import com.topcoder.server.AdminListener.AdminConstants;
import com.topcoder.server.AdminListener.request.AddTimeCommand;
import com.topcoder.server.AdminListener.request.AdvancePhaseRequest;
import com.topcoder.server.AdminListener.request.AllocatePrizesRequest;
import com.topcoder.server.AdminListener.request.AnnounceAdvancingCodersRequest;
import com.topcoder.server.AdminListener.request.ApprovedQuestionCommand;
import com.topcoder.server.AdminListener.request.AssignRoomsCommand;
import com.topcoder.server.AdminListener.request.BackEndChangeRoundRequest;
import com.topcoder.server.AdminListener.request.BackEndLoginRequest;
import com.topcoder.server.AdminListener.request.BackEndRefreshAccessRequest;
import com.topcoder.server.AdminListener.request.BackEndRoundAccessRequest;
import com.topcoder.server.AdminListener.request.BackupTablesRequest;
import com.topcoder.server.AdminListener.request.BanIPCommand;
import com.topcoder.server.AdminListener.request.BlobColumnRequest;
import com.topcoder.server.AdminListener.request.BootUserCommand;
import com.topcoder.server.AdminListener.request.CancelSystemTestCaseTestingCommand;
import com.topcoder.server.AdminListener.request.ClearCacheRequest;
import com.topcoder.server.AdminListener.request.ClearPracticeRoomsCommand;
import com.topcoder.server.AdminListener.request.ClearTestCasesCommand;
import com.topcoder.server.AdminListener.request.CoderObject;
import com.topcoder.server.AdminListener.request.CoderProblemObject;
import com.topcoder.server.AdminListener.request.ComponentBroadcastCommand;
import com.topcoder.server.AdminListener.request.ConsolidateTestRequest;
import com.topcoder.server.AdminListener.request.ContestManagementRequest;
import com.topcoder.server.AdminListener.request.CreateSystestsRequest;
import com.topcoder.server.AdminListener.request.DisableRoundCommand;
import com.topcoder.server.AdminListener.request.DisconnectRequest;
import com.topcoder.server.AdminListener.request.EnableRoundCommand;
import com.topcoder.server.AdminListener.request.EndContestCommand;
import com.topcoder.server.AdminListener.request.EndHSContestCommand;
import com.topcoder.server.AdminListener.request.GarbageCollectionRequest;
import com.topcoder.server.AdminListener.request.GenerateTemplateCommand;
import com.topcoder.server.AdminListener.request.GetBackupCopiesRequest;
import com.topcoder.server.AdminListener.request.GetLoggingStreamsRequest;
import com.topcoder.server.AdminListener.request.GetNewIDRequest;
import com.topcoder.server.AdminListener.request.GetPrincipalsRequest;
import com.topcoder.server.AdminListener.request.GetRoundProblemComponentsRequest;
import com.topcoder.server.AdminListener.request.GlobalBroadcastCommand;
import com.topcoder.server.AdminListener.request.ImportantMessagesRequest;
import com.topcoder.server.AdminListener.request.InsertPracticeRoomRequest;
import com.topcoder.server.AdminListener.request.LoadRoundRequest;
import com.topcoder.server.AdminListener.request.LoggingStreamSubscribeRequest;
import com.topcoder.server.AdminListener.request.LoggingStreamUnsubscribeRequest;
import com.topcoder.server.AdminListener.request.ObjectSearchRequest;
import com.topcoder.server.AdminListener.request.ObjectUpdateRequest;
import com.topcoder.server.AdminListener.request.ProblemObject;
import com.topcoder.server.AdminListener.request.RecalculateScoreRequest;
import com.topcoder.server.AdminListener.request.RefreshAllRoomsCommand;
import com.topcoder.server.AdminListener.request.RefreshBroadcastsCommand;
import com.topcoder.server.AdminListener.request.RefreshProbsCommand;
import com.topcoder.server.AdminListener.request.RefreshRegCommand;
import com.topcoder.server.AdminListener.request.RefreshRoomCommand;
import com.topcoder.server.AdminListener.request.RefreshRoomListsCommand;
import com.topcoder.server.AdminListener.request.RefreshRoundCommand;
import com.topcoder.server.AdminListener.request.RegisterUserRequest;
import com.topcoder.server.AdminListener.request.RegistrationObject;
import com.topcoder.server.AdminListener.request.ReplayListenerRequest;
import com.topcoder.server.AdminListener.request.ReplayReceiverRequest;
import com.topcoder.server.AdminListener.request.RestartEventTopicListenerRequest;
import com.topcoder.server.AdminListener.request.RestartServiceRequest;
import com.topcoder.server.AdminListener.request.RestoreRoundCommand;
import com.topcoder.server.AdminListener.request.RestoreTablesRequest;
import com.topcoder.server.AdminListener.request.RoomObject;
import com.topcoder.server.AdminListener.request.RoundBroadcastCommand;
import com.topcoder.server.AdminListener.request.RoundForwardCommand;
import com.topcoder.server.AdminListener.request.RoundIDCommand;
import com.topcoder.server.AdminListener.request.RoundObject;
import com.topcoder.server.AdminListener.request.RunRatingsRequest;
import com.topcoder.server.AdminListener.request.RunSeasonRatingsRequest;
import com.topcoder.server.AdminListener.request.SecurityCheck;
import com.topcoder.server.AdminListener.request.SecurityManagementRequest;
import com.topcoder.server.AdminListener.request.ServerReplySecurityCheck;
import com.topcoder.server.AdminListener.request.SetAdminForwardingAddressRequest;
import com.topcoder.server.AdminListener.request.SetForwardingAddressRequest;
import com.topcoder.server.AdminListener.request.SetForumIDRequest;
import com.topcoder.server.AdminListener.request.SetRoundTermsRequest;
import com.topcoder.server.AdminListener.request.SetUserStatusCommand;
import com.topcoder.server.AdminListener.request.ShowSpecResultsCommand;
import com.topcoder.server.AdminListener.request.ShutdownRequest;
import com.topcoder.server.AdminListener.request.SpecAppShowRoomRequest;
import com.topcoder.server.AdminListener.request.StartSpecAppRotationRequest;
import com.topcoder.server.AdminListener.request.StopSpecAppRotationRequest;
import com.topcoder.server.AdminListener.request.SystemTestCommand;
import com.topcoder.server.AdminListener.request.TextColumnRequest;
import com.topcoder.server.AdminListener.request.TextSearchRequest;
import com.topcoder.server.AdminListener.request.TextUpdateRequest;
import com.topcoder.server.AdminListener.request.UnloadRoundRequest;
import com.topcoder.server.AdminListener.request.UnregisterUserRequest;
import com.topcoder.server.AdminListener.request.UnsupportedRequestException;
import com.topcoder.server.AdminListener.request.UpdatePlaceCommand;
import com.topcoder.server.AdminListener.request.UserObject;
import com.topcoder.server.AdminListener.request.WarehouseLoadRequest;
import com.topcoder.server.AdminListener.response.BlobColumnResponse;
import com.topcoder.server.AdminListener.response.ChangeRoundResponse;
import com.topcoder.server.AdminListener.response.CommandFailedResponse;
import com.topcoder.server.AdminListener.response.CommandResponse;
import com.topcoder.server.AdminListener.response.CommandSucceededResponse;
import com.topcoder.server.AdminListener.response.LoginResponse;
import com.topcoder.server.AdminListener.response.ObjectSearchResponse;
import com.topcoder.server.AdminListener.response.ObjectUpdateResponse;
import com.topcoder.server.AdminListener.response.RefreshAccessResponse;
import com.topcoder.server.AdminListener.response.RoundAccessItem;
import com.topcoder.server.AdminListener.response.RoundAccessResponse;
import com.topcoder.server.AdminListener.response.TextColumnResponse;
import com.topcoder.server.AdminListener.response.TextSearchResponse;
import com.topcoder.server.AdminListener.response.TextUpdateResponse;
import com.topcoder.server.AdminListener.security.RoundAccessPermission;
import com.topcoder.server.AdminListener.security.SecurityFacade;
import com.topcoder.server.AdminListener.security.TCStaffPermission;
import com.topcoder.server.common.BackupCopy;
import com.topcoder.server.common.ExpectedResult;
import com.topcoder.server.common.ServerContestConstants;
import com.topcoder.server.common.TestCase;
import com.topcoder.server.common.TestCaseArg;
import com.topcoder.server.common.User;
import com.topcoder.server.contest.AnswerData;
import com.topcoder.server.contest.ComponentData;
import com.topcoder.server.contest.ComponentType;
import com.topcoder.server.contest.ContestData;
import com.topcoder.server.contest.Difficulty;
import com.topcoder.server.contest.Division;
import com.topcoder.server.contest.ImportantMessageData;
import com.topcoder.server.contest.Language;
import com.topcoder.server.contest.ProblemData;
import com.topcoder.server.contest.ProblemStatus;
import com.topcoder.server.contest.ProblemType;
import com.topcoder.server.contest.QuestionData;
import com.topcoder.server.contest.QuestionStyle;
import com.topcoder.server.contest.QuestionType;
import com.topcoder.server.contest.Region;
import com.topcoder.server.contest.RoundComponentData;
import com.topcoder.server.contest.RoundData;
import com.topcoder.server.contest.RoundEventData;
import com.topcoder.server.contest.RoundLanguageData;
import com.topcoder.server.contest.RoundProblemData;
import com.topcoder.server.contest.RoundRoomAssignment;
import com.topcoder.server.contest.RoundSegmentData;
import com.topcoder.server.contest.RoundType;
import com.topcoder.server.contest.Season;
import com.topcoder.server.contest.SurveyData;
import com.topcoder.server.contest.SurveyStatus;
import com.topcoder.server.ejb.BaseEJB;
import com.topcoder.server.listener.monitor.ChatItem;
import com.topcoder.server.listener.monitor.QuestionItem;
import com.topcoder.server.services.CoreServices;
import com.topcoder.shared.util.logging.Logger;
import com.topcoder.shared.common.ApplicationServer;
import com.topcoder.shared.dataAccess.resultSet.Contains;
import com.topcoder.shared.dataAccess.resultSet.ResultSetContainer;
import com.topcoder.shared.distCache.CacheClient;
import com.topcoder.shared.distCache.CacheClientFactory;
import com.topcoder.shared.ratings.RatingProcessFactory;
import com.topcoder.shared.ratings.process.RatingProcess;
import com.topcoder.shared.util.DBMS;
import com.topcoder.shared.util.IdGeneratorClient;
import com.topcoder.shared.util.dwload.TCLoad;
import com.topcoder.shared.util.dwload.TCLoadUtility;
import com.topcoder.util.idgenerator.IDGenerationException;
import com.topcoder.utilities.RatingQubits;
import com.topcoder.utilities.matchReport.MatchSummaryHTML;

/**
 * The EJB class which handles database access routines for admin services.
 *
 * <p>Updates for AdminTool 2.0
 * <p> New getRoundRoomAssignment(int) method added to get the room assignment
 * algorithm for specified round.
 * <p> New saveRoundRoomAssignment(RoundRoomAssignment) method added to save
 * details of round room assignment algorithm for specified round.
 * <p> New SecurityFacade field used to access the new Security Schema
 * <p> Updated GET_ALL_ROUNDS_QUERY and GET_ROUND_QUERY to include the room
 * assignment data for the round. Added UPDATE_ROOM_ASSIGNMENT_QUERY for
 * updating the room assignments.
 * <p> updated getRequestFunctionIds to support new requests
 * <p> new method GetNewID() that gets a new id generated by specified sequence
 * <p> updated insertRound, updateRound, getRound, getRounds, setRoundSegments
 * deleteRound, updateRoundId methods to support the room assignment data.
 * <p> added saveRoundRoomAssignment
 * <p> updated processLoginRequest to use the new security schema
 * <p> deprecated grantAuthority/revokeAuthority
 * <p> updated processChangeRoundRequest, processRefreshAccessRequest to use
 * the new security schema
 *
 * <p>
 * Changes in version 1.0 (TopCoder Competition Engine - Event Support For Registration v1.0):
 * <ol>
 * <li>Added {@link #setRoundEvents(RoundEventData)} to set the round events.</li>
 * <li>Added {@link #getRoundEvents(Connection, int)} to get the round event data. </li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in (Round Type Option Support For SRM Problem):
 * <ol>
 * <li>Update {@link #ROUND_TYPE_ID_IN}  field.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in Version 1.1 (Topcoder Competition Engine - Eligibility Questions Validation):
 * <ol>
 *     <li>Added {@link #verifyQuestion(com.topcoder.server.contest.QuestionData)} method.</li>
 *     <li>Updated {@link #modifyQuestion(com.topcoder.server.contest.QuestionData)} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.2 (Module Assembly - TopCoder Competition Engine - Add A Configuration to
 * Flag SRM to end automatically):
 * <ol>
 *     <li>Updated <code>GET_ALL_ROUNDS_QUERY</code>, <code>GET_ROUND_QUERY</code> and
 *         <code>UPDATE_ROUND_QUERY</code> fields.</li>
 *     <li>Updated <code>getRound(int roundID)</code> method.</li>
 *     <li>Updated <code>getRounds(Connection conn, ResultSet result)</code> method.</li>
 *     <li>Updated <code>updateRound(Connection conn, RoundData round)</code> method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.3 (TopCoder Competition Engine - Add Editorial Link For Matches):
 * <ol>
 *     <li>Updated <code>GET_ALL_ROUNDS_QUERY</code>, <code>GET_ROUND_QUERY</code> and
 *         <code>UPDATE_ROUND_QUERY</code> fields.</li>
 *     <li>Updated <code>getRound(int roundID)</code> method.</li>
 *     <li>Updated <code>getRounds(Connection conn, ResultSet result)</code> method.</li>
 *     <li>Updated <code>updateRound(Connection conn, RoundData round)</code> method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.4 (TopCoder Competition Engine - Support Instant Match Type):
 * <ol>
 *     <li>Added INSTANT_MATCHES_ROUND_TYPE_ID in ROUND_TYPE_ID_IN.</li>
 * </ol>
 * </p>
 *
 * @author savon_cn, freegod, dexy, xjtufreeman
 * @version 1.4
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class AdminServicesBean extends BaseEJB {

    private static final Logger log = Logger.getLogger(AdminServicesBean.class);

    /**
     * A facade to newly integrated TCS Security Manager component that
     * should be used by Admin Services EJB to process requests related
     * to aplication's security schema.
     *
     * @since Admin Tool 2.0
     */
    private SecurityFacade securityFacade = new SecurityFacade();

    /**
     * The eligible question type id.
     * @since 1.1
     */
    private static final int ELIGIBLE_QUESTION_TYPE_ID = 2;

    /**
     * The max length of short answer text.
     * @since 1.1
     */
    private static final int MAX_SHORT_ANSWER_LENGTH = 40;

    /**
     * The max length of long answer text.
     * @since 1.1
     */
    private static final int MAX_LONG_ANSWER_LENGTH = 250;

    private static Map permissionsMap = new ConcurrentHashMap(); //a cache of all the permissions a user has.  Used to check for access to commands.

    /**
     * <p>
     * the cleaning sql to delete the round event data.
     * </p>
     */
    private static final String CLEAR_ROUND_EVENTS_QUERY =
        "DELETE FROM round_event WHERE round_id=?";

    /**
     * <p>
     * the insert sql to insert the round event data.
     * </p>
     */
    private static final String INSERT_ROUND_EVENTS_QUERY =
        "INSERT INTO round_event (round_id,event_id,event_name,registration_url) VALUES(?,?,?,?)";

    // Query utilities to make life easier
    private static void printException(Exception e) {
        try {
            if (e instanceof SQLException) {
                String sqlErrorDetails = DBMS.getSqlExceptionString((SQLException) e);
                log.error("Admin services EJB: SQLException caught\n" + sqlErrorDetails, e);
            } else {
                log.error("Admin services EJB: Exception caught", e);
            }
        } catch (Exception ex) {
            log.error("Admin services EJB: Error printing exception!");
        }
    }

    private ResultSetContainer runSelectQuery(String query) throws SQLException {
        String s[] = new String[1];
        s[0] = query;
        ResultSetContainer rsc[] = runSelectQuery(s);
        return rsc[0];
    }

    private ResultSetContainer[] runSelectQuery(String query[]) throws SQLException {
        Connection c = null;

        try {
            c = DBMS.getConnection();
            ResultSetContainer rsc[] = runSelectQuery(c, query);
            c.close();
            c = null;
            return rsc;
        } catch (Exception e) {
            try {
                if (c != null) c.close();
            } catch (Exception e1) {
                printException(e1);
            }
            c = null;
            throw new SQLException(e.getMessage());
        }
    }

    private ResultSetContainer runSelectQuery(Connection c, String query) throws SQLException {
        String s[] = new String[1];
        s[0] = query;
        ResultSetContainer rsc[] = runSelectQuery(c, s);
        return rsc[0];
    }

    private ResultSetContainer[] runSelectQuery(Connection c, String query[]) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ResultSetContainer rsc[] = new ResultSetContainer[query.length];
            for (int i = 0; i < query.length; i++) {
                if (log.isDebugEnabled()) {
                    StringBuilder sb = new StringBuilder(1000);
                    sb.append("------------\n");
                    sb.append("About to run query " + i + ":\n");
                    sb.append(query[i] + "\n");
                    sb.append("------------\n");
                    log.debug(sb.toString());
                }
                ps = c.prepareStatement(query[i]);
                rs = ps.executeQuery();
                rsc[i] = new ResultSetContainer(rs, false);
                rs.close();
                rs = null;
                ps.close();
                ps = null;
            }
            return rsc;
        } catch (Exception e) {
            for (int i = 0; i < query.length; i++) {
                StringBuilder sb = new StringBuilder(1000);
                sb.append("------------\n");
                sb.append("Query " + i + ":\n");
                sb.append(query[i] + "\n");
                sb.append("------------\n");
                log.error(sb.toString());
            }
            printException(e);
            try {
                if (rs != null) rs.close();
            } catch (Exception e1) {
                printException(e1);
            }
            try {
                if (ps != null) ps.close();
            } catch (Exception e1) {
                printException(e1);
            }
            rs = null;
            ps = null;
            throw new SQLException(e.getMessage());
        }
    }

    private ResultSetContainer runSelectQuery(PreparedStatement ps) throws SQLException {
        ResultSet rs = null;
        try {
            rs = ps.executeQuery();
            return new ResultSetContainer(rs);
        } catch (Exception e) {
            printException(e);
            throw new SQLException(e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
            } catch (Exception e1) {
                printException(e1);
            }
        }
    }

    private int runUpdateQuery(String query) throws SQLException {
        Connection c = null;
        try {
            c = DBMS.getConnection();
            int rowsModified = runUpdateQuery(c, query);
            c.close();
            c = null;
            return rowsModified;
        } catch (Exception e) {
            try {
                if (c != null) c.close();
            } catch (Exception e1) {
                printException(e1);
            }
            c = null;
            throw new SQLException(e.getMessage());
        }
    }

    private int runUpdateQuery(Connection c, String query) throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = c.prepareStatement(query);
            int rowsModified = ps.executeUpdate();
            ps.close();
            ps = null;
            return rowsModified;
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder(300);
            sb.append("------------\n");
            sb.append("Query:\n");
            sb.append(query + "\n");
            sb.append("------------\n");
            log.error(sb.toString());
            printException(e);
            try {
                if (ps != null) ps.close();
            } catch (Exception e1) {
                printException(e1);
            }
            ps = null;
            throw new SQLException(e.getMessage());
        }
    }

    public int recalculateScore(int roundId, String handle) {
        int result = -1;
        if (handle == null || handle.length() == 0 || handle.equals("0")) {
            result = recalculateScores(roundId);
        } else {
            User user = CoreServices.getUser(handle, true);
            if (user != null) {
                int coderId = user.getID();
                System.out.println(handle + "=" + coderId);
                result = recalculateScore(roundId, coderId);
            }
        }
        return result;
    }

    private int recalculateScore(int roundId, int coderId) {
        String query = buildRecalculateScoreQuery(roundId, coderId);
        System.out.println(query);
        int result = -1;
        try {
            result = runUpdateQuery(query);
        } catch (SQLException e) {
            printException(e);
        }
        return result;
    }

    private int recalculateScores(int roundId) {
        String query = buildRecalculateScoresQuery(roundId);
        System.out.println(query);
        int result = -1;
        try {
            result = runUpdateQuery(query);
        } catch (SQLException e) {
            printException(e);
        }
        return result;
    }


    private final static String buildRecalculateScoreQuery(int roundId, int coderId) {
        StringBuilder b = new StringBuilder(256);
        b.append("update room_result ");
        b.append("set point_total = ");
        b.append("(");
        b.append("select ");
        b.append("NVL((select sum(challenger_points) from challenge ");
        b.append("where challenger_id = c.coder_id and round_id = r.round_id),0) + ");
        b.append("NVL((select sum(points) from component_state ");
        b.append("where coder_id = c.coder_id and round_id = r.round_id),0) ");
        b.append("from coder c, round r ");
        b.append("where c.coder_id = room_result.coder_id and r.round_id = room_result.round_id ");
        b.append(") ");
        b.append("where round_id = ");
        b.append(roundId);
        b.append(" and coder_id = ");
        b.append(coderId);
        return b.toString();
    }

    private final static String buildRecalculateScoresQuery(int roundId) {
        StringBuilder b = new StringBuilder(256);
        b.append("update room_result ");
        b.append("set point_total = ");
        b.append("(");
        b.append("select ");
        b.append("NVL((select sum(challenger_points) from challenge ");
        b.append("where challenger_id = c.coder_id and round_id = r.round_id),0) + ");
        b.append("NVL((select sum(points) from component_state ");
        b.append("where coder_id = c.coder_id and round_id = r.round_id),0) ");
        b.append("from coder c, round r ");
        b.append("where c.coder_id = room_result.coder_id and r.round_id = room_result.round_id ");
        b.append(") ");
        b.append("where round_id = ");
        b.append(roundId);
        return b.toString();
    }

    private static final String GET_CONTESTS_QUERY = "SELECT " +
        "c.contest_id," +
        "c.name," +
        "c.start_date," +
        "c.end_date," +
        "c.status," +
        "c.group_id," +
        "c.ad_text," +
        "c.ad_start," +
        "c.ad_end," +
        "c.ad_task," +
        "c.ad_command," +
        "c.activate_menu," +
        "c.season_id," +
        "s.name" +
        " FROM (contest c " +
        " LEFT OUTER JOIN season s " +
        " ON c.season_id = s.season_id)";

    public Collection getAllContests() throws SQLException {
        log.debug("Getting all contests..");
        Map contestMap = new HashMap();
        ResultSet rs = null;
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBMS.getConnection();
            ps = conn.prepareStatement(GET_CONTESTS_QUERY);
            rs = ps.executeQuery();
            Vector r = new Vector();
            while (rs.next()) {
                ContestData data = populateContest(rs);
                r.add(data);
                contestMap.put(new Integer(data.getId()), data);
            }
            if (log.isDebugEnabled())
                log.debug("Query returned " + r.size() + " contests");
            return r;
        } catch (SQLException e) {
            printException(e);
            throw e;
        } finally {
            close(conn, ps, rs);
        }
    }

    private static final String GET_MESSAGES_QUERY = "SELECT " +
        "message_id," +
        "message," +
        "begin_date," +
        "end_date," +
        "status_id" +
        " FROM message";

    public Collection getAllImportantMessages() throws SQLException {
        log.debug("Getting all messages..");
        Map messages = new HashMap();
        ResultSet rs = null;
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBMS.getConnection();
            ps = conn.prepareStatement(GET_MESSAGES_QUERY);
            rs = ps.executeQuery();
            Vector r = new Vector();
            while (rs.next()) {
                ImportantMessageData data = populateMessage(rs);
                r.add(data);
                messages.put(new Integer(data.getId()), data);
            }
            if (log.isDebugEnabled())
                log.debug("Query returned " + r.size() + " contests");
            return r;
        } catch (SQLException e) {
            printException(e);
            throw e;
        } finally {
            close(conn, ps, rs);
        }
    }

    private ImportantMessageData populateMessage(ResultSet result) throws SQLException {
        ImportantMessageData data = new ImportantMessageData();
        data.setId(result.getInt(1));
        data.setMessage(result.getString(2));
        data.setStartDate(getDate(result, 3));
        data.setEndDate(getDate(result, 4));
        data.setStatus(result.getInt(5));
        return data;
    }


    private static final String GET_CONTEST_QUERY = GET_CONTESTS_QUERY +
        " WHERE contest_id = ?";

    public ContestData getContest(int contestID) throws SQLException {
        log.debug("Getting contest #" + contestID);
        ResultSet result = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DBMS.getConnection();
            stmt = conn.prepareStatement(GET_CONTEST_QUERY);
            stmt.setInt(1, contestID);
            result = stmt.executeQuery();
            if (result.next()) {
                ContestData data = populateContest(result);
                return data;
            } else {
                throw new EJBException("Invalid contest id: " + contestID);
            }
        } catch (SQLException e) {
            printException(e);
            throw e;
        } finally {
            close(conn, stmt, result);
        }
    }

    private ContestData populateContest(ResultSet result) throws SQLException {
        ContestData data = new ContestData();
        data.setId(result.getInt(1));
        data.setName(result.getString(2));
        data.setStartDate(getDate(result, 3));
        data.setEndDate(getDate(result, 4));
        data.setStatus(result.getString(5));
        data.setGroupId(result.getInt(6));
        data.setAdText(result.getString(7));
        data.setAdStartDate(getDate(result, 8));
        data.setAdEndDate(getDate(result, 9));
        data.setAdTask(result.getString(10));
        data.setAdCommand(result.getString(11));
        data.setActivateMenu(result.getBoolean(12));
        int season_id = result.getInt(13);
        if (result.wasNull()) {
            data.setSeason(new Season(null,result.getString(14)));
        } else {
            data.setSeason(new Season(new Integer(season_id),result.getString(14)));
        }
        return data;
    }


    private boolean hasPermission(long userId, TCPermission perm) {

        Set permissionSet = (Set)permissionsMap.get(new Long(userId));
        Iterator i = permissionSet.iterator();
        while(i.hasNext()) {
            Object o = i.next();

            if(perm.equals(o)) {
                return true;
            }
        }

        return false;
    }

    public boolean checkClientRequestAccess(SecurityCheck request) throws SQLException, UnsupportedRequestException {
        Connection c = null;
        try {
            c = DBMS.getConnection();
            long userId = request.getUserId();
            Object requestObject = request.getRequestObject();
            // Check round access
            if (requestObject instanceof RoundIDCommand) {
                RoundIDCommand rc = (RoundIDCommand) requestObject;
                int roundId = rc.getRoundID();
                if (!roundExists(c, roundId)) {
                    return false;
                } else if (!wasGrantedRoundAccess(c, userId, roundId)) {
                    return false;
                }
            } else if (requestObject instanceof GetRoundProblemComponentsRequest) {
                //hack for broadcasts
                GetRoundProblemComponentsRequest rc = (GetRoundProblemComponentsRequest) requestObject;
                int roundId = rc.getRoundID();
                if (!roundExists(c, roundId)) {
                    return false;
                } else if (!wasGrantedRoundAccess(c, userId, roundId)) {
                    return false;
                }
            }

            // Check command access
            // NO MORE ASSUMPTIONS ABOUT STAFF!!!!
            //if (isTCStaff(c, userId)) {
            //return true;
            //}

            ArrayList allowedIds = getRequestFunctionIds(requestObject);

            boolean found = false;
            for(int i = 0; i < allowedIds.size(); i++) {
                TCPermission perm = (TCPermission)allowedIds.get(i);
                if(hasPermission(userId, perm))
                    found = true;
            }

            return found;
        } catch (SQLException e) {
            printException(e);
            throw e;
        } catch (UnsupportedRequestException e) {
            printException(e);
            throw e;
        } finally {
            try {
                if (c != null) c.close();
            } catch (Exception e1) {
                printException(e1);
            }
        }
    }

    public List checkServerReplyAccess(ServerReplySecurityCheck request) throws Exception {
        Connection c = null;
        try {
            c = DBMS.getConnection();
            int recipientId = request.getRecipientId();
            Object requestObject = request.getResponseObject();
            Map connections = request.getClientConnections();
            ArrayList allowedRecipients = new ArrayList();

            // Find out what id's are for this request object type
            ArrayList allowedIds = getResponseFunctionIds(requestObject);

            if (recipientId != AdminConstants.RECIPIENT_ALL) {
                // Check only the one recipient
                Long userId = (Long) connections.get(new Integer(recipientId));
                if (userId == null) {
                    // Server is sending to a recipient not logged in; disallow
                    return allowedRecipients;
                }
                long uid = userId.longValue();
                for(int i = 0; i < allowedIds.size(); i++) {
                    TCPermission perm = (TCPermission)allowedIds.get(i);
                    if(hasPermission(uid, perm))
                        allowedRecipients.add(new Integer(recipientId));
                }
                return allowedRecipients;
            }

            // Check all recipients
            Iterator it = connections.keySet().iterator();
            while (it.hasNext()) {
                Integer recipient = (Integer) it.next();
                Long userId = (Long) connections.get(recipient);
                long uid = userId.longValue();
                for(int i = 0; i < allowedIds.size(); i++) {
                    TCPermission perm = (TCPermission)allowedIds.get(i);
                    if(hasPermission(uid, perm))
                        allowedRecipients.add(recipient);
                }
            }

            return allowedRecipients;
        } catch (Exception e) {
            printException(e);
            throw e;
        } finally {
            try {
                if (c != null) c.close();
            } catch (Exception e1) {
                printException(e1);
            }
        }
    }

    /**
     * This method will get the role principals from the user
     * and get all the permissions for each role and return them in a set.
     *
     * @param user - already authenticated user
     * @return - set of allowed permissions for all roles for the user
     * @see SecurityFacade#getPermissions
     */
    private Set getAllPermissions(TCSubject user ) {
        Set allowedFuncs = new HashSet();
        Set prin = user.getPrincipals();
        try {
            for( Iterator it = prin.iterator(); it.hasNext();) {
                RolePrincipal r = (RolePrincipal)it.next();
                log.debug("found role id = " + r.getId()+ " name = "+r.getName());
                PermissionCollection perms = securityFacade.getPermissions(r,user);
                for( Iterator it2 = perms.getPermissions().iterator();it2.hasNext();) {
                    TCPermission p = (TCPermission)it2.next();
                    debug( "   with permission = " + p.getName() );
                    allowedFuncs.add(p);
                }
            }
        } catch (GeneralSecurityException e) {
            log.error( "getAllPermissions had exception: "+ e.getMessage());
        }
        return allowedFuncs;
    }

    /**
     * this method will return a set that contains only non-round permissions
     * @param allFuncs - all the permissions
     * @return - a set of non-round permissions
     */
    private Set filterNonRoundPermissions(Set allFuncs ) {
        Set filtered = new HashSet();
        for( Iterator it = allFuncs.iterator(); it.hasNext(); ) {
            TCPermission p = (TCPermission)it.next();
            if( AdminConstants.isNonRoundSpecificPermission(p)) {
                filtered.add( p );
            }
        }
        return filtered;
    }

    /**
     * This method is modified to use newly integrated TCS Security Manager
     * component to perform user's authentication and authorization instead of
     * existing security schema. It uses newly added class SecurityFacade to
     * authenticate and authorize user. It constructs LoginResponse object
     * with TCSubject and Set of TCPermission objects. The returned Set of
     * TCPermissions must include only TCPermissions correspodning to non-round
     * specific functions from old schema. New
     * AdminConstants.isNonRoundSpecificPermission(TCPermission) method may be
     * used to filter the permissions returned with LoginResponse.
     *
     * @see SecurityFacade#login
     * @see SecurityFacade#getRoles
     * @see SecurityFacade#getPermissions
     * @see AdminConstants#getPermission
     * @see LoginResponse(TCSubject, boolean, Set)
     */
    public LoginResponse processLoginRequest(BackEndLoginRequest request) {
        LoginResponse failure = new LoginResponse(0, false, new HashSet());
        Connection c = null;
        PreparedStatement ps = null;
        TCSubject userPrincipal = null;
        try {

            // AdminTool 2.0 Security Schema
            // The 'allowedFuncs' set contains TCPermission objects.
            Set allowedFuncs = null;

            // We now imploy the security facade to authenticate the user
            // Once the user has been authenticated, we gather the permissions
            // objects.
            userPrincipal = securityFacade.login(request.getHandle(),
                                                 new String(request.getPassword()));
            if (userPrincipal == null ) {
                // User not found in the new security schema
                log.error("Login request received for invalid user" + request.getHandle());
                return failure;
            }
            // get all the permission for all the roles
            allowedFuncs = getAllPermissions(userPrincipal);

            //stash in the cache, for later user
            permissionsMap.put(new Long(userPrincipal.getUserId()), allowedFuncs);

            // now we use the ols schema to check the user's status
            c = DBMS.getConnection();
            ps = c.prepareStatement("SELECT user_id, password, status FROM user WHERE handle=?");
            ps.setString(1, request.getHandle());
            ResultSetContainer rsc = runSelectQuery(ps);
            ps.close();
            ps = null;

            // as of AdminTool 2.0, we do NOT check the password here, only the status
            // Since both security schemas are used, we need to ensure that the
            // user exists in the old security schema  'user' table also.
            // remove this check once the old schema is gone
            if( rsc.getColumnCount() == 0 ) {
                // user is NOT in the old security schema
                log.error("Login request received for invalid user (not found in old schema) " + request.getHandle());
                return failure;
            }
            long userId = Long.parseLong(rsc.getItem(0, 0).toString());
            String status = rsc.getItem(0, 2).toString();
            if ( status.equals(AdminConstants.INACTIVE_USER_STATUS)) {
                // non-active status
                log.error("Login: inactive status for user " + request.getHandle());
                return failure;
            }

            // We have a valid user.  Now we have to see if they're a TC staff member.
            if (isTCStaff(userId)) {
                return new LoginResponse(userPrincipal.getUserId(),
                                         true, filterNonRoundPermissions(allowedFuncs));
            }

            // now we need to ensure that the new security schema contains at least
            // one RoundAccessPermission permission.
            boolean found = false;
            for( Iterator it = allowedFuncs.iterator(); it.hasNext();) {
                TCPermission perm = (TCPermission)it.next();
                if( perm.getName().startsWith(RoundAccessPermission.PREFIX))
                    found = true;
            }
            if( !found ) {
                // user does not have access to any rounds
                log.error("Login request failed. No round access. For user=" +
                          request.getHandle());
                return failure;
            }

            // Success!
            // filter the permssion to include only 'non-round' permissions
            return new LoginResponse(userPrincipal.getUserId(), true,
                                     filterNonRoundPermissions(allowedFuncs));

        } catch (Exception e) {
            printException(e);
            return failure;
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (Exception e1) {
                printException(e1);
            }
            try {
                if (c != null) c.close();
            } catch (Exception e1) {
                printException(e1);
            }
        }
    }

    /**
     * This method was modified to return a set of TCPermissions to the user
     * instead of the Integer allowed functions.
     * Also this method must be modified to get all permissions granted to
     * TCSubject contained within BackEndChangeRoundRequest (regardless whether
     * the user is TC Staff or not) and populate the Set within
     * ChangeRounfResponse with those permissions.
     *
     * @param request to change to a round
     * @return a ChangeRoundResponse object
     */
    public ChangeRoundResponse processChangeRoundRequest(BackEndChangeRoundRequest request) {
        Connection c = null;
        try {
            c = DBMS.getConnection();
            long userId = request.getUserId();
            int roundId = request.getRoundId();

            // Does round exist?  If so, get the name.
            StringBuilder query = new StringBuilder();
            query.append("SELECT c.name || ' ' || r.name AS name ");
            query.append("FROM round r, contest c ");
            query.append("WHERE r.contest_id = c.contest_id ");
            query.append("AND r.round_id = " + roundId);

            ResultSetContainer rsc = runSelectQuery(c, query.toString());
            if (rsc.getRowCount() == 0) {
                return new ChangeRoundResponse(false, roundId, "", "Round " + roundId +
                                               " does not exist", new HashSet());
            }
            String roundName = rsc.getItem(0, 0).toString();

            // get the permissions from the new security schema, use the cache now
            Set permissionSet = (Set)permissionsMap.get(new Long(request.getUserId()));

            // Is user TC admin?
            if (isTCStaff(userId)) {
                return new ChangeRoundResponse(true, roundId, roundName,
                                               "Change round successful", permissionSet);
            }

            // now we need to ensure that the new security schema contains a
            // RoundAccessPermission permission for this round.
            boolean found = false;
            RoundAccessPermission rap = new RoundAccessPermission(roundId);
            for( Iterator it = permissionSet.iterator(); it.hasNext();) {
                TCPermission perm = (TCPermission)it.next();
                if( perm.equals(rap))
                    found = true;
            }
            if (!found) {
                return new ChangeRoundResponse(false, roundId, roundName,
                                               "You have insufficient rights to change to this round",
                                               new HashSet());
            }

            // Success
            // filter the permssion to include only 'non-round' permissions
            return new ChangeRoundResponse(true, roundId, roundName,
                                           "Change round successful",
                                           filterNonRoundPermissions(permissionSet));

        } catch (Exception e) {
            printException(e);
            return new ChangeRoundResponse(false, 0, "", "Change round failed.  EJB invocation exception: " + e.toString(),
                                           new HashSet());
        } finally {
            try {
                if (c != null) c.close();
            } catch (Exception e1) {
                printException(e1);
            }
        }
    }


    public RoundAccessResponse processRoundAccessRequest(BackEndRoundAccessRequest request) throws SQLException {
        Connection c = null;
        try {
            c = DBMS.getConnection();
            long userId = request.getUserId();
            Collection accessibleRounds;
            if (DBMS.DB == DBMS.INFORMIX) {
                accessibleRounds = getAccessibleRoundsInformix(c, userId);
            } else {
                accessibleRounds = getAccessibleRounds(c, userId);
            }
            return new RoundAccessResponse(accessibleRounds);
        } catch (SQLException e) {
            printException(e);
            throw e;
        } catch (Exception e) {
            printException(e);
            throw new EJBException(e);
        } finally {
            try {
                if (c != null) c.close();
            } catch (Exception e1) {
                printException(e1);
            }
        }
    }


    /**
     * This method was modified to get all permissions granted to TCSubject
     * contained within BackEndRefreshAccessRequest (regardless whether the user
     * is TC Staff or not) and populate the Set within RefreshAccessResponse
     * with those permissions. Note: if specified round is not valid then a
     * RefreshAccessResponse is popoulated only with permissions corresponding
     * to non-round specific functions (like processLoginRequest does).
     *
     * @param request to change to a round
     * @return a RefreshAccessResponse object
     */
    public RefreshAccessResponse processRefreshAccessRequest(BackEndRefreshAccessRequest request) {
        Connection c = null;
        try {
            c = DBMS.getConnection();

            long userId = request.getUserId();
            int roundId = request.getRoundId();

            // Do we have a valid round ID?
            boolean isValidRound;
            if (roundId == 0) {
                isValidRound = false;
            } else {
                isValidRound = roundExists(c, roundId);
            }

            // get the new schema permissions
            //TODO, you might need to fix this
            Set permissions = (Set)permissionsMap.get(new Long(request.getUserId()));

            //stash in the cache
            permissionsMap.put(new Long(request.getUserId()), permissions);

            // Is user TC staffer?
            if (isTCStaff(userId)) {
                if (isValidRound) {
                    return new RefreshAccessResponse(true, permissions);
                } else {
                    return new RefreshAccessResponse(true,
                                                     filterNonRoundPermissions(permissions));
                }
            }

            // Success
            return new RefreshAccessResponse(true, permissions);

        } catch (SQLException e) {
            printException(e);
            return new RefreshAccessResponse(false, new HashSet());
        } finally {
            try {
                if (c != null) c.close();
            } catch (Exception e1) {
                printException(e1);
            }
        }
    }



    private boolean wasGrantedRoundAccess(Connection c, long userId, int roundId) throws SQLException {
        if (isTCStaff(userId))
            return true;

        //this should check round info now
        RoundAccessPermission rap = new RoundAccessPermission(roundId);

        boolean found = false;

        Set permissionSet = (Set)permissionsMap.get(new Long(userId));
        for( Iterator it = permissionSet.iterator(); it.hasNext();) {
            TCPermission perm = (TCPermission)it.next();
            if( perm.equals(rap))
                found = true;
        }
        return found;
    }
    /**
     * the round type list sub query.
     */
    private static final String ROUND_TYPE_ID_IN = "r.round_type_id IN (" + ContestConstants.SRM_ROUND_TYPE_ID +
            ", " + ContestConstants.INSTANT_MATCHES_ROUND_TYPE_ID +
            ", " + ContestConstants.TOURNAMENT_ROUND_TYPE_ID +
            ", " + ContestConstants.SRM_QA_ROUND_TYPE_ID +
            ", " + ContestConstants.PRIVATE_LABEL_TOURNAMENT_ROUND_TYPE_ID +
            ", " + ContestConstants.LONG_ROUND_TYPE_ID +
            ", " + ContestConstants.TEAM_SRM_ROUND_TYPE_ID +
            ", " + ContestConstants.TEAM_TOURNAMENT_ROUND_TYPE_ID +
            ", " + ContestConstants.WEAKEST_LINK_ROUND_TYPE_ID +
            ", " + ContestConstants.HS_SRM_ROUND_TYPE_ID +
            ", " + ContestConstants.HS_TOURNAMENT_ROUND_TYPE_ID +
            ", " + ContestConstants.MODERATED_CHAT_ROUND_TYPE_ID +
            ", " + ContestConstants.LONG_PROBLEM_ROUND_TYPE_ID +
            ", " + ContestConstants.LONG_PROBLEM_QA_ROUND_TYPE_ID +
            ", " + ContestConstants.INTRO_EVENT_ROUND_TYPE_ID +
            ", " + ContestConstants.LONG_PROBLEM_TOURNAMENT_ROUND_TYPE_ID +
            ", " + ContestConstants.EDUCATION_ALGO_ROUND_TYPE_ID +
            ", " + ContestConstants.AMD_LONG_PROBLEM_ROUND_TYPE_ID +")";


    private static final String GET_ACCESIBLE_ROUNDS_QUERY =
        "SELECT " +
        "r.round_id," +
        "c.name || ' ' || r.name," +
        "rs.start_time " +
        " FROM (contest c JOIN round r ON c.contest_id=r.contest_id " +
        " LEFT OUTER JOIN round_segment rs ON rs.round_id = r.round_id ) " +
        " WHERE " + ROUND_TYPE_ID_IN + " AND (rs.segment_id = 1 OR rs.segment_id IS NULL) ";

    // the method is not complete!
    private Collection getAccessibleRounds(Connection connection, long userId) throws SQLException {
        if (!isTCStaff(userId)) {
            throw new RuntimeException("not implemented");
        }
        Statement statement = null;
        try {
            statement = connection.createStatement();
            String sql = "SELECT r.round_id, CONCAT(c.name, ' ', r.name) FROM round r, contest c WHERE c.contest_id=r.round_id AND " +
                ROUND_TYPE_ID_IN + " ORDER BY r.round_id";
            ResultSet resultSet = null;
            try {
                resultSet = statement.executeQuery(sql);
                Collection collection = new ArrayList();
                while (resultSet.next()) {
                    int roundId = resultSet.getInt(1);
                    String name = resultSet.getString(2);
                    Date date = new Date(System.currentTimeMillis());
                    collection.add(new RoundAccessItem(roundId, name, date));
                }
                return collection;
            } finally {
                if (resultSet != null) {
                    resultSet.close();
                }
            }
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    private Collection getAccessibleRoundsInformix(Connection c, long userId) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Vector r = new Vector();
        String orderBy = " ORDER BY rs.start_time DESC";
        try {
            ps = c.prepareStatement(GET_ACCESIBLE_ROUNDS_QUERY + orderBy);
            rs = ps.executeQuery();
            while (rs.next()) {
                boolean valid = false;

                if (isTCStaff(userId))
                    valid = true;

                if(hasPermission(userId, new RoundAccessPermission(rs.getInt(1))))
                    valid = true;

                if(valid) {
                    RoundAccessItem round = new RoundAccessItem(
                                                                rs.getInt(1),
                                                                rs.getString(2),
                                                                rs.getTimestamp(3)
                                                                );
                    r.add(round);
                }
            }
            return r;
        } finally {
            close(null, ps, null);
        }
    }


    private boolean roundExists(Connection c, int roundId) throws SQLException {
        String query = "SELECT round_id FROM round WHERE round_id = " + roundId;
        ResultSetContainer rsc = runSelectQuery(c, query);
        return (rsc.getRowCount() > 0);
    }

    private boolean isTCStaff(long userId) {
        return hasPermission(userId, new TCStaffPermission());
    }

    private ArrayList getResponseFunctionIds(Object response) throws UnsupportedRequestException {
        ArrayList allowedIds = new ArrayList();
        if (response instanceof ChatItem) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_CHAT_VIEW));
        } else if (response instanceof QuestionItem) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_MODERATED_CHAT));
        } else {
            throw new UnsupportedRequestException("Security check error: Unknown response type " + response.getClass().toString());
        }
        return allowedIds;
    }

    /**
     * This method was modified to support the new types of request
     * defined by Admin Tool 2.0 application. Those requests are :
     * WarehouseLoadRequest, GetNewRoundIDRequest, GetRegisteredCodersRequest,
     * SetRoundTermsRequest, GetPrincipalsRequest.<p>
     * When such requests are received the returned ArrayList should contain
     * an Integer with value equal to value of corresponding constant from
     * AdminConstants class.
     */
    private ArrayList getRequestFunctionIds(Object request) throws UnsupportedRequestException {
        ArrayList allowedIds = new ArrayList();
        if (request instanceof ShutdownRequest) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_SHUTDOWN));
        } else if (request instanceof DisconnectRequest) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_DISCONNECT_CLIENT));
        } else if (request instanceof ClearTestCasesCommand) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_CLEAR_TEST_CASES));
        } else if (request instanceof RefreshRegCommand) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_REFRESH_REGISTRATION));
        } else if (request instanceof SystemTestCommand) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_SYSTEM_TEST));
        } else if (request instanceof CancelSystemTestCaseTestingCommand) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_CANCEL_SYSTEM_TEST_CASE));
        } else if (request instanceof UpdatePlaceCommand) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_END_CONTEST));
        } else if (request instanceof EndContestCommand) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_END_CONTEST));
        } else if (request instanceof EndHSContestCommand) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_END_CONTEST));
        } else if (request instanceof RefreshProbsCommand) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_REFRESH_PROBLEMS));
        } else if (request instanceof RefreshRoomCommand) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_REFRESH_ROOM));
        } else if (request instanceof RefreshAllRoomsCommand) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_REFRESH_ALL_ROOMS));
        } else if (request instanceof ShowSpecResultsCommand) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_SHOW_SPEC_RESULTS));
        } else if (request instanceof RoundForwardCommand) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_FORWARD_ROUND));
        } else if (request instanceof RestoreRoundCommand) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_RESTORE_ROUND));
        } else if (request instanceof GlobalBroadcastCommand) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_SEND_GLOBAL_BROADCAST));
        } else if (request instanceof ComponentBroadcastCommand) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_SEND_COMPONENT_BROADCAST));
        } else if (request instanceof RoundBroadcastCommand) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_SEND_ROUND_BROADCAST));
        } else if (request instanceof RefreshBroadcastsCommand) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_REFRESH_BROADCASTS));
        } else if (request instanceof AddTimeCommand) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_ADD_TIME));
        } else if (request instanceof AssignRoomsCommand) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_ASSIGN_ROOMS));
        } else if (request instanceof SetUserStatusCommand) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_SET_USER_STATUS));
        } else if (request instanceof BanIPCommand) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_BAN_IP));
        } else if (request instanceof RecalculateScoreRequest) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_RECALCULATE_SCORE));
        } else if (request instanceof EnableRoundCommand) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_ENABLE_ROUND));
        } else if (request instanceof DisableRoundCommand) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_DISABLE_ROUND));
        } else if (request instanceof RefreshRoundCommand) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_REFRESH_ROUND));
        } else if (request instanceof UserObject) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_CACHED_USER));
        } else if (request instanceof RegistrationObject) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_CACHED_REGISTRATION));
        } else if (request instanceof ProblemObject) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_CACHED_PROBLEM));
        } else if (request instanceof RoundObject) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_CACHED_ROUND));
        } else if (request instanceof RoomObject) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_CACHED_ROOM));
        } else if (request instanceof CoderObject) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_CACHED_CODER));
        } else if (request instanceof CoderProblemObject) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_CACHED_CODER_PROBLEM));
        } else if (request instanceof RefreshRoomListsCommand) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_REFRESH_ROOM_LISTS));
        } else if (request instanceof ApprovedQuestionCommand) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_MODERATED_CHAT));
        } else if (request instanceof GetLoggingStreamsRequest) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_LOGGING));
        } else if (request instanceof SetForwardingAddressRequest) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_FORWARDING));
        } else if (request instanceof SetAdminForwardingAddressRequest) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_ADMIN_FORWARDING));
        } else if (request instanceof LoggingStreamSubscribeRequest) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_LOGGING));
        } else if (request instanceof LoggingStreamUnsubscribeRequest) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_LOGGING));
        } else if (request instanceof ContestManagementRequest) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_CONTEST_MANAGEMENT));
        } else if (request instanceof LoadRoundRequest) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_LOAD_ROUND));
        } else if (request instanceof UnloadRoundRequest) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_UNLOAD_ROUND));
        } else if (request instanceof GarbageCollectionRequest) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_GARBAGE_COLLECTION));
        } else if (request instanceof ReplayListenerRequest) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_START_REPLAY_LISTENER));
        } else if (request instanceof ReplayReceiverRequest) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_START_REPLAY_RECEIVER));
        } else if (request instanceof StartSpecAppRotationRequest) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_SPEC_APP_START_ROTATE));
        } else if (request instanceof StopSpecAppRotationRequest) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_SPEC_APP_STOP_ROTATE));
        } else if (request instanceof SpecAppShowRoomRequest) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_SPEC_APP_SHOW_ROOM));
        } else if (request instanceof AdvancePhaseRequest) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_ADVANCE_CONTEST_PHASE));
        } else if (request instanceof CreateSystestsRequest) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_CREATE_SYSTEM_TESTS));
        } else if (request instanceof ConsolidateTestRequest) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_CONSOLIDATE_TEST_CASES));
        } else if (request instanceof AllocatePrizesRequest) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_ALLOCATE_PRIZES));
        } else if (request instanceof RunRatingsRequest) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_RUN_RATINGS));
        } else if (request instanceof RunSeasonRatingsRequest) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_RUN_RATINGS));
        } else if (request instanceof RegisterUserRequest) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_REGISTER_USER));
        } else if (request instanceof UnregisterUserRequest) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_UNREGISTER_USER));
        } else if (request instanceof InsertPracticeRoomRequest) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_INSERT_PRACTICE_ROOM));
        } else if (request instanceof ObjectSearchRequest) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_BLOB_SEARCH));
        } else if (request instanceof BlobColumnRequest) {
            // The reason for returning an ArrayList instead of an int
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_OBJECT_LOAD));
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_BLOB_SEARCH));
        } else if (request instanceof ObjectUpdateRequest) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_OBJECT_LOAD));
        } else if (request instanceof AnnounceAdvancingCodersRequest) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_ANNOUNCE_ADVANCING_CODERS));
        } else if (request instanceof TextSearchRequest) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_TEXT_SEARCH));
        } else if (request instanceof TextColumnRequest) {
            // The reason for returning an ArrayList instead of an int
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_TEXT_LOAD));
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_TEXT_SEARCH));
        } else if (request instanceof TextUpdateRequest) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_TEXT_LOAD));
        } else if (request instanceof GetNewIDRequest) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_NEW_ID));
        } else if (request instanceof GetPrincipalsRequest) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_SECURITY_MANAGEMENT));
        } else if (request instanceof ClearPracticeRoomsCommand) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_CLEAR_PRACTICE_ROOMS));
        } else if (request instanceof BackupTablesRequest) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_BACKUP_TABLES));
        } else if (request instanceof SetRoundTermsRequest) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_SET_ROUND_TERMS));
        } else if (request instanceof SetForumIDRequest) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_CONTEST_MANAGEMENT));
        } else if (request instanceof ImportantMessagesRequest) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_IMPORTANT_MESSAGES));
        } else if (request instanceof SecurityManagementRequest) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_SECURITY_MANAGEMENT));
        } else if (request instanceof RestartServiceRequest) {
            allowedIds.add(AdminConstants.getPermission(((RestartServiceRequest)request).getRequestType()));
        } else if (request instanceof WarehouseLoadRequest) {
            allowedIds.add(AdminConstants.getPermission(((WarehouseLoadRequest)request).getRequestID()));
        } else if (request instanceof GetBackupCopiesRequest) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_RESTORE_TABLES));
        } else if (request instanceof RestoreTablesRequest) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_RESTORE_TABLES));
        } else if (request instanceof RecalculateScoreRequest) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_RECALCULATE_SCORE));
        } else if (request instanceof GetPrincipalsRequest) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_SECURITY_MANAGEMENT));
        } else if (request instanceof GetRoundProblemComponentsRequest) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_CONTEST_MANAGEMENT));
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_SEND_ROUND_BROADCAST));
        } else if (request instanceof RestartEventTopicListenerRequest) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_RESTART_EVENT_TOPIC_LISTENER));
        } else if (request instanceof ClearCacheRequest) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_CLEAR_CACHE));
        } else if (request instanceof GenerateTemplateCommand) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_GENERATE_TEMPLATE));
        } else if (request instanceof BootUserCommand) {
            allowedIds.add(AdminConstants.getPermission(AdminConstants.REQUEST_BOOT_USER));
        } else {
            throw new UnsupportedRequestException("Security check error: Unknown request type " + request.getClass().toString());
        }
        return allowedIds;
    }

    public void addContest(ContestData contest) throws SQLException {
        Connection conn = null;
        try {
            conn = DBMS.getConnection();
            conn.setAutoCommit(false);
            addContest(conn, contest);
            conn.commit();
        } catch (SQLException e) {
            printException(e);
            rollback(conn);
            throw e;
        } finally {
            close(conn, null, null);
        }
    }


    private static final String INSERT_CONTEST_QUERY =
        "INSERT INTO contest (contest_id,name) VALUES (?,?)";

    private void addContest(Connection conn, ContestData contest) throws SQLException {
        executeUpdate(conn, INSERT_CONTEST_QUERY, new Object[]{new Integer(contest.getId()), contest.getName()});
        updateContest(conn, contest);
    }


    private static final String UPDATE_CONTEST_QUERY =
        "UPDATE contest SET " +
        "name=?," +
        "start_date=?," +
        "end_date=?," +
        "status=?," +
        "group_id=?," +
        "ad_text=?," +
        "ad_start=?," +
        "ad_end=?," +
        "ad_task=?," +
        "ad_command=?," +
        "activate_menu=?," +
        "season_id=?" +
        " WHERE contest_id=?";

    private void updateContest(Connection conn, ContestData contest) throws SQLException {
        Object[] params = new Object[]{
            contest.getName(),
            new Timestamp(contest.getStartDate().getTime()),
            new Timestamp(contest.getEndDate().getTime()),
            contest.getStatus(),
            new Integer(contest.getGroupId()),
            contest.getAdText(),
            new Timestamp(contest.getAdStartDate().getTime()),
            new Timestamp(contest.getAdEndDate().getTime()),
            contest.getAdTask(),
            contest.getAdCommand(),
            new Boolean(contest.isActivateMenu()),
            contest.getSeason().getId(),
            new Integer(contest.getId())
        };
        executeUpdate(conn, UPDATE_CONTEST_QUERY, params);
    }


    public void modifyContest(int id, ContestData contest) throws SQLException {
        Connection conn = null;
        try {
            conn = DBMS.getConnection();
            conn.setAutoCommit(false);
            if (id != contest.getId()) {
                addContest(conn, contest);
                updateContestId(conn, new Integer(id), new Integer(contest.getId()));
                deleteContest(id);
            }
            updateContest(conn, contest);
            conn.commit();
        } catch (SQLException e) {
            rollback(conn);
            printException(e);
            throw e;
        } finally {
            close(conn, null, null);
        }
    }

    public void addMessage(ImportantMessageData message) throws SQLException {
        Connection conn = null;
        try {
            conn = DBMS.getConnection();
            conn.setAutoCommit(false);
            addMessage(conn, message);
            conn.commit();
        } catch (SQLException e) {
            printException(e);
            rollback(conn);
            throw e;
        } finally {
            close(conn, null, null);
        }
    }


    private static final String INSERT_MESSAGE_QUERY =
        "INSERT INTO message (message_id,message,begin_date, end_date, status_id) VALUES (?,?,?,?,?)";

    private void addMessage(Connection conn, ImportantMessageData message) throws SQLException {
        executeUpdate(conn, INSERT_MESSAGE_QUERY, new Object[]{new Integer(message.getId()), message.getMessage(),
                                                               new Timestamp(message.getStartDate().getTime()),
                                                               new Timestamp(message.getEndDate().getTime()),
                                                               new Integer(message.getStatus())});
    }


    private static final String UPDATE_MESSAGE_QUERY =
        "UPDATE message SET " +
        "message=?," +
        "begin_date=?," +
        "end_date=?," +
        "status_id=?" +
        " WHERE message_id=?";

    private void updateMessage(Connection conn, ImportantMessageData message) throws SQLException {
        Object[] params = new Object[]{
            message.getMessage(),
            new Timestamp(message.getStartDate().getTime()),
            new Timestamp(message.getEndDate().getTime()),
            new Integer(message.getStatus()),
            new Integer(message.getId())
        };
        executeUpdate(conn, UPDATE_MESSAGE_QUERY, params);
    }


    public void modifyMessage(int id, ImportantMessageData message) throws SQLException {
        Connection conn = null;
        try {
            conn = DBMS.getConnection();
            conn.setAutoCommit(false);
            updateMessage(conn, message);
            conn.commit();
        } catch (SQLException e) {
            rollback(conn);
            printException(e);
            throw e;
        } finally {
            close(conn, null, null);
        }
    }


    private static final String UPDATE_CONTEST_ROUND_ID_QUERY =
        "UPDATE round SET contest_id=? WHERE contest_id=?";

    private void updateContestId(Connection conn, Integer oldId, Integer newId) throws SQLException {
        executeUpdate(conn, UPDATE_CONTEST_ROUND_ID_QUERY, new Object[]{newId, oldId});
    }

    //    private static final String GET_ROUND_IDS_QUERY = "SELECT round_id FROM round WHERE contest_id=?";
    private static final String DELETE_CONTEST_QUERY = "DELETE FROM contest WHERE contest_id=?";

    public void deleteContest(int id) throws SQLException {
        Connection conn = null;
        ResultSet result = null;
        PreparedStatement stmt = null;
        try {
            conn = DBMS.getConnection();
            conn.setAutoCommit(false);
            Object[] contestId = new Object[]{new Integer(id)};
            //            Let's force the user to delete these manually
            //            stmt = conn.prepareStatement(GET_ROUND_IDS_QUERY);
            //            stmt.setInt(1,id);
            //            result = stmt.executeQuery();
            //            while (result.next()) {
            //                deleteRound(conn,result.getInt(1));
            //            }
            executeUpdate(conn, DELETE_CONTEST_QUERY, contestId);
            conn.commit();
        } catch (SQLException e) {
            rollback(conn);
            printException(e);
            throw e;
        } finally {
            close(conn, stmt, result);
        }
    }


    public Collection getConflictingContests(int contestID) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder sqlStr = new StringBuilder(120);
        ArrayList contests = new ArrayList();
        ContestData contest;

        try {
            conn = DBMS.getConnection();

            sqlStr.append(" SELECT contest_id, name, start_date, end_date, status ").
                append(" FROM contest ").
                append(" WHERE (start_date BETWEEN (SELECT start_date FROM contest WHERE contest_id = ?) AND ").
                append("                           (SELECT end_date FROM contest WHERE contest_id = ?) OR ").
                append("        end_date BETWEEN (SELECT start_date FROM contest WHERE contest_id = ?) AND ").
                append("                         (SELECT end_date FROM contest WHERE contest_id = ?)) AND ").
                append("       contest_id != ? ").
                append(" ORDER BY contest_id ");

            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, contestID);
            ps.setInt(2, contestID);
            ps.setInt(3, contestID);
            ps.setInt(4, contestID);
            ps.setInt(5, contestID);
            rs = ps.executeQuery();

            while (rs.next()) {
                contest = new ContestData();
                contest.setId(rs.getInt(1));
                contest.setName(rs.getString(2));
                contest.setStartDate(rs.getTimestamp(3));
                contest.setEndDate(rs.getTimestamp(4));
                contest.setStatus(rs.getString(5));
                contests.add(contest);
            }
            return contests;
        } catch (SQLException e) {
            printException(e);
            throw e;
        } finally {
            close(conn, ps, rs);
        }
    }
    /**
     * <p>
     * delete the round with specific roundId.
     * </p>
     * @param conn
     *       the connection.
     * @param _roundId
     *       the round id.
     * @throws SQLException
     *       if any error occur when executing delete round.
     */
    private void deleteRound(Connection conn, int _roundId) throws SQLException {
        Object[] roundId = new Object[]{new Integer(_roundId)};
        executeUpdate(conn, "DELETE FROM broadcast WHERE round_id=?", roundId);
        executeUpdate(conn, "DELETE FROM invite_list WHERE round_id=?", roundId);
        executeUpdate(conn, "DELETE FROM round_segment WHERE round_id=?", roundId);
        executeUpdate(conn, "DELETE FROM round_event WHERE round_id=?", roundId);
        executeUpdate(conn, "DELETE FROM round_registration WHERE round_id=?", roundId);
        executeUpdate(conn, "DELETE FROM system_test_result WHERE round_id=?", roundId);
        executeUpdate(conn, "DELETE FROM challenge WHERE round_id=?", roundId);
        //delete the related data with component_state
        /*
        executeUpdate(conn, "DELETE FROM submission_class_file where component_state_id in " +
                "(select component_state_id from component_state where round_id=?)", roundId);

        executeUpdate(conn, "DELETE FROM submission where component_state_id in " +
                "(select component_state_id from component_state where round_id=?)", roundId);

        executeUpdate(conn, "DELETE FROM compilation_class_file where component_state_id in " +
                "(select component_state_id from component_state where round_id=?)", roundId);

        executeUpdate(conn, "DELETE FROM compilation where component_state_id in " +
                "(select component_state_id from component_state where round_id=?)", roundId);

        executeUpdate(conn, "DELETE FROM staging_component_state where component_state_id in " +
                "(select component_state_id from component_state where round_id=?)", roundId);

        executeUpdate(conn, "DELETE FROM staging_submission where component_state_id in " +
                "(select component_state_id from component_state where round_id=?)", roundId);

        executeUpdate(conn, "DELETE FROM staging_compilation where component_state_id in " +
                "(select component_state_id from component_state where round_id=?)", roundId);
        */
        executeUpdate(conn, "DELETE FROM component_state WHERE round_id=?", roundId);
        executeUpdate(conn, "DELETE FROM round_component WHERE round_id=?", roundId);
        executeUpdate(conn, "DELETE FROM round_question WHERE round_id=?", roundId);
        executeUpdate(conn, "DELETE FROM survey_question WHERE survey_id=?", roundId);
        executeUpdate(conn, "DELETE FROM survey WHERE survey_id=?", roundId);
        executeUpdate(conn, "DELETE FROM request WHERE round_id=?", roundId);
        executeUpdate(conn, "DELETE FROM room_result WHERE round_id=?", roundId);
        executeUpdate(conn, "DELETE FROM room WHERE round_id=?", roundId);
        executeUpdate(conn, "DELETE FROM round_room_assignment WHERE round_id=?", roundId);
        executeUpdate(conn, "DELETE FROM round WHERE round_id=?", roundId);
    }


    private static final String GET_NUM_ROUNDS_FOR_CONTEST_QUERY = "SELECT COUNT(*) FROM round " +
        " WHERE contest_id=?";

    public int getNumRounds(int contestID) throws SQLException {
        Connection conn = null;
        ResultSet result = null;
        PreparedStatement stmt = null;
        try {
            conn = DBMS.getConnection();
            stmt = conn.prepareStatement(GET_NUM_ROUNDS_FOR_CONTEST_QUERY);
            stmt.setInt(1, contestID);
            result = stmt.executeQuery();
            result.next();
            return result.getInt(1);
        } catch (SQLException e) {
            printException(e);
            throw e;
        } finally {
            close(conn, stmt, result);
        }
    }

    /**
     * <p>
     * This query string is used to select all round and round room assignment information.
     * </p>
     *
     * Modified in Changes in version 1.2:
     * <pre>
     * add auto_end column
     * </pre>
     * Changes in version 1.3:
     * <pre>
     * Add editorial_link column.
     * </pre>
     */
    private static final String GET_ALL_ROUNDS_QUERY =
        "SELECT" +
        "  r.contest_id," +
        "  r.round_id," +
        "  r.name," +
        "  r.round_type_id," +
        "  rt.round_type_desc," +
        "  r.status," +
        "  r.registration_limit," +
        "  r.invitational," +
        "  r.short_name," +
        "  r.region_id," +
        "  reg.region_name," +
        "  r.auto_end," +
        "  r.editorial_link," +
        "  ra.coders_per_room," +
        "  ra.algorithm," +
        "  ra.by_division," +
        "  ra.final," +
        "  ra.by_region," +
        "  ra.p" +
        "  " +
        " FROM (round r " +
        "      LEFT OUTER JOIN round_type_lu rt " +
        "        ON r.round_type_id=rt.round_type_id" +
        "      LEFT OUTER JOIN round_room_assignment ra" +
        "        ON r.round_id=ra.round_id" +
        "      LEFT OUTER JOIN region reg" +
        "        ON r.region_id=reg.region_id)";

    private static final String GET_ROUNDS_FOR_CONTEST_QUERY = GET_ALL_ROUNDS_QUERY +
        " WHERE r.contest_id=?";

    public Collection getRounds(int contestID) throws SQLException {
        Connection conn = null;
        ResultSet result = null;
        PreparedStatement stmt = null;
        try {
            conn = DBMS.getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement(GET_ROUNDS_FOR_CONTEST_QUERY);
            stmt.setInt(1, contestID);
            result = stmt.executeQuery();
            return getRounds(conn, result);
        } catch (SQLException e) {
            printException(e);
            throw e;
        } finally {
            close(conn, stmt, result);
        }
    }

    /**
     * This method was modified for AdminTool 2.0 to include the room assignment
     * data from the database. The method is backwards compatable with rounds
     * that do not have room assignment data. In that case, default room
     * assignment data is created and the user can modify the data using
     * the admin monitor functions.
     * @param conn  our db connection
     * @param result the result to process
     * @return  a collection containing a RoundData object
     * @throws SQLException if error occurred when read data from result
     *
     * Modified in Changes in version 1.2:
     * <pre>
     * add auto_end column
     * </pre>
     * Changes in version 1.3:
     * <pre>
     * Add editorialLink column.
     * </pre>
     */
    private Collection getRounds(Connection conn, ResultSet result) throws SQLException {
        Vector r = new Vector();
        while (result.next()) {
            int index = 1;
            ContestData contest = new ContestData();
            contest.setId(result.getInt(index++));
            Region region = new Region(new Integer(result.getInt(10)),result.getString(11));
            if (result.wasNull()) {
                region.setId(null);
            }
            RoundData data = new RoundData(
                                           contest,
                                           result.getInt(index++),
                                           result.getString(index++),
                                           new RoundType(
                                                         result.getInt(index++),
                                                         result.getString(index++)
                                                         ),
                                           result.getString(index++),
                                           result.getInt(index++),
                                           result.getInt(index++),
                                           result.getString(index++),
                                           region,
                                           result.getBoolean(index += 2),
                                           result.getString(++index)
                                           );
            // grab the round room assignment data from result
            int codersPerRoom = result.getInt(++index);
            int type = result.getInt(++index);
            boolean isDivision = result.getBoolean(++index);
            boolean isFinal = result.getBoolean(++index);
            boolean isRegion = result.getBoolean(++index);
            double p = result.getDouble(++index);
            // this takes care of rounds that don't have assignment data
            // type '0' is invalid so if we find thisvalue, we know that this
            // round must be a round with no room assignment data
            // a new default set of values is created instead
            // and stored int the RoundData
            RoundRoomAssignment rra = null;
            int roundId = data.getId();
            if(type == 0 ) {
                rra = new RoundRoomAssignment(roundId);
            } else {
                rra = new RoundRoomAssignment(roundId, codersPerRoom, type,
                                              isDivision, isFinal, isRegion, p );
            }
            data.setRoomAssignment(rra);
            data.setSegments(getRoundSegments(conn, data.getId()));
            data.setSurvey(getSurvey(conn, data.getId()));
            data.setLanguages(getRoundLanguages(conn, data.getId()));
            data.setEvent(getRoundEvents(conn,data.getId()));

            r.add(data);
        }
        return r;
    }


    /**
     * <p>
     * This query string is modified to select round and round room assignment information by the given round id.
     * </p>
     *
     * Modified in Changes in version 1.2:
     * <pre>
     * add auto_end column
     * </pre>
     * Changes in version 1.3:
     * <pre>
     * Add editorial_link column.
     * </pre>
     */
    private static final String GET_ROUND_QUERY =
        "SELECT" +
        "  r.contest_id," +
        "  r.round_id," +
        "  r.name," +
        "  r.round_type_id," +
        "  rt.round_type_desc," +
        "  r.status," +
        "  r.registration_limit," +
        "  r.invitational," +
        "  ra.short_name," +
        "  r.region_id," +
        "  reg.region_name," +
        "  r.auto_end," +
        "  r.editorial_link," +
        "  ra.coders_per_room," +
        "  ra.algorithm," +
        "  ra.by_division," +
        "  ra.by_region," +
        "  ra.final," +
        "  ra.p" +
        " FROM (round r " +
        "      LEFT OUTER JOIN round_type_lu rt " +
        "        ON r.round_type_id=rt.round_type_id" +
        "      LEFT OUTER JOIN round_room_assignment ra" +
        "        ON r.round_id=ra.round_id" +
        "      LEFT OUTER JOIN region reg" +
        "        ON r.region_id=reg.region_id)" +
        " WHERE r.round_id=?";

    /**
     * This method has been modified to allow for the room assignment data
     * that is now part of the RoundData object.
     *
     * @see RoundRoomAssignment
     * @see RoundData#setRoomAssignment
     *
     * @param roundID the round id
     * @return the round data
     * @throws SQLException if error occurred when read data from result
     *
     * Modified in Changes in version 1.2:
     * <pre>
     * add auto_end column
     * </pre>
     * Changes in version 1.3:
     * <pre>
     * Add editorial_link column
     * </pre>
     */
    public RoundData getRound(int roundID) throws SQLException {
        Connection conn = null;
        ResultSet result = null;
        PreparedStatement stmt = null;
        try {
            conn = DBMS.getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement(GET_ROUND_QUERY);
            stmt.setInt(1, roundID);
            result = stmt.executeQuery();
            if (result.next()) {
                int index = 1;
                ContestData contest = new ContestData();
                Region region = new Region(new Integer(result.getInt(10)),result.getString(11));
                if (result.wasNull()) {
                    region.setId(null);
                }
                contest.setId(
                              result.getInt(index++)
                              );
                RoundData data = new RoundData(
                                               contest,
                                               result.getInt(index++),
                                               result.getString(index++),
                                               new RoundType(
                                                             result.getInt(index++),
                                                             result.getString(index++)
                                                             ),
                                               result.getString(index++),
                                               result.getInt(index++),
                                               result.getInt(index++),
                                               result.getString(index++),
                                               region,
                                               result.getBoolean(index += 2),
                                               result.getString(++index)
                                               );
                // grab the round room assignment data from result
                int codersPerRoom = result.getInt(++index);
                int type = result.getInt(++index);
                boolean isDivision = result.getBoolean(++index);
                boolean isRegion = result.getBoolean(++index);
                boolean isFinal = result.getBoolean(++index);
                double p = result.getDouble(++index);
                // this takes care of rounds that don't have assignment data
                // type '0' is invalid so if we find this value, we know that this
                // round must be a round with no room assignment data
                // a new default set of values is created instead
                // and stored int the RoundData
                RoundRoomAssignment rra = null;
                int roundId = data.getId();
                if(type == 0 ) {
                    rra = new RoundRoomAssignment(roundId);
                } else {
                    rra = new RoundRoomAssignment(roundId, codersPerRoom, type,
                                                  isDivision, isFinal, isRegion, p );
                }
                data.setRoomAssignment(rra);
                data.setSegments(getRoundSegments(conn, data.getId()));
                data.setSurvey(getSurvey(conn, data.getId()));
                data.setLanguages(getRoundLanguages(conn, data.getId()));
                data.setEvent(getRoundEvents(conn,data.getId()));

                populateAdminRoomInfo(conn, data);
                return data;
            } else {
                throw new SQLException("Invalid round ID: " + roundID);
            }
        } catch (SQLException e) {
            printException(e);
            throw e;
        } finally {
            close(conn, stmt, result);
        }
    }


    private static final String GET_ADMIN_ROOM_QUERY =
        "SELECT " +
        "room_id," +
        "name " +
        " FROM room " +
        "WHERE round_id=? AND room_type_id IN (" + ContestConstants.ADMIN_ROOM_TYPE_ID + ", "
        + ContestConstants.TEAM_ADMIN_ROOM_TYPE_ID + ")";

    private void populateAdminRoomInfo(Connection conn, RoundData round) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(GET_ADMIN_ROOM_QUERY);
            ps.setInt(1, round.getId());
            rs = ps.executeQuery();
            if (rs.next()) {
                round.setAdminRoomID(rs.getInt(1));
                round.setAdminRoomName(rs.getString(2));
            }
        } finally {
            close(null, ps, rs);
        }
    }

    /**
     * Gets new ID generated by specififed sequence. Calls <code>
     * DBMS.getSeqID(sequence)</code> method to accomplish the task.
     *
     * @param  sequence an ID of sequence that should be used to generate new
     *         ID
     * @return an int representing the ID that was generated by specified
     *         sequence
     * @throws SQLException
     * @since  Admin Tool 2.0
     */
    public int getNewID(String sequence) throws SQLException {
        return getNewIDFor(sequence);
    }

    private static int getNewIDFor(String sequence) throws SQLException {
        int id = 0;
        try {
            id = IdGeneratorClient.getSeqIdAsInt(sequence);
            if (log.isDebugEnabled())
                log.debug("getNewId for seq = " + sequence + " new id =" + id);
            return id;
        } catch (IDGenerationException e) {
            printException(e);
            throw (SQLException) new SQLException("Could not generated id:" + sequence).initCause(e);
            }
        }


    private static final String GET_ROUND_TYPES_QUERY =
        "SELECT " +
        "round_type_id," +
        "round_type_desc" +
        " FROM round_type_lu";


    public Collection getRoundTypes() throws SQLException {
        Vector r = new Vector();
        Connection conn = null;
        ResultSet result = null;
        PreparedStatement stmt = null;
        try {
            conn = DBMS.getConnection();
            stmt = conn.prepareStatement(GET_ROUND_TYPES_QUERY);
            result = stmt.executeQuery();
            while (result.next()) {
                r.add(new RoundType(result.getInt(1), result.getString(2)));
            }
        } catch (SQLException e) {
            printException(e);
        } finally {
            close(conn, stmt, result);
        }

        return r;
    }

    private static final String GET_SEASONS_QUERY =
        "SELECT " +
        "season_id," +
        "name" +
        " FROM season";

    public Collection getSeasons() throws SQLException {
        Vector r = new Vector();
        Connection conn = null;
        ResultSet result = null;
        PreparedStatement stmt = null;
        try {
            conn = DBMS.getConnection();
            stmt = conn.prepareStatement(GET_SEASONS_QUERY);
            result = stmt.executeQuery();
            while (result.next()) {
                r.add(new Season(new Integer(result.getInt(1)), result.getString(2)));
            }
        } catch (SQLException e) {
            printException(e);
        } finally {
            close(conn, stmt, result);
        }

        return r;
    }

    private static final String GET_REGIONS_QUERY =
        "SELECT " +
        "region_id," +
        "region_name" +
        " FROM region";

    public Collection getRegions() throws SQLException {
        Vector r = new Vector();
        Connection conn = null;
        ResultSet result = null;
        PreparedStatement stmt = null;
        try {
            conn = DBMS.getConnection();
            stmt = conn.prepareStatement(GET_REGIONS_QUERY);
            result = stmt.executeQuery();
            while (result.next()) {
                r.add(new Region(new Integer(result.getInt(1)), result.getString(2)));
            }
        } catch (SQLException e) {
            printException(e);
        } finally {
            close(conn, stmt, result);
        }

        return r;
    }


   private static final String GET_LANGUAGES =
       "SELECT language_id, language_name" +
       " FROM language " +
       " WHERE status='Y'" +
       " ORDER BY language_id";


    public Collection getLanguages() throws SQLException {
       Connection conn = null;
       ResultSet result = null;
       PreparedStatement stmt = null;
       try {
           conn = DBMS.getConnection();
           stmt = conn.prepareStatement(GET_LANGUAGES);
           result = stmt.executeQuery();
           Vector r = new Vector();
           while (result.next()) {
               r.add(new Language(result.getInt(1), result.getString(2)));
           }
           return r;
       } catch (SQLException e) {
           printException(e);
           throw e;
       } finally {
           close(conn, stmt, result);
       }
    }

    private static final String CREATE_ADMIN_ROOM_QUERY =
        "INSERT INTO room (" +
        "room_id," +
        "round_id," +
        "name," +
        "division_id," +
        "room_type_id) " +
        "VALUES (?,?,'Admin Room',-1,?)";

    private static final String CREATE_PRACTICE_ROOM_QUERY =
        "INSERT INTO room (" +
        "room_id," +
        "round_id," +
        "name," +
        "division_id," +
        "room_type_id) " +
        "VALUES (?,?, ?, 1, ?)";// + ContestConstants.PRACTICE_CODER_ROOM + ")";

    public void addRound(RoundData round) throws SQLException {
        Connection conn = null;
        try {
            conn = DBMS.getConnection();
            conn.setAutoCommit(false);
            insertRound(conn, round);
            updateRound(conn, round);
            int roundTypeId = round.getType().getId();
            com.topcoder.netCommon.contest.round.RoundType roundType = com.topcoder.netCommon.contest.round.RoundType.get(roundTypeId);

            if (roundTypeId == ContestConstants.MODERATED_CHAT_ROUND_TYPE_ID) {
                createModeratedChatRoom(conn, round);
                // Create practice room or admin room, depending on contest type. One coder room is created
                // for long contests.
            } else if (roundTypeId == ContestConstants.PRACTICE_ROUND_TYPE_ID ||
                       roundTypeId == ContestConstants.TEAM_PRACTICE_ROUND_TYPE_ID ||
                       roundTypeId == ContestConstants.LONG_PROBLEM_PRACTICE_ROUND_TYPE_ID ||
                       roundTypeId == ContestConstants.AMD_LONG_PROBLEM_PRACTICE_ROUND_TYPE_ID) {
                Object[] param = new Object[]{
                    new Integer(round.getId()),
                            new Integer(round.getId()),
                            round.getName(),
                            new Integer(roundType.isTeamRound() ? ContestConstants.TEAM_PRACTICE_ROOM_TYPE_ID : ContestConstants.PRACTICE_ROOM_TYPE_ID)};

                executeUpdate(conn, CREATE_PRACTICE_ROOM_QUERY, param);
            } else {
                Object[] param = new Object[]{
                    new Integer(getNewID(DBMS.ROOM_SEQ)),
                            new Integer(round.getId()),
                            new Integer(roundType.isTeamRound() ?
                                ContestConstants.TEAM_ADMIN_ROOM_TYPE_ID : ContestConstants.ADMIN_ROOM_TYPE_ID)};
                executeUpdate(conn, CREATE_ADMIN_ROOM_QUERY, param);

                if (roundType.isLongRound()) {
                    Object[] param1 = new Object[]{
                            new Integer(round.getId()),
                            new Integer(round.getId()),
                            "Room 1",
                            new Integer(roundType.isTeamRound() ?
                                    ContestConstants.TEAM_CONTEST_ROOM_TYPE_ID : ContestConstants.CONTEST_ROOM_TYPE_ID)};
                    executeUpdate(conn, CREATE_DIV1_ROOM_QUERY, param1);
                }
            }
            conn.commit();
        } catch (SQLException e) {
            rollback(conn);
            printException(e);
            throw e;
        } finally {
            close(conn, null, null);
        }
    }

    private static void createModeratedChatRoom(Connection conn, RoundData round) throws SQLException {
        createRoom(conn, round.getId(), "Moderated Chat Room", ServerContestConstants.MODERATED_CHAT_ROOM_TYPE_ID);
    }

    private static final String CREATE_DIV1_ROOM_QUERY =
            "INSERT INTO room (room_id, round_id, name, division_id, room_type_id) " +
            "VALUES (      ?,        ?,    ?,          1,            ?)";

    private static final String CREATE_ROOM_QUERY =
        "INSERT INTO room (room_id, round_id, name, division_id, room_type_id) " +
        "VALUES (      ?,        ?,    ?,          -1,            ?)";

    private static void createRoom(Connection conn, int roundId, String roomName, int roomTypeId) throws SQLException {
        Object[] param = {
            new Integer(getNewIDFor(DBMS.ROOM_SEQ)),
            new Integer(roundId),
            roomName,
            new Integer(roomTypeId),
        };
        executeUpdate(conn, CREATE_ROOM_QUERY, param);
    }

    public void modifyRound(int oldRoundID, RoundData newRound) throws SQLException {
        Connection conn = null;
        try {
            conn = DBMS.getConnection();
            conn.setAutoCommit(false);
            if (oldRoundID != newRound.getId()) {
                insertRound(conn, newRound);
                updateRoundId(conn, new Integer(oldRoundID), new Integer(newRound.getId()));
                deleteRound(conn, oldRoundID);
            }
            updateRound(conn, newRound);
            conn.commit();
        } catch (SQLException e) {
            rollback(conn);
            printException(e);
            throw e;
        } finally {
            close(conn, null, null);
        }
    }


    private void updateRoundId(Connection conn, Integer oldId, Integer newId) throws SQLException {
        Object roundId[] = new Object[]{newId, oldId};
        executeUpdate(conn, "UPDATE round_registration set round_id=? WHERE round_id=?", roundId);
        executeUpdate(conn, "UPDATE room set round_id=? WHERE round_id=?", roundId);
        executeUpdate(conn, "UPDATE round_room_assignment set round_id=? WHERE round_id=?", roundId);
        executeUpdate(conn, "UPDATE room_result set round_id=? WHERE round_id=?", roundId);
        executeUpdate(conn, "UPDATE component_state set round_id=? WHERE round_id=?", roundId);
        executeUpdate(conn, "UPDATE system_test_result set round_id=? WHERE round_id=?", roundId);
        executeUpdate(conn, "UPDATE challenge set round_id=? WHERE round_id=?", roundId);
    }

    private void insertRound(Connection conn, RoundData round) throws SQLException {
        String stmt = "INSERT INTO round (round_id, name) VALUES (?,?)";
        executeUpdate(conn, stmt, new Object[]{new Integer(round.getId()), round.getName()});
        stmt = "INSERT INTO round_room_assignment (round_id) VALUES (?)";
        executeUpdate(conn, stmt, new Object[]{new Integer(round.getId())});
    }

    /**
     * The update round statement.
     *
     * Modified in Changes in version 1.2:
     * <pre>
     * add auto_end column
     * </pre>
     * Changes in version 1.3:
     * Add editorial_link column.
     */
    private static final String UPDATE_ROUND_QUERY =
        "UPDATE round SET " +
        "contest_id=?," +
        "name=?," +
        "round_type_id=?," +
        "status=?," +
        "registration_limit=?," +
        "invitational=?," +
        "short_name=?," +
        "region_id=?," +
        "auto_end=?," +
        "editorial_link=?" +
        " WHERE round_id=?";

    /**
     * This method has been be modified to include update of row in newly
     * defined "round_room_assignment" table corresponding to round with ID
     * taken from specified RoundData.
     *
     * @see AdminServicesBean#UPDATE_ROOM_ASSIGNMENT_QUERY
     * @see RoundData#getRoomAssignment
     * @see RoundRoomAssignment
     *
     * @param conn the Connection object
     * @param round the round data
     * @throws SQLException if error occurred when update the record
     *
     * Modified in Changes in version 1.2:
     * <pre>
     * add auto_end column
     * </pre>
     * Changes in version 1.3:
     * <pre>
     * add editorial_link column
     * </pre
     */
    private void updateRound(Connection conn, RoundData round) throws SQLException {
        Object[] params = new Object[]{
            new Integer(round.getContest().getId()),
            round.getName(),
            new Integer(round.getType().getId()),
            round.getStatus(),
            new Integer(round.getRegistrationLimit()),
            new Integer(round.getInvitationType()),
            round.getShortName(),
            round.getRegion().getId(),
            round.isAutoEnd(),
            round.getEditorialLink(),
            new Integer(round.getId())
        };
        executeUpdate(conn, UPDATE_ROUND_QUERY, params);
        saveRoundRoomAssignment(conn, round.getRoomAssignment(), false);
    }


    public void deleteRound(int roundID) throws SQLException {
        Connection conn = null;
        try {
            conn = DBMS.getConnection();
            conn.setAutoCommit(false);
            deleteRound(conn, roundID);
            conn.commit();
        } catch (SQLException e) {
            rollback(conn);
            printException(e);
            throw e;
        } finally {
            close(conn, null, null);
        }
    }

    private static final String GET_ROUND_SEGMENTS_QUERY =
        "SELECT segment_id, start_time, end_time, status FROM round_segment WHERE round_id=?";

    private static RoundSegmentData getRoundSegments(Connection conn, int roundID) throws SQLException {
        ResultSet result = null;
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(GET_ROUND_SEGMENTS_QUERY);
            stmt.setInt(1, roundID);
            result = stmt.executeQuery();
            RoundSegmentData r = new RoundSegmentData(roundID);
            while (result.next()) {
                int segmentId = result.getInt(1);
                Date date = getDate(result, 2);
                long start = date == null ? 0 : date.getTime();
                date = getDate(result, 3);
                long end = date == null ? 0 : date.getTime();
                int duration = (int) ((end - start) / 60000);
                String status = result.getString(4);
                switch (segmentId) {
                case 1:
                    r.setRegistrationStart(new Date(start));
                    r.setRegistrationLength(duration);
                    r.setRegistrationStatus(status);
                    break;
                case 2:
                    r.setCodingStart(new Date(start));
                    r.setCodingLength(duration);
                    r.setCodingStatus(status);
                    break;
                case 3:
                    r.setIntermissionLength(duration);
                    r.setIntermissionStatus(status);
                    break;
                case 4:
                    r.setChallengeLength(duration);
                    r.setChallengeStatus(status);
                    break;
                case 5:
                    r.setSystemTestStatus(status);
                    break;
                }
            }

            return r;
        } catch (SQLException e) {
            printException(e);
            throw e;
        } finally {
            close(null, stmt, result);
        }
    }


    private static final String GET_ROUND_LANGUAGES_QUERY =
        "SELECT l.language_id, l.language_name" +
        " FROM round_language rl, language l " +
        " WHERE rl.round_id=? AND l.language_id = rl.language_id";

    private static RoundLanguageData getRoundLanguages(Connection conn, int roundID) throws SQLException {
        ResultSet result = null;
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(GET_ROUND_LANGUAGES_QUERY);
            stmt.setInt(1, roundID);
            result = stmt.executeQuery();
            RoundLanguageData l = new RoundLanguageData();
            if (result.next()) {
                ArrayList languages = new ArrayList();
                do {
                    languages.add(new Language(result.getInt(1), result.getString(2)));
                } while (result.next());
                l.setLanguages(languages);
            } else {
                l.setUseDefaultLanguages();
            }
            return l;
        } catch (SQLException e) {
            printException(e);
            throw e;
        } finally {
            close(null, stmt, result);
        }
    }

    private static final String GET_ROUND_EVENT_QUERY =
        "SELECT re.event_id,re.event_name,re.registration_url" +
        " FROM round_event re WHERE re.round_id=?";
    /**
     * <p>
     * get the round events.
     * </p>
     * @param conn
     *        the db connection.
     * @param roundID
     *        the round id.
     * @return the round event data.
     * @throws SQLException
     *           if any sql error occurs.
     */
    public static RoundEventData getRoundEvents(Connection conn, int roundID) throws SQLException {
        ResultSet result = null;
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(GET_ROUND_EVENT_QUERY);
            stmt.setInt(1, roundID);
            result = stmt.executeQuery();
            RoundEventData re = new RoundEventData(roundID);
            if (result.next()) {
                re.setEventId(result.getInt(1));
                re.setEventName(result.getString(2));
                re.setRegistrationUrl(result.getString(3));
            }
            return re;
        } catch (SQLException e) {
            printException(e);
            throw e;
        } finally {
            close(null, stmt, result);
        }
    }

    private static final String CLEAR_ROUND_LANGUAGES_QUERY =
        "DELETE FROM round_language WHERE round_id=?";

    private static final String INSERT_ROUND_LANGUAGES_QUERY =
        "INSERT INTO round_language (round_id, language_id) VALUES (?,?)";

    public void setRoundLanguages(RoundLanguageData languages) throws SQLException {
        Connection conn = null;
        ResultSet result = null;
        try {
            conn = DBMS.getConnection();
            conn.setAutoCommit(false);
            Integer roundId = new Integer(languages.getRoundId());
            Object params[] = new Object[]{roundId};
            executeUpdate(conn, CLEAR_ROUND_LANGUAGES_QUERY, params);

            if (!languages.isUseDefaultLanguages()) {
                Set ls = languages.getLanguages();
                for (Iterator it = ls.iterator(); it.hasNext();) {
                    Language l = (Language) it.next();
                    params = new Object[] {roundId, new Integer(l.getId())};
                    executeUpdate(conn, INSERT_ROUND_LANGUAGES_QUERY, params);
                }
            }
            conn.commit();
        } catch (SQLException e) {
            rollback(conn);
            printException(e);
            throw e;
        } finally {
            close(conn, null, result);
        }
    }

    /**
     * <p>
     * save the round event data to database.
     * </p>
     *
     * @param eventData
     *        the round event data.
     * @throws SQLException
     *          if the database related error occurs.
     */
    public void setRoundEvents(RoundEventData eventData) throws SQLException {
        Connection conn = null;
        ResultSet result = null;
        try {
            if (eventData != null && eventData.getEventId() > 0) {
                conn = DBMS.getConnection();
                conn.setAutoCommit(false);
                Integer roundId = new Integer(eventData.getRoundId());
                Object params[] = new Object[] { roundId };
                executeUpdate(conn, CLEAR_ROUND_EVENTS_QUERY, params);

                params = new Object[] { roundId, eventData.getEventId(), eventData.getEventName(),
                    eventData.getRegistrationUrl() };
                executeUpdate(conn, INSERT_ROUND_EVENTS_QUERY, params);

                conn.commit();
            }
        } catch (SQLException e) {
            rollback(conn);
            printException(e);
            throw e;
        } finally {
            close(conn, null, result);
        }
    }

    private static final Integer REGISTRATION_PHASE =
        new Integer(ServerContestConstants.REGISTRATION_SEGMENT_ID);
    private static final Integer ROOM_ASSIGNMENT_PHASE =
        new Integer(ServerContestConstants.ROOM_ASSIGNMENT_SEGMENT_ID);
    private static final Integer CODING_PHASE =
        new Integer(ServerContestConstants.CODING_SEGMENT_ID);
    private static final Integer INTERMISSION_PHASE =
        new Integer(ServerContestConstants.INTERMISSION_SEGMENT_ID);
    private static final Integer CHALLENGE_PHASE =
        new Integer(ServerContestConstants.CHALLENGE_SEGMENT_ID);
    private static final Integer SYSTEM_TEST_PHASE =
        new Integer(ServerContestConstants.SYSTEM_TEST_SEGMENT_ID);

    private static final String INSERT_ROUND_SEGMENT_QUERY =
        "INSERT INTO round_segment (" +
        "round_id," +
        "segment_id," +
        "start_time," +
        "end_time," +
        "status " +
        ") VALUES (?,?,?,?,?)";

    private static final String CLEAR_ROUND_SEGMENTS_QUERY =
        "DELETE FROM round_segment WHERE round_id=?";


    public void setRoundSegments(RoundSegmentData segments) throws SQLException {
        Connection conn = null;
        ResultSet result = null;
        try {
            conn = DBMS.getConnection();
            conn.setAutoCommit(false);
            Integer roundId = new Integer(segments.getRoundId());
            Object params[] = new Object[]{roundId};
            executeUpdate(conn, CLEAR_ROUND_SEGMENTS_QUERY, params);

            long regStart = segments.getRegistrationStart().getTime();
            long regEnd = regStart + (60000L * segments.getRegistrationLength());
            long assignStart = regEnd + 60000;
            long assignEnd = segments.getCodingStart().getTime();
            long codingStart = segments.getCodingStart().getTime();
            long codingEnd = codingStart + (60000L * segments.getCodingLength());
            long intermissionStart = codingEnd;
            long intermissionEnd = intermissionStart + (60000L * segments.getIntermissionLength());
            long challengeStart = intermissionEnd;
            long challengeEnd = challengeStart + (60000L * segments.getChallengeLength());
            long systestStart = challengeEnd;
            long systestEnd = systestStart;

            String sql = INSERT_ROUND_SEGMENT_QUERY;
            params = new Object[]{
                roundId,
                REGISTRATION_PHASE,
                new Timestamp(regStart),
                new Timestamp(regEnd),
                segments.getRegistrationStatus()
            };
            executeUpdate(conn, sql, params);
            int index = 1;
            params[index++] = ROOM_ASSIGNMENT_PHASE;
            params[index++] = new Timestamp(assignStart);
            params[index++] = new Timestamp(assignEnd);
            params[index++] = segments.getRegistrationStatus();
            executeUpdate(conn, sql, params);
            index = 1;
            params[index++] = CODING_PHASE;
            params[index++] = new Timestamp(codingStart);
            params[index++] = new Timestamp(codingEnd);
            params[index++] = segments.getCodingStatus();
            executeUpdate(conn, sql, params);
            index = 1;
            params[index++] = INTERMISSION_PHASE;
            params[index++] = new Timestamp(intermissionStart);
            params[index++] = new Timestamp(intermissionEnd);
            params[index++] = segments.getIntermissionStatus();
            executeUpdate(conn, sql, params);
            index = 1;
            params[index++] = CHALLENGE_PHASE;
            params[index++] = new Timestamp(challengeStart);
            params[index++] = new Timestamp(challengeEnd);
            params[index++] = segments.getChallengeStatus();
            executeUpdate(conn, sql, params);
            index = 1;
            params[index++] = SYSTEM_TEST_PHASE;
            params[index++] = new Timestamp(systestStart);
            params[index++] = new Timestamp(systestEnd);
            params[index++] = segments.getSystemTestStatus();
            executeUpdate(conn, sql, params);
            conn.commit();
        } catch (SQLException e) {
            rollback(conn);
            printException(e);
            throw e;
        } finally {
            close(conn, null, result);
        }
    }

    private Map componentParametersMap(ResultSet params) throws SQLException {
        Map paramsMap = new HashMap();

        // params will be provided in sort_order already, so just assemble into ArrayLists
        while (params.next()) {
            Integer key = new Integer(params.getInt(1));
            ArrayList types = null;

            if (paramsMap.containsKey(key)) {
                types = (ArrayList) paramsMap.get(key);
            } else {
                types = new ArrayList();
            }

            types.add(params.getString(2));
            paramsMap.put(key, types);
        }

        return paramsMap;
    }

    private static final String GET_PROBLEM_COMPONENT_PARAM_TYPES = "SELECT " +
        "c.component_id," +
        "dt.data_type_desc," +
        "pa.sort_order " +
        "FROM component c " +
        "INNER JOIN problem p ON p.problem_id = c.problem_id " +
        "LEFT OUTER JOIN parameter pa ON pa.component_id = c.component_id " +
        "LEFT OUTER JOIN data_type dt ON pa.data_type_id = dt.data_type_id " +
        "WHERE p.problem_id=? ORDER BY c.component_id, pa.sort_order";

    private Map problemComponentParameters(Connection conn, int problemID) throws SQLException {
        ResultSet params = null;
        PreparedStatement stmt = null;
        Map paramTypes = null;

        try {
            stmt = conn.prepareStatement(GET_PROBLEM_COMPONENT_PARAM_TYPES);
            stmt.setInt(1, problemID);
            params = stmt.executeQuery();

            paramTypes = componentParametersMap(params);

            return paramTypes;
        } catch (SQLException e) {
            printException(e);
            throw e;
        } finally {
            close(null, stmt, params);
        }
    }


    /**
     * Query to retrieve problem components and their attributes for specific round id
     */
    private static final String GET_ROUND_PROBLEM_COMPONENTS_GLOBAL_QUERY = "SELECT " +
        "c.problem_id, " +
        "c.component_id," +
        "c.class_name," +
        "c.method_name," +
        "dt.data_type_desc," +
        "c.component_type_id," +
        "ct.component_type_desc," +
        "rc.points," +
        "rc.division_id," +
        "div.division_desc," +
        "rc.difficulty_id," +
        "diff.difficulty_desc," +
        "rc.open_order," +
        "rc.submit_order " +
        "FROM (component c INNER JOIN problem p ON p.problem_id = c.problem_id " +
        " LEFT OUTER JOIN data_type dt ON c.result_type_id=dt.data_type_id " +
        " LEFT OUTER JOIN round_component rc ON c.component_id=rc.component_id " +
        " LEFT OUTER JOIN division div ON rc.division_id=div.division_id " +
        " LEFT OUTER JOIN difficulty diff ON rc.difficulty_id=diff.difficulty_id) " +
        " LEFT OUTER JOIN component_type_lu ct ON ct.component_type_id = c.component_type_id " +
        " WHERE rc.round_id=?";

    /**
     * Select a list of components of all problems of a specific round.
     *
     * @param roundID round id to get components for
     * @return a Collection of RoundComponentData
     * @throws SQLException
     * @see AdminServicesBean#getRoundProblemComponents(int roundID, int problemID, int divisionID)
     */
    public Collection getRoundProblemComponents(int roundID) throws SQLException {
        Connection conn = null;
        ResultSet result = null;
        PreparedStatement stmt = null;
        Collection assigned = new Vector();
        Map params = null;
        try {
            conn = DBMS.getConnection();
            stmt = conn.prepareStatement(GET_ROUND_PROBLEM_COMPONENTS_GLOBAL_QUERY);
            stmt.setInt(1, roundID);
            result = stmt.executeQuery();
            while (result.next()) {
                int index = 1;
                int problemID = result.getInt(index++);
                int componentID = result.getInt(index++);
                String className = result.getString(index++);
                String methodName = result.getString(index++);

                params = problemComponentParameters(conn, problemID);
                List paramTypes = (List) params.get(new Integer(componentID));

                String resultType = result.getString(index++);
                ComponentData componentData = new ComponentData(
                                                                componentID,
                                                                problemID,
                                                                className,
                                                                methodName,
                                                                resultType,
                                                                paramTypes,
                                                                new ComponentType(result.getInt(index++),
                                                                                  result.getString(index++))
                                                                );
                assigned.add(new RoundComponentData(
                                                    componentData,
                                                    result.getDouble(index++),
                                                    new Division(
                                                                 result.getInt(index++),
                                                                 result.getString(index++)
                                                                 ),
                                                    new Difficulty(
                                                                   result.getInt(index++),
                                                                   result.getString(index++)
                                                                   ),
                                                    result.getInt(index++),
                                                    result.getInt(index++)
                                                    ));
            }
            return assigned;
        } catch (SQLException e) {
            printException(e);
            throw e;
        } finally {
            close(conn, stmt, result);
        }
    }

    /**
     * This SQL is very subtle.  The rc.round_id=? in the ON of the LEFT OUTER JOIN cannot go into the WHERE clause.
     *
     * What I'm trying to do here is select all components for the given problem, and, if the component has been
     * assigned to this round, optionally grab all the round_component info as well, otherwise grab nulls.  If the
     * round_id and division_id check isn't in the ON, and the component is assigned to another round or to the other
     * division, then the join will accept the other round's data from round_component, then reject all retreived rows
     * in the WHERE clause, and not just return null values for the round_component columns.
     */
    private static final String GET_ROUND_PROBLEM_COMPONENTS_QUERY = "SELECT " +
        "c.component_id," +
        "c.class_name," +
        "c.method_name," +
        "dt.data_type_desc," +
        "c.component_type_id," +
        "ct.component_type_desc," +
        "rc.points," +
        "rc.division_id," +
        "div.division_desc," +
        "rc.difficulty_id," +
        "diff.difficulty_desc," +
        "rc.open_order," +
        "rc.submit_order " +
        "FROM (component c INNER JOIN problem p ON p.problem_id = c.problem_id " +
        " LEFT OUTER JOIN data_type dt ON c.result_type_id=dt.data_type_id " +
        " LEFT OUTER JOIN round_component rc ON c.component_id=rc.component_id AND rc.round_id=? AND rc.division_id=? " +
        " LEFT OUTER JOIN division div ON rc.division_id=div.division_id " +
        " LEFT OUTER JOIN difficulty diff ON rc.difficulty_id=diff.difficulty_id) " +
        " LEFT OUTER JOIN component_type_lu ct ON ct.component_type_id = c.component_type_id " +
        "WHERE p.problem_id=? ";


    public Collection getRoundProblemComponents(int roundID, int problemID, int divisionID) throws SQLException {
        Connection conn = null;
        ResultSet result = null;
        PreparedStatement stmt = null;
        Collection assigned = new Vector();
        Map params = null;
        try {
            conn = DBMS.getConnection();
            stmt = conn.prepareStatement(GET_ROUND_PROBLEM_COMPONENTS_QUERY);
            stmt.setInt(1, roundID);
            stmt.setInt(2, divisionID);
            stmt.setInt(3, problemID);
            result = stmt.executeQuery();
            params = problemComponentParameters(conn, problemID);

            while (result.next()) {
                int index = 1;
                int componentID = result.getInt(index++);
                String className = result.getString(index++);
                String methodName = result.getString(index++);

                List paramTypes = (List) params.get(new Integer(componentID));

                String resultType = result.getString(index++);
                ComponentData componentData = new ComponentData(
                                                                componentID,
                                                                problemID,
                                                                className,
                                                                methodName,
                                                                resultType,
                                                                paramTypes,
                                                                new ComponentType(result.getInt(index++),
                                                                                  result.getString(index++))
                                                                );
                assigned.add(new RoundComponentData(
                                                    componentData,
                                                    result.getDouble(index++),
                                                    new Division(
                                                                 result.getInt(index++),
                                                                 result.getString(index++)
                                                                 ),
                                                    new Difficulty(
                                                                   result.getInt(index++),
                                                                   result.getString(index++)
                                                                   ),
                                                    result.getInt(index++),
                                                    result.getInt(index++)
                                                    ));
            }
            return assigned;
        } catch (SQLException e) {
            printException(e);
            throw e;
        } finally {
            close(conn, stmt, result);
        }
    }

    private static final String GET_AVAILABLE_PROBLEMS_QUERY = "SELECT " +
        "p.problem_id," +
        "p.name," +
        "p.status_id," +
        "s.status_desc, " +
        "p.problem_type_id, " +
        "t.problem_type_desc " +
        " FROM problem p " +
        " LEFT OUTER JOIN problem_status_lu s ON p.status_id=s.problem_status_id " +
        " LEFT OUTER JOIN problem_type_lu t ON p.problem_type_id = t.problem_type_id ";

    public Collection getProblems() throws SQLException {
        Connection conn = null;
        ResultSet result = null;
        Collection available = new Vector();
        PreparedStatement stmt = null;
        try {
            conn = DBMS.getConnection();
            stmt = conn.prepareStatement(GET_AVAILABLE_PROBLEMS_QUERY);
            result = stmt.executeQuery();

            while (result.next()) {
                int index = 1;
                int problemID = result.getInt(index++);
                String problemName = result.getString(index++);
                int problemStatusID = result.getInt(index++);
                String problemStatusDesc = result.getString(index++);
                int problemTypeID = result.getInt(index++);
                String problemTypeDesc = result.getString(index++);
                available.add(new ProblemData(
                                              problemID,
                                              problemName,
                                              new ProblemType(
                                                              problemTypeID,
                                                              problemTypeDesc
                                                              ),
                                              new ProblemStatus(
                                                                problemStatusID,
                                                                problemStatusDesc
                                                                )
                                              ));
            }
            return available;
        } catch (SQLException e) {
            printException(e);
            throw e;
        } finally {
            close(conn, stmt, result);
        }
    }

    private static final String GET_ASSIGNED_PROBLEMS_QUERY = "SELECT DISTINCT " +
        "p.problem_id," +
        "p.name," +
        "p.status_id," +
        "s.status_desc, " +
        "p.problem_type_id, " +
        "t.problem_type_desc, " +
        "d.division_id, " +
        "d.division_desc " +
        " FROM problem p " +
        " INNER JOIN component c ON p.problem_id = c.problem_id " +
        " INNER JOIN round_component rc ON rc.component_id = c.component_id " +
        " INNER JOIN division d ON rc.division_id = d.division_id " +
        " LEFT OUTER JOIN problem_status_lu s ON p.status_id=s.problem_status_id " +
        " LEFT OUTER JOIN problem_type_lu t ON p.problem_type_id = t.problem_type_id " +
        " WHERE round_id=?";

    public Collection getAssignedProblems(int roundID) throws SQLException {
        Connection conn = null;
        ResultSet result = null;
        Collection assigned = new Vector();
        PreparedStatement stmt = null;
        try {
            conn = DBMS.getConnection();
            stmt = conn.prepareStatement(GET_ASSIGNED_PROBLEMS_QUERY);
            stmt.setInt(1, roundID);
            result = stmt.executeQuery();
            while (result.next()) {
                int index = 1;
                int problemID = result.getInt(index++);
                String problemName = result.getString(index++);
                int problemStatusID = result.getInt(index++);
                String problemStatusDesc = result.getString(index++);
                int problemTypeID = result.getInt(index++);
                String problemTypeDesc = result.getString(index++);
                assigned.add(new RoundProblemData(new ProblemData(
                                                                  problemID,
                                                                  problemName,
                                                                  new ProblemType(
                                                                                  problemTypeID,
                                                                                  problemTypeDesc
                                                                                  ),
                                                                  new ProblemStatus(
                                                                                    problemStatusID,
                                                                                    problemStatusDesc
                                                                                    )
                                                                  ), new Division(result.getInt(index++), result.getString(index++))));
            }
            return assigned;
        } catch (SQLException e) {
            printException(e);
            throw e;
        } finally {
            close(conn, stmt, result);
        }
    }


    private static final String CLEAR_COMPONENTS_QUERY = "DELETE FROM round_component WHERE round_id=?";
    private static final String INSERT_COMPONENTS_QUERY = "INSERT INTO round_component (" +
        "round_id," +
        "component_id," +
        "points," +
        "division_id," +
        "difficulty_id," +
        "open_order," +
        "submit_order " +
        ") VALUES (?,?,?,?,?,?,?)";

    public void setComponents(int roundID, Collection components) throws SQLException {
        Connection conn = null;
        if (log.isDebugEnabled()) {
            log.debug("Setting " + components.size() + " components for round #" + roundID);
        }
        try {
            Object[] params;
            conn = DBMS.getConnection();
            conn.setAutoCommit(false);
            Integer roundId = new Integer(roundID);
            params = new Object[]{roundId};
            executeUpdate(conn, CLEAR_COMPONENTS_QUERY, params);
            params = new Object[7];
            for (Iterator it = components.iterator(); it.hasNext();) {
                RoundComponentData roundComponentData = (RoundComponentData) it.next();
                if (log.isDebugEnabled()) {
                    debug("Assigning component " + roundComponentData + " to round #" + roundID);
                }
                params[0] = roundId;
                params[1] = new Integer(roundComponentData.getComponentData().getId());
                params[2] = new Double(roundComponentData.getPointValue());
                params[3] = new Integer(roundComponentData.getDivision().getId());
                params[4] = new Integer(roundComponentData.getDifficulty().getId());
                params[5] = new Integer(roundComponentData.getOpenOrder());
                params[6] = new Integer(roundComponentData.getSubmitOrder());
                executeUpdate(conn, INSERT_COMPONENTS_QUERY, params);
            }
            conn.commit();
        } catch (SQLException e) {
            rollback(conn);
            printException(e);
            throw e;
        } finally {
            close(conn, null, null);
        }
    }

    private static final String GET_PROBLEM_STATUS_TYPES_QUERY =
        "SELECT " +
        "problem_status_id," +
        "status_desc" +
        " FROM problem_status_lu";


    public Collection getProblemStatusTypes() throws SQLException {
        Connection conn = null;
        ResultSet result = null;
        PreparedStatement stmt = null;
        try {
            conn = DBMS.getConnection();
            stmt = conn.prepareStatement(GET_PROBLEM_STATUS_TYPES_QUERY);
            result = stmt.executeQuery();
            Vector r = new Vector();
            while (result.next()) {
                r.add(new ProblemStatus(
                                        result.getInt(1),
                                        result.getString(2))
                      );
            }
            return r;
        } catch (SQLException e) {
            printException(e);
            throw e;
        } finally {
            close(conn, null, null);
        }
    }

    private static final String GET_DIFFICULTY_LEVELS_QUERY =
        "SELECT " +
        "difficulty_id," +
        "difficulty_desc" +
        " FROM difficulty";


    public Collection getDifficultyLevels() throws SQLException {
        Connection conn = null;
        ResultSet result = null;
        PreparedStatement stmt = null;
        try {
            conn = DBMS.getConnection();
            stmt = conn.prepareStatement(GET_DIFFICULTY_LEVELS_QUERY);
            result = stmt.executeQuery();
            Vector r = new Vector();
            while (result.next()) {
                r.add(new Difficulty(
                                     result.getInt(1),
                                     result.getString(2))
                      );
            }
            return r;
        } catch (SQLException e) {
            printException(e);
            throw e;
        } finally {
            close(conn, stmt, result);
        }
    }


    private static final String GET_DIVISIONS_QUERY =
        "SELECT " +
        "division_id," +
        "division_desc" +
        " FROM division WHERE division_id>0";


    public Collection getDivisions() throws SQLException {
        Connection conn = null;
        ResultSet result = null;
        PreparedStatement stmt = null;
        try {
            conn = DBMS.getConnection();
            stmt = conn.prepareStatement(GET_DIVISIONS_QUERY);
            result = stmt.executeQuery();
            Vector r = new Vector();
            while (result.next()) {
                r.add(new Division(
                                   result.getInt(1),
                                   result.getString(2))
                      );
            }
            return r;
        } catch (SQLException e) {
            printException(e);
            throw e;
        } finally {
            close(conn, stmt, result);
        }
    }


    private static final String GET_SURVEY_STATUS_TYPES_QUERY =
        "SELECT " +
        "status_id," +
        "status_desc" +
        " FROM status_lu WHERE status_type_id=5";


    public Collection getSurveyStatusTypes() throws SQLException {
        Connection conn = null;
        ResultSet result = null;
        PreparedStatement stmt = null;
        try {
            conn = DBMS.getConnection();
            stmt = conn.prepareStatement(GET_SURVEY_STATUS_TYPES_QUERY);
            result = stmt.executeQuery();
            Vector r = new Vector();
            while (result.next()) {
                r.add(new SurveyStatus(
                                       result.getInt(1),
                                       result.getString(2))
                      );
            }
            return r;
        } catch (SQLException e) {
            printException(e);
            throw e;
        } finally {
            close(conn, stmt, result);
        }
    }


    private static final String GET_SURVEY_QUERY =
        "SELECT " +
        "survey.name," +
        "survey.start_date," +
        "survey.end_date," +
        "survey.text," +
        "survey.status_id," +
        "status_lu.status_desc " +
        "FROM survey LEFT OUTER JOIN status_lu ON survey.status_id=status_lu.status_id " +
        "WHERE survey.survey_id=?";

    private SurveyData getSurvey(Connection conn, int roundID) throws SQLException {
        ResultSet result = null;
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(GET_SURVEY_QUERY);
            stmt.setInt(1, roundID);
            result = stmt.executeQuery();
            if (result.next()) {
                int index = 1;
                String name = result.getString(index++);
                Date date = getDate(result, index++);
                long start = date == null ? 0 : date.getTime();
                date = getDate(result, index++);
                long end = date == null ? 0 : date.getTime();
                return new SurveyData(
                                      roundID,
                                      name,
                                      result.getString(index++),
                                      new Date(start),
                                      (int) ((end - start) / 60000),
                                      new SurveyStatus(
                                                       result.getInt(index++),
                                                       result.getString(index++)
                                                       )
                                      );
            } else {
                return new SurveyData(roundID);
            }
        } catch (SQLException e) {
            printException(e);
            throw e;
        } finally {
            close(null, stmt, result);
        }
    }


    private static final String INSERT_SURVEY_QUERY =
        "INSERT INTO survey (" +
        "survey_id," +
        "name," +
        "status_id," +
        "start_date," +
        "end_date " +
        ") SELECT " +
        "r.round_id," +
        "c.name || ' ' || r.name || ' survey'," +
        "0," +
        "c.start_date," +
        "c.end_date " +
        "FROM contest c JOIN round r ON r.contest_id = c.contest_id WHERE r.round_id = ?";

    private void createSurveyIfNotExists(Connection conn, int roundID) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("SELECT COUNT(*) FROM survey WHERE survey_id = ?");
            ps.setInt(1, roundID);
            rs = ps.executeQuery();
            rs.next();
            if (rs.getInt(1) == 0) {
                Object[] param = new Object[]{
                    new Integer(roundID)
                };
                executeUpdate(conn, INSERT_SURVEY_QUERY, param);
            }
        } finally {
            close(null, ps, rs);
        }
    }

    private static final String UPDATE_SURVEY_QUERY =
        "UPDATE survey SET " +
        "name=?," +
        "start_date=?," +
        "end_date=?," +
        "text=?," +
        "status_id=? " +
        "WHERE survey_id=?";


    public void setSurvey(SurveyData survey) throws SQLException {
        Connection conn = null;
        try {
            conn = DBMS.getConnection();
            conn.setAutoCommit(false);
            createSurveyIfNotExists(conn, survey.getId());
            Integer surveyId = new Integer(survey.getId());
            Timestamp startDate = new Timestamp(survey.getStartDate().getTime());
            Timestamp endDate = new Timestamp(survey.getStartDate().getTime() + (60000L * survey.getLength()));
            Object[] params = new Object[]{
                survey.getName(),
                startDate,
                endDate,
                survey.getText(),
                new Integer(survey.getStatus().getId()),
                surveyId
            };
            executeUpdate(conn, UPDATE_SURVEY_QUERY, params);
            conn.commit();
        } catch (SQLException e) {
            rollback(conn);
            printException(e);
            throw e;
        } finally {
            close(conn, null, null);
        }
    }

    private static final String GET_QUESTION_TYPES_QUERY =
        "SELECT " +
        "question_type_id," +
        "question_type_desc" +
        " FROM question_type";


    public Collection getQuestionTypes() throws SQLException {
        Connection conn = null;
        ResultSet result = null;
        PreparedStatement stmt = null;
        try {
            conn = DBMS.getConnection();
            stmt = conn.prepareStatement(GET_QUESTION_TYPES_QUERY);
            result = stmt.executeQuery();
            Vector r = new Vector();
            while (result.next()) {
                r.add(new QuestionType(result.getInt(1), result.getString(2)));
            }
            return r;
        } catch (SQLException e) {
            printException(e);
            throw e;
        } finally {
            close(conn, stmt, result);
        }
    }


    private static final String GET_QUESTION_STYLES_QUERY =
        "SELECT " +
        "question_style_id," +
        "question_style_desc" +
        " FROM question_style";


    public Collection getQuestionStyles() throws SQLException {
        Connection conn = null;
        ResultSet result = null;
        PreparedStatement stmt = null;
        try {
            conn = DBMS.getConnection();
            stmt = conn.prepareStatement(GET_QUESTION_STYLES_QUERY);
            result = stmt.executeQuery();
            Vector r = new Vector();
            while (result.next()) {
                r.add(new QuestionStyle(result.getInt(1), result.getString(2)));
            }
            return r;
        } catch (SQLException e) {
            printException(e);
            throw e;
        } finally {
            close(conn, stmt, result);
        }
    }


    private static final String GET_QUESTIONS_QUERY =
        "SELECT " +
        "q.question_id," +
        "q.keyword," +
        "q.question_text," +
        "q.question_type_id," +
        "qt.question_type_desc," +
        "q.question_style_id," +
        "s.question_style_desc," +
        "q.status_id," +
        "status_lu.status_desc" +
        " FROM question q LEFT OUTER JOIN round_question rq ON q.question_id=rq.question_id " +
        "LEFT OUTER JOIN question_type qt ON q.question_type_id=qt.question_type_id " +
        "LEFT OUTER JOIN question_style s ON q.question_style_id=s.question_style_id " +
        "LEFT OUTER JOIN status_lu ON q.status_id=status_lu.status_id " +
        "WHERE rq.round_id=?";


    public Collection getQuestions(int roundID) throws SQLException {
        Connection conn = null;
        ResultSet result = null;
        PreparedStatement stmt = null;
        try {
            conn = DBMS.getConnection();
            stmt = conn.prepareStatement(GET_QUESTIONS_QUERY);
            stmt.setInt(1, roundID);
            result = stmt.executeQuery();
            Vector r = new Vector();
            while (result.next()) {
                int index = 1;
                QuestionData data = new QuestionData(
                                                     result.getInt(index++),
                                                     result.getString(index++),
                                                     result.getString(index++),
                                                     new QuestionType(
                                                                      result.getInt(index++),
                                                                      result.getString(index++)
                                                                      ),
                                                     new QuestionStyle(
                                                                       result.getInt(index++),
                                                                       result.getString(index++)
                                                                       ),
                                                     new SurveyStatus(
                                                                      result.getInt(index++),
                                                                      result.getString(index++)
                                                                      )
                                                     );
                r.add(data);
            }
            return r;
        } catch (SQLException e) {
            printException(e);
            throw e;
        } finally {
            close(conn, stmt, result);
        }
    }


    private static final String INSERT_QUESTION_QUERY =
        "INSERT INTO question (" +
        "question_id," +
        "question_text," +
        "keyword," +
        "question_type_id," +
        "question_style_id," +
        "status_id, " +
        "is_required " +
        ") VALUES (?,?,?,?,?,?,?)";

    private static final String ADD_ROUND_QUESTION_QUERY =
        "INSERT INTO round_question (round_id,question_id) VALUES (?,?)";
    private static final String ADD_SURVEY_QUESTION_QUERY =
        "INSERT INTO survey_question (survey_id,question_id) VALUES (?,?)";


    public int addQuestion(int roundID, QuestionData question) throws SQLException {
        Connection conn = null;
        try {
            conn = DBMS.getConnection();
            conn.setAutoCommit(false);
            createSurveyIfNotExists(conn, roundID);
            int questionID = getNewIDFor(DBMS.SURVEY_SEQ);
            question.setId(questionID);
            Object param[] = new Object[]{
                new Integer(question.getId()),
                question.getText(),
                question.getKeyword(),
                new Integer(question.getType().getId()),
                new Integer(question.getStyle().getId()),
                new Integer(question.getStatus().getId()),
                new Boolean(question.isRequired())
            };
            executeUpdate(conn, INSERT_QUESTION_QUERY, param);
            param = new Object[]{
                new Integer(roundID),
                new Integer(question.getId())
            };
            executeUpdate(conn, ADD_ROUND_QUESTION_QUERY, param);
            executeUpdate(conn, ADD_SURVEY_QUESTION_QUERY, param);
            conn.commit();
            return questionID;
        } catch (SQLException e) {
            rollback(conn);
            printException(e);
            throw e;
        } finally {
            close(conn, null, null);
        }
    }



    /**
     * <p>
     *     Verify question answers.
     * </p>
     * @param question
     *          the question data
     * @return
     *          a String with error message
     * @throws SQLException
     *          if any related error occurs
     * @since 1.1
     */
    private String verifyQuestion(QuestionData question) throws SQLException {
        if (question.getType().getId() != ELIGIBLE_QUESTION_TYPE_ID) {
            //ignore other types except eligible questions
            return "";
        }

        Collection answers = getAnswers(question.getId());
        if (null == answers || answers.size() <= 0) {
            return "";
        }
        int count = 0, maxLength = 0;
        for (Iterator it = answers.iterator(); it.hasNext(); ) {
            AnswerData data = (AnswerData)it.next();
            if (data.isCorrect()) {
                count++;
            }
            maxLength = (data.getText().length() > maxLength ? data.getText().length() : maxLength);
        }
        if (count < 1) {
            return "A question must have at least 1 correct answers.";
        }
        if (question.getStyle().getId() == Question.SINGLECHOICE) {
            //there must be 1 correct answer
            if (count != 1) {
                return "A Single Choice question must have exactly 1 correct answers.";
            }
        } else if (question.getStyle().getId() == Question.LONGANSWER) {
            if (maxLength > MAX_LONG_ANSWER_LENGTH) {
                return "The length of answer text can't exceed " + MAX_LONG_ANSWER_LENGTH + " chars";
            }
        } else if (question.getStyle().getId() == Question.SHORTANSWER) {
            if (maxLength > MAX_SHORT_ANSWER_LENGTH) {
                return "The length of answer text can't exceed " + MAX_SHORT_ANSWER_LENGTH + " chars";
            }
        }
        return "";
    }

    private static final String UPDATE_QUESTION_QUERY =
        "UPDATE question SET " +
        "question_text=?," +
        "keyword=?," +
        "question_type_id=?," +
        "question_style_id=?," +
        "status_id=? " +
        "WHERE question_id=?";

    public void modifyQuestion(QuestionData question) throws SQLException {
        try {
            String verifyMessage = verifyQuestion(question);
            if (verifyMessage.length() > 0) {
                throw new SQLException(verifyMessage);
            }
            Object[] param = new Object[]{
                question.getText(),
                question.getKeyword(),
                new Integer(question.getType().getId()),
                new Integer(question.getStyle().getId()),
                new Integer(question.getStatus().getId()),
                new Integer(question.getId())
            };
            executeUpdate(UPDATE_QUESTION_QUERY, param);
        } catch (SQLException e) {
            printException(e);
            throw e;
        }
    }


    private static final String DELETE_SURVEY_QUESTION_QUERY =
        "DELETE FROM survey_question WHERE question_id=?";
    private static final String DELETE_ROUND_QUESTION_QUERY =
        "DELETE FROM round_question WHERE question_id=?";
    private static final String DELETE_ANSWER_QUESTION_QUERY =
        "DELETE FROM answer WHERE question_id=?";
    private static final String DELETE_QUESTION_QUERY =
        "DELETE FROM question WHERE question_id=?";

    public void deleteQuestion(int questionID) throws SQLException {
        Connection conn = null;
        try {
            conn = DBMS.getConnection();
            conn.setAutoCommit(false);
            Integer questionId = new Integer(questionID);
            Object[] param = new Object[]{
                questionId
            };
            executeUpdate(conn, DELETE_SURVEY_QUESTION_QUERY, param);
            executeUpdate(conn, DELETE_ROUND_QUESTION_QUERY, param);
            executeUpdate(conn, DELETE_ANSWER_QUESTION_QUERY, param);
            executeUpdate(conn, DELETE_QUESTION_QUERY, param);
            conn.commit();
        } catch (SQLException e) {
            rollback(conn);
            printException(e);
            throw e;
        } finally {
            close(conn, null, null);
        }
    }


    private static final String GET_ANSWERS_QUERY =
        "SELECT " +
        "answer_id," +
        "answer_text," +
        "sort_order," +
        "correct " +
        " FROM answer WHERE question_id=?";

    public Collection getAnswers(int questionID) throws SQLException {
        Connection conn = null;
        ResultSet result = null;
        PreparedStatement stmt = null;
        try {
            conn = DBMS.getConnection();
            stmt = conn.prepareStatement(GET_ANSWERS_QUERY);
            stmt.setInt(1, questionID);
            result = stmt.executeQuery();
            Vector r = new Vector();
            while (result.next()) {
                AnswerData data = new AnswerData(
                                                 result.getInt(1),
                                                 result.getString(2),
                                                 result.getInt(3),
                                                 result.getInt(4) == 1
                                                 );
                r.add(data);
            }
            return r;
        } catch (SQLException e) {
            printException(e);
            throw e;
        } finally {
            close(conn, stmt, result);
        }
    }


    private static final String INSERT_ANSWER_QUERY =
        "INSERT INTO answer (" +
        "answer_id," +
        "question_id," +
        "answer_text," +
        "sort_order," +
        "correct " +
        ") VALUES (?,?,?,?,?)";

    public int addAnswer(int questionID, AnswerData answer) throws SQLException {
        Connection conn = null;
        try {
            int answerID = getNewIDFor(DBMS.SURVEY_SEQ);
            conn = DBMS.getConnection();
            conn.setAutoCommit(false);
            answer.setId(answerID);
            Object param[] = new Object[]{
                new Integer(answer.getId()),
                new Integer(questionID),
                answer.getText(),
                new Integer(answer.getSortOrder()),
                new Integer(answer.isCorrect() ? 1 : 0)
            };
            executeUpdate(conn, INSERT_ANSWER_QUERY, param);
            conn.commit();
            return answerID;
        } catch (SQLException e) {
            rollback(conn);
            printException(e);
            throw e;
        } finally {
            close(conn, null, null);
        }
    }


    private static final String UPDATE_ANSWER_QUERY =
        "UPDATE answer SET " +
        "answer_text=?," +
        "sort_order=?," +
        "correct=? " +
        "WHERE answer_id=?";

    public void modifyAnswer(AnswerData answer) throws SQLException {
        try {
            Object[] param = new Object[]{
                answer.getText(),
                new Integer(answer.getSortOrder()),
                new Integer(answer.isCorrect() ? 1 : 0),
                new Integer(answer.getId())
            };
            executeUpdate(UPDATE_ANSWER_QUERY, param);
        } catch (SQLException e) {
            printException(e);
            throw e;
        }
    }


    private static final String DELETE_ANSWER_QUERY =
        "DELETE FROM answer WHERE answer_id=?";

    public void deleteAnswer(int answerID) throws SQLException {
        try {
            Object[] param = new Object[]{
                new Integer(answerID)
            };
            executeUpdate(DELETE_ANSWER_QUERY, param);
        } catch (SQLException e) {
            printException(e);
            throw e;
        }
    }

    /**
     * A constant String containing SQL query that should be used to save
     * round room assignment details.<p>
     * <pre>
     *   UPDATE round_room_assignment
     *   SET
     *     coders_per_room = ?,
     *     algorithm = ?,
     *     isByDivision = ?,
     *     isByRegion = ?,
     *     isFinal = ?,
     *     p = ?
     *   WHERE
     *     round_id = ?
     * </pre>
     *
     * @since Admin Tool 2.0
     */
    private final static String UPDATE_ROOM_ASSIGNMENT_QUERY =
        "UPDATE round_room_assignment " +
        "   SET " +
        "     coders_per_room = ?,"+
        "     algorithm = ?,"+
        "     by_division = ?,"+
        "     by_region = ?,"+
        "     final = ?,"+
        "     p = ?"+
        "   WHERE "+
        "     round_id = ?";

    /**
     * Saves the details of round room assignment algorithm for specified
     * round to databse.
     *
     * @param  details a RoundRoomAssignment object containing the details
     *         of round room assignment algorithm for some round that needs
     * @throws IllegalArgumentException if given argument is null.
     * @throws SQLException
     * @since  Admin Tool 2.0
     */
    public void saveRoundRoomAssignment(RoundRoomAssignment details)
        throws SQLException {
        Connection conn = null;
        if( details == null )
            throw new IllegalArgumentException("details cannot be null");
        conn = DBMS.getConnection();
        saveRoundRoomAssignment( conn, details, true );
    }

    /**
     * This is the internal version of the same method above but overloaded
     * to allow the caller to pass a db connection. It saves the details of
     * round room assignment algorithm for specified round to database.
     *
     * This is needed because the method updateRound() needs to perform the update
     * operation using it's own db connection.
     *
     * @param  details a RoundRoomAssignment object containing the details
     *         of round room assignment algorithm for some round that needs
     * @throws IllegalArgumentException if given argument is null.
     * @throws SQLException
     * @since  Admin Tool 2.0
     * @see AdminServices
     */
    private void saveRoundRoomAssignment(Connection conn,
                                         RoundRoomAssignment details,
                                         boolean doCommit )
        throws SQLException {
        try {
            conn.setAutoCommit(false);
            Integer roundId = new Integer(details.getRoundId());
            Integer codersPerRoom = new Integer(details.getCodersPerRoom());
            Integer algorithm = new Integer(details.getType());
            Integer isByDivision = new Integer(details.isByDivision() ? 1 : 0);
            Integer isByRegion = new Integer(details.isByRegion() ? 1 : 0);
            Integer isFinal = new Integer(details.isFinal() ? 1 : 0);
            Double  p = new Double(details.getP());
            Object[] params = new Object[]{
                codersPerRoom,
                algorithm,
                isByDivision,
                isByRegion,
                isFinal,
                p,
                roundId
            };
            executeUpdate(conn, UPDATE_ROOM_ASSIGNMENT_QUERY, params);
            if( doCommit )
                conn.commit();
        } catch (SQLException e) {
            rollback(conn);
            printException(e);
            throw e;
        } finally {
            if( doCommit )
                close(conn, null, null);
        }
    }

    // Code mostly taken from com.topcoder.utilities.CreateSysTest
    public CommandResponse createSystemTests(CreateSystestsRequest request) throws Exception {
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            c = DBMS.getConnection();
            c.setAutoCommit(false);

            int roundId = request.getRoundID();

            StringBuilder sqlStr = new StringBuilder(250);
            ArrayList testCases = new ArrayList(20);

            // Get a list of all challenges that were successful
            sqlStr.append("SELECT component_id, args, expected FROM challenge WHERE round_id = ? ");
            sqlStr.append(" AND succeeded = 1 ORDER by component_id");

            ps = c.prepareStatement(sqlStr.toString());
            ps.setInt(1, roundId);
            rs = ps.executeQuery();

            ArrayList tmp = null;
            int testCaseId = 0;
            // For each challenge, grab the args and expected
            while (rs.next()) {
                tmp = new ArrayList(4);
                testCaseId = IdGeneratorClient.getSeqIdAsInt(DBMS.JMA_SEQ);
                tmp.add(new Integer(testCaseId));  // test case id
                tmp.add(new Integer(rs.getInt(1))); // component id
                tmp.add(DBMS.getBlobObject(rs, 2)); // args
                tmp.add(DBMS.getBlobObject(rs, 3)); // expected
                testCases.add(tmp);
            }

            rs.close();
            rs = null;
            ps.close();
            ps = null;

            debug("TEST CASES: " + testCases);
            ArrayList thisCase = null;
            int testId;
            int probId;
            Object testCaseArgs = null;
            Object expected = null;
            for (int i = 0; i < testCases.size(); i++) {
                thisCase = (ArrayList) testCases.get(i);

                // Insert the system test case into SYSTEM_TEST_CASES
                testId = ((Integer) thisCase.get(0)).intValue();
                probId = ((Integer) thisCase.get(1)).intValue();
                testCaseArgs = thisCase.get(2);
                expected = thisCase.get(3);
                debug("-----------------------------------");
                debug("testId: " + testId);
                debug("probId: " + probId);
                debug("testCaseArgs: " + testCaseArgs);
                debug("expected: " + expected);
                debug("-----------------------------------");

                sqlStr.replace(0, sqlStr.length(), "INSERT INTO system_test_case");
                sqlStr.append(" (test_case_id, component_id, args, expected_result, test_number) ");
                sqlStr.append("VALUES (?, ?, ?, ?, (SELECT COUNT(*) FROM system_test_case WHERE component_id = ?))");
                ps = c.prepareStatement(sqlStr.toString());
                ps.setInt(1, testId);
                ps.setInt(2, probId);
                ps.setBytes(3, DBMS.serializeBlobObject(testCaseArgs));
                ps.setBytes(4, DBMS.serializeBlobObject(expected));
                ps.setInt(5, probId);

                int rows = ps.executeUpdate();

                if (rows != 1)
                    log.error("System test case was not able to be added!");
            } // end for loop over test cases

            c.commit();
            return new CommandSucceededResponse(testCases.size() + " test case(s) added");
        } catch (Exception e) {
            rollback(c);
            printException(e);
            throw e;
        } finally {
            DBMS.close(c, ps, rs);
            }
            }

    // Rewrite of code in cst.java
    public CommandResponse consolidateTestCases(ConsolidateTestRequest request) throws Exception {
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            c = DBMS.getConnection();
            c.setAutoCommit(false);

            int i, seenCount, dupCount, roundId = request.getRoundID();
            ResultSetContainer rsc = runSelectQuery(c, "SELECT DISTINCT component_id FROM round_component WHERE round_id = " + roundId);
            if (rsc.getRowCount() == 0) {
                return new CommandFailedResponse("Consolidate failed; no components found in database for round " + roundId);
            }

            // Loop over problems checking for duplicates WITHIN a given problem's test cases (original
            // cst.java program incorrectly checked for duplicates among test cases across all round components).
            int totalDupCount = 0;
            for (i = 0; i < rsc.getRowCount(); i++) {
                int componentId = Integer.parseInt(rsc.getItem(i, 0).toString());

                ps = c.prepareStatement("SELECT test_case_id, args FROM system_test_case WHERE component_id = " + componentId + " ORDER BY test_number, test_case_id");
                rs = ps.executeQuery();
                seenCount = 0;
                dupCount = 0;

                HashSet duplicateTestIds = new HashSet();
                HashSet testCasesSeen = new HashSet();
                while (rs.next()) {
                    seenCount++;
                    ArrayList args = (ArrayList) DBMS.getBlobObject(rs, 2);
                    if (!testCasesSeen.add(args)) {
                        dupCount++;
                        duplicateTestIds.add(new Integer(rs.getInt("test_case_id")));
                    }
                }

                rs.close();
                rs = null;
                ps.close();
                ps = null;

                debug("For component " + componentId + " there were " + seenCount + " test cases, with " + dupCount + " duplicates");

                // Remove duplicates
                Iterator it = duplicateTestIds.iterator();
                while (it.hasNext()) {
                    int dupId = ((Integer) it.next()).intValue();
                    int rowsModified = runUpdateQuery("DELETE FROM system_test_case WHERE test_case_id = " + dupId);
                    if (rowsModified != 1) {
                        log.error("Test case id " + dupId + " has already been removed from database");
                    }
                }

                // Reorder
                if (dupCount != 0) {
                    ps = c.prepareStatement("SELECT test_case_id, (SELECT COUNT(*) FROM system_test_case AS stc WHERE stc.component_id = system_test_case.component_id AND stc.test_number < system_test_case.test_number) FROM system_test_case WHERE component_id = " + componentId);
                    rs = ps.executeQuery();

                    HashMap map = new HashMap();
                    while (rs.next()) {
                        map.put(new Integer(rs.getInt(1)), new Integer(rs.getInt(2)));
                    }

                    rs.close();
                    rs = null;
                    ps.close();
                    ps = null;

                    for (Iterator iter = map.keySet().iterator(); iter.hasNext();) {
                        Integer id = (Integer) iter.next();
                        Integer number = (Integer) map.get(id);
                        int rowsModified = runUpdateQuery("UPDATE system_test_case SET test_number = " + number + " WHERE test_case_id = " + id);
                        if (rowsModified != 1) {
                            log.error("Update test number of test case id " + id + " failed.");
                        }
                    }
                }

                totalDupCount += dupCount;
            }

            c.commit();
            return new CommandSucceededResponse(totalDupCount + " duplicate test case(s) removed from database");

        } catch (Exception e) {
            printException(e);
            try {
                if (c != null) c.rollback();
            } catch (Exception e1) {
                printException(e1);
            }
            throw e;
        } finally {
            try {
                if (rs != null) rs.close();
            } catch (Exception e1) {
                printException(e1);
            }
            try {
                if (ps != null) ps.close();
            } catch (Exception e1) {
                printException(e1);
            }
            try {
                if (c != null) c.setAutoCommit(true);
            } catch (Exception e1) {
                printException(e1);
            }
            try {
                if (c != null) c.close();
            } catch (Exception e1) {
                printException(e1);
            }
        }
    }

    public CommandResponse runRatings(RunRatingsRequest request) throws Exception {
        Connection c = null;
        try {
            c = DBMS.getConnection();
            c.setAutoCommit(false);
            int roundId = request.getRoundID();
            boolean shouldCommit = request.getShouldCommit();
            boolean runByDivision = request.getRunByDivision();
            int ratingType = request.getRatingType();
            if (ratingType == ContestConstants.MM_RATING) {
                RatingProcess ratingProcess = RatingProcessFactory.getMarathonRatingProcess(roundId, c);
                ratingProcess.runProcess();
                if (shouldCommit) {
                    c.commit();
                } else {
                    c.rollback();
                }
            } else {
                RatingQubits ratingProcessor = new RatingQubits();
                ratingProcessor.runRatings(c, roundId, shouldCommit, runByDivision, ratingType);
            }
            return new CommandSucceededResponse("Ratings successfully updated.  See log for detailed updates.");
        } catch (Exception e) {
            printException(e);
            throw e;
        } finally {
            try {
                if (c != null) c.setAutoCommit(true);
            } catch (Exception e1) {
                printException(e1);
            }
            try {
                if (c != null) c.close();
            } catch (Exception e1) {
                printException(e1);
            }
        }
    }

    public CommandResponse runSeasonRatings(RunSeasonRatingsRequest request) throws Exception {
        Connection c = null;
        try {
            c = DBMS.getConnection();
            c.setAutoCommit(false);
            RatingQubits ratingProcessor = new RatingQubits();
            int roundId = request.getRoundID();
            boolean shouldCommit = request.getShouldCommit();
            boolean runByDivision = request.getRunByDivision();
            int season = request.getSeason();
            ratingProcessor.runSeasonRatings(c, roundId, shouldCommit, runByDivision, season);
            return new CommandSucceededResponse("Season Ratings successfully updated.  See log for detailed updates.");
        } catch (Exception e) {
            printException(e);
            throw e;
        } finally {
            try {
                if (c != null) c.setAutoCommit(true);
            } catch (Exception e1) {
                printException(e1);
            }
            try {
                if (c != null) c.close();
            } catch (Exception e1) {
                printException(e1);
            }
        }
    }

    public CommandResponse insertPracticeRooms(InsertPracticeRoomRequest request) throws SQLException {
        Connection c = null;
        PreparedStatement ps = null;
        try {
            c = DBMS.getConnection();
            c.setAutoCommit(false);

            int roundId = request.getRoundID();
            String name = request.getName();
            int groupId = request.getGroupID();

            StringBuilder getDivisionsQuery = new StringBuilder();
            getDivisionsQuery.append("SELECT DISTINCT(division_id) FROM round_component WHERE round_id=" + roundId + " ORDER BY 1");
            ResultSetContainer divisionsRsc = runSelectQuery(c, getDivisionsQuery.toString());
            int numDivisions = divisionsRsc.getRowCount();

            if (numDivisions == 0) {
                return new CommandFailedResponse("Practice room generation: No components found for round " + roundId + " in the database!");
            }

            // Is this team or regular?
            StringBuilder getIsTeamQuery = new StringBuilder();
            getIsTeamQuery.append("SELECT round_type_id FROM round WHERE round_id = ");
            getIsTeamQuery.append(roundId);
            ResultSetContainer isTeamRsc = runSelectQuery(c, getIsTeamQuery.toString());
            int roundTypeId = Integer.parseInt(isTeamRsc.getItem(0, "round_type_id").toString());
            com.topcoder.netCommon.contest.round.RoundType roundType = com.topcoder.netCommon.contest.round.RoundType.get(roundTypeId);

            // Loop over divisions and add.  A separate practice room will be created for each division.
            // Now use unified way to get new round IDs
            for (int i = 0; i < numDivisions; i++) {
                int nextRoundId = getNewIDFor(DBMS.ROUND_SEQ);
                int nextContestId = getNewIDFor(DBMS.CONTEST_SEQ);
                int nextRoomId = getNewIDFor(DBMS.ROOM_SEQ);
                int divisionId = Integer.parseInt(divisionsRsc.getItem(i, 0).toString());
                StringBuilder getComponents = new StringBuilder();
                getComponents.append("SELECT component_id, difficulty_id, submit_order, points, open_order ");
                getComponents.append("FROM round_component WHERE round_id = " + roundId + " AND division_id = " + divisionId + " ");
                getComponents.append("ORDER BY 2");
                ResultSetContainer rsc = runSelectQuery(c, getComponents.toString());

                // The name for this practice room
                //int roomSequenceId = nextRoundId - AdminConstants.PRACTICE_ROUND_START_ID + 1;
                //String practiceRoomName = roomSequenceId + " - " + name + " DIV " + divisionId;
                String practiceRoomName;
                String contestName;
                String roundName;
                if (!roundType.isLongRound()) {
                    practiceRoomName = name + " DIV " + divisionId;
                    contestName = practiceRoomName;
                    roundName = AdminConstants.PRACTICE_ROUND_NAME;
                } else {
                    practiceRoomName = name;
                    contestName = AdminConstants.PRACTICE_ROUND_NAME;
                    roundName = practiceRoomName;
                }
                // Add contest
                StringBuilder contestInsert = new StringBuilder();
                contestInsert.append("INSERT INTO contest (contest_id, name, status) VALUES(?,?,?)");
                ps = c.prepareStatement(contestInsert.toString());
                ps.setInt(1, nextContestId);
                ps.setString(2, contestName);
                ps.setString(3, AdminConstants.PRACTICE_CONTEST_STATUS);
                ps.executeUpdate();
                ps.close();
                ps = null;

                // Add round
                StringBuilder roundInsert = new StringBuilder();
                roundInsert.append("INSERT INTO round (contest_id, round_id, name, status, round_type_id) VALUES(?,?,?,?,?)");
                ps = c.prepareStatement(roundInsert.toString());
                ps.setInt(1, nextContestId);
                ps.setInt(2, nextRoundId);
                ps.setString(3, roundName);
                ps.setString(4, AdminConstants.PRACTICE_ROUND_STATUS);
                if (roundType.isTeamRound()) {
                    ps.setInt(5, ContestConstants.TEAM_PRACTICE_ROUND_TYPE_ID);
                } else if (roundType.isLongRound()) {
                    if (roundType.getId() == ContestConstants.AMD_LONG_PROBLEM_ROUND_TYPE_ID) {
                        ps.setInt(5, ContestConstants.AMD_LONG_PROBLEM_PRACTICE_ROUND_TYPE_ID);
                    } else {
                        ps.setInt(5, ContestConstants.LONG_PROBLEM_PRACTICE_ROUND_TYPE_ID);
                    }
                } else {
                    ps.setInt(5, ContestConstants.PRACTICE_ROUND_TYPE_ID);
                }
                ps.executeUpdate();
                ps.close();
                ps = null;

                // Add group
                StringBuilder groupInsert = new StringBuilder();
                groupInsert.append("insert into round_group_xref values (?, ?)");
                ps = c.prepareStatement(groupInsert.toString());
                ps.setInt(1, nextRoundId);
                ps.setInt(2, groupId);
                ps.executeUpdate();
                ps.close();

                // Add room
                StringBuilder roomInsert = new StringBuilder();
                roomInsert.append("INSERT INTO room (round_id, room_id, name, division_id, room_type_id) VALUES(?,?,?,?,?)");
                ps = c.prepareStatement(roomInsert.toString());
                ps.setInt(1, nextRoundId);
                ps.setInt(2, nextRoomId);
                ps.setString(3, practiceRoomName);
                ps.setInt(4, divisionId);
                if (roundType.isTeamRound()) {
                    ps.setInt(5, ContestConstants.TEAM_PRACTICE_ROOM_TYPE_ID);
                } else {
                    ps.setInt(5, ContestConstants.PRACTICE_ROOM_TYPE_ID);
                }
                ps.executeUpdate();
                ps.close();
                ps = null;

                // Add round segment information
                StringBuilder segmentInsert = new StringBuilder();
                segmentInsert.append("INSERT INTO round_segment (round_id, segment_id, start_time, end_time, status) ");
                segmentInsert.append(" SELECT " + nextRoundId + ", segment_id, CURRENT, CURRENT, status ");
                segmentInsert.append(" FROM round_segment WHERE round_id = " + AdminConstants.PRACTICE_ROUND_START_ID);
                int rowsInserted = runUpdateQuery(c, segmentInsert.toString());
                if (rowsInserted == 0) {
                    c.rollback();
                    return new CommandFailedResponse("Practice room generation: Could not find round segment information to insert");
                }

                for (int j = 0; j < rsc.getRowCount(); j++) {
                    int componentId = Integer.parseInt(rsc.getItem(j, "component_id").toString());
                    int difficultyId = Integer.parseInt(rsc.getItem(j, "difficulty_id").toString());
                    int submitOrder = Integer.parseInt(rsc.getItem(j, "submit_order").toString());
                    float points = Float.parseFloat(rsc.getItem(j, "points").toString());
                    // Open order is not always set.  Default to submit order.
                    int openOrder = submitOrder;
                    try {
                        openOrder = Integer.parseInt(rsc.getItem(j, "open_order").toString());
                    } catch (Exception ignore) {
                    }

                    // GT - Removed Check bc not really needed anymore
                    //Sanity check
                    //StringBuilder dupCheck = new StringBuilder();
                    //dupCheck.append("SELECT r.round_id FROM round_component rc, round r ");
                    //dupCheck.append("WHERE rc.component_id = " + componentId + " ");
                    //dupCheck.append("AND rc.round_id = r.round_id ");
                    //dupCheck.append("AND r.round_type_id = " + ContestConstants.PRACTICE_ROUND_TYPE_ID);
                    //ResultSetContainer dupRsc = runSelectQuery(c, dupCheck.toString());
                    //if (dupRsc.getRowCount() > 0) {
                    //    int dupRoundId = Integer.parseInt(dupRsc.getItem(0, 0).toString());
                    //    StringBuilder response = new StringBuilder();
                    //    response.append("ProblemComponent ID " + componentId + " already is in practice round " + dupRoundId + ".\n");
                    //    response.append("This means that practice rooms have likely already been generated for this round.\n");
                    //    response.append("Therefore, new practice rooms were not created.");
                    //    c.rollback();
                    //    return new CommandFailedResponse(response.toString());
                    //}

                    // Insert round component
                    StringBuilder componentInsert = new StringBuilder();
                    componentInsert.append("INSERT INTO round_component ");
                    componentInsert.append("(round_id, component_id, submit_order, division_id, difficulty_id, points, open_order) ");
                    componentInsert.append("VALUES (?,?,?,?,?,?,?)");
                    ps = c.prepareStatement(componentInsert.toString());
                    ps.setInt(1, nextRoundId);
                    ps.setInt(2, componentId);
                    ps.setInt(3, submitOrder);
                    ps.setInt(4, divisionId);
                    ps.setInt(5, difficultyId);
                    ps.setFloat(6, points);
                    ps.setInt(7, openOrder);
                    ps.executeUpdate();
                    ps.close();
                    ps = null;
                } // end for loop over division problems
            } // end for loop over divisions

            c.commit();
            return new CommandSucceededResponse(numDivisions + " new practice room(s) added successfully for round " + roundId);
        } catch (SQLException e) {
            printException(e);
            try {
                if (c != null) c.rollback();
            } catch (Exception e1) {
                printException(e1);
            }
            throw e;
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (Exception e1) {
                printException(e1);
            }
            try {
                if (c != null) c.setAutoCommit(true);
            } catch (Exception e1) {
                printException(e1);
            }
            try {
                if (c != null) c.close();
            } catch (Exception e1) {
                printException(e1);
            }
        }
    }

    public BlobColumnResponse getBlobColumnMetadata() {
        try {
            StringBuilder query = new StringBuilder();
            query.append("SELECT st.tabname, sc.colname ");
            query.append("FROM sysblobs sb, systables st, syscolumns sc ");
            query.append("WHERE st.tabid > 99 ");
            query.append("AND sb.tabid = st.tabid ");
            query.append("AND sb.colno = sc.colno ");
            query.append("AND sc.tabid = st.tabid ");
            query.append("AND sc.coltype = " + ContestConstants.COLUMN_TYPE_BLOB + " ");
            query.append("ORDER BY 1,2");
            ResultSetContainer rsc = runSelectQuery(query.toString());

            TreeMap tableColumns = new TreeMap();
            String previousTable = null;
            ArrayList columnList = new ArrayList();

            int rowcount = rsc.getRowCount();
            if (rowcount == 0) {
                // No data?!?
                log.error("No blob columns found in database");
                return new BlobColumnResponse(false, null);
            }

            for (int i = 0; i < rsc.getRowCount(); i++) {
                String table = rsc.getItem(i, 0).toString();
                String column = rsc.getItem(i, 1).toString();

                if (i == 0) {
                    previousTable = table;
                    columnList.add(column);
                    continue;
                }

                if (previousTable.equals(table)) {
                    columnList.add(column);
                    continue;
                }

                tableColumns.put(previousTable, columnList);
                previousTable = table;
                columnList = new ArrayList();
                columnList.add(column);
            }
            tableColumns.put(previousTable, columnList);

            return new BlobColumnResponse(true, tableColumns);
        } catch (SQLException e) {
            printException(e);
            return new BlobColumnResponse(false, null);
        }
    }

    public ObjectUpdateResponse updateDBObject(ObjectUpdateRequest request) {
        Connection c = null;
        PreparedStatement ps = null;
        StringBuilder update = new StringBuilder();

        try {
            c = DBMS.getConnection();
            c.setAutoCommit(false);

            String tableName = request.getTableName();
            String columnName = request.getColumnName();
            String whereClause = request.getWhereClause().trim();
            Object updateObject = request.getUpdateObject();
            boolean enforceUnique = request.getRequireUniqueRow();

            // Build the query
            update.append("UPDATE " + tableName + " SET " + columnName + "=? ");
            if (whereClause.length() > 0) {
                update.append("WHERE " + whereClause);
            }

            String updateStr = update.toString();
            ps = c.prepareStatement(updateStr);
            ps.setBytes(1, DBMS.serializeBlobObject(updateObject));
            int rowsModified = ps.executeUpdate();

            String queryStr = "  Query:\n" + updateStr;
            if (rowsModified == 0) {
                return new ObjectUpdateResponse(false, "There were no database rows matching the given constraint." + queryStr);
            }
            if (enforceUnique && rowsModified > 1) {
                c.rollback();
                return new ObjectUpdateResponse(false, rowsModified + " rows would have been affected, violating uniqueness requirement." + queryStr);
            }

            c.commit();
            return new ObjectUpdateResponse(true, rowsModified + " database row(s) successfully updated." + queryStr);
        } catch (Exception e) {
            printException(e);
            try {
                if (c != null) c.rollback();
            } catch (Exception e1) {
                printException(e1);
            }
            return new ObjectUpdateResponse(false, "Check the where clause; the EJB received a database exception running the query:\n" + e + "\n-----\nQuery:\n" + update.toString());
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (Exception e1) {
                printException(e1);
            }
            try {
                if (c != null) c.setAutoCommit(true);
            } catch (Exception e1) {
                printException(e1);
            }
            try {
                if (c != null) c.close();
            } catch (Exception e1) {
                printException(e1);
            }
        }
    }

    public ObjectSearchResponse runObjectSearch(ObjectSearchRequest request) {
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            c = DBMS.getConnection();

            String tableName = request.getTableName().trim();
            final String columnName = request.getColumnName();
            final String searchText = request.getSearchText();
            String userWhereClause = request.getWhereClause().trim();

            // Build the query
            StringBuilder query = new StringBuilder();

            // These are specified in design document
            if (tableName.equals("system_test_result")) {
                query.append("SELECT system_test_result.component_id, component.class_name, system_test_result.coder_id, ");
                query.append("user.handle, system_test_result.round_id, contest.name, system_test_result.received ");
                query.append("FROM system_test_result, component, user, contest, round ");
                query.append("WHERE system_test_result.component_id = component.component_id ");
                query.append("AND system_test_result.coder_id = user.user_id ");
                query.append("AND system_test_result.round_id = round.round_id ");
                query.append("AND round.contest_id = contest.contest_id ");
                if (userWhereClause.length() > 0) {
                    query.append("AND (" + userWhereClause + ")");
                }
            } else if (tableName.equals("system_test_case")) {
                query.append("SELECT system_test_case.component_id, system_test_case.test_case_id, ");
                query.append("system_test_case.args, system_test_case.expected_result, system_test_case.test_number ");
                query.append("FROM system_test_case ");
                if (userWhereClause.length() > 0) {
                    query.append("WHERE " + userWhereClause);
                }
            } else if (tableName.equals("challenge")) {
                query.append("SELECT challenge.component_id, challenge.challenge_id, challenge.round_id, room_result.room_id, ");
                query.append("challenge.defendant_id, u1.handle AS defendant_handle, challenge.challenger_id, ");
                query.append("u2.handle AS challenger_handle, challenge.succeeded, challenge.args, challenge.expected, ");
                query.append("challenge.received, challenge.message, challenge.submit_time ");
                query.append("FROM challenge, room_result, user u1, user u2 ");
                query.append("WHERE challenge.round_id = room_result.round_id ");
                query.append("AND challenge.challenger_id = room_result.coder_id ");
                query.append("AND challenge.defendant_id = u1.user_id ");
                query.append("AND challenge.challenger_id = u2.user_id ");
                if (userWhereClause.length() > 0) {
                    query.append("AND (" + userWhereClause + ")");
                }
            } else if (tableName.equals("problem")) {
                query.append("SELECT problem.problem_id, problem.class_name, problem.param_types FROM problem ");
                if (userWhereClause.length() > 0) {
                    query.append("WHERE " + userWhereClause);
                }
            } else if (tableName.equals("staging_problem")) {
                query.append("SELECT staging_problem.problem_id, staging_problem.class_name, staging_problem.param_types ");
                query.append("FROM staging_problem ");
                if (userWhereClause.length() > 0) {
                    query.append("WHERE " + userWhereClause);
                }
            } else {
                query.append("SELECT * FROM " + tableName + " ");
                if (userWhereClause.length() > 0) {
                    query.append("WHERE " + userWhereClause);
                }
            }

            // Run the query
            ps = c.prepareStatement(query.toString());
            rs = ps.executeQuery();
            ResultSetContainer temp = new ResultSetContainer(rs, true);
            // This weeds out the rows which don't have the search text
            ResultSetContainer rsc = new ResultSetContainer(temp, new Contains(columnName, searchText));


            return new ObjectSearchResponse(true, "Object search successful", rsc);
        } catch (Exception e) {
            printException(e);
            String errorMsg;
            if (e instanceof SQLException) {
                errorMsg = "Received database exception; check where clause:\n" +
                    DBMS.getSqlExceptionString((SQLException) e);
            } else {
                errorMsg = "Received exception during object search:\n" + e;
            }
            return new ObjectSearchResponse(false, errorMsg, null);
        } finally {
            try {
                if (rs != null) rs.close();
            } catch (Exception e1) {
                printException(e1);
            }
            try {
                if (ps != null) ps.close();
            } catch (Exception e1) {
                printException(e1);
            }
            try {
                if (c != null) c.close();
            } catch (Exception e1) {
                printException(e1);
            }
        }
    }


    public TextColumnResponse getTextColumnMetadata() {
        try {
            StringBuilder query = new StringBuilder();
            query.append("SELECT st.tabname, sc.colname ");
            query.append("FROM sysblobs sb, systables st, syscolumns sc ");
            query.append("WHERE st.tabid > 99 ");
            query.append("AND sb.tabid = st.tabid ");
            query.append("AND sb.colno = sc.colno ");
            query.append("AND sc.tabid = st.tabid ");
            query.append("AND sc.coltype = " + ContestConstants.COLUMN_TYPE_TEXT + " ");
            query.append("ORDER BY 1,2");
            ResultSetContainer rsc = runSelectQuery(query.toString());

            TreeMap tableColumns = new TreeMap();
            String previousTable = null;
            ArrayList columnList = new ArrayList();

            int rowcount = rsc.getRowCount();
            if (rowcount == 0) {
                // No data?!?
                log.error("No text columns found in database");
                return new TextColumnResponse(false, null);
            }

            for (int i = 0; i < rsc.getRowCount(); i++) {
                String table = rsc.getItem(i, 0).toString();
                String column = rsc.getItem(i, 1).toString();

                if (i == 0) {
                    previousTable = table;
                    columnList.add(column);
                    continue;
                }

                if (previousTable.equals(table)) {
                    columnList.add(column);
                    continue;
                }

                tableColumns.put(previousTable, columnList);
                previousTable = table;
                columnList = new ArrayList();
                columnList.add(column);
            }
            tableColumns.put(previousTable, columnList);

            return new TextColumnResponse(true, tableColumns);
        } catch (SQLException e) {
            printException(e);
            return new TextColumnResponse(false, null);
        }
    }

    public TextUpdateResponse updateDBText(TextUpdateRequest request) {
        Connection c = null;
        PreparedStatement ps = null;

        try {
            c = DBMS.getConnection();
            c.setAutoCommit(false);

            String tableName = request.getTableName();
            String columnName = request.getColumnName();
            String whereClause = request.getWhereClause().trim();
            Object updateObject = request.getUpdateObject();
            boolean enforceUnique = request.getRequireUniqueRow();

            // Build the query
            StringBuilder update = new StringBuilder();
            update.append("UPDATE " + tableName + " SET " + columnName + "=? ");
            if (whereClause.length() > 0) {
                update.append("WHERE " + whereClause);
            }

            ps = c.prepareStatement(update.toString());
            ps.setBytes(1, DBMS.serializeBlobObject(updateObject));
            int rowsModified = ps.executeUpdate();

            if (rowsModified == 0) {
                return new TextUpdateResponse(false, "There were no database rows matching the given constraint.");
            }
            if (enforceUnique && rowsModified > 1) {
                c.rollback();
                return new TextUpdateResponse(false, rowsModified + " rows would have been affected, violating uniqueness requirement");
            }

            c.commit();
            return new TextUpdateResponse(true, rowsModified + " database row(s) successfully updated");
        } catch (Exception e) {
            printException(e);
            try {
                if (c != null) c.rollback();
            } catch (Exception e1) {
                printException(e1);
            }
            return new TextUpdateResponse(false, "Check the where clause; the EJB received a database exception running the query:\n" + e);
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (Exception e1) {
                printException(e1);
            }
            try {
                if (c != null) c.setAutoCommit(true);
            } catch (Exception e1) {
                printException(e1);
            }
            try {
                if (c != null) c.close();
            } catch (Exception e1) {
                printException(e1);
            }
        }
    }

    public TextSearchResponse runTextSearch(TextSearchRequest request) {
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            c = DBMS.getConnection();

            String tableName = request.getTableName().trim();
            final String columnName = request.getColumnName();
            final String searchText = request.getSearchText();
            String userWhereClause = request.getWhereClause().trim();

            // Build the query
            StringBuilder query = new StringBuilder();

            query.append("SELECT * FROM " + tableName + " ");
            if (userWhereClause.length() > 0) {
                query.append("WHERE " + userWhereClause);
            }


            // Run the query
            ps = c.prepareStatement(query.toString());
            rs = ps.executeQuery();
            ResultSetContainer temp = new ResultSetContainer(rs, true);
            // This weeds out the rows which don't have the search text
            ResultSetContainer rsc = new ResultSetContainer(temp, new Contains(columnName, searchText));


            return new TextSearchResponse(true, "Text search successful", rsc);
        } catch (Exception e) {
            printException(e);
            String errorMsg;
            if (e instanceof SQLException) {
                errorMsg = "Received database exception; check where clause:\n" +
                    DBMS.getSqlExceptionString((SQLException) e);
            } else {
                errorMsg = "Received exception during textt search:\n" + e;
            }
            return new TextSearchResponse(false, errorMsg, null);
        } finally {
            try {
                if (rs != null) rs.close();
            } catch (Exception e1) {
                printException(e1);
            }
            try {
                if (ps != null) ps.close();
            } catch (Exception e1) {
                printException(e1);
            }
            try {
                if (c != null) c.close();
            } catch (Exception e1) {
                printException(e1);
            }
        }
    }


    public Collection getTestCases(int componentID) throws SQLException {
        TestCase testCaseAttr = null;
        ExpectedResult expectedResultAttr = null;

        ArrayList testCaseArgs = null;

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        Object expBlobObject = null;
        int testCaseID = 0;
        int testOrder = 0;
        ArrayList blobObject = null;

        StringBuilder txtGetTestCases = new StringBuilder(150);
        txtGetTestCases.append(" SELECT component_id, test_case_id, args, expected_result, test_number ").
            append(" FROM system_test_case ").
            append(" WHERE component_id = ? ").
            append(" ORDER BY test_number, test_case_id ");

        try {
            conn = DBMS.getConnection();

            ps = conn.prepareStatement(txtGetTestCases.toString());
            ps.setInt(1, componentID);

            rs = ps.executeQuery();

            ArrayList testCases = new ArrayList();
            while (rs.next()) {
                testCaseAttr = new TestCase();
                testCaseAttr.setComponentId(rs.getInt(1));
                testCaseID = rs.getInt(2);
                testCaseAttr.setTestCaseId(testCaseID);
                testCaseAttr.setTestOrder(testOrder);

                try {
                    blobObject = (ArrayList) DBMS.getBlobObject(rs, 3);
                } catch (Exception e) {
                    printException(e);
                }

                try {
                    expBlobObject = DBMS.getBlobObject(rs, 4);
                } catch (Exception e) {
                    printException(e);
                }

                testCaseArgs = buildTestCaseArgs(blobObject, componentID, testCaseID);
                testCaseAttr.setTestCaseArgs(testCaseArgs);

                expectedResultAttr = buildExpectedResult(expBlobObject, componentID, testCaseID);

                testCaseAttr.setExpectedResult(expectedResultAttr);

                testCases.add(testCaseAttr);
                testOrder++;
            }
            return testCases;
        } catch (SQLException e) {
            printException(e);
            throw e;
        } finally {
            close(conn, ps, rs);
        }
    }

    /**
     * This constant String contains a DML String that should be used to
     * delete existing terms for specified round.
     *
     * <pre>
     *   DELETE FROM round_terms rr
     *   WHERE round id = ?
     * </pre>
     *
     * @since Admin Tool 2.0
     */
    public final static String DELETE_ROUND_TERMS_QUERY = "DELETE FROM round_terms WHERE round_id = ?";

    /**
     * This constant String contains a DML String that should be used to
     * insert terms for specified round.
     *
     * <pre>
     *   INSERT INTO round_terms VALUES (?, ?)
     * </pre>
     *
     * @since Admin Tool 2.0
     */
    public final static String INSERT_ROUND_TERMS_QUERY = "INSERT INTO round_terms VALUES (?, ?)";

    /**
     * Persists the content of terms for specified round in database using
     * specified properties to evalute content of terms. To do so reads
     * "terms.txt" file into String and replaces the references to template
     * properties in form "{property_name}" with value corresponding to
     * property name in given Hashtable. After that issues to SQL queries :
     * <code>DELETE_ROUND_TERMS_QUERY</code> and <code>INSERT_ROUND_TERMS_QUERY
     * </code>.
     *
     * @param roundID an ID of round to set terms for
     * @param params a Hashtable with property names and property values
     * @throws SQLException
     * @since Admin Tool 2.0
     */
    public void setRoundTerms(int roundID, Map params) throws SQLException {
        String agreement = null;
        try {
            String resourceName = ApplicationServer.IAGREE;
            InputStream resourceAsStream = getClass().getResourceAsStream(resourceName);
            BufferedReader in = new BufferedReader(new InputStreamReader(resourceAsStream));

            StringBuilder terms = new StringBuilder();
            String line = null;
            while ((line = in.readLine()) != null) {
                terms.append(line + "\n");
            }
            Iterator it = params.keySet().iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                String val = (String) params.get(key);
                String keyTag = "{" + key + "}";
                int index = -1;
                while((index = terms.indexOf(keyTag)) != -1) {
                    terms.delete(index, keyTag.length() + index);
                    terms.insert(index, val);
                }
            }
            agreement = terms.toString();
        } catch (IOException ioe) {
            log.error("error reading from terms.txt file", ioe);
        }
        Object[] queryParams = new Object[]{new Integer(roundID)};
        executeUpdate(DELETE_ROUND_TERMS_QUERY, queryParams);

        queryParams = new Object[]{new Integer(roundID), agreement};
        executeUpdate(INSERT_ROUND_TERMS_QUERY, queryParams);
    }

    /*****************************************************************************************/

    private static ArrayList buildTestCaseArgs(ArrayList blobObject, int componentID, int testCaseID) {
        TestCaseArg testCaseArgAttr = null;
        ArrayList argValues = null;
        ArrayList testCaseArgs = new ArrayList();
        String arrayType = "";

        for (int i = 0; i < blobObject.size(); i++) {
            argValues = new ArrayList();
            testCaseArgAttr = new TestCaseArg();
            testCaseArgAttr.setProblemId(componentID);
            testCaseArgAttr.setTestCaseId(testCaseID);
            testCaseArgAttr.setArgPosition(i);

            if (blobObject.get(i) instanceof Integer) {
                testCaseArgAttr.setArgType("Integer");
                argValues.add(blobObject.get(i));
                testCaseArgAttr.setArgValue(argValues);
            } else if (blobObject.get(i) instanceof Double) {
                testCaseArgAttr.setArgType("Double");
                argValues.add(blobObject.get(i));
                testCaseArgAttr.setArgValue(argValues);
            } else if (blobObject.get(i) instanceof String) {
                testCaseArgAttr.setArgType("String");
                argValues.add(blobObject.get(i));
                testCaseArgAttr.setArgValue(argValues);
            } else if (blobObject.get(i) instanceof Float) {
                testCaseArgAttr.setArgType("Float");
                argValues.add(blobObject.get(i));
                testCaseArgAttr.setArgValue(argValues);
            } else if (blobObject.get(i) instanceof Boolean) {
                testCaseArgAttr.setArgType("Boolean");
                argValues.add(blobObject.get(i));
                testCaseArgAttr.setArgValue(argValues);
            } else if (blobObject.get(i) instanceof Long) {
                testCaseArgAttr.setArgType("Long");
                argValues.add(blobObject.get(i));
                testCaseArgAttr.setArgValue(argValues);
            } else if (blobObject.get(i) instanceof Character) {
                testCaseArgAttr.setArgType("Character");
                argValues.add(blobObject.get(i));
                testCaseArgAttr.setArgValue(argValues);
            } else if (blobObject.get(i) instanceof ArrayList) {
                testCaseArgAttr.setArgType("ArrayList");
                argValues = (ArrayList) blobObject.get(i);
                testCaseArgAttr.setArgValue(argValues);
                testCaseArgAttr.setArgListTypes(getArgTypes(argValues, "ArrayList"));
            } else if (blobObject.get(i).getClass().isArray()) {
                arrayType = blobObject.get(i).getClass().getComponentType().toString();
                if (arrayType.equals("int")) {
                    testCaseArgAttr.setArgType("int[]");
                    argValues.add(blobObject.get(i));
                    testCaseArgAttr.setArgValue(argValues);
                } else if (arrayType.equals("class java.lang.String")) {
                    testCaseArgAttr.setArgType("String[]");
                    argValues.add(blobObject.get(i));
                    testCaseArgAttr.setArgValue(argValues);
                } else if (arrayType.equals("double")) {
                    testCaseArgAttr.setArgType("double[]");
                    argValues.add(blobObject.get(i));
                    testCaseArgAttr.setArgValue(argValues);
                } else if (arrayType.equals("float")) {
                    testCaseArgAttr.setArgType("float[]");
                    argValues.add(blobObject.get(i));
                    testCaseArgAttr.setArgValue(argValues);
                } else if (arrayType.equals("boolean")) {
                    testCaseArgAttr.setArgType("boolean[]");
                    argValues.add(blobObject.get(i));
                    testCaseArgAttr.setArgValue(argValues);
                } else if (arrayType.equals("long")) {
                    testCaseArgAttr.setArgType("long[]");
                    argValues.add(blobObject.get(i));
                    testCaseArgAttr.setArgValue(argValues);
                } else if (arrayType.equals("char")) {
                    testCaseArgAttr.setArgType("char[]");
                    argValues.add(blobObject.get(i));
                    testCaseArgAttr.setArgValue(argValues);
                } else if (arrayType.equals("byte")) {
                    testCaseArgAttr.setArgType("byte[]");
                    argValues.add(blobObject.get(i));
                    testCaseArgAttr.setArgValue(argValues);
                } else if (arrayType.equals("short")) {
                    testCaseArgAttr.setArgType("short[]");
                    argValues.add(blobObject.get(i));
                    testCaseArgAttr.setArgValue(argValues);
                }
            }

            testCaseArgs.add(testCaseArgAttr);

        }

        return testCaseArgs;

    }


    /*****************************************************************************************/

    private static ExpectedResult buildExpectedResult(Object blobObject,
                                                      int componentID, int testCaseID) {

        ExpectedResult expectedResultAttr = new ExpectedResult();
        ArrayList argValues = new ArrayList();
        String arrayType = "";

        expectedResultAttr.setProblemId(componentID);
        expectedResultAttr.setTestCaseId(testCaseID);

        if (blobObject instanceof Integer) {
            expectedResultAttr.setResultType("Integer");
            argValues.add(new Integer(blobObject.toString()));
            expectedResultAttr.setResultValue(argValues);
        } else if (blobObject instanceof Double) {
            expectedResultAttr.setResultType("Double");
            argValues.add(new Double(blobObject.toString()));
            expectedResultAttr.setResultValue(argValues);
        } else if (blobObject instanceof String) {
            expectedResultAttr.setResultType("String");
            argValues.add(new String(blobObject.toString()));
            expectedResultAttr.setResultValue(argValues);
        } else if (blobObject instanceof Float) {
            expectedResultAttr.setResultType("Float");
            argValues.add(new Float(blobObject.toString()));
            expectedResultAttr.setResultValue(argValues);
        } else if (blobObject instanceof Boolean) {
            expectedResultAttr.setResultType("Boolean");
            argValues.add(new Boolean(blobObject.toString()));
            expectedResultAttr.setResultValue(argValues);
        } else if (blobObject instanceof Long) {
            expectedResultAttr.setResultType("Long");
            argValues.add(new Long(blobObject.toString()));
            expectedResultAttr.setResultValue(argValues);
        } else if (blobObject instanceof Character) {
            expectedResultAttr.setResultType("Character");
            argValues.add(new Character((blobObject.toString()).charAt(0)));
            expectedResultAttr.setResultValue(argValues);
        } else if (blobObject instanceof ArrayList) {
            expectedResultAttr.setResultType("ArrayList");
            argValues = (ArrayList) blobObject;
            expectedResultAttr.setResultValue(argValues);
            expectedResultAttr.setArgListTypes(getArgTypes(argValues, "ArrayList"));
        } else if (blobObject.getClass().isArray()) {
            arrayType = blobObject.getClass().getComponentType().toString();
            if (arrayType.equals("int")) {
                expectedResultAttr.setResultType("int[]");
                argValues.add(blobObject);
                expectedResultAttr.setResultValue(argValues);
            } else if (arrayType.equals("class java.lang.String")) {
                expectedResultAttr.setResultType("String[]");
                argValues.add(blobObject);
                expectedResultAttr.setResultValue(argValues);
            } else if (arrayType.equals("double")) {
                expectedResultAttr.setResultType("double[]");
                argValues.add(blobObject);
                expectedResultAttr.setResultValue(argValues);
            } else if (arrayType.equals("float")) {
                expectedResultAttr.setResultType("float[]");
                argValues.add(blobObject);
                expectedResultAttr.setResultValue(argValues);
            } else if (arrayType.equals("boolean")) {
                expectedResultAttr.setResultType("boolean[]");
                argValues.add(blobObject);
                expectedResultAttr.setResultValue(argValues);
            } else if (arrayType.equals("long")) {
                expectedResultAttr.setResultType("long[]");
                argValues.add(blobObject);
                expectedResultAttr.setResultValue(argValues);
            } else if (arrayType.equals("char")) {
                expectedResultAttr.setResultType("char[]");
                argValues.add(blobObject);
                expectedResultAttr.setResultValue(argValues);
            } else if (arrayType.equals("byte")) {
                expectedResultAttr.setResultType("byte[]");
                argValues.add(blobObject);
                expectedResultAttr.setResultValue(argValues);
            } else if (arrayType.equals("short")) {
                expectedResultAttr.setResultType("short[]");
                argValues.add(blobObject);
                expectedResultAttr.setResultValue(argValues);
            }
        }

        return expectedResultAttr;

    }


    private static ArrayList getArgTypes(ArrayList arrList, String type) {

        ArrayList Matrix2DArrList = null;
        ArrayList arrTypeList = new ArrayList();

        int repeatVal = 0;

        if (type.equals("ArrayList")) {
            repeatVal = arrList.size();
        } else if (type.equals("Matrix2D")) {
            Matrix2DArrList = getArgTypes((ArrayList) arrList.get(0), "firstType");
            arrTypeList.add(Matrix2DArrList.get(0));
        } else if (type.equals("firstType")) {
            repeatVal = 1;
        }

        for (int i = 0; i < repeatVal; i++) {

            if (arrList.get(i) instanceof Integer) {
                arrTypeList.add("Integer");
            } else if (arrList.get(i) instanceof Double) {
                arrTypeList.add("Double");
            } else if (arrList.get(i) instanceof String) {
                arrTypeList.add("String");
            } else if (arrList.get(i) instanceof Float) {
                arrTypeList.add("Float");
            } else if (arrList.get(i) instanceof Boolean) {
                arrTypeList.add("Boolean");
            } else if (arrList.get(i) instanceof Long) {
                arrTypeList.add("Long");
            } else if (arrList.get(i) instanceof Character) {
                arrTypeList.add("Character");
            }

        }

        return arrTypeList;

    }

    private static void close(Connection conn, PreparedStatement ps, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            printException(e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                printException(e);
            } finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException e) {
                    printException(e);
                }
            }
        }
    }

    private void rollback(Connection conn) {
        try {
            if (conn != null)
                conn.rollback();
        } catch (SQLException e) {
            printException(e);
        }
    }

    private static void info(Object message) {
        log.info(message);
    }

    private static void debug(Object message) {
        log.debug(message);
    }

    private static final String INSERT_BACKUP_QUERY =
        "INSERT INTO backup (backup_id, round_id, timestamp, comment) VALUES (?, ?, ?, ?)";
    private static final String INSERT_BACKUP_TABLES_QUERY =
        "INSERT INTO backup_tables (backup_id, table_name) VALUES (?, ?)";

    /**
     * Creates a backup copy of specified tables for specified round. Created
     * backup copy is added to existing backup copies of this and other
     * rounds.
     *
     * To do so obtains an ID for new backup copy using newly defined
     * BACKUP_SEQ sequence, inserts records into new "backup" and "backup tables"
     * tables and insert data from given tables into corresponding staging
     * tables.
     *
     * If the backup is created without any problems then the new backup
     * id is returned.
     *
     * @param  roundID an ID of requested round
     * @param  tableNames a Set of String names of tables that should be
     *         backed up.
     * @return the id of newly created backup
     * @throws IllegalArgumentException if given Set is null.
     * @since  Admin Tool 2.0
     */
    public long backupTables(int roundID, Set tableNames, String comment) throws SQLException {
        if (tableNames == null) {
            throw new IllegalArgumentException("No tables specified to backup");
        }

        Iterator t = tableNames.iterator();
        while (t.hasNext()) {
            if (t.next() == null) {
                throw new IllegalArgumentException("Tables contain null");
            }
        }

        int backupID = getNewIDFor(DBMS.BACKUP_SEQ);

        /**
         * Construct the backup queries for specified round
         * and stick them into a hash for easy access by table name
         */
        Hashtable backupQueries = new Hashtable();
        backupQueries.put("challenge",
                          "INSERT INTO staging_challenge SELECT "
                          + "challenge_id, "
                          + "defendant_id, "
                          + "component_id, "
                          + "round_id, "
                          + "succeeded, "
                          + "submit_time, "
                          + "challenger_id, "
                          + "args, "
                          + "message, "
                          + "challenger_points, "
                          + "defendant_points, "
                          + "expected, "
                          + "received, "
                          + "status_id, "
                          + backupID
                          + " FROM challenge WHERE round_id = ?");
        /*
        backupQueries.put("rating",
                          "INSERT INTO staging_rating SELECT "
                          + "coder_id, "
                          + "round_id, "
                          + "rating, "
                          + "num_ratings, "
                          + "modify_date, "
                          + "vol, "
                          + "rating_no_vol, "
                          + backupID
                          + " FROM rating WHERE coder_id IN "
                          + "(SELECT coder_id FROM room_result WHERE round_id = ?)");*/

        backupQueries.put("component_state",
                          "INSERT INTO staging_component_state SELECT "
                          + "component_state_id, "
                          + "round_id, "
                          + "coder_id, "
                          + "component_id, "
                          + "points, "
                          + "status_id, "
                          + "language_id, "
                          + "submission_number, "
                          + backupID
                          + " FROM component_state WHERE round_id = ?");

        backupQueries.put("compilation",
                          "INSERT INTO staging_compilation SELECT "
                          + "component_state_id, "
                          + "open_time, "
                          + "compilation_text, "
                          + "compilation_class_file, "
                          + "language_id, "
                          + backupID
                          + " FROM compilation WHERE component_state_id IN "
                          + "(SELECT component_state_id FROM component_state WHERE round_id = ?)");

        backupQueries.put("submission",
                          "INSERT INTO staging_submission SELECT "
                          + "component_state_id, "
                          + "submission_number, "
                          + "submission_text, "
                          + "submission_class_file, "
                          + "open_time, "
                          + "submit_time, "
                          + "submission_points, "
                          + "language_id, "
                          + backupID
                          + " FROM submission WHERE component_state_id IN "
                          + "(SELECT component_state_id FROM component_state WHERE round_id = ?)");

        backupQueries.put("room_result",
                          "INSERT INTO staging_room_result (round_id, room_id, coder_id, point_total, room_seed, paid, old_rating, old_vol, "
                          + "new_rating, new_vol, room_placed, attended, advanced, overall_rank, division_seed, division_placed, round_payment_id, "
                          + "rated_flag, backup_id) "
                          + "SELECT "
                          + "round_id, "
                          + "room_id, "
                          + "coder_id, "
                          + "point_total, "
                          + "room_seed, "
                          + "paid, "
                          + "old_rating, "
                          + "old_vol, "
                          + "new_rating, "
                          + "new_vol, "
                          + "room_placed, "
                          + "attended, "
                          + "advanced, "
                          + "overall_rank, "
                          + "division_seed, "
                          + "division_placed, "
                          + "round_payment_id, "
                          + "rated_flag, "
                          + backupID
                          + " FROM room_result WHERE round_id = ?");
        /*
        backupQueries.put("system_test_result",
                          "INSERT INTO staging_system_test_result SELECT "
                          + "coder_id, "
                          + "round_id, "
                          + "component_id, "
                          + "test_case_id, "
                          + "num_iterations, "
                          + "processing_time, "
                          + "deduction_amount, "
                          + "timestamp, "
                          + "viewable, "
                          + "received, "
                          + "succeeded, "
                          + "message, "
                          + backupID
                          + ", failure_type_id "
                          + " FROM system_test_result WHERE round_id = ?");*/

        backupQueries.put("tc_algo_rating",
                          "INSERT INTO staging_algo_rating SELECT "
                          + "coder_id, "
                          + "rating, "
                          + "vol, "
                          + "round_id, "
                          + "num_ratings, "
                          + "algo_rating_type_id, "
                          + "modify_date, "
                          + backupID
                          + " FROM algo_rating WHERE coder_id in "
                          + "(select coder_id from room_result where round_id = ?) AND algo_rating_type_id = 1");

        backupQueries.put("tchs_algo_rating",
                          "INSERT INTO staging_algo_rating SELECT "
                          + "coder_id, "
                          + "rating, "
                          + "vol, "
                          + "round_id, "
                          + "num_ratings, "
                          + "algo_rating_type_id, "
                          + "modify_date, "
                          + backupID
                          + " FROM algo_rating WHERE coder_id in "
                          + "(select coder_id from room_result where round_id = ?) AND algo_rating_type_id = 2");

        backupQueries.put("mm_algo_rating",
                          "INSERT INTO staging_algo_rating SELECT "
                          + "coder_id, "
                          + "rating, "
                          + "vol, "
                          + "round_id, "
                          + "num_ratings, "
                          + "algo_rating_type_id, "
                          + "modify_date, "
                          + backupID
                          + " FROM algo_rating WHERE coder_id in "
                          + "(select coder_id from long_comp_result where round_id = ?) AND algo_rating_type_id = 3");

        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBMS.getConnection();
            conn.setAutoCommit(false);

            ps = conn.prepareStatement(INSERT_BACKUP_QUERY);
            ps.setInt(1, backupID);
            ps.setInt(2, roundID);
            ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            ps.setString(4, comment + ""); // just in case comment is null the whole thing shouldn't fail
            ps.executeUpdate();
            ps.close();

            ps = conn.prepareStatement(INSERT_BACKUP_TABLES_QUERY);
            ps.setInt(1, backupID);

            Iterator i = tableNames.iterator();
            while (i.hasNext()) {
                Object tableName = i.next();
                log.info("backing up table " + tableName);
                // backup the actual data
                PreparedStatement backup_ps = conn.prepareStatement(backupQueries.get(tableName).toString());
                backup_ps.setInt(1, roundID);
                backup_ps.executeUpdate();
                backup_ps.close();

                // register that specified table has been backed up
                ps.setString(2, tableName.toString());
                ps.executeUpdate();
            }
            ps.close();

            conn.commit();
            return backupID;
        } catch (SQLException e) {
            rollback(conn);
            printException(e);
            throw e;
        } finally {
            close(conn, null, null);
        }
    }

    private static final String GET_BACKUP_COPIES_QUERY = "SELECT "
        + " b.backup_id, "
        + " b.timestamp, "
        + " b.comment, "
        + " t.table_name "
        + " FROM backup AS b LEFT OUTER JOIN backup_tables AS t ON t.backup_id = b.backup_id "
        + " WHERE b.round_id = ? "
        + " ORDER BY b.backup_id";

    /**
     * Gets the list of existing backup copies for specified round.
     *
     * @param  roundID an ID of requested round.
     * @return a List of BackupCopy objects representing existing backup copies
     *         for specified round.
     * @throws SQLException if any SQL Error occurs or given round does not
     *         exist
     * @since  Admin Tool 2.0
     * @see    BackupCopy
     */
    public List getBackupCopies(int roundID) throws SQLException {
        Connection conn = null;
        ResultSet result = null;
        PreparedStatement ps = null;

        try {
            conn = DBMS.getConnection();
            ps = conn.prepareStatement(GET_BACKUP_COPIES_QUERY);
            ps.setInt(1, roundID);
            result = ps.executeQuery();
            Vector copies = new Vector();
            BackupCopy currentCopy = null;
            /**
             * What we are doing here is pretty straightforward:
             * If the current row refers to the same backup copy as the previous one
             * then we simply add the table to the currentCopy object. If it refers to a new
             * backup copy we store the add object in the copies vector and create a new one.
             * We continue until there are no more rows left.
             */
            while (result.next()) {
                if (currentCopy == null || currentCopy.getID() != result.getInt(1)) {
                    if (currentCopy != null) {
                        copies.add(currentCopy);
                    }
                    currentCopy = new BackupCopy(result.getInt(1), roundID, result.getTimestamp(2), result.getString(3));
                    // it may be the case that a backup copy has no tables even though this is
                    // not supposed to happen
                    if (result.getString(4) != null) {
                        currentCopy.addTableName(result.getString(4));
                    }
                } else {
                    currentCopy.addTableName(result.getString(4));
                }
            }
            // make sure the last object is added to the List
            if (currentCopy != null) {
                copies.add(currentCopy);
            }

            return copies;
        } catch (SQLException e) {
            printException(e);
            throw e;
        } finally {
            close(conn, ps, result);
        }
    }


    private static final String GET_BACKUP_ROUND_ID_AND_TABLES_QUERY = // 1 arg
        "SELECT b.round_id, t.table_name " +
        "FROM backup AS b LEFT OUTER JOIN backup_tables AS t ON t.backup_id = b.backup_id " +
        "WHERE b.backup_id = ?";

//    private static final String RESTORE_CHALLENGE_QUERY = // 12 args
//        "update challenge set " +
//        "succeeded     = " +
//        "( select sc.succeeded from staging_challenge sc where sc.round_id = challenge.round_id and sc.challenger_id = challenge.challenger_id " +
//        "  and sc.defendant_id = challenge.defendant_id and sc.problem_id = challenge.component_id and sc.challenge_id = challenge.challenge_id and sc.backup_id = ?) " +
//        ",submit_time   = " +
//        "( select sc.submit_time from staging_challenge sc where sc.round_id = challenge.round_id and sc.challenger_id = challenge.challenger_id " +
//        " and sc.defendant_id = challenge.defendant_id and sc.problem_id = challenge.component_id and sc.challenge_id = challenge.challenge_id and sc.backup_id = ?) " +
//        ",args          = " +
//        "( select sc.args from staging_challenge sc where sc.round_id = challenge.round_id and sc.challenger_id = challenge.challenger_id " +
//        "  and sc.defendant_id = challenge.defendant_id and sc.problem_id = challenge.component_id and sc.challenge_id = challenge.challenge_id and sc.backup_id = ?) " +
//        ",message       = " +
//        "( select sc.message from staging_challenge sc where sc.round_id = challenge.round_id and sc.challenger_id = challenge.challenger_id " +
//        "  and sc.defendant_id = challenge.defendant_id and sc.problem_id = challenge.component_id and sc.challenge_id = challenge.challenge_id and sc.backup_id = ?) " +
//        ",challenger_points = " +
//        "( select sc.challenger_points from staging_challenge sc where sc.round_id = challenge.round_id and sc.challenger_id = challenge.challenger_id " +
//        "  and sc.defendant_id = challenge.defendant_id and sc.problem_id = challenge.component_id and sc.challenge_id = challenge.challenge_id and sc.backup_id = ?) " +
//        ",defendant_points  = " +
//        "( select sc.defendant_points from staging_challenge sc where sc.round_id = challenge.round_id and sc.challenger_id = challenge.challenger_id " +
//        "  and sc.defendant_id = challenge.defendant_id and sc.problem_id = challenge.component_id and sc.challenge_id = challenge.challenge_id and sc.backup_id = ?) " +
//        ",expected          = " +
//        "( select sc.expected from staging_challenge sc where sc.round_id = challenge.round_id and sc.challenger_id = challenge.challenger_id " +
//        "  and sc.defendant_id = challenge.defendant_id and sc.problem_id = challenge.component_id and sc.challenge_id = challenge.challenge_id and sc.backup_id = ?) " +
//        ",received          = " +
//        "( select sc.received from staging_challenge sc where sc.round_id = challenge.round_id and sc.challenger_id = challenge.challenger_id " +
//        "  and sc.defendant_id = challenge.defendant_id and sc.problem_id = challenge.component_id and sc.challenge_id = challenge.challenge_id and sc.backup_id = ?) " +
//        ",status_id         = " +
//        "( select sc.status_id from staging_challenge sc where sc.round_id = challenge.round_id and sc.challenger_id = challenge.challenger_id " +
//        "  and sc.defendant_id = challenge.defendant_id and sc.problem_id = challenge.component_id and sc.challenge_id = challenge.challenge_id and sc.backup_id = ?) " +
//        "where exists " +
//        "( select 2 from staging_challenge sc where sc.round_id = challenge.round_id and sc.challenger_id = challenge.challenger_id " +
//        "  and sc.defendant_id = challenge.defendant_id and sc.problem_id = challenge.component_id and sc.challenge_id = challenge.challenge_id and sc.backup_id = ?) ";

    private static final String RESTORE_RATING_QUERY = // 7 args
        "update rating set " +
        "round_id = " +
        "(select round_id from staging_rating where staging_rating.coder_id = rating.coder_id and staging_rating.backup_id = ?) " +
        ",rating  = " +
        "(select rating from staging_rating where staging_rating.coder_id = rating.coder_id and staging_rating.backup_id = ?) " +
        ",last_rated_event = " +
        "(select last_rated_event from staging_rating where staging_rating.coder_id = rating.coder_id and staging_rating.backup_id = ?) " +
        ",num_ratings = " +
        "(select num_ratings from staging_rating where staging_rating.coder_id = rating.coder_id and staging_rating.backup_id = ?) " +
        ",vol = " +
        "(select vol from staging_rating where staging_rating.coder_id= rating.coder_id and staging_rating.backup_id = ?) " +
        ",rating_no_vol = " +
        "(select rating_no_vol from staging_rating where staging_rating.coder_id = rating.coder_id and staging_rating.backup_id = ?) " +
        "where rating.coder_id in (select staging_rating.coder_id from staging_rating where staging_rating.backup_id = ?) ";

    private static final String RESTORE_RATING_QUERY_2 = // 2 args
        "update  room_result set " +
        "rated_flag  = " +
        "    ( select rr2.rated_flag from staging_room_result rr2 where room_result.round_id = rr2.round_id " +
        "        and room_result.room_id = rr2.room_id and room_result.coder_id = rr2.coder_id and rr2.backup_id = ?) " +
        "where exists " +
        "    ( select 1 from staging_room_result rr2 where room_result.round_id = rr2.round_id " +
        "     and room_result.room_id = rr2.room_id and room_result.coder_id = rr2.coder_id and rr2.backup_id = ?) ";

    private static final String RESTORE_COMPONENT_STATE_QUERY = // 8 args
        "update component_state set " +
        "round_id          = " +
        " (select sps.round_id from staging_component_state sps where sps.component_state_id = component_state.component_state_id and sps.backup_id = ?) " +
        ",coder_id          = " +
        "(select sps.coder_id from staging_component_state sps where sps.component_state_id = component_state.component_state_id and sps.backup_id = ?) " +
        ",component_id      = " +
        "(select sps.component_id from staging_component_state sps where sps.component_state_id = component_state.component_state_id and sps.backup_id = ?) " +
        ",points            = " +
        "(select sps.points from staging_component_state sps where sps.component_state_id = component_state.component_state_id and sps.backup_id = ?) " +
        ",status_id         = " +
        "(select sps.status_id from staging_component_state sps where sps.component_state_id = component_state.component_state_id and sps.backup_id = ?) " +
        ",language_id       = " +
        "(select sps.language_id from staging_component_state sps where sps.component_state_id = component_state.component_state_id and sps.backup_id = ?) " +
        ",submission_number = " +
        "(select sps.submission_number from staging_component_state sps where sps.component_state_id = component_state.component_state_id and sps.backup_id = ?) " +
        "where exists " +
        "(select sps.round_id from staging_component_state sps where sps.component_state_id = component_state.component_state_id and sps.backup_id = ?) ";


//    private static final String RESTORE_COMPILATION_QUERY = // 4 args
//        "update compilation set " +
//        "open_time    = " +
//        "( select sc.open_time from staging_compilation sc where sc.component_state_id = compilation.component_state_id and sc.backup_id = ?) " +
//        ",compilation_text = " +
//        "( select sc.compilation_text from staging_compilation sc where sc.component_state_id = compilation.component_state_id and sc.backup_id = ?) " +
//        ",compilation_class_file = " +
//        "( select sc.compilation_class_file from staging_compilation sc where sc.component_state_id = compilation.component_state_id and sc.backup_id = ?) " +
//        "where exists " +
//        "( select sc.component_state_id from staging_compilation sc where sc.component_state_id = compilation.component_state_id and sc.backup_id = ?) ";
//
//    private static final String RESTORE_SUBMISSION_QUERY = // 3 args
//        "update submission set " +
//        "submission_number = " +
//        "(select ss.submission_number from staging_submission ss where ss.component_state_id = submission.component_state_id and ss.backup_id = ?) " +
//        ",submission_text   = " +
//        "(select ss.submission_text from staging_submission ss where ss.component_state_id = submission.component_state_id and ss.backup_id = ?) " +
//        ",submission_class_file = " +
//        "(select ss.submission_class_file from staging_submission ss where ss.component_state_id = submission.component_state_id and ss.backup_id = ?) "+
//        "where exists " +
//        "( select ss.component_state_id from staging_submission_ ss where ss.component_state_id = submission.component_state_id and ss.backup_id = ?) ";


    private static final String RESTORE_ROOM_RESULT_QUERY = // 13 args
        "update room_result set " +
        "point_total = " +
        "    ( select rr2.point_total from staging_room_result rr2 where room_result.round_id = rr2.round_id " +
        "        and room_result.room_id = rr2.room_id and room_result.coder_id = rr2.coder_id and rr2.backup_id = ?) " +
        ",room_seed   = " +
        "    ( select rr2.room_seed from staging_room_result rr2 where room_result.round_id = rr2.round_id " +
        "        and room_result.room_id = rr2.room_id and room_result.coder_id = rr2.coder_id and rr2.backup_id = ?) " +
        ",paid       = " +
        "    ( select rr2.paid from staging_room_result rr2 where room_result.round_id = rr2.round_id " +
        "        and room_result.room_id = rr2.room_id and room_result.coder_id = rr2.coder_id and rr2.backup_id = ?) " +
        ",old_rating = " +
        "    ( select rr2.old_rating from staging_room_result rr2 where room_result.round_id = rr2.round_id " +
        "        and room_result.room_id = rr2.room_id and room_result.coder_id = rr2.coder_id and rr2.backup_id = ?) " +
        ",old_vol = " +
        "    ( select rr2.old_vol from staging_room_result rr2 where room_result.round_id = rr2.round_id " +
        "        and room_result.room_id = rr2.room_id and room_result.coder_id = rr2.coder_id and rr2.backup_id = ?) " +
        ",new_rating = " +
        "    ( select rr2.new_rating from staging_room_result rr2 where room_result.round_id = rr2.round_id " +
        "        and room_result.room_id = rr2.room_id and room_result.coder_id = rr2.coder_id and rr2.backup_id = ?) " +
        ",new_vol = " +
        "    ( select rr2.new_vol from staging_room_result rr2 where room_result.round_id = rr2.round_id " +
        "        and room_result.room_id = rr2.room_id and room_result.coder_id = rr2.coder_id and rr2.backup_id = ?) " +
        ",room_placed = " +
        "    ( select rr2.room_placed from staging_room_result rr2 where room_result.round_id = rr2.round_id " +
        "        and room_result.room_id = rr2.room_id and room_result.coder_id = rr2.coder_id and rr2.backup_id = ?) " +
        ",attended   = " +
        "    ( select rr2.attended from staging_room_result rr2 where room_result.round_id = rr2.round_id " +
        "        and room_result.room_id = rr2.room_id and room_result.coder_id = rr2.coder_id and rr2.backup_id = ?) " +
        ",advanced  = " +
        "    ( select rr2.advanced from staging_room_result rr2 where room_result.round_id = rr2.round_id " +
        "        and room_result.room_id = rr2.room_id and room_result.coder_id = rr2.coder_id and rr2.backup_id = ?) " +
        ",overall_rank    = " +
        "    ( select rr2.overall_rank from staging_room_result rr2 where room_result.round_id = rr2.round_id " +
        "        and room_result.room_id = rr2.room_id and room_result.coder_id = rr2.coder_id and rr2.backup_id = ?) " +
        ",division_seed  = " +
        "    ( select rr2.division_seed from staging_room_result rr2 where room_result.round_id = rr2.round_id " +
        "        and room_result.room_id = rr2.room_id and room_result.coder_id = rr2.coder_id and rr2.backup_id = ?) " +
        ",division_placed  = " +
        "    ( select rr2.division_placed from staging_room_result rr2 where room_result.round_id = rr2.round_id " +
        "        and room_result.room_id = rr2.room_id and room_result.coder_id = rr2.coder_id and rr2.backup_id = ?) " +
        ",rated_flag  = " +
        "    ( select rr2.rated_flag from staging_room_result rr2 where room_result.round_id = rr2.round_id " +
        "        and room_result.room_id = rr2.room_id and room_result.coder_id = rr2.coder_id and rr2.backup_id = ?) " +
        "where exists " +
        "    ( select 1 from staging_room_result rr2 where room_result.round_id = rr2.round_id " +
        "        and room_result.room_id = rr2.room_id and room_result.coder_id = rr2.coder_id and rr2.backup_id = ?) ";

    private static final String DELETE_SYSTEM_TEST_RESULT_QUERY = // 1 arg
        "DELETE FROM system_test_result WHERE round_id = ? ";

    private static final String RESTORE_SYSTEM_TEST_RESULT_QUERY = // 1 arg
        "INSERT INTO system_test_result " +
        "(coder_id, " +
        "round_id, " +
        "component_id, " +
        "test_case_id, " +
        "num_iterations, " +
        "processing_time, " +
        "deduction_amount, " +
        "timestamp, " +
        "viewable, " +
        "received, " +
        "succeeded, " +
        "message, " +
        "failure_type_id) SELECT " +
        "coder_id, " +
        "round_id, " +
        "problem_id, " +
        "test_case_id, " +
        "num_iterations, " +
        "processing_time, " +
        "deduction_amount, " +
        "timestamp, " +
        "viewable, " +
        "received, " +
        "succeeded, " +
        "message," +
        "failure_type_id " +
        "FROM staging_system_test_result WHERE backup_id = ? ";

    /**
     * Restores specified tables for some round from specified backup copy.
     *
     * @param  backupID an ID of requested backup copy to restore tables from
     * @param  tableNames a Set of String names of tables that should be
     *         restored.
     * @throws SQLException if any SQL Error occurs or backup copy with given
     *         id does not exist or round with ID specified to given backup
     *         copy does not exist
     * @throws IllegalArgumentException if given Set is null.
     * @since  Admin Tool 2.0
     */
    public void restoreTables(int backupID, Set tableNames) throws SQLException {
        if (tableNames == null) {
            throw new IllegalArgumentException("No tables specified to restore");
        }

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet result = null;
        Statement statement = null;

        int roundID;
        boolean nonExistentBackupId = true;
        Vector availableTableNames = new Vector();
        try {
            conn = DBMS.getConnection();
            // first retrieve the tables included in this backup
            ps = conn.prepareStatement(GET_BACKUP_ROUND_ID_AND_TABLES_QUERY);
            ps.setInt(1, backupID);
            result = ps.executeQuery();
            while (result.next()) {
                nonExistentBackupId = false;
                if (result.getString(2) != null) { // just in case there are no tables in the backup
                    availableTableNames.add(result.getString(2));
                }
            }

            if (nonExistentBackupId) { // check that the backup exists even if it has no tables
                throw new IllegalArgumentException("attempted to restore non existing backup id " + backupID);
            }

            // the round_id is the same for all rows hence we only need read it from the last one
            roundID = result.getInt(1);
        } catch (SQLException e) {
            rollback(conn);
            printException(e);
            throw e;
        } finally {
            close(conn, ps, result);
        }

        /**
         * Restore every table that we are asked to restore
         * and is available in specified backup copy
         * i.e. tableName is in tableNames and availableTableNames
         */
        try {
            conn = DBMS.getConnection();
            conn.setAutoCommit(false);

            Iterator iter = availableTableNames.iterator();
            while (iter.hasNext()) {
                Object tableName = iter.next();
                if (tableNames.contains(tableName)) {
                    info("restoring table " + tableName + " for round " + roundID);
                    if (tableName.equals("system_test_result")) {
                        ps = conn.prepareStatement(DELETE_SYSTEM_TEST_RESULT_QUERY);
                        ps.setInt(1, roundID);
                        ps.executeUpdate();
                        ps.close();

                        ps = conn.prepareStatement(RESTORE_SYSTEM_TEST_RESULT_QUERY);
                        ps.setInt(1, backupID);
                        ps.executeUpdate();
                        ps.close();
                    } else if (tableName.equals("challenge")) {
                        //Had to do it this way bc prepared Statements were having problems.
                        statement = conn.createStatement();
                        statement.execute(
                                          "update challenge set " +
                                          "succeeded     = " +
                                          "( select sc.succeeded from staging_challenge sc where sc.round_id = challenge.round_id and sc.challenger_id = challenge.challenger_id " +
                                          "  and sc.defendant_id = challenge.defendant_id and sc.problem_id = challenge.component_id and sc.challenge_id = challenge.challenge_id and sc.backup_id = " +backupID+ " ) "+
                                          ",submit_time   = " +
                                          "( select sc.submit_time from staging_challenge sc where sc.round_id = challenge.round_id and sc.challenger_id = challenge.challenger_id " +
                                          " and sc.defendant_id = challenge.defendant_id and sc.problem_id = challenge.component_id and sc.challenge_id = challenge.challenge_id and sc.backup_id = " +backupID+ " ) "+
                                          ",args          = " +
                                          "( select sc.args from staging_challenge sc where sc.round_id = challenge.round_id and sc.challenger_id = challenge.challenger_id " +
                                          "  and sc.defendant_id = challenge.defendant_id and sc.problem_id = challenge.component_id and sc.challenge_id = challenge.challenge_id and sc.backup_id = " +backupID+ " ) "+
                                          ",message       = " +
                                          "( select sc.message from staging_challenge sc where sc.round_id = challenge.round_id and sc.challenger_id = challenge.challenger_id " +
                                          "  and sc.defendant_id = challenge.defendant_id and sc.problem_id = challenge.component_id and sc.challenge_id = challenge.challenge_id and sc.backup_id = " +backupID+ " ) "+
                                          ",challenger_points = " +
                                          "( select sc.challenger_points from staging_challenge sc where sc.round_id = challenge.round_id and sc.challenger_id = challenge.challenger_id " +
                                          "  and sc.defendant_id = challenge.defendant_id and sc.problem_id = challenge.component_id and sc.challenge_id = challenge.challenge_id and sc.backup_id = " +backupID+ " ) "+
                                          ",defendant_points  = " +
                                          "( select sc.defendant_points from staging_challenge sc where sc.round_id = challenge.round_id and sc.challenger_id = challenge.challenger_id " +
                                          "  and sc.defendant_id = challenge.defendant_id and sc.problem_id = challenge.component_id and sc.challenge_id = challenge.challenge_id and sc.backup_id = " +backupID+ " ) "+
                                          ",expected          = " +
                                          "( select sc.expected from staging_challenge sc where sc.round_id = challenge.round_id and sc.challenger_id = challenge.challenger_id " +
                                          "  and sc.defendant_id = challenge.defendant_id and sc.problem_id = challenge.component_id and sc.challenge_id = challenge.challenge_id and sc.backup_id = " +backupID+ " ) "+
                                          ",received          = " +
                                          "( select sc.received from staging_challenge sc where sc.round_id = challenge.round_id and sc.challenger_id = challenge.challenger_id " +
                                          "  and sc.defendant_id = challenge.defendant_id and sc.problem_id = challenge.component_id and sc.challenge_id = challenge.challenge_id and sc.backup_id = " +backupID+ " ) "+
                                          ",status_id         = " +
                                          "( select sc.status_id from staging_challenge sc where sc.round_id = challenge.round_id and sc.challenger_id = challenge.challenger_id " +
                                          "  and sc.defendant_id = challenge.defendant_id and sc.problem_id = challenge.component_id and sc.challenge_id = challenge.challenge_id and sc.backup_id = " +backupID+ " ) "+
                                          "where exists " +
                                          "( select 2 from staging_challenge sc where sc.round_id = challenge.round_id and sc.challenger_id = challenge.challenger_id " +
                                          "  and sc.defendant_id = challenge.defendant_id and sc.problem_id = challenge.component_id and sc.challenge_id = challenge.challenge_id and sc.backup_id = " +backupID+ " ) "
                                          );
                    } else if (tableName.equals("rating")) {
                        ps = conn.prepareStatement(RESTORE_RATING_QUERY);
                        for (int i = 1; i <= 7; i++) {
                            ps.setInt(i, backupID);
                        }
                        ps.executeUpdate();
                        ps.close();

                        ps = conn.prepareStatement(RESTORE_RATING_QUERY_2);
                        for (int i = 1; i <= 2; i++) {
                            ps.setInt(i, backupID);
                        }
                        ps.executeUpdate();
                        ps.close();
                    } else if (tableName.equals("component_state")) {
                        ps = conn.prepareStatement(RESTORE_COMPONENT_STATE_QUERY);
                        for (int i = 1; i <= 8; i++) {
                            ps.setInt(i, backupID);
                        }
                        ps.executeUpdate();
                        ps.close();
                    } else if (tableName.equals("compilation")) {
                        //Had to do it this way bc prepared Statements were having problems.
                        statement = conn.createStatement();
                        statement.execute(
                                          "update compilation set " +
                                          "open_time    = " +
                                          "( select sc.open_time from staging_compilation sc where sc.component_state_id = compilation.component_state_id and sc.backup_id = " +backupID+ " ) "+
                                          ",compilation_text = " +
                                          "( select sc.compilation_text from staging_compilation sc where sc.component_state_id = compilation.component_state_id and sc.backup_id = " +backupID+ " ) "+
                                          ",language_id = " +
                                          "( select sc.language_id from staging_compilation sc where sc.component_state_id = compilation.component_state_id and sc.backup_id = " +backupID+ " ) "+
                                          "where exists " +
                                          "( select sc.component_state_id from staging_compilation sc where sc.component_state_id = compilation.component_state_id and sc.backup_id = " +backupID+ " ) "
                                          );
                    } else if (tableName.equals("submission")) {
                        //Had to do it this way bc prepared Statements were having problems.
                        statement = conn.createStatement();
                        statement.execute(
                                          "update submission set " +
                                          "open_time    = " +
                                          "( select ss.open_time from staging_submission ss where ss.component_state_id = submission.component_state_id and ss.backup_id = " +backupID+ " ) "+
                                          ",submission_text = " +
                                          "( select ss.submission_text from staging_submission ss where ss.component_state_id = submission.component_state_id and ss.backup_id = " +backupID+ " ) "+
                                          ",language_id = " +
                                          "( select ss.language_id from staging_submission ss where ss.component_state_id = submission.component_state_id and ss.backup_id = " +backupID+ " ) "+
                                          ",submit_time = " +
                                          "( select ss.submit_time from staging_submission ss where ss.component_state_id = submission.component_state_id and ss.backup_id = " +backupID+ " ) "+
                                          ",submission_points = " +
                                          "( select ss.language_id from staging_submission ss where ss.component_state_id = submission.component_state_id and ss.backup_id = " +backupID+ " ) "+
                                          "where exists " +
                                          "( select ss.component_state_id from staging_submission ss where ss.component_state_id = submission.component_state_id and ss.backup_id = " +backupID+ " ) "
                                          );
                    } else if (tableName.equals("room_result")) {
                        ps = conn.prepareStatement(RESTORE_ROOM_RESULT_QUERY);
                        for (int i = 1; i <= 13; i++) {
                            ps.setInt(i, backupID);
                        }
                        ps.executeUpdate();
                        ps.close();
                    } else if (tableName.equals("system_test_result")) {
                        ps = conn.prepareStatement(DELETE_SYSTEM_TEST_RESULT_QUERY);
                        ps.setInt(1, roundID);
                        ps.executeUpdate();
                        ps.close();

                        ps = conn.prepareStatement(RESTORE_SYSTEM_TEST_RESULT_QUERY);
                        ps.setInt(1, backupID);
                        ps.executeUpdate();
                        ps.close();
                    }
                }
            }
            conn.commit();
        } catch (SQLException e) {
            rollback(conn);
            printException(e);
            throw e;
        } finally {
            close(conn, null, null);
        }

    }

    public void clearCache() throws RemoteException {
        CacheClient cc = CacheClientFactory.createCacheClient();
        cc.clearCache();
    }

    public String generateTemplate(int roundID) throws SQLException {
        String ret = "";
        Connection conn = null;

        try {
            conn = DBMS.getConnection(DBMS.DW_DATASOURCE_NAME);
            ret = MatchSummaryHTML.run(conn, roundID);
        } catch (SQLException e) {
            rollback(conn);
            printException(e);
            throw e;
        } finally {
            close(conn, null, null);
        }


        return ret;
    }

    /**
     * Performs the warehouse data load process specified by name of class
     * extending TCLoad class and a Hashtable containing neceessary
     * parameters. This method performs the initialization of TCLoad subclass
     * similar to <code>TCLoadUtility.runTCLoad()</code> and if it goes
     * successfully then performs the warehouse data load process with
     * <code>TCLoadUtility.doLoad(TCLoad)</code> method.
     *
     * @param tcLoadClass a TCLoad extending class that should be used to
     * perform a warehouse data load
     * @param params a Hashtable mapping the String parameter names to String
     * values of parameters
     * @throws IllegalArgumentException if any parameter is null
     * @throws ClassNotFoundException if the class specified by tcLoadClass can not be found
     * @throws SQLException if the load fails to be executed by TCLoadUtility.doLoad()
     * @since Admin Tool 2.0
     * @see   TCLoad
     * @see   TCLoadUtility
     */
    public void loadWarehouseData(String tcLoadClass, Map params, int type) throws ClassNotFoundException, SQLException {
        StringBuilder sErrorMsg = new StringBuilder();

        String source = "java:InformixDS";
        String target = "java:DW";

        switch(type) {
        case AdminConstants.REQUEST_WAREHOUSE_LOAD_AGGREGATE:
            source = "java:DW";
            target = "java:DW";
            break;
        case AdminConstants.REQUEST_WAREHOUSE_LOAD_CODER:
            source = "java:InformixDS";
            target = "java:DW";
            break;
        case AdminConstants.REQUEST_WAREHOUSE_LOAD_RANK:
            source = "java:DW";
            target = "java:DW";
            break;
        case AdminConstants.REQUEST_WAREHOUSE_LOAD_ROUND:
            source = "java:InformixDS";
            target = "java:DW";
        default:
            break;
        }

        if (tcLoadClass == null) {
            throw new IllegalArgumentException("Please specify a load to run");
        }

        if (params == null) {
            throw new IllegalArgumentException("Null params for load");
        }

        Class loadme = null;
        try {
            loadme = Class.forName(tcLoadClass);
        } catch (Exception ex) {
            sErrorMsg.setLength(0);
            sErrorMsg.append("Unable to load class for load: ");
            sErrorMsg.append(tcLoadClass);
            sErrorMsg.append(". Cannot continue.\n");
            sErrorMsg.append(ex.getMessage());
            throw new ClassNotFoundException(sErrorMsg.toString());
        }

        Object ob = null;
        try {
            ob = loadme.newInstance();
            if (ob == null) {
                throw new Exception("Object is null after newInstance call.");
            }
        } catch (Exception ex) {
            sErrorMsg.setLength(0);
            sErrorMsg.append("Unable to create new instance of class for load: ");
            sErrorMsg.append(tcLoadClass);
            sErrorMsg.append(". Cannot continue.\n");
            sErrorMsg.append(ex.getMessage());
            throw new IllegalArgumentException(sErrorMsg.toString());
        }

        if (!(ob instanceof TCLoad)) {
            sErrorMsg.setLength(0);
            sErrorMsg.append(tcLoadClass + " is not an instance of TCLoad. You must ");
            sErrorMsg.append("extend TCLoad to create a TopCoder database load.");
            throw new IllegalArgumentException(sErrorMsg.toString());
        }

        TCLoad load = (TCLoad) ob;
        if (!load.setParameters(new Hashtable(params))) {
            sErrorMsg.setLength(0);
            sErrorMsg.append("set params failed:" + load.getReasonFailed());
            throw new IllegalArgumentException(sErrorMsg.toString());
        }

        try {
            TCLoadUtility.doLoad(load, source, target);
        } catch(Exception e) {
            e.printStackTrace();
            throw new SQLException("Failed to do load: " + e.getMessage());
        }

    }

    public CommandResponse setForumID(int roundID, int forumID) throws SQLException {
        Connection conn = DBMS.getConnection();
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement("UPDATE round SET forum_id = ? WHERE round_id = ?");
            ps.setInt(1, forumID);
            ps.setInt(2, roundID);
            if (ps.executeUpdate() != 1) {
                throw new SQLException("Setting forum ID for round " + roundID + " failed.");
            }
            return new CommandSucceededResponse("Forum " + forumID + " is set successfully for round " + roundID);
        } finally {
            close(conn, ps, null);
        }
    }
}
