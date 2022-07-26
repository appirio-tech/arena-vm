package com.topcoder.client.spectatorApp.event;

import com.topcoder.client.spectatorApp.messages.TCSCoderInfo;


/**
 * AnnounceReviewBoardResults
 *
 * Description:		Announcement of the review board results (subclasses determine which)
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

public abstract class AnnounceReviewBoardResultsEvent extends java.util.EventObject {

	/** The contest ID */
	private int roundID;
	
	/** The coder names */
	private TCSCoderInfo[] coders;
	
	/** The reviewer names */
	private String[] reviewerNames;
	
	/** The scores */
	private double[][] scores;
	
	/** The final scores */
	private double[] finalScores;
	
	/** Empty constructor as defined by the javabean standard */
	public AnnounceReviewBoardResultsEvent(Object source, int roundID, TCSCoderInfo[] coders, String[] reviewerNames, double[][] scores, double[] finalScores) {
		super(source);
		this.roundID = roundID;
		this.coders = coders;
		this.reviewerNames = reviewerNames;
		this.scores = scores;
		this.finalScores = finalScores;
	}
	
	/** Get the contest name */
	public int getRoundID() {
		return roundID;
	}
	
	/** Get the coder names */
	public TCSCoderInfo[] getCoders() {
		return coders;
	}

	/** Get the reviewer names */
	public String[] getReviewerNames() {
		return reviewerNames;
	}

	/** Get the scores */
	public double[][] getScores() {
		return scores;
	}

	/** Get the final scores */
	public double[] getFinalScores() {
		return finalScores;
	}

}
