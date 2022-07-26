/**

 * SpectatorApp.java

 *

 * Description:		Spectator application (main)

 * @author			Tim "Pops" Roberts

 * @version			1.0

 */
package com.topcoder.client.spectatorApp;

import com.topcoder.netCommon.contestantMessages.request.KeepAliveRequest;
import java.awt.Frame;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.StringTokenizer;
import org.apache.log4j.Category;
import com.topcoder.client.spectatorApp.controller.GUIController;
import com.topcoder.client.spectatorApp.event.ConnectionAdapter;
import com.topcoder.client.spectatorApp.event.ConnectionEvent;
import com.topcoder.client.spectatorApp.event.LoginAdapter;
import com.topcoder.client.spectatorApp.event.LoginEvent;
import com.topcoder.client.spectatorApp.netClient.Client;
import com.topcoder.client.spectatorApp.netClient.DispatchThread;
import com.topcoder.client.spectatorApp.netClient.LoggingConnectionProcessor;
import com.topcoder.client.spectatorApp.netClient.LoggingEventProcessor;
import com.topcoder.client.spectatorApp.netClient.RequestThread;
import com.topcoder.client.spectatorApp.netClient.SpectatorEventProcessor;
import com.topcoder.client.spectatorApp.netClient.SpectatorServerSocket;
import com.topcoder.client.spectatorApp.scoreboard.model.ComponentContestManager;
import com.topcoder.client.spectatorApp.scoreboard.model.ContestManager;
import com.topcoder.client.spectatorApp.scoreboard.model.RoomManager;
import com.topcoder.client.spectatorApp.scoreboard.model.RoundManager;
import com.topcoder.client.spectatorApp.scoreboard.model.TeamManager;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contestantMessages.request.ExchangeKeyRequest;
import com.topcoder.netCommon.contestantMessages.request.RegisterRoomRequest;
import com.topcoder.netCommon.contestantMessages.request.RegisterWeakestLinkTeamRequest;
import com.topcoder.shared.netCommon.MessageEncryptionHandler;

public class SpectatorApp extends Thread {
	/** the userid */
	public String userid = null;

	/** the password */
	public String password = null;

	/** the remote IP address to connect to */
	public String remoteIP = null;

	/** the remote port number to connect to */
	public int remotePort = 5000;

	/** the length of time the "moving to" is shown */
	public int moveDelay = 3000;

	/** The room ID's to listen to */
	public int[] roomID;

	/** The team ID's to listen to */
	public int[] teamID;

	/** reference to the logging category */
	private static final Category cat = Category.getInstance(SpectatorApp.class.getName());

	/** reference to the client connection */
	private Client clientConnection = null;

	/** reference to the our announcer listener */
	private SpectatorServerSocket announcerListener;

	/** static reference to the SpectatorApp */
	private static SpectatorApp spectatorApp;

	/** reference to a spectator frame */
	private SpectatorAppFrame appFrame = null;

    private MessageEncryptionHandler encryptionHandler;

	/**
	 * Main method to start the spectator application
	 * 
	 * @param args
	 *           the arguments
	 */
	public static void main(String[] args) {
		// Verify the parameters
		if (args.length != 1) {
			cat.fatal("Wrong number of parameters specified. \n\n");
			printUsage();
			exitApplication(-1);
		}
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(args[0]));
		} catch (FileNotFoundException e) {
			cat.fatal("Filename '" + args[0] + "' does not exist. \n\n");
			printUsage();
			exitApplication(-1);
		} catch (IOException e) {
			cat.fatal("Error accessing '" + args[0] + "'", e);
			printUsage();
			exitApplication(-1);
		}
		// Create and run the spectator application
		try {
			spectatorApp = new SpectatorApp(properties);
		} catch (NumberFormatException e) {
			cat.fatal("One of the port numbers are not a numeric number. \n\n");
			printUsage();
			exitApplication(-1);
		} catch (Throwable t) {
			cat.fatal("Exception occurred", t);
			exitApplication(-1);
		}
	}

	/** Gets the userid */
	public String getuserid() {
		return userid;
	}

	/** Gets the password */
	public String getpassword() {
		return password;
	}

	/** Gets the remoteIP */
	public String getremoteIP() {
		return remoteIP;
	}

	/** Gets the remotePort */
	public int getremotePort() {
		return remotePort;
	}

	/** Gets the move delay */
	public int getMoveDelay() {
		return moveDelay;
	}

    public MessageEncryptionHandler getEncryptionHandler() {
        return encryptionHandler;
    }

	/** Gets the spectatorApp */
	public static final SpectatorApp getInstance() {
		return spectatorApp;
	}

	/**
	 * Prints the usage for the program
	 */
	public static void printUsage() {
		cat.error("Usage:");
		cat.error("   configFile - the name of the configuration file");
	}

	/**
	 * Exits the application
	 * 
	 * @param rc
	 *           the return code
	 */
	public static void exitApplication(int rc) {
		cat.info("Spectator Application exiting");
		GUIController.getInstance().dispose();
		System.exit(rc);
	}

	public Frame getFrame() {
		return this.appFrame;
	}

	/**
	 * Constructor for the spectator application
	 * 
	 * @param properties
	 *           the properties file for the application
	 */
	public SpectatorApp(Properties properties) throws NumberFormatException {
		// Save the parameters
		this.remoteIP = properties.getProperty("remoteIP");
		this.remotePort = Integer.parseInt(properties.getProperty("remotePort"));
		this.userid = properties.getProperty("userid");
		this.password = properties.getProperty("password");
		this.moveDelay = Integer.parseInt(properties.getProperty("moveDelay")) * 1000;
		int localPort = Integer.parseInt(properties.getProperty("localPort"));
		StringTokenizer str = new StringTokenizer(properties.getProperty("requestRooms"), " ,");
		this.roomID = new int[str.countTokens()];
		for (int x = 0; x < roomID.length; x++)
			roomID[x] = Integer.parseInt(str.nextToken());
		str = new StringTokenizer(properties.getProperty("requestTeams"), " ,");
		this.teamID = new int[str.countTokens()];
		for (int x = 0; x < teamID.length; x++)
			teamID[x] = Integer.parseInt(str.nextToken());
		cat.info("Spectator Application starting...");
		// Show the frame
		appFrame = new SpectatorAppFrame();
		// Startup the dispatch/request threads adding our processors
		DispatchThread.getInstance().addEventProcessor(new LoggingEventProcessor());
		DispatchThread.getInstance().addEventProcessor(SpectatorEventProcessor.getInstance());
		RequestThread.getInstance().addConnectionProcessor(new LoggingConnectionProcessor());
		// Add the handlers for the login/connection events
		SpectatorEventProcessor.getInstance().addLoginListener(new LoginHandler());
		SpectatorEventProcessor.getInstance().addConnectionListener(new ConnectionHandler());
		// Initialize the singletons
		HeartBeatTimer.getInstance();
		PhaseTracker.getInstance();
		ContestManager.getInstance();
		RoundManager.getInstance();
		RoomManager.getInstance();
		TeamManager.getInstance();
		ComponentContestManager.getInstance();
		
		// Start up the announcer listener
		try {
			announcerListener = new SpectatorServerSocket(localPort);
		} catch (java.io.IOException e) {
			cat.fatal("Starting the announcer listener", e);
			exitApplication(-1);
		}
		// Start a client connection
                if(!remoteIP.trim().equals("")) {
                    clientConnection = new Client(remoteIP, remotePort);
                } else {
                    cat.info("Not starting algorithm connector");
                }
		// Have the thread go to sleep (this is the only NON-daemon thread)
		try {
			start();
		} catch (Throwable t) {
			cat.error("Start", t);
		}
	}

	public void run() {
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (Throwable t) {
			}
		}
	}
        
        public class KeepAliveThread extends Thread {
            private boolean quit = false;
            public void quit() {
                quit = true;
            }
            
            public void run() {
                try {
                    while(!quit) {
                        RequestThread.getInstance().queueMessage(new KeepAliveRequest());
                        Thread.sleep(30000);
                    }
                } catch (Exception e) {
                    
                }
            }
        }
        
        private KeepAliveThread kat = null;

	/** Class that handles any connection events */
	public class ConnectionHandler extends ConnectionAdapter {
		/**
		 * Connection Made event
		 * 
		 * @param evt
		 *           the connection event that was made
		 */
		public void connectionMade(ConnectionEvent evt) {
			cat.info("Connection established to " + evt.getRemoteIP() + ":" + evt.getRemotePort());
			// Queue up our login message
            encryptionHandler = new MessageEncryptionHandler();
            RequestThread.getInstance().queueMessage(new ExchangeKeyRequest(encryptionHandler.generateRequestKey()));
		}

		/**
		 * Lost connection event
		 * 
		 * @param evt
		 *           the connection event that was lost
		 */
		public void connectionLost(ConnectionEvent evt) {
			cat.info("Connection was lost - try to re-establish...");
                        if(kat != null) {
                            kat.quit();
                            kat = null;
                        }
            encryptionHandler = null;
			// Close the client connection
			if (clientConnection != null)
				clientConnection.close();
			// Start a new client connection
			clientConnection = new Client(remoteIP, remotePort);
		}
	}

	/** Class that handles any login events */
	public class LoginHandler extends LoginAdapter {
		/**
		 * Login Successful event
		 * 
		 * @param evt
		 *           the login event
		 */
		public void loginSuccessful(LoginEvent evt) {
			cat.info("Login was successful");
			// Send the request rooms
			for (int x = 0; x < roomID.length; x++) {
				RequestThread.getInstance().queueMessage(new RegisterRoomRequest(roomID[x]));
			}
			// Send the request for teams
			for (int x = 0; x < teamID.length; x++) {
				RequestThread.getInstance().queueMessage(new RegisterWeakestLinkTeamRequest(teamID[x]));
			}
                        kat = new KeepAliveThread();
                        kat.start();
		}

		/**
		 * Login unsuccessful event. The spectator application will fail
		 * 
		 * @param evt
		 *           the login event
		 */
		public void loginFailure(LoginEvent evt) {
			cat.fatal("Login failed: " + evt.getErrorMsg());
			exitApplication(-1);
		}
	}
}
/* @(#)SpectatorApp.java */
