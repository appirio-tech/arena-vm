package com.topcoder.client.netClient;

import java.io.IOException;
import java.security.GeneralSecurityException;

import com.topcoder.client.contestant.message.MessageProcessor;
import com.topcoder.client.security.PublicKeyObtainer;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contestantMessages.NetCommonCSHandler;
import com.topcoder.netCommon.contestantMessages.request.BaseRequest;
import com.topcoder.netCommon.contestantMessages.request.KeepAliveRequest;
import com.topcoder.netCommon.io.ClientConnector;
import com.topcoder.netCommon.io.ClientConnectorFactory;
import com.topcoder.netCommon.io.ClientSocket;
import com.topcoder.netCommon.io.InputChannelActivityListener;
import com.topcoder.netCommon.io.ProxiedSocketClientConnector;
import com.topcoder.shared.netCommon.resettabletask.ResettableTaskRunner;
import com.topcoder.shared.netCommon.resettabletask.ResettableTimerTask;

/**
 * Defines the client-side network message layer which manipulates message receiving and sending.
 * 
 * @author Qi Liu
 * @version $Id: Client.java 72046 2008-07-31 06:47:43Z qliu $
 */
public class Client {
    /**
     * Default connection keep-alive timeout
     */
    private static final long KEEPALIVE_TIMEOUT_MS = 30000;

    /**
     * Key of system property used to look for the default input inactivity timeout
     */
    private static final String INACTIVITY_TIMEOUT_KEY = "com.topcoder.input.inactivity.timeout";

    /**
     * Time to wait for inconming bytes during read/skip operation on the input channel
     */
    private static final int INACTIVITY_TIMEOUT = getInactivityTimeout();

    private static final String SSL_PORT_OFFSET_KEY = "com.topcoder.ssl.port.offset";

    private static final int SSL_PORT_OFFSET = resolveValue(SSL_PORT_OFFSET_KEY,
        ContestConstants.APPLET_SSL_PORT_OFFSET);

    private ContestResponseHandler responseHandler;

    /**
     * ResponseWaiterManager responsible for managing all waiters used in synchronous requests.
     */
    private ResponseWaiterManager waiterManager = new ResponseWaiterManager();

    /**
     * Flag indicating if the http tunnel is being used.
     */
    private boolean httpTunnel = false;

    /**
     * TaskRunner used for connection keep-alive tasks
     */
    private ResettableTaskRunner taskRunner;

    /**
     * Task used to send keep-alive messages every a specified time. This task is reset every time a message is sent to
     * server.
     */
    private ResettableTimerTask sendTask;

    /**
     * Task used to monitor incoming activity. This task is reset every time a data is read
     */
    private ResettableTimerTask receiveTask;

    /**
     * Message processor used to notify about lost connections
     */
    private MessageProcessor messageProcessor;

    // private int requestCounter;

    private boolean rhInitialized = false;

    private ClientSocket clientSocket;

    private Object synchRequestMutex = new Object();

    /**
     * Creates a new client which connects to a server using host and port. It uses plain socket.
     * 
     * @param hostName the host of the server.
     * @param portNum the port of the server.
     * @throws IOException if I/O error occurs.
     */
    public Client(String hostName, int portNum) throws IOException {
        this(hostName, portNum, false);
    }

    /**
     * Creates a new client which connects to a server using host and port. It uses either plain socket or SSL socket.
     * 
     * @param hostName the host of the server.
     * @param portNum the port of the server.
     * @param useSSL <code>true</code> if SSL should be used; <code>false</code> otherwise.
     * @throws IOException if I/O error occurs.
     */
    public Client(String hostName, int portNum, boolean useSSL) throws IOException {
        try {
            init(ClientConnectorFactory.createSocketConnector(hostName, portNum, useSSL, SSL_PORT_OFFSET));
        } catch (IOException e) {
            throw e;
        }
    }

    /**
     * Creates a new client which connects to the server via a HTTP CONNECT proxy.
     * 
     * @param hostName the host of the HTTP CONNECT proxy.
     * @param portNum the port of the HTTP CONNECT proxy.
     * @param destinationHost the actual arena server host and port.
     * @throws IOException if I/O error occurs.
     */
    public Client(String hostName, int portNum, String destinationHost) throws IOException {
        // Establish the socket connection to the socket server. Set up the streams
        // that are associated with the resultant socket.
        try {
            init(new ProxiedSocketClientConnector(hostName, portNum, destinationHost));
        } catch (IOException e) {
            // e.printStackTrace();
            throw e;
        }
    }

    /**
     * Creates a new client which connects to the server via a HTTP tunnel. It may use HTTP or or HTTPS to connect the
     * tunnel.
     * 
     * @param tunnelLocation the URL of the HTTP tunnel.
     * @param useSSL <code>true</code> if HTTPS should be used; <code>false</code> otherwise.
     * @throws IOException if I/O error occurs.
     */
    public Client(String tunnelLocation, boolean useSSL) throws IOException {
        try {
            httpTunnel = true;
            init(ClientConnectorFactory.createTunneledConnector(tunnelLocation, useSSL,
                ContestConstants.APPLET_SSL_PORT_OFFSET));
        } catch (IOException e) {
            throw e;
        }
    }

    /**
     * Initializes the response handler, which continously receives and dispatches all received messages.
     * 
     * @param gui the response message processor which the handler dispatches received messages to.
     */
    public synchronized void initContestResponseHandler(MessageProcessor gui) {
        // Client initializes the responseHandler here because we may not want to
        // use the responseHandler just because we have a Client instance. It is
        // set up "on-demand".
        // Only initialize it at most ONE time... we want to give out multiple
        // references to the same responseHandler.

        if (!rhInitialized) {
            this.messageProcessor = gui;
            this.responseHandler = new ContestResponseHandler(this, new NetMessageProcessor(this, waiterManager), gui);
            this.responseHandler.start();
            this.rhInitialized = true;
        }
    }

    /**
     * Sends an asynchronous request message to the server. Once the message object is sent over the network, the method
     * returns immediately.
     * 
     * @param request the request message object to be sent.
     */
    public synchronized void sendRequest(BaseRequest request) {
        try {
            writeObject(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a synchronized request message to the server. The method will not return until a corresponding response
     * message has been received and processed.
     * 
     * @param request the request message object to be sent.
     * @throws RequestTimedOutException if the corresponding response message is not received after timeout.
     */
    public void sendSynchRequest(BaseRequest request) throws RequestTimedOutException {
        synchronized (synchRequestMutex) {
            int requestType = request.getRequestType();
            ResponseToSyncRequestWaiter waiter = waiterManager.registerWaiterFor(requestType);
            try {
                sendRequest(request);
            } catch (RuntimeException e) {
                waiterManager.unregisterWaiterFor(requestType);
                throw e;
            }
            try {
                if (!waiter.block())
                    return;
            } catch (InterruptedException e) {
                // If interrupted something strange happens, report a timeout
            } finally {
                waiterManager.unregisterWaiterFor(requestType);
            }
        }
        throw new RequestTimedOutException("Request timed out " + System.currentTimeMillis());
    }

    /**
     * Closes the network message layer. The underlying socket layer is also closed.
     */
    public void close() {
        // Clean up ...
        try {
            synchronized (this.responseHandler) {
                if (this.responseHandler != null) {
                    this.responseHandler.close();
                }
            }

            if (taskRunner != null && taskRunner.isRunning()) {
                taskRunner.stop();
            }

            waiterManager.unblockAll();

            synchronized (clientSocket) {
                closeSocket();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes the network message layer. An underlying socket layer is given. The keep-alive thread is also
     * initialized to keep the connection alive.
     * 
     * @param connector the underlying socket layer to do the actual communication.
     * @throws IOException if I/O error occurs.
     */
    private void init(ClientConnector connector) throws IOException {
        try {
            clientSocket = new ClientSocket(connector, new NetCommonCSHandler(PublicKeyObtainer.obtainPublicKey()));
        } catch (GeneralSecurityException e) {
            throw (IOException) new IOException("Encryption property invalid.").initCause(e);
        }
        initKeepAliveTasks(KEEPALIVE_TIMEOUT_MS);
    }

    /**
     * Reads a message from the underlying socket.
     * 
     * @return a message object received from the underlying socket.
     * @throws IOException if I/O error occurs.
     * @throws ClassNotFoundException if the message object class cannot be resolved.
     */
    Object readObject() throws IOException, ClassNotFoundException {
        return clientSocket.readObject();
    }

    /**
     * Writes a message to the underlying socket.
     * 
     * @param object the message object to be written.
     * @throws IOException if I/O error occurs.
     */
    private void writeObject(Object object) throws IOException {
        clientSocket.writeObject(object);
        sendTask.reset();
    }

    /**
     * Close the underlying socket.
     * 
     * @throws IOException if I/O error occurs.
     */
    private void closeSocket() throws IOException {
        clientSocket.close();
    }

    /**
     * Update the keep-alive timeout values of the heartbeat processor
     * 
     * @param timeout Keep-alive timeout (in milliseconds) for connection
     * @param httpTimeout Keep-alive timeout (in milliseconds) for connection is http tunnel is being used
     */
    public void updateKeepAliveParameters(long timeout, long httpTimeout) {
        if (sendTask != null) {
            httpTimeout = httpTimeout > timeout ? timeout : httpTimeout;
            sendTask.setWaitTime(httpTunnel ? httpTimeout : timeout);
        }
    }

    /**
     * Initializes tasks used to keep connection alive.
     * 
     * @param keepAliveTimeOut Time in milliseconds to wait for output channel activity before send a keep alive request
     * @param receiveTimeOut Time in milliseconds to wait for input channel activitiy before notify connection as lost.
     */
    private void initKeepAliveTasks(long keepAliveTimeOut) {
        taskRunner = new ResettableTaskRunner();
        sendTask = new ResettableTimerTask(keepAliveTimeOut) {
            protected boolean doAction() {
                try {
                    sendRequest(new KeepAliveRequest());
                    return false;
                } catch (RuntimeException e) {
                    if (messageProcessor != null) {
                        messageProcessor.lostConnection();
                    }
                    return true;
                }
            };
        };
        receiveTask = new ResettableTimerTask(INACTIVITY_TIMEOUT) {
            protected boolean doAction() {
                try {
                    if (messageProcessor != null) {
                        messageProcessor.lostConnection();
                    }
                    return true;
                } catch (RuntimeException e) {
                    return true;
                }
            };
        };

        clientSocket.setInputChannelActivityListener(new InputChannelActivityListener() {
            public void bytesRead(int cntBytes) {
                waiterManager.dataRead();
                receiveTask.reset();
            }
        });
        taskRunner.registerTask("SendKeepAlive", sendTask);
        taskRunner.registerTask("ReceiveMonitor", receiveTask);
        taskRunner.start();
    }

    protected void finalize() throws Throwable {
        /*
         * If this Client is going to be garbage collected its heartbeat must be stopped
         */
        if (taskRunner != null && taskRunner.isRunning()) {
            try {
                taskRunner.stop();
            } catch (RuntimeException e) {
                // Nothing to do
            }
        }
        super.finalize();
    }

    /**
     * Gets the timeout for network inactivity.
     * 
     * @return the inactivity timeout to use.
     */
    public static int getInactivityTimeout() {
        return resolveValue(INACTIVITY_TIMEOUT_KEY, ResponseToSyncRequestWaiter.INPUT_CHANNEL_INACTIVITY_TIMEOUT_MILLIS);
    }

    /**
     * Gets an integer value from the system properties. If the key does not exist or the value is invalid, the default
     * value is returned.
     * 
     * @param key the key of the system property.
     * @param defaultValue the default value when key is missing.
     * @return the integer value from the system properties.
     */
    private static int resolveValue(String key, int defaultValue) {
        String strValue = System.getProperty(key);
        if (strValue != null) {
            try {
                return Integer.parseInt(strValue);
            } catch (Exception e) {
                // use the default value.
            }
        }
        return defaultValue;
    }
}
