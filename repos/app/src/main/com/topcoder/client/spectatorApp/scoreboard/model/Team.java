package com.topcoder.client.spectatorApp.scoreboard.model;

/**
 * Round.java
 *
 * Description:		The round model
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

import com.topcoder.client.spectatorApp.event.VoteEvent;
import com.topcoder.client.spectatorApp.event.VoteListener;
import com.topcoder.client.spectatorApp.netClient.SpectatorEventProcessor;
import com.topcoder.shared.netCommon.messages.spectator.CoderRoomData;

public class Team {

    /** Identifier of the room */
    private int teamID;

    /** Name of the round */
    private String teamName;

    /** Identifier of the round that the team is associated with */
    private int roundID;

    /** The overall points for the team */
    private int teamPoints = 0;

    /**
     * The individual coders
     */
    private int[] coderID;

    /**
     * The coder data
     */
    private CoderRoomData[] coderData;

    /**
     * The points for each coder
     */
    private int[] coderPoints;

    /**
     * Whether the coder has been voted out or not
     */
    private boolean[] coderVotedOut;

    /**
     * Who (coderID) the coder voted for
     */
    private int[] coderVotedFor;

    /** Handler for Voting */
    private VoteHandler voteHandler = new VoteHandler();

    /**
     *  Constructor of a Team
     *
     *  @param teamID the team identifier
     *  @param teamName the team name
     *  @param roundID the round the team is associated with
     *  @param coderIDs the coders assigned to the team
     */
    public Team(int teamID, String teamName, int roundID, int[] coderID) {
        this.teamID = teamID;
        this.teamName = teamName;
        this.roundID = roundID;
        this.coderID = coderID;
        this.coderData = new CoderRoomData[coderID.length];
        this.coderPoints = new int[coderID.length];
        this.coderVotedOut = new boolean[coderID.length];
        this.coderVotedFor = new int[coderID.length];

        // Register the handler as a listener
        SpectatorEventProcessor.getInstance().addVoteListener(voteHandler);

    }


    /**
     * Disposes of any resources used
     */
    public void dispose() {
        // Removes the handler as a listener
        SpectatorEventProcessor.getInstance().removeVoteListener(voteHandler);
    }

    /**
     * Returns the roundID the team is associated with
     * @return the roundID
     */
    public int getRoundID() {
        return roundID;
    }


    /**
     * Returns the round the team is associated with
     * @return the round
     */
    public Round getRound() {
        return RoundManager.getInstance().getRound(roundID);
    }

    /**
     * Hack method since we don't have a coder master
     * Set's the name for the coder
     * @param data the coder data
     */
    public void updateCoderData(CoderRoomData coderData) {
        int x = findCoderID(coderData.getCoderID());
        if (x >= 0) this.coderData[x] = coderData;
    }

    /**
     * Returns whether a person has been voted out or not
     * @param coderID the coderID to check
     * @return whether they have been voted out or not
     */
    public boolean isVotedOut(int coderID) {
        int x = findCoderID(coderID);
        return x < 0 ? false : coderVotedOut[x];
    }

    /**
     * Updates the coder points
     * @param coderID the identifier of the coder
     * @param
     */
    public void updateCoderTotal(int coderID, int points) {
        int x = findCoderID(coderID);
        if (x >= 0) {
            // Update the team points
            this.teamPoints = teamPoints - coderPoints[x] + points;

            // Update the coder points
            coderPoints[x] = points;
        }
    }

    /**
     * Returns the votes cast against the coderid
     * @param coderID the id to get votes against
     * @return the number of votes
     */
    public int getVotesAgainst(int coderID) {
        int ct = 0;
        for (int x = coderVotedFor.length - 1; x >= 0; x--) {
            if (coderVotedFor[x] == coderID) ct++;
        }

        return ct;
    }

    /**
     * Finds the coderID within the team
     * @return the coderID position or -1 if not part of team
     */
    public int findCoderID(int coderID) {
        // Find the coder
        for (int x = this.coderID.length - 1; x >= 0; x--) {

            // Is it our coder
            if (this.coderID[x] == coderID) {
                return x;
            }
        }

        // Didn't find it
        return -1;
    }


    /**
     * Returns the coderData for a coderID.
     * @return The coder data or null if not found
     */
    public CoderRoomData getCoderData(int coderID) {
        int r = findCoderID(coderID);
        if (r < 0) return null;
        return coderData[r];
    }

    /**
     * Returns the teamID.
     * @return int
     */
    public int getTeamID() {
        return teamID;
    }

    /**
     * Returns the teamName.
     * @return String
     */
    public String getTeamName() {
        return teamName;
    }

    /**
     * Returns the teamPoints.
     * @return int
     */
    public int getTeamPoints() {
        return teamPoints;
    }

    /**
     * Returns the number of coders allocated to the team
     * @return the count
     */
    public int getCoderCount() {
        return coderID.length;
    }

    /**
     * Returns the coders assigned to this team.
     * @return CoderRoomData
     */
    public CoderRoomData[] getCoderData() {
        return coderData;
    }


    /** Class handling the voting messages */
    private class VoteHandler implements VoteListener {

        public void votedFor(VoteEvent evt) {

            // Find the coder ID
            int idx = findCoderID(evt.getCoderID());
            // Not in this team? - return
            if (idx < 0) {
                return;
            }

            // Save who they voted for
            coderVotedFor[idx] = evt.getVictimID();
        }

        public void votedOut(VoteEvent evt) {

            // Find the coder ID
            int idx = findCoderID(evt.getVictimID());

            // Not in this team? - return
            if (idx < 0) return;

            // Knock them out!
            coderVotedOut[idx] = true;
        }
    }
}
