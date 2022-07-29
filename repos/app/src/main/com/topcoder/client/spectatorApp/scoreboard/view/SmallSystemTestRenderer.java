/**
 * CodingPhaseRenderer.java Description: Coding Phase renderer
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
package com.topcoder.client.spectatorApp.scoreboard.view;

import java.awt.Font;
import java.awt.Insets;
import org.apache.log4j.Category;
import com.topcoder.client.spectatorApp.FontFactory;
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

public class SmallSystemTestRenderer extends SPage {
	/** Reference to the logging category */
	private static final Category cat = Category.getInstance(SmallSystemTestRenderer.class.getName());

	/** Font used for "Point Value" title */
	private static final Font pointValueTitleFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_BOLD, Font.PLAIN, 18);

	/** The header font */
	private static final Font headerFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_HEAVY, Font.PLAIN, 30);

	/** Constructs the panel */
	public SmallSystemTestRenderer(String roomTitle, String contestName, ScoreboardPointTracker pointTracker) {
		super(new HeaderPanel(roomTitle), new SystemRenderer(pointTracker), new TrailerPanel(contestName));
	}

	public static class SystemRenderer extends LayeredPanel {
		/** The title layer */
		public static final int TITLE_LAYER = 0;

		/** The table layer */
		public static final int TABLE_LAYER = 1;

		private SystemRenderer(ScoreboardPointTracker pointTracker) {
			// Create the table
			STable table = new STable();
			// Get the sizes of things
			int rowSize = pointTracker.getCoderCount();
			int colSize = pointTracker.getProblemCount();
			
			// Create the array items
			UserPlacePanel[] userPanels = new UserPlacePanel[rowSize];
			TotalValuePanel[] totalPanels = new TotalValuePanel[rowSize];
			SystemTestValuePanel[][] pointPanels = new SystemTestValuePanel[rowSize][colSize];
			
			// Create the column header panels (+2 for user and totals)
			AnimatePanel[] columnHeaderPanels = new AnimatePanel[colSize + 2];
			
			// Create the column panels
			int headerSize = 0;
			for (int c = 0; c < colSize; c++) {
				STextField field = new STextField("LEVEL " + (c + 1), headerFont);
				field.setJustification(STextField.CENTER);
				SBox box = new SBox(field, new Insets(1, 3, 1, 3));
				box.setResizePanel(true);
				columnHeaderPanels[c + 1] = box;
				headerSize = Math.max(headerSize, box.getHeight());
			}
			table.setColumnHeadings(columnHeaderPanels);
			
			// Create the user panels and the total panels
			for (int r = 0; r < rowSize; r++) {
				userPanels[r] = new UserPlacePanel(pointTracker, pointTracker.getCoder(r).getCoderID());
				totalPanels[r] = new TotalValuePanel(pointTracker, pointTracker.getCoder(r).getCoderID(), pointTracker.getPriorTotalValue(r));
			}
			
			// Create the individual data panels
			for (int r = 0; r < rowSize; r++) {
				for (int c = 0; c < colSize; c++) {
					pointPanels[r][c] = new SystemTestValuePanel(pointTracker, pointTracker.getCoder(r).getCoderID(), pointTracker.getProblem(c).getProblemID());
				}
			}
			
			// Add each row to the table
			int rowHeight = 0;
			SGridCell[][] cells = new SGridCell[rowSize][colSize + 2];
			for (int r = 0; r < rowSize; r++) {
				cells[r][0] = new SGridCell(userPanels[r], SGridCell.VERTICAL_ALIGNMENT_WEST, SGridCell.HORIZONTAL);
				for (int c = 0; c < colSize; c++) {
					cells[r][c + 1] = new SGridCell(pointPanels[r][c], SGridCell.VERTICAL_ALIGNMENT, SGridCell.NONE);
				}
				cells[r][cells[r].length - 1] = new SGridCell(totalPanels[r], SGridCell.VERTICAL_ALIGNMENT_EAST, SGridCell.NONE);
				rowHeight = Math.max(rowHeight, cells[r][cells[r].length - 1].getHeight());
			}
			
			// Setup the policies
			table.setRowSpacingPolicy(new SGridSpacingPolicy.HeaderSpacingPolicy(10, new SGridSpacingPolicy.EdgePercentageSpacingPolicy(1.0, .00)));
			table.setColumnSpacingPolicy(new SGridSpacingPolicy.EdgePercentageSpacingPolicy(1.0, .05));
			//table.setRowSizingPolicy(SGridSizingPolicy.HEADEREVEN_SIZING);
			table.setRowSizingPolicy(new SGridSizingPolicy.HeaderSizingPolicy(headerSize, new SGridSizingPolicy.FixedSizingPolicy(rowHeight)));
			table.setColumnSizingPolicy(new SGridSizingPolicy.CollapseStrategy(0, SGridSizingPolicy.NONE));
			
			// Setup the user handle outline
			SOutline outline = new SOutline(null, new Insets(15, 15, 15, 15));
			outline.setFillBox(true);
			table.setColumnOutline(0, outline);
			
			// Set the layout
			table.setRows(cells);
			
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
