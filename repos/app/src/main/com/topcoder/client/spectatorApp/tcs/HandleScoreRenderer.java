/**
 * HandleRenderer.java
 *
 * Description:		Renders the handle of a TCS Coder
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.tcs;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.text.DecimalFormat;

import com.topcoder.client.spectatorApp.FontFactory;
import com.topcoder.client.spectatorApp.widgets.SBox;
import com.topcoder.client.spectatorApp.widgets.SEmbeddedPanel;
import com.topcoder.client.spectatorApp.widgets.SGridCell;
import com.topcoder.client.spectatorApp.widgets.SGridLayout;
import com.topcoder.client.spectatorApp.widgets.STextField;

public class HandleScoreRenderer extends SEmbeddedPanel  {

    /** Font used */
    private static final Font descFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_BOLD, Font.PLAIN, 24);

    /** Constructs the trailer panel */
    public HandleScoreRenderer(String handle, double points, Color color) {

    	// Format
		DecimalFormat frm = new DecimalFormat("##,##0.00");

		// The rows
	    String[] desc = { handle, frm.format(points) };
	    Color[] descColor = { color, Color.white };
	    
        // Create the title grid descriptors
		SGridCell[][] descriptors = new SGridCell[desc.length][1];
		for(int x=0;x<desc.length;x++) {
			STextField descField = new STextField(desc[x], descColor[x], descFont);
			descriptors[x][0] = new SGridCell(descField, SGridCell.CENTER, SGridCell.NONE);
		}
		SGridLayout layout = new SGridLayout(descriptors);
		
		// Create the title grid and embed it
		SBox box = new SBox(layout, new Insets(5,15,5,15));
		box.setResizePanel(true);
		
		// Set the title
		setEmbeddedPanel(box);

    }
}
