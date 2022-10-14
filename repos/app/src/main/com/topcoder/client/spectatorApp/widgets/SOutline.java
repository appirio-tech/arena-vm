package com.topcoder.client.spectatorApp.widgets;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Insets;
import com.topcoder.client.spectatorApp.views.AnimatePanel;
import com.topcoder.client.spectatorApp.views.DirectDrawPanel;
import com.topcoder.client.spectatorApp.views.LayeredPanel;

/**
 * This class provides an outline around another panel. Note that if this panel
 * is resized, the embedded panel will be automatically centered within the size
 * (unless the size is smaller than the panel in which case the panel will be
 * moved to the upper left and chopped). If the underlying panel changes size,
 * this panel will reposition that panel within it's size limits.
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
public class SOutline extends LayeredPanel {
	/** Rectangle background color */
	private Color backColor = new Color(5, 80, 113);

	/** Rectangle outline color */
	private Color outlineColor = new Color(0, 150, 166);

	/** The alpha composite to draw the background with */
	private AlphaComposite backgroundComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .5f);

	/** Handy dandy reference to the outline layer */
	private final static int OUTLINE_LAYER = 0;

	/** Reference to the embedded panel */
	private final static int EMBEDDED_LAYER = 1;

	/** The insets around the panel */
	private Insets insets;

	/** Indicator whether to resize internal panel also */
	private boolean resizePanel = false;

	/** The panel */
	private AnimatePanel panel;

	/**
	 * Constructs a border around the specified panel with 5pt insets around it.
	 * 
	 * @param panel
	 *           the panel to wrap
	 */
	public SOutline() {
		this(null);
	}

	/**
	 * Constructs a border around the specified panel with 5pt insets around it.
	 * 
	 * @param panel
	 *           the panel to wrap
	 */
	public SOutline(AnimatePanel panel) {
		this(panel, new Insets(5, 5, 5, 5));
	}

	/**
	 * Constructs an outline around the specified panel with using the specified
	 * insets
	 * 
	 * @param panel
	 *           the panel to wrap
	 * @param insets
	 *           the insets to use
	 */
	public SOutline(AnimatePanel panel, Insets insets) {
		// Add in the layer
		addLayer(new OutlineOnly());
		// Save the insets
		this.insets = insets;
		// Set the initial size
		if (panel == null) {
			setSize(insets.left + insets.right, insets.top + insets.bottom);
		} else {
			setPanel(panel);
		}
	}

	/** Sets the panel embedded within the outline */
	public void setPanel(AnimatePanel panel) {
		// Save the panel
		this.panel = panel;
		// Set the panel layer
		setLayer(EMBEDDED_LAYER, panel);
		// Set the size
		setSize(insets.left + panel.getWidth() + insets.right, insets.top + panel.getHeight() + insets.bottom);
		// Set the panel at the insets...
		panel.setPosition(insets.left, insets.top);
	}

	/** Sets the panel embedded within the outline */
	public void setPanel(AnimatePanel panel, Insets insets) {
		// Save the panel
		this.panel = panel;
		this.insets = insets;
		// Set the panel layer
		setLayer(EMBEDDED_LAYER, panel);
		// Set the size
		setSize(insets.left + panel.getWidth() + insets.right, insets.top + panel.getHeight() + insets.bottom);
		// Set the panel at the insets...
		panel.setPosition(insets.left, insets.top);
	}

	/**
	 * Sets whether the embedded panel will be resized when the outline is
	 * resized
	 * 
	 * @param resizePanel
	 *           true to be resized
	 */
	public void setResizePanel(boolean resizePanel) {
		this.resizePanel = resizePanel;
	}

	/**
	 * Returns whether the embedded panel will be resized when the outline is
	 * resized
	 * 
	 * @return true if the embedded panel will be resized
	 */
	public boolean isResizePanel() {
		return resizePanel;
	}

	/**
	 * Overridden to resize the panel minus the insets
	 */
	public void setSize(int width, int height) {
		// Call the super size
		super.setSize(width, height);
		// Set the size of the outline
		getLayer(OUTLINE_LAYER).setSize(width, height);
		// If resize, then resize panel
		if (resizePanel && panel != null) {
			panel.setSize(width - insets.left - insets.right, height - insets.top - insets.bottom);
		}
	}

	/**
	 * Returns the background color being used
	 * 
	 * @return the background color being used
	 */
	public Color getBackColor() {
		return backColor;
	}

	/**
	 * Sets the background color being used
	 * 
	 * @param backColor
	 *           the background color being used
	 */
	public void setBackColor(Color backColor) {
		this.backColor = backColor;
	}

	/**
	 * Sets whether to fill the full box or not. Useful when the embedded panel
	 * is transparent
	 * 
	 * @param fill
	 *           fill the full box
	 */
	public void setFillBox(boolean fill) {
		// Get the prior box
		AnimatePanel prior = getLayer(OUTLINE_LAYER);
		AnimatePanel newPanel;
		// Determine what to do
		if (fill) {
			if (prior instanceof FullOutline) return;
			newPanel = new FullOutline();
		} else {
			if (prior instanceof OutlineOnly) return;
			newPanel = new OutlineOnly();
		}
		// Set the new panel the same position/size
		newPanel.setPosition(prior.getX(), prior.getY());
		newPanel.setSize(prior.getWidth(), prior.getHeight());
		// Set it back into the layer
		setLayer(OUTLINE_LAYER, newPanel);
	}

	/** Overriden to get the embedded size */
	public int getVerticalAlignment() {
		return panel == null ? getHeight() / 2 : panel.getVerticalAlignment() + insets.top;
	}

	/**
	 * Returns whether the box is being filled or not
	 * 
	 * @return whether the box is being filled or not
	 */
	public boolean isFillBox() {
		return (getLayer(OUTLINE_LAYER) instanceof FullOutline);
	}

	/**
	 * Returns the AlphaComposite being used to draw the background
	 * 
	 * @return the AlphaComposite being used to draw the background
	 */
	public AlphaComposite getBackgroundComposite() {
		return backgroundComposite;
	}

	/**
	 * Sets the AlphaComposite being used to draw the background
	 * 
	 * @param backgroundComposite
	 *           the AlphaComposite being used to draw the background
	 */
	public void setBackgroundComposite(AlphaComposite backgroundComposite) {
		this.backgroundComposite = backgroundComposite;
	}

	/**
	 * Returns the outline color to use
	 * 
	 * @return the outline color to use
	 */
	public Color getOutlineColor() {
		return outlineColor;
	}

	/**
	 * Sets the outline color to use
	 * 
	 * @param outlineColor
	 *           the outline color to use. Null to turn off the outline
	 */
	public void setOutlineColor(Color outlineColor) {
		this.outlineColor = outlineColor;
	}

	/** Returns the insets used */
	public Insets getInsets() {
		return insets;
	}

	/**
	 * The class that will actually draw the outline. This is a direct draw to
	 * avoid trading off the amount of memory that would be overlapped with the
	 * image.
	 * 
	 * @author Tim "Pops" Roberts
	 * @version 1.0
	 */
	class OutlineOnly extends DirectDrawPanel {
		/**
		 * Draw the image
		 */
		protected void drawImage(Graphics2D g2D) {
			// Get the height/widths
			int width = getWidth();
			int height = getHeight();
			int panelWidth = panel == null ? 0 : panel.getWidth();
			int panelHeight = panel == null ? 0 : panel.getHeight();
			// Figure out the padding
			int widthPad = (width - panelWidth) / 2;
			if (widthPad < 0) widthPad = 0;
			int heightPad = (height - panelHeight) / 2;
			if (heightPad < 0) heightPad = 0;
			// Nothing to do if there is no padding
			if (widthPad == 0 && heightPad == 0) return;
			// Draw the background
			g2D.setColor(backColor);
			g2D.setComposite(backgroundComposite);
			// Fill each of the borders
			g2D.fillRect(0, 0, width, heightPad); // Top border
			g2D.fillRect(0, widthPad, widthPad, height - (2 * widthPad)); // Left
																								// border
			g2D.fillRect(width - widthPad, widthPad, widthPad, height - (2 * widthPad)); // Right
																													// border
			g2D.fillRect(0, height - heightPad, width, heightPad); // Bottom
																						// border
			// Restore to a full alpha
			g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
			// Draw the outline
			if (outlineColor != null) {
				g2D.setColor(outlineColor);
				g2D.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
			}
			if (getDebugColor() != null) {
				g2D.setPaint(getDebugColor());
				g2D.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
			}
		}
	}

	/**
	 * The class that will draw the FULL box with the outline
	 * 
	 * @author Tim "Pops" Roberts
	 * @version 1.0
	 */
	class FullOutline extends DirectDrawPanel {
		/**
		 * Draw the image
		 */
		protected void drawImage(Graphics2D g2D) {
			// Draw the background
			g2D.setColor(backColor);
			g2D.setComposite(backgroundComposite);
			g2D.fillRect(1, 1, getWidth() - 2, getHeight() - 2);
			// Restore to a full alpha
			g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
			// Draw the outline
			if (outlineColor != null) {
				g2D.setColor(outlineColor);
				g2D.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
			}
			// Override the outline with the debug color
			if (getDebugColor() != null) {
				g2D.setPaint(getDebugColor());
				g2D.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
			}
		}
	}
}
