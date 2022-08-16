/*
 * RoundEventFactory
 * 
 * Created 10/02/2007
 */
package com.topcoder.shared.round.events;



/**
 * Factory object for Round Event Listeners and Notifiers.<p>
 * 
 * The factory must be configured using <code>configureFactory</code> method 
 * before invoking <code>getFactory</code> method.
 * 
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public abstract class RoundEventFactory {
    /**
     * The default factory to use.
     */
    private static RoundEventFactory factory;
    
    /**
     * Configures the {@link RoundEventFactory} with the concrete instance
     * to use for creating listeners and notifiers
     * 
     * @param factory Concrete factory to use
     */
    public static void configureFactory(RoundEventFactory factory) {
        RoundEventFactory.factory = factory;
    }
    
    /**
     * @return Returns the default factory   
     * 
     * @throws IllegalStateException if no factory was configured
     */
    public static RoundEventFactory getFactory() {
        if (factory == null) {
            throw new IllegalStateException("RoundEventFactory is not set");
        }
        return factory;
    }
    
    /**
     * Creates a RoundEventPublisher.
     * 
     * @param moduleName The module name requesting the Publisher. Since notifications may come
     *        from many sources, this information will be provided to the round event listener.
     *        In addition custom implementations may use the module name to configure the underlying service.
     * 
     * @return The publisher
     * 
     * @throws RoundEventException If the publisher could not be created.
     */
    public abstract RoundEventPublisher createPublisher(String moduleName) throws RoundEventException;
    
    /**
     * Creates a RoundEventListener
     * 
     * @param moduleName The module name requesting the Listener. 
     *                   Custom implementations may use the module name to configure the underlying service.
     *        
     * 
     * @return The listener
     * 
     * @throws RoundEventException If the publisher could not be created.
     */
    public abstract RoundEventListener createListener(String moduleName) throws RoundEventException;
}
