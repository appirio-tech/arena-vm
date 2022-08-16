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
import com.topcoder.client.spectatorApp.scoreboard.model.ScoreboardPointTracker;
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
import com.topcoder.shared.netCommon.messages.spectator.CoderRoomData;

public class RankedCodingRenderer extends SPage {
	/** Reference to the logging category */
	//private static final Category cat = Category.getInstance(RankedCodingRenderer.class.getName());

	/** Font used for "Point Value" title */
	private static final Font pointValueTitleFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_BOLD, Font.PLAIN, 18);

	/** The header font */
	private static final Font headerFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_HEAVY, Font.PLAIN, 30);

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
	public RankedCodingRenderer(String roomTitle, String contestName, ScoreboardPointTracker pointTracker) {
		super(new HeaderPanel(roomTitle), null, null);
		
		// Save a reference to the point tracker
		tracker = pointTracker;
		tracker.getPlacementTracker().getChangeSupport().addListener(placementHandler);

		// Set the body panel
		bodyPanel = new BodyPanel(pointTracker);
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
		/** The title layer */
		private static final int TITLE_LAYER = 0;

		/** The table layer */
		private static final int TABLE_LAYER = 1;

		public BodyPanel(ScoreboardPointTracker pointTracker) {
			// Create the table
			STable table = new STable();
			
			// Get the sizes of things
			int rowSize = pointTracker.getCoderCount();
			int colSize = pointTracker.getProblemCount();
			
			// Create the array items
			UserPlacePanel[] placePanels = new UserPlacePanel[rowSize];
			UserPlaceScorePanel[] scorePanels = new UserPlaceScorePanel[rowSize];
			TotalValuePanel[] totalPanels = new TotalValuePanel[rowSize];
			PointValuePanel[][] pointPanels = new PointValuePanel[rowSize][colSize];
			
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
				
				scorePanels[r] = new UserPlaceScorePanel(pointTracker, pointTracker.getCoder(r).getCoderID());
				trailerPanelByCoderID.put(pointTracker.getCoder(r).getCoderID(), scorePanels[r]);
				
				totalPanels[r] = new TotalValuePanel(pointTracker, pointTracker.getCoder(r).getCoderID());
				colWidth[colWidth.length - 1] = Math.max(colWidth[colWidth.length - 1], totalPanels[r].getWidth());
			}
			// Create the individual data panels
			for (int r = 0; r < rowSize; r++) {
				for (int c = 0; c < colSize; c++) {
					pointPanels[r][c] = new PointValuePanel(pointTracker, pointTracker.getCoder(r).getCoderID(), pointTracker.getProblem(c).getProblemID());
					colWidth[c+1] = Math.max(colWidth[c+1], pointPanels[r][c].getWidth());
				}
			}
			
			// Add each row to the table
			int rowHeight = 0;
			int userPanelWidth = 0;
			for (int r = 0; r < rowSize; r++) {
				SGridCell[] cells = new SGridCell[colSize + 2];
				
				// Add the user panel 
				cells[0] = new SGridCell(placePanels[r], SGridCell.VERTICAL_ALIGNMENT_WEST, SGridCell.HORIZONTAL);
				rowHeight = Math.max(rowHeight, cells[0].getHeight());
				userPanelWidth = Math.max(userPanelWidth, cells[0].getWidth());
				
				// Add each of the point panels 
				for (int c = 0; c < colSize; c++) {
					cells[c + 1] = new SGridCell(pointPanels[r][c], SGridCell.VERTICAL_ALIGNMENT, SGridCell.NONE);
					rowHeight = Math.max(rowHeight, cells[c + 1].getHeight());
				}
				
				// Add the total panel
				cells[cells.length - 1] = new SGridCell(totalPanels[r], SGridCell.VERTICAL_ALIGNMENT_EAST, SGridCell.NONE);
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
			
			// Add the layers
			addLayer(new SMargin(new STextField("POINT VALUE:", pointValueTitleFont), new Insets(15, 0, 0, 0)));
			addLayer(table);
			
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
			title.setPosition(table.getColumnStart(1), 0);
		}
	}
}
