/**
 * TCSTotalValuePanel.java
 *
 * Description:		Total value panel to the tcs display
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.tcs;

import java.awt.Color;
import java.awt.Font;
import java.text.DecimalFormat;

import com.topcoder.client.spectatorApp.FontFactory;
import com.topcoder.client.spectatorApp.widgets.STextField;

public class TCSTotalValueRenderer extends STextField {

    /** Font used for the point value */
    private static final Font totalValueFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_BOLDOBLIQUE, Font.PLAIN, 48);
 
    /** Color used for the total */
    private static final Color textColor = Color.white;

    /** Constructs the user handle panel */
    public TCSTotalValueRenderer(double value) {

    	super(textColor, totalValueFont);
    	
        // Save the point value text
        setText(value<0 ? "" : new DecimalFormat("###0.00").format(value));
    }
}
