/*
 * BaseCodingRoom
 * 
 * Created 05/30/2007
 */
package com.topcoder.server.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.util.logging.Logger;

/**
 * @author Diego Belfer (mural)
 * @version $Id: BaseCodingRoom.java 71588 2008-07-10 02:38:40Z dbelfer $
 */
public abstract class BaseCodingRoom extends Room {
    /**
     * Category for logging.
     */
    private static Logger log = Logger.getLogger(BaseCodingRoom.class);

    /**
     * @param name
     * @param id
     * @param roomType
     * @param ratingType
     */
    public BaseCodingRoom(int id, String name, Round contest, int divisionId, int type, int ratingType) {
        super(name, id, type, ratingType);
        setName(name); // Cause the roomNumber to get set.
        m_contestId = contest.getContestID();
        m_roundId = contest.getRoundID();
        m_type = type;
        m_divisionId = divisionId;
        m_components = contest.getDivisionComponents(m_divisionId);
        int componentCount = 0;
        if (m_components != null)
            componentCount = m_components.size();
        if (log.isDebugEnabled()) {
            log.debug("Created ContestRoom: " + m_contestId + "," + m_roundId + ",Div: " + divisionId + ", ProblemCount: "
                    + componentCount);
            log.debug("ProblemIDs = " + m_components);
        }
    }

    private int m_contestId;

    public int getContestID() {
        return m_contestId;
    }

    private int m_roundId;

    public int getRoundID() {
        return m_roundId;
    }

    private int m_divisionId;

    public int getDivisionID() {
        return m_divisionId;
    }

    private ArrayList m_components;

    public ArrayList getComponents() {
        if (log.isDebugEnabled())
            log.debug("getComponents(): " + m_components);
        return m_components;
    }

    private boolean m_eligible = true;

    public boolean isEligible() {
        return m_eligible;
    }

    public void setEligible(boolean value) {
        m_eligible = value;
    }

    private boolean m_unrated = false;

    public boolean isUnrated() {
        return m_unrated;
    }

    public void setUnrated(boolean value) {
        m_unrated = value;
    }

    private double m_leaderPoints = 0;
    // public final double getLeaderPoints() { return m_leaderPoints;}

    private boolean m_closeContest = true;

    public final boolean isCloseContest() {
        return m_closeContest;
    }

    private Coder m_leader;
    // since object isn't serializable
    //We cannot use a plain string, compile would share the instance  
    private String leaderLock = new String("lock");

    public Coder getLeader() {
        return m_leader;
    }
    
    public RoomLeaderInfo getLeaderInfo() {
        synchronized (leaderLock) {
            Coder leader = getLeader();
            return new RoomLeaderInfo(leader, getCoderIndex(leader.getID()) + 1, getPointResolver().getPoints(leader), isCloseContest());
        }
    }
    
    /*
     * public final void setLeader(Coder newLeader) { m_leader = newLeader;
     * m_leaderPoints = m_leader.getPoints(); }
     */

    protected abstract PointResolver getPointResolver();
    
    /**
     * Determines if there is a new leader and updates the room accordingly.
     * Returns true when isCloseStatus changed, the leader pts changed, or
     * leader changed.
     * 
     * This was synced on the method, changed to sync on m_leader
     */
    public boolean updateLeader() {
        PointResolver resolver = getPointResolver();
        synchronized (leaderLock) {
            boolean oldClose = m_closeContest;
            Coder oldLeader = m_leader;
            double oldPoints = m_leaderPoints;
            int oldUserID = -1;
            if (oldLeader != null)
                oldUserID = oldLeader.getID();

            Coder firstPlace = null;
            for (Iterator i = getAllCoders(); i.hasNext();) {
                Coder nextCoder = (Coder) i.next();
                if (firstPlace == null || resolver.getPoints(nextCoder) > resolver.getPoints(firstPlace)) {
                    firstPlace = nextCoder;
                }
            }
            Coder secondPlace = null;
            for (Iterator i = getAllCoders(); i.hasNext();) {
                Coder nextCoder = (Coder) i.next();
                if (firstPlace.getID() != nextCoder.getID()
                        && (secondPlace == null || resolver.getPoints(nextCoder) > resolver.getPoints(secondPlace))) {
                    secondPlace = nextCoder;
                }
            }

            m_leader = firstPlace;
            int leaderUserID = -1;
            if (firstPlace == null) {
                m_leaderPoints = 0;
            } else {
                m_leaderPoints = resolver.getPoints(firstPlace);
                leaderUserID = firstPlace.getID();
            }
            m_closeContest = false;
            if (firstPlace != null && secondPlace != null) {
                m_closeContest = (resolver.getPoints(firstPlace) - resolver.getPoints(secondPlace)) <= (50 * 100);
            }
            return oldClose != m_closeContest || oldPoints != m_leaderPoints || oldUserID != leaderUserID;
        }
    }

    // This was synced, only called from constructor, so I removed the sync
    public void setName(String name) {
        if (name == null)
            name = "Unnamed Room";
        super.setName(name);
        // This is preserved logic from the old system to generate the
        // roomNumber
        if (name.startsWith("Room")) {
            try {
                m_roomNumber = Integer.parseInt(name.substring(5)) - 1;
            } catch (NumberFormatException nfe) {
                log.error("Failed to get room number from name: " + name, nfe);
            }
        }
    }

    private int m_roomNumber = -1;

    public int getRoomNumber() {
        return m_roomNumber;
    }

    /*
     * Data members/getter/setters
     */

    // Collection of all coders assigned to this room.
    // This list must maintain its ordering during a contest for the applet
    // UI to behave correctly.
    private final ArrayList m_assignedCoders = new ArrayList(30);
    private ArrayList m_assignedNames = new ArrayList(30);
    private ArrayList m_assignedRatings = new ArrayList(30);

    // this was method synced, now synced on m_assignedNames
    public void addCoder(Coder c) {
        synchronized (m_assignedNames) {
            if (log.isDebugEnabled()) {
                log.debug("Added Coder: " + c.getID());
            }
            m_coderIDMap.put(new Integer(c.getID()), c);
            int index = -1;
            if (!ContestConstants.isPracticeRoomType(getType())) {
                for (int i = 0; i < m_assignedRatings.size(); i++) {
                    int nextRating = ((Integer) m_assignedRatings.get(i)).intValue();
                    if (c.getRating() > nextRating) {
                        index = i;
                        break;
                    }
                }
            }
            if (index == -1)
                index = m_assignedRatings.size();
            m_assignedCoders.add(index, c);
            m_assignedNames.add(index, c.getName());
            m_assignedRatings.add(index, new Integer(c.getRating()));
        }
    }

    public boolean isUserAssigned(int userID) {
        synchronized (m_coderIDMap) {
            return m_coderIDMap.containsKey(new Integer(userID));
        }
    }

    public ArrayList[] getAssignedCoderData() {
        synchronized (m_assignedNames) {
            ArrayList[] result = new ArrayList[2];
            result[0] = (ArrayList) m_assignedNames.clone();
            result[1] = (ArrayList) m_assignedRatings.clone();
            return result;
        }
    }

    private HashMap m_coderIDMap = new HashMap(10);

    public Coder getCoder(int id) {
        synchronized (m_coderIDMap) {
            Integer coderID = new Integer(id);
            return (Coder) m_coderIDMap.get(coderID);
        }
    }

    // This must return all coders in the same order each time
    // during a contest.
    public Iterator getAllCoders() {
        synchronized (m_assignedCoders) {
            ArrayList coders = (ArrayList) m_assignedCoders.clone();
            return coders.iterator();
        }
    }

    public int getNumCoders() {
        synchronized (m_assignedCoders) {
            return m_assignedCoders.size();
        }
    }

    /**
     * @deprecated everything should be done by ID now, not index!
     * @param index
     *            an index
     * @return a Coder
     */
    public Coder getNthCoder(int index) {
        synchronized (m_assignedCoders) {
            try {
                return (Coder) m_assignedCoders.get(index);
            } catch (IndexOutOfBoundsException e) {
                return null;
            }
        }
    }

    public int getCoderIndex(int coderID) {
        synchronized (m_assignedCoders) {
            int index = -1;
            for (int i = 0; i < m_assignedCoders.size(); i++) {
                Coder coder = (Coder) m_assignedCoders.get(i);
                if (coder.getID() == coderID) {
                    index = i;
                    break;
                }
            }
            return index;
        }
    }

    public int[] getCoderIDs() {
        synchronized (m_assignedCoders) {
            int numCoders = getNumCoders();
            int idlist[] = new int[numCoders];
            for (int i = 0; i < numCoders; i++) {
                Coder coder = (Coder) m_assignedCoders.get(i);
                idlist[i] = coder.getID();
            }
            return idlist;
        }
    }

    /*
     * protected SpectatorTable m_spectatorTable; public SpectatorTable
     * getSpectatorTable() { if (m_spectatorTable == null)
     * buildSpectatorTable(); return (SpectatorTable)m_spectatorTable.clone(); }
     * synchronized protected void buildSpectatorTable() { SpectatorTable table =
     * new SpectatorTable(m_assignedCoders.size()); for (int i=0;i<m_assignedCoders.size();i++) {
     * Coder c = (Coder)m_assignedCoders.get(i); table.add(c); } }
     */

    // Spectator methods
    private List m_spectators = Collections.synchronizedList(new LinkedList());

    public void addSpectator(int userID) {
        m_spectators.add(new Integer(userID));
    }

    public boolean removeSpectator(int userID) {
        return m_spectators.remove(new Integer(userID));
    }

    public Iterator getAllSpectators() {
        ArrayList result = new ArrayList(m_spectators);
        return result.iterator();
    }

    protected List getAssignedNames() {
        return m_assignedNames;
    }

    public String toString() {
        return super.toString() + ", contestID=" + m_contestId + ", roundID=" + m_roundId + ", divisionID="
                + m_divisionId + ", isEligible=" + m_eligible + ", isUnrated=" + m_unrated + ", roomType=" + m_type
                + ", roomNumber=" + m_roomNumber + ", leader=" + m_leader + ", leaderPoints=" + m_leaderPoints
                + ", isCloseContest=" + m_closeContest + ", problems=" + m_components + ", assignedCoders="
                + m_assignedCoders + ", assignedNames=" + m_assignedNames + ", assignedRatings=" + m_assignedRatings
                + ", coderIDMap=" + m_coderIDMap + ", spectators=" + m_spectators;
    }
    
    protected interface PointResolver extends Serializable {
        int getPoints(Coder coder);
    }
}
