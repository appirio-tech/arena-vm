package com.topcoder.client.spectatorApp.widgets;

import java.awt.Graphics2D;
import java.awt.Image;
import org.apache.log4j.Category;
import com.topcoder.client.spectatorApp.CommonRoutines;
import com.topcoder.client.spectatorApp.views.BufferedImagePanel;

/**
 * This class will draw an image centered within the size. The
 * background will be transparent.
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
public class SImage extends BufferedImagePanel {
	
    /** Reference to the logging category */
    private static final Category cat = Category.getInstance(SImage.class.getName());

    /** The coder Image */
    private Image image;

	/**
	 * Constructs a panel with the given image
	 * @param image the image to use
	 */
	public SImage(Image image) {
        // Load the image
        if (!CommonRoutines.loadImagesFully(new Image[]{image})) {
            cat.error("Unable to load image");
            return;
        }
        
        // Save the image
		this.image = image;
		
		// Set the size
		setSize(image.getWidth(null), image.getHeight(null));
	}
	

	/** Overriden to get the embedded size */
	public int getVerticalAlignment() {
		return getHeight() / 2;
	}
	
	/**
	 * Draw the image
	 */
	protected void drawImage(Graphics2D g2D) {
        // Draw in the image centered
		if(image!=null) {
			g2D.drawImage(image, (getWidth() - image.getWidth(null)) / 2, (getHeight() - image.getHeight(null)) / 2, null);
		}
	}
}
