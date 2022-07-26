/**
 * ChallengeTotalValuePanel.java Description: Total value panel to the
 * scoreboard rooms
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
package com.topcoder.client.spectatorApp.scoreboard.view;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Insets;
import com.topcoder.client.spectatorApp.FontFactory;
import com.topcoder.client.spectatorApp.views.AnimatePanel;
import com.topcoder.client.spectatorApp.widgets.SGridCell;
import com.topcoder.client.spectatorApp.widgets.SGridLayout;
import com.topcoder.client.spectatorApp.widgets.SGridSizingPolicy;
import com.topcoder.client.spectatorApp.widgets.SMargin;
import com.topcoder.client.spectatorApp.widgets.STextField;

public abstract class AbstractComponentValue extends SGridLayout implements AnimatePanel {
	/** Font used for the point value */
	private static final Font avgValueFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_BOLD, Font.PLAIN, 30);

	/** Font used for the place */
	private static final Font placeFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_BOOK, Font.PLAIN, 18);

	/** The placement field (ie their current place in the room) */
	protected STextField placementField;

	/** The average field */
	protected STextField avgValueField;
	
	/** The cells constructing this */
	private SGridCell[][] cells;

	/** The average value field */
	private final static STextField averagePrototype = new STextField("##0.00", avgValueFont);

	/** The placement  */
	private final static STextField placePrototype = new STextField("9th", placeFont);

	/** Constructs the user handle panel */
	public AbstractComponentValue() {
		// Create the fields to be shown
		avgValueField = new STextField("", avgValueFont);
		avgValueField.setLike(averagePrototype);
		avgValueField.setJustification(STextField.CENTER);
		
		placementField = new STextField("", placeFont);
		placementField.setLike(placePrototype);
		placementField.setJustification(STextField.CENTER);
		
		cells = new SGridCell[][] {  
			{ new SGridCell(new SMargin(avgValueField, new Insets(1, 3, 1, 3)), SGridCell.CENTER, SGridCell.HORIZONTAL) },  
			{ new SGridCell(new SMargin(placementField, new Insets(1, 3, 1, 3)), SGridCell.CENTER, SGridCell.HORIZONTAL) } };  
		
		int width=0,height=0;
		for (int x = 0;x<cells.length;x++) {
			width  = Math.max(width, cells[x][0].getWidth());
			height += cells[x][0].getHeight();
		}

		this.setColumnSizingPolicy(SGridSizingPolicy.EVEN_SIZING);
		this.setRowSizingPolicy(SGridSizingPolicy.EVEN_SIZING);

		// Set the vertical alignment 
		setVerticalAlignment(height / 2);
	
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
