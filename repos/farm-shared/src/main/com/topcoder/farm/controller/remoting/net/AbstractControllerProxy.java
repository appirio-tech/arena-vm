/*
* Copyright (C) 2006-2013 TopCoder Inc., All Rights Reserved.
*/

/*
 * AbstractControllerProxy
 *
 * Created 07/21/2006
 */
package com.topcoder.farm.controller.remoting.net;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.topcoder.farm.controller.command.ControllerCommand;
import com.topcoder.farm.satellite.SatelliteNodeCallback;
import com.topcoder.farm.satellite.command.SatelliteCommand;
import com.topcoder.farm.shared.net.connection.api.Connection;
import com.topcoder.farm.shared.net.connection.api.ConnectionFactory;
import com.topcoder.farm.shared.net.connection.api.ReconnectableConnection;
import com.topcoder.farm.shared.net.connection.api.ReconnectableConnectionHandler;
import com.topcoder.farm.shared.net.connection.remoting.RemotingException;
import com.topcoder.farm.shared.net.connection.remoting.RequestResponseHandler;
import com.topcoder.farm.shared.net.connection.remoting.RequestResponseProcessor;
import com.topcoder.farm.shared.net.connection.remoting.TimeoutException;
import com.topcoder.farm.shared.net.connection.remoting.RequestResponseProcessor.RequestProcessor;
import com.topcoder.farm.shared.util.concurrent.WaitFlag;
import com.topcoder.farm.shared.util.concurrent.runner.Runner;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public abstract class AbstractControllerProxy implements RequestProcessor {
    private Log log = LogFactory.getLog(getClass());
    private ConnectionFactory factory;
    private RequestResponseHandler requestResponseHandler;
    private RequestResponseProcessor invocationProcessor;
    private Connection connection;
    private SatelliteNodeCallbackSkeleton nodeCallback;
    private String registeredId;
    private WaitFlag registered = new WaitFlag();
    private Runner requestRunner;
    private int registrationTimeout;
    private int ackTimeout;



    /**
     * Creates a new AbstractControllerProxy that uses the given factory
     * to obtain a connection to the Controller server and the given runner to
     * process incoming requests
     *
     * @param factory The factory to create connections to the controller
     * @param requestRunner  The runner to run requests
     * @param registrationTimeout maximum time to wait while asserting registration state (reconnecting)
     *                          0 means wait for ever
     * @param ackTimeout Max time in ms to wait for the ack response, 0 means wait for ever
     */
    public AbstractControllerProxy(ConnectionFactory factory, Runner requestRunner, int registrationTimeout, int ackTimeout) {
        this.factory = factory;
        this.requestRunner = requestRunner;
        this.registrationTimeout = registrationTimeout;
        this.ackTimeout = ackTimeout;
    }



    /**
     * Subclasses must implement SkeletonCallback to handle callbacks that are specifics
     * for the node type.
     * The skeleton callback should handle specific remoting behavior and delegate call
     * to the real callback object.
     *
     * @param callback The real callback object passed as argument in the register method
     *
     * @return The SkeletonCallback.
     */
    protected abstract SatelliteNodeCallbackSkeleton buildSkeletonCallback(SatelliteNodeCallback callback);


    /**
     * This method is called by the connection handler
     * when the connection has successfully reconnected.
     * The proxy skeleton is created when the connection is
     * established, but the registration must be invoked by the
     * satellite node
     */
    protected abstract void reRegister();


    /**
     * Initializes the connection to the controller
     */
    protected synchronized void init() throws RemotingException {
        log.debug("Initializing proxy");
        try {
            if (connection == null) {
                this.connection = factory.create(buildConnectionHandler());
                this.requestResponseHandler = new RequestResponseHandler(ackTimeout);
                this.invocationProcessor = new RequestResponseProcessor(this, requestRunner);
            }
            if (log.isDebugEnabled()) {
                log.debug("Initialized with connection:" + connection);
            }
        } catch (IOException e) {
            release();
            throw new RemotingException(e);
        }
    }


    /**
     * Releases the proxy object.
     * Clears the connection handler and closes the connection.
     * Stops the
     *
     *
     */
    protected synchronized void release() {
        if (connection != null) {
            log.debug("Releasing proxy");
            releaseConnection();
            this.requestRunner=null;
            this.requestResponseHandler = null;
            this.invocationProcessor = null;
            this.nodeCallback = null;
        }
    }

    protected void releaseConnection() {
        Connection cnn = connection;
        if (cnn != null) {
            connection.clearHandler();
            connection.close();
            this.connection = null;
        }
    }

    protected Object invokeSync(ControllerCommand command) throws Exception {
        return getRequestResponseHandler().invokeSync(getConnection(), command);
    }

    protected void invokeASync(ControllerCommand command) {
        getRequestResponseHandler().invokeAsync(getConnection(), command);
    }

    protected void invoke(ControllerCommand command) throws InterruptedException {
        getRequestResponseHandler().invoke(getConnection(), command);
    }

    /*
     * When the connection is lost, and then the reconnect succeeds
     * we must register with the controller
     */
    protected void handleReconnected() {
        setRegistered();
    }

    /*
     * The connection to the server have been lost
     * The reconnecting process is starting
     */
    protected void handleReconnecting() {
        clearRegistered();
        requestResponseHandler.connectionLost(connection);
        invocationProcessor.connectionLost(connection);
    }

    protected void handlePerformReconnection(ReconnectableConnection connection2) {
        reRegister();
    }

    protected void handleConnectionLost() {
        clearRegistered();
        SatelliteNodeCallback realCallback = nodeCallback.getRealCallback();
        releaseConnection();
        log.info("CONNECTION LOST");
        realCallback.unregistered("Connection Lost");
    }

    protected void handleConnectionClosed() {
        releaseConnection();
        log.info("CONNECTION CLOSED");
    }



    protected void handleReceived(Object object) {
        //We try first for a response to an invocation
        if (!requestResponseHandler.processMessage(connection, object)) {
            if (!invocationProcessor.processMessage(connection, object)) {
                log.error("Invalid remote invocation received: " + object);
            }
        }
    }

    /**
     * @see com.topcoder.farm.shared.net.connection.remoting.RequestResponseProcessor.RequestProcessor#processRequest(Object)
     */
    public Object processRequest(Object object) throws Exception {
        if (object instanceof SatelliteCommand) {
            SatelliteCommand msg = (SatelliteCommand) object;
            return msg.execute(getNodeCallback());
        } else {
            log.error("Invalid remote invocation received: " + object);
            return null;
        }
    }

    protected Connection getConnection() {
        return connection;
    }

    protected void handleUnexpectedException(Exception e) {
        throw new RemotingException("Server thrown an expected exception", e);
    }

    protected RequestResponseHandler getRequestResponseHandler() {
        return requestResponseHandler;
    }

    protected void setRequestResponseHandler(RequestResponseHandler invoker) {
        this.requestResponseHandler = invoker;
    }

    protected SatelliteNodeCallback getNodeCallback() {
        return nodeCallback;
    }

    protected void setNodeCallback(SatelliteNodeCallback nodeRef) {
        this.nodeCallback = buildSkeletonCallback(nodeRef);
    }

    protected String getRegisteredId() {
        return registeredId;
    }

    protected void setRegisteredId(String registeredId) {
        this.registeredId = registeredId;
    }

    protected void setRegistered() {
        registered.set();
    }

    void clearRegistered() {
        registered.clear();
    }

    boolean isRegistered() {
        return registered.isSet();
    }

    protected void assertRegistered() {
        try {
            if (!registered.await(registrationTimeout)) {
                throw new TimeoutException("Timeout while wating for registration");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Thread interrupted");
        }
    }

    private ReconnectableConnectionHandler buildConnectionHandler() {
        return new ReconnectableConnectionHandler() {
            public void reconnecting(ReconnectableConnection connection) {
                handleReconnecting();
            }

            public void connectionLost(Connection connection) {
                handleConnectionLost();
            }

            public void connectionClosed(Connection connection) {
                handleConnectionClosed();
            }

            public void receive(Connection connection, Object object) {
                handleReceived(object);
            }

            public void performReconnection(ReconnectableConnection connection) {
                handlePerformReconnection(connection);
            }

            public void reconnected(ReconnectableConnection connection) {
                handleReconnected();
            }
        };
    }

    /**
     * <p>
     * Changes in version 1.0 (TC Competition Engine - Processor and Controller Handshake Change v1.0):
     * <ol>
     *      <li>Add {@link #getEndpointIP()} method.</li>
     * </ol>
     * </p>
     * @author savon_cn
     * @version 1.0
     */
    class SatelliteNodeCallbackSkeleton implements SatelliteNodeCallback {
        private SatelliteNodeCallback realCallback;

        public SatelliteNodeCallbackSkeleton(SatelliteNodeCallback realCallback) {
            this.realCallback = realCallback;
        }

        public void unregistered(String cause) {
            try {
                realCallback.unregistered(cause);
            } finally {
                release();
            }
        }

        public void disconnect(String cause) {
            realCallback.disconnect(cause);
        }

        public SatelliteNodeCallback getRealCallback() {
            return realCallback;
        }

        public void setRealCallback(SatelliteNodeCallback realCallback) {
            this.realCallback = realCallback;
        }
        
        @Override
        public String getEndpointString() {
            return realCallback.getEndpointString();
        }
        /**
         * Get the remote processor ip
         * @return the remote processor ip
         */
        @Override
        public String getEndpointIP() {
            return realCallback.getEndpointIP();
        }
    }
}
