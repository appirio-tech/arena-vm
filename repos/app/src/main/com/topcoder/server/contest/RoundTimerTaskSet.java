/*
 * User: Michael Cervantes
 * Date: Sep 8, 2002
 * Time: 9:34:24 AM
 */
package com.topcoder.server.contest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contest.round.RoundType;
import com.topcoder.server.common.ContestRound;
import com.topcoder.server.common.Round;
import com.topcoder.server.processor.AdminCommands;
import com.topcoder.server.processor.Processor;
import com.topcoder.server.services.CoreServices;
import com.topcoder.shared.util.logging.Logger;

/**
 * Modifications for AdminTool 2.0 are :
 * <p>New private variable of type TimerTask added to start the room assignment
 * algorithm when the registration phase ends.
 * <p>New private inner class AssignRoomsTimerTask extending TimerTask is declared 
 * to run room assignment algorithm at the appropriate time.
 * <p>Existing cancel() and schedule(Timer) methods are modified to take into 
 * consideration newly defined "room assignment" segment of contest round.
 * 
 * @author TCDEVELOPER
 */
public final class RoundTimerTaskSet {
    private static final int TIME_BEFORE_STARTING_SYSTEM_TEST = 60000*10;

    private static final Logger log = Logger.getLogger(RoundTimerTaskSet.class);

    private final long contestID;
    private final long roundID;

    private final class RoundEventTimerTask extends TimerTask {

        private final int phase;

        private RoundEventTimerTask(int phase) {
            this.phase = phase;
        }

        public void run() {
            firePhaseEvent(phase);
        }
    }

    private final TimerTask countDownStartTask;
    private final TimerTask registrationStartTask;
    private final TimerTask almostContestStartTask;
    private final TimerTask codingStartTask;
    private final TimerTask intermissionStartTask;
    private final TimerTask challengeStartTask;
    private final TimerTask pendingSystemTestStartTask;
    private final TimerTask votingStartTask = new RoundEventTimerTask(ContestConstants.VOTING_PHASE);
    private final TimerTask votingEndTask = new RoundEventTimerTask(ContestConstants.TIE_BREAKING_VOTING_PHASE);
    private final TimerTask tieBreakingVotingStartTask = new RoundEventTimerTask(ContestConstants.TIE_BREAKING_VOTING_PHASE);
    private final TimerTask contestCompleteBeginTask = new RoundEventTimerTask(ContestConstants.CONTEST_COMPLETE_PHASE);
    private TimerTask systemTestStartTask;
    
    /**
     * A TimerTask that should start a round room assignment process for 
     * round that this RoundTimerTaskSet was created for. This variable should
     * be initialized with new AssignRoomsTimerTask instance.
     *
     * @since Admin Tool 2.0
     */
    private TimerTask roomAssignmentStartTask = null;  

    /**
     * Time to wait before unloading the round after the coding phase ended
     */
    private long waitTimeToUnloadAfterCoding;  


    public RoundTimerTaskSet(long contestID, long roundID, long waitTimeToUnloadAfterCoding) {
        this.contestID = contestID;
        this.roundID = roundID;
        this.waitTimeToUnloadAfterCoding = waitTimeToUnloadAfterCoding;
        countDownStartTask = new RoundEventTimerTask(ContestConstants.STARTS_IN_PHASE);
        registrationStartTask = new RoundEventTimerTask(ContestConstants.REGISTRATION_PHASE);
        almostContestStartTask = new RoundEventTimerTask(ContestConstants.ALMOST_CONTEST_PHASE);
        roomAssignmentStartTask = new AssignRoomsTimerTask(getRound().getRoomAssignment());
        codingStartTask = new RoundEventTimerTask(ContestConstants.CODING_PHASE);
        intermissionStartTask = new RoundEventTimerTask(ContestConstants.INTERMISSION_PHASE);
        challengeStartTask = new RoundEventTimerTask(ContestConstants.CHALLENGE_PHASE);
        pendingSystemTestStartTask = new RoundEventTimerTask(ContestConstants.PENDING_SYSTESTS_PHASE);
        systemTestStartTask = null;
    }

    private Round getRound() {
        return CoreServices.getContestRound((int) roundID);
    }


    private DateFormat logDateFormat = new SimpleDateFormat("h:mm a");

    /**
     * This method is modified to create new AssignRoomsTimerTask with
     * details of round room assignment algorithm obtained from ContestRound
     * and schedule it to be run 1 minute after registration phase ends.
     *
     * @see ContestRound#getRoomAssignment()
     * @see ContestRound#getRoomAssignmentStart()
     */ 
    public void schedule(Timer timer, boolean sendPastEvent)
    {
        Round round = getRound();
        Date currentTime = new Date();
        log.info("Scheduling round @ " + logDateFormat.format(currentTime) + ": " + round);

        Date regStart = round.getRegistrationStart();
        Date regEnd = round.getRegistrationEnd();
        Date assignStart = round.getRoomAssignmentStart();
        Date assignEnd = round.getRoomAssignmentEnd();
        Date codingStart = round.getCodingStart();
        Date codingEnd = round.getCodingEnd();
        Date challengeStart = round.getChallengeStart();
        Date challengeEnd = round.getChallengeEnd();
        
        if (round.isLongContestRound()) {
            processLongRound(timer, sendPastEvent, round, currentTime, regStart, regEnd, assignStart, assignEnd, codingStart, codingEnd, challengeStart, challengeEnd);
        } else if (round.getRoundType().equals(RoundType.EDUCATION_ALGO_ROUND_TYPE)){
            processEducationRound(timer, sendPastEvent, round, currentTime, regStart, regEnd, assignStart, assignEnd, codingStart, codingEnd, challengeStart, challengeEnd);
        } else {
            processNonLongRound(timer, sendPastEvent, round, currentTime, regStart, regEnd, assignStart, assignEnd, codingStart, codingEnd, challengeStart, challengeEnd);
        }
    }

    private void processLongRound(Timer timer, boolean sendPastEvent, Round round, Date currentTime, Date regStart, Date regEnd, Date assignStart, Date assignEnd, Date codingStart, Date codingEnd, Date challengeStart, Date challengeEnd) {
        if(sendPastEvent) {
            if (currentTime.before(regStart)) {
                timer.schedule(countDownStartTask, currentTime); // fire right away
            }
            if (currentTime.before(regEnd)) {
                timer.schedule(registrationStartTask, regStart);
            }
            if (currentTime.before(codingEnd)) {
                //We add 1 ms to avoid bad queue handling done by the Timer.
                timer.schedule(codingStartTask, new Date(codingStart.getTime()+1));
            }
            if (codingEnd.before(challengeStart) && currentTime.before(challengeStart)) {
                //We have intermission
                timer.schedule(intermissionStartTask, codingEnd);
            }
            timer.schedule(pendingSystemTestStartTask, challengeEnd);
        }
        else {
            log.info("PASTEVENT = false");
            if (currentTime.before(regStart)) {
                timer.schedule(registrationStartTask, regStart);
            }
            if (currentTime.before(codingStart)) {
                timer.schedule(codingStartTask, new Date(codingStart.getTime()+1));
            }
            if (codingEnd.before(challengeStart) && currentTime.before(codingEnd)) {
                timer.schedule(intermissionStartTask, codingEnd);
            }
            //We don't have challenge, so 
            timer.schedule(pendingSystemTestStartTask, challengeEnd);
            Processor.processRoundEventAddTime(round.getContestID(), round.getRoundID(), round.getPhase());
        }
    }
    
    private void processEducationRound(Timer timer, boolean sendPastEvent, Round round, Date currentTime, Date regStart, Date regEnd, Date assignStart, Date assignEnd, Date codingStart, Date codingEnd, Date challengeStart, Date challengeEnd) {
        systemTestStartTask = new StartSystemTestsTimerTask((int) roundID);
        if(sendPastEvent) {
            if (currentTime.before(codingStart)) {
                timer.schedule(almostContestStartTask, currentTime); // fire right away
            }
            if (currentTime.before(codingEnd)) {
                timer.schedule(codingStartTask, codingStart);
                timer.schedule(pendingSystemTestStartTask, codingEnd);
                timer.schedule(systemTestStartTask, new Date(codingEnd.getTime()+TIME_BEFORE_STARTING_SYSTEM_TEST));
            } else {
                timer.schedule(contestCompleteBeginTask, currentTime);
            }
            Date unloadTime = new Date(codingEnd.getTime()+waitTimeToUnloadAfterCoding);
            if (currentTime.before(unloadTime)) {
                timer.schedule(new UnloadRoundTimerTask((int) roundID), unloadTime);
            }
        }
        else {
            log.info("PASTEVENT = false");
            if (currentTime.before(codingStart)) {
                timer.schedule(codingStartTask, codingStart);
            }
            if (currentTime.before(codingEnd)) {
                timer.schedule(pendingSystemTestStartTask, codingEnd);
                timer.schedule(systemTestStartTask, new Date(codingEnd.getTime()+TIME_BEFORE_STARTING_SYSTEM_TEST));
            } else {
                timer.schedule(contestCompleteBeginTask, currentTime);
            }
            Processor.processRoundEventAddTime(round.getContestID(), round.getRoundID(), round.getPhase());
        }
    }
    
    private void processNonLongRound(Timer timer, boolean sendPastEvent, Round round, Date currentTime, Date regStart, Date regEnd, Date assignStart, Date assignEnd, Date codingStart, Date codingEnd, Date challengeStart, Date challengeEnd) {
        if(sendPastEvent) {
            if (currentTime.before(regStart)) {
                timer.schedule(countDownStartTask, currentTime); // fire right away
            }
            if (currentTime.before(regEnd)) {
                timer.schedule(registrationStartTask, regStart);
                timer.schedule(almostContestStartTask, regEnd);
            }
            // we may not have a room assignment start/end. (pre 2.0 Contests)
            if( assignEnd != null && assignStart != null ) {
                if (currentTime.before(assignEnd)) {
                    timer.schedule(roomAssignmentStartTask, assignStart );
                }
            }
            if (currentTime.before(codingEnd)) {
                timer.schedule(codingStartTask, codingStart);
            }
            if (currentTime.before(challengeStart)) {
                timer.schedule(intermissionStartTask, codingEnd);
            }
            if (currentTime.before(challengeEnd)) {
                timer.schedule(challengeStartTask, challengeStart);
            }
            round.scheduleChallengeEndTask(timer, pendingSystemTestStartTask);
            round.scheduleVotingPhaseTasks(timer, votingStartTask, votingEndTask, tieBreakingVotingStartTask, contestCompleteBeginTask);
        }
        else {
            log.info("PASTEVENT = false");
            if (currentTime.before(regStart)) {
                timer.schedule(registrationStartTask, regStart);
            }
            if (currentTime.before(regEnd)) {
                timer.schedule(almostContestStartTask, regEnd);
            }
            // we may not have a room assignment start/end. (pre 2.0 Contests)
            if( assignEnd != null && assignStart != null ) {
                if (currentTime.before(assignEnd)) {
                    timer.schedule(roomAssignmentStartTask, assignStart );
                }
            }
            if (currentTime.before(codingStart)) {
                timer.schedule(codingStartTask, codingStart);
            }
            if (currentTime.before(codingEnd)) {
                timer.schedule(intermissionStartTask, codingEnd);
            }
            if (currentTime.before(challengeStart)) {
                timer.schedule(challengeStartTask, challengeStart);
            }
            round.scheduleChallengeEndTask(timer, pendingSystemTestStartTask);
            round.scheduleVotingPhaseTasks(timer, votingStartTask, votingEndTask, tieBreakingVotingStartTask, contestCompleteBeginTask);
            
            Processor.processRoundEventAddTime(round.getContestID(), round.getRoundID(), round.getPhase());
        }
    }
    
    public void schedule(Timer timer) {
        schedule(timer, true);
    }

    public void cancel() {
        countDownStartTask.cancel();
        registrationStartTask.cancel();
        almostContestStartTask.cancel();
        roomAssignmentStartTask.cancel();
        codingStartTask.cancel();
        intermissionStartTask.cancel();
        challengeStartTask.cancel();
        pendingSystemTestStartTask.cancel();
        votingStartTask.cancel();
        votingEndTask.cancel();
        tieBreakingVotingStartTask.cancel();
        contestCompleteBeginTask.cancel();
        if (systemTestStartTask != null) {
            systemTestStartTask.cancel();
        }
    }

    private void firePhaseEvent(int phase) {
        log.info("FIRED EVENT: " + phase);
        Processor.processRoundEvent((int) contestID, (int) roundID, phase);
    }
    
    /**
     * A TimerTask that should run round room assignment algorithm 1 minute
     * after registration phase ends.
     *
     * @author  TCSDESIGNER
     * @bersion 1.0 07/31/2003
     * @since   Admin Tool 2.0
     */        
    private final class AssignRoomsTimerTask extends TimerTask {

        /**
         * The details of round room assignment algorithm that should be used
         * to assign coders to rooms.
         */
        private RoundRoomAssignment details = null;

        /**
         * Constructs new TimerTask that will use specified round rooms
         * assignment details to assign coders to rooms.
         *
         * @param  details a RoundRoomAssignment object containing details of
         *         round rooms assignment algorithm
         * @throws IllegalArgumentException if given argument is null.
         */
        private AssignRoomsTimerTask(RoundRoomAssignment details) {
            if( details == null )
                throw new IllegalArgumentException("details cannot be null");
            this.details = details;
        }

        /**
         * Performs the room assignment process. To do so gets all necessary
         * data from RoundRoomAssigment object passed to constructor and
         * issues <code>AdminCommands.assignRooms()</code> method with 
         * appropriate parameters.
         *
         * @see AdminCommands#assignRooms()
         */
        public void run() {
            log.info("AssignRoomsTimerTask fired - Round ID =" + details.getRoundId() + 
                    " Type =" + details.getType() +
                    " Coders Per Room=" + details.getCodersPerRoom() +
                    " Is By Division=" + details.isByDivision() +
                    " Is Final=" + details.isFinal() + 
                    " Is By Region=" + details.isByRegion() +
                    " P = " + details.getP());
            AdminCommands.assignRooms(details.getRoundId(), details.getCodersPerRoom(),
                    details.getType(), details.isByDivision(), 
                    details.isFinal(), details.isByRegion(), details.getP());
        }
    }
    
    
    private static class StartSystemTestsTimerTask extends TimerTask {
        private int roundId;
        
        public StartSystemTestsTimerTask(int roundId) {
            this.roundId = roundId;
        }

        public void run() {
            log.info("Auto starting system test for round Id="+roundId);
            try {
                //This is ugly, why I should use an admin command? That needs to be refactored.
                AdminCommands.systemTest(roundId, 0, 0, false, false);
            } catch (Exception e) {
                log.error("Exception while starting system tests", e);
            }
        }
    }
    
    private static class UnloadRoundTimerTask extends TimerTask {
        private int roundId;
        
        public UnloadRoundTimerTask(int roundId) {
            this.roundId = roundId;
        }

        public void run() {
            log.info("Auto unloading round Id="+roundId);
            try {
                Processor.unloadContestRound(roundId);
            } catch (Exception e) {
                log.error("Exception unloading round", e);
            }
        }
    }
}
