package com.topcoder.client.spectatorApp.event;

import java.awt.Image;


/**
 * AnnounceReviewBoard
 *
 * Description:		Announcement of the review board (subclasses determine which)
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

public abstract class AnnounceReviewBoardEvent extends java.util.EventObject {

	/** The contest ID */
	private int roundID;
	
	/** The reviewer handles */
	private String[] handles;
	
	/** The reviewer tcRatings */
	private int[] tcRatings;
	
	/** The reviewer tcsRatings */
	private int[] tcsRatings;
	
	/** The reviewer images */
	private Image[] images;
	
	/** Empty constructor as defined by the javabean standard */
	public AnnounceReviewBoardEvent(Object source, int roundID, String[] handles, int[] tcRatings, int[] tcsRatings, Image[] images) {
		super(source);
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

	/** Get the images */
	public Image[] getImages() {
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

}
