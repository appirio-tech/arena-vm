package com.topcoder.server.listener;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.topcoder.shared.netCommon.CSHandlerFactory;
import com.topcoder.server.listener.monitor.MonitorInterface;
import com.topcoder.server.listener.nio.channels.SocketChannel;
import com.topcoder.server.listener.util.SerialIntegerGenerator;
import com.topcoder.shared.util.logging.Logger;

/**
 * The default listener implementation that uses non-blocking I/O.
 *
 * @author  Timur Zambalayev
 */
public final class NBIOListener implements ListenerInterface {

    private static final String CLASS_NAME = "NBIOListener.";

    private final SerialIntegerGenerator NUMBER_GENERATOR ;
    private final Logger cat;
    private final ProcessorInterface processor;
    private final AcceptHandler acceptHandler;
    private final RequestHandler requestHandler;
    private final ResponseHandler responseHandler;
    private final MonitorInterface monitor;

    /**
     * Creates a new listener instance.
     *
     * @param   port                the port number.
     * @param   processor           the processor.
     * @param   numAcceptThreads    the number of accept threads.
     * @param   numReadThreads      the number of read threads.
     * @param   numWriteThreads     the number of write threads.
     * @param   monitor             the monitor.
     * @param   csHandlerFactory    the CS handler factory.
     * @param   ips                 set of IPs, allowed (isAllowedSet==true) or banned (isAllowedSet==false).
     * @param   isAllowedSet        indicates if ips is an allowed IPs set or a banned IPs set.
     */
    public NBIOListener(int port, ProcessorInterface processor, int numAcceptThreads, int numReadThreads,
            int numWriteThreads, MonitorInterface monitor, CSHandlerFactory csHandlerFactory, Collection ips,
            boolean isAllowedSet) {

        this(null, port, processor, numAcceptThreads, numReadThreads, numWriteThreads, monitor, csHandlerFactory, ips,
                isAllowedSet, 0, Integer.MAX_VALUE);
    }


    /**
     * Creates a new listener instance.
     *
     * @param   port                the port number.
     * @param   processor           the processor.
     * @param   numAcceptThreads    the number of accept threads.
     * @param   numReadThreads      the number of read threads.
     * @param   numWriteThreads     the number of write threads.
     * @param   monitor             the monitor.
     * @param   csHandlerFactory    the CS handler factory.
     * @param   ips                 set of IPs, allowed (isAllowedSet==true) or banned (isAllowedSet==false).
     * @param   isAllowedSet        indicates if ips is an allowed IPs set or a banned IPs set.
     * @param   minConnectionId     Mininum connection id to generate by this listener
     * @param   maxConnectionId     maximum connection id to generate by this listener
     */
    public NBIOListener(String ipAddress, int port, ProcessorInterface processor, int numAcceptThreads, int numReadThreads,
            int numWriteThreads, MonitorInterface monitor, CSHandlerFactory csHandlerFactory, Collection ips,
            boolean isAllowedSet, int minConnectionId, int maxConnectionId) {
        this(ipAddress, port, processor, numAcceptThreads, numReadThreads, numWriteThreads, monitor,
                new DefaultReaderWriterFactory(csHandlerFactory), ips, isAllowedSet, minConnectionId, maxConnectionId);
    }


    /**
     * Creates a new listener instance.
     *
     * @param   port                the port number.
     * @param   processor           the processor.
     * @param   numAcceptThreads    the number of accept threads.
     * @param   numReadThreads      the number of read threads.
     * @param   numWriteThreads     the number of write threads.
     * @param   monitor             the monitor.
     * @param   readerWriterFactory the ReaderWriter factory to use for serializing/deserializing messages
     * @param   ips                 set of IPs, allowed (isAllowedSet==true) or banned (isAllowedSet==false).
     * @param   isAllowedSet        indicates if ips is an allowed IPs set or a banned IPs set.
     * @param   minConnectionId     Mininum connection id to generate by this listener
     * @param   maxConnectionId     maximum connection id to generate by this listener
     */
    public NBIOListener(String ipAddress, int port, ProcessorInterface processor, int numAcceptThreads, int numReadThreads,
            int numWriteThreads, MonitorInterface monitor, ReaderWriterFactory readerWriterFactory,  Collection ips,
            boolean isAllowedSet, int minConnectionId, int maxConnectionId) {

        NUMBER_GENERATOR = new SerialIntegerGenerator(minConnectionId, maxConnectionId);
        this.processor = processor;
        this.monitor = monitor;
        cat = Logger.getLogger(ListenerConstants.PACKAGE_NAME + CLASS_NAME + port);
        HandlerClientImpl handlerClientImpl = new HandlerClientImpl(this);
        acceptHandler = new AcceptHandler(ipAddress, port, handlerClientImpl, numAcceptThreads, ips, isAllowedSet);
        requestHandler = new RequestHandler(port, handlerClientImpl, numReadThreads, monitor, readerWriterFactory);
        responseHandler = new ResponseHandler(port, numWriteThreads, handlerClientImpl, readerWriterFactory);
        processor.setListener(this);
    }

    public int getConnectionsSize() {
        return responseHandler.getConnectionsSize();
    }

    public int getResponseQueueSize() {
        return responseHandler.getQueueSize();
    }

    public int getInTrafficSize() {
        return requestHandler.getTrafficSize();
    }

    public int getOutTrafficSize() {
        return responseHandler.getTrafficSize();
    }

    public void start() throws IOException {
        info("starting");
        responseHandler.start();
        try {
            processor.start();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        requestHandler.start();
        acceptHandler.start();
        info("started");
    }

    private void receiveRequest(int connection_id, Object request) {
        processor.receive(connection_id, request);
    }

    public void send(int connection_id, Object response) {
        Integer id = NUMBER_GENERATOR.getInteger(connection_id);
        if (id == null) {
            return;
        }
        if(!responseHandler.writeObject(id, response)) {
            debug("ERROR WRITING");
        }
    }

    public void shutdown(int connection_id) {
        shutdown(connection_id, false);
    }

    public void shutdown(int connection_id, boolean notifyProcessor) {
        //Integer id = NUMBER_GENERATOR.getInteger(connection_id);
        //if (id == null) {
//            return;
        //}
        closeConnection(new Integer(connection_id), notifyProcessor, false);
    }

    private void closeAllConnections() {
        Collection keySet = new ArrayList(responseHandler.keySet());
        info("closing " + keySet.size() + " readConnections");
        for (Iterator it = keySet.iterator(); it.hasNext();) {
            closeConnection((Integer) it.next());
        }
    }

    public void stop() {
        info("stopping");
        acceptHandler.stop();
        requestHandler.stop();
        processor.stop();
        responseHandler.stop();
        closeAllConnections();
        int queueSize = responseHandler.getQueueSize();
        int waitSetSize = responseHandler.getWaitSetSize();
        if (queueSize != 0 || waitSetSize != 0) {
            throw new RuntimeException("assertion failed! queueSize=" + queueSize + ", waitSetSize=" + waitSetSize);
        }
        info("stopped");
    }

    private void acceptNewSocket(SocketChannel socketChannel) {
        Integer id = NUMBER_GENERATOR.nextNewInteger();
        String remoteIP = AcceptHandler.getRemoteAddress(socketChannel);
        debug("new connection, id=" + id + ", remoteIP=" + remoteIP);
        int idInt = id.intValue();
        monitor.newConnection(idInt, remoteIP);
        responseHandler.register(id, socketChannel);
        processor.newConnection(idInt, remoteIP);
        requestHandler.register(id, socketChannel);
    }

    private void closeConnection(Integer id) {
        closeConnection(id, true, false);
    }

    private void closeConnection(Integer id, boolean lost) {
        if(lost)
            closeConnection(id, false, true); //don't notify processor, very important
        else
            closeConnection(id, true, false);
    }

    private void closeConnection(Integer id, boolean notify, boolean lost) {
        if (cat.isDebugEnabled()) {
            cat.debug("close connection id=" + id + " notify=" + notify + " lost=" + lost);
        }
        int idInt = id.intValue();
        SocketChannel socketChannel = responseHandler.remove(id);
        if (socketChannel != null) {
            try {
                try {
                    Socket socket = socketChannel.socket();
                    // Temporary fix for bug in Java nio!? Closing a channel SHOULD close the socket correctly!?
                    //if (!socket.isOutputShutdown()) { // Always returns true? bug?
                    try {
                        socket.shutdownOutput();
                        debug("output shutdown");
                    } catch (IOException e) {
                        debug("IOException on socket.shutdownOutput(): " + e);
                        // No worries, since this just tries to fix the nio-bug? / Peter
                    }
                    socket.close();
                } finally {
                    socketChannel.close();
                }
            } catch (IOException e) {
                error("socketChannel.close()", e);
            }

            debug("closed connection, id: " + id);
            if (notify) {
                processor.lostConnection(idInt);
            }
            monitor.lostConnection(idInt);
        }

        NUMBER_GENERATOR.removeInteger(id);
        if(lost) {
            processor.lostConnectionTemporarily(idInt);
        }

    }

    public void banIP(String ipAddress) {
        acceptHandler.banIP(ipAddress);
    }

    public int getMaxConnectionId() {
        return NUMBER_GENERATOR.getMaxValue();
    }

    public int getMinConnectionId() {
        return NUMBER_GENERATOR.getMinValue();
    }

    public void banIPwithExpiry(String ipAddress, long expiresAt)
    {
        acceptHandler.banIPwithExpiry(ipAddress, expiresAt);
    }

    private void debug(String message) {
        cat.debug(message);
    }

    private void info(String message) {
        cat.info(message);
    }

    private void error(String message, Throwable t) {
        cat.error(message, t);
    }

    private static final class HandlerClientImpl implements HandlerClient {

        private final NBIOListener listener;

        private HandlerClientImpl(NBIOListener listener) {
            this.listener = listener;
        }

        public void acceptNewSocket(SocketChannel socketChannel) {
            listener.acceptNewSocket(socketChannel);
        }

        public void closeConnection(Integer id) {
            listener.closeConnection(id);
        }

        public void closeConnection(Integer id, boolean lost) {
            listener.closeConnection(id, lost);
        }

        public void receiveRequest(int connection_id, Object request) {
            listener.receiveRequest(connection_id, request);
        }

        public void banIPwithExpiry(String ipAddress, long expiresAt)
        {
            listener.banIPwithExpiry(ipAddress, expiresAt);
        }

    }
}
