package com.topcoder.client.spectatorApp.scoreboard.view;

import java.text.DecimalFormat;
import com.topcoder.client.spectatorApp.scoreboard.model.ComponentRound;
import com.topcoder.client.spectatorApp.scoreboard.model.TotalChangeEvent;
import com.topcoder.client.spectatorApp.scoreboard.model.TotalChangeListener;
import com.topcoder.client.spectatorApp.widgets.STextField;

public class ComponentContestTotalValue extends AbstractComponentValue {
	/** Reference to the component contest */
	private ComponentRound contest;

	/** Coder index (in the component contest) assigned to this object */
	private int coderIdx;

	/** The total change handler */
	private TotalChangeHandler totalChangeHandler = new TotalChangeHandler();
	
	/** Constructs the user handle panel */
	public ComponentContestTotalValue(ComponentRound contest, int coderIdx) {
		super();
		
		// Save the point value text
		this.contest = contest;
		this.coderIdx = coderIdx;

		placementField.setVisible(false);
		
		contest.addTotalChangeListener(totalChangeHandler);
		totalChangeHandler.updateTotal(null);
		avgValueField.setJustification(STextField.RIGHT);
		
		doLayout();
	}
	
	public void dispose() {
		contest.removeTotalChangeListener(totalChangeHandler);
		super.dispose();
	}
	
	private class TotalChangeHandler implements TotalChangeListener
	{
		final DecimalFormat avgFormatter = new DecimalFormat("#00");

		public void updateTotal(TotalChangeEvent evt) {
			int value = contest.getTotalWager(coderIdx);
			avgValueField.setVisible(value >= 0);
			if (value >= 0) {
				avgValueField.setText(avgFormatter.format(value));
			}
			doLayout();
		}
	}
}
