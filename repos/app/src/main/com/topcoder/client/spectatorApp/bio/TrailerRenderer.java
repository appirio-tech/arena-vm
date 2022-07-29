/**
 * TrailerRenderer.java
 *
 * Description:		Trailer renderer for the bio
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.bio;

import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;

import com.topcoder.client.spectatorApp.FontFactory;
import com.topcoder.client.spectatorApp.widgets.SImage;
import com.topcoder.client.spectatorApp.widgets.SLayeredDividerLine;
import com.topcoder.client.spectatorApp.widgets.SMargin;
import com.topcoder.client.spectatorApp.widgets.STextField;


public class TrailerRenderer extends SLayeredDividerLine {

    /** Font used for the contest name */
    private static final Font nameFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_CONDENSED, Font.PLAIN, 42);

    /** Constructs the trailer panel */
    public TrailerRenderer(String contestName, Image logo) {
    	// Build the layered panel from a text field (with margins) on the center
    	// and the image field (with margins) on the right
    	super(true, null,
    				contestName==null ? null : new SMargin(
    						new STextField(contestName, nameFont), 
							new Insets(5,15,5,15)), 
    				logo==null ? null : new SMargin(
    						new SImage(logo), 
							new Insets(5,15,5,15))); 
    }

}
