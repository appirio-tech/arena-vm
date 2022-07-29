/*
 * Created on Sep 22, 2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.topcoder.client.spectatorApp.announcer.tabs;

import javax.swing.Icon;
import javax.swing.JPanel;

/**
 * @author Tim
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public abstract class AnnouncerTab extends JPanel {

	/** The parent tab pane */
	private AnnouncerTabPane parentPane;
	
	/** Empty selection method */
	public void tabSelected() {}
	
	/** Empty unselection method */
	public void tabUnSelected() {}
	
	/** Sets the parent tab pane */
	public void setParentTabPane(AnnouncerTabPane parentPane) {
		this.parentPane = parentPane;
	}
	
	/** Returns the parent tab pane */
	public AnnouncerTabPane getParentTabPane() {
		return parentPane;
	}
	
	/** Notification to reconfigure */
	public void reConfigure() {
	}
	
	/** Notification of exit */
	public void exitApp() {
		
	}
	
	public abstract String getTitle();
	public abstract Icon getIcon();
	public abstract String getTip();
}
