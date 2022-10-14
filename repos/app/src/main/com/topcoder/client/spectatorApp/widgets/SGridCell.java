package com.topcoder.client.spectatorApp.widgets;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.log4j.Category;
import com.topcoder.client.spectatorApp.views.AnimatePanel;
import com.topcoder.client.spectatorApp.views.LayeredPanel;

/**
 * Class used with the {@link SGridLayout} class. Specifies the information
 * about any individual cell.
 * <p>
 * Three special anchors are defined here:
 * <ol>
 * <li>VERTICAL_ALIGNMENT - anchors in the middle based on
 * AnimatePanel.getVerticalAlignment()</li>
 * <li>VERTICAL_ALIGNMENT_EAST - anchors in the EAST based on
 * AnimatePanel.getVerticalAlignment()</li>
 * <li>VERTICAL_ALIGNMENT_WEST - anchors in the WEST based on
 * AnimatePanel.getVerticalAlignment()</li>
 * </ol>
 * Note: the cell interprets the VERTICAL_ALIGNMENT_XXX as center, east, west
 * respectively. It's up to the container then to align this cell on it's
 * vertical alignement axis.
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
public class SGridCell extends LayeredPanel {
	/** Reference to the logging category */
	private static final Category cat = Category.getInstance(SGridCell.class.getName());

	/** Where panel is anchored */
	private int anchor;

	/** Determine how the panel should 'stretch' */
	private int fill;

	/**
	 * Special anchor to anchor along the rows vertical alignment Vertical
	 * alignment and a fill of either VERTICAL or BOTH is dangerous because the
	 * vertical alignment could (will) push the edge of the panel over the cell's
	 * edge (because the vertical alignment usually will not equal the vertical
	 * middle).
	 */
	public static final int VERTICAL_ALIGNMENT = 0;

	/**
	 * Special anchor to anchor along the rows vertical alignment Vertical
	 * alignment and a fill of either VERTICAL or BOTH is dangerous because the
	 * vertical alignment could (will) push the edge of the panel over the cell's
	 * edge (because the vertical alignment usually will not equal the vertical
	 * middle).
	 */
	public static final int VERTICAL_ALIGNMENT_EAST = 1;

	/**
	 * Special anchor to anchor along the rows vertical alignment Vertical
	 * alignment and a fill of either VERTICAL or BOTH is dangerous because the
	 * vertical alignment could (will) push the edge of the panel over the cell's
	 * edge (because the vertical alignment usually will not equal the vertical
	 * middle).
	 */
	public static final int VERTICAL_ALIGNMENT_WEST = 2;

	/** Anchor to the north */
	public static final int NORTH = 3;

	/** Anchor to the northeast */
	public static final int NORTHEAST = 4;

	/** Anchor to the northwest */
	public static final int NORTHWEST = 5;

	/** Anchor to the east */
	public static final int EAST = 6;

	/** Anchor to the center */
	public static final int CENTER = 7;

	/** Anchor to the west */
	public static final int WEST = 8;

	/** Anchor to the south */
	public static final int SOUTH = 9;

	/** Anchor to the southeast */
	public static final int SOUTHEAST = 10;

	/** Anchor to the southwest */
	public static final int SOUTHWEST = 11;

	/** Allow no filling */
	public static final int NONE = 12;

	/** Fill both horizontal and vertically */
	public static final int BOTH = 13;

	/** Fill horizontally only */
	public static final int HORIZONTAL = 14;

	/** Fill vertically only */
	public static final int VERTICAL = 15;

	/**
	 * Constructor for the panel with a NORTHWEST anchor and a fill contraint of
	 * NONE
	 * 
	 * @param panel
	 *           the panel
	 */
	public SGridCell(AnimatePanel panel) {
		this(panel, NORTHWEST, NONE);
	}

	/**
	 * Constructor for the panel with a NORTHWEST anchor and the specified fill
	 * contraint
	 * 
	 * @param panel
	 *           the panel
	 * @param fill
	 *           the fill contraint
	 */
	public SGridCell(AnimatePanel panel, int fill) {
		this(panel, NORTHWEST, fill);
	}

	/**
	 * Constructor for the panel with specified anchor and fill
	 * 
	 * @param panel
	 *           the panel
	 * @param anchor
	 *           the anchor constraint
	 * @param fill
	 *           the fill constraint
	 */
	public SGridCell(AnimatePanel panel, int anchor, int fill) {
		assert panel != null : panel;
		// Save stuff
		this.anchor = anchor;
		this.fill = fill;
		
		// Set the panel (also causes a relayout - so must be done last)
		setPanel(panel);
	}

	/**
	 * Return the animate panel
	 * 
	 * @return the animate panel
	 */
	public AnimatePanel getPanel() {
		return getLayer(0);
	}

	/**
	 * Sets the panel used in this cell. Note: you MUST call the grid layout's
	 * doLayout() after setting cell
	 */
	public void setPanel(AnimatePanel panel) {
		// Set the panel position
		setLayer(0, panel);
		
		// Set the size to layout the panel
		if (getWidth() < 0 || getHeight() < 0) {
			if (panel == null) {
				super.setSize(1, 1);
			} else {
				super.setSize(panel.getWidth(), panel.getHeight());
			}
		}

		if (getVerticalAlignment() < 0) {
			int va = -1;
			if (panel != null) {
				va = panel.getVerticalAlignment();
			}
			if (va < 0 && getHeight() >= 0) va = getHeight() / 2;
			setVerticalAlignment(va);
		}
		
		layoutCell();
	}

	/**
	 * Helper method to determine if the cell is vertically aligned or not
	 * 
	 * @return true if vertically aligned, false otherwise
	 */
	public boolean isVerticallyAligned() {
		return anchor == VERTICAL_ALIGNMENT || anchor == VERTICAL_ALIGNMENT_EAST || anchor == VERTICAL_ALIGNMENT_WEST;
	}

	/**
	 * Return the anchor constraint
	 * 
	 * @return the anchor constraint
	 */
	public int getAnchor() {
		return anchor;
	}

	/**
	 * Return the fill constraint
	 * 
	 * @return the fill constraint
	 */
	public int getFill() {
		return fill;
	}

	/**
	 * Resizes first the internal panel and then the cell itself
	 */
	public void resize() {
		AnimatePanel panel = getPanel();
		if (panel == null) return;
		// Call the panel's resize method
		try {
			Method resize = panel.getClass().getMethod("resize", null);
			resize.invoke(panel, null);
		} catch (NoSuchMethodException e) {
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}
		layoutCell();
	}

	/** Overridden to set the panel */
	public void setSize(int width, int height) {
		// Set the size of the cell
		super.setSize(width, height);
		layoutCell();
	}
	
	public void setVerticalAlignment(int alignment) {
		super.setVerticalAlignment(alignment);
		layoutCell();
	}
	
	private void layoutCell()
	{
		int width = getWidth();
		int height = getHeight();
		
		// Get the panel
		AnimatePanel panel = getLayer(0);
		
		// If there is no panel - simply return;
		if (panel == null) return;
		
		// Get the panel width/height
		int panelWidth = panel.getWidth();
		int panelHeight = panel.getHeight();
		
		// Adjust based on the fill option
		switch (getFill()) {
		case NONE: {
			break;
		}
		case HORIZONTAL: {
			panelWidth = width;
			break;
		}
		case VERTICAL: {
			panelHeight = height;
			break;
		}
		case BOTH: {
			panelWidth = width;
			panelHeight = height;
			break;
		}
		default: {
			throw new AssertionError(getFill());
		}
		}
		// Set the panels width/height
		if (getFill() != NONE) panel.setSize(panelWidth, panelHeight);
		
		// Determine what position to place it in
		int cVA = getVerticalAlignment();
		int pVA = panel.getVerticalAlignment();
		int panelX = 0;
		int panelY = 0;
		switch (getAnchor()) {
		case NORTHEAST: {
			panelX = width - panelWidth;
			break;
		}
		case NORTH: {
			panelX = (width / 2) - (panelWidth / 2);
			break;
		}
		
		case VERTICAL_ALIGNMENT:
			panelX = (width / 2) - (panelWidth / 2);
			panelY = cVA - pVA;
			break;
			
		case CENTER: {
			panelX = (width / 2) - (panelWidth / 2);
			panelY = (height / 2) - (panelHeight / 2);
			break;
		}
		case NORTHWEST: {
			break;
		}
		case VERTICAL_ALIGNMENT_EAST:
			panelX = width - panelWidth;
			panelY = cVA - pVA;
			break;
			
		case EAST: {
			panelX = width - panelWidth;
			panelY = (height / 2) - (panelHeight / 2);
			break;
		}
		case VERTICAL_ALIGNMENT_WEST:
			panelY = cVA - pVA;
			break;
			
		case WEST: {
			panelY = (height / 2) - (panelHeight / 2);
			break;
		}
		case SOUTHEAST: {
			panelX = width - panelWidth;
			panelY = height - panelHeight;
			break;
		}
		case SOUTH: {
			panelX = (width / 2) - (panelWidth / 2);
			panelY = height - panelHeight;
			break;
		}
		case SOUTHWEST: {
			panelY = height - panelHeight;
			break;
		}
		default:
			throw new AssertionError(getAnchor());
		}
		panel.setPosition(panelX, panelY);
		
		if (panelX < 0 || panelY < 0 || panelX + panelWidth > width || panelY + panelHeight > height)
		{
			Rectangle ours = new Rectangle(0,0,width,height);
			Rectangle its = new Rectangle(panelX, panelY, panelWidth, panelHeight);
			cat.debug("Panel: " + panel.getClass().getName() + " exceeds the bounds of this cell");
			cat.debug("      ours: " + ours);
			cat.debug("      its: " + its);
		}
	}

	/** Overridden to add debugging colors */
	public void render(Graphics2D g2D) {
		super.render(g2D);
		if (getDebugColor() != null) {
			g2D.setPaint(getDebugColor());
			g2D.drawRect(getX(), getY(), getWidth() - 1, getHeight() - 1);
		}
	}

	/** Returns a nice debugging string */
	public String toString() {
		AnimatePanel panel = getPanel();
		return "[" + getX() + ", " + getY() + "] (" + getWidth() + ", " + getHeight() + ")"
					+ (panel == null ? "" : " : [" + panel.getX() + ", " + panel.getY() + "] (" + panel.getWidth() + ", " + panel.getHeight() + ")   '" + panel.toString() + "'");
	}
}
