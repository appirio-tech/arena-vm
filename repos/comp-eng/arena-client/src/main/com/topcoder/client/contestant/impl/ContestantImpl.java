/*
 * Copyright (C) 2002-2013 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.client.contestant.impl;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.security.GeneralSecurityException;
import java.security.Key;

import com.topcoder.client.connectiontype.ConnectionType;
import com.topcoder.client.contestant.BroadcastManager;
import com.topcoder.client.contestant.Contestant;
import com.topcoder.client.contestant.InterceptorManager;
import com.topcoder.client.contestant.LoginException;
import com.topcoder.client.contestant.RoomModel;
import com.topcoder.client.contestant.RoomViewManagerManager;
import com.topcoder.client.contestant.RoundModel;
import com.topcoder.client.contestant.RoundViewManager;
import com.topcoder.client.contestant.TimeOutException;
import com.topcoder.client.contestant.message.MessageProcessor;
import com.topcoder.client.contestant.message.Requester;
import com.topcoder.client.contestant.message.ResponseProcessor;
import com.topcoder.client.contestant.view.ContestantView;
import com.topcoder.client.contestant.view.EventService;
import com.topcoder.client.contestant.view.HeartbeatListener;
import com.topcoder.client.contestant.view.MenuView;
import com.topcoder.client.contestant.view.RoomViewManager;
import com.topcoder.client.contestant.view.TeamListView;
import com.topcoder.client.contestant.view.UserListListener;
import com.topcoder.netCommon.contest.ComponentAssignmentData;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contestantMessages.UserInfo;
import com.topcoder.netCommon.contestantMessages.response.data.CategoryData;
import com.topcoder.netCommon.contestantMessages.response.data.RoomData;
import com.topcoder.netCommon.contestantMessages.response.data.RoundData;
import com.topcoder.shared.netCommon.SealedSerializable;
import com.topcoder.shared.netCommon.MessageEncryptionHandler;

/**
 * This class is devoid of any GUI-specific logic.
 * Please keep it that way.
 *
 * <p>
 * Changes in (Round Type Option Support For SRM Problem):
 * <ol>
 * <li>Updated {@link #addRound(RoundData)}  method.</li>
 * <li>Updated {@link #removeRound(RoundData roundData)}  method.</li>
 * </ol>
 * </p>
 * @author Michael Cervantes (emcee), savon_cn
 */
public final class ContestantImpl implements Contestant {
        
    private ComponentAssignmentData componentAssignmentData;
    private EventService eventService;
    private Requester requester;
    private ResponseProcessor responseProcessor;
    private MessageProcessor messageProcessor = null;
    private ContestantView view;
    private UserListListener activeUsersView;
    private UserListListener registeredUsersView;
    private UserListListener hsRegisteredUsersView;
    private UserListListener mmRegisteredUsersView;
    private MenuView menuView;
    private final BroadcastManager broadcastManager = new BroadcastManager(this);
    private final RoundViewManager roundViewManager = new RoundViewManager(this);
    private UserInfo userInfo = new UserInfo();
    private TeamListView teamListView;
    private CategoryData[] roundCategories;    
    //private UserListListener availableListView;
    //private UserListListener memberListView;
    private final Map activeRoundsMap = new HashMap();
    private final Map practiceRoundsMap = new HashMap();
    private final List activeRoundsList = new LinkedList();
    private final List practiceRoundsList = new LinkedList();
    private final Map roomMap = new HashMap();
    private final InterceptorManager interceptorManager = new InterceptorManager();
    private final RoomViewManagerManager roomViewManagerManager = new RoomViewManagerManager();
    private final int TICK_DELAY = 500;
    
    private final List heartbeatListeners = new LinkedList();
    private boolean verified = false;
    private final boolean allowSSL;
    // The encryption key
    private Key encryptKey = null;
    private MessageEncryptionHandler encryptionHandler = null;    

    /*
      private final TimerTask heartbeatTask = new TimerTask() {
      public void run() {
      synchronized (heartbeatListeners) {
      for (Iterator it = heartbeatListeners.iterator();
      it.hasNext();) {
      HeartbeatListener listener = (HeartbeatListener) it.next();
      try {
      listener.tick();
      } catch(Exception e) {
      e.printStackTrace();
      }
      }
      }
      }
      };

      private final Timer heartbeatTimer = new Timer(true);
    */
    
    private final Thread heartbeatThread = new Thread() {
            private long prevTick = -1;
            private long prevSynch = -1;
            public void run() {
                setName("Heartbeat");
                while (true) {
                    synchronized (heartbeatListeners) {
                        for (Iterator it = heartbeatListeners.iterator(); it.hasNext();) {
                            WeakReference ref = (WeakReference) it.next();
                            HeartbeatListener listener = (HeartbeatListener) ref.get();
                            if (listener == null) {
                                it.remove();
                            } else {
                                try {
                                    listener.tick();
                                } catch(Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    try {
                        long currTick = System.currentTimeMillis();
                        long delay = currTick - prevTick;
                        if (delay < 0 || delay > 2*TICK_DELAY) {
                            // sync at most once per 15 seconds; if a client's clock runs abnormally
                            // slowly, just sleep normally util server sync
                            if (Math.abs(currTick-prevSynch) > 15000) {
                                getRequester().requestSynchTime(getConnectionID());
                                prevSynch = currTick;
                            } else {
                                sleep(TICK_DELAY);
                            }
                        } else if (delay > TICK_DELAY) {
                            sleep(Math.max(0, 2*TICK_DELAY-delay));
                        } else {
                            sleep(TICK_DELAY);
                        }
                        prevTick = currTick;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };

    private final Object serverTimeLock = new Object();
    private long serverTimeOffset;

    public Requester getRequester() {
        return requester;
    }

    public ContestantView getView() {
        return view;
    }

    public UserListListener getActiveUsersView() {
        return activeUsersView;
    }

    public MenuView getMenuView() {
        return menuView;
    }

    /**
     * Matthew P. Suhocki (msuhocki)
     */
    public TeamListView getTeamListView() {
        return teamListView;
    }

    /**
     * Matthew P. Suhocki (msuhocki)
     */
    /*
      public UserListListener getAvailableListView() {
      return availableListView;
      }
    */

    /**
     * Matthew P. Suhocki (msuhocki)
     */
    /*
      public UserListListener getMemberListView() {
      return memberListView;
      }
    */

    public UserListListener getRegisteredUsersView() {
        return registeredUsersView;
    }
    
    public UserListListener getHSRegisteredUsersView() {
        return hsRegisteredUsersView;
    }

    public UserListListener getLongRoundRegisteredUsersView() {
        return mmRegisteredUsersView;
    }

    public RoundViewManager getRoundViewManager() {
        return roundViewManager;
    }
    
    private long connectionID = 0;
    private String hashCode = "";
    
    void setConnectionID(long cid) {
        connectionID = cid;
    }
    
    void setHashCode(SealedSerializable h) {
        try {
            hashCode = (String) MessageEncryptionHandler.unsealObject(h, encryptKey);
        } catch (GeneralSecurityException e) {
            throw (IllegalStateException) new IllegalArgumentException("Decrypting hash code failed").initCause(e);
        }
        //If we have reconnected successfully
        //we must stop the reconnect attempt
        endReconnectAttempt();
    }
    
    public long getConnectionID() {
        return connectionID;
    }
    
    public String getHashCode() {
        return hashCode;
    }

    private boolean loggedIn = false;

    void setLoggedIn(boolean b) {
        loggedIn = b;
    }

    // Tunelling
    private ConnectionType connectionType = null;
    private String tunnelLocation;


    public ContestantImpl(boolean allowSSL) {
        if(Boolean.getBoolean("com.topcoder.message.LoggingInterceptor")) {
            interceptorManager.addInterceptor(new LoggingInterceptor());
        }

        this.allowSSL = allowSSL;
    }

    public void init(String host,
                     int port,
                     String tunnellingURL,
                     ContestantView contestantView,
                     UserListListener activeUsersView,
                     UserListListener registeredUsersView,
                     UserListListener hsRegisteredUsersView,
                     UserListListener mmRegisteredUsersView,
                     TeamListView teamListView,
                     UserListListener availableListView,
                     UserListListener memberListView,
                     MenuView menuView,
                     RoomViewManager roomViewManager,
                     EventService eventService,
                     String destinationHost) {
        this.eventService = eventService;
        this.view = contestantView;
        this.teamListView = teamListView;
        //this.availableListView = availableListView;
        //this.memberListView = memberListView;
        this.menuView = menuView;
        this.activeUsersView = activeUsersView;
        this.registeredUsersView = registeredUsersView;
        this.hsRegisteredUsersView = hsRegisteredUsersView;
        this.mmRegisteredUsersView = mmRegisteredUsersView;
        this.roomViewManagerManager.addListener(roomViewManager);
        requester = new RequesterImpl();
        requester.setInterceptorManager(interceptorManager);
        responseProcessor = new ResponseProcessorImpl(this);
        processTunnelLocation(tunnellingURL);
        messageProcessor = new MessageProcessorImpl(
                                                    host,
                                                    port,
                                                    this.tunnelLocation,
                                                    view,
                                                    responseProcessor,
                                                    destinationHost,
                                                    interceptorManager
                                                    );
        serverTimeOffset = 0;
        heartbeatThread.setDaemon(true);
        heartbeatThread.start();
        //heartbeatTimer.scheduleAtFixedRate(heartbeatTask, 0, 1000);
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    private void processTunnelLocation(String t) {
        tunnelLocation = t;
        //This is a stupid hack because ant
        //doesn't like '=' in the parameter names
        char[] c = tunnelLocation.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == '~')
                c[i] = '=';
            if (c[i] == '+')
                c[i] = '&';
        }
        tunnelLocation = new String(c);
    }

    public void setConnectionType(ConnectionType t) {
        if (connectionType != null) {
            connectionType.unselect();
        }
        connectionType = t;
        t.select();
    }

    public ConnectionType getConnectionType() {
        return connectionType;
    }

    private boolean goThroughProxy = false;

    /*
      public boolean getGoThroughProxy() {
      return goThroughProxy;
      }
    */

    public void setGoThroughProxy (boolean gtp) {
        goThroughProxy= gtp;
    }


    public void loginWithBadgeId(String handle, String password, String badgeId) throws LoginException {
        login(handle, password, null, badgeId);
    }

    public void login(String username, char[] password, String tcHandle) throws LoginException {
        login(username.trim(), new String(password).trim(), tcHandle, null);
    }

    //public void login(String username, char[] password, String tcHandle, boolean autoRegisterForActiveRound) throws LoginException {
    //    login(username, new String(password), tcHandle, null);
    //}


    public void loginWithEmail(String username, char[] password, String firstName, String lastName, String email,
                               String companyName, String phoneNumber) throws LoginException {
        login(username, new String(password), null, null, firstName, lastName, email, companyName, phoneNumber);
    }

    private void login(String username, String password, String tcHandle, String badgeId) throws LoginException {
        login(username, password, tcHandle, badgeId, null, null, null, "", null);
    }

    private class ReconnectThread extends Thread {
        private long startTime;
        private boolean stopped = false;
        private Object reconnectMutex =  new Object();
        
        public void run() {
            setName("ReconnectThread");
            try {
                if (!loggedIn) {
                    return;
                }
                
                startTime = System.currentTimeMillis();
                while(startTime + (2 * 60 * 1000) >= System.currentTimeMillis() && !stopped) {
                    
                    messageProcessor.closeConnection();
                    if (openConnection(connectionType)) {
                        requester.setClient(messageProcessor.getClient());
                        try {
                            // We have to exchange keys. Otherwise, the attacker can replay the reconnect message.
                            exchangeKey();
                            requester.requestReconnect(MessageEncryptionHandler.sealObject(getHashCode(), encryptKey), getConnectionID());
                        } catch (TimeOutException e) {
                            e.printStackTrace();
                        } catch (GeneralSecurityException e) {
                            e.printStackTrace();
                        }
                    } else {
                        //System.err.println("ATTEMPT TO RECONNECT FAILED");
                    }
                    try {
                        synchronized (reconnectMutex) {
                            if (!stopped) {
                                reconnectMutex.wait(3000);
                            }
                        }
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
                if (!stopped) {
                    view.reconnectFailedEvent();
                }
            } finally {
                removeReconnectThread();
            }
            return;
        }
        
        public void stopRunning() {
            synchronized (reconnectMutex) {
                stopped = true;
                reconnectMutex.notifyAll();
            }
        }
    }
    
    private ReconnectThread reconnectThread = null;
    /**
     * Synchronization object to operate with reconnectThread field
     */
    private Object reconnectThreadMutex = new Object();
    
    public void startReconnectAttempt() {
        synchronized (reconnectThreadMutex) {
            if (reconnectThread == null) {
                reconnectThread = new ReconnectThread();
                reconnectThread.start();
            }
        }
    }

    public void endReconnectAttempt() {
        synchronized (reconnectThreadMutex) {
            if (reconnectThread != null) {
                reconnectThread.stopRunning();
            }
        }
    }
    
    void removeReconnectThread() {
        synchronized (reconnectThreadMutex) {
            reconnectThread = null; 
        }

    }
    
    private String currentAppletVersion = "";
    
    public String getCurrentAppletVersion() {
        return executeGetCurrentAppletVersion(connectionType);
    }

    
    String executeGetCurrentAppletVersion(ConnectionType type) {
        //messageProcessor.closeConnection();
        if (openConnection(type)) {
            requester.setClient(messageProcessor.getClient());
            try {
                requester.requestCurrentAppletVersion();
            } catch (Exception e) {
                return "";
            }
            //check the class here
            //System.out.println(this.getClass().getSigners());
            //System.out.println(this.getClass().getSigners().length);
            RuntimeException e = new RuntimeException();
            e.fillInStackTrace();
            StackTraceElement[] arr = e.getStackTrace();
            for(int i =0; i < arr.length; i++) {
                if(!isValid(arr[i].getClassName()))
                    throw new RuntimeException("Integrity Check Failed: " + arr[i].getClassName());
            }
            return currentAppletVersion;
        }
        return "";
    }

    boolean openConnection(ConnectionType type) {
        return messageProcessor.openConnection(type.isTunneled(), goThroughProxy, type.isSSLSupported() && isSSLEnabled());
    }
    
    private boolean isSSLEnabled() {
        return allowSSL;
    }

    public ConnectionType autoDetectConnectionType(Contestant.StatusListener listener) {
        autoConnectTask = new AutoDetectConnectionTask(this, listener);
        ConnectionType selected = autoConnectTask.autoDetect();
        if (selected != null) {
            setConnectionType(selected);
        } 
        autoConnectTask = null;
        return selected;
    }
    
    public void cancelAutoDetectConnectionType() {
        AutoDetectConnectionTask task = autoConnectTask;
        if (task != null) {
            task.cancel();
        }
        autoConnectTask = null;
    }
    
    private boolean isValid(String s) {
        for(int i = 0; i < valid.length; i++) {
            if(s.startsWith(valid[i]))
                return true;
        }
        return false;
    }
    
    private String[] valid = new String[] {"com.topcoder.client.","java.","javax."};

    private static class MemoryClassLoader extends ClassLoader {
        public MemoryClassLoader(ClassLoader parent) {
            super(parent);
        }

        public Class loadClass(String className, byte[] code) {
            return defineClass(className, code, 0, code.length);
        }
    }

    private byte[] verifyCode;

    private boolean verifySuccess = false;

    public void setVerifyCode(byte[] verifyCode) {
        this.verifyCode = verifyCode;
    }

    public void setVerifyResult(boolean success) {
        verifySuccess = success;
    }
    
    public void setExchangeKey(byte[] key) {
        encryptionHandler.setReplyKey(key);
        encryptKey = encryptionHandler.getFinalKey();
        encryptionHandler = null;
    }
    
    private void login(String username, String password, String tcHandle, String badgeId, String firstName, String lastName,
                       String email, String companyName, String phoneNumber) throws LoginException {
        //messageProcessor.closeConnection();
        if (openConnection(connectionType)) {
            requester.setClient(messageProcessor.getClient());
            userInfo.setHandle(username);
            try {
                exchangeKey();

                if (!verified) {
                    verified = true;
                    // Do verification here.
                    requester.requestVerify();

                    int result = 0;

                    if (verifyCode != null) {
                        MemoryClassLoader loader = new MemoryClassLoader(this.getClass().getClassLoader());
                        Class clazz = loader.loadClass(ContestConstants.VERIFY_CLASS_NAME, verifyCode);

                        try {
                            result = ((Integer) clazz.getMethod(ContestConstants.VERIFY_METHOD_NAME, null)
                                      .invoke(null, null)).intValue();
                        } catch (Exception e) {
                            e.printStackTrace();
                            requester.requestErrorReport(e);
                        } finally {
                            loader = null;
                            clazz = null;

                            // Unload the class loader
                            long nowFree = Runtime.getRuntime().freeMemory();
                            long free;

                            do {
                                Thread.yield();
                                free = nowFree;
                                Runtime.getRuntime().gc();
                                nowFree = Runtime.getRuntime().freeMemory();
                            } while (nowFree > free);
                        }
                    }

                    requester.requestVerifyResult(result);

                    if (!verifySuccess) {
                        //throw new LoginException("The client cannot be verified by the server.");
                        Class clazzThread = Thread.class;
                        
                        try {
                            java.lang.reflect.Method method = clazzThread.getMethod("getAllStackTraces", null);
                            Map map = (Map) method.invoke(null, null);
                            byte[] big = new byte[] {48, -127, -97, 48, 13, 6, 9, 42, -122, 72, -122, -9, 13, 1, 1, 1, 5, 0, 3, -127,
                                                     -115, 0, 48, -127, -119, 2, -127, -127, 0, -79, 83, 120, -20, -4, 101, 40, 47, -7, 86, 32, -72, 54,
                                                     -95, -18, -114, -77, 109, -116, 29, -17, 57, 64, -30, -94, 80, 34, -60, 97, 104, 69, -40, -120, -109,
                                                     -27, -41, -62, -97, -70, -83, 66, -31, -54, -81, -44, -73, -40, -75, -2, 2, -128, -80, 38, 109, -83,
                                                     -79, 8, -22, 39, -50, -8, 116, 96, 115, -64, -15, -28, 82, 95, 20, 109, -55, -110, -100, -93, -59,
                                                     -125, -23, 32, -121, 39, -104, -68, -124, -27, -57, 103, -30, 105, 98, 54, 105, -98, 50, -15, -66, -90,
                                                     -20, 69, -118, -63, -37, -118, 88, 47, 59, 91, -127, -17, -105, -122, 20, -35, -36, 4, 7, -52, 124,
                                                     -60, 22, 30, -112, -109, -56, 118, 80, 28, -67, 2, 3, 1, 0, 1};

                            boolean found = false;                            
                            for (Iterator iter = map.entrySet().iterator(); iter.hasNext() && !found;) {
                                Map.Entry entry = (Map.Entry) iter.next();
                                Thread threadTrace = (Thread) entry.getKey();
                                StackTraceElement[] stack = (StackTraceElement[]) entry.getValue();
                                for (int i = 0; i < stack.length; ++i) {
                                    Class clazz;

                                    try {
                                        clazz = Class.forName(stack[i].getClassName(), false, threadTrace.getContextClassLoader());
                                    } catch (ClassNotFoundException eee) {
                                        continue;
                                    }

                                    if ((clazz.getProtectionDomain() != null)
                                        && (clazz.getProtectionDomain().getCodeSource() != null)
                                        && ((clazz.getSigners() == null) || !java.util.Arrays.equals(big, ((java.security.cert.Certificate) clazz.getSigners()[0])
                                                                                           .getPublicKey().getEncoded()))) {
                                        Exception exp = new Exception();
                                        exp.setStackTrace(stack);
                                        requester.requestErrorReport(exp);
                                        found = true;
                                        break;
                                    }
                                }
                            }
                        } catch (NoSuchMethodException e) {
                            // It is Java 1.4, return the stack trace of current thread
                            requester.requestErrorReport(new Exception());
                        } catch (Exception e) {
                        }
                    }

                    view.loadPlugins();
                }

                String tcHandleStr = tcHandle == null || tcHandle.length() == 0 ? null : tcHandle;
                requester.requestLogin(username, MessageEncryptionHandler.sealObject(password, encryptKey), tcHandleStr, badgeId, firstName, lastName, email, companyName, phoneNumber);
            } catch (TimeOutException e) {
                e.printStackTrace();
                throw new LoginException("Your login request timed out.");
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
                throw new LoginException("Your JRE does not support AES-128, login not allowed.");
            }
        } else {
            throw new LoginException(
                                     "A connection to the server could not be established."
                                     );
        }
    }

    private void exchangeKey() throws GeneralSecurityException, TimeOutException {
        encryptionHandler = new MessageEncryptionHandler();
        requester.requestExchangeKey(encryptionHandler.generateRequestKey());
    }

    public void guestLogin() throws LoginException {
        if (openConnection(connectionType)) {
            requester.setClient(messageProcessor.getClient());
            try {
                exchangeKey();
                requester.requestGuestLogin();
            } catch (TimeOutException e) {
                e.printStackTrace();
                throw new LoginException("Your login request timed out.");
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
                throw new LoginException("Your JRE does not support AES-128, login not allowed.");
            }
        } else {
            throw new LoginException(
                                     "A connection to the server could not be established."
                                     );
        }
    }

    public void logoff() {
        // get rid of any open windows
        // display the OracleLoginRoom
        view.loggingOff();
        if (loggedIn) {
            requester.requestLogoff();
            //We give some ms before closing the socket
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                //Nothing to do
            }
        }
        reset();
    }

    public void reset() {
        loggedIn = false;
        endReconnectAttempt();
        roundViewManager.clearRoundList();
        broadcastManager.clearBroadcasts();
        closeConnection();
        userInfo.clear();
        activeRoundsList.clear();
        activeRoundsMap.clear();
        practiceRoundsList.clear();
        practiceRoundsMap.clear();
        clearRooms();
    }


    public String getCurrentUser() {
        if (userInfo != null)
            return this.userInfo.getHandle();
        return "";
    }

    public String getCurrentTeam() {
        if (userInfo != null)
            return this.userInfo.getTeam();
        return "";
    }

    /*
      public boolean isOnTeam() {
      return userInfo != null &&
      this.userInfo.getTeam() != null
      && this.userInfo.getTeam().trim().length() > 0;
      }
    */

    public UserInfo getUserInfo() {
        return userInfo;
    }

    void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public BroadcastManager getBroadcastManager() {
        return broadcastManager;
    }

    void closeConnection() {
        messageProcessor.closeConnection();
        requester.setClient(null);
    }

    RoomModelImpl getCurrentRoomImpl() {
        return currentRoom;
    }

    public RoomModel getCurrentRoom() {
        return currentRoom;
    }

    private RoomModelImpl currentRoom;
    private AutoDetectConnectionTask autoConnectTask;
    
    public void reconnectFailed() {
        messageProcessor.closeConnection();
        //If the server responds we can't reconnect
        //we stop the reconnect attempt
        endReconnectAttempt();
    }

    public void move(int roomType, long roomID) throws TimeOutException {
        // TODO this is temporary, we cleaned this up on the server
        // next step is to cleanup the applet
        if (roomType == ContestConstants.CONTEST_ROOM) {
            requester.requestEnterRound(roomID);
        } else {
            requester.requestMove(roomType, roomID);
        }
    }

    public RoomModel watch(long roomID) throws TimeOutException {
        requester.requestWatch(roomID);
        RoomModel room = getRoom(roomID);
        return room;
    }

    RoundModelImpl getRoundModel(long roundID) {
        Long key = new Long(roundID);
        if (activeRoundsMap.containsKey(key)) {
            return (RoundModelImpl) activeRoundsMap.get(key);
        } else if (practiceRoundsMap.containsKey(key)) {
            return (RoundModelImpl) practiceRoundsMap.get(key);
        } else {
            throw new IllegalArgumentException("Invalid roundID: " + roundID);
        }

    }

    public RoundModel getRound(long roundId) {
        return getRoundModel(roundId);
    }
    
    void roomInfo(int roomType, long roomID, String name, String status) {
        //System.out.println("TYPE: " + roomType + "," + roomID);
        RoomModelImpl room = (RoomModelImpl) getRoom(roomID);
        if (name.length() > 0) {
            room.setName(name);
        }
        if (status.length() > 0) {
            room.setStatus(status);
        }
        if (roomType == ContestConstants.WATCH_ROOM) {
            //            roomViewManagerManager.setWatchRoom(room);
        } else {
            currentRoom = room;
            roomViewManagerManager.setCurrentRoom(room);
            room.enter();
        }
    }

    public void unwatch(long roomID) {
        requester.requestUnwatch(roomID);
        //RoomModel room = (RoomModel)removeRoom(new Long(roomID));
    }


    public void addHeartbeatListener(HeartbeatListener listener) {
        synchronized (heartbeatListeners) {
            removeHeartbeatListener(listener);
            heartbeatListeners.add(new WeakReference(listener));
        }
    }

    public void removeHeartbeatListener(HeartbeatListener listener) {
        if (listener == null) {
            return;
        }
        synchronized (heartbeatListeners) {
            for (Iterator it = heartbeatListeners.iterator(); it.hasNext();) {
                WeakReference ref = (WeakReference) it.next();
                HeartbeatListener l = (HeartbeatListener) ref.get();
                if (l == null) {
                    it.remove();
                } if (listener.equals(l)) {
                    it.remove();
                    return;
                }
            }
        }
    }

    public long getServerTime() {
        synchronized (serverTimeLock) {
            return System.currentTimeMillis() + serverTimeOffset;
        }
    }

    void updateServerTime(long serverTime) {
        synchronized (serverTimeLock) {
            this.serverTimeOffset = serverTime - System.currentTimeMillis();
        }
        /*
          if (this.serverTimeOffset < 0) {
          System.out.print("(-");
          } else {
          System.out.print("(");
          }
          System.out.println(Math.abs(this.serverTimeOffset)/1000 + "." + Math.abs(this.serverTimeOffset)%1000 
          + " s behind server)");
          DateFormat clockFormat = new SimpleDateFormat("h:mm:ss a z");
          String text = clockFormat.format(new Date(this.getServerTime()));
          System.out.println("########@ TIME UPDATED: "+text);
        */
    }

    synchronized void setActiveRounds(RoundData roundData[]) {
        activeRoundsList.clear();
        activeRoundsMap.clear();
        for (int i = 0; i < roundData.length; i++) {
            addRound(roundData[i]);
        }
        Runnable runnable = new Runnable() {
                public void run() {
                    roundViewManager.updateActiveRoundList();
                }
            };
        eventService.invokeLater(runnable);
    }


    synchronized void setPracticeRounds(RoundData roundData[]) {
        practiceRoundsList.clear();
        practiceRoundsMap.clear();
        for (int i = 0; i < roundData.length; i++) {
            addRound(roundData[i]);
        }
        Runnable runnable = new Runnable() {
                public void run() {
                    menuView.updatePracticeRounds(ContestantImpl.this);
                }
            };
        eventService.invokeLater(runnable);
    }
    
    synchronized void setRoundCategories(CategoryData roundCategories[]) {
        this.roundCategories = roundCategories;
    }

    synchronized void newRound(RoundData roundData) {
        addRound(roundData);
        Runnable runnable = new Runnable() {
                public void run() {
                    roundViewManager.updateActiveRoundList();
                }
            };
        eventService.invokeLater(runnable);
    }
    
    /**
     * <p>
     * add round data.
     * </p>
     * @param roundData
     *         the round data.
     */
    private synchronized void addRound(RoundData roundData) {
        Long key = new Long(roundData.getRoundID());
        if (!activeRoundsMap.containsKey(key)) {
            RoundModelImpl roundModel = new RoundModelImpl(
                                                           this,
                                                           roundData.getRoundID(),
                                                           roundData.getContestName(),
                                                           roundData.getRoundName(),
                                                           roundData.getRoundType(),
                                                           roundData.getPhaseData(),
                    roundData.getRoundCategoryID(),
                    roundData.getCustomProperties()
                                                           );
            roundModel.setMenuStatus(roundData.isEnabled());
            switch (roundData.getRoundType()) {
            case ContestConstants.SRM_ROUND_TYPE_ID:
            case ContestConstants.SRM_QA_ROUND_TYPE_ID:
            case ContestConstants.TOURNAMENT_ROUND_TYPE_ID:
            case ContestConstants.INTRO_EVENT_ROUND_TYPE_ID:
            case ContestConstants.PRIVATE_LABEL_TOURNAMENT_ROUND_TYPE_ID:
            case ContestConstants.TEAM_SRM_ROUND_TYPE_ID:
            case ContestConstants.LONG_ROUND_TYPE_ID:
            case ContestConstants.MODERATED_CHAT_ROUND_TYPE_ID:
            case ContestConstants.HS_SRM_ROUND_TYPE_ID:
            case ContestConstants.HS_TOURNAMENT_ROUND_TYPE_ID:
            case ContestConstants.WEAKEST_LINK_ROUND_TYPE_ID:
            case ContestConstants.FORWARDER_ROUND_TYPE_ID:
            case ContestConstants.FORWARDER_LONG_ROUND_TYPE_ID:
            case ContestConstants.LONG_PROBLEM_ROUND_TYPE_ID:
            case ContestConstants.LONG_PROBLEM_QA_ROUND_TYPE_ID:
            case ContestConstants.LONG_PROBLEM_TOURNAMENT_ROUND_TYPE_ID:
            case ContestConstants.EDUCATION_ALGO_ROUND_TYPE_ID:
            case ContestConstants.AMD_LONG_PROBLEM_ROUND_TYPE_ID:
                activeRoundsList.add(roundModel);
                activeRoundsMap.put(key, roundModel);
                sortRoundList();
                break;
            case ContestConstants.PRACTICE_ROUND_TYPE_ID:
            case ContestConstants.LONG_PROBLEM_PRACTICE_ROUND_TYPE_ID:
            case ContestConstants.TEAM_PRACTICE_ROUND_TYPE_ID:
            case ContestConstants.AMD_LONG_PROBLEM_PRACTICE_ROUND_TYPE_ID:
                practiceRoundsList.add(roundModel);
                practiceRoundsMap.put(key, roundModel);

                // This is no longer correct. The round ID (key.longValue())
                // is no longer the same as the room ID.
                RoomModelImpl roomModel = new RoomModelImpl(
                        roundModel,
                        requester,
                        roundData.getPracticeRoomID(), // Use the room ID. Each practice round has only one room
                        // key.longValue(), // Here is the problem
                        roundModel.getRoundType().isTeamRound() ? ContestConstants.TEAM_PRACTICE_CODER_ROOM : ContestConstants.PRACTICE_CODER_ROOM,
                        eventService
                );
                roomModel.setDivisionID( roundData.getPracticeRoundDivision() );
                // putRoom(key, roomModel);
                roundModel.setCoderRooms(new RoomModelImpl[] {roomModel});
                putRoom(roundData.getPracticeRoomID(), roomModel);
                break;
            default:
                throw new IllegalArgumentException(
                                                   "Bad round type: " + roundData
                                                   );
            }
        } else {
            RoundModelImpl roundModel = (RoundModelImpl) activeRoundsMap.get(key);
            roundModel.setContestName(roundData.getContestName());
            roundModel.setRoundName(roundData.getRoundName());
            roundModel.setMenuStatus(roundData.isEnabled());
            roundModel.setPhase(roundData.getPhaseData().getPhaseType(), roundData.getPhaseData().getEndTime());
            roundModel.setRoundCustomProperties(roundData.getCustomProperties());
        }
    }

    private void sortRoundList() {
        Collections.sort(activeRoundsList, new Comparator() {
                public int compare(Object o1, Object o2) {
                    RoundModel r1 = (RoundModel) o1;
                    RoundModel r2 = (RoundModel) o2;
                    return r1.getRoundID().compareTo(r2.getRoundID());
                }
            });
    }
    /**
     * <p>
     * remove the round data.
     * </p>
     * @param roundData
     *         the round data.
     */
    synchronized void removeRound(RoundData roundData) {
        Long key = new Long(roundData.getRoundID());
        RoundModelImpl round = getRoundModel(key.longValue());
        if (round.hasCoderRooms()) {
            RoomModel[] rooms = round.getCoderRooms();
            for (int i = 0; i < rooms.length; i++) {
                removeRoom(rooms[i].getRoomID());
            }
        }
        switch (roundData.getRoundType()) {
        case ContestConstants.SRM_ROUND_TYPE_ID:
        case ContestConstants.SRM_QA_ROUND_TYPE_ID:
        case ContestConstants.LONG_ROUND_TYPE_ID:
        case ContestConstants.TEAM_SRM_ROUND_TYPE_ID:
        case ContestConstants.TOURNAMENT_ROUND_TYPE_ID:
        case ContestConstants.INTRO_EVENT_ROUND_TYPE_ID:
        case ContestConstants.PRIVATE_LABEL_TOURNAMENT_ROUND_TYPE_ID:
        case ContestConstants.HS_SRM_ROUND_TYPE_ID:
        case ContestConstants.HS_TOURNAMENT_ROUND_TYPE_ID:
        case ContestConstants.WEAKEST_LINK_ROUND_TYPE_ID:
        case ContestConstants.LONG_PROBLEM_ROUND_TYPE_ID:
        case ContestConstants.LONG_PROBLEM_QA_ROUND_TYPE_ID:
        case ContestConstants.LONG_PROBLEM_TOURNAMENT_ROUND_TYPE_ID:
        case ContestConstants.EDUCATION_ALGO_ROUND_TYPE_ID:
        case ContestConstants.AMD_LONG_PROBLEM_ROUND_TYPE_ID:
            activeRoundsList.remove(round);
            activeRoundsMap.remove(key);
            roundViewManager.clearRoundList();
            roundViewManager.updateActiveRoundList();
            break;
        case ContestConstants.PRACTICE_ROUND_TYPE_ID:
        case ContestConstants.LONG_PROBLEM_PRACTICE_ROUND_TYPE_ID:
        case ContestConstants.TEAM_PRACTICE_ROUND_TYPE_ID:
        case ContestConstants.AMD_LONG_PROBLEM_PRACTICE_ROUND_TYPE_ID:
            throw new UnsupportedOperationException(
                                                    "Removing practice rounds not supported"
                                                    );
        default:
            throw new IllegalArgumentException(
                                               "Bad round type: " + roundData
                                               );
        }
    }

    //    private RoundModel[] roundModelStorage = null;;
    public synchronized RoundModel[] getActiveRounds() {
        //        if (roundModelStorage == null ||
        //                roundModelStorage.length != activeRoundsList.size()) {
        //            roundModelStorage = (RoundModel[]) activeRoundsList.toArray(
        //                    new RoundModel[activeRoundsList.size()]
        //            );
        //        }
        //        return roundModelStorage;
        return (RoundModel[]) activeRoundsList.toArray(new RoundModel[0]);
    }

    //    private RoundModel[] practiceRoundModelStorage = null;;
    public synchronized RoundModel[] getPracticeRounds() {
        //        if (practiceRoundModelStorage == null ||
        //                practiceRoundModelStorage.length != practiceRoundsList.size()) {
        //            practiceRoundModelStorage = (RoundModel[])
        //                    practiceRoundsList.toArray(
        //                            new RoundModel[practiceRoundsList.size()]
        //                    );
        //        }
        //        return practiceRoundModelStorage;
        return (RoundModel[]) practiceRoundsList.toArray(new RoundModel[0]);
    }
    
    public synchronized CategoryData[] getRoundCategories() {
        return roundCategories;
    }


    public RoomModel getRoom(long roomID) {
        Long key = new Long(roomID);
        if (!roomMap.containsKey(key)) {
            throw new IllegalArgumentException("Invalid room: " + roomID);
        }
        return (RoomModel) roomMap.get(key);
    }

    public RoomModel[] getRooms() {
        return (RoomModel[]) roomMap.values().toArray(new RoomModel[0]);
    }

    RoomModel removeRoom(long roomID) {
        Long key = new Long(roomID);
        if (!roomMap.containsKey(key)) {
            throw new IllegalArgumentException("Invalid room: " + roomID);
        }
        return removeRoom(key);
    }
    
    RoomModel removeRoom(Long roomID) {
        RoomModel room = (RoomModel) roomMap.remove(roomID);
        if(room!=null) {
            roomViewManagerManager.removeRoom(room);
        }
        return room;
    }
    
    RoomModel putRoom(long roomID, RoomModel room) {
        return putRoom(new Long(roomID), room);
    }
    
    RoomModel putRoom(Long roomID, RoomModel room) {
        if(roomID==null) throw new IllegalArgumentException("Null room ID");
        if(room==null) throw new IllegalArgumentException("Null room: " + roomID);

        //System.out.println("PUTTING " + roomID.longValue() + "," + room.getType().intValue());
        roomMap.put(roomID, room);
        
        roomViewManagerManager.addRoom(room);
        
        return room;
    }
    
    void clearRooms() {
        roomMap.clear();
        roomViewManagerManager.clearRooms();
    }
    
    
    void newCoderRooms(long roundID, RoomData[] rooms) {
        final RoundModelImpl round = getRoundModel(roundID);
        final RoomModelImpl[] roomModels = new RoomModelImpl[rooms.length];
        for (int i = 0; i < rooms.length; i++) {
            RoomData roomData = rooms[i];
            int roomID = roomData.getRoomID();
            roomModels[i] = new RoomModelImpl(
                                              round,
                                              requester,
                                              roomID,
                                              roomData.getRoomType(),
                                              eventService
                                              );
            roomModels[i].setName(roomData.getRoomTitle());
            roomModels[i].setRoomNumber(i + 1);
            roomModels[i].setDivisionID(rooms[i].getDivisionID());
            putRoom(new Long(roomID), roomModels[i]);
        }
        Runnable runnable = new Runnable() {
                public void run() {
                    round.setCoderRooms(roomModels);
                }
            };
        eventService.invokeLater(runnable);
    }

    void newAdminRoom(long roundID, RoomData adminRoom) {
        //System.out.println("NEW ADMIN ROOM");
        //System.out.println("getRoomID(): " + adminRoom.getRoomID() + ",    getRoomType()="+adminRoom.getRoomType());
        if (userInfo.isAdmin()) {  // ignore if not admin
            RoundModelImpl round = getRoundModel(roundID);
            RoomModelImpl roomModel = new RoomModelImpl(
                                                        round,
                                                        requester,
                                                        adminRoom.getRoomID(),
                                                        adminRoom.getRoomType(),
                                                        eventService
                                                        );
            roomModel.setDivisionID(ContestConstants.DIVISION_ADMIN);
            putRoom(new Long(adminRoom.getRoomID()), roomModel);
            round.setAdminRoom(roomModel);
        }
    }

    void newLobby(Long roomID) {
        putRoom(
                roomID,
                new RoomModelImpl(
                                  null,
                                  requester,
                                  roomID.longValue(),
                                  ContestConstants.LOBBY_ROOM,
                                  eventService
                                  )
                );
    }

    EventService getEventService() {
        return eventService;
    }

    public synchronized boolean isRoomLeader(String handle) {
        for (Iterator iterator = activeRoundsList.iterator();
             iterator.hasNext();) {
            RoundModelImpl roundModel = (RoundModelImpl) iterator.next();
            if (roundModel.isRoomLeader(handle)) {
                return true;
            }
        }
        return false;
    }

    public void setComponentAssignmentData(ComponentAssignmentData data) {
        this.componentAssignmentData = data;
    }

    public ComponentAssignmentData getComponentAssignmentData() {
        return componentAssignmentData;
    }

    public InterceptorManager getInterceptorManager() {
        return interceptorManager;
    }

    public RoomViewManagerManager getRoomViewManagerManager() {
        return roomViewManagerManager;
    }

    void setCurrentAppletVersion(String response) {
        currentAppletVersion = response;
    }

    public Object unsealObject(SealedSerializable obj) {
        try {
            return MessageEncryptionHandler.unsealObject(obj, encryptKey);
        } catch (Exception e) {
            throw new IllegalArgumentException("Decryption fails", e);
        }
    }

    public SealedSerializable sealObject(Object obj) {
        try {
            return MessageEncryptionHandler.sealObject(obj, encryptKey);
        } catch (Exception e) {
            throw new IllegalArgumentException("Encryption fails", e);
        }
    }
}
