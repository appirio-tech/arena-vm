package com.topcoder.server.contest;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.server.common.Rating;
import com.topcoder.server.common.User;
import com.topcoder.shared.util.logging.Logger;
import java.util.ArrayList;

/**
 * This class is responsible for handling the logic of assigning rooms according to the IronMan
 * divisional structure.
 * This class is not thread safe and should only be accessed from a single thread.
 */
public class DartboardRoomAssigner implements RoomAssigner {

    private static Logger trace = Logger.getLogger(DartboardRoomAssigner.class);

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

    public DartboardRoomAssigner() {
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

        //tickets for the room lottery.  Each person gets one.
        ArrayList tickets = new ArrayList();
        
        for (int i = 0; i < users.size(); i++) {
            tickets.add(new Integer(i));
        }
        
        for (int i = 0; i < numRooms; i++) {
            AssignedRoom room = new AssignedRoom("Room " + m_currentRoomNumber, divisionID, eligible, unrated);
            rooms.add(room);
            
            m_currentRoomNumber++;
            trace.debug("Creating room: " + room.getName() + " with count = " + codersInRoom[i]);
            
            for (int j = 0; j < codersInRoom[i]; j++) {
                double r = Math.random();
                int max = tickets.size() - 1;
                
                int index = (int)Math.round(r * max);
                
                int ticket = ((Integer)tickets.get(index)).intValue();
                tickets.remove(index);
                
                User userToAssign = (User) users.get(ticket);
                
                room.addUser(userToAssign);
            }
        }
        
        if(tickets.size() > 0)
            trace.error("TICKETS LEFT OVER");
        
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

    private int ratingType = Rating.ALGO;
    
    public void initialize(int codersPerRoom, boolean byDivision, boolean byRegion, int ratingType) {
        //no by division, no by region
        m_rooms = codersPerRoom;
        this.ratingType = ratingType;
        
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
