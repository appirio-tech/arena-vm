package com.topcoder.client.spectatorApp.announcer.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import com.topcoder.client.spectatorApp.announcer.Announcer;

/**
 * Action that will exit the application
 */
public class ActionExit extends AbstractAction implements Action {

	public ActionExit() {
		super("Exit");
	}
	
	/** Exit the application */
	public void actionPerformed(ActionEvent arg0) {
		Announcer.getInstance().exitApp();
	}

}
