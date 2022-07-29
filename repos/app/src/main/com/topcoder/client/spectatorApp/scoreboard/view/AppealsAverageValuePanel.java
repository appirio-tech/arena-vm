/**
 * ChallengeTotalValuePanel.java Description: Total value panel to the
 * scoreboard rooms
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
package com.topcoder.client.spectatorApp.scoreboard.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.text.DecimalFormat;
import com.topcoder.client.spectatorApp.CommonRoutines;
import com.topcoder.client.spectatorApp.FontFactory;
import com.topcoder.client.spectatorApp.SpectatorApp;
import com.topcoder.client.spectatorApp.scoreboard.model.ComponentContest;
import com.topcoder.client.spectatorApp.scoreboard.model.PointDirection;
import com.topcoder.client.spectatorApp.views.AnimatePanel;
import com.topcoder.client.spectatorApp.views.DirectDrawPanel;

public class AppealsAverageValuePanel extends DirectDrawPanel implements AnimatePanel {
	/** Font used for the point value */
	private static final Font avgValueFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_BOLD, Font.PLAIN, 36);

	/** Font metrics for the font */
	private FontMetrics avgValueFM;

	/** The margin between arrow and total */
	private static final int marginArrowLeft = 5;

	/** The margin between arrow and side */
	private static final int marginArrowRight = 5;

	/** The maximum arrow width */
	private int arrowWidth;

	/** Reference to the component contest */
	private ComponentContest contest;

	/** Color used for normal text */
	private Color textColor = Color.white;
	
	/** Color used for increasing value */
	private Color increasingColor = Color.green;
	
	/** Color used for decreasing value */
	private Color decreasingColor = Color.red;
	
	/** Image of a red arrow */
	private Image upArrow;

	/** Image of a green arrow */
	private Image downArrow;

	/** Coder index (in the component contest) assigned to this object */
	private int coderIdx;

	/** Formatter for the total value */
	private static final DecimalFormat formatter = new DecimalFormat("##0.00");

	/** The prior average value */
	private int priorValue;

	/** Boolean indicating whether to draw or not (part of blinking) */
	private boolean drawAvg = true;

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
	public AppealsAverageValuePanel(ComponentContest contest, int coderID) {
		this(contest, coderID, contest.getAverageScore(contest.indexOfCoder(coderID)));
	}
	
	/** Constructs the user handle panel */
	public AppealsAverageValuePanel(ComponentContest contest, int coderID, int priorValue) {
		// Save the point value text
		this.contest = contest;
		this.coderIdx = contest.indexOfCoder(coderID);
		if (coderIdx < 0) {
			throw new IllegalArgumentException("Unknown coder id: " + coderID);
		}
		this.priorValue = priorValue;
		
		// Get the images
		upArrow = Toolkit.getDefaultToolkit().getImage(SpectatorApp.class.getResource("green_arrow.gif"));
		downArrow = Toolkit.getDefaultToolkit().getImage(SpectatorApp.class.getResource("red_arrow.gif"));
		
		// Load the images
		if (!CommonRoutines.loadImagesFully(new Image[] { upArrow, downArrow })) {
			throw new IllegalArgumentException("Error loading the up or down arrow images [green_arrow.gif, red_arrow.gif]");
		}
		
		// Get the font metrics
		avgValueFM = CommonRoutines.getFontMetrics(avgValueFont);
		
		// Calculate the height/width
		int height = avgValueFM.getAscent();
		arrowWidth = Math.max(upArrow.getWidth(null), downArrow.getWidth(null));
		int width = avgValueFM.stringWidth("####.##") + marginArrowLeft + arrowWidth + marginArrowRight; 
		
		// Vertical alignment is the bottom of the height
		setVerticalAlignment(height);
		super.setSize(width, height);
	}

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
				drawAvg = !drawAvg;
				lastBlink = now;
			}
		} else {
			// Draw the totals as is
			drawAvg = true;
		}
	}

	/**
	 * Overrides set size to do nothing
	 */
	//public void setSize(int width, int height) {}

	/**
	 * Paints the panel
	 * 
	 * @param g2D
	 *           the graphics to paint with
	 */
	public void drawImage(Graphics2D g2D) {
		// If no drawing need - return
		if (!drawAvg) return;
		
		// Get the total value
		int averageValue = contest.getAverageScore(coderIdx);
		if (averageValue < 0) return;
		
		// Setup antialiasing
		g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		// Is our total value different than last?
		if (averageValue != priorValue) {
			// Set the last time to blink (starts blinking!)
			lastStartBlink = CommonRoutines.getCurrentTime();
			// Save the total value
			priorValue = averageValue;
		}
		
		final PointDirection dir = contest.getTotalPointDirection(coderIdx);
		if (dir == PointDirection.DecreasingValue) {
			g2D.drawImage(downArrow, getWidth() - arrowWidth - marginArrowRight, getHeight() - downArrow.getHeight(null), null);
			g2D.setPaint(decreasingColor);
		} else if (dir == PointDirection.IncreasingValue) {
			g2D.drawImage(upArrow, getWidth() - arrowWidth - marginArrowRight, getHeight() - upArrow.getHeight(null), null);
			g2D.setPaint(increasingColor);
		} else {
			g2D.setPaint(textColor);
		}
		
		// Create a decimal formatter and format the total value
		String text = formatter.format((averageValue / 100.0));
		
		// Draw the text right justified and centered vertically
		g2D.setFont(avgValueFont);
		g2D.drawString(text, getWidth() - avgValueFM.stringWidth(text) - marginArrowLeft - arrowWidth - marginArrowRight, getVerticalAlignment());
	}
}
