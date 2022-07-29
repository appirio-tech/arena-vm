/**
 * TCSReviewBoardRenderer.java
 *
 * Description:		The renderer for the review board
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.tcs;

import com.topcoder.client.spectatorApp.event.AnnounceReviewBoardEvent;
import com.topcoder.client.spectatorApp.widgets.SGridCell;
import com.topcoder.client.spectatorApp.widgets.SGridLayout;
import com.topcoder.client.spectatorApp.widgets.SGridSizingPolicy;
import com.topcoder.client.spectatorApp.widgets.SImage;
import com.topcoder.client.spectatorApp.widgets.SLabelledImage;
import com.topcoder.client.spectatorApp.widgets.SPage;


public class TCSReviewBoardRenderer extends SPage {

    /** Constructs the trailer panel */
    public TCSReviewBoardRenderer(AnnounceReviewBoardEvent evt, String title, String roundName, String contestName) {

		// Add the layers
		super(new HeaderWithTitleRenderer(roundName, title),
			  new BodyPanel(evt),
			  new TrailerRenderer(contestName));

    }

	/** The main body */
	static class BodyPanel extends SGridLayout {
		
		public BodyPanel(AnnounceReviewBoardEvent evt) {

			// Create the column grids
			SGridCell[][] columns = new SGridCell[1][evt.getHandles().length];
			
			// Create each column		
			// !! Assumes every array in evt is the same size !!!	
			for(int x=0;x<evt.getHandles().length;x++) {
				// Create the Image renderers
				ReviewBoardHandleRenderer handle = new ReviewBoardHandleRenderer(evt.getHandles()[x], evt.getTcRatings()[x], evt.getTcsRatings()[x]);
				SLabelledImage labelledImage = new SLabelledImage(new SImage(evt.getImages()[x]), handle);
				
				// Add the column
				columns[0][x] = new SGridCell(labelledImage, SGridCell.CENTER, SGridCell.NONE);
			}
			
			// Make the columns evenly spaced and setup the grid
			this.setColumnSizingPolicy(SGridSizingPolicy.EVEN_SIZING);
			this.setGrid(columns);
		}	
	}
}
