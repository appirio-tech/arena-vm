package com.topcoder.client.spectatorApp.messages;



/**
 * AnnounceDevelopmentReviewBoard
 *
 * Description:		Announcement of the Development review board
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

public class AnnounceDevelopmentReviewBoard extends AnnounceReviewBoard implements java.io.Serializable {

	/** Empty constructor as defined by the javabean standard */
	public AnnounceDevelopmentReviewBoard(int roundID, String[] handles, int[] tcRatings, int[] tcsRatings, byte[][] images) {
		super(roundID, handles, tcRatings, tcsRatings, images);
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
