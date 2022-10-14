/**
 * SystemTestDoneRenderer.java
 *
 * Description:		Coding Phase renderer
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.scoreboard.view;

import com.topcoder.client.spectatorApp.scoreboard.model.ScoreboardPointTracker;
import com.topcoder.client.spectatorApp.views.AnimatePanel;
import com.topcoder.client.spectatorApp.widgets.SDimmerFilter;
import com.topcoder.client.spectatorApp.widgets.SPage;
import com.topcoder.client.spectatorApp.widgets.STable;


public class SmallSystemTestDoneRenderer extends SmallSystemTestRenderer implements AnimatePanel {

    /** Constructs the panel */
    public SmallSystemTestDoneRenderer(String roomTitle, String contestName, ScoreboardPointTracker pointTracker) {
    	// Construct the panel
		super(roomTitle, contestName, pointTracker);
	
		// Get the testing renderer
		SmallSystemTestRenderer.SystemRenderer testLayer = (SmallSystemTestRenderer.SystemRenderer)getLayer(SPage.BODY_LAYER);
		
		// Get the row layers
		STable table = (STable)testLayer.getLayer(SmallSystemTestRenderer.SystemRenderer.TABLE_LAYER);
		
		// Add our highlight filter to the none winners
		for(int r=0;r<pointTracker.getCoderCount();r++) {
			if(!pointTracker.isWinner(r)) {
				table.setRowFilter(r, SDimmerFilter.INSTANCE);
			}
		}
		 
    }
    
}
