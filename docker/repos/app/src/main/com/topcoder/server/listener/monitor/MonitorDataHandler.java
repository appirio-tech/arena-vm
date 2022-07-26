package com.topcoder.server.listener.monitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.topcoder.shared.util.logging.Logger;

public final class MonitorDataHandler implements MonitorInterface, ArenaMonitor {

    private static final Logger log = Logger.getLogger(MonitorDataHandler.class);
    private final Object listLock = new Object();
    private final Object bytesLock = new Object();
    private final Map connMap = new HashMap();
    private final Map usernameMap = new HashMap();

    private ArrayList actionList = new ArrayList();
    private HashMap bytesMap = new HashMap();
    private MonitorChatHandler chatHandler;

    void start() {
        chatHandler.start();
    }

    void stop() {
        chatHandler.stop();
    }

    /* (non-Javadoc)
     * @see com.topcoder.server.listener.monitor.ArenaMonitor#setChatHandler(com.topcoder.server.listener.monitor.MonitorChatHandler)
     */
    public void setChatHandler(MonitorChatHandler chatHandler) {
        this.chatHandler = chatHandler;
    }

    public void newConnection(int id, String remoteIP) {
        synchronized (listLock) {
            if (log.isDebugEnabled()) {
                log.debug("Adding connection #" + id + " from " + remoteIP + " to map");
            }
            AddItem item = new AddItem(id, remoteIP);
            actionList.add(item);
            connMap.put(new Integer(id), item);
        }
    }

    public void lostConnection(int id) {
        if (log.isDebugEnabled()) {
            log.debug("removing connection id: " + id);
        }
        synchronized (listLock) {
            RemoveItem removeItem = new RemoveItem(id);
            actionList.add(removeItem);
            Integer idInt = new Integer(id);
            connMap.remove(idInt);
            usernameMap.remove(idInt);
        }
    }

    public void associateConnections(int existentConnectionId, int newConnectionID) {
        setUsername(newConnectionID, ">"+existentConnectionId);
    }
    
    /* (non-Javadoc)
     * @see com.topcoder.server.listener.monitor.ArenaMonitor#setUsername(int, java.lang.String)
     */
    public void setUsername(int id, String username) {
        synchronized (listLock) {
            Integer key = new Integer(id);
            if (connMap.containsKey(key)) {
                UsernameItem item = new UsernameItem(id, username);
                actionList.add(item);
                usernameMap.put(key, item);
            }
        }
    }

    public void bytesRead(int id, int numBytes) {
        Integer idInt = new Integer(id);
        synchronized (bytesLock) {
            Integer b = (Integer) bytesMap.get(idInt);
            int k;
            if (b == null) {
                k = 0;
            } else {
                k = b.intValue();
            }
            bytesMap.put(idInt, new Integer(k + numBytes));
        }
    }

    FirstResponse getFirstResponse() {
        ArrayList actionListToSend = new ArrayList();
        long time;
        synchronized (listLock) {
            time = System.currentTimeMillis();
            for (Iterator it = connMap.values().iterator(); it.hasNext();) {
                AddItem item = (AddItem) it.next();
                if (log.isDebugEnabled()) {
                    log.debug("Building FirstResponse ... adding item " + item);
                }
                actionListToSend.add(item);
            }
            for (Iterator it = usernameMap.values().iterator(); it.hasNext();) {
                UsernameItem item = (UsernameItem) it.next();
                actionListToSend.add(item);
            }
        }
        return new FirstResponse(time, new MonitorStatsItem(actionListToSend, new HashMap()));
    }

    MonitorStatsItem getResponse() {
        ArrayList actionListToSend;
        synchronized (listLock) {
            actionListToSend = actionList;
            actionList = new ArrayList();
        }
        HashMap bytesMapToSend;
        synchronized (bytesLock) {
            bytesMapToSend = bytesMap;
            bytesMap = new HashMap();
        }
        return new MonitorStatsItem(actionListToSend, bytesMapToSend);
    }

    /* (non-Javadoc)
     * @see com.topcoder.server.listener.monitor.ArenaMonitor#chat(int, java.lang.String, java.lang.String)
     */
    public void chat(int roomID, String username, String message) {
        chatHandler.chat(roomID, username, message);
    }

    /* SYHAAS 2002-05-10 added this to comply with the MonitorInterface */
    /* (non-Javadoc)
     * @see com.topcoder.server.listener.monitor.ArenaMonitor#question(int, java.lang.String, java.lang.String)
     */
    public void question(int roomID, String username, String message) {
        chatHandler.question(roomID, username, message);
    }

}
