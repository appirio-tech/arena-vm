package com.topcoder.client.spectatorApp.views;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.image.VolatileImage;

import com.topcoder.client.spectatorApp.CommonRoutines;

/**
 * Abstract panel that used for static volatile images that will automatically
 * redraw themselves if the image is lost or invalidated  - transparency not 
 * supported
 * @author Tim 'Pops' Roberts
 */
public abstract class VolatileImagePanel extends AbstractAnimatePanel {

	/** The volatile image */
	private VolatileImage image = null;
	//private BufferedImage image = null;
	
	/** 
	 * Empty constructor 
	 */
	public VolatileImagePanel() {
	}

	/** 
	 * Creates the volatile image based on the current width/height 
	 */
	private VolatileImage createImage() {
//	private BufferedImage createImage() {
		// Dispose of the old image
		dispose();
		
		// Create the new image
		image = CommonRoutines.createVolatileImage(getWidth(), getHeight());
		//image = CommonImageRoutines.createOffScreenBuffer(getWidth(), getHeight(), Transparency.OPAQUE);
		return image;
	}
	
	/** 
	 * Releases the resources allocated to the image 
	 */
	public void dispose() {
		if(image!=null) {
			image.flush();
			image=null;
		}
	}
	
	/** 
	 * Implementing class must provide the drawing for this image
	 * @param g2D the graphics2d to use
	 */
	protected abstract void drawImage(Graphics2D g2D);
	
	/** 
	 * Override the set size to dispose of the image created 
	 * to ensure it's recreated with the new size properly
	 * @param width the width of the image
	 * @param height the height of the image
	 */ 
	public void setSize(int width, int height) {
		super.setSize(width, height);
		dispose();
	}
	
	/** 
	 * Redraws the image with the associated configuration.
	 * This will ensure the image has been created and is valid.
	 * Once the image has been created and validated, the 
	 * drawImage(Graphics2D) method is called for the implementor
	 * to draw the image.
	 * @param config the current graphics configuration 
	 */
	private void drawImage(GraphicsConfiguration config) {
		
		// Loop until contents aren't lost
		do {
			// Is it valid?
			if (image==null || image.validate(config) == VolatileImage.IMAGE_INCOMPATIBLE) {
//			if (image==null) {
				// old image doesn't work with new GraphicsConfig; re-create it
				createImage();
			}
				
			// get the graphics and draw onto it
			Graphics2D g = image.createGraphics();
			drawImage(g);
			g.dispose();
			
//		} while (image==null);		
		} while (image!=null && image.contentsLost());		
	}
	
	/** Renders this panel */
	public void render(Graphics2D g2D) {
		do {
			// Is the image still valid?
			int imageStatus = image==null ? VolatileImage.IMAGE_INCOMPATIBLE : image.validate(g2D.getDeviceConfiguration());
//			int imageStatus = image==null ? VolatileImage.IMAGE_INCOMPATIBLE : VolatileImage.IMAGE_OK;
			
			switch (imageStatus) {
				case VolatileImage.IMAGE_INCOMPATIBLE :
					createImage();
					drawImage(g2D.getDeviceConfiguration());
					break;
				
				case VolatileImage.IMAGE_RESTORED :
					drawImage(g2D.getDeviceConfiguration());
					break;				
			}
				
			
			// Draw the image
			if(image!=null) g2D.drawImage(image, getX(), getY(), null);
			
		// Loop back if we lost the image
		} while (image!=null && image.contentsLost());
//		} while (image==null);
	}
}
