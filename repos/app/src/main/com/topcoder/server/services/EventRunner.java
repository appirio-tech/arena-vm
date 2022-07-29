package com.topcoder.server.services;

//import com.topcoder.server.common.TCEvent;

import org.apache.log4j.Category;

import java.util.LinkedList;

//import org.apache.log4j.Category;

final class EventRunner implements Runnable {

    //private static Category trace = Category.getInstance( EventService.class.getName() );
    private static final int SLEEP_TIME = 250;

    private boolean m_stopped;

    EventRunner() {
    }

    void stop() {
        m_stopped = true;
    }

    public void run() {
        while (!m_stopped) {
            LinkedList eventQueue = EventService.getAndClearEventQueue();
            if (eventQueue.size() > 0) {
                //trace.debug("GT Before Event Runner Event Service Send ");
                EventService.handleGlobalSend(eventQueue);
                //trace.debug("GT After Event Runner Event Service Send ");
            }
            try {
                Thread.currentThread().sleep(SLEEP_TIME);
            } catch (Throwable t) {
            }
        }
    }
}
