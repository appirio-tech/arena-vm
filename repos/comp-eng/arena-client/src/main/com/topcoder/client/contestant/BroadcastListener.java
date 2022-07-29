package com.topcoder.client.contestant;

import com.topcoder.netCommon.contestantMessages.AdminBroadcast;

/**
 * Components wishing to receive notification of the various broadcast-related events should implement this interface,
 * and register themselves with the client model.
 * 
 * @author Michael Cervantes (emcee)
 * @since Apr 8, 2002
 * @version $Id: BroadcastListener.java 71772 2008-07-18 07:46:22Z qliu $
 */
public interface BroadcastListener {
    /**
     * Called upon receipt of a new broadcast by the client model.
     * 
     * @param bc the broadcast message.
     */
    public void newBroadcast(AdminBroadcast bc);

    /**
     * Called upon receipt of a list of cached broadcasts.
     */
    public void refreshBroadcasts();

    /**
     * Called upon reading of a broadcast.
     * 
     * @param bc the broadcast message which is read.
     */
    void readBroadcast(AdminBroadcast bc);
}
