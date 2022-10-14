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
import com.topcoder.client.spectatorApp.CommonRoutines;
import com.topcoder.client.spectatorApp.announcer.events.ShowComponentResultsByComponentIDEvent;

/**
 * A property panel that will allow setting/getting of the
 * ShowSystemTestResultsByProblem properties
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
public class ShowComponentResultsByComponentIDPanel extends PropertyPanel<ShowComponentResultsByComponentIDEvent> {
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
	 * The delay field
	 */
	private JFormattedTextField delay = new JFormattedTextField();

	/**
	 * Creates a Title Property Panel to display the title
	 * 
	 * @param event
	 */
	public ShowComponentResultsByComponentIDPanel(ShowComponentResultsByComponentIDEvent event) {
		// Call super constructor
		super(event);
		
		// Setup the roundid text field
		roundID.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		
		// Setup the number formatter
		NumberFormatter intFormatter = new NumberFormatter(NumberFormat.getIntegerInstance());
		intFormatter.setValueClass(Integer.class);
		intFormatter.setMinimum(new Integer(0));
		
		// Create the factory and set the value
		DefaultFormatterFactory intFactory = new DefaultFormatterFactory(intFormatter);
		contestID.setFormatterFactory(intFactory);
		contestID.setValue(new Integer(event.getContestID()));
		
		roundID.setFormatterFactory(intFactory);
		roundID.setValue(new Integer(event.getRoundID()));
		
		delay.setFormatterFactory(intFactory);
		delay.setValue(new Integer(event.getDelay()));

		// Set the title field
		titleField.setText(event.getTitle());
		
		// Setup the panel
		int currentRow = 0;
		this.add(new JLabel("Title: "), new GridBagConstraints(0, currentRow, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		this.add(titleField, new GridBagConstraints(1, currentRow, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		this.add(new JLabel("Contest ID: "), new GridBagConstraints(0, ++currentRow, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		this.add(contestID, new GridBagConstraints(1, currentRow, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		this.add(new JLabel("Round ID: "), new GridBagConstraints(0, ++currentRow, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		this.add(roundID, new GridBagConstraints(1, currentRow, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		this.add(new JLabel("Delay (seconds): "), new GridBagConstraints(0, ++currentRow, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		this.add(delay, new GridBagConstraints(1, currentRow, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		this.add(new JLabel(), new GridBagConstraints(0, ++currentRow, 2, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
	}

	/**
	 * Sets the title to the current title displayed
	 */
	public void saveProperties() {
		// Commit if valid
		if (contestID.isEditValid()) {
			try {
				contestID.commitEdit();
			} catch (ParseException e) {
			}
		}
		if (roundID.isEditValid()) {
			try {
				roundID.commitEdit();
			} catch (ParseException e) {
			}
		}
		// Commit if valid
		// Commit if valid
		if (delay.isEditValid()) {
			try {
				delay.commitEdit();
			} catch (ParseException e) {
			}
		}
		// Sets the title back
		getEvent().setTitle(titleField.getText());
		getEvent().setContestID(((Integer) contestID.getValue()).intValue());
		getEvent().setRoundID(((Integer) roundID.getValue()).intValue());
		getEvent().setDelay(((Integer) delay.getValue()).intValue());
		
		// Validate event
		try {
			getEvent().validateEvent();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error Validating the event: \r\n" + e.toString(), "Error Validating Event", JOptionPane.ERROR_MESSAGE);
		}
	}
}
