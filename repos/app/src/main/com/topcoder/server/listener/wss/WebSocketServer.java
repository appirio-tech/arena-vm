/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.server.listener.wss;


import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.topcoder.netCommon.contestantMessages.request.ActiveUsersRequest;
import com.topcoder.netCommon.contestantMessages.request.AddTeamMemberRequest;
import com.topcoder.netCommon.contestantMessages.request.AssignComponentRequest;
import com.topcoder.netCommon.contestantMessages.request.AssignComponentsRequest;
import com.topcoder.netCommon.contestantMessages.request.AutoSystestRequest;
import com.topcoder.netCommon.contestantMessages.request.BaseRequest;
import com.topcoder.netCommon.contestantMessages.request.BatchTestRequest;
import com.topcoder.netCommon.contestantMessages.request.ChallengeInfoRequest;
import com.topcoder.netCommon.contestantMessages.request.ChallengeRequest;
import com.topcoder.netCommon.contestantMessages.request.ChatRequest;
import com.topcoder.netCommon.contestantMessages.request.ClearPracticeProblemRequest;
import com.topcoder.netCommon.contestantMessages.request.ClearPracticeRequest;
import com.topcoder.netCommon.contestantMessages.request.CloseDivSummaryRequest;
import com.topcoder.netCommon.contestantMessages.request.CloseLeaderBoardRequest;
import com.topcoder.netCommon.contestantMessages.request.CloseProblemRequest;
import com.topcoder.netCommon.contestantMessages.request.CloseSummaryRequest;
import com.topcoder.netCommon.contestantMessages.request.CloseTeamListRequest;
import com.topcoder.netCommon.contestantMessages.request.CoderHistoryRequest;
import com.topcoder.netCommon.contestantMessages.request.CoderInfoRequest;
import com.topcoder.netCommon.contestantMessages.request.CompileRequest;
import com.topcoder.netCommon.contestantMessages.request.DivSummaryRequest;
import com.topcoder.netCommon.contestantMessages.request.EnterRequest;
import com.topcoder.netCommon.contestantMessages.request.EnterRoundRequest;
import com.topcoder.netCommon.contestantMessages.request.ErrorReportRequest;
import com.topcoder.netCommon.contestantMessages.request.ErrorRequest;
import com.topcoder.netCommon.contestantMessages.request.ExchangeKeyRequest;
import com.topcoder.netCommon.contestantMessages.request.GenericPopupRequest;
import com.topcoder.netCommon.contestantMessages.request.GetAdminBroadcastsRequest;
import com.topcoder.netCommon.contestantMessages.request.GetChallengeProblemRequest;
import com.topcoder.netCommon.contestantMessages.request.GetCurrentAppletVersionRequest;
import com.topcoder.netCommon.contestantMessages.request.GetImportantMessagesRequest;
import com.topcoder.netCommon.contestantMessages.request.GetLeaderBoardRequest;
import com.topcoder.netCommon.contestantMessages.request.GetProblemRequest;
import com.topcoder.netCommon.contestantMessages.request.GetSourceCodeRequest;
import com.topcoder.netCommon.contestantMessages.request.GetTeamListRequest;
import com.topcoder.netCommon.contestantMessages.request.JoinTeamRequest;
import com.topcoder.netCommon.contestantMessages.request.KeepAliveRequest;
import com.topcoder.netCommon.contestantMessages.request.LeaveTeamRequest;
import com.topcoder.netCommon.contestantMessages.request.LoginRequest;
import com.topcoder.netCommon.contestantMessages.request.LogoutRequest;
import com.topcoder.netCommon.contestantMessages.request.LongSubmitRequest;
import com.topcoder.netCommon.contestantMessages.request.LongTestResultsRequest;
import com.topcoder.netCommon.contestantMessages.request.MoveRequest;
import com.topcoder.netCommon.contestantMessages.request.OpenComponentForCodingRequest;
import com.topcoder.netCommon.contestantMessages.request.OpenProblemForReadingRequest;
import com.topcoder.netCommon.contestantMessages.request.OpenSummaryRequest;
import com.topcoder.netCommon.contestantMessages.request.PracticeSystemTestRequest;
import com.topcoder.netCommon.contestantMessages.request.ReadMessageRequest;
import com.topcoder.netCommon.contestantMessages.request.ReconnectRequest;
import com.topcoder.netCommon.contestantMessages.request.RegisterInfoRequest;
import com.topcoder.netCommon.contestantMessages.request.RegisterRequest;
import com.topcoder.netCommon.contestantMessages.request.RegisterRoomRequest;
import com.topcoder.netCommon.contestantMessages.request.RegisterUsersRequest;
import com.topcoder.netCommon.contestantMessages.request.RegisterWeakestLinkTeamRequest;
import com.topcoder.netCommon.contestantMessages.request.RegisteredRoundListRequest;
import com.topcoder.netCommon.contestantMessages.request.RemoveTeamMemberRequest;
import com.topcoder.netCommon.contestantMessages.request.RoundProblemsRequest;
import com.topcoder.netCommon.contestantMessages.request.RoundScheduleRequest;
import com.topcoder.netCommon.contestantMessages.request.RoundStatsRequest;
import com.topcoder.netCommon.contestantMessages.request.SSOLoginRequest;
import com.topcoder.netCommon.contestantMessages.request.SaveRequest;
import com.topcoder.netCommon.contestantMessages.request.SearchRequest;
import com.topcoder.netCommon.contestantMessages.request.SetLanguageRequest;
import com.topcoder.netCommon.contestantMessages.request.SubmitRequest;
import com.topcoder.netCommon.contestantMessages.request.SynchTimeRequest;
import com.topcoder.netCommon.contestantMessages.request.SystestResultsRequest;
import com.topcoder.netCommon.contestantMessages.request.TestInfoRequest;
import com.topcoder.netCommon.contestantMessages.request.TestRequest;
import com.topcoder.netCommon.contestantMessages.request.ToggleChatRequest;
import com.topcoder.netCommon.contestantMessages.request.UnassignComponentRequest;
import com.topcoder.netCommon.contestantMessages.request.UnwatchRequest;
import com.topcoder.netCommon.contestantMessages.request.VerifyRequest;
import com.topcoder.netCommon.contestantMessages.request.VerifyResultRequest;
import com.topcoder.netCommon.contestantMessages.request.ViewQueueRequest;
import com.topcoder.netCommon.contestantMessages.request.VisitedPracticeRequest;
import com.topcoder.netCommon.contestantMessages.request.VoteRequest;
import com.topcoder.netCommon.contestantMessages.request.WLMyTeamInfoRequest;
import com.topcoder.netCommon.contestantMessages.request.WLTeamsInfoRequest;
import com.topcoder.netCommon.contestantMessages.request.WatchRequest;
import com.topcoder.server.AdminListener.request.ChangeRoundRequest;
import com.topcoder.server.AdminListener.request.LoadRoundRequest;
import com.topcoder.server.AdminListener.request.RoundAccessRequest;
import com.topcoder.server.listener.wss.listeners.GenericListener;
import com.topcoder.server.listener.wss.listeners.LoginDataListener;
import com.topcoder.server.listener.wss.listeners.LogoutDataListener;
import com.topcoder.server.listener.wss.listeners.SSOLoginDataListener;
import com.topcoder.server.listener.wss.listeners.WebSocketDisconnectListener;
import com.topcoder.shared.util.SimpleResourceBundle;
import com.topcoder.shared.util.concurrent.ConcurrentHashSet;


/**
 * The web socket server based on netty-socketio.
 *
 * <p>
 * Version 1.1 - Module Assembly - TopCoder Competition Engine - Web Socket Listener
 * <ol>Make it a stand alone listener</ol>
 * <ol>Moved the listeners in separate java files.</ol>
 * </p>
 *
 * <p>
 * Version 1.2 - Module Assembly - Connecting Web Socket Listener and Main Listener v1.0
 * <ol>Add main listener connector</ol>
 * </p>
 *
 * <p>
 * Version 1.3 (Module Assembly - TCC Web Socket - Coder Profile and Active Users) changes:
 * <ol>
 *  <li>Updated {@link #registerListeners()} to support getCoderInfo event.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.4 (Module Assembly - TCC Web Socket - Get Registered Rounds and
 * Round Problems):
 * <ol>
 *      <li>Update {@link #registerListeners()} method to include REGISTERED_ROUND_LIST listener.</li>
 *      <li>Update {@link #registerListeners()} method to include ROUND_PROBLEMS listener.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.5 (TCC Web Socket Refactoring):
 * <ol>
 *      <li>Changed {@link #registerListeners()} to handle trivial requests via {@link GenericListener}.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Version 1.6 (Web Socket Listener - Add configuration for using SSL or not v1.0) changes:
 * <ol>
 *  <li>Update {@link #WebSocketServer(int port, String mainListenerIP, int mainListenerPort)} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.7 (Problem to Restart Web Socket Listener v1.0):
 * <ol>
 *      <li>Update {@link #WebSocketServer(int port, String mainListenerIP, int mainListenerPort)} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.8 (Module Assembly - TopCoder Competition Engine - Batch Test):
 * <ol>
 *      <li>Update {@link #registerListeners()} method to add event listener
 *      for {@link BatchTestRequest} class.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.9 (Module Assembly - Web Socket Listener -
 * Porting Round Load Related Events):
 * <ol>
 *      <li>Updated {@link #registerListeners()} to handle new load round access requests</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.10 (Module Assembly - Web Socket Listener - Track User Actions From Web Arena):
 * <ol>
 *     <li>Added {@link #sessionToUserHandleMap} to store the map from session to user handle.</li>
 * </ol>
 * </p>
 * @author Standlove, freegod, dexy, gevak, savon_cn, ananthhh
 * @version 1.10
 * @since 1.0
 */
public class WebSocketServer {
	private static final Logger logger = Logger.getLogger(WebSocketServer.class);
	
    /**
     * The port validation message.
     */
    private static final String PORT_VALIDATION_MESSAGE = "The port must be an int value in range [1, 65535].";

    /**
     * Minimum port number.
     */
    private static final int MIN_PORT_NUMBER = 1;

    /**
     * Maximum port number.
     */
    private static final int MAX_PORT_NUMBER = 65535;

    /**
     * The SocketIOServer instance.
     */
    private final SocketIOServer server;

    /**
     * Mapping from the WebSocket session id to SocketIO Client.
     */
    private final Map<UUID, SocketIOClient> sessionToConnectionMap = new ConcurrentHashMap<UUID, SocketIOClient>();

    /**
     * Mapping from the WebSocket session id to SocketIO Client.
     */
//    private final Map<UUID, String> jwtMap = new ConcurrentHashMap<UUID, String>();

    /**
     * Mapping from WebSocket session id to the user handle who tried to login.
     * It's added when user tries to login.
     *
     * @since 1.10
     */
    private final Map<UUID, String> sessionToUserHandleMap = new ConcurrentHashMap<UUID, String>();

    /**
     * The actions required to record.
     *
     * @since 1.10
     */
    private final Set<String> recordedActions = new ConcurrentHashSet<String>();

    /**
     * Connector to the main listener.
     */
    private MainListenerConnector mlc;

    /**
     * IP of the main listener.
     */
    private String mlIP;

    /**
     * Port of the main listener.
     */
    private Integer mlP;

    /**
     * Creates a new instance of this class.
     *
     * @param port    the port number
     * @param mainListenerIP    the address of the main listener
     * @param mainListenerPort  the port number of the main listener
     * @throws IllegalArgumentException if port is not in range [1, 65535]
     * @throws IllegalStateException if keyStoreFileName or keyStorePassword is missing in configuration file.
     */
    public WebSocketServer(int port, String mainListenerIP, int mainListenerPort) {
        if (port < MIN_PORT_NUMBER || port > MAX_PORT_NUMBER) {
            throw new IllegalArgumentException(PORT_VALIDATION_MESSAGE);
        }
        this.mlIP = mainListenerIP;
        this.mlP = mainListenerPort;

        // set SocketIOServer configuration
        Configuration config = new Configuration();
        config.getSocketConfig().setReuseAddress(true);
        config.setPort(port);
        // load keyStoreFileName & keyStorePassword from bundle
        SimpleResourceBundle bundle = SimpleResourceBundle.getBundle(
                WebSocketServerHelper.WEB_SOCKET_SERVER_BUNDLE_NAME);

        boolean isStartWithSSL = bundle.getBoolean(WebSocketServerHelper.IS_START_WITH_SSL, false);
        if (isStartWithSSL) {
            String keyStoreFileName = bundle.getString(WebSocketServerHelper.KEYSTORE_FILE_NAME_KEY);
            String keyStorePassword = bundle.getString(WebSocketServerHelper.KEYSTORE_PASSSWORD_KEY);

            if (WebSocketServerHelper.isNullOrEmpty(keyStoreFileName)) {
                throw new IllegalStateException("The keyStoreFileName property is missing in the bundle file.");
            }
            if (WebSocketServerHelper.isNullOrEmpty(keyStorePassword)) {
                throw new IllegalStateException("The keyStorePassword property is missing in the bundle file.");
            }
            config.setKeyStorePassword(keyStorePassword);
            InputStream stream = WebSocketServer.class.getClassLoader().getResourceAsStream(keyStoreFileName);

            if (stream == null) {
                throw new IllegalArgumentException(
                        "The keyStoreFileName: " + keyStoreFileName + " doesn't appear on class path.");
            }

            config.setKeyStore(stream);
        }

        SimpleResourceBundle recordActionBundle = SimpleResourceBundle.getBundle(
                WebSocketServerHelper.RECORD_ACTION_BUNDLE_NAME);
        String actions = recordActionBundle.getString(WebSocketServerHelper.RECORD_ACTION_KEY);
        if (null != actions && actions.trim().length() > 0) {
            String[] recordActions = actions.split(",");
            for(String action : recordActions) {
                if (action.trim().length() > 0) {
                    recordedActions.add(action.trim());
                }
            }
        }

        server = new SocketIOServer(config);
    }

    /**
     * Getter for the session id to SocketIO Client mapping.
     *
     * @return the session id to SocketIO Client mapping
     */
    public Map<UUID, SocketIOClient> getSessionToConnectionMap() {
        return sessionToConnectionMap;
    }

//    /**
//     * Getter for the session id to jwt mapping.
//     *
//     * @return the session id to jwt mapping
//     */
//    public Map<UUID, String> getSessionToJwtMap() {
//        return jwtMap;
//    }

    /**
     * Getter for the map from session id to user handle.
     *
     * @return the map from session id to user handle
     *
     * @since 1.10
     */
    public Map<UUID, String> getSessionToUserHandleMap() {
        return sessionToUserHandleMap;
    }

    /**
     * Getter for the set of actions required to record.
     *
     * @return the set containing all actions required to record.
     *
     * @since 1.10
     */
    public Set<String> getRecordedActions() {
        return recordedActions;
    }

    /**
     * Getter for the main listener connector.
     *
     * @return the main listener connector
     */
    public MainListenerConnector getMainListenerConnector() {
        return mlc;
    }

    /**
     * Add listeners to the server.
     */
    private void registerListeners() {
        // Connection related listeners.
        server.addEventListener(LoginRequest.class.getSimpleName(),
                com.topcoder.server.listener.wss.request.LoginRequest.class, new LoginDataListener(this));
        server.addEventListener(SSOLoginRequest.class.getSimpleName(),
                SSOLoginRequest.class, new SSOLoginDataListener(this));
        server.addEventListener(LogoutRequest.class.getSimpleName(),
                LogoutRequest.class, new LogoutDataListener(this));
        server.addDisconnectListener(new WebSocketDisconnectListener(this));

        // Generic listeners.
        server.addEventListener(ActiveUsersRequest.class.getSimpleName(),
                ActiveUsersRequest.class, new GenericListener<ActiveUsersRequest>(this));
        server.addEventListener(AddTeamMemberRequest.class.getSimpleName(),
                AddTeamMemberRequest.class, new GenericListener<AddTeamMemberRequest>(this));
        server.addEventListener(AssignComponentRequest.class.getSimpleName(),
                AssignComponentRequest.class, new GenericListener<AssignComponentRequest>(this));
        server.addEventListener(AssignComponentsRequest.class.getSimpleName(),
                AssignComponentsRequest.class, new GenericListener<AssignComponentsRequest>(this));
        server.addEventListener(AutoSystestRequest.class.getSimpleName(),
                AutoSystestRequest.class, new GenericListener<AutoSystestRequest>(this));
        server.addEventListener(BaseRequest.class.getSimpleName(),
                BaseRequest.class, new GenericListener<BaseRequest>(this));
        server.addEventListener(ChallengeInfoRequest.class.getSimpleName(),
                ChallengeInfoRequest.class, new GenericListener<ChallengeInfoRequest>(this));
        server.addEventListener(ChallengeRequest.class.getSimpleName(),
                ChallengeRequest.class, new GenericListener<ChallengeRequest>(this));
        server.addEventListener(ChatRequest.class.getSimpleName(),
                ChatRequest.class, new GenericListener<ChatRequest>(this));
        server.addEventListener(ClearPracticeProblemRequest.class.getSimpleName(),
                ClearPracticeProblemRequest.class, new GenericListener<ClearPracticeProblemRequest>(this));
        server.addEventListener(ClearPracticeRequest.class.getSimpleName(),
                ClearPracticeRequest.class, new GenericListener<ClearPracticeRequest>(this));
        server.addEventListener(CloseDivSummaryRequest.class.getSimpleName(),
                CloseDivSummaryRequest.class, new GenericListener<CloseDivSummaryRequest>(this));
        server.addEventListener(CloseLeaderBoardRequest.class.getSimpleName(),
                CloseLeaderBoardRequest.class, new GenericListener<CloseLeaderBoardRequest>(this));
        server.addEventListener(CloseProblemRequest.class.getSimpleName(),
                CloseProblemRequest.class, new GenericListener<CloseProblemRequest>(this));
        server.addEventListener(CloseSummaryRequest.class.getSimpleName(),
                CloseSummaryRequest.class, new GenericListener<CloseSummaryRequest>(this));
        server.addEventListener(CloseTeamListRequest.class.getSimpleName(),
                CloseTeamListRequest.class, new GenericListener<CloseTeamListRequest>(this));
        server.addEventListener(CoderHistoryRequest.class.getSimpleName(),
                CoderHistoryRequest.class, new GenericListener<CoderHistoryRequest>(this));
        server.addEventListener(CoderInfoRequest.class.getSimpleName(),
                CoderInfoRequest.class, new GenericListener<CoderInfoRequest>(this));
        server.addEventListener(CompileRequest.class.getSimpleName(),
                CompileRequest.class, new GenericListener<CompileRequest>(this));
        server.addEventListener(DivSummaryRequest.class.getSimpleName(),
                DivSummaryRequest.class, new GenericListener<DivSummaryRequest>(this));
        server.addEventListener(EnterRequest.class.getSimpleName(),
                EnterRequest.class, new GenericListener<EnterRequest>(this));
        server.addEventListener(EnterRoundRequest.class.getSimpleName(),
                EnterRoundRequest.class, new GenericListener<EnterRoundRequest>(this));
        server.addEventListener(ErrorReportRequest.class.getSimpleName(),
                ErrorReportRequest.class, new GenericListener<ErrorReportRequest>(this));
        server.addEventListener(ErrorRequest.class.getSimpleName(),
                ErrorRequest.class, new GenericListener<ErrorRequest>(this));
        server.addEventListener(ExchangeKeyRequest.class.getSimpleName(),
                ExchangeKeyRequest.class, new GenericListener<ExchangeKeyRequest>(this));
        server.addEventListener(GenericPopupRequest.class.getSimpleName(),
                GenericPopupRequest.class, new GenericListener<GenericPopupRequest>(this));
        server.addEventListener(GetAdminBroadcastsRequest.class.getSimpleName(),
                GetAdminBroadcastsRequest.class, new GenericListener<GetAdminBroadcastsRequest>(this));
        server.addEventListener(GetChallengeProblemRequest.class.getSimpleName(),
                GetChallengeProblemRequest.class, new GenericListener<GetChallengeProblemRequest>(this));
        server.addEventListener(GetCurrentAppletVersionRequest.class.getSimpleName(),
                GetCurrentAppletVersionRequest.class, new GenericListener<GetCurrentAppletVersionRequest>(this));
        server.addEventListener(GetImportantMessagesRequest.class.getSimpleName(),
                GetImportantMessagesRequest.class, new GenericListener<GetImportantMessagesRequest>(this));
        server.addEventListener(GetLeaderBoardRequest.class.getSimpleName(),
                GetLeaderBoardRequest.class, new GenericListener<GetLeaderBoardRequest>(this));
        server.addEventListener(GetProblemRequest.class.getSimpleName(),
                GetProblemRequest.class, new GenericListener<GetProblemRequest>(this));
        server.addEventListener(GetSourceCodeRequest.class.getSimpleName(),
                GetSourceCodeRequest.class, new GenericListener<GetSourceCodeRequest>(this));
        server.addEventListener(GetTeamListRequest.class.getSimpleName(),
                GetTeamListRequest.class, new GenericListener<GetTeamListRequest>(this));
        server.addEventListener(JoinTeamRequest.class.getSimpleName(),
                JoinTeamRequest.class, new GenericListener<JoinTeamRequest>(this));
        server.addEventListener(KeepAliveRequest.class.getSimpleName(),
                KeepAliveRequest.class, new GenericListener<KeepAliveRequest>(this));
        server.addEventListener(LeaveTeamRequest.class.getSimpleName(),
                LeaveTeamRequest.class, new GenericListener<LeaveTeamRequest>(this));
        server.addEventListener(LongSubmitRequest.class.getSimpleName(),
                LongSubmitRequest.class, new GenericListener<LongSubmitRequest>(this));
        server.addEventListener(LongTestResultsRequest.class.getSimpleName(),
                LongTestResultsRequest.class, new GenericListener<LongTestResultsRequest>(this));
        server.addEventListener(MoveRequest.class.getSimpleName(),
                MoveRequest.class, new GenericListener<MoveRequest>(this));
        server.addEventListener(OpenComponentForCodingRequest.class.getSimpleName(),
                OpenComponentForCodingRequest.class, new GenericListener<OpenComponentForCodingRequest>(this));
        server.addEventListener(OpenProblemForReadingRequest.class.getSimpleName(),
                OpenProblemForReadingRequest.class, new GenericListener<OpenProblemForReadingRequest>(this));
        server.addEventListener(OpenSummaryRequest.class.getSimpleName(),
                OpenSummaryRequest.class, new GenericListener<OpenSummaryRequest>(this));
        server.addEventListener(PracticeSystemTestRequest.class.getSimpleName(),
                PracticeSystemTestRequest.class, new GenericListener<PracticeSystemTestRequest>(this));
        server.addEventListener(ReadMessageRequest.class.getSimpleName(),
                ReadMessageRequest.class, new GenericListener<ReadMessageRequest>(this));
        server.addEventListener(ReconnectRequest.class.getSimpleName(),
                ReconnectRequest.class, new GenericListener<ReconnectRequest>(this));
        server.addEventListener(RegisteredRoundListRequest.class.getSimpleName(),
                RegisteredRoundListRequest.class, new GenericListener<RegisteredRoundListRequest>(this));
        server.addEventListener(RegisterInfoRequest.class.getSimpleName(),
                RegisterInfoRequest.class, new GenericListener<RegisterInfoRequest>(this));
        server.addEventListener(RegisterRequest.class.getSimpleName(),
                RegisterRequest.class, new GenericListener<RegisterRequest>(this));
        server.addEventListener(RegisterRoomRequest.class.getSimpleName(),
                RegisterRoomRequest.class, new GenericListener<RegisterRoomRequest>(this));
        server.addEventListener(RegisterUsersRequest.class.getSimpleName(),
                RegisterUsersRequest.class, new GenericListener<RegisterUsersRequest>(this));
        server.addEventListener(RegisterWeakestLinkTeamRequest.class.getSimpleName(),
                RegisterWeakestLinkTeamRequest.class, new GenericListener<RegisterWeakestLinkTeamRequest>(this));
        server.addEventListener(RemoveTeamMemberRequest.class.getSimpleName(),
                RemoveTeamMemberRequest.class, new GenericListener<RemoveTeamMemberRequest>(this));
        server.addEventListener(RoundProblemsRequest.class.getSimpleName(),
                RoundProblemsRequest.class, new GenericListener<RoundProblemsRequest>(this));
        server.addEventListener(RoundScheduleRequest.class.getSimpleName(),
                RoundScheduleRequest.class, new GenericListener<RoundScheduleRequest>(this));
        server.addEventListener(RoundStatsRequest.class.getSimpleName(),
                RoundStatsRequest.class, new GenericListener<RoundStatsRequest>(this));
        server.addEventListener(SaveRequest.class.getSimpleName(),
                SaveRequest.class, new GenericListener<SaveRequest>(this));
        server.addEventListener(SearchRequest.class.getSimpleName(),
                SearchRequest.class, new GenericListener<SearchRequest>(this));
        server.addEventListener(SetLanguageRequest.class.getSimpleName(),
                SetLanguageRequest.class, new GenericListener<SetLanguageRequest>(this));
        server.addEventListener(SubmitRequest.class.getSimpleName(),
                SubmitRequest.class, new GenericListener<SubmitRequest>(this));
        server.addEventListener(SynchTimeRequest.class.getSimpleName(),
                SynchTimeRequest.class, new GenericListener<SynchTimeRequest>(this));
        server.addEventListener(SystestResultsRequest.class.getSimpleName(),
                SystestResultsRequest.class, new GenericListener<SystestResultsRequest>(this));
        server.addEventListener(TestInfoRequest.class.getSimpleName(),
                TestInfoRequest.class, new GenericListener<TestInfoRequest>(this));
        server.addEventListener(TestRequest.class.getSimpleName(),
                TestRequest.class, new GenericListener<TestRequest>(this));
        server.addEventListener(BatchTestRequest.class.getSimpleName(),
                BatchTestRequest.class, new GenericListener<BatchTestRequest>(this));
        server.addEventListener(ToggleChatRequest.class.getSimpleName(),
                ToggleChatRequest.class, new GenericListener<ToggleChatRequest>(this));
        server.addEventListener(UnassignComponentRequest.class.getSimpleName(),
                UnassignComponentRequest.class, new GenericListener<UnassignComponentRequest>(this));
        server.addEventListener(UnwatchRequest.class.getSimpleName(),
                UnwatchRequest.class, new GenericListener<UnwatchRequest>(this));
        server.addEventListener(VerifyRequest.class.getSimpleName(),
                VerifyRequest.class, new GenericListener<VerifyRequest>(this));
        server.addEventListener(VerifyResultRequest.class.getSimpleName(),
                VerifyResultRequest.class, new GenericListener<VerifyResultRequest>(this));
        server.addEventListener(ViewQueueRequest.class.getSimpleName(),
                ViewQueueRequest.class, new GenericListener<ViewQueueRequest>(this));
        server.addEventListener(VisitedPracticeRequest.class.getSimpleName(),
                VisitedPracticeRequest.class, new GenericListener<VisitedPracticeRequest>(this));
        server.addEventListener(VoteRequest.class.getSimpleName(),
                VoteRequest.class, new GenericListener<VoteRequest>(this));
        server.addEventListener(WatchRequest.class.getSimpleName(),
                WatchRequest.class, new GenericListener<WatchRequest>(this));
        server.addEventListener(WLMyTeamInfoRequest.class.getSimpleName(),
                WLMyTeamInfoRequest.class, new GenericListener<WLMyTeamInfoRequest>(this));
        server.addEventListener(WLTeamsInfoRequest.class.getSimpleName(),
                WLTeamsInfoRequest.class, new GenericListener<WLTeamsInfoRequest>(this));
        server.addEventListener(RoundAccessRequest.class.getSimpleName(),
                RoundAccessRequest.class, new GenericListener<RoundAccessRequest>(this));
        server.addEventListener(ChangeRoundRequest.class.getSimpleName(),
                ChangeRoundRequest.class, new GenericListener<ChangeRoundRequest>(this));
        server.addEventListener(LoadRoundRequest.class.getSimpleName(),
                LoadRoundRequest.class, new GenericListener<LoadRoundRequest>(this));
    }

    /**
     * Start the web socket server.
     */
    public void start() {
        registerListeners();

        // start
        logger.info("Starting the WebSocketServer...");
//        CoreServices.start();
        server.start();
        logger.info("Done starting the WebSocketServer");

        //start the main listener connection
        mlc = new MainListenerConnector(mlIP, mlP, this);
        boolean started = mlc.start();
        logger.info("started web socket: " + started);

    }

    /**
     * Stop the web socket server.
     */
    public void stop() {
        logger.info("Stopping the WebSocketServer...");
        server.stop();
//        CoreServices.stop();
        logger.info("Done stopping the WebSocketServer");
    }


    /**
     * The main method to start the WebSocket server.
     *
     * @param argv the input arguments
     */
    public static void main(String[] argv) {
        final String usageMsg = "Usage: java -cp [classpath] [javaClass] [port] [mainListenerIP] [mainListenerPort]\n"
                + "[classpath] is the class path containing the [javaClass] and all the dependencies\n"
                + "[javaClass] is this com.topcoder.server.listener.wss.WebSocketServer\n"
                + "[port] should be a valid port number\n"
                + "[mainListenerIP] should be a valid IP address"
                + "[mainListenerPort] should be a valid port number.";

        int len = argv.length;
        if (len < 1) {
            System.err.println(usageMsg);
            return;
        }

        // parse port number
        int port = -1;
        try {
            port = Integer.parseInt(argv[0]);
        } catch (NumberFormatException ex) {
            System.err.println(PORT_VALIDATION_MESSAGE);
            System.err.println(usageMsg);
            return;
        }
        String mainListenerIP = "127.0.0.1";
        int mainListenerPort = 5555;
        try {
            mainListenerIP = argv[1];
            mainListenerPort = Integer.parseInt(argv[2]);
        } catch (Exception ex) {
            //ignore
        }

        // create and start the WebSocket server
        try {
            final WebSocketServer server = new WebSocketServer(port, mainListenerIP, mainListenerPort);
            Thread shutDownHook = new Thread(new Runnable() {
                @Override
                public void run() {
                    server.stop();
                }
            });

            Runtime.getRuntime().addShutdownHook(shutDownHook);
            server.start();
        } catch (IllegalArgumentException ex) {
            System.err.println(ex.getMessage());
            System.err.println(usageMsg);
        }
    }
}
