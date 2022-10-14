/**
 * ChallengeTotalValuePanel.java Description: Total value panel to the
 * scoreboard rooms
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
package com.topcoder.client.spectatorApp.scoreboard.view;

import java.text.DecimalFormat;
import com.topcoder.client.spectatorApp.CommonRoutines;
import com.topcoder.client.spectatorApp.scoreboard.model.ComponentContest;
import com.topcoder.client.spectatorApp.scoreboard.model.PlacementChangeEvent;
import com.topcoder.client.spectatorApp.scoreboard.model.PlacementChangeListener;
import com.topcoder.client.spectatorApp.scoreboard.model.TotalChangeEvent;
import com.topcoder.client.spectatorApp.scoreboard.model.TotalChangeListener;
import com.topcoder.client.spectatorApp.views.AnimatePanel;

public class ComponentContestValue extends AbstractComponentValue implements AnimatePanel {
	/** Reference to the component contest */
	private ComponentContest contest;

	/** Coder index (in the component contest) assigned to this object */
	private int coderID;

	/** The placement tracker */
	private PlacementChangeHandler placementHandler = new PlacementChangeHandler();

	/** The total change handler */
	private TotalChangeHandler totalChangeHandler = new TotalChangeHandler();
	
	/** Constructs the user handle panel */
	public ComponentContestValue(ComponentContest contest, int coderID) {
		super();
		
		// Save the point value text
		this.contest = contest;
		this.coderID = coderID;
		if (contest.indexOfCoder(coderID) < 0) {
			throw new IllegalArgumentException("Unknown coder id: " + coderID);
		}
		
		contest.addTotalChangeListener(totalChangeHandler);
		totalChangeHandler.updateTotal(null);
		
		contest.getPlacementTracker().getChangeSupport().addListener(placementHandler);
		placementHandler.placementChanged(null); // force a change to initially set the ranking
		
		doLayout();
	}
	
	public void dispose() {
		contest.removeTotalChangeListener(totalChangeHandler);
		contest.getPlacementTracker().getChangeSupport().removeListener(placementHandler);
		super.dispose();
	}
	
	private class TotalChangeHandler implements TotalChangeListener
	{
		final DecimalFormat avgFormatter = new DecimalFormat("##0.00");

		public void updateTotal(TotalChangeEvent evt) {
			int value = contest.getAverageScore(contest.indexOfCoder(coderID));
			avgValueField.setVisible(value >= 0);
			placementField.setVisible(value >= 0);
			
			if (value >= 0) {
				avgValueField.setText(avgFormatter.format(value / 100.0));
				avgValueField.resize();
				doLayout();
			}
		}
	}
	
	private class PlacementChangeHandler implements PlacementChangeListener
	{
		public void placementChanged(PlacementChangeEvent evt) {
			int placement = contest.getPlacementTracker().getPlacement(coderID); 
			if (placement < 1) {
				placementField.setText("");
			} else {
				if (contest.getPlacementTracker().isTied(coderID)) {
					placementField.setText("T-" + CommonRoutines.getRank(placement));
				} else {
					placementField.setText(CommonRoutines.getRank(placement));
				}
			}
			placementField.resize();
			doLayout();
		}
	}
}
