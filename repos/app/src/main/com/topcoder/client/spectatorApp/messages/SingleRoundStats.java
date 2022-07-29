/**
 * SingleRoundStats.java
 *
 * Description:		Holds statistics about a coder's single round stats
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.messages;


public class SingleRoundStats implements java.io.Serializable {

    /** Number of competitions */
    public int numCompetitions;

    /** Volatitily */
    public int volatility;

    /** Submission percentage */
    public double submissionPrct;

    /** Challenge percentage */
    public double challengePrct;

    /** Average points */
    public double avgPoints;

    /** Room wins */
    public int roomWins;

    /** Constructor */
    public SingleRoundStats(int numCompetitions, int volatility, double submissionPrct, double challengePrct, double avgPoints, int roomWins) {
        this.numCompetitions = numCompetitions;
        this.volatility = volatility;
        this.submissionPrct = submissionPrct;
        this.challengePrct = challengePrct;
        this.avgPoints = avgPoints;
        this.roomWins = roomWins;
    }

    /** Gets the numCompetitions */
    public int getNumCompetitions() {
        return numCompetitions;
    }

    /** Gets the volatility */
    public int getVolatility() {
        return volatility;
    }

    /** Gets the submissionPrct */
    public double getSubmissionPrct() {
        return submissionPrct;
    }

    /** Gets the challengePrct */
    public double getChallengePrct() {
        return challengePrct;
    }

    /** Gets the avgPoints */
    public double getAvgPoints() {
        return avgPoints;
    }

    /** Gets the roomWins */
    public int getRoomWins() {
        return roomWins;
    }

    public String toString() {
        return new StringBuffer().append("(SingleRoundStats)[").append(numCompetitions).append(", ").append(volatility).append(", ").append(submissionPrct).append(", ").append(challengePrct).append(", ").append(avgPoints).append(", ").append(roomWins).append("]").toString();
    }
}


/* @(#)SingleRoundStats.java */
