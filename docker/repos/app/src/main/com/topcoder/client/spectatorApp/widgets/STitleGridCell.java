package com.topcoder.client.spectatorApp.widgets;



/**
 * The Title Grid cell used with an {@link STitleGrid}.  Simply describes
 * the title and it's related description
 */
public class STitleGridCell {
	/** The field containing the title */
	private STextField titleField;
	
	/** The field containing the description */
	private STextField descField;
	
	/**
	 * Constructs the title grid from the attributes
	 * @param titleField the title field
	 * @param descField the description field
	 */
	public STitleGridCell(STextField titleField, STextField descField) {
		this.titleField = titleField;
		this.descField = descField;
	}
	
	/**
	 * Returns the title field 
	 * @return the title field
	 */
	public STextField getTitleField() {
		return titleField;
	}
	
	/**
	 * Returns the description field
	 * @return the description field
	 */
	public STextField getDescField() {
		return descField;
	}
}
