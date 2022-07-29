package com.topcoder.client.spectatorApp.scoreboard.view;

import java.awt.Color;
import com.topcoder.client.spectatorApp.scoreboard.model.ComponentContest;
import com.topcoder.client.spectatorApp.scoreboard.model.TotalChangeEvent;
import com.topcoder.client.spectatorApp.scoreboard.model.TotalChangeListener;

public class ComponentWagerValue extends AbstractComponentValue {
	/** Reference to the component contest */
	private ComponentContest contest;

	/** Coder index (in the component contest) assigned to this object */
	private int coderID;

	/** The total change handler */
	private TotalChangeHandler totalChangeHandler = new TotalChangeHandler();
	
	public ComponentWagerValue(ComponentContest contest, int coderID) {
		
		this.contest = contest;
		this.coderID = coderID;
		
		final int idx = contest.indexOfCoder(coderID);
		if (idx < 0) {
			throw new IllegalArgumentException("Unknown coder id: " + coderID);
		}

		avgValueField.setText(String.valueOf(contest.getCoder(idx).getWager()));
		avgValueField.setColor(Color.YELLOW);
		placementField.setVisible(false);
		contest.addTotalChangeListener(totalChangeHandler);
		
		int value = contest.getAverageScore(contest.indexOfCoder(coderID));
		avgValueField.setVisible(value >= 0);
	}
	
	public void dispose() {
		contest.removeTotalChangeListener(totalChangeHandler);
		super.dispose();
	}
	
	private class TotalChangeHandler implements TotalChangeListener
	{
		public void updateTotal(TotalChangeEvent evt) {
			int value = contest.getAverageScore(contest.indexOfCoder(coderID));
			avgValueField.setVisible(value >= 0);
		}
	}
}
