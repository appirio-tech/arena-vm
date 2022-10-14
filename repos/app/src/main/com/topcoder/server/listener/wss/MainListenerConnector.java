/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
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

import com.corundumstudio.socketio.SocketIOClient;
import com.topcoder.netCommon.contestantMessages.request.*;
import com.topcoder.netCommon.contestantMessages.response.BaseResponse;
import com.topcoder.netCommon.contestantMessages.response.ForcedLogoutResponse;
import com.topcoder.netCommon.contestantMessages.response.KeepAliveResponse;
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
 * @author gondzo, freegod, gevak, flytoj2ee
 * @version 1.5
 * @since 1.0
 */
public class MainListenerConnector {
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

            WebSocketServerHelper.info("successfully connect to " + ip + " with port: " + port);
        } catch (Exception ex) {
            WebSocketServerHelper.info("Could not connect on port:" + port);
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
                WebSocketServerHelper.info("close the client socket that is connected to arena main");
                socket.close();
            }
        } catch (IOException e) {
            WebSocketServerHelper.error("error occur when try to close client socket", e);
        }
    }

    /**
     * Reconnect to main listener
     */
    public void reConnectMainListener() {
        closeClientSocket();
        while(!start()) {
            WebSocketServerHelper.info("try to reconnect every " + MS_BETWEEN_TRIES);
            try {
                Thread.sleep(MS_BETWEEN_TRIES);
            } catch (InterruptedException e) {
                WebSocketServerHelper.error("error occur when try to reconnect", e);
            }
        }
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

        private boolean closed = false;

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
                WebSocketServerHelper.info("Couldn't get I/O for the connection.");
                e.printStackTrace();
            }
            connector = cn;
        }

        /**
         * Starts the thread processing.
         *
         */
        @Override
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
            @Override
            public void run() {
                while (true) {
                    Object o;
                    try {
                        o = csClient.readObject();

                        if (o != null) {
                        	if (o instanceof KeepAliveResponse) {
                                WebSocketServerHelper.info("Received keep alive response");
                        	} else {
                                processMessage(o);                        		
                        	}
                        }
                    } catch (SocketTimeoutException e) {
                        WebSocketServerHelper.info("Reading timeout... trying again...");
                        continue;
                    } catch (SocketException e) {
                        WebSocketServerHelper.error("Lost connection.", e);
                        connector.reConnectMainListener();
                        break;
                    } catch (StreamCorruptedException e) {
                        e.printStackTrace();
                        continue;
                    } catch (EOFException e) {
                        WebSocketServerHelper.error("Lost connection.", e);
                        connector.reConnectMainListener();
                        break;
                    } catch (IOException e) {
                        if (e.getMessage() != null
                                && (e.getMessage().toLowerCase().indexOf("stream closed") > -1
                                        || e.getMessage().toLowerCase().indexOf("stream is closed") > -1
                                        || e.getMessage().toLowerCase().indexOf("premature eof") > -1)) {
                            WebSocketServerHelper.error("Lost connection.", e);
                            connector.reConnectMainListener();
                            break;
                        }
                    } catch (Exception e) {
                        WebSocketServerHelper.info("Error processing received message.");
                        continue;
                    }
                }
                closed = true;
            }

            /**
             * Private method to process incoming message.
             *
             * @param o the incoming message
             */
            private void processMessage(Object o) {
                if (!(o instanceof SocketMessage)) {
                    WebSocketServerHelper.info("message is not a SocketMessage instance: " + o.toString());
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

                if(responseMsg instanceof RoundAccessResponse ||
                        responseMsg instanceof ChangeRoundResponse ||
                        responseMsg instanceof CommandResponse) {
                    WebSocketServerHelper.info("Handling " + responseMsg.getClass().getCanonicalName());
                    client.sendEvent(responseMsg.getClass().getSimpleName(), responseMsg);
                    WebSocketServerHelper.info(responseMsg.getClass().getCanonicalName() + " processed successfully.");
                    return;
                }
                if (!(responseMsg instanceof BaseResponse)) {
                    WebSocketServerHelper.info("Message is not a BaseResponse instance: " + responseMsg.toString());
                    return;
                }
                if (client == null) {
                    WebSocketServerHelper.info("Unknown webSocket connection uuid: " + uuid + ".");
                    return;
                }
                BaseResponse response = (BaseResponse) responseMsg;
                WebSocketServerHelper.info("Handling " + response.getClass().getCanonicalName() + ".");

                // Handle disconnection.
                if (response instanceof ForcedLogoutResponse) {
                    connector.getServer().getSessionToConnectionMap().remove(uuid);
                    connector.getServer().getSessionToUserHandleMap().remove(uuid);
                }
                // Pass responses directly.
                client.sendEvent(response.getClass().getSimpleName(), response);

                WebSocketServerHelper.info(response.getClass().getCanonicalName() + " processed successfully.");
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
            @Override
            public void run() {
            	long lastSendTime = -1;
                while (!closed) {
                    try {
                        SocketMessage message = null;
                        message = connector.queue.poll();
                        if (message != null) {
                        	if (message.getMessage() != null) {
                                WebSocketServerHelper.info("sending message: " + message.getMessage().getClass().getSimpleName());                        		
                        	}
                            csClient.writeObject(message);
                            lastSendTime = System.currentTimeMillis();
                        } else {
                            long currentTime = System.currentTimeMillis();
                            if (currentTime - lastSendTime >= 60000) {
                            	WebSocketServerHelper.info("sending keep alive message");
                            	csClient.writeObject(new KeepAliveRequest());
                            	lastSendTime = currentTime;
                            }
                            Thread.sleep(10);
                        }
                    } catch (Exception e) {
                        WebSocketServerHelper.error("Error processing single message.", e);
                        return;
                    }
                }

            }

        }

    }
}
