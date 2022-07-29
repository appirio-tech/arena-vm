package com.topcoder.client.contestMonitor.view.gui;

public class TimeOutException extends Exception {

    public TimeOutException() {
        super("Timed out waiting for response");
    }
}


