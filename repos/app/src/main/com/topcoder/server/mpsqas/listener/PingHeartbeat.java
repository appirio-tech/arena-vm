package com.topcoder.server.mpsqas.listener;

import com.topcoder.netCommon.mpsqas.communication.Peer;
import com.topcoder.netCommon.mpsqas.communication.message.PingResponse;

import java.util.List;

/**
 * Sends a ping response to every user at specified intervals.
 *
 * @author mitalub
 */
public class PingHeartbeat implements Runnable {

    //ms
    private static final int INTERVAL = 60000;

    private MPSQASProcessor processor;
    private Thread thread;

    /**
     * Starts the heartbeat thread.
     */
    public void init(MPSQASProcessor processor) {
        this.processor = processor;
        thread = new Thread(this);
        thread.start();
    }

    /**
     * Stops the heartbeat thread.
     */
    public void stop() {
        thread.interrupt();
    }

    /**
     * Every INTERVAL ms, sends a ping response to all logged in users.
     */
    public void run() {
        List peers;
        PingResponse response;
        while (!thread.isInterrupted()) {
            peers = processor.getPeers();
            response = new PingResponse();
            for (int i = 0; i < peers.size(); i++) {
                ((Peer) peers.get(i)).sendMessage(response);
            }
            try {
                Thread.sleep(INTERVAL);
            } catch (Exception e) {
            }
        }
    }
}
