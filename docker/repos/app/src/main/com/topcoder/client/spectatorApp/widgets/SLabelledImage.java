package com.topcoder.client.spectatorApp.widgets;

import java.awt.Graphics2D;
import com.topcoder.client.spectatorApp.views.AnimatePanel;
import com.topcoder.client.spectatorApp.views.LayeredPanel;

/**
 * This class combines an image that is labelled by another panel that will be
 * centered, both horizontally and vertically (plus or minus an offset) at the
 * bottom of the image. The image will be automatically wrapped in an SOutline
 * using the default insets.
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
public class SLabelledImage extends LayeredPanel {
	/** The center offset to use to position the label on the image */
	private int centerOffset;

	/** Identifier of the outline layer */
	private final static int OUTLINE_LAYER = 0;

	/** Identifier of the label layer */
	private final static int LABEL_LAYER = 1;

	/**
	 * Constructs a labelled image panel with the label being centered at the
	 * bottom of the image (where it overlaps the image by about half)
	 * 
	 * @param image
	 *           the image
	 * @param label
	 *           the label panel to assign
	 */
	public SLabelledImage(SImage image, AnimatePanel label) {
		this(image, label, 0);
	}

	/**
	 * Constructs a labelled image panel with the label being centered at the
	 * bottom of the image (offset from centered [vertically] by the amount
	 * specified]
	 * 
	 * @param image
	 *           the image
	 * @param label
	 *           the label panel to assign
	 * @param centerOffset
	 *           the vertical offset for the image where positive moves the label
	 *           down and negative
	 */
	public SLabelledImage(SImage image, AnimatePanel label, int centerOffset) {
		// Embed the image into an outline
		SOutline outline = new SOutline(image);
		// Setup the panel
		this.centerOffset = centerOffset;
		addLayer(outline);
		addLayer(label);
		// Lay it out
		setSize(Math.max(outline.getWidth(), label.getWidth()), outline.getHeight() + (label.getHeight() / 2) + centerOffset);
	}

	/**
	 * Returns the height of the image & outline only
	 * 
	 * @return the height of the image & outline only
	 */
	public int getImageHeight() {
		return getLayer(0).getHeight();
	}

	/** Overridden to properly position things */
	public void setSize(int width, int height) {
		// Call super set size
		super.setSize(width, height);
		// Get the panels
		AnimatePanel outline = getLayer(OUTLINE_LAYER);
		AnimatePanel label = getLayer(LABEL_LAYER);
		// Calculate the compacted width/height
		int minHeight = outline.getHeight() + (label.getHeight() / 2) + centerOffset;
		int minWidth = Math.max(outline.getWidth(), label.getWidth());
		// Figure out the padding
		int widthPad = (width - minWidth) / 2;
		if (widthPad < 0) widthPad = 0;
		int heightPad = (height - minHeight) / 2;
		if (heightPad < 0) heightPad = 0;
		// Position the outline
		outline.setPosition(widthPad + (minWidth / 2) - (outline.getWidth() / 2), heightPad);
		label.setPosition(widthPad + (minWidth / 2) - (label.getWidth() / 2), heightPad + outline.getHeight() - (label.getHeight() / 2) + centerOffset);
	}

	/**
	 * Returns the outline panel being used
	 * 
	 * @return the outline panel being used
	 */
	public int getCenterOffset() {
		return centerOffset;
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
