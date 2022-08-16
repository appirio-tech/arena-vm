/**
 * AbstractAnimatePanel.java Description: Abstract animation panel that manages
 * position and size
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
package com.topcoder.client.spectatorApp.views;

import java.awt.Color;
import java.awt.Component;
import org.apache.log4j.Category;
import com.topcoder.client.spectatorApp.ColorProperty;

public abstract class AbstractAnimatePanel extends Component implements AnimatePanel {
	/** Reference to the logging category */
	private static final Category cat = Category.getInstance(AbstractAnimatePanel.class.getName());

	/** The height */
	private int height = -1;

	/** The width */
	private int width = -1;

	/** The horizontal position */
	private int x = 0;

	/** The vertical position */
	private int y = 0;

	/** The vertical alignment */
	private int verticalAlignment = -1;

	/**
	 * Sets the size of the panel
	 * 
	 * @param width
	 *           new width
	 * @param height
	 *           new height
	 */
	public void setSize(int width, int height) {
		// If no change - ignore
		if (this.width == width && this.height == height) return;
		// Save our width/height
		this.width = width;
		this.height = height;
		// Debugging...
		// if(cat.isDebugEnabled()) cat.debug(getClass() + " setSize(" + width +
		// ", " + height + ")");
	}

	/**
	 * Gets the width of the panel
	 * 
	 * @returns the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Gets the height of the panel
	 * 
	 * @returns the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Gets the vertical alignment of the panel
	 * 
	 * @returns the vertical alignment
	 */
	public int getVerticalAlignment() {
		return verticalAlignment;
	}

	/**
	 * Gets the vertical alignment of the panel
	 * 
	 * @returns the vertical alignment
	 */
	public void setVerticalAlignment(int alignment) {
		verticalAlignment = alignment;
	}

	/**
	 * Returns the horizontal position of the panel
	 * 
	 * @return the horizontal position of the panel
	 */
	public int getX() {
		return x;
	}

	/**
	 * Returns the vertical position of the panel
	 * 
	 * @return the vertical position of the panel
	 */
	public int getY() {
		return y;
	}

	/**
	 * Sets the position of the panel
	 * 
	 * @param x
	 *           the horizontal position of the panel
	 * @param y
	 *           the vertical position of the panel
	 */
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
		// Debugging...
		// if(cat.isDebugEnabled()) cat.debug(getClass() + " setPosition(" + x +
		// ", " + y + ")");
	}

	/**
	 * Empty dispose method
	 */
	public void dispose() {}

	/**
	 * Empty animate method
	 */
	public void animate(long now, long diff) {}

	/** Sets this panel like another (both position and size) */
	public void setLike(AnimatePanel source) {
		this.setPosition(source.getX(), source.getY());
		this.setSize(source.getWidth(), source.getHeight());
	}
	
	private Color debugColor = ColorProperty.getColor(getClass().getSimpleName() + ".debugColor");
	
	public void setDebugColor(Color debugColor) 
	{
		this.debugColor = debugColor;
	}
	
	protected Color getDebugColor()
	{
		return debugColor;
	}
}
