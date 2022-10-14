package com.topcoder.client.spectatorApp.announcer.events;

import com.topcoder.shared.netCommon.messages.spectator.DefineContest;

/**
 * The define contest event. This event will implement the javabean standard
 * since it is read by the XMLDecoder
 * 
 * @author Pops
 */
public class DefineContestEvent extends AnnouncerEvent {
	/** The contest id */
	private int contestID;

	/** The contest name */
	private String contestName;

	/** The logo large filename */
	private String logoLargeFileName;

	/** The logo large file */
	private byte[] logoLargeImage;

	/** The logo small filename */
	private String logoSmallFileName;

	/** The logo small file */
	private byte[] logoSmallImage;

	/** The sponsor filename */
	private String sponsorFileName;

	/** The sponsor file */
	private byte[] sponsorImage;

	/** Empty Constructor - as required by javabean standard */
	public DefineContestEvent() {}

	/** Returns the DefineContest message */
	public Object getMessage() {
		return new DefineContest(contestID, contestName, logoLargeImage, logoSmallImage, sponsorImage);
	}

	/** Return the contest id */
	public int getContestID() {
		return contestID;
	}

	/** Sets the contest ID */
	public void setContestID(int contestID) {
		this.contestID = contestID;
	}

	/** Return the contest name */
	public String getContestName() {
		return contestName;
	}

	/** Sets the contest name */
	public void setContestName(String contestName) {
		this.contestName = contestName;
	}

	/** Return the large logo file name */
	public String getLogoLargeFileName() {
		return logoLargeFileName;
	}

	/** Sets the large logo file name */
	public void setLogoLargeFileName(String logoLargeFileName) {
		this.logoLargeFileName = logoLargeFileName;
	}

	/** Returns the small logo file name */
	public String getLogoSmallFileName() {
		return logoSmallFileName;
	}

	/** Sets the small logo file name */
	public void setLogoSmallFileName(String logoSmallFileName) {
		this.logoSmallFileName = logoSmallFileName;
	}

	/** Returns the sponsor file name */
	public String getSponsorFileName() {
		return sponsorFileName;
	}

	/** Sets the sponsor file name */
	public void setSponsorFileName(String sponsorFileName) {
		this.sponsorFileName = sponsorFileName;
	}

	/** Nothing to validate! */
	public void validateEvent() throws Exception {
		this.logoSmallImage = getImage(logoSmallFileName);
		this.sponsorImage = getImage(sponsorFileName);
		this.logoLargeImage = getImage(logoLargeFileName);
	}
}
