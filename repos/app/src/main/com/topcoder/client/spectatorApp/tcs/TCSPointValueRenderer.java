/**
 * TCSPointValuePanel.java
 *
 * Description:		Point value panel to show the TCS point totals
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.tcs;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.text.DecimalFormat;

import com.topcoder.client.spectatorApp.FontFactory;
import com.topcoder.client.spectatorApp.widgets.SEmbeddedPanel;
import com.topcoder.client.spectatorApp.widgets.SOutline;
import com.topcoder.client.spectatorApp.widgets.STextField;

public class TCSPointValueRenderer extends SEmbeddedPanel {

    /** Font used for the point value */
    private static final Font pointValueFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_BOLDOBLIQUE, Font.PLAIN, 48);

    /** Constructs the user handle panel */
    public TCSPointValueRenderer(double value, Color backColor) {

    	// Create the text field
		STextField field = new STextField(value<0 ? "" : new DecimalFormat("###0.00").format(value), pointValueFont);
		field.setJustification(STextField.RIGHT);

		// Create the outline
		SOutline outline = new SOutline(field, new Insets(3,10,3,10));
		outline.setBackColor(backColor);
		outline.setFillBox(true);
		outline.setBackgroundComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .5f));
		outline.setResizePanel(true);
		
		// Set the outline
		setEmbeddedPanel(outline);
    }


}
