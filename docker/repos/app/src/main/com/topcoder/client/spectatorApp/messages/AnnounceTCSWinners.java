package com.topcoder.client.spectatorApp.messages;


/**
 * AnnounceTCSWinners
 *
 * Description:		Announcement of a the winners of a TCS contest
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

public class AnnounceTCSWinners implements java.io.Serializable {

	/** The contest id */
	private int roundID;
	
	/** Design winner image */
	private byte[] designImage;
	
	/** Design Winner Average */
	private double designWinnerAverage;
	
	/** Design Winner Handle */
	private String designHandle;
	
	/** Development winner image */
	private byte[] developmentImage;
	
	/** Development Winner Average */
	private double developmentWinnerAverage;
		
	/** Development Winner Handle */
	private String developmentHandle;
        
        private int developmentWinnerRating;
        private int designWinnerRating;

	/** Empty constructor as defined by the javabean standard */
	public AnnounceTCSWinners(int roundID, String designHandle, byte[] designImage, double designWinnerAverage, String developmentHandle, byte[] developmentImage, double developmentWinnerAverage, int designRating, int developmentRating) {
		this.roundID = roundID;
		this.designHandle = designHandle;
		this.designImage = designImage;
		this.designWinnerAverage = designWinnerAverage;
		this.developmentHandle = developmentHandle;
		this.developmentImage = developmentImage;
		this.developmentWinnerAverage = developmentWinnerAverage;
                this.designWinnerRating = designRating;
                this.developmentWinnerRating = developmentRating;
	}
	
	/** Get the design winner handle */
	public String getDesignHandle() {
		return designHandle;
	}

	/** Get the design image */
	public byte[] getDesignImage() {
		return designImage;
	}

	/** Get the design winner average */
	public double getDesignWinnerAverage() {
		return designWinnerAverage;
	}
	
	/** Get the development winner handle */
	public String getDevelopmentHandle() {
		return developmentHandle;
	}
	
	/** Get the development image file name */
	public byte[] getDevelopmentImage() {
		return developmentImage;
	}

	/** Get the development winner average */
	public double getDevelopmentWinnerAverage() {
		return developmentWinnerAverage;
	}

	/** Gets the contestID associated with this */
	public int getRoundID() {
		return roundID;
	}
        
        public int getDesignRating()
        {
            return this.designWinnerRating;
        }
        
        public int getDevelopmentRating()
        {
            return this.developmentWinnerRating;
        }

	public String toString() {
		return new StringBuffer().append("(AnnounceTCSWinners)[").append(roundID).append(", ").append(designWinnerAverage).append(", ").append(developmentWinnerAverage).append(designWinnerRating).append(", ").append(developmentWinnerRating).append("]").toString();
	}
}
