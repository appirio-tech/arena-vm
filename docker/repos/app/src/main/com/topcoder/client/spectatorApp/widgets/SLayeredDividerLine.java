package com.topcoder.client.spectatorApp.widgets;

import java.awt.Graphics2D;
import com.topcoder.client.spectatorApp.views.AnimatePanel;
import com.topcoder.client.spectatorApp.views.LayeredPanel;

/**
 * A divider line that will overlay other panels on top of them (typically image
 * and/or title panels). The caller can then use the typical
 * {@link LayeredPanel} API to manipulate the layers on top of it. Note: the
 * DividerLine is EXPECTED to be in getLayer(0). A ClassCastException will occur
 * if this layer is replace with a non-DividerLine layer.
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
public class SLayeredDividerLine extends LayeredPanel {
	/** The left layer */
	private AnimatePanel left = null;

	/** The right layer */
	private AnimatePanel right = null;

	/** The center layer */
	private AnimatePanel center = null;

	/** The divider layer */
	private final static int DIVIDER_LAYER = 0;

	/**
	 * Constructs the layered divider line with ONLY the divider line. Assumes
	 * the user will add layers on top of this.
	 * 
	 * @param atTop
	 *           whether the divider is on top or not
	 */
	public SLayeredDividerLine(boolean atTop) {
		// Add the divider line
		SDividerLine dividerLine = new SDividerLine(atTop);
		addLayer(dividerLine);
		super.setSize(dividerLine.getWidth(), dividerLine.getHeight());
	}

	/**
	 * Constructs the layered divider line with a left, right and center panel.
	 * This will
	 * 
	 * @param atTop
	 *           whether the divider is on top or not
	 * @param panelPositions
	 *           the panels and their positions
	 */
	public SLayeredDividerLine(boolean atTop, AnimatePanel left, AnimatePanel center, AnimatePanel right) {
		this(atTop);
		// Max widths/heights
//		int[] maxWidth = new int[3];
//		int maxHeight = 0;
		// Save our references
		this.left = left;
		this.right = right;
		this.center = center;
		// Add the three layers
		// Order important:
		// 1) lay the right first (image)
		// 2) lay the left next (probably text)
		// 3) lay the third last (probably more text)
		// Laying out this way will prevent overlap
		if (right != null) addLayer(right);
		if (left != null) addLayer(left);
		if (center != null) addLayer(center);
		// Set the size to the minimums
		setSize(Math.max(getLayer(DIVIDER_LAYER).getWidth(), getWidth(left) + getWidth(center) + getWidth(right)), ((SDividerLine) getLayer(DIVIDER_LAYER)).getDividerHeight()
					+ Math.max(getHeight(left), Math.max(getHeight(center), getHeight(right))));
	}

	/** Helper method to get the width of the panel. If panel is null, returns 0 */
	private int getWidth(AnimatePanel panel) {
		return panel == null ? 0 : panel.getWidth();
	}

	/** Helper method to get the height of the panel. If panel is null, returns 0 */
	private int getHeight(AnimatePanel panel) {
		return panel == null ? 0 : panel.getHeight();
	}

	/**
	 * Helper method to get the vertical alignment of the panel. If panel is
	 * null, returns 0
	 */
	private int getVerticalAlignment(AnimatePanel panel) {
		return panel == null ? 0 : panel.getVerticalAlignment();
	}

	/**
	 * Overrides to properly position everything
	 */
	public void setSize(int width, int height) {
		// Call the super size
		super.setSize(width, height);
		// Get the divider line (first layer)
		SDividerLine dividerLine = (SDividerLine) getLayer(DIVIDER_LAYER);
		dividerLine.setPosition(0, 0);
		dividerLine.setSize(width, height);
		// Eliminate the divider height
		height -= dividerLine.getDividerHeight();
		int pad = dividerLine.isOnTop() ? dividerLine.getDividerHeight() : 0;
		// Find the biggest height and the alignment position
		AnimatePanel[] panels = getLayers();
		// Figure out the max height/alignment of all non divider line panels
		int alignmentLine = 0, maxHeight = 0;
		for (int x = 1; x < panels.length; x++) {
			// Ignore the right image...
			if (panels[x] != right) {
				if (getHeight(panels[x]) == maxHeight) {
					alignmentLine = Math.max(alignmentLine, getVerticalAlignment(panels[x]));
				} else if (getHeight(panels[x]) > maxHeight) {
					alignmentLine = getVerticalAlignment(panels[x]);
					maxHeight = getHeight(panels[x]);
				}
			}
		}
		if (maxHeight > height) maxHeight = height;
		// Set the vertical alignment
		setVerticalAlignment(pad + ((height - maxHeight) / 2) + alignmentLine);
		// Set the layout position of all other panels
		panels = getLayers();
		for (int x = 1; x < panels.length; x++) {
			int xPos = 0;
			// Set the size (if it's not the left/right/center
			if (!(panels[x] == left || panels[x] == center || panels[x] == right)) {
				panels[x].setSize(width, height);
			}
			// Figure out where to put things
			if (panels[x] == center) xPos = (width - center.getWidth()) / 2;
			if (panels[x] == right) xPos = width - right.getWidth();
			if (panels[x] != right) {
				panels[x].setPosition(xPos, getVerticalAlignment() - panels[x].getVerticalAlignment());
			} else {
				panels[x].setPosition(xPos, pad);
			}
		}
	}

	/** Renders the panel incrementally or fully - up to the panel */
	public void render(Graphics2D g2D) {
		super.render(g2D);
		if (getDebugColor() != null) {
			g2D.setPaint(getDebugColor());
			g2D.drawRect(getX(), getY(), getWidth() - 1, getHeight() - 1);
		}
	}
}
