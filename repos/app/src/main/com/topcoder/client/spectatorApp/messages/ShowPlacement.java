package com.topcoder.client.spectatorApp.messages;

import java.io.Serializable;
import com.topcoder.client.spectatorApp.CommonRoutines;

public class ShowPlacement implements Serializable {
	
	private int[] placements;
	
	public ShowPlacement() {		
	}
	
	public ShowPlacement(int[] placements) {
		this.placements = placements;
	}

	public int[] getPlacements() {
		return placements;
	}

	public void setPlacements(int[] placements) {
		this.placements = placements;
	}
	
	@Override
	public String toString() {
		return "(ShowPlacement) [" + CommonRoutines.prettyPrint(placements) + "]";
	}
}
