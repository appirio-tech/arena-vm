/*
 * DisconnectTimer.java
 *
 * This thread is responsible for destroying any hung connections after a timeout
 *
 * Created on January 10, 2005, 3:31 PM
 */

package com.topcoder.server.processor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.topcoder.shared.util.logging.Logger;
/**
 *
 * @author rfairfax
 */
public class DisconnectTimer {
    
    protected static Logger trace = Logger.getLogger(DisconnectTimer.class);
    protected static Map s_hungConnections = new HashMap();
    protected static DisconnectThread disconnectThread;

    public static void addConnection(int id) {
        synchronized (s_hungConnections) {
            if(!s_hungConnections.containsKey(new Integer(id))) {
                s_hungConnections.put(new Integer(id), new Long(System.currentTimeMillis() + (2 * 60 * 1000)));
                s_hungConnections.notifyAll();
            }
        }
    }
    
    public static void removeConnection(int id) {
        synchronized (s_hungConnections) {
            s_hungConnections.remove(new Integer(id));
            s_hungConnections.notifyAll();
        }
    }

    static class DisconnectThread implements Runnable {

        protected boolean m_stopped = false;

        public void run() {
            try {
                while (!m_stopped) {
                    Integer id;
                    long time;
                    synchronized (s_hungConnections) {
                        while (!m_stopped && s_hungConnections.isEmpty()) {
                            try {
                                s_hungConnections.wait();
                            } catch (InterruptedException ie) {
                                trace.debug("Interrupted waiting for disconnects", ie);
                            }
                        }
                        trace.debug("NON-EMPTY");
                        if (!s_hungConnections.isEmpty()) {
                            //loop through items
                            trace.debug("LOOP");
                            Iterator it = s_hungConnections.entrySet().iterator();
                            while(it.hasNext()) {
                                Map.Entry entry = ((Map.Entry)it.next());
                                id = (Integer)entry.getKey();
                                time = ((Long)entry.getValue()).longValue();
                                if(time <= System.currentTimeMillis()) {
                                    //probably want to kill the queue here as well
                                    if (trace.isDebugEnabled()) {
                                        trace.debug("HERE AND KILLING " + id.intValue());
                                    }
                                    RequestProcessor.lostConnection(id.intValue());
                                    it.remove();
                                }
                            }
                            
                        }
                    }
                    
                    try {
                        Thread.sleep(5 * 1000);
                    } catch (InterruptedException ie) {
                        trace.debug("Interrupted sleeping", ie);
                    }
                }
            } finally {
                trace.debug("Exiting run");
            }
        }

        public void stop() {
            m_stopped = true;
        }
    }

    public static void start() {
        DisconnectThread disconnectThread = new DisconnectThread();
        Thread t = new Thread(disconnectThread, "DisconnectThread");
        t.setDaemon(true);
        t.start();
    }

    public static void stop() {
        disconnectThread.stop();

        synchronized (s_hungConnections) {
            s_hungConnections.notifyAll();
        }
    }
    
}
