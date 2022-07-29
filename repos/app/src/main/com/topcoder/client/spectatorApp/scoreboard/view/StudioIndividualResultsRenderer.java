/**
 * CodingPhaseRenderer renders for more than 4 people Description: Coding Phase
 * renderer
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
package com.topcoder.client.spectatorApp.scoreboard.view;

import com.topcoder.client.spectatorApp.CommonRoutines;
import com.topcoder.client.spectatorApp.widgets.SImage;
import com.topcoder.client.spectatorApp.widgets.STextField;
import java.awt.Font;
import com.topcoder.client.spectatorApp.FontFactory;
import com.topcoder.client.spectatorApp.views.LayeredPanel;import com.topcoder.client.spectatorApp.widgets.SOutline;


import com.topcoder.client.spectatorApp.widgets.SPage;import com.topcoder.client.spectatorApp.widgets.SStudioBackground;

import com.topcoder.client.spectatorApp.widgets.STable;import java.awt.AlphaComposite;
import java.awt.Color;

import java.awt.Image;import java.awt.Insets;

import java.awt.Toolkit;

public class StudioIndividualResultsRenderer extends SPage {
	private static final Font handleFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_BOLD, Font.PLAIN, 36);

	/** The body panel */
	private BodyPanel bodyPanel;
        
        private String caption;
	private byte[] image;
        
	/** Constructs the panel */
	public StudioIndividualResultsRenderer(String caption, byte[] image) {
		super(null, null, new SStudioBackground() , null, true);
                this.caption = caption;
                this.image = image;
		
		// Set the body panel
		bodyPanel = new BodyPanel();
		setLayer(BODY_LAYER, bodyPanel);
                //addLayer(bodyPanel);
		// Force an animation to start things off
		this.animate(0, 0);		
	}

	@Override
	public void dispose() {
		super.dispose();
	}
	
	public void animate(long now, long diff) {
		// Now do the animation for everyone (must follow the table.setRows
		// because it will relayout in the animate
		super.animate(now, diff);		
	}

	public void setSize(int width, int height) {
		super.setSize(width, height);
		
		//AnimatePanel background = getLayer(BACKGROUND_LAYER);
		//bodyPanel.setPosition(background.getX(), background.getY());
		//bodyPanel.setSize(width, background.getHeight());
	}

	class BodyPanel extends LayeredPanel {
                private static final int IMAGE_LAYER = 0;
                private static final int OUTLINE_LAYER = 1;
                
		public BodyPanel() {
			// Create the table
			
                        int rowHeight = 0;
                        int colWidth = 0;
			// Create the array items
                        
			Image[] images = new Image[1];
                        images[0] = Toolkit.getDefaultToolkit().createImage(image);
			CommonRoutines.loadImagesFully(images);

                        SImage img = new SImage(images[0]); 
                        STextField text = new STextField(caption, handleFont);
                        int width = text.getWidth();
                        
                        SOutline outline = new SOutline(text, new Insets(5, (1365 - width) / 2, 5, (1365 - width) / 2));
                        outline.setFillBox(true);
                        outline.setBackColor(Color.BLACK);
                        outline.setBackgroundComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .75f));
		
			
                        //cells[1][0] = new SGridCell(new STextField(handles[0]), SGridCell.CENTER, SGridCell.NONE);
                        
                        //SImage rightBackground = new SImage(Toolkit.getDefaultToolkit().getImage(SpectatorApp.class.getResource("tourneyLogo.png")));
                        
			// Layer'em
			addLayer(img);
                        addLayer(outline);
			
			// Add the vertical line
			
			// Make the current width/height equal to the minimum
			//setSize(table.getWidth(), table.getHeight());
		}

		public void setSize(int width, int height) {
			super.setSize(width, height);
			// Get the layers
			SImage img = (SImage) getLayer(IMAGE_LAYER);
                        SOutline outline = (SOutline) getLayer(OUTLINE_LAYER);
                        
			
			// Set the table size
                        
			img.setPosition((width - img.getWidth()) / 2, (height - img.getHeight()) / 2);
                        outline.setPosition(0, height /2);
			//table.setSize(width, height);

		}
		
		/** This function tries to normalize the differences between SGridLayout and STable
		 * (STable.getRowCount doesn't include header, SGridLayout.getRowStart/End does include it
		 * @param table the table to evaluate
		 * @param row the row (excluding header) to evaluate
		 * @return a 2 digit array representing on either side of the specified row 
		 */ 
		private int[] getRowPos(STable table, int row) {
			int[] rc = new int[2];
			int rowTotal = table.getRowCount(); // adjust for header
			
                        if(row == -1)
                            return new int[] { 0,0 };
			// Handle first row
			if (row <= 1) {
				int spacing = getMidpointBetween(table, row, row+1);
				rc[0] = table.getRowStart(row) - spacing;
				rc[1] = table.getRowEnd(row) + spacing;
			} else if (row < rowTotal) {
				rc[0] = table.getRowStart(row) - getMidpointBetween(table, row - 1, row);
				rc[1] = table.getRowEnd(row) + getMidpointBetween(table, row, row + 1);
			} else {
				int spacing = getMidpointBetween(table, row-1, row);
				rc[0] = table.getRowStart(row) - spacing;
				rc[1] = table.getRowEnd(row) + spacing;
			}
			
			return rc;
		}
		
		/**
		 * Gets the spacing between two rows
		 * @param table the table to use
		 * @param firstRow the first row (including header)
		 * @param secondRow the second row (including header)
		 * @return the midpoint between the rows
		 */
		private int getMidpointBetween(STable table, int firstRow, int secondRow) {
			int rowTotal = table.getRowCount() + 1; // adjust for header
			if (firstRow < 0 || secondRow < 0 || firstRow >= rowTotal || secondRow >= rowTotal) {
				return 3;
			}
			return (int) Math.round((table.getRowStart(secondRow) - table.getRowEnd(firstRow)) / 2.0);
		}
	}
}
