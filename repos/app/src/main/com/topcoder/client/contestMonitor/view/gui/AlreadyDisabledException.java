package com.topcoder.client.contestMonitor.view.gui;

public class AlreadyDisabledException extends Exception {

    public AlreadyDisabledException() {
        super("UI is already disabled pending response");
    }
}


