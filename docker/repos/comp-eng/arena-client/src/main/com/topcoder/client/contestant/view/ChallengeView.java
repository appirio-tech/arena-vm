/**
 * @author Michael Cervantes (emcee)
 * @since May 7, 2002
 */
package com.topcoder.client.contestant.view;

import com.topcoder.client.contestant.RoomModel;

/**
 * Defines a UI instance which is responsible to present room summary table. The coder may use this room summary table
 * to select the solution to be viewed and challenged.
 * 
 * @author Michael Cervantes
 * @version $Id: ChallengeView.java 72010 2008-07-29 08:46:53Z qliu $
 */
public interface ChallengeView {
    /**
     * Called when there is an update on the room summary table.
     * 
     * @param room the room model whose summary table is updated.
     */
    void updateChallengeTable(RoomModel room);
}
