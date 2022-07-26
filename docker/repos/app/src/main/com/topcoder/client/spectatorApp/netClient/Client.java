/**
 * Client.java Description: Connection to the server from the client
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
package com.topcoder.client.spectatorApp.netClient;

import java.io.EOFException;
import java.io.IOException;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import org.apache.log4j.Logger;
import com.topcoder.client.spectatorApp.PhaseTracker;
import com.topcoder.client.spectatorApp.messages.ConnectionResponse;
import com.topcoder.client.spectatorApp.messages.RenderCommand;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contestantMessages.NetCommonSocketFactory;
import com.topcoder.netCommon.io.ClientSocket;
import com.topcoder.shared.netCommon.messages.MessagePacket;
import com.topcoder.shared.netCommon.messages.spectator.PhaseChange;

public class Client extends Thread implements ConnectionProcessor {
	/** reference to the logging category */
	private static final Logger cat = Logger.getLogger(Client.class.getName());

	/** The remote name connected to */
	private String remoteIP;

	/** The remote port connected to */
	private int remotePort;

	/** The socket factor to be used */
	/** The socket interface used by the socket factory */
	private ClientSocket clientSocket;

	/** Instance of the dispatch thread */
	private DispatchThread dispatchThread = DispatchThread.getInstance();

	/** Switched used to keep the socket running */
	private boolean keepRunning = true;

	/** Determines if we have read the first packet or not */
	private boolean firstPacket = true;

	/**
	 * Client Constructor
	 * 
	 * @param remoteIP
	 *           remoteIP to connect to
	 * @param remotePort
	 *           remotePort to connect to
	 */
	public Client(String remoteIP, int remotePort) {
		// Save the connection info
		this.remoteIP = remoteIP;
		this.remotePort = remotePort;
		// Make a deamon thread
		setDaemon(true);
		// Start the thread
		start();
	}

	/**
	 * Connect and process all objects (and eventually disconnect) to/from the
	 * server)
	 */
	public void run() {
		// Try to connect
		try {
			// Get the client socket
			Socket socket = new Socket(remoteIP, remotePort);
			clientSocket = NetCommonSocketFactory.newClientSocket(socket);
			// Add ourselves as a client connection
			RequestThread.getInstance().addConnectionProcessor(this);
			// Send a connection made message
			queueMadeConnection();
		} catch (Throwable t) {
			queueLostConnection(t);
			return;
		}
		// Loop around reading the next object
		while (keepRunning) {
			try {
				// Read an object from the client socket
				Object o = clientSocket.readObject();
				cat.info("(Received) " + o);
				if (!(o instanceof MessagePacket)) {
					cat.info("Object read is not of instance MessagePacket:");
					cat.info(o);
					continue;
				}
				MessagePacket messagePacket = (MessagePacket) o;
				// If this is the first packet - disable the painting of the window
				// (reason: we may have reconnected and we don't want to repaint
				// as we are processing all the events)
				if (firstPacket) dispatchThread.queueMessage(new RenderCommand(false));
				try {
					// Get the messages
					List messages = messagePacket.getMessages();
					// Loop through the arraylist, queueing up the responses
					for (int x = 0; x < messages.size(); x++) {
						// Get the message in the message packet
						Object message = messages.get(x);
						// SPECIAL - ignore end contests from the server (these will
						// be generated internally when the system tests
						// have been completed (see ScoreboardPointTracker)!
						// (Unless the prior phase was a voting phase...)
						// Update: we translate it to a SYSTEMTEST phase change to put
						// the spec app back into
						// system testing phase if it had been switch out (by the
						// announcer app)
						if (message instanceof PhaseChange 
						&& ((PhaseChange) message).getPhaseID() == ContestConstants.CONTEST_COMPLETE_PHASE 
						&& PhaseTracker.getInstance().getPhaseID() != ContestConstants.VOTING_PHASE
						&& PhaseTracker.getInstance().getPhaseID() != ContestConstants.TIE_BREAKING_VOTING_PHASE) {
							message = new PhaseChange(ContestConstants.SYSTEM_TESTING_PHASE, ((PhaseChange) message).getTimeAllocated());
						}
						dispatchThread.queueMessage(message);
					}
				} finally {
					// Reset painting if it's the first packet
					if (firstPacket) {
						dispatchThread.queueMessage(new RenderCommand(true));
						firstPacket = false;
					}
				}
				// } catch (ClassNotFoundException e) {
				// e.printStackTrace();
			} catch (ClassCastException e) {
				cat.warn("ReadObject", e);
			} catch (SocketException e) {
				queueLostConnection(e);
				break;
			} catch (StreamCorruptedException e) {
				cat.warn("ReadObject", e);
			} catch (OptionalDataException e) {
				cat.warn("ReadObject", e);
			} catch (EOFException e) {
				queueLostConnection(e);
				break;
			} catch (IOException e) {
				cat.warn("ReadObject", e);
			} catch (Throwable e) {
				cat.warn("ReadObject", e);
			}
		}
		// Removes ourselves as a client connection
		RequestThread.getInstance().removeConnectionProcessor(this);
		// Close
		try {
			clientSocket.close();
		} catch (Throwable t) {
			cat.warn("Closing Socket", t);
		}
	}

	/**
	 * Queue's a succesful connection message to the dispatch thread
	 */
	private final void queueMadeConnection() {
		dispatchThread.queueMessage(new ConnectionResponse(remoteIP, remotePort));
	}

	/**
	 * Queue's a lost connection message to the dispatch thread
	 * 
	 * @param t
	 *           the exception that caused the lost connection
	 */
	private final void queueLostConnection(Throwable t) {
		dispatchThread.queueMessage(new ConnectionResponse(remoteIP, remotePort, t.toString()));
	}

	/**
	 * Sends a message to the remote server
	 * 
	 * @param messagePacket
	 *           messagePacket to send
	 */
	public void sendMessage(MessagePacket messagePacket) {
		try {
			// MessagePacket messagePacket = new MessagePacket();
			// messagePacket.add(message);
			clientSocket.writeObject(messagePacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Closes the current connection
	 */
	public void close() {
		// Shutdown the loop and interrupt the thread
		keepRunning = false;
		this.interrupt();
	}
}
