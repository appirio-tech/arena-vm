/**
 * VoteListener.java
 *
 * Description:		Listener for vote events
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.event;


public interface VoteListener extends java.util.EventListener {

    /**
     * Information about who voted for who
     *
     * @param evt the Vote information event
     */
    public abstract void votedFor(VoteEvent evt);

    /**
     * Information about who was voted out
     *
     * @param evt the Vote information event
     */
    public abstract void votedOut(VoteEvent evt);
}

