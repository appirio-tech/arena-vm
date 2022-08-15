package com.topcoder.client.spectatorApp.widgets;

import com.topcoder.client.spectatorApp.views.AnimatePanel;
import com.topcoder.client.spectatorApp.views.LayeredPanel;

/**
 * This panel provides the view of a 'page'. The page consists of a header and a
 * background and optionally a trailer and body.
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
public class SPage extends LayeredPanel {
	/** The header layer */
	public final static int HEADER_LAYER = 1;

	/** The background layer */
	public final static int BACKGROUND_LAYER = 0;

	/** The trailer layer */
	public final static int TRAILER_LAYER = 2;

	/** The body layer */
	public final static int BODY_LAYER = 3;

        private boolean fullBackground = false;
	/**
	 * Constructs the page with ONLY a header
	 * 
	 * @param header
	 *           the header
	 */
	public SPage(AnimatePanel header) {
		this(header, null);
	}

	/**
	 * Constructs the page with a header and trailer
	 * 
	 * @param header
	 *           the header
	 * @param trailer
	 *           the trailer (null means no trailer)
	 */
	public SPage(AnimatePanel header, AnimatePanel trailer) {
		this(header, null, trailer);
	}

	/**
	 * Constructs the page with a header and trailer
	 * 
	 * @param header
	 *           the header
	 * @param trailer
	 *           the trailer (null means no trailer)
	 */
	public SPage(AnimatePanel header, AnimatePanel body, AnimatePanel trailer) {
		this(header, body, new SBackground(), trailer, false);
	}

	/**
	 * Constructs the page with a header and trailer
	 * 
	 * @param header
	 *           the header
	 * @param trailer
	 *           the trailer (null means no trailer)
	 */
	public SPage(AnimatePanel header, AnimatePanel body, AnimatePanel background, AnimatePanel trailer, boolean fullBackground) {
		// Construct the super
		super();
                this.fullBackground = fullBackground;
		// Make sure the header isn't null
		assert header == null : header;
		assert background == null : background;
		// Add all the layers
		setLayer(HEADER_LAYER, header);
		setLayer(BACKGROUND_LAYER, background);
		setLayer(TRAILER_LAYER, trailer);
		setLayer(BODY_LAYER, body);
		// Figure out the minimum width
		int minWidth = 0;
		for (int x = 0; x < getLayers().length; x++) {
			if (getLayer(x) != null) {
				minWidth = Math.max(minWidth, getLayer(x).getWidth());
			}
		}
		// Figure out the minimum height
		int minHeight = 0;
                if(getLayer(HEADER_LAYER) != null)
                    minHeight = getLayer(HEADER_LAYER).getHeight();
		if (getLayer(TRAILER_LAYER) != null) minHeight += getLayer(TRAILER_LAYER).getHeight();
		minHeight += Math.max(getLayer(BACKGROUND_LAYER).getHeight(), getLayer(BODY_LAYER) == null ? 0 : getLayer(BODY_LAYER).getWidth());
		// Set the size
		super.setSize(minWidth, minHeight);
	}

	/**
	 * Returns the background, useful to determine the position and size of the
	 * background (minus header and trailer)
	 */
	public AnimatePanel getBackgroundPanel() {
		return getLayer(BACKGROUND_LAYER);
	}

	/**
	 * Allows setting of the body panel
	 */
	public void setBodyPanel(AnimatePanel panel) {
		// Set the body panel
		setLayer(BODY_LAYER, panel);
		// Get the background layer
		AnimatePanel background = getLayer(BACKGROUND_LAYER);
                AnimatePanel header = getLayer(HEADER_LAYER);
		// Set the body layer to the backgrounds size and position
                if(fullBackground) {
                    int height = 0;
                    if(header != null)
                        height = header.getHeight();
                    panel.setPosition(background.getX(), background.getY() + height);
                    panel.setSize(background.getWidth(), background.getHeight() - height);
                } else {
                    panel.setPosition(background.getX(), background.getY());
                    panel.setSize(background.getWidth(), background.getHeight());
                }
	}

	/** Overrides to resize */
	public void setSize(int width, int height) {
		super.setSize(width, height);
		// Get the layers
		AnimatePanel header = getLayer(HEADER_LAYER);
		AnimatePanel background = getLayer(BACKGROUND_LAYER);
		AnimatePanel trailer = getLayer(TRAILER_LAYER);
		AnimatePanel body = getLayer(BODY_LAYER);
		
		// Set the header's position and width (not height)
                if(header != null) {
                    header.setPosition(0, 0);
                    header.setSize(width, header.getHeight());
                }
		//System.out.println(">>> Header: " + width + ":" + header.getHeight());
		
		// Set the trailer (if one exists) position and width (not height)
		if (trailer != null) {
			trailer.setPosition(0, height - trailer.getHeight());
			trailer.setSize(width, trailer.getHeight());
			height -= trailer.getHeight();
			//System.out.println(">>> Trailer: " + width + ":" + trailer.getHeight());
		}

                if(!fullBackground) {
		// Subtract height (must be after trailer check above since height it used)
                    if(header != null) height -= header.getHeight();
		
		// Set the backgrounds position and size
                    background.setPosition(0, header.getHeight());
                    background.setSize(width, height);
                } else {
                    background.setPosition(0, 0);
                    background.setSize(width, height);
                    
                    if(header != null) height -= header.getHeight();
                }
		///System.out.println(">>> Background: " + width + ":" + height);
		
		// Set the body position and size
		if (body != null) {
                        int hgt = 0;
                        if(header != null) hgt = header.getHeight();
			body.setPosition(0, hgt);
                        if(fullBackground)
                            body.setSize(background.getWidth(), background.getHeight() - hgt);
                        else
                            body.setSize(background.getWidth(), background.getHeight());
			//System.out.println(">>> Body: " + body.getWidth() + ":" + body.getHeight());
                        //System.out.println(">>> Body: " + body.getX() + ":" + body.getY());
		}
	}
}
