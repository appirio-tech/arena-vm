package com.topcoder.client.spectatorApp.announcer.events;

import com.topcoder.client.spectatorApp.messages.AnnounceDevelopmentReviewBoard;


/**
 * The announce the development review board.  
 * 
 * This event will implement the javabean standard since it is read by the XMLDecoder
 * 
 * @author Pops
 */
public class AnnounceDevelopmentReviewBoardEvent extends AnnounceReviewBoardEvent {

	/** Empty Constructor - required by Javabean standard */
	public AnnounceDevelopmentReviewBoardEvent() {
	}
	
	/** Returns the AnnounceDevelopmentReviewBoard message */
	public Object getMessage() {
		// Return the event
		return new AnnounceDevelopmentReviewBoard(getRoundID(), getReviewerHandles(), getReviewerTCRating(), getReviewerTCSRating(), getImages());	
	}
	
}
