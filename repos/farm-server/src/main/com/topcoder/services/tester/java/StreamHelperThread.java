/*
 * StreamHelperThread.java
 *
 * Created on February 8, 2005, 3:34 PM
 */

package com.topcoder.services.tester.java;

import java.lang.Thread;
import java.io.InputStream;

import com.topcoder.shared.util.logging.Logger;
import java.lang.StringBuffer;

/**
 *
 * @author rfairfax
 */
 public class StreamHelperThread extends Thread {

    private StringBuffer str = new StringBuffer();

    private static Logger trace = Logger.getLogger(StreamHelperThread.class);

    private InputStream obj = null;
    private int max = 3000000;

    private volatile boolean quit = false;

    private volatile boolean done = false;

    public void quit() {
        quit = true;
    }

    public String getString() {
        return str.toString();
    }


    public StreamHelperThread(InputStream i) {
        obj = i;
        start();
    }

    public boolean isDone() {
        return done;
    }

    public void run() {
        while(!quit) {
            try {
                while(obj.available() > 0 ) {
                    char c = (char)obj.read();
                    str.append(c);
                    //trace.debug("READING:" + c);
                }
                Thread.sleep(50);
            } catch(Exception e) {
                trace.error("Exception reading stream", e);
                done = true;
                return;
            }
        }
        //get the rest
        try {
            while(obj.available() > 0 ) {
                char c = (char)obj.read();
                str.append(c);
                //trace.debug("READING:" + c);
            }
        } catch(Exception e) {
            trace.error("Exception reading stream", e);
            done = true;
            return;
        }
        done = true;
    }
}
