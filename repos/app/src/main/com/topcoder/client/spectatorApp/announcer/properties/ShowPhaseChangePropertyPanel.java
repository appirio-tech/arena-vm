package com.topcoder.client.spectatorApp.announcer.properties;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.text.NumberFormat;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import com.topcoder.client.spectatorApp.announcer.events.ShowPhaseChangeEvent;
import com.topcoder.netCommon.contest.ContestConstants;

/**
 * A property panel that will allow setting/getting of the ShowPhaseChangeEvent
 * properties
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
public class ShowPhaseChangePropertyPanel extends PropertyPanel<ShowPhaseChangeEvent> {
	/**
	 * The title field
	 */
	private JTextField titleField = new JTextField();

	/**
	 * The phase id drop down
	 */
	private JComboBox phaseID = new JComboBox();

	/**
	 * The time allocated field
	 */
	private JFormattedTextField timeAllocated = new JFormattedTextField();

	/**
	 * Creates a Title Property Panel to display the title
	 * 
	 * @param event
	 */
	public ShowPhaseChangePropertyPanel(ShowPhaseChangeEvent event) {
		// Call super constructor
		super(event);
		
		// Setup the phase id model
		int selectionIDX = 0;
		DefaultComboBoxModel model = new DefaultComboBoxModel();
		for (int x = 0; x < ContestConstants.SPECTATOR_PHASES.length; x++) {
			model.addElement(new PhaseElement(ContestConstants.SPECTATOR_PHASES[x], ContestConstants.SPECTATOR_PHASE_NAMES[x]));
			if (ContestConstants.SPECTATOR_PHASES[x] == event.getPhaseID()) selectionIDX = x;
		}
		
		phaseID.setModel(model);
		phaseID.setSelectedIndex(selectionIDX);
		
		// Setup the number formatter
		NumberFormatter intFormatter = new NumberFormatter(NumberFormat.getIntegerInstance());
		intFormatter.setValueClass(Integer.class);
		intFormatter.setMinimum(new Integer(0));
		
		// Create the factory and set the value
		final DefaultFormatterFactory intFactory = new DefaultFormatterFactory(intFormatter);
		timeAllocated.setFormatterFactory(intFactory);

		// Set the title field
		titleField.setText(event.getTitle());
		timeAllocated.setValue(new Integer(event.getTimeAllocated()));
		
		// Setup the panel
		int currentRow = 0;
		this.add(new JLabel("Title: "), new GridBagConstraints(0, currentRow, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		this.add(titleField, new GridBagConstraints(1, currentRow, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		this.add(new JLabel("Phase: "), new GridBagConstraints(0, ++currentRow, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		this.add(phaseID, new GridBagConstraints(1, currentRow, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		this.add(new JLabel("Time Allocated (in seconds): "), new GridBagConstraints(0, ++currentRow, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		this.add(timeAllocated, new GridBagConstraints(1, currentRow, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		this.add(new JLabel(), new GridBagConstraints(0, ++currentRow, 2, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
	}

	/**
	 * Sets the title to the current title displayed
	 */
	public void saveProperties() {
		// Get the element selected
		PhaseElement elem = (PhaseElement) phaseID.getSelectedItem();
		
		// Sets the title back
		getEvent().setTitle(titleField.getText());
		getEvent().setPhaseID(elem.getPhaseID());
		getEvent().setTimeAllocated(((Integer) timeAllocated.getValue()).intValue());
		
		// Validate event
		try {
			getEvent().validateEvent();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error Validating the event: \r\n" + e.toString(), "Error Validating Event", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Class that maintains the relationship between the name and it's value
	 * 
	 * @author Tim "Pops" Roberts
	 * @version 1.0
	 */
	class PhaseElement {
		/** Holder of the phase id */
		private int phaseID;

		/** Textual description of the phase */
		private String phaseText;

		/** Constructor of the element */
		public PhaseElement(int phaseID, String phaseText) {
			this.phaseID = phaseID;
			this.phaseText = phaseText;
		}

		/** Returns the phase ID of this element */
		public int getPhaseID() {
			return phaseID;
		}

		/** Returns the textual description of this element */
		public String toString() {
			return phaseText;
		}
	}
}
