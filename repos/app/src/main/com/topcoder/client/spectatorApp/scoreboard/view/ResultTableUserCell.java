/**
 * UserHandlePanel.java Description: Header panel to the scoreboard rooms
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
package com.topcoder.client.spectatorApp.scoreboard.view;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Insets;
import com.topcoder.client.spectatorApp.CommonRoutines;
import com.topcoder.client.spectatorApp.Constants;
import com.topcoder.client.spectatorApp.FontFactory;
import com.topcoder.client.spectatorApp.messages.TCSCoderInfo;
import com.topcoder.client.spectatorApp.widgets.SBracket;
import com.topcoder.client.spectatorApp.widgets.SGridCell;
import com.topcoder.client.spectatorApp.widgets.SGridLayout;
import com.topcoder.client.spectatorApp.widgets.SGridSizingPolicy;
import com.topcoder.client.spectatorApp.widgets.SMargin;
import com.topcoder.client.spectatorApp.widgets.STextField;

public class ResultTableUserCell extends SGridLayout {
	/** Font used for the "rank" title */
	private static final Font rankTitleFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_BOLD, Font.PLAIN, 24);

	/** Font used for th user handles */
	private static final Font userHandleFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_BOOK, Font.PLAIN, 36);

	/** The placement field (ie their current place in the room) */
	private STextField placementField;

	/** The handle field */
	private STextField handleField;
	
	/** The cells constructing this */
	private SGridCell[][] cells;

	/** The proto-type */
	private final static STextField prototype = new STextField("99th", rankTitleFont);
	
	/** The coder id related to this panel */
	private final TCSCoderInfo coderInfo;
	
	private final int rank;
	
	/**
	 * Constructs the user handle panel
	 */
	public ResultTableUserCell(int rank, TCSCoderInfo coderInfo) {
		this.coderInfo = coderInfo;
		this.rank = rank;
		
		// Create the fields to be shown
		handleField = new STextField(coderInfo.getHandle(), userHandleFont);
		handleField.setColor(Constants.getRankColor(coderInfo.getTcRating()));
		
		placementField = new STextField("", rankTitleFont);
		placementField.setLike(prototype);
		placementField.setText(CommonRoutines.getRank(rank));
		
		SBracket leftBracket = new SBracket();
		SBracket rightBracket = new SBracket(false);
		
		// Setup the cells
		cells = new SGridCell[][] { { 
			new SGridCell(new SMargin(placementField, new Insets(0, 3, 0, 3)), SGridCell.VERTICAL_ALIGNMENT_WEST, SGridCell.NONE), 
			new SGridCell(leftBracket, SGridCell.WEST, SGridCell.VERTICAL),
			new SGridCell(handleField, SGridCell.VERTICAL_ALIGNMENT_WEST, SGridCell.HORIZONTAL), 
			new SGridCell(rightBracket, SGridCell.WEST, SGridCell.VERTICAL) } };

		int width=0,height=0;
		for (int x = 0;x<cells[0].length;x++) {
			//cells[0][x].setDebugColor(Color.red);
			width += cells[0][x].getWidth();
			height = Math.max(height, cells[0][x].getHeight());
		}
		
		// Set the spacing policies to none
		this.setColumnSizingPolicy(new SGridSizingPolicy.CollapseStrategy(2, SGridSizingPolicy.NONE));
		this.setRowSizingPolicy(SGridSizingPolicy.EVEN_SIZING);
		
		// Set the vertical alignment 
		setVerticalAlignment(Math.max(placementField.getVerticalAlignment(), handleField.getVerticalAlignment()));
	
		//setDebugColor(Color.GREEN);
		
		// Lay it out...
		this.setGrid(cells);
		this.setSize(width, height);
		
		doLayout();
	}

	/** Overridden to add debugging colors */
	public void render(Graphics2D g2D) {
		super.render(g2D);
		if (getDebugColor() != null) {
			g2D.setColor(getDebugColor());
			g2D.drawRect(getX(), getY(), getWidth() - 1, getHeight() - 1);
		}
	}
}
