package com.topcoder.client.contestApplet.runner;

/**
 * prod.java
 *
 * Created June 4, 2001
 */

import com.topcoder.client.contestApplet.*;
//import com.topcoder.client.contestApplet.frames.*;
import java.awt.event.*;

//import javax.swing.*;
//import com.topcoder.client.contestApplet.listener.*;

/**
 * Runs the ContestApplet as an application.
 *
 * @author Alex Roman
 * @version
 */

public final class prod {


  ////////////////////////////////////////////////////////////////////////////////
  public static void main(String args[])
  ////////////////////////////////////////////////////////////////////////////////
  {
      String host = "www.topcoder.com", port = "5001";
      String tunnelLocation = "http://arena2.topcoder.com/servlet/com.topcoder.utilities.HTTPTunnelling.Tunnel?host=listener+port=5001";
      String roomVersion = "TopCoder";
      String destinationHost = "";
      boolean poweredByView = false;
      String sponsorName = "TopCoder";

    ContestApplet ca = new ContestApplet(host, (Integer.valueOf(port)).intValue(),tunnelLocation,roomVersion, destinationHost, poweredByView, sponsorName);
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
