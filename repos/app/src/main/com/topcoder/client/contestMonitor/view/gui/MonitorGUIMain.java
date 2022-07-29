package com.topcoder.client.contestMonitor.view.gui;

//import java.io.IOException;
//import java.net.UnknownHostException;

import com.topcoder.client.contestMonitor.model.MonitorController;

public final class MonitorGUIMain {

    public static void main(String[] args) {
        try {
            new MonitorController(args);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // The login window will start the monitor frame once login is successful.
        // (The login window is started by the monitor controller because it has to
        // pass messages back to the window.)
        /*
        MonitorFrame frame=new MonitorFrame(controller);
        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */
    }

}
