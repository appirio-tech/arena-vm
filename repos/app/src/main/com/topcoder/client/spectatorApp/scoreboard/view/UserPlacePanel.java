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
import com.topcoder.client.spectatorApp.scoreboard.model.CompetitionTracker;
import com.topcoder.client.spectatorApp.scoreboard.model.PlacementChangeEvent;
import com.topcoder.client.spectatorApp.scoreboard.model.PlacementChangeListener;
import com.topcoder.client.spectatorApp.widgets.SBracket;
import com.topcoder.client.spectatorApp.widgets.SGridCell;
import com.topcoder.client.spectatorApp.widgets.SGridLayout;
import com.topcoder.client.spectatorApp.widgets.SGridSizingPolicy;
import com.topcoder.client.spectatorApp.widgets.SMargin;
import com.topcoder.client.spectatorApp.widgets.STextField;

public class UserPlacePanel extends SGridLayout {
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

	/** The placement tracker */
	private PlacementChangeHandler placementHandler = new PlacementChangeHandler();
	
	/** The scoreboard point tracker */
	private CompetitionTracker tracker;
	
	/** The coder id related to this panel */
	private int coderID;
	
	/**
	 * Constructs the user handle panel
	 */
	public UserPlacePanel(CompetitionTracker tracker, int coderID) {
		this.coderID = coderID;
		
		int idx = tracker.indexOfCoder(coderID);
		if (idx < 0) {
			throw new IllegalArgumentException("Unknown coder id: " + coderID);
		}
		
		// Create the fields to be shown
		handleField = new STextField(tracker.getCoder(idx).getHandle(), userHandleFont);
		handleField.setColor(Constants.getRankColor(tracker.getCoder(idx).getRank()));
		
		placementField = new STextField("", rankTitleFont);
		placementField.setLike(prototype);
		
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
		// this.setColumnSpacingPolicy(new
		// SGridSpacingPolicy.FixedSpacingPolicy(5));
		// this.setRowSpacingPolicy(SGridSpacingPolicy.NONE);
		this.setColumnSizingPolicy(new SGridSizingPolicy.CollapseStrategy(2, SGridSizingPolicy.NONE));
		this.setRowSizingPolicy(SGridSizingPolicy.EVEN_SIZING);
		
		// Set the vertical alignment 
		setVerticalAlignment(Math.max(placementField.getVerticalAlignment(), handleField.getVerticalAlignment()));
	
		//setDebugColor(Color.GREEN);
		
		// Lay it out...
		this.setGrid(cells);
		this.setSize(width, height);
	
		this.tracker = tracker;
		tracker.getPlacementTracker().getChangeSupport().addListener(placementHandler);
		placementHandler.placementChanged(null); // force a change to initially set the ranking
		
		doLayout();
	}

	public void dispose() {
		tracker.getPlacementTracker().getChangeSupport().removeListener(placementHandler);
		super.dispose();
	}
	
//	public void setSize(int width, int height) {
//		super.setSize(width, height);
//	}
	
	/** Returns the coder id for this panel */
	public int getCoderID()
	{
		return coderID;
	}
	
	/** Overridden to add debugging colors */
	public void render(Graphics2D g2D) {
		super.render(g2D);
		if (getDebugColor() != null) {
			g2D.setColor(getDebugColor());
			g2D.drawRect(getX(), getY(), getWidth() - 1, getHeight() - 1);
		}
	}
	
	private class PlacementChangeHandler implements PlacementChangeListener
	{
		public void placementChanged(PlacementChangeEvent evt) {
			int placement = tracker.getPlacementTracker().getPlacement(coderID); 
			if (placement < 1) {
				placementField.setText("");
			} else {
				placementField.setText(CommonRoutines.getRank(placement));
			}
		}
	}
}
