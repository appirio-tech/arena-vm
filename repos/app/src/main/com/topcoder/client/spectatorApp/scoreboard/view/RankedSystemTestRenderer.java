/**
 * CodingPhaseRenderer renders for more than 4 people Description: Coding Phase
 * renderer
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
package com.topcoder.client.spectatorApp.scoreboard.view;

import java.awt.Font;
import java.awt.Insets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.topcoder.client.spectatorApp.FontFactory;
import com.topcoder.client.spectatorApp.scoreboard.model.PlacementChangeEvent;
import com.topcoder.client.spectatorApp.scoreboard.model.PlacementChangeListener;
import com.topcoder.client.spectatorApp.scoreboard.model.ScoreboardPointTracker;
import com.topcoder.client.spectatorApp.scoreboard.model.StatusChangeEvent;
import com.topcoder.client.spectatorApp.scoreboard.model.StatusChangeListener;
import com.topcoder.client.spectatorApp.views.AnimatePanel;
import com.topcoder.client.spectatorApp.widgets.SBox;
import com.topcoder.client.spectatorApp.widgets.SDimmerFilter;
import com.topcoder.client.spectatorApp.widgets.SEmbeddedPanel;
import com.topcoder.client.spectatorApp.widgets.SGridCell;
import com.topcoder.client.spectatorApp.widgets.SGridSizingPolicy;
import com.topcoder.client.spectatorApp.widgets.SGridSpacingPolicy;
import com.topcoder.client.spectatorApp.widgets.SOutline;
import com.topcoder.client.spectatorApp.widgets.SPage;
import com.topcoder.client.spectatorApp.widgets.STable;
import com.topcoder.client.spectatorApp.widgets.STextField;
import com.topcoder.shared.netCommon.messages.spectator.CoderRoomData;

public class RankedSystemTestRenderer extends SPage {
	/** Reference to the logging category */
	//private static final Category cat = Category.getInstance(RankedSystemTestRenderer.class.getName());

	/** Font used for "Point Value" title */
	//private static final Font pointValueTitleFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_BOLD, Font.PLAIN, 18);

	/** The header font */
	private static final Font headerFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_HEAVY, Font.PLAIN, 30);

	/** The body panel */
	private BodyPanel bodyPanel;

	/** The row cells by coder id*/
	private Map<Integer, SGridCell[]> cellsByCoderID = new HashMap<Integer, SGridCell[]>();

	/** The point tracker */
	protected ScoreboardPointTracker tracker;

	/** Switch telling us whether to relayout the screen or not */
	private boolean relayout = false;
	
	/** The change handle */
	private PlacementChangeListener placementHandler = new PlacementChangeListener() {
		public void placementChanged(PlacementChangeEvent evt) {
			relayout = true;
		}		
	};
	
	/** The status handler */
	private StatusChangeListener statusHandler = new StatusChangeListener() {
		public void updateStatus(StatusChangeEvent evt) {
			relayout = true;
		}
	};
	
	/** The underlying table */
	protected STable table;

	/** Setup the user handle outline */
	private SOutline currentTestingOutline;
	
	/** Constructs the panel */
	public RankedSystemTestRenderer(String roomTitle, String contestName, ScoreboardPointTracker pointTracker) {
		super(new HeaderPanel(roomTitle), null, null);
		
		// Save a reference to the point tracker
		tracker = pointTracker;
		tracker.getPlacementTracker().getChangeSupport().addListener(placementHandler);
		tracker.addStatusChangeListener(statusHandler);
		
		// Set the body panel
		bodyPanel = new BodyPanel(pointTracker);
		addLayer(bodyPanel);
		
		// Setup the testing outline
		// Note: make really wide to go beyond screen limits
		currentTestingOutline = new SOutline(null, new Insets(5, 500, 5, 500));
		currentTestingOutline.setFillBox(true);
		
		// Force an animation to start things off
		relayout=true;
		this.animate(0, 0);
		
	}

	@Override
	public void dispose() {
		tracker.getPlacementTracker().getChangeSupport().removeListener(placementHandler);
		tracker.removeStatusChangeListener(statusHandler);
		super.dispose();
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

	protected void layoutRanks()	{
		// Get the ranked cells and set the rows
		RankedCells rc = getRankedCells();
		table.setRows(rc.cells);
		
		// Clear all outlines and filters
		table.clearRowOutlines();
		table.clearRowFilters();
		
		// Loop through and set outlines/filters
		for (int r = 0; r < rc.coderRoomData.length; r++) {
			int idx = tracker.indexOfCoder(rc.coderRoomData[r].getCoderID());
			if (idx >= 0 && tracker.isCoderSystemTesting(idx)) {
				table.setRowOutline(r, currentTestingOutline);
			} else {
				if (idx < 0 || !tracker.hasProblemPoints(idx)) {
					table.setRowFilter(r, SDimmerFilter.INSTANCE);
				}
			}			
		}

		// Layout table
		table.doLayout();
	}
	
	public void setSize(int width, int height) {
		super.setSize(width, height);
		AnimatePanel background = getLayer(BACKGROUND_LAYER);
		bodyPanel.setPosition(background.getX(), background.getY());
		bodyPanel.setSize(width, background.getHeight());
	}
	
	protected RankedCells getRankedCells()
	{
		List<CoderRoomData> coders = tracker.getPlacementTracker().getCodersByPlacement();
		RankedCells rc = new RankedCells(coders.size());
		for (int r = coders.size() - 1; r >= 0; r--) {
			rc.coderRoomData[r] = (CoderRoomData) coders.get(r);
 			rc.cells[r] = cellsByCoderID.get(coders.get(r).getCoderID());	
		}
		return rc;
	}
	
	class RankedCells {
		SGridCell[][] cells;
		CoderRoomData[] coderRoomData;
		public RankedCells(int size) {
			this.cells = new SGridCell[size][];
			coderRoomData = new CoderRoomData[size];
		}
	}

	class BodyPanel extends SEmbeddedPanel {
		public BodyPanel(ScoreboardPointTracker pointTracker) {
			// Create the table
			table = new STable();
			
			// Get the sizes of things
			int rowSize = pointTracker.getCoderCount();
			int colSize = pointTracker.getProblemCount();
			
			// Create the array items
			UserPlacePanel[] placePanels = new UserPlacePanel[rowSize];
			TotalValuePanel[] totalPanels = new TotalValuePanel[rowSize];
			SystemTestValuePanel[][] pointPanels = new SystemTestValuePanel[rowSize][colSize];
			
			// Create the column header panels (+2 for user and totals)
			AnimatePanel[] columnHeaderPanels = new AnimatePanel[colSize + 2];
			
			int headerSize = 0;
			int[] colWidth = new int[colSize + 2];
			
			// Create the column panels
			for (int c = 0; c < colSize; c++) {
				STextField field = new STextField("LEVEL " + (c + 1), headerFont);
				field.setJustification(STextField.CENTER);
				SBox box = new SBox(field, new Insets(1, 3, 1, 3));
				box.setResizePanel(true);
				columnHeaderPanels[c + 1] = box;
				headerSize = Math.max(headerSize, box.getHeight());
				colWidth[c+1] = Math.max(colWidth[c+1], box.getWidth());
			}
			table.setColumnHeadings(columnHeaderPanels);
			
			// Create the user panels and the total panels
			for (int r = 0; r < rowSize; r++) {
				placePanels[r] = new UserPlacePanel(pointTracker, pointTracker.getCoder(r).getCoderID());
				colWidth[0] = Math.max(colWidth[0], placePanels[r].getWidth());

				totalPanels[r] = new TotalValuePanel(pointTracker, pointTracker.getCoder(r).getCoderID());
				colWidth[colWidth.length - 1] = Math.max(colWidth[colWidth.length - 1], totalPanels[r].getWidth());
			}
			
			// Create the individual data panels
			for (int r = 0; r < rowSize; r++) {
				for (int c = 0; c < colSize; c++) {
					pointPanels[r][c] = new SystemTestValuePanel(pointTracker, pointTracker.getCoder(r).getCoderID(), pointTracker.getProblem(c).getProblemID());
					colWidth[c+1] = Math.max(colWidth[c+1], pointPanels[r][c].getWidth());
				}
			}
			
			// Add each row to the table
			int rowHeight = 0, userPanelWidth=0;
			for (int r = 0; r < rowSize; r++) {
				SGridCell[] cells = new SGridCell[colSize + 2];
				cells[0] = new SGridCell(placePanels[r], SGridCell.VERTICAL_ALIGNMENT_WEST, SGridCell.HORIZONTAL);
				rowHeight = Math.max(rowHeight, cells[0].getHeight());
				userPanelWidth = Math.max(userPanelWidth, cells[0].getWidth());

				for (int c = 0; c < colSize; c++) {
					cells[c + 1] = new SGridCell(pointPanels[r][c], SGridCell.VERTICAL_ALIGNMENT, SGridCell.NONE);
					rowHeight = Math.max(rowHeight, cells[c + 1].getHeight());
				}
				
				cells[cells.length - 1] = new SGridCell(totalPanels[r], SGridCell.VERTICAL_ALIGNMENT_EAST, SGridCell.NONE);
				rowHeight = Math.max(rowHeight, cells[cells.length - 1].getHeight());
				
				// Add the row to the lookup
				cellsByCoderID.put(placePanels[r].getCoderID(), cells);
			}
			
			// Reset all the user panels to the same size
			// Set the user handle to the max size
			for (SGridCell[] cells : cellsByCoderID.values()) {
				cells[0].setSize(userPanelWidth, cells[0].getHeight());
			}
			
			// Setup the cells
			table.setRows(getRankedCells().cells);
			
			// Setup the policies
			table.setRowSpacingPolicy(new SGridSpacingPolicy.HeaderSpacingPolicy(10, new SGridSpacingPolicy.PercentageSpacingPolicy(1.0)));
			table.setColumnSpacingPolicy(new SGridSpacingPolicy.EdgePercentageSpacingPolicy(1.0, .05));
			table.setRowSizingPolicy(new SGridSizingPolicy.HeaderSizingPolicy(headerSize, new SGridSizingPolicy.FixedSizingPolicy(rowHeight)));
			table.setColumnSizingPolicy(new SGridSizingPolicy.PointGridLayout(colWidth));
			
			// Setup the user handle outline (make left/right insets huge to cover
			// whole screen)
			SOutline outline = new SOutline(null, new Insets(20, 5, 10, 5));
			outline.setFillBox(true);
			table.setColumnOutline(0, outline);
			
			// Make the current width/height equal to the minimum
			setSize(table.getWidth(), table.getHeight());
			
			// Embed the table
			setEmbeddedPanel(table);
		}
	}
}
