/**
 * Class Room
 *
 * Author: Hao Kung
 *
 * Description: This class will contain information about a room in the system
 */
package com.topcoder.server.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import com.topcoder.server.services.EventService;

public class Room implements Serializable {

    private String m_cacheKey;
    protected int m_type;

    public String getCacheKey() {
        return m_cacheKey;
    }

    /*
     * Constructors
     */
    public Room(String name, int id, int roomType, int ratingType) {
        m_name = name;
        m_id = id;
        m_type = roomType;
        m_ratingType = ratingType;
        m_cacheKey = getCacheKey(m_id);
    }

    /*
     * Data members/getter/setters
     */
    
    private int m_ratingType;
    
    /**
     * Gets the rating type to be displayed for the room
     * @returns rating type
     */
    public int getRatingType() {
       return m_ratingType; 
    }
    
    /**
     * Sets the rating type to be displayed for this room
     * @params type The rating type to display
     */
    public void setRatingType(int type) {
        this.m_ratingType = type;
    }

    // Room name
    private String m_name;

    public final String getName() {
        return m_name;
    }

    public void setName(String name) {
        m_name = name;
    }

    public final String getDescription() {
        return m_name.substring(5);
    }

    protected int m_id;

    public final int getRoomID() {
        return m_id;
    }

    private int m_capacity = -1;

    public final int getCapacity() {
        return m_capacity;
    }

    public final void setCapacity(int capacity) {
        m_capacity = capacity;
    }

    public final int getOccupancy() {
        return m_users.size();
    }

    private boolean m_isAdminRoom = false;

    public final boolean isAdminRoom() {
        return m_isAdminRoom;
    }

    public final void setAdminRoom(boolean value) {
        m_isAdminRoom = value;
    }

    private ArrayList m_userNames = new ArrayList();
    private ArrayList m_userRatings = new ArrayList();

    public ArrayList[] getUserData() {
        synchronized (m_users) {
            ArrayList[] results = new ArrayList[2];
            results[0] = (ArrayList) m_userNames.clone();
            results[1] = (ArrayList) m_userRatings.clone();
            return results;
        }
    }

    public final ArrayList getUserNames() {
        synchronized (m_users) {
            return (ArrayList) m_userNames.clone();
        }
    }
    /*
	public final ArrayList getUserRatings() {
		synchronized (m_users) {
			return (ArrayList)m_userRatings.clone();
		}
	}
    */


    // Collection of all user ids in the room(includes observers??)
    private Collection m_users = new HashSet();

    public final void enter(User u) {
        Integer userID = new Integer(u.getID());
        synchronized (m_users) {
            if (m_users.add(userID)) {
                m_userNames.add(u.getName());
                m_userRatings.add(new Integer(u.getRating(m_ratingType).getRating()));
            }
            if (m_capacity > 0 && m_capacity == m_userNames.size()) {
                System.out.println(u.getID());
                LobbyFullEvent lfe = new LobbyFullEvent(u.getID(), m_name, true);
                EventService.sendGlobalEvent(lfe);
            }
        }
    }

    public final void leave(User u) {
        synchronized (m_users) {
            if (m_users.remove(new Integer(u.getID()))) {
                int index = -1;
                String name = u.getName();
                if (m_capacity > 0 && m_capacity == m_userNames.size()) {
                    LobbyFullEvent lfe = new LobbyFullEvent(u.getID(), m_name, false);
                    EventService.sendGlobalEvent(lfe);
                }
                index = m_userNames.indexOf(name);
                if (index != -1) {
                    m_userNames.remove(name);
                    m_userRatings.remove(index);
                }
            }
        }
    }

    public static String getCacheKey(int id) {
        return "Room.Id." + id;
    }

    public String toString() {
        try {
            return "id=" + m_id + ", name=" + m_name + ", type=" + m_type + ", isAdminRoom=" + m_isAdminRoom + ", users=" + m_users +
                    ", userNames=" + m_userNames + ", userRatings=" + m_userRatings;
        } catch (Throwable t) {
            return "room.toString() threw " + t;
        }
    }

    public int getType() {
        return m_type;
    }
}
