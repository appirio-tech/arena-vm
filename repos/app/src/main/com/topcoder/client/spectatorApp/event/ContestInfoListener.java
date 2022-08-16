/**
 * ContestInfoListener.java
 *
 * Description:		Listener for contest info events
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.event;


public interface ContestInfoListener extends java.util.EventListener {

    /**
     * Information about the contest
     *
     * @param evt the contest information event
     */
    public abstract void contestInfo(ContestInfoEvent evt);

}


/* @(#)ContestInfoListener.java */
