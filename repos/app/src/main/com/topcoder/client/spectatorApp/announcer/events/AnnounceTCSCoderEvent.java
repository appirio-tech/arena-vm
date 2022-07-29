package com.topcoder.client.spectatorApp.announcer.events;

import com.topcoder.client.spectatorApp.messages.AnnounceTCSCoder;


/**
 * The announce TCS coder event.  
 * 
 * This event will implement the javabean standard since it is read by the XMLDecoder
 * 
 * @author Pops
 */
public class AnnounceTCSCoderEvent extends AnnouncerEvent {

	/** The contest identifier */
	private int roundID;
	
	/** Name of the coder */
	private String coderName;

	/** CoderType (Student/Professional) */
	private String coderType;

	/** Image file name for the coder*/
	private String imageFileName;

	/** Image bytes of the coder */
	private byte[] image;

	/** Coder's handle */
	private String handle;
        
        /** Coder's school */
        private String school;

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

	/** Empty Constructor - required by Javabean standard */
	public AnnounceTCSCoderEvent() {
	}
	
	/** Returns the AnnouncerCoder message */
	public Object getMessage() {
		return new AnnounceTCSCoder(roundID, 
									coderName,
									coderType,
									image,
									handle,
									tcRating,
									tcsRating,
									seed,
									earnings,
									tournamentNumberSubmissions,
									tournamentLevel1Average,
									tournamentLevel2Average,
									tournamentWins,
									lifetimeNumberSubmissions,
									lifetimeLevel1Average,
									lifetimeLevel2Average,
									lifetimeWins,
                                                                        school);
	}
	
	/** Return the coder name */
	public String getCoderName() {
		return coderName;
	}
        
        public String getSchool() {
                return school;
        }

	/** Set the coder name */
	public void setCoderName(String coderName) {
		this.coderName = coderName;
	}
        
        public void setSchool(String school)
        {
                this.school = school;
        }

	/** Return the handle */
	public String getHandle() {
		return handle;
	}

	/** Sets the handle */
	public void setHandle(String handle) {
		this.handle = handle;
	}

	/** Get the image file name */
	public String getImageFileName() {
		return imageFileName;
	}

	/** Sets the image file name */
	public void setImageFileName(String imageFileName) {
		this.imageFileName = imageFileName;
	}

	/** Get the coder image */
	public byte[] getCoderImage() {
		return image;
	}

	/** Gets the seed */
	public int getSeed() {
		return seed;
	}

	/** Sets the seed */
	public void setSeed(int seed) {
		this.seed = seed;
	}

	/** Gets the coder type */
	public String getCoderType() {
		return coderType;
	}

	/** Sets the coder type */
	public void setCoderType(String coderType) {
		this.coderType = coderType;
	}

	/** Gets the earnings */
	public double getEarnings() {
		return earnings;
	}

	/** Sets the earnings */
	public void setEarnings(double earnings) {
		this.earnings = earnings;
	}

	/** Gets the lifetime level 1 average */
	public double getLifetimeLevel1Average() {
		return lifetimeLevel1Average;
	}

	/** Sets the lifetime level 1 average */
	public void setLifetimeLevel1Average(double lifetimeLevel1Average) {
		this.lifetimeLevel1Average = lifetimeLevel1Average;
	}

	/** Gets the lifetime level2 average */
	public double getLifetimeLevel2Average() {
		return lifetimeLevel2Average;
	}

	/** Sets the lifetime level 2 average */
	public void setLifetimeLevel2Average(double lifetimeLevel2Average) {
		this.lifetimeLevel2Average = lifetimeLevel2Average;
	}

	/** Gets the lifetime number of submissions */
	public int getLifetimeNumberSubmissions() {
		return lifetimeNumberSubmissions;
	}

	/** Sets the lifetime number of submissions */
	public void setLifetimeNumberSubmissions(int lifetimeNumberSubmissions) {
		this.lifetimeNumberSubmissions = lifetimeNumberSubmissions;
	}

	/** Gets the lifetime number of wins */
	public int getLifetimeWins() {
		return lifetimeWins;
	}

	/** Sets the lifetime number of wins */
	public void setLifetimeWins(int lifetimeWins) {
		this.lifetimeWins = lifetimeWins;
	}

	/** Gets the TC rating */
	public int getTcRating() {
		return tcRating;
	}

	/** Sets the TC rating */
	public void setTcRating(int tcRating) {
		this.tcRating = tcRating;
	}

	/** Gets the TCS rating */
	public int getTcsRating() {
		return tcsRating;
	}

	/** Sets the TCS rating */
	public void setTcsRating(int tcsRating) {
		this.tcsRating = tcsRating;
	}

	/** Gets the tournament level 1 average */
	public double getTournamentLevel1Average() {
		return tournamentLevel1Average;
	}

	/** Sets the tournament level 1 average */
	public void setTournamentLevel1Average(double tournamentLevel1Average) {
		this.tournamentLevel1Average = tournamentLevel1Average;
	}

	/** Gets the tournament level 2 average */
	public double getTournamentLevel2Average() {
		return tournamentLevel2Average;
	}

	/** Sets the tournament level 2 average */
	public void setTournamentLevel2Average(double tournamentLevel2Average) {
		this.tournamentLevel2Average = tournamentLevel2Average;
	}

	/** Gets the tournament number of submissions */
	public int getTournamentNumberSubmissions() {
		return tournamentNumberSubmissions;
	}

	/** Sets the tournament number of submissions */
	public void setTournamentNumberSubmissions(int tournamentNumberSubmissions) {
		this.tournamentNumberSubmissions = tournamentNumberSubmissions;
	}

	/** Gets the tournament wins */
	public int getTournamentWins() {
		return tournamentWins;
	}

	/** Sets the tournament wins */
	public void setTournamentWins(int tournamentWins) {
		this.tournamentWins = tournamentWins;
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
		this.image = getImage(imageFileName);
	}

}
