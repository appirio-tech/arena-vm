/*
 * AbstractConnection
 *
 * Created 06/29/2006
 */
package com.topcoder.farm.shared.net.connection.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.topcoder.farm.shared.net.connection.api.Connection;
import com.topcoder.farm.shared.net.connection.api.ConnectionHandler;
import com.topcoder.farm.shared.util.concurrent.Lock;

/**
 * This class provides a skeletal implementation of the <tt>Connection</tt>
 * interface to minimize the effort required to implement this interface.
 * For decorated connection use the <tt>AbstractDecoratedConnection</tt> should
 * be used in preference to this class.
 *
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public abstract class AbstractConnection implements Connection {
    /**
     * Log category for this instance
     */
    protected Log log = LogFactory.getLog(getClass());

    /**
     * The handler responsible for handling the connection events
     */
    private volatile ConnectionHandler handler = getNullHandler();

    /**
     * This Lock provides synchronization support for handling state transition on
     * concurrent enviroments.
     *
     * Before obtaining the state and invoking the proper method on it, it is
     * necessary to lock on this object if the state will generate a state transition
     *
     * In general, as soon as the state has been changed in the transtion handling method
     * of this class, the stateLock should be release to avoid resource contention
     */
    protected final Lock stateLock = new Lock();

    /**
     * The current state of the connection
     */
    private ConnectionState state;


    /**
     * Attributes for this connection
     */

    private Map attributes = new HashMap();

    /**
     * Create a new AbstractConnection
     * The state of this newly created connections is obtained
     * using the method getConnectedState()
     */
    protected AbstractConnection() {
        state = getConnectedState();
    }

    /**
     * The send method.
     *
     * This method is called when a send request has been invoked
     * in this connection and the state of the connection is allowing
     * the sending.
     *
     * This is the actual send method implementation.
     *
     * @param object Object to send through the connection
     *
     * @throws IOException if the exception is thrown during the sending
     */
    protected abstract void bareSend(Object object) throws IOException;

    /**
     * The close method
     *
     * This method is called when a close request has been invoked
     * in this connection and the state of the connection is allowing
     * the closing.
     *
     * This is the actual close method implementation.
     *
     */
    protected abstract void bareClose();

    /**
     * Handles the lost of the connection
     *
     * SubClasses must invoke this method when the underlying
     * connection is detected as lost. The underlying connection
     * should be properly handled by the caller
     *
     * Handles synchronization issues that could occurr
     * during state delegation and transition.
     */
    public void handleConnectionLost() {
        int lck = stateLock.lock();
        try {
            getState().handleConnectionLost(this);
        } catch (Exception e) {
            log.warn("Exception raise during event notification",e);
        } finally {
            stateLock.tryToUnlock(lck);
        }
    }

    /**
     * Handles the closing of the connection.
     *
     * SubClasses must invoke this method when the underlying
     * connection is detected as closed. The underlying connection
     * must be properly handled by the caller
     *
     * Handles synchronization issues that could occurr
     * during state delegation and transition.
     */
    public void handleConnectionClosed() {
        int lck = stateLock.lock();
        try {
            getState().handleConnectionClosed(this);
        } catch (Exception e) {
            log.warn("Exception raise during event notification",e);
        } finally {
            stateLock.tryToUnlock(lck);
        }
    }

    /**
     * Handles connection end event.<p>
     * If the close method was invoked for this connection, handleConnectionClosed will be called
     * otherwise handleConnectionLost will be called.
     *
     */
    public void handleConnectionEnd() {
        if (!isClosed()) {
            handleConnectionLost();
        } else {
            handleConnectionClosed();
        }
    }

    /**
     * Handles the received event.
     *
     * SubClasses must invoke this method when a incoming object is received
     * by the underlying connection.
     *
     * @param object Received Object
     */
    public void handleReceived(Object object) {
        try {
            if (log.isDebugEnabled()) {
                log.debug("Object received "+object);
            }
            getState().handleReceived(this, object);
        } catch (Exception e) {
            log.warn("Exception raise during event notification",e);
        }
    }

    /**
     * Notifies to the Handler that the connection has been closed
     */
    protected void notifyConnectionClosed() {
       log.debug("Reporting connection as closed");
       handler.connectionClosed(this);
    }

    /**
     * Notifies to the Handler that the connection has been lost
     */
    protected void notifyConnectionLost() {
        log.debug("Reporting connection as lost");
        handler.connectionLost(this);
     }

    /**
     * Notifies to the handler that an <tt>object</tt> has been received by this
     * connection.
     *
     * @param object Object received by this connection
     */
    protected void notifyReceived(Object object) {
        handler.receive(this, object);
    }

    /**
     * @see Connection#close()
     */
    public void close() {
        int lck = stateLock.lock();
        try {
            getState().close(this);
        } finally {
            stateLock.tryToUnlock(lck);
        }
    }

    /**
     * @see Connection#send(Object)
     */
    public void send(Object object) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug("Sending object "+object);
        }
        getState().send(this, object);
        log.debug("Object sent");
    }

    /**
     * @see Connection#setHandler(ConnectionHandler)
     */
    public void setHandler(ConnectionHandler handler) {
        this.handler = handler;
    }


    /**
     * @see Connection#clearHandler()
     */
    public void clearHandler() {
        this.handler = getNullHandler();
    }

    /**
     * @see Connection#isClosed()
     */
    public boolean isClosed() {
        return getState().isClosed();
    }

    /**
     * @see Connection#isLost()
     */
    public boolean isLost() {
        return getState().isLost();
    }

    /**
     * @see Connection#getHandler()
     */
    public ConnectionHandler getHandler() {
        return handler;
    }

    /**
     * @see com.topcoder.farm.shared.net.connection.api.Connection#setAttribute(java.lang.String, java.lang.Object)
     */
    public Object setAttribute(String key, Object value) {
       synchronized (attributes) {
           return attributes.put(key, value);
       }
    }

    /**
     * @see com.topcoder.farm.shared.net.connection.api.Connection#getAttribute(java.lang.String)
     */
    public Object getAttribute(String key) {
        synchronized (attributes) {
            return attributes.get(key);
        }
    }

    /**
     * @see com.topcoder.farm.shared.net.connection.api.Connection#removeAttribute(java.lang.String)
     */
    public Object removeAttribute(String key) {
        synchronized (attributes) {
            return attributes.remove(key);
        }
    }

    /**
     * Set the current connection state
     *
     * Note: This method is not synchronized, synchronization should be done
     * by the caller
     *
     * @param state new State for the connection
     */
    protected void setState(ConnectionState state) {
        this.state = state;
    }

    /**
     * Return the current state for the connection
     *
     * Note: This method is not synchronized, synchronization should be done
     * by the caller
     *
     * @return the State
     */
    protected ConnectionState getState() {
        return state;
    }

    /**
     * Actual handling of the connection lost event.
     *
     * Note: State was changed previously
     */
    protected void doHandleConnectionLost() {
        try {
            notifyConnectionLost();
        } finally {
            release();
        }
    }

    /**
     * Actual handling of the connection closed event
     *
     * Note: State was changed previously
     */
    protected void doHandleConnectionClosed() {
        try {
            notifyConnectionClosed();
        } finally {
            release();
        }
    }

    /**
     * Actual handling of the connection received event
     *
     * @param object Object received
     */
    public void doHandleReceived(Object object) {
        notifyReceived(object);
    }

    /**
     * Returns the connected state instance for this connection
     *
     * @return the State
     */
    protected ConnectionState getConnectedState() {
        return ConnectedState.getInstance();
    }

    /**
     * Returns the closed state instace for this connection
     *
     * @return the State
     */
    protected ConnectionState getClosedState() {
        return ClosedState.getInstance();
    }

    /**
     * Returns the closing state instace for this connection
     *
     * @return the State
     */
    protected ConnectionState getClosingState() {
        return ClosingState.getInstance();
    }

    /**
     * Returns the lost state instace for this connection
     *
     * @return the State
     */
    protected ConnectionState getLostState() {
        return LostState.getInstance();
    }

    /**
     * State transition from Connected To Closing
     * Impl: Changes the state and does the operation implied by the transition
     *
     * Default operation: doClose
     */
    public void stateChangingFromConnectedToClosing() {
        this.setState(getClosingState());
        stateLock.unlock();
        this.bareClose();
    }


    /**
     * State transition from Connected To Lost
     * Impl: Changes the state and does the operation implied by the transition
     *
     * Default operation: doHandleConnectinoLost
     */
    public void stateChangingFromConnectedToLost() {
        setState(getLostState());
        stateLock.unlock();
        doHandleConnectionLost();
    }

    /**
     * State transition from Connected To Closed
     * Impl: Changes the state and does the operation implied by the transition
     *
     * Default operation: doHandleConnectionClosed
     */
    public void stateChangingFromConnectedToClosed() {
        setState(getClosedState());
        stateLock.unlock();
        doHandleConnectionClosed();
    }

    /**
     * State transition from Closing To Closed
     * Impl: Changes the state and does the operation implied by the transition
     *
     * Default operation: doHandleConnectionClosed
     */
    public void stateChangingFromClosingToClosed() {
        setState(getClosedState());
        stateLock.unlock();
        doHandleConnectionClosed();
    }

    /**
     * @return The ConnectionHandler used as null handler
     */
    protected ConnectionHandler getNullHandler() {
        return NullConnectionHandler.INSTANCE;
    }

    protected void release() {
        clearHandler();
        clearAttributes();
    }



    /**
     * Removes all atributes
     */
    private void clearAttributes() {
        synchronized (attributes) {
            attributes.clear();
        }
    }
}
