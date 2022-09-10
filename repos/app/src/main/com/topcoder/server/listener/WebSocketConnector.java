/*
 * Copyright (C) ~2014 TopCoder Inc., All Rights Reserved.
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

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contestantMessages.request.KeepAliveRequest;
import com.topcoder.netCommon.contestantMessages.request.LoginRequest;
import com.topcoder.netCommon.contestantMessages.response.KeepAliveResponse;
import com.topcoder.netCommon.contestantMessages.response.PopUpGenericResponse;
import com.topcoder.netCommon.io.ClientSocket;
import com.topcoder.server.AdminListener.request.BackEndChangeRoundRequest;
import com.topcoder.server.AdminListener.request.BackEndRoundAccessRequest;
import com.topcoder.server.AdminListener.request.ChangeRoundRequest;
import com.topcoder.server.AdminListener.request.LoadRoundRequest;
import com.topcoder.server.AdminListener.request.RoundAccessRequest;
import com.topcoder.server.AdminListener.response.ChangeRoundResponse;
import com.topcoder.server.AdminListener.response.RoundAccessResponse;
import com.topcoder.server.ejb.AdminServices.AdminServices;
import com.topcoder.server.ejb.AdminServices.AdminServicesLocator;
import com.topcoder.server.listener.monitor.MonitorProcessor;
import com.topcoder.server.listener.socket.messages.SocketMessage;
import com.topcoder.server.processor.RequestProcessor;
import com.topcoder.server.security.PrivateKeyObtainer;
import com.topcoder.shared.netCommon.CSHandler;

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
    /**
     * The mapping of the client connection id to connection data.
     */
    private static Map<Integer, WebsocketConnectionData> connections = new ConcurrentHashMap<Integer, WebsocketConnectionData>();


    /**
     * The port to connect to.
     */
    private int port;

    /**
     * The socket to send and receive messages.
     */
    private ServerSocket serverSocket;

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
    	WebsocketConnectionData connectionData = connections.get(connectionID);
        if (message instanceof MultiMessage) {
            List messages = ((MultiMessage) message).getMessages();
            for (int i = 0; i < messages.size(); i++) {
            	connectionData.queue.add(new SocketMessage(connectionData.uuid, messages.get(i)));
            }
        } else {
        	connectionData.queue.add(new SocketMessage(connectionData.uuid, message));
        }
    }

    /**
     * Public api to remove the connection id from the set of connections.
     *
     * @param connectionID    the id of the client connection
     */
    public void remove(int connectionID) {
    	ListenerMain.info("WebSocketConnector remove websocket connection: " + connectionID);
        connections.remove(connectionID);
    }
    /**
     * Private api to process a message from a client.
     *
     * @param o    the message to process
     */
    private void processMessage(Object o, Queue<Object> queue) {
        if (!(o instanceof SocketMessage)) {
            // invalid message
            ListenerMain.info("WebSocketConnector invalid message received: ");
            return;
        }
        SocketMessage sm = (SocketMessage) o;
        int cid = sm.getUuid().hashCode();
        Object msg = sm.getMessage();
        if (!connections.containsKey(cid)) {
            ListenerMain.info("WebSocketConnector adding new connection from websocket");
            processor.newConnection(cid, "127.0.0.1");
            monitorProcessor.newConnection(cid, "127.0.0.1");
            connections.put(new Integer(cid), new WebsocketConnectionData(sm.getUuid(), queue));
        }
        if (msg instanceof LoginRequest) {
            LoginRequest lr = (LoginRequest) msg;
            ListenerMain.info("WebSocketConnector handling LoginRequest: " + lr.getUserID() + " " + lr.getPassword());

            RequestProcessor.processNoLimit(cid, lr);
        } else if (msg instanceof RoundAccessRequest) {
            ListenerMain.info("WebSocketConnector handling RoundAccessRequest ");
            try {
                RoundAccessResponse response = adminServices.
                        processRoundAccessRequest(new BackEndRoundAccessRequest(cid, RequestProcessor.getUserID(cid)));
                this.write(cid, response);
            } catch (Exception e) {
                this.write(cid, new PopUpGenericResponse("Round Access Error",
                        "Error occured while processing your request", ContestConstants.GENERIC,
                        ContestConstants.LABEL));
                ListenerMain.error(e);
            }
        } else if (msg instanceof ChangeRoundRequest) {
            ListenerMain.info("WebSocketConnector handling ChangeRoundRequest ");
            try {
                ChangeRoundResponse response = adminServices.
                        processChangeRoundRequest(new BackEndChangeRoundRequest(cid, RequestProcessor.getUserID(cid),
                                ((ChangeRoundRequest) msg).getRoundId()));
                this.write(cid, response);
            } catch (Exception e) {
                this.write(cid, new PopUpGenericResponse("Change round Error",
                        "Error occured while processing your request",
                        ContestConstants.GENERIC, ContestConstants.LABEL));
                ListenerMain.error(e);
            }

        } else if (msg instanceof LoadRoundRequest) {
            ListenerMain.info("WebSocketConnector handling LoadRoundRequest ");
            monitorProcessor.receive(cid, msg);
        } else if (msg != null) {
            ListenerMain.info("WebSocketConnector handling " + msg.getClass().getSimpleName());
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

                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        } catch (Exception ex) {
            ListenerMain.info("WebSocketConnector could not listen on port:" + port);
            return false;
        }

        return true;
    }

    private static class WebsocketConnectionData {
    	private final UUID uuid;
    	private final Queue<Object> queue;
    	public WebsocketConnectionData(UUID uuid, Queue<Object> queue) {
    		this.uuid = uuid;
    		this.queue = queue;
    	}
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
         * The queue of messages to send.
         */
        private final Queue<Object> queue = new ConcurrentLinkedQueue<Object>();

        private boolean closed = false;

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

                ListenerMain.info("WebSocketConnector accepted socket from " + csClient.getRemoteEndpoint());
            } catch (IOException e) {
                ListenerMain.info("WebSocketConnector couldn't get I/O for the connection.");
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
                        o = csClient.readObject();
                        if (o != null) {
                        	if (o instanceof KeepAliveRequest) {
                                ListenerMain.info("WebSocketConnector received keep alive request");
                                queue.add(new KeepAliveResponse(((KeepAliveRequest) o).getRequestType()));
                        	} else {
                                processMessage(o);
                        	}
                        }
                    } catch (SocketTimeoutException e) {
                        ListenerMain.info("WebSocketConnector Reading timeout... trying again...");
                        continue;
                    } catch (SocketException e) {
                        ListenerMain.info("WebSocketConnector Lost connection.");
                        break;
                    } catch (StreamCorruptedException e) {
                        e.printStackTrace();
                        continue;
                    } catch (EOFException e) {
                        ListenerMain.info("WebSocketConnector Lost connection.");
                        break;
                    } catch (IOException e) {
                        if (e.getMessage() != null
                                && (e.getMessage().toLowerCase().indexOf("stream closed") > -1
                                        || e.getMessage().toLowerCase().indexOf("stream is closed") > -1
                                        || e.getMessage().toLowerCase().indexOf("premature eof") > -1)) {
                            ListenerMain.info("WebSocketConnector Lost connection.");
                            break;
                        }
                    } catch (Exception e) {
                        ListenerMain.info("WebSocketConnector Error processing received message.");
                        continue;
                    }
                }
                try {
					csClient.close();
	                ListenerMain.info("WebSocketConnector closed socket from " + csClient.getRemoteEndpoint());
				} catch (IOException e) {
					// ignore
				}
            	closed = true;
            }

            /**
             * Process the received message.
             *
             * @param o     The received message object
             */
            private void processMessage(Object o) {
                connector.processMessage(o, queue);
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
                while (!closed) {
                    try {
                    	Object message = queue.poll();
                        if (message != null) {
                        	if (message instanceof SocketMessage && ((SocketMessage) message).getMessage() != null) {
                                ListenerMain.info("WebSocketConnector sending message " + ((SocketMessage) message).getMessage().getClass().getSimpleName());
                        	}

                            csClient.writeObject(message);
                        } else {
                            Thread.sleep(10);
                        }
                    } catch (Exception e) {
                        ListenerMain.info("WebSocketConnector Error while sending message.");
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
        ListenerMain.info("WebSocketConnector started: " + started);

    }
}
