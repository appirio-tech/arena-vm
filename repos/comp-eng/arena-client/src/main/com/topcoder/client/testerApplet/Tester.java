package com.topcoder.client.testerApplet;

import java.io.IOException;
import java.util.Random;

import com.topcoder.client.connectiontype.ConnectionType;
import com.topcoder.netCommon.io.ClientConnector;
import com.topcoder.netCommon.io.ClientConnectorFactory;
import com.topcoder.netCommon.io.ClientSocket;
import com.topcoder.netCommon.testerMessages.PingRequest;
import com.topcoder.netCommon.testerMessages.PongResponse;

public class Tester {
    private ClientSocket socket;
    private Random random = new Random();
    private volatile int result = 0;

    public Tester(String host, int port, String tunnel, ConnectionType type, boolean useSSL) throws IOException {
        ClientConnector connector;
        if (type.isTunneled()) {
            connector = ClientConnectorFactory.createTunneledConnector(tunnel, useSSL);
        } else {
            connector = ClientConnectorFactory.createSocketConnector(host, port, useSSL);
        }
        socket = new ClientSocket(connector, new TesterCSHandler());
    }

    public synchronized void sendAndWait(int length, final int messages) throws Exception {
        final byte[] payload = new byte[length];
        random.nextBytes(payload);
        final IOException[] ee = new IOException[] {null};
        Thread thread = new Thread(new Runnable() {
                public void run() {
                    int count = messages;
                    while (count > 0) {
                        try {
                            PongResponse response = (PongResponse) socket.readObject();
                            if (response.getPayload().length != payload.length) {
                                throw new IOException("Failed: Payload differs");
                            }
                        } catch (IOException e) {
                            ee[0] = e;
                            result = -1;
                            break;
                        } catch (Exception e) {
                            ee[0] = new IOException(e.toString());
                            result = -1;
                            break;
                        }
                        count--;
                    }
                    
                    synchronized (Thread.currentThread()) {
                        result = 1;
                        Thread.currentThread().notify();
                    }
                }
            });

        thread.start();
        for(int j=0; j < messages && result == 0; j++) {
            PingRequest request = new PingRequest(payload);
            socket.writeObject(request);
        }

        try {
            synchronized (thread) {
                if (result == 0) {
                    thread.wait(5000);
                }
            }
            // If not interrupted. timed-out.
            thread.interrupt();
            if (result == 0) {
                throw new IOException("Timed-out.");
            }
            if (result == -1) throw ee[0];
        } catch (InterruptedException e) {
        } finally {
            try {
                close();
            } catch (IOException e) {
            }
        }
    }

    public void close() throws IOException {
        ClientSocket s = this.socket;
        if (s != null) {
            s.close();
            this.socket = null;
        }
    }
}
