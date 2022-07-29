/*
 * ThreadedReader.java
 *
 * Created on February 1, 2006, 4:55 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.topcoder.services.util;

import java.io.IOException;
import java.io.Reader;

/**
 *
 * @author rfairfax
 */
public class ThreadedReader extends Reader {
    
    private Reader source;
    private final int bufferSize = 1 * 1024 * 1024; //1meg increments
    private char[] buffer;
    private int pos = 0;
    private int length = 0;
    private boolean closed = false;
    
    /** Creates a new instance of ThreadedReader */
    public ThreadedReader(Reader r) {
        super(r);
        
        this.source = r;
        buffer = new char[bufferSize];
        new ReaderThread().start();
    }

    public int read(char[] c, int off, int len) throws IOException {
        synchronized(source) {
            if(closed && pos == length)
                throw new IOException("Stream Closed");
            //figure out how much we have, take min of that / what they want
            int n = Math.min(len, length - pos);
            
            System.arraycopy(buffer, pos, c, off, n);
            pos += n;
            
            //System.out.println("ALLOWING READ OF " + n);
            
            return n;
        }
    }

    public void close() throws IOException {
        closed = true;
        source.close();
    }
    
    private class ReaderThread extends Thread {
        public void run() {
            try {
                //int cnt = 0;
                //System.out.println("STARTING");
                while(!closed) {
                    do {
                        //pull a character off
                        synchronized(source) {
                            //are we at the end
                            if(length == buffer.length) {
                                //System.out.println("RESIZE");
                                //add bufferSize to the buffer
                                char[] newBuffer = new char[buffer.length + bufferSize];
                                System.arraycopy(buffer, pos, newBuffer, 0, length - pos);
                                length -= pos;
                                pos = 0;
                                buffer = newBuffer;
                            }

                            int c = source.read();
                            if(c != -1) {
                                //System.out.println("CHAR: " + (char)c);
                                buffer[length] = (char)c;
                                length++;
                                //if(length % 100 == 0)
                                    //System.out.println("READ:" + length);
                            } else {
                                closed = true;
                            }                     
                        }
                    } while(source.ready());
                    //cnt++;
                    //if(cnt % 100 == 0)
                        //System.out.println("NODATA");
                    Thread.sleep(50);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            //System.out.println("ENDING");
        }
    }
    
}
