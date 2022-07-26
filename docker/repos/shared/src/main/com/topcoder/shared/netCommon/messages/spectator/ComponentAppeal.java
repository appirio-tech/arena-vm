/**
 * Notification of a component appeal being placed
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

public class ComponentAppeal extends Message implements Serializable, Cloneable, CustomSerializable {
	/** the contest identifier for the request */
	private int contestID;
	
	/** the round identifier for the request */
	private int roundID;
	
	/** the component identifier for the request */
	private long componentID;
	
	/** the appeal id for the update */
	private long appealID;
	
	/** The coder id for the update */
	private int coderID;
	
	/** The reviewer id for the update */
	private int reviewerCoderID;
	
	/** The appeal status */
	private String status;
	
	/** The appeal status where an appeal is pending */
	public final static String APPEAL_PENDING = "pending";
	
	/** The appeal status where an appeal was successful.
	 * A ComponentScoreUpdate should follow this message
	 */
	public final static String APPEAL_SUCCESSFUL = "successful";
	
	/** The appeal status where an appeal was rejected */
	public final static String APPEAL_REJECTED = "rejected";
	
	/** No-arg constructor required by custom serialization */
	public ComponentAppeal() {
	}
	
	/**
	 * Create an appeal notification
	 * @param contestID the contest id
	 * @param roundID the round id
	 * @param componentID the component id
	 * @param coderID the coder id related to the appeal
	 * @param reviewerCoderID the reviewer ID related to the appeal
	 * @param status the status of the component appeal
	 */
	public ComponentAppeal(int contestID, int roundID, long componentID, long appealID, int coderID, 
	    int reviewerCoderID, String status) {
		this.contestID = contestID;
		this.roundID = roundID;
		this.componentID = componentID;
		this.appealID = appealID;
		this.coderID = coderID;
		this.reviewerCoderID = reviewerCoderID;
		this.status = status;
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
	 * The appeal id that is being updated
	 * @return the appeal id being update
	 */
	public long getAppealID() {
		return appealID;
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
	 * The status of the appeal 
	 * @return the status of the appeal
	 */
	public String getStatus() {
		return status;
	}
	
	public void setContestID(int contestID) {
		this.contestID = contestID;
	}
	
	public void setComponentID(long componentID) {
		this.componentID = componentID;
	}
	
	public void setRoundID(int roundID) {
		this.roundID = roundID;
	}
	
	public void setAppealID(long appealID) {
		this.appealID = appealID;
	}
	
	public void setCoderID(int coderID) {
		this.coderID = coderID;
	}
	public void setReviewerCoderID(int reviewerCoderID) {
		this.reviewerCoderID = reviewerCoderID;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	public void customWriteObject(CSWriter writer) throws IOException {
		writer.writeInt(contestID);
		writer.writeInt(roundID);
		writer.writeLong(componentID);
		writer.writeLong(appealID);
		writer.writeInt(coderID);
		writer.writeInt(reviewerCoderID);
		writer.writeString(status);
	}
	
	public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
		contestID = reader.readInt();
		roundID = reader.readInt();
		componentID = reader.readLong();
		appealID = reader.readLong();
		coderID = reader.readInt();
		reviewerCoderID = reader.readInt();
		status = reader.readString();
	}
	
	public String toString() {
		return "(ComponentAppeal)[" + contestID + ", " + roundID + ", " + componentID + ", " + appealID + ", " + 
		    coderID + ", " + reviewerCoderID + ", " + status + "]";
	}
}
