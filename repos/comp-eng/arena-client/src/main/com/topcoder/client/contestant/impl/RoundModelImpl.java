/**
 * @author Michael Cervantes (emcee)
 * @since May 7, 2002
 */
package com.topcoder.client.contestant.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import com.topcoder.client.contestant.Coder;
import com.topcoder.client.contestant.ProblemComponentModel;
import com.topcoder.client.contestant.ProblemModel;
import com.topcoder.client.contestant.RoomModel;
import com.topcoder.client.contestant.RoundModel;
import com.topcoder.client.contestant.view.EventService;
import com.topcoder.client.contestant.view.LeaderListener;
import com.topcoder.client.contestant.view.PhaseListener;
import com.topcoder.client.contestant.view.RoomListListener;
import com.topcoder.client.contestant.view.RoundProblemsListener;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contest.round.RoundCustomProperties;
import com.topcoder.netCommon.contest.round.RoundProperties;
import com.topcoder.netCommon.contest.round.RoundType;
import com.topcoder.netCommon.contestantMessages.response.data.ComponentLabel;
import com.topcoder.netCommon.contestantMessages.response.data.LeaderboardItem;
import com.topcoder.netCommon.contestantMessages.response.data.PhaseData;
import com.topcoder.netCommon.contestantMessages.response.data.ProblemLabel;

/**
 * This class is devoid of any GUI-specific logic.
 * Please keep it that way.
 */
class RoundModelImpl implements RoundModel {

    private Long roundID;
    private String contestName;  // contest name
    private int roundCategoryID;   // round category
    private String roundName;    // round name
    private Integer roundType;       // round type
    private Integer phase;       // current phase
    private boolean menuStatus;
    private ContestantImpl contestantModel;
    private EventService eventService;
    private long endOfPhase;
    private HashSet roomLeadersSet = new HashSet();
    private LeaderboardItem[] leaderboard;
    private RoomModelImpl[] coderRooms;
    private RoomModelImpl adminRoom;
    private HashMap problems = new HashMap();
    private HashMap assignedComponents = new HashMap();
    private PhaseData[] schedule;
    private RoundType roundTypeImpl;
    private RoundProperties roundProperties;

    private Vector leaderListeners = new Vector();

    public RoundModelImpl(ContestantImpl contestantModel, long roundID, String contestName, String roundName, int roundType,
            PhaseData phaseData) {
        this.roundID = new Long(roundID);
        this.contestName = contestName;
        this.roundName = roundName;
        this.roundType = new Integer(roundType);
        this.phase = new Integer(phaseData.getPhaseType());
        this.endOfPhase = phaseData.getEndTime();
        this.contestantModel = contestantModel;
        this.menuStatus = false;
        this.eventService = contestantModel.getEventService();
        this.roundTypeImpl = RoundType.get(roundType);
    }
    
    public RoundModelImpl(ContestantImpl contestantModel, long roundID, String contestName, String roundName, int roundType,
            PhaseData phaseData, int roundCategoryID, RoundCustomProperties customProperties) {
        this(contestantModel,roundID,contestName,roundName,roundType,phaseData);
        this.roundCategoryID = roundCategoryID;
        setRoundCustomProperties(customProperties);
    }
    
    public int getRoundCategoryID() {
        return roundCategoryID;
    }

    public Long getRoundID() {
        return roundID;
    }

    public String getContestName() {
        return contestName;
    }

    public String getRoundName() {
        return roundName;
    }
    
    public String getDisplayName() {
        if(getRoundType().isLongRound()) {
            if(getRoundTypeId().intValue() == ContestConstants.LONG_PROBLEM_TOURNAMENT_ROUND_TYPE_ID) {
                return getContestName() + " - " + getRoundName();
            } else {
                return getContestName();
            }
        } else {
            return getContestName() + " - " + getRoundName();
        }
    }

    public String getSingleName() {
        if (getRoundType().isPracticeRound() && getRoundType().isLongRound()) {
            return getRoundName();
        } else {
            return getContestName();
        }
    }

    public Integer getRoundTypeId() {
        return roundType;
    }

    public synchronized Integer getPhase() {
        return phase;
    }

    public synchronized boolean getMenuStatus() {
        return menuStatus;
    }

    synchronized void setMenuStatus(boolean enabled) {
        menuStatus = enabled;
        Runnable runnable = new Runnable() {
            public void run() {
                for (Iterator it = phaseViews.iterator(); it.hasNext();) {
                    ((PhaseListener) it.next()).enableRound(RoundModelImpl.this);
                }
            }
        };
        eventService.invokeLater(runnable);
    }

    synchronized public int getSecondsLeftInPhase() {
        long left = endOfPhase - contestantModel.getServerTime();
        return left > 0 ? ((int) (left / 1000L)) : 0;
    }

    private Vector phaseViews = new Vector();

    public void addPhaseListener(PhaseListener view) {
        phaseViews.add(view);
    }

    public void removePhaseListener(PhaseListener view) {
        phaseViews.remove(view);
    }
    
    public boolean containsPhaseListener(PhaseListener view) {
    	return phaseViews.contains(view);
    }

    private Vector roomListViews = new Vector();

    public void addRoomListListener(RoomListListener view) {
        roomListViews.add(view);
    }

    public void removeRoomListListener(RoomListListener view) {
        roomListViews.remove(view);
    }

    private Vector roundProblemsViews = new Vector();

    public void addRoundProblemsListener(RoundProblemsListener view) {
        roundProblemsViews.add(view);
    }

    public void removeRoundProblemsListener(RoundProblemsListener view) {
        roundProblemsViews.remove(view);
    }


    synchronized void setPhase(final int phase, long endOfPhase) {
        this.phase = new Integer(phase);
        this.endOfPhase = endOfPhase;
        Runnable runnable = new Runnable() {
            public void run() {
                for (Iterator it = phaseViews.iterator(); it.hasNext();) {
                    ((PhaseListener) it.next()).phaseEvent(phase, RoundModelImpl.this);
                }
            }
        };
        eventService.invokeLater(runnable);
    }

    void updateSystestProgress(final int done, final int total) {
        Runnable runnable = new Runnable() {
            public void run() {
                for (Iterator it = phaseViews.iterator(); it.hasNext();) {
                    ((PhaseListener) it.next()).updateSystestProgress(done, total, RoundModelImpl.this);
                }
            }
        };
        eventService.invokeLater(runnable);
    }

    public synchronized boolean hasLeaderboard() {
        return leaderboard != null;
    }

    public synchronized LeaderboardItem[] getLeaderboard() {
        return leaderboard;
    }

    public boolean isRoomLeader(String userName) {
        return roomLeadersSet.contains(userName);
    }

    synchronized void setLeaderboard(LeaderboardItem[] leaderboard) {
        this.leaderboard = leaderboard;
        this.roomLeadersSet.clear();
        for (int i = 0; i < leaderboard.length; i++) {
            roomLeadersSet.add(leaderboard[i].getUserName());
        }
    }

    synchronized void updateLeaderboard(RoomModelImpl room, LeaderboardItem item) {
        if (leaderboard == null) {
            throw new IllegalStateException("Unitialized leaderboard for round #" + roundID);
        }

        for (int i = 0; i < leaderboard.length; i++) {
            if (leaderboard[i].getRoomID() == item.getRoomID()) {
                roomLeadersSet.remove(leaderboard[i].getUserName());
                updateLeader(room, item, i);
                return;
            } 
        }
        // New room added to the Round
        LeaderboardItem[] newLeaderboard = new LeaderboardItem[leaderboard.length+1];
        System.arraycopy(leaderboard, 0, newLeaderboard, 0, leaderboard.length);
        leaderboard = newLeaderboard;
        updateLeader(room, item, leaderboard.length-1);
    }

    private void updateLeader(RoomModelImpl room, LeaderboardItem item, int i) {
        leaderboard[i] = item;
        roomLeadersSet.add(item.getUserName());
        notifyLeaderListeners(room);
    }


    public synchronized boolean hasAdminRoom() {
        return adminRoom != null;
    }

    public synchronized RoomModel getAdminRoom() {
        return adminRoom;
    }

    synchronized void setAdminRoom(RoomModelImpl adminRoom) {
        this.adminRoom = adminRoom;
        Runnable runnable = new Runnable() {
            public void run() {
                for (Iterator it = roomListViews.iterator(); it.hasNext();) {
                    ((RoomListListener) it.next()).roomListEvent(RoundModelImpl.this);
                }
            }
        };
        eventService.invokeLater(runnable);
    }

    public synchronized boolean hasCoderRooms() {
        return coderRooms != null && coderRooms.length > 0;
    }

    public synchronized RoomModel[] getCoderRooms() {
        return coderRooms;
    }

    synchronized void setCoderRooms(RoomModelImpl[] coderRooms) {
        this.coderRooms = coderRooms;
        Runnable runnable = new Runnable() {
            public void run() {
                for (Iterator it = roomListViews.iterator(); it.hasNext();) {
                    ((RoomListListener) it.next()).roomListEvent(RoundModelImpl.this);
                }
            }
        };
        eventService.invokeLater(runnable);
    }

    public synchronized boolean hasProblems(Integer divisionID) {
        return problems.get(divisionID) != null;
    }

    public synchronized ProblemComponentModel[] getAssignedComponents(Integer divisionID) {
        if (assignedComponents.containsKey(divisionID)) {
            return (ProblemComponentModelImpl[]) assignedComponents.get(divisionID);
        } else {
            throw new IllegalArgumentException("No assigned components for division: " + divisionID);
        }
    }

    public synchronized ProblemComponentModel getAssignedComponent(Integer divisionID, Long componentID) {
        ProblemComponentModel[] assignedComponents = getAssignedComponents(divisionID);
        for (int i = 0; i < assignedComponents.length; i++) {
            if (componentID.equals(assignedComponents[i].getID())) {
                return assignedComponents[i];
            }
        }
        throw new IllegalArgumentException("Component not found in round assigned components: " + componentID);
    }

    public synchronized ProblemModel[] getProblems(Integer divisionID) {
        return getProblemsImpl(divisionID);
    }

    synchronized ProblemModelImpl[] getProblemsImpl(Integer divisionID) {
        if (problems.containsKey(divisionID)) {
            return (ProblemModelImpl[]) problems.get(divisionID);
        } else {
            throw new IllegalArgumentException("No problems for division: " + divisionID);
        }
    }

    public synchronized ProblemModel getProblem(Integer divisionID, Long problemID) {
        return getProblemImpl(divisionID, problemID);
    }

    synchronized ProblemModelImpl getProblemImpl(Integer divisionID, Long problemID) {
        ProblemModelImpl[] divisionProblems = getProblemsImpl(divisionID);
        for (int i = 0; i < divisionProblems.length; i++) {
            ProblemModelImpl divisionProblem = divisionProblems[i];
            if (divisionProblem.getProblemID().equals(problemID)) {
                return divisionProblem;
            }
        }
        throw new IllegalArgumentException("Bad problemID: " + problemID);
    }

    public synchronized ProblemComponentModel getComponent(Integer divisionID, Long componentID) {
        return getComponentImpl(divisionID, componentID);
    }

    synchronized ProblemComponentModelImpl getComponentImpl(Integer divisionID, Long componentID) {
        ProblemModelImpl[] divisionProblems = getProblemsImpl(divisionID);
        for (int i = 0; i < divisionProblems.length; i++) {
            ProblemComponentModel[] components = divisionProblems[i].getComponents();
            for (int j = 0; j < components.length; j++) {
                ProblemComponentModel component = components[j];
                if (component.getID().equals(componentID)) {
                    return (ProblemComponentModelImpl) component;
                }
            }
        }
        throw new IllegalArgumentException("Bad componentID: " + componentID);
    }

    synchronized void setAssignedComponents(Integer divisionID, ComponentLabel[] assignedComponents) {
        ProblemComponentModelImpl[] components = new ProblemComponentModelImpl[assignedComponents.length];
        for (int i = 0; i < assignedComponents.length; i++) {
            components[i] = new ProblemComponentModelImpl(
                    assignedComponents[i].getComponentID(),
                    assignedComponents[i].getComponentTypeID(),
                    assignedComponents[i].getPointValue(),
                    assignedComponents[i].getClassName(),
                    assignedComponents[i].getComponentChallengeData(),
                    getProblem(divisionID, assignedComponents[i].getProblemID()));
        }

        Arrays.sort(components, new Comparator() {
            public int compare(Object o1, Object o2) {
                ProblemComponentModel p1 = (ProblemComponentModel) o1;
                ProblemComponentModel p2 = (ProblemComponentModel) o2;
                return p1.getPoints().compareTo(p2.getPoints());
            }
        });

        this.assignedComponents.put(divisionID, components);

        Runnable runnable = new Runnable() {
            public void run() {
                for (Iterator it = roundProblemsViews.iterator(); it.hasNext();) {
                    ((RoundProblemsListener) it.next()).roundProblemsEvent();
                }
            }
        };
        eventService.invokeLater(runnable);
    }

    synchronized void setProblems(Integer divisionID, ComponentLabel[] assignedComponents, ProblemLabel[] problems) {
        ProblemModelImpl[] divisionProblems = new ProblemModelImpl[problems.length];
        for (int i = 0; i < problems.length; i++) {
            ProblemLabel problem = problems[i];
            divisionProblems[i] = new ProblemModelImpl(problem.getProblemID(), this, divisionID, problem.getName(),
                    problem.getProblemTypeID(), eventService);
            ProblemComponentModelImpl[] components = new ProblemComponentModelImpl[problem.getComponents().length];
            for (int j = 0; j < problem.getComponents().length; j++) {
                components[j] = new ProblemComponentModelImpl(
                        problem.getComponents()[j].getComponentID(),
                        problem.getComponents()[j].getComponentTypeID(),
                        problem.getComponents()[j].getPointValue(),
                        problem.getComponents()[j].getClassName(),
                        problem.getComponents()[j].getComponentChallengeData(),
                        divisionProblems[i]);
            }
            divisionProblems[i].setComponents(components);
        }

        //sort by point value of primary component
        Arrays.sort(divisionProblems, new Comparator() {
            public int compare(Object o1, Object o2) {
                ProblemModelImpl p1 = (ProblemModelImpl) o1;
                ProblemModelImpl p2 = (ProblemModelImpl) o2;
                return p1.getPrimaryComponent().getPoints().compareTo(p2.getPrimaryComponent().getPoints());
            }
        });

        this.problems.put(divisionID, divisionProblems);

        ProblemComponentModelImpl[] components = new ProblemComponentModelImpl[assignedComponents.length];
        for (int i = 0; i < assignedComponents.length; i++) {
            components[i] = new ProblemComponentModelImpl(
                    assignedComponents[i].getComponentID(),
                    assignedComponents[i].getComponentTypeID(),
                    assignedComponents[i].getPointValue(),
                    assignedComponents[i].getClassName(),
                    assignedComponents[i].getComponentChallengeData(),
                    getProblem(divisionID, assignedComponents[i].getProblemID()));
        }

        Arrays.sort(components, new Comparator() {
            public int compare(Object o1, Object o2) {
                ProblemComponentModel p1 = (ProblemComponentModel) o1;
                ProblemComponentModel p2 = (ProblemComponentModel) o2;
                return p1.getPoints().compareTo(p2.getPoints());
            }
        });

        this.assignedComponents.put(divisionID, components);

        Runnable runnable = new Runnable() {
            public void run() {
                for (Iterator it = roundProblemsViews.iterator(); it.hasNext();) {
                    ((RoundProblemsListener) it.next()).roundProblemsEvent();
                }
            }
        };
        eventService.invokeLater(runnable);
    }
    
    public RoomModel getRoomByCoder(String handle) {
        RoomModel[] rm = getCoderRooms();
        Coder cd = null;
        for(int i = 0; i < rm.length;i++)
        {
            cd = rm[i].getCoder(handle);
            if(cd != null)
            {
                return rm[i];
            }
        }
        if (getAdminRoom() != null && getAdminRoom().getCoder(handle) != null) {
            return getAdminRoom();
        }
        throw new IllegalStateException("Cannot find room for " + handle);
    }
 

    public synchronized boolean hasSchedule() {
        return schedule != null;
    }

    public synchronized PhaseData[] getSchedule() {
        return schedule;
    }

    synchronized void setSchedule(PhaseData[] schedule) {
        this.schedule = schedule;
    }

    public synchronized boolean isInChallengePhase() {
        return phase.intValue() == ContestConstants.CHALLENGE_PHASE;
    }

    public void addLeaderListener(LeaderListener view) {
        if(view==null) return;
        synchronized (leaderListeners) {
            if (!leaderListeners.contains(view)) {
                leaderListeners.add(view);
            }
        }
    }

    public void removeLeaderListener(LeaderListener view) {
        leaderListeners.remove(view);
    }


    private void notifyLeaderListeners(final RoomModelImpl room) {
        eventService.invokeLater(new Runnable() {
            public void run() {
                Collection listeners;
                synchronized (leaderListeners) {
                    listeners = new LinkedList(leaderListeners);
                }
                for (Iterator it = listeners.iterator(); it.hasNext();) {
                    LeaderListener leaderListener = (LeaderListener) it.next();
                    if(leaderListener==null) continue;
                    leaderListener.updateLeader(room);
                }
            }
        });
    }

    void setContestName(String contestName) {
        this.contestName = contestName;
    }

    void setRoundName(String roundName) {
        this.roundName = roundName;
    }

    public RoundProperties getRoundProperties() {
        return roundProperties;
    }

    public RoundType getRoundType() {
        return roundTypeImpl;
    }
    
    public boolean canDisplaySummary() {
        return getRoundProperties().isSummaryEnabledDuringContest() || (getPhase() != null & getPhase().intValue() >= ContestConstants.CONTEST_COMPLETE_PHASE);
    }

    public void setRoundCustomProperties(RoundCustomProperties customProperties) {
        this.roundProperties = new RoundProperties(roundTypeImpl, customProperties);
    }
}
