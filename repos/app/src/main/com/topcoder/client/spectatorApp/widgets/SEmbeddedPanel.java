package com.topcoder.client.spectatorApp.widgets;

import java.awt.Graphics2D;
import com.topcoder.client.spectatorApp.views.AnimatePanel;
import com.topcoder.client.spectatorApp.views.LayeredPanel;

/**
 * This panel provides the ability to simply embed another panel within it.
 * Whenever this panel is resized, the embedded panel is resized also.
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
public class SEmbeddedPanel extends LayeredPanel {
	/**
	 * Empty Constructor. It's assumed that the setEmbeddedPanel will be called
	 * later.
	 */
	public SEmbeddedPanel() {}

	/**
	 * Constructs the panel using the passed panel as the embedded panel
	 * 
	 * @param panel
	 *           the embedded panel
	 */
	public SEmbeddedPanel(AnimatePanel panel) {
		addLayer(panel);
		setSize(panel.getWidth(), panel.getHeight());
	}

	/**
	 * Sets the embedded panel
	 * 
	 * @param panel
	 *           the embedded panel
	 */
	public void setEmbeddedPanel(AnimatePanel panel) {
		if (getLayers().length == 0) {
			addLayer(panel);
		} else {
			setLayer(0, panel);
		}
		setSize(panel.getWidth(), panel.getHeight());
	}

	/**
	 * Returns the embedded panel or null if none exists
	 * 
	 * @return the embedded panel or null if none exists
	 */
	public AnimatePanel getEmbeddedPanel() {
		if (getLayers().length == 0) return null;
		return getLayer(0);
	}

	/** Overriden to set the size */
	public void setSize(int width, int height) {
		super.setSize(width, height);
		if (getLayers().length > 0) {
			getLayer(0).setPosition(0, 0);
			getLayer(0).setSize(width, height);
		}
	}

	/** Overriden to get the embedded size */
	public int getVerticalAlignment() {
		return getLayers().length > 0 ? getLayer(0).getVerticalAlignment() : getHeight() / 2;
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
