package com.topcoder.client.contestApplet.runner;

/**
 * generic.java
 *
 * Created June 4, 2001
 */

import java.awt.event.*;
//import javax.swing.*;
import com.topcoder.client.contestApplet.ContestApplet;


/**
 * Runs the ContestApplet as an application.
 *
 * @author Alex Roman
 * @version
 */

public final class generic {
    ////////////////////////////////////////////////////////////////////////////////
    public static void main(String args[])
            ////////////////////////////////////////////////////////////////////////////////
    {
        String host = "",
                port = "",
                tunnel = "",
                company = "TopCoder",
                destinationHost = "",
                poweredByView = "false",
                sponsorName = "";

        if (args.length == 7) {
            host = args[0];
            port = args[1];
            tunnel = args[2];
            company = args[3];
            sponsorName = args[4];
            poweredByView = args[5];
            destinationHost = args[6];
        } else if (args.length == 6) {
            host = args[0];
            port = args[1];
            tunnel = args[2];
            company = args[3];
            sponsorName = args[4];
            poweredByView = args[5];
        } else if (args.length == 5) {
            host = args[0];
            port = args[1];
            tunnel = args[2];
            company = args[3];
            sponsorName = args[4];
        } else if (args.length == 4) {
            host = args[0];
            port = args[1];
            tunnel = args[2];
            company = args[3];
        } else if (args.length == 3) {
            host = args[0];
            port = args[1];
            tunnel = args[2];
        } else if (args.length == 2) {
            host = args[0];
            port = args[1];
        } else {
            System.out.println("USAGE: java -cp ContestApplet.jar jmaContestApplet.runner.generic 'host' 'port'");
            System.exit(1);
        }

        ContestApplet ca = new ContestApplet(host,
                Integer.parseInt(port),
                tunnel,
                company,
                destinationHost,
                new Boolean(poweredByView).booleanValue(), sponsorName);
        ca.getMainFrame().addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }

            public void windowClosed(WindowEvent e) {
                System.exit(0);
            }
        });
        ca.getMainFrame().show();
    }
}
