package com.topcoder.server.listener;

import com.topcoder.server.listener.util.concurrent.CountDown;
import com.topcoder.shared.util.StoppableThread;


final class WorkerThread implements StoppableThread.Client {

    private final QueueIterator queueIterator;
    private final Object lock;
    private final StoppableThread thread;
    private final CountDown count;

    WorkerThread(QueueIterator queueIterator, Object lock, int id, CountDown count, String name) {
        this.queueIterator = queueIterator;
        this.lock = lock;
        this.count = count;
        thread = new StoppableThread(this, name + ".WorkerThread." + id);
    }

    void start() {
        thread.start();
    }

    void stop() {
        try {
            thread.stopThread();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void cycle() throws InterruptedException {
        try {
            Object object;
            synchronized (lock) {
                while (!queueIterator.hasNext()) {
                    lock.wait();
                }
                object = queueIterator.next();
                if (object == null) {
                    return;
                }
            }
            queueIterator.processKey(object);
        } finally {
            count.release();
        }
    }

}
