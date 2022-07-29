package com.topcoder.client.spectatorApp.announcer.events;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.messages.spectator.CoderData;
import com.topcoder.shared.netCommon.messages.spectator.RoomData;
import com.topcoder.shared.netCommon.messages.spectator.RoomWinner;

/**
 * Sets the room winner
 * 
 * This event will implement the javabean standard since it is read by the XMLDecoder
 * 
 * @author Pops
 */
public class SetRoomWinnerEvent extends AnnouncerEvent {

	/** The room ID */
	private int roomID;
	
	/** The coder handle */
	private String handle;
	
	/** Empty constructor as defined by the javabean standard */
	public SetRoomWinnerEvent() {
	}
	
	/** Returns the ShowInitial message */
	public Object getMessage() {
		return new RoomWinner(new RoomData(roomID, ContestConstants.CODER_ROOM), new CoderData(-1, handle, -1));
	}
	
	/** Nothing to validate! */
	public void validateEvent() {}
	
	/** Gets the handle */
	public String getHandle() {
		return handle;
	}

	/** Sets the handle */
	public void setHandle(String handle) {
		this.handle = handle;
	}

	/** Gets the room id*/
	public int getRoomID() {
		return roomID;
	}

	/** Sets the handle */
	public void setRoomID(int roomID) {
		this.roomID = roomID;
	}

}
