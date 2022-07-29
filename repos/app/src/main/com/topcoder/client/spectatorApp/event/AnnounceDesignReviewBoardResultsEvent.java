package com.topcoder.client.spectatorApp.event;

import com.topcoder.client.spectatorApp.messages.TCSCoderInfo;


/**
 * AnnounceDesignReviewBoardResults
 *
 * Description:		Announcement of the design review board results
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

public class AnnounceDesignReviewBoardResultsEvent extends AnnounceReviewBoardResultsEvent {

	/** Empty constructor as defined by the javabean standard */
	public AnnounceDesignReviewBoardResultsEvent(Object source, int roundID, TCSCoderInfo[] coderNames, String[] reviewerNames, double[][] scores, double[] finalScores) {
		super(source, roundID, coderNames, reviewerNames, scores, finalScores);
	}

}
