/**
 * TrailerPanel.java
 *
 * Description:		Trailer panel to the scoreboard rooms
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.scoreboard.view;

import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.net.URL;

import com.topcoder.client.spectatorApp.FontFactory;
import com.topcoder.client.spectatorApp.SpectatorApp;
import com.topcoder.client.spectatorApp.views.AnimatePanel;
import com.topcoder.client.spectatorApp.views.LayeredPanel;
import com.topcoder.client.spectatorApp.widgets.SDividerLine;
import com.topcoder.client.spectatorApp.widgets.SImage;
import com.topcoder.client.spectatorApp.widgets.SMargin;
import com.topcoder.client.spectatorApp.widgets.STextField;


public class TrailerPanel extends LayeredPanel {
	
    /** Font used for the contest name */
    private static final Font nameFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_CONDENSED, Font.PLAIN, 42);

	/** The contest name field */
	private SDividerLine dividerLine = new SDividerLine(true);
    
	/** The margin around the contest field */
	private SMargin marginField;
    
    /** The left image (may be null if none) */
    private AnimatePanel leftImage;
    
    /** The right image (may be null if none) */
    private AnimatePanel rightImage;
    
    /** Constructs the trailer panel */
    public TrailerPanel(String contestName) {

    	// Create the contest name field
    	marginField = new SMargin(new STextField(contestName, nameFont), new Insets(5,5,5,5));

		// Get the left image (if one exists)
    	URL leftURL = SpectatorApp.class.getResource("lefttrailer.jpg");
    	if(leftURL!=null) {
    		leftImage = new SImage(Toolkit.getDefaultToolkit().getImage(leftURL));
    	}
		
    	// Get the right image (if one exists)
		URL rightURL = SpectatorApp.class.getResource("righttrailer.jpg");
		if(rightURL!=null) {
			rightImage = new SImage(Toolkit.getDefaultToolkit().getImage(rightURL));
		}
		
		// Lay it out in order of drawing (ie text last)
		addLayer(dividerLine);
		if(leftImage!=null) addLayer(leftImage);
		if(rightImage!=null) addLayer(rightImage);
		addLayer(marginField);

		// Set the contest name
		setContestName(contestName);
    }

    /**
     * Gets the contestName
     * @return the contest Name
     */
    public String getContestName() {
        return ((STextField)marginField.getWrappedPanel()).getText();
    }

    /**
     * Sets the contest name
     * @param contestName the name of the contest
     */
    public void setContestName(String contestName) {

    	// Set the name
		((STextField)marginField.getWrappedPanel()).setText(contestName);
		
		// Calculate initial width/height
		int width = Math.max(dividerLine.getWidth(),
					marginField.getWidth()
					+ (leftImage==null ? 0 : leftImage.getWidth())
					+ (rightImage==null ? 0 : rightImage.getWidth()));
				
		int height = dividerLine.getDividerHeight() +
					Math.max(marginField.getHeight(), 
					Math.max(leftImage==null ? 0 : leftImage.getHeight(), 
							 rightImage==null ? 0 : rightImage.getHeight()));		
		
		setSize(width, height);
    }

    /** Overridden */
    public void setSize(int width, int height) {
    	super.setSize(width, height);
    	
    	// Set the divider line size
    	dividerLine.setSize(width, height);
    	
    	// Get the info on the divider
    	height-=dividerLine.getDividerHeight();
    	int pad = dividerLine.isOnTop() ? dividerLine.getDividerHeight() : 0;
    	
    	// Set the image positions
    	if(leftImage!=null) leftImage.setPosition(0, pad);
    	if(rightImage!=null) rightImage.setPosition(width - rightImage.getWidth(), pad);
    	
    	// Set the margin field centered
    	marginField.setPosition((width - marginField.getWidth()) / 2, pad + (height - marginField.getHeight()) / 2);
    }
}
