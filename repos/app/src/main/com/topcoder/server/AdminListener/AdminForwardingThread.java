package com.topcoder.server.AdminListener;

import com.topcoder.shared.util.StoppableThread;
import com.topcoder.server.util.TCLinkedQueue;
import com.topcoder.server.AdminListener.AdminConstants;
import com.topcoder.server.AdminListener.request.TestConnection;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import java.io.*;

public class AdminForwardingThread {

    private class InputThread implements StoppableThread.Client {

        public void cycle() throws InterruptedException {
            try {
                //since this is just forwarding, we want to simply throw away the responses
                socketInputStream.readObject();
            } catch (Exception e) {
                e.printStackTrace();
                stop();
            }
        }
    }

    private class OutputThread implements StoppableThread.Client {

        public void cycle() throws InterruptedException {
            try {
                Object request = queue.take();
                socketOutputStream.writeObject(request);
                System.out.println("forwarded: " + request);
            } catch (IOException e) {
                e.printStackTrace();
                stop();
            }
        }
    }

    private SSLSocket socket;
    private ObjectOutputStream socketOutputStream;
    private ObjectInputStream socketInputStream;
    private SSLSocketFactory sslFact;
    private TCLinkedQueue queue;
    private StoppableThread inputThread;
    private StoppableThread outputThread;
    private boolean going;

    public AdminForwardingThread(String host, int port) throws IOException {
        super();
        try {
            System.out.println("creating new admin forwarding thread");
            queue = new TCLinkedQueue();
            sslFact = (SSLSocketFactory) SSLSocketFactory.getDefault();
            socket = (SSLSocket) sslFact.createSocket(host, port);
            socket.setEnabledCipherSuites(socket.getSupportedCipherSuites());
            System.out.println("socket created");
            socketOutputStream = new ObjectOutputStream(socket.getOutputStream());
            socketInputStream = new ObjectInputStream(socket.getInputStream());
            System.out.println("streams created");
            //TestConnection is just to make sure its working
            socketOutputStream.writeObject(new TestConnection());
            inputThread = new StoppableThread(new InputThread(), "AdminInputThread");
            outputThread = new StoppableThread(new OutputThread(), "AdminOutputThread");
        } catch (IOException e) {
            stop();
            throw e;
        }
        inputThread.start();
        outputThread.start();
        going = true;
    }

    public void forward(Object request) {
        if (going) {
            queue.put(request);
        }
    }

    public void stop() {
        going = false;
        try {
            inputThread.stopThread();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            outputThread.stopThread();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isGoing() {
        return going;
    }
}
