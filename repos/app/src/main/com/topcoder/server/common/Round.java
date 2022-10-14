/*
 * Round
 * 
 * Created 05/29/2007
 */
package com.topcoder.server.common;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import com.topcoder.netCommon.contest.round.RoundCustomProperties;
import com.topcoder.netCommon.contest.round.RoundProperties;
import com.topcoder.netCommon.contest.round.RoundType;
import com.topcoder.netCommon.contestantMessages.response.data.ComponentLabel;
import com.topcoder.netCommon.contestantMessages.response.data.PhaseData;
import com.topcoder.netCommon.contestantMessages.response.data.ProblemLabel;
import com.topcoder.server.contest.RoundRoomAssignment;

/**
 * @author Diego Belfer (mural)
 * @version $Id: Round.java 82616 2013-01-10 02:26:16Z FireIce $
 */
public interface Round extends Serializable {

    String getCacheKey();

    boolean isActive();

    void setActive(boolean act);

    int getRoundTypeId();
    
    RoundType getRoundType();

    RoundProperties getRoundProperties();
    
    RoundCustomProperties getRoundCustomProperties();

    String getContestName();

    String getRoundName();
    
    String getDisplayName();

    int getContestID();

    int getRoundID();
    /**
     * <p>
     * get the round event data.
     * </p>
     * @return the round event data.
     */
    RoundEvent getRoundEvent();
    /**
     * <p>
     * set the round event data.
     * </p>
     * @param event
     *         the round event data.
     */
    void setRoundEvent(RoundEvent event);
    
    Iterator getAllRoomIDs();

    ArrayList getAllRoomIDsList();

    ArrayList getAllRoomIDsListClone();

    int getNumRooms();

    void addRoomID(int roomID);

    boolean getActiveMenu();

    void setActiveMenu(boolean active);

    
    /**
     * Gets the details of round room assignment algorithm that should be used 
     * to assign coders to this contest round rooms.
     *
     * @return a RoundRoomAssignment object containing algorithm details
     * @since Admin Tool 2.0
     */
    public RoundRoomAssignment getRoomAssignment();
    
    boolean isModeratedChat();

    boolean isTeamRound();

    boolean isLongRound();
    
    boolean isLongContestRound();

    int getPracticeDivisionID();

    void setPracticeDivisionID(int practiceDivisionID);

    Timestamp getRegistrationStart();

    void setRegistrationStart(Timestamp start);

    Timestamp getRegistrationEnd();

    void setRegistrationEnd(Timestamp end);

    public static final int NO_REGION = -1;

    int getRegion();

    void setRegion(int r);
    
    public static final int NO_SEASON = -1;
    
    int getSeason();
    
    void setSeason(int s);


    /** 
     * Sets the time of start of room assignment segment to specified time.
     *
     * @param  start a time of start of room assignment segment
     * @throws IllegalArgumentExeption if given parameter is null
     * @since  Admin Tool 2.0
     */
    void setRoomAssignmentStart(Timestamp start);

    /** 
     * Sets the time of end of room assignment segment to specified time.
     *
     * @param  end a time of end of room assignment segment
     * @throws IllegalArgumentExeption if given parameter is null
     * @since  Admin Tool 2.0
     */
    void setRoomAssignmentEnd(Timestamp end);

    /** 
     * Gets the time of start of room assignment segment.
     *
     * @return a Timestamp representing time of start of room assignment segment
     * @since  Admin Tool 2.0
     */
    Timestamp getRoomAssignmentStart();

    /** 
     * Gets the time of end of room assignment segment.
     *
     * @return a Timestamp representing time of end of room assignment segment
     * @since  Admin Tool 2.0
     */
    Timestamp getRoomAssignmentEnd();

    /**
     * Sets the details of round room assignment algorithm that should be used
     * to assign coders to this round rooms.
     *
     * @param  details a RoundRoomAssignment object containing the details 
     *         of room assignment algorithm.
     * @throws IllegalArgumentException if given argument is null.
     * @since  Admin Tool 2.0
     */
    void setRoomAssignment(RoundRoomAssignment details);

    /*
     * Sets the room category for the gui practice menu
     */
    void setCategory(int category);

    /*
     * Returns the room category for the gui practice menu
     */
    int getCategory();

    boolean areRoomsAssigned();
    
    Integer getAssignedRoom(int userId);
    
    Integer getNonAdminRoom();
    
    Timestamp getCodingStart();

    void setCodingStart(Timestamp time);

    Timestamp getCodingEnd();

    void setCodingEnd(Timestamp time);

    Timestamp getIntermissionStart();

    void setIntermissionStart(Timestamp time);

    Timestamp getIntermissionEnd();

    void setIntermissionEnd(Timestamp time);

    Timestamp getChallengeStart();

    void setChallengeStart(Timestamp time);

    Timestamp getChallengeEnd();

    void setChallengeEnd(Timestamp time);

    Timestamp getSystemTestStart();

    void setSystemTestStart(Timestamp time);

    Timestamp getSystemTestEnd();

    void setSystemTestEnd(Timestamp time);

    Timestamp getModeratedChatStart();

    void setModeratedChatStart(Timestamp time);

    Timestamp getModeratedChatEnd();

    void setModeratedChatEnd(Timestamp time);

    int getPhase();

    boolean inRegistration();

    boolean inCoding();

    //public final boolean inCoding() { return m_phase == ContestConstants.CODING_PHASE;}
    boolean inChallenge();
    //public final boolean inTesting() { return m_phase == ContestConstants.SYSTEM_TESTING_PHASE;}

    long getPhaseStart();

    long getPhaseEnd();

    Timestamp getVotingEnd();

    Timestamp getTieBreakingVotingEnd();

    Timestamp getNextEventTime();

    void setDivisionComponents(int div, ArrayList components, ArrayList pointVals);

    Collection getDivisions();

    ArrayList getDivisionComponents(int div);

    ArrayList getDivisionProblems(int div);

    int getRoundComponentPointVal(int componentId, int divisionId);

    void beginCountdownPhase();
    // no end countdown since that's registration I guess

    void beginRegistrationPhase();

    void endRegistrationPhase();

    void beginCodingPhase();

    void endCodingPhase();

    void beginChallengePhase();

    void endChallengePhase();

    void beginVotingPhase();

    void beginTieBreakingVotingPhase();

    void beginTestingPhase();

    void endTestingPhase();

    void beginModeratedChattingPhase();

    // clean up stuff here, generate final scores, payouts, etc...
    void finishContest();

    PhaseData getPhaseData();

    ComponentLabel[] getComponentLabels(int divisionID);

    ComponentLabel getComponentLabel(int divisionID, int componentID);

    void scheduleVotingPhaseTasks(Timer timer, TimerTask votingStartTask, TimerTask votingEndTask,
            TimerTask tieBreakingVotingStartTask, TimerTask tieBreakingVotingEndTask);

    void scheduleChallengeEndTask(Timer timer, TimerTask challengeEndTask);

    ProblemLabel[] getProblemLabels(int divisionID);
}