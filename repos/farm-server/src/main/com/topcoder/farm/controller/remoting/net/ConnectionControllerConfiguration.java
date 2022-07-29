/*
 * ConnectionControllerConfiguration
 * 
 * Created 07/26/2006
 */
package com.topcoder.farm.controller.remoting.net;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ConnectionControllerConfiguration {
    private ConnectionListenerConfiguration connectionListenerConfiguration;
    private int runnerMinThreads;
    private int runnerMaxThreads;
    private int timeoutForAck;
   
   
    
    public ConnectionListenerConfiguration getConnectionListenerConfiguration() {
        return connectionListenerConfiguration;
    }
    
    public void setConnectionListenerConfiguration(ConnectionListenerConfiguration config) {
        this.connectionListenerConfiguration = config;
    }
    
    public int getRunnerMaxThreads() {
        return runnerMaxThreads;
    }
    
    public void setRunnerMaxThreads(int runnerMaxThreads) {
        this.runnerMaxThreads = runnerMaxThreads;
    }
    
    public int getRunnerMinThreads() {
        return runnerMinThreads;
    }
    
    public void setRunnerMinThreads(int runnerMinThreads) {
        this.runnerMinThreads = runnerMinThreads;
    }

    public int getTimeoutForAck() {
        return timeoutForAck;
    }

    public void setTimeoutForAck(int timeoutForAck) {
        this.timeoutForAck = timeoutForAck;
    }


}
