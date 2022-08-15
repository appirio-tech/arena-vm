package com.topcoder.server.util;

import org.apache.log4j.Logger;

import com.topcoder.shared.util.StoppableThread;

public class QueueReaderThread implements StoppableThread.Client {

    private static final Logger logger = Logger.getLogger(QueueReaderThread.class);

    private TCLinkedQueue queue;
    private Client queueClient;
    private StoppableThread thread;

    public QueueReaderThread(TCLinkedQueue queue, Client queueClient, String name) {
        this.queue = queue;
        this.queueClient = queueClient;
        thread = new StoppableThread(this, name);
    }

    public void start() {
        thread.start();
    }

    public void stop() {
        try {
            thread.stopThread();
        } catch (InterruptedException e) {
            logger.error(e);
        }
    }

    public void cycle() {
        try {
            Object o = queue.take();
            queueClient.receivedQueueItem(o);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public interface Client {

        void receivedQueueItem(Object item);
    }
}
