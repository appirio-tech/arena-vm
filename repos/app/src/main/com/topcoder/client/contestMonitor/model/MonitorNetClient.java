package com.topcoder.client.contestMonitor.model;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import com.topcoder.server.AdminListener.AdminConstants;
import com.topcoder.server.AdminListener.request.*;
import com.topcoder.server.AdminListener.response.BlobColumnResponse;
import com.topcoder.server.AdminListener.response.ChangeRoundResponse;
import com.topcoder.server.AdminListener.response.ClearCacheAck;
import com.topcoder.server.AdminListener.response.CommandFailedResponse;
import com.topcoder.server.AdminListener.response.CommandSucceededResponse;
import com.topcoder.server.AdminListener.response.ContestManagementAck;
import com.topcoder.server.AdminListener.response.GenerateTemplateAck;
import com.topcoder.server.AdminListener.response.GetLoggingStreamsAck;
import com.topcoder.server.AdminListener.response.GetQueueInfoResponse;
import com.topcoder.server.AdminListener.response.InsufficientRightsResponse;
import com.topcoder.server.AdminListener.response.LoginResponse;
import com.topcoder.server.AdminListener.response.ObjectSearchResponse;
import com.topcoder.server.AdminListener.response.ObjectUpdateResponse;
import com.topcoder.server.AdminListener.response.RecalculateScoreAck;
import com.topcoder.server.AdminListener.response.RefreshAccessResponse;
import com.topcoder.server.AdminListener.response.RoundAccessResponse;
import com.topcoder.server.AdminListener.response.SetRoundTermsAck;
import com.topcoder.server.AdminListener.response.TextColumnResponse;
import com.topcoder.server.AdminListener.response.TextSearchResponse;
import com.topcoder.server.AdminListener.response.TextUpdateResponse;
import com.topcoder.server.contest.AnswerData;
import com.topcoder.server.contest.ContestData;
import com.topcoder.server.contest.RoundEventData;
import com.topcoder.server.contest.RoundLanguageData;
import com.topcoder.server.contest.QuestionData;
import com.topcoder.server.contest.RoundData;
import com.topcoder.server.contest.RoundRoomAssignment;
import com.topcoder.server.contest.RoundSegmentData;
import com.topcoder.server.contest.SurveyData;
import com.topcoder.server.listener.monitor.ActionItem;
import com.topcoder.server.listener.monitor.AddItem;
import com.topcoder.server.listener.monitor.CachedItem;
import com.topcoder.server.listener.monitor.ChatItem;
import com.topcoder.server.listener.monitor.FirstResponse;
import com.topcoder.server.listener.monitor.MonitorStatsItem;
import com.topcoder.server.listener.monitor.QuestionItem;
import com.topcoder.server.listener.monitor.RemoveItem;
import com.topcoder.server.listener.monitor.UsernameItem;
import com.topcoder.shared.util.StoppableThread;
import com.topcoder.server.util.TCLinkedQueue;
import com.topcoder.shared.util.logging.Logger;
import com.topcoder.server.util.logging.net.LoggingMessage;
import com.topcoder.server.util.logging.net.StreamID;

/**
 * Modifications for AdminTool 2.0 are :
 * <p>New <code>sendGetNewID()</code> method added to CommandSender
 * interface in order to meet the "1.2.1 Using Sequences when creating rounds"
 * requirement is implemented.
 * <p>New <code>sendSaveRoundRoomAssignment(RoundRoomAssignment)</code> method
 * added to CommandSender interface in order to meet the "1.2.5 Auto Room
 * Assignments" requirement is implemented.
 * <p>New <code>sendSecurityRequest(int)</code> and <code>sendGetPrincipals(int)
 * </code> methods are added to meet the "1.2.12 Security Object update" requirement.
 *
 * @author TCDESIGNER
 */
class MonitorNetClient implements StoppableThread.Client, BlockingReaderThread.Client, CommandSender {

    public static final int ALL = -1;

    private static final Logger log = Logger.getLogger(MonitorNetClient.class);

    private final TCLinkedQueue outQueue = new TCLinkedQueue();
    private final TCLinkedQueue chatPanelQueue = new TCLinkedQueue();
    private final TCLinkedQueue cachedObjectsPanelQueue = new TCLinkedQueue();
    /* Da Twink Daddy - 05/09/2002 - New memeber */
    /**
     * Holds QuestionItems to be posted on the GUI.
     *
     * The SocketReaderThread will (through the {@see objectReceived} method) post any recieved QuestionItems to this
     * queue, the MonitorController will then read from the queue (through the {@link #dequeueQuestionItem} method) and update itself accordingly.
     */
    private final TCLinkedQueue questionQueue = new TCLinkedQueue();
    private final StoppableThread writeThread = new StoppableThread(this, "MonitorNetClient.WriteThread");
    private final MonitorServerConnection[] servers;
    private final int length;
    private final MonitorController controller;
    private final List connList = Collections.synchronizedList(new ArrayList());
    private final Map timeMap = new HashMap();
    private String user;
    private String password;

    private boolean isStopping;

    MonitorNetClient(String[] argv, MonitorController controller) throws UnknownHostException {
        this.controller = controller;
        length = argv.length;
        servers = new MonitorServerConnection[length];
        for (int i = 0; i < length; i++) {
            StringTokenizer tk = new StringTokenizer(argv[i], ":");
            String host = tk.nextToken();
            if (!tk.hasMoreTokens()) {
                System.out.println("port not specified (e.g. localhost:5011)");
                throw new IllegalArgumentException();
            }
            int port = Integer.parseInt(tk.nextToken());
            servers[i] = new MonitorServerConnection(host, port, this, i);
        }
        writeThread.start();
    }

    int getNumServers() {
        return length;
    }

    int getNumConnections() {
        return connList.size();
    }

    MonitorServerConnection getServer(int id) {
        return servers[id];
    }

    ConnectionItem getConnection(int id) {
        return (ConnectionItem) connList.get(id);
    }

    private void writeRequest(int id, MonitorRequest list) {
        try {
            servers[id].writeObject(list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cycle() throws InterruptedException {
        WriteTask task = (WriteTask) outQueue.take();
        int recipient = task.recipient;
        MonitorRequest request = task.request;
        if (recipient == ALL) {
            for (int i = 0; i < length; i++) {
                writeRequest(i, request);
            }
        } else {
            writeRequest(recipient, request);
        }
        // If request is a login clear the password from memory
        if (request instanceof LoginRequest) {
            char pw[] = ((LoginRequest) request).getPassword();
            for (int i = 0; i < pw.length; i++) {
                pw[i] = 0;
            }
        }
    }

    void connectionStatusChanged(int id, boolean connected) {
        controller.serverStatusChanged(id);
        if (!connected) {
            removeAllConnections(id);
        }

    }

    private void removeAllConnections(int serverId) {
        synchronized (connList) {
            for (int i = 0; i < connList.size(); i++) {
                ConnectionItem connItem = (ConnectionItem) connList.get(i);
                if (serverId == connItem.getServerId().intValue()) {
                    connList.remove(i);
                    i--;
                }
            }
        }
        updateConnectionsTable();
    }

    private int getRowIndex(int serverId, int id) {
        synchronized (connList) {
            for (int i = 0; i < connList.size(); i++) {
                ConnectionItem connItem = (ConnectionItem) connList.get(i);
                if (serverId == connItem.getServerId().intValue() && id == connItem.getConnId().intValue()) {
                    return i;
                }
            }
        }
        return -1;
    }

    public ConnectionItem getConnection(int serverId, int id) {
        int index = getRowIndex(serverId, id);
        if (index < 0) {
            return null;
        }
        return getConnection(index);
    }

    public void receivedObject(int serverId, Object obj) {
        //System.out.println("received object: " + obj);
        if (obj instanceof LoginResponse) {
            LoginResponse response = (LoginResponse) obj;
            controller.processLoginResponse(response);
            controller.applySecurity(response.getAllowedFunctions());
        } else if (obj instanceof GetQueueInfoResponse) {
            controller.displayMessage(((GetQueueInfoResponse)obj).getResponse());
        } else if (obj instanceof RoundAccessResponse) {
            controller.forwardResponse(obj);
        } else if (obj instanceof ChangeRoundResponse) {
            ChangeRoundResponse response = (ChangeRoundResponse) obj;
            if (response.getSucceeded()) {
                controller.addRoundAccess(response.getRoundId(), response.getRoundName(), response.getAllowedFunctions());
                controller.setRoundId(response.getRoundId(), response.getRoundName());
                controller.applySecurity(response.getAllowedFunctions());
            }
            controller.displayMessage(response.getMessage());
        } else if (obj instanceof RefreshAccessResponse) {
            RefreshAccessResponse response = (RefreshAccessResponse) obj;
            if (response.getSucceeded()) {
                controller.refreshRoundAccess(response.getAllowedFunctions());
                controller.applySecurity(response.getAllowedFunctions());
                controller.displayMessage("Access to commands refreshed.");
            } else {
                controller.displayMessage("Refresh of access to commands failed.");
            }
        } else if (obj instanceof InsufficientRightsResponse) {
            controller.forwardResponse(obj);
        } else if (obj instanceof CommandFailedResponse) {
            controller.displayMessage(((CommandFailedResponse) obj).getMessage());
        } else if (obj instanceof CommandSucceededResponse) {
            controller.displayMessage(((CommandSucceededResponse) obj).getMessage());
        } else if (obj instanceof FirstResponse) {
            FirstResponse firstResponse = (FirstResponse) obj;
            long time = firstResponse.getTime();
            timeMap.put(new Integer(serverId), new Long(time));
            processStatsItem(firstResponse.getStatsItem(), serverId, false);
            updateConnectionsTable();
        } else if (obj instanceof MonitorStatsItem) {
            MonitorStatsItem statsItem = (MonitorStatsItem) obj;
            processStatsItem(statsItem, serverId, true);
            updateConnectionsTable();
        } else if (obj instanceof ChatItem) {
            if (controller.isChatAllowed()) {
                ChatItem chatItem = (ChatItem) obj;
                chatPanelQueue.put(chatItem);
            }
            /* Da Twink Daddy - 05/09/2002 - New Branch */
        } else if (obj instanceof QuestionItem) {
            questionQueue.put(obj);
        } else if (obj instanceof CachedItem) {
            CachedItem cachedItem = (CachedItem) obj;
            cachedObjectsPanelQueue.put(cachedItem);
        } else if (obj instanceof GetLoggingStreamsAck) {
            GetLoggingStreamsAck response = (GetLoggingStreamsAck) obj;
            controller.getLoggingController().updateStreams(response.getStreams());
        } else if (obj instanceof LoggingMessage) {
            LoggingMessage msg = (LoggingMessage) obj;
            controller.getLoggingController().routeLoggingEvent(msg.getStreamID(), msg.getEvent());
        } else if (obj instanceof SetRoundTermsAck) {
            SetRoundTermsAck response = (SetRoundTermsAck) obj;
            if (response.isSuccess()) {
                controller.displayMessage("Terms updated for round: " + ((SetRoundTermsAck) obj).getRoundID());
            } else {
                controller.displayMessage("Terms update failed: " + response.getException());
            }
        } else if (obj instanceof RecalculateScoreAck) {
            RecalculateScoreAck response = (RecalculateScoreAck)obj;
            if (response.isSuccess()) {
                controller.displayMessage("Recalculated score for round=" + response.getRoundId() + ",handle=" + response.getHandle());
            } else {
                controller.displayMessage("Recalculate score failed: " + response.getException());
            }
        } else if (obj instanceof ClearCacheAck) {
            ClearCacheAck response = (ClearCacheAck)obj;
            if (response.isSuccess()) {
                controller.displayMessage("Cache cleared");
            } else {
                controller.displayMessage("Clear cache failed: " + response.getException());
            }
        } else if (obj instanceof GenerateTemplateAck) {
            GenerateTemplateAck response = (GenerateTemplateAck)obj;
            if (response.isSuccess()) {
                controller.displayBigMessage(response.getMessage());
            } else {
                controller.displayBigMessage("Generate Template failed: " + response.getException());
            }
        } else if (obj instanceof ContestManagementAck) {
            controller.getCreateContestController().receive((ContestManagementAck) obj);
        } else if (obj instanceof BlobColumnResponse) {
            controller.forwardResponse(obj);
        } else if (obj instanceof ObjectUpdateResponse) {
            ObjectUpdateResponse response = (ObjectUpdateResponse) obj;
            controller.displayMessage(response.getMessage());
        } else if (obj instanceof ObjectSearchResponse) {
            ObjectSearchResponse response = (ObjectSearchResponse) obj;
            if (response.getSucceeded()) {
                controller.displaySearchResults(response);
            } else {
                controller.displayMessage(response.getMessage());
            }
        } else if (obj instanceof TextColumnResponse) {
            controller.forwardResponse(obj);
        } else if (obj instanceof TextUpdateResponse) {
            TextUpdateResponse response = (TextUpdateResponse) obj;
            controller.displayMessage(response.getMessage());
        } else if (obj instanceof TextSearchResponse) {
            TextSearchResponse response = (TextSearchResponse) obj;
            if (response.getSucceeded()) {
                controller.displaySearchResults(response);
            } else {
                controller.displayMessage(response.getMessage());
            }
        } else {
            throw new RuntimeException("not implemented: " + obj);
        }
    }

    public ChatItem dequeueChatItem() throws InterruptedException {
        return (ChatItem) chatPanelQueue.take();
    }

    public CachedItem dequeueCachedItem() throws InterruptedException {
        return (CachedItem) cachedObjectsPanelQueue.take();
    }

    /* Da Twink Daddy - 05/09/2002 - New method */
    /**
     * Retrieves an item from the {@link #questionQueue}.
     *
     * @return  QuestionItem    the retrieved item
     */
    public QuestionItem dequeueQuestionItem() throws InterruptedException {
        return (QuestionItem) questionQueue.take();
    }

    private void processStatsItem(MonitorStatsItem statsItem, int serverId, boolean checkTime) {
        Collection actionList = statsItem.getActionList();
        for (Iterator it = actionList.iterator(); it.hasNext();) {
            ActionItem item = (ActionItem) it.next();
            int id = item.getId();
            int ind = getRowIndex(serverId, id);
            if (checkTime) {
                long time = item.getTime();
                long startTime = ((Long) timeMap.get(new Integer(serverId))).longValue();
                if (time < startTime) {
                    continue;
                }
            }
            if (item instanceof AddItem) {
                if (ind == -1) {
                    connList.add(new ConnectionItem(serverId, id, ((AddItem) item).getRemoteIP()));
                }
            } else if (item instanceof RemoveItem) {
                if(ind != -1)
                    connList.remove(ind);
            } else if (item instanceof UsernameItem) {
                if (ind != -1) {
                    getConnection(ind).setUsername(((UsernameItem) item).getUsername());
                }
            } else {
                throw new RuntimeException("unknown type: " + item);
            }
        }
        Map bytesMap = statsItem.getBytesMap();
        synchronized (connList) {
            for (int i = 0; i < connList.size(); i++) {
                ConnectionItem connItem = (ConnectionItem) connList.get(i);
                if (serverId == connItem.getServerId().intValue()) {
                    Integer numBytes = (Integer) bytesMap.get(connItem.getConnId());
                    if (numBytes != null) {
                        connItem.add(numBytes.intValue());
                    } else {
                        connItem.add(0);
                    }
                }
            }
        }
    }

    private void updateConnectionsTable() {
        controller.updateConnectionsTable();
    }

    public void stop(int id) {
        stop(id, false);
    }

    public void stop(int id, boolean force) {
        synchronized (this) {
            if (!force && isStopping) {
                return;
            }
        }
        MonitorServerConnection conn = servers[id];
        if (conn.isConnected()) {
            conn.stop(isStopping);
            info("stopped: " + id);
        }
    }

    void close() {
        try {
            writeThread.stopThread();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        synchronized (this) {
            isStopping = true;
        }
        for (int i = 0; i < length; i++) {
            stop(i, true);
        }
    }

    private void enqueue(int recipient, MonitorRequest request) {
        outQueue.put(new WriteTask(recipient, request));
    }

    public void shutdownAllListeners() {
        info("shutdown all listeners");
        enqueue(ALL, new ShutdownRequest());
    }

    public void disconnectAppletClient(int serverId, int connId) {
        enqueue(serverId, new DisconnectRequest(connId));
    }

    public void sendClearPracticeRooms(int type) {
        enqueue(ALL, new ClearPracticeRoomsCommand(type));
    }

    public void sendClearTestCasesCommand(int roundId) {
        enqueue(ALL, new ClearTestCasesCommand(roundId));
    }

    public void sendRefreshRegCommand(int roundId) {
        enqueue(ALL, new RefreshRegCommand(roundId));
    }

    public void sendSystemTestCommand(int roundId, int coderID, int problemID, boolean failOnFirstBadTest, boolean reference) {
        enqueue(ALL, new SystemTestCommand(roundId, coderID, problemID, failOnFirstBadTest,reference));
    }

    public void sendCancelSystemTestCaseTestingCommand(int roundId, int testCaseId) {
        enqueue(ALL, new CancelSystemTestCaseTestingCommand(roundId, testCaseId));
    }

    public void sendUpdatePlaceCommand(int roundId) {
        enqueue(ALL, new UpdatePlaceCommand(roundId));
    }

    public void sendEndContestCommand(int roundId) {
        enqueue(ALL, new EndContestCommand(roundId));
    }

    public void sendEndHSContestCommand(int roundId) {
        enqueue(ALL, new EndHSContestCommand(roundId));
    }

    public void sendGenerateTemplateCommand(int roundId) {
        enqueue(ALL, new GenerateTemplateCommand(roundId));
    }

    public void sendRefreshProbsCommand(int roundID) {
        enqueue(ALL, new RefreshProbsCommand(roundID));
    }

    public void sendRefreshRoom(int roundID, int roomID) {
        enqueue(ALL, new RefreshRoomCommand(roundID, roomID));
    }

    public void sendRoundForwarder(String host, int port, boolean enable) {
        enqueue(ALL, new RoundForwardCommand(host, port, enable, user, password));
    }

    public void sendShowSpecResults() {
        enqueue(ALL, new ShowSpecResultsCommand());
    }

    public void sendRefreshAllRooms(int roundID) {
        enqueue(ALL, new RefreshAllRoomsCommand(roundID));
    }

    public void sendRestoreRound(int roundID) {
        enqueue(ALL, new RestoreRoundCommand(roundID));
    }

    public void sendGlobalBroadcast(String message) {
        enqueue(ALL, new GlobalBroadcastCommand(message));
    }

    public void sendComponentBroadcast(int roundID, String message, int problemId) {
        enqueue(ALL, new ComponentBroadcastCommand(roundID, message, problemId));
    }

    public void sendRoundBroadcast(int roundID, String message) {
        enqueue(ALL, new RoundBroadcastCommand(roundID, message));
    }

    public void sendRefreshBroadcasts(int roundID) {
        enqueue(ALL, new RefreshBroadcastsCommand(roundID));
    }

    public void sendAddTime(int roundId, int minutes, int seconds, int phase, boolean addToStart) {
        enqueue(ALL, new AddTimeCommand(roundId, minutes, seconds, phase, addToStart));
    }

    public void sendAssignRooms(int roundId, int codersPerRoom, int type, boolean isByDivision,
                                boolean isFinal, boolean isByRegion, double p) {
        enqueue(ALL, new AssignRoomsCommand(roundId, codersPerRoom, type, isByDivision, isFinal, isByRegion, p));
    }

    public void sendSetUserStatus(String handle, boolean isActiveStatus) {
        enqueue(ALL, new SetUserStatusCommand(handle, isActiveStatus));
    }

    public void sendBootUser(String handle) {
        enqueue(ALL, new BootUserCommand(handle));
    }

    public void sendRecalculateScore(int roundId, String handle) {
        enqueue(ALL, new RecalculateScoreRequest(roundId, handle));
    }

    public void sendBanIP(String ipAddress) {
        enqueue(ALL, new BanIPCommand(ipAddress));
    }

    public void sendEnableContestRound(int roundID) {
        enqueue(ALL, new EnableRoundCommand(roundID));
    }

    public void sendDisableContestRound(int roundID) {
        enqueue(ALL, new DisableRoundCommand(roundID));
    }

    public void sendRefreshContestRound(int roundID) {
        enqueue(ALL, new RefreshRoundCommand(roundID));
    }

    public void sendUserObject(int roundId, String handle) {
        enqueue(ALL, new UserObject(roundId, handle));
    }

    public void sendRegistationObject(int roundId, int eventID) {
        enqueue(ALL, new RegistrationObject(roundId, eventID));
    }

    public void sendProblemObject(int roundId, int problemID) {
        enqueue(ALL, new ProblemObject(roundId, problemID));
    }

    public void sendRoundObject(int contestID, int roundID) {
        enqueue(ALL, new RoundObject(contestID, roundID));
    }

    public void sendRoomObject(int roundId, int roomID) {
        enqueue(ALL, new RoomObject(roundId, roomID));
    }

    public void sendCoderObject(int roundId, int roomID, int coderID) {
        enqueue(ALL, new CoderObject(roundId, roomID, coderID));
    }

    public void sendCoderProblemObject(int roundId, int roomID, int coderID, int problemIndex) {
        enqueue(ALL, new CoderProblemObject(roundId, roomID, coderID, problemIndex));
    }

    public void sendRefreshRoomLists(int roundId, boolean practice, boolean activeContest, boolean lobbies) {
        enqueue(ALL, new RefreshRoomListsCommand(roundId, practice, activeContest, lobbies));
    }
    /* Da Twink Daddy - 05/12/2002 - New method */
    /**
     * Asynchronously send an ApprovedQuestionCommand to all servers.
     *
     * @param   text    text of the question
     * @param   roomID  moderated chat for which the question is meant
     */
    public void sendApprovedQuestion(String text, int roomID, String username) {
        enqueue(ALL, new ApprovedQuestionCommand(text, roomID, username));
    }

    public void sendGetLoggingStreams() {
        enqueue(ALL, new GetLoggingStreamsRequest());
    }

    public void sendLoggingStreamSubscribe(StreamID stream) {
        enqueue(ALL, new LoggingStreamSubscribeRequest(stream));
    }

    public void sendLoggingStreamUnsubscribe(StreamID stream) {
        enqueue(ALL, new LoggingStreamUnsubscribeRequest(stream));
    }

    private static void info(String s) {
        log.info(s);
    }

    // Login method
    public void sendLoginRequest(String userid, char[] password) {
        this.user = userid;
        this.password = new String(password);
        enqueue(ALL, new LoginRequest(userid, password));
    }

    public void sendGetAllContests() {
        // TODO - direct this to a specific listener?
        enqueue(ALL, new GetAllContestsRequest());
    }

    public void sendGetAllImportantMessages() {
        // TODO - direct this to a specific listener?
        enqueue(ALL, new GetAllImportantMessagesRequest());
    }


    public void sendRoundAccess() {
        enqueue(ALL, new RoundAccessRequest());
    }

    /**
     * This method was updated for AdminTool2.0 to include the TCSubject with
     * the request object.
     * @param roundId the round being requested
     */
    public void sendChangeRound(int roundId) {
        ChangeRoundRequest req = new ChangeRoundRequest(roundId);
        enqueue(ALL, req);
    }

    /**
     * This method was updated for AdminTool2.0 to include the TCSubject with
     * the request object.
     * @param roundId the round for which access is being requested
     */
    public void sendRefreshAccess(Integer roundId) {
        RefreshAccessRequest req = new RefreshAccessRequest(roundId.intValue());
        enqueue(ALL, req);
    }

    public void sendAddContest(ContestData contest) {
        // TODO - direct this to a specific listener
        enqueue(ALL, new AddContestRequest(contest));
    }

    public void sendModifyContest(int id, ContestData contest) {
        // TODO - direct this to a specific listener
        enqueue(ALL, new ModifyContestRequest(id, contest));
    }

    public void sendDeleteContest(int id) {
        // TODO - direct this to a specific listener
        enqueue(ALL, new DeleteContestRequest(id));
    }


    public void sendGetRounds(int contestId) {
        // TODO - direct this to a specific listener
        enqueue(ALL, new GetRoundsRequest(contestId));
    }

    public void sendAddRound(RoundData round) {
        // TODO - direct this to a specific listener
        enqueue(ALL, new AddRoundRequest(round.getContest().getId(), round));
    }

    public void sendModifyRound(int id, RoundData Round) {
        // TODO - direct this to a specific listener
        enqueue(ALL, new ModifyRoundRequest(id, Round));
    }

    public void sendDeleteRound(int id) {
        // TODO - direct this to a specific listener
        enqueue(ALL, new DeleteRoundRequest(id));
    }

    public void sendVerifyRound(int roundID) {
        enqueue(ALL, new VerifyRoundRequest(roundID));
    }

    public void sendSetSegments(RoundSegmentData segmentData) {
        // TODO - direct this to a specific listener
        enqueue(ALL, new SetRoundSegmentsRequest(segmentData.getRoundId(), segmentData));
    }
    /**
     * <p>
     * send the setting round event data request.
     * </p>
     * @param eventData
     *         the round event data.
     */
    public void sendSetEvents(RoundEventData eventData) {
        enqueue(ALL, new SetRoundEventsRequest(eventData));
    }
    
    public void sendSetLanguages(RoundLanguageData languageData) {
        // TODO - direct this to a specific listener
        enqueue(ALL, new SetRoundLanguagesRequest(languageData.getRoundId(), languageData));
    }

    public void sendGetProblems(int roundID) {
        enqueue(ALL, new GetProblemsRequest(roundID));
    }

    /**
     * Send the request to get data for all components of all problems assigned
     * to specified round.
     *
     * @param roundID an int representing the ID of requested round.
     * @since Admin Tool 2.0
     */
    public void sendGetRoundProblemComponents(int roundID) {
        enqueue(ALL, new GetRoundProblemComponentsRequest(roundID));
    }

    public void sendGetRoundProblemComponents(int roundID, int problemID, int divisionID) {
        enqueue(ALL, new GetRoundProblemComponentsRequest(roundID, problemID, divisionID));
    }

    public void sendSetComponents(int roundID, Collection components) {
        enqueue(ALL, new SetComponentsRequest(roundID, components));
    }

    public void sendSetSurvey(SurveyData survey) {
        enqueue(ALL, new SetSurveyRequest(survey));
    }

    public void sendGetQuestions(int roundID) {
        enqueue(ALL, new GetQuestionsRequest(roundID));
    }

    public void sendAddQuestion(int roundID, QuestionData question) {
        enqueue(ALL, new AddQuestionRequest(roundID, question));
    }

    public void sendModifyQuestion(QuestionData question) {
        enqueue(ALL, new ModifyQuestionRequest(question));
    }

    public void sendDeleteQuestion(int questionID) {
        enqueue(ALL, new DeleteQuestionRequest(questionID));
    }

    public void sendGetAnswers(int questionID) {
        enqueue(ALL, new GetAnswersRequest(questionID));
    }

    public void sendAddAnswer(int questionID, AnswerData answer) {
        enqueue(ALL, new AddAnswerRequest(questionID, answer));
    }

    public void sendModifyAnswer(AnswerData answer) {
        enqueue(ALL, new ModifyAnswerRequest(answer));
    }

    public void sendDeleteAnswer(int answerID) {
        enqueue(ALL, new DeleteAnswerRequest(answerID));
    }

    public void sendLoadRound(int roundID) {
        enqueue(ALL, new LoadRoundRequest(roundID));
    }

    public void sendUnloadRound(int roundID) {
        enqueue(ALL, new UnloadRoundRequest(roundID));
    }

    public void sendGarbageCollection() {
        enqueue(ALL, new GarbageCollectionRequest());
    }

    public void sendRestartEventTopicListener() {
        enqueue(ALL, new RestartEventTopicListenerRequest());
    }

    public void sendReplayListener() {
        enqueue(ALL, new ReplayListenerRequest());
    }

    public void sendReplayReceiver() {
        enqueue(ALL, new ReplayReceiverRequest());
    }

    //    public void sendSetSpectatorRoom(int roundId, int roomId) {
    //        enqueue(ALL, new SetSpectatorRoomRequest(roundId, roomId));
    //    }

    public void sendStartSpecAppRotation(int delay) {
        enqueue(ALL, new StartSpecAppRotationRequest(delay));
    }

    public void sendStopSpecAppRotation() {
        enqueue(ALL, new StopSpecAppRotationRequest());
    }

    public void sendSpecAppShowRoom(long roomID) {
        enqueue(ALL, new SpecAppShowRoomRequest(roomID));
    }

    public void sendAdvancePhase(int roundId, Integer phaseId) {
        enqueue(ALL, new AdvancePhaseRequest(roundId, phaseId));
    }

    public void sendCreateSystests(int roundId) {
        enqueue(ALL, new CreateSystestsRequest(roundId));
    }

    public void sendConsolidateTest(int roundId) {
        enqueue(ALL, new ConsolidateTestRequest(roundId));
    }

    public void sendAllocatePrizes(int roundId, boolean commit) {
        enqueue(ALL, new AllocatePrizesRequest(roundId, commit));
    }

    public void sendRunRatings(int roundId, boolean commit, boolean byDivision, int ratingType) {
        enqueue(ALL, new RunRatingsRequest(roundId, commit, byDivision,ratingType));
    }

    public void sendRunSeasonRatings(int roundId, boolean commit, boolean byDivision, int season) {
        enqueue(ALL, new RunSeasonRatingsRequest(roundId, commit, byDivision,season));
    }

    public void sendRegisterUser(int roundId, String handle, boolean atLeast18) {
        enqueue(ALL, new RegisterUserRequest(roundId, handle, atLeast18));
    }

    public void sendUnregisterUser(int roundId, String handle) {
        enqueue(ALL, new UnregisterUserRequest(roundId, handle));
    }

    public void sendInsertPracticeRoom(int roundId, String name, int groupID) {
        enqueue(ALL, new InsertPracticeRoomRequest(roundId, name, groupID));
    }

    public void sendObjectSearchRequest(String tableName, String columnName, String searchText, String whereClause) {
        enqueue(ALL, new ObjectSearchRequest(tableName, columnName, searchText, whereClause));
    }

    public void sendBlobColumnRequest() {
        enqueue(ALL, new BlobColumnRequest());
    }

    public void sendObjectUpdateRequest(String tableName, String columnName, String whereClause, Object updateObject, boolean unique) {
        enqueue(ALL, new ObjectUpdateRequest(tableName, columnName, whereClause, updateObject, unique));
    }

    public void sendTextSearchRequest(String tableName, String columnName, String searchText, String whereClause) {
        enqueue(ALL, new TextSearchRequest(tableName, columnName, searchText, whereClause));
    }

    public void sendTextColumnRequest() {
        enqueue(ALL, new TextColumnRequest());
    }

    public void sendTextUpdateRequest(String tableName, String columnName, String whereClause, Object updateObject, boolean unique) {
        enqueue(ALL, new TextUpdateRequest(tableName, columnName, whereClause, updateObject, unique));
    }

    public void sendAnnounceAdvancingCoders(int roundID, int numAdvancingCoders) {
        enqueue(ALL, new AnnounceAdvancingCodersRequest(roundID, numAdvancingCoders));
    }

    public void sendSetForwardingAdressRequest(String address) {
        enqueue(ALL, new SetForwardingAddressRequest(address));
    }

    public void sendSetAdminForwardingAdressRequest(String address) {
        enqueue(ALL, new SetAdminForwardingAddressRequest(address));
    }

    /**
     * Sends the request to create backup copies of specified tables for
     * specified round to Admin Listener Server. Constructs new <code>
     * BackupTablesRequest</code> and fills it with table names from given Set.
     *
     * @param  roundID an ID of requested round.
     * @param  tableNames a Set of String table names that should be backed
     *         up.
     * @throws IllegalArgumentException if given argument is null
     * @throws ClassCastException if given Set contains non-String object
     * @since  Admin Tool 2.0
     * @see    BackupTablesRequest
     */
    public void sendBackupTables(int roundID, Set tableNames, String commment) {
        BackupTablesRequest request = new BackupTablesRequest(roundID);
        Iterator i = tableNames.iterator();
        while (i.hasNext()) {
            request.addTableName((String) i.next());
        }
        request.setComment(commment);
        enqueue(ALL, request);
    }

    /**
     * Sends the request to get the list of backup copies for specified round
     * to Admin Listener Server. Constructs new <code>GetBackupCopiesRequest
     * </code> wrapping specified round ID and enqueues it for delivering to
     * Admin Listener server.
     *
     * @param  roundID an ID of round to get list of existing backup copies for
     * @since  Admin Tool 2.0
     * @see    GetBackupCopiesRequest
     */
    public void sendGetBackupCopies(int roundID) {
        enqueue(ALL, new GetBackupCopiesRequest(roundID));
    }

    /**
     * Sends the request to restore specified tables from specified backup copy
     * to Admin Listener Server.  Constructs new <code>
     * RestoreTablesRequest</code> and fills it with table names from given Set.
     *
     * @param  backupID an ID of requested backup copy
     * @param  tableNames a Set of String table names that should be restored
     * @throws IllegalArgumentException if given Set is null
     * @throws ClassCastException if given Set contains non-String object
     * @since  Admin Tool 2.0
     */
    public void sendRestoreTables(int backupID, Set tableNames) {
        RestoreTablesRequest req = new RestoreTablesRequest(backupID);
        Iterator i = tableNames.iterator();
        while (i.hasNext()) {
            req.addTableName((String) i.next());
        }
        enqueue(ALL, req);
    }

    /**
     * Sends the request to perform a warehouse data load process specified
     * by requestID, name of the class that should be used to perform the load,
     * Hashtable containing parameters and their values. Creates new <code>
     * WarehouseLoadRequest</code> object and sends it to Admin Listener
     * server.
     *
     * @param  requestID an ID of warehouse load request that should be used
     * to check the permission of requestor to perform such action
     * @param  params a Hashtable mapping parameter names to parameter values.
     * These parameters should be used to configure TCLoad class before
     * performing the load.
     * @throws IllegalArgumentException if any of given parameters is null, or
     * className is empty
     * @since  Admin Tool 2.0
     * @see    WarehouseLoadRequest
     */
    public void sendPerformWarehouseLoad(int requestID, Hashtable params) {
        if (params == null)
            throw new IllegalArgumentException("null params when sending warehouse load request");

        WarehouseLoadRequest request = new WarehouseLoadRequest(requestID, params);
        enqueue(ALL, request);
    }

    /**
     * Sends a request for ID generated by specififed sequence to Admin
     * Listener server. Namely new <code>GetIDRequest</code> object is created
     * and sent to Admin Listener server.
     *
     * @param sequence an ID of sequence to get new ID from. The value of this
     *        argument should be one of <code>DBMS.*_SEQ</code> constants.
     * @since Admin Tool 2.0
     * @see   GetNewIDRequest
     */
    public void sendGetNewID(String sequence) {
        enqueue(ALL, new GetNewIDRequest(sequence));
    }

    /**
     * Sends the request to save specified round room assignment algorithm
     * details to Admin Listener server. Constructs new <code>
     * SaveRoundRoomAssignmentRequest</code> wrapping given details and
     * enqueues it for delivering to Admin Listener server.
     *
     * @param  details a RoundRoomAssignment instance containing details of
     *         room assignment algorithm for some round
     * @throws IllegalArgumentException if given argument is null
     * @since  Admin Tool 2.0
     * @see    RoundRoomAssignment
     */
    public void sendSaveRoundRoomAssignment(RoundRoomAssignment details) {
        if( details == null )
            throw new IllegalArgumentException( "details cannot be null");
        enqueue(ALL, new SaveRoundRoomAssignmentRequest(details));
    }

    private static class WriteTask {

        private final int recipient;
        private final MonitorRequest request;


        private WriteTask(int recipient, MonitorRequest request) {
            this.recipient = recipient;
            this.request = request;
        }

    }

    public void sendAdvanceWLCoders(int roundId, int targetRoundId) {
        enqueue(ALL, new AdvanceWLCodersRequest(roundId, targetRoundId));
    }


    /**
     * Sends the request to set a terms for specified round using specified
     * properties to evaluate the content of terms. Constructs new <code>
     * SetRoundTermsRequest</code> from given parameters and sends it to
     * AdminListener server.
     *
     * @param  roundID an ID of round to set terms for
     * @param  params a Hashtable mapping parameter names to parameter values.
     *         These parameters should be used to evaluate the content of
     *         round terms based on terms template and propery values.
     * @throws IllegalArgumentException if any of given parameters is null
     * @since  Admin Tool 2.0
     * @see    SetRoundTermsRequest
     */
    public void sendSetRoundTerms(int roundID, Hashtable params) {
        enqueue(ALL, new SetRoundTermsRequest(roundID, params));
    }


    /**
     * Sends the request to restart the service to Admin Listener server.
     * Constructs new <code>RestartServiceRequest</code> object with given
     * type of request and enqueues it for delivering to Admin Listener
     * server.
     *
     * @param  requestType an int representing the concrete type of request
     *         to restart the service. The value of this argument should be
     *         one of <code>AdminConstants.REQUEST_RESTART_*</code> constants.
     * @since  Admin Tool 2.0
     * @see    com.topcoder.server.AdminListener.AdminConstants#REQUEST_RESTART_COMPILERS
     * @see    com.topcoder.server.AdminListener.AdminConstants#REQUEST_RESTART_TESTERS
     * @see    com.topcoder.server.AdminListener.AdminConstants#REQUEST_RESTART_ALL
     * @see    com.topcoder.server.AdminListener.request.RestartServiceRequest
     */
    public void sendRestartService(int requestType, int restartMode) {
        enqueue(ALL, new RestartServiceRequest(requestType,restartMode));
    }

    /**
     * Sends the request to perform specified SecurityManagementRequest
     * to Admin Listener Server. Enqueues specified request for delivering to
     * Admin Listener server.
     *
     * @param  request a SecurityManagementRequest to be sent to Admin Listener
     * @throws IllegalArgumentException if given request is null
     * @since  Admin Tool 2.0
     */
    public void sendSecurityRequest(SecurityManagementRequest request) {
        if( request == null )
            throw new IllegalArgumentException("invalid security request = " +
                                               request);
        enqueue(ALL, request);
    }

    /**
     * Sends the request to get the list of existing TCPrincipals of specified
     * type to Admin Listener Server. Creates new GetPrincipalsRequest object
     * and enqueues it for delivering to Admin Listener server.
     *
     * @param  type a type of principals, either AdminConstants.GROUP_PRINCIPALS
     *         or AdminConstants.ROLE_PRINCIPALS
     * @throws IllegalArgumentException if given type contains incorrect value
     * @since  Admin Tool 2.0
     */
    public void sendGetPrincipals(int type) {
        if( type != AdminConstants.GROUP_PRINCIPALS &&
            type != AdminConstants.ROLE_PRINCIPALS)
            throw new IllegalArgumentException("invalid type = " + type);
        enqueue(ALL, new GetPrincipalsRequest(type));
    }

    public void sendAddMessage(com.topcoder.server.contest.ImportantMessageData message) {
        enqueue(ALL, new AddMessageRequest(message));
    }

    public void sendModifyMessage(int id, com.topcoder.server.contest.ImportantMessageData message) {
        enqueue(ALL, new ModifyMessageRequest(id, message));
    }

    public void sendGetQueueInfoRequest(String queueName) {
        enqueue(ALL, new GetQueueInfoRequest(queueName));
    }

    public void sendClearCacheCommand() {
        enqueue(ALL, new ClearCacheRequest());
    }

    public void sendSetForumID(int roundID, int forumID) {
        enqueue(ALL, new SetForumIDRequest(roundID, forumID));
    }
}
