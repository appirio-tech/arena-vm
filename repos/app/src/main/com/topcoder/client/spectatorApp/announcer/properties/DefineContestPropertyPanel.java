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

import com.topcoder.client.spectatorApp.announcer.events.DefineContestEvent;

/**
 * A property panel that will allow setting/getting of the DefineContestEvent
 * properties
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
public class DefineContestPropertyPanel extends PropertyPanel {

	/**
	 * The title field
	 */
	private JTextField titleField = new JTextField();
	
	/**
	 * The contest id field
	 */
	private JFormattedTextField contestID = new JFormattedTextField();
	
	/**
	 * The contest name field
	 */
	private JTextField contestName = new JTextField();
	
	/**
	 * The Large Logo Filename field
	 */
	private JTextField logoLargeFileName = new JTextField();
	
	/**
	 * The Small Logo Filename field
	 */
	private JTextField logoSmallFileName = new JTextField();
	
	/**
	 * The Sponsor Logo Filename field
	 */
	private JTextField sponsorLogoFileName = new JTextField();
	
	/**
	 * Creates a Title Property Panel to display the title
	 * 
	 * @param event
	 */
	public DefineContestPropertyPanel(DefineContestEvent event) {
		// Call super constructor
		super(event);
		
		// Setup the numeric field
		contestID.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		
		// Setup the number formatter
		NumberFormatter formatter = new NumberFormatter(NumberFormat.getIntegerInstance());
		formatter.setValueClass(Integer.class);
		formatter.setMinimum(new Integer(0));
		
		// Create the factory and set the value
		DefaultFormatterFactory factory = new DefaultFormatterFactory(formatter);
		contestID.setFormatterFactory(factory);
		
		// Setup the fields
		titleField.setText(event.getTitle());
		contestID.setValue(new Integer(event.getContestID()));
		contestName.setText(event.getContestName());
		logoLargeFileName.setText(event.getLogoLargeFileName());
		logoSmallFileName.setText(event.getLogoSmallFileName());
		sponsorLogoFileName.setText(event.getSponsorFileName());
		
		// Setup the panel
		int currentRow = 0;
		this.add(new JLabel("Title: "), new GridBagConstraints(0,currentRow,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		this.add(titleField, new GridBagConstraints(1,currentRow,1,1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		this.add(new JLabel("Contest ID: "), new GridBagConstraints(0,++currentRow,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		this.add(contestID, new GridBagConstraints(1,currentRow,1,1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		this.add(new JLabel("Contest Name: "), new GridBagConstraints(0,++currentRow,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		this.add(contestName, new GridBagConstraints(1,currentRow,1,1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		this.add(new JLabel("Large Logo Filename: "), new GridBagConstraints(0,++currentRow,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		this.add(logoLargeFileName, new GridBagConstraints(1,currentRow,1,1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		this.add(new BrowseButton(logoLargeFileName), new GridBagConstraints(2,currentRow,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		this.add(new JLabel("Small Logo Filename: "), new GridBagConstraints(0,++currentRow,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		this.add(logoSmallFileName, new GridBagConstraints(1,currentRow,1,1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		this.add(new BrowseButton(logoSmallFileName), new GridBagConstraints(2,currentRow,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		this.add(new JLabel("Sponsor Logo Filename: "), new GridBagConstraints(0,++currentRow,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		this.add(sponsorLogoFileName, new GridBagConstraints(1,currentRow,1,1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		this.add(new BrowseButton(sponsorLogoFileName), new GridBagConstraints(2,currentRow,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		this.add(new JLabel(), new GridBagConstraints(0,++currentRow,2,1,1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0, 0));
	}
	
	/**
	 * Sets the title to the current title displayed
	 */
	public void saveProperties() {
		// Commit if valid
		if (contestID.isEditValid()) try { contestID.commitEdit(); } catch (ParseException e) {}
		
		// Convienence cast
		DefineContestEvent event = (DefineContestEvent) getEvent();
		
		// Sets the title back
		event.setTitle(titleField.getText());
		event.setContestID(((Integer) contestID.getValue()).intValue());
		event.setContestName(contestName.getText());
		event.setLogoLargeFileName(logoLargeFileName.getText());
		event.setLogoSmallFileName(logoSmallFileName.getText());
		event.setSponsorFileName(sponsorLogoFileName.getText());
		
		// Validate event
		try {
			event.validateEvent();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error Validating the event: \r\n" + e.toString(), "Error Validating Event", JOptionPane.ERROR_MESSAGE);
		}

	}
}
