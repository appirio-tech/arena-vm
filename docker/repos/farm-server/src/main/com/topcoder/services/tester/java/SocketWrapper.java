/*
 * SocketWrapper.java
 *
 * Created on February 2, 2006, 12:09 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.topcoder.services.tester.java;

import java.net.*;
import java.io.*;

/**
 * @author cstjohn
 */
public class SocketWrapper {
    private static final int SIZE = 100000;
    private Socket sock;
    private BufferedInputStream inputStream;
    private BufferedOutputStream outputStream;
    
    public SocketWrapper(Socket s) throws IOException {
        sock = s;
        sock.setTcpNoDelay(true);
        outputStream = new BufferedOutputStream(s.getOutputStream());
        inputStream = new BufferedInputStream(s.getInputStream());
    }
    
    public BufferedInputStream getInputStream() { return inputStream; }
    public BufferedOutputStream getOutputStream() { return outputStream; }
    
    public void close() throws IOException {
        outputStream.flush();
        sock.close();
    }
}
