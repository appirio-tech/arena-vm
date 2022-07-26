/**
 * TCSReviewerTitleRenderer
 *
 * Description:		Header column panel to display the reviewer name
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.tcs;

//import com.topcoder.client.spectatorApp.scoreboard.model.*;
//import com.topcoder.client.spectatorApp.event.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;

import com.topcoder.client.spectatorApp.FontFactory;
import com.topcoder.client.spectatorApp.widgets.SBox;
import com.topcoder.client.spectatorApp.widgets.STextField;

public class TCSReviewerTitleRenderer extends SBox {

    /** Font used for the point value */
    private static final Font titleFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_HEAVY, Font.PLAIN, 28);

    /** Constructs the title panel panel */
    public TCSReviewerTitleRenderer(String titleText) {
    	super();
    	
    	// Get the field
    	STextField field = new STextField(titleText, Color.white, titleFont);
    	field.setJustification(STextField.CENTER);

        // Embed it
    	setPanel(field, new Insets(2,5,2,5));
    	setResizePanel(true);
    }

}

