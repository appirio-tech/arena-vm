package com.topcoder.server.mpsqas.listener;

import java.io.IOException;
import java.util.HashSet;

import javax.ejb.CreateException;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import com.topcoder.server.ejb.MPSQASServices.MPSQASServices;
import com.topcoder.server.ejb.MPSQASServices.MPSQASServicesLocator;
import com.topcoder.server.ejb.MPSQASServices.event.MPSQASServiceEventListener;
import com.topcoder.server.ejb.ProblemServices.ProblemServices;
import com.topcoder.server.ejb.ProblemServices.ProblemServicesLocator;
import com.topcoder.server.listener.NBIOListener;

/**
 * The listener for the MPSQAS applet manager.  Constructs and manages an <code>NBIOListener</code>.  All message
 * processing is handled by an <code>MPSQASMessageHandler</code>.  The default implementation for this and other
 * server-side features is provided by <code>ListenerObjectFactory</code>.
 *
 * @author Logan Hanks
 * @see com.topcoder.netCommon.mpsqas.communication.MPSQASMessageHandler
 * @see ListenerObjectFactory
 * @see NBIOListener
 */
public class MPSQASListener {

    /** The default MPSQAS listener port (used if no port is explicitly specified). */
    final static public int DEFAULT_PORT = 5016;

    private Logger logger;
    private NBIOListener listener;
    private MPSQASProcessor mpsqasProcessor;
    private MPSQASServices services;
    private PingHeartbeat pingHeartbeat;
    private ContestWatcher contestWatcher;
    private ProblemServices problemServices;
    private BroadcastProcessor broadcastProcessor;
    private BroadcastListener broadcastListener;
    private MPSQASServiceEventListener mpsqasServiceListener;
    

    /**
     * Construct and start a new MPSQAS listener on the given port.
     *
     * @param port
     * @throws IOException
     * @throws NamingException
     * @throws CreateException
     */
    public MPSQASListener(int port)
            throws IOException, NamingException, CreateException {
        logger = Logger.getLogger(getClass());

        logger.info("Getting MPSQASServices");
        services = MPSQASServicesLocator.getService();
        logger.info("Getting ProblemServices");
        problemServices = ProblemServicesLocator.getService();
        mpsqasProcessor = new MPSQASProcessor(services,
                problemServices);
        logger.info("Initiating broadcast listener and processor.");
        //create broadcast stuff
        broadcastProcessor = new BroadcastProcessor(mpsqasProcessor);
        broadcastListener = new BroadcastListener();
        broadcastListener.init(broadcastProcessor);

        //create ping stuff
        logger.info("Initiating ping heartbeat.");
        pingHeartbeat = new PingHeartbeat();
        pingHeartbeat.init(mpsqasProcessor);

        //create contest watcher stuff
        logger.info("Initiating contest watcher.");
        pingHeartbeat = new PingHeartbeat();
        contestWatcher = new ContestWatcher();
        contestWatcher.init(services);

        //create mpsqas service listener stuff
        mpsqasServiceListener = new MPSQASServiceEventListener();
        mpsqasServiceListener.addAvailableTestResultsProcessor(
                new MPSQASListenerMPSQASServiceProcessor(mpsqasProcessor));

        listener = new NBIOListener(port, mpsqasProcessor,
                1, 1, 1, new MPSQASMonitor(),
                new MPSQASMessageHandlerFactory(), new HashSet(), false);
        logger.info("Starting NBIOListener");
        listener.start();
    }

    /**
     * Construct and start a new MPSQAS listener on the default port.
     *
     * @throws IOException
     * @see #DEFAULT_PORT
     */
    public MPSQASListener()
            throws IOException, NamingException, CreateException {
        this(DEFAULT_PORT);
    }

    /**
     * This application entry point starts an MPSQAS listener.  If an argument is supplied, then the numeric value of
     * the first argument specifies the port to listen on.  Otherwise the default port is used.
     *
     * @param args
     * @throws IOException
     * @see #DEFAULT_PORT
     */
    static public void main(String[] args)
            throws IOException, NamingException, CreateException {
        if (args.length < 1)
            new MPSQASListener();
        else {
            try {
                int port = Integer.parseInt(args[0]);

                new MPSQASListener(port);
            } catch (NumberFormatException ex) {
                System.err.println("Usage: MPSQASListener [port]");
            }
        }
    }

    /**
     * Shuts down the MPSQAS listener.
     */
    public void stop() {
        logger.info("Stopping NBIOListener");
        listener.stop();
    }
}
