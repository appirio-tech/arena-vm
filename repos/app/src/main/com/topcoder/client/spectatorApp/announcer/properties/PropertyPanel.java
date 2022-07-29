package com.topcoder.client.spectatorApp.announcer.properties;

import java.awt.GridBagLayout;
import javax.swing.JPanel;
import com.topcoder.client.spectatorApp.announcer.events.AnnouncerEvent;

/**
 * The abstract base of all Property Panels. This abstract base provides:
 * <ol>
 * <li> Getter of the event (and constructor to save the event) </li>
 * <li> Sets the panel to a GridBagLayout </li>
 * </ol>
 * 
 * @author Tim "Pops" Roberts
 */
public abstract class PropertyPanel<E extends AnnouncerEvent> extends JPanel {
	/**
	 * The announcer event related to the properties
	 */
	private E event;

	/**
	 * Constructs this panel from the passed event
	 * 
	 * @param event
	 */
	public PropertyPanel(E event) {
		this.setLayout(new GridBagLayout());
		this.event = event;
	}

	/**
	 * Returns the event related to this property panel
	 * 
	 * @return the event related to this property panel
	 */
	public E getEvent() {
		return event;
	}

	/**
	 * This method will be called when the property panel should save the
	 * properties back to the event. Typically when the property panel is about
	 * to be dereferenced
	 */
	public abstract void saveProperties();
}
