package com.topcoder.client.spectatorApp.announcer.events;

import java.util.Arrays;
import com.topcoder.client.spectatorApp.messages.ShowTCSPlacement;

public class ShowTCSPlacementEvent extends AnnouncerEvent {

	private int[] placements;
        private int roundID;
	
	public ShowTCSPlacementEvent() {
	}
	
	@Override
	public Object getMessage() {
		return new ShowTCSPlacement(placements, roundID);
	}

	@Override
	public void validateEvent() throws Exception {
	}

        public int getRoundID() {
            return roundID;
        }
        
        public void setRoundID(int r) {
            roundID = r;
        }
        
	public int[] getPlacements() {
		return placements;
	}

	public void setPlacements(int[] placements) {
		this.placements = placements;
		Arrays.sort(placements);
	}
	
}
