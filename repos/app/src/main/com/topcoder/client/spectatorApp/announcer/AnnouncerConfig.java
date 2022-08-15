/*
 * Created on Sep 22, 2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.topcoder.client.spectatorApp.announcer;

import java.beans.ExceptionListener;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

import javax.swing.JOptionPane;

import com.topcoder.client.spectatorApp.announcer.events.AnnouncerEvent;
import com.topcoder.client.spectatorApp.announcer.events.ConnectionEvent;
import com.topcoder.client.spectatorApp.announcer.tabs.ScheduledEvent;

/**
 * @author Tim
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class AnnouncerConfig {

	/** Dummy contest id */
	private final int CONTESTID = 1;
	
	/** Dummy round id */
	private final int ROUNDID = 1;

	/** Singleton instance of the config file */
	private static AnnouncerConfig config = null;

	/** Array holding the events */		
	private ArrayList<AnnouncerEvent> events = new ArrayList<AnnouncerEvent>();
	
	/** Array holding the connections */
	private ArrayList<ConnectionEvent> connections = new ArrayList<ConnectionEvent>();
	
	/** Private constructor enforcing singleton pattern */
	private AnnouncerConfig() {
		
	}
	
	/** Gets the singleton configuration file */
	public static synchronized AnnouncerConfig getInstance() {
		if(config==null) config = new AnnouncerConfig();
		return config;
	}
	
	/** Reload the file */
	public void loadFile(File fileName) throws Exception {
		// Clear the events/connections
		events.clear();
		connections.clear();
		Announcer.getInstance().getScheduledEvents().reConfigureAndClear();
		
		// Define the decoder
		XMLDecoder d=null;
		
		try {
			// Create the decoder
			d = new XMLDecoder(new BufferedInputStream(new FileInputStream(fileName)));
			d.setExceptionListener(new ExceptionListener() {
				public void exceptionThrown(Exception e) {
					e.printStackTrace();
				}
			});
			
			// Loop until readObject throws an error.
			while(true) {
				// Try to read an object.  Will throw a ArrayIndexOutOfBounds
				// exception when done (wonder who made that intelligent decision)
				Object o = d.readObject();
				
				if(o==null) {
					JOptionPane.showMessageDialog(null, "Error reading an event (probably a class not found error) - see your console for messages", "Error reading the event", JOptionPane.ERROR_MESSAGE);
					continue;
				}
				
				// Is it an connection, or model, or event?
				if(o instanceof ConnectionEvent) {
					connections.add((ConnectionEvent)o);
				} else if(o instanceof ScheduledEvent){
					Announcer.getInstance().getScheduledEvents().scheduleEvent((ScheduledEvent)o);
				} else {
					// Validate the event first
					try {
						((AnnouncerEvent)o).validateEvent();
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, "Error validating the class: \r\n" + o.getClass().getName() + "\r\n\r\n" + e.toString(), "Error reading the event", JOptionPane.ERROR_MESSAGE);
					}
					events.add((AnnouncerEvent)o);
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			// All done reading
		} finally {
			// Close it
			if(d!=null) d.close();
		}
	}
	
	public void saveAsFile(File f){
		if(f==null)return;
		if(!f.canWrite())
			JOptionPane.showMessageDialog(null, "Cannot write to file:" + f.getName(), "Error writing to file", JOptionPane.ERROR_MESSAGE);
		
		// Define the encoder
		XMLEncoder xmlE=null;
		
		try {
			// Create the decoder
			xmlE = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(f)));
			xmlE.setExceptionListener(new ExceptionListener() {
				public void exceptionThrown(Exception e) {
					e.printStackTrace();
				}
			});
			
			//first write the connections
			for(ConnectionEvent c : connections)xmlE.writeObject(c);
			
			//next write the actions
			for(AnnouncerEvent a: events)xmlE.writeObject(a);
			
			//finally write the schedule
			for(Object action : Announcer.getInstance().getScheduledEvents().model.getDataVector()){
				Vector me=(Vector)action;
				xmlE.writeObject(new ScheduledEvent((AnnouncerEvent)me.get(0),(Integer)me.get(1)));
			}
			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error when writing to file:" + e.toString(), "Error writing to file", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		} finally {
			// Close it
			if(xmlE!=null) xmlE.close();
		}	
	}

	/** Get all the spectator addresses to connect to */
	public ConnectionEvent[] getSpecAppAddresses() {
		// Return the list
		return (ConnectionEvent[])connections.toArray(new ConnectionEvent[0]);
		
	}
	
	/** Return the events that are defined */
	public AnnouncerEvent[] getEvents() {
		return (AnnouncerEvent[])events.toArray(new AnnouncerEvent[0]);
	}

}
