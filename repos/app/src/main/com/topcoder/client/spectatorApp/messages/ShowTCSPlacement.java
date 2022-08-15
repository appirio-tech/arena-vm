package com.topcoder.client.spectatorApp.messages;

import java.io.Serializable;
import com.topcoder.client.spectatorApp.CommonRoutines;

public class ShowTCSPlacement implements Serializable {
	
	private int[] placements;
        
        private int roundID;
	
	public ShowTCSPlacement() {		
	}
	
	public ShowTCSPlacement(int[] placements, int roundID) {
		this.placements = placements;
                this.roundID = roundID;
	}
        
        public int getRoundID() {
            return roundID;
        }
        
        public void setRoundID(int r) {
            this.roundID = r;
        }

	public int[] getPlacements() {
		return placements;
	}

	public void setPlacements(int[] placements) {
		this.placements = placements;
	}
	
	@Override
	public String toString() {
		return "(ShowTCSPlacement) [" + CommonRoutines.prettyPrint(placements) + "]";
	}
}
