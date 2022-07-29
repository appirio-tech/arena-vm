package com.topcoder.client.spectatorApp.scoreboard.view;

import java.awt.Insets;
import com.topcoder.client.spectatorApp.widgets.SEmbeddedPanel;
import com.topcoder.client.spectatorApp.widgets.SGridCell;
import com.topcoder.client.spectatorApp.widgets.SGridSizingPolicy;
import com.topcoder.client.spectatorApp.widgets.SLine;
import com.topcoder.client.spectatorApp.widgets.SMargin;
import com.topcoder.client.spectatorApp.widgets.SOutline;
import com.topcoder.client.spectatorApp.widgets.STable;

/**
 * UserTrailer Created on Mar 14, 2004
 * 
 * @author Tim Roberts
 * @version 1.0
 */
public class UserTrailer extends SEmbeddedPanel {
	/** The underlying table */
	private STable table;

	/** Constructs from the panels provided */
	public UserTrailer(UserPlaceScorePanel[] panels) {
		table = new STable();
		
		setPanels(panels);
		
		// Setup the user handle outline
		SOutline outline = new SOutline(table, new Insets(10, 10, 10, 10));
		outline.setFillBox(true);
		outline.setResizePanel(true);
		this.setEmbeddedPanel(new SMargin(outline, new Insets(15, 0, 15, 0)));
	}

	/** Overridden to resize the underlying panels */
//	public void setSize(int width, int height) {
//		for (int x = userCells.length - 1; x >= 0; x--)
//			userCells[x].resize();
//		super.setSize(width, height);
//	}

	public void setPanels(UserPlaceScorePanel[] panels) {
		// Create the user cells
		SGridCell[] userCells = new SGridCell[panels.length];
		for (int x = 0; x < panels.length; x++) {
			userCells[x] = new SGridCell(panels[x], SGridCell.CENTER, SGridCell.HORIZONTAL);
		}
		
		// Setup the table cells
		SGridCell[][] tableCells = new SGridCell[1][userCells.length + (userCells.length - 1)];
		for (int x = 0; x < userCells.length; x++) {
			tableCells[0][x * 2] = userCells[x];
			if (x < userCells.length - 1) {
				tableCells[0][(x * 2) + 1] = new SGridCell(new SMargin(new SLine(), new Insets(0, 3, 0, 3)), SGridCell.CENTER, SGridCell.BOTH);
			}
		}
		
		//table.setRowSizingPolicy(SGridSizingPolicy.EVEN_SIZING);
		// Special column sizing to eliminate the lines from sizing
		table.setColumnSizingPolicy(new SGridSizingPolicy() {
			public void assignSizes(int[] values, int total) {				
				// Eliminate the lines from the totals
				for (int x = 1; x < values.length; x += 2) {
					total -= values[x]; // subtract the line
				}
								
				// Now size the others evenly
				int size = total / ((values.length / 2) + 1);
				for (int x = 0; x < values.length; x += 2) {
					values[x] = size;
				}
			}
		});
		
		// Set the rows
		table.setRows(tableCells);
		table.doLayout();
		
	}
}
