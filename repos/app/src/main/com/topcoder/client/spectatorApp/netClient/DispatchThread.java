/**
 * DispatchThread.java
 *
 * Description:		Thread that will take responses and dispatch them to the appropriate listener.  This class is thread safe.
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.netClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Category;

public class DispatchThread extends Thread {

    /** reference to the logging category */
    private static final Category cat = Category.getInstance(DispatchThread.class.getName());

    /** Singleton instance of the thread */
    private static DispatchThread dispatchThread;

    /** Lock used to coordinate notifying */
    private Object notifyLock = new Object();

    /** Queue of event to dispatch */
    private List messageQueue = Collections.synchronizedList(new ArrayList());

    /** List of processors for events */
    private ArrayList processorList = new ArrayList();

    /** Lock used to lock the processor list */
    private Object processorListLock = new Object();


    /**
     *  Private Constructor.  The DispatchThread implements a singleton pattern.  Please use getInstance() to retrieve an instance of this class
     */
    private DispatchThread() {
        // Start running our thread
        setDaemon(true);
        start();
    }


    /**
     *  Get's the instance of the dispatch thread.
     */
    public final static synchronized DispatchThread getInstance() {
        if (dispatchThread == null) dispatchThread = new DispatchThread();
        return dispatchThread;
    }


    /**
     *  Thread that will dispatch the events to the support handlers
     */
    public final void run() {

        synchronized (notifyLock) {

            // Keep going...
            while (true) {

                try {
                    // If we have nothing in the response queu
                    if (messageQueue.size() == 0) {
                        // Wait for one second (or until notified)
                        // The timeout is because between the size check above
                        // and the wait - something could have come in and not
                        // have been processed - the timeout allows it to be processed
                        try {
                            notifyLock.wait(1000);
                        } catch (Throwable t) {
                        }
                    }

                    // Is there anything to process?
                    while (messageQueue.size() > 0) {
                        // Get the event
                        dispatchMessage(messageQueue.remove(0));
                    }
                } catch (Throwable t) {
// dpecora - don't fill in trace
                    cat.error("Error processing message", t);
                }
            }
        }
    }

    /**
     *  Queue's a message to be dispatched
     *
     *  @param message the message to add to the queue
     */
    public void queueMessage(Object message) {

        // Add the response to the queue
        messageQueue.add(message);

        // Notify the thread that an update happened
        synchronized (notifyLock) {
            notifyLock.notify();
        }
    }

    /**
     *  Adds an event processor to the beginning of the notification chain
     *
     *  @param eventProcessor the event processor to add
     */
    public void addEventProcessor(EventProcessor eventProcessor) {
        synchronized (processorListLock) {
            processorList.add(eventProcessor);
        }
    }

    /**
     *  Removes an event processor from the notification chain
     *
     *  @param eventProcessor the event processor to remove
     *  @returns the eventProcessor instance that was removed.  null is returned if the passed eventProcessor was not found
     */
    public Object removeEventProcessor(EventProcessor eventProcessor) {
        synchronized (processorListLock) {
            int pos = processorList.indexOf(eventProcessor);
            if (pos >= 0) {
                return processorList.remove(pos);
            } else {
                return null;
            }
        }
    }

    /**
     *  Dispatches the event to all event processors in reverse order (ie last in, first notified)
     *
     *  @param event the event that will be sent to all processors
     */
    private synchronized void dispatchMessage(Object event) {
        synchronized (processorListLock) {
            for (int x = processorList.size() - 1; x >= 0; x--) {
                ((EventProcessor) processorList.get(x)).processEvent(event);
            }
        }
    }
}


/* @(#)DispatchThread.java */
