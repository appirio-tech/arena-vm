package com.topcoder.server.contest;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.server.common.Rating;
import com.topcoder.server.common.User;
import com.topcoder.shared.util.logging.Logger;

/**
 * This class is responsible for handling the logic of assigning rooms according to the IronMan
 * divisional structure.
 * This class is not thread safe and should only be accessed from a single thread.
 */
public class IronmanRoomAssigner implements RoomAssigner {

    private static Logger trace = Logger.getLogger(IronmanRoomAssigner.class);

    /**
     * Users with a rating greater or equal to this rating are placed in division one.
     */
    public static final int DIVISION_ONE_RATING_CUTOFF = 1200;

    public static final int DEFAULT_CODERS_PER_ROOM = 10;

    public static final boolean USING_INELIGIBLE_SEPARATION = false;

    protected int m_currentRoomNumber;
    protected int m_codersPerRoom;
    protected List m_divisionOneUsers;
    protected List m_divisionOneIneligibleUsers;
    protected List m_divisionTwoUsers;
    protected List m_divisionTwoIneligibleUsers;
    protected boolean byDivision;
    protected boolean isByRegion;
    /**
     * Unrated users contains the list of both eligible and ineligible users.
     */
    protected List m_unratedUsers;

    protected Comparator m_ratingComparator;

    public IronmanRoomAssigner() {
    }

    protected void reset() {
        m_currentRoomNumber = 1;
        m_divisionOneUsers = new LinkedList();
        m_divisionTwoUsers = new LinkedList();
        m_unratedUsers = new LinkedList();

        if (USING_INELIGIBLE_SEPARATION) {
            m_divisionOneIneligibleUsers = new LinkedList();
            m_divisionTwoIneligibleUsers = new LinkedList();
        }
    }

    /**
     * Divides the given coders into the Lists for eligible, division and unrated.
     */
    protected void divideUsers(Collection users) {
        for (Iterator allUsers = users.iterator(); allUsers.hasNext();) {
            User user = (User) allUsers.next();
            if (!byDivision || user.getRating(m_ratingType).getRating() >= DIVISION_ONE_RATING_CUTOFF) {
                if (USING_INELIGIBLE_SEPARATION) {
                    if (user.isEligible()) {
                        m_divisionOneUsers.add(user);
                    } else {
                        m_divisionOneIneligibleUsers.add(user);
                    }
                } else {
                    m_divisionOneUsers.add(user);
                }
            } else if (user.getRating(m_ratingType).getNumRatings() > 0) {
                if (USING_INELIGIBLE_SEPARATION) {
                    if (user.isEligible()) {
                        m_divisionTwoUsers.add(user);
                    } else {
                        m_divisionTwoIneligibleUsers.add(user);
                    }
                } else {
                    m_divisionTwoUsers.add(user);
                }
            } else {
                m_unratedUsers.add(user);
            }
        }
        trace.debug("divideCoders. Total Coders = " + users.size());
        if (USING_INELIGIBLE_SEPARATION) {
            trace.debug("Division One = " + m_divisionOneUsers.size() + " Ineligible = " + m_divisionOneIneligibleUsers.size());
            trace.debug("Division Two = " + m_divisionTwoUsers.size() + " Ineligible = " + m_divisionTwoIneligibleUsers.size());
        } else {
            trace.debug("Division One = " + m_divisionOneUsers.size());
            trace.debug("Division Two = " + m_divisionTwoUsers.size());
        }
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
        rooms.addAll(createRooms(m_divisionTwoUsers, ContestConstants.DIVISION_TWO, true, false));
        rooms.addAll(createRooms(m_unratedUsers, ContestConstants.DIVISION_TWO, true, true));

        if (USING_INELIGIBLE_SEPARATION) {
            rooms.addAll(createRooms(m_divisionOneIneligibleUsers, ContestConstants.DIVISION_ONE, false, false));
            rooms.addAll(createRooms(m_divisionTwoIneligibleUsers, ContestConstants.DIVISION_TWO, false, false));
        }

        return rooms;
    }
    
    private int m_ratingType = Rating.ALGO;

    public void initialize(int codersPerRoom, boolean byDivision, boolean byRegion, int ratingType) {
        m_codersPerRoom = codersPerRoom;
        this.byDivision = byDivision;
        this.isByRegion = byRegion;
        this.m_ratingType = ratingType;
        if (m_codersPerRoom < 1) throw new RuntimeException("Invalid number of coders per room: " + m_codersPerRoom);

        if(!isByRegion) {
            m_ratingComparator = new Comparator() {
                public int compare(Object o1, Object o2) {
                    User coder1 = (User) o1;
                    User coder2 = (User) o2;
                    return coder2.getRating(m_ratingType).getRating() - coder1.getRating(m_ratingType).getRating();
                }
            };
        } else {
            m_ratingComparator = new Comparator() {
                public int compare(Object o1, Object o2) {
                    User coder1 = (User) o1;
                    User coder2 = (User) o2;
                    if(coder1.getSeed() == coder2.getSeed())
                        return coder2.getRating(m_ratingType).getRating() - coder1.getRating(m_ratingType).getRating();
                    
                    return coder1.getSeed() - coder2.getSeed();
                }
            };
        }
    }
}
