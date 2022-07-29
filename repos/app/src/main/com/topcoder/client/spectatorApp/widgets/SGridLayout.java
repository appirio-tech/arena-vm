package com.topcoder.client.spectatorApp.widgets;

import java.awt.Graphics2D;
import com.topcoder.client.spectatorApp.views.LayeredPanel;

/**
 * This class provides a layout similar to the grid bad layout (although not as
 * complicated) Note: this component will NOT automatically relayout if any
 * panel resizes itself. The class will work correctly if any given cell is null
 * or if the rows contain variable number of columns.
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
public class SGridLayout extends LayeredPanel {
	/** Reference to the panels */
	private SGridCell[][] cells;

	/** Helper variable denoting how many rows there are */
	private int R = 0;

	/** Helper variable denoting the maximum number of columns */
	private int C = 0;

	/** The row sizing policy */
	private SGridSizingPolicy rowSizingPolicy = SGridSizingPolicy.EDGE_SIZING;

	/** The column sizing policy */
	private SGridSizingPolicy columnSizingPolicy = SGridSizingPolicy.EDGE_SIZING;

	/** The row weight policy */
	private SGridSpacingPolicy rowSpacingPolicy = SGridSpacingPolicy.NONE;

	/** The column weight policy */
	private SGridSpacingPolicy columnSpacingPolicy = SGridSpacingPolicy.NONE;

	/**
	 * Boolean indicating whether the layout has been invalidated and needs
	 * rebuilding
	 */
	public boolean invalidated = false;

	/** Quick reference to the column positions */
	private int[] colStart;

	private int[] colEnd;

	/** Quick reference to the row positions */
	private int[] rowStart;

	private int[] rowEnd;

	/** Empty constructors */
	public SGridLayout() {
		super.setSize(-1, -1);
		invalidated = true;
	}

	/**
	 * Constructs the Grid Layout using the specified sizing policies
	 * 
	 * @param rowPolicy
	 *           the row sizing policy to use
	 * @param columnPolicy
	 *           the column sizing policy to use
	 */
	public SGridLayout(SGridSizingPolicy rowPolicy, SGridSizingPolicy columnPolicy) {
		this.rowSizingPolicy = rowPolicy;
		this.columnSizingPolicy = columnPolicy;
		super.setSize(-1, -1);
		invalidated = true;
	}

	/**
	 * Constructs the Grid Layout from the specified cells using the default
	 * weight policies
	 * 
	 * @param panel
	 *           the panel to margin around
	 */
	public SGridLayout(SGridCell[][] cells) {
		setGrid(cells);
		super.setSize(-1, -1);
		invalidated = true;
	}

	/**
	 * Set's the cells to layout
	 * 
	 * @param cells
	 *           the cells to layout
	 */
	public void setGrid(SGridCell[][] cells) {
		// Eliminate all existing panesl
		clear();
		// Save the panels
		this.cells = cells;
		// Add each panel as a layer and add a property listener
		// on the size property
		R = cells.length;
		for (int r = 0; r < cells.length; r++) {
			C = Math.max(C, cells[r].length);
			for (int c = 0; c < cells[r].length; c++) {
				if (cells[r][c] != null) {
					addLayer(cells[r][c]);
				}
			}
		}
		// Invalidate the layout
		invalidate();
	}

	/**
	 * Invalidates the layout - forces the grid to relayout in the next call to
	 * animate
	 */
	public void invalidate() {
		invalidated = true;
	}

	/** Overridden to force a layout if needed */
	public int getWidth() {
		if (invalidated) doLayout();
		return super.getWidth();
	}

	/** Overridden to force a layout if needed */
	public int getHeight() {
		if (invalidated) doLayout();
		return super.getHeight();
	}

	/** Overridden to relayout if the grid has been invalidated */
	public void animate(long now, long diff) {
		if (invalidated) {
			invalidated = false;
			doLayout();
		}
		super.animate(now, diff);
	}

	/**
	 * Overridden to relayout the panel
	 */
	public void setSize(int width, int height) {
		// Call the super size
		super.setSize(width, height);
		
		// Invalidate the layout
		// invalidate();
		doLayout();
	}

	/**
	 * Sets the row sizing policy. The doLayout() will need to be call after
	 * setting the row sizing policy (it is NOT called automatically)
	 * 
	 * @param rowPolicy
	 *           the row sizing policy
	 */
	public void setRowSizingPolicy(SGridSizingPolicy rowPolicy) {
		this.rowSizingPolicy = rowPolicy;
		// Invalidate the layout
		invalidate();
	}

	/**
	 * Returns the row sizing policy
	 * 
	 * @return the row sizing policy
	 */
	public SGridSizingPolicy getRowSizingPolicy() {
		return rowSizingPolicy;
	}

	/**
	 * Returns the column sizing policy. The doLayout() will need to be call
	 * after setting the column sizing policy (it is NOT called automatically)
	 * 
	 * @param columnPolicy
	 *           the column sizing policy
	 */
	public void setColumnSizingPolicy(SGridSizingPolicy columnPolicy) {
		this.columnSizingPolicy = columnPolicy;
		// Invalidate the layout
		invalidate();
	}

	/**
	 * Returns the column sizing policy.
	 * 
	 * @return the column sizing policy
	 */
	public SGridSizingPolicy getColumnSizingPolicy() {
		return columnSizingPolicy;
	}

	/**
	 * Sets the row spacing policy. The doLayout() will need to be call after
	 * setting the row spacing policy (it is NOT called automatically)
	 * 
	 * @param rowPolicy
	 *           the row spacing policy
	 */
	public void setRowSpacingPolicy(SGridSpacingPolicy rowPolicy) {
		this.rowSpacingPolicy = rowPolicy;
		// Invalidate the layout
		invalidate();
	}

	/**
	 * Returns the row spacing policy
	 * 
	 * @return the row spacing policy
	 */
	public SGridSpacingPolicy getRowSpacingPolicy() {
		return rowSpacingPolicy;
	}

	/**
	 * Sets the column spacing policy. The doLayout() will need to be call after
	 * setting the column spacing policy (it is NOT called automatically)
	 * 
	 * @param columnPolicy
	 *           the column spacing policy
	 */
	public void setColumnSpacingPolicy(SGridSpacingPolicy columnPolicy) {
		this.columnSpacingPolicy = columnPolicy;
		// Invalidate the layout
		invalidate();
	}

	/**
	 * Returns the column spacing policy.
	 * 
	 * @return the column spacing policy
	 */
	public SGridSpacingPolicy getColumnSpacingPolicy() {
		return columnSpacingPolicy;
	}

	/** Returns the x-pos the given column starts in */
	public int getColumnStart(int c) {
		return colStart[c];
	}

	/** Returns the x-pos the given column ends in */
	public int getColumnEnd(int c) {
		return colEnd[c];
	}

	/** Returns the y-pos the given row starts in */
	public int getRowStart(int r) {
		return rowStart[r];
	}

	/** Returns the y-pos the given row ends in */
	public int getRowEnd(int r) {
		return rowEnd[r];
	}

	public void resize() {
		invalidate();
		super.setSize(-1, -1);
		doLayout();
	}

	/**
	 * Layes out the panel
	 */
	public void doLayout() {
		// Reset the invalidated
		invalidated = false;
		
		// Determine each row's height
		int calcHeight = 0;
		int[] rowHeight = new int[R];
		for (int r = 0; r < R; r++) {
			rowHeight[r] = calculateRowHeight(r);
			calcHeight += rowHeight[r];
		}
		
		// Find the rows vertical alignment
		int[] rowAlignment = new int[R];
		for (int r = 0; r < R; r++) {
			rowAlignment[r] = getRowVerticalAlignment(r);
			if (rowAlignment[r] <= 0) {
				rowAlignment[r] = rowHeight[r] / 2;
			}
		}
		
		// Determine each column's width
		int calcWidth = 0;
		int[] colWidth = new int[C];
		for (int c = 0; c < C; c++) {
			colWidth[c] = calculateColumnWidth(c);
			calcWidth += colWidth[c];
		}
		
		// Need to set the default size if none specified
		if (super.getWidth() < 0 || super.getHeight() < 0) {
			super.setSize(calcWidth, calcHeight);
		}
		
		int width = getWidth();
		int height = getHeight();
		
		// Assign the sizes
		columnSizingPolicy.assignSizes(colWidth, width);
		rowSizingPolicy.assignSizes(rowHeight, height);
//		for (int x = 0; x< rowHeight.length; x++) {
//			System.out.println(">>> RowHeight: " + x + " is " + rowHeight[x]);
//		}
		
		// Recalculate our width/height after assignment
		calcWidth = 0;
		for (int x = 0; x < colWidth.length; x++)
			calcWidth += colWidth[x];
		
		calcHeight = 0;
		for (int x = 0; x < rowHeight.length; x++)
			calcHeight += rowHeight[x];
		
		// Figure out the padding (ie spacing) between columns
		int[] padWidth = new int[C + 1];
		columnSpacingPolicy.assignSpacing(padWidth, getWidth() - calcWidth);
		
		// Figure out the padding (ie spacing) between rows
		int[] padHeight = new int[R + 1];
		rowSpacingPolicy.assignSpacing(padHeight, getHeight() - calcHeight);
//		for (int x = 0; x< rowHeight.length; x++) {
//			System.out.println(">>> RowPadding: " + x + " is " + padHeight[x]);
//		}
		
		// Create the column/row starts
		colStart = new int[C];
		colEnd = new int[C];
		rowStart = new int[R];
		rowEnd = new int[R];
		
		// Layout
		int currY = padHeight[0];
		for (int r = 0; r < R; r++) {
			int currX = padWidth[0];
			for (int c = 0; c < C; c++) {
				// Make sure the panel exists, then set the size
				if (c < cells[r].length && cells[r][c] != null) {
					// Set the cell size
					cells[r][c].setSize(colWidth[c], rowHeight[r]);
					
					// Set the cells position
					cells[r][c].setPosition(currX, currY);
					
					// Set the vertical alighment in the cell
					if (cells[r][c].isVerticallyAligned()) {
						cells[r][c].setVerticalAlignment(rowAlignment[r]);
					}
				}
				
				// Move to the next column
				colStart[c] = currX;
				currX += colWidth[c];
				colEnd[c] = currX - 1;
				currX += padWidth[c + 1];
			}
			// Move to the next row
			rowStart[r] = currY;
			currY += rowHeight[r];
			rowEnd[r] = currY - 1;
			currY += padHeight[r + 1];
//			System.out.println(">>> Row: " + r);
//			System.out.println(">>>    Start: " + rowStart[r]);
//			System.out.println(">>>    Height: " + rowHeight[r]);
//			System.out.println(">>>    End: " + rowEnd[r]);
//			System.out.println(">>>    Pad to next: " + padHeight[r+1]);
//			System.out.println(">>>    Next Start: " + currY);
		}
	}

	/**
	 * Calculates the row height of the given row
	 * 
	 * @param row
	 *           the row to calculate the height
	 * @return the height
	 */
	private int calculateRowHeight(int r) {
		// Bounds check assertion
		assert r >= 0 && r < cells.length : r;
		
		// Loop through all the columns and get the maximum height
		int maxAbove = 0;
		for (int c = 0; c < C; c++) {
			// Make sure the column exists for the row
			if (c < cells[r].length) {
				
				// Make sure the cell is not null
				if (cells[r][c] != null) {
					int va = cells[r][c].getVerticalAlignment();
					int h = cells[r][c].getHeight();
					maxAbove = Math.max(maxAbove, h - va);
				}
			}
		}
		
		// Return the total height (the maximum verticalAlighment + the maximum 'above' verticalAlighment)
		return getRowVerticalAlignment(r) + maxAbove;
	}

	/**
	 * Calculates the row height of the given row
	 * 
	 * @param row
	 *           the row to calculate the height
	 * @return the height
	 */
	private int getRowVerticalAlignment(int r) {
		// Bounds check assertion
		assert r >= 0 && r < cells.length : r;
		
		// Loop through all the columns and get the maximum height
		int maxVA = 0;
		for (int c = 0; c < C; c++) {
			// Make sure the column exists for the row
			if (c < cells[r].length) {
				// Make sure the cell is not null (and is being vertically aligned)
				if (cells[r][c] != null) {
					if (cells[r][c].isVerticallyAligned()) {
						maxVA = Math.max(maxVA, cells[r][c].getVerticalAlignment());
					} else {
						maxVA = Math.max(maxVA, cells[r][c].getHeight() / 2);
					}
				}
			}
		}
		// Return the middle of the row
		return maxVA;
	}

	/**
	 * Calculates the row height of the given row
	 * 
	 * @param row
	 *           the row to calculate the height
	 * @return the height
	 */
	private int calculateColumnWidth(int c) {
		// Bounds check assertion
		assert c >= 0 : c;
		// Loop through all the columns and get the maximum height
		int width = 0;
		for (int r = 0; r < cells.length; r++) {
			// Make sure the row has the column
			if (c < cells[r].length) {
				// Make sure the cell is not null
				if (cells[r][c] != null) {
					width = Math.max(width, cells[r][c].getWidth());
				}
			}
		}
		// Return the height
		return width;
	}

	/** Overridden to add debugging colors */
	public void render(Graphics2D g2D) {
		super.render(g2D);
		if (getDebugColor() != null) {
			g2D.setPaint(getDebugColor());
			g2D.drawRect(getX(), getY(), getWidth() - 1, getHeight() - 1);
		}
	}
}
