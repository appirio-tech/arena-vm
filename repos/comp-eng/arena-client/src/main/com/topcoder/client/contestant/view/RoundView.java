/*
 * User: Michael Cervantes Date: Aug 8, 2002 Time: 3:05:19 PM
 */
package com.topcoder.client.contestant.view;

import com.topcoder.client.contestant.Contestant;

/**
 * Defines a UI instance which is notified when the list of active rounds has been updated.
 * 
 * @author Michael Cervantes
 * @version $Id: RoundView.java 72032 2008-07-30 06:28:49Z qliu $
 */
public interface RoundView {
    /**
     * Called when the active round list has been updated. The list can be accessed via the network
     * communication instance.
     * 
     * @param model the network communication instance.
     */
    void updateActiveRoundList(Contestant model);

    /**
     * Called when the active round list needs to be cleared.
     */
    void clearRoundList();
}
