/*
 * Copyright (C) 2014-2015 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.server.listener;

import java.io.EOFException;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.security.Key;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contestantMessages.request.LoginRequest;
import com.topcoder.netCommon.contestantMessages.request.SSOLoginRequest;
import com.topcoder.netCommon.contestantMessages.response.PopUpGenericResponse;
import com.topcoder.netCommon.io.ClientSocket;
import com.topcoder.server.AdminListener.request.BackEndLoginRequest;
import com.topcoder.server.AdminListener.request.BackEndChangeRoundRequest;
import com.topcoder.server.AdminListener.request.BackEndRoundAccessRequest;
import com.topcoder.server.AdminListener.request.ChangeRoundRequest;
import com.topcoder.server.AdminListener.request.LoadRoundRequest;
import com.topcoder.server.AdminListener.request.RoundAccessRequest;
import com.topcoder.server.AdminListener.response.LoginResponse;
import com.topcoder.server.AdminListener.response.ChangeRoundResponse;
import com.topcoder.server.AdminListener.response.RoundAccessResponse;
import com.topcoder.server.ejb.AdminServices.AdminServices;
import com.topcoder.server.ejb.AdminServices.AdminServicesLocator;
import com.topcoder.server.listener.monitor.MonitorProcessor;
import com.topcoder.server.listener.socket.messages.SocketMessage;
import com.topcoder.server.processor.RequestProcessor;
import com.topcoder.server.security.PrivateKeyObtainer;
import com.topcoder.shared.netCommon.CSHandler;
import com.topcoder.server.services.CoreServices;
import com.topcoder.server.common.User;

/**
 * The connector to the web socket listener.
 * <p>
 * Version 1.1 - Module Assembly - Web Socket Listener - Porting Round Load Related Events
 * <ol>
 *      <li>Added {@link #adminServices} and {@link #monitorProcessor} to process new Round
 *      load related requests</li>
 * </ol>
 * </p>
 * @author gondzo, ananthhh
 * @version 1.1
 */
public class WebSocketConnector {
    private static final Logger logger = Logger.getLogger(WebSocketConnector.class);
	
	/**
     * The mapping of the client connection id to uuid.
     */
    private static Map<Integer, UUID> connections = new ConcurrentHashMap<Integer, UUID>();

    /**
     * The port to connect to.
     */
    private int port;

    /**
     * The socket to send and receive messages.
     */
    private ServerSocket serverSocket;

    /**
     * The queue of messages to send.
     */
    private final Queue<SocketMessage> queue = new ConcurrentLinkedQueue<SocketMessage>();

    /**
     * The arena processor.
     */
    private ArenaProcessor processor;

    /**
     * Handle to AdminServices EJB
     */
    private AdminServices adminServices;

    /**
     * Handle to MonitorProcessor
     * This is used to handle all MonitorRequests from web arena
     */
    private MonitorProcessor monitorProcessor;

    /**
     * Creates a new instance of this class.
     *
     * @param port    the port number
     */
    public WebSocketConnector(int port) {
        this.port = port;
    }

    /**
     * Setter of the arena processor.
     *
     * @param ap    the arena processor
     */
    public void setProcessor(ArenaProcessor ap) {
        processor = ap;
    }

    /**
     * Public api to get whether a client is connected or not.
     *
     * @param cid    the id of the client connection
     * @return if the client is connected to this connection
     */
    public boolean isConnected(int cid) {
        return connections.containsKey(cid);
    }

    /**
     * Setter for monitor processor
     *
     * @param mp the monitor processor
     */
    public void setMonitorProcessor(MonitorProcessor mp) {
        monitorProcessor = mp;
    }

    /**
     * Public api to send a message to the client.
     *
     * @param connectionID    the id of the client connection
     * @param message    the message to send
     */
    public void write(int connectionID, Object message) {
        if (message instanceof MultiMessage) {
            List messages = ((MultiMessage) message).getMessages();
            for (int i = 0; i < messages.size(); i++) {
                queue.add(new SocketMessage(connections.get(connectionID), messages.get(i)));
            }
        } else {
            queue.add(new SocketMessage(connections.get(connectionID), message));
        }
    }

    /**
     * Public api to remove the connection id from the set of connections.
     *
     * @param connectionID    the id of the client connection
     */
    public void remove(int connectionID) {
        connections.remove(connectionID);
    }
    /**
     * Private api to process a message from a client.
     *
     * @param o    the message to process
     */
    private void processMessage(Object o) {
        if (!(o instanceof SocketMessage)) {
            // invalid message
            ListenerMain.info("invalid message received: ");
            return;
        }
        ListenerMain.info("handling received message: ");
        SocketMessage sm = (SocketMessage) o;
        int cid = sm.getUuid().hashCode();
        Object msg = sm.getMessage();
        if (!connections.containsKey(cid)) {
            ListenerMain.info("adding new connection from websocket");
            processor.newConnection(cid, "127.0.0.1");
            monitorProcessor.newConnection(cid, "127.0.0.1");
            connections.put(new Integer(cid), sm.getUuid());
        }
        if (msg instanceof LoginRequest) {
            LoginRequest lr = (LoginRequest) msg;
            ListenerMain.info("handling login request: " + lr.getUserID() + " " + lr.getPassword());

            ListenerMain.info("processing new websocket messages");
            RequestProcessor.processNoLimit(cid, lr);
        } else if (msg instanceof SSOLoginRequest) {
            SSOLoginRequest lr = (SSOLoginRequest) msg;

            ListenerMain.info("processing new websocket messages");
            RequestProcessor.processNoLimit(cid, lr);
            
            // login to admin tool if this is a admin user, so that we can do round operation
            String[] userInfo = CoreServices.getUserInfoBySSO(lr.getSSO());
            if (userInfo != null) {
                User user = CoreServices.getUser(userInfo[0], false);
                if (user != null && user.isLevelTwoAdmin()) {
                    try {
                        LoginResponse response = adminServices.
                                processLoginRequest(new BackEndLoginRequest(cid, userInfo[0], userInfo[1].toCharArray()));
                        ListenerMain.info(response.toString());
                        this.write(cid, response);
                    } catch (Exception e) {
                        this.write(cid, new PopUpGenericResponse("Login Admin Error",
                                "Error occured while processing your request", ContestConstants.GENERIC,
                                ContestConstants.LABEL));
                        ListenerMain.error(e);
                    }
                }
            }
        } else if (msg instanceof RoundAccessRequest) {
            try {
                RoundAccessResponse response = adminServices.
                        processRoundAccessRequest(new BackEndRoundAccessRequest(cid, RequestProcessor.getUserID(cid)));
                ListenerMain.info(response.toString());
                this.write(cid, response);
            } catch (Exception e) {
                this.write(cid, new PopUpGenericResponse("Round Access Error",
                        "Error occured while processing your request", ContestConstants.GENERIC,
                        ContestConstants.LABEL));
                ListenerMain.error(e);
            }
        } else if (msg instanceof ChangeRoundRequest) {
            try {
                ChangeRoundResponse response = adminServices.
                        processChangeRoundRequest(new BackEndChangeRoundRequest(cid, RequestProcessor.getUserID(cid),
                                ((ChangeRoundRequest) msg).getRoundId()));
                ListenerMain.info(response.toString());
                this.write(cid, response);
            } catch (Exception e) {
                this.write(cid, new PopUpGenericResponse("Change round Error",
                        "Error occured while processing your request",
                        ContestConstants.GENERIC, ContestConstants.LABEL));
                ListenerMain.error(e);
            }

        } else if (msg instanceof LoadRoundRequest) {
            ListenerMain.info("Handling LoadRoundRequest ");
            monitorProcessor.receive(cid, msg);
        } else if (msg != null) {
            ListenerMain.info("message received: " + msg.getClass());
            RequestProcessor.processNoLimit(cid, msg);
        }
    }

    /**
     * Public api to start listening for connections.
     *
     * @return whether the server is started or not
     */
    public boolean start() {
        try {
            serverSocket = new ServerSocket(port);
            adminServices = AdminServicesLocator.getService();
            new Thread(new Runnable() {

                @Override
                public void run() {
                    while (true) {
                        try {
                            Socket clientSocket = serverSocket.accept();
                            ConnectionManager cm = new ConnectionManager(clientSocket, WebSocketConnector.this);
                            new Thread(cm).start();
                        } catch (IOException e) {
                        	logger.error("Unable to start server socket: "+ e.getMessage(), e);
                        }
                    }
                }
            }).start();
        } catch (Exception ex) {
        	logger.fatal("Unable to listen on port " + port + ": " + ex.getMessage(), ex);
            ListenerMain.info("Could not listen on port:" + port);
            return false;
        }

        return true;
    }

    /**
     * Private class to manage a single client connection.
     * <p>
     * Changes in version 1.1 (Module Assembly - TCC Web Socket - Get Registered
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
     * @author TCSASSEMBLER, dexy
     * @version 1.1
     */
    private class ConnectionManager implements Runnable {
        /** The web socket connector. */
        private WebSocketConnector connector;

        /**
         * The client socket object used to send and receive
         * CustomSerializable objects.
         *
         * @since 1.1
         */
        private ClientSocket csClient;

        /**
         * The thread for reading messages.
         */
        private Thread readThread;

        /**
         * The thread to write messages.
         */
        private Thread writeThread;

        /**
         * Creates a new instance of this class.
         *
         * @param socket    The socket used to send and receive messages
         * @param cn    the WebSocketConnector instance
         */
        public ConnectionManager(Socket socket, WebSocketConnector cn) {
            Key encryptKey = PrivateKeyObtainer.obtainPrivateKey();
            CSHandler csHandler = new WSSCommonCSHandler(encryptKey);
            try {
                csClient = new ClientSocket(socket, csHandler);
            } catch (IOException e) {
                ListenerMain.info("Couldn't get I/O for the connection.");
                logger.error("Unable to connect to client socket: " + e.getMessage(), e);
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
         * Private class to manage reading received messages.
         *
         * <p>
         * Changes in version 1.1 (Module Assembly - TCC Web Socket - Get RegisteredRounds and
         * Round Problems):
         * <ol>
         *      <li>Change {@link #SocketReadThread(WebSocketConnector} constructor.</li>
         *      <li>Remove {@link #run()} method to handle readObject.</li>
         * </ol>
         * </p>
         *
         * @author TCSASSEMBLER, dexy
         * @version 1.1
         */
        private class SocketReadThread implements Runnable {
            /**
             * The main listener connector.
             */
            private WebSocketConnector connector;

            /**
             * Creates a new instance of this class.
             *
             * @param cn    the WebSocketConnector instance
             */
            public SocketReadThread(WebSocketConnector cn) {
                connector = cn;
            }

            /**
             * Starts the thread processing.
             *
             */
            @Override
            public void run() {
                while (true) {
                    Object o;
                    try {
                        ListenerMain.info("ListenerMain reading object from reader...");
                        o = csClient.readObject();
                        if (o != null) {
                            processMessage(o);
                        }
                    } catch (SocketTimeoutException e) {
                    	logger.info("Socket timeout:" + e.getMessage());
                        //ListenerMain.info("Reading timeout... trying again...");
                        continue;
                    } catch (SocketException e) {
                        //ListenerMain.info("Lost connection.");
                    	logger.warn("Socket exception: "+ e.getMessage(), e);
                        break;
                    } catch (StreamCorruptedException e) {
                        logger.warn("Corrupted stream exception: "+ e.getMessage(), e);
                        continue;
                    } catch (EOFException e) {
                    	logger.info("EOF while reading/processing message:" + e.getMessage());
                        //ListenerMain.info("Lost connection.");
                        break;
                    } catch (IOException e) {
                        if (e.getMessage() != null
                                && (e.getMessage().toLowerCase().indexOf("stream closed") > -1
                                        || e.getMessage().toLowerCase().indexOf("stream is closed") > -1
                                        || e.getMessage().toLowerCase().indexOf("premature eof") > -1)) {
                            //ListenerMain.info("Lost connection.");
                        	logger.info("Lost connection:" + e.getMessage());
                            break;
                        }
                    } catch (Exception e) {
                        //ListenerMain.info("Error processing received message.");
                    	logger.warn("Unknown error while reading/processing message");
                        continue;
                    }
                }
            }

            /**
             * Process the received message.
             *
             * @param o     The received message object
             */
            private void processMessage(Object o) {
            	if (logger.isInfoEnabled()) {
            		logger.info("Received message: " + o.toString());
            	}
                connector.processMessage(o);
            }

        }

        /**
         * Private class to manage writing messages.
         *
         * <p>
         * Changes in version 1.1 (Module Assembly - TCC Web Socket - Get Registered Rounds and
         * Round Problems):
         * <ol>
         *      <li>Change {@link #SocketWriteThread(WebSocketConnector} constructor.</li>
         *      <li>Remove {@link #run()} method to handle writeObject.</li>
         * </ol>
         * </p>
         *
         * @author TCSASSEMBLER, dexy
         * @version 1.1
         */
        private class SocketWriteThread implements Runnable {
            /**
             * The main listener connector.
             */
            private WebSocketConnector connector;

            /**
             * Creates a new instance of this class.
             *
             * @param cn    the WebSocketConnector instance
             */
            public SocketWriteThread(WebSocketConnector cn) {
                connector = cn;
            }

            /**
             * Starts the thread processing.
             *
             */
            @Override
            public void run() {
                while (true) {
                    try {
                        SocketMessage message = null;
                        message = connector.queue.poll();
                        if (message != null) {
                        	if (logger.isInfoEnabled()) {
								logger.info("ListenerMain writing object " + message.getClass() + " to writer...");
                        	}

                            csClient.writeObject(message);
                        } else {
                            Thread.sleep(10);
                        }
                    } catch (Exception e) {
                    	logger.info("Unable to write to client: "+ e.getMessage());
                        //ListenerMain.info("Error while sending message.");
                        return;
                    }
                }

            }

        }

    }

    /**
     * The main method to start the WebSocketConnector.
     *
     * @param args the input arguments
     */
    public static void main(String args[]) {
        WebSocketConnector wsc = new WebSocketConnector(5555);
        boolean started = wsc.start();
        ListenerMain.info("started: " + started);

    }
}
