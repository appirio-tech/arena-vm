package com.topcoder.client.spectatorApp.announcer.properties;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import com.topcoder.client.spectatorApp.CommonRoutines;
import com.topcoder.client.spectatorApp.announcer.events.IgnorePhaseChangeEvent;

/**
 * A property panel that will allow setting/getting of the IgnorePhaseChangeEvent
 * properties
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
public class IgnorePhaseChangePropertyPanel extends PropertyPanel<IgnorePhaseChangeEvent> {
	/**
	 * The title field
	 */
	private JTextField titleField = new JTextField();

	/**
	 * The round id field
	 */
	private JCheckBox ignoreField = new JCheckBox();

	/**
	 * Creates a Title Property Panel to display the title
	 * 
	 * @param event
	 */
	public IgnorePhaseChangePropertyPanel(IgnorePhaseChangeEvent event) {
		// Call super constructor
		super(event);
		
		// Set the title field
		titleField.setText(event.getTitle());

		boolean ignore = event.isIgnore();
		ignoreField.setSelected(ignore);
		
		// Setup the panel
		int currentRow = 0;
		this.add(new JLabel("Title: "), new GridBagConstraints(0, currentRow, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		this.add(titleField, new GridBagConstraints(1, currentRow, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		this.add(new JLabel("Ignore: "), new GridBagConstraints(0, ++currentRow, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		this.add(ignoreField, new GridBagConstraints(1, currentRow, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
	}

	/**
	 * Sets the title to the current title displayed
	 */
	public void saveProperties() {
		// Sets the title back
		getEvent().setTitle(titleField.getText());

		getEvent().setIgnore(ignoreField.isSelected());
	}
}
