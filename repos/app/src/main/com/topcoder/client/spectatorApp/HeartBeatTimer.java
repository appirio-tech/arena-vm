/**
 * HeartBeatTimer.java
 *
 * Description:		A heart beat timer that broadcasts timer updates every second
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import com.topcoder.client.spectatorApp.event.TimerAdapter;
import com.topcoder.client.spectatorApp.event.TimerEvent;
import com.topcoder.client.spectatorApp.event.TimerListener;
import com.topcoder.client.spectatorApp.event.TimerSupport;
import com.topcoder.client.spectatorApp.netClient.SpectatorEventProcessor;

public class HeartBeatTimer {

    /** Support class for TimerListeners */
    private TimerSupport timerSpt = new TimerSupport();

    /** The singleton instance of the heartbeat timer */
    private static HeartBeatTimer heartBeatTimer;

    /** Time left in current phase */
    private int timeLeft = 0;

    /** Timer lock */
    private Object timeLock = new Object();

    /** Internal timer object */
    private Timer timer;

    /**
     * HeartBeatTimer Constructor.  The timer implements a singleton pattern.  Please use HeartBeatTimer.getInstance() to get an instance of it.
     */
    private HeartBeatTimer() {
        // Listen for timer updates from the server
        SpectatorEventProcessor.getInstance().addTimerListener(new TimerHandler());

        // Create a new timer to click off every second
        timer = new Timer(1000, new TimerActionHandler());
        timer.setRepeats(true);
        timer.setCoalesce(true);
        timer.start();
    }

    /**
     * Returns the singleton instance of the heartbeattimer
     *
     * @return HeartBeatTimer
     */
    public static synchronized HeartBeatTimer getInstance() {
        if (heartBeatTimer == null) heartBeatTimer = new HeartBeatTimer();
        return heartBeatTimer;
    }

    /**
     * Returns the time left at the moment
     *
     * @return the time left
     */
    public int getTimeLeft() {
        return timeLeft;
    }

    /** Class that handles timer update events */
    private class TimerHandler extends TimerAdapter {

        /**
         * Updates the internal countdown from the timerupdate event sent from the server
         */
        public void timerUpdate(TimerEvent evt) {
            synchronized (timeLock) {
                timeLeft = evt.getTimeLeft();
            }
        }
    }

    /** Class reponsible for timer updates */
    private class TimerActionHandler implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            // Update the seconds elapsed
            synchronized (timeLock) {
                timeLeft--;
                TimerEvent timeEvent = new TimerEvent(this, timeLeft);
                timerSpt.fireTimerUpdate(timeEvent);
            }
        }
    }

    /**
     *  Adds a listener of type com.topcoder.client.spectatorApp.event.TimerListener
     *
     *  @param listener the listener to add
     */
    public synchronized void addTimerListener(TimerListener listener) {
        timerSpt.addTimerListener(listener);
    }

    /**
     *  Removes a listener of type com.topcoder.client.spectatorApp.event.TimerListener
     *
     *  @param listener the listener to remove
     */
    public synchronized void removeTimerListener(TimerListener listener) {
        timerSpt.removeTimerListener(listener);
    }
}


/* @(#)HeartBeatTimer.java */
