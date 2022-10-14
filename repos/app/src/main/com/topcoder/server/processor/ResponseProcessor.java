/*
 * Copyright (C)  - 2014 TopCoder Inc., All Rights Reserved.
 */

package com.topcoder.server.processor;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Vector;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contest.SurveyChoiceData;
import com.topcoder.netCommon.contest.SurveyQuestionData;
import com.topcoder.netCommon.contestantMessages.UserInfo;
import com.topcoder.netCommon.contestantMessages.response.AssignComponentsResponse;
import com.topcoder.netCommon.contestantMessages.response.BaseResponse;
import com.topcoder.netCommon.contestantMessages.response.ChallengeInfoResponse;
import com.topcoder.netCommon.contestantMessages.response.ChallengesListResponse;
import com.topcoder.netCommon.contestantMessages.response.ComponentAssignmentDataResponse;
import com.topcoder.netCommon.contestantMessages.response.CreateCategoryListResponse;
import com.topcoder.netCommon.contestantMessages.response.CreateChallengeTableResponse;
import com.topcoder.netCommon.contestantMessages.response.CreateLeaderBoardResponse;
import com.topcoder.netCommon.contestantMessages.response.CreateMenuResponse;
import com.topcoder.netCommon.contestantMessages.response.CreateProblemsResponse;
import com.topcoder.netCommon.contestantMessages.response.CreateRoomListResponse;
import com.topcoder.netCommon.contestantMessages.response.CreateRoundListResponse;
import com.topcoder.netCommon.contestantMessages.response.CreateUserListResponse;
import com.topcoder.netCommon.contestantMessages.response.CreateVisitedPracticeResponse;
import com.topcoder.netCommon.contestantMessages.response.EnableRoundResponse;
import com.topcoder.netCommon.contestantMessages.response.ExchangeKeyResponse;
import com.topcoder.netCommon.contestantMessages.response.ForcedLogoutResponse;
import com.topcoder.netCommon.contestantMessages.response.GetCurrentAppletVersionResponse;
import com.topcoder.netCommon.contestantMessages.response.GetImportantMessagesResponse;
import com.topcoder.netCommon.contestantMessages.response.GetProblemResponse;
import com.topcoder.netCommon.contestantMessages.response.GetTeamProblemResponse;
import com.topcoder.netCommon.contestantMessages.response.ImportantMessageResponse;
import com.topcoder.netCommon.contestantMessages.response.KeepAliveInitializationDataResponse;
import com.topcoder.netCommon.contestantMessages.response.KeepAliveResponse;
import com.topcoder.netCommon.contestantMessages.response.LoginResponse;
import com.topcoder.netCommon.contestantMessages.response.LongTestResultsResponse;
import com.topcoder.netCommon.contestantMessages.response.NoBadgeIdResponse;
import com.topcoder.netCommon.contestantMessages.response.OpenComponentResponse;
import com.topcoder.netCommon.contestantMessages.response.PhaseDataResponse;
import com.topcoder.netCommon.contestantMessages.response.PopUpGenericResponse;
import com.topcoder.netCommon.contestantMessages.response.PracticeSystemTestResponse;
import com.topcoder.netCommon.contestantMessages.response.PracticeSystemTestResultResponse;
import com.topcoder.netCommon.contestantMessages.response.ReconnectResponse;
import com.topcoder.netCommon.contestantMessages.response.RegisteredRoundListResponse;
import com.topcoder.netCommon.contestantMessages.response.RegisteredUsersResponse;
import com.topcoder.netCommon.contestantMessages.response.RoomInfoResponse;
import com.topcoder.netCommon.contestantMessages.response.RoundProblemsResponse;
import com.topcoder.netCommon.contestantMessages.response.RoundScheduleResponse;
import com.topcoder.netCommon.contestantMessages.response.SubmissionHistoryResponse;
import com.topcoder.netCommon.contestantMessages.response.SynchTimeResponse;
import com.topcoder.netCommon.contestantMessages.response.SystestProgressResponse;
import com.topcoder.netCommon.contestantMessages.response.TestInfoResponse;
import com.topcoder.netCommon.contestantMessages.response.UnsynchronizeResponse;
import com.topcoder.netCommon.contestantMessages.response.UpdateChatResponse;
import com.topcoder.netCommon.contestantMessages.response.UpdateCoderComponentResponse;
import com.topcoder.netCommon.contestantMessages.response.UpdateCoderPointsResponse;
import com.topcoder.netCommon.contestantMessages.response.UpdateLeaderBoardResponse;
import com.topcoder.netCommon.contestantMessages.response.UpdateMenuResponse;
import com.topcoder.netCommon.contestantMessages.response.UpdateRoundListResponse;
import com.topcoder.netCommon.contestantMessages.response.UpdateTeamListResponse;
import com.topcoder.netCommon.contestantMessages.response.UpdateUserListResponse;
import com.topcoder.netCommon.contestantMessages.response.UserInfoResponse;
import com.topcoder.netCommon.contestantMessages.response.VerifyResponse;
import com.topcoder.netCommon.contestantMessages.response.VerifyResultResponse;
import com.topcoder.netCommon.contestantMessages.response.WatchResponse;
import com.topcoder.netCommon.contestantMessages.response.data.CategoryData;
import com.topcoder.netCommon.contestantMessages.response.data.ChallengeData;
import com.topcoder.netCommon.contestantMessages.response.data.CoderComponentItem;
import com.topcoder.netCommon.contestantMessages.response.data.CoderItem;
import com.topcoder.netCommon.contestantMessages.response.data.ComponentLabel;
import com.topcoder.netCommon.contestantMessages.response.data.LeaderboardItem;
import com.topcoder.netCommon.contestantMessages.response.data.LongCoderComponentItem;
import com.topcoder.netCommon.contestantMessages.response.data.LongCoderItem;
import com.topcoder.netCommon.contestantMessages.response.data.LongTestResultData;
import com.topcoder.netCommon.contestantMessages.response.data.PhaseData;
import com.topcoder.netCommon.contestantMessages.response.data.PracticeTestResultData;
import com.topcoder.netCommon.contestantMessages.response.data.ProblemLabel;
import com.topcoder.netCommon.contestantMessages.response.data.RoomData;
import com.topcoder.netCommon.contestantMessages.response.data.RoundData;
import com.topcoder.netCommon.contestantMessages.response.data.TeamListInfo;
import com.topcoder.netCommon.contestantMessages.response.data.UserListItem;
import com.topcoder.netCommon.testerMessages.PongResponse;
import com.topcoder.server.common.BaseCoderComponent;
import com.topcoder.server.common.BaseCodingRoom;
import com.topcoder.server.common.BaseRound;
import com.topcoder.server.common.ChatEvent;
import com.topcoder.server.common.Coder;
import com.topcoder.server.common.CoderComponent;
import com.topcoder.server.common.CoderHistory;
import com.topcoder.server.common.ContestEvent;
import com.topcoder.server.common.ContestRound;
import com.topcoder.server.common.ForwarderLongContestRound;
import com.topcoder.server.common.LeaderBoard;
import com.topcoder.server.common.LeaderEvent;
import com.topcoder.server.common.LobbyFullEvent;
import com.topcoder.server.common.LongCoderComponent;
import com.topcoder.server.common.LongContestCoder;
import com.topcoder.server.common.LongContestRoom;
import com.topcoder.server.common.PhaseEvent;
import com.topcoder.server.common.Rating;
import com.topcoder.server.common.Registration;
import com.topcoder.server.common.ResponseEvent;
import com.topcoder.server.common.Room;
import com.topcoder.server.common.Round;
import com.topcoder.server.common.RoundComponent;
import com.topcoder.server.common.ServerContestConstants;
import com.topcoder.server.common.SurveyAnswer;
import com.topcoder.server.common.SurveyQuestion;
import com.topcoder.server.common.Team;
import com.topcoder.server.common.TeamCoder;
import com.topcoder.server.common.TeamContestRoom;
import com.topcoder.server.common.User;
import com.topcoder.server.common.WeakestLinkRound;
import com.topcoder.server.contest.ImportantMessageData;
import com.topcoder.server.ejb.TestServices.LongSubmissionData;
import com.topcoder.server.ejb.TestServices.LongTestResult;
import com.topcoder.server.listener.KeepAliveProperties;
import com.topcoder.server.listener.ListenerInterface;
import com.topcoder.server.listener.ListenerMain;
import com.topcoder.server.listener.MessageBuilder;
import com.topcoder.server.services.CoreServices;
import com.topcoder.shared.netCommon.SealedSerializable;
import com.topcoder.shared.netCommon.messages.spectator.SpectatorLoginResult;
import com.topcoder.shared.problem.DataType;
import com.topcoder.shared.problem.Problem;
import com.topcoder.shared.problem.SimpleComponent;
import com.topcoder.shared.util.Formatters;
import com.topcoder.shared.util.logging.Logger;


/**
 * Processes all responses being sent to the ResponseHandler and creates
 * the network objects in the format the client is expecting.  The majority of the
 * methods create the appropriate Responses for the applet based on the activity
 * that has just been completed.
 *
 * <p>
 * Changes in version 1.1 (Module Assembly - TCC Web Socket - Get Registered Rounds and
 * Round Problems):
 * <ol>
 *      <li>Add {@link #getRegisteredRoundList(Integer, User)} method to process
 *      RegisteredRoundListRequest.</li>
 *      <li>Add {@link #getRoundProblems(Integer, User, long, int)} method to process
 *      RoundProblemsRequest.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.2 (Return Peak Memory Usage for Marathon Match Cpp v1.0):
 * <ol>
 *      <li>Update {@link #longTestResultsResponse(int roundId, String handle, 
 *                      int componentId, LongTestResult[] results, int resultsType)} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.3 (TopCoder Competition Engine - Responses for Challenges and Challengers):
 * <ol>
 *      <li>Update {@link #move()} method to send the previous challenges in a room to coders in the web arena</li>
 *      <li>Update {@link #reconnectSuccess()} method to send the previous challenges in a room to 
 *          coders in the web arena when the user reconnects to a room</li>
 *      <li>Added sendResponseToWebArenaUsers(BaseCodingRoom codingRoom, BaseResponse response) to send response to web arena users only</li>
 *      <li>Added getRoomChallengeData(BaseCodingRoom contestRoom) to construct previous challenges data.</li>
 * </ol>
 * </p>
 *
 * @author Graham Hesselroth, dexy, gondzo
 * @version 1.3
 */
public final class ResponseProcessor {

    private ResponseProcessor() {
    }

    /**
     * Category for logging.
     */
    private static Logger trace = Logger.getLogger(ResponseProcessor.class);

    /**
     * Use to store a list of messages when a client is temporarily disconnected.
     * The keys are connection IDs, and the values are array lists of messages.
     * When a client is temporarily disconnected, an empty array list is inserted,
     * indicating the messages are needed to be accumulated.
     */
    private static Map disconnectedClientMessages = new HashMap();

    /**
     * Stores all the error messages sent back to the client.
     */
    private static ResourceBundle g_errorResources;

    static {
        try {
            g_errorResources = ResourceBundle.getBundle("ErrorMessages");
        } catch (MissingResourceException mre) {
            trace.error("Failed to find ErrorMessages resources.", mre);
        }
    }

    private static ListenerInterface s_listener;

    public static void setListener(ListenerInterface listener) {
        s_listener = listener;
    }

    static void shutDownConnection(Integer connectionID) {
        if (s_listener != null) {
            // Notify the listener that the connection is closed.
            s_listener.shutdown(connectionID.intValue());
        }
        synchronized (disconnectedClientMessages) {
            disconnectedClientMessages.remove(connectionID);
        }
    }

    private static SpecAppProcessor specAppProcessor;

    /**
     * Internal class to send out the timer update messages to all clients
     * every time period.
     */
    private static class HeartbeatRunner implements Runnable {

        private int m_delay;

        private HeartbeatRunner(int delay) {
            m_delay = delay;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(m_delay);
                } catch (InterruptedException ie) {
                    trace.error("Heartbeat thread was interrupted", ie);
                }

                specAppProcessor.timerUpdate();

                Iterator allConnections = RequestProcessor.allConnectionIDs();
                while (allConnections.hasNext()) {
                    Integer connectionID = (Integer) allConnections.next();
                    int userID = RequestProcessor.getUserID(connectionID);
                    if (userID != RequestProcessor.INVALID_USER) {
                        User user = CoreServices.getUser(userID, false);
                        if (!user.isSpectator()) {
                            int roomID = user.getRoomID();
                            if (roomID != ContestConstants.INVALID_ROOM && !ContestConstants.isPracticeRoomType(user.getRoomType())) {
                                process(connectionID, new SynchTimeResponse());
                            }
                        }
                    }
                }
            }
        }
    }


    static void setSpecAppProcessor(SpecAppProcessor specAppProcessor) {
        ResponseProcessor.specAppProcessor = specAppProcessor;
    }

    /**
     * Intializes the ResponseProcessor for runngin and starts the HeartBeat thread.
     */
    public static void start() {
        // TODO create data for standard responses.
        createRoomLists();

        specAppProcessor.setListener(s_listener);

        ResourceBundle processorSettings = null;
        try {
            processorSettings = ResourceBundle.getBundle("Processor");
        } catch (MissingResourceException mre) {
            trace.error("Failed to load Processor Settings", mre);
        }

        int heartbeatSeconds = 0;
        try {
            heartbeatSeconds = Integer.parseInt(processorSettings.getString("processor.heartbeat.delay"));
        } catch (Exception e) {
            trace.error("Failed to get heartbeat seconds from properties", e);
        }
        if (heartbeatSeconds > 0) {
            Runnable heartbeat = new HeartbeatRunner(heartbeatSeconds * 1000);
            Thread timerHeartBeat = new Thread(heartbeat, "ResponseProcessor.TimerHeartbeat");
            timerHeartBeat.setDaemon(true);
            timerHeartBeat.start();
        }


    }

    public static void stop() {
    }

    /**
     * Actually performs the send by invoking the Listener.send API.
     */
    private static void processInternal(Integer connectionID, Object responses) {
        if (connectionID != null) {
            processInternal(connectionID.intValue(), responses);
        }
    }

    private static void processInternal(int connectionId, Object responses) {
        if (ListenerMain.getSocketConnector().isConnected(connectionId)){
            ListenerMain.getSocketConnector().write(connectionId,responses);
        }else{
            if (s_listener != null) {
                s_listener.send(connectionId, responses);
            }
        }
    }

    public static void sendMessageToCoderId(int coderId, BaseResponse response) {
        Integer connectionId = RequestProcessor.getConnectionID(coderId);
        sendMessageToConnectionId(connectionId, response);
    }

    public static void sendMessageToCoderId(int coderId, ArrayList list) {
        Integer connectionId = RequestProcessor.getConnectionID(coderId);
        processInternal(connectionId, list);
    }

    public static void sendMessageToConnectionId(Integer connectionId, BaseResponse response) {
        processInternal(connectionId, response);
    }
    /**
     * Send the response/s to a single user on the given connection.
     */
    public static void process(Integer connectionID, Object response) {
        processInternal(connectionID, response);
    }

    /**
     * Send the given response to all the connectionIDs passed in.
     */
    public static void process(Iterator connectionIDs, Object responses) {
        while (connectionIDs != null && connectionIDs.hasNext()) {
            processInternal((Integer) connectionIDs.next(), responses);
        }
    }

    /**
     * Creates a simple message Response with the given message and title.
     */
    static PopUpGenericResponse simpleMessage(String message, String title) {
        PopUpGenericResponse response = new PopUpGenericResponse(title, message, ContestConstants.GENERIC, ContestConstants.LABEL);
        return response;
    }

    /**
     * Creates a big message Response with the given message and title.
     */
    static PopUpGenericResponse simpleBigMessage(String message, String title) {
        /* CSHandler may throw UTFDataFormatException if this exceeds 64k */
        PopUpGenericResponse response = new PopUpGenericResponse(title, message, ContestConstants.GENERIC, ContestConstants.TEXT_AREA);
        return response;
    }


    private static CreateUserListResponse createUserList(List handles, List ratings, int listType, int roomID, int roomType) {
        if (trace.isDebugEnabled()) trace.debug("user list size " + handles.size());
        UserListItem[] userList = new UserListItem[handles.size()];
        for (int i = 0; i < handles.size(); i++) {
            userList[i] = new UserListItem((String) handles.get(i), ((Integer) ratings.get(i)).intValue(), ContestConstants.SINGLE_USER);
        }
        return new CreateUserListResponse(listType, userList, roomType, roomID);
    }


    private static CreateUserListResponse createRoomUserList(Room room, int roomID) {
        ArrayList[] userData = room.getUserData();
        return createUserList(userData[0], userData[1], ContestConstants.ROOM_USERS, roomID, room.getType());
    }

    /**
     * Returns a response object to create the menu of active rooms
     */
    // TODO need to add some mechanism to clean this up when a contest is updated
    // TODO we really want this created when the room assignments are done and not during the intial moves.
    private static Map s_activeRoomMenus = new HashMap();

    // Called from CoreServices when the contest is updated.  Needs to be broadcast.
    static void updateActiveRoomMenus(Round round) {
        Vector coderRoomList = new Vector();
        RoomData adminRoom = null;
        ArrayList roomIDs = new ArrayList();


        for (Iterator allRooms = round.getAllRoomIDsListClone().iterator(); allRooms.hasNext();) {
            BaseCodingRoom nextRoom = (BaseCodingRoom) CoreServices.getRoom(((Integer) allRooms.next()).intValue(), false);
            if (nextRoom.isAdminRoom()) {
                if (adminRoom == null) {
                    adminRoom = new RoomData(nextRoom.getRoomID(), nextRoom.getType(), nextRoom.getName(), nextRoom.getRoundID(), nextRoom.getDivisionID());
                } else {
                    throw new IllegalStateException("Two admin rooms configured for round #" + round.getRoundID() + " room IDs: " + nextRoom.getRoomID() + ", " + adminRoom.getRoomID());
                }
            } else {
                coderRoomList.add(new RoomData(nextRoom.getRoomID(), nextRoom.getType(), nextRoom.getName(), nextRoom.getRoundID(), nextRoom.getDivisionID()));
            }
            roomIDs.add(new Integer(nextRoom.getRoomID()));
        }
        RoomData coderRooms[] = new RoomData[coderRoomList.size()];
        coderRoomList.copyInto(coderRooms);
        CreateRoomListResponse response = null;
        if (adminRoom == null) {
            response = new CreateRoomListResponse(round.getRoundID(), coderRooms);
        } else {
            response = new CreateRoomListResponse(round.getRoundID(), coderRooms, adminRoom);
        }

        //this was at the top, seems better placed down here
        synchronized (s_activeRoomMenus) {
            trace.info("STARTING S_ACTIVEROOMMENUS");
            s_activeRoomMenus.put(round.getCacheKey(), response);
            if (trace.isDebugEnabled()) trace.debug("Setting activeRoomsIDs with key = " + round.getCacheKey());
            trace.info("DONE S_ACTIVEROOMMENUS");
        }
    }

    /**
     * Helper method to create a Response for the menu of active rooms.
     */
    private static CreateRoomListResponse createActiveRoomMenu(BaseCodingRoom room) {
        CreateRoomListResponse response = null;

        String cacheKey = BaseRound.getCacheKey(room.getRoundID());

        synchronized (s_activeRoomMenus) {
            response = (CreateRoomListResponse) s_activeRoomMenus.get(cacheKey);
        }

        if (response == null) {
            updateActiveRoomMenus(CoreServices.getContestRound(room.getRoundID()));
            response = (CreateRoomListResponse) s_activeRoomMenus.get(cacheKey);
        }

        return response;
    }

    static CreateRoomListResponse createActiveRoomMenu(long roundID) {
        CreateRoomListResponse response = null;

        String cacheKey = BaseRound.getCacheKey((int) roundID);

        synchronized (s_activeRoomMenus) {
            response = (CreateRoomListResponse) s_activeRoomMenus.get(cacheKey);
        }

        if (response == null) {
            updateActiveRoomMenus(CoreServices.getContestRound((int) roundID));
            response = (CreateRoomListResponse) s_activeRoomMenus.get(cacheKey);
        }

        return response;
    }

    /**
     * Constructs a CREATE_CHALLENGE_TABLE_RS Response for the Applet.
     */
    public static CreateChallengeTableResponse createChallengeTable(BaseCodingRoom room, int roomID, int roomType) {
        Round round = CoreServices.getContestRound(room.getRoundID());
        boolean sendFinalComponentItem = round.getPhase() >= ContestConstants.CONTEST_COMPLETE_PHASE;
        synchronized (room) {
            CoderItem[] coderItems = new CoderItem[room.getNumCoders()];
            int k = 0;
            for (Iterator coderIterator = room.getAllCoders(); coderIterator.hasNext();) {
                Coder coder = (Coder) coderIterator.next();
                long[] componentIDs = coder.getComponentIDs();
                CoderComponentItem[] coderComponentItems = new CoderComponentItem[componentIDs.length];
                for (int i = 0; i < componentIDs.length; i++) {
                    long componentID = componentIDs[i];
                    BaseCoderComponent coderComponent = coder.getComponent(componentID);
                    if (trace.isDebugEnabled()) trace.debug("Here is the LANGUAGE "+coderComponent.getLanguage());
                    coderComponentItems[i] = newCoderComponentItem(coderComponent, sendFinalComponentItem);
                }
                if (coder instanceof LongContestCoder) {
                    coderItems[k++] = new LongCoderItem(coder.getName(), coder.getRating(), coder.getPoints(), coderComponentItems, ContestConstants.SINGLE_USER, coder.getFinalPoints());
                } else {
                    coderItems[k++] = new CoderItem(coder.getName(), coder.getRating(), coder.getPoints(), coderComponentItems, (coder instanceof TeamCoder) ? ContestConstants.TEAM_USER : ContestConstants.SINGLE_USER);
                    if (coder instanceof TeamCoder) {
                        ArrayList memberNames = new ArrayList();
                        for (Iterator it = ((TeamCoder) coder).getMemberCoders().iterator(); it.hasNext();) {
                            memberNames.add(((Coder) it.next()).getName());
                        }
                        coderItems[k - 1].setMemberNames(memberNames);
                    }
                }
            }
            return new CreateChallengeTableResponse(coderItems, roomType, roomID);
        }
    }

    //    /**
    //     * Constructs an ASSIGNED_USERS Response for the Applet.
    //     */
    //    private static CreateUserListResponse createAssignedUsers( ContestRoom room )
    //    {
    //        int roomID = room.getRoomID();
    //        int roomType = room.getType();
    //        ArrayList [] userData = room.getAssignedCoderData();
    //        return createUserList(userData[0], userData[1], ContestConstants.ASSIGNED_USERS, roomID, roomType );
    //    }

    /**
     * Creates the set of problems for the applet.  May be called with a specific room index and type
     * which is required for Watch Rooms.
     */
    private static CreateProblemsResponse createProblems(long roundID, int divisionID) {
        Round round = CoreServices.getContestRound((int) roundID);
        return new CreateProblemsResponse(round.getProblemLabels(divisionID), round.getComponentLabels(divisionID), roundID, divisionID);
    }

    /**
     * Public APIs will exist for each of the following types (though some may be combined into common
     * methods where it makes sense to do so).  Each method will construct an ResponseObject and then
     * invoke the ResponseHandler passing the connectionID and ResponseObject.
     * Essentially these methods would just be type-safe methods of creating a Response object and
     * sending it to the correct set of users.
     */
    static void loginFailed(Integer connectionID, int requestID, String message) {
        if (trace.isDebugEnabled()) trace.debug("loginFailed: " + message);
        MessageBuilder msgBuilder = new MessageBuilder(2);
        LoginResponse response = new LoginResponse(false);
        msgBuilder.add(response);
        msgBuilder.add(simpleMessage(message, g_errorResources.getString("ERROR")));
        process(connectionID, msgBuilder.buildResponseToSyncRequest(requestID));
    }

    static void currentAppletVersion(Integer connectionID, int requestID, String version) {
        MessageBuilder msgBuilder = new MessageBuilder();
        GetCurrentAppletVersionResponse resp = new GetCurrentAppletVersionResponse(version);
        msgBuilder.add(resp);
        process(connectionID, msgBuilder.buildResponseToSyncRequest(requestID));
    }

    private static CreateMenuResponse s_activeModeratedChats;
    private static CreateMenuResponse s_lobbyContests;
    private static CreateRoundListResponse s_activeContests;
    private static RoundData[] s_activeContestRequiringRegisteredUser;
    private static CreateRoundListResponse s_activeAdminContests;
    private static CreateRoundListResponse s_practiceContests;
    private static CreateCategoryListResponse s_categories;
    private static ArrayList s_activeModeratedChatIDs;

    public static void updateActiveContest(long roundID, boolean enabled) {
        RoundData rounds[] = s_activeContests.getRoundData();
        for (int i = 0; i < rounds.length; i++) {
            if (rounds[i].getRoundID() == roundID) {
                rounds[i].setEnabled(enabled);
                return;
            }
        }
        rounds = s_activeContestRequiringRegisteredUser;
        for (int i = 0; i < rounds.length; i++) {
            if (rounds[i].getRoundID() == roundID) {
                rounds[i].setEnabled(enabled);
                return;
            }
        }
        throw new IllegalArgumentException("Bad round ID " + roundID);
    }


    /*added by SYHAAS 2002-05-18*/
    private static void updateActiveChat(String name, String status) {
        ArrayList chatNames = s_activeModeratedChats.getNames();
        ArrayList chatStatus = s_activeModeratedChats.getStatii();
        for (int i = 0; i < chatNames.size(); i++) {
            if (chatNames.get(i).equals(name)) chatStatus.set(i, status);
        }
    }

    // TODO need some mechanism to update the lists with new rooms at runtime.
    static void createRoomLists() {
        trace.debug("Starting createRoomLists() ...");
        refreshActiveContestRoomLists();
        createPracticeRoomLists();
        createCategoriesList();
        createLobbyRoomLists();
        if (trace.isDebugEnabled()) trace.debug("Finished creating room lists. Active Count = " + s_activeContests.getRoundData().length +
                " Practice Count = " + s_practiceContests.getRoundData().length);
    }

    //We must ensure only one thread updates the active room list at a time
    private static final Object refreshActiveContestRoomListsMutex = new Object();

    public static void refreshActiveContestRoomLists() {
        synchronized (refreshActiveContestRoomListsMutex) {
            s_activeModeratedChatIDs = new ArrayList();

            // Create the contest room list.
            Round[] activeContests = CoreServices.getAllActiveRounds();
            if (activeContests == null) {
                trace.error("activeContests==null");
                return;
            }

            RoundData adminRoundData[] = new RoundData[activeContests.length];
            List activeRounds = new LinkedList();
            List onlyRegisteredUserRounds = new LinkedList();
            for (int i = 0; i < activeContests.length; i++) {
                Round nextContest = activeContests[i];
                RoundData data = new RoundData(nextContest.getRoundID(), nextContest.getContestName(), nextContest.getRoundName(),
                        nextContest.getRoundTypeId(), nextContest.getPhaseData(), nextContest.getActiveMenu(), nextContest.getRoundCustomProperties());
                if (!nextContest.getRoundProperties().isVisibleOnlyForRegisteredUsers()) {
                    activeRounds.add(data);
                } else {
                    onlyRegisteredUserRounds.add(data);
                }
                adminRoundData[i] = (RoundData) data.clone();
                adminRoundData[i].setEnabled(true);
            }
            RoundData roundData[] = (RoundData[]) activeRounds.toArray(new RoundData[activeRounds.size()]);
            s_activeContestRequiringRegisteredUser = (RoundData[]) onlyRegisteredUserRounds.toArray(new RoundData[onlyRegisteredUserRounds.size()]);
            s_activeContests = new CreateRoundListResponse(CreateRoundListResponse.ACTIVE, roundData);
            s_activeAdminContests = new CreateRoundListResponse(CreateRoundListResponse.ACTIVE, adminRoundData);


            /* SYHAAS 2002-05-15 modified to create active moderated chats */
            ArrayList chatNames = new ArrayList();
            ArrayList chatStatus = new ArrayList();
            ArrayList activeChats = CoreServices.getActiveModeratedChatSessions();
            if (activeChats == null) {
                trace.error("activeChats==null");
                return;
            }
            for (int i = 0; i < activeChats.size(); i++) {
                int chatRoundId = ((Integer) activeChats.get(i)).intValue();
                ContestRound chat = (ContestRound) CoreServices.getContestRound(chatRoundId);
                chatNames.add(chat.getContestName());
                if (chat.getActiveMenu())
                    chatStatus.add("A");
                else
                    chatStatus.add("F");
                Iterator rooms = chat.getAllRoomIDs();
                while (rooms.hasNext()) {
                    Room r = CoreServices.getRoom(((Integer) (rooms.next())).intValue(), false);
                    s_activeModeratedChatIDs.add(new Integer(r.getRoomID()));
                }
                //s_activeModeratedChatIDs.add(new Integer(chat.getContestID()));
            }
            s_activeModeratedChats = new CreateMenuResponse(ContestConstants.ACTIVE_CHAT_MENU, chatNames, chatStatus, s_activeModeratedChatIDs);
        }
    }

    static void createLobbyRoomLists() {
        // Create the contest room list.
        //ContestRound lobbies = CoreServices.getContestRound(CoreServices.LOBBY_ROUND_ID);
        Round lobbies = CoreServices.getLobbiesContestRound(CoreServices.LOBBY_ROUND_ID);
        ArrayList lobbyNames = new ArrayList();
        ArrayList lobbyStatus = new ArrayList();
        ArrayList lobbyIDs = new ArrayList();
        for (Iterator s_lobbyIDs = lobbies.getAllRoomIDs(); s_lobbyIDs.hasNext();) {
            int id = ((Integer) (s_lobbyIDs.next())).intValue();
            lobbyIDs.add(new Integer(id));
            Room r = CoreServices.getRoom(id, false);
            String lobbyName = r.getName();
            int cap = r.getCapacity();
            int occ = r.getOccupancy();
            lobbyNames.add(lobbyName);
            if (cap > 0 && cap == occ)
                lobbyStatus.add("F");
            else
                lobbyStatus.add("A");
        }
        s_lobbyContests = new CreateMenuResponse(ContestConstants.LOBBY_MENU, lobbyNames, lobbyStatus, lobbyIDs);
    }

    static void createCategoriesList() {
        trace.debug("Before core services load categories.");
        CategoryData[] roundCategories = CoreServices.loadCategories();
        trace.debug("Before CreateCategoryListResponse.");
        s_categories = new CreateCategoryListResponse(roundCategories);
    }

    static void createPracticeRoomLists() {
        // Create the practice room list
        int limit = Processor.getPracticeRoundLimit();
        Round practiceRounds[] = CoreServices.loadPracticeRounds(limit);
        RoundData practiceRoundsData[] = new RoundData[practiceRounds.length];

        for (int i = 0; i < practiceRounds.length; i++) {
            Round practiceRound = practiceRounds[i];
            int practiceRoundID = practiceRound.getRoundID();
            if (practiceRound != null) {
                practiceRoundsData[i] = new RoundData(
                        practiceRoundID,
                        practiceRound.getContestName(),
                        practiceRound.getRoundName(),
                        practiceRound.getRoundTypeId(),
                        practiceRound.getCategory(),
                        new PhaseData(practiceRoundID, ContestConstants.CODING_PHASE, 0, 0),
                        practiceRound.getRoundCustomProperties(),
                        practiceRound.getPracticeDivisionID(),
                        ((Integer)practiceRound.getAllRoomIDsList().get(0)).intValue()
                        );
            } else {
                throw new IllegalStateException("CoreServices returned null ContestRound for practice id: " + practiceRoundID);
            }
        }
        s_practiceContests = new CreateRoundListResponse(CreateRoundListResponse.PRACTICE, practiceRoundsData);
    }

    public static CreateRoundListResponse getPracticeContests() {
    	return s_practiceContests;
    }

    public static String hashForUser(Integer connectionID, User user) throws Exception {

        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] plain = (ServerContestConstants.HASH_SECRET+
                //connectionID.intValue()+user.getName()
                //assumes lastLogin is updated only during regular logins, not reconnects, must verify. And that lastLogin is correctly updated before the hash key is sent to the client
                user.getName() + user.getLastLogin()
                ).getBytes();
        byte[] raw = md.digest(plain);
        StringBuilder hex = new StringBuilder();
        for(int i=0; i<raw.length; i++)
            hex.append(Integer.toHexString(raw[i]&0xff));
        return hex.toString();
    }

    static void reconnectSuccess(Integer connectionID, int requestID, User user, List pendingMessages) {
        try {

            AdminBroadcastManager.getInstance().sendRecentBroadcasts(connectionID, user.getID());

            MessageBuilder msgBuilder = new MessageBuilder(3);
            msgBuilder.add(new ReconnectResponse(true, RequestProcessor.sealObject(connectionID, hashForUser(connectionID, user)), connectionID.longValue()));

            msgBuilder.add(new SynchTimeResponse());

            //This comes from login.  For now this is duplicate code.
            //Eventually both paths should be unified, but it'll take some work
            /**/
            CreateRoundListResponse roundListResponse = getActiveRoundsResponse(user);
            RoundData[] roundData = roundListResponse.getRoundData();

            // add leaderboard
            for (int i = 0; i < roundData.length; i++) {
                LeaderBoard leaderboard = CoreServices.getLeaderBoard(roundData[i].getRoundID(), false);
                msgBuilder.add(createLeaderBoardResponse(leaderboard));
            }

            ///room move code
            createRoomReconnectResponses(user, user.getRoomID(), msgBuilder);

            ArrayList al = new ArrayList();
            for (Iterator i = user.getWatchedDivSummaryRooms(); i.hasNext();) {
                Integer roomID = ((Integer) i.next());
                if(!al.contains(roomID)) {
                    createRoomReconnectResponses(user, roomID.intValue(), msgBuilder);
                    al.add(roomID);
                }
            }

            for (Iterator i = user.getWatchedRooms(); i.hasNext();) {
                Integer roomID = ((Integer) i.next());
                if(!al.contains(roomID)) {
                    createRoomReconnectResponses(user, roomID.intValue(), msgBuilder);
                    al.add(roomID);
                }
            }
            // Add keep alive initialization data required by the client
            msgBuilder.add(getKeepAliveInitializationData());

            // Add all accumulated messages for the client
            if (pendingMessages != null) {
                for (Iterator iter = pendingMessages.iterator(); iter.hasNext();) {
                    msgBuilder.add(iter.next());
                }
            }
            process(connectionID, msgBuilder.buildResponseToSyncRequest(requestID));
        } catch(Exception e) {
            trace.error("Error processing reconnect request", e);
            reconnectFailure(connectionID, requestID, "Reconnect Failed");
        }
    }

    static List getPendingResponses(Integer connectionID) {
        synchronized (disconnectedClientMessages) {
            if (disconnectedClientMessages.containsKey(connectionID)) {
                return new ArrayList((List)disconnectedClientMessages.get(connectionID));
            } else {
                return null;
            }
        }
    }

    static void lostConnection(Integer connectionID) {
        synchronized (disconnectedClientMessages) {
            if (!disconnectedClientMessages.containsKey(connectionID)) {
                disconnectedClientMessages.put(connectionID, new ArrayList());
            }
        }
    }

    private static void createRoomReconnectResponses(User user, int rmID,  MessageBuilder msgBuilder) {
        Room destination = CoreServices.getRoom(rmID, false);
        if (destination == null) {
            trace.error("move destination room was null for ID = " + user.getRoomID());
            return;
        }
        int roomID = destination.getRoomID();
        int roomType = destination.getType();

        BaseCodingRoom contestRoom = null;
        Round contestRound = null;
        int activePhase = ContestConstants.INACTIVE_PHASE;
        if (ContestConstants.isPracticeRoomType(destination.getType())) {       // Always use coding phase for a practice room.
            activePhase = ContestConstants.CODING_PHASE;
            contestRoom = (BaseCodingRoom) destination;
            contestRound = CoreServices.getContestRound(contestRoom.getRoundID());
        } else if (roomType == ContestConstants.LOBBY_ROOM) {
            // TODO what is phase for lobbies now?  ignore
        } else if (destination instanceof BaseCodingRoom) {
            contestRoom = (BaseCodingRoom) destination;
            contestRound = CoreServices.getContestRound(contestRoom.getRoundID());
            activePhase = contestRound.getPhase();
        }

        //        String status = "";
        //        if (ServerContestConstants.isLobby(roomID)) {
        //            status = CoreServices.getLobbyStatus();
        //        } else if (ContestConstants.isPracticeRoomType(destination.getType())) {
        //            status = ContestConstants.PRACTICE_STATUS;
        //        } else if (contestRound != null && contestRound.isModeratedChat()) {
        //            status = ("Moderated Chat");
        //        } else if (contestRound != null) {
        //            status = (contestRound.getContestName() + ", " + contestRound.getRoundName() + "      " + destination.getName());
        //        } else {
        //            trace.warn("Couldn't determine status for room #" + roomID);s
        //        }

        if (trace.isDebugEnabled()) trace.debug("roomType: " + roomType);
        if (roomType == ContestConstants.TEAM_CODER_ROOM || roomType == ContestConstants.TEAM_PRACTICE_CODER_ROOM || roomType == ContestConstants.TEAM_ADMIN_ROOM) {
            //send out the create problems for a team contest because the components may have changed since
            //the original, or the original may have been all components.
            if (trace.isDebugEnabled()) {
                trace.debug("contestRoom: " + contestRoom);
                trace.debug("contestRoom.isUserAssigned(): " + (contestRoom.isUserAssigned(user.getID())));
            }
            if (contestRoom instanceof TeamContestRoom && contestRoom.isUserAssigned(user.getID())) {
                try {
                    TeamCoder teamCoder = (TeamCoder) contestRoom.getCoder(user.getID());
                    //allResponses.add(new CreateProblemsResponse(
                    //        contestRound.getProblemLabels(contestRoom.getDivisionID()),
                    //        teamCoder.getComponentLabels(user.getID()),
                    //        contestRound.getRoundID(),
                    //        contestRoom.getDivisionID()));
                    msgBuilder.add(new ComponentAssignmentDataResponse(teamCoder.getComponentAssignmentData()));
                } catch (Exception e) {
                    e.printStackTrace();
                    trace.error("Error making CreateProblemsResponse for " + user.getID() + ", probably didn't get a " +
                            "TeamCoder from TeamContestRoom.getCoder()");
                    throw new IllegalStateException("Couldn't get CreateProblemsResponse.");
                }
            }
        }


        switch (roomType) {
        case ContestConstants.CODER_ROOM:
        case ContestConstants.TEAM_CODER_ROOM:
        case ContestConstants.SPECTATOR_ROOM:
            if (destination instanceof BaseCodingRoom) {
                trace.debug("Creating challenge table and assigned users for contestRoom");
                contestRoom = (BaseCodingRoom) destination;
                // Add ChallengeTable
                msgBuilder.add(createChallengeTable(contestRoom, contestRoom.getRoomID(), contestRoom.getType()));
                Integer connectionID = RequestProcessor.getConnectionID(user.getID());
                if (ListenerMain.getSocketConnector().isConnected(connectionID)){
                    trace.debug("sending challenges list to connection: "+connectionID);
                    ChallengeData[] cd = getRoomChallengeData(contestRoom);
                    ChallengesListResponse clr =new ChallengesListResponse(contestRoom.getType(),contestRoom.getRoomID(), cd);
                    msgBuilder.add(clr);
                }else{
                    trace.debug("NOT sending challenges list to connection: "+connectionID);
                }
            }
            break;
        case ContestConstants.ADMIN_ROOM:
        case ContestConstants.TEAM_ADMIN_ROOM:
        case ContestConstants.TEAM_PRACTICE_CODER_ROOM:
        case ContestConstants.PRACTICE_CODER_ROOM:
        case ContestConstants.PRACTICE_SPECTATOR_ROOM:
            if (destination instanceof BaseCodingRoom) {
                trace.debug("Creating challenge table and assigned users for contestRoom");
                contestRoom = (BaseCodingRoom) destination;
                // Add problem list for room  -- this is really only needed for the practice rooms
                if (roomType != ContestConstants.TEAM_PRACTICE_CODER_ROOM && roomType != ContestConstants.TEAM_ADMIN_ROOM) {
                    //allResponses.add(createProblems(contestRoom.getRoundID(), contestRoom.getDivisionID()));
                }
                // Add ChallengeTable
                msgBuilder.add(createChallengeTable(contestRoom, contestRoom.getRoomID(), contestRoom.getType()));
            } else {
                throw new IllegalStateException("Expected a contest room.  Got: " + destination);
            }
            break;
        case ContestConstants.INVALID_ROOM:
        case ContestConstants.WATCH_ROOM:
        case ContestConstants.LOGIN_ROOM:
        case ContestConstants.LOBBY_ROOM:
        case ContestConstants.MODERATED_CHAT_ROOM:
            break;
        default:
            trace.warn("Unknown room type (" + roomType + ").");
            break;
        }

        if (contestRoom != null && contestRound != null && !ContestConstants.isPracticeRoomType(destination.getType())
                && contestRound.inCoding()) {
            long phaseStartTime = contestRound.getPhaseStart();
            long phaseEndTime = contestRound.getPhaseEnd();
            int coderId = user.getID();
            if (contestRound.getRoundProperties().usesPerUserCodingTime() && contestRoom.isUserAssigned(coderId)) {
                Coder coder = contestRoom.getCoder(coderId);
                if (coder.hasOpenedComponents()) {

                    phaseStartTime = coder.getEarliestComponentOpenTime();
                    phaseEndTime = phaseStartTime + contestRound.getRoundProperties().getPerUserCodingTime().longValue();
                }

            }

            PhaseData phaseData = new PhaseData(contestRoom.getRoundID(), activePhase, phaseStartTime, phaseEndTime);
            PhaseDataResponse phaseDataResponse = new PhaseDataResponse(phaseData);
            msgBuilder.add(phaseDataResponse);
        }

        if (activePhase == ContestConstants.SYSTEM_TESTING_PHASE) {
            msgBuilder.add(getSystestProgressResponse(contestRoom.getContestID(), contestRoom.getRoundID()));
        }

        // try sending it here - this makes the client send a EnterRequest
        /*if (roomType == ContestConstants.CONTEST_ROOM || roomType == ContestConstants.CODER_ROOM || roomType == ContestConstants.ADMIN_ROOM || roomType == ContestConstants.WATCH_ROOM) {
          long roundID = ((ContestRoom) destination).getRoundID();
          allResponses.add(new RoomInfoResponse(roundID, roomType, roomID, destination.getName(), status));
          } else {
          allResponses.add(new RoomInfoResponse(roomType, roomID, destination.getName(), status));
          }*/

        // Create the user list response.
        msgBuilder.add(createRoomUserList(destination, roomID));
    }

    static void reconnectFailure(Integer connectionID, int requestID, String message) {
        MessageBuilder msgBuilder = new MessageBuilder(2);
        ReconnectResponse response = new ReconnectResponse(false);
        msgBuilder.add(response);
        msgBuilder.add(simpleMessage(message, g_errorResources.getString("ERROR")));

        process(connectionID, msgBuilder.buildResponseToSyncRequest(requestID));
    }

    static void synchTime(Integer connectionID, int requestID) {
        MessageBuilder msgBuilder = new MessageBuilder(1);
        SynchTimeResponse response = new SynchTimeResponse();
        msgBuilder.add(response);
        process(connectionID, msgBuilder.buildResponseToSyncRequest(requestID));
    }

    /**
     * Sends Repsonses to the user indicating that the login was successful.
     */
    static void loginSuccess(Integer connectionID, int requestID, User user, boolean autoRegisterForActiveRound) {
        trace.debug("loginSuccess");
        try {
            if (user.isSpectator()) {
                specAppProcessor.spectatorLoginSuccess(connectionID, user);
                return;
            } else if (user.isForwarder()) {
                process(connectionID, new SpectatorLoginResult(Integer.toString(user.getID()), RequestProcessor.sealObject(connectionID, "No pass"), true, ""));
                return;
            }

            MessageBuilder msgBuilder = new MessageBuilder();
            LoginResponse loginResponse = new LoginResponse(true, connectionID.longValue(), RequestProcessor.sealObject(connectionID, hashForUser(connectionID, user)));
            msgBuilder.add(loginResponse);

            java.sql.Timestamp lastLogin = user.getLastLogin();
            long lastLoginMS = 0;
            if (lastLogin != null)
                lastLoginMS = lastLogin.getTime();
            // Create user info response
            WeakestLinkRound activeWeakestLinkRound = CoreServices.getActiveWeakestLinkRound();
            String handle = user.getName();
            boolean isWeakestLinkParticipant;
            if (activeWeakestLinkRound == null) {
                isWeakestLinkParticipant = false;
            } else {
                isWeakestLinkParticipant = activeWeakestLinkRound.isWeakestLinkParticipant(handle);
            }
            //Get the user avatar path from DB.
            String pathImage = CoreServices.getMemberPhotoPath(user.getID());
            
            UserInfo ui = new UserInfo(
                    handle,
                    user.isGuest(),
                    user.isLevelTwoAdmin(),
                    user.isCaptain(),
                    lastLoginMS,
                    user.isCompetitionUser() ? user.getRating(Rating.ALGO).getNumRatings() : user.getRating(Rating.HS).getNumRatings(),
                            user.isCompetitionUser() ? user.getRating(Rating.ALGO).getRating() : user.getRating(Rating.HS).getRating(),
                                    isWeakestLinkParticipant,
                                    user.getTeamName(),
                                    pathImage
                    );
            UserInfoResponse userInfoResponse = new UserInfoResponse(ui);

            // Send update preferences
            HashMap defaults = new HashMap();
            defaults.put(new Integer(ContestConstants.LANGUAGE), new Integer(user.getLanguage()));

            ui.setPreferences(defaults);

            msgBuilder.add(userInfoResponse);

            // Send Contest menu
            CreateRoundListResponse roundListResponse = getActiveRoundsResponse(user);
            msgBuilder.add(roundListResponse);
            RoundData[] roundData = roundListResponse.getRoundData();

            // add rooms, leaderboard, problems
            for (int i = 0; i < roundData.length; i++) {
                addRoundDefinitionResponses(msgBuilder, roundData[i].getRoundID());
            }

            msgBuilder.add(s_practiceContests);
            msgBuilder.add(s_categories);
            // Send Active Moderated Chat Sessions
            msgBuilder.add(s_activeModeratedChats);//SYHAAS 2002-05-15 added
            //GT, I think there is no need for this ...
            //createLobbyRoomLists();
            msgBuilder.add(s_lobbyContests);

            msgBuilder.add(new SynchTimeResponse());

            //Add keep alive initialization data required by the client
            msgBuilder.add(getKeepAliveInitializationData());

            //TODO: lookup important messages for this user and pass them along

            List messages = CoreServices.getUserImportantMessages(user.getID());

            for(int i = 0; i < messages.size(); i++ ) {
                ImportantMessageData m = (ImportantMessageData)messages.get(i);

                ImportantMessageResponse important = new ImportantMessageResponse(m.getId(), m.getMessage());
                msgBuilder.add(important);
            }

            process(connectionID, msgBuilder.buildResponseToSyncRequest(requestID));

            if (trace.isDebugEnabled()) trace.debug("autoRegisterForActiveRound "+autoRegisterForActiveRound);
            if ( autoRegisterForActiveRound ) {
                registerForActiveLongRound(connectionID, user);
            }

        } catch (Exception e) {
            // Send an error if an exception occurs.
            trace.error("Error processing login response", e);
            loginFailed(connectionID, requestID, g_errorResources.getString("SERVER_ERROR") + e.getMessage());
        }
    }


    /**
     * @return a KeepAliveInitializationDataResponse with all keep-alive data required by the client
     */
    private static KeepAliveInitializationDataResponse getKeepAliveInitializationData() {
        KeepAliveInitializationDataResponse response = new KeepAliveInitializationDataResponse();
        response.setTimeout(KeepAliveProperties.getTimeout());
        response.setHttpTimeout(KeepAliveProperties.getHttpTimeout());
        return response;
    }

    static void registerForActiveLongRound(Integer connectionID, User user) {
        //Admins Should never be registered
        if (user.isLevelOneAdmin() || user.isLevelTwoAdmin())
            return;

        Round[] activeLongRounds = CoreServices.getActiveRegLongRounds();
        if (activeLongRounds.length > 0) {
            //There is a bug in this logic that needs to get fixed
            //We currently can not handle long round active contests
            //where people are only invited to a subset of the active rounds.
            //EX.  I have 2 active rounds going.  Coder A is invited to Round 1
            //coder B is invited to Round 2.  The logic checks to see if they
            //are invited to any round and then based on the round robin logic
            //registers them to the next round.  The next round can be one
            //where they are not invited to though!!!!
            String activeLongContestRoundName = "";
            boolean isRegistered = false;
            boolean isInvited = false;
            int min = Integer.MAX_VALUE;
            int selRound = 0;

            for (int i = 0; i < activeLongRounds.length; i++) {
                Round activeLongRound = activeLongRounds[i];
                Registration registration = CoreServices.getRegistration(activeLongRound.getRoundID());

                if (registration.isInvitationOnly() && registration.isInvited(user.getID())) {
                    isInvited = true;
                }
                if (registration.isRegistered(user.getID())) {
                    isRegistered = true;
                    activeLongContestRoundName = activeLongRound.getDisplayName();
                    String message = "You are registered to " + activeLongContestRoundName;
                    process(connectionID, simpleMessage(message, "Event Notification"));
                    break;
                }

                int sz = registration.getUserNames().size();
                if(sz < min &&  ((registration.isInvitationOnly() && registration.isInvited(user.getID())) || !registration.isInvitationOnly()))
                {
                    isInvited = true;
                    min = sz;
                    selRound = i;
                }
            }

            if (isInvited && !isRegistered) {
                //changed to do a true round robin cycle
                Round activeLongRoundRegister = activeLongRounds[selRound];
                int roundId = activeLongRoundRegister.getRoundID();
                activeLongContestRoundName = activeLongRoundRegister.getDisplayName();
                CoreServices.registerCoderByHandle(user.getName(), roundId, true);
                String message = "You are registered to " + activeLongContestRoundName;
                process(connectionID, simpleMessage(message, "Event Notification"));
            }
        }
    }

    static void updateCurrentUser(Integer connectionID, User user) {
        WeakestLinkRound activeWeakestLinkRound = CoreServices.getActiveWeakestLinkRound();
        boolean isWeakestLinkParticipant;
        if (activeWeakestLinkRound == null) {
            isWeakestLinkParticipant = false;
        } else {
            isWeakestLinkParticipant = activeWeakestLinkRound.isWeakestLinkParticipant(user.getName());
        }
        //Get the user avatar path from DB.
        String pathImage = CoreServices.getMemberPhotoPath(user.getID());
        
        UserInfo ui = new UserInfo(
                user.getName(),
                user.isGuest(),
                user.isLevelTwoAdmin(),
                user.isCaptain(),
                user.getLastLogin() != null ? user.getLastLogin().getTime() : 0,
                        user.isCompetitionUser() ? user.getRating(Rating.ALGO).getNumRatings() : user.getRating(Rating.HS).getNumRatings(),
                                user.isCompetitionUser() ? user.getRating(Rating.ALGO).getRating() : user.getRating(Rating.HS).getRating(),
                                        isWeakestLinkParticipant,
                                        user.getTeamName(),
                                        pathImage
                );
        UserInfoResponse userInfoResponse = new UserInfoResponse(ui);
        process(connectionID, userInfoResponse);
    }

    /**
     * Sends the logout Responses.
     */
    static void logout(Integer connectionID, int requestID) {
        trace.debug("logout");
        //int roomIndex = -1;
        //int roomType = -1;
        UnsynchronizeResponse unsynchronizeResponse = new UnsynchronizeResponse(requestID);
        process(connectionID, unsynchronizeResponse);
    }

    static void getMessages(Integer connectionID, ImportantMessageData[] msg) {
        GetImportantMessagesResponse resp = new GetImportantMessagesResponse();

        for(int i =0; i < msg.length;i++) {
            resp.addItem(msg[i].getMessage(), msg[i].getTime());
        }

        process(connectionID, resp);
    }

    static void move(Integer connectionID, int requestID, int userID) {
        if (trace.isDebugEnabled()) trace.debug("move - userID=" + userID + " requestID=" + requestID);
        MessageBuilder msgBuilder = new MessageBuilder();
        User user = CoreServices.getUser(userID, false);
        Room destination = CoreServices.getRoom(user.getRoomID(), false);
        if (destination == null) {
            trace.error("move destination room was null for ID = " + user.getRoomID());
            return;
        }
        int roomID = destination.getRoomID();
        int roomType = destination.getType();

        BaseCodingRoom contestRoom = null;
        Round contestRound = null;
        int activePhase = ContestConstants.INACTIVE_PHASE;
        if (ContestConstants.isPracticeRoomType(destination.getType())) {       // Always use coding phase for a practice room.
            activePhase = ContestConstants.CODING_PHASE;
            contestRoom = (BaseCodingRoom) destination;
            contestRound = CoreServices.getContestRound(contestRoom.getRoundID());
        } else if (roomType == ContestConstants.LOBBY_ROOM) {
            // TODO what is phase for lobbies now?  ignore
        } else if (destination instanceof BaseCodingRoom) {
            contestRoom = (BaseCodingRoom) destination;
            contestRound = CoreServices.getContestRound(contestRoom.getRoundID());
            activePhase = contestRound.getPhase();
        }

        String status = "";
        if (ServerContestConstants.isLobby(roomID)) {
            status = CoreServices.getLobbyStatus();
        } else if (ContestConstants.isPracticeRoomType(destination.getType())) {
            status = ContestConstants.PRACTICE_STATUS;
        } else if (contestRound != null && contestRound.isModeratedChat()) {
            status = ("Moderated Chat");
        } else if (contestRound != null) {
            status = (contestRound.getDisplayName() + "      " + destination.getName());
        } else {
            trace.warn("Couldn't determine status for room #" + roomID);
        }

        if (trace.isDebugEnabled()) trace.debug("roomType: " + roomType);
        if (roomType == ContestConstants.TEAM_CODER_ROOM || roomType == ContestConstants.TEAM_PRACTICE_CODER_ROOM || roomType == ContestConstants.TEAM_ADMIN_ROOM) {
            //send out the create problems for a team contest because the components may have changed since
            //the original, or the original may have been all components.
            if (trace.isDebugEnabled()) {
                trace.debug("contestRoom: " + contestRoom);
                trace.debug("contestRoom.isUserAssigned(): " + (contestRoom.isUserAssigned(userID)));
            }
            if (contestRoom instanceof TeamContestRoom && contestRoom.isUserAssigned(userID)) {
                try {
                    TeamCoder teamCoder = (TeamCoder) contestRoom.getCoder(userID);
                    msgBuilder.add(new CreateProblemsResponse(
                            contestRound.getProblemLabels(contestRoom.getDivisionID()),
                            teamCoder.getComponentLabels(userID),
                            contestRound.getRoundID(),
                            contestRoom.getDivisionID()));
                    msgBuilder.add(new ComponentAssignmentDataResponse(teamCoder.getComponentAssignmentData()));
                } catch (Exception e) {
                    e.printStackTrace();
                    trace.error("Error making CreateProblemsResponse for " + userID + ", probably didn't get a " +
                            "TeamCoder from TeamContestRoom.getCoder()");
                    throw new IllegalStateException("Couldn't get CreateProblemsResponse.");
                }
            }
        }


        switch (roomType) {
        case ContestConstants.CODER_ROOM:
        case ContestConstants.TEAM_CODER_ROOM:
        case ContestConstants.SPECTATOR_ROOM:
            if (destination instanceof BaseCodingRoom) {
                trace.debug("Creating challenge table and assigned users for contestRoom");
                contestRoom = (BaseCodingRoom) destination;
                // Add ChallengeTable
                msgBuilder.add(createChallengeTable(contestRoom, contestRoom.getRoomID(), contestRoom.getType()));
                if (ListenerMain.getSocketConnector().isConnected(connectionID)){
                    trace.debug("sending challenges list to connection: "+connectionID);
                    ChallengeData[] cd = getRoomChallengeData(contestRoom);
                    ChallengesListResponse clr =new ChallengesListResponse(contestRoom.getType(),contestRoom.getRoomID(),cd);
                    msgBuilder.add(clr);
                }else{
                    trace.debug("NOT sending challenges list to connection: "+connectionID);
                }
            }
            break;
        case ContestConstants.ADMIN_ROOM:
        case ContestConstants.TEAM_ADMIN_ROOM:
        case ContestConstants.TEAM_PRACTICE_CODER_ROOM:
        case ContestConstants.PRACTICE_CODER_ROOM:
        case ContestConstants.PRACTICE_SPECTATOR_ROOM:
            if (destination instanceof BaseCodingRoom) {
                trace.debug("Creating challenge table and assigned users for contestRoom");
                contestRoom = (BaseCodingRoom) destination;
                // Add problem list for room  -- this is really only needed for the practice rooms
                if (roomType != ContestConstants.TEAM_PRACTICE_CODER_ROOM && roomType != ContestConstants.TEAM_ADMIN_ROOM) {
                    msgBuilder.add(createProblems(contestRoom.getRoundID(), contestRoom.getDivisionID()));
                }
                // Add ChallengeTable
                msgBuilder.add(createChallengeTable(contestRoom, contestRoom.getRoomID(), contestRoom.getType()));
            } else {
                throw new IllegalStateException("Expected a contest room.  Got: " + destination);
            }
            break;
        case ContestConstants.INVALID_ROOM:
        case ContestConstants.WATCH_ROOM:
        case ContestConstants.LOGIN_ROOM:
        case ContestConstants.LOBBY_ROOM:
        case ContestConstants.MODERATED_CHAT_ROOM:
            /* Do Nothing */
            break;
        default:
            trace.warn("Unknown room type (" + roomType + ").");
            break;
        }

        if (contestRoom != null && contestRound != null && !ContestConstants.isPracticeRoomType(destination.getType()) &&
                contestRound.inCoding()) {
            long phaseStartTime = contestRound.getPhaseStart();
            long phaseEndTime = contestRound.getPhaseEnd();
            int coderId = user.getID();
            if (contestRound.getRoundProperties().usesPerUserCodingTime() && contestRoom.isUserAssigned(coderId)) {
                Coder coder = contestRoom.getCoder(coderId);
                if (coder.hasOpenedComponents()) {
                    phaseStartTime = coder.getEarliestComponentOpenTime();
                    phaseEndTime = phaseStartTime + contestRound.getRoundProperties().getPerUserCodingTime().longValue();
                }

            }

            PhaseData phaseData = new PhaseData(contestRoom.getRoundID(), activePhase, phaseStartTime, phaseEndTime);
            PhaseDataResponse phaseDataResponse = new PhaseDataResponse(phaseData);
            msgBuilder.add(phaseDataResponse);
        }

        if (activePhase == ContestConstants.SYSTEM_TESTING_PHASE) {
            msgBuilder.add(getSystestProgressResponse(contestRoom.getContestID(), contestRoom.getRoundID()));
        }

        // try sending it here
        if (roomType == ContestConstants.CONTEST_ROOM || roomType == ContestConstants.CODER_ROOM || roomType == ContestConstants.ADMIN_ROOM || roomType == ContestConstants.WATCH_ROOM) {
            long roundID = ((BaseCodingRoom) destination).getRoundID();
            msgBuilder.add(new RoomInfoResponse(roundID, roomType, roomID, destination.getName(), status));
        } else {
            msgBuilder.add(new RoomInfoResponse(roomType, roomID, destination.getName(), status));
        }

        // Create the user list response.
        msgBuilder.add(createRoomUserList(destination, roomID));

        msgBuilder.add(new SynchTimeResponse());

        process(connectionID, msgBuilder.buildResponseToSyncRequest(requestID));
    }

    /**
     * Helper method to send response only to the users connevted via web arena.
     * @param coding room - room to send the responses to
     * @param response - the response to send.
     */
    public static void sendResponseToWebArenaUsers(BaseCodingRoom codingRoom, BaseResponse response){
        for (Iterator i = codingRoom.getAllCoders(); i.hasNext();) {
            Coder coder = (Coder) i.next();
            Integer connectionId = RequestProcessor.getConnectionID(coder.getID());
            if (connectionId==null){
                trace.debug("NOT sending response to a non connected user: "+coder.getID());
                continue;
            }
            int userID = RequestProcessor.getUserID(connectionId);
            User usr = CoreServices.getUser(userID);
            if (usr.getRoomID()!=codingRoom.getRoomID()){
                trace.debug("NOT sending response to a user in a different room: "+coder.getID());
                continue;   
            }
            
            if (ListenerMain.getSocketConnector().isConnected(connectionId)){
                trace.debug("sending response update to coder: "+coder.getID());
                sendMessageToConnectionId(connectionId, response);
            }else{
                trace.debug("NOT sending response to coder not in web arena: "+coder.getID());
            }
        }
    }

    /**
     * Helper method to get the info about previous challenges in a room.
     * @param contestRoom - the room the get the challenge info data for
     * @returns - the array of challengeData objects
    */
    public static ChallengeData[] getRoomChallengeData(BaseCodingRoom contestRoom){
        trace.debug("enter getRoomChallengeData");
        List data = new ArrayList();
        for (Iterator i = contestRoom.getAllCoders(); i.hasNext();) {
            Coder coder = (Coder) i.next();
            trace.debug("handling coder: "+coder.getName());
            CoderHistory coderHistory = coder.getHistory();
            ArrayList challenges = (ArrayList) coderHistory.getChallenges();
            String handle = coder.getName();
            int rating = coder.getRating();
            trace.debug("coder has: "+challenges.size()+" challenges in his history");
            for (int j=0;j<challenges.size();j++){
                CoderHistory.ChallengeData cd = (CoderHistory.ChallengeData) challenges.get(j);
                if (!cd.isChallenger()){
                    trace.debug("coder is not challenger"); 
                    continue;
                }
                boolean succedded =cd.getPoints()>0;
                Date date = cd.getDate();
                int componentID = cd.getComponentID();
                int language = coder.getComponent(componentID).getSubmittedLanguage();
                int points = cd.getPoints()/100;
                int defenderID = cd.getOtherUserID();
                Coder defender = contestRoom.getCoder(defenderID);
                String defenderHandle = defender.getName();
                int defenderRating = defender.getRating();
                ChallengeData d = new ChallengeData();
                d.setChallengerHandle(handle);
                d.setDefenderHandle(defenderHandle);
                d.setChallengerRating(rating);
                d.setDefenderRating(defenderRating);
                d.setDate(date);
                d.setSuccess(succedded);
                d.setLanguage(language+"");
                d.setPoints(points);
                d.setComponentID(componentID);
                trace.debug("adding challenge to challenge list");
                data.add(d);
            }
        }
        trace.debug("exit getRoomChallengeData. Return "+data.size()+" challenges");
        return (ChallengeData[])data.toArray(new ChallengeData[data.size()]);
    }

    static void error(Integer connectionID, String message) {
        process(connectionID, simpleMessage(message, g_errorResources.getString("ERROR")));
    }

    static void forceLogout(Integer connectionID, String message)
    {
        process(connectionID, new ForcedLogoutResponse(g_errorResources.getString("ERROR"), message));
    }

    static void coderInfo(Integer connectionID, String message) {
        String title = "Coder Info";
        process(connectionID, simpleBigMessage(message, title));
    }

    static void loggedInUsers(Integer connectionID, int userID) {
        trace.debug("loggedInUsers");
        User user = CoreServices.getUser(userID, false);
        //        UserState state = UserState.getUserState(userID);
        int roomID = user.getRoomID();
        int roomType = user.getRoomType();
        ArrayList[] userData = CoreServices.getLoggedInUserData();
        CreateUserListResponse response = createUserList(userData[0], userData[1], ContestConstants.ACTIVE_USERS, roomID, roomType);
        process(connectionID, response);
    }

    static void visitedPractice(Integer connectionID, int userID) {
        int[] roundIDs = CoreServices.loadVisitedPracticeRounds(userID);
        if (trace.isDebugEnabled()) trace.debug("visitedPractice() - #" + roundIDs.length);
        CreateVisitedPracticeResponse response = new CreateVisitedPracticeResponse(roundIDs);
        process(connectionID, response);
    }

    static void search(Integer connectionID, String userName, String roomName, int targetRoomType, int targetRoomID) {
        //User user = CoreServices.getUser( userID, false );
        //        UserState state = UserState.getUserState(userID);
        /*
          int roomIndex = state.roomIDToIndex( user.getRoomID() );
          int roomType = user.getRoomType();
         */

        //        int targetRoomIndex = state.roomIDToIndex(targetRoomID);
        ArrayList buttons = new ArrayList();
        buttons.add("Yes");
        buttons.add("No");
        String title = "Search";
        String msg = userName + " is in " + roomName + ".\nWould you like to go there now?";
        ArrayList roomInfo = new ArrayList();
        roomInfo.add(new Integer(targetRoomType));
        roomInfo.add(new Integer(targetRoomID));
        PopUpGenericResponse response = new PopUpGenericResponse(title, msg, ContestConstants.ROOM_MOVE, ContestConstants.LABEL, buttons, roomInfo);
        process(connectionID, response);
    }

    static void clearPracticer(Integer connectionID) {
        String message = "Your data has been successfully cleared.";
        String title = "Data Cleared.";
        // TODO currentState.getCoderProblems().clear();
        process(connectionID, simpleMessage(message, title));
    }

    //added 2-20 rfairfax
    static void clearPracticeProblem(Integer connectionID) {
        String message = "The problem(s) has been successfully cleared.";
        String title = "Problem(s) Cleared.";
        // TODO currentState.getCoderProblems().clear();
        process(connectionID, simpleMessage(message, title));
    }


    static void chat(Integer connectionID, int userID, String message, int scope) {
        User user = CoreServices.getUser(userID, false);
        int roomID = user.getRoomID();
        int roomType = user.getRoomType();
        UpdateChatResponse response = new UpdateChatResponse(ContestConstants.IRC_CHAT, message, roomType, roomID, scope);
        process(connectionID, response);
    }

    public static CreateLeaderBoardResponse createLeaderBoardResponse(LeaderBoard board) {
        return new CreateLeaderBoardResponse(board.getRoundID(), board.getItems());
    }

    static UpdateChatResponse createChatResponse(ChatEvent event) {
        return createChatResponse(event, -1, -1);
    }

    static UpdateChatResponse createChatResponse(ChatEvent event, int roomID, int roomType) {
        UpdateChatResponse response;
        int type = event.getChatStyle();
        if (event.getChatStyle() == ContestConstants.USER_CHAT || event.getChatStyle() == ContestConstants.MODERATED_CHAT_SPEAKER_CHAT || event.getChatStyle() == ContestConstants.MODERATED_CHAT_QUESTION_CHAT)
            response = new UpdateChatResponse(type, event.getMessage(), event.getPrefix(), event.getUserRating(), roomType, roomID, event.getScope());
        else
            response = new UpdateChatResponse(type, event.getMessage(), roomType, roomID, event.getScope());
        return response;
    }

    static void sendChat(Iterator connectionIDs, ChatEvent event) {
        sendChat(connectionIDs, event, event.getRoomID(), -1);
    }

    static void sendChat(Iterator connectionIDs, ChatEvent event, int roomID, int roomType) {
        if (connectionIDs == null || !connectionIDs.hasNext()) return;
        UpdateChatResponse response = createChatResponse(event, roomID, roomType);
        process(connectionIDs, response);
    }

    static void sendResponse(Iterator connectionIDs, ResponseEvent event) {
        if (connectionIDs == null || !connectionIDs.hasNext()) return;
        process(connectionIDs, event.getAllResponses());
    }

    /*added by SYHAAS 2002-05-18*/
    public static UpdateMenuResponse getModeratedChatMenuResponse(String contestName, String status) {
        //TODO hao make this use contest id, instead of name
        updateActiveChat(contestName, status);
        return new UpdateMenuResponse(ContestConstants.ACTIVE_CHAT_MENU, contestName, status);
    }

    private static SystestProgressResponse getSystestProgressResponse(int contestID, int roundID) {
        return new SystestProgressResponse(SystemTestProgress.getTestsDone(contestID, roundID), SystemTestProgress.getTotalTests(contestID, roundID), roundID);
    }

    /**
     * invoked when a lobby becomes full, or ceases to become full
     */
    static void sendLobbyFullEvent(Iterator connectionIDs, LobbyFullEvent event) {
        //TODO hao make this use the id, not the lobby name
        UpdateMenuResponse response = new UpdateMenuResponse(ContestConstants.LOBBY_MENU, event.getLobbyName(), event.getFull() ? "F" : "A");
        process(connectionIDs, response);
    }



    /**
     * Sends notification to all users that the Phase Has changed along with a specific message depending
     * on if the user is registered and if they are in their assigned room.
     */
    static void sendPhaseEvent(Iterator connectionIDs, PhaseEvent event) {
        int roomID = -1;
        int roomType = -1;

        Round contest = CoreServices.getContestRound(event.getRound());
        String displayName = contest.getDisplayName();
        int phase = event.getPhase();
        ArrayList allResponses = new ArrayList();

        // new PhaseData response
        PhaseData data = new PhaseData(event.getRound(), phase, contest.getPhaseStart(),
                contest.getPhaseEnd());
        PhaseDataResponse phaseResp = new PhaseDataResponse(data);
        allResponses.add(phaseResp);


        if (trace.isDebugEnabled()) trace.debug("sendPhaseEvent: contest=" + contest.toString());//added by SYHAAS -remove!

        /*
          Notify the spectators too
         */
        //        if (event.getPhase() != ContestConstants.PENDING_SYSTESTS_PHASE) {
        specAppProcessor.processPhaseEvent(contest);
        //        }

        String systemMessage = null;
        String userMessage = null;
        switch (phase) {
        case ContestConstants.REGISTRATION_PHASE:
            systemMessage = "System> Registration is now open for " + displayName + ".\n";
            break;
        case ContestConstants.ALMOST_CONTEST_PHASE:
            if (contest.getRoundProperties().hasRegistrationPhase()) {
                systemMessage = "System> Registration is closed for " + displayName + ".\n";
            }
            break;
        case ContestConstants.CODING_PHASE:
            systemMessage = "System> " + displayName + " Coding Phase is starting.\n";
            userMessage = "Coding Phase is starting for " + displayName + ".";
            // Enable the menu in case someone hasn't already gotten the update.
            allResponses.add(new EnableRoundResponse(event.getRound()));
            break;
        case ContestConstants.INTERMISSION_PHASE:
            systemMessage = "System> " + displayName + " Coding Phase is ending.\n";
            userMessage = "The coding phase is ending for " + displayName + ".";
            break;
        case ContestConstants.CHALLENGE_PHASE:
            systemMessage = "System> " + displayName + " Challenge Phase is starting.\n";
            userMessage = "Challenge Phase is starting for " + displayName + ".";
            break;
        case ContestConstants.VOTING_PHASE:
            systemMessage = "System> " + displayName + " Voting Phase is starting.\n";
            userMessage = "Voting Phase is starting for " + displayName + ".";
            break;
        case ContestConstants.TIE_BREAKING_VOTING_PHASE:
            systemMessage = "System> " + displayName + " Tie Breaking Voting Phase is starting.\n";
            userMessage = "Tie Breaking Voting Phase is starting for " + displayName + ".";
            break;
        case ContestConstants.PENDING_SYSTESTS_PHASE:
            //Ahhhh!! Phase sequence is hardcode in EveryPlace
            //We should ask the sequence to the round
            if (contest.getRoundProperties().hasChallengePhase()) {
                systemMessage = "System> " + displayName + " Challenge Phase is ending.\n";
                userMessage = "Challenge Phase is ending for " + displayName + ".";
            } else {
                //More ahhhs!!!!!! Again!
                if (contest.getIntermissionStart().before(contest.getIntermissionEnd())) {
                    systemMessage = "System> " + displayName + " Intermission Phase is ending.\n";
                    userMessage = "Intermission Phase is ending for " + displayName + ".";
                } else {
                    systemMessage = "System> " + displayName + " Coding Phase is ending.\n";
                    userMessage = "Coding Phase is ending for " + displayName + ".";
                }
            }
            break;
        case ContestConstants.SYSTEM_TESTING_PHASE:
            allResponses.add(getSystestProgressResponse(contest.getContestID(), contest.getRoundID()));
            break;
        case ContestConstants.INACTIVE_PHASE://modified by SYHAAS 2002-05-18
            if (!contest.isModeratedChat()) {
                userMessage = "The system tester has completed its testing. You can view the final results for any coding room by opening the summary window for that room.";
            }
            break;
        case ContestConstants.MODERATED_CHATTING_PHASE://added by SYHAAS 2002-05-18
            trace.debug("ENTERED MODERATED CHATTING PHASE SY");
            // Enable the menu in case someone hasn't already gotten the update.
            allResponses.add(getModeratedChatMenuResponse(displayName, "A"));
            systemMessage = "System> Moderated Chat, \"" + displayName + "\", has begun.\n";
            break;
        case ContestConstants.STARTS_IN_PHASE:
        case ContestConstants.CONTEST_COMPLETE_PHASE:
            /* Do nothing */
            break;
        default:
            trace.warn("systemMessage, Unknown phase (" + phase + ").");
            break;
        }

        if (systemMessage != null && event.getPopups()) {
            allResponses.add(new UpdateChatResponse(ContestConstants.SYSTEM_CHAT, systemMessage, roomType, roomID, ContestConstants.GLOBAL_CHAT_SCOPE));
        }

        //added by SYHAAS 2002-05-20
        if (contest.isModeratedChat()) {
            //send to everyone
            while (connectionIDs.hasNext()) {
                Integer connectionID = (Integer) connectionIDs.next();
                process(connectionID, allResponses);
            }

            return;//stop right here
        }

        //GT changed the line to use Round ID, WARNING MAY HAVE GREATER EFFECT
        //Registration registration = CoreServices.getRegistration(contest.getContestID());
        Registration registration = CoreServices.getRegistration(contest.getRoundID());
        // Local cache of roomnames so we dont have to keep hitting coreservices.
        while (connectionIDs.hasNext()) {
            // Create a new list for the next user.
            ArrayList nextUserResponses = (ArrayList) allResponses.clone();
            Integer connectionID = (Integer) connectionIDs.next();
            int userID = RequestProcessor.getUserID(connectionID);
            if (userID != RequestProcessor.INVALID_USER) {
                User user = CoreServices.getUser(userID, false);

                if (user.isSpectator()) {
                    trace.debug(" Ignoring spectator phase event ");
                    // do nothing
                } else {
                    // Update the phase for the room if the user isnt in a contest room.
                    if (!ContestConstants.isPracticeRoomType(user.getRoomType()) && user.getRoomID() != ContestConstants.INVALID_ROOM) {
                        //                        Room room = CoreServices.getRoom(user.getRoomID(), false);
                        //                        ArrayList timerInfo = CoreServices.getTimerInfo(user.getRoomID(), room.getName());
                        nextUserResponses.add(new PhaseDataResponse(contest.getPhaseData()));
                    }

                    Integer assignedRoomIDInteger = contest.getAssignedRoom(userID);
                    if (assignedRoomIDInteger != null) {
                        int assignedRoomID = assignedRoomIDInteger.intValue();
                        BaseCodingRoom room = (BaseCodingRoom) CoreServices.getRoom(assignedRoomID, false);
                        //                        UserState userState = UserState.getUserState(userID);
                        createActiveRoomMenu(room);
                        boolean userRegistered = registration.isRegistered(userID);
                        boolean userNotInRoom = user.getRoomID() != assignedRoomID;
                        if (trace.isDebugEnabled()) trace.debug("UserRegistered = " + userRegistered + " NotInRoom = " + userNotInRoom);
                        if (userRegistered) {
                            if (userNotInRoom) {
                                switch (phase) {
                                case ContestConstants.INACTIVE_PHASE:
                                case ContestConstants.STARTS_IN_PHASE:
                                case ContestConstants.REGISTRATION_PHASE:
                                case ContestConstants.ALMOST_CONTEST_PHASE:
                                case ContestConstants.INTERMISSION_PHASE:
                                case ContestConstants.PENDING_SYSTESTS_PHASE:
                                case ContestConstants.SYSTEM_TESTING_PHASE:
                                case ContestConstants.CONTEST_COMPLETE_PHASE:
                                case ContestConstants.MODERATED_CHATTING_PHASE:
                                case ContestConstants.VOTING_PHASE:
                                case ContestConstants.TIE_BREAKING_VOTING_PHASE:
                                    /* Do nothing */
                                    break;
                                case ContestConstants.CODING_PHASE:
                                case ContestConstants.CHALLENGE_PHASE:
                                    trace.debug("Creating popup response to move to room");
                                    String message = userMessage + "\nWould you like to go to your room now?";
                                    String title = "Phase Change";
                                    ArrayList buttons = new ArrayList();
                                    buttons.add("Yes");
                                    buttons.add("No");
                                    ArrayList roomInfo = new ArrayList();
                                    roomInfo.add(new Integer(ContestConstants.CODER_ROOM));
                                    roomInfo.add(new Integer(assignedRoomID));
                                    if(event.getPopups())
                                    {
                                        nextUserResponses.add(new PopUpGenericResponse(title, message, ContestConstants.ROOM_MOVE, ContestConstants.LABEL, buttons, roomInfo));
                                    }
                                    break;
                                default:
                                    trace.warn("userNotInRoom, Unknown phase (" + phase + ").");
                                    break;
                                }
                            } else {
                                switch (phase) {
                                case ContestConstants.INACTIVE_PHASE:
                                case ContestConstants.CODING_PHASE:
                                case ContestConstants.INTERMISSION_PHASE:
                                case ContestConstants.CHALLENGE_PHASE:
                                case ContestConstants.PENDING_SYSTESTS_PHASE:
                                    String message = userMessage;
                                    String title = "Phase Change";
                                    if(event.getPopups())
                                    {
                                        nextUserResponses.add(new PopUpGenericResponse(title, message, ContestConstants.GENERIC, ContestConstants.LABEL));
                                    }
                                    break;
                                case ContestConstants.STARTS_IN_PHASE:
                                case ContestConstants.REGISTRATION_PHASE:
                                case ContestConstants.ALMOST_CONTEST_PHASE:
                                case ContestConstants.SYSTEM_TESTING_PHASE:
                                case ContestConstants.CONTEST_COMPLETE_PHASE:
                                case ContestConstants.MODERATED_CHATTING_PHASE:
                                case ContestConstants.VOTING_PHASE:
                                case ContestConstants.TIE_BREAKING_VOTING_PHASE:
                                    /* Do nothing */
                                    break;
                                default:
                                    trace.warn("userInRoom, Unknown phase (" + phase + ").");
                                    break;
                                }
                            }
                        }
                    } else {
                        if (trace.isDebugEnabled()) trace.debug("Got a null assigned room for user: " + userID);
                    }
                    nextUserResponses.add(new SynchTimeResponse());
                    synchronized (disconnectedClientMessages) {
                        if (disconnectedClientMessages.containsKey(connectionID)) {
                            trace.info("User disconnected, accumulating phase changing messages, user ID=" + userID);
                            // When the user is temporarily disconnected, accumulate the message
                            ((List) disconnectedClientMessages.get(connectionID)).addAll(nextUserResponses);
                        }
                    }
                    process(connectionID, nextUserResponses);
                }
            }
        }
    }


    static void sendLeaderEvent(Iterator connectionIDs, LeaderEvent event) {
        if (connectionIDs == null || !connectionIDs.hasNext()) return;
        LeaderboardItem leaderboardItem = new LeaderboardItem(
                event.getRoomID(),
                event.getLeaderName(),
                event.getLeaderRating(),
                event.getLeaderSeed(),
                event.getLeaderPoints(),
                event.getIsClose()
                );
        UpdateLeaderBoardResponse response = new UpdateLeaderBoardResponse(event.getRoundID(), leaderboardItem);
        process(connectionIDs, response);
    }

    //    private static int getNumRoomComponents(int roomID) {
    //        int ncomponents = 0;
    //        Room baseRoom = CoreServices.getRoom( roomID, false );
    //        if( baseRoom != null && baseRoom instanceof ContestRoom )
    //        {
    //            ArrayList components = ( (ContestRoom)baseRoom ).getComponents();
    //
    //            if (components != null) ncomponents = components.size();
    //        }
    //        return ncomponents;
    //    }


    static void sendContestEvent(Iterator connectionIDs, int roomID, int roomType, ContestEvent event) {
        if (connectionIDs == null || !connectionIDs.hasNext()) return;
        ArrayList allResponses = new ArrayList();
        Room r_room = CoreServices.getRoom(roomID, false);
        BaseCodingRoom room;
        String handle = "";
        if (r_room instanceof BaseCodingRoom) {
            room = (BaseCodingRoom) r_room;
            if (event.getUserID() != RequestProcessor.INVALID_USER) {
                handle = room.getCoder(event.getUserID()).getName();
            }
        } else {
            throw new IllegalStateException("ContestEvent happening outside of ContestRoom!!");
        }

        if (event.getMessage() != null) {
            int chatType = ContestConstants.SYSTEM_CHAT;
            if (event.getAction() == ContestEvent.SUBMIT_COMPONENT || event.getAction() == ContestEvent.CHALLENGE_COMPONENT) {
                chatType = ContestConstants.EMPH_SYSTEM_CHAT;
                //if it is a team contest, both team and global should get message
                if (room.getType() == ContestConstants.TEAM_CODER_ROOM ||
                        room.getType() == ContestConstants.TEAM_PRACTICE_CODER_ROOM) {
                    allResponses.add(new UpdateChatResponse(chatType, event.getMessage(), roomType, roomID, ContestConstants.TEAM_CHAT_SCOPE));
                }
            }
            allResponses.add(new UpdateChatResponse(chatType, event.getMessage(), roomType, roomID, ContestConstants.GLOBAL_CHAT_SCOPE));
        }
        switch (event.getAction()) {
        case ContestEvent.OPEN_COMPONENT:
            allResponses.add(new UpdateCoderComponentResponse(handle,
                    new CoderComponentItem(event.getComponentID(), event.getTotalPoints(), ContestConstants.LOOKED_AT),
                    roomType, roomID));
            break;
        case ContestEvent.CLEAR_PRACTICER:
            for (Iterator componentIterator = room.getComponents().iterator(); componentIterator.hasNext();) {
                Integer componentID = (Integer) componentIterator.next();
                SimpleComponent component = CoreServices.getSimpleComponent(componentID.intValue());
                allResponses.add(new UpdateCoderComponentResponse(
                        handle,
                        new CoderComponentItem(
                                component.getComponentID(),
                                0,
                                ContestConstants.NOT_OPENED
                                ),
                                roomType,
                                roomID
                        ));
            }
            allResponses.add(new UpdateCoderPointsResponse(handle, 0.0, roomType, roomID));
            break;

            //added 2-20 rfairfax
        case ContestEvent.CLEAR_PRACTICE_PROBLEM:
            for (Iterator componentIterator = room.getComponents().iterator(); componentIterator.hasNext();) {
                Integer componentID = (Integer) componentIterator.next();
                if(componentID.intValue() == event.getComponentID())
                {
                    SimpleComponent component = CoreServices.getSimpleComponent(componentID.intValue());
                    allResponses.add(new UpdateCoderComponentResponse(
                            handle,
                            new CoderComponentItem(
                                    component.getComponentID(),
                                    0,
                                    ContestConstants.NOT_OPENED
                                    ),
                                    roomType,
                                    roomID
                            ));
                }
            }
            allResponses.add(new UpdateCoderPointsResponse(handle, Formatters.getDouble(event.getTotalPoints()).doubleValue(), roomType, roomID));
            break;
        case ContestEvent.COMPILE_COMPONENT:
            allResponses.add(new UpdateCoderComponentResponse(handle,
                    new CoderComponentItem(event.getComponentID(), event.getTotalPoints(), ContestConstants.COMPILED_UNSUBMITTED),
                    roomType, roomID));
            break;
        case ContestEvent.SUBMIT_COMPONENT:
            if (room instanceof LongContestRoom || CoreServices.getContestRound(room.getRoundID()) instanceof ForwarderLongContestRound) {
                Coder coder = room.getCoder(event.getUserID());
                allResponses.add(new UpdateCoderComponentResponse(handle,
                        newCoderComponentItem(coder.getComponent(event.getComponentID()), false),
                        roomType, roomID));
            } else  {
                allResponses.add(new UpdateCoderComponentResponse(handle,
                        new CoderComponentItem(event.getComponentID(), event.getSubmissionPoints(), ContestConstants.NOT_CHALLENGED, event.getLanguage()),
                        roomType, roomID));
                allResponses.add(new UpdateCoderPointsResponse(handle,
                        Formatters.getDouble(event.getTotalPoints()).doubleValue(), roomType, roomID));
            }
            break;
        case ContestEvent.TEST_COMPONENT:
            if (room instanceof LongContestRoom || CoreServices.getContestRound(room.getRoundID()) instanceof ForwarderLongContestRound) {
                Coder coder = room.getCoder(event.getUserID());
                allResponses.add(new UpdateCoderComponentResponse(handle,
                        newCoderComponentItem(coder.getComponent(event.getComponentID()), false),
                        roomType, roomID));
            }
            break;
        case ContestEvent.CHALLENGE_COMPONENT:
            if (event.getChallengeSuccess()) {
                // Update defendant
                allResponses.add(new UpdateCoderComponentResponse(handle,
                        new CoderComponentItem(event.getComponentID(), 0, ContestConstants.CHALLENGE_SUCCEEDED),
                        roomType, roomID));
                allResponses.add(new UpdateCoderPointsResponse(handle,
                        Formatters.getDouble(event.getTotalPoints()).doubleValue(), roomType, roomID));
            }
            allResponses.add(new UpdateCoderPointsResponse(event.getChallengerName(),
                    Formatters.getDouble(event.getChallengerTotalPoints()).doubleValue(), roomType, roomID));
            break;
        case ContestEvent.SCORES_UPDATED:
            for (Iterator it = room.getAllCoders(); it.hasNext();) {
                Coder coder = (Coder) it.next();
                LongCoderComponent coderComponent = (LongCoderComponent) coder.getComponent(event.getComponentID());
                if (coderComponent!= null && coderComponent.getStatus() >= ContestConstants.NOT_CHALLENGED) {
                    allResponses.add(new UpdateCoderComponentResponse(coder.getName(),
                            newCoderComponentItem(coderComponent, false), roomType, roomID));
                    allResponses.add(new UpdateCoderPointsResponse(coder.getName(),
                            Formatters.getDouble(coderComponent.getEarnedPoints()).doubleValue(), roomType, roomID));
                }

            }
            break;
        case ContestEvent.TEST_COMPLETED:
            Coder coder = room.getCoder(event.getUserID());
            allResponses.add(new UpdateCoderComponentResponse(handle,
                    newCoderComponentItem(coder.getComponent(event.getComponentID()), false),
                    roomType, roomID));
            break;
        default:
            trace.error("Unknown ContestEvent action: " + event.getAction());
            break;
        }
        if (allResponses.size() > 0) {
            process(connectionIDs, allResponses);
        }
    }


    private static CoderComponentItem newCoderComponentItem(BaseCoderComponent coderComponent, boolean isContestEnded) {
        if (coderComponent instanceof LongCoderComponent) {
            LongCoderComponent cc = (LongCoderComponent) coderComponent;
            return new LongCoderComponentItem(
                    cc.getComponentID(), cc.getEarnedPoints(), cc.getStatus(), cc.getSubmittedLanguage(),
                    cc.getSubmissionCount(), cc.getSubmittedTime(), cc.getExampleSubmissionCount(),
                    cc.getExampleSubmittedTime(), cc.getExampleSubmittedLanguage());
        } else {
            return new CoderComponentItem(coderComponent.getComponentID(), coderComponent.getEarnedPoints(),
                    coderComponent.getStatus(), coderComponent.getSubmittedLanguage(), !isContestEnded ? null : ((CoderComponent) coderComponent).getPassedSystemTests());
        }
    }

    /** SYHAAS 2002-05-13 created
     * approved questions come here to be broadcast
     */
    static ArrayList createQuestionBroadcast(String username, String message, int roomID) {
        ArrayList allResponses = new ArrayList();
        int roomIndex = roomID;
        int roomType = ContestConstants.MODERATED_CHAT_ROOM;
        UpdateChatResponse response = new UpdateChatResponse(ContestConstants.MODERATED_CHAT_QUESTION_CHAT, message, username + ">", CoreServices.getUser(username).getRating(Rating.ALGO).getRating(), roomType, roomIndex, ContestConstants.GLOBAL_CHAT_SCOPE);
        allResponses.add(response);
        return allResponses;
    }

    static void getProblem(Integer connectionID, int problemId, long roundID, int divisionID) {
        if (trace.isDebugEnabled()) trace.debug("getProblem( connectionID = " + connectionID + ", roundID = " + roundID + ", problemID = "
                + problemId + ", divisionID = " + divisionID + ")");

        Problem problem = CoreServices.getProblem(problemId);

        GetProblemResponse response = new GetProblemResponse(ContestConstants.EDIT_SOURCE_RW, problem, roundID, divisionID);
        process(connectionID, response);
    }

    static void getTeamProblem(Integer connectionID, int problemID, long roundID, int divisionID) {
        trace.debug("sending GetTeamProblemResponse");
        Problem problem = CoreServices.getProblem(problemID);
        GetTeamProblemResponse response = new GetTeamProblemResponse(problem, roundID, divisionID);
        process(connectionID, response);
    }


    static ArrayList enterRoom(User user) {
        ArrayList allResponses = new ArrayList();

        int roomID = user.getRoomID();
        int roomType = user.getRoomType();
        // TODO get Team name
        String teamName = "";

        //get the room, for rating type
        Room room = CoreServices.getRoom(roomID, false);

        UpdateUserListResponse updateUserListResponse = new UpdateUserListResponse(ContestConstants.ROOM_USERS, ContestConstants.ADD, new UserListItem(user.getName(), user.getRating(room.getRatingType()).getRating(), teamName, ContestConstants.SINGLE_USER), roomType, roomID);
        allResponses.add(updateUserListResponse);

        String message = "System> " + user.getName() + " has entered the room.\n";
        UpdateChatResponse updateChatResponse = new UpdateChatResponse(ContestConstants.SYSTEM_CHAT, message, roomType, roomID, ContestConstants.GLOBAL_CHAT_SCOPE);
        allResponses.add(updateChatResponse);

        return allResponses;
    }

    static ArrayList leaveRoom(User user, boolean logout) {
        ArrayList allResponses = new ArrayList();

        //        UserState state = UserState.getUserState(user.getID());
        int roomID = user.getRoomID();
        int roomType = user.getRoomType();

        Room room = CoreServices.getRoom(roomID, false);

        if (trace.isDebugEnabled()) trace.debug("leaveRoom roomID = " + user.getRoomID() + " roomID = " + roomID);
        if (roomID != -1 || roomType == ContestConstants.LOBBY_ROOM) {
            String message;
            if (logout) {
                message = "System> " + user.getName() + " has logged out.\n";
            } else {
                message = "System> " + user.getName() + " has left the room.\n";
            }
            UpdateChatResponse updateChatResponse = new UpdateChatResponse(ContestConstants.SYSTEM_CHAT, message, roomType, roomID, ContestConstants.GLOBAL_CHAT_SCOPE);
            allResponses.add(updateChatResponse);

            if (trace.isDebugEnabled()) trace.debug("Updating userList for roomIndex: " + roomID + " type: " + roomType + " name: " + user.getName());
            UpdateUserListResponse updateUserListResponse = new UpdateUserListResponse(ContestConstants.ROOM_USERS, ContestConstants.REMOVE, new UserListItem(user.getName(), user.getRating(room.getRatingType()).getRating(), ContestConstants.SINGLE_USER), roomType, roomID);
            allResponses.add(updateUserListResponse);
        }
        return allResponses;
    }

    static void testInfo(Integer connectionID, RoundComponent component) {
        DataType[] params = component.getComponent().getParamTypes();
        TestInfoResponse response = new TestInfoResponse(params, component.getComponent().getComponentID());
        process(connectionID, response);
    }

    static void submitProblem(Integer connectionID, int componentID) {
        String msg = "Resubmission will result in a 10% penalty plus a time factor. Are you sure you wish to resubmit?";
        String title = "Multiple Submission";
        ArrayList buttons = new ArrayList();
        buttons.add("Yes");
        buttons.add("No");
        PopUpGenericResponse response = new PopUpGenericResponse(title, msg, ContestConstants.SUBMIT_PROBLEM, ContestConstants.LABEL, buttons, new Integer(componentID));
        process(connectionID, response);
    }

    static void registerUsers(Integer connectionID, long roundID, Registration reg) {
        List names = reg.getUserNames();
        List ratings = reg.getUserRatings();
        List countries = reg.getUserCountries();
        List teams = reg.getUserTeams();
        UserListItem users[] = new UserListItem[names.size()];
        for (int i = 0; i < names.size(); i++) {
            String name = (String) names.get(i);
            Integer rating = (Integer) ratings.get(i);
            String country = (String) countries.get(i);
            String team = (String) teams.get(i);
            users[i] = new UserListItem(name, rating.intValue(), team, -1, ContestConstants.SINGLE_USER, country);
        }
        RegisteredUsersResponse response = new RegisteredUsersResponse(roundID, users);
        process(connectionID, response);
    }

    // Maps team id -> HashSet of connection ids who need updates
    private static HashMap s_teamListListenerMap = new HashMap(20);

    public static void closeTeamList(Integer connectionID, int teamID) {
        HashSet listeners = (HashSet) s_teamListListenerMap.get(new Integer(teamID));
        if (listeners != null) {
            listeners.remove(connectionID);
        } else {
            trace.error("No team found with id: " + teamID);
        }
    }

    public static void getTeamList(Integer connectionID, int teamID) {
        HashSet listeners = (HashSet) s_teamListListenerMap.get(new Integer(teamID));
        if (listeners == null) {
            listeners = new HashSet();
            s_teamListListenerMap.put(new Integer(teamID), listeners);
        }
        listeners.add(connectionID);
    }

    public static void updateTeamList(Team team) {
        HashSet listeners = (HashSet) s_teamListListenerMap.get(new Integer(team.getID()));

        User cap = CoreServices.getUser(team.getCaptainID(), false);
        TeamListInfo info = new TeamListInfo(team.getName(), team.getRating(), cap.getName(),
                cap.getRating(Rating.ALGO).getRating(), team.getAvailable(), team.getSize(),
                "Pending");
        UpdateTeamListResponse response = new UpdateTeamListResponse(info);
        process(listeners.iterator(), response);
    }


    static void registerInfo(Integer connectionID, Round contest, Registration registration) {
        
	long minutesUntilCoding = contest != null && contest.getCodingStart() != null ? 
		((contest.getCodingStart().getTime() - System.currentTimeMillis()) / 1000 ) / 60 :
		0;

	ArrayList allResponses = new ArrayList();
        PopUpGenericResponse response;

        StringBuilder mainText = new StringBuilder(contest.getDisplayName());
        mainText.append(" starts in ");
        mainText.append(minutesUntilCoding);
        mainText.append(minutesUntilCoding == 1 ? " minute " : " minutes ");
        mainText.append("and is open for registration. Agree to the terms below to register.\n\nRegistration Terms:\n");
        mainText.append(registration.getIAgreeString());
        String msg = mainText.toString();
        String title = "Event Registration";
        ArrayList buttons = new ArrayList();
        buttons.add("I Agree");
        buttons.add("I Disagree");
        if (registration.hasSurveyQuestions()) {
            ArrayList surveyQuestions = new ArrayList();
            for (Iterator allQuestions = registration.getSurveyQuestions(); allQuestions.hasNext();) {
                SurveyQuestion question = (SurveyQuestion) allQuestions.next();
                ArrayList questionChoices = question.getAnswerChoices();
                ArrayList choicesData = new ArrayList();
                for (int i = 0; i < questionChoices.size(); i++) {
                    SurveyAnswer answer = (SurveyAnswer) questionChoices.get(i);
                    SurveyChoiceData choiceData = new SurveyChoiceData(answer.getAnswerID(), answer.getAnswer(), answer.isCorrectEligible());
                    choicesData.add(choiceData);
                }
                SurveyQuestionData questionData = new SurveyQuestionData(question.getID(), question.getString(), question.getType(), question.isEligibleQuestion(), choicesData);
                if (trace.isDebugEnabled()) trace.debug("Adding questionData: " + question.getString() + " choice count = " + choicesData.size());
                surveyQuestions.add(questionData);
            }
            String surveyMsg = "TopCoder expects that all users of the TopCoder Competition Arena will exhibit professional behavior.  "
                    + "Any member whose chat is considered by us to be offensive in any way will be immediately removed from the Arena.  "
                    + "If you do not plan to respect this policy, we ask that you not participate in the Arena. \n\n"
                    + "In order to register for participation in this event, you must answer the survey question(s) and agree "
                    + "to the rules of this competition.  The results of this survey may be reported in aggregate and displayed "
                    + "on TopCoder's website or provided to sponsors and prospective sponsors of TopCoder.  When you have "
                    + "selected an answer to the survey question and read the rules, click the \"Agree\" button at the bottom.";
            response = new PopUpGenericResponse(title, msg, ContestConstants.CONTEST_REGISTRATION_SURVEY, ContestConstants.TEXT_AREA, buttons, surveyQuestions, surveyMsg, new Long(contest.getRoundID()));
        } else {
            response = new PopUpGenericResponse(title, msg, ContestConstants.CONTEST_REGISTRATION, ContestConstants.TEXT_AREA, buttons, new Long(contest.getRoundID()));
        }
        allResponses.add(response);
        process(connectionID, allResponses);
    }


    static void getChallengeComponent(Integer connectionID, BaseCodingRoom room, RoundComponent component, String code,
            Coder defenderCoder, Integer languageID) {
        Problem problem = CoreServices.getProblem(component.getComponent().getProblemID());
        ArrayList allResponses = new ArrayList();
        //FIXME Why we send the Problem statement each time the user wants to get the source code for a problem
        GetProblemResponse response = new GetProblemResponse(ContestConstants.VIEW_SOURCE, problem, room.getRoundID(), room.getDivisionID());
        allResponses.add(response);
        allResponses.add(getOpenComponentResponse(component.getComponent().getComponentID(), code, ContestConstants.VIEW_SOURCE, room.getType(), room.getRoomID(), languageID, defenderCoder.getName()));
        process(connectionID, allResponses);
    }


    static void getSourceCode(Integer connectionID, BaseCodingRoom room, RoundComponent component, String code,
            Coder defenderCoder, Integer languageID) {
        getChallengeComponent(connectionID, room, component, code, defenderCoder, languageID);
    }

    static void challengeInfo(Integer connectionID, RoundComponent roundComponent, String message) {
        DataType[] params = roundComponent.getComponent().getParamTypes();
        ChallengeInfoResponse response = new ChallengeInfoResponse(params, message);
        process(connectionID, response);
    }

    static void watchDivSummary(Integer connectionID, ArrayList rooms, int requestID) {
        ArrayList allResponses = new ArrayList();

        for(int i = 0; i < rooms.size(); i++)
        {
            BaseCodingRoom room = (BaseCodingRoom)rooms.get(i);

            int roomID = room.getRoomID();
            if (trace.isDebugEnabled()) trace.debug("watching roomID = " + roomID + ", roomID = " + roomID);
            //ArrayList[] userData = room.getUserData();

            CreateChallengeTableResponse createChallengeTableResponse = createChallengeTable(room, roomID, ContestConstants.WATCH_ROOM);
            allResponses.add(createChallengeTableResponse);

            //            CreateUserListResponse createUserListResponse = createUserList(userData[0], userData[1], ContestConstants.ROOM_USERS, roomID, ContestConstants.WATCH_ROOM);
            //            allResponses.add(createUserListResponse);

            //            RoomInfoResponse roomTypeResponse = new RoomInfoResponse(ContestConstants.WATCH_ROOM, roomID, "", "");
            //            allResponses.add(roomTypeResponse);

            //            WatchResponse watchResponse = new WatchResponse(ContestConstants.WATCH_ROOM, roomID);
            //            allResponses.add(watchResponse);

        }

        //allResponses.add(unsynchronizeResponse);

        process(connectionID, allResponses);
    }

    static void watch(Integer connectionID, BaseCodingRoom room, int requestID) {
        int roomID = room.getRoomID();
        if (trace.isDebugEnabled()) trace.debug("watching roomID = " + roomID + ", roomID = " + roomID);
        MessageBuilder msgBuilder = new MessageBuilder(4);
        ArrayList[] userData = room.getUserData();

        CreateChallengeTableResponse createChallengeTableResponse = createChallengeTable(room, roomID, ContestConstants.WATCH_ROOM);
        msgBuilder.add(createChallengeTableResponse);

        CreateUserListResponse createUserListResponse = createUserList(userData[0], userData[1], ContestConstants.ROOM_USERS, roomID, ContestConstants.WATCH_ROOM);
        msgBuilder.add(createUserListResponse);

        RoomInfoResponse roomTypeResponse = new RoomInfoResponse(ContestConstants.WATCH_ROOM, roomID, "", "");
        msgBuilder.add(roomTypeResponse);

        WatchResponse watchResponse = new WatchResponse(ContestConstants.WATCH_ROOM, roomID);
        msgBuilder.add(watchResponse);

        process(connectionID, msgBuilder.buildResponseToSyncRequest(requestID));
    }

    static void assignComponents(Integer connectionID, int roundID, int divisionID, ComponentLabel[] componentLabels) {
        CoreServices.getContestRound(roundID);
        ArrayList allResponses = new ArrayList();
        allResponses.add(new AssignComponentsResponse(componentLabels, roundID, divisionID));
        process(connectionID, allResponses);
    }

    static void keepAlive(Integer connectionID, int requestId) {
        process(connectionID, new KeepAliveResponse(requestId));
    }

    /*
      private static void openComponent(Integer connectionID, int componentID, String code, int editSource, int roomType, int roomID,
      Integer languageID, String writerHandler) {
      openComponent(connectionID, componentID, code, editSource, roomType, roomID, languageID, writerHandler, null);
      }
     */

    static void openComponent(Integer connectionID, int componentID, String code, int editSource, int roomType, int roomID,
            Integer languageID, String writerHandler, PhaseDataResponse phaseDataResponse) {
        ArrayList allResponses = new ArrayList();
        allResponses.add(getOpenComponentResponse(componentID, code, editSource, roomType, roomID, languageID, writerHandler));
        if (phaseDataResponse != null) {
            allResponses.add(phaseDataResponse);
        }
        process(connectionID, allResponses);
    }

    static OpenComponentResponse getOpenComponentResponse(int componentID, String code, int editSource, int roomType, int roomID,
            Integer languageID, String writerHandler) {
        return new OpenComponentResponse(writerHandler, componentID, code, editSource, roomType, roomID,
                languageID.intValue());
    }

    static void sendRefreshPracticeRooms(Iterator connectionIDs)
    {
        ArrayList allResponses = new ArrayList();
        allResponses.add(s_categories);
        allResponses.add(s_practiceContests);
        while (connectionIDs.hasNext()) {
            process((Integer) connectionIDs.next(), allResponses);
        }
    }
    static void loadContestRound(Iterator connectionIDs, Round round) {
        MessageBuilder msgBuilder = new MessageBuilder(2);
        msgBuilder.add(new UpdateRoundListResponse(
                UpdateRoundListResponse.ACTION_ADD,
                new RoundData(
                        round.getRoundID(),
                        round.getContestName(),
                        round.getRoundName(),
                        round.getRoundTypeId(),
                        round.getPhaseData(),
                        round.getActiveMenu(),
                        round.getRoundCustomProperties()
                        )
                ));
        addRoundDefinitionResponses(msgBuilder, round.getRoundID());
        Object response = msgBuilder.buildResponse();
        while (connectionIDs.hasNext()) {
            process((Integer) connectionIDs.next(), response);
        }
        specAppProcessor.loadRound(round);
    }

    private static void addRoundDefinitionResponses(MessageBuilder msgBuilder, long roundID) {
        Round round = CoreServices.getContestRound((int) roundID);
        msgBuilder.add(createActiveRoomMenu(round.getRoundID()));
        LeaderBoard leaderboard = CoreServices.getLeaderBoard(round.getRoundID(), false);
        msgBuilder.add(createLeaderBoardResponse(leaderboard));
        msgBuilder.add(createRoundScheduleResponse(new Long(round.getRoundID())));
        msgBuilder.add(createProblems(round.getRoundID(), ContestConstants.DIVISION_ONE));
        msgBuilder.add(createProblems(round.getRoundID(), ContestConstants.DIVISION_TWO));
        msgBuilder.add(createProblems(round.getRoundID(), ContestConstants.DIVISION_ADMIN));
    }

    static void unloadContestRound(Iterator connectionIDs, Round round) {
        BaseResponse response = new UpdateRoundListResponse(UpdateRoundListResponse.ACTION_REMOVE, new RoundData(
                round.getRoundID(),
                round.getContestName(),
                round.getRoundName(),
                round.getRoundTypeId(),
                round.getPhaseData(),
                round.getActiveMenu(),
                round.getRoundCustomProperties()
                ));
        while (connectionIDs.hasNext()) {
            process((Integer) connectionIDs.next(), response);
        }
    }

    private static CreateRoundListResponse getActiveRoundsResponse(User user) {
        if (user.isLevelTwoAdmin()) {
            return s_activeAdminContests;
        } else {
            List additionalRounds = getRoundsRequiringRegisteredUser(user);
            if (additionalRounds == null) {
                return s_activeContests;
            } else {
                RoundData[] actives = s_activeContests.getRoundData();
                RoundData[] allRounds = new RoundData[actives.length+additionalRounds.size()];
                System.arraycopy(actives, 0, allRounds, 0, actives.length);
                for (int i = actives.length; i < allRounds.length; i++) {
                    allRounds[i] = (RoundData) additionalRounds.get(i-actives.length);
                }
                return new CreateRoundListResponse(CreateRoundListResponse.ACTIVE, allRounds);
            }
        }
    }

    private static List getRoundsRequiringRegisteredUser(User user) {
        List additionalRounds = null;
        for (int i = 0; i < s_activeContestRequiringRegisteredUser.length; i++) {
            RoundData roundData = s_activeContestRequiringRegisteredUser[i];
            if (CoreServices.getRegistration(roundData.getRoundID()).isRegistered(user.getID())) {
                if (additionalRounds == null) {
                    additionalRounds = new ArrayList(5);
                }
                additionalRounds.add(roundData);
            }
        }
        return additionalRounds;
    }

    static void roundSchedule(Integer connectionID, Long roundID) {
        ArrayList list = new ArrayList(1);
        list.add(connectionID);
        roundSchedule(list.iterator(), roundID);
    }

    static void roundSchedule(Iterator connectionIDs, Long roundID) {
        RoundScheduleResponse response = createRoundScheduleResponse(roundID);
        while (connectionIDs.hasNext()) {
            process((Integer) connectionIDs.next(), response);
        }
    }

    private static RoundScheduleResponse createRoundScheduleResponse(Long roundID) {
        Round round = CoreServices.getContestRound(roundID.intValue());
        PhaseData schedule[] = null;
        if (!round.getRoundProperties().hasChallengePhase()) {
            //We need to check if we have an intermission.
            if (round.getIntermissionStart().before(round.getIntermissionEnd())) {
                schedule = new PhaseData[]{
                        new PhaseData(
                                roundID.intValue(),
                                ContestConstants.CODING_PHASE,
                                round.getCodingStart().getTime(),
                                round.getCodingEnd().getTime()
                                ),
                                new PhaseData(
                                        roundID.intValue(),
                                        ContestConstants.INTERMISSION_PHASE,
                                        round.getIntermissionStart().getTime(),
                                        round.getIntermissionEnd().getTime()
                                        ),
                                        new PhaseData(
                                                roundID.intValue(),
                                                ContestConstants.SYSTEM_TESTING_PHASE,
                                                round.getSystemTestStart().getTime(),
                                                round.getSystemTestEnd().getTime()
                                                )
                };
            } else {
                schedule = new PhaseData[]{
                        new PhaseData(
                                roundID.intValue(),
                                ContestConstants.CODING_PHASE,
                                round.getCodingStart().getTime(),
                                round.getCodingEnd().getTime()
                                ),
                                new PhaseData(
                                        roundID.intValue(),
                                        ContestConstants.SYSTEM_TESTING_PHASE,
                                        round.getSystemTestStart().getTime(),
                                        round.getSystemTestEnd().getTime()
                                        )
                };
            }
        } else {
            schedule = new PhaseData[]{
                    new PhaseData(
                            roundID.intValue(),
                            ContestConstants.REGISTRATION_PHASE,
                            round.getRegistrationStart().getTime(),
                            round.getRegistrationEnd().getTime()
                            ),
                            new PhaseData(
                                    roundID.intValue(),
                                    ContestConstants.CODING_PHASE,
                                    round.getCodingStart().getTime(),
                                    round.getCodingEnd().getTime()
                                    ),
                                    new PhaseData(
                                            roundID.intValue(),
                                            ContestConstants.INTERMISSION_PHASE,
                                            round.getIntermissionStart().getTime(),
                                            round.getIntermissionEnd().getTime()
                                            ),
                                            new PhaseData(
                                                    roundID.intValue(),
                                                    ContestConstants.CHALLENGE_PHASE,
                                                    round.getChallengeStart().getTime(),
                                                    round.getChallengeEnd().getTime()
                                                    ),
                                                    new PhaseData(
                                                            roundID.intValue(),
                                                            ContestConstants.SYSTEM_TESTING_PHASE,
                                                            round.getSystemTestStart().getTime(),
                                                            round.getSystemTestEnd().getTime()
                                                            )
            };
        }
        RoundScheduleResponse response = new RoundScheduleResponse(roundID.longValue(), schedule);
        return response;
    }

    public static void addPracticeCoder(Iterator connectionIds, BaseCodingRoom contestRoom) {
        process(connectionIds, createChallengeTable(contestRoom, contestRoom.getRoomID(), contestRoom.getType()));
    }

    public static void addCoderToRoom(Iterator connectionIds, BaseCodingRoom contestRoom, int roomType) {
        process(connectionIds, createChallengeTable(contestRoom, contestRoom.getRoomID(), roomType));
    }

    // For weakest link rounds only
    public static void announceWeakestLinkElimination(int victimID) {
        specAppProcessor.announceWeakestLinkElimination(victimID);
    }

    static void noBadgeId(Integer connectionID, int requestID, String handle, SealedSerializable password) {
        MessageBuilder msgBuilder = new MessageBuilder(3);
        msgBuilder.add(new LoginResponse(false));
        msgBuilder.add(new UnsynchronizeResponse(requestID));
        msgBuilder.add(new NoBadgeIdResponse(handle, password));
        process(connectionID, msgBuilder.buildResponseToSyncRequest(requestID));
    }

    /**
     * Returns a response message that forces the client to update the Practice System Test results table
     * 
     * @param data Containes the test case result data
     * @return The response object
     */
    public static BaseResponse updatePracticeSystemTestResult(PracticeTestResultData data) {
        return new PracticeSystemTestResultResponse(data);
    }

    /**
     * Returns a response message that forces the client to initialize Practice System Test results table
     * 
     * @param testCaseCountByComponentId A Map<Integer,Integer> containing the Component Id and the number of test cases
     *                                  of the Component
     * @return The response object
     */
    public static BaseResponse practiceSystemTestResponse(Map testCaseCountByComponentId) {
        return new PracticeSystemTestResponse(testCaseCountByComponentId);
    }

    /*
      private static int getTeamID(Integer connectionID) {
      int userID = RequestProcessor.getUserID(connectionID);
      User user = CoreServices.getUser(userID, false);
      return user.getTeamID();
      }
     */

    static void sendVerifyClass(Integer connectionID, int requestID, byte[] code) {
        MessageBuilder msgBuilder = new MessageBuilder();
        VerifyResponse resp = new VerifyResponse(code);
        msgBuilder.add(resp);
        process(connectionID, msgBuilder.buildResponseToSyncRequest(requestID));
    }

    static void sendVerifyResult(Integer connectionID, int requestID, boolean success) {
        MessageBuilder msgBuilder = new MessageBuilder();
        VerifyResultResponse resp = new VerifyResultResponse(success);
        msgBuilder.add(resp);
        process(connectionID, msgBuilder.buildResponseToSyncRequest(requestID));
    }

    static void sendPong(Integer connectionID, byte[] payload) {
        process(connectionID, new PongResponse(payload));
    }

    static SubmissionHistoryResponse submissionHistoryResponse(int roundId, String handle, int componentId, boolean exampleHistory, LongSubmissionData[] submissions) {
        int[] numbers = new int[submissions.length];
        long[] times = new long[submissions.length];
        int[] langs = new int[submissions.length];
        double[] scores = null;
        if (!exampleHistory) {
            scores = new double[submissions.length];
        }

        int pendingSubmissionNumber = 0;
        for (int i = 0; i < submissions.length; i++) {
            LongSubmissionData data = submissions[i];
            numbers[i] = data.getNumber();
            times[i] = data.getTimestamp().getTime();
            langs[i] = data.getLanguageId();
            if (!exampleHistory) {
                scores[i] = data.getScore();
            }
            if (data.hasPendingTests()) {
                pendingSubmissionNumber = data.getNumber();
            }
        }
        return new SubmissionHistoryResponse(roundId, handle, componentId, exampleHistory, pendingSubmissionNumber, numbers, times, langs, scores);
    }


    /**
     * Create the long test response.
     * @param roundId the round id.
     * @param handle the user handle.
     * @param componentId the problem component id.
     * @param results the long test results
     * @param resultsType the results type.
     * @return the long test results response.
     */
    static LongTestResultsResponse longTestResultsResponse(int roundId, String handle, int componentId, LongTestResult[] results, int resultsType) {
        String[] args = new String[results.length];
        LongTestResultData[]  resultData = new LongTestResultData[results.length];
        for (int i = 0; i < results.length; i++) {
            LongTestResult data = results[i];
            resultData[i] = new LongTestResultData(i, data.getScore(), data.getFatalErrors(), data.getProcessingTime(),
                    data.getStdOut(), data.getStdErr(), data.getPeakMemoryUsed());
            args[i] = data.getArg();
        }
        return new LongTestResultsResponse(roundId, componentId, handle, resultsType, args, resultData);
    }

    static void sendExchangeKeyResponse(Integer connectionID, int requestID, byte[] key) {
        MessageBuilder msgBuilder = new MessageBuilder();
        ExchangeKeyResponse resp = new ExchangeKeyResponse(key);
        msgBuilder.add(resp);
        process(connectionID, msgBuilder.buildResponseToSyncRequest(requestID));
    }

    /**
     * Gets the list of rounds user is registered to and sends them to the client.
     *
     * @param connectionID connection id of the user
     * @param user the user who made the request
     * @since 1.1
     */
    public static void getRegisteredRoundList(Integer connectionID, User user) {
        if (trace.isDebugEnabled()) {
            trace.debug("get registered round list: connectionID = " + connectionID
                    + " user = " + user);
        }
        List<RoundData> roundData = new ArrayList<RoundData>(0);
        List registeredRounds = getRoundsRequiringRegisteredUser(user);
        if (registeredRounds != null) {
            roundData.addAll(registeredRounds);
        }
        if (s_activeContests != null) {
            RoundData [] activeRounds = s_activeContests.getRoundData();
            if (activeRounds != null) {
                for (int i = 0; i < activeRounds.length; i++) {
                    RoundData round = activeRounds [i];
                    if (CoreServices.getRegistration(round.getRoundID()).isRegistered(user.getID())) {
                        roundData.add(activeRounds [i]);
                    }
                }
            }
        }

        if (roundData.size() > 0) {
            RoundData [] rd = new RoundData [roundData.size()];
            for (int i = 0; i < roundData.size(); i++) {
                rd [i] = roundData.get(i);
            }

            RegisteredRoundListResponse registeredRoundListResponse =
                    new RegisteredRoundListResponse(rd);
            process(connectionID, registeredRoundListResponse);
        } else {
            RegisteredRoundListResponse registeredRoundListResponse =
                    new RegisteredRoundListResponse(null);
            process(connectionID, registeredRoundListResponse);
        }
        if (trace.isDebugEnabled()) {
            trace.debug("getRegisteredRoundList process successfully, connectionID = " + connectionID
                    + "user = " + user);
        }
    }

    /**
     * Gets the list of problems assigned to given round and division
     * and returns them to the client.
     *
     * @param connectionID connection id of the user
     * @param user the user
     * @param roundID the round id
     * @param divisionID the division id
     * @since 1.1
     */
    public static void getRoundProblems(Integer connectionID, User user, long roundID, int divisionID) {
        if (trace.isDebugEnabled()) {
            trace.debug("get round problems: connectionID = " + connectionID
                    + " roundID = " + roundID + ", divisionID = " + divisionID);
        }

        Round round = CoreServices.getContestRound((int) roundID);
        RoundProblemsResponse rpr = new RoundProblemsResponse(roundID, divisionID);
        if (round == null) {
            if (trace.isDebugEnabled()) {
                trace.debug("get round problems: bad roundID");
            }
            rpr.setErrorMessage("Bad round id.");
        } else {
            if (CoreServices.getRegistration(round.getRoundID()).isRegistered(user.getID())) {
                try {
                    ProblemLabel [] problemLabel = round.getProblemLabels(divisionID);
                    ComponentLabel [] componentLabel = round.getComponentLabels(divisionID);
                    rpr.setProblems(problemLabel);
                    rpr.setAssignedComponents(componentLabel);
                } catch (IllegalArgumentException e) {
                    if (trace.isDebugEnabled()) {
                        trace.debug("get round problems: bad divisionID");
                    }
                    rpr.setErrorMessage("Bad division id.");
                }
            } else {
                rpr.setErrorMessage("You are not registered for this round.");
            }
        }
        process(connectionID, rpr);
        if (trace.isDebugEnabled()) {
            trace.debug("getRoundProblems processed successfully, connectionID = " + connectionID
                    + " roundID = " + roundID + ", divisionID = " + divisionID);
        }
    }
}
