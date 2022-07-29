package com.topcoder.client.spectatorApp.messages;


/**
 * AnnounceReviewBoard
 *
 * Description:		Announcement of the review board (subclasses determine which)
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

public abstract class AnnounceReviewBoard implements java.io.Serializable {

	/** The contest id */
	private int roundID;
	
	/** The reviewer handles */
	private String[] handles;
	
	/** The reviewer tcRatings */
	private int[] tcRatings;
	
	/** The reviewer tcsRatings */
	private int[] tcsRatings;
	
	/** The reviewer byte[]s */
	private byte[][] images;
	
	/** Empty constructor as defined by the javabean standard */
	public AnnounceReviewBoard(int roundID, String[] handles, int[] tcRatings, int[] tcsRatings, byte[][] images) {
		this.roundID = roundID;
		this.handles = handles;
		this.tcRatings = tcRatings;
		this.tcsRatings = tcsRatings;
		this.images = images;
	}
	
	/** Get the contest name */
	public int getRoundID() {
		return roundID;
	}
	
	/** Get the handles */
	public String[] getHandles() {
		return handles;
	}

	/** Get the imagess */
	public byte[][] getImages() {
		return images;
	}

	/** Get the TCRatings */
	public int[] getTcRatings() {
		return tcRatings;
	}

	/** Get the TCS Ratings */
	public int[] getTcsRatings() {
		return tcsRatings;
	}

	/** To string method */
	public String toString() {
		StringBuffer buf = new StringBuffer(500);
		buf.append(roundID);
		buf.append(", {");
		for(int x=0;x<handles.length;x++) {
			buf.append("{");
			buf.append(handles[x]);
			buf.append(", ");
			buf.append(tcRatings[x]);
			buf.append(", ");
			buf.append(tcsRatings[x]);
			buf.append("}");
			if(x!=handles.length-1) buf.append(", ");
		}
		buf.append("}");
		
		return buf.toString();
	}
}
