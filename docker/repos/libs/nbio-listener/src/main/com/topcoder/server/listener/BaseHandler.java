package com.topcoder.server.listener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import com.topcoder.server.listener.nio.channels.ClosedChannelException;
import com.topcoder.server.listener.nio.channels.SelectableChannel;
import com.topcoder.server.listener.nio.channels.SelectionKey;
import com.topcoder.server.listener.nio.channels.SocketChannel;
import com.topcoder.server.listener.util.concurrent.CountDown;
import com.topcoder.shared.util.StoppableThread;
import com.topcoder.shared.util.logging.Logger;


abstract class BaseHandler implements StoppableThread.Client, QueueIterator {
    private static final int MAX_THREADS = 32;

    protected final Logger log;
    private final StoppableThread backgroundThread;
    private final WorkerThread workerThread[];
    private final Object iteratorLock = new Object();
    private final CountDown count = new CountDown(0);
    private final HandlerClient handlerClient;

    private Iterator iterator = (new ArrayList(0)).iterator();
    private SelectSource selectSource;

    BaseHandler(int port, String name, int numWorkerThreads, HandlerClient handlerClient) {
        this.handlerClient = handlerClient;
        name += "." + port;
        log = Logger.getLogger(name);
        backgroundThread = new StoppableThread(this, name);
        if (numWorkerThreads < 0 || numWorkerThreads > MAX_THREADS) {
            numWorkerThreads = 0;
        }
        if (log.isInfoEnabled()) log.info("numWorkerThreads=" + numWorkerThreads);
        workerThread = new WorkerThread[numWorkerThreads];
        for (int i = 0; i < workerThread.length; i++) {
            workerThread[i] = new WorkerThread(this, iteratorLock, i, count, name);
        }
    }

    void acceptNewSocket(SocketChannel socketChannel) {
        handlerClient.acceptNewSocket(socketChannel);
    }
    
    void closeConnection(Integer id, boolean lost) {
        handlerClient.closeConnection(id, lost);
    }

    void closeConnection(Integer id) {
        handlerClient.closeConnection(id);
    }

    void receiveRequest(int id, Object request) {
        handlerClient.receiveRequest(id, request);
    }
    
    
    protected void closeConnectionDueToErrors(Integer connectionId) {
        try {
            handlerClient.closeConnection(connectionId, true);
        } catch (Exception e) {
            log.error("Exception while closing connection: " + connectionId, e);
        }
    }
    

    abstract int getOps();

    void open() throws IOException {
    }

    void close() {
    }

    final void start() throws IOException {
        if (log.isInfoEnabled()) log.info("starting");
        selectSource = new SelectSource();
        if (log.isDebugEnabled()) log.debug("selector created");
        open();
        backgroundThread.start();
        for (int i = 0; i < workerThread.length; i++) {
            workerThread[i].start();
        }
        if (log.isInfoEnabled()) log.info("threads started");
    }

    final void stop() {
        if (log.isInfoEnabled()) log.info("stopping threads");
        try {
            backgroundThread.stopThread();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < workerThread.length; i++) {
            workerThread[i].stop();
        }
        if (selectSource != null) {
            if (log.isInfoEnabled()) log.info("closing selector");
            selectSource.close();
        }
        close();
        if (log.isInfoEnabled()) log.info("stopped");
    }

    final SelectionKey register(SelectableChannel channel, Attachment att) {
        if (log.isDebugEnabled()) log.debug("registering socket channel");
        try {
            return selectSource.register(channel, getOps(), att);
        } catch (ClosedChannelException e) {
            return null;
        }
    }

    Set getSet() throws InterruptedException {
        return dequeue();
    }

    final Set dequeue() throws InterruptedException {
        return selectSource.dequeue();
    }

    public final boolean hasNext() {
        return iterator.hasNext();
    }

    public final Object next() {
        return iterator.next();
    }

    void cycleInit() throws InterruptedException {
    }
    
    public void banIPwithExpiry(String ipAddress, long expiresAt)
    {
        handlerClient.banIPwithExpiry(ipAddress, expiresAt);
    }

    public final void cycle() throws InterruptedException {
        cycleInit();
        synchronized (iteratorLock) {
            Set set = getSet();
            iterator = set.iterator();
            if (!hasNext()) {
                return;
            }
            count.restart(set.size());
            iteratorLock.notifyAll();
        }
        for (; ;) {
            Object object;
            synchronized (iteratorLock) {
                if (!hasNext()) {
                    break;
                }
                object = next();
                if (object == null) {
                    count.release();
                    continue;
                }
            }
            processKey(object);
            count.release();
        }
        count.acquire();
    }

}
