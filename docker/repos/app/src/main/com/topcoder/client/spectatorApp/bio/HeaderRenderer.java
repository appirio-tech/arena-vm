/**
 * HeaderRenderer.java
 *
 * Description:		Renders the header for the spectator bio
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

public class HeaderRenderer extends SLayeredDividerLine {

	/** Font used for the title */
	private static Font font = FontFactory.getInstance().getFont(FontFactory.FUTURA_HEAVYOBLIQUE, Font.PLAIN, 48);
	
	/** Constructs the trailer panel */
    public HeaderRenderer(String roomTitle, Image img) {

    	// Build the layered panel from a text field (with margins) on the left
    	// and the image field (with margins) on the right
    	super(false, 
    				roomTitle==null ? null : new SMargin(
    						new STextField(roomTitle, font), 
							new Insets(5,20,5,20)), 
				    null,
    				img==null ? null : new SMargin(
    						new SImage(img), 
							new Insets(5,20,5,20))); 
    }
}
