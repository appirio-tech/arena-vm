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

import com.topcoder.client.spectatorApp.announcer.events.SetRoomWinnerEvent;

/**
 * A property panel that will allow setting/getting of the SetRoomWinnerEvent
 * properties
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
public class SetRoomWinnerPropertyPanel extends PropertyPanel {

	/**
	 * The title field
	 */
	private JTextField titleField = new JTextField();
	
	/**
	 * The room id field
	 */
	private JFormattedTextField roomID = new JFormattedTextField();
	
	/**
	 * The handle field
	 */
	private JTextField handle = new JTextField();
	
	/**
	 * Creates a Title Property Panel to display the title
	 * @param event
	 */
	public SetRoomWinnerPropertyPanel(SetRoomWinnerEvent event) {
		// Call super constructor
		super(event);
		
		// Setup the roundid text field
		roomID.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		
		// Setup the number formatter
		NumberFormatter formatter = new NumberFormatter(NumberFormat.getIntegerInstance());
		formatter.setValueClass(Integer.class);
		formatter.setMinimum(new Integer(0));
		
		// Create the factory and set the value
		DefaultFormatterFactory factory = new DefaultFormatterFactory(formatter);
		roomID.setFormatterFactory(factory);
		roomID.setValue(new Integer(event.getRoomID()));
	
		// Set the title field
		titleField.setText(event.getTitle());
		handle.setText(event.getHandle());
		
		// Setup the panel
		int currentRow = 0;
		this.add(new JLabel("Title: "), new GridBagConstraints(0,currentRow,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		this.add(titleField, new GridBagConstraints(1,currentRow,1,1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		this.add(new JLabel("Room ID: "), new GridBagConstraints(0,++currentRow,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		this.add(roomID, new GridBagConstraints(1,currentRow,1,1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		this.add(new JLabel("Handle: "), new GridBagConstraints(0,++currentRow,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		this.add(handle, new GridBagConstraints(1,currentRow,1,1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		this.add(new JLabel(), new GridBagConstraints(0,++currentRow,2,1,1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0, 0));
		
	}

	/**
	 * Sets the title to the current title displayed
	 */
	public void saveProperties() {
		// Commit if valid
		if(roomID.isEditValid()) {
			try {
				roomID.commitEdit();
			} catch (ParseException e) {
			}
	    }
		
		// Convience cast
		SetRoomWinnerEvent event = (SetRoomWinnerEvent)getEvent();
	
		// Sets the title back
		event.setTitle(titleField.getText());
		event.setRoomID(((Integer)roomID.getValue()).intValue());
		event.setHandle(handle.getText());
		
		// Validate event
		try {
			event.validateEvent();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error Validating the event: \r\n" + e.toString(), "Error Validating Event", JOptionPane.ERROR_MESSAGE);
		}

	}
	
}
