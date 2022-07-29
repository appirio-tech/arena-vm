package com.topcoder.client.spectatorApp.widgets;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import org.apache.log4j.Category;
import com.topcoder.client.spectatorApp.CommonRoutines;
import com.topcoder.client.spectatorApp.SpectatorApp;
import com.topcoder.client.spectatorApp.views.VolatileImagePanel;

/**
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
public class SNoHeaderBackground extends VolatileImagePanel {
	/** Reference to the logging category */
	private static final Category cat = Category.getInstance(SNoHeaderBackground.class.getName());

	/** The image to tile in the background */
	private BufferedImage tiledImage;

	/**
	 * Constructs the background image from the dots.gif file
	 */
	public SNoHeaderBackground() {
		// Get the background tile
		Image tile = Toolkit.getDefaultToolkit().getImage(SpectatorApp.class.getResource("backdropnoheader.png"));
		// Load the images
		if (!CommonRoutines.loadImagesFully(new Image[] { tile })) {
			cat.error("Unable to load dots.gif");
			return;
		}
		// Save to a buffered image
		tiledImage = CommonRoutines.createBufferedImage(tile, Transparency.OPAQUE);
	}

	/**
	 * Draws the background tiled
	 */
	public void drawImage(Graphics2D g2D) {
		// Render the body background
		g2D.setPaint(new TexturePaint(tiledImage, new Rectangle(0, 0, tiledImage.getWidth(), tiledImage.getHeight())));
		g2D.fillRect(0, 0, getWidth(), getHeight());
		// Debug color
		if (getDebugColor() != null) {
			g2D.setPaint(getDebugColor());
			g2D.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		}
	}
}
