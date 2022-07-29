package com.topcoder.client.spectatorApp.announcer.tabs;

import java.awt.Dimension;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * The announcer pane to use
 */
public class AnnouncerTabPane extends JTabbedPane implements ChangeListener {

	/** The tabs */
	private AnnouncerTab[] tabs = new AnnouncerTab[] { new ConnectionEvents(), new SendEvents(), new ScheduleEvents() };

	/** The schedule events tab position */
	private final static int SCHEDULEEVENTSTAB = 2;
		
	/** The prior selection */
	private int priorIdx = 0;

	public AnnouncerTabPane() {
		// Super event
		super(JTabbedPane.TOP);
		
		// Add each tab
		for(int x=0;x<tabs.length;x++) {
			this.addTab(tabs[x].getTitle(), tabs[x].getIcon(), tabs[x], tabs[x].getTip());
			tabs[x].setParentTabPane(this);
		}
		
		// Slap on our listener
		this.addChangeListener(this);
		
		this.setPreferredSize(new Dimension(800,600));
	}
	
	/** Returns the schedule events tab */
	public ScheduleEvents getScheduleEventsTab() {
		return (ScheduleEvents)tabs[SCHEDULEEVENTSTAB];
	}
	
	/** State change listener */
	public void stateChanged(ChangeEvent e) {
		// Let them know they are being unselected
		tabs[priorIdx].tabUnSelected();
		
		// Get the new selection
		priorIdx = this.getSelectedIndex();
		
		// Let it know it's getting selected
		tabs[priorIdx].tabSelected();
	}

	/** Notify all the tabs to reconfigure */
	public void reConfigure() {
		for(int x=0;x<tabs.length;x++) tabs[x].reConfigure();
	}


	/** Notify all the tabs that the app is exiting */
	public void exitApp() {
		for(int x=0;x<tabs.length;x++) tabs[x].exitApp();
	}
}
