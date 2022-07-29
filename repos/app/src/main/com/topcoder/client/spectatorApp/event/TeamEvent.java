/**
 * TeamEvent
 *
 * Description:		Contains information related to the current team
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.event;

public class TeamEvent extends java.util.EventObject {

    /** The identifier of the team */
    private int teamID;

    /** The team name */
    private String teamName;

    /** The identifier of the round the team is associated with */
    private int roundID;

    /** The coders associated with the team */
    private int[] coderID;

    /**
     *  Constructor of a team event
     *
     *  @param teamID     the identifier of the team
     */
    public TeamEvent(Object source, int teamID) {
        this(source, teamID, "", 0, new int[0]);
    }

    /**
     *  Constructor of a team event
     *
     *  @param teamID     the identifier of the team
     *  @param teamName   the team name
     *  @param coderID    the coders assigned
     */
    public TeamEvent(Object source, int teamID, String teamName, int roundID, int[] coderID) {
        super(source);
        this.teamID = teamID;
        this.teamName = teamName;
        this.roundID = roundID;
        this.coderID = coderID;
    }


    /** Gets the team identifier*/
    public int getTeamID() {
        return teamID;
    }

    /** Gets the team name*/
    public String getTeamName() {
        return teamName;
    }

    /** Gets the coders assigned to the team */
    public int[] getCoders() {
        return coderID;
    }

    /**
     * Returns the roundID.
     * @return int
     */
    public int getRoundID() {
        return roundID;
    }

}


/* @(#)ContestInfoEvent.java */
