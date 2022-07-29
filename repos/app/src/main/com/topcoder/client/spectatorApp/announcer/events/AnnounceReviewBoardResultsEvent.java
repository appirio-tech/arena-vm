package com.topcoder.client.spectatorApp.announcer.events;

import com.topcoder.client.spectatorApp.messages.ShowInitial;
import com.topcoder.client.spectatorApp.messages.TCSCoderInfo;

/**
 * Event to announcer review board results (subclasses decide which board)
 * 
 * This event will implement the javabean standard since it is read by the XMLDecoder
 * 
 * @author Pops
 */
public class AnnounceReviewBoardResultsEvent extends AnnouncerEvent {

	/** The contest identifier */
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
	public AnnounceReviewBoardResultsEvent() {
	}
	
	/** Returns the ShowInitial message */
	public Object getMessage() {
		return new ShowInitial();
	}
	
	/** Gets the coder names */
	public TCSCoderInfo[] getCoders() {
		return coders;
	}

	/** Sets the coder names */
	public void setCoders(TCSCoderInfo[] coderNames) {
		this.coders = coderNames;
	}

	/** Gets the reviewer names */
	public String[] getReviewerNames() {
		return reviewerNames;
	}

	/** Sets the reviewer names */
	public void setReviewerNames(String[] reviewerNames) {
		this.reviewerNames = reviewerNames;
	}

	/** Gets the scores */
	public double[][] getScores() {
		return scores;
	}

	/** Sets the scores */
	public void setScores(double[][] scores) {
		this.scores = scores;
	}

	/** Gets the final scores */
	public double[] getFinalScores() {
		return finalScores;
	}

	/** Sets the final scores */
	public void setFinalScores(double[] finalScores) {
		this.finalScores = finalScores;
	}

	/** Validate lengths of fields */
	public void validateEvent() throws Exception {
		if(reviewerNames.length!=3) throw new Exception("You do not have exactly 3 reviewer names");
		
		for(int x=0;x<scores.length;x++) {
			if(scores[x].length!=3) throw new Exception("You do not have exactly 3 scores for row " + x);			
		}
	}		

	/** Gets the roundID associated with this */
	public int getRoundID() {
		return roundID;
	}

	/** Sets the roundID associated with this */
	public void setRoundID(int roundID) {
		this.roundID = roundID;
	}
}
