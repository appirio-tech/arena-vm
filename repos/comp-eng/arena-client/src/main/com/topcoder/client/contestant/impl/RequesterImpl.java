/*
* Copyright (C)  - 2014 TopCoder Inc., All Rights Reserved.
*/

package com.topcoder.client.contestant.impl;

import java.util.ArrayList;

import com.topcoder.client.contestant.InterceptorManager;
import com.topcoder.client.contestant.TimeOutException;
import com.topcoder.client.contestant.message.Requester;
import com.topcoder.client.netClient.Client;
import com.topcoder.client.netClient.RequestTimedOutException;
import com.topcoder.netCommon.contest.ComponentAssignmentData;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contestantMessages.request.ActiveUsersRequest;
import com.topcoder.netCommon.contestantMessages.request.AddTeamMemberRequest;
import com.topcoder.netCommon.contestantMessages.request.AssignComponentsRequest;
import com.topcoder.netCommon.contestantMessages.request.AutoSystestRequest;
import com.topcoder.netCommon.contestantMessages.request.BaseRequest;
import com.topcoder.netCommon.contestantMessages.request.BatchTestRequest;
import com.topcoder.netCommon.contestantMessages.request.ChallengeRequest;
import com.topcoder.netCommon.contestantMessages.request.ChatRequest;
import com.topcoder.netCommon.contestantMessages.request.ClearPracticeProblemRequest;
import com.topcoder.netCommon.contestantMessages.request.ClearPracticeRequest;
import com.topcoder.netCommon.contestantMessages.request.CloseDivSummaryRequest;
import com.topcoder.netCommon.contestantMessages.request.CloseLeaderBoardRequest;
import com.topcoder.netCommon.contestantMessages.request.CloseProblemRequest;
import com.topcoder.netCommon.contestantMessages.request.CloseSummaryRequest;
import com.topcoder.netCommon.contestantMessages.request.CoderHistoryRequest;
import com.topcoder.netCommon.contestantMessages.request.CoderInfoRequest;
import com.topcoder.netCommon.contestantMessages.request.CompileRequest;
import com.topcoder.netCommon.contestantMessages.request.DivSummaryRequest;
import com.topcoder.netCommon.contestantMessages.request.EnterRequest;
import com.topcoder.netCommon.contestantMessages.request.EnterRoundRequest;
import com.topcoder.netCommon.contestantMessages.request.ErrorReportRequest;
import com.topcoder.netCommon.contestantMessages.request.ExchangeKeyRequest;
import com.topcoder.netCommon.contestantMessages.request.GenericPopupRequest;
import com.topcoder.netCommon.contestantMessages.request.GetAdminBroadcastsRequest;
import com.topcoder.netCommon.contestantMessages.request.GetChallengeProblemRequest;
import com.topcoder.netCommon.contestantMessages.request.GetCurrentAppletVersionRequest;
import com.topcoder.netCommon.contestantMessages.request.GetImportantMessagesRequest;
import com.topcoder.netCommon.contestantMessages.request.GetLeaderBoardRequest;
import com.topcoder.netCommon.contestantMessages.request.GetSourceCodeRequest;
import com.topcoder.netCommon.contestantMessages.request.JoinTeamRequest;
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
import com.topcoder.netCommon.contestantMessages.request.RegisterUsersRequest;
import com.topcoder.netCommon.contestantMessages.request.RemoveTeamMemberRequest;
import com.topcoder.netCommon.contestantMessages.request.RoundStatsRequest;
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
import com.topcoder.shared.netCommon.SealedSerializable;

/**
 * <p>Title: Requester</p>
 * <p>Description: Handles construction sending of Request objects to server.</p>
 *
 * <p>
 * Changes in version 1.1 (Module Assembly - TopCoder Competition Engine - Batch Test):
 * <ol>
 *      <li>Added {@link #requestBatchTest(ArrayList, long)} method to send {@link BatchTestRequest}
 *      to the server.</li>
 * </ol>
 * </p>
 *
 * @author Walter Mundt, dexy
 * @version 1.1
 */
final class RequesterImpl implements Requester {
    private Client client;
    private InterceptorManager interceptorManager = null;

    public void requestCurrentAppletVersion() throws TimeOutException {
        GetCurrentAppletVersionRequest request = new GetCurrentAppletVersionRequest();

        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        try {
            if (client != null) {
                client.sendSynchRequest(request);
            }
        } catch (RequestTimedOutException e) {
            throw new TimeOutException(e);
        }
    }

    public void requestSynchTime(long connectionID) throws TimeOutException {
        SynchTimeRequest request = new SynchTimeRequest(connectionID);

        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        try {
            if (client != null) {
                client.sendSynchRequest(request);
            }
        } catch (RequestTimedOutException e) {
            throw new TimeOutException(e);
        }
    }

    public void requestReconnect(SealedSerializable hash, long connectionID) throws TimeOutException {
        ReconnectRequest request = new ReconnectRequest(connectionID, hash);

        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        try {
            client.sendSynchRequest(request);
        } catch (RequestTimedOutException e) {
            throw new TimeOutException(e);
        }
    }

    public void requestLogin(String username, SealedSerializable password, String tcHandle, String badgeId, String firstName, String lastName,
                             String email, String companyName, String phoneNumber) throws TimeOutException {
        LoginRequest request = new LoginRequest(username, password, tcHandle, ContestConstants.LOGIN, badgeId, firstName, lastName,
                                                email, companyName, phoneNumber);

        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        try {
            client.sendSynchRequest(request);
        } catch (RequestTimedOutException e) {
            throw new TimeOutException(e);
        }
    }

    public void requestGuestLogin() throws TimeOutException {
        LoginRequest request = new LoginRequest("Guest", null, ContestConstants.GUEST_LOGIN);

        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        try {
            client.sendSynchRequest(request);
        } catch (RequestTimedOutException e) {
            throw new TimeOutException(e);
        }
    }

    public void requestDivSummary(long roundID, long divisionID) throws TimeOutException
    {
        DivSummaryRequest request = new DivSummaryRequest((int) roundID, (int)divisionID);

        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        try {
            client.sendRequest(request);
        } catch (Exception e) {
            throw new TimeOutException(e);
        }
    }

    public void requestWatch(long roomID) throws TimeOutException {
        WatchRequest request = new WatchRequest((int) roomID);

        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        try {
            client.sendSynchRequest(request);
        } catch (RequestTimedOutException e) {
            throw new TimeOutException(e);
        }
    }

    public void requestLogoff() {
        LogoutRequest request = new LogoutRequest();

        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        if (client != null) {
            client.sendRequest(request);
        }
    }

    public void requestMove(int roomType, long roomID) throws TimeOutException {
        MoveRequest request = new MoveRequest(roomType, (int) roomID);

        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        try {
            client.sendSynchRequest(request);
        } catch (RequestTimedOutException e) {
            throw new TimeOutException(e);
        }
    }

    public void requestEnterRound(long roundID) throws TimeOutException {
        EnterRoundRequest request = new EnterRoundRequest(roundID);

        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        try {
            client.sendSynchRequest(request);
        } catch (RequestTimedOutException e) {
            throw new TimeOutException(e);
        }
    }

    public void requestCompile(String code, int language, long componentID) {
        CompileRequest request = new CompileRequest(language, (int) componentID, code);

        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        client.sendRequest(request);
    }

    public void requestTest(ArrayList test, long componentID) {
        TestRequest request = new TestRequest(test, (int) componentID);

        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        client.sendRequest(request);
    }

    /**
     * Requests batch testing.
     *
     * @param test list of tests
     * @param componentID the component id to be tested
     * @since 1.1
     */
    public void requestBatchTest(ArrayList test, long componentID) {
        BatchTestRequest request = new BatchTestRequest(test, (int) componentID);

        // See if any interceptor excepts it first
        if (interceptorManager != null && interceptorManager.sendMessage(request)) {
            return;
        }
        client.sendRequest(request);
    }


    public void requestChallenge(String def, long componentID, ArrayList test) {
        ChallengeRequest request = new ChallengeRequest((int) componentID, test, def);

        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        client.sendRequest(request);
    }

    public void requestSubmitCode(long componentID) {
        SubmitRequest request = new SubmitRequest((int) componentID);

        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        client.sendRequest(request);
    }

    public void requestChatMessage(long roomID, String msg, int scope) {
        ChatRequest request = new ChatRequest(msg, (int) roomID, scope);

        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        client.sendRequest(request);
    }

    public void requestCoderHistory(String handle, long roomID, int userType) {
        CoderHistoryRequest request = new CoderHistoryRequest(handle, (int) roomID, userType);

        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        client.sendRequest(request);
    }


    public void requestSubmissionHistory(String handle, long roomID, int userType, boolean exampleSubmission) {
        int type = exampleSubmission ? CoderHistoryRequest.TYPE_SUBMISSIONS_EXAMPLE : CoderHistoryRequest.TYPE_SUBMISSIONS_NON_EXAMPLE;
        CoderHistoryRequest request = new CoderHistoryRequest(handle, (int) roomID, userType, type);

        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        client.sendRequest(request);
    }

    public void requestCoderInfo(String coder, int userType) {
        CoderInfoRequest request = new CoderInfoRequest(coder, userType);

        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        client.sendRequest(request);
    }

    public void requestEnter(long roomID) {
        EnterRequest request = new EnterRequest((int) roomID);

        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        client.sendRequest(request);
    }

    public void requestOpenComponentForCoding(long id) {
        OpenComponentForCodingRequest request = new OpenComponentForCodingRequest((int) id);

        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        client.sendRequest(request);
    }

    public void requestOpenProblemForReading(long roundId, long problemId) {
        OpenProblemForReadingRequest request = new OpenProblemForReadingRequest((int) roundId, (int) problemId);

        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        client.sendRequest(request);
    }

    public void requestCloseComponent(long componentID, String writer) {
        CloseProblemRequest request = new CloseProblemRequest((int) componentID, writer);
        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        //this is for dangling references after restarting as an applet
        //static classes suck
        if(client != null)
            client.sendRequest(request);
    }

    public void requestSave(long componentID, String code, int languageID) {
        SaveRequest request = new SaveRequest(code, (int) componentID, languageID);

        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        client.sendRequest(request);
    }

    public void requestSubmitLong(long componentID, String code, int languageID, boolean example) {
        LongSubmitRequest request = new LongSubmitRequest(code, (int) componentID, languageID, example);

        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        client.sendRequest(request);
    }

    public void requestLongTestResults(long componentID, long roomID, String handle, int resultsType) {
        LongTestResultsRequest request = new LongTestResultsRequest((int) componentID, (int) roomID, handle, resultsType);

        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        client.sendRequest(request);
    }

    public void requestSearch(String search) {
        SearchRequest request = new SearchRequest(search);

        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        client.sendRequest(request);
    }

    public void requestSetLanguage(int languageID) {
        SetLanguageRequest request = new SetLanguageRequest(languageID);

        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        client.sendRequest(request);
    }

    ////////////////////////////////////////////////////////////////////////////////
    //    public void requestChallengeInfo(String handle, long componentID)
    //            ////////////////////////////////////////////////////////////////////////////////
    //    {
    //        ChallengeInfoRequest request = new ChallengeInfoRequest(handle, (int) componentID);
    //        client.sendRequest(request);
    //    }

    public void requestUnwatch(long roomID) {

        UnwatchRequest request = new UnwatchRequest((int) roomID);

        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        client.sendRequest(request);
    }

    public void requestCloseDivSummary(long roundID, long divisionID)
    {
        CloseDivSummaryRequest request = new CloseDivSummaryRequest((int) roundID, (int)divisionID);

        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        client.sendRequest(request);
    }

    public void requestClearPractice(long roomID) {

        ClearPracticeRequest request = new ClearPracticeRequest((int) roomID);

        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        client.sendRequest(request);
    }

    public void requestClearPracticeProblem(long roomID, Long[] componentID) {

        ClearPracticeProblemRequest request = new ClearPracticeProblemRequest((int) roomID, componentID);

        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        client.sendRequest(request);
    }

    /*
      public void requestError() {
      client.sendRequest(new ErrorRequest());
      }
    */

    public void requestChallengeComponent(long componentID, boolean pretty, long roomID, String defender) {
        GetChallengeProblemRequest request = new GetChallengeProblemRequest(defender, (int) componentID, pretty, (int) roomID);

        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        client.sendRequest(request);
    }

    public void requestSourceCode(int roundId, String handle, int componentId, boolean example, int submissionNumber, boolean pretty) {
        BaseRequest request = new GetSourceCodeRequest(roundId, handle, componentId, example, submissionNumber, pretty);

        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        client.sendRequest(request);
    }

    public void requestViewQueueStatus() {
        BaseRequest request = new ViewQueueRequest();

        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        client.sendRequest(request);
    }

    public void requestActiveUsers() {
        ActiveUsersRequest request = new ActiveUsersRequest();

        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        client.sendRequest(request);
    }

    public void requestPracticeSystemTest(long roomID, int[] componentsId) {
        PracticeSystemTestRequest request = new PracticeSystemTestRequest((int) roomID, componentsId);

        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        client.sendRequest(request);
    }

    public void requestGetAdminBroadcast() {
        GetAdminBroadcastsRequest request = new GetAdminBroadcastsRequest();

        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        client.sendRequest(request);
    }

    public void requestRegisterEventInfo(long roundID) {
        RegisterInfoRequest request = new RegisterInfoRequest((int) roundID);

        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        client.sendRequest(request);
    }


    public void requestRegister(long roundID, ArrayList surveyData) {
        RegisterRequest request = new RegisterRequest(surveyData, (int) roundID);

        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        client.sendRequest(request);
    }

    public void requestRegisterUsers(long roundID) {
        RegisterUsersRequest request = new RegisterUsersRequest((int) roundID);

        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        client.sendRequest(request);
    }

    public void requestTestInfo(long componentID) {
        TestInfoRequest request = new TestInfoRequest((int) componentID);
        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        client.sendRequest(request);
    }

    public void requestToggleChat() {
        ToggleChatRequest request = new ToggleChatRequest();

        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        client.sendRequest(request);
    }

    public void requestPopupGeneric(int type, int button, ArrayList surveyData) {
        if (surveyData == null) surveyData = new ArrayList();
        GenericPopupRequest request = new GenericPopupRequest(type, button, surveyData);
        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        client.sendRequest(request);
    }

    public void requestGetLeaderBoard() {
        GetLeaderBoardRequest request = new GetLeaderBoardRequest();
        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        client.sendRequest(request);
    }

    public void requestCloseLeaderBoard() {
        CloseLeaderBoardRequest request = new CloseLeaderBoardRequest();
        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        if (client != null) {
            client.sendRequest(request);
        }
    }

    public void requestOpenSummary(long roomID) {
        OpenSummaryRequest request = new OpenSummaryRequest((int) roomID);
        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        client.sendRequest(request);
    }

    public void requestCloseSummary(long roomID) {
        CloseSummaryRequest request = new CloseSummaryRequest((int) roomID);
        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        if (client != null) {
            client.sendRequest(request);
        }
    }

    /*
      public void requestGetTeamList() {
      //todo mike I don't know if this needs to change
      client.sendRequest(new GetTeamListRequest());
      }

      public void requestCloseTeamList() {
      //todo mike I don't know if this needs to change
      client.sendRequest(new CloseTeamListRequest());
      }
    */

    /**
     * Requests membership on a team.
     *
     *  Matthew P. Suhocki (msuhocki)
     *
     * @param teamName  Handle of the team to join
     */
    public void requestJoinTeam(String teamName) {
        JoinTeamRequest request = new JoinTeamRequest(teamName);
        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        client.sendRequest(request);
    }

    /**
     * Requests to be removed from a team.
     *
     *  Matthew P. Suhocki (msuhocki)
     *
     * @param teamName  Handle of the team to leave
     */
    public void requestLeaveTeam(String teamName) {
        LeaveTeamRequest request = new LeaveTeamRequest(teamName);
        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        client.sendRequest(request);
    }

    /**
     * Requests to make the user a member of the captain's team
     */
    public void requestAddTeamMember(String userHandle) {
        AddTeamMemberRequest request = new AddTeamMemberRequest(userHandle);
        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        client.sendRequest(request);
    }

    /**
     * Requests to remove the user from the captain's team
     */
    public void requestRemoveTeamMember(String userHandle) {
        RemoveTeamMemberRequest request = new RemoveTeamMemberRequest(userHandle);
        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        client.sendRequest(request);
    }

    public void requestAssignComponents(ComponentAssignmentData data) {
        AssignComponentsRequest request = new AssignComponentsRequest(data);
        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        client.sendRequest(request);
    }

    /*
      public void getUserMode() {
      }
    */

    public void setClient(Client client) {
        this.client = client;
    }

    public void setInterceptorManager(InterceptorManager interceptorManager) {
        this.interceptorManager = interceptorManager;
    }

    private void sendRequest(BaseRequest request) {
        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        client.sendRequest(request);
    }

    public void requestVote(int roundId, String selectedName) {
        VoteRequest request = new VoteRequest(roundId, selectedName);
        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        sendRequest(request);
    }

    public void requestRoundStats(int roundId, String coderName) {
        RoundStatsRequest request = new RoundStatsRequest(roundId, coderName);
        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        sendRequest(request);
    }

    public void requestWLMyTeamInfo(int roundId) {
        WLMyTeamInfoRequest request = new WLMyTeamInfoRequest(roundId);
        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        sendRequest(request);
    }

    public void requestWLTeamsInfo(int roundId) {
        WLTeamsInfoRequest request = new WLTeamsInfoRequest(roundId);
        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        sendRequest(request);
    }

    public void requestSunAutoCompile(int roundID) {
        AutoSystestRequest request = new AutoSystestRequest( roundID);

        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        client.sendRequest(request);
    }

    public void requestSystestResults(int roundId) {
        SystestResultsRequest request = new SystestResultsRequest( roundId);

        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        client.sendRequest(request);
    }

    public void requestReadMessage(int messageID) {
        ReadMessageRequest request = new ReadMessageRequest(messageID);

        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        client.sendRequest(request);
    }

    public void requestImportantMessages() {
        GetImportantMessagesRequest request = new GetImportantMessagesRequest();

        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        client.sendRequest(request);
    }

    public void requestVisitedPractice() {
        VisitedPracticeRequest request = new VisitedPracticeRequest();

        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        client.sendRequest(request);
    }

    public void requestVerify() throws TimeOutException {
        VerifyRequest request = new VerifyRequest();

        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        try {
            client.sendSynchRequest(request);
        } catch (RequestTimedOutException e) {
            throw new TimeOutException(e);
        }
    }

    public void requestVerifyResult(int result) throws TimeOutException {
        VerifyResultRequest request = new VerifyResultRequest(result);

        // See if any interceptor excepts it first
        if(interceptorManager!=null && interceptorManager.sendMessage(request)) return;

        try {
            client.sendSynchRequest(request);
        } catch (RequestTimedOutException e) {
            throw new TimeOutException(e);
        }
    }

    public void requestErrorReport(Throwable error) {
        ErrorReportRequest request = new ErrorReportRequest(error);

        // See if any interceptor excepts it first
        if (interceptorManager != null && interceptorManager.sendMessage(request)) return;

        client.sendRequest(request);
    }

    public void requestExchangeKey(byte[] key) throws TimeOutException {
        ExchangeKeyRequest request = new ExchangeKeyRequest(key);

        // See if any interceptor excepts it first
        if (interceptorManager != null && interceptorManager.sendMessage(request)) return;

        try {
            client.sendSynchRequest(request);
        } catch (RequestTimedOutException e) {
            throw new TimeOutException (e);
        }
    }
}
