package com.topcoder.client.spectatorApp.announcer.properties;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.text.NumberFormat;
import java.text.ParseException;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import com.topcoder.client.spectatorApp.announcer.events.DefineComponentContestConnectionEvent;

/** 
 * Property panel for the DefineComponentContestConnectionEvent
 * @author Pops 
 */
public class DefineComponentContestConnectionPropertyPanel extends PropertyPanel<DefineComponentContestConnectionEvent> {
	/**
	 * The title field
	 */
	private JTextField titleField = new JTextField();

	/**
	 * The contest id field
	 */
	private JFormattedTextField contestID = new JFormattedTextField();

	/**
	 * The round id field
	 */
	private JFormattedTextField roundID = new JFormattedTextField();

	/**
	 * The component id field
	 */
	private JFormattedTextField componentID = new JFormattedTextField();

	/**
	 * The url field
	 */
	private JTextField url = new JTextField();

	/**
	 * The polltime field
	 */
	private JFormattedTextField pollTime = new JFormattedTextField();

	/**
	 * Creates property panel for the define component contest
	 * 
	 * @param event
	 */
	public DefineComponentContestConnectionPropertyPanel(DefineComponentContestConnectionEvent event) {
		// Call super constructor
		super(event);
		
		// Setup the numeric field
		contestID.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		roundID.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		componentID.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		pollTime.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		
		// Setup the number formatter
		NumberFormatter intFormatter = new NumberFormatter(NumberFormat.getIntegerInstance());
		intFormatter.setValueClass(Integer.class);
		intFormatter.setMinimum(new Integer(0));
		
		NumberFormatter longFormatter = new NumberFormatter(NumberFormat.getNumberInstance());
		longFormatter.setValueClass(Long.class);
		longFormatter.setMinimum(new Long(0));
		

		// Create the factory and set the value
		final DefaultFormatterFactory intFactory = new DefaultFormatterFactory(intFormatter);
		contestID.setFormatterFactory(intFactory);
		roundID.setFormatterFactory(intFactory);
		
		
		final DefaultFormatterFactory longFactory = new DefaultFormatterFactory(longFormatter);
		componentID.setFormatterFactory(longFactory);
		pollTime.setFormatterFactory(longFactory);
		
		// Setup the fields
		titleField.setText(event.getTitle());
		contestID.setValue(new Integer(event.getContestID()));
		roundID.setValue(new Integer(event.getRoundID()));
		componentID.setValue(new Long(event.getComponentID()));
		url.setText(event.getUrl());
		pollTime.setValue(new Long(event.getPollTime()));
		
		// Setup the panel
		int currentRow = 0;
		this.add(new JLabel("Title: "), new GridBagConstraints(0, currentRow, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		this.add(titleField, new GridBagConstraints(1, currentRow, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		this.add(new JLabel("Contest ID: "), new GridBagConstraints(0, ++currentRow, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		this.add(contestID, new GridBagConstraints(1, currentRow, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		this.add(new JLabel("Round ID: "), new GridBagConstraints(0, ++currentRow, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		this.add(roundID, new GridBagConstraints(1, currentRow, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		this.add(new JLabel("Component ID: "), new GridBagConstraints(0, ++currentRow, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		this.add(componentID, new GridBagConstraints(1, currentRow, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		this.add(new JLabel("URL: "), new GridBagConstraints(0, ++currentRow, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		this.add(url, new GridBagConstraints(1, currentRow, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		this.add(new JLabel("Polling Time (in milliseconds): "), new GridBagConstraints(0, ++currentRow, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		this.add(pollTime, new GridBagConstraints(1, currentRow, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		this.add(new JLabel(), new GridBagConstraints(0, ++currentRow, 2, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
	}

	/**
	 * Sets the title to the current title displayed
	 */
	public void saveProperties() {
		// Commit if valid
		if (contestID.isEditValid()) try {
			contestID.commitEdit();
		} catch (ParseException e) {
		}
		
		if (roundID.isEditValid()) try {
			roundID.commitEdit();
		} catch (ParseException e) {
		}
		
		if (componentID.isEditValid()) try {
			componentID.commitEdit();
		} catch (ParseException e) {
		}
		
		if (pollTime.isEditValid()) try {
			pollTime.commitEdit();
		} catch (ParseException e) {
		}
		
		// Convienence cast
		DefineComponentContestConnectionEvent event = getEvent();
		
		// Sets the title back
		event.setTitle(titleField.getText());
		event.setContestID(((Integer) contestID.getValue()).intValue());
		event.setRoundID(((Integer) roundID.getValue()).intValue());
		event.setComponentID(((Long) componentID.getValue()).longValue());
		event.setUrl(url.getText());
		event.setPollTime(((Long) pollTime.getValue()).longValue());
		
		// Validate event
		try {
			event.validateEvent();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error Validating the event: \r\n" + e.toString(), "Error Validating Event", JOptionPane.ERROR_MESSAGE);
		}
	}
}
