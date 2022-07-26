package com.topcoder.client.spectatorApp.event;

import java.awt.Image;


/**
 * AnnounceDesignReviewBoard
 *
 * Description:		Announcement of the Design review board
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

public class AnnounceDesignReviewBoardEvent extends AnnounceReviewBoardEvent {

	/** Empty constructor as defined by the javabean standard */
	public AnnounceDesignReviewBoardEvent(Object source, int roundID, String[] handles, int[] tcRatings, int[] tcsRatings, Image[] images) {
		super(source, roundID, handles, tcRatings, tcsRatings, images);
	}
}
