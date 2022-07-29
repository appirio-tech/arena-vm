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
import com.topcoder.client.spectatorApp.Constants;
import com.topcoder.client.spectatorApp.FontFactory;
import com.topcoder.client.spectatorApp.scoreboard.model.ComponentContest;
import com.topcoder.client.spectatorApp.scoreboard.model.PlacementChangeEvent;
import com.topcoder.client.spectatorApp.scoreboard.model.PlacementChangeListener;
import com.topcoder.client.spectatorApp.scoreboard.model.Round;
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
import com.topcoder.shared.netCommon.messages.spectator.CoderData;
import com.topcoder.shared.netCommon.messages.spectator.ComponentCoder;

public class ComponentAppealsPanel extends SPage {
	/** Font used for "Review Board" title */
	private static final Font pointValueTitleFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_BOLD, Font.PLAIN, 18);

	/** The header font */
	private static final Font headerFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_HEAVY, Font.PLAIN, 30);

	/** The body panel */
	private BodyPanel bodyPanel;

	/** The row cells by coder id */
	private Map<Integer, SGridCell[]> cellsByCoderID = new HashMap<Integer, SGridCell[]>();

	/** The component contest */
	private ComponentContest contest;

	/** Switch telling us whether to relayout the screen or not */
	private boolean relayout = false;
	
	/** Color for the total score header */
	private static final Color headerTotalScore = Color.WHITE;
	
	/** The title layer */
//	private static final int TITLE_LAYER = 0;

	/** The table layer */
//	private static final int TABLE_LAYER = 1;

	/** The vertical line layer */
	private static final int VERTICALLINE_LAYER = 2;


	/** The change handle */
	private PlacementChangeListener placementHandler = new PlacementChangeListener() {
		public void placementChanged(PlacementChangeEvent evt) {
			relayout = true;
		}		
	};
	
	/** Constructs the panel */
	public ComponentAppealsPanel(Round round, ComponentContest contest) {
		super(new HeaderPanel(round.getRoundName() + " " + contest.getComponentName()), null, null);
		
		// Save a reference to the point tracker
		this.contest = contest;
		contest.getPlacementTracker().getChangeSupport().addListener(placementHandler);
		
		// Set the body panel
		bodyPanel = new BodyPanel(contest);
		addLayer(bodyPanel);
		
		// Force an animation to start things off
		relayout = true;
		this.animate(0, 0);
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
		STable table = (STable) bodyPanel.getLayer(BodyPanel.TABLE_LAYER);
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
		/** The title layer */
		private static final int TITLE_LAYER = 0;

		/** The table layer */
		private static final int TABLE_LAYER = 1;

		public BodyPanel(ComponentContest contest) {
			// Create the table
			STable table = new STable();
			
			// Get the sizes of things
			List<ComponentCoder> coders = contest.getCoders();
			List<CoderData> reviewers = contest.getReviewers();
			final int rowSize = coders.size();
			final int colSize = reviewers.size();
			
			// Create the array items
			UserPlacePanel[] placePanels = new UserPlacePanel[rowSize];
			AppealsAverageValuePanel[] totalPanels = new AppealsAverageValuePanel[rowSize];
			AppealsPointValuePanel[][] pointPanels = new AppealsPointValuePanel[rowSize][colSize];
			
			// Create the column header panels (+2 for user and totals)
			AnimatePanel[] columnHeaderPanels = new AnimatePanel[colSize + 2];
			
			int headerSize = 0;
			int[] colWidth = new int[colSize + 2];
			
			// Create the column panels
			for (int c = 0; c < colSize; c++) {
				STextField field = new STextField(reviewers.get(c).getHandle(), headerFont);
				field.setJustification(STextField.RIGHT);
				field.setColor(Constants.getRankColor(reviewers.get(c).getRank()));
				SMargin margin = new SMargin(field, new Insets(1, 3, 1, 3));
				columnHeaderPanels[c + 1] = margin;
				colWidth[c+1] = Math.max(colWidth[c+1], margin.getWidth());
				headerSize = Math.max(headerSize, margin.getHeight());
			}
			STextField field = new STextField("Avg. Score", headerFont);
			field.setJustification(STextField.RIGHT);
			field.setColor(headerTotalScore);
			SMargin margin = new SMargin(field, new Insets(1, 3, 1, 3));
			columnHeaderPanels[columnHeaderPanels.length - 1] = margin;
			table.setColumnHeadings(columnHeaderPanels);
			
			// Create the user panels and the total panels
			for (int r = 0; r < rowSize; r++) {
				placePanels[r] = new UserPlacePanel(contest, coders.get(r).getCoderID());
				colWidth[0] = Math.max(colWidth[0], placePanels[r].getWidth());
				
				totalPanels[r] = new AppealsAverageValuePanel(contest, coders.get(r).getCoderID());
				colWidth[colWidth.length - 1] = Math.max(colWidth[colWidth.length - 1], totalPanels[r].getWidth());
			}
			
			// Create the individual data panels
			for (int r = 0; r < rowSize; r++) {
				for (int c = 0; c < colSize; c++) {
					pointPanels[r][c] = new AppealsPointValuePanel(
								contest, 
								coders.get(r).getCoderID(), 
								reviewers.get(c).getCoderID());
					colWidth[c+1] = Math.max(colWidth[c+1], pointPanels[r][c].getWidth());
				}
			}
			
			// Add each row to the table
			int rowHeight = 0;
			int minUserWidth = 0;
			
			for (int r = 0; r < rowSize; r++) {
				SGridCell[] cells = new SGridCell[colSize + 2];
				cells[0] = new SGridCell(placePanels[r], SGridCell.VERTICAL_ALIGNMENT_WEST, SGridCell.HORIZONTAL);
				rowHeight = Math.max(rowHeight, cells[0].getHeight());
				minUserWidth = Math.max(minUserWidth, cells[0].getWidth());
				
				for (int c = 0; c < colSize; c++) {
					cells[c + 1] = new SGridCell(pointPanels[r][c], SGridCell.VERTICAL_ALIGNMENT, SGridCell.NONE);
					rowHeight = Math.max(rowHeight, cells[c + 1].getHeight());
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
			addLayer(new SMargin(new STextField("Review Board", pointValueTitleFont), new Insets(15, 0, 0, 0)));
			addLayer(table);
			
			// Add the vertical line
			addLayer(new SLine(true));
			
			// Add each of the lines separating them
			for (int x = 0; x < rowSize + 1; x++) {
				addLayer(new SLine(false));
			}
			
			// Make the current width/height equal to the minimum
			setSize(table.getWidth(), getLayer(TITLE_LAYER).getHeight() + table.getHeight());			
		}

		public void setSize(int width, int height) {
			super.setSize(width, height);
			
			// Get the layers
			AnimatePanel title = getLayer(TITLE_LAYER);
			STable table = (STable) getLayer(TABLE_LAYER);
			
			// Set the table size
			table.setPosition(0, title.getHeight());
			table.setSize(width, height - title.getHeight());
			
			// Set the title position
			title.setPosition(
						(table.getColumnEnd(table.getColumnCount() - 2) - table.getColumnStart(1)) / 2 - title.getWidth() / 2
						+ table.getColumnStart(1)
						, 0);
			
			int labelHeight = title.getHeight();
			
			// No rows
			if (table.getRowCount() < 2) {
				return;
			}
			
			SLine vertLine = (SLine) getLayer(VERTICALLINE_LAYER);
			int[] firstRow = getRowPos(table, 0);
			int[] lastRow = getRowPos(table, table.getRowCount() - 1);
			vertLine.setSize(vertLine.getWidth(), lastRow[1] - firstRow[0]);
			vertLine.setPosition(table.getColumnStart(1), firstRow[0] + labelHeight);
			
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
				line.setPosition(table.getColumnStart(1), labelHeight + yPos);
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
