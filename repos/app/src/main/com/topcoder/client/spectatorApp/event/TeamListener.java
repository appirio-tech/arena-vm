/**
 * TeamListener.java
 *
 * Description:		Listener for team events
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.event;


public interface TeamListener extends java.util.EventListener {

    /**
     * Information about the Team
     *
     * @param evt the Team information event
     */
    public abstract void defineTeam(TeamEvent evt);

    /**
     * Show Team event
     *
     * @param evt the Team information event
     */
    public abstract void showTeam(TeamEvent evt);
}

