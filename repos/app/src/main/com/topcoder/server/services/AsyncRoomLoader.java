/*
 * AsyncRoomLoader
 * 
 * Created 08/20/2007
 */
package com.topcoder.server.services;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import com.topcoder.server.common.Room;
import com.topcoder.shared.util.logging.Logger;

/**
 * @author Diego Belfer (mural)
 * @version $Id: AsyncRoomLoader.java 72730 2008-09-08 08:19:06Z qliu $
 */
public class AsyncRoomLoader {
    private final Logger log = Logger.getLogger(CoreServices.class);
    private final LinkedList roomRequests = new LinkedList();
    private final Map roomListeners = new HashMap();
    private final Set requestedConnections = Collections.synchronizedSet(new HashSet());
    private RoomLoaderDaemon[] threads;

    public static interface RoomLoadedListener {
        void roomLoaded(Room room);
        Integer getConnectionId();
    }

    private class RoomLoaderDaemon extends Thread {
        private volatile boolean stopped = false;
        
        public RoomLoaderDaemon(String name) {
            super(name);
        }

        public void run() {
            while (!stopped) {
                // Get the first ID.
                Integer roomId;

                synchronized(roomRequests) {
                    while (roomRequests.size() == 0) {
                        try {
                            roomRequests.wait();
                        } catch (InterruptedException e) {
                            return;
                        }
                    }
                    roomId = (Integer) roomRequests.removeFirst();
                }

                log.info("Picked room " + roomId + " to load from DB");

                // This process is slow
                Room room = null;
                try {
                    room = CoreServices.getDBRoom(roomId.intValue());
                } catch (Exception e) {
                    log.error("Error loading room", e);
                    //remove it from the list
                    synchronized(roomRequests) {
                        roomListeners.remove(roomId);
                    }
                    continue;
                }

                if(room == null) {
                    log.error("Error loading room, room == null");
                    //remove it from the list
                    synchronized(roomRequests) {
                        roomListeners.remove(roomId);
                    }
                    continue;
                }
                // Save to cache
                CoreServices.saveToCache(room.getCacheKey(), room);
                log.info("Loaded room " + roomId + " from DB");

                // Begin to notify clients
                Map listeners;
                synchronized(roomRequests) {
                    listeners = (Map) roomListeners.remove(roomId);
                }
                
                for (Iterator iter = listeners.entrySet().iterator(); iter.hasNext();) {
                    RoomLoadedListener listener = (RoomLoadedListener) ((Map.Entry) iter.next()).getValue();
                    // Remove the requestedConnection
                    requestedConnections.remove(listener.getConnectionId());
                    // Call the listener
                    listener.roomLoaded(room);
                }
            }
        }
        
        public void halt() {
            stopped = true;
            this.interrupt();
        }
    }
    
    public AsyncRoomLoader(int maxThreads) {
        threads = new RoomLoaderDaemon[maxThreads];
    }
    
    public boolean loadRoom(int roomId, RoomLoadedListener listener) {
        try {
            Room room = null;
            room = CoreServices.getRoomFromCache(roomId);
    
            if (room == null) {
                synchronized(roomRequests) {
                    room = CoreServices.getRoomFromCache(roomId);
                    if (room == null) {
                        if (requestedConnections.contains(listener.getConnectionId())) {
                            // If there is a request from the connection already, do not accept it.
                            return false;
                        }

                        requestedConnections.add(listener.getConnectionId());
                        Integer roomIdKey = new Integer(roomId);
    
                        if (roomListeners.containsKey(roomIdKey)) {
                            ((Map) roomListeners.get(roomIdKey)).put(listener.getConnectionId(), listener);
                        } else {
                            roomRequests.addLast(roomIdKey);
                            roomListeners.put(roomIdKey, new HashMap());
                            ((Map) roomListeners.get(roomIdKey)).put(listener.getConnectionId(), listener);
                            roomRequests.notify();
                        }
                    }
                }
            }
    
            if (room != null) {
                requestedConnections.remove(listener.getConnectionId());
                listener.roomLoaded(room);
                return true;
            }
    
            return false;
        } catch (Exception e) {
            log.error("Exception in getRoomAsync(" + roomId + ")", e);
            throw new RuntimeException(e);
        }
    }

    public void start() {
        for (int i = 0; i < threads.length; i++) {
            RoomLoaderDaemon t = new RoomLoaderDaemon("Room DB Loader-"+i);
            threads[i] = t;
            t.setDaemon(true);
            t.start();
        }
        
    }
    
    
    public void stop() {
        for (int i = 0; i < threads.length; i++) {
            if (threads[i] != null) {
                threads[i].halt();
                threads[i] = null;
            }
        }
    }
}
