package com.topcoder.server.AdminListener;

import java.net.InetAddress;
import java.util.StringTokenizer;

import org.apache.log4j.*;

/**
 * This class contains the entry point for the admin listener server. <p>
 *
 * argument 1 - start | stop | kill, depending on what you want to do.
 * argument 2 - the port on which to start the admin listener.
 * argument 3 - (optional) the ip and port at which the contest server is
 *  listening for admin connections, in the format ip:port.  If unspecified,
 *  no contest server connection will be made.  This probably is not what you want.
 * argument 4 - (optional) the port used by the logging server.
 *  If unspecified, no logging server will be instantiated.
 *
 * @author  Dave Pecora
 * @version 1.00, 06/01/2002
 */

public class AdminListenerMain {

    private static int port = AdminConstants.ADMIN_LISTENER_DEFAULT_PORT;
    private static InetAddress contestListenerAddress = null;
    private static int contestListenerPort = 0;
    private static int loggingServerPort = 0;
    private static final Category log = Category.getInstance(AdminListenerMain.class);
    private static AdminListener listener = null;

    private static final Thread SHUTDOWN_HOOK = new Thread(new Runnable() {
        public void run() {
            stop();
        }
    });

    private AdminListenerMain() {
    }

    private static void start() throws Exception {
        listener = new AdminListener(port, contestListenerAddress, contestListenerPort, loggingServerPort);
        listener.start();
    }

    private static void stop() {
        if (listener != null) {
            listener.stop();
        }
    }

    /**
     * The entry point for the admin listener server.
     *
     * @param args Arguments as noted above.
     */
    public static void main(String args[]) {
        try {
            if (args.length > 0) {
                port = Integer.parseInt(args[0]);
            }
            if (args.length > 1) {
                StringTokenizer st = new StringTokenizer(args[1], ":");
                contestListenerAddress = InetAddress.getByName(st.nextToken());
                contestListenerPort = Integer.parseInt(st.nextToken());
            }
            if (args.length > 2) {
                loggingServerPort = Integer.parseInt(args[2]);
            }
            log.debug("loggingServerPort: " + loggingServerPort);
        } catch (Exception e) {
            log.error("Error parsing arguments.\n" +
                    "Usage: AdminListenerMain [port] [contest listener IP:port] [logging port]\n" +
                    "Example: AdminListenerMain 6000 172.16.20.30:5001 6001");
            System.exit(0);
        }

        try {
            Runtime.getRuntime().addShutdownHook(SHUTDOWN_HOOK);
            start();
        } catch (Exception e) {
            log.fatal("Startup of admin listener server failed", e);
        }
    }
}

