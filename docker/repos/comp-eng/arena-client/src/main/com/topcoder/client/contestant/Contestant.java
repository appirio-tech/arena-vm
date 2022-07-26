/**
 * @author Michael Cervantes (emcee)
 * @since May 2, 2002
 */
package com.topcoder.client.contestant;

import com.topcoder.client.connectiontype.ConnectionType;
import com.topcoder.client.contestant.message.Requester;
import com.topcoder.client.contestant.view.ContestantView;
import com.topcoder.client.contestant.view.EventService;
import com.topcoder.client.contestant.view.HeartbeatListener;
import com.topcoder.client.contestant.view.MenuView;
import com.topcoder.client.contestant.view.RoomViewManager;
import com.topcoder.client.contestant.view.TeamListView;
import com.topcoder.client.contestant.view.UserListListener;
import com.topcoder.netCommon.contest.ComponentAssignmentData;
import com.topcoder.netCommon.contestantMessages.UserInfo;
import com.topcoder.netCommon.contestantMessages.response.data.CategoryData;
import com.topcoder.shared.netCommon.SealedSerializable;

/**
 * Defines an interface which has the functionality to connect and communicate with the server.
 * 
 * @author Michael Cervantes (emcee)
 * @version $Id: Contestant.java 72313 2008-08-14 07:16:48Z qliu $
 */
public interface Contestant {
    /**
     * Initializes the communication back-end.
     * 
     * @param host the host of the server/proxy.
     * @param port the port of the server/proxy.
     * @param tunnellingURL the URL if connects to server via HTTP tunneling.
     * @param contestantView the listener used to be notified important issues.
     * @param activeUsersView the listener used to be notified when active user list is available.
     * @param registeredUsersView the listener used to be notified when registered user list of a SRM is available.
     * @param hsRegisteredUsersView the listener used to be notified when registered user list of a HSSRM is available.
     * @param mmRegisteredUsersView the listener used to be notified when registered user list of a MM is available.
     * @param teamListView the listener used to be notified when the list of team is available.
     * @param availableListView the listener used to be notified when the list of available members is available. It is
     *            used by team competition.
     * @param memberListView the listener used to be notified when the list of team-assigned members is available.
     * @param menuView the listener used to be notified when lobby/chat/practice rounds have been changed.
     * @param roomViewManager the listener used to be notified when there is a change to the rooms.
     * @param eventService the asynchorous executer.
     * @param destinationHost the host and port of the server if proxy is used.
     */
    void init(String host, int port, String tunnellingURL, ContestantView contestantView,
        UserListListener activeUsersView, UserListListener registeredUsersView, UserListListener hsRegisteredUsersView,
        UserListListener mmRegisteredUsersView, TeamListView teamListView, // team list view
        UserListListener availableListView, // team list available members view
        UserListListener memberListView, // team list recruited members view
        MenuView menuView, RoomViewManager roomViewManager, EventService eventService, String destinationHost);

    /**
     * Gets the broadcast message manager.
     * 
     * @return the broadcast message manager.
     * @see BroadcastManager
     */
    BroadcastManager getBroadcastManager();

    /**
     * Gets the requester which sends requests to the server.
     * 
     * @return the message requester.
     * @see Requester
     */
    Requester getRequester();

    /**
     * Sets the connection type used to connect the server.
     * 
     * @param t the connection type used.
     */
    void setConnectionType(ConnectionType t);

    /**
     * Sets a flag indicating if the connection should go through a HTTP CONNECT proxy specified by the host/port in
     * <code>init</code>.
     * 
     * @param gtp a flag indicating if the connection should go through a proxy.
     */
    void setGoThroughProxy(boolean gtp);

    /**
     * Gets the connection type used to connect the server.
     * 
     * @return the connection type used.
     */
    ConnectionType getConnectionType();

    /**
     * Detects the best connection type that can be used.
     * <p>
     * The detected connection type is set as the connection type to use.
     * <p>
     * If the connection type could not be detected, returns <code>null</code>.
     * <p>
     * 
     * @param listener to listen autodetect process status
     * @return The connection type or <code>null</code> if autodetection failed.
     */
    ConnectionType autoDetectConnectionType(StatusListener listener);

    /**
     * Cancels the best connection type detection.
     */
    void cancelAutoDetectConnectionType();

    /**
     * Gets a flag indicating if the user has been logged in.
     * 
     * @return <code>true</code> if the user has been logged in; <code>false</code> otherwise.
     */
    boolean isLoggedIn();

    /**
     * Gets the unique connection ID of the connection.
     * 
     * @return the unique connection ID.
     */
    long getConnectionID();

    /**
     * Gets the hash code used to authenticate the user/connection during a reconnection.
     * 
     * @return the hash code used to authenticate during reconnection.
     */
    String getHashCode();

    /**
     * Starts the reconnection attempts. It is done asynchronously.
     */
    void startReconnectAttempt();

    /**
     * Attempts to log the user in. The username, password and the handle (optional) are given.
     * 
     * @param username the username of the user.
     * @param password the password of the user.
     * @param tcHandle the optional handle of the user.
     * @throws LoginException if login process fails.
     */
    void login(String username, char[] password, String tcHandle) throws LoginException;

    /**
     * Gets the current applet version number.
     * 
     * @return the current applet version number.
     */
    String getCurrentAppletVersion();

    /**
     * Attempts to log the user in. More information are provided. It used to register a new user automatically.
     * However, it seems to be deprecated since user has to register on website.
     * 
     * @param username the username of the user.
     * @param password the password of the user.
     * @param firstName the first name of the user.
     * @param lastName the last name of the user.
     * @param email the email address of the user.
     * @param companyName the company name of the user.
     * @param phoneNumber the phone number of the user.
     * @throws LoginException if login process fails.
     */
    void loginWithEmail(String username, char[] password, String firstName, String lastName, String email,
        String companyName, String phoneNumber) throws LoginException;

    /**
     * Attempts to log the user in. The badge ID is given.
     * 
     * @param handle the username of the user.
     * @param password the password of the user.
     * @param badgeId the badge ID of the user.
     * @throws LoginException if login process fails.
     */
    void loginWithBadgeId(String handle, String password, String badgeId) throws LoginException;

    /**
     * Attempts to log in as a guest.
     * 
     * @throws LoginException if login process fails.
     */
    void guestLogin() throws LoginException;

    /**
     * Logs the user off. It automatically resets the internal state and closes the connection.
     */
    void logoff();

    /**
     * Resets the internal state and closes the connection.
     */
    void reset();

    /**
     * Gets the current logged in user. If no user has been logged in, empty string is returned.
     * 
     * @return the current user.
     */
    String getCurrentUser();

    /**
     * Gets the current team of the logged in user. If no user has been logged in, empty string is returned.
     * 
     * @return the current team.
     */
    String getCurrentTeam();

    /**
     * Gets information about the current logged in user.
     * 
     * @return information about the current logged in user.
     */
    UserInfo getUserInfo();

    /**
     * Moves the logged in user to the given room. When the room ID is <code>ContestConstants.ANY_ROOM</code>, and
     * the room type is a lobby room, an available lobby room is chosen.
     * 
     * @param roomType the room type of the room to move to.
     * @param roomID the ID of the room to move to.
     * @throws TimeOutException if the moving request timed out.
     */
    public void move(int roomType, long roomID) throws TimeOutException;

    /**
     * Watches the given room.
     * 
     * @param roomID the room ID of the room.
     * @return the model of the room to be watched.
     * @throws TimeOutException if the watching request timed out.
     */
    RoomModel watch(long roomID) throws TimeOutException;

    /**
     * Stops watching the given room.
     * 
     * @param roomID the room ID of the room.
     */
    void unwatch(long roomID);

    /**
     * Adds a heartbeat listener which is notified regularly to synchronize time of server.
     * 
     * @param listener the heartbeat listener to be added.
     */
    void addHeartbeatListener(HeartbeatListener listener);

    /**
     * Removes a heartbeat listener which is notified regularly to synchronize time of server.
     * 
     * @param listener the heartbeat listener to be removed.
     */
    void removeHeartbeatListener(HeartbeatListener listener);

    /**
     * Gets the manager used to manage listeners of active round updates.
     * 
     * @return the active round listener manager.
     */
    RoundViewManager getRoundViewManager();

    /**
     * Gets the current server time. There is no network communication done by this method, so it returns immediately.
     * 
     * @return the current server time.
     */
    long getServerTime();

    /**
     * Gets the model of the current room.
     * 
     * @return the model of the current room.
     */
    RoomModel getCurrentRoom();

    /**
     * Gets the model of the given room.
     * 
     * @param roomID the room ID of the room whose model is returned.
     * @return the model of the specified room.
     * @throws IllegalArgumentException if the room is not available.
     */
    RoomModel getRoom(long roomID);

    /**
     * Gets an array of all available rooms of active rounds. A copy is returned.
     * 
     * @return an array of all available rooms.
     */
    RoomModel[] getRooms();

    /**
     * Gets an array of all active rounds. A copy is returned.
     * 
     * @return an array of all active rounds.
     */
    RoundModel[] getActiveRounds();

    /**
     * Gets an array of all practice rounds. A copy is returned.
     * 
     * @return an array of all practice rounds.
     */
    RoundModel[] getPracticeRounds();

    /**
     * Gets the model of a round. The round must be active or practice.
     * 
     * @param roundId the round ID of the round.
     * @return the model of a round.
     * @throws IllegalArgumentException if the round is not active nor practice.
     */
    RoundModel getRound(long roundId);

    /**
     * Gets the array of all practice round categories. There is no copy.
     * 
     * @return the array of all practice round categories.
     */
    CategoryData[] getRoundCategories();

    /**
     * Gets a flag indicating if the user is a leader of a room of an active round.
     * 
     * @param handle the handle of the user to be checked.
     * @return <code>true</code> if the user is a leader of a room; <code>false</code> otherwise.
     */
    boolean isRoomLeader(String handle);

    /**
     * Gets the component assignment of a team round. Only the team of the user will be accessible.
     * 
     * @return the component assignemtn of a team round.
     */
    ComponentAssignmentData getComponentAssignmentData();

    /**
     * Gets the message interceptor manager. The message interceptor are notified when messages are received from
     * server.
     * 
     * @return the message interceptor manager.
     */
    InterceptorManager getInterceptorManager();

    /**
     * Gets the manager to manage room update listeners.
     * 
     * @return the room view manager manager.
     */
    RoomViewManagerManager getRoomViewManagerManager();

    /**
     * Defines an listener interface which is notified when the connection type auto-detection status has been changed.
     * 
     * @author Michael Cervantes (emcee)
     * @version $Id: Contestant.java 72313 2008-08-14 07:16:48Z qliu $
     */
    public interface StatusListener {
        /**
         * Updates the connection type auto-detection status.
         * 
         * @param status the status to be updated.
         */
        void updateStatus(String status);
    }

    /**
     * Decrypts an encrypted object transmitted over the network. The decryption is done by the symmetric encryption key
     * negotiated by server and client.
     * 
     * @param obj the encrypted object to be decrypted.
     * @return the decrypted object.
     * @throws IllegalArgumentException if decryption fails.
     */
    public Object unsealObject(SealedSerializable obj);

    /**
     * Encrypts an object to be transmitted over the network. The encryption is done by the symmetric encryption key
     * negotiated by server and client.
     * 
     * @param obj the object to be encrypted.
     * @return the encrypted object.
     * @throws IllegalArgumentException if encryption fails.
     */
    public SealedSerializable sealObject(Object obj);
}
