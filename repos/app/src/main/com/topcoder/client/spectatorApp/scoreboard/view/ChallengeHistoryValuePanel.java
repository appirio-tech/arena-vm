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
import com.topcoder.client.spectatorApp.scoreboard.model.ScoreboardCoderTracker;
import com.topcoder.client.spectatorApp.scoreboard.model.ScoreboardPointTracker;
import com.topcoder.client.spectatorApp.views.AnimatePanel;
import com.topcoder.client.spectatorApp.views.DirectDrawPanel;

public class ChallengeHistoryValuePanel extends DirectDrawPanel implements AnimatePanel {
	/** Font used for the titles */
	private static final Font titleFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_BOOK, Font.PLAIN, 18);

	/** Font used for the title */
	private static final Font textFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_BOOK, Font.PLAIN, 14);

	/** Font used for the points */
	private static final Font pointFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_BOLD, Font.PLAIN, 30);

	/** Font metrics for the title font */
	private FontMetrics titleFontFM;

	/** Font metrics for the text font */
	private FontMetrics textFontFM;

	/** Font metrics for the point font */
	private FontMetrics pointFontFM;

	/** Row (in the pointTracker) assigned to this object */
	private int row;

	/** Column (in the pointTracker) assigned to this object */
	private int col;

	/** The maximum number of people viewing it */
	private int maxViewing = 4;

	/** Margin between the point value and the viewing */
	private static final int marginBetween = 5;

	/** Color used for the viewing */
	private static final Color viewingColor = Color.yellow;

	/** Color used for the not viewing */
	private static final Color noViewingColor = Color.white;

	/** Color used for the text */
	private static final Color challengeColor = Color.red;

	/** Rectangle title background color */
	private static final Color textColor = Color.white;

	/** Viewing title */
	private static final String viewingTitle = "viewing:";

	/** Challenger title */
	private static final String challengerTitle = "challenger:";

	/** Reference to the scoreboard point tracker */
	private ScoreboardPointTracker pointTracker;

	/** Reference to the scoreboard point tracker */
	private ScoreboardCoderTracker coderTracker;

	/** Formatter for the total value */
	private static final DecimalFormat formatter = new DecimalFormat("###0.00");

	/** Constructs the user handle panel */
	public ChallengeHistoryValuePanel(ScoreboardCoderTracker coderTracker, ScoreboardPointTracker pointTracker, int coderID, int problemID, int maxViewing) {
		// Save the point value text
		this.pointTracker = pointTracker;
		this.coderTracker = coderTracker;
		this.row = pointTracker.indexOfCoder(coderID);
		if (row < 0) {
			throw new IllegalArgumentException("Unknown coder id: " + coderID);
		}
		this.col = pointTracker.indexOfProblem(problemID);
		if (col < 0) {
			throw new IllegalArgumentException("Unknown problem id: " + problemID);
		}
		
		this.maxViewing = maxViewing;
		
		// Get the font metrics
		titleFontFM = CommonRoutines.getFontMetrics(titleFont);
		textFontFM = CommonRoutines.getFontMetrics(textFont);
		pointFontFM = CommonRoutines.getFontMetrics(pointFont);
		
		// Figure out the maximum width of the coder names
		int coderWidth = 0;
		for (int x = pointTracker.getCoderCount() - 1; x >= 0; x--) {
			coderWidth = Math.max(textFontFM.stringWidth(pointTracker.getCoder(x).getHandle()), coderWidth);
		}
		
		// Calculate the height
		int height = pointFontFM.getAscent() + marginBetween + titleFontFM.getHeight() + (textFontFM.getHeight() * maxViewing);
		
		// Calculate the width
		int width = Math.max(coderWidth, Math.max(titleFontFM.stringWidth(viewingTitle), titleFontFM.stringWidth(challengerTitle)));
		width = Math.max(width, pointFontFM.stringWidth(formatter.format(9999.99)));
		super.setSize(width, height);
	}

	/**
	 * Overrides set size to do nothing
	 */
	public void setSize(int width, int height) {}

	/**
	 * Paints the panel
	 * 
	 * @param g2D
	 *           the graphics to paint with
	 * @returns the area that is volatile
	 */
	public void drawImage(Graphics2D g2D) {
		// Save the transform
		AffineTransform savedTransform = g2D.getTransform();
		
		// Turn off antialiasing
		g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		// Get the coders viewing the problem
		java.util.List coders = coderTracker.getCoders(row, col);
		String challengerName = pointTracker.getChallengerName(row, col);
		
		// Was there a successful challenger
		if (challengerName == null) {
			// If not - is any one viewing the problem?
			if (coders.size() > 0) {
				g2D.setPaint(viewingColor);
			} else {
				g2D.setPaint(noViewingColor);
			}
		} else {
			g2D.setPaint(challengeColor);
		}
		
		// Format the point total
		double pointValue = (double) pointTracker.getPointValue(row, col) / 100.0;
		String pointValueText = formatter.format(pointValue);
		
		// Translate down to where the point should be drawn and draw it
		g2D.translate(0, pointFontFM.getAscent());
		g2D.setFont(pointFont);
		g2D.drawString(pointValueText, getWidth() - pointFontFM.stringWidth(pointValueText), 0);
		
		// Translate down past the margin and draw the title
		g2D.translate(0, marginBetween);
		
		// Only show label if someone is viewing or it's been challenged
		if (coders.size() != 0 || challengerName != null) {
			g2D.setPaint(textColor);
			g2D.setFont(titleFont);
			g2D.drawString(challengerName == null ? viewingTitle : challengerTitle, 0, titleFontFM.getAscent());
		}
		// Translate to the first line should appear
		g2D.translate(0, titleFontFM.getHeight() + textFontFM.getAscent());
		g2D.setFont(textFont);
		
		// Succesfully challenged?
		if (challengerName == null) {
			// If not, draw all the people that are viewing the problem
			for (int x = 0; x < Math.min(maxViewing, coders.size()); x++) {
				g2D.drawString((String) coders.get(x), 0, 0);
				g2D.translate(0, textFontFM.getHeight());
			}
		} else {
			// If so, draw the challenger name and end
			g2D.drawString(challengerName, 0, 0);
		}
		g2D.setTransform(savedTransform);
	}
}
