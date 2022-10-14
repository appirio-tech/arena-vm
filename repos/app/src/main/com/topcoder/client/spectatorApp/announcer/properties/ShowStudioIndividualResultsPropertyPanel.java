package com.topcoder.client.spectatorApp.announcer.properties;



import com.topcoder.client.spectatorApp.announcer.events.ShowStudioEvent;
import com.topcoder.client.spectatorApp.announcer.events.ShowStudioIndividualResultsEvent;
import java.awt.GridBagConstraints;

import java.awt.Insets;

import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;

import java.text.NumberFormat;



import javax.swing.JButton;

import javax.swing.JFileChooser;

import javax.swing.JFormattedTextField;

import javax.swing.JLabel;

import javax.swing.JOptionPane;

import javax.swing.JScrollPane;

import javax.swing.JTable;

import javax.swing.JTextField;

import javax.swing.table.DefaultTableModel;

import javax.swing.text.DefaultFormatterFactory;

import javax.swing.text.NumberFormatter;



import com.topcoder.client.spectatorApp.announcer.events.AnnounceReviewBoardEvent;



/**
 *
 * A property panel that will allow setting/getting of the ShowRoundEvent
 *
 * properties
 *
 *
 *
 * @author Tim "Pops" Roberts
 *
 * @version 1.0
 *
 */

public class ShowStudioIndividualResultsPropertyPanel extends PropertyPanel{
    
    
    
    /**
     *
     * The title field
     *
     */
    
    private JTextField captionField = new JTextField();
    
    
    
    /**
     *
     * The Reviewers table
     *
     */
    
    private JTextField imageField = new JTextField();
    
    
    /**
     *
     * Creates a Title Property Panel to display the title
     *
     * @param event
     *
     */
    
    public ShowStudioIndividualResultsPropertyPanel(ShowStudioIndividualResultsEvent event) {
        
        // Call super constructor
        
        super(event);
        // Set the title field
        
        captionField.setText(event.getCaption());
        imageField.setText(event.getImageName());
        
        // Setup the panel
        
        int currentRow = 0;
        
        this.add(new JLabel("Caption: "), new GridBagConstraints(0,currentRow,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
        
        this.add(captionField, new GridBagConstraints(1,currentRow,1,1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
        
        this.add(new JLabel("Image: "), new GridBagConstraints(0,++currentRow,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
        
        this.add(imageField, new GridBagConstraints(1,currentRow,1,1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
        
        
        
        this.add(new JLabel(), new GridBagConstraints(0,++currentRow,2,1,1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0, 0));
        
        
        
    }
    
    
    
    /**
     *
     * Sets the title to the current title displayed
     *
     */
    
    public void saveProperties() {
        
        
       
        
        // Convience cast
        
        ShowStudioIndividualResultsEvent event = (ShowStudioIndividualResultsEvent)getEvent();
        
        
        
        // Set the title
        
        event.setCaption(captionField.getText());
        event.setImageName(imageField.getText());
        
        
        try {
            
            getEvent().validateEvent();
            
        } catch (Exception e) {
            
            JOptionPane.showMessageDialog(null, "Error Validating the event: \r\n" + e.toString(), "Error Validating Event", JOptionPane.ERROR_MESSAGE);
            
        }
        
        
        
    }
    
    
    
}

