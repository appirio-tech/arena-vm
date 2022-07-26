/**
 * CodingPhaseRenderer renders for more than 4 people Description: Coding Phase
 * renderer
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
package com.topcoder.client.spectatorApp.scoreboard.view;

import com.topcoder.client.spectatorApp.netClient.SpectatorEventProcessor;
import com.topcoder.client.spectatorApp.event.ShowScreenEvent;
import com.topcoder.client.spectatorApp.event.ShowScreenListener;
import com.topcoder.client.spectatorApp.widgets.SReloadingImage;
import com.topcoder.client.spectatorApp.widgets.STextField;
import com.topcoder.client.spectatorApp.views.AnimatePanel;
import java.awt.Font;
import com.topcoder.client.spectatorApp.FontFactory;
import com.topcoder.client.spectatorApp.views.LayeredPanel;
import com.topcoder.client.spectatorApp.widgets.SGridCell;
import com.topcoder.client.spectatorApp.widgets.SGridSizingPolicy;
import com.topcoder.client.spectatorApp.widgets.SGridSpacingPolicy;
import com.topcoder.client.spectatorApp.widgets.SMargin;

import com.topcoder.client.spectatorApp.widgets.SPage;
import com.topcoder.client.spectatorApp.widgets.STable;
import java.awt.Insets;
import java.io.File;

public abstract class AbstractScreenContestRenderer extends SPage {
	private static final Font handleFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_BOLD, Font.PLAIN, 36);

	/** The body panel */
	private BodyPanel bodyPanel;
        
    private String[] computerNames;
	private String[] handles;
    private String path;
    private SGridCell[][] cells;

    private SReloadingImage[] images;        
    private int[] currentScreen = new int[] {0, 1};
    private boolean relayout = true;
        
    private ShowScreenListener listener = new ShowScreenListener() {
        public void showScreens(ShowScreenEvent evt) {
            relayout = true;
            currentScreen = evt.getShowScreens();
        }
    };
        
	/** Constructs the panel */
	protected AbstractScreenContestRenderer(String[] computerNames, String path, String[] handles,
        AnimatePanel header, AnimatePanel background) {
		super(header, null, background, null, true);
        this.computerNames = computerNames;
        this.path = path;
        this.handles = handles;
		
		SpectatorEventProcessor.getInstance().addShowScreenListener(listener);
		
		// Set the body panel
		bodyPanel = new BodyPanel();
		setLayer(BODY_LAYER, bodyPanel);
        //addLayer(bodyPanel);
		// Force an animation to start things off
		this.animate(0, 0);		
	}

	@Override
	public void dispose() {
		SpectatorEventProcessor.getInstance().removeShowScreenListener(listener);
		for (int i=0;i<images.length;++i) images[i].stop();
		
		super.dispose();
	}
	
	public void animate(long now, long diff) {
	    if (relayout) {
            relayout = false;
            layoutScreens();
	    }
	    
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
	
	private void layoutScreens() {
		STable table = (STable) bodyPanel.getLayer(BodyPanel.TABLE_LAYER);
		table.setRows(getScreenCells());
		table.doLayout();
	}
	
	private SGridCell[][] getScreenCells() {
        // Currently only for 2 screens in a row. If need change, add more layout options here
        SGridCell[][] newCells = new SGridCell[2][currentScreen.length];
        for (int i=0;i<images.length;++i) {
            images[i].pause();
        }
        for (int i=0;i<currentScreen.length;++i) {
            images[currentScreen[i]].resume();
            newCells[0][i] = cells[0][currentScreen[i]];
            newCells[1][i] = cells[1][currentScreen[i]];
        }
        
        return newCells;
	}

	class BodyPanel extends LayeredPanel {
        private static final int TABLE_LAYER = 0;
                
		public BodyPanel() {
			// Create the table
			STable table = new STable();
			
                        int rowHeight = 0;
                        int colWidth = 0;
			// Create the array items

			// Set the table rows
            cells = new SGridCell[2][computerNames.length];
            images = new SReloadingImage[computerNames.length];
            
            for (int i=0;i<computerNames.length;++i) {
		images[i] = new SReloadingImage(path + File.separator + computerNames[i] + ".png");
                cells[0][i] = new SGridCell(new SMargin(images[i], new Insets(0,5,0,5)), SGridCell.WEST, SGridCell.HORIZONTAL);
                cells[1][i] = new SGridCell(new STextField(handles[i]), SGridCell.CENTER, SGridCell.NONE);
            }
                        
            colWidth = cells[0][0].getWidth();
            rowHeight = cells[0][0].getHeight();
            //SImage rightBackground = new SImage(Toolkit.getDefaultToolkit().getImage(SpectatorApp.class.getResource("tourneyLogo.png")));
                        
			// Setup the policies
			table.setRowSpacingPolicy(new SGridSpacingPolicy.HeaderSpacingPolicy(10, new SGridSpacingPolicy.PercentageSpacingPolicy(1.0)));
			table.setColumnSpacingPolicy(new SGridSpacingPolicy.EdgePercentageSpacingPolicy(1.0, .05));
			//table.setRowSizingPolicy(new SGridSizingPolicy.FixedSizingPolicy(10));
			table.setColumnSizingPolicy(new SGridSizingPolicy.FixedSizingPolicy(colWidth));
			
			table.setRows(getScreenCells());
			
			// Layer'em
			addLayer(table);
			
			// Add the vertical line
			
			// Make the current width/height equal to the minimum
			setSize(table.getWidth(), table.getHeight());
		}

		public void setSize(int width, int height) {
			super.setSize(width, height);
			// Get the layers
			STable table = (STable) getLayer(TABLE_LAYER);
			
			// Set the table size
			table.setPosition(0, 0);
			table.setSize(width, height);
			
			int[] firstRow = getRowPos(table, 0);
                        //System.out.println(firstRow[1]);
			int[] lastRow = getRowPos(table, table.getRowCount() - 1);
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
