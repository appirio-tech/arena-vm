/*
 * TimeoutThread.java
 *
 * Created on February 16, 2005, 1:36 PM
 */

package com.topcoder.services.tester.invoke;

import java.lang.Thread;
import com.topcoder.shared.util.logging.Logger;

/**
 *
 * @author rfairfax
 */
public class TimeoutThread extends Thread {
    
    private boolean quit = false;
    private long timeout = 0;
    private Thread threadToInterrupt = null;
    private static Logger trace = Logger.getLogger(TimeoutThread.class);
    
    public void quit() {
       if(!quit) {
           quit = true;
           this.interrupt();
       }
    }
    
    public TimeoutThread(Thread t, long timeout) {
        threadToInterrupt = t;
        this.timeout = timeout;
    }
    
    public void run() {
        try {
            Thread.sleep(timeout);
        } catch(InterruptedException ie) {
            //process finished
            trace.debug("TIMEOUT KILLED");
            return;
        }
        
        if(!quit) {
            threadToInterrupt.interrupt();
            quit = true;
        }
    }
    
}
