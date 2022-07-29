/**
 * SystemTestDelayer.java
 *
 * Description:		Provides a minimum delay between releases of system test messages
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp;

import java.util.ArrayList;

import com.topcoder.client.spectatorApp.event.ProblemResultAdapter;
import com.topcoder.client.spectatorApp.event.ProblemResultEvent;
import com.topcoder.client.spectatorApp.event.ProblemResultListener;
import com.topcoder.client.spectatorApp.event.ProblemResultSupport;
import com.topcoder.client.spectatorApp.event.TimerAdapter;
import com.topcoder.client.spectatorApp.event.TimerEvent;
import com.topcoder.client.spectatorApp.netClient.SpectatorEventProcessor;

public class SystemTestDelayer {

    /** The singleton instance of this class */
    private static SystemTestDelayer SystemTestDelayer = null;

    /** Time (in seconds) to delay */
    private int delayTime = 0;

    /** Time (in seconds) to delay */
    private int timeSinceLastMessage = 0;

    /** Arraylist for the messages */
    private ArrayList msgs = new ArrayList();

    /** Lock for the messages */
    private Object msgsLock = new Object();

    /** Support for problem results */
    private ProblemResultSupport problemResultSpt = new ProblemResultSupport();

    /** Support for listeners

     /**
     * Private Constructor for the phase tracker
     */
    private SystemTestDelayer() {
        SpectatorEventProcessor.getInstance().addProblemResultListener(new ProblemResultHandler());
        HeartBeatTimer.getInstance().addTimerListener(new TimerHandler());
    }


    /**
     * Fires off any pending messages
     */
    private void fireMessage() {
        synchronized (msgsLock) {
            // Are we beyond the delay since the last message
            if (timeSinceLastMessage >= delayTime) {
                // Do we have a message to send
                if (msgs.size() > 0) {
                    ProblemResultEvent msg = (ProblemResultEvent) msgs.remove(0);
                    problemResultSpt.fireSystemTested(msg);
                    //System.out.println(">>> Sending Message: " + msg);
                    // Reset the time last sent
                    timeSinceLastMessage = 0;
                }
            } else {
                // Update the time
                timeSinceLastMessage++;
                //if (msgs.size() > 0) System.out.println(">>> " + msgs.size() + " messages are waiting: " + (delayTime - timeSinceLastMessage));
            }

        }
    }

    /**
     * Returns the singleton instance of the phase tracker
     *
     * @returns the singleton instance of the phase tracker
     */
    public synchronized static final SystemTestDelayer getInstance() {
        if (SystemTestDelayer == null) SystemTestDelayer = new SystemTestDelayer();
        return SystemTestDelayer;
    }

    /**
     *  Adds a listener of type ProblemResultListener
     *
     *  @param listener the listener to add
     */
    public synchronized void addProblemResultListener(ProblemResultListener listener) {
        problemResultSpt.addProblemResultListener(listener);
    }

    /**
     *  Removes a listener of type ProblemResultListener
     *
     *  @param listener the listener to remove
     */
    public synchronized void removeProblemResultListener(ProblemResultListener listener) {
        problemResultSpt.removeProblemResultListener(listener);
    }


    /** Handler of timer messages messages */
    private class TimerHandler extends TimerAdapter {

        public void timerUpdate(TimerEvent e) {
            fireMessage();
        }
    }

    /** Handler of system test messages */
    private class ProblemResultHandler extends ProblemResultAdapter {

        public void systemTested(ProblemResultEvent evt) {
            synchronized (msgsLock) {
                msgs.add(evt);
            }
        }
    }


}


/* @(#)SystemTestDelayer.java */
