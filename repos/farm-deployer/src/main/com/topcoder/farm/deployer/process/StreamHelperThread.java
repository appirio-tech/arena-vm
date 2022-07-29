/*
 * StreamHelperThread.java
 *
 * Created on February 8, 2005, 3:34 PM
 */

package com.topcoder.farm.deployer.process;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author rfairfax
 */
 public class StreamHelperThread extends Thread {
     private final Log log  = LogFactory.getLog(StreamHelperThread.class);
     private volatile StringBuffer buffer;
     private InputStream streamToRead;
     private int maxSize;

     private volatile boolean quit = false;
     private volatile boolean done = false;

     public void quit() {
         quit = true;
     }

     public String getString() {
         return buffer.toString();
     }

     public StreamHelperThread(InputStream i) {
         this(i, new StringBuffer());
     }
     
     public StreamHelperThread(InputStream i, StringBuffer buffer) {
         this(i, buffer, Integer.MAX_VALUE);
     }
     
     public StreamHelperThread(InputStream i, int maxSize) {
         this(i, new StringBuffer(), maxSize);
     }
     
     public StreamHelperThread(InputStream i, StringBuffer buffer, int maxSize) {
         streamToRead = i;
         this.buffer = buffer;
         this.maxSize =maxSize;
         start();
     }

     public boolean isDone() {
         return done;
     }

     public void run() {
         log.debug("Stream reading started");
         try {
             while (!quit) {
                 readWhileAvailable();
                 Thread.sleep(50);
             }
             readWhileAvailable();
         } catch (Exception e) {
         }
         log.debug("Stream reading ended");
         done = true;
     }

    private void readWhileAvailable() throws IOException {
        while (streamToRead.available() > 0) {
             char c = (char) streamToRead.read();
             if (buffer.length() < maxSize) {
                 buffer.append(c);
             }
        }
    }
    
    public String drainContents() {
        String result;
        synchronized (buffer) {
            result = buffer.toString();
            buffer.setLength(0);
        }
        return result;
    }
}
