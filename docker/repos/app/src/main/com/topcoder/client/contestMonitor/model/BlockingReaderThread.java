package com.topcoder.client.contestMonitor.model;

import com.topcoder.netCommon.io.ClientSocket;
import org.apache.log4j.Category;

import java.io.ObjectInputStream;


public class BlockingReaderThread implements Runnable {

    private final Thread thread;
    private final Client client;
    private final int id;
    private final ClientSocket socket;
    private static final Category log = Category.getInstance(BlockingReaderThread.class);

    BlockingReaderThread(ClientSocket socket, Client client, int id) throws Exception {
        this.client = client;
        this.id = id;
        this.socket = socket;
        thread = new Thread(this);
        thread.start();
    }

    // It is assumed the socket has already been opened
    public void run() {
        try {
            for (; ;) {
                // This blocks
                Object obj = socket.readObject();
                client.receivedObject(id, obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // This is the normal method of exiting the thread, as a result of
            // the socket being closed elsewhere.  Log it anyway, just in case.
            client.stop(id);
            log.error("Blocking reader thread has died", e);
        }
    }

    interface Client {

        void receivedObject(int id, Object obj);

        void stop(int id);
    }
}

