/**
 * RoundListener.java
 *
 * Description:		Listener for contest info events
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.event;


public interface RoundListener extends java.util.EventListener {

    /**
     * Information about the round
     *
     * @param evt the round information event
     */
    public abstract void defineRound(RoundEvent evt);

    /**
     * Show round event
     *
     * @param evt the round information event
     */
    public abstract void showRound(RoundEvent evt);
}


/* @(#)ContestInfoListener.java */
