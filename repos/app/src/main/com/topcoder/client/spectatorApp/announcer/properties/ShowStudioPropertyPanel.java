package com.topcoder.client.spectatorApp.announcer.properties;



import com.topcoder.client.spectatorApp.announcer.events.AbstractShowScreenEvent;
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

public class ShowStudioPropertyPanel extends PropertyPanel{
    
    
    
    /**
     *
     * The title field
     *
     */
    
    private JTextField pathField = new JTextField();
    
    private JTextField timeField = new JTextField();
    
    private JTextField titleField = new JTextField();
    
    
    
    /**
     *
     * The Reviewers table
     *
     */
    
    private JTable computersTable = new JTable();
    
    
    
    /**
     *
     * The table model
     *
     */
    
    private DefaultTableModel computersModel = new DefaultTableModel();
    
    
    
    /**
     *
     * Creates a Title Property Panel to display the title
     *
     * @param event
     *
     */
    
    public ShowStudioPropertyPanel(AbstractShowScreenEvent event) {
        
        // Call super constructor
        
        super(event);
        
        
        // Populate the rows
        
        computersModel.addColumn("Computer");
        computersModel.addColumn("Handle");
        
        for(int x=0;x<event.getComputerNames().length;x++) {
            
            computersModel.addRow(new Object[] {
                
                event.getComputerNames()[x],
                event.getHandles()[x]
                        
            });
            
        }
        
        
        
        // Setup the table
        
        computersTable.setModel(computersModel);
        
        
        // Set the title field
        titleField.setText(event.getName());
        pathField.setText(event.getPath());
        timeField.setText(event.getTime());
        
        // Setup the panel
        
        int currentRow = 0;
        this.add(new JLabel("Name: "), new GridBagConstraints(0,currentRow,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
        
        this.add(titleField, new GridBagConstraints(1,currentRow,1,1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
        
        this.add(new JLabel("Path: "), new GridBagConstraints(0,++currentRow,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
        
        this.add(pathField, new GridBagConstraints(1,currentRow,1,1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
        
        this.add(new JLabel("Time: "), new GridBagConstraints(0,++currentRow,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
        
        this.add(timeField, new GridBagConstraints(1,currentRow,1,1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
        
        this.add(new JScrollPane(computersTable), new GridBagConstraints(0,++currentRow,2,1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
        
        this.add(new JLabel(), new GridBagConstraints(0,++currentRow,2,1,1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0, 0));
        
        
        
    }
    
    
    
    /**
     *
     * Sets the title to the current title displayed
     *
     */
    
    public void saveProperties() {
        
        
        
        // Force the table to stop editing
        
        if(computersTable.isEditing()) computersTable.getCellEditor().stopCellEditing();
        
        
        
        // Convience cast
        
        AbstractShowScreenEvent event = (AbstractShowScreenEvent)getEvent();
        
        
        
        // Set the title
        event.setName(titleField.getText());
        event.setPath(pathField.getText());
        event.setTime(timeField.getText());
        
        
        
        // Get all the values
        
        int N = computersTable.getRowCount();
        
        String[] computers = new String[N];
        String[] handles = new String[N];
        for(int x=0;x<N;x++) {
            
            computers[x] = (String)computersModel.getValueAt(x, 0);
            handles[x] = (String)computersModel.getValueAt(x, 1);
        }
        
        
        
        // Set'em
        
        event.setComputerNames(computers);
        event.setHandles(handles);
        // Validate event
        
        try {
            
            getEvent().validateEvent();
            
        } catch (Exception e) {
            
            JOptionPane.showMessageDialog(null, "Error Validating the event: \r\n" + e.toString(), "Error Validating Event", JOptionPane.ERROR_MESSAGE);
            
        }
        
        
        
    }
    
    
    
}

