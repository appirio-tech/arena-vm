/**
 * ChallengeHistoryPanel.java Description: Shows the history for a specific
 * coder
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
package com.topcoder.client.spectatorApp.scoreboard.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import com.topcoder.client.spectatorApp.CommonRoutines;
import com.topcoder.client.spectatorApp.FontFactory;
import com.topcoder.client.spectatorApp.scoreboard.model.CoderHistory;
import com.topcoder.client.spectatorApp.scoreboard.model.Location;
import com.topcoder.client.spectatorApp.scoreboard.model.ScoreboardCoderTracker;
import com.topcoder.client.spectatorApp.scoreboard.model.ScoreboardPointTracker;
import com.topcoder.client.spectatorApp.views.AnimatePanel;
import com.topcoder.client.spectatorApp.views.DirectDrawPanel;
import com.topcoder.shared.netCommon.messages.spectator.CoderRoomData;

public class ChallengeHistoryPanel extends DirectDrawPanel implements AnimatePanel {
	/** Number of columns */
	private static final int numCols = 4;

	/** Font used for the headers */
	private static final Font[] headerFonts = { FontFactory.getInstance().getFont(FontFactory.FUTURA_BOLD, Font.PLAIN, 12), FontFactory.getInstance().getFont(FontFactory.FUTURA_BOLD, Font.PLAIN, 12),
				FontFactory.getInstance().getFont(FontFactory.FUTURA_BOLD, Font.PLAIN, 12), FontFactory.getInstance().getFont(FontFactory.FUTURA_BOLD, Font.PLAIN, 12) };

	/** Font used for the text */
	private static final Font[] textFont = { FontFactory.getInstance().getFont(FontFactory.FUTURA_BOOK, Font.PLAIN, 14), FontFactory.getInstance().getFont(FontFactory.FUTURA_BOOK, Font.PLAIN, 14),
				FontFactory.getInstance().getFont(FontFactory.FUTURA_BOLD, Font.PLAIN, 14), FontFactory.getInstance().getFont(FontFactory.FUTURA_BOOK, Font.PLAIN, 14) };

	/** Font metrics for the header fonts */
	private FontMetrics[] headerFontFM = new FontMetrics[numCols];

	/** Font metrics for the text fonts */
	private FontMetrics[] textFontFM = new FontMetrics[numCols];

	/** Header strings */
	private static final String[] headers = { "CODER", "PROBLEM", "ACTION", "DECISION" };

	/** Problem text */
	private String[] probDesc;

	/** String representing viewing */
	private static final String viewing = "viewing";

	/** String representing challenging */
	private static final String challenging = "challenged";

	/** Row (in the pointTracker) assigned to this object */
	private int row;

	/** The maximum number of history items to show */
	private int maxHistory = 6;

	/** The header insets */
	private static final Insets headerInsets = new Insets(3, 3, 3, 3);

	/** Reference to the scoreboard point tracker */
	private ScoreboardPointTracker pointTracker;

	/** Reference to the scoreboard coder tracker */
	private ScoreboardCoderTracker coderTracker;

	/** Margin between cols */
	private static final int marginBetweenCols = 5;

	/** Margin between header/text */
	private static final int marginBetween = 5;

	/** The calculated column width */
	private int columnWidth = 0;

	/** The calculated header height */
	private int headerHeight = 0;

	/** Temporary stringbuffer */
	private StringBuffer str = new StringBuffer(10);

	/** Temporary ArrayList for history */
	private ArrayList<CoderHistory> history = new ArrayList<CoderHistory>();

	/** Constructs the user handle panel */
	public ChallengeHistoryPanel(ScoreboardPointTracker pointTracker, ScoreboardCoderTracker coderTracker, int coderID, int maxHistory) {
		// Save the point value text
		this.pointTracker = pointTracker;
		this.coderTracker = coderTracker;
		this.row = pointTracker.indexOfCoder(coderID);
		if (row < 0) {
			throw new IllegalArgumentException("Unknown coder id: " + coderID);
		}
		this.maxHistory = maxHistory;
		
		// Get the font metrics
		for (int x = numCols - 1; x >= 0; x--) {
			headerFontFM[x] = CommonRoutines.getFontMetrics(headerFonts[x]);
			textFontFM[x] = CommonRoutines.getFontMetrics(textFont[x]);
		}
		int[] colWidths = new int[numCols];
		
		// Create the problem descriptions
		probDesc = new String[pointTracker.getProblemCount()];
		for (int x = 1; x <= probDesc.length; x++) {
			probDesc[x - 1] = "LEVEL " + x;
			colWidths[1] = Math.max(colWidths[1], textFontFM[1].stringWidth(probDesc[x - 1]));
		}
		
		// Calculate the biggest coder
		java.util.List<CoderRoomData> coders = pointTracker.getCoders();
		for (int x = coders.size() - 1; x >= 0; x--) {
			colWidths[0] = Math.max(colWidths[0], textFontFM[0].stringWidth(coders.get(x).getHandle()));
		}
		
		// Calculate the biggest action
		colWidths[2] = Math.max(textFontFM[2].stringWidth(viewing), textFontFM[2].stringWidth(challenging));
		
		// Calculate the biggest width for the challenge value (assume 999)
		colWidths[3] = textFontFM[3].stringWidth("+999");
		
		// Calulate the maximum widht of the text
		int maxTextWidth = Math.max(colWidths[0], Math.max(colWidths[1], Math.max(colWidths[2], colWidths[3])));
		
		// Calculate the header width/height
		int maxHeaderWidth = 0;
		int maxHeaderHeight = 0;
		for (int x = numCols - 1; x >= 0; x--) {
			maxHeaderWidth = Math.max(maxHeaderWidth, (1 + headerInsets.left + headerFontFM[x].stringWidth(headers[x]) + headerInsets.right + 1));
			maxHeaderHeight = Math.max(maxHeaderHeight, 1 + headerInsets.top + headerFontFM[x].getAscent() + headerInsets.bottom + 1);
		}
		
		// Calculate the maximum text height
		int maxTextHeight = 0;
		for (int x = numCols - 1; x >= 0; x--)
			maxTextHeight = Math.max(maxTextHeight, textFontFM[x].getHeight());
		
		// Calculate the width/height
		columnWidth = Math.max(maxHeaderWidth, maxTextWidth);
		headerHeight = maxHeaderHeight;
		int width = (columnWidth * numCols) + (marginBetweenCols * (numCols - 1));
		int height = maxHeaderHeight + marginBetween + (maxTextHeight * maxHistory);
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
		// Setup antialiasing
		g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		// Get the History (copy of)
		history.clear();
		history.addAll(pointTracker.getHistory(row));
		
		// Add a viewing to the history if the coder is viewing something
		Location loc = coderTracker.findCoder(pointTracker.getCoder(row).getHandle());
		if (loc != null) history.add(new CoderHistory(CoderHistory.OPENED, loc.getCol(), pointTracker.getCoder(loc.getRow()).getHandle(), 0));
		
		// Draw the the headers
		for (int x = 0; x < numCols; x++) {
			// Save the top of the column position
			AffineTransform colTop = g2D.getTransform();
			
			// Draw the box
			g2D.setPaint(Color.white);
			g2D.drawRect(0, 0, columnWidth, headerHeight);
			
			// Draw the header text
			g2D.setFont(headerFonts[x]);
			g2D.drawString(headers[x], (columnWidth / 2) - (headerFontFM[x].stringWidth(headers[x]) / 2), (headerHeight / 2) + (headerFontFM[x].getAscent() / 2));
			
			// translate down to the history
			g2D.translate(0, headerHeight + marginBetween + textFontFM[x].getAscent());
			
			// Set the font
			g2D.setFont(textFont[x]);
			
			// Draw the history for th ecolumn
			for (int h = history.size() - 1; h >= 0 && h >= history.size() - maxHistory; h--) {
				// Get the history
				CoderHistory hist = history.get(h);
				
				// Decide what to draw
				switch (x) {
				// Draw the coder name left justified
				case 0:
					g2D.setPaint(Color.white);
					g2D.drawString(hist.getCoderHandle(), 0, 0);
					break;
				// Draw the problem description centered
				case 1:
					g2D.setPaint(Color.white);
					g2D.drawString(probDesc[hist.getColumn()], (columnWidth / 2) - (textFontFM[x].stringWidth(probDesc[hist.getColumn()]) / 2), 0);
					break;
				// Draw the viewing/challenge text left justified
				case 2:
					if (hist.getType() == CoderHistory.OPENED) {
						g2D.setPaint(Color.yellow);
						g2D.drawString(viewing, 0, 0);
					} else {
						if (hist.getPoints() < 0) {
							g2D.setPaint(Color.red);
						} else {
							g2D.setPaint(Color.green);
						}
						g2D.drawString(challenging, 0, 0);
					}
					break;
				// Draw the points centered
				case 3:
					if (hist.getType() == CoderHistory.OPENED) {
						g2D.setPaint(Color.yellow);
						g2D.drawString("-", (columnWidth / 2) - (textFontFM[x].stringWidth("-") / 2), 0);
					} else {
						// Clear our string buffer
						str.setLength(0);
						// Good or bad challenge
						if (hist.getPoints() < 0) {
							g2D.setPaint(Color.red);
							str.append("-");
						} else {
							g2D.setPaint(Color.green);
							str.append("+");
						}
						str.append((int) (hist.getPoints() / 100));
						g2D.drawString(str.toString(), (columnWidth / 2) - (textFontFM[x].stringWidth(str.toString()) / 2), 0);
					}
					break;
				}
				// Translate down to the next one
				g2D.translate(0, textFontFM[x].getHeight());
			}
			// Translate over to the next column - top
			g2D.setTransform(colTop);
			g2D.translate(columnWidth + marginBetweenCols, 0);
		}
	}
}
