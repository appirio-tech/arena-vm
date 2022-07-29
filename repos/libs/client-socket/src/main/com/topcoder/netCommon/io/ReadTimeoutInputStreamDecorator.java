/*
 * ReadTimeoutInputStreamDecorator
 * 
 * Created 03/20/2006
 */
package com.topcoder.netCommon.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.SocketTimeoutException;

/**
 * Decorator class used to add readTimeout capability on InputStream 
 * created using an UrlConnection of Java 1.4.
 * This decorator verifies that at least one byte is available 
 * to be read before invoking read method of the decorated InputStream
 *
 * @author Diego Belfer (Mural)
 * @version $Id$
 */

class ReadTimeoutInputStreamDecorator extends InputStream  {
    
    /**
     * Decorated InputStream
     */
    private InputStream decorated;
    
    /**
     * Time to wait for data to be available when read is invoked
     */
    private int readTimeOut;

    /**
     * Creates a new ReadTimeoutInputStreamDecorator for the specified
     * InputStream
     * 
     * @param readTimeOut Time to wait for data to be available when read is invoked.
     * @param decorated The decorated InputStream. 
     */
    public ReadTimeoutInputStreamDecorator(int readTimeOut, InputStream decorated) {
        this.readTimeOut = readTimeOut;
        this.decorated = decorated;
    }

    /**
     * @see java.io.InputStream#available()
     */
    public int available() throws IOException {
        return decorated.available();
    }

    /**
     * @see java.io.InputStream#close()
     */
    public void close() throws IOException {
        decorated.close();
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        return decorated.equals(obj);
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return decorated.hashCode();
    }

    /**
     * @see java.io.InputStream#mark(int)
     */
    public void mark(int readlimit) {
        decorated.mark(readlimit);
    }

    /**
     * @see java.io.InputStream#markSupported()
     */
    public boolean markSupported() {
        return decorated.markSupported();
    }

    /**
     * Verifies that data is available to be read. 
     * Then delegates to decorated InputStream
     * 
     * @see java.io.InputStream#read()
     */
    public int read() throws IOException {
        verifyAvailable();
        return decorated.read();
    }

    /**
     * Verifies that data is available to be read. 
     * Then delegates to decorated InputStream
     * 
     * @see java.io.InputStream#read(byte[], int, int)
     */
    public int read(byte[] b, int off, int len) throws IOException {
        verifyAvailable();
        return decorated.read(b, off, len);
    }

    /**
     * Verifies that data is available to be read. 
     * Then delegates to decorated InputStream
     * 
     * @see java.io.InputStream#read(byte[])
     */
    public int read(byte[] b) throws IOException {
        verifyAvailable();
        return decorated.read(b);
    }

    /**
     * @see java.io.InputStream#reset()
     */
    public void reset() throws IOException {
        decorated.reset();
    }

    /**
     * @see java.io.InputStream#skip(long)
     */
    public long skip(long n) throws IOException {
        return decorated.skip(n);
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return decorated.toString();
    }

    /**
     * Verifies that the decorated input stream has available data.
     * 
     * @throws IOException if decorated handler throws this exception
     * @throws InterruptedIOException if the thread was interrupted while waiting for available data 
     * @throws SocketTimeoutException if after waiting readTimeOut no data is available to be read 
     */
    private void verifyAvailable() throws IOException, InterruptedIOException, SocketTimeoutException {
        if (decorated.available() == 0) {
            try {
                Thread.sleep(readTimeOut);
            } catch (InterruptedException e) {
                throw new InterruptedIOException(e.getMessage());
            }
        } 
        if (decorated.available() == 0) throw new SocketTimeoutException("Read Timeout");
    }
}