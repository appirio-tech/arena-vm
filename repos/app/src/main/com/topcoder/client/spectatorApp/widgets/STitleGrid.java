package com.topcoder.client.spectatorApp.widgets;

import java.awt.Insets;

/**
 * This class provides a black box with a title in column 1 and a 
 * description in column 2
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
public class STitleGrid extends SGridLayout {

	/**
	 * Constructs the class from the specified descriptors
	 * @param titles the title descriptors to build from
	 */
	public STitleGrid(STitleGridCell[] titles) {
		// Super construct
		super();
		
		// Create the cells
		SGridCell[][] cells = new SGridCell[titles.length][2];
		for(int x=0;x<titles.length;x++) {
			cells[x][0] = new SGridCell(new SMargin(titles[x].getTitleField(), new Insets(0,0,0,5)), SGridCell.VERTICAL_ALIGNMENT_EAST, SGridCell.NONE);
			cells[x][1] = new SGridCell(new SMargin(titles[x].getDescField(), new Insets(0,5,0,0)), SGridCell.VERTICAL_ALIGNMENT_WEST, SGridCell.NONE);
		}
		
		// Create the layout
		this.setRowSizingPolicy(SGridSizingPolicy.RIGHT_SIZING);
		this.setColumnSizingPolicy(SGridSizingPolicy.EDGE_SIZING);
		this.setGrid(cells);
	}
}

