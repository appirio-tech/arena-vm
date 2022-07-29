package com.topcoder.client.spectatorApp.announcer.events;

import com.topcoder.client.spectatorApp.messages.AnnounceDesignReviewBoard;


/**
 * The announce the design review board.  
 * 
 * This event will implement the javabean standard since it is read by the XMLDecoder
 * 
 * @author Pops
 */
public class AnnounceDesignReviewBoardEvent extends AnnounceReviewBoardEvent {

	/** Empty Constructor - required by Javabean standard */
	public AnnounceDesignReviewBoardEvent() {
	}
	
	/** Returns the AnnounceDesignReviewBoard message */
	public Object getMessage() {
		// Return the event
		return new AnnounceDesignReviewBoard(getRoundID(), getReviewerHandles(), getReviewerTCRating(), getReviewerTCSRating(), getImages());
	}
	
}
