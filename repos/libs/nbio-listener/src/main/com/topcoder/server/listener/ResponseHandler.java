package com.topcoder.server.listener;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.topcoder.server.listener.nio.channels.ClosedChannelException;
import com.topcoder.server.listener.nio.channels.SelectionKey;
import com.topcoder.server.listener.nio.channels.SocketChannel;

final class ResponseHandler extends BaseHandler {

    private static final String CLASS_NAME = "ResponseHandler";

    private final Map writeConnections = new ConcurrentHashMap();
    private final Map waitMap = new LinkedHashMap(16, 0.75f, true);
    private final ReaderWriterFactory rwFactory;

    private Set waitSet;
    private int trafficSize;

    ResponseHandler(int port, int numWorkerThreads, HandlerClient handlerClient, ReaderWriterFactory rwFactory) {
        super(port, ListenerConstants.PACKAGE_NAME + CLASS_NAME, numWorkerThreads, handlerClient);
        this.rwFactory = rwFactory;
    }

    public void open() {
    }

    public void close() {
    }

    int getOps() {
        return SelectionKey.OP_WRITE;
    }

    int getTrafficSize() {
        return trafficSize;
    }

    int getConnectionsSize() {
        return writeConnections.size();
    }

    int getQueueSize() {
        Collection t = new ArrayList(waitMap.keySet());
        int sum = 0;
        for (Iterator it = t.iterator(); it.hasNext();) {
            ResponseWriter writer = (ResponseWriter) writeConnections.get(it.next());
            if (writer != null) {
                sum += writer.getQueueSize();
            }
        }
        return sum;
    }

    int getWaitSetSize() {
        return waitMap.size();
    }

    Set keySet() {
        return writeConnections.keySet();
    }

    void register(Integer id, SocketChannel socketChannel) {
        ResponseWriter writer = new ResponseWriter(id, socketChannel, rwFactory.newObjectWriter());
        SelectionKey key = register(socketChannel, writer);
        if (key == null) {
            return;
        }
        writer.setSelectionKey(key);
        writeConnections.put(id, writer);
    }
    
    boolean writeObject(Integer id, Object object) {
        ResponseWriter writer =  (ResponseWriter) writeConnections.get(id);
        if (writer == null) {
            return false;
        }
        synchronized (writer) {
            writer.enqueue(object);
            addToQueueIfNeeded(id, writer);
        }
        return true;
    }

    SocketChannel remove(Integer id) {
        ResponseWriter writer = (ResponseWriter) writeConnections.remove(id);
        if (writer != null) {
            synchronized (writer) {
                removeFromQueue(id, null);
                return (SocketChannel)writer.channel();
            }
        } else {
            removeFromQueue(id, null);
        }
        return null;
    }

    Set getSet() {
        return waitSet;
    }

    private Long lastWriteOk;
    void cycleInit() throws InterruptedException {
        waitSet = dequeue();
        waitSet = removeTimeOutConnections(waitSet);
        lastWriteOk = new Long(System.currentTimeMillis());
    }

    private Set removeTimeOutConnections(Set waitSet) {
        long maxTime = System.currentTimeMillis() - ResponseWriter.NO_KEY_SPIN_LIMIT;
        synchronized (waitMap) {
            for (Iterator it = waitMap.entrySet().iterator(); it.hasNext();) {
                Map.Entry entry = (Map.Entry) it.next();
                if (((Long)entry.getValue()).longValue() < maxTime) {
                    if (waitSet.size() == 0) {
                        waitSet = new HashSet();
                    }
                    ResponseWriter writer = (ResponseWriter) writeConnections.get(entry.getKey());
                    if (writer != null) {
                        log.error("cannot write, no progress, scheduling disconnect, lastWrite=" + entry.getValue() + ", " + writer);
                        writer.setMustClose(true);
                        waitSet.add(writer.getSelectionKey());
                    }
                } else {
                    break;
                }
            }
        }
        return waitSet;
    }

    public void processKey(Object object) throws InterruptedException {
        SelectionKey key = (SelectionKey) object;
        ResponseWriter writer = (ResponseWriter) key.attachment();
        
        try {
            if (writer == null) {
                log.warn("The writer was null for a selected Key");
                return;
            }
            if (writer.isMustClose()) {
                closeConnectionDueToErrors(writer.getConnectionId());
                return;
            }
            int bytesWritten = writer.write();
            trafficSize += bytesWritten;
            if (bytesWritten != 0) {
                synchronized (waitMap) {
                    if (System.currentTimeMillis() - lastWriteOk.longValue() > 1000) {
                        lastWriteOk = new Long(System.currentTimeMillis());
                    }
                    waitMap.put(writer.getConnectionId(), lastWriteOk);
                }
            } else {
                if (log.isDebugEnabled()) log.debug("Unexpected, no bytes writen in a ready connection");
            }
            //debug("processKey "+writer+" "+bytesWritten);
        } catch (SocketException e) {
            if (log.isDebugEnabled()) log.debug("ERROR 1");
            // socket closed
        } catch (ClosedChannelException e) {
            if (log.isDebugEnabled()) log.debug("ERROR 2");
        } catch (IOException e) {
            if (log.isDebugEnabled()) log.debug("ERROR 3");
            //error("writer.write()",e);
        }
        synchronized (writer) {
            if (writer.isQueueEmpty()) {
                if (writer.getSelectionKey() == null) {
                    writer.setSelectionKey(key);
                }
                removeFromQueue(writer.getConnectionId(), writer);
            }
        }
    }

    /**
     * Remove the connection from the queue of connections with pending messages
     * 
     * IMPORTANT: The connection associated writer monitor must be hold by the current thread. 
     * 
     * @param id The id of the connection
     * @param writer The writer holding the selection key to modify. null if no changes are need for the 
     *        selection key because channel is closing.
     * 
     */
    private void removeFromQueue(Integer id, ResponseWriter writer) {
        synchronized (waitMap) {
            waitMap.remove(id);
        }
        if (writer != null) {
            try {
                writer.getSelectionKey().disableOps(SelectionKey.OP_WRITE);
            } catch (Exception e) {
                if (log.isDebugEnabled()) {
                    log.error("Exception disabling interestOps on connection "+id, e);
                }
            }
        }
    }
    
    private void addToQueueIfNeeded(Integer id, ResponseWriter writer) {
        boolean updateKeys = false;
        synchronized (waitMap) {
            if (!waitMap.containsKey(id)) {
                waitMap.put(id, new Long(System.currentTimeMillis()));
                updateKeys = true;
            }
        }
        if (updateKeys) {
            try {
                writer.getSelectionKey().enableOps(SelectionKey.OP_WRITE);
            } catch (Exception e) {
                if (log.isDebugEnabled()) {
                    log.error("Exception enabling interestOps on connection "+id, e);
                }
            }
        }
    }

}
