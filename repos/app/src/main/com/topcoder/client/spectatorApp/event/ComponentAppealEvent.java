package com.topcoder.client.spectatorApp.event;

import com.topcoder.client.spectatorApp.Constants.AppealStatus;

public class ComponentAppealEvent extends ComponentEvent {
	private long appealID;
	private int coderID;
	private int reviewerID;
	private AppealStatus status;
	
	public ComponentAppealEvent(Object source, int contestID, int roundID, long componentID, long appealID, int coderID, int reviewerID, AppealStatus status) {
		super(source, contestID, roundID, componentID);
		this.appealID = appealID;
		this.coderID = coderID;
		this.reviewerID = reviewerID;
		this.status = status;
	}

	public long getAppealID() {
		return appealID;
	}

	public int getCoderID() {
		return coderID;
	}

	public int getReviewerID() {
		return reviewerID;
	}

	public AppealStatus getStatus() {
		return status;
	}
}
