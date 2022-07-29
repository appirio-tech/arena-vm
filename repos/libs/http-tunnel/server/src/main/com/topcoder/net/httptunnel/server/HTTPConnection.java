/*
 * HTTPConnection
 *
 * Created 04/06/2007
 */
package com.topcoder.net.httptunnel.server;

import com.topcoder.shared.util.logging.Logger;

/**
 * HTTPConnection represents an HTTP persistent connection.<p>
 *
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class HTTPConnection {
    private final static Logger log = Logger.getLogger(HTTPConnection.class);
    public static final HTTPConnection NO_CONNECTION = new NullHTTPConnection();
    public static final int STATE_NO_CONNECTED = 0;
    public static final int STATE_WAITING_REQUEST = 0;
    public static final int STATE_MUST_SEND_RESPONSE = 1;
    public static final int STATE_CHUNKED_INPUT = 2;
    public static final int STATE_OUTPUT_OPEN = 4;

    /**
     * The id of this connection
     */
    private Integer id;
    /**
     * The current state of this connection
     */
    private volatile int state;
    
    /**
     * Indicates whether or not this connect should use 
     * chunked output for streaming messages
     */
    private boolean useChunkedOutput;
    
    /**
     * The originating IP of this connection
     */
    private String remoteIP;
    private HTTPListenerProcessor processor;


    @SuppressWarnings("unused")
    private HTTPConnection() {
    }

    /**
     * Creates a new HTTPConnection.
     *
     * @param id The id of the new connection
     * @param remoteIP The originating IP of the new connection
     * @param processor The processor to delegate connection requests
     */
    public HTTPConnection(Integer id, String remoteIP, HTTPListenerProcessor processor) {
        this.id = id;
        this.remoteIP = remoteIP;
        this.processor = processor;
    }

    public Integer getId() {
        return id;
    }

    public int getState() {
        return state;
    }

    public String getRemoteIP() {
        return remoteIP;
    }

    /**
     * Closes the connection.<p>
     *
     * If the connection was in chunked output mode, a final chunk is sent first.<p>
     */
    public void close() {
        if (state == STATE_OUTPUT_OPEN && useChunkedOutput) {
            processor.send(id, HTTPChunkedContent.CLOSING_CHUNK);
        }
        processor.shutdown(id);
    }

    /**
     * Sends an HTTPResponse through this connection
     *
     * @param response The response to send
     *
     * @throws IllegalHTTPStateException If the state of this connections does not allow a response to be sent.
     */
    public void send(HTTPResponse response) throws IllegalHTTPStateException {
        if (log.isDebugEnabled()) {
            log.debug("{"+id+"} sending: "+response);
        }
        updateWithOutput(response, false);
        processor.send(id, response);
    }

    /**
     * Sends an HTTPResponse and opens a chunked output channel to allow sending many objects
     * into this response.
     *
     * @param response The response to send. The response object is modified by this method.
     *
     * @throws IllegalHTTPStateException If the state of this connections does not allow a response to be sent.
     */
    public void sendAndOpenOutputStream(HTTPResponse response) throws IllegalHTTPStateException {
        updateWithOutput(response, true);
        if (useChunkedOutput) {
            HTTPHelper.setChunked(response);
        }  else {
            response.setHeader(HTTPConstants.HEADER_CONNECTION, HTTPConstants.HEADER_CONNECTION_CLOSE);
        }
        if (log.isDebugEnabled()) {
            log.debug("{"+id+"} sending and opening stream: "+response);
        }
        processor.send(id, response);
    }

    /**
     * Sends an object using a previously opened chunked output channel.
     *
     * @param object The object to send.
     *
     * @throws IllegalHTTPStateException If the state of this connections does not allow a response to be sent.
     */
    public void sendToOutputStream(Object object) throws IllegalHTTPStateException {
        updateWithOutputCustom(object);
        if (log.isDebugEnabled()) {
            log.debug("{"+id+"} sending to output stream: "+object);
        }
        processor.send(id, object);
    }


    /**
     * This method is called by the processor when a message arrives through this connection.<p>
     * If calls the proper method for notifying teh incoming message and validates the current connection
     * status allows the kind of objecct received.
     *
     * @param request The object received.
     * @throws IllegalHTTPStateException If the current state does not allows the incoming object
     */
    public void handleReceive(Object request) throws IllegalHTTPStateException {
        if (log.isDebugEnabled()) {
            log.debug("{"+id+"} received:"+request);
        }
        if (request instanceof HTTPRequest) {
            HTTPRequest req = (HTTPRequest)request;
            updateWithInput(req);
            processor.notifyProcessIncoming(this, req);
        } else if (request instanceof HTTPChunkedContent) {
            HTTPChunkedContent req = (HTTPChunkedContent)request;
            updateWithInput(req);
            processor.notifyProcessIncoming(this, req);
        }
    }

    /**
     * This method is called by the processor when the connection has been lost.
     * It propagates the notification if required.
     *
     * @throws IllegalHTTPStateException If the current state does not allows the incoming object
     */
    public void handleConnectionLost() {
        if (state != STATE_WAITING_REQUEST) {
            processor.notifyConnectionLost(this);
        }
    }



    /*
     * Status verification and update methods
     */
    private void updateWithInput(HTTPRequest request) throws IllegalHTTPStateException {
        if (state != STATE_WAITING_REQUEST) {
            //We don't support pipeline for now, so assert this
            throw new IllegalHTTPStateException("Can't accept requests in this state");
        }
        useChunkedOutput = HTTPConstants.HTTP_VERSION_1_1.equals(request.getHttpVersion());
        if (HTTPHelper.isChunked(request)) {
            state = STATE_CHUNKED_INPUT;
        } else {
            state = STATE_MUST_SEND_RESPONSE;
        }
    }

    private void updateWithInput(HTTPChunkedContent request) throws IllegalHTTPStateException {
        if (state != STATE_CHUNKED_INPUT) {
            //We don't support pipeline for now, so assert this
            throw new IllegalHTTPStateException("Can't accept chunks in this state");
        }
        if (HTTPChunkedContent.CLOSING_CHUNK == request) {
            state = STATE_MUST_SEND_RESPONSE;
        }
    }


    private void updateWithOutput(HTTPResponse response, boolean opening) throws IllegalHTTPStateException {
        if (state != STATE_MUST_SEND_RESPONSE) {
            //We don't support pipeline for now, so assert this
            throw new IllegalHTTPStateException("Can't send a response in this state");
        }
        if (opening || HTTPHelper.isChunked(response)) {
            response.setOpeningOutputChannel(opening);
            state = STATE_OUTPUT_OPEN;
        } else {
            state = STATE_WAITING_REQUEST;
        }
    }

    private void updateWithOutputCustom(Object response) throws IllegalHTTPStateException {
        if (response instanceof HTTPResponse || response instanceof HTTPChunkedContent) {
            throw new IllegalHTTPStateException("Can't send an HTTPResponse/HTTPChunkedContent as custom object");
        }
        if (state != STATE_OUTPUT_OPEN) {
            //We don't support pipeline for now, so assert this
            throw new IllegalHTTPStateException("Can't send a custom object in this state");
        }
    }

    protected void setState(int state) {
        this.state = state;
    }

    /**
     * Null pattern implementation for HTTPConnection.<p>
     * Throws exception when calling action methods.
     */
    private static final class NullHTTPConnection extends HTTPConnection {
        public NullHTTPConnection() {
            super(new Integer(Integer.MIN_VALUE), "", null);
            setState(STATE_NO_CONNECTED);
        }
        public void sendToOutputStream(Object object) throws IllegalHTTPStateException {
            throw new IllegalHTTPStateException("No connected");
        }
        public void sendAndOpenOutputStream(HTTPResponse response) throws IllegalHTTPStateException {
            throw new IllegalHTTPStateException("No connected");
        }
        public void send(HTTPResponse response) throws IllegalHTTPStateException {
            throw new IllegalHTTPStateException("No connected");
        }
        public void handleReceive(Object request) throws IllegalHTTPStateException {
            throw new IllegalHTTPStateException("No connected");
        }
        public void handleConnectionLost() {
        }
        public int getState() {
            return STATE_NO_CONNECTED;
        }
    }

}
