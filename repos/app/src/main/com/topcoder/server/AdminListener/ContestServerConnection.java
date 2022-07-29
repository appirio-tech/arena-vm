package com.topcoder.server.AdminListener;

// Some code here copied from com.topcoder.utilities.monitor.model.MonitorServerConnection.

import java.io.IOException;
import java.net.InetAddress;
import java.net.ConnectException;

import com.topcoder.netCommon.io.ClientSocket;
import com.topcoder.server.AdminListener.request.*;
import com.topcoder.server.AdminListener.response.*;
import com.topcoder.shared.util.StoppableThread;
import com.topcoder.server.util.SocketReaderThread;
import com.topcoder.shared.util.logging.Logger;
import com.topcoder.server.listener.monitor.MonitorCSHandler;

//import org.apache.log4j.Category;

final class ContestServerConnection implements SocketReaderThread.Client {

    private InetAddress address = null;
    private int port = 0;
    private AdminProcessor processor;
    private final int id = AdminConstants.CONTEST_LISTENER_CONNECTION_ID;
    private static Logger log = Logger.getLogger(ContestServerConnection.class);

    private ClientSocket clientSocket;
    private SocketReaderThread readerThread;
    private StoppableThread retryThread;
    private boolean connected = false;
//    private Object connectionLock=new Object();

    private final int RECONNECT_DELAY = 5 * 1000;

    ContestServerConnection(AdminProcessor processor, InetAddress address, int port) {
        this.processor = processor;
        clientSocket = null;
        readerThread = null;
        this.address = address;
        this.port = port;
        retryThread = new StoppableThread(retryClient, "Contest Listener Reconnect");
    }

    void start() {
        retryThread.start();
    }

    void stop() {
        try {
            retryThread.stopThread();
        } catch (InterruptedException e) {
            log.error(e);
        }
        disconnect();
    }

    boolean connect() {
//        synchronized (connectionLock) {
        if (connected) {
            return false;
        }
        try {
            clientSocket = new ClientSocket(address, port, new MonitorCSHandler());
            readerThread = new SocketReaderThread(clientSocket, this, id);
            connected = true;
        } catch (ConnectException e) {
            log.debug("Attempt to connect to contest server failed");
            cannotConnect();
            return false;
        } catch (Exception e) {
            // Exception means there's something haywire with the connection.
            log.debug("Attempt to connect to contest server failed: ", e);
            cannotConnect();
            return false;
        }
//        }
        processor.newConnection(id, address.toString());
        return true;
    }

    private void cannotConnect() {
        close();
        connected = false;
    }

    void disconnect() {
//        synchronized (connectionLock) {
        if (!connected) {
            return;
        }
        close();
        connected = false;
//        }
        processor.lostConnection(id);
    }

    InetAddress getContestServerAddress() {
        return address;
    }

    int getContestServerPort() {
        return port;
    }

    boolean isConnected() {
        return connected;
    }

    void writeObject(Object obj) {
        try {
            if (connected) {
                clientSocket.writeObject(obj);
            } else {
                log.error("Message sent to contest server but server is not up");
                if (obj instanceof ClientCommandRequest) {
                    int sender = ((ClientCommandRequest) obj).getSenderId();
                    CommandFailedResponse failure = new CommandFailedResponse("There is no active connection to the contest server");
                    processor.receive(AdminConstants.CONTEST_LISTENER_CONNECTION_ID, new ContestServerResponse(sender, failure));
                }
            }
        } catch (IOException e) {
            log.error("Error writing object out to contest server", e);
            disconnect();
        }
    }

    private void close() {
        try {
            if (clientSocket != null) {
                clientSocket.close();
            }
            clientSocket = null;
        } catch (Exception e) {
            log.error("Error closing client socket", e);
        }
        try {
            if (readerThread != null) {
                readerThread.stop();
            }
            readerThread = null;
        } catch (Exception e) {
            log.error("Error closing reader thread", e);
        }
    }

    // SocketReaderThread.Client interface
    public void receivedObject(int id, Object obj) {
        processor.receive(id, obj);
    }

    // Called when the reader thread is about to die
    public void stop(int id) {
        if (connected) {
            disconnect();
        }
    }

    private StoppableThread.Client retryClient = new StoppableThread.Client() {
        public void cycle() throws InterruptedException {
            boolean success = connect();
            if (success) {
                log.info("Contest listener connection established");
            }
            Thread.sleep(RECONNECT_DELAY);
        }
    };

}
