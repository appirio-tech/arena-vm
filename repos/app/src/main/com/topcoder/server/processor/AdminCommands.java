/*
* Copyright (C) 2002 - 2014 TopCoder Inc., All Rights Reserved.
*/
package com.topcoder.server.processor;

import java.util.ArrayList;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.server.AdminListener.request.ApprovedQuestionCommand;
import com.topcoder.server.TopicListener.ReplayListener;
import com.topcoder.server.common.BaseRound;
import com.topcoder.server.common.ContestRound;
import com.topcoder.server.common.Round;
import com.topcoder.server.common.TCEvent;
import com.topcoder.server.common.WeakestLinkRound;
import com.topcoder.server.ejb.TestServices.LongContestServicesException;
import com.topcoder.server.ejb.TestServices.LongContestServicesLocator;
import com.topcoder.server.replay.ReplayReciever;
import com.topcoder.server.services.CoreServices;
import com.topcoder.server.services.CoreServicesException;
import com.topcoder.server.services.EventService;
import com.topcoder.shared.util.StageQueue;
import com.topcoder.shared.util.logging.Logger;
import com.topcoder.shared.i18n.MessageProvider;

/**
 * AdminCommand implements all of the commands which can be entered with a
 * "/admin" prefix in the applet chat window. Several of the commands in the
 * class are intended for debugging purposes only. Refer to the
 * admincommands.txt file for details on what each command does and how it
 * works.
 *
 * <p>
 * Changes in version 1.1 (TopCoder Competiton Engine - Automatically End Matches v1.0) :
 * <ol>
 *      <li>Update {@link #systemTest(int roundID, int coderID, int problemID,
 *              boolean failOnFirstBadTest, boolean reference)} method.</li>
 *      <li>Add {@link #systemTest(int roundID, int coderID, int problemID,
 *              boolean failOnFirstBadTest, boolean reference. boolean async)} method.</li>
 *      
 * </ol>
 * </p>
 *@author Graham Hesselroth (gsh), TCSASSEMBLER
 *@version 1.1
 *@since     Jan 02, 2002
 */
public final class AdminCommands {

    static class EndContestTask implements Runnable {

        private int roundID;

        EndContestTask(int roundID) {
            this.roundID = roundID;
        }

        public void run() {
            CoreServices.endContest(roundID);
        }
    }

    static class EndHSContestTask implements Runnable {
        private int roundID;

        EndHSContestTask(int roundID) {
            this.roundID = roundID;
        }

        public void run() {
            CoreServices.endHSContest(roundID);
        }
    }

    static class AssignRoomsTask implements Runnable {

        private final Round m_contest;
        private final int m_coders;
        private final boolean m_byDivision;
        private final int m_type;
        private final boolean m_isFinal;
        private final boolean m_isByRegion;
        private final double m_p;

        AssignRoomsTask(Round contest, int coders, int type, boolean byDivision,
                boolean isFinal, boolean isByRegion, double p) {
            m_contest = contest;
            m_coders = coders;
            m_type = type;
            m_byDivision = byDivision;
            m_isFinal = isFinal;
            m_isByRegion = isByRegion;
            m_p = p;
        }

        public void run() {
            CoreServices.assignRooms(m_contest.getContestID(), m_contest.getRoundID(), m_coders, m_type,
                    m_byDivision, m_isFinal, m_isByRegion, m_p);
        }
    }

    private static Logger trace = Logger.getLogger(AdminCommands.class);

//    private static Map s_timers = new WeakHashMap();


    public static void loadContestRound(long roundID) {
        Processor.loadContestRound(roundID);
    }

    public static void unloadContestRound(long roundID) {
        Processor.unloadContestRound(roundID);
    }


    public static void refreshRegistration() {
        trace.info("Refreshing registration");
        CoreServices.refreshRegistration();
    }

    public static void clearTestCases() {
        CoreServices.clearTestCases();
    }

    public static void cancelSystemTestCaseTesting(int roundID, int testCaseId) {
        ContestRound contest = (ContestRound) CoreServices.getContestRound(roundID);
        try {
            CoreServices.cancelSystemTestCaseTesting(contest.getContestID(), contest.getRoundID(), testCaseId);
        } catch (CoreServicesException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    /**
     * Invoke the system test
     * @param roundID the round id.
     * @param coderID the coder id.
     * @param problemID the problem id.
     * @param failOnFirstBadTest if it is failed when meet with first bad test.
     * @param reference if it is needed to reference
     */
    public static void systemTest(int roundID, int coderID, int problemID, boolean failOnFirstBadTest, boolean reference) {
        systemTest(roundID, coderID, problemID, failOnFirstBadTest, reference, true);
    }
    /**
     * Invoke the system test
     * @param roundID the round id.
     * @param coderID the coder id.
     * @param problemID the problem id.
     * @param failOnFirstBadTest if it is failed when meet with first bad test.
     * @param reference if it is needed to reference
     * @param async (if the system test should be invoked asynchronization)
     */
    public static void systemTest(int roundID, int coderID, int problemID, boolean failOnFirstBadTest, boolean reference, boolean async) {
        trace.debug("---Sys Test requested---");
        BaseRound contest = (BaseRound) CoreServices.getContestRound(roundID);
        if (contest.isLongContestRound() && (coderID > 0 || problemID > 0)) {
            throw new RuntimeException("You cannot specify coder or problem on a Long Round");
        }
        if (coderID <= 0) {
            SystemTestProgress.start(contest.getContestID(), contest.getRoundID(), contest.getRoundType());
            announcePhase(contest.getRoundID(), ContestConstants.SYSTEM_TESTING_PHASE);
        }
        if (!failOnFirstBadTest) {
            trace.warn("Ignoring failOnFirstBadTest=false. This option is not allowed anymore");
        }
        try {
            if (!contest.isLongContestRound()) {
                CoreServices.systemTest(contest.getContestID(), contest.getRoundID(), coderID, problemID, reference, async);
            } else {
                LongContestServicesLocator.getService().startLongSystemTests(contest.getRoundID());
            }
        } catch (CoreServicesException e) {
           throw new RuntimeException(e.getMessage());
        } catch (LongContestServicesException e) {
            throw new RuntimeException(MessageProvider.getText(e.getLocalizableMessage()));
        } catch (Exception e) {
            trace.error("Exception starting system tests",e);
            throw new RuntimeException(e.getMessage());
        }
    }
    

    public static void endContest(int roundID) {
        Round contest = CoreServices.getContestRound(roundID);
        SystemTestProgress.stop(contest.getContestID(), roundID);
        announcePhase(roundID, ContestConstants.CONTEST_COMPLETE_PHASE);
        StageQueue.addTask(new EndContestTask(roundID));
    }

    public static void updatePlace(int roundID) {
        trace.info("Updating place for round: " + roundID);
        CoreServices.updatePlace(roundID);
    }

    public static void endHSContest(int roundID) {
        StageQueue.addTask(new EndHSContestTask(roundID));
    }

    public static void refreshRoundProblems(int round) {
        trace.info("Refreshing round: " + round);
        CoreServices.refreshRoundProblems(round);
    }

    public static void clearPracticeRooms(int type) {
        trace.info("Clearing Practice Rooms, type:" + type );
        CoreServices.clearPracticeRooms(type);
    }

    public static void refreshRoom(int roomID) {
        trace.info("Refreshing room: " + roomID);
        CoreServices.refreshRoom(roomID);
    }

    public static void refreshAllRooms(int roundID) {
        trace.info("Refreshing all rooms in round: " + roundID);
        CoreServices.refreshAllRooms(roundID);
    }

    public static void roundForwarding(String host, int port, boolean enable, String user, String password) {
        trace.info("Adding round forward thread: " + host + ":" + port + ", " + enable);
        CoreServices.roundForwarding(host, port, enable, user, password);
    }

    public static void restoreRound(int round) {
        trace.info("Trying to restore round: NOT IMPLEMENTED" + round);
//    CoreServices.loadAsActiveContest(round);
    }

    public static void showSpecResults() {
        CoreServices.showSpecResults();
    }


    //added by SYHAAS 2002-05-16
    public static void broadcastApprovedQuestion(ApprovedQuestionCommand aqc) {
        ArrayList response = ResponseProcessor.createQuestionBroadcast(aqc.getUser(), aqc.getMessage(), aqc.getRoomID());
        EventService.sendResponseToRoom(aqc.getRoomID(), response);
    }

    public static void addTime(int roundID, int minutes, int seconds, int phase, boolean addToStart) {
        trace.info("Adding " + minutes + ":" + seconds + " to Phase: " + phase);
        Round round = CoreServices.getContestRound(roundID);
        CoreServices.addTimeToContestRound(round, minutes, seconds, phase, addToStart);
        ResponseProcessor.roundSchedule(Processor.getAllTargetConnectionIDs(round), new Long(roundID));
    }

    public static void assignRooms(int roundID, int codersPerRoom, int type, boolean byDivision,
            boolean isFinal, boolean isByRegion, double p) {
        Round contest = CoreServices.getContestRound(roundID);
        CoreServices.refreshRoundProblems(roundID);
        StageQueue.addTask(new AssignRoomsTask(contest, codersPerRoom, type, byDivision,
                isFinal, isByRegion, p));
    }

    public static void setUserStatus(String handle, boolean isActiveStatus) {
        CoreServices.setUserStatus(handle, isActiveStatus);
    }

    public static void enableContestRound(int round) {
        trace.info("Enabling Active Menu round: " + round);
        CoreServices.enableContestRound(round);
    }

    public static void disableContestRound(int round) {
        trace.info("Disabling Active Menu round: " + round);
        CoreServices.disableContestRound(round);
    }

    public static void refreshContestRound(int round) {
        trace.info("Refreshing round: " + round);
        CoreServices.refreshContestRound(round);
    }

    public static void refreshRoomLists(boolean practice, boolean activeContests, boolean lobbies) {
        if (practice) {
            ResponseProcessor.createCategoriesList();
            ResponseProcessor.createPracticeRoomLists();
            //added rfairfax
            ResponseProcessor.sendRefreshPracticeRooms(Processor.getConnectionIDs(TCEvent.ALL_TARGET, -1));
        }
        if (activeContests) {
            ResponseProcessor.refreshActiveContestRoomLists();
        }
        if (lobbies) {
            ResponseProcessor.createLobbyRoomLists();
        }
    }

    // dpecora - New functions that used to be admin chat commands only
    // Code copied from below
    public static void startReplayListener() {
        Thread t = new Thread(new ReplayListener());
        t.start();
    }

    public static void startReplayReceiver() {
        Thread t = new Thread(new ReplayReciever());
        t.start();
    }

    public static String runGarbageCollection() {
        Runtime r = Runtime.getRuntime();
        long totalMem = r.totalMemory() / 1024;
        long freeMem = r.freeMemory() / 1024;
        long usedMem = totalMem - freeMem;
        r.gc();
        long totalMemAfter = r.totalMemory() / 1024;
        long freeMemAfter = r.freeMemory() / 1024;
        long usedMemAfter = totalMemAfter - freeMemAfter;
        String gcmessage = "Before Total = " + totalMem + " Free = " + freeMem + " Used = " + usedMem + " kB.\n";
        gcmessage += "After  Total = " + totalMemAfter + " Free = " + freeMemAfter + " Used = " + usedMemAfter + " kB.\n";
        gcmessage += "Freed Memory = " + (usedMem - usedMemAfter) + " kB.\n";
        return gcmessage;
    }

    public static void advancePhase(int roundID, Integer phaseId) {
        int phase = 0;
        if (phaseId == null) {
            Round contest = CoreServices.getContestRound(roundID);
            phase = contest.getPhase() + 1;
        } else {
            phase = phaseId.intValue();
        }
        announcePhase(roundID, phase);
    }

    static void announcePhase(int roundID, int phase) {
        //SYHAAS 2002-05-19 modified to take MODERATED_CHATTING_PHASE into consideration
        if (phase < ContestConstants.INACTIVE_PHASE || phase > ContestConstants.MODERATED_CHATTING_PHASE) {
            throw new IllegalArgumentException("Invalid Phase = " + phase);
        }
        trace.info("Setting phase = " + phase);
        Round contest = CoreServices.getContestRound(roundID);
        preprocessPhaseChange(contest, phase);
        Processor.processRoundEvent(contest.getContestID(), contest.getRoundID(), phase);
    }

    private static void preprocessPhaseChange(Round contest, int phase) {
        switch (phase) {
            case ContestConstants.SYSTEM_TESTING_PHASE:
                SystemTestProgress.startIfNotRunning(contest.getContestID(), contest.getRoundID(), contest.getRoundType());
                break;
            case ContestConstants.CONTEST_COMPLETE_PHASE:
                SystemTestProgress.stop(contest.getContestID(), contest.getRoundID());
                break;
        }
    }

    private AdminCommands() {
    }

    private static SpecAppController specAppController;

    public static void setSpecAppController(SpecAppController specAppController) {
        AdminCommands.specAppController = specAppController;
    }

    public static void showSpecAppRoom(long roomID) {
        specAppController.setRotating(false);
        specAppController.broadcastShowRoom(new Long(roomID));
    }

    public static void startSpecAppRotation(int delay) {
        specAppController.setRotateDelay(delay);
        specAppController.setRotating(true);
    }

    public static void stopSpecAppRotation() {
        specAppController.setRotating(false);
    }

    public static void announceAdvancingCoders(int roundID, int numAdvancing) {
        specAppController.announceAdvancingCoders(roundID, numAdvancing);
    }

    public static void recycleEventTopicListener() {
        RequestProcessor.recycleEventTopicListener();
    }

    public static void setForwardingAddress(String address) {
        RequestProcessor.setForwardingAddress(address);
    }

    public static void advanceWLCoders(int roundId, int targetRoundId) {
        WeakestLinkRound weakestLinkRound = CoreServices.getWeakestLinkRound(roundId);
        weakestLinkRound.advanceCoders(targetRoundId);
    }

    /**
     * Boots a specified user to any lobby room.
     * Useful when too many users are clogging the admin room.
     */
    public static void bootUser(String handle) {
        Integer connectionID = RequestProcessor.getConnectionID(CoreServices.getUser(handle).getID());
        RequestProcessor.bootUser(connectionID);
    }
}
