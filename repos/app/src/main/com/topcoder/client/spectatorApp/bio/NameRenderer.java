/**
 * NameRenderer.java
 *
 * Description:		Renders the header for the spectator bio
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.bio;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;

import com.topcoder.client.spectatorApp.FontFactory;
import com.topcoder.client.spectatorApp.widgets.SBox;
import com.topcoder.client.spectatorApp.widgets.SEmbeddedPanel;
import com.topcoder.client.spectatorApp.widgets.SGridCell;
import com.topcoder.client.spectatorApp.widgets.SGridLayout;
import com.topcoder.client.spectatorApp.widgets.SGridSizingPolicy;
import com.topcoder.client.spectatorApp.widgets.SMargin;
import com.topcoder.client.spectatorApp.widgets.STextField;

public class NameRenderer extends SEmbeddedPanel {

    /** Font used */
    private static final Font titleFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_BOLD, Font.PLAIN, 30);

    /** Font used */
    private static Font textFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_BOLD, Font.PLAIN, 36);

    /** Color of the titles */
    private static final Color titleColor = new Color(153, 153, 153);

    /** Color of the text */
    private static final Color textColor = Color.white;


    /** Constructs the trailer panel */
    public NameRenderer(String coderName, String coderType) {
    	
    	super();
    	
    	SMargin nameTitle = new SMargin(new STextField("Name:", titleColor, titleFont), new Insets(0,15,0,15));
    	SMargin name = new SMargin(new STextField(coderName, textColor, textFont), new Insets(0,15,0,15));
    	SMargin typeTitle = new SMargin(new STextField("Coder Type:", titleColor, titleFont), new Insets(0,15,0,15));
        SMargin type = new SMargin(new STextField(coderType, textColor, textFont), new Insets(0,15,0,15));

        SGridLayout layout = new SGridLayout();
    	layout.setColumnSizingPolicy(SGridSizingPolicy.RIGHT_SIZING);
		layout.setGrid(new SGridCell[][] {
    	    	{ new SGridCell(nameTitle, SGridCell.VERTICAL_ALIGNMENT_EAST, SGridCell.NONE),
				  new SGridCell(name,SGridCell.VERTICAL_ALIGNMENT_WEST, SGridCell.NONE)
				}, 
    	    	{ new SGridCell(typeTitle,SGridCell.VERTICAL_ALIGNMENT_EAST, SGridCell.NONE),
    	    	  new SGridCell(type,SGridCell.VERTICAL_ALIGNMENT_WEST, SGridCell.NONE)
				} 
    	});
    	
		// Create the box and embed it
    	SBox box = new SBox(layout);
    	setEmbeddedPanel(box);
    }
}
