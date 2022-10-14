package com.topcoder.client.spectatorApp.announcer.properties;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.topcoder.client.spectatorApp.announcer.events.ShowImageEvent;

/**
 * A property panel that will allow setting/getting of the ShowImageEvent
 * properties
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
public class ShowImagePropertyPanel extends PropertyPanel {

	/**
	 * The title field
	 */
	private JTextField titleField = new JTextField();
	
	/**
	 * The Image id field
	 */
	private JTextField imagePath = new JTextField();
	
	/**
	 * Creates a Title Property Panel to display the title
	 * @param event
	 */
	public ShowImagePropertyPanel(ShowImageEvent event) {
		// Call super constructor
		super(event);
		
		// Create the factory and set the value
		imagePath.setText(event.getImagePath());
	
		// Set the title field
		titleField.setText(event.getTitle());
		
		// Setup the panel
		int currentRow = 0;
		this.add(new JLabel("Title: "), new GridBagConstraints(0,currentRow,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		this.add(titleField, new GridBagConstraints(1,currentRow,1,1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		this.add(new JLabel("Image Path: "), new GridBagConstraints(0,++currentRow,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		this.add(imagePath, new GridBagConstraints(1,currentRow,1,1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		this.add(new JLabel(), new GridBagConstraints(0,++currentRow,2,1,1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0, 0));
		
	}

	/**
	 * Sets the title to the current title displayed
	 */
	public void saveProperties() {
		// Sets the title back
		getEvent().setTitle(titleField.getText());
		((ShowImageEvent)getEvent()).setImagePath(imagePath.getText());
		
		// Validate event
		try {
			getEvent().validateEvent();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error Validating the event: \r\n" + e.toString(), "Error Validating Event", JOptionPane.ERROR_MESSAGE);
		}

	}
	
}
