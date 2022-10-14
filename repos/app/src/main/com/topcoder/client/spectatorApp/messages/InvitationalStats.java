/**
 * InvitationalStats.java
 *
 * Description:		Holds statistics about a specific coder's collegiate stats
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.messages;


public class InvitationalStats implements java.io.Serializable {

    /** Number of competitions */
    public int numCompetitions;

    /** Number of submissions */
    public int numSubmissions;

    /** Submission percentage */
    public double submissionPrct;

    /** Number of challenges */
    public int numChallenges;

    /** Challenge percentage */
    public double challengePrct;

    /** Constructor */
    public InvitationalStats(int numCompetitions, int numSubmissions, double submissionPrct, int numChallenges, double challengePrct) {
        this.numCompetitions = numCompetitions;
        this.numSubmissions = numSubmissions;
        this.submissionPrct = submissionPrct;
        this.numChallenges = numChallenges;
        this.challengePrct = challengePrct;
    }

    /** Gets the numSubmissions */
    public int getNumSubmissions() {
        return numSubmissions;
    }

    /** Gets the submissionPrct */
    public double getSubmissionPrct() {
        return submissionPrct;
    }

    /** Gets the numChallenges */
    public int getNumChallenges() {
        return numChallenges;
    }

    /** Gets the challengePrct */
    public double getChallengePrct() {
        return challengePrct;
    }

    /** Gets the avgPoints */
    public int getNumCompetitions() {
        return numCompetitions;
    }

    public String toString() {
        return new StringBuffer().append("(InvitationalStats)[").append(numCompetitions).append(", ").append(numSubmissions).append(", ").append(submissionPrct).append(", ").append(numChallenges).append(", ").append(challengePrct).append("]").toString();
    }
}


/* @(#)CollegiateStats.java */
