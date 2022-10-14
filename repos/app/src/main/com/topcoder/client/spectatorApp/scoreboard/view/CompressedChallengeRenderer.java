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
import com.topcoder.client.spectatorApp.scoreboard.model.CompetitionTracker;
import com.topcoder.client.spectatorApp.scoreboard.model.PlacementChangeEvent;
import com.topcoder.client.spectatorApp.scoreboard.model.PlacementChangeListener;
import com.topcoder.client.spectatorApp.scoreboard.model.ScoreboardCoderTracker;
import com.topcoder.client.spectatorApp.scoreboard.model.ScoreboardPointTracker;
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
import com.topcoder.shared.netCommon.messages.spectator.CoderRoomData;
import com.topcoder.shared.netCommon.messages.spectator.ProblemData;

public class CompressedChallengeRenderer extends SPage {
	/** Font used for "offense/defense" title */
	private static final Font titleFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_BOLD, Font.PLAIN, 18);

	/** The header font */
	private static final Font problemHeaderFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_BOLD, Font.PLAIN, 18);

	/** The header font */
	//private static final Font headerFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_HEAVY, Font.PLAIN, 30);

	/** The body panel */
	private BodyPanel bodyPanel;

	/** The row cells by coder id */
	private Map<Integer, SGridCell[]> cellsByCoderID = new HashMap<Integer, SGridCell[]>();

	/** The point tracker */
	private CompetitionTracker<CoderRoomData> tracker;

	/** Switch telling us whether to relayout the screen or not */
	private boolean relayout = false;
	
	/** The change handle */
	private PlacementChangeListener placementHandler = new PlacementChangeListener() {
		public void placementChanged(PlacementChangeEvent evt) {
			relayout = true;
		}		
	};
	
	/** Constructs the panel */
	public CompressedChallengeRenderer(String roomTitle, String contestName, ScoreboardPointTracker pointTracker, ScoreboardCoderTracker coderTracker) {
		super(new HeaderPanel(roomTitle), null, null);
		
		// Save a reference to the point tracker
		tracker = pointTracker;
		tracker.getPlacementTracker().getChangeSupport().addListener(placementHandler);
		
		// Set the body panel
		bodyPanel = new BodyPanel(pointTracker, coderTracker);
		addLayer(bodyPanel);
		
		// Force an animation to start things off
		relayout = true;
		this.animate(0, 0);		
	}

	@Override
	public void dispose() {
		tracker.getPlacementTracker().getChangeSupport().removeListener(placementHandler);
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
		
		bodyPanel.updateLines();
		
		// table.setPosition(x,y);
	}
	
	private SGridCell[][] getRankedCells()
	{
		List<CoderRoomData> coders = tracker.getPlacementTracker().getCodersByPlacement();
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
		/** The defense title layer */
		private static final int DEFENSE_LAYER = 0;

		/** The offense title layer */
		private static final int OFFENSE_LAYER = 1;

		/** The table layer */
		private static final int TABLE_LAYER = 2;

		/** The vertical line layer */
		private static final int VERTICALLINE_LAYER = 3;

        private int rows;
        
        public void updateLines() {
            STable table = (STable) getLayer(BodyPanel.TABLE_LAYER);
            
		    for (int i = rows; i >= 0; --i) {
                removeLayer(VERTICALLINE_LAYER + 1 + i);
            }
            
            rows = table.getRowCount();
            for (int i = 0; i < rows + 1; ++i) {
                addLayer(new SLine(false));
            }
            setSize(getWidth(), getHeight());
        }

		public BodyPanel(ScoreboardPointTracker pointTracker, ScoreboardCoderTracker coderTracker) {
			// Create the table
			STable table = new STable();
			
			// Get the sizes of things
			List<CoderRoomData> coders = pointTracker.getCoders();
			List<ProblemData> problems = pointTracker.getProblems();
			final int rowSize = coders.size();
			final int colSize = problems.size();
			
			// Create the array items
			UserPlacePanel[] placePanels = new UserPlacePanel[rowSize];
			CoderActivityPanel[] activityPanels = new CoderActivityPanel[rowSize];
			ChallengeTotalPanel[] challengeTotalPanels = new ChallengeTotalPanel[rowSize];
			ChallengerValuePanel[][] pointPanels = new ChallengerValuePanel[rowSize][colSize];
			ChallengeTotalValuePanel[] totalPanels = new ChallengeTotalValuePanel[rowSize];
			
			// Create the column header panels (+4 for user, history, challenge and totals)
			AnimatePanel[] columnHeaderPanels = new AnimatePanel[colSize + 4];
			
			// Create the column panels
			int headerSize = 0;
			int[] colWidth = new int[colSize + 4];
			
			STextField challengesTitle = new STextField("CHALLENGES", problemHeaderFont);
			challengesTitle.setJustification(STextField.CENTER);
			columnHeaderPanels[2] = challengesTitle;
			headerSize = Math.max(headerSize, challengesTitle.getHeight());
			
			for (int c = 0; c < colSize; c++) {
				STextField field = new STextField("LEVEL " + (c + 1), problemHeaderFont);
				field.setJustification(STextField.CENTER);
				columnHeaderPanels[c + 3] = field;
				headerSize = Math.max(headerSize, field.getHeight());
				colWidth[c+3] = Math.max(colWidth[c+3], field.getWidth());
			}
			table.setColumnHeadings(columnHeaderPanels);
			
			// Create the user panels and the total panels
			for (int r = 0; r < rowSize; r++) {
				placePanels[r] = new UserPlacePanel(pointTracker, coders.get(r).getCoderID());
				colWidth[0] = Math.max(colWidth[0], placePanels[r].getWidth());
				
				activityPanels[r] = new CoderActivityPanel(pointTracker, coderTracker, coders.get(r).getCoderID());
				colWidth[1] = Math.max(colWidth[1], activityPanels[r].getWidth());
				
				challengeTotalPanels[r] = new ChallengeTotalPanel(pointTracker, coders.get(r).getCoderID());
				colWidth[2] = Math.max(colWidth[2], challengeTotalPanels[r].getWidth());
				
				totalPanels[r] = new ChallengeTotalValuePanel(pointTracker, coders.get(r).getCoderID());
				colWidth[colWidth.length - 1] = Math.max(colWidth[colWidth.length - 1], totalPanels[r].getWidth());
			}
			
			// Create the individual data panels
			for (int r = 0; r < rowSize; r++) {
				for (int c = 0; c < colSize; c++) {
					pointPanels[r][c] = new ChallengerValuePanel(
								coderTracker,
								pointTracker, 
								coders.get(r).getCoderID(), 
								problems.get(c).getProblemID());
					colWidth[c+3] = Math.max(colWidth[c+3], pointPanels[r][c].getWidth());
				}
			}
			
			// Add each row to the table
			
			int rowHeight = 0;
			int minUserWidth = 0;
			
			for (int r = 0; r < rowSize; r++) {
				SGridCell[] cells = new SGridCell[colSize + 4];
				cells[0] = new SGridCell(placePanels[r], SGridCell.VERTICAL_ALIGNMENT_WEST, SGridCell.HORIZONTAL);
				rowHeight = Math.max(rowHeight, cells[0].getHeight());
				minUserWidth = Math.max(minUserWidth, cells[0].getWidth());
				
				cells[1] = new SGridCell(new SMargin(activityPanels[r], new Insets(0, 5, 0, 0)), SGridCell.WEST, SGridCell.NONE);
				rowHeight = Math.max(rowHeight, cells[1].getHeight());
				
				cells[2] = new SGridCell(challengeTotalPanels[r], SGridCell.VERTICAL_ALIGNMENT_WEST, SGridCell.NONE);
				rowHeight = Math.max(rowHeight, cells[2].getHeight());
				
				for (int c = 0; c < colSize; c++) {
					cells[c + 3] = new SGridCell(pointPanels[r][c], SGridCell.VERTICAL_ALIGNMENT, SGridCell.NONE);
					rowHeight = Math.max(rowHeight, cells[c + 3].getHeight());
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
			//table.setRowSizingPolicy(SGridSizingPolicy.EVEN_SIZING);
			table.setColumnSizingPolicy(new SGridSizingPolicy.PointGridLayout(colWidth));
			
			// Setup the user handle outline
			SOutline outline = new SOutline(null, new Insets(15, 5, 25, 5));
			outline.setFillBox(true);
			table.setColumnOutline(0, outline);
		
			// Setup the title fields
			SOutline defense = new SOutline(new STextField("DEFENSE", titleFont), new Insets(15, 5, 5, 5));
			defense.setOutlineColor(null);
			defense.setFillBox(true);
			
			SOutline offense = new SOutline(new STextField("OFFENSE", titleFont), new Insets(15, 5, 5, 5));
			offense.setOutlineColor(null);
			offense.setFillBox(true);
			
			// Layer'em
			addLayer(defense);
			addLayer(offense);
			addLayer(table);
			
			// Add the vertical line
			addLayer(new SLine(true));
			
			rows = table.getRowCount();
			
			// Add each of the lines separating them
			for (int x = 0; x < rows + 1; x++) {
				addLayer(new SLine(false));
			}
			
			// Make the current width/height equal to the minimum
			setSize(table.getWidth(), Math.max(getLayer(DEFENSE_LAYER).getHeight(), getLayer(OFFENSE_LAYER).getHeight()) + table.getHeight());
		}

		public void setSize(int width, int height) {
			super.setSize(width, height);
			// Get the layers
			AnimatePanel defense = getLayer(DEFENSE_LAYER);
			AnimatePanel offense = getLayer(OFFENSE_LAYER);
			STable table = (STable) getLayer(TABLE_LAYER);
			
			// Set the table size
			int labelHeight = Math.max(defense.getHeight(), offense.getHeight());
			table.setPosition(0, labelHeight);
			table.setSize(width, height - labelHeight);
			
			// Set the title position
			int len = table.getColumnEnd(5) - table.getColumnStart(3);
			defense.setPosition(table.getColumnStart(3) + (len / 2) - (defense.getWidth() / 2), 0);
			len = table.getColumnEnd(2) - table.getColumnStart(1);
			offense.setPosition(table.getColumnStart(1) + (len / 2) - (offense.getWidth() / 2), 0);
			
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
