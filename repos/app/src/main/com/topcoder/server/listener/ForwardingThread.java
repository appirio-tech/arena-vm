package com.topcoder.server.listener;

import java.io.IOException;

import com.topcoder.netCommon.contestantMessages.NetCommonSocketFactory;
import com.topcoder.netCommon.io.ClientSocket;
import com.topcoder.server.util.TCLinkedQueue;
import com.topcoder.shared.util.StoppableThread;

public class ForwardingThread {

    private class InputThread implements StoppableThread.Client {

        public void cycle() throws InterruptedException {
            try {
                //since this is just forwarding, we want to simply throw away the responses
                socket.readObject();
            } catch (IOException e) {
                e.printStackTrace();
                stop();
            }
        }
    }

    private class OutputThread implements StoppableThread.Client {

        public void cycle() throws InterruptedException {
            try {
                Object request = queue.take();
                socket.writeObject(request);
                System.out.println("forwarded: " + request);
            } catch (IOException e) {
                e.printStackTrace();
                stop();
            }
        }
    }

    private TCLinkedQueue queue;
    private ClientSocket socket;
    private StoppableThread inputThread;
    private StoppableThread outputThread;
    private boolean going;

    public ForwardingThread(String host, int port, long connectionID) throws IOException {
        super();
        try {
            queue = new TCLinkedQueue();
            socket = NetCommonSocketFactory.newClientSocket(host, port);
            inputThread = new StoppableThread(new InputThread(), "inputThread." + connectionID);
            outputThread = new StoppableThread(new OutputThread(), "outputThread." + connectionID);
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
