/**
 * AnnounceCoderListener.java
 *
 * Description:		Listener for announce coder events
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.event;


public interface AnnounceCoderListener extends java.util.EventListener {

    /**
     * Announces a coder
     *
     * @param evt the event
     */
    public abstract void announceCoder(AnnounceCoderEvent evt);

}


/* @(#)AnnounceCoderListener.java */
