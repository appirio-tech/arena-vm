package com.topcoder.client.spectatorApp.messages;



/**
 * AnnounceDesignReviewBoard
 *
 * Description:		Announcement of the design review board
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

public class AnnounceDesignReviewBoard extends AnnounceReviewBoard implements java.io.Serializable {

	/** Empty constructor as defined by the javabean standard */
	public AnnounceDesignReviewBoard(int roundID, String[] handles, int[] tcRatings, int[] tcsRatings, byte[][] images) {
		super(roundID, handles, tcRatings, tcsRatings, images);
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
