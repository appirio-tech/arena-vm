package com.topcoder.client.spectatorApp.widgets;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import com.topcoder.client.spectatorApp.views.LayeredPanel;

/**
 * An implementation of the LayeredFilter that will dim the
 * corresponding AnimatePanel
 * 
 * @author Tim Roberts
 * @version 1.0
 */

public class SDimmerFilter implements LayeredPanel.LayeredFilter {
	/** The static instance of this filter */
	public final static SDimmerFilter INSTANCE = new SDimmerFilter();
	
	/** Simply sets the alpha composite */
	public void filter(Graphics2D g2D) {
	  g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .25f));
	}	 
}

