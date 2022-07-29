package com.topcoder.server.AdminListener;

import java.net.InetAddress;

import com.topcoder.server.util.TCLinkedQueue;
import com.topcoder.server.util.logging.net.*;
import com.topcoder.server.listener.ListenerInterface;
import org.apache.log4j.*;

public class AdminListener implements ListenerInterface {

    private int port, contestListenerPort;
    private InetAddress contestListenerAddress;
    private TCLinkedQueue incomingMessageQueue, backEndQueue;
    private ContestServerConnection contestServerConnection = null;
    private ClientConnectionSet clientConnectionManager;
    private AdminProcessor processor;
    private BackEndDispatcher backEndDispatcher;
    private LoggingServer loggingServer;
    private ContestManagementProcessor contestManagementProcessor;
    private static final Logger log = Logger.getLogger(AdminListener.class);

    AdminListener(int port, InetAddress contestListenerAddress, int contestListenerPort, int loggingServerPort)
            throws Exception {
        this.port = port;
        this.contestListenerAddress = contestListenerAddress;
        this.contestListenerPort = contestListenerPort;

        incomingMessageQueue = new TCLinkedQueue();
        backEndQueue = new TCLinkedQueue();
        loggingServer = new LoggingServer(this, loggingServerPort);
        contestManagementProcessor = new ContestManagementProcessor();
        processor = new AdminProcessor(this, incomingMessageQueue, backEndQueue, loggingServer, contestManagementProcessor);
        clientConnectionManager = new ClientConnectionSet(processor, port);
        backEndDispatcher = new BackEndDispatcher(backEndQueue, processor, contestManagementProcessor);
        contestServerConnection = new ContestServerConnection(processor, contestListenerAddress, contestListenerPort);
    }

    public void start() {
        // Start back end dispatcher thread
        backEndDispatcher.start();

        // start the loggin server
        loggingServer.start();

        // Start message processing thread
        processor.start();

        // Starts listening for clients
        clientConnectionManager.start();

        // Connect to the contest server if necessary
        if (contestListenerAddress != null) {
            contestServerConnection.start();
        }
    }

    public void stop() {

        // Stop back end dispatcher thread
        backEndDispatcher.stop();

        // Stop message processing thread
        processor.stop();

        // Shutdown logging server
        loggingServer.shutdown();

        // Stop listening for clients and shut down all existing client reader threads
        clientConnectionManager.stop();

        // Disconnect from the contest server (if applicable)
        contestServerConnection.stop();
    }

    /**
     * Sends the given object to the given connection ID.
     *
     * @param   connection_id       the connection ID that represents a socket connection with the given client.
     * @param   obj                 the object to be sent.
     */
    public void send(int connection_id, Object obj) {
        if (connection_id == AdminConstants.CONTEST_LISTENER_CONNECTION_ID) {
            contestServerConnection.writeObject(obj);
        } else {
            clientConnectionManager.writeObject(obj, connection_id);
        }
    }

    // Sends to all clients.
    public void clientBroadcast(Object obj) {
        clientConnectionManager.broadcast(obj);
    }

    /**
     * Shuts down the socket connection associated with the given connection ID.
     *
     * @param   connection_id       the connection ID that represents a socket connection with the given client.
     */
    public void shutdown(int connection_id) {
        if (connection_id == AdminConstants.CONTEST_LISTENER_CONNECTION_ID) {
            contestServerConnection.stop();
        } else {
            clientConnectionManager.closeConnection(connection_id);
        }
    }

    // The remaining ListenerInterface functions aren't really useful/applicable here,
    // so we dispose of them quickly.  If there isn't a pressing need to implement
    // the ListenerInterface I would like to delete these.
    public void shutdown(int connection_id, boolean notifyProcessor) {
        shutdown(connection_id);
    }

    public void banIP(String ipAddress) {
    }

    public int getConnectionsSize() {
        return 0;
    }

    public int getResponseQueueSize() {
        return 0;
    }

    public int getInTrafficSize() {
        return 0;
    }

    public int getOutTrafficSize() {
        return 0;
    }

    public int getMaxConnectionId() {
        return Integer.MAX_VALUE;
    }

    public int getMinConnectionId() {
        return 0;
    }
}

