/*
* Copyright (C) - 2014 TopCoder Inc., All Rights Reserved.
*/

package com.topcoder.server.services;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;
import java.util.Timer;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import com.topcoder.netCommon.contest.ComponentAssignmentData;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contest.TeamConstants;
import com.topcoder.netCommon.contest.round.text.ComponentNameBuilder;
import com.topcoder.netCommon.contestantMessages.AdminBroadcast;
import com.topcoder.netCommon.contestantMessages.ComponentBroadcast;
import com.topcoder.netCommon.contestantMessages.RoundBroadcast;
import com.topcoder.netCommon.contestantMessages.response.data.CategoryData;
import com.topcoder.server.AdminListener.response.CommandFailedResponse;
import com.topcoder.server.AdminListener.response.CommandResponse;
import com.topcoder.server.AdminListener.response.CommandSucceededResponse;
import com.topcoder.server.TopicListener.ChatArchiveListener;
import com.topcoder.server.common.ActionEvent;
import com.topcoder.server.common.BaseCoderComponent;
import com.topcoder.server.common.BaseCodingRoom;
import com.topcoder.server.common.BaseRound;
import com.topcoder.server.common.ChallengeAttributes;
import com.topcoder.server.common.Coder;
import com.topcoder.server.common.CoderFactory;
import com.topcoder.server.common.ContestEvent;
import com.topcoder.server.common.ContestRoom;
import com.topcoder.server.common.ContestRound;
import com.topcoder.server.common.EventRegistration;
import com.topcoder.server.common.LeaderBoard;
import com.topcoder.server.common.MoveEvent;
import com.topcoder.server.common.Rating;
import com.topcoder.server.common.Registration;
import com.topcoder.server.common.RegistrationResult;
import com.topcoder.server.common.Results;
import com.topcoder.server.common.Room;
import com.topcoder.server.common.Round;
import com.topcoder.server.common.RoundComponent;
import com.topcoder.server.common.RoundProblem;
import com.topcoder.server.common.TCEvent;
import com.topcoder.server.common.Team;
import com.topcoder.server.common.TeamCoder;
import com.topcoder.server.common.Tracking;
import com.topcoder.server.common.User;
import com.topcoder.server.common.UserTestAttributes;
import com.topcoder.server.common.WeakestLinkData;
import com.topcoder.server.common.WeakestLinkRound;
import com.topcoder.server.common.WeakestLinkTeam;
import com.topcoder.server.contest.ImportantMessageData;
import com.topcoder.server.contest.RoundTimerTaskSet;
import com.topcoder.server.ejb.DBServices.DBServices;
import com.topcoder.server.ejb.DBServices.DBServicesException;
import com.topcoder.server.ejb.DBServices.DBServicesLocator;
import com.topcoder.server.ejb.ProblemServices.ProblemServices;
import com.topcoder.server.ejb.ProblemServices.ProblemServicesException;
import com.topcoder.server.ejb.ProblemServices.ProblemServicesLocator;
import com.topcoder.server.ejb.TestServices.LongContestServicesLocator;
import com.topcoder.server.ejb.TestServices.TestServicesLocator;
import com.topcoder.server.ejb.TrackingServices.TrackingServices;
import com.topcoder.server.processor.AdminBroadcastManager;
import com.topcoder.server.processor.Processor;
import com.topcoder.server.processor.RequestProcessor;
import com.topcoder.server.processor.ResponseProcessor;
import com.topcoder.server.services.AsyncRoomLoader.RoomLoadedListener;
import com.topcoder.server.services.authenticate.Authenticator;
import com.topcoder.server.services.authenticate.HandleTakenException;
import com.topcoder.server.services.authenticate.InvalidPasswordException;
import com.topcoder.server.services.authenticate.InvalidSSOException;
import com.topcoder.server.services.authenticate.TCAuthenticator;
import com.topcoder.server.util.ArrayUtils;
import com.topcoder.shared.problem.Problem;
import com.topcoder.shared.problem.ProblemComponent;
import com.topcoder.shared.problem.SimpleComponent;
import com.topcoder.shared.util.Formatters;
import com.topcoder.shared.util.SimpleResourceBundle;
import com.topcoder.shared.util.logging.Logger;
import com.topcoder.util.cache.SoftReferenceLRUCache;

/**
 * <p>
 * This class will contain all the static methods for use by the request processors or anyone else
 * who wishes to access/save any data
 * </p>
 * <p>
 * Changes in version 1.0 (TopCoder Competition Engine - Event Support For Registration v1.0):
 * <ol>
 * <li>Added {@link #getUserFromDB(int)} to always get user from db.</li>
 * <li>Added {@link #getEventRegistration(int,int)} to get event registration data.</li>
 * <li>Added {@link #getEventRegistrationFromDB(int)} to get event registration data from DB. </li>
 * <li>Update {@link #getContestRound(int)} to store the round event data.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in 1. (Fix Tester Choosing Issue for Testing Writer Solution v1.0):
 * <ol>
 * <li>Added {@link #removeSimpleComponentFromCache(int componentID)}  method.</li>
 * </ol>
 * </p>
 *
 *
 * <p>
 * Changes in version 1.2 (TopCoder Competition Engine - Revise Authentication Logic for SSO v1.0):
 * <ol>
 *      <li>Updated {@link #authenticateUser(String)} method.</li>
 * </ol>
 * </p>
 *
 * <p>
<<<<<<< HEAD
 * Changes in version 1.3 (Web Arena UI Member Photo Display v1.0):
 * <ol>
 *      <li>Add {@link #getMemberPhotoPath(int)} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.4(Module Assembly - Web Socket Listener - Track User Actions From Web Arena):
 * <ol>
 *     <li>Added {@link #recordUserAction(String, Object, String)} method.</li>
 *     <li>Added {@link #getHandleBySSO(String)} method.</li>
 * </ol>
 * </p>
 *
 * @author savon_cn, freegod
 * @version 1.4
 */

public final class CoreServices {

    /**
     * Category for logging.
     */
    private static final Logger s_trace = Logger.getLogger(CoreServices.class);

    private static ConnectionArchiver connectionArchiver;

    private static int serverID;
    private static final int LOBBY_ROLLOVER = 300;


    private static final Timer roundTimer = new Timer();
    private static final Map roundTimerTasks = new ConcurrentHashMap();
    private static final Set activeRounds = new HashSet();
    private static final Map activeRoundRefs = new HashMap();
    private static  AsyncRoomLoader roomLoader;
    
    private CoreServices() {
    }

    /**
     * Called when a listener starts up
     */
    public static void start() {
        info("start");

        try {
            s_dbServices.deleteConnections();
        } catch (Exception e) {
            s_trace.error("Error in DB.deleteConnections()", e);
        }

        s_trace.debug("Loading user name -> user id info.");
        //No more of this stuff
        //if (!loadHandleToIDMap()) {
        //    s_trace.error("Failed loading handle to user id info, exiting.");
        //    System.exit(-1);
        //}
        s_trace.debug("Done loading user name -> user id info.");

        // check if the lobby exists
        initLobby();

        try {
            serverID = s_dbServices.getNextServerID();
            System.out.println(serverID);
        } catch (Exception e) {
            s_trace.error(e);
            serverID = -2;
        }

        connectionArchiver = new ConnectionArchiver(s_dbServices);
        connectionArchiver.start();

        CompileService.init();

        // start a chat archiver
        Thread t = new Thread(new ChatArchiveListener(), "ChatArchive");
        t.setDaemon(true);
        t.start();

        int maxThreads;
        try {
            maxThreads = Integer.parseInt(s_coreSettings.getString("asyncRoomLoader.threads"));
        } catch (Exception e1) {
            s_trace.warn("Could not obtain asyncRoomLoader.threads. Setting to 2", e1);
            maxThreads = 2;
        }
        // Start a new room loader
        roomLoader = new AsyncRoomLoader(maxThreads);
        roomLoader.start();

        synchronized (s_loginLock) {
            HashSet map = (HashSet) safeCacheGet(LOGGEDIN_USERIDS_KEY, false);
            if (map == null) {
                map = new HashSet(500);
                s_cache.addRef(LOGGEDIN_USERIDS_KEY);
                saveToCache(LOGGEDIN_USERIDS_KEY, map);
            }
            ArrayList userNames = (ArrayList) safeCacheGet(LOGGEDIN_USERNAMES_KEY, false);
            if (userNames == null) {
                userNames = new ArrayList();
                s_cache.addRef(LOGGEDIN_USERNAMES_KEY);
                saveToCache(LOGGEDIN_USERNAMES_KEY, userNames);
            }
            ArrayList userRatings = (ArrayList) safeCacheGet(LOGGEDIN_USERRATINGS_KEY, false);
            if (userRatings == null) {
                userRatings = new ArrayList();
                s_cache.addRef(LOGGEDIN_USERRATINGS_KEY);
                saveToCache(LOGGEDIN_USERRATINGS_KEY, userRatings);
            }
        }

        // prime the active contest, TODO: better way to do this.
        //        loadNextContestRound();

        try {
            if (s_coreSettings != null) {
                boolean preloadRooms = s_coreSettings.getBoolean("preload.rooms");
                if (preloadRooms) {
                    info("Loading practice rooms");
                    ArrayList practiceContestIDs = getPracticeRoundIDs();
                    for (int i = 0; i < practiceContestIDs.size(); i++) {
                        int roundID = ((Integer) practiceContestIDs.get(i)).intValue();
                        Round contestRound = CoreServices.getContestRound(roundID, false);

                        // Since we use room ID sequence, there is no guarantee that round ID is room ID.
                        for (Iterator iter = contestRound.getAllRoomIDs(); iter.hasNext();) {
                            getRoom(((Integer) iter.next()).intValue(), false);
                        }
                    }
                    info(practiceContestIDs.size() + " practice rooms loaded");
                }

                //                s_spectatorRoomId = Integer.parseInt(s_coreSettings.getString("spectator.roomId"));
            }
            authenticator = s_coreSettings.getString("authenticator").trim();
            auth = (Authenticator) Class.forName(authenticator).newInstance();
            info("Authenticator = " + authenticator);
        } catch (Exception e) {
            s_trace.error("Exception", e);
        }

        // seed arraylist of guest ids
        for (int i = 0; i < 100; i++) {
            s_guestIDs.add(new Integer(200 - i));
        }
        startAutoSystem();
    }

    private static void initCache() {
        int maxCacheSize = Integer.MAX_VALUE;
        try {
            maxCacheSize = Integer.parseInt(s_coreSettings.getString("cache.maxSize"));
        } catch (Exception e) {
            s_trace.warn("Could not obtain cache.maxsize properties. Using default",e);
        }
        SoftReferenceLRUCache nonPermMap = new SoftReferenceLRUCache(maxCacheSize);
        s_cache = new SimpleCache(nonPermMap);
    }

    private static void startAutoSystem() {
        s_autoSystemTest = "true".equals(s_coreSettings.getString("autoSystemTest"));
        if (s_autoSystemTest) {
            long minTimeBetweenChallengeCollection = s_coreSettings.getLong("autoSystemTest.minMsIntervalBetweenTestCaseCollection");
            int maxNumberOfAutoSystemTestEnqueuers = s_coreSettings.getInt("autoSystemTest.maxNumberOfAutoSystemTestEnqueuers");
            int maxNumberOfAutoSystemTestResultsReporters = s_coreSettings.getInt("autoSystemTest.maxNumberOfAutoSystemTestResultsReporters");
            long minTimeBetweenResultReports = s_coreSettings.getLong("autoSystemTest.minMsBetweenResultReport");
            int maxSizeResultReportBatch = s_coreSettings.getInt("autoSystemTest.maxSizeResultReportBatch");


            SRMTestScheduler.enableAutoSystemTests(minTimeBetweenChallengeCollection,
                                                   maxNumberOfAutoSystemTestEnqueuers,
                                                   maxNumberOfAutoSystemTestResultsReporters,
                                                   minTimeBetweenResultReports,
                                                   maxSizeResultReportBatch);

        }
    }

    private static String authenticator = "TCAuthenticator";
    private static Authenticator auth = new TCAuthenticator();

    /**
     * Called when the listener shutsdown
     */
    public static void stop() {
        roundTimer.cancel();
        //roundTimer = null;
        if (connectionArchiver != null) {
            connectionArchiver.stop();
        }
        s_cache.clearRef();
        roomLoader.stop();
        info("stop");
    }


    private static DBServices s_dbServices;
    private static TrackingServices s_trackingServices;
    private static ProblemServices s_problemServices;

    private static SimpleCache s_cache;
    private static Set s_broadcastCache = Collections.synchronizedSet(new TreeSet());

    // we'll just save a hashmap of all the login stuff in memory
    //private static HashMap s_loginData = null;

    // save a map to get from handle -> userID (stores as lowercase)
    private static Map s_handleToIDMap = new ConcurrentHashMap();

    /*
      private static boolean loadHandleToIDMap() {
      try {
      s_handleToIDMap = s_dbServices.getHandleToUserIDMap();
      s_teamNameMap = s_dbServices.getTeamNameToTeamIDMap();
      } catch (Exception e) {
      s_trace.error("Error loading handle->userID info from DB", e);
      }
      return s_handleToIDMap != null;
      }
    */

    /* Static initialization block for the topic stuff */
    //Static init stuff that initializaes the ejb stuff
    private static SimpleResourceBundle s_coreSettings;

    /**
     * Flag indicating if system tests must be enqueued when a submit is received for a non practice round.
     */
    private static boolean s_autoSystemTest;
    
    /**
     * Numbers of coders per room when a Long round is taking place
     */
    private static int CODERS_PER_LONG_ROUND_ROOM;
    
    static {
        info("Trying to initialize...");
        try {
            try {
                s_coreSettings = SimpleResourceBundle.getBundle("CoreServices");
            } catch (MissingResourceException mre) {
                s_trace.error("Failed to load CoreServices resources", mre);
            }
            s_trace.debug("In Try block...");
            initCache();
            s_dbServices = DBServicesLocator.getService();
            s_problemServices = ProblemServicesLocator.getService();
            s_trackingServices = null;
            CODERS_PER_LONG_ROUND_ROOM = s_coreSettings.getInt("codersPerLongRoundRoom", 50);
        } catch (Throwable e) {
            s_trace.fatal("Could not create EJBs for World!", e);
            e.printStackTrace();
        }
        info("Done initializing!");
    }

    /**
     * Initializes the lobbies, only done once
     **/
    public static final int LOBBY_ROUND_ID = 0;

    private static void initLobby() {
        try {
            info("Loading lobbies");
            s_cache.addRef(ContestRound.getCacheKey(LOBBY_ROUND_ID));
            CoreServices.getContestRound(LOBBY_ROUND_ID, false);
            synchronized (activeRounds) {
                addRoundRef(LOBBY_ROUND_ID);
            }
            info("lobbies loaded");

        } catch (Exception e) {
            s_trace.error("Exception", e);
        }

    }

    //    private static int s_spectatorRoomId = ContestConstants.ADMIN_LOBBY_ROOM_ID;
    //
    //    public static int getSpectatorRoomId() {
    //        return s_spectatorRoomId;
    //    }
    //
    //    public static void setSpectatorRoomId(int id) {
    //        info("New Spectator Room ID: " + id);
    //        s_spectatorRoomId = id;
    //    }

    /**
     * Releases a lock from the cache
     */
    public static final void releaseLock(String key) {
        try {
            s_cache.releaseLock(key);
        } catch (Exception e) {
            s_trace.error("Error releasing lock for: " + key, e);
        }
    }


    public static final void refreshRoundProblems(int roundID) {
        info("Refreshing Problems For round: " + roundID);
        try {
            // TODO: Worry about locks?  for now NO LOCKING on problems

            Round round = getContestRound(roundID, false);
            // this stuff deosn't change the Db anyways, reload the contest
            // if the problems got changed
            for (Iterator iterDiv = round.getDivisions().iterator(); iterDiv.hasNext(); ) {
                int division = ((Integer) iterDiv.next()).intValue();
                Iterator iter = round.getDivisionComponents(division).iterator();
                while (iter.hasNext()) {
                    int componentID = ((Integer) iter.next()).intValue();
                    loadComponentIntoCache(componentID);
                }
            }
            //dotNetController.reloadSolutions();
            info("Refresh problems complete.");
        } catch (Exception e) {
            s_trace.error("Exception refreshing contest round problems: " + roundID);
        }
    }

    private static void loadComponentIntoCache(int componentID) throws RemoteException, ProblemServicesException {
        SimpleComponent simpleComponent = s_problemServices.getSimpleComponent(componentID);
        Problem problem = s_problemServices.getProblem(simpleComponent.getProblemID());
        // save it back to the cache
        saveToCache(problem.getCacheKey(), problem);
        saveToCache(simpleComponent.getCacheKey(), simpleComponent);
        ProblemComponent[] components = problem.getProblemComponents();
        for (int i = 0; i < components.length; i++) {
            saveToCache(components[i].getCacheKey(), components[i]);
        }
    }

    /**
     * flush everything related to this round from the cache and reload it
     */
    public static final void refreshContestRound(int roundID) {
        info("Refreshing round: " + roundID);
        try {
            // get the lock
            getContestRound(roundID, true);
            Round round = getContestRoundFromDb(roundID);

            // save it back to the cache
            saveToCache(round.getCacheKey(), round);

            synchronized (activeRounds) {
                if (activeRounds.contains(new Long(roundID))) {
                    // Flush round room strong references
                    addRoundRef(roundID);
                }
            }
                
            getRegistration(roundID, true);
            Registration reg = s_dbServices.getRegistration(roundID);
            saveToCache(reg.getCacheKey(), reg);

            info("Refresh round complete.");
        } catch (Exception e) {
            s_trace.error("Exception refreshing contest round: " + roundID);
        } finally {
            releaseLock(ContestRound.getCacheKey(roundID));
            releaseLock(Registration.getCacheKey(roundID));
        }
    }


    private static String m_lobbyStatus = "Pick a practice room from the menu above to test your coding skills.";

    /**
     * Sets that status message for the lobby
     */
    public static void setLobbyStatus(String status) {
        if (s_trace.isDebugEnabled()) {
            s_trace.debug("setLobbyStatus = " + status);
        }        
        m_lobbyStatus = status;
    }


    public static long getCurrentDBTime() {
        try {
            //TODO how to do this with informix now?
            return System.currentTimeMillis();
            //return s_dbServices.getCurrentTimestamp().getTime();
        } catch (Exception e) {
        }
        return 0;
    }


    /**
     * Returns all the active contests in the database
     */
    public static Round[] getAllActiveRounds() {
        try {
            synchronized (activeRounds) {
                Round r[] = new Round[activeRounds.size()];
                int k = 0;
                for (Iterator rounds = activeRounds.iterator(); rounds.hasNext();) {
                    Long roundID = (Long) rounds.next();
                    r[k++] = getContestRound(roundID.intValue());
                }
                return r;
            }
        } catch (Exception e) {
            s_trace.error("Exception in getAllActiveContests: ", e);
            return null;
        }
    }

    /*
      public static LeaderBoard[] getAllActiveLeaderboards() {
      try {
      synchronized (activeRounds) {
      LeaderBoard r[] = new LeaderBoard[activeRounds.size()];
      int k = 0;
      for (Iterator rounds = activeRounds.iterator(); rounds.hasNext();) {
      Long roundID = (Long) rounds.next();
      r[k++] = getLeaderBoard(roundID.intValue(), false);
      }
      return r;
      }
      }
      catch (Exception e) {
      s_trace.error(e);
      return null;
      }
      }
    */


    /** SYHAAS 2002-05-13 created
     * called from Processor.chat()
     */
    public static ArrayList getAllowedSpeakers(int round_id) {
        String key = "AllSpeakers." + round_id;
        try {
            ArrayList speakers = (ArrayList) s_cache.get(key);
            if (speakers == null) {
                speakers = s_dbServices.getAllowedSpeakers(round_id);
                saveToCache(key, speakers);
            }

            return speakers;
        } catch (Exception e) {
            s_trace.error("Exception in getAllowedSpeakers(): ", e);
        }
        return null;
    }

    /** SYHAAS 2002-05-13 created6
     * called from Processor.chat() and ResponseProcessor.createActiveContestRoomLists()
     */
    public static ArrayList getActiveModeratedChatSessions() {
        try {
            ArrayList modchats = (ArrayList) s_cache.get("ModChats");
            if (modchats == null) {
                modchats = s_dbServices.getAllActiveModeratedChatSessions();
                saveToCache("ModChats", modchats);
            }

            /*
              if (modchats != null && modchats.size() > 0) {

              //ContestRound curChat = (ContestRound) modchats.get(0);
              // TODO what is this?
              //loadAsActiveContest(curChat.getRoundID());
              }
            */

            return modchats;
        } catch (Exception e) {
            s_trace.error("Exception in getActiveModeratedChatSessions(): ", e);
        }
        return null;
    }

    public static void updatePlace(long roundID) {
        Round contestRound = getContestRound((int) roundID, true);
        try {
            if (!contestRound.isLongContestRound()) {
                s_dbServices.updateAlgoPlace(contestRound.getRoundID());
            }
        } catch (Exception e) {
            s_trace.error("Error ending contest", e);
            throw new RuntimeException(e);
        } finally {
            releaseLock(contestRound.getCacheKey());
        }
    }

    /**
     * Takes care of ending a contest, sending out the broadcasts, updating the DB, etc.
     */
    public static void endContest(long roundID) {
        Round contestRound = getContestRound((int) roundID, true);
        try {
            if (!contestRound.isLongContestRound()) {
                CoreServices.clearTestCases();
                TestServicesLocator.getService().updateComponentStateAndPointsFromSystemTestResults((int) roundID);
            }
            s_dbServices.endContest(contestRound.getContestID(), contestRound.getRoundID());
            // set status to past
            //contestRound.setStatus("P");
            //            contest.setActive(false);

            saveToCache(contestRound.getCacheKey(), contestRound);

            ActionEvent event = new ActionEvent(TCEvent.ALL_TARGET, contestRound.getContestID(), ActionEvent.END_CONTEST);
            event.setRoundID(contestRound.getRoundID());
            EventService.sendGlobalEvent(event);
        } catch (Exception e) {
            s_trace.error("Error ending contest", e);
            throw new RuntimeException(e);
        } finally {
            releaseLock(contestRound.getCacheKey());
        }
    }

    /**
     * Takes care of ending a high school contest, sending out the broadcasts, updating the DB, etc.
     */
    public static void endHSContest(long roundID) {
        Round contestRound = getContestRound((int) roundID, true);
        try {
            CoreServices.clearTestCases();
            TestServicesLocator.getService().updateComponentStateAndPointsFromSystemTestResults((int) roundID);
            s_dbServices.endTCHSContest(contestRound.getContestID(), contestRound.getRoundID());

            saveToCache(contestRound.getCacheKey(), contestRound);

        } catch (Exception e) {
            s_trace.error("Error ending contest", e);
            throw new RuntimeException(e);
        } finally {
            releaseLock(contestRound.getCacheKey());
        }
    }

    public static boolean isHighSchoolRound(long roundID) {
        try {
            return s_dbServices.isHighSchoolRound(roundID);
        } catch (Exception e) {
            s_trace.error("Error determining if round is high school round. ", e);
            return false;
        }
    }


    private static ArrayList s_guestIDs = new ArrayList(100);

    public static User guestLogin() {
        final String KEY = "Guest.ID";
        try {
            Integer guestID = null;
            synchronized (s_guestIDs) {
                if (!s_guestIDs.isEmpty()) {
                    guestID = (Integer) s_guestIDs.remove(s_guestIDs.size() - 1); // use the last id for the guest id
                }
            }
            if (guestID == null) {
                guestID = (Integer) s_cache.getAndLock(KEY);
                if (guestID == null) {
                    s_cache.addRef(KEY);
                    guestID = new Integer(201);
                }
                s_cache.set(KEY, new Integer(guestID.intValue() + 1));
            }
            User user = new User(guestID.intValue(), "guest" + guestID.intValue());
            user.setGuest(true);
            saveToCache(user.getCacheKey(), user);
            handleLogin(user);
            // TODO: Possibly clear guest practice room info.
            return user;
        } catch (Exception e) {
            s_trace.error("Error in guestlogin", e);
        } finally {
            releaseLock(KEY);
        }
        return null;
    }


    private static Integer s_nextSpectatorID = new Integer(-999999);

    public static User spectatorLogin(String userName, String password) throws InvalidPasswordException, HandleTakenException {
        User u = authenticateUser(userName, password, null);
        if (!u.isLevelOneAdmin() && !u.isLevelTwoAdmin()) {
            s_trace.error("Non-admin '" + userName + "' attempted to login as spectator.");
            throw new InvalidPasswordException();
        }
        try {
            int id;
            synchronized (s_nextSpectatorID) {
                id = s_nextSpectatorID.intValue();
                s_nextSpectatorID = new Integer(id + 1);
            }
            User user = new User(id, "Spectator " + id);
            user.setSpectator(true);
            saveToCache(user.getCacheKey(), user);
            /*                  if ( user != null ) {
                                handleLogin( user );
                                // TODO: Possibly clear guest practice room info.
                                }*/
            s_cache.addRef(user.getCacheKey());
            return user;
        } catch (Exception e) {
            s_trace.error("Error in spectatorLogin", e);
        } finally {
        }
        return null;
    }

    public static User forwarderLogin(String userName, String password) throws InvalidPasswordException, HandleTakenException {
        User u = authenticateUser(userName, password, null);
        if (!u.isLevelOneAdmin() && !u.isLevelTwoAdmin()) {
            s_trace.error("Non-admin '" + userName + "' attempted to login as forwarder.");
            throw new InvalidPasswordException();
        }
        try {
            int id;
            synchronized (s_nextSpectatorID) {
                id = s_nextSpectatorID.intValue();
                s_nextSpectatorID = new Integer(id + 1);
            }
            User user = new User(id, "Forwarder " + id);
            user.setForwarder(true);
            saveToCache(user.getCacheKey(), user);
            s_cache.addRef(user.getCacheKey());
            return user;
        } catch (Exception e) {
            s_trace.error("Error in forwarderLogin", e);
        } finally {
        }
        return null;
    }


    private static final Object s_loginLock = new Object();

    /**
     * Called on a login, takes care of cleaning up the logged in data
     */
    private static void handleLogin(User user) {
        try {
            synchronized (s_loginLock) {
                s_trace.debug("Start synch");
                HashSet loggedInSet = getLoginSet(true);
                loggedInSet.add(new Integer(user.getID()));
                saveToCache(LOGGEDIN_USERIDS_KEY, loggedInSet);

                ArrayList userNames = (ArrayList) safeCacheGet(LOGGEDIN_USERNAMES_KEY, true);
                if (userNames == null) userNames = new ArrayList();
                userNames.add(user.getName());
                saveToCache(LOGGEDIN_USERNAMES_KEY, userNames);

                ArrayList userRatings = (ArrayList) safeCacheGet(LOGGEDIN_USERRATINGS_KEY, true);
                if (userRatings == null) userRatings = new ArrayList();
                userRatings.add(new Integer(user.getRating(Rating.ALGO).getRating()));
                saveToCache(LOGGEDIN_USERRATINGS_KEY, userRatings);

                s_cache.addRef(user.getCacheKey());
                s_trace.debug("End synch");
            }
        } catch (Exception e) {
            s_trace.error("Error updating logged in user object", e);
        } finally {
            releaseLock(LOGGEDIN_USERIDS_KEY);
            releaseLock(LOGGEDIN_USERRATINGS_KEY);
            releaseLock(LOGGEDIN_USERNAMES_KEY);
        }
    }


    /**
     * Called on a logout, takes care of cleaning up the logged in data
     */
    private static void handleLogout(User user) {
        try {
            synchronized (s_loginLock) {
                HashSet loggedInSet = getLoginSet(true);
                if (loggedInSet.remove(new Integer(user.getID()))) saveToCache(LOGGEDIN_USERIDS_KEY, loggedInSet);

                ArrayList userNames = (ArrayList) safeCacheGet(LOGGEDIN_USERNAMES_KEY, true);
                int ind = userNames.indexOf(user.getName());
                userNames.remove(user.getName());
                saveToCache(LOGGEDIN_USERNAMES_KEY, userNames);

                ArrayList userRatings = (ArrayList) safeCacheGet(LOGGEDIN_USERRATINGS_KEY, true);
                if (ind != -1) userRatings.remove(ind);
                saveToCache(LOGGEDIN_USERRATINGS_KEY, userRatings);

                s_cache.removeRef(user.getCacheKey());
            }

            if (user.isGuest()) {
                synchronized (s_guestIDs) {
                    s_guestIDs.add(new Integer(user.getID()));
                }
            }
        } catch (Exception e) {
            s_trace.error("Error updating logged in user object", e);
        } finally {
            releaseLock(LOGGEDIN_USERIDS_KEY);
            releaseLock(LOGGEDIN_USERRATINGS_KEY);
            releaseLock(LOGGEDIN_USERNAMES_KEY);
        }
    }

    /*
      public static boolean isPasswordValid(String username, String password) {
      try {
      return authenticateUser(username, password, null) != null;
      }
      catch (Exception e) {
      return false;
      }
      }
    */

    /**
     * Get the authenticated user, if the user was not in the cache, it
     * stores it
     */
    public static User getAuthenticatedUser(String username, String password, String handle) throws InvalidPasswordException, HandleTakenException {
        User user = authenticateUser(username, password, handle);
        //TODO Ugly hack, the whole process should be changed
        //Authentication and user retrieval should be totally separate, as should checking the handle
        if (s_cache.get(user.getCacheKey()) == null) {
            //If we don't have the user in the cache we store it for future use
            saveToCache(user.getCacheKey(), user);
        }
        //s_handleToIDMap.put(u.getName().toLowerCase(), new Integer(u.getID()));
        s_handleToIDMap.put(user.getName().toLowerCase(), new Integer(user.getID()));
        if (s_trace.isDebugEnabled()) {
            s_trace.debug(user.toString());
        }            
        return user;
    }

    /**
     * Get the authenticated user, if the user was not in the cache, it
     * stores it
     */
    public static User getAuthenticatedUser(String sso) throws InvalidSSOException, HandleTakenException {
        User user = authenticateUser(sso);
        if (s_cache.get(user.getCacheKey()) == null) {
            saveToCache(user.getCacheKey(), user);
        }
        s_handleToIDMap.put(user.getName().toLowerCase(), new Integer(user.getID()));
        if (s_trace.isDebugEnabled()) {
            s_trace.debug(user.toString());
        }
        return user;
    }

    //private static Authenticator defaultAuthenticator = new TCAuthenticator();

    private static User authenticateUser(String username, String password, String newHandle)
        throws InvalidPasswordException, HandleTakenException {
        User user = null;
        try {
            //s_trace.debug("Authenticate. handle:" + username + " pass:" + password + " newHandle:"+newHandle);
            user = auth.authenticateUser(s_dbServices, username, password, newHandle);
        } catch (InvalidPasswordException e) {
            //if it fails, check to see if it is an admin!
            //u = defaultAuthenticator.authenticateUser(s_dbServices, username, password, null);
            //System.out.println(u);
            //if (user != null && (user.isLevelTwoAdmin() || user.isLevelOneAdmin())) return user;
            throw e;
        }
        return user;
    }

    /**
     * Authenticate user using the sso value.
     *
     * @param sso - the SSO value.
     * @returns authenticated user.
     *
     * @throws InvalidSSOException if sso is invalid.
     * @throws HandleTakenException if error occurs when taking handle.
     */
    private static User authenticateUser(String sso)
        throws InvalidSSOException, HandleTakenException {
        try {
            String[] fields = s_dbServices.validateSSOToken(sso);

            if (fields == null || fields.length != 2) {
                throw new InvalidSSOException("The sso token is invalid.");
            }
            String handle = fields[0];
            String password = fields[1];

            // use handle as username here
            return auth.authenticateUser(s_dbServices, handle, password, handle);
        } catch (InvalidSSOException e) {
            s_trace.error("Fail to authenticate by sso.", e);
            throw e;
        } catch (DBServicesException e) {
            s_trace.error("Fail to authenticate by sso.", e);
            throw new InvalidSSOException("The sso token is invalid.", e);
        } catch (InvalidPasswordException e) {
            s_trace.error("Fail to authenticate by sso.", e);
            throw new InvalidSSOException("The sso token is invalid.", e);
        } catch (RemoteException e) {
            s_trace.error("Fail to authenticate by sso.", e);
            throw new InvalidSSOException("There are errors to get remote bean.", e);
        }
    }

    /**
     * Validates user/pass and returns true for success, false otherwise
     */
    public static User login(String userName, String pass, String newHandle) throws InvalidPasswordException, HandleTakenException {
        if (s_trace.isDebugEnabled()) {
            s_trace.debug("Login attempt. handle:" + userName + " pass:" + pass + " newHandle:"+newHandle);
        }
        try {
            User user = authenticateUser(userName, pass, newHandle);
            if (s_trace.isDebugEnabled()) {
                s_trace.debug("User is: " + user);
            }            
            if (user != null) {
                doLogin(user);
            }
            return user;
        } catch (InvalidPasswordException e) {
            //s_trace.error("Errorloging in", e);
            throw e;

        } catch (HandleTakenException e) {
            throw e;
        } catch (Exception e) {
            s_trace.error("Error logging in", e);
        }
        return null;
    }
    
    
    
   /**
    * Do the actual login of the user
    */
   public static void doLogin(User authenticatedUser) {
       if (s_trace.isDebugEnabled()) {
           s_trace.debug("Loging in user: handle:" + authenticatedUser.getName());
       }
       handleLogin(authenticatedUser);
       boolean release = true;
       try {
           safeCacheGet(authenticatedUser.getCacheKey(), true);
           saveToCache(authenticatedUser.getCacheKey(), authenticatedUser);
           release = false;
           s_trace.debug("Saved user to cache");
       } finally {
           if (release) {
               releaseLock(authenticatedUser.getCacheKey());
           }
       }
   }


    private static boolean removeUserFromTeam(User user, int teamID) {
        Team team = null;
        try {
            team = getTeam(teamID, true);
            if (team == null) {
                s_trace.error("Team null in removeUserFromTeam: " + user + "," + teamID);
                return false;
            }

            team.removeMember(user);
            s_dbServices.commitTeam(team);
            return true;
        } catch (Exception e) {
            s_trace.error(e);
        } finally {
            if (team != null) {
                saveToCache(team.getCacheKey(), team);
            }
        }
        return false;
    }

    public static boolean removeUserFromTeam(String userName, int teamID) {
        User user = null;
        try {
            user = getUser(userName);
            user = getUser(user.getID(), true);
            return removeUserFromTeam(user, teamID);
        } finally {
            if (user != null) {
                saveToCache(user.getCacheKey(), user);
            }
        }
    }

    public static boolean removeUserFromTeam(int userID, int teamID) {
        User user = null;
        try {
            user = getUser(userID, true);
            return removeUserFromTeam(user, teamID);
        } finally {
            if (user != null) {
                saveToCache(user.getCacheKey(), user);
            }
        }
    }

    public static boolean addUserToTeam(String userName, int teamID) {
        User user = null;
        Team team = null;
        try {
            user = getUser(userName);
            user = getUser(user.getID(), true);
            team = getTeam(teamID, true);
            if (team == null) {
                s_trace.error("Team null in addUserToTeam: " + userName + "," + teamID);
                return false;
            }

            team.addMember(user);
            s_dbServices.commitTeam(team);
            return true;
        } catch (Exception e) {
            s_trace.error(e);
        } finally {
            if (user != null) {
                saveToCache(user.getCacheKey(), user);
            }
            if (team != null) {
                saveToCache(team.getCacheKey(), team);
            }
        }
        return false;
    }

    public static boolean addInterestedToTeam(int userID, String teamName) {
        User user = null;
        Team team = null;
        try {
            user = getUser(userID, true);
            team = getTeam(teamName, true);
            if (team == null) {
                s_trace.error("Team null in addInterestedToTeam: " + userID + "," + teamName);
                return false;
            }

            team.addInterestedCoder(user);
            s_dbServices.commitTeam(team);
            return true;
        } catch (Exception e) {
            s_trace.error(e);
        } finally {
            if (user != null) {
                saveToCache(user.getCacheKey(), user);
            }
            if (team != null) {
                saveToCache(team.getCacheKey(), team);
            }
        }
        return false;
    }

    public static boolean removeInterestedFromTeam(int userID, String teamName) {
        User user = null;
        Team team = null;
        try {
            user = getUser(userID, true);
            team = getTeam(teamName, true);
            if (team == null) {
                s_trace.error("Team null in removeUserFromTeam: " + userID + "," + teamName);
                return false;
            }

            team.removeInterestedCoder(user);
            s_dbServices.commitTeam(team);
            return true;
        } catch (Exception e) {
            s_trace.error(e);
        } finally {
            if (user != null) {
                saveToCache(user.getCacheKey(), user);
            }
            if (team != null) {
                saveToCache(team.getCacheKey(), team);
            }
        }
        return false;
    }

    public static void saveComponentAssignmentData(ComponentAssignmentData data) {
        try {
            s_dbServices.saveComponentAssignmentData(data);
        } catch (Exception e) {
            s_trace.error(e);
        }
    }

    public static void dbSynchTeamMembersComponents(int contestId, int roundId, int roomId, int componentId, int oldUser, int newUser) {
        try {
            s_dbServices.synchTeamMembersComponents(contestId, roundId, roomId, componentId, oldUser, newUser);
        } catch (Exception e) {
            s_trace.error("Error calling bean to sync team members' components.", e);
        }
    }

    /**
     * Sends a test request using the TestService
     */
    public static boolean submitUserTest(UserTestAttributes userTest) {
        try {
            SRMTestScheduler.submitUserTest(userTest);
            return true;
        } catch (Exception e) {
            s_trace.error("Failed to submit user test", e);
        }
        return false;
    }

    /**
     * Sends a challenge request using the TestService
     */
    public static boolean submitChallengeTest(ChallengeAttributes chal) {
        try {
            SRMTestScheduler.submitChallengeTest(chal);
            return true;
        } catch (Exception e) {
            s_trace.error("Failed to submit challenge", e);
        }
        return false;
    }

    /*
      public static int getSecondsLeftInPhase( ContestRound contest ) {
      try {
      long currentTime = System.currentTimeMillis();
      if (contest.isModeratedChat()) {
      //it's a moderated chat
      if (currentTime < contest.getModeratedChatStart().getTime()) {
      return (int) ((contest.getModeratedChatStart().getTime() - currentTime) / 1000);
      }
      else if (currentTime < contest.getModeratedChatEnd().getTime()) {
      return (int) ((contest.getModeratedChatEnd().getTime() - currentTime) / 1000);
      }
      else
      return 0;
      }
      else {
      //it's a contest
      if (currentTime < contest.getCodingStart().getTime()) {
      return (int) ((contest.getCodingStart().getTime() - currentTime) / 1000);
      }
      else if (currentTime < contest.getCodingEnd().getTime()) {    // It's in the coding phase
      return (int) ((contest.getCodingEnd().getTime() - currentTime) / 1000);
      }
      else if (contest.getIntermissionEnd() != null && currentTime < contest.getIntermissionEnd().getTime()) { // It's intermission
      return (int) ((contest.getIntermissionEnd().getTime() - currentTime) / 1000);
      }
      else if (contest.getChallengeEnd() != null && currentTime < contest.getChallengeEnd().getTime()) {    // It's in the challenge phase
      return (int) ((contest.getChallengeEnd().getTime() - currentTime) / 1000);
      }
      else {
      return 0;
      }
      }
      }
      catch (Exception e) {
      // most likely a practice contest
      return 0;
      }
      }
    */



    private static final int GARBAGE = Integer.MIN_VALUE;

    /**
     * Logs out a given user
     */
    public static void logout(int userID) {
        int roomID = GARBAGE;
        try {

            User user = getUser(userID, true);
            try {
                if (user != null) {
                    roomID = user.getRoomID();
                    user.setRoomID(ContestConstants.INVALID_ROOM);
                    user.setRoomType(ContestConstants.INVALID_ROOM);
                } else {
                    s_trace.error("User " + userID + " null on logout.");
                    return;
                }
            } finally {
                saveToCache(User.getCacheKey(userID), user);
            }

            if (roomID != ContestConstants.INVALID_ROOM) {
                Room room = getRoom(roomID, false);
                if (room != null) {
                    room.leave(user);
                    removeRoomRef(room);
                }
            }

            // remove all watched rooms, since we can only spectate rooms of active rounds, no need to add/remove ref.
            for (Iterator iter = user.getWatchedDivSummaryRooms(); iter.hasNext(); ) {
                Integer roomId = (Integer) iter.next();
                BaseCodingRoom room = (BaseCodingRoom) getRoom(roomId.intValue());
                room.removeSpectator(user.getID());
                user.removeWatchedDivSummaryRoom(roomId.intValue());
            }
            for (Iterator iter = user.getWatchedRooms(); iter.hasNext(); ) {
                Integer roomId = (Integer) iter.next();
                BaseCodingRoom room = (BaseCodingRoom) getRoom(roomId.intValue());
                room.removeSpectator(user.getID());
                user.removeWatchedRoom(roomId.intValue());
            }
            handleLogout(user);
        } catch (Exception e) {
            s_trace.error("Error on logout", e);
            e.printStackTrace();
        }
    }

    public static void addRoomRef(Room room) {
        synchronized (room) {
            if (room.getOccupancy() == 0) {
                s_cache.addRef(room.getCacheKey());
            }
        }
    }

    public static void removeRoomRef(Room room) {
        synchronized (room) {
            if (room.getOccupancy() == 0) {
                /*
                boolean inactive = true;
                Integer id = new Integer(room.getRoomID());
                synchronized (activeRoundRooms) {
                    for (Iterator iter = activeRoundRooms.values().iterator(); iter.hasNext(); ) {
                        if (((Collection) iter.next()).contains(id)) {
                            inactive = false;
                            break;
                        }
                    }
                }
                if (inactive) {
                    s_cache.removeRef(room.getCacheKey());
                }
                */
                s_cache.removeRef(room.getCacheKey());
            }
        }
    }
    /**
     * <p>
     * remove the simple component entity from cache.
     * </p>
     * @param componentID
     *         the specific component id.
     */
    public static void removeSimpleComponentFromCache(int componentID) {
    	String key = SimpleComponent.getCacheKey(componentID);
    	if(key!=null) {
	    	synchronized (key) {
	    		s_cache.removeRef(key);
	    		s_cache.set(key, null);
	    	}
    	}
    }
    /**
     * Helper which takes care of exception handling for retreiving objects from the cache
     */
    private static Object safeCacheGet(String key, boolean lock) {
        try {
            if (lock) {
                return s_cache.getAndLock(key);
            } else {
                return s_cache.get(key);
            }
        } catch (Exception e) {
            s_trace.error("Failed cache get for Key:" + key, e);
        }
        return null;
    }

    /**
     * Gets a leaderboard for the given contest
     */
    /*
      public static LeaderBoard getLeaderBoard(int cID, int rID) {
      return getLeaderBoard(cID, rID, false);
      }
    */

    /**
     * Gets a leaderboard for the given contest
     */
    public static LeaderBoard getLeaderBoard(int rID, boolean lock) {
        String key = LeaderBoard.getCacheKey(rID);
        LeaderBoard lb = (LeaderBoard) safeCacheGet(key, lock);
        // this means the contest object isn't loaded, assume that the contest will always
        // load the leader board into the cache on creation, also we will not kick out
        // our leaderboards from the cache.
        if (lb == null) {
            // guess just stick an empty on in there for now
            s_trace.debug("Got a null leaderboard. Creating a new one and intializing");
            lb = new LeaderBoard(rID);
            Round round = getContestRound(rID, false);
            if (round != null) {
                lb.initialize(round);
            }
            saveToCache(lb.getCacheKey(), lb);
            if (lock) {
                safeCacheGet(lb.getCacheKey(), true);
            }
        }
        return lb;
    }

    /**
     * Reloads a room from the db
     */
    public static Room refreshRoom(int roomID) {
        BaseCodingRoom room = (BaseCodingRoom) getDBRoom(roomID);

        // Copy over users/spectators to room.
        BaseCodingRoom oldRoom = (BaseCodingRoom) getRoom(roomID, true);
        synchronized (oldRoom) {
            try {
                ArrayList userNames = oldRoom.getUserNames();
                for (int i = 0; i < userNames.size(); i++) {
                    String userName = (String) userNames.get(i);
                    User user = getUser(userName);
                    if (user != null) {
                        addRoomRef(room);
                        room.enter(user);
                    } else {
                        s_trace.error("Got a null user for name: " + userName + " on room: " + roomID);
                    }
                }
                for (Iterator i = oldRoom.getAllSpectators(); i.hasNext();) {
                    int spectatorID = ((Integer) i.next()).intValue();
                    room.addSpectator(spectatorID);
                }
            } catch (Exception e) {
                s_trace.error("Failed to refreshRoom: " + roomID, e);
            } finally {
                saveToCache(room.getCacheKey(), room);
            }
        }
        return room;
    }

    /**
     * Reloads all rooms of the given round from the db
     */
    public static void refreshAllRooms(int roundID) {
    	Round round = getContestRound(roundID);
    	Iterator roundIterator = round.getAllRoomIDs();
    	while (roundIterator.hasNext()) {
    		refreshRoom(((Integer)roundIterator.next()).intValue());
    	}
    }

    public static void roundForwarding(String host, int port, boolean enable, String user, String password) {
        if(enable) {
            //add thread
            RequestProcessor.addRoundForwarder(host, port, user, password);
        } else {
            //remove thread
            RequestProcessor.removeRoundForwarder(host, port);
        }
    }

    public static void showSpecResults() {
        RequestProcessor.showSpecResults();
    }

    /**
     * Gets a room from the dB
     */
    static Room getDBRoom(int roomID) {
        try {
            Room room = s_dbServices.getRoom(roomID);
            if (room != null && room instanceof BaseCodingRoom) {
                try {
                    //We need to update the leader since the only the listener has the real state 
                    //of the round.
                    ((BaseCodingRoom) room).updateLeader();
                } catch (Exception e) {
                    s_trace.error("Exception updating leader in getDBRoom(" + roomID + ")", e);
                }
            }
            return room;
        } catch (Exception e) {
            s_trace.error("Exception in getDBRoom(" + roomID + ")", e);
        }
        return null;
    }

    /*
      public static String getCoderProblemStringFromCache(int roomID, int coderID, int componentIndex) {
      //TODO fix this to use componentID
      CoderStringPair pair = getCoderFromCache(roomID, coderID);
      Coder coder = pair.getCoder();
      if (coder == null) {
      return pair.getMessage();
      }
      int numProblems = coder.getNumComponents();
      if (componentIndex < 0 || componentIndex >= numProblems) {
      return "componentIndex is out of bounds, problemIndex=" + componentIndex + ", numProblems=" + numProblems;
      }
      CoderComponent coderComponent = coder.getComponent(componentIndex);
      if (coderComponent == null) {
      return "coderComponent is null";
      }
      return coderComponent.toString();
      }

      private static class CoderStringPair {
      private final Coder coder;
      private final String message;

      private CoderStringPair(Coder coder, String message) {
      this.coder = coder;
      this.message = message;
      }

      private Coder getCoder() {
      return coder;
      }

      private String getMessage() {
      return message;
      }
      }
    */

    private static String getCoderMessageFromCache(int roomID, int coderID) {
        Room room = getRoomFromCache(roomID);
        String message;
        if (room == null) {
            message = "room not in cache";
        } else if (!(room instanceof BaseCodingRoom)) {
            message = "not ContestRoom";
        } else {
            BaseCodingRoom contestRoom = (BaseCodingRoom) room;
            if (contestRoom.isUserAssigned(coderID)) {
                Coder coder = contestRoom.getCoder(coderID);
                message = coder.toString();
            } else {
                message = "no such coder in the room";
            }
        }
        return message;
    }

    public static void clearPracticeRooms(int type) {
        try {
            int limit = Processor.getPracticeRoundLimit();
            Round practiceRounds[] = loadPracticeRounds(limit);

            for(int i = 0; i < practiceRounds.length; i++) {
                //call dbservices to clear and move data
                int roomID = ((Integer)practiceRounds[i].getAllRoomIDsList().get(0)).intValue();
                s_trace.info("Clearing Room " + roomID);

                s_dbServices.backupPracticeRoom(practiceRounds[i].getRoundID());

                //loop through everyone in the room, delete everyone not in the room
                Room rm = getRoomFromCache(roomID, false);
                if(rm == null) {
                    //delete everyone
                    s_dbServices.clearPracticeRoom(practiceRounds[i].getRoundID(), type);
                } else {

                    BaseCodingRoom r = (BaseCodingRoom)rm;
                    ArrayList names = r.getUserNames();

                    for(Iterator it = r.getAllCoders(); it.hasNext();) {
                        Coder c = (Coder)it.next();

                        if(!names.contains(c.getName()) && s_dbServices.isDeleteCoderFromPracticeRoom(practiceRounds[i].getRoundID(), c.getID(), type)) {
                            //remove user
                            s_dbServices.deleteCoderFromPracticeRoom(practiceRounds[i].getRoundID(), c.getID());
                        }
                    }

                    refreshRoom(roomID);
                }
            }
        } catch (Exception e) {
            s_trace.error("Exception in clearPracticeRooms", e);
            throw new RuntimeException(e);
        }
    }

    public static String getCoderStringFromCache(int roomID, int coderID) {
        return getCoderMessageFromCache(roomID, coderID);
    }

    public static Room getRoomFromCache(int roomID) {
        return getRoomFromCache(roomID, false);
    }

    private static Room getRoomFromCache(int roomID, boolean lock) {
        String roomKey = Room.getCacheKey(roomID);
        Room room = (Room) safeCacheGet(roomKey, lock);

        return room;
    }

    public static Room getRoom(int roomId) {
        return getRoom(roomId, false);
    }

    /**
     * Gets a room asynchronizely via a thread if the room is not in cache.
     * @return true if the room is in cache and the listener is called immediately;
     *         false if the room needs to be loaded from DB, and the listener is called later.
     */
    public static boolean getRoomAsync(int roomId, RoomLoadedListener listener) {
        return roomLoader.loadRoom(roomId, listener);
    }

    /**
     * Gets a room
     */
    public static Room getRoom(int roomID, boolean lock) {
        try {
            Room room = getRoomFromCache(roomID, lock);
            if (room == null) {
                room = getDBRoom(roomID);
                if (room != null) {
                    saveToCache(room.getCacheKey(), room);
                    if (lock) {
                        safeCacheGet(room.getCacheKey(), true);
                    }
                }
            }
            return room;
        } catch (Exception e) {
            s_trace.error("Exception in getRoom(" + roomID + "," + lock + ")", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets a room
     */
    public static BaseCodingRoom getContestRoom(int roomID, boolean lock) {
        Room room = getRoom(roomID, lock);
        if (room instanceof BaseCodingRoom) {
            return (BaseCodingRoom) room;
        } else {
            throw new IllegalArgumentException("Room ID " + roomID + " does not identify a contest room.  room = " + room);
        }
    }

    public static BaseCodingRoom getContestRoomByKey(String key, boolean lock) {
        BaseCodingRoom cr = (BaseCodingRoom) safeCacheGet(key, lock);
        return cr;
    }

    private static HashMap s_teamNameMap = new HashMap();

    public static int getTeamIDForName(String name) {
        Integer i = (Integer) s_teamNameMap.get(name.toLowerCase());
        if (i == null) {
            return -1;
        }
        return i.intValue();
    }

    public static Team getTeam(String name) {
        return getTeam(name, false);
    }


    public static int getCoderSchool(int coderID) {
        int schoolID = 0;
        try {
            schoolID = s_dbServices.getCoderSchoolID(coderID);
        } catch (Exception e) {
            s_trace.error("Error getting School for this user" + coderID, e);
        }
        return schoolID;
    }

    public static boolean doesUserHaveCoach(int coderID) {
        boolean m_doesUserHaveCoach = false;
        int schoolID = 0;
        try {
            schoolID = getCoderSchool(coderID);
            if (s_dbServices.getCoachIDFromSchoolID(schoolID) > 0) {
                m_doesUserHaveCoach = true;
            }
        } catch (Exception e) {
            s_trace.error("Error getting Coach for this user" + coderID, e);
        }
        return m_doesUserHaveCoach;
    }

    public static boolean isCoach(int coderID) {
        boolean m_isCoach = false;
        try {

            if (s_dbServices.getSchoolIDFromCoach(coderID) > 0) {
                m_isCoach = true;
            }
        } catch (Exception e) {
            s_trace.error("Error getting Type School for this user" + coderID, e);
        }
        return m_isCoach;
    }

    public static Team getTeam(String name, boolean lock) {
        int id = getTeamIDForName(name);
        if (id == -1) {
            try {
                s_teamNameMap = s_dbServices.getTeamNameToTeamIDMap();
            } catch (Exception e) {
            }
            id = getTeamIDForName(name);
            if (id == -1) {
                s_trace.error("No team found: " + name);
                return null;
            }
        }
        return getTeam(id, lock);
    }

    /**
     * Gets a team
     */
    public static Team getTeam(int teamID, boolean lock) {
        if (teamID == User.NO_TEAM) {
            s_trace.debug("Asked for a No team");
            return null;
        }
        try {
            String key = Team.getCacheKey(teamID);
            Team team = (Team) safeCacheGet(key, lock);
            if (team == null) {
                if (s_trace.isDebugEnabled()) {
                    s_trace.debug("Failed to find team in cache: " + teamID);
                }                
                // TODO(HAO): Uncomment
                team = s_dbServices.getTeam(teamID);

                // gotta save it to the cache if got here
                if (team != null) {
                    saveToCache(Team.getCacheKey(teamID), team);
                    s_teamNameMap.put(team.getName(), new Integer(teamID));
                    s_trace.debug("saved team to cache");
                    if (lock) {
                        safeCacheGet(Team.getCacheKey(teamID), true);
                        s_trace.debug("locked team");
                    }
                }
            }
            return team;
        } catch (Exception e) {
            s_trace.error("Exception in getUser(" + teamID + "," + lock + ")", e);
        }
        return null;
    }

    public static User getUser(int userId) {
        return getUser(userId, false);
    }
    /**
     * <p>
     * always get user from db.
     * </p>
     * @param userID
     *        the user id.
     * @return the user entity from db.
     */
    public static User getUserFromDB(int userID) {
        s_trace.info("getUserFromDB(" + userID + ")");
        try {
            return s_dbServices.getUser(userID);
        } catch (Exception e) {
            s_trace.error(e);
            throw new RuntimeException(e);
        }        
    }
    /**
     * Creates a team with just 1 member, the specified user, adds the team
     * to the DB, and then saves the team info.
     */
    public static synchronized void createPracticeTeamForUser(int userID) {
        s_trace.info("createPracticeTeamForUser(" + userID + ")");
        try {
            User user = getUser(userID, true);
            if (user.isOnTeam()) {
                throw new IllegalStateException("User already on team.");
            }

            Team team = s_dbServices.createTeam(user.getName() + "'s Practice Team", userID, TeamConstants.PRACTICE_TEAM);
            user.setCaptain(true);
            user.setTeamID(team.getID());
            user.setTeamName(team.getName());
            saveToCache(User.getCacheKey(userID), user);
            s_teamNameMap = s_dbServices.getTeamNameToTeamIDMap();
            if (s_trace.isDebugEnabled()) {
                s_trace.debug("Team created for user: " + team);
            }            
        } catch (Exception e) {
            s_trace.error("Error creating practice team for user: ", e);
        }
    }

    /**
     * Gets a user
     */
    public static User getUser(int userID, boolean lock) {
        try {
            String userKey = User.getCacheKey(userID);
            if (s_trace.isDebugEnabled()) {
                s_trace.debug("USER KEY IS:" + userKey);
            }            
            User user = (User) safeCacheGet(userKey, lock);
            //s_trace.debug("HANDLE ===== "+ user.getName()+" "+user.getID());
            if (user == null) {
                if (s_trace.isDebugEnabled()) {
                    s_trace.debug("Failed to find user in cache: " + userID);
                }                
                user = s_dbServices.getUser(userID);

                // gotta save it to the cache if got here
                if (user != null) {
                    saveToCache(User.getCacheKey(userID), user);
                    // Put it in the handle-ID map
                    s_handleToIDMap.put(user.getName().toLowerCase(), new Integer(user.getID()));
                    s_trace.debug("saved user to cache");
                    if (lock) {
                        safeCacheGet(User.getCacheKey(userID), true);
                        s_trace.debug("locked user");
                    }
                } else {
                    throw new IllegalArgumentException("Bad user id: " + userID);
                }
            }
            return user;
        } catch (Exception e) {
            s_trace.error(e);
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Takes care of updating the runtime objects for when a user enters a room
     */
    public static void enter(int userID, int roomID) {
        s_trace.debug("Enter");
        try {
            Room room = getRoom(roomID, false);
            if (room == null) {
                s_trace.error("enter got null room for id: " + roomID);
                return;
            }
            User user = getUser(userID, false);
            if (user == null) {
                s_trace.error("enter got null user for id: " + userID);
                return;
            }

            // update the old room
            int oldRoomID = user.getRoomID();

            // moving to the room he's already in don't do anything
            if (oldRoomID == roomID) {
                return;
            }

            if (oldRoomID != ContestConstants.INVALID_ROOM) {
                Room oldRoom = getRoom(oldRoomID, false);
                if (oldRoom != null) {
                    oldRoom.leave(user);
                    removeRoomRef(oldRoom);
                }
            }
            user.setRoomID(roomID);
            user.setRoomType(room.getType());

            synchronized (room) {
                addRoomRef(room);
                room.enter(user);
                // if its a contest room check if we have a coder object for him, if not create one
                if (ContestConstants.isPracticeRoomType(room.getType())) {
                    BaseCodingRoom cr = (BaseCodingRoom) room;
                    if (!cr.isUserAssigned(userID)) {
                        BaseRound contest = (BaseRound) getContestRound(cr.getRoundID(), false);
                        Coder coder = null;
                        if (!contest.getRoundType().isTeamRound()) {
                            s_trace.debug("Adding Individual Coder to room assignments.");
                            coder = CoderFactory.createCoder(userID, user.getName(), cr.getDivisionID(),
                                    contest, roomID, user.getRating(room.getRatingType()).getRating(), user.getLanguage());
                        } else {
                            s_trace.debug("Adding Team Coder to room assignments.");
                            if (!user.isOnTeam()) {
                                s_trace.error("User: " + user + " has tried to enter a team practice room but is not on a team.");
                                return;
                            }
                            Team team = getTeam(user.getTeamID(), false);
                            coder = new TeamCoder(team, contest, cr.getDivisionID(),
                                                  roomID, team.getRating(), team.getLanguage());

                            for (Iterator it = team.getMembers().iterator(); it.hasNext();) {
                                User iuser = getUser(((Integer) it.next()).intValue(), false);

                                Coder icoder =  CoderFactory.createCoder(iuser.getID(), iuser.getName(), cr.getDivisionID(), contest, roomID,
                                        iuser.getRating(room.getRatingType()).getRating(), iuser.getLanguage());
                                ((TeamCoder) coder).addMemberCoder(icoder);
                            }
                            ((TeamCoder) coder).setComponentAssignmentData(s_dbServices.getComponentAssignmentData(team.getID(), contest.getRoundID()));
                        }
                        if (!contest.getRoundType().isLongRound()) {
                            s_dbServices.addPracticeCoder(coder);
                        }
                        cr.addCoder(coder);
                        Iterator connectionsIterator = Processor.getConnectionIDs(TCEvent.ROOM_TARGET, cr.getRoomID());
                        Set connections = new HashSet();
                        while (connectionsIterator.hasNext()) {
                            connections.add(connectionsIterator.next());
                        }
                        connections.remove(RequestProcessor.getConnectionID(userID));
                        ResponseProcessor.addPracticeCoder(connections.iterator(), cr);
                    }
                }
            }
            if (room instanceof BaseCodingRoom) {
                user.setContestRoom(roomID);
            }

            // broadcast out a move event
            MoveEvent me = new MoveEvent(user.getID(), roomID);
            EventService.sendGlobalEvent(me);

            saveToCache(room.getCacheKey(), room);
            saveToCache(User.getCacheKey(user.getID()), user);

        } catch (Exception e) {
            s_trace.error("Exception in enter", e);
        }
    }

    /**
     * Runs system test
     * Note: if coderID > 0 its only running for that user
     */
    public static void systemTest(int contestId, int roundId, int coderId, int problemId, boolean reference) throws CoreServicesException {
        s_trace.info("STARTING SYSTEM TESTS: CoreServices.systemTest(" + contestId + ", " + roundId + ", " + coderId + ", " + problemId + ", " + reference + ")");
        try {
            int[] componentIds = null;
            if (problemId != 0) {
                componentIds = new int[] {problemId};
            }
            SRMTestScheduler.execSystemTest(contestId, roundId, coderId, reference, componentIds);
        } catch (SRMTestSchedulerStateException e) {
            throw new CoreServicesException(e.getMessage(),e);
        } catch (Exception e) {
            s_trace.error("Exception running system test", e);
        }
    }

    /**
     * Clears test cases cache
     */
    public static void clearTestCases() {
        TestCaseCache.clearTestCasesCache();
    }

    /**
     * Cancels a system test case testing.
     * This command should be called when a system test case has been changed or deleted.
     */
    public static void cancelSystemTestCaseTesting(int contestId, int roundId, int testCaseId) throws CoreServicesException {
        s_trace.info("Cancelling System test case testing: CoreServices.cancelSystemTestCaseTesting(" + contestId + ", " + roundId + ", " + testCaseId + ")");
        try {
            SRMTestScheduler.cancelSystemTestCaseTesting(contestId, roundId, testCaseId);
        } catch (SRMTestSchedulerStateException e) {
            throw new CoreServicesException(e.getMessage(),e);
        } catch (Exception e) {
            s_trace.error("Exception cancelling system test case testing", e);
        }
    }


    /**
     * Runs practice system tests on specific components
     * Note: if coderID > 0 its only running for that user
     */
    public static void practiceSystemTest(int contestId, int roundId, int coderId, int[] componentsId) {
        s_trace.info("STARTING PRACTICE SYSTEM TESTS: CoreServices.practiceSystemTest(" + contestId + ", " + roundId + ", " + coderId + ", " + ArrayUtils.asString(componentsId)+ ")");
        try {
            SRMTestScheduler.execPracticeSystemTest(contestId, roundId, coderId, componentsId);
        } catch (Exception e) {
            s_trace.error("Exception running system test", e);
        }
    }

    /**
     * Cancels and removes any previous system test run/running for the coder/component and reschedules all system tests availables for the component
     */
    public static void autoSystemTest(int roomId, int coderId, int componentId) {
        if (!s_autoSystemTest) {
            //If the property was not set, just return
            return;
        }
        s_trace.info("STARTING AUTO SYSTEM TEST: CoreServices.autoSystemTest(" + coderId + ", " +  componentId+ ")");
        try {
            SRMTestScheduler.execAutoSystemTest(roomId, coderId, componentId);
        } catch (Exception e) {
            s_trace.error("Exception running auto system test. coder="+coderId+" componentId="+componentId, e);
        }
    }

    /**
     * Activates a contest so people can move into it
     */
    public static void enableContestRound(int roundId) {
        try {
            Round contest = getContestRound(roundId, true);
            contest.setActiveMenu(true);
            saveToCache(contest.getCacheKey(), contest);
        } finally {
            releaseLock(ContestRound.getCacheKey(roundId));
        }
        info("Activated round: " + roundId);
        ActionEvent event = new ActionEvent(TCEvent.ALL_TARGET, roundId, ActionEvent.ENABLE_CONTEST);
        event.setRoundID(roundId);
        EventService.sendGlobalEvent(event);
        //        try {
        //            s_dbServices.changeContestRoundMenuStatus(roundId, true);
        //        } catch (Exception e) {
        //            s_trace.error("Exception trying to enable contest round", e);
        //        }
    }

    /**
     * Disables a contest on the applets menu
     */
    public static void disableContestRound(int roundId) {
        try {
            Round contest = getContestRound(roundId, true);
            contest.setActiveMenu(false);
            saveToCache(contest.getCacheKey(), contest);
        } finally {
            releaseLock(ContestRound.getCacheKey(roundId));
        }
        info("Disabled round: " + roundId);
        ActionEvent event = new ActionEvent(TCEvent.ALL_TARGET, roundId, ActionEvent.DISABLE_CONTEST);
        event.setRoundID(roundId);
        EventService.sendGlobalEvent(event);
        //        try {
        //            s_dbServices.changeContestRoundMenuStatus(roundId, false);
        //        } catch (Exception e) {
        //            s_trace.error("Exception trying to disable contest round", e);
        //        }
    }

    /**
     * Assigns rooms
     */
    // dpecora - remove startRoom as a parameter
    public static void assignRooms(int contestId, int roundId, int codersPerRoom, int type,
                                   boolean byDivision, boolean isFinal, boolean isByRegion, double p) {
        info("Assigning rooms for: " + roundId + " " + codersPerRoom + " coders per room");
        try {
            s_dbServices.assignRooms(contestId, roundId, codersPerRoom, type, byDivision, isFinal, isByRegion, p);

            s_trace.info("STARTING CONTEST ROUND");
            getContestRound(roundId, true);
            Round contest = getContestRoundFromDb(roundId);
            contest.setActive(true);
            //int roundTypeId = contest.getRoundType();

            contest.endRegistrationPhase();

            //moved to here to stop a bunch of lockups without reason
            saveToCache(contest.getCacheKey(), contest);

            synchronized (activeRounds) {
                if (activeRounds.contains(new Long(roundId))) {
                    // Flush round room references
                    addRoundRef(roundId);
                }
            }

            s_trace.debug("Assigned rooms complete and saved to cache");
            // Update menu so ppl can move to rooms early.
            ActionEvent event = new ActionEvent(TCEvent.ALL_TARGET, contestId, ActionEvent.ASSIGNED_ROOMS);
            event.setRoundID(roundId);
            EventService.sendGlobalEvent(event);

        } catch (Exception e) {
            s_trace.error("Exception assigning rooms", e);
        } finally {
            s_trace.info("DONE WITH CONTEST ROUND");
            releaseLock(ContestRound.getCacheKey(roundId));
        }
    }


    /**
     * Archives chat
     */
    public static void archiveChat(ArrayList chatEvents) {
        try {
            s_dbServices.archiveChat(chatEvents);
            if (s_trace.isDebugEnabled()) {
                s_trace.debug("Archived " + chatEvents.size() + " chats.");
            }            
        } catch (Exception e) {
            e.printStackTrace();
            s_trace.error("Error archiveChat", e);
        }
    }

    /**
     * Deletes a contest from the DB
     */
    /*
      public static void deleteContest(int contestID) {
      try {
      s_dbServices.deleteContest(contestID);
      } catch (Exception e) {
      e.printStackTrace();
      s_trace.error("Error delete contest", e);
      }
      }
    */

    /**
     * Create's a contest in the db
     */
    public static void createContest(Round cr) {
        try {
            s_dbServices.createContest(cr);
        } catch (Exception e) {
            e.printStackTrace();
            s_trace.error("Error creating contest", e);
        }
    }

    /**
     * Returns a list of the practice contest ids
     */
    private static ArrayList getPracticeRoundIDs() {
        try {
            return s_dbServices.getPracticeRoundIDs();
        } catch (Exception e) {
            e.printStackTrace();
            s_trace.error("Error getting practice contests", e);
        }
        return null;
    }

    /**
     * Returns a list of round categories for use of practice rounds menu creation
     */
    public static CategoryData[] loadCategories() {
        try {
            s_trace.debug("In CoreServices loadCategories()");
            return s_dbServices.getCategories();
        } catch (Exception e) {
            s_trace.error("Error getting round categories", e);
            throw new RuntimeException(e);
        }
    }

    private static Set activePracticeRounds = Collections.synchronizedSet(new HashSet());
    private static Set activePracticeRooms = Collections.synchronizedSet(new HashSet());

    public static boolean isPracticeRoundActive(long roundID) {
        return activePracticeRounds.contains(new Long(roundID));
    }

    public static boolean isPracticeRoomActive(int roomID) {
        return activePracticeRooms.contains(new Integer(roomID));
    }

    public static boolean isLobbyRoom(int roomID) {
        for (Iterator iter = lobbies.getAllRoomIDs(); iter.hasNext(); ) {
            if (((Integer) iter.next()).intValue() == roomID) {
                return true;
            }
        }
        return false;
    }

    public static boolean isRoomActive(int roomID) {
        Round[] rounds = getAllActiveRounds();
        for (int i=0;i<rounds.length;++i) {
            for (Iterator iter = rounds[i].getAllRoomIDs(); iter.hasNext(); ) {
                if (((Integer) iter.next()).intValue() == roomID) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns a list of the practice contest ids
     */
    public static Round[] loadPracticeRounds(int limit) {
        try {
            synchronized(activePracticeRounds) {
                synchronized(activePracticeRooms) {
                    activePracticeRounds.clear();
                    activePracticeRooms.clear();
                    Round[] practiceRounds = s_dbServices.getPracticeRounds(limit);
                    for (int i = 0; i < practiceRounds.length; i++) {
                        Round practiceRound = practiceRounds[i];
                        activePracticeRounds.add(new Long(practiceRound.getRoundID()));
                        for (Iterator iter = practiceRound.getAllRoomIDs(); iter.hasNext(); ) {
                            activePracticeRooms.add(iter.next());
                        }
                        saveToCache(practiceRound.getCacheKey(), practiceRound);
                    }
                    return practiceRounds;
                }
            }
        } catch (Exception e) {
            s_trace.error("Error getting practice contests", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns a practice rounds a given user has visited
     */
    public static int[] loadVisitedPracticeRounds(int coderID) {
        try {
            return s_dbServices.getVisitedPracticeRounds(coderID);
        } catch (Exception e) {
            s_trace.error("Error getting visited practice rounds", e);
            throw new RuntimeException(e);
        }
    }


    /**
     * Allocates the prizes after a contest
     */
    public static Collection allocatePrizes(int roundID, boolean isFinal) {
        try {
            return s_dbServices.allocatePrizes(roundID, isFinal);
        } catch (Exception e) {
            s_trace.error("Error allocating prizes", e);
            return null;
        }
    }

    /**
     * Adds time to the current contest
     */
    public static void addTimeToContestRound(Round contest, int minutes, int seconds, int phase, boolean addtostart) {
        try {
            s_dbServices.addTime(contest, minutes, seconds, phase, addtostart);
            Long roundID = new Long(contest.getRoundID());
            handleRoundScheduleChanged(roundID);
        } catch (Exception e) {
            s_trace.error("Error adding time to current contest", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * @param roundID
     * @throws RemoteException
     * @throws DBServicesException 
     */
    static void handleRoundScheduleChanged(Long roundID) throws RemoteException, DBServicesException {
        Round contest = updateRoundDefinition(roundID);
        // reset timer
        RoundTimerTaskSet roundTimerTaskSet = (RoundTimerTaskSet) roundTimerTasks.remove(roundID);
        roundTimerTaskSet.cancel();
        roundTimerTaskSet = new RoundTimerTaskSet(contest.getContestID(), contest.getRoundID(), getWaitTimeToUnloadAfterCoding());
        roundTimerTasks.put(roundID, roundTimerTaskSet);
        roundTimerTaskSet.schedule(roundTimer, false);
    }

    static Round updateRoundDefinition(Long roundID) throws RemoteException, DBServicesException {
        Round oldRound = getContestRound(roundID.intValue(), true);;
        Round contest = getContestRoundFromDb(roundID.intValue());
        contest.setActive(oldRound.isActive());
        contest.setActiveMenu(oldRound.getActiveMenu());
        saveToCache(contest.getCacheKey(), contest);

        synchronized (activeRounds) {
            if (activeRounds.contains(roundID)) {
                addRoundRef(roundID.intValue());
            }
        }

        return contest;
    }

    /**
     * Wraps a getComponent call with the point value for the problem
     */
    public static RoundComponent getRoundComponent(int roundID, int componentID, int divisionID) {
        Round contest = getContestRound(roundID, false);
        try {
            int points = contest.getRoundComponentPointVal(componentID, divisionID);
            if (s_trace.isDebugEnabled()) {
                s_trace.debug(
                              "Got roundComponentPointVal - " +
                              "roundID  = " + roundID +
                              ", componentID = " + componentID +
                              ", division ID = " + divisionID +
                              ", points = " + points
                              );
            }
            return new RoundComponent(points, getSimpleComponent(componentID));
        } catch (Throwable t) {
            s_trace.error("Failed to get point vals", t);
            throw new RuntimeException(t.getMessage());
        }
    }

    public static void processRoundEvent(Round contestRound) {
        try {
            s_dbServices.processRoundEvent(contestRound);
        } catch (Exception e) {
            s_trace.error(e);
            throw new RuntimeException(e);
        }
    }

    public static void replayOpenComponent(int userID, int componentId) {
        boolean releaseLock = true;
        int roomID = GARBAGE;
        try {
            User user = getUser(userID, false);

            //   get the object
            roomID = user.getRoomID();
            BaseCodingRoom room = (BaseCodingRoom) getRoom(roomID, true);
            Coder coder = room.getCoder(userID);
            SimpleComponent component = getSimpleComponent(componentId);
            if (s_trace.isDebugEnabled()) {
                s_trace.debug("getComponent has coder opened id = " + componentId + " open = "
                              + coder.isComponentOpened(componentId));
            }            
            if (!coder.isComponentOpened(componentId)) {
                releaseLock = openComponent(coder, userID, room, component, componentId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            s_trace.error("Error in replayOpenComponent()", e);
        } finally {
            if (releaseLock) releaseLock(Room.getCacheKey(roomID));
        }
    }

    private static boolean openComponent(Coder coder, int userID, BaseCodingRoom room, SimpleComponent comp, int componentID) throws Exception {
        try {
            // make sure the db records his open!!!
            info("OPEN coder:" + coder.getID() + " contest: " + coder.getContestID() +
                    " round: " + coder.getRoundID() + " room: " + room.getRoomID() +
                    " component: " + comp.getComponentID());
            Round round = getContestRound(room.getRoundID());
            User user = getUser(userID, false);
            if (round.getPhase() == ContestConstants.CODING_PHASE || user.isLevelTwoAdmin()) {
                // GT says he only cares if its the coding phase
                if (!round.isLongContestRound()) {
                    long openTime = s_dbServices.coderOpenComponent(userID, coder.getContestID(), coder.getRoundID(), room.getRoomID(), comp.getComponentID());
                    handleComponentOpened(coder, room, comp, openTime);
                    return false;
                } else {
                    LongContestServicesLocator.getService().openComponentIfNotOpened(coder.getContestID(), coder.getRoundID(), comp.getComponentID(), userID);
                }
            }
            return true;
        } catch (Exception e) {
            s_trace.error("Error in openComponent()", e);
            throw e;
        }
    }

    public static void handleComponentOpened(Coder coder, BaseCodingRoom room, SimpleComponent comp, long openTime) {
        coder.setOpenedComponent(comp.getComponentID());
        // save this room
        saveToCache(room.getCacheKey(), room);

        // send out the event if its a new open
        BaseCoderComponent coderComponent = coder.getComponent(comp.getComponentID());
        coderComponent.setOpenedTime(openTime);
        Round round = getContestRound(coder.getRoundID(), false);

        // send the event out
        StringBuilder message = new StringBuilder(50);
        message.append("System> ");
        message.append(coder.getName());
        message.append(" is opening the ");
        ComponentNameBuilder nameBuilder = round.getRoundType().getComponentNameBuilder();
        String componentName = nameBuilder.longNameForComponent(
                comp.getClassName(), 
                round.getRoundComponentPointVal(comp.getComponentID(), room.getDivisionID()), 
                round.getRoundProperties());
        message.append(componentName);
        message.append(".\n");
        ContestEvent event = new ContestEvent(room.getRoomID(), ContestEvent.OPEN_COMPONENT, message.toString(),
                coder.getID(), comp.getProblemID(), comp.getComponentID(), coderComponent.getStatusString());
        event.setChallengerName(coder.getName());
        event.setEventTime(System.currentTimeMillis());
        EventService.sendGlobalEvent(event);
    }

    private static void openProblem(Coder coder, BaseCodingRoom room, int problemID) {
        info("OPEN " + coder.getID() + ", " + coder.getContestID() + "," + coder.getRoundID() + "," + room.getRoomID() + "," +
             problemID);
    }

    /**
     * Retreives the specified problem at index problemIndex
     *
     * @param componentID index of the component selected
     */
    public static RoundComponent getComponent(int userID, int componentID) {
        boolean releaseLock = true;
        int roomID = GARBAGE;
        try {
            User user = getUser(userID, false);

            // get the object
            roomID = user.getRoomID();
            BaseCodingRoom room = (BaseCodingRoom) getRoom(roomID, true);
            Coder coder = room.getCoder(userID);
            Round contest = getContestRound(coder.getRoundID(), false);
            SimpleComponent component = getSimpleComponent(componentID);
            info("getComponent has coder opened id = " + componentID + " open = " + coder.isComponentOpened(componentID));
            if (!coder.isComponentOpened(componentID)) {
                releaseLock = openComponent(coder, userID, room, component, componentID);
            }
            int points = contest.getRoundComponentPointVal(componentID, room.getDivisionID());
            return new RoundComponent(points, component);
        } catch (Exception e) {
            s_trace.error("Error in getComponent()", e);
        } finally {
            if (releaseLock) releaseLock(Room.getCacheKey(roomID));
        }
        return null;
    }

    public static RoundProblem getProblem(int userID, int problemID) {
        boolean releaseLock = true;
        int roomID = GARBAGE;
        try {
            User user = getUser(userID, false);
            roomID = user.getRoomID();
            BaseCodingRoom room = (BaseCodingRoom) getRoom(roomID, true);
            Coder coder = room.getCoder(userID);
            getContestRound(coder.getRoundID(), false);
            Problem problem = getProblem(problemID);
            openProblem(coder, room, problemID);
            return new RoundProblem(-1, problem, -1);
        } catch (Exception e) {
            s_trace.error("Error in getProblem()", e);
        } finally {
            if (releaseLock) releaseLock(Room.getCacheKey(roomID));
        }
        return null;
    }

    public static Round getContestRoundFromCache(int roundID) {
        return getContestRoundFromCache(roundID, false);
    }

    private static Round getContestRoundFromCache(int roundID, boolean lock) {
        String key = ContestRound.getCacheKey(roundID);
        return (Round) safeCacheGet(key, lock);
    }    
    
    public static Round getContestRound(int roundID) {
        return getContestRound(roundID, false);
    }
    
    public static Round getLobbiesContestRound(int roundID) {

        try {
            Round contestRound = getContestRoundFromDb(roundID);
            s_trace.debug("Going to the DB for lobbies");
            if (contestRound != null) {
                lobbies = contestRound;
                saveToCache(contestRound.getCacheKey(), contestRound);
                synchronized (activeRounds) {
                    addRoundRef(roundID);
                }
            }
            return contestRound;
        } catch (Exception e) {
            s_trace.error("Exception in getContestRound(" + roundID + ")", e);
        }
        return null;
    }
    public static WeakestLinkRound getWeakestLinkRound(int roundId) {
        return (WeakestLinkRound) getContestRound(roundId);
    }

    public static void addActiveContest(int roundID) {
        synchronized(activeRounds) {
            Long key = new Long(roundID);
            if(!activeRounds.contains(key)) {
                activeRounds.add(key);
                s_cache.addRef(ContestRound.getCacheKey(roundID));
                s_cache.addRef(Registration.getCacheKey(roundID));
                s_cache.addRef(LeaderBoard.getCacheKey(roundID));
            }
            // Add room, problem, etc.
            addRoundRef(roundID);
        }
    }

    private static void removeRoundRef(int roundID) {
        Long id = new Long(roundID);
        synchronized (activeRoundRefs) {
            if (activeRoundRefs.containsKey(id)) {
                //info("Start remove round " + roundID + " strong reference");
                for (Iterator iter = ((Collection)activeRoundRefs.get(id)).iterator(); iter.hasNext(); ) {
                    String key = (String) iter.next();
                    s_cache.removeRef(key);
                    // Unload the room
                    saveToCache(key, null);
                }
                //info("End remove round " + roundID + " strong reference");
                activeRoundRefs.remove(id);
            }
        }
    }

    private static void moveUserOutOfRound(Round round) {
        for (Iterator iter = round.getAllRoomIDs(); iter.hasNext(); ) {
            int roomId = ((Integer) iter.next()).intValue();
            Room room = getRoomFromCache(roomId);

            // Move all users
            for (Iterator iterUser = room.getUserNames().iterator(); iterUser.hasNext(); ) {
                User user = getUser((String) iterUser.next());
                // Move the in-room users to lobbies
                RequestProcessor.bootUser(RequestProcessor.getConnectionID(user.getID()));
            }

            if (room instanceof BaseCodingRoom) {
                // Close all spectators
                for (Iterator iterUser = ((BaseCodingRoom) room).getAllSpectators(); iterUser.hasNext(); ) {
                    Integer userId = (Integer) iterUser.next();
                    User user = getUser(userId.intValue());
                    ((BaseCodingRoom) room).removeSpectator(userId.intValue());
                    user.removeWatchedDivSummaryRoom(roomId);
                    user.removeWatchedRoom(roomId);
                    Processor.toggleWatchUserConnection(userId.intValue(), RequestProcessor.getConnectionID(userId.intValue()), roomId, false);
                }
            }
        }
    }

    private static void addRoundRef(int roundID) {
        removeRoundRef(roundID);
        Long id = new Long(roundID);
        Round round = null;
        round = getContestRound(roundID, false);
        synchronized (activeRoundRefs) {
            Collection collection = new ArrayList();
            //info("Start add round " + roundID + " strong reference");
            for (Iterator iter = round.getAllRoomIDs(); iter.hasNext(); ) {
                Integer roomId = (Integer) iter.next();
                collection.add(Room.getCacheKey(roomId.intValue()));
                s_cache.addRef(Room.getCacheKey(roomId.intValue()));
            }
            for (Iterator iter = round.getDivisions().iterator(); iter.hasNext(); ) {
                int division = ((Integer) iter.next()).intValue();
                for (Iterator iterComp = round.getDivisionComponents(division).iterator(); iterComp.hasNext(); ) {
                    Integer compId = (Integer) iterComp.next();
                    SimpleComponent simpleComponent = getRoundComponent(roundID, compId.intValue(), division).getComponent();
                    collection.add(simpleComponent.getCacheKey());
                    s_cache.addRef(simpleComponent.getCacheKey());
                    Problem problem = getProblem(simpleComponent.getProblemID());
                    collection.add(problem.getCacheKey());
                    s_cache.addRef(problem.getCacheKey());
                    // No need to store problem component, since it is strongly referenced in problem object.
                }
            }
            //info("End add round " + roundID + " strong reference");
            activeRoundRefs.put(id, collection);
        }
    }

    public static boolean isRoundActive(long roundID) {
        return activeRounds.contains(new Long(roundID));
    }

    public static void loadContestRound(int roundID) {
        synchronized (activeRounds) {
            Long key = new Long(roundID);
            if (activeRounds.contains(key)) {
                unloadContestRound(roundID);
            }
            info("Loading round #" + roundID);
            Round round = null;
            try {
                round = getContestRound(roundID, true);
                round.setActive(true);
                activeRounds.add(key);
                s_cache.addRef(ContestRound.getCacheKey(roundID));
                s_cache.addRef(Registration.getCacheKey(roundID));
                s_cache.addRef(LeaderBoard.getCacheKey(roundID));
                addRoundRef(roundID);
                System.out.println("set active true for roundId " + roundID);
                // cache registration
                getRegistration(roundID);
                getLeaderBoard(roundID, false);
                RoundTimerTaskSet roundTimerTaskSet = new RoundTimerTaskSet(round.getContestID(), round.getRoundID(), getWaitTimeToUnloadAfterCoding());
                roundTimerTaskSet.schedule(roundTimer);  // this will start the timer
                roundTimerTasks.put(key, roundTimerTaskSet);
            } finally {
                saveToCache(ContestRound.getCacheKey(roundID), round);
            }
        }
    }

    private static void unloadContestRound(Long roundID) {
        synchronized (activeRounds) {
            if (!activeRounds.contains(roundID)) {
                throw new IllegalArgumentException("Round #" + roundID + " not loaded!");
            }
            info("Unloading round #" + roundID);
            RoundTimerTaskSet roundTimerTaskSet = (RoundTimerTaskSet) roundTimerTasks.remove(roundID);
            roundTimerTaskSet.cancel();  // stop timer events
            Round round = getContestRound(roundID.intValue(), false);
            activeRounds.remove(roundID);
            s_cache.removeRef(ContestRound.getCacheKey(roundID.intValue()));
            s_cache.removeRef(Registration.getCacheKey(roundID.intValue()));
            s_cache.removeRef(LeaderBoard.getCacheKey(roundID.intValue()));
            // Remove reference of all rooms
            moveUserOutOfRound(round);
            removeRoundRef(roundID.intValue());
            saveToCache(ContestRound.getCacheKey(roundID.intValue()), null);
            saveToCache(Registration.getCacheKey(roundID.intValue()), null);
            saveToCache(LeaderBoard.getCacheKey(roundID.intValue()), null);
        }
    }

    public static void unloadContestRound(int roundID) {
        unloadContestRound(new Long(roundID));
    }

    public static Round getContestRoundByKey(String key, boolean lock) {
        Round cr = (Round) safeCacheGet(key, lock);
        return cr;
    }

    /**
     * Retreives the specified contest round, locking if necessary
     */
    public static Round getContestRound(int roundID, boolean lock) {
        try {
            Round cr = getContestRoundFromCache(roundID, lock);
            if (cr == null) {
                cr = getContestRoundFromDb(roundID);

                // save it back to the cache always if he doesn't want to write
                if (!lock && cr != null) saveToCache(cr.getCacheKey(), cr);
            }
            return cr;
        } catch (Exception e) {
            s_trace.error("Exception in getContestRound(" + roundID + "," + lock + ")", e);
        }
        return null;
    }

    /**
     * retrieve the specified event registration data from database.
     * @param userId
     *         the user id.
     * @param eventId
     *         the event id.
     * @param lock
     *         if it is needed to lock
     * @return
     *         the round event data.
     */
    public static EventRegistration getEventRegistration(int userId, int eventId) {
        try {
            return getEventRegistrationFromDb(userId,eventId);
        } catch (Exception e) {
            s_trace.error("Exception in getEventRegistration(" + userId + "," + eventId +")", e);
        }
        return null;
    }

    private static Round getContestRoundFromDb(int roundID) throws RemoteException, DBServicesException {
        Round contestRound = s_dbServices.getContestRound(roundID);
        boolean loadWeakestLinkData = s_coreSettings.getBoolean("loadWeakestLinkData");
        if (loadWeakestLinkData && contestRound instanceof WeakestLinkRound) {
            WeakestLinkRound weakRound = (WeakestLinkRound) contestRound;
            weakRound.loadWeakestLinkData();
        } 
        return (Round) contestRound;
    }
    /**
     * <p>
     * get the event registration data from database.
     * </p>
     * @param userId
     *         the user id.
     * @param eventId
     *         the event id.
     * @return  the event registration data.
     * @throws RemoteException
     *          if any error occur during ejb call.
     * @throws DBServicesException
     *          if any error occur during DBService ejb call.
     */
    private static EventRegistration getEventRegistrationFromDb(int userId,int eventId) 
        throws RemoteException, DBServicesException {
        return s_dbServices.getEventRegistrationData(userId,eventId);
    }
    
    public static Problem getProblemFromCache(int problemID) {
        String problemKey = Problem.getCacheKey(problemID);
        return (Problem) safeCacheGet(problemKey, false);
    }

    /**
     * Retreives the specified problem.
     */
    public static Problem getProblem(int problemID) {
        try {
            /*
              String problemKey = Problem.getCacheKey(problemID);
              Problem problem = (Problem)safeCacheGet(problemKey, false);
              //Problem problem = (Problem)safeCacheGet(problemKey, lock);
              */
            Problem problem = getProblemFromCache(problemID);
            if (problem == null) {
                problem = s_problemServices.getProblem(problemID);

                // save it back to the cache always if he doesn't want to write
                saveToCache(problem.getCacheKey(), problem);

                //We must store problemComponents in the cache to avoid loading them again.
                if (problem != null) { 
                    ProblemComponent[] components = problem.getProblemComponents();
                    for (int i = 0; i < components.length; i++) {
                        saveToCache(components[i].getCacheKey(), components[i]);
                    }
                }
                //if (!lock) saveToCache(problem.getCacheKey(), problem);
            }
            return problem;
        } catch (Exception e) {
            s_trace.error("Exception in getProblem(" + problemID + ")", e);
            //s_trace.error("Exception in getProblem("+problemID+","+lock+")", e);
        }
        return null;
    }

    private static ProblemComponent getComponentFromCache(int componentID) {
        String key = ProblemComponent.getCacheKey(componentID);
        return (ProblemComponent) safeCacheGet(key, false);
    }

    /**
     * Retreives the specified component.
     */
    public static ProblemComponent getComponent(int componentID) {
        try {
            ProblemComponent component = getComponentFromCache(componentID);
            if (component == null) {
                component = s_problemServices.getProblemComponent(componentID, false);

                if (s_trace.isDebugEnabled()) {
                    s_trace.debug("Loaded component " + component);
                }

                // save it back to the cache always if he doesn't want to write
                saveToCache(component.getCacheKey(), component);
            }
            return component;
        } catch (Exception e) {
            s_trace.error("Exception in getComponent(" + componentID + ")", e);
        }
        return null;
    }

    //added 2-20 rfairfax
    public static boolean clearPracticeProblem(int userID, int roomID, Long[] componentID)
    {
        try {
            BaseCodingRoom room = (BaseCodingRoom) getRoom(roomID, false);
            boolean teamClear = room.getType() == ContestConstants.TEAM_ADMIN_ROOM ||
                    room.getType() == ContestConstants.TEAM_PRACTICE_CODER_ROOM;
            int coderID = userID;
            if (teamClear) {
                coderID = getUser(userID, false).getTeamID();
            }


            for(int i = 0; i < componentID.length;i++)
            {
                if (s_dbServices.clearPracticeProblem(coderID, room.getRoundID(),componentID[i], teamClear)) {
                    boolean releaseLock = true;
                    try {
                        BaseCodingRoom cr = (BaseCodingRoom) getRoom(roomID, true);
                        Coder coder = cr.getCoder(userID);
                        // Note this doesnt clear out challenges on this user by other coders in this room.
                        coder.clearProblem(componentID[i]);
                        saveToCache(cr.getCacheKey(), cr);
                        releaseLock = false;

                        ContestEvent event = new ContestEvent(roomID, ContestEvent.CLEAR_PRACTICE_PROBLEM, null,
                                userID, 0, componentID[i].intValue(), "Unopened");
                        event.setTotalPoints(coder.getPoints());

                        EventService.sendGlobalEvent(event);

                    } catch (Exception e) {
                        s_trace.error("Exception in clearPracticeProblem():clearing contest room", e);
                        return false;
                    } finally {
                        if (releaseLock) {
                            releaseLock(Room.getCacheKey(roomID));
                        }
                    }
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
            s_trace.error("Error in clearPracticeProblem()", e);
            return false;
        }
        return true;

    }

    /**
     * Assumes the user is in a practice room, and it will try to clear his data for that room
     */
    public static boolean clearPracticer(int userID, int roomID) {
        try {
            BaseCodingRoom room = (BaseCodingRoom) getRoom(roomID, false);
            boolean teamClear = room.getType() == ContestConstants.TEAM_ADMIN_ROOM ||
                room.getType() == ContestConstants.TEAM_PRACTICE_CODER_ROOM;
            int coderID = userID;
            if (teamClear) {
                coderID = getUser(userID, false).getTeamID();
            }


            if (s_dbServices.clearPracticer(coderID, room.getRoundID(), teamClear)) {
                boolean releaseLock = true;
                try {
                    BaseCodingRoom cr = (BaseCodingRoom) getRoom(roomID, true);
                    Coder coder = cr.getCoder(userID);
                    // Note this doesnt clear out challenges on this user by other coders in this room.
                    coder.clearData();
                    saveToCache(cr.getCacheKey(), cr);
                    releaseLock = false;

                    ContestEvent event = new ContestEvent(roomID, ContestEvent.CLEAR_PRACTICER, null,
                                                          userID, 0, 0, "Unopened");
                    EventService.sendGlobalEvent(event);

                } catch (Exception e) {
                    s_trace.error("Exception in clearPracticer():clearing contest room", e);
                    return false;
                } finally {
                    if (releaseLock) {
                        releaseLock(Room.getCacheKey(roomID));
                    }
                }
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            s_trace.error("Error in clearPracticer()", e);
            return false;
        }
    }

    private static final String LOGGEDIN_USERIDS_KEY = "UserLogin.KEY";

    public static Integer handleToID(String handle) {
        return handleToID(handle, false);
    }

    private static Integer handleToID(String handle, boolean ignoreCase) {
        Integer userID;
        try {
            userID = (Integer) s_handleToIDMap.get(handle.toLowerCase());
            if (s_trace.isDebugEnabled()) {
                s_trace.debug("USER ID:" + userID);
            }            
            if (userID == null) {

                if (s_trace.isDebugEnabled()) {
                    s_trace.debug("user id is: " + userID);
                    s_trace.debug("handle is: " + handle);
                }                
                User user = s_dbServices.getUser(handle, ignoreCase);

                //s_trace.debug("This is the correct name we got from the db"+user.getName());
                if (user != null) {
                    userID = (Integer) s_handleToIDMap.get(user.getName().toLowerCase());
                    if (userID != null) {
                        return userID;
                    } else {
                        userID = new Integer(user.getID());
                        saveToCache(user.getCacheKey(), user);
                        if (s_trace.isDebugEnabled()) {
                            s_trace.debug("user is saved: " + userID);
                        }                        
                        //s_handleToIDMap.put(handle.toLowerCase(), new Integer(user.getID()));
                        s_handleToIDMap.put(user.getName().toLowerCase(), new Integer(user.getID()));
                        return userID;
                    }
                } else {
                    return null;
                }
            } else {
                return userID;
            }
        } catch (Exception e) {
            e.printStackTrace();
            s_trace.error("getUser(handle) failed for " + handle, e);
        }
        return null;
    }



    /**
     * returns true if the user is logged in, false otherwise
     */
    public static boolean isLoggedIn(String handle) {
        return isLoggedIn(handle, false);
    }
    public static boolean isLoggedIn(String handle, boolean ignoreCase) {
        Integer userID = handleToID(handle, ignoreCase);
        if (s_trace.isDebugEnabled()) {
            s_trace.debug("User id is: " + userID);
        }        
        synchronized (s_loginLock) {
            if (s_trace.isDebugEnabled()) {
                s_trace.debug("User is logged in: " + isLoggedIn(userID));
            }            
            return isLoggedIn(userID);
        }
    }

    public static boolean isLoggedIn(Integer userID) {
        boolean ret = getLoginSet(false).contains(userID);
        if (s_trace.isDebugEnabled()) {
            s_trace.debug("in isLoggedIn method :" + ret);
        }        
        return ret;
    }

    private static final String LOGGEDIN_USERNAMES_KEY = "LogIn.Names";
    private static final String LOGGEDIN_USERRATINGS_KEY = "LogIn.Ratings";

    /**
     * returns an array list of logged in names/ratings
     */
    public static ArrayList[] getLoggedInUserData() {
        ArrayList[] result = new ArrayList[2];
        synchronized (s_loginLock) {
            ArrayList names = (ArrayList) safeCacheGet(LOGGEDIN_USERNAMES_KEY, false);
            result[0] = (ArrayList) names.clone();
            ArrayList ratings = (ArrayList) safeCacheGet(LOGGEDIN_USERRATINGS_KEY, false);
            result[1] = (ArrayList) ratings.clone();
        }
        return result;
    }

    /**
     * returns the AdminRoomMap (round id -> admin room id)
     */
    private static final String ADMINROOMMAP_KEY = "Admin.Map";

    private static HashMap getAdminRoomMap() {
        s_trace.debug("getAdminRoomMap");
        HashMap map = (HashMap) safeCacheGet(ADMINROOMMAP_KEY, false);
        if (map == null) {
            try {
                map = s_dbServices.getAdminRoomMap();
                saveToCacheAdminRoomMapKey(map);
            } catch (Exception e) {
                s_trace.error("Error in getAdminRoomMap", e);
            }
        }
        if (s_trace.isDebugEnabled()) {
            s_trace.debug("Admin Room Map: " + map);
        }        
        return map;
    }

    private static void saveToCacheAdminRoomMapKey(HashMap map) {
        try {
            safeCacheGet(ADMINROOMMAP_KEY, true); // get the lock
            saveToCache(ADMINROOMMAP_KEY, map);
        } finally {
            releaseLock(ADMINROOMMAP_KEY);
        }
    }

    /**
     * Returns the Admin RoomID for the given round
     */
    public static int getAdminRoomIDForRound(int roundID) {
        HashMap map = getAdminRoomMap();
        Integer roomid = (Integer) map.get(new Integer(roundID));
        if (roomid == null) {
            s_trace.error("Didn't find an admin room for round " + roundID);
            return ContestConstants.ADMIN_LOBBY_ROOM_ID;
        } else {
            info("Admin Room: " + roundID + "->" + roomid.intValue());
            return roomid.intValue();
        }
    }

    /**
     * returns hashset containing Integers for each logged in userID
     */
    private static HashSet getLoginSet(boolean lock) {
        try {
            HashSet map = (HashSet) safeCacheGet(LOGGEDIN_USERIDS_KEY, lock);
            if (map == null) {
                map = new HashSet(500);

                // save it back to the cache if its read only
                if (!lock) {
                    // get the lock to make sure noone else does this
                    safeCacheGet(LOGGEDIN_USERIDS_KEY, true);
                    saveToCache(LOGGEDIN_USERIDS_KEY, map);
                    releaseLock(LOGGEDIN_USERIDS_KEY);
                }
            }
            return map;
        } catch (Exception e) {
            s_trace.error("Exception in getLoginSet()", e);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Reloads the registration from the database for the active contests
     */
    public static void refreshRegistration() {
        Round[] contests = getAllActiveRounds();
        for (int i = 0; i < contests.length; i++) {
            Round contest = contests[i];
            String key = Registration.getCacheKey(contest.getRoundID());
            try {
                safeCacheGet(key, true);
                Registration reg = s_dbServices.getRegistration(contest.getRoundID());
                // write him back if the other guy doesn't want the lock, no point checknig for NPE
                saveToCache(reg.getCacheKey(), reg);
            } catch (Exception e) {
                s_trace.error("DB problem getting registration", e);
            } finally {
                if (key.length() > 0) releaseLock(key);
            }
        }
    }

    public static Registration getRegistrationFromCache(int eventID) {
        return getRegistrationFromCache(eventID, false);
    }

    public static Registration getRegistrationFromCache(int eventID, boolean lock) {
        String key = Registration.getCacheKey(eventID);
        return (Registration) safeCacheGet(key, lock);
    }

    public static Registration getRegistration(int eventID) {
        return getRegistration(eventID, false);
    }

    /**
     * Retrieves the registration for the given contest
     */
    private static Registration getRegistration(int eventID, boolean lock) {
        Registration reg = null;
        try {
            reg = getRegistrationFromCache(eventID, lock);
        } catch (Exception e) {
            s_trace.error("Cache problem getting registration", e);
        }
        if (reg != null) {
            if (s_trace.isDebugEnabled()) {
                s_trace.debug("Found registration in cache using key = '" + Registration.getCacheKey(eventID) + "'");
            }            
            return reg;
        }
        if (s_trace.isDebugEnabled()) {
            s_trace.debug("Loading registration from db, eventID :" + eventID);
        }        
        try {
            reg = s_dbServices.getRegistration(eventID);

            // write him back if the other guy doesn't want the lock, no point checknig for NPE
            if (!lock) saveToCache(reg.getCacheKey(), reg);
        } catch (Exception e) {
            s_trace.error("DB problem getting registration", e);
        }
        return reg;
    }

    /**
     * Call this function if you are an admin registering a user manually
     *
     * @param handle The user handle
     * @param roundId The round ID
     * @param atLeast18 Whether or not the coder is at least 18 years of age.
     */
    public static CommandResponse registerCoderByHandle(String handle, int roundId, boolean atLeast18) {
        registerIfModChat(roundId, handle);
        Registration reg = null;
        boolean saved = false;
        try {
            reg = getRegistration(roundId, true);
            CommandResponse response = s_dbServices.registerCoderByHandle(handle, roundId, atLeast18);
            if (response instanceof CommandSucceededResponse) {
                User user = getUser(handle);
                reg.register(user);
                saveToCache(reg.getCacheKey(), reg);
                saved = true;
                registerIfLongRound(roundId, user);
            }
            return response;
        } catch (Exception e) {
            s_trace.error("", e);
            return new CommandFailedResponse("Received exception calling EJB:\n" + e);
        } finally {
            if (reg != null && !saved) {
                releaseLock(reg.getCacheKey());
            }
        }
    }

    private static void registerIfModChat(int roundId, String handle) {
        Round contestRound = getContestRound(roundId);
        if (contestRound.getRoundTypeId() == ContestConstants.MODERATED_CHAT_ROUND_TYPE_ID) {
            Collection allowedSpeakers = getAllowedSpeakers(contestRound.getRoundID());
            User user = getUser(handle);
            allowedSpeakers.add(new Integer(user.getID()));
        }
    }


    private static void registerIfLongRound(int roundId, User user) throws RemoteException, SQLException, DBServicesException {
        boolean released = false;
        boolean refreshLists = false;
        Round contestRound = getContestRound(roundId, true);
        try {
            int contestId = contestRound.getContestID();
            boolean isRoundActive = contestRound.isActive();
            boolean isLongRound = contestRound.isLongRound();
            if (isRoundActive && isLongRound) {
                int coderId = user.getID();

                ArrayList allRoomIDsList = contestRound.getAllRoomIDsList();
                int lastRoomId = ((Integer) allRoomIDsList.get(allRoomIDsList.size() - 1)).intValue();
                int roomSeed = 1;
                int divisionSeed = roomSeed;

                BaseCodingRoom oldRoom = (BaseCodingRoom) getRoom(lastRoomId, false);

                //check if we need to add a room
                if(oldRoom.isAdminRoom() || oldRoom.getCoderIDs().length >= CODERS_PER_LONG_ROUND_ROOM) {
                    //create a new room
                    int newRoomId = s_dbServices.createNewQualRoom(roundId);

                    //add the room to the round
                    contestRound.addRoomID(newRoomId);

                    //save round
                    saveToCache(contestRound.getCacheKey(), contestRound);

                    //send out menu update
                    refreshLists = true;

                    lastRoomId = newRoomId;
                    oldRoom = (BaseCodingRoom) getRoom(lastRoomId, false);
                }

                int rating = user.getRating(oldRoom.getRatingType()).getRating();

                s_dbServices.insertRoomResult(roundId, lastRoomId, coderId, roomSeed, rating, divisionSeed);

                if (contestRound instanceof ContestRound) {
                    ((ContestRound) contestRound).getAssignedRoomMap().put(new Integer(coderId), new Integer(oldRoom.getRoomID()));
                }
                releaseLock(contestRound.getCacheKey());
                released = true;

                BaseCodingRoom newRoom = (BaseCodingRoom) refreshRoom(lastRoomId);
                restoreOpenedTimes(oldRoom, newRoom);
                initializeLeaderBoardIfEmpty(contestRound);

                if(refreshLists) {
                    ActionEvent event = new ActionEvent(TCEvent.ALL_TARGET, contestId, ActionEvent.QUAL_UPDATE_ROOMS);
                    event.setRoundID(roundId);
                    EventService.sendGlobalEvent(event);
                }
            }
        } finally {
            if(!released)
                releaseLock(contestRound.getCacheKey());
        }
    }

    private static void restoreOpenedTimes(BaseCodingRoom oldRoom, BaseCodingRoom newRoom) {
        Iterator allCoders = oldRoom.getAllCoders();
        while (allCoders.hasNext()) {
            Coder coder = (Coder) allCoders.next();
            int id = coder.getID();
            Coder newCoder = newRoom.getCoder(id);
            long[] componentIDs = coder.getComponentIDs();
            for (int i = 0; i < componentIDs.length; i++) {
                long componentID = componentIDs[i];
                BaseCoderComponent newComponent = newCoder.getComponent(componentID);
                BaseCoderComponent component = coder.getComponent(componentID);
                long openedTime = component.getOpenedTime();
                newComponent.setOpenedTime(openedTime);
            }
        }
    }

    public static void setupLeaderBoardIfEmpty(Round contestRound) {
        LeaderBoard leaderBoard = CoreServices.getLeaderBoard(contestRound.getRoundID(), false);
        boolean initialized = leaderBoard.isInitialized();
        if (!initialized) {
            LeaderBoard board = CoreServices.getLeaderBoard(contestRound.getRoundID(), true);
            try {
                board.initialize(contestRound);
            } finally {
                CoreServices.saveToCache(board.getCacheKey(), board);
            }
        }
    }

    public static void initializeLeaderBoardIfEmpty(Round contestRound) {
        LeaderBoard leaderBoard = CoreServices.getLeaderBoard(contestRound.getRoundID(), false);
        boolean initialized = leaderBoard.isInitialized();
        if (!initialized) {
            LeaderBoard board = CoreServices.getLeaderBoard(contestRound.getRoundID(), true);
            try {
                board.initialize(contestRound);
            } finally {
                CoreServices.saveToCache(board.getCacheKey(), board);
            }
            ArrayList allResponses = new ArrayList();
            allResponses.add(ResponseProcessor.createLeaderBoardResponse(leaderBoard));
            Iterator connectionIDs = Processor.getAllTargetConnectionIDs(contestRound);
            ResponseProcessor.process(connectionIDs, allResponses);
        }
    }

    /**
     * Call this function if you are an admin unregistering a user manually
     *
     * @param handle The user handle
     * @param roundId The round ID
     */
    public static CommandResponse unregisterCoderByHandle(String handle, int roundId) {
        Registration reg = null;
        boolean saved = false;
        try {
            reg = getRegistration(roundId, true);
            CommandResponse response = s_dbServices.unregisterCoderByHandle(handle, roundId);
            if (response instanceof CommandSucceededResponse) {
                reg.unregister(getUser(handle));
                saveToCache(reg.getCacheKey(), reg);
                saved = true;
            }
            return response;
        } catch (Exception e) {
            s_trace.error("", e);
            return new CommandFailedResponse("Received exception calling EJB:\n" + e);
        } finally {
            if (reg != null && !saved) {
                releaseLock(reg.getCacheKey());
            }
        }
    }

    /**
     * Call this function to register a user
     *
     * @param eventID currently is the roundID
     * @param surveyData this should contain an array list of SurveyAnswer objects
     */
    public static Results register(int userID, int eventID, ArrayList surveyData) {
        s_trace.debug("In register");
        try {
            Registration reg = getRegistration(eventID, true);

            if(reg.isContestFull()) {
                return new Results(false, "There are no more spots available for this event");
            }
            
            Results results = s_dbServices.registerCoder(userID, eventID, surveyData);
            if (results.isSuccess()) {
                // success, do something with the output
                reg.register(getUser(userID, false));
                saveToCache(reg.getCacheKey(), reg);
                s_trace.debug("Saved to cache supposedly");
            } else {
                // failed do something else with the output
                s_trace.error("Failed registering: " + results.getMsg());
            }
            return results;
        } catch (Exception e) {
            s_trace.error("Error registering user", e);
            return new Results(false, e.getMessage());
        } finally {
            releaseLock(Registration.getCacheKey(eventID));
        }
    }


    /**
     * Saves an object to the cache
     */
    public static void saveToCache(String key, Serializable o) {
        try {
            s_cache.set(key, o);
            s_cache.releaseLock(key);
        } catch (Exception e) {
            s_trace.error("Error on saving to cache", e);
        }
    }

    /**
     * Returns the coder info for a user
     */
    public static String getCoderInfo(String userName, int userType) {
        // we will crash and burn hard if this fails...but we should
        if (userType == ContestConstants.SINGLE_USER) {
            User user = getUser(userName);
            if (user != null) {
                return user.getCoderInfo();
            } else {
                return "Failed to get info for user: " + userName;
            }
        } else {
            Team team = getTeam(userName, false);
            if (team != null) {
                return team.getTeamInfo();
            } else {
                return "Failed to get info for team: " + userName;
            }
        }
    }

    public static String getUserStringFromCache(String handle) {
        Integer userID = handleToID(handle);
        if (userID == null) {
            return "no such user";
        }
        String userKey = User.getCacheKey(userID.intValue());
        User user = (User) safeCacheGet(userKey, false);
        if (user == null) {
            return "not in cache";
        }
        return user.toString();
    }

    /**
     * Gets a user using his handle.
     */
    public static User getUser(String handle) {
        return getUser(handle, false);
    }

    /**
     * Gets a user using his handle, either case insensitively or not.
     */
    public static User getUser(String handle, boolean ignoreCase) {
        //boolean lock=false;
        try {
            Integer id = handleToID(handle, ignoreCase);
            if (id != null) {
                if (s_trace.isDebugEnabled()) {
                    //return getUser(id.intValue(), lock);
                    s_trace.debug("ID IS NOT null: " + id.intValue());
                }                
                return getUser(id.intValue(), false);
            }
            if (s_trace.isDebugEnabled()) {
                s_trace.debug("YES ID IS null for: " + handle);
            }            
            User user = s_dbServices.getUser(handle, ignoreCase);
            if (user != null) {
                saveToCache(user.getCacheKey(), user);
                //if (lock)     safeCacheGet(user.getCacheKey(), lock);
                //s_handleToIDMap.put(handle.toLowerCase(), new Integer(user.getID()));
                s_handleToIDMap.put(handle.toLowerCase(), new Integer(user.getID()));
                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
            s_trace.error("getUser(handle, ignoreCase) failed for " + handle + "," + ignoreCase, e);
        }
        return null;
    }



    /**
     * Tells the connection archiver to log a new connection
     */
    public static void addConnection(String ip, int connID, int coderID, String userName) {
        connectionArchiver.add(ip, connID, coderID, userName);
    }

    /**
     * Tells the connection archiver to log out a connection
     */
    public static void removeConnection(int connId) {
        connectionArchiver.remove(connId);
    }

    public static void setCoderLanguage(int coderID, int languageID) {
        try {
            s_dbServices.setCoderLanguage(coderID, languageID);
        } catch (Exception e) {
            s_trace.error(e);
        }
    }


    public static void setUserStatus(String handle, boolean isActiveStatus) {
        try {
            s_dbServices.setUserStatus(handle, isActiveStatus);
        } catch (Exception e) {
            s_trace.error(e);
        }
    }

    public static void track(Tracking t) {
        try {
            if (s_trackingServices != null) {
                s_trackingServices.storeTracking(t);
            }
        } catch (Exception e) {
            s_trace.error("Error storing Tracking", e);
        }
    }

    public static int getServerID() {
        return serverID;
    }

    private static Round lobbies = getContestRound(LOBBY_ROUND_ID);

    public static int getFirstAvailableLobbyID() {
        Iterator rooms = lobbies.getAllRoomIDs();
        double ratio = 1;
        int index = 0;
        while (rooms.hasNext()) {
            Room r = getRoom(((Integer) (rooms.next())).intValue(), false);
            if (r.getCapacity() > 0 && r.getOccupancy() < LOBBY_ROLLOVER) return r.getRoomID();
            if (r.getCapacity() > 0 && ((double) r.getOccupancy()) / ((double) r.getCapacity()) < ratio) {
                ratio = ((double) r.getOccupancy()) / ((double) r.getCapacity());
                index = r.getRoomID();
            }
        }
        return index;
    }

    public static void addNewBroadcast(AdminBroadcast broadcast, int senderID) {
        int round_id = -1, component_id = -1;
        if (broadcast.getType() == ContestConstants.BROADCAST_TYPE_ADMIN_ROUND) {
            RoundBroadcast rb = (RoundBroadcast) broadcast;
            round_id = rb.getRoundID();
        } else if (broadcast.getType() == ContestConstants.BROADCAST_TYPE_ADMIN_COMPONENT) {
            ComponentBroadcast pb = (ComponentBroadcast) broadcast;
            round_id = pb.getRoundID();
            component_id = pb.getComponentID();
        }
        s_broadcastCache.add(broadcast);
        try {
            // status?
            s_dbServices.addNewBroadcast(broadcast.getTime(), broadcast.getMessage(), round_id, component_id, senderID,
                                         broadcast.getType(), 0);
        } catch (Exception e) {
            s_trace.error("in addNewBroadcast: ", e);
        }
    }

    /**
     * Gets all broadcasts for all round IDs contained in roundIDs set, and <3 hours old, from the cache.  
     * If roundIDs is empty, only generic broadcasts are returned.  
     * If roundIDs is null, all broadcasts <3 hours old are returned.
     */
    public static Collection getBroadcasts(Set roundIDs) {
        ArrayList broadcasts = new ArrayList();
        long minTime = getCurrentDBTime() - 1000L * 60L * 60L * 3L;
        try {
            synchronized (s_broadcastCache) {
                for (Iterator i = s_broadcastCache.iterator(); i.hasNext();) {
                    AdminBroadcast b = (AdminBroadcast) i.next();
                    if (b.getTime() < minTime) { // items are ordered in reverse time order; after one of these, all
                        i.remove();              // are older than 3 hours
                        int nItemsRemoved = 1;
                        while (i.hasNext()) {
                            i.next();
                            i.remove();
                            nItemsRemoved++;
                        }
                        if (s_trace.isDebugEnabled()) {
                            s_trace.debug("getBroadcasts: " + nItemsRemoved + " old broadcasts removed from cache.");
                        }                        
                    } else {
                        if (roundIDs == null || b.getType() == ContestConstants.BROADCAST_TYPE_ADMIN_GENERIC ||
                                roundIDs.contains(new Integer(((RoundBroadcast) b).getRoundID()))) {
                            broadcasts.add(b);
                        }
                    }
                }
            }
        } catch (ConcurrentModificationException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            s_trace.error("Broadcasts modified while in getBroadcasts: " + sw);
        }
        if (s_trace.isDebugEnabled()) {
            s_trace.debug("getBroadcasts: " + broadcasts.size() + " items returned from cache for round " + roundIDs);
        }        
        return broadcasts;
    }

    public static void refreshBroadcastCache(int roundID) {
        long minTime = getCurrentDBTime() - 1000L * 60L * 60L * 3L;
        Collection broadcastList;
        try {
            broadcastList = s_dbServices.getRecentBroadcasts(minTime, roundID);
        } catch (Exception e) {
            s_trace.error("in refreshBroadcastCache: ", e);
            return;
        }
        if (broadcastList == null) {
            s_trace.error("getRecentBroadcasts returned null, not updating cache!");
            return;
        }
        if (s_trace.isDebugEnabled()) {
            s_trace.debug("refreshBroadcastCache: " + broadcastList.size() + " items returned from DBServices.");
        }        
        // DBServices doesn't populate the extra fields, so we need to here.
        AdminBroadcastManager manager = AdminBroadcastManager.getInstance();
        for (Iterator i = broadcastList.iterator(); i.hasNext();) {
            AdminBroadcast broadcast = (AdminBroadcast) i.next();
            if (broadcast.getType() == ContestConstants.BROADCAST_TYPE_ADMIN_ROUND) {
                RoundBroadcast rb = (RoundBroadcast) broadcast;
                manager.populateRoundBroadcast(rb);
            } else if (broadcast.getType() == ContestConstants.BROADCAST_TYPE_ADMIN_COMPONENT) {
                ComponentBroadcast pb = (ComponentBroadcast) broadcast;
                manager.populateComponentBroadcast(pb, 1);
            }
        }
        s_broadcastCache = Collections.synchronizedSet(new TreeSet(broadcastList));
    }

    private static SimpleComponent getSimpleComponentFromCache(int componentID) {
        String key = SimpleComponent.getCacheKey(componentID);
        return (SimpleComponent) safeCacheGet(key, false);
    }

    /**
     * Retreives the specified component.
     */
    public static SimpleComponent getSimpleComponent(int componentID) {
        try {
            SimpleComponent component = getSimpleComponentFromCache(componentID);
            if (component == null) {
                component = s_problemServices.getSimpleComponent(componentID);

                if (s_trace.isDebugEnabled()) {
                    s_trace.debug("Loaded component " + component);
                }

                // save it back to the cache always if he doesn't want to write
                saveToCache(component.getCacheKey(), component);
            }
            return component;
        } catch (Exception e) {
            s_trace.error("Exception in getComponent(" + componentID + ")", e);
        }
        return null;
    }

    public static String getLobbyStatus() {
        return m_lobbyStatus;
    }

    public static WeakestLinkData loadWeakestLinkData(int roundId) throws RemoteException, DBServicesException {
        return s_dbServices.loadWeakestLinkData(roundId);
    }

    // Returns null if team was not found
    public static WeakestLinkTeam getWeakestLinkTeam(int teamId) throws RemoteException, DBServicesException {
        try {
            WeakestLinkRound wlr = getActiveWeakestLinkRound();
            if (wlr == null) {
                s_trace.error("No weakest link round active; could not identify round for team " + teamId);
                return null;
            }
            int roundId = wlr.getRoundID();
            return getWeakestLinkTeam(teamId, roundId);
        } catch (RuntimeException e) {
            s_trace.error("Multiple weakest link rounds are active; could not uniquely identify round for team " + teamId);
            return null;
        }
    }

    public static WeakestLinkTeam getWeakestLinkTeam(int teamId, int roundId) throws RemoteException, DBServicesException {
        return s_dbServices.getWeakestLinkTeam(teamId, roundId);
    }

    public static void storeWeakestLinkData(WeakestLinkData weakestLinkData, int targetRoundId)
        throws RemoteException, DBServicesException {
        s_dbServices.storeWeakestLinkData(weakestLinkData, targetRoundId);
    }

    public static WeakestLinkRound getActiveWeakestLinkRound() {
        WeakestLinkRound result = null;
        Round[] allActiveRounds = getAllActiveRounds();
        for (int i = 0; i < allActiveRounds.length; i++) {
            Round activeRound = allActiveRounds[i];
            if (activeRound instanceof WeakestLinkRound) {
                if (result != null) {
                    throw new RuntimeException("more than one active WL rounds");
                }
                result = (WeakestLinkRound) activeRound;
            }
        }
        return result;
    }
    /**
     * Gets the user image info.
     * @param coderId the coder id.
     * @return the user image info entity.
     */
    public static String getMemberPhotoPath(int coderId) {
        try {
            return s_dbServices.getMemberPhotoPath(coderId);
        } catch (Exception e) {
            s_trace.error("Could not retrieve the user image info with coder id " + coderId);
            return "";
        }
    }
    public static void storeBadgeId(int roundId, int coderId, String badgeId) throws DBServicesException, RemoteException {
        s_dbServices.storeBadgeId(roundId, coderId, badgeId);
    }

    private static void info(Object message) {
        s_trace.info(message);
    }

    public static RegistrationResult registerUser(String userId, String password, String firstName, String lastName,
                                                  String email, String phoneNumber) {
        try {
            return s_dbServices.registerUser(userId, password, firstName, lastName, email, phoneNumber);
        } catch (Exception e) {
            throw new RuntimeException("" + e);
        }
    }

    public static Round[] getActiveRegLongRounds() {
        Round[] activeRounds = getAllActiveRounds();
        List activeLongRoundList = new ArrayList();
        for (int i = 0; i < activeRounds.length; i++) {
            Round activeRound = activeRounds[i];
            if (activeRound.isLongRound() && activeRound.inCoding()) {
                activeLongRoundList.add(activeRound);
            }
        }
        Round[] activeLongRounds = new Round[activeLongRoundList.size()];
        for (int i = 0; i < activeLongRounds.length; i++) {
            activeLongRounds[i] = (Round) activeLongRoundList.get(i);
        }
        return activeLongRounds;
    }

    public static boolean hasCodingTimeExpiredForCoder(Round round, Coder coder) {
        return round.getRoundProperties().usesPerUserCodingTime() && 
               (System.currentTimeMillis() - coder.getEarliestComponentOpenTime()) >= round.getRoundProperties().getPerUserCodingTime().longValue();
    }
    
    public static void processSystestResultsRequest(Integer connectionID, User user,  Round round) {
        try {
            ContestRoom room = (ContestRoom) CoreServices.getRoom(round.getAssignedRoom(user.getID()).intValue());
            Coder c = room.getCoder(user.getID());

            long[] componentIDs = c.getComponentIDs();
            if (!hasCodingTimeExpiredForCoder(round, c)) {
                for (int i = 0; i < componentIDs.length; i++) {
                    int componentID = (int)componentIDs[i];
                    if (!c.isComponentOpened(componentID) || !c.getComponent(componentID).isSubmitted()) {
                        Processor.error(connectionID, "Please submit all problems first.");
                        return;
                    }
                }
            }

            StringBuilder retval = new StringBuilder(200);
            retval.append("System test results:\n");
            double pnts = 0;
            ComponentNameBuilder nameBuilder = round.getRoundType().getComponentNameBuilder();
            for (int i = 0; i < componentIDs.length; i++) {
                int componentID = (int)componentIDs[i];
                if (c.isComponentOpened(componentID) && c.getComponent(componentID).isSubmitted()) {
                    RoundComponent cp = CoreServices.getRoundComponent(c.getRoundID(), componentID, c.getDivisionID());
                    String componentName = nameBuilder.longNameForComponent(cp.getComponent().getClassName(), cp.getPointVal(), round.getRoundProperties());
                    int status = s_dbServices.getComponentSystemTestStatus(c.getID(), componentID, round.getRoundID());
                    if (status == DBServices.SYSTEM_TESTS_PENDING) {
                        retval.append("System tests are being executed on the ").append(componentName).append(" \n");
                    } else if (status == DBServices.SYSTEM_TESTS_PASSED) {
                        retval.append("Passed all system tests on the ").append(componentName).append(" \n");
                        //get point value
                        pnts += (double) c.getComponent(componentID).getSubmittedValue() / 100.0;
                    } else {
                        //if failed, lookup failure case
                        retval.append("Failed system tests on the ").append(componentName).append(" with args: ");
                        retval.append(s_dbServices.getFailureMessage(c.getID(), componentID, round.getRoundID()));
                    }
                }
            }
            retval.append("Final Score: " + Formatters.getDoubleString(pnts));
            retval.append("\n\nTo view these results again, select \"System Test Results\" on the summary screen");

            //build results
            Processor.simpleBigMessage(connectionID, retval.toString());
        } catch (Exception e) {
            s_trace.error(e);
            throw new RuntimeException(e);
        }
    }

    public static List getUserImportantMessages(int user_id) {
        try {
            List result = s_dbServices.getUserImportantMessages(user_id);
            return result;
        } catch(Exception e) {
            s_trace.error(e);
            return new ArrayList();
        }
    }

    public static void readMessage(int userID, int messageID) {
        try {
            s_dbServices.readMessage(userID, messageID);
        } catch(Exception e) {
            s_trace.error(e);
        }
    }

    public static ImportantMessageData[] getMessages(int userID) {
        try {
            return s_dbServices.getMessages(userID);
        } catch(Exception e) {
            s_trace.error(e);
            return new ImportantMessageData[0];
        }
    }

    public static List getRoundIDsToLoadOnStartUp() {
        long autoLoadTime = getWaitTimeToUnloadAfterCoding();
        try {
            return s_dbServices.getRoundIDsToLoadOnStartUp(System.currentTimeMillis()-autoLoadTime);
        } catch (Exception e) {
            s_trace.error("Could not find rounds to load on startup", e);
            return Collections.EMPTY_LIST;
        }
    }

    private static long getWaitTimeToUnloadAfterCoding() {
        long autoLoadTime = s_coreSettings.getLong("autoLoadCodingTimeEndSecs")*1000;
        return autoLoadTime;
    }

    /**
     * <p>
     *     Track user actions in web arena.
     * </p>
     * @param userHandle
     *          the user handle
     * @param message
     *          the actual request
     * @param client
     *          the client used. Currently only 'web arena'
     * @since 1.4
     */
    public static void recordUserAction(String userHandle, Object message, String client) {
        String actionName = message.getClass().getSimpleName();
        actionName = actionName.replace("Request", "");
        try {
            if (null != userHandle && userHandle.trim().length() > 0) {
                s_dbServices.recordUserAction(userHandle, actionName, client, new java.util.Date());
            }
        } catch (Exception e) {
            s_trace.error("Error occurs while tracking user actions:", e);
        }
    }

    /**
     * <p>
     *     Get user handle from sso.
     * </p>
     * @param sso
     *          the sso
     * @return
     *          user handle. null if sso is invalid
     * @since 1.4
     */
    public static String getHandleBySSO(String sso) {
        String userHandle = "";
        try {
            String[] userInfo = s_dbServices.validateSSOToken(sso);
            if (userInfo.length > 0) {
                userHandle = userInfo[0];
            }
        } catch (Exception e) {
            s_trace.error("sso is invalid.");
        }
        return userHandle;
    }
}
