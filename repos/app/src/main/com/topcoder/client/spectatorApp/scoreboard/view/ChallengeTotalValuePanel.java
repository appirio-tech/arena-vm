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
import com.topcoder.client.spectatorApp.scoreboard.model.PointDirection;
import com.topcoder.client.spectatorApp.scoreboard.model.ScoreboardPointTracker;
import com.topcoder.client.spectatorApp.views.AnimatePanel;
import com.topcoder.client.spectatorApp.views.DirectDrawPanel;

public class ChallengeTotalValuePanel extends DirectDrawPanel implements AnimatePanel {
	/** Font used for the point value */
	private static final Font totalValueFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_HEAVY, Font.PLAIN, 30);

	/** Font metrics for the font */
	private FontMetrics totalValueFM;

	/** The margin between arrow and total */
	private static final int marginArrow = 5;

	/** The maximum arrow width */
	private int arrowWidth;

	/** Reference to the point tracker */
	private ScoreboardPointTracker pointTracker;

	/** Image of a red arrow */
	private Image upArrow;

	/** Image of a green arrow */
	private Image downArrow;

	/** Row (in the pointTracker) assigned to this object */
	private int row;

	/** Formatter for the total value */
	private static final DecimalFormat formatter = new DecimalFormat("###0.00");

	/** String holding the previous text */
	private int priorValue;

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
	public ChallengeTotalValuePanel(ScoreboardPointTracker pointTracker, int coderID) {
		this(pointTracker, coderID, pointTracker.getTotalScore(pointTracker.indexOfCoder(coderID)));
	}
	
	/** Constructs the user handle panel */
	public ChallengeTotalValuePanel(ScoreboardPointTracker pointTracker, int coderID, int priorValue) {
		// Save the point value text
		this.pointTracker = pointTracker;
		this.row = pointTracker.indexOfCoder(coderID);
		if (row < 0) {
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
		totalValueFM = CommonRoutines.getFontMetrics(totalValueFont);
		
		// Calculate the height/width
		int height = totalValueFM.getAscent();
		arrowWidth = Math.max(upArrow.getWidth(null), downArrow.getWidth(null));
		int width = arrowWidth + marginArrow + totalValueFM.stringWidth("####.##");
		
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
				drawTotal = !drawTotal;
				lastBlink = now;
			}
		} else {
			// Draw the totals as is
			drawTotal = true;
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
		if (!drawTotal) return;
		
		// Get the total value
		int totalValue = pointTracker.getTotalScore(row);
		
		// Is our total value different than last?
		if (totalValue != priorValue) {
			// Set the last time to blink (starts blinking!)
			lastStartBlink = CommonRoutines.getCurrentTime();
			// Save the total value
			priorValue = totalValue;
		}
		
		// Create a decimal formatter and format the total value
		String text = formatter.format((totalValue / 100.0));
		
		// Setup antialiasing
		g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		
		PointDirection dir = pointTracker.getTotalValueDirection(row); 
		if (dir == PointDirection.NoChange ) {
			g2D.setPaint(Color.white);
		} else if (dir == PointDirection.IncreasingValue) {
			g2D.drawImage(upArrow, 0, getHeight() - upArrow.getHeight(null), null);
			g2D.setPaint(Color.green);
		} else if (dir == PointDirection.DecreasingValue) {
			g2D.drawImage(downArrow, 0, getHeight() - downArrow.getHeight(null), null);
			g2D.setPaint(Color.red);
		} else {
			g2D.setPaint(Color.white);
		}
		
		// Draw the text right justified and centered vertically
		g2D.setFont(totalValueFont);
		g2D.drawString(text, getWidth() - totalValueFM.stringWidth(text), getVerticalAlignment());
	}
}
