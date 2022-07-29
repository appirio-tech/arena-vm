package com.topcoder.client.spectatorApp.scoreboard.model;

import com.topcoder.shared.netCommon.messages.spectator.CoderData;

public interface CompetitionTracker<E extends CoderData> {
	/**
	 * Get's the placement tracker associated with this scoreboard
	 */
	public CompetitionPlacementTracker<E> getPlacementTracker();

	/**
	 * Returns the coder data for a specific index position
	 * 
	 * @returns the coder data
	 * @see com.topcoder.netCommon.spectatorMessages.CoderRoomData
	 */
	public E getCoder(int idx);

	/**
	 * Returns the index position of a given coderID
	 * 
	 * @param coderID
	 *           the coder id
	 * @returns a crossreference index or -1 if a coderID was not found
	 */
	public int indexOfCoder(int coderID);
}
