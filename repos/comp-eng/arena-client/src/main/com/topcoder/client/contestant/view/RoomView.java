/**
 * @author Michael Cervantes (emcee)
 * @since May 10, 2002
 */
package com.topcoder.client.contestant.view;

import com.topcoder.client.contestant.RoomModel;

/**
 * Defines a UI instance which is presenting a room. All room data can be accessed via the model of the room.
 * 
 * @author Michael Cervantes
 * @version $Id: RoomView.java 72032 2008-07-30 06:28:49Z qliu $
 */
public interface RoomView {
    /**
     * Sets the model of the room to be presented by this UI instance.
     * 
     * @param model the model of the room.
     */
    void setModel(RoomModel model);
}
