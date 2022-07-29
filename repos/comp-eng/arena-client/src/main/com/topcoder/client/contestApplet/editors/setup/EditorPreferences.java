/**
 * EditorPreferences.java
 *
 * Description:		Table model for the editor of plugins
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.contestApplet.editors.setup;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
//import javax.swing.border.*;
import com.topcoder.client.contestApplet.common.*;

//import com.topcoder.client.contestApplet.widgets.*;


public class EditorPreferences extends JDialog implements ActionListener, WindowListener {

    // Create the buttons
    JButton addButton = new JButton("Add");
    JButton deleteButton = new JButton("Delete");
    JButton configButton = new JButton("Configure");
    JButton saveButton = new JButton("Save");
    JButton closeButton = new JButton("Close");
    JButton browseButton = new JButton("Browse");

    JTextField commonPath  = new JTextField();
    
    EditorPreferencesTableModel model;
    EditorPreferencesTable table;
    
    LocalPreferences pref = LocalPreferences.getInstance();
    String origCommonPath;

    public EditorPreferences(JFrame parent) {
        super(parent, "Editor Preferences", true);

        Common.setLocationRelativeTo(parent, this);

        // Initialize the table stuff
        model = new EditorPreferencesTableModel(parent);
        table = new EditorPreferencesTable(model);

        // Set the close operations
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(this);

        // Get the content pane and set attributes
        Container pane = getContentPane();
        pane.setBackground(Common.WPB_COLOR);
        pane.setLayout(new GridBagLayout());

        // Set the common lib
        origCommonPath = pref.getPluginCommonPath();
        if(origCommonPath==null) origCommonPath="";
        commonPath.setText(origCommonPath);
        
        // Make the buttons the same size
        Dimension size = new Dimension(89, 27);
        addButton.setMaximumSize(size);
        deleteButton.setMaximumSize(size);
        configButton.setMaximumSize(size);
        closeButton.setMaximumSize(size);
        browseButton.setMaximumSize(size);

        JLabel commonLabel = new JLabel("Common ClassPath: ");
        commonLabel.setForeground(Common.FG_COLOR);
        commonLabel.setBackground(Common.BG_COLOR);
        
        
        // Layout the common library row
        pane.add(commonLabel, new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(10,10,5,5),0,0));
        pane.add(commonPath, new GridBagConstraints(1,0,1,1,1,0,GridBagConstraints.WEST,GridBagConstraints.BOTH,new Insets(10,0,5,0),0,0));
        pane.add(browseButton, new GridBagConstraints(2,0,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(10,5,5,10),0,0));
        
        // Layout the table
        pane.add(table, new GridBagConstraints(0,1,3,1,1,1,GridBagConstraints.NORTHWEST,GridBagConstraints.BOTH,new Insets(5,5,5,5),0,0));
        
        // Layout the buttons
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.add(addButton, new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(5,5,5,0),0,0));
        buttonPanel.add(deleteButton, new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(5,5,5,0),0,0));
        buttonPanel.add(configButton, new GridBagConstraints(2,0,1,1,1,1,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(5,5,5,0),0,0));
        buttonPanel.add(saveButton, new GridBagConstraints(4,0,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(5,5,5,0),0,0));
        buttonPanel.add(closeButton, new GridBagConstraints(5,0,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(5,5,5,10),0,0));
        pane.add(buttonPanel, new GridBagConstraints(0,2,3,1,0,0,GridBagConstraints.EAST,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,0),0,0));

        // Setup actionlisteners
        addButton.addActionListener(this);
        deleteButton.addActionListener(this);
        configButton.addActionListener(this);
        saveButton.addActionListener(this);
        closeButton.addActionListener(this);
        browseButton.addActionListener(this);

        // Pack it
        this.pack();
    }

    public void actionPerformed(ActionEvent e) {

        // End any pending editing
        endEditing();

        // Get the source of the action
        Object source = e.getSource();

        // Get the table
        JTable jtable = table.getTable();

        // Determine what to do..
        if (source == addButton) {
            int newrow = model.addRow();
            if (newrow >= 0) {
                jtable.setRowSelectionInterval(newrow, newrow);
                jtable.scrollRectToVisible(jtable.getCellRect(newrow, 0, true));
            }
        } else if (source == deleteButton) {
            model.deleteRow(jtable.getSelectedRow());
        } else if (source == configButton) {
            // Set the common path (in case it's changed)
            pref.setPluginCommonPath(commonPath.getText());
            
            model.configure(jtable.getSelectedRow());
        } else if (source == saveButton) {
            int rc = model.save();
            if (rc != EditorPreferencesTableModel.SAVESUCCESS && rc != EditorPreferencesTableModel.SAVEEXCEPTION) jtable.setRowSelectionInterval(rc, rc);
        } else if (source == closeButton) {
            windowClosing(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        } else if (source == browseButton) {
            // Create a file chooser
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            fileChooser.setMultiSelectionEnabled(true);
            fileChooser.setPreferredSize(new Dimension(500, 300));
            fileChooser.setMinimumSize(new Dimension(500, 300));

            // Open it and add results back to the classpath
            int rc = fileChooser.showOpenDialog(null);

            if (rc == JFileChooser.APPROVE_OPTION) {
                if (commonPath.getText().equals("")) {
                    commonPath.setText(getFileNames(fileChooser.getSelectedFiles()));
                } else {
                    StringBuffer temp = new StringBuffer(commonPath.getText());
                    temp.append(File.pathSeparator);
                    temp.append(getFileNames(fileChooser.getSelectedFiles()));
                    commonPath.setText(temp.toString());
                }
            }
        }
    }

    public void windowClosing(WindowEvent e) {
        // End any pending editing
        endEditing();

        // Are saves pending?
        if (model.savePending() || !commonPath.getText().trim().equals(origCommonPath)) {

            // Should we save?
            if (Common.confirm("Save Pending", "Changes are pending.  Do you want to save before closing?", this)) {

                // Save the common lib
                pref.setPluginCommonPath(commonPath.getText());
                
                // Try to save
                int rc = model.save();
                if (rc == EditorPreferencesTableModel.SAVEEXCEPTION) return;
                if (rc != EditorPreferencesTableModel.SAVESUCCESS) {
                    table.getTable().setRowSelectionInterval(rc, rc);
                    return;
                }
                
                Common.showMessage("Warning", "You MAY need to close all browsers and restart for any changes to implemented", this);
            }
        }
        
        // Reload the preferences to restore stuff if cancelled changes were made
        try {
            pref.reload();
        } catch (IOException io) {}
        
        // Close the window
        dispose();
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowClosed(WindowEvent e) {
    }

    public void windowDeactivated(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowOpened(WindowEvent e) {
    }

    private void endEditing() {
        // Save the common lib (so that the add can pick it up)
        pref.setPluginCommonPath(commonPath.getText());
                
        // Force any pending editing changes when a button is pressed
        JTable jtable = table.getTable();
        if (jtable.isEditing()) {
            TableCellEditor tc = jtable.getCellEditor(jtable.getEditingRow(), jtable.getEditingColumn());
            if (tc != null) tc.stopCellEditing();
        }
    }

    private static String getFileNames(File[] files) {
        StringBuffer str = new StringBuffer();
        for (int x = files.length - 1; x >= 0; x--) {
            String fileName;
            try {
                fileName = files[x].getCanonicalPath();
            } catch (IOException e) {
                fileName = files[x].getAbsolutePath();
            }
            if (files[x].isDirectory()) fileName += File.separator;
            str.append(fileName);
            if (x > 0) str.append(File.pathSeparator);
        }
        return str.toString();
    }
}

