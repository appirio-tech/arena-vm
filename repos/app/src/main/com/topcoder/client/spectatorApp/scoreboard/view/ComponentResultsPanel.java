/**
 * CodingPhaseRenderer renders for more than 4 people Description: Coding Phase
 * renderer
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
package com.topcoder.client.spectatorApp.scoreboard.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.topcoder.client.spectatorApp.FontFactory;
import com.topcoder.client.spectatorApp.scoreboard.model.ComponentContest;
import com.topcoder.client.spectatorApp.scoreboard.model.ComponentRound;
import com.topcoder.client.spectatorApp.scoreboard.model.PlacementChangeEvent;
import com.topcoder.client.spectatorApp.scoreboard.model.PlacementChangeListener;
import com.topcoder.client.spectatorApp.views.AnimatePanel;
import com.topcoder.client.spectatorApp.views.LayeredPanel;
import com.topcoder.client.spectatorApp.widgets.SGridCell;
import com.topcoder.client.spectatorApp.widgets.SGridSizingPolicy;
import com.topcoder.client.spectatorApp.widgets.SGridSpacingPolicy;
import com.topcoder.client.spectatorApp.widgets.SLine;
import com.topcoder.client.spectatorApp.widgets.SMargin;
import com.topcoder.client.spectatorApp.widgets.SOutline;
import com.topcoder.client.spectatorApp.widgets.SPage;
import com.topcoder.client.spectatorApp.widgets.STable;
import com.topcoder.client.spectatorApp.widgets.STextField;
import com.topcoder.shared.netCommon.messages.spectator.ComponentCoder;

public class ComponentResultsPanel extends SPage {
	/** Font used for "Review Board" title */
	private static final Font pointValueTitleFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_BOLD, Font.PLAIN, 18);

	/** The header font */
	private static final Font headerFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_BOOK, Font.PLAIN, 24);

	/** The body panel */
	private BodyPanel bodyPanel;

	/** The row cells by coder id */
	private Map<Integer, SGridCell[]> cellsByCoderID = new HashMap<Integer, SGridCell[]>();

	/** The component contests */
	private ComponentRound contest;

	/** Switch telling us whether to relayout the screen or not */
	private boolean relayout = false;
	
	/** The change handle */
	private PlacementChangeListener placementHandler = new PlacementChangeListener() {
		public void placementChanged(PlacementChangeEvent evt) {
			relayout = true;
		}		
	};
	
	/** Constructs the panel */
	public ComponentResultsPanel(String title, ComponentRound contest) {
		super(new HeaderPanel(title), null, null);
		
		// Save a reference to the point tracker
		this.contest = contest;
		
		// Set the body panel
		bodyPanel = new BodyPanel(contest);
		addLayer(bodyPanel);
		
		contest.getPlacementTracker().getChangeSupport().addListener(placementHandler);

		// Force an animation to start things off
		relayout = true;
		this.animate(0, 0);
	}

	BodyPanel getBodyPanel()
	{
		return bodyPanel;
	}
	
	@Override
	public void dispose() {
		contest.getPlacementTracker().getChangeSupport().removeListener(placementHandler);
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

	private void layoutRanks()	{
		STable table = (STable) bodyPanel.getLayer(bodyPanel.TABLE_LAYER);
		table.setRows(getRankedCells());
		table.doLayout();
		// table.setPosition(x,y);
	}
	
	private SGridCell[][] getRankedCells()
	{
		List<ComponentCoder> coders = contest.getPlacementTracker().getCodersByPlacement();
		SGridCell[][] newCells = new SGridCell[coders.size()][];
		for (int r = coders.size() - 1; r >= 0; r--) {
			newCells[r] = cellsByCoderID.get(coders.get(r).getCoderID());
		}
		return newCells;
	}
	
	public void setSize(int width, int height) {
		super.setSize(width, height);
		
		AnimatePanel background = getLayer(BACKGROUND_LAYER);
		bodyPanel.setPosition(background.getX(), background.getY());
		bodyPanel.setSize(width, background.getHeight());
	}

	class BodyPanel extends LayeredPanel {
		/** The table layer */
		private int TABLE_LAYER;

		/** The vertical line layer */
		private int VERTICALLINE_LAYER;

		public BodyPanel(ComponentRound contest) {
			// Create the table
			STable table = new STable();
			
			List<ComponentCoder> coders = contest.getCoders();
			List<ComponentContest> components = contest.getComponents();
			
			// Get the sizes of things
			final int rowSize = coders.size();
			final int colSize = components.size();
			
			if (rowSize == 0 || colSize == 0) return;
			
			// Create the array items
			UserPlacePanel[] placePanels = new UserPlacePanel[rowSize];
			ComponentContestValue[][] pointPanels = new ComponentContestValue[rowSize][colSize];
			ComponentWagerValue[][] wagerPanels = new ComponentWagerValue[rowSize][colSize];
			ComponentContestTotalValue[] totalPanels = new ComponentContestTotalValue[rowSize];
			
			// Create the column header panels (+2 for user and totals)
			AnimatePanel[] columnHeaderPanels = new AnimatePanel[colSize * 2 + 2];
			
			int headerSize = 0;
			int[] colWidth = new int[colSize * 2 + 2];
			
			// Create the column panels
			for (int c = 0; c < colSize; c++) {
				STextField scoreField = new STextField("Avg. Score", headerFont);
				scoreField.setJustification(STextField.CENTER);
				SMargin scoreMargin = new SMargin(scoreField, new Insets(1, 3, 1, 3));
				columnHeaderPanels[c*2 + 1] = scoreMargin;
				colWidth[c*2 + 1] = Math.max(colWidth[c*2+1], scoreMargin.getWidth());
				headerSize = Math.max(headerSize,scoreMargin.getHeight());

				STextField pointsField = new STextField("Wager", headerFont);
				pointsField.setColor(Color.yellow);
				pointsField.setJustification(STextField.CENTER);
				SMargin pointsMargin = new SMargin(pointsField, new Insets(1, 3, 1, 3));
				columnHeaderPanels[c*2 + 2] = pointsMargin;
				colWidth[c*2 + 2] = Math.max(colWidth[c*2+2], pointsMargin.getWidth());
				headerSize = Math.max(headerSize,pointsMargin.getHeight());

			}
			STextField scoreField = new STextField("Total Pts.", headerFont);
			scoreField.setJustification(STextField.RIGHT);
			SMargin scoreMargin = new SMargin(scoreField, new Insets(1, 3, 1, 3));
			columnHeaderPanels[columnHeaderPanels.length - 1] = scoreMargin;
			
			table.setColumnHeadings(columnHeaderPanels);
			
			// Create the user panels and the total panels
			for (int r = 0; r < rowSize; r++) {
				placePanels[r] = new UserPlacePanel(contest, coders.get(r).getCoderID());
				colWidth[0] = Math.max(colWidth[0], placePanels[r].getWidth());
				
				totalPanels[r] = new ComponentContestTotalValue(contest, r);
				colWidth[colWidth.length - 1] = Math.max(colWidth[colWidth.length - 1], totalPanels[r].getWidth());
			}
			
			// Create the individual data panels
			for (int r = 0; r < rowSize; r++) {
				for (int c = 0; c < colSize; c++) {
					pointPanels[r][c] = new ComponentContestValue(contest.getComponent(c), coders.get(r).getCoderID()); 
					colWidth[c*2+1] = Math.max(colWidth[c*2+1], pointPanels[r][c].getWidth());
					
					wagerPanels[r][c] = new ComponentWagerValue(contest.getComponent(c), coders.get(r).getCoderID()); 
					colWidth[c*2+2] = Math.max(colWidth[c*2+2], wagerPanels[r][c].getWidth());
				}
			}
			
			// Add each row to the table
			int rowHeight = 0;
			int minUserWidth = 0;
			
			for (int r = 0; r < rowSize; r++) {
				SGridCell[] cells = new SGridCell[colSize*2 + 2];
				cells[0] = new SGridCell(placePanels[r], SGridCell.VERTICAL_ALIGNMENT_WEST, SGridCell.HORIZONTAL);
				rowHeight = Math.max(rowHeight, cells[0].getHeight());
				minUserWidth = Math.max(minUserWidth, cells[0].getWidth());
				
				for (int c = 0; c < colSize; c++) {
					cells[c*2 + 1] = new SGridCell(pointPanels[r][c], SGridCell.VERTICAL_ALIGNMENT, SGridCell.HORIZONTAL);
					rowHeight = Math.max(rowHeight, cells[c*2 + 1].getHeight());
					
					cells[c*2 + 2] = new SGridCell(wagerPanels[r][c], SGridCell.VERTICAL_ALIGNMENT, SGridCell.HORIZONTAL);
					rowHeight = Math.max(rowHeight, cells[c*2 + 2].getHeight());
				}
				
				cells[cells.length - 1] = new SGridCell(totalPanels[r], SGridCell.VERTICAL_ALIGNMENT_EAST, SGridCell.NONE);
				rowHeight = Math.max(rowHeight, cells[cells.length - 1].getHeight());
				
				cellsByCoderID.put(placePanels[r].getCoderID(), cells);
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
			int titleHeight = 0;
			for(int x = 0; x < contest.getComponentCount(); x++) {
				STextField name = new STextField(contest.getComponent(x).getComponentName(), pointValueTitleFont);
				addLayer(new SMargin(name, new Insets(15, 0, 0, 0)));
				titleHeight = Math.max(titleHeight, getLayer(x).getHeight());
			}
			addLayer(table);

			TABLE_LAYER = contest.getComponentCount();
			VERTICALLINE_LAYER = TABLE_LAYER + 1;
			
			// Add the vertical line
			addLayer(new SLine(true));
			
			// Add each of the lines separating them
			for (int x = 0; x < rowSize + 1; x++) {
				addLayer(new SLine(false));
			}
			
			// Make the current width/height equal to the minimum
			setSize(table.getWidth(), titleHeight + table.getHeight());			
		}

		STable getTableLayer()
		{
			return (STable) getLayer(TABLE_LAYER);
		}
		
		public void setSize(int width, int height) {
			super.setSize(width, height);
			
			// Get the layers
			STable table = getTableLayer();
			
			int titleHeight = 0;
			for(int x = 0; x < contest.getComponentCount(); x++) {
				AnimatePanel title = getLayer(x);
				titleHeight = Math.max(titleHeight, title.getHeight());
			}
			
			// Set the table size
			table.setPosition(0, titleHeight);
			table.setSize(width, height - titleHeight);
			
			for(int x = 0; x < contest.getComponentCount(); x++) {
				AnimatePanel title = getLayer(x);
				int beginCol = table.getColumnStart(x*2+1);
				int endCol = table.getColumnEnd(x*2+2);
				int colLength = endCol - beginCol;
				
				// Set the title position
				title.setPosition((colLength / 2 - title.getWidth() / 2) + beginCol, 0);
			}
			
			// No rows
			if (table.getRowCount() < 2) {
				return;
			}
			
			SLine vertLine = (SLine) getLayer(VERTICALLINE_LAYER);
			int[] firstRow = getRowPos(table, 0);
			int[] lastRow = getRowPos(table, table.getRowCount() - 1);
			vertLine.setSize(vertLine.getWidth(), lastRow[1] - firstRow[0]);
			vertLine.setPosition(table.getColumnStart(1), firstRow[0] + titleHeight);
			
			// Position and size the lines
			//int lineWidth = table.getColumnEnd(6) - table.getColumnStart(1);
			int lineWidth = width - table.getColumnStart(1);
			for (int row = 0; row < table.getRowCount() + 1; row++) {
				int[] pos = getRowPos(table, Math.min(row, table.getRowCount() - 1));
				int yPos;

				if (row < table.getRowCount()) {
					yPos = pos[0];
				} else {
					yPos = pos[1];
				}
				
				//System.out.println(">>> Line for row (" + row + ") will be at " + (yPos));
				SLine line = (SLine) getLayer(VERTICALLINE_LAYER + 1 + row);
				line.setSize(lineWidth, line.getHeight());
				line.setPosition(table.getColumnStart(1), titleHeight + yPos);
			}

		}
		/** This function tries to normalize the differences between SGridLayout and STable
		 * (STable.getRowCount doesn't include header, SGridLayout.getRowStart/End does include it
		 * @param table the table to evaluate
		 * @param row the row (excluding header) to evaluate
		 * @return a 2 digit array representing on either side of the specified row 
		 */ 
		private int[] getRowPos(STable table, int row) {
			int[] rc = new int[2];
			int rowTotal = table.getRowCount() + 1; // adjust for header
			row++; // adjust for header
			
			// Handle first row
			if (row <= 1) {
				int spacing = getMidpointBetween(table, row, row+1);
				rc[0] = table.getRowStart(row) - spacing;
				rc[1] = table.getRowEnd(row) + spacing;
			} else if (row < rowTotal - 1) {
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
