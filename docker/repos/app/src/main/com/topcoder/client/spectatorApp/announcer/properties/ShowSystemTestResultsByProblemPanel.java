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

import com.topcoder.client.spectatorApp.announcer.events.ShowSystemTestResultsByProblemEvent;

/**
 * A property panel that will allow setting/getting of the ShowSystemTestResultsByProblem
 * properties
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
public class ShowSystemTestResultsByProblemPanel extends PropertyPanel {

	/**
	 * The title field
	 */
	private JTextField titleField = new JTextField();
	
	/**
	 * The round id field
	 */
	private JFormattedTextField roundID = new JFormattedTextField();
	
	/**
	 * The problem id field
	 */
	private JFormattedTextField problemID = new JFormattedTextField();
	
	/**
	 * The delay field
	 */
	private JFormattedTextField delay = new JFormattedTextField();
	
	/**
	 * Creates a Title Property Panel to display the title
	 * @param event
	 */
	public ShowSystemTestResultsByProblemPanel(ShowSystemTestResultsByProblemEvent event) {
		// Call super constructor
		super(event);
		
		// Setup the roundid text field
		roundID.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		
		// Setup the number formatter
		NumberFormatter formatter = new NumberFormatter(NumberFormat.getIntegerInstance());
		formatter.setValueClass(Integer.class);
		formatter.setMinimum(new Integer(0));
		
		// Create the factory and set the value
		DefaultFormatterFactory factory = new DefaultFormatterFactory(formatter);
		roundID.setFormatterFactory(factory);
		roundID.setValue(new Integer(event.getRoundID()));
		problemID.setFormatterFactory(factory);
		problemID.setValue(new Integer(event.getProblemID()));
		delay.setFormatterFactory(factory);
		delay.setValue(new Integer(event.getDelay()));
	
		// Set the title field
		titleField.setText(event.getTitle());
		
		// Setup the panel
		int currentRow = 0;
		this.add(new JLabel("Title: "), new GridBagConstraints(0,currentRow,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		this.add(titleField, new GridBagConstraints(1,currentRow,1,1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		this.add(new JLabel("Round ID: "), new GridBagConstraints(0,++currentRow,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		this.add(roundID, new GridBagConstraints(1,currentRow,1,1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		this.add(new JLabel("Problem ID: "), new GridBagConstraints(0,++currentRow,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		this.add(problemID, new GridBagConstraints(1,currentRow,1,1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		this.add(new JLabel("Delay (seconds): "), new GridBagConstraints(0,++currentRow,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		this.add(delay, new GridBagConstraints(1,currentRow,1,1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		this.add(new JLabel(), new GridBagConstraints(0,++currentRow,2,1,1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0, 0));
		
	}

	/**
	 * Sets the title to the current title displayed
	 */
	public void saveProperties() {
		// Commit if valid
		if(roundID.isEditValid()) {
			try {
				roundID.commitEdit();
			} catch (ParseException e) {
			}
	    }
	
		// Commit if valid
		if(problemID.isEditValid()) {
			try {
				problemID.commitEdit();
			} catch (ParseException e) {
			}
		}
	
		// Commit if valid
		if(delay.isEditValid()) {
			try {
				delay.commitEdit();
			} catch (ParseException e) {
			}
		}
	
		// Sets the title back
		getEvent().setTitle(titleField.getText());
		((ShowSystemTestResultsByProblemEvent)getEvent()).setRoundID(((Integer)roundID.getValue()).intValue());
		((ShowSystemTestResultsByProblemEvent)getEvent()).setProblemID(((Integer)problemID.getValue()).intValue());
		((ShowSystemTestResultsByProblemEvent)getEvent()).setDelay(((Integer)delay.getValue()).intValue());
		
		// Validate event
		try {
			getEvent().validateEvent();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error Validating the event: \r\n" + e.toString(), "Error Validating Event", JOptionPane.ERROR_MESSAGE);
		}

	}
	
}
