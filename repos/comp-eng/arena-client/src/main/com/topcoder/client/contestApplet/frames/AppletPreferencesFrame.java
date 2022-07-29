/**
 * ChatColorPreferences.java
 *
 * Description:		Dialog enclosing panels for setting font/color prefs.
 * @author			Greg "AdamSelene" Eldridge
 * @version			0.1
 */

// This borrows/scavenges heavily from Pops's ChatColorPreferences dialog.

package com.topcoder.client.contestApplet.frames;

import com.topcoder.client.contestApplet.ContestApplet;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;

import com.topcoder.client.contestApplet.panels.TabbedAppletConfigurationPanel;
import com.topcoder.client.contestApplet.common.Common;

public final class AppletPreferencesFrame extends JDialog implements ActionListener, WindowListener {

    JButton saveButton = new JButton("Save");
    JButton closeButton = new JButton("Close");

    TabbedAppletConfigurationPanel configPanel;

    JFrame myParent;

    public ContestApplet getApplet() {
        return ((MainFrame)myParent).getContestApplet();
    }

    public AppletPreferencesFrame(JFrame parent) {
        super(parent, "Applet Display Preferences", true);
        myParent = parent; 			// for recentering
        Container pane;				// to store the content pane for the frame
        Dimension size;				// to size element

        Common.setLocationRelativeTo(parent, this);

        // Set the close operations
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(this);

        // Get the content pane and set attributes
        pane = getContentPane();
        pane.setBackground(Common.BG_COLOR);
        pane.setLayout(new BorderLayout());

        // Make the buttons the same size
        size = new Dimension(89, 21);
        saveButton.setMaximumSize(size);
        closeButton.setMaximumSize(size);

        // Create the button bar
        Box buttonPane = Box.createHorizontalBox();
        buttonPane.add(Box.createHorizontalGlue());
        buttonPane.add(saveButton);
        buttonPane.add(Box.createHorizontalStrut(2));
        buttonPane.add(closeButton);
        buttonPane.add(Box.createHorizontalStrut(5));
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.add(buttonPane, BorderLayout.EAST);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 10));
        buttonPanel.setBackground(Common.BG_COLOR);

        // Setup actionlisteners
        saveButton.addActionListener(this);
        closeButton.addActionListener(this);

        configPanel = new TabbedAppletConfigurationPanel(this);
        pane.add(configPanel, BorderLayout.CENTER);
        pane.add(buttonPanel, BorderLayout.SOUTH);
        
        this.pack();
    }

    public void actionPerformed(ActionEvent e) {

        // Get the source of the action
        Object source = e.getSource();
        if (source == saveButton) {
            configPanel.saveAll();
        } else if (source == closeButton) {
            windowClosing(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        }
    }

    public void windowClosing(WindowEvent e) {

        // Are saves pending?
        if (configPanel.isChangesPending()) {

            // Should we save?
            if (Common.confirm("Save Pending", "Changes are pending.  Do you want to save before closing?", this)) {
                configPanel.saveAll();                
            }
        }
        // Close the window
        dispose();
    }

    public void pack() {
        this.setSize(new Dimension(780,500));
//        super.pack();
        Common.setLocationRelativeTo(myParent, this);
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

    public static void main(String[] args) {

        JFrame fakeParent = new JFrame("Faker");
        AppletPreferencesFrame tester = new AppletPreferencesFrame(fakeParent);
        //f.pack();
        tester.show();
    }
}