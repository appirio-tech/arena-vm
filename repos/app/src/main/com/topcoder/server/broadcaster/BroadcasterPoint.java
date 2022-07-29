package com.topcoder.server.broadcaster;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Category;

import com.topcoder.netCommon.io.ClientSocket;
import com.topcoder.server.common.replayMessages.BroadcasterMessage;
import com.topcoder.server.common.replayMessages.ConfirmationMessage;
import com.topcoder.server.common.replayMessages.HeartbeatMessage;
import com.topcoder.server.common.replayMessages.ReplayCSHandler;
import com.topcoder.server.util.TCLinkedQueue;
import com.topcoder.shared.util.SerialIntGenerator;
import com.topcoder.shared.util.StoppableThread;

abstract class BroadcasterPoint implements StoppableThread.Client, SenderInterface {

    /**
     * The time to sleep after an unsuccessful connection.
     */
    private static final int CONNECT_INTERVAL = 50;
    private static final int SO_TIMEOUT = 50;

    private final Category cat;
    private final StoppableThread connectThread;
    private final TCLinkedQueue sendQueue = new TCLinkedQueue();
    private final TCLinkedQueue recvQueue = new TCLinkedQueue();
    private final String name;
    private final boolean isHeartbeat;
    private final SerialIntGenerator idGenerator = new SerialIntGenerator();
    private final List unconfirmedList = Collections.synchronizedList(new LinkedList());

    private ClientSocket socket;
    private SenderInterface sender;
    private HeartbeatHandler heartbeatHandler;
    private boolean haveSignal;
    private Object monitor;
    private int prevID = -1;

    BroadcasterPoint(String name, boolean isHeartbeat) {
        this.name = name;
        this.isHeartbeat = isHeartbeat;
        cat = Category.getInstance(name);
        connectThread = new StoppableThread(this, name);
    }

    final int getUnconfirmedSize() {
        return unconfirmedList.size();
    }

    final void setSender(SenderInterface sender) {
        this.sender = sender;
    }

    public final void start() {
        internalStart();
        connectThread.start();
        info("started");
    }

    private Object sendDequeue() throws InterruptedException {
        return sendQueue.take();
    }

    private void recvEnqueue(Object obj) {
        recvQueue.put(obj);
    }

    private Object recvDequeue() throws InterruptedException {
        return recvQueue.take();
    }

    public final void stop() {
        info("stopping");
        try {
            connectThread.stopThread();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
        info("stopped");
    }

    abstract int getPort();

    abstract void internalStart();

    abstract boolean connect();

    abstract void shutdown() throws IOException;

    final void setSocket(Socket socket) {
        try {
            socket.setSoTimeout(SO_TIMEOUT);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        try {
            this.socket = new ClientSocket(socket, new ReplayCSHandler());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeObject(Object obj) throws IOException {
        socket.writeObject(obj);
    }

    private Object readObject() throws IOException {
        return socket.readObject();
    }

    private void disconnect() throws IOException {
        if (socket != null) {
            socket.close();
        }
    }

    Object receive() throws InterruptedException {
        return recvDequeue();
    }

    private void sendEnqueue(Object obj) {
        if (isHeartbeat && !(obj instanceof HeartbeatMessage) && !(obj instanceof ConfirmationMessage) &&
                !(obj instanceof BroadcasterMessage)) {
            BroadcasterMessage broadcasterMessage = new BroadcasterMessage(idGenerator.next(), obj);
            unconfirmedList.add(broadcasterMessage);
            obj = broadcasterMessage;
        }
        sendQueue.put(obj);
    }

    private void doRecvOperation() throws IOException {
        Object object = readObject();
        if (isHeartbeat) {
            if (object instanceof HeartbeatMessage) {
                heartbeatHandler.receiveHeartbeat((HeartbeatMessage) object);
                return;
            }
            if (object instanceof ConfirmationMessage) {
                ConfirmationMessage confirmationMessage = (ConfirmationMessage) object;
                if (unconfirmedList.size() <= 0) {
                    error("unconfirmedList.size()<=0, confirmation=" + confirmationMessage.getMessageID());
                    return;
                }
                BroadcasterMessage message = (BroadcasterMessage) unconfirmedList.remove(0);
                if (message.getMessageID() != confirmationMessage.getMessageID()) {
                    error("message IDs don't match, unconfirmed=" + message.getMessageID() +
                            ", confirmation=" + confirmationMessage.getMessageID());
                }
                return;
            }
            if (object instanceof BroadcasterMessage) {
                if (sender == null) {
                    throw new RuntimeException("sender is null?");
                }
                BroadcasterMessage broadcasterMessage = (BroadcasterMessage) object;
                object = broadcasterMessage.getMessage();
                int messageID = broadcasterMessage.getMessageID();
                if (messageID - 1 != prevID) {
                    error("messageIDs out of order, messageID=" + messageID + ", prevID=" + prevID);
                    return;
                }
                long sentTime = broadcasterMessage.getSentTime();
                sendEnqueue(new ConfirmationMessage(messageID, sentTime));
                prevID = messageID;
            }
        }
        if (sender == null) {
            recvEnqueue(object);
        } else {
            sender.send(object);
        }
    }

    public final void send(Object object) {
        sendEnqueue(object);
    }

    private void doSendOperation() throws IOException, InterruptedException {
        writeObject(sendDequeue());
    }

    final void info(String msg) {
        cat.info(msg);
    }

    final void error(String msg) {
        cat.error(msg);
    }

    public final void cycle() throws InterruptedException {
        boolean connected = connect();
        if (!connected) {
            Thread.sleep(CONNECT_INTERVAL);
            return;
        }
        if (unconfirmedList.size() > 0) {
            for (Iterator it = unconfirmedList.iterator(); it.hasNext();) {
                sendEnqueue(it.next());
            }
        }
        SendHandler sendHandler = null;
        RecvHandler recvHandler = null;
        haveSignal = true;
        try {
            monitor = new Object();
            sendHandler = new SendHandler(monitor, this);
            if (isHeartbeat) {
                heartbeatHandler = new HeartbeatHandler(this, name);
            }
            recvHandler = new RecvHandler(monitor, this);
            synchronized (monitor) {
                while (haveSignal && sendHandler.isRunning() && recvHandler.isRunning()) {
                    monitor.wait();
                }
            }
        } finally {
            if (recvHandler != null) {
                recvHandler.stop();
            }
            if (heartbeatHandler != null) {
                heartbeatHandler.stop();
                heartbeatHandler = null;
            }
            if (sendHandler != null) {
                sendHandler.stop();
            }
            try {
                disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        info("signal is lost");
    }

    final void lostHeartbeat() {
        info("lost heartbeat");
        haveSignal = false;
        synchronized (monitor) {
            monitor.notifyAll();
        }
    }

    private static final class SendHandler extends Handler {

        private SendHandler(Object monitor, BroadcasterPoint point) {
            super(point.name + ".SendHandler", monitor, point);
        }

        void doOperation() throws IOException, InterruptedException {
            getPoint().doSendOperation();
        }

    }

    private static final class RecvHandler extends Handler {

        private RecvHandler(Object monitor, BroadcasterPoint point) {
            super(point.name + ".RecvHandler", monitor, point);
        }

        void doOperation() throws IOException {
            getPoint().doRecvOperation();
        }

    }

    private static abstract class Handler implements StoppableThread.Client {

        private final StoppableThread thread;
        private final Object monitor;
        private final BroadcasterPoint point;

        private boolean running = true;

        private Handler(String name, Object monitor, BroadcasterPoint point) {
            this.monitor = monitor;
            this.point = point;
            thread = new StoppableThread(this, name);
            thread.start();
        }

        BroadcasterPoint getPoint() {
            return point;
        }

        abstract void doOperation() throws IOException, InterruptedException;

        final boolean isRunning() {
            return running;
        }

        public final void stop() {
            try {
                thread.stopThread();
            } catch (InterruptedException e) {
            }
        }

        private void stopRunning() {
            synchronized (monitor) {
                running = false;
                monitor.notifyAll();
            }
        }

        public final void cycle() throws InterruptedException {
            try {
                doOperation();
            } catch (InterruptedIOException e) {
            } catch (IOException e) {
                if (running) {
                    point.info("" + e);
                }
                stopRunning();
            }
        }

    }

}
