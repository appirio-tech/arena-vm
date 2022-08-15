package com.topcoder.client.testerApplet;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class TesterApplet extends JApplet {
    private String host;
    private int port;
    private String tunnel;

    public TesterApplet() {
        init(getParameter("host"), getParameter("port"), getParameter("tunnel"));
    }

    public TesterApplet(String[] args) {
        init(args[0], args[1], args[2]);
    }

    private void init(String host, String port, String tunnel) {
        if (host == null) {
            this.host = "www.topcoder.com";
        } else {
            this.host = host;
        }
        if (port == null) {
            this.port = 5001;
        } else {
            try {
                this.port = Integer.parseInt(port);
            } catch (NumberFormatException e) {
                this.port = 5001;
            }
        }
        if (tunnel == null) {
            this.tunnel = "http://tunnel1.topcoder.com/servlets-examples/tunnel?host~newlistener&port~5001";
        } else {
            this.tunnel = tunnel;
        }

        JFrame frame = new MainFrame(this.host, this.port, this.tunnel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        if (args.length != 3 && args.length != 0) {
            System.err.println("Usage: java -cp TesterApplet.jar [<host> <port> <tunnel URL>]");
            return;
        }

        final TesterApplet applet = (args.length == 0) ? new TesterApplet(new String[] {null, null, null}) : new TesterApplet(args);

        SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    applet.start();
                }
            });
    }
}
