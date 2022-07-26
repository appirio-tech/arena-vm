package com.topcoder.client.spectatorApp.widgets;



import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import org.apache.log4j.Category;
import com.topcoder.client.spectatorApp.CommonRoutines;
import com.topcoder.client.spectatorApp.SpectatorApp;
import com.topcoder.client.spectatorApp.views.VolatileImagePanel;



/**

 * The spectator divider plus a background.  If the height of

 * this panel is larger than the divider height, then the background

 * of the excess is filled with the background color (it is

 * NOT transparent)  

 * 

 * @author Tim "Pops" Roberts

 * @version 1.0

 */

public class SStudioDividerLine extends VolatileImagePanel {

	

    /** Reference to the logging category */

    private static final Category cat = Category.getInstance(SDividerLine.class.getName());



    /** The background color */

    private Color backColor = Color.black;



    /** The background texture */

    private BufferedImage dividerImage;



    /** The divider height */

    private int dividerHeight;



    /** Boolean indicating whether to put the divider at the top or bottom */

    private boolean atTop;



    /**

     * Constructs the divider

     * @param atTop

     */

	public SStudioDividerLine(boolean atTop) {

		// Save the atTop value

		this.atTop = atTop;

		

		// Get the divider image

        Image divider = Toolkit.getDefaultToolkit().getImage(SpectatorApp.class.getResource("headerBG.png"));

        

        // Load the images

        if (!CommonRoutines.loadImagesFully(new Image[]{divider})) {

            cat.error("Unable to load the dividerline.jpg image");

            return;

        }



		// Convert to Buffered Images

		dividerImage = CommonRoutines.createBufferedImage(divider, Transparency.OPAQUE);



		

        // Get the divider height

        dividerHeight = divider.getHeight(null);

        

	}

	

	/**

	 * Return the background color being used

	 * @return the background color being used 

	 */

	public Color getBackgroundColor() {

		return backColor;

	}

	

	/**

	 * Sets the background color being used

	 * @param backColor the background color being used

	 */

	public void setBackgroundColor(Color backColor) {

		this.backColor = backColor;

	}

	

	/** 

	 * Returns the divider height

	 * @param the divider height

	 */

	public int getDividerHeight() {

		return dividerHeight;

	}

	

	/**

	 * Returns whether the divider is on top or not

	 * @return whether the divider is on top or not

	 */

	public boolean isOnTop() {

		return this.atTop;

	}

	

    /**

     * Create the back buffer

     */

    protected void drawImage(Graphics2D g2D) {



        // Setup antialiasing

        g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);



        // First - paint the background color over everything

        g2D.setPaint(backColor);

        g2D.fillRect(0, 0, getWidth(), getHeight());

        

        // Get the texture paint

		TexturePaint dividerTexture;

		if(atTop) {

			dividerTexture = new TexturePaint(dividerImage, new Rectangle2D.Double(0, 0, dividerImage.getWidth(null), dividerImage.getHeight(null)));

		} else {

			dividerTexture = new TexturePaint(dividerImage, new Rectangle2D.Double(0, getHeight()-dividerHeight, dividerImage.getWidth(null), dividerImage.getHeight(null)));			

		}

		

        // Draw the divider line

        g2D.setPaint(dividerTexture);



        // Draw it at the specified height (either height of the panel

        // or height of the divider - whichever is smaller

        if(atTop) {

        	g2D.fillRect(0, 0, getWidth(), Math.min(dividerHeight, getHeight()));

        } else {

        	g2D.fillRect(0, Math.max(getHeight() - dividerHeight, 0), getWidth(), dividerHeight); //Math.min(dividerHeight, getHeight()));

        }

    }

	

}

