/*
 * RemoteProxyControllerProvider
 * 
 * Created 07/20/2006
 */
package com.topcoder.farm.controller.remoting.net;

import java.lang.reflect.Constructor;

import com.topcoder.farm.satellite.SatelliteConfiguration;
import com.topcoder.farm.shared.net.connection.api.ConnectionFactory;
import com.topcoder.farm.shared.net.connection.impl.clientsocket.ClientSocketConnectionFactory;
import com.topcoder.farm.shared.net.connection.impl.reconnectable.AutoReconnectConnectionFactory;
import com.topcoder.farm.shared.serialization.FarmCSHandlerFactory;
import com.topcoder.farm.shared.util.concurrent.runner.Runner;
import com.topcoder.farm.shared.util.version.LazyProvider;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class RemoteProxyControllerProvider implements LazyProvider {
    private ConnectionFactory factory;
    private String proxyClassName;
    private Runner requestRunner;
    private int registrationTimeout;
    private int ackTimeout;
    
    public RemoteProxyControllerProvider(SatelliteConfiguration configuration, 
                                         Runner runner, 
                                         String proxyClassName,
                                         int maxReconnectAttemps,
                                         int registrationTimeout,
                                         int ackTimeout) {
        this.proxyClassName = proxyClassName;
        this.requestRunner = runner;
        this.ackTimeout = ackTimeout;
        this.registrationTimeout = registrationTimeout;
        factory = new AutoReconnectConnectionFactory(
                new ClientSocketConnectionFactory(
                                    configuration.getAddresses()[0], 
                                    new FarmCSHandlerFactory(),
                                    configuration.getInactivityTimeout(),
                                    configuration.getKeepAliveTimeout()),
                maxReconnectAttemps, 1000);
    }

    public Object getObject() {
        try {
            Constructor constructor = Class.forName(this.proxyClassName).
                                getConstructor(new Class[] {ConnectionFactory.class, Runner.class, int.class, int.class});
            
            return constructor.newInstance(new Object[] {
                        factory, 
                        requestRunner, 
                        new Integer(registrationTimeout), new 
                        Integer(ackTimeout)});
        } catch (Exception e) {
            throw (IllegalStateException) new IllegalStateException("Cannot create proxy instance").initCause(e);
        }
    }
}
