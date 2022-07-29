/**
 * TotalValuePanel.java Description: Total value panel to the scoreboard rooms
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
package com.topcoder.client.spectatorApp.scoreboard.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.text.DecimalFormat;
import com.topcoder.client.spectatorApp.CommonRoutines;
import com.topcoder.client.spectatorApp.FontFactory;
import com.topcoder.client.spectatorApp.scoreboard.model.ScoreboardPointTracker;
import com.topcoder.client.spectatorApp.views.AnimatePanel;
import com.topcoder.client.spectatorApp.views.DirectDrawPanel;

public class TotalValuePanel extends DirectDrawPanel implements AnimatePanel {
	/** Font used for the point value */
	private static final Font totalValueFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_BOLDOBLIQUE, Font.PLAIN, 48);

	/** Font used for the previous point value */
	private static final Font previousValueFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_BOLDOBLIQUE, Font.PLAIN, 20);

	/** The preferred vertical previous alignment */
	private int verticalPreviousAlignment;

	/** The margin between total value and previous total */
	private static final int marginPrevious = 5;

	/** Reference to the point tracker */
	private ScoreboardPointTracker pointTracker;

	/** Row (in the pointTracker) assigned to this object */
	private int row;

	/** Color used for the total */
	private static final Color textColor = Color.white;

	/** Formatter for the total value */
	private static final DecimalFormat formatter = new DecimalFormat("###0.00");

	/** String holding the previous text */
	private String previousText;

	/** Bounds of the total value */
	private Rectangle totalBounds;
	
	/** Bounds of the prev value */
	private Rectangle prevBounds;
	
	/** Constructs the user handle panel */
	public TotalValuePanel(ScoreboardPointTracker pointTracker, int coderID) {
		// Save the point value text
		this.pointTracker = pointTracker;
		this.row = pointTracker.indexOfCoder(coderID);
		if(this.row < 0) {
			throw new IllegalArgumentException("Unknown coder id: " + coderID);
		}

		totalBounds = CommonRoutines.getIntRectangle(new TextLayout("9999.99", totalValueFont, new FontRenderContext(null, true, false)));
		
		// Calculate the vertical alignment of the text
		setVerticalAlignment(totalBounds.height);
		super.setSize(totalBounds.width, totalBounds.height);
	}

	/** Constructs the user handle panel */
	public TotalValuePanel(ScoreboardPointTracker pointTracker, int coderID, int priorValue) {
		// Default constructor
		this(pointTracker, coderID);
		
		// Set the prior value
		previousText = "PREV: " + formatter.format((priorValue / 100.0));
		prevBounds = CommonRoutines.getIntRectangle(new TextLayout(previousText, previousValueFont, new FontRenderContext(null, true, false)));
		
		// Figure out it's alignment
		verticalPreviousAlignment = getVerticalAlignment() + marginPrevious + prevBounds.height;
		
		// Recalculate the height/width
		int width = Math.max(getWidth(), prevBounds.width);
		int height = marginPrevious + prevBounds.height;
		
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
	 */
	public void drawImage(Graphics2D g2D) {
		// Setup antialiasing
		g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		// Create a decimal formatter and format the total value
		String text = formatter.format((pointTracker.getTotalScore(row) / 100.0));
		//Rectangle tb = CommonRoutines.getIntRectangle(new TextLayout(text, totalValueFont, g2D.getFontRenderContext()));
		
		// Draw the text right justified and centered vertically
		g2D.setPaint(textColor);
		g2D.setFont(totalValueFont);
		int textWidth = g2D.getFontMetrics().stringWidth(text);
//		g2D.drawString(text, getWidth() - tb.width, getVerticalAlignment());
		g2D.drawString(text, getWidth() - textWidth, getVerticalAlignment());
		
		// Only draw the previous total if specified
		if (previousText != null) {
			g2D.setFont(previousValueFont);
			g2D.drawString(previousText, getWidth() - prevBounds.width, verticalPreviousAlignment);
		}
	}
}
