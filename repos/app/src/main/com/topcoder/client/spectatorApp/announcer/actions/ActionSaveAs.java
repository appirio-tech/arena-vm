package com.topcoder.client.spectatorApp.announcer.actions;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import com.topcoder.client.spectatorApp.announcer.Announcer;


/** The open action */
public class ActionSaveAs extends AbstractAction implements Action {
	
	/** The last file saved */
	private static File lastFile = null;
	
	public ActionSaveAs() {
		super("Save as...");
	}
	
	public void actionPerformed(ActionEvent e) {
		// Create a chooser
		JFileChooser fileChooser = new JFileChooser();
		//FileDialog fd = new FileDialog(Announcer.getInstance(),"Please specify the Announcer Configuration file to use:");
		
		// Setup the chooser
		fileChooser.setDialogTitle("Save Announcer configuration file as...");
		fileChooser.addChoosableFileFilter(new XMLFileFilter());
		if(Announcer.getInstance().getLastFile()!=null){ 
			fileChooser.setCurrentDirectory(Announcer.getInstance().getLastFile().getAbsoluteFile().getParentFile());
			fileChooser.setSelectedFile(Announcer.getInstance().getLastFile().getAbsoluteFile());
		}
		
		// Show the open dialog
		int rc = fileChooser.showSaveDialog(null);
		//fd.setMode(FileDialog.LOAD);
		
		if(rc==JFileChooser.APPROVE_OPTION) {
			try {
				Announcer.getInstance().saveAsConfig(fileChooser.getSelectedFile());
				Announcer.getInstance().setLastFile(fileChooser.getSelectedFile());
			} catch (Exception f) {
				JOptionPane.showMessageDialog(null, f.toString(), "Error saving the configuration file", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	/** The XML file filter */
	class XMLFileFilter extends FileFilter {
		public boolean accept(File file) {
			return file.isDirectory() || file.getName().toUpperCase().endsWith(".XML");
		}

		public String getDescription() {
			return "(*.xml) XML Configuration File";
		}
		
	}
}
