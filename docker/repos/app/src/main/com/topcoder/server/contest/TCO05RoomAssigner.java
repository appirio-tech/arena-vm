package com.topcoder.server.contest;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.server.common.User;
import com.topcoder.shared.util.logging.Logger;

/**
 * This class is responsible for handling the logic of assigning rooms according to the IronMan
 * divisional structure.
 * This class is not thread safe and should only be accessed from a single thread.
 */
public class TCO05RoomAssigner implements RoomAssigner {

    private static Logger trace = Logger.getLogger(TCO05RoomAssigner.class);

    /**
     * Users with a rating greater or equal to this rating are placed in division one.
     */
    public static final int DEFAULT_ROOMS = 10;

    protected int m_currentRoomNumber;
    protected int m_rooms;
    protected List m_divisionOneUsers;
    protected List m_divisionOneIneligibleUsers;
    /**
     * Unrated users contains the list of both eligible and ineligible users.
     */
    protected List m_unratedUsers;

    protected Comparator m_ratingComparator;

    public TCO05RoomAssigner() {

    }

    protected void reset() {
        m_currentRoomNumber = 1;
        m_divisionOneUsers = new LinkedList();
        m_unratedUsers = new LinkedList();

    }

    /**
     * Divides the given coders into the Lists for eligible, division and unrated.
     */
    protected void divideUsers(Collection users) {
        for (Iterator allUsers = users.iterator(); allUsers.hasNext();) {
            User user = (User) allUsers.next();
            m_divisionOneUsers.add(user);
        }
        trace.debug("divideCoders. Total Coders = " + users.size());
        trace.debug("Division One = " + m_divisionOneUsers.size());

        trace.debug("Unrated = " + m_unratedUsers.size());
    }

    protected Collection createRooms(List users, int divisionID, boolean eligible, boolean unrated) {
        Collection rooms = new LinkedList();
        if (users.isEmpty()) {
            return rooms;
        }

        if (!unrated) {
            Collections.sort(users, m_ratingComparator);
        }
        int numRooms = m_rooms;
        int codersPerRoom = users.size() / numRooms;
        int remainingUsers = users.size() % numRooms;
        int[] codersInRoom = new int[numRooms];
        for (int i = 0; i < numRooms; i++) {
            codersInRoom[i] = codersPerRoom;
        }
        // Allocate the remainder coders from the last room forward
        for (int i = numRooms - 1; remainingUsers > 0 && i >= 0; i--) {
            codersInRoom[i]++;
            remainingUsers--;
        }

        for (int i = 0; i < numRooms; i++) {
            AssignedRoom room = new AssignedRoom("Room " + m_currentRoomNumber, divisionID, eligible, unrated);
            rooms.add(room);
            //room.setEligible( eligible );
            //room.setUnrated( unrated );
            m_currentRoomNumber++;
            trace.debug("Creating room: " + room.getName() + " with count = " + codersInRoom[i]);
            for (int j = 0; j < codersInRoom[i]; j++) {
                User userToAssign = (User) users.remove(0);
                room.addUser(userToAssign);
            }
        }
        return rooms;
    }

    /**
     * Assigns the given set of coders to rooms and returns a Collection of ContestRooms created
     * with all the coders assigned.
     */
    public Collection assignRooms(Collection users) {
        reset();
        divideUsers(users);

        Collection rooms = new LinkedList();
        rooms.addAll(createRooms(m_divisionOneUsers, ContestConstants.DIVISION_ONE, true, false));

        return rooms;
    }

    public void initialize(int codersPerRoom, boolean byDivision, boolean byRegion, int ratingType) {
        m_rooms = codersPerRoom;
        if (m_rooms < 1) throw new RuntimeException("Invalid number of coders per room: " + m_rooms);

        m_ratingComparator = new Comparator() {
            public int compare(Object o1, Object o2) {
                User coder1 = (User) o1;
                User coder2 = (User) o2;
                return coder2.getTournamentRating() - coder1.getTournamentRating();
            }
        };
    }
}
