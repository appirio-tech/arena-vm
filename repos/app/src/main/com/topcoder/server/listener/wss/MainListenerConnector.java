/*
 * Copyright (C) 2014 - 2015 TopCoder Inc., All Rights Reserved.
 */

package com.topcoder.server.listener.wss;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.security.Key;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;

import com.corundumstudio.socketio.SocketIOClient;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contestantMessages.response.BaseResponse;
import com.topcoder.netCommon.contestantMessages.response.ForcedLogoutResponse;
import com.topcoder.netCommon.contestantMessages.response.OpenComponentResponse;
import com.topcoder.netCommon.io.ClientSocket;
import com.topcoder.server.AdminListener.response.ChangeRoundResponse;
import com.topcoder.server.AdminListener.response.CommandResponse;
import com.topcoder.server.AdminListener.response.RoundAccessResponse;
import com.topcoder.server.listener.WSSCommonCSHandler;
import com.topcoder.server.listener.socket.messages.SocketMessage;
import com.topcoder.server.security.PrivateKeyObtainer;
import com.topcoder.server.services.CoreServices;
import com.topcoder.shared.netCommon.CSHandler;

/**
 * The connector to the main listener.
 *
 * <p>
 * Version 1.1 (Module Assembly - TCC Web Socket - Coder Profile and Active Users) changes:
 * <ol>
 *  <li>Updated {@link ConnectionManager.SocketReadThread.processSingleMessage} method to support
 *      Active Users Request and Coder Info Request.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.2 (TCC Web Socket Refactoring):
 * <ol>
 *  <li>Refactored {@link ConnectionManager.SocketReadThread.processSingleMessage(Object, UUID)} method
     to pass most responses directly, using class simple name as socket command.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.3 (LoginResponse sendEvent problem fix and upgrade netty-socketio library):
 * <ol>
 *  <li>Updated SocketReadThread class to directly write the response. It supports binary data now.</li>
 *  <li>Fix an issue of disconnection which caused by KeepAliveResponse extends UnsynchronizedResponse.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.4 (Problem to Restart Web Socket Listener v1.0):
 * <ol>
 *      <li>Add {@link #MS_BETWEEN_TRIES} field.</li>
 *      <li>Add {@link #closeClientSocket()} method.</li>
 *      <li>Add {@link #reConnectMainListener()} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.5 (Module Assembly - Web Socket Listener - Track User Actions From Web Arena):
 * <ol>
 *     <li>Updated {@link #write(java.util.UUID, Object)} method to record user action.</li>
 *     <li>Updated {@link ConnectionManager.SocketReadThread#processSingleMessage(Object, UUID)} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.6 (Web Socket Listener - Backend Logic Update To Generate and Return the image):
 * <ol>
 *     <li>Updated {@link ConnectionManager.SocketReadThread#processSingleMessage(Object, UUID)} method to add special
 *     handling for OpenComponentResponse.</li>
 *     <li>Removed unneeded @Override annotations (methods that implements an interface don't need this annotation)</li>
 * </ol>
 * </p>
 * @author gondzo, freegod, gevak, flytoj2ee, ahmed.seddiq, TCSASSEMBLER
 * @version 1.6
 * @since 1.0
 */
public class MainListenerConnector {
	private static final Logger logger = Logger.getLogger(MainListenerConnector.class);
	
    /**
     * The IP address of the main listener.
     */
    private String ip;

    /**
     * The port to connect to.
     */
    private int port;

    /**
     * The socket to send and receive messages.
     */
    private Socket socket;

    /**
     * The WebSocketServer instance.
     */
    private WebSocketServer server;

    /**
     * The queue of the messages to send.
     */
    private final Queue<SocketMessage> queue = new ConcurrentLinkedQueue<SocketMessage>();

    /**
     * The reconnect try interval time (ms).
     */
    private static final int MS_BETWEEN_TRIES = 5000;

    /**
     * The client field in user_action_audit.
     *
     * @since 1.5
     */
    private static final String CLIENT_TYPE = "web arena";

    /**
     * Creates a new instance of this class.
     *
     * @param ip    the IP address of the main listener
     * @param port    the port number of the main listener
     * @param server    the WebSocketServer instance
     */
    public MainListenerConnector(String ip, int port, WebSocketServer server) {
        this.ip = ip;
        this.port = port;
        this.server = server;
    }

    /**
     * Public api to send a message to main listener.
     *
     * @param connectionID    the id of the client connection
     * @param message    the message to send
     */
    public void write(UUID connectionID, Object message) {
        queue.add(new SocketMessage(connectionID, message));
        if (server.getSessionToUserHandleMap().containsKey(connectionID)) {
            if (server.getRecordedActions().contains(message.getClass().getSimpleName())) {
                CoreServices.recordUserAction(server.getSessionToUserHandleMap().get(connectionID),
                        message, CLIENT_TYPE);
            }
        }
    }

    /**
     * Public api to start the connection to main listener.
     *
     * @return whether the server is connected or not
     */
    public boolean start() {
        try {
            socket = new Socket(ip, port);
            ConnectionManager cm = new ConnectionManager(socket, MainListenerConnector.this);
            new Thread(cm).start();

        } catch (Exception ex) {
            logger.info("Could not connect on port:" + port);
            return false;
        }

        return true;
    }

    /**
     * Close the client socket.
     */
    public void closeClientSocket() {
        try {
            if(socket != null && !socket.isClosed()) {
                logger.info("close the client socket that is connected to arena main");
                socket.close();
            }
        } catch (IOException e) {
            logger.error("error occur when try to close client socket: " + e.getMessage(), e);
        }
    }

    /**
     * Reconnect to main listener
     */
    public void reConnectMainListener() {
        closeClientSocket();
        while(!start()) {
            logger.info("try to reconnect every " + MS_BETWEEN_TRIES);
            try {
                Thread.sleep(MS_BETWEEN_TRIES);
            } catch (InterruptedException e) {
                logger.error("error occur when try to reconnect: " + e.getMessage(), e);
            }
        }
        logger.info("successfully connect to " + ip + " with port: " + port);
    }
    /**
     * Getter of the web socket server instance.
     *
     * @return WebSocketServer instance
     */
    public WebSocketServer getServer() {
        return server;
    }

    /**
     * Private class to manage a single server connection
     *
     * <p>
     * Version 1.1 (Module Assembly - TCC Web Socket - Coder Profile and Active Users) changes:
     * <ol>
     *  <li>Updated {@link SocketReadThread.processSingleMessage} method to support
     *      Active Users Request and Coder Info Request.</li>
     * </ol>
     * </p>
     *
     * <p>
     * Changes in version 1.2 (Module Assembly - TCC Web Socket - Get Registered
     * Rounds and Round Problems):
     * <ol>
     *      <li>Class is adapted to send and receive CustomSerializable objects.</li>
     *      <li>Remove {@link #socket} field of Socket type.</li>
     *      <li>Remove {@link #socketOut} field of ObjectOutputStream type.</li>
     *      <li>Remove {@link #socketIn} field of ObjectInputStream type.</li>
     *      <li>Add {@link #csClient} field to handle reading/writing CustomSerializable objects.</li>
     * </ol>
     * </p>
     *
     * @author gondzo, freegod, dexy
     * @version 1.2
     */
    private class ConnectionManager implements Runnable {
        /** The main listener connector. */
        private MainListenerConnector connector;

        /**
         * The client socket object used to send and receive
         * CustomSerializable objects.
         *
         * @since 1.1
         */
        private ClientSocket csClient;

        /**
         * The message reading thread.
         */
        private Thread readThread;

        /**
         * The message writing thread.
         */
        private Thread writeThread;

        /**
         * Creates a new instance of this class.
         *
         * @param socket    the socket to send messages
         * @param cn    main listener connector
         */
        public ConnectionManager(Socket socket, MainListenerConnector cn) {
            Key encryptKey = PrivateKeyObtainer.obtainPrivateKey();
            CSHandler csHandler = new WSSCommonCSHandler(encryptKey);
            try {
                csClient = new ClientSocket(socket, csHandler);
            } catch (IOException e) {
                logger.info("Couldn't get I/O for the connection:" + e.getMessage());
            }
            connector = cn;
        }

        /**
         * Starts the thread processing.
         *
         */
        public void run() {
            readThread = new Thread(new SocketReadThread(connector));
            writeThread = new Thread(new SocketWriteThread(connector));
            readThread.start();
            writeThread.start();
        }


        /**
         * Private class to read incoming messages.
         *
         * <p>
         * Version 1.1 (Module Assembly - TCC Web Socket - Coder Profile and Active Users) changes:
         * <ol>
         *  <li>Updated {@link #processSingleMessage} method to support
         *      Active Users Request and Coder Info Request.</li>
         * </ol>
         * </p>
         *
         * <p>
         * Changes in version 1.2 (Module Assembly - TCC Web Socket -
         * Get Registered Rounds and Round Problems):
         * <ol>
         * <li>Remove {@link #SocketReadThread(ObjectInputStream, MainListenerConnector)} constructor.
         * <li>Add {@link #SocketReadThread(MainListenerConnector)} constructor.
         * <li>Update {@link #run} to use client to read/write CustomSerializable objects.</li>
         * <li>Update {@link #processSingleMessage(Object, UUID)} method to handle
         * CreateRoundListResponse, CreateProblemsResponse.</li>
         * </ol>
         * </p>
         *
         * <p>
         * Changes in version 1.3 (TCC Web Socket Refactoring):
         * <ol>
         *  <li>Refactored {@link #processSingleMessage(Object, UUID)} method
         *   to pass most responses directly, using class simple name as socket command.</li>
         * </ol>
         * </p>
         *
         * <p>
         * Changes in version 1.4 (LoginResponse sendEvent problem fix and upgrade netty-socketio library):
         * <ol>
         *  <li>Updated the code to directly write the response. It supports binary data now.</li>
         *  <li>Fix an issue of disconnection which caused by KeepAliveResponse extends UnsynchronizedResponse.</li>
         * </ol>
         * </p>
         *
         * <p>
         * Changes in version 1.5 (Problem to Restart Web Socket Listener v1.0):
         * <ol>
         *      <li>Update {@link #run()} method to reconnect main listener.</li>
         * </ol>
         * </p>
         * <p>
         * Changes in version 1.6 (Module Assembly - Web Socket Listener -
         * Porting Round Load Related Events):
         * <ol>
         *      <li>Update {@link #processSingleMessage(Object, UUID)} to accept load round related responses</li>
         * </ol>
         * </p>
         *
         * @author gondzo, freegod, dexy, gevak, flytoj2ee, ananthhh
         * @version 1.6
         */
        private class SocketReadThread implements Runnable {
            /**
             * The main listener connector.
             */
            private MainListenerConnector connector;

            /**
             * Creates a new instance of this class.
             *
             * @param cn            main listener connector
             */
            public SocketReadThread(MainListenerConnector cn) {
                connector = cn;
            }

            /**
             * Starts the thread processing.
             */
            public void run() {
                while (true) {
                    Object o;
                    try {
                        logger.info("reading object from reader");
                        o = csClient.readObject();

                        if (o != null) {
                            processMessage(o);
                        }
                    } catch (SocketTimeoutException e) {
                    	logger.info("Reading timeout... trying again...");
                        continue;
                    } catch (SocketException e) {
                    	if (logger.isInfoEnabled()) {
                    		logger.info("Lost connection: " + e.getMessage());
                    	}
                        connector.reConnectMainListener();
                        break;
                    } catch (StreamCorruptedException e) {
                        logger.warn("Stream corruption error: "+ e.getMessage(), e);
                        continue;
                    } catch (EOFException e) {
                        logger.info("Lost connection: " + e.getMessage());
                        connector.reConnectMainListener();
                        break;
                    } catch (IOException e) {
                        if (e.getMessage() != null
                                && (e.getMessage().toLowerCase().indexOf("stream closed") > -1
                                        || e.getMessage().toLowerCase().indexOf("stream is closed") > -1
                                        || e.getMessage().toLowerCase().indexOf("premature eof") > -1)) {
                            logger.info("Lost connection: " + e.getMessage());
                            connector.reConnectMainListener();
                            break;
                        }
                    } catch (Exception e) {
                        logger.warn("Error processing received message: " + e.getMessage(), e);
                        continue;
                    }
                }
            }

            /**
             * Private method to process incoming message.
             *
             * @param o the incoming message
             */
            @SuppressWarnings("rawtypes")
			private void processMessage(Object o) {
            	if (logger.isInfoEnabled()) {
            		logger.info("Received message: " + o.toString());
            	}
                if (!(o instanceof SocketMessage)) {
                    logger.warn("message is not a SocketMessage instance: " + o.toString());
                    return;
                }
                Object responseMsg = ((SocketMessage) o).getMessage();
                UUID uuid = ((SocketMessage) o).getUuid();
                if (responseMsg instanceof List) {
                    List messages = (List) responseMsg;
                    for (int i = 0; i < messages.size(); i++) {
                        processSingleMessage(messages.get(i), uuid);
                    }

                } else {
                    processSingleMessage(responseMsg, uuid);
                }
            }

            /**
             * Private method to process single incoming message.
             *
             * Changes in version 1.4:
             *  - Updated the code to directly write the response. It supports binary data now.
             *
             * @param responseMsg the incoming message
             * @param uuid the session ID
             */
            private void processSingleMessage(Object responseMsg, UUID uuid) {
                SocketIOClient client = connector.getServer().getSessionToConnectionMap().get(uuid);

                // Checks if the client is valid
                // this check is moved here @since 1.6 so as to be validated before any access to the client
                if (client == null) {
                    logger.info("Unknown webSocket connection uuid: " + uuid + ".");
                    return;
                }

                if(responseMsg instanceof RoundAccessResponse ||
                        responseMsg instanceof ChangeRoundResponse ||
                        responseMsg instanceof CommandResponse) {
                    logger.info("Handling " + responseMsg.getClass().getCanonicalName());
                    client.sendEvent(responseMsg.getClass().getSimpleName(), responseMsg);
                    logger.info(responseMsg.getClass().getCanonicalName() + " processed successfully.");
                    return;
                }
                if (!(responseMsg instanceof BaseResponse)) {
                    logger.info("Message is not a BaseResponse instance: " + responseMsg.toString());
                    return;
                }

                BaseResponse response = (BaseResponse) responseMsg;
                logger.info("Handling " + response.getClass().getCanonicalName() + ".");

                // Special handling for OpenComponentResponse. It will overwrite the response to
                // replace the source code with a JPG image encoded in a data URI format.
                // @since 1.6
                if(responseMsg instanceof OpenComponentResponse) {
                    OpenComponentResponse comp = (OpenComponentResponse) responseMsg;
                    if(comp.getEditable() == ContestConstants.VIEW_SOURCE) {
                        // It is a challenge request
                        String challenger = connector.getServer().getSessionToUserHandleMap().get(uuid);
                        // Don't return image if viewing own code
                        if(!challenger.equals(comp.getWriterHandle())) {
                            String codeImage = CoreServices.getCodeImage(comp.getWriterHandle(),
                                    comp.getRoomID(), comp.getComponentID(), comp.getCode(), comp.getLanguageID());
                            // create a new response with empty code and with the codeImage set.
                            if (codeImage != null && codeImage.trim().length() > 0) {
                                response = new OpenComponentResponse(comp.getWriterHandle(), comp.getComponentID(), "",
                                        codeImage, comp.getEditable(), comp.getRoomType(), comp.getRoomID(),
                                        comp.getLanguageID());
                            }
                        }
                    }
                }


                // Handle disconnection.
                if (response instanceof ForcedLogoutResponse) {
                    connector.getServer().getSessionToConnectionMap().remove(uuid);
                    connector.getServer().getSessionToUserHandleMap().remove(uuid);
                }
                // Pass responses directly.
                client.sendEvent(response.getClass().getSimpleName(), response);

                logger.info(response.getClass().getCanonicalName() + " processed successfully.");
            }
        }

        /**
         * Private class to manage writing messages.
         *
         * <p>
         * Changes in version 1.1 (Module Assembly - TCC Web Socket - Get Registered Rounds and Round Problems):
         * <ol>
         *      <li>Update {@link #run()} method to use client to read/write CustomSerializable objects.</li>
         *      <li>Remove {@link #SocketWriteThread(ObjectOutputStream, MainListenerConnector)} constructor.</li>
         *      <li>Add {@link #SocketWriteThread(MainListenerConnector)} constructor.</li>
         * </ol>
         * </p>
         *
         * @author gondzo, dexy
         * @version 1.1
         */
        private class SocketWriteThread implements Runnable {
            /**
             * The main listener connector.
             */
            private MainListenerConnector connector;

            /**
             * Creates a new instance of this class.
             *
             * @param cn
             *            main listener connector
             */
            public SocketWriteThread(MainListenerConnector cn) {

                connector = cn;
            }

            /**
             * Starts the thread processing.
             */
            public void run() {
                while (true) {
                    try {
                        SocketMessage message = null;
                        message = connector.queue.poll();
                        if (message != null) {
                            logger.info("sending message: " + message.toString());
                            csClient.writeObject(message);
                        } else {
                            Thread.sleep(10);
                        }
                    } catch (Exception e) {
                        logger.info("Error processing single message: " + e.getMessage());
                        return;
                    }
                }

            }

        }

    }
}
