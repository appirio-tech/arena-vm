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
public class TCHSRoomAssigner implements RoomAssigner {

    private static Logger trace = Logger.getLogger(TCHSRoomAssigner.class);

    /**
     * Users with a rating greater or equal to this rating are placed in division one.
     */
    public static final int DEFAULT_CODERS_PER_ROOM = 10;

    protected int m_currentRoomNumber;
    protected int m_codersPerRoom;
    protected List m_divisionOneUsers;

    public TCHSRoomAssigner() {
    }

    protected void reset() {
        m_currentRoomNumber = 1;
        m_divisionOneUsers = new LinkedList();
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

    }
    
    private class Team {
        private int id;
        private ArrayList users;
        private ArrayList used;
        
        public Team(int id) {
            this.id = id;
            users = new ArrayList();
            used = new ArrayList();
        }
        
        public boolean hasAvailableUser() {
            for(int i = 0; i < users.size(); i++) {
                if( !((Boolean)used.get(i)).booleanValue() )
                    return true;
            }
            
            return false;
        }
        
        public User getUser() {
            ArrayList tickets = new ArrayList();
            for(int i = 0; i < users.size(); i++) {
                if( !((Boolean)used.get(i)).booleanValue() ) {
                    tickets.add(new Integer(i));
                }
            }
            double r = Math.random();
            int max = tickets.size() - 1;

            int index = (int)Math.round(r * max);

            int ticket = ((Integer)tickets.get(index)).intValue();
            
            used.set(ticket, new Boolean(true));
            
            return (User)users.get(ticket);
        }
        
        public void addUser(User u) {
            users.add(u);
            used.add(new Boolean(false));
        }
        
        public int getID() {
            return id;
        }
    }
    
    private ArrayList teams = new ArrayList();
    
    private void addUserToTeam(int team, User u) {
        Team t = null;
        for(int i = 0; i < teams.size(); i++) {
            Team a = (Team)teams.get(i);
            if(a.getID() == team) {
                t = a;
                break;
            }
        }
        if(t == null) {
            t = new Team(team);
            teams.add(t);
        }
        
        t.addUser(u);
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
        
        //setup team records
        for(int i = 0; i < users.size(); i++) {
            User u = (User)users.get(i);
            addUserToTeam(u.getTeamID(), u);
        }
        
        for (int i = 0; i < numRooms; i++) {
            AssignedRoom room = new AssignedRoom("Room " + m_currentRoomNumber, divisionID, eligible, unrated);
            rooms.add(room);
            
            m_currentRoomNumber++;
            trace.debug("Creating room: " + room.getName() + " with count = " + codersInRoom[i]);
        
            int[] teamcount = new int[teams.size()];
            int count = 0;
            for (int j = 0; j < codersInRoom[i]; j++) {
                //tickets for the room lottery.  Each person gets one.
                ArrayList tickets = new ArrayList();

                count++;
                
                for (int x = 0; x < teams.size(); x++) {
                    Team t = (Team)teams.get(x);
                    if(t.hasAvailableUser()) {
                        for(int y = 0; y < (count - teamcount[x]);y++) {
                            tickets.add(new Integer(x));
                        }
                    }
                }
                
                double r = Math.random();
                int max = tickets.size() - 1;
                
                int index = (int)Math.round(r * max);
                
                int ticket = ((Integer)tickets.get(index)).intValue();
                
                teamcount[ticket]++;
                Team t = (Team)teams.get(ticket);
                User userToAssign = t.getUser();
                
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

    private int ratingType = Rating.HS;
    
    public void initialize(int codersPerRoom, boolean byDivision, boolean byRegion, int ratingType) {
        //no by division, no by region
        m_codersPerRoom = codersPerRoom;
        this.ratingType = ratingType;
        
        if (m_codersPerRoom < 1) throw new RuntimeException("Invalid number of coders per room: " + m_codersPerRoom);

    }
}
