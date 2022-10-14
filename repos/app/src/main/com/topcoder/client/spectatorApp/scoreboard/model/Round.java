package com.topcoder.client.spectatorApp.scoreboard.model;

/**
 * Round.java
 *
 * Description:		The round model
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

import com.topcoder.client.spectatorApp.PhaseTracker;

public class Round {

    /** Identifier of the room */
    private int roundID;

    /** Name of the round */
    private String roundName;

    /** The contest the round is associated to */
    private int contestID;

    /** Type of the round */
    private int roundType;
    
    private int[] tcsPlacements;

    /**
     *  Constructor of a Round
     *
     *  @param roundID the unique identifier of a round
     *  @param roundName the name of the round
     *  @param contestID the contest the round is associated with
     */
    public Round(int roundID, String roundName, int contestID, int roundType) throws InstantiationException {
        this.roundID = roundID;
        this.roundName = roundName;
        this.contestID = contestID;
        this.roundType = roundType;

        // Get the associated contest
        Contest contest = ContestManager.getInstance().getContest(contestID);
        if (contest == null) throw new InstantiationException("ContestID: " + contestID + " was not found");

    }


    /**
     * Disposes of any resources used
     */
    public void dispose() {
    }

    public int getRoundType() {
        return roundType;
    }

    /**
     * Returns the current phase
     */
    public int getPhase() {
        return PhaseTracker.getInstance().getPhaseID();
    }

    /**
     * Returns the contestID.
     * @return int
     */
    public int getContestID() {
        return contestID;
    }

    /**
     * Returns the contest associated with the round (can be null if not found)
     * @return String
     */
    public Contest getContest() {
        return ContestManager.getInstance().getContest(contestID);
    }

    /**
     * Returns the roundID.
     * @return int
     */
    public int getRoundID() {
        return roundID;
    }

    /**
     * Returns the roundName.
     * @return String
     */
    public String getRoundName() {
        return roundName;
    }
    
    public int[] getTCSPlacements() {
        return tcsPlacements;
    }
    
    public void setTCSPlacements(int[] placements) {
        tcsPlacements = placements;
    }

}
