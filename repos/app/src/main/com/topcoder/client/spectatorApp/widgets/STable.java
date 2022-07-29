package com.topcoder.client.spectatorApp.widgets;

import java.awt.Color;
import java.awt.Insets;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import com.topcoder.client.spectatorApp.views.AnimatePanel;
import com.topcoder.client.spectatorApp.views.LayeredPanel;

/**
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
public class STable extends SGridLayout {
	/** The column headings */
	private SGridCell[] colHeading = new SGridCell[0];

	/** The cells */
	private SGridCell[][] cells = new SGridCell[0][0];

	/** Whether the column is outlined */
	private Map<Integer, SOutline> outlineColumn = new HashMap<Integer, SOutline>();

	/** Whether the row is outlined */
	private Map<Integer, SOutline> outlineRow = new HashMap<Integer, SOutline>();

	/** The row filters to use */
	private Map<Integer, LayeredPanel.LayeredFilter> rowFilters = new HashMap<Integer, LayeredPanel.LayeredFilter>();

	/** Boolean indicating whether to rebuild the table or not */
	private boolean rebuildTable = false;

	/**
	 * Creates the table with a weight policy (both row/col) of
	 * {@link SGridWeightPolicy.NONE}
	 */
	public STable() {
		super();
		setRowSizingPolicy(SGridSizingPolicy.NONE);
		setColumnSizingPolicy(SGridSizingPolicy.NONE);
	}

	/** Marks the table for needing rebuilding */
	public void invalidateTable() {
		rebuildTable = true;
		invalidate();
	}

	/**
	 * Sets the column headings based on the panels. The panels will be centered
	 * (horizontally) and place in the SOUTH position in the header row. The
	 * panels will be resized horizontally
	 * 
	 * @param panels
	 *           the panels that make up the column headings
	 */
	public void setColumnHeadings(AnimatePanel[] panels) {
		setColumnHeadings(panels, SGridCell.SOUTH, SGridCell.HORIZONTAL);
	}

	/**
	 * Sets the column headings based on the panels. The specified anchor and
	 * resize will be applied to ALL headers.
	 * 
	 * @param panels
	 *           the panels that make up the column headings
	 * @param anchor
	 *           the anchor value
	 * @param resize
	 *           the resize value
	 */
	public void setColumnHeadings(AnimatePanel[] panels, int anchor, int resize) {
		SGridCell[] cell = new SGridCell[panels.length];
		for (int x = panels.length - 1; x >= 0; x--) {
			cell[x] = new SGridCell(panels[x], anchor, resize);
			//cell[x].setDebugColor(Color.red);
		}
		setColumnHeadings(cell);
	}

	/**
	 * Sets the column headings based on the cells. This allows you to customize
	 * each cell. This will also clear out any column outlines
	 * 
	 * @param cells
	 *           the cells
	 */
	public void setColumnHeadings(SGridCell[] cells) {
		// Set the cells
		colHeading = cells;
		
		// Clear out any outlines
		outlineColumn.clear();
		outlineRow.clear();
		rowFilters.clear();
		
		// Invalidate the table
		invalidateTable();
	}

	/**
	 * Adds a row of panels. Each panel will be aligned via
	 * {@link SGridCell.VERTICAL_ALIGNMENT} and will not be resized.
	 * 
	 * @param panels
	 *           the panels to add
	 */
	public void addRow(AnimatePanel[] panels) {
		setRow(getRowCount(), panels);
	}

	/**
	 * Adds a row of panels. The specified anchor and resize will be applied to
	 * ALL panels.
	 * 
	 * @param panels
	 *           the panels to add
	 * @param anchor
	 *           the anchor to use
	 */
	public void addRow(AnimatePanel[] panels, int anchor, int resize) {
		setRow(getRowCount(), panels, anchor, resize);
	}

	/**
	 * Adds a row of cells. Useful to customize the anchor and resizing of each
	 * cell
	 * 
	 * @param cells
	 *           the cells to add
	 */
	public void addRow(SGridCell[] cells) {
		setRow(getRowCount(), cells);
	}

	/**
	 * Sets a set of panels to a particular row. Each panel will be aligned via
	 * {@link SGridCell.VERTICAL_ALIGNMENT} and will not be resizable.
	 * 
	 * @param row
	 *           the row to set. If row does not exist, will be created
	 * @param panels
	 *           the panels
	 */
	public void setRow(int row, AnimatePanel[] panels) {
		setRow(row, panels, SGridCell.VERTICAL_ALIGNMENT, SGridCell.NONE);
	}

	/**
	 * Sets a set of panels to a particular row. The specified anchor and resize
	 * will be applied to ALL panels.
	 * 
	 * @param row
	 *           the row to set. If row does not exist, will be created
	 * @param panels
	 *           the panels
	 * @param anchor
	 *           the anchor
	 * @param resize
	 *           the resize
	 */
	public void setRow(int row, AnimatePanel[] panels, int anchor, int resize) {
		SGridCell[] cell = new SGridCell[panels.length];
		for (int x = panels.length - 1; x >= 0; x--) {
			cell[x] = new SGridCell(panels[x], anchor, resize);
		}
		setRow(row, cell);
	}

	/**
	 * Sets a set of panels to a particular row. Useful to customize the anchor
	 * and resizability of each cell.
	 * 
	 * @param row
	 *           the row to set. If row does not exist, will be created
	 * @param panels
	 *           the panels
	 */
	public void setRow(int row, SGridCell[] theCells) {
		// Ensure the row exists
		ensureRowExists(row);
		
		// Set the cells
		cells[row] = theCells;
		
		// Invalidate the table
		invalidateTable();
	}

	/**
	 * Sets all the rows
	 * 
	 * @param cells
	 */
	public void setRows(SGridCell[][] cells) {
		this.cells = cells;
		
		// Invalidate the table
		invalidateTable();
	}

	/**
	 * Sets a layered filter on the row.
	 * 
	 * @param row
	 *           the row to set
	 * @param filter
	 *           the filter to set, use null to unset
	 */
	public void setRowFilter(int row, LayeredPanel.LayeredFilter filter) {
		checkRowRange(row);
		
		// Add or remove the filter
		if (filter == null) {
			rowFilters.remove(row);
		} else {
			rowFilters.put(row, filter);
		}
		// Invalidate the table
		invalidateTable();
	}

	/**
	 * Removes all row filters
	 */
	public void clearRowFilters() {
		rowFilters.clear();
		invalidateTable(); // Don't invalidate the table - no layout changes
									// here
	}

	/**
	 * Removes all column outlines
	 */
	public void clearColumnOutlines() {
		this.outlineColumn.clear();
		invalidateTable();
	}

	/**
	 * Removes all row outlines
	 */
	public void clearRowOutlines() {
		this.outlineRow.clear();
		invalidateTable();
	}

	/**
	 * Sets a set of panels to a particular column. Each panel will be aligned
	 * via {@link SGridCell.VERTICAL_ALIGNMENT} and will not be resizable.
	 * 
	 * @param col
	 *           the column to set. If column does not exist, will be created
	 * @param panels
	 *           the panels
	 */
	public void setColumn(int col, AnimatePanel[] panels) {
		setColumn(col, panels, SGridCell.VERTICAL_ALIGNMENT, SGridCell.NONE);
	}

	/**
	 * Sets a set of panels to a particular column. The specified anchor and
	 * resize will be applied to ALL panels.
	 * 
	 * @param col
	 *           the column to set. If col does not exist, will be created
	 * @param panels
	 *           the panels
	 * @param anchor
	 *           the anchor
	 * @param resize
	 *           the resize
	 */
	public void setColumn(int col, AnimatePanel[] panels, int anchor, int resize) {
		SGridCell[] cell = new SGridCell[panels.length];
		for (int x = panels.length - 1; x >= 0; x--) {
			cell[x] = new SGridCell(panels[x], anchor, resize);
		}
		setColumn(col, cell);
	}

	/**
	 * Sets a set of panels to a particular column. Useful to customize the
	 * anchor and resizability of each cell.
	 * 
	 * @param col
	 *           the column to set. If column does not exist, will be created
	 * @param panels
	 *           the panels
	 */
	public void setColumn(int col, SGridCell[] theCells) {
		// Ensure the column exists
		ensureColumnExists(col);
		// Loop through the rows
		for (int r = 0; r < cells.length; r++) {
			// Are we beyond the cells?
			if (r >= theCells.length) break;
			// Set it
			cells[r][col] = theCells[r];
		}
		// Invalidate the table
		invalidateTable();
	}

	/**
	 * Sets the outline used for the column
	 * 
	 * @param col
	 *           the column
	 * @param outline
	 *           the outline, use null to eliminate one
	 */
	public void setColumnOutline(int col, SOutline outline) {
		// Add or remove the outline
		if (outline == null) {
			outlineColumn.remove(col);
		} else {
			outlineColumn.put(col, outline);
		}
		// Invalide the table
		invalidateTable();
	}

	/**
	 * Sets the outline used for the column
	 * 
	 * @param col
	 *           the column
	 * @param outline
	 *           the outline, use null to eliminate one
	 */
	public void setRowOutline(int row, SOutline outline) {
		// Add or remove the outline
		Integer rowI = new Integer(row);
		if (outline == null) {
			outlineRow.remove(rowI);
		} else {
			outlineRow.put(rowI, outline);
		}
		// Invalidate the layout
		invalidateTable();
	}

	/**
	 * Sets a specific cell
	 * 
	 * @param row
	 *           the row
	 * @param column
	 *           the column
	 * @param theCell
	 *           the cell
	 */
	public void setCell(int row, int column, SGridCell theCell) {
		// Ensure the row/col exists
		ensureRowExists(row);
		ensureColumnExists(column);
		// Set the cell
		cells[row][column] = theCell;
		// Invalidate the table
		invalidateTable();
	}

	/**
	 * Gets a specific cell
	 * 
	 * @param row
	 *           the row
	 * @param column
	 *           the column
	 */
	public SGridCell getCell(int row, int column) {
		// Ensure the row/col exists
		checkRowRange(row);
		checkColumnRange(column);
		// Return the cell
		return cells[row][column];
	}

	/**
	 * Swap two rows with each other
	 * 
	 * @param from
	 *           the from row
	 * @param to
	 *           the to row
	 */
	public void swapRow(int from, int to) {
		checkRowRange(from);
		checkRowRange(to);
		if (from == to) return;
		SGridCell[] fromCells = cells[from];
		cells[from] = cells[to];
		cells[to] = fromCells;
		// Invalidate the table
		invalidateTable();
	}

	/**
	 * Moves one row to another row, shifting all rows down from the to position
	 * 
	 * @param from
	 *           the from row
	 * @param to
	 *           the to row
	 */
	public void moveRow(int from, int to) {
		checkRowRange(from);
		checkRowRange(to);
		if (from == to) return;
		// Allocate new array (minus one row)
		SGridCell[][] newCells = new SGridCell[cells.length][];
		// Find the max,min points
		int pt1 = Math.min(from, to);
		int pt2 = Math.max(from, to);
		// Loop through cells
		for (int x = cells.length - 1; x >= 0; x--) {
			// Outside of points, simple reference
			if (x < pt1 || x > pt2) {
				newCells[x] = cells[x];
				// If on the two points
			} else if (x == to) {
				newCells[x] = cells[from];
				// Between the two points
			} else {
				if (from < to) {
					newCells[x] = cells[x + 1];
				} else {
					newCells[x] = cells[x - 1];
				}
			}
		}
		// Save the new rows
		cells = newCells;
		// Invalidate the layout
		invalidateTable();
	}

	/**
	 * Sets a set of panels to a particular row. Each panel will be aligned via
	 * {@link SGridCell.VERTICAL_ALIGNMENT} and will not be resizable.
	 * 
	 * @param row
	 *           the row to set. If row does not exist, will be created
	 * @param panels
	 *           the panels
	 */
	public void deleteRow(int row) {
		checkRowRange(row);
		// Allocate new array (minus one row)
		SGridCell[][] newCells = new SGridCell[cells.length - 1][];
		// Loop through cells
		for (int x = cells.length - 1; x >= 0; x--) {
			// Skip the row being deleted
			if (x == row) continue;
			// Adjust the index
			int idx = (x < row ? x : x - 1);
			// Set the row
			newCells[idx] = cells[x];
		}
		// Save the new rows
		cells = newCells;
		// Invalidate the layout
		invalidateTable();
	}

	/** Returns the number of rows defined */
	public int getRowCount() {
		return cells.length;
	}

	/** Returns the number of columns defined */
	public int getColumnCount() {
		int max = colHeading.length;
		for (int x = cells.length - 1; x >= 0; x--) {
			max = Math.max(max, cells[x].length);
		}
		return max;
	}

	/**
	 * Lays out the table
	 */
	private void rebuildTable() {
		// Clear all the existing columns
		clear();
		
		// Put the headers together with the rows
		SGridCell[][] full = new SGridCell[cells.length + 1][];
		full[0] = colHeading;
		for (int x = 1; x <= cells.length; x++) {
			full[x] = cells[x - 1];
		}
		
		// Set the layout
		setGrid(full);
		
		// Loop through the filters
		for (Iterator itr = rowFilters.entrySet().iterator(); itr.hasNext();) {
			// Get the info
			Map.Entry entry = (Map.Entry) itr.next();
			int r = ((Integer) entry.getKey()).intValue();
			LayeredPanel.LayeredFilter filter = (LayeredPanel.LayeredFilter) entry.getValue();
			
			// Does the row exists?
			if (r < cells.length) {
				// Loop through all the cells
				for (int c = cells[r].length - 1; c >= 0; c--) {
					// If a panel exists for the cell, apply the filter to it
					if (cells[r][c] != null) {
						this.setFilter(cells[r][c], filter);
					}
				}
			}
		}
		
		// Add any column outline panels
		for (SOutline p : outlineColumn.values()) {
			// Add the layer
			addLayer(0, p);
		}
		
		// Add any row outline panels
		for (SOutline p : outlineRow.values()) {
			// Add the layer
			addLayer(0, p);
		}
	}

	/** Overridden to layout the outlines */
	public void doLayout() {
		// If the table needs rebuilt, rebuild it
		if (rebuildTable) rebuildTable();
		
		// Call the super dobie dobie do...
		super.doLayout();
		
		// Loop through the columns that need outlines
		for (Map.Entry<Integer, SOutline> entry : outlineColumn.entrySet()) {
			// Get the outline insets
			SOutline outline = entry.getValue();
			Insets insets = outline.getInsets();
			
			// Get the column
			int cc = entry.getKey();
			
			// Get the boundry of the column
			Rectangle bounds = getColumnPanelBounds(cc);
			if (bounds == null) continue;
			
			// Calculate the X and Y position of the outline
			int xPos = bounds.x - insets.left;
			int yPos = bounds.y - insets.top;
			
			// Calculate the width and height
			int width = bounds.width + insets.left + insets.right;
			int height = bounds.height + insets.top + insets.bottom;
			
			// Set the outline's position and size
			outline.setPosition(xPos, yPos);
			outline.setSize(width, height);
		}
		
		// Loop through the rows that need outlines
		for (Map.Entry<Integer, SOutline> entry : outlineRow.entrySet()) {
			
			// Get the outline insets
			SOutline outline = entry.getValue();
			Insets insets = outline.getInsets();
			
			// Get the row
			int rr = entry.getKey();// + 1; // get over the header
			
			// Get the boundry of the row
			Rectangle bounds = getRowPanelBounds(rr);
			if (bounds == null) continue;
			
			// Calculate the X and Y position of the outline
			int xPos = bounds.x - insets.left;
			int yPos = bounds.y - insets.top;
			
			// Calculate the width and height
			int width = bounds.width + insets.left + insets.right;
			int height = bounds.height + insets.top + insets.bottom;
			
			// Set the outline's position and size
			outline.setPosition(xPos, yPos);
			outline.setSize(width, height);
		}
	}

	/**
	 * Returns the boundry of the panels within the cells for a column
	 * 
	 * @param c
	 *           the column to find
	 * @return a rectangle encompassing the bounds of all the panels within the
	 *         cells for a specific column or null if either column doesn't exist
	 *         or the cells have no panels
	 */
	public Rectangle getColumnPanelBounds(int c) {
		int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
		
		// Loop through all the rows
		for (int r = 0; r < cells.length; r++) {
			// Make sure the column exists
			if (c >= cells[r].length) continue;
			
			// Get the cell (if cell is null or panel is null, ignore)
			SGridCell cell = getCell(r, c);
			if (cell == null || cell.getPanel() == null) continue;
			
			// Get the panel and find the min & max boundries
			AnimatePanel panel = cell.getPanel();
			minX = Math.min(minX, panel.getX() + cell.getX());
			minY = Math.min(minY, panel.getY() + cell.getY());
			maxX = Math.max(maxX, panel.getX() + panel.getWidth() + cell.getX());
			maxY = Math.max(maxY, panel.getY() + panel.getHeight() + cell.getY());
		}
		// If something wasn't found - return null
		if (minX == Integer.MAX_VALUE || minY == Integer.MAX_VALUE || maxX == Integer.MIN_VALUE || maxY == Integer.MIN_VALUE) return null;
		
		// Return a rectangle encompasing the boundry
		return new Rectangle(minX, minY, maxX - minX, maxY - minY);
	}

	/**
	 * Returns the boundry of the panels within the cells for a row
	 * 
	 * @param r
	 *           the row to find
	 * @return a rectangle encompassing the bounds of all the panels within the
	 *         cells for a specific row or null if the row doesn't exist or all
	 *         cells within the row have no panels
	 */
	public Rectangle getRowPanelBounds(int r) {
		int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
		// Validate R
		if (r >= cells.length) return null;
		for (int c = 0; c < cells[r].length; c++) {
			// Get the cell (if cell is null or panel is null, ignore)
			SGridCell cell = getCell(r, c);
			if (cell == null || cell.getPanel() == null) continue;
			// Get the panel and calculate min/max
			AnimatePanel panel = cell.getPanel();
			minX = Math.min(minX, panel.getX() + cell.getX());
			minY = Math.min(minY, panel.getY() + cell.getY());
			maxX = Math.max(maxX, panel.getX() + panel.getWidth() + cell.getX());
			maxY = Math.max(maxY, panel.getY() + panel.getHeight() + cell.getY());
		}
		// If something wasn't found - return null
		if (minX == Integer.MAX_VALUE || minY == Integer.MAX_VALUE || maxX == Integer.MIN_VALUE || maxY == Integer.MIN_VALUE) return null;
		// Return the boudnry
		return new Rectangle(minX, minY, maxX - minX, maxY - minY);
	}

	/**
	 * Helper method to determine if value is within the range
	 * 
	 * @param row
	 *           the row number to check
	 * @throws IllegalArgumentException
	 *            if row is out of range
	 */
	private void checkRowRange(int row) {
		if (row < 0 || row >= cells.length) throw new IllegalArgumentException("The row " + row + "is out of range (" + cells.length + ")");
	}

	/**
	 * Helper method to determine if value is within the range
	 * 
	 * @param col
	 *           the column number to check
	 * @throws IllegalArgumentException
	 *            if row is out of range
	 */
	private void checkColumnRange(int col) {
		if (col < 0 || col >= colHeading.length) throw new IllegalArgumentException("The column " + col + "is out of range (" + colHeading.length + ")");
	}

	/** Ensures the row exists (expands if not) */
	private void ensureRowExists(int row) {
		// If out of range - create the range!
		if (cells.length <= row) {
			SGridCell[][] newCell = new SGridCell[row + 1][];
			for (int x = 0; x < cells.length; x++)
				newCell[x] = this.cells[x];
			cells = newCell;
		}
	}

	/** Ensures the column exists (expands if not) */
	private void ensureColumnExists(int col) {
		// Loop through the rows
		for (int r = 0; r < cells.length; r++) {
			// Does the column exist?
			if (cells[r].length <= col) {
				SGridCell[] newRow = new SGridCell[col + 1];
				System.arraycopy(cells[r], 0, newRow, 0, cells[r].length);
				cells[r] = newRow;
			}
		}
	}
}
