/*
 * RoundEventListener
 * 
 * Created Sep 28, 2007
 */
package com.topcoder.shared.round.events;

import java.util.concurrent.Executor;

/**
 * RoundEventListener interface.
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public interface RoundEventListener {
    /**
     * Starts listening for events.<p>
     * 
     * A handler should be set prior to calling this method.
     * 
     * @throws RoundEventException If the listener could not be started.
     */
    void start() throws RoundEventException;
    
    /**
     * Stops listening for events.
     */
    void stop();
    
    /**
     * Returns the module name for which this Listener was created
     * @return
     */
    public String getModuleName();
    
    /**
     * Sets the runner used for processing incoming events.<p>
     * A default runner will be provider by implementors, but the type
     * of runner is determined by the implementation.
     *  
     * @param runner The runner to set.
     * 
     * @return The old runner set.
     */
    public Executor setRunner(Executor runner);
    
    /**
     * Sets the handler for handling incoming events
     * 
     * @param h The handler to set
     * 
     * @throws RoundEventException If the handler could not be set
     */
    public void setHandler(Handler h) throws RoundEventException;
    
    /**
     * Interface defining methods called by the listener in order to notify
     * events
     */
    public interface Handler {
        /**
         * Called when a RoundCreatedEvent is received
         * 
         * @param event The event
         * @return true if the event was properly handler, false otherwise
         */
        boolean handle(RoundCreatedEvent event);
        /**
         * Called when a RoundDeletedEvent is received
         * 
         * @param event The event
         * @return true if the event was properly handler, false otherwise
         */
        boolean handle(RoundDeletedEvent event);
        /**
         * Called when a RoundModifiedEvent is received
         * 
         * @param event The event
         * @return true if the event was properly handler, false otherwise
         */
        boolean handle(RoundModifiedEvent event);
        /**
         * Called when a class extending RoundEvent is received, but no matching method
         * exists, or if the most specific method could not handle the event (return false)
         * 
         * @param event The event
         * @return true if the event was properly handler, false otherwise
         */
        boolean handle(RoundEvent event);
    }
}
