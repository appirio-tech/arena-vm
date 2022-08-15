package com.topcoder.client.spectatorApp.announcer;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

import com.topcoder.client.spectatorApp.announcer.actions.ActionExit;
import com.topcoder.client.spectatorApp.announcer.actions.ActionOpen;
import com.topcoder.client.spectatorApp.announcer.actions.ActionSaveAs;

/**
 * The menu bar for the application...
 */
public class AnnouncerMenu extends JMenuBar {
	/** Constructor */
	public AnnouncerMenu() {
		// Add the file menu
		add(new FileMenu());
	}
	
	/** The file menu */
	class FileMenu extends JMenu {
		public FileMenu() {
			super("File");
		
			// Add the menu items
			add(new ActionOpen());
			add(new ActionSaveAs());
			addSeparator();
			add(new ActionExit());
		}
	}
}
