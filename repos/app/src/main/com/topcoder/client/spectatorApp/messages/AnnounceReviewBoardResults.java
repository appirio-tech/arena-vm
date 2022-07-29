package com.topcoder.client.spectatorApp.messages;


/**
 * AnnounceReviewBoardResults
 *
 * Description:		Announcement of the review board results (subclasses determine which)
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

public abstract class AnnounceReviewBoardResults implements java.io.Serializable {

	/** The contest id */
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
	public AnnounceReviewBoardResults(int roundID, TCSCoderInfo[] coders, String[] reviewerNames, double[][] scores, double[] finalScores) {
		this.roundID = roundID;
		this.coders = coders;
		this.reviewerNames = reviewerNames;
		this.scores = scores;
		this.finalScores = finalScores;
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


	/** Gets the roundID associated with this */
	public int getRoundID() {
		return roundID;
	}

	/** To string method */
	public String toString() {
		StringBuffer buf = new StringBuffer(500);
		buf.append(roundID);
		buf.append(", {");
		for(int x=0;x<coders.length;x++) {
			buf.append(coders[x].toString());
			if(x!=coders.length-1) buf.append(", ");
		}
		buf.append("},{");
		for(int x=0;x<reviewerNames.length;x++) {
			buf.append(reviewerNames[x]);
			if(x!=reviewerNames.length-1) buf.append(", ");
		}
		buf.append("},{");
		for(int x=0;x<scores.length;x++){
			buf.append("{");
			for(int y=0;y<scores[x].length;y++) {
				buf.append(scores[x][y]);
				if(y!=scores[x].length-1) buf.append(", ");
			}
			buf.append("}");
			if(x!=scores.length-1) buf.append(", ");
		}
		buf.append("},{");
		for(int x=0;x<finalScores.length;x++){
			buf.append(finalScores[x]);
			if(x!=finalScores.length-1) buf.append(", ");
		}
		buf.append("}");
		
		return buf.toString();
	}

}
