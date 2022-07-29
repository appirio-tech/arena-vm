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

import com.topcoder.client.spectatorApp.announcer.events.AnnounceTCSWinnersEvent;

/**
 * A property panel that will allow setting/getting of the AnnounceTCSWinnersEvent
 * properties
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
public class AnnounceTCSWinnerPropertyPanel extends PropertyPanel {

	/**
	 * The title field
	 */
	private JTextField titleField = new JTextField();
	
	/**
	 * The round id field
	 */
	private JFormattedTextField roundID = new JFormattedTextField();
	
	/**
	 * The winning design handle field
	 */
	private JTextField designHandle = new JTextField();
	
	/**
	 * The winning design average
	 */
	private JFormattedTextField designAverage = new JFormattedTextField();
	
	/**
	 * The winning design image
	 */
	private JTextField designImageFileName = new JTextField();
	
	/**
	 * The winning development handle field
	 */
	private JTextField developmentHandle = new JTextField();
	
	/**
	 * The winning development average
	 */
	private JFormattedTextField developmentAverage = new JFormattedTextField();
	
	/**
	 * The winning development image
	 */
	private JTextField developmentImageFileName = new JTextField();
        
        private JFormattedTextField designRating = new JFormattedTextField();
        
        private JFormattedTextField developmentRating = new JFormattedTextField();
	
	/**
	 * Creates a Title Property Panel to display the title
	 * 
	 * @param event
	 */
	public AnnounceTCSWinnerPropertyPanel(AnnounceTCSWinnersEvent event) {
		// Call super constructor
		super(event);
		
		// Setup the numeric field
		roundID.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		designAverage.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		developmentAverage.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
                designRating.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
                developmentRating.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		
		// Setup the number formatter
		NumberFormatter integerFormatter = new NumberFormatter(NumberFormat.getIntegerInstance());
		integerFormatter.setValueClass(Integer.class);
		integerFormatter.setMinimum(new Integer(0));
		
		NumberFormatter doubleFormatter = new NumberFormatter(NumberFormat.getNumberInstance());
		doubleFormatter.setValueClass(Double.class);
		doubleFormatter.setMinimum(new Double(0));
		
		// Create the factory and set the value
		DefaultFormatterFactory integerFactory = new DefaultFormatterFactory(integerFormatter);
		roundID.setFormatterFactory(integerFactory);
                
		
		DefaultFormatterFactory doubleFactory = new DefaultFormatterFactory(doubleFormatter);
		designAverage.setFormatterFactory(doubleFactory);
		developmentAverage.setFormatterFactory(doubleFactory);
                
                designRating.setFormatterFactory(integerFactory);
                developmentRating.setFormatterFactory(integerFactory);
		
		// Setup the fields
		titleField.setText(event.getTitle());
		roundID.setValue(new Integer(event.getRoundID()));
		designHandle.setText(event.getDesignHandle());
		designAverage.setValue(new Double(event.getDesignWinnerAverage()));
		designImageFileName.setText(event.getDesignImageFileName());
		developmentHandle.setText(event.getDevelopmentHandle());
		developmentAverage.setValue(new Double(event.getDevelopmentWinnerAverage()));
		developmentImageFileName.setText(event.getDevelopmentImageFileName());
                designRating.setValue(new Integer(event.getDesignWinnerRating()));
                developmentRating.setValue(new Integer(event.getDevelopmentWinnerRating()));
                
		
		
		// Setup the panel
		int currentRow = 0;
		this.add(new JLabel("Title: "), new GridBagConstraints(0,currentRow,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		this.add(titleField, new GridBagConstraints(1,currentRow,1,1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		this.add(new JLabel("Round ID: "), new GridBagConstraints(0,++currentRow,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		this.add(roundID, new GridBagConstraints(1,currentRow,1,1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		this.add(new JLabel("Design Handle: "), new GridBagConstraints(0,++currentRow,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		this.add(designHandle, new GridBagConstraints(1,currentRow,1,1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		this.add(new JLabel("Design Average: "), new GridBagConstraints(0,++currentRow,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		this.add(designAverage, new GridBagConstraints(1,currentRow,1,1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
                this.add(new JLabel("Design Rating: "), new GridBagConstraints(0,++currentRow,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
                this.add(designRating, new GridBagConstraints(1,currentRow,1,1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		this.add(new JLabel("Design Image Filename: "), new GridBagConstraints(0,++currentRow,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		this.add(designImageFileName, new GridBagConstraints(1,currentRow,1,1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		this.add(new BrowseButton(designImageFileName), new GridBagConstraints(2,currentRow,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		this.add(new JLabel("Development Handle: "), new GridBagConstraints(0,++currentRow,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		this.add(developmentHandle, new GridBagConstraints(1,currentRow,1,1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		this.add(new JLabel("Development Average: "), new GridBagConstraints(0,++currentRow,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		this.add(developmentAverage, new GridBagConstraints(1,currentRow,1,1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		this.add(new JLabel("Development Average: "), new GridBagConstraints(0,++currentRow,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		this.add(developmentRating, new GridBagConstraints(1,currentRow,1,1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
                this.add(new JLabel("Development Image Filename: "), new GridBagConstraints(0,++currentRow,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		this.add(developmentImageFileName, new GridBagConstraints(1,currentRow,1,1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		this.add(new BrowseButton(developmentImageFileName), new GridBagConstraints(2,currentRow,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		this.add(new JLabel(), new GridBagConstraints(0,++currentRow,2,1,1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0, 0));
	}
	
	/**
	 * Sets the title to the current title displayed
	 */
	public void saveProperties() {
		// Commit if valid
		if (roundID.isEditValid()) try { roundID.commitEdit(); } catch (ParseException e) {}
		if (designAverage.isEditValid()) try { designAverage.commitEdit(); } catch (ParseException e) {}
		if (developmentAverage.isEditValid()) try { developmentAverage.commitEdit(); } catch (ParseException e) {}
                if (designRating.isEditValid()) try { designRating.commitEdit(); } catch (ParseException e) {}
                if (developmentRating.isEditValid()) try { developmentRating.commitEdit(); } catch (ParseException e) {}
		
		// Convienence cast
		AnnounceTCSWinnersEvent event = (AnnounceTCSWinnersEvent) getEvent();
		
		// Sets the title back
		event.setTitle(titleField.getText());
		event.setRoundID(((Integer) roundID.getValue()).intValue());
		event.setDesignHandle(designHandle.getText());
		event.setDesignWinnerAverage(((Double)designAverage.getValue()).doubleValue());
		event.setDevelopmentHandle(developmentHandle.getText());
		event.setDevelopmentWinnerAverage(((Double)developmentAverage.getValue()).doubleValue());
		event.setDesignImageFileName(designImageFileName.getText());
		event.setDevelopmentImageFileName(developmentImageFileName.getText());
                event.setDesignWinnerRating(((Integer)designRating.getValue()).intValue());
                event.setDevelopmentWinnerRating(((Integer)developmentRating.getValue()).intValue());
		
		// Validate event
		try {
			event.validateEvent();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error Validating the event: \r\n" + e.toString(), "Error Validating Event", JOptionPane.ERROR_MESSAGE);
		}
	}
}
