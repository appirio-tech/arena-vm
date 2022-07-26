/**
 * ContestListener.java
 *
 * Description:		Listener for contest info events
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.event;


public interface ContestListener extends java.util.EventListener {

    /**
     * Information about the contest
     *
     * @param evt the contest information event
     */
    public abstract void defineContest(ContestEvent evt);

}


/* @(#)ContestInfoListener.java */
