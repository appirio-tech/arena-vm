package com.topcoder.client.contestMonitor.model;

import org.apache.log4j.Category;

import java.io.ObjectInputStream;
import java.net.Socket;

/**
 * Borrowed and modified from the monitor client.
 *
 * @author John Waymouth (coderlemming)
 */

public class TestBlockingReaderThread implements Runnable {

    private final Thread thread;
    private final ObjectInputStream socketInputStream;
    private final Client client;
    private final int id;
    private static final Category log = Category.getInstance(BlockingReaderThread.class);

    private boolean stopRequested = false;

    TestBlockingReaderThread(Socket socket, Client client, int id) throws Exception {
        this.socketInputStream = new ObjectInputStream(socket.getInputStream());
        this.client = client;
        this.id = id;
        thread = new Thread(this);
        thread.start();
    }

    // It is assumed the socket has already been opened
    public void run() {
        try {
            for (; ;) {
                // This blocks
                Object obj = socketInputStream.readObject();
                client.receivedObject(id, obj);
            }
        } catch (Exception e) {
            // This is the normal method of exiting the thread, as a result of
            // the socket being closed elsewhere.  Log it anyway, just in case.

            // John Waymouth: except, if a stop has already been requested,
            // chances are this exception was expected, so don't log.

            if (!stopRequested) {
                client.stop(id);
                log.error("Blocking reader thread has died", e);
            }
        }
    }

    public void stop() {
        stopRequested = true;
    }

    interface Client {

        void receivedObject(int id, Object obj);

        void stop(int id);
    }
}
