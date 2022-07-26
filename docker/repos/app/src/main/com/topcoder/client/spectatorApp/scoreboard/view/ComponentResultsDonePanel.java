package com.topcoder.client.spectatorApp.scoreboard.view;

import java.util.List;
import org.apache.log4j.Category;
import com.topcoder.client.spectatorApp.controller.GUIController;
import com.topcoder.client.spectatorApp.scoreboard.model.ComponentRound;
import com.topcoder.client.spectatorApp.scoreboard.model.PlacementChangeEvent;
import com.topcoder.client.spectatorApp.scoreboard.model.PlacementChangeListener;
import com.topcoder.client.spectatorApp.widgets.SDimmerFilter;
import com.topcoder.client.spectatorApp.widgets.STable;
import com.topcoder.shared.netCommon.messages.spectator.ComponentCoder;

public class ComponentResultsDonePanel extends ComponentResultsPanel {
	/** Reference to the logging category */
	private static final Category cat = Category.getInstance(ComponentResultsDonePanel.class.getName());

	private ComponentRound contest;
	
	/** The change handle */
	private PlacementChangeListener placementHandler = new PlacementChangeListener() {
		public void placementChanged(PlacementChangeEvent evt) {
			recalculateWinner();
		}		
	};
	
	public ComponentResultsDonePanel(String title, ComponentRound contest) {
		super(title, contest);
		this.contest = contest;

		contest.getPlacementTracker().getChangeSupport().addListener(placementHandler);
		recalculateWinner();
	}
	
	@Override
	public void dispose() {
		contest.getPlacementTracker().getChangeSupport().removeListener(placementHandler);
	}
	
	private void recalculateWinner() {
		final STable table = getBodyPanel().getTableLayer();
		
		GUIController.getInstance().enablePainting(false);
		
		// Add our highlight filter to the none winners
		List<ComponentCoder> coders = contest.getPlacementTracker().getCodersByPlacement();
		
		for(int r=0;r<contest.getCoderCount();r++) {
			final int coderRow = coders.indexOf(contest.getCoder(r));
//			final int coderID = contest.getCoder(r).getCoderID();
//			final int coderRow = contest.getPlacementTracker().getPlacement(coderID);
			cat.error(">>>> CoderID: " + contest.getCoder(r).getHandle() + ":" + r + ":" + coderRow);
			if (!contest.isWinningCoder(r)) {
				cat.error(">>>> CoderID: " + contest.getCoder(r).getHandle() + ": DIMMING");
				table.setRowFilter(coderRow, SDimmerFilter.INSTANCE);
			}
		}
		GUIController.getInstance().enablePainting(true);
	}
}
