/*
 * InvokerConfiguration
 * 
 * Created 10/20/2006
 */
package com.topcoder.farm.client.invoker;

/**
 * Invoker configuration parameters
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class InvokerConfiguration {
    /**
     * Indicates if when registering with controller, pending requests must
     * be cancelled
     */
    private boolean cancelOnRegistration;
    /**
     * Indicates if when registering with controller, if responses available on the farm
     * must be delivered
     */
    private boolean deliverOnRegistration;

    public boolean isCancelOnRegistration() {
        return cancelOnRegistration;
    }

    public void setCancelOnRegistration(boolean cancelOnRegistration) {
        this.cancelOnRegistration = cancelOnRegistration;
    }

    public boolean isDeliverOnRegistration() {
        return deliverOnRegistration;
    }

    public void setDeliverOnRegistration(boolean deliverOnRegistration) {
        this.deliverOnRegistration = deliverOnRegistration;
    }
}
