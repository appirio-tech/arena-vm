/**
 * SystemTestDoneRenderer.java Description: Coding Phase renderer
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
package com.topcoder.client.spectatorApp.scoreboard.view;

import com.topcoder.client.spectatorApp.scoreboard.model.ScoreboardPointTracker;
import com.topcoder.client.spectatorApp.views.AnimatePanel;
import com.topcoder.client.spectatorApp.widgets.SDimmerFilter;

public class RankedSystemTestDoneRenderer extends RankedSystemTestRenderer implements AnimatePanel {
	/** Constructs the panel */
	public RankedSystemTestDoneRenderer(String roomTitle, String contestName, ScoreboardPointTracker pointTracker) {
		// Construct the panel
		super(roomTitle, contestName, pointTracker);

	}

	@Override
	protected void layoutRanks() {
		// Loop through and set outlines/filters
		RankedCells rc = getRankedCells();
		table.setRows(rc.cells);
		
		// Clear all outlines and filters
		table.clearRowOutlines();
		table.clearRowFilters();
		
		for (int r = 0; r < rc.coderRoomData.length; r++) {
			int idx = tracker.indexOfCoder(rc.coderRoomData[r].getCoderID());
			if (idx >=0 && !tracker.isWinner(idx)) {
				table.setRowFilter(r, SDimmerFilter.INSTANCE);
			}
		}

		// Layout table
		table.doLayout();
	}
}
