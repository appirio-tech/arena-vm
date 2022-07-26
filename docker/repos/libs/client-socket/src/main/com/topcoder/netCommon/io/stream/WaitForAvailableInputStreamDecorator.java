/*
 * WaitForAvailableInputStreamDecorator
 * 
 * Created 06/06/2006
 */
package com.topcoder.netCommon.io.stream;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;

/**
 * Decorator class used to delay the actual reading/skipping on the decorated inputstream 
 * until data is available. 
 * 
 * NOTE: The <code>available</code> method behaves in different way depending on the actual 
 * implementation of the InputStream.
 *   
 * @autor Diego Belfer (Mural)
 * @version $Id$
 */
public class WaitForAvailableInputStreamDecorator extends FilterInputStream {

    /**
     * Creates a new WaitForAvailableInputStreamDecorator for the specified
     * InputStream
     * 
     * @param in The decorated InputStream. 
     */
    public WaitForAvailableInputStreamDecorator(InputStream in) {
        super(in);
    }

    /**
     * Verifies that data is available to be read. 
     * Then delegates to decorated InputStream
     * 
     * @see java.io.InputStream#read()
     */
    public int read() throws IOException {
        waitForAvailable();
        return in.read();
    }

    /**
     * Verifies that data is available to be read. 
     * Then delegates to decorated InputStream
     * 
     * @see java.io.InputStream#read(byte[], int, int)
     */
    public int read(byte[] b, int off, int len) throws IOException {
        waitForAvailable();
        return in.read(b, off, len);
    }

    /**
     * @see java.io.InputStream#skip(long)
     */
    public long skip(long n) throws IOException {
        waitForAvailable();
        return in.skip(n);
    }
    

    /**
     * Verifies that the decorated input stream has available data and then returns
     * 
     * @throws IOException if decorated handler throws this exception
     * @throws InterruptedIOException if the thread was interrupted while waiting for available data 
     */
    private void waitForAvailable() throws IOException, InterruptedIOException {
        while (in.available() == 0) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new InterruptedIOException(e.getMessage());
            }
        }
    }
}