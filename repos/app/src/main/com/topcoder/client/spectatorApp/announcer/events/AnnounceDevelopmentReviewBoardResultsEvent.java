package com.topcoder.client.spectatorApp.announcer.events;

import com.topcoder.client.spectatorApp.messages.AnnounceDevelopmentReviewBoardResults;


/**
 * The announce the development review board's results.  
 * 
 * This event will implement the javabean standard since it is read by the XMLDecoder
 * 
 * @author Pops
 */
public class AnnounceDevelopmentReviewBoardResultsEvent extends AnnounceReviewBoardResultsEvent {

	/** Empty Constructor - required by Javabean standard */
	public AnnounceDevelopmentReviewBoardResultsEvent() {
	}
	
	/** Returns the AnnounceDevelopmentReviewBoardResults message */
	public Object getMessage() {
		return new AnnounceDevelopmentReviewBoardResults(getRoundID(), getCoders(), getReviewerNames(), getScores(), getFinalScores());
	}
	
}
