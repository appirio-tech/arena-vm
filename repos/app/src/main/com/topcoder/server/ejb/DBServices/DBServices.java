/*
* Copyright (C) - 2014 TopCoder Inc., All Rights Reserved.
*/
package com.topcoder.server.ejb.DBServices;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.ejb.EJBObject;

import com.topcoder.netCommon.contest.ComponentAssignmentData;
import com.topcoder.netCommon.contestantMessages.response.data.CategoryData;
import com.topcoder.server.AdminListener.response.CommandResponse;
import com.topcoder.server.common.Coder;
import com.topcoder.server.common.EventRegistration;
import com.topcoder.server.common.LongCoderComponent;
import com.topcoder.server.common.Registration;
import com.topcoder.server.common.RegistrationResult;
import com.topcoder.server.common.Results;
import com.topcoder.server.common.Room;
import com.topcoder.server.common.Round;
import com.topcoder.server.common.Team;
import com.topcoder.server.common.User;
import com.topcoder.server.common.WeakestLinkData;
import com.topcoder.server.common.WeakestLinkTeam;
import com.topcoder.server.contest.ImportantMessageData;
import com.topcoder.shared.language.Language;

/**
 * Public Interface provided for DBServices ejb.
 *
 * <p>
 * Changes in version 1.1 (TopCoder Competition Engine Arena Login Logic Update v1.0):
 * <ol>
 *      <li>Add {@link #addUserGroup(int userId, int groupId)} method to add user group.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.2 (TopCoder Competition Engine - Revise Authentication Logic for SSO v1.0):
 * <ol>
 *      <li>Removed {@link #authenticateUserSSO(String)} method.</li>
 *      <li>Added {@link #validateSSOToken(String)} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.3 (Web Arena UI Member Photo Display v1.0):
 * <ol>
 *      <li>Add {@link #getMemberPhotoPath(int coderId)} method.</li>
 * </ol>
 * </p>
 * <p>
 * Changes in version 1.3 (Module Assembly - Web Socket Listener - Track User Actions From Web Arena):
 * <ol>
 *     <li>Added {@link #recordUserAction(String, String, String, java.util.Date)} method.</li>
 * </ol>
 * </p>
 *
 * @author Hao Kung, freegod
 * @version 1.3
 * @see com.topcoder.server.ejb.DBServices.DBServicesBean
 */
public interface DBServices extends EJBObject {
    int SYSTEM_TESTS_FAILED = -1;
    int SYSTEM_TESTS_PENDING = 0;
    int SYSTEM_TESTS_PASSED = 1;

    User authenticateUser(String user, String pass) throws RemoteException, DBServicesException;

    /**
     * validate sso and return { handle, password } values.
     *
     * @param sso the sso to validate.
     * @return the { handle, password } array, or null if sso is invalid.
     *
     * @throws RemoteException if there are remote exceptions.
     * @throws DBServicesException if error occurs in db operations.
     * @since 1.2
     */
    String[] validateSSOToken(String sso) throws RemoteException, DBServicesException;

    /* grp 05.30.2003 don't currently see any uses, can probably be removed and getUser(String, boolean) be used instead */
    User getUser(String username) throws RemoteException, DBServicesException;

    User getUser(String username, boolean ignoreCase) throws RemoteException, DBServicesException;

    /* get the user only when it is active. added on Oct. 04 2007 by visualage. used in login */
    User getUser(int userID, boolean activeOnly) throws RemoteException, DBServicesException;

    User getUser(int userID) throws RemoteException, DBServicesException;

    User getHighSchoolUser(int userID) throws RemoteException, DBServicesException;

    Team getTeam(int teamID) throws RemoteException, DBServicesException;

    Team createTeam(String teamName, int captainID, int teamType) throws RemoteException, DBServicesException;

    void commitTeam(Team team) throws RemoteException, DBServicesException;

    void registerTeam(int teamID, int roundID, Collection coders) throws RemoteException, DBServicesException;

    void deleteConnections() throws RemoteException, DBServicesException;

    CommandResponse registerCoderByHandle(String handle, int roundId, boolean eligibleHint) throws RemoteException, DBServicesException;

    CommandResponse unregisterCoderByHandle(String handle, int roundId) throws RemoteException, DBServicesException;
    
    Results registerCoder(int coderId, int eventId, List surveyData) throws RemoteException, DBServicesException;
    
    Results registerCoderWithChecks(int coderId, int roundId, List surveyData) throws RemoteException, DBServicesException;

    boolean clearPracticer(int coderId, int roundId, boolean teamClear) throws RemoteException, DBServicesException;

    //added 2-20 rfairfax
    boolean clearPracticeProblem(int coderId, int roundId,Long componentID, boolean teamClear) throws RemoteException, DBServicesException;

    Room getRoom(int roomID) throws RemoteException, DBServicesException;
    Coder getRoomCoder(Round round, int roomId, int coderId, int teamId) throws RemoteException, DBServicesException;

    long coderOpenComponent(int coderId, int contestId, int roundId, int roomId, int componentId) throws RemoteException, DBServicesException;
    
    long coderOpenLongComponent(int coderId, int contestId, int roundId, int componentId) throws RemoteException, DBServicesException;

    void synchTeamMembersComponents(int contestId, int roundId, int roomId, int componentId, int oldCoderId, int newCoderId) throws RemoteException, DBServicesException;

    HashMap getHandleToUserIDMap() throws RemoteException, DBServicesException;

    HashMap getTeamNameToTeamIDMap() throws RemoteException, DBServicesException;

    void endContest(int contestId, int roundId) throws RemoteException, DBServicesException;
    
    void endTCHSContest(int contestId, int roundId) throws RemoteException, DBServicesException;

    Round getContestRound(int roundID) throws RemoteException, DBServicesException;
    /**
     * <p>
     * get the event registration data.
     * </p>
     * @param userId
     *         the user id.
     * @param eventId
     *         the event id.
     * @return the event registration data.
     * @throws RemoteException
     *          if any error occurs during ejb call.
     * @throws DBServicesException
     *          if any db related error occurs.
     */
    EventRegistration getEventRegistrationData(int userId,int eventId) throws RemoteException, DBServicesException;
    
    Registration getRegistration(int eventID) throws RemoteException, DBServicesException;
    
    LongCoderComponent getLongCoderComponent(int roundId, int coderId, int componentId) throws DBServicesException, RemoteException;
    
    void backupPracticeRoom(int roundID) throws RemoteException, DBServicesException;

    boolean isDeleteCoderFromPracticeRoom(int roundID, int coderID, int type) throws RemoteException, DBServicesException;    

    void clearPracticeRoom(int roundID, int type) throws RemoteException, DBServicesException;
    
    void deleteCoderFromPracticeRoom(int roundID, int coderID) throws RemoteException, DBServicesException;

    void addTime(Round contest, int minutes, int seconds, int phase, boolean addToStart)
            throws RemoteException, DBServicesException;

    void addPracticeCoder(Coder coder) throws RemoteException, DBServicesException;

    void archiveChat(ArrayList chatQueue) throws RemoteException, DBServicesException;

    void processRoundEvent(Round contestState) throws RemoteException, DBServicesException;

    void assignRooms(int contestId, int roundId, int codersPerRoom, int type,
            boolean byDivision, boolean isFinal, boolean isByRegion, double p) throws RemoteException, DBServicesException;

    HashMap getAdminRoomMap() throws RemoteException, DBServicesException;

    void removeConnection(String serverType, int servId, int connId, Timestamp timestamp)
            throws RemoteException, DBServicesException;

    void addConnection(String ip, String serverType, int servID, int connID, int coderID, String userName, Timestamp timestamp)
            throws RemoteException, DBServicesException;

    void createContest(Round cr) throws RemoteException, DBServicesException;

    int getNextServerID() throws RemoteException, DBServicesException;

    Collection allocatePrizes(int roundID, boolean isFinal) throws RemoteException, DBServicesException;

    ArrayList getPracticeRoundIDs() throws RemoteException, DBServicesException;

    void setCoderLanguage(int coderID, int languageID) throws RemoteException, DBServicesException;
    
    void setUserStatus(String handle, boolean isActiveStatus) throws RemoteException, DBServicesException;

    ArrayList getAllActiveModeratedChatSessions() throws RemoteException, DBServicesException;

    ArrayList getAllowedSpeakers(int round_id) throws RemoteException, DBServicesException;

    void addNewBroadcast(long timeSent, String message, int round_id, int problem_id, int sent_by_user_id, int broadcast_type_id,
            int status_id) throws RemoteException, DBServicesException;

    Collection getRecentBroadcasts(long minTimeSent, int round_id) throws RemoteException, DBServicesException;

    Collection getRoundBroadcasts(int round_id) throws RemoteException, DBServicesException;

    User createUser(String username, String id, int companyID) throws RemoteException, DBServicesException;

    boolean checkTaken(String username) throws RemoteException, DBServicesException;

    User getCompanyUser(int companyID, String companyUserID) throws RemoteException, DBServicesException;

    void insertRoomResult(int roundId, int lastRoomId, int coderId, int roomSeed, int rating, int divisionSeed)
            throws RemoteException,DBServicesException;

    Round[] getPracticeRounds(int limit) throws RemoteException, DBServicesException;
    
    int[] getVisitedPracticeRounds(int coderID) throws RemoteException, DBServicesException;
    
    CategoryData[] getCategories() throws RemoteException, DBServicesException;

    WeakestLinkData loadWeakestLinkData(int roundId) throws RemoteException, DBServicesException;

    WeakestLinkTeam getWeakestLinkTeam(int teamId, int roundId) throws RemoteException, DBServicesException;

    void storeWeakestLinkData(WeakestLinkData weakestLinkData, int targetRoundId) throws RemoteException, DBServicesException;

    void storeBadgeId(int roundId, int coderId, String badgeId) throws RemoteException,DBServicesException;

    void saveComponentAssignmentData(ComponentAssignmentData data) throws RemoteException, DBServicesException;

    ComponentAssignmentData getComponentAssignmentData(int teamID, int roundID) throws RemoteException, DBServicesException;

    int getCoderSchoolID(int coderID) throws RemoteException, DBServicesException, DBServicesException;

    int getCoachIDFromSchoolID(int schoolID) throws RemoteException, DBServicesException, DBServicesException;

    int getSchoolIDFromCoach(int coderID)  throws RemoteException, DBServicesException, DBServicesException;

    RegistrationResult registerUser(String userId, String password, String firstName, String lastName, String email, String phoneNumber)
            throws RemoteException, DBServicesException;

    int getComponentSystemTestStatus(int coderID, int componentID, int roundID) throws DBServicesException, RemoteException;
    
    String getFailureMessage(int coderID, int componentID, int roundID) throws DBServicesException, RemoteException;
    
    ArrayList getUserImportantMessages(int user_id) throws RemoteException, DBServicesException;
    
    void readMessage(int userID, int messageID) throws RemoteException, DBServicesException;
    
    ImportantMessageData[] getMessages(int user_id) throws RemoteException, DBServicesException;

    boolean isComponentOpened(int coderId, int roundId, int componentId)throws RemoteException, DBServicesException;
    
    boolean isLongComponentOpened(int coderId, int roundId, int componentId)throws RemoteException, DBServicesException;
    
    boolean isHighSchoolRound(long roundID) throws RemoteException, DBServicesException;
    
    int createNewQualRoom(int roundId) throws RemoteException, DBServicesException;
    
    Language[] getAllowedLanguagesForRound(int roundID) throws DBServicesException, RemoteException;

    List getRoundIDsToLoadOnStartUp(long minCodingTimeEnd) throws DBServicesException, RemoteException;
    
    void updateAlgoPlace(int roundId) throws RemoteException, DBServicesException;
    /**
     * add to table user_group_xref with the group id and user id.
     * @param userId the user id that to be added.
     * @param groupId the group id that to be added.
     * @throws RemoteException if the ejb remote call error occurs.
     * @throws DBServicesException if any related error occurs.
     * @return true=add the user group, false=not add maybe the record is already exist.
     */
    boolean addUserGroup(int userId, int groupId) throws RemoteException, DBServicesException;

    /**
     * Gets the user image info.
     * @param coderId the coder id.
     * @return the member photo path.
     */
    String getMemberPhotoPath(int coderId) throws RemoteException, DBServicesException;

    /**
     * <p>
     * Track web arena user actions.
     * </p>
     *
     * @param userHandle
     *          the user handle
     * @param actionName
     *          the action name
     * @param client
     *          the client used. Currently only 'web arena'
     * @param date
     *          the created date
     * @throws RemoteException
     *          if related error occurs.
     * @throws DBServicesException if any related error occurs.
     * @since 1.3
     */
    void recordUserAction(String userHandle, String actionName, String client, java.util.Date date)
            throws RemoteException, DBServicesException;
}
