/*
* Copyright (C) 2007 - 2014 TopCoder Inc., All Rights Reserved.
*/

package com.topcoder.server.common;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contest.round.NullRoundCustomProperties;
import com.topcoder.netCommon.contest.round.RoundCustomProperties;
import com.topcoder.netCommon.contest.round.RoundProperties;
import com.topcoder.netCommon.contest.round.RoundType;
import com.topcoder.netCommon.contestantMessages.response.data.ComponentChallengeData;
import com.topcoder.netCommon.contestantMessages.response.data.ComponentLabel;
import com.topcoder.netCommon.contestantMessages.response.data.PhaseData;
import com.topcoder.netCommon.contestantMessages.response.data.ProblemLabel;
import com.topcoder.server.contest.RoundRoomAssignment;
import com.topcoder.server.services.CoreServices;
import com.topcoder.shared.problem.Problem;
import com.topcoder.shared.util.logging.Logger;


/**
 * <p>
 * Changes in version 1.0 (TopCoder Competition Engine - Event Support For Registration v1.0):
 * <ol>
 * <li>Added {@link #getRoundEvent()}  method to get the round event data.</li>
 * <li>Added {@link #setRoundEvent(RoundEvent)} method to set the round event data.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.1 (TopCoder Competiton Engine - Automatically End Matches v1.0) :
 * <ol>
 *      <li>Add {@link #autoEnd} field.</li>
 *      <li>Add {@link #isAutoEnd()} method.</li>
 *      <li>Add {@link #setAutoEnd(boolean autoEnd)} method.</li>
 *      <li>Add {@link #showAutoSystemTestMessage} field.</li>
 *      <li>Add {@link #isShowAutoSystemTestMessage()} method.</li>
 *      <li>Add {@link #setShowAutoSystemTestMessage(boolean isShowAutoSystemTestMessage)} method.</li>
 *      <li>Add {@link #systemTestEnd} method.</li>
 *      <li>Add {@link #isSystemTestEnd()} method.</li>
 *      <li>Add {@link #setSystemTestEnd(boolean)} method.</li>
 * </ol>
 * </p>
 * @author Diego Belfer (mural), TCSASSEMBLER
 * @version 1.1
 */
public abstract class BaseRound implements Serializable, Round {
    private static final Logger log = Logger.getLogger(BaseRound.class);
    private RoundType roundTypeImpl;
    private String m_contestName;
    private String m_roundName;
    private int m_contestID;
    private int m_roundID;
    private String m_cacheKey;
    private boolean m_active;
    private ArrayList m_rooms = new ArrayList(30); // holds the ids of the contest rooms for this contest
    private boolean m_activeMenu = false;
    private RoundCustomProperties customProperties;
    private RoundEvent roundEvent;
    private HashMap m_pointValMap = new HashMap();
    private RoundProperties roundProperties;
    /**
     * Represents a flag indicating if the round is ended automatically.
     *
     * @since 1.1
     */
    private boolean autoEnd;
    /**
     * Represents a flag indicating showAutoSystemTestMessage
     * It only need to show Once.
     *
     * @since 1.1
     */
    private boolean showAutoSystemTestMessage;
    /**
     * Represents a flag to indicate the system test is end.
     * @since 1.1
     */
    private boolean systemTestEnd;

    public BaseRound(int contestId, int roundId, int roundType, String contestName, String roundName) {
         this(contestId, roundId, roundType, contestName, roundName, NullRoundCustomProperties.INSTANCE);
     }
    
    
    public BaseRound(int contestId, int roundId, int roundType, String contestName, String roundName, RoundCustomProperties customProperties) {
        this.m_contestID = contestId;
        this.m_roundID = roundId;
        this.roundTypeImpl = RoundType.get(roundType);
        this.m_contestName = contestName;
        this.m_roundName = roundName;
        this.m_cacheKey = getCacheKey(m_roundID);
        this.customProperties = customProperties;
        this.roundProperties = new RoundProperties(this.roundTypeImpl, this.customProperties);
        setRoomAssignment(new RoundRoomAssignment(roundId));
    }
    /**
     * <p>
     * get the round event data.
     * </p>
     */
    public RoundEvent getRoundEvent() {
        return roundEvent;
    }
    /**
     * <p>
     * set the round event data.
     * </p>
     * @param roundEvent
     *          the round event data.
     */
    public void setRoundEvent(RoundEvent roundEvent) {
        this.roundEvent = roundEvent;
    }
    public String getCacheKey() {
        return m_cacheKey;
    }

    public static String getCacheKey(int roundID) {
        return "Contest:" + roundID;
    }

    public boolean isActive() {
        return m_active;
    }

    public void setActive(boolean act) {
        m_active = act;
    }

    public int getRoundTypeId() {
        return roundTypeImpl.getId();
    }

    public String getContestName() {
        return m_contestName;
    }

    public String getRoundName() {
        return m_roundName;
    }
    
    public String getDisplayName() {
        return m_contestName + " - " + m_roundName;
    }

    public int getContestID() {
        return m_contestID;
    }

    public int getRoundID() {
        return m_roundID;
    }
    
    public final Iterator getAllRoomIDs() {
        synchronized(m_rooms) {
            if (log.isDebugEnabled()) {
                log.debug("getAllRoomIDs() returned:");
                Iterator it = m_rooms.iterator();
                while (it.hasNext()) {
                    int nextRoomId = ((Integer) it.next()).intValue();
                    log.debug("room id " + nextRoomId);
                }
            }
            return m_rooms.iterator();
        }
    }

    public final ArrayList getAllRoomIDsList() {
        synchronized(m_rooms) {
            return m_rooms;
        }
    }
    
    public final ArrayList getAllRoomIDsListClone() {
        synchronized(m_rooms) {
            return (ArrayList)m_rooms.clone();
        }
    }

    public final int getNumRooms() {
        synchronized(m_rooms) {
            return m_rooms.size();
        }
    }

    public final void addRoomID(int roomID) {
        synchronized(m_rooms) {
            m_rooms.add(new Integer(roomID));
        }
    }
    
    protected List getRooms() {
        return m_rooms;
    }
    
    public final boolean getActiveMenu() {
        return m_activeMenu;
    }

    public final void setActiveMenu(boolean active) {
        m_activeMenu = active;
    }
    
    
    
    
    //COPIED
    private int practiceDivisionID;

    public final int getPracticeDivisionID() {
        return practiceDivisionID;
    }

    public final void setPracticeDivisionID(int practiceDivisionID) {
        this.practiceDivisionID = practiceDivisionID;
    }


    public String toString() {
        return "contestID=" + getContestID() + ", roundID=" + getRoundID() + ", contestName=" + getContestName() +
                ", roundName=" + getRoundName() + ", roundType=" + getRoundTypeId() + ", phase=" + m_phase + ", isActive=" + isActive() +
                ", isActiveMenu=" + getActiveMenu() + ", regStart=" + m_regStart + ", regEnd=" + m_regEnd +
                ", assignStart=" + m_assignStart + ", assignEnd=" + m_assignEnd +
                ", codingStart=" + m_codingStart + ", codingEnd=" + m_codingEnd +
                ", intermissionStart=" + m_intermissionStart + ", intermissionEnd=" + m_intermissionEnd +
                ", challengeStart=" + m_challengeStart + ", challengeEnd=" + m_challengeEnd +
                ", systemTestStart=" + m_systemTestStart + ", systemTestEnd=" + m_systemTestEnd +
                ", problemsMap=" + m_componentMap + ", probValMap=" + m_pointValMap +
                ", rooms=" + getRooms() + ", modChatStart=" + m_moderatedChatStart +
                ", modChatEnd=" + m_moderatedChatEnd;
        //return m_contestName+", "+m_roundName+","+m_problemsMap;
    }

    /*
    * Data members/getter/setters
    */

    private Timestamp m_regStart;
    private Timestamp m_regEnd;

    public Timestamp getRegistrationStart() {
        return m_regStart;
    }

    public final void setRegistrationStart(Timestamp start) {
        m_regStart = start;
    }

    public Timestamp getRegistrationEnd() {
        return m_regEnd;
    }

    public final void setRegistrationEnd(Timestamp end) {
        m_regEnd = end;
    }
    
    private int m_region = NO_REGION;
    
    public int getRegion() {
        return m_region;
    }
    
    public void setRegion(int r) {
        m_region = r;
    }
    
    private int m_season = NO_SEASON;
    
    public int getSeason() {
        return m_season;
    }
    
    public void setSeason(int s) {
        m_season = s;
    }


    /**
     * A time of start of room assignment segment.
     *
     * @since Admin Tool 2.0
     */
    private Timestamp m_assignStart = null;

    /**
     * A time of end of room assignment segment.
     *
     * @since Admin Tool 2.0
     */
    private Timestamp m_assignEnd = null;

    /**
     * A details of round room assignment algorithm that should be used to
     * assign coders to round rooms.
     *
     * @since Admin Tool 2.0
     */                                                         
    private RoundRoomAssignment roomAssignmentDetails = null;

    /** 
     * Sets the time of start of room assignment segment to specified time.
     *
     * @param  start a time of start of room assignment segment
     * @throws IllegalArgumentExeption if given parameter is null
     * @since  Admin Tool 2.0
     */
    public void setRoomAssignmentStart(Timestamp start) {
        if( start == null )
            throw new IllegalArgumentException("starting timestamp cannot be null");
        m_assignStart = start;
    }

    /** 
     * Sets the time of end of room assignment segment to specified time.
     *
     * @param  end a time of end of room assignment segment
     * @throws IllegalArgumentExeption if given parameter is null
     * @since  Admin Tool 2.0
     */
    public void setRoomAssignmentEnd(Timestamp end) {
        if( end == null )
            throw new IllegalArgumentException("ending timestamp cannot be null");
        m_assignEnd = end;
    }

    /** 
     * Gets the time of start of room assignment segment.
     *
     * @return a Timestamp representing time of start of room assignment segment
     * @since  Admin Tool 2.0
     */
    public Timestamp getRoomAssignmentStart() {
        return m_assignStart;
    }

    /** 
     * Gets the time of end of room assignment segment.
     *
     * @return a Timestamp representing time of end of room assignment segment
     * @since  Admin Tool 2.0
     */
    public Timestamp getRoomAssignmentEnd() {
        return m_assignEnd;
    }

    /**
     * Sets the details of round room assignment algorithm that should be used
     * to assign coders to this round rooms.
     *
     * @param  details a RoundRoomAssignment object containing the details 
     *         of room assignment algorithm.
     * @throws IllegalArgumentException if given argument is null.
     * @since  Admin Tool 2.0
     */
    public void setRoomAssignment(RoundRoomAssignment details) {
        if( details == null )
            throw new IllegalArgumentException( "details cannot be null");
        roomAssignmentDetails = details;
    }

    /**
     * Gets the details of round room assignment algorithm that should be used 
     * to assign coders to this contest round rooms.
     *
     * @return a RoundRoomAssignment object containing algorithm details
     * @since Admin Tool 2.0
     */
    public RoundRoomAssignment getRoomAssignment() {
        return roomAssignmentDetails;
    }
    
    private int m_category;
    
    /*
     * Sets the room category for the gui practice menu
     */
    public void setCategory(int category) {
        m_category = category;
    }
    
    /*
     * Returns the room category for the gui practice menu
     */
    public int getCategory() {
        return m_category;
    }

    private Timestamp m_codingStart;

    public Timestamp getCodingStart() {
        return m_codingStart;
    }

    public final void setCodingStart(Timestamp time) {
        m_codingStart = time;
    }

    private Timestamp m_codingEnd;

    public Timestamp getCodingEnd() {
        return m_codingEnd;
    }

    public final void setCodingEnd(Timestamp time) {
        m_codingEnd = time;
    }

    private Timestamp m_intermissionStart;

    public Timestamp getIntermissionStart() {
        return m_intermissionStart;
    }

    public final void setIntermissionStart(Timestamp time) {
        m_intermissionStart = time;
    }

    private Timestamp m_intermissionEnd;

    public Timestamp getIntermissionEnd() {
        return m_intermissionEnd;
    }

    public final void setIntermissionEnd(Timestamp time) {
        m_intermissionEnd = time;
    }

    private Timestamp m_challengeStart;

    public Timestamp getChallengeStart() {
        return m_challengeStart;
    }

    public final void setChallengeStart(Timestamp time) {
        m_challengeStart = time;
    }

    private Timestamp m_challengeEnd;

    public Timestamp getChallengeEnd() {
        return m_challengeEnd;
    }

    public final void setChallengeEnd(Timestamp time) {
        m_challengeEnd = time;
    }

    private Timestamp m_systemTestStart;

    public Timestamp getSystemTestStart() {
        return m_systemTestStart;
    }

    public final void setSystemTestStart(Timestamp time) {
        m_systemTestStart = time;
    }

    private Timestamp m_systemTestEnd;

    public Timestamp getSystemTestEnd() {
        return m_systemTestEnd;
    }

    public final void setSystemTestEnd(Timestamp time) {
        m_systemTestEnd = time;
    }

    /*added by SYHAAS 2002-05-18*/
    private Timestamp m_moderatedChatStart;

    public Timestamp getModeratedChatStart() {
        return m_moderatedChatStart;
    }

    public final void setModeratedChatStart(Timestamp time) {
        m_moderatedChatStart = time;
    }

    /*added by SYHAAS 2002-05-18*/
    private Timestamp m_moderatedChatEnd;

    public Timestamp getModeratedChatEnd() {
        return m_moderatedChatEnd;
    }

    public final void setModeratedChatEnd(Timestamp time) {
        m_moderatedChatEnd = time;
    }

    private int m_phase = ContestConstants.INACTIVE_PHASE;

    public final int getPhase() {
        return m_phase;
    }

    public boolean inRegistration() {
        return m_phase == ContestConstants.REGISTRATION_PHASE;
    }

    public final boolean inCoding() {
        return m_phase == ContestConstants.CODING_PHASE;
    }
    //public final boolean inCoding() { return m_phase == ContestConstants.CODING_PHASE;}
    public final boolean inChallenge() {
        return m_phase == ContestConstants.CHALLENGE_PHASE;
    }
    //public final boolean inTesting() { return m_phase == ContestConstants.SYSTEM_TESTING_PHASE;}

    public final long getPhaseStart() {
        switch (m_phase) {
        case ContestConstants.INACTIVE_PHASE:
        case ContestConstants.STARTS_IN_PHASE:
            return 0;
        case ContestConstants.REGISTRATION_PHASE:
            return getRegistrationStart().getTime();
        case ContestConstants.ALMOST_CONTEST_PHASE:
            return getRegistrationEnd().getTime();
        case ContestConstants.CODING_PHASE:
            return getCodingStart().getTime();
        case ContestConstants.INTERMISSION_PHASE:
            return getIntermissionStart().getTime();
        case ContestConstants.CHALLENGE_PHASE:
            return getChallengeStart().getTime();
        case ContestConstants.VOTING_PHASE:
            return getChallengeEnd().getTime();
        case ContestConstants.TIE_BREAKING_VOTING_PHASE:
            return getVotingEnd().getTime();
        case ContestConstants.MODERATED_CHATTING_PHASE:
            return getModeratedChatStart().getTime();
        case ContestConstants.PENDING_SYSTESTS_PHASE:
        case ContestConstants.SYSTEM_TESTING_PHASE:
        case ContestConstants.CONTEST_COMPLETE_PHASE:
            return 0;
        default:
            log.warn("getPhaseStart(), Unknown phase (" + m_phase + ").");
            return 0;
        }
    }

    public long getPhaseEnd() {
        long phaseEnd;
        switch (m_phase) {
        case ContestConstants.INACTIVE_PHASE:
        case ContestConstants.STARTS_IN_PHASE:
            phaseEnd = getRegistrationStart().getTime();
            break;
        case ContestConstants.REGISTRATION_PHASE:
            phaseEnd = getRegistrationEnd().getTime();
            break;
        case ContestConstants.ALMOST_CONTEST_PHASE:
            phaseEnd = getCodingStart().getTime();
            break;
        case ContestConstants.CODING_PHASE:
            phaseEnd = getCodingEnd().getTime();
            break;
        case ContestConstants.INTERMISSION_PHASE:
            phaseEnd = getIntermissionEnd().getTime();
            break;
        case ContestConstants.CHALLENGE_PHASE:
            phaseEnd = getChallengeEnd().getTime();
            break;
        case ContestConstants.VOTING_PHASE:
            phaseEnd = getVotingEnd().getTime();
            break;
        case ContestConstants.TIE_BREAKING_VOTING_PHASE:
            phaseEnd = getTieBreakingVotingEnd().getTime();
            break;
        case ContestConstants.MODERATED_CHATTING_PHASE:
            phaseEnd = getModeratedChatEnd().getTime();
            break;
        case ContestConstants.PENDING_SYSTESTS_PHASE:
        case ContestConstants.SYSTEM_TESTING_PHASE:
        case ContestConstants.CONTEST_COMPLETE_PHASE:
            phaseEnd = 0;
            break;
        default:
            throw new RuntimeException("Unknown phase (" + m_phase + ").");
        }
        return phaseEnd;
    }

    public Timestamp getVotingEnd() {
        return new Timestamp(0);
    }

    public Timestamp getTieBreakingVotingEnd() {
        return new Timestamp(0);
    }

    public final Timestamp getNextEventTime() {
        
        switch (m_phase) {
        case ContestConstants.INACTIVE_PHASE:
            return isModeratedChat() ? getModeratedChatStart() : getRegistrationStart();
        case ContestConstants.STARTS_IN_PHASE:
            return getRegistrationStart();
        case ContestConstants.REGISTRATION_PHASE:
            return getRegistrationEnd();
        case ContestConstants.ALMOST_CONTEST_PHASE:
            return getCodingStart();
        case ContestConstants.CODING_PHASE:
            return getCodingEnd();
        case ContestConstants.INTERMISSION_PHASE:
            return getIntermissionEnd();
        case ContestConstants.CHALLENGE_PHASE:
            return getChallengeEnd();
        case ContestConstants.MODERATED_CHATTING_PHASE:
            return getModeratedChatEnd();
        case ContestConstants.PENDING_SYSTESTS_PHASE:
        case ContestConstants.SYSTEM_TESTING_PHASE:
        case ContestConstants.CONTEST_COMPLETE_PHASE:
            return null;
        default:
            log.warn("getNextEventTime(), Unknown phase (" + m_phase + ").");
            return null;
        }
    }

    private HashMap m_componentMap = new HashMap(10);

    public final void setDivisionComponents(int div, ArrayList components, ArrayList pointVals) {
        if (log.isDebugEnabled()) {
            log.debug("setDivisionComponents: div = " + div + " Components = " + components + " Points = " + pointVals);
        }
        m_componentMap.put(new Integer(div), components);
        if (components.size() != pointVals.size()) {
            log.error("Really bad, components size is not the same as point vals!!!");
            return;
        }

        // update the point map
        HashMap pointMap = new HashMap();
        for (int i = 0; i < components.size(); i++) {
            pointMap.put(components.get(i), pointVals.get(i));
        }
        m_pointValMap.put(new Integer(div), pointMap);
    }


    public final Collection getDivisions() {
        return m_componentMap.keySet();
    }


    public final ArrayList getDivisionComponents(int div) {
        Integer divID = new Integer(div);
        if (m_componentMap.containsKey(divID)) {
            return (ArrayList) m_componentMap.get(divID);
        } else {
            throw new IllegalArgumentException("Bad division: " + div);
        }
    }

    public final ArrayList getDivisionProblems(int div) {
        Integer divID = new Integer(div);
        if (m_componentMap.containsKey(divID)) {
            ArrayList components = (ArrayList) m_componentMap.get(divID);
            ArrayList problems = new ArrayList();
            for (int i = 0; i < components.size(); i++) {
                Integer problemId = new Integer(
                        CoreServices.getRoundComponent(getRoundID(),
                                ((Integer) components.get(i)).intValue(),
                                div).getComponent().getProblemID());
                if (!problems.contains(problemId)) {
                    problems.add(problemId);
                }
            }
            return problems;
        } else {
            throw new IllegalArgumentException("Bad division: " + div);
        }
    }

    public final int getRoundComponentPointVal(int componentId, int divisionId) {
        HashMap divisionPointValMap = (HashMap) m_pointValMap.get(new Integer(divisionId));
        if (divisionPointValMap == null) {
            throw new IllegalArgumentException("Bad division ID: " + divisionId);
        }
        Integer pointValue = (Integer) divisionPointValMap.get(new Integer(componentId));
        if (pointValue == null) {
            throw new IllegalArgumentException("Bad component ID: " + componentId);
        }
        return pointValue.intValue();
    }

    public final void beginCountdownPhase() {
        m_phase = ContestConstants.STARTS_IN_PHASE;
    }
    // no end countdown since that's registration I guess

    public final void beginRegistrationPhase() {
        m_phase = ContestConstants.REGISTRATION_PHASE;
    }

    public final void endRegistrationPhase() {
        m_phase = ContestConstants.ALMOST_CONTEST_PHASE;
    }

    public final void beginCodingPhase() {
        m_phase = ContestConstants.CODING_PHASE;
    }

    public final void endCodingPhase() {
        m_phase = ContestConstants.INTERMISSION_PHASE;
    }

    public final void beginChallengePhase() {
        m_phase = ContestConstants.CHALLENGE_PHASE;
    }

    public final void endChallengePhase() {
        // TODO : should this auto transition to system test?
        m_phase = ContestConstants.PENDING_SYSTESTS_PHASE;
    }

    public final void beginVotingPhase() {
        m_phase = ContestConstants.VOTING_PHASE;
    }

    public final void beginTieBreakingVotingPhase() {
        m_phase = ContestConstants.TIE_BREAKING_VOTING_PHASE;
    }

    public final void beginTestingPhase() {
        m_phase = ContestConstants.SYSTEM_TESTING_PHASE;
    }

    public final void endTestingPhase() {
        m_phase = ContestConstants.CONTEST_COMPLETE_PHASE;
    }

    public final void beginModeratedChattingPhase()//added by SYHAAS 2002-05-18
    {
        m_phase = ContestConstants.MODERATED_CHATTING_PHASE;
    }

    // clean up stuff here, generate final scores, payouts, etc...
    public final void finishContest() {
        setActive(false);
        m_phase = ContestConstants.INACTIVE_PHASE;
    }

    public final PhaseData getPhaseData() {
        return new PhaseData(getRoundID(), getPhase(), getPhaseStart(), getPhaseEnd());
    }


    public final ComponentLabel[] getComponentLabels(int divisionID) {
        List components = getDivisionComponents(divisionID);
        ComponentLabel[] r = new ComponentLabel[components.size()];
        int k = 0;
        for (Iterator componentIterator = components.iterator(); componentIterator.hasNext();) {
            Integer componentID = (Integer) componentIterator.next();
            ComponentLabel label = getComponentLabel(divisionID, componentID.intValue());
            r[k++] = label;
        }
        return r;
    }

    public ComponentLabel getComponentLabel(int divisionID, int componentID) {
        RoundComponent rc = CoreServices.getRoundComponent(getRoundID(), componentID, divisionID);
        ComponentLabel label = new ComponentLabel(
                rc.getComponent().getClassName(),
                rc.getComponent().getProblemID(),
                rc.getComponent().getComponentID(),
                rc.getPointVal(),
                divisionID,
                rc.getComponent().getComponentTypeID(),
                createComponentChallengeData(rc.getComponent().getProblemID()));
        if (log.isDebugEnabled())
            log.debug("Adding componentID : " + componentID + " Points = " + rc.getPointVal());
        return label;
    }

    public void scheduleVotingPhaseTasks(Timer timer, TimerTask votingStartTask, TimerTask votingEndTask,
            TimerTask tieBreakingVotingStartTask, TimerTask tieBreakingVotingEndTask) {
    }

    public void scheduleChallengeEndTask(Timer timer, TimerTask challengeEndTask) {
        timer.schedule(challengeEndTask, getChallengeEnd());
    }

    public final ProblemLabel[] getProblemLabels(int divisionID) {
        List problems = getDivisionProblems(divisionID);
        ProblemLabel[] r = new ProblemLabel[problems.size()];
        int k = 0;
        for (Iterator problemIterator = problems.iterator(); problemIterator.hasNext();) {
            Integer problemID = (Integer) problemIterator.next();
            Problem rp = CoreServices.getProblem(problemID.intValue());
            ProblemLabel label = new ProblemLabel(
                    rp.getName(),
                    rp.getProblemId(),
                    divisionID,
                    rp.getProblemTypeID(),
                    getComponentLabelsForProblem(rp.getProblemId(), divisionID));
            if (log.isDebugEnabled())
                log.debug("Adding problemID: " + problemID);
            r[k++] = label;
        }
        return r;
    }

    protected ComponentLabel[] getComponentLabelsForProblem(int problemID, int divisionID) {
        ArrayList al_components = new ArrayList();
        List divisionComponents = getDivisionComponents(divisionID);
        RoundComponent roundComponent;
        for (int i = 0; i < divisionComponents.size(); i++) {
            roundComponent = CoreServices.getRoundComponent(getRoundID(), ((Integer) divisionComponents.get(i)).intValue(), divisionID);
            if (roundComponent.getComponent().getProblemID() == problemID) {
                al_components.add(new ComponentLabel(
                        roundComponent.getComponent().getClassName(),
                        problemID,
                        roundComponent.getComponent().getComponentID(),
                        roundComponent.getPointVal(),
                        divisionID,
                        roundComponent.getComponent().getComponentTypeID(),
                        createComponentChallengeData(problemID)));
            }
        }
        return (ComponentLabel[]) al_components.toArray(new ComponentLabel[al_components.size()]);
    }

    protected abstract ComponentChallengeData createComponentChallengeData(int problemID);
    
    
    public Integer getNonAdminRoom() {
        Iterator roomIDs = getAllRoomIDs();
        while (roomIDs.hasNext()) {
            int roomID = ((Integer) roomIDs.next()).intValue();
            Room nextRoom = CoreServices.getRoom(roomID, false);
            if (!nextRoom.isAdminRoom()) {
                if (log.isDebugEnabled()) log.debug("Got nonadmin room: " + roomID);
                return new Integer(roomID);
            }
        }
        return null;
    }
    
    public Integer getAdminRoom() {
        Iterator roomIDs = getAllRoomIDs();
        while (roomIDs.hasNext()) {
            int roomID = ((Integer) roomIDs.next()).intValue();
            Room nextRoom = CoreServices.getRoom(roomID, false);
            if (nextRoom.isAdminRoom()) {
                if (log.isDebugEnabled()) log.debug("Got admin room: " + roomID);
                return new Integer(roomID);
            }
        }
        return null;
    }
    
    public RoundProperties getRoundProperties() {
        return roundProperties;
    }

    public RoundType getRoundType() {
        return roundTypeImpl;
    }
    
    public RoundCustomProperties getRoundCustomProperties() {
        return customProperties;
    }
    
    public void setCustomProperties(RoundCustomProperties customProperties) {
        this.customProperties = customProperties;
        this.roundProperties = new RoundProperties(roundTypeImpl, this.customProperties);
    }

    /**
     * Gets the end automatically flag.
     *
     * @return autoEnd the end automatically flag
     * @since 1.1
     */
    public boolean isAutoEnd() {
        return autoEnd;
    }

    /**
     * Sets the end automatically flag.
     *
     * @param autoEnd the end automatically flag
     * @since 1.1
     */
    public void setAutoEnd(boolean autoEnd) {
        this.autoEnd = autoEnd;
    }
    /**
     * Gets the show auto system test message flag.
     *
     * @return the auto system test message flag.
     * @since 1.1
     */
    public boolean isShowAutoSystemTestMessage() {
    	return showAutoSystemTestMessage;
    }
    /**
     * Sets the show auto system test message flag.
     * @param showAutoSystemTestMessage the auto system test message flag.
     * @since 1.1
     */
    public void setShowAutoSystemTestMessage(boolean showAutoSystemTestMessage) {
    	this.showAutoSystemTestMessage = showAutoSystemTestMessage;
    }
    /**
     * Gets the system test end flag
     * @return the system test end flag.
     * @since 1.1
     */
    public boolean isSystemTestEnd() {
    	return systemTestEnd;
    }
    /**
     * Sets the system test end flag.
     * @param systemTestEnd the system test end flag.
     * @since 1.1
     */
    public void setSystemTestEnd(boolean systemTestEnd) {
    	this.systemTestEnd = systemTestEnd;
    }
}
