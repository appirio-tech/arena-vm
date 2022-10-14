/*
 * ConnectionController
 * 
 * Created 06/26/2006
 */
package com.topcoder.farm.controller.remoting.net;


import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.topcoder.farm.controller.ControllerLocator;
import com.topcoder.farm.server.net.connection.ConnectionListener;
import com.topcoder.farm.server.net.connection.ConnectionListenerHandler;
import com.topcoder.farm.shared.net.connection.api.Connection;
import com.topcoder.farm.shared.util.concurrent.runner.ExecutorServiceToRunnerAdapter;
import com.topcoder.farm.shared.util.concurrent.runner.Runner;

/**
 * The ConnectionController class is responsible for
 * starting connection monitoring. It allows to remote
 * nodes to establish connection to the controller configured on 
 * the ControllerLocator creating a ControllerSkeleton for every new connection 
 * detected. 
 * 
 * It listen for new connections and when a new connection is detected
 * it creates a ControllerSkeleton for the connection.
 * 
  * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ConnectionController  {
    private Log log = LogFactory.getLog(ConnectionController.class);
    private String controllerId;
    private int timeOutForAck;
    
    /**
     * The listener reponsible for reporting new connections
     */
    private ConnectionListener connectionListener;
    
    /**
     * The runner used to process invocations on the skeleton
     */
    private Runner requestRunner;

    public ConnectionController(String controllerId, ConnectionControllerConfiguration configuration) {
        this.controllerId = controllerId;
        this.timeOutForAck = configuration.getTimeoutForAck();
        this.requestRunner = buildRunner(configuration);
        this.connectionListener = getListener(configuration);
        this.connectionListener.setHandler(new ConnectionListenerHandler() {
            public void newConnection(Connection connection) {
                handleNewConnection(connection);
            }
        });
    }
    
    public void start() throws IOException {
        log.info("Starting connection - controller binding for " + controllerId);
        connectionListener.start();
    }
    
    public void stop() {
        log.info("Stopping connection - controller binding for " + controllerId);
        requestRunner.stopAccepting();
        connectionListener.stop();
    }
    
    private ExecutorServiceToRunnerAdapter buildRunner(ConnectionControllerConfiguration configuration) {
        return new ExecutorServiceToRunnerAdapter(
                new ThreadPoolExecutor(configuration.getRunnerMinThreads(), 
                        configuration.getRunnerMaxThreads(), 
                        5000L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>()));
    }
    
   
    private ConnectionListener getListener(ConnectionControllerConfiguration configuration) {
        return new ListenerConnectionFactory().create(configuration.getConnectionListenerConfiguration());
    }
   
    void handleNewConnection(Connection connection) {
        new ControllerSkeleton(ControllerLocator.getController(), connection, requestRunner, timeOutForAck);
    }
}
