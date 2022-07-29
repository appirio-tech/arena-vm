package com.topcoder.shared.netCommon.messages.spectator;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;
import com.topcoder.shared.netCommon.messages.Message;

public class RequestComponentUpdate extends Message implements CustomSerializable, Cloneable, Serializable {
	/** the contest identifier for the request */
	private int contestID;
	
	/** the round identifier for the request */
	private int roundID;
	
	/** the component identifier for the request */
	private long componentID;

	/** No-arg constructor required by custom serialization */
	public RequestComponentUpdate() {
	}

	/**
	 * Create a request for an update on a component
	 * @param contestID the contest id
	 * @param roundID the round id
	 * @param componentID the component id
	 */
	public RequestComponentUpdate(int contestID, int roundID, long componentID) {
		this.contestID = contestID;
		this.roundID = roundID;
		this.componentID = componentID;
	}

	/**
	 * The contest id that is being request
	 * @return the contest id that is being request
	 */
	public int getContestID() {
		return contestID;
	}

	/**
	 * The round id that is being request
	 * @return the round id that is being request
	 */
	public int getRoundID() {
		return roundID;
	}
	
	/**
	 * The component id that is being request
	 * @return the component id that is being request
	 */
	public long getComponentID() {
		return componentID;
	}

	public void setComponentID(long componentID) {
		this.componentID = componentID;
	}
	public void setContestID(int contestID) {
		this.contestID = contestID;
	}
	public void setRoundID(int roundID) {
		this.roundID = roundID;
	}

	public void customWriteObject(CSWriter writer) throws IOException {
		writer.writeInt(contestID);
		writer.writeInt(roundID);
		writer.writeLong(componentID);
	}

	public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
		contestID = reader.readInt();
		roundID = reader.readInt();
		componentID = reader.readLong();
	}
	
	public String toString() {
		return "(RequestComponentUpdate)[" + contestID + ", " + roundID + ", " + componentID + "]";
	}
}
