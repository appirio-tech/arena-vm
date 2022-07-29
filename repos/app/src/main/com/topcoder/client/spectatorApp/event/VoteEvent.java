/**
 * VoteEvent
 *
 * Description:		Contains information related to the current vote
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.event;

public class VoteEvent extends java.util.EventObject {

    /** The identifier of the vote */
    private int coderID;

    /** The voting for */
    private int victimID;

    /**
     *  Constructor of a vote event
     *
     *  @param victimID the victim coder ID
     */
    public VoteEvent(Object source, int victimID) {
        this(source, -1, victimID);
    }

    /**
     *  Constructor of a vote event
     *
     *  @param coderID  the coder voting for the victim
     *  @param victimID the victim coder ID
     */
    public VoteEvent(Object source, int coderID, int victimID) {
        super(source);
        this.coderID = coderID;
        this.victimID = victimID;
    }


    /** Gets the coder identifier*/
    public int getCoderID() {
        return coderID;
    }

    /** Gets the victim's coder identifier*/
    public int getVictimID() {
        return victimID;
    }


}


/* @(#)ContestInfoEvent.java */
