package com.topcoder.client.mpsqasApplet.controller.defaultimpl;

import com.topcoder.client.mpsqasApplet.controller.MovingRoomController;
import com.topcoder.client.mpsqasApplet.messaging.IMoveRequestProcessor;
import com.topcoder.client.mpsqasApplet.model.MovingRoomModel;
import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;
import com.topcoder.client.mpsqasApplet.view.MovingRoomView;

/**
 * The default implementation of the Moving Room Controller.  When it is
 * taken off hold, a thread is started to count 10 seconds.  When the
 * thread is done, the move times out, and an internal request is made
 * to load the Foyer Room.  If the controller is put back on hold
 * before the 10 seconds are up, the thread is interrupted so it will
 * not make the Foyer Room request.
 */
public class MovingRoomControllerImpl
        implements MovingRoomController, Runnable {

    private boolean keepRunning;
    private static final int TIME_OUT_MILLIS = 10000;
    private MovingRoomModel model;
    private MovingRoomView view;
    private Thread counter;

    /**
     * Stores the model and view.
     */
    public void init() {
        keepRunning = false;
        model = MainObjectFactory.getMovingRoomModel();
        view = MainObjectFactory.getMovingRoomView();
    }

    /**
     * Interrupts the counter thread.
     */
    public void placeOnHold() {
        keepRunning = false;
        if (counter != null) {
            counter.interrupt();
        }
    }

    /**
     * Starts the counter thread.
     */
    public void takeOffHold() {
        if (keepRunning) {
            keepRunning = false;
            if (counter != null) {
                counter.interrupt();

                //give it time to stop;
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                }
            }
        }

        keepRunning = true;
        counter = new Thread(this);
        counter.start();
    }

    /**
     * Counts 10 seconds, if 10 seconds are up and it has not been interrupted,
     * makes a request to load the Foyer Room.
     */
    public void run() {
        int count = 0;
        boolean wasInterrupted = false;
        while (!wasInterrupted && keepRunning && count < 10) {
            count++;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException interruptedexception) {
                wasInterrupted = true;
            }
        }

        if (keepRunning && !wasInterrupted) {
            keepRunning = false;
            MainObjectFactory.getIStatusMessageRequestProcessor().addMessage(
                    "Timed out waiting for move response.", true);
            MainObjectFactory.getIMoveRequestProcessor().loadFoyerRoom();
        }
    }
}
