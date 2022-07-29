package com.topcoder.server.processor;

import com.topcoder.server.common.Tracking;
import com.topcoder.server.services.CoreServices;
import com.topcoder.server.util.TCLinkedQueue;
import com.topcoder.shared.util.StoppableThread;

final class TrackingQueue implements StoppableThread.Client {

    //holds all unprocessed requests
    private final TCLinkedQueue queue = new TCLinkedQueue();
    private final StoppableThread thread = new StoppableThread(this, "TrackingQueue");

    void start() {
        thread.start();
    }

    void stop() {
        try {
            thread.stopThread();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void cycle() throws InterruptedException {
        // takes Request objects off the queue and transforms them into Tracking objects and sends them to the EJB
        Tracking t = (Tracking) queue.take();
        CoreServices.track(t);
    }

    //adds a request for processing
    void addTracking(Tracking t) {
        queue.put(t);

    }

}