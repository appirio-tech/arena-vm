package com.topcoder.client.spectatorApp.announcer.properties;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTextField;

/**
 * This helper class will create a Browse button that is linked
 * to a JTextField
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
public class BrowseButton extends JButton implements ActionListener {
	
	/** The Linked JTextField */
	private JTextField linkedField;
	
	/**
	 * Constructor of the BrowseButton
	 * 
	 * @param linkedField field to place contents in
	 */
	public BrowseButton(JTextField linkedField) {
		super("Browse");
		this.linkedField = linkedField;
		this.addActionListener(this);
	}
	
	/**
	 * The action listener for the browse buttongs
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		JFileChooser fileChooser = new JFileChooser(linkedField.getText());
		
		int returnVal = fileChooser.showOpenDialog(null);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			linkedField.setText(fileChooser.getSelectedFile().getAbsolutePath());
		}
	}
}
