package com.topcoder.server.contest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.server.common.Rating;
import com.topcoder.server.common.User;
import com.topcoder.shared.util.logging.Logger;

/**
 * The "Ultra" Random room assigner.  Unlike RandomRoomAssigner, this
 * assigner uses true random seeding
 */
public class UltraRandomRoomAssigner implements RoomAssigner {

    private static Logger trace = Logger.getLogger(UltraRandomRoomAssigner.class);

    /**
     * Users with a rating greater or equal to this rating are placed in division one.
     */
    public static final int DIVISION_ONE_RATING_CUTOFF = 1200;

    public static final boolean USING_INELIGIBLE_SEPARATION = false;

    protected int m_currentRoomNumber;
    protected int m_codersPerRoom;

    protected List m_divisionOneUsers;
    protected List m_divisionOneIneligibleUsers;
    protected List m_divisionTwoUsers;
    protected List m_divisionTwoIneligibleUsers;

    public UltraRandomRoomAssigner() {
    }

    protected void reset() {
        m_currentRoomNumber = 1;
        m_divisionOneUsers = new ArrayList();
        m_divisionTwoUsers = new ArrayList();

        if (USING_INELIGIBLE_SEPARATION) {
            m_divisionOneIneligibleUsers = new ArrayList();
            m_divisionTwoIneligibleUsers = new ArrayList();
        }
    }

    /**
     * Divides the given coders into the Lists for eligible, division and unrated.
     */
    protected void divideUsers(Collection users) {
        for (Iterator allUsers = users.iterator(); allUsers.hasNext();) {
            User user = (User) allUsers.next();
            if(m_byDivision) {
                if (user.getRating(m_ratingType).getRating() >= DIVISION_ONE_RATING_CUTOFF) {
                    if (USING_INELIGIBLE_SEPARATION) {
                        if (user.isEligible()) {
                            m_divisionOneUsers.add(user);
                        } else {
                            m_divisionOneIneligibleUsers.add(user);
                        }
                    } else {
                        m_divisionOneUsers.add(user);
                    }
                } else {
                    if (USING_INELIGIBLE_SEPARATION) {
                        if (user.isEligible()) {
                            m_divisionTwoUsers.add(user);
                        } else {
                            m_divisionTwoIneligibleUsers.add(user);
                        }
                    } else {
                        m_divisionTwoUsers.add(user);
                    }
                }
            } else {
                if (USING_INELIGIBLE_SEPARATION) {
                        if (user.isEligible()) {
                            m_divisionOneUsers.add(user);
                        } else {
                            m_divisionOneIneligibleUsers.add(user);
                        }
                    } else {
                        m_divisionOneUsers.add(user);
                    }
            }
        }

        trace.debug("divideCoders. Total Coders = " + users.size());
        if (m_byDivision) {
            if (USING_INELIGIBLE_SEPARATION) {
                trace.debug("Division One = " + m_divisionOneUsers.size() + " Ineligible = " + m_divisionOneIneligibleUsers.size());
                trace.debug("Division Two = " + m_divisionTwoUsers.size() + " Ineligible = " + m_divisionTwoIneligibleUsers.size());
            } else {
                trace.debug("Division One = " + m_divisionOneUsers.size());
                trace.debug("Division Two = " + m_divisionTwoUsers.size());
            }
        } else {
            if (USING_INELIGIBLE_SEPARATION) {
                trace.debug("Single Division Target = " + m_divisionTarget + " Coders = " + m_divisionOneUsers.size() + " Ineligible = " + m_divisionOneIneligibleUsers.size());
            } else {
                trace.debug("Single Division Target = " + m_divisionTarget + " Coders = " + m_divisionOneUsers.size());
            }
        }
    }

    protected Collection createRooms(List users, int divisionID, boolean eligible, boolean unrated) {
        Collection rooms = new LinkedList();
        if (users.isEmpty()) {
            return rooms;
        }

        int numRooms = (int) Math.ceil((float) users.size() / m_codersPerRoom);
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
        if(m_byDivision) {
            rooms.addAll(createRooms(m_divisionOneUsers, ContestConstants.DIVISION_ONE, true, false));
            rooms.addAll(createRooms(m_divisionTwoUsers, ContestConstants.DIVISION_TWO, true, false));

            if (USING_INELIGIBLE_SEPARATION) {
                rooms.addAll(createRooms(m_divisionOneIneligibleUsers, ContestConstants.DIVISION_ONE, false, false));
                rooms.addAll(createRooms(m_divisionTwoIneligibleUsers, ContestConstants.DIVISION_TWO, false, false));
            }
        } else {
            rooms.addAll(createRooms(m_divisionOneUsers, m_divisionTarget, true, false));
            if (USING_INELIGIBLE_SEPARATION) {
                rooms.addAll(createRooms(m_divisionOneIneligibleUsers, m_divisionTarget, false, false));
            }
        }

        return rooms;
    }

    protected int m_divisionTarget = ContestConstants.DIVISION_ONE;
    boolean m_byDivision = true;
    private int m_ratingType = Rating.ALGO;

    public void initialize(int codersPerRoom, boolean byDivision, boolean byRegion, int ratingType) {
        m_codersPerRoom = codersPerRoom;
        m_byDivision = byDivision;
        m_ratingType = ratingType;
        
        //this is not regional
        if (m_codersPerRoom < 1) throw new RuntimeException("Invalid number of coders per room: " + m_codersPerRoom);
    }
}
