package com.topcoder.client.spectatorApp.event;

import java.awt.Image;


/**
 * AnnounceTCSWinners
 *
 * Description:		Announcement of a the winners of a TCS contest
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

public class AnnounceTCSWinnersEvent extends java.util.EventObject {

	/** The contest id */
	private int roundID;

	/** Design winner handle */
	private String designHandle;
		
	/** Design winner image */
	private Image designImage;
	
	/** Design Winner Average */
	private double designWinnerAverage;
	
	/** Development winner handle */
	private String developmentHandle;
	
	/** Development winner image */
	private Image developmentImage;
	
	/** Development Winner Average */
	private double developmentWinnerAverage;
        
        private int developmentWinnerRating;
        private int designWinnerRating;
	
	/** Empty constructor as defined by the javabean standard */
	public AnnounceTCSWinnersEvent(Object source, int roundID, String designHandle, Image designImage, double designWinnerAverage, String developmentHandle, Image developmentImage, double developmentWinnerAverage, int designWinnerRating, int developmentWinnerRating) {
		super(source);
		this.roundID = roundID;
		this.designHandle = designHandle;
		this.designImage = designImage;
		this.designWinnerAverage = designWinnerAverage;
		this.developmentHandle = developmentHandle;
		this.developmentImage = developmentImage;
		this.developmentWinnerAverage = developmentWinnerAverage;
                this.designWinnerRating = designWinnerRating;
                this.developmentWinnerRating = developmentWinnerRating;
	}
	
	/** Get the contest name */
	public int getRoundID() {
		return roundID;
	}
		
	/** Get the design winner handle */
	public String getDesignHandle() {
		return designHandle;
	}
	
	/** Get the design image */
	public Image getDesignImage() {
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
	public Image getDevelopmentImage() {
		return developmentImage;
	}

	/** Get the development winner average */
	public double getDevelopmentWinnerAverage() {
		return developmentWinnerAverage;
	}
        
        public int getDesignWinnerRating() {
            return this.designWinnerRating;
        }
        public int getDevelopmentWinnerRating() {
            return this.developmentWinnerRating;
        }
}
