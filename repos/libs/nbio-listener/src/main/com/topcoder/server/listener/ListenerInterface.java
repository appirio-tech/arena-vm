package com.topcoder.server.listener;

import java.io.IOException;

/**
 * The public listener interface.
 */
public interface ListenerInterface {

    /**
     * Starts the listener and all other components down the chain.
     *
     * @throws  java.io.IOException     if an I/O error occurs.
     */
    void start() throws IOException;

    /**
     * Stops the listener and all other components down the chain.
     */
    void stop();

    /**
     * Sends the given response for the given connection ID.
     *
     * @param   connection_id       the connection ID that represents a socket connection with the given client.
     * @param   response            the object to be sent.
     */
    void send(int connection_id, Object response);

    /**
     * Shuts down the socket connection associated with the given connection ID.
     *
     * @param   connection_id       the connection ID that represents a socket connection with the given client.
     */
    void shutdown(int connection_id);

    /**
     * Shuts down the socket connection associated with the given connection ID and notifies the processor
     * if notifyProcessor is <code>true</code>.
     *
     * @param   connection_id       the connection ID that represents a socket connection with the given client.
     * @param   notifyProcessor     whether the listener should notify the processor.
     */
    void shutdown(int connection_id, boolean notifyProcessor);

    void banIP(String ipAddress);

    /**
     * Gets the current number of connections. It's used only for testing.
     *
     * @return  the current number of connections.
     */
    int getConnectionsSize();

    /**
     * Gets the current response queue size. It's used only for testing.
     *
     * @return  the current response queue size.
     */
    int getResponseQueueSize();

    int getInTrafficSize();

    int getOutTrafficSize();


    /**
     * Returns the maximum value that a connection id handled by this listener can be.
     *
     * @return The value.
     */
    int getMaxConnectionId();

    /**
     * Returns the minimum value that a connection id handled by this listener can be.
     *
     * @return The value.
     */
    int getMinConnectionId();
    
}
