package com.topcoder.client.mpsqasApplet;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ResourceBundle;

import javax.swing.JApplet;
import javax.swing.JButton;

import com.topcoder.client.mpsqasApplet.object.ComponentObjectFactory;
import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;

/**
 * LaunchMPSQAS is the applet which will be displayed on the web page from
 * which the user can launch the MPSQAS applet window.  Also contains a main
 * method to start it up from the command line.
 *
 * @author mitalub
 */
public class LaunchMPSQAS extends JApplet implements ActionListener {

    private final static String RESOURCE_BUNDLE_NAME = "MPSQASApplet";
    public final static String DEFAULT_HOST_ADDRESS = "65.112.118.215";
    public final static String DEFAULT_TUNNEL_ADDRESS = "http://tunnel1.topcoder.com:8080/tunnel?dummy";
    public final static int DEFAULT_PORT_NUMBER = 5037;
    public final static Font NORMAL_FONT = new Font("SansSerif", Font.PLAIN, 12);

    private static boolean testing = false;
    private JButton launchButton;  //the button to click

    /**
     * Launches Applet in new JFrame
     *
     * @param args[0] Host address (optional)
     * @param args[1] Port number (optional)
     */
    public static void main(String[] args) {
        for (int i = 0; i < args.length; i++) {
            System.out.println(args[i]);
            if (args[i].equalsIgnoreCase("OUTTOFILE")) {
                try {
                    System.setErr(new PrintStream(new FileOutputStream("err.txt")));
                    System.setOut(new PrintStream(new FileOutputStream("out.txt")));
                } catch (Exception e) {
                }
                break;
            }
        }
        String hostAddress = DEFAULT_HOST_ADDRESS;
        int portNumber = DEFAULT_PORT_NUMBER;
        String tunnel = DEFAULT_TUNNEL_ADDRESS;
        try {
            portNumber = Integer.parseInt(args[1]);
            hostAddress = args[0];
        } catch (Exception e) {
            //leave defaults
        }

        if (args.length >= 3) {
            tunnel = args[2];
        }

        testing = (args.length == 1 &&
                (args[0].equals("t") || args[0].equals("true")))
                || (args.length == 4 &&
                (args[3].equals("t") || args[3].equals("true")));
        try {
            ResourceBundle rb = ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME);
            MainObjectFactory.init(rb);
            ComponentObjectFactory.init(rb);
            MainObjectFactory.getMainApplet()
                    .init(hostAddress, portNumber, tunnel, new LaunchMPSQAS());
        } catch (Exception e) {
            System.out.println("Error launching Applet: ");
            e.printStackTrace();
        }
    }

    /**
     * init initializes the applet by creating the lauch button and adding
     * it to the main container.
     */
    public void init() {
        launchButton = new JButton("Launch MPSQAS");
        launchButton.setFont(NORMAL_FONT);
        launchButton.addActionListener(this);
        getContentPane().add(launchButton, BorderLayout.CENTER);
    }

    /**
     * Called when the Launch button is pressed. This method creates the
     * MainApplet and, if there is an error connecting to the server an error
     * message is displayed.
     *
     * @param e   The event causing this method to be invoked
     */
    public void actionPerformed(ActionEvent e) {
        try {
            ResourceBundle rb = ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME);
            MainObjectFactory.init(rb);
            ComponentObjectFactory.init(rb);
            MainObjectFactory.getMainApplet().init(getParameter("HOST"),
                    Integer.parseInt(getParameter("PORT")), getParameter("TUNNEL"), this);
            launchButton.setEnabled(false);
        } catch (Exception exc) {
            System.out.println("Error launching MPSQAS applet.");
            exc.printStackTrace();
        }
    }

    /**
     * Reactivates launch button.
     */
    public void reActivate() {
        if (launchButton != null) {
            launchButton.setEnabled(true);
        }
    }

    public boolean isTesting() {
        return testing;
    }
}
