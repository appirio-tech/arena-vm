package com.topcoder.client.spectatorApp.announcer.events;

import com.topcoder.client.spectatorApp.messages.AnnounceTCSWinners;

/**
 * Announce the TCS winners
 * 
 * This event will implement the javabean standard since it is read by the XMLDecoder
 * 
 * @author Pops
 */
public class AnnounceTCSWinnersEvent extends AnnouncerEvent {

	/** The contest identifier */
	private int roundID;
	
	/** Design winner image filename */
	private String designImageFileName;
	
	/** Design winner image */
	private byte[] designImage;
	
	/** Design Winner Average */
	private double designWinnerAverage;
	
	/** Design Winner handle */
	private String designHandle;
	
	/** Development winner image filename */
	private String developmentImageFileName;
	
	/** Development winner image */
	private byte[] developmentImage;
	
	/** Development Winner Average */
	private double developmentWinnerAverage;
	
	/** Development Winner Handle */
	private String developmentHandle;
        
        private int designWinnerRating;
        private int developmentWinnerRating;
	
	/** Empty constructor as defined by the javabean standard */
	public AnnounceTCSWinnersEvent() {
	}
	
	/** Returns the ShowInitial message */
	public Object getMessage() {
		return new AnnounceTCSWinners(roundID, designHandle, designImage, designWinnerAverage, developmentHandle, developmentImage, developmentWinnerAverage, designWinnerRating, developmentWinnerRating);
	}
	
	/** Get the design image file name */
	public String getDesignImageFileName() {
		return designImageFileName;
	}

	/** Set the design image file name */
	public void setDesignImageFileName(String designImageFileName) {
		this.designImageFileName = designImageFileName;
	}

	/** Get the design winner average */
	public double getDesignWinnerAverage() {
		return designWinnerAverage;
	}

	/** Set the design winner average */
	public void setDesignWinnerAverage(double designWinnerAverage) {
		this.designWinnerAverage = designWinnerAverage;
	}

	/** Get the development image file name */
	public String getDevelopmentImageFileName() {
		return developmentImageFileName;
	}

	/** Set the development image file name */
	public void setDevelopmentImageFileName(String developmentImageFileName) {
		this.developmentImageFileName = developmentImageFileName;
	}

	/** Get the development winner average */
	public double getDevelopmentWinnerAverage() {
		return developmentWinnerAverage;
	}

	/** Set the development winner average */
	public void setDevelopmentWinnerAverage(double developmentWinnerAverage) {
		this.developmentWinnerAverage = developmentWinnerAverage;
	}

	/** Gets the roundID associated with this */
	public int getRoundID() {
		return roundID;
	}

	/** Sets the roundID associated with this */
	public void setRoundID(int roundID) {
		this.roundID = roundID;
	}

	/** Validate image filenames */
	public void validateEvent() throws Exception {
		developmentImage = getImage(developmentImageFileName);
		designImage = getImage(designImageFileName);
	}

	/** Get the design winner handle */
	public String getDesignHandle() {
		return designHandle;
	}

	/** Set the design winner handle */
	public void setDesignHandle(String designHandle) {
		this.designHandle = designHandle;
	}

	/** Get the development winner handle */
	public String getDevelopmentHandle() {
		return developmentHandle;
	}

	/** Set the development winner handle */
	public void setDevelopmentHandle(String developmentHandle) {
		this.developmentHandle = developmentHandle;
	}

        public int getDesignWinnerRating() {
            return designWinnerRating;
        }
        public void setDesignWinnerRating(int designWinnerRating) {
            this.designWinnerRating = designWinnerRating;
        }
        public int getDevelopmentWinnerRating() {
            return developmentWinnerRating;
        }
        public void setDevelopmentWinnerRating(int developmentWinnerRating) {
            this.developmentWinnerRating = developmentWinnerRating;
        }
}
