/**
 * HeaderRenderer.java
 *
 * Description:		Renders the header for the TCS screens
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.tcs;

import java.awt.Font;
import java.awt.Insets;

import com.topcoder.client.spectatorApp.FontFactory;
import com.topcoder.client.spectatorApp.widgets.SImage;
import com.topcoder.client.spectatorApp.widgets.SLayeredDividerLine;
import com.topcoder.client.spectatorApp.widgets.SMargin;
import com.topcoder.client.spectatorApp.widgets.STextField;

public class HeaderRenderer extends SLayeredDividerLine {

    /** Constructs the trailer panel */
    public HeaderRenderer(String roomTitle) {

    	// Build the layered panel from a text field (with margins) on the left
    	super(false, 
    		  new SMargin(
    		     new STextField(roomTitle, FontFactory.getInstance().getFont(FontFactory.FUTURA_HEAVYOBLIQUE, Font.PLAIN, 48)), 
				 new Insets(5,20,5,20)),
		      null, 
    		  null);
    }

    /** Constructs the trailer panel */
    public HeaderRenderer(String roomTitle, SImage image) {

    	// Build the layered panel from a text field (with margins) on the left
    	// and the image field on the right
    	super(false, 
    		  new SMargin(
    		     new STextField(roomTitle, FontFactory.getInstance().getFont(FontFactory.FUTURA_HEAVYOBLIQUE, Font.PLAIN, 48)), 
				 new Insets(5,20,5,20)),
		      null, 
    		  image);
    }

}
