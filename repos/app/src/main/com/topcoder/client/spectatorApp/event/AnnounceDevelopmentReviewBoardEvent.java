package com.topcoder.client.spectatorApp.event;

import java.awt.Image;


/**
 * AnnounceDevelopmentReviewBoard
 *
 * Description:		Announcement of the development review board
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

public class AnnounceDevelopmentReviewBoardEvent extends AnnounceReviewBoardEvent {

	/** Empty constructor as defined by the javabean standard */
	public AnnounceDevelopmentReviewBoardEvent(Object source, int roundID, String[] handles, int[] tcRatings, int[] tcsRatings, Image[] images) {
		super(source, roundID, handles, tcRatings, tcsRatings, images);
	}

}
