/**
 * ChallengePhaseRenderer.java Description: Challenge Phase renderer. Note: this
 * is different from the other renderers in the way it aligns things. The top of
 * the ChallengeHistoryPanel aligns to the top of the userhandlepanels. The
 * userhandlepanel's vertical alignment is then used to align the total score on
 * it's vertical alignment..
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
import com.topcoder.client.spectatorApp.widgets.SOutline;
import com.topcoder.client.spectatorApp.widgets.SPage;
import com.topcoder.client.spectatorApp.widgets.STable;
import com.topcoder.client.spectatorApp.widgets.STextField;
import com.topcoder.shared.netCommon.messages.spectator.CoderRoomData;

public class RankedChallengeRenderer extends SPage {
	/** Reference to the logging category */
	//private static final Category cat = Category.getInstance(RankedChallengeRenderer.class.getName());

	/** Font used for "offense/defense" title */
	private static final Font titleFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_BOLD, Font.PLAIN, 18);

	/** The header font */
	private static final Font problemHeaderFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_BOLD, Font.PLAIN, 18);

	/** The user trailer */
	private UserTrailer trailer = null;

	/** The body panel */
	private BodyPanel bodyPanel;

	/** The row cells by coder id*/
	private Map<Integer, SGridCell[]> cellsByCoderID = new HashMap<Integer, SGridCell[]>();

	/** The user place panels by coder id */
	private Map<Integer, UserPlaceScorePanel> trailerPanelByCoderID = new HashMap<Integer, UserPlaceScorePanel>();

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
	public RankedChallengeRenderer(String roomTitle, String contestName, ScoreboardPointTracker pointTracker, ScoreboardCoderTracker coderTracker) {
		super(new HeaderPanel(roomTitle), null, null);
		
		// Save a reference to the point tracker
		tracker = pointTracker;
		tracker.getPlacementTracker().getChangeSupport().addListener(placementHandler);
		
		// Set the body panel
		bodyPanel = new BodyPanel(pointTracker, coderTracker);
		addLayer(bodyPanel);
		
		if (trailerPanelByCoderID.size() <= 4) {
			setLayer(TRAILER_LAYER, new TrailerPanel(roomTitle));
		} else {
			// Create the trailer panels to be used (the 5th place and above
			// panels)
			// Create the trailer and set it...
			trailer = new UserTrailer(getTrailerPanels());
			addLayer(trailer);
		}
		
		// Force an animation to start things off
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

	public void layoutRanks() {
		// Get the ranked cells and set in the table
		STable table = (STable) bodyPanel.getLayer(BodyPanel.TABLE_LAYER);
		table.setRows(getRankedCells());
		table.doLayout();

		if (trailer != null) {
			UserPlaceScorePanel[] up = getTrailerPanels();
			trailer.setPanels(up);
		}
	}

	private SGridCell[][] getRankedCells()
	{
		List<CoderRoomData> coders = tracker.getPlacementTracker().getCodersByPlacement();
		SGridCell[][] newCells = new SGridCell[Math.max(0,coders.size()-4)][];
		for (int r = Math.max(0,coders.size()-4) - 1; r >= 0; r--) {
			newCells[r] = cellsByCoderID.get(coders.get(r).getCoderID());
		}
		return newCells;
	}
	
	private UserPlaceScorePanel[] getTrailerPanels()
	{
		List<CoderRoomData> coders = tracker.getPlacementTracker().getCodersByPlacement();
		if (coders.size() < 4) {
			return new UserPlaceScorePanel[0];
		}
		
		UserPlaceScorePanel[] up = new UserPlaceScorePanel[Math.min(coders.size() - 4, 4)];
		for (int r = 4; r < Math.min(coders.size(), 8); r++) {
			up[r-4] = trailerPanelByCoderID.get(coders.get(r).getCoderID());
		}
		return up;
	}
	
	public void setSize(int width, int height) {
		super.setSize(width, height);
		
		if (trailer != null) {
			trailer.setSize(width, trailer.getHeight());
			trailer.setPosition(0, height - trailer.getHeight());
		}
		
		AnimatePanel background = getLayer(BACKGROUND_LAYER);
		bodyPanel.setPosition(background.getX(), background.getY());
		bodyPanel.setSize(width, background.getHeight() - (trailer == null ? 0 : trailer.getHeight()));
	}

	class BodyPanel extends LayeredPanel {
		/** The defense title layer */
		private static final int DEFENSE_LAYER = 0;

		/** The offense title layer */
		private static final int OFFENSE_LAYER = 1;

		/** The table layer */
		private static final int TABLE_LAYER = 2;

		public BodyPanel(ScoreboardPointTracker pointTracker, ScoreboardCoderTracker coderTracker) {
			// Create the table
			STable table = new STable();
			
			// Get the sizes of things
			int rowSize = pointTracker.getCoderCount();
			int colSize = pointTracker.getProblemCount();
			
			// Create the array items
			UserPlacePanel[] placePanels = new UserPlacePanel[rowSize];
			UserPlaceScorePanel[] scorePanels = new UserPlaceScorePanel[rowSize];
			ChallengeTotalValuePanel[] totalPanels = new ChallengeTotalValuePanel[rowSize];
			ChallengeHistoryValuePanel[][] pointPanels = new ChallengeHistoryValuePanel[rowSize][colSize];
			ChallengeHistoryPanel[] historyPanels = new ChallengeHistoryPanel[rowSize];
			
			// Create the column header panels (+3 for history, user and totals)
			AnimatePanel[] columnHeaderPanels = new AnimatePanel[colSize + 3];
			
			int headerSize = 0;
			int[] colWidth = new int[colSize + 3];
			
			// Create the column panels
			for (int c = 0; c < colSize; c++) {
				STextField field = new STextField("LEVEL " + (c + 1), problemHeaderFont);
				field.setJustification(STextField.CENTER);
				columnHeaderPanels[c + 1] = field;
				headerSize = Math.max(headerSize, field.getHeight());
				colWidth[c+1] = Math.max(colWidth[c+1], field.getWidth());
			}
			table.setColumnHeadings(columnHeaderPanels);
			
			// Create the user panels and the total panels
			for (int r = 0; r < rowSize; r++) {
				placePanels[r] = new UserPlacePanel(pointTracker, pointTracker.getCoder(r).getCoderID());
				colWidth[0] = Math.max(colWidth[0], placePanels[r].getWidth());
				
				scorePanels[r] = new UserPlaceScorePanel(pointTracker, pointTracker.getCoder(r).getCoderID());
				trailerPanelByCoderID.put(pointTracker.getCoder(r).getCoderID(), scorePanels[r]);
				
				historyPanels[r] = new ChallengeHistoryPanel(pointTracker, coderTracker, pointTracker.getCoder(r).getCoderID(), 3);
				colWidth[colWidth.length - 2] = Math.max(colWidth[colWidth.length - 2], historyPanels[r].getWidth());
				
				totalPanels[r] = new ChallengeTotalValuePanel(pointTracker, pointTracker.getCoder(r).getCoderID(), pointTracker.getTotalScore(r));
				colWidth[colWidth.length - 1] = Math.max(colWidth[colWidth.length - 1], totalPanels[r].getWidth());
			}
			// Create the individual data panels
			for (int r = 0; r < rowSize; r++) {
				for (int c = 0; c < colSize; c++) {
					pointPanels[r][c] = new ChallengeHistoryValuePanel(coderTracker, pointTracker, pointTracker.getCoder(r).getCoderID(), pointTracker.getProblem(c).getProblemID(), 3);
					colWidth[c+1] = Math.max(colWidth[c+1], pointPanels[r][c].getWidth());
				}
			}
			// Add each row to the table
			int rowHeight = 0, userPanelWidth=0;
			for (int r = 0; r < rowSize; r++) {
				SGridCell[] cells = new SGridCell[colSize + 3];

				cells[0] = new SGridCell(placePanels[r], SGridCell.NORTHWEST, SGridCell.HORIZONTAL);
				rowHeight = Math.max(rowHeight, cells[0].getHeight());
				userPanelWidth = Math.max(userPanelWidth, cells[0].getWidth());

				for (int c = 0; c < colSize; c++) {
					cells[c + 1] = new SGridCell(pointPanels[r][c], SGridCell.NORTH, SGridCell.NONE);
					rowHeight = Math.max(rowHeight, cells[c + 1].getHeight());
				}
				
				cells[cells.length - 2] = new SGridCell(historyPanels[r], SGridCell.NORTH, SGridCell.NONE);
				rowHeight = Math.max(rowHeight, cells[cells.length - 2].getHeight());
				
				cells[cells.length - 1] = new SGridCell(totalPanels[r], SGridCell.NORTHEAST, SGridCell.NONE);
				rowHeight = Math.max(rowHeight, cells[cells.length - 1].getHeight());
				
				// Add the row to the lookup
				cellsByCoderID.put(placePanels[r].getCoderID(), cells);
			}
			
			// Set the user handle to the max size
			for (SGridCell[] cells : cellsByCoderID.values()) {
				cells[0].setSize(userPanelWidth, cells[0].getHeight());
			}
			
			// Set the layout
			table.setRows(getRankedCells());
			
			// Setup the policies
			table.setRowSpacingPolicy(new SGridSpacingPolicy.HeaderSpacingPolicy(10, new SGridSpacingPolicy.PercentageSpacingPolicy(1.0)));
			table.setColumnSpacingPolicy(new SGridSpacingPolicy.EdgePercentageSpacingPolicy(1.0, .05));
			table.setRowSizingPolicy(new SGridSizingPolicy.HeaderSizingPolicy(headerSize, new SGridSizingPolicy.FixedSizingPolicy(rowHeight)));
			table.setColumnSizingPolicy(new SGridSizingPolicy.PointGridLayout(colWidth));
			
			// Setup the user handle outline
			SOutline outline = new SOutline(null, new Insets(15, 5, 15, 5));
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
			
			// Add each of the lines separating them
			for (int x = 0; x < Math.min(4, rowSize) - 1; x++) {
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
			int len = table.getColumnEnd(3) - table.getColumnStart(1);
			defense.setPosition(table.getColumnStart(1) + (len / 2) - (defense.getWidth() / 2), 0);
			len = table.getColumnEnd(4) - table.getColumnStart(4);
			offense.setPosition(table.getColumnStart(4) + (len / 2) - (offense.getWidth() / 2), 0);
			
			// Position and size the lines
			int lineWidth = table.getColumnEnd(4) - table.getColumnStart(1);
			for (int x = TABLE_LAYER + 1; x < getLayers().length; x++) {
				int row = x - TABLE_LAYER;
				int yPos = table.getRowEnd(row) + (table.getRowStart(row + 1) - table.getRowEnd(row)) / 2;
				SLine line = (SLine) getLayer(x);
				line.setSize(lineWidth, line.getHeight());
				line.setPosition(table.getColumnStart(1), labelHeight + yPos);
			}
		}
	}
}
