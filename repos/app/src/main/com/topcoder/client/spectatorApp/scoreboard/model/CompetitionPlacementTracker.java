package com.topcoder.client.spectatorApp.scoreboard.model;

import java.util.List;
import com.topcoder.shared.netCommon.messages.spectator.CoderData;

public interface CompetitionPlacementTracker<E extends CoderData> {
	/** Dispose of resources */
	public void dispose();

	/** The the placement for the given coder id */
	public int getPlacement(int coderID);

	/** Determines if the specified coderID is tied with anyone */
	public boolean isTied(int coderID);

	/** Get the list of coders sorted in their placement order */
	public List<E> getCodersByPlacement();

	/** Get the list of coders sorted in the placement order without filtering events */
	public List<E> getCodersByPlacementUnfiltered();

	/** Get the placement change support */
	public PlacementChangeSupport getChangeSupport();
}
