package com.topcoder.client.spectatorApp.event;

import com.topcoder.client.spectatorApp.messages.TCSCoderInfo;


/**
 * AnnounceDevelopmentReviewBoardResults
 *
 * Description:		Announcement of the Development review board results
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

public class AnnounceDevelopmentReviewBoardResultsEvent extends AnnounceReviewBoardResultsEvent {
	
	/** Empty constructor as defined by the javabean standard */
	public AnnounceDevelopmentReviewBoardResultsEvent(Object source, int roundID, TCSCoderInfo[] coderNames, String[] reviewerNames, double[][] scores, double[] finalScores) {
		super(source, roundID, coderNames, reviewerNames, scores, finalScores);
	}
}
