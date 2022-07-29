/*
 * ClientSocketToConnectionAdapter
 *
 * Created 06/29/2006
 */
package com.topcoder.farm.shared.net.connection.impl.clientsocket;

import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import com.topcoder.farm.shared.net.connection.api.ConnectionHandler;
import com.topcoder.farm.shared.net.connection.impl.AbstractConnection;
import com.topcoder.farm.shared.net.connection.impl.ConnectionKeepAliveMessage;
import com.topcoder.netCommon.io.ClientSocket;
import com.topcoder.netCommon.io.InputChannelActivityListener;
import com.topcoder.shared.netCommon.resettabletask.ResettableTaskRunner;
import com.topcoder.shared.netCommon.resettabletask.ResettableTimerTask;

/**
 * Adapter for ClientSocket to the Connection interface
 *
 * Implementation: Each instance of this class creates a thread
 * that is used to read the ClientSocket. The thread is started as soon
 * as the ConnectionHandler responsible for handling connection events is
 * set.
 * This class does not implement any kind of message buffering, the message is reported as
 * received as soon as it has arrived
 *
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ClientSocketToConnectionAdapter extends AbstractConnection {
    /**
     * Default connection keep-alive timeout
     */
    private static final int KEEPALIVE_TIMEOUT_MS = 15000;

    /**
     * Default INACTIVITY_TIMEOUT  value
     */
    private static final int INACTIVITY_TIMEOUT_MS = 35000;

    /**
     * The socket providing the actual connection
     */
    private ClientSocket socket;

    /**
     * Thread running the <code>socketReader</code> responsible for
     * reading the <code>socket</code>. This thread is started when the
     * handler is set
     */
    private Thread readerThread;

    /**
     * Runnable instance responsible for reading the socket and reporting events
     * on the socket.
     */
    private SocketReaderRunnable socketReader;


   /**
    * Task used to send keep-alive messages every a specified time.
    * This task is reset every time a message is sent to server.
    */
   private ResettableTimerTask sendTask;

   /**
    * Task used to monitor incoming activity.
    * This task is reset every time a data is read
    */
   private ResettableTimerTask receiveTask;

   /**
    * Task runner for keep alive tasks
    */
   private ResettableTaskRunner taskRunner;

   /**
    * Time to wait in ms for bytes available in input channel
    */
   private int inactivityTimeout = INACTIVITY_TIMEOUT_MS;

   /**
    * Time to wait in ms for output activity timeout before sending a keepalive message
    */
   private int keepAliveTimeout = KEEPALIVE_TIMEOUT_MS;

    /**
     * Creates a new ClientSocketToConnectionAdapter for the given socket
     * Note: No event will be reported until a handler had been set for
     * this connection. The socket won't be read until then.
     *
     *  @param socket The socket providing the actual connection
     */
    public ClientSocketToConnectionAdapter(ClientSocket socket) {
        this.socket = socket;
    }

    /**
     * Creates a new ClientSocketToConnectionAdapter for the given socket
     * Events for the connection are inmediatly reported to the handler as
     * soon as they produce.
     *
     * @param socket The socket providing the actual connection
     * @param listener The handler responsible for handing connection events
     */
    public ClientSocketToConnectionAdapter(ClientSocket socket, ConnectionHandler listener) {
        this.socket = socket;
        setHandler(listener);
    }

    /**
     * Sets the handler for the connection.
     * Note: If no handler was set previously, the socket start to be read
     * inmediatly and events for the connection are reported.
     */
    public void setHandler(ConnectionHandler handler) {
        super.setHandler(handler);
        if (handler != null && socketReader == null) {
            startListeningSocket();
        }
    }

    /**
     * Starts listening the socket.
     * Starts a thread running a SocketReaderRunnable instance responsible for
     * reading the socket and reporting the events for the connection.
     */
    private synchronized void startListeningSocket() {
        if (socketReader  == null) {
            socketReader = new SocketReaderRunnable();
            readerThread = new Thread(socketReader, "SocketReader["+this.toString()+"]");
            readerThread.setDaemon(true);
            readerThread.start();
            initKeepAliveTasks();
        }
    }

    /**
     * Sends the message through the socket
     */
    protected void bareSend(Object message) throws IOException {
        sendTask.reset();
        socket.writeObject(message);

    }

    /**
     * Closes the socket and stops the reader thread if it is necessary
     */
    protected void bareClose() {
        try {
            ResettableTaskRunner resettableTaskRunner = taskRunner;
            if (resettableTaskRunner != null) {
                resettableTaskRunner.stop();
            }
            SocketReaderRunnable reader = socketReader;
            if (reader != null) {
                reader.close();
            }
            internalSocketClose();
            if (reader == null) {
                notifyConnectionClosed();
            }
        } catch (Exception e) {
            //Discard exception when trying to close
        }
    }

    private void internalSocketClose() {
        try {
            final ClientSocket clientSocket = socket;
            if (clientSocket != null) {
                clientSocket.close();
            }
        } catch (IOException e) {
            //Ignore close exception
        }
    }

    /**
     * Close the socket and handle the connection as Lost
     */
    void internalHandleConnectionLost() {
        try {
            handleConnectionLost();
        } finally {
            bareClose();
        }
    }

    /**
     * Close the socket and handle the connection as Closed
     */
    void internalHandleConnectionClosed() {
        try {
            socket.close();
        } catch (IOException e) {
            //Discard exception while closing
        }
        handleConnectionClosed();
    }

    void internalHandleConnectionEnd() {
        if (!isClosed()) {
            internalHandleConnectionLost();
        } else {
            internalHandleConnectionClosed();
        }

    }

    public String toString() {
        return "ClientSocketAdapter[" + socket.getLocalEndpoint() + " -> " + socket.getRemoteEndpoint()+ "]";
    }


    /**
     * Initializes tasks used to keep connection alive.
     *
     * @param keepAliveTimeout Time in milliseconds to wait for output channel activity
     *                          before send a keep alive request
     * @param receiveTimeOut Time in milliseconds to wait for input channel activitiy
     *                       before notify connection as lost.
     */
    private void initKeepAliveTasks() {
        taskRunner =  new ResettableTaskRunner();
        sendTask = new ResettableTimerTask(getKeepAliveTimeout()) {
            protected boolean doAction() {
                try {
                    ConnectionKeepAliveMessage message = new ConnectionKeepAliveMessage(System.currentTimeMillis());
                    if (log.isDebugEnabled()) {
                        log.debug("Sending keepalive message: " + message);
                    }
                    bareSend(message);
                    return false;
                } catch (Exception e) {
                    log.error("Exception sending keep alive",e);
                    internalSocketClose();
                    return true;
                }
            };
        };
        receiveTask = new ResettableTimerTask(getInactivityTimeout()) {
            protected boolean doAction() {
                log.info("Closing connection due to inactivity");
                internalSocketClose();
                return true;
            };
        };
        socket.setInputChannelActivityListener(new InputChannelActivityListener() {
            public void bytesRead(int cntBytes) {
                receiveTask.reset();
            }
        });
        taskRunner.registerTask("SendKeepAlive["+this.toString()+"]", sendTask);
        taskRunner.registerTask("InactivityMonitor["+this.toString()+"]", receiveTask);
        taskRunner.start();
    }

    /**
     * @return The inactivity timeout in use by this connection
     */
    public int getInactivityTimeout() {
        return inactivityTimeout;
    }

    /**
     * @return The keep timeout in use by this connection
     */
    public int getKeepAliveTimeout() {
        return keepAliveTimeout;
    }

    /**
     * Sets The inactivity timeout for this connection
     * @param inactivityTimeout the timeout in ms
     */
    public void setInactivityTimeout(int inactivityTimeout) {
        this.inactivityTimeout = inactivityTimeout;
        if (log.isDebugEnabled()) {
            log.debug("Setting inactivity timeout to: "+inactivityTimeout);
        }
        if (receiveTask != null) {
            receiveTask.setWaitTime(inactivityTimeout);
        }
    }

    /**
     * Sets the kepp alive timeout for this connection
     * @param keepAliveTimeout the timeout in ms
     */
    public void setKeepAliveTimeout(int keepAliveTimeout) {
        this.keepAliveTimeout = keepAliveTimeout;
        if (log.isDebugEnabled()) {
            log.debug("Setting keepAlive timeout to: "+keepAliveTimeout);
        }
        if (sendTask != null) {
            sendTask.setWaitTime(keepAliveTimeout);
        }
    }

    public void handleReceived(Object object) {
        if (object instanceof ConnectionKeepAliveMessage) {
            log.debug("KeepAlive received token=" + ((ConnectionKeepAliveMessage) object).getToken());
            return;
        }
        super.handleReceived(object);
    }


    /**
     * Runnable responsible for reading the socket and reporting events
     * for the connection.
     */
    public class SocketReaderRunnable implements Runnable {
        private volatile boolean closed;

        public void run() {
            /*
             * Reads the socket an report the object read using
             * the handleReceived method provide by the abstract class.
             * If an exception is thrown during the reading, it handles the exception
             * and report the connection as lost when needed.
             */
            while (true) {
                try {
                    Object value = socket.readObject();
                    handleReceived(value);
                } catch (SocketTimeoutException e) {
                    if (closed) break;
                } catch (EOFException e) {
                    log.info("Socket closed by peer");
                    break;
                } catch (SocketException e) {
                    log.info("Socket closed? : " + e.getMessage());
                    break;
                } catch (IOException e) {
                    if (e.getMessage() != null && (e.getMessage().indexOf("stream is closed") > -1 ||
                                                    e.getMessage().indexOf("Premature EOF") > -1)) {
                        break;
                    }
                    log.info("Exception captured...continuing",e);
                } catch (Exception e) {
                    log.info("Exception captured...continuing",e);
                }
            }
            //If we exit the loop the connection was lost or closed
            internalHandleConnectionEnd();
        }

        /**
         * Set the closed flag to true.
         */
        public void close() {
            closed = true;
        }
    }
}
