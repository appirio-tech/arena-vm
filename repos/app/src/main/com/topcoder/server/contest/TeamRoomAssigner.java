package com.topcoder.server.contest;

import java.util.*;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.server.common.*;
import com.topcoder.shared.util.logging.*;

/**
 * This class is responsible for handling the logic of assigning rooms according to the IronMan
 * divisional structure.
 * This class is not thread safe and should only be accessed from a single thread.
 */
public class TeamRoomAssigner implements RoomAssigner {

    private static Logger trace = Logger.getLogger(TeamRoomAssigner.class);

    private static final int DEFAULT_TEAMS_PER_ROOM = 2;
    public static final int DIVISION_ONE_RATING_CUTOFF = 1200;

    protected int m_currentRoomNumber;
    protected int m_codersPerRoom;
    protected List m_divisionOneTeams;
    protected List m_divisionOneIneligibleTeams;
    protected List m_divisionTwoTeams;
    protected List m_divisionTwoIneligibleTeams;
    /**
     * Unrated users contains the list of both eligible and ineligible users.
     */
    protected List m_unratedTeams;

    protected Comparator m_ratingComparator;

    protected void reset() {
        m_currentRoomNumber = 1;
        m_divisionOneTeams = new LinkedList();
        m_divisionOneIneligibleTeams = new LinkedList();
        m_divisionTwoTeams = new LinkedList();
        m_divisionTwoIneligibleTeams = new LinkedList();
        m_unratedTeams = new LinkedList();
    }

    /**
     * Divides the given coders into the Lists for eligible, division and unrated.
     */
    protected void divideTeams(Collection teams) {
        for (Iterator allTeams = teams.iterator(); allTeams.hasNext();) {
            Team team = (Team) allTeams.next();
            if (team.getRating() >= DIVISION_ONE_RATING_CUTOFF) {
                if (team.isEligible()) {
                    m_divisionOneTeams.add(team);
                } else {
                    m_divisionOneIneligibleTeams.add(team);
                }
            } else if (team.getNumRatedEvents() > 0) {
                if (team.isEligible()) {
                    m_divisionTwoTeams.add(team);
                } else {
                    m_divisionTwoIneligibleTeams.add(team);
                }
            } else {
                m_unratedTeams.add(team);
            }
        }
        trace.debug("divideTeams. Total Teams = " + teams.size());
        trace.debug("Division One = " + m_divisionOneTeams.size() + " Ineligible = " + m_divisionOneIneligibleTeams.size());
        trace.debug("Division Two = " + m_divisionTwoTeams.size() + " Ineligible = " + m_divisionTwoIneligibleTeams.size());
        trace.debug("Unrated = " + m_unratedTeams.size());
    }

    protected Collection createRooms(List teams, int divisionID, boolean eligible, boolean unrated) {
        Collection rooms = new LinkedList();
        if (teams.isEmpty()) {
            return rooms;
        }

        if (!unrated) {
            Collections.sort(teams, m_ratingComparator);
        }
        int max = m_codersPerRoom;
        int numRooms = (int) Math.ceil((float) teams.size() / max);
        int teamsPerRoom = teams.size() / numRooms;
        while (teamsPerRoom == 1 && teams.size() > 1) {
            max++;
            numRooms = (int) Math.ceil((float) teams.size() / max);
            teamsPerRoom = teams.size() / numRooms;
        }
        int remainingTeams = teams.size() % numRooms;
        int[] teamsInRoom = new int[numRooms];
        for (int i = 0; i < numRooms; i++) {
            teamsInRoom[i] = teamsPerRoom;
        }
        // Allocate the remainder coders from the last room forward
        for (int i = numRooms - 1; remainingTeams > 0 && i >= 0; i--) {
            teamsInRoom[i]++;
            remainingTeams--;
        }

        for (int i = 0; i < numRooms; i++) {
            AssignedTeamRoom room = new AssignedTeamRoom("Room " + m_currentRoomNumber, divisionID);
            rooms.add(room);
            room.setEligible(eligible);
            room.setUnrated(unrated);
            m_currentRoomNumber++;
            trace.debug("Creating room: " + room.getName() + " with count = " + teamsInRoom[i]);
            for (int j = 0; j < teamsInRoom[i]; j++) {
                Team teamToAssign = (Team) teams.remove(0);
                room.addTeam(teamToAssign);
            }
        }
        return rooms;
    }

    /**
     * Assigns the given set of coders to rooms and returns a Collection of ContestRooms created
     * with all the coders assigned.
     */
    public Collection assignRooms(Collection teams) {
        reset();
        divideTeams(teams);

        Collection rooms = new LinkedList();
        rooms.addAll(createRooms(m_divisionOneTeams, ContestConstants.DIVISION_ONE, true, false));
        rooms.addAll(createRooms(m_divisionOneIneligibleTeams, ContestConstants.DIVISION_ONE, false, false));
        rooms.addAll(createRooms(m_divisionTwoTeams, ContestConstants.DIVISION_TWO, true, false));
        rooms.addAll(createRooms(m_divisionTwoIneligibleTeams, ContestConstants.DIVISION_TWO, false, false));
        rooms.addAll(createRooms(m_unratedTeams, ContestConstants.DIVISION_TWO, true, true));
        return rooms;
    }

    public void initialize(int codersPerRoom, boolean byDivision, boolean byRegion, int ratingType) {
        m_codersPerRoom = codersPerRoom;
        m_ratingComparator = new Comparator() {
            public int compare(Object o1, Object o2) {
                Team team1 = (Team) o1;
                Team team2 = (Team) o2;
                return team2.getRating() - team1.getRating();
            }
        };
    }

    public TeamRoomAssigner() {
    }

}
