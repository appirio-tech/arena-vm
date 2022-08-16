/**
 * Description: Contains information about an appeal status change
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
package com.topcoder.client.spectatorApp.scoreboard.model;

import com.topcoder.client.spectatorApp.Constants;
import com.topcoder.client.spectatorApp.Constants.AppealStatus;

public class AppealChangeEvent extends java.util.EventObject {
	/** The coder index of the change */
	private int coderIdx;

	/** The reviewer index of the change */
	private int reviewerIdx;

	/** The new status */
	private Constants.AppealStatus newStatus;

	/**
	 * Constructor of a Event
	 * 
	 * @param source the source of the event
	 * @param coderIdx the row that Change
	 * @param reviewerIdx the column that Change
	 * @param coderHandle the handle of the coder
	 */
	public AppealChangeEvent(Object source, int coderIdx, int reviewerIdx, Constants.AppealStatus newStatus) {
		super(source);
		this.coderIdx = coderIdx;
		this.reviewerIdx = reviewerIdx;
		this.newStatus = newStatus;
	}

	/**
	 * Returns the index of the coder
	 * 
	 * @returns the coder index
	 */
	public int getCoderIdx() {
		return coderIdx;
	}

	/**
	 * Returns the index of the reviewer
	 * 
	 * @returns the reviewer index
	 */
	public int getReviewerIdx() {
		return reviewerIdx;
	}

	/**
	 * Returns the new appeal status
	 * 
	 * @returns the appeal status
	 */
	public AppealStatus getAppealStatus() {
		return newStatus;
	}

	/**
	 * Returns the string representation of this event
	 * 
	 * @returns the string representation of this event
	 */
	public String toString() {
		return new StringBuffer().append("(AppealChangeEvent)[").append(coderIdx).append(", ").append(reviewerIdx).append(", ").append(newStatus).append("]").toString();
	}
}
