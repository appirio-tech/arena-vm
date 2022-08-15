/*
* Copyright (C) 2008 - 2014 TopCoder Inc., All Rights Reserved.
*/

package com.topcoder.netCommon.contestantMessages;

import java.io.IOException;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;

import com.topcoder.netCommon.contest.Matrix2D;
import com.topcoder.netCommon.contest.SurveyAnswerData;
import com.topcoder.netCommon.contest.SurveyChoiceData;
import com.topcoder.netCommon.contest.SurveyQuestionData;
import com.topcoder.netCommon.contest.round.NullRoundCustomProperties;
import com.topcoder.netCommon.contest.round.RoundCustomPropertiesImpl;
import com.topcoder.netCommon.contest.round.RoundType;
import com.topcoder.netCommon.contestantMessages.request.ActiveUsersRequest;
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
import com.topcoder.netCommon.contestantMessages.request.CoderHistoryRequest;
import com.topcoder.netCommon.contestantMessages.request.CoderInfoRequest;
import com.topcoder.netCommon.contestantMessages.request.CompileRequest;
import com.topcoder.netCommon.contestantMessages.request.DivSummaryRequest;
import com.topcoder.netCommon.contestantMessages.request.EnterRequest;
import com.topcoder.netCommon.contestantMessages.request.EnterRoundRequest;
import com.topcoder.netCommon.contestantMessages.request.ErrorReportRequest;
import com.topcoder.netCommon.contestantMessages.request.ErrorRequest;
import com.topcoder.netCommon.contestantMessages.request.GenericPopupRequest;
import com.topcoder.netCommon.contestantMessages.request.GetAdminBroadcastsRequest;
import com.topcoder.netCommon.contestantMessages.request.GetChallengeProblemRequest;
import com.topcoder.netCommon.contestantMessages.request.GetCurrentAppletVersionRequest;
import com.topcoder.netCommon.contestantMessages.request.GetLeaderBoardRequest;
import com.topcoder.netCommon.contestantMessages.request.GetProblemRequest;
import com.topcoder.netCommon.contestantMessages.request.GetSourceCodeRequest;
import com.topcoder.netCommon.contestantMessages.request.KeepAliveRequest;
import com.topcoder.netCommon.contestantMessages.request.LoginRequest;
import com.topcoder.netCommon.contestantMessages.request.LogoutRequest;
import com.topcoder.netCommon.contestantMessages.request.LongSubmitRequest;
import com.topcoder.netCommon.contestantMessages.request.LongTestResultsRequest;
import com.topcoder.netCommon.contestantMessages.request.MoveRequest;
import com.topcoder.netCommon.contestantMessages.request.OpenComponentForCodingRequest;
import com.topcoder.netCommon.contestantMessages.request.OpenSummaryRequest;
import com.topcoder.netCommon.contestantMessages.request.PracticeSystemTestRequest;
import com.topcoder.netCommon.contestantMessages.request.RegisterInfoRequest;
import com.topcoder.netCommon.contestantMessages.request.RegisterRequest;
import com.topcoder.netCommon.contestantMessages.request.RegisterUsersRequest;
import com.topcoder.netCommon.contestantMessages.request.RoundScheduleRequest;
import com.topcoder.netCommon.contestantMessages.request.RoundStatsRequest;
import com.topcoder.netCommon.contestantMessages.request.SaveRequest;
import com.topcoder.netCommon.contestantMessages.request.SearchRequest;
import com.topcoder.netCommon.contestantMessages.request.SubmitRequest;
import com.topcoder.netCommon.contestantMessages.request.TestInfoRequest;
import com.topcoder.netCommon.contestantMessages.request.TestRequest;
import com.topcoder.netCommon.contestantMessages.request.ToggleChatRequest;
import com.topcoder.netCommon.contestantMessages.request.UnwatchRequest;
import com.topcoder.netCommon.contestantMessages.request.VerifyRequest;
import com.topcoder.netCommon.contestantMessages.request.VerifyResultRequest;
import com.topcoder.netCommon.contestantMessages.request.VisitedPracticeRequest;
import com.topcoder.netCommon.contestantMessages.request.VoteRequest;
import com.topcoder.netCommon.contestantMessages.request.WatchRequest;
import com.topcoder.netCommon.contestantMessages.response.BatchTestResponse;
import com.topcoder.netCommon.contestantMessages.response.ChallengeInfoResponse;
import com.topcoder.netCommon.contestantMessages.response.CreateCategoryListResponse;
import com.topcoder.netCommon.contestantMessages.response.CreateChallengeTableResponse;
import com.topcoder.netCommon.contestantMessages.response.CreateLeaderBoardResponse;
import com.topcoder.netCommon.contestantMessages.response.CreateMenuResponse;
import com.topcoder.netCommon.contestantMessages.response.CreateProblemsResponse;
import com.topcoder.netCommon.contestantMessages.response.CreateRoomListResponse;
import com.topcoder.netCommon.contestantMessages.response.CreateRoundListResponse;
import com.topcoder.netCommon.contestantMessages.response.CreateUserListResponse;
import com.topcoder.netCommon.contestantMessages.response.CreateVisitedPracticeResponse;
import com.topcoder.netCommon.contestantMessages.response.EnableRoundResponse;
import com.topcoder.netCommon.contestantMessages.response.EndSyncResponse;
import com.topcoder.netCommon.contestantMessages.response.GetAdminBroadcastResponse;
import com.topcoder.netCommon.contestantMessages.response.GetCurrentAppletVersionResponse;
import com.topcoder.netCommon.contestantMessages.response.GetProblemResponse;
import com.topcoder.netCommon.contestantMessages.response.KeepAliveInitializationDataResponse;
import com.topcoder.netCommon.contestantMessages.response.KeepAliveResponse;
import com.topcoder.netCommon.contestantMessages.response.LoginResponse;
import com.topcoder.netCommon.contestantMessages.response.LongTestResultsResponse;
import com.topcoder.netCommon.contestantMessages.response.OpenComponentResponse;
import com.topcoder.netCommon.contestantMessages.response.PhaseDataResponse;
import com.topcoder.netCommon.contestantMessages.response.PopUpGenericResponse;
import com.topcoder.netCommon.contestantMessages.response.PracticeSystemTestResponse;
import com.topcoder.netCommon.contestantMessages.response.PracticeSystemTestResultResponse;
import com.topcoder.netCommon.contestantMessages.response.RegisteredUsersResponse;
import com.topcoder.netCommon.contestantMessages.response.RoomInfoResponse;
import com.topcoder.netCommon.contestantMessages.response.RoundScheduleResponse;
import com.topcoder.netCommon.contestantMessages.response.SingleBroadcastResponse;
import com.topcoder.netCommon.contestantMessages.response.StartSyncResponse;
import com.topcoder.netCommon.contestantMessages.response.SubmissionHistoryResponse;
import com.topcoder.netCommon.contestantMessages.response.SynchTimeResponse;
import com.topcoder.netCommon.contestantMessages.response.SystestProgressResponse;
import com.topcoder.netCommon.contestantMessages.response.TestInfoResponse;
import com.topcoder.netCommon.contestantMessages.response.UnsynchronizeResponse;
import com.topcoder.netCommon.contestantMessages.response.UpdateChatResponse;
import com.topcoder.netCommon.contestantMessages.response.UpdateCoderComponentResponse;
import com.topcoder.netCommon.contestantMessages.response.UpdateCoderPointsResponse;
import com.topcoder.netCommon.contestantMessages.response.UpdateLeaderBoardResponse;
import com.topcoder.netCommon.contestantMessages.response.UpdateMenuResponse;
import com.topcoder.netCommon.contestantMessages.response.UpdatePreferencesResponse;
import com.topcoder.netCommon.contestantMessages.response.UpdateUserListResponse;
import com.topcoder.netCommon.contestantMessages.response.UserInfoResponse;
import com.topcoder.netCommon.contestantMessages.response.VerifyResponse;
import com.topcoder.netCommon.contestantMessages.response.VerifyResultResponse;
import com.topcoder.netCommon.contestantMessages.response.VoteResponse;
import com.topcoder.netCommon.contestantMessages.response.VoteResultsResponse;
import com.topcoder.netCommon.contestantMessages.response.WatchResponse;
import com.topcoder.netCommon.contestantMessages.response.data.BatchTestResult;
import com.topcoder.netCommon.contestantMessages.response.data.CategoryData;
import com.topcoder.netCommon.contestantMessages.response.data.CoderComponentItem;
import com.topcoder.netCommon.contestantMessages.response.data.CoderItem;
import com.topcoder.netCommon.contestantMessages.response.data.ComponentChallengeData;
import com.topcoder.netCommon.contestantMessages.response.data.ComponentLabel;
import com.topcoder.netCommon.contestantMessages.response.data.LeaderboardItem;
import com.topcoder.netCommon.contestantMessages.response.data.LongCoderComponentItem;
import com.topcoder.netCommon.contestantMessages.response.data.LongCoderItem;
import com.topcoder.netCommon.contestantMessages.response.data.PhaseData;
import com.topcoder.netCommon.contestantMessages.response.data.PracticeTestResultData;
import com.topcoder.netCommon.contestantMessages.response.data.ProblemLabel;
import com.topcoder.netCommon.contestantMessages.response.data.RoomData;
import com.topcoder.netCommon.contestantMessages.response.data.RoundData;
import com.topcoder.netCommon.contestantMessages.response.data.UserListItem;
import com.topcoder.shared.netCommon.CSHandler;
import com.topcoder.shared.problem.ComponentCategory;
import com.topcoder.shared.problem.DataType;
import com.topcoder.shared.problem.NodeElement;
import com.topcoder.shared.problem.Problem;
import com.topcoder.shared.problem.ProblemComponent;
import com.topcoder.shared.problem.TestCase;
import com.topcoder.shared.problem.TextElement;
import com.topcoder.shared.problem.UserConstraint;

/**
 * Defines a custom serialization handler shared by arena clients and server. It allows specific handling for arena
 * requests/responses.
 *
 * <p>
 * Changes in version 1.1 (Module Assembly - TopCoder Competition Engine - Batch Test):
 * <ol>
 *      <li>Added {@link #BATCH_TEST_REQUEST} constant to support {@link BatchTestRequest} objects.</li>
 *      <li>Added {@link #BATCH_TEST_RESPONSE} constant to support {@link BatchTestResponse} objects.</li>
 *      <li>Added {@link #BATCH_TEST_RESULT} constant to support {@link BatchTestResult} objects.</li>
 *      <li>Added registerClassID for {@link BatchTestRequest}, {@link BatchTestResponse}
 *      and {@link BatchTestResult} classes.</li>
 * </ol>
 * </p>
 *
 * @author Qi Liu, dexy
 * @version 1.1
 */
public class NetCommonCSHandler extends CSHandler {
    /**
     * Tag to support {@link BatchTestRequest} objects.
     * @since 1.1
     */
    private static final byte BATCH_TEST_REQUEST = 59;
    /**
     * Tag to support {@link BatchTestResponse} objects.
     * @since 1.1
     */
    private static final byte BATCH_TEST_RESPONSE = 58;
    /**
     * Tag to support {@link BatchTestResult} objects.
     * @since 1.1
     */
    private static final byte BATCH_TEST_RESULT = 57;
    // private static final byte REQUEST=66;
    // 67
    private static final byte MATRIX_2D = 68;

    private static final byte SURVEYANSWERDATA = 69;

    private static final byte SURVEYCHOICEDATA = 70;

    private static final byte SURVEYQUESTIONDATA = 71;

    private static final byte ADMINBROADCAST = 72;

    private static final byte ROUNDBROADCAST = 73;

    private static final byte COMPONENTBROADCAST = 74;

    private static final byte USERINFO = 75;

    private static final byte POPUPGENERICRESPONSE = 76;

    private static final byte CREATEMENURESPONSE = 77;

    private static final byte CREATEUSERLISTRESPONSE = 78;

    private static final byte CREATELEADERBOARDRESPONSE = 79;

    private static final byte UPDATELEADERBOARDRESPONSE = 80;

    private static final byte UPDATEMENURESPONSE = 81;

    private static final byte UPDATEPREFERENCESRESPONSE = 82;

    // private static final byte UPDATETIMERINFORESPONSE=83;
    private static final byte UPDATEUSERLISTRESPONSE = 84;

    private static final byte WATCHRESPONSE = 85;

    private static final byte LOGINRESPONSE = 86;

    private static final byte KEEPALIVERESPONSE = 87;

    // private static final byte UPDATEPHASERESPONSE=88;
    private static final byte CREATECHALLENGETABLERESPONSE = 89;

    private static final byte UPDATECODERCOMPONENTRESPONSE = 90;

    private static final byte CREATEPROBLEMSRESPONSE = 91;

    private static final byte UPDATECHATRESPONSE = 92;

    private static final byte GETPROBLEMRESPONSE = 93;

    private static final byte TESTINFORESPONSE = 94;

    private static final byte CHALLENGEINFORESPONSE = 95;

    private static final byte UNSYNCHRONIZERESPONSE = 96;

    private static final byte ROOMTYPERESPONSE = 97;

    private static final byte UPDATECODERPOINTSRESPONSE = 98;

    private static final byte TOGGLECHATREQUEST = 99;

    private static final byte TESTINFOREQUEST = 100;

    private static final byte ACTIVEUSERSREQUEST = 101;

    private static final byte KEEPALIVEREQUEST = 102;

    private static final byte CHALLENGEINFOREQUEST = 103;

    private static final byte CHATREQUEST = 104;

    private static final byte CLOSELEADERBOARDREQUEST = 105;

    private static final byte CODERHISTORYREQUEST = 106;

    private static final byte CODERINFOREQUEST = 107;

    private static final byte ENTERREQUEST = 108;

    private static final byte ERRORREQUEST = 109;

    private static final byte GENERICPOPUPREQUEST = 110;

    private static final byte GETADMINBROADCASTSREQUEST = 111;

    private static final byte SYSTESTPROGRESSRESPONSE = 112;

    private static final byte GETCHALLENGEPROBLEMREQUEST = 113;

    private static final byte GETLEADERBOARDREQUEST = 114;

    // private static final byte TUNNELIPREQUEST = 115; //Not used anymore, it can be reused
    private static final byte LOGINREQUEST = 116;

    private static final byte LOGOUTREQUEST = 117;

    private static final byte MOVEREQUEST = 118;

    private static final byte REGISTERINFOREQUEST = 119;

    private static final byte REGISTERREQUEST = 120;

    private static final byte SEARCHREQUEST = 121;

    private static final byte UNWATCHREQUEST = 122;

    private static final byte WATCHREQUEST = 123;

    private static final byte CLEARPRACTICEREQUEST = 124;

    private static final byte CHALLENGEREQUEST = 125;

    private static final byte CLOSEPROBLEMREQUEST = 126;

    private static final byte COMPILEREQUEST = 127;

    private static final byte GETPROBLEMREQUEST = -128;

    private static final byte PRACTICESYSTEMTESTREQUEST = -127;

    private static final byte REGISTERUSERSREQUEST = -126;

    private static final byte SAVEREQUEST = -125;

    private static final byte SUBMITREQUEST = -124;

    private static final byte TESTREQUEST = -123;

    private static final byte SINGLEBROADCASTRESPONSE = -122;

    private static final byte GETADMINBROADCASTRESPONSE = -121;

    private static final byte USERINFORESPONSE = -120;

    private static final byte OPENSUMMARYREQUEST = -119;

    private static final byte CLOSESUMMARYREQUEST = -118;

    private static final byte CREATEROOMLISTRESPONSE = -117;

    private static final byte ENABLEROUNDRESPONSE = -116;

    private static final byte CREATEROUNDLISTRESPONSE = -115;

    private static final byte REGISTEREDUSERSRESPONSE = -114;

    private static final byte SYNCHTIMERESPONSE = -113;

    private static final byte PHASEDATARESPONSE = -112;

    private static final byte ROUNDDATA = -111;

    // private static final byte SPEC_ROOMDATA = -110;
    private static final byte PHASEDATA = -109;

    private static final byte LEADERBOARDITEM = -107;

    private static final byte USERLISTITEM = -106;

    private static final byte CODERITEM = -105;

    private static final byte CODERCOMPONENTITEM = -104;

    private static final byte ROUNDSCHEDULEREQUEST = -103;

    private static final byte ROUNDSCHEDULERESPONSE = -102;

    private static final byte ENTERROUNDREQUEST = -101;

    private static final byte APPLET_ROOMDATA = -100;

    private static final byte VOTE_RESPONSE = -99;

    private static final byte VOTE_REQUEST = -98;

    private static final byte VOTE_RESULTS_RESPONSE = -97;

    private static final byte ROUND_STATS_REQUEST = -96;

    private static final byte CLEAR_PRACTICE_PROBLEM_REQUEST = -95;

    private static final byte DIV_SUMMARY_REQUEST = -94;

    private static final byte CLOSE_DIV_SUMMARY_REQUEST = -93;

    private static final byte START_SYNC_RESPONSE = -92;

    private static final byte END_SYNC_RESPONSE = -91;

    // private static final byte SET_LANGUAGE_REQUEST = -90; //Not used anymore
    private static final byte GET_CURRENT_APPLET_VERSION_RESPONSE = -89;

    private static final byte CREATE_CATEGORY_LIST_RESPONSE = -88;

    private static final byte CATEGORY_DATA = -87;

    private static final byte KEEPALIVE_INIT_DATA_RESPONSE = -86;

    private static final byte CREATE_VISITED_PRACTICE_RESPONSE = -85;

    private static final byte PROBLEM_LABEL = -84;

    private static final byte COMPONENT_LABEL = -83;

    private static final byte COMPONENT_CHALLENGE_DATA = -82;

    private static final byte DATA_TYPE = -81;

    private static final byte PROBLEM = -80;

    private static final byte PROBLEM_COMPONENT = -79;

    private static final byte NODE_ELEMENT = -78;

    private static final byte TEXT_ELEMENT = -77;

    private static final byte USER_CONSTRAINT = -76;

    private static final byte TEST_CASE = -75;

    private static final byte COMPONENT_CATEGORY = -74;

    private static final byte OPEN_COMPONENT_RESPONSE = -73;

    private static final byte PRACTICE_SYSTEM_TEST_RESPONSE = -72;

    private static final byte PRACTICE_SYSTEM_TEST_RESULT_RESPONSE = -71;

    private static final byte PRACTICE_TEST_RESULT_DATA = -70;

    private static final byte GET_CURRENT_VERSION_REQUEST = -69;

    private static final byte VISITED_PRACTICE_REQUEST = -68;

    private static final byte OPEN_COMPONENT_REQUEST = -67;

    private static final byte LONGCODERCOMPONENTITEM = -66;

    private static final byte SUBMISSION_HISTORY_RESPONSE = -65;

    private static final byte LONG_SUBMIT_REQUEST = -64;

    private static final byte LONG_TEST_RESULTS_REQUEST = -63;

    private static final byte GET_SOURCE_CODE_REQUEST = -62;

    private static final byte LONG_TEST_RESULTS_RESPONSE = -61;

    private static final byte LONGCODERITEM = -60;

    private static final byte VERIFY_REQUEST = -59;

    private static final byte VERIFY_RESPONSE = -58;

    private static final byte VERIFY_RESULT_REQUEST = -57;

    private static final byte VERIFY_RESULT_RESPONSE = -56;

    private static final byte ERROR_REPORT_REQUEST = -55;

    private static final byte ROUND_CUSTOM_PROPERTIES = -54;

    private static final byte ROUND_TYPE = -53;

    private static final byte NULL_ROUND_CUSTOM_PROPERTIES = -52;

    private static final Map writeMap;

    private static final Map readMap;

    /*
     * Just a little implementation change to avoid problems with inheritance. And to avoid so many ifs and instanceof.
     */
    static {
        writeMap = new HashMap(150);
        readMap = new HashMap(150);
        registerClassID(Matrix2D.class, MATRIX_2D);
        registerClassID(SurveyAnswerData.class, SURVEYANSWERDATA);
        registerClassID(SurveyChoiceData.class, SURVEYCHOICEDATA);
        registerClassID(SurveyQuestionData.class, SURVEYQUESTIONDATA);
        registerClassID(ComponentBroadcast.class, COMPONENTBROADCAST);
        registerClassID(RoundBroadcast.class, ROUNDBROADCAST);
        registerClassID(AdminBroadcast.class, ADMINBROADCAST);
        registerClassID(UserInfo.class, USERINFO);
        registerClassID(PopUpGenericResponse.class, POPUPGENERICRESPONSE);
        registerClassID(CreateMenuResponse.class, CREATEMENURESPONSE);
        registerClassID(CreateUserListResponse.class, CREATEUSERLISTRESPONSE);
        registerClassID(CreateLeaderBoardResponse.class, CREATELEADERBOARDRESPONSE);
        registerClassID(UpdateLeaderBoardResponse.class, UPDATELEADERBOARDRESPONSE);
        registerClassID(UpdateMenuResponse.class, UPDATEMENURESPONSE);
        registerClassID(UpdatePreferencesResponse.class, UPDATEPREFERENCESRESPONSE);
        registerClassID(UpdateUserListResponse.class, UPDATEUSERLISTRESPONSE);
        registerClassID(LoginResponse.class, LOGINRESPONSE);
        registerClassID(CreateChallengeTableResponse.class, CREATECHALLENGETABLERESPONSE);
        registerClassID(UpdateCoderComponentResponse.class, UPDATECODERCOMPONENTRESPONSE);
        registerClassID(CreateProblemsResponse.class, CREATEPROBLEMSRESPONSE);
        registerClassID(UpdateChatResponse.class, UPDATECHATRESPONSE);
        registerClassID(GetProblemResponse.class, GETPROBLEMRESPONSE);
        registerClassID(TestInfoResponse.class, TESTINFORESPONSE);
        registerClassID(ChallengeInfoResponse.class, CHALLENGEINFORESPONSE);
        registerClassID(UnsynchronizeResponse.class, UNSYNCHRONIZERESPONSE);
        registerClassID(RoomInfoResponse.class, ROOMTYPERESPONSE);
        registerClassID(UpdateCoderPointsResponse.class, UPDATECODERPOINTSRESPONSE);
        registerClassID(ToggleChatRequest.class, TOGGLECHATREQUEST);
        registerClassID(TestInfoRequest.class, TESTINFOREQUEST);
        registerClassID(ActiveUsersRequest.class, ACTIVEUSERSREQUEST);
        registerClassID(ChallengeInfoRequest.class, CHALLENGEINFOREQUEST);
        registerClassID(ChatRequest.class, CHATREQUEST);
        registerClassID(CloseLeaderBoardRequest.class, CLOSELEADERBOARDREQUEST);
        registerClassID(CoderHistoryRequest.class, CODERHISTORYREQUEST);
        registerClassID(CoderInfoRequest.class, CODERINFOREQUEST);
        registerClassID(EnterRequest.class, ENTERREQUEST);
        registerClassID(ErrorRequest.class, ERRORREQUEST);
        registerClassID(GenericPopupRequest.class, GENERICPOPUPREQUEST);
        registerClassID(GetAdminBroadcastsRequest.class, GETADMINBROADCASTSREQUEST);
        registerClassID(GetChallengeProblemRequest.class, GETCHALLENGEPROBLEMREQUEST);
        registerClassID(GetLeaderBoardRequest.class, GETLEADERBOARDREQUEST);
        registerClassID(LoginRequest.class, LOGINREQUEST);
        registerClassID(LogoutRequest.class, LOGOUTREQUEST);
        registerClassID(MoveRequest.class, MOVEREQUEST);
        registerClassID(RegisterInfoRequest.class, REGISTERINFOREQUEST);
        registerClassID(RegisterRequest.class, REGISTERREQUEST);
        registerClassID(SearchRequest.class, SEARCHREQUEST);
        registerClassID(UnwatchRequest.class, UNWATCHREQUEST);
        registerClassID(WatchRequest.class, WATCHREQUEST);
        registerClassID(DivSummaryRequest.class, DIV_SUMMARY_REQUEST);
        registerClassID(CloseDivSummaryRequest.class, CLOSE_DIV_SUMMARY_REQUEST);
        registerClassID(ClearPracticeProblemRequest.class, CLEAR_PRACTICE_PROBLEM_REQUEST);
        registerClassID(ClearPracticeRequest.class, CLEARPRACTICEREQUEST);
        registerClassID(ChallengeRequest.class, CHALLENGEREQUEST);
        registerClassID(CloseProblemRequest.class, CLOSEPROBLEMREQUEST);
        registerClassID(CompileRequest.class, COMPILEREQUEST);
        registerClassID(GetProblemRequest.class, GETPROBLEMREQUEST);
        registerClassID(PracticeSystemTestRequest.class, PRACTICESYSTEMTESTREQUEST);
        registerClassID(RegisterUsersRequest.class, REGISTERUSERSREQUEST);
        registerClassID(SaveRequest.class, SAVEREQUEST);
        registerClassID(SubmitRequest.class, SUBMITREQUEST);
        registerClassID(TestRequest.class, TESTREQUEST);
        registerClassID(SingleBroadcastResponse.class, SINGLEBROADCASTRESPONSE);
        registerClassID(GetAdminBroadcastResponse.class, GETADMINBROADCASTRESPONSE);
        registerClassID(UserInfoResponse.class, USERINFORESPONSE);
        registerClassID(WatchResponse.class, WATCHRESPONSE);
        registerClassID(KeepAliveRequest.class, KEEPALIVEREQUEST);
        registerClassID(KeepAliveResponse.class, KEEPALIVERESPONSE);
        registerClassID(SystestProgressResponse.class, SYSTESTPROGRESSRESPONSE);
        registerClassID(OpenSummaryRequest.class, OPENSUMMARYREQUEST);
        registerClassID(CloseSummaryRequest.class, CLOSESUMMARYREQUEST);
        registerClassID(CreateRoundListResponse.class, CREATEROUNDLISTRESPONSE);
        registerClassID(CreateRoomListResponse.class, CREATEROOMLISTRESPONSE);
        registerClassID(EnableRoundResponse.class, ENABLEROUNDRESPONSE);
        registerClassID(RegisteredUsersResponse.class, REGISTEREDUSERSRESPONSE);
        registerClassID(PhaseDataResponse.class, PHASEDATARESPONSE);
        registerClassID(SynchTimeResponse.class, SYNCHTIMERESPONSE);
        registerClassID(RoundData.class, ROUNDDATA);
        // registerClassID(com.topcoder.shared.netCommon.messages.spectator.RoomData.class,SPEC_ROOMDATA);
        registerClassID(RoomData.class, APPLET_ROOMDATA);
        registerClassID(CoderItem.class, CODERITEM);
        registerClassID(LongCoderItem.class, LONGCODERITEM);
        registerClassID(VerifyRequest.class, VERIFY_REQUEST);
        registerClassID(VerifyResultRequest.class, VERIFY_RESULT_REQUEST);
        registerClassID(VerifyResponse.class, VERIFY_RESPONSE);
        registerClassID(VerifyResultResponse.class, VERIFY_RESULT_RESPONSE);
        registerClassID(ErrorReportRequest.class, ERROR_REPORT_REQUEST);
        registerClassID(UserListItem.class, USERLISTITEM);
        registerClassID(PhaseData.class, PHASEDATA);
        registerClassID(LeaderboardItem.class, LEADERBOARDITEM);
        registerClassID(CoderComponentItem.class, CODERCOMPONENTITEM);
        registerClassID(LongCoderComponentItem.class, LONGCODERCOMPONENTITEM);
        registerClassID(SubmissionHistoryResponse.class, SUBMISSION_HISTORY_RESPONSE);
        registerClassID(LongSubmitRequest.class, LONG_SUBMIT_REQUEST);
        registerClassID(LongTestResultsRequest.class, LONG_TEST_RESULTS_REQUEST);
        registerClassID(LongTestResultsResponse.class, LONG_TEST_RESULTS_RESPONSE);
        registerClassID(GetSourceCodeRequest.class, GET_SOURCE_CODE_REQUEST);
        registerClassID(RoundScheduleRequest.class, ROUNDSCHEDULEREQUEST);
        registerClassID(RoundScheduleResponse.class, ROUNDSCHEDULERESPONSE);
        registerClassID(EnterRoundRequest.class, ENTERROUNDREQUEST);
        registerClassID(VoteResponse.class, VOTE_RESPONSE);
        registerClassID(VoteRequest.class, VOTE_REQUEST);
        registerClassID(VoteResultsResponse.class, VOTE_RESULTS_RESPONSE);
        registerClassID(RoundStatsRequest.class, ROUND_STATS_REQUEST);
        registerClassID(StartSyncResponse.class, START_SYNC_RESPONSE);
        registerClassID(GetCurrentAppletVersionResponse.class, GET_CURRENT_APPLET_VERSION_RESPONSE);
        registerClassID(EndSyncResponse.class, END_SYNC_RESPONSE);
        registerClassID(CreateCategoryListResponse.class, CREATE_CATEGORY_LIST_RESPONSE);
        registerClassID(CategoryData.class, CATEGORY_DATA);
        registerClassID(KeepAliveInitializationDataResponse.class, KEEPALIVE_INIT_DATA_RESPONSE);
        registerClassID(CreateVisitedPracticeResponse.class, CREATE_VISITED_PRACTICE_RESPONSE);
        registerClassID(ProblemLabel.class, PROBLEM_LABEL);
        registerClassID(ComponentLabel.class, COMPONENT_LABEL);
        registerClassID(ComponentChallengeData.class, COMPONENT_CHALLENGE_DATA);
        registerClassID(DataType.class, DATA_TYPE);
        registerClassID(Problem.class, PROBLEM);
        registerClassID(ProblemComponent.class, PROBLEM_COMPONENT);
        registerClassID(NodeElement.class, NODE_ELEMENT);
        registerClassID(TextElement.class, TEXT_ELEMENT);
        registerClassID(UserConstraint.class, USER_CONSTRAINT);
        registerClassID(TestCase.class, TEST_CASE);
        registerClassID(ComponentCategory.class, COMPONENT_CATEGORY);
        registerClassID(OpenComponentResponse.class, OPEN_COMPONENT_RESPONSE);
        registerClassID(PracticeSystemTestResponse.class, PRACTICE_SYSTEM_TEST_RESPONSE);
        registerClassID(PracticeSystemTestResultResponse.class, PRACTICE_SYSTEM_TEST_RESULT_RESPONSE);
        registerClassID(PracticeTestResultData.class, PRACTICE_TEST_RESULT_DATA);
        registerClassID(GetCurrentAppletVersionRequest.class, GET_CURRENT_VERSION_REQUEST);
        registerClassID(VisitedPracticeRequest.class, VISITED_PRACTICE_REQUEST);
        registerClassID(OpenComponentForCodingRequest.class, OPEN_COMPONENT_REQUEST);
        registerClassID(RoundCustomPropertiesImpl.class, ROUND_CUSTOM_PROPERTIES);
        registerClassID(RoundType.class, ROUND_TYPE);
        registerClassID(NullRoundCustomProperties.class, NULL_ROUND_CUSTOM_PROPERTIES);
        // registerClassID(SetLanguageRequest.class,SET_LANGUAGE_REQUEST);

        registerClassID(BatchTestRequest.class, BATCH_TEST_REQUEST);
        registerClassID(BatchTestResponse.class, BATCH_TEST_RESPONSE);
        registerClassID(BatchTestResult.class, BATCH_TEST_RESULT);
    }

    /**
     * Creates a new instance of <code>NetCommonCSHandler</code>. The encryption key used to encrypt/decrypt ciphered
     * messages is given.
     *
     * @param key the encryption key used to encrypt/decrypt ciphered messages.
     */
    public NetCommonCSHandler(Key key) {
        super(key);
    }

    private static void registerClassID(Class clazz, byte classID) {
        Byte classId = new Byte(classID);
        writeMap.put(clazz, classId);
        readMap.put(classId, clazz);
    }

    protected boolean writeObjectOverride2(Object object) throws IOException {
        return false;
    }

    protected final boolean writeObjectOverride(Object object) throws IOException {
        if (writeObjectOverride2(object)) {
            return true;
        }
        Byte classId = (Byte) writeMap.get(object.getClass());
        if (classId != null) {
            writeByte(classId.byteValue());
            customWriteObject(object);
            return true;
        }
        return false;
    }

    protected final Object readObjectOverride(byte type) throws IOException {
        Class clazz = (Class) readMap.get(new Byte(type));
        if (clazz != null) {
            return readCustomSerializable(clazz);
        } else {
            return readObjectOverride2(type);
        }
    }

    protected Object readObjectOverride2(byte type) throws IOException {
        return super.readObjectOverride(type);
    }
}
