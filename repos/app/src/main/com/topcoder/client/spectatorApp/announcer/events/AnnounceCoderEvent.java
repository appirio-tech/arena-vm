package com.topcoder.client.spectatorApp.announcer.events;

import com.topcoder.client.spectatorApp.messages.AnnounceCoder;
import com.topcoder.client.spectatorApp.messages.CoderStats;
import com.topcoder.client.spectatorApp.messages.InvitationalStats;

/**
 * The announce coder event.  
 * 
 * This event will implement the javabean standard since it is read by the XMLDecoder
 * 
 * @author Pops
 */
public class AnnounceCoderEvent extends AnnouncerEvent {

	/** Round the coder is part of */
	private int roundID;

	/** Name of the coder */
	private String coderName;

	/** Image file name for the coder*/
	private String imageFileName;

	/** Image bytes of the coder */
	private byte[] image;

	/** College the coder belongs to */
	private String college;

	/** Coder's handle */
	private String handle;

	/** Coder's rating */
	private int rating;

	/** Coder's ranking */
	private int ranking;

	/** Coder's seed */
	private int seed;

	/** Number of competitions (invitational) */
	private int invitationalNumberCompetitions;

	/** Number of submissions (invitational) */
	private int invitationalNumberSubmissions;

	/** Submission percentage (invitational) */
	private double invitationalSubmissionPercent;

	/** Number of challenges (invitational) */
	private int invitationalNumberChallenges;

	/** Challenge percentage (invitational) */
	private double invitationalChallengePercent;

	/** Number of competitions */
	private int srmNumberCompetitions;

	/** Number of submissions */
	private int srmNumberSubmissions;

	/** Submission percentage */
	private double srmSubmissionPercent;

	/** Number of challenges */
	private int srmNumberChallenges;

	/** Challenge percentage */
	private double srmChallengePercent;

	/** Empty Constructor - required by Javabean standard */
	public AnnounceCoderEvent() {
	}
	
	/** Returns the AnnouncerCoder message */
	public Object getMessage() {
		// Create the stat structures
		InvitationalStats inviteStats = new InvitationalStats(invitationalNumberCompetitions, invitationalNumberSubmissions, invitationalSubmissionPercent, invitationalNumberChallenges, invitationalChallengePercent);
		CoderStats coderStats = new CoderStats(srmNumberCompetitions, srmNumberSubmissions, srmSubmissionPercent, srmNumberChallenges, srmChallengePercent);
		
		// Return the message
		return new AnnounceCoder(roundID, coderName, image, college, handle, rating, ranking, seed, coderStats, inviteStats);
	}
	
	/** Return the coder name */
	public String getCoderName() {
		return coderName;
	}

	/** Set the coder name */
	public void setCoderName(String coderName) {
		this.coderName = coderName;
	}

	/** Return the college */
	public String getCollege() {
		return college;
	}

	/** Set the college */
	public void setCollege(String college) {
		this.college = college;
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


	/** Gets the invitational challenge percent */
	public double getInvitationalChallengePercent() {
		return invitationalChallengePercent;
	}

	/** Sets the invitational challenge percent */
	public void setInvitationalChallengePercent(double invitationalChallengePercent) {
		this.invitationalChallengePercent = invitationalChallengePercent;
	}

	/** Gets the invitational number challenges */
	public int getInvitationalNumberChallenges() {
		return invitationalNumberChallenges;
	}

	/** Sets the invitational number challenges */
	public void setInvitationalNumberChallenges(int invitationalNumberChallenges) {
		this.invitationalNumberChallenges = invitationalNumberChallenges;
	}

	/** Gets the invitational number competitions */
	public int getInvitationalNumberCompetitions() {
		return invitationalNumberCompetitions;
	}

	/** Sets the invitational number competitions */
	public void setInvitationalNumberCompetitions(int invitationalNumberCompetitions) {
		this.invitationalNumberCompetitions = invitationalNumberCompetitions;
	}

	/** Gets the invitational number submissions */
	public int getInvitationalNumberSubmissions() {
		return invitationalNumberSubmissions;
	}

	/** Sets the invitational number submissions */
	public void setInvitationalNumberSubmissions(int invitationalNumberSubmissions) {
		this.invitationalNumberSubmissions = invitationalNumberSubmissions;
	}

	/** Gets the invitational submission percent */
	public double getInvitationalSubmissionPercent() {
		return invitationalSubmissionPercent;
	}

	/** Sets the invitational submission percent */
	public void setInvitationalSubmissionPercent(double invitationalSubmissionPercent) {
		this.invitationalSubmissionPercent = invitationalSubmissionPercent;
	}

	/** Gets the ranking */
	public int getRanking() {
		return ranking;
	}

	/** Sets the ranking */
	public void setRanking(int ranking) {
		this.ranking = ranking;
	}

	/** Gets the rating */
	public int getRating() {
		return rating;
	}

	/** Sets the rating */
	public void setRating(int rating) {
		this.rating = rating;
	}

	/** Gets the round id */
	public int getRoundID() {
		return roundID;
	}

	/** Sets the round id */
	public void setRoundID(int roundID) {
		this.roundID = roundID;
	}

	/** Gets the seed */
	public int getSeed() {
		return seed;
	}

	/** Sets the seed */
	public void setSeed(int seed) {
		this.seed = seed;
	}

	/** Get the SRM challenge percent */
	public double getSRMChallengePercent() {
		return srmChallengePercent;
	}

	/** Sets the SRM challenge percent */
	public void setSrmChallengePercent(double srmChallengePercent) {
		this.srmChallengePercent = srmChallengePercent;
	}

	/** Gets the SRM number challenges */
	public int getSRMNumberChallenges() {
		return srmNumberChallenges;
	}

	/** Sets the SRM number challenges */
	public void setSrmNumberChallenges(int srmNumberChallenges) {
		this.srmNumberChallenges = srmNumberChallenges;
	}

	/** Gets the SRM number Competitions */
	public int getSRMNumberCompetitions() {
		return srmNumberCompetitions;
	}

	/** Sets the SRM number Competitions */
	public void setSrmNumberCompetitions(int srmNumberCompetitions) {
		this.srmNumberCompetitions = srmNumberCompetitions;
	}

	/** Gets the SRM number submissions */
	public int getSRMNumberSubmissions() {
		return srmNumberSubmissions;
	}

	/** Sets the SRM number Submissions */
	public void setSrmNumberSubmissions(int srmNumberSubmissions) {
		this.srmNumberSubmissions = srmNumberSubmissions;
	}

	/** Gets the SRM submission percent */
	public double getSRMSubmissionPercent() {
		return srmSubmissionPercent;
	}

	/** Sets the SRM submission Percent */
	public void setSrmSubmissionPercent(double srmSubmissionPercent) {
		this.srmSubmissionPercent = srmSubmissionPercent;
	}

	/** Validate image filenames */
	public void validateEvent() throws Exception {
		this.image = getImage(imageFileName);
	}

}
