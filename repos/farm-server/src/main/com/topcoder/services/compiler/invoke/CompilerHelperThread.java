package com.topcoder.services.compiler.invoke;
import java.io.InputStream;

import com.topcoder.shared.util.logging.Logger;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

/**
 *
 * @author rfairfax
 */
public class CompilerHelperThread extends Thread {
    
    private ByteArrayOutputStream str = new ByteArrayOutputStream();
    
    private static Logger trace = Logger.getLogger(CompilerHelperThread.class);
    
    private InputStream obj = null;
    private int max = 3000000;
    
    private boolean quit = false;
    
    private boolean done = false;
    
    public void quit() {
        quit = true;
    }
    
    public void appendTo(OutputStream os) {
        try {
            str.writeTo(os);
        } catch (Exception e) {
            trace.error("Stream write failed", e);
        }
    }
    
    public byte[] getStreamContents() {
        return str.toByteArray();
    }
    
    public CompilerHelperThread(InputStream i) {
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
                    int b  = obj.read();
                    str.write(b);
                }
                Thread.sleep(50);
            } catch(Exception e) {
                done = true;
                return;
            }
        }
        //get the rest
        try {
            while(obj.available() > 0 ) {
                int b = obj.read();
                str.write(b);
            }
        } catch(Exception e) {
            done = true;
            return;
        }
        done = true;
    }
}
