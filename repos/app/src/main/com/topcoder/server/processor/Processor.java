/*
 * Copyright (C) - 2022 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.server.processor;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Date;

import org.apache.commons.collections.CollectionUtils;

import com.topcoder.farm.deployer.process.ProcessRunner;
import com.topcoder.farm.deployer.process.ProcessRunner.ProcessRunResult;
import com.topcoder.netCommon.contest.ComponentAssignmentData;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contest.TeamConstants;
import com.topcoder.netCommon.contest.round.RoundProperties;
import com.topcoder.netCommon.contest.round.text.ComponentNameBuilder;
import com.topcoder.netCommon.contestantMessages.response.BaseResponse;
import com.topcoder.netCommon.contestantMessages.response.CoderHistoryResponse;
import com.topcoder.netCommon.contestantMessages.response.CreateChallengeTableResponse;
import com.topcoder.netCommon.contestantMessages.response.EnableRoundResponse;
import com.topcoder.netCommon.contestantMessages.response.ForcedLogoutResponse;
import com.topcoder.netCommon.contestantMessages.response.PhaseDataResponse;
import com.topcoder.netCommon.contestantMessages.response.PopUpGenericResponse;
import com.topcoder.netCommon.contestantMessages.response.SubmitResultsResponse;
import com.topcoder.netCommon.contestantMessages.response.UpdateChatResponse;
import com.topcoder.netCommon.contestantMessages.response.ChallengesListResponse;
import com.topcoder.netCommon.contestantMessages.response.ChallengeResponse;
import com.topcoder.netCommon.contestantMessages.response.data.CoderHistoryData;
import com.topcoder.netCommon.contestantMessages.response.data.PhaseData;
import com.topcoder.netCommon.contestantMessages.response.data.PracticeTestResultData;
import com.topcoder.netCommon.contestantMessages.response.data.UserListItem;
import com.topcoder.netCommon.contestantMessages.response.data.ChallengeData;
import com.topcoder.server.common.ActionEvent;
import com.topcoder.server.common.BaseCoderComponent;
import com.topcoder.server.common.BaseCodingRoom;
import com.topcoder.server.common.BaseRound;
import com.topcoder.server.common.ChallengeAttributes;
import com.topcoder.server.common.ChatEvent;
import com.topcoder.server.common.Coder;
import com.topcoder.server.common.CoderComponent;
import com.topcoder.server.common.CoderFactory;
import com.topcoder.server.common.CoderHistory;
import com.topcoder.server.common.CoderHistory.ChallengeCoder;
import com.topcoder.server.common.CompileEvent;
import com.topcoder.server.common.ContestEvent;
import com.topcoder.server.common.ContestRoom;
import com.topcoder.server.common.ContestRound;
import com.topcoder.server.common.EventRegistration;
import com.topcoder.server.common.ForwarderLongContestRound;
import com.topcoder.server.common.LeaderBoard;
import com.topcoder.server.common.LeaderEvent;
import com.topcoder.server.common.LobbyFullEvent;
import com.topcoder.server.common.Location;
import com.topcoder.server.common.LongCoderComponent;
import com.topcoder.server.common.LongContestRoom;
import com.topcoder.server.common.LongContestRound;
import com.topcoder.server.common.MoveEvent;
import com.topcoder.server.common.PhaseEvent;
import com.topcoder.server.common.Registration;
import com.topcoder.server.common.ReplayChallengeEvent;
import com.topcoder.server.common.ReplayCompileEvent;
import com.topcoder.server.common.ReplaySubmitEvent;
import com.topcoder.server.common.ResponseEvent;
import com.topcoder.server.common.Results;
import com.topcoder.server.common.Room;
import com.topcoder.server.common.Round;
import com.topcoder.server.common.RoundComponent;
import com.topcoder.server.common.RoundEvent;
import com.topcoder.server.common.ServerContestConstants;
import com.topcoder.server.common.Submission;
import com.topcoder.server.common.SubmitResults;
import com.topcoder.server.common.TCEvent;
import com.topcoder.server.common.Team;
import com.topcoder.server.common.TeamCoder;
import com.topcoder.server.common.TeamCoderHistory;
import com.topcoder.server.common.TeamContestRoom;
import com.topcoder.server.common.TestEvent;
import com.topcoder.server.common.User;
import com.topcoder.server.common.UserTestAttributes;
import com.topcoder.server.ejb.DBServices.DBServicesLocator;
import com.topcoder.server.ejb.TestServices.LongCoderHistory;
import com.topcoder.server.ejb.TestServices.LongContestServiceEventListener;
import com.topcoder.server.ejb.TestServices.LongContestServicesException;
import com.topcoder.server.ejb.TestServices.LongContestServicesLocator;
import com.topcoder.server.ejb.TestServices.LongRoundOverallScore;
import com.topcoder.server.ejb.TestServices.LongSubmissionData;
import com.topcoder.server.ejb.TestServices.LongTestQueueStatusItem;
import com.topcoder.server.ejb.TestServices.LongTestResult;
import com.topcoder.server.ejb.asyncservices.AsyncServiceClientInvoker;
import com.topcoder.server.farm.compiler.srm.SRMCompilationCurrentHandler;
import com.topcoder.server.farm.compiler.srm.SRMCompilerInvoker;
import com.topcoder.server.listener.ListenerMain;
import com.topcoder.server.services.CompileService;
import com.topcoder.server.services.CoreServices;
import com.topcoder.server.services.EventService;
import com.topcoder.server.services.TestService;
import com.topcoder.server.util.ArrayUtils;
import com.topcoder.shared.common.ServicesConstants;
import com.topcoder.shared.i18n.MessageProvider;
import com.topcoder.shared.language.BaseLanguage;
import com.topcoder.shared.language.Language;
import com.topcoder.shared.messaging.messages.LongCompileRequest;
import com.topcoder.shared.messaging.messages.LongCompileResponse;
import com.topcoder.shared.problem.DataType;
import com.topcoder.shared.problem.Problem;
import com.topcoder.shared.problem.ProblemComponent;
import com.topcoder.shared.problem.SimpleComponent;
import com.topcoder.shared.util.Formatters;
import com.topcoder.shared.util.SimpleResourceBundle;
import com.topcoder.shared.util.StageQueue;
import com.topcoder.shared.util.StringUtil;
import com.topcoder.shared.util.logging.Logger;

/**
 * Implements the processing logic required before calling into the services package.
 * <p>
 * Changes in version 1.0 (TopCoder Competition Engine - Event Support For Registration v1.0):
 * <ol>
 * <li>Updated {@link #registerInfo(Integer,int,int)} to support the event check during the registration process.</li>
 * <li>Updated {@link #canRegister(int, Round, Registration, StringBuilder)} to check the event_registration status.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.1 (Round Type Option Support For SRM Problem):
 * <ol>
 * <li>Update {@link #canRegister(int, Round, Registration, StringBuilder)}  method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.3 (PoC Assembly - TopCoder Competition Engine - Support Custom Output Checker):
 * <ol>
 *     <li>Update {@link #handleTestEvent(TestEvent)} to handle check answer response.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.4 (Module Assembly - TopCoder Competition Engine - Batch Test):
 * <ol>
 *      <li>Added {@link #checkExampleTest(Object, com.topcoder.shared.problem.TestCase[], DataType[], DataType)}
 *      method to check if arguments are from given test cases.</li>
 *      <li>Updated {@link #test(Integer, int, Object[], long)} to use {@link #checkExampleTest(Object,
 *      com.topcoder.shared.problem.TestCase[], DataType[], DataType)} method.</li>
 *      <li>Added {@link #batchTest(Integer, int, Object[], long)} method to handle batch testing request.</li>
 *      <li>Updated {@link #handleTestEvent(TestEvent)} method to handle batch test event.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.5 (TopCoder Competition Engine - Responses for Challenges and Challengers):
 * <ol>
 *      <li>Updated {@link #updateChallengeProblems} method to notify the coders in a room of a new challenge.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.6 (Python3 Support):
 * <ol>
 *      <li>Updated {@link #getPrettyCode(String, int)} method.</li>
 * </ol>
 * </p>
 *
 * @author savon_cn, gevak, dexy, gondzo, liuliquan
 * @version 1.6
 */
public final class Processor {
    /**
     * Category for logging.
     */
    private static Logger trace = Logger.getLogger(Processor.class);

    /**
     * Stores all the settings for running the processors.
     */
    private static String s_astylePath = "/usr/local/bin/astyle";

    /** The s_replay mode. */
    private static boolean s_replayMode = false;

    /** The s_high school competition. */
    private static boolean s_highSchoolCompetition = false;

    /** The practice round limit. */
    private static int practiceRoundLimit;
    /**
     * <p>
     * this is the no eligible to register notification.
     * </p>
     */
    private static final String NOT_ELIGIBLE_TO_REGISTER = "You are not eligible to participate in this competition.";

    /**
     * <p>
     * the eligible_ind = 1 in event_registration table to register legally.
     * </p>
     */
    private static final int ELIGIBLE_TO_REGISTER = 1;

    /**
     * Gets the practice round limit.
     *
     * @return the practice round limit
     */
    public static int getPracticeRoundLimit() {
        return practiceRoundLimit;
    }

    /**
     * SRMCompilerInvoker, responsible for scheduling compilations and
     * handling compilation responses.
     */
    private static SRMCompilerInvoker compiler;

    /** The spec app processor. */
    private static SpecAppProcessor specAppProcessor;

    /** The long contest event listener. */
    private static LongContestServiceEventListener longContestEventListener;

    /**
     * Sets the spec app processor.
     *
     * @param specAppProcessor the new spec app processor
     */
    static void setSpecAppProcessor(SpecAppProcessor specAppProcessor) {
        Processor.specAppProcessor = specAppProcessor;
    }


    /**
     * In replay mode.
     *
     * @return true, if successful
     */
    public static boolean inReplayMode() {
        return s_replayMode;
    }

    /**
     * Instantiates a new processor.
     */
    private Processor() {
    }

    /**
     * Gets the current time.
     *
     * @return the current time
     */
    private static long getCurrentTime() {
        return CoreServices.getCurrentDBTime();
    }

    /**
     * Initializes the Processor.  Loads in the settings from properties files.
     */
    static void start() {
        try {
            SimpleResourceBundle s_processorSettings = SimpleResourceBundle.getBundle("Processor");
            s_astylePath = s_processorSettings.getString("processor.astyle.path").trim();
            practiceRoundLimit = s_processorSettings.getInt("processor.practiceRoundLimit");

            // replay only if set to true;
            s_replayMode = s_processorSettings.getBoolean("processor.replay");
            s_highSchoolCompetition = s_processorSettings.getBoolean("processor.highschool");

            int autoLoadRoundId = s_processorSettings.getInt("processor.autoLoadRoundId");
            if (autoLoadRoundId >= 0) {
                loadContestRound(autoLoadRoundId);
            }
            loadAutoRounds();
            startLongContestListener();
        } catch (MissingResourceException mre) {
            trace.error("Failed to load Processor Settings", mre);
        }
    }


    /**
     * Load auto rounds.
     */
    private static void loadAutoRounds() {
        List roundIDsToLoadOnStartUp = CoreServices.getRoundIDsToLoadOnStartUp();
        trace.info("Loading auto loadable rounds: "+roundIDsToLoadOnStartUp);
        for (Iterator it = roundIDsToLoadOnStartUp.iterator(); it.hasNext();) {
            Number id = (Number) it.next();
            loadContestRound(id.longValue());
        }
        trace.info("Auto loadable rounds loaded");
    }


    /**
     * Start long contest listener.
     */
    private static void startLongContestListener() {
        trace.info("Starting long contest listener");
        longContestEventListener = new LongContestServiceEventListener();
        longContestEventListener.addEventHandler(new LongContestServiceEventListener.Handler() {
            public void submissionMade(int roundId, int coderId, int componentId, boolean example, int submissionNumber) {
                notifyLongSubmission(roundId, coderId, componentId, example);
            }

            public void overallScoreRecalculated(LongRoundOverallScore scores) {
                updateLongScores(scores);
            }

            public void testCompleted(int roundId, int coderId, int componentId, int submissionNumber, boolean example) {
                notifyTestCompleted(roundId, coderId, componentId, submissionNumber, example);
            }

            public void coderRegistered(int roundId, int coderId) {
                notifyRegistration(roundId, coderId);
            }

            public void componentOpened(int roundId, int coderId, int componentId, long openTime) {
                notifyComponentOpened(roundId, coderId, componentId, openTime);
            }

            public void saved(int roundId, int coderId, int componentId, String source, int language) {
                notifySaved(roundId, coderId, componentId, source, language);
            }

            public void systemTestCompleted(int roundId, int coder, int componentId, int submissionNumber) {
                //Nothing to do with this
            }

            public void roundSystemTestingCompleted(int intValue) {
                //Nothing to do with this

            }
        });
        trace.info("Long contest listener started");
    }

    /**
     * Stop.
     */
    static void stop() {
        if (longContestEventListener != null) {
            longContestEventListener.release();
            longContestEventListener = null;
        }
        try {
            getSRMCompiler().releaseCompiler();
        } catch (Exception e) {
            trace.error("Exception releasing SRM compiler",e);
        }
    }

    /**
     * Logout.
     *
     * @param connectionID the connection id
     * @param userID the user id
     */
    static void logout(Integer connectionID, int userID) {
        removeUserConnections(connectionID, userID);
    }

    /**
     * Toggle chat.
     *
     * @param userID the user id
     */
    static void toggleChat(int userID) {
        UserState state = UserState.getUserState(userID);
        state.setReceiveChat(!state.getReceiveChat());
        toggleUserChatConnection(userID, state.getReceiveChat());
    }

    /**
     * Sets the language.
     *
     * @param userID the user id
     * @param languageID the language id
     */
    static void setLanguage(int userID, int languageID) {
        CoreServices.setCoderLanguage(userID, languageID);
    }

    /**
     * Adds a user to s_roundConnections if they are registered; called only on login.
     *
     * @param user the user
     * @param connectionID the connection id
     */
    static void checkRoundParticipation(User user, Integer connectionID) {
        Round activeRounds[] = CoreServices.getAllActiveRounds();
        for (int i = 0; i < activeRounds.length; i++) {
            Round round = activeRounds[i];
            Registration reg = CoreServices.getRegistration(round.getRoundID());
            if (reg.isRegistered(user.getID())) {
                toggleConnection(connectionID, round.getRoundID(), true, s_roundConnections);
            }
        }
    }


    /**
     * Performs a search for the given userName for the given userID.
     *
     * @param connectionID the connection id
     * @param userID the user id
     * @param userName the user name
     */
    static void search(Integer connectionID, int userID, String userName) {
        trace.debug("search");
        if (trace.isDebugEnabled()) trace.debug("Searching for: " + userName);
        String title = "Search";
        String message = userName + " is not logged into the applet.";
        User searcher = CoreServices.getUser(userID, false);

        if (searcher!=null)
            info(searcher.getName() + " is searching for " + userName);

        if (trace.isDebugEnabled()) trace.debug("We got the searcher id " + searcher.getID());
        boolean sendSimpleMessage = true;
        if (CoreServices.isLoggedIn(userName, true)) {
            if (trace.isDebugEnabled()) trace.debug("is Logged in (ignoring case): " + userName);
            User user = CoreServices.getUser(userName, false);
            if (trace.isDebugEnabled()) trace.debug("Their ID is : " + user.getID());
            int userRoom = user.getRoomID();
            if (trace.isDebugEnabled()) trace.debug("USERROOM IS: "+userRoom);
            if (userRoom == searcher.getRoomID()) {
                message = user.getName() + " is in this room.";
            } else {
                sendSimpleMessage = false;
                String roomName;
                Room room = CoreServices.getRoom(userRoom, false);
                if (room.isAdminRoom() && !searcher.isLevelTwoAdmin()) {
                    ResponseProcessor.error(connectionID, "Only admins are allowed in this area.");
                    if (trace.isDebugEnabled()) trace.debug("User: " + userID + " attempted to enter an admin room.");
                    return;
                }
                if (ContestConstants.isPracticeRoomType(room.getType())) {
                    roomName = "Practice Room " + room.getName();
                } else {
                    roomName = "";
                    //get all active rounds may deadlock during room assignments
                    //we want to avoid this, so we're going to do some magic

                    if(room instanceof BaseCodingRoom) {
                        BaseCodingRoom ct = (BaseCodingRoom)room;
                        Round round = CoreServices.getContestRound(ct.getRoundID(), false);
                        if (!canMoveIntoRoomOfRound(searcher, round)) {
                            ResponseProcessor.error(connectionID, "User is participating in a private contest, only invited users are allowed to enter those rooms.");
                            if (trace.isDebugEnabled()) trace.debug("User: " + userID + " attempted to enter a private room.");
                            return;
                        }
                        roomName = round.getDisplayName() + " -> ";
                    }
                    roomName = roomName +  room.getName();
                }
                ResponseProcessor.search(connectionID, user.getName(), roomName, room.getType(), room.getRoomID());
            }
        }
        if (sendSimpleMessage) {
            ResponseProcessor.process(connectionID, ResponseProcessor.simpleMessage(message, title));
        }
    }


    /**
     * Can move into room of round.
     *
     * @param user the user
     * @param round the round
     * @return true, if successful
     */
    private static boolean canMoveIntoRoomOfRound(User user, Round round) {
        return !round.getRoundProperties().isVisibleOnlyForRegisteredUsers()
                || user.isLevelTwoAdmin()
                || CoreServices.getRegistration(round.getRoundID()).isRegistered(user.getID());
    }


    /**
     * Search room.
     *
     * @param connectionID the connection id
     * @param userID the user id
     * @param userName the user name
     */
    static void searchRoom(Integer connectionID, int userID, String userName) {
        trace.debug("search");
        if (trace.isDebugEnabled()) trace.debug("Searching for: " + userName);
        String title = "Assigned Room Search";
        User searcher = CoreServices.getUser(userID, false);

        if (searcher!=null)
            info(searcher.getName() + " is searching for " + userName);

        if (trace.isDebugEnabled()) trace.debug("We got the searcher id " + searcher.getID());

        User user = CoreServices.getUser(userName, false);
        if(user != null)
        {
            if (trace.isDebugEnabled()) trace.debug("Their ID is : " + user.getID());
            String roomName;

            roomName = "Assigned Rooms:";
            Round[] rounds = CoreServices.getAllActiveRounds();
            for(int i = 0; i < rounds.length; i++)
            {
                Iterator it = rounds[i].getAllRoomIDs();
                while(it.hasNext())
                {
                    Room rm = CoreServices.getRoomFromCache(((Integer) it.next()).intValue());
                    if(rm != null && !rm.isAdminRoom() && !ContestConstants.isPracticeRoomType(rm.getType()))
                    {
                        if(rm instanceof BaseCodingRoom)
                        {
                            BaseCodingRoom cr = (BaseCodingRoom)rm;
                            if(cr.isUserAssigned(user.getID()))
                            {
                                roomName = roomName + "\n" + rounds[i].getDisplayName() + " -> " + cr.getName();
                            }
                        }
                        else if(rm instanceof TeamContestRoom)
                        {
                            TeamContestRoom tcr = (TeamContestRoom)rm;
                            if(tcr.isUserAssigned(user.getID()))
                            {
                                roomName = roomName + "\n" + rounds[i].getDisplayName() + " -> " + tcr.getName();
                            }
                        }
                    }
                }
            }
            ResponseProcessor.process(connectionID, ResponseProcessor.simpleMessage(roomName, title));
        }
        else
        {
            ResponseProcessor.process(connectionID, ResponseProcessor.simpleMessage("Invalid Handle", title));
        }

    }

    /**
     * Maximum length of a chat message.
     */
    private static final int MAX_CHAT_LENGTH = 500;

    /**
     * Checks if is valid action.
     *
     * @param message the message
     * @return true, if is valid action
     */
    private static boolean isValidAction(String message) {
        String userAction;
        final String[] validActions = {"admin", "find", "whois", "search", "me", "msg", "whisper", "moderator", "room", "rooms"};
        if (message.startsWith("/")) {
            userAction = message.substring(message.indexOf("/"), message.length() - 1);
            for (int i = 0; i < validActions.length; i++) {
                String word = validActions[i];
                if (userAction.indexOf(word) >= 0) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    /**
     * Longest matching logged in handle.
     *
     * @param text the text
     * @return the string
     */
    private static String longestMatchingLoggedInHandle(String text) {
        while (!CoreServices.isLoggedIn(text) && text.indexOf(' ') != -1) {
            text = text.substring(0, text.lastIndexOf(' ')).trim();
        }
        return text;
    }

    /**
     * Performs a chat for the given userID and message.
     *
     * @param connectionID the connection id
     * @param userID the user id
     * @param message the message
     * @param scope the scope
     */
    static void chat(Integer connectionID, int userID, String message, int scope) {
        if (!isValidAction(message)) {
            //they dont have proper formatting, alert them
            BaseResponse response = new PopUpGenericResponse(
                                                             "Error using /<action> command",
                                                             "Unknown User Action Command: " + message.substring(message.indexOf("/"), message.length() - 1),
                                                             ContestConstants.GENERIC,
                                                             ContestConstants.LABEL
                                                             );
            ResponseProcessor.process(connectionID, response);
            //alert admins that this user wanted to say something with /me...could be duplicating if taboo words are here
            return;
        }
        User user = CoreServices.getUser(userID, false);
        RequestProcessor.monitorChat(user.getRoomID(), user.getName(), message);

        // dpecora - disable admin chat commands
        /*
          if (user.isLevelOneAdmin() && AdminCommands.processCommand(user, connectionID, message)) {
          // Check is this is an admin command and don't process as a regular chat if so.
          return;
          }
        */

        if (user.getRoomID() == ContestConstants.INVALID_ROOM) {
            trace.error("User: " + userID + " chatting from invalid room");
            return;
        }

        if (message.length() > MAX_CHAT_LENGTH) {
            message = message.substring(0, MAX_CHAT_LENGTH);
        }

        int roundID = 0;

        Room room = CoreServices.getRoom(user.getRoomID(), false);
        if (room != null) {
            if (room instanceof BaseCodingRoom) {
                roundID = ((BaseCodingRoom) room).getRoundID();
            } else if (room.getType() == ContestConstants.LOBBY_ROOM) {
                roundID = CoreServices.LOBBY_ROUND_ID;
            } else {
                throw new IllegalStateException("Don't recognize room type: " + room);
            }
        } else {
            trace.error("Null room while user: " + userID + " chatting RoomID: " + user.getRoomID());
        }

        if (user.isLevelTwoAdmin() && user.getRoomID() == ContestConstants.ADMIN_LOBBY_ROOM_ID && message.indexOf(":") > 0) {
            String toUser = message.substring(0, message.indexOf(":")).trim();
            if (trace.isDebugEnabled()) trace.debug("Processing admin lobby response to user " + toUser);

            //The Admin Room is a magic place.  Messages get amazingly redirected to the proper room.
            //These messages will now come in private, so no one else in the room can gleam info
            if (CoreServices.isLoggedIn(toUser)) {
                User targetUser = CoreServices.getUser(toUser);

                //find the room they are in
                Room targetRoom = CoreServices.getRoom(targetUser.getRoomID(), false);

                if (targetRoom != null && targetRoom.getRoomID() != ContestConstants.ADMIN_LOBBY_ROOM_ID) {
                    if (targetRoom instanceof BaseCodingRoom) {
                        int targetRoundID = ((BaseCodingRoom) targetRoom).getRoundID();
                        //figure out if the round is in coding or challenge phases
                        Round round = CoreServices.getContestRound(targetRoundID);

                        if( round.inCoding() || round.inChallenge() ) {
                            //private
                            EventService.sendUserMessage(user.getID(), targetUser.getRoomID(), roundID,
                                                         targetUser.getID(), ContestConstants.USER_CHAT, "(From Admin) " + message, user.getName() + ">", user.getRating(targetRoom.getRatingType()).getRating());
                            EventService.sendAdminMessage(ContestConstants.USER_CHAT, message, user.getID(), ContestConstants.ADMIN_LOBBY_ROOM_ID,
                                                          user.getName() + ">", user.getRating(targetRoom.getRatingType()).getRating());
                            return;
                        } else {
                            //send it to the room
                            EventService.sendRoomUserMessage(user.getID(), targetUser.getRoomID(), roundID, ContestConstants.USER_CHAT, message,
                                                             user.getName() + ">", user.getRating(targetRoom.getRatingType()).getRating());
                            if(user.getRoomID() != targetUser.getRoomID()) {
                                EventService.sendRoomUserMessage(user.getID(), user.getRoomID(), roundID, ContestConstants.USER_CHAT, message,
                                                                 user.getName() + ">", user.getRating(room.getRatingType()).getRating());
                            }
                            return;
                        }

                    } else if (room.getType() == ContestConstants.LOBBY_ROOM) {
                        //send it to the room
                        EventService.sendRoomUserMessage(user.getID(), targetUser.getRoomID(), roundID, ContestConstants.USER_CHAT, message,
                                                         user.getName() + ">", user.getRating(targetRoom.getRatingType()).getRating());
                    } else {
                        throw new IllegalStateException("Don't recognize room type: " + room);
                    }
                } else {
                    if (trace.isDebugEnabled()) trace.debug("Null room / admin room while user: " + userID + " chatting RoomID: " + user.getRoomID());
                }

            } else {
                if (trace.isDebugEnabled()) trace.debug("Target user was not logged in");
                //send to admin "invalid target"
                //ResponseProcessor.chat(connectionID, userID, toUser + " is not logged in.\n", scope);
                //return;
            }
        }

        int chatType = ContestConstants.USER_CHAT;

        /* SYHAAS 2002-05-13 added to check if the user is allowed to speak and if the user is in a moderated chat */
        List allowedSpeakers = CoreServices.getAllowedSpeakers(roundID);
        List activeModeratedChats = CoreServices.getActiveModeratedChatSessions();
        boolean isSpeaker, inModeratedChat = false;
        isSpeaker = allowedSpeakers.contains(new Integer(userID));

        Iterator iter = activeModeratedChats.iterator();//this returns ContestRoom's
        while (iter.hasNext()) {
            if (((Integer) iter.next()).intValue() == roundID) {
                inModeratedChat = true;
                break;
            }
        }

        //only allow this IF NOT in a moderated chat room, so if user is in mc, then it is not allowed
        if (message.indexOf("/me") != -1) {
            if (inModeratedChat) {
                //they dont have proper formatting, alert them
                BaseResponse response = new PopUpGenericResponse(
                                                                 "Error using /me",
                                                                 "This is not allowed in this room, try /moderator [your question here]",
                                                                 ContestConstants.GENERIC,
                                                                 ContestConstants.LABEL
                                                                 );
                ResponseProcessor.process(connectionID, response);
                //alert admins that this user wanted to say something with /me...could be duplicating if taboo words are here
                String me = "*" + user.getName() + " tried /me in Moderated Chat " + message;
                EventService.sendAdminMessage(ContestConstants.IRC_CHAT, me);
                return;
            } else if (message.indexOf("/me") == 0) {
                //send it
                int index = message.indexOf("/me");
                message = message.substring(0, index) + user.getName() + message.substring(index + 3);

                EventService.sendRoomUserMessage(user.getID(), user.getRoomID(), roundID, ContestConstants.IRC_CHAT, "**" + message);
                return;
            }
        }
        if (message.startsWith("/search") || message.startsWith("/find")) {
            trace.debug("coderInfo from chat");
            if (message.trim().indexOf(" ") == -1) {
                //they dont have proper formatting, alert them
                BaseResponse response = new PopUpGenericResponse(
                                                                 "Error using /search or /find",
                                                                 "Error using /search or /find, please specify a valid handle",
                                                                 ContestConstants.GENERIC,
                                                                 ContestConstants.LABEL
                                                                 );
                ResponseProcessor.process(connectionID, response);
                return;
            }
            message = message.substring(message.indexOf(" "));
            String userName = message.substring(1, message.length() - 1);
            if (userName != null) {
                Processor.search(connectionID, userID, userName.trim());
                return;
            } else {
                trace.error("Null user name requested in coderInfo from chat");
                return;
            }
        }
        if (message.startsWith("/room") || message.startsWith("/rooms")) {
            if (message.trim().indexOf(" ") == -1) {
                //they dont have proper formatting, alert them
                BaseResponse response = new PopUpGenericResponse(
                                                                 "Error using /room",
                                                                 "Error using /room, please specify a valid handle",
                                                                 ContestConstants.GENERIC,
                                                                 ContestConstants.LABEL
                                                                 );
                ResponseProcessor.process(connectionID, response);
                return;
            }
            message = message.substring(message.indexOf(" "));
            String userName = message.substring(1, message.length() - 1);
            if (userName != null) {
                Processor.searchRoom(connectionID, userID, userName.trim());
                return;
            } else {
                trace.error("Null user name requested in coderInfo from chat");
                return;
            }
        }
        if (message.startsWith("/whois")) {
            trace.debug("coderInfo from chat");
            if (message.trim().indexOf(" ") == -1) {
                //they dont have proper formatting, alert them
                BaseResponse response = new PopUpGenericResponse(
                                                                 "Error using /whois",
                                                                 "Error using /whois, please specify a valid handle",
                                                                 ContestConstants.GENERIC,
                                                                 ContestConstants.LABEL
                                                                 );
                ResponseProcessor.process(connectionID, response);
                return;
            }
            message = message.substring(message.indexOf(" "));
            String userName = message.substring(1, message.length() - 1);
            if (userName != null) {
                String coderInfo = CoreServices.getCoderInfo(userName.trim(), ContestConstants.SINGLE_USER);
                if (trace.isDebugEnabled()) trace.debug("USRER:" + userName);
                ResponseProcessor.coderInfo(connectionID, coderInfo);
                return;
            } else {
                trace.error("Null user name requested in coderInfo from chat");
                return;
            }
        }

        if ((message.startsWith("/msg") || message.startsWith("/whisper")) && message.lastIndexOf(" ") - message.indexOf(" ") > 1 &&
            message.indexOf(" ", message.indexOf(" ") + 1) != message.length() - 1) {
            message = message.substring(message.indexOf(" ") + 1);
            if (trace.isDebugEnabled()) trace.debug("message is: " + message);

            String toUser = longestMatchingLoggedInHandle(message.trim());
            //String toUser = message.substring(0, message.indexOf(" "));
            if (trace.isDebugEnabled()) trace.debug("To user is: " + toUser);
            message = message.substring(toUser.length());
            if (trace.isDebugEnabled()) trace.debug("message is: " + message);

            //added admin and admins target that will send the whisper to all admins
            if(toUser.equalsIgnoreCase("admin") || toUser.equalsIgnoreCase("admins")) {
                ResponseProcessor.chat(connectionID, userID, "You whisper to " + toUser + ": " + message, scope);
                // Send the message to admins as well ;-)
                String whisper = "**" + user.getName() + " whispers to " + toUser + ": " + message;
                EventService.sendAdminMessage(ContestConstants.WHISPER_TO_YOU_CHAT, whisper, user.getID());
                return;
            }

            if (CoreServices.isLoggedIn(toUser)) {
                User targetUser = CoreServices.getUser(toUser);

                /* SYHAAS 2002-05-09 added disallow IMs to allowed speakers, just send msg to user */
                if (inModeratedChat && allowedSpeakers.contains(new Integer(targetUser.getID()))) {
                    //they are not allowed to IM a speaker
                    BaseResponse response = new PopUpGenericResponse(
                                                                     "Whisper Error",
                                                                     "The Speaker does not accept whispers, try: /moderator [your question here]",
                                                                     ContestConstants.GENERIC,
                                                                     ContestConstants.LABEL
                                                                     );
                    ResponseProcessor.process(connectionID, response);
                    //alert admins that this user wanted to whisper to speaker..haha
                    String whisper = "**" + user.getName() + " tried to whisper to speaker " + toUser + ": " + message;
                    EventService.sendAdminMessage(ContestConstants.IRC_CHAT, whisper);
                    return;
                }

                String targetMessage = user.getName() + " whispers to you: " + message;
                EventService.sendUserMessage(user.getID(), targetUser.getRoomID(), roundID, targetUser.getID(), ContestConstants.WHISPER_TO_YOU_CHAT, targetMessage);
                ResponseProcessor.chat(connectionID, userID, "You whisper to " + toUser + ": " + message, scope);
                // Send the message to admins as well ;-)
                String whisper = "**" + user.getName() + " whispers to " + toUser + ": " + message;
                EventService.sendAdminMessage(ContestConstants.IRC_CHAT, whisper, user.getID());
            } else {
                ResponseProcessor.chat(connectionID, userID, toUser + " is not logged in.\n", scope);
            }
            return;
        }

        //test if user is a speaker and/or in a moderated chat room
        if (trace.isDebugEnabled()) trace.debug("isSpeaker=" + isSpeaker + " inModeratedChat=" + inModeratedChat);
        if (inModeratedChat) {
            if (isSpeaker) {
                //then fall through becuz they are allowed to just chat
                chatType = ContestConstants.MODERATED_CHAT_SPEAKER_CHAT;
            } else {
                //other wise we are a normal user
                //make sure the message starts with /moderator
                if (message.startsWith("/moderator ") && ((message.lastIndexOf(" ") - message.indexOf(" ")) >= 0) &&
                    (message.indexOf(" ") + 1 < message.length())) {
                    message = message.substring(11);
                    //send to admin tool
                    RequestProcessor.monitorQuestion(user.getRoomID(), user.getName(), message);
                    BaseResponse response = new UpdateChatResponse(
                                                                   ContestConstants.SYSTEM_CHAT,
                                                                   "System> Your question has been submitted.\n",
                                                                   -1,
                                                                   -1,
                                                                   ContestConstants.GLOBAL_CHAT_SCOPE
                                                                   );
                    ResponseProcessor.process(connectionID, response);
                    return;
                } else {
                    //they dont have proper formatting, alert them
                    BaseResponse response = new PopUpGenericResponse(
                                                                     "Invalid Question Format",
                                                                     "The correct format for posting a question is: /moderator [your question here]",
                                                                     ContestConstants.GENERIC,
                                                                     ContestConstants.LABEL
                                                                     );
                    ResponseProcessor.process(connectionID, response);
                    return;
                }
            }
        }

        if (message.startsWith("/moderator ")) {
            // Ignore the message.

            ResponseProcessor.process(connectionID, new PopUpGenericResponse(
                                                                             "Incorrect Usage",
                                                                             "/moderator is only used in moderated chat rooms",
                                                                             ContestConstants.GENERIC,
                                                                             ContestConstants.LABEL)
                                      );
            return;
        }

        String prefix = user.getName() + ">";
        int adminLobby = ContestConstants.ADMIN_LOBBY_ROOM_ID;
        boolean sendToAdmins = message.trim().length() > 7 && user.getRoomID() != ContestConstants.ADMIN_LOBBY_ROOM_ID
            && (message.trim().substring(0, 7).equalsIgnoreCase("admins:") || message.trim().substring(0, 6).equalsIgnoreCase("admin:"));

        //more magic here, figure out if the target is an admin, and if so, send it to them privately
        if (message.indexOf(":") > 0) {
            String toUser = message.substring(0, message.indexOf(":")).trim();
            if (CoreServices.isLoggedIn(toUser)) {
                User targetUser = CoreServices.getUser(toUser);
                if(targetUser.isLevelOneAdmin()) {
                    sendToAdmins = true;
                    adminLobby = targetUser.getRoomID();
                }
            }
        }

        //make sure admins: is used from global scope.
        if (scope == ContestConstants.TEAM_CHAT_SCOPE && sendToAdmins) {
            ResponseProcessor.process(connectionID, new PopUpGenericResponse(
                                                                             "Incorrect Usage",
                                                                             "admins: cannot be used from team chat tab.",
                                                                             ContestConstants.GENERIC,
                                                                             ContestConstants.LABEL)
                                      );
            return;
        }

        if (scope == ContestConstants.GLOBAL_CHAT_SCOPE) {
            if(sendToAdmins) {
                //figure out where the message is coming from.  if it's an active contest make it private
                Room targetRoom = CoreServices.getRoom(user.getRoomID(), false);
                if (targetRoom != null) {
                    int rating = user.getRating(targetRoom.getRatingType()).getRating();
                    if (targetRoom instanceof BaseCodingRoom) {
                        int targetRoundID = ((BaseCodingRoom) targetRoom).getRoundID();
                        //figure out if the round is in coding or challenge phases
                        Round round = CoreServices.getContestRound(targetRoundID);

                        if( round.inCoding() || round.inChallenge() ) {
                            //private
                            EventService.sendUserMessage(user.getID(), user.getRoomID(), roundID, user.getID(), ContestConstants.USER_CHAT, "(To Admin) " + message, prefix, rating);
                            EventService.sendAdminMessage(ContestConstants.USER_CHAT, message, user.getID(), adminLobby, prefix, rating);

                        } else {
                            //send it to the room
                            EventService.sendRoomUserMessage(user.getID(), user.getRoomID(), roundID, ContestConstants.USER_CHAT,
                                                             message, prefix, rating);
                            EventService.sendRoomUserMessage(user.getID(), adminLobby, roundID, ContestConstants.USER_CHAT,
                                                             message, prefix, rating);

                        }

                    } else if (room.getType() == ContestConstants.LOBBY_ROOM) {
                        //send it to the room
                        EventService.sendRoomUserMessage(user.getID(), user.getRoomID(), roundID, ContestConstants.USER_CHAT,
                                                         message, prefix, rating);
                        if(user.getRoomID() != adminLobby)
                            EventService.sendRoomUserMessage(user.getID(), adminLobby, roundID, ContestConstants.USER_CHAT,
                                                             message, prefix, rating);

                    } else {
                        throw new IllegalStateException("Don't recognize room type: " + room);
                    }
                } else {
                    trace.error("Null room while user: " + userID + " chatting RoomID: " + user.getRoomID());
                }
            } else {
                EventService.sendRoomUserMessage(user.getID(), user.getRoomID(), roundID, chatType, message, prefix, user.getRating(room.getRatingType()).getRating());
            }
        } else {
            if (user.getTeamID() == User.NO_TEAM) {
                ResponseProcessor.process(connectionID, new PopUpGenericResponse(
                                                                                 "Incorrect Usage",
                                                                                 "You are not assigned to a team. Chat in the global tab.",
                                                                                 ContestConstants.GENERIC,
                                                                                 ContestConstants.LABEL)
                                          );
                return;
            }
            if (trace.isDebugEnabled()) trace.debug("User.getTeamID() = " + user.getTeamID());
            EventService.sendRoomUserMessage(user.getID(), user.getRoomID(), roundID, chatType, message, prefix, user.getRating(room.getRatingType()).getRating(), user.getTeamID());
        }
    }

    /**
     * Can get component.
     *
     * @param userID the user id
     * @return the results
     */
    static Results canGetComponent(int userID) {
        User user = CoreServices.getUser(userID, false);
        BaseCodingRoom room = (BaseCodingRoom) CoreServices.getRoom(user.getRoomID(), false);
        if (room == null) {
            trace.error("User: " + userID + " getProblem in null room: " + user.getRoomID());
            return new Results(false, "Server error attempting to get the component");
        }
        Round contestRound = CoreServices.getContestRound(room.getRoundID());
        int activePhase = contestRound.getPhase();
        if (!user.isLevelTwoAdmin() && (activePhase < ContestConstants.CODING_PHASE || activePhase > ContestConstants.CONTEST_COMPLETE_PHASE)) {
            return new Results(false, "You cannot view the problems until the coding phase is active.");
        }
        if (!room.isUserAssigned(userID)) {
            return new Results(false, "You can only view problems if you are assigned to this room.");
        }
        UserState userState = UserState.getUserState(userID);
        if (userState.getProblemState().getState() != UserState.ProblemState.CLOSE) {
            trace.info("User " + userID + " tries to get another problem component when the state is " + userState.getProblemState().getState());
            return new Results(false, "You can only view one problem/code at a time.");
        }
        return new Results(true, "");
    }

    /**
     * Can get problem.
     *
     * @param userID the user id
     * @param roundId the round id
     * @param problemID the problem id
     * @return the results
     */
    static Results canGetProblem(int userID, int roundId, int problemID) {
        if (!CoreServices.isRoundActive(roundId) && !CoreServices.isPracticeRoundActive(roundId)) {
            return new Results(false, "You cannot view the problems for inactive rounds.");
        }
        User user = CoreServices.getUser(userID, false);
        Round contestRound = CoreServices.getContestRound(roundId);
        int activePhase = contestRound.getPhase();
        if (!user.isLevelTwoAdmin() && (activePhase < ContestConstants.CODING_PHASE || activePhase > ContestConstants.CONTEST_COMPLETE_PHASE)) {
            return new Results(false, "You cannot view the problems until the coding phase is active.");
        }
        if (contestRound.isTeamRound()) {
            BaseCodingRoom room = (BaseCodingRoom) CoreServices.getRoom(user.getRoomID(), false);
            if (room == null || room.getRoundID() != roundId) {
                trace.error("User: " + userID + " getProblem in null room: " + user.getRoomID());
                return new Results(false, "Server error attempting to get the problem");
            }
            if (!room.isUserAssigned(userID)) {
                return new Results(false, "You can only view problems if you are assigned to this room.");
            }
        } else if (contestRound.isLongContestRound()) {
            Problem problem = CoreServices.getProblem(problemID);
            ProblemComponent component = problem.getComponent(0);
            LongContestRound round = (LongContestRound) contestRound;
            BaseCodingRoom room = CoreServices.getContestRoom(round.getMainRoomId().intValue(), false);
            if (!round.getDivisionComponents(room.getDivisionID()).contains(new Integer(component.getComponentId()))) {
                trace.error("CHEATER: " + userID + " trying to get a problem not assigned for the round");
                return new Results(false, "Server error attempting to get the problem");
            }
        } else {
            trace.error("CHEATER: " + userID + " trying to get problem statement without opening the problem in algorithm round");
            return new Results(false, "Server error attempting to get the problem");
        }
        UserState userState = UserState.getUserState(userID);
        if (userState.getProblemState().getState() != UserState.ProblemState.CLOSE) {
            trace.info("User " + userID + " tries to get another problem when the state is " + userState.getProblemState().getState());
            return new Results(false, "You can only view one problem/code at a time.");
        }
        return new Results(true, "");
    }


    /**
     * Saves the source for the given user as their currently opened problem.
     *
     * @param connectionID the connection id
     * @param userID the user id
     * @param source the source
     * @param componentID the component id
     * @param languageID the language id
     */
    static void save(Integer connectionID, int userID, String source, long componentID, int languageID) {
        UserState userState = UserState.getUserState(userID);
        if (userState.isBusy()) {
            info("Throwing out a save request from " + userID + " on component " + componentID + " because server is busy");
            return;
        }
        User user = CoreServices.getUser(userID, false);
        int roomID = user.getRoomID();
        if (roomID == ContestConstants.INVALID_ROOM) {
            trace.error("User: " + userID + " called save in invalid room.  Connection: " + connectionID);
            return;
        }
        Round round = null;
        BaseCodingRoom room = null;
        boolean updateCache = false;
        Room baseRoom = CoreServices.getRoom(roomID, true);
        try {
            if (baseRoom != null && baseRoom instanceof BaseCodingRoom) {
                room = (BaseCodingRoom) baseRoom;
                Coder coder = room.getCoder(user.getID());
                BaseCoderComponent coderComponent = coder.getComponent(componentID);
                if (!user.isLevelTwoAdmin() && !ContestConstants.ACCEPT_MULTIPLE_SUBMISSIONS && coderComponent.isSubmitted()) {
                    ResponseProcessor.error(connectionID, "You are not allowed to save after submitting.");
                    return;
                }
                round = CoreServices.getContestRound(room.getRoundID());
                int activePhase = round.getPhase();
                if (!user.isLevelTwoAdmin() && activePhase != ContestConstants.CODING_PHASE) {
                    ResponseProcessor.error(connectionID, "You cannot save code unless the coding phase is active.");
                    return;
                }
                if (!user.isLevelTwoAdmin() && hasCodingTimeExpiredForCoder(round, coder)) {
                    userState.setCanTestOrSubmit(false);
                    trace.info("save(): Coding time expired for user : "+user.getID());
                    ResponseProcessor.error(connectionID, "You cannot save after your time has expired.");
                    return;
                }
                userState.setCurrentBusyTime();
                userState.setCanTestOrSubmit(false);
                coderComponent.setProgramText(source);
                coderComponent.setLanguage(languageID);

                updateCache = true;

                //notify the spectator
                if(!ContestConstants.isPracticeRoomType(room.getType()))
                    specAppProcessor.savingComponent(round, room, coder, coderComponent, source, languageID);
            }
        } finally {
            if (updateCache) {
                CoreServices.saveToCache(Room.getCacheKey(roomID), baseRoom);
            } else {
                CoreServices.releaseLock(Room.getCacheKey(roomID));
            }
        }
        Results res = null;
        if (round != null && round.isLongContestRound()) {
            try {
                LongContestServicesLocator.getService().save(room.getContestID(), room.getRoundID(), (int) componentID, userID, source, languageID);
                res = new Results(true, "Code saved successfully.");
            } catch (LongContestServicesException e) {
                res = new Results(false, MessageProvider.getText(e.getLocalizableMessage()));
            } catch (Exception e) {
                trace.error("Exception while saving long component", e);
                res = new Results(false, "Failed to save.");
            }
        } else {
            res = CompileService.saveComponent(room.getContestID(), room.getRoundID(), componentID, userID, source, languageID);
        }

        if (res.isSuccess()) {
            PopUpGenericResponse response = ResponseProcessor.simpleMessage(res.getMsg(), "Save Results");
            ResponseProcessor.process(connectionID, response);
        } else {
            ResponseProcessor.error(connectionID, res.getMsg());
        }
        userState.resetBusyTime();
    }

    /**
     * Can move.
     *
     * @param userID the user id
     * @param roomType the room type
     * @param roomID the room id
     * @return the results
     */
    static Results canMove(int userID, int roomType, int roomID) {
        User user = CoreServices.getUser(userID, false);
        if (user == null) {
            trace.error("CoreServices return null user.");
            return new Results(false, "Unknown error(null user)");
        }

        Room room = CoreServices.getRoom(roomID, false);
        if (room == null) {
            trace.error("Failed to load target room: " + roomID);
            return new Results(false, "Server Error: Failed to load destination room");
        }

        if(room.isAdminRoom() && !user.isLevelTwoAdmin()) {
            trace.error("User trying to move to admin room: " + userID);
            return new Results(false, "Server Error: Unathorized to move to admin room");
        }

        switch (roomType) {
        case ContestConstants.CONTEST_ROOM:
            // For a contest_type the roomID passed in is actually the ContestID
            Round contest = CoreServices.getContestRound(roomID);
            if (contest == null) {
                trace.error("Failed to get contest for move to contest: " + roomID);
                return new Results(false, "Server error: Failed to get contest.");
            }
            if (user.isLevelTwoAdmin()) {
            } else {
                if (!contest.areRoomsAssigned()) {
                    trace.error("Null assigned users map on contest move.");
                    return new Results(false, "Unknown error(null assigned users map)");
                }
                if (!canMoveIntoRoomOfRound(user, contest)) {
                    return new Results(false, "You are not allowed to enter this room");
                }
                Integer roomIDInteger = contest.getAssignedRoom(userID);
                if (roomIDInteger != null) {
                    roomID = roomIDInteger.intValue();
                    if (trace.isDebugEnabled()) trace.debug("User: " + user.getName() + " got assigned room: " + roomID);
                } else {
                    Integer nonAdminRoom = contest.getNonAdminRoom();
                    if (nonAdminRoom==null) {
                        trace.error("Unregistered user couldn't find non-admin room.");
                        //roomType = ContestConstants.LOBBY_ROOM;
                        roomID = CoreServices.getFirstAvailableLobbyID();
                        return new Results(false, "Unknown Error(unregistered user couldn't find non-admin room)");
                    } else {
                        roomID = nonAdminRoom.intValue();
                    }
                }
            }
            break;
        case ContestConstants.CODER_ROOM:
            BaseCodingRoom codingRoom = (BaseCodingRoom) room;
            Round round = CoreServices.getContestRound(codingRoom.getRoundID());
            if (!canMoveIntoRoomOfRound(user, round)) {
                return new Results(false, "You are not allowed to enter this room");
            }
            break;
        default:
            break;
        }
        if (room.getOccupancy() >= room.getCapacity() && room.getCapacity() > 0) {
            info("Room " + roomID + " is full with capacity = " + room.getCapacity());
            return new Results(false, "That room is full");
        }

        return new Results(true, "");
    }

    /**
     * Enter round.
     *
     * @param connectionID the connection id
     * @param userID the user id
     * @param roundID the round id
     */
    static void enterRound(Integer connectionID, int userID, int roundID) {
        info(userID + " entering round " + roundID);
        User user = CoreServices.getUser(userID, false);
        if (user == null) {
            trace.error("CoreServices return null user.");
            return;
        }

        Round contest = CoreServices.getContestRound(roundID);
        if (contest == null) {
            trace.error("Failed to get contest for move to contest: " + roundID);
            ResponseProcessor.error(connectionID, "Server error: Failed to get contest.");
            return;
        }

        int roomID = -1;
        if (user.isLevelTwoAdmin()) {
            Iterator roomIDs = contest.getAllRoomIDs();
            boolean gotNonAdminRoom = false;
            // Find the first non-admin room for the contest.
            while (roomIDs.hasNext() && !gotNonAdminRoom) {
                roomID = ((Integer) roomIDs.next()).intValue();
                Room nextRoom = CoreServices.getRoom(roomID, false);
                if (!nextRoom.isAdminRoom()) {
                    if (trace.isDebugEnabled()) trace.debug("Got nonadmin room for admin move to contest: " + roomID);
                }
                gotNonAdminRoom = !nextRoom.isAdminRoom();
            }
            // If there were no-non admin rooms, get the admin room for the round.
            if (!gotNonAdminRoom) {
                roomID = CoreServices.getAdminRoomIDForRound(contest.getRoundID());
                if (trace.isDebugEnabled()) trace.debug("Admin Room: " + roomID);
            }
        } else {
            if (!contest.areRoomsAssigned()) {
                trace.error("Null assigned users map on contest move.");
                return;
            }
            Integer roomIDInteger = contest.getAssignedRoom(userID);
            if (roomIDInteger != null) {
                roomID = roomIDInteger.intValue();
                if (trace.isDebugEnabled()) trace.debug("User: " + user.getName() + " got assigned room: " + roomID);
            } else {
                Integer nonAdminRoom = contest.getNonAdminRoom();
                if (nonAdminRoom == null) {
                    trace.error("Unregistered user couldn't find non-admin room.");
                    roomID = CoreServices.getFirstAvailableLobbyID();
                    return;
                } else {
                    roomID = nonAdminRoom.intValue();
                }
            }
        }

        Room room = CoreServices.getRoom(roomID, false);
        if (room == null) {
            ResponseProcessor.error(connectionID, "Server Error: Failed to load destination room.");
            trace.error("Failed to load target room: " + roomID);
            return;
        }

        if (trace.isDebugEnabled()) trace.debug("Moving " + userID + " to room " + roomID);

        if (room.getOccupancy() >= room.getCapacity() && room.getCapacity() > 0) {
            trace.error("Room " + roomID + " is full with capacity = " + room.getCapacity());
            ResponseProcessor.error(connectionID, "That room is full");
            return;
        }

        // Send the exit message to the room.
        if (trace.isDebugEnabled()) trace.debug("Sending exit message on RoomID = " + user.getRoomID());
        if (user.getRoomID() >= 0) {
            toggleUserConnection(userID, user.getRoomID(), false);
            ArrayList responses = ResponseProcessor.leaveRoom(user, false);
            if (trace.isDebugEnabled()) trace.debug("Responses size = " + responses.size());
            if (responses.size() > 0) {
                EventService.sendResponseToRoom(user.getRoomID(), responses);
            }
        }
        toggleUserConnection(userID, roomID, true);
        // Perform actual move in server state.
        CoreServices.enter(userID, roomID);
    }

    /**
     * Performs the move of the user to the desired room.
     *
     * @param connectionID the connection id
     * @param userID the user id
     * @param roomType the room type
     * @param roomID the room id
     */
    static void move(Integer connectionID, int userID, int roomType, int roomID) {
        Results canMove = canMove(userID, roomType, roomID);
        if (!canMove.isSuccess()) {
            ResponseProcessor.error(connectionID, canMove.getMsg());
            return;
        }

        User user = CoreServices.getUser(userID, false);
        Room room = CoreServices.getRoom(roomID, false);
        info("Moving " + userID + " to " + roomID);

        if (room.getType() == ContestConstants.TEAM_PRACTICE_CODER_ROOM && !user.isOnTeam()) {
            info("Non-team user moving to team practice room, creating a team for the user.");
            CoreServices.createPracticeTeamForUser(userID);
            user = CoreServices.getUser(userID, false);
            ResponseProcessor.updateCurrentUser(connectionID, user);
        }


        switch (roomType) {
        case ContestConstants.CONTEST_ROOM:
            // For a contest_type the roomID passed in is actually the ContestID
            Round contest = CoreServices.getContestRound(roomID);

            if (user.isLevelTwoAdmin()) {
                Iterator roomIDs = contest.getAllRoomIDs();
                boolean gotNonAdminRoom = false;
                // Find the first non-admin room for the contest.
                while (roomIDs.hasNext() && !gotNonAdminRoom) {
                    roomID = ((Integer) roomIDs.next()).intValue();
                    Room nextRoom = CoreServices.getRoom(roomID, false);
                    if (!nextRoom.isAdminRoom()) {
                        if (trace.isDebugEnabled()) trace.debug("Got nonadmin room for admin move to contest: " + roomID);
                    }
                    gotNonAdminRoom = !nextRoom.isAdminRoom();
                }
                // If there were no-non admin rooms, get the admin room for the round.
                if (!gotNonAdminRoom) {
                    roomID = CoreServices.getAdminRoomIDForRound(contest.getRoundID());
                    info("Admin Room: " + roomID);
                }
            } else {
                Integer roomIDInteger = contest.getAssignedRoom(userID);
                if (roomIDInteger != null) {
                    roomID = roomIDInteger.intValue();
                    if (trace.isDebugEnabled()) trace.debug("User: " + user.getName() + " got assigned room: " + roomID);
                } else {
                    roomID = contest.getNonAdminRoom().intValue();
                }
            }
            break;
        default:
            break;
        }

        // Send the exit message to the room.
        if (trace.isDebugEnabled()) trace.debug("Sending exit message on RoomID = " + user.getRoomID());

        if (user.getRoomID() >= 0) {
            toggleUserConnection(userID, user.getRoomID(), false);
            ArrayList responses = ResponseProcessor.leaveRoom(user, false);
            if (trace.isDebugEnabled()) trace.debug("Responses size = " + responses.size());
            if (responses.size() > 0) {
                EventService.sendResponseToRoom(user.getRoomID(), responses);
            }
        }
        toggleUserConnection(userID, roomID, true);
        // Perform actual move in server state.
        CoreServices.enter(userID, roomID);
    }

    /**
     * Sends the enter message to the room the user has just entered.
     *
     * @param userID the user id
     * @param enteringNewRoom the entering new room
     */
    static void enter(int userID, boolean enteringNewRoom) {
        // Send enter message to room
        if (enteringNewRoom) {
            User user = CoreServices.getUser(userID, false);
            //UserState userState = UserState.getUserState( userID );
            if (user.getRoomID() != ContestConstants.INVALID_ROOM) {
                EventService.sendResponseToRoom(user.getRoomID(), ResponseProcessor.enterRoom(user));
            }
        }
    }

    /**
     * Gets the team list.
     *
     * @param connectionID the connection id
     * @param userID the user id
     * @return the team list
     */
    public static void getTeamList(Integer connectionID, int userID) {
        User user = CoreServices.getUser(userID, false);
        int teamID = user.getTeamID();
        if (teamID == User.NO_TEAM) {
            trace.error("getTeamList called by user: " + user.getName() + " with no team.");
            return;
        }

        ResponseProcessor.getTeamList(connectionID, teamID);
    }

    /**
     * Close team list.
     *
     * @param connectionID the connection id
     * @param userID the user id
     */
    public static void closeTeamList(Integer connectionID, int userID) {
        User user = CoreServices.getUser(userID, false);
        int teamID = user.getTeamID();
        if (teamID == User.NO_TEAM) {
            trace.error("getTeamList called by user: " + user.getName() + " with no team.");
            return;
        }

        ResponseProcessor.closeTeamList(connectionID, teamID);
    }

    /**
     * Join team.
     *
     * @param userID the user id
     * @param teamName the team name
     */
    public static void joinTeam(int userID, String teamName) {
        try {
            CoreServices.addInterestedToTeam(userID, teamName);
        } finally {
        }
    }

    /**
     * Leave team.
     *
     * @param userID the user id
     */
    public static void leaveTeam(int userID) {
        try {
            User user = CoreServices.getUser(userID, false);
            Team team = CoreServices.getTeam(user.getTeamID(), false);
            CoreServices.removeInterestedFromTeam(userID, team.getName());
            CoreServices.removeUserFromTeam(userID, team.getID());
        } finally {

        }
    }


    /**
     * Adds the team member.
     *
     * @param captainID the captain id
     * @param userToAdd the user to add
     */
    public static void addTeamMember(int captainID, String userToAdd) {
        User cap = null;
        try {
            cap = CoreServices.getUser(captainID, false);
            CoreServices.addUserToTeam(userToAdd, cap.getTeamID());
        } finally {
        }
    }

    /**
     * Removes the team member.
     *
     * @param captainID the captain id
     * @param userToRemove the user to remove
     */
    public static void removeTeamMember(int captainID, String userToRemove) {
        User cap = null;
        try {
            cap = CoreServices.getUser(captainID, false);
            CoreServices.removeUserFromTeam(userToRemove, cap.getTeamID());
        } finally {
        }
    }

    //    public static void sendToSpectators(MessagePacket mp) {
    //        synchronized (s_allEvents) {
    //            s_allEvents.add(mp);
    //        }
    //        ResponseProcessor.process(RequestProcessor.allSpectatorConnectionIDs(), mp);
    //    }
    //
    //    public static void sendToSpectators(Message m) {
    //        ArrayList al = new ArrayList(1);
    //        al.add(m);
    //        MessagePacket mp = new MessagePacket(al);
    //        sendToSpectators(mp);
    //    }


    // Can be moved to Rules Engine
    /**
     * Can compile.
     *
     * @param userID the user id
     * @param source the source
     * @param componentID the component id
     * @param languageId the language id
     * @return the results
     */
    static Results canCompile(int userID, String source, long componentID, int languageId) {
        User user = CoreServices.getUser(userID, false);
        UserState userState = UserState.getUserState(userID);
        ContestRoom room = null;
        if (user.getRoomID() == ContestConstants.INVALID_ROOM) {  // Just drop the request if the user is in an invalid room.
            return new Results(false, "Invalid room");
        }

        UserState.ProblemState problemState = userState.getProblemState();
        if (problemState.getState() != UserState.ProblemState.CODING || problemState.getProblemID() != componentID || problemState.getRoomID() != user.getRoomID()) {
            trace.info("User " + userID + " tries to compile while not opening for coding, state=" + problemState.getState() + ", component ID=" + problemState.getProblemID() + ", room ID=" + problemState.getRoomID());
            return new Results(false, "You cannot compile outside of coding environment.");
        }

        try {
            Room baseRoom = CoreServices.getRoom(user.getRoomID(), false);
            if (!(baseRoom instanceof ContestRoom)) {
                trace.error("Invalid compile request.  User not in a contest room.");
                return new Results(false, "You cannot compile outside of a contest room.");
            }
            room = (ContestRoom) baseRoom;
            Coder coder = room.getCoder(userID);
            if (coder == null) {
                trace.error("compile got null object: user = " + user + " coder = " + coder);
                return new Results(false, "General server error.  Invalid user state.");
            }
            if (userState.isBusy()) {
                info("compile throwing out a request because server is busy.");
                return new Results(false, "compile throwing out a request because server is busy.");
            }
            CoderComponent currentComponent = (CoderComponent) coder.getComponent(componentID);
            if (!user.isLevelTwoAdmin() && !ContestConstants.ACCEPT_MULTIPLE_SUBMISSIONS && currentComponent.isSubmitted()) {
                throw new RuntimeException("You are not allowed to compile after submitting.");
            }

            String programText = source;
            if (programText.trim().length() == 0) {
                currentComponent.setStatus(ContestConstants.LOOKED_AT);
                userState.setCanTestOrSubmit(false);
                trace.debug("Aborting compile on blank code");
                return new Results(false, "You cannot compile blank code.");
            }
            trace.debug("Getting contestRound");
            Round contestRound = CoreServices.getContestRound(room.getRoundID());

            int activePhase = contestRound.getPhase();
            if (trace.isDebugEnabled()) trace.debug("Got contestRound in phase: " + activePhase);

            if (!user.isLevelTwoAdmin() && activePhase != ContestConstants.CODING_PHASE && !ContestConstants.isPracticeRoomType(user.getRoomType())) {
                userState.setCanTestOrSubmit(false);
                trace.error("Aborting compile on !admin|codephase|practice");
                return new Results(false, "You cannot compile in a contest that is not active.");
            }
            if (!user.isLevelTwoAdmin() && hasCodingTimeExpiredForCoder(contestRound, coder)) {
                userState.setCanTestOrSubmit(false);
                trace.info("canCompile(): Coding time expired for user : "+user.getID());
                return new Results(false, "You cannot compile after your time has expired");
            }
            Language lang = BaseLanguage.getLanguage(languageId);
            if (!contestRound.getRoundProperties().allowsLanguage(lang)) {
                userState.setCanTestOrSubmit(false);
                trace.error("Client sent an invalid language for the round. Lang="+lang.getName()+" roundId="+contestRound.getRoundID());
                return new Results(false, "Language '"+lang.getName()+"' is not available in this round.");
            }
        } finally {
        }
        return new Results(true, "");
    }

    /**
     * Compiles the users current open problem with the given source and language.
     *
     * @param userID the user id
     * @param source the source
     * @param language the language
     * @param componentID the component id
     */
    static void compile(int userID, String source, int language, int componentID) {
        //TODO send componentID to canCompile, for now, assume true
        Results canCompile = canCompile(userID, source, componentID, language);
        if (!canCompile.isSuccess()) {
            throw new RuntimeException(canCompile.getMsg());
        }
        // Note, canCompile did all the validation
        User user = CoreServices.getUser(userID, false);
        UserState userState = UserState.getUserState(userID);
        ContestRoom room = null;
        boolean saveRoom = false;

        try {
            room = (ContestRoom) CoreServices.getRoom(user.getRoomID(), true);
            Coder coder = room.getCoder(userID);
            CoderComponent currentComponent = (CoderComponent) coder.getComponent(componentID);
            info("processing a compile request, userID=" + userID + ", language=" + language + " component="+componentID);
            saveRoom = true;
            String programText = source;
            trace.debug("Getting contestRound");
            Round contestRound = CoreServices.getContestRound(room.getRoundID());
            int activePhase = contestRound.getPhase();
            if (trace.isDebugEnabled()) trace.debug("Got contestRound in phase: " + activePhase);
            int appletServerID = userID; // We can just use the userID in the new system
            int serverID = userID;
            currentComponent.setProgramText(programText);
            currentComponent.setLanguage(language);

            userState.setCurrentBusyTime();

            trace.debug("Creating locations");
            Location location = new Location(coder.getContestID(), coder.getRoundID(), coder.getRoomID());
            Round contest = CoreServices.getContestRound(coder.getRoundID());
            RoundComponent component = CoreServices.getRoundComponent(contest.getRoundID(), currentComponent.getComponentID(), coder.getDivisionID());
            if (trace.isDebugEnabled()) trace.debug("Got roundProblem: " + component);
            //todo is all this really neccessary for a compile?  I think all we need is the componentID, coder, language, and userID.
            //I don't see why division or Location are important
            Submission sub = new Submission(location, component, programText, language);
            sub.setCoderId(userID);
            long time = getCurrentTime();
            sub.setSubmitTime(time);
            if (trace.isDebugEnabled()) trace.debug("point value: " + currentComponent.getPointValue());
            sub.setPointValue(currentComponent.getPointValue());
            //sub.setSelectedComponentID(componentID);
            if (!scheduleCompilation(userID, sub, appletServerID, serverID, time)) {
                userState.setCanTestOrSubmit(false);
                userState.resetBusyTime();
                throw new RuntimeException("You cannot compile in a contest that is not active.");
            }

            if (currentComponent.getStatus() == ContestConstants.LOOKED_AT) {
                ContestEvent event = new ContestEvent(room.getRoomID(), ContestEvent.COMPILE_COMPONENT, null,
                                                      userID, component.getComponent().getProblemID(),
                                                      component.getComponent().getComponentID(),
                                                      currentComponent.getStatusString());
                event.setChallengerName(coder.getName());
                event.setEventTime(System.currentTimeMillis());
                EventService.sendGlobalEvent(event);
            }

            if(!ContestConstants.isPracticeRoomType(room.getType()))
                specAppProcessor.compilingComponent(contestRound, room, coder, currentComponent, programText, language);
        } finally {
            // release room lock
            if (room != null) {
                if (saveRoom) {
                    CoreServices.saveToCache(Room.getCacheKey(room.getRoomID()), room);
                } else {
                    CoreServices.releaseLock(Room.getCacheKey(room.getRoomID()));
                }
            }
        }
    }

    /**
     * Schedule compilation.
     *
     * @param userID the user id
     * @param sub the sub
     * @param appletServerId the applet server id
     * @param socketServerId the socket server id
     * @param submitTime the submit time
     * @return true, if successful
     */
    private static boolean scheduleCompilation(int userID, Submission sub, int appletServerId, int socketServerId, long submitTime) {
        trace.debug("submitCompile");
        Round round = CoreServices.getContestRound(sub.getRoundID());
        User user = CoreServices.getUser(userID, false);
        StringBuilder message = new StringBuilder();
        message.append("System> ");
        message.append(user.getName());
        message.append(" is compiling the ");
        ComponentNameBuilder nameBuilder = round.getRoundType().getComponentNameBuilder();
        String componentName = nameBuilder.longNameForComponent(sub.getComponent().getClassName(), sub.getRoundPointVal(), round.getRoundProperties());
        message.append(componentName).append(".\n");
        trace.debug("Sending compile room message");
        EventService.sendRoomSystemMessage(user.getRoomID(), message.toString());


        if (false) { // uncomment for replay
            ReplayCompileEvent rce = new ReplayCompileEvent(userID, sub.getLocation().getRoomID(), sub.getComponent().getComponentID(),
                                                            sub.getProgramText(), sub);
            EventService.sendReplayEvent(rce);
        }

        int language = sub.getLanguage();
        if (trace.isDebugEnabled()) trace.debug("sub.getLanguage() = " + language);
        try {
            getSRMCompiler().compileSubmission(sub);
            return true;
        } catch (Exception e) {
            trace.error("",e);
            return false;
        }
    }

    /**
     * Gets the SRM compiler.
     *
     * @return the SRM compiler
     */
    private static synchronized SRMCompilerInvoker getSRMCompiler() {
        if (compiler == null) {
            compiler = SRMCompilerInvoker.create("Processor", new SRMCompilationCurrentHandler());
        }
        return compiler;
    }

    /**
     * Clears the practice data for the user in their current room.
     *
     * @param connectionID the connection id
     * @param userID the user id
     * @param roomID the room id
     */
    static void clearPracticer(Integer connectionID, int userID, int roomID) {
        User user = CoreServices.getUser(userID, false);
        if (!user.isLevelTwoAdmin() && !ContestConstants.isPracticeRoomType(user.getRoomType())) {
            ResponseProcessor.error(connectionID, "You are only allowed to clear your code in practice rooms.");
            trace.error("CHEATER!!! " + user.getName() + " is trying to clear their code outside of a practice room.");
            return;
        }

        BaseCodingRoom room = (BaseCodingRoom) CoreServices.getRoom(roomID, false);
        if (room.getType() == ContestConstants.TEAM_PRACTICE_CODER_ROOM ||
            room.getType() == ContestConstants.TEAM_ADMIN_ROOM) {
            if (!user.isCaptain()) {
                ResponseProcessor.error(connectionID, "Only the captain can clear your team's code.");
                return;
            }
        }

        if (CoreServices.clearPracticer(userID, roomID)) {
            UserState userState = UserState.getUserState(userID);
            userState.setCanTestOrSubmit(false);
            userState.resetBusyTime();
            ResponseProcessor.clearPracticer(connectionID);
        } else {
            ResponseProcessor.error(connectionID, "There was an error clearing your data.  Please email service@topcoder.com");
            trace.error("Failed to clear practice problems for user: " + user.getName());
        }
    }

    //added 2-20 rfairfax
    /**
     * Clear practice problem.
     *
     * @param connectionID the connection id
     * @param userID the user id
     * @param roomID the room id
     * @param componentID the component id
     */
    static void clearPracticeProblem(Integer connectionID, int userID, int roomID, Long[] componentID) {
        User user = CoreServices.getUser(userID, false);
        if (!user.isLevelTwoAdmin() && !ContestConstants.isPracticeRoomType(user.getRoomType())) {
            ResponseProcessor.error(connectionID, "You are only allowed to clear your code in practice rooms.");
            trace.error("CHEATER!!! " + user.getName() + " is trying to clear their code outside of a practice room.");
            return;
        }

        BaseCodingRoom room = (BaseCodingRoom) CoreServices.getRoom(roomID, false);
        if (room.getType() == ContestConstants.TEAM_PRACTICE_CODER_ROOM ||
            room.getType() == ContestConstants.TEAM_ADMIN_ROOM) {
            if (!user.isCaptain()) {
                ResponseProcessor.error(connectionID, "Only the captain can clear your team's code.");
                return;
            }
        }

        if (CoreServices.clearPracticeProblem(userID, roomID, componentID)) {
            UserState userState = UserState.getUserState(userID);
            userState.setCanTestOrSubmit(false);
            userState.resetBusyTime();
            ResponseProcessor.clearPracticeProblem(connectionID);
        } else {
            ResponseProcessor.error(connectionID, "There was an error clearing your data.  Please email service@topcoder.com");
            trace.error("Failed to clear practice problems for user: " + user.getName());
        }
    }


    /**
     * Sends the test info for the users currently opened problem.
     *
     * @param connectionID the connection id
     * @param userID the user id
     * @param componentID the component id
     */
    static void testInfo(Integer connectionID, int userID, int componentID) {
        User user = CoreServices.getUser(userID, false);
        UserState userState = UserState.getUserState(userID);
        if (user.getRoomID() == ContestConstants.INVALID_ROOM) {
            trace.error("testInfo with user: " + userID + " in invalid room. Connection: " + connectionID);
            return;
        }
        ContestRoom room = (ContestRoom) CoreServices.getRoom(user.getRoomID(), false);
        Coder coder = room.getCoder(user.getID());
        CoderComponent coderProblem = (CoderComponent) coder.getComponent(componentID);

        if (!coderProblem.isSubmitted() && !userState.canTestOrSubmit()) {
            ResponseProcessor.error(connectionID, "You cannot test unless you have successfully compiled first.");
            return;
        }
        RoundComponent component = CoreServices.getRoundComponent(coder.getRoundID(), coderProblem.getComponentID(), coder.getDivisionID());
        ResponseProcessor.testInfo(connectionID, component);
    }

    /**
     * Can test.
     *
     * @param userID the user id
     * @param componentID the component id
     * @return the results
     */
    public static Results canTest(int userID, long componentID) {
        User user = CoreServices.getUser(userID, false);
        if (user.getRoomID() == ContestConstants.INVALID_ROOM) {
            trace.error("test with user: " + userID + " in invalid room.");
            return new Results(false, "Error: cannot test in invalid room");
        }
        UserState userState = UserState.getUserState(userID);
        UserState.ProblemState problemState = userState.getProblemState();
        if (problemState.getState() != UserState.ProblemState.CODING || problemState.getProblemID() != componentID || user.getRoomID() != problemState.getRoomID()) {
            trace.info("User " + userID + " tries to test while not opening for coding, state=" + problemState.getState() + ", component ID=" + problemState.getProblemID() + ", room ID=" + problemState.getRoomID());
            return new Results(false, "You cannot test outside of coding environment.");
        }

        Room baseRoom = CoreServices.getRoom(user.getRoomID(), false);
        if (!(baseRoom instanceof ContestRoom)) {
            trace.error("Invalid test request.  User not in a contest room.");
            return new Results(false, "You cannot test outside of a contest room.");
        }
        ContestRoom room = (ContestRoom) baseRoom;
        Coder coder = room.getCoder(user.getID());
        CoderComponent coderComponent = (CoderComponent) coder.getComponent(componentID);

        if (!coderComponent.isSubmitted() && !userState.canTestOrSubmit()) {
            return new Results(false, "You cannot test unless you have successfully compiled first.");
        }

        if (userState.isBusy()) {      // Disregard the request
            return new Results(false, "Error: Server is busy processing your previous request");
        }
        if (!user.isLevelTwoAdmin() && hasCodingTimeExpiredForCoder(CoreServices.getContestRound(room.getRoundID()), coder)) {
            trace.info("canTest(): Coding time expired for user : "+user.getID());
            return new Results(false, "You cannot test after your time has expired");
        }
        return new Results(true, "");
    }


    /**
     * Performs a test against the users currently opened problem with the given arguments.
     *
     * @param connectionID the connection id
     * @param userID the user id
     * @param args the args
     * @param componentID the component id
     */
    static void test(Integer connectionID, int userID, Object[] args, long componentID) {
        //TODO uncomment and fix this
        info(userID + " is tesing component " + componentID +
             " with args: " + ContestConstants.makePretty(args));
        Results canTest = canTest(userID, componentID);
        if (!canTest.isSuccess()) {
            ResponseProcessor.error(connectionID, canTest.getMsg());
            return;
        }

        User user = CoreServices.getUser(userID, false);

        UserState userState = UserState.getUserState(userID);
        if (userState.isBusy()) {
            info("Throwing out a test request because server is busy: " + userID);
            return;
        }
        ContestRoom room = (ContestRoom) CoreServices.getRoom(user.getRoomID(), false);
        Coder coder = room.getCoder(user.getID());
        CoderComponent coderComponent = (CoderComponent) coder.getComponent(componentID);
        userState.setCurrentBusyTime();

        RoundComponent component = CoreServices.getRoundComponent(coder.getRoundID(),
                coderComponent.getComponentID(), coder.getDivisionID());
        Location location = new Location(coder.getContestID(), coder.getRoundID(), coder.getRoomID());

        info("Got component 1");
        // Check to see if it's a given test case
        com.topcoder.shared.problem.TestCase[] testCases = CoreServices.getComponent(
                coderComponent.getComponentID()).getTestCases();
        DataType[] dataTypes = CoreServices.getComponent(coderComponent.getComponentID()).getParamTypes();
        DataType returnType = CoreServices.getComponent(coderComponent.getComponentID()).getReturnType();
        info("Got component 2");
        boolean given = false;
        Object result = checkExampleTest(args, testCases, dataTypes, returnType);

        UserTestAttributes userTest;
        if (result != null) {
            userTest = new UserTestAttributes(user.getID(), location,
                    component.getComponent(), coderComponent.getLanguage(), result);
        } else {
            userTest = new UserTestAttributes(user.getID(), location,
                    component.getComponent(), coderComponent.getLanguage());
        }
        userTest.setArgs(args);

        StringBuilder message = new StringBuilder("System> ");
        message.append(user.getName());
        message.append(" is testing the ");
        Round round = CoreServices.getContestRound(room.getRoundID());
        ComponentNameBuilder nameBuilder = round.getRoundType().getComponentNameBuilder();
        String componentName = nameBuilder.longNameForComponent(component.getComponent().getClassName(),
                component.getPointVal(), round.getRoundProperties());
        message.append(componentName).append(".\n");

        EventService.sendRoomSystemMessage(user.getRoomID(), message.toString());

        if (!ContestConstants.isPracticeRoomType(room.getType())) {
            specAppProcessor.testingComponent(round, room, coder, coderComponent);
        }

        long time = getCurrentTime();
        userTest.setSubmitTime(time);
        if (!CoreServices.submitUserTest(userTest)) {
            ResponseProcessor.error(connectionID, "Failed to submit test");
        }
    }

    /**
     * Checks if given arguments are from one of the given test cases.
     * If that is the case it returns the expected return value for that test case,
     * otherwise it returns null.
     *
     * @param args the test arguments
     * @param testCases the test cases
     * @param dataTypes the data types
     * @param returnType the return type
     * @return if the arguments are one of the given test cases it returns the expected return value
     *              for that test case, otherwise it returns null
     * @since 1.4
     */
    private static Object checkExampleTest(Object args, com.topcoder.shared.problem.TestCase[] testCases,
            DataType[] dataTypes, DataType returnType) {
        Object result = "";
        for (int x = 0; x < testCases.length; x++) {
            String[] inputs = testCases[x].getInput();
            Object[] formattedInputs = new Object[inputs.length];
            for (int i = 0; i < inputs.length; i++) {
                // an array
                if (dataTypes[i].getDescription().equals("ArrayList")
                        || dataTypes[i].getDescription().startsWith("vector")
                        || dataTypes[i].getDescription().endsWith("[]")) {
                    formattedInputs[i] = bracketParse(inputs[i]);
                    // a string
                } else if (dataTypes[i].getDescription().equalsIgnoreCase("string")) {
                    ArrayList al = bracketParse(inputs[i]);
                    formattedInputs[i] = al.get(0);
                    // a char
                } else if (dataTypes[i].getDescription().equals("char")) {
                    formattedInputs[i] = inputs[i].substring(1, inputs[i].length() - 1);
                    // a number
                } else {
                    formattedInputs[i] = inputs[i];
                }
            }
            // Check to see if the args are equal
            if (argsEqual(formattedInputs, args)) {
                // an array
                if (returnType.getDescription().equals("ArrayList")
                        || returnType.getDescription().startsWith("vector")
                        || returnType.getDescription().endsWith("[]")) {
                    Object[] arr = bracketParse(testCases[x].getOutput()).toArray();
                    String type = returnType.getDescription().toLowerCase();
                    // format the array properly
                    if (type.matches(".*string.*")) {
                        String[] fin = new String[arr.length];
                        for (int i = 0; i < fin.length; i++) {
                            fin[i] = arr[i].toString();
                        }
                        result = fin;
                    } else if (type.matches(".*char.*")) {
                        char[] fin = new char[arr.length];
                        for (int i = 0; i < fin.length; i++) {
                            fin[i] = arr[i].toString().charAt(0);
                        }
                        result = fin;
                    } else if (type.matches(".*float.*")) {
                        float[] fin = new float[arr.length];
                        for (int i = 0; i < fin.length; i++) {
                            fin[i] = Float.valueOf(arr[i].toString()).floatValue();
                        }
                        result = fin;
                    } else if (type.matches(".*boolean.*")) {
                        boolean[] fin = new boolean[arr.length];
                        for (int i = 0; i < fin.length; i++) {
                            fin[i] = Boolean.valueOf(arr[i].toString()).booleanValue();
                        }
                        result = fin;
                    } else if (type.matches(".*long.*")) {
                        long[] fin = new long[arr.length];
                        for (int i = 0; i < fin.length; i++) {
                            fin[i] = Long.valueOf(arr[i].toString()).longValue();
                        }
                        result = fin;
                    } else if (type.matches(".*byte.*")) {
                        byte[] fin = new byte[arr.length];
                        for (int i = 0; i < fin.length; i++) {
                            fin[i] = Byte.valueOf(arr[i].toString()).byteValue();
                        }
                        result = fin;
                    } else if (type.matches(".*short.*")) {
                        short[] fin = new short[arr.length];
                        for (int i = 0; i < fin.length; i++) {
                            fin[i] = Short.valueOf(arr[i].toString()).shortValue();
                        }
                        result = fin;
                    } else if (type.matches(".*int.*")) {
                        int[] fin = new int[arr.length];
                        for (int i = 0; i < fin.length; i++) {
                            fin[i] = Integer.valueOf(arr[i].toString()).intValue();
                        }
                        result = fin;
                    } else if (type.matches(".*double.*")) {
                        double[] fin = new double[arr.length];
                        for (int i = 0; i < fin.length; i++) {
                            fin[i] = Double.valueOf(arr[i].toString()).doubleValue();
                        }
                        result = fin;
                    } else {
                        result = arr;
                    }
                    // a string
                } else if (returnType.getDescription().equalsIgnoreCase("string")) {
                    ArrayList al = bracketParse(testCases[x].getOutput());
                    result = al.get(0);
                    // a char
                } else if (returnType.getDescription().equals("char")) {
                    result = new Character(testCases[x].getOutput().substring(1,
                            testCases[x].getOutput().length() - 1).charAt(0));
                    // a number
                } else if (returnType.getDescription().equals("float")) {
                    result = Float.valueOf(testCases[x].getOutput());
                } else if (returnType.getDescription().equals("boolean")) {
                    result = Boolean.valueOf(testCases[x].getOutput());
                } else if (returnType.getDescription().equals("long")) {
                    result = Long.valueOf(testCases[x].getOutput());
                } else if (returnType.getDescription().equals("byte")) {
                    result = Byte.valueOf(testCases[x].getOutput());
                } else if (returnType.getDescription().equals("short")) {
                    result = Short.valueOf(testCases[x].getOutput());
                } else if (returnType.getDescription().equals("int")) {
                    result = Integer.valueOf(testCases[x].getOutput());
                } else if (returnType.getDescription().equals("double")) {
                    result = Double.valueOf(testCases[x].getOutput());
                    // not recognized... something went wrong.
                } else {
                    result = testCases[x].getOutput();
                }
                return result;
            }
        }
        return null;
    }

    /**
     * Performs a batch test against the users currently opened problem with the given arguments.
     *
     * @param connectionID connection id
     * @param userID user id
     * @param tests the list of tests - every element of this array contains the arguments
     *          (another list) for the testing
     * @param componentID component id
     * @since 1.4
     */
    static void batchTest(Integer connectionID, int userID, Object[] tests, long componentID) {
        StringBuilder build = new StringBuilder(userID + " is batch testing, tests: " + componentID
                + " with args:\n");
        for (int itest = 0; itest < tests.length; itest++) {
            build.append("test #");
            build.append(itest);
            build.append(": ");
            build.append(ContestConstants.makePretty(tests [itest]));
        }
        info(build.toString());
        Results canTest = canTest(userID, componentID);
        if (!canTest.isSuccess()) {
            ResponseProcessor.error(connectionID, canTest.getMsg());
            return;
        }
        User user = CoreServices.getUser(userID, false);
        UserState userState = UserState.getUserState(userID);
        if (userState.isBusy()) {
            info("Throwing out a batch test request because server is busy: " + userID);
            return;
        }
        ContestRoom room = (ContestRoom) CoreServices.getRoom(user.getRoomID(), false);
        Coder coder = room.getCoder(user.getID());
        CoderComponent coderComponent = (CoderComponent) coder.getComponent(componentID);
        userState.setCurrentBusyTime();

        RoundComponent component = CoreServices.getRoundComponent(coder.getRoundID(),
                                    coderComponent.getComponentID(), coder.getDivisionID());
        Location location = new Location(coder.getContestID(), coder.getRoundID(), coder.getRoomID());

        info("Got component 1");
        // Check to see if it's a given test case
        com.topcoder.shared.problem.TestCase[] testCases = CoreServices.getComponent(
                coderComponent.getComponentID()).getTestCases();
        DataType[] dataTypes = CoreServices.getComponent(coderComponent.getComponentID()).getParamTypes();
        DataType returnType = CoreServices.getComponent(coderComponent.getComponentID()).getReturnType();
        info("Got component 2");
        Object result = "";
        Object [] batchArgs = new Object [tests.length * UserTestAttributes.BATCH_ARGS_BLOCK_SIZE];
        for (int itest = 0; itest < tests.length; itest++) {
            Object[] args;
            if (tests [itest] instanceof List) {
                info("batchtest instanceof ArrayList");
                args = ((List) tests [itest]).toArray();
            } else {
                info("batchtest instanceof object[]");
                args = (Object []) tests [itest];
            }
            info("length: " + args.length);
            result = checkExampleTest(args, testCases, dataTypes, returnType);

            batchArgs [UserTestAttributes.BATCH_ARGS_BLOCK_SIZE * itest] = args;
            batchArgs [UserTestAttributes.BATCH_ARGS_BLOCK_SIZE * itest + 1] = result;
        }
        UserTestAttributes userTest = new UserTestAttributes(user.getID(), location,
                component.getComponent(), coderComponent.getLanguage());
        userTest.setArgs(batchArgs);
        userTest.setBatchTest(true);

        //userTest.setArgs(args);

        StringBuilder message = new StringBuilder("System> ");
        message.append(user.getName());
        message.append(" is batch testing the ");
        Round round = CoreServices.getContestRound(room.getRoundID());
        ComponentNameBuilder nameBuilder = round.getRoundType().getComponentNameBuilder();
        String componentName = nameBuilder.longNameForComponent(
                component.getComponent().getClassName(), component.getPointVal(), round.getRoundProperties());
        message.append(componentName).append(".\n");

        EventService.sendRoomSystemMessage(user.getRoomID(), message.toString());

        if (!ContestConstants.isPracticeRoomType(room.getType())) {
            specAppProcessor.testingComponent(round, room, coder, coderComponent);
        }

        long time = getCurrentTime();
        userTest.setSubmitTime(time);
        if (!CoreServices.submitUserTest(userTest)) {
            ResponseProcessor.error(connectionID, "Failed to submit batch test");
        }
    }

    /**
     * Parse an array-type argument that is enclosed with braces.  Each
     * element of the array is separated by a comma.
     *
     * Note: this is copied from ArrayListInputdialog.java.
     *
     * @return An array list containing all the values parsed from the string.
     */
    private static final int START = 0;

    /** The Constant IN_QUOTE. */
    private static final int IN_QUOTE = 1;

    /** The Constant ESCAPE. */
    private static final int ESCAPE = 2;

    /**
     * Bracket parse.
     *
     * @param text the text
     * @return the array list
     */
    private static ArrayList bracketParse(String text)
    {
        ArrayList result = new ArrayList();
        text = text.trim();
        //
        // modified 4/9/2003 by schveiguy
        //
        // fix bug where empty array causes exception
        //
        if(text.length() > 0 && text.charAt(0) == '{') text = text.substring(1);
        if (text.length() > 0 && text.charAt(text.length() - 1) == '}') text = text.substring(0, text.length() - 1);
        if(text.length() == 0)
            return result;
        int state = START;
        StringBuilder buf = new StringBuilder(500);
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            switch (state) {
            case ESCAPE:
                switch (ch) {
                case '\\':
                    buf.append('\\');
                    break;
                case '"':
                    buf.append('"');
                    break;
                default: //we'll just assume it was a mistake, problems really should not use tabs, line feeds, etc.
                    buf.append('\\');
                    buf.append(ch);
                }
                state = IN_QUOTE;
                break;
            case IN_QUOTE:
                switch (ch) {
                case '\\':
                    state = ESCAPE;
                    break;
                case '"':
                    String param = buf.toString();
                    buf.delete(0, buf.length());
                    state = START;
                    result.add(param);
                    break;
                default:
                    buf.append(ch);
                    break;
                }
                break;
            case START:
                if (Character.isWhitespace(ch)) {
                    if (buf.length() > 0) {
                        String param = buf.toString().trim();
                        buf.delete(0, buf.length());
                        result.add(param);
                    }
                    continue;
                }
                switch (ch) {
                case '"':
                    if (buf.length() > 0) {
                        buf.append('"');
                    } else {
                        state = IN_QUOTE;
                    }
                    break;
                case ',':
                    if (buf.length() > 0 || (i == 0) || (i > 0 && text.charAt(i - 1) == ',')) {
                        String param = buf.toString().trim();
                        buf.delete(0, buf.length());
                        result.add(param);
                    }
                    break;
                default:
                    buf.append(ch);
                }
            }
        }
        if (buf.length() > 0 || text.charAt(text.length() - 1) == ',') {
            String param = buf.toString().trim();
            buf.delete(0, buf.length());
            result.add(param);
        }
        //
        // return the array list containing the values
        //
        return result;
    }

    /**
     * Runs a practice system test for the user in their current room.
     *
     * @param connectionID the connection id
     * @param userID the user id
     * @param roomID the room id
     * @param componentIds the component ids
     */
    static void practiceSystemTest(Integer connectionID, int userID, int roomID, int[] componentIds) {
        User user = CoreServices.getUser(userID, false);
        if (!ContestConstants.isPracticeRoomType(user.getRoomType()) && !user.isLevelTwoAdmin()) {
            ResponseProcessor.error(connectionID, "You are only allowed to system test your code in practice rooms.");
            trace.error("CHEATER!!! " + user.getName() + " is trying to system test their code outside of a practice room.");
            return;
        }
        UserState userState = UserState.getUserState(userID);
        if (userState.isBusy()) {
            info("Throwing out a practice system test request because server is busy: " + userID);
            return;
        }
        ContestRoom room = (ContestRoom) CoreServices.getRoom(roomID, false);
        userState.setCurrentBusyTime();
        CoreServices.practiceSystemTest(room.getContestID(), room.getRoundID(), userID, componentIds);
    }

    /**
     * Auto system test.
     *
     * @param connectionID the connection id
     * @param userID the user id
     * @param roundID the round id
     */
    static void autoSystemTest(Integer connectionID, int userID, int roundID) {
        User user = CoreServices.getUser(userID, false);
        UserState userState = UserState.getUserState(userID);
        if (userState.isBusy()) {
            info("Throwing out a system test request because server is busy: " + userID);
            return;
        }
        userState.setCurrentBusyTime();
        if (!CoreServices.isRoundActive(roundID) && !CoreServices.isPracticeRoundActive(roundID)) {
            info("Throwing out a system test request because the round is not active. User: " + userID + ", round: " + roundID);
            return;
        }
        Round round = CoreServices.getContestRound(roundID);
        //uncomment to allow auto sys tests
        //CoreServices.systemTest(true, false, round.getContestID(), round.getRoundID(), userID, 0, true);
    }

    /**
     * Can submit.
     *
     * @param userID the user id
     * @param componentID the component id
     * @return the results
     */
    static Results canSubmit(int userID, long componentID) {
        UserState userState = UserState.getUserState(userID);
        if (userState.isBusy()) {
            info("Throwing out a submit problem request because server is busy: " + userID);
            return new Results(false, "Error: Server is busy processing your previous request");
        }

        User user = CoreServices.getUser(userID, false);
        //          boolean saveRoom = false;
        int roomID = user.getContestRoom();
        if (roomID == ContestConstants.INVALID_ROOM) {
            roomID = user.getRoomID();
            if (roomID == ContestConstants.INVALID_ROOM) {
                trace.error("submit user: " + userID + " invalid room.");
                return new Results(false, "Server error while trying to submit");
            }
        }

        UserState.ProblemState problemState = userState.getProblemState();
        if (problemState.getState() != UserState.ProblemState.CODING || problemState.getProblemID() != componentID || problemState.getRoomID() != roomID) {
            trace.info("User " + userID + " tries to submit while not opening for coding, state=" + problemState.getState() + ", component ID=" + problemState.getProblemID() + ", room ID=" + problemState.getRoomID());
            return new Results(false, "You cannot submit outside of coding environment.");
        }

        Room baseRoom = CoreServices.getRoom(roomID, false);
        if (!(baseRoom instanceof BaseCodingRoom)) {
            trace.error("Invalid submit request.  User not in a coding room.");
            return new Results(false, "You cannot submit outside of a coding room.");
        }
        BaseCodingRoom room = (BaseCodingRoom) baseRoom;
        Round round = CoreServices.getContestRound(room.getRoundID());

        if (!user.isLevelTwoAdmin() && round.getPhase() != ContestConstants.CODING_PHASE && !ContestConstants.isPracticeRoomType(room.getType())) {
            return new Results(false, "You cannot submit in a contest that is not active.");
        }
        Coder coder = room.getCoder(userID);
        BaseCoderComponent coderComponent = coder.getComponent(componentID);
        if (!user.isLevelTwoAdmin() && !ContestConstants.ACCEPT_MULTIPLE_SUBMISSIONS && coderComponent.isSubmitted()) {
            return new Results(false, "Multiple submissions are not allowed.");
        }

        if (coder instanceof TeamCoder) {
            trace.debug("coder instanceof TeamCoder == true");
            ComponentAssignmentData cad = ((TeamCoder) coder).getComponentAssignmentData();
            if (cad.getAssignedUserForComponent((int) componentID) != userID) {
                return new Results(false, "You are not assigned to this component.");
            }
        }

        if (!round.isLongContestRound()) {
            if (trace.isDebugEnabled()) trace.debug("UserState.canTestOrSubmit() = " + userState.canTestOrSubmit());
            if (!userState.canTestOrSubmit()) {
                trace.debug("In canSubmit(): You cannot submit unless you have successfully compiled first.");
                return new Results(false, "You cannot submit unless you have successfully compiled first.");
            }
        }

        if (!user.isLevelTwoAdmin() && hasCodingTimeExpiredForCoder(round, coder)) {
            trace.info("canSubmit(): Coding time expired for user : "+user.getID());
            return new Results(false, "You cannot submit after your time has expired");
        }
        return new Results(true, "");
    }


    /**
     * Checks for coding time expired for coder.
     *
     * @param round the round
     * @param coder the coder
     * @return true, if successful
     */
    private static boolean hasCodingTimeExpiredForCoder(Round round, Coder coder) {
        return CoreServices.hasCodingTimeExpiredForCoder(round, coder);
    }


    /**
     * Submits the current source for the users currently opened problem.
     *
     * @param connectionID the connection id
     * @param userID the user id
     * @param check the check
     * @param componentID the component id
     */
    static void submit(Integer connectionID, int userID, boolean check, int componentID) {
        Results canSub = canSubmit(userID, componentID);
        if (!canSub.isSuccess()) {
            ResponseProcessor.error(connectionID, canSub.getMsg());
            return;
        }

        UserState userState = UserState.getUserState(userID);
        User user = CoreServices.getUser(userID, false);
        info(user.getName() + " is attempting to submit " + componentID);
        boolean saveRoom = false;
        int roomID = user.getContestRoom();
        if (roomID == ContestConstants.INVALID_ROOM) {
            roomID = user.getRoomID();
        }
        Room baseRoom = CoreServices.getRoom(roomID, true);
        boolean mustAutoSystemTest = false;
        try {
            ContestRoom room = (ContestRoom) baseRoom;
            ContestRound round = (ContestRound) CoreServices.getContestRound(room.getRoundID());
            Coder coder = room.getCoder(userID);
            CoderComponent coderComponent = (CoderComponent) coder.getComponent(componentID);
            if (!user.isLevelTwoAdmin() && !ContestConstants.ACCEPT_MULTIPLE_SUBMISSIONS && coderComponent.isSubmitted()) {
                ResponseProcessor.error(connectionID, "Multiple submissions are not allowed.");
                return;
            }

            if (trace.isDebugEnabled()) trace.debug("UserState.canTestOrSubmit() = " + userState.canTestOrSubmit());
            if (check && coderComponent.isSubmitted()) {
                ResponseProcessor.submitProblem(connectionID, componentID);
                return;
            }

            userState.setCurrentBusyTime();
            if(!ContestConstants.isPracticeRoomType(room.getType()))
                specAppProcessor.submittingComponent(round, room, coder, coderComponent);


            Location location = new Location(coder.getContestID(), coder.getRoundID(), coder.getRoomID());
            RoundComponent component = CoreServices.getRoundComponent(round.getRoundID(), componentID, coder.getDivisionID());
            Submission sub = new Submission(location, component, coderComponent.getProgramText(), coderComponent.getLanguage());
            sub.setCoderId(userID);
            long time = getCurrentTime();
            sub.setSubmitTime(time);
            sub.setPointValue(coderComponent.getPointValue());
            //sub.setSelectedComponentID(componentID);

            SubmitResults results = CompileService.submit(sub, round);
            saveRoom = true;
            if (results.isSuccess()) {
                sub.setPointValue(results.getPoints());
                sub.setUpdatedPoints(coder.getPoints() + results.getPoints());
                doSubmit(coder, userID, coderComponent, sub, room, false);

                String message = "Submission was successful";
                if (round.getRoundProperties().usesScore()) {
                    String trimmedPointVal = Formatters.getDoubleString(sub.getPointValue());
                    message = message + " for " + trimmedPointVal + " points.";
                }

                //changed from popup to custom response to allow trapping of event
                //PopUpGenericResponse response = ResponseProcessor.simpleMessage(message, "Submission Results");

                //check if all problems are submitted, if so, send auto-test response
                ArrayList comps = round.getDivisionComponents(coder.getDivisionID());
                boolean bSystest = true;
                for(int i = 0; i < comps.size(); i++)  {
                    Integer compID = (Integer)comps.get(i);
                    BaseCoderComponent cc = coder.getComponent(compID.longValue());
                    if(!cc.isSubmitted())  {
                        bSystest = false;
                        break;
                    }
                }

                if(ContestConstants.isPracticeRoomType(room.getType()) || room.isAdminRoom()) {
                    bSystest = false;
                }

                if (round.isLongRound() && !ContestConstants.ACCEPT_MULTIPLE_SUBMISSIONS && bSystest) {
                    message = message + "\n\nTo view these system test results, select \"System Test Results\" on the summary screen\n";
                }

                SubmitResultsResponse response = new SubmitResultsResponse(message, coder.getRoundID(), bSystest);
                ResponseProcessor.process(connectionID, response);
                info("Submission update points " + sub.getUpdatedPoints() + " for " + user.getName());

                if(!ContestConstants.isPracticeRoomType(room.getType())) {
                    specAppProcessor.submittedComponent(round, room, coder, coderComponent);

                    //Since all goes OK, we schedule system tests for the submission
                    //Auto system test can be disabled. CoreServices take care of this
                    mustAutoSystemTest  = true;
                }
            } else {
                trace.debug("Sending error message from CoreServices.submit");
                ResponseProcessor.error(connectionID, results.getMsg());
            }
            userState.setCanTestOrSubmit(false);
            userState.resetBusyTime();
        } finally {
            if (saveRoom) {
                CoreServices.saveToCache(Room.getCacheKey(roomID), baseRoom);
            } else {
                CoreServices.releaseLock(Room.getCacheKey(roomID));
            }
        }
        try {
            if (mustAutoSystemTest) {
                CoreServices.autoSystemTest(roomID, userID, componentID);
            }
        } catch (Exception e) {
            trace.error("Failed to add auto system test", e);
        }
    }

    /**
     * Do submit.
     *
     * @param coder the coder
     * @param userID the user id
     * @param coderProblem the coder problem
     * @param sub the sub
     * @param room the room
     * @param replay the replay
     */
    private static void doSubmit(Coder coder, int userID, CoderComponent coderProblem, Submission sub, ContestRoom room, boolean replay) {

        /**
           Start Multiple Submit GT Stuff
        */
        int totalPoints = 0;
        long[] componentIDs = coder.getComponentIDs();
        int numProblems = componentIDs.length;
        if (trace.isDebugEnabled()) trace.debug("outnumProblems :" + numProblems);
        for (int i = 0; i < numProblems; i++) {
            double points = coder.getComponent(componentIDs[i]).getSubmittedValue();
            if (coderProblem.getComponentID() != componentIDs[i]) {
                totalPoints += points;
            }
            if (trace.isDebugEnabled()) trace.debug(" POINTS :" + points + " for " + i);
        }

        /**
           End Multiple Submit GT Stuff
        **/


        coder.setPoints(totalPoints + sub.getPointValue());
        coderProblem.setSubmittedValue(sub.getPointValue());
        coderProblem.setSubmittedProgramText(sub.getProgramText());
        coderProblem.setSubmittedLanguage(sub.getLanguage());
        coderProblem.setSubmittedTime(System.currentTimeMillis());
        coderProblem.setStatus(ContestConstants.NOT_CHALLENGED);

        CoderHistory hist = coder.getHistory();


        if (hist instanceof TeamCoderHistory && coder instanceof TeamCoder) {
            ((TeamCoderHistory) hist).addSubmission("" + sub.getRoundPointVal() , new java.sql.Date(sub.getSubmitTime()), sub.getPointValue(),
                                                    CoreServices.getUser(((TeamCoder) coder).getComponentAssignmentData().getAssignedUserForComponent(
                                                                                                                                                      coderProblem.getComponentID()), false).getName());
        } else {
            hist.addSubmission("" + sub.getRoundPointVal(), new java.sql.Date(sub.getSubmitTime()), sub.getPointValue());
        }
        updateLeaderBoard(coder.getRoundID(), room);

        String trimmedPointVal = Formatters.getDoubleString(sub.getPointValue());
        Round round = CoreServices.getContestRound(room.getRoundID());
        StringBuilder message = new StringBuilder(100);
        ComponentNameBuilder nameBuilder = round.getRoundType().getComponentNameBuilder();
        String componentName = nameBuilder.longNameForComponent(sub.getComponent().getClassName(), sub.getRoundPointVal(), round.getRoundProperties());

        message.append("System> ").append(CoreServices.getUser(userID, false).getName())
               .append(" has submitted the ").append(componentName);
        if (round.getRoundProperties().usesScore()) {
            message.append(" for ").append(trimmedPointVal).append(" points.");
        }
        message.append("\n");

        String status = trimmedPointVal + " points";

        //TODO -1 should maybe be problemID?
        ContestEvent event = new ContestEvent(room.getRoomID(), ContestEvent.SUBMIT_COMPONENT, message.toString(),
                                              userID, -1, coderProblem.getComponentID(), status);
        event.setTotalPoints(coder.getPoints());
        event.setSubmissionPoints(Formatters.getDouble(sub.getPointValue()).intValue());

        event.setEventTime(System.currentTimeMillis());
        event.setLanguage(coderProblem.getLanguage());
        EventService.sendGlobalEvent(event);


        // this event is for replay's sake only, TODO change this check to something valid
        if (!replay) {
            if (false) { // uncomment for replay
                ReplaySubmitEvent sevent = new ReplaySubmitEvent(userID, room.getRoomID(), coderProblem.getComponentID(), sub.getPointValue(), sub.getProgramText());
                EventService.sendReplayEvent(sevent);
            }
        }
    }


    /**
     * Update leader board.
     *
     * @param roundId the round id
     * @param room the room
     */
    private static void updateLeaderBoard(int roundId, BaseCodingRoom room) {
        if (!ContestConstants.isPracticeRoomType(room.getType()) && !room.isAdminRoom()) {
            if (room.updateLeader()) {
                LeaderBoard board = CoreServices.getLeaderBoard(roundId, true);
                try {
                    board.updateLeader(room);
                } finally {
                    CoreServices.saveToCache(board.getCacheKey(), board);
                }
                EventService.sendGlobalEvent(new LeaderEvent(room));
            }
        }
    }


    /**
     * Submits the given program text for a component in a Long Contest Round.
     *
     * @param connectionID the connection id
     * @param userID the user id
     * @param example the example
     * @param componentID the component id
     * @param languageID the language id
     * @param programText the program text
     */
    static void submitLong(Integer connectionID, int userID, boolean example, int componentID, int languageID, String programText) {
        Results canSub = canSubmit(userID, componentID);
        if (!canSub.isSuccess()) {
            ResponseProcessor.error(connectionID, canSub.getMsg());
            return;
        }

        UserState userState = UserState.getUserState(userID);
        User user = CoreServices.getUser(userID, false);
        info(user.getName() + " is attempting to submit " + componentID);

        int roomID = user.getContestRoom();
        if (roomID == ContestConstants.INVALID_ROOM) {
            roomID = user.getRoomID();
        }
        LongContestRoom room = (LongContestRoom) CoreServices.getRoom(roomID);
        Round round = CoreServices.getContestRound(room.getRoundID());
        Coder coder = room.getCoder(userID);
        LongCoderComponent coderComponent = (LongCoderComponent) coder.getComponent(componentID);

        if (trace.isDebugEnabled()) trace.debug("UserState.canTestOrSubmit() = " + userState.canTestOrSubmit());

        userState.setCurrentBusyTime();
        if(!ContestConstants.isPracticeRoomType(room.getType()))
            specAppProcessor.submittingComponent(round, room, coder, coderComponent);

        LongCompileRequest request = new LongCompileRequest(userID, componentID, round.getRoundID(), round.getContestID(),
                languageID, -1, programText, example);

        try {
            LongContestServicesLocator.getAsyncService(new LongSubmitAsyncHandler(round.getRoundID(), userID), 35000, null).submit(request);
        } catch (Exception e) {
            trace.error("Could not submit long: "+request, e);
            ResponseProcessor.error(connectionID, "An error occurred while submitting your code.");
            userState.resetBusyTime();
            return;
        }
    }


    /**
     * The Class LongSubmitAsyncHandler.
     */
    private static final class LongSubmitAsyncHandler implements AsyncServiceClientInvoker.AsyncResponseHandler {

        /** The round id. */
        private int roundId;

        /** The coder id. */
        private int coderId;

        /**
         * Instantiates a new long submit async handler.
         *
         * @param roundId the round id
         * @param coderId the coder id
         */
        public LongSubmitAsyncHandler(int roundId, int coderId) {
            this.roundId = roundId;
            this.coderId = coderId;
        }

        /* (non-Javadoc)
         * @see com.topcoder.server.ejb.asyncservices.AsyncServiceClientInvoker.AsyncResponseHandler#timeout(java.lang.Object)
         */
        public void timeout(Object responseId) {
            trace.warn("Async Long Submit timeout: roundId="+roundId+" coderId="+coderId);
        }

        /* (non-Javadoc)
         * @see com.topcoder.server.ejb.asyncservices.AsyncServiceClientInvoker.AsyncResponseHandler#succeeded(java.lang.Object, java.lang.Object)
         */
        public void succeeded(Object responseId, Object object) {
            LongCompileResponse response = (LongCompileResponse) object;
            handleAsyncSubmitResult(response.getCompileStatus(), response.getCompileError());
        }

        /* (non-Javadoc)
         * @see com.topcoder.server.ejb.asyncservices.AsyncServiceClientInvoker.AsyncResponseHandler#invocationFailed(java.lang.Object, java.lang.Exception)
         */
        public void invocationFailed(Object responseId, Exception e) {
            trace.error("Async Long Submit Failed: roundId="+roundId+" coderId="+coderId, e);
            handleFailure("An error occurred while submitting your code.");
        }

        /* (non-Javadoc)
         * @see com.topcoder.server.ejb.asyncservices.AsyncServiceClientInvoker.AsyncResponseHandler#exceptionThrown(java.lang.Object, java.lang.Exception)
         */
        public void exceptionThrown(Object responseId, Exception e) {
            trace.error("Async Long Submit Failed: roundId="+roundId+" coderId="+coderId, e);
            if (e instanceof LongContestServicesException) {
                handleFailure(MessageProvider.getText(((LongContestServicesException) e).getLocalizableMessage()));
            } else {
                handleFailure("An error occurred while submitting your code.");
            }
        }

        /* (non-Javadoc)
         * @see com.topcoder.server.ejb.asyncservices.AsyncServiceClientInvoker.AsyncResponseHandler#asyncServiceFailure(java.lang.Object)
         */
        public void asyncServiceFailure(Object responseId) {
            trace.error("Async service failure: roundId="+roundId+" coderId="+coderId);
            handleFailure("An error occurred while submitting your code.");
        }

        /**
         * Handle failure.
         *
         * @param message the message
         */
        public void handleFailure(String message) {
            handleAsyncSubmitResult(false, message);
        }

        /**
         * Handle async submit result.
         *
         * @param status the status
         * @param message the message
         */
        public void handleAsyncSubmitResult(boolean status, String message) {
            UserState userState = UserState.getUserState(coderId);
            if (userState != null) {
                Integer connectionID = RequestProcessor.getConnectionID(coderId);
                if (connectionID != null) {
                    if (status) {
                        String msg = "Submission was successful\n"+message;
                        SubmitResultsResponse response = new SubmitResultsResponse(msg, roundId, false);
                        ResponseProcessor.process(connectionID, response);
                    } else {
                        trace.debug("Sending error message from CoreServices.submitLong");
                        ResponseProcessor.error(connectionID, message);
                    }
                }
                userState.resetBusyTime();
            }
        }
    }

    /**
     * View long queue status.
     *
     * @param connectionID the connection id
     */
    public static void viewLongQueueStatus(Integer connectionID) {
        info("Long queue status");
        try {
            boolean hasItems = false;
            StringBuilder sb = new StringBuilder(1000);
            List status = LongContestServicesLocator.getService().getLongTestQueueStatus();
            int systemTestCount = 0;
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
            sb.append("<html>");
            sb.append("<table width=\"100%\" border=\"1\" cellpadding=\"2\" cellspacing=\"0\">");
            sb.append("<tr>");
            sb.append("<td>Handle</td>");
            sb.append("<td>Contest</td>");
            sb.append("<td>Language</td>");
            sb.append("<td align=\"center\">Entered Queue</td>");
            sb.append("<td align=\"right\">Type</td>");
            sb.append("<td align=\"right\">Tests Remaining</td>");
            sb.append("</tr>");
            for (Iterator it = status.iterator(); it.hasNext(); ) {
                LongTestQueueStatusItem item = (LongTestQueueStatusItem) it.next();
                if (item.getTestAction() == ServicesConstants.LONG_SYSTEM_TEST_ACTION) {
                    systemTestCount +=  item.getCount();
                } else {
                    sb.append("<tr>");
                    sb.append("<td>").append(CoreServices.getUser(item.getUserId()).getName()).append("</td>");
                    sb.append("<td>").append(item.getDisplayName()).append("</td>");
                    sb.append("<td>").append(item.getLanguageName()).append("</td>");
                    sb.append("<td align=\"center\">").append(dateFormat.format(item.getQueueDate())).append("</td>");
                    sb.append("<td align=\"right\">").append(item.getSubmissionType()).append("</td>");
                    sb.append("<td align=\"right\">").append(item.getCount()).append("</td>");
                    sb.append("</tr>");
                    hasItems = true;
                }
            }
            if (!hasItems) {
                sb.append("<tr><td colspan=\"6\">There are currently no submissions in the queue.</td></tr>");
            }
            sb.append("</table>");
            if (systemTestCount > 0) {
                sb.insert(6, "<b>System Tests Remaining: "+systemTestCount+"</b><br>");
            }
            sb.append("</html>");
            ResponseProcessor.process(connectionID, ResponseProcessor.simpleBigMessage(sb.toString(), "Queue Status"));
        } catch (Exception e) {
            trace.error("Failed to obtain queue status", e);
            ResponseProcessor.error(connectionID, "Failed to obtain queue status");
        }
    }

    /**
     * Notify long submission.
     *
     * @param roundId the round id
     * @param coderId the coder id
     * @param componentId the component id
     * @param example the example
     */
    static void notifyLongSubmission(int roundId, int coderId, int componentId, boolean example) {
        info("Long submission roundId=" + roundId  + " for coderId=" + coderId+ " example="+example);

        LongContestRound round = (LongContestRound) CoreServices.getContestRound(roundId);
        Integer assignedRoom = round.getAssignedRoom(coderId);
        if (assignedRoom == null) {
            trace.error("Received long submission event and no room is assigned");
            return;
        }
        int roomId = assignedRoom.intValue();
        LongCoderComponent sourceCoderComp;
        try {
            sourceCoderComp = DBServicesLocator.getService().getLongCoderComponent(round.getRoundID(), coderId, componentId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        BaseCodingRoom baseRoom  = CoreServices.getContestRoom(roomId, true);
        if (baseRoom == null) {
            trace.error("Received long submission event and assigned room does not exists");
            return;
        }
        Coder coder = null;
        boolean save = false;
        LongContestRoom room;
        try {
            room = (LongContestRoom) baseRoom;
            coder = room.getCoder(coderId);
            LongCoderComponent destCoderComp = (LongCoderComponent) coder.getComponent(componentId);
            destCoderComp.updateFrom(sourceCoderComp);
            updateLeaderBoard(roundId, room);
            save = true;
        } finally {
            if (save) {
                CoreServices.saveToCache(Room.getCacheKey(roomId), baseRoom);
            } else {
                CoreServices.releaseLock(Room.getCacheKey(roomId));
            }
        }

        if(!ContestConstants.isPracticeRoomType(room.getType())) {
            if (!example) {
                specAppProcessor.submittedComponent(round, room, coder, sourceCoderComp);
            } else {
                specAppProcessor.testingComponent(round, room, coder, sourceCoderComp);
            }
        }
        String message = "System> " + coder.getName() + " has made "+(example ? "an example" : "a full")+" submission\n";
        int eventType = example ? ContestEvent.TEST_COMPONENT : ContestEvent.SUBMIT_COMPONENT;
        ContestEvent event = new ContestEvent(room.getRoomID(), eventType, message, coder.getID(), -1, sourceCoderComp.getComponentID(), null);
        EventService.sendGlobalEvent(event);

        //spectator send new data out
        specAppProcessor.submittingLongComponent(round, room, coder, sourceCoderComp);
    }

    /**
     * Update long scores.
     *
     * @param scores the scores
     */
    public static void updateLongScores(LongRoundOverallScore scores) {
        info("Overall scores updated roundId=" + scores.getRoundId() + " componentId=" + scores.getComponentId());

        LongContestRound round = (LongContestRound) CoreServices.getContestRound(scores.getRoundId());
        if (round.getMainRoomId() == null) {
            trace.error("Received long score event and no room is assigned");
            return;
        }
        int roomId = round.getMainRoomId().intValue();
        BaseCodingRoom baseRoom  = CoreServices.getContestRoom(roomId, true);
        if (baseRoom == null) {
            trace.error("Received long score event submission event and assigned room does not exists");
            return;
        }

        boolean save = false;
        LongContestRoom room;
        try {
            room = (LongContestRoom) baseRoom;
            List scoreValues = scores.getScores();
            for (Iterator it = scoreValues.iterator(); it.hasNext();) {
                LongRoundOverallScore.ScoreEntry entry = (LongRoundOverallScore.ScoreEntry) it.next();
                Coder coder = room.getCoder(entry.getCoderId());
                if (coder != null) {
                    BaseCoderComponent component = coder.getComponent(scores.getComponentId());
                    if (component != null) {
                        component.setSubmittedValue((int) Math.round(entry.getScore()*100));
                    }
                }
            }
            save = true;
            updateLeaderBoard(scores.getRoundId(), room);
        } finally {
            if (save) {
                CoreServices.saveToCache(Room.getCacheKey(roomId), baseRoom);
            } else {
                CoreServices.releaseLock(Room.getCacheKey(roomId));
            }
        }

        if(!ContestConstants.isPracticeRoomType(room.getType())) {
           specAppProcessor.updateLongScores(round, room, scores.getComponentId());
        }
        ContestEvent event = new ContestEvent(room.getRoomID(), ContestEvent.SCORES_UPDATED, null, RequestProcessor.INVALID_USER, 0, scores.getComponentId(), null);
        EventService.sendGlobalEvent(event);
    }

    /**
     * Notify registration.
     *
     * @param roundId the round id
     * @param coderId the coder id
     */
    public static void notifyRegistration(int roundId, int coderId) {
        info("Registration roundId=" + roundId + " coderId="+coderId);
        Round round = CoreServices.getContestRound(roundId);
        handleRegistrationResult(true, RequestProcessor.getConnectionID(coderId), "You have successfully registered for the match "+round.getContestName(), roundId, coderId);
    }


    /**
     * Handle coder added to room.
     *
     * @param users the users
     * @param round the round
     * @param roomId the room id
     */
    private static void handleCoderAddedToRoom(User[] users, BaseRound round, int roomId) {
        int roundId = round.getRoundID();

        if (CoreServices.getRoomFromCache(roomId) == null) {
            return;
        }
        Room baseRoom = CoreServices.getRoom(roomId, true);
        try {
            BaseCodingRoom room = (BaseCodingRoom) baseRoom;
            for (int i = 0; i < users.length; i++) {
                User user = users[i];
                Coder coder = CoderFactory.createCoder(user.getID(), user.getName(), room.getDivisionID(),
                    round, room.getRoomID(), user.getRating(room.getRatingType()).getRating(), user.getLanguage());
                room.addCoder(coder);
            }
            updateLeaderBoard(roundId, room);
        } finally {
            CoreServices.releaseLock(baseRoom.getCacheKey());
        }
        Iterator connectionsIterator = Processor.getConnectionIDs(TCEvent.ROOM_TARGET, baseRoom.getRoomID());
        Set connections = new HashSet();
        while (connectionsIterator.hasNext()) {
            connections.add(connectionsIterator.next());
        }
        ArrayList watchConnections = getWatchConnections(baseRoom.getRoomID());

        ResponseProcessor.addCoderToRoom(connections.iterator(), (BaseCodingRoom) baseRoom, baseRoom.getType());
        watchConnections.removeAll(connections);
        if (watchConnections.size() > 0) {
            ResponseProcessor.addCoderToRoom(watchConnections.iterator(), (BaseCodingRoom) baseRoom, ContestConstants.WATCH_ROOM);
        }
    }

    /**
     * Notify test completed.
     *
     * @param roundId the round id
     * @param coderId the coder id
     * @param componentId the component id
     * @param submissionNumber the submission number
     * @param example the example
     */
    public static void notifyTestCompleted(int roundId, int coderId, int componentId, int submissionNumber, boolean example) {
        info("TestCompleted for roundId=" + roundId + " coderId="+coderId+" componentId=" + componentId+" subnum="+submissionNumber+" example="+example);
        Round round = CoreServices.getContestRound(roundId);
        if (round == null) {
            trace.error("Got a null round for testCompleted: " +roundId);
            return;
        }
        Integer roomId = round.getAssignedRoom(coderId);
        if (roomId == null) {
            trace.error("Got a null room for testCompleted: " +roundId);
            return;
        }
        Room baseRoom = CoreServices.getRoom(roomId.intValue(), true);
        BaseCoderComponent component;
        Coder coder;
        try {
            LongContestRoom room = (LongContestRoom) baseRoom;
            coder = room.getCoder(coderId);
            if (coder == null) {
                trace.error("Got a null coder for testCompleted: " +coderId);
                return;
            }
            component = coder.getComponent(componentId);
            if (component == null) {
                trace.error("Got a null coder component for testCompleted: " +componentId);
                return;
            }
            component.setStatus(ContestConstants.SYSTEM_TEST_SUCCEEDED);
        } finally {
            CoreServices.saveToCache(Room.getCacheKey(roomId.intValue()), baseRoom);
        }

        ContestEvent event = new ContestEvent(roomId.intValue(), ContestEvent.TEST_COMPLETED, null, coderId, 0, componentId, null);
        EventService.sendGlobalEvent(event);

        specAppProcessor.processedComponent(round, (BaseCodingRoom)baseRoom, coder, (LongCoderComponent)component, example);

        //If it is an example and it is connected, we notify result availability to the user
        if (!example || !CoreServices.isLoggedIn(new Integer(coderId))) {
            return;
        }
        ResponseProcessor.sendMessageToCoderId(coderId, ResponseProcessor.simpleMessage("Example results of example submission "+submissionNumber+" are ready" , "Example Results"));
    }

    /**
     * Notify component opened.
     *
     * @param roundId the round id
     * @param coderId the coder id
     * @param componentId the component id
     * @param openTime the open time
     */
    static void notifyComponentOpened(int roundId, int coderId, int componentId, long openTime) {
        LongContestRound round = (LongContestRound) CoreServices.getContestRound(roundId);
        Coder coder = CoreServices.getContestRoom(round.getAssignedRoom(coderId).intValue(), false).getCoder(coderId);
        if (coder == null) {
            if (round.getRoundType().isPracticeRound()) {
                User user = CoreServices.getUser(coderId);
                handleCoderAddedToRoom(new User[]{user}, round, round.getMainRoomId().intValue());
            }
        }
        Room baseRoom = CoreServices.getRoom(round.getAssignedRoom(coderId).intValue(), true);
        boolean unlock = true;
        try {
            BaseCodingRoom room = (BaseCodingRoom) baseRoom;
            SimpleComponent comp = CoreServices.getSimpleComponent(componentId);
            coder = room.getCoder(coderId);
            if (coder != null) {
                unlock = false;
                CoreServices.handleComponentOpened(coder, room, comp, openTime);
            }
        } finally {
            if (unlock) {
                CoreServices.releaseLock(baseRoom.getCacheKey());
            }
        }
    }

    /**
     * Notify saved.
     *
     * @param roundId the round id
     * @param coderId the coder id
     * @param componentId the component id
     * @param source the source
     * @param language the language
     */
    static void notifySaved(int roundId, int coderId, int componentId, String source, int language) {
        Round round = CoreServices.getContestRound(roundId);
        Room baseRoom = CoreServices.getRoom(round.getAssignedRoom(coderId).intValue(), true);
        boolean unlock = true;
        try {
            BaseCodingRoom room = (BaseCodingRoom) baseRoom;
            Coder coder = room.getCoder(coderId);
            BaseCoderComponent coderComponent = coder.getComponent(componentId);
            coderComponent.setProgramText(source);
            coderComponent.setLanguage(language);
            unlock = false;
            CoreServices.saveToCache(room.getCacheKey(), room);
        } finally {
            if (unlock) {
                CoreServices.releaseLock(baseRoom.getCacheKey());
            }
        }
    }

    /**
     * Sends the coder history for a user in their room.
     *
     * @param connectionID the connection id
     * @param userID the user id
     * @param roomID the room id
     * @param challengeID the challenge id
     * @return the coder history
     */
    static void getCoderHistory(Integer connectionID, int userID, int roomID, int challengeID) {
        if (trace.isDebugEnabled()) trace.debug(" getCoderHistory on RoomID: " + roomID + " challengeID " + challengeID);
        User user = CoreServices.getUser(userID);
        BaseCodingRoom room = (BaseCodingRoom) CoreServices.getRoom(roomID, false);
        Round round = CoreServices.getContestRound(room.getRoundID());
        if (!user.isLevelTwoAdmin() && !round.getRoundProperties().isCoderHistoryEnabled()) {
            BaseResponse response = ResponseProcessor.simpleBigMessage("Coder history is not enabled in this kind of round", "Coder History");
            ResponseProcessor.process(connectionID, response);
            return;
        }
        if (!user.isLevelTwoAdmin() && !round.getRoundProperties().isSummaryEnabledDuringContest() && round.getPhase() < ContestConstants.CONTEST_COMPLETE_PHASE) {
            BaseResponse response = ResponseProcessor.simpleBigMessage("Coder history is not enabled until the end of the round", "Coder History");
            ResponseProcessor.process(connectionID, response);
            return;
        }
        if (!user.isLevelTwoAdmin() && !round.getRoundProperties().getShowScoresOfOtherCoders().booleanValue() && userID != challengeID) {
            BaseResponse response = ResponseProcessor.simpleBigMessage("You are only allowed to see your own coder history.", "Coder History");
            ResponseProcessor.process(connectionID, response);
            return;
        }
        Coder coder = room.getCoder(challengeID);
        if (coder != null) {
            List data = new ArrayList();
            if  (!round.isLongContestRound()) {
                CoderHistory history = coder.getHistory();
                // Add all submission history
                for (Iterator iter = history.getSubmissions().iterator(); iter.hasNext();) {
                    CoderHistory.SubmissionData submission = (CoderHistory.SubmissionData) iter.next();
                    UserListItem item = new UserListItem(coder.getName(), coder.getRating());
                    data.add(new CoderHistoryData(submission.getDate(), item, Integer.parseInt(submission.getComponentValue()), CoderHistoryData.ACTION_SUBMIT, submission.getPoints() / 100.0, submission.getDetail()));
                }
                // Add all challenge history
                for (Iterator iter = history.getChallenges().iterator(); iter.hasNext();) {
                    CoderHistory.ChallengeData challenge = (CoderHistory.ChallengeData) iter.next();
                    Coder otherCoder = room.getCoder(challenge.getOtherUserID());
                    UserListItem item = otherCoder != null ? new UserListItem(otherCoder.getName(), otherCoder.getRating()) : new UserListItem(challenge.getOtherUser().getName(), challenge.getOtherUser().getRating());
                    data.add(new CoderHistoryData(challenge.getDate(), item, coder.getComponent(challenge.getComponentID()).getPointValue(), challenge.isChallenger() ? CoderHistoryData.ACTION_CHALLENGE : CoderHistoryData.ACTION_DEFEND, challenge.getPoints() / 100.0, challenge.getDetail()));
                }
                // Add all system tests
                for (Iterator iter = history.getSystemTests().iterator(); iter.hasNext();) {
                    CoderHistory.TestData test = (CoderHistory.TestData) iter.next();
                    data.add(new CoderHistoryData(test.getTimestamp(), new UserListItem("System Tester", -1), Integer.parseInt(test.getProblemVal()), CoderHistoryData.ACTION_TEST, test.getDeductAmt() / 100.0, test.getDetail()));
                }
            } else {
                try {
                    LongCoderHistory coderHistory = null;
                    UserListItem item = new UserListItem(coder.getName(), coder.getRating());
                    if(round instanceof ForwarderLongContestRound) {
                        coderHistory = RoundForwarderProcessor.getCoderHistory(round.getRoundID(), coder.getID());
                    } else {
                        coderHistory = LongContestServicesLocator.getService().getCoderHistory(round.getRoundID(), coder.getID());
                    }
                    // Full submissions
                    LongSubmissionData[] subs = coderHistory.getFullSubmissions();
                    for (int i=0;i<subs.length;++i) {
                        LongSubmissionData sub = subs[i];
                        data.add(new CoderHistoryData(sub.getTimestamp(), item, -1, sub.hasPendingTests() ? CoderHistoryData.ACTION_FULL_PENDING : CoderHistoryData.ACTION_FULL, sub.getScore(), "Number:" + sub.getNumber() + ", Language:" + BaseLanguage.getLanguage(sub.getLanguageId()).getName()));
                    }

                    // Example submissions
                    subs = coderHistory.getExampleSubmissions();
                    for (int i=0;i<subs.length;++i) {
                        LongSubmissionData sub = subs[i];
                        data.add(new CoderHistoryData(sub.getTimestamp(), item, -1, sub.hasPendingTests() ? CoderHistoryData.ACTION_EXAMPLE_PENDING : CoderHistoryData.ACTION_EXAMPLE, Double.NaN, "Number:" + sub.getNumber() + ", Language:" + BaseLanguage.getLanguage(sub.getLanguageId()).getName()));
                    }
                } catch (Exception e) {
                    trace.error("Could not obtain coder history", e);
                }
            }
            CoderHistoryResponse response = new CoderHistoryResponse(coder.getName(), round.isLongContestRound(), (CoderHistoryData[]) data.toArray(new CoderHistoryData[0]));
            ResponseProcessor.process(connectionID, response);
        } else {
            trace.error("Got a null coder for challengeID: " + challengeID + " on room: " + roomID);
        }
    }



    /**
     * Sends the coder history for a user in their room.
     *
     * @param connectionID the connection id
     * @param roomID the room id
     * @param challengeID the challenge id
     * @param example the example
     * @return the submission history
     */
    static void getSubmissionHistory(Integer connectionID, int roomID, int challengeID, boolean example) {
        if (trace.isDebugEnabled()) trace.debug(" getSubmissionHistory on RoomID: " + roomID + " challengeID " + challengeID+ " example:"+example);
        BaseCodingRoom room = (BaseCodingRoom) CoreServices.getRoom(roomID, false);
        Round round = CoreServices.getContestRound(room.getRoundID());
        Coder coder = room.getCoder(challengeID);
        if (coder != null) {
            LongCoderHistory coderHistory = null;
            LongSubmissionData[] submissions;
            if(round instanceof ForwarderLongContestRound) {
                coderHistory = RoundForwarderProcessor.getCoderHistory(round.getRoundID(), coder.getID());
            } else {
                try {
                    coderHistory = LongContestServicesLocator.getService().getCoderHistory(round.getRoundID(), coder.getID());
                } catch (Exception e) {
                    trace.error("Could not obtain coder history", e);
                    ResponseProcessor.error(connectionID,  "Failed to obtain coder history");;
                }
            }
            try {
                if (example) {
                    submissions = coderHistory.getExampleSubmissions();
                } else {
                    submissions = coderHistory.getFullSubmissions();
                }
                BaseResponse response = ResponseProcessor.submissionHistoryResponse(
                            round.getRoundID(),
                            coder.getName(),
                            (int) coder.getComponentIDs()[0],
                            example,
                            submissions);
                ResponseProcessor.process(connectionID, response);
            } catch (Exception e) {
                trace.error("Could not obtain coder history", e);
                ResponseProcessor.error(connectionID,  "Failed to obtain coder history");;
            }

        } else {
            trace.error("Got a null coder for challengeID: " + challengeID + " on room: " + roomID);
        }
    }

    /**
     * Builds the long coder history.
     *
     * @param round the round
     * @param coder the coder
     * @return the string
     */
    private static String buildLongCoderHistory(Round round, Coder coder) {
        try {
            LongCoderHistory coderHistory = null;
            if(round instanceof ForwarderLongContestRound) {
                coderHistory = RoundForwarderProcessor.getCoderHistory(round.getRoundID(), coder.getID());
            } else {
                coderHistory = LongContestServicesLocator.getService().getCoderHistory(round.getRoundID(), coder.getID());
            }

            StringBuilder sb = new StringBuilder(10000);
            DecimalFormat scoreFormat = new DecimalFormat("0.00");
            DateFormat dateFormat = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
            sb.append("Full Submissions\n");
            sb.append("Number              Date             Score                Language\n");
            sb.append("---------------------------------------------------------------------------\n");
            LongSubmissionData[] subs = coderHistory.getFullSubmissions();
            if (subs.length == 0) {
                sb.append("No full submission made\n");
            } else {
                for (int i = 0; i < subs.length; i++) {
                    LongSubmissionData sub = subs[i];
                    sb.append(sub.hasPendingTests()? "*" : " ");
                    sb.append(StringUtil.padLeft(String.valueOf(sub.getNumber()),5)).append("      ");
                    sb.append(dateFormat.format(sub.getTimestamp())).append("      ");
                    sb.append(StringUtil.padLeft(scoreFormat.format(sub.getScore()),15)).append("      ");
                    sb.append(BaseLanguage.getLanguage(sub.getLanguageId()).getName());
                    sb.append("\n");
                }
            }
            sb.append("\nExample Submissions\n");
            sb.append("Number              Date            Language\n");
            sb.append("--------------------------------------------------------------\n");
            subs = coderHistory.getExampleSubmissions();
            if (subs.length == 0) {
                sb.append("No example submission made\n");
            }
            for (int i = 0; i < subs.length; i++) {
                LongSubmissionData sub = subs[i];
                sb.append(sub.hasPendingTests()? "*" : " ");
                sb.append(StringUtil.padLeft(String.valueOf(sub.getNumber()),6)).append("     ");
                sb.append(dateFormat.format(sub.getTimestamp())).append("     ");
                sb.append(BaseLanguage.getLanguage(sub.getLanguageId()).getName());
                sb.append("\n");
            }
            sb.append("\n\n\n\n* Submission has pending tests.");
            return sb.toString();
        } catch (Exception e) {
            trace.error("Could not obtain coder history", e);
            return "Failed to obtain coder history";
        }
    }


    /**
     * Gets the long test results.
     *
     * @param connectionID the connection id
     * @param requestingUserId the requesting user id
     * @param coderId the coder id
     * @param roomID the room id
     * @param componentID the component id
     * @param resultsType the results type
     * @return the long test results
     */
    static void getLongTestResults(Integer connectionID, int requestingUserId, int coderId, int roomID, int componentID, int resultsType) {
        if (trace.isDebugEnabled()) trace.debug(" getLongTestResult on RoomID: " + roomID + " coderId " + coderId + " resultType=" + resultsType);

        if (roomID == ContestConstants.INVALID_ROOM) {
            trace.error("getLongTestResults user: " + requestingUserId + " invalid room. Connection: " + connectionID);
            ResponseProcessor.error(connectionID, "Server error getting example results");
            return;
        }
        User viewingUser = CoreServices.getUser(requestingUserId);
        LongContestRoom room = (LongContestRoom) CoreServices.getContestRoom(roomID, false);
        if (!room.isUserAssigned(coderId)) {
            trace.error("Coder not in room with ID = " + coderId + " NumCoders = " + room.getNumCoders() + " RoomID = " + roomID);
            return;
        }
        Round round = CoreServices.getContestRound(room.getRoundID());
        Coder coder = room.getCoder(coderId);

        int activePhase = round.getPhase();
        boolean valid = (ContestConstants.INTERMISSION_PHASE < activePhase && activePhase <= ContestConstants.CONTEST_COMPLETE_PHASE && resultsType == 0)
                || (activePhase == ContestConstants.CONTEST_COMPLETE_PHASE && resultsType == 2)
                || (ContestConstants.isPracticeRoomType(room.getType()) && !room.isAdminRoom())
                || viewingUser.isLevelTwoAdmin()
                || activePhase == ContestConstants.INACTIVE_PHASE
                || coderId == requestingUserId;

        if (!valid) {
            ResponseProcessor.error(connectionID, "You cannot view test results here.");
            return;
        }
        if (coder.getComponent(componentID) == null) {
            trace.error("Invalid component on long test results, componentID="+componentID +" from connection="+connectionID);
            return;
        }

        LongTestResult[] results;
        try {
            results = LongContestServicesLocator.getService().getLongTestResults(round.getRoundID(), coderId, componentID, resultsType);
        } catch (LongContestServicesException e) {
            trace.error("Could not obtain test results history", e);
            ResponseProcessor.error(connectionID, "Failed to obtain test results: "+MessageProvider.getText(e.getLocalizableMessage()));
            return;
        } catch (Exception e) {
            trace.error("Could not obtain test results history", e);
            ResponseProcessor.error(connectionID, "Failed to obtain test results");
            return;
        }
        BaseResponse response = ResponseProcessor.longTestResultsResponse(round.getRoundID(), coder.getName(), componentID, results, resultsType);
        ResponseProcessor.process(connectionID, response);
    }

    /**
     * Helper function to check if a user can register.
     *
     * @param userID the user id
     * @param contest         the round contest.
     * @param registration         the registration.
     * @param message         the message info.
     * @return true = the user can registered
     */
    private static boolean canRegister(int userID, Round contest, Registration registration, StringBuilder message) {
        boolean canRegister = false;
        if (trace.isDebugEnabled()) trace.debug("SEASON: " + contest.getSeason());
        // to check the event_registration status
        /**
         * If there are any options on where to insert these checks,
         * the preference is to do that as early as possible
         * (to avoid ask member to fill in anything if this member can't register anyway)
         */
        RoundEvent eventData = contest.getRoundEvent();

        if ((eventData != null) && (eventData.getEventId() > 0)) {
            EventRegistration evd = CoreServices.getEventRegistration(userID, eventData.getEventId());

            if ((evd == null) || (evd.getUserId() < 0) || (evd.getEventId() < 0)) {
                message.append("In order to participate in this competition, you must register for \n")
                           .append(eventData.getEventName()).append("\nRegistration is available at: \n")
                           .append(eventData.getRegistrationUrl()).append("\n")
                           .append("Please register at the provided URL first and then repeat registration at the Arena.");

                return canRegister;
            } else if (evd.getEligibleInd() != ELIGIBLE_TO_REGISTER) {
                message.append(NOT_ELIGIBLE_TO_REGISTER);

                return canRegister;
            }
        }

        if (!contest.inRegistration()) {
            message.append("Registration is not open.");
        } else if (registration.isContestFull()) {
            message.append("There are no more spots available for " + contest.getContestName() + ".");
        } else if (registration.isRegistered(userID)) {
            message.append("You are already registered for " + contest.getContestName() + ".");
        } else if (registration.isInvitationOnly() && !registration.isInvited(userID)) {
            message.append("Registration in this competition is by invitation only.");
        } else if (contest.getRoundType().isTeamRound() && !CoreServices.getUser(userID, false).isOnTeam()) {
            message.append("You must be on a team to register for a team contest.");
        } else if (contest.getRoundType().isTeamRound()
                   && CoreServices.getTeam(CoreServices.getUser(userID, false).getTeamID(), false).getTeamTypeID() == TeamConstants.PRACTICE_TEAM) {
            message.append("Practice team members cannot register for a team contest.");
        } else if (s_highSchoolCompetition && !CoreServices.doesUserHaveCoach(userID)) {
            message.append("There is no coach registered for your school. A coach much register from your school in order for you to compete.");
        } else if (s_highSchoolCompetition && CoreServices.isCoach(userID)) {
            message.append("Coaches are not eligible to compete." );
        } else if (contest.getRoundTypeId() == ContestConstants.SRM_ROUND_TYPE_ID && !CoreServices.getUser(userID,false).isCompetitionUser()) {
            message.append("You must be a registered competition user to compete in this round.");
        } else if (contest.getRoundTypeId() == ContestConstants.SRM_QA_ROUND_TYPE_ID && !CoreServices.getUser(userID,false).isCompetitionUser()) {
            message.append("You must be a registered competition user to compete in this round.");
        } else if (contest.getRoundTypeId() == ContestConstants.HS_SRM_ROUND_TYPE_ID && !CoreServices.getUser(userID,false).isHSCompetitionUser()) {
            message.append("You must be a registered high school competition user to compete in this round.");
        } else if (contest.getRoundTypeId() == ContestConstants.TOURNAMENT_ROUND_TYPE_ID && !CoreServices.getUser(userID,false).isCompetitionUser()) {
            message.append("You must be a registered competition user to compete in this round.");
        } else if (contest.getRoundTypeId() == ContestConstants.INTRO_EVENT_ROUND_TYPE_ID && !CoreServices.getUser(userID,false).isCompetitionUser()) {
            message.append("You must be a registered competition user to compete in this round.");
        } else if (contest.getRoundTypeId() == ContestConstants.HS_TOURNAMENT_ROUND_TYPE_ID && !CoreServices.getUser(userID,false).isHSCompetitionUser()) {
            message.append("You must be a registered high school competition user to compete in this round.");
        } else if (contest.getRoundTypeId() == ContestConstants.LONG_PROBLEM_ROUND_TYPE_ID && !CoreServices.getUser(userID,false).isCompetitionUser()) {
            message.append("You must be a registered competition user to compete in this round.");
        } else if (contest.getRoundTypeId() == ContestConstants.LONG_PROBLEM_QA_ROUND_TYPE_ID && !CoreServices.getUser(userID,false).isCompetitionUser()) {
            message.append("You must be a registered competition user to compete in this round.");
        } else if (contest.getRoundTypeId() == ContestConstants.LONG_PROBLEM_TOURNAMENT_ROUND_TYPE_ID && !CoreServices.getUser(userID,false).isCompetitionUser()) {
            message.append("You must be a registered competition user to compete in this round.");
        } else if ((contest.getRoundTypeId() == ContestConstants.HS_SRM_ROUND_TYPE_ID || contest.getRoundTypeId() == ContestConstants.HS_TOURNAMENT_ROUND_TYPE_ID) && !CoreServices.getUser(userID, false).hasSeason(contest.getSeason())) {
            message.append("You must be a registered high school competition user for this season to compete in this round.");
        } else if (contest.getRoundTypeId() == ContestConstants.EDUCATION_ALGO_ROUND_TYPE_ID) {
            message.append("You cannot register for the round.");
        } else if (contest.getRoundTypeId() == ContestConstants.AMD_LONG_PROBLEM_ROUND_TYPE_ID && !CoreServices.getUser(userID,false).isCompetitionUser()) {
            message.append("You must be a registered competition user to compete in this round.");
        } else {
            if (!contest.inRegistration()) {
                message.append("Registration is not open.");
            } else {
                canRegister = true;
            }
        }
        return canRegister;
    }

    /**
     * Sends the information for registering to the user (survey questions, legalese, etc).
     * Checks if they are allowes to register in the first place as well.
     *
     * @param connectionID the connection id
     * @param userID the user id
     * @param roundID the round id
     */
    static void registerInfo(Integer connectionID, int userID, int roundID) {
        User u = CoreServices.getUser(userID, false);
        if (u.isGuest()) {
            ResponseProcessor.error(connectionID, "Guests cannot register.");
            return;
        }
        /**
         * in order to check the user active status,
         * we must always get the User from database.
         */
        User ckActiveUser = CoreServices.getUserFromDB(userID);
        if(!ckActiveUser.isUserActive()) {
            ResponseProcessor.error(connectionID, NOT_ELIGIBLE_TO_REGISTER);
            return;
        }

        if (!CoreServices.isRoundActive(roundID)) {
            trace.error("Invalid round [" + roundID + "] in registerInfo");
            return;
        }

        Round contest = CoreServices.getContestRound(roundID);
        if (contest == null) {
            trace.error("Null round [" + roundID + "] in registerInfo");
            return;
        }

        Registration registration = CoreServices.getRegistration(contest.getRoundID());
        StringBuilder message = new StringBuilder();

        if (canRegister(userID, contest, registration, message)) {
            ResponseProcessor.registerInfo(connectionID, contest, registration);
        } else {
            PopUpGenericResponse response = ResponseProcessor.simpleMessage(message.toString(), "Event Registration");
            ResponseProcessor.process(connectionID, response);
        }
    }

    /**
     * Performs registration for the given user with the specified survey answers.
     *
     * @param connectionID the connection id
     * @param userID the user id
     * @param surveyData the survey data
     * @param roundID the round id
     */
    static void register(Integer connectionID, int userID, ArrayList surveyData, int roundID) {
        User u = CoreServices.getUser(userID, false);
        if (u.isGuest()) {
            ResponseProcessor.error(connectionID, "Guests cannot register.");
            return;
        }

        if (!CoreServices.isRoundActive(roundID)) {
            trace.error("Invalid round [" + roundID + "] in registerInfo");
            ResponseProcessor.error(connectionID, "You cannot register for an inactive round.");
            return;
        }

        Round contest = CoreServices.getContestRound(roundID);

        Registration registration = CoreServices.getRegistration(roundID);
        StringBuilder message = new StringBuilder();

        if (canRegister(userID, contest, registration, message)) {
            ArrayList surveyResponses = new ArrayList();
            if (registration.hasSurveyQuestions()) {
                surveyResponses = surveyData;
            }
            if (contest.isLongContestRound()) {
                try {
                    LongContestServicesLocator.getService().register(contest.getRoundID(), userID, surveyResponses);
                } catch (LongContestServicesException e) {
                    PopUpGenericResponse response = ResponseProcessor.simpleMessage(MessageProvider.getText(e.getLocalizableMessage()), "Event Registration");
                    ResponseProcessor.process(connectionID, response);
                } catch (Exception e) {
                    trace.error("Exception during registratrion ",e);
                    ResponseProcessor.error(connectionID, "Registration failed. Internal error.");
                }
            } else {
                Results results = CoreServices.register(userID, contest.getRoundID(), surveyResponses);
                handleRegistrationResult(results.isSuccess(), connectionID, results.getMsg(), contest.getRoundID(), userID);
            }
        } else {
            PopUpGenericResponse response = ResponseProcessor.simpleMessage(message.toString(), "Event Registration");
            ResponseProcessor.process(connectionID, response);
        }
    }

    /**
     * Handle registration result.
     *
     * @param success the success
     * @param connectionID the connection id
     * @param message the message
     * @param roundId the round id
     * @param coderId the coder id
     */
    static void handleRegistrationResult(boolean success, Integer connectionID, String message, int roundId, int coderId) {
        if (success) {
            handleRegistrationChanged(roundId, new int[] {coderId}, null);
        }
        if (connectionID != null) {
            PopUpGenericResponse response = ResponseProcessor.simpleMessage(message, "Event Registration");
            ResponseProcessor.process(connectionID, response);
        }

    }

    /**
     * This method updates in memory state only. Changes to DB must be done before calling this method
     *
     * @param roundId the round id
     * @param addedCoders the added coders
     * @param removedCoders the removed coders
     */
    static void handleRegistrationChanged(int roundId, int[] addedCoders, int[] removedCoders) {
        BaseRound round = (BaseRound) CoreServices.getContestRound(roundId);
        if (round.isActive() && round.getPhase() >= ContestConstants.CODING_PHASE && (removedCoders != null && removedCoders.length > 0)) {
            throw new IllegalStateException("The round is active, you cannot remove a coder once the coding phase is running");
        }
        trace.info("Registration change: added="+ArrayUtils.asString(addedCoders)+" removed="+ArrayUtils.asString(removedCoders));
        Registration registration = CoreServices.getRegistrationFromCache(roundId, true);
        try {
            if (addedCoders != null && addedCoders.length > 0) {
                List cnns = null;
                if (round.getRoundProperties().isVisibleOnlyForRegisteredUsers()) {
                    cnns = new ArrayList(addedCoders.length);
                }
                User[] users = new User[addedCoders.length];
                for (int j = 0; j < addedCoders.length; j++) {
                    User user = CoreServices.getUser(addedCoders[j]);
                    users[j] = user;
                    registration.register(user);
                    Integer cnnId = RequestProcessor.getConnectionID(user.getID());
                    if (cnnId != null) {
                        toggleConnection(cnnId, roundId, true, s_roundConnections);
                        if (cnns != null) {
                            cnns.add(cnnId);
                        }
                    }
                }
                if (cnns != null) {
                    //We send the contest for all connected users that must see it
                    ResponseProcessor.loadContestRound(cnns.iterator(), round);
                }

                if (!round.getRoundProperties().useRoomAssignamentProcess()) {
                    //If the round does not use RoomAssignment, the coder must be added to the room
                    handleCoderAddedToRoom(users, round, round.getNonAdminRoom().intValue());
                }

            }
            if (removedCoders != null && removedCoders.length > 0) {
                List cnns = null;
                if (round.getRoundProperties().isVisibleOnlyForRegisteredUsers()) {
                    cnns = new ArrayList(removedCoders.length);
                }
                for (int j = 0; j < removedCoders.length; j++) {
                    User user = CoreServices.getUser(removedCoders[j]);
                    registration.unregister(user);
                    Integer cnnId = RequestProcessor.getConnectionID(user.getID());
                    if (cnnId != null) {
                        toggleConnection(cnnId, roundId, false, s_roundConnections);
                        if (cnns != null) {
                            cnns.add(cnnId);
                        }
                    }
                }
                if (!round.getRoundProperties().useRoomAssignamentProcess()) {
                    //If the round does not use RoomAssignment,we need to refresh the room
                    CoreServices.refreshRoom( round.getNonAdminRoom().intValue() );
            }
                if (cnns != null) {
                    //We sent an unload command for all users connected that had been registered
                    ResponseProcessor.unloadContestRound(cnns.iterator(), round);
        }
    }
        } finally {
            CoreServices.saveToCache(registration.getCacheKey(), registration);
        }
    }

    /**
     * Sends the source for another users component to the user.
     *
     * @param connectionID the connection id
     * @param challengerID the challenger id
     * @param pretty the pretty
     * @param roomID the room id
     * @param defenderID the defender id
     * @param componentID the component id
     * @return the challenge component
     */
    static void getChallengeComponent(Integer connectionID, int challengerID, boolean pretty, int roomID, int defenderID,
                                      int componentID) {
        if (roomID == ContestConstants.INVALID_ROOM) {
            trace.error("getChallengeProblem user: " + challengerID + " invalid room. Connection: " + connectionID);
            ResponseProcessor.error(connectionID, "Server error getting challenge problem");
            return;
        }
        User viewingUser = CoreServices.getUser(challengerID);
        BaseCodingRoom room = CoreServices.getContestRoom(roomID, false);
        if (!room.isUserAssigned(defenderID)) {
            trace.error("Defender not in room with ID = " + defenderID + " NumCoders = " + room.getNumCoders() + " RoomID = " + roomID);
            return;
        }
        BaseRound round = (BaseRound) CoreServices.getContestRound(room.getRoundID());
        Coder defenderCoder = room.getCoder(defenderID);
        BaseCoderComponent coderComponent = defenderCoder.getComponent(componentID);

        int activePhase = round.getPhase();

        boolean teammateView = defenderCoder instanceof TeamCoder
            && ((TeamCoder) defenderCoder).isMemberCoder(challengerID)
            && activePhase <= ContestConstants.INTERMISSION_PHASE;

        boolean spectatorRoom = round.getRoundTypeId() == ContestConstants.FORWARDER_ROUND_TYPE_ID;

        boolean valid = (ContestConstants.INTERMISSION_PHASE < activePhase && activePhase <= ContestConstants.CONTEST_COMPLETE_PHASE)
            || ContestConstants.isPracticeRoomType(room.getType())
            || viewingUser.isLevelTwoAdmin()
            || activePhase == ContestConstants.INACTIVE_PHASE
            || teammateView
            || spectatorRoom;
        if (!valid) {
            ResponseProcessor.error(connectionID, "You cannot view code here.");
            return;
        }

        UserState userState = UserState.getUserState(challengerID);
        if (userState.getProblemState().getState() != UserState.ProblemState.CLOSE) {
            trace.info("User " + challengerID + " tries to view other's source when the state is " + userState.getProblemState().getState());
            ResponseProcessor.error(connectionID, "You can only view one problem/code at a time.");
            return;
        }

        RoundComponent component =
            CoreServices.getRoundComponent(defenderCoder.getRoundID(), coderComponent.getComponentID(), defenderCoder.getDivisionID());

        if (!coderComponent.isSubmitted() && !teammateView && !viewingUser.isLevelTwoAdmin() && !spectatorRoom) { // Someone is trying to look at unsubmitted code. shame on them.
            trace.error("ERROR: " + viewingUser.getName() + " is trying to look at empty source. Shame on him.");
            ResponseProcessor.error(connectionID, "You cannot view source unless that coder has submitted.");
            return;
        }

        String code = coderComponent.getSubmittedProgramText();
        if (teammateView) {
            //give them the compiled, not submitted code
            code = coderComponent.getProgramText();
        }

        if((viewingUser.isLevelTwoAdmin() || spectatorRoom) && !coderComponent.isSubmitted()) {
            code = coderComponent.getProgramText();
        }

        trace.debug("About to get Code");
        final int MAX_CODE_LENGTH = 15000;
        if (pretty && code.length() < MAX_CODE_LENGTH) {
            code = getPrettyCode(code, coderComponent.getLanguage());
        }
        trace.debug("BACK FROM About to get Code");

        if((viewingUser.isLevelTwoAdmin() || spectatorRoom) && !coderComponent.isSubmitted()) {
            ResponseProcessor.getChallengeComponent(connectionID, room, component, code, defenderCoder, new Integer(coderComponent.getLanguage()));
        } else {
            ResponseProcessor.getChallengeComponent(connectionID, room, component, code, defenderCoder, new Integer(coderComponent.getSubmittedLanguage()));
        }

        // Mark the user is watching the code, probably challenging the code.
        userState.setProblemState(new UserState.ProblemState(UserState.ProblemState.CHALLENGING, roomID, defenderID, componentID));

        // note that the coder is looking at this problem
        Coder looker = null;

        if (room.isUserAssigned(challengerID)) {
            looker = room.getCoder(challengerID);
            looker.addViewedComponent(challengerID, coderComponent, defenderCoder.getName());
        }

        if (!viewingUser.isLevelTwoAdmin() && (viewingUser.getRoomType() == ContestConstants.CODER_ROOM ||
                                               viewingUser.getRoomType() == ContestConstants.PRACTICE_CODER_ROOM ||
                                               viewingUser.getRoomType() == ContestConstants.TEAM_CODER_ROOM ||
                                               viewingUser.getRoomType() == ContestConstants.TEAM_PRACTICE_CODER_ROOM)
            && !teammateView && !spectatorRoom) {
            StringBuilder message = new StringBuilder("System> ");
            message.append(viewingUser.getName());
            message.append(" is viewing the source of ");
            message.append(defenderCoder.getName());
            message.append("'s ");
            ComponentNameBuilder nameBuilder = round.getRoundType().getComponentNameBuilder();
            String componentName = nameBuilder.longNameForComponent(
                    component.getComponent().getClassName(),
                    component.getPointVal(),
                    round.getRoundProperties());
            message.append(componentName);
            message.append(".\n");
            EventService.sendRoomSystemMessage(roomID, message.toString());
        }

        if ((looker != null) && !spectatorRoom && !viewingUser.isLevelTwoAdmin() && (viewingUser.getRoomType() == ContestConstants.CODER_ROOM)) {
            specAppProcessor.openComponent(round, room, defenderCoder, looker, coderComponent);
        }
    }

    /**
     * Gets the long source code.
     *
     * @param connectionID the connection id
     * @param challengerID the challenger id
     * @param pretty the pretty
     * @param roundId the round id
     * @param defenderID the defender id
     * @param componentID the component id
     * @param example the example
     * @param submissionNumber the submission number
     * @return the long source code
     */
    public static void getLongSourceCode(Integer connectionID, int challengerID, boolean pretty, int roundId, int defenderID, int componentID, boolean example, int submissionNumber) {
        if (trace.isDebugEnabled()) trace.debug("getLongSourceCode("+connectionID+", "+challengerID+", "+pretty+", "+roundId+", "+defenderID+", "+componentID+", "+example+", "+submissionNumber+")" );

        LongContestRound round = (LongContestRound) CoreServices.getContestRound(roundId);
        Integer assignedRoom = round.getAssignedRoom(defenderID);
        if (assignedRoom == null) {
            trace.error("getLongSourceCode user: " + challengerID + " invalid room. Connection: " + connectionID);
            ResponseProcessor.error(connectionID, "Server error getting challenge problem");
            return;
        }
        UserState userState = UserState.getUserState(challengerID);
        if (userState.getProblemState().getState() != UserState.ProblemState.CLOSE) {
            trace.info("User " + challengerID + " tries to view other's marathon source when the state is " + userState.getProblemState().getState());
            ResponseProcessor.error(connectionID, "You can only view one problem/code at a time.");
            return;
        }
        int roomID = assignedRoom.intValue();
        User viewingUser = CoreServices.getUser(challengerID);
        BaseCodingRoom room = CoreServices.getContestRoom(roomID, false);
        if (!room.isUserAssigned(defenderID)) {
            trace.error("Defender not in room with ID = " + defenderID + " NumCoders = " + room.getNumCoders() + " RoomID = " + roomID);
            return;
        }

        Coder defenderCoder = room.getCoder(defenderID);
        LongCoderComponent coderComponent = (LongCoderComponent) defenderCoder.getComponent(componentID);

        int activePhase = round.getPhase();

        boolean spectatorRoom = round.getRoundTypeId() == ContestConstants.FORWARDER_LONG_ROUND_TYPE_ID;

        boolean valid = challengerID == defenderID ||
                (ContestConstants.INTERMISSION_PHASE < activePhase && activePhase <= ContestConstants.CONTEST_COMPLETE_PHASE)
                || (ContestConstants.isPracticeRoomType(room.getType()) && !room.isAdminRoom())
                || viewingUser.isLevelTwoAdmin()
                || activePhase == ContestConstants.INACTIVE_PHASE
                || spectatorRoom;

        if (!valid) {
            ResponseProcessor.error(connectionID, "You cannot view code here.");
            return;
        }

        RoundComponent component =
                CoreServices.getRoundComponent(defenderCoder.getRoundID(), coderComponent.getComponentID(), defenderCoder.getDivisionID());

        if (!coderComponent.isSubmitted() && !viewingUser.isLevelTwoAdmin() && !spectatorRoom) { // Someone is trying to look at unsubmitted code. shame on them.
            trace.error("ERROR: " + viewingUser.getName() + " is trying to look at empty source. Shame on him.");
            ResponseProcessor.error(connectionID, "You cannot view source unless that coder has submitted.");
            return;
        }

        String code;
        int language;

        code = coderComponent.getExampleSubmittedProgramText();
        language = coderComponent.getExampleSubmittedLanguage();
        if (example && submissionNumber != coderComponent.getExampleSubmissionCount()) {
            code = null;
        } else if (!example) {
            code = coderComponent.getSubmittedProgramText();
            language = coderComponent.getSubmittedLanguage();
            if (coderComponent.getSubmissionCount() != submissionNumber) {
                code = null;
            }
        }

        if (code == null) {
            if(spectatorRoom) {
                LongSubmissionData data = null;
                LongCoderHistory hist = RoundForwarderProcessor.getCoderHistory(roundId, defenderID);
                LongSubmissionData[] arr = null;
                if(example) {
                    arr = hist.getExampleSubmissions();
                } else {
                    arr = hist.getFullSubmissions();
                }
                for(int i = 0; i < arr.length; i++) {
                    if(arr[i].getNumber() == submissionNumber) {
                        data = arr[i];
                        break;
                    }
                }
                if(data != null) {
                    code = data.getText();
                    language = data.getLanguageId();
                }
            } else {
                LongSubmissionData submission;
                try {
                    submission = LongContestServicesLocator.getService().getSubmission(roundId, defenderID, componentID, example, submissionNumber);
                } catch (LongContestServicesException e) {
                    trace.error("Failed to obtain the required source code: "+MessageProvider.getText(e.getLocalizableMessage()), e);
                    ResponseProcessor.error(connectionID, "Failed to obtain the required source code");
                    return;
                } catch (Exception e) {
                    trace.error("Failed to obtain the required source code", e);
                    ResponseProcessor.error(connectionID, "Failed to obtain the required source code");
                    return;
                }
                code = submission.getText();
                language = submission.getLanguageId();
            }
        }
        trace.debug("About to get Code");
        final int MAX_CODE_LENGTH = 15000;
        if (pretty && code.length() < MAX_CODE_LENGTH) {
            code = getPrettyCode(code, language);
        }
        trace.debug("BACK FROM About to get Code");

        // Mark the user is watching the code, probably challenging the code.
        userState.setProblemState(new UserState.ProblemState(UserState.ProblemState.CHALLENGING, roomID, defenderID, componentID));

        ResponseProcessor.getSourceCode(connectionID, room, component, code, defenderCoder, new Integer(language));

        // note that the coder is looking at this problem
        Coder looker = null;

        if (room.isUserAssigned(challengerID)) {
            looker = room.getCoder(challengerID);
            looker.addViewedComponent(challengerID, coderComponent, defenderCoder.getName());
        }


        /* no messages for now
        if (!viewingUser.isLevelTwoAdmin() && (viewingUser.getRoomType() == ContestConstants.CODER_ROOM ||
                viewingUser.getRoomType() == ContestConstants.PRACTICE_CODER_ROOM ||
                viewingUser.getRoomType() == ContestConstants.TEAM_CODER_ROOM ||
                viewingUser.getRoomType() == ContestConstants.TEAM_PRACTICE_CODER_ROOM)
                && !spectatorRoom) {
            StringBuilder message = new StringBuilder("System> ");
            message.append(viewingUser.getName());
            message.append(" is viewing the source of ");
            message.append(defenderCoder.getName());
            message.append("'s ");
            message.append(component.getComponent().getClassName());
            message.append(" component.\n");
            EventService.sendRoomSystemMessage(roomID, message.toString());
        }*/

        if ((looker != null) && !spectatorRoom && !viewingUser.isLevelTwoAdmin() && (viewingUser.getRoomType() == ContestConstants.CODER_ROOM)) {
            //specAppProcessor.openComponent(round, room, defenderCoder, looker, coderComponent);
        }
    }



    /** The s_track close. */
    private static boolean s_trackClose = true;

    /**
     * Sets the track close.
     *
     * @param f the new track close
     */
    public static void setTrackClose(boolean f) {
        s_trackClose = f;
    }

    /**
     * Close problem.
     *
     * @param userID the user id
     * @param ownerName the owner name
     * @param componentID the component id
     */
    static void closeProblem(int userID, String ownerName, int componentID) {
        if (trace.isDebugEnabled()) trace.debug("closeProblem: userID:" + userID + " owner:" + ownerName + " id:" + componentID);

        // Mark the problem is closed.
        UserState.getUserState(userID).setProblemState(UserState.PROBLEM_CLOSE);

        if (!s_trackClose) {
            return;
        }

        //debug("ONE");

        User user = CoreServices.getUser(userID, false);

        int roomID = user.getContestRoom();

        if (roomID == ContestConstants.INVALID_ROOM) {
            return;
        }

        //debug("TWO");
        BaseCodingRoom room;
        try {
            room = (BaseCodingRoom) CoreServices.getRoom(roomID, true);
            Round round = CoreServices.getContestRound(room.getRoundID());
            int activePhase = round.getPhase();

            //debug("THREE");
            if (!room.isUserAssigned(userID)) {
                return;
            }

            //debug("FOUR");

            Coder viewerCoder = room.getCoder(userID);
            BaseCoderComponent coderComponent = viewerCoder.getViewedComponent(userID, ownerName, componentID);
            if (coderComponent == null) {
                //trace.debug("Failed to get viewed problem, NULL");
                //the coder is closing their own code via the dropdown
                //notify the spectator
                trace.debug("Notifying spectator of close");
                round = CoreServices.getContestRound(room.getRoundID());
                if(!ContestConstants.isPracticeRoomType(room.getType()))
                    specAppProcessor.closeOwnComponent(round, room, viewerCoder, componentID);
                return;
            }
            Coder defenderCoder = room.getCoder(coderComponent.getCoderID());
            viewerCoder.closeViewedComponent(userID, ownerName, componentID);

            boolean teammateView = viewerCoder instanceof TeamCoder
                && ((TeamCoder) defenderCoder).isMemberCoder(userID)
                && activePhase <= ContestConstants.INTERMISSION_PHASE;
            ProblemComponent component = CoreServices.getComponent(coderComponent.getComponentID());

            boolean ownProblem = viewerCoder.getID() == coderComponent.getCoderID();
            CoreServices.saveToCache(room.getCacheKey(), room);
            if (coderComponent != null && !user.isLevelTwoAdmin() && !teammateView && (user.getRoomType() == ContestConstants.CODER_ROOM ||
                                                                                       user.getRoomType() == ContestConstants.PRACTICE_CODER_ROOM || user.getRoomType() == ContestConstants.TEAM_CODER_ROOM ||
                                                                                       user.getRoomType() == ContestConstants.TEAM_PRACTICE_CODER_ROOM)) {
                StringBuilder message = new StringBuilder("System> ");
                message.append(user.getName());
                message.append(" closed ");

                Coder other = null;
                if (ownProblem) {
                    message.append("the ");
                } else {
                    other = room.getCoder(coderComponent.getCoderID());
                    if (other != null) {
                        message.append(other.getName());
                        message.append("'s ");
                    } else {
                        message.append("someone's");
                    }
                }
                ComponentNameBuilder nameBuilder = round.getRoundType().getComponentNameBuilder();
                String componentName = nameBuilder.longNameForComponent(component.getClassName(), coderComponent.getPointValue(), round.getRoundProperties());
                message.append(componentName);
                message.append(".\n");
                if (trace.isDebugEnabled()) trace.debug("Sending to room: " + message);
                EventService.sendRoomSystemMessage(roomID, message.toString());
            }

            if (coderComponent != null && !user.isLevelTwoAdmin() && user.getRoomType() == ContestConstants.CODER_ROOM) {
                trace.debug("sending close to spectator");
                round = CoreServices.getContestRound(room.getRoundID());
                specAppProcessor.closeComponent(round, room, defenderCoder, viewerCoder, coderComponent);
            } else {
                if (trace.isDebugEnabled()) {
                    trace.debug("Room type: " + user.getRoomType());
                    trace.debug("Coder component" + coderComponent);
                    trace.debug("Level two: " + user.isLevelTwoAdmin());
                }
            }
        } catch (Exception e) {
            trace.error("closeProblem error: ", e);
        } finally {
            CoreServices.releaseLock(Room.getCacheKey(roomID));
        }

    }


    /**
     * Can challenge.
     *
     * @param userID the user id
     * @param challengeID the challenge id
     * @param componentID the component id
     * @return the results
     */
    static Results canChallenge(int userID, int challengeID, int componentID) {
        UserState userState = UserState.getUserState(userID);
        UserState.ProblemState problemState = userState.getProblemState();
        if (problemState.getState() != UserState.ProblemState.CHALLENGING || problemState.getProblemID() != componentID || problemState.getUserID() != challengeID) {
            trace.info("User " + userID + " tries to challenge while not opening for viewing, state=" + problemState.getState() + ", component ID=" + problemState.getProblemID() + ", room ID=" + problemState.getRoomID() + ", defender ID=" + problemState.getUserID());
            return new Results(false, "You cannot challenge outside of coding environment.");
        }

        User user = CoreServices.getUser(userID, false);
        if (user.getRoomID() == ContestConstants.INVALID_ROOM) {  // Fail if the user isn't in a real room.
            return new Results(false, "Invalid room");
        }
        ContestRoom room = (ContestRoom) CoreServices.getRoom(user.getRoomID(), false);
        Round round = CoreServices.getContestRound(room.getRoundID());
        Room baseRoom = CoreServices.getRoom(user.getRoomID(), false);
        if (!(baseRoom instanceof ContestRoom)) {
            trace.error("Invalid challenge request.  User not in a contest room.");
            return new Results(false, "You cannot challenge outside of a contest room.");
        }

        if (!user.isLevelTwoAdmin() && !round.inChallenge() && !ContestConstants.isPracticeRoomType(user.getRoomType())) {
            return new Results(false, "You cannot submit a challenge while the challenge phase is not active.");
        }
        if (!room.isUserAssigned(userID)) {
            return new Results(false, "You cannot challenge in a room you aren't assigned to.");
        }

        if (room.getCoder(challengeID) == null) {
            return new Results(false, "The person you are trying to challenge is not in your room");
        }

        Coder coder = room.getCoder(challengeID);
        if (coder.getID() == room.getCoder(userID).getID() && !ContestConstants.isPracticeRoomType(user.getRoomType())) {
            return new Results(false, "You cannot challenge yourself.");
        }

        if (s_highSchoolCompetition) {
            if (CoreServices.getCoderSchool(userID) == CoreServices.getCoderSchool(challengeID)) {
                return new Results(false, "You cannot challenge a person from the same school");
            }
        }
        if (CoreServices.getComponent(componentID).getComponentTypeID() != ContestConstants.COMPONENT_TYPE_MAIN) {
            return new Results(false, "You can only challenge a problem's main component.");
        }

        if (coder.getComponent(componentID).getStatus() == ContestConstants.CHALLENGE_SUCCEEDED) {
            if (!ContestConstants.ACCEPT_MULTIPLE_CHALLENGES && !ContestConstants.isPracticeRoomType(user.getRoomType())) {
                return new Results(false, "This has already been successfully challenged.");
            }
        }

        if (coder.getComponent(componentID).getStatus() < ContestConstants.NOT_CHALLENGED) {
            return new Results(false, "This problem does not have a submitted main component.");
        }

        Coder challengingCoder = room.getCoder(user.getID());

        long componentIDs[] = challengingCoder.getComponentIDs();
        boolean hasOpened = false;
        for (int i = 0; i < componentIDs.length; i++) {
            CoderComponent coderComponent = (CoderComponent) challengingCoder.getComponent(componentIDs[i]);
            if (coderComponent.isOpened()) hasOpened = true;
        }

        if (!user.isLevelTwoAdmin() && !hasOpened && !ContestConstants.isPracticeRoomType(user.getRoomType())) {
            return new Results(false, "You cannot challenge unless you have opened something.");
        }
        if (!user.isLevelTwoAdmin() && challengingCoder.getPoints() < 0 && !ContestConstants.isPracticeRoomType(user.getRoomType())) {
            return new Results(false, "You must have positive points to challenge.");
        }

        // Check if the problem has already been challenged by this user.
        if (!ContestConstants.ACCEPT_REPEAT_CHALLENGES && challengingCoder.getHistory().hasChallenged(coder.getID(), componentID) && !ContestConstants.isPracticeRoomType(user.getRoomType())) {
            if (challengingCoder instanceof TeamCoder) {
                return new Results(false, "Your team has already challenged this problem.");
            } else {
                return new Results(false, "You have already challenged this problem.");
            }
        }

        return new Results(true, "");
    }

    /**
     * Can challenge.
     *
     * @param userID the user id
     * @param challengeID the challenge id
     * @param componentID the component id
     * @param args the args
     * @return the results
     */
    static Results canChallenge(int userID, int challengeID, int componentID, Object[] args) {
        User user = CoreServices.getUser(userID, false);
        ContestRoom room = (ContestRoom) CoreServices.getRoom(user.getRoomID(), false);
        Coder challengingCoder = room.getCoder(userID);
        trace.debug("In canChallenge.");

        // Check if the exact challenge has already been done.
        if (challengingCoder.getHistory().hasChallenged(challengeID, componentID, args) && !ContestConstants.isPracticeRoomType(user.getRoomType())) {
            if (challengingCoder instanceof TeamCoder) {
                return new Results(false, "Your team has already executed this challenge.");
            } else {
                return new Results(false, "You have already executed this challenge.");
            }
        } else {
            return canChallenge(userID, challengeID, componentID);
        }
    }

    /**
     * Sends the data for constructing the challenge arguments to a given user.
     *
     * @param connectionID the connection id
     * @param userID the user id
     * @param challengeID the challenge id
     * @param componentID the component id
     */
    static void challengeInfo(Integer connectionID, int userID, int challengeID, int componentID) {
        //TODO fix and uncomment this
        Results can = canChallenge(userID, challengeID, componentID);
        if (!can.isSuccess()) {
            ResponseProcessor.error(connectionID, can.getMsg());
            return;
        }
        User user = CoreServices.getUser(userID, false);
        ContestRoom room = (ContestRoom) CoreServices.getRoom(user.getRoomID(), false);
        //          ContestRound round = CoreServices.getContestRound( room.getContestID(), room.getRoundID());
        Coder coder = room.getCoder(challengeID);
        boolean successiveChallenge = false;
        if (coder.getComponent(componentID).getStatus() == ContestConstants.CHALLENGE_SUCCEEDED) {
            successiveChallenge = true;
        }
        //          Coder challengingCoder = room.getCoder( user.getID() );
        RoundComponent component = CoreServices.getRoundComponent(coder.getRoundID(), componentID, coder.getDivisionID());
        int challengePoints = ContestConstants.EASY_CHALLENGE * 100;  // TODO update this if you want different point values
        if (successiveChallenge)
            challengePoints = ContestConstants.SUCCESSIVE_CHALLENGE * 100;

        String message = "If successful, this challenge will be worth " + (challengePoints / 100) +
            " points. If it is unsuccessful, it will cost you " +
            ContestConstants.UNSUCCESSFUL_CHALLENGE + " points.";
        ResponseProcessor.challengeInfo(connectionID, component, message);
    }

    /**
     * Performs a challenge for the user with the given arguments.
     *
     * @param connectionID the connection id
     * @param userID the user id
     * @param defenderID the defender id
     * @param componentID the component id
     * @param args the args
     */
    static void challenge(Integer connectionID, int userID, int defenderID, int componentID, Object[] args) {
        info(userID + " is attempting to challenge " + defenderID + " on component " +
             componentID + " with " + ContestConstants.makePretty(args));
        Results can = canChallenge(userID, defenderID, componentID, args);
        if (!can.isSuccess()) {
            ResponseProcessor.error(connectionID, can.getMsg());
            return;
        }

        UserState userState = UserState.getUserState(userID);
        if (userState.isBusy()) {      // Disregard the request
            info("Throwing out a challenge request because server is busy for user: " + userID);
            ResponseProcessor.error(connectionID, "Throwing out challenge request because server is busy with previous request.");
            return;
        }
        User user = CoreServices.getUser(userID, false);
        Room baseRoom = CoreServices.getRoom(user.getRoomID(), false);
        ContestRoom room = (ContestRoom) baseRoom;
        //          ContestRound round = CoreServices.getContestRound( room.getContestID(), room.getRoundID());

        Coder challengingCoder = room.getCoder(userID);
        Coder defendantCoder = room.getCoder(defenderID);
        CoderComponent coderComponent = (CoderComponent) defendantCoder.getComponent(componentID);

        boolean successiveChallenge = false;
        if (coderComponent.getStatus() == ContestConstants.CHALLENGE_SUCCEEDED) {
            successiveChallenge = true;
        }

        userState.setCurrentBusyTime();

        RoundComponent component = CoreServices.getRoundComponent(defendantCoder.getRoundID(), coderComponent.getComponentID(), defendantCoder.getDivisionID());
        ChallengeAttributes chal = new ChallengeAttributes(component, coderComponent.getSubmittedLanguage(), CoreServices.getProblem(component.getComponent().getProblemID()));
        chal.setChallengerId(userID);
        chal.setDefendantId(defendantCoder.getUserIDForComponent(componentID));
        chal.setComponentID(componentID);
        Location location = new Location(defendantCoder.getContestID(), defendantCoder.getRoundID(), defendantCoder.getRoomID());
        chal.setLocation(location);
        chal.setArgs(args);

        int challengePoints = ContestConstants.EASY_CHALLENGE * 100;  // TODO update this if you want different point values
        int challengeValue = coderComponent.getSubmittedValue();
        if (successiveChallenge) {
            challengePoints = ContestConstants.SUCCESSIVE_CHALLENGE * 100;
            challengeValue = 0;
        }
        chal.setChalValue(challengePoints);
        chal.setPointValue(challengeValue);

        chal.setChalUsername(user.getName());
        chal.setDefUsername(defendantCoder.getName());

        long currentTime = getCurrentTime();
        chal.setSubmitTime(currentTime);
        //int appletServerId = userID;
        CoreServices.submitChallengeTest(chal);

        Round contestRound = CoreServices.getContestRound(room.getRoundID());
        if(!ContestConstants.isPracticeRoomType(room.getType()))
            specAppProcessor.challengingComponent(contestRound, room, defendantCoder, challengingCoder, coderComponent);
    }

     /**
      * Div summary.
      *
      * @param connectionID the connection id
      * @param userID the user id
      * @param roundID the round id
      * @param divisionID the division id
      * @param requestID the request id
      */
     static void divSummary(Integer connectionID, int userID, int roundID, int divisionID, int requestID) {
        Round[] rounds = CoreServices.getAllActiveRounds();
        Round selectedRound = null;
        for(int i = 0; i < rounds.length; i++)
        {
            if(rounds[i].getRoundID() == roundID)
            {
                selectedRound = rounds[i];
                break;
            }
        }

        if (selectedRound == null) {
            trace.error("Null contest round when trying to watch: " + roundID);
            return;
        }

        ArrayList al = new ArrayList();

        for(Iterator i = selectedRound.getAllRoomIDs(); i.hasNext();)
        {
            Integer roomID = (Integer)i.next();
            BaseCodingRoom room = (BaseCodingRoom)CoreServices.getRoom(roomID.intValue());
            if(!room.isAdminRoom() && room.getDivisionID() == divisionID)
            {
                al.add(room);
                room.addSpectator(userID);

                User user = CoreServices.getUser(userID, false);
                user.addWatchedDivSummaryRoom(roomID.intValue());

                toggleWatchUserConnection(userID, connectionID, roomID.intValue(), true);
            }
        }

        // This call is thread-safe so no need for a lock.
        ResponseProcessor.watchDivSummary(connectionID, al, requestID);
    }

    /**
     * Adds a user(invisibly) to the specified room, and takes care of
     * any other setup.
     *
     * @param connectionID the connection id
     * @param userID the user id
     * @param roomID the room id
     * @param requestID the request id
     */
    static void watch(Integer connectionID, int userID, int roomID, int requestID) {
        if (!CoreServices.isRoomActive(roomID)) {
            trace.error("Try to watch a room which is not active: " + roomID);
            return;
        }
        Room baseRoom = CoreServices.getRoom(roomID, false);
        if (baseRoom == null) {
            trace.error("Null contest room when trying to watch: " + roomID);
            return;
        }
        if (!(baseRoom instanceof BaseCodingRoom)) {
            trace.error("Non contest room when trying to watch: " + baseRoom);
            return;
        }
        BaseCodingRoom room = (BaseCodingRoom) baseRoom;
        // This call is thread-safe so no need for a lock.
        room.addSpectator(userID);

        User user = CoreServices.getUser(userID, false);
        user.addWatchedRoom(roomID);

        toggleWatchUserConnection(userID, connectionID, roomID, true);
        ResponseProcessor.watch(connectionID, room, requestID);
    }

    /**
     * Removes a user from the rooms watch list.
     *
     * @param connectionID the connection id
     * @param userID the user id
     * @param roomID the room id
     */
    static void unwatch(Integer connectionID, int userID, int roomID) {
        if (!CoreServices.isRoomActive(roomID)) {
            trace.error("Try to unwatch a room which is not active: " + roomID);
            return;
        }
        Room baseRoom = CoreServices.getRoom(roomID, false);
        if (baseRoom == null) {
            trace.error("Null contest room when trying to unwatch: " + roomID);
            return;
        }
        if (!(baseRoom instanceof BaseCodingRoom)) {
            trace.error("Non contest room when trying to unwatch: " + baseRoom);
            return;
        }
        BaseCodingRoom room = (BaseCodingRoom) baseRoom;
        room.removeSpectator(userID);

        User user = CoreServices.getUser(userID, false);
        user.removeWatchedRoom(roomID);

        if(!user.hasWatchedDivSummaryRoom(roomID))
            {
                toggleWatchUserConnection(userID, connectionID, roomID, false);
            }
    }

    /**
     * Close div summary.
     *
     * @param connectionID the connection id
     * @param userID the user id
     * @param roundID the round id
     * @param divisionID the division id
     * @param requestID the request id
     */
    static void closeDivSummary(Integer connectionID, int userID, int roundID, int divisionID, int requestID) {
       Round[] rounds = CoreServices.getAllActiveRounds();
       Round selectedRound = null;
       for(int i = 0; i < rounds.length; i++)
       {
           if(rounds[i].getRoundID() == roundID)
           {
               selectedRound = rounds[i];
               break;
           }
       }

       if (selectedRound == null) {
           trace.error("Null contest round when trying to watch: " + roundID);
           return;
       }

       for(Iterator i = selectedRound.getAllRoomIDs(); i.hasNext();)
       {
           Integer roomID = (Integer)i.next();
           BaseCodingRoom room = (BaseCodingRoom)CoreServices.getRoom(roomID.intValue());

           User user = CoreServices.getUser(userID, false);
           user.removeWatchedDivSummaryRoom(roomID.intValue());

           room.removeSpectator(userID);

           if(!user.hasWatchedRoom(roomID.intValue()))
           {
                toggleWatchUserConnection(userID, connectionID, roomID.intValue(), false);
           }
       }
    }


    /**
     * Assign components.
     *
     * @param connectionID the connection id
     * @param assignerID the assigner id
     * @param cad the cad
     */
    static void assignComponents(Integer connectionID, int assignerID, ComponentAssignmentData cad) {
        User assigner = CoreServices.getUser(assignerID, false);

        if (!assigner.isCaptain() || cad.getTeamID() != assigner.getTeamID()) {
            ResponseProcessor.error(connectionID, "You must be the captain to assign a component.");
            trace.warn("Unauthorized attempt to assign component for team " + assigner.getID());
            return;
        }

        if (!CoreServices.isRoomActive(assigner.getRoomID()) && !CoreServices.isPracticeRoomActive(assigner.getRoomID())) {
            ResponseProcessor.error(connectionID, "Must be in an active team contest room or a practice team contest room to assign a component.");
            trace.error("Attempt to assign component in an invalid contest room by user " + assignerID);
            return;
        }

        Room room = CoreServices.getRoom(assigner.getRoomID(), false);
        TeamContestRoom contestRoom = null;

        if (room instanceof TeamContestRoom) {
            contestRoom = (TeamContestRoom) room;
        } else {
            ResponseProcessor.error(connectionID, "Must be in a team contest room to assign a component.");
            trace.warn("Attempt to assign component outside of team contest room...");
            return;
        }

        Round round = CoreServices.getContestRound(contestRoom.getRoundID());

        if (round.getPhase() != ContestConstants.CODING_PHASE && !room.isAdminRoom()) {
            ResponseProcessor.error(connectionID, "You can only assign components during the coding phase");
            trace.warn("Attempt to assign component outside the coding phase");
            return;
        }

        Team team = CoreServices.getTeam(cad.getTeamID(), false);

        //make sure they're all on the team
        int[] assignedComponents = cad.getAssignedComponents();
        User[] assignedUsers = new User[assignedComponents.length];
        RoundComponent[] assignedRoundComponents = new RoundComponent[assignedComponents.length];
        for (int i = 0; i < assignedComponents.length; i++) {
            if (!team.isMember(cad.getAssignedUserForComponent(assignedComponents[i]))) {
                ResponseProcessor.error(connectionID, "You can only assign coders on your team.");
                trace.warn("Attempt to assign user outside of team, assigner=" + assignerID + ", assignee=" +
                           cad.getAssignedUserForComponent(assignedComponents[i]));
                return;
            }
            assignedRoundComponents[i] = CoreServices.getRoundComponent(contestRoom.getRoundID(), assignedComponents[i],
                                                                        contestRoom.getDivisionID());
            assignedUsers[i] = CoreServices.getUser(cad.getAssignedUserForComponent(assignedComponents[i]), false);
        }

        TeamCoder teamCoder = (TeamCoder) contestRoom.getCoder(cad.getTeamID());

        ComponentAssignmentData oldcad = teamCoder.getComponentAssignmentData();

        //if any components have changed users, sync them up in the DB
        int[] oldAssignedComponents = oldcad.getAssignedComponents();
        int oldUser, newUser;
        ArrayList toSetOpen = new ArrayList();
        for (int i = 0; i < oldAssignedComponents.length; i++) {
            if (teamCoder.isComponentOpened(oldAssignedComponents[i])) {
                toSetOpen.add(new Integer(oldAssignedComponents[i]));
            }
            oldUser = oldcad.getAssignedUserForComponent(oldAssignedComponents[i]);
            newUser = cad.getAssignedUserForComponent(oldAssignedComponents[i]);
            if (oldUser != -1 && newUser != -1 && oldUser != newUser) {
                CoreServices.dbSynchTeamMembersComponents(round.getContestID(), round.getRoundID(), contestRoom.getRoomID(),
                                                          oldAssignedComponents[i], oldUser, newUser);
            }
        }

        //save assignments (make sure to copy over the team member list)
        cad.setTeamMembers(oldcad.getTeamMembers());
        CoreServices.saveComponentAssignmentData(cad);
        teamCoder.setComponentAssignmentData(cad);

        //make sure any components that were open are still open
        for (int i = 0; i < toSetOpen.size(); i++) {
            teamCoder.setOpenedComponent(((Integer) toSetOpen.get(i)).intValue());
        }

        //send the CreateProblemsResponse to all the connections on the team in the room
        ArrayList connections = new ArrayList((Collection) s_roomChatConnections.get(new Integer(contestRoom.getRoomID())));
        for (int i = 0; i < connections.size(); i++) {
            if (isOnTeam((Integer) connections.get(i), cad.getTeamID())) {
                Integer id = (Integer) connections.get(i);
                ResponseProcessor.assignComponents(id, contestRoom.getRoundID(),
                                                   contestRoom.getDivisionID(), teamCoder.getComponentLabels(RequestProcessor.getUserID(id)));
            }
        }

        //send system messages to all in room
        for (int i = 0; i < assignedComponents.length; i++) {
            StringBuilder message = new StringBuilder("System> ");
            message.append(assignedUsers[i].getName());
            message.append(" has been assigned to the ");
            message.append(assignedRoundComponents[i].getComponent().getClassName());
            message.append(" component.\n");
            EventService.sendRoomSystemMessage(contestRoom.getRoomID(), message.toString());
        }
    }

    //--------------------------------------------------------------------------------------------------------
    // Callback processing
    //--------------------------------------------------------------------------------------------------------

    // These maps maintain the connection lists for each room including the watch rooms.  A separate list is maintained
    // for the people who are in the rooms/watching the room and have chat turned on so that chat messages can be sent
    // out quickly without having to filter the connection list.
    /** The s_round connections. */
    private static Map s_roundConnections = new ConcurrentHashMap();

    /** The s_room connections. */
    private static Map s_roomConnections = new ConcurrentHashMap();

    /** The s_room chat connections. */
    private static Map s_roomChatConnections = new ConcurrentHashMap();

    /** The s_watch room connections. */
    private static Map s_watchRoomConnections = new ConcurrentHashMap();

    /** The s_watch room chat connections. */
    private static Map s_watchRoomChatConnections = new ConcurrentHashMap();

    /**
     * Removes the user connections.
     *
     * @param connectionID the connection id
     * @param userID the user id
     */
    private static void removeUserConnections(Integer connectionID, int userID) {
        User user = CoreServices.getUser(userID, false);
        toggleUserConnection(userID, connectionID, user.getRoomID(), false);

        Object[] roundKeys;
        synchronized (s_roundConnections) {
            roundKeys = s_roundConnections.keySet().toArray();
        }
        for (int j = 0; j < roundKeys.length; j++) {
            Integer roundID = (Integer) roundKeys[j];
            toggleConnection(connectionID, roundID.intValue(), false, s_roundConnections);
        }

        for (Iterator i = user.getWatchedRooms(); i.hasNext();) {
            int roomID = ((Integer) i.next()).intValue();
            toggleWatchUserConnection(userID, connectionID, roomID, false);
        }
    }

    /**
     * Reestablish watches.
     *
     * @param connectionID the connection id
     * @param userID the user id
     */
    public static void reestablishWatches(Integer connectionID, int userID) {
        User user = CoreServices.getUser(userID, false);
        toggleUserConnection(userID, connectionID, user.getRoomID(), true);

        for (Iterator i = user.getWatchedDivSummaryRooms(); i.hasNext();) {
            int roomID = ((Integer) i.next()).intValue();
            toggleWatchUserConnection(userID, connectionID, roomID, true);
        }

        for (Iterator i = user.getWatchedRooms(); i.hasNext();) {
            int roomID = ((Integer) i.next()).intValue();
            toggleWatchUserConnection(userID, connectionID, roomID, true);
        }
    }

    /**
     * Toggle user chat connection.
     *
     * @param userID the user id
     * @param insert the insert
     */
    private static void toggleUserChatConnection(int userID, boolean insert) {
        // toggles all connections in all rooms for this user.
        User user = CoreServices.getUser(userID, false);
        Integer connectionID = RequestProcessor.getConnectionID(userID);

        toggleUserChatConnection(connectionID, user.getRoomID(), insert);
        for (Iterator i = user.getWatchedRooms(); i.hasNext();) {
            int roomID = ((Integer) i.next()).intValue();
            toggleRoomConnection(connectionID, roomID, insert, s_watchRoomChatConnections);
        }
    }

    /**
     * Toggles the watch connections for the user.
     *
     * @param userID the user id
     * @param connectionID the connection id
     * @param roomID the room id
     * @param insert the insert
     */
    public static void toggleWatchUserConnection(int userID, Integer connectionID, int roomID, boolean insert) {
        toggleRoomConnection(connectionID, roomID, insert, s_watchRoomConnections);
        if (connectionID != null) {
            UserState userState = UserState.getUserState(userID);
            toggleRoomConnection(connectionID, roomID, insert && userState.getReceiveChat(), s_watchRoomChatConnections);
        }
    }

    /**
     * Toggle user connection.
     *
     * @param userID the user id
     * @param roomID the room id
     * @param insert the insert
     */
    private static void toggleUserConnection(int userID, int roomID, boolean insert) {
        Integer connectionID = RequestProcessor.getConnectionID(userID);
        toggleUserConnection(userID, connectionID, roomID, insert);
    }

    /**
     * Toggle connection.
     *
     * @param connectionID the connection id
     * @param ID the id
     * @param insert the insert
     * @param map the map
     */
    private static void toggleConnection(Integer connectionID, int ID, boolean insert, Map map) {
        if (connectionID == null) return;
        Integer roomKey = new Integer(ID);
        synchronized (map) {
            Collection connections = (Collection) map.get(roomKey);
            if (insert) {
                if (connections == null) {
                    if (trace.isDebugEnabled()) trace.debug("Creating: " + ID + "," + map.size());
                    connections = Collections.synchronizedSet(new TreeSet());
                    map.put(roomKey, connections);
                }
                connections.add(connectionID);
            } else {
                if (connections != null) {
                    connections.remove(connectionID);
                    if (connections.isEmpty()) {
                        trace.debug("Removing");
                        map.remove(roomKey);
                    }
                }
            }
        }
    }

    /**
     * Toggle room connection.
     *
     * @param connectionID the connection id
     * @param roomID the room id
     * @param insert the insert
     * @param map the map
     */
    private static void toggleRoomConnection(Integer connectionID, int roomID, boolean insert, Map map) {
        //debug("Toggling: " + roomID);
        if (roomID != ContestConstants.INVALID_ROOM) {
            toggleConnection(connectionID, roomID, insert, map);
        }
    }

    /**
     * Toggle user chat connection.
     *
     * @param connectionID the connection id
     * @param roomID the room id
     * @param insert the insert
     */
    private static void toggleUserChatConnection(Integer connectionID, int roomID, boolean insert) {
        toggleRoomConnection(connectionID, roomID, insert, s_roomChatConnections);
    }

    /**
     * Toggles the user's connection in the room and if the user is receiving chat toggles their connection
     * in the room's chat connection set.
     *
     * @param userID the user id
     * @param connectionID the connection id
     * @param roomID the room id
     * @param insert the insert
     */
    private static void toggleUserConnection(int userID, Integer connectionID, int roomID, boolean insert) {
        toggleRoomConnection(connectionID, roomID, insert, s_roomConnections);
        if (connectionID != null) {
            UserState userState = UserState.getUserState(userID);
            toggleUserChatConnection(connectionID, roomID, insert && userState.getReceiveChat());
        }
    }

    /**
     * Returns an iterator of connectionIDs for the given target type and target filtered by
     * users who don't have chat disabled.
     *
     * @param type the type
     * @param target the target
     * @return the chat connection i ds
     */
    private static Iterator getChatConnectionIDs(int type, int target) {
        Iterator result = null;
        switch (type) {
        case TCEvent.ALL_TARGET:
            result = RequestProcessor.allConnectionIDs();
            break;
        case TCEvent.USER_TARGET:
            if (UserState.hasUserState(target)) {
                UserState userState = UserState.getUserState(target);
                if (userState.getReceiveChat()) {
                    Integer connectionID = RequestProcessor.getConnectionID(target);
                    if (connectionID != null) {
                        ArrayList list = new ArrayList(1);
                        list.add(connectionID);
                        result = list.iterator();
                    }
                }
            }
            break;
        case TCEvent.ROOM_TARGET:
            Integer roomKey = new Integer(target);
            Collection connections = (Collection) s_roomChatConnections.get(roomKey);
            if (connections != null) {
                ArrayList connectionList = new ArrayList(connections);
                result = connectionList.iterator();
            }
            break;
        }
        return result;
    }

    /**
     * Returns the list of all connections watching the given room.  If chat is true
     * it returns the set of connections filtered by those who dont have chat turned off.
     *
     * @param target the target
     * @param chat the chat
     * @return the watch connections
     */
    private static ArrayList getWatchConnections(int target, boolean chat) {
        Integer roomKey = new Integer(target);
        Map map = s_watchRoomConnections;
        if (chat) map = s_watchRoomChatConnections;
        Collection connections = (Collection) map.get(roomKey);
        if (connections != null) {
            return new ArrayList(connections);
        }
        return new ArrayList(0);
    }

    /**
     * Returns the list of connections watching the room.
     *
     * @param target the target
     * @return the watch connections
     */
    public static ArrayList getWatchConnections(int target) {
        return getWatchConnections(target, false);
    }

    /**
     * Gets the all target connection i ds.
     *
     * @param round the round
     * @return the all target connection i ds
     */
    public static Iterator getAllTargetConnectionIDs(Round round) {
        return getConnectionIDs(TCEvent.ALL_TARGET, -1, round);
    }

    /**
     * Returns the widest target for the round.<p>
     *
     * The ALL target cannot be used for rounds that are only visible by a group of users.
     *
     * @param type the type
     * @param target the target
     * @param round the round
     * @return the connection i ds
     */
    public static Iterator getConnectionIDs(int type, int target, Round round) {
        if (type == TCEvent.ALL_TARGET && round.getRoundProperties().isVisibleOnlyForRegisteredUsers()) {
            return getConnectionIDs(TCEvent.ROUND_TARGET, round.getRoundID());
        } else {
            return getConnectionIDs(type, target);
        }
    }

    /**
     * Returns the Iterator of all connections at the given target type and target.
     *
     * @param type the type
     * @param target the target
     * @return the connection i ds
     */
    public static Iterator getConnectionIDs(int type, int target) {
        Iterator result = null;
        Collection connections;
        if (trace.isDebugEnabled()) trace.debug("getConnectionIDs: " + type + "," + target);
        switch (type) {
        case TCEvent.ALL_TARGET:
            result = RequestProcessor.allConnectionIDs();
            break;
        case TCEvent.ADMIN_TARGET:
            result = RequestProcessor.allAdminConnectionIDs();
            break;
        case TCEvent.USER_TARGET:
            ArrayList list = new ArrayList(1);
            Integer connectionID = RequestProcessor.getConnectionID(target);
            if (connectionID != null) list.add(connectionID);
            result = list.iterator();
            break;
        case TCEvent.TEAM_TARGET:
            list = new ArrayList();
            Team team = CoreServices.getTeam(target, false);
            for (Iterator it = team.getMembers().iterator(); it.hasNext();) {
                Integer userID = (Integer) it.next();
                connectionID = RequestProcessor.getConnectionID(userID.intValue());
                if (connectionID != null) list.add(connectionID);
            }
            result = list.iterator();
            break;
        case TCEvent.ROOM_TARGET:
            Integer roomKey = new Integer(target);
            connections = (Collection) s_roomConnections.get(roomKey);
            if (connections != null) {
                ArrayList connectionList = new ArrayList(connections);
                result = connectionList.iterator();
            } else {
                result = Collections.EMPTY_LIST.iterator();
            }
            break;
        case TCEvent.ROUND_TARGET:
            Integer roundKey = new Integer(target);
            connections = (Collection) s_roundConnections.get(roundKey);
            ArrayList connectionList = new ArrayList();
            if (connections != null) {
                connectionList.addAll(connections);
            }
            CollectionUtils.addAll(connectionList, RequestProcessor.allAdminConnectionIDs());
            result = connectionList.iterator();
            break;
        }
        if(result == null)
            trace.debug("Result is null");
        return result;
    }


    /**
     * Updates the challenge history, coder state, leaderboard, and room challenge table based
     * on the results of a challenge.
     *
     * @param challengerUserID the challenger user id
     * @param challenge the challenge
     */
    private static void updateChallengeProblems(int challengerUserID, ChallengeAttributes challenge) {
        //User user = CoreServices.getUser( userID, false );
        Location location = challenge.getLocation();
        ContestRoom room = (ContestRoom) CoreServices.getRoom(location.getRoomID(), true);
        Round round = CoreServices.getContestRound(room.getRoundID());
        try {
            Coder challengerCoder = room.getCoder(challengerUserID);
            Coder defendantCoder = room.getCoder(challenge.getDefendantId());
            if (trace.isDebugEnabled()) trace.debug("Coder ID: " + challengerUserID);
            CoderHistory coderHistory = challengerCoder.getHistory();

            if (trace.isDebugEnabled()) trace.debug("Defendant: " + defendantCoder + "ID: " + challenge.getDefendantId());
            CoderHistory defendantHistory = defendantCoder.getHistory();

            boolean challengeSucceeded = challenge.isSuccesfulChallenge();

            int coderPointChange = challenge.getPenaltyValue();
            int defendantPointChange = challenge.getPointValue();
            int status = ContestConstants.CHALLENGE_FAILED;
            if (trace.isDebugEnabled()) {
                trace.debug("coderPointChange = " + coderPointChange);
                trace.debug("defendantPointChange = " + defendantPointChange);
            }

            CoderComponent challengedComponent = (CoderComponent) defendantCoder.getComponent(challenge.getComponentID());

            if (challengeSucceeded) {
                coderPointChange = challenge.getChalValue();
                defendantCoder.setPoints(defendantCoder.getPoints() - defendantPointChange);
                status = ContestConstants.CHALLENGE_SUCCEEDED;
                challengedComponent.setSuccesfullyChallengedTime(System.currentTimeMillis());
                challengedComponent.setChallenger(challengerCoder.getName());
                challengedComponent.setChallengeArgs(challenge.getArgs());
            }
            challengerCoder.setPoints(challengerCoder.getPoints() + coderPointChange);
            challengedComponent.setStatus(status);


            // Send out challenge board updates
            // Challenger point change
            //            int userIndex = room.getCoderIndex(defendant.getID());
            //TODO -1 should be problemID?
            ContestEvent event = new ContestEvent(room.getRoomID(), ContestEvent.CHALLENGE_COMPONENT, "System> " + challenge.getChallengeHistoryMessage(),
                                                  defendantCoder.getID(), -1, challengedComponent.getComponentID(),
                                                  challengedComponent.getStatusString());
            event.setChallengeSuccess(challengeSucceeded);
            event.setTotalPoints(defendantCoder.getPoints());
            event.setChallengerTotalPoints(challengerCoder.getPoints());
            event.setChallengerID(challengerCoder.getID());
            event.setChallengerName(challengerCoder.getName());
            event.setEventTime(System.currentTimeMillis());
            EventService.sendGlobalEvent(event);

            if(!ContestConstants.isPracticeRoomType(room.getType()))
                specAppProcessor.challengedComponent(round, room, defendantCoder, challengerCoder, challengedComponent);

            // Send out leader update
            if (!ContestConstants.isPracticeRoomType(room.getType())) {
                if (room.updateLeader()) {
                    LeaderBoard board = CoreServices.getLeaderBoard(round.getRoundID(), true);
                    try {
                        board.updateLeader(room);
                    } finally {
                        CoreServices.saveToCache(board.getCacheKey(), board);
                    }
                    EventService.sendGlobalEvent(new LeaderEvent(room));
                }
            }


            // Record the right number of points here.
            int points;
            if (challengeSucceeded) {
                points = challenge.getChalValue();
            } else {
                points = challenge.getPenaltyValue();
            }
            if (trace.isDebugEnabled()) trace.debug("points = " + points);
            coderHistory.addChallenge(challenge.getChallengeHistoryMessage(), new java.sql.Date(challenge.getSubmitTime()),
                                      points, challengedComponent.getComponentID(),
                                      new ChallengeCoder(defendantCoder.getID(), defendantCoder.getName(), defendantCoder.getRating()),
                                      true, challenge.getArgs());
            if (challengeSucceeded) {
                points = -1 * challenge.getPointValue();
            } else {
                points = 0;
            }
            if (trace.isDebugEnabled()) trace.debug("points = " + points);
            defendantHistory.addChallenge(challenge.getChallengeHistoryMessage(), new java.sql.Date(challenge.getSubmitTime()),
                    points, challengedComponent.getComponentID(),
                    new ChallengeCoder(challengerCoder.getID(), challengerCoder.getName(), challengerCoder.getRating()),
                    false, challenge.getArgs());
            //notify the room coders of a new challenge
            if (!ContestConstants.isPracticeRoomType(room.getType())) {
                trace.debug("sending challenges response to room: "+room.getRoomID());
                ChallengeData cd = new ChallengeData();
                cd.setChallengerHandle(challengerCoder.getName());
                cd.setDefenderHandle(defendantCoder.getName());
                cd.setChallengerRating(challengerCoder.getRating());
                cd.setDefenderRating(defendantCoder.getRating());
                cd.setDate(new Date(challenge.getSubmitTime()));
                cd.setSuccess(challengeSucceeded);
                cd.setLanguage(defendantCoder.getComponent(challengedComponent.getComponentID()).getSubmittedLanguage()+"");
                cd.setPoints(challengeSucceeded?challenge.getChalValue()/100:challenge.getPenaltyValue()/100);
                cd.setComponentID(challengedComponent.getComponentID());
                ChallengeResponse cr =new ChallengeResponse(room.getType(),room.getRoomID(),cd);
                ResponseProcessor.sendResponseToWebArenaUsers(room,cr);
            }
        } catch (Exception e) {
            trace.fatal("Error occured while updating objects after challenge", e);
        } finally {
            CoreServices.saveToCache(Room.getCacheKey(room.getRoomID()), room);
        }
    }

    /**
     * Invoked by the TopicListener when the TestService has completed a request.
     * May be for a practice system test, user test, challenge or system test.
     *
     * @param event Test event.
     */
    public static void handleTestEvent(TestEvent event) {
        trace.debug("handleTestEvent");
        int userID = event.getUserID();
        Integer connectionID = RequestProcessor.getConnectionID(userID);
        if (connectionID != null) {
            UserState userState = UserState.getUserState(userID);
            userState.resetBusyTime();
            BaseResponse response = null;
            String title;
            String message;
            switch (event.getTestType()) {
            case ContestConstants.AUTO_SYSTEST:
                message = (String) event.getData();
                title = "System Test Results";
                //message = PracticeSystemTestAssembler.getMessage(userID, event.getSubmitTime(), message);
                if (message == null) {
                    // the first result of two language servers
                    return;
                }
                // TODO update the cache objects with results.
                // TODO room.updateChallengeTable( coder );
                response = ResponseProcessor.simpleBigMessage(message, title);
                break;
            case ContestConstants.PRACTICE_SYSTEM_TEST:
                Object eventData = event.getData();
                if (eventData instanceof PracticeTestResultData) {
                    // TODO update the cache objects with results.
                    // TODO room.updateChallengeTable( coder );
                    response = ResponseProcessor.updatePracticeSystemTestResult((PracticeTestResultData) eventData);
                } else {
                    response = ResponseProcessor.practiceSystemTestResponse((Map) eventData);
                }
                break;
            case ContestConstants.TEST:
                UserTestAttributes testAttributes = (UserTestAttributes) event.getData();
                if (!testAttributes.isBatchTest()) {
                    if (testAttributes.getSucceeded()) {
                        title = "Test Results";
                        response = ResponseProcessor.simpleBigMessage(testAttributes.getResultValue(), title);
                    } else {
                        title = "Error";
                        response = ResponseProcessor.simpleMessage(testAttributes.getResultValue(), title);
                    }
                } else {
                    response = testAttributes.getBatchTestResponse();
                    trace.debug("BatchTest Response sending: " + response);
                }

                break;
            case ContestConstants.CHALLENGE:
                ChallengeAttributes challengeAttributes = (ChallengeAttributes) event.getData();
                if (!challengeAttributes.isSystemFailure()) {
                    if (trace.isDebugEnabled()) {
                        trace.debug("Challenge was valid. Succeeded = "
                                + challengeAttributes.isSuccesfulChallenge());
                    }
                    updateChallengeProblems(userID, challengeAttributes);

                    // send out replay message
                    if (false) { // uncomment for replay
                        ReplayChallengeEvent rce = new ReplayChallengeEvent(challengeAttributes);
                        EventService.sendReplayEvent(rce);
                    }
                }

                if (challengeAttributes.isExceptionResult() || challengeAttributes.isTimeOut()) {
                    // Defendant's code didn't execute correctly
                    message = "Your challenge of " + challengeAttributes.getDefUsername()
                            + " was successful.\n" + challengeAttributes.getMessage();
                } else if (challengeAttributes.isSuccesfulChallenge()) {
                    // Challenge succeeded
                    message = "Your challenge of " + challengeAttributes.getDefUsername()
                            + " was successful.\nThe method returned " + challengeAttributes.getResultValue()
                            + " when it should have returned "
                            + ContestConstants.makePretty(challengeAttributes.getExpectedResult()) + ".\n"
                            + "Answer check result: " + challengeAttributes.getMessage();
                } else if (challengeAttributes.isSystemFailure()) {
                    // Challenge failed
                    message = "Your challenge was invalid.\n" + challengeAttributes.getMessage();
                } else {
                    // Invalid input
                    message = "Your challenge of " + challengeAttributes.getDefUsername()
                            + " was unsuccessful.\nThe method returned "
                            + challengeAttributes.getResultValue() + " as expected.";
                }
                response = ResponseProcessor.simpleBigMessage(message, "Challenge Results");
                break;
            }
            if (response != null) {
                ResponseProcessor.process(connectionID, response);
            }
        }
    }

    /** The s_pending lobby lock. */
    private static Object s_pendingLobbyLock = new Object();

    /** The s_pending lobby responses. */
    private static HashMap s_pendingLobbyResponses = new HashMap(10);

    /** The s_pending lobby chat responses. */
    private static HashMap s_pendingLobbyChatResponses = new HashMap(10);

    /**
     * Flushes all queued up responses to the connections.  Currently only Lobby responses (both chat and
     * non-chat) are batched up.
     */
    public static void flushPendingEvents() {
        trace.debug("flushPendingEvents");
        HashMap pendingResponses = null;
        HashMap pendingChatResponses = null;
        boolean chat = false;
        boolean other = false;
        synchronized (s_pendingLobbyLock) {
            if (s_pendingLobbyResponses.size() > 0) {
                other = true;
                if (trace.isDebugEnabled()) trace.debug("pendingLobbyResponses size = " + s_pendingLobbyResponses.size());
                pendingResponses = s_pendingLobbyResponses;
                s_pendingLobbyResponses = new HashMap(10);
            }
            if (s_pendingLobbyChatResponses.size() > 0) {
                chat = true;
                if (trace.isDebugEnabled()) trace.debug("pendingLobbyChatResponses size = " + s_pendingLobbyChatResponses.size());
                pendingChatResponses = s_pendingLobbyChatResponses;
                s_pendingLobbyChatResponses = new HashMap(10);
            }
        }
        // This will result in the chat messages being out of order with regards to the other events.
        // There shouldn't be anything that relies on the correct ordering, but it might look odd to see
        // a chat message come through after someone leaves a room
        if (other) {
            Iterator keys = pendingResponses.keySet().iterator();
            while (keys.hasNext()) {
                Integer target = (Integer) (keys.next());
                ArrayList responses = (ArrayList) pendingResponses.get(target);
                ResponseProcessor.process(getConnectionIDs(TCEvent.ROOM_TARGET, target.intValue()), responses);
            }
        }
        if (chat) {
            Iterator keys = pendingChatResponses.keySet().iterator();
            while (keys.hasNext()) {
                Integer target = (Integer) (keys.next());
                ArrayList responses = (ArrayList) pendingChatResponses.get(target);
                ResponseProcessor.process(getConnectionIDs(TCEvent.ROOM_TARGET, target.intValue()), responses);
            }
        }
    }

    /**
     * Invoked when the TopicListener has received a chat event.  Sends the chat to all targetted users.
     *
     * @param event the event
     */
    private static void handleChatEvent(ChatEvent event) {
        int target = event.getTarget();
        trace.debug("handleChatEvent");
        if (event.getTargetType() == TCEvent.ROOM_TARGET && ServerContestConstants.isLobby(target)) {
            UpdateChatResponse response = ResponseProcessor.createChatResponse(event, target, ContestConstants.LOBBY_ROOM);
            synchronized (s_pendingLobbyLock) {
                if (event.getUserMessage()) {
                    ArrayList lobby = (ArrayList) (s_pendingLobbyChatResponses.get(new Integer(target)));
                    if (lobby == null) {
                        lobby = new ArrayList();
                        s_pendingLobbyChatResponses.put(new Integer(target), lobby);
                    }
                    lobby.add(response);
                } else {
                    ArrayList lobby = (ArrayList) (s_pendingLobbyResponses.get(new Integer(target)));
                    if (lobby == null) {
                        lobby = new ArrayList();
                        s_pendingLobbyResponses.put(new Integer(target), lobby);
                    }
                    lobby.add(response);
                }
            }
        } else {
            int scope = event.getScope();
            int teamID = event.getTeamID();
            Integer connectionID;
            Set connections = new HashSet();
            if (event.getUserMessage()) {
                Iterator iterator = getChatConnectionIDs(event.getTargetType(), event.getTarget());
                while (iterator.hasNext()) {
                    connectionID = (Integer) iterator.next();
                    if (scope == ContestConstants.GLOBAL_CHAT_SCOPE || isOnTeam(connectionID, teamID)) {
                        connections.add(connectionID);
                    }
                }
            } else {
                Iterator iterator = getConnectionIDs(event.getTargetType(), event.getTarget());
                if(iterator != null) {
                    while (iterator.hasNext()) {
                        connections.add(iterator.next());
                    }
                }
            }
            ResponseProcessor.sendChat(connections.iterator(), event);
            // Now check if we need to send this to a watch room list.
            if (scope == ContestConstants.GLOBAL_CHAT_SCOPE && event.getTargetType() == TCEvent.ROOM_TARGET && !ServerContestConstants.isLobby(event.getTarget()) && !ContestConstants.isPracticeRoomType(CoreServices.getRoom(target, false).getType())) {
                ArrayList watchConnections = getWatchConnections(target, event.getUserMessage());
                if (watchConnections != null && watchConnections.size() > 0) {
                    watchConnections.removeAll(connections); // no double chat
                    ResponseProcessor.sendChat(watchConnections.iterator(), event, target, ContestConstants.WATCH_ROOM);
                }
            }
        }
    }

    /**
     * Sends the given response to all target users.
     *
     * @param event the event
     */
    private static void handleResponseEvent(ResponseEvent event) {
        trace.debug("handleResponseEvent");
        if (event.getTargetType() == TCEvent.ROOM_TARGET && ServerContestConstants.isLobby(event.getTarget())) {
            synchronized (s_pendingLobbyLock) {
                ArrayList lobby = (ArrayList) (s_pendingLobbyResponses.get(new Integer(event.getTarget())));
                if (lobby == null) {
                    lobby = new ArrayList();
                    s_pendingLobbyResponses.put(new Integer(event.getTarget()), lobby);
                }
                lobby.addAll(event.getAllResponses());
            }
        } else {
            Iterator rcids = getConnectionIDs(event.getTargetType(), event.getTarget());
            if (trace.isDebugEnabled()) trace.debug("Got response CIDs = " + rcids);
            if (rcids != null) {
                if (trace.isDebugEnabled()) {
                    int count = 0;
                    while (rcids.hasNext()) {
                        trace.debug("CID = " + rcids.next());
                        count++;
                    }
                    trace.debug("Total connections: " + count);
                }
            }
            ResponseProcessor.sendResponse(getConnectionIDs(event.getTargetType(), event.getTarget()), event);
            int target = event.getTarget();
            // Check if we need to send the event to any watch listeners.
            if (event.getTargetType() == TCEvent.ROOM_TARGET && !ServerContestConstants.isLobby(event.getTarget())) {
                Room room = CoreServices.getRoom(target, false);
                if (room != null && !ContestConstants.isPracticeRoomType(room.getType())) {
                    ArrayList connections = getWatchConnections(target);
                    if (connections != null && connections.size() > 0) {
                        ResponseProcessor.sendResponse(connections.iterator(), event.cloneForRoom(target, ContestConstants.WATCH_ROOM));
                    }
                }
            }
        }
    }

    /**
     * Sends notification of the phase change to all users.
     *
     * @param event the event
     */
    private static void handlePhaseEvent(PhaseEvent event) {
        trace.debug("handlePhaseEvent");
        Round round = CoreServices.getContestRound(event.getRound());
        if (!round.getRoundProperties().isVisibleOnlyForRegisteredUsers()) {
            //Only set the status if the event can be seen by everybody
            CoreServices.setLobbyStatus(event.getLobbyStatus());
        }
        ResponseProcessor.sendPhaseEvent(getConnectionIDs(event.getTargetType(), event.getTarget(), round), event);
    }

    /**
     * Handle lobby full event.
     *
     * @param event the event
     */
    private static void handleLobbyFullEvent(LobbyFullEvent event) {
        if (trace.isDebugEnabled()) trace.debug("handleLobbyFullEvent - " + event.getLobbyName() + " is now " + (event.getFull() ? "full" : "non-full"));
        ResponseProcessor.sendLobbyFullEvent(getConnectionIDs(event.getTargetType(), event.getTarget()), event);
    }


    /**
     * The Class EndContestTask.
     */
    private static class EndContestTask implements Runnable {

        /** The m_event. */
        private ActionEvent m_event;

        /**
         * Instantiates a new end contest task.
         *
         * @param event the event
         */
        private EndContestTask(ActionEvent event) {
            m_event = event;
        }

        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        public void run() {
            endContest(m_event);
        }
    }

    /**
     * The Class AssignRoomsTask.
     */
    private static class AssignRoomsTask implements Runnable {

        /** The m_event. */
        private ActionEvent m_event;

        /**
         * Instantiates a new assign rooms task.
         *
         * @param event the event
         */
        private AssignRoomsTask(ActionEvent event) {
            m_event = event;
        }

        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        public void run() {
            assignRooms(m_event);
        }
    }

    /**
     * The Class ActiveMenuTask.
     */
    private static class ActiveMenuTask implements Runnable {

        /** The m_event. */
        private ActionEvent m_event;

        /**
         * Instantiates a new active menu task.
         *
         * @param event the event
         */
        private ActiveMenuTask(ActionEvent event) {
            m_event = event;
        }

        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        public void run() {
            activeMenuTaskHandler(m_event);
        }
    }


    /**
     * Notifies all users that the rooms have been assigned and updates the leaderboard for this server.
     *
     * @param event the event
     */
    private static void assignRooms(ActionEvent event) {
        Round contest = CoreServices.getContestRound(event.getRoundID());
        trace.info("STARTING MENUS");
        ResponseProcessor.updateActiveRoomMenus(contest);
        trace.info("DONE WITH MENUS");
        ArrayList allResponses = new ArrayList();
        allResponses.add(ResponseProcessor.createActiveRoomMenu(contest.getRoundID()));
        allResponses.add(new EnableRoundResponse(event.getRoundID()));
        // Add the LeaderBoard message
        LeaderBoard leaderBoard = CoreServices.getLeaderBoard(contest.getRoundID(), true);
        try {
            trace.info("STARTING LEADERBOARD");
            leaderBoard.initialize(contest);
            trace.info("DONE WITH LEADERBOARD");
        } catch (Exception t) {
            trace.fatal("Failed to intialize leaderboard.", t);
        } finally {
            CoreServices.saveToCache(leaderBoard.getCacheKey(), leaderBoard);
        }
        allResponses.add(ResponseProcessor.createLeaderBoardResponse(leaderBoard));

        if(event.getAction() != ActionEvent.QUAL_UPDATE_ROOMS && event.getAction() != ActionEvent.FORWARDED_ROUND_UPDATE) {
            // Add chat for moving to rooms.
            String msg = "Room assignments are completed for " + contest.getDisplayName() + ". You may move to your room anytime by selecting the contest from the Active Contests menu.\n";
            ChatEvent chatEvent = new ChatEvent(TCEvent.ALL_TARGET, -1, ContestConstants.SYSTEM_CHAT, msg, ContestConstants.GLOBAL_CHAT_SCOPE);
            UpdateChatResponse updateChatResponse = ResponseProcessor.createChatResponse(chatEvent);
            allResponses.add(updateChatResponse);
        }
        Iterator connectionIDs = getConnectionIDs(event.getTargetType(), event.getTarget());
        ResponseProcessor.process(connectionIDs, allResponses);

        if(event.getAction() != ActionEvent.FORWARDED_ROUND_UPDATE)
            specAppProcessor.assignRooms(contest);
    }

    /**
     * System test ended for round.
     *
     * @param roundId the round id
     */
    public static void systemTestEndedForRound(int roundId) {
        Round round = CoreServices.getContestRound(roundId);
        if (round.getRoundProperties().autoEndContestAfterSystemTests()) {
            AdminCommands.endContest(roundId);
        }
    }

    /**
     * Notifies all users that the contest is over and does a final update of rooms and leaderboard.
     *
     * @param event the event
     */
    private static void endContest(ActionEvent event) {
        // s_roundConnections.remove(new Integer(event.getRoundID())); // don't clear out round broadcast cache
        Round contest = CoreServices.getContestRound(event.getRoundID());
        LeaderBoard leaderBoard = CoreServices.getLeaderBoard(contest.getRoundID(), false);

        // re-load all the rooms from the db and save to cache
        for (Iterator rooms = contest.getAllRoomIDs(); rooms.hasNext();) {
            int roomID = ((Integer) rooms.next()).intValue();
            if (trace.isDebugEnabled()) trace.debug("Refreshing room: " + roomID);
            Room baseRoom = CoreServices.getRoom(roomID, false);
            if (baseRoom != null && !baseRoom.isAdminRoom()) {
                BaseCodingRoom room;
                try {
                    room = (BaseCodingRoom) CoreServices.refreshRoom(roomID);
                    if (!room.isAdminRoom()) {
                        // Send new challenge board for room to users in room.
                        ArrayList allResponses = new ArrayList();
                        // TODO the roomID might need to get xlated.
                        CreateChallengeTableResponse response = ResponseProcessor.createChallengeTable(room, roomID, room.getType());
                        allResponses.add(response);
                        Iterator connectionIDs = getConnectionIDs(TCEvent.ROOM_TARGET, roomID);
                        ResponseProcessor.process(connectionIDs, allResponses);
                        // Now send message to watch room if necessary
                        ArrayList watchConnections = getWatchConnections(roomID);
                        if (watchConnections != null && watchConnections.size() > 0) {
                            allResponses = new ArrayList(1);
                            response = ResponseProcessor.createChallengeTable(room, roomID, ContestConstants.WATCH_ROOM);
                            allResponses.add(response);
                            ResponseProcessor.process(watchConnections.iterator(), allResponses);
                        }

                        specAppProcessor.processSystests(contest, room);
                    }
                } catch (Exception e) {
                    trace.error("Exception while updating room: " + roomID, e);
                }
            }
        }

        specAppProcessor.endContest(contest);

        // Add the LeaderBoard message
        leaderBoard = CoreServices.getLeaderBoard(contest.getRoundID(), true);
        try {
            // Update the leaderboard with the contest.
            leaderBoard.initialize(contest);
        } catch (RuntimeException e) {
            trace.fatal("Failed to intialize leaderboard.", e);
        } finally {
            CoreServices.saveToCache(leaderBoard.getCacheKey(), leaderBoard);
        }
        ArrayList allResponses = new ArrayList();
        allResponses.add(ResponseProcessor.createLeaderBoardResponse(leaderBoard));
        Iterator connectionIDs = getConnectionIDs(event.getTargetType(), event.getTarget(), contest);
        ResponseProcessor.process(connectionIDs, allResponses);

        String msg = "The system tester has completed its testing. You can view the final results for any coding room by opening the summary window for that room.";
        try {
            AdminBroadcastManager.getInstance().doBroadcast(msg, ContestConstants.BROADCAST_TYPE_ADMIN_ROUND, contest.getRoundID(), -1, -1);
        } catch (Exception e) {
            trace.error("Bad admin broadcast: ", e);
        }
    }

    /**
     * Takes care of sending out the responses for the clients to update their active contest menus.
     *
     * @param event the event
     */
    private static void activeMenuTaskHandler(ActionEvent event) {
        Round contest = CoreServices.getContestRound(event.getRoundID());
        ResponseProcessor.updateActiveRoomMenus(contest);
        ArrayList allResponses = new ArrayList();
        boolean enable = event.getAction() == ActionEvent.ENABLE_CONTEST;
        String bsString = "F";
        if (enable) bsString = "A";
        //added by SYHAAS 2002-05-19
        if (contest.isModeratedChat()) {
            allResponses.add(ResponseProcessor.getModeratedChatMenuResponse(contest.getContestName(), bsString));
        } else {
            ResponseProcessor.updateActiveContest(contest.getRoundID(), enable);
            if (enable) {
                allResponses.add(new EnableRoundResponse(contest.getRoundID()));
            }
        }


        /*
        // Add the LeaderBoard message
        LeaderBoard leaderBoard = CoreServices.getLeaderBoard( contest.getContestID(), contest.getRoundID(), true );
        try {
        leaderBoard.initialize( contest );
        } catch ( Throwable t ) {
        trace.fatal( "Failed to intialize leaderboard.", t );
        } finally {
        CoreServices.saveToCache( leaderBoard.getCacheKey(), leaderBoard );
        }
        allResponses.add( ResponseProcessor.createLeaderBoard( leaderBoard, -1, -1 ) );
        */
        Iterator connectionIDs = getConnectionIDs(event.getTargetType(), event.getTarget(), contest);
        ResponseProcessor.process(connectionIDs, allResponses);
    }

    /**
     * Dispatches a given action based on the type.  May be to kick a user, end a contest or notify
     * people that the room assignments are complete.
     *
     * @param event the event
     */
    private static void handleActionEvent(ActionEvent event) {
        trace.debug("handleActionEvent");
        int userID;
        Integer connectionID;

        switch (event.getAction()) {
        case ActionEvent.LOGGED_IN_ELSEWHERE:
            userID = event.getTarget();
            connectionID = RequestProcessor.getConnectionID(userID);
            if (connectionID != null) {
                if (trace.isDebugEnabled()) trace.debug("Disconnecting user on connection: " + connectionID + "(Logged in elsewhere)");
                //RequestProcessor.logoutDueToOtherLogin(connectionID);
                ResponseProcessor.forceLogout(connectionID, "You have logged in at another location.");
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                dropConnection(connectionID);
            }
            break;
        case ActionEvent.KICK_USER:
            userID = event.getTarget();
            kickUser(userID);
            break;
        case ActionEvent.END_CONTEST:
            {
                StageQueue.addTask(new EndContestTask(event));
            }
            break;
        case ActionEvent.FORWARDED_ROUND_UPDATE:
        case ActionEvent.QUAL_UPDATE_ROOMS:
        case ActionEvent.ASSIGNED_ROOMS:
            {
                StageQueue.addTask(new AssignRoomsTask(event));
            }
            break;
        case ActionEvent.ENABLE_CONTEST:
        case ActionEvent.DISABLE_CONTEST:
            {
                StageQueue.addTask(new ActiveMenuTask(event));
            }
            break;
        default:
            trace.error("Unknown action in handleActionEvent: " + event.getAction());
            break;
        }
    }


    /**
     * Kick user.
     *
     * @param userID the user id
     */
    public static void kickUser(int userID) {
        Integer connectionID;
        connectionID = RequestProcessor.getConnectionID(userID);
        if (connectionID != null) {
            if (trace.isDebugEnabled()) trace.debug("Kicking user on connection: " + connectionID);
            if (ListenerMain.getSocketConnector().isConnected(connectionID)){
                ListenerMain.getSocketConnector().write(connectionID,new ForcedLogoutResponse("FORCED LOGOUT", "DISCONECTED"));
                ListenerMain.getSocketConnector().remove(connectionID);
            }
            dropConnection(connectionID);
        } else {
            //this might fix the ghosting
            User user = CoreServices.getUser(userID, false);
            if (user != null) {
                if (user.getRoomID() >= 0) {
                    EventService.sendResponseToRoom(user.getRoomID(), ResponseProcessor.leaveRoom(user, true));
                } else {
                    trace.info("User " + userID + " logout without leave room: room ID=" + user.getRoomID() + " room type=" + user.getRoomType());
                }
            }
            CoreServices.logout(userID);
        }
    }

    /**
     * Drop/Close the given connection, login out the user and dropping
     * taken resources.
     *
     * @param connectionID The connection to close
     */
    public static void dropConnection(Integer connectionID) {
        RequestProcessor.handleLostConnection(connectionID.intValue(), true);
    }

    /**
     * Notifies the room that a given event has occured (Compile, Submit, Test, Challenge).
     *
     * @param event the event
     */
    private static void handleContestEvent(ContestEvent event) {
        if (trace.isDebugEnabled()) {
            trace.debug("handleContestEvent: " + event);
        }
        int roomID = event.getTarget();
        Set roomConnections = new HashSet();
        if (event.getTargetType() != TCEvent.ROOM_TARGET) {
            trace.error("Unexpected target for contest event. Review method Processor#handleContestEvent");
        }
        boolean isVisibleEvent = true;
        Room room = CoreServices.getRoom(roomID);
        if (room instanceof BaseCodingRoom) {
            Round round = CoreServices.getContestRound(((BaseCodingRoom) room).getRoundID());
            isVisibleEvent = round.getRoundProperties().getShowScoresOfOtherCoders().booleanValue();
        }

        if (isVisibleEvent) {
        Iterator roomConnectionsIterator = getConnectionIDs(event.getTargetType(), roomID);
        if(roomConnectionsIterator != null) {
                for (;roomConnectionsIterator.hasNext();) {
                    roomConnections.add(roomConnectionsIterator.next());
                }
            }
        } else {
            Iterator it = getConnectionIDs(ContestEvent.USER_TARGET, event.getUserID());
            if(it != null) {
                for (;it.hasNext();) {
                    roomConnections.add(it.next());
                }
            }
        }


        ResponseProcessor.sendContestEvent(roomConnections.iterator(), roomID, -1, event);


        int target = roomID;
        if (isVisibleEvent && event.getTargetType() == TCEvent.ROOM_TARGET && !ServerContestConstants.isLobby(target) && !ContestConstants.isPracticeRoomType(CoreServices.getRoom(target, false).getType())) {
            // check if this is the spectator room too
            //            if (target == CoreServices.getSpectatorRoomId()) {
            //                ResponseProcessor.sendSpectatorContestEvent(roomID, event);
            //            }

            ArrayList connections = getWatchConnections(target);
            connections.removeAll(roomConnections);
            if (connections != null && connections.size() > 0) {
                ResponseProcessor.sendContestEvent(connections.iterator(), roomID, ContestConstants.WATCH_ROOM, event);
            }
        }
    }

    /**
     * Updates the leaderboard with a new leader for a given room.
     *
     * @param event the event
     */
    private static void handleLeaderEvent(LeaderEvent event) {
        trace.debug("handleLeaderEvent");
        Iterator roomConnections = getConnectionIDs(event.getTargetType(), event.getTarget(), CoreServices.getContestRound((int) event.getRoundID()));
        ResponseProcessor.sendLeaderEvent(roomConnections, event);
        int target = event.getTarget();
        if (event.getTargetType() == TCEvent.ROOM_TARGET && !ServerContestConstants.isLobby(target) && !ContestConstants.isPracticeRoomType(CoreServices.getRoom(target, false).getType())) {
            ArrayList connections = getWatchConnections(target);
            if (connections != null && connections.size() > 0) {
                ResponseProcessor.sendLeaderEvent(connections.iterator(), event);
            }
        }

    }

    /**
     * Sends notification back to a user if their compile was successful or not.
     *
     * @param event the event
     */
    public static void handleCompileEvent(CompileEvent event) {
        trace.debug("handleCompileEvent");
        int userID = event.getTarget();
        Integer connectionID = RequestProcessor.getConnectionID(userID);
        if (connectionID != null) {
            User user = CoreServices.getUser(userID, false);
            UserState userState = UserState.getUserState(userID);
            userState.resetBusyTime();
            Submission submission = event.getSubmission();
            int roomID = submission.getLocation().getRoomID();
            Room baseRoom = null;
            try {
                baseRoom = CoreServices.getRoom(roomID, true);
                if (baseRoom == null || !(baseRoom instanceof ContestRoom)) {          // Ignore this request since the user is no longer in a valid room.
                    trace.error("Failed to get room for compileEvent. User = " + userID + " Room = " + roomID);
                    return;
                }
                ContestRoom room = (ContestRoom) baseRoom;
                Coder coder = room.getCoder(user.getID());
                CoderComponent coderComponent = (CoderComponent) coder.getComponent(submission.getComponent().getComponentID());
                Round contestRound = CoreServices.getContestRound(room.getRoundID());

                String title = "Compile Result";
                String error = submission.getCompileError();
                String message;
                boolean status = submission.getCompileStatus();
                if (status) {
                    userState.setCanTestOrSubmit(true);
                    if (coderComponent.getStatus() == ContestConstants.LOOKED_AT) coderComponent.setStatus(ContestConstants.COMPILED_UNSUBMITTED);
                    if (trace.isDebugEnabled()) trace.debug("CanTestOrSubmit = " + userState.canTestOrSubmit());
                } else {
                    //If a person has already submitted don't reset them back to looked at, this way they will get system tested
                    if (coderComponent.getStatus() != ContestConstants.NOT_CHALLENGED) coderComponent.setStatus(ContestConstants.LOOKED_AT);
                    userState.setCanTestOrSubmit(false);
                }
                if (status) {
                    if (submission.getLanguage() != ContestConstants.JAVA) {
                        if (error.equals("")) {
                            message = "Your code compiled successfully, with no warnings.";
                            ResponseProcessor.process(connectionID, ResponseProcessor.simpleMessage(message, title));
                        } else {
                            message = "Your code compiled successfully, but generated warnings:\n\n" + error;
                            ResponseProcessor.process(connectionID, ResponseProcessor.simpleBigMessage(message, title));
                        }
                    } else {
                        message = "Your code compiled successfully.";
                        ResponseProcessor.process(connectionID, ResponseProcessor.simpleMessage(message, title));
                    }
                } else {
                    message = "Your code did not compile:\n\n" + error;
                    ResponseProcessor.process(connectionID, ResponseProcessor.simpleBigMessage(message, title));
                }

                if(!ContestConstants.isPracticeRoomType(room.getType()))
                    specAppProcessor.compiledComponent(contestRound, room, coder, coderComponent, status);
            } finally {
                CoreServices.saveToCache(Room.getCacheKey(roomID), baseRoom);
            }
        }
    }

    /**
     * 	Process replay contest events.
     *
     * @param event the event
     */
    private static void handleReplayContestEvent(ContestEvent event) {
        if (trace.isDebugEnabled()) trace.debug("handleReplayContestEvent: " + event);
        int action = event.getAction();
        switch (action) {
        case ContestEvent.OPEN_COMPONENT:
            CoreServices.replayOpenComponent(event.getUserID(), event.getComponentID());
            break;
        case ContestEvent.SUBMIT_COMPONENT:
        case ContestEvent.TEST_COMPONENT:
        case ContestEvent.COMPILE_COMPONENT:
        default:
            if (trace.isDebugEnabled()) trace.debug("Ignoring action: " + action);
            break;
        }

    }

    /**
     * Handle replay compile event.
     *
     * @param event the event
     */
    private static void handleReplayCompileEvent(ReplayCompileEvent event) {
        if (trace.isDebugEnabled()) trace.debug("handleReplayCompileEvent: " + event);
        int userID = event.getUserID();

        Submission sub = event.getSub();
        int roomID = sub.getLocation().getRoomID();
        Room baseRoom;
        try {
            baseRoom = CoreServices.getRoom(roomID, true);
            if (baseRoom == null || !(baseRoom instanceof ContestRoom)) {        // Ignore this request since the user is no longer in a valid room.
                trace.error("Failed to get room for compileEvent. User = " + userID + " Room = " + roomID);
                return;
            }
            ContestRoom room = (ContestRoom) baseRoom;
            Coder coder = room.getCoder(userID);
            CoderComponent prob = (CoderComponent) coder.getComponent(sub.getComponent().getComponentID());
            prob.setLanguage(sub.getLanguage());
            CoreServices.saveToCache(room.getCacheKey(), room);
        } catch (Exception e) {
            trace.error("Exception", e);
        } finally {
            CoreServices.releaseLock(Room.getCacheKey(roomID));
        }
        scheduleCompilation(userID, sub, -1, -1, sub.getSubmitTime());
    }

    /**
     * Handle replay challenge event.
     *
     * @param event the event
     */
    private static void handleReplayChallengeEvent(ReplayChallengeEvent event) {
        if (trace.isDebugEnabled()) trace.debug("handleReplayChallengeEvent: " + event);
        int userID = event.getUserID();

        ChallengeAttributes challengeAttr = event.generateChallengeAttributes();
        TestService.recordChallengeResults(challengeAttr);
        updateChallengeProblems(userID, challengeAttr);

    }

    /**
     * Handle replay submit event.
     *
     * @param event the event
     */
    private static void handleReplaySubmitEvent(ReplaySubmitEvent event) {
        if (trace.isDebugEnabled()) trace.debug("handleReplaySubmitEvent: " + event);
        int userID = event.getUserID();
        User user = CoreServices.getUser(userID, false);
        boolean saveRoom = false;
        int roomID = user.getContestRoom();
        Room baseRoom = CoreServices.getRoom(roomID, true);
        try {
            ContestRoom room = (ContestRoom) baseRoom;
            Round round = CoreServices.getContestRound(room.getRoundID());
            Coder coder = room.getCoder(userID);
            CoderComponent coderComponent = (CoderComponent) coder.getComponent(event.getComponentID());

            Location loc = new Location(round.getContestID(), round.getRoundID(), roomID);
            RoundComponent component = CoreServices.getRoundComponent(round.getRoundID(), event.getComponentID(), room.getDivisionID());
            Submission sub = new Submission(loc, component, "SHOULDN'T SEE THIS!", 0);
            sub.setProgramText(event.getSrc());
            sub.setPointValue(event.getSubVal());
            sub.setUpdatedPoints(event.getSubVal());
            sub.setSubmitTime(System.currentTimeMillis());
            //sub.setSelectedComponentID(event.getComponentID());
            sub.setCoderId(userID);
            saveRoom = true;
            doSubmit(coder, userID, coderComponent, sub, room, true);
            CompileService.replaySubmit(sub);
        } catch (Exception e) {
            trace.error("error in handleReplaySubmitEvent: roomID=" + roomID, e);
        } finally {
            if (saveRoom) {
                CoreServices.saveToCache(Room.getCacheKey(roomID), baseRoom);
            } else {
                CoreServices.releaseLock(Room.getCacheKey(roomID));
            }
        }
    }

    /**
     * 	Process these events in replay mode, need to call into CoreServices to update state as well.
     *
     * @param event the event
     */
    private static void handleReplayEvent(TCEvent event) {
        if (trace.isDebugEnabled()) trace.debug("handleReplayEvent: " + event);
        int type = event.getEventType();
        try {
            switch (type) {
            case TCEvent.CHAT_TYPE:
                Processor.handleChatEvent((ChatEvent) event);
                break;
            case TCEvent.RESPONSE_TYPE:
                //				info("Ignoring response replay event for now.");	// prolly not needed
                Processor.handleResponseEvent((ResponseEvent) event);
                break;
            case TCEvent.COMPILE_TYPE:
                /*				CompileEvent ce = (CompileEvent)event;
                                Submission sub = ce.getSubmission();
                                CoreServices.replayCompile(sub);*/
                break;
            case TCEvent.ACTION_TYPE:
                if (((ActionEvent) event).getAction() != ActionEvent.END_CONTEST) {
                    Processor.handleActionEvent((ActionEvent) event);
                }
                break;
            case TCEvent.CONTEST_TYPE:
                Processor.handleReplayContestEvent((ContestEvent) event);
                break;
            case TCEvent.TEST_TYPE:
                Processor.handleTestEvent((TestEvent) event);
                break;
            case TCEvent.LEADER_TYPE:
                trace.debug("Ignoring leader replay event for now."); // prolly not needed
                break;
            case TCEvent.PHASE_TYPE:
                //PhaseEvent pe = (PhaseEvent) event;
                Processor.handlePhaseEvent((PhaseEvent) event);
                //replayPhaseEvent(pe.getContest(), pe.getRound(), pe.getPhase());
                throw new UnsupportedOperationException("Replay unsupported...");
            case TCEvent.MOVE_TYPE:
                MoveEvent me = (MoveEvent) event;
                trace.debug("Replay move into " + me.getRoomID());
                CoreServices.enter(me.getUserID(), me.getRoomID());
                break;
            case TCEvent.SUBMIT_TYPE:
                trace.debug("Ignoring submit event for now.");   // prolly not needed
                break;
            case TCEvent.REPLAY_SUBMIT_TYPE:
                ReplaySubmitEvent se = (ReplaySubmitEvent) event;
                Processor.handleReplaySubmitEvent(se);
                break;
            case TCEvent.REPLAY_CHALLENGE_TYPE:
                ReplayChallengeEvent rce = (ReplayChallengeEvent) event;
                Processor.handleReplayChallengeEvent(rce);
                break;
            case TCEvent.REPLAY_COMPILE_TYPE:
                ReplayCompileEvent rce2 = (ReplayCompileEvent) event;
                Processor.handleReplayCompileEvent(rce2);
                break;
            default:
                trace.error("Unknown event type: " + type);
            }
        } catch (Exception e) {
            trace.error("Failed to dispatch event", e);
        }
    }

    /**
     * Dispatches the TCEvent to the appropriate method based on the event type.
     *
     * @param event the event
     */
    public static void dispatchEvent(TCEvent event) {
        if (event.isReplayEvent()) {
            if (s_replayMode) {
                handleReplayEvent(event);
            } else {
                info("Ignoring replay event because replayMode = false");
            }
            return;
        }

        int type = event.getEventType();
        try {
            switch (type) {
            case TCEvent.CHAT_TYPE:
                Processor.handleChatEvent((ChatEvent) event);
                break;
            case TCEvent.RESPONSE_TYPE:
                Processor.handleResponseEvent((ResponseEvent) event);
                break;
            case TCEvent.COMPILE_TYPE:
                Processor.handleCompileEvent((CompileEvent) event);
                break;
            case TCEvent.ACTION_TYPE:
                Processor.handleActionEvent((ActionEvent) event);
                break;
            case TCEvent.CONTEST_TYPE:
                Processor.handleContestEvent((ContestEvent) event);
                break;
            case TCEvent.TEST_TYPE:
                Processor.handleTestEvent((TestEvent) event);
                break;
            case TCEvent.LEADER_TYPE:
                Processor.handleLeaderEvent((LeaderEvent) event);
                break;
            case TCEvent.PHASE_TYPE:
                Processor.handlePhaseEvent((PhaseEvent) event);
                break;
            case TCEvent.LOBBY_FULL_TYPE:
                Processor.handleLobbyFullEvent((LobbyFullEvent) event);
                break;
            case TCEvent.MOVE_TYPE:
                break;
            default:
                trace.error("Unknown event type: " + type);
            }
        } catch (Exception e) {
            trace.error("Failed to dispatch event", e);
        }
    }

    /**
     * Execs out to the astyle tool to prettify the source code.
     *
     * @param code the code
     * @param language the language
     * @return the pretty code
     */
    private static String getPrettyCode(String code, int language) {
        if (language == ContestConstants.VB || language == ContestConstants.PYTHON || language == ContestConstants.PYTHON3 || code == null || code.trim().length() == 0) {
            return code;
        }
        String[] cmd = new String[] {s_astylePath, null};
        if (language == ContestConstants.JAVA) {
            cmd[1] = "--style=java";
        } else {
            cmd[1] = "--style=kr";
        }
        try {
            ProcessRunner runner = new ProcessRunner(cmd, null, 2000);
            runner.setMaxStdErrSize(0);
            runner.setMaxStdOutSize(100000);
            ProcessRunResult result = runner.run(new ByteArrayInputStream(code.getBytes()));
            if (result.getExitCode() != 0) {
                trace.error("Prettify process exitCode: "+result.getExitCode());
                return code;
            }
            return result.getStdOut();
        } catch (Exception e) {
            trace.error("Could not prettify to the source code.\n\tLanguage: "+language + "\n\tCode: " +code, e);
        }
        return code;
    }


    /**
     * Checks if is connection registered.
     *
     * @param roundID the round id
     * @param connectionID the connection id
     * @return true, if is connection registered
     */
    public static boolean isConnectionRegistered(int roundID, Integer connectionID) {
        Set s = (Set) s_roundConnections.get(new Integer(roundID));
        if (s == null) return false;
        return s.contains(connectionID);
    }

    /**
     * Sends the component at the given id for the user to the connectionID.
     *
     * @param connectionID the connection id
     * @param userID the user id
     * @param componentID the component id
     */
    static void openComponent(Integer connectionID, int userID, int componentID) {
        //TODO uncomment and fix this
        Results canGet = canGetComponent(userID);
        if (!canGet.isSuccess()) {
            ResponseProcessor.error(connectionID, canGet.getMsg());
            return;
        }

        User user = CoreServices.getUser(userID, false);
        BaseCodingRoom room = (BaseCodingRoom) CoreServices.getRoom(user.getRoomID(), false);
        Round contestRound = CoreServices.getContestRound(room.getRoundID());
        int activePhase = contestRound.getPhase();
        Coder coder = room.getCoder(user.getID());
        //System.out.println("coder: " + coder);

        if (coder instanceof TeamCoder) {
            //make sure this individual coder is the one assigned to the problem
            if (((TeamCoder) coder).getComponentAssignmentData().getAssignedUserForComponent(componentID) != userID) {
                ResponseProcessor.error(connectionID, "You are not assigned to this component.");
                return;
            }
        }

        PhaseDataResponse phaseDataResponse = null;
        //if (!coder.isComponentOpened(componentID)) {
        if (!coder.hasOpenedComponents()) {
            RoundProperties roundProperties = contestRound.getRoundProperties();
            if (roundProperties.usesPerUserCodingTime()) {
                long startTime = System.currentTimeMillis();
                long endTime = startTime + roundProperties.getPerUserCodingTime().longValue();
                PhaseData phaseData = new PhaseData(room.getRoundID(), activePhase, startTime, endTime);
                phaseDataResponse = new PhaseDataResponse(phaseData);
            }
        }

        CoreServices.getComponent(userID, componentID);
        // TODO : this seems kinda cheesy.   Should be able to submit a problem if it has been successfully compiled
        // in the past.  Opening it shouldn't invalidate submissions.  This is the current behavior of the deployed
        // system.
        UserState userState = UserState.getUserState(userID);
        userState.setCanTestOrSubmit(false);

        // Refresh the objects since CoreServices might change them
        room = (BaseCodingRoom) CoreServices.getRoom(user.getRoomID(), false);
        coder = room.getCoder(user.getID());

        BaseCoderComponent coderComponent = coder.getComponent(componentID);

        // note that the coder is looking at this problem
        //          coder.addViewedProblem(coderProblem, coder.getName());

        user = CoreServices.getUser(userID, true);
        user.setLanguage(coderComponent.getLanguage());
        CoreServices.saveToCache(User.getCacheKey(userID), user);

        String code;
        int languageId;
        if (ContestConstants.CODING_PHASE < activePhase && activePhase <= ContestConstants.CONTEST_COMPLETE_PHASE) {
            code = coderComponent.getSubmittedProgramText();
            languageId = coderComponent.getSubmittedLanguage();
        } else {
            code = coderComponent.getProgramText();
            languageId = coderComponent.getLanguage();
        }
        ProblemComponent component = CoreServices.getComponent(componentID);
        if (code == null || code.equals("")) {
            //TODO is there a better way?
            code = component.getDefaultSolution();
        }
        int editSource = ContestConstants.EDIT_SOURCE_RO;
        if (coderComponent.isWritable()) {
            if (trace.isDebugEnabled()) trace.debug("Making problem writable with status: " + coderComponent.getStatus());
            editSource = ContestConstants.EDIT_SOURCE_RW;
        }

        // Mark the problem is opened for coding
        userState.setProblemState(new UserState.ProblemState(UserState.ProblemState.CODING, room.getRoomID(), userID, componentID));

        if(!ContestConstants.isPracticeRoomType(room.getType()))
            specAppProcessor.openComponent(contestRound, room, coder, coder, coderComponent);

        ResponseProcessor.getProblem(connectionID, component.getProblemId(), room.getRoundID(), room.getDivisionID());
        ResponseProcessor.openComponent(connectionID, componentID, code, editSource, room.getType(), room.getRoomID(),
                new Integer(languageId), coder.getName(), phaseDataResponse);
    }

    /**
     * Open problem.
     *
     * @param connectionID the connection id
     * @param roundId the round id
     * @param userID the user id
     * @param problemID the problem id
     */
    static void openProblem(Integer connectionID, int roundId, int userID, int problemID) {
        //TODO uncomment and fix this
        Results canGet = canGetProblem(userID, roundId,  problemID);
        if (!canGet.isSuccess()) {
            ResponseProcessor.error(connectionID, canGet.getMsg());
            return;
        }
        Round round = CoreServices.getContestRound(roundId);
        User user = CoreServices.getUser(userID, false);
        /*
          int activePhase = contestRound.getPhase();

          PhaseDataResponse phaseDataResponse = null;
          int roundType = contestRound.getRoundType();
          if (roundType == ContestConstants.LONG_ROUND_TYPE_ID) {
          long startTime = System.currentTimeMillis();
          long endTime = startTime + ContestConstants.LONG_ROUND_CODING_LENGTH;
          PhaseData phaseData = new PhaseData(room.getRoundID(), activePhase, startTime, endTime);
          phaseDataResponse = new PhaseDataResponse(phaseData);
          }
        */
        int roomId = user.getRoomID();

        if (round.isLongContestRound()) {
            int codingRoomId = ((LongContestRound) round).getMainRoomId().intValue();
            roomId = codingRoomId;
            ResponseProcessor.getProblem(connectionID, problemID, roundId, CoreServices.getContestRoom(codingRoomId, false).getDivisionID());
        } else {
            BaseCodingRoom room = (BaseCodingRoom) CoreServices.getRoom(user.getRoomID(), false);
            //ContestRound contestRound = CoreServices.getContestRound(room.getRoundID());
            CoreServices.getProblem(userID, problemID);
            // Refresh the objects since CoreServices might change them
            room = (BaseCodingRoom) CoreServices.getRoom(user.getRoomID(), false);
            ResponseProcessor.getTeamProblem(connectionID, problemID, room.getRoundID(), room.getDivisionID());
        }

        // Mark the problem is opened for coding
        UserState userState = UserState.getUserState(userID);
        userState.setProblemState(new UserState.ProblemState(UserState.ProblemState.CODING, roomId, userID, problemID));
    }

    /**
     * Load contest round.
     *
     * @param roundID the round id
     */
    static void loadContestRound(long roundID) {
        Round round = CoreServices.getContestRound((int) roundID);
        if (round.isActive()) {
            unloadContestRound(roundID);
        }
        //We need to broadcast the round before the round is actually loaded.
        //This is because timer tasks will notify phase events as soon as the round is loaded, causing
        //exceptions in the client if it does not have the round.
        round = CoreServices.getContestRound((int) roundID);
        fillRoundConnectionsForBroadcasting(round);
        initNotifyRoundLoaded(round);
        CoreServices.loadContestRound((int) roundID);
        notifyRoundLoaded(round);
    }

    /**
     * Fill round connections for broadcasting.
     *
     * @param round the round
     */
    private static void fillRoundConnectionsForBroadcasting(Round round) {
        Registration reg = CoreServices.getRegistration(round.getRoundID());
        List userIds = reg.getRegisteredUserIds();
        for (Iterator it = userIds.iterator(); it.hasNext();) {
            Integer userId = (Integer) it.next();
            Integer connectionID = RequestProcessor.getConnectionID(userId.intValue());
            if (connectionID != null) {
                toggleConnection(connectionID, round.getRoundID(), true, s_roundConnections);
            }
        }
    }


    /**
     * Reload contest round if not started.
     *
     * @param roundID the round id
     * @return true, if successful
     */
    static boolean reloadContestRoundIfNotStarted(long roundID) {
        trace.info("Reloading contest round: " + roundID);
        Round round = CoreServices.getContestRound((int) roundID);
        if (!round.isActive() || round.getPhase() >= ContestConstants.CODING_PHASE) {
            trace.info("Cannot reload round because it has started. Ignoring it.");
            return false;
        }
        CoreServices.unloadContestRound((int) roundID);
        round = CoreServices.getContestRound((int) roundID);
        initNotifyRoundLoaded(round);
        CoreServices.loadContestRound((int) roundID);
        notifyRoundLoaded(round);
        return true;
    }


    /**
     * Inits the notify round loaded.
     *
     * @param round the round
     */
    private static void initNotifyRoundLoaded(Round round) {
        Iterator connectionIDs = getAllTargetConnectionIDs(round);
        ResponseProcessor.loadContestRound(connectionIDs, round);
    }

    /**
     * Notify round loaded.
     *
     * @param round the round
     */
    private static void notifyRoundLoaded(Round round) {
        ResponseProcessor.refreshActiveContestRoomLists();
    }

    /**
     * Unload contest round.
     *
     * @param roundID the round id
     */
    public static void unloadContestRound(long roundID) {
        Round round = CoreServices.getContestRound((int) roundID);
        CoreServices.unloadContestRound((int) roundID);
        Iterator connectionIDs = getAllTargetConnectionIDs(round);
        ResponseProcessor.unloadContestRound(connectionIDs, round);
        ResponseProcessor.refreshActiveContestRoomLists();
        synchronized (s_roundConnections) {
            s_roundConnections.remove(new Integer(round.getRoundID()));
    }
    }

    /**
     * Process round event.
     *
     * @param contestId the contest id
     * @param roundId the round id
     * @param newPhase the new phase
     */
    public static void processRoundEvent(int contestId, int roundId, int newPhase) {
        processRoundEvent(contestId, roundId, newPhase, true);
    }

    /**
     * Process round event add time.
     *
     * @param contestId the contest id
     * @param roundId the round id
     * @param newPhase the new phase
     */
    public static void processRoundEventAddTime(int contestId, int roundId, int newPhase) {
        if (trace.isDebugEnabled()) trace.debug("processRoundEventAddTime: " + contestId + " phase: " + newPhase);
        try {
            // TODO the contest phase is only being updated on this server instead of on all servers.
            Round contest = CoreServices.getContestRound(roundId, true);
            if (trace.isDebugEnabled()) trace.debug("Got contest = " + contest);
            String lobbyStatus = CoreServices.getLobbyStatus();

            int old_phase = contest.getPhase();

            switch (newPhase) {
            case ContestConstants.STARTS_IN_PHASE:
                info("Entering Starts in phase");
                contest.beginCountdownPhase();
                break;
            case ContestConstants.REGISTRATION_PHASE:
                info("Entering Registration Phase");
                contest.beginRegistrationPhase();
                lobbyStatus = "Registration for " + contest.getContestName() + " is open until five minutes before the contest.";
                break;
            case ContestConstants.ALMOST_CONTEST_PHASE:
                info("Entering Almost Contest Phase");
                contest.endRegistrationPhase();
                if (contest.getRoundProperties().hasRegistrationPhase()) {
                    lobbyStatus = "Registration for " + contest.getContestName() + " is closed";
                }
                break;
            case ContestConstants.CODING_PHASE:
                info("Entering Coding Phase");
                contest.beginCodingPhase();
                lobbyStatus = contest.getContestName() + " is in progress.";
                setTrackClose(true);
                break;
            case ContestConstants.INTERMISSION_PHASE:
                info("Entering Intermission Phase");
                contest.endCodingPhase();
                break;
            case ContestConstants.CHALLENGE_PHASE:
                info("Entering Challenge Phase");
                contest.beginChallengePhase();
                break;
            case ContestConstants.VOTING_PHASE:
                info("Entering Voting Phase");
                contest.beginVotingPhase();
                break;
            case ContestConstants.TIE_BREAKING_VOTING_PHASE:
                info("Entering Tie Breaking Voting Phase");
                contest.beginTieBreakingVotingPhase();
                break;
            case ContestConstants.PENDING_SYSTESTS_PHASE:
                info("Entering Pending Systests Phase");
                contest.endChallengePhase();
                setTrackClose(false);
                break;
            case ContestConstants.SYSTEM_TESTING_PHASE:
                info("Entering Testing Phase");
                contest.beginTestingPhase();
                break;
            case ContestConstants.CONTEST_COMPLETE_PHASE:
                info("Entering Contest Complete Phase");
                contest.endTestingPhase();
                lobbyStatus = contest.getContestName() + " has completed.";
                break;
            case ContestConstants.INACTIVE_PHASE:
                info("Entering Inactive Phase");
                contest.finishContest();
                setTrackClose(false);
                lobbyStatus = "Pick a practice room from the menu above to test your coding skills.";
                break;
            case ContestConstants.MODERATED_CHATTING_PHASE://added by SYHAAS 2002-05-18
                info("Entering Moderated Chatting Phase");
                contest.beginModeratedChattingPhase();
                break;
            default:
                throw new IllegalArgumentException("Unknown phase: " + newPhase);
            }
            if (trace.isDebugEnabled()) trace.debug("ProcessRoundEvent to phase: " + newPhase + " status = " + lobbyStatus);
            int phase = contest.getPhase();
            if (trace.isDebugEnabled()) trace.debug("ContestPhase = " + phase);
            if (ContestConstants.REGISTRATION_PHASE <= phase && phase <= ContestConstants.PENDING_SYSTESTS_PHASE) {
                CoreServices.processRoundEvent(contest);
                trace.debug("DBServices processed roundEvent");
            } else {
                trace.debug("No call to DB on this phase");
            }

            CoreServices.saveToCache(contest.getCacheKey(), contest);

            // only publish if asked to
            PhaseEvent event = new PhaseEvent(contestId, roundId, newPhase, lobbyStatus, false);
            EventService.sendGlobalEvent(event);

        } catch (Exception e) {
            trace.error("Exception in processRoundEvent(" + contestId + "," + roundId + "," + newPhase + ")", e);
        } finally {
            CoreServices.releaseLock(BaseRound.getCacheKey(roundId));
            ResponseProcessor.refreshActiveContestRoomLists();
        }
    }

    /**
     * Takes care of updating a phase change for a round.
     *
     * @param contestId the contest id
     * @param roundId the round id
     * @param newPhase the new phase
     * @param publish the publish
     */
    private static void processRoundEvent(int contestId, int roundId, int newPhase, boolean publish) {
        if (trace.isDebugEnabled()) trace.debug("processRoundEvent: " + contestId + " phase: " + newPhase);
        try {
            // TODO the contest phase is only being updated on this server instead of on all servers.
            Round contest = CoreServices.getContestRound(roundId, true);
            if (trace.isDebugEnabled()) trace.debug("Got contest = " + contest);
            String lobbyStatus = CoreServices.getLobbyStatus();

            int old_phase = contest.getPhase();

            switch (newPhase) {
            case ContestConstants.STARTS_IN_PHASE:
                info("Entering Starts in phase");
                contest.beginCountdownPhase();
                break;
            case ContestConstants.REGISTRATION_PHASE:
                info("Entering Registration Phase");
                contest.beginRegistrationPhase();
                lobbyStatus = "Registration for " + contest.getContestName() + " is open until five minutes before the contest.";
                break;
            case ContestConstants.ALMOST_CONTEST_PHASE:
                info("Entering Almost Contest Phase");
                contest.endRegistrationPhase();
                if (contest.getRoundProperties().hasRegistrationPhase()) {
                    lobbyStatus = "Registration for " + contest.getContestName() + " is closed";
                }
                break;
            case ContestConstants.CODING_PHASE:
                info("Entering Coding Phase");
                contest.beginCodingPhase();
                lobbyStatus = contest.getContestName() + " is in progress.";
                setTrackClose(true);
                break;
            case ContestConstants.INTERMISSION_PHASE:
                info("Entering Intermission Phase");
                contest.endCodingPhase();
                break;
            case ContestConstants.CHALLENGE_PHASE:
                info("Entering Challenge Phase");
                contest.beginChallengePhase();
                break;
            case ContestConstants.VOTING_PHASE:
                info("Entering Voting Phase");
                contest.beginVotingPhase();
                break;
            case ContestConstants.TIE_BREAKING_VOTING_PHASE:
                info("Entering Tie Breaking Voting Phase");
                contest.beginTieBreakingVotingPhase();
                break;
            case ContestConstants.PENDING_SYSTESTS_PHASE:
                info("Entering Pending Systests Phase");
                contest.endChallengePhase();
                setTrackClose(false);
                break;
            case ContestConstants.SYSTEM_TESTING_PHASE:
                info("Entering Testing Phase");
                contest.beginTestingPhase();
                break;
            case ContestConstants.CONTEST_COMPLETE_PHASE:
                info("Entering Contest Complete Phase");
                contest.endTestingPhase();
                lobbyStatus = contest.getContestName() + " has completed.";
                break;
            case ContestConstants.INACTIVE_PHASE:
                info("Entering Inactive Phase");
                contest.finishContest();
                setTrackClose(false);
                lobbyStatus = "Pick a practice room from the menu above to test your coding skills.";
                break;
            case ContestConstants.MODERATED_CHATTING_PHASE://added by SYHAAS 2002-05-18
                info("Entering Moderated Chatting Phase");
                contest.beginModeratedChattingPhase();
                break;
            default:
                throw new IllegalArgumentException("Unknown phase: " + newPhase);
            }
            if (trace.isDebugEnabled()) trace.debug("ProcessRoundEvent to phase: " + newPhase + " status = " + lobbyStatus);
            int phase = contest.getPhase();
            if (trace.isDebugEnabled()) trace.debug("ContestPhase = " + phase);
            if (ContestConstants.REGISTRATION_PHASE <= phase && phase <= ContestConstants.PENDING_SYSTESTS_PHASE) {
                CoreServices.processRoundEvent(contest);
                trace.debug("DBServices processed roundEvent");
            } else {
                trace.debug("No call to DB on this phase");
            }

            CoreServices.saveToCache(contest.getCacheKey(), contest);

            // only publish if asked to
            if(publish) {
                PhaseEvent event = new PhaseEvent(contestId, roundId, newPhase, lobbyStatus);
                EventService.sendGlobalEvent(event);
            }
        } catch (Exception e) {
            trace.error("Exception in processRoundEvent(" + contestId + "," + roundId + "," + newPhase + ")", e);
        } finally {
            CoreServices.releaseLock(BaseRound.getCacheKey(roundId));
            ResponseProcessor.refreshActiveContestRoomLists();
        }
    }

    /**
     * Info.
     *
     * @param message the message
     */
    private static void info(Object message) {
        trace.info(message);
    }

    /**
     * Returns true if the user corresponding to the passed
     * connection id is on the team specified by the passed
     * team id.
     *
     * @param connectionID the connection id
     * @param teamID the team id
     * @return true, if is on team
     */
    private static boolean isOnTeam(Integer connectionID, int teamID) {
        try {
            int userID = RequestProcessor.getUserID(connectionID);
            User user = CoreServices.getUser(userID, false);
            return (user.getTeamID() == teamID && teamID != User.NO_TEAM);
        } catch (IllegalArgumentException e) {
            trace.error("", e);
            return false;
        }
    }

    /**
     * Error.
     *
     * @param connectionID the connection id
     * @param message the message
     */
    public static void error(Integer connectionID, String message)
    {
        ResponseProcessor.error(connectionID, message);
    }

    /**
     * Simple big message.
     *
     * @param connectionID the connection id
     * @param message the message
     */
    public static void simpleBigMessage(Integer connectionID, String message)
    {
        BaseResponse response = ResponseProcessor.simpleBigMessage(message, "System Test Results");
        ResponseProcessor.process(connectionID, response);
    }

    /**
     * Checks to see if arguments are equal for challenge.
     *
     * @param args1 the args1
     * @param args2 the args2
     * @return true, if successful
     */
    public static boolean argsEqual(Object args1, Object args2) {
        // convert lists to arrays
        if (args1 instanceof List) {
            args1 = ((List)args1).toArray();
        }
        if (args2 instanceof List) {
            args2 = ((List)args2).toArray();
        }

        if (args1 == null && args2 == null) {
            return true;
        } else if (args1 == null || args2 == null) {
            trace.debug("argsEqual(): null");
            return false;
        } else if (!(args1.getClass().isArray() && args1.getClass().isArray())) {
            return args1.toString().equals(args2.toString());
        } else if (Array.getLength(args1) != Array.getLength(args2)) {
            trace.debug("argsEqual(): length mismatch");
            return false;
        } else {
            for (int i = 0; i < Array.getLength(args1); i++) {
                try {
                    if (!argsEqual(Array.get(args1,i),Array.get(args2,i))) {
                        return false;
                    }
                    // The object is not an array
                } catch (IllegalArgumentException e) {
                    if (!Array.get(args1,i).toString().equals(Array.get(args2,i).toString())) {
                        trace.debug("argsEqual(): #" + i + " not equal.");
                        Object a1 = Array.get(args1,i);
                        Object a2 = Array.get(args2,i);
                        if (trace.isDebugEnabled()) {
                            trace.debug("argsEqual(): Arg 1: " + a1 + " Type: " + a1.getClass().getName());
                            trace.debug("argsEqual(): Arg 2: " + a2 + " Type: " + a2.getClass().getName());
                        }
                        return false;
                    }
                }
            }
            trace.debug("argsEqual(): EQUAL!");
            return true;
        }
    }


}
