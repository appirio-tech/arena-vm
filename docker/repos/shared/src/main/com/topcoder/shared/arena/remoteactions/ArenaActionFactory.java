/*
 * RoundEventFactory
 * 
 * Created 10/02/2007
 */
package com.topcoder.shared.arena.remoteactions;

/**
 * Factory object for Arena Actions Listeners and Requesters.<p>
 * 
 * The factory must be configured using <code>configureFactory</code> method 
 * before invoking <code>getFactory</code> method.
 * 
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public abstract class ArenaActionFactory {
    /**
     * The default factory to use.
     */
    private static ArenaActionFactory factory;
    
    /**
     * Configures the {@link ArenaActionFactory} with the concrete instance
     * to use for creating listeners and notifiers
     * 
     * @param factory Concrete factory to use
     */
    public static void configureFactory(ArenaActionFactory factory) {
        ArenaActionFactory.factory = factory;
    }
    
    /**
     * @return Returns the default factory   
     * 
     * @throws IllegalStateException if no factory was configured
     */
    public static ArenaActionFactory getFactory() {
        if (factory == null) {
            throw new IllegalStateException("ArenaActionFactory is not set");
        }
        return factory;
    }
    
    /**
     * Creates a ArenaActionRequester.
     * 
     * @param moduleName The module name requesting the Publisher. Since notifications may come
     *        from many sources, this information will be provided to the round event listener.
     *        In addition custom implementations may use the module name to configure the underlying service.
     * 
     * @return The publisher
     * 
     * @throws ArenaActionRequesterException If the publisher could not be created.
     */
    public abstract ArenaActionRequester createRequester(String moduleName) throws ArenaActionRequesterException;
    
    /**
     * Creates a ArenaActionRequestListener
     * 
     * @param moduleName The module name requesting the Listener. 
     *                   Custom implementations may use the module name to configure the underlying service.
     *        
     * 
     * @return The listener
     * 
     * @throws ArenaActionListenerException If the publisher could not be created.
     */
    public abstract ArenaActionRequestListener createListener(String moduleName) throws ArenaActionListenerException;
}
