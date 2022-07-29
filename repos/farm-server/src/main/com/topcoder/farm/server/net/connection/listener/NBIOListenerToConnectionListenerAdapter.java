/*
 * NBIOListenerToConnectionListenerAdapter
 *
 * Created 06/27/2006
 */
package com.topcoder.farm.server.net.connection.listener;

import java.io.IOException;
import java.util.Collection;

import com.topcoder.farm.server.net.connection.ConnectionListener;
import com.topcoder.farm.server.net.connection.ConnectionListenerHandler;
import com.topcoder.farm.shared.net.connection.api.Connection;
import com.topcoder.shared.netCommon.CSHandlerFactory;
import com.topcoder.server.listener.ConnectionStatusMonitorProcessorDecorator;
import com.topcoder.server.listener.NBIOListener;
import com.topcoder.server.listener.monitor.MonitorInterface;

/**
 * Adapter class to allow using a NBIOListener where a ConnectionListener
 * is expected.
 *
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class NBIOListenerToConnectionListenerAdapter implements ConnectionListener {
    /**
     * ConnectionListenerHandler used to notify about new detected connections
     */
    private ConnectionListenerHandler connectionHandler;

    /**
     * The NBIOListener
     */
    private NBIOListener listener;

    /**
     * Creates a new NBIOListenerToConnectionListenerAdapter using the specified arguments
     * to create the underlying NBIOListener
     *
     * Argument documentation extracted from NBIOListener API Doc.
     * @param   port                the port number.
     * @param   numAcceptThreads    the number of accept threads.
     * @param   numReadThreads      the number of read threads.
     * @param   numWriteThreads     the number of write threads.
     * @param   monitor             the monitor.
     * @param   csHandlerFactory    the CS handler factory.
     * @param   ips                 set of IPs, allowed (isAllowedSet==true) or banned (isAllowedSet==false).
     * @param   isAllowedSet        indicates if ips is an allowed IPs set or a banned IPs set.
     * @param   scanInterval        Interval between connections scan when looking for lost connections
     * @param   keepAliveTimeout    KeepAlive timeout used by the client to send keep alive messages   
     */
    public NBIOListenerToConnectionListenerAdapter(int port, int numAcceptThreads, int numReadThreads,
            int numWriteThreads, MonitorInterface monitor, CSHandlerFactory csHandlerFactory, Collection ips,
            boolean isAllowedSet, long scanInterval, long keepAliveTimeout) {
        
        listener = new NBIOListener(port, 
                        new ConnectionStatusMonitorProcessorDecorator(new ProcessorForListenerAdapter(this), scanInterval, keepAliveTimeout), 
                        numAcceptThreads, 
                        numReadThreads, 
                        numWriteThreads, 
                        monitor, 
                        csHandlerFactory, 
                        ips, 
                        isAllowedSet);
    }

    /**
     * @see com.topcoder.farm.server.net.connection.ConnectionListener#start()
     */
    public void start() throws IOException {
        if (connectionHandler == null) {
            throw new IllegalStateException("A connection handler must be set before starting the listener");
        }
        listener.start();
    }

    /**
     * @see com.topcoder.farm.server.net.connection.ConnectionListener#stop()
     */
    public void stop() {
        listener.stop();
    }


    /**
     * @see com.topcoder.farm.server.net.connection.ConnectionListener#setHandler(ConnectionListenerHandler)
     */
    public void setHandler(ConnectionListenerHandler handler) {
        if (handler == null) {
            throw new IllegalArgumentException("Handler can't be null");
        }
        connectionHandler = handler;
    }

    /**
     * Notifies about the new connection to the handler
     *
     * @param connection the connection to notify as new
     *
     */
    void notifyNewConnection(Connection connection) {
        connectionHandler.newConnection(connection);
    }
}
