package com.topcoder.client.spectatorApp.announcer.events;

import com.topcoder.client.spectatorApp.messages.AnnounceDesignReviewBoardResults;


/**
 * The announce the design review board's results.  
 * 
 * This event will implement the javabean standard since it is read by the XMLDecoder
 * 
 * @author Pops
 */
public class AnnounceDesignReviewBoardResultsEvent extends AnnounceReviewBoardResultsEvent {

	/** Empty Constructor - required by Javabean standard */
	public AnnounceDesignReviewBoardResultsEvent() {
	}
	
	/** Returns the AnnounceDesignReviewBoardResults message */
	public Object getMessage() {
		return new AnnounceDesignReviewBoardResults(getRoundID(), getCoders(), getReviewerNames(), getScores(), getFinalScores());
	}
	
}
