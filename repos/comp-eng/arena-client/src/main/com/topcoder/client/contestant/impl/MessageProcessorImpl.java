/*
* Copyright (C)  - 2014 TopCoder Inc., All Rights Reserved.
*/

package com.topcoder.client.contestant.impl;

/**
 * MessageProcesorImpl.java
 *
 */

import java.io.IOException;

import com.topcoder.client.contestant.InterceptorManager;
import com.topcoder.client.contestant.message.MessageProcessor;
import com.topcoder.client.contestant.message.ResponseProcessor;
import com.topcoder.client.contestant.view.ContestantView;
import com.topcoder.client.netClient.Client;
import com.topcoder.netCommon.contestantMessages.response.AssignComponentsResponse;
import com.topcoder.netCommon.contestantMessages.response.BaseResponse;
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

//import java.net.URL;
//import java.net.HttpURLConnection;


/**
 * This class manages the client connection and sends/receives the socket
 * request/responses for the Contest applet.
 *
 * <p>
 * Changes in version 1.1 (Module Assembly - TopCoder Competition Engine - Batch Test):
 * <ol>
 *      <li>Updated {@link #receive(BaseResponse)} method to handle {@link BatchTestResponse}.</li>
 * </ol>
 * </p>
 *
 * @author Alex Roman, dexy
 * @version 1.1
 */
class MessageProcessorImpl implements MessageProcessor {
    private Client client = null;
    private String host = null;
    private String tunnel = null;
    private int port;
    private ContestantView view;
    private ResponseProcessor responseProcessor;
    private String destinationHost = null;
    private InterceptorManager interceptorManager;


    /**
     * Default Constructor to initialize the MessageProcessor.
     *
     */
    ////////////////////////////////////////////////////////////////////////////////
    MessageProcessorImpl(String h, int p, String t,ContestantView v, ResponseProcessor rp, String dH, InterceptorManager interceptorManager)
        ////////////////////////////////////////////////////////////////////////////////
    {
        this.host = h;
        this.port = p;
        this.view = v;
        this.tunnel = t;
        this.responseProcessor = rp;
        this.destinationHost = dH;
        this.interceptorManager = interceptorManager;
    }


    /**
     * Open a connection to the contest applet socket server.
     *
     * @return   a boolean signifying whether the connection opened successfully.
     */
    ////////////////////////////////////////////////////////////////////////////////
    public boolean openConnection(boolean doHTTPTunnelling)
        ////////////////////////////////////////////////////////////////////////////////
    {
        return this.openConnection(doHTTPTunnelling, false, false);
    }

    /**
     * Open a connection to the contest applet socket server.
     *
     * @return   a boolean signifying whether the connection opened successfully.
     */
    ////////////////////////////////////////////////////////////////////////////////
    public boolean openConnection(boolean doHTTPTunnelling, boolean goThroughProxy, boolean useSSL)
        ////////////////////////////////////////////////////////////////////////////////
    {
        boolean status = true;
        if (client==null) {
            try {
                if (goThroughProxy) {
                    client = new Client(host, port, destinationHost);
                } else {
                    if (doHTTPTunnelling) {
                        client = new Client(tunnel, useSSL);
                    } else {
                        client = new Client(host, port, useSSL);
                    }
                }
                client.initContestResponseHandler(this);
            } catch (IOException e) {
                System.out.println("FAILED TO ESTABLISH A CONNECTION TO THE SOCKET SERVER");
                status = false;
            }
        }
        return status;
    }
    /**
     * If "something" should happen and the connection to the socket server breaks,
     * gracefully close the connection and notify the applet to save any remaining
     * work and reload the Login room.
     */
    ////////////////////////////////////////////////////////////////////////////////
    public void lostConnection()
        ////////////////////////////////////////////////////////////////////////////////
    {
        closeConnection();
        view.lostConnectionEvent();
    }

    /**
     * Manually close the connection to the applet server. But before doing so
     * make sure the user can save what he/she is working on.
     */
    ////////////////////////////////////////////////////////////////////////////////
    public void closeConnection()
        ////////////////////////////////////////////////////////////////////////////////
    {
        view.closingConnectionEvent();

        // close the connection to the socket server.
        if (client != null) {
            client.close();
            client = null;
        }
    }

    /**
     * Get a handle to the client.
     *
     * @return     a handle to the Client object.
     */
    ////////////////////////////////////////////////////////////////////////////////
    public Client getClient()
        ////////////////////////////////////////////////////////////////////////////////
    {
        return (client);
    }


    /**
     * Process an incoming response from the server and give it
     * to the client.
     *
     * @param  response a Response object containing the information that needs to be sent
     *         to the server.
     */
    ////////////////////////////////////////////////////////////////////////////////
    public void receive(BaseResponse response)
        ////////////////////////////////////////////////////////////////////////////////
    {
        // Allow an interceptor first dibs on the message...
        if (interceptorManager != null && interceptorManager.receiveMessage(response)) {
            return;
        }

        //System.out.println("reading ***********\n"+response);
        if (response instanceof LoginResponse) {
            responseProcessor.login((LoginResponse) response);
        } else if (response instanceof GetCurrentAppletVersionResponse) {
            responseProcessor.currentAppletVersion((GetCurrentAppletVersionResponse) response);
        } else if (response instanceof ReconnectResponse) {
            responseProcessor.reconnect((ReconnectResponse) response);
        } else if (response instanceof CreateMenuResponse) {
            responseProcessor.createMenu((CreateMenuResponse) response);
        } else if (response instanceof UpdateMenuResponse) {
            responseProcessor.updateMenu((UpdateMenuResponse) response);
        } else if (response instanceof CreateLeaderBoardResponse) {
            responseProcessor.createLeaderBoard((CreateLeaderBoardResponse) response);
        } else if (response instanceof UpdateLeaderBoardResponse) {
            responseProcessor.updateLeaderBoard((UpdateLeaderBoardResponse) response);
        } else if (response instanceof UpdatePreferencesResponse) {
            responseProcessor.updatePreferences((UpdatePreferencesResponse) response);
        } else if (response instanceof CreateUserListResponse) {
            responseProcessor.createUserList((CreateUserListResponse) response);
        } else if (response instanceof UpdateUserListResponse) {
            responseProcessor.updateUserList((UpdateUserListResponse) response);
        } else if (response instanceof PopUpGenericResponse) {
            responseProcessor.popupGeneric((PopUpGenericResponse) response);
        } else if (response instanceof SubmitResultsResponse) {
            responseProcessor.submitResponse((SubmitResultsResponse) response);
        } else if (response instanceof ForcedLogoutResponse) {
            responseProcessor.forceLogoff((ForcedLogoutResponse) response);
        } else if (response instanceof CreateChallengeTableResponse) {
            responseProcessor.createChallengeTable((CreateChallengeTableResponse) response);
        } else if (response instanceof UpdateCoderComponentResponse) {
            responseProcessor.updateCoderComponent((UpdateCoderComponentResponse) response);
        } else if (response instanceof UpdateCoderPointsResponse) {
            responseProcessor.updateCoderPoints((UpdateCoderPointsResponse) response);
        } else if (response instanceof UpdateChatResponse) {
            responseProcessor.updateChat((UpdateChatResponse) response);
        } else if (response instanceof GetProblemResponse) {
            responseProcessor.getProblem((GetProblemResponse) response);
        } else if (response instanceof GetTeamProblemResponse) {
            responseProcessor.getTeamProblem((GetTeamProblemResponse) response);
        } else if (response instanceof TestInfoResponse) {
            responseProcessor.testInfo((TestInfoResponse) response);
        } else if (response instanceof BatchTestResponse) {
            responseProcessor.batchTest((BatchTestResponse) response);
        } else if (response instanceof ChallengeInfoResponse) {
            responseProcessor.challengeInfo((ChallengeInfoResponse) response);
        } else if (response instanceof RoomInfoResponse) {
            responseProcessor.roomType((RoomInfoResponse) response);
        } else if (response instanceof CreateProblemsResponse) {
            responseProcessor.createProblems((CreateProblemsResponse) response);
        } else if (response instanceof AssignComponentsResponse) {
            responseProcessor.assignComponents((AssignComponentsResponse) response);
        } else if (response instanceof GetAdminBroadcastResponse) {
            responseProcessor.getAdminBroadcast((GetAdminBroadcastResponse) response);
        } else if (response instanceof SingleBroadcastResponse) {
            responseProcessor.singleBroadcast((SingleBroadcastResponse) response);
        } else if (response instanceof UserInfoResponse) {
            responseProcessor.userInfo((UserInfoResponse) response);
        } else if (response instanceof WatchResponse) {
            responseProcessor.watch((WatchResponse) response);
        } else if (response instanceof ComponentAssignmentDataResponse) {
            responseProcessor.updateAssignedComponents((ComponentAssignmentDataResponse) response);
        } else if (response instanceof KeepAliveResponse) {
        } else if (response instanceof SystestProgressResponse) {
            responseProcessor.systestProgress((SystestProgressResponse) response);
        } else if (response instanceof UpdateTeamListResponse) {
            responseProcessor.updateTeamList((UpdateTeamListResponse) response);
        } else if (response instanceof CreateTeamListResponse) {
            responseProcessor.createTeamList((CreateTeamListResponse) response);
        } else if (response instanceof CreateRoundListResponse) {
            responseProcessor.createRoundList((CreateRoundListResponse) response);
        } else if (response instanceof CreateCategoryListResponse) {
            responseProcessor.createCategoryList((CreateCategoryListResponse) response);
        } else if (response instanceof EnableRoundResponse) {
            responseProcessor.enableRound((EnableRoundResponse) response);
        } else if (response instanceof CreateRoomListResponse) {
            responseProcessor.createRoomList((CreateRoomListResponse) response);
        } else if (response instanceof UpdateRoundListResponse) {
            responseProcessor.updateRoundList((UpdateRoundListResponse) response);
        } else if (response instanceof PhaseDataResponse) {
            responseProcessor.phaseData((PhaseDataResponse) response);
        } else if (response instanceof SynchTimeResponse) {
            responseProcessor.synchTime((SynchTimeResponse) response);
        } else if (response instanceof OpenComponentResponse) {
            responseProcessor.openComponent((OpenComponentResponse) response);
        } else if (response instanceof RegisteredUsersResponse) {
            responseProcessor.registeredUsers((RegisteredUsersResponse) response);
        } else if (response instanceof RoundScheduleResponse) {
            responseProcessor.roundSchedule((RoundScheduleResponse) response);
        } else if (response instanceof VoteResponse) {
            responseProcessor.vote((VoteResponse) response);
        } else if (response instanceof VoteResultsResponse) {
            responseProcessor.voteResults((VoteResultsResponse) response);
        } else if (response instanceof RoundStatsResponse) {
            responseProcessor.roundStatsResponse((RoundStatsResponse) response);
        } else if (response instanceof NoBadgeIdResponse) {
            responseProcessor.noBadgeId((NoBadgeIdResponse) response);
        } else if (response instanceof WLMyTeamInfoResponse) {
            responseProcessor.wlMyTeamInfoResponse((WLMyTeamInfoResponse) response);
        } else if (response instanceof WLTeamsInfoResponse) {
            responseProcessor.wlTeamsInfoResponse((WLTeamsInfoResponse) response);
        } else if (response instanceof ImportantMessageResponse) {
            responseProcessor.importantMessage((ImportantMessageResponse) response);
        } else if (response instanceof GetImportantMessagesResponse) {
            responseProcessor.getImportantMessages((GetImportantMessagesResponse) response);
        } else if (response instanceof CreateVisitedPracticeResponse) {
            responseProcessor.visitedPracticeList((CreateVisitedPracticeResponse) response);
        } else if (response instanceof PracticeSystemTestResultResponse) {
            responseProcessor.practiceSystemTestResult((PracticeSystemTestResultResponse) response);
        } else if (response instanceof PracticeSystemTestResponse) {
            responseProcessor.practiceSystemTestResponse((PracticeSystemTestResponse) response);
        } else if (response instanceof VerifyResponse) {
            responseProcessor.verifyResponse((VerifyResponse) response);
        } else if (response instanceof VerifyResultResponse) {
            responseProcessor.verifyResult((VerifyResultResponse) response);
        } else if (response instanceof SubmissionHistoryResponse) {
            responseProcessor.submissionHistory((SubmissionHistoryResponse) response);
        } else if (response instanceof LongTestResultsResponse) {
            responseProcessor.longTestResults((LongTestResultsResponse) response);
        } else if (response instanceof CoderHistoryResponse) {
            responseProcessor.coderHistory((CoderHistoryResponse) response);
        } else if (response instanceof ExchangeKeyResponse) {
            responseProcessor.exchangeKey((ExchangeKeyResponse) response);
        } else {
            System.err.println("MessageProcessorImpl, Unrecognized response: " + response);
        }
    }
}
