/**
 * @author Michael Cervantes (emcee)
 * @since Apr 29, 2002
 */
package com.topcoder.client.contestant.view;

import java.util.ArrayList;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestant.ProblemModel;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contestantMessages.response.CoderHistoryResponse;
import com.topcoder.netCommon.contestantMessages.response.CreateVisitedPracticeResponse;
import com.topcoder.netCommon.contestantMessages.response.GetImportantMessagesResponse;
import com.topcoder.netCommon.contestantMessages.response.ImportantMessageResponse;
import com.topcoder.netCommon.contestantMessages.response.LongTestResultsResponse;
import com.topcoder.netCommon.contestantMessages.response.NoBadgeIdResponse;
import com.topcoder.netCommon.contestantMessages.response.PracticeSystemTestResponse;
import com.topcoder.netCommon.contestantMessages.response.PracticeSystemTestResultResponse;
import com.topcoder.netCommon.contestantMessages.response.RoundStatsResponse;
import com.topcoder.netCommon.contestantMessages.response.SubmissionHistoryResponse;
import com.topcoder.netCommon.contestantMessages.response.VoteResponse;
import com.topcoder.netCommon.contestantMessages.response.VoteResultsResponse;
import com.topcoder.netCommon.contestantMessages.response.WLMyTeamInfoResponse;
import com.topcoder.netCommon.contestantMessages.response.WLTeamsInfoResponse;

/**
 * Defines a UI instance which handles events not related to other listeners or UI instances.
 * 
 * @author Michael Cervantes
 * @version $Id: ContestantView.java 72313 2008-08-14 07:16:48Z qliu $
 */
public interface ContestantView {
    /**
     * Called when the connection is temporarily lost.
     */
    void lostConnectionEvent();

    /**
     * Called when the connection is being closed.
     */
    void closingConnectionEvent();

    /**
     * Sets the network connection status.
     * 
     * @param on <code>true</code> if the connection is normal; <code>false</code> otherwise.
     */
    void setConnectionStatus(boolean on);

    /**
     * Pops up a dialog which contains a title, a text message, and several items. There may be additional data related
     * to the dialog that should not be displayed.
     * 
     * @param type the type of the dialog.
     * @param type2 the type of the message text.
     * @param title the title of the dialog.
     * @param msg the text message.
     * @param al the items which should appear in the dialog.
     * @param o the data related to the dialog.
     * @see ContestConstants#ROOM_MOVE
     * @see ContestConstants#CONTEST_REGISTRATION
     * @see ContestConstants#CONTEST_REGISTRATION_SURVEY
     * @see ContestConstants#SUBMIT_RESULTS
     * @see ContestConstants#TEXT_AREA
     * @see ContestConstants#LABEL
     * @see ContestConstants#WRAPPING_TEXT_AREA
     * @see ContestApplet#popup(int, int, String, String, ArrayList, Object)
     */
    void popup(final int type, final int type2, final String title, final String msg, final ArrayList al, final Object o);

    /**
     * Pops up a generic information dialog. It only contains a title, a text message.
     * 
     * @param type the type of the message text.
     * @param title the title of the dialog.
     * @param msg the message of the dialog.
     * @see ContestConstants#TEXT_AREA
     * @see ContestConstants#LABEL
     * @see ContestConstants#WRAPPING_TEXT_AREA
     * @see ContestApplet#popup(int, String, String)
     */
    void popup(final int type, final String title, final String msg);

    /**
     * Called when the user has been logged off.
     */
    void loggingOff();

    /**
     * Called when a vote has been received.
     * 
     * @param voteResponse the vote to be presented.
     */
    void vote(VoteResponse voteResponse);

    /**
     * Called when the results of vote has been received.
     * 
     * @param voteResultsResponse the results of the vote.
     */
    void voteResults(VoteResultsResponse voteResultsResponse);

    /**
     * Called when the round statistics has been received.
     * 
     * @param roundStatsResponse the round statistics.
     */
    void roundStatsResponse(RoundStatsResponse roundStatsResponse);

    /**
     * Called when a badge ID is needed to be entered by the user.
     * 
     * @param noBadgeIdResponse the handle/password of the user whose badge ID is needed.
     */
    void noBadgeId(NoBadgeIdResponse noBadgeIdResponse);

    /**
     * Called when the 'Weakest Link' team information of the current user is available.
     * 
     * @param wlTeamInfoResponse the 'Weakest Link' team information.
     */
    void wlMyTeamInfoResponse(WLMyTeamInfoResponse wlTeamInfoResponse);

    /**
     * Called when information of all 'Weakest Link' teams is available.
     * 
     * @param wlTeamsInfoResponse the information of all 'Weakest Link' teams.
     */
    void wlTeamsInfoResponse(WLTeamsInfoResponse wlTeamsInfoResponse);

    /**
     * Called when the reconnection fails after temporarily connection loss.
     */
    void reconnectFailedEvent();

    /**
     * Called when an important message has been received.
     * 
     * @param response the important message.
     */
    void importantMessage(ImportantMessageResponse response);

    /**
     * Called when the summary of all important messages has been received.
     * 
     * @param response the summary of all important messages.
     */
    void importantMessageSummry(GetImportantMessagesResponse response);

    /**
     * Called when the visited practice room list of the current user is received.
     * 
     * @param response the visited practice room list.
     */
    void visitedPracticeList(CreateVisitedPracticeResponse response);

    /**
     * Called when the system test of the current user in a practice room has been started.
     * 
     * @param response the information about the system test.
     */
    void startPracticeSystest(PracticeSystemTestResponse response);

    /**
     * Called when the progress of the system test of the current user in a practice room is received.
     * 
     * @param response the progress of the system test.
     */
    void practiceSystestResult(PracticeSystemTestResultResponse response);

    /**
     * Called when the submission history for a marathon component is available.
     * 
     * @param response the submission history.
     */
    void showSubmissionHistory(SubmissionHistoryResponse response);

    /**
     * Called when the test results of a marathon component is available.
     * 
     * @param response the test results.
     */
    void showLongTestResults(LongTestResultsResponse response);

    /**
     * Called when the problem statement of a marathon has been received.
     * 
     * @param problem the model of the problem.
     */
    void showProblemStatement(ProblemModel problem);

    /**
     * Called when the history of a coder (may be other than the current user) is available.
     * 
     * @param response the history of a coder.
     */
    void showCoderHistory(CoderHistoryResponse response);

    /**
     * Loads all editor plugins.
     */
    void loadPlugins();
}
