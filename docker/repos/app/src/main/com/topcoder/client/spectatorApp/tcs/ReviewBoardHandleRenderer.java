/**
 * ReviewBoardHandleRenderer
 *
 * Description:		Renders the handles for the review board
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.tcs;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;

import com.topcoder.client.spectatorApp.Constants;
import com.topcoder.client.spectatorApp.FontFactory;
import com.topcoder.client.spectatorApp.widgets.SBox;
import com.topcoder.client.spectatorApp.widgets.SEmbeddedPanel;
import com.topcoder.client.spectatorApp.widgets.STextField;
import com.topcoder.client.spectatorApp.widgets.STitleGrid;
import com.topcoder.client.spectatorApp.widgets.STitleGridCell;

public class ReviewBoardHandleRenderer extends SEmbeddedPanel  {

    /** Font used */
    private static final Font titleFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_BOOK, Font.PLAIN, 21);

    /** Font used */
    private static final Font descFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_BOLD, Font.PLAIN, 21);

    /** Color of the titles */
    private static final Color titleColor = new Color(153, 153, 153);

    /** The titles */
    private static final String[] titles = {"HANDLE:", "TC RATING:", "TCS RATING:"};
    
    /** The descriptions */
    private String[] desc = new String[titles.length];

    /** The Colors for the descriptions */
    private Color[] descColor = {new Color(204, 0, 0), new Color(204, 0, 0), Color.white};

    /** Constructs the trailer panel */
    public ReviewBoardHandleRenderer(String handle, int tcRating, int tcsRating) {
		
        // Get the room title
        desc[0] = handle;
        desc[1] = String.valueOf(tcRating);
        if(tcsRating == 0)
        {
            desc[2] = "NR";
        }
        else
		desc[2] = String.valueOf(tcsRating);

		// Setup colors
		descColor[0] = Constants.getRankColor(tcsRating);
		descColor[1] = Constants.getRankColor(tcRating);   		
		descColor[2] = Constants.getRankColor(tcsRating);   		

        // Create the title grid descriptors
		STitleGridCell[] descriptors = new STitleGridCell[titles.length];
		for(int x=0;x<titles.length;x++) {
			STextField titleField = new STextField(titles[x], titleColor, titleFont);
			STextField descField = new STextField(desc[x], descColor[x], descFont);
			descriptors[x] = new STitleGridCell(titleField, descField);
		}
		
		// Create the title gridl
		STitleGrid titleGrid = new STitleGrid(descriptors);
		
		// Create the box
		SBox box = new SBox(titleGrid, new Insets(5,5,5,5));
		
		// Set the title
		setEmbeddedPanel(box);

    }
}
