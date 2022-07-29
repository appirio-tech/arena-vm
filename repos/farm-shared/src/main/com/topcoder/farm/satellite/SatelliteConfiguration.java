/*
 * SatelliteConfiguration
 * 
 * Created 07/04/2006
 */
package com.topcoder.farm.satellite;

import java.net.InetSocketAddress;

/**
 * Base class for all satellite configurations. This
 * class contains common configuration paramentes used by all satellite nodes.
 * 
 *  
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class SatelliteConfiguration {
    /**
     * Contains all known controller addresses of the farm 
     */
    private InetSocketAddress[] addresses;
    
    /**
     * Max time to wait in ms for client registrarion and re-registration. If a send is made
     * while a reconnection attempt is taking place, this will the max time to wait for the
     * re-registraion completes before throwning an exception.
     */
    private int registrationTimeout = 15000;
    
    /**
     * Max time to wait for an ack response after sending an ack request. Currently this values is used
     * for sync responses too (2 times this value).
     */
    private int ackTimeout = 3000;
    
    
    /**
     * Max time to wait in ms for output channel activity before sending a keep alive message
     */
    private int keepAliveTimeout = 15000;

    /**
     * Max time to wait in ms for input channel activity before reporting connection as Lost
     */
    private int inactivityTimeout = 35000;
    

    public SatelliteConfiguration() {
    }

    /**
     * @return Returns the addresses.
     */
    public InetSocketAddress[] getAddresses() {
        return addresses;
    }

    /**
     * @param addresses The addresses to set.
     */
    public void setAddresses(InetSocketAddress[] addresses) {
        this.addresses = addresses;
    }

    public int getRegistrationTimeout() {
        return registrationTimeout;
    }

    public void setRegistrationTimeout(int registrationTimeout) {
        this.registrationTimeout = registrationTimeout;
    }
    
    public int getAckTimeout() {
        return ackTimeout;
    }

    public void setAckTimeout(int ackTimeout) {
        this.ackTimeout = ackTimeout;
    }

    public int getInactivityTimeout() {
        return inactivityTimeout;
    }

    public void setInactivityTimeout(int inactivityTimeOut) {
        this.inactivityTimeout = inactivityTimeOut;
    }

    public int getKeepAliveTimeout() {
        return keepAliveTimeout;
    }

    public void setKeepAliveTimeout(int keepAliveTimeOut) {
        this.keepAliveTimeout = keepAliveTimeOut;
    }
}
