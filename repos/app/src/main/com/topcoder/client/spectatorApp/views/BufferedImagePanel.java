package com.topcoder.client.spectatorApp.views;



import java.awt.Graphics2D;

import java.awt.Transparency;

import java.awt.image.BufferedImage;



import com.topcoder.client.spectatorApp.CommonRoutines;



/**

 * Abstract panel that used for static buffered images that will automatically

 * redraw themselves when resized - supports transparency

 * @author Tim 'Pops' Roberts

 */

public abstract class BufferedImagePanel extends AbstractAnimatePanel {



	/** The volatile image */

	private BufferedImage image = null;

	

	/** 

	 * Empty constructor 

	 */

	public BufferedImagePanel() {

	}



	/** 

	 * Creates the buffered image based on the current width/height 

	 */

	private void createImage() {

		// Dispose of the old image

		dispose();

		

		// Create the new image

		image = CommonRoutines.createOffScreenBuffer(getWidth(), getHeight(), Transparency.BITMASK);

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


        protected boolean redraw = false;

	/** Renders this panel */

	public void render(Graphics2D g2D) {

		// Is the image created?

		if(image==null) {

			createImage();

			drawImage(image.createGraphics());				

		}
                if(redraw) {
                    redraw = false;
                    drawImage(image.createGraphics());				
                }

			

		// Draw the image
		g2D.drawImage(image, getX(), getY(), null);

	}

}

