/**
 * PointValuePanel.java
 *
 * Description:		Point value panel to the scoreboard rooms
 * @author			Tim "Pops" Roberts
 * @version			1.0
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
import com.topcoder.client.spectatorApp.Constants.AppealStatus;
import com.topcoder.client.spectatorApp.scoreboard.model.ComponentContest;
import com.topcoder.client.spectatorApp.views.AnimatePanel;
import com.topcoder.client.spectatorApp.views.DirectDrawPanel;

public class AppealsPointValuePanel extends DirectDrawPanel implements AnimatePanel {

	/** Font used for the points */
	private static final Font pointFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_BOLD, Font.PLAIN, 36); //was 36
        
        private static final Font appealCountFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_BOLD, Font.PLAIN, 14); //was 36

	/** Font metrics for the point font */
	private FontMetrics pointFontFM;

	/** Coder index (in the contest) assigned to this object */
	private int coderIdx;

	/** Reviewer index (in the contest) assigned to this object */
	private int reviewerIdx;

	/** Color used for the regular point*/
	private static final Color textColor = Color.white;

	/** Color used for the appeal pending */
	private static final Color pendingColor = Color.yellow;

	/** Color used for the appeal failed */
	private static final Color failureColor = Color.red;

	/** Color used for the appeal successful */
	private static final Color successfulColor = Color.green;

	/** Reference to the component contest */
	private ComponentContest contest;

	/** Formatter for the total value */
	private static final DecimalFormat formatter = new DecimalFormat("##0.00");

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

	/** The prior value */
	public int priorValue;
	
	/** The prior appeal status*/
	public AppealStatus priorAppeal;
	
	/** Constructs the user handle panel */
	public AppealsPointValuePanel(ComponentContest contest, int coderID, int reviewerID) {
		// Save the point value text
		this.contest = contest;
		this.coderIdx = contest.indexOfCoder(coderID);
		if (coderIdx < 0) {
			throw new IllegalArgumentException("Unknown coder id: " + coderID);
		}
		
		this.reviewerIdx = contest.indexOfReviewer(reviewerID);
		if (reviewerIdx < 0) {
			throw new IllegalArgumentException("Unknown reviewer id: " + reviewerID);
		}
		
		// Get the font metrics
		pointFontFM = CommonRoutines.getFontMetrics(pointFont);
				
		// Set the size
		super.setSize(pointFontFM.stringWidth(formatter.format(9999.99)), pointFontFM.getAscent());
	
		// Set the vertical alighment to the ascent
		setVerticalAlignment(pointFontFM.getAscent());
		
		// Get the prior value
		priorValue = contest.getScore(coderIdx, reviewerIdx);
		priorAppeal = contest.getAppealStatus(coderIdx, reviewerIdx);
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
		//if (!drawTotal) return;
		
		// Format the point total
		final int pointValue = contest.getScore(coderIdx, reviewerIdx);
		if (pointValue < 0) return;
		
		// Save the transform
		AffineTransform savedTransform = g2D.getTransform();
		
		// Turn off antialiasing
		g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		final AppealStatus status = contest.getAppealStatus(coderIdx, reviewerIdx);
		if (priorValue != pointValue || priorAppeal != status) {
			// Set the last time to blink (starts blinking!)
			lastStartBlink = CommonRoutines.getCurrentTime();
			
			// Save the total value
			priorValue = pointValue;
			priorAppeal = status;

		}
		
		String pointValueText = formatter.format(pointValue / 100.0);
		
		// Set the color based on the direction
		if (status == AppealStatus.None) {
			g2D.setPaint(textColor);
		} else if (status == AppealStatus.Pending) {
			g2D.setPaint(pendingColor);
		} else if (status == AppealStatus.Failed) {
			g2D.setPaint(failureColor);
		} else if (status == AppealStatus.Successful) {
			g2D.setPaint(successfulColor);
		}
		
		// Translate down to where the point should be drawn and draw it
		g2D.setFont(pointFont);
		g2D.translate(0, g2D.getFontMetrics().getAscent());
                if(drawTotal)
                    g2D.drawString(pointValueText, getWidth() - g2D.getFontMetrics().stringWidth(pointValueText), 0);
		
                //pending appeals values
                g2D.setFont(appealCountFont);
                g2D.setPaint(pendingColor);
                g2D.translate(0, g2D.getFontMetrics().getAscent());
                
                int baseOffset = 0;
                int offset = 0;
                
                String pendingValueText = "P: " + String.valueOf(contest.getPendingAppealCount(coderIdx, reviewerIdx)) + "   ";
                String successfulValueText = "S: " + String.valueOf(contest.getSuccessfulAppealCount(coderIdx, reviewerIdx)) + "   ";
                String failedValueText = "F: " + String.valueOf(contest.getFailedAppealCount(coderIdx, reviewerIdx));
                
                baseOffset = (getWidth() - g2D.getFontMetrics().stringWidth(pendingValueText + successfulValueText + failedValueText));
                offset = baseOffset;
                
                g2D.drawString(pendingValueText, offset, 0);
                
                offset += g2D.getFontMetrics().stringWidth(pendingValueText);
                
                g2D.setPaint(successfulColor);
                
                g2D.drawString(successfulValueText, offset, 0);
                offset += g2D.getFontMetrics().stringWidth(successfulValueText);

                g2D.setPaint(failureColor);
                
                g2D.drawString(failedValueText, offset, 0);
                
		g2D.setTransform(savedTransform);
	}
}
