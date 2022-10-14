/*
 * User: Michael Cervantes Date: Aug 6, 2002 Time: 4:17:10 PM
 */
package com.topcoder.client.contestant;

import com.topcoder.client.contestant.view.LeaderListener;
import com.topcoder.client.contestant.view.PhaseListener;
import com.topcoder.client.contestant.view.RoomListListener;
import com.topcoder.client.contestant.view.RoundProblemsListener;
import com.topcoder.netCommon.contest.round.RoundProperties;
import com.topcoder.netCommon.contest.round.RoundType;
import com.topcoder.netCommon.contestantMessages.response.data.LeaderboardItem;
import com.topcoder.netCommon.contestantMessages.response.data.PhaseData;

/**
 * Defines an interface which represents a round.
 * 
 * @author Michael Cervates
 * @version $Id: RoundModel.java 72032 2008-07-30 06:28:49Z qliu $
 */
public interface RoundModel {
    /**
     * Gets the category of the round.
     * 
     * @return the category of the round.
     */
    int getRoundCategoryID();

    /**
     * Gets the unique ID of the round.
     * 
     * @return the unique ID of the round.
     */
    Long getRoundID();

    /**
     * Gets the name of the contest of the round.
     * 
     * @return the contest name.
     */
    String getContestName();

    /**
     * Gets the name of the round.
     * 
     * @return the round name.
     */
    String getRoundName();

    /**
     * Gets the full name of the round (include contest name).
     * 
     * @return the full name of the round.
     */
    String getDisplayName();

    /**
     * Gets the single descriptive name of the round. For marathon practice round, it is the round name. For all others,
     * it is the contest name.
     * 
     * @return the single name of the round.
     */
    String getSingleName();

    /**
     * Gets the round type ID of the round.
     * 
     * @return the round type ID.
     * @see RoundType
     */
    Integer getRoundTypeId();

    /**
     * Gets the round type of the round.
     * 
     * @return the round type.
     */
    RoundType getRoundType();

    /**
     * Gets the properties of the round.
     * 
     * @return the properties of the round.
     */
    RoundProperties getRoundProperties();

    /**
     * Gets the current phase of the round.
     * 
     * @return the current phase of the round.
     */
    Integer getPhase();

    /**
     * Gets the flag indicating if the round menu should be enabled.
     * 
     * @return <code>true</code> if the round menu should be enabled; <code>false</code> otherwise.
     */
    boolean getMenuStatus();

    /**
     * Gets the time left in the current phase, in seconds.
     * 
     * @return the time left in the current phase.
     */
    int getSecondsLeftInPhase();

    /**
     * Gets a flag indicating if the current phase is challenge phase.
     * 
     * @return <code>true</code> if the current phase is challenge phase; <code>false</code> otherwise.
     */
    boolean isInChallengePhase();

    /**
     * Adds a listener which is called when there is a phase change in the round.
     * 
     * @param view the listener to be added.
     */
    void addPhaseListener(PhaseListener view);

    /**
     * Removes a listener which is called when there is a phase change in the round.
     * 
     * @param view the listener to be removed.
     */
    void removePhaseListener(PhaseListener view);

    /**
     * Gets a flag indicating if there is a listener which is called when there is a phase change in the round.
     * 
     * @param view the listener to be tested.
     * @return <code>true</code> if such listener exists; <code>false</code> otherwise.
     */
    boolean containsPhaseListener(PhaseListener view);

    /**
     * Adds a listener which is called when there is a change to the room list in the round.
     * 
     * @param view the listener to be added.
     */
    void addRoomListListener(RoomListListener view);

    /**
     * Removes a listener which is called when there is a change to the room list in the round.
     * 
     * @param view the listener to be removed.
     */
    void removeRoomListListener(RoomListListener view);

    /**
     * Adds a listener which is called when the team assignment of problem component is changed.
     * 
     * @param view the listener to be added.
     */
    void addRoundProblemsListener(RoundProblemsListener view);

    /**
     * Removes a listener which is called when the team assignment of problem component is changed.
     * 
     * @param view the listener to be removed.
     */
    void removeRoundProblemsListener(RoundProblemsListener view);

    /**
     * Adds a listener which is called when the room leader of the round is changed.
     * 
     * @param view the listener to be added.
     */
    void addLeaderListener(LeaderListener view);

    /**
     * Removes a listener which is called when the room leader of the round is changed.
     * 
     * @param view the listener to be removed.
     */
    void removeLeaderListener(LeaderListener view);

    /**
     * Gets a flag indicating if the round has an admin room.
     * 
     * @return <code>true</code> if there is an admin room; <code>false</code> otherwise.
     */
    boolean hasAdminRoom();

    /**
     * Gets the admin room of the round.
     * 
     * @return the admin room of the round.
     */
    RoomModel getAdminRoom();

    /**
     * Gets a flag indicating if the round has rooms assigned for coders.
     * 
     * @return <code>true</code> if the rooms are assigned; <code>false</code> otherwise.
     */
    boolean hasCoderRooms();

    /**
     * Gets the assigned rooms for coders.
     * 
     * @return the assigned rooms for coders.
     */
    RoomModel[] getCoderRooms();

    /**
     * Gets a flag indicating if there are problems for the given division in the round.
     * 
     * @param divisionID the division to be checked.
     * @return <code>true</code> if there are problems for the division; <code>false</code> otherwise.
     */
    boolean hasProblems(Integer divisionID);

    /**
     * Gets the problems for the given division in the round.
     * 
     * @param divisionID the division whose problems are retrieved.
     * @return the problems for the division.
     */
    ProblemModel[] getProblems(Integer divisionID);

    /**
     * Gets team assigned problem components for the given division in the round.
     * 
     * @param divisionID the division whose team assigned components are retrieved.
     * @return the team assigned problem components for the division.
     */
    ProblemComponentModel[] getAssignedComponents(Integer divisionID);

    /**
     * Gets team assigned problem component for the given division and the given component in the round.
     * 
     * @param divisionID the division whose team assigned component is retrieved.
     * @param componentID the component those team assigned component is retrieved.
     * @return the team assigned problem components for the division.
     */
    ProblemComponentModel getAssignedComponent(Integer divisionID, Long componentID);

    /**
     * Gets the problem model for the given problem in the given division of the round.
     * 
     * @param divisionID the division of the problem.
     * @param problemID the ID of the problem.
     * @return the problem model for the problem in the division.
     */
    ProblemModel getProblem(Integer divisionID, Long problemID);

    /**
     * Gets the problem component model for the given problem component in the given division of the round.
     * 
     * @param divisionID the division of the problem component.
     * @param componentID the ID of the problem component.
     * @return the problem component model for the problem component in the division.
     */
    ProblemComponentModel getComponent(Integer divisionID, Long componentID);

    /**
     * Gets a flag indicating if the round has leader board to be displayed.
     * 
     * @return <code>true</code> if the round has leader board; <code>false</code> otherwise.
     */
    boolean hasLeaderboard();

    /**
     * Gets leader information of all rooms in the round.
     * 
     * @return all leader information in the round.
     * @see LeaderboardItem
     */
    LeaderboardItem[] getLeaderboard();

    /**
     * Gets a flag indicating if the round has phase schedule.
     * 
     * @return <code>true</code> if there is phase schedule; <code>false</code> otherwise.
     */
    boolean hasSchedule();

    /**
     * Gets the schedule of all phases in the round.
     * 
     * @return the schedule of all phases.
     * @see PhaseData
     */
    PhaseData[] getSchedule();

    /**
     * Gets a flag indicating if the given coder is a leader of any room in the round.
     * 
     * @param userName the handle of the coder.
     * @return <code>true</code> if the coder is a leader of a room in the round; <code>false</code> otherwise.
     */
    boolean isRoomLeader(String userName);

    /**
     * Gets the room model where the given coder is assigned in the round.
     * 
     * @param handle the handle of the coder.
     * @return the room model where the coder is assigned.
     * @throws IllegalStateException if the coder is not assigned to any room in the round.
     */
    public RoomModel getRoomByCoder(String handle);

    /**
     * Gets a flag indicating if the round summary can be shown.
     * 
     * @return <code>true</code> if the round summary can be shown; <code>false</code> otherwise.
     */
    boolean canDisplaySummary();
}
