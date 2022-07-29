/**
 * Notification of a score update for a specific coder by a specific reviewer
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
package com.topcoder.shared.netCommon.messages.spectator;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;
import com.topcoder.shared.netCommon.messages.Message;

public class ComponentScoreUpdate extends Message implements Serializable, Cloneable, CustomSerializable  {
	/** the contest identifier for the request */
	private int contestID;
	
	/** the round identifier for the request */
	private int roundID;
	
	/** the component identifier for the request */
	private long componentID;

	/** The coder id for the update */
	private int coderID;
	
	/** The reviewer id for the update */
	private int reviewerCoderID;
	
	/** The new score */
	private int score;
	
	/** No-arg constructor required by custom serialization */
	public ComponentScoreUpdate() {
	}

	/**
	 * Create a request for an update of a score
	 * @param contestID the contest id
	 * @param roundID the round id
	 * @param componentID the component id
	 * @param coderID the coder id related to the update
	 * @param reviewerCoderID the reviewer ID related to the update
	 * @param score the new score
	 */
	public ComponentScoreUpdate(int contestID, int roundID, long componentID, int coderID, int reviewerCoderID, int score) {
		this.contestID = contestID;
		this.roundID = roundID;
		this.componentID = componentID;
		this.coderID = coderID;
		this.reviewerCoderID = reviewerCoderID;
		this.score = score;
	}

	/**
	 * The contest id that is being update
	 * @return the contest id that is being update
	 */
	public int getContestID() {
		return contestID;
	}

	/**
	 * The round id that is being update
	 * @return the round id that is being update
	 */
	public int getRoundID() {
		return roundID;
	}
	
	/**
	 * The component id that is being update
	 * @return the component id that is being update
	 */
	public long getComponentID() {
		return componentID;
	}

	/** 
	 * The coder id that is being updated
	 * @return the coder id being update
	 */
	public int getCoderID() {
		return coderID;
	}

	/** 
	 * The reviewer coder id that is being updated
	 * @return the reviewer coder id being update
	 */
	public int getReviewerCoderID() {
		return reviewerCoderID;
	}

	/** 
	 * The new score 
	 * @return the new score
	 */
	public int getScore() {
		return score;
	}

	public void setContestID(int contestID) {
		this.contestID = contestID;
	}
	public void setRoundID(int roundID) {
		this.roundID = roundID;
	}
	public void setComponentID(long componentID) {
		this.componentID = componentID;
	}
	public void setCoderID(int coderID) {
		this.coderID = coderID;
	}
	public void setReviewerCoderID(int reviewerCoderID) {
		this.reviewerCoderID = reviewerCoderID;
	}
	public void setScore(int score) {
		this.score = score;
	}

	public void customWriteObject(CSWriter writer) throws IOException {
		writer.writeInt(contestID);
		writer.writeInt(roundID);
		writer.writeLong(componentID);
		writer.writeInt(coderID);
		writer.writeInt(reviewerCoderID);
		writer.writeInt(score);
	}

	public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
		contestID = reader.readInt();
		roundID = reader.readInt();
		componentID = reader.readLong();
		coderID = reader.readInt();
		reviewerCoderID = reader.readInt();
		score = reader.readInt();
	}
	
	public String toString() {
		return "(ComponentScoreUpdate)[" + contestID + ", " + roundID + ", " + componentID + ", " + coderID + ", " + reviewerCoderID + ", " + score + "]";
	}
}
