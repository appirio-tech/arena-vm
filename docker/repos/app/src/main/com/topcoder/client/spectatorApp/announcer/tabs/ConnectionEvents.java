package com.topcoder.client.spectatorApp.announcer.tabs;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import com.topcoder.client.spectatorApp.announcer.Announcer;
import com.topcoder.client.spectatorApp.announcer.AnnouncerConfig;
import com.topcoder.client.spectatorApp.announcer.events.ConnectionEvent;

/**
 * Panel that will show all the connectors and their status
 */
public class ConnectionEvents extends AnnouncerTab {
	
	/** The table model used for connections */
	private DefaultTableModel model = new NonEditableTableModel();
	
	/** The table of connections */
	private JTable table = new JTable(model);
	
	/** The connection thread */
	private ConnectionThread connectionThread=null;

	/** The connection status's */	
	private static final String NOTCONNECTED = "Not Connected";
	private static final String CONNECTING = "Connecting";
	private static final String CONNECTED = "Connected";
	private static final String ERROR = "Error - see console";
	
	/** Constructor */
	public ConnectionEvents() {
		// Set up the layout
		this.setLayout(new BorderLayout());
		
		// Setup table model
		model.addColumn("Connection");
		model.addColumn("Port");
		model.addColumn("Status");
		
		// SEtup the reconnect button
		JButton reconnect = new JButton("Reconnect");
		reconnect.addActionListener(new ReconnectHandler());
		
		// Configure the panel		
		add(new JScrollPane(table), BorderLayout.CENTER);
		add(reconnect, BorderLayout.SOUTH);
	}
		
	/** Reconfigure the events listed */
	public void reConfigure() {
		// Close any current threads
		try {
			Announcer.getInstance().getClient().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Stop the connection thread
		if(connectionThread!=null && connectionThread.isAlive()) connectionThread.stopRunning();
		
		// Clear the model
		while(model.getRowCount()>0) model.removeRow(0);
		
		// Get the connection events
		ConnectionEvent[] events = AnnouncerConfig.getInstance().getSpecAppAddresses();
		
		// Setup the model
		for(int x=0;x<events.length;x++) {
			model.addRow(new Object[] { events[x].getHostName(), new Integer(events[x].getPortNo()), NOTCONNECTED });
		}
		
		// Startup the connection thread
		connectionThread = new ConnectionThread(events);
		connectionThread.start();
	}
	
	/** App is exiting */
	public void exitApp() {
		if(connectionThread!=null && connectionThread.isAlive()) connectionThread.stopRunning();
	}
	
	/** Return the title for the tab */
	public String getTitle() {
		return "Connections";
	}

	/** Return the icon used for the tab */
	public Icon getIcon() {
		return null;
	}

	/** Return the tab tip */
	public String getTip() {
		return "Connections and Status";
	}
	
	/** The reconnect handler */
	class ReconnectHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			reConfigure();
		}
	}
	
	/** Non-editable table model */
	class NonEditableTableModel extends DefaultTableModel {
		public boolean isCellEditable(int r, int c) { return false; }
	}
	
	/** The thread used to connect */
	class ConnectionThread extends Thread {
		/** The connections */
		private ConnectionEvent[] events;
		
		/** The running status */
		private boolean stopRunning = false;
		
		/** Constructor */
		public ConnectionThread(ConnectionEvent[] events) {
			this.events = events;
		}
		
		/** Stop the thread */
		public void stopRunning() {
			stopRunning = true;
			this.interrupt();
		}
		
		/** The run */
		public void run() {
			
			// Loop through all the events
			//
			// NOTE: assumes the order of the events
			//       is the same as in the model!
			for(int x=0;x<events.length;x++) {
				// Check if we should stop
				if(stopRunning) return;
				
				// Update the status to connecting (must do on the swing thread)
				final int row = x;
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						table.setRowSelectionInterval(row, row); 
						model.setValueAt(CONNECTING, row, 2); 
					}
				});

				try {
					// Try to connect
					Announcer.getInstance().getClient().connect(events[x]);

					// Update the status
					SwingUtilities.invokeLater(new Runnable() {
						public void run() { 
							model.setValueAt(CONNECTED, row, 2); 
							table.getSelectionModel().clearSelection();
						}
					});
				} catch (UnknownHostException e) {
					// Update the status
					SwingUtilities.invokeLater(new Runnable() {
						public void run() { 
							model.setValueAt(ERROR, row, 2); 
							table.getSelectionModel().clearSelection();
						}
					});
					
					// Print stack trace
					e.printStackTrace();
				} catch (IOException e) {
					// Update the status
					SwingUtilities.invokeLater(new Runnable() {
						public void run() { 
							model.setValueAt(ERROR, row, 2); 
							table.getSelectionModel().clearSelection();
						}
					});
					
					// Print stack trace
					e.printStackTrace();
				}
			}
		}
	}
}
