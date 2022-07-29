/**
 * AnnounceCoderEvent.java
 *
 * Description:		Contains information related to a coder;
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.event;

import java.awt.Image;

public class AnnounceCoderEvent extends java.util.EventObject {

    /** ID of the round */
    public int roundID;

    /** Coder name */
    public String coderName;

    /** Coder handle */
    public String coderHandle;

    /** Coder image */
    public Image coderImage;

    /** Coder college */
    public String coderCollege;

    /** Coder rating */
    public int coderRating;

    /** Coder ranking */
    public int coderRanking;

    /** Coder seed */
    public int coderSeed;

    /** Coder competitions */
    public int coderCompetitions;

    /** Coder submissions */
    public int coderSubmissions;

    /** Coder submission percentage */
    public double coderSubmissionPrct;

    /** Coder challenges */
    public int coderChallenges;

    /** Coder challenge percentage */
    public double coderChallengePrct;

    /** Collegiate competitions */
    public int invitationalCompetitions;

    /** Collegiate submissions */
    public int invitationalSubmissions;

    /** Collegiate submission percentage */
    public double invitationalSubmissionPrct;

    /** Collegiate challenges */
    public int invitationalChallenges;

    /** Collegiate challenge percentage */
    public double invitationalChallengePrct;

    /** Constructor */
    public AnnounceCoderEvent(Object source, int roundID, String coderName, String coderHandle, Image coderImage, String coderCollege, int coderRating, int coderRanking, int coderSeed, int coderCompetitions, int coderSubmissions, double coderSubmissionPrct, int coderChallenges, double coderChallengePrct, int invitationalCompetitions, int invitationalSubmissions, double invitationalSubmissionPrct, int invitationalChallenges, double invitationalChallengePrct) {
        super(source);
        this.roundID = roundID;
        this.coderName = coderName;
        this.coderHandle = coderHandle;
        this.coderImage = coderImage;
        this.coderCollege = coderCollege;
        this.coderRating = coderRating;
        this.coderRanking = coderRanking;
        this.coderSeed = coderSeed;
        this.coderCompetitions = coderCompetitions;
        this.coderSubmissions = coderSubmissions;
        this.coderSubmissionPrct = coderSubmissionPrct;
        this.coderChallenges = coderChallenges;
        this.coderChallengePrct = coderChallengePrct;
        this.invitationalCompetitions = invitationalCompetitions;
        this.invitationalSubmissions = invitationalSubmissions;
        this.invitationalSubmissionPrct = invitationalSubmissionPrct;
        this.invitationalChallenges = invitationalChallenges;
        this.invitationalChallengePrct = invitationalChallengePrct;
    }

    /** Gets the round ID*/
    public int getRoundID() {
        return roundID;
    }

    /** Gets the coderName */
    public String getCoderName() {
        return coderName;
    }

    /** Gets the coderHandle */
    public String getCoderHandle() {
        return coderHandle;
    }

    /** Gets the coderImage */
    public Image getCoderImage() {
        return coderImage;
    }

    /** Gets the coderType */
    public String getCoderType() {
        return coderCollege;
    }

    /** Gets the coderRating */
    public int getCoderRating() {
        return coderRating;
    }

    /** Gets the coderRanking */
    public int getCoderRanking() {
        return coderRanking;
    }

    /** Gets the coderSeed */
    public int getCoderSeed() {
        return coderSeed;
    }

    /** Gets the coderCompetitions */
    public int getCoderCompetitions() {
        return coderCompetitions;
    }

    /** Gets the coderSubmissions */
    public int getCoderSubmissions() {
        return coderSubmissions;
    }

    /** Gets the coderSubmissionPrct */
    public double getCoderSubmissionPrct() {
        return coderSubmissionPrct;
    }

    /** Gets the coderChallenges */
    public int getCoderChallenges() {
        return coderChallenges;
    }

    /** Gets the coderChallengePrct */
    public double getCoderChallengePrct() {
        return coderChallengePrct;
    }

    /** Gets the invitationalCompetitions */
    public int getInvitationalCompetitions() {
        return invitationalCompetitions;
    }

    /** Gets the invitationalSubmissions */
    public int getInvitationalSubmissions() {
        return invitationalSubmissions;
    }

    /** Gets the invitationalSubmissionPrct */
    public double getInvitationalSubmissionPrct() {
        return invitationalSubmissionPrct;
    }

    /** Gets the invitationalChallenges */
    public int getInvitationalChallenges() {
        return invitationalChallenges;
    }

    /** Gets the invitationalChallengePrct */
    public double getInvitationalChallengePrct() {
        return invitationalChallengePrct;
    }
}


/* @(#)AnnounceCoderEvent.java */
