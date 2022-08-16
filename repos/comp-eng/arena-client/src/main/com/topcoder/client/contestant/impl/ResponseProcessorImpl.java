/*
* Copyright (C) 2002 - 2014 TopCoder Inc., All Rights Reserved.
*/

/**
 * @author Michael Cervantes (emcee)
 * @since Apr 29, 2002
 */

package com.topcoder.client.contestant.impl;

import java.util.ArrayList;
import java.util.Iterator;

import com.topcoder.client.contestant.message.ResponseProcessor;
import com.topcoder.client.contestant.view.ContestantView;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contest.round.RoundType;
import com.topcoder.netCommon.contestantMessages.AdminBroadcast;
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
import com.topcoder.netCommon.contestantMessages.response.data.BatchTestResult;
import com.topcoder.netCommon.contestantMessages.response.data.LeaderboardItem;
import com.topcoder.netCommon.contestantMessages.response.data.PhaseData;
import com.topcoder.netCommon.contestantMessages.response.data.RoundData;
import com.topcoder.netCommon.contestantMessages.response.data.TeamListInfo;
import com.topcoder.netCommon.contestantMessages.response.data.UserListItem;
import com.topcoder.shared.problem.Problem;
import com.topcoder.shared.problem.ProblemComponent;

/**
 * This class is devoid of any GUI-specific logic. Please keep it that way.
 *
 * <p>
 * Changes in version 1.1 (Module Assembly - TopCoder Competition Engine - Batch Test):
 * <ol>
 *      <li>Added {@link #batchTest(BatchTestResponse)} method to handle {@link BatchTestResponse}.</li>
 * </ol>
 * </p>
 *
 * @author Michael Cervantes (emcee), dexy
 * @version 1.1
 */
final class ResponseProcessorImpl implements ResponseProcessor {
    private final ContestantImpl model;

    private final ContestantView view;

    ResponseProcessorImpl(ContestantImpl model) {
        this.model = model;
        this.view = model.getView();
    }

    private RoomModelImpl getRoomModel(long roomID) {
        return (RoomModelImpl) model.getRoom(roomID);
    }

    /*
     * ****************************************************************************** RESPONSES
     * ******************************************************************************
     */

    public void currentAppletVersion(GetCurrentAppletVersionResponse response) {
        model.setCurrentAppletVersion(response.getVersion());
    }

    public void reconnect(ReconnectResponse response) {
        if (response.getSuccess()) {
            model.setLoggedIn(response.getSuccess());
            model.setConnectionID(response.getConnectionID());
            model.setHashCode(response.getHashCode());

            // TODO: RYAN GUI
            for (Iterator i = model.getBroadcastManager().getBroadcasts().iterator(); i.hasNext();) {
                AdminBroadcast bc = (AdminBroadcast) i.next();
                if (!model.getBroadcastManager().hasRead(bc)) {
                    model.getBroadcastManager().newBroadcast(bc);
                }
            }

            view.setConnectionStatus(true);
        } else {
            model.reconnectFailed();
            view.setConnectionStatus(true);
            view.reconnectFailedEvent();
        }
    }

    /*******************************************************************************************************************
     * Logic Response contains boolean indictating success
     */
    public void login(LoginResponse response) {
        model.setLoggedIn(response.getSuccess());
        if (response.getSuccess()) {
            view.setConnectionStatus(true);
            model.setConnectionID(response.getConnectionID());
            model.setHashCode(response.getHashCode());
        }
    }

    public void userInfo(UserInfoResponse response) {
        model.setUserInfo(response.getUserInfo());
    }

    public void updatePreferences(UpdatePreferencesResponse response) {

    }

    public void createMenu(CreateMenuResponse response) {
        switch (response.getType()) {
        // case ContestConstants.PRACTICE_ROOM_MENU:
        // model.getMenuView().createPracticeMenu(response.getNames(),response.getIDs());
        // break;
        case ContestConstants.LOBBY_MENU:
            ArrayList IDs = response.getIDs();
            ArrayList names = response.getNames();
            for (int i = 0; i < IDs.size(); i++) {
                Integer id = (Integer) IDs.get(i);
                // String name = (String) names.get(i);
                model.newLobby(new Long(id.intValue()));
            }
            model.getMenuView().createLobbyMenu(names, response.getStatii(), IDs);
            break;
        case ContestConstants.ACTIVE_CHAT_MENU:
            model.getMenuView().createActiveChatMenu(response.getNames(), response.getStatii(), response.getIDs());
            break;
        default:
            throw new IllegalArgumentException("Invalid menu type: " + response.getType());
        }
    }

    public void updateMenu(UpdateMenuResponse response) {
        switch (response.getType()) {
        case ContestConstants.LOBBY_MENU:
            model.getMenuView().modifyLobbyMenu(response.getElement(), response.getStatus());
            break;
        case ContestConstants.ACTIVE_CHAT_MENU:
            model.getMenuView().modifyActiveChatMenu(response.getElement(), response.getStatus());
            break;
        default:
            throw new IllegalArgumentException("Invalid update menu type: " + response.getType());
        }
    }

    /**
     */
    public void watch(WatchResponse response) {
        // view.watchEvent(response.getRoomIndex());
    }

    public void createLeaderBoard(CreateLeaderBoardResponse response) {
        model.getRoundModel(response.getRoundID()).setLeaderboard(response.getItems());
        LeaderboardItem[] leaderboard = response.getItems();
        for (int i = 0; i < leaderboard.length; i++) {
            LeaderboardItem leaderboardItem = leaderboard[i];
            RoomModelImpl room = getRoomModel(leaderboardItem.getRoomID());
            room.setLeader(leaderboardItem);
        }
    }

    public void updateLeaderBoard(UpdateLeaderBoardResponse response) {
        LeaderboardItem item = response.getItem();
        RoomModelImpl room = getRoomModel(item.getRoomID());
        room.setLeader(item);
        model.getRoundModel(response.getRoundID()).updateLeaderboard(room, item);
    }

    public void popupGeneric(PopUpGenericResponse response) {
        int type1 = response.getType1();
        int type2 = response.getType2();
        String msg = response.getMessage();
        String title = response.getTitle();
        if (type1 == ContestConstants.GENERIC) {
            view.popup(type2, title, msg);
        } else if (type1 == ContestConstants.CONTEST_REGISTRATION_SURVEY) {
            ArrayList al = response.getButtons(); // buttons
            al.add(response.getSurveyQuestions()); // survey questions
            al.add(response.getSurveyMessage()); // survey message
            Object o = response.getMoveData();
            view.popup(type1, type2, title, msg, al, o);
        } else { // CONTEST_REGISTRATION or ROOM_MOVE
            ArrayList al = response.getButtons();
            Object o = response.getMoveData();
            view.popup(type1, type2, title, msg, al, o);
        }
    }

    public void submitResponse(SubmitResultsResponse response) {
        // execute submit results screen
        Long roundID = new Long(response.getRoundID());
        ArrayList al = new ArrayList();
        al.add(roundID);
        al.add(new Boolean(response.getSystest()));

        view.popup(ContestConstants.SUBMIT_RESULTS, ContestConstants.TEXT_AREA, "Submission Results", response
            .getMessage(), al, roundID);

    }

    public void forceLogoff(ForcedLogoutResponse response) {
        view.popup(ContestConstants.LABEL, response.getTitle(), response.getMessage());
        view.loggingOff();
        model.reset();

    }

    public void createUserList(CreateUserListResponse response) {
        RoomModelImpl roomModel = getRoomModel(response.getRoomID());
        UserListItem[] items = response.getUserListItems();

        if (roomModel == null)
            return;
        int type = response.getType();
        switch (type) {
        case ContestConstants.ROOM_USERS:
            roomModel.setUserList(items);
            break;
        // Get this info from the createChallengeTable
        // case ContestConstants.ASSIGNED_USERS:
        // roomModel.addToContestantList(items);
        // break;
        case ContestConstants.ACTIVE_USERS:
            model.getActiveUsersView().updateUserList(items);
            break;
        case ContestConstants.TEAM_AVAILABLE_USERS:
            roomModel.addToAvailableList(items);
            break;
        case ContestConstants.TEAM_MEMBER_USERS:
            roomModel.addToMemberList(items);
            break;
        default:
            throw new IllegalArgumentException("Invalid user list type: " + type);
        }
    }

    public void updateUserList(UpdateUserListResponse response) {
        RoomModelImpl roomModel = getRoomModel(response.getRoomID());
        UserListItem item = response.getUserListItem();

        if (roomModel == null)
            return;
        int type = response.getType();
        int action = response.getAction();
        switch (type) {
        case ContestConstants.ROOM_USERS:
            if (action == ContestConstants.ADD) {
                roomModel.addToUserList(item);
                // roomModel.addToUserList((String) response.getData().get(0),((Integer)
                // response.getData().get(1)).intValue());
            } else if (action == ContestConstants.REMOVE) {
                roomModel.removeFromUserList(item);
                // roomModel.removeFromUserList(response.getName());
            }
            break;
        case ContestConstants.ASSIGNED_USERS:
            if (action == ContestConstants.ADD) {
                // roomModel.addToContestantList(item);
            }
            break;
        case ContestConstants.ACTIVE_USERS:
            // currently not implemented
            break;
        case ContestConstants.REGISTERED_USERS:
            // currently not implemented
            break;
        case ContestConstants.TEAM_AVAILABLE_USERS:
            if (action == ContestConstants.ADD) {
                roomModel.addToAvailableList(item);
            } else if (action == ContestConstants.REMOVE) {
                roomModel.removeFromAvailableList(item);
            }
            break;
        case ContestConstants.TEAM_MEMBER_USERS:
            if (action == ContestConstants.ADD) {
                roomModel.addToMemberList(item);
            } else if (action == ContestConstants.REMOVE) {
                roomModel.removeFromMemberList(item);
            }
            break;
        }
    }

    public void updateChat(UpdateChatResponse response) {
        RoomModelImpl roomModel;
        if (response.getRoomID() == -1) {
            roomModel = model.getCurrentRoomImpl();
            if (roomModel == null) {
                throw new IllegalStateException("Global chat message received but no current room to direct it to: "
                    + response);
            }
        } else {
            roomModel = getRoomModel(response.getRoomID());
        }

        if (response.getType() == ContestConstants.USER_CHAT
            || response.getType() == ContestConstants.MODERATED_CHAT_SPEAKER_CHAT
            || response.getType() == ContestConstants.MODERATED_CHAT_QUESTION_CHAT) {
            String prefix = response.getPrefix();
            roomModel.updateChatRoom(prefix, response.getRating(), response.getData(), response.getScope());
        } else {
            String message = response.getData();
            roomModel.updateChatRoom(response.getType(), message, response.getScope());
        }
    }

    /**
     * GET_PROBLEM
     */
    // //////////////////////////////////////////////////////////////////////////////
    public void getProblem(GetProblemResponse response) {
        Integer division = new Integer(response.getDivisionID());
        RoundModelImpl roundModel = model.getRoundModel(response.getRoundID());
        Problem problem = response.getProblem();
        ProblemModelImpl problemModel = roundModel.getProblemImpl(division, new Long(response
            .getProblem().getProblemId()));
        ProblemComponent problemComponents[] = problem.getProblemComponents();
        for (int i = 0; i < problemComponents.length; i++) {
            ProblemComponent problemComponent = problemComponents[i];
            ProblemComponentModelImpl problemComponentModel = roundModel.getComponentImpl(division,
                new Long(problemComponent.getComponentId()));
            problemComponentModel.setServerComponentObject(problemComponent);
        }
        problemModel.setServerProblemObject(problem);
    }

    public void getTeamProblem(GetTeamProblemResponse response) {
        Integer division = new Integer(response.getDivisionID());
        RoundModelImpl roundModel = model.getRoundModel(response.getRoundID());
        Problem problem = response.getProblem();
        ProblemModelImpl problemModel = roundModel.getProblemImpl(division, new Long(response
            .getProblem().getProblemId()));
        ProblemComponent problemComponents[] = problem.getProblemComponents();
        for (int i = 0; i < problemComponents.length; i++) {
            ProblemComponent problemComponent = problemComponents[i];
            ProblemComponentModelImpl problemComponentModel = roundModel.getComponentImpl(division,
                new Long(problemComponent.getComponentId()));
            problemComponentModel.setServerComponentObject(problemComponent);
        }
        problemModel.setReadOnlyServerProblemObject(problem);
    }

    /**
     * TEST_INFO [0] -> (ArrayList) parameters
     */
    public void testInfo(TestInfoResponse response) {
        model.getCurrentRoomImpl().updateTestInfo(response.getDataTypes(), response.getComponentID());
    }

    /**
     * Handles {@link BatchTestResponse} received after batch testing.
     *
     * @param response the response of the batch testing with all the results
     * @since 1.1
     */
    public void batchTest(BatchTestResponse response) {
        StringBuilder message = new StringBuilder();
        ArrayList results = response.getResults();
        for (int iresult = 0; iresult < results.size(); iresult++) {
            BatchTestResult result = (BatchTestResult) results.get(iresult);
            if (result == null) {
                message.append("[TEST CASE #" + (iresult + 1) + "]: NULL");
                continue;
            }
            message.append("[TEST CASE #" + (iresult + 1) + "]:");
            message.append("\nSuccess: " + result.isSuccess());
            if (result.getErrorMessage() != null && result.getErrorMessage().length() > 0) {
                message.append("\nError message: " + result.getErrorMessage());
            }
            message.append("\nStatus: " + result.getStatus());
            if (result.getMessage() != null && result.getMessage().length() > 0) {
                message.append("\nMessage: " + result.getMessage());
            }
            if (result.getReturnValue() != null) {
                message.append("\nReturn value: " + result.getReturnValue().toString());
            }
            if (result.getCorrectExample() != null && result.getCorrectExample().length() > 0) {
                message.append("\nCorrect example: " + result.getCorrectExample());
            }
            message.append("\nExecution time (ms): " + result.getExecutionTime());
            message.append("\nPeak memory used (KB): " + result.getPeakMemoryUsed());
            if (result.getStdOut() != null && result.getStdOut().length() > 0) {
                message.append("\nStandard output: " + result.getStdOut());
            }
            if (result.getStdErr() != null && result.getStdErr().length() > 0) {
                message.append("\nStandard error: " + result.getStdErr());
            }
            if (result.getStacktrace() != null && result.getStacktrace().length() > 0) {
                message.append("\nStack trace: " + result.getStacktrace());
            }

            message.append("\n\n");
        }
        view.popup(ContestConstants.TEXT_AREA, "Batch Test Results", message.toString());
    }

    public void createProblems(CreateProblemsResponse response) {
        RoundModelImpl roundModel = model.getRoundModel(response.getRoundID());
        roundModel.setProblems(new Integer(response.getDivisionID()), response.getAssignedComponents(), response.getProblems());
    }

    public void assignComponents(AssignComponentsResponse response) {
        RoundModelImpl roundModel = model.getRoundModel(response.getRoundID());
        roundModel.setAssignedComponents(new Integer(response.getDivisionID()), response.getAssignedComponents());
    }

    public void challengeInfo(ChallengeInfoResponse response) {
        // model.getCurrentRoomImpl().updateChallengeInfo(response.getDataTypes(), response.getMessage());
    }

    // public void updateChallengeCell(UpdateChallengeTableCellResponse response) {
    // RoomModelImpl roomModel = getRoomModel(response.getRoomType(),response.getRoomID());
    // if (roomModel == null)
    // return;
    // roomModel.updateChallengeCell(response.getHandle(),response.getComponentID(),response.getStatus());
    // }

    public void updateCoderComponent(UpdateCoderComponentResponse response) {
        RoomModelImpl roomModel = getRoomModel(response.getRoomID());
        if (roomModel == null)
            return;
        roomModel.updateCoderComponent(response.getCoderHandle(), response.getComponent());
    }

    public void updateCoderPoints(UpdateCoderPointsResponse response) {
        RoomModelImpl roomModel = getRoomModel(response.getRoomID());
        if (roomModel == null)
            return;
        roomModel.updateCoderPoints(response.getCoderHandle(), response.getPoints());
    }

    public void createChallengeTable(CreateChallengeTableResponse response) {
        RoomModelImpl roomModel = getRoomModel(response.getRoomID());
        roomModel.updateChallengeTable(response.getCoders());
    }

    public void roomType(RoomInfoResponse response) {
        model.roomInfo(response.getRoomType(), response.getRoomID(), response.getName(), response.getStatus());
    }

    public void singleBroadcast(SingleBroadcastResponse response) {
        model.getBroadcastManager().newBroadcast(response.getBroadcast());
    }

    public void getAdminBroadcast(GetAdminBroadcastResponse response) {
        model.getBroadcastManager().refresh(response.getBroadcasts());
    }

    public void systestProgress(SystestProgressResponse response) {
        model.getRoundModel(response.getRoundID()).updateSystestProgress(response.getDone(), response.getTotal());
    }

    public void updateTeamList(UpdateTeamListResponse response) {
        model.getTeamListView().updateTeamList(response.getTeamListInfo());
    }

    public void createTeamList(CreateTeamListResponse response) {
        TeamListInfo[] info = response.getTeamListInfo();
        for (int i = 0; i < info.length; i++)
            model.getTeamListView().updateTeamList(info[i]);
    }

    public void createRoundList(CreateRoundListResponse response) {
        RoundData[] roundData = response.getRoundData();
        if (response.getType() == CreateRoundListResponse.PRACTICE) {
            model.setPracticeRounds(roundData);
        } else if (response.getType() == CreateRoundListResponse.ACTIVE) {
            model.setActiveRounds(roundData);
        } else {
            throw new IllegalArgumentException("Bad type: " + response);
        }
    }

    public void createCategoryList(CreateCategoryListResponse response) {
        model.setRoundCategories(response.getCategories());
    }

    public void createRoomList(CreateRoomListResponse response) {
        if (response.hasAdminRoom()) {
            model.newAdminRoom(response.getRoundID(), response.getAdminRoom());
        }
        model.newCoderRooms(response.getRoundID(), response.getCoderRooms());
    }

    public void enableRound(EnableRoundResponse response) {
        model.getRoundModel(response.getRoundID()).setMenuStatus(true);
    }

    public void registeredUsers(RegisteredUsersResponse response) {
        RoundType type = model.getRoundModel(response.getRoundID()).getRoundType();
        if (type.isHsRound()) {
            model.getHSRegisteredUsersView().updateUserList(response.getUserListItems());
        } else if (type.isLongRound()) {
            model.getLongRoundRegisteredUsersView().updateUserList(response.getUserListItems());
        } else {
            model.getRegisteredUsersView().updateUserList(response.getUserListItems());
        }

    }

    public void updateRoundList(UpdateRoundListResponse response) {
        switch (response.getAction()) {
        case UpdateRoundListResponse.ACTION_ADD:
            model.newRound(response.getRoundData());
            break;
        case UpdateRoundListResponse.ACTION_REMOVE:
            model.removeRound(response.getRoundData());
            break;
        default:
            throw new IllegalArgumentException("Bad action: " + response.getAction());
        }
    }

    public void phaseData(PhaseDataResponse response) {
        PhaseData phaseData = response.getPhaseData();
        RoundModelImpl roundModel = model.getRoundModel(phaseData.getRoundID());
        roundModel.setPhase(phaseData.getPhaseType(), phaseData.getEndTime());
    }

    public void synchTime(SynchTimeResponse response) {
        model.updateServerTime(response.getTime());
    }

    public void openComponent(OpenComponentResponse response) {
        // int type = response.getEditable();
        RoomModelImpl roomModel = getRoomModel(response.getRoomID());

        CoderImpl writer = roomModel.getCoderImpl(response.getWriterHandle());
        CoderComponentImpl writerComponent = writer.getCoderComponent(new Long(response.getComponentID()));
        String sourceCode = response.getCode();
        writerComponent.setSourceCode(response.getLanguageID().intValue(), sourceCode);

        // if (type == ContestConstants.VIEW_SOURCE) {
        // // roomModel.setChallengeComponentSource(response.getCode(), response.getLanguageID());
        // }
        // else if ( type == ContestConstants.EDIT_SOURCE_RW ||
        // type == ContestConstants.EDIT_SOURCE_RO) {
        // roomModel.setComponentSource(response.getLanguageID(), response.getCode());
        // }
    }

    public void roundSchedule(RoundScheduleResponse response) {
        RoundModelImpl roundModel = model.getRoundModel(response.getRoundID());
        roundModel.setSchedule(response.getSchedule());
    }

    public void vote(VoteResponse voteResponse) {
        view.vote(voteResponse);
    }

    public void voteResults(VoteResultsResponse voteResultsResponse) {
        view.voteResults(voteResultsResponse);
    }

    public void roundStatsResponse(RoundStatsResponse roundStatsResponse) {
        view.roundStatsResponse(roundStatsResponse);
    }

    public void noBadgeId(NoBadgeIdResponse noBadgeIdResponse) {
        view.noBadgeId(noBadgeIdResponse);
    }

    public void wlMyTeamInfoResponse(WLMyTeamInfoResponse wlTeamInfoResponse) {
        view.wlMyTeamInfoResponse(wlTeamInfoResponse);
    }

    public void wlTeamsInfoResponse(WLTeamsInfoResponse wlTeamsInfoResponse) {
        view.wlTeamsInfoResponse(wlTeamsInfoResponse);
    }

    public void updateAssignedComponents(ComponentAssignmentDataResponse response) {
        model.setComponentAssignmentData(response.getData());
    }

    public void importantMessage(ImportantMessageResponse response) {
        view.importantMessage(response);
    }

    public void getImportantMessages(GetImportantMessagesResponse response) {
        view.importantMessageSummry(response);
    }

    public void visitedPracticeList(CreateVisitedPracticeResponse response) {
        view.visitedPracticeList(response);
    }

    public void practiceSystemTestResult(PracticeSystemTestResultResponse response) {
        view.practiceSystestResult(response);
    }

    public void practiceSystemTestResponse(PracticeSystemTestResponse response) {
        view.startPracticeSystest(response);
    }

    public void verifyResponse(VerifyResponse response) {
        model.setVerifyCode(response.getVerifyCode());
    }

    public void verifyResult(VerifyResultResponse response) {
        model.setVerifyResult(response.getSuccess());
    }

    public void submissionHistory(SubmissionHistoryResponse response) {
        view.showSubmissionHistory(response);
    }

    public void longTestResults(LongTestResultsResponse response) {
        view.showLongTestResults(response);
    }

    public void coderHistory(CoderHistoryResponse response) {
        view.showCoderHistory(response);
    }

    public void exchangeKey(ExchangeKeyResponse response) {
        model.setExchangeKey(response.getKey());
    }
}
