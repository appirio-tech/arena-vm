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
 * This class is responsible for handling the logic of assigning rooms according to the IronMan
 * divisional structure.
 * This class is not thread safe and should only be accessed from a single thread.
 */
public class RandomRoomAssigner implements RoomAssigner {

    private static Logger trace = Logger.getLogger(RandomRoomAssigner.class);

    /**
     * Users with a rating greater or equal to this rating are placed in division one.
     */
    public static final int DIVISION_ONE_RATING_CUTOFF = 1200;

    public static final int DEFAULT_CODERS_PER_ROOM = 20;
    public static final double DEFAULT_P = 1.5;

    public static final boolean USING_INELIGIBLE_SEPARATION = false;

    protected int m_currentRoomNumber;
    protected int m_codersPerRoom;
    protected List m_divisionOneUsers;
    protected List m_divisionOneIneligibleUsers;
    protected List m_divisionTwoUsers;
    protected List m_divisionTwoIneligibleUsers;
    private double m_P;
    /**
     * Unrated users contains the list of both eligible and ineligible users.
     */
    protected List m_unratedUsers;

    public RandomRoomAssigner(double p) {
        m_P = p;
    }

    protected void reset() {
        m_currentRoomNumber = 1;
        m_divisionOneUsers = new ArrayList();
        m_divisionTwoUsers = new ArrayList();
        m_unratedUsers = new ArrayList();

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
            if(byDivision) {
                if (user.getRating(ratingType).getRating() >= DIVISION_ONE_RATING_CUTOFF) {
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
        double total = 0;
        Iterator it = users.iterator();
        double[] ratings = new double[users.size()];
        double[] search = new double[ratings.length];
        for (int i = 0; it.hasNext(); i++) {
            User user = (User) it.next();
            //give the unrateds 500 points, so they go in with everyone else
            double rating = Math.pow((user.getRating(ratingType).getRating()) / 1024.0, m_P);
            if (rating == 0) rating = 0.5;
            total += rating;
            ratings[i] = rating;
        }
        search[0] = ratings[0] / total;
        for (int i = 1; i < search.length; i++) {
            search[i] = ratings[i] / total + search[i - 1];
        }
        for (int i = 0; i < numRooms; i++) {
            AssignedRoom room = new AssignedRoom("Room " + m_currentRoomNumber, divisionID, eligible, unrated);
            rooms.add(room);
            //room.setEligible( eligible );
            //room.setUnrated( unrated );
            m_currentRoomNumber++;
            trace.debug("Creating room: " + room.getName() + " with count = " + codersInRoom[i]);
            for (int j = 0; j < codersInRoom[i]; j++) {
                double rand = Math.random();
                int index = binSearch(search, rand);
                if (ratings[index] == 0)               //something is wrong, probably a rounding error, shouldn't happen very often, but just in case we'll find the highest rated person
                {
                    for (int k = 0; k < ratings.length; k++) {
                        if (ratings[k] != 0) {
                            index = k;
                            break;
                        }
                    }
                }
                total -= ratings[index];
                ratings[index] = 0;
                search[0] = ratings[0] / total;
                for (int k = 1; k < search.length; k++) {
                    search[k] = ratings[k] / total + search[k - 1];
                }
                User userToAssign = (User) users.get(index);
                //System.out.println(userToAssign);
                room.addUser(userToAssign);
            }
        }
        return rooms;
    }

    private static int binSearch(double[] freq, double rand) {
        int low = -1, high = freq.length - 1;
        while (high > low + 1) {
            int med = (low + high) / 2;
            if (rand > freq[med])
                low = med;
            else
                high = med;
        }
        return high;
//        trace.error("binary search failed, resorting to linear");
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
    
    private boolean byDivision = true;
    private int ratingType = Rating.ALGO;

    public void initialize(int codersPerRoom, boolean byDivision, boolean byRegion, int ratingType) {
        m_codersPerRoom = codersPerRoom;
        this.byDivision = byDivision;
        this.ratingType = ratingType;
        //no regional
        
        if (m_codersPerRoom < 1) throw new RuntimeException("Invalid number of coders per room: " + m_codersPerRoom);

    }
}
