package com.topcoder.client.spectatorApp.widgets;



import java.awt.Graphics2D;
import java.awt.Image;
import org.apache.log4j.Category;
import com.topcoder.client.spectatorApp.CommonRoutines;
import com.topcoder.client.spectatorApp.views.BufferedImagePanel;
import java.awt.Toolkit;
import java.io.File;



/**

 * This class will draw an image centered within the size. The

 * background will be transparent.

 * 

 * @author Tim "Pops" Roberts

 * @version 1.0

 */

public class SReloadingImage extends BufferedImagePanel {

	

    /** Reference to the logging category */

    private static final Category cat = Category.getInstance(SReloadingImage.class.getName());



    /** The coder Image */

    private Image image;

    private String path;

    private boolean paused = false;
    
    private Object objLock = new Object();


	/**

	 * Constructs a panel with the given image

	 * @param image the image to use

	 */

	public SReloadingImage(String image) {

        // Load the image
            path = image;
            this.image = Toolkit.getDefaultToolkit().getImage(path);

        if (!CommonRoutines.loadImagesFully(new Image[]{this.image})) {

            cat.error("Unable to load image");

            //return;

        }

        

        // Save the image


		

		// Set the size

		setSize(this.image.getWidth(null), this.image.getHeight(null));
                Thread t = new Thread(new Loader());
                //t.setDaemon(false);
                t.start();
	}

        public void pause() {
             paused = true;
        }

        public void resume() {
             paused = false;
        }
        
        private class Loader implements Runnable {
            public void run() {
                cat.info("THREAD LAUNCHED");
                long lastModified = -2; 
                while(running) {
                    try {
                        if (paused) {
                            Thread.sleep(1000/15);
                            continue;
			}
                        //cat.info("NEW IMAGE CYCLE");
                        long lm = new File(path).lastModified();

                        if (lm != lastModified) {
                            lastModified = lm;
                            Image img = Toolkit.getDefaultToolkit().createImage(path);
                            if(CommonRoutines.loadImagesFully(new Image[] {img})) {
                                synchronized(objLock) {
                                    image = img;
                                    setSize(image.getWidth(null), image.getHeight(null));
                                    redraw = true;
                                    //cat.info("NEW IMAGE LOADED");
                                }
                            }
                        }
                        Thread.sleep(1000/15);
                    } catch (Exception e) {
                        cat.error("LOADER ERROR", e);
                    }
                }
                cat.info("THREAD DYING");
            }
        }
        
        private boolean running = true;

        public void stop() {
            running = false;
        }
	
        public void dispose() {
            //running = false;
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
                        synchronized(objLock) {
                            g2D.drawImage(image, (getWidth() - image.getWidth(null)) / 2, (getHeight() - image.getHeight(null)) / 2, null);
                        }
		}

	}

}

