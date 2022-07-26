package com.topcoder.client.spectatorApp.scoreboard.model;

/**
 * TeamManager.java
 *
 * Description:		The manager of Teams
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Category;

import com.topcoder.client.spectatorApp.event.TeamAdapter;
import com.topcoder.client.spectatorApp.event.TeamEvent;
import com.topcoder.client.spectatorApp.netClient.SpectatorEventProcessor;

public class TeamManager {

    /** Reference to the logging category */
    private static final Category cat = Category.getInstance(RoomManager.class.getName());

    /** Singleton instance */
    private static TeamManager teamManager = null;

    /** Handler for Team definitions */
    private TeamHandler teamHandler = new TeamHandler();

    /** List holding all the Teams */
    private HashMap teams = new HashMap();

    /**
     * Constructor of a Team manager.  This registers a Team listener witht the event processor
     */
    private TeamManager() {
        // Register the handler as a listener
        SpectatorEventProcessor.getInstance().addTeamListener(teamHandler);

    }

    /**
     * Retreives the singleton instance
     * @returns RoomManager the singleton Team manager
     */
    public static synchronized TeamManager getInstance() {
        if (teamManager == null) teamManager = new TeamManager();
        return teamManager;
    }

    /**
     * Disposes of any resources used
     */
    public void dispose() {
        // Removes the handler as a listener
        SpectatorEventProcessor.getInstance().removeTeamListener(teamHandler);

        // Allow each room to dispose of any resources
        for (Iterator itr = teams.values().iterator(); itr.hasNext();) {
            ((Team) itr.next()).dispose();
        }
    }


    /**
     * Returns the first team
     * @return the first team or null if none
     */
    public Team getFirstTeam() {
        if (teams.size() == 0) return null;
        return (Team) teams.values().iterator().next();
    }

    /**
     * Returns the Team that the coder is assigned to
     * @param coderID the coder identifier
     * @return the team they are assigned to or null if not found
     */
    public Team getAssignedTeam(int coderID) {
        // Loop through all the teams
        for (Iterator itr = teams.values().iterator(); itr.hasNext();) {
            // Get the team
            Team team = (Team) itr.next();

            // Is the coder found?
            if (team.findCoderID(coderID) >= 0) return team;
        }

        // no team!
        return null;
    }

    /**
     * Returns the Team matching the TeamID.  Returns null if not found
     * @param roomID the roomID to find
     */
    public Team getTeam(int TeamID) {
        return (Team) teams.get(new Integer(TeamID));
    }

    /** Class handling the define team messages */
    private class TeamHandler extends TeamAdapter {

        public void defineTeam(TeamEvent evt) {

            // Create the new contest
            Team team = new Team(evt.getTeamID(), evt.getTeamName(), evt.getRoundID(), evt.getCoders());

            // Dispose of the old room if it was defined
            Team oldTeam = getTeam(evt.getTeamID());
            if (oldTeam != null) oldTeam.dispose();

            // Put the new Team
            teams.put(new Integer(evt.getTeamID()), team);

        }
    }
}

