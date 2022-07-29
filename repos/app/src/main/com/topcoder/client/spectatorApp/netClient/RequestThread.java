/**
 * RequestThread.java
 *
 * Description:		Thread that will take requests and dispatch them to the registered connections.  This class is thread safe
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.netClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.topcoder.shared.netCommon.messages.Message;
import com.topcoder.shared.netCommon.messages.MessagePacket;

public class RequestThread extends Thread {

    /** Singleton instance of the thread */
    private static RequestThread requestThread;

    /** Lock used to coordinate notifying */
    private Object notifyLock = new Object();

    /** The client connection */
    private ConnectionProcessor ConnectionProcessor = null;

    /** Queue of event to dispatch */
    private List messageQueue = Collections.synchronizedList(new ArrayList());

    /** List of client connections */
    private ArrayList connectionList = new ArrayList();

    /** Lock used to coordinate ConnectionProcessor */
    private Object connectionLock = new Object();

    /** The unique id to identify a message packet */
    private int uniqueID = 0;

    /**
     *  Private Constructor.  The DispatchThread implements a singleton pattern.  Please use getInstance() to retrieve an instance of this class
     */
    private RequestThread() {
        // Start running our thread
        setDaemon(true);
        start();
    }


    /**
     *  Get's the instance of the dispatch thread.
     */
    public final static synchronized RequestThread getInstance() {
        if (requestThread == null) requestThread = new RequestThread();
        return requestThread;
    }


    /**
     *  Thread that will dispatch the events to the support handlers
     */
    public final void run() {

        synchronized (notifyLock) {

            // Keep going...
            while (true) {

                // If we have nothing in the response queue
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
                    // Dispatch the messages
                    dispatchMessage((Message) messageQueue.remove(0));
                }
            }
        }
    }


    /**
     *  Queue's a response to be dispatched
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
     *  Adds a client connection as a receiver of events
     *
     *  @param ConnectionProcessor the client connection
     *
     *  @see com.topcoder.client.spectatorApp.netClient.ConnectionProcessor
     */
    public void addConnectionProcessor(ConnectionProcessor ConnectionProcessor) {
        synchronized (connectionLock) {
            connectionList.add(ConnectionProcessor);
        }
    }

    /**
     *  Removes a client connection as a receiver of events
     *
     *  @param ConnectionProcessor the client connection
     *
     *  @see com.topcoder.client.spectatorApp.netClient.ConnectionProcessor
     */
    public Object removeConnectionProcessor(ConnectionProcessor ConnectionProcessor) {
        synchronized (connectionLock) {
            int pos = connectionList.indexOf(ConnectionProcessor);
            if (pos >= 0) {
                return connectionList.remove(pos);
            } else {
                return null;
            }
        }
    }

    /**
     *  Dispatches the message to all client connection in reverse order (ie last in, first notified)
     *
     *  @param message the message to be sent
     *  @see com.topcoder.shared.netCommon.messages.Message
     */
    private synchronized void dispatchMessage(Message message) {
        synchronized (connectionLock) {
            // Create a new message packet
            MessagePacket packet = new MessagePacket(uniqueID++);
            packet.add(message);
            for (int x = connectionList.size() - 1; x >= 0; x--) {
                ((ConnectionProcessor) connectionList.get(x)).sendMessage(packet);
            }


        }
    }

}


/* @(#)RequestThread.java */
