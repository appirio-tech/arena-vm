package com.topcoder.client.spectatorApp.announcer.events;

import java.util.Arrays;
import com.topcoder.client.spectatorApp.messages.ShowPlacement;

public class ShowPlacementEvent extends AnnouncerEvent {

	private int[] placements;
	
	public ShowPlacementEvent() {
	}
	
	@Override
	public Object getMessage() {
		return new ShowPlacement(placements);
	}

	@Override
	public void validateEvent() throws Exception {
	}

	public int[] getPlacements() {
		return placements;
	}

	public void setPlacements(int[] placements) {
		this.placements = placements;
		Arrays.sort(placements);
	}
	
}
