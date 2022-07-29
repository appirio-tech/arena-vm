package com.topcoder.client.contestMonitor.model;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Set;

import com.topcoder.server.AdminListener.request.SecurityManagementRequest;
import com.topcoder.server.contest.AnswerData;
import com.topcoder.server.contest.ContestData;
import com.topcoder.server.contest.ImportantMessageData;
import com.topcoder.server.contest.RoundEventData;
import com.topcoder.server.contest.RoundLanguageData;
import com.topcoder.server.contest.QuestionData;
import com.topcoder.server.contest.RoundData;
import com.topcoder.server.contest.RoundRoomAssignment;
import com.topcoder.server.contest.RoundSegmentData;
import com.topcoder.server.contest.SurveyData;
import com.topcoder.server.listener.monitor.CachedItem;
import com.topcoder.server.listener.monitor.ChatItem;
import com.topcoder.server.util.logging.net.StreamID;

/**
 * Modifications for AdminTool 2.0 are :
 * <p>New <code>sendGetID(int)</code> method added to meet the
 * "1.2.1 Using Sequences when creating rounds" requirement.
 * <p>New <code>sendSaveRoundRoomAssignment(RoundRoomAssignment)</code> method
 * added to meet the "1.2.5 Auto Room Assignments" requirement.
 * <p>New <code>sendSecurityRequest(int)</code> and <code>sendGetPrincipals(int)
 * "1.2.12 Security Object update" requirement.
 *
 * <p>
 * Changes in version 1.0 (TopCoder Competition Engine - Event Support For Registration v1.0):
 * <ol>
 * <li>Added {@link #sendSetEvents(RoundEventData)}  method to send the setting round event data.</li>
 * </ol>
 * </p>
 * @author TCDEVELOPER
 */
public interface CommandSender {

    ConnectionItem getConnection(int serverId, int id);

    void shutdownAllListeners();

    void disconnectAppletClient(int serverId, int connId);

    void sendGetQueueInfoRequest(String queueName);

    void sendClearTestCasesCommand(int roundID);

    void sendRefreshRegCommand(int roundID);

    void sendSystemTestCommand(int roundID, int coderID, int problemID, boolean failOnFirstBadTest, boolean reference);

    void sendCancelSystemTestCaseTestingCommand(int roundID, int testCaseId);

    void sendUpdatePlaceCommand(int roundID);

    void sendEndContestCommand(int roundID);

    void sendEndHSContestCommand(int roundID);

    void sendGenerateTemplateCommand(int roundID);

    void sendClearCacheCommand();

    void sendRefreshProbsCommand(int roundID);

    void sendRefreshRoom(int roundID, int roomID);

    void sendRefreshAllRooms(int roundID);

    void sendRoundForwarder(String host, int port, boolean enable);

    void sendShowSpecResults();

    void sendRestoreRound(int roundID);

    void sendRefreshBroadcasts(int roundID);

    void sendClearPracticeRooms(int type);

    void sendGlobalBroadcast(String message);

    void sendComponentBroadcast(int roundID, String message, int problemId);

    void sendRoundBroadcast(int roundID, String message);

    void sendAddTime(int roundID, int minutes, int seconds, int phase, boolean addToStart);

    void sendAssignRooms(int roundID, int codersPerRoom, int type, boolean isByDivision,
                         boolean isFinal, boolean isByRegion, double p);

    void sendSetUserStatus(String handle, boolean isActiveStatus);

    void sendBootUser(String handle);

    void sendRecalculateScore(int roundId, String handle);

    void sendBanIP(String ipAddress);

    void sendEnableContestRound(int roundID);

    void sendDisableContestRound(int roundID);

    void sendRefreshContestRound(int roundID);

    void sendRefreshRoomLists(int roundID, boolean practice, boolean activeContest, boolean lobbies);

    ChatItem dequeueChatItem() throws InterruptedException;

    CachedItem dequeueCachedItem() throws InterruptedException;

    void sendUserObject(int roundID, String handle);

    void sendRegistationObject(int roundID, int eventID);

    void sendProblemObject(int roundID, int problemID);

    void sendRoundObject(int contestID, int roundID);

    void sendRoomObject(int roundID, int roomID);

    void sendCoderObject(int roundID, int roomID, int coderID);

    void sendCoderProblemObject(int roundID, int roomID, int coderID, int problemIndex);
    /* Da Twink Daddy - 05/12/2002 - New method */
    /**
     * Gives the server an approved question for a moderated chat.
     *
     * @param   text    the text of the question
     * @param   roomID  the room of the moderated chat
     */
    void sendApprovedQuestion(String text, int roomID, String username);

    void sendGetLoggingStreams();

    void sendLoggingStreamSubscribe(StreamID stream);

    void sendLoggingStreamUnsubscribe(StreamID stream);

    // New methods
    void sendLoginRequest(String userid, char[] password);

    void sendRoundAccess();

    void sendChangeRound(int roundId);

    void sendRefreshAccess(Integer roundId);

    void sendGetAllContests();

    void sendGetAllImportantMessages();

    void sendAddContest(ContestData contest);

    void sendModifyContest(int id, ContestData contest);

    void sendAddMessage(ImportantMessageData message);

    void sendModifyMessage(int id, ImportantMessageData message);

    void sendDeleteContest(int id);

    void sendGetRounds(int contestId);

    void sendAddRound(RoundData round);

    void sendModifyRound(int id, RoundData round);

    void sendDeleteRound(int id);

    void sendVerifyRound(int roundID);

    void sendSetSegments(RoundSegmentData segmentData);
    /**
     * <p>
     * send the command of setting events.
     * </p>
     * @param eventData
     *         the round event data.
     */
    void sendSetEvents(RoundEventData eventData);
    
    void sendSetLanguages(RoundLanguageData languageData);

    void sendSetSurvey(SurveyData survey);

    void sendGetProblems(int roundID);

    /**
     * Send the request to get data for all components of all problems assigned
     * to specified round.
     *
     * @param roundID an int representing the ID of requested round.
     * @since Admin Tool 2.0
     */
    void sendGetRoundProblemComponents(int roundID);

    void sendGetRoundProblemComponents(int roundID, int problemID, int divisionID);

    void sendSetComponents(int roundID, Collection problems);

    void sendGetQuestions(int roundID);

    void sendAddQuestion(int roundID, QuestionData question);

    void sendModifyQuestion(QuestionData question);

    void sendDeleteQuestion(int questionID);

    void sendGetAnswers(int questionID);

    void sendAddAnswer(int questionID, AnswerData answer);

    void sendModifyAnswer(AnswerData answer);

    void sendDeleteAnswer(int answerID);

    void sendLoadRound(int roundID);

    void sendUnloadRound(int roundID);

    void sendGarbageCollection();

    void sendRestartEventTopicListener();

    void sendReplayListener();

    void sendReplayReceiver();

    void sendStartSpecAppRotation(int delay);

    void sendStopSpecAppRotation();

    void sendSpecAppShowRoom(long roomID);

    void sendAdvancePhase(int roundId, Integer phaseId);

    void sendCreateSystests(int roundId);

    void sendConsolidateTest(int roundId);

    void sendAllocatePrizes(int roundId, boolean commit);

    void sendRunRatings(int roundId, boolean commit, boolean byDivision, int ratingType);

    void sendRunSeasonRatings(int roundId, boolean commit, boolean byDivision, int season);

    void sendAnnounceAdvancingCoders(int roundID, int numAdvancingCoders);

    void sendRegisterUser(int roundId, String handle, boolean atLeast18);

    void sendUnregisterUser(int roundId, String handle);

    void sendInsertPracticeRoom(int roundId, String name, int groupID);

    void sendObjectSearchRequest(String tableName, String columnName, String searchText, String whereClause);

    void sendBlobColumnRequest();

    void sendObjectUpdateRequest(String tableName, String columnName, String whereClause, Object updateObject, boolean unique);

    void sendTextSearchRequest(String tableName, String columnName, String searchText, String whereClause);

    void sendTextColumnRequest();

    void sendTextUpdateRequest(String tableName, String columnName, String whereClause, Object updateObject, boolean unique);

    void sendSetForwardingAdressRequest(String address);

    void sendSetAdminForwardingAdressRequest(String address);

    void sendAdvanceWLCoders(int roundId, int targetRoundId);

    /**
     * Sends a request for ID generated by specififed sequence to Admin
     * Listener server. Namely new <code>GetIDRequest</code> object is created
     * and sent to Admin Listener server.
     *
     * @param sequence an ID of sequence to get new ID from. The value of this
     *        argument should be one of <code>DBMS.*_SEQ</code> constants.
     * @since Admin Tool 2.0
     * @see   com.topcoder.server.AdminListener.request.GetNewIDRequest
     * @see   DBMS.*_SEQ
     */
    void sendGetNewID(String sequence);

    /**
     * Sends the request to create backup copies of specified tables for
     * specified round to Admin Listener Server.
     *
     * @param  roundID an ID of requested round.
     * @param  tableNames a Set of String table names that should be backed
     *         up.
     * @throws IllegalArgumentException if given argument is null
     * @throws ClassCastException if given Set contains non-String object
     * @since  Admin Tool 2.0
     */
    void sendBackupTables(int roundID, Set tableNames, String comment);

    /**
     * Sends the request to get the list of backup copies for specified round
     * to Admin Listener Server.
     *
     * @param  roundID an ID of round to get list of existing backup copies for
     * @since  Admin Tool 2.0
     */
    void sendGetBackupCopies(int roundID);

    /**
     * Sends the request to restore specified tables from specified backup copy
     * to Admin Listener Server.
     *
     * @param  backupID an ID of requested backup copy
     * @param  tableNames a Set of String table names that should be restored
     * @throws IllegalArgumentException if given Set is null
     * @throws ClassCastException if given Set contains non-String object
     * @since  Admin Tool 2.0
     */
    void sendRestoreTables(int backupID, Set tableNames);

    /**
     * Sends the request to perform a warehouse data load process specified by
     * requestID, Hashtable containing parameters and their values. Creates new
     * WarehouseLoadRequest object and sends it to Admin Listener server.
     *
     * @param requestID an ID of warehouse load request that should be used to
     * check the permission of requestor to perform such action
     * @param params a Hashtable mapping parameter names to parameter values.
     * These parameters should be used to configure TCLoad class before
     * performing the load.
     * @throws IllegalArgumentException if any of given parameters is null
     * @since Admin Tool 2.0
     * @see com.topcoder.server.AdminListener.request.WarehouseLoadRequest
     */
    void sendPerformWarehouseLoad(int requestID, Hashtable params);

    /**
     * Sends the request to save specified round room assignment algorithm
     * details to Admin Listener server.
     *
     * @param  details a RoundRoomAssignment instance containing details of
     *         room assignment algorithm for some round
     * @throws IllegalArgumentException if given argument is null
     * @since  Admin Tool 2.0
     */
    void sendSaveRoundRoomAssignment(RoundRoomAssignment details);

    /**
     * Sends the request to perform specified SecurityManagementRequest
     * to Admin Listener Server.
     *
     * @param  request a SecurityManagementRequest to be sent to Admin Listener
     * @throws IllegalArgumentException if given request is null
     * @since  Admin Tool 2.0
     */
    void sendSecurityRequest(SecurityManagementRequest request);

    /**
     * Sends the request to get the list of existing TCPrincipals of specified
     * type to Admin Listener Server.
     *
     * @param  type a type of principals, either AdminConstants.GROUP_PRINCIPALS
     *         or AdminConstants.ROLE_PRINCIPALS
     * @since  Admin Tool 2.0
     */
    void sendGetPrincipals(int type);
    /**
     * Sends the request to set a terms for specified round using specified
     * properties to evaluate the content of terms.
     *
     * @param  roundID an ID of round to set terms for
     * @param  params a Hashtable mapping parameter names to parameter values.
     *         These parameters should be used to evaluate the content of
     *         round terms based on terms template and propery values.
     * @throws IllegalArgumentException if any of given parameters is null
     * @since  Admin Tool 2.0
     * @see    com.topcoder.server.AdminListener.request.SetRoundTermsRequest
     */
    void sendSetRoundTerms(int roundID, Hashtable params);


    /**
     * Sends the request to restart the specified service(s) to Admin Listener
     * server.
     *
     * @param  requestType an int representing the concrete type of request
     *         to restart the service. The value of this argument should be
     *         one of <code>AdminConstants.REQUEST_RESTART_*</code> constants.
     * @since  Admin Tool 2.0
     * @see    com.topcoder.server.AdminListener.AdminConstants#REQUEST_RESTART_COMPILERS
     * @see    com.topcoder.server.AdminListener.AdminConstants#REQUEST_RESTART_TESTERS
     * @see    com.topcoder.server.AdminListener.AdminConstants#REQUEST_RESTART_ALL
     */
    void sendRestartService(int requestType,int restartMode);

    void sendSetForumID(int roundID, int forumID);
}
