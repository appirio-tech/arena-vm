package com.topcoder.client.spectatorApp.messages;


/**
 * AnnounceDevelopmentReviewBoardResults
 *
 * Description:		Announcement of the Development review board results
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

public class AnnounceDevelopmentReviewBoardResults extends AnnounceReviewBoardResults implements java.io.Serializable {

	/** Empty constructor as defined by the javabean standard */
	public AnnounceDevelopmentReviewBoardResults(int roundID, TCSCoderInfo[] coderNames, String[] reviewerNames, double[][] scores, double[] finalScores) {
		super(roundID, coderNames, reviewerNames, scores, finalScores);
	}
	
	/** To string method */
	public String toString() {
		StringBuffer buf = new StringBuffer(500);
		buf.append("(AnnounceDevelopmentReviewBoard)[");
		buf.append(super.toString());
		buf.append("]");
		
		return buf.toString();
	}
}
