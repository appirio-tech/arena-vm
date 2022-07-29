package com.topcoder.server.util;

//import java.io.IOException;
import java.io.EOFException;
import java.net.SocketException;

import org.apache.log4j.Category;

import com.topcoder.netCommon.io.ClientSocket;
import com.topcoder.shared.util.StoppableThread;

// Almost identical to the old SocketReaderThread class in com.topcoder.utilities.monitor.model,
// except for the exception handling in cycle()

public class SocketReaderThread implements StoppableThread.Client {

    private final StoppableThread thread;
    private final ClientSocket clientSocket;
    private final Client client;
    private final int id;
    private static final Category log = Category.getInstance(SocketReaderThread.class);

    public SocketReaderThread(ClientSocket clientSocket, Client client, int id) {
        this.clientSocket = clientSocket;
        this.client = client;
        this.id = id;
        thread = new StoppableThread(this, "SocketReaderThread." + id);
        thread.start();
    }

    public void stop() {
        try {
            thread.stopThread();
        } catch (InterruptedException e) {
            log.error(e);
        }
    }

    public void cycle() throws InterruptedException {
        try {
            Object obj = clientSocket.readObject();
            client.receivedObject(id, obj);
        } catch (Exception e) {
            if (!(e instanceof SocketException)) {
                log.error("Received socket error", e);
            }
            if (e instanceof SocketException || e instanceof EOFException) {
                client.stop(id);
                stop();
            }
        }
    }

    public interface Client {

        void receivedObject(int id, Object obj);

        void stop(int id);
    }
}
