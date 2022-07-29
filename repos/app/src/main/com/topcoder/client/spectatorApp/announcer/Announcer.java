/**
 * Announcer.java Description: Announcer application (main)
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
package com.topcoder.client.spectatorApp.announcer;

import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.topcoder.client.spectatorApp.announcer.actions.ActionOpen;
import com.topcoder.client.spectatorApp.announcer.tabs.AnnouncerTabPane;
import com.topcoder.client.spectatorApp.announcer.tabs.ScheduleEvents;

public class Announcer extends JFrame {
	/** The singleton announcer app */
	private static Announcer announcer = null;

	/** The client connection */
	private Client client = new Client();

	/** The tab pane */
	private AnnouncerTabPane tabPane;
	
	/** last defined directory */
	private File lastDir=null;
	
	/** most recent file */
	private File lastFile=null;
	
	public File getLastFile() {
		return lastFile;
	}

	public void setLastFile(File lastFile) {
		this.lastFile = lastFile;
	}

	public ScheduleEvents getScheduledEvents(){
		return tabPane.getScheduleEventsTab();
	}
	
	/**
	 * Main method to start the spectator application
	 * 
	 * @param args
	 *           the arguments
	 */
	public static void main(String[] args) {
		// Create the announcer app
		Announcer announcer = Announcer.getInstance();
		if (args.length > 0) {
			// Load the config file specified
			try {
				File f=new File(args[0]);
				announcer.loadConfig(f);
				if(f==null)f=new File(args[0]);
				announcer.setLastFile(f.getAbsoluteFile());
				announcer.setLastDir(f.getAbsoluteFile().getParentFile());
			} catch (Throwable t) {
				t.printStackTrace();
				System.exit(-1);
			}
		} else {
			// No config file - do an action open event...
			new ActionOpen().actionPerformed(new ActionEvent(announcer, -1, "startitup!"));
		}
	}

	/** Constructs the frame */
	private Announcer() {
		super("Announce App v.2.1");
		// Create the tabbed pane
		tabPane = new AnnouncerTabPane();
		// Setup the look
		this.setJMenuBar(new AnnouncerMenu());
		this.getContentPane().add(tabPane);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowHandler());
		// Pack and show!
		this.pack();
		this.show();
	}

	/** Singleton access to the announcer app */
	public static synchronized Announcer getInstance() {
		if (announcer == null) announcer = new Announcer();
		return announcer;
	}

	/** Exit out of the application */
	public void exitApp() {
		tabPane.exitApp();
		this.dispose();
		System.exit(-1);
	}

	/** Returns the client in use */
	public Client getClient() {
		return client;
	}

	/** Load the configuration file */
	public void loadConfig(File configFile) throws Exception {
		// Reload config
		AnnouncerConfig.getInstance().loadFile(configFile);
		// Close the client
		if (client != null) client.close();
		// Notify the pane to reconfigure
		tabPane.setSelectedIndex(0);
		tabPane.reConfigure();
	}
	
	/** Save a configuration file */
	public void saveAsConfig(File configFile) throws Exception {
		AnnouncerConfig.getInstance().saveAsFile(configFile);
	}
	
	
	/** The window handler */
	class WindowHandler extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			exitApp();
		}
	}


	public File getLastDir() {
		return lastDir;
	}

	public void setLastDir(File lastDir) {
		this.lastDir = lastDir;
	}
}
