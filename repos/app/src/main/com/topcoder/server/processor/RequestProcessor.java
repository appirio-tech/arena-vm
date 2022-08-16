/*
 * Copyright (C) - 2014 TopCoder Inc., All Rights Reserved.
 */

package com.topcoder.server.processor;

import java.security.Key;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.NullCipher;

import org.apache.commons.collections.iterators.ObjectArrayIterator;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contestantMessages.request.ActiveUsersRequest;
import com.topcoder.netCommon.contestantMessages.request.AddTeamMemberRequest;
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
import com.topcoder.netCommon.contestantMessages.request.UnwatchRequest;
import com.topcoder.netCommon.contestantMessages.request.VerifyRequest;
import com.topcoder.netCommon.contestantMessages.request.VerifyResultRequest;
import com.topcoder.netCommon.contestantMessages.request.ViewQueueRequest;
import com.topcoder.netCommon.contestantMessages.request.VisitedPracticeRequest;
import com.topcoder.netCommon.contestantMessages.request.VoteRequest;
import com.topcoder.netCommon.contestantMessages.request.WLMyTeamInfoRequest;
import com.topcoder.netCommon.contestantMessages.request.WLTeamsInfoRequest;
import com.topcoder.netCommon.contestantMessages.request.WatchRequest;
import com.topcoder.netCommon.testerMessages.PingRequest;
import com.topcoder.server.TopicListener.EventTopicListener;
import com.topcoder.server.common.BaseCodingRoom;
import com.topcoder.server.common.ContestRoom;
import com.topcoder.server.common.LongContestRound;
import com.topcoder.server.common.Rating;
import com.topcoder.server.common.Registration;
import com.topcoder.server.common.RegistrationResult;
import com.topcoder.server.common.Room;
import com.topcoder.server.common.Round;
import com.topcoder.server.common.TeamContestRoom;
import com.topcoder.server.common.User;
import com.topcoder.server.common.WeakestLinkRound;
import com.topcoder.server.contest.ImportantMessageData;
import com.topcoder.server.listener.ForwardingThread;
import com.topcoder.server.listener.monitor.ArenaMonitor;
import com.topcoder.server.services.AsyncRoomLoader.RoomLoadedListener;
import com.topcoder.server.services.CoreServices;
import com.topcoder.server.services.EventService;
import com.topcoder.server.services.VerifyService;
import com.topcoder.server.services.authenticate.HandleTakenException;
import com.topcoder.server.services.authenticate.InvalidPasswordException;
import com.topcoder.server.services.authenticate.InvalidSSOException;
import com.topcoder.shared.arena.remoteactions.ArenaActionFactory;
import com.topcoder.shared.arena.remoteactions.bus.ArenaActionBusFactory;
import com.topcoder.shared.messagebus.BusFactory;
import com.topcoder.shared.messagebus.jms.JMSConfigurationParser;
import com.topcoder.shared.messagebus.jms.activemq.ActiveMQBusFactory;
import com.topcoder.shared.netCommon.MessageEncryptionHandler;
import com.topcoder.shared.netCommon.SealedSerializable;
import com.topcoder.shared.netCommon.messages.Message;
import com.topcoder.shared.netCommon.messages.MessagePacket;
import com.topcoder.shared.round.events.RoundEventFactory;
import com.topcoder.shared.round.events.bus.RoundEventBusFactory;
import com.topcoder.shared.util.SimpleResourceBundle;
import com.topcoder.shared.util.StageQueue;
import com.topcoder.shared.util.logging.Logger;

/**
 * The RequestProcessor takes requests from the RequestHandler.  This class contains all the
 * core logic for processing requests from the clients.  In order to process the
 * requests this class calls into the Processor.
 *
 * <p>
 * Changes in version 1.1 (Module Assembly - TCC Web Socket - Get Registered Rounds and
 * Round Problems):
 * <ol>
 *      <li>Update {@link #dispatch(Integer, BaseRequest)} method to handle
 *      RegisteredRoundListRequest, RoundProblemsRequest requests.</li>
 *      <li>Add {@link #getRegisteredRoundList(Integer)} method to handle
 *      RegisteredRoundListRequest.</li>
 *      <li>Add {@link #getRoundProblems(Integer, RoundProblemsRequest)} method to handle
 *      RoundProblemsRequest.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.2 (Module Assembly - TopCoder Competition Engine - Batch Test):
 * <ol>
 *      <li>Updated {@link #dispatch(Integer, BaseRequest)} method to handle {@link BatchTestRequest}.</li>
 *      <li>Added {@link #batchTest(Integer, BatchTestRequest)} method to handle {@link BatchTestRequest}.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.6 (Module Assembly - Web Socket Listener - 
 * Porting Round Load Related Events):
 * <ol>
 *      <li>Changed scope of {@link #getUserID(Integer)} to enable its access in WebSocketConnector.</li>
 * </ol>
 * </p>
 * @author Graham Hesselroth, dexy, ananthhh
 * @version 1.2
 */
public final class RequestProcessor {

    /**
     * Category for logging.
     */
    private static Logger trace = Logger.getLogger(RequestProcessor.class);
    /**
     * Maximum number of errors to endure on a connection before dropping it.
     */
    private static final int MAX_ERRORS = 5;
    /**
     * Invalid UserID value.
     */
    public static final int INVALID_USER = Integer.MIN_VALUE;
    /**
     * Stores all the error messages sent back to the client.
     */
    private static ResourceBundle g_errorResources;
    /**
     * Stores all the settings for running the processors.
     */
    private static SimpleResourceBundle s_processorSettings = SimpleResourceBundle.getBundle("Processor");
    /**
     * List of logged in admins on this processor
     */
    private static List s_adminList = Collections.synchronizedList(new ArrayList(10));
    //    /**
    //     * List of logged in spectators on this processor
    //     */
    //    private static ArrayList s_spectatorList = new ArrayList(10);
    private static SpecAppController specAppController;
    private static SpecAppProcessor specAppProcessor;

    /**
     * Maps userIDs to connectionIDs once a login has been processed.
     */
    private static Map s_userToConnectionTable = Collections.synchronizedMap(new HashMap());

    /**
     * Maps connectionIDs to ConnectionData
     */
    private static ConcurrentHashMap<Integer, ConnectionData> connectionData = new ConcurrentHashMap<Integer, ConnectionData>();

    /**
     * List of connectionIDs which haven't received a valid login yet.
     */
    private static List s_initialConnections = Collections.synchronizedList(new LinkedList());
    /**
     * Listener to read JMS messages for events.
     */
    private static EventTopicListener s_eventListener;
    private static Object s_requestLock = new Object();
    /**
     * Lock for adding new requests to the main queue before processing.
     */
    private static Object s_pendingRequestLock = new Object();
    /**
     * Lock for requests about to be processed.
     */
    private static LinkedList s_requestQueue = new LinkedList();
    private static LinkedList s_pendingRequestQueue = new LinkedList();
    private static Set s_inProcessConnections = new TreeSet();
    //private static final TrackingQueue s_trackingQueue = new TrackingQueue();
    /**
     * Thread that run Topic Listener
     */
    private static Thread listenerThread;
    private static ArenaMonitor s_monitor;
    private static String weakestLinkIPPrefix;
    private static boolean autoRegisterForActiveLongRound;
    private static int maxRequestQueueSize;

    static {
        try {
            g_errorResources = ResourceBundle.getBundle("ErrorMessages");
        } catch (MissingResourceException mre) {
            trace.error("Failed to load ErrorMessages resources", mre);
        }
    }

    public static void setMonitor(ArenaMonitor monitor) {
        s_monitor = monitor;
    }

    static void monitorChat(int roomID, String username, String message) {
        s_monitor.chat(roomID, username, message);
    }

    /* SYHAAS 2002-05-09 added this method */
    static void monitorQuestion(int roomID, String username, String message) {
        s_monitor.question(roomID, username, message);
    }

    /**
     * Called when a RequestRunner has completed its processing of a given set.
     */
    public static void completedRequestSet(RequestSet set) {
        synchronized (s_requestLock) {
            s_inProcessConnections.removeAll(set.getConnections());
        }
        synchronized (s_pendingRequestLock) {
            s_pendingRequestLock.notifyAll();
        }
    }
    /**
     * Maximum number of connections allowed per request set
     */
    private static final int MAX_CONNECTIONS_PER_REQUEST_SET = 6;

    /**
     * Filter the pending request queue by the set of connections currently being processed
     * and return the ready to process requests along with the connection set.  Restrict
     * the set to contain only MAX_CONNECTIONS_PER_REQUEST_SET.
     */
    private static void filterRequests(LinkedList toProcess, LinkedList pending, Set connections) {
        if (toProcess == null) {
            return;
        }

        for (ListIterator i = toProcess.listIterator(); i.hasNext();) {
            PendingRequest request = (PendingRequest) i.next();
            Integer connectionID = new Integer(request.connectionID);
            if (s_inProcessConnections.contains(connectionID) || connections.size() > MAX_CONNECTIONS_PER_REQUEST_SET) {
                pending.add(request);
                i.remove();
            } else {
                connections.add(connectionID);
            }
        }
    }

    /**
     * Combines the two given lists with the elements of the second list appended to the first.
     * Handles either of the lists being null.
     */
    private static LinkedList combineLists(LinkedList first, LinkedList second) {
        if (first != null && second != null) {
            first.addAll(second);
            return first;
        } else if (first != null) {
            return first;
        } else if (second != null) {
            return second;
        }
        return null;
    }

    /**
     * Returns the next set.  If no PendingRequests are available blocks until there are some to
     * put into the set.
     */
    public static RequestSet getNextRequestSet() {
        RequestSet next = null;
        while (next == null || next.getRequests().size() == 0) {
            synchronized (s_pendingRequestLock) {
                next = createNextRequestSet();
                if (next == null || next.getRequests().size() == 0) {
                    try {
                        s_pendingRequestLock.wait();
                    } catch (InterruptedException ie) {
                    }
                }
            }
        }
        return next;
    }

    /**
     * Creates a set of all pending requests for connectionIDs that aren't already being processed.
     */
    private static RequestSet createNextRequestSet() {
        synchronized (s_requestLock) {
            // Check for any new requests.
            LinkedList pendingRequests = null;
            synchronized (s_pendingRequestLock) {
                if (s_pendingRequestQueue.size() > 0) {
                    pendingRequests = s_pendingRequestQueue;
                    s_pendingRequestQueue = new LinkedList();
                }
            }
            // Check if there were any previously delayed requests.
            LinkedList previousRequests = null;
            if (s_requestQueue.size() > 0) {
                previousRequests = s_requestQueue;
                s_requestQueue = new LinkedList();
            }
            // Simply return null if there is nothing to process.
            if (pendingRequests == null && previousRequests == null) {
                return null;
            }

            // Filter the previously delayed and new requests lists for connections already being processed.
            TreeSet connections = new TreeSet();
            filterRequests(previousRequests, s_requestQueue, connections);
            filterRequests(pendingRequests, s_requestQueue, connections);
            RequestSet set = new RequestSet(combineLists(previousRequests, pendingRequests), connections);
            // Add all the connections to be processed to the inProcess set.
            s_inProcessConnections.addAll(connections);
            return set;
        }
    }

    /**
     * Start up the Event Topic Listener
     */
    private static void startEventTopicListener() {
        trace.info("In startEventTopicListener...Starting threads");
        s_eventListener = null;
        listenerThread = null;
        try {
            s_eventListener = new EventTopicListener();
            listenerThread = new Thread(s_eventListener, "RequestProcessor.ListenerThread");
            listenerThread.setDaemon(true);
            listenerThread.start();
        } catch (Exception e) {
            trace.fatal("Failed to Start EventTopicListener", e);
        }
    }

    /**
     * Stop up the Event Topic Listener
     */
    private static void stopEventTopicListener() {
        trace.info("In stopEventTopicListener...");
        try {
            s_eventListener.deactivate();
        } catch (Exception e) {
            trace.fatal("Failed to Stop EventTopicListener", e);
        }
    }

    /**
     * Restart the Event Topic Listener
     */
    public static void recycleEventTopicListener() {
        trace.info("In recycle EventTopicListener...");
        try {
            stopEventTopicListener();
            startEventTopicListener();
        } catch (Exception e) {
            trace.fatal("Failed to restart EventTopicListener", e);
        }
    }

    /**
     * Initializes the RequestProcessor and starts all threads.
     */
    public static void start() {
        trace.info("start");
        maxRequestQueueSize = s_processorSettings.getInt("processor.request.queue_limit", 2000);
        trace.info("Request queue hard limit:" + maxRequestQueueSize);
        int stageThreads = s_processorSettings.getInt("processor.stage.threads", 1);
        try {
            trace.info("Configuring Bus service and round Event support");
            BusFactory.configureFactory(new ActiveMQBusFactory(new JMSConfigurationParser().getConfiguration()));
            RoundEventFactory.configureFactory(new RoundEventBusFactory());
            ArenaActionFactory.configureFactory(new ArenaActionBusFactory());
        } catch (Exception e) {
            trace.error("Failed to configure BusFactory and RoundEvent factory", e);
        }
        // obsolete
//        RoundEventProcessor.init();
//        ArenaActionProcessor.init();
        StageQueue.start(stageThreads);
        DisconnectTimer.start();
        CoreServices.start();
        //TestService.start();
        VerifyService.start();
        specAppProcessor = new SpecAppProcessor();
        Processor.setSpecAppProcessor(specAppProcessor);
        ResponseProcessor.setSpecAppProcessor(specAppProcessor);
        Processor.start();
        ResponseProcessor.start();
//        RoundEventProcessor.start();
        specAppController = new SpecAppController(specAppProcessor);
        AdminCommands.setSpecAppController(specAppController);
        specAppController.start();
        trace.info("server initialization complete...Starting threads");
        startEventTopicListener();
//        ArenaActionProcessor.start();
        int threadCount = s_processorSettings.getInt("processor.threads", 1);

        for (int i = 0; i < threadCount; i++) {
            RequestRunner runner = new RequestRunner();
            Thread requestThread = new Thread(runner, "RequestProcessor.RequestRunner." + i);
            requestThread.start();
        }
        weakestLinkIPPrefix = s_processorSettings.getString("processor.weakestLinkIPPrefix");
        autoRegisterForActiveLongRound = s_processorSettings.getBoolean("processor.autoRegisterForLongRound");
        trace.info("Threads started.  Ready for connections");
    }

    public static void stop() {
        trace.info("stop");
        //s_trackingQueue.addTracking(new Tracking(-1, -1, -1, -1, -1, -1, -1, -1, CoreServices.getServerID(), System.currentTimeMillis()));
        specAppController.stop();
//        RoundEventProcessor.stop();
//        ArenaActionProcessor.stop();
        Processor.stop();
        CoreServices.stop();
        VerifyService.stop();
        StageQueue.stop();
        DisconnectTimer.stop();
        ResponseProcessor.stop();
        //s_trackingQueue.stop();
        Iterator it = forwardingThreads.values().iterator();
        while (it.hasNext()) {
            ForwardingThread ft = (ForwardingThread) it.next();
            ft.stop();
        }
    }

    /**
     * Adds the incoming request to the processing queue.
     */
    public static void process(int cnnId, Object requestObject) {
        Integer connectionID = new Integer(cnnId);
        ConnectionData data = getConnectionData(connectionID);
        if (data.incRequestCount() > maxRequestQueueSize) {
            trace.warn("Dropping connection "+connectionID+"( user="+data.getUserId()+" ) request queue size is " + data.getRequestCount() + ", over limit " + maxRequestQueueSize);
            Processor.dropConnection(connectionID);
            return;
        }
        processNoLimit(cnnId, requestObject);
    }

    /**
     * Adds the incoming request to the processing queue.
     */
    public static void processNoLimit(int connectionID, Object requestObject) {
        synchronized (s_pendingRequestLock) {
            s_pendingRequestQueue.add(new PendingRequest(PendingRequest.PROCESS_REQUEST, connectionID, requestObject));
            s_pendingRequestLock.notifyAll();
        }
    }
    private static String forwardingAddress = null;
    private static int forwardingPort = -1;
    private static boolean forwarding = false;
    private static HashMap forwardingThreads = new HashMap();

    public static void setForwardingAddress(String address) {
        int idx = address.indexOf(':');
        if (idx == -1) {
            forwarding = false;
        } else {
            forwardingAddress = address.substring(0, idx);
            forwarding = true;
            try {
                forwardingPort = Integer.parseInt(address.substring(idx + 1));
            } catch (Exception e) {
                forwarding = false;
                e.printStackTrace();
            }
        }
        if (!forwarding) {
            Iterator it = forwardingThreads.values().iterator();
            while (it.hasNext()) {
                ForwardingThread ft = (ForwardingThread) it.next();
                ft.stop();
            }
        }
    }

    public static void showSpecResults() {
        RoundForwarderProcessor.displaySystests();
    }

    public static void addRoundForwarder(String host, int port, String user, String password) {
        specAppProcessor.addForwarder(host, port, user, password);
    }

    public static void removeRoundForwarder(String host, int port) {
        specAppProcessor.removeForwarder(host, port);
    }

    /**
     * Indicates that a new connection has been established with the server at the given IP.
     */
    public static void newConnection(int connectionID, String remoteIP) {
        synchronized (s_pendingRequestLock) {
            trace.debug("Creating Pending Request");
            s_pendingRequestQueue.add(new PendingRequest(PendingRequest.NEW_CONNECTION, connectionID, remoteIP));
            trace.debug("Notifying");
            s_pendingRequestLock.notifyAll();
        }
        trace.debug("Done Notifying");
    }

    /**
     * Called when the given connection is lost.
     */
    public static void lostConnection(int connectionID) {
        synchronized (s_pendingRequestLock) {
            s_pendingRequestQueue.add(new PendingRequest(PendingRequest.LOST_CONNECTION, connectionID, null));
            s_pendingRequestLock.notifyAll();
        }
    }

    /**
     * Returns two ArrayLists.  The first is all the ConnectionIDs on the server.
     * The second is all the corresponding UserIDs.
     */
    public static Iterator allConnectionIDs() {
        return new ObjectArrayIterator(s_userToConnectionTable.values().toArray());
    }

    /**
     * Returns an iterator over all admin connectionIDs on this server.
     */
    public static Iterator allAdminConnectionIDs() {
        return new ObjectArrayIterator(s_adminList.toArray());
    }

    /**
     * Returns the Integer for the userID if they are logged onto this applet
     * and null otherwise.
     */
    public static Integer getConnectionID(int userID) {
        return (Integer) s_userToConnectionTable.get(new Integer(userID));
    }

    /**
     * Informs the processor that a new connection has been established
     * with the given ID.  Adds the connectionID to the initialConnections list.
     */
    public static void handleNewConnection(int connectionID, String remoteIP) {
        if (trace.isDebugEnabled()) {
            trace.debug("newConnection on connectionID : " + connectionID + " from " + remoteIP);
        }
        Integer id = new Integer(connectionID);
        if (!s_initialConnections.contains(id)) {
            s_initialConnections.add(id);
            ConnectionData data = new ConnectionData();
            data.setIP(remoteIP);
            connectionData.put(id, data);
            if (forwarding) {
                try {
                    forwardingThreads.put(id, new ForwardingThread(forwardingAddress, forwardingPort, connectionID));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            trace.info("Added new connection #" + id + " for IP " + remoteIP);
        } else {
            trace.error("NewConnection attempted for existing connection: " + connectionID);
        }
    }

    /**
     * Informs the processor that the specified connection was lost.
     * Removes the connectionID from the connectionTable or initialConnection
     * collections depending on the state of the connection.
     */
    public static void handleLostConnection(int connectionID, boolean drop) {
        trace.info("lostConnection on connectionID : " + connectionID);

        Integer id = new Integer(connectionID);
        if (isConnected(id)) {
            if (drop) {
                trace.debug("DROPPING");
                DisconnectTimer.removeConnection(connectionID);
                logout(id, null);
                ResponseProcessor.shutDownConnection(id);
            } else {
                DisconnectTimer.addConnection(connectionID);
                ResponseProcessor.lostConnection(id);
            }
        } else {
            //no need to hang prelogin connections
            trace.debug("CLEANING");
            cleanupConnection(id, -1);
            ResponseProcessor.shutDownConnection(id);
        }
    }

    private static void cleanupConnection(Integer connectionID, int userID, boolean clearUserState) {
        s_monitor.setUsername(connectionID.intValue(), "");
        connectionData.remove(connectionID);
        s_userToConnectionTable.remove(new Integer(userID));
        VerifyService.removeConnection(connectionID);
        // Make sure the initialConnection is removed
        s_initialConnections.remove(connectionID);
        s_adminList.remove(connectionID);
        specAppProcessor.removeConnection(connectionID);
        // Notify the spectator controller (responsible for sending out
        // updates to scoreboard) that this connection has been lost.
        specAppController.removeConnection(connectionID.intValue());
        //s_trackingQueue.addTracking(new Tracking(-1, -1, -1, -1, -1, -1, -1, connectionID.intValue(), CoreServices.getServerID(), System.currentTimeMillis()));
        ForwardingThread ft = (ForwardingThread) forwardingThreads.remove(connectionID);
        if (ft != null) {
            ft.stop();
        }
        if (clearUserState) {
            UserState.removeUserState(userID);
        }
    }

    /**
     * Cleans up any internal state which might be present for the connection or user.
     */
    private static void cleanupConnection(Integer connectionID, int userID) {
        cleanupConnection(connectionID, userID, true);
    }

    /**
     * Returns true if the given connectionID has been logged in.
     */
    public static boolean isConnected(Integer connectionID) {
        return getConnectionData(connectionID).isConnected();
    }

    /**
     * Helper method to return if a given connection has been created but the user
     * hasn't logged in yet.
     */
    public static boolean isInitialConnection(Integer connectionID) {
        return s_initialConnections.contains(connectionID);
    }

    private static boolean isForwarder(Integer connectionID) {
        int userID = getUserID(connectionID);
        User user = CoreServices.getUser(userID, false);

        return user.isForwarder();
    }

    /**
     * Public API called to process a given request.
     * Decomposes and validates the request and then calls dispatch to process.
     * The return request will have internal fields validated and process will
     * verify that the given connectionID and user specified in the
     * request match up with the logged in user for the connectionID.
     * Also if the given connectionID has not had a successful login yet
     * an error response is sent.
     */
    static void handleProcess(int connectionID, Object requestObject) {
        Integer id = new Integer(connectionID);
        ConnectionData cnnData = getConnectionData(id);

        if (requestObject instanceof MessagePacket) {
            MessagePacket packet = (MessagePacket) requestObject;
            List messages = packet.getMessages();
            cnnData.incRequestCount(messages.size() - 1);
            for (Iterator it = messages.iterator(); it.hasNext();) {
                Object o = it.next();
                handleProcess(connectionID, o);
            }
            return;
        }

        cnnData.decRequestCount();

        if (requestObject instanceof BaseRequest) {
            BaseRequest request = (BaseRequest) requestObject;
            if (trace.isDebugEnabled()) {
                trace.debug("New request object type = " + request.getRequestType() + " received.");
            }

            // Check for invalid requests types based on the state of the connection
            if (request.getRequestType() != ContestConstants.LOGIN &&
                    request.getRequestType() != ContestConstants.SPECTATOR_LOGIN &&
                    request.getRequestType() != ContestConstants.SPECTATOR_REQUEST &&
                    request.getRequestType() != ContestConstants.KEEP_ALIVE_REQUEST &&
                    request.getRequestType() != ContestConstants.GUEST_LOGIN &&
                    request.getRequestType() != ContestConstants.RECONNECT &&
                    request.getRequestType() != ContestConstants.GET_CURRENT_APPLET_VERSION_REQUEST &&
                    request.getRequestType() != ContestConstants.VERIFY_REQUEST &&
                    request.getRequestType() != ContestConstants.VERIFY_RESULT_REQUEST &&
                    request.getRequestType() != ContestConstants.EXCHANGE_KEY_REQUEST &&
                    request.getRequestType() != ContestConstants.ERROR_REPORT) {
                if (!isConnected(id)) {
                    ResponseProcessor.error(id, g_errorResources.getString("LOGIN_FAILURE"));
                    trace.error("Non-login request sent for initial connectionID : " + connectionID);
                    checkErrorCount(id);
                }
            } else if (request.getRequestType() != ContestConstants.KEEP_ALIVE_REQUEST) {
                // Make sure we are dealing with an initial connection if the request is a LOGIN
                if (!isInitialConnection(id)) {
                    ResponseProcessor.error(id, g_errorResources.getString("INVALID_REQUEST"));
                    trace.error("Login sent for initialized connectionID : " + connectionID);
                    checkErrorCount(id);
                    return;
                }
            }
            if (trace.isInfoEnabled()) {
                if (!KeepAliveRequest.class.equals(request.getClass())) {
                    String className = request.getClass().getName();
                    className = className.substring(className.lastIndexOf('.') + 1);
                    trace.info("*** Processing " + className + " for connection: " + id + "***");
                }
            }
            dispatch(id, request);
            if (forwarding) {
                ForwardingThread ft = (ForwardingThread) forwardingThreads.get(id);
                if (ft != null && ft.isGoing()) {
                    ft.forward(request);
                }
            }
        } else if (requestObject instanceof PingRequest) {
            /// Echo request
            ping(id, (PingRequest) requestObject);
        } else if (requestObject instanceof Message) {
            //these are coming from forwarder threads, check to make sure we should process
            if (!isForwarder(id)) {
                throw new IllegalStateException("Forwarded request received from non-forwarder: " + requestObject);
            }
            Message request = (Message) requestObject;
            RoundForwarderProcessor.dispatchForwardedRequest(id, request);
        } else {
            throw new IllegalStateException("Non-Request in RequestProcessor.handleProcess: " + requestObject);
        }
    }

    /**
     * Checks the request's runtime type and dispatches appropriately.
     *
     * @param connectionID the connection id
     * @param request the request
     */
    private static void dispatch(Integer connectionID, BaseRequest request) {
        if (request instanceof LoginRequest) {
            login(connectionID, (LoginRequest) request);
        } else if (request instanceof SSOLoginRequest) {
            ssoLogin(connectionID, (SSOLoginRequest) request);
        } else if (request instanceof SynchTimeRequest) {
            synchTime(connectionID, (SynchTimeRequest) request);
        } else if (request instanceof ReconnectRequest) {
            reconnect(connectionID, (ReconnectRequest) request);
        } else if (request instanceof WatchRequest) {
            watch(connectionID, (WatchRequest) request);
        } else if (request instanceof CloseDivSummaryRequest) {
            closeDivSummaryRequest(connectionID, (CloseDivSummaryRequest) request);
        } else if (request instanceof DivSummaryRequest) {
            divSummaryRequest(connectionID, (DivSummaryRequest) request);
        } else if (request instanceof LogoutRequest) {
            logout(connectionID, (LogoutRequest) request);
        } else if (request instanceof MoveRequest) {
            move(connectionID, (MoveRequest) request);
        } else if (request instanceof CompileRequest) {
            compile(connectionID, (CompileRequest) request);
        } else if (request instanceof TestRequest) {
            test(connectionID, (TestRequest) request);
        } else if (request instanceof ChallengeRequest) {
            challenge(connectionID, (ChallengeRequest) request);
        } else if (request instanceof BatchTestRequest) {
            batchTest(connectionID, (BatchTestRequest) request);
        } else if (request instanceof SubmitRequest) {
            submit(connectionID, (SubmitRequest) request);
        } else if (request instanceof ChatRequest) {
            chat(connectionID, (ChatRequest) request);
        } else if (request instanceof CoderHistoryRequest) {
            coderHistory(connectionID, (CoderHistoryRequest) request);
        } else if (request instanceof CoderInfoRequest) {
            coderInfo(connectionID, (CoderInfoRequest) request);
        } else if (request instanceof EnterRequest) {
            enter(connectionID, (EnterRequest) request);
            //        } else if (request instanceof GetProblemRequest) {
            //            getProblem(connectionID, (GetProblemRequest)request);
        } else if (request instanceof CloseProblemRequest) {
            closeProblem(connectionID, (CloseProblemRequest) request);
        } else if (request instanceof SaveRequest) {
            save(connectionID, (SaveRequest) request);
        } else if (request instanceof SearchRequest) {
            search(connectionID, (SearchRequest) request);
        } else if (request instanceof GetAdminBroadcastsRequest) {
            sendAdminBroadcasts(connectionID);
        } else if (request instanceof ChallengeInfoRequest) {
            challengeInfo(connectionID, (ChallengeInfoRequest) request);
        } else if (request instanceof UnwatchRequest) {
            unwatch(connectionID, (UnwatchRequest) request);
        } else if (request instanceof ClearPracticeRequest) {
            clearPracticer(connectionID, (ClearPracticeRequest) request);
        } else if (request instanceof ClearPracticeProblemRequest) {
            trace.info("MESSAGE RECEIVED: CLEAR PRACTICE PROBLEM");
            clearPracticeProblem(connectionID, (ClearPracticeProblemRequest) request);
        } else if (request instanceof ErrorRequest) {
            error(connectionID);
        } else if (request instanceof GetChallengeProblemRequest) {
            getChallengeProblem(connectionID, (GetChallengeProblemRequest) request);
        } else if (request instanceof ActiveUsersRequest) {
            loggedInUsers(connectionID);
        } else if (request instanceof PracticeSystemTestRequest) {
            practiceSystemTest(connectionID, (PracticeSystemTestRequest) request);
        } else if (request instanceof RegisterInfoRequest) {
            registerInfo(connectionID, (RegisterInfoRequest) request);
        } else if (request instanceof RegisterRequest) {
            register(connectionID, (RegisterRequest) request);
        } else if (request instanceof RegisterUsersRequest) {
            registerUsers(connectionID, (RegisterUsersRequest) request);
        } else if (request instanceof TestInfoRequest) {
            testInfo(connectionID, (TestInfoRequest) request);
        } else if (request instanceof ToggleChatRequest) {
            toggleChat(connectionID);
        } else if (request instanceof SetLanguageRequest) {
            setLanguage(connectionID, (SetLanguageRequest) request);
        } else if (request instanceof GenericPopupRequest) {
            processPopupResponse(connectionID, (GenericPopupRequest) request);
        } else if (request instanceof GetLeaderBoardRequest) {
            getLeaderBoard(connectionID);
        } else if (request instanceof CloseLeaderBoardRequest) {
            closeLeaderBoard(connectionID);
        } else if (request instanceof OpenSummaryRequest) {
            openSummary(connectionID);
        } else if (request instanceof CloseSummaryRequest) {
            closeSummary(connectionID);
        } else if (request instanceof KeepAliveRequest) {
            keepAlive(connectionID, (KeepAliveRequest) request);
        } else if (request instanceof AssignComponentsRequest) {
            assignComponents(connectionID, (AssignComponentsRequest) request);
        } else if (request instanceof GetTeamListRequest) {
            //getTeamList(connectionID, (GetTeamListRequest)request);
            getTeamList(connectionID);
        } else if (request instanceof CloseTeamListRequest) {
            //closeTeamList(connectionID, (CloseTeamListRequest)request);
            closeTeamList(connectionID);
        } else if (request instanceof JoinTeamRequest) {
            joinTeam(connectionID, (JoinTeamRequest) request);
        } else if (request instanceof LeaveTeamRequest) {
            //leaveTeam(connectionID, (LeaveTeamRequest)request);
            leaveTeam(connectionID);
        } else if (request instanceof VoteRequest) {
            vote(connectionID, (VoteRequest) request);
        } else if (request instanceof RoundStatsRequest) {
            roundStatsRequest(connectionID, (RoundStatsRequest) request);
        } else if (request instanceof WLMyTeamInfoRequest) {
            wlMyTeamInfoRequest(connectionID, (WLMyTeamInfoRequest) request);
        } else if (request instanceof WLTeamsInfoRequest) {
            wlTeamsInfoRequest(connectionID, (WLTeamsInfoRequest) request);
        } else if (request instanceof AutoSystestRequest) {
            autoSystestRequest(connectionID, (AutoSystestRequest) request);
        } else if (request instanceof SystestResultsRequest) {
            systestResultsRequest(connectionID, (SystestResultsRequest) request);
        } else if (request instanceof RegisterRoomRequest) {
            if (connectionID == null) {
                trace.error("Cannot execute a register room request for a null connection ID");
            } else {
                User user = CoreServices.getUser(getUserID(connectionID));
                // only spectators are allowed.
                if (user.isSpectator()) {
                    specAppController.registerRoom(connectionID.intValue(),
                            ((RegisterRoomRequest) request).getRoomID());
                } else {
                    trace.error("Cannot execute a register room request for a non-spectator " + user.getName());
                }
            }
        } else if (request instanceof RegisterWeakestLinkTeamRequest) {
            if (connectionID == null) {
                trace.error("Cannot execute a register weakest link team request for a null connection ID");
            } else {
                User user = CoreServices.getUser(getUserID(connectionID));
                // only spectators are allowed.
                if (user.isSpectator()) {
                    specAppController.registerWeakestLinkTeam(connectionID.intValue(),
                            ((RegisterWeakestLinkTeamRequest) request).getTeamID());
                } else {
                    trace.error("Cannot execute a register weakest link team request for a non-spectator "
                            + user.getName());
                }
            }
        } else if (request instanceof AddTeamMemberRequest) {
            addTeamMember(connectionID, (AddTeamMemberRequest) request);
        } else if (request instanceof RemoveTeamMemberRequest) {
            removeTeamMember(connectionID, (RemoveTeamMemberRequest) request);
        } else if (request instanceof OpenComponentForCodingRequest) {
            openComponent(connectionID, (OpenComponentForCodingRequest) request);
        } else if (request instanceof OpenProblemForReadingRequest) {
            openProblem(connectionID, (OpenProblemForReadingRequest) request);
        } else if (request instanceof RoundScheduleRequest) {
            roundSchedule(connectionID, (RoundScheduleRequest) request);
        } else if (request instanceof EnterRoundRequest) {
            enterRound(connectionID, (EnterRoundRequest) request);
        } else if (request instanceof ReadMessageRequest) {
            readMessage(connectionID, (ReadMessageRequest) request);
        } else if (request instanceof GetImportantMessagesRequest) {
            getMessages(connectionID, (GetImportantMessagesRequest) request);
        } else if (request instanceof GetCurrentAppletVersionRequest) {
            getAppletVersion(connectionID, (GetCurrentAppletVersionRequest) request);
        } else if (request instanceof VisitedPracticeRequest) {
            visitedPractice(connectionID);
        } else if (request instanceof VerifyRequest) {
            getVerifyClass(connectionID, (VerifyRequest) request);
        } else if (request instanceof VerifyResultRequest) {
            verifyClient(connectionID, (VerifyResultRequest) request);
        } else if (request instanceof ErrorReportRequest) {
            clientErrorReport(connectionID, (ErrorReportRequest) request);
        } else if (request instanceof LongSubmitRequest) {
            submitLong(connectionID, (LongSubmitRequest) request);
        } else if (request instanceof LongTestResultsRequest) {
            longTestResults(connectionID, (LongTestResultsRequest) request);
        } else if (request instanceof GetSourceCodeRequest) {
            getSourceCode(connectionID, (GetSourceCodeRequest) request);
        } else if (request instanceof ViewQueueRequest) {
            viewQueue(connectionID, (ViewQueueRequest) request);
        } else if (request instanceof ExchangeKeyRequest) {
            exchangeKey(connectionID, (ExchangeKeyRequest) request);
        } else if (request instanceof RegisteredRoundListRequest) {
            getRegisteredRoundList(connectionID);
        } else if (request instanceof RoundProblemsRequest) {
            getRoundProblems(connectionID, (RoundProblemsRequest) request);
        } else {
            trace.error("Unknown request type: " + request.getRequestType() + ", " + request.getClass().getName());
            ResponseProcessor.error(connectionID, g_errorResources.getString("RESPONSE_TYPE_FAILURE")
                    + request.getRequestType());
            checkErrorCount(connectionID);
        }
    }

    /**
     * In charge of handling the WatchRoom Request, responds with a DefineRoom
     */
    /*  private static void watchRoom(Integer connectionID, WatchRoom watch) {
    RoomData rd = watch.getRoom();
    int roomId = rd.getRoomID();
    ContestRoom room = (ContestRoom)CoreServices.getRoom(roomId, false);
    ContestRound contest = CoreServices.getContestRound(room.getContestID(), room.getRoundID(), false);
    RoomData returnData = new RoomData(room.getRoomID(), room.getRoomType(), room.getName(), contest.getContestName());
    Iterator roomCoders = room.getAllCoders();
    ArrayList coders = new ArrayList();
    int seed = 1;
    while (roomCoders.hasNext()) {
    Coder cur = (Coder)roomCoders.next();
    CoderRoomData crd = new CoderRoomData(cur.getName(), cur.getRating(), seed++);
    coders.add(crd);
    }
    int roundID = room.getRoundID();
    int divID = room.getDivisionID();
    ArrayList problems = new ArrayList(6);
    ArrayList pids = room.questions();
    for (int i=0;i<pids.size();i++) {
    int pid = ((Integer)pids.get(i)).intValue();
    RoundProblem rp = CoreServices.getRoundProblem(roundID, pid, divID);
    problems.add(new ProblemData(pid, rp.getPointVal()));
    }
    DefineRoom load = new DefineRoom(returnData, coders, problems);
    process( connectionID.intValue(), load);
    }
     */
    /*    protected static void spectatorRequest(Integer connectionID, Request request) {
    MessagePacket msgs = (MessagePacket)request.getData();
    Iterator iter = msgs.getMessages().iterator();
    while (iter.hasNext()) {
    Message msg = (Message)iter.next();
    // only support two messages, TODO: add switch
    if (msg instanceof LoginRequest) {
    login(connectionID, (LoginRequest)msg);
    }// else if (msg instanceof WatchRoom) {
    //              watchRoom(connectionID, (WatchRoom)msg);
    //          }
    }
    }
     */
    /**
     * Inspects the Request Type for the given request and
     * invokes the corresponding method for that type.
     * When processing is finished, some of the Requests are added to the TrackingQueue
     */
    /*private static void dispatch(Integer connectionID, Request request)
    {
    switch(request.getRequestType())
    {
    case ContestConstants.SPECTATOR_REQUEST:
    spectatorRequest(connectionID, request);
    break;
    default:
    trace.error("Unknown request type: " + request.getRequestType());
    ResponseProcessor.error(connectionID, g_errorResources.getString("RESPONSE_TYPE_FAILURE") +
    request.getRequestType());
    checkErrorCount(connectionID);
    break;
    }
    }*/
    /**
     * Increments the current error count for the connection and if MAX_ERRORS has been
     * exceeded, shuts down the connection.
     */
    private static void checkErrorCount(Integer connectionID) {
        ConnectionData data = getConnectionData(connectionID);
        if (data == null) {
            trace.error("checkErrorCount: The connection " + connectionID + " is already removed");
        }

        if (data.incErrorCount() >= MAX_ERRORS) {
            // TODO perhaps implement a check based on frequency of errors.
            ResponseProcessor.error(connectionID, g_errorResources.getString("MAX_ERRORS"));
            logout(connectionID, null);
        }
    }

    /**
     * Logs that an error occurred on the given connection.
     */
    private static void error(Integer connectionID) {
        trace.error("Error occured on connectionID: " + connectionID);
    }

    /**
     * Returns the userID for the given connection or INVALID_USER if the connection
     * has not been initialized.
     */
    public static int getUserID(Integer connectionID) {
        try {
            return getConnectionData(connectionID).getUserId().intValue();
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("No valid user for connection #: " + connectionID);
        }
    }

    /**
     * Sets up internal data structures to mark the connection as authenticated.
     */
    private static void processLogin(Integer connectionID, int requestType, User user, String userName) {
        if (trace.isDebugEnabled()) {
            trace.debug("processLogin: connection=" + connectionID + ", userName = " + userName + ", user = " + user);
        }
        if (user == null) {
            return;
        }
        if (!s_initialConnections.remove(connectionID)) {
            trace.warn("connectionID missing from initial connections container in processLogin()");
            return;
        }
        Integer userID = new Integer(user.getID());
        ConnectionData data = getConnectionData(connectionID);
        data.setUserId(userID);
        s_userToConnectionTable.put(userID, connectionID);
        s_monitor.setUsername(connectionID.intValue(), user.getName());
        CoreServices.addConnection(data.getIP(), connectionID.intValue(), user.getID(), userName);
        if (user.isLevelTwoAdmin()) {
            if (trace.isDebugEnabled()) {
                trace.debug("Adding level two admin: connection=" + connectionID + ", userName = " + userName);
            }
            s_adminList.add(connectionID);
        }
        if (!VerifyService.isVerified(connectionID)) {
            trace.info("UNVERIFIED CLIENT! connection=" + connectionID + ", userName=" + userName);
        }

        Processor.checkRoundParticipation(user, connectionID);
        ResponseProcessor.loginSuccess(connectionID, requestType, user, autoRegisterForActiveLongRound);
        if (trace.isDebugEnabled()) {
            trace.debug("Login processed succesfully: connection=" + connectionID + ", userName = " + userName);
        }
    }

    /**
     * Each of the following methods is called from the dispatch method depending on the Request Type.
     */
    //handles reconnecting dropped orca clients and sending queued requests
    private static void reconnect(Integer connectionID, ReconnectRequest request) {
        //get user on connection ID and check hash
        String hash = (String) unsealObject(connectionID, request.getHash());
        if (trace.isDebugEnabled()) {
            trace.debug("RECONNECT " + request.getConnectionID() + ":" + hash);
        }
        int uid;
        List pendingResponses = ResponseProcessor.getPendingResponses(new Integer((int)request.getConnectionID()));
        try {
            uid = getUserID(new Integer((int) request.getConnectionID()));
        } catch (Exception e) {
            ResponseProcessor.reconnectFailure(connectionID, request.getRequestType(), "Reconnect Failed");
            return;
        }

        if (trace.isDebugEnabled()) {
            trace.debug("UID=" + uid);
        }

        User u = CoreServices.getUser(uid, false);
        if (trace.isDebugEnabled()) {
            trace.debug("U =" + u.getName());
        }
        try {
            if (!hash.equals(ResponseProcessor.hashForUser(new Integer((int) request.getConnectionID()), u))) {
                ResponseProcessor.reconnectFailure(connectionID, request.getRequestType(), "Invalid Hash " + ResponseProcessor.hashForUser(new Integer((int) request.getConnectionID()), u) + "!= " + hash);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            ResponseProcessor.reconnectFailure(connectionID, request.getRequestType(), "Exception");
            return;
        }


        //TODO: dequeue

        //TODO: setup watches
        //reestablishWatches



        //login new user
        if (!s_initialConnections.remove(connectionID)) {
            trace.warn("connectionID missing from initial connections container in processLogin()");
            return;
        }
        Integer userID = new Integer(u.getID());
        ConnectionData data = getConnectionData(connectionID);
        data.setUserId(userID);
        s_monitor.setUsername(connectionID.intValue(), u.getName());
        CoreServices.addConnection(data.getIP(), connectionID.intValue(), u.getID(), u.getName());
        if (u.isLevelTwoAdmin()) {
            if (trace.isDebugEnabled()) {
                trace.debug("Adding level two admin: connection=" + connectionID + ", userName = " + u.getName());
            }
            s_adminList.add(connectionID);
        }

        Processor.checkRoundParticipation(u, connectionID);

        //TODO: dequeue

        //setup watches again
        Processor.reestablishWatches(connectionID, uid);

        //kill existing user
        Processor.logout(new Integer((int) request.getConnectionID()), uid);
        CoreServices.removeConnection((int) request.getConnectionID());
        cleanupConnection(new Integer((int) request.getConnectionID()), uid, false);

        //need to get chat to work
        s_userToConnectionTable.put(userID, connectionID);

        //Close old connection
        ResponseProcessor.shutDownConnection(new Integer((int) request.getConnectionID()));
        DisconnectTimer.removeConnection((int) request.getConnectionID());

        //send success
        ResponseProcessor.reconnectSuccess(connectionID, request.getRequestType(), u, pendingResponses);
        if (trace.isDebugEnabled()) {
            trace.debug("Login processed succesfully: connection=" + connectionID + ", userName = " + u.getName());
        }
    }

    /**
     * Synchronizes the client clock with the server clock.
     */
    private static void synchTime(Integer connectionID, SynchTimeRequest synchRequest) {
        trace.debug("synch time on connectionID : " + connectionID);
        ResponseProcessor.synchTime(connectionID, synchRequest.getRequestType());
        if (trace.isDebugEnabled()) {
            trace.debug("SynchTime processed succesfully: connection=" + connectionID);
        }
    }

    /**
     * Validates the login request with the stored data for the user and udpates the
     * internal connection collections if successful.
     */
    //private static final int MAX_LOGIN_ATTEMPTS = 5;
    //private static Map s_loginAttempts = Collections.synchronizedMap( new HashMap() );
    private static void login(Integer connectionID, LoginRequest loginRequest) {
        String userId = loginRequest.getUserID();
        if (trace.isDebugEnabled()) {
            trace.debug("login on connectionID : " + connectionID + " from user '" + userId + "'");
        }
        String userName = null;
        User user = null;
        int requestType = loginRequest.getRequestType();
        if (loginRequest.getProtocolVersion() != ContestConstants.PROTOCOL_VERSION) {
            trace.error("Bad protocol version in login request from connectionID " + connectionID + " user " + userId);
            ResponseProcessor.loginFailed(connectionID, requestType, g_errorResources.getString("LOGIN_FAILURE"));
            return;
        }
        if (requestType == ContestConstants.LOGIN) {
            String password = (String) unsealObject(connectionID, loginRequest.getPassword());

            String firstName = loginRequest.getFirstName();
            String lastName = loginRequest.getLastName();
            String email = loginRequest.getEmail();
            String phoneNumber = loginRequest.getPhoneNumber();
            if (phoneNumber == null) {
                phoneNumber = "";
            }
            if (firstName != null || lastName != null || email != null) {
                if (firstName.equals(ContestConstants.SUN_PRACTICE_DUMMY_FIRST_NAME)) {
                    user = CoreServices.getUser(userId);
                    if (user == null) {
                        if (registerUser(userId, password, firstName, "dummyLastName", "dummyEmail", connectionID, requestType, "")) {
                            return;
                        }
                    }
                } else if (firstName != null && lastName != null && email != null) {
                    if (registerUser(userId, password, firstName, lastName, email, connectionID, requestType, phoneNumber)) {
                        return;
                    }
                } else {
                    String message = "To register, please, specify all fields: First Name, Last Name, Email.";
                    ResponseProcessor.loginFailed(connectionID, requestType, message);
                    return;
                }
            }

            userName = userId;
            String newHandle = loginRequest.getNewHandle();
            //s_loginAttempts.remove( userName );
            try {
                //We authenticate the user and get it
                User authenticatedUser = CoreServices.getAuthenticatedUser(userName, password, newHandle);
                String tcName = authenticatedUser.getName();
                if (tcName != null) {
                    WeakestLinkRound weakestLinkRound = CoreServices.getActiveWeakestLinkRound();
                    if (weakestLinkRound != null && weakestLinkRound.isWeakestLinkParticipant(tcName)) {
                        String ip = getConnectionData(connectionID).getIP();
                        if (!ip.startsWith(weakestLinkIPPrefix)) {
                            String message = "For this event you can login only from the on-site machines";
                            ResponseProcessor.loginFailed(connectionID, requestType, message);
                            return;
                        }
                        String badgeId = weakestLinkRound.getBadgeId(tcName);
                        if (badgeId == null) {
                            badgeId = loginRequest.getBadgeId();
                            if (badgeId == null) {
                                ResponseProcessor.noBadgeId(connectionID, requestType, tcName, sealObject(connectionID, password));
                                return;
                            }
                            weakestLinkRound.setBadgeId(tcName, badgeId);
                        }
                    }
                }
                if (tcName != null && CoreServices.isLoggedIn(tcName)) {
                    trace.info(tcName + " is already logged in.  Kicking previous login.");
                    Processor.kickUser(authenticatedUser.getID());
                }
                if (authenticatedUser.getEmailStatus() != 1) {
                    CoreServices.logout(authenticatedUser.getID());
                    ResponseProcessor.loginFailed(connectionID, requestType, "Please log into the TopCoder website and update your email address before logging in.");
                    return;
                }
                //Login the authenticated user
                CoreServices.doLogin(authenticatedUser);
                user = authenticatedUser;
            } catch (InvalidPasswordException e) {
                //trace.error("Errorloging in", e);
                ResponseProcessor.loginFailed(connectionID, requestType, g_errorResources.getString("LOGIN_FAILURE_PASSWORD"));
                return;
            } catch (HandleTakenException e) {
                ResponseProcessor.loginFailed(connectionID, requestType, g_errorResources.getString("LOGIN_FAILURE_TAKEN"));
                return;
            }
            if (newHandle != null && !user.getName().equals(newHandle)) {
                ResponseProcessor.error(connectionID, g_errorResources.getString("USER_EXISTS"));
            }
        } else if (requestType == ContestConstants.GUEST_LOGIN) {
            if (!s_processorSettings.getBoolean("processor.allowGuest", false)) {
                ResponseProcessor.loginFailed(connectionID, requestType, g_errorResources.getString("LOGIN_FAILURE_PASSWORD"));
                return;
            }
            user = CoreServices.guestLogin();
            userName = "Guest";
        } else if (requestType == ContestConstants.SPECTATOR_LOGIN) {
            String password = (String) unsealObject(connectionID, loginRequest.getPassword());
            try {
                user = CoreServices.spectatorLogin(userId, password);
            } catch (InvalidPasswordException e) {
                ResponseProcessor.loginFailed(connectionID, requestType, g_errorResources.getString("LOGIN_FAILURE_PASSWORD"));
                return;
            } catch (HandleTakenException e) {
                ResponseProcessor.loginFailed(connectionID, requestType, g_errorResources.getString("LOGIN_FAILURE_TAKEN"));
                return;
            }
            userName = "Spectator";
        } else if (requestType == ContestConstants.FORWARDER_LOGIN) {
            String password = (String) unsealObject(connectionID, loginRequest.getPassword());
            try {
                user = CoreServices.forwarderLogin(userId, password);
            } catch (InvalidPasswordException e) {
                ResponseProcessor.loginFailed(connectionID, requestType, g_errorResources.getString("LOGIN_FAILURE_PASSWORD"));
                return;
            } catch (HandleTakenException e) {
                ResponseProcessor.loginFailed(connectionID, requestType, g_errorResources.getString("LOGIN_FAILURE_TAKEN"));
                return;
            }
            userName = "Forwarder";
        }
        try {
            processLogin(connectionID, requestType, user, user.getName());
        } catch (Exception e) {
            if (requestType == ContestConstants.LOGIN) {
                ResponseProcessor.loginFailed(connectionID, requestType, g_errorResources.getString("LOGIN_FAILURE"));
                trace.error("Login failure on connection: " + connectionID, e);
            } else {
                ResponseProcessor.loginFailed(connectionID, requestType, g_errorResources.getString("GUEST_LOGIN_FAILURE"));
                trace.error("Guest Login failure on connection: " + connectionID, e);
            }
        }
    }

    /**
     * Validates the login request with the stored data for the user and udpates the
     * internal connection collections if successful.
     */
    //private static final int MAX_LOGIN_ATTEMPTS = 5;
    //private static Map s_loginAttempts = Collections.synchronizedMap( new HashMap() );
    private static void ssoLogin(Integer connectionID, SSOLoginRequest loginRequest) {
        String sso = loginRequest.getSSO();
        if (trace.isDebugEnabled()) {
            trace.debug("login on connectionID : " + connectionID + " using sso '" + sso + "'");
        }
        String userName = null;
        User user = null;
        int requestType = loginRequest.getRequestType();
        try {
            //We authenticate the user and get it
            User authenticatedUser = CoreServices.getAuthenticatedUser(sso);
            String tcName = authenticatedUser.getName();

            if (tcName != null && CoreServices.isLoggedIn(tcName)) {
                trace.info(tcName + " is already logged in.  Kicking previous login.");
                Processor.kickUser(authenticatedUser.getID());
            }
            if (authenticatedUser.getEmailStatus() != 1) {
                CoreServices.logout(authenticatedUser.getID());
                ResponseProcessor.loginFailed(connectionID, requestType, "Please log into the TopCoder website and update your email address before logging in.");
                return;
            }
            //Login the authenticated user
            CoreServices.doLogin(authenticatedUser);
            user = authenticatedUser;
        } catch (InvalidSSOException e) {
            //trace.error("Errorloging in", e);
            ResponseProcessor.loginFailed(connectionID, requestType, g_errorResources.getString("LOGIN_FAILURE_PASSWORD"));
            return;
        } catch (HandleTakenException e) {
            ResponseProcessor.loginFailed(connectionID, requestType, g_errorResources.getString("LOGIN_FAILURE_TAKEN"));
            return;
        }

        try {
            processLogin(connectionID, ContestConstants.LOGIN, user, user.getName());
        } catch (Exception e) {
            ResponseProcessor.loginFailed(connectionID, requestType, g_errorResources.getString("LOGIN_FAILURE"));
            trace.error("Login failure on connection: " + connectionID, e);
        }
    }

    /*public static void logoutDueToOtherLogin(Integer connectionID)
    {
    trace.debug("logout on connectionID: " + connectionID);
    int userID = getUserID(connectionID);
    User user = CoreServices.getUser(userID, false);
    if (user != null) {
    // Notify room of logout.
    if (user.getRoomID() >= 0) {
    EventService.sendResponseToRoom(user.getRoomID(), ResponseProcessor.leaveRoom(user, true));
    }
    ResponseProcessor.logout(connectionID, ContestConstants.LOGOUT);
    }
    Processor.logout(connectionID, userID);
    CoreServices.logout(userID);
    CoreServices.removeConnection(connectionID.intValue());
    cleanupConnection(connectionID, userID);
    trace.debug("logout complete");
    }*/
    private static void logout(Integer connectionID, LogoutRequest request) {
        if (trace.isDebugEnabled()) {
            trace.debug("logout on connectionID: " + connectionID);
        }
        int userID = getUserID(connectionID);
        User user = CoreServices.getUser(userID, false);

        if (user != null) {
            // Notify room of logout.
            if (user.getRoomID() >= 0) {
                EventService.sendResponseToRoom(user.getRoomID(), ResponseProcessor.leaveRoom(user, true));
            } else {
                trace.info("User " + userID + " logout without leave room: room ID=" + user.getRoomID() + " room type=" + user.getRoomType());
            }
        }

        Processor.logout(connectionID, userID);
        CoreServices.logout(userID);

        s_userToConnectionTable.remove(new Integer(userID));
        ConnectionData data = getConnectionData(connectionID);
        if (data != null) {
            data.setUserId(null);
        }

        if (request != null) {
            ResponseProcessor.logout(connectionID, request.getRequestType());
        }

        CoreServices.removeConnection(connectionID.intValue());
        cleanupConnection(connectionID, userID);
        trace.debug("logout complete");
    }

    private static ConnectionData getConnectionData(Integer connectionID) {
        return connectionData.get(connectionID);
    }

    private static void enterRound(Integer connectionID, EnterRoundRequest request) {
        trace.debug("enterRound");
        int userID = getUserID(connectionID);
        int roundID = (int) request.getRoundID();
        //User user = CoreServices.getUser(userID, false);
        if (trace.isDebugEnabled()) {
            trace.debug("enter roundID: " + roundID);
        }
        if (CoreServices.isRoundActive(roundID)) {
            Processor.enterRound(connectionID, userID, roundID);
            ResponseProcessor.move(connectionID, request.getRequestType(), userID);
        }

        /*
    int request_id = request.getRequestType();
    int request_type_id = ContestConstants.ENTER;
    int coder_id = userID;
    int round_id = roundID;
    int room_id = roundID;
    long timestamp = System.currentTimeMillis();
    int connection_id = connectionID.intValue();
    s_trackingQueue.addTracking(new Tracking(request_id, request_type_id, coder_id, round_id, room_id, timestamp, -1, connection_id, CoreServices.getServerID(), timestamp));
         */

    }

    private static void readMessage(Integer connectionID, ReadMessageRequest request) {
        int userID = getUserID(connectionID);
        int messageID = request.getMessageID();

        CoreServices.readMessage(userID, messageID);
    }

    private static void getMessages(Integer connectionID, GetImportantMessagesRequest request) {
        int userID = getUserID(connectionID);

        ImportantMessageData[] msg = CoreServices.getMessages(userID);

        ResponseProcessor.getMessages(connectionID, msg);
    }

    private static class MoveRoomLoadedListener implements RoomLoadedListener {

        private Integer connectionID;
        private int userID;
        private int type;
        private int roomID;
        private MoveRequest request;

        public MoveRoomLoadedListener(Integer connectionID, int userID, int roomType, int roomID, MoveRequest request) {
            this.connectionID = connectionID;
            this.userID = userID;
            this.type = roomType;
            this.roomID = roomID;
            this.request = request;
        }

        @Override
        public Integer getConnectionId() {
            return connectionID;
        }

        @Override
        public void roomLoaded(Room room) {
            int round_id = 0;

            if (type != ContestConstants.TEAM_CODER_ROOM && type != ContestConstants.LOBBY_ROOM && type != ContestConstants.CODER_ROOM && type != ContestConstants.MODERATED_CHAT_ROOM && type != ContestConstants.ADMIN_ROOM && type != ContestConstants.TEAM_ADMIN_ROOM) {
                round_id = roomID;
            } else {
                if (room != null && room instanceof BaseCodingRoom) {
                    round_id = ((BaseCodingRoom) room).getRoundID();
                }
            }

            Processor.move(connectionID, userID, type, roomID);
            ResponseProcessor.move(connectionID, request.getRequestType(), userID);
        }
    }

    private static void move(Integer connectionID, MoveRequest request) {
        trace.debug("move");
        int userID = getUserID(connectionID);
        int type = request.getMoveType();
        int roomID = request.getRoomID();
        User user = CoreServices.getUser(userID, false);
        if (trace.isDebugEnabled()) {
            trace.debug("move roomID: " + roomID + " type = " + type);
        }
        if (type == ContestConstants.LOBBY_ROOM && roomID == -1) {
            if (user.isLevelTwoAdmin()) {
                roomID = ContestConstants.ADMIN_LOBBY_ROOM_ID;
            } else {
                roomID = CoreServices.getFirstAvailableLobbyID();
            }
        }

        if (!CoreServices.isLobbyRoom(roomID) && !CoreServices.isPracticeRoomActive(roomID) && !CoreServices.isRoomActive(roomID)) {
            trace.error("Unable to move user " + userID + " to an inactive room " + roomID);
            return;
        }

        boolean loaded = CoreServices.getRoomAsync(roomID, new MoveRoomLoadedListener(connectionID, userID, type, roomID, request));

        if (!loaded) {
            // When the room cannot be immediately obtained, return a message
            trace.info("Waiting to load room " + roomID + " because user " + userID + " requested");
        }

        /*
    int round_id = 0;
    if (type != ContestConstants.TEAM_CODER_ROOM && type != ContestConstants.LOBBY_ROOM
    && type != ContestConstants.CODER_ROOM && type != ContestConstants.MODERATED_CHAT_ROOM
    && type != ContestConstants.ADMIN_ROOM && type != ContestConstants.TEAM_ADMIN_ROOM) {
    //System.out.println("here1, type="+type);
    round_id = roomID;
    } else {
    Room room = CoreServices.getRoom(roomID, false);
    //System.out.println("here2, room="+room);
    if (room != null && room instanceof ContestRoom) {
    round_id = ((ContestRoom) room).getRoundID();
    }
    }
    //        trace.info("round_id: " + round_id);
    //        trace.info("dest: " + roomID);
    Processor.move(connectionID, userID, type, roomID);
    ResponseProcessor.move(connectionID, request.getRequestType(), userID);*/
        /*
    int request_id = request.getRequestType();
    int request_type_id = ContestConstants.MOVE;
    int coder_id = userID;
    int room_id = roomID;
    long timestamp = System.currentTimeMillis();
    int connection_id = connectionID.intValue();
    s_trackingQueue.addTracking(new Tracking(request_id, request_type_id, coder_id, round_id, room_id, timestamp, -1, connection_id, CoreServices.getServerID(), timestamp));
         */
    }

    private static void enter(Integer connectionID, EnterRequest request) {
        int userID = getUserID(connectionID);
        int roomID = request.getRoomID();
        boolean enteringNewRoom = (roomID == -1);
        if (!enteringNewRoom && !CoreServices.isLobbyRoom(roomID) && !CoreServices.isPracticeRoomActive(roomID) && !CoreServices.isRoomActive(roomID)) {
            trace.error("Unable to let user " + userID + " enter an inactive room " + roomID);
            return;
        }
        Processor.enter(userID, enteringNewRoom);
    }

    private static void coderInfo(Integer connectionID, CoderInfoRequest request) {
        trace.debug("coderInfo");
        int userID = getUserID(connectionID);
        String userName = request.getCoder();
        int userType = request.getUserType();
        if (userName != null) {
            String coderInfo = CoreServices.getCoderInfo(userName, userType);
            ResponseProcessor.coderInfo(connectionID, coderInfo);
        } else {
            trace.error("Null user name requested in coderInfo");
        }
        /*
    int request_id = request.getRequestType();
    int request_type_id = ContestConstants.CODER_INFO;
    int coder_id = userID;
    long timestamp = System.currentTimeMillis();
    int connection_id = connectionID.intValue();
    s_trackingQueue.addTracking(new Tracking(request_id, request_type_id, coder_id, -1, -1, -1, -1, connection_id, CoreServices.getServerID(), timestamp));
         */
    }

    private static void processPopupResponse(Integer connectionID, GenericPopupRequest request) {
        trace.debug("processPopupResponse");
        int userID = getUserID(connectionID);

        if (request.getButton() != 0) {
            return;
        }
        switch (request.getPopupType()) {
        //         case ContestConstants.CONTEST_REGISTRATION:
        //         case ContestConstants.CONTEST_REGISTRATION_SURVEY:
        //              Processor.register(connectionID, userID, request.getSurveyData(), roundID);
        //              break;
        case ContestConstants.SUBMIT_PROBLEM:
            //TODO fix this
            Processor.submit(connectionID, userID, false, ((Integer) (request.getSurveyData().get(0))).intValue());
            break;
        default:
            throw new IllegalArgumentException("Bad popup type: " + request.getPopupType());
        }
    }

    private static void toggleChat(Integer connectionID) {
        trace.debug("toggleChat");
        int userID = getUserID(connectionID);
        Processor.toggleChat(userID);
    }

    private static void setLanguage(Integer connectionID, SetLanguageRequest request) {
        trace.debug("setLanguage");
        int userID = getUserID(connectionID);
        int languageID = request.getLanguageID();
        Processor.setLanguage(userID, languageID);
    }

    private static void loggedInUsers(Integer connectionID) {
        trace.debug("loggedInUsers");
        ResponseProcessor.loggedInUsers(connectionID, getUserID(connectionID));

        /*
    int request_id = ContestConstants.LOGGED_IN_USERS;
    int request_type_id = ContestConstants.LOGGED_IN_USERS;
    int coder_id = getUserID(connectionID);
    long timestamp = System.currentTimeMillis();
    int connection_id = connectionID.intValue();
    s_trackingQueue.addTracking(new Tracking(request_id, request_type_id, coder_id, -1, -1, -1, -1, connection_id, CoreServices.getServerID(), timestamp));
         */
    }

    private static void search(Integer connectionID, SearchRequest request) {
        trace.debug("search");
        int userID = getUserID(connectionID);
        String userName = request.getSearch();
        Processor.search(connectionID, userID, userName);

        /*
    int request_id = request.getRequestType();
    int request_type_id = ContestConstants.SEARCH;
    int coder_id = userID;
    long timestamp = System.currentTimeMillis();
    int connection_id = connectionID.intValue();
    s_trackingQueue.addTracking(new Tracking(request_id, request_type_id, coder_id, -1, -1, -1, -1, connection_id, CoreServices.getServerID(), timestamp));
         */

    }

    private static void chat(Integer connectionID, ChatRequest request) {
        trace.debug("chat");
        Processor.chat(connectionID, getUserID(connectionID), request.getMsg(), request.getScope());
    }

    private static void compile(Integer connectionID, CompileRequest request) {
        trace.debug("compile");
        String source = request.getCode().trim();
        int language = request.getLanguage();
        try {
            Processor.compile(getUserID(connectionID), source, language, request.getComponentID());
        } catch (Exception e) {
            trace.error(e);
            e.printStackTrace();
            String message = e.getMessage();
            if (message == null || message.trim().equals("")) {
                message = "Server Error while compiling.";
            }
            ResponseProcessor.error(connectionID, message);
        }
    }

    private static void coderHistory(Integer connectionID, CoderHistoryRequest request) {
        trace.debug("coderHistory");
        int userID = getUserID(connectionID);
        int roomID = request.getRoomID();
        int userType = request.getUserType();
        String challengeName = request.getHandle();
        int challengeID;
        if (userType == ContestConstants.SINGLE_USER) {
            challengeID = CoreServices.handleToID(challengeName).intValue();
        } else {
            challengeID = CoreServices.getTeam(challengeName, false).getID();
        }
        if (request.getHistoryType() == CoderHistoryRequest.TYPE_ALL) {
            Processor.getCoderHistory(connectionID, userID, roomID, challengeID);
        } else {
            Processor.getSubmissionHistory(connectionID, roomID, challengeID, request.getHistoryType() == CoderHistoryRequest.TYPE_SUBMISSIONS_EXAMPLE);
        }

        /*
    long timestamp = System.currentTimeMillis();
    int connection_id = connectionID.intValue();
    int request_id = request.getRequestType();
    int request_type_id = ContestConstants.CODER_HISTORY;
    int coder_id = userID;
    int round_id = 0;
    ContestRoom room = CoreServices.getContestRoom(roomID, false);
    if (room != null) {
    round_id = room.getRoundID();
    }
    s_trackingQueue.addTracking(new Tracking(request_id, request_type_id, coder_id, round_id, roomID, -1, -1, connection_id, CoreServices.getServerID(), timestamp));
         */
    }

    private static void longTestResults(Integer connectionID, LongTestResultsRequest request) {
        trace.debug("lastLongExampleResult");
        int requestingUserID = getUserID(connectionID);
        int roomID = request.getRoomID();
        String challengeName = request.getHandle();
        int coderId = CoreServices.handleToID(challengeName).intValue();
        Processor.getLongTestResults(connectionID, requestingUserID, coderId, roomID, request.getComponentID(), request.getResultType());

        /*
    long timestamp = System.currentTimeMillis();
    int connection_id = connectionID.intValue();
    int request_id = request.getRequestType();
    int request_type_id = ContestConstants.CODER_HISTORY;
    int coder_id = userID;
    int round_id = 0;
    ContestRoom room = CoreServices.getContestRoom(roomID, false);
    if (room != null) {
    round_id = room.getRoundID();
    }
    s_trackingQueue.addTracking(new Tracking(request_id, request_type_id, coder_id, round_id, roomID, -1, -1, connection_id, CoreServices.getServerID(), timestamp));
         */
    }

    private static void challengeInfo(Integer connectionID, ChallengeInfoRequest request) {
        trace.debug("challengeInfo");
        int userID = getUserID(connectionID);
        int componentID = request.getComponentID();
        String challengeName = request.getDefender();
        int challengeID = CoreServices.handleToID(challengeName).intValue();
        Processor.challengeInfo(connectionID, userID, challengeID, componentID);
    }

    /**
     * Interfaces with the core service for Compile/Test/Challenge.  Waits for the call to finish and sends a
     * response through the processor after completion.
     */
    private static void test(Integer connectionID, TestRequest request) {
        trace.debug("test");
        Processor.test(connectionID, getUserID(connectionID), request.getArgs().toArray(), request.getComponentID());
    }

    /**
     * Handles {@link BatchTestRequest} requests. It passes the request to {@link Processor}
     *
     * @param connectionID the connection id
     * @param request the request for batch testing
     * @since 1.2
     */
    private static void batchTest(Integer connectionID, BatchTestRequest request) {
        trace.debug("batch test");
        Processor.batchTest(connectionID, getUserID(connectionID), request.getTests().toArray(),
                request.getComponentID());
    }

    private static void getChallengeProblem(Integer connectionID, GetChallengeProblemRequest request) {
        trace.debug("getChallengeProblem");
        boolean pretty = request.isPretty();
        int roomID = request.getRoomID();
        BaseCodingRoom room = (BaseCodingRoom) CoreServices.getRoom(roomID, false);
        String defendantName = request.getDefendant();
        if (trace.isDebugEnabled()) {
            trace.debug("defendantName: " + defendantName);
        }
        int defenderID;
        if (room instanceof TeamContestRoom) {
            defenderID = CoreServices.getTeam(defendantName).getID();
        } else {
            defenderID = CoreServices.handleToID(defendantName).intValue();
        }
        int componentID = request.getComponentID();
        int challengerID = getUserID(connectionID);
        Processor.getChallengeComponent(connectionID, challengerID, pretty, roomID, defenderID, componentID);

        /*
    int request_id = request.getRequestType();
    int request_type_id = ContestConstants.GET_CHALLENGE_PROBLEM;
    int coder_id = challengerID;
    int round_id = 0;
    if (room != null) {
    round_id = room.getRoundID();
    }
    long timestamp = System.currentTimeMillis();
    int connection_id = connectionID.intValue();
    s_trackingQueue.addTracking(new Tracking(request_id, request_type_id, coder_id, round_id, roomID, -1, -1, connection_id, CoreServices.getServerID(), timestamp));
         */
    }

    private static void getSourceCode(Integer connectionID, GetSourceCodeRequest request) {
        trace.debug("getSourceCode");
        boolean pretty = request.isPretty();
        int roundId = request.getRoundId();
        LongContestRound round = (LongContestRound) CoreServices.getContestRound(roundId);
        String handle = request.getHandle();
        if (trace.isDebugEnabled()) {
            trace.debug("handle: " + handle);
        }
        int coderId = CoreServices.handleToID(handle).intValue();
        int componentID = request.getComponentId();
        int requesterId = getUserID(connectionID);
        Processor.getLongSourceCode(connectionID, requesterId, pretty, roundId, coderId, componentID, request.isExample(), request.getSubmissionNumber());
    }

    private static void challenge(Integer connectionID, ChallengeRequest request) {
        trace.debug("challenge");
        String defendantName = request.getDefender();
        User challenger = CoreServices.getUser(getUserID(connectionID), false);
        ContestRoom room = (ContestRoom) CoreServices.getRoom(challenger.getRoomID(), false);
        int defenderID;
        if (room instanceof TeamContestRoom) {
            defenderID = CoreServices.getTeam(defendantName).getID();
        } else {
            defenderID = CoreServices.handleToID(defendantName).intValue();
        }
        int componentID = request.getComponentID();
        ArrayList args = request.getTest();
        Processor.challenge(connectionID, getUserID(connectionID), defenderID, componentID, args.toArray());
    }

    private static void practiceSystemTest(Integer connectionID, PracticeSystemTestRequest request) {
        trace.debug("practiceSystemTest");
        Processor.practiceSystemTest(connectionID, getUserID(connectionID), request.getRoomID(), request.getComponentIds());
    }

    private static void clearPracticer(Integer connectionID, ClearPracticeRequest request) {
        trace.debug("clearPracticer");
        int userID = getUserID(connectionID);
        Processor.clearPracticer(connectionID, userID, request.getRoomID());
    }

    private static void clearPracticeProblem(Integer connectionID, ClearPracticeProblemRequest request) {
        trace.debug("clearPracticeProblem");
        int userID = getUserID(connectionID);
        Long[] tmp = request.getComponentID();
        Processor.clearPracticeProblem(connectionID, userID, request.getRoomID(), tmp);
    }

    private static void submit(Integer connectionID, SubmitRequest request) {
        trace.debug("submit");
        int userID = getUserID(connectionID);
        Processor.submit(connectionID, userID, true, request.getComponentID());
    }

    private static void submitLong(Integer connectionID, LongSubmitRequest request) {
        trace.debug("longSubmit");
        int userID = getUserID(connectionID);
        Processor.submitLong(connectionID, userID, request.isExample(), request.getComponentID(), request.getLanguageID(), request.getCode());
    }

    private static void viewQueue(Integer connectionID, ViewQueueRequest request) {
        trace.debug("viewQueue");
        Processor.viewLongQueueStatus(connectionID);
    }

    private static void save(Integer connectionID, SaveRequest request) {
        trace.debug("save");
        int userID = getUserID(connectionID);
        Processor.save(connectionID, userID, request.getCode(), request.getComponentID(), request.getLanguageID());
    }

    private static void closeProblem(Integer connectionID, CloseProblemRequest request) {
        trace.debug("closeProblem");
        int userID = getUserID(connectionID);
        Processor.closeProblem(userID, request.getWriter(), request.getProblemID());
    }

    private static void divSummaryRequest(Integer connectionID, DivSummaryRequest request) {
        int userID = getUserID(connectionID);
        int roundID = request.getRoundID();
        int divisionID = request.getDivisionID();
        if (trace.isDebugEnabled()) {
            trace.debug("div summary for userID: " + userID + " roundID: " + roundID + " divisionID: " + divisionID);
        }
        Processor.divSummary(connectionID, userID, roundID, divisionID, request.getRequestType());

    }

    private static void closeDivSummaryRequest(Integer connectionID, CloseDivSummaryRequest request) {
        int userID = getUserID(connectionID);
        int roundID = request.getRoundID();
        int divisionID = request.getDivisionID();
        if (trace.isDebugEnabled()) {
            trace.debug("close div summary for userID: " + userID + " roundID: " + roundID + " divisionID: " + divisionID);
        }
        Processor.closeDivSummary(connectionID, userID, roundID, divisionID, request.getRequestType());

    }

    private static void watch(Integer connectionID, WatchRequest request) {
        int userID = getUserID(connectionID);
        int roomID = request.getRoomID();
        if (trace.isDebugEnabled()) {
            trace.debug("watch for userID: " + userID + " roomID: " + roomID);
        }
        Processor.watch(connectionID, userID, roomID, request.getRequestType());

        /*
    int request_id = request.getRequestType();
    int request_type_id = ContestConstants.WATCH;
    int coder_id = userID;
    int round_id = 0;
    ContestRoom room = (ContestRoom) CoreServices.getRoom(roomID, false);
    if (room != null) {
    round_id = room.getRoundID();
    }
    long timestamp = System.currentTimeMillis();
    int connection_id = connectionID.intValue();
    s_trackingQueue.addTracking(new Tracking(request_id, request_type_id, coder_id, round_id, roomID, timestamp, -1, connection_id, CoreServices.getServerID(), timestamp));
         */
    }

    private static void unwatch(Integer connectionID, UnwatchRequest request) {
        trace.debug("unwatch");
        int userID = getUserID(connectionID);
        int roomID = request.getRoomID();
        Processor.unwatch(connectionID, userID, roomID);

        /*
    int request_id = request.getRequestType();
    int request_type_id = ContestConstants.UNWATCH;
    int coder_id = userID;
    int round_id = 0;
    ContestRoom room = (ContestRoom) CoreServices.getRoom(roomID, false);
    if (room != null) {
    round_id = room.getRoundID();
    }
    long timestamp = System.currentTimeMillis();
    int connection_id = connectionID.intValue();
    s_trackingQueue.addTracking(new Tracking(request_id, request_type_id, coder_id, round_id, roomID, -1, timestamp, connection_id, CoreServices.getServerID(), timestamp));
         */
    }

    private static void registerInfo(Integer connectionID, RegisterInfoRequest request) {
        int roundID = request.getRoundID();
        trace.debug("registerInfo");
        int userID = getUserID(connectionID);
        Processor.registerInfo(connectionID, userID, roundID);
    }

    private static void register(Integer connectionID, RegisterRequest request) {
        trace.debug("register");
        int userID = getUserID(connectionID);
        Processor.register(connectionID, userID, request.getSurveyData(), request.getRoundID());
    }

    private static void registerUsers(Integer connectionID, RegisterUsersRequest request) {
        trace.debug("registerUsers");

        // TODO get from request
        int roundID = request.getRoundID();
        int userID = getUserID(connectionID);
        Round contest = CoreServices.getContestRound(roundID);
        Registration reg = null;
        if (contest != null) {
            reg = CoreServices.getRegistration(contest.getRoundID());
        } else {
            //What is this for!?
            reg = new Registration(0, Rating.ALGO);
        }
        ResponseProcessor.registerUsers(connectionID, roundID, reg);

    }

    private static void testInfo(Integer connectionID, TestInfoRequest request) {
        trace.debug("testInfo");
        int userID = getUserID(connectionID);
        Processor.testInfo(connectionID, userID, request.getComponentID());
    }

    private static void getLeaderBoard(Integer connectionID) {

        /*
    int userID = getUserID(connectionID);
    int request_id = ContestConstants.GET_LEADER_BOARD;
    int request_type_id = ContestConstants.GET_LEADER_BOARD;
    int coder_id = userID;
    int round_id = -1;
    long timestamp = System.currentTimeMillis();
    int connection_id = connectionID.intValue();
    s_trackingQueue.addTracking(new Tracking(request_id, request_type_id, coder_id, round_id, -1, timestamp, -1, connection_id, CoreServices.getServerID(), timestamp));
         */
    }

    private static void closeLeaderBoard(Integer connectionID) {

        /*
    int userID = getUserID(connectionID);
    int request_id = ContestConstants.CLOSE_LEADER_BOARD;
    int request_type_id = ContestConstants.CLOSE_LEADER_BOARD;
    int coder_id = userID;
    int round_id = -1;
    long timestamp = System.currentTimeMillis();
    int connection_id = connectionID.intValue();
    s_trackingQueue.addTracking(new Tracking(request_id, request_type_id, coder_id, round_id, -1, timestamp, -1, connection_id, CoreServices.getServerID(), timestamp));
         */
    }

    private static void openSummary(Integer connectionID) {

        /*
    int userID = getUserID(connectionID);
    int request_id = ContestConstants.OPEN_SUMMARY_REQUEST;
    int request_type_id = ContestConstants.OPEN_SUMMARY_REQUEST;
    int coder_id = userID;
    int round_id = 0;
    long timestamp = System.currentTimeMillis();
    int connection_id = connectionID.intValue();
    s_trackingQueue.addTracking(new Tracking(request_id, request_type_id, coder_id, round_id, -1, timestamp, -1, connection_id, CoreServices.getServerID(), timestamp));
         */
    }

    private static void closeSummary(Integer connectionID) {

        /*
    int userID = getUserID(connectionID);
    int request_id = ContestConstants.CLOSE_SUMMARY_REQUEST;
    int request_type_id = ContestConstants.CLOSE_SUMMARY_REQUEST;
    int coder_id = userID;
    int round_id = 0;
    long timestamp = System.currentTimeMillis();
    int connection_id = connectionID.intValue();
    s_trackingQueue.addTracking(new Tracking(request_id, request_type_id, coder_id, round_id, -1, timestamp, -1, connection_id, CoreServices.getServerID(), timestamp));
         */
    }

    private static void keepAlive(Integer connectionID, KeepAliveRequest request) {
        trace.debug("keep alive");
        ResponseProcessor.keepAlive(connectionID, request.getRequestType());
    }

    private static void sendAdminBroadcasts(Integer connectionID) {
        AdminBroadcastManager.getInstance().sendRecentBroadcasts(connectionID, getUserID(connectionID));
    }

    private static void addTeamMember(Integer connectionID, AddTeamMemberRequest request) {
        int userID = getUserID(connectionID);
        String nameToAdd = request.getUserHandle();
        Processor.addTeamMember(userID, nameToAdd);
    }

    private static void removeTeamMember(Integer connectionID, RemoveTeamMemberRequest request) {
        int userID = getUserID(connectionID);
        String nameToRemove = request.getUserHandle();
        Processor.removeTeamMember(userID, nameToRemove);
    }

    private static void joinTeam(Integer connectionID, JoinTeamRequest request) {
        int userID = getUserID(connectionID);
        Processor.joinTeam(userID, request.getTeamName());
    }

    /*
    private static void leaveTeam(Integer connectionID, LeaveTeamRequest request )
    {
    int userID = getUserID(connectionID);
    Processor.leaveTeam(userID);
    }
     */
    private static void leaveTeam(Integer connectionID) {
        int userID = getUserID(connectionID);
        Processor.leaveTeam(userID);
    }

    /*
    private static void getTeamList(Integer connectionID, GetTeamListRequest request) {
    int userID = getUserID(connectionID);
    Processor.getTeamList(connectionID, userID);
    }
     */
    private static void getTeamList(Integer connectionID) {
        int userID = getUserID(connectionID);
        Processor.getTeamList(connectionID, userID);
    }

    /*
    private static void closeTeamList(Integer connectionID, CloseTeamListRequest request) {
    int userID = getUserID(connectionID);
    Processor.closeTeamList(connectionID, userID);
    }
     */
    private static void closeTeamList(Integer connectionID) {
        int userID = getUserID(connectionID);
        Processor.closeTeamList(connectionID, userID);
    }

    private static void assignComponents(Integer connectionID, AssignComponentsRequest request) {
        Processor.assignComponents(connectionID, getUserID(connectionID), request.getData());
    }

    private static void openComponent(Integer connectionID, OpenComponentForCodingRequest request) {
        //String handle = request.getHandle();
        int userID;
        userID = getUserID(connectionID);
        Processor.openComponent(connectionID, userID, request.getComponentID());
    }

    private static void openProblem(Integer connectionID, OpenProblemForReadingRequest request) {
        /*
        String handle = request.getHandle();
        int userID;
        if (handle == null) {
            userID = getUserID(connectionID);
        } else {
            userID = CoreServices.handleToID(request.getHandle()).intValue();
        }
         */
        int userID = getUserID(connectionID);
        Processor.openProblem(connectionID, request.getRoundId(), userID, request.getProblemID());
    }

    private static void visitedPractice(Integer connectionID) {
        int userID = getUserID(connectionID);
        ResponseProcessor.visitedPractice(connectionID, userID);
    }

    private static void roundSchedule(Integer connectionID, RoundScheduleRequest request) {
        if (!CoreServices.isRoundActive(request.getRoundID())) {
            ResponseProcessor.error(connectionID, "The round is not active.");
            return;
        }
        ResponseProcessor.roundSchedule(connectionID, request.getRoundID());
    }

    private static WeakestLinkRound getWeakestLinkRound(int roundId) {
        return CoreServices.getWeakestLinkRound(roundId);
    }

    private static void vote(Integer connectionId, VoteRequest voteRequest) {
        int userId = getUserID(connectionId);
        String coderName = voteRequest.getCoderName();
        assertNotNull(coderName);
        int roundId = voteRequest.getRoundId();
        if (!CoreServices.isRoundActive(roundId) && !CoreServices.isPracticeRoundActive(roundId)) {
            ResponseProcessor.error(connectionId, "You cannot vote for an inactive round.");
            return;
        }
        WeakestLinkRound contestRound = getWeakestLinkRound(roundId);
        int victimId = CoreServices.handleToID(coderName).intValue();
        contestRound.receivedVote(userId, victimId);
        specAppProcessor.announceWeakestLinkVote(userId, victimId);
    }

    private static void assertNotNull(Object object) {
        assertTrue(object != null);
    }

    private static void assertTrue(boolean condition) {
        if (!condition) {
            throw new RuntimeException();
        }
    }

    private static void roundStatsRequest(Integer connectionID, RoundStatsRequest roundStatsRequest) {
        int roundId = roundStatsRequest.getRoundId();
        if (!CoreServices.isRoundActive(roundId) && !CoreServices.isPracticeRoundActive(roundId)) {
            ResponseProcessor.error(connectionID, "You cannot get statistics for an inactive round.");
            return;
        }
        String coderName = roundStatsRequest.getCoderName();
        WeakestLinkRound contestRound = getWeakestLinkRound(roundId);
        contestRound.roundStatsRequest(connectionID, coderName);
    }

    private static void wlMyTeamInfoRequest(Integer connectionID, WLMyTeamInfoRequest wlMyTeamInfoRequest) {
        int userId = getUserID(connectionID);
        int roundId = wlMyTeamInfoRequest.getRoundId();
        if (!CoreServices.isRoundActive(roundId) && !CoreServices.isPracticeRoundActive(roundId)) {
            ResponseProcessor.error(connectionID, "You cannot get your team information for an inactive round.");
            return;
        }
        WeakestLinkRound contestRound = getWeakestLinkRound(roundId);
        contestRound.myTeamInfoRequest(connectionID, userId);
    }

    private static void wlTeamsInfoRequest(Integer connectionID, WLTeamsInfoRequest wlTeamsInfoRequest) {
        int userId = getUserID(connectionID);
        int roundId = wlTeamsInfoRequest.getRoundId();
        if (!CoreServices.isRoundActive(roundId) && !CoreServices.isPracticeRoundActive(roundId)) {
            ResponseProcessor.error(connectionID, "You cannot get all team information for an inactive round.");
            return;
        }
        WeakestLinkRound contestRound = getWeakestLinkRound(roundId);
        contestRound.teamsInfoRequest(connectionID, userId);
    }

    private final static String HANDLE_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            "abcdefghijklmnopqrstuvwxyz" +
            "0123456789" +
            "-_.{}[]()";

    private static boolean registerUser(String userId, String password, String firstName, String lastName, String email,
            Integer connectionID, int requestType, String phoneNumber) {
        if (userId.length() < 1) {
            ResponseProcessor.loginFailed(connectionID, requestType, "Empty username");
            return true;
        }
        if (userId.length() > 15) {
            ResponseProcessor.loginFailed(connectionID, requestType, "Username is too long (should be no more than 15 characters)");
            return true;
        }
        for (int i = 0; i < userId.length(); i++) {
            char c = userId.charAt(i);
            if (HANDLE_ALPHABET.indexOf(c) == -1) {
                ResponseProcessor.loginFailed(connectionID, requestType, "Invalid character in username: " + c);
                return true;
            }
        }
        if (password.length() < 7) {
            ResponseProcessor.loginFailed(connectionID, requestType, "Password is too short (should be at least 7 characters)");
            return true;
        }
        if (password.length() > 15) {
            ResponseProcessor.loginFailed(connectionID, requestType, "Password is too long (should be no more than 15 characters)");
            return true;
        }
        RegistrationResult registrationResult = CoreServices.registerUser(userId, password, firstName, lastName, email, phoneNumber);
        if (!registrationResult.isRegistered()) {
            String message = registrationResult.getMessage();
            ResponseProcessor.loginFailed(connectionID, requestType, message);
            return true;
        }
        return false;
    }

    private static void autoSystestRequest(Integer connectionID, AutoSystestRequest request) {
        Processor.autoSystemTest(connectionID, getUserID(connectionID), request.getRoundID());
    }

    //Used on Showdown rounds
    private static void systestResultsRequest(Integer connectionID, SystestResultsRequest request) {
        int userID = getUserID(connectionID);
        User user = CoreServices.getUser(userID, false);
        if (!CoreServices.isRoundActive(request.getRoundID()) && !CoreServices.isPracticeRoundActive(request.getRoundID())) {
            ResponseProcessor.error(connectionID, "You cannot get system test results for an inactive round.");
            return;
        }
        Round round = CoreServices.getContestRound(request.getRoundID());

        CoreServices.processSystestResultsRequest(connectionID, user, round);
    }
    //TODO: Change this to a config file, preferrably a reloadable one.
    private static final String currentAppletVersion = s_processorSettings.getString("currentAppletVersion");

    private static void getAppletVersion(Integer connectionID, GetCurrentAppletVersionRequest getCurrentAppletVersionRequest) {
        ResponseProcessor.currentAppletVersion(connectionID, getCurrentAppletVersionRequest.getRequestType(), currentAppletVersion);
    }

    public static void bootUser(Integer connectionID) {
        move(connectionID, new MoveRequest(ContestConstants.LOBBY_ROOM, ContestConstants.ANY_ROOM));
    }

    private static void getVerifyClass(Integer connectionID, VerifyRequest request) {
        ResponseProcessor.sendVerifyClass(connectionID, request.getRequestType(), VerifyService.buildVerifyClass(connectionID));
    }

    private static void verifyClient(Integer connectionID, VerifyResultRequest request) {
        boolean success = VerifyService.verifyClient(connectionID, request.getVerification());

        // Here, we only log the sucess or failure in order not to alert the fake clients
        if (!success) {
            trace.info("Verification failed for connection ID=" + connectionID);
        }

        // Always send a success message
        //ResponseProcessor.sendVerifyResult(connectionID, request.getRequestType(), true);

        // Turn on the verification
        ResponseProcessor.sendVerifyResult(connectionID, request.getRequestType(), success);
    }

    private static void clientErrorReport(Integer connectionID, ErrorReportRequest request) {
        trace.error("Client reported an error for connection ID=" + connectionID + ", JVM version = " + request.getJVMVersion() + ", JVM vendor = " + request.getJVMVendor() + ", error = " + request.getError());
    }

    private static void ping(Integer connectionID, PingRequest request) {
        ResponseProcessor.sendPong(connectionID, request.getPayload());
    }

    private static void exchangeKey(Integer connectionID, ExchangeKeyRequest request) {
        MessageEncryptionHandler handler = new MessageEncryptionHandler();
        handler.setRequestKey(request.getKey());
        byte[] key = handler.generateReplyKey();
        getConnectionData(connectionID).setEncriptionKey(handler.getFinalKey());
        ResponseProcessor.sendExchangeKeyResponse(connectionID, request.getRequestType(), key);
    }

    public static Object unsealObject(Integer connectionID, SealedSerializable obj) {
        Key key = getConnectionData(connectionID).getEncriptionKey();
        try {
            if (key != null) {
                return MessageEncryptionHandler.unsealObject(obj, key);
            } else {
                return obj.getObject(new NullCipher());
            }
        } catch (Exception e) {
            trace.error("Decrypting object fails, connection=" + connectionID, e);
            return null;
        }
    }

    public static SealedSerializable sealObject(Integer connectionID, Object obj) {
        Key key = getConnectionData(connectionID).getEncriptionKey();
        try {
            if (key != null) {
                return MessageEncryptionHandler.sealObject(obj, key);
            } else {
                return new SealedSerializable(obj, new NullCipher());
            }
        } catch (Exception e) {
            trace.error("Encrypting object fails, connection=" + connectionID, e);
            return null;
        }
    }

    private static class ConnectionData {
        private volatile Integer userId;
        private volatile String IP;
        private volatile int errorCount;
        private volatile Key encriptionKey;
        private volatile int requestCount;

        public boolean isConnected() {
            return userId != null;
        }

        public int incErrorCount() {
            return ++errorCount;
        }

        void setUserId(Integer userId) {
            this.userId = userId;
        }

        Integer getUserId() {
            return userId;
        }

        void setIP(String iP) {
            IP = iP;
        }

        String getIP() {
            return IP;
        }

        void setEncriptionKey(Key encriptionKey) {
            this.encriptionKey = encriptionKey;
        }

        Key getEncriptionKey() {
            return encriptionKey;
        }

        int decRequestCount() {
            return --requestCount;
        }

        int incRequestCount() {
            return ++requestCount;
        }

        int incRequestCount(int value) {
            requestCount += value;
            return requestCount;
        }

        void setRequestCount(int requestCount) {
            this.requestCount = requestCount;
        }

        int getRequestCount() {
            return requestCount;
        }
    }

    /**
     * Handles RegisteredRoundListRequest requests.
     * Calls ResponseProcessor, if the user is valid, which retrieves the list of
     * registered rounds and returns them to the user.
     *
     * @param connectionID connection id of the user who requested
     * @since 1.1
     */
    private static void getRegisteredRoundList(Integer connectionID) {
        if (trace.isDebugEnabled()) {
            trace.debug("getRegisteredRoundList: connectionID = " + connectionID);
        }
        try {
            int userID = getUserID(connectionID);
            User user = CoreServices.getUser(userID);
            ResponseProcessor.getRegisteredRoundList(connectionID, user);
        } catch (IllegalArgumentException e) {
            trace.error("getRegisteredRoundList: Bad connectionID " + connectionID);
        }
    }

    /**
     * Handles RoundProblemsRequest requests.
     * Calls ResponseProcessor to return the list of problems of specific round and division.
     *
     * @param connectionID connection id of the user who requested
     * @param request the request which contains the round and division for which problems are
     *                retrieved
     * @since 1.1
     */
    private static void getRoundProblems(Integer connectionID, RoundProblemsRequest request) {
        if (trace.isDebugEnabled()) {
            trace.debug("getRoundProblems: connectionID = " + connectionID);
        }
        try {
            int userID = getUserID(connectionID);
            User user = CoreServices.getUser(userID);
            ResponseProcessor.getRoundProblems(connectionID, user, request.getRoundID(), request.getDivisionID());
        } catch (IllegalArgumentException e) {
            trace.error("getRoundProblems: Bad connectionID " + connectionID);
        }
    }
}
