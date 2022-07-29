package com.topcoder.client.spectatorApp.widgets;

import java.awt.Color;
import java.awt.Graphics2D;
import com.topcoder.client.spectatorApp.views.BufferedImagePanel;

/**
 * Draws a bracket in the given space
 * 
 * @author Tim Roberts
 * @version 1.0
 */
public class SBracket extends BufferedImagePanel {
	/** The color of the bracket */
	private Color color;

	/** Determines whether drawn on the left or right */
	private boolean left = true;

	/**
	 * Constructs a left bracket with a white color
	 */
	public SBracket() {
		this(true);
	}

	/**
	 * Constructs the bracket with a white color
	 * 
	 * @param left
	 *           true to have bracket drawn on left facing right (false the
	 *           reverse)
	 */
	public SBracket(boolean left) {
		this(left, Color.white);
	}

	/**
	 * Constructs the bracket with the given color
	 * 
	 * @param left
	 *           true to have bracket drawn on left facing right (false the
	 *           reverse)
	 * @param color
	 *           the color of the bracket
	 */
	public SBracket(boolean left, Color color) {
		this.left = left;
		this.color = color;
		setSize(5, 1);
	}

	public int getVerticalAlignment() {
		return getHeight() / 2;
	}

	protected void drawImage(Graphics2D g2D) {
		// Set the color
		g2D.setPaint(color);
		
		// Draw top bar
		g2D.drawLine(0, 0, getWidth() - 1, 0);
		
		// Draw vertical line
		if (left) {
			g2D.drawLine(0, 0, 0, getHeight() - 1);
		} else {
			g2D.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight() - 1);
		}
		
		// Draw bottom bar
		g2D.drawLine(0, getHeight() - 1, getWidth() - 1, getHeight() - 1);
		
		// If we have a debug color, highlight our area
		if (getDebugColor() != null) {
			g2D.setPaint(getDebugColor());
			g2D.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		}
	}
}
