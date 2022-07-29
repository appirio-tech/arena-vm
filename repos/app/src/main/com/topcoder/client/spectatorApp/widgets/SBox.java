package com.topcoder.client.spectatorApp.widgets;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Insets;
import com.topcoder.client.spectatorApp.views.AnimatePanel;

/**
 * Creates a box with an outline and a black background
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
public class SBox extends SOutline {
	/**
	 * Creates the box embedding the given panel
	 * @param panel the panel to embed
	 */
	public SBox() {
		super();
		makeBox();
	}
	
	/**
	 * Creates the box embedding the given panel
	 * @param panel the panel to embed
	 */
	public SBox(AnimatePanel panel) {
		super(panel);
		makeBox();
	}
	/**
	 * Creates the box embedding the given panel
	 * at the given insets
	 * @param panel the panel to embed
	 * @param insets the insets to use
	 */
	public SBox(AnimatePanel panel, Insets insets) {
		super(panel, insets);
		makeBox();
	}

	/** Sets the panel with insets */
	public void setPanel(AnimatePanel panel, Insets insets) {
		super.setPanel(panel, insets);
	}
	
	/** Makes the outline into a box */
	private void makeBox() {
		setFillBox(true);
		setBackColor(Color.black);
		setOutlineColor(Color.white);
		setBackgroundComposite(AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 1.0f));
		
	}
}
