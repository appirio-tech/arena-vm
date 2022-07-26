/**
 * UserHandlePanel.java Description: Header panel to the scoreboard rooms
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
import com.topcoder.client.spectatorApp.CommonRoutines;
import com.topcoder.client.spectatorApp.Constants;
import com.topcoder.client.spectatorApp.FontFactory;
import com.topcoder.client.spectatorApp.views.AnimatePanel;
import com.topcoder.client.spectatorApp.views.BufferedImagePanel;

public class UserHandlePanel extends BufferedImagePanel implements AnimatePanel {
	/** Font used for th user handles */
	private static final Font userHandleFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_BOOK, Font.PLAIN, 48);

	/** Length of the bracket top/bottom */
	private static final int bracketLen = 3;
	
	/** Minimum margin between user bracket and user handle (both sides) */
	private static final int marginUserBracket = 1;

	/** Minimum margin between user handle and the top/bottom sides */
	private static final int marginUserVerticleBox = 5;

	/** Maximum width of to allow the user handle to get */
	private static final int maxUserHandleWidth = 400;

	/** Color used for the user handle bracket */
	private static final Color userHandleBracketColor = Color.white;

	/** Coder information */
	private String handle;

	/** Coder rank */
	private int rank;

	/** Bounds of the handle text */
	private Rectangle textBounds;
	
	/**
	 * Constructs the user handle panel Since the user handle is a static image -
	 * the image is prebuilt in the constructor and simply painted each time.
	 */
	public UserHandlePanel(String handle, int seed, int rank) {
		// Save the coder information
		this.handle = handle;
		this.rank = rank;
		
		// Get the layout and create an (int) rectangle out of the bounds
		textBounds = CommonRoutines.getIntRectangle(new TextLayout(handle, userHandleFont, new FontRenderContext(null, true, false)));
		
		// Figure out the width
		int width = 2 + bracketLen * 2 + marginUserBracket * 2 + Math.min(maxUserHandleWidth, (int) Math.ceil(textBounds.getWidth()));
		
		// Figure out the height (ignore the leading on the user handle
		int height = textBounds.height + marginUserVerticleBox * 2;
		
		// Figure out the proper vertical alignment
		setVerticalAlignment((int) Math.ceil((height / 2d) + (textBounds.height / 2d)));
		
		// Create our back buffer
		super.setSize(width, height);
	}

	/**
	 * Overrides set size to do nothing
	 */
	public void setSize(int width, int height) {
		// Don't bother if the new width is larger than us
		if (width > getWidth()) return;
		
		// Recalculate the vertical alignment
		setVerticalAlignment((int) Math.ceil((height / 2d) + (textBounds.height / 2d)));
		
		// Do it for the new width
		super.setSize(width, height);
	}

	/**
	 * Creates the back buffer
	 */
	protected void drawImage(Graphics2D g2D) {
		// Setup antialiasing
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		// Do the user brackets
		int width = getWidth();
		int height = getHeight();
		
		g2D.setPaint(userHandleBracketColor);
		g2D.drawLine(1, 0, bracketLen, 1);
		g2D.drawLine(0, 0, 0, height - 1);
		g2D.drawLine(1, height - 1, bracketLen, height - 1);
		g2D.drawLine(width - bracketLen - 2, 0, width - 2, 0);
		g2D.drawLine(width - 1, 0, width - 1, height - 1);
		g2D.drawLine(width - bracketLen - 2, height - 1, width - 2, height - 1);
		
		// Right justify the user handle in the center (vertically) (clips name if
		// too large)
		g2D.setFont(userHandleFont);
		g2D.setPaint(Constants.getRankColor(rank));
		int x = width - 1 - bracketLen - marginUserBracket - textBounds.width;
		g2D.setClip(x, 0, width - 1 - bracketLen - marginUserBracket, getHeight());
		g2D.drawString(handle, x, getVerticalAlignment());
		
		if (getDebugColor() != null) {
			g2D.setPaint(getDebugColor());
			g2D.drawRect(0,0,width-1, height-1);
		}
	}
}
