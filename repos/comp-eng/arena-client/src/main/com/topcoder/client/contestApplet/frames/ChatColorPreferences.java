/**
 * ChatColorPreferences.java
 *
 * Description:		Table model for the editor of plugins
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.contestApplet.frames;

import javax.swing.*;
//import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
//import javax.swing.border.*;
import com.topcoder.client.contestApplet.common.Common;
//import com.topcoder.client.contestApplet.widgets.*;
import com.topcoder.client.contestApplet.panels.*;


public final class ChatColorPreferences { //extends JDialog implements ActionListener, WindowListener {

/*	JButton saveButton		= new JButton("Save");
	JButton closeButton		= new JButton("Close");
	ChatConfigurationPanel configPanel = new ChatConfigurationPanel(this);

	public ChatColorPreferences(JFrame parent) {
		super(parent, "Chat Color Preferences", true);

		Common.setLocationRelativeTo(parent, this);

		// Set the close operations
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(this);

		// Get the content pane and set attributes
		Container pane = getContentPane();
		pane.setBackground(Common.BG_COLOR);
		pane.setLayout(new BorderLayout());

		// Make the buttons the same size
		Dimension size = new Dimension(89,21);
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
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(10,0,10,10));
		buttonPanel.setBackground(Common.BG_COLOR);

		// Setup actionlisteners
		saveButton.addActionListener(this);
		closeButton.addActionListener(this);

		// Add to the panes
		pane.add(configPanel, BorderLayout.CENTER);
		pane.add(buttonPanel, BorderLayout.SOUTH);

		// Pack it
		this.pack();
	}

	public void actionPerformed(ActionEvent e) {

		// Get the source of the action
		Object source = e.getSource();

		if (source==saveButton) {
			configPanel.saveColors();
		} else if (source==closeButton) {
			windowClosing(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		}
	}

	public void windowClosing(WindowEvent e) {

		// Are saves pending?
		if (configPanel.isChangesPending()) {

			// Should we save?
			if (Common.confirm("Save Pending", "Changes are pending.  Do you want to save before closing?", this)) {
				// Try to save
				configPanel.saveColors();
			}
		}
		// Close the window
		dispose();
	}

	public void windowActivated(WindowEvent e) {}
	public void windowClosed(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}


    /*
	public static void main(String[] args) {
		//try {System.setErr(new java.io.PrintStream(new java.io.FileOutputStream("run.log")));} catch (java.io.IOException e) {};
		JFrame f = new JFrame("FF");
		ChatColorPreferences ff = new ChatColorPreferences(f);
		f.pack();
		f.show();
		ff.show();
	}*/

}


/* @(#)ChatColorPreferences.java */
