/*
 * ListenerConnectionFactory
 * 
 * Created 07/26/2006
 */
package com.topcoder.farm.controller.remoting.net;

import java.util.Arrays;

import com.topcoder.farm.server.net.connection.ConnectionListener;
import com.topcoder.farm.server.net.connection.listener.NBIOListenerToConnectionListenerAdapter;
import com.topcoder.shared.netCommon.CSHandlerFactory;
import com.topcoder.server.listener.monitor.MonitorInterface;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ListenerConnectionFactory {
    
    /**
     * Creates a new Connection Listener using the configuration specified 
     * 
     * @param configuration The configuration containing all information required for
     *                      the creation of the ConnectionListener 
     * 
     * @return the created ConnectionListener
     */
    public ConnectionListener create(ConnectionListenerConfiguration configuration) {
        if (configuration.getListenerType() != ListenerType.NBIOListenerType) {
            throw new IllegalArgumentException("The connection listener type is not supported");
        }
        return createNBIOConnectionListener(configuration);
    }

    private ConnectionListener createNBIOConnectionListener(ConnectionListenerConfiguration configuration) {
        NBIOListenerConfiguration nbioConfig = (NBIOListenerConfiguration) configuration;
        return new NBIOListenerToConnectionListenerAdapter(
                nbioConfig.getPort(), 
                nbioConfig.getNumReadThreads(), 
                nbioConfig.getNumAcceptThreads(), 
                nbioConfig.getNumWriteThreads(), 
                (MonitorInterface) newInstance(nbioConfig.getMonitorClassName()), 
                (CSHandlerFactory) newInstance(nbioConfig.getCsHandlerFactoryClassName()),
                Arrays.asList(nbioConfig.getIpsStringList().split(",")), 
                nbioConfig.isAllowedSet(),
                nbioConfig.getScanInterval(),
                nbioConfig.getKeepAliveTimeout());
    }

    private Object newInstance(String className) {
        try {
            return Class.forName(className).newInstance();
        } catch (Exception e) {
            throw (IllegalArgumentException) new IllegalArgumentException("Exception trying to instantiate class " + className).initCause(e);
        }
    }
}
