package com.topcoder.client.contestant.view;

import com.topcoder.client.contestant.RoundModel;

/**
 * Defines an interface which is notified when the room list of a round has been changed.
 * 
 * @author Qi Liu
 * @version $Id: RoomListListener.java 72032 2008-07-30 06:28:49Z qliu $
 */
public interface RoomListListener {
    /**
     * Called when there is a change to the room list of a round.
     * 
     * @param round the model of the round whose room list is changed.
     */
    void roomListEvent(RoundModel round);

}
