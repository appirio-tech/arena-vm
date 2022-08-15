/**
 * RoomWinner.java
 *
 * Description:		Notifies the spectator application of a winner of a room
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.shared.netCommon.messages.spectator;

import com.topcoder.shared.netCommon.messages.*;
import com.topcoder.shared.netCommon.*;

import java.io.*;

public class RoomWinner extends Message {
	/** room information */
	private RoomData room;

	/** Coder information */
	private CoderData coder;

	/**
	 * No-arg constructor needed by customserialization
	 *
	 */
	public RoomWinner() {}

	/** Default Constructor */
	public RoomWinner(RoomData room, CoderData coder) {
		super();
		this.room = room;
		this.coder = coder;
	}

	/**
	 * Gets the room information
	 *
	 * @returns the room information
	 * @see com.topcoder.netCommon.spectatorMessages.RoomData
	 */
	public RoomData getRoom() {
		return room;
	}

	/**
	 * Gets the Coder information
	 *
	 * @returns the coder that has won the room
	 * @see com.topcoder.netCommon.spectatorMessages.CoderData
	 */
	public CoderData getCoder() {
		return coder;
	}

	/** Default customWriteObject */
	public void customWriteObject(CSWriter writer) throws IOException {
		super.customWriteObject(writer);
		room.customWriteObject(writer);
		coder.customWriteObject(writer);
	}

	/** Default customReadObject */
	public void customReadObject(CSReader reader) throws IOException {
		super.customReadObject(reader);

		room = new RoomData();
		room.customReadObject(reader);

		coder = new CoderData();
		coder.customReadObject(reader);
	}

	/**
	 * Gets the string representation of this object
	 *
	 * @returns the string representation of this object
	 */
	public String toString() {
		return new StringBuffer().append("(RoomWinner)[").append(room.toString()).append("]").append("(CoderData)[[").append(coder.toString()).append("]").toString();
	}
}


/* @(#)ShowMessage.java */
