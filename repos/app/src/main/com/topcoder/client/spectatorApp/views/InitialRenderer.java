/**
 * InitialRenderer.java
 *
 * Description:		The initial panel that is displayed
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.views;

import java.awt.Color;
import java.awt.Graphics2D;



public class InitialRenderer extends LayeredPanel implements AnimatePanel {

    /** Constructs the initial renderer */
    public InitialRenderer() {
		addLayer(new BackgroundPanel());
		addLayer(new TopCoderEmblem());
    }

	public void dispose() {
		AnimatePanel[] panels = this.getLayers();
		for(int x=0;x<panels.length;x++) panels[x].dispose();
	}
	
	public void setSize(int width, int height) {
		super.setSize(width, height);
		
		getLayer(0).setPosition(0,0);
		getLayer(0).setSize(width, height);
		
		AnimatePanel panel = getLayer(1);
		panel.setPosition((width / 2) - (panel.getWidth() / 2), (height / 2) - (panel.getHeight() / 2));
	}
	
	class BackgroundPanel extends VolatileImagePanel {
	    /**
	     * Render a black background
	     */
	    public void drawImage(Graphics2D g2D) {
	        // Paint the back buffer black
	        g2D.setPaint(Color.black);
	        g2D.fillRect(0, 0, getWidth(), getHeight());
	    }
	}


}
