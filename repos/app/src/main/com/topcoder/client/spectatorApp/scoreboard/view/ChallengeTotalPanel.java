/**
 * ChallengeValuePanel.java Description: Point value panel to the scoreboard
 * rooms
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
package com.topcoder.client.spectatorApp.scoreboard.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.text.DecimalFormat;
import com.topcoder.client.spectatorApp.CommonRoutines;
import com.topcoder.client.spectatorApp.FontFactory;
import com.topcoder.client.spectatorApp.scoreboard.model.ScoreboardPointTracker;
import com.topcoder.client.spectatorApp.views.AnimatePanel;
import com.topcoder.client.spectatorApp.views.DirectDrawPanel;

public class ChallengeTotalPanel extends DirectDrawPanel implements AnimatePanel {
	/** Font used for the points */
	private static final Font pointFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_BOLD, Font.PLAIN, 30);

	/** Font metrics for the point font */
	private FontMetrics pointFontFM;

	/** Row (in the pointTracker) assigned to this object */
	private int row;

	/** Color used for the viewing */
	private static final Color positiveColor = Color.green;

	/** Color used for the not viewing */
	private static final Color noChangeColor = Color.white;

	/** Color used for the text */
	private static final Color negativeColor = Color.red;

	/** Reference to the scoreboard point tracker */
	private ScoreboardPointTracker pointTracker;

	/** Formatter for the total value */
	private static final DecimalFormat formatter = new DecimalFormat("###0.00");

	/** The prior value */
	public int priorValue;
	
	/** Boolean indicating whether to draw or not (part of blinking) */
	private boolean drawTotal = true;

	/** Length of time to blink (in milli seconds) */
	private static final long blinkLen = CommonRoutines.calcTimePerSecond(1.5); // one and a half seconds

	/**
	 * Miminmum amount of time that needs to pass to change blinks (in milli
	 * seconds)
	 */
	private static final long minBlink = CommonRoutines.calcTimePerSecond(.25); // quarter of a second

	/** Last time we started to blinked */
	private long lastStartBlink = -1;

	/** Last time we blinked */
	private long lastBlink = -1;


	/** Constructs the user handle panel */
	public ChallengeTotalPanel(ScoreboardPointTracker pointTracker, int coderID) {
		// Save the point value text
		this.pointTracker = pointTracker;
		this.row = pointTracker.indexOfCoder(coderID);
		if (row < 0) {
			throw new IllegalArgumentException("Unknown coder id: " + coderID);
		}
		
		// Get the font metrics
		pointFontFM = CommonRoutines.getFontMetrics(pointFont);
				
		// Set the size
		super.setSize(pointFontFM.stringWidth(formatter.format(9999.99)), pointFontFM.getAscent());
	
		// Get the prior value
		priorValue = pointTracker.getChallengeValue(row);
		
		// Set the vertical alighment to the ascent
		setVerticalAlignment(pointFontFM.getAscent());
	}

	/**
	 * Overrides set size to do nothing
	 */
	//public void setSize(int width, int height) {}

	/**
	 * Provides 'blinking' of the score when changed
	 */
	public void animate(long now, long diff) {
		// If we are beyond our blink time
		if (now - lastStartBlink <= blinkLen) {
			// Has the minimal amount of time passed to change the status of the
			// blink
			if (now - lastBlink >= minBlink) {
				// Reverse the blink
				drawTotal = !drawTotal;
				lastBlink = now;
			}
		} else {
			// Draw the totals as is
			drawTotal = true;
		}
	}

	/**
	 * Paints the panel
	 * 
	 * @param g2D
	 *           the graphics to paint with
	 * @returns the area that is volatile
	 */
	public void drawImage(Graphics2D g2D) {
		// If no drawing need - return
		if (!drawTotal) return;
		
		// Save the transform
		AffineTransform savedTransform = g2D.getTransform();
		
		// Turn off antialiasing
		g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		// Format the point total
		int pointValue = pointTracker.getChallengeValue(row);
		if (priorValue != pointValue) {
			// Set the last time to blink (starts blinking!)
			lastStartBlink = CommonRoutines.getCurrentTime();
			
			// Save the total value
			priorValue = pointValue;

		}
		
		String pointValueText = formatter.format(pointValue / 100.0);
		
		// Set the color based on the direction
		if (pointValue == 0) {
			g2D.setPaint(noChangeColor);
		} else if (pointValue < 0) {
			g2D.setPaint(negativeColor);
		} else {
			g2D.setPaint(positiveColor);
		}
		
		// Translate down to where the point should be drawn and draw it
		g2D.setFont(pointFont);
		g2D.translate(0, g2D.getFontMetrics().getAscent());
		g2D.drawString(pointValueText, getWidth() - g2D.getFontMetrics().stringWidth(pointValueText), 0);
		
		g2D.setTransform(savedTransform);
	}
}
