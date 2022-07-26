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
import com.topcoder.client.spectatorApp.scoreboard.model.ScoreboardPointTracker;
import com.topcoder.client.spectatorApp.widgets.SGridCell;
import com.topcoder.client.spectatorApp.widgets.SGridLayout;
import com.topcoder.client.spectatorApp.widgets.SGridSizingPolicy;
import com.topcoder.client.spectatorApp.widgets.SGridSpacingPolicy;
import com.topcoder.client.spectatorApp.widgets.SMargin;
import com.topcoder.client.spectatorApp.widgets.STextField;
import com.topcoder.client.spectatorApp.widgets.STotalPointTrackingTextField;

public class UserPlaceScorePanel extends SGridLayout {
	/** Font used for the "rank" title */
	private static final Font font = FontFactory.getInstance().getFont(FontFactory.FUTURA_BOLD, Font.PLAIN, 24);

	/** The cells that make up this panel */
	private SGridCell[][] cells;

	/** The placement field */
	private STextField placementField;

	/** The handle field */
	private STextField handleField;

	/** The point field */
	private STextField pointField;

	/** The proto-type */
	private final static STextField prototypeRank = new STextField("99th", font);

	/** The placement tracker */
	private PlacementChangeHandler placementHandler = new PlacementChangeHandler();
	
	/** The scoreboard point tracker */
	private CompetitionTracker tracker;
	
	/** The coder id related to this panel */
	private int coderID;
	
	/**
	 * Constructs the user handle panel
	 */
	public UserPlaceScorePanel(ScoreboardPointTracker pointTracker, int coderID) {
		this.coderID = coderID;
		this.tracker = pointTracker;
		
		// Create the fields to be shown
		int row = pointTracker.indexOfCoder(coderID);
		if (row < 0) {
			throw new IllegalArgumentException("Unknown coder id: " + coderID);
		}
		
		handleField = new STextField(pointTracker.getCoder(row).getHandle(), font);
		handleField.setColor(Constants.getRankColor(pointTracker.getCoder(row).getRank()));
		handleField.setJustification(STextField.LEFT);
		
		placementField = new STextField("", font);
		placementField.setLike(prototypeRank);
		
		STextField dashField = new STextField("-", font);
		pointField = new STotalPointTrackingTextField(pointTracker, coderID, font);
		
		SMargin placeMargin = new SMargin(placementField, new Insets(0,0,0,3));
		SMargin dashMargin = new SMargin(dashField, new Insets(0,3,0,3));
		
		final int fixedWidth = placeMargin.getWidth() + dashMargin.getWidth() + pointField.getWidth();
		
		// Setup the cells
		cells = new SGridCell[][] { { 
					new SGridCell(placeMargin, SGridCell.VERTICAL_ALIGNMENT_EAST, SGridCell.NONE), 
					new SGridCell(handleField, SGridCell.VERTICAL_ALIGNMENT_WEST, SGridCell.HORIZONTAL),
					new SGridCell(dashMargin, SGridCell.VERTICAL_ALIGNMENT_WEST, SGridCell.NONE), 
					new SGridCell(pointField, SGridCell.VERTICAL_ALIGNMENT_WEST, SGridCell.NONE), } };
		
		this.setColumnSizingPolicy(new SGridSizingPolicy() {
			public void assignSizes(int[] values, int total) {
				handleField.resize();
				int diff = total - fixedWidth;
				if (diff > handleField.getWidth()) {
					values[1] = handleField.getWidth();
				} else {
					values[1] = diff;
				}
			}			
		});
		this.setColumnSpacingPolicy(new SGridSpacingPolicy.EdgePercentageSpacingPolicy(1,1));
		this.setRowSizingPolicy(SGridSizingPolicy.EDGE_SIZING);
		
		// Set the vertical alignment
		int va = Math.max(placementField.getVerticalAlignment(), handleField.getVerticalAlignment());
		va = Math.max(va, pointField.getVerticalAlignment());
		setVerticalAlignment(va);
	
		// Lay it out...
		this.setGrid(cells);
		doLayout();
		
		tracker.getPlacementTracker().getChangeSupport().addListener(placementHandler);
		placementHandler.placementChanged(null); // force a change to initially set the ranking
		
	}

	public void dispose() {
		tracker.getPlacementTracker().getChangeSupport().removeListener(placementHandler);
		super.dispose();
	}
	
	/**
	 * Resizes this component to fit
	 */
//	public void resize() {
//		// Ask all the panels to resize themselves
//		for (int x = 0; x < cells[0].length; x++) {
//			cells[0][x].resize();
//		}
//		super.resize();
//	}
	
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
