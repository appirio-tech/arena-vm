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
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import com.topcoder.client.spectatorApp.CommonRoutines;
import com.topcoder.client.spectatorApp.FontFactory;
import com.topcoder.client.spectatorApp.SpectatorApp;
import com.topcoder.client.spectatorApp.views.AnimatePanel;
import com.topcoder.client.spectatorApp.views.DirectDrawPanel;

public class ResultTableCell extends DirectDrawPanel implements AnimatePanel {
	/** Font used for the point value */
	private static final Font totalValueFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_BOLDOBLIQUE, Font.PLAIN, 48);

	/** The preferred vertical previous alignment */
	private int verticalPreviousAlignment;

	/** The margin between total value and previous total */
	private static final int marginPrevious = 5;

	/** THe content assigned to this object */
	private final String content;

	/** Color used for the total */
	private static final Color textColor = Color.white;

	/** System Testing Image */
	private Image systemTestingImage;

	/** Point where system testing image should be drawn */
	private Point systemTestingPoint;

	/** Bounds of the total value */
	private Rectangle totalBounds;
	
	/** Constructs the user handle panel */
	public ResultTableCell(String content) {
		// Save the point value text
		this.content = content;

		totalBounds = CommonRoutines.getIntRectangle(new TextLayout("9999.99", totalValueFont, new FontRenderContext(null, true, false)));
		systemTestingImage = Toolkit.getDefaultToolkit().getImage(SpectatorApp.class.getResource("hourglass.gif"));
		systemTestingPoint = new Point((totalBounds.width / 2) - (systemTestingImage.getWidth(null) / 2), (totalBounds.height / 2) - (systemTestingImage.getHeight(null) / 2));
		
		// Calculate the vertical alignment of the text
		setVerticalAlignment(totalBounds.height);
		super.setSize(totalBounds.width, totalBounds.height);
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
		String text = content;
		
		if ("$".equals(text)) {
            g2D.drawImage(systemTestingImage, systemTestingPoint.x, systemTestingPoint.y, null);
        } else {
            // Draw the text right justified and centered vertically
            g2D.setPaint(textColor);
            g2D.setFont(totalValueFont);
            int textWidth = g2D.getFontMetrics().stringWidth(text);
            g2D.drawString(text, getWidth() - textWidth, getVerticalAlignment());
        }
	}
}
