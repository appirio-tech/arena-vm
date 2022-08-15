/*
 * Author: Michael Cervantes (emcee)
 * Date: Jun 2, 2002
 * Time: 10:09:32 PM
 */
package com.topcoder.server.AdminListener;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import com.topcoder.netCommon.io.ClientSocket;
import com.topcoder.server.listener.ListenerInterface;
import com.topcoder.server.listener.monitor.MonitorCSHandler;
import com.topcoder.server.util.logging.net.LoggingMessage;
import com.topcoder.server.util.logging.net.StreamID;
import com.topcoder.server.util.logging.net.TCBatchLoggingEvent;
import com.topcoder.server.util.logging.net.TCLoggingEvent;
import com.topcoder.shared.util.StoppableThread;
import com.topcoder.shared.util.logging.Logger;

public class LoggingServer {

    private static final Logger log = Logger.getLogger(LoggingServer.class);

    private ListenerInterface messageSender;

    private ServerSocket socket;
    private StoppableThread listenerThread;

    // collection of subscribers for recently dropped connections
    private Map subscriberCache = new HashMap();
    private Timer subscriberCacheTimer = new Timer();
    private long MAX_TIME_IN_SUBSCRIBER_CACHE_MS = 30 * 1000;


    private Map streams = new HashMap();
    private static final int CATCH_UP_BUFFER_SIZE = 50;
    private boolean enabled = true;

    public LoggingServer(ListenerInterface messageSender, int port) {
        if (port == 0) {
            enabled = false;
            log.debug("Port: " + port);
            return;
        }
        this.messageSender = messageSender;
        try {
            socket = new ServerSocket(port);
            socket.setSoTimeout(1000);
        } catch (IOException e) {
            log.error("Error creating server socket on port " + port, e);
            throw new RuntimeException(e);
        }
        listenerThread = new StoppableThread(new ServerSocketRunner(), "LoggingServer.listenerThread");
    }

    public synchronized void start() {
        log.info("Starting logging server...");
        if (enabled)
            listenerThread.start();
    }

    public void shutdown() {
        log.info("Logging server shutting down...");
        if (!enabled)
            return;
        enabled = false;
        try {
            log.info("Stopping logging listener thread...");
            listenerThread.stopThread();
        } catch (InterruptedException e) {
            log.error("Interrupted while stopping logging server thread", e);
        } finally {
            try {
                log.info("Closing logging server socket...");
                socket.close();
            } catch (IOException e) {
                log.error("Error closing logging server socket", e);
            } finally {
                try {
                    log.info("Killing logging streams...");
                    Set streamsCopy = new HashSet(streams.values());
                    for (Iterator it = streamsCopy.iterator(); it.hasNext();) {
                        try {
                            LoggingWorker worker = (LoggingWorker) it.next();
                            removeStream(worker.streamID, false);
                            worker.shutdown();
                        } catch (Exception e) {
                            log.error("Shutdown error");
                            log.error(e);
                        }
                    }
                } finally {
                    subscriberCacheTimer.cancel();
                    subscriberCache.clear();
                }
            }
        }
    }

    public synchronized Collection getSupportedStreams() {
        return streams.keySet();
    }

    private final class ServerSocketRunner implements StoppableThread.Client {

        public void cycle() throws InterruptedException {
            try {
                Socket conn = socket.accept();
                conn.setSoTimeout(1000);
                ClientSocket client = new ClientSocket(conn, new MonitorCSHandler());
                Object o = client.readObject();
                //ObjectInputStream reader = new ObjectInputStream(conn.getInputStream());
                //Object o = reader.readObject();

                if (!(o instanceof StreamID)) {
                    log.warn("Expected streamID, got: " + o);
                    try {
                        client.close();
                    } finally {
                        conn.close();
                    }

                    return;
                }

                newStream((StreamID) o, conn, client);
            } catch (SocketTimeoutException e) {
                // ignore, this is normal
                // time for another cycle
            } catch (Exception e) {
                log.error("ServerSocketRunner: cycle() error");
                log.error(e);
                e.printStackTrace();
            }
        }
        
        private void newStream(StreamID id, Socket conn, ClientSocket client) {
            id.setHost(conn.getInetAddress().getHostAddress());

            if (log.isDebugEnabled()) {
                log.debug("New logging connection with " + id);
            }

            if (streams.containsKey(id)) {
                log.info("Closing pre-existing stream: " + id);
                LoggingWorker existingWorker = (LoggingWorker) streams.get(id);
                removeStream(id, true);
                existingWorker.shutdown();
            }

            Set subscribers = (Set) subscriberCache.remove(id);
            if (subscribers == null) {
                subscribers = new HashSet();
            }
            // build a new worker for this stream
            LoggingWorker worker = new LoggingWorker(conn, client, subscribers, id);
            StoppableThread thread = new StoppableThread(
                    worker,
                    "Logging Worker - " + conn.getInetAddress()
            );
            worker.setThread(thread);
            addStream(id, worker);
            thread.start();
        }
    }


    private synchronized void addStream(StreamID id, LoggingWorker worker) {
        streams.put(id, worker);
    }


    public synchronized void addSubscriber(int connectionID, StreamID stream) {
        if (!enabled)
            return;
        if (streams.containsKey(stream)) {
            LoggingWorker worker = (LoggingWorker) streams.get(stream);
            worker.addSubscriber(connectionID);
        } else {
            log.error("Unrecoginzed stream: " + stream);
        }
    }

    public void removeSubscriber(int connectionID) {
        if (!enabled)
            return;
        if (log.isDebugEnabled())
            log.debug("Removing subscriber " + connectionID);
        synchronized (this) {
            for (Iterator it = streams.values().iterator(); it.hasNext();) {
                ((LoggingWorker) it.next()).removeSubscriber(connectionID);
            }
            Integer key = new Integer(connectionID);
            for (Iterator it = subscriberCache.values().iterator(); it.hasNext();) {
                ((Set) it.next()).remove(key);
            }
        }
    }

    public void removeSubscriber(int connectionID, StreamID stream) {
        if (!enabled)
            return;
        if (log.isDebugEnabled())
            log.debug("Removing subscriber " + connectionID + " from stream " + stream);
        synchronized (this) {
            LoggingWorker worker = (LoggingWorker) streams.get(stream);
            if (worker != null) {
                worker.removeSubscriber(connectionID);
            }
            Set subscribers = (Set) subscriberCache.get(stream);
            if (subscribers != null)
                subscribers.remove(new Integer(connectionID));
        }
    }

    private synchronized void removeStream(StreamID id, boolean cacheSubscribers) {
        if (!enabled)
            return;
        if (log.isDebugEnabled()) {
            log.debug("Removing stream: " + id);
        }
        LoggingWorker worker = (LoggingWorker) streams.remove(id);
        if (worker != null && cacheSubscribers) {
            addToSubscriberCache(worker.streamID, worker.subscribers);
        }
    }


    private synchronized void addToSubscriberCache(final StreamID stream, Set subscribers) {
        subscriberCache.put(stream, subscribers);
        subscriberCacheTimer.schedule(new TimerTask() {
            public void run() {
                removeFromSubscriberCache(stream);
            }
        }, MAX_TIME_IN_SUBSCRIBER_CACHE_MS);
    }

    private synchronized Set removeFromSubscriberCache(StreamID id) {
        if (subscriberCache.containsKey(id)) {
            if (log.isInfoEnabled())
                log.info("Removing stream " + id + " from subscriber cache");
            return (Set) subscriberCache.remove(id);
        }
        return null;
    }


    private final class LoggingWorker implements StoppableThread.Client {

        private ClientSocket client;
        private Socket conn;
        Set subscribers = new HashSet();
        StreamID streamID;
        List catchUpBuffer = new LinkedList();
        StoppableThread thread;

        private synchronized void addSubscriber(int connectionID) {
            subscribers.add(new Integer(connectionID));
            catchUp(connectionID);
        }

        private synchronized void removeSubscriber(int connectionID) {
            subscribers.remove(new Integer(connectionID));
        }
        
        LoggingWorker(Socket conn, ClientSocket client, Set subscribers, StreamID streamID) {
            this.conn = conn;
            this.client = client;
            this.streamID = streamID;
            this.subscribers = subscribers;
        }

        private void setThread(StoppableThread thread) {
            this.thread = thread;
        }

        public void cycle() throws InterruptedException {
            try {
                Object event = client.readObject();
                if (event != null) {
                    // save messages so new subscribers can catch up
                    synchronized (this) {
                        addToCatchUpBuffer(event);
                        notifySubscribers(event);
                    }
                }
            } catch (SocketTimeoutException e) {
                // ignore, this is normal
                // time for another cycle
            } catch (InterruptedIOException e) {
                log.debug("Read interrupted...");
            } catch (IOException e) {
                log.debug("Lost connection with " + conn.getInetAddress() + "...");
                removeStream(streamID, true);
                shutdown();
            } catch (Exception e) {
                log.error("Abnormal error reading logging data from " + conn.getInetAddress(), e);
                removeStream(streamID, true);
                shutdown();
            }
        }

        private synchronized void addToCatchUpBuffer(Object event) {
            if(event instanceof TCBatchLoggingEvent) {
                TCBatchLoggingEvent evt = (TCBatchLoggingEvent)event;
                List l = evt.getItems();
                for(int i = 0; i < l.size(); i++) {
                    if (catchUpBuffer.size() == CATCH_UP_BUFFER_SIZE) {
                        catchUpBuffer.remove(0);
                    }
                    catchUpBuffer.add(l.get(i));
                }
            } else {
                if (catchUpBuffer.size() == CATCH_UP_BUFFER_SIZE) {
                    catchUpBuffer.remove(0);
                }
                catchUpBuffer.add(event);
            }
        }

        private synchronized void notifySubscribers(Object e) {
            for (Iterator it = subscribers.iterator(); it.hasNext();) {
                Integer conn = (Integer) it.next();
                try {
                    sendEvent(conn.intValue(), streamID, e);
                } catch (Exception ex) {
                    log.error("notifySubscribers() error");
                    log.error(ex);
                }
            }
        }

        private void closeConnection() {
            try {
                log.debug("Closing reader");
                client.close();
            } catch (IOException e) {
                log.error(e);
            } finally {
                try {
                    log.debug("Closing connection");
                    conn.close();
                } catch (IOException e) {
                    log.error(e);
                }
            }
        }

        private boolean isShutdown = false;

        private void shutdown() {
            synchronized (this) {
                if (isShutdown)
                    return;
                isShutdown = true;
            }
            log.debug("Stopping worker thread");
            try {
                thread.stopThread();
            } catch (InterruptedException e) {
                log.error(e);
            } finally {
                closeConnection();
            }
        }

        private synchronized void catchUp(int connectionID) {
            for (Iterator it = catchUpBuffer.iterator(); it.hasNext();) {
                Object e = it.next();
                sendEvent(connectionID, streamID, e);
            }
        }
    }


    private void sendEvent(int connectionID, StreamID stream, Object e) {
        if(e instanceof TCBatchLoggingEvent) {
            List l = ((TCBatchLoggingEvent)e).getItems();
            for(int i = 0; i < l.size();i++) {
                messageSender.send(connectionID, new LoggingMessage(stream, (TCLoggingEvent)l.get(i)));
            }
        } else {
            messageSender.send(connectionID, new LoggingMessage(stream, (TCLoggingEvent)e));
        }
    }
}
