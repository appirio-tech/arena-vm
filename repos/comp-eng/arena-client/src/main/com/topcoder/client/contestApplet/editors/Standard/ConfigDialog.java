/**
 * FindDialog.java
 *
 * Description:		Allows the user to enter find criteria and options
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.contestApplet.editors.Standard;

import com.topcoder.client.contestApplet.ContestApplet;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.common.LocalPreferences;

public class ConfigDialog implements ItemListener, ActionListener {

    JDialog dial;
    
    private LocalPreferences localPref = LocalPreferences.getInstance();
    private boolean autoIndent = localPref.isTrue(LocalPreferences.EDSTDINDENT);
    
    JCheckBox indentOption = new JCheckBox("Auto Indent", autoIndent);
    JButton okButton = new JButton("Save");
    JButton cancelButton = new JButton("Cancel");

    public ConfigDialog() {
        // Create the dialog
        dial = new JDialog((JFrame)null, "Standard Editor Options", true);

        // Setup the content pane
        Container contentPane = dial.getContentPane();
        contentPane.setBackground(Common.WPB_COLOR);

        // Add listeners
        indentOption.addItemListener(this);
        indentOption.setBackground(Common.WPB_COLOR);
        indentOption.setForeground(Common.FG_COLOR);
        
        okButton.addActionListener(this);
        cancelButton.addActionListener(this);

        // Create the options box
        int spacing = 40;
        Box optionFind = Box.createHorizontalBox();
        optionFind.add(Box.createHorizontalStrut(spacing));
        optionFind.add(indentOption);
        optionFind.add(Box.createHorizontalStrut(spacing));
        optionFind.add(Box.createHorizontalGlue());

        Box buttons = Box.createHorizontalBox();
        buttons.add(Box.createHorizontalStrut(spacing));
        buttons.add(okButton);
        buttons.add(Box.createHorizontalStrut(10));
        buttons.add(cancelButton);
        buttons.add(Box.createHorizontalStrut(spacing));
        buttons.add(Box.createHorizontalGlue());
        
        // Combine them in all
        Box all = Box.createVerticalBox();
        all.add(Box.createVerticalStrut(15));
        all.add(optionFind);
        all.add(Box.createVerticalStrut(15));
        all.add(buttons);
        all.add(Box.createVerticalStrut(15));

        // Add them to the dialog
        dial.getContentPane().add(all);
        dial.setResizable(false);
        //dial.setSize(new Dimension(397, 179));
        dial.pack();
        
    }

    public void show() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        dial.setLocation(screenSize.width / 2 - dial.getWidth() / 2,
                screenSize.height / 2 - dial.getHeight() / 2);

        // Show the dialog and bring it to the front
        dial.setVisible(true);
        dial.toFront();
    }

    public void hide() {
        dial.setVisible(false);
    }

    public void itemStateChanged(ItemEvent e) {
        // Decide which check box made it
        Object source = e.getItemSelectable();
        if (source == indentOption) {
            autoIndent = e.getStateChange() == ItemEvent.SELECTED;
        } 
    }

    public void actionPerformed(ActionEvent e) {

        if( e.getSource() == okButton) {
           localPref.setTrue(LocalPreferences.EDSTDINDENT, autoIndent);
           try {
            localPref.savePreferences();
           } catch(Exception ex) {
               
           }
           hide();
           dial.dispose();
        } else if(e.getSource() == cancelButton) {
           hide();
           dial.dispose();
        }

    }

}
