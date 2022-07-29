/**
 * TrailerRenderer.java
 *
 * Description:		Trailer renderer for the tcs screens 
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.tcs;

import java.awt.Font;
import java.awt.Insets;

import com.topcoder.client.spectatorApp.FontFactory;
import com.topcoder.client.spectatorApp.widgets.SLayeredDividerLine;
import com.topcoder.client.spectatorApp.widgets.SMargin;
import com.topcoder.client.spectatorApp.widgets.STextField;


public class TrailerRenderer extends SLayeredDividerLine {

	/** Font used for the contest name */
	private static final Font nameFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_CONDENSED, Font.PLAIN, 42);
	
    /** Constructs the trailer panel */
    public TrailerRenderer(String contestName) {
    	// Setup the trailer with the contest name centered
    	super(true, null, 
    			contestName==null ? null : new SMargin(
    			   new STextField(contestName, nameFont),
				   new Insets(5,0,5,0)), 
			null);
    }
}
