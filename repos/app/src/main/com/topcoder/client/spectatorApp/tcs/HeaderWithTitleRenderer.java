/**
 * HeaderWithTitleRenderer.java
 *
 * Description:		Header panel to the TCS screens that include a title
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.tcs;

import java.awt.Font;
import java.awt.Toolkit;

import com.topcoder.client.spectatorApp.FontFactory;
import com.topcoder.client.spectatorApp.SpectatorApp;
import com.topcoder.client.spectatorApp.widgets.SImage;
import com.topcoder.client.spectatorApp.widgets.STextField;


public class HeaderWithTitleRenderer extends HeaderRenderer {

    /** Font used for the descriptions */
    private static Font descFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_HEAVYOBLIQUE, Font.PLAIN, 48);
	
    /** Constructor
     *
     * @param roundTitle the title of the round
     * @param headerTitle the header title that will appear on the right side
     */
    public HeaderWithTitleRenderer(String roundTitle, String headerTitle) {
    	// Create the header from teh round title
    	super(roundTitle, new SImage(Toolkit.getDefaultToolkit().getImage(SpectatorApp.class.getResource("rightheadersolid.jpg"))));
    	
    	// Create the header title field (justified right)
    	STextField header = new STextField(headerTitle, descFont);
    	header.setJustification(STextField.RIGHT);
    	
    	// Add the layout on top of the divider line
    	addLayer(header);
	}
	
}
