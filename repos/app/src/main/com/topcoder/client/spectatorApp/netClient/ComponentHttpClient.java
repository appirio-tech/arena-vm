package com.topcoder.client.spectatorApp.netClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import org.apache.log4j.Logger;
import com.topcoder.client.spectatorApp.messages.RenderCommand;
import com.topcoder.shared.netCommon.messages.Message;
import com.topcoder.shared.netCommon.messages.MessagePacket;
import com.topcoder.shared.netCommon.messages.MessageUtil;

public class ComponentHttpClient extends Thread implements ConnectionProcessor {
	/** reference to the logging category */
	private static final Logger cat = Logger.getLogger(ComponentHttpClient.class.getName());

	/** The url that we will be using*/
	private final String url;

	/** The polling time for the url */
	private final long pollTime;

	/** Instance of the dispatch thread */
	private final DispatchThread dispatchThread = DispatchThread.getInstance();

	/** Switched used to keep the socket running */
	private boolean keepRunning = true;

	/** Determines if we have read the first packet or not */
	private boolean firstPacket = true;

	private Queue<MessagePacket> msgToSend = new LinkedList<MessagePacket>();
	
	private final Message defaultMsg; 
	/**
	 * Constructs the http client to poll the url every x times
	 * 
	 * @param url the url to poll
	 * @param pollTime the time (in milliseconds) to poll
	 */
	public ComponentHttpClient(String url, long pollTime, Message defaultMsg) {
		// Save the connection info
		this.url = url;
		this.pollTime = pollTime;
		this.defaultMsg = defaultMsg;
		
		// Make a deamon thread
		setDaemon(true);
	}

	/**
	 * Connect and process all objects (and eventually disconnect) to/from the
	 * server)
	 */
	public void run() {
		// Add ourselves as a connection processor? 
		RequestThread.getInstance().addConnectionProcessor(this);
		
		// Loop around reading the next object
		while (keepRunning) {
			try {
				synchronized(this) {
					wait(pollTime);
				}
				
				final MessagePacket mp;
				synchronized(msgToSend) {
					mp = msgToSend.poll();
				}
				
				final Message msg;
				if (mp == null || mp.getMessages().size() == 0) {
					msg = defaultMsg;
				} else {
					msg = (Message) mp.getMessages().get(0);
				}
				
				final String fullUrl = url + MessageUtil.encodeQueryStringMessage(msg);
				final URL url = new URL(fullUrl);
				
				if (cat.isInfoEnabled()) {
					cat.info("Sending to " + fullUrl + ":" + msg);
				}
				
				final URLConnection httpConn = url.openConnection();
//				httpConn.setRequestMethod("GET");
				httpConn.setDoInput(true);
				httpConn.setDoOutput(false);
				httpConn.setReadTimeout(Integer.getInteger("com.topcoder.client.spectatorApp.netClient.ComponentHttpClient", 1000));
				httpConn.connect();
				
				final BufferedReader br = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
				final	StringBuffer buf = new StringBuffer(4096);
				while(true) {
					String ln = br.readLine();
					if (ln == null) break;
					buf.append(ln);
					buf.append("\n");
				}
				
				final MessagePacket messagePacket = MessageUtil.decodeXMLMessagePacket(buf.toString());
				
				// If this is the first packet - disable the painting of the window
				// (reason: we may have reconnected and we don't want to repaint
				// as we are processing all the events)
				if (firstPacket) dispatchThread.queueMessage(new RenderCommand(false));
				try {
					// Get the messages
					final List messages = messagePacket.getMessages();
					
					// Loop through the arraylist, queueing up the responses
					for (int x = 0; x < messages.size(); x++) {
						
						// Get the message in the message packet
						Object message = messages.get(x);
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
				cat.warn("ReadObject", e);
				break;
			} catch (StreamCorruptedException e) {
				cat.warn("ReadObject", e);
			} catch (OptionalDataException e) {
				cat.warn("ReadObject", e);
			} catch (IOException e) {
				cat.warn("ReadObject", e);
			} catch (Throwable e) {
				cat.warn("ReadObject", e);
			}
		}
		// Removes ourselves as a client connection
		RequestThread.getInstance().removeConnectionProcessor(this);
	}

	public void sendMessage(Message msg) {
		sendMessage(new MessagePacket(msg));
	}
	
	public void sendMessage(MessagePacket messagePacket) {
		if (messagePacket.getMessages().size() != 1) {
			throw new UnsupportedOperationException("Message packet can only contain a single message");
		}
		synchronized(msgToSend) {
			msgToSend.add(messagePacket);
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
