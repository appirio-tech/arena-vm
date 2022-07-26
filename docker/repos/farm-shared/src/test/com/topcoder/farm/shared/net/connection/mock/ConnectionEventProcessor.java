/*
 * ConnectionEventProcessor
 * 
 * Created 07/21/2006
 */
package com.topcoder.farm.shared.net.connection.mock;

import com.topcoder.farm.shared.net.connection.api.ConnectionHandler;
import com.topcoder.farm.shared.util.queue.Queue;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ConnectionEventProcessor implements Runnable {
    private Queue events;
    private ConnectionHandler handler;
    private Thread thread;
    
    public ConnectionEventProcessor(Queue events, ConnectionHandler handler) {
        this.events = events;
        this.handler = handler;
    }

    public void start() {
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    public void stop() {
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
    }
    
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                ConnectionEvent evt = (ConnectionEvent) events.poll(0);
                if (evt != null && handler != null) {
                    switch (evt.getType()) {
                        case ConnectionEvent.RECEIVED:
                            handler.receive(evt.getConnection(), evt.getArg());
                            break;
                        case ConnectionEvent.LOST:
                            handler.connectionLost(evt.getConnection());
                            break;
                        case ConnectionEvent.CLOSED:
                            handler.connectionClosed(evt.getConnection());
                            break;
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
            }
        }
    }
}
