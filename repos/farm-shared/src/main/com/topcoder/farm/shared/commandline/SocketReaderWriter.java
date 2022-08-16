/*
 * SocketReaderWriter
 * 
 * Created 09/04/2006
 */
package com.topcoder.farm.shared.commandline;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * ReaderWriter that uses a socket to read commands from and 
 * the same socket to write messages to user 
 * 
 * @author Diego Belfer (Mural)
 * @version $Id$
 */
public class SocketReaderWriter implements ReaderWriter {
    private CommandLineInterpreter interpreter;
    private int port;
    private ServerSocket server;
    private Socket clSocket;
    private BufferedReader reader;
    private PrintStream printStream;
    
    public SocketReaderWriter(CommandLineInterpreter interpreter, int port) {
        this.interpreter = interpreter;
        this.port = port;
    }
    
    public void initialize() throws IOException {
        server = new ServerSocket(port);
    }
    
    public PrintStream getPrintStream() {
        return printStream;
    }

    public String readLine() throws IOException {
        waitForConnection();
        String line;
        try {
            line = reader.readLine();
        } catch (IOException e) {
            releaseConnection();
            throw e;
        }
        return line;
    }

    private void releaseConnection() {
        interpreter.sessionFinished();
        try {
            if (reader != null) reader.close();
        } catch (Exception e1) {
        }
        reader = null;
        try {
            if (printStream != null) printStream.close();
        } catch (Exception e1) {
        }
        printStream = null;
        try {
            if (clSocket != null) clSocket.close();
        } catch (Exception e1) {
        }
        clSocket = null;
    }

    private void waitForConnection() throws IOException {
        if (clSocket == null) {
            clSocket = server.accept();
            reader = new BufferedReader(new InputStreamReader(clSocket.getInputStream()));
            printStream = new PrintStream(clSocket.getOutputStream());
            interpreter.newSessionStarted();
            
        }
    }

    public void release() {
        releaseConnection();
        try {
            server.close();
        } catch (Exception e) {
        }
        server = null;
    }
    
    public void reset() {
        releaseConnection();
    }
}