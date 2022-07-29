package com.topcoder.server.listener;


/**
 * The public processor interface.
 */
public interface ProcessorInterface {

    /**
     * Sets the listener that should be used with this processor.
     *
     * @param   listener    the listener.
     */
    void setListener(ListenerInterface listener);

    /**
     * Starts the processor.
     */
    void start();

    /**
     * Stops the processor.
     */
    void stop();

    /**
     * Notifies the processor of the new connection that has a unique connection ID. The connection was
     * made from the given remove IP address.
     *
     * @param   connection_id       the unique connection ID.
     * @param   remoteIP            the remote IP address in the textual form from which the connection was made.
     */
    void newConnection(int connection_id, String remoteIP);

    /**
     * Notifies the processor that a new request was received from the client associated with the given connection ID.
     *
     * @param   connection_id       the connection ID that represents a socket connection with the given client.
     * @param   request             the received request.
     */
    void receive(int connection_id, Object request);

    /**
     * Notifies the processor that we lost connection with the client represents by the given connection ID.
     *
     * @param   connection_id       the connection ID that represents a socket connection with the given client.
     */
    void lostConnection(int connection_id);
    
    /**
     * Notifies the processor that we lost connection with the client represents by the given connection ID.
     *
     * @param   connection_id       the connection ID that represents a socket connection with the given client.
     *
     * @returns true if the connection should be killed from all monitors, etc.
     */
    void lostConnectionTemporarily(int connection_id);

}
