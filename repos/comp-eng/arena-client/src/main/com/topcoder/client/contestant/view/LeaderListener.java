/**
 * @author Michael Cervantes (emcee)
 * @since Apr 30, 2002
 */
package com.topcoder.client.contestant.view;

import com.topcoder.client.contestant.RoomModel;

/**
 * Defines an interface which is notified when the leading information of a room has been updated.
 * 
 * @author Michael Cervantes
 * @version $Id: LeaderListener.java 72032 2008-07-30 06:28:49Z qliu $
 */
public interface LeaderListener {
    /**
     * Called when the leading information of the room is updated.
     * 
     * @param room the room whose leading information is updated.
     */
    void updateLeader(RoomModel room);
}
