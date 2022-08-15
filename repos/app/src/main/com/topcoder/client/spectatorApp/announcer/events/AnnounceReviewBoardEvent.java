package com.topcoder.client.spectatorApp.announcer.events;


/**
 * The announce a review board.  A child class will determine which review board.  
 * 
 * This event will implement the javabean standard since it is read by the XMLDecoder
 * 
 * @author Pops
 */
public abstract class AnnounceReviewBoardEvent extends AnnouncerEvent {

	/** The contest identifier */
	private int roundID;
	
	/** Handles of the Reviewers */
	private String[] reviewerHandles;

	/** TCRatings of the Reviewers */
	private int[] reviewerTCRating;

	/** TCS Ratings of the Reviewers */
	private int[] reviewerTCSRating;

	/** Image file names of the Reviewers */
	private String[] reviewerImageFileName;

	/** Image bytes of the reviewers */
	private byte[][] image;

	/** Gets the handles of each reviewer */
	public String[] getReviewerHandles() {
		return reviewerHandles;
	}

	/** Sets the handles of each reviewer */
	public void setReviewerHandles(String[] reviewerHandles) {
		this.reviewerHandles = reviewerHandles;
	}

	/** Gets each Reviewers image file name */
	public String[] getReviewerImageFileName() {
		return reviewerImageFileName;
	}

	/** Sets each Reviewers image file name */
	public void setReviewerImageFileName(String[] reviewersImageFileName) {
		this.reviewerImageFileName = reviewersImageFileName;	
	}

	/** Gets each Reviewers TC Rating */
	public int[] getReviewerTCRating() {
		return reviewerTCRating;
	}

	/** Sets each Reviewers TC Rating */
	public void setReviewerTCRating(int[] reviewerTCRating) {
		this.reviewerTCRating = reviewerTCRating;
	}

	/** Gets each Reviewers TCS Rating */
	public int[] getReviewerTCSRating() {
		return reviewerTCSRating;
	}

	/** Sets each Reviewers TCS Rating */
	public void setReviewerTCSRating(int[] reviewerTCSRating) {
		this.reviewerTCSRating = reviewerTCSRating;
	}

	/** Gets the actual images*/
	public byte[][] getImages() {
		return image;
	}

	/** Validate image filenames and array lengths */
	public void validateEvent() throws Exception {
		if(reviewerHandles.length!=3) throw new Exception("You do not have exactly 3 reviewer handles");
		if(reviewerImageFileName.length!=3) throw new Exception("You do not have exactly 3 reviewer image filenames");
		if(reviewerTCRating.length!=3) throw new Exception("You do not have exactly 3 reviewer TC Ratings");
		if(reviewerTCSRating.length!=3) throw new Exception("You do not have exactly 3 reviewer TCS Ratings");
		
		image = new byte[3][];
		for(int x=0;x<reviewerImageFileName.length;x++) {
			image[x] = getImage(reviewerImageFileName[x]);
		}
	}

	/** Gets the roundID associated with this */
	public int getRoundID() {
		return roundID;
	}

	/** Sets the roundID associated with this */
	public void setRoundID(int roundID) {
		this.roundID = roundID;
	}

}
