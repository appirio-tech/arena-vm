package com.topcoder.client.spectatorApp.messages;


/**
 * AnnounceDesignReviewBoardResults
 *
 * Description:		Announcement of the design review board results
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

public class AnnounceDesignReviewBoardResults extends AnnounceReviewBoardResults implements java.io.Serializable {

	/** Empty constructor as defined by the javabean standard */
	public AnnounceDesignReviewBoardResults(int roundID, TCSCoderInfo[] coderNames, String[] reviewerNames, double[][] scores, double[] finalScores) {
		super(roundID, coderNames, reviewerNames, scores, finalScores);
	}

	/** To string method */
	public String toString() {
		StringBuffer buf = new StringBuffer(500);
		buf.append("(AnnounceDesignReviewBoard)[");
		buf.append(super.toString());
		buf.append("]");
		
		return buf.toString();
	}

}
