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
import com.topcoder.client.spectatorApp.announcer.events.DefineRoundEvent;

/**
 * A property panel that will allow setting/getting of the DefineRoundEvent
 * properties
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
public class DefineRoundPropertyPanel extends PropertyPanel {
	/**
	 * The title field
	 */
	private JTextField titleField = new JTextField();

	/**
	 * The round id field
	 */
	private JFormattedTextField roundID = new JFormattedTextField();

	/**
	 * The round name field
	 */
	private JTextField roundName = new JTextField();

	/**
	 * The contest id field
	 */
	private JFormattedTextField contestID = new JFormattedTextField();

	/**
	 * The round type field
	 */
	private JFormattedTextField roundType = new JFormattedTextField();

	/**
	 * Creates a Title Property Panel to display the title
	 * 
	 * @param event
	 */
	public DefineRoundPropertyPanel(DefineRoundEvent event) {
		// Call super constructor
		super(event);
		// Setup the numeric field
		roundID.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		roundType.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		contestID.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		// Setup the number formatter
		NumberFormatter formatter = new NumberFormatter(NumberFormat.getIntegerInstance());
		formatter.setValueClass(Integer.class);
		formatter.setMinimum(new Integer(0));
		// Create the factory and set the value
		DefaultFormatterFactory factory = new DefaultFormatterFactory(formatter);
		roundID.setFormatterFactory(factory);
		roundType.setFormatterFactory(factory);
		contestID.setFormatterFactory(factory);
		// Setup the fields
		titleField.setText(event.getTitle());
		roundID.setValue(new Integer(event.getRoundID()));
		roundType.setValue(new Integer(event.getRoundType()));
		contestID.setValue(new Integer(event.getContestID()));
		roundName.setText(event.getRoundName());
		// Setup the panel
		int currentRow = 0;
		this.add(new JLabel("Title: "), new GridBagConstraints(0, currentRow, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		this.add(titleField, new GridBagConstraints(1, currentRow, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		this.add(new JLabel("Contest ID: "), new GridBagConstraints(0, ++currentRow, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		this.add(contestID, new GridBagConstraints(1, currentRow, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		this.add(new JLabel("Round ID: "), new GridBagConstraints(0, ++currentRow, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		this.add(roundID, new GridBagConstraints(1, currentRow, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		this.add(new JLabel("Round Type: "), new GridBagConstraints(0, ++currentRow, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		this.add(roundType, new GridBagConstraints(1, currentRow, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		this.add(new JLabel("Round Name: "), new GridBagConstraints(0, ++currentRow, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		this.add(roundName, new GridBagConstraints(1, currentRow, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
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
		if (roundType.isEditValid()) try {
			roundType.commitEdit();
		} catch (ParseException e) {
		}
		// Convienence cast
		DefineRoundEvent event = (DefineRoundEvent) getEvent();
		// Sets the title back
		event.setTitle(titleField.getText());
		event.setRoundID(((Integer) roundID.getValue()).intValue());
		event.setContestID(((Integer) contestID.getValue()).intValue());
		event.setRoundType(((Integer) roundType.getValue()).intValue());
		event.setRoundName(roundName.getText());
		// Validate event
		try {
			event.validateEvent();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error Validating the event: \r\n" + e.toString(), "Error Validating Event", JOptionPane.ERROR_MESSAGE);
		}
	}
}
