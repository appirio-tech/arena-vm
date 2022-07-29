/**
 * AnnounceTCSCoder.java
 *
 * Description:		Announcement of a tcs coder
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.event;

import java.awt.Image;


public class AnnounceTCSCoderEvent extends java.util.EventObject {

	/** The contest id */
	private int roundID;
	
	/** Name of the coder */
	private String coderName;

	/** CoderType (Student/Professional) */
	private String coderType;

	/** Image bytes of the coder */
	private Image image;

	/** Coder's handle */
	private String handle;

	/** Coder's TC rating */
	private int tcRating;

	/** Coder's TCS rating */
	private int tcsRating;

	/** Coder's seed */
	private int seed;

	/** Coder's earnings */
	private double earnings;

	/** Number of submissions (tournament) */
	private int tournamentNumberSubmissions;

	/** Level 1 average (tournament) */
	private double tournamentLevel1Average;

	/** Level 2 average (tournament) */
	private double tournamentLevel2Average;

	/** Number of wins (tournament) */
	private int tournamentWins;

	/** Number of submissions (lifetime) */
	private int lifetimeNumberSubmissions;

	/** Level 1 average (lifetime) */
	private double lifetimeLevel1Average;

	/** Level 2 average (lifetime) */
	private double lifetimeLevel2Average;

	/** Number of wins (lifetime) */
	private int lifetimeWins;
        
        private String school;

    /** Constructor */
    public AnnounceTCSCoderEvent(Object source, int roundID, String coderName, String coderType, Image image, String handle, int tcRating, int tcsRating, int seed, double earnings, int tournamentNumberSubmissions, double tournamentLevel1Average, double tournamentLevel2Average, int tournamentWins, int lifetimeNumberSubmissions, double lifetimeLevel1Average, double lifetimeLevel2Average, int lifetimeWins, String school) {
    	super(source);  
    	this.roundID = roundID;
		this.coderName = coderName;
		this.coderType = coderType;
		this.image = image;
		this.handle = handle;
		this.tcRating = tcRating;
		this.tcsRating = tcsRating;
		this.seed = seed;
		this.earnings = earnings;
		this.tournamentNumberSubmissions = tournamentNumberSubmissions;
		this.tournamentLevel1Average = tournamentLevel1Average;
		this.tournamentLevel2Average = tournamentLevel2Average;
		this.tournamentWins = tournamentWins;
		this.lifetimeNumberSubmissions = lifetimeNumberSubmissions;
		this.lifetimeLevel1Average = lifetimeLevel1Average;
		this.lifetimeLevel2Average = lifetimeLevel2Average;
		this.lifetimeWins = lifetimeWins;
                this.school = school;
    }

	/** Get the contest name */
	public int getRoundID() {
		return roundID;
	}
	
	/** Return the coder name */
	public String getCoderName() {
		return coderName;
	}

	/** Return the handle */
	public String getHandle() {
		return handle;
	}

	/** Get the image */
	public Image getImage() {
		return image;
	}

	/** Gets the seed */
	public int getSeed() {
		return seed;
	}

	/** Gets the coder type */
	public String getCoderType() {
		return coderType;
	}

	/** Gets the earnings */
	public double getEarnings() {
		return earnings;
	}

	/** Gets the lifetime level 1 average */
	public double getLifetimeLevel1Average() {
		return lifetimeLevel1Average;
	}

	/** Gets the lifetime level2 average */
	public double getLifetimeLevel2Average() {
		return lifetimeLevel2Average;
	}

	/** Gets the lifetime number of submissions */
	public int getLifetimeNumberSubmissions() {
		return lifetimeNumberSubmissions;
	}

	/** Gets the lifetime number of wins */
	public int getLifetimeWins() {
		return lifetimeWins;
	}

	/** Gets the TC rating */
	public int getTcRating() {
		return tcRating;
	}

	/** Gets the TCS rating */
	public int getTcsRating() {
		return tcsRating;
	}

	/** Gets the tournament level 1 average */
	public double getTournamentLevel1Average() {
		return tournamentLevel1Average;
	}

	/** Gets the tournament level 2 average */
	public double getTournamentLevel2Average() {
		return tournamentLevel2Average;
	}

	/** Gets the tournament number of submissions */
	public int getTournamentNumberSubmissions() {
		return tournamentNumberSubmissions;
	}

	/** Gets the tournament wins */
	public int getTournamentWins() {
		return tournamentWins;
	}
        
        public String getSchool() {
            return school;
        }
}
