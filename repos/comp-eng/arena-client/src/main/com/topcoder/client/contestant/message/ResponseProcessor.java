/*
* Copyright (C) 2001 - 2014 TopCoder Inc., All Rights Reserved.
*/

package com.topcoder.client.contestant.message;

/**
 * ResponseProcessor.java
 *
 * Created on July 11, 2001 10:55 AM
 */

//import java.util.*;
//import com.topcoder.netCommon.contest.*;

import com.topcoder.netCommon.contestantMessages.response.AssignComponentsResponse;
import com.topcoder.netCommon.contestantMessages.response.BatchTestResponse;
import com.topcoder.netCommon.contestantMessages.response.ChallengeInfoResponse;
import com.topcoder.netCommon.contestantMessages.response.CoderHistoryResponse;
import com.topcoder.netCommon.contestantMessages.response.ComponentAssignmentDataResponse;
import com.topcoder.netCommon.contestantMessages.response.CreateCategoryListResponse;
import com.topcoder.netCommon.contestantMessages.response.CreateChallengeTableResponse;
import com.topcoder.netCommon.contestantMessages.response.CreateLeaderBoardResponse;
import com.topcoder.netCommon.contestantMessages.response.CreateMenuResponse;
import com.topcoder.netCommon.contestantMessages.response.CreateProblemsResponse;
import com.topcoder.netCommon.contestantMessages.response.CreateRoomListResponse;
import com.topcoder.netCommon.contestantMessages.response.CreateRoundListResponse;
import com.topcoder.netCommon.contestantMessages.response.CreateTeamListResponse;
import com.topcoder.netCommon.contestantMessages.response.CreateUserListResponse;
import com.topcoder.netCommon.contestantMessages.response.CreateVisitedPracticeResponse;
import com.topcoder.netCommon.contestantMessages.response.EnableRoundResponse;
import com.topcoder.netCommon.contestantMessages.response.ExchangeKeyResponse;
import com.topcoder.netCommon.contestantMessages.response.ForcedLogoutResponse;
import com.topcoder.netCommon.contestantMessages.response.GetAdminBroadcastResponse;
import com.topcoder.netCommon.contestantMessages.response.GetCurrentAppletVersionResponse;
import com.topcoder.netCommon.contestantMessages.response.GetImportantMessagesResponse;
import com.topcoder.netCommon.contestantMessages.response.GetProblemResponse;
import com.topcoder.netCommon.contestantMessages.response.GetTeamProblemResponse;
import com.topcoder.netCommon.contestantMessages.response.ImportantMessageResponse;
import com.topcoder.netCommon.contestantMessages.response.LoginResponse;
import com.topcoder.netCommon.contestantMessages.response.LongTestResultsResponse;
import com.topcoder.netCommon.contestantMessages.response.NoBadgeIdResponse;
import com.topcoder.netCommon.contestantMessages.response.OpenComponentResponse;
import com.topcoder.netCommon.contestantMessages.response.PhaseDataResponse;
import com.topcoder.netCommon.contestantMessages.response.PopUpGenericResponse;
import com.topcoder.netCommon.contestantMessages.response.PracticeSystemTestResponse;
import com.topcoder.netCommon.contestantMessages.response.PracticeSystemTestResultResponse;
import com.topcoder.netCommon.contestantMessages.response.ReconnectResponse;
import com.topcoder.netCommon.contestantMessages.response.RegisteredUsersResponse;
import com.topcoder.netCommon.contestantMessages.response.RoomInfoResponse;
import com.topcoder.netCommon.contestantMessages.response.RoundScheduleResponse;
import com.topcoder.netCommon.contestantMessages.response.RoundStatsResponse;
import com.topcoder.netCommon.contestantMessages.response.SingleBroadcastResponse;
import com.topcoder.netCommon.contestantMessages.response.SubmissionHistoryResponse;
import com.topcoder.netCommon.contestantMessages.response.SubmitResultsResponse;
import com.topcoder.netCommon.contestantMessages.response.SynchTimeResponse;
import com.topcoder.netCommon.contestantMessages.response.SystestProgressResponse;
import com.topcoder.netCommon.contestantMessages.response.TestInfoResponse;
import com.topcoder.netCommon.contestantMessages.response.UpdateChatResponse;
import com.topcoder.netCommon.contestantMessages.response.UpdateCoderComponentResponse;
import com.topcoder.netCommon.contestantMessages.response.UpdateCoderPointsResponse;
import com.topcoder.netCommon.contestantMessages.response.UpdateLeaderBoardResponse;
import com.topcoder.netCommon.contestantMessages.response.UpdateMenuResponse;
import com.topcoder.netCommon.contestantMessages.response.UpdatePreferencesResponse;
import com.topcoder.netCommon.contestantMessages.response.UpdateRoundListResponse;
import com.topcoder.netCommon.contestantMessages.response.UpdateTeamListResponse;
import com.topcoder.netCommon.contestantMessages.response.UpdateUserListResponse;
import com.topcoder.netCommon.contestantMessages.response.UserInfoResponse;
import com.topcoder.netCommon.contestantMessages.response.VerifyResponse;
import com.topcoder.netCommon.contestantMessages.response.VerifyResultResponse;
import com.topcoder.netCommon.contestantMessages.response.VoteResponse;
import com.topcoder.netCommon.contestantMessages.response.VoteResultsResponse;
import com.topcoder.netCommon.contestantMessages.response.WLMyTeamInfoResponse;
import com.topcoder.netCommon.contestantMessages.response.WLTeamsInfoResponse;
import com.topcoder.netCommon.contestantMessages.response.WatchResponse;

/**
 * Defines an interface which processes all response messages received from the server. The detailed
 * information of each response should be referred to the response message documentation.
 *
 * <p>
 * Changes in version 1.1 (Module Assembly - TopCoder Competition Engine - Batch Test):
 * <ol>
 *      <li>Added {@link #batchTest(BatchTestResponse)} method to handle {@link BatchTestResponse}.</li>
 * </ol>
 * </p>
 *
 * @author Alex Roman, dexy
 * @version 1.1
 */
public interface ResponseProcessor {
    /**
     * Processes the <code>GetCurrentAppletVersionResponse</code> message.
     *
     * @param response the message to be processed.
     */
    void currentAppletVersion(GetCurrentAppletVersionResponse response);

    /**
     * Processes the <code>ReconnectResponse</code> message.
     *
     * @param response the message to be processed.
     */
    void reconnect(ReconnectResponse response);

    /**
     * Processes the <code>LoginResponse</code> message.
     *
     * @param loginMessage the message to be processed.
     */
    void login(LoginResponse loginMessage);

    /**
     * Processes the <code>CreateMenuResponse</code> message.
     *
     * @param response the message to be processed.
     */
    void createMenu(CreateMenuResponse response);

    /**
     * Processes the <code>UpdateMenuResponse</code> message.
     *
     * @param response the message to be processed.
     */
    void updateMenu(UpdateMenuResponse response);

    /**
     * Processes the <code>UpdatePreferencesResponse</code> message.
     *
     * @param response the message to be processed.
     */
    void updatePreferences(UpdatePreferencesResponse response);

    /**
     * Processes the <code>WatchResponse</code> message.
     *
     * @param response the message to be processed.
     */
    void watch(WatchResponse response);

    /**
     * Processes the <code>CreateLeaderBoardResponse</code> message.
     *
     * @param response the message to be processed.
     */
    void createLeaderBoard(CreateLeaderBoardResponse response);

    /**
     * Processes the <code>UpdateLeaderBoardResponse</code> message.
     *
     * @param response the message to be processed.
     */
    void updateLeaderBoard(UpdateLeaderBoardResponse response);

    /**
     * Processes the <code>CreateUserListResponse</code> message.
     *
     * @param response the message to be processed.
     */
    void createUserList(CreateUserListResponse response);

    /**
     * Processes the <code>UpdateUserListResponse</code> message.
     *
     * @param response the message to be processed.
     */
    void updateUserList(UpdateUserListResponse response);

    /**
     * Processes the <code>UpdateChatResponse</code> message.
     *
     * @param chatResponse the message to be processed.
     */
    void updateChat(UpdateChatResponse chatResponse);

    /**
     * Processes the <code>GetProblemResponse</code> message.
     *
     * @param response the message to be processed.
     */
    void getProblem(GetProblemResponse response);

    /**
     * Processes the <code>GetTeamProblemResponse</code> message.
     *
     * @param response the message to be processed.
     */
    void getTeamProblem(GetTeamProblemResponse response);

    /**
     * Processes the <code>TestInfoResponse</code> message.
     *
     * @param response the message to be processed.
     */
    void testInfo(TestInfoResponse response);

    /**
     * Processes the {@link BatchTestResponse} message.
     *
     * @param response the message to be processed.
     * @since 1.1
     */
    void batchTest(BatchTestResponse response);

    /**
     * Processes the <code>CreateProblemsResponse</code> message.
     *
     * @param response the message to be processed.
     */
    void createProblems(CreateProblemsResponse response);

    /**
     * Processes the <code>AssignComponentsResponse</code> message.
     *
     * @param response the message to be processed.
     */
    void assignComponents(AssignComponentsResponse response);

    /**
     * Processes the <code>ChallengeInfoResponse</code> message.
     *
     * @param response the message to be processed.
     */
    void challengeInfo(ChallengeInfoResponse response);

    /**
     * Processes the <code>UpdateCoderComponentResponse</code> message.
     *
     * @param response the message to be processed.
     */
    void updateCoderComponent(UpdateCoderComponentResponse response);

    /**
     * Processes the <code>UpdateCoderPointsResponse</code> message.
     *
     * @param response the message to be processed.
     */
    void updateCoderPoints(UpdateCoderPointsResponse response);

    /**
     * Processes the <code>CreateChallengeTableResponse</code> message.
     *
     * @param response the message to be processed.
     */
    void createChallengeTable(CreateChallengeTableResponse response);

    /**
     * Processes the <code>PopUpGenericResponse</code> message.
     *
     * @param response the message to be processed.
     */
    void popupGeneric(PopUpGenericResponse response);

    /**
     * Processes the <code>ForcedLogoutResponse</code> message.
     *
     * @param response the message to be processed.
     */
    void forceLogoff(ForcedLogoutResponse response);

    /**
     * Processes the <code>RoomInfoResponse</code> message.
     *
     * @param response the message to be processed.
     */
    void roomType(RoomInfoResponse response);

    /**
     * Processes the <code>GetAdminBroadcastResponse</code> message.
     *
     * @param response the message to be processed.
     */
    void getAdminBroadcast(GetAdminBroadcastResponse response);

    /**
     * Processes the <code>SingleBroadcastResponse</code> message.
     *
     * @param response the message to be processed.
     */
    void singleBroadcast(SingleBroadcastResponse response);

    /**
     * Processes the <code>UserInfoResponse</code> message.
     *
     * @param response the message to be processed.
     */
    void userInfo(UserInfoResponse response);

    /**
     * Processes the <code>CreateTeamListResponse</code> message.
     *
     * @param response the message to be processed.
     */
    void createTeamList(CreateTeamListResponse response);

    /**
     * Processes the <code>UpdateTeamListResponse</code> message.
     *
     * @param response the message to be processed.
     */
    void updateTeamList(UpdateTeamListResponse response);

    /**
     * Processes the <code>CreateRoundListResponse</code> message.
     *
     * @param response the message to be processed.
     */
    void createRoundList(CreateRoundListResponse response);

    /**
     * Processes the <code>CreateCategoryListResponse</code> message.
     *
     * @param response the message to be processed.
     */
    void createCategoryList(CreateCategoryListResponse response);

    /**
     * Processes the <code>EnableRoundResponse</code> message.
     *
     * @param response the message to be processed.
     */
    void enableRound(EnableRoundResponse response);

    /**
     * Processes the <code>UpdateRoundListResponse</code> message.
     *
     * @param response the message to be processed.
     */
    void updateRoundList(UpdateRoundListResponse response);

    /**
     * Processes the <code>CreateRoomListResponse</code> message.
     *
     * @param response the message to be processed.
     */
    void createRoomList(CreateRoomListResponse response);

    /**
     * Processes the <code>PhaseDataResponse</code> message.
     *
     * @param response the message to be processed.
     */
    void phaseData(PhaseDataResponse response);

    /**
     * Processes the <code>SynchTimeResponse</code> message.
     *
     * @param response the message to be processed.
     */
    void synchTime(SynchTimeResponse response);

    /**
     * Processes the <code>SystestProgressResponse</code> message.
     *
     * @param response the message to be processed.
     */
    void systestProgress(SystestProgressResponse response);

    /**
     * Processes the <code>OpenComponentResponse</code> message.
     *
     * @param response the message to be processed.
     */
    void openComponent(OpenComponentResponse response);

    /**
     * Processes the <code>RegisteredUsersResponse</code> message.
     *
     * @param response the message to be processed.
     */
    void registeredUsers(RegisteredUsersResponse response);

    /**
     * Processes the <code>RoundScheduleResponse</code> message.
     *
     * @param response the message to be processed.
     */
    void roundSchedule(RoundScheduleResponse response);

    /**
     * Processes the <code>VoteResponse</code> message.
     *
     * @param voteResponse the message to be processed.
     */
    void vote(VoteResponse voteResponse);

    /**
     * Processes the <code>VoteResultsResponse</code> message.
     *
     * @param voteResultsResponse the message to be processed.
     */
    void voteResults(VoteResultsResponse voteResultsResponse);

    /**
     * Processes the <code>RoundStatsResponse</code> message.
     *
     * @param roundStatsResponse the message to be processed.
     */
    void roundStatsResponse(RoundStatsResponse roundStatsResponse);

    /**
     * Processes the <code>NoBadgeIdResponse</code> message.
     *
     * @param noBadgeIdResponse the message to be processed.
     */
    void noBadgeId(NoBadgeIdResponse noBadgeIdResponse);

    /**
     * Processes the <code>WLMyTeamInfoResponse</code> message.
     *
     * @param wlTeamInfoResponse the message to be processed.
     */
    void wlMyTeamInfoResponse(WLMyTeamInfoResponse wlTeamInfoResponse);

    /**
     * Processes the <code>WLTeamsInfoResponse</code> message.
     *
     * @param wlTeamsInfoResponse the message to be processed.
     */
    void wlTeamsInfoResponse(WLTeamsInfoResponse wlTeamsInfoResponse);

    /**
     * Processes the <code>ComponentAssignmentDataResponse</code> message.
     *
     * @param response the message to be processed.
     */
    void updateAssignedComponents(ComponentAssignmentDataResponse response);

    /**
     * Processes the <code>SubmitResultsResponse</code> message.
     *
     * @param response the message to be processed.
     */
    void submitResponse(SubmitResultsResponse response);

    /**
     * Processes the <code>ImportantMessageResponse</code> message.
     *
     * @param response the message to be processed.
     */
    void importantMessage(ImportantMessageResponse response);

    /**
     * Processes the <code>GetImportantMessagesResponse</code> message.
     *
     * @param response the message to be processed.
     */
    void getImportantMessages(GetImportantMessagesResponse response);

    /**
     * Processes the <code>CreateVisitedPracticeResponse</code> message.
     *
     * @param response the message to be processed.
     */
    void visitedPracticeList(CreateVisitedPracticeResponse response);

    /**
     * Processes the <code>PracticeSystemTestResponse</code> message.
     *
     * @param response the message to be processed.
     */
    void practiceSystemTestResponse(PracticeSystemTestResponse response);

    /**
     * Processes the <code>PracticeSystemTestResultResponse</code> message.
     *
     * @param response the message to be processed.
     */
    void practiceSystemTestResult(PracticeSystemTestResultResponse response);

    /**
     * Processes the <code>VerifyResponse</code> message.
     *
     * @param response the message to be processed.
     */
    void verifyResponse(VerifyResponse response);

    /**
     * Processes the <code>VerifyResultResponse</code> message.
     *
     * @param response the message to be processed.
     */
    void verifyResult(VerifyResultResponse response);

    /**
     * Processes the <code>SubmissionHistoryResponse</code> message.
     *
     * @param response the message to be processed.
     */
    void submissionHistory(SubmissionHistoryResponse response);

    /**
     * Processes the <code>LongTestResultsResponse</code> message.
     *
     * @param response the message to be processed.
     */
    void longTestResults(LongTestResultsResponse response);

    /**
     * Processes the <code>CoderHistoryResponse</code> message.
     *
     * @param response the message to be processed.
     */
    void coderHistory(CoderHistoryResponse response);

    /**
     * Processes the <code>ExchangeKeyResponse</code> message.
     *
     * @param response the message to be processed.
     */
    void exchangeKey(ExchangeKeyResponse response);
}
