/**
 * Renfers the result of misc rounds, using data and columns input manually.
 * This is useful when showing some static data (not requiring to retrieve data from servers, etc), such as
 * the final result of design, development, studio, marathon, etc.
 * 
 * @author visualage
 * @version 1.0
 */
package com.topcoder.client.spectatorApp.scoreboard.view;

import java.awt.Font;
import java.awt.Insets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.topcoder.client.spectatorApp.FontFactory;
import com.topcoder.client.spectatorApp.netClient.SpectatorEventProcessor;
import com.topcoder.client.spectatorApp.messages.TCSCoderInfo;
import com.topcoder.client.spectatorApp.views.AnimatePanel;
import com.topcoder.client.spectatorApp.views.LayeredPanel;
import com.topcoder.client.spectatorApp.widgets.SBox;
import com.topcoder.client.spectatorApp.widgets.SGridCell;
import com.topcoder.client.spectatorApp.widgets.SGridSizingPolicy;
import com.topcoder.client.spectatorApp.widgets.SGridSpacingPolicy;
import com.topcoder.client.spectatorApp.widgets.SMargin;
import com.topcoder.client.spectatorApp.widgets.SOutline;
import com.topcoder.client.spectatorApp.widgets.SPage;
import com.topcoder.client.spectatorApp.widgets.STable;
import com.topcoder.client.spectatorApp.widgets.STextField;
import com.topcoder.client.spectatorApp.widgets.SDimmerFilter;
import com.topcoder.client.spectatorApp.event.AnnounceTableResultsEvent;
import com.topcoder.client.spectatorApp.event.ShowPlacementListener;
import com.topcoder.client.spectatorApp.event.ShowPlacementEvent;
import com.topcoder.client.spectatorApp.event.ShowTCSPlacementEvent;

public class AnnounceResultTablePanel extends SPage {
	/** The header font */
	private static final Font headerFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_BOLD, Font.PLAIN, 36);

	/** The body panel */
	private BodyPanel bodyPanel;
	
	private int[] showPlacement;
	
	private SGridCell[][] cells;
	
	private ShowPlacementListener placementListener = new ShowPlacementListener() {
        public void showPlacements(ShowPlacementEvent evt) {
            showPlacement = evt.getShowPlacements();
            
            relayout = true;
        }
        
        public void showTCSPlacements(ShowTCSPlacementEvent evt) {
        }
	};
	
	private boolean relayout;
	
	private final boolean[] highlights;
	
	/** Constructs the panel */
	public AnnounceResultTablePanel(AnnounceTableResultsEvent evt, String roundName, String contestName) {
        super(new HeaderPanel(roundName), null, null);

        SpectatorEventProcessor.getInstance().addShowPlacementListener(placementListener);
        showPlacement = SpectatorEventProcessor.getInstance().getShowPlacements();
        
        if (showPlacement == null) {
            // When there is now previous show placement message, show all coders.
            // Assign initial show placements. Show all coders
            showPlacement = new int[evt.getCoders().length];
        
            for (int i=0;i<showPlacement.length;++i) {
                showPlacement[i] = i + 1;
            }
        }
        
        highlights = evt.getHighlights();
        
        bodyPanel = new BodyPanel(evt);
        addLayer(bodyPanel);
        
        relayout = true;
        animate(0, 0);
    }
    
	public void animate(long now, long diff) {
		if (relayout) {
			relayout = false;
			layoutRanks();
		}
		
		// Now do the animation for everyone (must follow the table.setRows
		// because it will relayout in the animate
		super.animate(now, diff);		
	}
    
    public void dispose() {
        SpectatorEventProcessor.getInstance().removeShowPlacementListener(placementListener);
        super.dispose();
    }

	public void setSize(int width, int height) {
		super.setSize(width, height);
		
		AnimatePanel background = getLayer(BACKGROUND_LAYER);
		bodyPanel.setPosition(background.getX(), background.getY());
		bodyPanel.setSize(width, background.getHeight());
	}
	
	private void layoutRanks() {
		STable table = (STable) bodyPanel.getLayer(BodyPanel.TABLE_LAYER);
		table.setRows(getRankedCells());
		
		table.clearRowOutlines();
		table.clearRowFilters();
		
		for (int r = showPlacement.length - 1; r >= 0; r--) {
            // If the coder requires dim
            if (!highlights[showPlacement[r] - 1]) {
				table.setRowFilter(r, SDimmerFilter.INSTANCE);
            }
		}
		
		table.doLayout();
	}

	private SGridCell[][] getRankedCells()
	{
		SGridCell[][] newCells = new SGridCell[showPlacement.length][];
		for (int r = showPlacement.length - 1; r >= 0; r--) {
			newCells[r] = cells[showPlacement[r] - 1];
		}
		return newCells;
	}
	
	class BodyPanel extends LayeredPanel {
		/** The table layer */
		private static final int TABLE_LAYER = 0;
		
		public BodyPanel(AnnounceTableResultsEvent evt) {
			// Create the table
			STable table = new STable();

            TCSCoderInfo[] coders = evt.getCoders();
            String[] headers = evt.getColumnHeaders();
            String[][] scores = evt.getScores();
            int[] ranks = evt.getRanks();
			int rowSize = coders.length;
			int colSize = headers.length;
			ResultTableCell[][] cellPanels = new ResultTableCell[rowSize][colSize];
			ResultTableUserCell[] coderPanels = new ResultTableUserCell[rowSize];

			AnimatePanel[] columnHeaderPanels = new AnimatePanel[colSize + 1];
			
			int headerSize = 0;
			int[] colWidth = new int[colSize + 1];
			
			// Create the column panels
			for (int c = 0; c < colSize; c++) {
				STextField field = new STextField(headers[c], headerFont);
				field.setJustification(STextField.CENTER);
				SBox box = new SBox(field, new Insets(1, 3, 1, 3));
				box.setResizePanel(true);
				columnHeaderPanels[c + 1] = box;
				colWidth[c+1] = Math.max(colWidth[c+1], box.getWidth());
				headerSize = Math.max(headerSize, box.getHeight());
			}
			table.setColumnHeadings(columnHeaderPanels);
			
			// Create the user panels
			for (int r = 0; r < rowSize; r++) {
				coderPanels[r] = new ResultTableUserCell(ranks[r], coders[r]);
				colWidth[0] = Math.max(colWidth[0], coderPanels[r].getWidth());
			}
			
			// Create the individual data panels
			for (int r = 0; r < rowSize; r++) {
				for (int c = 0; c < colSize; c++) {
					cellPanels[r][c] = new ResultTableCell(scores[r][c]);
					colWidth[c+1] = Math.max(colWidth[c+1], cellPanels[r][c].getWidth());
				}
			}
			
			// Add each row to the table
			int rowHeight = 0;
			int minUserWidth = 0;

            cells = new SGridCell[rowSize][];
			
			for (int r = 0; r < rowSize; r++) {
				SGridCell[] rowCells = new SGridCell[colSize + 1];
				rowCells[0] = new SGridCell(coderPanels[r], SGridCell.VERTICAL_ALIGNMENT_WEST, SGridCell.HORIZONTAL);
				rowHeight = Math.max(rowHeight, rowCells[0].getHeight());
				minUserWidth = Math.max(minUserWidth, rowCells[0].getWidth());
				
				for (int c = 0; c < colSize; c++) {
					rowCells[c + 1] = new SGridCell(cellPanels[r][c], SGridCell.VERTICAL_ALIGNMENT, SGridCell.NONE);
					rowHeight = Math.max(rowHeight, rowCells[c + 1].getHeight());
				}
				
				cells[r] = rowCells;
			}

			// Set the table rows
			table.setRows(getRankedCells());
			
			// Setup the policies
			table.setRowSpacingPolicy(new SGridSpacingPolicy.HeaderSpacingPolicy(10, new SGridSpacingPolicy.PercentageSpacingPolicy(1.0)));
			table.setColumnSpacingPolicy(new SGridSpacingPolicy.EdgePercentageSpacingPolicy(1.0, .05));
			table.setRowSizingPolicy(new SGridSizingPolicy.HeaderSizingPolicy(headerSize, new SGridSizingPolicy.FixedSizingPolicy(rowHeight)));
			table.setColumnSizingPolicy(new SGridSizingPolicy.PointGridLayout(colWidth));
			
			// Setup the user handle outline
			SOutline outline = new SOutline(null, new Insets(15, 5, 5, 5));
			outline.setFillBox(true);
			table.setColumnOutline(0, outline);
		
			// Add the layers
			addLayer(table);
		}

		public void setSize(int width, int height) {
			super.setSize(width, height);
			
			// Get the layers
			STable table = (STable) getLayer(TABLE_LAYER);
			
			// Set the table size
			table.setPosition(0, 0);
			table.setSize(width, height);
		}
    }
}