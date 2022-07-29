/*
 * HTTPTunnelListener
 *
 * Created 04/03/2007
 */
package com.topcoder.net.httptunnel.server;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import com.topcoder.net.httptunnel.common.digest.TokenDigester;
import com.topcoder.server.listener.ListenerInterface;
import com.topcoder.server.listener.NBIOListener;
import com.topcoder.server.listener.ProcessorInterface;
import com.topcoder.server.listener.monitor.MonitorInterface;
import com.topcoder.shared.netCommon.CSHandlerFactory;
import com.topcoder.shared.util.logging.Logger;


/**
 * HTTPTunnelListener class is the server-side main-class of the Tunnel Component.<p>
 *
 * This class is responsible for encapsulation of HTTP connections, request, responses. It
 * presents tunnel connections as ordinary TCP connections.<p>
 *
 * Clients that want to connect to the TunnelListener must use the
 * {@link com.topcoder.net.httptunnel.client.HTTPTunnelClientConnector HTTPTunnelClientConnector} class.<p>
 *
 * The HTTP support is highly limited and the HTTP flow is fixed.<p>
 *
 *
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class HTTPTunnelListener implements ListenerInterface, HTTPListenerProcessor.HTTPHandler {
    private final Logger log = Logger.getLogger(HTTPTunnelListener.class);

    /**
     * digestGenerator used to validate digest
     */
    private final TokenDigester digestGenerator = new TokenDigester();
    /**
     * Tunnel Connections map to access them using tunnelId/outputConnectionId
     */
    private final Map outputConns = new ConcurrentHashMap();
    /**
     * Tunnel Connections map to access them using inputConnectionId,
     * inputConns remains in the map while a chunked data is being received
     */
    private final Map inputConns = new ConcurrentHashMap();

    /**
     * Listener for TCP socket handling
     */
    private final NBIOListener socketListener;
    /**
     * The processor to use for event notifications.
     */
    private final ProcessorInterface processor;
    /**
     * A random number generator, for token generation
     */
    private final Random random = new Random();
    /**
     * The monitor interface to notify monitor events
     */
    private final MonitorInterface monitor;

    /**
     * The max difference allowed between server time estimated by the client
     * and the current server time in seconds.
     */
    private int maxTimeDiffAllowed = 60;

    /**
     * Creates a new HTTPTunnelListener
     *
     * @param ipAddress The ipAddress which this listener must bind to
     * @param port The port which this listener must bind to
     * @param processor The processor where connection events must be notified,
     * @param numAcceptThreads The number of accept threads to use.
     * @param numReadThreads The number of read threads to use.
     * @param numWriteThreads The number of write threads to use.
     * @param monitor The monitor where other kind of connection events must notified
     * @param csHandlerFactory The factory for CustomSerializable objects serialization
     * @param ips The ips set
     * @param isAllowedSet If the ip set is the allowed set or a not allowed set
     * @param minConnectionId The minConnection id to use when notifying events
     * @param maxConnectionId The maxConnection id to use when notifying events
     */
    public HTTPTunnelListener(String ipAddress, int port, ProcessorInterface processor, int numAcceptThreads, int numReadThreads,
            int numWriteThreads, MonitorInterface monitor, CSHandlerFactory csHandlerFactory, Collection ips,
            boolean isAllowedSet, int minConnectionId, int maxConnectionId) {
        this.processor = processor;
        this.monitor = monitor;
        this.socketListener = new NBIOListener(ipAddress, port, new HTTPListenerProcessor(this), numAcceptThreads, numReadThreads, numWriteThreads,
                buildFilterMonitor(), new HTTPTunnelReaderWriterFactory(csHandlerFactory), ips, isAllowedSet, minConnectionId, maxConnectionId);
    }


    /*
     * ListenerInterface methods
     */
    public void start() throws IOException {
        processor.start();
        socketListener.start();
    }

    public void stop() {
        socketListener.stop();
        processor.stop();
    }

    public void banIP(String ipAddress) {
        socketListener.banIP(ipAddress);
    }

    public int getInTrafficSize() {
        return socketListener.getInTrafficSize();
    }

    public int getMaxConnectionId() {
        return socketListener.getMaxConnectionId();
    }

    public int getMinConnectionId() {
        return socketListener.getMinConnectionId();
    }

    public int getOutTrafficSize() {
        return socketListener.getOutTrafficSize();
    }

    public int getResponseQueueSize() {
        return socketListener.getResponseQueueSize();
    }

    public int getConnectionsSize() {
        return outputConns.size();
    }

    public void send(int connection_id, Object response) {
        TunnelConnection conn = (TunnelConnection) outputConns.get(new Integer(connection_id));
        if (conn != null) {
            sendToOutputStream(conn.getPersistentConnection(), response);
        }
    }

    public void shutdown(int connection_id, boolean notifyProcessor) {
        internalShutdown(new Integer(connection_id), notifyProcessor);
    }

    public void shutdown(int connection_id) {
        internalShutdown(new Integer(connection_id), false);
    }


    /*
     *HTTPHandler methods
     */
    public void processIncoming(HTTPConnection conn, HTTPRequest req) {
        if (req.getMethod() == HTTPConstants.REQ_METHOD_HEAD) {
            sendOkResponseToInput(conn);
            return;
        }
        if (req.getMethod() != HTTPConstants.REQ_METHOD_GET &&
            req.getMethod() != HTTPConstants.REQ_METHOD_POST) {
            sendUnsupportedMethodResponse(conn);
            return;
        }
        String id = req.getParameter("id");
        if (id == null) {
            handleNewOutputConnection(conn, req);
        } else {
            handleNewInputConnection(conn, req);
        }
    }

    public void processIncoming(HTTPConnection conn, HTTPChunkedContent request) {
        TunnelConnection tunnel = getAssociatedTunnelForInput(conn);
        if (tunnel != null) {
            if (request != HTTPChunkedContent.CLOSING_CHUNK) {
                processContent(tunnel.getId(), request.getContent());
            } else {
                dissociateInputFromTunnel(conn, false);
                sendOkResponseToInput(conn);
            }
        } else {
            log.warn("Invalid request received in connection: "+ conn.getId()+", unexpected chunked content received");
            closeAndNotifyAsLost(conn);
        }
    }

    public void processConnectionLost(HTTPConnection conn) {
        if (processConnectionEnded(conn)) {
            notifyAsLost(conn);
        }
    }

    /*
     * Helper methods
     */
    private void notifyAsLost(HTTPConnection conn) {
        processor.lostConnectionTemporarily(conn.getId().intValue());
    }

    private void processContent(Integer outputId, Object content) {
        if (content != null) {
            processor.receive(outputId.intValue(), content);
        }
    }

    private void handleNewOutputConnection(HTTPConnection httpConn, HTTPRequest req) {
        TunnelConnection tunnel = new TunnelConnection(httpConn, generateTCToken(httpConn.getId()));
        outputConns.put(httpConn.getId(), tunnel);
        processor.newConnection(tunnel.getId().intValue(), tunnel.getRemoteIP());
        try {
            intializeOutputConnection(httpConn, tunnel, HTTPConstants.RESPONSE_OK, System.currentTimeMillis());
        } catch (Exception e) {
            log.warn("Unexpected exception opening out connection."+ httpConn.getId(), e);
            closeAndNotifyAsLost(httpConn);
        }
    }

    private void handleNewInputConnection(HTTPConnection httpConn, HTTPRequest req) {
        int outputId = Integer.parseInt(req.getParameter("id"));
        TunnelConnection tunnel = (TunnelConnection) outputConns.get(new Integer(outputId));
        if (tunnel != null) {
            //we must check the token.
            if (isValidConnectionRequest(req, tunnel)) {
                //Ok we accept the connection
                monitor.associateConnections(tunnel.getId().intValue(), httpConn.getId().intValue());
                processContent(tunnel.getId(), req.getContent());
                if (!HTTPHelper.isChunked(req)) {
                    sendOkResponseToInput(httpConn);
                } else {
                    associateInputToTunnel(tunnel, httpConn);
                }
            } else {
                log.warn("Illegal token received from connection: "+httpConn.getId());
                httpConn.close();
            }
        } else {
            log.info("Output connection was already closed. "+httpConn.getId());
            sendErrorResponseToInput(httpConn);
        }

    }


    private boolean isValidConnectionRequest(HTTPRequest req, TunnelConnection tunnel) {
        String digest = req.getHeader(HTTPConstants.HEADER_TC_DIGEST);
        String currentTS = req.getHeader(HTTPConstants.HEADER_TC_TS);
        if (digestGenerator.isValidDigest(tunnel.getToken(), tunnel.getId().intValue(), currentTS, digest)) {
            long currentServerTS = System.currentTimeMillis();
            if (Math.abs(currentServerTS-Long.parseLong(currentTS)) > maxTimeDiffAllowed*1000) {
                log.warn("Time sent by client differs in more than "+maxTimeDiffAllowed+" seconds.");
            }
            return true;
        }
        return false;
    }

    private void sendOkResponseToInput(HTTPConnection httpConn) {
        HTTPResponse response = new HTTPResponse(HTTPConstants.RESPONSE_OK, new Date());
        response.setHeader(HTTPConstants.HEADER_CONNECTION, HTTPConstants.HEADER_CONNECTION_KEEP_ALIVE);
        sendResponse(httpConn, response);
    }

    private void sendErrorResponseToInput(HTTPConnection httpConn) {
        HTTPResponse response = new HTTPResponse(HTTPConstants.RESPONSE_SERVER_ERROR, new Date());
        sendResponse(httpConn, response);
    }

    private void sendUnsupportedMethodResponse(HTTPConnection httpConn) {
        HTTPResponse response = new HTTPResponse(HTTPConstants.RESPONSE_METHOD_NOT_ALLOWED, new Date());
        sendResponse(httpConn, response);
    }

    private void sendResponse(HTTPConnection conn, HTTPResponse response) {
        try {
            conn.send(response);
        } catch (IllegalHTTPStateException e) {
            log.error("Unexpected exception while sending message to connection: "+conn);
            conn.close();
        } finally {
            processConnectionEnded(conn);
        }
    }

    private void intializeOutputConnection(HTTPConnection httpConn, TunnelConnection tunnel, int responseCode, long initialServerTS) throws IllegalHTTPStateException {
        HTTPResponse response = new HTTPResponse(HTTPConstants.RESPONSE_OK, new Date());
        response.setHeader(HTTPConstants.HEADER_TC_TOKEN, tunnel.getToken());
        response.setHeader(HTTPConstants.HEADER_TC_TUNNELID, String.valueOf(httpConn.getId()));
        response.setHeader(HTTPConstants.HEADER_TC_TS, String.valueOf(initialServerTS));
        httpConn.sendAndOpenOutputStream(response);
    }

    private void sendToOutputStream(HTTPConnection httpConn, Object response) {
        try {
            httpConn.sendToOutputStream(response);
        } catch (Exception e) {
            log.error("Unexpected exception while sending message to connection: "+httpConn);
            closeAndNotifyAsLost(httpConn);
        }
    }

    private void closeAndNotifyAsLost(HTTPConnection httpConn) {
        httpConn.close();
        if (processConnectionEnded(httpConn)) {
            notifyAsLost(httpConn);
        }
    }

    private void internalShutdown(Integer cnnId, boolean notifyProcessor) {
        TunnelConnection tunnel = (TunnelConnection) outputConns.get(cnnId);
        if (tunnel != null) {
            tunnel.getPersistentConnection().close();
            processConnectionEnded(tunnel.getPersistentConnection());
            if (notifyProcessor) {
                processor.lostConnection(cnnId.intValue());
            }
        }
    }


    private String generateTCToken(Integer id) {
        synchronized (random) {
            return String.valueOf(random.nextLong());
        }
    }

    /**
     * Process connection as Closed/Lost.
     *
     * @param conn The connection closed/lost
     * @return true if it was an output connection
     */
    private boolean processConnectionEnded(HTTPConnection httpConn) {
        TunnelConnection tunnel = (TunnelConnection) outputConns.remove(httpConn.getId());
        if (tunnel != null) {
            log.debug("Was an output connection. Dropping connection and notifying");
            HTTPConnection inputConnection = tunnel.getInputConnection();
            if (inputConnection != HTTPConnection.NO_CONNECTION) {
                if (log.isDebugEnabled()) log.debug("Dropping associated input connection: "+ tunnel.getInputId());
                dissociateInputFromTunnel(inputConnection, true);
            }
            return true;
        } else {
            if (dissociateInputFromTunnel(httpConn, false)) {
                log.debug("Was an input connection. Nothing to do");
            }
            return false;
        }
    }

    private TunnelConnection getAssociatedTunnelForInput(HTTPConnection httpConn) {
        return (TunnelConnection) inputConns.get(httpConn.getId());
    }

    private void associateInputToTunnel(TunnelConnection tunnel, HTTPConnection httpConn) {
        if (log.isDebugEnabled()) {
            log.debug("Associating connection "+httpConn.getId()+" with tunnel"+tunnel.getId());
        }
        inputConns.put(httpConn.getId(), tunnel);
        tunnel.setInputConnection(httpConn);
    }

    private boolean dissociateInputFromTunnel(HTTPConnection httpConn, boolean mustShutdown) {
        TunnelConnection tunnel = (TunnelConnection) inputConns.remove(httpConn.getId());
        if (mustShutdown) {
            httpConn.close();
        }
        if (tunnel != null) {
            if (log.isDebugEnabled()) {
                log.debug("Dissociating connection "+httpConn.getId()+"  tunnel"+tunnel.getId());
            }
            tunnel.setInputConnection(HTTPConnection.NO_CONNECTION);
            return true;
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Dissociating connection "+httpConn.getId()+" was called but it was not associated");
            }
            return false;
        }
    }

    private MonitorInterface buildFilterMonitor() {
        return new MonitorInterface(){
            public void newConnection(int id, String remoteIP) {
                monitor.newConnection(id, remoteIP);
            }

            public void lostConnection(int id) {
                monitor.lostConnection(id);
            }

            public void bytesRead(int id, int numBytes) {
                monitor.bytesRead(id, numBytes);
            }

            public void associateConnections(int existentConnectionId, int newConnectionID) {
                throw new IllegalStateException("This should never happen");
            }
        };
    }
}