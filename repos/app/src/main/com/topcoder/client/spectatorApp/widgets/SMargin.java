package com.topcoder.client.spectatorApp.widgets;

import java.awt.Graphics2D;
import java.awt.Insets;
import com.topcoder.client.spectatorApp.views.AnimatePanel;
import com.topcoder.client.spectatorApp.views.LayeredPanel;

/**
 * This class provides an artifical margin around another panel.
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
public class SMargin extends LayeredPanel {
	/** The margin to be used around the panel */
	private Insets insets;

	/**
	 * Constructs the margin from the panel and insets
	 * 
	 * @param panel
	 *           the panel to margin around
	 * @param insets
	 *           the insets to use
	 */
	public SMargin(AnimatePanel panel, Insets insets) {
		// Add our layer
		this.addLayer(panel);
		// Save the insets
		this.insets = insets;
		// Put the panel in position
		panel.setPosition(insets.left, insets.top);
		setVerticalAlignment(panel.getVerticalAlignment() + insets.top);
		// Set the overall size
		super.setSize(insets.left + panel.getWidth() + insets.right, insets.top + panel.getHeight() + insets.bottom);
	}

	/** Overrides to resize embedded field */
	public void setSize(int width, int height) {
		super.setSize(width, height);
		getLayer(0).setSize(width - insets.left - insets.right, height - insets.top - insets.bottom);
	}

	/** Overriden to get the embedded size */
	public int getVerticalAlignment() {
		return getLayers().length > 0 ? getLayer(0).getVerticalAlignment() + insets.top : getHeight() / 2;
	}

	/**
	 * Returns the panel being wrapped
	 * 
	 * @return the panel being wrapped
	 */
	public AnimatePanel getWrappedPanel() {
		return getLayer(0);
	}

	/** Overridden to add debugging colors */
	public void render(Graphics2D g2D) {
		super.render(g2D);
		if (getDebugColor() != null) {
			g2D.setColor(getDebugColor());
			g2D.drawRect(getX(), getY(), getWidth() - 1, getHeight() - 1);
		}
	}
}
