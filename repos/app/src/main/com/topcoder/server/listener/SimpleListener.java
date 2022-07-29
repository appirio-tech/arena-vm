package com.topcoder.server.listener;

import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.topcoder.netCommon.contestantMessages.NetCommonSocketFactory;
import com.topcoder.netCommon.io.ClientSocket;
import com.topcoder.server.listener.monitor.MonitorInterface;
import com.topcoder.server.listener.util.SerialIntegerGenerator;
import com.topcoder.shared.util.StoppableThread;

/**
 * A simple threaded listener implementation. It should be used only for testing.
 *
 * @author  Timur Zambalayev
 */
public final class SimpleListener implements ListenerInterface, StoppableThread.Client {

    private final SerialIntegerGenerator NUMBER_FACTORY ;

    private final StoppableThread backgroundThread = new StoppableThread(this, "SimpleListener");
    private final Map connMap = Collections.synchronizedMap(new HashMap());
    private final ProcessorInterface processor;
    private final int port;
    private final MonitorInterface monitor;

    private ServerSocket serverSocket;

    /**
     * Creates a new instance of this class.
     *
     * @param   port            the port number.
     * @param   processor       the processor.
     * @param   monitor         the monitor.
     */
    public SimpleListener(int port, ProcessorInterface processor, MonitorInterface monitor, int minConnectionId, int maxConnectionId) {
        NUMBER_FACTORY = new SerialIntegerGenerator(minConnectionId, maxConnectionId);
        this.port = port;
        this.processor = processor;
        this.monitor = monitor;
        processor.setListener(this);
    }

    public int getConnectionsSize() {
        return connMap.size();
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

    public void start() throws IOException {
        info("starting, port=" + port + ", processor=" + processor);
        try {
            processor.start();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        serverSocket = new ServerSocket(port);
        info("created server socket");
        backgroundThread.start();
    }

    public void stop() {
        info("stopping");
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            backgroundThread.stopThread();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        processor.stop();
        closeAllConnections();
        info("stopped");
    }

    public void cycle() throws InterruptedException {
        ClientSocket socket;
        String remoteIP;
        try {
            Socket socketImpl = serverSocket.accept();
            remoteIP = socketImpl.getInetAddress().getHostAddress();
            socket = NetCommonSocketFactory.newClientSocket(socketImpl);
        } catch (SocketException e) {
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Integer id = NUMBER_FACTORY.nextNewInteger();
        info("accepted socket");
        Handler handler = new Handler(socket, id, processor, this, monitor);
        connMap.put(id, handler);
        int idInt = id.intValue();
        monitor.newConnection(idInt, remoteIP);
        processor.newConnection(idInt, remoteIP);
        (new Thread(handler)).start();
    }

    public void send(int connection_id, Object response) {
        Integer i = new Integer(connection_id);
        Handler handler = (Handler) connMap.get(i);
        handler.process(response);
    }

    private void closeAllConnections() {
        Collection keySet = new ArrayList(connMap.keySet());
        info("closing " + keySet.size() + " connections");
        for (Iterator it = keySet.iterator(); it.hasNext();) {
            closeConnection((Integer) it.next(), true);
        }
    }

    private void closeConnection(Integer id, boolean notify) {
        Handler handler = (Handler) connMap.get(id);
        System.out.println("closeConnection: " + handler + " " + connMap);
        monitor.lostConnection(id.intValue());
        handler.close(notify);
        connMap.remove(id);
    }

    public void shutdown(int connection_id) {
        shutdown(connection_id, false);
    }

    public void shutdown(int connection_id, boolean notifyProcessor) {
        closeConnection(new Integer(connection_id), notifyProcessor);
    }

    public void banIP(String ipAddress) {
    }


    public int getMinConnectionId() {
        return NUMBER_FACTORY.getMinValue();
    }

    public int getMaxConnectionId() {
        return NUMBER_FACTORY.getMaxValue();
    }

    private static void info(String msg) {
        System.out.println(msg);
    }

    private static class Handler implements Runnable {

        private final ClientSocket socket;
        private final Integer id;
        private final ProcessorInterface processor;
        private final SimpleListener listener;
        private final MonitorInterface monitor;

        private boolean running = true;

        private Handler(ClientSocket socket, Integer id, ProcessorInterface processor, SimpleListener listener,
                MonitorInterface monitor) {
            this.socket = socket;
            this.id = id;
            this.processor = processor;
            this.listener = listener;
            this.monitor = monitor;
        }

        private void process(Object object) {
            if (!running) {
                return;
            }
            try {
                socket.writeObject(object);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            try {
                while (running) {
                    Object object = socket.readObject();
                    monitor.bytesRead(id.intValue(), 123);
                    processor.receive(id.intValue(), object);
                }
            } catch (EOFException e) {
            } catch (SocketException e) {
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                listener.closeConnection(id, true);
            }
        }

        private void close(boolean notify) {
            running = false;
            if (notify) {
                processor.lostConnection(id.intValue());
                try {
                    socket.close();
                    System.out.println("closed socket");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
