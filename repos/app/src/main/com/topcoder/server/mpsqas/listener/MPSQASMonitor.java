package com.topcoder.server.mpsqas.listener;

import org.apache.log4j.Logger;

import com.topcoder.server.listener.monitor.MonitorInterface;

/**
 * Monitors connections to the MPSQAS listener.
 *
 * @author Logan Hanks
 */
public class MPSQASMonitor
        implements MonitorInterface {

    Logger logger;

    public MPSQASMonitor() {
        logger = Logger.getLogger(getClass());
    }

    public void newConnection(int id, String remoteIP) {
        logger.info("Monitor: new connection");
    }

    public void lostConnection(int id) {
        logger.info("Monitor: lost connection");
    }

    public void bytesRead(int id, int numBytes) {
        logger.info("Monitor: bytes read: " + numBytes);
    }
    
    public void associateConnections(int existentConnectionId, int newConnectionID) {

    }
}
