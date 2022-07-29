/**
 * AddDialog.java
 *
 * Description:		The add plugin dialog
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.contestApplet.editors.setup;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.editors.*;
import java.util.*;
import java.io.*;

public class AddDialog implements ActionListener {

    private JFrame parent;
    private JDialog dial;
    private EditorPlugin target;
    private java.util.List existing;

    private JTextField name = createTextField(25);
    private JTextField entryPoint = createTextField(25);
    private JTextField classPath = createTextField(25);
    private JLabel nameLabel = createLabel("Name:");
    private JLabel entryPointLabel = createLabel("EntryPoint:");
    private JLabel classPathLabel = createLabel("ClassPath:");
    private JButton browse = createButton("Browse");
    private JButton okay = createButton("OK");
    private JButton cancel = createButton("Cancel");

    public AddDialog(JFrame parent, EditorPlugin target, java.util.List existing) {

        // Save the target
        this.target = target;
        this.parent = parent;
        this.existing = existing;

        // Create the dialog
        dial = new JDialog(parent, "Enter Plugin Information", true);
        Common.setLocationRelativeTo(parent, dial);

        // Setup the content pane
        Container contentPane = dial.getContentPane();
        contentPane.setBackground(Common.WPB_COLOR);

        // Set the max size for the browse button
        browse.setMaximumSize(new Dimension(100, 21));

        // Add listeners
        browse.addActionListener(this);
        okay.addActionListener(this);
        cancel.addActionListener(this);

        // Create a box to hold the OK/Cancel buttons
        Box buttonBox = Box.createHorizontalBox();
        buttonBox.add(Box.createHorizontalGlue());
        buttonBox.add(okay);
        buttonBox.add(Box.createHorizontalStrut(5));
        buttonBox.add(cancel);
        buttonBox.add(Box.createHorizontalStrut(5));

        // Combine them in all into a verticle box
        Box all = Box.createVerticalBox();
        all.add(Box.createVerticalStrut(15));
        all.add(createHorizontalBox(nameLabel, name, null));
        all.add(Box.createVerticalStrut(5));
        all.add(createHorizontalBox(entryPointLabel, entryPoint, null));
        all.add(Box.createVerticalStrut(5));
        all.add(createHorizontalBox(classPathLabel, classPath, browse));
        all.add(Box.createVerticalStrut(15));
        all.add(Box.createVerticalGlue());
        all.add(buttonBox);
        all.add(Box.createVerticalStrut(15));

        // Wrap the verticle into a horizontal for proper spacing
        Box spacing = Box.createHorizontalBox();
        spacing.add(Box.createHorizontalStrut(15));
        spacing.add(all);
        spacing.add(Box.createHorizontalStrut(15));

        // Add them to the dialog
        dial.getContentPane().add(spacing);
        dial.setResizable(false);
        dial.setSize(new Dimension(503, 180));
        //dial.setMinimumSize(new Dimension(503,180));
        dial.pack();
        dial.setVisible(true);
        //System.out.println(dial.getSize());
    }

    public void actionPerformed(ActionEvent e) {

        Object source = e.getSource();

        if (source == okay) {

            // Validate the entries
            if (name.getText().equals("")) {
                Common.showMessage("Error", "Please provide a plugin name", parent);
                return;
            } else if (entryPoint.getText().equals("")) {
                Common.showMessage("Error", "Please provide a entry point", parent);
                return;
            } else if (doesNameExist(name.getText())) {
                Common.showMessage("Error", "The plugin name already exists - choose another", parent);
                return;
            }

            // Set the plugin stuff
            target.setName(name.getText());
            target.setEntryPoint(entryPoint.getText());
            target.setClassPath(classPath.getText());


            // Try to instantiate it
            DynamicEditor editor = null;
            try {
                editor = PluginManager.getInstance().createEditor(target);
            } catch (InstantiationError er) {
                Common.showMessage("Instantiation Error", "Could not instantiate the plugin (see the java console for details).", parent);
                return;
            } catch (NoSuchMethodError er) {
                Common.showMessage("Editor Plugin Error", "The plugin does not implement the required methods for an editor plugin.", parent);
                return;
            } finally {
                if(editor!=null) editor.dispose();
            }
            
            // Close the dialog
            dial.dispose();

        } else if (source == cancel) {
            // Set the plugin name to nothing and Close the dialog
            target.setName("");
            dial.dispose();
        } else if (source == browse) {
            // Create a file chooser
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            fileChooser.setMultiSelectionEnabled(true);
            fileChooser.setPreferredSize(new Dimension(500, 300));
            fileChooser.setMinimumSize(new Dimension(500, 300));

            // Open it and add results back to the classpath
            int rc = fileChooser.showOpenDialog(null);

            if (rc == JFileChooser.APPROVE_OPTION) {
                if (classPath.getText().equals("")) {
                    classPath.setText(getFileNames(fileChooser.getSelectedFiles()));
                } else {
                    StringBuffer temp = new StringBuffer(classPath.getText());
                    temp.append(File.pathSeparator);
                    temp.append(getFileNames(fileChooser.getSelectedFiles()));
                    classPath.setText(temp.toString());
                }
            }
        }
    }

    private boolean doesNameExist(String name) {
        for(int x=existing.size()-1;x>=0;x--) {
            if(((EditorPlugin)existing.get(x)).getName().equals(name)) return true;
        }
        
        return false;
    }
    
    private static Box createHorizontalBox(JLabel label, JTextField field, JButton button) {
        Box temp = Box.createHorizontalBox();
        temp.add(Box.createHorizontalStrut(5));
        temp.add(label);
        temp.add(Box.createHorizontalStrut(5));
        temp.add(field);

        if (button != null) {
            temp.add(Box.createHorizontalStrut(5));
            temp.add(button);
        }

        temp.add(Box.createHorizontalGlue());
        return temp;
    }

    private static JTextField createTextField(int size) {
        JTextField temp = new JTextField(size);
        temp.setMaximumSize(new Dimension(200, 21));
        temp.setMinimumSize(new Dimension(200, 21));
        temp.setPreferredSize(new Dimension(200, 21));
        temp.setForeground(Common.FG_COLOR);
        temp.setCaretColor(Common.FG_COLOR);
        temp.setBackground(Common.BG_COLOR);
        return temp;
    }

    private static JLabel createLabel(String text) {
        JLabel temp = new JLabel(text);
        temp.setForeground(Common.FG_COLOR);
        temp.setBackground(Common.WPB_COLOR);
        temp.setMaximumSize(new Dimension(100, 21));
        temp.setMinimumSize(new Dimension(100, 21));
        temp.setPreferredSize(new Dimension(100, 21));
        return temp;
    }

    private static JButton createButton(String text) {
        JButton temp = new JButton(text);
        return temp;
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
