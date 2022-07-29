/**
 * SwitchRenderer.java
 *
 * Description:		The panel that displays a 'switch'
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.views;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

import org.apache.log4j.Category;

import com.topcoder.client.spectatorApp.CommonRoutines;
import com.topcoder.client.spectatorApp.FontFactory;
import com.topcoder.client.spectatorApp.SpectatorApp;
import com.topcoder.client.spectatorApp.scoreboard.view.HeaderPanel;
import com.topcoder.client.spectatorApp.scoreboard.view.TrailerPanel;


public class SwitchRenderer extends LayeredPanel implements AnimatePanel {

    /** Reference to the logging category */
    private static final Category cat = Category.getInstance(SwitchRenderer.class.getName());

    /** Some object that can be related back */
    private Object someObject = null;

	/** Panel # layer of the background */
	private static final int BACKGROUNDLAYER = 0;

	/** Panel # layer of the header */
	private static final int HEADERLAYER = 2;

	/** Panel # layer of the trailer */
	private static final int TRAILERLAYER= 3;

	/** Panel # layer of the message  */
	private static final int MESSAGELAYER = 1;
	
    /** Constructs the trailer panel */
    public SwitchRenderer(String switchTitle, String contestName) {

		// Add the layers in order		
		super.addLayer(new BackgroundPanel());
		super.addLayer(new MessagePanel());
		super.addLayer(new HeaderPanel(switchTitle));
		super.addLayer(new TrailerPanel(contestName));
    }

    /**
     * Set's the some object
     * @param someObject the object to set
     */
    public void setSomeObject(Object someObject) {
        this.someObject = someObject;
    }

    /**
     * Returns the object set
     * @return the object set or null if none set
     */
    public Object getSomeObject() {
        return someObject;
    }

    /**
     * Sets the current title, contestname and message
     * @param switchTitle the title to show
     * @param contestName the contest name to show
     * @param message the message to display
     */
    public void setMoveMessage(String switchTitle, String contestName, String message) {
    	// Get the various panels
    	HeaderPanel headerPanel = (HeaderPanel)getLayer(HEADERLAYER);
		TrailerPanel trailerPanel = (TrailerPanel)getLayer(TRAILERLAYER);
		MessagePanel messagePanel = (MessagePanel)getLayer(MESSAGELAYER);
    	
    	// If nothing changed - ignore
    	if(headerPanel.getRoomTitle().equals(switchTitle) 
    	&& trailerPanel.getContestName().equals(contestName)
    	&& messagePanel.getMoveMessage().equals(message)) return;
    	
        // Save the message
        ((MessagePanel)getLayer(MESSAGELAYER)).setMoveMessage(message);
        ((HeaderPanel)getLayer(HEADERLAYER)).setRoomTitle(switchTitle);
        ((TrailerPanel)getLayer(TRAILERLAYER)).setContestName(contestName);

		// Refigure the size/positions that may have changed based
		// on the messages
		setSize(getWidth(), getHeight());
    }


    /** Sets the size of the panel */
    public void setSize(int width, int height) {
    	// Record the new size
    	super.setSize(width, height);
    	
		// Get the various panels
		HeaderPanel headerPanel = (HeaderPanel)getLayer(HEADERLAYER);
		TrailerPanel trailerPanel = (TrailerPanel)getLayer(TRAILERLAYER);
		BackgroundPanel backgroundPanel = (BackgroundPanel)getLayer(BACKGROUNDLAYER);
		MessagePanel messagePanel = (MessagePanel)getLayer(MESSAGELAYER);

		// Set the pos/size of the header panel
		headerPanel.setPosition(0,0);
		headerPanel.setSize(width, headerPanel.getHeight());    	

		// Set the pos/size of the trailer panel
		trailerPanel.setPosition(0, height - trailerPanel.getHeight());
		trailerPanel.setSize(width, trailerPanel.getHeight());    	

		// Set the pos/size of the background panel
		backgroundPanel.setPosition(0, headerPanel.getHeight());
		backgroundPanel.setSize(width, height-headerPanel.getHeight()-trailerPanel.getHeight());

		// Set the pos/size of the message panel
		messagePanel.setPosition(0, backgroundPanel.getY());
		messagePanel.setSize(width, backgroundPanel.getHeight());
    }

    /**
     * Creates the back buffer.  Overridden to do nothing
     */
    class BackgroundPanel extends VolatileImagePanel implements AnimatePanel {
		/** Reference to the background image*/
		private BufferedImage backImage;


		public BackgroundPanel() {
			// Get the background tile
			Image background = Toolkit.getDefaultToolkit().getImage(SpectatorApp.class.getResource("dots.gif"));

			// Load the images
			if (!CommonRoutines.loadImagesFully(new Image[]{background})) {
				System.out.println("Error loading the images");
				return;
			}

			// Convert to Buffered Images
			backImage = CommonRoutines.createBufferedImage(background, Transparency.OPAQUE);
		}
		
    	public void drawImage(Graphics2D g2D) {
	        // Paint the background image
	        g2D.setPaint(new TexturePaint(backImage, new Rectangle(0, 0, backImage.getWidth(), backImage.getHeight())));
	        g2D.fillRect(0, 0, getWidth(), getHeight());
    	}	
    }

	class MessagePanel extends DirectDrawPanel {
		private String moveMessage = "";
	
		/** Font used */
		private Font titleFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_BOLD, Font.PLAIN, 58);

		/** Font metrics */
		private FontMetrics titleFontFM;

		/** Color of the text */
		private Color titleColor = Color.white;

		public MessagePanel() {
			// Get the font metrics
			titleFontFM = CommonRoutines.getFontMetrics(titleFont);

		}	
		
		/**
		 * Sets the move message
		 */
		public void setMoveMessage(String moveMessage) {
			this.moveMessage = moveMessage;
		}
		
		/**
		 * Returns the current move message
		 * @return the move message
		 */
		public String getMoveMessage() {
			return moveMessage;
		}
		
	    /**
	     * Paints the panel
	     *
	     * @param g2D the graphics to paint with
	     */
	    public void drawImage(Graphics2D g2D) {
	
	        // Center the message on the screen
	        int h = getHeight() / 2 - titleFontFM.getAscent() / 2;
	        int w = getWidth() / 2 - titleFontFM.stringWidth(moveMessage) / 2;
	
	        // Setup antialiasing
	        g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	
	        // Paint it
	        g2D.setPaint(titleColor);
	        g2D.setFont(titleFont);
	        g2D.drawString(moveMessage, w, h);
	
	    }
	}
}
