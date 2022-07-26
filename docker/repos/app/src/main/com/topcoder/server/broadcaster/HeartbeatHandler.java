package com.topcoder.server.broadcaster;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Category;

import com.topcoder.server.common.replayMessages.HeartbeatMessage;

final class HeartbeatHandler {

    private static final long PERIOD = 1000;

    private final Timer timer = new Timer();
    private final BroadcasterPoint point;
    private final Category cat;

    private long lastTime = System.currentTimeMillis();

    HeartbeatHandler(BroadcasterPoint point, String name) {
        this.point = point;
        cat = Category.getInstance(name + ".HeartbeatHandler");
        timer.scheduleAtFixedRate(new HeartbeatTask(), 0, PERIOD);
    }

    synchronized void receiveHeartbeat(HeartbeatMessage heartbeatMessage) {
        lastTime = System.currentTimeMillis();
    }

    private synchronized void checkHeartbeat() {
        long fromLast = System.currentTimeMillis() - lastTime;
        if (fromLast >= 3 * PERIOD) {
            point.lostHeartbeat();
        }
    }

    void stop() {
        timer.cancel();
    }

    final void info(String msg) {
        cat.info(msg);
    }

    private class HeartbeatTask extends TimerTask {

        public void run() {
            point.send(new HeartbeatMessage());
            checkHeartbeat();
        }

    }

}
