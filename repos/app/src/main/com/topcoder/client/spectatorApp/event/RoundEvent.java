/**
 * RoundEvent.java
 *
 * Description:		Contains information related to the current round
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.event;


public class RoundEvent extends java.util.EventObject {

    /** The identifier of the round */
    private int roundID;

    /** The round name */
    private String roundName;

    /** The contest that the round is associated with */
    private int contestID;

    /** The type of round */
    private int roundType;

    /**
     *  Constructor of a round event
     *
     *  @param roundID     the identifier of the round
     */
    public RoundEvent(Object source, int roundID) {
        this(source, roundID, 0, null, 0);
    }


    /**
     *  Constructor of a round event
     *
     *  @param roundID     the identifier of the round
     *  @param roundType   the type of round
     *  @param roundName   the round name
     *  @param contestID   the contest the round is associated with
     */
    public RoundEvent(Object source, int roundID, int roundType, String roundName, int contestID) {
        super(source);
        this.roundID = roundID;
        this.roundType = roundType;
        this.roundName = roundName;
        this.contestID = contestID;
    }


    /** Gets the round identifier*/
    public int getRoundID() {
        return roundID;
    }

    /** Gets the round type */
    public int getRoundType() {
        return roundType;
    }

    /** Gets the round name*/
    public String getRoundName() {
        return roundName;
    }

    /** Gets the identifier of the contest*/
    public int getContestID() {
        return contestID;
    }

}


/* @(#)ContestInfoEvent.java */
