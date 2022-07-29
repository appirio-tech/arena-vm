package com.topcoder.client.spectatorApp.widgets;

import java.awt.Font;
import java.text.DecimalFormat;
import com.topcoder.client.spectatorApp.scoreboard.model.ScoreboardPointTracker;

/**
 * SPointTrackingTextField Created on Mar 14, 2004
 * 
 * @author Tim Roberts
 * @version 1.0
 */
public class STotalPointTrackingTextField extends STextField {
	/** The point tracker */
	private ScoreboardPointTracker pointTracker;

	/** The row we are tracking */
	private int row;

	/** Formatter for the value */
	private static final DecimalFormat formatter = new DecimalFormat("###0.00");

	/** Last known value */
	private int lastKnownValue = -1;

	/**
	 * Constructs with a default font
	 * 
	 * @param pointTracker
	 *           the point tracker
	 * @param row
	 *           the row
	 * @param col
	 *           the col
	 */
	public STotalPointTrackingTextField(ScoreboardPointTracker pointTracker, int coderID) {
		super("9999.99"); // prototype value to set width properly
		this.pointTracker = pointTracker;
		this.row = pointTracker.indexOfCoder(coderID);
		if (row < 0) {
			throw new IllegalArgumentException("Unknown coder id: " + coderID);
		}
	}

	/**
	 * Constructs
	 * 
	 * @param pointTracker
	 *           the point tracker
	 * @param row
	 *           the row
	 * @param col
	 *           the col
	 * @param font
	 *           the font to use
	 */
	public STotalPointTrackingTextField(ScoreboardPointTracker pointTracker, int coderID, Font font) {
		super("9999.99", font); // prototype value to set width properly
		this.pointTracker = pointTracker;
		this.row = pointTracker.indexOfCoder(coderID);
		if (row < 0) {
			throw new IllegalArgumentException("Unknown coder id: " + coderID);
		}
	}

	/** Overridden to set value if it changed */
	public void animate(long now, long diff) {
		updateValue();
	}

	/** Overridden to resize with the latest value */
	public void resize() {
		updateValue();
		super.resize();
	}

	/** Updates the value of the text field */
	private void updateValue() {
		if (pointTracker == null) return;
		int value = pointTracker.getTotalScore(row);
		if (lastKnownValue != value) {
			lastKnownValue = value;
			setText(getCurrentValue(value));
		}
	}

	/** Returns the current formatted value */
	private String getCurrentValue(int value) {
		return formatter.format(value / 100.00);
	}
}
