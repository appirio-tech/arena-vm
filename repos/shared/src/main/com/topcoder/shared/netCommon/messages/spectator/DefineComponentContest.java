/**
 * Defines the information for a specific component contest
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
package com.topcoder.shared.netCommon.messages.spectator;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;
import com.topcoder.shared.netCommon.messages.Message;

public class DefineComponentContest extends Message implements Serializable, Cloneable, CustomSerializable {
	/** The contest identifier */
	private int contestID;
	
	/** The round identifier */
	private int roundID;
	
	/** The component information */
	private ComponentData componentData;
	
	/** The list of coders for the component contest (ComponentCoder elements)*/
	private final ArrayList coderData = new ArrayList();
	
	/** The list of review board members (CoderData elements) */
	private final ArrayList reviewBoardMembers = new ArrayList();

	/** Noarg constructor required by custom serialization */
	public DefineComponentContest() {
	}
	
	/** Constructs the component contest from the specified args
	 * @param contestID the contest identifier
	 * @param roundID the round identifier
	 * @param componentData the component information
	 * @param coderData the list of ComponentCoder elements defining the coders and their wagers on the componet
	 * @param reviewBoardMembers the list of CoderData elements defining the review board members
	 */
	public DefineComponentContest(int contestID, int roundID, ComponentData componentData, List coderData, List reviewBoardMembers) {
		this.contestID = contestID;
		this.roundID = roundID;
		this.componentData = componentData;
		this.coderData.addAll(coderData);
		this.reviewBoardMembers.addAll(reviewBoardMembers);
	}

	/** 
	 * The contest id being defined
	 * @return the contest id being defined
	 */
	public int getContestID() {
		return contestID;
	}

	/**
	 * The round id being defined
	 * @return the round id being defined
	 */
	public int getRoundID() {
		return roundID;
	}

	/**  
	 * The component information
	 * @return the component information
	 */
	public ComponentData getComponentData() {
		return componentData;
	}

	/**
	 * An unmodifiable list of ComponentCoder elements defining the coders in the contest
	 * @return an unmodifiable list of ComponentCoder elements defining the coders in the contest
	 */
	public ArrayList getCoderData() {
		return new ArrayList(coderData);
	}

	/**
	 * An unmodifiable list of CoderData elements defining the review board members in the contest
	 * @return an unmodifiable list of CoderData elements defining the review board members in the contest
	 */
	public ArrayList getReviewBoardMembers() {
		return new ArrayList(reviewBoardMembers);
	}

	public void setContestID(int contestID) {
		this.contestID = contestID;
	}
	public void setRoundID(int roundID) {
		this.roundID = roundID;
	}
	public void setComponentData(ComponentData componentData) {
		this.componentData = componentData;
	}
	public void setCoderData(ArrayList coderData) {
		this.coderData.clear();
		this.coderData.addAll(coderData);
	}
	public void setReviewBoardMembers(ArrayList reviewBoardMembers) {
		this.reviewBoardMembers.clear();
		this.reviewBoardMembers.addAll(reviewBoardMembers);
	}
	
	public void customWriteObject(CSWriter writer) throws IOException {
		writer.writeInt(contestID);
		writer.writeInt(roundID);
		writer.writeObject(componentData);
		writer.writeArrayList(coderData);
		writer.writeArrayList(reviewBoardMembers);
	}

	public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
		contestID = reader.readInt();
		roundID = reader.readInt();
		componentData = (ComponentData) reader.readObject();
		
		coderData.clear();
		coderData.addAll(reader.readArrayList());
		
		reviewBoardMembers.clear();
		reviewBoardMembers.addAll(reader.readArrayList());
	}

	public String toString() {
		return "(ComponentCoder)[" + contestID + ", " + roundID + ", " + componentData + ", " + coderData + ", " + reviewBoardMembers + "]";
	}
}
