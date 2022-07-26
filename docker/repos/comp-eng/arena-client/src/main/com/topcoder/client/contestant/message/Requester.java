/*
* Copyright (C) 2002 - 2014 TopCoder Inc., All Rights Reserved.
*/

/**
 * @author Michael Cervantes (emcee)
 * @since May 2, 2002
 */
package com.topcoder.client.contestant.message;

import java.util.ArrayList;

import com.topcoder.client.contestant.InterceptorManager;
import com.topcoder.client.contestant.TimeOutException;
import com.topcoder.client.netClient.Client;
import com.topcoder.netCommon.contest.ComponentAssignmentData;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contestantMessages.response.LongTestResultsResponse;
import com.topcoder.shared.netCommon.SealedSerializable;

/**
 * Defines an interface which is responsible to send request messages to the server.
 *
 * <p>
 * Changes in version 1.1 (Module Assembly - TopCoder Competition Engine - Batch Test):
 * <ol>
 *      <li>Added {@link #requestBatchTest(ArrayList, long)} method to request batch testing.</li>
 * </ol>
 * </p>
 *
 * @author Michael Cervantes (emcee), dexy
 * @version 1.1
 */
public interface Requester {
    /**
     * Requests the applet version allowed by the server.
     *
     * @throws TimeOutException if there is no corresponding response before timing out.
     */
    void requestCurrentAppletVersion() throws TimeOutException;

    /**
     * Requests the current time on the server. It synchronizes the time on the client.
     *
     * @param connectionID the connection ID of the client.
     * @throws TimeOutException if there is no corresponding response before timing out.
     */
    void requestSynchTime(long connectionID) throws TimeOutException;

    /**
     * Requests to reconnect to the server. This allows temporary connection interruption without user to re-type the
     * handle/password. The authentication connection is done by comparing the old connection ID and the
     * connection-specific hash string. If the pair checks out on the server, the reconnection is successful. Otherwise,
     * it fails and force user to log off.
     *
     * @param hash the hash value of the old connection.
     * @param connectionID the old connection ID.
     * @throws TimeOutException if there is no corresponding response before timing out.
     */
    void requestReconnect(SealedSerializable hash, long connectionID) throws TimeOutException;

    /**
     * Requests to log the user in. Only username and password are required. For all other information, most of them are
     * designed to register a user automatically when such user does not exist. It is not functioning any more.
     *
     * @param username the username of the user.
     * @param password the password of the user.
     * @param tcHandle the optional TopCoder handle of the user.
     * @param badgeId the optional badge ID of the user.
     * @param firstName the optional first name of the user.
     * @param lastName the optional last name of the user.
     * @param email the optional email of the user.
     * @param companyName the optional company name of the user.
     * @param phoneNumber the optional phone number of the user.
     * @throws TimeOutException if there is no corresponding response before timing out.
     */
    void requestLogin(String username, SealedSerializable password, String tcHandle, String badgeId, String firstName,
        String lastName, String email, String companyName, String phoneNumber) throws TimeOutException;

    /**
     * Requests to log the user in as a guest. For most of the competitions, guests are not allowed.
     *
     * @throws TimeOutException if there is no corresponding response before timing out.
     */
    void requestGuestLogin() throws TimeOutException;

    /**
     * Requests for the division summary for a round.
     *
     * @param roundID the round whose summary is requested.
     * @param divisionID the division whose summary is requested.
     * @throws TimeOutException if there is no corresponding response before timing out.
     */
    void requestDivSummary(long roundID, long divisionID) throws TimeOutException;

    /**
     * Requests to subscribe the changes of a room. For changes of the current room, there is no need to subscribe
     * separately.
     *
     * @param roomID the ID of the room to be subscribed.
     * @throws TimeOutException if there is no corresponding response before timing out.
     */
    void requestWatch(long roomID) throws TimeOutException;

    /**
     * Requests to log off.
     */
    void requestLogoff();

    /**
     * Requests to move the logged in user to a room. When the room ID is <code>ContestConstants.ANY_ROOM</code> and
     * the room type is a lobby room type, the logged in user will be moved to an available lobby room.
     *
     * @param roomType the type of the room to be moved to.
     * @param roomID the ID of the room to be moved to.
     * @throws TimeOutException if there is no corresponding response before timing out.
     */
    void requestMove(int roomType, long roomID) throws TimeOutException;

    /**
     * Requests to move the logged in user to an assigned room in the round. When the user has no assigned room in the
     * round, he will be moved to the first room in the round.
     *
     * @param roundID the ID of the round where the assigned room is located.
     * @throws TimeOutException if there is no corresponding response before timing out.
     */
    void requestEnterRound(long roundID) throws TimeOutException;

    /**
     * Requests to compile the solution code for a problem component. The user has to be in a contest room.
     *
     * @param code the source code to be compiled.
     * @param language the ID of the programming language of the source code.
     * @param componentID the ID of the problem component.
     */
    void requestCompile(String code, int language, long componentID);

    /**
     * Requests to test the solution of the logged in user for a problem component. The solution must be compiled first.
     * The user has to be in a contest room.
     *
     * @param test the arguments of the test.
     * @param componentID the ID of the problem component.
     */
    void requestTest(ArrayList test, long componentID);

    /**
     * Requests to batch test the solution of the logged in user for a problem component.
     * The solution must be compiled first. The user has to be in a contest room.
     * It executes all the testCases in one block and server responds with all the answers in one response.
     *
     * @param testCases the list of test cases for the batch testing
     * @param componentID the ID of the problem component.
     * @since 1.1
     */
    void requestBatchTest(ArrayList testCases, long componentID);

    /**
     * Requests to challenge the solution of a coder in the same room for a problem component. The user has to be in a
     * contest room.
     *
     * @param defender the handle of the coder to be challenged.
     * @param componentID the ID of the problem component.
     * @param test the arguments of the challenge.
     */
    void requestChallenge(String defender, long componentID, ArrayList test);

    /**
     * Requests to submit the solution of the logged in user for a problem component. The last compiled solution will be
     * submitted. The user has to be in a contest room.
     *
     * @param componentID the ID of the problem component.
     */
    void requestSubmitCode(long componentID);

    /**
     * Requests to send a chat text by the logged in user in a room. The chat text includes control commands as defined
     * in <code>com.topcoder.client.contestApplet.uilogic.panels.ChatPanel</code>. The chat can be either global or
     * with in the team.
     *
     * @param roomID the ID of the room where the chat happens.
     * @param msg the chat text.
     * @param scope the scope of the chat text.
     * @see ContestConstants#GLOBAL_CHAT_SCOPE
     * @see ContestConstants#TEAM_CHAT_SCOPE
     */
    void requestChatMessage(long roomID, String msg, int scope);

    /**
     * Requests to retrieve the history of a coder in a contest room.
     *
     * @param handle the handle of the coder.
     * @param roomID the ID of the contest room.
     * @param userType the type of the user.
     */
    void requestCoderHistory(String handle, long roomID, int userType);

    /**
     * Requests to retrieve the marathon submission history of a coder in a marathon contest room.
     *
     * @param handle the handle of the coder.
     * @param roomID the ID of the marathon contest room.
     * @param userType the type of the user.
     * @param example a flag indicating if example submission (<code>true</code>) or full submission (<code>false</code>)
     *            should be retrieved.
     */
    void requestSubmissionHistory(String handle, long roomID, int userType, boolean example);

    /**
     * Requests to retrieve the general information of a coder.
     *
     * @param coder the handle of the coder.
     * @param userType the type of the user.
     */
    void requestCoderInfo(String coder, int userType);

    /**
     * Notifies the server that the logged in user has entered a room successfully. Usually it is immediately called
     * after a successful response of a move request.
     *
     * @param roomID the ID of the room that the user entered.
     */
    void requestEnter(long roomID);

    /**
     * Requests to open the problem component and start coding. The user must be in a contest room.
     *
     * @param componentID the ID of the problem component.
     */
    void requestOpenComponentForCoding(long componentID);

    /**
     * Requests to open the problem statement for reading only. It is for marathon contest.
     *
     * @param roundId the ID of the round whose problem statement is retrieved.
     * @param problemID the ID of the problem whose problem statement is retrieved.
     */
    void requestOpenProblemForReading(long roundId, long problemID);

    /**
     * Requests to close the problem component and stop coding.
     *
     * @param componentID the ID of the problem component.
     * @param writer the logged in user who closes the problem component.
     */
    void requestCloseComponent(long componentID, String writer);

    /**
     * Requests to save the solution code for a problem component. The user has to be in a contest room.
     *
     * @param code the source code to be compiled.
     * @param languageID the ID of the programming language of the source code.
     * @param componentID the ID of the problem component.
     */
    void requestSave(long componentID, String code, int languageID);

    /**
     * Requests to submit the marathon solution of the logged in user for a problem component. There is no need to
     * compile before submission for marathon problems. The user has to be in a contest room.
     *
     * @param componentID the ID of the problem component.
     * @param code the source code of the solution.
     * @param languageID the ID of the programming language of the source code.
     * @param example <code>true</code> if the submission is an example submission; <code>false</code> otherwise.
     */
    void requestSubmitLong(long componentID, String code, int languageID, boolean example);

    /**
     * Requests to retrieve the marathon test results for a coder's solution of a problem component in a room.
     *
     * @param componentID the ID of the problem component.
     * @param roomID the ID of the room.
     * @param coder the handle of the coder.
     * @param resultsType the type of the marathon test result.
     * @see LongTestResultsResponse#RESULT_EXAMPLE
     * @see LongTestResultsResponse#RESULT_NONFINAL
     * @see LongTestResultsResponse#RESULT_FINAL
     */
    void requestLongTestResults(long componentID, long roomID, String coder, int resultsType);

    /**
     * Requests to search for a user who has logged in to the arena.
     *
     * @param search the handle of the user to be searched.
     */
    void requestSearch(String search);

    /**
     * Requests to close the division summary of a round.
     *
     * @param roundID the ID of the round.
     * @param divisionID the division of the round.
     */
    void requestCloseDivSummary(long roundID, long divisionID);

    /**
     * Requests to unsubscribe the changes of a room. For changes of the current room, there is no need to unsubscribe.
     *
     * @param roomID the ID of the room to be unsubscribed.
     */
    void requestUnwatch(long roomID);

    /**
     * Requests to clear all solutions of the logged in user in the practice room.
     *
     * @param roomID the ID of the practice room.
     */
    void requestClearPractice(long roomID);

    /**
     * Requests to clear solutions of the logged in user in the practice room.
     *
     * @param roomID the ID of the practice room.
     * @param componentID the IDs of the problem component whose solution will be cleared.
     */
    void requestClearPracticeProblem(long roomID, Long[] componentID);

    /**
     * Requests to view other coder's source code for a problem component during/after challenge phase.
     *
     * @param componentID the ID of the problem component.
     * @param pretty a flag indicating if the code should be formatted.
     * @param roomID the ID of the room.
     * @param defender the handle of the coder whose solution is retrieved.
     */
    void requestChallengeComponent(long componentID, boolean pretty, long roomID, String defender);

    /**
     * Requests to retrieve a list of all logged in users.
     */
    void requestActiveUsers();

    /**
     * Requests to retrieve all important messages.
     */
    void requestImportantMessages();

    /**
     * Requests to retrieve the system test results for problem components in a practice room. Only the logged in user's
     * system test results will be retrieved.
     *
     * @param roomID the ID of the practice room.
     * @param componentsId the IDs of the problem component.
     */
    void requestPracticeSystemTest(long roomID, int[] componentsId);

    /**
     * Requests to retrieve all admin broadcasts.
     */
    void requestGetAdminBroadcast();

    /**
     * Requests to register for a marathon round. The round must be active round.
     *
     * @param roundID the ID of the marathon round.
     */
    void requestRegisterEventInfo(long roundID);

    /**
     * Requests to register for a round with surveys. The round must be active round.
     *
     * @param roundID the ID of the round.
     * @param surveyData the survey answers.
     */
    void requestRegister(long roundID, ArrayList surveyData);

    /**
     * Requests to retrieve all registered users for a round. The round must be active round.
     *
     * @param roundID the ID of the round.
     */
    void requestRegisterUsers(long roundID);

    /**
     * Requests to retrieve the argument types of the problem component. The argument types are required when entering
     * the testing arguments. The user must be in a contest room, and must have compiled before testing.
     *
     * @param componentID the ID of the problem component.
     */
    void requestTestInfo(long componentID);

    /**
     * Requests to stop/start receiving chat texts. Upon connection establishment, the chat texts are sent by the
     * server.
     */
    void requestToggleChat();

    /**
     * Notifies the server that a popup dialog has been successfully shown on the client.
     *
     * @param type the type of the dialog.
     * @param button the response of the dialog.
     * @param surveyData additional response data.
     */
    void requestPopupGeneric(int type, int button, ArrayList surveyData);

    /**
     * Requests to retrieve the leader board information and updates.
     */
    void requestGetLeaderBoard();

    /**
     * Requests to stop retrieving the leader board information and updates.
     */
    void requestCloseLeaderBoard();

    /**
     * Requests for the room summary of a room. The room must be a contest room.
     *
     * @param roomID the ID of the room.
     */
    void requestOpenSummary(long roomID);

    /**
     * Requests to close the room summary of a room. The room must be a contest room.
     *
     * @param roomID the ID of the room.
     */
    void requestCloseSummary(long roomID);

    /**
     * Requests to join a team.
     *
     * @param teamName the name of the team.
     */
    void requestJoinTeam(String teamName);

    /**
     * Requests to leave a team.
     *
     * @param teamName the name of the team.
     */
    void requestLeaveTeam(String teamName);

    /**
     * Requests to add a coder as a member of the current team of the logged in user.
     *
     * @param userHandle the handle of the coder.
     */
    void requestAddTeamMember(String userHandle);

    /**
     * Requests to remove a coder as a member of the current team of the logged in user.
     *
     * @param userHandle the handle of the coder.
     */
    void requestRemoveTeamMember(String userHandle);

    /**
     * Requests to assign components in the team. Only the current team of the logged in user can be assigned.
     *
     * @param data the team component assignment.
     */
    public void requestAssignComponents(ComponentAssignmentData data);

    /**
     * Sets the network communication client used by this requester.
     *
     * @param client the network communication client.
     */
    void setClient(Client client);

    /**
     * Sets the interceptor manager which intercepts messages sent by this requester.
     *
     * @param manager the interceptor maanager.
     * @see InterceptorManager
     */
    void setInterceptorManager(InterceptorManager manager);

    /**
     * Requests to vote for a coder in the round.
     *
     * @param roundId the ID of the round.
     * @param selectedName the handle of the coder to be voted.
     */
    void requestVote(int roundId, String selectedName);

    /**
     * Requests to retrieve for the statistics of a coder in the round. It is used by voting.
     *
     * @param roundId the ID of the round.
     * @param coderName the handle of the coder.
     */
    void requestRoundStats(int roundId, String coderName);

    /**
     * Requests to retrieve the team information of the logged in user during a '<a
     * href="http://en.wikipedia.org/wiki/The_Weakest_Link">Weakest Link</a>' round.
     *
     * @param roundId the ID of the 'Weakest Link' round.
     */
    void requestWLMyTeamInfo(int roundId);

    /**
     * Requests to retrieve all team information during a 'Weakest Link' round.
     *
     * @param roundId the ID of the 'Weakest Link' round.
     */
    void requestWLTeamsInfo(int roundId);

    /**
     * Requests to automatically do system test of the logged in user's recent submission for a round hosted by 'Sun'.
     *
     * @param roundId the ID of the round.
     */
    void requestSunAutoCompile(int roundId);

    /**
     * Requests to retrieve system test results of the logged in user's submission for a round hosted by 'Sun'.
     *
     * @param roundId the ID of the round.
     */
    void requestSystestResults(int roundId);

    /**
     * Requests to mark the important message as read.
     *
     * @param messageId the ID of the message to be marked.
     */
    void requestReadMessage(int messageId);

    /**
     * Requests to set the default programming language for the logged in user.
     *
     * @param languageID the ID of the programming language.
     */
    void requestSetLanguage(int languageID);

    /**
     * Requests to retrieve all visited practice rooms for the logged in user.
     */
    void requestVisitedPractice();

    /**
     * Requests to retrieve verification data to verify the client is signed by TopCoder.
     *
     * @throws TimeOutException if there is no corresponding response before timing out.
     */
    void requestVerify() throws TimeOutException;

    /**
     * Requests to verify the result of the client signature verification.
     *
     * @param result the verification result.
     * @throws TimeOutException if there is no corresponding response before timing out.
     */
    void requestVerifyResult(int result) throws TimeOutException;

    /**
     * Reports a client-side exception to the server.
     *
     * @param error the exception occurred on the client-side.
     */
    void requestErrorReport(Throwable error);

    /**
     * Requests to retrieve the source code of a submission for a marathon problem component by a coder.
     *
     * @param roundId the ID of the marathon round.
     * @param handle the handle of the coder.
     * @param componentId the ID of the marathon problem component.
     * @param example <code>true</code> if example submission source should be retrieved; <code>false</code>
     *            otherwise.
     * @param submissionNumber the number of the submission whose source should be retrieved.
     * @param pretty a flag indicating if the source should be formatted.
     */
    void requestSourceCode(int roundId, String handle, int componentId, boolean example, int submissionNumber,
        boolean pretty);

    /**
     * Requests to retrieve the current testing queue for marathon submissions.
     */
    void requestViewQueueStatus();

    /**
     * Requests to exchange the partial encryption key used to encrypt sensitive data over the network, such as user's
     * password, hash for reconnection, etc. The key is used by a symmetric encryption algorithm.
     *
     * @param key the partial key to be exchanged.
     * @throws TimeOutException if there is no corresponding response before timing out.
     */
    void requestExchangeKey(byte[] key) throws TimeOutException;
}
