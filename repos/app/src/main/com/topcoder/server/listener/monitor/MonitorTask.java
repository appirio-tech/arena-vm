package com.topcoder.server.listener.monitor;

import java.util.TimerTask;

public class MonitorTask extends TimerTask {

    private final MonitorProcessor processor;

    public MonitorTask(MonitorProcessor processor) {
        this.processor = processor;
    }

    public void run() {
        processor.send();
    }

}
